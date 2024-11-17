package org.telegram.ui.Components;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.text.SpannableString;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.ColorUtils;
import androidx.dynamicanimation.animation.DynamicAnimation;
import androidx.dynamicanimation.animation.SpringAnimation;
import androidx.dynamicanimation.animation.SpringForce;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ChatObject;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.R;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.UserObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.ActionBarPopupWindow;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ChatActivity;
import org.telegram.ui.Components.Bulletin;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.PremiumPreviewFragment;

public abstract class SenderSelectPopup extends ActionBarPopupWindow {
    private FrameLayout bulletinContainer;
    private Runnable bulletinHideCallback;
    private List bulletins;
    private TLRPC.ChatFull chatFull;
    private boolean clicked;
    private final int currentAccount;
    public View dimView;
    private boolean dismissed;
    private View headerShadow;
    public TextView headerText;
    private boolean isDismissingByBulletin;
    private Boolean isHeaderShadowVisible;
    private LinearLayoutManager layoutManager;
    private int popupX;
    private int popupY;
    public LinearLayout recyclerContainer;
    private RecyclerListView recyclerView;
    protected boolean runningCustomSprings;
    private FrameLayout scrimPopupContainerLayout;
    private TLRPC.TL_channels_sendAsPeers sendAsPeers;
    protected List springAnimations;

    private class BackButtonFrameLayout extends FrameLayout {
        public BackButtonFrameLayout(Context context) {
            super(context);
        }

        @Override
        public boolean dispatchKeyEvent(KeyEvent keyEvent) {
            if (keyEvent.getKeyCode() == 4 && keyEvent.getRepeatCount() == 0 && SenderSelectPopup.this.isShowing()) {
                SenderSelectPopup.this.dismiss();
            }
            return super.dispatchKeyEvent(keyEvent);
        }
    }

    public interface OnSelectCallback {
        void onPeerSelected(RecyclerView recyclerView, SenderView senderView, TLRPC.Peer peer);
    }

    public static final class SenderView extends LinearLayout {
        public final SimpleAvatarView avatar;
        public final TextView subtitle;
        public final TextView title;

        public SenderView(Context context) {
            super(context);
            setLayoutParams(new RecyclerView.LayoutParams(-1, -2));
            setOrientation(0);
            setGravity(16);
            int dp = AndroidUtilities.dp(14.0f);
            int i = dp / 2;
            setPadding(dp, i, dp, i);
            SimpleAvatarView simpleAvatarView = new SimpleAvatarView(context);
            this.avatar = simpleAvatarView;
            addView(simpleAvatarView, LayoutHelper.createFrame(40, 40.0f));
            LinearLayout linearLayout = new LinearLayout(context);
            linearLayout.setOrientation(1);
            addView(linearLayout, LayoutHelper.createLinear(0, -1, 1.0f, 12, 0, 0, 0));
            TextView textView = new TextView(context);
            this.title = textView;
            int i2 = Theme.key_actionBarDefaultSubmenuItem;
            textView.setTextColor(Theme.getColor(i2));
            textView.setTextSize(1, 16.0f);
            textView.setTag(textView);
            textView.setMaxLines(1);
            linearLayout.addView(textView);
            TextView textView2 = new TextView(context);
            this.subtitle = textView2;
            textView2.setTextColor(ColorUtils.setAlphaComponent(Theme.getColor(i2), 102));
            textView2.setTextSize(1, 14.0f);
            textView2.setTag(textView2);
            textView2.setMaxLines(1);
            textView2.setEllipsize(TextUtils.TruncateAt.END);
            linearLayout.addView(textView2);
        }
    }

    public SenderSelectPopup(final Context context, final ChatActivity chatActivity, final MessagesController messagesController, final TLRPC.ChatFull chatFull, TLRPC.TL_channels_sendAsPeers tL_channels_sendAsPeers, final OnSelectCallback onSelectCallback) {
        super(context);
        this.springAnimations = new ArrayList();
        this.bulletins = new ArrayList();
        this.chatFull = chatFull;
        this.sendAsPeers = tL_channels_sendAsPeers;
        this.currentAccount = chatActivity == null ? UserConfig.selectedAccount : chatActivity.getCurrentAccount();
        BackButtonFrameLayout backButtonFrameLayout = new BackButtonFrameLayout(context);
        this.scrimPopupContainerLayout = backButtonFrameLayout;
        backButtonFrameLayout.setLayoutParams(LayoutHelper.createFrame(-2, -2.0f));
        setContentView(this.scrimPopupContainerLayout);
        setWidth(-2);
        setHeight(-2);
        setBackgroundDrawable(null);
        Drawable mutate = ContextCompat.getDrawable(context, R.drawable.popup_fixed_alert).mutate();
        mutate.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_actionBarDefaultSubmenuBackground), PorterDuff.Mode.MULTIPLY));
        this.scrimPopupContainerLayout.setBackground(mutate);
        android.graphics.Rect rect = new android.graphics.Rect();
        mutate.getPadding(rect);
        this.scrimPopupContainerLayout.setPadding(rect.left, rect.top, rect.right, rect.bottom);
        View view = new View(context);
        this.dimView = view;
        view.setBackgroundColor(855638016);
        final int dp = AndroidUtilities.dp(450.0f);
        final int width = (int) (chatActivity.contentView.getWidth() * 0.75f);
        LinearLayout linearLayout = new LinearLayout(context) {
            @Override
            protected int getSuggestedMinimumWidth() {
                return AndroidUtilities.dp(260.0f);
            }

            @Override
            protected void onMeasure(int i, int i2) {
                super.onMeasure(View.MeasureSpec.makeMeasureSpec(Math.min(View.MeasureSpec.getSize(i), width), Integer.MIN_VALUE), View.MeasureSpec.makeMeasureSpec(Math.min(View.MeasureSpec.getSize(i2), dp), View.MeasureSpec.getMode(i2)));
            }
        };
        this.recyclerContainer = linearLayout;
        linearLayout.setOrientation(1);
        TextView textView = new TextView(context);
        this.headerText = textView;
        textView.setTextColor(Theme.getColor(Theme.key_dialogTextBlue));
        this.headerText.setTextSize(1, 16.0f);
        this.headerText.setText(LocaleController.getString(R.string.SendMessageAsTitle));
        this.headerText.setTypeface(AndroidUtilities.bold(), 1);
        int dp2 = AndroidUtilities.dp(18.0f);
        this.headerText.setPadding(dp2, AndroidUtilities.dp(12.0f), dp2, AndroidUtilities.dp(12.0f));
        this.recyclerContainer.addView(this.headerText);
        FrameLayout frameLayout = new FrameLayout(context);
        final ArrayList<TLRPC.TL_sendAsPeer> arrayList = tL_channels_sendAsPeers.peers;
        this.recyclerView = new RecyclerListView(context);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
        this.layoutManager = linearLayoutManager;
        this.recyclerView.setLayoutManager(linearLayoutManager);
        this.recyclerView.setAdapter(new RecyclerListView.SelectionAdapter() {
            @Override
            public int getItemCount() {
                return arrayList.size();
            }

            @Override
            public boolean isEnabled(RecyclerView.ViewHolder viewHolder) {
                return true;
            }

            @Override
            public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
                SimpleAvatarView simpleAvatarView;
                SenderView senderView = (SenderView) viewHolder.itemView;
                TLRPC.TL_sendAsPeer tL_sendAsPeer = (TLRPC.TL_sendAsPeer) arrayList.get(i);
                TLRPC.Peer peer = tL_sendAsPeer.peer;
                long j = peer.channel_id;
                long j2 = j != 0 ? -j : 0L;
                if (j2 == 0) {
                    long j3 = peer.user_id;
                    if (j3 != 0) {
                        j2 = j3;
                    }
                }
                boolean z = true;
                if (j2 < 0) {
                    TLRPC.Chat chat = messagesController.getChat(Long.valueOf(-j2));
                    if (chat != null) {
                        if (tL_sendAsPeer.premium_required) {
                            SpannableString spannableString = new SpannableString(((Object) TextUtils.ellipsize(chat.title, senderView.title.getPaint(), width - AndroidUtilities.dp(100.0f), TextUtils.TruncateAt.END)) + " d");
                            ColoredImageSpan coloredImageSpan = new ColoredImageSpan(R.drawable.msg_mini_premiumlock);
                            coloredImageSpan.setTopOffset(1);
                            coloredImageSpan.setSize(AndroidUtilities.dp(14.0f));
                            coloredImageSpan.setColorKey(Theme.key_windowBackgroundWhiteGrayText5);
                            spannableString.setSpan(coloredImageSpan, spannableString.length() - 1, spannableString.length(), 33);
                            senderView.title.setEllipsize(null);
                            senderView.title.setText(spannableString);
                        } else {
                            senderView.title.setEllipsize(TextUtils.TruncateAt.END);
                            senderView.title.setText(chat.title);
                        }
                        senderView.subtitle.setText(LocaleController.formatPluralString((!ChatObject.isChannel(chat) || chat.megagroup) ? "Members" : "Subscribers", chat.participants_count, new Object[0]));
                        senderView.avatar.setAvatar(chat);
                    }
                    simpleAvatarView = senderView.avatar;
                    TLRPC.Peer peer2 = chatFull.default_send_as;
                    z = peer2 == null ? false : false;
                } else {
                    TLRPC.User user = messagesController.getUser(Long.valueOf(j2));
                    if (user != null) {
                        senderView.title.setText(UserObject.getUserName(user));
                        senderView.subtitle.setText(LocaleController.getString(R.string.VoipGroupPersonalAccount));
                        senderView.avatar.setAvatar(user);
                    }
                    simpleAvatarView = senderView.avatar;
                    TLRPC.Peer peer3 = chatFull.default_send_as;
                    if (peer3 == null) {
                    }
                }
                simpleAvatarView.setSelected(z, false);
            }

            @Override
            public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
                return new RecyclerListView.Holder(new SenderView(viewGroup.getContext()));
            }
        });
        this.recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int i, int i2) {
                boolean z = SenderSelectPopup.this.layoutManager.findFirstCompletelyVisibleItemPosition() != 0;
                if (SenderSelectPopup.this.isHeaderShadowVisible == null || z != SenderSelectPopup.this.isHeaderShadowVisible.booleanValue()) {
                    SenderSelectPopup.this.headerShadow.animate().cancel();
                    SenderSelectPopup.this.headerShadow.animate().alpha(z ? 1.0f : 0.0f).setDuration(150L).start();
                    SenderSelectPopup.this.isHeaderShadowVisible = Boolean.valueOf(z);
                }
            }
        });
        this.recyclerView.setOnItemClickListener(new RecyclerListView.OnItemClickListener() {
            @Override
            public final void onItemClick(View view2, int i) {
                SenderSelectPopup.this.lambda$new$2(arrayList, context, chatFull, chatActivity, onSelectCallback, view2, i);
            }
        });
        this.recyclerView.setOverScrollMode(2);
        frameLayout.addView(this.recyclerView);
        this.headerShadow = new View(context);
        Drawable drawable = ContextCompat.getDrawable(context, R.drawable.header_shadow);
        drawable.setAlpha(153);
        this.headerShadow.setBackground(drawable);
        this.headerShadow.setAlpha(0.0f);
        frameLayout.addView(this.headerShadow, LayoutHelper.createFrame(-1, 4.0f));
        this.recyclerContainer.addView(frameLayout, LayoutHelper.createFrame(-1, -2.0f));
        this.scrimPopupContainerLayout.addView(this.recyclerContainer);
    }

    public void lambda$new$0(ChatActivity chatActivity) {
        if (chatActivity != null) {
            chatActivity.presentFragment(new PremiumPreviewFragment("select_sender"));
            dismiss();
        }
    }

    public void lambda$new$1(WindowManager windowManager) {
        windowManager.removeView(this.bulletinContainer);
    }

    public void lambda$new$2(List list, Context context, TLRPC.ChatFull chatFull, final ChatActivity chatActivity, OnSelectCallback onSelectCallback, View view, int i) {
        TLRPC.TL_sendAsPeer tL_sendAsPeer = (TLRPC.TL_sendAsPeer) list.get(i);
        if (this.clicked) {
            return;
        }
        if (!tL_sendAsPeer.premium_required || UserConfig.getInstance(UserConfig.selectedAccount).isPremium()) {
            this.clicked = true;
            onSelectCallback.onPeerSelected(this.recyclerView, (SenderView) view, tL_sendAsPeer.peer);
            return;
        }
        try {
            view.performHapticFeedback(3, 2);
        } catch (Exception unused) {
        }
        final WindowManager windowManager = (WindowManager) context.getSystemService("window");
        if (this.bulletinContainer == null) {
            this.bulletinContainer = new FrameLayout(context) {
                @Override
                public boolean onTouchEvent(MotionEvent motionEvent) {
                    View contentView = SenderSelectPopup.this.getContentView();
                    contentView.getLocationInWindow(r2);
                    int[] iArr = {iArr[0] + SenderSelectPopup.this.popupX, iArr[1] + SenderSelectPopup.this.popupY};
                    getLocationInWindow(new int[2]);
                    if ((motionEvent.getAction() != 0 || motionEvent.getX() > iArr[0]) && motionEvent.getX() < iArr[0] + contentView.getWidth() && motionEvent.getY() > iArr[1] && motionEvent.getY() < iArr[1] + contentView.getHeight()) {
                        motionEvent.offsetLocation(r1[0] - iArr[0], (AndroidUtilities.statusBarHeight + r1[1]) - iArr[1]);
                        return contentView.dispatchTouchEvent(motionEvent);
                    }
                    if (!SenderSelectPopup.this.dismissed && !SenderSelectPopup.this.isDismissingByBulletin) {
                        SenderSelectPopup.this.isDismissingByBulletin = true;
                        SenderSelectPopup.this.startDismissAnimation(new SpringAnimation[0]);
                    }
                    return true;
                }
            };
        }
        Runnable runnable = this.bulletinHideCallback;
        if (runnable != null) {
            AndroidUtilities.cancelRunOnUIThread(runnable);
        }
        if (this.bulletinContainer.getParent() == null) {
            WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
            layoutParams.height = -1;
            layoutParams.width = -1;
            layoutParams.format = -3;
            layoutParams.type = 99;
            int i2 = Build.VERSION.SDK_INT;
            if (i2 >= 21) {
                layoutParams.flags |= Integer.MIN_VALUE;
            }
            if (i2 >= 28) {
                layoutParams.layoutInDisplayCutoutMode = 1;
            }
            AndroidUtilities.setPreferredMaxRefreshRate(windowManager, this.bulletinContainer, layoutParams);
            windowManager.addView(this.bulletinContainer, layoutParams);
        }
        final Bulletin make = Bulletin.make(this.bulletinContainer, new SelectSendAsPremiumHintBulletinLayout(context, chatActivity.themeDelegate, ChatObject.isChannelAndNotMegaGroup(chatFull == null ? null : MessagesController.getInstance(this.currentAccount).getChat(Long.valueOf(chatFull.id))), new Runnable() {
            @Override
            public final void run() {
                SenderSelectPopup.this.lambda$new$0(chatActivity);
            }
        }), 1500);
        make.getLayout().addCallback(new Bulletin.Layout.Callback() {
            @Override
            public void onAttach(Bulletin.Layout layout, Bulletin bulletin) {
                Bulletin.Layout.Callback.CC.$default$onAttach(this, layout, bulletin);
            }

            @Override
            public void onDetach(Bulletin.Layout layout) {
                Bulletin.Layout.Callback.CC.$default$onDetach(this, layout);
            }

            @Override
            public void onEnterTransitionEnd(Bulletin.Layout layout) {
                Bulletin.Layout.Callback.CC.$default$onEnterTransitionEnd(this, layout);
            }

            @Override
            public void onEnterTransitionStart(Bulletin.Layout layout) {
                Bulletin.Layout.Callback.CC.$default$onEnterTransitionStart(this, layout);
            }

            @Override
            public void onExitTransitionEnd(Bulletin.Layout layout) {
                Bulletin.Layout.Callback.CC.$default$onExitTransitionEnd(this, layout);
            }

            @Override
            public void onExitTransitionStart(Bulletin.Layout layout) {
                Bulletin.Layout.Callback.CC.$default$onExitTransitionStart(this, layout);
            }

            @Override
            public void onHide(Bulletin.Layout layout) {
                SenderSelectPopup.this.bulletins.remove(make);
            }

            @Override
            public void onShow(Bulletin.Layout layout) {
                SenderSelectPopup.this.bulletins.add(make);
            }
        });
        make.show();
        Runnable runnable2 = new Runnable() {
            @Override
            public final void run() {
                SenderSelectPopup.this.lambda$new$1(windowManager);
            }
        };
        this.bulletinHideCallback = runnable2;
        AndroidUtilities.runOnUIThread(runnable2, 2500L);
    }

    public void lambda$startDismissAnimation$10(SpringAnimation springAnimation, DynamicAnimation dynamicAnimation, boolean z, float f, float f2) {
        if (z) {
            return;
        }
        this.springAnimations.remove(springAnimation);
        dynamicAnimation.cancel();
    }

    public void lambda$startDismissAnimation$6(DynamicAnimation dynamicAnimation, float f, float f2) {
        this.recyclerContainer.setScaleX(1.0f / f);
    }

    public void lambda$startDismissAnimation$7(DynamicAnimation dynamicAnimation, float f, float f2) {
        this.recyclerContainer.setScaleY(1.0f / f);
    }

    public void lambda$startDismissAnimation$8(DynamicAnimation dynamicAnimation, boolean z, float f, float f2) {
        if (this.dimView.getParent() != null) {
            ((ViewGroup) this.dimView.getParent()).removeView(this.dimView);
        }
        dismiss();
    }

    public void lambda$startDismissAnimation$9(DynamicAnimation dynamicAnimation, boolean z, float f, float f2) {
        this.runningCustomSprings = false;
    }

    public void lambda$startShowAnimation$3(DynamicAnimation dynamicAnimation, float f, float f2) {
        this.recyclerContainer.setScaleX(1.0f / f);
    }

    public void lambda$startShowAnimation$4(DynamicAnimation dynamicAnimation, float f, float f2) {
        this.recyclerContainer.setScaleY(1.0f / f);
    }

    public void lambda$startShowAnimation$5(SpringAnimation springAnimation, DynamicAnimation dynamicAnimation, boolean z, float f, float f2) {
        if (z) {
            return;
        }
        this.springAnimations.remove(springAnimation);
        dynamicAnimation.cancel();
    }

    @Override
    public void dismiss() {
        if (this.dismissed) {
            return;
        }
        FrameLayout frameLayout = this.bulletinContainer;
        if (frameLayout != null && frameLayout.getAlpha() == 1.0f) {
            final WindowManager windowManager = (WindowManager) this.bulletinContainer.getContext().getSystemService("window");
            this.bulletinContainer.animate().alpha(0.0f).setDuration(150L).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animator) {
                    try {
                        windowManager.removeViewImmediate(SenderSelectPopup.this.bulletinContainer);
                    } catch (Exception unused) {
                    }
                    if (SenderSelectPopup.this.bulletinHideCallback != null) {
                        AndroidUtilities.cancelRunOnUIThread(SenderSelectPopup.this.bulletinHideCallback);
                    }
                }
            });
        }
        this.dismissed = true;
        super.dismiss();
    }

    @Override
    public void showAtLocation(View view, int i, int i2, int i3) {
        this.popupX = i2;
        this.popupY = i3;
        super.showAtLocation(view, i, i2, i3);
    }

    public void startDismissAnimation(SpringAnimation... springAnimationArr) {
        Iterator it = new ArrayList(this.springAnimations).iterator();
        while (it.hasNext()) {
            ((SpringAnimation) it.next()).cancel();
        }
        this.springAnimations.clear();
        this.scrimPopupContainerLayout.setPivotX(AndroidUtilities.dp(8.0f));
        this.scrimPopupContainerLayout.setPivotY(r2.getMeasuredHeight() - AndroidUtilities.dp(8.0f));
        this.recyclerContainer.setPivotX(0.0f);
        this.recyclerContainer.setPivotY(0.0f);
        this.scrimPopupContainerLayout.setScaleX(1.0f);
        this.scrimPopupContainerLayout.setScaleY(1.0f);
        this.recyclerContainer.setAlpha(1.0f);
        this.dimView.setAlpha(1.0f);
        ArrayList<SpringAnimation> arrayList = new ArrayList();
        SpringAnimation springAnimation = (SpringAnimation) new SpringAnimation(this.scrimPopupContainerLayout, DynamicAnimation.SCALE_X).setSpring(new SpringForce(0.25f).setStiffness(750.0f).setDampingRatio(1.0f)).addUpdateListener(new DynamicAnimation.OnAnimationUpdateListener() {
            @Override
            public final void onAnimationUpdate(DynamicAnimation dynamicAnimation, float f, float f2) {
                SenderSelectPopup.this.lambda$startDismissAnimation$6(dynamicAnimation, f, f2);
            }
        });
        SpringAnimation springAnimation2 = (SpringAnimation) new SpringAnimation(this.scrimPopupContainerLayout, DynamicAnimation.SCALE_Y).setSpring(new SpringForce(0.25f).setStiffness(750.0f).setDampingRatio(1.0f)).addUpdateListener(new DynamicAnimation.OnAnimationUpdateListener() {
            @Override
            public final void onAnimationUpdate(DynamicAnimation dynamicAnimation, float f, float f2) {
                SenderSelectPopup.this.lambda$startDismissAnimation$7(dynamicAnimation, f, f2);
            }
        });
        FrameLayout frameLayout = this.scrimPopupContainerLayout;
        DynamicAnimation.ViewProperty viewProperty = DynamicAnimation.ALPHA;
        arrayList.addAll(Arrays.asList(springAnimation, springAnimation2, new SpringAnimation(frameLayout, viewProperty).setSpring(new SpringForce(0.0f).setStiffness(750.0f).setDampingRatio(1.0f)), new SpringAnimation(this.recyclerContainer, viewProperty).setSpring(new SpringForce(0.25f).setStiffness(750.0f).setDampingRatio(1.0f)), (SpringAnimation) new SpringAnimation(this.dimView, viewProperty).setSpring(new SpringForce(0.0f).setStiffness(750.0f).setDampingRatio(1.0f)).addEndListener(new DynamicAnimation.OnAnimationEndListener() {
            @Override
            public final void onAnimationEnd(DynamicAnimation dynamicAnimation, boolean z, float f, float f2) {
                SenderSelectPopup.this.lambda$startDismissAnimation$8(dynamicAnimation, z, f, f2);
            }
        })));
        arrayList.addAll(Arrays.asList(springAnimationArr));
        this.runningCustomSprings = springAnimationArr.length > 0;
        ((SpringAnimation) arrayList.get(0)).addEndListener(new DynamicAnimation.OnAnimationEndListener() {
            @Override
            public final void onAnimationEnd(DynamicAnimation dynamicAnimation, boolean z, float f, float f2) {
                SenderSelectPopup.this.lambda$startDismissAnimation$9(dynamicAnimation, z, f, f2);
            }
        });
        for (final SpringAnimation springAnimation3 : arrayList) {
            this.springAnimations.add(springAnimation3);
            springAnimation3.addEndListener(new DynamicAnimation.OnAnimationEndListener() {
                @Override
                public final void onAnimationEnd(DynamicAnimation dynamicAnimation, boolean z, float f, float f2) {
                    SenderSelectPopup.this.lambda$startDismissAnimation$10(springAnimation3, dynamicAnimation, z, f, f2);
                }
            });
            springAnimation3.start();
        }
    }

    public void startShowAnimation() {
        Iterator it = this.springAnimations.iterator();
        while (it.hasNext()) {
            ((SpringAnimation) it.next()).cancel();
        }
        this.springAnimations.clear();
        this.scrimPopupContainerLayout.setPivotX(AndroidUtilities.dp(8.0f));
        this.scrimPopupContainerLayout.setPivotY(r4.getMeasuredHeight() - AndroidUtilities.dp(8.0f));
        this.recyclerContainer.setPivotX(0.0f);
        this.recyclerContainer.setPivotY(0.0f);
        ArrayList<TLRPC.TL_sendAsPeer> arrayList = this.sendAsPeers.peers;
        TLRPC.Peer peer = this.chatFull.default_send_as;
        if (peer == null) {
            peer = null;
        }
        if (peer != null) {
            int dp = AndroidUtilities.dp(54.0f);
            int size = arrayList.size() * dp;
            int i = 0;
            while (i < arrayList.size()) {
                TLRPC.Peer peer2 = arrayList.get(i).peer;
                long j = peer2.channel_id;
                if (j == 0 || j != peer.channel_id) {
                    long j2 = peer2.user_id;
                    if (j2 == 0 || j2 != peer.user_id) {
                        long j3 = peer2.chat_id;
                        if (j3 == 0 || j3 != peer.chat_id) {
                            i++;
                        }
                    }
                }
                this.layoutManager.scrollToPositionWithOffset(i, ((i == arrayList.size() - 1 || this.recyclerView.getMeasuredHeight() >= size) ? 0 : this.recyclerView.getMeasuredHeight() % dp) + AndroidUtilities.dp(7.0f) + (size - ((arrayList.size() - 2) * dp)));
                if (this.recyclerView.computeVerticalScrollOffset() > 0) {
                    this.headerShadow.animate().cancel();
                    this.headerShadow.animate().alpha(1.0f).setDuration(150L).start();
                }
            }
        }
        this.scrimPopupContainerLayout.setScaleX(0.25f);
        this.scrimPopupContainerLayout.setScaleY(0.25f);
        this.recyclerContainer.setAlpha(0.25f);
        this.dimView.setAlpha(0.0f);
        SpringAnimation springAnimation = (SpringAnimation) new SpringAnimation(this.scrimPopupContainerLayout, DynamicAnimation.SCALE_X).setSpring(new SpringForce(1.0f).setStiffness(750.0f).setDampingRatio(1.0f)).addUpdateListener(new DynamicAnimation.OnAnimationUpdateListener() {
            @Override
            public final void onAnimationUpdate(DynamicAnimation dynamicAnimation, float f, float f2) {
                SenderSelectPopup.this.lambda$startShowAnimation$3(dynamicAnimation, f, f2);
            }
        });
        SpringAnimation springAnimation2 = (SpringAnimation) new SpringAnimation(this.scrimPopupContainerLayout, DynamicAnimation.SCALE_Y).setSpring(new SpringForce(1.0f).setStiffness(750.0f).setDampingRatio(1.0f)).addUpdateListener(new DynamicAnimation.OnAnimationUpdateListener() {
            @Override
            public final void onAnimationUpdate(DynamicAnimation dynamicAnimation, float f, float f2) {
                SenderSelectPopup.this.lambda$startShowAnimation$4(dynamicAnimation, f, f2);
            }
        });
        FrameLayout frameLayout = this.scrimPopupContainerLayout;
        DynamicAnimation.ViewProperty viewProperty = DynamicAnimation.ALPHA;
        for (final SpringAnimation springAnimation3 : Arrays.asList(springAnimation, springAnimation2, new SpringAnimation(frameLayout, viewProperty).setSpring(new SpringForce(1.0f).setStiffness(750.0f).setDampingRatio(1.0f)), new SpringAnimation(this.recyclerContainer, viewProperty).setSpring(new SpringForce(1.0f).setStiffness(750.0f).setDampingRatio(1.0f)), new SpringAnimation(this.dimView, viewProperty).setSpring(new SpringForce(1.0f).setStiffness(750.0f).setDampingRatio(1.0f)))) {
            this.springAnimations.add(springAnimation3);
            springAnimation3.addEndListener(new DynamicAnimation.OnAnimationEndListener() {
                @Override
                public final void onAnimationEnd(DynamicAnimation dynamicAnimation, boolean z, float f, float f2) {
                    SenderSelectPopup.this.lambda$startShowAnimation$5(springAnimation3, dynamicAnimation, z, f, f2);
                }
            });
            springAnimation3.start();
        }
    }
}
