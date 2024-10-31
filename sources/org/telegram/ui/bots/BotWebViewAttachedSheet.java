package org.telegram.ui.bots;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.ColorUtils;
import androidx.core.math.MathUtils;
import androidx.core.util.Consumer;
import androidx.dynamicanimation.animation.DynamicAnimation;
import androidx.dynamicanimation.animation.SpringAnimation;
import androidx.dynamicanimation.animation.SpringForce;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import org.json.JSONObject;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.AnimationNotificationsLocker;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.ContactsController;
import org.telegram.messenger.DialogObject;
import org.telegram.messenger.FileLoader;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.GenericProvider;
import org.telegram.messenger.ImageLocation;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MediaDataController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.MessagesStorage;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.R;
import org.telegram.messenger.SendMessagesHelper;
import org.telegram.messenger.UserObject;
import org.telegram.messenger.Utilities;
import org.telegram.messenger.browser.Browser;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.tgnet.tl.TL_bots;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.ActionBarMenuItem;
import org.telegram.ui.ActionBar.ActionBarMenuSubItem;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.BackDrawable;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.BottomSheetTabDialog;
import org.telegram.ui.ActionBar.BottomSheetTabs;
import org.telegram.ui.ActionBar.BottomSheetTabsOverlay;
import org.telegram.ui.ActionBar.INavigationLayout;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ChatActivity;
import org.telegram.ui.Components.Bulletin;
import org.telegram.ui.Components.BulletinFactory;
import org.telegram.ui.Components.CubicBezierInterpolator;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.OverlayActionBarLayoutDialog;
import org.telegram.ui.Components.ShareAlert;
import org.telegram.ui.Components.SimpleFloatPropertyCompat;
import org.telegram.ui.Components.SizeNotifierFrameLayout;
import org.telegram.ui.Components.VerticalPositionAutoAnimator;
import org.telegram.ui.DialogsActivity;
import org.telegram.ui.LaunchActivity;
import org.telegram.ui.PaymentFormActivity;
import org.telegram.ui.Stars.StarsController;
import org.telegram.ui.Stories.DarkThemeResourceProvider;
import org.telegram.ui.TopicsFragment;
import org.telegram.ui.bots.BotButtons;
import org.telegram.ui.bots.BotWebViewAttachedSheet;
import org.telegram.ui.bots.ChatAttachAlertBotWebViewLayout;
import org.telegram.ui.web.BotWebViewContainer;

public class BotWebViewAttachedSheet implements NotificationCenter.NotificationCenterDelegate, BaseFragment.AttachedSheet, BottomSheetTabsOverlay.Sheet {
    private static final SimpleFloatPropertyCompat ACTION_BAR_TRANSITION_PROGRESS_VALUE = new SimpleFloatPropertyCompat("actionBarTransitionProgress", new SimpleFloatPropertyCompat.Getter() {
        @Override
        public final float get(Object obj) {
            float f;
            f = ((BotWebViewAttachedSheet) obj).actionBarTransitionProgress;
            return f;
        }
    }, new SimpleFloatPropertyCompat.Setter() {
        @Override
        public final void set(Object obj, float f) {
            BotWebViewAttachedSheet.lambda$static$2((BotWebViewAttachedSheet) obj, f);
        }
    }).setMultiplier(100.0f);
    private ActionBar actionBar;
    private boolean actionBarAttached;
    private int actionBarColor;
    private boolean actionBarIsLight;
    private Drawable actionBarShadow;
    private boolean backButtonShown;
    private BackDrawable backDrawable;
    private BotButtons botButtons;
    private long botId;
    private String buttonText;
    private final Context context;
    private int currentAccount;
    private TLRPC.BotApp currentWebApp;
    private BottomSheetTabDialog dialog;
    private boolean dismissed;
    private boolean forceExpnaded;
    private final BaseFragment fragment;
    private boolean ignoreLayout;
    private long lastSwipeTime;
    private VerticalPositionAutoAnimator mainButtonAutoAnimator;
    private int navBarColor;
    private boolean needCloseConfirmation;
    private boolean needsContext;
    private Runnable onDismissListener;
    private ActionBarMenuItem optionsItem;
    private boolean overrideBackgroundColor;
    private Activity parentActivity;
    private long peerId;
    private ChatAttachAlertBotWebViewLayout.WebProgressView progressView;
    private long queryId;
    private int replyToMsgId;
    private WebViewRequestProps requestProps;
    private Theme.ResourcesProvider resourcesProvider;
    private ActionBarMenuSubItem settingsItem;
    public boolean showExpanded;
    private float shown;
    private ValueAnimator shownAnimator;
    private boolean silent;
    private SpringAnimation springAnimation;
    private ChatAttachAlertBotWebViewLayout.WebViewSwipeContainer swipeContainer;
    Drawable verifiedDrawable;
    private Boolean wasLightStatusBar;
    private BotWebViewContainer webViewContainer;
    public final WindowView windowView;
    private float actionBarTransitionProgress = 0.0f;
    private boolean defaultFullsize = false;
    private Boolean fullsize = null;
    public float showOffsetY = Float.MAX_VALUE;
    private Paint linePaint = new Paint(1);
    private Paint dimPaint = new Paint();
    private Paint backgroundPaint = new Paint(1);
    private int actionBarColorKey = -1;
    private Paint actionBarPaint = new Paint(1);
    private Runnable pollRunnable = new Runnable() {
        @Override
        public final void run() {
            BotWebViewAttachedSheet.this.lambda$new$5();
        }
    };
    private int lineColor = Theme.getColor(Theme.key_sheet_scrollUp);

    public class AnonymousClass3 implements BotWebViewContainer.Delegate {
        private boolean sentWebViewData;

        AnonymousClass3() {
        }

        public void lambda$onSendWebViewData$0(TLObject tLObject, TLRPC.TL_error tL_error) {
            if (tLObject instanceof TLRPC.TL_updates) {
                MessagesController.getInstance(BotWebViewAttachedSheet.this.currentAccount).processUpdates((TLRPC.TL_updates) tLObject, false);
            }
            final BotWebViewAttachedSheet botWebViewAttachedSheet = BotWebViewAttachedSheet.this;
            AndroidUtilities.runOnUIThread(new Runnable() {
                @Override
                public final void run() {
                    BotWebViewAttachedSheet.this.dismiss();
                }
            });
        }

        public void lambda$onWebAppOpenInvoice$2(String str, String str2) {
            BotWebViewAttachedSheet.this.webViewContainer.onInvoiceStatusUpdate(str, str2);
        }

        public void lambda$onWebAppOpenInvoice$3(OverlayActionBarLayoutDialog overlayActionBarLayoutDialog, String str, PaymentFormActivity.InvoiceStatus invoiceStatus) {
            if (invoiceStatus != PaymentFormActivity.InvoiceStatus.PENDING) {
                overlayActionBarLayoutDialog.dismiss();
            }
            BotWebViewAttachedSheet.this.webViewContainer.onInvoiceStatusUpdate(str, invoiceStatus.name().toLowerCase(Locale.ROOT));
        }

        public boolean lambda$onWebAppSwitchInlineQuery$4(TLRPC.User user, String str, OverlayActionBarLayoutDialog overlayActionBarLayoutDialog, DialogsActivity dialogsActivity, ArrayList arrayList, CharSequence charSequence, boolean z, boolean z2, int i, TopicsFragment topicsFragment) {
            String str2;
            long j = ((MessagesStorage.TopicKey) arrayList.get(0)).dialogId;
            Bundle bundle = new Bundle();
            bundle.putBoolean("scrollToTopOnResume", true);
            if (DialogObject.isEncryptedDialog(j)) {
                bundle.putInt("enc_id", DialogObject.getEncryptedChatId(j));
            } else {
                if (DialogObject.isUserDialog(j)) {
                    str2 = "user_id";
                } else {
                    j = -j;
                    str2 = "chat_id";
                }
                bundle.putLong(str2, j);
            }
            bundle.putString("start_text", "@" + UserObject.getPublicUsername(user) + " " + str);
            if (BotWebViewAttachedSheet.this.parentActivity instanceof LaunchActivity) {
                BaseFragment lastFragment = ((LaunchActivity) BotWebViewAttachedSheet.this.parentActivity).getActionBarLayout().getLastFragment();
                if (MessagesController.getInstance(BotWebViewAttachedSheet.this.currentAccount).checkCanOpenChat(bundle, lastFragment)) {
                    overlayActionBarLayoutDialog.dismiss();
                    BotWebViewAttachedSheet.this.dismissed = true;
                    AndroidUtilities.cancelRunOnUIThread(BotWebViewAttachedSheet.this.pollRunnable);
                    BotWebViewAttachedSheet.this.webViewContainer.destroyWebView();
                    NotificationCenter.getInstance(BotWebViewAttachedSheet.this.currentAccount).removeObserver(BotWebViewAttachedSheet.this, NotificationCenter.webViewResultSent);
                    NotificationCenter.getGlobalInstance().removeObserver(BotWebViewAttachedSheet.this, NotificationCenter.didSetNewTheme);
                    BotWebViewAttachedSheet.this.release();
                    lastFragment.presentFragment(new INavigationLayout.NavigationParams(new ChatActivity(bundle)).setRemoveLast(true));
                }
            }
            return true;
        }

        @Override
        public boolean isClipboardAvailable() {
            return MediaDataController.getInstance(BotWebViewAttachedSheet.this.currentAccount).botInAttachMenu(BotWebViewAttachedSheet.this.botId);
        }

        @Override
        public void onCloseRequested(Runnable runnable) {
            BotWebViewAttachedSheet.this.dismiss(false, runnable);
        }

        @Override
        public void onCloseToTabs() {
            BotWebViewAttachedSheet.this.dismiss(true, null);
        }

        @Override
        public void onInstantClose() {
            BotWebViewContainer.Delegate.CC.$default$onInstantClose(this);
        }

        @Override
        public void onSendWebViewData(String str) {
            if (BotWebViewAttachedSheet.this.queryId != 0 || this.sentWebViewData) {
                return;
            }
            this.sentWebViewData = true;
            TLRPC.TL_messages_sendWebViewData tL_messages_sendWebViewData = new TLRPC.TL_messages_sendWebViewData();
            tL_messages_sendWebViewData.bot = MessagesController.getInstance(BotWebViewAttachedSheet.this.currentAccount).getInputUser(BotWebViewAttachedSheet.this.botId);
            tL_messages_sendWebViewData.random_id = Utilities.random.nextLong();
            tL_messages_sendWebViewData.button_text = BotWebViewAttachedSheet.this.buttonText;
            tL_messages_sendWebViewData.data = str;
            ConnectionsManager.getInstance(BotWebViewAttachedSheet.this.currentAccount).sendRequest(tL_messages_sendWebViewData, new RequestDelegate() {
                @Override
                public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                    BotWebViewAttachedSheet.AnonymousClass3.this.lambda$onSendWebViewData$0(tLObject, tL_error);
                }
            });
        }

        @Override
        public void onSetBackButtonVisible(boolean z) {
            BotWebViewAttachedSheet.this.backButtonShown = z;
            BotWebViewAttachedSheet.this.backDrawable.setRotation(z ? 0.0f : 1.0f, true);
        }

        @Override
        public void onSetSettingsButtonVisible(boolean z) {
            if (BotWebViewAttachedSheet.this.settingsItem != null) {
                BotWebViewAttachedSheet.this.settingsItem.setVisibility(z ? 0 : 8);
            }
        }

        @Override
        public void onSetupMainButton(boolean z, boolean z2, String str, int i, int i2, boolean z3, boolean z4) {
            BotWebViewAttachedSheet.this.botButtons.setMainState(BotButtons.ButtonState.of(z, z2, z3, z4, str, i, i2), true);
        }

        @Override
        public void onSetupSecondaryButton(boolean z, boolean z2, String str, int i, int i2, boolean z3, boolean z4, String str2) {
            BotWebViewAttachedSheet.this.botButtons.setSecondaryState(BotButtons.ButtonState.of(z, z2, z3, z4, str, i, i2, str2), true);
        }

        @Override
        public void onWebAppBackgroundChanged(boolean z, int i) {
            BotWebViewContainer.Delegate.CC.$default$onWebAppBackgroundChanged(this, z, i);
        }

        @Override
        public void onWebAppExpand() {
            if (BotWebViewAttachedSheet.this.swipeContainer.isSwipeInProgress()) {
                return;
            }
            BotWebViewAttachedSheet.this.swipeContainer.stickTo((-BotWebViewAttachedSheet.this.swipeContainer.getOffsetY()) + BotWebViewAttachedSheet.this.swipeContainer.getTopActionBarOffsetY());
        }

        @Override
        public void onWebAppOpenInvoice(TLRPC.InputInvoice inputInvoice, final String str, TLObject tLObject) {
            PaymentFormActivity paymentFormActivity;
            if (BotWebViewAttachedSheet.this.getContext() == null) {
                return;
            }
            BaseFragment lastFragment = ((LaunchActivity) BotWebViewAttachedSheet.this.parentActivity).getActionBarLayout().getLastFragment();
            if (tLObject instanceof TLRPC.TL_payments_paymentFormStars) {
                AndroidUtilities.hideKeyboard(BotWebViewAttachedSheet.this.windowView);
                final AlertDialog alertDialog = new AlertDialog(BotWebViewAttachedSheet.this.getContext(), 3);
                alertDialog.showDelayed(150L);
                StarsController.getInstance(BotWebViewAttachedSheet.this.currentAccount).openPaymentForm(null, inputInvoice, (TLRPC.TL_payments_paymentFormStars) tLObject, new Runnable() {
                    @Override
                    public final void run() {
                        AlertDialog.this.dismiss();
                    }
                }, new Utilities.Callback() {
                    @Override
                    public final void run(Object obj) {
                        BotWebViewAttachedSheet.AnonymousClass3.this.lambda$onWebAppOpenInvoice$2(str, (String) obj);
                    }
                });
                return;
            }
            if (tLObject instanceof TLRPC.PaymentForm) {
                TLRPC.PaymentForm paymentForm = (TLRPC.PaymentForm) tLObject;
                MessagesController.getInstance(BotWebViewAttachedSheet.this.currentAccount).putUsers(paymentForm.users, false);
                paymentFormActivity = new PaymentFormActivity(paymentForm, str, lastFragment);
            } else {
                paymentFormActivity = tLObject instanceof TLRPC.PaymentReceipt ? new PaymentFormActivity((TLRPC.PaymentReceipt) tLObject) : null;
            }
            if (paymentFormActivity != null) {
                BotWebViewAttachedSheet.this.swipeContainer.stickTo((-BotWebViewAttachedSheet.this.swipeContainer.getOffsetY()) + BotWebViewAttachedSheet.this.swipeContainer.getTopActionBarOffsetY());
                AndroidUtilities.hideKeyboard(BotWebViewAttachedSheet.this.windowView);
                final OverlayActionBarLayoutDialog overlayActionBarLayoutDialog = new OverlayActionBarLayoutDialog(BotWebViewAttachedSheet.this.getContext(), BotWebViewAttachedSheet.this.resourcesProvider);
                overlayActionBarLayoutDialog.show();
                paymentFormActivity.setPaymentFormCallback(new PaymentFormActivity.PaymentFormCallback() {
                    @Override
                    public final void onInvoiceStatusChanged(PaymentFormActivity.InvoiceStatus invoiceStatus) {
                        BotWebViewAttachedSheet.AnonymousClass3.this.lambda$onWebAppOpenInvoice$3(overlayActionBarLayoutDialog, str, invoiceStatus);
                    }
                });
                paymentFormActivity.setResourcesProvider(BotWebViewAttachedSheet.this.resourcesProvider);
                overlayActionBarLayoutDialog.addFragment(paymentFormActivity);
            }
        }

        @Override
        public void onWebAppReady() {
            BotWebViewContainer.Delegate.CC.$default$onWebAppReady(this);
        }

        @Override
        public void onWebAppSetActionBarColor(int i, int i2, boolean z) {
            BotWebViewAttachedSheet.this.actionBarColorKey = i;
            BotWebViewAttachedSheet.this.setActionBarColor(i2, z, true);
        }

        @Override
        public void onWebAppSetBackgroundColor(int i) {
            BotWebViewAttachedSheet.this.setBackgroundColor(i, true);
        }

        @Override
        public void onWebAppSetNavigationBarColor(int i) {
            BotWebViewAttachedSheet.this.setNavigationBarColor(i, true);
        }

        @Override
        public void onWebAppSetupClosingBehavior(boolean z) {
            BotWebViewAttachedSheet.this.needCloseConfirmation = z;
        }

        @Override
        public void onWebAppSwipingBehavior(boolean z) {
            BotWebViewAttachedSheet.this.swipeContainer.setAllowSwipes(z);
        }

        @Override
        public void onWebAppSwitchInlineQuery(final TLRPC.User user, final String str, List list) {
            if (list.isEmpty()) {
                if (BotWebViewAttachedSheet.this.parentActivity instanceof LaunchActivity) {
                    BaseFragment lastFragment = ((LaunchActivity) BotWebViewAttachedSheet.this.parentActivity).getActionBarLayout().getLastFragment();
                    if (lastFragment instanceof ChatActivity) {
                        ((ChatActivity) lastFragment).getChatActivityEnterView().setFieldText("@" + UserObject.getPublicUsername(user) + " " + str);
                        BotWebViewAttachedSheet.this.dismiss();
                        return;
                    }
                    return;
                }
                return;
            }
            Bundle bundle = new Bundle();
            bundle.putInt("dialogsType", 14);
            bundle.putBoolean("onlySelect", true);
            bundle.putBoolean("allowGroups", list.contains("groups"));
            bundle.putBoolean("allowMegagroups", list.contains("groups"));
            bundle.putBoolean("allowLegacyGroups", list.contains("groups"));
            bundle.putBoolean("allowUsers", list.contains("users"));
            bundle.putBoolean("allowChannels", list.contains("channels"));
            bundle.putBoolean("allowBots", list.contains("bots"));
            DialogsActivity dialogsActivity = new DialogsActivity(bundle);
            AndroidUtilities.hideKeyboard(BotWebViewAttachedSheet.this.windowView);
            final OverlayActionBarLayoutDialog overlayActionBarLayoutDialog = new OverlayActionBarLayoutDialog(BotWebViewAttachedSheet.this.getContext(), BotWebViewAttachedSheet.this.resourcesProvider);
            dialogsActivity.setDelegate(new DialogsActivity.DialogsActivityDelegate() {
                @Override
                public final boolean didSelectDialogs(DialogsActivity dialogsActivity2, ArrayList arrayList, CharSequence charSequence, boolean z, boolean z2, int i, TopicsFragment topicsFragment) {
                    boolean lambda$onWebAppSwitchInlineQuery$4;
                    lambda$onWebAppSwitchInlineQuery$4 = BotWebViewAttachedSheet.AnonymousClass3.this.lambda$onWebAppSwitchInlineQuery$4(user, str, overlayActionBarLayoutDialog, dialogsActivity2, arrayList, charSequence, z, z2, i, topicsFragment);
                    return lambda$onWebAppSwitchInlineQuery$4;
                }
            });
            overlayActionBarLayoutDialog.show();
            overlayActionBarLayoutDialog.addFragment(dialogsActivity);
        }
    }

    public class AnonymousClass8 extends ActionBar.ActionBarMenuOnItemClick {
        AnonymousClass8() {
        }

        public void lambda$onItemClick$0() {
            BotWebViewAttachedSheet.this.dismiss();
        }

        @Override
        public void onItemClick(int i) {
            TLRPC.User user;
            if (i == -1) {
                if (BotWebViewAttachedSheet.this.webViewContainer.onBackPressed()) {
                    return;
                }
                BotWebViewAttachedSheet.this.onCheckDismissByUser();
                return;
            }
            if (i == R.id.menu_open_bot) {
                Bundle bundle = new Bundle();
                bundle.putLong("user_id", BotWebViewAttachedSheet.this.botId);
                if (BotWebViewAttachedSheet.this.parentActivity instanceof LaunchActivity) {
                    ((LaunchActivity) BotWebViewAttachedSheet.this.parentActivity).lambda$runLinkRequest$93(new ChatActivity(bundle));
                }
                BotWebViewAttachedSheet.this.dismiss();
                return;
            }
            if (i == R.id.menu_tos_bot) {
                Browser.openUrl(BotWebViewAttachedSheet.this.getContext(), LocaleController.getString(R.string.BotWebViewToSLink));
                return;
            }
            if (i == R.id.menu_privacy) {
                if (BotWebViewAttachedSheet.openPrivacy(BotWebViewAttachedSheet.this.currentAccount, BotWebViewAttachedSheet.this.botId)) {
                    BotWebViewAttachedSheet.this.dismiss(true);
                    return;
                }
                return;
            }
            if (i == R.id.menu_reload_page) {
                if (BotWebViewAttachedSheet.this.webViewContainer.getWebView() != null) {
                    BotWebViewAttachedSheet.this.webViewContainer.getWebView().animate().cancel();
                    BotWebViewAttachedSheet.this.webViewContainer.getWebView().animate().alpha(0.0f).start();
                }
                BotWebViewAttachedSheet.this.progressView.setLoadProgress(0.0f);
                BotWebViewAttachedSheet.this.progressView.setAlpha(1.0f);
                BotWebViewAttachedSheet.this.progressView.setVisibility(0);
                BotWebViewAttachedSheet.this.webViewContainer.setBotUser(MessagesController.getInstance(BotWebViewAttachedSheet.this.currentAccount).getUser(Long.valueOf(BotWebViewAttachedSheet.this.botId)));
                BotWebViewAttachedSheet.this.webViewContainer.loadFlickerAndSettingsItem(BotWebViewAttachedSheet.this.currentAccount, BotWebViewAttachedSheet.this.botId, BotWebViewAttachedSheet.this.settingsItem);
                BotWebViewAttachedSheet.this.webViewContainer.reload();
                return;
            }
            if (i == R.id.menu_settings) {
                BotWebViewAttachedSheet.this.webViewContainer.onSettingsButtonPressed();
                return;
            }
            if (i == R.id.menu_delete_bot) {
                BotWebViewAttachedSheet.deleteBot(BotWebViewAttachedSheet.this.currentAccount, BotWebViewAttachedSheet.this.botId, new Runnable() {
                    @Override
                    public final void run() {
                        BotWebViewAttachedSheet.AnonymousClass8.this.lambda$onItemClick$0();
                    }
                });
                return;
            }
            if (i == R.id.menu_add_to_home_screen_bot) {
                MediaDataController.getInstance(BotWebViewAttachedSheet.this.currentAccount).installShortcut(BotWebViewAttachedSheet.this.botId, MediaDataController.SHORTCUT_TYPE_ATTACHED_BOT);
                return;
            }
            if (i == R.id.menu_collapse_bot) {
                BotWebViewAttachedSheet.this.forceExpnaded = true;
                BotWebViewAttachedSheet.this.dismiss(true, null);
                return;
            }
            if (i != R.id.menu_share_bot || (user = MessagesController.getInstance(BotWebViewAttachedSheet.this.currentAccount).getUser(Long.valueOf(BotWebViewAttachedSheet.this.botId))) == null) {
                return;
            }
            String str = "https://" + MessagesController.getInstance(BotWebViewAttachedSheet.this.currentAccount).linkPrefix + "/" + UserObject.getPublicUsername(user) + "?profile";
            new ShareAlert(BotWebViewAttachedSheet.this.getContext(), null, str, false, str, false, AndroidUtilities.computePerceivedBrightness(BotWebViewAttachedSheet.this.actionBarColor) <= 0.721f ? new DarkThemeResourceProvider() : null).show();
        }
    }

    public class AnonymousClass9 implements View.OnLayoutChangeListener {
        final boolean val$instant;
        final boolean val$lowBounce;

        AnonymousClass9(boolean z, boolean z2) {
            this.val$lowBounce = z;
            this.val$instant = z2;
        }

        @Override
        public void onLayoutChange(View view, int i, int i2, int i3, int i4, int i5, int i6, int i7, int i8) {
            view.removeOnLayoutChangeListener(this);
            BotWebViewAttachedSheet.this.swipeContainer.setSwipeOffsetY(this.val$lowBounce ? BotWebViewAttachedSheet.this.swipeContainer.getHeight() * 0.5f : BotWebViewAttachedSheet.this.swipeContainer.getHeight());
            BotWebViewAttachedSheet.this.windowView.setAlpha(1.0f);
            final AnimationNotificationsLocker animationNotificationsLocker = new AnimationNotificationsLocker();
            animationNotificationsLocker.lock();
            BotWebViewAttachedSheet.this.updateShownAnimated(true);
            BotWebViewAttachedSheet botWebViewAttachedSheet = BotWebViewAttachedSheet.this;
            if (botWebViewAttachedSheet.showOffsetY != Float.MAX_VALUE) {
                botWebViewAttachedSheet.swipeContainer.setSwipeOffsetAnimationDisallowed(true);
                BotWebViewAttachedSheet.this.swipeContainer.setOffsetY(BotWebViewAttachedSheet.this.showOffsetY);
                BotWebViewAttachedSheet.this.swipeContainer.setSwipeOffsetAnimationDisallowed(false);
            }
            BotWebViewAttachedSheet botWebViewAttachedSheet2 = BotWebViewAttachedSheet.this;
            if (botWebViewAttachedSheet2.showExpanded || botWebViewAttachedSheet2.isFullSize()) {
                if (this.val$instant) {
                    BotWebViewAttachedSheet.this.swipeContainer.setSwipeOffsetY((-BotWebViewAttachedSheet.this.swipeContainer.getOffsetY()) + BotWebViewAttachedSheet.this.swipeContainer.getTopActionBarOffsetY());
                    animationNotificationsLocker.unlock();
                } else {
                    BotWebViewAttachedSheet.this.swipeContainer.stickTo((-BotWebViewAttachedSheet.this.swipeContainer.getOffsetY()) + BotWebViewAttachedSheet.this.swipeContainer.getTopActionBarOffsetY(), new BotWebViewAttachedSheet$9$$ExternalSyntheticLambda1(animationNotificationsLocker));
                }
            } else if (this.val$instant) {
                BotWebViewAttachedSheet.this.swipeContainer.setSwipeOffsetY(0.0f);
                animationNotificationsLocker.unlock();
            } else {
                ((SpringAnimation) new SpringAnimation(BotWebViewAttachedSheet.this.swipeContainer, ChatAttachAlertBotWebViewLayout.WebViewSwipeContainer.SWIPE_OFFSET_Y, 0.0f).setSpring(new SpringForce(0.0f).setDampingRatio(this.val$lowBounce ? 1.0f : 0.75f).setStiffness(this.val$lowBounce ? 800.0f : 500.0f)).addEndListener(new DynamicAnimation.OnAnimationEndListener() {
                    @Override
                    public final void onAnimationEnd(DynamicAnimation dynamicAnimation, boolean z, float f, float f2) {
                        AnimationNotificationsLocker.this.unlock();
                    }
                })).start();
            }
            BotWebViewAttachedSheet.this.swipeContainer.opened = true;
        }
    }

    public static class MainButtonSettings {
        public int color;
        public boolean isActive;
        public boolean isProgressVisible;
        public boolean isVisible;
        public String text;
        public int textColor;

        public static MainButtonSettings of(boolean z, boolean z2, String str, int i, int i2, boolean z3) {
            MainButtonSettings mainButtonSettings = new MainButtonSettings();
            mainButtonSettings.isVisible = z;
            mainButtonSettings.isActive = z2;
            mainButtonSettings.text = str;
            mainButtonSettings.color = i;
            mainButtonSettings.textColor = i2;
            mainButtonSettings.isProgressVisible = z3;
            return mainButtonSettings;
        }
    }

    public class WindowView extends SizeNotifierFrameLayout implements BaseFragment.AttachedSheetWindow, BottomSheetTabsOverlay.SheetView {
        private final Path clipPath;
        private boolean drawingFromOverlay;
        private final Paint navbarPaint;
        private final RectF rect;

        public WindowView(Context context) {
            super(context);
            this.navbarPaint = new Paint(1);
            this.rect = new RectF();
            this.clipPath = new Path();
            setWillNotDraw(false);
        }

        @Override
        public void dispatchDraw(Canvas canvas) {
            if (this.drawingFromOverlay) {
                return;
            }
            super.dispatchDraw(canvas);
            this.navbarPaint.setColor(BotWebViewAttachedSheet.this.navBarColor);
            RectF rectF = AndroidUtilities.rectTmp;
            rectF.set(0.0f, getHeight() - getPaddingBottom(), getWidth(), getHeight() + AndroidUtilities.navigationBarHeight);
            canvas.drawRect(rectF, this.navbarPaint);
        }

        @Override
        public void draw(Canvas canvas) {
            if (this.drawingFromOverlay) {
                return;
            }
            super.draw(canvas);
            float f = AndroidUtilities.isTablet() ? 0.0f : BotWebViewAttachedSheet.this.actionBarTransitionProgress;
            BotWebViewAttachedSheet.this.linePaint.setColor(BotWebViewAttachedSheet.this.lineColor);
            BotWebViewAttachedSheet.this.linePaint.setAlpha((int) (BotWebViewAttachedSheet.this.linePaint.getAlpha() * (1.0f - (Math.min(0.5f, f) / 0.5f))));
            canvas.save();
            float f2 = 1.0f - f;
            float containerTop = BotWebViewAttachedSheet.this.getContainerTop();
            canvas.scale(f2, f2, getWidth() / 2.0f, containerTop);
            canvas.drawLine((getWidth() / 2.0f) - AndroidUtilities.dp(16.0f), containerTop, (getWidth() / 2.0f) + AndroidUtilities.dp(16.0f), containerTop, BotWebViewAttachedSheet.this.linePaint);
            canvas.restore();
            BotWebViewAttachedSheet.this.actionBarShadow.setAlpha((int) (BotWebViewAttachedSheet.this.actionBar.getAlpha() * 255.0f));
            float y = BotWebViewAttachedSheet.this.actionBar.getY() + BotWebViewAttachedSheet.this.actionBar.getTranslationY() + BotWebViewAttachedSheet.this.actionBar.getHeight();
            BotWebViewAttachedSheet.this.actionBarShadow.setBounds(0, (int) y, getWidth(), (int) (y + BotWebViewAttachedSheet.this.actionBarShadow.getIntrinsicHeight()));
            BotWebViewAttachedSheet.this.actionBarShadow.draw(canvas);
        }

        @Override
        protected boolean drawChild(Canvas canvas, View view, long j) {
            if (this.drawingFromOverlay) {
                return false;
            }
            return super.drawChild(canvas, view, j);
        }

        @Override
        public float drawInto(Canvas canvas, RectF rectF, float f, RectF rectF2, float f2, boolean z) {
            this.rect.set(BotWebViewAttachedSheet.this.swipeContainer.getLeft(), AndroidUtilities.lerp(BotWebViewAttachedSheet.this.swipeContainer.getTranslationY() + AndroidUtilities.dp(24.0f), 0.0f, BotWebViewAttachedSheet.this.actionBarTransitionProgress), BotWebViewAttachedSheet.this.swipeContainer.getRight(), getHeight());
            AndroidUtilities.lerpCentered(this.rect, rectF, f, rectF2);
            canvas.save();
            this.clipPath.rewind();
            float lerp = AndroidUtilities.lerp(AndroidUtilities.dp(16.0f) * (AndroidUtilities.isTablet() ? 1.0f : 1.0f - BotWebViewAttachedSheet.this.actionBarTransitionProgress), AndroidUtilities.dp(10.0f), f);
            this.rect.set(rectF2);
            if (z) {
                this.rect.top -= AndroidUtilities.dp(16.0f) * (1.0f - BotWebViewAttachedSheet.this.actionBarTransitionProgress);
            }
            this.clipPath.addRoundRect(this.rect, lerp, lerp, Path.Direction.CW);
            canvas.clipPath(this.clipPath);
            if (!BotWebViewAttachedSheet.this.overrideBackgroundColor) {
                BotWebViewAttachedSheet.this.backgroundPaint.setColor(BotWebViewAttachedSheet.this.getColor(Theme.key_windowBackgroundWhite));
            }
            int alpha = BotWebViewAttachedSheet.this.backgroundPaint.getAlpha();
            BotWebViewAttachedSheet.this.backgroundPaint.setAlpha((int) (alpha * f2));
            canvas.drawPaint(BotWebViewAttachedSheet.this.backgroundPaint);
            BotWebViewAttachedSheet.this.backgroundPaint.setAlpha(alpha);
            if (BotWebViewAttachedSheet.this.swipeContainer != null) {
                canvas.save();
                canvas.translate(BotWebViewAttachedSheet.this.swipeContainer.getX(), Math.max(BotWebViewAttachedSheet.this.swipeContainer.getY(), rectF2.top) + (z ? AndroidUtilities.lerp(AndroidUtilities.dp(16.0f), BotWebViewAttachedSheet.this.actionBar.getHeight(), BotWebViewAttachedSheet.this.actionBarTransitionProgress) * (1.0f - f2) : AndroidUtilities.dp(51.0f) * f));
                if (z) {
                    canvas.scale(1.0f, AndroidUtilities.lerp(1.0f, 1.25f, 1.0f - f2));
                    BotWebViewAttachedSheet.this.swipeContainer.setAlpha(f2);
                }
                BotWebViewAttachedSheet.this.swipeContainer.draw(canvas);
                if (z) {
                    BotWebViewAttachedSheet.this.swipeContainer.setAlpha(1.0f);
                }
                canvas.restore();
            }
            if (z && BotWebViewAttachedSheet.this.actionBar != null && BotWebViewAttachedSheet.this.actionBarTransitionProgress > 0.0f) {
                canvas.saveLayerAlpha(BotWebViewAttachedSheet.this.actionBar.getX(), BotWebViewAttachedSheet.this.actionBar.getY() + rectF2.top, BotWebViewAttachedSheet.this.actionBar.getX() + BotWebViewAttachedSheet.this.actionBar.getWidth(), BotWebViewAttachedSheet.this.actionBar.getY() + rectF2.top + BotWebViewAttachedSheet.this.actionBar.getHeight() + BotWebViewAttachedSheet.this.actionBarShadow.getIntrinsicHeight(), (int) (f2 * 255.0f * BotWebViewAttachedSheet.this.actionBarTransitionProgress), 31);
                canvas.translate(BotWebViewAttachedSheet.this.actionBar.getX(), BotWebViewAttachedSheet.this.actionBar.getY() + rectF2.top);
                BotWebViewAttachedSheet.this.actionBar.draw(canvas);
                BotWebViewAttachedSheet.this.actionBarShadow.setAlpha(255);
                BotWebViewAttachedSheet.this.actionBarShadow.setBounds(0, BotWebViewAttachedSheet.this.actionBar.getHeight(), BotWebViewAttachedSheet.this.actionBar.getWidth(), BotWebViewAttachedSheet.this.actionBar.getHeight() + BotWebViewAttachedSheet.this.actionBarShadow.getIntrinsicHeight());
                BotWebViewAttachedSheet.this.actionBarShadow.draw(canvas);
                canvas.restore();
            }
            canvas.restore();
            return lerp;
        }

        @Override
        public RectF getRect() {
            this.rect.set(BotWebViewAttachedSheet.this.swipeContainer.getLeft(), AndroidUtilities.lerp(BotWebViewAttachedSheet.this.swipeContainer.getTranslationY() + AndroidUtilities.dp(24.0f), 0.0f, BotWebViewAttachedSheet.this.actionBarTransitionProgress), BotWebViewAttachedSheet.this.swipeContainer.getRight(), getHeight());
            return this.rect;
        }

        @Override
        public void onAttachedToWindow() {
            super.onAttachedToWindow();
            Bulletin.addDelegate(this, new Bulletin.Delegate() {
                @Override
                public boolean allowLayoutChanges() {
                    return Bulletin.Delegate.CC.$default$allowLayoutChanges(this);
                }

                @Override
                public boolean bottomOffsetAnimated() {
                    return Bulletin.Delegate.CC.$default$bottomOffsetAnimated(this);
                }

                @Override
                public boolean clipWithGradient(int i) {
                    return Bulletin.Delegate.CC.$default$clipWithGradient(this, i);
                }

                @Override
                public int getBottomOffset(int i) {
                    return Bulletin.Delegate.CC.$default$getBottomOffset(this, i);
                }

                @Override
                public int getTopOffset(int i) {
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

        @Override
        public void onDetachedFromWindow() {
            super.onDetachedFromWindow();
            Bulletin.removeDelegate(this);
        }

        @Override
        protected void onDraw(Canvas canvas) {
            if (this.drawingFromOverlay) {
                return;
            }
            super.onDraw(canvas);
            if (!BotWebViewAttachedSheet.this.overrideBackgroundColor) {
                BotWebViewAttachedSheet.this.backgroundPaint.setColor(BotWebViewAttachedSheet.this.getColor(Theme.key_windowBackgroundWhite));
            }
            RectF rectF = AndroidUtilities.rectTmp;
            rectF.set(0.0f, 0.0f, getWidth(), getHeight());
            canvas.drawRect(rectF, BotWebViewAttachedSheet.this.dimPaint);
            BotWebViewAttachedSheet.this.actionBarPaint.setColor(BotWebViewAttachedSheet.this.actionBarColor);
            float dp = AndroidUtilities.dp(16.0f) * (AndroidUtilities.isTablet() ? 1.0f : 1.0f - BotWebViewAttachedSheet.this.actionBarTransitionProgress);
            rectF.set(BotWebViewAttachedSheet.this.swipeContainer.getLeft(), AndroidUtilities.lerp(BotWebViewAttachedSheet.this.swipeContainer.getTranslationY(), 0.0f, BotWebViewAttachedSheet.this.actionBarTransitionProgress), BotWebViewAttachedSheet.this.swipeContainer.getRight(), BotWebViewAttachedSheet.this.swipeContainer.getTranslationY() + AndroidUtilities.dp(24.0f) + dp);
            canvas.drawRoundRect(rectF, dp, dp, BotWebViewAttachedSheet.this.actionBarPaint);
            rectF.set(BotWebViewAttachedSheet.this.swipeContainer.getLeft(), BotWebViewAttachedSheet.this.swipeContainer.getTranslationY() + AndroidUtilities.dp(24.0f), BotWebViewAttachedSheet.this.swipeContainer.getRight(), getHeight());
            canvas.drawRect(rectF, BotWebViewAttachedSheet.this.backgroundPaint);
        }

        @Override
        public boolean onTouchEvent(MotionEvent motionEvent) {
            if (this.drawingFromOverlay) {
                return false;
            }
            if (motionEvent.getAction() != 0 || (motionEvent.getY() > AndroidUtilities.lerp(BotWebViewAttachedSheet.this.swipeContainer.getTranslationY() + AndroidUtilities.dp(24.0f), 0.0f, BotWebViewAttachedSheet.this.actionBarTransitionProgress) && motionEvent.getX() <= BotWebViewAttachedSheet.this.swipeContainer.getRight() && motionEvent.getX() >= BotWebViewAttachedSheet.this.swipeContainer.getLeft())) {
                return super.onTouchEvent(motionEvent);
            }
            BotWebViewAttachedSheet.this.dismiss(true, null);
            return true;
        }

        @Override
        public void setDrawingFromOverlay(boolean z) {
            if (this.drawingFromOverlay != z) {
                this.drawingFromOverlay = z;
                invalidate();
            }
        }
    }

    public BotWebViewAttachedSheet(BaseFragment baseFragment) {
        this.fragment = baseFragment;
        this.context = baseFragment.getContext();
        this.resourcesProvider = baseFragment.getResourceProvider();
        ChatAttachAlertBotWebViewLayout.WebViewSwipeContainer webViewSwipeContainer = new ChatAttachAlertBotWebViewLayout.WebViewSwipeContainer(getContext()) {
            @Override
            protected void onMeasure(int r5, int r6) {
                throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.bots.BotWebViewAttachedSheet.AnonymousClass1.onMeasure(int, int):void");
            }

            @Override
            public void requestLayout() {
                if (BotWebViewAttachedSheet.this.ignoreLayout) {
                    return;
                }
                super.requestLayout();
            }
        };
        this.swipeContainer = webViewSwipeContainer;
        webViewSwipeContainer.setAllowFullSizeSwipe(true);
        this.swipeContainer.setShouldWaitWebViewScroll(true);
        Context context = getContext();
        Theme.ResourcesProvider resourcesProvider = this.resourcesProvider;
        int i = Theme.key_windowBackgroundWhite;
        BotWebViewContainer botWebViewContainer = new BotWebViewContainer(context, resourcesProvider, getColor(i), true) {
            @Override
            public void onWebViewCreated() {
                super.onWebViewCreated();
                BotWebViewAttachedSheet.this.swipeContainer.setWebView(BotWebViewAttachedSheet.this.webViewContainer.getWebView());
            }
        };
        this.webViewContainer = botWebViewContainer;
        botWebViewContainer.setDelegate(new AnonymousClass3());
        this.linePaint.setStyle(Paint.Style.FILL_AND_STROKE);
        this.linePaint.setStrokeWidth(AndroidUtilities.dp(4.0f));
        this.linePaint.setStrokeCap(Paint.Cap.ROUND);
        this.dimPaint.setColor(1073741824);
        this.actionBarColor = getColor(i);
        checkNavBarColor();
        WindowView windowView = new WindowView(getContext());
        this.windowView = windowView;
        windowView.setDelegate(new SizeNotifierFrameLayout.SizeNotifierFrameLayoutDelegate() {
            @Override
            public final void onSizeChanged(int i2, boolean z) {
                BotWebViewAttachedSheet.this.lambda$new$6(i2, z);
            }
        });
        windowView.addView(this.swipeContainer, LayoutHelper.createFrame(-1, -1.0f, 49, 0.0f, 24.0f, 0.0f, 0.0f));
        BotButtons botButtons = new BotButtons(getContext(), this.resourcesProvider);
        this.botButtons = botButtons;
        botButtons.setOnButtonClickListener(new Utilities.Callback() {
            @Override
            public final void run(Object obj) {
                BotWebViewAttachedSheet.this.lambda$new$7((Boolean) obj);
            }
        });
        this.botButtons.setOnResizeListener(new Runnable() {
            @Override
            public final void run() {
                BotWebViewAttachedSheet.this.lambda$new$8();
            }
        });
        windowView.addView(this.botButtons, LayoutHelper.createFrame(-1, -2, 81));
        this.mainButtonAutoAnimator = VerticalPositionAutoAnimator.attach(this.botButtons);
        setNavigationBarColor(getColor(Theme.key_windowBackgroundGray), false);
        this.actionBarShadow = ContextCompat.getDrawable(getContext(), R.drawable.header_shadow).mutate();
        ActionBar actionBar = new ActionBar(getContext(), this.resourcesProvider) {
            @Override
            public void onMeasure(int i2, int i3) {
                super.onMeasure(i2, i3);
            }
        };
        this.actionBar = actionBar;
        actionBar.setBackgroundColor(0);
        ActionBar actionBar2 = this.actionBar;
        BackDrawable backDrawable = new BackDrawable(false);
        this.backDrawable = backDrawable;
        actionBar2.setBackButtonDrawable(backDrawable);
        this.backDrawable.setAnimationTime(200.0f);
        this.backDrawable.setColorFilter(null);
        this.backDrawable.setRotation(1.0f, false);
        updateActionBarColors();
        this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
            @Override
            public void onItemClick(int i2) {
                if (i2 == -1) {
                    BotWebViewAttachedSheet.this.onCheckDismissByUser();
                }
            }
        });
        this.actionBar.setAlpha(0.0f);
        windowView.addView(this.actionBar, LayoutHelper.createFrame(-1, -2, 49));
        ChatAttachAlertBotWebViewLayout.WebProgressView webProgressView = new ChatAttachAlertBotWebViewLayout.WebProgressView(getContext(), this.resourcesProvider);
        this.progressView = webProgressView;
        windowView.addView(webProgressView, LayoutHelper.createFrame(-1, -2.0f, 81, 0.0f, 0.0f, 0.0f, 0.0f));
        this.webViewContainer.setWebViewProgressListener(new Consumer() {
            @Override
            public final void accept(Object obj) {
                BotWebViewAttachedSheet.this.lambda$new$10((Float) obj);
            }
        });
        if (this.springAnimation == null) {
            this.springAnimation = new SpringAnimation(this, ACTION_BAR_TRANSITION_PROGRESS_VALUE).setSpring(new SpringForce().setStiffness(1200.0f).setDampingRatio(1.0f));
        }
        this.swipeContainer.addView(this.webViewContainer, LayoutHelper.createFrame(-1, -1.0f));
        this.swipeContainer.setScrollListener(new Runnable() {
            @Override
            public final void run() {
                BotWebViewAttachedSheet.this.lambda$new$11();
            }
        });
        this.swipeContainer.setScrollEndListener(new Runnable() {
            @Override
            public final void run() {
                BotWebViewAttachedSheet.this.lambda$new$12();
            }
        });
        this.swipeContainer.setDelegate(new ChatAttachAlertBotWebViewLayout.WebViewSwipeContainer.Delegate() {
            @Override
            public final void onDismiss() {
                BotWebViewAttachedSheet.this.lambda$new$13();
            }
        });
        this.swipeContainer.setTopActionBarOffsetY((ActionBar.getCurrentActionBarHeight() + AndroidUtilities.statusBarHeight) - AndroidUtilities.dp(24.0f));
        this.swipeContainer.setIsKeyboardVisible(new GenericProvider() {
            @Override
            public final Object provide(Object obj) {
                Boolean lambda$new$14;
                lambda$new$14 = BotWebViewAttachedSheet.this.lambda$new$14((Void) obj);
                return lambda$new$14;
            }
        });
    }

    public static void deleteBot(final int i, final long j, final Runnable runnable) {
        final TLRPC.TL_attachMenuBot tL_attachMenuBot;
        Iterator<TLRPC.TL_attachMenuBot> it = MediaDataController.getInstance(i).getAttachMenuBots().bots.iterator();
        while (true) {
            if (!it.hasNext()) {
                tL_attachMenuBot = null;
                break;
            }
            TLRPC.TL_attachMenuBot next = it.next();
            if (next.bot_id == j) {
                tL_attachMenuBot = next;
                break;
            }
        }
        if (tL_attachMenuBot == null) {
            return;
        }
        new AlertDialog.Builder(LaunchActivity.getLastFragment().getContext()).setTitle(LocaleController.getString(R.string.BotRemoveFromMenuTitle)).setMessage(AndroidUtilities.replaceTags(LocaleController.formatString("BotRemoveFromMenu", R.string.BotRemoveFromMenu, tL_attachMenuBot.short_name))).setPositiveButton(LocaleController.getString(R.string.OK), new DialogInterface.OnClickListener() {
            @Override
            public final void onClick(DialogInterface dialogInterface, int i2) {
                BotWebViewAttachedSheet.lambda$deleteBot$28(i, j, tL_attachMenuBot, runnable, dialogInterface, i2);
            }
        }).setNegativeButton(LocaleController.getString(R.string.Cancel), null).show();
    }

    public int getColor(int i) {
        return Theme.getColor(i, this.resourcesProvider);
    }

    public static void hasPrivacy(int i, long j, final Utilities.Callback callback) {
        if (callback == null) {
            return;
        }
        TLRPC.UserFull userFull = MessagesController.getInstance(i).getUserFull(j);
        if (userFull != null) {
            callback.run(Boolean.valueOf(hasPrivacy(userFull)));
            return;
        }
        TLRPC.User user = MessagesController.getInstance(i).getUser(Long.valueOf(j));
        if (user == null) {
            callback.run(Boolean.FALSE);
        } else {
            MessagesController.getInstance(i).loadFullUser(user, 0, true, new Utilities.Callback() {
                @Override
                public final void run(Object obj) {
                    BotWebViewAttachedSheet.lambda$hasPrivacy$36(Utilities.Callback.this, (TLRPC.UserFull) obj);
                }
            });
        }
    }

    public static boolean hasPrivacy(TLRPC.UserFull userFull) {
        return (userFull == null || userFull.bot_info == null) ? false : true;
    }

    public static boolean hasPrivacyCommand(TLRPC.UserFull userFull) {
        TL_bots.BotInfo botInfo;
        if (userFull == null || (botInfo = userFull.bot_info) == null) {
            return false;
        }
        if (botInfo.privacy_policy_url != null) {
            return true;
        }
        Iterator<TLRPC.TL_botCommand> it = botInfo.commands.iterator();
        while (it.hasNext()) {
            if ("privacy".equals(it.next().command)) {
                return true;
            }
        }
        return false;
    }

    public static void lambda$deleteBot$26(int i) {
        MediaDataController.getInstance(i).loadAttachMenuBots(false, true);
    }

    public static void lambda$deleteBot$27(final int i, TLObject tLObject, TLRPC.TL_error tL_error) {
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                BotWebViewAttachedSheet.lambda$deleteBot$26(i);
            }
        });
    }

    public static void lambda$deleteBot$28(final int i, long j, TLRPC.TL_attachMenuBot tL_attachMenuBot, Runnable runnable, DialogInterface dialogInterface, int i2) {
        TLRPC.TL_messages_toggleBotInAttachMenu tL_messages_toggleBotInAttachMenu = new TLRPC.TL_messages_toggleBotInAttachMenu();
        tL_messages_toggleBotInAttachMenu.bot = MessagesController.getInstance(i).getInputUser(j);
        tL_messages_toggleBotInAttachMenu.enabled = false;
        ConnectionsManager.getInstance(i).sendRequest(tL_messages_toggleBotInAttachMenu, new RequestDelegate() {
            @Override
            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                BotWebViewAttachedSheet.lambda$deleteBot$27(i, tLObject, tL_error);
            }
        }, 66);
        tL_attachMenuBot.show_in_side_menu = false;
        NotificationCenter.getInstance(i).lambda$postNotificationNameOnUIThread$1(NotificationCenter.attachMenuBotsDidLoad, new Object[0]);
        MediaDataController.getInstance(i).uninstallShortcut(j, MediaDataController.SHORTCUT_TYPE_ATTACHED_BOT);
        if (runnable != null) {
            runnable.run();
        }
    }

    public void lambda$dismiss$31(Runnable runnable) {
        release();
        if (runnable != null) {
            runnable.run();
        }
        Runnable runnable2 = this.onDismissListener;
        if (runnable2 != null) {
            runnable2.run();
            this.onDismissListener = null;
        }
    }

    public static void lambda$hasPrivacy$35(Utilities.Callback callback, TLRPC.UserFull userFull) {
        callback.run(Boolean.valueOf(hasPrivacy(userFull)));
    }

    public static void lambda$hasPrivacy$36(final Utilities.Callback callback, final TLRPC.UserFull userFull) {
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                BotWebViewAttachedSheet.lambda$hasPrivacy$35(Utilities.Callback.this, userFull);
            }
        });
    }

    public void lambda$new$10(Float f) {
        this.progressView.setLoadProgressAnimated(f.floatValue());
        if (f.floatValue() == 1.0f) {
            ValueAnimator duration = ValueAnimator.ofFloat(1.0f, 0.0f).setDuration(200L);
            duration.setInterpolator(CubicBezierInterpolator.DEFAULT);
            duration.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                    BotWebViewAttachedSheet.this.lambda$new$9(valueAnimator);
                }
            });
            duration.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animator) {
                    BotWebViewAttachedSheet.this.progressView.setVisibility(8);
                }
            });
            duration.start();
        }
    }

    public void lambda$new$11() {
        Paint paint;
        int i;
        if (this.swipeContainer.getSwipeOffsetY() > 0.0f) {
            paint = this.dimPaint;
            i = (int) ((1.0f - MathUtils.clamp(this.swipeContainer.getSwipeOffsetY() / this.swipeContainer.getHeight(), 0.0f, 1.0f)) * 64.0f);
        } else {
            paint = this.dimPaint;
            i = 64;
        }
        paint.setAlpha(i);
        this.windowView.invalidate();
        this.webViewContainer.invalidateViewPortHeight();
        if (this.springAnimation != null) {
            ?? r0 = 1.0f - (Math.min(this.swipeContainer.getTopActionBarOffsetY(), this.swipeContainer.getTranslationY() - this.swipeContainer.getTopActionBarOffsetY()) / this.swipeContainer.getTopActionBarOffsetY()) > 0.5f ? 1 : 0;
            this.actionBarAttached = r0;
            float f = ((float) r0) * 100.0f;
            if (this.springAnimation.getSpring().getFinalPosition() != f) {
                this.springAnimation.getSpring().setFinalPosition(f);
                this.springAnimation.start();
                checkNavBarColor();
            }
        }
        this.mainButtonAutoAnimator.setOffsetY(Math.max(0.0f, this.swipeContainer.getSwipeOffsetY()));
        this.lastSwipeTime = System.currentTimeMillis();
    }

    public void lambda$new$12() {
        this.webViewContainer.invalidateViewPortHeight(true);
    }

    public void lambda$new$13() {
        dismiss(true, null);
    }

    public Boolean lambda$new$14(Void r2) {
        return Boolean.valueOf(this.windowView.getKeyboardHeight() >= AndroidUtilities.dp(20.0f));
    }

    public void lambda$new$3(TLRPC.TL_error tL_error) {
        if (this.dismissed) {
            return;
        }
        if (tL_error != null) {
            dismiss();
        } else {
            AndroidUtilities.runOnUIThread(this.pollRunnable, 60000L);
        }
    }

    public void lambda$new$4(TLObject tLObject, final TLRPC.TL_error tL_error) {
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                BotWebViewAttachedSheet.this.lambda$new$3(tL_error);
            }
        });
    }

    public void lambda$new$5() {
        if (this.dismissed || this.queryId == 0) {
            return;
        }
        TLRPC.TL_messages_prolongWebView tL_messages_prolongWebView = new TLRPC.TL_messages_prolongWebView();
        tL_messages_prolongWebView.bot = MessagesController.getInstance(this.currentAccount).getInputUser(this.botId);
        tL_messages_prolongWebView.peer = MessagesController.getInstance(this.currentAccount).getInputPeer(this.peerId);
        tL_messages_prolongWebView.query_id = this.queryId;
        tL_messages_prolongWebView.silent = this.silent;
        if (this.replyToMsgId != 0) {
            tL_messages_prolongWebView.reply_to = SendMessagesHelper.getInstance(this.currentAccount).createReplyInput(this.replyToMsgId);
            tL_messages_prolongWebView.flags |= 1;
        }
        ConnectionsManager.getInstance(this.currentAccount).sendRequest(tL_messages_prolongWebView, new RequestDelegate() {
            @Override
            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                BotWebViewAttachedSheet.this.lambda$new$4(tLObject, tL_error);
            }
        });
    }

    public void lambda$new$6(int i, boolean z) {
        if (i > AndroidUtilities.dp(20.0f)) {
            ChatAttachAlertBotWebViewLayout.WebViewSwipeContainer webViewSwipeContainer = this.swipeContainer;
            webViewSwipeContainer.stickTo((-webViewSwipeContainer.getOffsetY()) + this.swipeContainer.getTopActionBarOffsetY());
        }
    }

    public void lambda$new$7(Boolean bool) {
        if (this.webViewContainer != null) {
            if (bool.booleanValue()) {
                this.webViewContainer.onMainButtonPressed();
            } else {
                this.webViewContainer.onSecondaryButtonPressed();
            }
        }
    }

    public void lambda$new$8() {
        this.swipeContainer.requestLayout();
    }

    public void lambda$new$9(ValueAnimator valueAnimator) {
        this.progressView.setAlpha(((Float) valueAnimator.getAnimatedValue()).floatValue());
    }

    public void lambda$onCheckDismissByUser$30(DialogInterface dialogInterface, int i) {
        dismiss();
    }

    public static void lambda$openPrivacy$37(int i, long j) {
        SendMessagesHelper.getInstance(i).sendMessage(SendMessagesHelper.SendMessageParams.of("/privacy", j, null, null, null, false, null, null, null, true, 0, null, false));
    }

    public static void lambda$requestWebView$15(ActionBarMenuItem actionBarMenuItem, Boolean bool) {
        if (bool.booleanValue()) {
            actionBarMenuItem.showSubItem(R.id.menu_privacy);
        } else {
            actionBarMenuItem.hideSubItem(R.id.menu_privacy);
        }
    }

    public void lambda$requestWebView$16(TLRPC.TL_error tL_error, TLObject tLObject) {
        WebViewRequestProps webViewRequestProps;
        if (tL_error == null && (webViewRequestProps = this.requestProps) != null) {
            webViewRequestProps.applyResponse(tLObject);
            loadFromResponse(false);
        }
    }

    public void lambda$requestWebView$17(final TLObject tLObject, final TLRPC.TL_error tL_error) {
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                BotWebViewAttachedSheet.this.lambda$requestWebView$16(tL_error, tLObject);
            }
        });
    }

    public void lambda$requestWebView$18(TLRPC.TL_error tL_error, TLObject tLObject) {
        WebViewRequestProps webViewRequestProps;
        if (tL_error == null && (webViewRequestProps = this.requestProps) != null) {
            webViewRequestProps.applyResponse(tLObject);
            loadFromResponse(false);
        }
    }

    public void lambda$requestWebView$19(final TLObject tLObject, final TLRPC.TL_error tL_error) {
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                BotWebViewAttachedSheet.this.lambda$requestWebView$18(tL_error, tLObject);
            }
        });
    }

    public void lambda$requestWebView$20(TLRPC.TL_error tL_error, TLObject tLObject) {
        WebViewRequestProps webViewRequestProps;
        if (tL_error == null && (webViewRequestProps = this.requestProps) != null) {
            webViewRequestProps.applyResponse(tLObject);
            loadFromResponse(false);
        }
    }

    public void lambda$requestWebView$21(final TLObject tLObject, final TLRPC.TL_error tL_error) {
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                BotWebViewAttachedSheet.this.lambda$requestWebView$20(tL_error, tLObject);
            }
        });
    }

    public void lambda$requestWebView$22(TLRPC.TL_error tL_error, TLObject tLObject) {
        WebViewRequestProps webViewRequestProps;
        if (tL_error == null && (webViewRequestProps = this.requestProps) != null) {
            webViewRequestProps.applyResponse(tLObject);
            loadFromResponse(false);
        }
    }

    public void lambda$requestWebView$23(final TLObject tLObject, final TLRPC.TL_error tL_error) {
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                BotWebViewAttachedSheet.this.lambda$requestWebView$22(tL_error, tLObject);
            }
        });
    }

    public void lambda$requestWebView$24(TLRPC.TL_error tL_error, TLObject tLObject) {
        WebViewRequestProps webViewRequestProps;
        if (tL_error == null && (webViewRequestProps = this.requestProps) != null) {
            webViewRequestProps.applyResponse(tLObject);
            loadFromResponse(false);
        }
    }

    public void lambda$requestWebView$25(final TLObject tLObject, final TLRPC.TL_error tL_error) {
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                BotWebViewAttachedSheet.this.lambda$requestWebView$24(tL_error, tLObject);
            }
        });
    }

    public void lambda$setActionBarColor$34(int i, int i2, BotWebViewMenuContainer$ActionBarColorsAnimating botWebViewMenuContainer$ActionBarColorsAnimating, ValueAnimator valueAnimator) {
        float floatValue = ((Float) valueAnimator.getAnimatedValue()).floatValue();
        this.actionBarColor = ColorUtils.blendARGB(i, i2, floatValue);
        checkNavBarColor();
        this.windowView.invalidate();
        this.actionBar.setBackgroundColor(this.actionBarColor);
        botWebViewMenuContainer$ActionBarColorsAnimating.updateActionBar(this.actionBar, floatValue);
        this.lineColor = botWebViewMenuContainer$ActionBarColorsAnimating.getColor(Theme.key_sheet_scrollUp);
        this.windowView.invalidate();
    }

    public void lambda$setBackgroundColor$32(int i, int i2, ValueAnimator valueAnimator) {
        this.backgroundPaint.setColor(ColorUtils.blendARGB(i, i2, ((Float) valueAnimator.getAnimatedValue()).floatValue()));
        updateActionBarColors();
        this.windowView.invalidate();
    }

    public void lambda$setNavigationBarColor$33(int i, int i2, ValueAnimator valueAnimator) {
        this.navBarColor = ColorUtils.blendARGB(i, i2, ((Float) valueAnimator.getAnimatedValue()).floatValue());
        checkNavBarColor();
    }

    public void lambda$showJustAddedBulletin$0(String str) {
        BulletinFactory.of(this.windowView, this.resourcesProvider).createSimpleBulletin(R.raw.contact_check, AndroidUtilities.replaceTags(str)).setDuration(5000).show(true);
    }

    public static void lambda$static$2(BotWebViewAttachedSheet botWebViewAttachedSheet, float f) {
        botWebViewAttachedSheet.actionBarTransitionProgress = f;
        botWebViewAttachedSheet.windowView.invalidate();
        botWebViewAttachedSheet.actionBar.setAlpha(f);
        botWebViewAttachedSheet.updateLightStatusBar();
    }

    public void lambda$updateShownAnimated$29(ValueAnimator valueAnimator) {
        this.shown = ((Float) valueAnimator.getAnimatedValue()).floatValue();
        checkNavBarColor();
    }

    private void loadFromResponse(boolean z) {
        if (this.requestProps == null) {
            return;
        }
        long max = Math.max(0L, 60000 - (System.currentTimeMillis() - this.requestProps.responseTime));
        String str = null;
        this.fullsize = null;
        TLObject tLObject = this.requestProps.response;
        if (tLObject instanceof TLRPC.TL_webViewResultUrl) {
            TLRPC.TL_webViewResultUrl tL_webViewResultUrl = (TLRPC.TL_webViewResultUrl) tLObject;
            this.queryId = tL_webViewResultUrl.query_id;
            str = tL_webViewResultUrl.url;
            this.fullsize = Boolean.valueOf(tL_webViewResultUrl.fullsize);
        } else if (tLObject instanceof TLRPC.TL_appWebViewResultUrl) {
            this.queryId = 0L;
            str = ((TLRPC.TL_appWebViewResultUrl) tLObject).url;
        } else if (tLObject instanceof TLRPC.TL_simpleWebViewResultUrl) {
            this.queryId = 0L;
            str = ((TLRPC.TL_simpleWebViewResultUrl) tLObject).url;
        }
        if (str != null && !z) {
            MediaDataController.getInstance(this.currentAccount).increaseWebappRating(this.requestProps.botId);
            this.webViewContainer.loadUrl(this.currentAccount, str);
        }
        AndroidUtilities.runOnUIThread(this.pollRunnable, max);
        ChatAttachAlertBotWebViewLayout.WebViewSwipeContainer webViewSwipeContainer = this.swipeContainer;
        if (webViewSwipeContainer != null) {
            webViewSwipeContainer.setFullSize(isFullSize());
        }
    }

    public static JSONObject makeThemeParams(Theme.ResourcesProvider resourcesProvider) {
        try {
            JSONObject jSONObject = new JSONObject();
            int color = Theme.getColor(Theme.key_dialogBackground, resourcesProvider);
            jSONObject.put("bg_color", color);
            jSONObject.put("section_bg_color", Theme.getColor(Theme.key_windowBackgroundWhite, resourcesProvider));
            int i = Theme.key_windowBackgroundGray;
            jSONObject.put("secondary_bg_color", Theme.getColor(i, resourcesProvider));
            jSONObject.put("text_color", Theme.getColor(Theme.key_windowBackgroundWhiteBlackText, resourcesProvider));
            jSONObject.put("hint_color", Theme.getColor(Theme.key_windowBackgroundWhiteHintText, resourcesProvider));
            jSONObject.put("link_color", Theme.getColor(Theme.key_windowBackgroundWhiteLinkText, resourcesProvider));
            jSONObject.put("button_color", Theme.getColor(Theme.key_featuredStickers_addButton, resourcesProvider));
            jSONObject.put("button_text_color", Theme.getColor(Theme.key_featuredStickers_buttonText, resourcesProvider));
            jSONObject.put("header_bg_color", Theme.getColor(Theme.key_actionBarDefault, resourcesProvider));
            jSONObject.put("accent_text_color", Theme.blendOver(color, Theme.getColor(Theme.key_windowBackgroundWhiteBlueText4, resourcesProvider)));
            jSONObject.put("section_header_text_color", Theme.blendOver(color, Theme.getColor(Theme.key_windowBackgroundWhiteBlueHeader, resourcesProvider)));
            jSONObject.put("subtitle_text_color", Theme.blendOver(color, Theme.getColor(Theme.key_windowBackgroundWhiteGrayText2, resourcesProvider)));
            jSONObject.put("destructive_text_color", Theme.blendOver(color, Theme.getColor(Theme.key_text_RedRegular, resourcesProvider)));
            jSONObject.put("section_separator_color", Theme.blendOver(color, Theme.getColor(Theme.key_divider, resourcesProvider)));
            jSONObject.put("bottom_bar_bg_color", Theme.getColor(i, resourcesProvider));
            return jSONObject;
        } catch (Exception e) {
            FileLog.e(e);
            return null;
        }
    }

    public static boolean openPrivacy(final int i, final long j) {
        TL_bots.BotInfo botInfo;
        TLRPC.UserFull userFull = MessagesController.getInstance(i).getUserFull(j);
        if (userFull == null || (botInfo = userFull.bot_info) == null) {
            return false;
        }
        String str = botInfo.privacy_policy_url;
        if (str == null && !hasPrivacyCommand(userFull)) {
            str = LocaleController.getString(R.string.BotDefaultPrivacyPolicy);
        }
        if (str != null) {
            Browser.openUrl(ApplicationLoader.applicationContext, str);
            return false;
        }
        BaseFragment safeLastFragment = LaunchActivity.getSafeLastFragment();
        if (safeLastFragment == null) {
            return false;
        }
        if (!(safeLastFragment instanceof ChatActivity) || ((ChatActivity) safeLastFragment).getDialogId() != j) {
            safeLastFragment.presentFragment(ChatActivity.of(j));
        }
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                BotWebViewAttachedSheet.lambda$openPrivacy$37(i, j);
            }
        }, 150L);
        return true;
    }

    private void preloadShortcutBotIcon(TLRPC.User user, TLRPC.TL_attachMenuBot tL_attachMenuBot) {
        if (tL_attachMenuBot == null || !tL_attachMenuBot.show_in_side_menu || MediaDataController.getInstance(this.currentAccount).isShortcutAdded(this.botId, MediaDataController.SHORTCUT_TYPE_ATTACHED_BOT)) {
            return;
        }
        if (user == null) {
            user = MessagesController.getInstance(this.currentAccount).getUser(Long.valueOf(this.botId));
        }
        if (user == null || user.photo == null || FileLoader.getInstance(this.currentAccount).getPathToAttach(user.photo.photo_small, true).exists()) {
            return;
        }
        MediaDataController.getInstance(this.currentAccount).preloadImage(ImageLocation.getForUser(user, 1), 0);
    }

    private void updateActionBarColors() {
        if (this.overrideBackgroundColor) {
            return;
        }
        BackDrawable backDrawable = this.backDrawable;
        int i = Theme.key_windowBackgroundWhiteBlackText;
        backDrawable.setColor(getColor(i));
        this.backDrawable.setRotatedColor(getColor(i));
        this.actionBar.setTitleColor(getColor(i));
        this.actionBar.setItemsColor(getColor(i), false);
        this.actionBar.setItemsBackgroundColor(getColor(Theme.key_actionBarWhiteSelector), false);
        this.actionBar.setPopupBackgroundColor(getColor(Theme.key_actionBarDefaultSubmenuBackground), false);
        this.actionBar.setPopupItemsColor(getColor(Theme.key_actionBarDefaultSubmenuItem), false, false);
        this.actionBar.setPopupItemsColor(getColor(Theme.key_actionBarDefaultSubmenuItemIcon), true, false);
        this.actionBar.setPopupItemsSelectorColor(getColor(Theme.key_dialogButtonSelector), false);
    }

    private void updateLightStatusBar() {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.bots.BotWebViewAttachedSheet.updateLightStatusBar():void");
    }

    public void updateShownAnimated(final boolean z) {
        ValueAnimator valueAnimator = this.shownAnimator;
        if (valueAnimator != null) {
            valueAnimator.cancel();
        }
        ValueAnimator ofFloat = ValueAnimator.ofFloat(this.shown, z ? 1.0f : 0.0f);
        this.shownAnimator = ofFloat;
        ofFloat.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public final void onAnimationUpdate(ValueAnimator valueAnimator2) {
                BotWebViewAttachedSheet.this.lambda$updateShownAnimated$29(valueAnimator2);
            }
        });
        this.shownAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animator) {
                BotWebViewAttachedSheet.this.shown = z ? 1.0f : 0.0f;
                BotWebViewAttachedSheet.this.checkNavBarColor();
            }
        });
        this.shownAnimator.setDuration(200L);
        this.shownAnimator.setInterpolator(CubicBezierInterpolator.EASE_OUT_QUINT);
        this.shownAnimator.start();
    }

    public void attachInternal() {
        NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.didSetNewTheme);
        BottomSheetTabDialog bottomSheetTabDialog = this.dialog;
        if (bottomSheetTabDialog != null) {
            bottomSheetTabDialog.attach();
        } else if (this.fragment.getLayoutContainer() != null) {
            this.fragment.getLayoutContainer().addView(this.windowView);
        }
    }

    @Override
    public boolean attachedToParent() {
        return this.windowView != null;
    }

    public void checkNavBarColor() {
        LaunchActivity launchActivity;
        BottomSheetTabDialog bottomSheetTabDialog = this.dialog;
        if (bottomSheetTabDialog != null) {
            bottomSheetTabDialog.updateNavigationBarColor();
        } else {
            if (!attachedToParent() || (launchActivity = LaunchActivity.instance) == null) {
                return;
            }
            launchActivity.checkSystemBarColors(true, true, true, false);
        }
    }

    @Override
    public void didReceivedNotification(int i, int i2, Object... objArr) {
        if (i == NotificationCenter.webViewResultSent) {
            if (this.queryId == ((Long) objArr[0]).longValue()) {
                dismiss();
                return;
            }
            return;
        }
        if (i == NotificationCenter.didSetNewTheme) {
            this.windowView.invalidate();
            this.webViewContainer.updateFlickerBackgroundColor(getColor(Theme.key_windowBackgroundWhite));
            updateActionBarColors();
            updateLightStatusBar();
        }
    }

    @Override
    public void dismiss() {
        dismiss(false, null);
    }

    @Override
    public void dismiss(boolean z) {
        dismiss(z, null);
    }

    public void dismiss(boolean z, final Runnable runnable) {
        LaunchActivity launchActivity;
        if (this.dismissed) {
            return;
        }
        this.dismissed = true;
        AndroidUtilities.cancelRunOnUIThread(this.pollRunnable);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.webViewResultSent);
        NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.didSetNewTheme);
        updateShownAnimated(false);
        if (z && ((launchActivity = LaunchActivity.instance) == null || launchActivity.getBottomSheetTabsOverlay() == null)) {
            z = false;
        }
        if (z) {
            SpringAnimation springAnimation = this.springAnimation;
            if (springAnimation != null) {
                springAnimation.getSpring().setFinalPosition(0.0f);
                this.springAnimation.start();
            }
            LaunchActivity.instance.getBottomSheetTabsOverlay().dismissSheet(this);
        } else {
            this.webViewContainer.destroyWebView();
            this.swipeContainer.setFullSize(false);
            this.swipeContainer.stickTo(r4.getHeight() + this.windowView.measureKeyboardHeight() + (isFullSize() ? AndroidUtilities.dp(200.0f) : 0), new Runnable() {
                @Override
                public final void run() {
                    BotWebViewAttachedSheet.this.lambda$dismiss$31(runnable);
                }
            });
        }
        checkNavBarColor();
    }

    public long getBotId() {
        return this.botId;
    }

    public float getContainerTop() {
        return AndroidUtilities.isTablet() ? AndroidUtilities.lerp(this.swipeContainer.getTranslationY() + AndroidUtilities.dp(12.0f), AndroidUtilities.statusBarHeight / 2.0f, this.actionBarTransitionProgress) : AndroidUtilities.lerp(this.swipeContainer.getTranslationY(), AndroidUtilities.statusBarHeight + (ActionBar.getCurrentActionBarHeight() / 2.0f), AndroidUtilities.isTablet() ? 0.0f : this.actionBarTransitionProgress) + AndroidUtilities.dp(12.0f);
    }

    public Context getContext() {
        return this.context;
    }

    @Override
    public int getNavigationBarColor(int i) {
        return ColorUtils.blendARGB(i, this.navBarColor, this.shown);
    }

    @Override
    public WindowView mo998getWindowView() {
        return this.windowView;
    }

    @Override
    public boolean isAttachedLightStatusBar() {
        if (this.overrideBackgroundColor) {
            return !this.actionBarIsLight && this.actionBarAttached;
        }
        return !AndroidUtilities.isTablet() && ColorUtils.calculateLuminance(Theme.getColor(Theme.key_windowBackgroundWhite, null, true)) >= 0.7210000157356262d && this.actionBarAttached;
    }

    @Override
    public boolean isFullSize() {
        Boolean bool = this.fullsize;
        return bool == null ? this.defaultFullsize : bool.booleanValue();
    }

    @Override
    public boolean isFullyVisible() {
        return false;
    }

    @Override
    public boolean isShown() {
        return !this.dismissed;
    }

    @Override
    public boolean onAttachedBackPressed() {
        if (this.webViewContainer.onBackPressed()) {
            return true;
        }
        dismiss(true, null);
        return true;
    }

    public boolean onCheckDismissByUser() {
        if (!this.needCloseConfirmation) {
            dismiss();
            return true;
        }
        TLRPC.User user = MessagesController.getInstance(this.currentAccount).getUser(Long.valueOf(this.botId));
        AlertDialog create = new AlertDialog.Builder(getContext()).setTitle(user != null ? ContactsController.formatName(user.first_name, user.last_name) : null).setMessage(LocaleController.getString(R.string.BotWebViewChangesMayNotBeSaved)).setPositiveButton(LocaleController.getString(R.string.BotWebViewCloseAnyway), new DialogInterface.OnClickListener() {
            @Override
            public final void onClick(DialogInterface dialogInterface, int i) {
                BotWebViewAttachedSheet.this.lambda$onCheckDismissByUser$30(dialogInterface, i);
            }
        }).setNegativeButton(LocaleController.getString(R.string.Cancel), null).create();
        create.show();
        ((TextView) create.getButton(-1)).setTextColor(getColor(Theme.key_text_RedBold));
        return false;
    }

    @Override
    public void release() {
        BotWebViewContainer botWebViewContainer = this.webViewContainer;
        if (botWebViewContainer != null) {
            botWebViewContainer.destroyWebView();
        }
        BottomSheetTabDialog bottomSheetTabDialog = this.dialog;
        if (bottomSheetTabDialog != null) {
            bottomSheetTabDialog.detach();
            return;
        }
        this.fragment.removeSheet(this);
        WindowView windowView = this.windowView;
        if (windowView == null || !(windowView.getParent() instanceof ViewGroup)) {
            return;
        }
        ((ViewGroup) this.windowView.getParent()).removeView(this.windowView);
    }

    public void requestWebView(org.telegram.ui.ActionBar.BaseFragment r11, org.telegram.ui.bots.WebViewRequestProps r12) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.bots.BotWebViewAttachedSheet.requestWebView(org.telegram.ui.ActionBar.BaseFragment, org.telegram.ui.bots.WebViewRequestProps):void");
    }

    public boolean restoreState(org.telegram.ui.ActionBar.BaseFragment r9, org.telegram.ui.ActionBar.BottomSheetTabs.WebTabData r10) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.bots.BotWebViewAttachedSheet.restoreState(org.telegram.ui.ActionBar.BaseFragment, org.telegram.ui.ActionBar.BottomSheetTabs$WebTabData):boolean");
    }

    @Override
    public BottomSheetTabs.WebTabData saveState() {
        BottomSheetTabs.WebTabData webTabData = new BottomSheetTabs.WebTabData();
        webTabData.actionBarColor = this.actionBarColor;
        webTabData.actionBarColorKey = this.actionBarColorKey;
        webTabData.overrideActionBarColor = this.overrideBackgroundColor;
        webTabData.backgroundColor = this.backgroundPaint.getColor();
        webTabData.props = this.requestProps;
        BotWebViewContainer botWebViewContainer = this.webViewContainer;
        webTabData.ready = botWebViewContainer != null && botWebViewContainer.isPageLoaded();
        webTabData.themeIsDark = Theme.isCurrentThemeDark();
        BotWebViewContainer botWebViewContainer2 = this.webViewContainer;
        webTabData.lastUrl = botWebViewContainer2 != null ? botWebViewContainer2.getUrlLoaded() : null;
        ChatAttachAlertBotWebViewLayout.WebViewSwipeContainer webViewSwipeContainer = this.swipeContainer;
        webTabData.expanded = (webViewSwipeContainer != null && 1.0f - (Math.min(webViewSwipeContainer.getTopActionBarOffsetY(), this.swipeContainer.getTranslationY() - this.swipeContainer.getTopActionBarOffsetY()) / this.swipeContainer.getTopActionBarOffsetY()) > 0.5f) || this.forceExpnaded || isFullSize();
        webTabData.fullsize = isFullSize();
        ChatAttachAlertBotWebViewLayout.WebViewSwipeContainer webViewSwipeContainer2 = this.swipeContainer;
        webTabData.expandedOffset = webViewSwipeContainer2 != null ? webViewSwipeContainer2.getOffsetY() : Float.MAX_VALUE;
        webTabData.backButton = this.backButtonShown;
        ActionBarMenuSubItem actionBarMenuSubItem = this.settingsItem;
        webTabData.settings = actionBarMenuSubItem != null && actionBarMenuSubItem.getVisibility() == 0;
        webTabData.buttons = this.botButtons.state;
        webTabData.confirmDismiss = this.needCloseConfirmation;
        webTabData.needsContext = this.needsContext;
        ChatAttachAlertBotWebViewLayout.WebViewSwipeContainer webViewSwipeContainer3 = this.swipeContainer;
        webTabData.allowSwipes = webViewSwipeContainer3 == null || webViewSwipeContainer3.isAllowedSwipes();
        webTabData.navigationBarColor = this.navBarColor;
        BotWebViewContainer botWebViewContainer3 = this.webViewContainer;
        BotWebViewContainer.MyWebView webView = botWebViewContainer3 == null ? null : botWebViewContainer3.getWebView();
        if (webView != null) {
            this.webViewContainer.preserveWebView();
            webTabData.webView = webView;
            BotWebViewContainer botWebViewContainer4 = this.webViewContainer;
            webTabData.proxy = botWebViewContainer4 != null ? botWebViewContainer4.getBotProxy() : null;
            webTabData.viewWidth = webView.getWidth();
            webTabData.viewHeight = webView.getHeight();
            webTabData.viewScroll = webView.getScrollY();
            webView.onPause();
        }
        return webTabData;
    }

    public void setActionBarColor(final int i, boolean z, boolean z2) {
        final int i2 = this.actionBarColor;
        final BotWebViewMenuContainer$ActionBarColorsAnimating botWebViewMenuContainer$ActionBarColorsAnimating = new BotWebViewMenuContainer$ActionBarColorsAnimating();
        botWebViewMenuContainer$ActionBarColorsAnimating.setFrom(this.overrideBackgroundColor ? this.actionBarColor : 0, this.resourcesProvider);
        this.overrideBackgroundColor = z;
        this.actionBarIsLight = ColorUtils.calculateLuminance(i) < 0.7210000157356262d;
        botWebViewMenuContainer$ActionBarColorsAnimating.setTo(this.overrideBackgroundColor ? i : 0, this.resourcesProvider);
        if (z2) {
            ValueAnimator duration = ValueAnimator.ofFloat(0.0f, 1.0f).setDuration(200L);
            duration.setInterpolator(CubicBezierInterpolator.DEFAULT);
            duration.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                    BotWebViewAttachedSheet.this.lambda$setActionBarColor$34(i2, i, botWebViewMenuContainer$ActionBarColorsAnimating, valueAnimator);
                }
            });
            duration.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animator) {
                    BotWebViewAttachedSheet.this.actionBarColor = ColorUtils.blendARGB(i2, i, 1.0f);
                    BotWebViewAttachedSheet.this.checkNavBarColor();
                    BotWebViewAttachedSheet.this.windowView.invalidate();
                    BotWebViewAttachedSheet.this.actionBar.setBackgroundColor(BotWebViewAttachedSheet.this.actionBarColor);
                    botWebViewMenuContainer$ActionBarColorsAnimating.updateActionBar(BotWebViewAttachedSheet.this.actionBar, 1.0f);
                    BotWebViewAttachedSheet.this.lineColor = botWebViewMenuContainer$ActionBarColorsAnimating.getColor(Theme.key_sheet_scrollUp);
                    BotWebViewAttachedSheet.this.windowView.invalidate();
                }
            });
            duration.start();
        } else {
            this.actionBarColor = i;
            checkNavBarColor();
            this.windowView.invalidate();
            this.actionBar.setBackgroundColor(this.actionBarColor);
            botWebViewMenuContainer$ActionBarColorsAnimating.updateActionBar(this.actionBar, 1.0f);
            this.lineColor = botWebViewMenuContainer$ActionBarColorsAnimating.getColor(Theme.key_sheet_scrollUp);
            this.windowView.invalidate();
        }
        updateLightStatusBar();
        if (this.optionsItem != null) {
            boolean z3 = ColorUtils.calculateLuminance(i) < 0.7210000157356262d;
            int i3 = z3 ? -1 : -16777216;
            this.optionsItem.setPopupItemsSelectorColor(Theme.multAlpha(i3, z3 ? 0.22f : 0.12f));
            this.optionsItem.setPopupItemsColor(Theme.multAlpha(i3, 0.55f), true);
            this.optionsItem.setPopupItemsColor(i3, false);
            this.optionsItem.redrawPopup(i);
        }
    }

    public void setBackgroundColor(final int i, boolean z) {
        final int color = this.backgroundPaint.getColor();
        if (!z) {
            this.backgroundPaint.setColor(i);
            updateActionBarColors();
            this.windowView.invalidate();
        } else {
            ValueAnimator duration = ValueAnimator.ofFloat(0.0f, 1.0f).setDuration(200L);
            duration.setInterpolator(CubicBezierInterpolator.DEFAULT);
            duration.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                    BotWebViewAttachedSheet.this.lambda$setBackgroundColor$32(color, i, valueAnimator);
                }
            });
            duration.start();
        }
    }

    public void setDefaultFullsize(boolean z) {
        if (this.defaultFullsize != z) {
            this.defaultFullsize = z;
            ChatAttachAlertBotWebViewLayout.WebViewSwipeContainer webViewSwipeContainer = this.swipeContainer;
            if (webViewSwipeContainer != null) {
                webViewSwipeContainer.setFullSize(isFullSize());
            }
        }
    }

    @Override
    public boolean setDialog(BottomSheetTabDialog bottomSheetTabDialog) {
        this.dialog = bottomSheetTabDialog;
        return true;
    }

    @Override
    public void setKeyboardHeightFromParent(int i) {
    }

    @Override
    public void setLastVisible(boolean z) {
        BotWebViewContainer botWebViewContainer = this.webViewContainer;
        if (botWebViewContainer != null) {
            botWebViewContainer.setKeyboardFocusable(z);
        }
    }

    public void setNavigationBarColor(final int i, boolean z) {
        final int i2 = this.navBarColor;
        this.botButtons.setBackgroundColor(i, z);
        if (!z) {
            this.navBarColor = i;
            checkNavBarColor();
            return;
        }
        ValueAnimator duration = ValueAnimator.ofFloat(0.0f, 1.0f).setDuration(200L);
        duration.setInterpolator(CubicBezierInterpolator.DEFAULT);
        duration.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                BotWebViewAttachedSheet.this.lambda$setNavigationBarColor$33(i2, i, valueAnimator);
            }
        });
        duration.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animator) {
                BotWebViewAttachedSheet.this.navBarColor = ColorUtils.blendARGB(i2, i, 1.0f);
                BotWebViewAttachedSheet.this.checkNavBarColor();
            }
        });
        duration.start();
    }

    public void setNeedsContext(boolean z) {
        this.needsContext = z;
    }

    @Override
    public void setOnDismissListener(Runnable runnable) {
        this.onDismissListener = runnable;
    }

    public void setParentActivity(Activity activity) {
        this.parentActivity = activity;
    }

    public void setWasOpenedByLinkIntent(boolean z) {
        BotWebViewContainer botWebViewContainer = this.webViewContainer;
        if (botWebViewContainer != null) {
            botWebViewContainer.setWasOpenedByLinkIntent(z);
        }
    }

    public void show() {
        show(false);
    }

    public void show(boolean z) {
        show(z, false);
    }

    public void show(boolean z, boolean z2) {
        if (AndroidUtilities.isSafeToShow(getContext())) {
            this.windowView.setAlpha(0.0f);
            this.windowView.addOnLayoutChangeListener(new AnonymousClass9(z, z2));
            attachInternal();
        }
    }

    @Override
    public boolean showDialog(Dialog dialog) {
        return false;
    }

    public void showJustAddedBulletin() {
        TLRPC.TL_attachMenuBot tL_attachMenuBot;
        TLRPC.User user = MessagesController.getInstance(this.currentAccount).getUser(Long.valueOf(this.botId));
        Iterator<TLRPC.TL_attachMenuBot> it = MediaDataController.getInstance(this.currentAccount).getAttachMenuBots().bots.iterator();
        while (true) {
            if (!it.hasNext()) {
                tL_attachMenuBot = null;
                break;
            } else {
                tL_attachMenuBot = it.next();
                if (tL_attachMenuBot.bot_id == this.botId) {
                    break;
                }
            }
        }
        if (tL_attachMenuBot == null) {
            return;
        }
        boolean z = tL_attachMenuBot.show_in_side_menu;
        final String formatString = (z && tL_attachMenuBot.show_in_attach_menu) ? LocaleController.formatString("BotAttachMenuShortcatAddedAttachAndSide", R.string.BotAttachMenuShortcatAddedAttachAndSide, user.first_name) : z ? LocaleController.formatString("BotAttachMenuShortcatAddedSide", R.string.BotAttachMenuShortcatAddedSide, user.first_name) : LocaleController.formatString("BotAttachMenuShortcatAddedAttach", R.string.BotAttachMenuShortcatAddedAttach, user.first_name);
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                BotWebViewAttachedSheet.this.lambda$showJustAddedBulletin$0(formatString);
            }
        }, 200L);
    }
}
