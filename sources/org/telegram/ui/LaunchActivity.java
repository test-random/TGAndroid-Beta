package org.telegram.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Canvas;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.Rect;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.os.StatFs;
import android.os.SystemClock;
import android.provider.Settings;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.style.ClickableSpan;
import android.util.Base64;
import android.util.SparseArray;
import android.util.SparseIntArray;
import android.view.ActionMode;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowInsets;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;
import com.google.common.primitives.Longs;
import j$.util.function.Consumer;
import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.regex.Pattern;
import org.telegram.messenger.AccountInstance;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.AutoDeleteMediaTask;
import org.telegram.messenger.BuildVars;
import org.telegram.messenger.ChannelBoostsController;
import org.telegram.messenger.ChatObject;
import org.telegram.messenger.ContactsController;
import org.telegram.messenger.DialogObject;
import org.telegram.messenger.FileLoader;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.FlagSecureReason;
import org.telegram.messenger.GenericProvider;
import org.telegram.messenger.LiteMode;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.LocationController;
import org.telegram.messenger.MediaController;
import org.telegram.messenger.MediaDataController;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.MessagesStorage;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.NotificationsController;
import org.telegram.messenger.R;
import org.telegram.messenger.SendMessagesHelper;
import org.telegram.messenger.SharedConfig;
import org.telegram.messenger.SharedPrefsHelper;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.UserObject;
import org.telegram.messenger.Utilities;
import org.telegram.messenger.browser.Browser;
import org.telegram.messenger.voip.VideoCapturerDevice;
import org.telegram.messenger.voip.VoIPService;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.tgnet.Vector;
import org.telegram.tgnet.tl.TL_account;
import org.telegram.tgnet.tl.TL_chatlists;
import org.telegram.tgnet.tl.TL_stories;
import org.telegram.ui.ActionBar.ActionBarLayout;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.BottomSheetTabs;
import org.telegram.ui.ActionBar.BottomSheetTabsOverlay;
import org.telegram.ui.ActionBar.DrawerLayoutContainer;
import org.telegram.ui.ActionBar.INavigationLayout;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Adapters.DrawerLayoutAdapter;
import org.telegram.ui.Cells.ChatMessageCell;
import org.telegram.ui.Cells.DrawerActionCell;
import org.telegram.ui.Cells.DrawerAddCell;
import org.telegram.ui.Cells.DrawerProfileCell;
import org.telegram.ui.Cells.DrawerUserCell;
import org.telegram.ui.Cells.LanguageCell;
import org.telegram.ui.ChatRightsEditActivity;
import org.telegram.ui.Components.AlertsCreator;
import org.telegram.ui.Components.AnimatedEmojiDrawable;
import org.telegram.ui.Components.AttachBotIntroTopView;
import org.telegram.ui.Components.BatteryDrawable;
import org.telegram.ui.Components.BlockingUpdateView;
import org.telegram.ui.Components.Bulletin;
import org.telegram.ui.Components.BulletinFactory;
import org.telegram.ui.Components.CubicBezierInterpolator;
import org.telegram.ui.Components.EmbedBottomSheet;
import org.telegram.ui.Components.FireworksOverlay;
import org.telegram.ui.Components.FloatingDebug.FloatingDebugController;
import org.telegram.ui.Components.FolderBottomSheet;
import org.telegram.ui.Components.Forum.ForumUtilities;
import org.telegram.ui.Components.GroupCallPip;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.MediaActivity;
import org.telegram.ui.Components.PasscodeView;
import org.telegram.ui.Components.PasscodeViewDialog;
import org.telegram.ui.Components.PipRoundVideoView;
import org.telegram.ui.Components.Premium.LimitReachedBottomSheet;
import org.telegram.ui.Components.Premium.boosts.BoostPagerBottomSheet;
import org.telegram.ui.Components.RLottieDrawable;
import org.telegram.ui.Components.RLottieImageView;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.Components.SearchTagsList;
import org.telegram.ui.Components.SideMenultItemAnimator;
import org.telegram.ui.Components.SizeNotifierFrameLayout;
import org.telegram.ui.Components.StickersAlert;
import org.telegram.ui.Components.TermsOfServiceView;
import org.telegram.ui.Components.ThemeEditorView;
import org.telegram.ui.Components.UndoView;
import org.telegram.ui.Components.spoilers.SpoilerEffect2;
import org.telegram.ui.Components.voip.VoIPHelper;
import org.telegram.ui.DialogsActivity;
import org.telegram.ui.LaunchActivity;
import org.telegram.ui.LocationActivity;
import org.telegram.ui.PaymentFormActivity;
import org.telegram.ui.SelectAnimatedEmojiDialog;
import org.telegram.ui.Stars.ISuperRipple;
import org.telegram.ui.Stars.StarsController;
import org.telegram.ui.Stars.SuperRipple;
import org.telegram.ui.Stars.SuperRippleFallback;
import org.telegram.ui.Stories.StoriesController;
import org.telegram.ui.Stories.StoriesListPlaceProvider;
import org.telegram.ui.Stories.StoryViewer;
import org.telegram.ui.Stories.recorder.StoryRecorder;
import org.telegram.ui.WallpapersListActivity;
import org.telegram.ui.bots.BotWebViewSheet;
import org.telegram.ui.bots.WebViewRequestProps;
import org.webrtc.voiceengine.WebRtcAudioTrack;

public class LaunchActivity extends BasePermissionsActivity implements INavigationLayout.INavigationLayoutDelegate, NotificationCenter.NotificationCenterDelegate, DialogsActivity.DialogsActivityDelegate {
    public static LaunchActivity instance;
    public static boolean isActive;
    public static boolean isResumed;
    public static Runnable onResumeStaticCallback;
    private static LaunchActivity staticInstanceForAlerts;
    public static boolean systemBlurEnabled;
    public static Runnable whenResumed;
    private ActionBarLayout actionBarLayout;
    private long alreadyShownFreeDiscSpaceAlertForced;
    private SizeNotifierFrameLayout backgroundTablet;
    private BlockingUpdateView blockingUpdateView;
    private BottomSheetTabsOverlay bottomSheetTabsOverlay;
    private boolean checkFreeDiscSpaceShown;
    private ArrayList contactsToSend;
    private Uri contactsToSendUri;
    private int currentConnectionState;
    private ISuperRipple currentRipple;
    private View customNavigationBar;
    private String documentsMimeType;
    private ArrayList documentsOriginalPathsArray;
    private ArrayList documentsPathsArray;
    private ArrayList documentsUrisArray;
    private DrawerLayoutAdapter drawerLayoutAdapter;
    public DrawerLayoutContainer drawerLayoutContainer;
    private HashMap englishLocaleStrings;
    private Uri exportingChatUri;
    View feedbackView;
    private boolean finished;
    private FireworksOverlay fireworksOverlay;
    private FlagSecureReason flagSecureReason;
    public FrameLayout frameLayout;
    private ArrayList importingStickers;
    private ArrayList importingStickersEmoji;
    private String importingStickersSoftware;
    private SideMenultItemAnimator itemAnimator;
    private RelativeLayout launchLayout;
    private ActionBarLayout layersActionBarLayout;
    private boolean loadingLocaleDialog;
    private TLRPC.TL_theme loadingTheme;
    private boolean loadingThemeAccent;
    private String loadingThemeFileName;
    private Theme.ThemeInfo loadingThemeInfo;
    private AlertDialog loadingThemeProgressDialog;
    private TLRPC.TL_wallPaper loadingThemeWallpaper;
    private String loadingThemeWallpaperName;
    private Dialog localeDialog;
    private Runnable lockRunnable;
    private ValueAnimator navBarAnimator;
    private boolean navigateToPremiumBot;
    private Runnable navigateToPremiumGiftCallback;
    private ViewTreeObserver.OnGlobalLayoutListener onGlobalLayoutListener;
    private PasscodeViewDialog passcodeDialog;
    private Intent passcodeSaveIntent;
    private boolean passcodeSaveIntentIsNew;
    private boolean passcodeSaveIntentIsRestore;
    private ArrayList photoPathsArray;
    private Dialog proxyErrorDialog;
    private ActionBarLayout rightActionBarLayout;
    private View rippleAbove;
    private SelectAnimatedEmojiDialog.SelectAnimatedEmojiDialogWindow selectAnimatedEmojiDialog;
    private String sendingText;
    private FrameLayout shadowTablet;
    private FrameLayout shadowTabletSide;
    private RecyclerListView sideMenu;
    private FrameLayout sideMenuContainer;
    private boolean switchingAccount;
    private HashMap systemLocaleStrings;
    private boolean tabletFullSize;
    private int[] tempLocation;
    private TermsOfServiceView termsOfServiceView;
    private ImageView themeSwitchImageView;
    private RLottieDrawable themeSwitchSunDrawable;
    private View themeSwitchSunView;
    private String videoPath;
    private ActionMode visibleActionMode;
    private String voicePath;
    private boolean wasMutedByAdminRaisedHand;
    private Utilities.Callback webviewShareAPIDoneListener;
    public static final Pattern PREFIX_T_ME_PATTERN = Pattern.compile("^(?:http(?:s|)://|)([A-z0-9-]+?)\\.t\\.me");
    private static final ArrayList mainFragmentsStack = new ArrayList();
    private static final ArrayList layerFragmentsStack = new ArrayList();
    private static final ArrayList rightFragmentsStack = new ArrayList();
    public ArrayList sheetFragmentsStack = new ArrayList();
    private List overlayPasscodeViews = new ArrayList();
    public final ArrayList visibleDialogs = new ArrayList();
    private boolean isNavigationBarColorFrozen = false;
    private List onUserLeaveHintListeners = new ArrayList();
    private SparseIntArray requestedPermissions = new SparseIntArray();
    private int requsetPermissionsPointer = 5934;
    private Consumer blurListener = new Consumer() {
        AnonymousClass1() {
        }

        @Override
        public void r(Boolean bool) {
            LaunchActivity.systemBlurEnabled = bool.booleanValue();
        }

        @Override
        public Consumer andThen(Consumer consumer) {
            return Consumer.CC.$default$andThen(this, consumer);
        }
    };

    class AnonymousClass1 implements Consumer {
        AnonymousClass1() {
        }

        @Override
        public void r(Boolean bool) {
            LaunchActivity.systemBlurEnabled = bool.booleanValue();
        }

        @Override
        public Consumer andThen(Consumer consumer) {
            return Consumer.CC.$default$andThen(this, consumer);
        }
    }

    public class AnonymousClass10 extends RelativeLayout {
        private boolean inLayout;
        private Path path = new Path();

        AnonymousClass10(Context context) {
            super(context);
            this.path = new Path();
        }

        @Override
        protected void dispatchDraw(Canvas canvas) {
            if (LaunchActivity.this.layersActionBarLayout != null) {
                LaunchActivity.this.layersActionBarLayout.parentDraw(this, canvas);
            }
            super.dispatchDraw(canvas);
        }

        @Override
        protected void onLayout(boolean z, int i, int i2, int i3, int i4) {
            int i5 = i3 - i;
            int i6 = i4 - i2;
            if (AndroidUtilities.isInMultiwindow || (AndroidUtilities.isSmallTablet() && getResources().getConfiguration().orientation != 2)) {
                LaunchActivity.this.actionBarLayout.getView().layout(0, 0, LaunchActivity.this.actionBarLayout.getView().getMeasuredWidth(), LaunchActivity.this.actionBarLayout.getView().getMeasuredHeight());
            } else {
                int i7 = (i5 / 100) * 35;
                if (i7 < AndroidUtilities.dp(320.0f)) {
                    i7 = AndroidUtilities.dp(320.0f);
                }
                LaunchActivity.this.shadowTabletSide.layout(i7, 0, LaunchActivity.this.shadowTabletSide.getMeasuredWidth() + i7, LaunchActivity.this.shadowTabletSide.getMeasuredHeight());
                LaunchActivity.this.actionBarLayout.getView().layout(0, 0, LaunchActivity.this.actionBarLayout.getView().getMeasuredWidth(), LaunchActivity.this.actionBarLayout.getView().getMeasuredHeight());
                LaunchActivity.this.rightActionBarLayout.getView().layout(i7, 0, LaunchActivity.this.rightActionBarLayout.getView().getMeasuredWidth() + i7, LaunchActivity.this.rightActionBarLayout.getView().getMeasuredHeight());
            }
            int measuredWidth = (i5 - LaunchActivity.this.layersActionBarLayout.getView().getMeasuredWidth()) / 2;
            int measuredHeight = ((i6 - LaunchActivity.this.layersActionBarLayout.getView().getMeasuredHeight()) + AndroidUtilities.statusBarHeight) / 2;
            LaunchActivity.this.layersActionBarLayout.getView().layout(measuredWidth, measuredHeight, LaunchActivity.this.layersActionBarLayout.getView().getMeasuredWidth() + measuredWidth, LaunchActivity.this.layersActionBarLayout.getView().getMeasuredHeight() + measuredHeight);
            LaunchActivity.this.backgroundTablet.layout(0, 0, LaunchActivity.this.backgroundTablet.getMeasuredWidth(), LaunchActivity.this.backgroundTablet.getMeasuredHeight());
            LaunchActivity.this.shadowTablet.layout(0, 0, LaunchActivity.this.shadowTablet.getMeasuredWidth(), LaunchActivity.this.shadowTablet.getMeasuredHeight());
        }

        @Override
        protected void onMeasure(int i, int i2) {
            this.inLayout = true;
            int size = View.MeasureSpec.getSize(i);
            int size2 = View.MeasureSpec.getSize(i2);
            setMeasuredDimension(size, size2);
            if (AndroidUtilities.isInMultiwindow || (AndroidUtilities.isSmallTablet() && getResources().getConfiguration().orientation != 2)) {
                LaunchActivity.this.tabletFullSize = true;
                LaunchActivity.this.actionBarLayout.getView().measure(View.MeasureSpec.makeMeasureSpec(size, 1073741824), View.MeasureSpec.makeMeasureSpec(size2, 1073741824));
            } else {
                LaunchActivity.this.tabletFullSize = false;
                int i3 = (size / 100) * 35;
                if (i3 < AndroidUtilities.dp(320.0f)) {
                    i3 = AndroidUtilities.dp(320.0f);
                }
                LaunchActivity.this.actionBarLayout.getView().measure(View.MeasureSpec.makeMeasureSpec(i3, 1073741824), View.MeasureSpec.makeMeasureSpec(size2, 1073741824));
                LaunchActivity.this.shadowTabletSide.measure(View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(1.0f), 1073741824), View.MeasureSpec.makeMeasureSpec(size2, 1073741824));
                LaunchActivity.this.rightActionBarLayout.getView().measure(View.MeasureSpec.makeMeasureSpec(size - i3, 1073741824), View.MeasureSpec.makeMeasureSpec(size2, 1073741824));
            }
            LaunchActivity.this.backgroundTablet.measure(View.MeasureSpec.makeMeasureSpec(size, 1073741824), View.MeasureSpec.makeMeasureSpec(size2, 1073741824));
            LaunchActivity.this.shadowTablet.measure(View.MeasureSpec.makeMeasureSpec(size, 1073741824), View.MeasureSpec.makeMeasureSpec(size2, 1073741824));
            LaunchActivity.this.layersActionBarLayout.getView().measure(View.MeasureSpec.makeMeasureSpec(Math.min(AndroidUtilities.dp(530.0f), size - AndroidUtilities.dp(16.0f)), 1073741824), View.MeasureSpec.makeMeasureSpec((size2 - AndroidUtilities.statusBarHeight) - AndroidUtilities.dp(16.0f), 1073741824));
            this.inLayout = false;
        }

        @Override
        public void requestLayout() {
            if (this.inLayout) {
                return;
            }
            super.requestLayout();
        }
    }

    public class AnonymousClass11 extends SizeNotifierFrameLayout {
        AnonymousClass11(Context context) {
            super(context);
        }

        @Override
        protected boolean isActionBarVisible() {
            return false;
        }
    }

    public class AnonymousClass12 extends SelectAnimatedEmojiDialog {
        final SelectAnimatedEmojiDialog.SelectAnimatedEmojiDialogWindow[] val$popup;

        AnonymousClass12(BaseFragment baseFragment, Context context, boolean z, Integer num, int i, Theme.ResourcesProvider resourcesProvider, SelectAnimatedEmojiDialog.SelectAnimatedEmojiDialogWindow[] selectAnimatedEmojiDialogWindowArr) {
            super(baseFragment, context, z, num, i, resourcesProvider);
            r15 = selectAnimatedEmojiDialogWindowArr;
        }

        @Override
        protected void onEmojiSelected(View view, Long l, TLRPC.Document document, Integer num) {
            TLRPC.EmojiStatus emojiStatus;
            if (l == null) {
                emojiStatus = new TLRPC.TL_emojiStatusEmpty();
            } else if (num != null) {
                TLRPC.TL_emojiStatusUntil tL_emojiStatusUntil = new TLRPC.TL_emojiStatusUntil();
                tL_emojiStatusUntil.document_id = l.longValue();
                tL_emojiStatusUntil.until = num.intValue();
                emojiStatus = tL_emojiStatusUntil;
            } else {
                TLRPC.TL_emojiStatus tL_emojiStatus = new TLRPC.TL_emojiStatus();
                tL_emojiStatus.document_id = l.longValue();
                emojiStatus = tL_emojiStatus;
            }
            MessagesController.getInstance(LaunchActivity.this.currentAccount).updateEmojiStatus(emojiStatus);
            TLRPC.User currentUser = UserConfig.getInstance(LaunchActivity.this.currentAccount).getCurrentUser();
            if (currentUser != null) {
                for (int i = 0; i < LaunchActivity.this.sideMenu.getChildCount(); i++) {
                    View childAt = LaunchActivity.this.sideMenu.getChildAt(i);
                    if (childAt instanceof DrawerUserCell) {
                        DrawerUserCell drawerUserCell = (DrawerUserCell) childAt;
                        drawerUserCell.setAccount(drawerUserCell.getAccountNumber());
                    } else if (childAt instanceof DrawerProfileCell) {
                        if (l != null) {
                            ((DrawerProfileCell) childAt).animateStateChange(l.longValue());
                        }
                        ((DrawerProfileCell) childAt).setUser(currentUser, LaunchActivity.this.drawerLayoutAdapter.isAccountsShown());
                    } else if ((childAt instanceof DrawerActionCell) && LaunchActivity.this.drawerLayoutAdapter.getId(LaunchActivity.this.sideMenu.getChildAdapterPosition(childAt)) == 15) {
                        boolean z = DialogObject.getEmojiStatusDocumentId(currentUser.emoji_status) != 0;
                        ((DrawerActionCell) childAt).updateTextAndIcon(LaunchActivity.this.getString(z ? R.string.ChangeEmojiStatus : R.string.SetEmojiStatus), z ? R.drawable.msg_status_edit : R.drawable.msg_status_set);
                    }
                }
            }
            if (r15[0] != null) {
                LaunchActivity.this.selectAnimatedEmojiDialog = null;
                r15[0].dismiss();
            }
        }

        @Override
        public void onSettings() {
            DrawerLayoutContainer drawerLayoutContainer = LaunchActivity.this.drawerLayoutContainer;
            if (drawerLayoutContainer != null) {
                drawerLayoutContainer.closeDrawer();
            }
        }
    }

    public class AnonymousClass13 extends SelectAnimatedEmojiDialog.SelectAnimatedEmojiDialogWindow {
        AnonymousClass13(View view, int i, int i2) {
            super(view, i, i2);
        }

        @Override
        public void dismiss() {
            super.dismiss();
            LaunchActivity.this.selectAnimatedEmojiDialog = null;
        }
    }

    public class AnonymousClass14 extends BlockingUpdateView {
        AnonymousClass14(Context context) {
            super(context);
        }

        @Override
        public void setVisibility(int i) {
            super.setVisibility(i);
            if (i == 8) {
                LaunchActivity.this.drawerLayoutContainer.setAllowOpenDrawer(true, false);
            }
        }
    }

    public class AnonymousClass15 implements TermsOfServiceView.TermsOfServiceViewDelegate {
        AnonymousClass15() {
        }

        public void lambda$onAcceptTerms$0() {
            LaunchActivity.this.termsOfServiceView.setVisibility(8);
        }

        @Override
        public void onAcceptTerms(int i) {
            UserConfig.getInstance(i).unacceptedTermsOfService = null;
            UserConfig.getInstance(i).saveConfig(false);
            LaunchActivity.this.drawerLayoutContainer.setAllowOpenDrawer(true, false);
            if (LaunchActivity.mainFragmentsStack.size() > 0) {
                ((BaseFragment) LaunchActivity.mainFragmentsStack.get(LaunchActivity.mainFragmentsStack.size() - 1)).onResume();
            }
            LaunchActivity.this.termsOfServiceView.animate().alpha(0.0f).setDuration(150L).setInterpolator(AndroidUtilities.accelerateInterpolator).withEndAction(new Runnable() {
                @Override
                public final void run() {
                    LaunchActivity.AnonymousClass15.this.lambda$onAcceptTerms$0();
                }
            }).start();
        }
    }

    public class AnonymousClass16 extends DialogsActivity {
        AnonymousClass16(Bundle bundle) {
            super(bundle);
        }

        @Override
        public boolean shouldShowNextButton(DialogsActivity dialogsActivity, ArrayList arrayList, CharSequence charSequence, boolean z) {
            if (LaunchActivity.this.exportingChatUri != null) {
                return false;
            }
            if (LaunchActivity.this.contactsToSend != null && LaunchActivity.this.contactsToSend.size() == 1 && !LaunchActivity.mainFragmentsStack.isEmpty()) {
                return true;
            }
            if (arrayList.size() <= 1) {
                return LaunchActivity.this.videoPath != null || (LaunchActivity.this.photoPathsArray != null && LaunchActivity.this.photoPathsArray.size() > 0);
            }
            return false;
        }
    }

    public class AnonymousClass17 implements ChatRightsEditActivity.ChatRightsEditActivityDelegate {
        final DialogsActivity val$fragment;
        final int val$intentAccount;

        AnonymousClass17(DialogsActivity dialogsActivity, int i) {
            r2 = dialogsActivity;
            r3 = i;
        }

        @Override
        public void didChangeOwner(TLRPC.User user) {
        }

        @Override
        public void didSetRights(int i, TLRPC.TL_chatAdminRights tL_chatAdminRights, TLRPC.TL_chatBannedRights tL_chatBannedRights, String str) {
            r2.removeSelfFromStack();
            NotificationCenter.getInstance(r3).lambda$postNotificationNameOnUIThread$1(NotificationCenter.closeChats, new Object[0]);
        }
    }

    public class AnonymousClass18 implements MessagesController.MessagesLoadedCallback {
        final Bundle val$args;
        final long val$dialog_id;
        final Runnable val$dismissLoading;
        final BaseFragment val$lastFragment;
        final String val$livestream;
        final Integer val$messageId;

        AnonymousClass18(Runnable runnable, String str, BaseFragment baseFragment, long j, Integer num, Bundle bundle) {
            this.val$dismissLoading = runnable;
            this.val$livestream = str;
            this.val$lastFragment = baseFragment;
            this.val$dialog_id = j;
            this.val$messageId = num;
            this.val$args = bundle;
        }

        public void lambda$onMessagesLoaded$0(AccountInstance accountInstance, long j, BaseFragment baseFragment) {
            long j2 = -j;
            ChatObject.Call groupCall = accountInstance.getMessagesController().getGroupCall(j2, false);
            VoIPHelper.startCall(accountInstance.getMessagesController().getChat(Long.valueOf(j2)), accountInstance.getMessagesController().getInputPeer(j), null, false, Boolean.valueOf(groupCall == null || !groupCall.call.rtmp_stream), LaunchActivity.this, baseFragment, accountInstance);
        }

        public void lambda$onMessagesLoaded$1(final AccountInstance accountInstance, final long j, final BaseFragment baseFragment) {
            AndroidUtilities.runOnUIThread(new Runnable() {
                @Override
                public final void run() {
                    LaunchActivity.AnonymousClass18.this.lambda$onMessagesLoaded$0(accountInstance, j, baseFragment);
                }
            });
        }

        public void lambda$onMessagesLoaded$2(String str, final long j, final BaseFragment baseFragment) {
            if (str != null) {
                final AccountInstance accountInstance = AccountInstance.getInstance(LaunchActivity.this.currentAccount);
                long j2 = -j;
                if (accountInstance.getMessagesController().getGroupCall(j2, false) != null) {
                    VoIPHelper.startCall(accountInstance.getMessagesController().getChat(Long.valueOf(j2)), accountInstance.getMessagesController().getInputPeer(j), null, false, Boolean.valueOf(!r0.call.rtmp_stream), LaunchActivity.this, baseFragment, accountInstance);
                    return;
                }
                TLRPC.ChatFull chatFull = accountInstance.getMessagesController().getChatFull(j2);
                if (chatFull != null) {
                    if (chatFull.call != null) {
                        accountInstance.getMessagesController().getGroupCall(j2, true, new Runnable() {
                            @Override
                            public final void run() {
                                LaunchActivity.AnonymousClass18.this.lambda$onMessagesLoaded$1(accountInstance, j, baseFragment);
                            }
                        });
                    } else if (baseFragment.getParentActivity() != null) {
                        BulletinFactory.of(baseFragment).createSimpleBulletin(R.raw.linkbroken, LocaleController.getString(R.string.InviteExpired)).show();
                    }
                }
            }
        }

        @Override
        public void onError() {
            if (!LaunchActivity.this.isFinishing()) {
                AlertsCreator.showSimpleAlert((BaseFragment) LaunchActivity.mainFragmentsStack.get(LaunchActivity.mainFragmentsStack.size() - 1), LocaleController.getString(R.string.JoinToGroupErrorNotExist));
            }
            try {
                this.val$dismissLoading.run();
            } catch (Exception e) {
                FileLog.e(e);
            }
        }

        @Override
        public void onMessagesLoaded(boolean r8) {
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.LaunchActivity.AnonymousClass18.onMessagesLoaded(boolean):void");
        }
    }

    public class AnonymousClass19 implements MessagesController.MessagesLoadedCallback {
        final Bundle val$args;
        final boolean[] val$canceled;
        final Runnable val$dismissLoading;
        final TLRPC.ChatInvite val$invite;

        AnonymousClass19(Runnable runnable, boolean[] zArr, Bundle bundle, TLRPC.ChatInvite chatInvite) {
            r2 = runnable;
            r3 = zArr;
            r4 = bundle;
            r5 = chatInvite;
        }

        @Override
        public void onError() {
            if (!LaunchActivity.this.isFinishing()) {
                AlertsCreator.showSimpleAlert((BaseFragment) LaunchActivity.mainFragmentsStack.get(LaunchActivity.mainFragmentsStack.size() - 1), LocaleController.getString(R.string.JoinToGroupErrorNotExist));
            }
            try {
                r2.run();
            } catch (Exception e) {
                FileLog.e(e);
            }
        }

        @Override
        public void onMessagesLoaded(boolean z) {
            try {
                r2.run();
            } catch (Exception e) {
                FileLog.e(e);
            }
            if (r3[0]) {
                return;
            }
            ChatActivity chatActivity = new ChatActivity(r4);
            TLRPC.ChatInvite chatInvite = r5;
            if (chatInvite instanceof TLRPC.TL_chatInvitePeek) {
                chatActivity.setChatInvite(chatInvite);
            }
            LaunchActivity.this.getActionBarLayout().presentFragment(chatActivity);
        }
    }

    class AnonymousClass2 extends FrameLayout {
        AnonymousClass2(Context context) {
            super(context);
        }

        @Override
        protected void dispatchDraw(Canvas canvas) {
            super.dispatchDraw(canvas);
            LaunchActivity.this.drawRippleAbove(canvas, this);
        }
    }

    class AnonymousClass20 extends ClickableSpan {
        AnonymousClass20() {
        }

        @Override
        public void onClick(View view) {
            LaunchActivity.this.getActionBarLayout().presentFragment(new PremiumPreviewFragment("gift"));
        }

        @Override
        public void updateDrawState(TextPaint textPaint) {
            super.updateDrawState(textPaint);
            textPaint.setUnderlineText(false);
        }
    }

    class AnonymousClass21 extends AnimatorListenerAdapter {
        final RLottieImageView val$darkThemeView;
        final boolean val$toDark;

        AnonymousClass21(boolean z, RLottieImageView rLottieImageView) {
            r2 = z;
            r3 = rLottieImageView;
        }

        @Override
        public void onAnimationEnd(Animator animator) {
            LaunchActivity.this.rippleAbove = null;
            LaunchActivity.this.drawerLayoutContainer.invalidate();
            LaunchActivity.this.themeSwitchImageView.invalidate();
            LaunchActivity.this.themeSwitchImageView.setImageDrawable(null);
            LaunchActivity.this.themeSwitchImageView.setVisibility(8);
            LaunchActivity.this.themeSwitchSunView.setVisibility(8);
            NotificationCenter.getGlobalInstance().lambda$postNotificationNameOnUIThread$1(NotificationCenter.themeAccentListUpdated, new Object[0]);
            if (!r2) {
                r3.setVisibility(0);
            }
            DrawerProfileCell.switchingTheme = false;
        }
    }

    public class AnonymousClass22 implements Runnable {
        AnonymousClass22() {
        }

        @Override
        public void run() {
            if (LaunchActivity.this.lockRunnable == this) {
                if (AndroidUtilities.needShowPasscode(true)) {
                    if (BuildVars.LOGS_ENABLED) {
                        FileLog.d("lock app");
                    }
                    LaunchActivity.this.showPasscodeActivity(true, false, -1, -1, null, null);
                    try {
                        NotificationsController.getInstance(UserConfig.selectedAccount).showNotifications();
                    } catch (Exception e) {
                        FileLog.e(e);
                    }
                } else if (BuildVars.LOGS_ENABLED) {
                    FileLog.d("didn't pass lock check");
                }
                LaunchActivity.this.lockRunnable = null;
            }
        }
    }

    public class AnonymousClass23 extends AnimatorListenerAdapter {
        final int val$toColor;

        AnonymousClass23(int i) {
            r2 = i;
        }

        @Override
        public void onAnimationEnd(Animator animator) {
            LaunchActivity.this.setNavigationBarColor(r2, false);
        }
    }

    public class AnonymousClass3 extends DrawerLayoutContainer {
        private boolean wasPortrait;

        AnonymousClass3(Context context) {
            super(context);
        }

        public void lambda$onLayout$0() {
            if (LaunchActivity.this.selectAnimatedEmojiDialog != null) {
                LaunchActivity.this.selectAnimatedEmojiDialog.dismiss();
                LaunchActivity.this.selectAnimatedEmojiDialog = null;
            }
        }

        @Override
        public void closeDrawer() {
            super.closeDrawer();
            if (LaunchActivity.this.selectAnimatedEmojiDialog != null) {
                LaunchActivity.this.selectAnimatedEmojiDialog.dismiss();
                LaunchActivity.this.selectAnimatedEmojiDialog = null;
            }
        }

        @Override
        public void closeDrawer(boolean z) {
            super.closeDrawer(z);
            if (LaunchActivity.this.selectAnimatedEmojiDialog != null) {
                LaunchActivity.this.selectAnimatedEmojiDialog.dismiss();
                LaunchActivity.this.selectAnimatedEmojiDialog = null;
            }
        }

        @Override
        public void dispatchDraw(Canvas canvas) {
            if (LaunchActivity.this.actionBarLayout.getParent() == this) {
                LaunchActivity.this.actionBarLayout.parentDraw(this, canvas);
            }
            super.dispatchDraw(canvas);
        }

        @Override
        public void onLayout(boolean z, int i, int i2, int i3, int i4) {
            super.onLayout(z, i, i2, i3, i4);
            setDrawerPosition(getDrawerPosition());
            boolean z2 = i4 - i2 > i3 - i;
            if (z2 != this.wasPortrait) {
                post(new Runnable() {
                    @Override
                    public final void run() {
                        LaunchActivity.AnonymousClass3.this.lambda$onLayout$0();
                    }
                });
                this.wasPortrait = z2;
            }
        }
    }

    class AnonymousClass4 extends View {
        AnonymousClass4(Context context) {
            super(context);
        }

        @Override
        protected void onDraw(Canvas canvas) {
            if (LaunchActivity.this.themeSwitchSunDrawable != null) {
                LaunchActivity.this.themeSwitchSunDrawable.draw(canvas);
                invalidate();
            }
        }
    }

    class AnonymousClass5 extends FireworksOverlay {
        AnonymousClass5(Context context) {
            super(context);
            setVisibility(8);
        }

        @Override
        public void onStop() {
            super.onStop();
            setVisibility(8);
        }

        @Override
        public void start(boolean z) {
            setVisibility(0);
            super.start(z);
        }
    }

    class AnonymousClass6 extends RecyclerListView {
        AnonymousClass6(Context context) {
            super(context);
        }

        @Override
        public boolean drawChild(Canvas canvas, View view, long j) {
            int i;
            if (LaunchActivity.this.itemAnimator != null && LaunchActivity.this.itemAnimator.isRunning() && LaunchActivity.this.itemAnimator.isAnimatingChild(view)) {
                i = canvas.save();
                canvas.clipRect(0, LaunchActivity.this.itemAnimator.getAnimationClipTop(), getMeasuredWidth(), getMeasuredHeight());
            } else {
                i = -1;
            }
            boolean drawChild = super.drawChild(canvas, view, j);
            if (i >= 0) {
                canvas.restoreToCount(i);
                invalidate();
                invalidateViews();
            }
            return drawChild;
        }
    }

    public class AnonymousClass7 extends ItemTouchHelper.SimpleCallback {
        private RecyclerView.ViewHolder selectedViewHolder;

        public class AnonymousClass1 extends AnimatorListenerAdapter {
            final View val$view;

            AnonymousClass1(View view) {
                r2 = view;
            }

            @Override
            public void onAnimationEnd(Animator animator) {
                r2.setBackground(null);
            }
        }

        AnonymousClass7(int i, int i2) {
            super(i, i2);
        }

        private void clearSelectedViewHolder() {
            RecyclerView.ViewHolder viewHolder = this.selectedViewHolder;
            if (viewHolder != null) {
                View view = viewHolder.itemView;
                this.selectedViewHolder = null;
                view.setTranslationX(0.0f);
                view.setTranslationY(0.0f);
                if (Build.VERSION.SDK_INT >= 21) {
                    ObjectAnimator ofFloat = ObjectAnimator.ofFloat(view, "elevation", 0.0f);
                    ofFloat.addListener(new AnimatorListenerAdapter() {
                        final View val$view;

                        AnonymousClass1(View view2) {
                            r2 = view2;
                        }

                        @Override
                        public void onAnimationEnd(Animator animator) {
                            r2.setBackground(null);
                        }
                    });
                    ofFloat.setDuration(150L).start();
                }
            }
        }

        @Override
        public void clearView(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
            clearSelectedViewHolder();
        }

        @Override
        public boolean isLongPressDragEnabled() {
            return false;
        }

        @Override
        public void onChildDraw(Canvas canvas, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float f, float f2, int i, boolean z) {
            View view;
            View view2;
            View view3 = viewHolder.itemView;
            if (LaunchActivity.this.drawerLayoutAdapter.isAccountsShown()) {
                RecyclerView.ViewHolder findViewHolderForAdapterPosition = recyclerView.findViewHolderForAdapterPosition(LaunchActivity.this.drawerLayoutAdapter.getFirstAccountPosition() - 1);
                RecyclerView.ViewHolder findViewHolderForAdapterPosition2 = recyclerView.findViewHolderForAdapterPosition(LaunchActivity.this.drawerLayoutAdapter.getLastAccountPosition() + 1);
                if ((findViewHolderForAdapterPosition != null && (view2 = findViewHolderForAdapterPosition.itemView) != null && view2.getBottom() == view3.getTop() && f2 < 0.0f) || (findViewHolderForAdapterPosition2 != null && (view = findViewHolderForAdapterPosition2.itemView) != null && view.getTop() == view3.getBottom() && f2 > 0.0f)) {
                    f2 = 0.0f;
                }
            }
            view3.setTranslationX(f);
            view3.setTranslationY(f2);
        }

        @Override
        public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder viewHolder2) {
            if (viewHolder.getItemViewType() != viewHolder2.getItemViewType()) {
                return false;
            }
            LaunchActivity.this.drawerLayoutAdapter.swapElements(viewHolder.getAdapterPosition(), viewHolder2.getAdapterPosition());
            return true;
        }

        @Override
        public void onSelectedChanged(RecyclerView.ViewHolder viewHolder, int i) {
            clearSelectedViewHolder();
            if (i != 0) {
                this.selectedViewHolder = viewHolder;
                View view = viewHolder.itemView;
                LaunchActivity.this.sideMenu.cancelClickRunnables(false);
                view.setBackgroundColor(Theme.getColor(Theme.key_dialogBackground));
                if (Build.VERSION.SDK_INT >= 21) {
                    ObjectAnimator.ofFloat(view, "elevation", AndroidUtilities.dp(1.0f)).setDuration(150L).start();
                }
            }
        }

        @Override
        public void onSwiped(RecyclerView.ViewHolder viewHolder, int i) {
        }
    }

    public class AnonymousClass8 extends DialogsActivity {
        final int val$accountNumber;

        AnonymousClass8(Bundle bundle, int i) {
            super(bundle);
            r3 = i;
        }

        @Override
        public void onPreviewOpenAnimationEnd() {
            super.onPreviewOpenAnimationEnd();
            LaunchActivity.this.drawerLayoutContainer.setAllowOpenDrawer(false, false);
            LaunchActivity.this.drawerLayoutContainer.setDrawCurrentPreviewFragmentAbove(false);
            LaunchActivity.this.switchToAccount(r3, true);
            LaunchActivity.this.actionBarLayout.getView().invalidate();
        }

        @Override
        public void onTransitionAnimationEnd(boolean z, boolean z2) {
            super.onTransitionAnimationEnd(z, z2);
            if (z || !z2) {
                return;
            }
            LaunchActivity.this.drawerLayoutContainer.setDrawCurrentPreviewFragmentAbove(false);
            LaunchActivity.this.actionBarLayout.getView().invalidate();
        }
    }

    class AnonymousClass9 implements View.OnAttachStateChangeListener {
        AnonymousClass9() {
        }

        @Override
        public void onViewAttachedToWindow(View view) {
            LaunchActivity.this.getWindowManager().addCrossWindowBlurEnabledListener(Consumer.Wrapper.convert(LaunchActivity.this.blurListener));
        }

        @Override
        public void onViewDetachedFromWindow(View view) {
            LaunchActivity.this.getWindowManager().removeCrossWindowBlurEnabledListener(Consumer.Wrapper.convert(LaunchActivity.this.blurListener));
        }
    }

    private void checkCurrentAccount() {
        int i = this.currentAccount;
        if (i != UserConfig.selectedAccount) {
            NotificationCenter.getInstance(i).removeObserver(this, NotificationCenter.openBoostForUsersDialog);
            NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.appDidLogout);
            NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.mainUserInfoChanged);
            NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.attachMenuBotsDidLoad);
            NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.didUpdateConnectionState);
            NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.needShowAlert);
            NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.wasUnableToFindCurrentLocation);
            NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.openArticle);
            NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.hasNewContactsToImport);
            NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.needShowPlayServicesAlert);
            NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.fileLoaded);
            NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.fileLoadProgressChanged);
            NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.fileLoadFailed);
            NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.historyImportProgressChanged);
            NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.groupCallUpdated);
            NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.stickersImportComplete);
            NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.newSuggestionsAvailable);
            NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.currentUserPremiumStatusChanged);
            NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.chatSwithcedToForum);
            NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.storiesEnabledUpdate);
        }
        int i2 = UserConfig.selectedAccount;
        this.currentAccount = i2;
        NotificationCenter.getInstance(i2).addObserver(this, NotificationCenter.openBoostForUsersDialog);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.appDidLogout);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.mainUserInfoChanged);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.attachMenuBotsDidLoad);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.didUpdateConnectionState);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.needShowAlert);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.wasUnableToFindCurrentLocation);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.openArticle);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.hasNewContactsToImport);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.needShowPlayServicesAlert);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.fileLoaded);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.fileLoadProgressChanged);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.fileLoadFailed);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.historyImportProgressChanged);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.groupCallUpdated);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.stickersImportComplete);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.newSuggestionsAvailable);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.currentUserShowLimitReachedDialog);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.currentUserPremiumStatusChanged);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.chatSwithcedToForum);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.storiesEnabledUpdate);
    }

    private void checkFreeDiscSpace(final int i) {
        staticInstanceForAlerts = this;
        AutoDeleteMediaTask.run();
        SharedConfig.checkLogsToDelete();
        if ((Build.VERSION.SDK_INT < 26 || i != 0) && !this.checkFreeDiscSpaceShown) {
            Utilities.globalQueue.postRunnable(new Runnable() {
                @Override
                public final void run() {
                    LaunchActivity.this.lambda$checkFreeDiscSpace$155(i);
                }
            }, 2000L);
        }
    }

    public static void checkFreeDiscSpaceStatic(int i) {
        LaunchActivity launchActivity = staticInstanceForAlerts;
        if (launchActivity != null) {
            launchActivity.checkFreeDiscSpace(i);
        }
    }

    private void checkLayout() {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.LaunchActivity.checkLayout():void");
    }

    private void checkSystemBarColors() {
        checkSystemBarColors(false, true, !this.isNavigationBarColorFrozen, true);
    }

    private void checkSystemBarColors(boolean z, boolean z2) {
        checkSystemBarColors(false, z, z2, true);
    }

    private void checkWasMutedByAdmin(boolean z) {
        ChatObject.Call call;
        long j;
        VoIPService sharedInstance = VoIPService.getSharedInstance();
        boolean z2 = false;
        if (sharedInstance == null || (call = sharedInstance.groupCall) == null) {
            this.wasMutedByAdminRaisedHand = false;
            return;
        }
        boolean z3 = this.wasMutedByAdminRaisedHand;
        TLRPC.InputPeer groupCallPeer = sharedInstance.getGroupCallPeer();
        if (groupCallPeer != null) {
            j = groupCallPeer.user_id;
            if (j == 0) {
                long j2 = groupCallPeer.chat_id;
                if (j2 == 0) {
                    j2 = groupCallPeer.channel_id;
                }
                j = -j2;
            }
        } else {
            j = UserConfig.getInstance(this.currentAccount).clientUserId;
        }
        TLRPC.TL_groupCallParticipant tL_groupCallParticipant = (TLRPC.TL_groupCallParticipant) call.participants.get(j);
        boolean z4 = (tL_groupCallParticipant == null || tL_groupCallParticipant.can_self_unmute || !tL_groupCallParticipant.muted) ? false : true;
        if (z4 && tL_groupCallParticipant.raise_hand_rating != 0) {
            z2 = true;
        }
        this.wasMutedByAdminRaisedHand = z2;
        if (z || !z3 || z2 || z4 || GroupCallActivity.groupCallInstance != null) {
            return;
        }
        showVoiceChatTooltip(38);
    }

    public static void clearFragments() {
        Iterator it = mainFragmentsStack.iterator();
        while (it.hasNext()) {
            ((BaseFragment) it.next()).onFragmentDestroy();
        }
        mainFragmentsStack.clear();
        if (AndroidUtilities.isTablet()) {
            Iterator it2 = layerFragmentsStack.iterator();
            while (it2.hasNext()) {
                ((BaseFragment) it2.next()).onFragmentDestroy();
            }
            layerFragmentsStack.clear();
            Iterator it3 = rightFragmentsStack.iterator();
            while (it3.hasNext()) {
                ((BaseFragment) it3.next()).onFragmentDestroy();
            }
            rightFragmentsStack.clear();
        }
    }

    public static void dismissAllWeb() {
        ArrayList arrayList;
        BaseFragment safeLastFragment = getSafeLastFragment();
        if (safeLastFragment == null) {
            return;
        }
        EmptyBaseFragment sheetFragment = safeLastFragment.getParentLayout() instanceof ActionBarLayout ? ((ActionBarLayout) safeLastFragment.getParentLayout()).getSheetFragment(false) : null;
        if (sheetFragment != null && (arrayList = sheetFragment.sheetsStack) != null) {
            for (int size = arrayList.size() - 1; size >= 0; size--) {
                ((BaseFragment.AttachedSheet) sheetFragment.sheetsStack.get(size)).dismiss(true);
            }
        }
        ArrayList arrayList2 = safeLastFragment.sheetsStack;
        if (arrayList2 != null) {
            for (int size2 = arrayList2.size() - 1; size2 >= 0; size2--) {
                ((BaseFragment.AttachedSheet) safeLastFragment.sheetsStack.get(size2)).dismiss(true);
            }
        }
        ArrayList arrayList3 = new ArrayList();
        Iterator it = BotWebViewSheet.activeSheets.iterator();
        while (it.hasNext()) {
            arrayList3.add((BotWebViewSheet) it.next());
        }
        Iterator it2 = arrayList3.iterator();
        while (it2.hasNext()) {
            ((BotWebViewSheet) it2.next()).dismiss(true);
        }
    }

    public void drawRippleAbove(Canvas canvas, View view) {
        View view2;
        if (view == null || (view2 = this.rippleAbove) == null || view2.getBackground() == null) {
            return;
        }
        if (this.tempLocation == null) {
            this.tempLocation = new int[2];
        }
        this.rippleAbove.getLocationInWindow(this.tempLocation);
        int[] iArr = this.tempLocation;
        int i = iArr[0];
        int i2 = iArr[1];
        view.getLocationInWindow(iArr);
        int[] iArr2 = this.tempLocation;
        int i3 = i - iArr2[0];
        int i4 = i2 - iArr2[1];
        canvas.save();
        canvas.translate(i3, i4);
        this.rippleAbove.getBackground().draw(canvas);
        canvas.restore();
    }

    private java.util.List findContacts(java.lang.String r19, java.lang.String r20, boolean r21) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.LaunchActivity.findContacts(java.lang.String, java.lang.String, boolean):java.util.List");
    }

    private BaseFragment getClientNotActivatedFragment() {
        return LoginActivity.loadCurrentState(false, this.currentAccount).getInt("currentViewNum", 0) != 0 ? new LoginActivity() : new IntroActivity();
    }

    public static BaseFragment getLastFragment() {
        INavigationLayout actionBarLayout;
        INavigationLayout iNavigationLayout;
        BubbleActivity bubbleActivity = BubbleActivity.instance;
        if (bubbleActivity != null && (iNavigationLayout = bubbleActivity.actionBarLayout) != null) {
            return iNavigationLayout.getLastFragment();
        }
        LaunchActivity launchActivity = instance;
        if (launchActivity == null || launchActivity.sheetFragmentsStack.isEmpty()) {
            LaunchActivity launchActivity2 = instance;
            if (launchActivity2 == null || launchActivity2.getActionBarLayout() == null) {
                return null;
            }
            actionBarLayout = instance.getActionBarLayout();
        } else {
            actionBarLayout = (INavigationLayout) instance.sheetFragmentsStack.get(r0.size() - 1);
        }
        return actionBarLayout.getLastFragment();
    }

    public static BaseFragment getSafeLastFragment() {
        INavigationLayout actionBarLayout;
        INavigationLayout iNavigationLayout;
        BubbleActivity bubbleActivity = BubbleActivity.instance;
        if (bubbleActivity != null && (iNavigationLayout = bubbleActivity.actionBarLayout) != null) {
            return iNavigationLayout.getSafeLastFragment();
        }
        LaunchActivity launchActivity = instance;
        if (launchActivity == null || launchActivity.sheetFragmentsStack.isEmpty()) {
            LaunchActivity launchActivity2 = instance;
            if (launchActivity2 == null || launchActivity2.getActionBarLayout() == null) {
                return null;
            }
            actionBarLayout = instance.getActionBarLayout();
        } else {
            actionBarLayout = (INavigationLayout) instance.sheetFragmentsStack.get(r0.size() - 1);
        }
        return actionBarLayout.getSafeLastFragment();
    }

    private String getStringForLanguageAlert(HashMap hashMap, String str, int i) {
        String str2 = (String) hashMap.get(str);
        return str2 == null ? LocaleController.getString(str, i) : str2;
    }

    public static int getTimestampFromLink(Uri uri) {
        int i;
        String query = uri.getPathSegments().contains("video") ? uri.getQuery() : uri.getQueryParameter("t") != null ? uri.getQueryParameter("t") : null;
        if (query == null) {
            return -1;
        }
        try {
            i = Integer.parseInt(query);
        } catch (Throwable unused) {
            i = -1;
        }
        if (i == -1) {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("mm:ss");
            try {
                return (int) ((simpleDateFormat.parse(query).getTime() - simpleDateFormat.parse("00:00").getTime()) / 1000);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        return i;
    }

    private boolean handleIntent(Intent intent, boolean z, boolean z2, boolean z3) {
        return handleIntent(intent, z, z2, z3, null, true, false);
    }

    private boolean handleIntent(android.content.Intent r108, boolean r109, boolean r110, boolean r111, org.telegram.messenger.browser.Browser.Progress r112, boolean r113, boolean r114) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.LaunchActivity.handleIntent(android.content.Intent, boolean, boolean, boolean, org.telegram.messenger.browser.Browser$Progress, boolean, boolean):boolean");
    }

    private void invalidateCachedViews(View view) {
        if (view.getLayerType() != 0) {
            view.invalidate();
        }
        if (view instanceof ViewGroup) {
            ViewGroup viewGroup = (ViewGroup) view;
            for (int i = 0; i < viewGroup.getChildCount(); i++) {
                invalidateCachedViews(viewGroup.getChildAt(i));
            }
        }
    }

    private void invalidateTabletMode() {
        long j;
        ArrayList arrayList;
        Boolean wasTablet = AndroidUtilities.getWasTablet();
        if (wasTablet == null) {
            return;
        }
        AndroidUtilities.resetWasTabletFlag();
        if (wasTablet.booleanValue() != AndroidUtilities.isTablet()) {
            long j2 = 0;
            if (wasTablet.booleanValue()) {
                ArrayList arrayList2 = mainFragmentsStack;
                ArrayList arrayList3 = rightFragmentsStack;
                arrayList2.addAll(arrayList3);
                ArrayList arrayList4 = layerFragmentsStack;
                arrayList2.addAll(arrayList4);
                arrayList3.clear();
                arrayList4.clear();
                j = 0;
            } else {
                ArrayList arrayList5 = mainFragmentsStack;
                ArrayList<BaseFragment> arrayList6 = new ArrayList(arrayList5);
                arrayList5.clear();
                rightFragmentsStack.clear();
                layerFragmentsStack.clear();
                long j3 = 0;
                j = 0;
                for (BaseFragment baseFragment : arrayList6) {
                    if (baseFragment instanceof DialogsActivity) {
                        DialogsActivity dialogsActivity = (DialogsActivity) baseFragment;
                        if (dialogsActivity.isMainDialogList() && !dialogsActivity.isArchive()) {
                            arrayList = mainFragmentsStack;
                            arrayList.add(baseFragment);
                        }
                    }
                    if (baseFragment instanceof ChatActivity) {
                        ChatActivity chatActivity = (ChatActivity) baseFragment;
                        if (!chatActivity.isInScheduleMode()) {
                            rightFragmentsStack.add(baseFragment);
                            if (j3 == 0) {
                                j3 = chatActivity.getDialogId();
                                j = chatActivity.getTopicId();
                            }
                        }
                    }
                    arrayList = layerFragmentsStack;
                    arrayList.add(baseFragment);
                }
                j2 = j3;
            }
            setupActionBarLayout();
            this.actionBarLayout.rebuildFragments(1);
            if (AndroidUtilities.isTablet()) {
                this.rightActionBarLayout.rebuildFragments(1);
                this.layersActionBarLayout.rebuildFragments(1);
                Iterator it = mainFragmentsStack.iterator();
                while (it.hasNext()) {
                    BaseFragment baseFragment2 = (BaseFragment) it.next();
                    if (baseFragment2 instanceof DialogsActivity) {
                        DialogsActivity dialogsActivity2 = (DialogsActivity) baseFragment2;
                        if (dialogsActivity2.isMainDialogList()) {
                            dialogsActivity2.setOpenedDialogId(j2, j);
                        }
                    }
                }
            }
        }
    }

    public void lambda$animateNavigationBarColor$164(ValueAnimator valueAnimator) {
        setNavigationBarColor(((Integer) valueAnimator.getAnimatedValue()).intValue(), false);
    }

    public void lambda$checkAppUpdate$128(TLRPC.TL_help_appUpdate tL_help_appUpdate, int i, Browser.Progress progress) {
        BaseFragment lastFragment;
        TLRPC.TL_help_appUpdate tL_help_appUpdate2 = SharedConfig.pendingAppUpdate;
        if (tL_help_appUpdate2 == null || !tL_help_appUpdate2.version.equals(tL_help_appUpdate.version)) {
            boolean newAppVersionAvailable = SharedConfig.setNewAppVersionAvailable(tL_help_appUpdate);
            if (newAppVersionAvailable) {
                if (tL_help_appUpdate.can_not_skip) {
                    showUpdateActivity(i, tL_help_appUpdate, false);
                } else if (ApplicationLoader.isStandaloneBuild() || BuildVars.DEBUG_VERSION) {
                    this.drawerLayoutAdapter.notifyDataSetChanged();
                    ApplicationLoader.applicationLoaderInstance.showUpdateAppPopup(this, tL_help_appUpdate, i);
                }
                NotificationCenter.getGlobalInstance().lambda$postNotificationNameOnUIThread$1(NotificationCenter.appUpdateAvailable, new Object[0]);
            }
            if (progress != null) {
                progress.end();
                if (newAppVersionAvailable || (lastFragment = getLastFragment()) == null) {
                    return;
                }
                BulletinFactory.of(lastFragment).createSimpleBulletin(R.raw.chats_infotip, LocaleController.getString(R.string.YourVersionIsLatest)).show();
            }
        }
    }

    public static void lambda$checkAppUpdate$129(Browser.Progress progress) {
        if (progress != null) {
            progress.end();
            BaseFragment lastFragment = getLastFragment();
            if (lastFragment != null) {
                BulletinFactory.of(lastFragment).createSimpleBulletin(R.raw.chats_infotip, LocaleController.getString(R.string.YourVersionIsLatest)).show();
            }
        }
    }

    public static void lambda$checkAppUpdate$130(Browser.Progress progress, TLRPC.TL_error tL_error) {
        if (progress != null) {
            progress.end();
            BaseFragment lastFragment = getLastFragment();
            if (lastFragment != null) {
                BulletinFactory.of(lastFragment).showForError(tL_error);
            }
        }
    }

    public void lambda$checkAppUpdate$131(final int i, final Browser.Progress progress, TLObject tLObject, final TLRPC.TL_error tL_error) {
        Runnable runnable;
        SharedConfig.lastUpdateCheckTime = System.currentTimeMillis();
        SharedConfig.saveConfig();
        if (tLObject instanceof TLRPC.TL_help_appUpdate) {
            final TLRPC.TL_help_appUpdate tL_help_appUpdate = (TLRPC.TL_help_appUpdate) tLObject;
            AndroidUtilities.runOnUIThread(new Runnable() {
                @Override
                public final void run() {
                    LaunchActivity.this.lambda$checkAppUpdate$128(tL_help_appUpdate, i, progress);
                }
            });
            return;
        }
        if (tLObject instanceof TLRPC.TL_help_noAppUpdate) {
            runnable = new Runnable() {
                @Override
                public final void run() {
                    LaunchActivity.lambda$checkAppUpdate$129(Browser.Progress.this);
                }
            };
        } else if (tL_error == null) {
            return;
        } else {
            runnable = new Runnable() {
                @Override
                public final void run() {
                    LaunchActivity.lambda$checkAppUpdate$130(Browser.Progress.this, tL_error);
                }
            };
        }
        AndroidUtilities.runOnUIThread(runnable);
    }

    public void lambda$checkAppUpdate$132(int i) {
        ConnectionsManager.getInstance(this.currentAccount).cancelRequest(i, true);
    }

    public void lambda$checkFreeDiscSpace$153(DialogInterface dialogInterface) {
        this.checkFreeDiscSpaceShown = false;
    }

    public void lambda$checkFreeDiscSpace$154() {
        if (this.checkFreeDiscSpaceShown) {
            return;
        }
        try {
            Dialog createFreeSpaceDialog = AlertsCreator.createFreeSpaceDialog(this);
            createFreeSpaceDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public final void onDismiss(DialogInterface dialogInterface) {
                    LaunchActivity.this.lambda$checkFreeDiscSpace$153(dialogInterface);
                }
            });
            this.checkFreeDiscSpaceShown = true;
            createFreeSpaceDialog.show();
        } catch (Throwable unused) {
        }
    }

    public void lambda$checkFreeDiscSpace$155(int i) {
        File directory;
        if (UserConfig.getInstance(this.currentAccount).isClientActivated()) {
            try {
                SharedPreferences globalMainSettings = MessagesController.getGlobalMainSettings();
                if ((((i == 2 || i == 1) && Math.abs(this.alreadyShownFreeDiscSpaceAlertForced - System.currentTimeMillis()) > 240000) || Math.abs(globalMainSettings.getLong("last_space_check", 0L) - System.currentTimeMillis()) >= 259200000) && (directory = FileLoader.getDirectory(4)) != null) {
                    StatFs statFs = new StatFs(directory.getAbsolutePath());
                    long availableBlocksLong = statFs.getAvailableBlocksLong() * statFs.getBlockSizeLong();
                    if (i > 0 || availableBlocksLong < 52428800) {
                        if (i > 0) {
                            this.alreadyShownFreeDiscSpaceAlertForced = System.currentTimeMillis();
                        }
                        globalMainSettings.edit().putLong("last_space_check", System.currentTimeMillis()).commit();
                        AndroidUtilities.runOnUIThread(new Runnable() {
                            @Override
                            public final void run() {
                                LaunchActivity.this.lambda$checkFreeDiscSpace$154();
                            }
                        });
                    }
                }
            } catch (Throwable unused) {
            }
        }
    }

    public static void lambda$didReceivedNotification$140(int i, DialogInterface dialogInterface, int i2) {
        ArrayList arrayList = mainFragmentsStack;
        if (arrayList.isEmpty()) {
            return;
        }
        MessagesController.getInstance(i).openByUserName("spambot", (BaseFragment) arrayList.get(arrayList.size() - 1), 1);
    }

    public void lambda$didReceivedNotification$141(DialogInterface dialogInterface, int i) {
        MessagesController.getInstance(this.currentAccount).performLogout(2);
    }

    public static void lambda$didReceivedNotification$142(HashMap hashMap, int i, TLRPC.MessageMedia messageMedia, int i2, boolean z, int i3) {
        Iterator it = hashMap.entrySet().iterator();
        while (it.hasNext()) {
            MessageObject messageObject = (MessageObject) ((Map.Entry) it.next()).getValue();
            SendMessagesHelper.getInstance(i).sendMessage(SendMessagesHelper.SendMessageParams.of(messageMedia, messageObject.getDialogId(), messageObject, (MessageObject) null, (TLRPC.ReplyMarkup) null, (HashMap<String, String>) null, z, i3));
        }
    }

    public void lambda$didReceivedNotification$143(final HashMap hashMap, final int i, DialogInterface dialogInterface, int i2) {
        ArrayList arrayList = mainFragmentsStack;
        if (!arrayList.isEmpty() && AndroidUtilities.isMapsInstalled((BaseFragment) arrayList.get(arrayList.size() - 1))) {
            LocationActivity locationActivity = new LocationActivity(0);
            locationActivity.setDelegate(new LocationActivity.LocationActivityDelegate() {
                @Override
                public final void didSelectLocation(TLRPC.MessageMedia messageMedia, int i3, boolean z, int i4) {
                    LaunchActivity.lambda$didReceivedNotification$142(hashMap, i, messageMedia, i3, z, i4);
                }
            });
            lambda$runLinkRequest$93(locationActivity);
        }
    }

    public static void lambda$didReceivedNotification$144(int i, HashMap hashMap, boolean z, boolean z2, DialogInterface dialogInterface, int i2) {
        ContactsController.getInstance(i).syncPhoneBookByAlert(hashMap, z, z2, false);
    }

    public static void lambda$didReceivedNotification$145(int i, HashMap hashMap, boolean z, boolean z2, DialogInterface dialogInterface, int i2) {
        ContactsController.getInstance(i).syncPhoneBookByAlert(hashMap, z, z2, true);
    }

    public static void lambda$didReceivedNotification$146(int i, HashMap hashMap, boolean z, boolean z2, DialogInterface dialogInterface, int i2) {
        ContactsController.getInstance(i).syncPhoneBookByAlert(hashMap, z, z2, true);
    }

    public void lambda$didReceivedNotification$147(ValueAnimator valueAnimator) {
        this.frameLayout.invalidate();
    }

    public void lambda$didReceivedNotification$148() {
        if (this.isNavigationBarColorFrozen) {
            this.isNavigationBarColorFrozen = false;
            checkSystemBarColors(false, true);
        }
    }

    public void lambda$didReceivedNotification$149(TLObject tLObject, Theme.ThemeInfo themeInfo) {
        if (!(tLObject instanceof TLRPC.TL_wallPaper)) {
            onThemeLoadFinish();
            return;
        }
        TLRPC.TL_wallPaper tL_wallPaper = (TLRPC.TL_wallPaper) tLObject;
        this.loadingThemeInfo = themeInfo;
        this.loadingThemeWallpaperName = FileLoader.getAttachFileName(tL_wallPaper.document);
        this.loadingThemeWallpaper = tL_wallPaper;
        FileLoader.getInstance(themeInfo.account).loadFile(tL_wallPaper.document, tL_wallPaper, 1, 1);
    }

    public void lambda$didReceivedNotification$150(final Theme.ThemeInfo themeInfo, final TLObject tLObject, TLRPC.TL_error tL_error) {
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                LaunchActivity.this.lambda$didReceivedNotification$149(tLObject, themeInfo);
            }
        });
    }

    public void lambda$didReceivedNotification$151() {
        if (this.loadingTheme == null) {
            return;
        }
        File file = new File(ApplicationLoader.getFilesDirFixed(), "remote" + this.loadingTheme.id + ".attheme");
        TLRPC.TL_theme tL_theme = this.loadingTheme;
        Theme.ThemeInfo applyThemeFile = Theme.applyThemeFile(file, tL_theme.title, tL_theme, true);
        if (applyThemeFile != null) {
            lambda$runLinkRequest$93(new ThemePreviewActivity(applyThemeFile, true, 0, false, false));
        }
        onThemeLoadFinish();
    }

    public void lambda$didReceivedNotification$152(Theme.ThemeInfo themeInfo, File file) {
        themeInfo.createBackground(file, themeInfo.pathToWallpaper);
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                LaunchActivity.this.lambda$didReceivedNotification$151();
            }
        });
    }

    public void lambda$didSelectDialogs$134(int i, DialogsActivity dialogsActivity, boolean z, ArrayList arrayList, Uri uri, AlertDialog alertDialog, long j) {
        String str;
        long j2 = j;
        if (j2 != 0) {
            Bundle bundle = new Bundle();
            bundle.putBoolean("scrollToTopOnResume", true);
            if (!AndroidUtilities.isTablet()) {
                NotificationCenter.getInstance(i).lambda$postNotificationNameOnUIThread$1(NotificationCenter.closeChats, new Object[0]);
            }
            if (DialogObject.isUserDialog(j)) {
                str = "user_id";
            } else {
                j2 = -j2;
                str = "chat_id";
            }
            bundle.putLong(str, j2);
            ChatActivity chatActivity = new ChatActivity(bundle);
            chatActivity.setOpenImport();
            getActionBarLayout().presentFragment(chatActivity, dialogsActivity != null || z, dialogsActivity == null, true, false);
        } else {
            this.documentsUrisArray = arrayList;
            if (arrayList == null) {
                this.documentsUrisArray = new ArrayList();
            }
            this.documentsUrisArray.add(0, uri);
            openDialogsToSend(true);
        }
        try {
            alertDialog.dismiss();
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    public void lambda$didSelectDialogs$135(ChatActivity chatActivity, ArrayList arrayList, int i, CharSequence charSequence, int i2, boolean z, TLRPC.User user, boolean z2, int i3, long j, boolean z3) {
        MessageObject messageObject;
        TLRPC.TL_forumTopic findTopic;
        if (chatActivity != null) {
            getActionBarLayout().presentFragment(chatActivity, true, false, true, false);
        }
        AccountInstance accountInstance = AccountInstance.getInstance(UserConfig.selectedAccount);
        for (int i4 = 0; i4 < arrayList.size(); i4++) {
            long j2 = ((MessagesStorage.TopicKey) arrayList.get(i4)).dialogId;
            long j3 = ((MessagesStorage.TopicKey) arrayList.get(i4)).topicId;
            if (j3 == 0 || (findTopic = accountInstance.getMessagesController().getTopicsController().findTopic(-j2, j3)) == null || findTopic.topicStartMessage == null) {
                messageObject = null;
            } else {
                MessageObject messageObject2 = new MessageObject(accountInstance.getCurrentAccount(), findTopic.topicStartMessage, false, false);
                messageObject2.isTopicMainMessage = true;
                messageObject = messageObject2;
            }
            SendMessagesHelper.SendMessageParams of = SendMessagesHelper.SendMessageParams.of(user, j2, messageObject, messageObject, (TLRPC.ReplyMarkup) null, (HashMap<String, String>) null, z2, i3 != 0 ? i3 : i);
            if (TextUtils.isEmpty(charSequence)) {
                of.effect_id = j;
            }
            of.invert_media = z3;
            SendMessagesHelper.getInstance(i2).sendMessage(of);
            if (!TextUtils.isEmpty(charSequence)) {
                SendMessagesHelper.prepareSendingText(accountInstance, charSequence.toString(), j2, z, i3 != 0 ? i3 : i, j);
            }
        }
    }

    public void lambda$handleIntent$15(String str) {
        if (this.actionBarLayout.getFragmentStack().isEmpty()) {
            return;
        }
        this.actionBarLayout.getFragmentStack().get(0).presentFragment(new PremiumPreviewFragment(Uri.parse(str).getQueryParameter("ref")));
    }

    public void lambda$handleIntent$16(Intent intent, boolean z) {
        handleIntent(intent, true, false, false);
    }

    public void lambda$handleIntent$17(AlertDialog alertDialog, TLRPC.TL_error tL_error, String str, Bundle bundle, TLObject tLObject, TL_account.sendConfirmPhoneCode sendconfirmphonecode) {
        alertDialog.dismiss();
        if (tL_error == null) {
            lambda$runLinkRequest$93(new LoginActivity().cancelAccountDeletion(str, bundle, (TLRPC.TL_auth_sentCode) tLObject));
        } else {
            AlertsCreator.processError(this.currentAccount, tL_error, getActionBarLayout().getLastFragment(), sendconfirmphonecode, new Object[0]);
        }
    }

    public void lambda$handleIntent$18(final AlertDialog alertDialog, final String str, final Bundle bundle, final TL_account.sendConfirmPhoneCode sendconfirmphonecode, final TLObject tLObject, final TLRPC.TL_error tL_error) {
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                LaunchActivity.this.lambda$handleIntent$17(alertDialog, tL_error, str, bundle, tLObject, sendconfirmphonecode);
            }
        });
    }

    public void lambda$handleIntent$19(long j, long j2, ChatActivity chatActivity) {
        FileLog.d("LaunchActivity openForum after load " + j + " " + j2 + " TL_forumTopic " + MessagesController.getInstance(this.currentAccount).getTopicsController().findTopic(j, j2));
        if (this.actionBarLayout != null) {
            ForumUtilities.applyTopic(chatActivity, MessagesStorage.TopicKey.of(-j, j2));
            getActionBarLayout().presentFragment(chatActivity);
        }
    }

    public static void lambda$handleIntent$20(int[] iArr, long j, TLRPC.MessageMedia messageMedia, int i, boolean z, int i2) {
        SendMessagesHelper.getInstance(iArr[0]).sendMessage(SendMessagesHelper.SendMessageParams.of(messageMedia, j, (MessageObject) null, (MessageObject) null, (TLRPC.ReplyMarkup) null, (HashMap<String, String>) null, z, i2));
    }

    public void lambda$handleIntent$21(final int[] iArr, LocationController.SharingLocationInfo sharingLocationInfo) {
        int i = sharingLocationInfo.messageObject.currentAccount;
        iArr[0] = i;
        switchToAccount(i, true);
        LocationActivity locationActivity = new LocationActivity(2);
        locationActivity.setMessageObject(sharingLocationInfo.messageObject);
        final long dialogId = sharingLocationInfo.messageObject.getDialogId();
        locationActivity.setDelegate(new LocationActivity.LocationActivityDelegate() {
            @Override
            public final void didSelectLocation(TLRPC.MessageMedia messageMedia, int i2, boolean z, int i3) {
                LaunchActivity.lambda$handleIntent$20(iArr, dialogId, messageMedia, i2, z, i3);
            }
        });
        lambda$runLinkRequest$93(locationActivity);
    }

    public void lambda$handleIntent$22() {
        if (this.actionBarLayout.getFragmentStack().isEmpty()) {
            return;
        }
        this.actionBarLayout.getFragmentStack().get(0).showDialog(new StickersAlert(this, this.importingStickersSoftware, this.importingStickers, this.importingStickersEmoji, null));
    }

    public static void lambda$handleIntent$23(Thread thread, Throwable th) {
        if (thread == Looper.getMainLooper().getThread()) {
            FileLog.fatal(th, true);
        }
    }

    public static void lambda$handleIntent$24(Thread thread, Throwable th) {
        if (thread == Looper.getMainLooper().getThread()) {
            FileLog.fatal(th, true);
        }
    }

    public void lambda$handleIntent$25(BaseFragment baseFragment, boolean z) {
        presentFragment(baseFragment, z, false);
    }

    public void lambda$handleIntent$26(boolean z, int[] iArr, TLRPC.User user, String str, ContactsActivity contactsActivity) {
        TLRPC.UserFull userFull = MessagesController.getInstance(this.currentAccount).getUserFull(user.id);
        VoIPHelper.startCall(user, z, userFull != null && userFull.video_calls_available, this, userFull, AccountInstance.getInstance(iArr[0]));
    }

    public static void lambda$handleIntent$27(ActionIntroActivity actionIntroActivity, TLRPC.TL_error tL_error) {
        AlertsCreator.showSimpleAlert(actionIntroActivity, LocaleController.getString(R.string.AuthAnotherClient), LocaleController.getString(R.string.ErrorOccurred) + "\n" + tL_error.text);
    }

    public static void lambda$handleIntent$28(AlertDialog alertDialog, TLObject tLObject, final ActionIntroActivity actionIntroActivity, final TLRPC.TL_error tL_error) {
        try {
            alertDialog.dismiss();
        } catch (Exception unused) {
        }
        if (tLObject instanceof TLRPC.TL_authorization) {
            return;
        }
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                LaunchActivity.lambda$handleIntent$27(ActionIntroActivity.this, tL_error);
            }
        });
    }

    public static void lambda$handleIntent$29(final AlertDialog alertDialog, final ActionIntroActivity actionIntroActivity, final TLObject tLObject, final TLRPC.TL_error tL_error) {
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                LaunchActivity.lambda$handleIntent$28(AlertDialog.this, tLObject, actionIntroActivity, tL_error);
            }
        });
    }

    public void lambda$handleIntent$30(final ActionIntroActivity actionIntroActivity, String str) {
        final AlertDialog alertDialog = new AlertDialog(this, 3);
        alertDialog.setCanCancel(false);
        alertDialog.show();
        byte[] decode = Base64.decode(str.substring(17), 8);
        TLRPC.TL_auth_acceptLoginToken tL_auth_acceptLoginToken = new TLRPC.TL_auth_acceptLoginToken();
        tL_auth_acceptLoginToken.token = decode;
        ConnectionsManager.getInstance(this.currentAccount).sendRequest(tL_auth_acceptLoginToken, new RequestDelegate() {
            @Override
            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                LaunchActivity.lambda$handleIntent$29(AlertDialog.this, actionIntroActivity, tLObject, tL_error);
            }
        });
    }

    public void lambda$handleIntent$31(BaseFragment baseFragment, String str, String str2, DialogInterface dialogInterface, int i) {
        NewContactBottomSheet newContactBottomSheet = new NewContactBottomSheet(baseFragment, this);
        newContactBottomSheet.setInitialPhoneNumber(str, false);
        if (str2 != null) {
            String[] split = str2.split(" ", 2);
            newContactBottomSheet.setInitialName(split[0], split.length > 1 ? split[1] : null);
        }
        newContactBottomSheet.show();
    }

    public void lambda$onActivityResult$137() {
        GroupCallPip.clearForce();
        GroupCallPip.updateVisibility(this);
    }

    public static boolean lambda$onCreate$0() {
        return SharedConfig.passcodeHash.length() > 0 && !SharedConfig.allowScreenCapture;
    }

    public void lambda$onCreate$1(View view) {
        showSelectStatusDialog();
    }

    public void lambda$onCreate$2() {
        this.drawerLayoutContainer.closeDrawer(false);
    }

    public void lambda$onCreate$3(TLRPC.TL_attachMenuBot tL_attachMenuBot) {
        tL_attachMenuBot.side_menu_disclaimer_needed = false;
        tL_attachMenuBot.inactive = false;
        showAttachMenuBot(tL_attachMenuBot, null, true);
        MediaDataController.getInstance(this.currentAccount).updateAttachMenuBotsInCache();
    }

    public void lambda$onCreate$4(final TLRPC.TL_attachMenuBot tL_attachMenuBot, TLObject tLObject, TLRPC.TL_error tL_error) {
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                LaunchActivity.this.lambda$onCreate$3(tL_attachMenuBot);
            }
        });
    }

    public void lambda$onCreate$5(final TLRPC.TL_attachMenuBot tL_attachMenuBot, Boolean bool) {
        TLRPC.TL_messages_toggleBotInAttachMenu tL_messages_toggleBotInAttachMenu = new TLRPC.TL_messages_toggleBotInAttachMenu();
        tL_messages_toggleBotInAttachMenu.bot = MessagesController.getInstance(this.currentAccount).getInputUser(tL_attachMenuBot.bot_id);
        tL_messages_toggleBotInAttachMenu.enabled = true;
        tL_messages_toggleBotInAttachMenu.write_allowed = true;
        ConnectionsManager.getInstance(this.currentAccount).sendRequest(tL_messages_toggleBotInAttachMenu, new RequestDelegate() {
            @Override
            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                LaunchActivity.this.lambda$onCreate$4(tL_attachMenuBot, tLObject, tL_error);
            }
        }, 66);
    }

    public void lambda$onCreate$6(View view, int i, float f, float f2) {
        BaseFragment mediaActivity;
        int i2;
        BaseFragment chatActivity;
        BaseFragment callLogActivity;
        if (this.drawerLayoutAdapter.click(view, i)) {
            this.drawerLayoutContainer.closeDrawer(false);
            return;
        }
        if (i == 0) {
            DrawerProfileCell drawerProfileCell = (DrawerProfileCell) view;
            if (drawerProfileCell.isInAvatar(f, f2)) {
                openSettings(drawerProfileCell.hasAvatar());
                return;
            } else {
                this.drawerLayoutAdapter.setAccountsShown(!r7.isAccountsShown(), true);
                return;
            }
        }
        if (view instanceof DrawerUserCell) {
            switchToAccount(((DrawerUserCell) view).getAccountNumber(), true);
        } else {
            Integer num = null;
            if (view instanceof DrawerAddCell) {
                int i3 = 0;
                for (int i4 = 3; i4 >= 0; i4--) {
                    if (!UserConfig.getInstance(i4).isClientActivated()) {
                        i3++;
                        if (num == null) {
                            num = Integer.valueOf(i4);
                        }
                    }
                }
                if (!UserConfig.hasPremiumOnAccounts()) {
                    i3--;
                }
                if (i3 <= 0 || num == null) {
                    if (UserConfig.hasPremiumOnAccounts() || this.actionBarLayout.getFragmentStack().size() <= 0) {
                        return;
                    }
                    BaseFragment baseFragment = this.actionBarLayout.getFragmentStack().get(0);
                    LimitReachedBottomSheet limitReachedBottomSheet = new LimitReachedBottomSheet(baseFragment, this, 7, this.currentAccount, null);
                    baseFragment.showDialog(limitReachedBottomSheet);
                    limitReachedBottomSheet.onShowPremiumScreenRunnable = new Runnable() {
                        @Override
                        public final void run() {
                            LaunchActivity.this.lambda$onCreate$2();
                        }
                    };
                    return;
                }
                callLogActivity = new LoginActivity(num.intValue());
            } else {
                int id = this.drawerLayoutAdapter.getId(i);
                final TLRPC.TL_attachMenuBot attachMenuBot = this.drawerLayoutAdapter.getAttachMenuBot(i);
                if (attachMenuBot != null) {
                    if (attachMenuBot.inactive || attachMenuBot.side_menu_disclaimer_needed) {
                        WebAppDisclaimerAlert.show(this, new com.google.android.exoplayer2.util.Consumer() {
                            @Override
                            public final void accept(Object obj) {
                                LaunchActivity.this.lambda$onCreate$5(attachMenuBot, (Boolean) obj);
                            }
                        }, null, null);
                        return;
                    } else {
                        showAttachMenuBot(attachMenuBot, null, true);
                        return;
                    }
                }
                if (id == 2) {
                    chatActivity = new GroupCreateActivity(new Bundle());
                } else if (id == 3) {
                    Bundle bundle = new Bundle();
                    bundle.putBoolean("onlyUsers", true);
                    bundle.putBoolean("destroyAfterSelect", true);
                    bundle.putBoolean("createSecretChat", true);
                    bundle.putBoolean("allowBots", false);
                    bundle.putBoolean("allowSelf", false);
                    chatActivity = new ContactsActivity(bundle);
                } else if (id == 4) {
                    SharedPreferences globalMainSettings = MessagesController.getGlobalMainSettings();
                    if (BuildVars.DEBUG_VERSION || !globalMainSettings.getBoolean("channel_intro", false)) {
                        lambda$runLinkRequest$93(new ActionIntroActivity(0));
                        globalMainSettings.edit().putBoolean("channel_intro", true).commit();
                    } else {
                        Bundle bundle2 = new Bundle();
                        bundle2.putInt("step", 0);
                        chatActivity = new ChannelCreateActivity(bundle2);
                    }
                } else if (id == 6) {
                    Bundle bundle3 = new Bundle();
                    bundle3.putBoolean("needFinishFragment", false);
                    chatActivity = new ContactsActivity(bundle3);
                } else if (id == 7) {
                    callLogActivity = new InviteContactsActivity();
                } else {
                    if (id == 8) {
                        openSettings(false);
                        return;
                    }
                    if (id == 9) {
                        i2 = R.string.TelegramFaqUrl;
                    } else if (id == 10) {
                        callLogActivity = new CallLogActivity();
                    } else if (id == 11) {
                        Bundle bundle4 = new Bundle();
                        bundle4.putLong("user_id", UserConfig.getInstance(this.currentAccount).getClientUserId());
                        chatActivity = new ChatActivity(bundle4);
                    } else {
                        if (id != 13) {
                            if (id == 15) {
                                showSelectStatusDialog();
                                return;
                            }
                            if (id == 16) {
                                this.drawerLayoutContainer.closeDrawer(true);
                                Bundle bundle5 = new Bundle();
                                bundle5.putLong("user_id", UserConfig.getInstance(this.currentAccount).getClientUserId());
                                bundle5.putBoolean("my_profile", true);
                                mediaActivity = new ProfileActivity(bundle5, null);
                            } else {
                                if (id != 17) {
                                    return;
                                }
                                this.drawerLayoutContainer.closeDrawer(true);
                                Bundle bundle6 = new Bundle();
                                bundle6.putLong("dialog_id", UserConfig.getInstance(this.currentAccount).getClientUserId());
                                bundle6.putInt("type", 1);
                                mediaActivity = new MediaActivity(bundle6, null);
                            }
                            lambda$runLinkRequest$93(mediaActivity);
                            return;
                        }
                        i2 = R.string.TelegramFeaturesUrl;
                    }
                    Browser.openUrl(this, LocaleController.getString(i2));
                }
                lambda$runLinkRequest$93(chatActivity);
            }
            lambda$runLinkRequest$93(callLogActivity);
        }
        this.drawerLayoutContainer.closeDrawer(false);
    }

    public boolean lambda$onCreate$7(ItemTouchHelper itemTouchHelper, View view, int i) {
        if (view instanceof DrawerUserCell) {
            int accountNumber = ((DrawerUserCell) view).getAccountNumber();
            if (accountNumber != this.currentAccount && !AndroidUtilities.isTablet()) {
                AnonymousClass8 anonymousClass8 = new DialogsActivity(null) {
                    final int val$accountNumber;

                    AnonymousClass8(Bundle bundle, int accountNumber2) {
                        super(bundle);
                        r3 = accountNumber2;
                    }

                    @Override
                    public void onPreviewOpenAnimationEnd() {
                        super.onPreviewOpenAnimationEnd();
                        LaunchActivity.this.drawerLayoutContainer.setAllowOpenDrawer(false, false);
                        LaunchActivity.this.drawerLayoutContainer.setDrawCurrentPreviewFragmentAbove(false);
                        LaunchActivity.this.switchToAccount(r3, true);
                        LaunchActivity.this.actionBarLayout.getView().invalidate();
                    }

                    @Override
                    public void onTransitionAnimationEnd(boolean z, boolean z2) {
                        super.onTransitionAnimationEnd(z, z2);
                        if (z || !z2) {
                            return;
                        }
                        LaunchActivity.this.drawerLayoutContainer.setDrawCurrentPreviewFragmentAbove(false);
                        LaunchActivity.this.actionBarLayout.getView().invalidate();
                    }
                };
                anonymousClass8.setCurrentAccount(accountNumber2);
                this.actionBarLayout.presentFragmentAsPreview(anonymousClass8);
                this.drawerLayoutContainer.setDrawCurrentPreviewFragmentAbove(true);
                return true;
            }
            itemTouchHelper.startDrag(this.sideMenu.getChildViewHolder(view));
        }
        if (!(view instanceof DrawerActionCell)) {
            return false;
        }
        this.drawerLayoutAdapter.getId(i);
        TLRPC.TL_attachMenuBot attachMenuBot = this.drawerLayoutAdapter.getAttachMenuBot(i);
        if (attachMenuBot == null) {
            return false;
        }
        BotWebViewSheet.deleteBot(this.currentAccount, attachMenuBot.bot_id, null);
        return true;
    }

    public void lambda$onCreate$8() {
        checkSystemBarColors(true, false);
        if (getLastFragment() == null || getLastFragment().getLastStoryViewer() == null) {
            return;
        }
        getLastFragment().getLastStoryViewer().updatePlayingMode();
    }

    public static void lambda$onCreate$9(View view) {
        int measuredHeight = view.getMeasuredHeight();
        FileLog.d("height = " + measuredHeight + " displayHeight = " + AndroidUtilities.displaySize.y);
        if (Build.VERSION.SDK_INT >= 21) {
            measuredHeight -= AndroidUtilities.statusBarHeight;
        }
        if (measuredHeight <= AndroidUtilities.dp(100.0f) || measuredHeight >= AndroidUtilities.displaySize.y) {
            return;
        }
        int dp = AndroidUtilities.dp(100.0f) + measuredHeight;
        Point point = AndroidUtilities.displaySize;
        if (dp > point.y) {
            point.y = measuredHeight;
            if (BuildVars.LOGS_ENABLED) {
                FileLog.d("fix display size y to " + AndroidUtilities.displaySize.y);
            }
        }
    }

    public static void lambda$onPause$138(int i) {
        ApplicationLoader.mainInterfacePausedStageQueue = true;
        ApplicationLoader.mainInterfacePausedStageQueueTime = 0L;
        if (VoIPService.getSharedInstance() == null) {
            MessagesController.getInstance(i).ignoreSetOnline = false;
        }
    }

    public void lambda$onPowerSaver$136() {
        lambda$runLinkRequest$93(new LiteModeSettingsActivity());
    }

    public static void lambda$onResume$139() {
        ApplicationLoader.mainInterfacePausedStageQueue = false;
        ApplicationLoader.mainInterfacePausedStageQueueTime = System.currentTimeMillis();
    }

    public void lambda$openForumFromLink$126(TLObject tLObject, Integer num, long j, Runnable runnable, String str, int i, int i2) {
        TLRPC.Message message;
        if (tLObject instanceof TLRPC.messages_Messages) {
            ArrayList<TLRPC.Message> arrayList = ((TLRPC.messages_Messages) tLObject).messages;
            for (int i3 = 0; i3 < arrayList.size(); i3++) {
                if (arrayList.get(i3) != null && arrayList.get(i3).id == num.intValue()) {
                    message = arrayList.get(i3);
                    break;
                }
            }
        }
        message = null;
        if (message != null) {
            int i4 = this.currentAccount;
            Integer valueOf = Integer.valueOf(message.id);
            int i5 = this.currentAccount;
            runCommentRequest(i4, null, valueOf, null, Long.valueOf(MessageObject.getTopicId(i5, message, MessagesController.getInstance(i5).isForum(message))), MessagesController.getInstance(this.currentAccount).getChat(Long.valueOf(-j)), runnable, str, i, i2);
            return;
        }
        Bundle bundle = new Bundle();
        bundle.putLong("chat_id", -j);
        lambda$runLinkRequest$93(TopicsFragment.getTopicsOrChat(this, bundle));
        if (runnable != null) {
            runnable.run();
        }
    }

    public void lambda$openForumFromLink$127(final Integer num, final long j, final Runnable runnable, final String str, final int i, final int i2, final TLObject tLObject, TLRPC.TL_error tL_error) {
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                LaunchActivity.this.lambda$openForumFromLink$126(tLObject, num, j, runnable, str, i, i2);
            }
        });
    }

    public static void lambda$openMessage$39(Browser.Progress progress) {
        if (progress != null) {
            progress.end();
        }
    }

    public static void lambda$openMessage$40(Browser.Progress progress) {
        if (progress != null) {
            progress.end();
        }
    }

    public void lambda$openMessage$41(Browser.Progress progress, TLObject tLObject, long j, int i, BaseFragment baseFragment, Bundle bundle, ChatActivity chatActivity, String str, int i2) {
        if (progress != null) {
            progress.end();
        }
        if (tLObject instanceof TLRPC.TL_messages_chats) {
            TLRPC.TL_messages_chats tL_messages_chats = (TLRPC.TL_messages_chats) tLObject;
            if (!tL_messages_chats.chats.isEmpty()) {
                MessagesController.getInstance(this.currentAccount).putChats(tL_messages_chats.chats, false);
                TLRPC.Chat chat = tL_messages_chats.chats.get(0);
                if (chat != null && chat.forum) {
                    openForumFromLink(-j, Integer.valueOf(i), null);
                }
                if (baseFragment == null || MessagesController.getInstance(this.currentAccount).checkCanOpenChat(bundle, baseFragment)) {
                    ChatActivity chatActivity2 = new ChatActivity(bundle);
                    chatActivity.setHighlightQuote(i, str, i2);
                    getActionBarLayout().presentFragment(chatActivity2);
                    return;
                }
                return;
            }
        }
        showAlertDialog(AlertsCreator.createNoAccessAlert(this, LocaleController.getString(R.string.DialogNotAvailable), LocaleController.getString(R.string.LinkNotFound), null));
    }

    public void lambda$openMessage$42(final Browser.Progress progress, final long j, final int i, final BaseFragment baseFragment, final Bundle bundle, final ChatActivity chatActivity, final String str, final int i2, final TLObject tLObject, TLRPC.TL_error tL_error) {
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                LaunchActivity.this.lambda$openMessage$41(progress, tLObject, j, i, baseFragment, bundle, chatActivity, str, i2);
            }
        });
    }

    public void lambda$openMessage$43(int i) {
        ConnectionsManager.getInstance(this.currentAccount).cancelRequest(i, true);
    }

    public void lambda$openMessage$44(final Bundle bundle, final int i, final String str, final int i2, final long j, final Browser.Progress progress, final BaseFragment baseFragment) {
        final ChatActivity chatActivity = new ChatActivity(bundle);
        chatActivity.setHighlightQuote(i, str, i2);
        if ((AndroidUtilities.isTablet() ? this.rightActionBarLayout : getActionBarLayout()).presentFragment(chatActivity) || j >= 0) {
            if (progress != null) {
                progress.end();
                return;
            }
            return;
        }
        TLRPC.TL_channels_getChannels tL_channels_getChannels = new TLRPC.TL_channels_getChannels();
        TLRPC.TL_inputChannel tL_inputChannel = new TLRPC.TL_inputChannel();
        tL_inputChannel.channel_id = -j;
        tL_channels_getChannels.id.add(tL_inputChannel);
        final int sendRequest = ConnectionsManager.getInstance(this.currentAccount).sendRequest(tL_channels_getChannels, new RequestDelegate() {
            @Override
            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                LaunchActivity.this.lambda$openMessage$42(progress, j, i, baseFragment, bundle, chatActivity, str, i2, tLObject, tL_error);
            }
        });
        if (progress != null) {
            progress.onCancel(new Runnable() {
                @Override
                public final void run() {
                    LaunchActivity.this.lambda$openMessage$43(sendRequest);
                }
            });
        }
    }

    public void lambda$openMyStory$165(TLObject tLObject, int i, long j, boolean z) {
        StoriesListPlaceProvider storiesListPlaceProvider;
        TL_stories.StoryItem storyItem;
        if (tLObject instanceof TL_stories.TL_stories_stories) {
            TL_stories.TL_stories_stories tL_stories_stories = (TL_stories.TL_stories_stories) tLObject;
            int i2 = 0;
            while (true) {
                storiesListPlaceProvider = null;
                if (i2 >= tL_stories_stories.stories.size()) {
                    storyItem = null;
                    break;
                } else {
                    if (tL_stories_stories.stories.get(i2).id == i) {
                        storyItem = tL_stories_stories.stories.get(i2);
                        break;
                    }
                    i2++;
                }
            }
            if (storyItem != null) {
                storyItem.dialogId = j;
                BaseFragment lastFragment = getLastFragment();
                if (lastFragment == null) {
                    return;
                }
                if (lastFragment instanceof DialogsActivity) {
                    try {
                        storiesListPlaceProvider = StoriesListPlaceProvider.of(((DialogsActivity) lastFragment).dialogStoriesCell.recyclerListView);
                    } catch (Exception unused) {
                    }
                }
                StoriesListPlaceProvider storiesListPlaceProvider2 = storiesListPlaceProvider;
                lastFragment.getOrCreateStoryViewer().instantClose();
                ArrayList arrayList = new ArrayList();
                arrayList.add(Long.valueOf(j));
                if (z) {
                    lastFragment.getOrCreateStoryViewer().showViewsAfterOpening();
                }
                lastFragment.getOrCreateStoryViewer().open(this, storyItem, arrayList, 0, null, null, storiesListPlaceProvider2, false);
                return;
            }
        }
        BulletinFactory.global().createSimpleBulletin(R.raw.error, LocaleController.getString(R.string.StoryNotFound)).show(false);
    }

    public void lambda$openMyStory$166(final int i, final long j, final boolean z, final TLObject tLObject, TLRPC.TL_error tL_error) {
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                LaunchActivity.this.lambda$openMyStory$165(tLObject, i, j, z);
            }
        });
    }

    public void lambda$openStories$167(int[] iArr, long[] jArr) {
        int i = iArr[0] - 1;
        iArr[0] = i;
        if (i == 0) {
            NotificationCenter.getInstance(this.currentAccount).lambda$postNotificationNameOnUIThread$1(NotificationCenter.storiesUpdated, new Object[0]);
            openStories(jArr, false);
        }
    }

    public static void lambda$openStories$168(TLObject tLObject, MessagesController messagesController, long j, Runnable runnable) {
        if (tLObject instanceof TL_stories.TL_stories_peerStories) {
            TL_stories.TL_stories_peerStories tL_stories_peerStories = (TL_stories.TL_stories_peerStories) tLObject;
            messagesController.putUsers(tL_stories_peerStories.users, false);
            messagesController.getStoriesController().putStories(j, tL_stories_peerStories.stories);
        }
        runnable.run();
    }

    public static void lambda$openStories$169(final MessagesController messagesController, final long j, final Runnable runnable, final TLObject tLObject, TLRPC.TL_error tL_error) {
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                LaunchActivity.lambda$openStories$168(TLObject.this, messagesController, j, runnable);
            }
        });
    }

    public void lambda$openTopicRequest$34(TLRPC.TL_error tL_error, TLObject tLObject, int i, TLRPC.Chat chat, int i2, int i3, Runnable runnable, String str, int i4, ArrayList arrayList, int i5) {
        if (tL_error == null) {
            TLRPC.TL_messages_forumTopics tL_messages_forumTopics = (TLRPC.TL_messages_forumTopics) tLObject;
            SparseArray<TLRPC.Message> sparseArray = new SparseArray<>();
            for (int i6 = 0; i6 < tL_messages_forumTopics.messages.size(); i6++) {
                sparseArray.put(tL_messages_forumTopics.messages.get(i6).id, tL_messages_forumTopics.messages.get(i6));
            }
            MessagesController.getInstance(i).putUsers(tL_messages_forumTopics.users, false);
            MessagesController.getInstance(i).putChats(tL_messages_forumTopics.chats, false);
            MessagesController.getInstance(i).getTopicsController().processTopics(chat.id, tL_messages_forumTopics.topics, sparseArray, false, 2, -1);
            openTopicRequest(i, i2, chat, i3, MessagesController.getInstance(i).getTopicsController().findTopic(chat.id, i2), runnable, str, i4, arrayList, i5);
        }
    }

    public void lambda$openTopicRequest$35(final int i, final TLRPC.Chat chat, final int i2, final int i3, final Runnable runnable, final String str, final int i4, final ArrayList arrayList, final int i5, final TLObject tLObject, final TLRPC.TL_error tL_error) {
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                LaunchActivity.this.lambda$openTopicRequest$34(tL_error, tLObject, i, chat, i2, i3, runnable, str, i4, arrayList, i5);
            }
        });
    }

    public boolean lambda$processAttachMenuBot$119(TLRPC.User user, String str, int i, DialogsActivity dialogsActivity, ArrayList arrayList, CharSequence charSequence, boolean z, boolean z2, int i2, TopicsFragment topicsFragment) {
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
        bundle.putString("attach_bot", UserObject.getPublicUsername(user));
        if (str != null) {
            bundle.putString("attach_bot_start_command", str);
        }
        if (MessagesController.getInstance(i).checkCanOpenChat(bundle, dialogsActivity)) {
            NotificationCenter.getInstance(i).lambda$postNotificationNameOnUIThread$1(NotificationCenter.closeChats, new Object[0]);
            getActionBarLayout().presentFragment(new ChatActivity(bundle), true, false, true, false);
        }
        return true;
    }

    public void lambda$processAttachMenuBot$120(DialogsActivity dialogsActivity, BaseFragment baseFragment, TLRPC.User user, String str) {
        if (dialogsActivity == null) {
            if (baseFragment instanceof ChatActivity) {
                ((ChatActivity) baseFragment).openAttachBotLayout(user.id, str, true);
                return;
            }
            return;
        }
        if (baseFragment != null) {
            baseFragment.dismissCurrentDialog();
        }
        for (int i = 0; i < this.visibleDialogs.size(); i++) {
            if (((Dialog) this.visibleDialogs.get(i)).isShowing()) {
                ((Dialog) this.visibleDialogs.get(i)).dismiss();
            }
        }
        this.visibleDialogs.clear();
        lambda$runLinkRequest$93(dialogsActivity);
    }

    public void lambda$processAttachMenuBot$121(TLObject tLObject, int i, final DialogsActivity dialogsActivity, final BaseFragment baseFragment, final TLRPC.User user, final String str) {
        if (tLObject instanceof TLRPC.TL_boolTrue) {
            MediaDataController.getInstance(i).loadAttachMenuBots(false, true, new Runnable() {
                @Override
                public final void run() {
                    LaunchActivity.this.lambda$processAttachMenuBot$120(dialogsActivity, baseFragment, user, str);
                }
            });
        }
    }

    public void lambda$processAttachMenuBot$122(final int i, final DialogsActivity dialogsActivity, final BaseFragment baseFragment, final TLRPC.User user, final String str, final TLObject tLObject, TLRPC.TL_error tL_error) {
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                LaunchActivity.this.lambda$processAttachMenuBot$121(tLObject, i, dialogsActivity, baseFragment, user, str);
            }
        });
    }

    public void lambda$processAttachMenuBot$123(final int i, long j, final DialogsActivity dialogsActivity, final BaseFragment baseFragment, final TLRPC.User user, final String str, Boolean bool) {
        TLRPC.TL_messages_toggleBotInAttachMenu tL_messages_toggleBotInAttachMenu = new TLRPC.TL_messages_toggleBotInAttachMenu();
        tL_messages_toggleBotInAttachMenu.bot = MessagesController.getInstance(i).getInputUser(j);
        tL_messages_toggleBotInAttachMenu.enabled = true;
        tL_messages_toggleBotInAttachMenu.write_allowed = true;
        ConnectionsManager.getInstance(i).sendRequest(tL_messages_toggleBotInAttachMenu, new RequestDelegate() {
            @Override
            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                LaunchActivity.this.lambda$processAttachMenuBot$122(i, dialogsActivity, baseFragment, user, str, tLObject, tL_error);
            }
        }, 66);
    }

    public void lambda$processAttachMenuBot$124(TLObject tLObject, final int i, String str, String str2, final TLRPC.User user, final String str3, final long j) {
        BulletinFactory of;
        int i2;
        DialogsActivity dialogsActivity;
        final TLRPC.User user2 = user;
        if (tLObject instanceof TLRPC.TL_attachMenuBotsBot) {
            TLRPC.TL_attachMenuBotsBot tL_attachMenuBotsBot = (TLRPC.TL_attachMenuBotsBot) tLObject;
            MessagesController.getInstance(i).putUsers(tL_attachMenuBotsBot.users, false);
            TLRPC.TL_attachMenuBot tL_attachMenuBot = tL_attachMenuBotsBot.bot;
            if (str != null) {
                showAttachMenuBot(tL_attachMenuBot, str, false);
                return;
            }
            ArrayList arrayList = mainFragmentsStack;
            BaseFragment baseFragment = (BaseFragment) arrayList.get(arrayList.size() - 1);
            if (AndroidUtilities.isTablet() && !(baseFragment instanceof ChatActivity)) {
                ArrayList arrayList2 = rightFragmentsStack;
                if (!arrayList2.isEmpty()) {
                    baseFragment = (BaseFragment) arrayList2.get(arrayList2.size() - 1);
                }
            }
            final BaseFragment baseFragment2 = baseFragment;
            ArrayList arrayList3 = new ArrayList();
            if (!TextUtils.isEmpty(str2)) {
                for (String str4 : str2.split(" ")) {
                    if (MediaDataController.canShowAttachMenuBotForTarget(tL_attachMenuBot, str4)) {
                        arrayList3.add(str4);
                    }
                }
            }
            if (arrayList3.isEmpty()) {
                dialogsActivity = null;
            } else {
                Bundle bundle = new Bundle();
                bundle.putInt("dialogsType", 14);
                bundle.putBoolean("onlySelect", true);
                bundle.putBoolean("allowGroups", arrayList3.contains("groups"));
                bundle.putBoolean("allowMegagroups", arrayList3.contains("groups"));
                bundle.putBoolean("allowLegacyGroups", arrayList3.contains("groups"));
                bundle.putBoolean("allowUsers", arrayList3.contains("users"));
                bundle.putBoolean("allowChannels", arrayList3.contains("channels"));
                bundle.putBoolean("allowBots", arrayList3.contains("bots"));
                DialogsActivity dialogsActivity2 = new DialogsActivity(bundle);
                dialogsActivity2.setDelegate(new DialogsActivity.DialogsActivityDelegate() {
                    @Override
                    public final boolean didSelectDialogs(DialogsActivity dialogsActivity3, ArrayList arrayList4, CharSequence charSequence, boolean z, boolean z2, int i3, TopicsFragment topicsFragment) {
                        boolean lambda$processAttachMenuBot$119;
                        lambda$processAttachMenuBot$119 = LaunchActivity.this.lambda$processAttachMenuBot$119(user2, str3, i, dialogsActivity3, arrayList4, charSequence, z, z2, i3, topicsFragment);
                        return lambda$processAttachMenuBot$119;
                    }
                });
                dialogsActivity = dialogsActivity2;
            }
            if (tL_attachMenuBot.inactive) {
                AttachBotIntroTopView attachBotIntroTopView = new AttachBotIntroTopView(this);
                attachBotIntroTopView.setColor(Theme.getColor(Theme.key_chat_attachIcon));
                attachBotIntroTopView.setBackgroundColor(Theme.getColor(Theme.key_dialogTopBackground));
                attachBotIntroTopView.setAttachBot(tL_attachMenuBot);
                final DialogsActivity dialogsActivity3 = dialogsActivity;
                com.google.android.exoplayer2.util.Consumer consumer = new com.google.android.exoplayer2.util.Consumer() {
                    @Override
                    public final void accept(Object obj) {
                        LaunchActivity.this.lambda$processAttachMenuBot$123(i, j, dialogsActivity3, baseFragment2, user, str3, (Boolean) obj);
                    }
                };
                if (!tL_attachMenuBot.request_write_access) {
                    user2 = null;
                }
                WebAppDisclaimerAlert.show(this, consumer, user2, null);
                return;
            }
            if (dialogsActivity != null) {
                if (baseFragment2 != null) {
                    baseFragment2.dismissCurrentDialog();
                }
                for (int i3 = 0; i3 < this.visibleDialogs.size(); i3++) {
                    if (((Dialog) this.visibleDialogs.get(i3)).isShowing()) {
                        ((Dialog) this.visibleDialogs.get(i3)).dismiss();
                    }
                }
                this.visibleDialogs.clear();
                lambda$runLinkRequest$93(dialogsActivity);
                return;
            }
            if (baseFragment2 instanceof ChatActivity) {
                ChatActivity chatActivity = (ChatActivity) baseFragment2;
                if (MediaDataController.canShowAttachMenuBot(tL_attachMenuBot, chatActivity.getCurrentUser() != null ? chatActivity.getCurrentUser() : chatActivity.getCurrentChat())) {
                    chatActivity.openAttachBotLayout(user2.id, str3, false);
                    return;
                } else {
                    BulletinFactory.of(baseFragment2).createErrorBulletin(LocaleController.getString(R.string.BotAlreadyAddedToAttachMenu)).show();
                    return;
                }
            }
            of = BulletinFactory.of(baseFragment2);
            i2 = R.string.BotAlreadyAddedToAttachMenu;
        } else {
            ArrayList arrayList4 = mainFragmentsStack;
            of = BulletinFactory.of((BaseFragment) arrayList4.get(arrayList4.size() - 1));
            i2 = R.string.BotCantAddToAttachMenu;
        }
        of.createErrorBulletin(LocaleController.getString(i2)).show();
    }

    public void lambda$processAttachMenuBot$125(final int i, final String str, final String str2, final TLRPC.User user, final String str3, final long j, final TLObject tLObject, TLRPC.TL_error tL_error) {
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                LaunchActivity.this.lambda$processAttachMenuBot$124(tLObject, i, str, str2, user, str3, j);
            }
        });
    }

    public void lambda$processAttachedMenuBotFromShortcut$114(TLRPC.User user) {
        MessagesController.getInstance(this.currentAccount).openApp(user, 0);
    }

    public void lambda$processAttachedMenuBotFromShortcut$115(TLRPC.User user, Utilities.Callback callback) {
        MessagesController.getInstance(this.currentAccount).putUser(user, true);
        callback.run(user);
    }

    public void lambda$processAttachedMenuBotFromShortcut$116(long j, final Utilities.Callback callback) {
        final TLRPC.User user = MessagesStorage.getInstance(this.currentAccount).getUser(j);
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                LaunchActivity.this.lambda$processAttachedMenuBotFromShortcut$115(user, callback);
            }
        });
    }

    public void lambda$processBoostDialog$117(Browser.Progress progress, Long l, TL_stories.TL_premium_boostsStatus tL_premium_boostsStatus, ChatMessageCell chatMessageCell, Runnable runnable, ChannelBoostsController.CanApplyBoost canApplyBoost) {
        RightSlidingDialogContainer rightSlidingDialogContainer;
        if (progress != null) {
            progress.end();
        }
        BaseFragment lastFragment = getLastFragment();
        if (lastFragment == null) {
            return;
        }
        Theme.ResourcesProvider resourceProvider = lastFragment.getResourceProvider();
        if (lastFragment.getLastStoryViewer() != null && lastFragment.getLastStoryViewer().isFullyVisible()) {
            resourceProvider = lastFragment.getLastStoryViewer().getResourceProvider();
        }
        LimitReachedBottomSheet limitReachedBottomSheet = new LimitReachedBottomSheet(lastFragment, this, 19, this.currentAccount, resourceProvider);
        limitReachedBottomSheet.setCanApplyBoost(canApplyBoost);
        boolean z = false;
        if (!(lastFragment instanceof ChatActivity) ? !(!(lastFragment instanceof DialogsActivity) || (rightSlidingDialogContainer = ((DialogsActivity) lastFragment).rightSlidingDialogContainer) == null || rightSlidingDialogContainer.getCurrentFragmetDialogId() != l.longValue()) : ((ChatActivity) lastFragment).getDialogId() == l.longValue()) {
            z = true;
        }
        limitReachedBottomSheet.setBoostsStats(tL_premium_boostsStatus, z);
        limitReachedBottomSheet.setDialogId(l.longValue());
        limitReachedBottomSheet.setChatMessageCell(chatMessageCell);
        lastFragment.showDialog(limitReachedBottomSheet);
        if (runnable != null) {
            try {
                runnable.run();
            } catch (Exception e) {
                FileLog.e(e);
            }
        }
    }

    public void lambda$processBoostDialog$118(final Browser.Progress progress, final Runnable runnable, ChannelBoostsController channelBoostsController, final Long l, final ChatMessageCell chatMessageCell, final TL_stories.TL_premium_boostsStatus tL_premium_boostsStatus) {
        if (tL_premium_boostsStatus != null) {
            channelBoostsController.userCanBoostChannel(l.longValue(), tL_premium_boostsStatus, new com.google.android.exoplayer2.util.Consumer() {
                @Override
                public final void accept(Object obj) {
                    LaunchActivity.this.lambda$processBoostDialog$117(progress, l, tL_premium_boostsStatus, chatMessageCell, runnable, (ChannelBoostsController.CanApplyBoost) obj);
                }
            });
            return;
        }
        if (progress != null) {
            progress.end();
        }
        if (runnable != null) {
            runnable.run();
        }
    }

    public void lambda$processWebAppBot$109(int i, String str, String str2, String str3, String str4, String str5, String str6, String str7, String str8, String str9, String str10, String str11, String str12, boolean z, Integer num, Long l, Long l2, Integer num2, String str13, HashMap hashMap, String str14, String str15, String str16, String str17, TLRPC.TL_wallPaper tL_wallPaper, String str18, String str19, String str20, boolean z2, String str21, int i2, int i3, String str22, String str23, String str24, Browser.Progress progress, boolean z3, int i4, boolean z4, String str25, boolean z5, boolean z6, boolean z7, boolean z8, boolean z9, String str26) {
        runLinkRequest(i, str, str2, str3, str4, str5, str6, str7, str8, str9, str10, str11, str12, z, num, l, l2, num2, str13, hashMap, str14, str15, str16, str17, tL_wallPaper, str18, str19, str20, z2, str21, i2, i3, str22, str23, str24, null, null, progress, z3, i4, z4, str25, z5, z6, z7, z8, z9, str26);
    }

    public void lambda$processWebAppBot$110(BaseFragment baseFragment, int i, TLRPC.User user, TLRPC.TL_messages_botApp tL_messages_botApp, AtomicBoolean atomicBoolean, String str, boolean z, boolean z2, boolean z3, boolean z4) {
        if (baseFragment == null || !isActive || isFinishing() || isDestroyed()) {
            return;
        }
        long j = user.id;
        WebViewRequestProps of = WebViewRequestProps.of(i, j, j, null, null, 3, 0, false, tL_messages_botApp.app, atomicBoolean.get(), str, user, 0, z, z2);
        if (getBottomSheetTabs() == null || getBottomSheetTabs().tryReopenTab(of) == null) {
            SharedPrefsHelper.setWebViewConfirmShown(this.currentAccount, user.id, true);
            BotWebViewSheet botWebViewSheet = new BotWebViewSheet(this, baseFragment.getResourceProvider());
            botWebViewSheet.setWasOpenedByLinkIntent(z3);
            botWebViewSheet.setDefaultFullsize(!z);
            if (z2) {
                botWebViewSheet.setFullscreen(true, false);
            }
            botWebViewSheet.setNeedsContext(false);
            botWebViewSheet.setParentActivity(this);
            botWebViewSheet.requestWebView(baseFragment, of);
            botWebViewSheet.show();
            if (tL_messages_botApp.inactive || z4) {
                botWebViewSheet.showJustAddedBulletin();
            }
        }
    }

    public void lambda$processWebAppBot$112(Runnable runnable, final int i, final TLRPC.User user, final TLRPC.TL_messages_botApp tL_messages_botApp, final String str, final boolean z, final boolean z2, final boolean z3, final boolean z4, boolean z5, boolean z6, Browser.Progress progress) {
        runnable.run();
        final AtomicBoolean atomicBoolean = new AtomicBoolean();
        ArrayList arrayList = mainFragmentsStack;
        BaseFragment baseFragment = (arrayList == null || arrayList.isEmpty()) ? null : (BaseFragment) arrayList.get(arrayList.size() - 1);
        final BaseFragment baseFragment2 = baseFragment;
        BaseFragment baseFragment3 = baseFragment;
        final Runnable runnable2 = new Runnable() {
            @Override
            public final void run() {
                LaunchActivity.this.lambda$processWebAppBot$110(baseFragment2, i, user, tL_messages_botApp, atomicBoolean, str, z, z2, z3, z4);
            }
        };
        if (!z5) {
            if (tL_messages_botApp.inactive && z6) {
                WebAppDisclaimerAlert.show(this, new com.google.android.exoplayer2.util.Consumer() {
                    @Override
                    public final void accept(Object obj) {
                        runnable2.run();
                    }
                }, null, progress != null ? new ChatActivity$ChatMessageCellDelegate$$ExternalSyntheticLambda4(progress) : null);
                return;
            } else if (tL_messages_botApp.request_write_access || z4) {
                AlertsCreator.createBotLaunchAlert(baseFragment3, atomicBoolean, user, runnable2);
                return;
            }
        }
        runnable2.run();
    }

    public void lambda$processWebAppBot$113(final Browser.Progress progress, final int i, final String str, final String str2, final String str3, final String str4, final String str5, final String str6, final String str7, final String str8, final String str9, final String str10, final String str11, final String str12, final boolean z, final Integer num, final Long l, final Long l2, final Integer num2, final String str13, final HashMap hashMap, final String str14, final String str15, final String str16, final String str17, final TLRPC.TL_wallPaper tL_wallPaper, final String str18, final String str19, final String str20, final boolean z2, final String str21, final int i2, final int i3, final String str22, final String str23, final String str24, final boolean z3, final int i4, final boolean z4, final String str25, final boolean z5, final boolean z6, final boolean z7, final boolean z8, final boolean z9, final String str26, final Runnable runnable, final TLRPC.User user, final String str27, final boolean z10, final boolean z11, TLObject tLObject, TLRPC.TL_error tL_error) {
        if (progress != null) {
            progress.end();
        }
        if (tL_error != null) {
            AndroidUtilities.runOnUIThread(new Runnable() {
                @Override
                public final void run() {
                    LaunchActivity.this.lambda$processWebAppBot$109(i, str, str2, str3, str4, str5, str6, str7, str8, str9, str10, str11, str12, z, num, l, l2, num2, str13, hashMap, str14, str15, str16, str17, tL_wallPaper, str18, str19, str20, z2, str21, i2, i3, str22, str23, str24, progress, z3, i4, z4, str25, z5, z6, z7, z8, z9, str26);
                }
            });
        } else {
            final TLRPC.TL_messages_botApp tL_messages_botApp = (TLRPC.TL_messages_botApp) tLObject;
            AndroidUtilities.runOnUIThread(new Runnable() {
                @Override
                public final void run() {
                    LaunchActivity.this.lambda$processWebAppBot$112(runnable, i, user, tL_messages_botApp, str27, z5, z6, z7, z3, z10, z11, progress);
                }
            });
        }
    }

    public void lambda$runCommentRequest$32(org.telegram.tgnet.TLObject r16, int r17, org.telegram.tgnet.TLRPC.Chat r18, java.lang.Long r19, java.lang.Integer r20, java.lang.Integer r21, java.lang.Runnable r22, java.lang.String r23, int r24, int r25, org.telegram.tgnet.TLRPC.TL_messages_getDiscussionMessage r26, java.lang.Runnable r27) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.LaunchActivity.lambda$runCommentRequest$32(org.telegram.tgnet.TLObject, int, org.telegram.tgnet.TLRPC$Chat, java.lang.Long, java.lang.Integer, java.lang.Integer, java.lang.Runnable, java.lang.String, int, int, org.telegram.tgnet.TLRPC$TL_messages_getDiscussionMessage, java.lang.Runnable):void");
    }

    public void lambda$runCommentRequest$33(final int i, final TLRPC.Chat chat, final Long l, final Integer num, final Integer num2, final Runnable runnable, final String str, final int i2, final int i3, final TLRPC.TL_messages_getDiscussionMessage tL_messages_getDiscussionMessage, final Runnable runnable2, final TLObject tLObject, TLRPC.TL_error tL_error) {
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                LaunchActivity.this.lambda$runCommentRequest$32(tLObject, i, chat, l, num, num2, runnable, str, i2, i3, tL_messages_getDiscussionMessage, runnable2);
            }
        });
    }

    public void lambda$runImportRequest$36(TLObject tLObject, Uri uri, int i, AlertDialog alertDialog) {
        boolean z;
        if (isFinishing()) {
            return;
        }
        if (tLObject == null || this.actionBarLayout == null) {
            if (this.documentsUrisArray == null) {
                this.documentsUrisArray = new ArrayList();
            }
            this.documentsUrisArray.add(0, this.exportingChatUri);
            this.exportingChatUri = null;
            openDialogsToSend(true);
        } else {
            TLRPC.TL_messages_historyImportParsed tL_messages_historyImportParsed = (TLRPC.TL_messages_historyImportParsed) tLObject;
            Bundle bundle = new Bundle();
            bundle.putBoolean("onlySelect", true);
            bundle.putString("importTitle", tL_messages_historyImportParsed.title);
            bundle.putBoolean("allowSwitchAccount", true);
            if (tL_messages_historyImportParsed.pm) {
                bundle.putInt("dialogsType", 12);
            } else if (tL_messages_historyImportParsed.group) {
                bundle.putInt("dialogsType", 11);
            } else {
                String uri2 = uri.toString();
                Iterator<String> it = MessagesController.getInstance(i).exportPrivateUri.iterator();
                while (true) {
                    if (!it.hasNext()) {
                        z = false;
                        break;
                    } else if (uri2.contains(it.next())) {
                        bundle.putInt("dialogsType", 12);
                        z = true;
                        break;
                    }
                }
                if (!z) {
                    Iterator<String> it2 = MessagesController.getInstance(i).exportGroupUri.iterator();
                    while (true) {
                        if (!it2.hasNext()) {
                            break;
                        }
                        if (uri2.contains(it2.next())) {
                            bundle.putInt("dialogsType", 11);
                            z = true;
                            break;
                        }
                    }
                    if (!z) {
                        bundle.putInt("dialogsType", 13);
                    }
                }
            }
            if (SecretMediaViewer.hasInstance() && SecretMediaViewer.getInstance().isVisible()) {
                SecretMediaViewer.getInstance().closePhoto(false, false);
            } else if (PhotoViewer.hasInstance() && PhotoViewer.getInstance().isVisible()) {
                PhotoViewer.getInstance().closePhoto(false, true);
            } else if (ArticleViewer.hasInstance() && ArticleViewer.getInstance().isVisible()) {
                ArticleViewer.getInstance().close(false, true);
            }
            StoryRecorder.destroyInstance();
            GroupCallActivity groupCallActivity = GroupCallActivity.groupCallInstance;
            if (groupCallActivity != null) {
                groupCallActivity.dismiss();
            }
            this.drawerLayoutContainer.setAllowOpenDrawer(false, false);
            if (AndroidUtilities.isTablet()) {
                this.actionBarLayout.rebuildFragments(1);
                this.rightActionBarLayout.rebuildFragments(1);
            } else {
                this.drawerLayoutContainer.setAllowOpenDrawer(true, false);
            }
            DialogsActivity dialogsActivity = new DialogsActivity(bundle);
            dialogsActivity.setDelegate(this);
            getActionBarLayout().presentFragment(dialogsActivity, !AndroidUtilities.isTablet() ? this.actionBarLayout.getFragmentStack().size() <= 1 || !(this.actionBarLayout.getFragmentStack().get(this.actionBarLayout.getFragmentStack().size() - 1) instanceof DialogsActivity) : this.layersActionBarLayout.getFragmentStack().size() <= 0 || !(this.layersActionBarLayout.getFragmentStack().get(this.layersActionBarLayout.getFragmentStack().size() - 1) instanceof DialogsActivity), false, true, false);
        }
        try {
            alertDialog.dismiss();
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    public void lambda$runImportRequest$37(final Uri uri, final int i, final AlertDialog alertDialog, final TLObject tLObject, TLRPC.TL_error tL_error) {
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                LaunchActivity.this.lambda$runImportRequest$36(tLObject, uri, i, alertDialog);
            }
        }, 2L);
    }

    public static void lambda$runImportRequest$38(int i, int[] iArr, Runnable runnable, DialogInterface dialogInterface) {
        ConnectionsManager.getInstance(i).cancelRequest(iArr[0], true);
        if (runnable != null) {
            runnable.run();
        }
    }

    public void lambda$runLinkRequest$100(final int[] iArr, final int i, final Runnable runnable, final Integer num, final Integer num2, final Long l, final TLObject tLObject, TLRPC.TL_error tL_error) {
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                LaunchActivity.this.lambda$runLinkRequest$99(tLObject, iArr, i, runnable, num, num2, l);
            }
        });
    }

    public static void lambda$runLinkRequest$101(Runnable runnable) {
        try {
            runnable.run();
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    public void lambda$runLinkRequest$102(Runnable runnable, TLObject tLObject, boolean z, Long l, Browser.Progress progress, Long l2, Integer num, BaseFragment baseFragment, int i, Bundle bundle) {
        try {
            runnable.run();
        } catch (Exception e) {
            FileLog.e(e);
        }
        if (tLObject instanceof TLRPC.TL_messages_chats) {
            TLRPC.TL_messages_chats tL_messages_chats = (TLRPC.TL_messages_chats) tLObject;
            if (!tL_messages_chats.chats.isEmpty()) {
                MessagesController.getInstance(this.currentAccount).putChats(tL_messages_chats.chats, false);
                TLRPC.Chat chat = tL_messages_chats.chats.get(0);
                if (chat != null && z && ChatObject.isBoostSupported(chat)) {
                    processBoostDialog(Long.valueOf(-l.longValue()), null, progress);
                } else if (chat != null && chat.forum) {
                    long j = -l.longValue();
                    if (l2 != null) {
                        openForumFromLink(j, num, null);
                    } else {
                        openForumFromLink(j, null, null);
                    }
                }
                if (baseFragment == null || MessagesController.getInstance(i).checkCanOpenChat(bundle, baseFragment)) {
                    getActionBarLayout().presentFragment(new ChatActivity(bundle));
                    return;
                }
                return;
            }
        }
        showAlertDialog(AlertsCreator.createNoAccessAlert(this, LocaleController.getString(R.string.DialogNotAvailable), LocaleController.getString(R.string.LinkNotFound), null));
    }

    public void lambda$runLinkRequest$103(final Runnable runnable, final boolean z, final Long l, final Browser.Progress progress, final Long l2, final Integer num, final BaseFragment baseFragment, final int i, final Bundle bundle, final TLObject tLObject, TLRPC.TL_error tL_error) {
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                LaunchActivity.this.lambda$runLinkRequest$102(runnable, tLObject, z, l, progress, l2, num, baseFragment, i, bundle);
            }
        });
    }

    public void lambda$runLinkRequest$104(final Bundle bundle, final Long l, int[] iArr, final Runnable runnable, final boolean z, final Browser.Progress progress, final Long l2, final Integer num, final BaseFragment baseFragment, final int i) {
        if (getActionBarLayout().presentFragment(new ChatActivity(bundle))) {
            return;
        }
        TLRPC.TL_channels_getChannels tL_channels_getChannels = new TLRPC.TL_channels_getChannels();
        TLRPC.TL_inputChannel tL_inputChannel = new TLRPC.TL_inputChannel();
        tL_inputChannel.channel_id = l.longValue();
        tL_channels_getChannels.id.add(tL_inputChannel);
        iArr[0] = ConnectionsManager.getInstance(this.currentAccount).sendRequest(tL_channels_getChannels, new RequestDelegate() {
            @Override
            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                LaunchActivity.this.lambda$runLinkRequest$103(runnable, z, l, progress, l2, num, baseFragment, i, bundle, tLObject, tL_error);
            }
        });
    }

    public void lambda$runLinkRequest$105(TLObject tLObject) {
        long j;
        String str;
        if (!(tLObject instanceof TL_account.resolvedBusinessChatLinks)) {
            showAlertDialog(AlertsCreator.createSimpleAlert(this, LocaleController.getString(R.string.BusinessLink), LocaleController.getString(R.string.BusinessLinkInvalid)));
            return;
        }
        TL_account.resolvedBusinessChatLinks resolvedbusinesschatlinks = (TL_account.resolvedBusinessChatLinks) tLObject;
        MessagesController.getInstance(this.currentAccount).putUsers(resolvedbusinesschatlinks.users, false);
        MessagesController.getInstance(this.currentAccount).putChats(resolvedbusinesschatlinks.chats, false);
        MessagesStorage.getInstance(this.currentAccount).putUsersAndChats(resolvedbusinesschatlinks.users, resolvedbusinesschatlinks.chats, true, true);
        Bundle bundle = new Bundle();
        TLRPC.Peer peer = resolvedbusinesschatlinks.peer;
        if (!(peer instanceof TLRPC.TL_peerUser)) {
            if ((peer instanceof TLRPC.TL_peerChat) || (peer instanceof TLRPC.TL_peerChannel)) {
                j = peer.channel_id;
                str = "chat_id";
            }
            ChatActivity chatActivity = new ChatActivity(bundle);
            chatActivity.setResolvedChatLink(resolvedbusinesschatlinks);
            presentFragment(chatActivity, false, true);
        }
        j = peer.user_id;
        str = "user_id";
        bundle.putLong(str, j);
        ChatActivity chatActivity2 = new ChatActivity(bundle);
        chatActivity2.setResolvedChatLink(resolvedbusinesschatlinks);
        presentFragment(chatActivity2, false, true);
    }

    public void lambda$runLinkRequest$106(final TLObject tLObject, TLRPC.TL_error tL_error) {
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                LaunchActivity.this.lambda$runLinkRequest$105(tLObject);
            }
        });
    }

    public static void lambda$runLinkRequest$107(int i, int[] iArr, Runnable runnable, DialogInterface dialogInterface) {
        ConnectionsManager.getInstance(i).cancelRequest(iArr[0], true);
        if (runnable != null) {
            runnable.run();
        }
    }

    public static void lambda$runLinkRequest$108(int i, int[] iArr, Runnable runnable) {
        ConnectionsManager.getInstance(i).cancelRequest(iArr[0], true);
        if (runnable != null) {
            runnable.run();
        }
    }

    public void lambda$runLinkRequest$47(int i, String str, String str2, String str3, String str4, String str5, String str6, String str7, String str8, String str9, String str10, String str11, String str12, boolean z, Integer num, Long l, Long l2, Integer num2, String str13, HashMap hashMap, String str14, String str15, String str16, String str17, TLRPC.TL_wallPaper tL_wallPaper, String str18, String str19, String str20, boolean z2, String str21, int i2, String str22, String str23, String str24, String str25, String str26, Browser.Progress progress, boolean z3, int i3, boolean z4, String str27, boolean z5, boolean z6, boolean z7, boolean z8, boolean z9, String str28, int i4) {
        if (i4 != i) {
            switchToAccount(i4, true);
        }
        runLinkRequest(i4, str, str2, str3, str4, str5, str6, str7, str8, str9, str10, str11, str12, z, num, l, l2, num2, str13, hashMap, str14, str15, str16, str17, tL_wallPaper, str18, str19, str20, z2, str21, 1, i2, str22, str23, str24, str25, str26, progress, z3, i3, z4, str27, z5, z6, z7, z8, z9, str28);
    }

    public static void lambda$runLinkRequest$48(Browser.Progress progress, AlertDialog alertDialog) {
        if (progress != null) {
            progress.end();
        }
        if (alertDialog != null) {
            alertDialog.dismiss();
        }
    }

    public void lambda$runLinkRequest$49(TLObject tLObject, int i, String str, TLRPC.TL_error tL_error, Runnable runnable) {
        if (tLObject instanceof TLRPC.User) {
            TLRPC.User user = (TLRPC.User) tLObject;
            MessagesController.getInstance(i).putUser(user, false);
            Bundle bundle = new Bundle();
            bundle.putLong("user_id", user.id);
            lambda$runLinkRequest$93(new ChatActivity(bundle));
        } else {
            StringBuilder sb = new StringBuilder();
            sb.append("cant import contact token. token=");
            sb.append(str);
            sb.append(" err=");
            sb.append(tL_error == null ? null : tL_error.text);
            FileLog.e(sb.toString());
            BulletinFactory.of((BaseFragment) mainFragmentsStack.get(r2.size() - 1)).createErrorBulletin(LocaleController.getString(R.string.NoUsernameFound)).show();
        }
        try {
            runnable.run();
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    public void lambda$runLinkRequest$50(final int i, final String str, final Runnable runnable, final TLObject tLObject, final TLRPC.TL_error tL_error) {
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                LaunchActivity.this.lambda$runLinkRequest$49(tLObject, i, str, tL_error, runnable);
            }
        });
    }

    public static void lambda$runLinkRequest$51(TLObject tLObject, int i, String str, Runnable runnable) {
        ArrayList<TLRPC.Chat> arrayList;
        ArrayList<TLRPC.User> arrayList2;
        BaseFragment baseFragment = (BaseFragment) mainFragmentsStack.get(r0.size() - 1);
        try {
            if (tLObject instanceof TL_chatlists.chatlist_ChatlistInvite) {
                TL_chatlists.chatlist_ChatlistInvite chatlist_chatlistinvite = (TL_chatlists.chatlist_ChatlistInvite) tLObject;
                boolean z = chatlist_chatlistinvite instanceof TL_chatlists.TL_chatlists_chatlistInvite;
                if (z) {
                    TL_chatlists.TL_chatlists_chatlistInvite tL_chatlists_chatlistInvite = (TL_chatlists.TL_chatlists_chatlistInvite) chatlist_chatlistinvite;
                    arrayList = tL_chatlists_chatlistInvite.chats;
                    arrayList2 = tL_chatlists_chatlistInvite.users;
                } else if (chatlist_chatlistinvite instanceof TL_chatlists.TL_chatlists_chatlistInviteAlready) {
                    TL_chatlists.TL_chatlists_chatlistInviteAlready tL_chatlists_chatlistInviteAlready = (TL_chatlists.TL_chatlists_chatlistInviteAlready) chatlist_chatlistinvite;
                    arrayList = tL_chatlists_chatlistInviteAlready.chats;
                    arrayList2 = tL_chatlists_chatlistInviteAlready.users;
                } else {
                    arrayList = null;
                    arrayList2 = null;
                }
                MessagesController.getInstance(i).putChats(arrayList, false);
                MessagesController.getInstance(i).putUsers(arrayList2, false);
                if (!z || !((TL_chatlists.TL_chatlists_chatlistInvite) chatlist_chatlistinvite).peers.isEmpty()) {
                    FolderBottomSheet folderBottomSheet = new FolderBottomSheet(baseFragment, str, chatlist_chatlistinvite);
                    if (baseFragment != null) {
                        baseFragment.showDialog(folderBottomSheet);
                    } else {
                        folderBottomSheet.show();
                    }
                    runnable.run();
                    return;
                }
            }
            runnable.run();
            return;
        } catch (Exception e) {
            FileLog.e(e);
            return;
        }
        BulletinFactory.of(baseFragment).createErrorBulletin(LocaleController.getString(R.string.NoFolderFound)).show();
    }

    public static void lambda$runLinkRequest$52(final int i, final String str, final Runnable runnable, final TLObject tLObject, TLRPC.TL_error tL_error) {
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                LaunchActivity.lambda$runLinkRequest$51(TLObject.this, i, str, runnable);
            }
        });
    }

    public static void lambda$runLinkRequest$53(Runnable runnable) {
        try {
            runnable.run();
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    public static void lambda$runLinkRequest$54(Runnable runnable, String str) {
        if (runnable == null || !"paid".equals(str)) {
            return;
        }
        runnable.run();
    }

    public static void lambda$runLinkRequest$55(Runnable runnable, PaymentFormActivity.InvoiceStatus invoiceStatus) {
        if (invoiceStatus == PaymentFormActivity.InvoiceStatus.PAID) {
            runnable.run();
        }
    }

    public void lambda$runLinkRequest$56(TLRPC.TL_error tL_error, TLObject tLObject, TLRPC.TL_inputInvoiceSlug tL_inputInvoiceSlug, final Runnable runnable, int i, String str) {
        PaymentFormActivity paymentFormActivity;
        BulletinFactory of;
        int i2;
        if (tL_error != null) {
            if ("SUBSCRIPTION_ALREADY_ACTIVE".equalsIgnoreCase(tL_error.text)) {
                of = BulletinFactory.of((BaseFragment) mainFragmentsStack.get(r7.size() - 1));
                i2 = R.string.PaymentInvoiceSubscriptionLinkAlreadyPaid;
            } else {
                of = BulletinFactory.of((BaseFragment) mainFragmentsStack.get(r7.size() - 1));
                i2 = R.string.PaymentInvoiceLinkInvalid;
            }
            of.createErrorBulletin(LocaleController.getString(i2)).show();
        } else if (!isFinishing()) {
            if (tLObject instanceof TLRPC.TL_payments_paymentFormStars) {
                final Runnable runnable2 = this.navigateToPremiumGiftCallback;
                this.navigateToPremiumGiftCallback = null;
                StarsController.getInstance(this.currentAccount).openPaymentForm(null, tL_inputInvoiceSlug, (TLRPC.TL_payments_paymentFormStars) tLObject, new Runnable() {
                    @Override
                    public final void run() {
                        LaunchActivity.lambda$runLinkRequest$53(runnable);
                    }
                }, new Utilities.Callback() {
                    @Override
                    public final void run(Object obj) {
                        LaunchActivity.lambda$runLinkRequest$54(runnable2, (String) obj);
                    }
                });
                return;
            }
            if (tLObject instanceof TLRPC.PaymentForm) {
                TLRPC.PaymentForm paymentForm = (TLRPC.PaymentForm) tLObject;
                MessagesController.getInstance(i).putUsers(paymentForm.users, false);
                paymentFormActivity = new PaymentFormActivity(paymentForm, str, getActionBarLayout().getLastFragment());
            } else {
                paymentFormActivity = tLObject instanceof TLRPC.PaymentReceipt ? new PaymentFormActivity((TLRPC.PaymentReceipt) tLObject) : null;
            }
            if (paymentFormActivity != null) {
                final Runnable runnable3 = this.navigateToPremiumGiftCallback;
                if (runnable3 != null) {
                    this.navigateToPremiumGiftCallback = null;
                    paymentFormActivity.setPaymentFormCallback(new PaymentFormActivity.PaymentFormCallback() {
                        @Override
                        public final void onInvoiceStatusChanged(PaymentFormActivity.InvoiceStatus invoiceStatus) {
                            LaunchActivity.lambda$runLinkRequest$55(runnable3, invoiceStatus);
                        }
                    });
                }
                lambda$runLinkRequest$93(paymentFormActivity);
            }
        }
        try {
            runnable.run();
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    public void lambda$runLinkRequest$57(final TLRPC.TL_inputInvoiceSlug tL_inputInvoiceSlug, final Runnable runnable, final int i, final String str, final TLObject tLObject, final TLRPC.TL_error tL_error) {
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                LaunchActivity.this.lambda$runLinkRequest$56(tL_error, tLObject, tL_inputInvoiceSlug, runnable, i, str);
            }
        });
    }

    public void lambda$runLinkRequest$58(Runnable runnable, Long l, TL_stories.StoryItem storyItem) {
        try {
            runnable.run();
        } catch (Exception e) {
            FileLog.e(e);
        }
        BaseFragment lastFragment = getLastFragment();
        if (storyItem == null) {
            BulletinFactory global = BulletinFactory.global();
            if (global != null) {
                global.createSimpleBulletin(R.raw.story_bomb2, LocaleController.getString(R.string.StoryNotFound)).show();
                return;
            }
            return;
        }
        if (storyItem instanceof TL_stories.TL_storyItemDeleted) {
            BulletinFactory global2 = BulletinFactory.global();
            if (global2 != null) {
                global2.createSimpleBulletin(R.raw.story_bomb1, LocaleController.getString(R.string.StoryNotFound)).show();
                return;
            }
            return;
        }
        if (lastFragment != null) {
            storyItem.dialogId = l.longValue();
            StoryViewer createOverlayStoryViewer = lastFragment.createOverlayStoryViewer();
            createOverlayStoryViewer.instantClose();
            createOverlayStoryViewer.open(this, storyItem, (StoryViewer.PlaceProvider) null);
        }
    }

    public void lambda$runLinkRequest$59(int i, String str, String str2, String str3, String str4, String str5, String str6, String str7, String str8, String str9, String str10, String str11, String str12, boolean z, Integer num, Long l, Long l2, Integer num2, String str13, HashMap hashMap, String str14, String str15, String str16, String str17, TLRPC.TL_wallPaper tL_wallPaper, String str18, String str19, String str20, boolean z2, String str21, int i2, int i3, String str22, String str23, String str24, Browser.Progress progress, boolean z3, int i4, boolean z4, String str25, boolean z5, boolean z6, boolean z7, boolean z8, boolean z9, String str26) {
        runLinkRequest(i, str, str2, str3, str4, str5, str6, str7, str8, str9, str10, str11, str12, z, num, l, l2, num2, str13, hashMap, str14, str15, str16, str17, tL_wallPaper, str18, str19, str20, z2, str21, i2, i3, str22, str23, str24, null, null, progress, z3, i4, z4, str25, z5, z6, z7, z8, z9, str26);
    }

    public static void lambda$runLinkRequest$60(TLObject tLObject, int i) {
        if (tLObject instanceof TLRPC.TL_boolTrue) {
            MediaDataController.getInstance(i).loadAttachMenuBots(false, true, null);
        }
    }

    public static void lambda$runLinkRequest$61(final int i, final TLObject tLObject, TLRPC.TL_error tL_error) {
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                LaunchActivity.lambda$runLinkRequest$60(TLObject.this, i);
            }
        });
    }

    public void lambda$runLinkRequest$62(TLRPC.TL_attachMenuBot tL_attachMenuBot, final int i, Long l, String str, String str2, String str3, String str4, String str5, String str6, String str7, String str8, String str9, String str10, String str11, String str12, boolean z, Integer num, Long l2, Long l3, Integer num2, String str13, HashMap hashMap, String str14, String str15, String str16, String str17, TLRPC.TL_wallPaper tL_wallPaper, String str18, String str19, String str20, boolean z2, String str21, int i2, int i3, String str22, String str23, String str24, String str25, String str26, Browser.Progress progress, boolean z3, int i4, boolean z4, String str27, TLRPC.User user, Runnable runnable, boolean z5, boolean z6, boolean z7, boolean z8, boolean z9, boolean z10, String str28, Boolean bool) {
        tL_attachMenuBot.inactive = false;
        tL_attachMenuBot.request_write_access = false;
        TLRPC.TL_messages_toggleBotInAttachMenu tL_messages_toggleBotInAttachMenu = new TLRPC.TL_messages_toggleBotInAttachMenu();
        tL_messages_toggleBotInAttachMenu.bot = MessagesController.getInstance(i).getInputUser(l.longValue());
        tL_messages_toggleBotInAttachMenu.enabled = true;
        tL_messages_toggleBotInAttachMenu.write_allowed = true;
        ConnectionsManager.getInstance(i).sendRequest(tL_messages_toggleBotInAttachMenu, new RequestDelegate() {
            @Override
            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                LaunchActivity.lambda$runLinkRequest$61(i, tLObject, tL_error);
            }
        }, 66);
        processWebAppBot(i, str, str2, str3, str4, str5, str6, str7, str8, str9, str10, str11, str12, z, num, l2, l3, num2, str13, hashMap, str14, str15, str16, str17, tL_wallPaper, str18, str19, str20, z2, str21, i2, i3, str22, str23, str24, str25, str26, progress, z3, i4, z4, str27, user, runnable, z5, true, z6, z7, z8, z9, z10, str28);
    }

    public static void lambda$runLinkRequest$63(TLObject tLObject, int i) {
        if (tLObject instanceof TLRPC.TL_boolTrue) {
            MediaDataController.getInstance(i).loadAttachMenuBots(false, true, null);
        }
    }

    public static void lambda$runLinkRequest$64(final int i, final TLObject tLObject, TLRPC.TL_error tL_error) {
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                LaunchActivity.lambda$runLinkRequest$63(TLObject.this, i);
            }
        });
    }

    public void lambda$runLinkRequest$65(Long l, TLRPC.TL_attachMenuBot tL_attachMenuBot, AtomicBoolean atomicBoolean, final int i, String str, String str2, String str3, String str4, String str5, String str6, String str7, String str8, String str9, String str10, String str11, String str12, boolean z, Integer num, Long l2, Long l3, Integer num2, String str13, HashMap hashMap, String str14, String str15, String str16, String str17, TLRPC.TL_wallPaper tL_wallPaper, String str18, String str19, String str20, boolean z2, String str21, int i2, int i3, String str22, String str23, String str24, String str25, String str26, Browser.Progress progress, boolean z3, int i4, boolean z4, String str27, TLRPC.User user, Runnable runnable, boolean z5, boolean z6, boolean z7, boolean z8, boolean z9, String str28) {
        SharedPrefsHelper.setWebViewConfirmShown(this.currentAccount, l.longValue(), true);
        tL_attachMenuBot.inactive = false;
        tL_attachMenuBot.request_write_access = true ^ atomicBoolean.get();
        TLRPC.TL_messages_toggleBotInAttachMenu tL_messages_toggleBotInAttachMenu = new TLRPC.TL_messages_toggleBotInAttachMenu();
        tL_messages_toggleBotInAttachMenu.bot = MessagesController.getInstance(i).getInputUser(l.longValue());
        tL_messages_toggleBotInAttachMenu.write_allowed = atomicBoolean.get();
        ConnectionsManager.getInstance(i).sendRequest(tL_messages_toggleBotInAttachMenu, new RequestDelegate() {
            @Override
            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                LaunchActivity.lambda$runLinkRequest$64(i, tLObject, tL_error);
            }
        }, 66);
        processWebAppBot(i, str, str2, str3, str4, str5, str6, str7, str8, str9, str10, str11, str12, z, num, l2, l3, num2, str13, hashMap, str14, str15, str16, str17, tL_wallPaper, str18, str19, str20, z2, str21, i2, i3, str22, str23, str24, str25, str26, progress, z3, i4, z4, str27, user, runnable, false, false, z5, z6, z7, z8, z9, str28);
    }

    public void lambda$runLinkRequest$66(TLRPC.TL_error tL_error, final int i, final String str, final String str2, final String str3, final String str4, final String str5, final String str6, final String str7, final String str8, final String str9, final String str10, final String str11, final String str12, final boolean z, final Integer num, final Long l, final Long l2, final Integer num2, final String str13, final HashMap hashMap, final String str14, final String str15, final String str16, final String str17, final TLRPC.TL_wallPaper tL_wallPaper, final String str18, final String str19, final String str20, final boolean z2, final String str21, final int i2, final int i3, final String str22, final String str23, final String str24, final Browser.Progress progress, final boolean z3, final int i4, final boolean z4, final String str25, final boolean z5, final boolean z6, final boolean z7, final boolean z8, final boolean z9, final String str26, TLObject tLObject, final Long l3, final String str27, final String str28, final TLRPC.User user, final Runnable runnable) {
        LaunchActivity launchActivity;
        ChatActivity$ChatMessageCellDelegate$$ExternalSyntheticLambda4 chatActivity$ChatMessageCellDelegate$$ExternalSyntheticLambda4;
        if (tL_error != null) {
            AndroidUtilities.runOnUIThread(new Runnable() {
                @Override
                public final void run() {
                    LaunchActivity.this.lambda$runLinkRequest$59(i, str, str2, str3, str4, str5, str6, str7, str8, str9, str10, str11, str12, z, num, l, l2, num2, str13, hashMap, str14, str15, str16, str17, tL_wallPaper, str18, str19, str20, z2, str21, i2, i3, str22, str23, str24, progress, z3, i4, z4, str25, z5, z6, z7, z8, z9, str26);
                }
            });
            return;
        }
        if (tLObject instanceof TLRPC.TL_attachMenuBotsBot) {
            final TLRPC.TL_attachMenuBot tL_attachMenuBot = ((TLRPC.TL_attachMenuBotsBot) tLObject).bot;
            final boolean z10 = tL_attachMenuBot != null && (tL_attachMenuBot.show_in_side_menu || tL_attachMenuBot.show_in_attach_menu);
            if ((!tL_attachMenuBot.inactive && !tL_attachMenuBot.side_menu_disclaimer_needed) || !z10) {
                if (!tL_attachMenuBot.request_write_access && !z3) {
                    processWebAppBot(i, str, str2, str3, str4, str5, str6, str7, str8, str9, str10, str11, str12, z, num, l, l2, num2, str13, hashMap, str14, str15, str16, str17, tL_wallPaper, str18, str19, str20, z2, str21, i2, i3, str22, str23, str24, str27, str28, progress, z3, i4, z4, str25, user, runnable, false, false, z5, z6, z7, z8, z9, str26);
                    return;
                } else {
                    final AtomicBoolean atomicBoolean = new AtomicBoolean(true);
                    AlertsCreator.createBotLaunchAlert(getLastFragment(), atomicBoolean, user, new Runnable() {
                        @Override
                        public final void run() {
                            LaunchActivity.this.lambda$runLinkRequest$65(l3, tL_attachMenuBot, atomicBoolean, i, str, str2, str3, str4, str5, str6, str7, str8, str9, str10, str11, str12, z, num, l, l2, num2, str13, hashMap, str14, str15, str16, str17, tL_wallPaper, str18, str19, str20, z2, str21, i2, i3, str22, str23, str24, str27, str28, progress, z3, i4, z4, str25, user, runnable, z5, z6, z7, z8, z9, str26);
                        }
                    });
                    return;
                }
            }
            com.google.android.exoplayer2.util.Consumer consumer = new com.google.android.exoplayer2.util.Consumer() {
                @Override
                public final void accept(Object obj) {
                    LaunchActivity.this.lambda$runLinkRequest$62(tL_attachMenuBot, i, l3, str, str2, str3, str4, str5, str6, str7, str8, str9, str10, str11, str12, z, num, l, l2, num2, str13, hashMap, str14, str15, str16, str17, tL_wallPaper, str18, str19, str20, z2, str21, i2, i3, str22, str23, str24, str27, str28, progress, z3, i4, z4, str25, user, runnable, z10, z5, z6, z7, z8, z9, str26, (Boolean) obj);
                }
            };
            if (progress != null) {
                chatActivity$ChatMessageCellDelegate$$ExternalSyntheticLambda4 = new ChatActivity$ChatMessageCellDelegate$$ExternalSyntheticLambda4(progress);
                launchActivity = this;
            } else {
                launchActivity = this;
                chatActivity$ChatMessageCellDelegate$$ExternalSyntheticLambda4 = null;
            }
            WebAppDisclaimerAlert.show(launchActivity, consumer, null, chatActivity$ChatMessageCellDelegate$$ExternalSyntheticLambda4);
        }
    }

    public void lambda$runLinkRequest$67(final int i, final String str, final String str2, final String str3, final String str4, final String str5, final String str6, final String str7, final String str8, final String str9, final String str10, final String str11, final String str12, final boolean z, final Integer num, final Long l, final Long l2, final Integer num2, final String str13, final HashMap hashMap, final String str14, final String str15, final String str16, final String str17, final TLRPC.TL_wallPaper tL_wallPaper, final String str18, final String str19, final String str20, final boolean z2, final String str21, final int i2, final int i3, final String str22, final String str23, final String str24, final Browser.Progress progress, final boolean z3, final int i4, final boolean z4, final String str25, final boolean z5, final boolean z6, final boolean z7, final boolean z8, final boolean z9, final String str26, final Long l3, final String str27, final String str28, final TLRPC.User user, final Runnable runnable, final TLObject tLObject, final TLRPC.TL_error tL_error) {
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                LaunchActivity.this.lambda$runLinkRequest$66(tL_error, i, str, str2, str3, str4, str5, str6, str7, str8, str9, str10, str11, str12, z, num, l, l2, num2, str13, hashMap, str14, str15, str16, str17, tL_wallPaper, str18, str19, str20, z2, str21, i2, i3, str22, str23, str24, progress, z3, i4, z4, str25, z5, z6, z7, z8, z9, str26, tLObject, l3, str27, str28, user, runnable);
            }
        });
    }

    public boolean lambda$runLinkRequest$68(String str, int i, TLRPC.User user, DialogsActivity dialogsActivity, ArrayList arrayList, CharSequence charSequence, boolean z, boolean z2, int i2, TopicsFragment topicsFragment) {
        String str2;
        long j = ((MessagesStorage.TopicKey) arrayList.get(0)).dialogId;
        TLRPC.TL_inputMediaGame tL_inputMediaGame = new TLRPC.TL_inputMediaGame();
        TLRPC.TL_inputGameShortName tL_inputGameShortName = new TLRPC.TL_inputGameShortName();
        tL_inputMediaGame.id = tL_inputGameShortName;
        tL_inputGameShortName.short_name = str;
        tL_inputGameShortName.bot_id = MessagesController.getInstance(i).getInputUser(user);
        SendMessagesHelper.getInstance(i).sendGame(MessagesController.getInstance(i).getInputPeer(j), tL_inputMediaGame, 0L, 0L);
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
        if (MessagesController.getInstance(i).checkCanOpenChat(bundle, dialogsActivity)) {
            NotificationCenter.getInstance(i).lambda$postNotificationNameOnUIThread$1(NotificationCenter.closeChats, new Object[0]);
            getActionBarLayout().presentFragment(new ChatActivity(bundle), true, false, true, false);
        }
        return true;
    }

    public void lambda$runLinkRequest$69(int i, TLRPC.Chat chat, DialogsActivity dialogsActivity) {
        NotificationCenter.getInstance(i).lambda$postNotificationNameOnUIThread$1(NotificationCenter.closeChats, new Object[0]);
        Bundle bundle = new Bundle();
        bundle.putBoolean("scrollToTopOnResume", true);
        bundle.putLong("chat_id", chat.id);
        if (MessagesController.getInstance(this.currentAccount).checkCanOpenChat(bundle, dialogsActivity)) {
            presentFragment(new ChatActivity(bundle), true, false);
        }
    }

    public void lambda$runLinkRequest$70(java.lang.String r24, org.telegram.tgnet.TLRPC.TL_chatAdminRights r25, boolean r26, java.lang.String r27, final int r28, final org.telegram.tgnet.TLRPC.Chat r29, final org.telegram.ui.DialogsActivity r30, org.telegram.tgnet.TLRPC.User r31, long r32, java.lang.String r34) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.LaunchActivity.lambda$runLinkRequest$70(java.lang.String, org.telegram.tgnet.TLRPC$TL_chatAdminRights, boolean, java.lang.String, int, org.telegram.tgnet.TLRPC$Chat, org.telegram.ui.DialogsActivity, org.telegram.tgnet.TLRPC$User, long, java.lang.String):void");
    }

    public void lambda$runLinkRequest$71(final String str, final String str2, final int i, final TLRPC.Chat chat, final DialogsActivity dialogsActivity, final TLRPC.User user, final long j, final boolean z, final TLRPC.TL_chatAdminRights tL_chatAdminRights, final String str3) {
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                LaunchActivity.this.lambda$runLinkRequest$70(str, tL_chatAdminRights, z, str2, i, chat, dialogsActivity, user, j, str3);
            }
        });
    }

    public void lambda$runLinkRequest$72(long j, int i, TLRPC.User user, String str, DialogInterface dialogInterface, int i2) {
        Bundle bundle = new Bundle();
        bundle.putBoolean("scrollToTopOnResume", true);
        long j2 = -j;
        bundle.putLong("chat_id", j2);
        ChatActivity chatActivity = new ChatActivity(bundle);
        NotificationCenter.getInstance(i).lambda$postNotificationNameOnUIThread$1(NotificationCenter.closeChats, new Object[0]);
        MessagesController.getInstance(i).addUserToChat(j2, user, 0, str, chatActivity, null);
        getActionBarLayout().presentFragment(chatActivity, true, false, true, false);
    }

    public boolean lambda$runLinkRequest$73(final int i, final TLRPC.User user, final String str, final String str2, final DialogsActivity dialogsActivity, DialogsActivity dialogsActivity2, ArrayList arrayList, CharSequence charSequence, boolean z, boolean z2, int i2, TopicsFragment topicsFragment) {
        TLRPC.TL_chatAdminRights tL_chatAdminRights;
        final long j = ((MessagesStorage.TopicKey) arrayList.get(0)).dialogId;
        final TLRPC.Chat chat = MessagesController.getInstance(this.currentAccount).getChat(Long.valueOf(-j));
        if (chat == null || (!chat.creator && ((tL_chatAdminRights = chat.admin_rights) == null || !tL_chatAdminRights.add_admins))) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            int i3 = R.string.AddBot;
            builder.setTitle(LocaleController.getString(i3));
            builder.setMessage(AndroidUtilities.replaceTags(LocaleController.formatString("AddMembersAlertNamesText", R.string.AddMembersAlertNamesText, UserObject.getUserName(user), chat == null ? "" : chat.title)));
            builder.setNegativeButton(LocaleController.getString(R.string.Cancel), null);
            builder.setPositiveButton(LocaleController.getString(i3), new DialogInterface.OnClickListener() {
                @Override
                public final void onClick(DialogInterface dialogInterface, int i4) {
                    LaunchActivity.this.lambda$runLinkRequest$72(j, i, user, str2, dialogInterface, i4);
                }
            });
            builder.show();
        } else {
            MessagesController.getInstance(i).checkIsInChat(false, chat, user, new MessagesController.IsInChatCheckedCallback() {
                @Override
                public final void run(boolean z3, TLRPC.TL_chatAdminRights tL_chatAdminRights2, String str3) {
                    LaunchActivity.this.lambda$runLinkRequest$71(str, str2, i, chat, dialogsActivity, user, j, z3, tL_chatAdminRights2, str3);
                }
            });
        }
        return true;
    }

    public static void lambda$runLinkRequest$74(Runnable runnable) {
        try {
            runnable.run();
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    public void lambda$runLinkRequest$75(final Runnable runnable, final int i, final String str, final String str2, final boolean z, final String str3, final String str4, final int i2, final String str5, final String str6, final String str7, final String str8, final String str9, final String str10, final String str11, final String str12, final String str13, final String str14, final String str15, final String str16, final boolean z2, final Integer num, final Long l, final Long l2, final Integer num2, final HashMap hashMap, final String str17, final String str18, final String str19, final String str20, final TLRPC.TL_wallPaper tL_wallPaper, final String str21, final String str22, final int i3, final int i4, final String str23, final String str24, final String str25, final Browser.Progress progress, final boolean z3, final boolean z4, final String str26, final boolean z5, final boolean z6, final boolean z7, final boolean z8, final boolean z9, final String str27, final String str28, int[] iArr, final Long l3) {
        BulletinFactory of;
        String string;
        long longValue;
        boolean z10;
        BulletinFactory of2;
        int i5;
        final TLRPC.User user;
        if (l3 != null && l3.longValue() == Long.MAX_VALUE) {
            try {
                runnable.run();
            } catch (Exception e) {
                FileLog.e(e);
            }
            new AlertDialog.Builder(this, null).setTitle(LocaleController.getString(R.string.AffiliateLinkExpiredTitle)).setMessage(LocaleController.getString(R.string.AffiliateLinkExpiredText)).setNegativeButton(LocaleController.getString(R.string.OK), null).show();
            return;
        }
        if (!isFinishing()) {
            if (i == 0 || l3 == null) {
                if (l3 == null || this.actionBarLayout == null || (!(str == null && str2 == null) && ((str == null || l3.longValue() <= 0) && ((str2 == null || l3.longValue() <= 0) && ((!z || l3.longValue() >= 0) && (str3 == null || l3.longValue() >= 0)))))) {
                    try {
                        BaseFragment lastFragment = getLastFragment();
                        if (lastFragment != null) {
                            if (lastFragment instanceof ChatActivity) {
                                ((ChatActivity) lastFragment).shakeContent();
                            }
                            if (AndroidUtilities.isNumeric(str5)) {
                                of = BulletinFactory.of(lastFragment);
                                string = LocaleController.getString(R.string.NoPhoneFound);
                            } else {
                                of = BulletinFactory.of(lastFragment);
                                string = LocaleController.getString(R.string.NoUsernameFound);
                            }
                            of.createErrorBulletin(string).show();
                        }
                    } catch (Exception e2) {
                        FileLog.e(e2);
                    }
                } else {
                    if (!TextUtils.isEmpty(str4) && (user = MessagesController.getInstance(i2).getUser(l3)) != null && user.bot) {
                        if (!user.bot_attach_menu || MediaDataController.getInstance(i2).botInAttachMenu(user.id)) {
                            processWebAppBot(i2, str5, str6, str7, str8, str9, str10, str11, str12, str13, str14, str15, str16, z2, num, l, l2, num2, str, hashMap, str17, str18, str19, str20, tL_wallPaper, str21, str22, str2, z, str3, i3, i4, str23, str24, str25, str4, str28, progress, z3, i, z4, str26, user, runnable, false, false, z5, z6, z7, z8, z9, str27);
                            return;
                        }
                        TLRPC.TL_messages_getAttachMenuBot tL_messages_getAttachMenuBot = new TLRPC.TL_messages_getAttachMenuBot();
                        tL_messages_getAttachMenuBot.bot = MessagesController.getInstance(i2).getInputUser(l3.longValue());
                        ConnectionsManager.getInstance(i2).sendRequest(tL_messages_getAttachMenuBot, new RequestDelegate() {
                            @Override
                            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                                LaunchActivity.this.lambda$runLinkRequest$67(i2, str5, str6, str7, str8, str9, str10, str11, str12, str13, str14, str15, str16, z2, num, l, l2, num2, str, hashMap, str17, str18, str19, str20, tL_wallPaper, str21, str22, str2, z, str3, i3, i4, str23, str24, str25, progress, z3, i, z4, str26, z5, z6, z7, z8, z9, str27, l3, str4, str28, user, runnable, tLObject, tL_error);
                            }
                        });
                        return;
                    }
                    String str29 = str16;
                    if (z4 && ChatObject.isBoostSupported(MessagesController.getInstance(i2).getChat(Long.valueOf(-l3.longValue())))) {
                        processBoostDialog(l3, runnable, progress);
                        return;
                    }
                    if (str28 != null) {
                        TLRPC.User user2 = MessagesController.getInstance(i2).getUser(l3);
                        if (user2 != null && user2.bot) {
                            MessagesController.getInstance(i2).openApp(null, user2, str28, 0, progress, z5, z6);
                        }
                    } else if (str23 != null && str24 == null) {
                        TLRPC.User user3 = MessagesController.getInstance(i2).getUser(l3);
                        if (user3 == null || !user3.bot) {
                            ArrayList arrayList = mainFragmentsStack;
                            of2 = BulletinFactory.of((BaseFragment) arrayList.get(arrayList.size() - 1));
                            i5 = R.string.BotSetAttachLinkNotBot;
                        } else if (user3.bot_attach_menu) {
                            processAttachMenuBot(i2, l3.longValue(), str25, user3, str23, str28);
                        } else {
                            ArrayList arrayList2 = mainFragmentsStack;
                            of2 = BulletinFactory.of((BaseFragment) arrayList2.get(arrayList2.size() - 1));
                            i5 = R.string.BotCantAddToAttachMenu;
                        }
                        of2.createErrorBulletin(LocaleController.getString(i5)).show();
                    } else if (num != null && ((num2 != null || l2 != null) && l3.longValue() < 0)) {
                        int runCommentRequest = runCommentRequest(i2, runnable, num, num2, l2, MessagesController.getInstance(i2).getChat(Long.valueOf(-l3.longValue())));
                        iArr[0] = runCommentRequest;
                        if (runCommentRequest != 0) {
                            return;
                        }
                    } else if (str != null) {
                        Bundle bundle = new Bundle();
                        bundle.putBoolean("onlySelect", true);
                        bundle.putBoolean("cantSendToChannels", true);
                        bundle.putInt("dialogsType", 1);
                        bundle.putString("selectAlertString", LocaleController.getString(R.string.SendGameToText));
                        bundle.putString("selectAlertStringGroup", LocaleController.getString(R.string.SendGameToGroupText));
                        DialogsActivity dialogsActivity = new DialogsActivity(bundle);
                        final TLRPC.User user4 = MessagesController.getInstance(i2).getUser(l3);
                        dialogsActivity.setDelegate(new DialogsActivity.DialogsActivityDelegate() {
                            @Override
                            public final boolean didSelectDialogs(DialogsActivity dialogsActivity2, ArrayList arrayList3, CharSequence charSequence, boolean z11, boolean z12, int i6, TopicsFragment topicsFragment) {
                                boolean lambda$runLinkRequest$68;
                                lambda$runLinkRequest$68 = LaunchActivity.this.lambda$runLinkRequest$68(str, i2, user4, dialogsActivity2, arrayList3, charSequence, z11, z12, i6, topicsFragment);
                                return lambda$runLinkRequest$68;
                            }
                        });
                        getActionBarLayout().presentFragment(dialogsActivity, !AndroidUtilities.isTablet() ? this.actionBarLayout.getFragmentStack().size() <= 1 || !(this.actionBarLayout.getFragmentStack().get(this.actionBarLayout.getFragmentStack().size() - 1) instanceof DialogsActivity) : this.layersActionBarLayout.getFragmentStack().size() <= 0 || !(this.layersActionBarLayout.getFragmentStack().get(this.layersActionBarLayout.getFragmentStack().size() - 1) instanceof DialogsActivity), true, true, false);
                        if (SecretMediaViewer.hasInstance() && SecretMediaViewer.getInstance().isVisible()) {
                            SecretMediaViewer.getInstance().closePhoto(false, false);
                        } else if (PhotoViewer.hasInstance() && PhotoViewer.getInstance().isVisible()) {
                            PhotoViewer.getInstance().closePhoto(false, true);
                        } else if (ArticleViewer.hasInstance() && ArticleViewer.getInstance().isVisible()) {
                            ArticleViewer.getInstance().close(false, true);
                        }
                        StoryRecorder.destroyInstance();
                        GroupCallActivity groupCallActivity = GroupCallActivity.groupCallInstance;
                        if (groupCallActivity != null) {
                            groupCallActivity.dismiss();
                        }
                        this.drawerLayoutContainer.setAllowOpenDrawer(false, false);
                        if (AndroidUtilities.isTablet()) {
                            this.actionBarLayout.rebuildFragments(1);
                            this.rightActionBarLayout.rebuildFragments(1);
                        } else {
                            this.drawerLayoutContainer.setAllowOpenDrawer(true, false);
                        }
                    } else if (str10 == null && str11 == null) {
                        Bundle bundle2 = new Bundle();
                        TLRPC.User user5 = MessagesController.getInstance(i2).getUser(l3);
                        long longValue2 = l3.longValue();
                        long longValue3 = l3.longValue();
                        if (longValue2 < 0) {
                            bundle2.putLong("chat_id", -longValue3);
                            longValue = l3.longValue();
                        } else {
                            bundle2.putLong("user_id", longValue3);
                            longValue = l3.longValue();
                            if (str29 != null) {
                                if (str29.startsWith("@")) {
                                    str29 = " " + str29;
                                }
                                bundle2.putString("start_text", str29);
                            }
                        }
                        if (str9 == null || user5 == null || !user5.bot) {
                            z10 = false;
                        } else {
                            bundle2.putString("botUser", str9);
                            z10 = true;
                        }
                        if (this.navigateToPremiumBot) {
                            this.navigateToPremiumBot = false;
                            bundle2.putBoolean("premium_bot", true);
                        }
                        if (num != null) {
                            bundle2.putInt("message_id", num.intValue());
                        }
                        if (str2 != null) {
                            bundle2.putString("voicechat", str2);
                        }
                        if (z) {
                            bundle2.putBoolean("videochat", true);
                        }
                        if (str3 != null) {
                            bundle2.putString("livestream", str3);
                        }
                        if (i4 >= 0) {
                            bundle2.putInt("video_timestamp", i4);
                        }
                        if (str24 != null) {
                            bundle2.putString("attach_bot", str24);
                        }
                        if (str23 != null) {
                            bundle2.putString("attach_bot_start_command", str23);
                        }
                        ArrayList arrayList3 = mainFragmentsStack;
                        BaseFragment baseFragment = (arrayList3.isEmpty() || str2 != null) ? null : (BaseFragment) arrayList3.get(arrayList3.size() - 1);
                        if (baseFragment == null || MessagesController.getInstance(i2).checkCanOpenChat(bundle2, baseFragment)) {
                            boolean z11 = (baseFragment instanceof ChatActivity) && ((ChatActivity) baseFragment).getDialogId() == longValue;
                            if (z10 && z11) {
                                ((ChatActivity) baseFragment).setBotUser(str9);
                            } else if (str24 == null || !z11) {
                                long j = -longValue;
                                TLRPC.Chat chat = MessagesController.getInstance(this.currentAccount).getChat(Long.valueOf(j));
                                if (z8) {
                                    try {
                                        runnable.run();
                                    } catch (Exception e3) {
                                        FileLog.e(e3);
                                    }
                                    if (isFinishing()) {
                                        return;
                                    }
                                    Bundle bundle3 = new Bundle();
                                    long longValue4 = l3.longValue();
                                    long longValue5 = l3.longValue();
                                    if (longValue4 < 0) {
                                        bundle3.putLong("chat_id", -longValue5);
                                    } else {
                                        bundle3.putLong("user_id", longValue5);
                                    }
                                    getActionBarLayout().presentFragment(new ProfileActivity(bundle3));
                                    return;
                                }
                                if (chat == null || !chat.forum) {
                                    MessagesController.getInstance(i2).ensureMessagesLoaded(longValue, num == null ? 0 : num.intValue(), new AnonymousClass18(runnable, str3, baseFragment, longValue, num, bundle2));
                                    return;
                                }
                                Long valueOf = (l2 != null || num == null) ? l2 : Long.valueOf(num.intValue());
                                if (valueOf != null && valueOf.longValue() != 0) {
                                    openForumFromLink(longValue, num, new Runnable() {
                                        @Override
                                        public final void run() {
                                            LaunchActivity.lambda$runLinkRequest$74(runnable);
                                        }
                                    });
                                    return;
                                }
                                Bundle bundle4 = new Bundle();
                                bundle4.putLong("chat_id", j);
                                if (str2 != null) {
                                    bundle4.putString("voicechat", str2);
                                }
                                if (z) {
                                    bundle4.putBoolean("videochat", true);
                                }
                                lambda$runLinkRequest$93(TopicsFragment.getTopicsOrChat(this, bundle4));
                            } else {
                                ((ChatActivity) baseFragment).openAttachBotLayout(str24);
                            }
                        }
                    } else {
                        final TLRPC.User user6 = MessagesController.getInstance(i2).getUser(l3);
                        if (user6 == null || (user6.bot && user6.bot_nochats)) {
                            try {
                                ArrayList arrayList4 = mainFragmentsStack;
                                if (arrayList4.isEmpty()) {
                                    return;
                                }
                                BulletinFactory.of((BaseFragment) arrayList4.get(arrayList4.size() - 1)).createErrorBulletin(LocaleController.getString(R.string.BotCantJoinGroups)).show();
                                return;
                            } catch (Exception e4) {
                                FileLog.e(e4);
                                return;
                            }
                        }
                        Bundle bundle5 = new Bundle();
                        bundle5.putBoolean("onlySelect", true);
                        bundle5.putInt("dialogsType", 2);
                        bundle5.putBoolean("resetDelegate", false);
                        bundle5.putBoolean("closeFragment", false);
                        bundle5.putBoolean("allowGroups", str10 != null);
                        bundle5.putBoolean("allowChannels", str11 != null);
                        String str30 = TextUtils.isEmpty(str10) ? TextUtils.isEmpty(str11) ? null : str11 : str10;
                        final DialogsActivity dialogsActivity2 = new DialogsActivity(bundle5);
                        final String str31 = str30;
                        dialogsActivity2.setDelegate(new DialogsActivity.DialogsActivityDelegate() {
                            @Override
                            public final boolean didSelectDialogs(DialogsActivity dialogsActivity3, ArrayList arrayList5, CharSequence charSequence, boolean z12, boolean z13, int i6, TopicsFragment topicsFragment) {
                                boolean lambda$runLinkRequest$73;
                                lambda$runLinkRequest$73 = LaunchActivity.this.lambda$runLinkRequest$73(i2, user6, str12, str31, dialogsActivity2, dialogsActivity3, arrayList5, charSequence, z12, z13, i6, topicsFragment);
                                return lambda$runLinkRequest$73;
                            }
                        });
                        lambda$runLinkRequest$93(dialogsActivity2);
                    }
                }
                try {
                    runnable.run();
                    return;
                } catch (Exception e5) {
                    FileLog.e(e5);
                    return;
                }
            }
            MessagesController.getInstance(this.currentAccount).getStoriesController().resolveStoryLink(l3.longValue(), i, new com.google.android.exoplayer2.util.Consumer() {
                @Override
                public final void accept(Object obj) {
                    LaunchActivity.this.lambda$runLinkRequest$58(runnable, l3, (TL_stories.StoryItem) obj);
                }
            });
        }
    }

    public static void lambda$runLinkRequest$76(boolean[] zArr, DialogInterface dialogInterface) {
        zArr[0] = true;
    }

    public static void lambda$runLinkRequest$77(BaseFragment baseFragment, long j, TLRPC.Chat chat) {
        BulletinFactory.of(baseFragment).createSimpleBulletin(R.raw.stars_send, LocaleController.getString(R.string.StarsSubscriptionCompleted), AndroidUtilities.replaceTags(LocaleController.formatPluralString("StarsSubscriptionCompletedText", (int) j, chat.title))).show(true);
    }

    public void lambda$runLinkRequest$78(Long l, final long j) {
        BaseFragment safeLastFragment = getSafeLastFragment();
        if (safeLastFragment == null) {
            return;
        }
        final ChatActivity of = ChatActivity.of(l.longValue());
        safeLastFragment.presentFragment(of);
        final TLRPC.Chat chat = MessagesController.getInstance(this.currentAccount).getChat(Long.valueOf(-l.longValue()));
        if (chat != null) {
            AndroidUtilities.runOnUIThread(new Runnable() {
                @Override
                public final void run() {
                    LaunchActivity.lambda$runLinkRequest$77(BaseFragment.this, j, chat);
                }
            }, 250L);
        }
    }

    public void lambda$runLinkRequest$79(final long j, String str, final Long l) {
        if (!"paid".equals(str) || l.longValue() == 0) {
            return;
        }
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                LaunchActivity.this.lambda$runLinkRequest$78(l, j);
            }
        });
    }

    public void lambda$runLinkRequest$80(org.telegram.tgnet.TLRPC.TL_error r10, org.telegram.tgnet.TLObject r11, int r12, org.telegram.ui.ActionBar.AlertDialog r13, java.lang.Runnable r14, java.lang.String r15) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.LaunchActivity.lambda$runLinkRequest$80(org.telegram.tgnet.TLRPC$TL_error, org.telegram.tgnet.TLObject, int, org.telegram.ui.ActionBar.AlertDialog, java.lang.Runnable, java.lang.String):void");
    }

    public void lambda$runLinkRequest$81(final int i, final AlertDialog alertDialog, final Runnable runnable, final String str, final TLObject tLObject, final TLRPC.TL_error tL_error) {
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                LaunchActivity.this.lambda$runLinkRequest$80(tL_error, tLObject, i, alertDialog, runnable, str);
            }
        });
    }

    public void lambda$runLinkRequest$82(Runnable runnable, TLRPC.TL_error tL_error, TLObject tLObject, int i) {
        if (isFinishing()) {
            return;
        }
        try {
            runnable.run();
        } catch (Exception e) {
            FileLog.e(e);
        }
        if (tL_error != null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(LocaleController.getString(R.string.AppName));
            builder.setMessage(LocaleController.getString(tL_error.text.startsWith("FLOOD_WAIT") ? R.string.FloodWait : tL_error.text.equals("USERS_TOO_MUCH") ? R.string.JoinToGroupErrorFull : R.string.JoinToGroupErrorNotExist));
            builder.setPositiveButton(LocaleController.getString(R.string.OK), null);
            showAlertDialog(builder);
            return;
        }
        if (this.actionBarLayout != null) {
            TLRPC.Updates updates = (TLRPC.Updates) tLObject;
            if (updates.chats.isEmpty()) {
                return;
            }
            TLRPC.Chat chat = updates.chats.get(0);
            chat.left = false;
            chat.kicked = false;
            MessagesController.getInstance(i).putUsers(updates.users, false);
            MessagesController.getInstance(i).putChats(updates.chats, false);
            Bundle bundle = new Bundle();
            bundle.putLong("chat_id", chat.id);
            ArrayList arrayList = mainFragmentsStack;
            if (arrayList.isEmpty() || MessagesController.getInstance(i).checkCanOpenChat(bundle, (BaseFragment) arrayList.get(arrayList.size() - 1))) {
                ChatActivity chatActivity = new ChatActivity(bundle);
                NotificationCenter.getInstance(i).lambda$postNotificationNameOnUIThread$1(NotificationCenter.closeChats, new Object[0]);
                getActionBarLayout().presentFragment(chatActivity, false, true, true, false);
            }
        }
    }

    public void lambda$runLinkRequest$83(final int i, final Runnable runnable, final TLObject tLObject, final TLRPC.TL_error tL_error) {
        if (tL_error == null) {
            MessagesController.getInstance(i).processUpdates((TLRPC.Updates) tLObject, false);
        }
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                LaunchActivity.this.lambda$runLinkRequest$82(runnable, tL_error, tLObject, i);
            }
        });
    }

    public boolean lambda$runLinkRequest$84(boolean z, int i, String str, DialogsActivity dialogsActivity, ArrayList arrayList, CharSequence charSequence, boolean z2, boolean z3, int i2, TopicsFragment topicsFragment) {
        long j = ((MessagesStorage.TopicKey) arrayList.get(0)).dialogId;
        Bundle bundle = new Bundle();
        bundle.putBoolean("scrollToTopOnResume", true);
        bundle.putBoolean("hasUrl", z);
        if (DialogObject.isEncryptedDialog(j)) {
            bundle.putInt("enc_id", DialogObject.getEncryptedChatId(j));
        } else if (DialogObject.isUserDialog(j)) {
            bundle.putLong("user_id", j);
        } else {
            bundle.putLong("chat_id", -j);
        }
        if (MessagesController.getInstance(i).checkCanOpenChat(bundle, dialogsActivity)) {
            NotificationCenter.getInstance(i).lambda$postNotificationNameOnUIThread$1(NotificationCenter.closeChats, new Object[0]);
            MediaDataController.getInstance(i).saveDraft(j, 0, str, null, null, false, 0L);
            getActionBarLayout().presentFragment(new ChatActivity(bundle), true, false, true, false);
        }
        return true;
    }

    public void lambda$runLinkRequest$85(Runnable runnable, TLObject tLObject, int i, TL_account.authorizationForm authorizationform, TL_account.getAuthorizationForm getauthorizationform, String str, String str2, String str3) {
        try {
            runnable.run();
        } catch (Exception e) {
            FileLog.e(e);
        }
        if (tLObject != null) {
            MessagesController.getInstance(i).putUsers(authorizationform.users, false);
            lambda$runLinkRequest$93(new PassportActivity(5, getauthorizationform.bot_id, getauthorizationform.scope, getauthorizationform.public_key, str, str2, str3, authorizationform, (TL_account.Password) tLObject));
        }
    }

    public void lambda$runLinkRequest$86(final Runnable runnable, final int i, final TL_account.authorizationForm authorizationform, final TL_account.getAuthorizationForm getauthorizationform, final String str, final String str2, final String str3, final TLObject tLObject, TLRPC.TL_error tL_error) {
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                LaunchActivity.this.lambda$runLinkRequest$85(runnable, tLObject, i, authorizationform, getauthorizationform, str, str2, str3);
            }
        });
    }

    public void lambda$runLinkRequest$87(Runnable runnable, TLRPC.TL_error tL_error) {
        try {
            runnable.run();
            if ("APP_VERSION_OUTDATED".equals(tL_error.text)) {
                AlertsCreator.showUpdateAppAlert(this, LocaleController.getString(R.string.UpdateAppAlert), true);
            } else {
                showAlertDialog(AlertsCreator.createSimpleAlert(this, LocaleController.getString(R.string.ErrorOccurred) + "\n" + tL_error.text));
            }
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    public void lambda$runLinkRequest$88(int[] iArr, final int i, final Runnable runnable, final TL_account.getAuthorizationForm getauthorizationform, final String str, final String str2, final String str3, TLObject tLObject, final TLRPC.TL_error tL_error) {
        final TL_account.authorizationForm authorizationform = (TL_account.authorizationForm) tLObject;
        if (authorizationform == null) {
            AndroidUtilities.runOnUIThread(new Runnable() {
                @Override
                public final void run() {
                    LaunchActivity.this.lambda$runLinkRequest$87(runnable, tL_error);
                }
            });
        } else {
            iArr[0] = ConnectionsManager.getInstance(i).sendRequest(new TL_account.getPassword(), new RequestDelegate() {
                @Override
                public final void run(TLObject tLObject2, TLRPC.TL_error tL_error2) {
                    LaunchActivity.this.lambda$runLinkRequest$86(runnable, i, authorizationform, getauthorizationform, str, str2, str3, tLObject2, tL_error2);
                }
            });
        }
    }

    public void lambda$runLinkRequest$89(Runnable runnable, TLObject tLObject) {
        try {
            runnable.run();
        } catch (Exception e) {
            FileLog.e(e);
        }
        if (tLObject instanceof TLRPC.TL_help_deepLinkInfo) {
            TLRPC.TL_help_deepLinkInfo tL_help_deepLinkInfo = (TLRPC.TL_help_deepLinkInfo) tLObject;
            AlertsCreator.showUpdateAppAlert(this, tL_help_deepLinkInfo.message, tL_help_deepLinkInfo.update_app);
        }
    }

    public void lambda$runLinkRequest$90(final Runnable runnable, final TLObject tLObject, TLRPC.TL_error tL_error) {
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                LaunchActivity.this.lambda$runLinkRequest$89(runnable, tLObject);
            }
        });
    }

    public void lambda$runLinkRequest$91(Runnable runnable, TLObject tLObject, TLRPC.TL_error tL_error) {
        String str;
        AlertDialog.Builder createSimpleAlert;
        try {
            runnable.run();
        } catch (Exception e) {
            FileLog.e(e);
        }
        if (tLObject instanceof TLRPC.TL_langPackLanguage) {
            createSimpleAlert = AlertsCreator.createLanguageAlert(this, (TLRPC.TL_langPackLanguage) tLObject);
        } else {
            if (tL_error == null) {
                return;
            }
            if ("LANG_CODE_NOT_SUPPORTED".equals(tL_error.text)) {
                str = LocaleController.getString(R.string.LanguageUnsupportedError);
            } else {
                str = LocaleController.getString(R.string.ErrorOccurred) + "\n" + tL_error.text;
            }
            createSimpleAlert = AlertsCreator.createSimpleAlert(this, str);
        }
        showAlertDialog(createSimpleAlert);
    }

    public void lambda$runLinkRequest$92(final Runnable runnable, final TLObject tLObject, final TLRPC.TL_error tL_error) {
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                LaunchActivity.this.lambda$runLinkRequest$91(runnable, tLObject, tL_error);
            }
        });
    }

    public void lambda$runLinkRequest$94(Runnable runnable, TLObject tLObject, TLRPC.TL_wallPaper tL_wallPaper, TLRPC.TL_error tL_error) {
        try {
            runnable.run();
        } catch (Exception e) {
            FileLog.e(e);
        }
        if (!(tLObject instanceof TLRPC.TL_wallPaper)) {
            showAlertDialog(AlertsCreator.createSimpleAlert(this, LocaleController.getString(R.string.ErrorOccurred) + "\n" + tL_error.text));
            return;
        }
        TLRPC.TL_wallPaper tL_wallPaper2 = (TLRPC.TL_wallPaper) tLObject;
        if (tL_wallPaper2.pattern) {
            String str = tL_wallPaper2.slug;
            TLRPC.WallPaperSettings wallPaperSettings = tL_wallPaper.settings;
            WallpapersListActivity.ColorWallpaper colorWallpaper = new WallpapersListActivity.ColorWallpaper(str, wallPaperSettings.background_color, wallPaperSettings.second_background_color, wallPaperSettings.third_background_color, wallPaperSettings.fourth_background_color, AndroidUtilities.getWallpaperRotation(wallPaperSettings.rotation, false), r0.intensity / 100.0f, tL_wallPaper.settings.motion, null);
            colorWallpaper.pattern = tL_wallPaper2;
            tL_wallPaper2 = colorWallpaper;
        }
        ThemePreviewActivity themePreviewActivity = new ThemePreviewActivity(tL_wallPaper2, null, true, false);
        TLRPC.WallPaperSettings wallPaperSettings2 = tL_wallPaper.settings;
        themePreviewActivity.setInitialModes(wallPaperSettings2.blur, wallPaperSettings2.motion, wallPaperSettings2.intensity);
        lambda$runLinkRequest$93(themePreviewActivity);
    }

    public void lambda$runLinkRequest$95(final Runnable runnable, final TLRPC.TL_wallPaper tL_wallPaper, final TLObject tLObject, final TLRPC.TL_error tL_error) {
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                LaunchActivity.this.lambda$runLinkRequest$94(runnable, tLObject, tL_wallPaper, tL_error);
            }
        });
    }

    public void lambda$runLinkRequest$96(Browser.Progress progress) {
        this.loadingThemeFileName = null;
        this.loadingThemeWallpaperName = null;
        this.loadingThemeWallpaper = null;
        this.loadingThemeInfo = null;
        this.loadingThemeProgressDialog = null;
        this.loadingTheme = null;
        if (progress != null) {
            progress.end();
        }
    }

    public void lambda$runLinkRequest$97(TLObject tLObject, AlertDialog alertDialog, Runnable runnable, TLRPC.TL_error tL_error) {
        char c;
        if (tLObject instanceof TLRPC.TL_theme) {
            TLRPC.TL_theme tL_theme = (TLRPC.TL_theme) tLObject;
            TLRPC.TL_wallPaper tL_wallPaper = null;
            c = 0;
            TLRPC.ThemeSettings themeSettings = tL_theme.settings.size() > 0 ? tL_theme.settings.get(0) : null;
            if (themeSettings != null) {
                Theme.ThemeInfo theme = Theme.getTheme(Theme.getBaseThemeKey(themeSettings));
                if (theme != null) {
                    TLRPC.WallPaper wallPaper = themeSettings.wallpaper;
                    if (wallPaper instanceof TLRPC.TL_wallPaper) {
                        tL_wallPaper = (TLRPC.TL_wallPaper) wallPaper;
                        if (!FileLoader.getInstance(this.currentAccount).getPathToAttach(tL_wallPaper.document, true).exists()) {
                            this.loadingThemeProgressDialog = alertDialog;
                            this.loadingThemeAccent = true;
                            this.loadingThemeInfo = theme;
                            this.loadingTheme = tL_theme;
                            this.loadingThemeWallpaper = tL_wallPaper;
                            this.loadingThemeWallpaperName = FileLoader.getAttachFileName(tL_wallPaper.document);
                            FileLoader.getInstance(this.currentAccount).loadFile(tL_wallPaper.document, tL_wallPaper, 1, 1);
                            return;
                        }
                    }
                    try {
                        runnable.run();
                    } catch (Exception e) {
                        FileLog.e(e);
                    }
                    openThemeAccentPreview(tL_theme, tL_wallPaper, theme);
                }
                c = 1;
            } else {
                TLRPC.Document document = tL_theme.document;
                if (document != null) {
                    this.loadingThemeAccent = false;
                    this.loadingTheme = tL_theme;
                    this.loadingThemeFileName = FileLoader.getAttachFileName(document);
                    this.loadingThemeProgressDialog = alertDialog;
                    FileLoader.getInstance(this.currentAccount).loadFile(this.loadingTheme.document, tL_theme, 1, 1);
                }
                c = 1;
            }
        } else {
            if (tL_error == null || !"THEME_FORMAT_INVALID".equals(tL_error.text)) {
                c = 2;
            }
            c = 1;
        }
        if (c != 0) {
            try {
                runnable.run();
            } catch (Exception e2) {
                FileLog.e(e2);
            }
            showAlertDialog(AlertsCreator.createSimpleAlert(this, LocaleController.getString(R.string.Theme), LocaleController.getString(c == 1 ? R.string.ThemeNotSupported : R.string.ThemeNotFound)));
        }
    }

    public void lambda$runLinkRequest$98(final AlertDialog alertDialog, final Runnable runnable, final TLObject tLObject, final TLRPC.TL_error tL_error) {
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                LaunchActivity.this.lambda$runLinkRequest$97(tLObject, alertDialog, runnable, tL_error);
            }
        });
    }

    public void lambda$runLinkRequest$99(TLObject tLObject, int[] iArr, int i, Runnable runnable, Integer num, Integer num2, Long l) {
        if (tLObject instanceof TLRPC.TL_messages_chats) {
            TLRPC.TL_messages_chats tL_messages_chats = (TLRPC.TL_messages_chats) tLObject;
            if (!tL_messages_chats.chats.isEmpty()) {
                MessagesController.getInstance(this.currentAccount).putChats(tL_messages_chats.chats, false);
                iArr[0] = runCommentRequest(i, runnable, num, num2, l, tL_messages_chats.chats.get(0));
                return;
            }
        }
        try {
            runnable.run();
        } catch (Exception e) {
            FileLog.e(e);
        }
        showAlertDialog(AlertsCreator.createNoAccessAlert(this, LocaleController.getString(R.string.DialogNotAvailable), LocaleController.getString(R.string.LinkNotFound), null));
    }

    public boolean lambda$setupActionBarLayout$10(View view, MotionEvent motionEvent) {
        if (!this.actionBarLayout.getFragmentStack().isEmpty() && motionEvent.getAction() == 1) {
            float x = motionEvent.getX();
            float y = motionEvent.getY();
            int[] iArr = new int[2];
            this.layersActionBarLayout.getView().getLocationOnScreen(iArr);
            int i = iArr[0];
            int i2 = iArr[1];
            if (!this.layersActionBarLayout.checkTransitionAnimation() && (x <= i || x >= i + this.layersActionBarLayout.getView().getWidth() || y <= i2 || y >= i2 + this.layersActionBarLayout.getView().getHeight())) {
                if (!this.layersActionBarLayout.getFragmentStack().isEmpty()) {
                    while (this.layersActionBarLayout.getFragmentStack().size() - 1 > 0) {
                        ActionBarLayout actionBarLayout = this.layersActionBarLayout;
                        actionBarLayout.removeFragmentFromStack(actionBarLayout.getFragmentStack().get(0));
                    }
                    this.layersActionBarLayout.closeLastFragment(true);
                }
                return true;
            }
        }
        return false;
    }

    public static void lambda$setupActionBarLayout$11(View view) {
    }

    public void lambda$showAlertDialog$133(AlertDialog alertDialog, DialogInterface dialogInterface) {
        Bulletin createSimpleBulletin;
        if (alertDialog != null) {
            if (alertDialog == this.localeDialog) {
                ActionBarLayout actionBarLayout = this.actionBarLayout;
                BaseFragment lastFragment = actionBarLayout == null ? null : actionBarLayout.getLastFragment();
                try {
                    String str = LocaleController.getInstance().getCurrentLocaleInfo().shortName;
                    if (lastFragment != null) {
                        createSimpleBulletin = BulletinFactory.of(lastFragment).createSimpleBulletin(R.raw.msg_translate, getStringForLanguageAlert(str.equals("en") ? this.englishLocaleStrings : this.systemLocaleStrings, "ChangeLanguageLater", R.string.ChangeLanguageLater));
                    } else {
                        createSimpleBulletin = BulletinFactory.of(Bulletin.BulletinWindow.make(this), null).createSimpleBulletin(R.raw.msg_translate, getStringForLanguageAlert(str.equals("en") ? this.englishLocaleStrings : this.systemLocaleStrings, "ChangeLanguageLater", R.string.ChangeLanguageLater));
                    }
                    createSimpleBulletin.setDuration(5000).show();
                } catch (Exception e) {
                    FileLog.e(e);
                }
                this.localeDialog = null;
            } else if (alertDialog == this.proxyErrorDialog) {
                MessagesController.getGlobalMainSettings();
                SharedPreferences.Editor edit = MessagesController.getGlobalMainSettings().edit();
                edit.putBoolean("proxy_enabled", false);
                edit.putBoolean("proxy_enabled_calls", false);
                edit.commit();
                ConnectionsManager.setProxySettings(false, "", 1080, "", "", "");
                NotificationCenter.getGlobalInstance().lambda$postNotificationNameOnUIThread$1(NotificationCenter.proxySettingsChanged, new Object[0]);
                this.proxyErrorDialog = null;
            }
        }
        this.visibleDialogs.remove(alertDialog);
    }

    public void lambda$showLanguageAlert$159(HashMap hashMap, LocaleController.LocaleInfo[] localeInfoArr, String str) {
        this.systemLocaleStrings = hashMap;
        if (this.englishLocaleStrings == null || hashMap == null) {
            return;
        }
        showLanguageAlertInternal(localeInfoArr[1], localeInfoArr[0], str);
    }

    public void lambda$showLanguageAlert$160(final LocaleController.LocaleInfo[] localeInfoArr, final String str, TLObject tLObject, TLRPC.TL_error tL_error) {
        final HashMap hashMap = new HashMap();
        if (tLObject instanceof Vector) {
            Vector vector = (Vector) tLObject;
            for (int i = 0; i < vector.objects.size(); i++) {
                TLRPC.LangPackString langPackString = (TLRPC.LangPackString) vector.objects.get(i);
                hashMap.put(langPackString.key, langPackString.value);
            }
        }
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                LaunchActivity.this.lambda$showLanguageAlert$159(hashMap, localeInfoArr, str);
            }
        });
    }

    public void lambda$showLanguageAlert$161(HashMap hashMap, LocaleController.LocaleInfo[] localeInfoArr, String str) {
        this.englishLocaleStrings = hashMap;
        if (hashMap == null || this.systemLocaleStrings == null) {
            return;
        }
        showLanguageAlertInternal(localeInfoArr[1], localeInfoArr[0], str);
    }

    public void lambda$showLanguageAlert$162(final LocaleController.LocaleInfo[] localeInfoArr, final String str, TLObject tLObject, TLRPC.TL_error tL_error) {
        final HashMap hashMap = new HashMap();
        if (tLObject instanceof Vector) {
            Vector vector = (Vector) tLObject;
            for (int i = 0; i < vector.objects.size(); i++) {
                TLRPC.LangPackString langPackString = (TLRPC.LangPackString) vector.objects.get(i);
                hashMap.put(langPackString.key, langPackString.value);
            }
        }
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                LaunchActivity.this.lambda$showLanguageAlert$161(hashMap, localeInfoArr, str);
            }
        });
    }

    public static void lambda$showLanguageAlertInternal$156(LocaleController.LocaleInfo[] localeInfoArr, LanguageCell[] languageCellArr, View view) {
        Integer num = (Integer) view.getTag();
        localeInfoArr[0] = ((LanguageCell) view).getCurrentLocale();
        int i = 0;
        while (i < languageCellArr.length) {
            languageCellArr[i].setLanguageSelected(i == num.intValue(), true);
            i++;
        }
    }

    public void lambda$showLanguageAlertInternal$157(View view) {
        this.localeDialog = null;
        this.drawerLayoutContainer.closeDrawer(true);
        lambda$runLinkRequest$93(new LanguageSelectActivity());
        for (int i = 0; i < this.visibleDialogs.size(); i++) {
            if (((Dialog) this.visibleDialogs.get(i)).isShowing()) {
                ((Dialog) this.visibleDialogs.get(i)).dismiss();
            }
        }
        this.visibleDialogs.clear();
    }

    public void lambda$showLanguageAlertInternal$158(LocaleController.LocaleInfo[] localeInfoArr, DialogInterface dialogInterface, int i) {
        LocaleController.getInstance().applyLanguage(localeInfoArr[0], true, false, this.currentAccount);
        rebuildAllFragments(true);
    }

    public void lambda$showPasscodeActivity$13(Runnable runnable) {
        this.actionBarLayout.getView().setVisibility(4);
        if (AndroidUtilities.isTablet()) {
            ActionBarLayout actionBarLayout = this.layersActionBarLayout;
            if (actionBarLayout != null && actionBarLayout.getView() != null && this.layersActionBarLayout.getView().getVisibility() == 0) {
                this.layersActionBarLayout.getView().setVisibility(4);
            }
            ActionBarLayout actionBarLayout2 = this.rightActionBarLayout;
            if (actionBarLayout2 != null && actionBarLayout2.getView() != null) {
                this.rightActionBarLayout.getView().setVisibility(4);
            }
        }
        if (runnable != null) {
            runnable.run();
        }
    }

    public void lambda$showPasscodeActivity$14(PasscodeView passcodeView) {
        SharedConfig.isWaitingForPasscodeEnter = false;
        Intent intent = this.passcodeSaveIntent;
        if (intent != null) {
            handleIntent(intent, this.passcodeSaveIntentIsNew, this.passcodeSaveIntentIsRestore, true, null, false, true);
            this.passcodeSaveIntent = null;
        }
        this.drawerLayoutContainer.setAllowOpenDrawer(true, false);
        this.actionBarLayout.getView().setVisibility(0);
        this.actionBarLayout.rebuildFragments(1);
        this.actionBarLayout.updateTitleOverlay();
        if (AndroidUtilities.isTablet()) {
            this.layersActionBarLayout.rebuildFragments(1);
            this.rightActionBarLayout.rebuildFragments(1);
            if (this.layersActionBarLayout.getView().getVisibility() == 4) {
                this.layersActionBarLayout.getView().setVisibility(0);
            }
            this.rightActionBarLayout.getView().setVisibility(0);
        }
        NotificationCenter.getGlobalInstance().lambda$postNotificationNameOnUIThread$1(NotificationCenter.passcodeDismissed, passcodeView);
        try {
            NotificationsController.getInstance(UserConfig.selectedAccount).showNotifications();
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    public static DialogsActivity lambda$switchToAccount$12(Void r1) {
        return new DialogsActivity(null);
    }

    public void lambda$updateCurrentConnectionState$163() {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.LaunchActivity.lambda$updateCurrentConnectionState$163():void");
    }

    public static void makeRipple(float f, float f2, float f3) {
        LaunchActivity launchActivity = instance;
        if (launchActivity == null) {
            return;
        }
        launchActivity.makeRippleInternal(f, f2, f3);
    }

    private void makeRippleInternal(float f, float f2, float f3) {
        ISuperRipple iSuperRipple;
        ISuperRipple superRippleFallback;
        View decorView = getWindow().getDecorView();
        if (decorView == null) {
            return;
        }
        int i = Build.VERSION.SDK_INT;
        if (i >= 33) {
            ISuperRipple iSuperRipple2 = this.currentRipple;
            if (iSuperRipple2 == null || iSuperRipple2.view != decorView) {
                superRippleFallback = new SuperRipple(decorView);
                this.currentRipple = superRippleFallback;
            }
        } else if (i >= 26 && ((iSuperRipple = this.currentRipple) == null || iSuperRipple.view != decorView)) {
            superRippleFallback = new SuperRippleFallback(decorView);
            this.currentRipple = superRippleFallback;
        }
        ISuperRipple iSuperRipple3 = this.currentRipple;
        if (iSuperRipple3 != null) {
            iSuperRipple3.animate(f, f2, f3);
        }
    }

    private void onFinish() {
        Runnable runnable = this.lockRunnable;
        if (runnable != null) {
            AndroidUtilities.cancelRunOnUIThread(runnable);
            this.lockRunnable = null;
        }
        if (this.finished) {
            return;
        }
        this.finished = true;
        int i = this.currentAccount;
        if (i != -1) {
            NotificationCenter.getInstance(i).removeObserver(this, NotificationCenter.appDidLogout);
            NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.openBoostForUsersDialog);
            NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.mainUserInfoChanged);
            NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.attachMenuBotsDidLoad);
            NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.didUpdateConnectionState);
            NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.needShowAlert);
            NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.wasUnableToFindCurrentLocation);
            NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.openArticle);
            NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.hasNewContactsToImport);
            NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.needShowPlayServicesAlert);
            NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.fileLoaded);
            NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.fileLoadProgressChanged);
            NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.fileLoadFailed);
            NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.historyImportProgressChanged);
            NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.groupCallUpdated);
            NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.stickersImportComplete);
            NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.newSuggestionsAvailable);
            NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.currentUserShowLimitReachedDialog);
            NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.currentUserPremiumStatusChanged);
        }
        NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.needShowAlert);
        NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.didSetNewWallpapper);
        NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.suggestedLangpack);
        NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.reloadInterface);
        NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.didSetNewTheme);
        NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.needSetDayNightTheme);
        NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.needCheckSystemBarColors);
        NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.closeOtherAppActivities);
        NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.didSetPasscode);
        NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.notificationsCountUpdated);
        NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.screenStateChanged);
        NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.showBulletin);
        NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.appUpdateAvailable);
        NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.requestPermissions);
        NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.billingConfirmPurchaseError);
        LiteMode.removeOnPowerSaverAppliedListener(new LaunchActivity$$ExternalSyntheticLambda21(this));
    }

    private void onPasscodePause() {
        if (this.lockRunnable != null) {
            if (BuildVars.LOGS_ENABLED) {
                FileLog.d("cancel lockRunnable onPasscodePause");
            }
            AndroidUtilities.cancelRunOnUIThread(this.lockRunnable);
            this.lockRunnable = null;
        }
        if (SharedConfig.passcodeHash.length() != 0) {
            SharedConfig.lastPauseTime = (int) (SystemClock.elapsedRealtime() / 1000);
            AnonymousClass22 anonymousClass22 = new Runnable() {
                AnonymousClass22() {
                }

                @Override
                public void run() {
                    if (LaunchActivity.this.lockRunnable == this) {
                        if (AndroidUtilities.needShowPasscode(true)) {
                            if (BuildVars.LOGS_ENABLED) {
                                FileLog.d("lock app");
                            }
                            LaunchActivity.this.showPasscodeActivity(true, false, -1, -1, null, null);
                            try {
                                NotificationsController.getInstance(UserConfig.selectedAccount).showNotifications();
                            } catch (Exception e) {
                                FileLog.e(e);
                            }
                        } else if (BuildVars.LOGS_ENABLED) {
                            FileLog.d("didn't pass lock check");
                        }
                        LaunchActivity.this.lockRunnable = null;
                    }
                }
            };
            this.lockRunnable = anonymousClass22;
            if (SharedConfig.appLocked) {
                AndroidUtilities.runOnUIThread(anonymousClass22, 1000L);
                if (BuildVars.LOGS_ENABLED) {
                    FileLog.d("schedule app lock in 1000");
                }
            } else if (SharedConfig.autoLockIn != 0) {
                if (BuildVars.LOGS_ENABLED) {
                    FileLog.d("schedule app lock in " + ((SharedConfig.autoLockIn * 1000) + 1000));
                }
                AndroidUtilities.runOnUIThread(this.lockRunnable, (SharedConfig.autoLockIn * 1000) + 1000);
            }
        } else {
            SharedConfig.lastPauseTime = 0;
        }
        SharedConfig.saveConfig();
    }

    private void onPasscodeResume() {
        if (this.lockRunnable != null) {
            if (BuildVars.LOGS_ENABLED) {
                FileLog.d("cancel lockRunnable onPasscodeResume");
            }
            AndroidUtilities.cancelRunOnUIThread(this.lockRunnable);
            this.lockRunnable = null;
        }
        if (AndroidUtilities.needShowPasscode(true)) {
            showPasscodeActivity(true, false, -1, -1, null, null);
        }
        if (SharedConfig.lastPauseTime != 0) {
            SharedConfig.lastPauseTime = 0;
            SharedConfig.saveConfig();
            if (BuildVars.LOGS_ENABLED) {
                FileLog.d("reset lastPauseTime onPasscodeResume");
            }
        }
    }

    public void onPowerSaver(boolean z) {
        BaseFragment lastFragment;
        if (Build.VERSION.SDK_INT < 21 || this.actionBarLayout == null || !z || LiteMode.getPowerSaverLevel() >= 100 || (lastFragment = this.actionBarLayout.getLastFragment()) == null || (lastFragment instanceof LiteModeSettingsActivity)) {
            return;
        }
        int batteryLevel = LiteMode.getBatteryLevel();
        BulletinFactory.of(lastFragment).createSimpleBulletin(new BatteryDrawable(batteryLevel / 100.0f, -1, lastFragment.getThemedColor(Theme.key_dialogSwipeRemove), 1.3f), LocaleController.getString(R.string.LowPowerEnabledTitle), LocaleController.formatString("LowPowerEnabledSubtitle", R.string.LowPowerEnabledSubtitle, String.format("%d%%", Integer.valueOf(batteryLevel))), LocaleController.getString(R.string.Disable), new Runnable() {
            @Override
            public final void run() {
                LaunchActivity.this.lambda$onPowerSaver$136();
            }
        }).setDuration(5000).show();
    }

    private void onThemeLoadFinish() {
        AlertDialog alertDialog = this.loadingThemeProgressDialog;
        if (alertDialog != null) {
            try {
                alertDialog.dismiss();
            } finally {
                this.loadingThemeProgressDialog = null;
            }
        }
        this.loadingThemeWallpaperName = null;
        this.loadingThemeWallpaper = null;
        this.loadingThemeInfo = null;
        this.loadingThemeFileName = null;
        this.loadingTheme = null;
    }

    private void openDialogsToSend(boolean r11) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.LaunchActivity.openDialogsToSend(boolean):void");
    }

    private void openForumFromLink(long j, Integer num, Runnable runnable) {
        openForumFromLink(j, num, null, runnable, 0, -1);
    }

    private void openForumFromLink(final long j, final Integer num, final String str, final Runnable runnable, final int i, final int i2) {
        if (num != null) {
            TLRPC.TL_channels_getMessages tL_channels_getMessages = new TLRPC.TL_channels_getMessages();
            tL_channels_getMessages.channel = MessagesController.getInstance(this.currentAccount).getInputChannel(-j);
            tL_channels_getMessages.id.add(num);
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(tL_channels_getMessages, new RequestDelegate() {
                @Override
                public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                    LaunchActivity.this.lambda$openForumFromLink$127(num, j, runnable, str, i, i2, tLObject, tL_error);
                }
            });
            return;
        }
        Bundle bundle = new Bundle();
        bundle.putLong("chat_id", -j);
        lambda$runLinkRequest$93(TopicsFragment.getTopicsOrChat(this, bundle));
        if (runnable != null) {
            runnable.run();
        }
    }

    private void openMyStory(final int r12, final boolean r13) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.LaunchActivity.openMyStory(int, boolean):void");
    }

    private void openSettings(boolean z) {
        Bundle bundle = new Bundle();
        bundle.putLong("user_id", UserConfig.getInstance(this.currentAccount).clientUserId);
        if (z) {
            bundle.putBoolean("expandPhoto", true);
        }
        lambda$runLinkRequest$93(new ProfileActivity(bundle));
        this.drawerLayoutContainer.closeDrawer(false);
    }

    private void openStories(long[] jArr, boolean z) {
        boolean z2;
        StoriesListPlaceProvider of;
        final long[] jArr2 = jArr;
        int i = 0;
        while (true) {
            if (i >= jArr2.length) {
                z2 = true;
                break;
            }
            TLRPC.User user = MessagesController.getInstance(this.currentAccount).getUser(Long.valueOf(jArr2[i]));
            if (user != null && !user.stories_hidden) {
                z2 = false;
                break;
            }
            i++;
        }
        BaseFragment lastFragment = getLastFragment();
        if (lastFragment == null) {
            return;
        }
        StoriesController storiesController = MessagesController.getInstance(this.currentAccount).getStoriesController();
        ArrayList arrayList = new ArrayList(z2 ? storiesController.getHiddenList() : storiesController.getDialogListStories());
        ArrayList arrayList2 = new ArrayList();
        ArrayList arrayList3 = new ArrayList();
        if (!z2) {
            ArrayList arrayList4 = new ArrayList();
            for (int i2 = 0; i2 < jArr2.length; i2++) {
                TLRPC.User user2 = MessagesController.getInstance(this.currentAccount).getUser(Long.valueOf(jArr2[i2]));
                if (user2 == null || !user2.stories_hidden) {
                    arrayList4.add(Long.valueOf(jArr2[i2]));
                }
            }
            jArr2 = Longs.toArray(arrayList4);
        }
        int i3 = 0;
        if (z) {
            while (i3 < jArr2.length) {
                arrayList3.add(Long.valueOf(jArr2[i3]));
                i3++;
            }
        } else {
            while (i3 < jArr2.length) {
                arrayList2.add(Long.valueOf(jArr2[i3]));
                i3++;
            }
        }
        if (!arrayList3.isEmpty() && z) {
            final MessagesController messagesController = MessagesController.getInstance(this.currentAccount);
            final int[] iArr = {arrayList3.size()};
            final Runnable runnable = new Runnable() {
                @Override
                public final void run() {
                    LaunchActivity.this.lambda$openStories$167(iArr, jArr2);
                }
            };
            for (int i4 = 0; i4 < arrayList3.size(); i4++) {
                final long longValue = ((Long) arrayList3.get(i4)).longValue();
                TL_stories.TL_stories_getPeerStories tL_stories_getPeerStories = new TL_stories.TL_stories_getPeerStories();
                TLRPC.InputPeer inputPeer = messagesController.getInputPeer(longValue);
                tL_stories_getPeerStories.peer = inputPeer;
                if (inputPeer instanceof TLRPC.TL_inputPeerEmpty) {
                    iArr[0] = iArr[0] - 1;
                } else if (inputPeer == null) {
                    iArr[0] = iArr[0] - 1;
                } else {
                    ConnectionsManager.getInstance(this.currentAccount).sendRequest(tL_stories_getPeerStories, new RequestDelegate() {
                        @Override
                        public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                            LaunchActivity.lambda$openStories$169(MessagesController.this, longValue, runnable, tLObject, tL_error);
                        }
                    });
                }
            }
            return;
        }
        long clientUserId = UserConfig.getInstance(this.currentAccount).getClientUserId();
        for (int i5 = 0; i5 < arrayList.size(); i5++) {
            long peerDialogId = DialogObject.getPeerDialogId(((TL_stories.PeerStories) arrayList.get(i5)).peer);
            if (peerDialogId != clientUserId && !arrayList2.contains(Long.valueOf(peerDialogId)) && storiesController.hasUnreadStories(peerDialogId)) {
                arrayList2.add(Long.valueOf(peerDialogId));
            }
        }
        if (arrayList2.isEmpty()) {
            return;
        }
        if (lastFragment instanceof DialogsActivity) {
            try {
                of = StoriesListPlaceProvider.of(((DialogsActivity) lastFragment).dialogStoriesCell.recyclerListView);
            } catch (Exception unused) {
            }
            StoriesListPlaceProvider storiesListPlaceProvider = of;
            lastFragment.getOrCreateStoryViewer().instantClose();
            lastFragment.getOrCreateStoryViewer().open(this, null, arrayList2, 0, null, null, storiesListPlaceProvider, false);
        }
        of = null;
        StoriesListPlaceProvider storiesListPlaceProvider2 = of;
        lastFragment.getOrCreateStoryViewer().instantClose();
        lastFragment.getOrCreateStoryViewer().open(this, null, arrayList2, 0, null, null, storiesListPlaceProvider2, false);
    }

    private void openThemeAccentPreview(TLRPC.TL_theme tL_theme, TLRPC.TL_wallPaper tL_wallPaper, Theme.ThemeInfo themeInfo) {
        int i = themeInfo.lastAccentId;
        Theme.ThemeAccent createNewAccent = themeInfo.createNewAccent(tL_theme, this.currentAccount);
        themeInfo.prevAccentId = themeInfo.currentAccentId;
        themeInfo.setCurrentAccentId(createNewAccent.id);
        createNewAccent.pattern = tL_wallPaper;
        lambda$runLinkRequest$93(new ThemePreviewActivity(themeInfo, i != themeInfo.lastAccentId, 0, false, false));
    }

    private void openTopicRequest(final int r16, final int r17, final org.telegram.tgnet.TLRPC.Chat r18, final int r19, org.telegram.tgnet.TLRPC.TL_forumTopic r20, final java.lang.Runnable r21, final java.lang.String r22, final int r23, final java.util.ArrayList r24, final int r25) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.LaunchActivity.openTopicRequest(int, int, org.telegram.tgnet.TLRPC$Chat, int, org.telegram.tgnet.TLRPC$TL_forumTopic, java.lang.Runnable, java.lang.String, int, java.util.ArrayList, int):void");
    }

    private void processAttachMenuBot(final int i, final long j, final String str, final TLRPC.User user, final String str2, final String str3) {
        TLRPC.TL_messages_getAttachMenuBot tL_messages_getAttachMenuBot = new TLRPC.TL_messages_getAttachMenuBot();
        tL_messages_getAttachMenuBot.bot = MessagesController.getInstance(i).getInputUser(j);
        ConnectionsManager.getInstance(i).sendRequest(tL_messages_getAttachMenuBot, new RequestDelegate() {
            @Override
            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                LaunchActivity.this.lambda$processAttachMenuBot$125(i, str3, str, user, str2, j, tLObject, tL_error);
            }
        });
    }

    private void processAttachedMenuBotFromShortcut(final long j) {
        for (int i = 0; i < this.visibleDialogs.size(); i++) {
            if (this.visibleDialogs.get(i) instanceof BotWebViewSheet) {
                BotWebViewSheet botWebViewSheet = (BotWebViewSheet) this.visibleDialogs.get(i);
                if (botWebViewSheet.isShowing() && botWebViewSheet.getBotId() == j) {
                    return;
                }
            }
        }
        BaseFragment safeLastFragment = getSafeLastFragment();
        if (safeLastFragment != null && safeLastFragment.sheetsStack != null) {
            for (int i2 = 0; i2 < safeLastFragment.sheetsStack.size(); i2++) {
                if (((BaseFragment.AttachedSheet) safeLastFragment.sheetsStack.get(i2)).isShown()) {
                    safeLastFragment.sheetsStack.get(i2);
                }
            }
        }
        EmptyBaseFragment sheetFragment = this.actionBarLayout.getSheetFragment(false);
        if (sheetFragment != null && sheetFragment.sheetsStack != null) {
            for (int i3 = 0; i3 < sheetFragment.sheetsStack.size(); i3++) {
                if (((BaseFragment.AttachedSheet) sheetFragment.sheetsStack.get(i3)).isShown()) {
                    sheetFragment.sheetsStack.get(i3);
                }
            }
        }
        final Utilities.Callback callback = new Utilities.Callback() {
            @Override
            public final void run(Object obj) {
                LaunchActivity.this.lambda$processAttachedMenuBotFromShortcut$114((TLRPC.User) obj);
            }
        };
        TLRPC.User user = MessagesController.getInstance(this.currentAccount).getUser(Long.valueOf(j));
        if (user != null) {
            callback.run(user);
        } else {
            MessagesStorage.getInstance(this.currentAccount).getStorageQueue().postRunnable(new Runnable() {
                @Override
                public final void run() {
                    LaunchActivity.this.lambda$processAttachedMenuBotFromShortcut$116(j, callback);
                }
            });
        }
    }

    private void processBoostDialog(Long l, Runnable runnable, Browser.Progress progress) {
        processBoostDialog(l, runnable, progress, null);
    }

    private void processBoostDialog(final Long l, final Runnable runnable, final Browser.Progress progress, final ChatMessageCell chatMessageCell) {
        final ChannelBoostsController boostsController = MessagesController.getInstance(this.currentAccount).getBoostsController();
        if (progress != null) {
            progress.init();
        }
        boostsController.getBoostsStats(l.longValue(), new com.google.android.exoplayer2.util.Consumer() {
            @Override
            public final void accept(Object obj) {
                LaunchActivity.this.lambda$processBoostDialog$118(progress, runnable, boostsController, l, chatMessageCell, (TL_stories.TL_premium_boostsStatus) obj);
            }
        });
    }

    private void processWebAppBot(final int i, final String str, final String str2, final String str3, final String str4, final String str5, final String str6, final String str7, final String str8, final String str9, final String str10, final String str11, final String str12, final boolean z, final Integer num, final Long l, final Long l2, final Integer num2, final String str13, final HashMap hashMap, final String str14, final String str15, final String str16, final String str17, final TLRPC.TL_wallPaper tL_wallPaper, final String str18, final String str19, final String str20, final boolean z2, final String str21, final int i2, final int i3, final String str22, final String str23, final String str24, String str25, final String str26, final Browser.Progress progress, final boolean z3, final int i4, final boolean z4, final String str27, final TLRPC.User user, final Runnable runnable, final boolean z5, final boolean z6, final boolean z7, final boolean z8, final boolean z9, final boolean z10, final boolean z11, final String str28) {
        TLRPC.TL_messages_getBotApp tL_messages_getBotApp = new TLRPC.TL_messages_getBotApp();
        TLRPC.TL_inputBotAppShortName tL_inputBotAppShortName = new TLRPC.TL_inputBotAppShortName();
        tL_inputBotAppShortName.bot_id = MessagesController.getInstance(i).getInputUser(user);
        tL_inputBotAppShortName.short_name = str25;
        tL_messages_getBotApp.app = tL_inputBotAppShortName;
        ConnectionsManager.getInstance(i).sendRequest(tL_messages_getBotApp, new RequestDelegate() {
            @Override
            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                LaunchActivity.this.lambda$processWebAppBot$113(progress, i, str, str2, str3, str4, str5, str6, str7, str8, str9, str10, str11, str12, z, num, l, l2, num2, str13, hashMap, str14, str15, str16, str17, tL_wallPaper, str18, str19, str20, z2, str21, i2, i3, str22, str23, str24, z3, i4, z4, str27, z7, z8, z9, z10, z11, str28, runnable, user, str26, z6, z5, tLObject, tL_error);
            }
        });
    }

    private java.lang.String readImport(android.net.Uri r8) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.LaunchActivity.readImport(android.net.Uri):java.lang.String");
    }

    private int runCommentRequest(int i, Runnable runnable, Integer num, Integer num2, Long l, TLRPC.Chat chat) {
        return runCommentRequest(i, runnable, num, num2, l, chat, null, null, 0, -1);
    }

    private int runCommentRequest(final int i, final Runnable runnable, final Integer num, final Integer num2, final Long l, final TLRPC.Chat chat, final Runnable runnable2, final String str, final int i2, final int i3) {
        if (chat == null) {
            return 0;
        }
        final TLRPC.TL_messages_getDiscussionMessage tL_messages_getDiscussionMessage = new TLRPC.TL_messages_getDiscussionMessage();
        tL_messages_getDiscussionMessage.peer = MessagesController.getInputPeer(chat);
        tL_messages_getDiscussionMessage.msg_id = num2 != null ? num.intValue() : (int) l.longValue();
        return ConnectionsManager.getInstance(i).sendRequest(tL_messages_getDiscussionMessage, new RequestDelegate() {
            @Override
            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                LaunchActivity.this.lambda$runCommentRequest$33(i, chat, l, num2, num, runnable2, str, i2, i3, tL_messages_getDiscussionMessage, runnable, tLObject, tL_error);
            }
        });
    }

    private void runImportRequest(final Uri uri, ArrayList arrayList) {
        final int i = UserConfig.selectedAccount;
        final AlertDialog alertDialog = new AlertDialog(this, 3);
        final int[] iArr = {0};
        String readImport = readImport(uri);
        if (readImport == null) {
            return;
        }
        TLRPC.TL_messages_checkHistoryImport tL_messages_checkHistoryImport = new TLRPC.TL_messages_checkHistoryImport();
        tL_messages_checkHistoryImport.import_head = readImport;
        iArr[0] = ConnectionsManager.getInstance(i).sendRequest(tL_messages_checkHistoryImport, new RequestDelegate() {
            @Override
            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                LaunchActivity.this.lambda$runImportRequest$37(uri, i, alertDialog, tLObject, tL_error);
            }
        });
        final Runnable runnable = null;
        alertDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public final void onCancel(DialogInterface dialogInterface) {
                LaunchActivity.lambda$runImportRequest$38(i, iArr, runnable, dialogInterface);
            }
        });
        try {
            alertDialog.showDelayed(300L);
        } catch (Exception unused) {
        }
    }

    private void runLinkRequest(final int r55, final java.lang.String r56, final java.lang.String r57, final java.lang.String r58, final java.lang.String r59, final java.lang.String r60, final java.lang.String r61, final java.lang.String r62, final java.lang.String r63, final java.lang.String r64, final java.lang.String r65, final java.lang.String r66, final java.lang.String r67, final boolean r68, final java.lang.Integer r69, final java.lang.Long r70, final java.lang.Long r71, final java.lang.Integer r72, final java.lang.String r73, final java.util.HashMap r74, final java.lang.String r75, final java.lang.String r76, final java.lang.String r77, final java.lang.String r78, final org.telegram.tgnet.TLRPC.TL_wallPaper r79, final java.lang.String r80, final java.lang.String r81, final java.lang.String r82, final boolean r83, final java.lang.String r84, final int r85, final int r86, final java.lang.String r87, final java.lang.String r88, final java.lang.String r89, final java.lang.String r90, final java.lang.String r91, final org.telegram.messenger.browser.Browser.Progress r92, final boolean r93, final int r94, final boolean r95, final java.lang.String r96, final boolean r97, final boolean r98, final boolean r99, final boolean r100, final boolean r101, final java.lang.String r102) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.LaunchActivity.runLinkRequest(int, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, boolean, java.lang.Integer, java.lang.Long, java.lang.Long, java.lang.Integer, java.lang.String, java.util.HashMap, java.lang.String, java.lang.String, java.lang.String, java.lang.String, org.telegram.tgnet.TLRPC$TL_wallPaper, java.lang.String, java.lang.String, java.lang.String, boolean, java.lang.String, int, int, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, org.telegram.messenger.browser.Browser$Progress, boolean, int, boolean, java.lang.String, boolean, boolean, boolean, boolean, boolean, java.lang.String):void");
    }

    private void setupActionBarLayout() {
        DrawerLayoutContainer drawerLayoutContainer;
        ViewGroup view;
        if (this.drawerLayoutContainer.indexOfChild(this.launchLayout) != -1) {
            drawerLayoutContainer = this.drawerLayoutContainer;
            view = this.launchLayout;
        } else {
            drawerLayoutContainer = this.drawerLayoutContainer;
            view = this.actionBarLayout.getView();
        }
        int indexOfChild = drawerLayoutContainer.indexOfChild(view);
        if (indexOfChild != -1) {
            this.drawerLayoutContainer.removeViewAt(indexOfChild);
        }
        if (AndroidUtilities.isTablet()) {
            getWindow().setSoftInputMode(16);
            AnonymousClass10 anonymousClass10 = new RelativeLayout(this) {
                private boolean inLayout;
                private Path path = new Path();

                AnonymousClass10(Context this) {
                    super(this);
                    this.path = new Path();
                }

                @Override
                protected void dispatchDraw(Canvas canvas) {
                    if (LaunchActivity.this.layersActionBarLayout != null) {
                        LaunchActivity.this.layersActionBarLayout.parentDraw(this, canvas);
                    }
                    super.dispatchDraw(canvas);
                }

                @Override
                protected void onLayout(boolean z, int i, int i2, int i3, int i4) {
                    int i5 = i3 - i;
                    int i6 = i4 - i2;
                    if (AndroidUtilities.isInMultiwindow || (AndroidUtilities.isSmallTablet() && getResources().getConfiguration().orientation != 2)) {
                        LaunchActivity.this.actionBarLayout.getView().layout(0, 0, LaunchActivity.this.actionBarLayout.getView().getMeasuredWidth(), LaunchActivity.this.actionBarLayout.getView().getMeasuredHeight());
                    } else {
                        int i7 = (i5 / 100) * 35;
                        if (i7 < AndroidUtilities.dp(320.0f)) {
                            i7 = AndroidUtilities.dp(320.0f);
                        }
                        LaunchActivity.this.shadowTabletSide.layout(i7, 0, LaunchActivity.this.shadowTabletSide.getMeasuredWidth() + i7, LaunchActivity.this.shadowTabletSide.getMeasuredHeight());
                        LaunchActivity.this.actionBarLayout.getView().layout(0, 0, LaunchActivity.this.actionBarLayout.getView().getMeasuredWidth(), LaunchActivity.this.actionBarLayout.getView().getMeasuredHeight());
                        LaunchActivity.this.rightActionBarLayout.getView().layout(i7, 0, LaunchActivity.this.rightActionBarLayout.getView().getMeasuredWidth() + i7, LaunchActivity.this.rightActionBarLayout.getView().getMeasuredHeight());
                    }
                    int measuredWidth = (i5 - LaunchActivity.this.layersActionBarLayout.getView().getMeasuredWidth()) / 2;
                    int measuredHeight = ((i6 - LaunchActivity.this.layersActionBarLayout.getView().getMeasuredHeight()) + AndroidUtilities.statusBarHeight) / 2;
                    LaunchActivity.this.layersActionBarLayout.getView().layout(measuredWidth, measuredHeight, LaunchActivity.this.layersActionBarLayout.getView().getMeasuredWidth() + measuredWidth, LaunchActivity.this.layersActionBarLayout.getView().getMeasuredHeight() + measuredHeight);
                    LaunchActivity.this.backgroundTablet.layout(0, 0, LaunchActivity.this.backgroundTablet.getMeasuredWidth(), LaunchActivity.this.backgroundTablet.getMeasuredHeight());
                    LaunchActivity.this.shadowTablet.layout(0, 0, LaunchActivity.this.shadowTablet.getMeasuredWidth(), LaunchActivity.this.shadowTablet.getMeasuredHeight());
                }

                @Override
                protected void onMeasure(int i, int i2) {
                    this.inLayout = true;
                    int size = View.MeasureSpec.getSize(i);
                    int size2 = View.MeasureSpec.getSize(i2);
                    setMeasuredDimension(size, size2);
                    if (AndroidUtilities.isInMultiwindow || (AndroidUtilities.isSmallTablet() && getResources().getConfiguration().orientation != 2)) {
                        LaunchActivity.this.tabletFullSize = true;
                        LaunchActivity.this.actionBarLayout.getView().measure(View.MeasureSpec.makeMeasureSpec(size, 1073741824), View.MeasureSpec.makeMeasureSpec(size2, 1073741824));
                    } else {
                        LaunchActivity.this.tabletFullSize = false;
                        int i3 = (size / 100) * 35;
                        if (i3 < AndroidUtilities.dp(320.0f)) {
                            i3 = AndroidUtilities.dp(320.0f);
                        }
                        LaunchActivity.this.actionBarLayout.getView().measure(View.MeasureSpec.makeMeasureSpec(i3, 1073741824), View.MeasureSpec.makeMeasureSpec(size2, 1073741824));
                        LaunchActivity.this.shadowTabletSide.measure(View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(1.0f), 1073741824), View.MeasureSpec.makeMeasureSpec(size2, 1073741824));
                        LaunchActivity.this.rightActionBarLayout.getView().measure(View.MeasureSpec.makeMeasureSpec(size - i3, 1073741824), View.MeasureSpec.makeMeasureSpec(size2, 1073741824));
                    }
                    LaunchActivity.this.backgroundTablet.measure(View.MeasureSpec.makeMeasureSpec(size, 1073741824), View.MeasureSpec.makeMeasureSpec(size2, 1073741824));
                    LaunchActivity.this.shadowTablet.measure(View.MeasureSpec.makeMeasureSpec(size, 1073741824), View.MeasureSpec.makeMeasureSpec(size2, 1073741824));
                    LaunchActivity.this.layersActionBarLayout.getView().measure(View.MeasureSpec.makeMeasureSpec(Math.min(AndroidUtilities.dp(530.0f), size - AndroidUtilities.dp(16.0f)), 1073741824), View.MeasureSpec.makeMeasureSpec((size2 - AndroidUtilities.statusBarHeight) - AndroidUtilities.dp(16.0f), 1073741824));
                    this.inLayout = false;
                }

                @Override
                public void requestLayout() {
                    if (this.inLayout) {
                        return;
                    }
                    super.requestLayout();
                }
            };
            this.launchLayout = anonymousClass10;
            if (indexOfChild != -1) {
                this.drawerLayoutContainer.addView(anonymousClass10, indexOfChild, LayoutHelper.createFrame(-1, -1.0f));
            } else {
                this.drawerLayoutContainer.addView(anonymousClass10, LayoutHelper.createFrame(-1, -1.0f));
            }
            AnonymousClass11 anonymousClass11 = new SizeNotifierFrameLayout(this) {
                AnonymousClass11(Context this) {
                    super(this);
                }

                @Override
                protected boolean isActionBarVisible() {
                    return false;
                }
            };
            this.backgroundTablet = anonymousClass11;
            anonymousClass11.setOccupyStatusBar(false);
            this.backgroundTablet.setBackgroundImage(Theme.getCachedWallpaper(), Theme.isWallpaperMotion());
            this.launchLayout.addView(this.backgroundTablet, LayoutHelper.createRelative(-1, -1));
            ViewGroup viewGroup = (ViewGroup) this.actionBarLayout.getView().getParent();
            if (viewGroup != null) {
                viewGroup.removeView(this.actionBarLayout.getView());
            }
            this.launchLayout.addView(this.actionBarLayout.getView());
            ActionBarLayout actionBarLayout = new ActionBarLayout(this, false);
            this.rightActionBarLayout = actionBarLayout;
            actionBarLayout.setFragmentStack(rightFragmentsStack);
            this.rightActionBarLayout.setDelegate(this);
            this.launchLayout.addView(this.rightActionBarLayout.getView());
            FrameLayout frameLayout = new FrameLayout(this);
            this.shadowTabletSide = frameLayout;
            frameLayout.setBackgroundColor(1076449908);
            this.launchLayout.addView(this.shadowTabletSide);
            FrameLayout frameLayout2 = new FrameLayout(this);
            this.shadowTablet = frameLayout2;
            ArrayList arrayList = layerFragmentsStack;
            frameLayout2.setVisibility(arrayList.isEmpty() ? 8 : 0);
            this.shadowTablet.setBackgroundColor(2130706432);
            this.launchLayout.addView(this.shadowTablet);
            this.shadowTablet.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public final boolean onTouch(View view2, MotionEvent motionEvent) {
                    boolean lambda$setupActionBarLayout$10;
                    lambda$setupActionBarLayout$10 = LaunchActivity.this.lambda$setupActionBarLayout$10(view2, motionEvent);
                    return lambda$setupActionBarLayout$10;
                }
            });
            this.shadowTablet.setOnClickListener(new View.OnClickListener() {
                @Override
                public final void onClick(View view2) {
                    LaunchActivity.lambda$setupActionBarLayout$11(view2);
                }
            });
            ActionBarLayout actionBarLayout2 = new ActionBarLayout(this, false);
            this.layersActionBarLayout = actionBarLayout2;
            actionBarLayout2.setRemoveActionBarExtraHeight(true);
            this.layersActionBarLayout.setBackgroundView(this.shadowTablet);
            this.layersActionBarLayout.setUseAlphaAnimations(true);
            this.layersActionBarLayout.setFragmentStack(arrayList);
            this.layersActionBarLayout.setDelegate(this);
            this.layersActionBarLayout.setDrawerLayoutContainer(this.drawerLayoutContainer);
            ViewGroup view2 = this.layersActionBarLayout.getView();
            view2.setBackgroundResource(R.drawable.popup_fixed_alert3);
            view2.setVisibility(arrayList.isEmpty() ? 8 : 0);
            this.launchLayout.addView(view2);
        } else {
            ViewGroup viewGroup2 = (ViewGroup) this.actionBarLayout.getView().getParent();
            if (viewGroup2 != null) {
                viewGroup2.removeView(this.actionBarLayout.getView());
            }
            this.actionBarLayout.setFragmentStack(mainFragmentsStack);
            if (indexOfChild != -1) {
                this.drawerLayoutContainer.addView(this.actionBarLayout.getView(), indexOfChild, new ViewGroup.LayoutParams(-1, -1));
            } else {
                this.drawerLayoutContainer.addView(this.actionBarLayout.getView(), new ViewGroup.LayoutParams(-1, -1));
            }
        }
        FloatingDebugController.setActive(this, SharedConfig.isFloatingDebugActive, false);
    }

    private void showAttachMenuBot(TLRPC.TL_attachMenuBot tL_attachMenuBot, String str, boolean z) {
        this.drawerLayoutContainer.closeDrawer();
        BaseFragment lastFragment = getLastFragment();
        if (lastFragment == null) {
            return;
        }
        int i = this.currentAccount;
        long j = tL_attachMenuBot.bot_id;
        WebViewRequestProps of = WebViewRequestProps.of(i, j, j, tL_attachMenuBot.short_name, null, 1, 0, false, null, false, str, null, 2, false, false);
        if (getBottomSheetTabs() == null || getBottomSheetTabs().tryReopenTab(of) == null) {
            BotWebViewSheet botWebViewSheet = new BotWebViewSheet(this, lastFragment.getResourceProvider());
            botWebViewSheet.setNeedsContext(false);
            botWebViewSheet.setDefaultFullsize(z);
            botWebViewSheet.setParentActivity(this);
            botWebViewSheet.requestWebView(lastFragment, of);
            botWebViewSheet.show();
        }
    }

    private void showLanguageAlert(boolean z) {
        LocaleController.LocaleInfo localeInfo;
        if (UserConfig.getInstance(this.currentAccount).isClientActivated()) {
            try {
                if (!this.loadingLocaleDialog && !ApplicationLoader.mainInterfacePaused) {
                    String string = MessagesController.getGlobalMainSettings().getString("language_showed2", "");
                    final String str = MessagesController.getInstance(this.currentAccount).suggestedLangCode;
                    if (!z && string.equals(str)) {
                        if (BuildVars.LOGS_ENABLED) {
                            FileLog.d("alert already showed for " + string);
                            return;
                        }
                        return;
                    }
                    final LocaleController.LocaleInfo[] localeInfoArr = new LocaleController.LocaleInfo[2];
                    String str2 = str.contains("-") ? str.split("-")[0] : str;
                    String str3 = "in".equals(str2) ? "id" : "iw".equals(str2) ? "he" : "jw".equals(str2) ? "jv" : null;
                    for (int i = 0; i < LocaleController.getInstance().languages.size(); i++) {
                        LocaleController.LocaleInfo localeInfo2 = LocaleController.getInstance().languages.get(i);
                        if (localeInfo2.shortName.equals("en")) {
                            localeInfoArr[0] = localeInfo2;
                        }
                        if (localeInfo2.shortName.replace("_", "-").equals(str) || localeInfo2.shortName.equals(str2) || localeInfo2.shortName.equals(str3)) {
                            localeInfoArr[1] = localeInfo2;
                        }
                        if (localeInfoArr[0] != null && localeInfoArr[1] != null) {
                            break;
                        }
                    }
                    LocaleController.LocaleInfo localeInfo3 = localeInfoArr[0];
                    if (localeInfo3 != null && (localeInfo = localeInfoArr[1]) != null && localeInfo3 != localeInfo) {
                        if (BuildVars.LOGS_ENABLED) {
                            FileLog.d("show lang alert for " + localeInfoArr[0].getKey() + " and " + localeInfoArr[1].getKey());
                        }
                        this.systemLocaleStrings = null;
                        this.englishLocaleStrings = null;
                        this.loadingLocaleDialog = true;
                        TLRPC.TL_langpack_getStrings tL_langpack_getStrings = new TLRPC.TL_langpack_getStrings();
                        tL_langpack_getStrings.lang_code = localeInfoArr[1].getLangCode();
                        tL_langpack_getStrings.keys.add("English");
                        tL_langpack_getStrings.keys.add("ChooseYourLanguage");
                        tL_langpack_getStrings.keys.add("ChooseYourLanguageOther");
                        tL_langpack_getStrings.keys.add("ChangeLanguageLater");
                        ConnectionsManager.getInstance(this.currentAccount).sendRequest(tL_langpack_getStrings, new RequestDelegate() {
                            @Override
                            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                                LaunchActivity.this.lambda$showLanguageAlert$160(localeInfoArr, str, tLObject, tL_error);
                            }
                        }, 8);
                        TLRPC.TL_langpack_getStrings tL_langpack_getStrings2 = new TLRPC.TL_langpack_getStrings();
                        tL_langpack_getStrings2.lang_code = localeInfoArr[0].getLangCode();
                        tL_langpack_getStrings2.keys.add("English");
                        tL_langpack_getStrings2.keys.add("ChooseYourLanguage");
                        tL_langpack_getStrings2.keys.add("ChooseYourLanguageOther");
                        tL_langpack_getStrings2.keys.add("ChangeLanguageLater");
                        ConnectionsManager.getInstance(this.currentAccount).sendRequest(tL_langpack_getStrings2, new RequestDelegate() {
                            @Override
                            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                                LaunchActivity.this.lambda$showLanguageAlert$162(localeInfoArr, str, tLObject, tL_error);
                            }
                        }, 8);
                    }
                }
            } catch (Exception e) {
                FileLog.e(e);
            }
        }
    }

    private void showLanguageAlertInternal(org.telegram.messenger.LocaleController.LocaleInfo r18, org.telegram.messenger.LocaleController.LocaleInfo r19, java.lang.String r20) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.LaunchActivity.showLanguageAlertInternal(org.telegram.messenger.LocaleController$LocaleInfo, org.telegram.messenger.LocaleController$LocaleInfo, java.lang.String):void");
    }

    private void showTosActivity(int i, TLRPC.TL_help_termsOfService tL_help_termsOfService) {
        if (this.termsOfServiceView == null) {
            TermsOfServiceView termsOfServiceView = new TermsOfServiceView(this);
            this.termsOfServiceView = termsOfServiceView;
            termsOfServiceView.setAlpha(0.0f);
            this.drawerLayoutContainer.addView(this.termsOfServiceView, LayoutHelper.createFrame(-1, -1.0f));
            this.termsOfServiceView.setDelegate(new AnonymousClass15());
        }
        TLRPC.TL_help_termsOfService tL_help_termsOfService2 = UserConfig.getInstance(i).unacceptedTermsOfService;
        if (tL_help_termsOfService2 != tL_help_termsOfService && (tL_help_termsOfService2 == null || !tL_help_termsOfService2.id.data.equals(tL_help_termsOfService.id.data))) {
            UserConfig.getInstance(i).unacceptedTermsOfService = tL_help_termsOfService;
            UserConfig.getInstance(i).saveConfig(false);
        }
        this.termsOfServiceView.show(i, tL_help_termsOfService);
        this.drawerLayoutContainer.setAllowOpenDrawer(false, false);
        this.termsOfServiceView.animate().alpha(1.0f).setDuration(150L).setInterpolator(AndroidUtilities.decelerateInterpolator).setListener(null).start();
    }

    private void showUpdateActivity(int i, TLRPC.TL_help_appUpdate tL_help_appUpdate, boolean z) {
        if (this.blockingUpdateView == null) {
            AnonymousClass14 anonymousClass14 = new BlockingUpdateView(this) {
                AnonymousClass14(Context this) {
                    super(this);
                }

                @Override
                public void setVisibility(int i2) {
                    super.setVisibility(i2);
                    if (i2 == 8) {
                        LaunchActivity.this.drawerLayoutContainer.setAllowOpenDrawer(true, false);
                    }
                }
            };
            this.blockingUpdateView = anonymousClass14;
            this.drawerLayoutContainer.addView(anonymousClass14, LayoutHelper.createFrame(-1, -1.0f));
        }
        this.blockingUpdateView.show(i, tL_help_appUpdate, z);
        this.drawerLayoutContainer.setAllowOpenDrawer(false, false);
    }

    private void showVoiceChatTooltip(int i) {
        VoIPService sharedInstance = VoIPService.getSharedInstance();
        if (sharedInstance == null || mainFragmentsStack.isEmpty() || sharedInstance.groupCall == null) {
            return;
        }
        TLRPC.Chat chat = sharedInstance.getChat();
        BaseFragment baseFragment = this.actionBarLayout.getFragmentStack().get(this.actionBarLayout.getFragmentStack().size() - 1);
        UndoView undoView = null;
        if (baseFragment instanceof ChatActivity) {
            ChatActivity chatActivity = (ChatActivity) baseFragment;
            if (chatActivity.getDialogId() == (-chat.id)) {
                chat = null;
            }
            undoView = chatActivity.getUndoView();
        } else if (baseFragment instanceof DialogsActivity) {
            undoView = ((DialogsActivity) baseFragment).getUndoView();
        } else if (baseFragment instanceof ProfileActivity) {
            undoView = ((ProfileActivity) baseFragment).getUndoView();
        }
        if (undoView != null) {
            undoView.showWithAction(0L, i, chat);
        }
        if (i != 38 || VoIPService.getSharedInstance() == null) {
            return;
        }
        VoIPService.getSharedInstance().playAllowTalkSound();
    }

    private void switchToAvailableAccountOrLogout() {
        int i = 0;
        while (true) {
            if (i >= 4) {
                i = -1;
                break;
            } else if (UserConfig.getInstance(i).isClientActivated()) {
                break;
            } else {
                i++;
            }
        }
        TermsOfServiceView termsOfServiceView = this.termsOfServiceView;
        if (termsOfServiceView != null) {
            termsOfServiceView.setVisibility(8);
        }
        if (i != -1) {
            switchToAccount(i, true);
            return;
        }
        DrawerLayoutAdapter drawerLayoutAdapter = this.drawerLayoutAdapter;
        if (drawerLayoutAdapter != null) {
            drawerLayoutAdapter.notifyDataSetChanged();
        }
        RestrictedLanguagesSelectActivity.checkRestrictedLanguages(true);
        clearFragments();
        this.actionBarLayout.rebuildLogout();
        if (AndroidUtilities.isTablet()) {
            this.layersActionBarLayout.rebuildLogout();
            this.rightActionBarLayout.rebuildLogout();
        }
        lambda$runLinkRequest$93(new IntroActivity().setOnLogout());
    }

    private void updateCurrentConnectionState(int i) {
        int i2;
        String str;
        if (this.actionBarLayout == null) {
            return;
        }
        int connectionState = ConnectionsManager.getInstance(this.currentAccount).getConnectionState();
        this.currentConnectionState = connectionState;
        if (connectionState == 2) {
            i2 = R.string.WaitingForNetwork;
            str = "WaitingForNetwork";
        } else if (connectionState == 5) {
            i2 = R.string.Updating;
            str = "Updating";
        } else if (connectionState == 4) {
            i2 = R.string.ConnectingToProxy;
            str = "ConnectingToProxy";
        } else if (connectionState == 1) {
            i2 = R.string.Connecting;
            str = "Connecting";
        } else {
            i2 = 0;
            str = null;
        }
        this.actionBarLayout.setTitleOverlayText(str, i2, (connectionState == 1 || connectionState == 4) ? new Runnable() {
            @Override
            public final void run() {
                LaunchActivity.this.lambda$updateCurrentConnectionState$163();
            }
        } : null);
    }

    public void addOnUserLeaveHintListener(Runnable runnable) {
        this.onUserLeaveHintListeners.add(runnable);
    }

    public void addOverlayPasscodeView(PasscodeView passcodeView) {
        this.overlayPasscodeViews.add(passcodeView);
    }

    public boolean allowShowFingerprintDialog(PasscodeView passcodeView) {
        PasscodeViewDialog passcodeViewDialog;
        if (!this.overlayPasscodeViews.isEmpty() || (passcodeViewDialog = this.passcodeDialog) == null) {
            List list = this.overlayPasscodeViews;
            if (list.get(list.size() - 1) == passcodeView) {
                return true;
            }
        } else if (passcodeView == passcodeViewDialog.passcodeView) {
            return true;
        }
        return false;
    }

    public void animateNavigationBarColor(int i) {
        ValueAnimator ofArgb;
        if (Build.VERSION.SDK_INT < 26) {
            return;
        }
        ValueAnimator valueAnimator = this.navBarAnimator;
        if (valueAnimator != null) {
            valueAnimator.cancel();
            this.navBarAnimator = null;
        }
        ofArgb = ValueAnimator.ofArgb(getNavigationBarColor(), i);
        this.navBarAnimator = ofArgb;
        ofArgb.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public final void onAnimationUpdate(ValueAnimator valueAnimator2) {
                LaunchActivity.this.lambda$animateNavigationBarColor$164(valueAnimator2);
            }
        });
        this.navBarAnimator.addListener(new AnimatorListenerAdapter() {
            final int val$toColor;

            AnonymousClass23(int i2) {
                r2 = i2;
            }

            @Override
            public void onAnimationEnd(Animator animator) {
                LaunchActivity.this.setNavigationBarColor(r2, false);
            }
        });
        this.navBarAnimator.setInterpolator(CubicBezierInterpolator.EASE_OUT_QUINT);
        this.navBarAnimator.setDuration(320L);
        this.navBarAnimator.start();
    }

    public void checkAppUpdate(boolean z, final Browser.Progress progress) {
        if (z || !BuildVars.DEBUG_VERSION) {
            if (z || BuildVars.CHECK_UPDATES) {
                if (z || Math.abs(System.currentTimeMillis() - SharedConfig.lastUpdateCheckTime) >= MessagesController.getInstance(0).updateCheckDelay * 1000) {
                    TLRPC.TL_help_getAppUpdate tL_help_getAppUpdate = new TLRPC.TL_help_getAppUpdate();
                    try {
                        tL_help_getAppUpdate.source = ApplicationLoader.applicationContext.getPackageManager().getInstallerPackageName(ApplicationLoader.applicationContext.getPackageName());
                    } catch (Exception unused) {
                    }
                    if (tL_help_getAppUpdate.source == null) {
                        tL_help_getAppUpdate.source = "";
                    }
                    final int i = this.currentAccount;
                    final int sendRequest = ConnectionsManager.getInstance(i).sendRequest(tL_help_getAppUpdate, new RequestDelegate() {
                        @Override
                        public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                            LaunchActivity.this.lambda$checkAppUpdate$131(i, progress, tLObject, tL_error);
                        }
                    });
                    if (progress != null) {
                        progress.init();
                        progress.onCancel(new Runnable() {
                            @Override
                            public final void run() {
                                LaunchActivity.this.lambda$checkAppUpdate$132(sendRequest);
                            }
                        });
                    }
                }
            }
        }
    }

    public void checkSystemBarColors(boolean z) {
        checkSystemBarColors(z, true, !this.isNavigationBarColorFrozen, true);
    }

    public void checkSystemBarColors(boolean r12, boolean r13, boolean r14, boolean r15) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.LaunchActivity.checkSystemBarColors(boolean, boolean, boolean, boolean):void");
    }

    @Override
    public void didReceivedNotification(int r21, final int r22, java.lang.Object... r23) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.LaunchActivity.didReceivedNotification(int, int, java.lang.Object[]):void");
    }

    @Override
    public boolean didSelectDialogs(final org.telegram.ui.DialogsActivity r40, final java.util.ArrayList r41, final java.lang.CharSequence r42, final boolean r43, boolean r44, int r45, org.telegram.ui.TopicsFragment r46) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.LaunchActivity.didSelectDialogs(org.telegram.ui.DialogsActivity, java.util.ArrayList, java.lang.CharSequence, boolean, boolean, int, org.telegram.ui.TopicsFragment):boolean");
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent keyEvent) {
        BaseFragment lastFragment;
        int streamMinVolume;
        keyEvent.getKeyCode();
        if ((keyEvent.getKeyCode() == 24 || keyEvent.getKeyCode() == 25) && (lastFragment = getLastFragment()) != null && lastFragment.getLastStoryViewer() != null) {
            lastFragment.getLastStoryViewer().dispatchKeyEvent(keyEvent);
            return true;
        }
        if (keyEvent.getAction() == 0 && (keyEvent.getKeyCode() == 24 || keyEvent.getKeyCode() == 25)) {
            if (VoIPService.getSharedInstance() == null) {
                ArrayList arrayList = mainFragmentsStack;
                if (!arrayList.isEmpty() && ((!PhotoViewer.hasInstance() || !PhotoViewer.getInstance().isVisible()) && keyEvent.getRepeatCount() == 0)) {
                    BaseFragment baseFragment = (BaseFragment) arrayList.get(arrayList.size() - 1);
                    if ((baseFragment instanceof ChatActivity) && !BaseFragment.hasSheets(baseFragment) && ((ChatActivity) baseFragment).maybePlayVisibleVideo()) {
                        return true;
                    }
                    if (AndroidUtilities.isTablet()) {
                        ArrayList arrayList2 = rightFragmentsStack;
                        if (!arrayList2.isEmpty()) {
                            BaseFragment baseFragment2 = (BaseFragment) arrayList2.get(arrayList2.size() - 1);
                            if ((baseFragment2 instanceof ChatActivity) && !BaseFragment.hasSheets(baseFragment2) && ((ChatActivity) baseFragment2).maybePlayVisibleVideo()) {
                                return true;
                            }
                        }
                    }
                }
            } else if (Build.VERSION.SDK_INT >= 32) {
                boolean isSpeakerMuted = WebRtcAudioTrack.isSpeakerMuted();
                AudioManager audioManager = (AudioManager) getSystemService("audio");
                streamMinVolume = audioManager.getStreamMinVolume(0);
                boolean z = audioManager.getStreamVolume(0) == streamMinVolume && keyEvent.getKeyCode() == 25;
                WebRtcAudioTrack.setSpeakerMute(z);
                if (isSpeakerMuted != WebRtcAudioTrack.isSpeakerMuted()) {
                    showVoiceChatTooltip(z ? 42 : 43);
                }
            }
        }
        try {
            return super.dispatchKeyEvent(keyEvent);
        } catch (Exception e) {
            FileLog.e(e);
            return false;
        }
    }

    public INavigationLayout getActionBarLayout() {
        ActionBarLayout actionBarLayout = this.actionBarLayout;
        if (this.sheetFragmentsStack.isEmpty()) {
            return actionBarLayout;
        }
        return (INavigationLayout) this.sheetFragmentsStack.get(r0.size() - 1);
    }

    public BottomSheetTabs getBottomSheetTabs() {
        ActionBarLayout actionBarLayout;
        ActionBarLayout actionBarLayout2 = this.rightActionBarLayout;
        if (actionBarLayout2 == null || actionBarLayout2.getBottomSheetTabs() == null) {
            ActionBarLayout actionBarLayout3 = this.actionBarLayout;
            if (actionBarLayout3 == null || actionBarLayout3.getBottomSheetTabs() == null) {
                return null;
            }
            actionBarLayout = this.actionBarLayout;
        } else {
            actionBarLayout = this.rightActionBarLayout;
        }
        return actionBarLayout.getBottomSheetTabs();
    }

    public BottomSheetTabsOverlay getBottomSheetTabsOverlay() {
        return this.bottomSheetTabsOverlay;
    }

    public FireworksOverlay getFireworksOverlay() {
        return this.fireworksOverlay;
    }

    public INavigationLayout getLayersActionBarLayout() {
        return this.layersActionBarLayout;
    }

    public FrameLayout getMainContainerFrameLayout() {
        return this.frameLayout;
    }

    public int getMainFragmentsCount() {
        return mainFragmentsStack.size();
    }

    public int getNavigationBarColor() {
        int navigationBarColor;
        if (Build.VERSION.SDK_INT < 26) {
            return 0;
        }
        Window window = getWindow();
        if (this.customNavigationBar != null) {
            return this.drawerLayoutContainer.getNavigationBarColor();
        }
        navigationBarColor = window.getNavigationBarColor();
        return navigationBarColor;
    }

    public INavigationLayout getRightActionBarLayout() {
        return this.rightActionBarLayout;
    }

    public Dialog getVisibleDialog() {
        for (int size = this.visibleDialogs.size() - 1; size >= 0; size--) {
            Dialog dialog = (Dialog) this.visibleDialogs.get(size);
            if (dialog.isShowing()) {
                return dialog;
            }
        }
        return null;
    }

    public void hideVisibleActionMode() {
        ActionMode actionMode = this.visibleActionMode;
        if (actionMode == null) {
            return;
        }
        actionMode.finish();
    }

    public boolean isLightNavigationBar() {
        return AndroidUtilities.getLightNavigationBar(getWindow());
    }

    @Override
    public boolean needAddFragmentToStack(org.telegram.ui.ActionBar.BaseFragment r6, org.telegram.ui.ActionBar.INavigationLayout r7) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.LaunchActivity.needAddFragmentToStack(org.telegram.ui.ActionBar.BaseFragment, org.telegram.ui.ActionBar.INavigationLayout):boolean");
    }

    @Override
    public boolean needCloseLastFragment(org.telegram.ui.ActionBar.INavigationLayout r5) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.LaunchActivity.needCloseLastFragment(org.telegram.ui.ActionBar.INavigationLayout):boolean");
    }

    @Override
    public boolean needPresentFragment(BaseFragment baseFragment, boolean z, boolean z2, INavigationLayout iNavigationLayout) {
        return INavigationLayout.INavigationLayoutDelegate.CC.$default$needPresentFragment(this, baseFragment, z, z2, iNavigationLayout);
    }

    @Override
    public boolean needPresentFragment(org.telegram.ui.ActionBar.INavigationLayout r9, org.telegram.ui.ActionBar.INavigationLayout.NavigationParams r10) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.LaunchActivity.needPresentFragment(org.telegram.ui.ActionBar.INavigationLayout, org.telegram.ui.ActionBar.INavigationLayout$NavigationParams):boolean");
    }

    @Override
    public void onActionModeFinished(ActionMode actionMode) {
        int type;
        super.onActionModeFinished(actionMode);
        if (this.visibleActionMode == actionMode) {
            this.visibleActionMode = null;
        }
        if (Build.VERSION.SDK_INT >= 23) {
            type = actionMode.getType();
            if (type == 1) {
                return;
            }
        }
        this.actionBarLayout.onActionModeFinished(actionMode);
        if (AndroidUtilities.isTablet()) {
            this.rightActionBarLayout.onActionModeFinished(actionMode);
            this.layersActionBarLayout.onActionModeFinished(actionMode);
        }
    }

    @Override
    public void onActionModeStarted(ActionMode actionMode) {
        int type;
        super.onActionModeStarted(actionMode);
        this.visibleActionMode = actionMode;
        try {
            Menu menu = actionMode.getMenu();
            if (menu != null && !this.actionBarLayout.extendActionMode(menu) && AndroidUtilities.isTablet() && !this.rightActionBarLayout.extendActionMode(menu)) {
                this.layersActionBarLayout.extendActionMode(menu);
            }
        } catch (Exception e) {
            FileLog.e(e);
        }
        if (Build.VERSION.SDK_INT >= 23) {
            type = actionMode.getType();
            if (type == 1) {
                return;
            }
        }
        this.actionBarLayout.onActionModeStarted(actionMode);
        if (AndroidUtilities.isTablet()) {
            this.rightActionBarLayout.onActionModeStarted(actionMode);
            this.layersActionBarLayout.onActionModeStarted(actionMode);
        }
    }

    @Override
    public void onActivityResult(int i, int i2, Intent intent) {
        VoIPService sharedInstance;
        boolean canDrawOverlays;
        if (SharedConfig.passcodeHash.length() != 0 && SharedConfig.lastPauseTime != 0) {
            SharedConfig.lastPauseTime = 0;
            if (BuildVars.LOGS_ENABLED) {
                FileLog.d("reset lastPauseTime onActivityResult");
            }
            UserConfig.getInstance(this.currentAccount).saveConfig(false);
        }
        if (i == 105) {
            if (Build.VERSION.SDK_INT >= 23) {
                canDrawOverlays = Settings.canDrawOverlays(this);
                ApplicationLoader.canDrawOverlays = canDrawOverlays;
                if (canDrawOverlays) {
                    GroupCallActivity groupCallActivity = GroupCallActivity.groupCallInstance;
                    if (groupCallActivity != null) {
                        groupCallActivity.dismissInternal();
                    }
                    AndroidUtilities.runOnUIThread(new Runnable() {
                        @Override
                        public final void run() {
                            LaunchActivity.this.lambda$onActivityResult$137();
                        }
                    }, 200L);
                    return;
                }
                return;
            }
            return;
        }
        super.onActivityResult(i, i2, intent);
        if (i == 520) {
            if (i2 != -1 || (sharedInstance = VoIPService.getSharedInstance()) == null) {
                return;
            }
            VideoCapturerDevice.mediaProjectionPermissionResultData = intent;
            sharedInstance.createCaptureDevice(true);
            return;
        }
        if (i == 140) {
            LocationController.getInstance(this.currentAccount).startFusedLocationRequest(i2 == -1);
            return;
        }
        if (i == 521) {
            Utilities.Callback callback = this.webviewShareAPIDoneListener;
            if (callback != null) {
                callback.run(Boolean.valueOf(i2 == -1));
                this.webviewShareAPIDoneListener = null;
                return;
            }
            return;
        }
        ThemeEditorView themeEditorView = ThemeEditorView.getInstance();
        if (themeEditorView != null) {
            themeEditorView.onActivityResult(i, i2, intent);
        }
        if (this.actionBarLayout.getFragmentStack().size() != 0) {
            BaseFragment baseFragment = this.actionBarLayout.getFragmentStack().get(this.actionBarLayout.getFragmentStack().size() - 1);
            baseFragment.onActivityResultFragment(i, i2, intent);
            if (baseFragment.getLastStoryViewer() != null) {
                baseFragment.getLastStoryViewer().onActivityResult(i, i2, intent);
            }
        }
        if (AndroidUtilities.isTablet()) {
            if (this.rightActionBarLayout.getFragmentStack().size() != 0) {
                this.rightActionBarLayout.getFragmentStack().get(this.rightActionBarLayout.getFragmentStack().size() - 1).onActivityResultFragment(i, i2, intent);
            }
            if (this.layersActionBarLayout.getFragmentStack().size() != 0) {
                this.layersActionBarLayout.getFragmentStack().get(this.layersActionBarLayout.getFragmentStack().size() - 1).onActivityResultFragment(i, i2, intent);
            }
        }
        NotificationCenter.getGlobalInstance().lambda$postNotificationNameOnUIThread$1(NotificationCenter.onActivityResultReceived, Integer.valueOf(i), Integer.valueOf(i2), intent);
    }

    @Override
    public void onBackPressed() {
        ActionBarLayout actionBarLayout;
        if (FloatingDebugController.onBackPressed()) {
            return;
        }
        PasscodeViewDialog passcodeViewDialog = this.passcodeDialog;
        if (passcodeViewDialog != null && passcodeViewDialog.passcodeView.getVisibility() == 0) {
            finish();
            return;
        }
        BottomSheetTabsOverlay bottomSheetTabsOverlay = this.bottomSheetTabsOverlay;
        if ((bottomSheetTabsOverlay == null || !bottomSheetTabsOverlay.onBackPressed()) && !SearchTagsList.onBackPressedRenameTagAlert()) {
            if (ContentPreviewViewer.hasInstance() && ContentPreviewViewer.getInstance().isVisible()) {
                ContentPreviewViewer.getInstance().closeWithMenu();
                return;
            }
            if (SecretMediaViewer.hasInstance() && SecretMediaViewer.getInstance().isVisible()) {
                SecretMediaViewer.getInstance().closePhoto(true, false);
                return;
            }
            if (PhotoViewer.hasInstance() && PhotoViewer.getInstance().isVisible()) {
                PhotoViewer.getInstance().closePhoto(true, false);
                return;
            }
            if (ArticleViewer.hasInstance() && ArticleViewer.getInstance().isVisible()) {
                ArticleViewer.getInstance().close(true, false);
                return;
            }
            if (this.drawerLayoutContainer.isDrawerOpened()) {
                this.drawerLayoutContainer.closeDrawer(false);
                return;
            }
            if (AndroidUtilities.isTablet()) {
                if (this.layersActionBarLayout.getView().getVisibility() == 0) {
                    actionBarLayout = this.layersActionBarLayout;
                    actionBarLayout.onBackPressed();
                } else if (this.rightActionBarLayout.getView().getVisibility() == 0 && !this.rightActionBarLayout.getFragmentStack().isEmpty()) {
                    BaseFragment baseFragment = this.rightActionBarLayout.getFragmentStack().get(this.rightActionBarLayout.getFragmentStack().size() - 1);
                    if (baseFragment.onBackPressed()) {
                        baseFragment.lambda$onBackPressed$321();
                        return;
                    }
                    return;
                }
            }
            actionBarLayout = this.actionBarLayout;
            actionBarLayout.onBackPressed();
        }
    }

    @Override
    public void onConfigurationChanged(Configuration configuration) {
        AndroidUtilities.checkDisplaySize(this, configuration);
        AndroidUtilities.setPreferredMaxRefreshRate(getWindow());
        super.onConfigurationChanged(configuration);
        checkLayout();
        PipRoundVideoView pipRoundVideoView = PipRoundVideoView.getInstance();
        if (pipRoundVideoView != null) {
            pipRoundVideoView.onConfigurationChanged();
        }
        EmbedBottomSheet embedBottomSheet = EmbedBottomSheet.getInstance();
        if (embedBottomSheet != null) {
            embedBottomSheet.onConfigurationChanged(configuration);
        }
        BoostPagerBottomSheet boostPagerBottomSheet = BoostPagerBottomSheet.getInstance();
        if (boostPagerBottomSheet != null) {
            boostPagerBottomSheet.onConfigurationChanged(configuration);
        }
        PhotoViewer pipInstance = PhotoViewer.getPipInstance();
        if (pipInstance != null) {
            pipInstance.onConfigurationChanged(configuration);
        }
        ThemeEditorView themeEditorView = ThemeEditorView.getInstance();
        if (themeEditorView != null) {
            themeEditorView.onConfigurationChanged();
        }
        if (Theme.selectedAutoNightType == 3) {
            Theme.checkAutoNightThemeConditions();
        }
    }

    @Override
    public void onCreate(android.os.Bundle r16) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.LaunchActivity.onCreate(android.os.Bundle):void");
    }

    @Override
    public void onDestroy() {
        isActive = false;
        if (PhotoViewer.getPipInstance() != null) {
            PhotoViewer.getPipInstance().destroyPhotoViewer();
        }
        if (PhotoViewer.hasInstance()) {
            PhotoViewer.getInstance().destroyPhotoViewer();
        }
        if (SecretMediaViewer.hasInstance()) {
            SecretMediaViewer.getInstance().destroyPhotoViewer();
        }
        if (ArticleViewer.hasInstance()) {
            ArticleViewer.getInstance().destroyArticleViewer();
        }
        if (ContentPreviewViewer.hasInstance()) {
            ContentPreviewViewer.getInstance().destroy();
        }
        GroupCallActivity groupCallActivity = GroupCallActivity.groupCallInstance;
        if (groupCallActivity != null) {
            groupCallActivity.dismissInternal();
        }
        PipRoundVideoView pipRoundVideoView = PipRoundVideoView.getInstance();
        MediaController.getInstance().setBaseActivity(this, false);
        MediaController.getInstance().setFeedbackView(this.feedbackView, false);
        if (pipRoundVideoView != null) {
            pipRoundVideoView.close(false);
        }
        Theme.destroyResources();
        EmbedBottomSheet embedBottomSheet = EmbedBottomSheet.getInstance();
        if (embedBottomSheet != null) {
            embedBottomSheet.destroy();
        }
        ThemeEditorView themeEditorView = ThemeEditorView.getInstance();
        if (themeEditorView != null) {
            themeEditorView.destroy();
        }
        for (int i = 0; i < this.visibleDialogs.size(); i++) {
            try {
                if (((Dialog) this.visibleDialogs.get(i)).isShowing()) {
                    ((Dialog) this.visibleDialogs.get(i)).dismiss();
                }
            } catch (Exception e) {
                FileLog.e(e);
            }
        }
        this.visibleDialogs.clear();
        try {
            if (this.onGlobalLayoutListener != null) {
                getWindow().getDecorView().getRootView().getViewTreeObserver().removeOnGlobalLayoutListener(this.onGlobalLayoutListener);
            }
        } catch (Exception e2) {
            FileLog.e(e2);
        }
        super.onDestroy();
        onFinish();
        FloatingDebugController.onDestroy();
        FlagSecureReason flagSecureReason = this.flagSecureReason;
        if (flagSecureReason != null) {
            flagSecureReason.detach();
        }
    }

    @Override
    public boolean onKeyUp(int i, KeyEvent keyEvent) {
        ActionBarLayout actionBarLayout;
        if (i == 82 && !SharedConfig.isWaitingForPasscodeEnter) {
            if (PhotoViewer.hasInstance() && PhotoViewer.getInstance().isVisible()) {
                return super.onKeyUp(i, keyEvent);
            }
            if (ArticleViewer.hasInstance() && ArticleViewer.getInstance().isVisible()) {
                return super.onKeyUp(i, keyEvent);
            }
            if (AndroidUtilities.isTablet()) {
                if (this.layersActionBarLayout.getView().getVisibility() == 0 && !this.layersActionBarLayout.getFragmentStack().isEmpty()) {
                    actionBarLayout = this.layersActionBarLayout;
                } else if (this.rightActionBarLayout.getView().getVisibility() == 0 && !this.rightActionBarLayout.getFragmentStack().isEmpty()) {
                    actionBarLayout = this.rightActionBarLayout;
                }
                actionBarLayout.getView().onKeyUp(i, keyEvent);
            } else if (this.actionBarLayout.getFragmentStack().size() == 1) {
                if (this.drawerLayoutContainer.isDrawerOpened()) {
                    this.drawerLayoutContainer.closeDrawer(false);
                } else {
                    if (getCurrentFocus() != null) {
                        AndroidUtilities.hideKeyboard(getCurrentFocus());
                    }
                    this.drawerLayoutContainer.openDrawer(false);
                }
            }
            actionBarLayout = this.actionBarLayout;
            actionBarLayout.getView().onKeyUp(i, keyEvent);
        }
        return super.onKeyUp(i, keyEvent);
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        ActionBarLayout actionBarLayout = this.actionBarLayout;
        if (actionBarLayout != null) {
            actionBarLayout.onLowMemory();
            if (AndroidUtilities.isTablet()) {
                ActionBarLayout actionBarLayout2 = this.rightActionBarLayout;
                if (actionBarLayout2 != null) {
                    actionBarLayout2.onLowMemory();
                }
                ActionBarLayout actionBarLayout3 = this.layersActionBarLayout;
                if (actionBarLayout3 != null) {
                    actionBarLayout3.onLowMemory();
                }
            }
        }
    }

    @Override
    public void onMeasureOverride(int[] iArr) {
        INavigationLayout.INavigationLayoutDelegate.CC.$default$onMeasureOverride(this, iArr);
    }

    @Override
    public void onMultiWindowModeChanged(boolean z) {
        AndroidUtilities.isInMultiwindow = z;
        checkLayout();
        super.onMultiWindowModeChanged(z);
    }

    @Override
    public void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        handleIntent(intent, true, false, false, null, true, true);
    }

    public void onNewIntent(Intent intent, Browser.Progress progress) {
        super.onNewIntent(intent);
        handleIntent(intent, true, false, false, progress, true, false);
    }

    @Override
    public void onPause() {
        super.onPause();
        isResumed = false;
        NotificationCenter.getGlobalInstance().lambda$postNotificationNameOnUIThread$1(NotificationCenter.stopAllHeavyOperations, 4096);
        ApplicationLoader.mainInterfacePaused = true;
        final int i = this.currentAccount;
        Utilities.stageQueue.postRunnable(new Runnable() {
            @Override
            public final void run() {
                LaunchActivity.lambda$onPause$138(i);
            }
        });
        onPasscodePause();
        this.actionBarLayout.onPause();
        if (AndroidUtilities.isTablet()) {
            ActionBarLayout actionBarLayout = this.rightActionBarLayout;
            if (actionBarLayout != null) {
                actionBarLayout.onPause();
            }
            ActionBarLayout actionBarLayout2 = this.layersActionBarLayout;
            if (actionBarLayout2 != null) {
                actionBarLayout2.onPause();
            }
        }
        PasscodeViewDialog passcodeViewDialog = this.passcodeDialog;
        if (passcodeViewDialog != null) {
            passcodeViewDialog.passcodeView.onPause();
        }
        Iterator it = this.overlayPasscodeViews.iterator();
        while (it.hasNext()) {
            ((PasscodeView) it.next()).onPause();
        }
        ConnectionsManager.getInstance(this.currentAccount).setAppPaused(!(ApplicationLoader.applicationLoaderInstance != null ? r1.onPause() : false), false);
        if (PhotoViewer.hasInstance() && PhotoViewer.getInstance().isVisible()) {
            PhotoViewer.getInstance().onPause();
        }
        StoryRecorder.onPause();
        if (VoIPFragment.getInstance() != null) {
            VoIPFragment.onPause();
        }
        SpoilerEffect2.pause(true);
    }

    @Override
    public boolean onPreIme() {
        if (SecretMediaViewer.hasInstance() && SecretMediaViewer.getInstance().isVisible()) {
            SecretMediaViewer.getInstance().closePhoto(true, false);
            return true;
        }
        if (PhotoViewer.hasInstance() && PhotoViewer.getInstance().isVisible()) {
            PhotoViewer.getInstance().closePhoto(true, false);
            return true;
        }
        if (!ArticleViewer.hasInstance() || !ArticleViewer.getInstance().isVisible()) {
            return false;
        }
        ArticleViewer.getInstance().close(true, false);
        return true;
    }

    @Override
    public void onRebuildAllFragments(INavigationLayout iNavigationLayout, boolean z) {
        if (AndroidUtilities.isTablet() && iNavigationLayout == this.layersActionBarLayout) {
            this.rightActionBarLayout.rebuildAllFragmentViews(z, z);
            this.actionBarLayout.rebuildAllFragmentViews(z, z);
        }
        this.drawerLayoutAdapter.notifyDataSetChanged();
    }

    @Override
    public void onRequestPermissionsResult(int i, String[] strArr, int[] iArr) {
        super.onRequestPermissionsResult(i, strArr, iArr);
        if (checkPermissionsResult(i, strArr, iArr)) {
            ApplicationLoader applicationLoader = ApplicationLoader.applicationLoaderInstance;
            if (applicationLoader == null || !applicationLoader.checkRequestPermissionResult(i, strArr, iArr)) {
                if (this.actionBarLayout.getFragmentStack().size() != 0) {
                    this.actionBarLayout.getFragmentStack().get(this.actionBarLayout.getFragmentStack().size() - 1).onRequestPermissionsResultFragment(i, strArr, iArr);
                }
                if (AndroidUtilities.isTablet()) {
                    if (this.rightActionBarLayout.getFragmentStack().size() != 0) {
                        this.rightActionBarLayout.getFragmentStack().get(this.rightActionBarLayout.getFragmentStack().size() - 1).onRequestPermissionsResultFragment(i, strArr, iArr);
                    }
                    if (this.layersActionBarLayout.getFragmentStack().size() != 0) {
                        this.layersActionBarLayout.getFragmentStack().get(this.layersActionBarLayout.getFragmentStack().size() - 1).onRequestPermissionsResultFragment(i, strArr, iArr);
                    }
                }
                VoIPFragment.onRequestPermissionsResult(i, strArr, iArr);
                StoryRecorder.onRequestPermissionsResult(i, strArr, iArr);
                NotificationCenter.getGlobalInstance().lambda$postNotificationNameOnUIThread$1(NotificationCenter.onRequestPermissionResultReceived, Integer.valueOf(i), strArr, iArr);
                if (this.requestedPermissions.get(i, -1) >= 0) {
                    int i2 = this.requestedPermissions.get(i, -1);
                    this.requestedPermissions.delete(i);
                    NotificationCenter.getGlobalInstance().lambda$postNotificationNameOnUIThread$1(NotificationCenter.permissionsGranted, Integer.valueOf(i2));
                }
                NotificationCenter.getGlobalInstance().lambda$postNotificationNameOnUIThread$1(NotificationCenter.activityPermissionsGranted, Integer.valueOf(i), strArr, iArr);
            }
        }
    }

    @Override
    public void onResume() {
        boolean canDrawOverlays;
        MessageObject playingMessageObject;
        super.onResume();
        isResumed = true;
        Runnable runnable = onResumeStaticCallback;
        if (runnable != null) {
            runnable.run();
            onResumeStaticCallback = null;
        }
        if (Theme.selectedAutoNightType == 3) {
            Theme.checkAutoNightThemeConditions();
        }
        checkWasMutedByAdmin(true);
        NotificationCenter.getGlobalInstance().lambda$postNotificationNameOnUIThread$1(NotificationCenter.startAllHeavyOperations, 4096);
        MediaController mediaController = MediaController.getInstance();
        ViewGroup view = this.actionBarLayout.getView();
        this.feedbackView = view;
        mediaController.setFeedbackView(view, true);
        ApplicationLoader.mainInterfacePaused = false;
        MessagesController.getInstance(this.currentAccount).sortDialogs(null);
        showLanguageAlert(false);
        Utilities.stageQueue.postRunnable(new Runnable() {
            @Override
            public final void run() {
                LaunchActivity.lambda$onResume$139();
            }
        });
        checkFreeDiscSpace(0);
        MediaController.checkGallery();
        onPasscodeResume();
        PasscodeViewDialog passcodeViewDialog = this.passcodeDialog;
        if (passcodeViewDialog == null || passcodeViewDialog.passcodeView.getVisibility() != 0) {
            this.actionBarLayout.onResume();
            if (AndroidUtilities.isTablet()) {
                ActionBarLayout actionBarLayout = this.rightActionBarLayout;
                if (actionBarLayout != null) {
                    actionBarLayout.onResume();
                }
                ActionBarLayout actionBarLayout2 = this.layersActionBarLayout;
                if (actionBarLayout2 != null) {
                    actionBarLayout2.onResume();
                }
            }
        } else {
            this.actionBarLayout.dismissDialogs();
            if (AndroidUtilities.isTablet()) {
                ActionBarLayout actionBarLayout3 = this.rightActionBarLayout;
                if (actionBarLayout3 != null) {
                    actionBarLayout3.dismissDialogs();
                }
                ActionBarLayout actionBarLayout4 = this.layersActionBarLayout;
                if (actionBarLayout4 != null) {
                    actionBarLayout4.dismissDialogs();
                }
            }
            this.passcodeDialog.passcodeView.onResume();
            Iterator it = this.overlayPasscodeViews.iterator();
            while (it.hasNext()) {
                ((PasscodeView) it.next()).onResume();
            }
        }
        ConnectionsManager.getInstance(this.currentAccount).setAppPaused(false, false);
        updateCurrentConnectionState(this.currentAccount);
        if (PhotoViewer.hasInstance() && PhotoViewer.getInstance().isVisible()) {
            PhotoViewer.getInstance().onResume();
        }
        StoryRecorder.onResume();
        if (PipRoundVideoView.getInstance() != null && MediaController.getInstance().isMessagePaused() && (playingMessageObject = MediaController.getInstance().getPlayingMessageObject()) != null) {
            MediaController.getInstance().seekToProgress(playingMessageObject, playingMessageObject.audioProgress);
        }
        if (UserConfig.getInstance(UserConfig.selectedAccount).unacceptedTermsOfService != null) {
            int i = UserConfig.selectedAccount;
            showTosActivity(i, UserConfig.getInstance(i).unacceptedTermsOfService);
        } else {
            TLRPC.TL_help_appUpdate tL_help_appUpdate = SharedConfig.pendingAppUpdate;
            if (tL_help_appUpdate != null && tL_help_appUpdate.can_not_skip) {
                showUpdateActivity(UserConfig.selectedAccount, SharedConfig.pendingAppUpdate, true);
            }
        }
        checkAppUpdate(false, null);
        if (Build.VERSION.SDK_INT >= 23) {
            canDrawOverlays = Settings.canDrawOverlays(this);
            ApplicationLoader.canDrawOverlays = canDrawOverlays;
        }
        if (VoIPFragment.getInstance() != null) {
            VoIPFragment.onResume();
        }
        invalidateTabletMode();
        SpoilerEffect2.pause(false);
        ApplicationLoader applicationLoader = ApplicationLoader.applicationLoaderInstance;
        if (applicationLoader != null) {
            applicationLoader.onResume();
        }
        Runnable runnable2 = whenResumed;
        if (runnable2 != null) {
            runnable2.run();
            whenResumed = null;
        }
    }

    @Override
    public void onSaveInstanceState(Bundle bundle) {
        List<BaseFragment> fragmentStack;
        int size;
        BaseFragment baseFragment;
        String str;
        try {
            super.onSaveInstanceState(bundle);
            if (AndroidUtilities.isTablet()) {
                ActionBarLayout actionBarLayout = this.layersActionBarLayout;
                if (actionBarLayout == null || actionBarLayout.getFragmentStack().isEmpty()) {
                    ActionBarLayout actionBarLayout2 = this.rightActionBarLayout;
                    if (actionBarLayout2 == null || actionBarLayout2.getFragmentStack().isEmpty()) {
                        if (!this.actionBarLayout.getFragmentStack().isEmpty()) {
                            fragmentStack = this.actionBarLayout.getFragmentStack();
                            size = this.actionBarLayout.getFragmentStack().size() - 1;
                        }
                        baseFragment = null;
                    } else {
                        fragmentStack = this.rightActionBarLayout.getFragmentStack();
                        size = this.rightActionBarLayout.getFragmentStack().size() - 1;
                    }
                } else {
                    fragmentStack = this.layersActionBarLayout.getFragmentStack();
                    size = this.layersActionBarLayout.getFragmentStack().size() - 1;
                }
                baseFragment = fragmentStack.get(size);
            } else {
                if (!this.actionBarLayout.getFragmentStack().isEmpty()) {
                    fragmentStack = this.actionBarLayout.getFragmentStack();
                    size = this.actionBarLayout.getFragmentStack().size() - 1;
                    baseFragment = fragmentStack.get(size);
                }
                baseFragment = null;
            }
        } catch (Exception e) {
            FileLog.e(e);
            return;
        }
        if (baseFragment != null) {
            Bundle arguments = baseFragment.getArguments();
            if ((baseFragment instanceof ChatActivity) && arguments != null) {
                bundle.putBundle("args", arguments);
                str = "chat";
            } else if ((baseFragment instanceof GroupCreateFinalActivity) && arguments != null) {
                bundle.putBundle("args", arguments);
                str = "group";
            } else {
                if (!(baseFragment instanceof WallpapersListActivity)) {
                    if (!(baseFragment instanceof ProfileActivity)) {
                        if ((baseFragment instanceof ChannelCreateActivity) && arguments != null && arguments.getInt("step") == 0) {
                            bundle.putBundle("args", arguments);
                            str = "channel";
                        }
                        baseFragment.saveSelfArgs(bundle);
                    }
                    ProfileActivity profileActivity = (ProfileActivity) baseFragment;
                    if (!profileActivity.isSettings()) {
                        if (profileActivity.isChat() && arguments != null) {
                            bundle.putBundle("args", arguments);
                            str = "chat_profile";
                        }
                        baseFragment.saveSelfArgs(bundle);
                    }
                    str = "settings";
                    FileLog.e(e);
                    return;
                }
                str = "wallpapers";
            }
            bundle.putString("fragment", str);
            baseFragment.saveSelfArgs(bundle);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        Browser.bindCustomTabsService(this);
        ApplicationLoader.mainInterfaceStopped = false;
        GroupCallPip.updateVisibility(this);
        GroupCallActivity groupCallActivity = GroupCallActivity.groupCallInstance;
        if (groupCallActivity != null) {
            groupCallActivity.onResume();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        Browser.unbindCustomTabsService(this);
        ApplicationLoader.mainInterfaceStopped = true;
        GroupCallPip.updateVisibility(this);
        GroupCallActivity groupCallActivity = GroupCallActivity.groupCallInstance;
        if (groupCallActivity != null) {
            groupCallActivity.onPause();
        }
    }

    @Override
    public void onThemeProgress(float f) {
        if (ArticleViewer.hasInstance() && ArticleViewer.getInstance().isVisible()) {
            ArticleViewer.getInstance().updateThemeColors(f);
        }
        this.drawerLayoutContainer.setBehindKeyboardColor(Theme.getColor(Theme.key_windowBackgroundWhite));
        if (PhotoViewer.hasInstance()) {
            PhotoViewer.getInstance().updateColors();
        }
    }

    @Override
    protected void onUserLeaveHint() {
        Iterator it = this.onUserLeaveHintListeners.iterator();
        while (it.hasNext()) {
            ((Runnable) it.next()).run();
        }
        ActionBarLayout actionBarLayout = this.actionBarLayout;
        if (actionBarLayout != null) {
            actionBarLayout.onUserLeaveHint();
        }
    }

    public void openMessage(final long j, final int i, final String str, final Browser.Progress progress, int i2, final int i3) {
        Integer valueOf;
        Runnable runnable;
        TLRPC.Chat chat;
        if (j >= 0 || (chat = MessagesController.getInstance(this.currentAccount).getChat(Long.valueOf(-j))) == null || !ChatObject.isForum(chat)) {
            if (progress != null) {
                progress.init();
            }
            final Bundle bundle = new Bundle();
            if (j >= 0) {
                bundle.putLong("user_id", j);
            } else {
                long j2 = -j;
                TLRPC.Chat chat2 = MessagesController.getInstance(this.currentAccount).getChat(Long.valueOf(j2));
                if (chat2 == null || !chat2.forum) {
                    bundle.putLong("chat_id", j2);
                } else {
                    valueOf = Integer.valueOf(i);
                    runnable = new Runnable() {
                        @Override
                        public final void run() {
                            LaunchActivity.lambda$openMessage$40(Browser.Progress.this);
                        }
                    };
                }
            }
            bundle.putInt("message_id", i);
            ArrayList arrayList = mainFragmentsStack;
            final BaseFragment baseFragment = !arrayList.isEmpty() ? (BaseFragment) arrayList.get(arrayList.size() - 1) : null;
            if (baseFragment == null || MessagesController.getInstance(this.currentAccount).checkCanOpenChat(bundle, baseFragment)) {
                AndroidUtilities.runOnUIThread(new Runnable() {
                    @Override
                    public final void run() {
                        LaunchActivity.this.lambda$openMessage$44(bundle, i, str, i3, j, progress, baseFragment);
                    }
                });
                return;
            }
            return;
        }
        if (progress != null) {
            progress.init();
        }
        valueOf = Integer.valueOf(i);
        runnable = new Runnable() {
            @Override
            public final void run() {
                LaunchActivity.lambda$openMessage$39(Browser.Progress.this);
            }
        };
        openForumFromLink(j, valueOf, str, runnable, i2, i3);
    }

    public void lambda$runLinkRequest$93(BaseFragment baseFragment) {
        getActionBarLayout().presentFragment(baseFragment);
    }

    public boolean presentFragment(BaseFragment baseFragment, boolean z, boolean z2) {
        return getActionBarLayout().presentFragment(baseFragment, z, z2, true, false);
    }

    public void rebuildAllFragments(boolean z) {
        ActionBarLayout actionBarLayout = this.layersActionBarLayout;
        if (actionBarLayout == null) {
            actionBarLayout = this.actionBarLayout;
        }
        actionBarLayout.rebuildAllFragmentViews(z, z);
    }

    public void removeOnUserLeaveHintListener(Runnable runnable) {
        this.onUserLeaveHintListeners.remove(runnable);
    }

    public void removeOverlayPasscodeView(PasscodeView passcodeView) {
        this.overlayPasscodeViews.remove(passcodeView);
    }

    public void requestCustomNavigationBar() {
        if (this.customNavigationBar == null && Build.VERSION.SDK_INT >= 26) {
            this.customNavigationBar = this.drawerLayoutContainer.createNavigationBar();
            ((FrameLayout) getWindow().getDecorView()).addView(this.customNavigationBar);
        }
        View view = this.customNavigationBar;
        if (view != null) {
            if (view.getLayoutParams().height == AndroidUtilities.navigationBarHeight && ((FrameLayout.LayoutParams) this.customNavigationBar.getLayoutParams()).topMargin == this.customNavigationBar.getHeight()) {
                return;
            }
            this.customNavigationBar.getLayoutParams().height = AndroidUtilities.navigationBarHeight;
            ((FrameLayout.LayoutParams) this.customNavigationBar.getLayoutParams()).topMargin = this.drawerLayoutContainer.getMeasuredHeight();
            this.customNavigationBar.requestLayout();
        }
    }

    public void setLightNavigationBar(boolean z) {
        if (Build.VERSION.SDK_INT >= 26) {
            AndroidUtilities.setLightNavigationBar(getWindow(), z);
        }
    }

    public void setNavigateToPremiumBot(boolean z) {
        this.navigateToPremiumBot = z;
    }

    public void setNavigateToPremiumGiftCallback(Runnable runnable) {
        this.navigateToPremiumGiftCallback = runnable;
    }

    public void setNavigationBarColor(int r4, boolean r5) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.LaunchActivity.setNavigationBarColor(int, boolean):void");
    }

    public Dialog showAlertDialog(AlertDialog.Builder builder) {
        try {
            final AlertDialog show = builder.show();
            show.setCanceledOnTouchOutside(true);
            show.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public final void onDismiss(DialogInterface dialogInterface) {
                    LaunchActivity.this.lambda$showAlertDialog$133(show, dialogInterface);
                }
            });
            this.visibleDialogs.add(show);
            return show;
        } catch (Exception e) {
            FileLog.e(e);
            return null;
        }
    }

    public void showBulletin(androidx.arch.core.util.Function r3) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.LaunchActivity.showBulletin(androidx.arch.core.util.Function):void");
    }

    public void showPasscodeActivity(boolean z, boolean z2, int i, int i2, final Runnable runnable, Runnable runnable2) {
        if (this.drawerLayoutContainer == null || isFinishing()) {
            return;
        }
        if (this.passcodeDialog == null) {
            this.passcodeDialog = new PasscodeViewDialog(this);
        }
        SelectAnimatedEmojiDialog.SelectAnimatedEmojiDialogWindow selectAnimatedEmojiDialogWindow = this.selectAnimatedEmojiDialog;
        if (selectAnimatedEmojiDialogWindow != null) {
            selectAnimatedEmojiDialogWindow.dismiss();
            this.selectAnimatedEmojiDialog = null;
        }
        SharedConfig.appLocked = true;
        if (SecretMediaViewer.hasInstance() && SecretMediaViewer.getInstance().isVisible()) {
            SecretMediaViewer.getInstance().closePhoto(false, false);
        } else if (PhotoViewer.hasInstance() && PhotoViewer.getInstance().isVisible()) {
            PhotoViewer.getInstance().closePhoto(false, true);
        } else if (ArticleViewer.hasInstance() && ArticleViewer.getInstance().isVisible()) {
            ArticleViewer.getInstance().close(false, true);
        }
        StoryRecorder.destroyInstance();
        MessageObject playingMessageObject = MediaController.getInstance().getPlayingMessageObject();
        if (playingMessageObject != null && playingMessageObject.isRoundVideo()) {
            MediaController.getInstance().cleanupPlayer(true, true);
        }
        this.passcodeDialog.show();
        this.passcodeDialog.passcodeView.onShow(this.overlayPasscodeViews.isEmpty() && z, z2, i, i2, new Runnable() {
            @Override
            public final void run() {
                LaunchActivity.this.lambda$showPasscodeActivity$13(runnable);
            }
        }, runnable2);
        int i3 = 0;
        while (i3 < this.overlayPasscodeViews.size()) {
            ((PasscodeView) this.overlayPasscodeViews.get(i3)).onShow(z && i3 == this.overlayPasscodeViews.size() - 1, z2, i, i2, null, null);
            i3++;
        }
        SharedConfig.isWaitingForPasscodeEnter = true;
        this.drawerLayoutContainer.setAllowOpenDrawer(false, false);
        PasscodeView.PasscodeViewDelegate passcodeViewDelegate = new PasscodeView.PasscodeViewDelegate() {
            @Override
            public final void didAcceptedPassword(PasscodeView passcodeView) {
                LaunchActivity.this.lambda$showPasscodeActivity$14(passcodeView);
            }
        };
        this.passcodeDialog.passcodeView.setDelegate(passcodeViewDelegate);
        Iterator it = this.overlayPasscodeViews.iterator();
        while (it.hasNext()) {
            ((PasscodeView) it.next()).setDelegate(passcodeViewDelegate);
        }
        try {
            NotificationsController.getInstance(UserConfig.selectedAccount).showNotifications();
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    public void showSelectStatusDialog() {
        BaseFragment lastFragment;
        int i;
        View view;
        int i2;
        AnimatedEmojiDrawable.SwapAnimatedEmojiDrawable swapAnimatedEmojiDrawable;
        WindowInsets rootWindowInsets;
        WindowInsets rootWindowInsets2;
        int stableInsetLeft;
        if (this.selectAnimatedEmojiDialog != null || SharedConfig.appLocked || (lastFragment = this.actionBarLayout.getLastFragment()) == null) {
            return;
        }
        View childAt = this.sideMenu.getChildAt(0);
        SelectAnimatedEmojiDialog.SelectAnimatedEmojiDialogWindow[] selectAnimatedEmojiDialogWindowArr = new SelectAnimatedEmojiDialog.SelectAnimatedEmojiDialogWindow[1];
        TLRPC.User user = MessagesController.getInstance(UserConfig.selectedAccount).getUser(Long.valueOf(UserConfig.getInstance(UserConfig.selectedAccount).getClientUserId()));
        if (childAt instanceof DrawerProfileCell) {
            DrawerProfileCell drawerProfileCell = (DrawerProfileCell) childAt;
            AnimatedEmojiDrawable.SwapAnimatedEmojiDrawable emojiStatusDrawable = drawerProfileCell.getEmojiStatusDrawable();
            if (emojiStatusDrawable != null) {
                emojiStatusDrawable.play();
            }
            View emojiStatusDrawableParent = drawerProfileCell.getEmojiStatusDrawableParent();
            if (emojiStatusDrawable != null) {
                boolean z = emojiStatusDrawable.getDrawable() instanceof AnimatedEmojiDrawable;
            }
            Rect rect = AndroidUtilities.rectTmp2;
            drawerProfileCell.getEmojiStatusLocation(rect);
            int dp = (-(childAt.getHeight() - rect.centerY())) - AndroidUtilities.dp(16.0f);
            i = rect.centerX();
            if (Build.VERSION.SDK_INT >= 23 && getWindow() != null && getWindow().getDecorView() != null) {
                rootWindowInsets = getWindow().getDecorView().getRootWindowInsets();
                if (rootWindowInsets != null) {
                    rootWindowInsets2 = getWindow().getDecorView().getRootWindowInsets();
                    stableInsetLeft = rootWindowInsets2.getStableInsetLeft();
                    i -= stableInsetLeft;
                }
            }
            i2 = dp;
            swapAnimatedEmojiDrawable = emojiStatusDrawable;
            view = emojiStatusDrawableParent;
        } else {
            i = 0;
            view = null;
            i2 = 0;
            swapAnimatedEmojiDrawable = null;
        }
        View view2 = view;
        AnonymousClass12 anonymousClass12 = new SelectAnimatedEmojiDialog(lastFragment, this, true, Integer.valueOf(i), 0, null) {
            final SelectAnimatedEmojiDialog.SelectAnimatedEmojiDialogWindow[] val$popup;

            AnonymousClass12(BaseFragment lastFragment2, Context this, boolean z2, Integer num, int i3, Theme.ResourcesProvider resourcesProvider, SelectAnimatedEmojiDialog.SelectAnimatedEmojiDialogWindow[] selectAnimatedEmojiDialogWindowArr2) {
                super(lastFragment2, this, z2, num, i3, resourcesProvider);
                r15 = selectAnimatedEmojiDialogWindowArr2;
            }

            @Override
            protected void onEmojiSelected(View view3, Long l, TLRPC.Document document, Integer num) {
                TLRPC.EmojiStatus emojiStatus;
                if (l == null) {
                    emojiStatus = new TLRPC.TL_emojiStatusEmpty();
                } else if (num != null) {
                    TLRPC.TL_emojiStatusUntil tL_emojiStatusUntil = new TLRPC.TL_emojiStatusUntil();
                    tL_emojiStatusUntil.document_id = l.longValue();
                    tL_emojiStatusUntil.until = num.intValue();
                    emojiStatus = tL_emojiStatusUntil;
                } else {
                    TLRPC.TL_emojiStatus tL_emojiStatus = new TLRPC.TL_emojiStatus();
                    tL_emojiStatus.document_id = l.longValue();
                    emojiStatus = tL_emojiStatus;
                }
                MessagesController.getInstance(LaunchActivity.this.currentAccount).updateEmojiStatus(emojiStatus);
                TLRPC.User currentUser = UserConfig.getInstance(LaunchActivity.this.currentAccount).getCurrentUser();
                if (currentUser != null) {
                    for (int i3 = 0; i3 < LaunchActivity.this.sideMenu.getChildCount(); i3++) {
                        View childAt2 = LaunchActivity.this.sideMenu.getChildAt(i3);
                        if (childAt2 instanceof DrawerUserCell) {
                            DrawerUserCell drawerUserCell = (DrawerUserCell) childAt2;
                            drawerUserCell.setAccount(drawerUserCell.getAccountNumber());
                        } else if (childAt2 instanceof DrawerProfileCell) {
                            if (l != null) {
                                ((DrawerProfileCell) childAt2).animateStateChange(l.longValue());
                            }
                            ((DrawerProfileCell) childAt2).setUser(currentUser, LaunchActivity.this.drawerLayoutAdapter.isAccountsShown());
                        } else if ((childAt2 instanceof DrawerActionCell) && LaunchActivity.this.drawerLayoutAdapter.getId(LaunchActivity.this.sideMenu.getChildAdapterPosition(childAt2)) == 15) {
                            boolean z2 = DialogObject.getEmojiStatusDocumentId(currentUser.emoji_status) != 0;
                            ((DrawerActionCell) childAt2).updateTextAndIcon(LaunchActivity.this.getString(z2 ? R.string.ChangeEmojiStatus : R.string.SetEmojiStatus), z2 ? R.drawable.msg_status_edit : R.drawable.msg_status_set);
                        }
                    }
                }
                if (r15[0] != null) {
                    LaunchActivity.this.selectAnimatedEmojiDialog = null;
                    r15[0].dismiss();
                }
            }

            @Override
            public void onSettings() {
                DrawerLayoutContainer drawerLayoutContainer = LaunchActivity.this.drawerLayoutContainer;
                if (drawerLayoutContainer != null) {
                    drawerLayoutContainer.closeDrawer();
                }
            }
        };
        if (user != null) {
            anonymousClass12.setExpireDateHint(DialogObject.getEmojiStatusUntil(user.emoji_status));
        }
        anonymousClass12.setSelected((swapAnimatedEmojiDrawable == null || !(swapAnimatedEmojiDrawable.getDrawable() instanceof AnimatedEmojiDrawable)) ? null : Long.valueOf(((AnimatedEmojiDrawable) swapAnimatedEmojiDrawable.getDrawable()).getDocumentId()));
        anonymousClass12.setSaveState(2);
        anonymousClass12.setScrimDrawable(swapAnimatedEmojiDrawable, view2);
        AnonymousClass13 anonymousClass13 = new SelectAnimatedEmojiDialog.SelectAnimatedEmojiDialogWindow(anonymousClass12, -2, -2) {
            AnonymousClass13(View anonymousClass122, int i3, int i22) {
                super(anonymousClass122, i3, i22);
            }

            @Override
            public void dismiss() {
                super.dismiss();
                LaunchActivity.this.selectAnimatedEmojiDialog = null;
            }
        };
        this.selectAnimatedEmojiDialog = anonymousClass13;
        selectAnimatedEmojiDialogWindowArr2[0] = anonymousClass13;
        anonymousClass13.showAsDropDown(this.sideMenu.getChildAt(0), 0, i2, 48);
        selectAnimatedEmojiDialogWindowArr2[0].dimBehind();
    }

    public void switchToAccount(int i, boolean z) {
        switchToAccount(i, z, new GenericProvider() {
            @Override
            public final Object provide(Object obj) {
                DialogsActivity lambda$switchToAccount$12;
                lambda$switchToAccount$12 = LaunchActivity.lambda$switchToAccount$12((Void) obj);
                return lambda$switchToAccount$12;
            }
        });
    }

    public void switchToAccount(int i, boolean z, GenericProvider genericProvider) {
        if (i == UserConfig.selectedAccount || !UserConfig.isValidAccount(i)) {
            return;
        }
        this.switchingAccount = true;
        ConnectionsManager.getInstance(this.currentAccount).setAppPaused(true, false);
        UserConfig.selectedAccount = i;
        UserConfig.getInstance(0).saveConfig(false);
        checkCurrentAccount();
        if (AndroidUtilities.isTablet()) {
            this.layersActionBarLayout.removeAllFragments();
            this.rightActionBarLayout.removeAllFragments();
            if (!this.tabletFullSize) {
                this.shadowTabletSide.setVisibility(0);
                if (this.rightActionBarLayout.getFragmentStack().isEmpty()) {
                    this.backgroundTablet.setVisibility(0);
                }
                this.rightActionBarLayout.getView().setVisibility(8);
            }
            this.layersActionBarLayout.getView().setVisibility(8);
        }
        if (z) {
            this.actionBarLayout.removeAllFragments();
        } else {
            this.actionBarLayout.removeFragmentFromStack(0);
        }
        DialogsActivity dialogsActivity = (DialogsActivity) genericProvider.provide(null);
        dialogsActivity.setSideMenu(this.sideMenu);
        this.actionBarLayout.addFragmentToStack(dialogsActivity, -3);
        this.drawerLayoutContainer.setAllowOpenDrawer(true, false);
        this.actionBarLayout.rebuildFragments(1);
        if (AndroidUtilities.isTablet()) {
            this.layersActionBarLayout.rebuildFragments(1);
            this.rightActionBarLayout.rebuildFragments(1);
        }
        if (!ApplicationLoader.mainInterfacePaused) {
            ConnectionsManager.getInstance(this.currentAccount).setAppPaused(false, false);
        }
        if (UserConfig.getInstance(i).unacceptedTermsOfService != null) {
            showTosActivity(i, UserConfig.getInstance(i).unacceptedTermsOfService);
        }
        updateCurrentConnectionState(this.currentAccount);
        this.switchingAccount = false;
    }

    public void whenWebviewShareAPIDone(Utilities.Callback callback) {
        this.webviewShareAPIDoneListener = callback;
    }
}
