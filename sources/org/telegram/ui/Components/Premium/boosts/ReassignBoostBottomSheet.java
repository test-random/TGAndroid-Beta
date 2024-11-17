package org.telegram.ui.Components.Premium.boosts;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.os.CountDownTimer;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewPropertyAnimator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.exoplayer2.util.Consumer;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.DialogObject;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.R;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.Utilities;
import org.telegram.tgnet.TLRPC;
import org.telegram.tgnet.tl.TL_stories;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.BottomSheet;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Cells.HeaderCell;
import org.telegram.ui.Cells.ShadowSectionCell;
import org.telegram.ui.Components.AvatarDrawable;
import org.telegram.ui.Components.BackupImageView;
import org.telegram.ui.Components.BottomSheetWithRecyclerListView;
import org.telegram.ui.Components.Bulletin;
import org.telegram.ui.Components.BulletinFactory;
import org.telegram.ui.Components.CubicBezierInterpolator;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.LinkSpanDrawable;
import org.telegram.ui.Components.Premium.PremiumGradient;
import org.telegram.ui.Components.Premium.boosts.ReassignBoostBottomSheet;
import org.telegram.ui.Components.Premium.boosts.cells.selector.SelectorBtnCell;
import org.telegram.ui.Components.Premium.boosts.cells.selector.SelectorUserCell;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.Stories.recorder.ButtonWithCounterView;

public class ReassignBoostBottomSheet extends BottomSheetWithRecyclerListView {
    private final ButtonWithCounterView actionButton;
    private final List allUsedBoosts;
    private final SelectorBtnCell buttonContainer;
    private final TLRPC.Chat currentChat;
    private final TL_stories.TL_premium_myBoosts myBoosts;
    private final List selectedBoosts;
    private CountDownTimer timer;
    private TopCell topCell;

    private static class ArrowView extends FrameLayout {
        public ArrowView(Context context) {
            super(context);
            ImageView imageView = new ImageView(getContext());
            imageView.setImageResource(R.drawable.msg_arrow_avatar);
            imageView.setColorFilter(Theme.getColor(Theme.key_windowBackgroundWhiteGrayText7));
            addView(imageView);
        }
    }

    public static class AvatarHolderView extends FrameLayout {
        private final Paint bgPaint;
        private final BoostIconView boostIconView;
        public TLRPC.Chat chat;
        AvatarDrawable fromAvatarDrawable;
        private final BackupImageView imageView;

        public AvatarHolderView(Context context) {
            super(context);
            Paint paint = new Paint(1);
            this.bgPaint = paint;
            this.fromAvatarDrawable = new AvatarDrawable();
            BackupImageView backupImageView = new BackupImageView(getContext());
            this.imageView = backupImageView;
            backupImageView.setRoundRadius(AndroidUtilities.dp(30.0f));
            BoostIconView boostIconView = new BoostIconView(context);
            this.boostIconView = boostIconView;
            boostIconView.setAlpha(0.0f);
            addView(backupImageView, LayoutHelper.createFrame(-1, -1.0f, 0, 5.0f, 5.0f, 5.0f, 5.0f));
            addView(boostIconView, LayoutHelper.createFrame(28, 28.0f, 85, 0.0f, 0.0f, 0.0f, 3.0f));
            paint.setColor(Theme.getColor(Theme.key_dialogBackground));
        }

        @Override
        protected void dispatchDraw(Canvas canvas) {
            canvas.drawCircle(getMeasuredWidth() / 2.0f, getMeasuredHeight() / 2.0f, (getMeasuredHeight() / 2.0f) - AndroidUtilities.dp(2.0f), this.bgPaint);
            super.dispatchDraw(canvas);
        }

        public void setChat(TLRPC.Chat chat) {
            this.chat = chat;
            this.fromAvatarDrawable.setInfo(chat);
            this.imageView.setForUserOrChat(chat, this.fromAvatarDrawable);
        }
    }

    public static class BoostIconView extends View {
        Drawable boostDrawable;
        Paint paint;

        public BoostIconView(Context context) {
            super(context);
            this.paint = new Paint(1);
            this.boostDrawable = ContextCompat.getDrawable(getContext(), R.drawable.mini_boost_remove);
            this.paint.setColor(Theme.getColor(Theme.key_dialogBackground));
        }

        @Override
        protected void onDraw(Canvas canvas) {
            float measuredWidth = getMeasuredWidth() / 2.0f;
            float measuredHeight = getMeasuredHeight() / 2.0f;
            canvas.drawCircle(measuredWidth, measuredHeight, getMeasuredWidth() / 2.0f, this.paint);
            PremiumGradient.getInstance().updateMainGradientMatrix(0, 0, getMeasuredWidth(), getMeasuredHeight(), -AndroidUtilities.dp(10.0f), 0.0f);
            canvas.drawCircle(measuredWidth, measuredHeight, (getMeasuredWidth() / 2.0f) - AndroidUtilities.dp(2.0f), PremiumGradient.getInstance().getMainGradientPaint());
            float dp = AndroidUtilities.dp(18.0f) / 2.0f;
            this.boostDrawable.setBounds((int) (measuredWidth - dp), (int) (measuredHeight - dp), (int) (measuredWidth + dp), (int) (measuredHeight + dp));
            this.boostDrawable.draw(canvas);
        }
    }

    public static class TopCell extends LinearLayout {
        private final List addedChats;
        private final ArrowView arrowView;
        private final FrameLayout avatarsContainer;
        private final FrameLayout avatarsWrapper;
        private final TextView description;
        private final AvatarHolderView toAvatar;

        public TopCell(Context context) {
            super(context);
            this.addedChats = new ArrayList();
            setOrientation(1);
            setClipChildren(false);
            FrameLayout frameLayout = new FrameLayout(getContext());
            this.avatarsContainer = frameLayout;
            frameLayout.setClipChildren(false);
            FrameLayout frameLayout2 = new FrameLayout(getContext());
            this.avatarsWrapper = frameLayout2;
            frameLayout2.setClipChildren(false);
            frameLayout.addView(frameLayout2, LayoutHelper.createFrame(-1, 70.0f, 0, 0.0f, 0.0f, 0.0f, 0.0f));
            ArrowView arrowView = new ArrowView(context);
            this.arrowView = arrowView;
            frameLayout.addView(arrowView, LayoutHelper.createFrame(24, 24, 17));
            AvatarHolderView avatarHolderView = new AvatarHolderView(context);
            this.toAvatar = avatarHolderView;
            avatarHolderView.setLayerType(2, null);
            frameLayout.addView(avatarHolderView, LayoutHelper.createFrame(70, 70, 17));
            addView(frameLayout, LayoutHelper.createLinear(-1, 70, 0.0f, 15.0f, 0.0f, 0.0f));
            TextView textView = new TextView(context);
            textView.setTypeface(AndroidUtilities.bold());
            textView.setText(LocaleController.getString(R.string.BoostingReassignBoost));
            textView.setTextSize(1, 20.0f);
            textView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
            addView(textView, LayoutHelper.createLinear(-2, -2, 1, 0, 15, 0, 7));
            LinkSpanDrawable.LinksTextView linksTextView = new LinkSpanDrawable.LinksTextView(getContext());
            this.description = linksTextView;
            linksTextView.setTextSize(1, 14.0f);
            linksTextView.setGravity(1);
            linksTextView.setTextColor(Theme.getColor(Theme.key_dialogTextBlack));
            linksTextView.setLineSpacing(linksTextView.getLineSpacingExtra(), linksTextView.getLineSpacingMultiplier() * 1.1f);
            addView(linksTextView, LayoutHelper.createLinear(-2, -2, 1, 28, 0, 28, 18));
        }

        public static void lambda$setData$0(BottomSheet bottomSheet) {
            bottomSheet.dismiss();
            NotificationCenter.getInstance(UserConfig.selectedAccount).lambda$postNotificationNameOnUIThread$1(NotificationCenter.didStartedMultiGiftsSelector, new Object[0]);
            AndroidUtilities.runOnUIThread(new Runnable() {
                @Override
                public final void run() {
                    UserSelectorBottomSheet.open();
                }
            }, 220L);
        }

        public void lambda$setData$1(int i) {
            try {
                if (this.description.getLayout().getLineForOffset(i) == 0) {
                    this.description.getEditableText().insert(i, "\n");
                }
            } catch (Exception e) {
                FileLog.e(e);
            }
        }

        public void setData(TLRPC.Chat chat, final BottomSheet bottomSheet) {
            try {
                SpannableStringBuilder replaceTags = AndroidUtilities.replaceTags(LocaleController.formatPluralString("BoostingReassignBoostTextPluralWithLink", BoostRepository.boostsPerSentGift(), chat == null ? "" : chat.title, "%3$s"));
                SpannableStringBuilder replaceSingleTag = AndroidUtilities.replaceSingleTag(LocaleController.getString("BoostingReassignBoostTextLink", R.string.BoostingReassignBoostTextLink), Theme.key_chat_messageLinkIn, 2, new Runnable() {
                    @Override
                    public final void run() {
                        ReassignBoostBottomSheet.TopCell.lambda$setData$0(BottomSheet.this);
                    }
                });
                final int indexOf = TextUtils.indexOf(replaceTags, "%3$s");
                replaceTags.replace(indexOf, indexOf + 4, (CharSequence) replaceSingleTag);
                this.description.setText(replaceTags, TextView.BufferType.EDITABLE);
                this.description.post(new Runnable() {
                    @Override
                    public final void run() {
                        ReassignBoostBottomSheet.TopCell.this.lambda$setData$1(indexOf);
                    }
                });
            } catch (Exception e) {
                FileLog.e(e);
            }
        }

        public void showBoosts(List list, TLRPC.Chat chat) {
            ArrayList arrayList = new ArrayList(list.size());
            Iterator it = list.iterator();
            while (it.hasNext()) {
                arrayList.add(MessagesController.getInstance(UserConfig.selectedAccount).getChat(Long.valueOf(-DialogObject.getPeerDialogId(((TL_stories.TL_myBoost) it.next()).peer))));
            }
            showChats(arrayList, chat);
        }

        public void showChats(List list, TLRPC.Chat chat) {
            float f;
            float f2;
            int i;
            ViewPropertyAnimator translationX;
            long j;
            ViewPropertyAnimator interpolator;
            float dp;
            final AvatarHolderView avatarHolderView;
            ArrayList<TLRPC.Chat> arrayList = new ArrayList();
            ArrayList arrayList2 = new ArrayList();
            CubicBezierInterpolator cubicBezierInterpolator = CubicBezierInterpolator.DEFAULT;
            Iterator it = list.iterator();
            while (it.hasNext()) {
                TLRPC.Chat chat2 = (TLRPC.Chat) it.next();
                if (!this.addedChats.contains(chat2)) {
                    arrayList2.add(chat2);
                }
            }
            for (TLRPC.Chat chat3 : this.addedChats) {
                if (!list.contains(chat3)) {
                    arrayList.add(chat3);
                }
            }
            ArrayList<AvatarHolderView> arrayList3 = new ArrayList();
            for (int i2 = 0; i2 < this.avatarsWrapper.getChildCount(); i2++) {
                AvatarHolderView avatarHolderView2 = (AvatarHolderView) this.avatarsWrapper.getChildAt(i2);
                if (avatarHolderView2.getTag() == null) {
                    arrayList3.add(avatarHolderView2);
                }
            }
            Iterator it2 = arrayList2.iterator();
            while (true) {
                f = 0.0f;
                f2 = 0.1f;
                if (!it2.hasNext()) {
                    break;
                }
                TLRPC.Chat chat4 = (TLRPC.Chat) it2.next();
                AvatarHolderView avatarHolderView3 = new AvatarHolderView(getContext());
                avatarHolderView3.setLayerType(2, null);
                avatarHolderView3.setChat(chat4);
                int size = arrayList3.size();
                this.avatarsWrapper.addView(avatarHolderView3, 0, LayoutHelper.createFrame(70, 70, 17));
                avatarHolderView3.setTranslationX((-size) * AndroidUtilities.dp(23.0f));
                avatarHolderView3.setAlpha(0.0f);
                avatarHolderView3.setScaleX(0.1f);
                avatarHolderView3.setScaleY(0.1f);
                avatarHolderView3.animate().alpha(1.0f).scaleX(1.0f).scaleY(1.0f).setInterpolator(cubicBezierInterpolator).setDuration(200).start();
                if (size == 0) {
                    avatarHolderView3.boostIconView.setScaleY(1.0f);
                    avatarHolderView3.boostIconView.setScaleX(1.0f);
                    avatarHolderView3.boostIconView.setAlpha(1.0f);
                }
            }
            for (TLRPC.Chat chat5 : arrayList) {
                Iterator it3 = arrayList3.iterator();
                while (true) {
                    if (it3.hasNext()) {
                        avatarHolderView = (AvatarHolderView) it3.next();
                        if (avatarHolderView.chat == chat5) {
                            break;
                        }
                    } else {
                        avatarHolderView = null;
                        break;
                    }
                }
                if (avatarHolderView != null) {
                    avatarHolderView.setTag("REMOVED");
                    ViewPropertyAnimator interpolator2 = avatarHolderView.animate().alpha(f).translationXBy(AndroidUtilities.dp(23.0f)).scaleX(f2).scaleY(f2).setInterpolator(cubicBezierInterpolator);
                    long j2 = 200;
                    interpolator2.setDuration(j2).setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animator) {
                            avatarHolderView.setLayerType(0, null);
                            TopCell.this.avatarsWrapper.removeView(avatarHolderView);
                        }
                    }).start();
                    int i3 = 0;
                    for (AvatarHolderView avatarHolderView4 : arrayList3) {
                        int size2 = arrayList3.size() - 1;
                        if (avatarHolderView4 != avatarHolderView) {
                            i3++;
                            avatarHolderView4.animate().translationX((-(size2 - i3)) * AndroidUtilities.dp(23.0f)).setInterpolator(cubicBezierInterpolator).setDuration(j2).start();
                        }
                    }
                    if (arrayList3.get(arrayList3.size() - 1) == avatarHolderView && arrayList3.size() > 1) {
                        ((AvatarHolderView) arrayList3.get(arrayList3.size() - 2)).boostIconView.setScaleY(0.1f);
                        ((AvatarHolderView) arrayList3.get(arrayList3.size() - 2)).boostIconView.setScaleX(0.1f);
                        ((AvatarHolderView) arrayList3.get(arrayList3.size() - 2)).boostIconView.animate().alpha(1.0f).scaleY(1.0f).scaleX(1.0f).setDuration(j2).setInterpolator(cubicBezierInterpolator).start();
                    }
                    f = 0.0f;
                    f2 = 0.1f;
                }
            }
            AvatarHolderView avatarHolderView5 = this.toAvatar;
            if (avatarHolderView5.chat == null) {
                avatarHolderView5.setChat(chat);
            }
            this.addedChats.removeAll(arrayList);
            this.addedChats.addAll(arrayList2);
            this.avatarsContainer.animate().cancel();
            if (this.addedChats.isEmpty() || this.addedChats.size() == 1) {
                i = 200;
                translationX = this.avatarsContainer.animate().setInterpolator(cubicBezierInterpolator).translationX(0.0f);
            } else {
                translationX = this.avatarsContainer.animate().setInterpolator(cubicBezierInterpolator).translationX(AndroidUtilities.dp(11.5f) * (this.addedChats.size() - 1));
                i = 200;
            }
            translationX.setDuration(i).start();
            this.toAvatar.animate().cancel();
            this.avatarsWrapper.animate().cancel();
            if (this.addedChats.isEmpty()) {
                dp = 0.0f;
                j = 200;
                this.avatarsWrapper.animate().setInterpolator(cubicBezierInterpolator).translationX(0.0f).setDuration(j).start();
                interpolator = this.toAvatar.animate().setInterpolator(cubicBezierInterpolator);
            } else {
                j = 200;
                this.avatarsWrapper.animate().setInterpolator(cubicBezierInterpolator).translationX(-AndroidUtilities.dp(48.0f)).setDuration(j).start();
                interpolator = this.toAvatar.animate().setInterpolator(cubicBezierInterpolator);
                dp = AndroidUtilities.dp(48.0f);
            }
            interpolator.translationX(dp).setDuration(j).start();
        }
    }

    public ReassignBoostBottomSheet(BaseFragment baseFragment, TL_stories.TL_premium_myBoosts tL_premium_myBoosts, final TLRPC.Chat chat) {
        super(baseFragment, false, false);
        this.selectedBoosts = new ArrayList();
        this.allUsedBoosts = new ArrayList();
        this.topPadding = 0.3f;
        this.myBoosts = tL_premium_myBoosts;
        this.currentChat = chat;
        Iterator<TL_stories.TL_myBoost> it = tL_premium_myBoosts.my_boosts.iterator();
        while (it.hasNext()) {
            TL_stories.TL_myBoost next = it.next();
            TLRPC.Peer peer = next.peer;
            if (peer != null && DialogObject.getPeerDialogId(peer) != (-chat.id)) {
                this.allUsedBoosts.add(next);
            }
        }
        SelectorBtnCell selectorBtnCell = new SelectorBtnCell(getContext(), this.resourcesProvider, this.recyclerListView);
        this.buttonContainer = selectorBtnCell;
        selectorBtnCell.setClickable(true);
        selectorBtnCell.setOrientation(1);
        selectorBtnCell.setPadding(AndroidUtilities.dp(8.0f), AndroidUtilities.dp(8.0f), AndroidUtilities.dp(8.0f), AndroidUtilities.dp(8.0f));
        selectorBtnCell.setBackgroundColor(Theme.getColor(Theme.key_dialogBackground, this.resourcesProvider));
        GradientButtonWithCounterView gradientButtonWithCounterView = new GradientButtonWithCounterView(getContext(), true, this.resourcesProvider);
        this.actionButton = gradientButtonWithCounterView;
        gradientButtonWithCounterView.withCounterIcon();
        gradientButtonWithCounterView.setCounterColor(-6785796);
        gradientButtonWithCounterView.setOnClickListener(new View.OnClickListener() {
            @Override
            public final void onClick(View view) {
                ReassignBoostBottomSheet.this.lambda$new$3(chat, view);
            }
        });
        selectorBtnCell.addView(gradientButtonWithCounterView, LayoutHelper.createLinear(-1, 48, 87));
        ViewGroup viewGroup = this.containerView;
        int i = this.backgroundPaddingLeft;
        viewGroup.addView(selectorBtnCell, LayoutHelper.createFrameMarginPx(-1, -2.0f, 87, i, 0, i, 0));
        RecyclerListView recyclerListView = this.recyclerListView;
        int i2 = this.backgroundPaddingLeft;
        recyclerListView.setPadding(i2, 0, i2, AndroidUtilities.dp(64.0f));
        this.recyclerListView.setOnItemClickListener(new RecyclerListView.OnItemClickListener() {
            @Override
            public final void onItemClick(View view, int i3) {
                ReassignBoostBottomSheet.this.lambda$new$4(chat, view, i3);
            }
        });
        fixNavigationBar();
        updateTitle();
        updateActionButton(false);
        Bulletin.addDelegate(this.container, new Bulletin.Delegate() {
            @Override
            public boolean allowLayoutChanges() {
                return Bulletin.Delegate.CC.$default$allowLayoutChanges(this);
            }

            @Override
            public boolean bottomOffsetAnimated() {
                return Bulletin.Delegate.CC.$default$bottomOffsetAnimated(this);
            }

            @Override
            public boolean clipWithGradient(int i3) {
                return Bulletin.Delegate.CC.$default$clipWithGradient(this, i3);
            }

            @Override
            public int getBottomOffset(int i3) {
                return Bulletin.Delegate.CC.$default$getBottomOffset(this, i3);
            }

            @Override
            public int getTopOffset(int i3) {
                return AndroidUtilities.statusBarHeight;
            }

            @Override
            public void onBottomOffsetChange(float f) {
                Bulletin.Delegate.CC.$default$onBottomOffsetChange(this, f);
            }

            @Override
            public void onHide(Bulletin bulletin) {
                Bulletin.Delegate.CC.$default$onHide(this, bulletin);
            }

            @Override
            public void onShow(Bulletin bulletin) {
                Bulletin.Delegate.CC.$default$onShow(this, bulletin);
            }
        });
    }

    public void lambda$new$0(TL_stories.TL_premium_myBoosts tL_premium_myBoosts, List list, HashSet hashSet, TL_stories.TL_premium_boostsStatus tL_premium_boostsStatus) {
        dismiss();
        NotificationCenter.getInstance(UserConfig.selectedAccount).lambda$postNotificationNameOnUIThread$1(NotificationCenter.boostedChannelByUser, tL_premium_myBoosts, Integer.valueOf(list.size()), Integer.valueOf(hashSet.size()), tL_premium_boostsStatus);
    }

    public void lambda$new$1(TLRPC.Chat chat, final List list, final HashSet hashSet, final TL_stories.TL_premium_myBoosts tL_premium_myBoosts) {
        MessagesController.getInstance(this.currentAccount).getBoostsController().getBoostsStats(-chat.id, new Consumer() {
            @Override
            public final void accept(Object obj) {
                ReassignBoostBottomSheet.this.lambda$new$0(tL_premium_myBoosts, list, hashSet, (TL_stories.TL_premium_boostsStatus) obj);
            }
        });
    }

    public void lambda$new$2(TLRPC.TL_error tL_error) {
        this.actionButton.setLoading(false);
        BoostDialogs.showToastError(getContext(), tL_error);
    }

    public void lambda$new$3(final TLRPC.Chat chat, View view) {
        if (this.selectedBoosts.isEmpty() || this.actionButton.isLoading()) {
            return;
        }
        this.actionButton.setLoading(true);
        final ArrayList arrayList = new ArrayList();
        final HashSet hashSet = new HashSet();
        for (TL_stories.TL_myBoost tL_myBoost : this.selectedBoosts) {
            arrayList.add(Integer.valueOf(tL_myBoost.slot));
            hashSet.add(Long.valueOf(DialogObject.getPeerDialogId(tL_myBoost.peer)));
        }
        BoostRepository.applyBoost(chat.id, arrayList, new Utilities.Callback() {
            @Override
            public final void run(Object obj) {
                ReassignBoostBottomSheet.this.lambda$new$1(chat, arrayList, hashSet, (TL_stories.TL_premium_myBoosts) obj);
            }
        }, new Utilities.Callback() {
            @Override
            public final void run(Object obj) {
                ReassignBoostBottomSheet.this.lambda$new$2((TLRPC.TL_error) obj);
            }
        });
    }

    public void lambda$new$4(TLRPC.Chat chat, View view, int i) {
        if (view instanceof SelectorUserCell) {
            SelectorUserCell selectorUserCell = (SelectorUserCell) view;
            if (selectorUserCell.getBoost().cooldown_until_date > 0) {
                BulletinFactory.of(this.container, this.resourcesProvider).createSimpleBulletin(R.raw.chats_infotip, AndroidUtilities.replaceTags(LocaleController.formatPluralString("BoostingWaitWarningPlural", BoostRepository.boostsPerSentGift(), new Object[0])), 5).show(true);
                return;
            }
            if (this.selectedBoosts.contains(selectorUserCell.getBoost())) {
                this.selectedBoosts.remove(selectorUserCell.getBoost());
            } else {
                this.selectedBoosts.add(selectorUserCell.getBoost());
            }
            selectorUserCell.setChecked(this.selectedBoosts.contains(selectorUserCell.getBoost()), true);
            updateActionButton(true);
            this.topCell.showBoosts(this.selectedBoosts, chat);
        }
    }

    public static ReassignBoostBottomSheet show(BaseFragment baseFragment, TL_stories.TL_premium_myBoosts tL_premium_myBoosts, TLRPC.Chat chat) {
        ReassignBoostBottomSheet reassignBoostBottomSheet = new ReassignBoostBottomSheet(baseFragment, tL_premium_myBoosts, chat);
        reassignBoostBottomSheet.show();
        return reassignBoostBottomSheet;
    }

    private void updateActionButton(boolean z) {
        ButtonWithCounterView buttonWithCounterView;
        int i;
        this.actionButton.setShowZero(false);
        if (this.selectedBoosts.size() > 1) {
            buttonWithCounterView = this.actionButton;
            i = R.string.BoostingReassignBoosts;
        } else {
            buttonWithCounterView = this.actionButton;
            i = R.string.BoostingReassignBoost;
        }
        buttonWithCounterView.setText(LocaleController.getString(i), z);
        this.actionButton.setCount(this.selectedBoosts.size(), z);
        this.actionButton.setEnabled(this.selectedBoosts.size() > 0);
    }

    @Override
    protected RecyclerListView.SelectionAdapter createAdapter(RecyclerListView recyclerListView) {
        return new RecyclerListView.SelectionAdapter() {
            @Override
            public int getItemCount() {
                return ReassignBoostBottomSheet.this.allUsedBoosts.size() + 3;
            }

            @Override
            public int getItemViewType(int i) {
                if (i == 0) {
                    return 0;
                }
                int i2 = 1;
                if (i != 1) {
                    i2 = 2;
                    if (i != 2) {
                        return 3;
                    }
                }
                return i2;
            }

            @Override
            public boolean isEnabled(RecyclerView.ViewHolder viewHolder) {
                return viewHolder.getItemViewType() == 3;
            }

            @Override
            public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
                if (viewHolder.getItemViewType() == 3) {
                    TL_stories.TL_myBoost tL_myBoost = (TL_stories.TL_myBoost) ReassignBoostBottomSheet.this.allUsedBoosts.get(i - 3);
                    SelectorUserCell selectorUserCell = (SelectorUserCell) viewHolder.itemView;
                    selectorUserCell.setBoost(tL_myBoost);
                    selectorUserCell.setChecked(ReassignBoostBottomSheet.this.selectedBoosts.contains(tL_myBoost), false);
                    return;
                }
                if (viewHolder.getItemViewType() == 2) {
                    HeaderCell headerCell = (HeaderCell) viewHolder.itemView;
                    headerCell.setTextSize(15.0f);
                    headerCell.setPadding(0, 0, 0, AndroidUtilities.dp(2.0f));
                    headerCell.setText(LocaleController.getString(R.string.BoostingRemoveBoostFrom));
                    return;
                }
                if (viewHolder.getItemViewType() == 0) {
                    ReassignBoostBottomSheet.this.topCell = (TopCell) viewHolder.itemView;
                    ReassignBoostBottomSheet.this.topCell.setData(ReassignBoostBottomSheet.this.currentChat, ReassignBoostBottomSheet.this);
                }
            }

            @Override
            public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
                View view;
                Context context = viewGroup.getContext();
                if (i != 0) {
                    view = i != 1 ? i != 2 ? i != 3 ? new View(context) : new SelectorUserCell(context, true, ((BottomSheet) ReassignBoostBottomSheet.this).resourcesProvider, true) : new HeaderCell(context, 22) : new ShadowSectionCell(context, 12, Theme.getColor(Theme.key_windowBackgroundGray));
                } else {
                    TopCell topCell = new TopCell(context);
                    topCell.showBoosts(ReassignBoostBottomSheet.this.selectedBoosts, ReassignBoostBottomSheet.this.currentChat);
                    view = topCell;
                }
                view.setLayoutParams(new RecyclerView.LayoutParams(-1, -2));
                return new RecyclerListView.Holder(view);
            }
        };
    }

    @Override
    protected CharSequence getTitle() {
        return LocaleController.getString(R.string.BoostingReassignBoost);
    }

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        this.timer = new CountDownTimer(Long.MAX_VALUE, 1000L) {
            @Override
            public void onFinish() {
            }

            @Override
            public void onTick(long j) {
                int i;
                ArrayList arrayList = new ArrayList(ReassignBoostBottomSheet.this.allUsedBoosts.size());
                Iterator it = ReassignBoostBottomSheet.this.allUsedBoosts.iterator();
                while (true) {
                    if (!it.hasNext()) {
                        break;
                    }
                    TL_stories.TL_myBoost tL_myBoost = (TL_stories.TL_myBoost) it.next();
                    if (tL_myBoost.cooldown_until_date > 0) {
                        arrayList.add(tL_myBoost);
                    }
                    if (tL_myBoost.cooldown_until_date * 1000 < System.currentTimeMillis()) {
                        tL_myBoost.cooldown_until_date = 0;
                    }
                }
                if (arrayList.isEmpty()) {
                    return;
                }
                for (i = 0; i < ((BottomSheetWithRecyclerListView) ReassignBoostBottomSheet.this).recyclerListView.getChildCount(); i++) {
                    View childAt = ((BottomSheetWithRecyclerListView) ReassignBoostBottomSheet.this).recyclerListView.getChildAt(i);
                    if (childAt instanceof SelectorUserCell) {
                        SelectorUserCell selectorUserCell = (SelectorUserCell) childAt;
                        if (arrayList.contains(selectorUserCell.getBoost())) {
                            selectorUserCell.updateTimer();
                        }
                    }
                }
            }
        };
    }

    @Override
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        this.timer.cancel();
    }

    @Override
    public void onOpenAnimationEnd() {
        this.timer.start();
    }
}
