package org.telegram.ui.bots;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.text.SpannableStringBuilder;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowInsets;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.TextView;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.ColorUtils;
import androidx.core.graphics.Insets;
import androidx.core.math.MathUtils;
import androidx.core.util.Consumer;
import androidx.core.view.WindowInsetsCompat;
import androidx.dynamicanimation.animation.DynamicAnimation;
import androidx.dynamicanimation.animation.SpringAnimation;
import androidx.dynamicanimation.animation.SpringForce;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import org.json.JSONObject;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.AnimationNotificationsLocker;
import org.telegram.messenger.BotFullscreenButtons;
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
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.BottomSheetTabDialog;
import org.telegram.ui.ActionBar.BottomSheetTabs;
import org.telegram.ui.ActionBar.BottomSheetTabsOverlay;
import org.telegram.ui.ActionBar.INavigationLayout;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ArticleViewer;
import org.telegram.ui.ChatActivity;
import org.telegram.ui.ChatActivity$$ExternalSyntheticLambda230;
import org.telegram.ui.Components.Bulletin;
import org.telegram.ui.Components.BulletinFactory;
import org.telegram.ui.Components.CubicBezierInterpolator;
import org.telegram.ui.Components.ItemOptions;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.OverlayActionBarLayoutDialog;
import org.telegram.ui.Components.PasscodeView;
import org.telegram.ui.Components.SimpleFloatPropertyCompat;
import org.telegram.ui.Components.SizeNotifierFrameLayout;
import org.telegram.ui.DialogsActivity;
import org.telegram.ui.LaunchActivity;
import org.telegram.ui.PaymentFormActivity;
import org.telegram.ui.ProfileActivity;
import org.telegram.ui.Stars.StarsController;
import org.telegram.ui.TopicsFragment;
import org.telegram.ui.bots.BotButtons;
import org.telegram.ui.bots.BotDownloads;
import org.telegram.ui.bots.BotWebViewSheet;
import org.telegram.ui.bots.ChatAttachAlertBotWebViewLayout;
import org.telegram.ui.web.BotWebViewContainer;

public class BotWebViewSheet extends Dialog implements NotificationCenter.NotificationCenterDelegate, BottomSheetTabsOverlay.Sheet {
    private ActionBar actionBar;
    private int actionBarColor;
    private int actionBarColorKey;
    private boolean actionBarIsLight;
    private FrameLayout.LayoutParams actionBarLayoutParams;
    private Paint actionBarPaint;
    private Drawable actionBarShadow;
    private float actionBarTransitionProgress;
    public boolean attached;
    private boolean backButtonShown;
    private ValueAnimator backgroundColorAnimator;
    private Paint backgroundPaint;
    private BotButtons botButtons;
    private FrameLayout.LayoutParams botButtonsLayoutParams;
    private long botId;
    private BottomSheetTabs bottomTabs;
    private BottomSheetTabs.ClipTools bottomTabsClip;
    private FrameLayout bulletinContainer;
    private FrameLayout.LayoutParams bulletinContainerLayoutParams;
    private String buttonText;
    private int currentAccount;
    private TLRPC.BotApp currentWebApp;
    private boolean defaultFullsize;
    private Paint dimPaint;
    private boolean dismissed;
    private Bulletin downloadBulletin;
    private BotDownloads.DownloadBulletin downloadBulletinLayout;
    private String errorCode;
    private ArticleViewer.ErrorContainer errorContainer;
    private boolean errorShown;
    private HashMap fileItems;
    private boolean forceExpnaded;
    public boolean fromTab;
    private boolean fullscreen;
    private ValueAnimator fullscreenAnimator;
    private BotFullscreenButtons fullscreenButtons;
    private boolean fullscreenInProgress;
    private float fullscreenProgress;
    private float fullscreenTransitionProgress;
    private Boolean fullsize;
    private boolean hasSettings;
    private boolean ignoreLayout;
    private final Rect insets;
    private int keyboardInset;
    private BotDownloads.FileDownload lastBulletinFile;
    private long lastSwipeTime;
    private BottomSheetTabs.WebTabData lastTab;
    private int lineColor;
    private Paint linePaint;
    private int navBarColor;
    private final Rect navInsets;
    private boolean needCloseConfirmation;
    private boolean needsContext;
    private ValueAnimator openAnimator;
    private float openedProgress;
    private ItemOptions options;
    private BotFullscreenButtons.OptionsIcon optionsIcon;
    private ActionBarMenuItem optionsItem;
    private boolean orientationLocked;
    private boolean overrideActionBarColor;
    private boolean overrideBackgroundColor;
    private Activity parentActivity;
    private PasscodeView passcodeView;
    private long peerId;
    private Runnable pollRunnable;
    private ChatAttachAlertBotWebViewLayout.WebProgressView progressView;
    private long queryId;
    private int replyToMsgId;
    private WebViewRequestProps requestProps;
    private boolean resetOffsetY;
    private Theme.ResourcesProvider resourcesProvider;
    private BotSensors sensors;
    public boolean showExpanded;
    public float showOffsetY;
    private boolean silent;
    private SpringAnimation springAnimation;
    private boolean superDismissed;
    private ChatAttachAlertBotWebViewLayout.WebViewSwipeContainer swipeContainer;
    private int swipeContainerFromHeight;
    private int swipeContainerFromWidth;
    private FrameLayout.LayoutParams swipeContainerLayoutParams;
    Drawable verifiedDrawable;
    private Boolean wasLightStatusBar;
    private BotWebViewContainer webViewContainer;
    private WindowView windowView;
    public static HashSet activeSheets = new HashSet();
    private static final SimpleFloatPropertyCompat ACTION_BAR_TRANSITION_PROGRESS_VALUE = new SimpleFloatPropertyCompat("actionBarTransitionProgress", new SimpleFloatPropertyCompat.Getter() {
        @Override
        public final float get(Object obj) {
            float f;
            f = ((BotWebViewSheet) obj).actionBarTransitionProgress;
            return f;
        }
    }, new SimpleFloatPropertyCompat.Setter() {
        @Override
        public final void set(Object obj, float f) {
            BotWebViewSheet.lambda$static$3((BotWebViewSheet) obj, f);
        }
    }).setMultiplier(100.0f);
    private static int shownLockedBots = 0;

    public class AnonymousClass11 implements View.OnLayoutChangeListener {
        AnonymousClass11() {
        }

        @Override
        public void onLayoutChange(View view, int i, int i2, int i3, int i4, int i5, int i6, int i7, int i8) {
            view.removeOnLayoutChangeListener(this);
            BotWebViewSheet.this.swipeContainer.setSwipeOffsetY(BotWebViewSheet.this.swipeContainer.getHeight());
            BotWebViewSheet.this.windowView.setAlpha(1.0f);
            BotWebViewSheet botWebViewSheet = BotWebViewSheet.this;
            if (botWebViewSheet.showOffsetY != Float.MAX_VALUE) {
                botWebViewSheet.swipeContainer.setSwipeOffsetAnimationDisallowed(true);
                BotWebViewSheet.this.swipeContainer.setOffsetY(BotWebViewSheet.this.showOffsetY);
                BotWebViewSheet.this.swipeContainer.setSwipeOffsetAnimationDisallowed(false);
            }
            BotWebViewSheet.this.webViewContainer.invalidateViewPortHeight(true, true);
            final AnimationNotificationsLocker animationNotificationsLocker = new AnimationNotificationsLocker();
            animationNotificationsLocker.lock();
            BotWebViewSheet botWebViewSheet2 = BotWebViewSheet.this;
            if (botWebViewSheet2.showExpanded || botWebViewSheet2.isFullSize()) {
                BotWebViewSheet.this.swipeContainer.stickTo((-BotWebViewSheet.this.swipeContainer.getOffsetY()) + BotWebViewSheet.this.swipeContainer.getTopActionBarOffsetY(), new Runnable() {
                    @Override
                    public final void run() {
                        AnimationNotificationsLocker.this.unlock();
                    }
                });
            } else {
                ((SpringAnimation) new SpringAnimation(BotWebViewSheet.this.swipeContainer, ChatAttachAlertBotWebViewLayout.WebViewSwipeContainer.SWIPE_OFFSET_Y, 0.0f).setSpring(new SpringForce(0.0f).setDampingRatio(0.75f).setStiffness(500.0f)).addEndListener(new DynamicAnimation.OnAnimationEndListener() {
                    @Override
                    public final void onAnimationEnd(DynamicAnimation dynamicAnimation, boolean z, float f, float f2) {
                        AnimationNotificationsLocker.this.unlock();
                    }
                })).start();
            }
            BotWebViewSheet.this.swipeContainer.opened = true;
            if (!BotWebViewSheet.this.fullscreen || BotWebViewSheet.this.fullscreenButtons == null) {
                return;
            }
            BotWebViewSheet.this.fullscreenButtons.setAlpha(0.0f);
            BotWebViewSheet.this.fullscreenButtons.animate().alpha(1.0f).setDuration(220L).start();
        }
    }

    public class AnonymousClass3 implements BotWebViewContainer.Delegate {
        private boolean sentWebViewData;
        final Context val$context;
        final Theme.ResourcesProvider val$resourcesProvider;

        AnonymousClass3(Context context, Theme.ResourcesProvider resourcesProvider) {
            this.val$context = context;
            this.val$resourcesProvider = resourcesProvider;
        }

        public static Bulletin lambda$onEmojiStatusGranted$10(TLRPC.User user, BulletinFactory.UndoObject undoObject, BulletinFactory bulletinFactory) {
            return bulletinFactory.createUsersBulletin(Arrays.asList(user), AndroidUtilities.replaceTags(LocaleController.formatString(R.string.BotEmojiStatusPermissionRequestGranted, UserObject.getUserName(user))), null, undoObject).setDuration(5000);
        }

        public static Bulletin lambda$onEmojiStatusGranted$6(TLRPC.TL_error tL_error, BulletinFactory bulletinFactory) {
            return bulletinFactory.makeForError(tL_error);
        }

        public void lambda$onEmojiStatusGranted$7(TLObject tLObject, final TLRPC.TL_error tL_error) {
            if (tLObject instanceof TLRPC.TL_boolTrue) {
                BotWebViewSheet.this.webViewContainer.notifyEmojiStatusAccess("cancelled");
            } else {
                BotWebViewSheet.this.showBulletin(new Utilities.CallbackReturn() {
                    @Override
                    public final Object run(Object obj) {
                        Bulletin lambda$onEmojiStatusGranted$6;
                        lambda$onEmojiStatusGranted$6 = BotWebViewSheet.AnonymousClass3.lambda$onEmojiStatusGranted$6(TLRPC.TL_error.this, (BulletinFactory) obj);
                        return lambda$onEmojiStatusGranted$6;
                    }
                });
            }
        }

        public void lambda$onEmojiStatusGranted$8(final TLObject tLObject, final TLRPC.TL_error tL_error) {
            AndroidUtilities.runOnUIThread(new Runnable() {
                @Override
                public final void run() {
                    BotWebViewSheet.AnonymousClass3.this.lambda$onEmojiStatusGranted$7(tLObject, tL_error);
                }
            });
        }

        public void lambda$onEmojiStatusGranted$9() {
            TL_bots.toggleUserEmojiStatusPermission toggleuseremojistatuspermission = new TL_bots.toggleUserEmojiStatusPermission();
            toggleuseremojistatuspermission.bot = MessagesController.getInstance(BotWebViewSheet.this.currentAccount).getInputUser(BotWebViewSheet.this.botId);
            toggleuseremojistatuspermission.enabled = false;
            ConnectionsManager.getInstance(BotWebViewSheet.this.currentAccount).sendRequest(toggleuseremojistatuspermission, new RequestDelegate() {
                @Override
                public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                    BotWebViewSheet.AnonymousClass3.this.lambda$onEmojiStatusGranted$8(tLObject, tL_error);
                }
            });
        }

        public static Bulletin lambda$onEmojiStatusSet$11(TLRPC.Document document, BulletinFactory bulletinFactory) {
            return bulletinFactory.createEmojiBulletin(document, LocaleController.getString(R.string.BotEmojiStatusUpdated));
        }

        public void lambda$onLocationGranted$2() {
            BotLocation.get(BotWebViewSheet.this.getContext(), BotWebViewSheet.this.currentAccount, BotWebViewSheet.this.botId).setGranted(false, null);
        }

        public static Bulletin lambda$onLocationGranted$3(TLRPC.User user, BulletinFactory.UndoObject undoObject, BulletinFactory bulletinFactory) {
            return bulletinFactory.createUsersBulletin(Arrays.asList(user), AndroidUtilities.replaceTags(LocaleController.formatString(R.string.BotLocationPermissionRequestGranted, UserObject.getUserName(user))), null, undoObject).setDuration(5000);
        }

        public void lambda$onLocationGranted$4() {
            BaseFragment safeLastFragment = LaunchActivity.getSafeLastFragment();
            if (safeLastFragment == null || safeLastFragment.getParentLayout() == null) {
                return;
            }
            INavigationLayout parentLayout = safeLastFragment.getParentLayout();
            safeLastFragment.presentFragment(ProfileActivity.of(BotWebViewSheet.this.botId));
            AndroidUtilities.scrollToFragmentRow(parentLayout, "botPermissionLocation");
            BotWebViewSheet.this.dismiss(true);
        }

        public static Bulletin lambda$onLocationGranted$5(SpannableStringBuilder spannableStringBuilder, BulletinFactory bulletinFactory) {
            return bulletinFactory.createSimpleBulletinDetail(R.raw.error, spannableStringBuilder).setDuration(5000);
        }

        public void lambda$onSendWebViewData$1(TLObject tLObject, TLRPC.TL_error tL_error) {
            if (tLObject instanceof TLRPC.TL_updates) {
                MessagesController.getInstance(BotWebViewSheet.this.currentAccount).processUpdates((TLRPC.TL_updates) tLObject, false);
            }
            final BotWebViewSheet botWebViewSheet = BotWebViewSheet.this;
            AndroidUtilities.runOnUIThread(new Runnable() {
                @Override
                public final void run() {
                    BotWebViewSheet.this.lambda$openOptions$39();
                }
            });
        }

        public static Bulletin lambda$onSharedTo$0(String str, BulletinFactory bulletinFactory) {
            return bulletinFactory.createSimpleBulletin(R.raw.forward, AndroidUtilities.replaceTags(str));
        }

        public void lambda$onWebAppOpenInvoice$13(String str, String str2) {
            BotWebViewSheet.this.webViewContainer.onInvoiceStatusUpdate(str, str2);
        }

        public void lambda$onWebAppOpenInvoice$14(OverlayActionBarLayoutDialog overlayActionBarLayoutDialog, String str, PaymentFormActivity.InvoiceStatus invoiceStatus) {
            if (invoiceStatus != PaymentFormActivity.InvoiceStatus.PENDING) {
                overlayActionBarLayoutDialog.dismiss();
            }
            BotWebViewSheet.this.webViewContainer.onInvoiceStatusUpdate(str, invoiceStatus.name().toLowerCase(Locale.ROOT));
        }

        public boolean lambda$onWebAppSwitchInlineQuery$15(TLRPC.User user, String str, OverlayActionBarLayoutDialog overlayActionBarLayoutDialog, DialogsActivity dialogsActivity, ArrayList arrayList, CharSequence charSequence, boolean z, boolean z2, int i, TopicsFragment topicsFragment) {
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
            if (BotWebViewSheet.this.parentActivity instanceof LaunchActivity) {
                BaseFragment lastFragment = ((LaunchActivity) BotWebViewSheet.this.parentActivity).getActionBarLayout().getLastFragment();
                if (MessagesController.getInstance(BotWebViewSheet.this.currentAccount).checkCanOpenChat(bundle, lastFragment)) {
                    overlayActionBarLayoutDialog.dismiss();
                    BotWebViewSheet.this.dismissed = true;
                    AndroidUtilities.cancelRunOnUIThread(BotWebViewSheet.this.pollRunnable);
                    BotWebViewSheet.this.webViewContainer.destroyWebView();
                    NotificationCenter.getInstance(BotWebViewSheet.this.currentAccount).removeObserver(BotWebViewSheet.this, NotificationCenter.webViewResultSent);
                    NotificationCenter.getGlobalInstance().removeObserver(BotWebViewSheet.this, NotificationCenter.didSetNewTheme);
                    if (!BotWebViewSheet.this.superDismissed) {
                        BotWebViewSheet.super.dismiss();
                        BotWebViewSheet.this.superDismissed = true;
                    }
                    lastFragment.presentFragment(new INavigationLayout.NavigationParams(new ChatActivity(bundle)).setRemoveLast(true));
                }
            }
            return true;
        }

        @Override
        public BotSensors getBotSensors() {
            if (BotWebViewSheet.this.sensors == null) {
                BotWebViewSheet botWebViewSheet = BotWebViewSheet.this;
                botWebViewSheet.sensors = new BotSensors(this.val$context, botWebViewSheet.botId);
                BotWebViewSheet.this.sensors.attachWebView(BotWebViewSheet.this.webViewContainer.getWebView());
            }
            return BotWebViewSheet.this.sensors;
        }

        @Override
        public boolean isClipboardAvailable() {
            return MediaDataController.getInstance(BotWebViewSheet.this.currentAccount).botInAttachMenu(BotWebViewSheet.this.botId);
        }

        @Override
        public void onCloseRequested(Runnable runnable) {
            BotWebViewSheet.this.dismiss(runnable);
        }

        @Override
        public void onCloseToTabs() {
            BotWebViewSheet.this.dismiss(true);
        }

        @Override
        public void onEmojiStatusGranted(boolean z) {
            final TLRPC.User user = MessagesController.getInstance(BotWebViewSheet.this.currentAccount).getUser(Long.valueOf(BotWebViewSheet.this.botId));
            if (z) {
                final BulletinFactory.UndoObject undoObject = new BulletinFactory.UndoObject();
                undoObject.onUndo = new Runnable() {
                    @Override
                    public final void run() {
                        BotWebViewSheet.AnonymousClass3.this.lambda$onEmojiStatusGranted$9();
                    }
                };
                BotWebViewSheet.this.showBulletin(new Utilities.CallbackReturn() {
                    @Override
                    public final Object run(Object obj) {
                        Bulletin lambda$onEmojiStatusGranted$10;
                        lambda$onEmojiStatusGranted$10 = BotWebViewSheet.AnonymousClass3.lambda$onEmojiStatusGranted$10(TLRPC.User.this, undoObject, (BulletinFactory) obj);
                        return lambda$onEmojiStatusGranted$10;
                    }
                });
            }
        }

        @Override
        public void onEmojiStatusSet(final TLRPC.Document document) {
            BotWebViewSheet.this.showBulletin(new Utilities.CallbackReturn() {
                @Override
                public final Object run(Object obj) {
                    Bulletin lambda$onEmojiStatusSet$11;
                    lambda$onEmojiStatusSet$11 = BotWebViewSheet.AnonymousClass3.lambda$onEmojiStatusSet$11(TLRPC.Document.this, (BulletinFactory) obj);
                    return lambda$onEmojiStatusSet$11;
                }
            });
        }

        @Override
        public String onFullscreenRequested(boolean z) {
            if (BotWebViewSheet.this.fullscreen != z) {
                BotWebViewSheet.this.setFullscreen(z, true);
                return null;
            }
            if (BotWebViewSheet.this.fullscreen) {
                return "ALREADY_FULLSCREEN";
            }
            return null;
        }

        @Override
        public void onInstantClose() {
            onCloseRequested(null);
        }

        @Override
        public void onLocationGranted(boolean z) {
            final TLRPC.User user = MessagesController.getInstance(BotWebViewSheet.this.currentAccount).getUser(Long.valueOf(BotWebViewSheet.this.botId));
            if (z) {
                final BulletinFactory.UndoObject undoObject = new BulletinFactory.UndoObject();
                undoObject.undoText = LocaleController.getString(R.string.Undo);
                undoObject.onUndo = new Runnable() {
                    @Override
                    public final void run() {
                        BotWebViewSheet.AnonymousClass3.this.lambda$onLocationGranted$2();
                    }
                };
                BotWebViewSheet.this.showBulletin(new Utilities.CallbackReturn() {
                    @Override
                    public final Object run(Object obj) {
                        Bulletin lambda$onLocationGranted$3;
                        lambda$onLocationGranted$3 = BotWebViewSheet.AnonymousClass3.lambda$onLocationGranted$3(TLRPC.User.this, undoObject, (BulletinFactory) obj);
                        return lambda$onLocationGranted$3;
                    }
                });
                return;
            }
            final SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder();
            spannableStringBuilder.append((CharSequence) AndroidUtilities.replaceTags(LocaleController.formatString(R.string.BotLocationPermissionRequestDeniedApp, UserObject.getUserName(user))));
            spannableStringBuilder.append((CharSequence) " ");
            spannableStringBuilder.append(AndroidUtilities.replaceArrows(AndroidUtilities.makeClickable(LocaleController.getString(R.string.BotLocationPermissionRequestDeniedAppSettings), new Runnable() {
                @Override
                public final void run() {
                    BotWebViewSheet.AnonymousClass3.this.lambda$onLocationGranted$4();
                }
            }), true));
            BotWebViewSheet.this.showBulletin(new Utilities.CallbackReturn() {
                @Override
                public final Object run(Object obj) {
                    Bulletin lambda$onLocationGranted$5;
                    lambda$onLocationGranted$5 = BotWebViewSheet.AnonymousClass3.lambda$onLocationGranted$5(spannableStringBuilder, (BulletinFactory) obj);
                    return lambda$onLocationGranted$5;
                }
            });
        }

        @Override
        public void onOpenBackFromTabs() {
            if (BotWebViewSheet.this.lastTab != null) {
                BottomSheetTabs bottomSheetTabs = LaunchActivity.instance.getBottomSheetTabs();
                if (bottomSheetTabs != null) {
                    bottomSheetTabs.openTab(BotWebViewSheet.this.lastTab);
                }
                BotWebViewSheet.this.lastTab = null;
            }
        }

        @Override
        public void onOrientationLockChanged(boolean z) {
            BotWebViewSheet.this.lockOrientation(z);
        }

        @Override
        public void onSendWebViewData(String str) {
            if (BotWebViewSheet.this.queryId != 0 || this.sentWebViewData) {
                return;
            }
            this.sentWebViewData = true;
            TLRPC.TL_messages_sendWebViewData tL_messages_sendWebViewData = new TLRPC.TL_messages_sendWebViewData();
            tL_messages_sendWebViewData.bot = MessagesController.getInstance(BotWebViewSheet.this.currentAccount).getInputUser(BotWebViewSheet.this.botId);
            tL_messages_sendWebViewData.random_id = Utilities.random.nextLong();
            tL_messages_sendWebViewData.button_text = BotWebViewSheet.this.buttonText;
            tL_messages_sendWebViewData.data = str;
            ConnectionsManager.getInstance(BotWebViewSheet.this.currentAccount).sendRequest(tL_messages_sendWebViewData, new RequestDelegate() {
                @Override
                public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                    BotWebViewSheet.AnonymousClass3.this.lambda$onSendWebViewData$1(tLObject, tL_error);
                }
            });
        }

        @Override
        public void onSetBackButtonVisible(boolean z) {
            AndroidUtilities.updateImageViewImageAnimated(BotWebViewSheet.this.actionBar.getBackButton(), BotWebViewSheet.this.backButtonShown = z ? R.drawable.ic_ab_back : R.drawable.ic_close_white);
            if (BotWebViewSheet.this.fullscreenButtons != null) {
                BotWebViewSheet.this.fullscreenButtons.setBack(z, true);
            }
        }

        @Override
        public void onSetSettingsButtonVisible(boolean z) {
            BotWebViewSheet.this.hasSettings = z;
        }

        @Override
        public void onSetupMainButton(boolean z, boolean z2, String str, int i, int i2, boolean z3, boolean z4) {
            BotWebViewSheet.this.botButtons.setMainState(BotButtons.ButtonState.of(z, z2, z3, z4, str, i, i2), true);
            if (BotWebViewSheet.this.fullscreen) {
                BotWebViewSheet.this.updateFullscreenLayout();
                BotWebViewSheet.this.updateWindowFlags();
            }
        }

        @Override
        public void onSetupSecondaryButton(boolean z, boolean z2, String str, int i, int i2, boolean z3, boolean z4, String str2) {
            BotWebViewSheet.this.botButtons.setSecondaryState(BotButtons.ButtonState.of(z, z2, z3, z4, str, i, i2, str2), true);
            if (BotWebViewSheet.this.fullscreen) {
                BotWebViewSheet.this.updateFullscreenLayout();
                BotWebViewSheet.this.updateWindowFlags();
            }
        }

        @Override
        public void onSharedTo(ArrayList arrayList) {
            final String formatString = arrayList.size() == 1 ? LocaleController.formatString(R.string.BotSharedToOne, MessagesController.getInstance(BotWebViewSheet.this.currentAccount).getPeerName(((Long) arrayList.get(0)).longValue())) : LocaleController.formatPluralString("BotSharedToMany", arrayList.size(), new Object[0]);
            BotWebViewSheet.this.showBulletin(new Utilities.CallbackReturn() {
                @Override
                public final Object run(Object obj) {
                    Bulletin lambda$onSharedTo$0;
                    lambda$onSharedTo$0 = BotWebViewSheet.AnonymousClass3.lambda$onSharedTo$0(formatString, (BulletinFactory) obj);
                    return lambda$onSharedTo$0;
                }
            });
        }

        @Override
        public void onWebAppBackgroundChanged(boolean z, int i) {
            BotWebViewContainer.Delegate.CC.$default$onWebAppBackgroundChanged(this, z, i);
        }

        @Override
        public void onWebAppExpand() {
            if (BotWebViewSheet.this.swipeContainer.isSwipeInProgress()) {
                return;
            }
            BotWebViewSheet.this.swipeContainer.stickTo((-BotWebViewSheet.this.swipeContainer.getOffsetY()) + BotWebViewSheet.this.swipeContainer.getTopActionBarOffsetY());
        }

        @Override
        public void onWebAppOpenInvoice(TLRPC.InputInvoice inputInvoice, final String str, TLObject tLObject) {
            PaymentFormActivity paymentFormActivity;
            BaseFragment lastFragment = ((LaunchActivity) BotWebViewSheet.this.parentActivity).getActionBarLayout().getLastFragment();
            if (tLObject instanceof TLRPC.TL_payments_paymentFormStars) {
                AndroidUtilities.hideKeyboard(BotWebViewSheet.this.windowView);
                final AlertDialog alertDialog = new AlertDialog(BotWebViewSheet.this.getContext(), 3);
                alertDialog.showDelayed(150L);
                StarsController.getInstance(BotWebViewSheet.this.currentAccount).openPaymentForm(null, inputInvoice, (TLRPC.TL_payments_paymentFormStars) tLObject, new Runnable() {
                    @Override
                    public final void run() {
                        AlertDialog.this.dismiss();
                    }
                }, new Utilities.Callback() {
                    @Override
                    public final void run(Object obj) {
                        BotWebViewSheet.AnonymousClass3.this.lambda$onWebAppOpenInvoice$13(str, (String) obj);
                    }
                });
                return;
            }
            if (tLObject instanceof TLRPC.PaymentForm) {
                TLRPC.PaymentForm paymentForm = (TLRPC.PaymentForm) tLObject;
                MessagesController.getInstance(BotWebViewSheet.this.currentAccount).putUsers(paymentForm.users, false);
                paymentFormActivity = new PaymentFormActivity(paymentForm, str, lastFragment);
            } else {
                paymentFormActivity = tLObject instanceof TLRPC.PaymentReceipt ? new PaymentFormActivity((TLRPC.PaymentReceipt) tLObject) : null;
            }
            if (paymentFormActivity != null) {
                BotWebViewSheet.this.swipeContainer.stickTo((-BotWebViewSheet.this.swipeContainer.getOffsetY()) + BotWebViewSheet.this.swipeContainer.getTopActionBarOffsetY());
                AndroidUtilities.hideKeyboard(BotWebViewSheet.this.windowView);
                final OverlayActionBarLayoutDialog overlayActionBarLayoutDialog = new OverlayActionBarLayoutDialog(this.val$context, this.val$resourcesProvider);
                overlayActionBarLayoutDialog.show();
                paymentFormActivity.setPaymentFormCallback(new PaymentFormActivity.PaymentFormCallback() {
                    @Override
                    public final void onInvoiceStatusChanged(PaymentFormActivity.InvoiceStatus invoiceStatus) {
                        BotWebViewSheet.AnonymousClass3.this.lambda$onWebAppOpenInvoice$14(overlayActionBarLayoutDialog, str, invoiceStatus);
                    }
                });
                paymentFormActivity.setResourcesProvider(this.val$resourcesProvider);
                overlayActionBarLayoutDialog.addFragment(paymentFormActivity);
            }
        }

        @Override
        public void onWebAppReady() {
            BotWebViewContainer.Delegate.CC.$default$onWebAppReady(this);
        }

        @Override
        public void onWebAppSetActionBarColor(int i, int i2, boolean z) {
            BotWebViewSheet.this.actionBarColorKey = i;
            BotWebViewSheet.this.setActionBarColor(i2, z, true);
        }

        @Override
        public void onWebAppSetBackgroundColor(int i) {
            BotWebViewSheet.this.setBackgroundColor(i, true, true);
        }

        @Override
        public void onWebAppSetNavigationBarColor(int i) {
            BotWebViewSheet.this.setNavigationBarColor(i, true);
        }

        @Override
        public void onWebAppSetupClosingBehavior(boolean z) {
            BotWebViewSheet.this.needCloseConfirmation = z;
        }

        @Override
        public void onWebAppSwipingBehavior(boolean z) {
            if (BotWebViewSheet.this.swipeContainer != null) {
                BotWebViewSheet.this.swipeContainer.setAllowSwipes(z);
            }
        }

        @Override
        public void onWebAppSwitchInlineQuery(final TLRPC.User user, final String str, List list) {
            if (list.isEmpty()) {
                if (BotWebViewSheet.this.parentActivity instanceof LaunchActivity) {
                    BaseFragment lastFragment = ((LaunchActivity) BotWebViewSheet.this.parentActivity).getActionBarLayout().getLastFragment();
                    if (lastFragment instanceof ChatActivity) {
                        ((ChatActivity) lastFragment).getChatActivityEnterView().setFieldText("@" + UserObject.getPublicUsername(user) + " " + str);
                        BotWebViewSheet.this.lambda$openOptions$39();
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
            AndroidUtilities.hideKeyboard(BotWebViewSheet.this.windowView);
            final OverlayActionBarLayoutDialog overlayActionBarLayoutDialog = new OverlayActionBarLayoutDialog(this.val$context, this.val$resourcesProvider);
            dialogsActivity.setDelegate(new DialogsActivity.DialogsActivityDelegate() {
                @Override
                public final boolean didSelectDialogs(DialogsActivity dialogsActivity2, ArrayList arrayList, CharSequence charSequence, boolean z, boolean z2, int i, TopicsFragment topicsFragment) {
                    boolean lambda$onWebAppSwitchInlineQuery$15;
                    lambda$onWebAppSwitchInlineQuery$15 = BotWebViewSheet.AnonymousClass3.this.lambda$onWebAppSwitchInlineQuery$15(user, str, overlayActionBarLayoutDialog, dialogsActivity2, arrayList, charSequence, z, z2, i, topicsFragment);
                    return lambda$onWebAppSwitchInlineQuery$15;
                }
            });
            overlayActionBarLayoutDialog.show();
            overlayActionBarLayoutDialog.addFragment(dialogsActivity);
        }
    }

    public class WindowView extends SizeNotifierFrameLayout implements BottomSheetTabsOverlay.SheetView {
        private final Path clipPath;
        private boolean drawingFromOverlay;
        private final Paint navbarPaint;
        private final RectF rect;

        public WindowView(Context context) {
            super(context);
            setClipChildren(false);
            setClipToPadding(false);
            setWillNotDraw(false);
            this.navbarPaint = new Paint(1);
            this.rect = new RectF();
            this.clipPath = new Path();
        }

        @Override
        public void dispatchDraw(Canvas canvas) {
            boolean z;
            if (this.drawingFromOverlay) {
                return;
            }
            if (BotWebViewSheet.this.passcodeView.getVisibility() != 0 && BotWebViewSheet.this.fullscreenProgress < 1.0f && BotWebViewSheet.this.fullscreenProgress > 0.0f) {
                this.navbarPaint.setColor(Theme.multAlpha(BotWebViewSheet.this.navBarColor, BotWebViewSheet.this.openedProgress));
                if (BotWebViewSheet.this.navInsets.left > 0) {
                    canvas.drawRect(0.0f, 0.0f, BotWebViewSheet.this.navInsets.left, getHeight(), this.navbarPaint);
                }
                if (BotWebViewSheet.this.navInsets.top > 0) {
                    canvas.drawRect(0.0f, 0.0f, getWidth(), BotWebViewSheet.this.navInsets.top, this.navbarPaint);
                }
                if (BotWebViewSheet.this.navInsets.bottom > 0) {
                    canvas.drawRect(0.0f, getHeight() - BotWebViewSheet.this.navInsets.bottom, getWidth(), getHeight(), this.navbarPaint);
                }
                if (BotWebViewSheet.this.navInsets.right > 0) {
                    canvas.drawRect(getWidth() - BotWebViewSheet.this.navInsets.right, 0.0f, getWidth(), getHeight(), this.navbarPaint);
                }
            }
            if (BotWebViewSheet.this.bottomTabsClip == null || AndroidUtilities.isTablet()) {
                z = false;
            } else {
                canvas.save();
                canvas.translate(BotWebViewSheet.this.insets.left * (1.0f - BotWebViewSheet.this.fullscreenProgress), 0.0f);
                BotWebViewSheet.this.bottomTabsClip.clip(canvas, true, false, AndroidUtilities.lerp((getWidth() - BotWebViewSheet.this.insets.left) - BotWebViewSheet.this.insets.right, getWidth(), BotWebViewSheet.this.fullscreenProgress), getHeight(), 1.0f - BotWebViewSheet.this.fullscreenProgress);
                canvas.translate((-BotWebViewSheet.this.insets.left) * (1.0f - BotWebViewSheet.this.fullscreenProgress), 0.0f);
                z = true;
            }
            super.dispatchDraw(canvas);
            if (z) {
                canvas.restore();
            }
            if (BotWebViewSheet.this.passcodeView.getVisibility() != 0) {
                this.navbarPaint.setColor(Theme.multAlpha(BotWebViewSheet.this.navBarColor, BotWebViewSheet.this.openedProgress));
                if (BotWebViewSheet.this.navInsets.left > 0) {
                    canvas.drawRect(0.0f, 0.0f, BotWebViewSheet.this.navInsets.left * (1.0f - BotWebViewSheet.this.fullscreenProgress), getHeight(), this.navbarPaint);
                }
                if (BotWebViewSheet.this.navInsets.top > 0) {
                    canvas.drawRect(0.0f, 0.0f, getWidth(), BotWebViewSheet.this.navInsets.top * (1.0f - BotWebViewSheet.this.fullscreenProgress), this.navbarPaint);
                }
                if (BotWebViewSheet.this.navInsets.bottom > 0) {
                    canvas.drawRect(0.0f, getHeight() - (BotWebViewSheet.this.navInsets.bottom * ((BotWebViewSheet.this.botButtons == null || BotWebViewSheet.this.botButtons.getTotalHeight() <= 0) ? 1.0f - BotWebViewSheet.this.fullscreenProgress : 1.0f)), getWidth(), getHeight(), this.navbarPaint);
                }
                if (BotWebViewSheet.this.navInsets.right > 0) {
                    canvas.drawRect(getWidth() - (BotWebViewSheet.this.navInsets.right * (1.0f - BotWebViewSheet.this.fullscreenProgress)), 0.0f, getWidth(), getHeight(), this.navbarPaint);
                }
            }
        }

        @Override
        public boolean dispatchTouchEvent(MotionEvent motionEvent) {
            LaunchActivity launchActivity = LaunchActivity.instance;
            BottomSheetTabs bottomSheetTabs = launchActivity != null ? launchActivity.getBottomSheetTabs() : null;
            if (bottomSheetTabs != null && BotWebViewSheet.this.insets != null) {
                int height = (int) (bottomSheetTabs.getHeight(true) * (1.0f - BotWebViewSheet.this.fullscreenProgress));
                if (motionEvent.getY() >= (getHeight() - BotWebViewSheet.this.insets.bottom) - height && motionEvent.getY() <= getHeight() - BotWebViewSheet.this.insets.bottom && !AndroidUtilities.isTablet()) {
                    return bottomSheetTabs.touchEvent(motionEvent.getAction(), motionEvent.getX(), motionEvent.getY() - ((getHeight() - BotWebViewSheet.this.insets.bottom) - height));
                }
            }
            return super.dispatchTouchEvent(motionEvent);
        }

        @Override
        public void draw(Canvas canvas) {
            if (this.drawingFromOverlay) {
                return;
            }
            super.draw(canvas);
            float f = AndroidUtilities.isTablet() ? 0.0f : BotWebViewSheet.this.actionBarTransitionProgress;
            BotWebViewSheet.this.linePaint.setColor(BotWebViewSheet.this.lineColor);
            BotWebViewSheet.this.linePaint.setAlpha((int) (BotWebViewSheet.this.linePaint.getAlpha() * (1.0f - (Math.min(0.5f, f) / 0.5f)) * (1.0f - BotWebViewSheet.this.fullscreenProgress)));
            canvas.save();
            float f2 = 1.0f - f;
            float lerp = AndroidUtilities.isTablet() ? AndroidUtilities.lerp(BotWebViewSheet.this.swipeContainer.getTranslationY() + AndroidUtilities.dp(12.0f), AndroidUtilities.statusBarHeight / 2.0f, BotWebViewSheet.this.actionBarTransitionProgress) : AndroidUtilities.lerp(BotWebViewSheet.this.swipeContainer.getTranslationY(), AndroidUtilities.statusBarHeight + (ActionBar.getCurrentActionBarHeight() / 2.0f), f) + AndroidUtilities.dp(12.0f);
            canvas.scale(f2, f2, getWidth() / 2.0f, lerp);
            canvas.drawLine((getWidth() / 2.0f) - AndroidUtilities.dp(16.0f), lerp, (getWidth() / 2.0f) + AndroidUtilities.dp(16.0f), lerp, BotWebViewSheet.this.linePaint);
            canvas.restore();
            BotWebViewSheet.this.actionBarShadow.setAlpha((int) (BotWebViewSheet.this.actionBar.getAlpha() * 255.0f));
            float y = BotWebViewSheet.this.actionBar.getY() + BotWebViewSheet.this.actionBar.getTranslationY() + BotWebViewSheet.this.actionBar.getHeight();
            BotWebViewSheet.this.actionBarShadow.setBounds(BotWebViewSheet.this.insets.left, (int) y, getWidth() - BotWebViewSheet.this.insets.right, (int) (y + BotWebViewSheet.this.actionBarShadow.getIntrinsicHeight()));
            BotWebViewSheet.this.actionBarShadow.draw(canvas);
        }

        @Override
        protected boolean drawChild(Canvas canvas, View view, long j) {
            boolean z;
            if (view != BotWebViewSheet.this.swipeContainer || !BotWebViewSheet.this.fullscreenInProgress || BotWebViewSheet.this.swipeContainerFromHeight <= 0 || BotWebViewSheet.this.swipeContainerFromWidth <= 0) {
                z = false;
            } else {
                canvas.save();
                canvas.clipRect(view.getX(), view.getY(), view.getX() + AndroidUtilities.lerp(BotWebViewSheet.this.swipeContainerFromWidth, view.getWidth(), BotWebViewSheet.this.fullscreenTransitionProgress), view.getY() + AndroidUtilities.lerp(BotWebViewSheet.this.swipeContainerFromHeight, view.getHeight(), BotWebViewSheet.this.fullscreenTransitionProgress));
                z = true;
            }
            boolean drawChild = super.drawChild(canvas, view, j);
            if (z) {
                canvas.restore();
            }
            return drawChild;
        }

        @Override
        public float drawInto(Canvas canvas, RectF rectF, float f, RectF rectF2, float f2, boolean z) {
            this.rect.set(BotWebViewSheet.this.swipeContainer.getLeft(), BotWebViewSheet.this.swipeContainer.getTranslationY() + AndroidUtilities.dp(24.0f), BotWebViewSheet.this.swipeContainer.getRight(), getHeight());
            AndroidUtilities.lerpCentered(this.rect, rectF, f, rectF2);
            canvas.save();
            this.clipPath.rewind();
            float lerp = AndroidUtilities.lerp(AndroidUtilities.dp(16.0f) * (AndroidUtilities.isTablet() ? 1.0f : 1.0f - BotWebViewSheet.this.actionBarTransitionProgress), AndroidUtilities.dp(10.0f), f);
            this.clipPath.addRoundRect(rectF2, lerp, lerp, Path.Direction.CW);
            canvas.clipPath(this.clipPath);
            canvas.drawPaint(BotWebViewSheet.this.backgroundPaint);
            if (BotWebViewSheet.this.swipeContainer != null) {
                canvas.save();
                canvas.translate(rectF2.left, Math.max(BotWebViewSheet.this.swipeContainer.getY(), rectF2.top) + (f * AndroidUtilities.dp(51.0f)));
                BotWebViewSheet.this.swipeContainer.draw(canvas);
                canvas.restore();
            }
            canvas.restore();
            return lerp;
        }

        @Override
        public RectF getRect() {
            this.rect.set(BotWebViewSheet.this.swipeContainer.getLeft(), BotWebViewSheet.this.swipeContainer.getTranslationY() + AndroidUtilities.dp(24.0f), BotWebViewSheet.this.swipeContainer.getRight(), getHeight());
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
            if (BotWebViewSheet.this.passcodeView.getVisibility() != 0) {
                canvas.save();
                if (BotWebViewSheet.this.bottomTabsClip != null) {
                    BotWebViewSheet.this.bottomTabsClip.clip(canvas, false, false, getWidth(), getHeight(), 1.0f - BotWebViewSheet.this.fullscreenProgress);
                }
                if (!BotWebViewSheet.this.overrideBackgroundColor) {
                    int color = BotWebViewSheet.this.getColor(Theme.key_windowBackgroundWhite);
                    BotWebViewSheet.this.backgroundPaint.setColor(color);
                    BotWebViewSheet.this.webViewContainer.setFlickerViewColor(color);
                    if (BotWebViewSheet.this.errorContainer != null) {
                        BotWebViewSheet.this.errorContainer.setDark(AndroidUtilities.computePerceivedBrightness(BotWebViewSheet.this.backgroundPaint.getColor()) <= 0.721f, false);
                        BotWebViewSheet.this.errorContainer.setBackgroundColor(BotWebViewSheet.this.backgroundPaint.getColor());
                    }
                }
                RectF rectF = AndroidUtilities.rectTmp;
                rectF.set(0.0f, 0.0f, getWidth(), getHeight());
                canvas.drawRect(rectF, BotWebViewSheet.this.dimPaint);
                int height = BotWebViewSheet.this.bottomTabs != null ? BotWebViewSheet.this.bottomTabs.getHeight(true) : 0;
                BotWebViewSheet.this.actionBarPaint.setColor(BotWebViewSheet.this.actionBarColor);
                float dp = AndroidUtilities.dp(16.0f) * (AndroidUtilities.isTablet() ? 1.0f : 1.0f - BotWebViewSheet.this.actionBarTransitionProgress);
                rectF.set(AndroidUtilities.lerp(BotWebViewSheet.this.swipeContainer.getLeft(), 0, BotWebViewSheet.this.fullscreenProgress), AndroidUtilities.lerp(BotWebViewSheet.this.swipeContainer.getTranslationY(), 0.0f, BotWebViewSheet.this.actionBarTransitionProgress), BotWebViewSheet.this.swipeContainer.getRight(), BotWebViewSheet.this.swipeContainer.getTranslationY() + AndroidUtilities.dp(24.0f) + dp);
                canvas.drawRoundRect(rectF, dp, dp, BotWebViewSheet.this.actionBarPaint);
                rectF.set(AndroidUtilities.lerp(BotWebViewSheet.this.swipeContainer.getLeft(), 0, BotWebViewSheet.this.fullscreenProgress), BotWebViewSheet.this.swipeContainer.getTranslationY() + AndroidUtilities.dp(24.0f), AndroidUtilities.lerp(BotWebViewSheet.this.swipeContainer.getRight(), getWidth(), BotWebViewSheet.this.fullscreenProgress), getHeight() - height);
                canvas.drawRect(rectF, BotWebViewSheet.this.backgroundPaint);
                canvas.restore();
            }
        }

        @Override
        public boolean onTouchEvent(MotionEvent motionEvent) {
            if (motionEvent.getAction() != 0 || (motionEvent.getY() > AndroidUtilities.lerp(BotWebViewSheet.this.swipeContainer.getTranslationY() + AndroidUtilities.dp(24.0f), 0.0f, BotWebViewSheet.this.actionBarTransitionProgress) && motionEvent.getX() <= BotWebViewSheet.this.swipeContainer.getRight() && motionEvent.getX() >= BotWebViewSheet.this.swipeContainer.getLeft())) {
                return super.onTouchEvent(motionEvent);
            }
            BotWebViewSheet.this.dismiss(true, null);
            return true;
        }

        @Override
        public void setDrawingFromOverlay(boolean z) {
            if (this.drawingFromOverlay != z) {
                this.drawingFromOverlay = z;
                invalidate();
                BotWebViewSheet.this.updateWindowFlags();
                if (LaunchActivity.instance == null || !BotWebViewSheet.this.fullscreen) {
                    return;
                }
                LaunchActivity.instance.requestCustomNavigationBar();
                LaunchActivity.instance.setNavigationBarColor(BotWebViewSheet.this.navBarColor, false);
            }
        }
    }

    public BotWebViewSheet(Context context, Theme.ResourcesProvider resourcesProvider) {
        super(context, R.style.TransparentDialog);
        this.actionBarTransitionProgress = 0.0f;
        this.navInsets = new Rect();
        this.insets = new Rect();
        this.keyboardInset = 0;
        this.linePaint = new Paint(1);
        this.dimPaint = new Paint();
        this.backgroundPaint = new Paint(1);
        this.actionBarPaint = new Paint(1);
        this.pollRunnable = new Runnable() {
            @Override
            public final void run() {
                BotWebViewSheet.this.lambda$new$6();
            }
        };
        this.actionBarColorKey = -1;
        this.defaultFullsize = false;
        this.fullsize = null;
        this.fileItems = new HashMap();
        this.superDismissed = false;
        this.resetOffsetY = true;
        this.attached = false;
        this.resourcesProvider = resourcesProvider;
        this.lineColor = Theme.getColor(Theme.key_sheet_scrollUp);
        ChatAttachAlertBotWebViewLayout.WebViewSwipeContainer webViewSwipeContainer = new ChatAttachAlertBotWebViewLayout.WebViewSwipeContainer(context) {
            @Override
            protected void onMeasure(int r5, int r6) {
                throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.bots.BotWebViewSheet.AnonymousClass1.onMeasure(int, int):void");
            }

            @Override
            public void requestLayout() {
                if (BotWebViewSheet.this.ignoreLayout) {
                    return;
                }
                super.requestLayout();
            }

            @Override
            public void setTranslationY(float f) {
                super.setTranslationY(f);
                if (BotWebViewSheet.this.fullscreenButtons != null) {
                    BotWebViewSheet.this.fullscreenButtons.setTranslationY(AndroidUtilities.dp(24.0f) + f);
                }
                if (BotWebViewSheet.this.bulletinContainer != null) {
                    BotWebViewSheet.this.bulletinContainer.setTranslationY(AndroidUtilities.lerp(ActionBar.getCurrentActionBarHeight() - AndroidUtilities.dp(24.0f), BotWebViewSheet.this.insets.top + AndroidUtilities.dp(70.0f), BotWebViewSheet.this.fullscreenProgress) + BotWebViewSheet.this.swipeContainer.getTranslationY());
                }
            }
        };
        this.swipeContainer = webViewSwipeContainer;
        webViewSwipeContainer.setAllowFullSizeSwipe(true);
        this.swipeContainer.setShouldWaitWebViewScroll(true);
        int i = Theme.key_windowBackgroundWhite;
        BotWebViewContainer botWebViewContainer = new BotWebViewContainer(context, resourcesProvider, getColor(i), true) {
            @Override
            protected void onErrorShown(boolean z, int i2, String str) {
                if (z) {
                    BotWebViewSheet.this.createErrorContainer();
                    BotWebViewSheet.this.errorContainer.set(UserObject.getUserName(MessagesController.getInstance(BotWebViewSheet.this.currentAccount).getUser(Long.valueOf(BotWebViewSheet.this.botId))), str);
                    BotWebViewSheet.this.errorContainer.setDark(AndroidUtilities.computePerceivedBrightness(BotWebViewSheet.this.backgroundPaint.getColor()) <= 0.721f, false);
                    BotWebViewSheet.this.errorContainer.setBackgroundColor(BotWebViewSheet.this.backgroundPaint.getColor());
                    BotWebViewSheet.this.errorCode = str;
                }
                AndroidUtilities.updateViewVisibilityAnimated(BotWebViewSheet.this.errorContainer, BotWebViewSheet.this.errorShown = z, 1.0f, false);
                invalidate();
            }

            @Override
            public void onWebViewCreated(BotWebViewContainer.MyWebView myWebView) {
                super.onWebViewCreated(myWebView);
                BotWebViewSheet.this.swipeContainer.setWebView(myWebView);
                if (BotWebViewSheet.this.sensors != null) {
                    BotWebViewSheet.this.sensors.attachWebView(myWebView);
                }
                BotWebViewSheet.this.fullscreenButtons.setWebView(myWebView);
            }

            @Override
            public void onWebViewDestroyed(BotWebViewContainer.MyWebView myWebView) {
                if (BotWebViewSheet.this.sensors != null) {
                    BotWebViewSheet.this.sensors.detachWebView(myWebView);
                }
                BotWebViewSheet.this.fullscreenButtons.setWebView(null);
            }
        };
        this.webViewContainer = botWebViewContainer;
        botWebViewContainer.setDelegate(new AnonymousClass3(context, resourcesProvider));
        this.linePaint.setStyle(Paint.Style.FILL_AND_STROKE);
        this.linePaint.setStrokeWidth(AndroidUtilities.dp(4.0f));
        this.linePaint.setStrokeCap(Paint.Cap.ROUND);
        this.dimPaint.setColor(1073741824);
        this.actionBarColor = getColor(i);
        this.navBarColor = getColor(Theme.key_windowBackgroundGray);
        AndroidUtilities.setNavigationBarColor(getWindow(), this.navBarColor, false);
        WindowView windowView = new WindowView(context);
        this.windowView = windowView;
        windowView.setDelegate(new SizeNotifierFrameLayout.SizeNotifierFrameLayoutDelegate() {
            @Override
            public final void onSizeChanged(int i2, boolean z) {
                BotWebViewSheet.this.lambda$new$7(i2, z);
            }
        });
        WindowView windowView2 = this.windowView;
        ChatAttachAlertBotWebViewLayout.WebViewSwipeContainer webViewSwipeContainer2 = this.swipeContainer;
        FrameLayout.LayoutParams createFrame = LayoutHelper.createFrame(-1, -1, 49);
        this.swipeContainerLayoutParams = createFrame;
        windowView2.addView(webViewSwipeContainer2, createFrame);
        BotButtons botButtons = new BotButtons(getContext(), resourcesProvider) {
            @Override
            protected void onMeasure(int i2, int i3) {
                if (!BotWebViewSheet.this.fullscreen && AndroidUtilities.isTablet() && !AndroidUtilities.isInMultiwindow && !AndroidUtilities.isSmallTablet()) {
                    Point point = AndroidUtilities.displaySize;
                    i2 = View.MeasureSpec.makeMeasureSpec((int) (Math.min(point.x, point.y) * 0.8f), 1073741824);
                }
                super.onMeasure(i2, i3);
            }

            @Override
            public void setTranslationY(float f) {
                super.setTranslationY(f);
            }
        };
        this.botButtons = botButtons;
        botButtons.setOnButtonClickListener(new Utilities.Callback() {
            @Override
            public final void run(Object obj) {
                BotWebViewSheet.this.lambda$new$8((Boolean) obj);
            }
        });
        this.botButtons.setOnResizeListener(new Runnable() {
            @Override
            public final void run() {
                BotWebViewSheet.this.lambda$new$9();
            }
        });
        WindowView windowView3 = this.windowView;
        BotButtons botButtons2 = this.botButtons;
        FrameLayout.LayoutParams createFrame2 = LayoutHelper.createFrame(-1, -2, 81);
        this.botButtonsLayoutParams = createFrame2;
        windowView3.addView(botButtons2, createFrame2);
        BotFullscreenButtons botFullscreenButtons = new BotFullscreenButtons(getContext());
        this.fullscreenButtons = botFullscreenButtons;
        botFullscreenButtons.setAlpha(0.0f);
        this.fullscreenButtons.setVisibility(8);
        if (!MessagesController.getInstance(this.currentAccount).disableBotFullscreenBlur) {
            this.fullscreenButtons.setParentRenderNode(this.swipeContainer.getRenderNode());
        }
        this.windowView.addView(this.fullscreenButtons, LayoutHelper.createFrame(-1, -1, 119));
        this.fullscreenButtons.setOnCloseClickListener(new Runnable() {
            @Override
            public final void run() {
                BotWebViewSheet.this.lambda$new$10();
            }
        });
        this.fullscreenButtons.setOnCollapseClickListener(new Runnable() {
            @Override
            public final void run() {
                BotWebViewSheet.this.lambda$new$11();
            }
        });
        this.fullscreenButtons.setOnMenuClickListener(new Runnable() {
            @Override
            public final void run() {
                BotWebViewSheet.this.openOptions();
            }
        });
        FrameLayout frameLayout = new FrameLayout(context);
        this.bulletinContainer = frameLayout;
        WindowView windowView4 = this.windowView;
        FrameLayout.LayoutParams createFrame3 = LayoutHelper.createFrame(-1, 200, 55);
        this.bulletinContainerLayoutParams = createFrame3;
        windowView4.addView(frameLayout, createFrame3);
        this.actionBarShadow = ContextCompat.getDrawable(getContext(), R.drawable.header_shadow).mutate();
        ActionBar actionBar = new ActionBar(context, resourcesProvider) {
            @Override
            public void onMeasure(int i2, int i3) {
                if (AndroidUtilities.isTablet() && !AndroidUtilities.isInMultiwindow && !AndroidUtilities.isSmallTablet()) {
                    Point point = AndroidUtilities.displaySize;
                    i2 = View.MeasureSpec.makeMeasureSpec((int) (Math.min(point.x, point.y) * 0.8f), 1073741824);
                }
                super.onMeasure(i2, i3);
            }
        };
        this.actionBar = actionBar;
        actionBar.setBackgroundColor(0);
        this.actionBar.setBackButtonImage(R.drawable.ic_close_white);
        updateActionBarColors();
        this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
            @Override
            public void onItemClick(int i2) {
                if (i2 == -1) {
                    BotWebViewSheet.this.onCheckDismissByUser();
                }
            }
        });
        this.actionBar.setAlpha(0.0f);
        WindowView windowView5 = this.windowView;
        ActionBar actionBar2 = this.actionBar;
        FrameLayout.LayoutParams createFrame4 = LayoutHelper.createFrame(-1, -2, 49);
        this.actionBarLayoutParams = createFrame4;
        windowView5.addView(actionBar2, createFrame4);
        WindowView windowView6 = this.windowView;
        ChatAttachAlertBotWebViewLayout.WebProgressView webProgressView = new ChatAttachAlertBotWebViewLayout.WebProgressView(context, resourcesProvider) {
            @Override
            protected void onMeasure(int i2, int i3) {
                if (AndroidUtilities.isTablet() && !AndroidUtilities.isInMultiwindow && !AndroidUtilities.isSmallTablet()) {
                    Point point = AndroidUtilities.displaySize;
                    i2 = View.MeasureSpec.makeMeasureSpec((int) (Math.min(point.x, point.y) * 0.8f), 1073741824);
                }
                super.onMeasure(i2, i3);
            }
        };
        this.progressView = webProgressView;
        windowView6.addView(webProgressView, LayoutHelper.createFrame(-1, -2.0f, 81, 0.0f, 0.0f, 0.0f, 0.0f));
        this.webViewContainer.setWebViewProgressListener(new Consumer() {
            @Override
            public final void accept(Object obj) {
                BotWebViewSheet.this.lambda$new$13((Float) obj);
            }
        });
        this.swipeContainer.addView(this.webViewContainer, LayoutHelper.createFrame(-1, -1.0f));
        this.swipeContainer.setScrollListener(new Runnable() {
            @Override
            public final void run() {
                BotWebViewSheet.this.lambda$new$14();
            }
        });
        this.swipeContainer.setScrollEndListener(new Runnable() {
            @Override
            public final void run() {
                BotWebViewSheet.this.lambda$new$15();
            }
        });
        this.swipeContainer.setDelegate(new ChatAttachAlertBotWebViewLayout.WebViewSwipeContainer.Delegate() {
            @Override
            public final void onDismiss(boolean z) {
                BotWebViewSheet.this.lambda$new$16(z);
            }
        });
        this.swipeContainer.setIsKeyboardVisible(new GenericProvider() {
            @Override
            public final Object provide(Object obj) {
                Boolean lambda$new$17;
                lambda$new$17 = BotWebViewSheet.this.lambda$new$17((Void) obj);
                return lambda$new$17;
            }
        });
        PasscodeView passcodeView = new PasscodeView(context);
        this.passcodeView = passcodeView;
        this.windowView.addView(passcodeView, LayoutHelper.createFrame(-1, -1.0f));
        setContentView(this.windowView, new ViewGroup.LayoutParams(-1, -1));
        updateFullscreenLayout();
        LaunchActivity launchActivity = LaunchActivity.instance;
        BottomSheetTabs bottomSheetTabs = launchActivity != null ? launchActivity.getBottomSheetTabs() : null;
        this.bottomTabs = bottomSheetTabs;
        if (bottomSheetTabs != null) {
            final WindowView windowView7 = this.windowView;
            Objects.requireNonNull(windowView7);
            bottomSheetTabs.listen(new Runnable() {
                @Override
                public final void run() {
                    BotWebViewSheet.WindowView.this.invalidate();
                }
            }, new Runnable() {
                @Override
                public final void run() {
                    BotWebViewSheet.this.relayout();
                }
            });
            this.bottomTabsClip = new BottomSheetTabs.ClipTools(this.bottomTabs);
        }
    }

    private void applyAppBotSettings(TL_bots.botAppSettings botappsettings, boolean z) {
        if (botappsettings == null) {
            return;
        }
        boolean isCurrentThemeDark = Theme.isCurrentThemeDark();
        int i = botappsettings.flags;
        boolean z2 = ((isCurrentThemeDark ? 4 : 2) & i) != 0;
        if ((i & (isCurrentThemeDark ? 16 : 8)) != 0) {
            setActionBarColor((isCurrentThemeDark ? botappsettings.header_dark_color : botappsettings.header_color) | (-16777216), true, z);
        }
        if (z2) {
            setBackgroundColor((isCurrentThemeDark ? botappsettings.background_dark_color : botappsettings.background_color) | (-16777216), true, z);
            setNavigationBarColor((isCurrentThemeDark ? botappsettings.background_dark_color : botappsettings.background_color) | (-16777216), z);
        }
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
        new AlertDialog.Builder(LaunchActivity.getLastFragment().getContext()).setTitle(LocaleController.getString(R.string.BotRemoveFromMenuTitle)).setMessage(AndroidUtilities.replaceTags(LocaleController.formatString(R.string.BotRemoveFromMenu, tL_attachMenuBot.short_name))).setPositiveButton(LocaleController.getString(R.string.OK), new DialogInterface.OnClickListener() {
            @Override
            public final void onClick(DialogInterface dialogInterface, int i2) {
                BotWebViewSheet.lambda$deleteBot$44(i, j, tL_attachMenuBot, runnable, dialogInterface, i2);
            }
        }).setNegativeButton(LocaleController.getString(R.string.Cancel), null).show();
    }

    public int getColor(int i) {
        return Theme.getColor(i, this.resourcesProvider);
    }

    public void lambda$createErrorContainer$51(View view) {
        BotWebViewContainer.MyWebView webView = this.webViewContainer.getWebView();
        if (webView != null) {
            webView.reload();
        }
    }

    public static void lambda$deleteBot$42(int i) {
        MediaDataController.getInstance(i).loadAttachMenuBots(false, true);
    }

    public static void lambda$deleteBot$43(final int i, TLObject tLObject, TLRPC.TL_error tL_error) {
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                BotWebViewSheet.lambda$deleteBot$42(i);
            }
        });
    }

    public static void lambda$deleteBot$44(final int i, long j, TLRPC.TL_attachMenuBot tL_attachMenuBot, Runnable runnable, DialogInterface dialogInterface, int i2) {
        TLRPC.TL_messages_toggleBotInAttachMenu tL_messages_toggleBotInAttachMenu = new TLRPC.TL_messages_toggleBotInAttachMenu();
        tL_messages_toggleBotInAttachMenu.bot = MessagesController.getInstance(i).getInputUser(j);
        tL_messages_toggleBotInAttachMenu.enabled = false;
        ConnectionsManager.getInstance(i).sendRequest(tL_messages_toggleBotInAttachMenu, new RequestDelegate() {
            @Override
            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                BotWebViewSheet.lambda$deleteBot$43(i, tLObject, tL_error);
            }
        }, 66);
        tL_attachMenuBot.show_in_side_menu = false;
        NotificationCenter.getInstance(i).lambda$postNotificationNameOnUIThread$1(NotificationCenter.attachMenuBotsDidLoad, new Object[0]);
        MediaDataController.getInstance(i).uninstallShortcut(j, MediaDataController.SHORTCUT_TYPE_ATTACHED_BOT);
        if (runnable != null) {
            runnable.run();
        }
    }

    public void lambda$dismiss$46(Runnable runnable) {
        if (!this.superDismissed) {
            super.dismiss();
            this.superDismissed = true;
        }
        if (runnable != null) {
            runnable.run();
        }
    }

    public void lambda$new$10() {
        if (this.webViewContainer.onBackPressed()) {
            return;
        }
        onCheckDismissByUser();
    }

    public void lambda$new$11() {
        this.forceExpnaded = true;
        dismiss(true, null);
    }

    public void lambda$new$12(ValueAnimator valueAnimator) {
        this.progressView.setAlpha(((Float) valueAnimator.getAnimatedValue()).floatValue());
    }

    public void lambda$new$13(Float f) {
        this.progressView.setLoadProgressAnimated(f.floatValue());
        if (f.floatValue() == 1.0f) {
            ValueAnimator duration = ValueAnimator.ofFloat(1.0f, 0.0f).setDuration(200L);
            duration.setInterpolator(CubicBezierInterpolator.DEFAULT);
            duration.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                    BotWebViewSheet.this.lambda$new$12(valueAnimator);
                }
            });
            duration.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animator) {
                    BotWebViewSheet.this.progressView.setVisibility(8);
                }
            });
            duration.start();
        }
    }

    public void lambda$new$14() {
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
            float f = (1.0f - (Math.min(this.swipeContainer.getTopActionBarOffsetY(), this.swipeContainer.getTranslationY() - this.swipeContainer.getTopActionBarOffsetY()) / this.swipeContainer.getTopActionBarOffsetY()) > 0.5f ? 1 : 0) * 100.0f;
            if (this.springAnimation.getSpring().getFinalPosition() != f) {
                this.springAnimation.getSpring().setFinalPosition(f);
                this.springAnimation.start();
            }
        }
        if (this.fullscreen) {
            int i2 = this.insets.bottom;
        } else {
            Math.max(0.0f, this.swipeContainer.getSwipeOffsetY());
        }
        this.lastSwipeTime = System.currentTimeMillis();
    }

    public void lambda$new$15() {
        this.webViewContainer.invalidateViewPortHeight(true);
    }

    public void lambda$new$16(boolean z) {
        if (this.fullscreen && z) {
            return;
        }
        dismiss(true, null);
    }

    public Boolean lambda$new$17(Void r2) {
        return Boolean.valueOf(this.windowView.getKeyboardHeight() >= AndroidUtilities.dp(20.0f));
    }

    public void lambda$new$4(TLRPC.TL_error tL_error) {
        if (this.dismissed) {
            return;
        }
        if (tL_error != null) {
            lambda$openOptions$39();
        } else {
            AndroidUtilities.runOnUIThread(this.pollRunnable, 60000L);
        }
    }

    public void lambda$new$5(TLObject tLObject, final TLRPC.TL_error tL_error) {
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                BotWebViewSheet.this.lambda$new$4(tL_error);
            }
        });
    }

    public void lambda$new$6() {
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
                BotWebViewSheet.this.lambda$new$5(tLObject, tL_error);
            }
        });
    }

    public void lambda$new$7(int i, boolean z) {
        if (i > AndroidUtilities.dp(20.0f)) {
            ChatAttachAlertBotWebViewLayout.WebViewSwipeContainer webViewSwipeContainer = this.swipeContainer;
            webViewSwipeContainer.stickTo((-webViewSwipeContainer.getOffsetY()) + this.swipeContainer.getTopActionBarOffsetY());
        }
    }

    public void lambda$new$8(Boolean bool) {
        if (this.webViewContainer != null) {
            if (bool.booleanValue()) {
                this.webViewContainer.onMainButtonPressed();
            } else {
                this.webViewContainer.onSecondaryButtonPressed();
            }
        }
    }

    public void lambda$new$9() {
        this.swipeContainer.requestLayout();
    }

    public void lambda$onCheckDismissByUser$45(DialogInterface dialogInterface, int i) {
        lambda$openOptions$39();
    }

    public WindowInsets lambda$onCreate$18(View view, WindowInsets windowInsets) {
        int stableInsetLeft;
        int stableInsetTop;
        int stableInsetRight;
        int stableInsetBottom;
        WindowInsets consumeSystemWindowInsets;
        WindowInsets windowInsets2;
        WindowInsetsCompat windowInsetsCompat = WindowInsetsCompat.toWindowInsetsCompat(windowInsets, view);
        Insets insets = windowInsetsCompat.getInsets(WindowInsetsCompat.Type.navigationBars());
        this.navInsets.set(insets.left, insets.top, insets.right, insets.bottom);
        Insets insets2 = windowInsetsCompat.getInsets(WindowInsetsCompat.Type.displayCutout() | WindowInsetsCompat.Type.systemBars());
        Rect rect = this.insets;
        int i = insets2.left;
        stableInsetLeft = windowInsets.getStableInsetLeft();
        int max = Math.max(i, stableInsetLeft);
        int i2 = insets2.top;
        stableInsetTop = windowInsets.getStableInsetTop();
        int max2 = Math.max(i2, stableInsetTop);
        int i3 = insets2.right;
        stableInsetRight = windowInsets.getStableInsetRight();
        int max3 = Math.max(i3, stableInsetRight);
        int i4 = insets2.bottom;
        stableInsetBottom = windowInsets.getStableInsetBottom();
        rect.set(max, max2, max3, Math.max(i4, stableInsetBottom));
        int i5 = Build.VERSION.SDK_INT;
        if (i5 <= 28) {
            Rect rect2 = this.insets;
            rect2.top = Math.max(rect2.top, AndroidUtilities.getStatusBarHeight(getContext()));
        }
        int i6 = windowInsetsCompat.getInsets(WindowInsetsCompat.Type.ime()).bottom;
        if (i6 <= this.insets.bottom || i6 <= AndroidUtilities.dp(20.0f)) {
            i6 = 0;
        }
        this.keyboardInset = i6;
        updateFullscreenLayout();
        if (i5 >= 30) {
            windowInsets2 = WindowInsets.CONSUMED;
            return windowInsets2;
        }
        consumeSystemWindowInsets = windowInsets.consumeSystemWindowInsets();
        return consumeSystemWindowInsets;
    }

    public static void lambda$openOptions$32() {
    }

    public void lambda$openOptions$34() {
        Activity activity = this.parentActivity;
        if (activity instanceof LaunchActivity) {
            ((LaunchActivity) activity).lambda$runLinkRequest$93(ChatActivity.of(this.botId));
        }
        dismiss(true);
    }

    public void lambda$openOptions$35() {
        this.webViewContainer.onSettingsButtonPressed();
    }

    public void lambda$openOptions$36() {
        if (this.webViewContainer.getWebView() != null) {
            this.webViewContainer.getWebView().animate().cancel();
            this.webViewContainer.getWebView().animate().alpha(0.0f).start();
        }
        this.progressView.setLoadProgress(0.0f);
        this.progressView.setAlpha(1.0f);
        this.progressView.setVisibility(0);
        this.webViewContainer.setBotUser(MessagesController.getInstance(this.currentAccount).getUser(Long.valueOf(this.botId)));
        this.webViewContainer.loadFlickerAndSettingsItem(this.currentAccount, this.botId, null);
        this.webViewContainer.reload();
    }

    public void lambda$openOptions$37() {
        MediaDataController.getInstance(this.currentAccount).installShortcut(this.botId, MediaDataController.SHORTCUT_TYPE_ATTACHED_BOT);
    }

    public void lambda$openOptions$38() {
        Browser.openUrl(getContext(), LocaleController.getString(R.string.BotWebViewToSLink));
    }

    public void lambda$openOptions$40() {
        deleteBot(this.currentAccount, this.botId, new Runnable() {
            @Override
            public final void run() {
                BotWebViewSheet.this.lambda$openOptions$39();
            }
        });
    }

    public void lambda$requestWebView$19(TLRPC.UserFull userFull) {
        TL_bots.BotInfo botInfo;
        TL_bots.botAppSettings botappsettings;
        if (userFull == null || (botInfo = userFull.bot_info) == null || (botappsettings = botInfo.app_settings) == null) {
            return;
        }
        applyAppBotSettings(botappsettings, true);
    }

    public void lambda$requestWebView$20(final TLRPC.UserFull userFull) {
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                BotWebViewSheet.this.lambda$requestWebView$19(userFull);
            }
        });
    }

    public void lambda$requestWebView$21(View view) {
        openOptions();
    }

    public void lambda$requestWebView$22(TLRPC.TL_error tL_error, TLObject tLObject) {
        WebViewRequestProps webViewRequestProps;
        if (tL_error == null && (webViewRequestProps = this.requestProps) != null) {
            webViewRequestProps.applyResponse(tLObject);
            loadFromResponse();
        }
    }

    public void lambda$requestWebView$23(final TLObject tLObject, final TLRPC.TL_error tL_error) {
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                BotWebViewSheet.this.lambda$requestWebView$22(tL_error, tLObject);
            }
        });
    }

    public void lambda$requestWebView$24(TLRPC.TL_error tL_error, TLObject tLObject) {
        WebViewRequestProps webViewRequestProps;
        if (tL_error == null && (webViewRequestProps = this.requestProps) != null) {
            webViewRequestProps.applyResponse(tLObject);
            loadFromResponse();
        }
    }

    public void lambda$requestWebView$25(final TLObject tLObject, final TLRPC.TL_error tL_error) {
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                BotWebViewSheet.this.lambda$requestWebView$24(tL_error, tLObject);
            }
        });
    }

    public void lambda$requestWebView$26(TLRPC.TL_error tL_error, TLObject tLObject) {
        WebViewRequestProps webViewRequestProps;
        if (tL_error == null && (webViewRequestProps = this.requestProps) != null) {
            webViewRequestProps.applyResponse(tLObject);
            loadFromResponse();
        }
    }

    public void lambda$requestWebView$27(final TLObject tLObject, final TLRPC.TL_error tL_error) {
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                BotWebViewSheet.this.lambda$requestWebView$26(tL_error, tLObject);
            }
        });
    }

    public void lambda$requestWebView$28(TLRPC.TL_error tL_error, TLObject tLObject) {
        WebViewRequestProps webViewRequestProps;
        if (tL_error == null && (webViewRequestProps = this.requestProps) != null) {
            webViewRequestProps.applyResponse(tLObject);
            loadFromResponse();
        }
    }

    public void lambda$requestWebView$29(final TLObject tLObject, final TLRPC.TL_error tL_error) {
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                BotWebViewSheet.this.lambda$requestWebView$28(tL_error, tLObject);
            }
        });
    }

    public void lambda$requestWebView$30(TLRPC.TL_error tL_error, TLObject tLObject) {
        WebViewRequestProps webViewRequestProps;
        if (tL_error == null && (webViewRequestProps = this.requestProps) != null) {
            webViewRequestProps.applyResponse(tLObject);
            loadFromResponse();
        }
    }

    public void lambda$requestWebView$31(final TLObject tLObject, final TLRPC.TL_error tL_error) {
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                BotWebViewSheet.this.lambda$requestWebView$30(tL_error, tLObject);
            }
        });
    }

    public void lambda$setActionBarColor$50(int i, int i2, BotWebViewMenuContainer$ActionBarColorsAnimating botWebViewMenuContainer$ActionBarColorsAnimating, ValueAnimator valueAnimator) {
        float floatValue = ((Float) valueAnimator.getAnimatedValue()).floatValue();
        this.actionBarColor = ColorUtils.blendARGB(i, i2, floatValue);
        checkNavBarColor();
        this.windowView.invalidate();
        this.actionBar.setBackgroundColor(this.actionBarColor);
        botWebViewMenuContainer$ActionBarColorsAnimating.updateActionBar(this.actionBar, floatValue);
        this.lineColor = botWebViewMenuContainer$ActionBarColorsAnimating.getColor(Theme.key_sheet_scrollUp);
        this.windowView.invalidate();
    }

    public void lambda$setBackgroundColor$48(int i, int i2, ValueAnimator valueAnimator) {
        this.backgroundPaint.setColor(ColorUtils.blendARGB(i, i2, ((Float) valueAnimator.getAnimatedValue()).floatValue()));
        updateActionBarColors();
        this.windowView.invalidate();
        ArticleViewer.ErrorContainer errorContainer = this.errorContainer;
        if (errorContainer != null) {
            errorContainer.setDark(AndroidUtilities.computePerceivedBrightness(this.backgroundPaint.getColor()) <= 0.721f, false);
            this.errorContainer.setBackgroundColor(this.backgroundPaint.getColor());
        }
    }

    public void lambda$setNavigationBarColor$49(int i, int i2, ValueAnimator valueAnimator) {
        this.navBarColor = ColorUtils.blendARGB(i, i2, ((Float) valueAnimator.getAnimatedValue()).floatValue());
        checkNavBarColor();
    }

    public void lambda$setOpen$47(ValueAnimator valueAnimator) {
        this.openedProgress = ((Float) valueAnimator.getAnimatedValue()).floatValue();
        checkNavBarColor();
    }

    public static Bulletin lambda$showJustAddedBulletin$0(String str, BulletinFactory bulletinFactory) {
        return bulletinFactory.createSimpleBulletin(R.raw.contact_check, AndroidUtilities.replaceTags(str)).setDuration(5000);
    }

    public void lambda$showJustAddedBulletin$1(final String str) {
        showBulletin(new Utilities.CallbackReturn() {
            @Override
            public final Object run(Object obj) {
                Bulletin lambda$showJustAddedBulletin$0;
                lambda$showJustAddedBulletin$0 = BotWebViewSheet.lambda$showJustAddedBulletin$0(str, (BulletinFactory) obj);
                return lambda$showJustAddedBulletin$0;
            }
        });
    }

    public static void lambda$static$3(BotWebViewSheet botWebViewSheet, float f) {
        botWebViewSheet.actionBarTransitionProgress = f;
        botWebViewSheet.windowView.invalidate();
        botWebViewSheet.actionBar.setAlpha(f);
        botWebViewSheet.updateLightStatusBar();
        botWebViewSheet.updateDownloadBulletinArrow();
    }

    public void lambda$updateDownloadBulletin$41(BotDownloads.FileDownload fileDownload, View view) {
        if (fileDownload.isDownloading()) {
            fileDownload.cancel();
        } else {
            fileDownload.open();
        }
        ItemOptions itemOptions = this.options;
        if (itemOptions != null) {
            itemOptions.dismiss();
            this.options = null;
        }
    }

    private void loadFromResponse() {
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
            boolean z = this.fromTab;
            if (!z) {
                setFullscreen(tL_webViewResultUrl.fullscreen, !z);
            }
        } else if (tLObject instanceof TLRPC.TL_appWebViewResultUrl) {
            this.queryId = 0L;
            str = ((TLRPC.TL_appWebViewResultUrl) tLObject).url;
        } else if (tLObject instanceof TLRPC.TL_simpleWebViewResultUrl) {
            this.queryId = 0L;
            str = ((TLRPC.TL_simpleWebViewResultUrl) tLObject).url;
        }
        if (str != null && !this.fromTab) {
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

    public static int navigationBarColor(int i) {
        return Theme.adaptHSV(i, 0.35f, -0.1f);
    }

    public void openOptions() {
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
        ItemOptions itemOptions = this.options;
        if (itemOptions != null) {
            itemOptions.dismiss();
        }
        final ItemOptions makeOptions = ItemOptions.makeOptions(this.windowView, this.resourcesProvider, this.fullscreen ? this.fullscreenButtons : this.optionsItem, true);
        this.options = makeOptions;
        BotDownloads botDownloads = BotDownloads.get(getContext(), this.currentAccount, this.botId);
        this.fileItems.clear();
        if (botDownloads.hasFiles()) {
            final ItemOptions makeSwipeback = makeOptions.makeSwipeback();
            makeSwipeback.add(R.drawable.msg_arrow_back, LocaleController.getString(R.string.Back), new ChatActivity$$ExternalSyntheticLambda230(makeOptions));
            makeSwipeback.addGap();
            Iterator it2 = botDownloads.getFiles().iterator();
            while (it2.hasNext()) {
                BotDownloads.FileDownload fileDownload = (BotDownloads.FileDownload) it2.next();
                this.fileItems.put(fileDownload, makeSwipeback.add(fileDownload.file_name, "", new Runnable() {
                    @Override
                    public final void run() {
                        BotWebViewSheet.lambda$openOptions$32();
                    }
                }).getLast());
            }
            updateDownloadBulletin();
            makeSwipeback.setMinWidth(AndroidUtilities.dp(180.0f));
            makeOptions.add(R.drawable.menu_download_round, LocaleController.getString(R.string.BotDownloads), new Runnable() {
                @Override
                public final void run() {
                    ItemOptions.this.openSwipeback(makeSwipeback);
                }
            });
            makeOptions.addGap();
        }
        makeOptions.add(R.drawable.msg_bot, LocaleController.getString(R.string.BotWebViewOpenBot), new Runnable() {
            @Override
            public final void run() {
                BotWebViewSheet.this.lambda$openOptions$34();
            }
        }).addIf(this.hasSettings, R.drawable.msg_settings, LocaleController.getString(R.string.BotWebViewSettings), new Runnable() {
            @Override
            public final void run() {
                BotWebViewSheet.this.lambda$openOptions$35();
            }
        }).add(R.drawable.msg_retry, LocaleController.getString(R.string.BotWebViewReloadPage), new Runnable() {
            @Override
            public final void run() {
                BotWebViewSheet.this.lambda$openOptions$36();
            }
        }).addIf(user != null && user.bot_has_main_app, R.drawable.msg_home, LocaleController.getString(R.string.AddShortcut), new Runnable() {
            @Override
            public final void run() {
                BotWebViewSheet.this.lambda$openOptions$37();
            }
        }).add(R.drawable.menu_intro, LocaleController.getString(R.string.BotWebViewToS), new Runnable() {
            @Override
            public final void run() {
                BotWebViewSheet.this.lambda$openOptions$38();
            }
        }).addIf(tL_attachMenuBot != null && (tL_attachMenuBot.show_in_side_menu || tL_attachMenuBot.show_in_attach_menu), R.drawable.msg_delete, LocaleController.getString(R.string.BotWebViewDeleteBot), new Runnable() {
            @Override
            public final void run() {
                BotWebViewSheet.this.lambda$openOptions$40();
            }
        }).setGravity(5).translate(-this.insets.right, 0.0f).forceTop(true).setDrawScrim(false).show();
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

    public void relayout() {
        updateFullscreenLayout();
    }

    public void showBulletin(Utilities.CallbackReturn callbackReturn) {
        ((Bulletin) callbackReturn.run(BulletinFactory.of(this.bulletinContainer, this.resourcesProvider))).show(true);
    }

    public void updateActionBarColors() {
        if (!this.overrideActionBarColor) {
            ActionBar actionBar = this.actionBar;
            int i = Theme.key_windowBackgroundWhiteBlackText;
            actionBar.setTitleColor(getColor(i));
            this.actionBar.setItemsColor(getColor(i), false);
            this.actionBar.setItemsBackgroundColor(getColor(Theme.key_actionBarWhiteSelector), false);
            this.actionBar.setPopupBackgroundColor(getColor(Theme.key_actionBarDefaultSubmenuBackground), false);
            this.actionBar.setPopupItemsColor(getColor(Theme.key_actionBarDefaultSubmenuItem), false, false);
            this.actionBar.setPopupItemsColor(getColor(Theme.key_actionBarDefaultSubmenuItemIcon), true, false);
            this.actionBar.setPopupItemsSelectorColor(getColor(Theme.key_dialogButtonSelector), false);
        }
        this.webViewContainer.setFlickerViewColor(this.backgroundPaint.getColor());
    }

    private void updateDownloadBulletin() {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.bots.BotWebViewSheet.updateDownloadBulletin():void");
    }

    public void updateDownloadBulletinArrow() {
        BotDownloads.DownloadBulletin downloadBulletin = this.downloadBulletinLayout;
        if (downloadBulletin == null) {
            return;
        }
        downloadBulletin.setArrow(this.fullscreen ? AndroidUtilities.lerp(AndroidUtilities.dp(24.0f), AndroidUtilities.dp(26.0f), this.fullscreenProgress) : this.actionBarTransitionProgress > 0.5f ? AndroidUtilities.dp(24.0f) : -1);
    }

    private void updateLightStatusBar() {
        boolean z = true;
        if (this.overrideActionBarColor) {
            z = true ^ this.actionBarIsLight;
        } else {
            int color = Theme.getColor(Theme.key_windowBackgroundWhite, null, true);
            if (AndroidUtilities.isTablet() || ColorUtils.calculateLuminance(color) < 0.7210000157356262d || this.actionBarTransitionProgress < 0.85f) {
                z = false;
            }
        }
        Boolean bool = this.wasLightStatusBar;
        if (bool == null || bool.booleanValue() != z) {
            this.wasLightStatusBar = Boolean.valueOf(z);
            if (Build.VERSION.SDK_INT >= 23) {
                int systemUiVisibility = this.windowView.getSystemUiVisibility();
                this.windowView.setSystemUiVisibility(z ? systemUiVisibility | 8192 : systemUiVisibility & (-8193));
            }
        }
    }

    public void checkNavBarColor() {
        LaunchActivity launchActivity;
        if (!this.superDismissed && (launchActivity = LaunchActivity.instance) != null) {
            launchActivity.checkSystemBarColors(true, true, true, false);
        }
        WindowView windowView = this.windowView;
        if (windowView != null) {
            windowView.invalidate();
        }
    }

    public ArticleViewer.ErrorContainer createErrorContainer() {
        if (this.errorContainer == null) {
            ChatAttachAlertBotWebViewLayout.WebViewSwipeContainer webViewSwipeContainer = this.swipeContainer;
            ArticleViewer.ErrorContainer errorContainer = new ArticleViewer.ErrorContainer(getContext());
            this.errorContainer = errorContainer;
            webViewSwipeContainer.addView(errorContainer, LayoutHelper.createFrame(-1, -1.0f));
            this.errorContainer.setTranslationY(-1.0f);
            this.errorContainer.buttonView.setOnClickListener(new View.OnClickListener() {
                @Override
                public final void onClick(View view) {
                    BotWebViewSheet.this.lambda$createErrorContainer$51(view);
                }
            });
            this.errorContainer.setBackgroundColor(this.backgroundPaint.getColor());
            AndroidUtilities.updateViewVisibilityAnimated(this.errorContainer, this.errorShown, 1.0f, false);
        }
        return this.errorContainer;
    }

    @Override
    public void didReceivedNotification(int i, int i2, Object... objArr) {
        if (i == NotificationCenter.webViewResultSent) {
            if (this.queryId == ((Long) objArr[0]).longValue()) {
                lambda$openOptions$39();
                return;
            }
            return;
        }
        if (i != NotificationCenter.didSetNewTheme) {
            if (i == NotificationCenter.botDownloadsUpdate) {
                updateDownloadBulletin();
            }
        } else {
            this.windowView.invalidate();
            this.webViewContainer.updateFlickerBackgroundColor(getColor(Theme.key_windowBackgroundWhite));
            updateActionBarColors();
            updateLightStatusBar();
        }
    }

    @Override
    public void lambda$openOptions$39() {
        dismiss((Runnable) null);
    }

    public void dismiss(Runnable runnable) {
        dismiss(false, runnable);
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
        setOpen(false);
        AndroidUtilities.cancelRunOnUIThread(this.pollRunnable);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.webViewResultSent);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.botDownloadsUpdate);
        NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.didSetNewTheme);
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
            BotButtons botButtons = this.botButtons;
            if (botButtons != null) {
                botButtons.animate().translationY(this.botButtons.getTotalHeight()).alpha(0.0f).setDuration(160L).setInterpolator(CubicBezierInterpolator.EASE_OUT_QUINT).start();
            }
            this.webViewContainer.destroyWebView();
            ChatAttachAlertBotWebViewLayout.WebViewSwipeContainer webViewSwipeContainer = this.swipeContainer;
            int height = webViewSwipeContainer.getHeight();
            BotButtons botButtons2 = this.botButtons;
            int totalHeight = height + (botButtons2 != null ? botButtons2.getTotalHeight() : 0);
            Rect rect = this.insets;
            webViewSwipeContainer.stickTo(totalHeight + rect.top + rect.bottom + this.windowView.measureKeyboardHeight() + (isFullSize() ? AndroidUtilities.dp(200.0f) : 0), true, new Runnable() {
                @Override
                public final void run() {
                    BotWebViewSheet.this.lambda$dismiss$46(runnable);
                }
            });
        }
        activeSheets.remove(this);
    }

    public Activity getActivity() {
        Activity ownerActivity = getOwnerActivity();
        if (ownerActivity == null) {
            ownerActivity = LaunchActivity.instance;
        }
        return ownerActivity == null ? AndroidUtilities.findActivity(getContext()) : ownerActivity;
    }

    public long getBotId() {
        return this.botId;
    }

    @Override
    public int getNavigationBarColor(int i) {
        return ColorUtils.blendARGB(i, this.navBarColor, this.openedProgress);
    }

    @Override
    public WindowView mo1000getWindowView() {
        return this.windowView;
    }

    @Override
    public boolean hadDialog() {
        return false;
    }

    public boolean isFullSize() {
        Boolean bool;
        return this.fullscreen || ((bool = this.fullsize) != null ? bool.booleanValue() : this.defaultFullsize);
    }

    public void lockOrientation(boolean z) {
        if (this.orientationLocked == z) {
            return;
        }
        this.orientationLocked = z;
        if (this.attached) {
            shownLockedBots = z ? shownLockedBots + 1 : shownLockedBots - 1;
        }
        if (shownLockedBots > 0) {
            AndroidUtilities.lockOrientation(getActivity());
        } else {
            AndroidUtilities.unlockOrientation(getActivity());
        }
    }

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        setAttached(true);
        if (this.springAnimation == null) {
            this.springAnimation = new SpringAnimation(this, ACTION_BAR_TRANSITION_PROGRESS_VALUE).setSpring(new SpringForce().setStiffness(1200.0f).setDampingRatio(1.0f));
        }
    }

    @Override
    public void onBackPressed() {
        if (this.passcodeView.getVisibility() == 0) {
            if (getOwnerActivity() != null) {
                getOwnerActivity().finish();
            }
        } else {
            if (this.webViewContainer.onBackPressed()) {
                return;
            }
            dismiss(true, null);
        }
    }

    public boolean onCheckDismissByUser() {
        if (!this.needCloseConfirmation) {
            lambda$openOptions$39();
            return true;
        }
        TLRPC.User user = MessagesController.getInstance(this.currentAccount).getUser(Long.valueOf(this.botId));
        AlertDialog create = new AlertDialog.Builder(getContext()).setTitle(user != null ? ContactsController.formatName(user.first_name, user.last_name) : null).setMessage(LocaleController.getString(R.string.BotWebViewChangesMayNotBeSaved)).setPositiveButton(LocaleController.getString(R.string.BotWebViewCloseAnyway), new DialogInterface.OnClickListener() {
            @Override
            public final void onClick(DialogInterface dialogInterface, int i) {
                BotWebViewSheet.this.lambda$onCheckDismissByUser$45(dialogInterface, i);
            }
        }).setNegativeButton(LocaleController.getString(R.string.Cancel), null).create();
        create.show();
        ((TextView) create.getButton(-1)).setTextColor(getColor(Theme.key_text_RedBold));
        return false;
    }

    @Override
    protected void onCreate(android.os.Bundle r8) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.bots.BotWebViewSheet.onCreate(android.os.Bundle):void");
    }

    @Override
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        setAttached(false);
        SpringAnimation springAnimation = this.springAnimation;
        if (springAnimation != null) {
            springAnimation.cancel();
            this.springAnimation = null;
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        Context context = getContext();
        if ((context instanceof ContextWrapper) && !(context instanceof LaunchActivity)) {
            context = ((ContextWrapper) context).getBaseContext();
        }
        if (context instanceof LaunchActivity) {
            ((LaunchActivity) context).addOverlayPasscodeView(this.passcodeView);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        Context context = getContext();
        if ((context instanceof ContextWrapper) && !(context instanceof LaunchActivity)) {
            context = ((ContextWrapper) context).getBaseContext();
        }
        if (context instanceof LaunchActivity) {
            ((LaunchActivity) context).removeOverlayPasscodeView(this.passcodeView);
        }
    }

    @Override
    public void release() {
        if (this.superDismissed) {
            return;
        }
        try {
            super.dismiss();
        } catch (Exception e) {
            FileLog.e(e);
        }
        setOpen(false);
    }

    public void requestWebView(org.telegram.ui.ActionBar.BaseFragment r14, org.telegram.ui.bots.WebViewRequestProps r15) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.bots.BotWebViewSheet.requestWebView(org.telegram.ui.ActionBar.BaseFragment, org.telegram.ui.bots.WebViewRequestProps):void");
    }

    public boolean restoreState(org.telegram.ui.ActionBar.BaseFragment r11, org.telegram.ui.ActionBar.BottomSheetTabs.WebTabData r12) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.bots.BotWebViewSheet.restoreState(org.telegram.ui.ActionBar.BaseFragment, org.telegram.ui.ActionBar.BottomSheetTabs$WebTabData):boolean");
    }

    @Override
    public BottomSheetTabs.WebTabData saveState() {
        BottomSheetTabs.WebTabData webTabData = new BottomSheetTabs.WebTabData();
        webTabData.actionBarColor = this.actionBarColor;
        webTabData.actionBarColorKey = this.actionBarColorKey;
        webTabData.overrideActionBarColor = this.overrideActionBarColor;
        webTabData.overrideBackgroundColor = this.overrideBackgroundColor;
        webTabData.backgroundColor = this.backgroundPaint.getColor();
        webTabData.props = this.requestProps;
        BotWebViewContainer botWebViewContainer = this.webViewContainer;
        webTabData.ready = botWebViewContainer != null && botWebViewContainer.isPageLoaded();
        webTabData.themeIsDark = Theme.isCurrentThemeDark();
        BotWebViewContainer botWebViewContainer2 = this.webViewContainer;
        webTabData.lastUrl = botWebViewContainer2 != null ? botWebViewContainer2.getUrlLoaded() : null;
        ChatAttachAlertBotWebViewLayout.WebViewSwipeContainer webViewSwipeContainer = this.swipeContainer;
        webTabData.expanded = (webViewSwipeContainer != null && webViewSwipeContainer.getSwipeOffsetY() < 0.0f) || this.forceExpnaded || isFullSize() || this.fullscreen;
        webTabData.fullscreen = this.fullscreen;
        Boolean bool = this.fullsize;
        webTabData.fullsize = bool == null ? this.defaultFullsize : bool.booleanValue();
        ChatAttachAlertBotWebViewLayout.WebViewSwipeContainer webViewSwipeContainer2 = this.swipeContainer;
        webTabData.expandedOffset = webViewSwipeContainer2 != null ? webViewSwipeContainer2.getOffsetY() : Float.MAX_VALUE;
        webTabData.needsContext = this.needsContext;
        webTabData.backButton = this.backButtonShown;
        webTabData.confirmDismiss = this.needCloseConfirmation;
        webTabData.settings = this.hasSettings;
        ChatAttachAlertBotWebViewLayout.WebViewSwipeContainer webViewSwipeContainer3 = this.swipeContainer;
        webTabData.allowSwipes = webViewSwipeContainer3 == null || webViewSwipeContainer3.isAllowedSwipes();
        webTabData.buttons = this.botButtons.state;
        webTabData.navigationBarColor = this.navBarColor;
        BotSensors botSensors = this.sensors;
        if (botSensors != null) {
            botSensors.pause();
        }
        webTabData.sensors = this.sensors;
        BotWebViewContainer botWebViewContainer3 = this.webViewContainer;
        BotWebViewContainer.MyWebView webView = botWebViewContainer3 == null ? null : botWebViewContainer3.getWebView();
        if (webView != null) {
            this.webViewContainer.preserveWebView();
            webTabData.webView = webView;
            BotWebViewContainer botWebViewContainer4 = this.webViewContainer;
            webTabData.proxy = botWebViewContainer4 != null ? botWebViewContainer4.getBotProxy() : null;
            webTabData.viewWidth = webView.getWidth();
            webTabData.viewHeight = webView.getHeight();
            webView.onPause();
        }
        boolean z = this.errorShown;
        webTabData.error = z;
        if (z) {
            webTabData.errorDescription = this.errorCode;
        }
        webTabData.orientationLocked = this.orientationLocked;
        this.lastTab = webTabData;
        return webTabData;
    }

    public void setActionBarColor(final int i, boolean z, boolean z2) {
        final int i2 = this.actionBarColor;
        navigationBarColor(i);
        final BotWebViewMenuContainer$ActionBarColorsAnimating botWebViewMenuContainer$ActionBarColorsAnimating = new BotWebViewMenuContainer$ActionBarColorsAnimating();
        botWebViewMenuContainer$ActionBarColorsAnimating.setFrom(this.overrideActionBarColor ? this.actionBarColor : 0, this.resourcesProvider);
        this.overrideActionBarColor = z;
        this.actionBarIsLight = ColorUtils.calculateLuminance(i) < 0.7210000157356262d;
        botWebViewMenuContainer$ActionBarColorsAnimating.setTo(this.overrideActionBarColor ? i : 0, this.resourcesProvider);
        if (z2) {
            ValueAnimator duration = ValueAnimator.ofFloat(0.0f, 1.0f).setDuration(200L);
            duration.setInterpolator(CubicBezierInterpolator.DEFAULT);
            duration.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                    BotWebViewSheet.this.lambda$setActionBarColor$50(i2, i, botWebViewMenuContainer$ActionBarColorsAnimating, valueAnimator);
                }
            });
            duration.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animator) {
                    BotWebViewSheet.this.actionBarColor = ColorUtils.blendARGB(i2, i, 1.0f);
                    BotWebViewSheet.this.checkNavBarColor();
                    BotWebViewSheet.this.windowView.invalidate();
                    BotWebViewSheet.this.actionBar.setBackgroundColor(BotWebViewSheet.this.actionBarColor);
                    botWebViewMenuContainer$ActionBarColorsAnimating.updateActionBar(BotWebViewSheet.this.actionBar, 1.0f);
                    BotWebViewSheet.this.lineColor = botWebViewMenuContainer$ActionBarColorsAnimating.getColor(Theme.key_sheet_scrollUp);
                    BotWebViewSheet.this.windowView.invalidate();
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
    }

    public void setAttached(boolean r2) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.bots.BotWebViewSheet.setAttached(boolean):void");
    }

    public void setBackgroundColor(final int i, boolean z, boolean z2) {
        final int color = this.backgroundPaint.getColor();
        this.overrideBackgroundColor = z;
        ValueAnimator valueAnimator = this.backgroundColorAnimator;
        if (valueAnimator != null) {
            valueAnimator.cancel();
        }
        if (z2) {
            ValueAnimator duration = ValueAnimator.ofFloat(0.0f, 1.0f).setDuration(200L);
            this.backgroundColorAnimator = duration;
            duration.setInterpolator(CubicBezierInterpolator.DEFAULT);
            this.backgroundColorAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public final void onAnimationUpdate(ValueAnimator valueAnimator2) {
                    BotWebViewSheet.this.lambda$setBackgroundColor$48(color, i, valueAnimator2);
                }
            });
            this.backgroundColorAnimator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animator) {
                    BotWebViewSheet.this.backgroundPaint.setColor(i);
                    BotWebViewSheet.this.updateActionBarColors();
                    BotWebViewSheet.this.windowView.invalidate();
                    if (BotWebViewSheet.this.errorContainer != null) {
                        BotWebViewSheet.this.errorContainer.setDark(AndroidUtilities.computePerceivedBrightness(BotWebViewSheet.this.backgroundPaint.getColor()) <= 0.721f, false);
                        BotWebViewSheet.this.errorContainer.setBackgroundColor(BotWebViewSheet.this.backgroundPaint.getColor());
                    }
                }
            });
            this.backgroundColorAnimator.start();
            return;
        }
        this.backgroundPaint.setColor(i);
        updateActionBarColors();
        this.windowView.invalidate();
        ArticleViewer.ErrorContainer errorContainer = this.errorContainer;
        if (errorContainer != null) {
            errorContainer.setDark(AndroidUtilities.computePerceivedBrightness(this.backgroundPaint.getColor()) <= 0.721f, false);
            this.errorContainer.setBackgroundColor(this.backgroundPaint.getColor());
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
        return false;
    }

    public void setFullscreen(final boolean z, boolean z2) {
        float f;
        if (this.fullscreen == z) {
            return;
        }
        this.fullscreen = z;
        ValueAnimator valueAnimator = this.fullscreenAnimator;
        if (valueAnimator != null) {
            valueAnimator.cancel();
        }
        BotFullscreenButtons botFullscreenButtons = this.fullscreenButtons;
        if (botFullscreenButtons != null) {
            botFullscreenButtons.setPreview(z, z2);
        }
        this.swipeContainerFromWidth = this.swipeContainer.getWidth();
        this.swipeContainerFromHeight = this.swipeContainer.getHeight();
        this.resetOffsetY = false;
        if (!z2) {
            this.fullscreenInProgress = false;
            this.fullscreenProgress = z ? 1.0f : 0.0f;
            this.fullscreenTransitionProgress = 0.0f;
            updateFullscreenLayout();
            updateWindowFlags();
            this.actionBar.setVisibility(z ? 8 : 0);
            this.actionBar.setAlpha(1.0f - this.fullscreenProgress);
            this.actionBar.setTranslationY((-ActionBar.getCurrentActionBarHeight()) * this.fullscreenProgress);
            this.botButtons.setTranslationX(0.0f);
            this.fullscreenButtons.setAlpha(this.fullscreenProgress);
            this.webViewContainer.setViewPortHeightOffset(0.0f);
            this.webViewContainer.invalidateViewPortHeight(true, true);
            updateDownloadBulletinArrow();
            return;
        }
        updateFullscreenLayout();
        updateWindowFlags();
        updateDownloadBulletinArrow();
        if (!AndroidUtilities.isTablet() || AndroidUtilities.isInMultiwindow || AndroidUtilities.isSmallTablet()) {
            f = 0.0f;
        } else {
            int i = AndroidUtilities.displaySize.x;
            f = (i - ((int) (Math.min(i, r15.y) * 0.8f))) / 2.0f;
        }
        final float f2 = z ? this.insets.left + f : (-r4) - f;
        if (!z) {
            f = -f;
        }
        final float f3 = f;
        final float translationY = z ? this.swipeContainer.getTranslationY() : -AndroidUtilities.dp(24.0f);
        final float currentActionBarHeight = z ? -AndroidUtilities.dp(24.0f) : (ActionBar.getCurrentActionBarHeight() + AndroidUtilities.statusBarHeight) - AndroidUtilities.dp(24.0f);
        final float currentActionBarHeight2 = ActionBar.getCurrentActionBarHeight() + AndroidUtilities.statusBarHeight;
        this.swipeContainer.cancelStickTo();
        this.swipeContainer.setSwipeOffsetAnimationDisallowed(true);
        this.actionBar.setVisibility(0);
        this.swipeContainer.setTopActionBarOffsetY(z ? -r15 : currentActionBarHeight2 - AndroidUtilities.dp(24.0f));
        this.swipeContainer.invalidateTranslation();
        this.swipeContainer.invalidate();
        this.fullscreenTransitionProgress = 0.0f;
        float f4 = z ? 0.0f : 1.0f;
        this.fullscreenProgress = f4;
        this.actionBar.setAlpha(1.0f - f4);
        this.actionBar.setTranslationY((-ActionBar.getCurrentActionBarHeight()) * this.fullscreenProgress);
        this.swipeContainer.setTranslationY(AndroidUtilities.lerp(translationY, currentActionBarHeight, this.fullscreenTransitionProgress));
        this.swipeContainer.setTranslationX(AndroidUtilities.lerp(f2, 0.0f, this.fullscreenTransitionProgress));
        this.botButtons.setTranslationX(AndroidUtilities.lerp(f3, 0.0f, this.fullscreenTransitionProgress));
        this.fullscreenButtons.setAlpha(this.fullscreenProgress);
        this.windowView.invalidate();
        this.webViewContainer.setViewPortHeightOffset(this.swipeContainer.getTranslationY() - currentActionBarHeight);
        this.webViewContainer.invalidateViewPortHeight(false, false);
        this.fullscreenInProgress = true;
        ValueAnimator ofFloat = ValueAnimator.ofFloat(0.0f, 1.0f);
        this.fullscreenAnimator = ofFloat;
        final float f5 = f2;
        ofFloat.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator2) {
                BotWebViewSheet.this.fullscreenTransitionProgress = ((Float) valueAnimator2.getAnimatedValue()).floatValue();
                BotWebViewSheet botWebViewSheet = BotWebViewSheet.this;
                botWebViewSheet.fullscreenProgress = z ? botWebViewSheet.fullscreenTransitionProgress : 1.0f - botWebViewSheet.fullscreenTransitionProgress;
                BotWebViewSheet.this.actionBar.setAlpha(1.0f - BotWebViewSheet.this.fullscreenProgress);
                BotWebViewSheet.this.actionBar.setTranslationY((-ActionBar.getCurrentActionBarHeight()) * BotWebViewSheet.this.fullscreenProgress);
                BotWebViewSheet.this.swipeContainer.setTranslationY(AndroidUtilities.lerp(translationY, currentActionBarHeight, BotWebViewSheet.this.fullscreenTransitionProgress));
                BotWebViewSheet.this.swipeContainer.setTranslationX(AndroidUtilities.lerp(f5, 0.0f, BotWebViewSheet.this.fullscreenTransitionProgress));
                BotWebViewSheet.this.botButtons.setTranslationX(AndroidUtilities.lerp(f3, 0.0f, BotWebViewSheet.this.fullscreenTransitionProgress));
                BotWebViewSheet.this.fullscreenButtons.setAlpha(BotWebViewSheet.this.fullscreenProgress);
                BotWebViewSheet.this.windowView.invalidate();
                BotWebViewSheet.this.webViewContainer.setViewPortHeightOffset(BotWebViewSheet.this.swipeContainer.getTranslationY() - currentActionBarHeight);
                BotWebViewSheet.this.webViewContainer.invalidateViewPortHeight(false, false);
                BotWebViewSheet.this.updateDownloadBulletinArrow();
            }
        });
        this.fullscreenAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animator) {
                BotWebViewSheet.this.fullscreenInProgress = false;
                if (z) {
                    BotWebViewSheet.this.swipeContainer.setForceOffsetY(-AndroidUtilities.dp(24.0f));
                    BotWebViewSheet.this.swipeContainer.setTopActionBarOffsetY(-AndroidUtilities.dp(24.0f));
                } else {
                    BotWebViewSheet.this.updateFullscreenLayout();
                    BotWebViewSheet.this.updateWindowFlags();
                    BotWebViewSheet.this.swipeContainer.setForceOffsetY(currentActionBarHeight2 - AndroidUtilities.dp(24.0f));
                    BotWebViewSheet.this.swipeContainer.setTopActionBarOffsetY(currentActionBarHeight2 - AndroidUtilities.dp(24.0f));
                }
                BotWebViewSheet.this.swipeContainer.setSwipeOffsetY(0.0f);
                BotWebViewSheet botWebViewSheet = BotWebViewSheet.this;
                botWebViewSheet.fullscreenProgress = z ? botWebViewSheet.fullscreenTransitionProgress : 1.0f - botWebViewSheet.fullscreenTransitionProgress;
                BotWebViewSheet.this.actionBar.setAlpha(1.0f - BotWebViewSheet.this.fullscreenProgress);
                BotWebViewSheet.this.actionBar.setTranslationY((-ActionBar.getCurrentActionBarHeight()) * BotWebViewSheet.this.fullscreenProgress);
                BotWebViewSheet.this.fullscreenButtons.setAlpha(BotWebViewSheet.this.fullscreenProgress);
                if (z) {
                    BotWebViewSheet.this.actionBar.setVisibility(8);
                }
                BotWebViewSheet.this.swipeContainer.setSwipeOffsetAnimationDisallowed(false);
                BotWebViewSheet.this.swipeContainer.setTranslationX(AndroidUtilities.lerp(f2, 0.0f, BotWebViewSheet.this.fullscreenTransitionProgress));
                BotWebViewSheet.this.botButtons.setTranslationX(0.0f);
                BotWebViewSheet.this.windowView.invalidate();
                BotWebViewSheet.this.webViewContainer.setViewPortHeightOffset(0.0f);
                BotWebViewSheet.this.webViewContainer.invalidateViewPortHeight(true, true);
                BotWebViewSheet.this.updateDownloadBulletinArrow();
            }
        });
        this.fullscreenAnimator.setDuration(280L);
        this.fullscreenAnimator.setInterpolator(CubicBezierInterpolator.EASE_OUT_QUINT);
        this.fullscreenAnimator.start();
    }

    @Override
    public void setLastVisible(boolean z) {
        BottomSheetTabsOverlay.Sheet.CC.$default$setLastVisible(this, z);
    }

    public void setNavigationBarColor(final int i, boolean z) {
        final int i2 = this.navBarColor;
        this.botButtons.setBackgroundColor(i, z);
        if (z) {
            ValueAnimator duration = ValueAnimator.ofFloat(0.0f, 1.0f).setDuration(200L);
            duration.setInterpolator(CubicBezierInterpolator.DEFAULT);
            duration.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                    BotWebViewSheet.this.lambda$setNavigationBarColor$49(i2, i, valueAnimator);
                }
            });
            duration.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animator) {
                    BotWebViewSheet.this.navBarColor = ColorUtils.blendARGB(i2, i, 1.0f);
                    BotWebViewSheet.this.checkNavBarColor();
                }
            });
            duration.start();
        } else {
            this.navBarColor = i;
            checkNavBarColor();
        }
        AndroidUtilities.setNavigationBarColor(getWindow(), this.navBarColor, false);
    }

    public void setNeedsContext(boolean z) {
        this.needsContext = z;
    }

    public void setOpen(final boolean z) {
        ValueAnimator valueAnimator = this.openAnimator;
        if (valueAnimator != null) {
            valueAnimator.cancel();
        }
        if (Math.abs(this.openedProgress - (z ? 1.0f : 0.0f)) < 0.01f) {
            return;
        }
        ValueAnimator ofFloat = ValueAnimator.ofFloat(this.openedProgress, z ? 1.0f : 0.0f);
        this.openAnimator = ofFloat;
        ofFloat.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animator) {
                BotWebViewSheet.this.openedProgress = z ? 1.0f : 0.0f;
                BotWebViewSheet.this.checkNavBarColor();
            }
        });
        this.openAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public final void onAnimationUpdate(ValueAnimator valueAnimator2) {
                BotWebViewSheet.this.lambda$setOpen$47(valueAnimator2);
            }
        });
        this.openAnimator.setInterpolator(CubicBezierInterpolator.EASE_OUT_QUINT);
        this.openAnimator.setDuration(220L);
        this.openAnimator.start();
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

    @Override
    public void show() {
        if (AndroidUtilities.isSafeToShow(getContext())) {
            setOpen(true);
            this.windowView.setAlpha(0.0f);
            this.windowView.addOnLayoutChangeListener(new AnonymousClass11());
            super.show();
            this.superDismissed = false;
            activeSheets.add(this);
        }
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
        final String formatString = (z && tL_attachMenuBot.show_in_attach_menu) ? LocaleController.formatString(R.string.BotAttachMenuShortcatAddedAttachAndSide, user.first_name) : z ? LocaleController.formatString(R.string.BotAttachMenuShortcatAddedSide, user.first_name) : LocaleController.formatString(R.string.BotAttachMenuShortcatAddedAttach, user.first_name);
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                BotWebViewSheet.this.lambda$showJustAddedBulletin$1(formatString);
            }
        }, 200L);
    }

    public void updateFullscreenLayout() {
        ChatAttachAlertBotWebViewLayout.WebViewSwipeContainer webViewSwipeContainer;
        float currentActionBarHeight;
        BotButtons botButtons;
        this.fullscreenButtons.setInsets(this.insets);
        if (this.fullscreen) {
            BotButtons botButtons2 = this.botButtons;
            int i = (botButtons2 == null || botButtons2.getTotalHeight() <= 0) ? 0 : this.insets.bottom;
            BotWebViewContainer botWebViewContainer = this.webViewContainer;
            Rect rect = this.insets;
            botWebViewContainer.reportSafeInsets(new Rect(rect.left, rect.top, rect.right, (this.keyboardInset <= i && ((botButtons = this.botButtons) == null || botButtons.getTotalHeight() <= 0)) ? this.insets.bottom : 0), AndroidUtilities.dp(46.0f));
            this.windowView.setPadding(0, 0, 0, Math.max(this.keyboardInset, i));
        } else {
            this.webViewContainer.reportSafeInsets(new Rect(0, 0, 0, 0), 0);
            WindowView windowView = this.windowView;
            Rect rect2 = this.insets;
            int i2 = rect2.left;
            int i3 = rect2.right;
            int i4 = this.keyboardInset;
            BottomSheetTabs bottomSheetTabs = this.bottomTabs;
            windowView.setPadding(i2, 0, i3, Math.max(i4, (bottomSheetTabs != null ? bottomSheetTabs.getHeight(false) : 0) + this.insets.bottom));
        }
        this.swipeContainerLayoutParams.topMargin = AndroidUtilities.dp(24.0f);
        FrameLayout.LayoutParams layoutParams = this.actionBarLayoutParams;
        boolean z = this.fullscreen;
        layoutParams.leftMargin = !z ? 0 : this.insets.left;
        layoutParams.rightMargin = 0;
        FrameLayout.LayoutParams layoutParams2 = this.bulletinContainerLayoutParams;
        layoutParams2.leftMargin = !z ? 0 : this.insets.left;
        layoutParams2.rightMargin = !z ? 0 : this.insets.right;
        if (!this.fullscreenInProgress) {
            this.swipeContainer.setSwipeOffsetAnimationDisallowed(true);
            if (this.fullscreen) {
                webViewSwipeContainer = this.swipeContainer;
                currentActionBarHeight = -AndroidUtilities.dp(24.0f);
            } else {
                webViewSwipeContainer = this.swipeContainer;
                currentActionBarHeight = (ActionBar.getCurrentActionBarHeight() + AndroidUtilities.statusBarHeight) - AndroidUtilities.dp(24.0f);
            }
            webViewSwipeContainer.setTopActionBarOffsetY(currentActionBarHeight);
            this.swipeContainer.setSwipeOffsetAnimationDisallowed(false);
            this.swipeContainer.invalidateTranslation();
            this.swipeContainer.invalidate();
            this.swipeContainer.requestLayout();
        }
        ChatAttachAlertBotWebViewLayout.WebViewSwipeContainer webViewSwipeContainer2 = this.swipeContainer;
        if (webViewSwipeContainer2 != null) {
            webViewSwipeContainer2.setFullSize(isFullSize());
        }
        this.botButtons.requestLayout();
        this.windowView.requestLayout();
        this.fullscreenButtons.setVisibility(this.fullscreen ? 0 : 8);
    }

    public void updateWindowFlags() {
        WindowView windowView;
        int systemUiVisibility;
        BotButtons botButtons;
        try {
            Window window = getWindow();
            if (window == null) {
                return;
            }
            WindowManager.LayoutParams attributes = window.getAttributes();
            int i = Build.VERSION.SDK_INT <= 28 ? 1024 : 512;
            boolean z = this.fullscreen;
            attributes.flags = z ? i | attributes.flags : (i ^ (-1)) & attributes.flags;
            if (!z || (((botButtons = this.botButtons) != null && botButtons.getTotalHeight() > 0) || this.windowView.drawingFromOverlay)) {
                windowView = this.windowView;
                systemUiVisibility = windowView.getSystemUiVisibility() & (-3);
            } else {
                windowView = this.windowView;
                systemUiVisibility = windowView.getSystemUiVisibility() | 2;
            }
            windowView.setSystemUiVisibility(systemUiVisibility);
            window.setAttributes(attributes);
        } catch (Exception e) {
            FileLog.e(e);
        }
    }
}
