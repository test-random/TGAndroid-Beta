package org.telegram.ui.Stars;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.style.ImageSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.ReplacementSpan;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.OvershootInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.core.graphics.ColorUtils;
import androidx.core.view.NestedScrollingParent3;
import androidx.core.view.NestedScrollingParentHelper;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Objects;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.BillingController;
import org.telegram.messenger.BirthdayController;
import org.telegram.messenger.DialogObject;
import org.telegram.messenger.DocumentObject;
import org.telegram.messenger.Emoji;
import org.telegram.messenger.FileLoader;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.ImageLocation;
import org.telegram.messenger.ImageReceiver;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MediaDataController;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.R;
import org.telegram.messenger.SvgHelper;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.UserObject;
import org.telegram.messenger.Utilities;
import org.telegram.messenger.WebFile;
import org.telegram.messenger.browser.Browser;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.tgnet.tl.TL_payments;
import org.telegram.tgnet.tl.TL_stars;
import org.telegram.tgnet.tl.TL_stories;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.BottomSheet;
import org.telegram.ui.ActionBar.BottomSheet$$ExternalSyntheticLambda11;
import org.telegram.ui.ActionBar.SimpleTextView;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Cells.ChatActionCell$$ExternalSyntheticLambda7;
import org.telegram.ui.Cells.HeaderCell;
import org.telegram.ui.Cells.SessionCell;
import org.telegram.ui.ChatActivity;
import org.telegram.ui.ChatEditActivity;
import org.telegram.ui.Components.AnimatedFloat;
import org.telegram.ui.Components.AnimatedTextView;
import org.telegram.ui.Components.AvatarDrawable;
import org.telegram.ui.Components.BackupImageView;
import org.telegram.ui.Components.BottomSheetWithRecyclerListView;
import org.telegram.ui.Components.BulletinFactory;
import org.telegram.ui.Components.ColoredImageSpan;
import org.telegram.ui.Components.CombinedDrawable;
import org.telegram.ui.Components.CubicBezierInterpolator;
import org.telegram.ui.Components.EditTextBoldCursor;
import org.telegram.ui.Components.FireworksOverlay;
import org.telegram.ui.Components.FlickerLoadingView;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.LinkSpanDrawable;
import org.telegram.ui.Components.LoadingSpan;
import org.telegram.ui.Components.OutlineTextContainerView;
import org.telegram.ui.Components.Premium.GLIcon.GLIconRenderer;
import org.telegram.ui.Components.Premium.GLIcon.GLIconTextureView;
import org.telegram.ui.Components.Premium.StarParticlesView;
import org.telegram.ui.Components.Premium.boosts.UserSelectorBottomSheet;
import org.telegram.ui.Components.RLottieDrawable;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.Components.ScaleStateListAnimator;
import org.telegram.ui.Components.StarAppsSheet;
import org.telegram.ui.Components.TableView;
import org.telegram.ui.Components.Text;
import org.telegram.ui.Components.UItem;
import org.telegram.ui.Components.UniversalAdapter;
import org.telegram.ui.Components.UniversalRecyclerView;
import org.telegram.ui.Components.ViewPagerFixed;
import org.telegram.ui.Gifts.GiftSheet;
import org.telegram.ui.GradientHeaderActivity;
import org.telegram.ui.ImageReceiverSpan;
import org.telegram.ui.LaunchActivity;
import org.telegram.ui.PhotoViewer;
import org.telegram.ui.ProfileActivity;
import org.telegram.ui.Stars.StarsIntroActivity;
import org.telegram.ui.Stories.recorder.ButtonWithCounterView;
import org.telegram.ui.Stories.recorder.HintView2;
import org.telegram.ui.bots.AffiliateProgramFragment;
import org.telegram.ui.bots.ChannelAffiliateProgramsFragment;

public class StarsIntroActivity extends GradientHeaderActivity implements NotificationCenter.NotificationCenterDelegate {
    private static DecimalFormat floatFormat;
    private FrameLayout aboveTitleView;
    private UniversalAdapter adapter;
    private LinearLayout balanceLayout;
    private ButtonWithCounterView buyButton;
    private View emptyLayout;
    private FireworksOverlay fireworksOverlay;
    private ButtonWithCounterView giftButton;
    private boolean hadTransactions;
    private GLIconTextureView iconTextureView;
    private SpannableStringBuilder starBalanceIcon;
    private AnimatedTextView starBalanceTextView;
    private TextView starBalanceTitleView;
    private StarsTransactionsLayout transactionsLayout;
    private boolean expanded = false;
    private final int BUTTON_EXPAND = -1;
    private final int BUTTON_GIFT = -2;
    private final int BUTTON_SUBSCRIPTIONS_EXPAND = -3;
    private final int BUTTON_AFFILIATE = -4;

    public class AnonymousClass2 extends StarParticlesView {
        Paint[] paints;
        final int val$particlesCount;
        final int val$type;

        AnonymousClass2(Context context, int i, int i2) {
            super(context);
            this.val$particlesCount = i;
            this.val$type = i2;
            setClipWithGradient();
        }

        public Paint lambda$configure$0(Integer num) {
            return this.paints[num.intValue() % this.paints.length];
        }

        @Override
        protected void configure() {
            StarParticlesView.Drawable drawable = new StarParticlesView.Drawable(this.val$particlesCount);
            this.drawable = drawable;
            drawable.type = 105;
            int i = 0;
            drawable.roundEffect = false;
            drawable.useRotate = false;
            drawable.useBlur = true;
            drawable.checkBounds = true;
            drawable.isCircle = false;
            drawable.useScale = true;
            drawable.startFromCenter = true;
            if (this.val$type == 1) {
                drawable.centerOffsetY = AndroidUtilities.dp(24.0f);
            }
            this.paints = new Paint[20];
            while (true) {
                Paint[] paintArr = this.paints;
                if (i >= paintArr.length) {
                    this.drawable.getPaint = new Utilities.CallbackReturn() {
                        @Override
                        public final Object run(Object obj) {
                            Paint lambda$configure$0;
                            lambda$configure$0 = StarsIntroActivity.AnonymousClass2.this.lambda$configure$0((Integer) obj);
                            return lambda$configure$0;
                        }
                    };
                    StarParticlesView.Drawable drawable2 = this.drawable;
                    drawable2.size1 = 17;
                    drawable2.size2 = 18;
                    drawable2.size3 = 19;
                    drawable2.colorKey = Theme.key_windowBackgroundWhiteBlackText;
                    drawable2.init();
                    return;
                }
                paintArr[i] = new Paint(1);
                this.paints[i].setColorFilter(new PorterDuffColorFilter(ColorUtils.blendARGB(-371690, -14281, i / (this.paints.length - 1)), PorterDuff.Mode.SRC_IN));
                i++;
            }
        }

        @Override
        protected int getStarsRectWidth() {
            return getMeasuredWidth();
        }
    }

    public static class ExpandView extends FrameLayout {
        public final ImageView arrowView;
        private int lastId;
        private boolean needDivider;
        public final AnimatedTextView textView;

        public static class Factory extends UItem.UItemFactory {
            static {
                UItem.UItemFactory.setup(new Factory());
            }

            public static UItem asExpand(int i, CharSequence charSequence, boolean z) {
                UItem ofFactory = UItem.ofFactory(Factory.class);
                ofFactory.id = i;
                ofFactory.text = charSequence;
                ofFactory.collapsed = z;
                return ofFactory;
            }

            @Override
            public void bindView(View view, UItem uItem, boolean z) {
                ((ExpandView) view).set(uItem, z);
            }

            @Override
            public ExpandView createView(Context context, int i, int i2, Theme.ResourcesProvider resourcesProvider) {
                return new ExpandView(context, resourcesProvider);
            }
        }

        public ExpandView(Context context, Theme.ResourcesProvider resourcesProvider) {
            super(context);
            AnimatedTextView animatedTextView = new AnimatedTextView(context);
            this.textView = animatedTextView;
            animatedTextView.getDrawable().setHacks(true, true, true);
            animatedTextView.setTextSize(AndroidUtilities.dp(15.0f));
            addView(animatedTextView, LayoutHelper.createFrameRelatively(-1.0f, -1.0f, 8388627, 22.0f, 0.0f, 58.0f, 0.0f));
            ImageView imageView = new ImageView(context);
            this.arrowView = imageView;
            imageView.setScaleType(ImageView.ScaleType.CENTER);
            imageView.setImageResource(R.drawable.arrow_more);
            addView(imageView, LayoutHelper.createFrameRelatively(24.0f, 24.0f, 8388629, 0.0f, 0.0f, 17.0f, 0.0f));
        }

        @Override
        protected void onDetachedFromWindow() {
            super.onDetachedFromWindow();
            this.lastId = Integer.MAX_VALUE;
        }

        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);
            if (this.needDivider) {
                canvas.drawRect(LocaleController.isRTL ? 0.0f : AndroidUtilities.dp(22.0f), getMeasuredHeight() - 1, getMeasuredWidth() - (LocaleController.isRTL ? AndroidUtilities.dp(22.0f) : 0), getMeasuredHeight(), Theme.dividerPaint);
            }
        }

        @Override
        protected void onMeasure(int i, int i2) {
            super.onMeasure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(i), 1073741824), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(48.0f), 1073741824));
        }

        public void set(String str, boolean z, boolean z2, boolean z3) {
            boolean z4 = this.lastId == -1;
            this.lastId = -1;
            this.textView.setText(str, z4);
            int color = Theme.getColor(Theme.key_windowBackgroundWhiteBlueText2);
            this.textView.setTextColor(color);
            this.arrowView.setColorFilter(new PorterDuffColorFilter(color, PorterDuff.Mode.SRC_IN));
            if (z4) {
                this.arrowView.animate().rotation(z ? 0.0f : 180.0f).setDuration(340L).setInterpolator(CubicBezierInterpolator.EASE_OUT_QUINT);
            } else {
                this.arrowView.setRotation(z ? 0.0f : 180.0f);
            }
            this.needDivider = z3;
            setWillNotDraw(!z3);
        }

        public void set(UItem uItem, boolean z) {
            int i = this.lastId;
            int i2 = uItem.id;
            boolean z2 = i == i2;
            this.lastId = i2;
            this.textView.setText(uItem.text, z2);
            int color = Theme.getColor(uItem.accent ? Theme.key_windowBackgroundWhiteBlueText2 : Theme.key_windowBackgroundWhiteBlackText);
            this.textView.setTextColor(color);
            this.arrowView.setColorFilter(new PorterDuffColorFilter(color, PorterDuff.Mode.SRC_IN));
            if (z2) {
                this.arrowView.animate().rotation(uItem.collapsed ? 0.0f : 180.0f).setDuration(340L).setInterpolator(CubicBezierInterpolator.EASE_OUT_QUINT);
            } else {
                this.arrowView.setRotation(uItem.collapsed ? 0.0f : 180.0f);
            }
            this.needDivider = z;
            setWillNotDraw(!z);
        }
    }

    public static class GiftStarsSheet extends BottomSheetWithRecyclerListView implements NotificationCenter.NotificationCenterDelegate {
        private final int BUTTON_EXPAND;
        private UniversalAdapter adapter;
        private boolean expanded;
        private final FireworksOverlay fireworksOverlay;
        private final FrameLayout footerView;
        private final HeaderView headerView;
        private final TLRPC.User user;
        private final Runnable whenPurchased;

        public static class HeaderView extends LinearLayout {
            public final BackupImageView avatarImageView;
            public final StarParticlesView particlesView;
            public final LinkSpanDrawable.LinksTextView subtitleView;
            public final TextView titleView;
            private final FrameLayout topView;

            public HeaderView(Context context, int i, Theme.ResourcesProvider resourcesProvider) {
                super(context);
                setOrientation(1);
                FrameLayout frameLayout = new FrameLayout(context);
                this.topView = frameLayout;
                frameLayout.setClipChildren(false);
                frameLayout.setClipToPadding(false);
                StarParticlesView makeParticlesView = StarsIntroActivity.makeParticlesView(context, 70, 0);
                this.particlesView = makeParticlesView;
                frameLayout.addView(makeParticlesView, LayoutHelper.createFrame(-1, -1.0f));
                BackupImageView backupImageView = new BackupImageView(context);
                this.avatarImageView = backupImageView;
                backupImageView.setRoundRadius(AndroidUtilities.dp(50.0f));
                frameLayout.addView(backupImageView, LayoutHelper.createFrame(100, 100.0f, 17, 0.0f, 32.0f, 0.0f, 24.0f));
                addView(frameLayout, LayoutHelper.createFrame(-1, 150.0f));
                TextView textView = new TextView(context);
                this.titleView = textView;
                textView.setTextSize(1, 20.0f);
                textView.setTypeface(AndroidUtilities.bold());
                int i2 = Theme.key_dialogTextBlack;
                textView.setTextColor(Theme.getColor(i2, resourcesProvider));
                textView.setGravity(17);
                addView(textView, LayoutHelper.createLinear(-2, -2, 1, 0, 2, 0, 0));
                LinkSpanDrawable.LinksTextView linksTextView = new LinkSpanDrawable.LinksTextView(context, resourcesProvider);
                this.subtitleView = linksTextView;
                linksTextView.setLinkTextColor(Theme.getColor(Theme.key_chat_messageLinkIn, resourcesProvider));
                linksTextView.setTextSize(1, 14.0f);
                linksTextView.setTextColor(Theme.getColor(i2, resourcesProvider));
                linksTextView.setGravity(17);
                addView(linksTextView, LayoutHelper.createLinear(-2, -2, 1, 0, 9, 0, 18));
            }
        }

        public GiftStarsSheet(Context context, Theme.ResourcesProvider resourcesProvider, TLRPC.User user, Runnable runnable) {
            super(context, null, false, false, false, resourcesProvider);
            this.BUTTON_EXPAND = -1;
            this.user = user;
            this.whenPurchased = runnable;
            this.topPadding = 0.2f;
            NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.starGiftOptionsLoaded);
            NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.starBalanceUpdated);
            fixNavigationBar();
            RecyclerListView recyclerListView = this.recyclerListView;
            int i = this.backgroundPaddingLeft;
            recyclerListView.setPadding(i, 0, i, 0);
            this.recyclerListView.setOnItemClickListener(new RecyclerListView.OnItemClickListener() {
                @Override
                public final void onItemClick(View view, int i2) {
                    StarsIntroActivity.GiftStarsSheet.this.lambda$new$0(view, i2);
                }
            });
            DefaultItemAnimator defaultItemAnimator = new DefaultItemAnimator();
            defaultItemAnimator.setSupportsChangeAnimations(false);
            defaultItemAnimator.setDelayAnimations(false);
            defaultItemAnimator.setInterpolator(CubicBezierInterpolator.EASE_OUT_QUINT);
            defaultItemAnimator.setDurations(350L);
            this.recyclerListView.setItemAnimator(defaultItemAnimator);
            setBackgroundColor(Theme.getColor(Theme.key_dialogBackgroundGray, resourcesProvider));
            HeaderView headerView = new HeaderView(context, this.currentAccount, resourcesProvider);
            this.headerView = headerView;
            headerView.titleView.setText(LocaleController.getString(R.string.GiftStarsTitle));
            headerView.subtitleView.setText(TextUtils.concat(AndroidUtilities.replaceTags(LocaleController.formatString(R.string.GiftStarsSubtitle, UserObject.getForcedFirstName(user))), " ", AndroidUtilities.replaceArrows(AndroidUtilities.replaceSingleTag(LocaleController.getString(R.string.GiftStarsSubtitleLinkName).replace(' ', (char) 160), new Runnable() {
                @Override
                public final void run() {
                    StarsIntroActivity.GiftStarsSheet.this.lambda$new$1();
                }
            }), true)));
            LinkSpanDrawable.LinksTextView linksTextView = headerView.subtitleView;
            linksTextView.setMaxWidth(HintView2.cutInFancyHalf(linksTextView.getText(), headerView.subtitleView.getPaint()) + 1);
            this.actionBar.setTitle(getTitle());
            AvatarDrawable avatarDrawable = new AvatarDrawable();
            avatarDrawable.setInfo(user);
            headerView.avatarImageView.setForUserOrChat(user, avatarDrawable);
            FrameLayout frameLayout = new FrameLayout(context);
            this.footerView = frameLayout;
            LinkSpanDrawable.LinksTextView linksTextView2 = new LinkSpanDrawable.LinksTextView(context, resourcesProvider);
            frameLayout.setPadding(0, AndroidUtilities.dp(11.0f), 0, AndroidUtilities.dp(11.0f));
            linksTextView2.setTextSize(1, 12.0f);
            linksTextView2.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteGrayText4, resourcesProvider));
            linksTextView2.setLinkTextColor(Theme.getColor(Theme.key_chat_messageLinkIn, resourcesProvider));
            linksTextView2.setText(AndroidUtilities.replaceSingleTag(LocaleController.getString(R.string.StarsTOS), new Runnable() {
                @Override
                public final void run() {
                    StarsIntroActivity.GiftStarsSheet.this.lambda$new$2();
                }
            }));
            linksTextView2.setGravity(17);
            linksTextView2.setMaxWidth(HintView2.cutInFancyHalf(linksTextView2.getText(), linksTextView2.getPaint()));
            frameLayout.addView(linksTextView2, LayoutHelper.createFrame(-2, -1, 17));
            frameLayout.setBackgroundColor(Theme.getColor(Theme.key_dialogBackground, resourcesProvider));
            FireworksOverlay fireworksOverlay = new FireworksOverlay(getContext());
            this.fireworksOverlay = fireworksOverlay;
            this.containerView.addView(fireworksOverlay, LayoutHelper.createFrame(-1, -1.0f));
            UniversalAdapter universalAdapter = this.adapter;
            if (universalAdapter != null) {
                universalAdapter.update(false);
            }
        }

        public void lambda$new$0(View view, int i) {
            UItem item;
            UniversalAdapter universalAdapter = this.adapter;
            if (universalAdapter == null || (item = universalAdapter.getItem(i - 1)) == null) {
                return;
            }
            onItemClick(item, this.adapter);
        }

        public void lambda$new$1() {
            BaseFragment baseFragment;
            StarAppsSheet starAppsSheet = new StarAppsSheet(getContext());
            if (!AndroidUtilities.isTablet() && !AndroidUtilities.hasDialogOnTop(this.attachedFragment) && (baseFragment = this.attachedFragment) != null) {
                starAppsSheet.makeAttached(baseFragment);
            }
            starAppsSheet.show();
        }

        public void lambda$new$2() {
            Browser.openUrl(getContext(), LocaleController.getString(R.string.StarsTOSLink));
        }

        public static void lambda$onItemClick$3(long j) {
            BaseFragment safeLastFragment = LaunchActivity.getSafeLastFragment();
            if (safeLastFragment != null) {
                safeLastFragment.presentFragment(ChatActivity.of(j));
            }
        }

        public void lambda$onItemClick$4(UItem uItem, final long j, Boolean bool, String str) {
            Runnable runnable;
            if (getContext() == null) {
                return;
            }
            if ((bool.booleanValue() || str != null) && (runnable = this.whenPurchased) != null) {
                runnable.run();
            }
            dismiss();
            BaseFragment safeLastFragment = LaunchActivity.getSafeLastFragment();
            FireworksOverlay fireworksOverlay = LaunchActivity.instance.getFireworksOverlay();
            if (safeLastFragment == null) {
                return;
            }
            if (!bool.booleanValue()) {
                if (str != null) {
                    BulletinFactory.of(safeLastFragment).createSimpleBulletin(R.raw.error, LocaleController.formatString(R.string.UnknownErrorCode, str)).show();
                }
            } else {
                BulletinFactory.of(safeLastFragment).createSimpleBulletin(R.raw.stars_send, LocaleController.getString(R.string.StarsGiftSentPopup), AndroidUtilities.replaceTags(LocaleController.formatPluralString("StarsGiftSentPopupInfo", (int) uItem.longValue, UserObject.getForcedFirstName(this.user))), LocaleController.getString(R.string.ViewInChat), new Runnable() {
                    @Override
                    public final void run() {
                        StarsIntroActivity.GiftStarsSheet.lambda$onItemClick$3(j);
                    }
                }).setDuration(5000).show(true);
                if (fireworksOverlay != null) {
                    fireworksOverlay.start(true);
                }
                StarsController.getInstance(this.currentAccount).invalidateTransactions(true);
            }
        }

        @Override
        protected RecyclerListView.SelectionAdapter createAdapter(RecyclerListView recyclerListView) {
            UniversalAdapter universalAdapter = new UniversalAdapter(this.recyclerListView, getContext(), this.currentAccount, 0, true, new Utilities.Callback2() {
                @Override
                public final void run(Object obj, Object obj2) {
                    StarsIntroActivity.GiftStarsSheet.this.fillItems((ArrayList) obj, (UniversalAdapter) obj2);
                }
            }, this.resourcesProvider);
            this.adapter = universalAdapter;
            return universalAdapter;
        }

        @Override
        public void didReceivedNotification(int i, int i2, Object... objArr) {
            UniversalAdapter universalAdapter;
            if ((i == NotificationCenter.starGiftOptionsLoaded || i == NotificationCenter.starBalanceUpdated) && (universalAdapter = this.adapter) != null) {
                universalAdapter.update(true);
            }
        }

        @Override
        public void dismiss() {
            super.dismiss();
        }

        @Override
        public void dismissInternal() {
            super.dismissInternal();
            NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.starGiftOptionsLoaded);
            NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.starBalanceUpdated);
        }

        public void fillItems(ArrayList arrayList, UniversalAdapter universalAdapter) {
            UItem asFlicker;
            arrayList.add(UItem.asCustom(this.headerView));
            arrayList.add(UItem.asHeader(LocaleController.getString(R.string.TelegramStarsChoose)));
            ArrayList giftOptions = StarsController.getInstance(this.currentAccount).getGiftOptions();
            if (giftOptions != null && !giftOptions.isEmpty()) {
                int i = 0;
                int i2 = 1;
                for (int i3 = 0; i3 < giftOptions.size(); i3++) {
                    TL_stars.TL_starsGiftOption tL_starsGiftOption = (TL_stars.TL_starsGiftOption) giftOptions.get(i3);
                    if (this.expanded || !tL_starsGiftOption.extended) {
                        arrayList.add(StarTierView.Factory.asStarTier(i3, i2, tL_starsGiftOption));
                        i2++;
                    } else {
                        i++;
                    }
                }
                boolean z = this.expanded;
                if (!z && i > 0) {
                    asFlicker = ExpandView.Factory.asExpand(-1, LocaleController.getString(z ? R.string.NotifyLessOptions : R.string.NotifyMoreOptions), !this.expanded).accent();
                }
                arrayList.add(UItem.asCustom(this.footerView));
            }
            arrayList.add(UItem.asFlicker(31));
            arrayList.add(UItem.asFlicker(31));
            asFlicker = UItem.asFlicker(31);
            arrayList.add(asFlicker);
            arrayList.add(UItem.asCustom(this.footerView));
        }

        @Override
        protected CharSequence getTitle() {
            HeaderView headerView = this.headerView;
            if (headerView == null) {
                return null;
            }
            return headerView.titleView.getText();
        }

        public void onItemClick(final UItem uItem, UniversalAdapter universalAdapter) {
            if (uItem.id == -1) {
                this.expanded = !this.expanded;
                universalAdapter.update(true);
                this.recyclerListView.smoothScrollBy(0, AndroidUtilities.dp(200.0f), CubicBezierInterpolator.EASE_OUT);
            } else if (uItem.instanceOf(StarTierView.Factory.class) && (uItem.object instanceof TL_stars.TL_starsGiftOption)) {
                Activity findActivity = AndroidUtilities.findActivity(getContext());
                if (findActivity == null) {
                    findActivity = LaunchActivity.instance;
                }
                Activity activity = findActivity;
                if (activity == null) {
                    return;
                }
                final long j = this.user.id;
                StarsController.getInstance(this.currentAccount).buyGift(activity, (TL_stars.TL_starsGiftOption) uItem.object, j, new Utilities.Callback2() {
                    @Override
                    public final void run(Object obj, Object obj2) {
                        StarsIntroActivity.GiftStarsSheet.this.lambda$onItemClick$4(uItem, j, (Boolean) obj, (String) obj2);
                    }
                });
            }
        }

        @Override
        public void show() {
            BaseFragment lastFragment = LaunchActivity.getLastFragment();
            if (lastFragment instanceof ChatActivity) {
                ChatActivity chatActivity = (ChatActivity) lastFragment;
                if (chatActivity.isKeyboardVisible() && chatActivity.getChatActivityEnterView() != null) {
                    chatActivity.getChatActivityEnterView().closeKeyboard();
                }
            }
            super.show();
        }
    }

    public class NestedFrameLayout extends GradientHeaderActivity.ContentView implements NestedScrollingParent3 {
        private NestedScrollingParentHelper nestedScrollingParentHelper;

        public NestedFrameLayout(Context context) {
            super(context);
            this.nestedScrollingParentHelper = new NestedScrollingParentHelper(this);
        }

        public void lambda$onNestedScroll$0() {
            try {
                RecyclerListView currentListView = StarsIntroActivity.this.transactionsLayout.getCurrentListView();
                if (currentListView == null || currentListView.getAdapter() == null) {
                    return;
                }
                currentListView.getAdapter().notifyDataSetChanged();
            } catch (Throwable unused) {
            }
        }

        @Override
        public boolean onNestedPreFling(View view, float f, float f2) {
            return super.onNestedPreFling(view, f, f2);
        }

        @Override
        public void onNestedPreScroll(View view, int i, int i2, int[] iArr, int i3) {
            int i4;
            if (view == ((GradientHeaderActivity) StarsIntroActivity.this).listView && StarsIntroActivity.this.transactionsLayout.isAttachedToWindow()) {
                boolean isSearchFieldVisible = ((BaseFragment) StarsIntroActivity.this).actionBar.isSearchFieldVisible();
                int top = (((View) StarsIntroActivity.this.transactionsLayout.getParent()).getTop() - AndroidUtilities.statusBarHeight) - ActionBar.getCurrentActionBarHeight();
                int bottom = ((View) StarsIntroActivity.this.transactionsLayout.getParent()).getBottom();
                boolean z = false;
                if (i2 < 0) {
                    if (((GradientHeaderActivity) StarsIntroActivity.this).listView.getHeight() - bottom >= 0) {
                        RecyclerListView currentListView = StarsIntroActivity.this.transactionsLayout.getCurrentListView();
                        int findFirstVisibleItemPosition = ((LinearLayoutManager) currentListView.getLayoutManager()).findFirstVisibleItemPosition();
                        if (findFirstVisibleItemPosition != -1) {
                            RecyclerView.ViewHolder findViewHolderForAdapterPosition = currentListView.findViewHolderForAdapterPosition(findFirstVisibleItemPosition);
                            int top2 = findViewHolderForAdapterPosition != null ? findViewHolderForAdapterPosition.itemView.getTop() : -1;
                            int paddingTop = currentListView.getPaddingTop();
                            if (top2 != paddingTop || findFirstVisibleItemPosition != 0) {
                                iArr[1] = findFirstVisibleItemPosition != 0 ? i2 : Math.max(i2, top2 - paddingTop);
                                currentListView.scrollBy(0, i2);
                                z = true;
                            }
                        }
                    }
                    if (isSearchFieldVisible) {
                        if (z || top >= 0) {
                            iArr[1] = i2;
                            return;
                        } else {
                            iArr[1] = i2 - Math.max(top, i2);
                            return;
                        }
                    }
                    return;
                }
                if (isSearchFieldVisible) {
                    RecyclerListView currentListView2 = StarsIntroActivity.this.transactionsLayout.getCurrentListView();
                    iArr[1] = i2;
                    if (top > 0) {
                        iArr[1] = i2 - i2;
                    }
                    if (currentListView2 == null || (i4 = iArr[1]) <= 0) {
                        return;
                    }
                    currentListView2.scrollBy(0, i4);
                    return;
                }
                if (i2 > 0) {
                    RecyclerListView currentListView3 = StarsIntroActivity.this.transactionsLayout.getCurrentListView();
                    if (((GradientHeaderActivity) StarsIntroActivity.this).listView.getHeight() - bottom < 0 || currentListView3 == null || currentListView3.canScrollVertically(1)) {
                        return;
                    }
                    iArr[1] = i2;
                    ((GradientHeaderActivity) StarsIntroActivity.this).listView.stopScroll();
                }
            }
        }

        @Override
        public void onNestedScroll(View view, int i, int i2, int i3, int i4, int i5) {
        }

        @Override
        public void onNestedScroll(View view, int i, int i2, int i3, int i4, int i5, int[] iArr) {
            try {
                if (view == ((GradientHeaderActivity) StarsIntroActivity.this).listView && StarsIntroActivity.this.transactionsLayout.isAttachedToWindow()) {
                    RecyclerListView currentListView = StarsIntroActivity.this.transactionsLayout.getCurrentListView();
                    if (((GradientHeaderActivity) StarsIntroActivity.this).listView.getHeight() - ((View) StarsIntroActivity.this.transactionsLayout.getParent()).getBottom() >= 0) {
                        iArr[1] = i4;
                        currentListView.scrollBy(0, i4);
                    }
                }
            } catch (Throwable th) {
                FileLog.e(th);
                AndroidUtilities.runOnUIThread(new Runnable() {
                    @Override
                    public final void run() {
                        StarsIntroActivity.NestedFrameLayout.this.lambda$onNestedScroll$0();
                    }
                });
            }
        }

        @Override
        public void onNestedScrollAccepted(View view, View view2, int i, int i2) {
            this.nestedScrollingParentHelper.onNestedScrollAccepted(view, view2, i);
        }

        @Override
        public boolean onStartNestedScroll(View view, View view2, int i, int i2) {
            return i == 2;
        }

        @Override
        public void onStopNestedScroll(View view) {
        }

        @Override
        public void onStopNestedScroll(View view, int i) {
            this.nestedScrollingParentHelper.onStopNestedScroll(view);
        }
    }

    public static class StarTierView extends FrameLayout {
        private final AnimatedFloat animatedStarsCount;
        private SpannableString loading;
        private boolean needDivider;
        private final Drawable starDrawable;
        private final Drawable starDrawableOutline;
        private int starsCount;
        private final TextView textView;
        private final AnimatedTextView textView2;

        public static class Factory extends UItem.UItemFactory {
            static {
                UItem.UItemFactory.setup(new Factory());
            }

            public static UItem asStarTier(int i, int i2, TL_stars.TL_starsGiftOption tL_starsGiftOption) {
                UItem ofFactory = UItem.ofFactory(Factory.class);
                ofFactory.id = i;
                ofFactory.intValue = i2;
                long j = tL_starsGiftOption.stars;
                ofFactory.longValue = j;
                ofFactory.text = LocaleController.formatPluralStringSpaced("StarsCount", (int) j);
                ofFactory.subtext = tL_starsGiftOption.loadingStorePrice ? null : BillingController.getInstance().formatCurrency(tL_starsGiftOption.amount, tL_starsGiftOption.currency);
                ofFactory.object = tL_starsGiftOption;
                return ofFactory;
            }

            public static UItem asStarTier(int i, int i2, TL_stars.TL_starsTopupOption tL_starsTopupOption) {
                UItem ofFactory = UItem.ofFactory(Factory.class);
                ofFactory.id = i;
                ofFactory.intValue = i2;
                long j = tL_starsTopupOption.stars;
                ofFactory.longValue = j;
                ofFactory.text = LocaleController.formatPluralStringSpaced("StarsCount", (int) j);
                ofFactory.subtext = tL_starsTopupOption.loadingStorePrice ? null : BillingController.getInstance().formatCurrency(tL_starsTopupOption.amount, tL_starsTopupOption.currency);
                ofFactory.object = tL_starsTopupOption;
                return ofFactory;
            }

            @Override
            public void bindView(View view, UItem uItem, boolean z) {
                ((StarTierView) view).set(uItem.intValue, uItem.text, uItem.subtext, z);
            }

            @Override
            public boolean contentsEquals(UItem uItem, UItem uItem2) {
                return uItem.intValue == uItem2.intValue && uItem.id == uItem2.id && TextUtils.equals(uItem.subtext, uItem2.subtext);
            }

            @Override
            public StarTierView createView(Context context, int i, int i2, Theme.ResourcesProvider resourcesProvider) {
                return new StarTierView(context, resourcesProvider);
            }

            @Override
            public boolean equals(UItem uItem, UItem uItem2) {
                return uItem.id == uItem2.id;
            }
        }

        public StarTierView(Context context, Theme.ResourcesProvider resourcesProvider) {
            super(context);
            this.animatedStarsCount = new AnimatedFloat(this, 0L, 500L, CubicBezierInterpolator.EASE_OUT_QUINT);
            Drawable mutate = context.getResources().getDrawable(R.drawable.star_small_outline).mutate();
            this.starDrawableOutline = mutate;
            mutate.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_dialogBackground, resourcesProvider), PorterDuff.Mode.SRC_IN));
            this.starDrawable = context.getResources().getDrawable(R.drawable.star_small_inner).mutate();
            setWillNotDraw(false);
            TextView textView = new TextView(context);
            this.textView = textView;
            textView.setTypeface(AndroidUtilities.bold());
            textView.setTextSize(1, 15.0f);
            textView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText, resourcesProvider));
            addView(textView, LayoutHelper.createFrameRelatively(-2.0f, -2.0f, 8388627, 48.0f, 0.0f, 0.0f, 0.0f));
            AnimatedTextView animatedTextView = new AnimatedTextView(context);
            this.textView2 = animatedTextView;
            animatedTextView.setTextSize(AndroidUtilities.dp(15.0f));
            animatedTextView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteGrayText2, resourcesProvider));
            animatedTextView.setGravity(LocaleController.isRTL ? 3 : 5);
            addView(animatedTextView, LayoutHelper.createFrameRelatively(-2.0f, 21.0f, 8388629, 0.0f, 0.0f, 19.0f, 0.0f));
        }

        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);
            float f = this.animatedStarsCount.set(this.starsCount);
            float f2 = LocaleController.isRTL ? -1.0f : 1.0f;
            float dp = AndroidUtilities.dp(24.0f);
            float dp2 = AndroidUtilities.dp(24.0f);
            float dp3 = AndroidUtilities.dp(2.5f);
            float width = LocaleController.isRTL ? (getWidth() - AndroidUtilities.dp(19.0f)) - dp : AndroidUtilities.dp(19.0f);
            int ceil = (int) Math.ceil(f);
            while (true) {
                ceil--;
                if (ceil < 0) {
                    break;
                }
                float clamp = Utilities.clamp(f - ceil, 1.0f, 0.0f);
                float f3 = (((ceil - 1) - (1.0f - clamp)) * dp3 * f2) + width;
                float measuredHeight = (getMeasuredHeight() - dp2) / 2.0f;
                int i = (int) f3;
                int i2 = (int) measuredHeight;
                int i3 = (int) (f3 + dp);
                int i4 = (int) (measuredHeight + dp2);
                this.starDrawableOutline.setBounds(i, i2, i3, i4);
                int i5 = (int) (clamp * 255.0f);
                this.starDrawableOutline.setAlpha(i5);
                this.starDrawableOutline.draw(canvas);
                this.starDrawable.setBounds(i, i2, i3, i4);
                this.starDrawable.setAlpha(i5);
                this.starDrawable.draw(canvas);
            }
            if (this.needDivider) {
                canvas.drawRect(LocaleController.isRTL ? 0.0f : AndroidUtilities.dp(22.0f), getMeasuredHeight() - 1, getMeasuredWidth() - (LocaleController.isRTL ? AndroidUtilities.dp(22.0f) : 0), getMeasuredHeight(), Theme.dividerPaint);
            }
        }

        @Override
        protected void onMeasure(int i, int i2) {
            super.onMeasure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(i), 1073741824), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(48.0f), 1073741824));
        }

        public void set(int i, CharSequence charSequence, CharSequence charSequence2, boolean z) {
            boolean equals = TextUtils.equals(this.textView.getText(), charSequence);
            this.starsCount = i;
            if (!equals) {
                this.animatedStarsCount.set(i, true);
            }
            this.textView.setText(charSequence);
            if (charSequence2 == null) {
                if (this.loading == null) {
                    SpannableString spannableString = new SpannableString("x");
                    this.loading = spannableString;
                    spannableString.setSpan(new LoadingSpan(this.textView2, AndroidUtilities.dp(55.0f)), 0, this.loading.length(), 33);
                }
                charSequence2 = this.loading;
            }
            this.textView2.setText(charSequence2);
            float f = LocaleController.isRTL ? -1.0f : 1.0f;
            if (equals) {
                this.textView.animate().translationX(f * (i - 1) * AndroidUtilities.dp(2.66f)).setDuration(320L).setInterpolator(CubicBezierInterpolator.EASE_OUT_QUINT).start();
            } else {
                this.textView.setTranslationX(f * (i - 1) * AndroidUtilities.dp(2.66f));
            }
            this.needDivider = z;
            invalidate();
        }
    }

    public static class StarsBalanceView extends LinearLayout implements NotificationCenter.NotificationCenterDelegate {
        private final AnimatedTextView amountTextView;
        private ValueAnimator bounceAnimator;
        private final int currentAccount;
        private final TextView headerTextView;
        private long lastBalance;
        private SpannableString loadingString;

        public StarsBalanceView(Context context, int i) {
            super(context);
            this.lastBalance = -1L;
            this.currentAccount = i;
            setOrientation(1);
            setGravity(21);
            TextView textView = new TextView(context);
            this.headerTextView = textView;
            int i2 = Theme.key_windowBackgroundWhiteBlackText;
            textView.setTextColor(Theme.getColor(i2));
            textView.setTextSize(1, 13.0f);
            textView.setText(LocaleController.getString(R.string.StarsBalance));
            textView.setGravity(5);
            textView.setTypeface(AndroidUtilities.bold());
            addView(textView, LayoutHelper.createLinear(-2, -2, 5));
            final Drawable mutate = context.getResources().getDrawable(R.drawable.star_small_inner).mutate();
            AnimatedTextView animatedTextView = new AnimatedTextView(context) {
                @Override
                protected void dispatchDraw(Canvas canvas) {
                    int measuredWidth = (int) ((getMeasuredWidth() - getDrawable().getCurrentWidth()) - AndroidUtilities.dp(20.0f));
                    mutate.setBounds(measuredWidth, (getMeasuredHeight() - AndroidUtilities.dp(17.0f)) / 2, AndroidUtilities.dp(17.0f) + measuredWidth, (getMeasuredHeight() + AndroidUtilities.dp(17.0f)) / 2);
                    mutate.draw(canvas);
                    super.dispatchDraw(canvas);
                }
            };
            this.amountTextView = animatedTextView;
            animatedTextView.adaptWidth = true;
            animatedTextView.getDrawable().setHacks(false, true, true);
            animatedTextView.setTypeface(AndroidUtilities.bold());
            animatedTextView.setTextColor(Theme.getColor(i2));
            animatedTextView.setTextSize(AndroidUtilities.dp(13.0f));
            animatedTextView.setGravity(5);
            animatedTextView.setPadding(AndroidUtilities.dp(19.0f), 0, 0, 0);
            addView(animatedTextView, LayoutHelper.createLinear(-2, 20, 5, 0, -2, 0, 0));
            updateBalance(false);
            setPadding(AndroidUtilities.dp(15.0f), AndroidUtilities.dp(4.0f), AndroidUtilities.dp(15.0f), AndroidUtilities.dp(4.0f));
        }

        public void lambda$bounce$0(ValueAnimator valueAnimator) {
            float floatValue = ((Float) valueAnimator.getAnimatedValue()).floatValue();
            this.amountTextView.setScaleX(floatValue);
            this.amountTextView.setScaleY(floatValue);
        }

        public void bounce() {
            ValueAnimator valueAnimator = this.bounceAnimator;
            if (valueAnimator != null) {
                valueAnimator.cancel();
            }
            ValueAnimator ofFloat = ValueAnimator.ofFloat(0.9f, 1.0f);
            this.bounceAnimator = ofFloat;
            ofFloat.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public final void onAnimationUpdate(ValueAnimator valueAnimator2) {
                    StarsIntroActivity.StarsBalanceView.this.lambda$bounce$0(valueAnimator2);
                }
            });
            this.bounceAnimator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animator) {
                    StarsBalanceView.this.amountTextView.setScaleX(1.0f);
                    StarsBalanceView.this.amountTextView.setScaleY(1.0f);
                }
            });
            this.bounceAnimator.setDuration(320L);
            this.bounceAnimator.setInterpolator(new OvershootInterpolator());
            this.bounceAnimator.start();
        }

        @Override
        public void didReceivedNotification(int i, int i2, Object... objArr) {
            if (i == NotificationCenter.starBalanceUpdated) {
                updateBalance(true);
            }
        }

        @Override
        protected void onAttachedToWindow() {
            super.onAttachedToWindow();
            updateBalance(false);
            NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.starBalanceUpdated);
        }

        @Override
        protected void onDetachedFromWindow() {
            super.onDetachedFromWindow();
            NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.starBalanceUpdated);
        }

        @Override
        protected void onMeasure(int i, int i2) {
            super.onMeasure(i, View.MeasureSpec.makeMeasureSpec(ActionBar.getCurrentActionBarHeight(), 1073741824));
        }

        public void updateBalance(boolean z) {
            StarsController starsController = StarsController.getInstance(this.currentAccount);
            this.amountTextView.cancelAnimation();
            long j = StarsController.getInstance(this.currentAccount).getBalance().amount;
            long j2 = this.lastBalance;
            if (j > j2 && j2 != -1) {
                bounce();
            }
            if (starsController.balanceAvailable()) {
                this.amountTextView.setText(LocaleController.formatNumber(j, ' '));
                this.lastBalance = j;
                return;
            }
            if (this.loadingString == null) {
                SpannableString spannableString = new SpannableString("x");
                this.loadingString = spannableString;
                spannableString.setSpan(new LoadingSpan(this.amountTextView, AndroidUtilities.dp(48.0f)), 0, this.loadingString.length(), 33);
            }
            this.amountTextView.setText(this.loadingString, z);
            this.lastBalance = -1L;
        }
    }

    public static class StarsNeededSheet extends BottomSheetWithRecyclerListView implements NotificationCenter.NotificationCenterDelegate {
        private final int BUTTON_EXPAND;
        private UniversalAdapter adapter;
        private boolean expanded;
        private final FireworksOverlay fireworksOverlay;
        private final FrameLayout footerView;
        private final HeaderView headerView;
        private final long starsNeeded;
        private Runnable whenPurchased;

        public static class HeaderView extends LinearLayout {
            public final StarsBalanceView balanceView;
            public final GLIconTextureView iconView;
            public final StarParticlesView particlesView;
            public final TextView subtitleView;
            public final TextView titleView;
            private final FrameLayout topView;

            public HeaderView(Context context, int i, Theme.ResourcesProvider resourcesProvider) {
                super(context);
                setOrientation(1);
                FrameLayout frameLayout = new FrameLayout(context);
                this.topView = frameLayout;
                frameLayout.setClipChildren(false);
                frameLayout.setClipToPadding(false);
                StarParticlesView makeParticlesView = StarsIntroActivity.makeParticlesView(context, 70, 0);
                this.particlesView = makeParticlesView;
                frameLayout.addView(makeParticlesView, LayoutHelper.createFrame(-1, -1.0f));
                GLIconTextureView gLIconTextureView = new GLIconTextureView(context, 1, 2);
                this.iconView = gLIconTextureView;
                GLIconRenderer gLIconRenderer = gLIconTextureView.mRenderer;
                gLIconRenderer.colorKey1 = Theme.key_starsGradient1;
                gLIconRenderer.colorKey2 = Theme.key_starsGradient2;
                gLIconRenderer.updateColors();
                gLIconTextureView.setStarParticlesView(makeParticlesView);
                frameLayout.addView(gLIconTextureView, LayoutHelper.createFrame(170, 170.0f, 17, 0.0f, 32.0f, 0.0f, 24.0f));
                gLIconTextureView.setPaused(false);
                StarsBalanceView starsBalanceView = new StarsBalanceView(context, i);
                this.balanceView = starsBalanceView;
                ScaleStateListAnimator.apply(starsBalanceView);
                starsBalanceView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public final void onClick(View view) {
                        StarsIntroActivity.StarsNeededSheet.HeaderView.this.lambda$new$0(view);
                    }
                });
                frameLayout.addView(starsBalanceView, LayoutHelper.createFrame(-2, -2.0f, 53, 0.0f, 0.0f, 0.0f, 0.0f));
                addView(frameLayout, LayoutHelper.createFrame(-1, 150.0f));
                TextView textView = new TextView(context);
                this.titleView = textView;
                textView.setTextSize(1, 20.0f);
                textView.setTypeface(AndroidUtilities.bold());
                int i2 = Theme.key_dialogTextBlack;
                textView.setTextColor(Theme.getColor(i2, resourcesProvider));
                textView.setGravity(17);
                addView(textView, LayoutHelper.createLinear(-2, -2, 1, 0, 2, 0, 0));
                TextView textView2 = new TextView(context);
                this.subtitleView = textView2;
                textView2.setTextSize(1, 14.0f);
                textView2.setTextColor(Theme.getColor(i2, resourcesProvider));
                textView2.setGravity(17);
                addView(textView2, LayoutHelper.createLinear(-2, -2, 1, 0, 9, 0, 18));
            }

            public void lambda$new$0(View view) {
                BaseFragment lastFragment;
                if (this.balanceView.lastBalance > 0 && (lastFragment = LaunchActivity.getLastFragment()) != null) {
                    BaseFragment.BottomSheetParams bottomSheetParams = new BaseFragment.BottomSheetParams();
                    bottomSheetParams.transitionFromLeft = true;
                    bottomSheetParams.allowNestedScroll = false;
                    lastFragment.showAsSheet(new StarsIntroActivity(), bottomSheetParams);
                }
            }
        }

        public StarsNeededSheet(Context context, Theme.ResourcesProvider resourcesProvider, long j, int i, String str, Runnable runnable) {
            super(context, null, false, false, false, resourcesProvider);
            String str2;
            String str3;
            this.BUTTON_EXPAND = -1;
            this.topPadding = 0.2f;
            this.whenPurchased = runnable;
            fixNavigationBar();
            RecyclerListView recyclerListView = this.recyclerListView;
            int i2 = this.backgroundPaddingLeft;
            recyclerListView.setPadding(i2, 0, i2, 0);
            this.recyclerListView.setOnItemClickListener(new RecyclerListView.OnItemClickListener() {
                @Override
                public final void onItemClick(View view, int i3) {
                    StarsIntroActivity.StarsNeededSheet.this.lambda$new$0(view, i3);
                }
            });
            DefaultItemAnimator defaultItemAnimator = new DefaultItemAnimator();
            defaultItemAnimator.setSupportsChangeAnimations(false);
            defaultItemAnimator.setDelayAnimations(false);
            defaultItemAnimator.setInterpolator(CubicBezierInterpolator.EASE_OUT_QUINT);
            defaultItemAnimator.setDurations(350L);
            this.recyclerListView.setItemAnimator(defaultItemAnimator);
            setBackgroundColor(Theme.getColor(Theme.key_dialogBackgroundGray, resourcesProvider));
            this.starsNeeded = j;
            HeaderView headerView = new HeaderView(context, this.currentAccount, resourcesProvider);
            this.headerView = headerView;
            headerView.titleView.setText(LocaleController.formatPluralString("StarsNeededTitle", (int) Math.max(0L, j - StarsController.getInstance(this.currentAccount).getBalance().amount), new Object[0]));
            if (i == 1) {
                str2 = "StarsNeededTextBuySubscription";
            } else {
                if (i != 2) {
                    if (i == 7) {
                        str2 = "StarsNeededTextKeepBotSubscription";
                    } else if (i == 8) {
                        str2 = "StarsNeededTextKeepBizSubscription";
                    } else if (i != 3) {
                        if (i == 4) {
                            str2 = "StarsNeededTextLink";
                            if (str == null) {
                                str3 = "StarsNeededTextLink";
                            } else {
                                str3 = "StarsNeededTextLink_" + str.toLowerCase();
                            }
                            if (LocaleController.nullable(LocaleController.getString(str3)) != null) {
                                str2 = str3;
                            }
                        } else {
                            str2 = i == 5 ? "StarsNeededTextReactions" : i == 6 ? "StarsNeededTextGift" : i == 9 ? "StarsNeededBizText" : "StarsNeededText";
                        }
                    }
                }
                str2 = "StarsNeededTextKeepSubscription";
            }
            if (TextUtils.isEmpty(str2)) {
                headerView.subtitleView.setText("");
            } else {
                String nullable = LocaleController.nullable(LocaleController.formatString(str2, LocaleController.getStringResId(str2), str));
                headerView.subtitleView.setText(AndroidUtilities.replaceTags(nullable == null ? LocaleController.getString(str2) : nullable));
                TextView textView = headerView.subtitleView;
                textView.setMaxWidth(HintView2.cutInFancyHalf(textView.getText(), headerView.subtitleView.getPaint()));
            }
            this.actionBar.setTitle(getTitle());
            FrameLayout frameLayout = new FrameLayout(context);
            this.footerView = frameLayout;
            LinkSpanDrawable.LinksTextView linksTextView = new LinkSpanDrawable.LinksTextView(context, resourcesProvider);
            frameLayout.setPadding(0, AndroidUtilities.dp(11.0f), 0, AndroidUtilities.dp(11.0f));
            linksTextView.setTextSize(1, 12.0f);
            linksTextView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteGrayText4, resourcesProvider));
            linksTextView.setLinkTextColor(Theme.getColor(Theme.key_chat_messageLinkIn, resourcesProvider));
            linksTextView.setText(AndroidUtilities.replaceSingleTag(LocaleController.getString(R.string.StarsTOS), new Runnable() {
                @Override
                public final void run() {
                    StarsIntroActivity.StarsNeededSheet.this.lambda$new$1();
                }
            }));
            linksTextView.setGravity(17);
            linksTextView.setMaxWidth(HintView2.cutInFancyHalf(linksTextView.getText(), linksTextView.getPaint()));
            frameLayout.addView(linksTextView, LayoutHelper.createFrame(-2, -1, 17));
            frameLayout.setBackgroundColor(Theme.getColor(Theme.key_dialogBackground, resourcesProvider));
            FireworksOverlay fireworksOverlay = new FireworksOverlay(getContext());
            this.fireworksOverlay = fireworksOverlay;
            this.containerView.addView(fireworksOverlay, LayoutHelper.createFrame(-1, -1.0f));
            UniversalAdapter universalAdapter = this.adapter;
            if (universalAdapter != null) {
                universalAdapter.update(false);
            }
        }

        public void lambda$new$0(View view, int i) {
            UItem item;
            UniversalAdapter universalAdapter = this.adapter;
            if (universalAdapter == null || (item = universalAdapter.getItem(i - 1)) == null) {
                return;
            }
            onItemClick(item, this.adapter);
        }

        public void lambda$new$1() {
            Browser.openUrl(getContext(), LocaleController.getString(R.string.StarsTOSLink));
        }

        public void lambda$onItemClick$2(UItem uItem, Boolean bool, String str) {
            if (getContext() == null) {
                return;
            }
            if (bool.booleanValue()) {
                BulletinFactory.of((FrameLayout) this.containerView, this.resourcesProvider).createSimpleBulletin(R.raw.stars_topup, LocaleController.getString(R.string.StarsAcquired), AndroidUtilities.replaceTags(LocaleController.formatPluralString("StarsAcquiredInfo", (int) uItem.longValue, new Object[0]))).show();
                this.fireworksOverlay.start(true);
                StarsController.getInstance(this.currentAccount).invalidateTransactions(true);
            } else if (str != null) {
                BulletinFactory.of((FrameLayout) this.containerView, this.resourcesProvider).createSimpleBulletin(R.raw.error, LocaleController.formatString(R.string.UnknownErrorCode, str)).show();
            }
        }

        @Override
        protected RecyclerListView.SelectionAdapter createAdapter(RecyclerListView recyclerListView) {
            UniversalAdapter universalAdapter = new UniversalAdapter(this.recyclerListView, getContext(), this.currentAccount, 0, true, new Utilities.Callback2() {
                @Override
                public final void run(Object obj, Object obj2) {
                    StarsIntroActivity.StarsNeededSheet.this.fillItems((ArrayList) obj, (UniversalAdapter) obj2);
                }
            }, this.resourcesProvider);
            this.adapter = universalAdapter;
            return universalAdapter;
        }

        @Override
        public void didReceivedNotification(int i, int i2, Object... objArr) {
            Runnable runnable;
            if (i == NotificationCenter.starOptionsLoaded || i == NotificationCenter.starBalanceUpdated) {
                UniversalAdapter universalAdapter = this.adapter;
                if (universalAdapter != null) {
                    universalAdapter.update(true);
                }
                long j = StarsController.getInstance(this.currentAccount).getBalance().amount;
                this.headerView.titleView.setText(LocaleController.formatPluralStringComma("StarsNeededTitle", (int) (this.starsNeeded - j)));
                ActionBar actionBar = this.actionBar;
                if (actionBar != null) {
                    actionBar.setTitle(getTitle());
                }
                if (j < this.starsNeeded || (runnable = this.whenPurchased) == null) {
                    return;
                }
                runnable.run();
                this.whenPurchased = null;
                dismiss();
            }
        }

        @Override
        public void dismiss() {
            super.dismiss();
            HeaderView headerView = this.headerView;
            if (headerView != null) {
                headerView.iconView.setPaused(true);
            }
        }

        @Override
        public void dismissInternal() {
            super.dismissInternal();
            NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.starOptionsLoaded);
            NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.starBalanceUpdated);
        }

        public void fillItems(java.util.ArrayList r14, org.telegram.ui.Components.UniversalAdapter r15) {
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Stars.StarsIntroActivity.StarsNeededSheet.fillItems(java.util.ArrayList, org.telegram.ui.Components.UniversalAdapter):void");
        }

        @Override
        protected CharSequence getTitle() {
            HeaderView headerView = this.headerView;
            if (headerView == null) {
                return null;
            }
            return headerView.titleView.getText();
        }

        public void onItemClick(final UItem uItem, UniversalAdapter universalAdapter) {
            if (uItem.id == -1) {
                this.expanded = !this.expanded;
                universalAdapter.update(true);
            } else if (uItem.instanceOf(StarTierView.Factory.class) && (uItem.object instanceof TL_stars.TL_starsTopupOption)) {
                Activity findActivity = AndroidUtilities.findActivity(getContext());
                if (findActivity == null) {
                    findActivity = LaunchActivity.instance;
                }
                if (findActivity == null) {
                    return;
                }
                StarsController.getInstance(this.currentAccount).buy(findActivity, (TL_stars.TL_starsTopupOption) uItem.object, new Utilities.Callback2() {
                    @Override
                    public final void run(Object obj, Object obj2) {
                        StarsIntroActivity.StarsNeededSheet.this.lambda$onItemClick$2(uItem, (Boolean) obj, (String) obj2);
                    }
                });
            }
        }

        @Override
        public void show() {
            if (StarsController.getInstance(this.currentAccount).getBalance().amount >= this.starsNeeded) {
                Runnable runnable = this.whenPurchased;
                if (runnable != null) {
                    runnable.run();
                    this.whenPurchased = null;
                    return;
                }
                return;
            }
            BaseFragment lastFragment = LaunchActivity.getLastFragment();
            if (lastFragment instanceof ChatActivity) {
                ChatActivity chatActivity = (ChatActivity) lastFragment;
                if (chatActivity.isKeyboardVisible() && chatActivity.getChatActivityEnterView() != null) {
                    chatActivity.getChatActivityEnterView().closeKeyboard();
                }
            }
            super.show();
            NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.starOptionsLoaded);
            NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.starBalanceUpdated);
        }
    }

    public static class StarsOptionsSheet extends BottomSheetWithRecyclerListView implements NotificationCenter.NotificationCenterDelegate {
        private final int BUTTON_EXPAND;
        private UniversalAdapter adapter;
        private boolean expanded;
        private final FireworksOverlay fireworksOverlay;
        private final FrameLayout footerView;

        public StarsOptionsSheet(Context context, Theme.ResourcesProvider resourcesProvider) {
            super(context, null, false, false, false, resourcesProvider);
            this.BUTTON_EXPAND = -1;
            RecyclerListView recyclerListView = this.recyclerListView;
            int i = this.backgroundPaddingLeft;
            recyclerListView.setPadding(i, 0, i, 0);
            this.recyclerListView.setOnItemClickListener(new RecyclerListView.OnItemClickListener() {
                @Override
                public final void onItemClick(View view, int i2) {
                    StarsIntroActivity.StarsOptionsSheet.this.lambda$new$0(view, i2);
                }
            });
            DefaultItemAnimator defaultItemAnimator = new DefaultItemAnimator();
            defaultItemAnimator.setSupportsChangeAnimations(false);
            defaultItemAnimator.setDelayAnimations(false);
            defaultItemAnimator.setInterpolator(CubicBezierInterpolator.EASE_OUT_QUINT);
            defaultItemAnimator.setDurations(350L);
            this.recyclerListView.setItemAnimator(defaultItemAnimator);
            int i2 = Theme.key_windowBackgroundWhite;
            setBackgroundColor(Theme.getColor(i2, resourcesProvider));
            fixNavigationBar(Theme.getColor(i2, resourcesProvider));
            this.actionBar.setTitle(getTitle());
            FrameLayout frameLayout = new FrameLayout(context);
            this.footerView = frameLayout;
            LinkSpanDrawable.LinksTextView linksTextView = new LinkSpanDrawable.LinksTextView(context, resourcesProvider);
            frameLayout.setPadding(0, AndroidUtilities.dp(11.0f), 0, AndroidUtilities.dp(11.0f));
            linksTextView.setTextSize(1, 12.0f);
            linksTextView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteGrayText4, resourcesProvider));
            linksTextView.setLinkTextColor(Theme.getColor(Theme.key_chat_messageLinkIn, resourcesProvider));
            linksTextView.setText(AndroidUtilities.replaceSingleTag(LocaleController.getString(R.string.StarsTOS), new Runnable() {
                @Override
                public final void run() {
                    StarsIntroActivity.StarsOptionsSheet.this.lambda$new$1();
                }
            }));
            linksTextView.setGravity(17);
            linksTextView.setMaxWidth(HintView2.cutInFancyHalf(linksTextView.getText(), linksTextView.getPaint()));
            frameLayout.addView(linksTextView, LayoutHelper.createFrame(-2, -1, 17));
            frameLayout.setBackgroundColor(Theme.getColor(Theme.key_dialogBackground, resourcesProvider));
            FireworksOverlay fireworksOverlay = new FireworksOverlay(getContext());
            this.fireworksOverlay = fireworksOverlay;
            this.containerView.addView(fireworksOverlay, LayoutHelper.createFrame(-1, -1.0f));
            UniversalAdapter universalAdapter = this.adapter;
            if (universalAdapter != null) {
                universalAdapter.update(false);
            }
        }

        public void lambda$new$0(View view, int i) {
            UItem item;
            UniversalAdapter universalAdapter = this.adapter;
            if (universalAdapter == null || (item = universalAdapter.getItem(i - 1)) == null) {
                return;
            }
            onItemClick(item, this.adapter);
        }

        public void lambda$new$1() {
            Browser.openUrl(getContext(), LocaleController.getString(R.string.StarsTOSLink));
        }

        public void lambda$onItemClick$2(UItem uItem, Boolean bool, String str) {
            if (getContext() == null) {
                return;
            }
            dismiss();
            StarsController.getInstance(this.currentAccount).invalidateTransactions(true);
            BaseFragment safeLastFragment = LaunchActivity.getSafeLastFragment();
            if (safeLastFragment == null) {
                return;
            }
            if (!bool.booleanValue()) {
                if (str != null) {
                    BulletinFactory.of(safeLastFragment).createSimpleBulletin(R.raw.error, LocaleController.formatString(R.string.UnknownErrorCode, str)).show();
                }
            } else {
                BulletinFactory.of(safeLastFragment).createSimpleBulletin(R.raw.stars_topup, LocaleController.getString(R.string.StarsAcquired), AndroidUtilities.replaceTags(LocaleController.formatPluralString("StarsAcquiredInfo", (int) uItem.longValue, new Object[0]))).show();
                LaunchActivity launchActivity = LaunchActivity.instance;
                if (launchActivity != null) {
                    launchActivity.getFireworksOverlay().start(true);
                }
            }
        }

        @Override
        protected RecyclerListView.SelectionAdapter createAdapter(RecyclerListView recyclerListView) {
            UniversalAdapter universalAdapter = new UniversalAdapter(this.recyclerListView, getContext(), this.currentAccount, 0, true, new Utilities.Callback2() {
                @Override
                public final void run(Object obj, Object obj2) {
                    StarsIntroActivity.StarsOptionsSheet.this.fillItems((ArrayList) obj, (UniversalAdapter) obj2);
                }
            }, this.resourcesProvider);
            this.adapter = universalAdapter;
            universalAdapter.setApplyBackground(false);
            return this.adapter;
        }

        @Override
        public void didReceivedNotification(int i, int i2, Object... objArr) {
            UniversalAdapter universalAdapter;
            if ((i == NotificationCenter.starOptionsLoaded || i == NotificationCenter.starBalanceUpdated) && (universalAdapter = this.adapter) != null) {
                universalAdapter.update(true);
            }
        }

        @Override
        public void dismissInternal() {
            super.dismissInternal();
            NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.starOptionsLoaded);
            NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.starBalanceUpdated);
        }

        public void fillItems(ArrayList arrayList, UniversalAdapter universalAdapter) {
            UItem asFlicker;
            arrayList.add(UItem.asHeader(LocaleController.getString(R.string.TelegramStarsChoose)));
            ArrayList options = StarsController.getInstance(this.currentAccount).getOptions();
            if (options != null && !options.isEmpty()) {
                int i = 0;
                int i2 = 1;
                for (int i3 = 0; i3 < options.size(); i3++) {
                    TL_stars.TL_starsTopupOption tL_starsTopupOption = (TL_stars.TL_starsTopupOption) options.get(i3);
                    if (!tL_starsTopupOption.extended || this.expanded) {
                        arrayList.add(StarTierView.Factory.asStarTier(i3, i2, tL_starsTopupOption));
                        i2++;
                    } else {
                        i++;
                    }
                }
                boolean z = this.expanded;
                if (!z && i > 0) {
                    asFlicker = ExpandView.Factory.asExpand(-1, LocaleController.getString(z ? R.string.NotifyLessOptions : R.string.NotifyMoreOptions), !this.expanded).accent();
                }
                arrayList.add(UItem.asCustom(this.footerView));
            }
            arrayList.add(UItem.asFlicker(31));
            arrayList.add(UItem.asFlicker(31));
            arrayList.add(UItem.asFlicker(31));
            arrayList.add(UItem.asFlicker(31));
            asFlicker = UItem.asFlicker(31);
            arrayList.add(asFlicker);
            arrayList.add(UItem.asCustom(this.footerView));
        }

        @Override
        protected CharSequence getTitle() {
            return LocaleController.getString(R.string.StarsBuy);
        }

        public void onItemClick(final UItem uItem, UniversalAdapter universalAdapter) {
            if (uItem.id == -1) {
                this.expanded = !this.expanded;
                universalAdapter.update(true);
                this.recyclerListView.smoothScrollBy(0, AndroidUtilities.dp(300.0f));
            } else if (uItem.instanceOf(StarTierView.Factory.class) && (uItem.object instanceof TL_stars.TL_starsTopupOption)) {
                Activity findActivity = AndroidUtilities.findActivity(getContext());
                if (findActivity == null) {
                    findActivity = LaunchActivity.instance;
                }
                if (findActivity == null) {
                    return;
                }
                StarsController.getInstance(this.currentAccount).buy(findActivity, (TL_stars.TL_starsTopupOption) uItem.object, new Utilities.Callback2() {
                    @Override
                    public final void run(Object obj, Object obj2) {
                        StarsIntroActivity.StarsOptionsSheet.this.lambda$onItemClick$2(uItem, (Boolean) obj, (String) obj2);
                    }
                });
            }
        }

        @Override
        public void show() {
            long j = StarsController.getInstance(this.currentAccount).getBalance().amount;
            BaseFragment lastFragment = LaunchActivity.getLastFragment();
            if (lastFragment instanceof ChatActivity) {
                ChatActivity chatActivity = (ChatActivity) lastFragment;
                if (chatActivity.isKeyboardVisible() && chatActivity.getChatActivityEnterView() != null) {
                    chatActivity.getChatActivityEnterView().closeKeyboard();
                }
            }
            super.show();
            NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.starOptionsLoaded);
            NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.starBalanceUpdated);
        }
    }

    public static class StarsSubscriptionView extends LinearLayout {
        private final int currentAccount;
        public final BackupImageView imageView;
        private boolean needDivider;
        public final LinearLayout priceLayout;
        public final TextView priceSubtitleView;
        public final TextView priceTitleView;
        public final TextView productView;
        private final Theme.ResourcesProvider resourcesProvider;
        public final TextView subtitleView;
        public final LinearLayout textLayout;
        private boolean threeLines;
        public final SimpleTextView titleView;

        public static class Factory extends UItem.UItemFactory {
            static {
                UItem.UItemFactory.setup(new Factory());
            }

            public static UItem asSubscription(TL_stars.StarsSubscription starsSubscription) {
                UItem ofFactory = UItem.ofFactory(Factory.class);
                ofFactory.object = starsSubscription;
                return ofFactory;
            }

            @Override
            public void bindView(View view, UItem uItem, boolean z) {
                ((StarsSubscriptionView) view).set((TL_stars.StarsSubscription) uItem.object, z);
            }

            @Override
            public StarsSubscriptionView createView(Context context, int i, int i2, Theme.ResourcesProvider resourcesProvider) {
                StarsSubscriptionView starsSubscriptionView = (StarsSubscriptionView) getCached();
                return starsSubscriptionView != null ? starsSubscriptionView : new StarsSubscriptionView(context, i, resourcesProvider);
            }

            @Override
            public boolean equals(UItem uItem, UItem uItem2) {
                if (uItem == null && uItem2 == null) {
                    return true;
                }
                if (uItem != null && uItem2 != null) {
                    Object obj = uItem.object;
                    if (obj instanceof TL_stars.StarsSubscription) {
                        Object obj2 = uItem2.object;
                        if (obj2 instanceof TL_stars.StarsSubscription) {
                            return TextUtils.equals(((TL_stars.StarsSubscription) obj).id, ((TL_stars.StarsSubscription) obj2).id);
                        }
                    }
                }
                return false;
            }
        }

        public StarsSubscriptionView(Context context, int i, Theme.ResourcesProvider resourcesProvider) {
            super(context);
            this.currentAccount = i;
            this.resourcesProvider = resourcesProvider;
            setOrientation(0);
            BackupImageView backupImageView = new BackupImageView(context);
            this.imageView = backupImageView;
            backupImageView.setRoundRadius(AndroidUtilities.dp(46.0f));
            addView(backupImageView, LayoutHelper.createLinear(46, 46, 0.0f, 19, 13, 0, 13, 0));
            LinearLayout linearLayout = new LinearLayout(context);
            this.textLayout = linearLayout;
            linearLayout.setOrientation(1);
            addView(linearLayout, LayoutHelper.createLinear(-1, -2, 1.0f, 16, 0, 0, 0, 0));
            SimpleTextView simpleTextView = new SimpleTextView(context);
            this.titleView = simpleTextView;
            int i2 = Theme.key_windowBackgroundWhiteBlackText;
            simpleTextView.setTextColor(Theme.getColor(i2, resourcesProvider));
            simpleTextView.setTextSize(16);
            simpleTextView.setTypeface(AndroidUtilities.bold());
            NotificationCenter.listenEmojiLoading(simpleTextView);
            linearLayout.addView(simpleTextView, LayoutHelper.createLinear(-1, -2, 0.0f, 0.0f, 0.0f, 2.0f));
            TextView textView = new TextView(context);
            this.productView = textView;
            textView.setTextColor(Theme.getColor(i2, resourcesProvider));
            textView.setTextSize(1, 13.0f);
            textView.setVisibility(8);
            linearLayout.addView(textView, LayoutHelper.createLinear(-1, -2, 0.0f, 0.0f, 0.0f, 1.0f));
            TextView textView2 = new TextView(context);
            this.subtitleView = textView2;
            int i3 = Theme.key_windowBackgroundWhiteGrayText2;
            textView2.setTextColor(Theme.getColor(i3, resourcesProvider));
            textView2.setTextSize(1, 14.0f);
            linearLayout.addView(textView2, LayoutHelper.createLinear(-1, -2, 0.0f, 0.0f, 0.0f, 0.0f));
            LinearLayout linearLayout2 = new LinearLayout(context);
            this.priceLayout = linearLayout2;
            linearLayout2.setOrientation(1);
            addView(linearLayout2, LayoutHelper.createLinear(-2, -2, 0.0f, 16, 0, 0, 18, 0));
            TextView textView3 = new TextView(context);
            this.priceTitleView = textView3;
            textView3.setTextColor(Theme.getColor(i2, resourcesProvider));
            textView3.setTextSize(1, 16.0f);
            textView3.setTypeface(AndroidUtilities.bold());
            textView3.setGravity(5);
            linearLayout2.addView(textView3, LayoutHelper.createLinear(-1, -2, 5, 0, 0, 0, 1));
            TextView textView4 = new TextView(context);
            this.priceSubtitleView = textView4;
            textView4.setTextColor(Theme.getColor(i3, resourcesProvider));
            textView4.setTextSize(1, 13.0f);
            textView4.setGravity(5);
            linearLayout2.addView(textView4, LayoutHelper.createLinear(-1, -2, 5, 0, 0, 0, 0));
        }

        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);
            if (this.needDivider) {
                canvas.drawRect(AndroidUtilities.dp(72.0f), getMeasuredHeight() - 1, getMeasuredWidth(), getMeasuredHeight(), Theme.dividerPaint);
            }
        }

        @Override
        protected void onMeasure(int i, int i2) {
            super.onMeasure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(i), 1073741824), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(this.threeLines ? 68.0f : 58.0f), 1073741824));
        }

        public void set(TL_stars.StarsSubscription starsSubscription, boolean z) {
            boolean z2;
            String str;
            TextView textView;
            String str2;
            int i;
            long peerDialogId = DialogObject.getPeerDialogId(starsSubscription.peer);
            this.threeLines = !TextUtils.isEmpty(starsSubscription.title);
            MessagesController messagesController = MessagesController.getInstance(this.currentAccount);
            if (peerDialogId < 0) {
                TLRPC.Chat chat = messagesController.getChat(Long.valueOf(-peerDialogId));
                AvatarDrawable avatarDrawable = new AvatarDrawable();
                avatarDrawable.setInfo(chat);
                this.imageView.setForUserOrChat(chat, avatarDrawable);
                str = chat != null ? chat.title : null;
                z2 = false;
            } else {
                TLRPC.User user = messagesController.getUser(Long.valueOf(peerDialogId));
                AvatarDrawable avatarDrawable2 = new AvatarDrawable();
                avatarDrawable2.setInfo(user);
                this.imageView.setForUserOrChat(user, avatarDrawable2);
                String userName = UserObject.getUserName(user);
                z2 = !UserObject.isBot(user);
                str = userName;
            }
            long currentTime = ConnectionsManager.getInstance(this.currentAccount).getCurrentTime();
            SimpleTextView simpleTextView = this.titleView;
            simpleTextView.setText(Emoji.replaceEmoji(str, simpleTextView.getPaint().getFontMetricsInt(), false));
            if (TextUtils.isEmpty(starsSubscription.title)) {
                this.productView.setVisibility(8);
            } else {
                this.productView.setVisibility(0);
                SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder();
                if (starsSubscription.photo != null) {
                    ImageReceiverSpan imageReceiverSpan = new ImageReceiverSpan(this.productView, this.currentAccount, 14.0f);
                    imageReceiverSpan.setRoundRadius(4.0f);
                    imageReceiverSpan.enableShadow(false);
                    SpannableString spannableString = new SpannableString("x");
                    spannableString.setSpan(imageReceiverSpan, 0, 1, 33);
                    imageReceiverSpan.imageReceiver.setImage(ImageLocation.getForWebFile(WebFile.createWithWebDocument(starsSubscription.photo)), "14_14", null, null, 0, 0);
                    spannableStringBuilder.append((CharSequence) spannableString).append((CharSequence) " ");
                }
                spannableStringBuilder.append(Emoji.replaceEmoji(starsSubscription.title, this.titleView.getPaint().getFontMetricsInt(), false));
                this.productView.setText(spannableStringBuilder);
            }
            this.subtitleView.setTextSize(1, this.threeLines ? 13.0f : 14.0f);
            if (starsSubscription.canceled || starsSubscription.bot_canceled) {
                TextView textView2 = this.subtitleView;
                long j = starsSubscription.until_date;
                textView2.setText(LocaleController.formatString(j < currentTime ? R.string.StarsSubscriptionExpired : R.string.StarsSubscriptionExpires, LocaleController.formatDateChat(j)));
                this.priceTitleView.setVisibility(8);
                this.priceSubtitleView.setTextColor(Theme.getColor(Theme.key_color_red, this.resourcesProvider));
                this.priceSubtitleView.setText(LocaleController.getString(starsSubscription.bot_canceled ? z2 ? R.string.StarsSubscriptionStatusBizCancelled : R.string.StarsSubscriptionStatusBotCancelled : R.string.StarsSubscriptionStatusCancelled));
            } else {
                long j2 = starsSubscription.until_date;
                if (j2 < currentTime) {
                    this.subtitleView.setText(LocaleController.formatString(R.string.StarsSubscriptionExpired, LocaleController.formatDateChat(j2)));
                    this.priceTitleView.setVisibility(8);
                    this.priceSubtitleView.setTextColor(Theme.getColor(Theme.key_color_red, this.resourcesProvider));
                    textView = this.priceSubtitleView;
                    i = R.string.StarsSubscriptionStatusExpired;
                } else {
                    this.subtitleView.setText(LocaleController.formatString(R.string.StarsSubscriptionRenews, LocaleController.formatDateChat(j2)));
                    this.priceTitleView.setVisibility(0);
                    this.priceTitleView.setText(StarsIntroActivity.replaceStarsWithPlain("⭐️ " + Long.toString(starsSubscription.pricing.amount), 0.8f));
                    this.priceSubtitleView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteGrayText2, this.resourcesProvider));
                    int i2 = starsSubscription.pricing.period;
                    if (i2 == 2592000) {
                        textView = this.priceSubtitleView;
                        i = R.string.StarsParticipantSubscriptionPerMonth;
                    } else {
                        if (i2 == 60) {
                            textView = this.priceSubtitleView;
                            str2 = "per minute";
                        } else if (i2 == 300) {
                            textView = this.priceSubtitleView;
                            str2 = "per 5 minutes";
                        }
                        textView.setText(str2);
                    }
                }
                str2 = LocaleController.getString(i);
                textView.setText(str2);
            }
            this.needDivider = z;
            setWillNotDraw(!z);
        }
    }

    public static class StarsTransactionView extends LinearLayout {
        public static HashMap cachedPlatformDrawables;
        private final TextView amountTextView;
        private final AvatarDrawable avatarDrawable;
        private Runnable cancelCurrentGift;
        private final int currentAccount;
        private final TextView dateTextView;
        private final LinearLayout.LayoutParams dateTextViewParams;
        private final BackupImageView imageView;
        private final BackupImageView imageView2;
        private final FrameLayout imageViewContainer;
        private int imageViewCount;
        private boolean needDivider;
        private final SpannableString star;
        private final TextView subtitleTextView;
        private final LinearLayout textLayout;
        private boolean threeLines;
        private final TextView titleTextView;
        private final LinearLayout.LayoutParams titleTextViewParams;

        public static class Factory extends UItem.UItemFactory {
            static {
                UItem.UItemFactory.setup(new Factory());
            }

            public static UItem asTransaction(TL_stars.StarsTransaction starsTransaction, boolean z) {
                UItem ofFactory = UItem.ofFactory(Factory.class);
                ofFactory.object = starsTransaction;
                ofFactory.accent = z;
                return ofFactory;
            }

            @Override
            public void bindView(View view, UItem uItem, boolean z) {
                ((StarsTransactionView) view).set((TL_stars.StarsTransaction) uItem.object, uItem.accent, z);
            }

            @Override
            public StarsTransactionView createView(Context context, int i, int i2, Theme.ResourcesProvider resourcesProvider) {
                StarsTransactionView starsTransactionView = (StarsTransactionView) getCached();
                return starsTransactionView != null ? starsTransactionView : new StarsTransactionView(context, i, resourcesProvider);
            }
        }

        public StarsTransactionView(Context context, int i, final Theme.ResourcesProvider resourcesProvider) {
            super(context);
            this.imageViewCount = 1;
            this.currentAccount = i;
            setOrientation(0);
            FrameLayout frameLayout = new FrameLayout(context) {
                private final Paint backgroundPaint = new Paint(1);

                @Override
                protected boolean drawChild(Canvas canvas, View view, long j) {
                    if (StarsTransactionView.this.imageViewCount > 1) {
                        this.backgroundPaint.setColor(Theme.getColor(Theme.key_windowBackgroundWhite, resourcesProvider));
                        RectF rectF = AndroidUtilities.rectTmp;
                        rectF.set(view.getX(), view.getY(), view.getX() + view.getWidth(), view.getY() + view.getHeight());
                        rectF.inset(-AndroidUtilities.dp(1.66f), -AndroidUtilities.dp(1.66f));
                        canvas.drawRoundRect(rectF, AndroidUtilities.dp(13.0f), AndroidUtilities.dp(13.0f), this.backgroundPaint);
                    }
                    return super.drawChild(canvas, view, j);
                }
            };
            this.imageViewContainer = frameLayout;
            addView(frameLayout, LayoutHelper.createLinear(72, -1, 0.0f, 115));
            BackupImageView backupImageView = new BackupImageView(context);
            this.imageView2 = backupImageView;
            backupImageView.setRoundRadius(AndroidUtilities.dp(46.0f));
            frameLayout.addView(backupImageView, LayoutHelper.createFrame(46, 46.0f, 16, 13.0f, 0.0f, 13.0f, 0.0f));
            this.avatarDrawable = new AvatarDrawable();
            BackupImageView backupImageView2 = new BackupImageView(context);
            this.imageView = backupImageView2;
            backupImageView2.setRoundRadius(AndroidUtilities.dp(46.0f));
            frameLayout.addView(backupImageView2, LayoutHelper.createFrame(46, 46.0f, 16, 13.0f, 0.0f, 13.0f, 0.0f));
            LinearLayout linearLayout = new LinearLayout(context);
            this.textLayout = linearLayout;
            linearLayout.setOrientation(1);
            linearLayout.setGravity(19);
            addView(linearLayout, LayoutHelper.createLinear(-2, -1, 1.0f, 119));
            TextView textView = new TextView(context);
            this.titleTextView = textView;
            textView.setTypeface(AndroidUtilities.bold());
            int i2 = Theme.key_windowBackgroundWhiteBlackText;
            textView.setTextColor(Theme.getColor(i2, resourcesProvider));
            textView.setTextSize(1, 16.0f);
            TextUtils.TruncateAt truncateAt = TextUtils.TruncateAt.END;
            textView.setEllipsize(truncateAt);
            textView.setSingleLine(true);
            LinearLayout.LayoutParams createLinear = LayoutHelper.createLinear(-1, -2, 0.0f, 0.0f, 0.0f, 4.33f);
            this.titleTextViewParams = createLinear;
            linearLayout.addView(textView, createLinear);
            TextView textView2 = new TextView(context);
            this.subtitleTextView = textView2;
            textView2.setTextColor(Theme.getColor(i2, resourcesProvider));
            textView2.setTextSize(1, 13.0f);
            textView2.setEllipsize(truncateAt);
            textView2.setSingleLine(true);
            linearLayout.addView(textView2, LayoutHelper.createLinear(-1, -2, 0.0f, 0.0f, 0.0f, 0.33f));
            TextView textView3 = new TextView(context);
            this.dateTextView = textView3;
            textView3.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteGrayText2, resourcesProvider));
            textView3.setTextSize(1, 14.0f);
            textView3.setEllipsize(truncateAt);
            textView3.setSingleLine(true);
            LinearLayout.LayoutParams createLinear2 = LayoutHelper.createLinear(-1, -2);
            this.dateTextViewParams = createLinear2;
            linearLayout.addView(textView3, createLinear2);
            TextView textView4 = new TextView(context);
            this.amountTextView = textView4;
            textView4.setTypeface(AndroidUtilities.bold());
            textView4.setTextSize(1, 15.3f);
            textView4.setGravity(5);
            addView(textView4, LayoutHelper.createLinear(-2, -2, 0.0f, 21, 8, 0, 20, 0));
            SpannableString spannableString = new SpannableString("⭐️");
            this.star = spannableString;
            Drawable mutate = context.getResources().getDrawable(R.drawable.star_small_inner).mutate();
            mutate.setBounds(0, 0, AndroidUtilities.dp(21.0f), AndroidUtilities.dp(21.0f));
            spannableString.setSpan(new ImageSpan(mutate), 0, spannableString.length(), 33);
        }

        public static CombinedDrawable getPlatformDrawable(String str) {
            return getPlatformDrawable(str, 44);
        }

        public static CombinedDrawable getPlatformDrawable(String str, int i) {
            if (i != 44) {
                return SessionCell.createDrawable(i, str);
            }
            if (cachedPlatformDrawables == null) {
                cachedPlatformDrawables = new HashMap();
            }
            CombinedDrawable combinedDrawable = (CombinedDrawable) cachedPlatformDrawables.get(str);
            if (combinedDrawable != null) {
                return combinedDrawable;
            }
            HashMap hashMap = cachedPlatformDrawables;
            CombinedDrawable createDrawable = SessionCell.createDrawable(44, str);
            hashMap.put(str, createDrawable);
            return createDrawable;
        }

        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);
            if (this.needDivider) {
                canvas.drawRect(LocaleController.isRTL ? 0.0f : AndroidUtilities.dp(72.0f), getMeasuredHeight() - 1, getMeasuredWidth() - (LocaleController.isRTL ? AndroidUtilities.dp(72.0f) : 0), getMeasuredHeight(), Theme.dividerPaint);
            }
        }

        @Override
        protected void onMeasure(int i, int i2) {
            super.onMeasure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(i), 1073741824), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(this.threeLines ? 71.0f : 58.0f), 1073741824));
        }

        public void set(org.telegram.tgnet.tl.TL_stars.StarsTransaction r25, boolean r26, boolean r27) {
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Stars.StarsIntroActivity.StarsTransactionView.set(org.telegram.tgnet.tl.TL_stars$StarsTransaction, boolean, boolean):void");
        }
    }

    public static class StarsTransactionsLayout extends LinearLayout implements NotificationCenter.NotificationCenterDelegate {
        private final PageAdapter adapter;
        private final long bot_id;
        private final int currentAccount;
        private final ViewPagerFixed.TabsView tabsView;
        private final ViewPagerFixed viewPager;

        public static class Page extends FrameLayout implements NotificationCenter.NotificationCenterDelegate {
            private final long bot_id;
            private final int currentAccount;
            private final UniversalRecyclerView listView;
            private final Runnable loadTransactionsRunnable;
            private final Theme.ResourcesProvider resourcesProvider;
            private final int type;

            public Page(Context context, final long j, final int i, final int i2, int i3, Theme.ResourcesProvider resourcesProvider) {
                super(context);
                this.type = i;
                this.currentAccount = i2;
                this.bot_id = j;
                this.resourcesProvider = resourcesProvider;
                this.loadTransactionsRunnable = new Runnable() {
                    @Override
                    public final void run() {
                        StarsIntroActivity.StarsTransactionsLayout.Page.lambda$new$0(j, i2, i);
                    }
                };
                UniversalRecyclerView universalRecyclerView = new UniversalRecyclerView(context, i2, i3, true, new Utilities.Callback2() {
                    @Override
                    public final void run(Object obj, Object obj2) {
                        StarsIntroActivity.StarsTransactionsLayout.Page.this.fillItems((ArrayList) obj, (UniversalAdapter) obj2);
                    }
                }, new Utilities.Callback5() {
                    @Override
                    public final void run(Object obj, Object obj2, Object obj3, Object obj4, Object obj5) {
                        StarsIntroActivity.StarsTransactionsLayout.Page.this.onClick((UItem) obj, (View) obj2, ((Integer) obj3).intValue(), ((Float) obj4).floatValue(), ((Float) obj5).floatValue());
                    }
                }, null, resourcesProvider);
                this.listView = universalRecyclerView;
                addView(universalRecyclerView, LayoutHelper.createFrame(-1, -1.0f));
                universalRecyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {
                    @Override
                    public void onScrolled(RecyclerView recyclerView, int i4, int i5) {
                        if (!Page.this.listView.canScrollVertically(1) || Page.this.isLoadingVisible()) {
                            Page.this.loadTransactionsRunnable.run();
                        }
                    }
                });
            }

            public void fillItems(ArrayList arrayList, UniversalAdapter universalAdapter) {
                if (this.bot_id != 0) {
                    BotStarsController botStarsController = BotStarsController.getInstance(this.currentAccount);
                    Iterator it = botStarsController.getTransactions(this.bot_id, this.type).iterator();
                    while (it.hasNext()) {
                        arrayList.add(StarsTransactionView.Factory.asTransaction((TL_stars.StarsTransaction) it.next(), true));
                    }
                    if (botStarsController.didFullyLoadTransactions(this.bot_id, this.type)) {
                        return;
                    }
                } else {
                    StarsController starsController = StarsController.getInstance(this.currentAccount);
                    Iterator it2 = starsController.transactions[this.type].iterator();
                    while (it2.hasNext()) {
                        arrayList.add(StarsTransactionView.Factory.asTransaction((TL_stars.StarsTransaction) it2.next(), false));
                    }
                    if (starsController.didFullyLoadTransactions(this.type)) {
                        return;
                    }
                }
                arrayList.add(UItem.asFlicker(arrayList.size(), 7));
                arrayList.add(UItem.asFlicker(arrayList.size(), 7));
                arrayList.add(UItem.asFlicker(arrayList.size(), 7));
            }

            public static void lambda$new$0(long j, int i, int i2) {
                if (j != 0) {
                    BotStarsController.getInstance(i).loadTransactions(j, i2);
                } else {
                    StarsController.getInstance(i).loadTransactions(i2);
                }
            }

            public void onClick(UItem uItem, View view, int i, float f, float f2) {
                if (uItem.object instanceof TL_stars.StarsTransaction) {
                    StarsIntroActivity.showTransactionSheet(getContext(), false, 0L, this.currentAccount, (TL_stars.StarsTransaction) uItem.object, this.resourcesProvider);
                }
            }

            @Override
            public void didReceivedNotification(int i, int i2, Object... objArr) {
                if (i != NotificationCenter.starTransactionsLoaded) {
                    if (i == NotificationCenter.botStarsTransactionsLoaded && ((Long) objArr[0]).longValue() == this.bot_id) {
                        this.listView.adapter.update(true);
                        return;
                    }
                    return;
                }
                this.listView.adapter.update(true);
                if (!this.listView.canScrollVertically(1) || isLoadingVisible()) {
                    this.loadTransactionsRunnable.run();
                }
            }

            public boolean isLoadingVisible() {
                for (int i = 0; i < this.listView.getChildCount(); i++) {
                    if (this.listView.getChildAt(i) instanceof FlickerLoadingView) {
                        return true;
                    }
                }
                return false;
            }

            @Override
            protected void onAttachedToWindow() {
                super.onAttachedToWindow();
                NotificationCenter.getInstance(this.currentAccount).addObserver(this, this.bot_id != 0 ? NotificationCenter.botStarsTransactionsLoaded : NotificationCenter.starTransactionsLoaded);
                this.listView.adapter.update(false);
            }

            @Override
            protected void onDetachedFromWindow() {
                super.onDetachedFromWindow();
                NotificationCenter.getInstance(this.currentAccount).removeObserver(this, this.bot_id != 0 ? NotificationCenter.botStarsTransactionsLoaded : NotificationCenter.starTransactionsLoaded);
            }
        }

        private static class PageAdapter extends ViewPagerFixed.Adapter {
            private final long bot_id;
            private final int classGuid;
            private final Context context;
            private final int currentAccount;
            private final ArrayList items = new ArrayList();
            private final Theme.ResourcesProvider resourcesProvider;

            public PageAdapter(Context context, int i, long j, int i2, Theme.ResourcesProvider resourcesProvider) {
                this.context = context;
                this.currentAccount = i;
                this.classGuid = i2;
                this.resourcesProvider = resourcesProvider;
                this.bot_id = j;
                fill();
            }

            @Override
            public void bindView(View view, int i, int i2) {
            }

            @Override
            public View createView(int i) {
                return new Page(this.context, this.bot_id, i, this.currentAccount, this.classGuid, this.resourcesProvider);
            }

            public void fill() {
                this.items.clear();
                long j = this.bot_id;
                int i = this.currentAccount;
                if (j == 0) {
                    StarsController starsController = StarsController.getInstance(i);
                    this.items.add(UItem.asSpace(0));
                    if (starsController.hasTransactions(1)) {
                        this.items.add(UItem.asSpace(1));
                    }
                    if (!starsController.hasTransactions(2)) {
                        return;
                    }
                } else {
                    BotStarsController botStarsController = BotStarsController.getInstance(i);
                    this.items.add(UItem.asSpace(0));
                    if (botStarsController.hasTransactions(this.bot_id, 1)) {
                        this.items.add(UItem.asSpace(1));
                    }
                    if (!botStarsController.hasTransactions(this.bot_id, 2)) {
                        return;
                    }
                }
                this.items.add(UItem.asSpace(2));
            }

            @Override
            public int getItemCount() {
                return this.items.size();
            }

            @Override
            public String getItemTitle(int i) {
                int i2;
                int itemViewType = getItemViewType(i);
                if (itemViewType == 0) {
                    i2 = R.string.StarsTransactionsAll;
                } else if (itemViewType == 1) {
                    i2 = R.string.StarsTransactionsIncoming;
                } else {
                    if (itemViewType != 2) {
                        return "";
                    }
                    i2 = R.string.StarsTransactionsOutgoing;
                }
                return LocaleController.getString(i2);
            }

            @Override
            public int getItemViewType(int i) {
                if (i < 0 || i >= this.items.size()) {
                    return 0;
                }
                return ((UItem) this.items.get(i)).intValue;
            }
        }

        public StarsTransactionsLayout(Context context, int i, long j, int i2, Theme.ResourcesProvider resourcesProvider) {
            super(context);
            this.currentAccount = i;
            this.bot_id = j;
            setOrientation(1);
            ViewPagerFixed viewPagerFixed = new ViewPagerFixed(context);
            this.viewPager = viewPagerFixed;
            PageAdapter pageAdapter = new PageAdapter(context, i, j, i2, resourcesProvider);
            this.adapter = pageAdapter;
            viewPagerFixed.setAdapter(pageAdapter);
            ViewPagerFixed.TabsView createTabsView = viewPagerFixed.createTabsView(true, 3);
            this.tabsView = createTabsView;
            View view = new View(context);
            view.setBackgroundColor(Theme.getColor(Theme.key_divider, resourcesProvider));
            addView(createTabsView, LayoutHelper.createLinear(-1, 48));
            addView(view, LayoutHelper.createLinear(-1.0f, 1.0f / AndroidUtilities.density));
            addView(viewPagerFixed, LayoutHelper.createLinear(-1, -1));
            setBackgroundColor(Theme.getColor(Theme.key_dialogBackground, resourcesProvider));
        }

        @Override
        public void didReceivedNotification(int i, int i2, Object... objArr) {
            if (i == NotificationCenter.starTransactionsLoaded) {
                this.adapter.fill();
                this.viewPager.fillTabs(true);
            }
        }

        public RecyclerListView getCurrentListView() {
            View currentView = this.viewPager.getCurrentView();
            if (currentView instanceof Page) {
                return ((Page) currentView).listView;
            }
            return null;
        }

        @Override
        protected void onAttachedToWindow() {
            this.adapter.fill();
            this.viewPager.fillTabs(false);
            NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.starTransactionsLoaded);
            super.onAttachedToWindow();
        }

        @Override
        protected void onDetachedFromWindow() {
            NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.starTransactionsLoaded);
            super.onDetachedFromWindow();
        }
    }

    public StarsIntroActivity() {
        setWhiteBackground(true);
    }

    public static void addAvailabilityRow(TableView tableView, int i, TL_stars.StarGift starGift, Theme.ResourcesProvider resourcesProvider) {
        final TextView textView = (TextView) ((TableView.TableRowContent) tableView.addRow(LocaleController.getString(R.string.Gift2Availability), "").getChildAt(1)).getChildAt(0);
        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder("x ");
        LoadingSpan loadingSpan = new LoadingSpan(textView, AndroidUtilities.dp(90.0f), 0, resourcesProvider);
        loadingSpan.setColors(Theme.multAlpha(textView.getPaint().getColor(), 0.21f), Theme.multAlpha(textView.getPaint().getColor(), 0.08f));
        spannableStringBuilder.setSpan(loadingSpan, 0, 1, 33);
        textView.setText(spannableStringBuilder, TextView.BufferType.SPANNABLE);
        if (!starGift.sold_out) {
            StarsController.getInstance(i).getStarGift(starGift.id, new Utilities.Callback() {
                @Override
                public final void run(Object obj) {
                    StarsIntroActivity.lambda$addAvailabilityRow$110(textView, (TL_stars.StarGift) obj);
                }
            });
        } else {
            int i2 = starGift.availability_remains;
            textView.setText(i2 <= 0 ? LocaleController.formatPluralStringComma("Gift2Availability2ValueNone", starGift.availability_total) : LocaleController.formatPluralStringComma("Gift2Availability4Value", i2, LocaleController.formatNumber(starGift.availability_total, ',')));
        }
    }

    private static CharSequence appendStatus(SpannableStringBuilder spannableStringBuilder, TextView textView, String str) {
        spannableStringBuilder.append(" ");
        SpannableString spannableString = new SpannableString(str);
        spannableString.setSpan(new ReplacementSpan(textView.getCurrentTextColor(), str) {
            private final Paint backgroundPaint;
            private final Text layout;
            final int val$color;
            final String val$string;

            {
                this.val$color = r3;
                this.val$string = str;
                Paint paint = new Paint(1);
                this.backgroundPaint = paint;
                paint.setColor(Theme.multAlpha(r3, 0.1f));
                this.layout = new Text(str, 13.0f, AndroidUtilities.bold());
            }

            @Override
            public void draw(Canvas canvas, CharSequence charSequence, int i, int i2, float f, int i3, int i4, int i5, Paint paint) {
                RectF rectF = AndroidUtilities.rectTmp;
                rectF.set(f, (r12 - AndroidUtilities.dp(20.0f)) / 2.0f, AndroidUtilities.dp(12.0f) + f + this.layout.getCurrentWidth(), (AndroidUtilities.dp(20.0f) + r12) / 2.0f);
                canvas.drawRoundRect(rectF, AndroidUtilities.dp(4.0f), AndroidUtilities.dp(4.0f), this.backgroundPaint);
                this.layout.draw(canvas, f + AndroidUtilities.dp(6.0f), (i3 + i5) / 2.0f, this.val$color, 1.0f);
            }

            @Override
            public int getSize(Paint paint, CharSequence charSequence, int i, int i2, Paint.FontMetricsInt fontMetricsInt) {
                return (int) (AndroidUtilities.dp(12.0f) + this.layout.getCurrentWidth());
            }
        }, 0, spannableString.length(), 33);
        spannableStringBuilder.append((CharSequence) spannableString);
        return spannableStringBuilder;
    }

    public static CharSequence formatStarsAmount(TL_stars.StarsAmount starsAmount) {
        return formatStarsAmount(starsAmount, 0.777f, ',');
    }

    public static CharSequence formatStarsAmount(TL_stars.StarsAmount starsAmount, float f, char c) {
        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder();
        long j = starsAmount.amount;
        int i = starsAmount.nanos;
        long j2 = j + ((i >= 0 || j <= 0) ? (i <= 0 || j >= 0) ? 0 : 1 : -1);
        boolean z = j2 < 0;
        if (i != 0) {
            StringBuilder sb = new StringBuilder();
            sb.append(z ? "-" : "");
            sb.append(LocaleController.formatNumber(Math.abs(j2), c));
            spannableStringBuilder.append((CharSequence) sb.toString());
            if (floatFormat == null) {
                floatFormat = new DecimalFormat("0.################");
            }
            DecimalFormat decimalFormat = floatFormat;
            int i2 = starsAmount.nanos;
            double d = i2;
            if (i2 < 0) {
                Double.isNaN(d);
                d += 1.0E9d;
            }
            String format = decimalFormat.format(d / 1.0E9d);
            int indexOf = format.indexOf(".");
            if (indexOf >= 0) {
                int length = spannableStringBuilder.length();
                spannableStringBuilder.append((CharSequence) format.substring(indexOf));
                spannableStringBuilder.setSpan(new RelativeSizeSpan(f), length + 1, spannableStringBuilder.length(), 33);
            }
        } else {
            StringBuilder sb2 = new StringBuilder();
            sb2.append(z ? "-" : "");
            sb2.append(LocaleController.formatNumber(Math.abs(j2), ' '));
            spannableStringBuilder.append((CharSequence) sb2.toString());
        }
        return spannableStringBuilder;
    }

    public static CharSequence formatStarsAmountShort(TL_stars.StarsAmount starsAmount) {
        return formatStarsAmountShort(starsAmount, 0.777f, ' ');
    }

    public static CharSequence formatStarsAmountShort(TL_stars.StarsAmount starsAmount, float f, char c) {
        StringBuilder sb;
        String formatWholeNumber;
        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder();
        long j = starsAmount.amount;
        int i = starsAmount.nanos;
        long j2 = j + ((i >= 0 || j <= 0) ? (i <= 0 || j >= 0) ? 0 : 1 : -1);
        boolean z = j2 < 0;
        if (Math.abs(j2) > 1000 || starsAmount.nanos == 0) {
            if (starsAmount.amount <= 1000) {
                sb = new StringBuilder();
                sb.append(z ? "-" : "");
                formatWholeNumber = LocaleController.formatNumber(Math.abs(j2), c);
            } else {
                sb = new StringBuilder();
                sb.append(z ? "-" : "");
                formatWholeNumber = AndroidUtilities.formatWholeNumber((int) Math.abs(j2), 0);
            }
            sb.append(formatWholeNumber);
            spannableStringBuilder.append((CharSequence) sb.toString());
        } else {
            StringBuilder sb2 = new StringBuilder();
            sb2.append(z ? "-" : "");
            sb2.append(LocaleController.formatNumber(Math.abs(j2), c));
            spannableStringBuilder.append((CharSequence) sb2.toString());
            if (floatFormat == null) {
                floatFormat = new DecimalFormat("0.################");
            }
            DecimalFormat decimalFormat = floatFormat;
            int i2 = starsAmount.nanos;
            double d = i2;
            if (i2 < 0) {
                Double.isNaN(d);
                d += 1.0E9d;
            }
            String format = decimalFormat.format(d / 1.0E9d);
            int indexOf = format.indexOf(".");
            if (indexOf >= 0) {
                int length = spannableStringBuilder.length();
                String substring = format.substring(indexOf);
                if (substring.length() > 1) {
                    spannableStringBuilder.append((CharSequence) substring.substring(0, Math.min(substring.length(), 3)));
                    spannableStringBuilder.setSpan(new RelativeSizeSpan(f), length + 1, spannableStringBuilder.length(), 33);
                }
            }
        }
        return spannableStringBuilder;
    }

    public static CharSequence formatStarsAmountString(TL_stars.StarsAmount starsAmount) {
        return formatStarsAmountString(starsAmount, 0.777f, ',');
    }

    public static CharSequence formatStarsAmountString(TL_stars.StarsAmount starsAmount, float f, char c) {
        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder();
        long j = starsAmount.amount;
        int i = starsAmount.nanos;
        long j2 = ((i >= 0 || j <= 0) ? (i <= 0 || j >= 0) ? 0 : 1 : -1) + j;
        boolean z = j2 < 0;
        if (i != 0) {
            StringBuilder sb = new StringBuilder();
            sb.append(z ? "-" : "");
            sb.append(LocaleController.formatNumber(Math.abs(j2), c));
            spannableStringBuilder.append((CharSequence) sb.toString());
            if (floatFormat == null) {
                floatFormat = new DecimalFormat("0.################");
            }
            DecimalFormat decimalFormat = floatFormat;
            int i2 = starsAmount.nanos;
            double d = i2;
            if (i2 < 0) {
                Double.isNaN(d);
                d += 1.0E9d;
            }
            String format = decimalFormat.format(d / 1.0E9d);
            int indexOf = format.indexOf(".");
            if (indexOf >= 0) {
                int length = spannableStringBuilder.length();
                spannableStringBuilder.append((CharSequence) format.substring(indexOf));
                spannableStringBuilder.setSpan(new RelativeSizeSpan(f), length + 1, spannableStringBuilder.length(), 33);
            }
            spannableStringBuilder.append((CharSequence) " ").append((CharSequence) LocaleController.getString(R.string.StarsNano));
        } else {
            spannableStringBuilder.append((CharSequence) LocaleController.formatPluralStringComma("Stars", (int) j));
        }
        return spannableStringBuilder;
    }

    public static CharSequence getTransactionTitle(int i, boolean z, TL_stars.StarsTransaction starsTransaction) {
        if (starsTransaction.floodskip) {
            return LocaleController.getString(R.string.StarsTransactionFloodskip);
        }
        if (!starsTransaction.extended_media.isEmpty()) {
            return LocaleController.getString(R.string.StarMediaPurchase);
        }
        int i2 = starsTransaction.flags;
        if ((131072 & i2) == 0 && (65536 & i2) != 0) {
            return LocaleController.formatString(R.string.StarTransactionCommission, AffiliateProgramFragment.percents(starsTransaction.starref_commission_permille));
        }
        if (starsTransaction.stargift != null) {
            if (starsTransaction.refund) {
                return LocaleController.getString(starsTransaction.stars.amount > 0 ? R.string.Gift2TransactionRefundedSent : R.string.Gift2TransactionRefundedConverted);
            }
            return LocaleController.getString(starsTransaction.stars.amount > 0 ? R.string.Gift2TransactionConverted : R.string.Gift2TransactionSent);
        }
        if (starsTransaction.subscription) {
            int i3 = starsTransaction.subscription_period;
            if (i3 == 2592000) {
                return LocaleController.getString(R.string.StarSubscriptionPurchase);
            }
            if (i3 == 300) {
                return "5-minute subscription fee";
            }
            if (i3 == 60) {
                return "Minute subscription fee";
            }
        }
        if ((i2 & 8192) != 0) {
            return LocaleController.getString(R.string.StarsGiveawayPrizeReceived);
        }
        if (starsTransaction.gift) {
            if (starsTransaction.sent_by != null) {
                return LocaleController.getString(UserObject.isUserSelf(MessagesController.getInstance(i).getUser(Long.valueOf(DialogObject.getPeerDialogId(starsTransaction.sent_by)))) ? R.string.StarsGiftSent : R.string.StarsGiftReceived);
            }
            return LocaleController.getString(R.string.StarsGiftReceived);
        }
        String str = starsTransaction.title;
        if (str != null) {
            return str;
        }
        long peerDialogId = DialogObject.getPeerDialogId(starsTransaction.peer.peer);
        if (peerDialogId != 0) {
            if (peerDialogId >= 0) {
                return UserObject.getUserName(MessagesController.getInstance(UserConfig.selectedAccount).getUser(Long.valueOf(peerDialogId)));
            }
            TLRPC.Chat chat = MessagesController.getInstance(UserConfig.selectedAccount).getChat(Long.valueOf(-peerDialogId));
            return chat == null ? "" : chat.title;
        }
        TL_stars.StarsTransactionPeer starsTransactionPeer = starsTransaction.peer;
        if (starsTransactionPeer instanceof TL_stars.TL_starsTransactionPeerFragment) {
            return LocaleController.getString(z ? R.string.StarsTransactionWithdrawFragment : R.string.StarsTransactionFragment);
        }
        return starsTransactionPeer instanceof TL_stars.TL_starsTransactionPeerPremiumBot ? LocaleController.getString(R.string.StarsTransactionBot) : LocaleController.getString(R.string.StarsTransactionUnsupported);
    }

    public static void lambda$addAvailabilityRow$110(TextView textView, TL_stars.StarGift starGift) {
        if (starGift == null) {
            return;
        }
        int i = starGift.availability_remains;
        int i2 = starGift.availability_total;
        textView.setText(i <= 0 ? LocaleController.formatPluralStringComma("Gift2Availability2ValueNone", i2) : LocaleController.formatPluralStringComma("Gift2Availability4Value", i, LocaleController.formatNumber(i2, ',')));
    }

    public static void lambda$createView$0(Context context) {
        new ExplainStarsSheet(context).show();
    }

    public void lambda$createView$1(View view, int i) {
        UItem item;
        UniversalAdapter universalAdapter = this.adapter;
        if (universalAdapter == null || (item = universalAdapter.getItem(i)) == null) {
            return;
        }
        onItemClick(item, i);
    }

    public void lambda$createView$2(Context context, View view) {
        new StarsOptionsSheet(context, this.resourceProvider).show();
    }

    public void lambda$createView$3(View view) {
        StarsController.getInstance(this.currentAccount).getGiftOptions();
        UserSelectorBottomSheet.open(1, 0L, BirthdayController.getInstance(this.currentAccount).getState());
    }

    public void lambda$onItemClick$4(UItem uItem, Boolean bool, String str) {
        if (getContext() == null) {
            return;
        }
        if (bool.booleanValue()) {
            BulletinFactory.of(this).createSimpleBulletin(R.raw.stars_topup, LocaleController.getString(R.string.StarsAcquired), AndroidUtilities.replaceTags(LocaleController.formatPluralString("StarsAcquiredInfo", (int) uItem.longValue, new Object[0]))).show();
            this.fireworksOverlay.start(true);
            StarsController.getInstance(this.currentAccount).invalidateTransactions(true);
        } else if (str != null) {
            BulletinFactory.of(this).createSimpleBulletin(R.raw.error, LocaleController.formatString(R.string.UnknownErrorCode, str)).show();
        }
    }

    public static void lambda$openConfirmPurchaseSheet$10(Runnable runnable, DialogInterface dialogInterface) {
        if (runnable != null) {
            runnable.run();
        }
    }

    public static void lambda$openConfirmPurchaseSheet$5(StarsBalanceView starsBalanceView, View view) {
        BaseFragment lastFragment;
        if (starsBalanceView.lastBalance > 0 && (lastFragment = LaunchActivity.getLastFragment()) != null) {
            BaseFragment.BottomSheetParams bottomSheetParams = new BaseFragment.BottomSheetParams();
            bottomSheetParams.transitionFromLeft = true;
            bottomSheetParams.allowNestedScroll = false;
            lastFragment.showAsSheet(new StarsIntroActivity(), bottomSheetParams);
        }
    }

    public static void lambda$openConfirmPurchaseSheet$6(Context context) {
        Browser.openUrl(context, LocaleController.getString(R.string.StarsTOSLink));
    }

    public static void lambda$openConfirmPurchaseSheet$7(BottomSheet bottomSheet, ButtonWithCounterView buttonWithCounterView) {
        bottomSheet.setCanDismissWithSwipe(false);
        buttonWithCounterView.setLoading(false);
    }

    public static void lambda$openConfirmPurchaseSheet$8(final BottomSheet bottomSheet, final ButtonWithCounterView buttonWithCounterView, Boolean bool) {
        if (bool.booleanValue()) {
            bottomSheet.dismiss();
        } else {
            AndroidUtilities.runOnUIThread(new Runnable() {
                @Override
                public final void run() {
                    StarsIntroActivity.lambda$openConfirmPurchaseSheet$7(BottomSheet.this, buttonWithCounterView);
                }
            }, 400L);
        }
    }

    public static void lambda$openConfirmPurchaseSheet$9(Utilities.Callback callback, final BottomSheet bottomSheet, final ButtonWithCounterView buttonWithCounterView, View view) {
        if (callback == null) {
            bottomSheet.dismiss();
            return;
        }
        bottomSheet.setCanDismissWithSwipe(false);
        buttonWithCounterView.setLoading(true);
        callback.run(new Utilities.Callback() {
            @Override
            public final void run(Object obj) {
                StarsIntroActivity.lambda$openConfirmPurchaseSheet$8(BottomSheet.this, buttonWithCounterView, (Boolean) obj);
            }
        });
    }

    public static void lambda$openStarsChannelInviteSheet$11(StarsBalanceView starsBalanceView, View view) {
        BaseFragment lastFragment;
        if (starsBalanceView.lastBalance > 0 && (lastFragment = LaunchActivity.getLastFragment()) != null) {
            BaseFragment.BottomSheetParams bottomSheetParams = new BaseFragment.BottomSheetParams();
            bottomSheetParams.transitionFromLeft = true;
            bottomSheetParams.allowNestedScroll = false;
            lastFragment.showAsSheet(new StarsIntroActivity(), bottomSheetParams);
        }
    }

    public static void lambda$openStarsChannelInviteSheet$12(Context context) {
        Browser.openUrl(context, LocaleController.getString(R.string.StarsSubscribeInfoLink));
    }

    public static void lambda$openStarsChannelInviteSheet$13(BottomSheet bottomSheet, ButtonWithCounterView buttonWithCounterView) {
        bottomSheet.setCanDismissWithSwipe(false);
        buttonWithCounterView.setLoading(false);
    }

    public static void lambda$openStarsChannelInviteSheet$14(final BottomSheet bottomSheet, final ButtonWithCounterView buttonWithCounterView, Boolean bool) {
        if (bool.booleanValue()) {
            bottomSheet.dismiss();
        } else {
            AndroidUtilities.runOnUIThread(new Runnable() {
                @Override
                public final void run() {
                    StarsIntroActivity.lambda$openStarsChannelInviteSheet$13(BottomSheet.this, buttonWithCounterView);
                }
            }, 400L);
        }
    }

    public static void lambda$openStarsChannelInviteSheet$15(Utilities.Callback callback, final BottomSheet bottomSheet, final ButtonWithCounterView buttonWithCounterView, View view) {
        if (callback == null) {
            bottomSheet.dismiss();
            return;
        }
        bottomSheet.setCanDismissWithSwipe(false);
        buttonWithCounterView.setLoading(true);
        callback.run(new Utilities.Callback() {
            @Override
            public final void run(Object obj) {
                StarsIntroActivity.lambda$openStarsChannelInviteSheet$14(BottomSheet.this, buttonWithCounterView, (Boolean) obj);
            }
        });
    }

    public static void lambda$openStarsChannelInviteSheet$16(Runnable runnable, DialogInterface dialogInterface) {
        if (runnable != null) {
            runnable.run();
        }
    }

    public static void lambda$setGiftImage$17(int i, int i2, ImageReceiver imageReceiver, final boolean[] zArr) {
        TLRPC.Document document;
        String str = UserConfig.getInstance(i).premiumGiftsStickerPack;
        if (str == null) {
            MediaDataController.getInstance(i).checkPremiumGiftStickers();
            return;
        }
        TLRPC.TL_messages_stickerSet stickerSetByName = MediaDataController.getInstance(i).getStickerSetByName(str);
        if (stickerSetByName == null) {
            stickerSetByName = MediaDataController.getInstance(i).getStickerSetByEmojiOrName(str);
        }
        TLRPC.TL_messages_stickerSet tL_messages_stickerSet = stickerSetByName;
        if (tL_messages_stickerSet != null) {
            String str2 = i2 == 2 ? "2⃣" : i2 == 3 ? "3⃣" : "4⃣";
            int i3 = 0;
            while (true) {
                if (i3 >= tL_messages_stickerSet.packs.size()) {
                    break;
                }
                TLRPC.TL_stickerPack tL_stickerPack = tL_messages_stickerSet.packs.get(i3);
                if (TextUtils.equals(tL_stickerPack.emoticon, str2) && !tL_stickerPack.documents.isEmpty()) {
                    long longValue = tL_stickerPack.documents.get(0).longValue();
                    for (int i4 = 0; i4 < tL_messages_stickerSet.documents.size(); i4++) {
                        document = tL_messages_stickerSet.documents.get(i4);
                        if (document != null && document.id == longValue) {
                            break;
                        }
                    }
                } else {
                    i3++;
                }
            }
            document = null;
            if (document == null && !tL_messages_stickerSet.documents.isEmpty()) {
                document = tL_messages_stickerSet.documents.get(0);
            }
        } else {
            document = null;
        }
        if (document == null) {
            MediaDataController.getInstance(i).loadStickersByEmojiOrName(str, false, tL_messages_stickerSet == null);
            return;
        }
        imageReceiver.setAllowStartLottieAnimation(true);
        imageReceiver.setDelegate(new ImageReceiver.ImageReceiverDelegate() {
            @Override
            public void didSetImage(ImageReceiver imageReceiver2, boolean z, boolean z2, boolean z3) {
                RLottieDrawable lottieAnimation;
                if (!z || (lottieAnimation = imageReceiver2.getLottieAnimation()) == null || zArr[0]) {
                    return;
                }
                lottieAnimation.setCurrentFrame(0, false);
                AndroidUtilities.runOnUIThread(new ChatActionCell$$ExternalSyntheticLambda7(lottieAnimation));
                zArr[0] = true;
            }

            @Override
            public void didSetImageBitmap(int i5, String str3, Drawable drawable) {
                ImageReceiver.ImageReceiverDelegate.CC.$default$didSetImageBitmap(this, i5, str3, drawable);
            }

            @Override
            public void onAnimationReady(ImageReceiver imageReceiver2) {
                ImageReceiver.ImageReceiverDelegate.CC.$default$onAnimationReady(this, imageReceiver2);
            }
        });
        Drawable svgThumb = DocumentObject.getSvgThumb(document, Theme.key_windowBackgroundGray, 0.3f);
        TLRPC.PhotoSize closestPhotoSizeWithSize = FileLoader.getClosestPhotoSizeWithSize(document.thumbs, 160, true, null, true);
        imageReceiver.setAutoRepeat(0);
        imageReceiver.setImage(ImageLocation.getForDocument(document), "160_160_nr", ImageLocation.getForDocument(closestPhotoSizeWithSize, document), "160_160", svgThumb, document.size, "tgs", tL_messages_stickerSet, 1);
    }

    public static void lambda$setGiftImage$20(Runnable runnable, Runnable runnable2) {
        runnable.run();
        runnable2.run();
    }

    public static void lambda$showActionGiftSheet$80(Context context) {
        new ExplainStarsSheet(context).show();
    }

    public static void lambda$showActionGiftSheet$81(BottomSheet[] bottomSheetArr, long j, long j2) {
        bottomSheetArr[0].dismiss();
        BaseFragment safeLastFragment = LaunchActivity.getSafeLastFragment();
        if (safeLastFragment == null || UserObject.isService(j)) {
            return;
        }
        Bundle bundle = new Bundle();
        if (j > 0) {
            bundle.putLong("user_id", j);
            if (j == j2) {
                bundle.putBoolean("my_profile", true);
                bundle.putBoolean("open_gifts", true);
            }
        } else {
            bundle.putLong("chat_id", -j);
        }
        safeLastFragment.presentFragment(new ProfileActivity(bundle));
    }

    public static void lambda$showActionGiftSheet$82(Context context, int i, long j, BottomSheet[] bottomSheetArr) {
        BottomSheet bottomSheet = bottomSheetArr[0];
        Objects.requireNonNull(bottomSheet);
        new GiftSheet(context, i, j, new BottomSheet$$ExternalSyntheticLambda11(bottomSheet)).show();
    }

    public static void lambda$showActionGiftSheet$83(BottomSheet[] bottomSheetArr, long j, long j2) {
        bottomSheetArr[0].dismiss();
        BaseFragment safeLastFragment = LaunchActivity.getSafeLastFragment();
        if (safeLastFragment == null || UserObject.isService(j)) {
            return;
        }
        Bundle bundle = new Bundle();
        if (j > 0) {
            bundle.putLong("user_id", j);
            if (j == j2) {
                bundle.putBoolean("my_profile", true);
                bundle.putBoolean("open_gifts", true);
            }
        } else {
            bundle.putLong("chat_id", -j);
        }
        safeLastFragment.presentFragment(new ProfileActivity(bundle));
    }

    public static void lambda$showActionGiftSheet$84(Context context, int i, long j, BottomSheet[] bottomSheetArr) {
        BottomSheet bottomSheet = bottomSheetArr[0];
        Objects.requireNonNull(bottomSheet);
        new GiftSheet(context, i, j, new BottomSheet$$ExternalSyntheticLambda11(bottomSheet)).show();
    }

    public static void lambda$showActionGiftSheet$85(StarsIntroActivity starsIntroActivity, TLRPC.TL_messageActionStarGift tL_messageActionStarGift) {
        BulletinFactory.of(starsIntroActivity).createSimpleBulletin(R.raw.stars_topup, LocaleController.getString(R.string.Gift2ConvertedTitle), LocaleController.formatPluralStringComma("Gift2Converted", (int) tL_messageActionStarGift.convert_stars)).show(true);
    }

    public static void lambda$showActionGiftSheet$86(AlertDialog alertDialog, TLObject tLObject, BottomSheet[] bottomSheetArr, int i, long j, long j2, final TLRPC.TL_messageActionStarGift tL_messageActionStarGift, TLRPC.TL_error tL_error, Theme.ResourcesProvider resourcesProvider) {
        BulletinFactory of;
        String string;
        alertDialog.dismissUnless(400L);
        BaseFragment safeLastFragment = LaunchActivity.getSafeLastFragment();
        if (safeLastFragment == null) {
            return;
        }
        if (!(tLObject instanceof TLRPC.TL_boolTrue)) {
            if (tL_error != null) {
                of = BulletinFactory.of(bottomSheetArr[0].topBulletinContainer, resourcesProvider);
                string = LocaleController.formatString(R.string.UnknownErrorCode, tL_error.text);
            } else {
                of = BulletinFactory.of(bottomSheetArr[0].topBulletinContainer, resourcesProvider);
                string = LocaleController.getString(R.string.UnknownError);
            }
            of.createErrorBulletin(string).show(false);
            return;
        }
        bottomSheetArr[0].dismiss();
        StarsController.getInstance(i).invalidateProfileGifts(j);
        TLRPC.UserFull userFull = MessagesController.getInstance(i).getUserFull(j2);
        if (userFull != null) {
            int max = Math.max(0, userFull.stargifts_count - 1);
            userFull.stargifts_count = max;
            if (max <= 0) {
                userFull.flags2 &= -257;
            }
        }
        StarsController.getInstance(i).invalidateBalance();
        StarsController.getInstance(i).invalidateTransactions(true);
        if (safeLastFragment instanceof StarsIntroActivity) {
            BulletinFactory.of(safeLastFragment).createSimpleBulletin(R.raw.stars_topup, LocaleController.getString(R.string.Gift2ConvertedTitle), LocaleController.formatPluralStringComma("Gift2Converted", (int) tL_messageActionStarGift.convert_stars)).show(true);
            return;
        }
        final StarsIntroActivity starsIntroActivity = new StarsIntroActivity();
        starsIntroActivity.whenFullyVisible(new Runnable() {
            @Override
            public final void run() {
                StarsIntroActivity.lambda$showActionGiftSheet$85(StarsIntroActivity.this, tL_messageActionStarGift);
            }
        });
        safeLastFragment.presentFragment(starsIntroActivity);
    }

    public static void lambda$showActionGiftSheet$87(final AlertDialog alertDialog, final BottomSheet[] bottomSheetArr, final int i, final long j, final long j2, final TLRPC.TL_messageActionStarGift tL_messageActionStarGift, final Theme.ResourcesProvider resourcesProvider, final TLObject tLObject, final TLRPC.TL_error tL_error) {
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                StarsIntroActivity.lambda$showActionGiftSheet$86(AlertDialog.this, tLObject, bottomSheetArr, i, j, j2, tL_messageActionStarGift, tL_error, resourcesProvider);
            }
        });
    }

    public static void lambda$showActionGiftSheet$88(int i, final int i2, final long j, final BottomSheet[] bottomSheetArr, final long j2, final TLRPC.TL_messageActionStarGift tL_messageActionStarGift, final Theme.ResourcesProvider resourcesProvider, DialogInterface dialogInterface, int i3) {
        final AlertDialog alertDialog = new AlertDialog(ApplicationLoader.applicationContext, 3);
        alertDialog.showDelayed(500L);
        TL_stars.convertStarGift convertstargift = new TL_stars.convertStarGift();
        convertstargift.msg_id = i;
        convertstargift.user_id = MessagesController.getInstance(i2).getInputUser(j);
        ConnectionsManager.getInstance(i2).sendRequest(convertstargift, new RequestDelegate() {
            @Override
            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                StarsIntroActivity.lambda$showActionGiftSheet$87(AlertDialog.this, bottomSheetArr, i2, j, j2, tL_messageActionStarGift, resourcesProvider, tLObject, tL_error);
            }
        });
    }

    public static void lambda$showActionGiftSheet$89(Context context, final Theme.ResourcesProvider resourcesProvider, int i, TLRPC.User user, final TLRPC.TL_messageActionStarGift tL_messageActionStarGift, final int i2, final int i3, final long j, final BottomSheet[] bottomSheetArr, final long j2) {
        new AlertDialog.Builder(context, resourcesProvider).setTitle(LocaleController.getString(R.string.Gift2ConvertTitle)).setMessage(AndroidUtilities.replaceTags(LocaleController.formatPluralString("Gift2ConvertText2", i, UserObject.getForcedFirstName(user), LocaleController.formatPluralStringComma("Gift2ConvertStars", (int) tL_messageActionStarGift.convert_stars)))).setPositiveButton(LocaleController.getString(R.string.Gift2ConvertButton), new DialogInterface.OnClickListener() {
            @Override
            public final void onClick(DialogInterface dialogInterface, int i4) {
                StarsIntroActivity.lambda$showActionGiftSheet$88(i2, i3, j, bottomSheetArr, j2, tL_messageActionStarGift, resourcesProvider, dialogInterface, i4);
            }
        }).setNegativeButton(LocaleController.getString(R.string.Cancel), null).show();
    }

    public static void lambda$showActionGiftSheet$90(int i, BaseFragment baseFragment) {
        Bundle bundle = new Bundle();
        bundle.putLong("user_id", UserConfig.getInstance(i).getClientUserId());
        bundle.putBoolean("my_profile", true);
        bundle.putBoolean("open_gifts", true);
        baseFragment.presentFragment(new ProfileActivity(bundle));
    }

    public static void lambda$showActionGiftSheet$91(TLObject tLObject, BottomSheet[] bottomSheetArr, final int i, long j, TL_stars.StarGift starGift, boolean z, TLRPC.TL_error tL_error, Theme.ResourcesProvider resourcesProvider) {
        final BaseFragment safeLastFragment = LaunchActivity.getSafeLastFragment();
        if (safeLastFragment == null) {
            return;
        }
        if (tLObject instanceof TLRPC.TL_boolTrue) {
            bottomSheetArr[0].dismiss();
            StarsController.getInstance(i).invalidateProfileGifts(j);
            BulletinFactory.of(safeLastFragment).createEmojiBulletin(starGift.sticker, LocaleController.getString(z ? R.string.Gift2MadePrivateTitle : R.string.Gift2MadePublicTitle), AndroidUtilities.replaceSingleTag(LocaleController.getString(z ? R.string.Gift2MadePrivate : R.string.Gift2MadePublic), safeLastFragment instanceof ProfileActivity ? null : new Runnable() {
                @Override
                public final void run() {
                    StarsIntroActivity.lambda$showActionGiftSheet$90(i, safeLastFragment);
                }
            })).show(true);
        } else if (tL_error != null) {
            BulletinFactory.of(bottomSheetArr[0].topBulletinContainer, resourcesProvider).createErrorBulletin(LocaleController.formatString(R.string.UnknownErrorCode, tL_error.text)).show(false);
        }
    }

    public static void lambda$showActionGiftSheet$92(final BottomSheet[] bottomSheetArr, final int i, final long j, final TL_stars.StarGift starGift, final boolean z, final Theme.ResourcesProvider resourcesProvider, final TLObject tLObject, final TLRPC.TL_error tL_error) {
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                StarsIntroActivity.lambda$showActionGiftSheet$91(TLObject.this, bottomSheetArr, i, j, starGift, z, tL_error, resourcesProvider);
            }
        });
    }

    public static void lambda$showActionGiftSheet$93(ButtonWithCounterView buttonWithCounterView, TLRPC.TL_messageActionStarGift tL_messageActionStarGift, int i, final int i2, long j, final BottomSheet[] bottomSheetArr, final long j2, final TL_stars.StarGift starGift, final Theme.ResourcesProvider resourcesProvider) {
        if (buttonWithCounterView.isLoading()) {
            return;
        }
        buttonWithCounterView.setLoading(true);
        TL_stars.saveStarGift savestargift = new TL_stars.saveStarGift();
        final boolean z = tL_messageActionStarGift.saved;
        savestargift.unsave = z;
        savestargift.msg_id = i;
        savestargift.user_id = MessagesController.getInstance(i2).getInputUser(j);
        ConnectionsManager.getInstance(i2).sendRequest(savestargift, new RequestDelegate() {
            @Override
            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                StarsIntroActivity.lambda$showActionGiftSheet$92(bottomSheetArr, i2, j2, starGift, z, resourcesProvider, tLObject, tL_error);
            }
        });
    }

    public static void lambda$showActionGiftSheet$94(BottomSheet[] bottomSheetArr, long j) {
        BaseFragment safeLastFragment = LaunchActivity.getSafeLastFragment();
        if (safeLastFragment == null) {
            return;
        }
        bottomSheetArr[0].dismiss();
        if ((safeLastFragment instanceof ProfileActivity) && ((ProfileActivity) safeLastFragment).myProfile) {
            return;
        }
        Bundle bundle = new Bundle();
        bundle.putLong("user_id", j);
        bundle.putBoolean("my_profile", true);
        bundle.putBoolean("open_gifts", true);
        safeLastFragment.presentFragment(new ProfileActivity(bundle));
    }

    public static void lambda$showActionGiftSheet$95(BottomSheet[] bottomSheetArr, View view) {
        bottomSheetArr[0].dismiss();
    }

    public static void lambda$showBoostsSheet$64(BottomSheet[] bottomSheetArr, long j) {
        BottomSheet bottomSheet = bottomSheetArr[0];
        if (bottomSheet != null) {
            bottomSheet.dismiss();
        }
        BaseFragment safeLastFragment = LaunchActivity.getSafeLastFragment();
        if (safeLastFragment == null) {
            return;
        }
        safeLastFragment.presentFragment(ChatActivity.of(j));
    }

    public static void lambda$showBoostsSheet$65(BottomSheet[] bottomSheetArr, long j, TL_stories.Boost boost) {
        BottomSheet bottomSheet = bottomSheetArr[0];
        if (bottomSheet != null) {
            bottomSheet.dismiss();
        }
        BaseFragment safeLastFragment = LaunchActivity.getSafeLastFragment();
        if (safeLastFragment == null) {
            return;
        }
        safeLastFragment.presentFragment(ChatActivity.of(j, boost.giveaway_msg_id));
    }

    public static void lambda$showBoostsSheet$66(Context context) {
        Browser.openUrl(context, LocaleController.getString(R.string.StarsTOSLink));
    }

    public static void lambda$showBoostsSheet$67(BottomSheet[] bottomSheetArr, View view) {
        BottomSheet bottomSheet = bottomSheetArr[0];
        if (bottomSheet != null) {
            bottomSheet.dismiss();
        }
    }

    public static void lambda$showGiftSheet$100(AlertDialog alertDialog, TLObject tLObject, BottomSheet[] bottomSheetArr, int i, long j, long j2, final TL_stars.UserStarGift userStarGift, TLRPC.TL_error tL_error, Theme.ResourcesProvider resourcesProvider) {
        BulletinFactory of;
        String string;
        alertDialog.dismissUnless(400L);
        BaseFragment safeLastFragment = LaunchActivity.getSafeLastFragment();
        if (safeLastFragment == null) {
            return;
        }
        if (!(tLObject instanceof TLRPC.TL_boolTrue)) {
            if (tL_error != null) {
                of = BulletinFactory.of(bottomSheetArr[0].topBulletinContainer, resourcesProvider);
                string = LocaleController.formatString(R.string.UnknownErrorCode, tL_error.text);
            } else {
                of = BulletinFactory.of(bottomSheetArr[0].topBulletinContainer, resourcesProvider);
                string = LocaleController.getString(R.string.UnknownError);
            }
            of.createErrorBulletin(string).show(false);
            return;
        }
        bottomSheetArr[0].dismiss();
        StarsController.getInstance(i).invalidateProfileGifts(j);
        TLRPC.UserFull userFull = MessagesController.getInstance(i).getUserFull(j2);
        if (userFull != null) {
            int max = Math.max(0, userFull.stargifts_count - 1);
            userFull.stargifts_count = max;
            if (max <= 0) {
                userFull.flags2 &= -257;
            }
        }
        StarsController.getInstance(i).invalidateBalance();
        StarsController.getInstance(i).invalidateTransactions(true);
        if (safeLastFragment instanceof StarsIntroActivity) {
            BulletinFactory.of(safeLastFragment).createSimpleBulletin(R.raw.stars_topup, LocaleController.getString(R.string.Gift2ConvertedTitle), LocaleController.formatPluralStringComma("Gift2Converted", (int) userStarGift.convert_stars)).show(true);
            return;
        }
        final StarsIntroActivity starsIntroActivity = new StarsIntroActivity();
        starsIntroActivity.whenFullyVisible(new Runnable() {
            @Override
            public final void run() {
                StarsIntroActivity.lambda$showGiftSheet$99(StarsIntroActivity.this, userStarGift);
            }
        });
        safeLastFragment.presentFragment(starsIntroActivity);
    }

    public static void lambda$showGiftSheet$101(final AlertDialog alertDialog, final BottomSheet[] bottomSheetArr, final int i, final long j, final long j2, final TL_stars.UserStarGift userStarGift, final Theme.ResourcesProvider resourcesProvider, final TLObject tLObject, final TLRPC.TL_error tL_error) {
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                StarsIntroActivity.lambda$showGiftSheet$100(AlertDialog.this, tLObject, bottomSheetArr, i, j, j2, userStarGift, tL_error, resourcesProvider);
            }
        });
    }

    public static void lambda$showGiftSheet$102(final TL_stars.UserStarGift userStarGift, final int i, final BottomSheet[] bottomSheetArr, final long j, final long j2, final Theme.ResourcesProvider resourcesProvider, DialogInterface dialogInterface, int i2) {
        final AlertDialog alertDialog = new AlertDialog(ApplicationLoader.applicationContext, 3);
        alertDialog.showDelayed(500L);
        TL_stars.convertStarGift convertstargift = new TL_stars.convertStarGift();
        convertstargift.msg_id = userStarGift.msg_id;
        convertstargift.user_id = MessagesController.getInstance(i).getInputUser(userStarGift.from_id);
        ConnectionsManager.getInstance(i).sendRequest(convertstargift, new RequestDelegate() {
            @Override
            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                StarsIntroActivity.lambda$showGiftSheet$101(AlertDialog.this, bottomSheetArr, i, j, j2, userStarGift, resourcesProvider, tLObject, tL_error);
            }
        });
    }

    public static void lambda$showGiftSheet$103(Context context, final Theme.ResourcesProvider resourcesProvider, int i, TLRPC.User user, long j, final TL_stars.UserStarGift userStarGift, final int i2, final BottomSheet[] bottomSheetArr, final long j2, final long j3) {
        new AlertDialog.Builder(context, resourcesProvider).setTitle(LocaleController.getString(R.string.Gift2ConvertTitle)).setMessage(AndroidUtilities.replaceTags(LocaleController.formatPluralString("Gift2ConvertText2", i, (user == null || UserObject.isService(j)) ? LocaleController.getString(R.string.StarsTransactionHidden) : UserObject.getForcedFirstName(user), LocaleController.formatPluralStringComma("Gift2ConvertStars", (int) userStarGift.convert_stars)))).setPositiveButton(LocaleController.getString(R.string.Gift2ConvertButton), new DialogInterface.OnClickListener() {
            @Override
            public final void onClick(DialogInterface dialogInterface, int i3) {
                StarsIntroActivity.lambda$showGiftSheet$102(TL_stars.UserStarGift.this, i2, bottomSheetArr, j2, j3, resourcesProvider, dialogInterface, i3);
            }
        }).setNegativeButton(LocaleController.getString(R.string.Cancel), null).show();
    }

    public static void lambda$showGiftSheet$104(int i, BaseFragment baseFragment) {
        Bundle bundle = new Bundle();
        bundle.putLong("user_id", UserConfig.getInstance(i).getClientUserId());
        bundle.putBoolean("my_profile", true);
        bundle.putBoolean("open_gifts", true);
        baseFragment.presentFragment(new ProfileActivity(bundle));
    }

    public static void lambda$showGiftSheet$105(TLObject tLObject, BottomSheet[] bottomSheetArr, final int i, long j, TL_stars.StarGift starGift, boolean z, TLRPC.TL_error tL_error, Theme.ResourcesProvider resourcesProvider, ButtonWithCounterView buttonWithCounterView) {
        BulletinFactory of;
        String string;
        final BaseFragment safeLastFragment = LaunchActivity.getSafeLastFragment();
        if (safeLastFragment == null) {
            return;
        }
        if (tLObject instanceof TLRPC.TL_boolTrue) {
            bottomSheetArr[0].dismiss();
            StarsController.getInstance(i).invalidateProfileGifts(j);
            BulletinFactory.of(safeLastFragment).createEmojiBulletin(starGift.sticker, LocaleController.getString(z ? R.string.Gift2MadePrivateTitle : R.string.Gift2MadePublicTitle), AndroidUtilities.replaceSingleTag(LocaleController.getString(z ? R.string.Gift2MadePrivate : R.string.Gift2MadePublic), safeLastFragment instanceof ProfileActivity ? null : new Runnable() {
                @Override
                public final void run() {
                    StarsIntroActivity.lambda$showGiftSheet$104(i, safeLastFragment);
                }
            })).show(true);
        } else {
            if (tL_error != null) {
                of = BulletinFactory.of(bottomSheetArr[0].topBulletinContainer, resourcesProvider);
                string = LocaleController.formatString(R.string.UnknownErrorCode, tL_error.text);
            } else {
                of = BulletinFactory.of(bottomSheetArr[0].topBulletinContainer, resourcesProvider);
                string = LocaleController.getString(R.string.UnknownError);
            }
            of.createErrorBulletin(string).show(false);
        }
        buttonWithCounterView.setLoading(false);
    }

    public static void lambda$showGiftSheet$106(final BottomSheet[] bottomSheetArr, final int i, final long j, final TL_stars.StarGift starGift, final boolean z, final Theme.ResourcesProvider resourcesProvider, final ButtonWithCounterView buttonWithCounterView, final TLObject tLObject, final TLRPC.TL_error tL_error) {
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                StarsIntroActivity.lambda$showGiftSheet$105(TLObject.this, bottomSheetArr, i, j, starGift, z, tL_error, resourcesProvider, buttonWithCounterView);
            }
        });
    }

    public static void lambda$showGiftSheet$107(final ButtonWithCounterView buttonWithCounterView, TL_stars.UserStarGift userStarGift, final int i, final BottomSheet[] bottomSheetArr, final long j, final TL_stars.StarGift starGift, final Theme.ResourcesProvider resourcesProvider) {
        if (buttonWithCounterView.isLoading()) {
            return;
        }
        buttonWithCounterView.setLoading(true);
        TL_stars.saveStarGift savestargift = new TL_stars.saveStarGift();
        final boolean z = !userStarGift.unsaved;
        savestargift.unsave = z;
        savestargift.msg_id = userStarGift.msg_id;
        savestargift.user_id = MessagesController.getInstance(i).getInputUser(userStarGift.from_id);
        ConnectionsManager.getInstance(i).sendRequest(savestargift, new RequestDelegate() {
            @Override
            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                StarsIntroActivity.lambda$showGiftSheet$106(bottomSheetArr, i, j, starGift, z, resourcesProvider, buttonWithCounterView, tLObject, tL_error);
            }
        });
    }

    public static void lambda$showGiftSheet$108(BottomSheet[] bottomSheetArr, View view) {
        bottomSheetArr[0].dismiss();
    }

    public static void lambda$showGiftSheet$96(Context context) {
        new ExplainStarsSheet(context).show();
    }

    public static void lambda$showGiftSheet$97(BottomSheet[] bottomSheetArr, long j, TL_stars.UserStarGift userStarGift, long j2) {
        BaseFragment profileActivity;
        bottomSheetArr[0].dismiss();
        BaseFragment safeLastFragment = LaunchActivity.getSafeLastFragment();
        if (safeLastFragment == null || UserObject.isService(j)) {
            return;
        }
        if ((userStarGift.flags & 8) != 0) {
            profileActivity = ChatActivity.of(j, userStarGift.msg_id);
        } else {
            Bundle bundle = new Bundle();
            if (j > 0) {
                bundle.putLong("user_id", j);
                if (j == j2) {
                    bundle.putBoolean("my_profile", true);
                    bundle.putBoolean("open_gifts", true);
                }
            } else {
                bundle.putLong("chat_id", -j);
            }
            profileActivity = new ProfileActivity(bundle);
        }
        safeLastFragment.presentFragment(profileActivity);
    }

    public static void lambda$showGiftSheet$98(Context context, int i, long j, BottomSheet[] bottomSheetArr) {
        BottomSheet bottomSheet = bottomSheetArr[0];
        Objects.requireNonNull(bottomSheet);
        new GiftSheet(context, i, j, new BottomSheet$$ExternalSyntheticLambda11(bottomSheet)).show();
    }

    public static void lambda$showGiftSheet$99(StarsIntroActivity starsIntroActivity, TL_stars.UserStarGift userStarGift) {
        BulletinFactory.of(starsIntroActivity).createSimpleBulletin(R.raw.stars_topup, LocaleController.getString(R.string.Gift2ConvertedTitle), LocaleController.formatPluralStringComma("Gift2Converted", (int) userStarGift.convert_stars)).show(true);
    }

    public static void lambda$showMediaPriceSheet$69(OutlineTextContainerView outlineTextContainerView, EditTextBoldCursor editTextBoldCursor, View view, boolean z) {
        outlineTextContainerView.animateSelection(z, !TextUtils.isEmpty(editTextBoldCursor.getText()));
    }

    public static void lambda$showMediaPriceSheet$70(Context context) {
        Browser.openUrl(context, LocaleController.getString(R.string.PaidContentInfoLink));
    }

    public static void lambda$showMediaPriceSheet$71(EditTextBoldCursor editTextBoldCursor, BottomSheet[] bottomSheetArr) {
        AndroidUtilities.hideKeyboard(editTextBoldCursor);
        bottomSheetArr[0].dismiss();
    }

    public static boolean lambda$showMediaPriceSheet$72(boolean[] zArr, Utilities.Callback2 callback2, ButtonWithCounterView buttonWithCounterView, final EditTextBoldCursor editTextBoldCursor, final BottomSheet[] bottomSheetArr, TextView textView, int i, KeyEvent keyEvent) {
        if (i != 5) {
            return false;
        }
        if (zArr[0]) {
            return true;
        }
        if (callback2 != null) {
            zArr[0] = true;
            buttonWithCounterView.setLoading(true);
            callback2.run(Long.valueOf(Long.parseLong(editTextBoldCursor.getText().toString())), new Runnable() {
                @Override
                public final void run() {
                    StarsIntroActivity.lambda$showMediaPriceSheet$71(EditTextBoldCursor.this, bottomSheetArr);
                }
            });
        } else {
            AndroidUtilities.hideKeyboard(editTextBoldCursor);
            bottomSheetArr[0].dismiss();
        }
        return true;
    }

    public static void lambda$showMediaPriceSheet$73(EditTextBoldCursor editTextBoldCursor, BottomSheet[] bottomSheetArr) {
        AndroidUtilities.hideKeyboard(editTextBoldCursor);
        bottomSheetArr[0].dismiss();
    }

    public static void lambda$showMediaPriceSheet$74(boolean[] zArr, Utilities.Callback2 callback2, final EditTextBoldCursor editTextBoldCursor, ButtonWithCounterView buttonWithCounterView, final BottomSheet[] bottomSheetArr, View view) {
        if (zArr[0]) {
            return;
        }
        if (callback2 == null) {
            AndroidUtilities.hideKeyboard(editTextBoldCursor);
            bottomSheetArr[0].dismiss();
        } else {
            String obj = editTextBoldCursor.getText().toString();
            zArr[0] = true;
            buttonWithCounterView.setLoading(true);
            callback2.run(Long.valueOf(TextUtils.isEmpty(obj) ? 0L : Long.parseLong(obj)), new Runnable() {
                @Override
                public final void run() {
                    StarsIntroActivity.lambda$showMediaPriceSheet$73(EditTextBoldCursor.this, bottomSheetArr);
                }
            });
        }
    }

    public static void lambda$showMediaPriceSheet$75(boolean[] zArr, EditTextBoldCursor editTextBoldCursor, BottomSheet[] bottomSheetArr) {
        zArr[0] = false;
        AndroidUtilities.hideKeyboard(editTextBoldCursor);
        bottomSheetArr[0].dismiss();
    }

    public static void lambda$showMediaPriceSheet$76(final boolean[] zArr, Utilities.Callback2 callback2, ButtonWithCounterView buttonWithCounterView, final EditTextBoldCursor editTextBoldCursor, final BottomSheet[] bottomSheetArr, View view) {
        if (zArr[0]) {
            return;
        }
        if (callback2 == null) {
            AndroidUtilities.hideKeyboard(editTextBoldCursor);
            bottomSheetArr[0].dismiss();
        } else {
            zArr[0] = true;
            buttonWithCounterView.setLoading(true);
            callback2.run(0L, new Runnable() {
                @Override
                public final void run() {
                    StarsIntroActivity.lambda$showMediaPriceSheet$75(zArr, editTextBoldCursor, bottomSheetArr);
                }
            });
        }
    }

    public static void lambda$showMediaPriceSheet$79(BottomSheet[] bottomSheetArr, final EditTextBoldCursor editTextBoldCursor) {
        bottomSheetArr[0].setFocusable(true);
        editTextBoldCursor.requestFocus();
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                AndroidUtilities.showKeyboard(EditTextBoldCursor.this);
            }
        });
    }

    public static void lambda$showSoldOutGiftSheet$109(BottomSheet[] bottomSheetArr, View view) {
        bottomSheetArr[0].dismiss();
    }

    public static void lambda$showSubscriptionSheet$46(Context context) {
        Browser.openUrl(context, LocaleController.getString(R.string.StarsTOSLink));
    }

    public static void lambda$showSubscriptionSheet$47(ButtonWithCounterView buttonWithCounterView, BottomSheet[] bottomSheetArr, int i, long j) {
        buttonWithCounterView.setLoading(false);
        BottomSheet bottomSheet = bottomSheetArr[0];
        if (bottomSheet != null) {
            bottomSheet.dismiss();
        }
        StarsController.getInstance(i).invalidateSubscriptions(true);
        BaseFragment safeLastFragment = LaunchActivity.getSafeLastFragment();
        if (safeLastFragment == null) {
            return;
        }
        safeLastFragment.presentFragment(ChatActivity.of(j));
    }

    public static void lambda$showSubscriptionSheet$48(final ButtonWithCounterView buttonWithCounterView, final BottomSheet[] bottomSheetArr, final int i, final long j, TLObject tLObject, TLRPC.TL_error tL_error) {
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                StarsIntroActivity.lambda$showSubscriptionSheet$47(ButtonWithCounterView.this, bottomSheetArr, i, j);
            }
        });
    }

    public static void lambda$showSubscriptionSheet$49(final ButtonWithCounterView buttonWithCounterView, TL_stars.StarsSubscription starsSubscription, final int i, final BottomSheet[] bottomSheetArr, final long j) {
        buttonWithCounterView.setLoading(true);
        TL_stars.TL_fulfillStarsSubscription tL_fulfillStarsSubscription = new TL_stars.TL_fulfillStarsSubscription();
        tL_fulfillStarsSubscription.subscription_id = starsSubscription.id;
        tL_fulfillStarsSubscription.peer = new TLRPC.TL_inputPeerSelf();
        ConnectionsManager.getInstance(i).sendRequest(tL_fulfillStarsSubscription, new RequestDelegate() {
            @Override
            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                StarsIntroActivity.lambda$showSubscriptionSheet$48(ButtonWithCounterView.this, bottomSheetArr, i, j, tLObject, tL_error);
            }
        });
    }

    public static void lambda$showSubscriptionSheet$50(final ButtonWithCounterView buttonWithCounterView, final int i, final TL_stars.StarsSubscription starsSubscription, final BottomSheet[] bottomSheetArr, final long j, Context context, Theme.ResourcesProvider resourcesProvider, boolean z, String str, View view) {
        if (buttonWithCounterView.isLoading()) {
            return;
        }
        StarsController starsController = StarsController.getInstance(i);
        Runnable runnable = new Runnable() {
            @Override
            public final void run() {
                StarsIntroActivity.lambda$showSubscriptionSheet$49(ButtonWithCounterView.this, starsSubscription, i, bottomSheetArr, j);
            }
        };
        if (starsController.balance.amount < starsSubscription.pricing.amount) {
            new StarsNeededSheet(context, resourcesProvider, starsSubscription.pricing.amount, z ? 8 : j < 0 ? 2 : 7, str, runnable).show();
        } else {
            runnable.run();
        }
    }

    public static void lambda$showSubscriptionSheet$51(ButtonWithCounterView buttonWithCounterView, BottomSheet[] bottomSheetArr, int i, TLObject tLObject, String str) {
        buttonWithCounterView.setLoading(false);
        BottomSheet bottomSheet = bottomSheetArr[0];
        if (bottomSheet != null) {
            bottomSheet.dismiss();
        }
        StarsController.getInstance(i).invalidateSubscriptions(true);
        BaseFragment safeLastFragment = LaunchActivity.getSafeLastFragment();
        if (safeLastFragment != null) {
            BulletinFactory.of(safeLastFragment).createUsersBulletin(Collections.singletonList(tLObject), LocaleController.getString(R.string.StarsSubscriptionRenewedToast), AndroidUtilities.replaceTags(LocaleController.formatString(R.string.StarsSubscriptionRenewedToastText, str))).show(false);
        }
    }

    public static void lambda$showSubscriptionSheet$52(final ButtonWithCounterView buttonWithCounterView, final BottomSheet[] bottomSheetArr, final int i, final TLObject tLObject, final String str, TLObject tLObject2, TLRPC.TL_error tL_error) {
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                StarsIntroActivity.lambda$showSubscriptionSheet$51(ButtonWithCounterView.this, bottomSheetArr, i, tLObject, str);
            }
        });
    }

    public static void lambda$showSubscriptionSheet$53(final ButtonWithCounterView buttonWithCounterView, TL_stars.StarsSubscription starsSubscription, final int i, final BottomSheet[] bottomSheetArr, final TLObject tLObject, final String str, View view) {
        if (buttonWithCounterView.isLoading()) {
            return;
        }
        buttonWithCounterView.setLoading(true);
        TL_stars.TL_changeStarsSubscription tL_changeStarsSubscription = new TL_stars.TL_changeStarsSubscription();
        tL_changeStarsSubscription.canceled = Boolean.FALSE;
        tL_changeStarsSubscription.peer = new TLRPC.TL_inputPeerSelf();
        tL_changeStarsSubscription.subscription_id = starsSubscription.id;
        ConnectionsManager.getInstance(i).sendRequest(tL_changeStarsSubscription, new RequestDelegate() {
            @Override
            public final void run(TLObject tLObject2, TLRPC.TL_error tL_error) {
                StarsIntroActivity.lambda$showSubscriptionSheet$52(ButtonWithCounterView.this, bottomSheetArr, i, tLObject, str, tLObject2, tL_error);
            }
        });
    }

    public static void lambda$showSubscriptionSheet$54(ButtonWithCounterView buttonWithCounterView, BottomSheet[] bottomSheetArr, int i, boolean z, TL_stars.StarsSubscription starsSubscription, boolean z2, TLObject tLObject) {
        buttonWithCounterView.setLoading(false);
        BottomSheet bottomSheet = bottomSheetArr[0];
        if (bottomSheet != null) {
            bottomSheet.dismiss();
        }
        StarsController.getInstance(i).invalidateSubscriptions(true);
        BaseFragment safeLastFragment = LaunchActivity.getSafeLastFragment();
        if (safeLastFragment != null) {
            BulletinFactory.of(safeLastFragment).createUsersBulletin(Collections.singletonList(tLObject), LocaleController.getString(R.string.StarsSubscriptionCancelledToast), AndroidUtilities.replaceTags((!z || TextUtils.isEmpty(starsSubscription.title)) ? (!z2 || TextUtils.isEmpty(starsSubscription.title)) ? LocaleController.formatString(R.string.StarsSubscriptionCancelledToastText, LocaleController.formatDateChat(starsSubscription.until_date)) : LocaleController.formatString(R.string.StarsSubscriptionCancelledBotToastText, LocaleController.formatDateChat(starsSubscription.until_date), starsSubscription.title) : LocaleController.formatString(R.string.StarsSubscriptionCancelledBizToastText, LocaleController.formatDateChat(starsSubscription.until_date), starsSubscription.title))).show(false);
        }
    }

    public static void lambda$showSubscriptionSheet$55(final ButtonWithCounterView buttonWithCounterView, final BottomSheet[] bottomSheetArr, final int i, final boolean z, final TL_stars.StarsSubscription starsSubscription, final boolean z2, final TLObject tLObject, TLObject tLObject2, TLRPC.TL_error tL_error) {
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                StarsIntroActivity.lambda$showSubscriptionSheet$54(ButtonWithCounterView.this, bottomSheetArr, i, z, starsSubscription, z2, tLObject);
            }
        });
    }

    public static void lambda$showSubscriptionSheet$56(final ButtonWithCounterView buttonWithCounterView, final TL_stars.StarsSubscription starsSubscription, final int i, final BottomSheet[] bottomSheetArr, final boolean z, final boolean z2, final TLObject tLObject, View view) {
        if (buttonWithCounterView.isLoading()) {
            return;
        }
        buttonWithCounterView.setLoading(true);
        TL_stars.TL_changeStarsSubscription tL_changeStarsSubscription = new TL_stars.TL_changeStarsSubscription();
        tL_changeStarsSubscription.canceled = Boolean.TRUE;
        tL_changeStarsSubscription.peer = new TLRPC.TL_inputPeerSelf();
        tL_changeStarsSubscription.subscription_id = starsSubscription.id;
        ConnectionsManager.getInstance(i).sendRequest(tL_changeStarsSubscription, new RequestDelegate() {
            @Override
            public final void run(TLObject tLObject2, TLRPC.TL_error tL_error) {
                StarsIntroActivity.lambda$showSubscriptionSheet$55(ButtonWithCounterView.this, bottomSheetArr, i, z, starsSubscription, z2, tLObject, tLObject2, tL_error);
            }
        });
    }

    public static void lambda$showSubscriptionSheet$57(BaseFragment baseFragment, long j, TLRPC.Chat chat) {
        BulletinFactory.of(baseFragment).createSimpleBulletin(R.raw.stars_send, LocaleController.getString(R.string.StarsSubscriptionCompleted), AndroidUtilities.replaceTags(LocaleController.formatPluralString("StarsSubscriptionCompletedText", (int) j, chat.title))).show(true);
    }

    public static void lambda$showSubscriptionSheet$58(Long l, int i, final long j) {
        BaseFragment safeLastFragment = LaunchActivity.getSafeLastFragment();
        if (safeLastFragment == null) {
            return;
        }
        final ChatActivity of = ChatActivity.of(l.longValue());
        safeLastFragment.presentFragment(of);
        final TLRPC.Chat chat = MessagesController.getInstance(i).getChat(Long.valueOf(-l.longValue()));
        if (chat != null) {
            AndroidUtilities.runOnUIThread(new Runnable() {
                @Override
                public final void run() {
                    StarsIntroActivity.lambda$showSubscriptionSheet$57(BaseFragment.this, j, chat);
                }
            }, 250L);
        }
    }

    public static void lambda$showSubscriptionSheet$59(final int i, final long j, String str, final Long l) {
        if (!"paid".equals(str) || l.longValue() == 0) {
            return;
        }
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                StarsIntroActivity.lambda$showSubscriptionSheet$58(l, i, j);
            }
        });
    }

    public static void lambda$showSubscriptionSheet$60(ButtonWithCounterView buttonWithCounterView, TLObject tLObject, BottomSheet[] bottomSheetArr, Theme.ResourcesProvider resourcesProvider, final int i, TLRPC.TL_messages_checkChatInvite tL_messages_checkChatInvite) {
        buttonWithCounterView.setLoading(false);
        if (!(tLObject instanceof TLRPC.ChatInvite)) {
            BulletinFactory.of(bottomSheetArr[0].topBulletinContainer, resourcesProvider).createErrorBulletin(LocaleController.getString(R.string.LinkHashExpired)).show(false);
            return;
        }
        TLRPC.ChatInvite chatInvite = (TLRPC.ChatInvite) tLObject;
        TL_stars.TL_starsSubscriptionPricing tL_starsSubscriptionPricing = chatInvite.subscription_pricing;
        if (tL_starsSubscriptionPricing == null) {
            BulletinFactory.of(bottomSheetArr[0].topBulletinContainer, resourcesProvider).createErrorBulletin(LocaleController.getString(R.string.UnknownError)).show(false);
        } else {
            final long j = tL_starsSubscriptionPricing.amount;
            StarsController.getInstance(i).subscribeTo(tL_messages_checkChatInvite.hash, chatInvite, new Utilities.Callback2() {
                @Override
                public final void run(Object obj, Object obj2) {
                    StarsIntroActivity.lambda$showSubscriptionSheet$59(i, j, (String) obj, (Long) obj2);
                }
            });
        }
    }

    public static void lambda$showSubscriptionSheet$61(final ButtonWithCounterView buttonWithCounterView, final BottomSheet[] bottomSheetArr, final Theme.ResourcesProvider resourcesProvider, final int i, final TLRPC.TL_messages_checkChatInvite tL_messages_checkChatInvite, final TLObject tLObject, TLRPC.TL_error tL_error) {
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                StarsIntroActivity.lambda$showSubscriptionSheet$60(ButtonWithCounterView.this, tLObject, bottomSheetArr, resourcesProvider, i, tL_messages_checkChatInvite);
            }
        });
    }

    public static void lambda$showSubscriptionSheet$62(final ButtonWithCounterView buttonWithCounterView, TL_stars.StarsSubscription starsSubscription, final int i, final BottomSheet[] bottomSheetArr, final Theme.ResourcesProvider resourcesProvider, boolean[] zArr, Context context, View view) {
        if (buttonWithCounterView.isLoading()) {
            return;
        }
        buttonWithCounterView.setLoading(true);
        if (starsSubscription.chat_invite_hash != null) {
            final TLRPC.TL_messages_checkChatInvite tL_messages_checkChatInvite = new TLRPC.TL_messages_checkChatInvite();
            tL_messages_checkChatInvite.hash = starsSubscription.chat_invite_hash;
            ConnectionsManager.getInstance(i).sendRequest(tL_messages_checkChatInvite, new RequestDelegate() {
                @Override
                public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                    StarsIntroActivity.lambda$showSubscriptionSheet$61(ButtonWithCounterView.this, bottomSheetArr, resourcesProvider, i, tL_messages_checkChatInvite, tLObject, tL_error);
                }
            });
        } else if (starsSubscription.invoice_slug != null) {
            zArr[0] = true;
            Browser.openUrl(context, Uri.parse("https://t.me/$" + starsSubscription.invoice_slug), true, false, false, new Browser.Progress() {
                @Override
                public void end() {
                    ButtonWithCounterView.this.setLoading(false);
                }
            }, null, false, true, false);
        }
    }

    public static void lambda$showSubscriptionSheet$63(int i, NotificationCenter.NotificationCenterDelegate notificationCenterDelegate, DialogInterface dialogInterface) {
        NotificationCenter.getInstance(i).removeObserver(notificationCenterDelegate, NotificationCenter.starSubscriptionsLoaded);
    }

    public static void lambda$showTransactionSheet$21(boolean z, long j, TL_stars.StarsTransaction starsTransaction, int i, Theme.ResourcesProvider resourcesProvider, final BackupImageView backupImageView, final LinearLayout linearLayout, View view) {
        final long peerDialogId = z ? j : DialogObject.getPeerDialogId(starsTransaction.peer.peer);
        ArrayList arrayList = new ArrayList();
        for (int i2 = 0; i2 < starsTransaction.extended_media.size(); i2++) {
            TLRPC.MessageMedia messageMedia = starsTransaction.extended_media.get(i2);
            TLRPC.TL_message tL_message = new TLRPC.TL_message();
            tL_message.id = starsTransaction.msg_id;
            tL_message.dialog_id = peerDialogId;
            TLRPC.TL_peerChannel tL_peerChannel = new TLRPC.TL_peerChannel();
            tL_message.from_id = tL_peerChannel;
            long j2 = -peerDialogId;
            tL_peerChannel.channel_id = j2;
            TLRPC.TL_peerChannel tL_peerChannel2 = new TLRPC.TL_peerChannel();
            tL_message.peer_id = tL_peerChannel2;
            tL_peerChannel2.channel_id = j2;
            tL_message.date = starsTransaction.date;
            tL_message.flags |= 512;
            tL_message.media = messageMedia;
            tL_message.noforwards = true;
            arrayList.add(new MessageObject(i, tL_message, false, false));
        }
        if (arrayList.isEmpty()) {
            return;
        }
        PhotoViewer.getInstance().setParentActivity(LaunchActivity.getLastFragment(), resourcesProvider);
        PhotoViewer.getInstance().openPhoto(arrayList, 0, peerDialogId, 0L, 0L, new PhotoViewer.EmptyPhotoViewerProvider() {
            @Override
            public boolean forceAllInGroup() {
                return true;
            }

            @Override
            public PhotoViewer.PlaceProviderObject getPlaceForPhoto(MessageObject messageObject, TLRPC.FileLocation fileLocation, int i3, boolean z2) {
                ImageReceiver imageReceiver = BackupImageView.this.getImageReceiver();
                int[] iArr = new int[2];
                BackupImageView.this.getLocationInWindow(iArr);
                PhotoViewer.PlaceProviderObject placeProviderObject = new PhotoViewer.PlaceProviderObject();
                placeProviderObject.viewX = iArr[0];
                placeProviderObject.viewY = iArr[1] - (Build.VERSION.SDK_INT >= 21 ? 0 : AndroidUtilities.statusBarHeight);
                placeProviderObject.parentView = linearLayout;
                placeProviderObject.animatingImageView = null;
                placeProviderObject.imageReceiver = imageReceiver;
                if (z2) {
                    placeProviderObject.thumb = imageReceiver.getBitmapSafe();
                }
                placeProviderObject.radius = imageReceiver.getRoundRadius(true);
                placeProviderObject.dialogId = peerDialogId;
                placeProviderObject.clipTopAddition = 0;
                placeProviderObject.clipBottomAddition = 0;
                return placeProviderObject;
            }
        });
    }

    public static void lambda$showTransactionSheet$22(Context context, BottomSheet[] bottomSheetArr) {
        BottomSheet bottomSheet;
        BaseFragment baseFragment;
        StarAppsSheet starAppsSheet = new StarAppsSheet(context);
        if (!AndroidUtilities.isTablet() && !AndroidUtilities.hasDialogOnTop(bottomSheetArr[0].attachedFragment) && (bottomSheet = bottomSheetArr[0]) != null && (baseFragment = bottomSheet.attachedFragment) != null) {
            starAppsSheet.makeAttached(baseFragment);
        }
        starAppsSheet.show();
    }

    public static void lambda$showTransactionSheet$23(BottomSheet[] bottomSheetArr, TL_stars.StarsTransaction starsTransaction, long j) {
        bottomSheetArr[0].dismiss();
        BaseFragment safeLastFragment = LaunchActivity.getSafeLastFragment();
        if (safeLastFragment != null) {
            safeLastFragment.presentFragment((starsTransaction.flags & 8192) != 0 ? ChatActivity.of(j, starsTransaction.giveaway_post_id) : ChatActivity.of(j));
        }
    }

    public static void lambda$showTransactionSheet$24(Context context, int i, long j, BottomSheet[] bottomSheetArr) {
        BottomSheet bottomSheet = bottomSheetArr[0];
        Objects.requireNonNull(bottomSheet);
        new GiftSheet(context, i, j, new BottomSheet$$ExternalSyntheticLambda11(bottomSheet)).show();
    }

    public static void lambda$showTransactionSheet$25(BottomSheet[] bottomSheetArr, int i) {
        bottomSheetArr[0].dismiss();
        BaseFragment safeLastFragment = LaunchActivity.getSafeLastFragment();
        if (safeLastFragment != null) {
            Bundle bundle = new Bundle();
            bundle.putLong("user_id", UserConfig.getInstance(i).getClientUserId());
            bundle.putBoolean("my_profile", true);
            bundle.putBoolean("open_gifts", true);
            safeLastFragment.presentFragment(new ProfileActivity(bundle));
        }
    }

    public static void lambda$showTransactionSheet$26(BottomSheet[] bottomSheetArr, int i) {
        bottomSheetArr[0].dismiss();
        BaseFragment safeLastFragment = LaunchActivity.getSafeLastFragment();
        if (safeLastFragment != null) {
            Bundle bundle = new Bundle();
            bundle.putLong("user_id", UserConfig.getInstance(i).getClientUserId());
            bundle.putBoolean("my_profile", true);
            bundle.putBoolean("open_gifts", true);
            safeLastFragment.presentFragment(new ProfileActivity(bundle));
        }
    }

    public static void lambda$showTransactionSheet$27(BottomSheet[] bottomSheetArr, TL_stars.StarsTransaction starsTransaction, long j) {
        bottomSheetArr[0].dismiss();
        BaseFragment safeLastFragment = LaunchActivity.getSafeLastFragment();
        if (safeLastFragment != null) {
            safeLastFragment.presentFragment((starsTransaction.flags & 8192) != 0 ? ChatActivity.of(j, starsTransaction.giveaway_post_id) : ChatActivity.of(j));
        }
    }

    public static void lambda$showTransactionSheet$28(Context context, int i, long j, BottomSheet[] bottomSheetArr) {
        BottomSheet bottomSheet = bottomSheetArr[0];
        Objects.requireNonNull(bottomSheet);
        new GiftSheet(context, i, j, new BottomSheet$$ExternalSyntheticLambda11(bottomSheet)).show();
    }

    public static void lambda$showTransactionSheet$29(BottomSheet[] bottomSheetArr, long j) {
        bottomSheetArr[0].dismiss();
        BaseFragment safeLastFragment = LaunchActivity.getSafeLastFragment();
        if (safeLastFragment != null) {
            safeLastFragment.presentFragment(new AffiliateProgramFragment(j));
        }
    }

    public static void lambda$showTransactionSheet$30(BottomSheet[] bottomSheetArr, long j) {
        bottomSheetArr[0].dismiss();
        BaseFragment safeLastFragment = LaunchActivity.getSafeLastFragment();
        if (safeLastFragment != null) {
            safeLastFragment.presentFragment(ProfileActivity.of(j));
        }
    }

    public static void lambda$showTransactionSheet$31(BottomSheet[] bottomSheetArr, long j) {
        bottomSheetArr[0].dismiss();
        BaseFragment safeLastFragment = LaunchActivity.getSafeLastFragment();
        if (safeLastFragment != null) {
            safeLastFragment.presentFragment(ProfileActivity.of(j));
        }
    }

    public static void lambda$showTransactionSheet$32(BottomSheet[] bottomSheetArr, Context context, int i, long j, Theme.ResourcesProvider resourcesProvider, TL_payments.connectedBotStarRef connectedbotstarref) {
        bottomSheetArr[0].dismiss();
        ChannelAffiliateProgramsFragment.showShareAffiliateAlert(context, i, connectedbotstarref, j, resourcesProvider);
    }

    public static void lambda$showTransactionSheet$33(final int i, final Context context, final long j, long j2, final BottomSheet[] bottomSheetArr, final Theme.ResourcesProvider resourcesProvider) {
        BotStarsController.getInstance(i).getConnectedBot(context, j, j2, new Utilities.Callback() {
            @Override
            public final void run(Object obj) {
                StarsIntroActivity.lambda$showTransactionSheet$32(bottomSheetArr, context, i, j, resourcesProvider, (TL_payments.connectedBotStarRef) obj);
            }
        });
    }

    public static void lambda$showTransactionSheet$34(BottomSheet[] bottomSheetArr, long j) {
        bottomSheetArr[0].dismiss();
        BaseFragment safeLastFragment = LaunchActivity.getSafeLastFragment();
        if (safeLastFragment != null) {
            safeLastFragment.presentFragment(ProfileActivity.of(j));
        }
    }

    public static void lambda$showTransactionSheet$35(BottomSheet[] bottomSheetArr, TL_stars.StarsTransaction starsTransaction, long j) {
        bottomSheetArr[0].dismiss();
        BaseFragment safeLastFragment = LaunchActivity.getSafeLastFragment();
        if (safeLastFragment != null) {
            safeLastFragment.presentFragment((starsTransaction.flags & 8192) != 0 ? ChatActivity.of(j, starsTransaction.giveaway_post_id) : ChatActivity.of(j));
        }
    }

    public static void lambda$showTransactionSheet$36(BottomSheet[] bottomSheetArr, int i) {
        bottomSheetArr[0].dismiss();
        BaseFragment safeLastFragment = LaunchActivity.getSafeLastFragment();
        if (safeLastFragment != null) {
            Bundle bundle = new Bundle();
            bundle.putLong("user_id", UserConfig.getInstance(i).getClientUserId());
            bundle.putBoolean("my_profile", true);
            safeLastFragment.presentFragment(new ProfileActivity(bundle));
        }
    }

    public static void lambda$showTransactionSheet$37(BottomSheet[] bottomSheetArr, TL_stars.StarsTransaction starsTransaction, long j) {
        bottomSheetArr[0].dismiss();
        BaseFragment safeLastFragment = LaunchActivity.getSafeLastFragment();
        if (safeLastFragment != null) {
            safeLastFragment.presentFragment((starsTransaction.flags & 8192) != 0 ? ChatActivity.of(j, starsTransaction.giveaway_post_id) : ChatActivity.of(j));
        }
    }

    public static void lambda$showTransactionSheet$38(BottomSheet[] bottomSheetArr, long j, Context context) {
        bottomSheetArr[0].dismiss();
        if (UserObject.isService(j)) {
            Browser.openUrl(context, LocaleController.getString(R.string.StarsTransactionUnknownLink));
            return;
        }
        BaseFragment safeLastFragment = LaunchActivity.getSafeLastFragment();
        if (safeLastFragment != null) {
            safeLastFragment.presentFragment(ChatActivity.of(j));
        }
    }

    public static void lambda$showTransactionSheet$39(BottomSheet[] bottomSheetArr, long j, Context context) {
        bottomSheetArr[0].dismiss();
        if (UserObject.isService(j)) {
            Browser.openUrl(context, LocaleController.getString(R.string.StarsTransactionUnknownLink));
            return;
        }
        BaseFragment safeLastFragment = LaunchActivity.getSafeLastFragment();
        if (safeLastFragment != null) {
            safeLastFragment.presentFragment(ChatActivity.of(j));
        }
    }

    public static void lambda$showTransactionSheet$40(BottomSheet[] bottomSheetArr, long j, TL_stars.StarsTransaction starsTransaction) {
        bottomSheetArr[0].dismiss();
        BaseFragment safeLastFragment = LaunchActivity.getSafeLastFragment();
        if (safeLastFragment != null) {
            Bundle bundle = new Bundle();
            bundle.putLong("chat_id", -j);
            bundle.putInt("message_id", starsTransaction.msg_id);
            safeLastFragment.presentFragment(new ChatActivity(bundle));
        }
    }

    public static void lambda$showTransactionSheet$42(TL_stars.StarsTransaction starsTransaction, BottomSheet[] bottomSheetArr, Theme.ResourcesProvider resourcesProvider, View view) {
        AndroidUtilities.addToClipboard(starsTransaction.id);
        BulletinFactory.of(bottomSheetArr[0].topBulletinContainer, resourcesProvider).createSimpleBulletin(R.raw.copy, LocaleController.getString(R.string.StarsTransactionIDCopied)).show(false);
    }

    public static void lambda$showTransactionSheet$43(Context context) {
        Browser.openUrl(context, LocaleController.getString(R.string.StarsTOSLink));
    }

    public static void lambda$showTransactionSheet$44(Context context, TL_stars.StarsTransaction starsTransaction, View view) {
        Browser.openUrl(context, starsTransaction.transaction_url);
    }

    public static void lambda$showTransactionSheet$45(BottomSheet[] bottomSheetArr, View view) {
        bottomSheetArr[0].dismiss();
    }

    public static StarParticlesView makeParticlesView(Context context, int i, int i2) {
        return new AnonymousClass2(context, i, i2);
    }

    public static org.telegram.ui.ActionBar.BottomSheet openConfirmPurchaseSheet(final android.content.Context r32, org.telegram.ui.ActionBar.Theme.ResourcesProvider r33, int r34, org.telegram.messenger.MessageObject r35, long r36, java.lang.String r38, long r39, org.telegram.tgnet.TLRPC.WebDocument r41, int r42, final org.telegram.messenger.Utilities.Callback r43, final java.lang.Runnable r44) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Stars.StarsIntroActivity.openConfirmPurchaseSheet(android.content.Context, org.telegram.ui.ActionBar.Theme$ResourcesProvider, int, org.telegram.messenger.MessageObject, long, java.lang.String, long, org.telegram.tgnet.TLRPC$WebDocument, int, org.telegram.messenger.Utilities$Callback, java.lang.Runnable):org.telegram.ui.ActionBar.BottomSheet");
    }

    public static BottomSheet openStarsChannelInviteSheet(final Context context, Theme.ResourcesProvider resourcesProvider, int i, TLRPC.ChatInvite chatInvite, final Utilities.Callback callback, final Runnable runnable) {
        BottomSheet.Builder builder = new BottomSheet.Builder(context, false, resourcesProvider);
        LinearLayout linearLayout = new LinearLayout(context);
        linearLayout.setOrientation(1);
        linearLayout.setPadding(AndroidUtilities.dp(16.0f), 0, AndroidUtilities.dp(16.0f), AndroidUtilities.dp(8.0f));
        FrameLayout frameLayout = new FrameLayout(context);
        frameLayout.addView(makeParticlesView(context, 40, 0), LayoutHelper.createFrame(-1, -1.0f));
        BackupImageView backupImageView = new BackupImageView(context);
        backupImageView.setRoundRadius(AndroidUtilities.dp(80.0f));
        AvatarDrawable avatarDrawable = new AvatarDrawable();
        avatarDrawable.setPeerColor(chatInvite.color);
        avatarDrawable.setText(chatInvite.title);
        TLRPC.Photo photo = chatInvite.photo;
        if (photo != null) {
            backupImageView.setImage(ImageLocation.getForPhoto(FileLoader.getClosestPhotoSizeWithSize(photo.sizes, AndroidUtilities.dp(80.0f)), chatInvite.photo), "80_80", avatarDrawable, chatInvite);
        } else {
            backupImageView.setImageDrawable(avatarDrawable);
        }
        frameLayout.addView(backupImageView, LayoutHelper.createFrame(80, 80, 17));
        Drawable drawable = context.getResources().getDrawable(R.drawable.star_small_outline);
        int i2 = Theme.key_dialogBackground;
        drawable.setColorFilter(new PorterDuffColorFilter(Theme.getColor(i2, resourcesProvider), PorterDuff.Mode.SRC_IN));
        Drawable drawable2 = context.getResources().getDrawable(R.drawable.star_small_inner);
        ImageView imageView = new ImageView(context);
        imageView.setImageDrawable(drawable);
        frameLayout.addView(imageView, LayoutHelper.createFrame(26, 26, 17));
        imageView.setTranslationX(AndroidUtilities.dp(26.0f));
        imageView.setTranslationY(AndroidUtilities.dp(26.0f));
        imageView.setScaleX(1.2f);
        imageView.setScaleY(1.2f);
        ImageView imageView2 = new ImageView(context);
        imageView2.setImageDrawable(drawable2);
        frameLayout.addView(imageView2, LayoutHelper.createFrame(26, 26, 17));
        imageView2.setTranslationX(AndroidUtilities.dp(26.0f));
        imageView2.setTranslationY(AndroidUtilities.dp(26.0f));
        final StarsBalanceView starsBalanceView = new StarsBalanceView(context, i);
        ScaleStateListAnimator.apply(starsBalanceView);
        starsBalanceView.setOnClickListener(new View.OnClickListener() {
            @Override
            public final void onClick(View view) {
                StarsIntroActivity.lambda$openStarsChannelInviteSheet$11(StarsIntroActivity.StarsBalanceView.this, view);
            }
        });
        frameLayout.addView(starsBalanceView, LayoutHelper.createFrame(-2, -2.0f, 53, 0.0f, 0.0f, -8.0f, 0.0f));
        linearLayout.addView(frameLayout, LayoutHelper.createLinear(-1, 117, 7));
        TextView textView = new TextView(context);
        textView.setTextSize(1, 20.0f);
        textView.setTypeface(AndroidUtilities.bold());
        int i3 = Theme.key_dialogTextBlack;
        textView.setTextColor(Theme.getColor(i3, resourcesProvider));
        textView.setText(LocaleController.getString(R.string.StarsSubscribeTitle));
        textView.setGravity(17);
        linearLayout.addView(textView, LayoutHelper.createLinear(-2, -2, 1, 0, 8, 0, 0));
        TextView textView2 = new TextView(context);
        textView2.setTextSize(1, 14.0f);
        textView2.setTextColor(Theme.getColor(i3, resourcesProvider));
        TL_stars.TL_starsSubscriptionPricing tL_starsSubscriptionPricing = chatInvite.subscription_pricing;
        int i4 = tL_starsSubscriptionPricing.period;
        if (i4 == 2592000) {
            textView2.setText(AndroidUtilities.replaceTags(LocaleController.formatPluralString("StarsSubscribeText", (int) tL_starsSubscriptionPricing.amount, chatInvite.title)));
        } else {
            textView2.setText(AndroidUtilities.replaceTags(LocaleController.formatPluralString("StarsSubscribeTextTest", (int) tL_starsSubscriptionPricing.amount, chatInvite.title, i4 == 300 ? "5 minutes" : "a minute")));
        }
        textView2.setMaxWidth(HintView2.cutInFancyHalf(textView2.getText(), textView2.getPaint()));
        textView2.setGravity(17);
        linearLayout.addView(textView2, LayoutHelper.createLinear(-2, -2, 1, 0, 6, 0, 22));
        if (!TextUtils.isEmpty(chatInvite.about)) {
            TextView textView3 = new TextView(context);
            textView3.setTextSize(1, 14.0f);
            textView3.setTextColor(Theme.getColor(i3, resourcesProvider));
            textView3.setText(Emoji.replaceEmoji(chatInvite.about, textView3.getPaint().getFontMetricsInt(), false));
            textView3.setGravity(17);
            linearLayout.addView(textView3, LayoutHelper.createLinear(-2, -2, 1, 0, 6, 0, 22));
        }
        final ButtonWithCounterView buttonWithCounterView = new ButtonWithCounterView(context, resourcesProvider);
        buttonWithCounterView.setText(LocaleController.getString(R.string.StarsSubscribeButton), false);
        linearLayout.addView(buttonWithCounterView, LayoutHelper.createLinear(-1, 48));
        LinkSpanDrawable.LinksTextView linksTextView = new LinkSpanDrawable.LinksTextView(context, resourcesProvider);
        linksTextView.setText(AndroidUtilities.replaceSingleTag(LocaleController.getString(R.string.StarsSubscribeInfo), new Runnable() {
            @Override
            public final void run() {
                StarsIntroActivity.lambda$openStarsChannelInviteSheet$12(context);
            }
        }));
        linksTextView.setGravity(17);
        linksTextView.setTextSize(1, 13.0f);
        linksTextView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteGrayText4, resourcesProvider));
        linksTextView.setLinkTextColor(Theme.getColor(Theme.key_chat_messageLinkIn, resourcesProvider));
        linearLayout.addView(linksTextView, LayoutHelper.createLinear(-1, -2, 49, 14, 14, 14, 6));
        builder.setCustomView(linearLayout);
        final BottomSheet create = builder.create();
        buttonWithCounterView.setOnClickListener(new View.OnClickListener() {
            @Override
            public final void onClick(View view) {
                StarsIntroActivity.lambda$openStarsChannelInviteSheet$15(Utilities.Callback.this, create, buttonWithCounterView, view);
            }
        });
        create.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public final void onDismiss(DialogInterface dialogInterface) {
                StarsIntroActivity.lambda$openStarsChannelInviteSheet$16(runnable, dialogInterface);
            }
        });
        create.fixNavigationBar(Theme.getColor(i2, resourcesProvider));
        BaseFragment safeLastFragment = LaunchActivity.getSafeLastFragment();
        if (!AndroidUtilities.isTablet() && safeLastFragment != null && !AndroidUtilities.hasDialogOnTop(safeLastFragment)) {
            create.makeAttached(safeLastFragment);
        }
        create.show();
        return create;
    }

    public static SpannableStringBuilder replaceStars(CharSequence charSequence) {
        return replaceStars(charSequence, 1.13f);
    }

    public static SpannableStringBuilder replaceStars(CharSequence charSequence, float f) {
        if (charSequence == null) {
            return null;
        }
        SpannableStringBuilder spannableStringBuilder = !(charSequence instanceof SpannableStringBuilder) ? new SpannableStringBuilder(charSequence) : (SpannableStringBuilder) charSequence;
        SpannableString spannableString = new SpannableString("⭐ ");
        ColoredImageSpan coloredImageSpan = new ColoredImageSpan(R.drawable.msg_premium_liststar);
        coloredImageSpan.setScale(f, f);
        spannableString.setSpan(coloredImageSpan, 0, spannableString.length() - 1, 33);
        AndroidUtilities.replaceMultipleCharSequence("⭐️", spannableStringBuilder, "⭐");
        AndroidUtilities.replaceMultipleCharSequence("⭐ ", spannableStringBuilder, "⭐");
        AndroidUtilities.replaceMultipleCharSequence("⭐", spannableStringBuilder, spannableString);
        AndroidUtilities.replaceMultipleCharSequence("XTR ", spannableStringBuilder, "XTR");
        AndroidUtilities.replaceMultipleCharSequence("XTR", spannableStringBuilder, spannableString);
        return spannableStringBuilder;
    }

    public static SpannableStringBuilder replaceStars(CharSequence charSequence, ColoredImageSpan[] coloredImageSpanArr) {
        ColoredImageSpan coloredImageSpan;
        if (charSequence == null) {
            return null;
        }
        SpannableStringBuilder spannableStringBuilder = !(charSequence instanceof SpannableStringBuilder) ? new SpannableStringBuilder(charSequence) : (SpannableStringBuilder) charSequence;
        if (coloredImageSpanArr == null || (coloredImageSpan = coloredImageSpanArr[0]) == null) {
            coloredImageSpan = new ColoredImageSpan(R.drawable.msg_premium_liststar);
            coloredImageSpan.setScale(1.13f, 1.13f);
        }
        if (coloredImageSpanArr != null) {
            coloredImageSpanArr[0] = coloredImageSpan;
        }
        SpannableString spannableString = new SpannableString("⭐ ");
        spannableString.setSpan(coloredImageSpan, 0, spannableString.length() - 1, 33);
        AndroidUtilities.replaceMultipleCharSequence("⭐️", spannableStringBuilder, "⭐");
        AndroidUtilities.replaceMultipleCharSequence("⭐ ", spannableStringBuilder, "⭐");
        AndroidUtilities.replaceMultipleCharSequence("⭐", spannableStringBuilder, spannableString);
        AndroidUtilities.replaceMultipleCharSequence("XTR ", spannableStringBuilder, "XTR");
        AndroidUtilities.replaceMultipleCharSequence("XTR", spannableStringBuilder, spannableString);
        return spannableStringBuilder;
    }

    public static SpannableStringBuilder replaceStarsWithPlain(CharSequence charSequence, float f) {
        if (charSequence == null) {
            return null;
        }
        SpannableStringBuilder spannableStringBuilder = !(charSequence instanceof SpannableStringBuilder) ? new SpannableStringBuilder(charSequence) : (SpannableStringBuilder) charSequence;
        SpannableString spannableString = new SpannableString("⭐ ");
        ColoredImageSpan coloredImageSpan = new ColoredImageSpan(R.drawable.star_small_inner);
        coloredImageSpan.recolorDrawable = false;
        coloredImageSpan.setScale(f, f);
        spannableString.setSpan(coloredImageSpan, 0, spannableString.length() - 1, 33);
        AndroidUtilities.replaceMultipleCharSequence("⭐️", spannableStringBuilder, "⭐");
        AndroidUtilities.replaceMultipleCharSequence("⭐ ", spannableStringBuilder, "⭐");
        AndroidUtilities.replaceMultipleCharSequence("⭐", spannableStringBuilder, spannableString);
        AndroidUtilities.replaceMultipleCharSequence("XTR ", spannableStringBuilder, "XTR");
        AndroidUtilities.replaceMultipleCharSequence("XTR", spannableStringBuilder, spannableString);
        return spannableStringBuilder;
    }

    public static Runnable setGiftImage(View view, final ImageReceiver imageReceiver, final int i) {
        final boolean[] zArr = new boolean[1];
        final int currentAccount = imageReceiver.getCurrentAccount();
        final Runnable runnable = new Runnable() {
            @Override
            public final void run() {
                StarsIntroActivity.lambda$setGiftImage$17(currentAccount, i, imageReceiver, zArr);
            }
        };
        runnable.run();
        final Runnable listen = NotificationCenter.getInstance(currentAccount).listen(view, NotificationCenter.didUpdatePremiumGiftStickers, new Utilities.Callback() {
            @Override
            public final void run(Object obj) {
                runnable.run();
            }
        });
        final Runnable listen2 = NotificationCenter.getInstance(currentAccount).listen(view, NotificationCenter.diceStickersDidLoad, new Utilities.Callback() {
            @Override
            public final void run(Object obj) {
                runnable.run();
            }
        });
        return new Runnable() {
            @Override
            public final void run() {
                StarsIntroActivity.lambda$setGiftImage$20(listen, listen2);
            }
        };
    }

    public static Runnable setGiftImage(View view, ImageReceiver imageReceiver, long j) {
        return setGiftImage(view, imageReceiver, j <= 1000 ? 2 : j < 2500 ? 3 : 4);
    }

    public static void setGiftImage(ImageReceiver imageReceiver, TL_stars.StarGift starGift, int i) {
        TLRPC.Document document;
        if (starGift == null || (document = starGift.sticker) == null) {
            imageReceiver.clearImage();
            return;
        }
        TLRPC.PhotoSize closestPhotoSizeWithSize = FileLoader.getClosestPhotoSizeWithSize(document.thumbs, i);
        SvgHelper.SvgDrawable svgThumb = DocumentObject.getSvgThumb(starGift.sticker.thumbs, Theme.key_windowBackgroundGray, 0.35f);
        imageReceiver.setImage(ImageLocation.getForDocument(starGift.sticker), i + "_" + i, ImageLocation.getForDocument(closestPhotoSizeWithSize, starGift.sticker), i + "_" + i, svgThumb, 0L, null, starGift, 0);
    }

    public static org.telegram.ui.ActionBar.BottomSheet showActionGiftSheet(final android.content.Context r43, final int r44, final long r45, boolean r47, int r48, final int r49, final org.telegram.tgnet.TLRPC.TL_messageActionStarGift r50, final org.telegram.ui.ActionBar.Theme.ResourcesProvider r51) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Stars.StarsIntroActivity.showActionGiftSheet(android.content.Context, int, long, boolean, int, int, org.telegram.tgnet.TLRPC$TL_messageActionStarGift, org.telegram.ui.ActionBar.Theme$ResourcesProvider):org.telegram.ui.ActionBar.BottomSheet");
    }

    public static BottomSheet showBoostsSheet(final Context context, int i, final long j, final TL_stories.Boost boost, Theme.ResourcesProvider resourcesProvider) {
        if (boost == null || context == null) {
            return null;
        }
        BottomSheet.Builder builder = new BottomSheet.Builder(context, false, resourcesProvider);
        final BottomSheet[] bottomSheetArr = new BottomSheet[1];
        LinearLayout linearLayout = new LinearLayout(context);
        linearLayout.setOrientation(1);
        linearLayout.setPadding(0, AndroidUtilities.dp(20.0f), 0, AndroidUtilities.dp(4.0f));
        linearLayout.setClipChildren(false);
        linearLayout.setClipToPadding(false);
        FrameLayout frameLayout = new FrameLayout(context);
        frameLayout.setClipChildren(false);
        frameLayout.setClipToPadding(false);
        linearLayout.addView(frameLayout, LayoutHelper.createLinear(-1, 150, 7, 0, 0, 0, 10));
        StarParticlesView makeParticlesView = makeParticlesView(context, 70, 0);
        frameLayout.addView(makeParticlesView, LayoutHelper.createFrame(-1, -1.0f));
        final GLIconTextureView gLIconTextureView = new GLIconTextureView(context, 1, 2);
        GLIconRenderer gLIconRenderer = gLIconTextureView.mRenderer;
        gLIconRenderer.colorKey1 = Theme.key_starsGradient1;
        gLIconRenderer.colorKey2 = Theme.key_starsGradient2;
        gLIconRenderer.updateColors();
        gLIconTextureView.setStarParticlesView(makeParticlesView);
        frameLayout.addView(gLIconTextureView, LayoutHelper.createFrame(170, 170.0f, 17, 0.0f, 32.0f, 0.0f, 24.0f));
        gLIconTextureView.setPaused(false);
        TextView textView = new TextView(context);
        textView.setTextColor(Theme.getColor(Theme.key_dialogTextBlack, resourcesProvider));
        textView.setTextSize(1, 20.0f);
        textView.setTypeface(AndroidUtilities.bold());
        textView.setGravity(17);
        textView.setText(LocaleController.formatPluralStringSpaced("BoostStars", (int) boost.stars));
        linearLayout.addView(textView, LayoutHelper.createLinear(-1, -2, 17, 20, 0, 20, 4));
        TextView textView2 = new TextView(context);
        textView2.setBackground(Theme.createRoundRectDrawable(AndroidUtilities.dp(20.0f), -6915073));
        textView2.setTextColor(-1);
        textView2.setTextSize(1, 11.33f);
        textView2.setPadding(AndroidUtilities.dp(4.0f), 0, AndroidUtilities.dp(8.33f), 0);
        textView2.setGravity(17);
        textView2.setTypeface(AndroidUtilities.bold());
        StringBuilder sb = new StringBuilder();
        sb.append("x");
        int i2 = boost.multiplier;
        if (i2 == 0) {
            i2 = 1;
        }
        sb.append(LocaleController.formatPluralStringSpaced("BoostingBoostsCount", i2));
        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(sb.toString());
        ColoredImageSpan coloredImageSpan = new ColoredImageSpan(R.drawable.mini_boost_badge, 2);
        coloredImageSpan.translate(0.0f, AndroidUtilities.dp(0.66f));
        spannableStringBuilder.setSpan(coloredImageSpan, 0, 1, 33);
        textView2.setText(spannableStringBuilder);
        linearLayout.addView(textView2, LayoutHelper.createLinear(-2, 20, 17, 20, 4, 20, 4));
        TableView tableView = new TableView(context, resourcesProvider);
        tableView.addRowUser(LocaleController.getString(R.string.BoostFrom), i, j, new Runnable() {
            @Override
            public final void run() {
                StarsIntroActivity.lambda$showBoostsSheet$64(bottomSheetArr, j);
            }
        });
        tableView.addRow(LocaleController.getString(R.string.BoostGift), LocaleController.formatPluralString("BoostStars", (int) boost.stars, new Object[0]));
        if (boost.giveaway_msg_id != 0) {
            tableView.addRowLink(LocaleController.getString(R.string.BoostReason), LocaleController.getString(R.string.BoostReasonGiveaway), new Runnable() {
                @Override
                public final void run() {
                    StarsIntroActivity.lambda$showBoostsSheet$65(bottomSheetArr, j, boost);
                }
            });
        }
        String string = LocaleController.getString(R.string.BoostDate);
        int i3 = R.string.formatDateAtTime;
        tableView.addRow(string, LocaleController.formatString(i3, LocaleController.getInstance().getFormatterGiveawayCard().format(new Date(boost.date * 1000)), LocaleController.getInstance().getFormatterDay().format(new Date(boost.date * 1000))));
        tableView.addRow(LocaleController.getString(R.string.BoostUntil), LocaleController.formatString(i3, LocaleController.getInstance().getFormatterGiveawayCard().format(new Date(boost.expires * 1000)), LocaleController.getInstance().getFormatterDay().format(new Date(boost.expires * 1000))));
        linearLayout.addView(tableView, LayoutHelper.createLinear(-1, -2, 16.0f, 17.0f, 16.0f, 0.0f));
        LinkSpanDrawable.LinksTextView linksTextView = new LinkSpanDrawable.LinksTextView(context, resourcesProvider);
        linksTextView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteGrayText2, resourcesProvider));
        linksTextView.setLinkTextColor(Theme.getColor(Theme.key_chat_messageLinkIn, resourcesProvider));
        linksTextView.setTextSize(1, 14.0f);
        linksTextView.setText(AndroidUtilities.replaceSingleTag(LocaleController.getString(R.string.StarsTransactionTOS), new Runnable() {
            @Override
            public final void run() {
                StarsIntroActivity.lambda$showBoostsSheet$66(context);
            }
        }));
        linksTextView.setGravity(17);
        linearLayout.addView(linksTextView, LayoutHelper.createLinear(-1, -2, 14.0f, 15.0f, 14.0f, 7.0f));
        ButtonWithCounterView buttonWithCounterView = new ButtonWithCounterView(context, resourcesProvider);
        buttonWithCounterView.setText(LocaleController.getString(R.string.OK), false);
        buttonWithCounterView.setOnClickListener(new View.OnClickListener() {
            @Override
            public final void onClick(View view) {
                StarsIntroActivity.lambda$showBoostsSheet$67(bottomSheetArr, view);
            }
        });
        linearLayout.addView(buttonWithCounterView, LayoutHelper.createLinear(-1, 48, 16.0f, 8.0f, 16.0f, 0.0f));
        builder.setCustomView(linearLayout);
        BottomSheet create = builder.create();
        bottomSheetArr[0] = create;
        create.useBackgroundTopPadding = false;
        create.fixNavigationBar();
        BaseFragment safeLastFragment = LaunchActivity.getSafeLastFragment();
        if (!AndroidUtilities.isTablet() && !AndroidUtilities.hasDialogOnTop(safeLastFragment)) {
            bottomSheetArr[0].makeAttached(safeLastFragment);
        }
        gLIconTextureView.setPaused(false);
        bottomSheetArr[0].show();
        bottomSheetArr[0].setOnDismissListener(new Runnable() {
            @Override
            public final void run() {
                GLIconTextureView.this.setPaused(true);
            }
        });
        return bottomSheetArr[0];
    }

    public static org.telegram.ui.ActionBar.BottomSheet showGiftSheet(final android.content.Context r44, final int r45, final long r46, boolean r48, final org.telegram.tgnet.tl.TL_stars.UserStarGift r49, final org.telegram.ui.ActionBar.Theme.ResourcesProvider r50) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Stars.StarsIntroActivity.showGiftSheet(android.content.Context, int, long, boolean, org.telegram.tgnet.tl.TL_stars$UserStarGift, org.telegram.ui.ActionBar.Theme$ResourcesProvider):org.telegram.ui.ActionBar.BottomSheet");
    }

    public static BottomSheet showMediaPriceSheet(final Context context, final long j, final boolean z, final Utilities.Callback2 callback2, Theme.ResourcesProvider resourcesProvider) {
        BottomSheet.Builder builder = new BottomSheet.Builder(context, false, resourcesProvider);
        LinearLayout linearLayout = new LinearLayout(context);
        linearLayout.setOrientation(1);
        linearLayout.setClipChildren(false);
        linearLayout.setClipToPadding(false);
        linearLayout.setPadding(AndroidUtilities.dp(16.0f), AndroidUtilities.dp(16.0f), AndroidUtilities.dp(16.0f), AndroidUtilities.dp(8.0f));
        TextView textView = new TextView(context);
        textView.setTypeface(AndroidUtilities.bold());
        textView.setText(LocaleController.getString(R.string.PaidContentTitle));
        textView.setTextSize(1, 20.0f);
        int i = Theme.key_windowBackgroundWhiteBlackText;
        textView.setTextColor(Theme.getColor(i, resourcesProvider));
        linearLayout.addView(textView, LayoutHelper.createLinear(-1, -2, 4.0f, 0.0f, 4.0f, 18.0f));
        final EditTextBoldCursor editTextBoldCursor = new EditTextBoldCursor(context);
        final OutlineTextContainerView outlineTextContainerView = new OutlineTextContainerView(context, resourcesProvider);
        outlineTextContainerView.setForceForceUseCenter(true);
        outlineTextContainerView.setText(LocaleController.getString(R.string.PaidContentPriceTitle));
        outlineTextContainerView.setLeftPadding(AndroidUtilities.dp(36.0f));
        editTextBoldCursor.setTextColor(Theme.getColor(i, resourcesProvider));
        editTextBoldCursor.setCursorSize(AndroidUtilities.dp(20.0f));
        editTextBoldCursor.setCursorWidth(1.5f);
        ButtonWithCounterView buttonWithCounterView = null;
        editTextBoldCursor.setBackground(null);
        editTextBoldCursor.setTextSize(1, 18.0f);
        editTextBoldCursor.setMaxLines(1);
        int dp = AndroidUtilities.dp(16.0f);
        editTextBoldCursor.setPadding(AndroidUtilities.dp(6.0f), dp, dp, dp);
        editTextBoldCursor.setInputType(2);
        editTextBoldCursor.setTypeface(Typeface.DEFAULT);
        editTextBoldCursor.setSelectAllOnFocus(true);
        editTextBoldCursor.setHighlightColor(Theme.getColor(Theme.key_chat_inTextSelectionHighlight, resourcesProvider));
        editTextBoldCursor.setHandlesColor(Theme.getColor(Theme.key_chat_TextSelectionCursor, resourcesProvider));
        editTextBoldCursor.setGravity(LocaleController.isRTL ? 5 : 3);
        editTextBoldCursor.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public final void onFocusChange(View view, boolean z2) {
                StarsIntroActivity.lambda$showMediaPriceSheet$69(OutlineTextContainerView.this, editTextBoldCursor, view, z2);
            }
        });
        LinearLayout linearLayout2 = new LinearLayout(context);
        linearLayout2.setOrientation(0);
        ImageView imageView = new ImageView(context);
        imageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        imageView.setImageResource(R.drawable.star_small_inner);
        linearLayout2.addView(imageView, LayoutHelper.createLinear(-2, -2, 0.0f, 19, 14, 0, 0, 0));
        linearLayout2.addView(editTextBoldCursor, LayoutHelper.createLinear(-1, -2, 1.0f, 119));
        outlineTextContainerView.attachEditText(editTextBoldCursor);
        outlineTextContainerView.addView(linearLayout2, LayoutHelper.createFrame(-1, -2, 48));
        linearLayout.addView(outlineTextContainerView, LayoutHelper.createLinear(-1, -2));
        final TextView textView2 = new TextView(context);
        textView2.setTextSize(1, 16.0f);
        textView2.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteGrayText3));
        outlineTextContainerView.addView(textView2, LayoutHelper.createFrame(-2, -2.0f, 21, 0.0f, 0.0f, 14.0f, 0.0f));
        LinkSpanDrawable.LinksTextView linksTextView = new LinkSpanDrawable.LinksTextView(context);
        linksTextView.setText(AndroidUtilities.replaceArrows(AndroidUtilities.replaceSingleTag(LocaleController.getString(R.string.PaidContentInfo), new Runnable() {
            @Override
            public final void run() {
                StarsIntroActivity.lambda$showMediaPriceSheet$70(context);
            }
        }), true));
        linksTextView.setTextSize(1, 12.0f);
        linksTextView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteGrayText2, resourcesProvider));
        linksTextView.setLinkTextColor(Theme.getColor(Theme.key_chat_messageLinkIn, resourcesProvider));
        linearLayout.addView(linksTextView, LayoutHelper.createLinear(-1, -2, 14.0f, 3.0f, 14.0f, 24.0f));
        final ButtonWithCounterView buttonWithCounterView2 = new ButtonWithCounterView(context, resourcesProvider);
        buttonWithCounterView2.setText(LocaleController.getString(j > 0 ? R.string.PaidContentUpdateButton : R.string.PaidContentButton), false);
        linearLayout.addView(buttonWithCounterView2, LayoutHelper.createLinear(-1, 48));
        if (j > 0 && z) {
            buttonWithCounterView = new ButtonWithCounterView(context, false, resourcesProvider);
            buttonWithCounterView.setText(LocaleController.getString(R.string.PaidContentClearButton), false, false);
            linearLayout.addView(buttonWithCounterView, LayoutHelper.createLinear(-1, 48, 0.0f, 4.0f, 0.0f, 0.0f));
        }
        final ButtonWithCounterView buttonWithCounterView3 = buttonWithCounterView;
        builder.setCustomView(linearLayout);
        final BottomSheet[] bottomSheetArr = {builder.create()};
        editTextBoldCursor.setText(j <= 0 ? "" : Long.toString(j));
        editTextBoldCursor.addTextChangedListener(new TextWatcher() {
            private boolean ignore;
            private int shakeDp = 2;

            @Override
            public void afterTextChanged(android.text.Editable r10) {
                throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Stars.StarsIntroActivity.AnonymousClass13.afterTextChanged(android.text.Editable):void");
            }

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i2, int i3, int i4) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i2, int i3, int i4) {
            }
        });
        final boolean[] zArr = {false};
        editTextBoldCursor.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public final boolean onEditorAction(TextView textView3, int i2, KeyEvent keyEvent) {
                boolean lambda$showMediaPriceSheet$72;
                lambda$showMediaPriceSheet$72 = StarsIntroActivity.lambda$showMediaPriceSheet$72(zArr, callback2, buttonWithCounterView2, editTextBoldCursor, bottomSheetArr, textView3, i2, keyEvent);
                return lambda$showMediaPriceSheet$72;
            }
        });
        buttonWithCounterView2.setOnClickListener(new View.OnClickListener() {
            @Override
            public final void onClick(View view) {
                StarsIntroActivity.lambda$showMediaPriceSheet$74(zArr, callback2, editTextBoldCursor, buttonWithCounterView2, bottomSheetArr, view);
            }
        });
        if (buttonWithCounterView3 != null) {
            buttonWithCounterView3.setOnClickListener(new View.OnClickListener() {
                @Override
                public final void onClick(View view) {
                    StarsIntroActivity.lambda$showMediaPriceSheet$76(zArr, callback2, buttonWithCounterView3, editTextBoldCursor, bottomSheetArr, view);
                }
            });
        }
        bottomSheetArr[0].fixNavigationBar();
        bottomSheetArr[0].setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public final void onDismiss(DialogInterface dialogInterface) {
                AndroidUtilities.hideKeyboard(EditTextBoldCursor.this);
            }
        });
        bottomSheetArr[0].show();
        BaseFragment lastFragment = LaunchActivity.getLastFragment();
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                StarsIntroActivity.lambda$showMediaPriceSheet$79(bottomSheetArr, editTextBoldCursor);
            }
        }, lastFragment instanceof ChatActivity ? ((ChatActivity) lastFragment).needEnterText() : false ? 200L : 80L);
        return bottomSheetArr[0];
    }

    public static BottomSheet showSoldOutGiftSheet(Context context, int i, TL_stars.StarGift starGift, Theme.ResourcesProvider resourcesProvider) {
        if (starGift == null || context == null) {
            return null;
        }
        BottomSheet.Builder builder = new BottomSheet.Builder(context, false, resourcesProvider);
        LinearLayout linearLayout = new LinearLayout(context);
        linearLayout.setOrientation(1);
        linearLayout.setPadding(AndroidUtilities.dp(16.0f), AndroidUtilities.dp(20.0f), AndroidUtilities.dp(16.0f), AndroidUtilities.dp(8.0f));
        linearLayout.setClipChildren(false);
        linearLayout.setClipToPadding(false);
        BackupImageView backupImageView = new BackupImageView(context);
        setGiftImage(backupImageView.getImageReceiver(), starGift, 160);
        linearLayout.addView(backupImageView, LayoutHelper.createLinear(160, 160, 17, 0, -8, 0, 10));
        TextView textView = new TextView(context);
        textView.setTextColor(Theme.getColor(Theme.key_dialogTextBlack, resourcesProvider));
        textView.setTextSize(1, 20.0f);
        textView.setTypeface(AndroidUtilities.bold());
        textView.setGravity(17);
        textView.setText(LocaleController.getString(R.string.Gift2SoldOutSheetTitle));
        linearLayout.addView(textView, LayoutHelper.createLinear(-1, -2, 17, 20, 0, 20, 4));
        TextView textView2 = new TextView(context);
        textView2.setTextSize(1, 14.0f);
        textView2.setTypeface(AndroidUtilities.bold());
        textView2.setGravity(17);
        textView2.setTextColor(Theme.getColor(Theme.key_text_RedBold, resourcesProvider));
        textView2.setText(LocaleController.getString(R.string.Gift2SoldOutSheetSubtitle));
        linearLayout.addView(textView2, LayoutHelper.createLinear(-1, -2, 17, 20, 0, 20, 4));
        TableView tableView = new TableView(context, resourcesProvider);
        if (starGift.first_sale_date != 0) {
            tableView.addRowDateTime(LocaleController.getString(R.string.Gift2SoldOutSheetFirstSale), starGift.first_sale_date);
        }
        if (starGift.last_sale_date != 0) {
            tableView.addRowDateTime(LocaleController.getString(R.string.Gift2SoldOutSheetLastSale), starGift.last_sale_date);
        }
        tableView.addRow(LocaleController.getString(R.string.Gift2SoldOutSheetValue), replaceStarsWithPlain("⭐️ " + LocaleController.formatNumber(starGift.stars, ','), 0.8f));
        if (starGift.limited) {
            addAvailabilityRow(tableView, i, starGift, resourcesProvider);
        }
        linearLayout.addView(tableView, LayoutHelper.createLinear(-1, -2, 0.0f, 17.0f, 0.0f, 12.0f));
        ButtonWithCounterView buttonWithCounterView = new ButtonWithCounterView(context, resourcesProvider);
        buttonWithCounterView.setText(LocaleController.getString(R.string.OK), false);
        linearLayout.addView(buttonWithCounterView, LayoutHelper.createLinear(-1, 48));
        builder.setCustomView(linearLayout);
        final BottomSheet[] bottomSheetArr = {builder.create()};
        bottomSheetArr[0].useBackgroundTopPadding = false;
        buttonWithCounterView.setOnClickListener(new View.OnClickListener() {
            @Override
            public final void onClick(View view) {
                StarsIntroActivity.lambda$showSoldOutGiftSheet$109(bottomSheetArr, view);
            }
        });
        bottomSheetArr[0].fixNavigationBar();
        BaseFragment safeLastFragment = LaunchActivity.getSafeLastFragment();
        if (!AndroidUtilities.isTablet() && !AndroidUtilities.hasDialogOnTop(safeLastFragment)) {
            bottomSheetArr[0].makeAttached(safeLastFragment);
        }
        bottomSheetArr[0].show();
        return bottomSheetArr[0];
    }

    public static org.telegram.ui.ActionBar.BottomSheet showSubscriptionSheet(final android.content.Context r39, final int r40, final org.telegram.tgnet.tl.TL_stars.StarsSubscription r41, final org.telegram.ui.ActionBar.Theme.ResourcesProvider r42) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Stars.StarsIntroActivity.showSubscriptionSheet(android.content.Context, int, org.telegram.tgnet.tl.TL_stars$StarsSubscription, org.telegram.ui.ActionBar.Theme$ResourcesProvider):org.telegram.ui.ActionBar.BottomSheet");
    }

    public static BottomSheet showTransactionSheet(Context context, int i, int i2, TLRPC.Peer peer, TLRPC.Peer peer2, TLRPC.TL_messageActionGiftStars tL_messageActionGiftStars, Theme.ResourcesProvider resourcesProvider) {
        TL_stars.StarsTransaction starsTransaction = new TL_stars.StarsTransaction();
        starsTransaction.title = null;
        starsTransaction.description = null;
        starsTransaction.photo = null;
        TL_stars.TL_starsTransactionPeer tL_starsTransactionPeer = new TL_stars.TL_starsTransactionPeer();
        starsTransaction.peer = tL_starsTransactionPeer;
        tL_starsTransactionPeer.peer = peer;
        starsTransaction.date = i2;
        starsTransaction.stars = new TL_stars.StarsAmount(tL_messageActionGiftStars.stars);
        starsTransaction.id = tL_messageActionGiftStars.transaction_id;
        starsTransaction.gift = true;
        starsTransaction.sent_by = peer;
        starsTransaction.received_by = peer2;
        return showTransactionSheet(context, false, 0L, i, starsTransaction, resourcesProvider);
    }

    public static BottomSheet showTransactionSheet(Context context, int i, int i2, TLRPC.Peer peer, TLRPC.Peer peer2, TLRPC.TL_messageActionPrizeStars tL_messageActionPrizeStars, Theme.ResourcesProvider resourcesProvider) {
        TL_stars.StarsTransaction starsTransaction = new TL_stars.StarsTransaction();
        starsTransaction.title = null;
        starsTransaction.description = null;
        starsTransaction.photo = null;
        TL_stars.TL_starsTransactionPeer tL_starsTransactionPeer = new TL_stars.TL_starsTransactionPeer();
        starsTransaction.peer = tL_starsTransactionPeer;
        tL_starsTransactionPeer.peer = tL_messageActionPrizeStars.boost_peer;
        starsTransaction.date = i2;
        starsTransaction.stars = new TL_stars.StarsAmount(tL_messageActionPrizeStars.stars);
        starsTransaction.id = tL_messageActionPrizeStars.transaction_id;
        starsTransaction.gift = true;
        starsTransaction.flags |= 8192;
        starsTransaction.giveaway_post_id = tL_messageActionPrizeStars.giveaway_msg_id;
        starsTransaction.sent_by = peer;
        starsTransaction.received_by = peer2;
        return showTransactionSheet(context, false, 0L, i, starsTransaction, resourcesProvider);
    }

    public static BottomSheet showTransactionSheet(Context context, int i, int i2, TLRPC.TL_messageActionPaymentRefunded tL_messageActionPaymentRefunded, Theme.ResourcesProvider resourcesProvider) {
        TL_stars.StarsTransaction starsTransaction = new TL_stars.StarsTransaction();
        starsTransaction.title = null;
        starsTransaction.description = null;
        starsTransaction.photo = null;
        TL_stars.TL_starsTransactionPeer tL_starsTransactionPeer = new TL_stars.TL_starsTransactionPeer();
        starsTransaction.peer = tL_starsTransactionPeer;
        tL_starsTransactionPeer.peer = tL_messageActionPaymentRefunded.peer;
        starsTransaction.date = i2;
        starsTransaction.stars = new TL_stars.StarsAmount(tL_messageActionPaymentRefunded.total_amount);
        starsTransaction.id = tL_messageActionPaymentRefunded.charge.id;
        starsTransaction.refund = true;
        return showTransactionSheet(context, false, 0L, i, starsTransaction, resourcesProvider);
    }

    public static BottomSheet showTransactionSheet(Context context, boolean z, int i, TLRPC.TL_payments_paymentReceiptStars tL_payments_paymentReceiptStars, Theme.ResourcesProvider resourcesProvider) {
        TL_stars.StarsTransaction starsTransaction = new TL_stars.StarsTransaction();
        starsTransaction.title = tL_payments_paymentReceiptStars.title;
        starsTransaction.description = tL_payments_paymentReceiptStars.description;
        starsTransaction.photo = tL_payments_paymentReceiptStars.photo;
        TL_stars.TL_starsTransactionPeer tL_starsTransactionPeer = new TL_stars.TL_starsTransactionPeer();
        starsTransaction.peer = tL_starsTransactionPeer;
        tL_starsTransactionPeer.peer = MessagesController.getInstance(i).getPeer(tL_payments_paymentReceiptStars.bot_id);
        starsTransaction.date = tL_payments_paymentReceiptStars.date;
        starsTransaction.stars = new TL_stars.StarsAmount(-tL_payments_paymentReceiptStars.total_amount);
        starsTransaction.id = tL_payments_paymentReceiptStars.transaction_id;
        return showTransactionSheet(context, z, 0L, i, starsTransaction, resourcesProvider);
    }

    public static org.telegram.ui.ActionBar.BottomSheet showTransactionSheet(final android.content.Context r54, final boolean r55, final long r56, final int r58, final org.telegram.tgnet.tl.TL_stars.StarsTransaction r59, final org.telegram.ui.ActionBar.Theme.ResourcesProvider r60) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Stars.StarsIntroActivity.showTransactionSheet(android.content.Context, boolean, long, int, org.telegram.tgnet.tl.TL_stars$StarsTransaction, org.telegram.ui.ActionBar.Theme$ResourcesProvider):org.telegram.ui.ActionBar.BottomSheet");
    }

    private void updateBalance() {
        StarsController starsController = StarsController.getInstance(this.currentAccount);
        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder();
        spannableStringBuilder.append((CharSequence) this.starBalanceIcon);
        spannableStringBuilder.append(formatStarsAmount(starsController.getBalance(), 0.66f, ' '));
        this.starBalanceTextView.setText(spannableStringBuilder);
        this.buyButton.setText(LocaleController.getString(starsController.getBalance().amount > 0 ? R.string.StarsBuyMore : R.string.StarsBuy), true);
    }

    public boolean attachedTransactionsLayout() {
        StarsTransactionsLayout starsTransactionsLayout = this.transactionsLayout;
        if (starsTransactionsLayout == null || !(starsTransactionsLayout.getParent() instanceof View)) {
            return false;
        }
        return this.listView.getHeight() - ((View) this.transactionsLayout.getParent()).getBottom() >= 0;
    }

    @Override
    protected RecyclerView.Adapter createAdapter() {
        UniversalAdapter universalAdapter = new UniversalAdapter(this.listView, getContext(), this.currentAccount, this.classGuid, true, new Utilities.Callback2() {
            @Override
            public final void run(Object obj, Object obj2) {
                StarsIntroActivity.this.fillItems((ArrayList) obj, (UniversalAdapter) obj2);
            }
        }, getResourceProvider()) {
            @Override
            public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
                if (i != 42) {
                    return super.onCreateViewHolder(viewGroup, i);
                }
                HeaderCell headerCell = new HeaderCell(StarsIntroActivity.this.getContext(), Theme.key_windowBackgroundWhiteBlueHeader, 21, 0, false, ((BaseFragment) StarsIntroActivity.this).resourceProvider);
                headerCell.setHeight(25);
                return new RecyclerListView.Holder(headerCell);
            }
        };
        this.adapter = universalAdapter;
        return universalAdapter;
    }

    @Override
    protected GradientHeaderActivity.ContentView createContentView() {
        return new NestedFrameLayout(getContext());
    }

    @Override
    public StarParticlesView createParticlesView() {
        return makeParticlesView(getContext(), 75, 1);
    }

    @Override
    public View createView(final Context context) {
        this.useFillLastLayoutManager = false;
        this.particlesViewHeight = AndroidUtilities.dp(238.0f);
        this.transactionsLayout = new StarsTransactionsLayout(context, this.currentAccount, 0L, getClassGuid(), getResourceProvider());
        View view = new View(context) {
            @Override
            protected void onMeasure(int i, int i2) {
                int i3;
                StarsIntroActivity starsIntroActivity = StarsIntroActivity.this;
                if (starsIntroActivity.isLandscapeMode) {
                    i3 = (starsIntroActivity.statusBarHeight + ((BaseFragment) starsIntroActivity).actionBar.getMeasuredHeight()) - AndroidUtilities.dp(16.0f);
                } else {
                    int dp = AndroidUtilities.dp(140.0f);
                    StarsIntroActivity starsIntroActivity2 = StarsIntroActivity.this;
                    int i4 = dp + starsIntroActivity2.statusBarHeight;
                    if (starsIntroActivity2.backgroundView.getMeasuredHeight() + AndroidUtilities.dp(24.0f) > i4) {
                        i3 = AndroidUtilities.dp(24.0f) + StarsIntroActivity.this.backgroundView.getMeasuredHeight();
                    } else {
                        i3 = i4;
                    }
                }
                super.onMeasure(i, View.MeasureSpec.makeMeasureSpec((int) (i3 - (((GradientHeaderActivity) StarsIntroActivity.this).yOffset * 2.5f)), 1073741824));
            }
        };
        this.emptyLayout = view;
        view.setBackgroundColor(Theme.getColor(Theme.key_dialogBackgroundGray));
        super.createView(context);
        FrameLayout frameLayout = new FrameLayout(context);
        this.aboveTitleView = frameLayout;
        frameLayout.setClickable(true);
        GLIconTextureView gLIconTextureView = new GLIconTextureView(context, 1, 2);
        this.iconTextureView = gLIconTextureView;
        GLIconRenderer gLIconRenderer = gLIconTextureView.mRenderer;
        gLIconRenderer.colorKey1 = Theme.key_starsGradient1;
        gLIconRenderer.colorKey2 = Theme.key_starsGradient2;
        gLIconRenderer.updateColors();
        this.iconTextureView.setStarParticlesView(this.particlesView);
        this.aboveTitleView.addView(this.iconTextureView, LayoutHelper.createFrame(190, 190.0f, 17, 0.0f, 32.0f, 0.0f, 24.0f));
        configureHeader(LocaleController.getString(R.string.TelegramStars), AndroidUtilities.replaceArrows(AndroidUtilities.replaceSingleTag(LocaleController.getString(R.string.TelegramStarsInfo2), new Runnable() {
            @Override
            public final void run() {
                StarsIntroActivity.lambda$createView$0(context);
            }
        }), true), this.aboveTitleView, null);
        this.listView.setOverScrollMode(2);
        DefaultItemAnimator defaultItemAnimator = new DefaultItemAnimator();
        defaultItemAnimator.setSupportsChangeAnimations(false);
        defaultItemAnimator.setDelayAnimations(false);
        defaultItemAnimator.setInterpolator(CubicBezierInterpolator.EASE_OUT_QUINT);
        defaultItemAnimator.setDurations(350L);
        this.listView.setItemAnimator(defaultItemAnimator);
        this.listView.setOnItemClickListener(new RecyclerListView.OnItemClickListener() {
            @Override
            public final void onItemClick(View view2, int i) {
                StarsIntroActivity.this.lambda$createView$1(view2, i);
            }
        });
        FireworksOverlay fireworksOverlay = new FireworksOverlay(getContext());
        this.fireworksOverlay = fireworksOverlay;
        this.contentView.addView(fireworksOverlay, LayoutHelper.createFrame(-1, -1.0f));
        StarsController.getInstance(this.currentAccount);
        LinearLayout linearLayout = new LinearLayout(getContext());
        this.balanceLayout = linearLayout;
        linearLayout.setOrientation(1);
        this.balanceLayout.setPadding(0, 0, 0, AndroidUtilities.dp(10.0f));
        AnimatedTextView animatedTextView = new AnimatedTextView(getContext(), false, true, false);
        this.starBalanceTextView = animatedTextView;
        animatedTextView.setTypeface(AndroidUtilities.bold());
        this.starBalanceTextView.setTextSize(AndroidUtilities.dp(32.0f));
        this.starBalanceTextView.setGravity(17);
        this.starBalanceTextView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText, this.resourceProvider));
        this.starBalanceIcon = new SpannableStringBuilder("S");
        ImageReceiverSpan imageReceiverSpan = new ImageReceiverSpan(this.starBalanceTextView, this.currentAccount, 42.0f);
        ImageReceiver imageReceiver = imageReceiverSpan.imageReceiver;
        int i = R.raw.star_reaction;
        imageReceiver.setImageBitmap(new RLottieDrawable(i, "s" + i, AndroidUtilities.dp(42.0f), AndroidUtilities.dp(42.0f)));
        imageReceiverSpan.imageReceiver.setAutoRepeat(2);
        imageReceiverSpan.enableShadow(false);
        imageReceiverSpan.translate((float) (-AndroidUtilities.dp(3.0f)), 0.0f);
        this.starBalanceIcon.setSpan(imageReceiverSpan, 0, 1, 33);
        this.balanceLayout.addView(this.starBalanceTextView, LayoutHelper.createFrame(-1, 40.0f, 17, 24.0f, 0.0f, 24.0f, 0.0f));
        TextView textView = new TextView(getContext());
        this.starBalanceTitleView = textView;
        textView.setTextSize(1, 14.0f);
        this.starBalanceTitleView.setGravity(17);
        this.starBalanceTitleView.setText(LocaleController.getString(R.string.YourStarsBalance));
        this.starBalanceTitleView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteGrayText2, this.resourceProvider));
        this.balanceLayout.addView(this.starBalanceTitleView, LayoutHelper.createFrame(-1, -2.0f, 17, 24.0f, 0.0f, 24.0f, 0.0f));
        ButtonWithCounterView buttonWithCounterView = new ButtonWithCounterView(getContext(), this.resourceProvider);
        this.buyButton = buttonWithCounterView;
        buttonWithCounterView.setOnClickListener(new View.OnClickListener() {
            @Override
            public final void onClick(View view2) {
                StarsIntroActivity.this.lambda$createView$2(context, view2);
            }
        });
        this.balanceLayout.addView(this.buyButton, LayoutHelper.createFrame(-1, 48.0f, 17, 20.0f, 17.0f, 20.0f, 0.0f));
        this.giftButton = new ButtonWithCounterView(getContext(), false, this.resourceProvider);
        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder();
        spannableStringBuilder.append((CharSequence) "G  ");
        spannableStringBuilder.setSpan(new ColoredImageSpan(R.drawable.menu_stars_gift), 0, 1, 33);
        spannableStringBuilder.append((CharSequence) LocaleController.getString(R.string.TelegramStarsGift));
        this.giftButton.setText(spannableStringBuilder, false);
        this.giftButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public final void onClick(View view2) {
                StarsIntroActivity.this.lambda$createView$3(view2);
            }
        });
        this.balanceLayout.addView(this.giftButton, LayoutHelper.createFrame(-1, 48.0f, 17, 20.0f, 8.0f, 20.0f, 0.0f));
        updateBalance();
        UniversalAdapter universalAdapter = this.adapter;
        if (universalAdapter != null) {
            universalAdapter.update(false);
        }
        return this.fragmentView;
    }

    @Override
    public void didReceivedNotification(int r2, int r3, java.lang.Object... r4) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Stars.StarsIntroActivity.didReceivedNotification(int, int, java.lang.Object[]):void");
    }

    @Override
    protected boolean drawActionBarShadow() {
        return !attachedTransactionsLayout();
    }

    public void fillItems(ArrayList arrayList, UniversalAdapter universalAdapter) {
        UItem accent;
        if (getContext() == null) {
            return;
        }
        StarsController starsController = StarsController.getInstance(this.currentAccount);
        arrayList.add(UItem.asFullyCustom(getHeader(getContext())));
        arrayList.add(UItem.asCustom(this.balanceLayout));
        ButtonWithCounterView buttonWithCounterView = this.giftButton;
        if (buttonWithCounterView != null) {
            buttonWithCounterView.setVisibility(getMessagesController().starsGiftsEnabled ? 0 : 8);
        }
        arrayList.add(UItem.asShadow(null));
        if (getMessagesController().starrefConnectAllowed) {
            arrayList.add(AffiliateProgramFragment.ColorfulTextCell.Factory.as(-4, getThemedColor(Theme.key_color_green), R.drawable.filled_earn_stars, ChatEditActivity.applyNewSpan(LocaleController.getString(R.string.UserAffiliateProgramRowTitle)), LocaleController.getString(R.string.UserAffiliateProgramRowText)));
            arrayList.add(UItem.asShadow(null));
        }
        if (starsController.hasSubscriptions()) {
            arrayList.add(UItem.asHeader(LocaleController.getString(R.string.StarMySubscriptions)));
            for (int i = 0; i < starsController.subscriptions.size(); i++) {
                arrayList.add(StarsSubscriptionView.Factory.asSubscription((TL_stars.StarsSubscription) starsController.subscriptions.get(i)));
            }
            if (starsController.isLoadingSubscriptions()) {
                accent = UItem.asFlicker(arrayList.size(), 33);
            } else {
                if (!starsController.didFullyLoadSubscriptions()) {
                    accent = UItem.asButton(-3, R.drawable.arrow_more, LocaleController.getString(R.string.StarMySubscriptionsExpand)).accent();
                }
                arrayList.add(UItem.asShadow(null));
            }
            arrayList.add(accent);
            arrayList.add(UItem.asShadow(null));
        }
        boolean hasTransactions = starsController.hasTransactions();
        this.hadTransactions = hasTransactions;
        arrayList.add(hasTransactions ? UItem.asFullscreenCustom(this.transactionsLayout, ActionBar.getCurrentActionBarHeight() + AndroidUtilities.statusBarHeight) : UItem.asCustom(this.emptyLayout));
    }

    @Override
    protected View getHeader(Context context) {
        return super.getHeader(context);
    }

    @Override
    public int getNavigationBarColor() {
        return Theme.getColor(Theme.key_dialogBackgroundGray);
    }

    @Override
    public boolean onFragmentCreate() {
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.starOptionsLoaded);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.starBalanceUpdated);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.starTransactionsLoaded);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.starSubscriptionsLoaded);
        StarsController.getInstance(this.currentAccount).invalidateTransactions(true);
        StarsController.getInstance(this.currentAccount).invalidateSubscriptions(true);
        StarsController.getInstance(this.currentAccount).getOptions();
        return super.onFragmentCreate();
    }

    @Override
    public void onFragmentDestroy() {
        super.onFragmentDestroy();
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.starOptionsLoaded);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.starBalanceUpdated);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.starTransactionsLoaded);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.starSubscriptionsLoaded);
    }

    public void onItemClick(final UItem uItem, int i) {
        int i2 = uItem.id;
        if (i2 == -1) {
            this.expanded = !this.expanded;
        } else {
            if (i2 == -2) {
                StarsController.getInstance(this.currentAccount).getGiftOptions();
                UserSelectorBottomSheet.open(1, 0L, BirthdayController.getInstance(this.currentAccount).getState());
                return;
            }
            if (i2 != -3) {
                if (i2 == -4) {
                    presentFragment(new ChannelAffiliateProgramsFragment(getUserConfig().getClientUserId()));
                    return;
                }
                if (uItem.instanceOf(StarTierView.Factory.class)) {
                    if (uItem.object instanceof TL_stars.TL_starsTopupOption) {
                        StarsController.getInstance(this.currentAccount).buy(getParentActivity(), (TL_stars.TL_starsTopupOption) uItem.object, new Utilities.Callback2() {
                            @Override
                            public final void run(Object obj, Object obj2) {
                                StarsIntroActivity.this.lambda$onItemClick$4(uItem, (Boolean) obj, (String) obj2);
                            }
                        });
                        return;
                    }
                    return;
                } else {
                    if (uItem.instanceOf(StarsSubscriptionView.Factory.class) && (uItem.object instanceof TL_stars.StarsSubscription)) {
                        showSubscriptionSheet(getContext(), this.currentAccount, (TL_stars.StarsSubscription) uItem.object, getResourceProvider());
                        return;
                    }
                    return;
                }
            }
            StarsController.getInstance(this.currentAccount).loadSubscriptions();
        }
        this.adapter.update(true);
    }

    @Override
    public void onPause() {
        super.onPause();
        GLIconTextureView gLIconTextureView = this.iconTextureView;
        if (gLIconTextureView != null) {
            gLIconTextureView.setPaused(true);
            this.iconTextureView.setDialogVisible(true);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        GLIconTextureView gLIconTextureView = this.iconTextureView;
        if (gLIconTextureView != null) {
            gLIconTextureView.setPaused(false);
            this.iconTextureView.setDialogVisible(false);
        }
    }
}
