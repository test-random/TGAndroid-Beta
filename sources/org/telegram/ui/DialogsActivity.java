package org.telegram.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.AnimatedVectorDrawable;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.SpannableStringBuilder;
import android.text.TextPaint;
import android.text.TextUtils;
import android.transition.ChangeBounds;
import android.transition.TransitionManager;
import android.util.LongSparseArray;
import android.util.Property;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.ViewTreeObserver;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.ColorUtils;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSmoothScrollerCustom;
import androidx.recyclerview.widget.RecyclerView;
import androidx.vectordrawable.graphics.drawable.AnimatedVectorDrawableCompat$$ExternalSyntheticApiModelOutline0;
import androidx.viewpager.widget.ViewPager;
import j$.util.concurrent.ConcurrentHashMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import org.telegram.messenger.AccountInstance;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.AnimationNotificationsLocker;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.BirthdayController;
import org.telegram.messenger.BotWebViewVibrationEffect;
import org.telegram.messenger.BuildVars;
import org.telegram.messenger.ChatObject;
import org.telegram.messenger.ContactsController;
import org.telegram.messenger.DialogObject;
import org.telegram.messenger.FileLoader;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.ImageLoader;
import org.telegram.messenger.LiteMode;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MediaDataController;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.MessagesStorage;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.NotificationsController;
import org.telegram.messenger.R;
import org.telegram.messenger.SharedConfig;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.UserObject;
import org.telegram.messenger.Utilities;
import org.telegram.messenger.XiaomiUtilities;
import org.telegram.messenger.browser.Browser;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.SerializedData;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.tgnet.tl.TL_chatlists;
import org.telegram.tgnet.tl.TL_stars;
import org.telegram.tgnet.tl.TL_stories;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.ActionBarMenu;
import org.telegram.ui.ActionBar.ActionBarMenuItem;
import org.telegram.ui.ActionBar.ActionBarMenuSubItem;
import org.telegram.ui.ActionBar.ActionBarPopupWindow;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.BackDrawable;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.BottomSheet;
import org.telegram.ui.ActionBar.INavigationLayout;
import org.telegram.ui.ActionBar.MenuDrawable;
import org.telegram.ui.ActionBar.SimpleTextView;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ActionBar.ThemeDescription;
import org.telegram.ui.Adapters.DialogsAdapter;
import org.telegram.ui.Adapters.DialogsSearchAdapter;
import org.telegram.ui.Adapters.FiltersView;
import org.telegram.ui.Cells.ArchiveHintInnerCell;
import org.telegram.ui.Cells.DialogCell;
import org.telegram.ui.Cells.DialogsEmptyCell;
import org.telegram.ui.Cells.DialogsHintCell;
import org.telegram.ui.Cells.DividerCell;
import org.telegram.ui.Cells.DrawerActionCell;
import org.telegram.ui.Cells.DrawerAddCell;
import org.telegram.ui.Cells.DrawerProfileCell;
import org.telegram.ui.Cells.DrawerUserCell;
import org.telegram.ui.Cells.GraySectionCell;
import org.telegram.ui.Cells.HashtagSearchCell;
import org.telegram.ui.Cells.HeaderCell;
import org.telegram.ui.Cells.HintDialogCell;
import org.telegram.ui.Cells.LoadingCell;
import org.telegram.ui.Cells.ProfileSearchCell;
import org.telegram.ui.Cells.RequestPeerRequirementsCell;
import org.telegram.ui.Cells.ShadowSectionCell;
import org.telegram.ui.Cells.TextCell;
import org.telegram.ui.Cells.TextInfoPrivacyCell;
import org.telegram.ui.Cells.UnconfirmedAuthHintCell;
import org.telegram.ui.Cells.UserCell;
import org.telegram.ui.Components.AlertsCreator;
import org.telegram.ui.Components.AnimatedEmojiDrawable;
import org.telegram.ui.Components.AnimationProperties;
import org.telegram.ui.Components.ArchiveHelp;
import org.telegram.ui.Components.BlurredRecyclerView;
import org.telegram.ui.Components.Bulletin;
import org.telegram.ui.Components.BulletinFactory;
import org.telegram.ui.Components.ChatActivityEnterView;
import org.telegram.ui.Components.ChatAvatarContainer;
import org.telegram.ui.Components.CombinedDrawable;
import org.telegram.ui.Components.CubicBezierInterpolator;
import org.telegram.ui.Components.DialogsItemAnimator;
import org.telegram.ui.Components.FilterTabsView;
import org.telegram.ui.Components.FiltersListBottomSheet;
import org.telegram.ui.Components.FlickerLoadingView;
import org.telegram.ui.Components.FloatingDebug.FloatingDebugController;
import org.telegram.ui.Components.FloatingDebug.FloatingDebugProvider;
import org.telegram.ui.Components.FolderBottomSheet;
import org.telegram.ui.Components.ForegroundColorSpanThemable;
import org.telegram.ui.Components.FragmentContextView;
import org.telegram.ui.Components.ItemOptions;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.MediaActivity;
import org.telegram.ui.Components.NumberTextView;
import org.telegram.ui.Components.PacmanAnimation;
import org.telegram.ui.Components.PermissionRequest;
import org.telegram.ui.Components.Premium.LimitReachedBottomSheet;
import org.telegram.ui.Components.Premium.PremiumFeatureBottomSheet;
import org.telegram.ui.Components.Premium.boosts.UserSelectorBottomSheet;
import org.telegram.ui.Components.ProxyDrawable;
import org.telegram.ui.Components.PullForegroundDrawable;
import org.telegram.ui.Components.RLottieDrawable;
import org.telegram.ui.Components.RLottieImageView;
import org.telegram.ui.Components.RadialProgress2;
import org.telegram.ui.Components.RadialProgressView;
import org.telegram.ui.Components.Reactions.ReactionsLayoutInBubble;
import org.telegram.ui.Components.RecyclerAnimationScrollHelper;
import org.telegram.ui.Components.RecyclerItemsEnterAnimator;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.Components.SearchViewPager;
import org.telegram.ui.Components.SharedMediaLayout;
import org.telegram.ui.Components.SimpleThemeDescription;
import org.telegram.ui.Components.SizeNotifierFrameLayout;
import org.telegram.ui.Components.StickerEmptyView;
import org.telegram.ui.Components.UndoView;
import org.telegram.ui.Components.ViewPagerFixed;
import org.telegram.ui.DialogsActivity;
import org.telegram.ui.FilterCreateActivity;
import org.telegram.ui.FilteredSearchView;
import org.telegram.ui.Gifts.GiftSheet;
import org.telegram.ui.GroupCreateFinalActivity;
import org.telegram.ui.SelectAnimatedEmojiDialog;
import org.telegram.ui.Stars.StarsController;
import org.telegram.ui.Stars.StarsIntroActivity;
import org.telegram.ui.Stories.DialogStoriesCell;
import org.telegram.ui.Stories.StealthModeAlert;
import org.telegram.ui.Stories.StoriesController;
import org.telegram.ui.Stories.StoriesListPlaceProvider;
import org.telegram.ui.Stories.UserListPoller;
import org.telegram.ui.Stories.recorder.HintView2;
import org.telegram.ui.Stories.recorder.StoryRecorder;
import org.telegram.ui.bots.BotWebViewSheet;

public class DialogsActivity extends BaseFragment implements NotificationCenter.NotificationCenterDelegate, FloatingDebugProvider {
    public static boolean[] dialogsLoaded = new boolean[4];
    private static final Interpolator interpolator = new Interpolator() {
        @Override
        public final float getInterpolation(float f) {
            float lambda$static$0;
            lambda$static$0 = DialogsActivity.lambda$static$0(f);
            return lambda$static$0;
        }
    };
    public static float viewOffset = 0.0f;
    private final String ACTION_MODE_SEARCH_DIALOGS_TAG;
    public final Property SCROLL_Y;
    public final Property SEARCH_TRANSLATION_Y;
    private ValueAnimator actionBarColorAnimator;
    private Paint actionBarDefaultPaint;
    private int actionModeAdditionalHeight;
    private boolean actionModeFullyShowed;
    private final ArrayList actionModeViews;
    private ActionBarMenuSubItem addToFolderItem;
    private String addToGroupAlertString;
    private float additionalFloatingTranslation;
    private float additionalFloatingTranslation2;
    private float additionalOffset;
    private boolean afterSignup;
    public boolean allowBots;
    public boolean allowChannels;
    private boolean allowGlobalSearch;
    public boolean allowGroups;
    public boolean allowLegacyGroups;
    public boolean allowMegagroups;
    private boolean allowMoving;
    private boolean allowSwipeDuringCurrentTouch;
    private boolean allowSwitchAccount;
    public boolean allowUsers;
    private boolean animateToHasStories;
    private DrawerProfileCell.AnimatedStatusView animatedStatusView;
    private boolean animatingForward;
    private ActionBarMenuItem archive2Item;
    private ActionBarMenuSubItem archiveItem;
    private boolean askAboutContacts;
    private boolean askingForPermissions;
    private UnconfirmedAuthHintCell authHintCell;
    private boolean authHintCellAnimating;
    private ValueAnimator authHintCellAnimator;
    private float authHintCellProgress;
    private boolean authHintCellVisible;
    private ChatAvatarContainer avatarContainer;
    private boolean backAnimation;
    private BackDrawable backDrawable;
    private ActionBarMenuSubItem blockItem;
    private View blurredView;
    private ArrayList botShareDialogs;
    private Long cacheSize;
    private int canClearCacheCount;
    private boolean canDeletePsaSelected;
    private int canMuteCount;
    private int canPinCount;
    private int canReadCount;
    private int canReportSpamCount;
    private boolean canSelectTopics;
    private boolean canShowFilterTabsView;
    private boolean canShowHiddenArchive;
    private boolean canShowStoryHint;
    private int canUnarchiveCount;
    private int canUnmuteCount;
    private boolean cantSendToChannels;
    private boolean checkCanWrite;
    private boolean checkPermission;
    private boolean checkingImportDialog;
    private ActionBarMenuSubItem clearItem;
    private boolean closeFragment;
    private boolean closeSearchFieldOnHide;
    private ChatActivityEnterView commentView;
    private final boolean commentViewAnimated;
    private AnimatorSet commentViewAnimator;
    private View commentViewBg;
    private boolean commentViewIgnoreTopUpdate;
    private int commentViewPreviousTop;
    private float contactsAlpha;
    private ValueAnimator contactsAlphaAnimator;
    private int currentConnectionState;
    View databaseMigrationHint;
    private int debugLastUpdateAction;
    private DialogsActivityDelegate delegate;
    private ActionBarMenuItem deleteItem;
    private Long deviceSize;
    public DialogStoriesCell dialogStoriesCell;
    public boolean dialogStoriesCellVisible;
    private DialogsHintCell dialogsHintCell;
    private boolean dialogsHintCellVisible;
    private boolean dialogsListFrozen;
    private boolean disableActionBarScrolling;
    private ActionBarMenuItem doneItem;
    private AnimatorSet doneItemAnimator;
    private ActionBarMenuItem downloadsItem;
    private boolean downloadsItemVisible;
    private ItemOptions filterOptions;
    private float filterTabsMoveFrom;
    private float filterTabsProgress;
    private FilterTabsView filterTabsView;
    private boolean filterTabsViewIsVisible;
    private ValueAnimator filtersTabAnimator;
    private FiltersView filtersView;
    private boolean fixScrollYAfterArchiveOpened;
    private RadialProgressView floating2ProgressView;
    private RLottieImageView floatingButton;
    private RLottieImageView floatingButton2;
    private FrameLayout floatingButton2Container;
    private FrameLayout floatingButtonContainer;
    private float floatingButtonHideProgress;
    private float floatingButtonPanOffset;
    private float floatingButtonTranslation;
    private boolean floatingForceVisible;
    private boolean floatingHidden;
    private final AccelerateDecelerateInterpolator floatingInterpolator;
    private AnimatorSet floatingProgressAnimator;
    private boolean floatingProgressVisible;
    private int folderId;
    private int forumCount;
    private int fragmentContextTopPadding;
    private FragmentContextView fragmentContextView;
    private FragmentContextView fragmentLocationContextView;
    private ArrayList frozenDialogsList;
    private boolean hasInvoice;
    public boolean hasOnlySlefStories;
    private int hasPoll;
    public boolean hasStories;
    private int initialDialogsType;
    private String initialSearchString;
    private int initialSearchType;
    private boolean invalidateScrollY;
    boolean isDrawerTransition;
    private boolean isFirstTab;
    private boolean isNextButton;
    private boolean isPremiumHintUpgrade;
    public boolean isQuote;
    public boolean isReplyTo;
    boolean isSlideBackTransition;
    private int lastMeasuredTopPadding;
    private int maximumVelocity;
    private boolean maybeStartTracking;
    private MenuDrawable menuDrawable;
    private int messagesCount;
    private ArrayList movingDialogFilters;
    private DialogCell movingView;
    private boolean movingWas;
    private ActionBarMenuItem muteItem;
    private AnimationNotificationsLocker notificationsLocker;
    public boolean notify;
    private boolean onlySelect;
    private MessagesStorage.TopicKey openedDialogId;
    private ActionBarMenuItem optionsItem;
    private int otherwiseReloginDays;
    private PacmanAnimation pacmanAnimation;
    private Paint paint;
    float panTranslationY;
    private RLottieDrawable passcodeDrawable;
    private ActionBarMenuItem passcodeItem;
    private boolean passcodeItemVisible;
    private AlertDialog permissionDialog;
    private ActionBarMenuSubItem pin2Item;
    private ActionBarMenuItem pinItem;
    private Drawable premiumStar;
    private int prevPosition;
    private int prevTop;
    private float progressToActionMode;
    public float progressToDialogStoriesCell;
    public float progressToShowStories;
    private ProxyDrawable proxyDrawable;
    private ActionBarMenuItem proxyItem;
    private boolean proxyItemVisible;
    private ActionBarMenuSubItem readItem;
    private RectF rect;
    private ActionBarMenuSubItem removeFromFolderItem;
    private long requestPeerBotId;
    private TLRPC.RequestPeerType requestPeerType;
    private boolean resetDelegate;
    private boolean rightFragmentTransitionInProgress;
    private boolean rightFragmentTransitionIsOpen;
    public RightSlidingDialogContainer rightSlidingDialogContainer;
    public int scheduleDate;
    private float scrollAdditionalOffset;
    private boolean scrollBarVisible;
    private boolean scrollUpdated;
    private float scrollYOffset;
    private boolean scrollingManually;
    private float searchAnimationProgress;
    private boolean searchAnimationTabsDelayedCrossfade;
    private AnimatorSet searchAnimator;
    private long searchDialogId;
    private boolean searchFiltersWasShowed;
    private boolean searchIsShowed;
    private ActionBarMenuItem searchItem;
    private TLObject searchObject;
    private String searchString;
    private ViewPagerFixed.TabsView searchTabsView;
    private SearchViewPager searchViewPager;
    private int searchViewPagerIndex;
    float searchViewPagerTranslationY;
    private boolean searchWas;
    private boolean searchWasFullyShowed;
    private boolean searching;
    private String selectAlertString;
    private String selectAlertStringGroup;
    private SelectAnimatedEmojiDialog.SelectAnimatedEmojiDialogWindow selectAnimatedEmojiDialog;
    private View selectedCountView;
    private ArrayList selectedDialogs;
    private NumberTextView selectedDialogsCountTextView;
    private ActionBarPopupWindow sendPopupWindow;
    private SharedMediaLayout.SharedMediaPreloader sharedMediaPreloader;
    private int shiftDp;
    private boolean showSetPasswordConfirm;
    private String showingSuggestion;
    private RecyclerView sideMenu;
    final int slideAmplitudeDp;
    ValueAnimator slideBackTransitionAnimator;
    boolean slideFragmentLite;
    float slideFragmentProgress;
    private DialogCell slidingView;
    private boolean slowedReloadAfterDialogClick;
    private AnimatorSet speedAnimator;
    private ActionBarMenuItem speedItem;
    private long startArchivePullingTime;
    private boolean startedTracking;
    private AnimatedEmojiDrawable.SwapAnimatedEmojiDrawable statusDrawable;
    private Bulletin storiesBulletin;
    public boolean storiesEnabled;
    private float storiesOverscroll;
    private boolean storiesOverscrollCalled;
    ValueAnimator storiesVisibilityAnimator;
    ValueAnimator storiesVisibilityAnimator2;
    private float storiesYOffset;
    private HintView2 storyHint;
    private boolean storyHintShown;
    private ActionBarMenuItem switchItem;
    private Animator tabsAlphaAnimator;
    private AnimatorSet tabsAnimation;
    private boolean tabsAnimationInProgress;
    private float tabsYOffset;
    private TextPaint textPaint;
    private Bulletin topBulletin;
    private int topPadding;
    private UndoView[] undoView;
    private int undoViewIndex;
    private FrameLayout updateLayout;
    private AnimatorSet updateLayoutAnimator;
    private RadialProgress2 updateLayoutIcon;
    private boolean updatePullAfterScroll;
    private TextView updateTextView;
    private ViewPage[] viewPages;
    private boolean waitingForScrollFinished;
    private boolean wasDrawn;
    public boolean whiteActionBar;
    private ImageView[] writeButton;
    private FrameLayout writeButtonContainer;

    public class AnonymousClass11 extends LinearLayoutManager {
        private boolean fixOffset;
        boolean lastDragging;
        ValueAnimator storiesOverscrollAnimator;
        final ViewPage val$viewPage;

        AnonymousClass11(Context context, ViewPage viewPage) {
            super(context);
            this.val$viewPage = viewPage;
        }

        public static void lambda$onLayoutChildren$1(ViewPage viewPage) {
            viewPage.dialogsAdapter.notifyDataSetChanged();
        }

        public void lambda$onScrollStateChanged$0(ViewPage viewPage, ValueAnimator valueAnimator) {
            DialogsActivity.this.setStoriesOvercroll(viewPage, ((Float) valueAnimator.getAnimatedValue()).floatValue());
        }

        @Override
        protected int firstPosition() {
            return (this.val$viewPage.dialogsType == 0 && DialogsActivity.this.hasHiddenArchive() && this.val$viewPage.archivePullViewState == 2) ? 1 : 0;
        }

        @Override
        public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
            if (!BuildVars.DEBUG_PRIVATE_VERSION) {
                try {
                    super.onLayoutChildren(recycler, state);
                    return;
                } catch (IndexOutOfBoundsException e) {
                    FileLog.e(e);
                    final ViewPage viewPage = this.val$viewPage;
                    AndroidUtilities.runOnUIThread(new Runnable() {
                        @Override
                        public final void run() {
                            DialogsActivity.AnonymousClass11.lambda$onLayoutChildren$1(DialogsActivity.ViewPage.this);
                        }
                    });
                    return;
                }
            }
            try {
                super.onLayoutChildren(recycler, state);
            } catch (IndexOutOfBoundsException unused) {
                throw new RuntimeException("Inconsistency detected. dialogsListIsFrozen=" + DialogsActivity.this.dialogsListFrozen + " lastUpdateAction=" + DialogsActivity.this.debugLastUpdateAction);
            }
        }

        @Override
        public void onScrollStateChanged(int i) {
            super.onScrollStateChanged(i);
            ValueAnimator valueAnimator = this.storiesOverscrollAnimator;
            if (valueAnimator != null) {
                valueAnimator.removeAllListeners();
                this.storiesOverscrollAnimator.cancel();
            }
            if (this.val$viewPage.listView.getScrollState() != 1) {
                ValueAnimator ofFloat = ValueAnimator.ofFloat(DialogsActivity.this.storiesOverscroll, 0.0f);
                this.storiesOverscrollAnimator = ofFloat;
                final ViewPage viewPage = this.val$viewPage;
                ofFloat.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public final void onAnimationUpdate(ValueAnimator valueAnimator2) {
                        DialogsActivity.AnonymousClass11.this.lambda$onScrollStateChanged$0(viewPage, valueAnimator2);
                    }
                });
                this.storiesOverscrollAnimator.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animator) {
                        AnonymousClass11 anonymousClass11 = AnonymousClass11.this;
                        DialogsActivity.this.setStoriesOvercroll(anonymousClass11.val$viewPage, 0.0f);
                    }
                });
                this.storiesOverscrollAnimator.setDuration(200L);
                this.storiesOverscrollAnimator.setInterpolator(CubicBezierInterpolator.DEFAULT);
                this.storiesOverscrollAnimator.start();
            }
        }

        @Override
        public void prepareForDrop(View view, View view2, int i, int i2) {
            this.fixOffset = true;
            super.prepareForDrop(view, view2, i, i2);
            this.fixOffset = false;
        }

        @Override
        public void scrollToPositionWithOffset(int i, int i2) {
            if (this.fixOffset) {
                i2 -= this.val$viewPage.listView.getPaddingTop();
            }
            super.scrollToPositionWithOffset(i, i2);
        }

        @Override
        public int scrollVerticallyBy(int r19, androidx.recyclerview.widget.RecyclerView.Recycler r20, androidx.recyclerview.widget.RecyclerView.State r21) {
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.DialogsActivity.AnonymousClass11.scrollVerticallyBy(int, androidx.recyclerview.widget.RecyclerView$Recycler, androidx.recyclerview.widget.RecyclerView$State):int");
        }

        @Override
        public void smoothScrollToPosition(RecyclerView recyclerView, RecyclerView.State state, int i) {
            if (DialogsActivity.this.hasHiddenArchive() && i == 1) {
                super.smoothScrollToPosition(recyclerView, state, i);
                return;
            }
            LinearSmoothScrollerCustom linearSmoothScrollerCustom = new LinearSmoothScrollerCustom(recyclerView.getContext(), 0);
            linearSmoothScrollerCustom.setTargetPosition(i);
            startSmoothScroll(linearSmoothScrollerCustom);
        }
    }

    public class AnonymousClass26 extends DialogStoriesCell {
        AnonymousClass26(Context context, BaseFragment baseFragment, int i, int i2) {
            super(context, baseFragment, i, i2);
        }

        public void lambda$onUserLongPressed$0() {
            DialogsActivity.this.dialogStoriesCell.openStoryRecorder();
        }

        public void lambda$onUserLongPressed$1() {
            Bundle bundle = new Bundle();
            bundle.putLong("dialog_id", UserConfig.getInstance(((BaseFragment) DialogsActivity.this).currentAccount).getClientUserId());
            bundle.putInt("type", 1);
            bundle.putInt("start_from", 9);
            DialogsActivity.this.presentFragment(new MediaActivity(bundle, null));
        }

        public void lambda$onUserLongPressed$10(final View view) {
            TL_stories.TL_storiesStealthMode stealthMode = MessagesController.getInstance(UserConfig.selectedAccount).getStoriesController().getStealthMode();
            if (stealthMode == null || ConnectionsManager.getInstance(((BaseFragment) DialogsActivity.this).currentAccount).getCurrentTime() >= stealthMode.active_until_date) {
                StealthModeAlert stealthModeAlert = new StealthModeAlert(getContext(), 0.0f, 1, ((BaseFragment) DialogsActivity.this).resourceProvider);
                stealthModeAlert.setListener(new StealthModeAlert.Listener() {
                    @Override
                    public final void onButtonClicked(boolean z) {
                        DialogsActivity.AnonymousClass26.this.lambda$onUserLongPressed$9(view, z);
                    }
                });
                DialogsActivity.this.showDialog(stealthModeAlert);
            } else if (view instanceof DialogStoriesCell.StoryCell) {
                DialogsActivity.this.dialogStoriesCell.openStoryForCell((DialogStoriesCell.StoryCell) view);
            }
        }

        public void lambda$onUserLongPressed$11(View view, boolean z) {
            if (view instanceof DialogStoriesCell.StoryCell) {
                DialogsActivity.this.dialogStoriesCell.openStoryForCell((DialogStoriesCell.StoryCell) view);
                if (z) {
                    AndroidUtilities.runOnUIThread(new DialogsActivity$26$$ExternalSyntheticLambda15(), 500L);
                }
            }
        }

        public void lambda$onUserLongPressed$12(final View view) {
            StealthModeAlert stealthModeAlert = new StealthModeAlert(getContext(), 0.0f, 1, ((BaseFragment) DialogsActivity.this).resourceProvider);
            stealthModeAlert.setListener(new StealthModeAlert.Listener() {
                @Override
                public final void onButtonClicked(boolean z) {
                    DialogsActivity.AnonymousClass26.this.lambda$onUserLongPressed$11(view, z);
                }
            });
            DialogsActivity.this.showDialog(stealthModeAlert);
        }

        public void lambda$onUserLongPressed$13(long j) {
            DialogsActivity.this.toggleArciveForStory(j);
        }

        public void lambda$onUserLongPressed$14(long j) {
            DialogsActivity.this.toggleArciveForStory(j);
        }

        public void lambda$onUserLongPressed$2() {
            Bundle bundle = new Bundle();
            bundle.putLong("dialog_id", UserConfig.getInstance(((BaseFragment) DialogsActivity.this).currentAccount).getClientUserId());
            bundle.putInt("type", 1);
            DialogsActivity.this.presentFragment(new MediaActivity(bundle, null));
        }

        public void lambda$onUserLongPressed$3(long j) {
            DialogsActivity.this.dialogStoriesCell.openStoryRecorder(j);
        }

        public void lambda$onUserLongPressed$4(long j) {
            DialogsActivity.this.presentFragment(ChatActivity.of(j));
        }

        public void lambda$onUserLongPressed$5(long j) {
            DialogsActivity.this.presentFragment(ProfileActivity.of(j));
        }

        public void lambda$onUserLongPressed$6(long j) {
            DialogsActivity.this.presentFragment(ChatActivity.of(j));
        }

        public void lambda$onUserLongPressed$7(String str, long j) {
            MessagesController.getNotificationsSettings(((BaseFragment) DialogsActivity.this).currentAccount).edit().putBoolean("stories_" + str, false).apply();
            DialogsActivity.this.getNotificationsController().updateServerNotificationsSettings(j, 0L);
            TLRPC.User user = MessagesController.getInstance(((BaseFragment) DialogsActivity.this).currentAccount).getUser(Long.valueOf(j));
            String trim = user == null ? "" : user.first_name.trim();
            int indexOf = trim.indexOf(" ");
            if (indexOf > 0) {
                trim = trim.substring(0, indexOf);
            }
            BulletinFactory.of(DialogsActivity.this).createUsersBulletin(Arrays.asList(user), AndroidUtilities.replaceTags(LocaleController.formatString("NotificationsStoryMutedHint", R.string.NotificationsStoryMutedHint, trim))).show();
        }

        public void lambda$onUserLongPressed$8(String str, long j) {
            MessagesController.getNotificationsSettings(((BaseFragment) DialogsActivity.this).currentAccount).edit().putBoolean("stories_" + str, true).apply();
            DialogsActivity.this.getNotificationsController().updateServerNotificationsSettings(j, 0L);
            TLRPC.User user = MessagesController.getInstance(((BaseFragment) DialogsActivity.this).currentAccount).getUser(Long.valueOf(j));
            String trim = user == null ? "" : user.first_name.trim();
            int indexOf = trim.indexOf(" ");
            if (indexOf > 0) {
                trim = trim.substring(0, indexOf);
            }
            BulletinFactory.of(DialogsActivity.this).createUsersBulletin(Arrays.asList(user), AndroidUtilities.replaceTags(LocaleController.formatString("NotificationsStoryUnmutedHint", R.string.NotificationsStoryUnmutedHint, trim))).show();
        }

        public void lambda$onUserLongPressed$9(View view, boolean z) {
            if (view instanceof DialogStoriesCell.StoryCell) {
                DialogsActivity.this.dialogStoriesCell.openStoryForCell((DialogStoriesCell.StoryCell) view);
                if (z) {
                    AndroidUtilities.runOnUIThread(new DialogsActivity$26$$ExternalSyntheticLambda15(), 500L);
                }
            }
        }

        @Override
        public void lambda$new$0() {
            DialogsActivity dialogsActivity = DialogsActivity.this;
            if (dialogsActivity.hasOnlySlefStories && dialogsActivity.getStoriesController().hasOnlySelfStories()) {
                DialogsActivity.this.dialogStoriesCell.openSelfStories();
            } else {
                DialogsActivity.this.scrollToTop(true, true);
            }
        }

        @Override
        public void onUserLongPressed(final View view, final long j) {
            CombinedDrawable combinedDrawable;
            Drawable drawable;
            DialogsActivity dialogsActivity = DialogsActivity.this;
            dialogsActivity.filterOptions = ItemOptions.makeOptions(dialogsActivity, view).setViewAdditionalOffsets(0, AndroidUtilities.dp(8.0f), 0, 0).setScrimViewBackground(Theme.createRoundRectDrawable(AndroidUtilities.dp(6.0f), DialogsActivity.this.canShowFilterTabsView ? AndroidUtilities.dp(6.0f) : 0, Theme.getColor(DialogsActivity.this.isArchive() ? Theme.key_actionBarDefaultArchived : Theme.key_actionBarDefault)));
            if (UserObject.isService(j)) {
                BotWebViewVibrationEffect.APP_ERROR.vibrate();
                return;
            }
            view.performHapticFeedback(0);
            if (j == UserConfig.getInstance(((BaseFragment) DialogsActivity.this).currentAccount).getClientUserId()) {
                DialogsActivity dialogsActivity2 = DialogsActivity.this;
                if (!dialogsActivity2.storiesEnabled) {
                    DialogStoriesCell dialogStoriesCell = dialogsActivity2.dialogStoriesCell;
                    if (dialogStoriesCell != null) {
                        dialogStoriesCell.showPremiumHint();
                        return;
                    }
                    return;
                }
                ItemOptions itemOptions = dialogsActivity2.filterOptions;
                int i = R.drawable.msg_stories_add;
                String string = LocaleController.getString(R.string.AddStory);
                int i2 = Theme.key_actionBarDefaultSubmenuItemIcon;
                int i3 = Theme.key_actionBarDefaultSubmenuItem;
                itemOptions.add(i, string, i2, i3, new Runnable() {
                    @Override
                    public final void run() {
                        DialogsActivity.AnonymousClass26.this.lambda$onUserLongPressed$0();
                    }
                });
                DialogsActivity.this.filterOptions.add(R.drawable.msg_stories_archive, LocaleController.getString(R.string.ArchivedStories), i2, i3, new Runnable() {
                    @Override
                    public final void run() {
                        DialogsActivity.AnonymousClass26.this.lambda$onUserLongPressed$1();
                    }
                });
                DialogsActivity.this.filterOptions.add(R.drawable.msg_stories_saved, LocaleController.getString(R.string.SavedStories), i2, i3, new Runnable() {
                    @Override
                    public final void run() {
                        DialogsActivity.AnonymousClass26.this.lambda$onUserLongPressed$2();
                    }
                });
            } else {
                TLRPC.Chat chat = DialogsActivity.this.getMessagesController().getChat(Long.valueOf(-j));
                final String sharedPrefKey = NotificationsController.getSharedPrefKey(j, 0L);
                boolean z = !NotificationsCustomSettingsActivity.areStoriesNotMuted(((BaseFragment) DialogsActivity.this).currentAccount, j);
                boolean premiumFeaturesBlocked = MessagesController.getInstance(((BaseFragment) DialogsActivity.this).currentAccount).premiumFeaturesBlocked();
                boolean isPremium = UserConfig.getInstance(((BaseFragment) DialogsActivity.this).currentAccount).isPremium();
                boolean hasUnreadStories = MessagesController.getInstance(UserConfig.selectedAccount).getStoriesController().hasUnreadStories(j);
                if (premiumFeaturesBlocked || j <= 0 || isPremium || (drawable = ContextCompat.getDrawable(getContext(), R.drawable.msg_gallery_locked2)) == null) {
                    combinedDrawable = null;
                } else {
                    Drawable drawable2 = ContextCompat.getDrawable(getContext(), R.drawable.msg_stealth_locked);
                    if (drawable2 != null) {
                        drawable2.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_actionBarDefaultSubmenuItemIcon, ((BaseFragment) DialogsActivity.this).resourceProvider), PorterDuff.Mode.MULTIPLY));
                    }
                    drawable.setColorFilter(new PorterDuffColorFilter(ColorUtils.blendARGB(-1, -16777216, 0.5f), PorterDuff.Mode.MULTIPLY));
                    combinedDrawable = new CombinedDrawable(drawable2, drawable);
                }
                CombinedDrawable combinedDrawable2 = combinedDrawable;
                if (j < 0 && DialogsActivity.this.getStoriesController().canPostStories(j)) {
                    DialogsActivity.this.filterOptions.add(R.drawable.msg_stories_add, LocaleController.getString(R.string.AddStory), Theme.key_actionBarDefaultSubmenuItemIcon, Theme.key_actionBarDefaultSubmenuItem, new Runnable() {
                        @Override
                        public final void run() {
                            DialogsActivity.AnonymousClass26.this.lambda$onUserLongPressed$3(j);
                        }
                    });
                }
                ItemOptions makeMultiline = DialogsActivity.this.filterOptions.addIf(j > 0, R.drawable.msg_discussion, LocaleController.getString(R.string.SendMessage), new Runnable() {
                    @Override
                    public final void run() {
                        DialogsActivity.AnonymousClass26.this.lambda$onUserLongPressed$4(j);
                    }
                }).addIf(j > 0, R.drawable.msg_openprofile, LocaleController.getString(R.string.OpenProfile), new Runnable() {
                    @Override
                    public final void run() {
                        DialogsActivity.AnonymousClass26.this.lambda$onUserLongPressed$5(j);
                    }
                }).addIf(j < 0, R.drawable.msg_channel, LocaleController.getString(ChatObject.isChannelAndNotMegaGroup(chat) ? R.string.OpenChannel2 : R.string.OpenGroup2), new Runnable() {
                    @Override
                    public final void run() {
                        DialogsActivity.AnonymousClass26.this.lambda$onUserLongPressed$6(j);
                    }
                }).addIf(!z && j > 0, R.drawable.msg_mute, LocaleController.getString(R.string.NotificationsStoryMute2), new Runnable() {
                    @Override
                    public final void run() {
                        DialogsActivity.AnonymousClass26.this.lambda$onUserLongPressed$7(sharedPrefKey, j);
                    }
                }).makeMultiline(false).addIf(z && j > 0, R.drawable.msg_unmute, LocaleController.getString(R.string.NotificationsStoryUnmute2), new Runnable() {
                    @Override
                    public final void run() {
                        DialogsActivity.AnonymousClass26.this.lambda$onUserLongPressed$8(sharedPrefKey, j);
                    }
                }).makeMultiline(false);
                boolean z2 = !premiumFeaturesBlocked && j > 0 && isPremium && hasUnreadStories;
                int i4 = R.drawable.msg_stories_stealth2;
                int i5 = R.string.ViewAnonymously;
                makeMultiline.addIf(z2, i4, LocaleController.getString(i5), new Runnable() {
                    @Override
                    public final void run() {
                        DialogsActivity.AnonymousClass26.this.lambda$onUserLongPressed$10(view);
                    }
                }).makeMultiline(false).addIf(!premiumFeaturesBlocked && j > 0 && !isPremium && hasUnreadStories, i4, combinedDrawable2, LocaleController.getString(i5), new Runnable() {
                    @Override
                    public final void run() {
                        DialogsActivity.AnonymousClass26.this.lambda$onUserLongPressed$12(view);
                    }
                }).makeMultiline(false).addIf(!DialogsActivity.this.isArchive(), R.drawable.msg_archive, LocaleController.getString(R.string.ArchivePeerStories), new Runnable() {
                    @Override
                    public final void run() {
                        DialogsActivity.AnonymousClass26.this.lambda$onUserLongPressed$13(j);
                    }
                }).makeMultiline(false).addIf(DialogsActivity.this.isArchive(), R.drawable.msg_unarchive, LocaleController.getString(R.string.UnarchiveStories), new Runnable() {
                    @Override
                    public final void run() {
                        DialogsActivity.AnonymousClass26.this.lambda$onUserLongPressed$14(j);
                    }
                }).makeMultiline(false);
            }
            DialogsActivity.this.filterOptions.setGravity(3).translate(AndroidUtilities.dp(-8.0f), AndroidUtilities.dp(-10.0f)).show();
        }
    }

    public class AnonymousClass33 implements GroupCreateFinalActivity.GroupCreateFinalActivityDelegate {
        final AlertDialog val$progress;

        AnonymousClass33(AlertDialog alertDialog) {
            this.val$progress = alertDialog;
        }

        public void lambda$didFinishChatCreation$1(long j, BaseFragment[] baseFragmentArr, final Runnable runnable) {
            if (DialogsActivity.this.requestPeerType.has_username == null || !DialogsActivity.this.requestPeerType.has_username.booleanValue()) {
                runnable.run();
                return;
            }
            Bundle bundle = new Bundle();
            bundle.putInt("step", 1);
            bundle.putLong("chat_id", j);
            bundle.putBoolean("forcePublic", DialogsActivity.this.requestPeerType.has_username.booleanValue());
            ChannelCreateActivity channelCreateActivity = new ChannelCreateActivity(bundle);
            channelCreateActivity.setOnFinishListener(new Utilities.Callback2() {
                @Override
                public final void run(Object obj, Object obj2) {
                    runnable.run();
                }
            });
            DialogsActivity.this.presentFragment(channelCreateActivity);
            baseFragmentArr[1] = channelCreateActivity;
        }

        public void lambda$didFinishChatCreation$10(AlertDialog alertDialog, long j, BaseFragment[] baseFragmentArr, Runnable runnable) {
            BaseFragment baseFragment;
            alertDialog.dismiss();
            DialogsActivity.this.getMessagesController().loadChannelParticipants(Long.valueOf(j));
            DialogsActivityDelegate dialogsActivityDelegate = DialogsActivity.this.delegate;
            DialogsActivity.this.removeSelfFromStack();
            if (baseFragmentArr[1] != null) {
                baseFragmentArr[0].removeSelfFromStack();
                baseFragment = baseFragmentArr[1];
            } else {
                baseFragment = baseFragmentArr[0];
            }
            baseFragment.lambda$onBackPressed$319();
            if (dialogsActivityDelegate != null) {
                ArrayList arrayList = new ArrayList();
                arrayList.add(MessagesStorage.TopicKey.of(-j, 0L));
                DialogsActivity dialogsActivity = DialogsActivity.this;
                dialogsActivityDelegate.didSelectDialogs(dialogsActivity, arrayList, null, false, dialogsActivity.notify, dialogsActivity.scheduleDate, null);
            }
        }

        public void lambda$didFinishChatCreation$2(BaseFragment[] baseFragmentArr) {
            BaseFragment baseFragment;
            DialogsActivity.this.removeSelfFromStack();
            if (baseFragmentArr[1] != null) {
                baseFragmentArr[0].removeSelfFromStack();
                baseFragment = baseFragmentArr[1];
            } else {
                baseFragment = baseFragmentArr[0];
            }
            baseFragment.lambda$onBackPressed$319();
        }

        public void lambda$didFinishChatCreation$3(long j, final BaseFragment[] baseFragmentArr, Runnable runnable) {
            DialogsActivity.this.showSendToBotAlert(DialogsActivity.this.getMessagesController().getChat(Long.valueOf(j)), runnable, new Runnable() {
                @Override
                public final void run() {
                    DialogsActivity.AnonymousClass33.this.lambda$didFinishChatCreation$2(baseFragmentArr);
                }
            });
        }

        public static boolean lambda$didFinishChatCreation$4(Runnable runnable, TLRPC.TL_error tL_error) {
            runnable.run();
            return true;
        }

        public void lambda$didFinishChatCreation$5(AlertDialog alertDialog, long j, final Runnable runnable) {
            alertDialog.showDelayed(150L);
            if (DialogsActivity.this.requestPeerType.bot_participant == null || !DialogsActivity.this.requestPeerType.bot_participant.booleanValue()) {
                runnable.run();
            } else {
                DialogsActivity.this.getMessagesController().addUserToChat(j, DialogsActivity.this.getMessagesController().getUser(Long.valueOf(DialogsActivity.this.requestPeerBotId)), 0, null, DialogsActivity.this, false, runnable, new MessagesController.ErrorDelegate() {
                    @Override
                    public final boolean run(TLRPC.TL_error tL_error) {
                        boolean lambda$didFinishChatCreation$4;
                        lambda$didFinishChatCreation$4 = DialogsActivity.AnonymousClass33.lambda$didFinishChatCreation$4(runnable, tL_error);
                        return lambda$didFinishChatCreation$4;
                    }
                });
            }
        }

        public static boolean lambda$didFinishChatCreation$6(Runnable runnable, TLRPC.TL_error tL_error) {
            runnable.run();
            return true;
        }

        public void lambda$didFinishChatCreation$7(long j, final Runnable runnable) {
            if (DialogsActivity.this.requestPeerType.bot_admin_rights == null) {
                runnable.run();
                return;
            }
            TLRPC.User user = DialogsActivity.this.getMessagesController().getUser(Long.valueOf(DialogsActivity.this.requestPeerBotId));
            MessagesController messagesController = DialogsActivity.this.getMessagesController();
            TLRPC.TL_chatAdminRights tL_chatAdminRights = DialogsActivity.this.requestPeerType.bot_admin_rights;
            DialogsActivity dialogsActivity = DialogsActivity.this;
            messagesController.setUserAdminRole(j, user, tL_chatAdminRights, null, false, dialogsActivity, dialogsActivity.requestPeerType.bot_participant == null || !DialogsActivity.this.requestPeerType.bot_participant.booleanValue(), true, null, runnable, new MessagesController.ErrorDelegate() {
                @Override
                public final boolean run(TLRPC.TL_error tL_error) {
                    boolean lambda$didFinishChatCreation$6;
                    lambda$didFinishChatCreation$6 = DialogsActivity.AnonymousClass33.lambda$didFinishChatCreation$6(runnable, tL_error);
                    return lambda$didFinishChatCreation$6;
                }
            });
        }

        public static boolean lambda$didFinishChatCreation$8(Runnable runnable, TLRPC.TL_error tL_error) {
            runnable.run();
            return true;
        }

        public void lambda$didFinishChatCreation$9(long j, final Runnable runnable) {
            if (DialogsActivity.this.requestPeerType.user_admin_rights == null) {
                runnable.run();
            } else {
                DialogsActivity.this.getMessagesController().setUserAdminRole(j, DialogsActivity.this.getAccountInstance().getUserConfig().getCurrentUser(), ChatRightsEditActivity.rightsOR(DialogsActivity.this.getMessagesController().getChat(Long.valueOf(j)).admin_rights, DialogsActivity.this.requestPeerType.user_admin_rights), null, false, DialogsActivity.this, false, true, null, runnable, new MessagesController.ErrorDelegate() {
                    @Override
                    public final boolean run(TLRPC.TL_error tL_error) {
                        boolean lambda$didFinishChatCreation$8;
                        lambda$didFinishChatCreation$8 = DialogsActivity.AnonymousClass33.lambda$didFinishChatCreation$8(runnable, tL_error);
                        return lambda$didFinishChatCreation$8;
                    }
                });
            }
        }

        @Override
        public void didFailChatCreation() {
        }

        @Override
        public void didFinishChatCreation(GroupCreateFinalActivity groupCreateFinalActivity, final long j) {
            final BaseFragment[] baseFragmentArr = {groupCreateFinalActivity, null};
            Utilities.Callback callback = new Utilities.Callback() {
                @Override
                public final void run(Object obj) {
                    DialogsActivity.AnonymousClass33.this.lambda$didFinishChatCreation$1(j, baseFragmentArr, (Runnable) obj);
                }
            };
            Utilities.Callback callback2 = new Utilities.Callback() {
                @Override
                public final void run(Object obj) {
                    DialogsActivity.AnonymousClass33.this.lambda$didFinishChatCreation$3(j, baseFragmentArr, (Runnable) obj);
                }
            };
            final AlertDialog alertDialog = this.val$progress;
            Utilities.Callback callback3 = new Utilities.Callback() {
                @Override
                public final void run(Object obj) {
                    DialogsActivity.AnonymousClass33.this.lambda$didFinishChatCreation$5(alertDialog, j, (Runnable) obj);
                }
            };
            Utilities.Callback callback4 = new Utilities.Callback() {
                @Override
                public final void run(Object obj) {
                    DialogsActivity.AnonymousClass33.this.lambda$didFinishChatCreation$7(j, (Runnable) obj);
                }
            };
            Utilities.Callback callback5 = new Utilities.Callback() {
                @Override
                public final void run(Object obj) {
                    DialogsActivity.AnonymousClass33.this.lambda$didFinishChatCreation$9(j, (Runnable) obj);
                }
            };
            final AlertDialog alertDialog2 = this.val$progress;
            Utilities.doCallbacks(callback, callback2, callback3, callback4, callback5, new Utilities.Callback() {
                @Override
                public final void run(Object obj) {
                    DialogsActivity.AnonymousClass33.this.lambda$didFinishChatCreation$10(alertDialog2, j, baseFragmentArr, (Runnable) obj);
                }
            });
        }

        @Override
        public void didStartChatCreation() {
        }
    }

    public class AnonymousClass45 extends UndoView {
        AnonymousClass45(Context context) {
            super(context);
        }

        public void lambda$onRemoveDialogAction$0(int i, TLRPC.Dialog dialog) {
            if (DialogsActivity.this.frozenDialogsList == null || i < 0 || i >= DialogsActivity.this.frozenDialogsList.size()) {
                return;
            }
            DialogsActivity.this.frozenDialogsList.add(i, dialog);
            DialogsActivity.this.viewPages[0].updateList(true);
        }

        @Override
        protected boolean canUndo() {
            for (int i = 0; i < DialogsActivity.this.viewPages.length; i++) {
                if (DialogsActivity.this.viewPages[i].dialogsItemAnimator.isRunning()) {
                    return false;
                }
            }
            return true;
        }

        @Override
        protected void onRemoveDialogAction(long j, int i) {
            if (i == 1 || i == 27) {
                DialogsActivity.this.debugLastUpdateAction = 1;
                DialogsActivity.this.setDialogsListFrozen(true);
                if (DialogsActivity.this.frozenDialogsList != null) {
                    final int i2 = 0;
                    while (true) {
                        if (i2 >= DialogsActivity.this.frozenDialogsList.size()) {
                            i2 = -1;
                            break;
                        } else if (((TLRPC.Dialog) DialogsActivity.this.frozenDialogsList.get(i2)).id == j) {
                            break;
                        } else {
                            i2++;
                        }
                    }
                    DialogsActivity dialogsActivity = DialogsActivity.this;
                    if (i2 >= 0) {
                        final TLRPC.Dialog dialog = (TLRPC.Dialog) dialogsActivity.frozenDialogsList.remove(i2);
                        DialogsActivity.this.viewPages[0].dialogsAdapter.notifyDataSetChanged();
                        AndroidUtilities.runOnUIThread(new Runnable() {
                            @Override
                            public final void run() {
                                DialogsActivity.AnonymousClass45.this.lambda$onRemoveDialogAction$0(i2, dialog);
                            }
                        });
                    } else {
                        dialogsActivity.setDialogsListFrozen(false);
                    }
                }
                DialogsActivity.this.checkAnimationFinished();
            }
        }

        @Override
        public void setTranslationY(float f) {
            super.setTranslationY(f);
            if (this == DialogsActivity.this.undoView[0]) {
                if (DialogsActivity.this.undoView[1] == null || DialogsActivity.this.undoView[1].getVisibility() != 0) {
                    DialogsActivity.this.additionalFloatingTranslation = (getMeasuredHeight() + AndroidUtilities.dp(8.0f)) - f;
                    if (DialogsActivity.this.additionalFloatingTranslation < 0.0f) {
                        DialogsActivity.this.additionalFloatingTranslation = 0.0f;
                    }
                    if (DialogsActivity.this.floatingHidden) {
                        return;
                    }
                    DialogsActivity.this.updateFloatingButtonOffset();
                }
            }
        }
    }

    public class AnonymousClass5 extends ActionBarMenuItem.ActionBarMenuItemSearchListener {
        boolean isSpeedItemCreated = false;
        final Context val$context;
        final ActionBarMenu val$menu;

        AnonymousClass5(Context context, ActionBarMenu actionBarMenu) {
            this.val$context = context;
            this.val$menu = actionBarMenu;
        }

        public void lambda$onPreToggleSearch$0(View view) {
            DialogsActivity.this.showDialog(new PremiumFeatureBottomSheet(DialogsActivity.this, 2, true));
        }

        @Override
        public boolean canCollapseSearch() {
            if (DialogsActivity.this.switchItem != null) {
                DialogsActivity.this.switchItem.setVisibility(0);
            }
            if (DialogsActivity.this.proxyItem != null && DialogsActivity.this.proxyItemVisible) {
                DialogsActivity.this.proxyItem.setVisibility(0);
            }
            if (DialogsActivity.this.downloadsItem != null && DialogsActivity.this.downloadsItemVisible) {
                DialogsActivity.this.downloadsItem.setVisibility(0);
            }
            if (DialogsActivity.this.searchString == null) {
                return true;
            }
            DialogsActivity.this.lambda$onBackPressed$319();
            return false;
        }

        @Override
        public boolean canToggleSearch() {
            return !((BaseFragment) DialogsActivity.this).actionBar.isActionModeShowed() && DialogsActivity.this.databaseMigrationHint == null;
        }

        @Override
        public void onPreToggleSearch() {
            if (this.isSpeedItemCreated) {
                return;
            }
            this.isSpeedItemCreated = true;
            FrameLayout frameLayout = (FrameLayout) DialogsActivity.this.searchItem.getSearchClearButton().getParent();
            DialogsActivity dialogsActivity = DialogsActivity.this;
            Context context = this.val$context;
            ActionBarMenu actionBarMenu = this.val$menu;
            int color = Theme.getColor(Theme.key_actionBarActionModeDefaultSelector);
            int i = Theme.key_actionBarActionModeDefaultIcon;
            dialogsActivity.speedItem = new ActionBarMenuItem(context, actionBarMenu, color, Theme.getColor(i));
            DialogsActivity.this.speedItem.setIcon(R.drawable.avd_speed);
            DialogsActivity.this.speedItem.getIconView().setColorFilter(new PorterDuffColorFilter(Theme.getColor(i), PorterDuff.Mode.SRC_IN));
            DialogsActivity.this.speedItem.setTranslationX(AndroidUtilities.dp(32.0f));
            DialogsActivity.this.speedItem.setAlpha(0.0f);
            DialogsActivity.this.speedItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public final void onClick(View view) {
                    DialogsActivity.AnonymousClass5.this.lambda$onPreToggleSearch$0(view);
                }
            });
            DialogsActivity.this.speedItem.setClickable(false);
            DialogsActivity.this.speedItem.setFixBackground(true);
            FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(AndroidUtilities.dp(42.0f), -1);
            int dp = AndroidUtilities.dp(38.0f);
            layoutParams.rightMargin = dp;
            layoutParams.leftMargin = dp;
            layoutParams.gravity = 5;
            frameLayout.addView(DialogsActivity.this.speedItem, layoutParams);
            DialogsActivity.this.searchItem.setSearchAdditionalButton(DialogsActivity.this.speedItem);
            DialogsActivity dialogsActivity2 = DialogsActivity.this;
            dialogsActivity2.updateSpeedItem(dialogsActivity2.searchViewPager != null && DialogsActivity.this.searchViewPager.getCurrentPosition() == 2);
        }

        @Override
        public void onSearchCollapse() {
            DialogsActivity.this.searching = false;
            DialogsActivity.this.searchWas = false;
            if (DialogsActivity.this.viewPages[0] != null) {
                DialogsActivity.this.viewPages[0].listView.setEmptyView(DialogsActivity.this.folderId == 0 ? DialogsActivity.this.viewPages[0].progressView : null);
                if (!DialogsActivity.this.onlySelect) {
                    DialogsActivity.this.floatingButtonContainer.setVisibility(0);
                    if (DialogsActivity.this.floatingButton2Container != null) {
                        DialogsActivity.this.floatingButton2Container.setVisibility(DialogsActivity.this.storiesEnabled ? 0 : 8);
                    }
                    DialogsActivity.this.floatingHidden = true;
                    DialogsActivity.this.floatingButtonTranslation = AndroidUtilities.dp(100.0f);
                    DialogsActivity.this.floatingButtonHideProgress = 1.0f;
                    DialogsActivity.this.updateFloatingButtonOffset();
                }
                DialogsActivity.this.showSearch(false, false, true);
            }
            DialogsActivity.this.updateProxyButton(false, false);
            DialogsActivity.this.updatePasscodeButton();
            if (DialogsActivity.this.menuDrawable != null) {
                if (((BaseFragment) DialogsActivity.this).actionBar.getBackButton().getDrawable() != DialogsActivity.this.menuDrawable) {
                    ((BaseFragment) DialogsActivity.this).actionBar.setBackButtonDrawable(DialogsActivity.this.menuDrawable);
                    DialogsActivity.this.menuDrawable.setRotation(0.0f, true);
                }
                ((BaseFragment) DialogsActivity.this).actionBar.setBackButtonContentDescription(LocaleController.getString(R.string.AccDescrOpenMenu));
            }
            NotificationCenter.getGlobalInstance().lambda$postNotificationNameOnUIThread$1(NotificationCenter.needCheckSystemBarColors, Boolean.TRUE);
            ((SizeNotifierFrameLayout) DialogsActivity.this.fragmentView).invalidateBlur();
            if (DialogsActivity.this.optionsItem != null) {
                DialogsActivity.this.optionsItem.setVisibility(0);
            }
        }

        @Override
        public void onSearchExpand() {
            DialogsActivity.this.searching = true;
            if (DialogsActivity.this.switchItem != null) {
                DialogsActivity.this.switchItem.setVisibility(8);
            }
            if (DialogsActivity.this.proxyItem != null && DialogsActivity.this.proxyItemVisible) {
                DialogsActivity.this.proxyItem.setVisibility(8);
            }
            if (DialogsActivity.this.downloadsItem != null && DialogsActivity.this.downloadsItemVisible) {
                DialogsActivity.this.downloadsItem.setVisibility(8);
            }
            if (DialogsActivity.this.viewPages[0] != null) {
                if (DialogsActivity.this.searchString != null) {
                    DialogsActivity.this.viewPages[0].listView.hide();
                    if (DialogsActivity.this.searchViewPager != null) {
                        DialogsActivity.this.searchViewPager.searchListView.show();
                    }
                }
                if (!DialogsActivity.this.onlySelect) {
                    DialogsActivity.this.floatingButtonContainer.setVisibility(8);
                    if (DialogsActivity.this.floatingButton2Container != null) {
                        DialogsActivity.this.floatingButton2Container.setVisibility(8);
                    }
                    if (DialogsActivity.this.storyHint != null) {
                        DialogsActivity.this.storyHint.hide();
                    }
                }
            }
            DialogStoriesCell dialogStoriesCell = DialogsActivity.this.dialogStoriesCell;
            if (dialogStoriesCell != null && dialogStoriesCell.getPremiumHint() != null) {
                DialogsActivity.this.dialogStoriesCell.getPremiumHint().hide();
            }
            DialogsActivity dialogsActivity = DialogsActivity.this;
            if (!dialogsActivity.hasStories) {
                dialogsActivity.setScrollY(0.0f);
            }
            DialogsActivity.this.updatePasscodeButton();
            DialogsActivity.this.updateProxyButton(false, false);
            ((BaseFragment) DialogsActivity.this).actionBar.setBackButtonContentDescription(LocaleController.getString(R.string.AccDescrGoBack));
            NotificationCenter.getGlobalInstance().lambda$postNotificationNameOnUIThread$1(NotificationCenter.needCheckSystemBarColors, new Object[0]);
            ((SizeNotifierFrameLayout) DialogsActivity.this.fragmentView).invalidateBlur();
            if (DialogsActivity.this.optionsItem != null) {
                DialogsActivity.this.optionsItem.setVisibility(8);
            }
        }

        @Override
        public void onSearchFilterCleared(FiltersView.MediaFilterData mediaFilterData) {
            if (DialogsActivity.this.searchIsShowed) {
                if (DialogsActivity.this.searchViewPager != null) {
                    DialogsActivity.this.searchViewPager.removeSearchFilter(mediaFilterData);
                    DialogsActivity.this.searchViewPager.onTextChanged(DialogsActivity.this.searchItem.getSearchField().getText().toString());
                }
                DialogsActivity.this.updateFiltersView(true, null, null, false, true);
            }
        }

        @Override
        public void onTextChanged(EditText editText) {
            String obj = editText.getText().toString();
            if (obj.length() != 0 || ((DialogsActivity.this.searchViewPager != null && DialogsActivity.this.searchViewPager.dialogsSearchAdapter != null && DialogsActivity.this.searchViewPager.dialogsSearchAdapter.hasRecentSearch()) || DialogsActivity.this.searchFiltersWasShowed || DialogsActivity.this.hasStories)) {
                DialogsActivity.this.searchWas = true;
                if (!DialogsActivity.this.searchIsShowed) {
                    DialogsActivity.this.showSearch(true, false, true);
                }
            }
            if (DialogsActivity.this.searchViewPager != null) {
                DialogsActivity.this.searchViewPager.onTextChanged(obj);
            }
        }
    }

    public class AnonymousClass6 extends FilterTabsView {
        AnonymousClass6(Context context) {
            super(context);
        }

        public void lambda$onDefaultTabMoved$0() {
            DialogsActivity.this.showDialog(new PremiumFeatureBottomSheet(DialogsActivity.this, 9, true));
            DialogsActivity.this.filterTabsView.setIsEditing(false);
            DialogsActivity.this.showDoneItem(false);
        }

        @Override
        protected void onDefaultTabMoved() {
            if (DialogsActivity.this.getMessagesController().premiumFeaturesBlocked()) {
                return;
            }
            try {
                performHapticFeedback(3, 1);
            } catch (Exception unused) {
            }
            DialogsActivity dialogsActivity = DialogsActivity.this;
            dialogsActivity.topBulletin = BulletinFactory.of(dialogsActivity).createSimpleBulletin(R.raw.filter_reorder, AndroidUtilities.replaceTags(LocaleController.formatString(R.string.LimitReachedReorderFolder, LocaleController.getString(R.string.FilterAllChats))), LocaleController.getString(R.string.PremiumMore), 5000, new Runnable() {
                @Override
                public final void run() {
                    DialogsActivity.AnonymousClass6.this.lambda$onDefaultTabMoved$0();
                }
            }).show(true);
        }

        @Override
        public boolean onInterceptTouchEvent(MotionEvent motionEvent) {
            getParent().requestDisallowInterceptTouchEvent(true);
            DialogsActivity.this.maybeStartTracking = false;
            return super.onInterceptTouchEvent(motionEvent);
        }

        @Override
        public void setTranslationY(float f) {
            if (getTranslationY() != f) {
                super.setTranslationY(f);
                DialogsActivity.this.updateContextViewPosition();
                View view = DialogsActivity.this.fragmentView;
                if (view != null) {
                    view.invalidate();
                }
            }
        }
    }

    public class AnonymousClass60 implements DialogsSearchAdapter.DialogsSearchAdapterDelegate {
        AnonymousClass60() {
        }

        public void lambda$needClearList$1(DialogInterface dialogInterface, int i) {
            DialogsActivity.this.searchViewPager.dialogsSearchAdapter.clearRecentSearch();
        }

        public void lambda$needClearList$2(DialogInterface dialogInterface, int i) {
            if (DialogsActivity.this.searchViewPager.dialogsSearchAdapter.isRecentSearchDisplayed()) {
                DialogsActivity.this.searchViewPager.dialogsSearchAdapter.clearRecentSearch();
            } else {
                DialogsActivity.this.searchViewPager.dialogsSearchAdapter.clearRecentHashtags();
            }
        }

        public void lambda$needRemoveHint$0(long j, DialogInterface dialogInterface, int i) {
            DialogsActivity.this.getMediaDataController().removePeer(j);
        }

        @Override
        public void didPressedBlockedDialog(View view, long j) {
            DialogsActivity.this.showPremiumBlockedToast(view, j);
        }

        @Override
        public void didPressedOnSubDialog(long j) {
            DialogsActivity dialogsActivity;
            ChatActivity chatActivity;
            if (DialogsActivity.this.onlySelect) {
                if (DialogsActivity.this.validateSlowModeDialog(j)) {
                    if (DialogsActivity.this.selectedDialogs.isEmpty()) {
                        DialogsActivity.this.didSelectResult(j, 0, true, false);
                        return;
                    }
                    DialogsActivity.this.findAndUpdateCheckBox(j, DialogsActivity.this.addOrRemoveSelectedDialog(j, null));
                    DialogsActivity.this.updateSelectedCount();
                    ((BaseFragment) DialogsActivity.this).actionBar.closeSearchField();
                    return;
                }
                return;
            }
            Bundle bundle = new Bundle();
            if (DialogObject.isUserDialog(j)) {
                bundle.putLong("user_id", j);
            } else {
                bundle.putLong("chat_id", -j);
            }
            DialogsActivity.this.closeSearch();
            if (AndroidUtilities.isTablet() && DialogsActivity.this.viewPages != null) {
                for (int i = 0; i < DialogsActivity.this.viewPages.length; i++) {
                    DialogsAdapter dialogsAdapter = DialogsActivity.this.viewPages[i].dialogsAdapter;
                    DialogsActivity.this.openedDialogId.dialogId = j;
                    dialogsAdapter.setOpenedDialogId(j);
                }
                DialogsActivity.this.updateVisibleRows(MessagesController.UPDATE_MASK_SELECT_DIALOG);
            }
            if (DialogsActivity.this.searchString != null) {
                if (!DialogsActivity.this.getMessagesController().checkCanOpenChat(bundle, DialogsActivity.this)) {
                    return;
                }
                DialogsActivity.this.getNotificationCenter().lambda$postNotificationNameOnUIThread$1(NotificationCenter.closeChats, new Object[0]);
                dialogsActivity = DialogsActivity.this;
                chatActivity = new ChatActivity(bundle);
            } else {
                if (!DialogsActivity.this.getMessagesController().checkCanOpenChat(bundle, DialogsActivity.this)) {
                    return;
                }
                dialogsActivity = DialogsActivity.this;
                chatActivity = new ChatActivity(bundle);
            }
            dialogsActivity.presentFragment(chatActivity);
        }

        @Override
        public long getSearchForumDialogId() {
            RightSlidingDialogContainer rightSlidingDialogContainer = DialogsActivity.this.rightSlidingDialogContainer;
            if (rightSlidingDialogContainer == null || !(rightSlidingDialogContainer.getFragment() instanceof TopicsFragment)) {
                return 0L;
            }
            return ((TopicsFragment) DialogsActivity.this.rightSlidingDialogContainer.getFragment()).getDialogId();
        }

        @Override
        public boolean isSelected(long j) {
            return DialogsActivity.this.selectedDialogs.contains(Long.valueOf(j));
        }

        @Override
        public void needClearList() {
            String string;
            DialogInterface.OnClickListener onClickListener;
            AlertDialog.Builder builder = new AlertDialog.Builder(DialogsActivity.this.getParentActivity());
            if (DialogsActivity.this.searchViewPager.dialogsSearchAdapter.isSearchWas() && DialogsActivity.this.searchViewPager.dialogsSearchAdapter.isRecentSearchDisplayed()) {
                builder.setTitle(LocaleController.getString(R.string.ClearSearchAlertPartialTitle));
                builder.setMessage(LocaleController.formatPluralString("ClearSearchAlertPartial", DialogsActivity.this.searchViewPager.dialogsSearchAdapter.getRecentResultsCount(), new Object[0]));
                string = LocaleController.getString(R.string.Clear);
                onClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public final void onClick(DialogInterface dialogInterface, int i) {
                        DialogsActivity.AnonymousClass60.this.lambda$needClearList$1(dialogInterface, i);
                    }
                };
            } else {
                builder.setTitle(LocaleController.getString(R.string.ClearSearchAlertTitle));
                builder.setMessage(LocaleController.getString(R.string.ClearSearchAlert));
                string = LocaleController.getString(R.string.ClearButton);
                onClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public final void onClick(DialogInterface dialogInterface, int i) {
                        DialogsActivity.AnonymousClass60.this.lambda$needClearList$2(dialogInterface, i);
                    }
                };
            }
            builder.setPositiveButton(string, onClickListener);
            builder.setNegativeButton(LocaleController.getString(R.string.Cancel), null);
            AlertDialog create = builder.create();
            DialogsActivity.this.showDialog(create);
            TextView textView = (TextView) create.getButton(-1);
            if (textView != null) {
                textView.setTextColor(Theme.getColor(Theme.key_text_RedBold));
            }
        }

        @Override
        public void needRemoveHint(final long j) {
            TLRPC.User user;
            if (DialogsActivity.this.getParentActivity() == null || (user = DialogsActivity.this.getMessagesController().getUser(Long.valueOf(j))) == null) {
                return;
            }
            AlertDialog.Builder builder = new AlertDialog.Builder(DialogsActivity.this.getParentActivity());
            builder.setTitle(LocaleController.getString(R.string.ChatHintsDeleteAlertTitle));
            builder.setMessage(AndroidUtilities.replaceTags(LocaleController.formatString("ChatHintsDeleteAlert", R.string.ChatHintsDeleteAlert, ContactsController.formatName(user.first_name, user.last_name))));
            builder.setPositiveButton(LocaleController.getString(R.string.StickersRemove), new DialogInterface.OnClickListener() {
                @Override
                public final void onClick(DialogInterface dialogInterface, int i) {
                    DialogsActivity.AnonymousClass60.this.lambda$needRemoveHint$0(j, dialogInterface, i);
                }
            });
            builder.setNegativeButton(LocaleController.getString(R.string.Cancel), null);
            AlertDialog create = builder.create();
            DialogsActivity.this.showDialog(create);
            TextView textView = (TextView) create.getButton(-1);
            if (textView != null) {
                textView.setTextColor(Theme.getColor(Theme.key_text_RedBold));
            }
        }

        @Override
        public void runResultsEnterAnimation() {
            if (DialogsActivity.this.searchViewPager != null) {
                DialogsActivity.this.searchViewPager.runResultsEnterAnimation();
            }
        }

        @Override
        public void searchStateChanged(boolean z, boolean z2) {
            StickerEmptyView stickerEmptyView;
            boolean z3 = true;
            if (DialogsActivity.this.searchViewPager.emptyView.getVisibility() == 0) {
                z2 = true;
            }
            if (DialogsActivity.this.searching && DialogsActivity.this.searchWas && DialogsActivity.this.searchViewPager.emptyView != null) {
                if (z || DialogsActivity.this.searchViewPager.dialogsSearchAdapter.getItemCount() != 0) {
                    stickerEmptyView = DialogsActivity.this.searchViewPager.emptyView;
                } else {
                    stickerEmptyView = DialogsActivity.this.searchViewPager.emptyView;
                    z3 = false;
                }
                stickerEmptyView.showProgress(z3, z2);
            }
            if (z && DialogsActivity.this.searchViewPager.dialogsSearchAdapter.getItemCount() == 0) {
                DialogsActivity.this.searchViewPager.cancelEnterAnimation();
            }
        }
    }

    public class AnonymousClass7 implements FilterTabsView.FilterTabsViewDelegate {
        final Context val$context;

        AnonymousClass7(Context context) {
            this.val$context = context;
        }

        public void lambda$didSelectTab$1() {
            DialogsActivity.this.resetScroll();
            DialogsActivity.this.filterTabsView.setIsEditing(true);
            DialogsActivity.this.showDoneItem(true);
        }

        public void lambda$didSelectTab$2(boolean z, MessagesController.DialogFilter dialogFilter) {
            DialogsActivity.this.presentFragment(z ? new FiltersSetupActivity() : new FilterCreateActivity(dialogFilter));
        }

        public void lambda$didSelectTab$3(ArrayList arrayList, boolean z) {
            int i = 0;
            for (int i2 = 0; i2 < arrayList.size(); i2++) {
                TLRPC.Dialog dialog = (TLRPC.Dialog) arrayList.get(i2);
                if (dialog != null) {
                    DialogsActivity.this.getNotificationsController().setDialogNotificationsSettings(dialog.id, 0L, z ? 3 : 4);
                    i++;
                }
            }
            BulletinFactory.createMuteBulletin(DialogsActivity.this, z, i, (Theme.ResourcesProvider) null).show();
        }

        public void lambda$didSelectTab$4(ArrayList arrayList) {
            DialogsActivity.this.markDialogsAsRead(arrayList);
        }

        public void lambda$didSelectTab$5(boolean[] zArr, MessagesController.DialogFilter dialogFilter) {
            if (zArr[0]) {
                DialogsActivity.this.presentFragment(new FilterChatlistActivity(dialogFilter, null));
            } else {
                FilterCreateActivity.FilterInvitesBottomSheet.show(DialogsActivity.this, dialogFilter, null);
            }
        }

        public void lambda$showDeleteAlert$0(MessagesController.DialogFilter dialogFilter, DialogInterface dialogInterface, int i) {
            TLRPC.TL_messages_updateDialogFilter tL_messages_updateDialogFilter = new TLRPC.TL_messages_updateDialogFilter();
            tL_messages_updateDialogFilter.id = dialogFilter.id;
            DialogsActivity.this.getConnectionsManager().sendRequest(tL_messages_updateDialogFilter, null);
            DialogsActivity.this.getMessagesController().removeFilter(dialogFilter);
            DialogsActivity.this.getMessagesStorage().deleteDialogFilter(dialogFilter);
        }

        public void lambda$didSelectTab$6(final MessagesController.DialogFilter dialogFilter) {
            if (dialogFilter.isChatlist()) {
                FolderBottomSheet.showForDeletion(DialogsActivity.this, dialogFilter.id, null);
                return;
            }
            AlertDialog.Builder builder = new AlertDialog.Builder(DialogsActivity.this.getParentActivity());
            builder.setTitle(LocaleController.getString(R.string.FilterDelete));
            builder.setMessage(LocaleController.getString(R.string.FilterDeleteAlert));
            builder.setNegativeButton(LocaleController.getString(R.string.Cancel), null);
            builder.setPositiveButton(LocaleController.getString(R.string.Delete), new DialogInterface.OnClickListener() {
                @Override
                public final void onClick(DialogInterface dialogInterface, int i) {
                    DialogsActivity.AnonymousClass7.this.lambda$showDeleteAlert$0(dialogFilter, dialogInterface, i);
                }
            });
            AlertDialog create = builder.create();
            DialogsActivity.this.showDialog(create);
            TextView textView = (TextView) create.getButton(-1);
            if (textView != null) {
                textView.setTextColor(Theme.getColor(Theme.key_text_RedBold));
            }
        }

        @Override
        public boolean canPerformActions() {
            return !DialogsActivity.this.searching;
        }

        @Override
        public boolean didSelectTab(org.telegram.ui.Components.FilterTabsView.TabView r19, boolean r20) {
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.DialogsActivity.AnonymousClass7.didSelectTab(org.telegram.ui.Components.FilterTabsView$TabView, boolean):boolean");
        }

        @Override
        public int getTabCounter(int i) {
            if (DialogsActivity.this.initialDialogsType == 3) {
                return 0;
            }
            if (i == DialogsActivity.this.filterTabsView.getDefaultTabId()) {
                return DialogsActivity.this.getMessagesStorage().getMainUnreadCount();
            }
            ArrayList<MessagesController.DialogFilter> dialogFilters = DialogsActivity.this.getMessagesController().getDialogFilters();
            if (i < 0 || i >= dialogFilters.size()) {
                return 0;
            }
            return DialogsActivity.this.getMessagesController().getDialogFilters().get(i).unreadCount;
        }

        @Override
        public boolean isTabMenuVisible() {
            return DialogsActivity.this.filterOptions != null && DialogsActivity.this.filterOptions.isShown();
        }

        @Override
        public void onDeletePressed(int i) {
            lambda$didSelectTab$6(DialogsActivity.this.getMessagesController().getDialogFilters().get(i));
        }

        @Override
        public void onPageReorder(int i, int i2) {
            for (int i3 = 0; i3 < DialogsActivity.this.viewPages.length; i3++) {
                if (DialogsActivity.this.viewPages[i3].selectedType == i) {
                    DialogsActivity.this.viewPages[i3].selectedType = i2;
                } else if (DialogsActivity.this.viewPages[i3].selectedType == i2) {
                    DialogsActivity.this.viewPages[i3].selectedType = i;
                }
            }
        }

        @Override
        public void onPageScrolled(float f) {
            ViewPage viewPage;
            float measuredWidth;
            float measuredWidth2;
            if (f != 1.0f || DialogsActivity.this.viewPages[1].getVisibility() == 0 || DialogsActivity.this.searching) {
                if (DialogsActivity.this.animatingForward) {
                    DialogsActivity.this.viewPages[0].setTranslationX((-f) * DialogsActivity.this.viewPages[0].getMeasuredWidth());
                    viewPage = DialogsActivity.this.viewPages[1];
                    measuredWidth = DialogsActivity.this.viewPages[0].getMeasuredWidth();
                    measuredWidth2 = DialogsActivity.this.viewPages[0].getMeasuredWidth() * f;
                } else {
                    DialogsActivity.this.viewPages[0].setTranslationX(DialogsActivity.this.viewPages[0].getMeasuredWidth() * f);
                    viewPage = DialogsActivity.this.viewPages[1];
                    measuredWidth = DialogsActivity.this.viewPages[0].getMeasuredWidth() * f;
                    measuredWidth2 = DialogsActivity.this.viewPages[0].getMeasuredWidth();
                }
                viewPage.setTranslationX(measuredWidth - measuredWidth2);
                if (f == 1.0f) {
                    ViewPage viewPage2 = DialogsActivity.this.viewPages[0];
                    DialogsActivity.this.viewPages[0] = DialogsActivity.this.viewPages[1];
                    DialogsActivity.this.viewPages[1] = viewPage2;
                    DialogsActivity.this.viewPages[1].setVisibility(8);
                    DialogsActivity.this.showScrollbars(true);
                    DialogsActivity.this.updateCounters(false);
                    DialogsActivity.this.filterTabsView.stopAnimatingIndicator();
                    DialogsActivity dialogsActivity = DialogsActivity.this;
                    dialogsActivity.checkListLoad(dialogsActivity.viewPages[0]);
                    DialogsActivity.this.viewPages[0].dialogsAdapter.resume();
                    DialogsActivity.this.viewPages[1].dialogsAdapter.pause();
                }
            }
        }

        @Override
        public void onPageSelected(FilterTabsView.Tab tab, boolean z) {
            int i;
            if (DialogsActivity.this.viewPages[0].selectedType == tab.id) {
                return;
            }
            if (tab.isLocked) {
                DialogsActivity.this.filterTabsView.shakeLock(tab.id);
                DialogsActivity dialogsActivity = DialogsActivity.this;
                DialogsActivity dialogsActivity2 = DialogsActivity.this;
                dialogsActivity.showDialog(new LimitReachedBottomSheet(dialogsActivity2, this.val$context, 3, ((BaseFragment) dialogsActivity2).currentAccount, null));
                return;
            }
            ArrayList<MessagesController.DialogFilter> dialogFilters = DialogsActivity.this.getMessagesController().getDialogFilters();
            if (tab.isDefault || ((i = tab.id) >= 0 && i < dialogFilters.size())) {
                DialogsActivity dialogsActivity3 = DialogsActivity.this;
                dialogsActivity3.isFirstTab = tab.id == dialogsActivity3.filterTabsView.getFirstTabId();
                DialogsActivity.this.updateDrawerSwipeEnabled();
                DialogsActivity.this.viewPages[1].selectedType = tab.id;
                DialogsActivity.this.viewPages[1].setVisibility(0);
                DialogsActivity.this.viewPages[1].setTranslationX(DialogsActivity.this.viewPages[0].getMeasuredWidth());
                DialogsActivity.this.showScrollbars(false);
                DialogsActivity.this.switchToCurrentSelectedMode(true);
                DialogsActivity.this.animatingForward = z;
            }
        }

        @Override
        public void onSamePageSelected() {
            DialogsActivity.this.scrollToTop(true, false);
        }
    }

    public class AnonymousClass8 extends ActionBar.ActionBarMenuOnItemClick {
        AnonymousClass8() {
        }

        public void lambda$onItemClick$0() {
            DialogsActivity.this.passcodeItem.setAlpha(1.0f);
        }

        public void lambda$onItemClick$1() {
            DialogsActivity.this.passcodeItem.setAlpha(0.0f);
        }

        public void lambda$onItemClick$2(MessagesController.DialogFilter dialogFilter, boolean z) {
            boolean z2;
            ArrayList arrayList;
            int i;
            ArrayList arrayList2;
            long j;
            DialogsActivity dialogsActivity = DialogsActivity.this;
            ArrayList dialogsCount = FiltersListBottomSheet.getDialogsCount(dialogsActivity, dialogFilter, dialogsActivity.selectedDialogs, true, false);
            if (!z) {
                int size = (dialogFilter != null ? dialogFilter.alwaysShow.size() : 0) + dialogsCount.size();
                if ((size > DialogsActivity.this.getMessagesController().dialogFiltersChatsLimitDefault && !DialogsActivity.this.getUserConfig().isPremium()) || size > DialogsActivity.this.getMessagesController().dialogFiltersChatsLimitPremium) {
                    DialogsActivity dialogsActivity2 = DialogsActivity.this;
                    DialogsActivity dialogsActivity3 = DialogsActivity.this;
                    dialogsActivity2.showDialog(new LimitReachedBottomSheet(dialogsActivity3, dialogsActivity3.fragmentView.getContext(), 4, ((BaseFragment) DialogsActivity.this).currentAccount, null));
                    return;
                }
            }
            if (dialogFilter == null) {
                z2 = true;
                DialogsActivity.this.presentFragment(new FilterCreateActivity(null, dialogsCount));
            } else if (z) {
                for (int i2 = 0; i2 < DialogsActivity.this.selectedDialogs.size(); i2++) {
                    dialogFilter.neverShow.add((Long) DialogsActivity.this.selectedDialogs.get(i2));
                    dialogFilter.alwaysShow.remove(DialogsActivity.this.selectedDialogs.get(i2));
                }
                FilterCreateActivity.saveFilterToServer(dialogFilter, dialogFilter.flags, dialogFilter.name, dialogFilter.color, dialogFilter.alwaysShow, dialogFilter.neverShow, dialogFilter.pinnedDialogs, false, false, true, true, false, DialogsActivity.this, null);
                long longValue = DialogsActivity.this.selectedDialogs.size() == 1 ? ((Long) DialogsActivity.this.selectedDialogs.get(0)).longValue() : 0L;
                UndoView undoView = DialogsActivity.this.getUndoView();
                if (undoView != null) {
                    undoView.showWithAction(longValue, 21, Integer.valueOf(DialogsActivity.this.selectedDialogs.size()), dialogFilter, (Runnable) null, (Runnable) null);
                }
                z2 = true;
            } else {
                if (dialogsCount.isEmpty()) {
                    arrayList = dialogsCount;
                    i = 0;
                } else {
                    for (int i3 = 0; i3 < dialogsCount.size(); i3++) {
                        dialogFilter.neverShow.remove(dialogsCount.get(i3));
                    }
                    dialogFilter.alwaysShow.addAll(dialogsCount);
                    arrayList = dialogsCount;
                    i = 0;
                    FilterCreateActivity.saveFilterToServer(dialogFilter, dialogFilter.flags, dialogFilter.name, dialogFilter.color, dialogFilter.alwaysShow, dialogFilter.neverShow, dialogFilter.pinnedDialogs, false, false, true, true, false, DialogsActivity.this, null);
                }
                z2 = true;
                if (arrayList.size() == 1) {
                    arrayList2 = arrayList;
                    j = ((Long) arrayList2.get(i)).longValue();
                } else {
                    arrayList2 = arrayList;
                    j = 0;
                }
                UndoView undoView2 = DialogsActivity.this.getUndoView();
                if (undoView2 != null) {
                    undoView2.showWithAction(j, 20, Integer.valueOf(arrayList2.size()), dialogFilter, (Runnable) null, (Runnable) null);
                }
            }
            DialogsActivity.this.hideActionMode(z2);
        }

        @Override
        public void onItemClick(int r21) {
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.DialogsActivity.AnonymousClass8.onItemClick(int):void");
        }
    }

    public class ContentView extends SizeNotifierFrameLayout {
        private Paint actionBarSearchPaint;
        private Rect blurBounds;
        private int inputFieldHeight;
        private int[] pos;
        private int startedTrackingPointerId;
        private int startedTrackingX;
        private int startedTrackingY;
        private VelocityTracker velocityTracker;
        private boolean wasPortrait;
        private Paint windowBackgroundPaint;

        public ContentView(Context context) {
            super(context);
            this.actionBarSearchPaint = new Paint(1);
            this.windowBackgroundPaint = new Paint();
            this.pos = new int[2];
            this.blurBounds = new Rect();
            this.needBlur = true;
            this.blurBehindViews.add(this);
        }

        public void lambda$onMeasure$0() {
            if (DialogsActivity.this.selectAnimatedEmojiDialog != null) {
                DialogsActivity.this.selectAnimatedEmojiDialog.dismiss();
                DialogsActivity.this.selectAnimatedEmojiDialog = null;
            }
        }

        public void lambda$onTouchEvent$1() {
            DialogsActivity.this.showDialog(new LimitReachedBottomSheet(DialogsActivity.this, getContext(), 3, ((BaseFragment) DialogsActivity.this).currentAccount, null));
        }

        private boolean prepareForMoving(MotionEvent motionEvent, boolean z) {
            ViewPage viewPage;
            int i;
            int nextPageId = DialogsActivity.this.filterTabsView.getNextPageId(z);
            if (nextPageId < 0) {
                return false;
            }
            getParent().requestDisallowInterceptTouchEvent(true);
            DialogsActivity.this.maybeStartTracking = false;
            DialogsActivity.this.startedTracking = true;
            this.startedTrackingX = (int) (motionEvent.getX() + DialogsActivity.this.additionalOffset);
            ((BaseFragment) DialogsActivity.this).actionBar.setEnabled(false);
            DialogsActivity.this.filterTabsView.setEnabled(false);
            DialogsActivity.this.viewPages[1].selectedType = nextPageId;
            DialogsActivity.this.viewPages[1].setVisibility(0);
            DialogsActivity.this.animatingForward = z;
            DialogsActivity.this.showScrollbars(false);
            DialogsActivity.this.switchToCurrentSelectedMode(true);
            ViewPage[] viewPageArr = DialogsActivity.this.viewPages;
            if (z) {
                viewPage = viewPageArr[1];
                i = DialogsActivity.this.viewPages[0].getMeasuredWidth();
            } else {
                viewPage = viewPageArr[1];
                i = -DialogsActivity.this.viewPages[0].getMeasuredWidth();
            }
            viewPage.setTranslationX(i);
            return true;
        }

        public boolean checkTabsAnimationInProgress() {
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.DialogsActivity.ContentView.checkTabsAnimationInProgress():boolean");
        }

        @Override
        public void dispatchDraw(android.graphics.Canvas r21) {
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.DialogsActivity.ContentView.dispatchDraw(android.graphics.Canvas):void");
        }

        @Override
        protected boolean drawChild(android.graphics.Canvas r10, android.view.View r11, long r12) {
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.DialogsActivity.ContentView.drawChild(android.graphics.Canvas, android.view.View, long):boolean");
        }

        @Override
        public void drawList(android.graphics.Canvas r9, boolean r10, java.util.ArrayList r11) {
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.DialogsActivity.ContentView.drawList(android.graphics.Canvas, boolean, java.util.ArrayList):void");
        }

        public int getActionBarFullHeight() {
            float f = 0.0f;
            float height = ((BaseFragment) DialogsActivity.this).actionBar.getHeight() + (((DialogsActivity.this.filterTabsView == null || DialogsActivity.this.filterTabsView.getVisibility() == 8) ? 0.0f : DialogsActivity.this.filterTabsView.getMeasuredHeight() - ((1.0f - DialogsActivity.this.filterTabsProgress) * DialogsActivity.this.filterTabsView.getMeasuredHeight())) * (1.0f - DialogsActivity.this.searchAnimationProgress)) + (((DialogsActivity.this.searchTabsView == null || DialogsActivity.this.searchTabsView.getVisibility() == 8) ? 0.0f : DialogsActivity.this.searchTabsView.getMeasuredHeight()) * DialogsActivity.this.searchAnimationProgress);
            RightSlidingDialogContainer rightSlidingDialogContainer = DialogsActivity.this.rightSlidingDialogContainer;
            if (rightSlidingDialogContainer != null && rightSlidingDialogContainer.hasFragment()) {
                f = DialogsActivity.this.rightSlidingDialogContainer.openedProgress;
            }
            if (DialogsActivity.this.hasStories) {
                height += AndroidUtilities.dp(81.0f) * (1.0f - DialogsActivity.this.searchAnimationProgress) * (1.0f - f) * (1.0f - DialogsActivity.this.progressToActionMode);
            }
            return (int) (height + DialogsActivity.this.storiesOverscroll);
        }

        public int getActionBarTop() {
            float f = DialogsActivity.this.scrollYOffset;
            DialogsActivity dialogsActivity = DialogsActivity.this;
            if (dialogsActivity.hasStories) {
                RightSlidingDialogContainer rightSlidingDialogContainer = dialogsActivity.rightSlidingDialogContainer;
                f *= (1.0f - DialogsActivity.this.progressToActionMode) * (1.0f - ((rightSlidingDialogContainer == null || !rightSlidingDialogContainer.hasFragment()) ? 0.0f : DialogsActivity.this.rightSlidingDialogContainer.openedProgress));
            }
            return (int) ((-getY()) + (f * (1.0f - DialogsActivity.this.searchAnimationProgress)));
        }

        @Override
        public boolean hasOverlappingRendering() {
            return false;
        }

        @Override
        protected boolean invalidateOptimized() {
            return true;
        }

        @Override
        public void onAttachedToWindow() {
            super.onAttachedToWindow();
            if (DialogsActivity.this.statusDrawable != null) {
                DialogsActivity.this.statusDrawable.attach();
            }
        }

        @Override
        public void onDetachedFromWindow() {
            super.onDetachedFromWindow();
            if (DialogsActivity.this.statusDrawable != null) {
                DialogsActivity.this.statusDrawable.detach();
            }
        }

        @Override
        public boolean onInterceptTouchEvent(MotionEvent motionEvent) {
            int actionMasked = motionEvent.getActionMasked();
            if ((actionMasked == 1 || actionMasked == 3) && ((BaseFragment) DialogsActivity.this).actionBar.isActionModeShowed()) {
                DialogsActivity.this.allowMoving = true;
            }
            if (checkTabsAnimationInProgress()) {
                return true;
            }
            return (DialogsActivity.this.filterTabsView != null && DialogsActivity.this.filterTabsView.isAnimatingIndicator()) || onTouchEvent(motionEvent);
        }

        @Override
        public void onLayout(boolean r17, int r18, int r19, int r20, int r21) {
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.DialogsActivity.ContentView.onLayout(boolean, int, int, int, int):void");
        }

        @Override
        protected void onMeasure(int i, int i2) {
            int i3;
            int makeMeasureSpec;
            int i4;
            int size = View.MeasureSpec.getSize(i);
            int size2 = View.MeasureSpec.getSize(i2);
            boolean z = size2 > size;
            setMeasuredDimension(size, size2);
            int paddingTop = size2 - getPaddingTop();
            if (DialogsActivity.this.doneItem != null) {
                FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) DialogsActivity.this.doneItem.getLayoutParams();
                layoutParams.topMargin = ((BaseFragment) DialogsActivity.this).actionBar.getOccupyStatusBar() ? AndroidUtilities.statusBarHeight : 0;
                layoutParams.height = ActionBar.getCurrentActionBarHeight();
            }
            measureChildWithMargins(((BaseFragment) DialogsActivity.this).actionBar, i, 0, i2, 0);
            int measureKeyboardHeight = measureKeyboardHeight();
            int childCount = getChildCount();
            if (DialogsActivity.this.commentView != null) {
                measureChildWithMargins(DialogsActivity.this.commentView, i, 0, i2, 0);
                Object tag = DialogsActivity.this.commentView.getTag();
                if (tag == null || !tag.equals(2)) {
                    this.inputFieldHeight = 0;
                } else {
                    if (measureKeyboardHeight <= AndroidUtilities.dp(20.0f) && !AndroidUtilities.isInMultiwindow) {
                        paddingTop -= DialogsActivity.this.commentView.getEmojiPadding();
                    }
                    this.inputFieldHeight = DialogsActivity.this.commentView.getMeasuredHeight();
                }
                if (DialogsActivity.this.commentView.isPopupShowing()) {
                    DialogsActivity.this.fragmentView.setTranslationY(0.0f);
                    for (int i5 = 0; i5 < DialogsActivity.this.viewPages.length; i5++) {
                        if (DialogsActivity.this.viewPages[i5] != null) {
                            DialogsActivity.this.viewPages[i5].setTranslationY(0.0f);
                        }
                    }
                    if (!DialogsActivity.this.onlySelect) {
                        ((BaseFragment) DialogsActivity.this).actionBar.setTranslationY(0.0f);
                        if (DialogsActivity.this.topBulletin != null) {
                            DialogsActivity.this.topBulletin.updatePosition();
                        }
                    }
                    if (DialogsActivity.this.searchViewPager != null) {
                        DialogsActivity.this.searchViewPager.setTranslationY(DialogsActivity.this.searchViewPagerTranslationY);
                    }
                }
            }
            for (int i6 = 0; i6 < childCount; i6++) {
                View childAt = getChildAt(i6);
                if (childAt != null && childAt.getVisibility() != 8 && childAt != DialogsActivity.this.commentView && childAt != ((BaseFragment) DialogsActivity.this).actionBar) {
                    if (childAt instanceof DatabaseMigrationHint) {
                        childAt.measure(View.MeasureSpec.makeMeasureSpec(size, 1073741824), View.MeasureSpec.makeMeasureSpec(Math.max(AndroidUtilities.dp(10.0f), (((View.MeasureSpec.getSize(i2) + measureKeyboardHeight) - this.inputFieldHeight) + AndroidUtilities.dp(2.0f)) - ((BaseFragment) DialogsActivity.this).actionBar.getMeasuredHeight()), 1073741824));
                    } else if (childAt instanceof ViewPage) {
                        int makeMeasureSpec2 = View.MeasureSpec.makeMeasureSpec(size, 1073741824);
                        int dp = ((paddingTop - this.inputFieldHeight) + AndroidUtilities.dp(2.0f)) - DialogsActivity.this.topPadding;
                        DialogsActivity dialogsActivity = DialogsActivity.this;
                        if (dialogsActivity.hasStories || (dialogsActivity.filterTabsView != null && DialogsActivity.this.filterTabsView.getVisibility() == 0)) {
                            if (DialogsActivity.this.filterTabsView != null && DialogsActivity.this.filterTabsView.getVisibility() == 0) {
                                dp -= AndroidUtilities.dp(44.0f);
                            }
                            if (DialogsActivity.this.rightSlidingDialogContainer.hasFragment()) {
                                if (DialogsActivity.this.filterTabsView != null && DialogsActivity.this.filterTabsView.getVisibility() == 0) {
                                    dp += AndroidUtilities.dp(44.0f);
                                }
                                if (DialogsActivity.this.hasStories) {
                                    dp += AndroidUtilities.dp(81.0f);
                                }
                                if (DialogsActivity.this.dialogsHintCell != null && DialogsActivity.this.dialogsHintCell.getVisibility() == 0) {
                                    dp += DialogsActivity.this.dialogsHintCell.getMeasuredHeight();
                                }
                                if (DialogsActivity.this.authHintCell != null && DialogsActivity.this.authHintCell.getVisibility() == 0) {
                                    dp += DialogsActivity.this.authHintCell.getMeasuredHeight();
                                }
                            }
                        } else if (!DialogsActivity.this.onlySelect || DialogsActivity.this.initialDialogsType == 3) {
                            dp -= ((BaseFragment) DialogsActivity.this).actionBar.getMeasuredHeight();
                        }
                        if (DialogsActivity.this.dialogsHintCell != null) {
                            dp -= DialogsActivity.this.dialogsHintCell.height();
                        }
                        int i7 = dp + DialogsActivity.this.actionModeAdditionalHeight;
                        if (DialogsActivity.this.filtersTabAnimator != null) {
                            DialogsActivity dialogsActivity2 = DialogsActivity.this;
                            if (dialogsActivity2.hasStories || (dialogsActivity2.filterTabsView != null && DialogsActivity.this.filterTabsView.getVisibility() == 0)) {
                                i7 = (int) (i7 + DialogsActivity.this.filterTabsMoveFrom);
                                DialogsActivity dialogsActivity3 = DialogsActivity.this;
                                int i8 = (!dialogsActivity3.isSlideBackTransition || dialogsActivity3.isDrawerTransition) ? (int) (i7 * 0.05f) : 0;
                                childAt.setPadding(childAt.getPaddingLeft(), childAt.getPaddingTop(), childAt.getPaddingRight(), i8);
                                childAt.measure(makeMeasureSpec2, View.MeasureSpec.makeMeasureSpec(Math.max(AndroidUtilities.dp(10.0f), i7 + i8), 1073741824));
                                childAt.setPivotX(childAt.getMeasuredWidth() / 2);
                            }
                        }
                        childAt.setTranslationY(0.0f);
                        DialogsActivity dialogsActivity32 = DialogsActivity.this;
                        if (dialogsActivity32.isSlideBackTransition) {
                        }
                        childAt.setPadding(childAt.getPaddingLeft(), childAt.getPaddingTop(), childAt.getPaddingRight(), i8);
                        childAt.measure(makeMeasureSpec2, View.MeasureSpec.makeMeasureSpec(Math.max(AndroidUtilities.dp(10.0f), i7 + i8), 1073741824));
                        childAt.setPivotX(childAt.getMeasuredWidth() / 2);
                    } else {
                        if (childAt == DialogsActivity.this.searchViewPager) {
                            DialogsActivity.this.searchViewPager.setTranslationY(DialogsActivity.this.searchViewPagerTranslationY);
                            childAt.measure(View.MeasureSpec.makeMeasureSpec(size, 1073741824), View.MeasureSpec.makeMeasureSpec(Math.max(AndroidUtilities.dp(10.0f), ((((View.MeasureSpec.getSize(i2) + measureKeyboardHeight) - this.inputFieldHeight) + AndroidUtilities.dp(2.0f)) - ((!DialogsActivity.this.onlySelect || DialogsActivity.this.initialDialogsType == 3) ? ((BaseFragment) DialogsActivity.this).actionBar.getMeasuredHeight() : 0)) - DialogsActivity.this.topPadding) - (DialogsActivity.this.searchTabsView == null ? 0 : AndroidUtilities.dp(44.0f)), 1073741824));
                            childAt.setPivotX(childAt.getMeasuredWidth() / 2);
                        } else if (DialogsActivity.this.commentView == null || !DialogsActivity.this.commentView.isPopupView(childAt)) {
                            if (childAt == DialogsActivity.this.rightSlidingDialogContainer) {
                                int size3 = View.MeasureSpec.getSize(i2);
                                DialogsActivity dialogsActivity4 = DialogsActivity.this;
                                int i9 = (dialogsActivity4.isSlideBackTransition || dialogsActivity4.isDrawerTransition) ? (int) (size3 * 0.05f) : 0;
                                dialogsActivity4.rightSlidingDialogContainer.setTransitionPaddingBottom(i9);
                                childAt.measure(i, View.MeasureSpec.makeMeasureSpec(Math.max(AndroidUtilities.dp(10.0f), size3 + i9), 1073741824));
                            } else {
                                measureChildWithMargins(childAt, i, 0, i2, 0);
                            }
                        } else {
                            if (!AndroidUtilities.isInMultiwindow) {
                                i3 = 1073741824;
                                makeMeasureSpec = View.MeasureSpec.makeMeasureSpec(size, 1073741824);
                                i4 = childAt.getLayoutParams().height;
                            } else if (AndroidUtilities.isTablet()) {
                                i3 = 1073741824;
                                makeMeasureSpec = View.MeasureSpec.makeMeasureSpec(size, 1073741824);
                                i4 = Math.min(AndroidUtilities.dp(320.0f), ((paddingTop - this.inputFieldHeight) - AndroidUtilities.statusBarHeight) + getPaddingTop());
                            } else {
                                i3 = 1073741824;
                                makeMeasureSpec = View.MeasureSpec.makeMeasureSpec(size, 1073741824);
                                i4 = ((paddingTop - this.inputFieldHeight) - AndroidUtilities.statusBarHeight) + getPaddingTop();
                            }
                            childAt.measure(makeMeasureSpec, View.MeasureSpec.makeMeasureSpec(i4, i3));
                        }
                    }
                }
            }
            if (z != this.wasPortrait) {
                post(new Runnable() {
                    @Override
                    public final void run() {
                        DialogsActivity.ContentView.this.lambda$onMeasure$0();
                    }
                });
                this.wasPortrait = z;
            }
        }

        @Override
        public boolean onTouchEvent(android.view.MotionEvent r13) {
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.DialogsActivity.ContentView.onTouchEvent(android.view.MotionEvent):boolean");
        }

        @Override
        public void requestDisallowInterceptTouchEvent(boolean z) {
            if (DialogsActivity.this.maybeStartTracking && !DialogsActivity.this.startedTracking) {
                onTouchEvent(null);
            }
            super.requestDisallowInterceptTouchEvent(z);
        }

        @Override
        public void setPadding(int i, int i2, int i3, int i4) {
            DialogsActivity.this.fragmentContextTopPadding = i2;
            DialogsActivity.this.updateTopPadding();
        }
    }

    public interface DialogsActivityDelegate {
        boolean didSelectDialogs(DialogsActivity dialogsActivity, ArrayList arrayList, CharSequence charSequence, boolean z, boolean z2, int i, TopicsFragment topicsFragment);
    }

    public class DialogsHeader extends TLRPC.Dialog {
        public int headerType;

        public DialogsHeader(int i) {
            this.headerType = i;
        }
    }

    public class DialogsRecyclerView extends BlurredRecyclerView implements StoriesListPlaceProvider.ClippedView {
        public int additionalPadding;
        float animateFromSelectorPosition;
        boolean animateSwitchingSelector;
        private RecyclerListView animationSupportListView;
        LongSparseArray animationSupportViewsByDialogId;
        private int appliedPaddingTop;
        private boolean firstLayout;
        private boolean ignoreLayout;
        float lastDrawSelectorY;
        private int lastListPadding;
        private int lastTop;
        Paint paint;
        private final ViewPage parentPage;
        UserListPoller poller;
        RectF rectF;
        private float rightFragmentOpenedProgress;
        private Paint selectorPaint;
        float selectorPositionProgress;
        public boolean updateDialogsOnNextDraw;

        public DialogsRecyclerView(Context context, ViewPage viewPage) {
            super(context);
            this.firstLayout = true;
            this.paint = new Paint();
            this.rectF = new RectF();
            this.selectorPositionProgress = 1.0f;
            this.parentPage = viewPage;
            this.additionalClipBottom = AndroidUtilities.dp(200.0f);
        }

        private boolean drawMovingViewsOverlayed() {
            return getItemAnimator() != null && getItemAnimator().isRunning();
        }

        public void lambda$onTouchEvent$0(ValueAnimator valueAnimator) {
            setViewsOffset(((Float) valueAnimator.getAnimatedValue()).floatValue());
        }

        public void toggleArchiveHidden(boolean z, DialogCell dialogCell) {
            SharedConfig.toggleArchiveHidden();
            UndoView undoView = DialogsActivity.this.getUndoView();
            if (!SharedConfig.archiveHidden) {
                undoView.showWithAction(0L, 7, null, null);
                updatePullState();
                if (!z || dialogCell == null) {
                    return;
                }
                dialogCell.resetPinnedArchiveState();
                dialogCell.invalidate();
                return;
            }
            if (dialogCell != null) {
                DialogsActivity.this.disableActionBarScrolling = true;
                DialogsActivity.this.waitingForScrollFinished = true;
                int measuredHeight = dialogCell.getMeasuredHeight() + (dialogCell.getTop() - getPaddingTop());
                DialogsActivity dialogsActivity = DialogsActivity.this;
                if (dialogsActivity.hasStories && !dialogsActivity.dialogStoriesCell.isExpanded()) {
                    DialogsActivity.this.fixScrollYAfterArchiveOpened = true;
                    measuredHeight += AndroidUtilities.dp(81.0f);
                }
                smoothScrollBy(0, measuredHeight, CubicBezierInterpolator.EASE_OUT);
                if (z) {
                    DialogsActivity.this.updatePullAfterScroll = true;
                } else {
                    updatePullState();
                }
            }
            undoView.showWithAction(0L, 6, null, null);
        }

        public void updatePullState() {
            this.parentPage.archivePullViewState = SharedConfig.archiveHidden ? 2 : 0;
            if (this.parentPage.pullForegroundDrawable != null) {
                this.parentPage.pullForegroundDrawable.setWillDraw(this.parentPage.archivePullViewState != 0);
            }
        }

        @Override
        public void addView(View view, int i, ViewGroup.LayoutParams layoutParams) {
            super.addView(view, i, layoutParams);
            view.setTranslationY(DialogsActivity.viewOffset);
            view.setTranslationX(0.0f);
            view.setAlpha(1.0f);
        }

        @Override
        public boolean allowSelectChildAtPosition(View view) {
            return !(view instanceof HeaderCell) || view.isClickable();
        }

        @Override
        public void dispatchDraw(android.graphics.Canvas r24) {
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.DialogsActivity.DialogsRecyclerView.dispatchDraw(android.graphics.Canvas):void");
        }

        @Override
        public boolean drawChild(Canvas canvas, View view, long j) {
            if (drawMovingViewsOverlayed() && (view instanceof DialogCell) && ((DialogCell) view).isMoving()) {
                return true;
            }
            return super.drawChild(canvas, view, j);
        }

        public float getViewOffset() {
            return DialogsActivity.viewOffset;
        }

        @Override
        public void onDetachedFromWindow() {
            super.onDetachedFromWindow();
        }

        @Override
        public void onDraw(Canvas canvas) {
            if (this.parentPage.pullForegroundDrawable != null && DialogsActivity.viewOffset != 0.0f) {
                int paddingTop = getPaddingTop();
                if (paddingTop != 0) {
                    canvas.save();
                    canvas.translate(0.0f, paddingTop);
                }
                this.parentPage.pullForegroundDrawable.drawOverScroll(canvas);
                if (paddingTop != 0) {
                    canvas.restore();
                }
            }
            super.onDraw(canvas);
        }

        @Override
        public boolean onInterceptTouchEvent(MotionEvent motionEvent) {
            if (this.fastScrollAnimationRunning || DialogsActivity.this.waitingForScrollFinished || this.parentPage.dialogsItemAnimator.isRunning()) {
                return false;
            }
            if (motionEvent.getAction() == 0) {
                DialogsActivity.this.allowSwipeDuringCurrentTouch = !((BaseFragment) r0).actionBar.isActionModeShowed();
            }
            return super.onInterceptTouchEvent(motionEvent);
        }

        @Override
        public void onLayout(boolean z, int i, int i2, int i3, int i4) {
            super.onLayout(z, i, i2, i3, i4);
            this.lastListPadding = getPaddingTop();
            this.lastTop = i2;
            DialogsActivity.this.scrollAdditionalOffset = 0.0f;
            this.parentPage.pageAdditionalOffset = 0;
        }

        @Override
        public void onMeasure(int i, int i2) {
            int currentActionBarHeight;
            int findFirstVisibleItemPosition = this.parentPage.layoutManager.findFirstVisibleItemPosition();
            if (findFirstVisibleItemPosition != -1 && this.parentPage.itemTouchhelper.isIdle() && !this.parentPage.layoutManager.hasPendingScrollPosition() && this.parentPage.listView.getScrollState() != 1) {
                RecyclerView.ViewHolder findViewHolderForAdapterPosition = this.parentPage.listView.findViewHolderForAdapterPosition(findFirstVisibleItemPosition);
                if (findViewHolderForAdapterPosition != null) {
                    int top = findViewHolderForAdapterPosition.itemView.getTop();
                    if (this.parentPage.dialogsType == 0 && DialogsActivity.this.hasHiddenArchive() && this.parentPage.archivePullViewState == 2) {
                        findFirstVisibleItemPosition = Math.max(1, findFirstVisibleItemPosition);
                    }
                    this.ignoreLayout = true;
                    this.parentPage.layoutManager.scrollToPositionWithOffset(findFirstVisibleItemPosition, (int) ((top - this.lastListPadding) + DialogsActivity.this.scrollAdditionalOffset + this.parentPage.pageAdditionalOffset));
                    this.ignoreLayout = false;
                }
            } else if (findFirstVisibleItemPosition == -1 && this.firstLayout) {
                this.parentPage.layoutManager.scrollToPositionWithOffset((this.parentPage.dialogsType == 0 && DialogsActivity.this.hasHiddenArchive()) ? 1 : 0, (int) DialogsActivity.this.scrollYOffset);
            }
            if (!DialogsActivity.this.onlySelect || DialogsActivity.this.initialDialogsType == 3) {
                this.ignoreLayout = true;
                DialogsActivity dialogsActivity = DialogsActivity.this;
                currentActionBarHeight = (dialogsActivity.hasStories || (dialogsActivity.filterTabsView != null && DialogsActivity.this.filterTabsView.getVisibility() == 0)) ? ActionBar.getCurrentActionBarHeight() + (((BaseFragment) DialogsActivity.this).actionBar.getOccupyStatusBar() ? AndroidUtilities.statusBarHeight : 0) : (!((BaseFragment) DialogsActivity.this).inPreviewMode || Build.VERSION.SDK_INT < 21) ? 0 : AndroidUtilities.statusBarHeight;
                DialogsActivity dialogsActivity2 = DialogsActivity.this;
                if (dialogsActivity2.hasStories && !dialogsActivity2.actionModeFullyShowed) {
                    currentActionBarHeight += AndroidUtilities.dp(81.0f);
                }
                this.additionalPadding = 0;
                if (DialogsActivity.this.authHintCell != null && DialogsActivity.this.authHintCellProgress != 0.0f && !DialogsActivity.this.authHintCellAnimating) {
                    currentActionBarHeight += DialogsActivity.this.authHintCell.getMeasuredHeight();
                    this.additionalPadding += DialogsActivity.this.authHintCell.getMeasuredHeight();
                }
                if (currentActionBarHeight != getPaddingTop()) {
                    setTopGlowOffset(currentActionBarHeight);
                    setPadding(0, currentActionBarHeight, 0, 0);
                    if (DialogsActivity.this.hasStories) {
                        this.parentPage.progressView.setPaddingTop(currentActionBarHeight - AndroidUtilities.dp(81.0f));
                    } else {
                        this.parentPage.progressView.setPaddingTop(currentActionBarHeight);
                    }
                    for (int i3 = 0; i3 < getChildCount(); i3++) {
                        if (getChildAt(i3) instanceof DialogsAdapter.LastEmptyView) {
                            getChildAt(i3).requestLayout();
                        }
                    }
                }
                this.ignoreLayout = false;
            } else {
                currentActionBarHeight = 0;
            }
            if (this.firstLayout && DialogsActivity.this.getMessagesController().dialogsLoaded) {
                if (this.parentPage.dialogsType == 0 && DialogsActivity.this.hasHiddenArchive()) {
                    this.ignoreLayout = true;
                    ((LinearLayoutManager) getLayoutManager()).scrollToPositionWithOffset(1, (int) DialogsActivity.this.scrollYOffset);
                    this.ignoreLayout = false;
                }
                this.firstLayout = false;
            }
            super.onMeasure(i, i2);
            if (DialogsActivity.this.onlySelect || this.appliedPaddingTop == currentActionBarHeight || DialogsActivity.this.viewPages == null || DialogsActivity.this.viewPages.length <= 1 || DialogsActivity.this.startedTracking) {
                return;
            }
            if ((DialogsActivity.this.tabsAnimation != null && DialogsActivity.this.tabsAnimation.isRunning()) || DialogsActivity.this.tabsAnimationInProgress || DialogsActivity.this.filterTabsView == null) {
                return;
            }
            DialogsActivity.this.filterTabsView.isAnimatingIndicator();
        }

        @Override
        public boolean onTouchEvent(MotionEvent motionEvent) {
            ArrayList arrayList;
            DialogsActivity dialogsActivity;
            int i;
            ArrayList arrayList2;
            DialogsActivity dialogsActivity2;
            int i2;
            if (this.fastScrollAnimationRunning || DialogsActivity.this.waitingForScrollFinished || DialogsActivity.this.rightFragmentTransitionInProgress) {
                return false;
            }
            int action = motionEvent.getAction();
            if (action == 0) {
                setOverScrollMode(0);
            }
            if ((action == 1 || action == 3) && !this.parentPage.itemTouchhelper.isIdle() && this.parentPage.swipeController.swipingFolder) {
                this.parentPage.swipeController.swipeFolderBack = true;
                if (this.parentPage.itemTouchhelper.checkHorizontalSwipe(null, 4) != 0 && this.parentPage.swipeController.currentItemViewHolder != null) {
                    View view = this.parentPage.swipeController.currentItemViewHolder.itemView;
                    if (view instanceof DialogCell) {
                        DialogCell dialogCell = (DialogCell) view;
                        long dialogId = dialogCell.getDialogId();
                        if (DialogObject.isFolderDialogId(dialogId)) {
                            toggleArchiveHidden(false, dialogCell);
                        } else {
                            TLRPC.Dialog dialog = (TLRPC.Dialog) DialogsActivity.this.getMessagesController().dialogs_dict.get(dialogId);
                            if (dialog != null) {
                                if (SharedConfig.getChatSwipeAction(((BaseFragment) DialogsActivity.this).currentAccount) == 1) {
                                    arrayList2 = new ArrayList();
                                    arrayList2.add(Long.valueOf(dialogId));
                                    DialogsActivity.this.canReadCount = (dialog.unread_count > 0 || dialog.unread_mark) ? 1 : 0;
                                    dialogsActivity2 = DialogsActivity.this;
                                    i2 = 101;
                                } else if (SharedConfig.getChatSwipeAction(((BaseFragment) DialogsActivity.this).currentAccount) == 3) {
                                    if (DialogsActivity.this.getMessagesController().isDialogMuted(dialogId, 0L)) {
                                        arrayList = new ArrayList();
                                        arrayList.add(Long.valueOf(dialogId));
                                        DialogsActivity dialogsActivity3 = DialogsActivity.this;
                                        dialogsActivity3.canMuteCount = !MessagesController.getInstance(((BaseFragment) dialogsActivity3).currentAccount).isDialogMuted(dialogId, 0L) ? 1 : 0;
                                        DialogsActivity dialogsActivity4 = DialogsActivity.this;
                                        dialogsActivity4.canUnmuteCount = dialogsActivity4.canMuteCount > 0 ? 0 : 1;
                                        dialogsActivity = DialogsActivity.this;
                                        i = 104;
                                        dialogsActivity.performSelectedDialogsAction(arrayList, i, true, false);
                                    } else {
                                        NotificationsController.getInstance(UserConfig.selectedAccount).setDialogNotificationsSettings(dialogId, 0L, 3);
                                        if (BulletinFactory.canShowBulletin(DialogsActivity.this)) {
                                            BulletinFactory.createMuteBulletin(DialogsActivity.this, 3).show();
                                        }
                                    }
                                } else if (SharedConfig.getChatSwipeAction(((BaseFragment) DialogsActivity.this).currentAccount) == 0) {
                                    arrayList2 = new ArrayList();
                                    arrayList2.add(Long.valueOf(dialogId));
                                    DialogsActivity.this.canPinCount = !DialogsActivity.this.isDialogPinned(dialog) ? 1 : 0;
                                    dialogsActivity2 = DialogsActivity.this;
                                    i2 = 100;
                                } else if (SharedConfig.getChatSwipeAction(((BaseFragment) DialogsActivity.this).currentAccount) == 4) {
                                    arrayList = new ArrayList();
                                    arrayList.add(Long.valueOf(dialogId));
                                    dialogsActivity = DialogsActivity.this;
                                    i = 102;
                                    dialogsActivity.performSelectedDialogsAction(arrayList, i, true, false);
                                }
                                dialogsActivity2.performSelectedDialogsAction(arrayList2, i2, true, false);
                            }
                        }
                    }
                }
            }
            boolean onTouchEvent = super.onTouchEvent(motionEvent);
            if (this.parentPage.dialogsType == 0 && ((action == 1 || action == 3) && this.parentPage.archivePullViewState == 2 && DialogsActivity.this.hasHiddenArchive() && ((LinearLayoutManager) getLayoutManager()).findFirstVisibleItemPosition() == 0)) {
                int paddingTop = getPaddingTop();
                DialogCell findArchiveDialogCell = DialogsActivity.this.findArchiveDialogCell(this.parentPage);
                if (findArchiveDialogCell != null) {
                    int dp = (int) (AndroidUtilities.dp(SharedConfig.useThreeLinesLayout ? 78.0f : 72.0f) * 0.85f);
                    int top = (findArchiveDialogCell.getTop() - paddingTop) + findArchiveDialogCell.getMeasuredHeight();
                    long currentTimeMillis = System.currentTimeMillis() - DialogsActivity.this.startArchivePullingTime;
                    if (top < dp || currentTimeMillis < 200) {
                        DialogsActivity.this.disableActionBarScrolling = true;
                        smoothScrollBy(0, top, CubicBezierInterpolator.EASE_OUT_QUINT);
                        this.parentPage.archivePullViewState = 2;
                    } else if (this.parentPage.archivePullViewState != 1) {
                        if (getViewOffset() == 0.0f) {
                            DialogsActivity.this.disableActionBarScrolling = true;
                            smoothScrollBy(0, findArchiveDialogCell.getTop() - paddingTop, CubicBezierInterpolator.EASE_OUT_QUINT);
                        }
                        if (!DialogsActivity.this.canShowHiddenArchive) {
                            DialogsActivity.this.canShowHiddenArchive = true;
                            performHapticFeedback(3, 2);
                            if (this.parentPage.pullForegroundDrawable != null) {
                                this.parentPage.pullForegroundDrawable.colorize(true);
                            }
                        }
                        findArchiveDialogCell.startOutAnimation();
                        this.parentPage.archivePullViewState = 1;
                        if (AndroidUtilities.isAccessibilityScreenReaderEnabled()) {
                            AndroidUtilities.makeAccessibilityAnnouncement(LocaleController.getString(R.string.AccDescrArchivedChatsShown));
                        }
                    }
                    if (getViewOffset() != 0.0f) {
                        ValueAnimator ofFloat = ValueAnimator.ofFloat(getViewOffset(), 0.0f);
                        ofFloat.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                            @Override
                            public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                                DialogsActivity.DialogsRecyclerView.this.lambda$onTouchEvent$0(valueAnimator);
                            }
                        });
                        ofFloat.setDuration(Math.max(100L, 350.0f - ((getViewOffset() / PullForegroundDrawable.getMaxOverscroll()) * 120.0f)));
                        ofFloat.setInterpolator(CubicBezierInterpolator.EASE_OUT_QUINT);
                        setScrollEnabled(false);
                        ofFloat.addListener(new AnimatorListenerAdapter() {
                            @Override
                            public void onAnimationEnd(Animator animator) {
                                super.onAnimationEnd(animator);
                                DialogsRecyclerView.this.setScrollEnabled(true);
                            }
                        });
                        ofFloat.start();
                    }
                }
            }
            return onTouchEvent;
        }

        public void prepareSelectorForAnimation() {
            this.selectorPositionProgress = 0.0f;
            this.animateFromSelectorPosition = this.lastDrawSelectorY;
            this.animateSwitchingSelector = this.rightFragmentOpenedProgress != 0.0f;
        }

        @Override
        public void removeView(View view) {
            super.removeView(view);
            view.setTranslationY(0.0f);
            view.setTranslationX(0.0f);
            view.setAlpha(1.0f);
        }

        @Override
        public void requestLayout() {
            if (this.ignoreLayout) {
                return;
            }
            super.requestLayout();
        }

        @Override
        public void setAdapter(RecyclerView.Adapter adapter) {
            super.setAdapter(adapter);
            this.firstLayout = true;
        }

        public void setAnimationSupportView(RecyclerListView recyclerListView, float f, boolean z, boolean z2) {
            RecyclerListView recyclerListView2 = recyclerListView == null ? this.animationSupportListView : this;
            if (recyclerListView2 == null) {
                this.animationSupportListView = recyclerListView;
                return;
            }
            DialogCell dialogCell = null;
            DialogCell dialogCell2 = null;
            int i = Integer.MAX_VALUE;
            for (int i2 = 0; i2 < recyclerListView2.getChildCount(); i2++) {
                View childAt = recyclerListView2.getChildAt(i2);
                if (childAt instanceof DialogCell) {
                    DialogCell dialogCell3 = (DialogCell) childAt;
                    if (dialogCell3.getDialogId() == DialogsActivity.this.rightSlidingDialogContainer.getCurrentFragmetDialogId()) {
                        dialogCell = dialogCell3;
                    }
                    if (childAt.getTop() >= 0 && dialogCell3.getDialogId() != 0 && childAt.getTop() < i) {
                        i = dialogCell3.getTop();
                        dialogCell2 = dialogCell3;
                    }
                }
            }
            DialogCell dialogCell4 = (dialogCell == null || getAdapter().getItemCount() * AndroidUtilities.dp(70.0f) <= getMeasuredHeight() || ((float) (dialogCell2.getTop() - getPaddingTop())) <= ((float) (getMeasuredHeight() - getPaddingTop())) / 2.0f) ? dialogCell2 : dialogCell;
            this.animationSupportListView = recyclerListView;
            if (dialogCell4 != null) {
                if (recyclerListView != null) {
                    recyclerListView.setPadding(getPaddingLeft(), this.topPadding, getPaddingLeft(), getPaddingBottom());
                    DialogsAdapter dialogsAdapter = (DialogsAdapter) recyclerListView.getAdapter();
                    int findDialogPosition = dialogsAdapter.findDialogPosition(dialogCell4.getDialogId());
                    int top = (int) ((dialogCell4.getTop() - recyclerListView2.getPaddingTop()) + f);
                    if (findDialogPosition >= 0) {
                        boolean z3 = this.parentPage.dialogsType == 0 && this.parentPage.archivePullViewState == 2 && DialogsActivity.this.hasHiddenArchive();
                        DialogsActivity dialogsActivity = DialogsActivity.this;
                        ((LinearLayoutManager) recyclerListView.getLayoutManager()).scrollToPositionWithOffset(findDialogPosition, dialogsAdapter.fixScrollGap(this, findDialogPosition, top, z3, dialogsActivity.hasStories, dialogsActivity.canShowFilterTabsView, z));
                    }
                }
                int findDialogPosition2 = ((DialogsAdapter) getAdapter()).findDialogPosition(dialogCell4.getDialogId());
                int top2 = dialogCell4.getTop() - getPaddingTop();
                if (z2 && DialogsActivity.this.hasStories) {
                    top2 += AndroidUtilities.dp(81.0f);
                }
                if (findDialogPosition2 >= 0) {
                    ((LinearLayoutManager) getLayoutManager()).scrollToPositionWithOffset(findDialogPosition2, top2);
                }
            }
        }

        public void setOpenRightFragmentProgress(float f) {
            this.rightFragmentOpenedProgress = f;
            invalidate();
        }

        public void setViewsOffset(float f) {
            View findViewByPosition;
            DialogsActivity.viewOffset = f;
            int childCount = getChildCount();
            for (int i = 0; i < childCount; i++) {
                getChildAt(i).setTranslationY(f);
            }
            if (this.selectorPosition != -1 && (findViewByPosition = getLayoutManager().findViewByPosition(this.selectorPosition)) != null) {
                this.selectorRect.set(findViewByPosition.getLeft(), (int) (findViewByPosition.getTop() + f), findViewByPosition.getRight(), (int) (findViewByPosition.getBottom() + f));
                this.selectorDrawable.setBounds(this.selectorRect);
            }
            invalidate();
        }

        @Override
        public void updateClip(int[] iArr) {
            int paddingTop = (int) (getPaddingTop() + DialogsActivity.this.scrollYOffset);
            iArr[0] = paddingTop;
            iArr[1] = paddingTop + getMeasuredHeight();
        }

        @Override
        protected boolean updateEmptyViewAnimated() {
            return true;
        }
    }

    public class SwipeController extends ItemTouchHelper.Callback {
        private RecyclerView.ViewHolder currentItemViewHolder;
        private ViewPage parentPage;
        private boolean swipeFolderBack;
        private boolean swipingFolder;

        public SwipeController(ViewPage viewPage) {
            this.parentPage = viewPage;
        }

        public void lambda$onSwiped$0() {
            DialogsActivity.this.setDialogsListFrozen(false);
        }

        public void lambda$onSwiped$1() {
            DialogsActivity.this.setDialogsListFrozen(false);
        }

        public void lambda$onSwiped$2(TLRPC.Dialog dialog, int i) {
            DialogsActivity.this.dialogsListFrozen = true;
            DialogsActivity.this.getMessagesController().addDialogToFolder(dialog.id, 0, i, 0L);
            DialogsActivity.this.dialogsListFrozen = false;
            ArrayList<TLRPC.Dialog> dialogs = DialogsActivity.this.getMessagesController().getDialogs(0);
            int indexOf = dialogs.indexOf(dialog);
            if (indexOf < 0) {
                this.parentPage.updateList(false);
                return;
            }
            ArrayList<TLRPC.Dialog> dialogs2 = DialogsActivity.this.getMessagesController().getDialogs(1);
            if (!dialogs2.isEmpty() || indexOf != 1) {
                DialogsActivity.this.setDialogsListFrozen(true);
                this.parentPage.dialogsItemAnimator.prepareForRemove();
                this.parentPage.updateList(true);
                DialogsActivity.this.checkAnimationFinished();
            }
            if (dialogs2.isEmpty()) {
                dialogs.remove(0);
                if (indexOf == 1) {
                    DialogsActivity.this.setDialogsListFrozen(true);
                    this.parentPage.updateList(true);
                    DialogsActivity.this.checkAnimationFinished();
                } else {
                    if (!DialogsActivity.this.frozenDialogsList.isEmpty()) {
                        DialogsActivity.this.frozenDialogsList.remove(0);
                    }
                    this.parentPage.dialogsItemAnimator.prepareForRemove();
                    this.parentPage.updateList(true);
                }
            }
        }

        public void lambda$onSwiped$3(final org.telegram.tgnet.TLRPC.Dialog r12, int r13, int r14) {
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.DialogsActivity.SwipeController.lambda$onSwiped$3(org.telegram.tgnet.TLRPC$Dialog, int, int):void");
        }

        @Override
        public int convertToAbsoluteDirection(int i, int i2) {
            if (this.swipeFolderBack) {
                return 0;
            }
            return super.convertToAbsoluteDirection(i, i2);
        }

        @Override
        public long getAnimationDuration(RecyclerView recyclerView, int i, float f, float f2) {
            if (i == 4) {
                return 200L;
            }
            if (i == 8 && DialogsActivity.this.movingView != null) {
                final DialogCell dialogCell = DialogsActivity.this.movingView;
                AndroidUtilities.runOnUIThread(new Runnable() {
                    @Override
                    public final void run() {
                        dialogCell.setBackgroundDrawable(null);
                    }
                }, this.parentPage.dialogsItemAnimator.getMoveDuration());
                DialogsActivity.this.movingView = null;
            }
            return super.getAnimationDuration(recyclerView, i, f, f2);
        }

        @Override
        public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
            TLRPC.Dialog dialog;
            if (!DialogsActivity.this.waitingForDialogsAnimationEnd(this.parentPage) && ((((BaseFragment) DialogsActivity.this).parentLayout == null || !((BaseFragment) DialogsActivity.this).parentLayout.isInPreviewMode()) && !DialogsActivity.this.rightSlidingDialogContainer.hasFragment())) {
                if (this.swipingFolder && this.swipeFolderBack) {
                    View view = viewHolder.itemView;
                    if (view instanceof DialogCell) {
                        ((DialogCell) view).swipeCanceled = true;
                    }
                    this.swipingFolder = false;
                    return 0;
                }
                if (!DialogsActivity.this.onlySelect && this.parentPage.isDefaultDialogType() && DialogsActivity.this.slidingView == null) {
                    View view2 = viewHolder.itemView;
                    if (view2 instanceof DialogCell) {
                        DialogCell dialogCell = (DialogCell) view2;
                        long dialogId = dialogCell.getDialogId();
                        MessagesController.DialogFilter dialogFilter = null;
                        if (((BaseFragment) DialogsActivity.this).actionBar.isActionModeShowed(null)) {
                            TLRPC.Dialog dialog2 = (TLRPC.Dialog) DialogsActivity.this.getMessagesController().dialogs_dict.get(dialogId);
                            if (!DialogsActivity.this.allowMoving || dialog2 == null || !DialogsActivity.this.isDialogPinned(dialog2) || DialogObject.isFolderDialogId(dialogId)) {
                                return 0;
                            }
                            DialogsActivity.this.movingView = (DialogCell) viewHolder.itemView;
                            DialogsActivity.this.movingView.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                            this.swipeFolderBack = false;
                            return ItemTouchHelper.Callback.makeMovementFlags(3, 0);
                        }
                        int i = DialogsActivity.this.initialDialogsType;
                        try {
                            i = this.parentPage.dialogsAdapter.getDialogsType();
                        } catch (Exception unused) {
                        }
                        if ((DialogsActivity.this.filterTabsView == null || DialogsActivity.this.filterTabsView.getVisibility() != 0 || SharedConfig.getChatSwipeAction(((BaseFragment) DialogsActivity.this).currentAccount) != 5) && DialogsActivity.this.allowSwipeDuringCurrentTouch && (((dialogId != DialogsActivity.this.getUserConfig().clientUserId && dialogId != 777000 && i != 7 && i != 8) || SharedConfig.getChatSwipeAction(((BaseFragment) DialogsActivity.this).currentAccount) != 2) && (!DialogsActivity.this.getMessagesController().isPromoDialog(dialogId, false) || DialogsActivity.this.getMessagesController().promoDialogType == MessagesController.PROMO_TYPE_PSA))) {
                            boolean z = DialogsActivity.this.folderId == 0 && (SharedConfig.getChatSwipeAction(((BaseFragment) DialogsActivity.this).currentAccount) == 3 || SharedConfig.getChatSwipeAction(((BaseFragment) DialogsActivity.this).currentAccount) == 1 || SharedConfig.getChatSwipeAction(((BaseFragment) DialogsActivity.this).currentAccount) == 0 || SharedConfig.getChatSwipeAction(((BaseFragment) DialogsActivity.this).currentAccount) == 4) && !DialogsActivity.this.rightSlidingDialogContainer.hasFragment();
                            if (SharedConfig.getChatSwipeAction(((BaseFragment) DialogsActivity.this).currentAccount) == 1) {
                                if (DialogsActivity.this.viewPages[0].dialogsType == 7 || DialogsActivity.this.viewPages[0].dialogsType == 8) {
                                    dialogFilter = DialogsActivity.this.getMessagesController().selectedDialogFilter[DialogsActivity.this.viewPages[0].dialogsType == 8 ? (char) 1 : (char) 0];
                                }
                                if (dialogFilter != null && (dialogFilter.flags & MessagesController.DIALOG_FILTER_FLAG_EXCLUDE_READ) != 0 && (dialog = (TLRPC.Dialog) DialogsActivity.this.getMessagesController().dialogs_dict.get(dialogId)) != null && !dialogFilter.alwaysShow(((BaseFragment) DialogsActivity.this).currentAccount, dialog) && (dialog.unread_count > 0 || dialog.unread_mark)) {
                                    z = false;
                                }
                            }
                            this.swipeFolderBack = false;
                            this.swipingFolder = (z && !DialogObject.isFolderDialogId(dialogCell.getDialogId())) || (SharedConfig.archiveHidden && DialogObject.isFolderDialogId(dialogCell.getDialogId()));
                            dialogCell.setSliding(true);
                            return ItemTouchHelper.Callback.makeMovementFlags(0, 4);
                        }
                    }
                }
            }
            return 0;
        }

        @Override
        public float getSwipeEscapeVelocity(float f) {
            return 3500.0f;
        }

        @Override
        public float getSwipeThreshold(RecyclerView.ViewHolder viewHolder) {
            return 0.45f;
        }

        @Override
        public float getSwipeVelocityThreshold(float f) {
            return Float.MAX_VALUE;
        }

        @Override
        public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder viewHolder2) {
            View view = viewHolder2.itemView;
            if (!(view instanceof DialogCell)) {
                return false;
            }
            long dialogId = ((DialogCell) view).getDialogId();
            TLRPC.Dialog dialog = (TLRPC.Dialog) DialogsActivity.this.getMessagesController().dialogs_dict.get(dialogId);
            if (dialog == null || !DialogsActivity.this.isDialogPinned(dialog) || DialogObject.isFolderDialogId(dialogId)) {
                return false;
            }
            int adapterPosition = viewHolder.getAdapterPosition();
            int adapterPosition2 = viewHolder2.getAdapterPosition();
            if (this.parentPage.listView.getItemAnimator() == null) {
                ViewPage viewPage = this.parentPage;
                viewPage.listView.setItemAnimator(viewPage.dialogsItemAnimator);
            }
            this.parentPage.dialogsAdapter.moveDialogs(this.parentPage.listView, adapterPosition, adapterPosition2);
            if (DialogsActivity.this.viewPages[0].dialogsType == 7 || DialogsActivity.this.viewPages[0].dialogsType == 8) {
                MessagesController.DialogFilter dialogFilter = DialogsActivity.this.getMessagesController().selectedDialogFilter[DialogsActivity.this.viewPages[0].dialogsType == 8 ? (char) 1 : (char) 0];
                if (!DialogsActivity.this.movingDialogFilters.contains(dialogFilter)) {
                    DialogsActivity.this.movingDialogFilters.add(dialogFilter);
                }
            } else {
                DialogsActivity.this.movingWas = true;
            }
            return true;
        }

        @Override
        public void onSelectedChanged(RecyclerView.ViewHolder viewHolder, int i) {
            if (viewHolder != null) {
                this.parentPage.listView.hideSelector(false);
            }
            this.currentItemViewHolder = viewHolder;
            if (viewHolder != null) {
                View view = viewHolder.itemView;
                if (view instanceof DialogCell) {
                    ((DialogCell) view).swipeCanceled = false;
                }
            }
            super.onSelectedChanged(viewHolder, i);
        }

        @Override
        public void onSwiped(RecyclerView.ViewHolder viewHolder, int i) {
            if (viewHolder == null) {
                DialogsActivity.this.slidingView = null;
                return;
            }
            DialogCell dialogCell = (DialogCell) viewHolder.itemView;
            long dialogId = dialogCell.getDialogId();
            if (DialogObject.isFolderDialogId(dialogId)) {
                this.parentPage.listView.toggleArchiveHidden(false, dialogCell);
                return;
            }
            final TLRPC.Dialog dialog = (TLRPC.Dialog) DialogsActivity.this.getMessagesController().dialogs_dict.get(dialogId);
            if (dialog == null) {
                return;
            }
            if (!DialogsActivity.this.getMessagesController().isPromoDialog(dialogId, false) && DialogsActivity.this.folderId == 0 && SharedConfig.getChatSwipeAction(((BaseFragment) DialogsActivity.this).currentAccount) == 1) {
                ArrayList arrayList = new ArrayList();
                arrayList.add(Long.valueOf(dialogId));
                DialogsActivity.this.canReadCount = (dialog.unread_count > 0 || dialog.unread_mark) ? 1 : 0;
                DialogsActivity.this.performSelectedDialogsAction(arrayList, 101, true, false);
                return;
            }
            DialogsActivity.this.slidingView = dialogCell;
            final int adapterPosition = viewHolder.getAdapterPosition();
            final int itemCount = this.parentPage.dialogsAdapter.getItemCount();
            Runnable runnable = new Runnable() {
                @Override
                public final void run() {
                    DialogsActivity.SwipeController.this.lambda$onSwiped$3(dialog, itemCount, adapterPosition);
                }
            };
            DialogsActivity.this.setDialogsListFrozen(true);
            if (Utilities.random.nextInt(1000) != 1) {
                runnable.run();
                return;
            }
            if (DialogsActivity.this.pacmanAnimation == null) {
                DialogsActivity.this.pacmanAnimation = new PacmanAnimation(this.parentPage.listView);
            }
            DialogsActivity.this.pacmanAnimation.setFinishRunnable(runnable);
            DialogsActivity.this.pacmanAnimation.start();
        }
    }

    public class ViewPage extends FrameLayout {
        public boolean animateStoriesView;
        private DialogsAdapter animationSupportDialogsAdapter;
        private RecyclerListView animationSupportListView;
        private int archivePullViewState;
        private DialogsAdapter dialogsAdapter;
        private DialogsItemAnimator dialogsItemAnimator;
        private int dialogsType;
        private boolean isLocked;
        private ItemTouchHelper itemTouchhelper;
        private int lastItemsCount;
        private LinearLayoutManager layoutManager;
        public DialogsRecyclerView listView;
        public int pageAdditionalOffset;
        private FlickerLoadingView progressView;
        private PullForegroundDrawable pullForegroundDrawable;
        private RecyclerItemsEnterAnimator recyclerItemsEnterAnimator;
        Runnable saveScrollPositionRunnable;
        private RecyclerAnimationScrollHelper scrollHelper;
        public RecyclerListViewScroller scroller;
        private int selectedType;
        private SwipeController swipeController;
        Runnable updateListRunnable;
        boolean updating;

        public ViewPage(Context context) {
            super(context);
            this.saveScrollPositionRunnable = new Runnable() {
                @Override
                public final void run() {
                    DialogsActivity.ViewPage.this.lambda$new$0();
                }
            };
            this.updateListRunnable = new Runnable() {
                @Override
                public final void run() {
                    DialogsActivity.ViewPage.this.lambda$new$1();
                }
            };
        }

        public void lambda$new$0() {
            DialogsRecyclerView dialogsRecyclerView = this.listView;
            if (dialogsRecyclerView == null || dialogsRecyclerView.getScrollState() != 0 || this.listView.getChildCount() <= 0 || this.listView.getLayoutManager() == null) {
                return;
            }
            int i = 1;
            boolean z = this.dialogsType == 0 && DialogsActivity.this.hasHiddenArchive() && this.archivePullViewState == 2;
            float f = DialogsActivity.this.scrollYOffset;
            LinearLayoutManager linearLayoutManager = (LinearLayoutManager) this.listView.getLayoutManager();
            View view = null;
            int i2 = Integer.MAX_VALUE;
            int i3 = -1;
            for (int i4 = 0; i4 < this.listView.getChildCount(); i4++) {
                DialogsRecyclerView dialogsRecyclerView2 = this.listView;
                int childAdapterPosition = dialogsRecyclerView2.getChildAdapterPosition(dialogsRecyclerView2.getChildAt(i4));
                View childAt = this.listView.getChildAt(i4);
                if (childAdapterPosition != -1 && childAt != null && childAt.getTop() < i2) {
                    i2 = childAt.getTop();
                    i3 = childAdapterPosition;
                    view = childAt;
                }
            }
            if (view != null) {
                float top = view.getTop() - this.listView.getPaddingTop();
                if (DialogsActivity.this.hasStories) {
                    f = 0.0f;
                }
                if (this.listView.getScrollState() != 1) {
                    if (z && i3 == 0 && ((this.listView.getPaddingTop() - view.getTop()) - view.getMeasuredHeight()) + f < 0.0f) {
                        top = f;
                    } else {
                        i = i3;
                    }
                    linearLayoutManager.scrollToPositionWithOffset(i, (int) top);
                }
            }
        }

        public void lambda$new$1() {
            this.dialogsAdapter.updateList(this.saveScrollPositionRunnable);
            DialogsActivity.this.invalidateScrollY = true;
            this.listView.updateDialogsOnNextDraw = true;
            this.updating = false;
        }

        public boolean isDefaultDialogType() {
            int i = this.dialogsType;
            return i == 0 || i == 7 || i == 8;
        }

        @Override
        protected void onMeasure(int i, int i2) {
            ((FrameLayout.LayoutParams) this.listView.getLayoutParams()).bottomMargin = this.animateStoriesView ? -AndroidUtilities.dp(85.0f) : 0;
            super.onMeasure(i, i2);
        }

        public void updateList(boolean z) {
            if (((BaseFragment) DialogsActivity.this).isPaused) {
                return;
            }
            if (z) {
                AndroidUtilities.cancelRunOnUIThread(this.updateListRunnable);
                this.listView.setItemAnimator(this.dialogsItemAnimator);
                this.updateListRunnable.run();
            } else {
                if (this.updating) {
                    return;
                }
                this.updating = true;
                if (!this.dialogsItemAnimator.isRunning()) {
                    this.listView.setItemAnimator(null);
                }
                AndroidUtilities.runOnUIThread(this.updateListRunnable, 36L);
            }
        }
    }

    public DialogsActivity(Bundle bundle) {
        super(bundle);
        this.initialSearchType = -1;
        this.ACTION_MODE_SEARCH_DIALOGS_TAG = "search_dialogs_action_mode";
        this.isFirstTab = true;
        this.allowGlobalSearch = true;
        this.hasStories = false;
        this.hasOnlySlefStories = false;
        this.animateToHasStories = false;
        this.invalidateScrollY = true;
        this.contactsAlpha = 1.0f;
        this.undoView = new UndoView[2];
        this.movingDialogFilters = new ArrayList();
        this.actionBarDefaultPaint = new Paint();
        this.actionModeViews = new ArrayList();
        this.commentViewAnimated = false;
        this.rect = new RectF();
        this.paint = new Paint(1);
        this.textPaint = new TextPaint(1);
        this.askAboutContacts = true;
        this.floatingInterpolator = new AccelerateDecelerateInterpolator();
        this.checkPermission = true;
        this.resetDelegate = true;
        this.openedDialogId = new MessagesStorage.TopicKey();
        this.selectedDialogs = new ArrayList();
        this.notify = true;
        this.notificationsLocker = new AnimationNotificationsLocker();
        this.debugLastUpdateAction = -1;
        this.SCROLL_Y = new AnimationProperties.FloatProperty("animationValue") {
            @Override
            public Float get(DialogsActivity dialogsActivity) {
                return Float.valueOf(DialogsActivity.this.scrollYOffset);
            }

            @Override
            public void setValue(DialogsActivity dialogsActivity, float f) {
                dialogsActivity.setScrollY(f);
            }
        };
        this.SEARCH_TRANSLATION_Y = new AnimationProperties.FloatProperty("viewPagerTranslation") {
            @Override
            public Float get(View view) {
                return Float.valueOf(DialogsActivity.this.searchViewPagerTranslationY);
            }

            @Override
            public void setValue(View view, float f) {
                DialogsActivity dialogsActivity = DialogsActivity.this;
                dialogsActivity.searchViewPagerTranslationY = f;
                view.setTranslationY(dialogsActivity.panTranslationY + f);
            }
        };
        this.shiftDp = -4;
        this.commentViewPreviousTop = -1;
        this.commentViewIgnoreTopUpdate = false;
        this.scrollBarVisible = true;
        this.storiesEnabled = true;
        this.isNextButton = false;
        this.slideFragmentProgress = 1.0f;
        this.slideAmplitudeDp = 40;
    }

    static float access$4724(DialogsActivity dialogsActivity, float f) {
        float f2 = dialogsActivity.tabsYOffset - f;
        dialogsActivity.tabsYOffset = f2;
        return f2;
    }

    private void addSearchFilter(FiltersView.MediaFilterData mediaFilterData) {
        SearchViewPager searchViewPager;
        if (!this.searchIsShowed || (searchViewPager = this.searchViewPager) == null) {
            return;
        }
        ArrayList<FiltersView.MediaFilterData> currentSearchFilters = searchViewPager.getCurrentSearchFilters();
        if (!currentSearchFilters.isEmpty()) {
            for (int i = 0; i < currentSearchFilters.size(); i++) {
                if (mediaFilterData.isSameType(currentSearchFilters.get(i))) {
                    return;
                }
            }
        }
        currentSearchFilters.add(mediaFilterData);
        this.actionBar.setSearchFilter(mediaFilterData);
        this.actionBar.setSearchFieldText("");
        updateFiltersView(true, null, null, false, true);
    }

    private void askForPermissons(boolean r7) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.DialogsActivity.askForPermissons(boolean):void");
    }

    public void checkAnimationFinished() {
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                DialogsActivity.this.lambda$checkAnimationFinished$89();
            }
        }, 300L);
    }

    public boolean checkAutoscrollToStories(ViewPage viewPage) {
        FilterTabsView filterTabsView;
        if ((!this.hasStories && ((filterTabsView = this.filterTabsView) == null || filterTabsView.getVisibility() != 0)) || this.rightSlidingDialogContainer.hasFragment()) {
            return false;
        }
        int i = (int) (-this.scrollYOffset);
        int maxScrollYOffset = getMaxScrollYOffset();
        if (i == 0 || i == maxScrollYOffset) {
            return false;
        }
        if (i < maxScrollYOffset / 2) {
            if (!viewPage.listView.canScrollVertically(-1)) {
                return false;
            }
            viewPage.scroller.smoothScrollBy(-i);
            return true;
        }
        if (!viewPage.listView.canScrollVertically(1)) {
            return false;
        }
        viewPage.scroller.smoothScrollBy(maxScrollYOffset - i);
        return true;
    }

    private boolean checkCanWrite(long j) {
        AlertDialog.Builder builder;
        int i;
        if (this.addToGroupAlertString != null || this.initialDialogsType == 15 || !this.checkCanWrite) {
            return true;
        }
        if (DialogObject.isChatDialog(j)) {
            long j2 = -j;
            TLRPC.Chat chat = getMessagesController().getChat(Long.valueOf(j2));
            if (!ChatObject.isChannel(chat) || chat.megagroup) {
                return true;
            }
            if (!this.cantSendToChannels && ChatObject.isCanWriteToChannel(j2, this.currentAccount) && this.hasPoll != 2) {
                return true;
            }
            builder = new AlertDialog.Builder(getParentActivity());
            builder.setTitle(LocaleController.getString(R.string.SendMessageTitle));
            i = this.hasPoll == 2 ? R.string.PublicPollCantForward : R.string.ChannelCantSendMessage;
        } else {
            if (!DialogObject.isEncryptedDialog(j)) {
                return true;
            }
            if (this.hasPoll == 0 && !this.hasInvoice) {
                return true;
            }
            builder = new AlertDialog.Builder(getParentActivity());
            builder.setTitle(LocaleController.getString(R.string.SendMessageTitle));
            i = this.hasPoll != 0 ? R.string.PollCantForwardSecretChat : R.string.InvoiceCantForwardSecretChat;
        }
        builder.setMessage(LocaleController.getString(i));
        builder.setNegativeButton(LocaleController.getString(R.string.OK), null);
        showDialog(builder.create());
        return false;
    }

    public void checkListLoad(ViewPage viewPage) {
        checkListLoad(viewPage, viewPage.layoutManager.findFirstVisibleItemPosition(), viewPage.layoutManager.findLastVisibleItemPosition());
    }

    private void checkListLoad(ViewPage viewPage, int i, int i2) {
        final boolean z;
        final boolean z2;
        final boolean z3;
        final boolean z4;
        if (this.tabsAnimationInProgress || this.startedTracking) {
            return;
        }
        FilterTabsView filterTabsView = this.filterTabsView;
        if (filterTabsView != null && filterTabsView.getVisibility() == 0 && this.filterTabsView.isAnimatingIndicator()) {
            return;
        }
        int abs = Math.abs(i2 - i) + 1;
        if (i2 != -1) {
            RecyclerView.ViewHolder findViewHolderForAdapterPosition = viewPage.listView.findViewHolderForAdapterPosition(i2);
            boolean z5 = findViewHolderForAdapterPosition != null && findViewHolderForAdapterPosition.getItemViewType() == 11;
            this.floatingForceVisible = z5;
            if (z5) {
                hideFloatingButton(false);
            }
        } else {
            this.floatingForceVisible = false;
        }
        if (viewPage.dialogsType == 7 || viewPage.dialogsType == 8) {
            ArrayList<MessagesController.DialogFilter> dialogFilters = getMessagesController().getDialogFilters();
            if (viewPage.selectedType >= 0 && viewPage.selectedType < dialogFilters.size() && (dialogFilters.get(viewPage.selectedType).flags & MessagesController.DIALOG_FILTER_FLAG_EXCLUDE_ARCHIVED) == 0 && ((abs > 0 && i2 >= getDialogsArray(this.currentAccount, viewPage.dialogsType, 1, this.dialogsListFrozen).size() - 10) || (abs == 0 && !getMessagesController().isDialogsEndReached(1)))) {
                boolean z6 = !getMessagesController().isDialogsEndReached(1);
                if (z6 || !getMessagesController().isServerDialogsEndReached(1)) {
                    z = z6;
                    z2 = true;
                } else {
                    z = z6;
                    z2 = false;
                }
                if ((abs > 0 || i2 < getDialogsArray(this.currentAccount, viewPage.dialogsType, this.folderId, this.dialogsListFrozen).size() - 10) && (abs != 0 || (!(viewPage.dialogsType == 7 || viewPage.dialogsType == 8) || getMessagesController().isDialogsEndReached(this.folderId)))) {
                    z3 = false;
                    z4 = false;
                } else {
                    boolean z7 = !getMessagesController().isDialogsEndReached(this.folderId);
                    if (z7 || !getMessagesController().isServerDialogsEndReached(this.folderId)) {
                        z4 = z7;
                        z3 = true;
                    } else {
                        z4 = z7;
                        z3 = false;
                    }
                }
                if (!z3 || z2) {
                    AndroidUtilities.runOnUIThread(new Runnable() {
                        @Override
                        public final void run() {
                            DialogsActivity.this.lambda$checkListLoad$77(z3, z4, z2, z);
                        }
                    });
                }
                return;
            }
        }
        z2 = false;
        z = false;
        if (abs > 0) {
        }
        z3 = false;
        z4 = false;
        if (z3) {
        }
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                DialogsActivity.this.lambda$checkListLoad$77(z3, z4, z2, z);
            }
        });
    }

    private void checkSuggestClearDatabase() {
        if (getMessagesStorage().showClearDatabaseAlert) {
            getMessagesStorage().showClearDatabaseAlert = false;
            SuggestClearDatabaseBottomSheet.show(this);
        }
    }

    private void clearCacheHintVisible() {
        MessagesController.getGlobalMainSettings().edit().remove("cache_hint_showafter").remove("cache_hint_period").apply();
    }

    public void closeSearch() {
        if (!AndroidUtilities.isTablet()) {
            this.closeSearchFieldOnHide = true;
            return;
        }
        ActionBar actionBar = this.actionBar;
        if (actionBar != null) {
            actionBar.closeSearchField();
        }
        TLObject tLObject = this.searchObject;
        if (tLObject != null) {
            SearchViewPager searchViewPager = this.searchViewPager;
            if (searchViewPager != null) {
                searchViewPager.dialogsSearchAdapter.putRecentSearch(this.searchDialogId, tLObject);
            }
            this.searchObject = null;
        }
    }

    private void createActionMode(String str) {
        if (this.actionBar.actionModeIsExist(str)) {
            return;
        }
        ActionBarMenu createActionMode = this.actionBar.createActionMode(false, str);
        createActionMode.setBackgroundColor(0);
        createActionMode.drawBlur = false;
        NumberTextView numberTextView = new NumberTextView(createActionMode.getContext());
        this.selectedDialogsCountTextView = numberTextView;
        numberTextView.setTextSize(18);
        this.selectedDialogsCountTextView.setTypeface(AndroidUtilities.bold());
        this.selectedDialogsCountTextView.setTextColor(Theme.getColor(Theme.key_actionBarActionModeDefaultIcon));
        createActionMode.addView(this.selectedDialogsCountTextView, LayoutHelper.createLinear(0, -1, 1.0f, 72, 0, 0, 0));
        this.selectedDialogsCountTextView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public final boolean onTouch(View view, MotionEvent motionEvent) {
                boolean lambda$createActionMode$62;
                lambda$createActionMode$62 = DialogsActivity.lambda$createActionMode$62(view, motionEvent);
                return lambda$createActionMode$62;
            }
        });
        int i = R.drawable.msg_pin;
        this.pinItem = createActionMode.addItemWithWidth(100, i, AndroidUtilities.dp(54.0f));
        this.muteItem = createActionMode.addItemWithWidth(104, R.drawable.msg_mute, AndroidUtilities.dp(54.0f));
        int i2 = R.drawable.msg_archive;
        this.archive2Item = createActionMode.addItemWithWidth(107, i2, AndroidUtilities.dp(54.0f));
        this.deleteItem = createActionMode.addItemWithWidth(102, R.drawable.msg_delete, AndroidUtilities.dp(54.0f), LocaleController.getString(R.string.Delete));
        ActionBarMenuItem addItemWithWidth = createActionMode.addItemWithWidth(0, R.drawable.ic_ab_other, AndroidUtilities.dp(54.0f), LocaleController.getString(R.string.AccDescrMoreOptions));
        this.archiveItem = addItemWithWidth.addSubItem(105, i2, LocaleController.getString(R.string.Archive));
        this.pin2Item = addItemWithWidth.addSubItem(108, i, LocaleController.getString(R.string.DialogPin));
        this.addToFolderItem = addItemWithWidth.addSubItem(109, R.drawable.msg_addfolder, LocaleController.getString(R.string.FilterAddTo));
        this.removeFromFolderItem = addItemWithWidth.addSubItem(110, R.drawable.msg_removefolder, LocaleController.getString(R.string.FilterRemoveFrom));
        this.readItem = addItemWithWidth.addSubItem(101, R.drawable.msg_markread, LocaleController.getString(R.string.MarkAsRead));
        this.clearItem = addItemWithWidth.addSubItem(103, R.drawable.msg_clear, LocaleController.getString(R.string.ClearHistory));
        this.blockItem = addItemWithWidth.addSubItem(106, R.drawable.msg_block, LocaleController.getString(R.string.BlockUser));
        this.muteItem.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public final boolean onLongClick(View view) {
                boolean lambda$createActionMode$63;
                lambda$createActionMode$63 = DialogsActivity.this.lambda$createActionMode$63(view);
                return lambda$createActionMode$63;
            }
        });
        this.actionModeViews.add(this.pinItem);
        this.actionModeViews.add(this.archive2Item);
        this.actionModeViews.add(this.muteItem);
        this.actionModeViews.add(this.deleteItem);
        this.actionModeViews.add(addItemWithWidth);
        updateCounters(false);
    }

    public void createGroupForThis() {
        GroupCreateFinalActivity groupCreateFinalActivity;
        final AlertDialog alertDialog = new AlertDialog(getContext(), 3);
        TLRPC.RequestPeerType requestPeerType = this.requestPeerType;
        if (requestPeerType instanceof TLRPC.TL_requestPeerTypeBroadcast) {
            Bundle bundle = new Bundle();
            bundle.putInt("step", 0);
            Boolean bool = this.requestPeerType.has_username;
            if (bool != null) {
                bundle.putBoolean("forcePublic", bool.booleanValue());
            }
            final ChannelCreateActivity channelCreateActivity = new ChannelCreateActivity(bundle);
            channelCreateActivity.setOnFinishListener(new Utilities.Callback2() {
                @Override
                public final void run(Object obj, Object obj2) {
                    DialogsActivity.this.lambda$createGroupForThis$61(channelCreateActivity, alertDialog, (BaseFragment) obj, (Long) obj2);
                }
            });
            groupCreateFinalActivity = channelCreateActivity;
        } else {
            if (!(requestPeerType instanceof TLRPC.TL_requestPeerTypeChat)) {
                return;
            }
            Bundle bundle2 = new Bundle();
            Boolean bool2 = this.requestPeerType.bot_participant;
            bundle2.putLongArray("result", (bool2 == null || !bool2.booleanValue()) ? new long[]{getUserConfig().getClientUserId()} : new long[]{getUserConfig().getClientUserId(), this.requestPeerBotId});
            Boolean bool3 = this.requestPeerType.forum;
            bundle2.putInt("chatType", (bool3 == null || !bool3.booleanValue()) ? 4 : 5);
            bundle2.putBoolean("canToggleTopics", false);
            GroupCreateFinalActivity groupCreateFinalActivity2 = new GroupCreateFinalActivity(bundle2);
            groupCreateFinalActivity2.setDelegate(new AnonymousClass33(alertDialog));
            groupCreateFinalActivity = groupCreateFinalActivity2;
        }
        presentFragment(groupCreateFinalActivity);
    }

    public void findAndUpdateCheckBox(long j, boolean z) {
        if (this.viewPages == null) {
            return;
        }
        int i = 0;
        while (true) {
            ViewPage[] viewPageArr = this.viewPages;
            if (i >= viewPageArr.length) {
                return;
            }
            int childCount = viewPageArr[i].listView.getChildCount();
            int i2 = 0;
            while (true) {
                if (i2 < childCount) {
                    View childAt = this.viewPages[i].listView.getChildAt(i2);
                    if (childAt instanceof DialogCell) {
                        DialogCell dialogCell = (DialogCell) childAt;
                        if (dialogCell.getDialogId() == j) {
                            dialogCell.setChecked(z, true);
                            break;
                        }
                    }
                    i2++;
                }
            }
            i++;
        }
    }

    public DialogCell findArchiveDialogCell(ViewPage viewPage) {
        DialogsRecyclerView dialogsRecyclerView = viewPage.listView;
        for (int i = 0; i < dialogsRecyclerView.getChildCount(); i++) {
            View childAt = dialogsRecyclerView.getChildAt(i);
            if (childAt instanceof DialogCell) {
                DialogCell dialogCell = (DialogCell) childAt;
                if (dialogCell.isFolderCell()) {
                    return dialogCell;
                }
            }
        }
        return null;
    }

    public float getActionBarMoveFrom(boolean z) {
        float dp = this.hasStories ? 0.0f + AndroidUtilities.dp(81.0f) : 0.0f;
        if (z) {
            dp += AndroidUtilities.dp(44.0f);
        }
        DialogsHintCell dialogsHintCell = this.dialogsHintCell;
        if (dialogsHintCell != null && dialogsHintCell.getVisibility() == 0) {
            dp += this.dialogsHintCell.getMeasuredHeight();
        }
        return (this.authHintCell == null || !this.authHintCellVisible) ? dp : dp + r3.getMeasuredHeight();
    }

    public int getMaxScrollYOffset() {
        return this.hasStories ? AndroidUtilities.dp(81.0f) : ActionBar.getCurrentActionBarHeight();
    }

    private int getPinnedCount() {
        ArrayList<TLRPC.Dialog> dialogsArray = ((this.viewPages[0].dialogsType == 7 || this.viewPages[0].dialogsType == 8) && (!this.actionBar.isActionModeShowed() || this.actionBar.isActionModeShowed(null))) ? getDialogsArray(this.currentAccount, this.viewPages[0].dialogsType, this.folderId, this.dialogsListFrozen) : getMessagesController().getDialogs(this.folderId);
        int size = dialogsArray.size();
        int i = 0;
        for (int i2 = 0; i2 < size; i2++) {
            TLRPC.Dialog dialog = dialogsArray.get(i2);
            if (!(dialog instanceof TLRPC.TL_dialogFolder)) {
                if (!isDialogPinned(dialog)) {
                    if (!getMessagesController().isPromoDialog(dialog.id, false)) {
                        break;
                    }
                } else {
                    i++;
                }
            }
        }
        return i;
    }

    public StoriesController getStoriesController() {
        return getMessagesController().getStoriesController();
    }

    public boolean hasHiddenArchive() {
        return !this.onlySelect && this.initialDialogsType == 0 && this.folderId == 0 && getMessagesController().hasHiddenArchive();
    }

    public void hideActionMode(boolean z) {
        final float f;
        ?? r15;
        this.actionBar.hideActionMode();
        if (this.menuDrawable != null) {
            this.actionBar.setBackButtonContentDescription(LocaleController.getString(R.string.AccDescrOpenMenu));
        }
        this.selectedDialogs.clear();
        MenuDrawable menuDrawable = this.menuDrawable;
        if (menuDrawable != null) {
            menuDrawable.setRotation(0.0f, true);
        } else {
            BackDrawable backDrawable = this.backDrawable;
            if (backDrawable != null) {
                backDrawable.setRotation(0.0f, true);
            }
        }
        FilterTabsView filterTabsView = this.filterTabsView;
        if (filterTabsView != null) {
            filterTabsView.animateColorsTo(Theme.key_actionBarTabLine, Theme.key_actionBarTabActiveText, Theme.key_actionBarTabUnactiveText, Theme.key_actionBarTabSelector, Theme.key_actionBarDefault);
        }
        ValueAnimator valueAnimator = this.actionBarColorAnimator;
        Object obj = null;
        if (valueAnimator != null) {
            valueAnimator.cancel();
            this.actionBarColorAnimator = null;
        }
        if (this.progressToActionMode == 0.0f) {
            return;
        }
        if (this.hasStories) {
            setScrollY(-getMaxScrollYOffset());
            int i = 0;
            while (true) {
                ViewPage[] viewPageArr = this.viewPages;
                if (i >= viewPageArr.length) {
                    break;
                }
                ViewPage viewPage = viewPageArr[i];
                if (viewPage != null) {
                    viewPage.listView.cancelClickRunnables(true);
                }
                i++;
            }
            f = Math.max(0.0f, AndroidUtilities.dp(81.0f) + this.scrollYOffset);
        } else {
            f = 0.0f;
        }
        ValueAnimator ofFloat = ValueAnimator.ofFloat(this.progressToActionMode, 0.0f);
        this.actionBarColorAnimator = ofFloat;
        ofFloat.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public final void onAnimationUpdate(ValueAnimator valueAnimator2) {
                DialogsActivity.this.lambda$hideActionMode$90(f, valueAnimator2);
            }
        });
        this.actionBarColorAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animator) {
                super.onAnimationEnd(animator);
                DialogsActivity.this.actionBarColorAnimator = null;
                DialogsActivity.this.actionModeFullyShowed = false;
                DialogsActivity dialogsActivity = DialogsActivity.this;
                if (dialogsActivity.hasStories) {
                    dialogsActivity.invalidateScrollY = true;
                    DialogsActivity.this.fixScrollYAfterArchiveOpened = true;
                    DialogsActivity.this.fragmentView.invalidate();
                    DialogsActivity.this.scrollAdditionalOffset = -(AndroidUtilities.dp(81.0f) - f);
                    DialogsActivity.this.viewPages[0].setTranslationY(0.0f);
                    for (int i2 = 0; i2 < DialogsActivity.this.viewPages.length; i2++) {
                        if (DialogsActivity.this.viewPages[i2] != null) {
                            DialogsActivity.this.viewPages[i2].listView.requestLayout();
                        }
                    }
                    DialogsActivity.this.fragmentView.requestLayout();
                }
            }
        });
        this.actionBarColorAnimator.setInterpolator(CubicBezierInterpolator.DEFAULT);
        this.actionBarColorAnimator.setDuration(200L);
        this.actionBarColorAnimator.start();
        this.allowMoving = false;
        if (this.movingDialogFilters.isEmpty()) {
            r15 = 1;
        } else {
            int size = this.movingDialogFilters.size();
            int i2 = 0;
            while (i2 < size) {
                MessagesController.DialogFilter dialogFilter = (MessagesController.DialogFilter) this.movingDialogFilters.get(i2);
                FilterCreateActivity.saveFilterToServer(dialogFilter, dialogFilter.flags, dialogFilter.name, dialogFilter.color, dialogFilter.alwaysShow, dialogFilter.neverShow, dialogFilter.pinnedDialogs, false, false, true, true, false, this, null);
                i2++;
                size = size;
                obj = null;
            }
            r15 = 1;
            this.movingDialogFilters.clear();
        }
        if (this.movingWas) {
            getMessagesController().reorderPinnedDialogs(this.folderId, null, 0L);
            this.movingWas = false;
        }
        updateCounters(r15);
        if (this.viewPages != null) {
            int i3 = 0;
            while (true) {
                ViewPage[] viewPageArr2 = this.viewPages;
                if (i3 >= viewPageArr2.length) {
                    break;
                }
                viewPageArr2[i3].dialogsAdapter.onReorderStateChanged(false);
                i3 += r15;
            }
        }
        updateVisibleRows(MessagesController.UPDATE_MASK_REORDER | MessagesController.UPDATE_MASK_CHECK | (z ? MessagesController.UPDATE_MASK_CHAT : 0));
    }

    public void hideFloatingButton(boolean z) {
        HintView2 hintView2;
        if (this.rightSlidingDialogContainer.hasFragment()) {
            z = true;
        }
        if (this.floatingHidden != z) {
            if (z && this.floatingForceVisible) {
                return;
            }
            this.floatingHidden = z;
            AnimatorSet animatorSet = new AnimatorSet();
            ValueAnimator ofFloat = ValueAnimator.ofFloat(this.floatingButtonHideProgress, this.floatingHidden ? 1.0f : 0.0f);
            ofFloat.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                    DialogsActivity.this.lambda$hideFloatingButton$112(valueAnimator);
                }
            });
            animatorSet.playTogether(ofFloat);
            animatorSet.setDuration(300L);
            animatorSet.setInterpolator(this.floatingInterpolator);
            this.floatingButtonContainer.setClickable(true ^ z);
            animatorSet.start();
            if (!z || (hintView2 = this.storyHint) == null) {
                return;
            }
            hintView2.hide();
        }
    }

    public static ChatActivity highlightFoundQuote(ChatActivity chatActivity, MessageObject messageObject) {
        if (messageObject != null && messageObject.hasHighlightedWords()) {
            try {
                CharSequence charSequence = !TextUtils.isEmpty(messageObject.caption) ? messageObject.caption : messageObject.messageText;
                CharSequence highlightText = AndroidUtilities.highlightText(charSequence, messageObject.highlightedWords, (Theme.ResourcesProvider) null);
                if (highlightText instanceof SpannableStringBuilder) {
                    SpannableStringBuilder spannableStringBuilder = (SpannableStringBuilder) highlightText;
                    ForegroundColorSpanThemable[] foregroundColorSpanThemableArr = (ForegroundColorSpanThemable[]) spannableStringBuilder.getSpans(0, spannableStringBuilder.length(), ForegroundColorSpanThemable.class);
                    if (foregroundColorSpanThemableArr.length > 0) {
                        int spanStart = spannableStringBuilder.getSpanStart(foregroundColorSpanThemableArr[0]);
                        int spanEnd = spannableStringBuilder.getSpanEnd(foregroundColorSpanThemableArr[0]);
                        for (int i = 1; i < foregroundColorSpanThemableArr.length; i++) {
                            int spanStart2 = spannableStringBuilder.getSpanStart(foregroundColorSpanThemableArr[i]);
                            int spanStart3 = spannableStringBuilder.getSpanStart(foregroundColorSpanThemableArr[i]);
                            if (spanStart2 != spanEnd) {
                                if (spanStart2 > spanEnd) {
                                    for (int i2 = spanEnd; i2 <= spanStart2; i2++) {
                                        if (!Character.isWhitespace(spannableStringBuilder.charAt(i2))) {
                                            break;
                                        }
                                    }
                                }
                            }
                            spanEnd = spanStart3;
                        }
                        chatActivity.setHighlightQuote(messageObject.getRealId(), charSequence.subSequence(spanStart, spanEnd).toString(), spanStart);
                    }
                }
            } catch (Exception e) {
                FileLog.e(e);
            }
        }
        return chatActivity;
    }

    private boolean isCacheHintVisible() {
        Long l = this.cacheSize;
        if (l == null || this.deviceSize == null) {
            return false;
        }
        if (((float) l.longValue()) / ((float) this.deviceSize.longValue()) >= 0.3f) {
            return System.currentTimeMillis() > MessagesController.getGlobalMainSettings().getLong("cache_hint_showafter", 0L);
        }
        clearCacheHintVisible();
        return false;
    }

    public boolean isDialogPinned(TLRPC.Dialog dialog) {
        if (dialog == null) {
            return false;
        }
        MessagesController.DialogFilter dialogFilter = null;
        if ((this.viewPages[0].dialogsType == 7 || this.viewPages[0].dialogsType == 8) && (!this.actionBar.isActionModeShowed() || this.actionBar.isActionModeShowed(null))) {
            dialogFilter = getMessagesController().selectedDialogFilter[this.viewPages[0].dialogsType == 8 ? (char) 1 : (char) 0];
        }
        return dialogFilter != null ? dialogFilter.pinnedDialogs.indexOfKey(dialog.id) >= 0 : dialog.pinned;
    }

    public void lambda$animateContactsAlpha$113(ValueAnimator valueAnimator) {
        setContactsAlpha(((Float) valueAnimator.getAnimatedValue()).floatValue());
    }

    public static void lambda$askForPermissons$102(Activity activity, Boolean bool) {
        if (bool.booleanValue()) {
            if (PermissionRequest.canAskPermission("android.permission.POST_NOTIFICATIONS")) {
                activity.requestPermissions(new String[]{"android.permission.POST_NOTIFICATIONS"}, 1);
            } else {
                PermissionRequest.showPermissionSettings("android.permission.POST_NOTIFICATIONS");
            }
        }
    }

    public void lambda$askForPermissons$103(int i) {
        this.askAboutContacts = i != 0;
        MessagesController.getGlobalNotificationsSettings().edit().putBoolean("askAboutContacts", this.askAboutContacts).commit();
        askForPermissons(false);
    }

    public void lambda$checkAnimationFinished$89() {
        setDialogsListFrozen(false);
        updateDialogIndices();
    }

    public void lambda$checkListLoad$77(boolean z, boolean z2, boolean z3, boolean z4) {
        if (z) {
            getMessagesController().loadDialogs(this.folderId, -1, 100, z2);
        }
        if (z3) {
            getMessagesController().loadDialogs(1, -1, 100, z4);
        }
    }

    public static boolean lambda$createActionMode$62(View view, MotionEvent motionEvent) {
        return true;
    }

    public boolean lambda$createActionMode$63(View view) {
        performSelectedDialogsAction(this.selectedDialogs, 104, true, true);
        return true;
    }

    public void lambda$createGroupForThis$52(ChannelCreateActivity channelCreateActivity, BaseFragment baseFragment) {
        removeSelfFromStack();
        channelCreateActivity.removeSelfFromStack();
        baseFragment.lambda$onBackPressed$319();
    }

    public void lambda$createGroupForThis$53(Long l, final ChannelCreateActivity channelCreateActivity, final BaseFragment baseFragment, Runnable runnable) {
        showSendToBotAlert(getMessagesController().getChat(l), runnable, new Runnable() {
            @Override
            public final void run() {
                DialogsActivity.this.lambda$createGroupForThis$52(channelCreateActivity, baseFragment);
            }
        });
    }

    public static boolean lambda$createGroupForThis$54(Runnable runnable, TLRPC.TL_error tL_error) {
        runnable.run();
        return true;
    }

    public void lambda$createGroupForThis$55(AlertDialog alertDialog, Long l, final Runnable runnable) {
        alertDialog.showDelayed(150L);
        Boolean bool = this.requestPeerType.bot_participant;
        if (bool == null || !bool.booleanValue()) {
            runnable.run();
        } else {
            getMessagesController().addUserToChat(l.longValue(), getMessagesController().getUser(Long.valueOf(this.requestPeerBotId)), 0, null, this, false, runnable, new MessagesController.ErrorDelegate() {
                @Override
                public final boolean run(TLRPC.TL_error tL_error) {
                    boolean lambda$createGroupForThis$54;
                    lambda$createGroupForThis$54 = DialogsActivity.lambda$createGroupForThis$54(runnable, tL_error);
                    return lambda$createGroupForThis$54;
                }
            });
        }
    }

    public static boolean lambda$createGroupForThis$56(Runnable runnable, TLRPC.TL_error tL_error) {
        runnable.run();
        return true;
    }

    public void lambda$createGroupForThis$57(Long l, final Runnable runnable) {
        if (this.requestPeerType.bot_admin_rights == null) {
            runnable.run();
            return;
        }
        TLRPC.User user = getMessagesController().getUser(Long.valueOf(this.requestPeerBotId));
        MessagesController messagesController = getMessagesController();
        long longValue = l.longValue();
        TLRPC.RequestPeerType requestPeerType = this.requestPeerType;
        TLRPC.TL_chatAdminRights tL_chatAdminRights = requestPeerType.bot_admin_rights;
        Boolean bool = requestPeerType.bot_participant;
        messagesController.setUserAdminRole(longValue, user, tL_chatAdminRights, null, false, this, bool == null || !bool.booleanValue(), true, null, runnable, new MessagesController.ErrorDelegate() {
            @Override
            public final boolean run(TLRPC.TL_error tL_error) {
                boolean lambda$createGroupForThis$56;
                lambda$createGroupForThis$56 = DialogsActivity.lambda$createGroupForThis$56(runnable, tL_error);
                return lambda$createGroupForThis$56;
            }
        });
    }

    public static boolean lambda$createGroupForThis$58(Runnable runnable, TLRPC.TL_error tL_error) {
        runnable.run();
        return true;
    }

    public void lambda$createGroupForThis$59(Long l, final Runnable runnable) {
        if (this.requestPeerType.user_admin_rights == null) {
            runnable.run();
        } else {
            getMessagesController().setUserAdminRole(l.longValue(), getAccountInstance().getUserConfig().getCurrentUser(), ChatRightsEditActivity.rightsOR(getMessagesController().getChat(l).admin_rights, this.requestPeerType.user_admin_rights), null, true, this, false, true, null, runnable, new MessagesController.ErrorDelegate() {
                @Override
                public final boolean run(TLRPC.TL_error tL_error) {
                    boolean lambda$createGroupForThis$58;
                    lambda$createGroupForThis$58 = DialogsActivity.lambda$createGroupForThis$58(runnable, tL_error);
                    return lambda$createGroupForThis$58;
                }
            });
        }
    }

    public void lambda$createGroupForThis$60(AlertDialog alertDialog, Long l, ChannelCreateActivity channelCreateActivity, BaseFragment baseFragment, Runnable runnable) {
        alertDialog.dismiss();
        getMessagesController().loadChannelParticipants(l);
        DialogsActivityDelegate dialogsActivityDelegate = this.delegate;
        removeSelfFromStack();
        channelCreateActivity.removeSelfFromStack();
        baseFragment.lambda$onBackPressed$319();
        if (dialogsActivityDelegate != null) {
            ArrayList arrayList = new ArrayList();
            arrayList.add(MessagesStorage.TopicKey.of(-l.longValue(), 0L));
            dialogsActivityDelegate.didSelectDialogs(this, arrayList, null, false, this.notify, this.scheduleDate, null);
        }
    }

    public void lambda$createGroupForThis$61(final ChannelCreateActivity channelCreateActivity, final AlertDialog alertDialog, final BaseFragment baseFragment, final Long l) {
        Utilities.doCallbacks(new Utilities.Callback() {
            @Override
            public final void run(Object obj) {
                DialogsActivity.this.lambda$createGroupForThis$53(l, channelCreateActivity, baseFragment, (Runnable) obj);
            }
        }, new Utilities.Callback() {
            @Override
            public final void run(Object obj) {
                DialogsActivity.this.lambda$createGroupForThis$55(alertDialog, l, (Runnable) obj);
            }
        }, new Utilities.Callback() {
            @Override
            public final void run(Object obj) {
                DialogsActivity.this.lambda$createGroupForThis$57(l, (Runnable) obj);
            }
        }, new Utilities.Callback() {
            @Override
            public final void run(Object obj) {
                DialogsActivity.this.lambda$createGroupForThis$59(l, (Runnable) obj);
            }
        }, new Utilities.Callback() {
            @Override
            public final void run(Object obj) {
                DialogsActivity.this.lambda$createGroupForThis$60(alertDialog, l, channelCreateActivity, baseFragment, (Runnable) obj);
            }
        });
    }

    public void lambda$createSearchViewPager$134(View view, int i, float f, float f2) {
        ChatActivity highlightFoundQuote;
        Object object = this.searchViewPager.channelsSearchAdapter.getObject(i);
        if (object instanceof TLRPC.Chat) {
            Bundle bundle = new Bundle();
            bundle.putLong("chat_id", ((TLRPC.Chat) object).id);
            highlightFoundQuote = new ChatActivity(bundle);
            highlightFoundQuote.setNextChannels(this.searchViewPager.channelsSearchAdapter.getNextChannels(i));
        } else {
            if (!(object instanceof MessageObject)) {
                return;
            }
            MessageObject messageObject = (MessageObject) object;
            Bundle bundle2 = new Bundle();
            if (messageObject.getDialogId() >= 0) {
                bundle2.putLong("user_id", messageObject.getDialogId());
            } else {
                bundle2.putLong("chat_id", -messageObject.getDialogId());
            }
            bundle2.putInt("message_id", messageObject.getId());
            highlightFoundQuote = highlightFoundQuote(new ChatActivity(bundle2), messageObject);
        }
        presentFragment(highlightFoundQuote);
    }

    public void lambda$createSearchViewPager$135(View view, int i, float f, float f2) {
        String str;
        BaseFragment highlightFoundQuote;
        Object topPeerObject = this.searchViewPager.botsSearchAdapter.getTopPeerObject(i);
        if (topPeerObject instanceof TLRPC.User) {
            getMessagesController().openApp((TLRPC.User) topPeerObject, getClassGuid());
            return;
        }
        Object object = this.searchViewPager.botsSearchAdapter.getObject(i);
        if (object instanceof TLRPC.User) {
            highlightFoundQuote = ProfileActivity.of(((TLRPC.User) object).id);
        } else {
            if (!(object instanceof MessageObject)) {
                return;
            }
            MessageObject messageObject = (MessageObject) object;
            Bundle bundle = new Bundle();
            long dialogId = messageObject.getDialogId();
            long dialogId2 = messageObject.getDialogId();
            if (dialogId >= 0) {
                str = "user_id";
            } else {
                dialogId2 = -dialogId2;
                str = "chat_id";
            }
            bundle.putLong(str, dialogId2);
            bundle.putInt("message_id", messageObject.getId());
            highlightFoundQuote = highlightFoundQuote(new ChatActivity(bundle), messageObject);
        }
        presentFragment(highlightFoundQuote);
    }

    public void lambda$createSearchViewPager$136(View view, int i) {
        BaseFragment mediaActivity;
        String str;
        Object obj = this.searchViewPager.hashtagSearchAdapter.getItem(i).object;
        if (obj instanceof MessageObject) {
            MessageObject messageObject = (MessageObject) obj;
            Bundle bundle = new Bundle();
            long dialogId = messageObject.getDialogId();
            long dialogId2 = messageObject.getDialogId();
            if (dialogId >= 0) {
                str = "user_id";
            } else {
                dialogId2 = -dialogId2;
                str = "chat_id";
            }
            bundle.putLong(str, dialogId2);
            bundle.putInt("message_id", messageObject.getId());
            mediaActivity = highlightFoundQuote(new ChatActivity(bundle), messageObject);
        } else {
            if (!(obj instanceof StoriesController.SearchStoriesList)) {
                return;
            }
            StoriesController.SearchStoriesList searchStoriesList = (StoriesController.SearchStoriesList) obj;
            Bundle bundle2 = new Bundle();
            bundle2.putInt("type", 3);
            bundle2.putString("hashtag", searchStoriesList.query);
            bundle2.putInt("storiesCount", searchStoriesList.getCount());
            mediaActivity = new MediaActivity(bundle2, null);
        }
        presentFragment(mediaActivity);
    }

    public void lambda$createSearchViewPager$137(TLRPC.User user, DialogInterface dialogInterface, int i) {
        getMediaDataController().removeWebapp(user.id);
    }

    public boolean lambda$createSearchViewPager$138(View view, int i) {
        Object topPeerObject = this.searchViewPager.botsSearchAdapter.getTopPeerObject(i);
        if (topPeerObject instanceof TLRPC.User) {
            final TLRPC.User user = (TLRPC.User) topPeerObject;
            new AlertDialog.Builder(getContext(), this.resourceProvider).setTitle(LocaleController.getString(R.string.AppsClearSearch)).setMessage(LocaleController.formatString(R.string.AppsClearSearchAlert, "\"" + UserObject.getUserName(user) + "\"")).setNegativeButton(LocaleController.getString(R.string.Cancel), null).setPositiveButton(LocaleController.getString(R.string.Remove), new DialogInterface.OnClickListener() {
                @Override
                public final void onClick(DialogInterface dialogInterface, int i2) {
                    DialogsActivity.this.lambda$createSearchViewPager$137(user, dialogInterface, i2);
                }
            }).makeRed(-1).show();
        }
        return false;
    }

    public void lambda$createSearchViewPager$139(View view, int i, float f, float f2) {
        if (view instanceof ProfileSearchCell) {
            ProfileSearchCell profileSearchCell = (ProfileSearchCell) view;
            if (profileSearchCell.isBlocked()) {
                showPremiumBlockedToast(view, profileSearchCell.getDialogId());
                return;
            }
        }
        if (this.initialDialogsType != 10) {
            onItemClick(view, i, this.searchViewPager.dialogsSearchAdapter, f, f2);
        } else {
            SearchViewPager searchViewPager = this.searchViewPager;
            onItemLongClick(searchViewPager.searchListView, view, i, f, f2, -1, searchViewPager.dialogsSearchAdapter);
        }
    }

    public void lambda$createSearchViewPager$140(boolean z, ArrayList arrayList, ArrayList arrayList2, boolean z2) {
        updateFiltersView(z, arrayList, arrayList2, z2, true);
    }

    public void lambda$createView$10(View view) {
        INavigationLayout iNavigationLayout = this.parentLayout;
        if (iNavigationLayout != null && iNavigationLayout.isInPreviewMode()) {
            finishPreviewFragment();
            return;
        }
        Bundle bundle = new Bundle();
        bundle.putBoolean("destroyAfterSelect", true);
        presentFragment(new ContactsActivity(bundle));
    }

    public void lambda$createView$11(View view) {
        INavigationLayout iNavigationLayout = this.parentLayout;
        if (iNavigationLayout != null && iNavigationLayout.isInPreviewMode()) {
            finishPreviewFragment();
            return;
        }
        if (this.initialDialogsType == 10) {
            if (this.delegate == null || this.selectedDialogs.isEmpty()) {
                return;
            }
            ArrayList arrayList = new ArrayList();
            for (int i = 0; i < this.selectedDialogs.size(); i++) {
                arrayList.add(MessagesStorage.TopicKey.of(((Long) this.selectedDialogs.get(i)).longValue(), 0L));
            }
            this.delegate.didSelectDialogs(this, arrayList, null, false, this.notify, this.scheduleDate, null);
            return;
        }
        if (this.floatingButton.getVisibility() != 0) {
            return;
        }
        if (!this.storiesEnabled) {
            Bundle bundle = new Bundle();
            bundle.putBoolean("destroyAfterSelect", true);
            presentFragment(new ContactsActivity(bundle));
            return;
        }
        HintView2 hintView2 = this.storyHint;
        if (hintView2 != null) {
            hintView2.hide();
        }
        StoriesController.StoryLimit checkStoryLimit = MessagesController.getInstance(this.currentAccount).getStoriesController().checkStoryLimit();
        if (checkStoryLimit != null) {
            showDialog(new LimitReachedBottomSheet(this, getContext(), checkStoryLimit.getLimitReachedType(), this.currentAccount, null));
        } else {
            StoryRecorder.getInstance(getParentActivity(), this.currentAccount).closeToWhenSent(new StoryRecorder.ClosingViewProvider() {
                @Override
                public StoryRecorder.SourceView getView(long j) {
                    DialogStoriesCell dialogStoriesCell = DialogsActivity.this.dialogStoriesCell;
                    return StoryRecorder.SourceView.fromStoryCell(dialogStoriesCell != null ? dialogStoriesCell.findStoryCell(j) : null);
                }

                @Override
                public void preLayout(long j, final Runnable runnable) {
                    DialogsActivity dialogsActivity = DialogsActivity.this;
                    if (dialogsActivity.dialogStoriesCell == null) {
                        runnable.run();
                        return;
                    }
                    dialogsActivity.scrollToTop(false, true);
                    DialogsActivity.this.invalidateScrollY = true;
                    DialogsActivity.this.fragmentView.invalidate();
                    if (j == 0 || j == DialogsActivity.this.getUserConfig().getClientUserId()) {
                        DialogsActivity.this.dialogStoriesCell.scrollToFirstCell();
                    } else {
                        DialogsActivity.this.dialogStoriesCell.scrollTo(j);
                    }
                    DialogsActivity.this.viewPages[0].listView.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                        @Override
                        public boolean onPreDraw() {
                            DialogsActivity.this.viewPages[0].listView.getViewTreeObserver().removeOnPreDrawListener(this);
                            AndroidUtilities.runOnUIThread(runnable, 100L);
                            return false;
                        }
                    });
                }
            }).open(StoryRecorder.SourceView.fromFloatingButton(this.floatingButtonContainer), true);
        }
    }

    public void lambda$createView$12() {
        MessagesController.getInstance(this.currentAccount).getMainSettings().edit().putBoolean("storyhint", false).commit();
    }

    public void lambda$createView$13(Long l) {
        this.cacheSize = l;
        lambda$updateDialogsHint$29();
    }

    public void lambda$createView$14(Long l, Long l2) {
        this.deviceSize = l;
        lambda$updateDialogsHint$29();
    }

    public void lambda$createView$15(View view) {
        if (this.delegate == null || this.selectedDialogs.isEmpty()) {
            return;
        }
        ArrayList arrayList = new ArrayList();
        for (int i = 0; i < this.selectedDialogs.size(); i++) {
            arrayList.add(MessagesStorage.TopicKey.of(((Long) this.selectedDialogs.get(i)).longValue(), 0L));
        }
        this.delegate.didSelectDialogs(this, arrayList, this.commentView.getFieldText(), false, this.notify, this.scheduleDate, null);
    }

    public boolean lambda$createView$16(FrameLayout frameLayout, View view) {
        if (this.isNextButton) {
            return false;
        }
        onSendLongClick(frameLayout);
        return true;
    }

    public void lambda$createView$17(View view) {
        if (SharedConfig.isAppUpdateAvailable()) {
            AndroidUtilities.openForView(SharedConfig.pendingAppUpdate.document, true, getParentActivity());
        }
    }

    public void lambda$createView$18(View view) {
        finishPreviewFragment();
    }

    public void lambda$createView$5(View view) {
        this.filterTabsView.setIsEditing(false);
        showDoneItem(false);
    }

    public void lambda$createView$6(View view) {
        getContactsController().loadGlobalPrivacySetting();
        this.optionsItem.toggleSubMenu();
    }

    public void lambda$createView$7() {
        if (this.initialDialogsType != 10) {
            hideFloatingButton(false);
        }
        if (this.hasOnlySlefStories && getStoriesController().hasOnlySelfStories()) {
            this.dialogStoriesCell.openSelfStories();
        } else {
            scrollToTop(true, true);
        }
    }

    public void lambda$createView$8(ViewPage viewPage, View view, int i, float f, float f2) {
        boolean z = view instanceof DialogCell;
        if (z) {
            DialogCell dialogCell = (DialogCell) view;
            if (dialogCell.isBlocked()) {
                showPremiumBlockedToast(view, dialogCell.getDialogId());
                return;
            }
        }
        int i2 = this.initialDialogsType;
        if (i2 == 15 && (view instanceof TextCell)) {
            viewPage.dialogsAdapter.onCreateGroupForThisClick();
            return;
        }
        if (i2 == 10) {
            onItemLongClick(viewPage.listView, view, i, 0.0f, 0.0f, viewPage.dialogsType, viewPage.dialogsAdapter);
            return;
        }
        if ((i2 == 11 || i2 == 13) && i == 1) {
            Bundle bundle = new Bundle();
            bundle.putBoolean("forImport", true);
            bundle.putLongArray("result", new long[]{getUserConfig().getClientUserId()});
            bundle.putInt("chatType", 4);
            String string = this.arguments.getString("importTitle");
            if (string != null) {
                bundle.putString("title", string);
            }
            GroupCreateFinalActivity groupCreateFinalActivity = new GroupCreateFinalActivity(bundle);
            groupCreateFinalActivity.setDelegate(new GroupCreateFinalActivity.GroupCreateFinalActivityDelegate() {
                @Override
                public void didFailChatCreation() {
                }

                @Override
                public void didFinishChatCreation(GroupCreateFinalActivity groupCreateFinalActivity2, long j) {
                    ArrayList arrayList = new ArrayList();
                    arrayList.add(MessagesStorage.TopicKey.of(-j, 0L));
                    DialogsActivityDelegate dialogsActivityDelegate = DialogsActivity.this.delegate;
                    if (DialogsActivity.this.closeFragment) {
                        DialogsActivity.this.removeSelfFromStack();
                    }
                    DialogsActivity dialogsActivity = DialogsActivity.this;
                    dialogsActivityDelegate.didSelectDialogs(dialogsActivity, arrayList, null, true, dialogsActivity.notify, dialogsActivity.scheduleDate, null);
                }

                @Override
                public void didStartChatCreation() {
                }
            });
            presentFragment(groupCreateFinalActivity);
            return;
        }
        if ((view instanceof DialogsHintCell) && (viewPage.dialogsType == 7 || viewPage.dialogsType == 8)) {
            TL_chatlists.TL_chatlists_chatlistUpdates chatlistUpdate = viewPage.dialogsAdapter.getChatlistUpdate();
            if (chatlistUpdate != null) {
                MessagesController.DialogFilter dialogFilter = getMessagesController().selectedDialogFilter[viewPage.dialogsType - 7];
                if (dialogFilter != null) {
                    showDialog(new FolderBottomSheet(this, dialogFilter.id, chatlistUpdate));
                    return;
                }
                return;
            }
        } else if (z && !this.actionBar.isActionModeShowed() && !this.rightSlidingDialogContainer.hasFragment()) {
            DialogCell dialogCell2 = (DialogCell) view;
            AndroidUtilities.rectTmp.set(dialogCell2.avatarImage.getImageX(), dialogCell2.avatarImage.getImageY(), dialogCell2.avatarImage.getImageX2(), dialogCell2.avatarImage.getImageY2());
        }
        onItemClick(view, i, viewPage.dialogsAdapter, f, f2);
    }

    public void lambda$createView$9(View view, int i) {
        this.filtersView.cancelClickRunnables(true);
        addSearchFilter(this.filtersView.getFilterAt(i));
    }

    public void lambda$didReceivedNotification$105(ViewPage viewPage, Object[] objArr) {
        reloadViewPageDialogs(viewPage, objArr.length > 0);
        FilterTabsView filterTabsView = this.filterTabsView;
        if (filterTabsView == null || filterTabsView.getVisibility() != 0) {
            return;
        }
        this.filterTabsView.checkTabsCounter();
    }

    public void lambda$didReceivedNotification$106(TLRPC.Chat chat, long j, boolean z, TLRPC.User user, boolean z2) {
        if (chat == null) {
            getMessagesController().deleteDialog(j, 0, z);
            if (user != null && user.bot && z2) {
                getMessagesController().blockPeer(user.id);
            }
        } else if (ChatObject.isNotInChat(chat)) {
            getMessagesController().deleteDialog(j, 0, z);
        } else {
            getMessagesController().deleteParticipantFromChat(-j, getMessagesController().getUser(Long.valueOf(getUserConfig().getClientUserId())), (TLRPC.Chat) null, z, z);
        }
        getMessagesController().checkIfFolderEmpty(this.folderId);
    }

    public void lambda$didSelectResult$114(long j, boolean z) {
        setDialogsListFrozen(true);
        ArrayList arrayList = new ArrayList();
        arrayList.add(MessagesStorage.TopicKey.of(j, 0L));
        this.delegate.didSelectDialogs(this, arrayList, null, z, this.notify, this.scheduleDate, null);
    }

    public void lambda$didSelectResult$115(AlertDialog alertDialog, TLObject tLObject, TLRPC.User user, TLRPC.Chat chat, final long j, final boolean z, TLRPC.TL_error tL_error, TLRPC.TL_messages_checkHistoryImportPeer tL_messages_checkHistoryImportPeer) {
        try {
            alertDialog.dismiss();
        } catch (Exception e) {
            FileLog.e(e);
        }
        this.checkingImportDialog = false;
        if (tLObject != null) {
            AlertsCreator.createImportDialogAlert(this, this.arguments.getString("importTitle"), ((TLRPC.TL_messages_checkedHistoryImportPeer) tLObject).confirm_text, user, chat, new Runnable() {
                @Override
                public final void run() {
                    DialogsActivity.this.lambda$didSelectResult$114(j, z);
                }
            });
        } else {
            AlertsCreator.processError(this.currentAccount, tL_error, this, tL_messages_checkHistoryImportPeer, new Object[0]);
            getNotificationCenter().lambda$postNotificationNameOnUIThread$1(NotificationCenter.historyImportProgressChanged, Long.valueOf(j), tL_messages_checkHistoryImportPeer, tL_error);
        }
    }

    public void lambda$didSelectResult$116(final AlertDialog alertDialog, final TLRPC.User user, final TLRPC.Chat chat, final long j, final boolean z, final TLRPC.TL_messages_checkHistoryImportPeer tL_messages_checkHistoryImportPeer, final TLObject tLObject, final TLRPC.TL_error tL_error) {
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                DialogsActivity.this.lambda$didSelectResult$115(alertDialog, tLObject, user, chat, j, z, tL_error, tL_messages_checkHistoryImportPeer);
            }
        });
    }

    public void lambda$didSelectResult$117(long j, int i, TopicsFragment topicsFragment, DialogInterface dialogInterface, int i2) {
        didSelectResult(j, i, false, false, topicsFragment);
    }

    public void lambda$didSelectResult$118(long j, int i, boolean z, TopicsFragment topicsFragment) {
        if (this.delegate == null) {
            lambda$onBackPressed$319();
            return;
        }
        ArrayList arrayList = new ArrayList();
        arrayList.add(MessagesStorage.TopicKey.of(j, i));
        this.delegate.didSelectDialogs(this, arrayList, null, z, this.notify, this.scheduleDate, topicsFragment);
        if (this.resetDelegate) {
            this.delegate = null;
        }
    }

    public static boolean lambda$didSelectResult$119(Runnable runnable, TLRPC.TL_error tL_error) {
        runnable.run();
        return true;
    }

    public void lambda$didSelectResult$120(long j, final Runnable runnable) {
        if (this.requestPeerType.bot_admin_rights == null) {
            runnable.run();
        } else {
            getMessagesController().setUserAdminRole(-j, getMessagesController().getUser(Long.valueOf(this.requestPeerBotId)), this.requestPeerType.bot_admin_rights, null, false, this, true, true, null, runnable, new MessagesController.ErrorDelegate() {
                @Override
                public final boolean run(TLRPC.TL_error tL_error) {
                    boolean lambda$didSelectResult$119;
                    lambda$didSelectResult$119 = DialogsActivity.lambda$didSelectResult$119(runnable, tL_error);
                    return lambda$didSelectResult$119;
                }
            });
        }
    }

    public void lambda$getCustomSlideTransition$131(ValueAnimator valueAnimator) {
        setSlideTransitionProgress(((Float) valueAnimator.getAnimatedValue()).floatValue());
    }

    public void lambda$getThemeDescriptions$128() {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.DialogsActivity.lambda$getThemeDescriptions$128():void");
    }

    public void lambda$getThemeDescriptions$129() {
        SearchViewPager searchViewPager = this.searchViewPager;
        if (searchViewPager != null) {
            ActionBarMenu actionMode = searchViewPager.getActionMode();
            if (actionMode != null) {
                actionMode.setBackgroundColor(getThemedColor(Theme.key_actionBarActionModeDefault));
            }
            ActionBarMenuItem speedItem = this.searchViewPager.getSpeedItem();
            if (speedItem != null) {
                speedItem.getIconView().setColorFilter(new PorterDuffColorFilter(getThemedColor(Theme.key_actionBarActionModeDefaultIcon), PorterDuff.Mode.SRC_IN));
            }
        }
    }

    public void lambda$getThemeDescriptions$130() {
        this.speedItem.getIconView().setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_actionBarActionModeDefaultIcon), PorterDuff.Mode.SRC_IN));
        this.speedItem.setBackground(Theme.createSelectorDrawable(Theme.getColor(Theme.key_actionBarActionModeDefaultSelector)));
    }

    public void lambda$hideActionMode$90(float f, ValueAnimator valueAnimator) {
        if (this.hasStories) {
            this.viewPages[0].setTranslationY(f * (1.0f - this.progressToActionMode));
        }
        this.progressToActionMode = ((Float) valueAnimator.getAnimatedValue()).floatValue();
        for (int i = 0; i < this.actionBar.getChildCount(); i++) {
            if (this.actionBar.getChildAt(i).getVisibility() == 0 && this.actionBar.getChildAt(i) != this.actionBar.getActionMode() && this.actionBar.getChildAt(i) != this.actionBar.getBackButton()) {
                this.actionBar.getChildAt(i).setAlpha(1.0f - this.progressToActionMode);
            }
        }
        View view = this.fragmentView;
        if (view != null) {
            view.invalidate();
        }
    }

    public void lambda$hideFloatingButton$112(ValueAnimator valueAnimator) {
        this.floatingButtonHideProgress = ((Float) valueAnimator.getAnimatedValue()).floatValue();
        this.floatingButtonTranslation = AndroidUtilities.dp(100.0f) * this.floatingButtonHideProgress;
        updateFloatingButtonOffset();
    }

    public static void lambda$loadDialogs$1(AccountInstance accountInstance) {
        accountInstance.getDownloadController().loadDownloadingFiles();
    }

    public void lambda$onArchiveLongPress$79(DialogInterface dialogInterface, int i) {
        if (i == 0) {
            getMessagesStorage().readAllDialogs(1);
            return;
        }
        if (i != 1 || this.viewPages == null) {
            return;
        }
        int i2 = 0;
        while (true) {
            ViewPage[] viewPageArr = this.viewPages;
            if (i2 >= viewPageArr.length) {
                return;
            }
            if (viewPageArr[i2].dialogsType == 0 && this.viewPages[i2].getVisibility() == 0) {
                this.viewPages[i2].listView.toggleArchiveHidden(true, findArchiveDialogCell(this.viewPages[i2]));
            }
            i2++;
        }
    }

    public void lambda$onGetDebugItems$132() {
        getMessagesStorage().clearLocalDatabase();
        Toast.makeText(getContext(), LocaleController.getString(R.string.DebugClearLocalDatabaseSuccess), 0).show();
    }

    public void lambda$onGetDebugItems$133() {
        getMessagesController().clearSendAsPeers();
    }

    public void lambda$onItemLongClick$78(long j, DialogInterface dialogInterface, int i) {
        this.searchViewPager.dialogsSearchAdapter.removeRecentSearch(j);
    }

    public void lambda$onRequestPermissionsResultFragment$104() {
        getNotificationCenter().lambda$postNotificationNameOnUIThread$1(NotificationCenter.forceImportContactsStart, new Object[0]);
    }

    public static void lambda$onResume$64(Activity activity, Boolean bool) {
        if (bool.booleanValue()) {
            if (PermissionRequest.canAskPermission("android.permission.POST_NOTIFICATIONS")) {
                activity.requestPermissions(new String[]{"android.permission.POST_NOTIFICATIONS"}, 1);
            } else {
                PermissionRequest.showPermissionSettings("android.permission.POST_NOTIFICATIONS");
            }
        }
    }

    public void lambda$onResume$65(final Activity activity, Boolean bool) {
        if (bool.booleanValue()) {
            return;
        }
        showDialog(new NotificationPermissionDialog(activity, !PermissionRequest.canAskPermission("android.permission.POST_NOTIFICATIONS"), new Utilities.Callback() {
            @Override
            public final void run(Object obj) {
                DialogsActivity.lambda$onResume$64(activity, (Boolean) obj);
            }
        }));
    }

    public void lambda$onResume$66(int i) {
        this.askAboutContacts = i != 0;
        MessagesController.getGlobalNotificationsSettings().edit().putBoolean("askAboutContacts", this.askAboutContacts).apply();
        askForPermissons(false);
    }

    public void lambda$onResume$67(boolean z, boolean z2, boolean z3, final Activity activity) {
        boolean shouldShowRequestPermissionRationale;
        AlertDialog createPermissionErrorAlert;
        boolean shouldShowRequestPermissionRationale2;
        if (getParentActivity() == null) {
            return;
        }
        this.afterSignup = false;
        if (z || z2 || z3) {
            this.askingForPermissions = true;
            if (z && NotificationPermissionDialog.shouldAsk(activity)) {
                PermissionRequest.requestPermission("android.permission.POST_NOTIFICATIONS", new Utilities.Callback() {
                    @Override
                    public final void run(Object obj) {
                        DialogsActivity.this.lambda$onResume$65(activity, (Boolean) obj);
                    }
                });
                return;
            }
            if (z2 && this.askAboutContacts && getUserConfig().syncContacts) {
                shouldShowRequestPermissionRationale2 = activity.shouldShowRequestPermissionRationale("android.permission.READ_CONTACTS");
                if (shouldShowRequestPermissionRationale2) {
                    createPermissionErrorAlert = AlertsCreator.createContactsPermissionDialog(activity, new MessagesStorage.IntCallback() {
                        @Override
                        public final void run(int i) {
                            DialogsActivity.this.lambda$onResume$66(i);
                        }
                    }).create();
                    this.permissionDialog = createPermissionErrorAlert;
                    showDialog(createPermissionErrorAlert);
                    return;
                }
            }
            if (z3) {
                shouldShowRequestPermissionRationale = activity.shouldShowRequestPermissionRationale("android.permission.WRITE_EXTERNAL_STORAGE");
                if (shouldShowRequestPermissionRationale) {
                    if (activity instanceof BasePermissionsActivity) {
                        createPermissionErrorAlert = ((BasePermissionsActivity) activity).createPermissionErrorAlert(R.raw.permission_request_folder, LocaleController.getString(R.string.PermissionStorageWithHint));
                        this.permissionDialog = createPermissionErrorAlert;
                        showDialog(createPermissionErrorAlert);
                        return;
                    }
                    return;
                }
            }
            askForPermissons(true);
        }
    }

    public void lambda$onResume$68(DialogInterface dialogInterface, int i) {
        Intent permissionManagerIntent = XiaomiUtilities.getPermissionManagerIntent();
        if (permissionManagerIntent != null) {
            try {
                try {
                    getParentActivity().startActivity(permissionManagerIntent);
                } catch (Exception unused) {
                    Intent intent = new Intent("android.settings.APPLICATION_DETAILS_SETTINGS");
                    intent.setData(Uri.parse("package:" + ApplicationLoader.applicationContext.getPackageName()));
                    getParentActivity().startActivity(intent);
                }
            } catch (Exception e) {
                FileLog.e(e);
            }
        }
    }

    public static void lambda$onResume$69(DialogInterface dialogInterface, int i) {
        MessagesController.getGlobalNotificationsSettings().edit().putBoolean("askedAboutMiuiLockscreen", true).commit();
    }

    public void lambda$onResume$70(DialogInterface dialogInterface, int i) {
        Intent intent = new Intent("android.settings.MANAGE_APP_USE_FULL_SCREEN_INTENT");
        intent.setData(Uri.parse("package:" + ApplicationLoader.applicationContext.getPackageName()));
        try {
            getParentActivity().startActivity(intent);
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    public static void lambda$onResume$71(DialogInterface dialogInterface, int i) {
        MessagesController.getGlobalNotificationsSettings().edit().putBoolean("askedAboutFSILockscreen", true).commit();
    }

    public void lambda$onSendLongClick$125(KeyEvent keyEvent) {
        ActionBarPopupWindow actionBarPopupWindow;
        if (keyEvent.getKeyCode() == 4 && keyEvent.getRepeatCount() == 0 && (actionBarPopupWindow = this.sendPopupWindow) != null && actionBarPopupWindow.isShowing()) {
            this.sendPopupWindow.dismiss();
        }
    }

    public void lambda$onSendLongClick$126(View view) {
        ActionBarPopupWindow actionBarPopupWindow = this.sendPopupWindow;
        if (actionBarPopupWindow != null && actionBarPopupWindow.isShowing()) {
            this.sendPopupWindow.dismiss();
        }
        this.notify = false;
        if (this.delegate == null || this.selectedDialogs.isEmpty()) {
            return;
        }
        ArrayList arrayList = new ArrayList();
        for (int i = 0; i < this.selectedDialogs.size(); i++) {
            arrayList.add(MessagesStorage.TopicKey.of(((Long) this.selectedDialogs.get(i)).longValue(), 0L));
        }
        this.delegate.didSelectDialogs(this, arrayList, this.commentView.getFieldText(), false, this.notify, this.scheduleDate, null);
    }

    public void lambda$onSendLongClick$127(Activity activity, boolean z, Theme.ResourcesProvider resourcesProvider, View view) {
        ActionBarPopupWindow actionBarPopupWindow = this.sendPopupWindow;
        if (actionBarPopupWindow != null && actionBarPopupWindow.isShowing()) {
            this.sendPopupWindow.dismiss();
        }
        AlertsCreator.createScheduleDatePickerDialog(activity, z ? getUserConfig().getClientUserId() : -1L, new AlertsCreator.ScheduleDatePickerDelegate() {
            @Override
            public void didSelectDate(boolean z2, int i) {
                DialogsActivity dialogsActivity = DialogsActivity.this;
                dialogsActivity.scheduleDate = i;
                if (dialogsActivity.delegate == null || DialogsActivity.this.selectedDialogs.isEmpty()) {
                    return;
                }
                ArrayList arrayList = new ArrayList();
                for (int i2 = 0; i2 < DialogsActivity.this.selectedDialogs.size(); i2++) {
                    arrayList.add(MessagesStorage.TopicKey.of(((Long) DialogsActivity.this.selectedDialogs.get(i2)).longValue(), 0L));
                }
                DialogsActivityDelegate dialogsActivityDelegate = DialogsActivity.this.delegate;
                DialogsActivity dialogsActivity2 = DialogsActivity.this;
                dialogsActivityDelegate.didSelectDialogs(dialogsActivity2, arrayList, dialogsActivity2.commentView.getFieldText(), false, z2, i, null);
            }
        }, resourcesProvider);
    }

    public void lambda$performSelectedDialogsAction$91(ArrayList arrayList) {
        getMessagesController().addDialogToFolder(arrayList, this.folderId == 0 ? 0 : 1, -1, null, 0L);
    }

    public void lambda$performSelectedDialogsAction$92(int i, ArrayList arrayList, boolean z, HashSet hashSet) {
        if (i != 102) {
            performSelectedDialogsAction(arrayList, i, false, false);
            return;
        }
        getMessagesController().setDialogsInTransaction(true);
        performSelectedDialogsAction(arrayList, i, false, false, z ? hashSet : null);
        getMessagesController().setDialogsInTransaction(false);
        getMessagesController().checkIfFolderEmpty(this.folderId);
        if (this.folderId == 0 || getDialogsArray(this.currentAccount, this.viewPages[0].dialogsType, this.folderId, false).size() != 0) {
            return;
        }
        this.viewPages[0].listView.setEmptyView(null);
        this.viewPages[0].progressView.setVisibility(4);
        lambda$onBackPressed$319();
    }

    public void lambda$performSelectedDialogsAction$93(ArrayList arrayList, final int i, final HashSet hashSet, final boolean z) {
        if (arrayList.isEmpty()) {
            return;
        }
        final ArrayList arrayList2 = new ArrayList(arrayList);
        UndoView undoView = getUndoView();
        if (undoView != null) {
            undoView.showWithAction(arrayList2, i == 102 ? 27 : 26, (Object) null, (Object) null, new Runnable() {
                @Override
                public final void run() {
                    DialogsActivity.this.lambda$performSelectedDialogsAction$92(i, arrayList2, z, hashSet);
                }
            }, (Runnable) null);
        }
        hideActionMode(i == 103);
    }

    public void lambda$performSelectedDialogsAction$94(ArrayList arrayList, boolean z, boolean z2) {
        int size = arrayList.size();
        for (int i = 0; i < size; i++) {
            Long l = (Long) arrayList.get(i);
            long longValue = l.longValue();
            if (z) {
                getMessagesController().reportSpam(longValue, getMessagesController().getUser(l), null, null, false);
            }
            if (z2) {
                getMessagesController().deleteDialog(longValue, 0, true);
            }
            getMessagesController().blockPeer(longValue);
        }
        hideActionMode(false);
    }

    public void lambda$performSelectedDialogsAction$95(DialogInterface dialogInterface, int i) {
        getMessagesController().hidePromoDialog();
        hideActionMode(false);
    }

    public void lambda$performSelectedDialogsAction$97(final int i, final TLRPC.Chat chat, final long j, final boolean z, final boolean z2) {
        int i2;
        int i3;
        int i4;
        int i5;
        ArrayList arrayList;
        int i6;
        hideActionMode(false);
        if (i == 103 && ChatObject.isChannel(chat)) {
            if (!chat.megagroup || ChatObject.isPublic(chat)) {
                getMessagesController().deleteDialog(j, 2, z2);
                return;
            }
        }
        if (i == 102 && this.folderId != 0 && getDialogsArray(this.currentAccount, this.viewPages[0].dialogsType, this.folderId, false).size() == 1) {
            this.viewPages[0].progressView.setVisibility(4);
        }
        this.debugLastUpdateAction = 3;
        if (i == 102) {
            setDialogsListFrozen(true);
            if (this.frozenDialogsList != null) {
                i6 = 0;
                while (i6 < this.frozenDialogsList.size()) {
                    if (((TLRPC.Dialog) this.frozenDialogsList.get(i6)).id == j) {
                        break;
                    } else {
                        i6++;
                    }
                }
            }
            i6 = -1;
            checkAnimationFinished();
            i2 = i6;
        } else {
            i2 = -1;
        }
        UndoView undoView = getUndoView();
        if (undoView != null) {
            i3 = i2;
            undoView.showWithAction(j, i == 103 ? 0 : 1, new Runnable() {
                @Override
                public final void run() {
                    DialogsActivity.this.lambda$performSelectedDialogsAction$96(i, j, chat, z, z2);
                }
            });
        } else {
            i3 = i2;
        }
        ArrayList arrayList2 = new ArrayList(getDialogsArray(this.currentAccount, this.viewPages[0].dialogsType, this.folderId, false));
        int i7 = 0;
        while (true) {
            if (i7 >= arrayList2.size()) {
                i4 = 102;
                i5 = -1;
                break;
            } else {
                if (((TLRPC.Dialog) arrayList2.get(i7)).id == j) {
                    i5 = i7;
                    i4 = 102;
                    break;
                }
                i7++;
            }
        }
        if (i == i4) {
            int i8 = i3;
            if (i8 < 0 || i5 >= 0 || (arrayList = this.frozenDialogsList) == null) {
                setDialogsListFrozen(false);
                return;
            }
            arrayList.remove(i8);
            this.viewPages[0].dialogsItemAnimator.prepareForRemove();
            this.viewPages[0].updateList(true);
        }
    }

    public void lambda$performSelectedDialogsAction$98(DialogInterface dialogInterface) {
        hideActionMode(true);
    }

    public void lambda$performSelectedDialogsAction$99(DialogInterface dialogInterface) {
        hideActionMode(true);
    }

    public void lambda$pinDialog$100() {
        setDialogsListFrozen(false);
    }

    public void lambda$setDialogsListFrozen$111() {
        this.viewPages[0].dialogsAdapter.notifyDataSetChanged();
    }

    public void lambda$setStoriesOvercroll$19() {
        this.fragmentView.dispatchTouchEvent(AndroidUtilities.emptyMotionEvent());
    }

    public void lambda$showArchiveHelp$72() {
        presentFragment(new ArchiveSettingsActivity());
    }

    public void lambda$showArchiveHelp$73(BottomSheet[] bottomSheetArr) {
        BottomSheet bottomSheet = bottomSheetArr[0];
        if (bottomSheet != null) {
            bottomSheet.dismiss();
            bottomSheetArr[0] = null;
        }
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                DialogsActivity.this.lambda$showArchiveHelp$72();
            }
        }, 300L);
    }

    public static void lambda$showArchiveHelp$74(BottomSheet[] bottomSheetArr) {
        BottomSheet bottomSheet = bottomSheetArr[0];
        if (bottomSheet != null) {
            bottomSheet.dismiss();
            bottomSheetArr[0] = null;
        }
    }

    public void lambda$showChatPreview$80(boolean z, ArrayList arrayList, MessagesController.DialogFilter dialogFilter, long j, View view) {
        UndoView undoView;
        Integer valueOf;
        Runnable runnable;
        Runnable runnable2;
        int i;
        if (z) {
            dialogFilter.alwaysShow.remove(Long.valueOf(j));
            dialogFilter.neverShow.add(Long.valueOf(j));
            FilterCreateActivity.saveFilterToServer(dialogFilter, dialogFilter.flags, dialogFilter.name, dialogFilter.color, dialogFilter.alwaysShow, dialogFilter.neverShow, dialogFilter.pinnedDialogs, false, false, true, true, false, this, null);
            undoView = getUndoView();
            valueOf = Integer.valueOf(arrayList.size());
            runnable = null;
            runnable2 = null;
            i = 21;
        } else {
            if (!arrayList.isEmpty()) {
                for (int i2 = 0; i2 < arrayList.size(); i2++) {
                    dialogFilter.neverShow.remove(arrayList.get(i2));
                }
                dialogFilter.alwaysShow.addAll(arrayList);
                FilterCreateActivity.saveFilterToServer(dialogFilter, dialogFilter.flags, dialogFilter.name, dialogFilter.color, dialogFilter.alwaysShow, dialogFilter.neverShow, dialogFilter.pinnedDialogs, false, false, true, true, false, this, null);
            }
            undoView = getUndoView();
            valueOf = Integer.valueOf(arrayList.size());
            runnable = null;
            runnable2 = null;
            i = 20;
        }
        undoView.showWithAction(j, i, valueOf, dialogFilter, runnable, runnable2);
        hideActionMode(true);
        finishPreviewFragment();
    }

    public static void lambda$showChatPreview$81(ActionBarPopupWindow.ActionBarPopupWindowLayout[] actionBarPopupWindowLayoutArr, View view) {
        ActionBarPopupWindow.ActionBarPopupWindowLayout actionBarPopupWindowLayout = actionBarPopupWindowLayoutArr[0];
        if (actionBarPopupWindowLayout != null) {
            actionBarPopupWindowLayout.getSwipeBack().closeForeground();
        }
    }

    public static void lambda$showChatPreview$82(ActionBarPopupWindow.ActionBarPopupWindowLayout[] actionBarPopupWindowLayoutArr, int[] iArr, View view) {
        actionBarPopupWindowLayoutArr[0].getSwipeBack().openForeground(iArr[0]);
    }

    public static void lambda$showChatPreview$83(ChatActivity[] chatActivityArr, int i) {
        ChatActivity chatActivity = chatActivityArr[0];
        if (chatActivity == null || chatActivity.getFragmentView() == null || !chatActivityArr[0].isInPreviewMode()) {
            return;
        }
        ViewGroup.LayoutParams layoutParams = chatActivityArr[0].getFragmentView().getLayoutParams();
        if (layoutParams instanceof ViewGroup.MarginLayoutParams) {
            ((ViewGroup.MarginLayoutParams) layoutParams).bottomMargin = AndroidUtilities.dp(48.0f) + i;
            chatActivityArr[0].getFragmentView().setLayoutParams(layoutParams);
        }
    }

    public void lambda$showChatPreview$84(DialogCell dialogCell, long j, View view) {
        if (dialogCell.getHasUnread()) {
            markAsRead(j);
        } else {
            markAsUnread(j);
        }
        finishPreviewFragment();
    }

    public void lambda$showChatPreview$85(MessagesController.DialogFilter dialogFilter, TLRPC.Dialog dialog, long j) {
        int i;
        ArrayList<TLRPC.InputDialogPeer> arrayList;
        ArrayList<Long> arrayList2;
        Long valueOf;
        int i2 = Integer.MAX_VALUE;
        if (dialogFilter == null || !isDialogPinned(dialog)) {
            i = Integer.MAX_VALUE;
        } else {
            int size = dialogFilter.pinnedDialogs.size();
            for (int i3 = 0; i3 < size; i3++) {
                i2 = Math.min(i2, dialogFilter.pinnedDialogs.valueAt(i3));
            }
            i = i2 - this.canPinCount;
        }
        TLRPC.EncryptedChat encryptedChat = DialogObject.isEncryptedDialog(j) ? getMessagesController().getEncryptedChat(Integer.valueOf(DialogObject.getEncryptedChatId(j))) : null;
        UndoView undoView = getUndoView();
        if (undoView == null) {
            return;
        }
        if (isDialogPinned(dialog)) {
            pinDialog(j, false, dialogFilter, i, true);
            undoView.showWithAction(0L, 79, (Object) 1, (Object) 1600, (Runnable) null, (Runnable) null);
        } else {
            pinDialog(j, true, dialogFilter, i, true);
            undoView.showWithAction(0L, 78, (Object) 1, (Object) 1600, (Runnable) null, (Runnable) null);
            if (dialogFilter != null) {
                ArrayList<Long> arrayList3 = dialogFilter.alwaysShow;
                if (encryptedChat != null) {
                    if (!arrayList3.contains(Long.valueOf(encryptedChat.user_id))) {
                        arrayList2 = dialogFilter.alwaysShow;
                        valueOf = Long.valueOf(encryptedChat.user_id);
                        arrayList2.add(valueOf);
                    }
                } else if (!arrayList3.contains(Long.valueOf(j))) {
                    arrayList2 = dialogFilter.alwaysShow;
                    valueOf = Long.valueOf(j);
                    arrayList2.add(valueOf);
                }
            }
        }
        if (dialogFilter != null) {
            arrayList = null;
            FilterCreateActivity.saveFilterToServer(dialogFilter, dialogFilter.flags, dialogFilter.name, dialogFilter.color, dialogFilter.alwaysShow, dialogFilter.neverShow, dialogFilter.pinnedDialogs, false, false, true, true, false, this, null);
        } else {
            arrayList = null;
        }
        getMessagesController().reorderPinnedDialogs(this.folderId, arrayList, 0L);
        updateCounters(true);
        if (this.viewPages != null) {
            int i4 = 0;
            while (true) {
                ViewPage[] viewPageArr = this.viewPages;
                if (i4 >= viewPageArr.length) {
                    break;
                }
                viewPageArr[i4].dialogsAdapter.onReorderStateChanged(false);
                i4++;
            }
        }
        updateVisibleRows(MessagesController.UPDATE_MASK_REORDER | MessagesController.UPDATE_MASK_CHECK);
    }

    public void lambda$showChatPreview$86(final MessagesController.DialogFilter dialogFilter, final TLRPC.Dialog dialog, final long j, View view) {
        finishPreviewFragment();
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                DialogsActivity.this.lambda$showChatPreview$85(dialogFilter, dialog, j);
            }
        }, 100L);
    }

    public void lambda$showChatPreview$87(long j, View view) {
        boolean isDialogMuted = getMessagesController().isDialogMuted(j, 0L);
        if (isDialogMuted) {
            getNotificationsController().setDialogNotificationsSettings(j, 0L, 4);
        } else {
            getNotificationsController().setDialogNotificationsSettings(j, 0L, 3);
        }
        BulletinFactory.createMuteBulletin(this, !isDialogMuted, null).show();
        finishPreviewFragment();
    }

    public void lambda$showChatPreview$88(ArrayList arrayList, View view) {
        performSelectedDialogsAction(arrayList, 102, false, false);
        finishPreviewFragment();
    }

    public void lambda$showFiltersHint$109() {
        presentFragment(new FiltersSetupActivity());
    }

    public void lambda$showFiltersHint$110() {
        UndoView undoView = getUndoView();
        if (undoView != null) {
            undoView.showWithAction(0L, 15, null, new Runnable() {
                @Override
                public final void run() {
                    DialogsActivity.this.lambda$showFiltersHint$109();
                }
            });
        }
    }

    public void lambda$showOrUpdateActionMode$101(float f, ValueAnimator valueAnimator) {
        float floatValue = ((Float) valueAnimator.getAnimatedValue()).floatValue();
        this.progressToActionMode = floatValue;
        if (this.hasStories) {
            this.viewPages[0].setTranslationY((-f) * floatValue);
        }
        for (int i = 0; i < this.actionBar.getChildCount(); i++) {
            if (this.actionBar.getChildAt(i).getVisibility() == 0 && this.actionBar.getChildAt(i) != this.actionBar.getActionMode() && this.actionBar.getChildAt(i) != this.actionBar.getBackButton()) {
                this.actionBar.getChildAt(i).setAlpha(1.0f - this.progressToActionMode);
            }
        }
        View view = this.fragmentView;
        if (view != null) {
            view.invalidate();
        }
    }

    public void lambda$showPremiumBlockedToast$23() {
        if (LaunchActivity.getLastFragment() != null) {
            presentFragment(new PremiumPreviewFragment("noncontacts"));
        }
    }

    public void lambda$showSearch$75(ValueAnimator valueAnimator) {
        setSearchAnimationProgress(((Float) valueAnimator.getAnimatedValue()).floatValue(), false);
    }

    public static void lambda$showSendToBotAlert$122(Runnable runnable, DialogInterface dialogInterface, int i) {
        if (runnable != null) {
            runnable.run();
        }
    }

    public static void lambda$showSendToBotAlert$124(Runnable runnable, DialogInterface dialogInterface, int i) {
        if (runnable != null) {
            runnable.run();
        }
    }

    public void lambda$showSuggestion$107(DialogInterface dialogInterface, int i) {
        presentFragment(new PrivacySettingsActivity());
        AndroidUtilities.scrollToFragmentRow(this.parentLayout, "newChatsRow");
    }

    public void lambda$showSuggestion$108(DialogInterface dialogInterface) {
        onSuggestionDismiss();
    }

    public static float lambda$static$0(float f) {
        float f2 = f - 1.0f;
        return (f2 * f2 * f2 * f2 * f2) + 1.0f;
    }

    public void lambda$toggleArciveForStory$20(long j, boolean z) {
        getMessagesController().getStoriesController().toggleHidden(j, !z, false, true);
    }

    public void lambda$toggleArciveForStory$21(long j, boolean z) {
        getMessagesController().getStoriesController().toggleHidden(j, z, true, true);
    }

    public void lambda$toggleArciveForStory$22(final long j, final boolean z) {
        String str;
        TLRPC.Chat chat;
        getMessagesController().getStoriesController().toggleHidden(j, z, false, true);
        BulletinFactory.UndoObject undoObject = new BulletinFactory.UndoObject();
        undoObject.onUndo = new Runnable() {
            @Override
            public final void run() {
                DialogsActivity.this.lambda$toggleArciveForStory$20(j, z);
            }
        };
        undoObject.onAction = new Runnable() {
            @Override
            public final void run() {
                DialogsActivity.this.lambda$toggleArciveForStory$21(j, z);
            }
        };
        MessagesController messagesController = getMessagesController();
        if (j >= 0) {
            TLRPC.User user = messagesController.getUser(Long.valueOf(j));
            str = ContactsController.formatName(user.first_name, null, 15);
            chat = user;
        } else {
            TLRPC.Chat chat2 = messagesController.getChat(Long.valueOf(-j));
            str = chat2.title;
            chat = chat2;
        }
        this.storiesBulletin = BulletinFactory.global().createUsersBulletin(Collections.singletonList(chat), AndroidUtilities.replaceTags(isArchive() ? LocaleController.formatString("StoriesMovedToDialogs", R.string.StoriesMovedToDialogs, str) : LocaleController.formatString("StoriesMovedToContacts", R.string.StoriesMovedToContacts, ContactsController.formatName(str, null, 15))), null, undoObject).show();
    }

    public void lambda$updateAuthHintCellVisibility$25(float f, ValueAnimator valueAnimator) {
        float floatValue = ((Float) valueAnimator.getAnimatedValue()).floatValue();
        this.authHintCellProgress = floatValue;
        this.viewPages[0].listView.setTranslationY(f * floatValue);
        updateContextViewPosition();
    }

    public void lambda$updateAuthHintCellVisibility$26(final boolean z, int i, int i2, ValueAnimator valueAnimator) {
        View findViewByPosition;
        final float measuredHeight = this.authHintCell.getMeasuredHeight();
        if (!z && (findViewByPosition = this.viewPages[0].layoutManager.findViewByPosition(i)) != null) {
            measuredHeight += i2 - findViewByPosition.getTop();
        }
        this.viewPages[0].listView.setTranslationY(this.authHintCellProgress * measuredHeight);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public final void onAnimationUpdate(ValueAnimator valueAnimator2) {
                DialogsActivity.this.lambda$updateAuthHintCellVisibility$25(measuredHeight, valueAnimator2);
            }
        });
        valueAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animator) {
                DialogsActivity.this.notificationsLocker.unlock();
                DialogsActivity.this.authHintCellAnimating = false;
                DialogsActivity.this.authHintCellProgress = z ? 1.0f : 0.0f;
                View view = DialogsActivity.this.fragmentView;
                if (view != null) {
                    view.requestLayout();
                }
                DialogsActivity.this.viewPages[0].listView.requestLayout();
                DialogsActivity.this.viewPages[0].listView.setTranslationY(0.0f);
                if (z) {
                    return;
                }
                DialogsActivity.this.authHintCell.setVisibility(8);
            }
        });
        valueAnimator.setDuration(250L);
        valueAnimator.setInterpolator(CubicBezierInterpolator.DEFAULT);
        valueAnimator.start();
    }

    public void lambda$updateDialogsHint$27(View view) {
        Browser.openUrl(getContext(), getMessagesController().premiumManageSubscriptionUrl);
    }

    public void lambda$updateDialogsHint$28(View view) {
        MessagesController.getInstance(this.currentAccount).removeSuggestion(0L, "PREMIUM_GRACE");
        ChangeBounds changeBounds = new ChangeBounds();
        changeBounds.setDuration(200L);
        TransitionManager.beginDelayedTransition((ViewGroup) this.dialogsHintCell.getParent(), changeBounds);
        lambda$updateDialogsHint$29();
    }

    public void lambda$updateDialogsHint$30(long j, String str, View view) {
        new StarsIntroActivity.StarsNeededSheet(getContext(), getResourceProvider(), j, 2, str, new Runnable() {
            @Override
            public final void run() {
                DialogsActivity.this.lambda$updateDialogsHint$29();
            }
        }).show();
    }

    public void lambda$updateDialogsHint$31(View view) {
        MessagesController.getInstance(this.currentAccount).removeSuggestion(0L, "STARS_SUBSCRIPTION_LOW_BALANCE");
        ChangeBounds changeBounds = new ChangeBounds();
        changeBounds.setDuration(200L);
        TransitionManager.beginDelayedTransition((ViewGroup) this.dialogsHintCell.getParent(), changeBounds);
        lambda$updateDialogsHint$29();
    }

    public void lambda$updateDialogsHint$32(BirthdayController.BirthdayState birthdayState, View view) {
        if (birthdayState == null || birthdayState.today.size() != 1) {
            UserSelectorBottomSheet.open(0L, birthdayState);
        } else {
            showDialog(new GiftSheet(getContext(), this.currentAccount, birthdayState.today.get(0).id, null, null));
        }
    }

    public void lambda$updateDialogsHint$33(View view) {
        BirthdayController.getInstance(this.currentAccount).hide();
        MessagesController.getInstance(this.currentAccount).removeSuggestion(0L, "BIRTHDAY_CONTACTS_TODAY");
        ChangeBounds changeBounds = new ChangeBounds();
        changeBounds.setDuration(200L);
        TransitionManager.beginDelayedTransition((ViewGroup) this.dialogsHintCell.getParent(), changeBounds);
        lambda$updateDialogsHint$29();
        BulletinFactory.of(this).createSimpleBulletin(R.raw.chats_infotip, LocaleController.getString(R.string.BoostingPremiumChristmasToast), 4).setDuration(5000).show();
    }

    public void lambda$updateDialogsHint$34(TLObject tLObject, TLRPC.UserFull userFull, TLRPC.TL_birthday tL_birthday, TLRPC.TL_error tL_error) {
        Bulletin createSimpleBulletin;
        String str;
        if (tLObject instanceof TLRPC.TL_boolTrue) {
            createSimpleBulletin = BulletinFactory.of(this).createSimpleBulletin(R.raw.contact_check, LocaleController.getString(R.string.PrivacyBirthdaySetDone)).setDuration(5000);
        } else {
            if (userFull != null) {
                int i = userFull.flags2;
                userFull.flags2 = tL_birthday == null ? i & (-33) : i | 32;
                userFull.birthday = tL_birthday;
                getMessagesStorage().updateUserInfo(userFull, false);
            }
            if (tL_error != null && (str = tL_error.text) != null && str.startsWith("FLOOD_WAIT_")) {
                if (getContext() != null) {
                    showDialog(new AlertDialog.Builder(getContext(), this.resourceProvider).setTitle(LocaleController.getString(R.string.PrivacyBirthdayTooOftenTitle)).setMessage(LocaleController.getString(R.string.PrivacyBirthdayTooOftenMessage)).setPositiveButton(LocaleController.getString(R.string.OK), null).create());
                    return;
                }
                return;
            }
            createSimpleBulletin = BulletinFactory.of(this).createSimpleBulletin(R.raw.error, LocaleController.getString(R.string.UnknownError));
        }
        createSimpleBulletin.show();
    }

    public void lambda$updateDialogsHint$35(final TLRPC.UserFull userFull, final TLRPC.TL_birthday tL_birthday, final TLObject tLObject, final TLRPC.TL_error tL_error) {
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                DialogsActivity.this.lambda$updateDialogsHint$34(tLObject, userFull, tL_birthday, tL_error);
            }
        });
    }

    public void lambda$updateDialogsHint$36(TLRPC.TL_birthday tL_birthday) {
        TLRPC.TL_account_updateBirthday tL_account_updateBirthday = new TLRPC.TL_account_updateBirthday();
        tL_account_updateBirthday.flags |= 1;
        tL_account_updateBirthday.birthday = tL_birthday;
        final TLRPC.UserFull userFull = getMessagesController().getUserFull(getUserConfig().getClientUserId());
        final TLRPC.TL_birthday tL_birthday2 = userFull != null ? userFull.birthday : null;
        if (userFull != null) {
            userFull.flags2 |= 32;
            userFull.birthday = tL_birthday;
        }
        getMessagesController().invalidateContentSettings();
        getConnectionsManager().sendRequest(tL_account_updateBirthday, new RequestDelegate() {
            @Override
            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                DialogsActivity.this.lambda$updateDialogsHint$35(userFull, tL_birthday2, tLObject, tL_error);
            }
        }, 1024);
        MessagesController.getInstance(this.currentAccount).removeSuggestion(0L, "BIRTHDAY_SETUP");
        lambda$updateDialogsHint$29();
    }

    public void lambda$updateDialogsHint$37() {
        BaseFragment.BottomSheetParams bottomSheetParams = new BaseFragment.BottomSheetParams();
        bottomSheetParams.transitionFromLeft = true;
        bottomSheetParams.allowNestedScroll = false;
        showAsSheet(new PrivacyControlActivity(11), bottomSheetParams);
    }

    public void lambda$updateDialogsHint$38(View view) {
        showDialog(AlertsCreator.createBirthdayPickerDialog(getContext(), LocaleController.getString(R.string.EditProfileBirthdayTitle), LocaleController.getString(R.string.EditProfileBirthdayButton), null, new Utilities.Callback() {
            @Override
            public final void run(Object obj) {
                DialogsActivity.this.lambda$updateDialogsHint$36((TLRPC.TL_birthday) obj);
            }
        }, new Runnable() {
            @Override
            public final void run() {
                DialogsActivity.this.lambda$updateDialogsHint$37();
            }
        }, getResourceProvider()).create());
    }

    public void lambda$updateDialogsHint$39() {
        presentFragment(new UserInfoActivity());
    }

    public void lambda$updateDialogsHint$40(View view) {
        MessagesController.getInstance(this.currentAccount).removeSuggestion(0L, "BIRTHDAY_SETUP");
        ChangeBounds changeBounds = new ChangeBounds();
        changeBounds.setDuration(200L);
        TransitionManager.beginDelayedTransition((ViewGroup) this.dialogsHintCell.getParent(), changeBounds);
        lambda$updateDialogsHint$29();
        BulletinFactory.of(this).createSimpleBulletin(R.raw.chats_infotip, LocaleController.getString(R.string.BirthdaySetupLater), LocaleController.getString(R.string.Settings), new Runnable() {
            @Override
            public final void run() {
                DialogsActivity.this.lambda$updateDialogsHint$39();
            }
        }).setDuration(5000).show();
    }

    public void lambda$updateDialogsHint$42(View view) {
        MessagesController.getInstance(this.currentAccount).removeSuggestion(0L, "PREMIUM_CHRISTMAS");
        ChangeBounds changeBounds = new ChangeBounds();
        changeBounds.setDuration(200L);
        TransitionManager.beginDelayedTransition((ViewGroup) this.dialogsHintCell.getParent(), changeBounds);
        lambda$updateDialogsHint$29();
        BulletinFactory.of(this).createSimpleBulletin(R.raw.chats_infotip, LocaleController.getString(R.string.BoostingPremiumChristmasToast), 4).setDuration(5000).show();
    }

    public void lambda$updateDialogsHint$43() {
        MessagesController.getInstance(this.currentAccount).removeSuggestion(0L, "PREMIUM_RESTORE");
        lambda$updateDialogsHint$29();
    }

    public void lambda$updateDialogsHint$44(View view) {
        presentFragment(new PremiumPreviewFragment("dialogs_hint").setSelectAnnualByDefault());
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                DialogsActivity.this.lambda$updateDialogsHint$43();
            }
        }, 250L);
    }

    public void lambda$updateDialogsHint$45() {
        MessagesController.getInstance(this.currentAccount).removeSuggestion(0L, this.isPremiumHintUpgrade ? "PREMIUM_UPGRADE" : "PREMIUM_ANNUAL");
        lambda$updateDialogsHint$29();
    }

    public void lambda$updateDialogsHint$46(View view) {
        presentFragment(new PremiumPreviewFragment("dialogs_hint").setSelectAnnualByDefault());
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                DialogsActivity.this.lambda$updateDialogsHint$45();
            }
        }, 250L);
    }

    public void lambda$updateDialogsHint$47() {
        resetCacheHintVisible();
        lambda$updateDialogsHint$29();
    }

    public void lambda$updateDialogsHint$48(View view) {
        presentFragment(new CacheControlActivity());
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                DialogsActivity.this.lambda$updateDialogsHint$47();
            }
        }, 250L);
    }

    public static void lambda$updateDialogsHint$49(String str, View view) {
        ApplicationLoader applicationLoader = ApplicationLoader.applicationLoaderInstance;
        if (applicationLoader != null) {
            applicationLoader.onSuggestionClick(str);
        }
    }

    public void lambda$updateDialogsHint$50(String str) {
        MessagesController.getInstance(this.currentAccount).removeSuggestion(0L, str);
        lambda$updateDialogsHint$29();
    }

    public void lambda$updateDialogsHint$51(final String str, View view) {
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                DialogsActivity.this.lambda$updateDialogsHint$50(str);
            }
        }, 250L);
    }

    public void lambda$updateFilterTabsVisibility$76(boolean z, float f, ValueAnimator valueAnimator) {
        float floatValue = ((Float) valueAnimator.getAnimatedValue()).floatValue();
        this.filterTabsProgress = floatValue;
        if (!z && !this.hasStories) {
            setScrollY(f * floatValue);
        }
        View view = this.fragmentView;
        if (view != null) {
            view.invalidate();
        }
    }

    public void lambda$updateStatus$2(View view) {
        DialogStoriesCell dialogStoriesCell;
        if (!this.dialogStoriesCellVisible || (dialogStoriesCell = this.dialogStoriesCell) == null || dialogStoriesCell.isExpanded()) {
            showSelectStatusDialog();
        } else {
            scrollToTop(true, true);
        }
    }

    public void lambda$updateStatus$3(View view) {
        DialogStoriesCell dialogStoriesCell;
        if (!this.dialogStoriesCellVisible || (dialogStoriesCell = this.dialogStoriesCell) == null || dialogStoriesCell.isExpanded()) {
            showSelectStatusDialog();
        } else {
            scrollToTop(true, true);
        }
    }

    public static void loadDialogs(final AccountInstance accountInstance) {
        int currentAccount = accountInstance.getCurrentAccount();
        if (dialogsLoaded[currentAccount]) {
            return;
        }
        MessagesController messagesController = accountInstance.getMessagesController();
        messagesController.loadGlobalNotificationsSettings();
        messagesController.loadDialogs(0, 0, 100, true);
        messagesController.loadHintDialogs();
        messagesController.loadUserInfo(accountInstance.getUserConfig().getCurrentUser(), false, 0);
        accountInstance.getContactsController().checkInviteText();
        accountInstance.getMediaDataController().checkAllMedia(false);
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                DialogsActivity.lambda$loadDialogs$1(AccountInstance.this);
            }
        }, 200L);
        Iterator<String> it = messagesController.diceEmojies.iterator();
        while (it.hasNext()) {
            accountInstance.getMediaDataController().loadStickersByEmojiOrName(it.next(), true, true);
        }
        dialogsLoaded[currentAccount] = true;
    }

    private void markAsRead(long r17) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.DialogsActivity.markAsRead(long):void");
    }

    private void markAsUnread(long j) {
        getMessagesController().markDialogAsUnread(j, null, 0L);
    }

    public void markDialogsAsRead(ArrayList arrayList) {
        this.debugLastUpdateAction = 2;
        setDialogsListFrozen(true);
        checkAnimationFinished();
        for (int i = 0; i < arrayList.size(); i++) {
            long j = ((TLRPC.Dialog) arrayList.get(i)).id;
            TLRPC.Dialog dialog = (TLRPC.Dialog) arrayList.get(i);
            if (getMessagesController().isForum(j)) {
                getMessagesController().markAllTopicsAsRead(j);
            }
            getMessagesController().markMentionsAsRead(j, 0L);
            MessagesController messagesController = getMessagesController();
            int i2 = dialog.top_message;
            messagesController.markDialogAsRead(j, i2, i2, dialog.last_message_date, false, 0L, 0, true, 0);
        }
    }

    private boolean meetRequestPeerRequirements(TLRPC.User user) {
        Boolean bool;
        Boolean bool2;
        TLRPC.TL_requestPeerTypeUser tL_requestPeerTypeUser = (TLRPC.TL_requestPeerTypeUser) this.requestPeerType;
        return (user == null || UserObject.isReplyUser(user) || UserObject.isDeleted(user) || ((bool = tL_requestPeerTypeUser.bot) != null && bool.booleanValue() != user.bot) || ((bool2 = tL_requestPeerTypeUser.premium) != null && bool2.booleanValue() != user.premium)) ? false : true;
    }

    private boolean meetRequestPeerRequirements(TLRPC.User user, TLRPC.Chat chat) {
        if (chat == null) {
            return false;
        }
        boolean isChannelAndNotMegaGroup = ChatObject.isChannelAndNotMegaGroup(chat);
        TLRPC.RequestPeerType requestPeerType = this.requestPeerType;
        if (isChannelAndNotMegaGroup != (requestPeerType instanceof TLRPC.TL_requestPeerTypeBroadcast)) {
            return false;
        }
        Boolean bool = requestPeerType.creator;
        if (bool != null && bool.booleanValue() && !chat.creator) {
            return false;
        }
        Boolean bool2 = this.requestPeerType.bot_participant;
        if (bool2 != null && bool2.booleanValue() && !getMessagesController().isInChatCached(chat, user) && !ChatObject.canAddBotsToChat(chat)) {
            return false;
        }
        Boolean bool3 = this.requestPeerType.has_username;
        if (bool3 != null) {
            if (bool3.booleanValue() != (ChatObject.getPublicUsername(chat) != null)) {
                return false;
            }
        }
        Boolean bool4 = this.requestPeerType.forum;
        if (bool4 != null && bool4.booleanValue() != ChatObject.isForum(chat)) {
            return false;
        }
        if (this.requestPeerType.user_admin_rights == null || getMessagesController().matchesAdminRights(chat, getUserConfig().getCurrentUser(), this.requestPeerType.user_admin_rights)) {
            return this.requestPeerType.bot_admin_rights == null || getMessagesController().matchesAdminRights(chat, user, this.requestPeerType.bot_admin_rights) || ChatObject.canAddAdmins(chat);
        }
        return false;
    }

    private void onArchiveLongPress(View view) {
        view.performHapticFeedback(0, 2);
        BottomSheet.Builder builder = new BottomSheet.Builder(getParentActivity());
        boolean z = getMessagesStorage().getArchiveUnreadCount() != 0;
        builder.setItems(new CharSequence[]{z ? LocaleController.getString(R.string.MarkAllAsRead) : null, LocaleController.getString(SharedConfig.archiveHidden ? R.string.PinInTheList : R.string.HideAboveTheList)}, new int[]{z ? R.drawable.msg_markread : 0, SharedConfig.archiveHidden ? R.drawable.chats_pin : R.drawable.chats_unpin}, new DialogInterface.OnClickListener() {
            @Override
            public final void onClick(DialogInterface dialogInterface, int i) {
                DialogsActivity.this.lambda$onArchiveLongPress$79(dialogInterface, i);
            }
        });
        showDialog(builder.create());
    }

    private void onItemClick(android.view.View r20, int r21, androidx.recyclerview.widget.RecyclerView.Adapter r22, float r23, float r24) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.DialogsActivity.onItemClick(android.view.View, int, androidx.recyclerview.widget.RecyclerView$Adapter, float, float):void");
    }

    public boolean onItemLongClick(RecyclerListView recyclerListView, View view, int i, float f, float f2, int i2, RecyclerView.Adapter adapter) {
        TLRPC.Dialog dialog;
        DialogsSearchAdapter dialogsSearchAdapter;
        DialogsSearchAdapter dialogsSearchAdapter2;
        final long makeEncryptedDialogId;
        if (getParentActivity() != null && !(view instanceof DialogsHintCell)) {
            if (!this.actionBar.isActionModeShowed() && !AndroidUtilities.isTablet() && !this.onlySelect && (view instanceof DialogCell)) {
                DialogCell dialogCell = (DialogCell) view;
                if (!getMessagesController().isForum(dialogCell.getDialogId()) && !this.rightSlidingDialogContainer.hasFragment() && dialogCell.isPointInsideAvatar(f, f2)) {
                    return showChatPreview(dialogCell);
                }
            }
            RightSlidingDialogContainer rightSlidingDialogContainer = this.rightSlidingDialogContainer;
            if (rightSlidingDialogContainer != null && rightSlidingDialogContainer.hasFragment()) {
                return false;
            }
            SearchViewPager searchViewPager = this.searchViewPager;
            if (searchViewPager != null && adapter == (dialogsSearchAdapter2 = searchViewPager.dialogsSearchAdapter)) {
                Object item = dialogsSearchAdapter2.getItem(i);
                if (!this.searchViewPager.dialogsSearchAdapter.isSearchWas()) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getParentActivity());
                    builder.setTitle(LocaleController.getString(R.string.ClearSearchSingleAlertTitle));
                    if (item instanceof TLRPC.Chat) {
                        TLRPC.Chat chat = (TLRPC.Chat) item;
                        builder.setMessage(LocaleController.formatString("ClearSearchSingleChatAlertText", R.string.ClearSearchSingleChatAlertText, chat.title));
                        makeEncryptedDialogId = -chat.id;
                    } else if (item instanceof TLRPC.User) {
                        TLRPC.User user = (TLRPC.User) item;
                        builder.setMessage(user.id == getUserConfig().clientUserId ? LocaleController.formatString("ClearSearchSingleChatAlertText", R.string.ClearSearchSingleChatAlertText, LocaleController.getString(R.string.SavedMessages)) : LocaleController.formatString("ClearSearchSingleUserAlertText", R.string.ClearSearchSingleUserAlertText, ContactsController.formatName(user.first_name, user.last_name)));
                        makeEncryptedDialogId = user.id;
                    } else {
                        if (!(item instanceof TLRPC.EncryptedChat)) {
                            return false;
                        }
                        TLRPC.User user2 = getMessagesController().getUser(Long.valueOf(((TLRPC.EncryptedChat) item).user_id));
                        builder.setMessage(LocaleController.formatString("ClearSearchSingleUserAlertText", R.string.ClearSearchSingleUserAlertText, ContactsController.formatName(user2.first_name, user2.last_name)));
                        makeEncryptedDialogId = DialogObject.makeEncryptedDialogId(r0.id);
                    }
                    builder.setPositiveButton(LocaleController.getString(R.string.ClearSearchRemove), new DialogInterface.OnClickListener() {
                        @Override
                        public final void onClick(DialogInterface dialogInterface, int i3) {
                            DialogsActivity.this.lambda$onItemLongClick$78(makeEncryptedDialogId, dialogInterface, i3);
                        }
                    });
                    builder.setNegativeButton(LocaleController.getString(R.string.Cancel), null);
                    AlertDialog create = builder.create();
                    showDialog(create);
                    TextView textView = (TextView) create.getButton(-1);
                    if (textView != null) {
                        textView.setTextColor(Theme.getColor(Theme.key_text_RedBold));
                    }
                    return true;
                }
            }
            SearchViewPager searchViewPager2 = this.searchViewPager;
            if (searchViewPager2 != null && adapter == (dialogsSearchAdapter = searchViewPager2.dialogsSearchAdapter)) {
                if (this.onlySelect) {
                    onItemClick(view, i, adapter, f, f2);
                    return false;
                }
                long dialogId = (!(view instanceof ProfileSearchCell) || dialogsSearchAdapter.isGlobalSearch(i)) ? 0L : ((ProfileSearchCell) view).getDialogId();
                if (dialogId == 0) {
                    return false;
                }
                showOrUpdateActionMode(dialogId, view);
                return true;
            }
            ArrayList dialogsArray = getDialogsArray(this.currentAccount, i2, this.folderId, this.dialogsListFrozen);
            int fixPosition = ((DialogsAdapter) adapter).fixPosition(i);
            if (fixPosition < 0 || fixPosition >= dialogsArray.size() || (dialog = (TLRPC.Dialog) dialogsArray.get(fixPosition)) == null) {
                return false;
            }
            if (this.onlySelect) {
                int i3 = this.initialDialogsType;
                if ((i3 != 3 && i3 != 10) || !validateSlowModeDialog(dialog.id)) {
                    return false;
                }
                addOrRemoveSelectedDialog(dialog.id, view);
                updateSelectedCount();
                return true;
            }
            if (dialog instanceof TLRPC.TL_dialogFolder) {
                onArchiveLongPress(view);
                return false;
            }
            if (this.actionBar.isActionModeShowed() && isDialogPinned(dialog)) {
                return false;
            }
            showOrUpdateActionMode(dialog.id, view);
            return true;
        }
        return false;
    }

    private boolean onSendLongClick(View view) {
        final Activity parentActivity = getParentActivity();
        final Theme.ResourcesProvider resourceProvider = getResourceProvider();
        final boolean z = false;
        if (parentActivity == null) {
            return false;
        }
        LinearLayout linearLayout = new LinearLayout(parentActivity);
        linearLayout.setOrientation(1);
        ActionBarPopupWindow.ActionBarPopupWindowLayout actionBarPopupWindowLayout = new ActionBarPopupWindow.ActionBarPopupWindowLayout(parentActivity, resourceProvider);
        actionBarPopupWindowLayout.setAnimationEnabled(false);
        actionBarPopupWindowLayout.setOnTouchListener(new View.OnTouchListener() {
            private Rect popupRect = new Rect();

            @Override
            public boolean onTouch(View view2, MotionEvent motionEvent) {
                if (motionEvent.getActionMasked() != 0 || DialogsActivity.this.sendPopupWindow == null || !DialogsActivity.this.sendPopupWindow.isShowing()) {
                    return false;
                }
                view2.getHitRect(this.popupRect);
                if (this.popupRect.contains((int) motionEvent.getX(), (int) motionEvent.getY())) {
                    return false;
                }
                DialogsActivity.this.sendPopupWindow.dismiss();
                return false;
            }
        });
        actionBarPopupWindowLayout.setDispatchKeyEventListener(new ActionBarPopupWindow.OnDispatchKeyEventListener() {
            @Override
            public final void onDispatchKeyEvent(KeyEvent keyEvent) {
                DialogsActivity.this.lambda$onSendLongClick$125(keyEvent);
            }
        });
        actionBarPopupWindowLayout.setShownFromBottom(false);
        actionBarPopupWindowLayout.setupRadialSelectors(getThemedColor(Theme.key_dialogButtonSelector));
        ActionBarMenuSubItem actionBarMenuSubItem = new ActionBarMenuSubItem((Context) parentActivity, true, true, resourceProvider);
        actionBarMenuSubItem.setTextAndIcon(LocaleController.getString(R.string.SendWithoutSound), R.drawable.input_notify_off);
        actionBarMenuSubItem.setMinimumWidth(AndroidUtilities.dp(196.0f));
        actionBarPopupWindowLayout.addView((View) actionBarMenuSubItem, LayoutHelper.createLinear(-1, 48));
        actionBarMenuSubItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public final void onClick(View view2) {
                DialogsActivity.this.lambda$onSendLongClick$126(view2);
            }
        });
        boolean z2 = true;
        for (int i = 0; i < this.selectedDialogs.size(); i++) {
            long longValue = ((Long) this.selectedDialogs.get(i)).longValue();
            if (DialogObject.isEncryptedDialog(longValue)) {
                z2 = false;
            }
            TLRPC.Chat chat = getMessagesController().getChat(Long.valueOf(-longValue));
            if (chat != null && !ChatObject.canWriteToChat(chat)) {
                z2 = false;
            }
        }
        if (z2) {
            ActionBarMenuSubItem actionBarMenuSubItem2 = new ActionBarMenuSubItem((Context) parentActivity, true, true, resourceProvider);
            actionBarMenuSubItem2.setTextAndIcon(LocaleController.getString(R.string.ScheduleMessage), R.drawable.msg_calendar2);
            actionBarMenuSubItem2.setMinimumWidth(AndroidUtilities.dp(196.0f));
            actionBarPopupWindowLayout.addView((View) actionBarMenuSubItem2, LayoutHelper.createLinear(-1, 48));
            actionBarMenuSubItem2.setOnClickListener(new View.OnClickListener() {
                @Override
                public final void onClick(View view2) {
                    DialogsActivity.this.lambda$onSendLongClick$127(parentActivity, z, resourceProvider, view2);
                }
            });
        }
        linearLayout.addView(actionBarPopupWindowLayout, LayoutHelper.createLinear(-1, -2));
        ActionBarPopupWindow actionBarPopupWindow = new ActionBarPopupWindow(linearLayout, -2, -2);
        this.sendPopupWindow = actionBarPopupWindow;
        actionBarPopupWindow.setAnimationEnabled(false);
        this.sendPopupWindow.setAnimationStyle(R.style.PopupContextAnimation2);
        this.sendPopupWindow.setOutsideTouchable(true);
        this.sendPopupWindow.setClippingEnabled(true);
        this.sendPopupWindow.setInputMethodMode(2);
        this.sendPopupWindow.setSoftInputMode(0);
        this.sendPopupWindow.getContentView().setFocusableInTouchMode(true);
        SharedConfig.removeScheduledOrNoSoundHint();
        linearLayout.measure(View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(1000.0f), Integer.MIN_VALUE), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(1000.0f), Integer.MIN_VALUE));
        this.sendPopupWindow.setFocusable(true);
        int[] iArr = new int[2];
        view.getLocationInWindow(iArr);
        this.sendPopupWindow.showAtLocation(view, 51, ((iArr[0] + view.getMeasuredWidth()) - linearLayout.getMeasuredWidth()) + AndroidUtilities.dp(8.0f), (iArr[1] - linearLayout.getMeasuredHeight()) - AndroidUtilities.dp(2.0f));
        this.sendPopupWindow.dimBehind();
        view.performHapticFeedback(3, 2);
        return false;
    }

    private void onSuggestionDismiss() {
        if (this.showingSuggestion == null) {
            return;
        }
        getMessagesController().removeSuggestion(0L, this.showingSuggestion);
        this.showingSuggestion = null;
        showNextSupportedSuggestion();
    }

    public void lambda$performSelectedDialogsAction$96(int i, long j, TLRPC.Chat chat, boolean z, boolean z2) {
        if (i == 103) {
            getMessagesController().deleteDialog(j, 1, z2);
            return;
        }
        if (chat == null) {
            getMessagesController().deleteDialog(j, 0, z2);
            if (z && z2) {
                getMessagesController().blockPeer(j);
            }
        } else if (ChatObject.isNotInChat(chat)) {
            getMessagesController().deleteDialog(j, 0, z2);
        } else {
            getMessagesController().deleteParticipantFromChat(-j, getMessagesController().getUser(Long.valueOf(getUserConfig().getClientUserId())), (TLRPC.Chat) null, z2, false);
        }
        if (AndroidUtilities.isTablet()) {
            getNotificationCenter().lambda$postNotificationNameOnUIThread$1(NotificationCenter.closeChats, Long.valueOf(j));
        }
        getMessagesController().checkIfFolderEmpty(this.folderId);
    }

    public void performSelectedDialogsAction(ArrayList arrayList, int i, boolean z, boolean z2) {
        performSelectedDialogsAction(arrayList, i, z, z2, null);
    }

    private void performSelectedDialogsAction(final ArrayList arrayList, final int i, boolean z, boolean z2, HashSet hashSet) {
        boolean z3;
        int i2;
        long j;
        TLRPC.Chat chat;
        TLRPC.EncryptedChat encryptedChat;
        TLRPC.User user;
        int i3;
        boolean z4;
        int i4;
        ArrayList<Long> arrayList2;
        long j2;
        NotificationsController notificationsController;
        long j3;
        int i5;
        int i6;
        boolean z5;
        TLRPC.User user2;
        TLRPC.User user3;
        HashSet hashSet2 = hashSet;
        if (getParentActivity() == null) {
            return;
        }
        boolean z6 = (this.viewPages[0].dialogsType == 7 || this.viewPages[0].dialogsType == 8) && (!this.actionBar.isActionModeShowed() || this.actionBar.isActionModeShowed(null));
        MessagesController.DialogFilter dialogFilter = z6 ? getMessagesController().selectedDialogFilter[this.viewPages[0].dialogsType == 8 ? (char) 1 : (char) 0] : null;
        int size = arrayList.size();
        if (i == 105 || i == 107) {
            final ArrayList<Long> arrayList3 = new ArrayList<>(arrayList);
            getMessagesController().addDialogToFolder(arrayList3, this.canUnarchiveCount == 0 ? 1 : 0, -1, null, 0L);
            if (this.canUnarchiveCount == 0) {
                SharedPreferences globalMainSettings = MessagesController.getGlobalMainSettings();
                z3 = false;
                boolean z7 = globalMainSettings.getBoolean("archivehint_l", false) || SharedConfig.archiveHidden;
                if (z7) {
                    i2 = 1;
                } else {
                    i2 = 1;
                    globalMainSettings.edit().putBoolean("archivehint_l", true).commit();
                }
                int size2 = arrayList3.size();
                int i7 = z7 ? size2 > i2 ? 4 : 2 : size2 > i2 ? 5 : 3;
                UndoView undoView = getUndoView();
                if (undoView != null) {
                    undoView.showWithAction(0L, i7, null, new Runnable() {
                        @Override
                        public final void run() {
                            DialogsActivity.this.lambda$performSelectedDialogsAction$91(arrayList3);
                        }
                    });
                }
            } else {
                z3 = false;
                ArrayList<TLRPC.Dialog> dialogs = getMessagesController().getDialogs(this.folderId);
                if (this.viewPages != null && dialogs.isEmpty() && !this.hasStories) {
                    this.viewPages[0].listView.setEmptyView(null);
                    this.viewPages[0].progressView.setVisibility(4);
                    lambda$onBackPressed$319();
                }
            }
            hideActionMode(z3);
            return;
        }
        if ((i == 100 || i == 108) && this.canPinCount != 0) {
            ArrayList<TLRPC.Dialog> dialogs2 = getMessagesController().getDialogs(this.folderId);
            int size3 = dialogs2.size();
            int i8 = 0;
            int i9 = 0;
            for (int i10 = 0; i10 < size3; i10++) {
                TLRPC.Dialog dialog = dialogs2.get(i10);
                if (!(dialog instanceof TLRPC.TL_dialogFolder)) {
                    if (!isDialogPinned(dialog)) {
                        if (!getMessagesController().isPromoDialog(dialog.id, false)) {
                            break;
                        }
                    } else if (DialogObject.isEncryptedDialog(dialog.id)) {
                        i9++;
                    } else {
                        i8++;
                    }
                }
            }
            int i11 = 0;
            int i12 = 0;
            int i13 = 0;
            for (int i14 = 0; i14 < size; i14++) {
                Long l = (Long) arrayList.get(i14);
                int i15 = i11;
                long longValue = l.longValue();
                TLRPC.Dialog dialog2 = (TLRPC.Dialog) getMessagesController().dialogs_dict.get(longValue);
                if (dialog2 == null || isDialogPinned(dialog2)) {
                    i11 = i15;
                } else {
                    if (DialogObject.isEncryptedDialog(longValue)) {
                        i12++;
                        i11 = i15;
                    } else {
                        i11 = i15 + 1;
                    }
                    if (dialogFilter != null && dialogFilter.alwaysShow.contains(l)) {
                        i13++;
                    }
                }
            }
            int i16 = i11;
            int size4 = z6 ? 100 - dialogFilter.alwaysShow.size() : (this.folderId == 0 && dialogFilter == null) ? getUserConfig().isPremium() ? getMessagesController().dialogFiltersPinnedLimitPremium : getMessagesController().dialogFiltersPinnedLimitDefault : UserConfig.getInstance(this.currentAccount).isPremium() ? getMessagesController().maxFolderPinnedDialogsCountPremium : getMessagesController().maxFolderPinnedDialogsCountDefault;
            if (i12 + i9 > size4 || (i16 + i8) - i13 > size4) {
                if (this.folderId == 0 && dialogFilter == null) {
                    showDialog(new LimitReachedBottomSheet(this, getParentActivity(), 0, this.currentAccount, null));
                    return;
                } else {
                    AlertsCreator.showSimpleAlert(this, LocaleController.formatString("PinFolderLimitReached", R.string.PinFolderLimitReached, LocaleController.formatPluralString("Chats", size4, new Object[0])));
                    return;
                }
            }
        } else {
            if ((i == 102 || i == 103) && size > 1 && z) {
                final HashSet hashSet3 = new HashSet();
                boolean z8 = MessagesController.getInstance(this.currentAccount).canRevokePmInbox;
                long j4 = MessagesController.getInstance(this.currentAccount).revokeTimePmLimit;
                if (i == 102 && z8 && j4 == 2147483647L) {
                    Iterator it = arrayList.iterator();
                    boolean z9 = false;
                    while (it.hasNext()) {
                        Long l2 = (Long) it.next();
                        if (DialogObject.isUserDialog(l2.longValue()) || DialogObject.isEncryptedDialog(l2.longValue())) {
                            if (DialogObject.isEncryptedDialog(l2.longValue())) {
                                TLRPC.EncryptedChat encryptedChat2 = getMessagesController().getEncryptedChat(Integer.valueOf(DialogObject.getEncryptedChatId(l2.longValue())));
                                user2 = encryptedChat2 != null ? getMessagesController().getUser(Long.valueOf(encryptedChat2.user_id)) : null;
                            } else {
                                user2 = getMessagesController().getUser(l2);
                            }
                            if (user2 != null) {
                                ArrayList arrayList4 = (ArrayList) MessagesController.getInstance(this.currentAccount).dialogMessage.get(user2.id);
                                boolean z10 = (arrayList4 == null || arrayList4.size() != 1 || arrayList4.get(0) == null || ((MessageObject) arrayList4.get(0)).messageOwner == null || (!(((MessageObject) arrayList4.get(0)).messageOwner.action instanceof TLRPC.TL_messageActionUserJoined) && !(((MessageObject) arrayList4.get(0)).messageOwner.action instanceof TLRPC.TL_messageActionContactSignUp))) ? false : true;
                                if (!user2.bot && !UserObject.isDeleted(user2) && user2.id != getUserConfig().getClientUserId() && !z10) {
                                    hashSet3.add(l2);
                                    z9 = true;
                                }
                            }
                        }
                    }
                    z5 = z9;
                    i6 = 103;
                } else {
                    i6 = 103;
                    z5 = false;
                }
                AlertsCreator.createClearOrDeleteDialogsAlert(this, i == i6, i == 102, this.canClearCacheCount, size, z5, new MessagesStorage.BooleanCallback() {
                    @Override
                    public final void run(boolean z11) {
                        DialogsActivity.this.lambda$performSelectedDialogsAction$93(arrayList, i, hashSet3, z11);
                    }
                }, this.resourceProvider);
                return;
            }
            if (i == 106 && z) {
                if (size == 1) {
                    Long l3 = (Long) arrayList.get(0);
                    l3.longValue();
                    user3 = getMessagesController().getUser(l3);
                } else {
                    user3 = null;
                }
                AlertsCreator.createBlockDialogAlert(this, size, this.canReportSpamCount != 0, user3, new AlertsCreator.BlockDialogCallback() {
                    @Override
                    public final void run(boolean z11, boolean z12) {
                        DialogsActivity.this.lambda$performSelectedDialogsAction$94(arrayList, z11, z12);
                    }
                });
                return;
            }
        }
        int i17 = Integer.MAX_VALUE;
        if (dialogFilter != null && ((i == 100 || i == 108) && this.canPinCount != 0)) {
            int size5 = dialogFilter.pinnedDialogs.size();
            for (int i18 = 0; i18 < size5; i18++) {
                i17 = Math.min(i17, dialogFilter.pinnedDialogs.valueAt(i18));
            }
            i17 -= this.canPinCount;
        }
        int i19 = i17;
        int i20 = 0;
        int i21 = 0;
        while (i20 < size) {
            Long l4 = (Long) arrayList.get(i20);
            final long longValue2 = l4.longValue();
            TLRPC.Dialog dialog3 = (TLRPC.Dialog) getMessagesController().dialogs_dict.get(longValue2);
            if (dialog3 != null) {
                if (DialogObject.isEncryptedDialog(longValue2)) {
                    TLRPC.EncryptedChat encryptedChat3 = getMessagesController().getEncryptedChat(Integer.valueOf(DialogObject.getEncryptedChatId(longValue2)));
                    user = encryptedChat3 != null ? getMessagesController().getUser(Long.valueOf(encryptedChat3.user_id)) : new TLRPC.TL_userEmpty();
                    chat = null;
                    encryptedChat = encryptedChat3;
                } else if (DialogObject.isUserDialog(longValue2)) {
                    user = getMessagesController().getUser(l4);
                    encryptedChat = null;
                    chat = null;
                } else {
                    chat = getMessagesController().getChat(Long.valueOf(-longValue2));
                    encryptedChat = null;
                    user = null;
                }
                if (chat != null || user != null) {
                    if (user == null || !user.bot || MessagesController.isSupportUser(user)) {
                        i3 = 100;
                        z4 = false;
                    } else {
                        i3 = 100;
                        z4 = true;
                    }
                    if (i == i3 || i == 108) {
                        i4 = i20;
                        if (this.canPinCount != 0) {
                            if (!isDialogPinned(dialog3)) {
                                i21++;
                                TLRPC.EncryptedChat encryptedChat4 = encryptedChat;
                                pinDialog(longValue2, true, dialogFilter, i19, size == 1);
                                if (dialogFilter != null) {
                                    i19++;
                                    ArrayList<Long> arrayList5 = dialogFilter.alwaysShow;
                                    if (encryptedChat4 != null) {
                                        if (!arrayList5.contains(Long.valueOf(encryptedChat4.user_id))) {
                                            arrayList2 = dialogFilter.alwaysShow;
                                            j2 = encryptedChat4.user_id;
                                            arrayList2.add(Long.valueOf(j2));
                                        }
                                    } else if (!arrayList5.contains(Long.valueOf(dialog3.id))) {
                                        arrayList2 = dialogFilter.alwaysShow;
                                        j2 = dialog3.id;
                                        arrayList2.add(Long.valueOf(j2));
                                    }
                                }
                            }
                        } else if (isDialogPinned(dialog3)) {
                            i21++;
                            pinDialog(longValue2, false, dialogFilter, i19, size == 1);
                        }
                    } else if (i == 101) {
                        if (this.canReadCount != 0) {
                            markAsRead(longValue2);
                        } else {
                            markAsUnread(longValue2);
                        }
                    } else if (i == 102 || i == 103) {
                        if (size == 1) {
                            if (i != 102 || !this.canDeletePsaSelected) {
                                final TLRPC.Chat chat2 = chat;
                                final boolean z11 = z4;
                                AlertsCreator.createClearOrDeleteDialogAlert(this, i == 103, chat, user, DialogObject.isEncryptedDialog(dialog3.id), i == 102, new MessagesStorage.BooleanCallback() {
                                    @Override
                                    public final void run(boolean z12) {
                                        DialogsActivity.this.lambda$performSelectedDialogsAction$97(i, chat2, longValue2, z11, z12);
                                    }
                                });
                                return;
                            } else {
                                AlertDialog.Builder builder = new AlertDialog.Builder(getParentActivity());
                                builder.setTitle(LocaleController.getString(R.string.PsaHideChatAlertTitle));
                                builder.setMessage(LocaleController.getString(R.string.PsaHideChatAlertText));
                                builder.setPositiveButton(LocaleController.getString(R.string.PsaHide), new DialogInterface.OnClickListener() {
                                    @Override
                                    public final void onClick(DialogInterface dialogInterface, int i22) {
                                        DialogsActivity.this.lambda$performSelectedDialogsAction$95(dialogInterface, i22);
                                    }
                                });
                                builder.setNegativeButton(LocaleController.getString(R.string.Cancel), null);
                                showDialog(builder.create());
                                return;
                            }
                        }
                        if (getMessagesController().isPromoDialog(longValue2, true)) {
                            getMessagesController().hidePromoDialog();
                        } else if (i != 103 || this.canClearCacheCount == 0) {
                            i4 = i20;
                            lambda$performSelectedDialogsAction$96(i, longValue2, chat, z4, hashSet2 != null && hashSet2.contains(l4));
                        } else {
                            getMessagesController().deleteDialog(longValue2, 2, false);
                        }
                    } else if (i == 104) {
                        if (size == 1 && this.canMuteCount == 1) {
                            showDialog(AlertsCreator.createMuteAlert(this, longValue2, 0L, (Theme.ResourcesProvider) null), new DialogInterface.OnDismissListener() {
                                @Override
                                public final void onDismiss(DialogInterface dialogInterface) {
                                    DialogsActivity.this.lambda$performSelectedDialogsAction$98(dialogInterface);
                                }
                            });
                            return;
                        }
                        if (this.canUnmuteCount != 0) {
                            if (getMessagesController().isDialogMuted(longValue2, 0L)) {
                                notificationsController = getNotificationsController();
                                j3 = 0;
                                i5 = 4;
                                notificationsController.setDialogNotificationsSettings(longValue2, j3, i5);
                            }
                        } else if (z2) {
                            showDialog(AlertsCreator.createMuteAlert(this, arrayList, 0, (Theme.ResourcesProvider) null), new DialogInterface.OnDismissListener() {
                                @Override
                                public final void onDismiss(DialogInterface dialogInterface) {
                                    DialogsActivity.this.lambda$performSelectedDialogsAction$99(dialogInterface);
                                }
                            });
                            return;
                        } else if (!getMessagesController().isDialogMuted(longValue2, 0L)) {
                            notificationsController = getNotificationsController();
                            j3 = 0;
                            i5 = 3;
                            notificationsController.setDialogNotificationsSettings(longValue2, j3, i5);
                        }
                    }
                    i20 = i4 + 1;
                    hashSet2 = hashSet;
                }
            }
            i4 = i20;
            i20 = i4 + 1;
            hashSet2 = hashSet;
        }
        if (i == 104 && (size != 1 || this.canMuteCount != 1)) {
            BulletinFactory.createMuteBulletin(this, this.canUnmuteCount == 0, null).show();
        }
        if (i == 100 || i == 108) {
            if (dialogFilter != null) {
                FilterCreateActivity.saveFilterToServer(dialogFilter, dialogFilter.flags, dialogFilter.name, dialogFilter.color, dialogFilter.alwaysShow, dialogFilter.neverShow, dialogFilter.pinnedDialogs, false, false, true, true, false, this, null);
                j = 0;
            } else {
                j = 0;
                getMessagesController().reorderPinnedDialogs(this.folderId, null, 0L);
            }
            UndoView undoView2 = getUndoView();
            if (this.searchIsShowed && undoView2 != null) {
                undoView2.showWithAction(j, this.canPinCount != 0 ? 78 : 79, Integer.valueOf(i21));
            }
        }
        hideActionMode((i == 108 || i == 100 || i == 102) ? false : true);
    }

    private void pinDialog(long r17, boolean r19, org.telegram.messenger.MessagesController.DialogFilter r20, int r21, boolean r22) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.DialogsActivity.pinDialog(long, boolean, org.telegram.messenger.MessagesController$DialogFilter, int, boolean):void");
    }

    private void prepareBlurBitmap() {
        if (this.blurredView == null) {
            return;
        }
        int measuredWidth = (int) (this.fragmentView.getMeasuredWidth() / 6.0f);
        int measuredHeight = (int) (this.fragmentView.getMeasuredHeight() / 6.0f);
        Bitmap createBitmap = Bitmap.createBitmap(measuredWidth, measuredHeight, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(createBitmap);
        canvas.scale(0.16666667f, 0.16666667f);
        this.fragmentView.draw(canvas);
        Utilities.stackBlurBitmap(createBitmap, Math.max(7, Math.max(measuredWidth, measuredHeight) / 180));
        this.blurredView.setBackground(new BitmapDrawable(createBitmap));
        this.blurredView.setAlpha(0.0f);
        this.blurredView.setVisibility(0);
    }

    private void reloadViewPageDialogs(org.telegram.ui.DialogsActivity.ViewPage r9, boolean r10) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.DialogsActivity.reloadViewPageDialogs(org.telegram.ui.DialogsActivity$ViewPage, boolean):void");
    }

    private void resetCacheHintVisible() {
        SharedPreferences globalMainSettings = MessagesController.getGlobalMainSettings();
        long j = globalMainSettings.getLong("cache_hint_period", 604800000L);
        if (j <= 604800000) {
            j = 2592000000L;
        }
        globalMainSettings.edit().putLong("cache_hint_showafter", System.currentTimeMillis() + j).putLong("cache_hint_period", j).apply();
    }

    public void resetScroll() {
        if (this.scrollYOffset == 0.0f || this.hasStories) {
            return;
        }
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(ObjectAnimator.ofFloat(this, (Property<DialogsActivity, Float>) this.SCROLL_Y, 0.0f));
        animatorSet.setInterpolator(CubicBezierInterpolator.DEFAULT);
        animatorSet.setDuration(250L);
        animatorSet.start();
    }

    public void setDialogsListFrozen(boolean z) {
        setDialogsListFrozen(z, true);
    }

    private void setDialogsListFrozen(boolean z, boolean z2) {
        if (this.viewPages == null || this.dialogsListFrozen == z) {
            return;
        }
        this.frozenDialogsList = z ? new ArrayList(getDialogsArray(this.currentAccount, this.viewPages[0].dialogsType, this.folderId, false)) : null;
        this.dialogsListFrozen = z;
        this.viewPages[0].dialogsAdapter.setDialogsListFrozen(z);
        if (z || !z2) {
            return;
        }
        if (this.viewPages[0].listView.isComputingLayout()) {
            this.viewPages[0].listView.post(new Runnable() {
                @Override
                public final void run() {
                    DialogsActivity.this.lambda$setDialogsListFrozen$111();
                }
            });
        } else {
            this.viewPages[0].dialogsAdapter.notifyDataSetChanged();
        }
    }

    private void setFloatingProgressVisible(final boolean z, boolean z2) {
        if (this.floatingButton2 == null || this.floating2ProgressView == null) {
            return;
        }
        if (z2) {
            if (z == this.floatingProgressVisible) {
                return;
            }
            AnimatorSet animatorSet = this.floatingProgressAnimator;
            if (animatorSet != null) {
                animatorSet.cancel();
            }
            this.floatingProgressVisible = z;
            AnimatorSet animatorSet2 = new AnimatorSet();
            this.floatingProgressAnimator = animatorSet2;
            RLottieImageView rLottieImageView = this.floatingButton2;
            Property property = View.ALPHA;
            ObjectAnimator ofFloat = ObjectAnimator.ofFloat(rLottieImageView, (Property<RLottieImageView, Float>) property, z ? 0.0f : 1.0f);
            RLottieImageView rLottieImageView2 = this.floatingButton2;
            Property property2 = View.SCALE_X;
            ObjectAnimator ofFloat2 = ObjectAnimator.ofFloat(rLottieImageView2, (Property<RLottieImageView, Float>) property2, z ? 0.1f : 1.0f);
            RLottieImageView rLottieImageView3 = this.floatingButton2;
            Property property3 = View.SCALE_Y;
            animatorSet2.playTogether(ofFloat, ofFloat2, ObjectAnimator.ofFloat(rLottieImageView3, (Property<RLottieImageView, Float>) property3, z ? 0.1f : 1.0f), ObjectAnimator.ofFloat(this.floating2ProgressView, (Property<RadialProgressView, Float>) property, z ? 1.0f : 0.0f), ObjectAnimator.ofFloat(this.floating2ProgressView, (Property<RadialProgressView, Float>) property2, z ? 1.0f : 0.1f), ObjectAnimator.ofFloat(this.floating2ProgressView, (Property<RadialProgressView, Float>) property3, z ? 1.0f : 0.1f));
            this.floatingProgressAnimator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animator) {
                    View view;
                    if (animator == DialogsActivity.this.floatingProgressAnimator) {
                        if (z) {
                            if (DialogsActivity.this.floatingButton2 != null) {
                                view = DialogsActivity.this.floatingButton2;
                                view.setVisibility(8);
                            }
                            DialogsActivity.this.floatingProgressAnimator = null;
                        }
                        if (DialogsActivity.this.floatingButton2 != null) {
                            view = DialogsActivity.this.floating2ProgressView;
                            view.setVisibility(8);
                        }
                        DialogsActivity.this.floatingProgressAnimator = null;
                    }
                }

                @Override
                public void onAnimationStart(Animator animator) {
                    DialogsActivity.this.floating2ProgressView.setVisibility(0);
                    DialogsActivity.this.floatingButton2.setVisibility(0);
                }
            });
            this.floatingProgressAnimator.setDuration(150L);
            this.floatingProgressAnimator.setInterpolator(CubicBezierInterpolator.DEFAULT);
            this.floatingProgressAnimator.start();
            return;
        }
        AnimatorSet animatorSet3 = this.floatingProgressAnimator;
        if (animatorSet3 != null) {
            animatorSet3.cancel();
        }
        this.floatingProgressVisible = z;
        if (z) {
            this.floatingButton2.setAlpha(0.0f);
            this.floatingButton2.setScaleX(0.1f);
            this.floatingButton2.setScaleY(0.1f);
            this.floatingButton2.setVisibility(8);
            this.floating2ProgressView.setAlpha(1.0f);
            this.floating2ProgressView.setScaleX(1.0f);
            this.floating2ProgressView.setScaleY(1.0f);
            this.floating2ProgressView.setVisibility(0);
            return;
        }
        this.floatingButton2.setAlpha(1.0f);
        this.floatingButton2.setScaleX(1.0f);
        this.floatingButton2.setScaleY(1.0f);
        this.floatingButton2.setVisibility(0);
        this.floating2ProgressView.setAlpha(0.0f);
        this.floating2ProgressView.setScaleX(0.1f);
        this.floating2ProgressView.setScaleY(0.1f);
        this.floating2ProgressView.setVisibility(8);
    }

    private void setFragmentIsSliding(boolean z) {
        ViewPage viewPage;
        if (SharedConfig.getDevicePerformanceClass() <= 1 || !LiteMode.isEnabled(32768)) {
            return;
        }
        if (z) {
            ViewPage[] viewPageArr = this.viewPages;
            if (viewPageArr != null && (viewPage = viewPageArr[0]) != null) {
                viewPage.setLayerType(2, null);
                this.viewPages[0].setClipChildren(false);
                this.viewPages[0].setClipToPadding(false);
                this.viewPages[0].listView.setClipChildren(false);
            }
            ActionBar actionBar = this.actionBar;
            if (actionBar != null) {
                actionBar.setLayerType(2, null);
            }
            FilterTabsView filterTabsView = this.filterTabsView;
            if (filterTabsView != null) {
                filterTabsView.getListView().setLayerType(2, null);
            }
            View view = this.fragmentView;
            if (view == null) {
                return;
            } else {
                ((ViewGroup) view).setClipChildren(false);
            }
        } else {
            if (this.viewPages != null) {
                int i = 0;
                while (true) {
                    ViewPage[] viewPageArr2 = this.viewPages;
                    if (i >= viewPageArr2.length) {
                        break;
                    }
                    ViewPage viewPage2 = viewPageArr2[i];
                    if (viewPage2 != null) {
                        viewPage2.setLayerType(0, null);
                        viewPage2.setClipChildren(true);
                        viewPage2.setClipToPadding(true);
                        viewPage2.listView.setClipChildren(true);
                    }
                    i++;
                }
            }
            ActionBar actionBar2 = this.actionBar;
            if (actionBar2 != null) {
                actionBar2.setLayerType(0, null);
            }
            FilterTabsView filterTabsView2 = this.filterTabsView;
            if (filterTabsView2 != null) {
                filterTabsView2.getListView().setLayerType(0, null);
            }
            DialogStoriesCell dialogStoriesCell = this.dialogStoriesCell;
            if (dialogStoriesCell != null) {
                dialogStoriesCell.setLayerType(0, null);
            }
            View view2 = this.fragmentView;
            if (view2 == null) {
                return;
            } else {
                ((ViewGroup) view2).setClipChildren(true);
            }
        }
        this.fragmentView.requestLayout();
    }

    public void setScrollY(float f) {
        ViewPage[] viewPageArr = this.viewPages;
        if (viewPageArr != null) {
            int i = 0;
            int paddingTop = viewPageArr[0].listView.getPaddingTop() + ((int) f);
            while (true) {
                ViewPage[] viewPageArr2 = this.viewPages;
                if (i >= viewPageArr2.length) {
                    break;
                }
                viewPageArr2[i].listView.setTopGlowOffset(paddingTop);
                i++;
            }
        }
        if (this.fragmentView == null || f == this.scrollYOffset) {
            return;
        }
        this.scrollYOffset = f;
        Bulletin bulletin = this.topBulletin;
        if (bulletin != null) {
            bulletin.updatePosition();
        }
        DrawerProfileCell.AnimatedStatusView animatedStatusView = this.animatedStatusView;
        if (animatedStatusView != null) {
            animatedStatusView.translateY2((int) f);
            this.animatedStatusView.setAlpha(1.0f - ((-f) / ActionBar.getCurrentActionBarHeight()));
        }
        this.fragmentView.invalidate();
    }

    private void setSlideTransitionProgress(float f) {
        if (SharedConfig.getDevicePerformanceClass() <= 0 || this.slideFragmentProgress == f) {
            return;
        }
        this.slideFragmentLite = SharedConfig.getDevicePerformanceClass() <= 1 || !LiteMode.isEnabled(32768);
        this.slideFragmentProgress = f;
        View view = this.fragmentView;
        if (view != null) {
            view.invalidate();
        }
        if (this.slideFragmentLite) {
            FilterTabsView filterTabsView = this.filterTabsView;
            if (filterTabsView != null) {
                filterTabsView.getListView().setTranslationX((this.isDrawerTransition ? 1 : -1) * AndroidUtilities.dp(40.0f) * (1.0f - this.slideFragmentProgress));
                this.filterTabsView.invalidate();
            }
            DialogStoriesCell dialogStoriesCell = this.dialogStoriesCell;
            if (dialogStoriesCell != null) {
                dialogStoriesCell.setTranslationX((this.isDrawerTransition ? 1 : -1) * AndroidUtilities.dp(40.0f) * (1.0f - this.slideFragmentProgress));
            }
            RightSlidingDialogContainer rightSlidingDialogContainer = this.rightSlidingDialogContainer;
            if (rightSlidingDialogContainer == null || rightSlidingDialogContainer.getFragmentView() == null || this.rightFragmentTransitionInProgress) {
                return;
            }
            this.rightSlidingDialogContainer.getFragmentView().setTranslationX((this.isDrawerTransition ? 1 : -1) * AndroidUtilities.dp(40.0f) * (1.0f - this.slideFragmentProgress));
            return;
        }
        float f2 = 1.0f - ((1.0f - this.slideFragmentProgress) * 0.05f);
        FilterTabsView filterTabsView2 = this.filterTabsView;
        if (filterTabsView2 != null) {
            filterTabsView2.getListView().setScaleX(f2);
            this.filterTabsView.getListView().setScaleY(f2);
            this.filterTabsView.getListView().setTranslationX((this.isDrawerTransition ? AndroidUtilities.dp(4.0f) : -AndroidUtilities.dp(4.0f)) * (1.0f - this.slideFragmentProgress));
            this.filterTabsView.getListView().setPivotX(this.isDrawerTransition ? this.filterTabsView.getMeasuredWidth() : 0.0f);
            this.filterTabsView.getListView().setPivotY(0.0f);
            this.filterTabsView.invalidate();
        }
        DialogStoriesCell dialogStoriesCell2 = this.dialogStoriesCell;
        if (dialogStoriesCell2 != null) {
            dialogStoriesCell2.setScaleX(f2);
            this.dialogStoriesCell.setScaleY(f2);
            this.dialogStoriesCell.setTranslationX((this.isDrawerTransition ? AndroidUtilities.dp(4.0f) : -AndroidUtilities.dp(4.0f)) * (1.0f - this.slideFragmentProgress));
            this.dialogStoriesCell.setPivotX(this.isDrawerTransition ? r1.getMeasuredWidth() : 0.0f);
            this.dialogStoriesCell.setPivotY(0.0f);
        }
        RightSlidingDialogContainer rightSlidingDialogContainer2 = this.rightSlidingDialogContainer;
        if (rightSlidingDialogContainer2 == null || rightSlidingDialogContainer2.getFragmentView() == null) {
            return;
        }
        if (!this.rightFragmentTransitionInProgress) {
            this.rightSlidingDialogContainer.getFragmentView().setScaleX(f2);
            this.rightSlidingDialogContainer.getFragmentView().setScaleY(f2);
            this.rightSlidingDialogContainer.getFragmentView().setTranslationX((this.isDrawerTransition ? AndroidUtilities.dp(4.0f) : -AndroidUtilities.dp(4.0f)) * (1.0f - this.slideFragmentProgress));
        }
        this.rightSlidingDialogContainer.getFragmentView().setPivotX(this.isDrawerTransition ? this.rightSlidingDialogContainer.getMeasuredWidth() : 0.0f);
        this.rightSlidingDialogContainer.getFragmentView().setPivotY(0.0f);
    }

    public void setStoriesOvercroll(ViewPage viewPage, float f) {
        if (this.storiesOverscroll == f) {
            return;
        }
        this.storiesOverscroll = f;
        if (f == 0.0f) {
            this.storiesOverscrollCalled = false;
        }
        this.dialogStoriesCell.setOverscoll(f);
        viewPage.listView.setViewsOffset(f);
        viewPage.listView.setOverScrollMode(f != 0.0f ? 2 : 0);
        this.fragmentView.invalidate();
        if (f <= AndroidUtilities.dp(90.0f) || this.storiesOverscrollCalled) {
            return;
        }
        this.storiesOverscrollCalled = true;
        getOrCreateStoryViewer().doOnAnimationReady(new Runnable() {
            @Override
            public final void run() {
                DialogsActivity.this.lambda$setStoriesOvercroll$19();
            }
        });
        this.dialogStoriesCell.openOverscrollSelectedStory();
        this.dialogStoriesCell.performHapticFeedback(3);
    }

    public void showArchiveHelp() {
        getContactsController().loadGlobalPrivacySetting();
        BottomSheet show = new BottomSheet.Builder(getContext(), false, getResourceProvider()).setCustomView(new ArchiveHelp(getContext(), this.currentAccount, getResourceProvider(), new Runnable() {
            @Override
            public final void run() {
                DialogsActivity.this.lambda$showArchiveHelp$73(r2);
            }
        }, new Runnable() {
            @Override
            public final void run() {
                DialogsActivity.lambda$showArchiveHelp$74(r1);
            }
        }), 49).show();
        final BottomSheet[] bottomSheetArr = {show};
        show.fixNavigationBar(Theme.getColor(Theme.key_dialogBackground));
    }

    public void showDoneItem(final boolean r10) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.DialogsActivity.showDoneItem(boolean):void");
    }

    private void showFiltersHint() {
        if (this.askingForPermissions || !getMessagesController().dialogFiltersLoaded || !getMessagesController().showFiltersTooltip || this.filterTabsView == null || !getMessagesController().getDialogFilters().isEmpty() || this.isPaused || !getUserConfig().filtersLoaded || this.inPreviewMode) {
            return;
        }
        SharedPreferences globalMainSettings = MessagesController.getGlobalMainSettings();
        if (globalMainSettings.getBoolean("filterhint", false)) {
            return;
        }
        globalMainSettings.edit().putBoolean("filterhint", true).apply();
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                DialogsActivity.this.lambda$showFiltersHint$110();
            }
        }, 1000L);
    }

    private void showNextSupportedSuggestion() {
        if (this.showingSuggestion != null) {
            return;
        }
        for (String str : getMessagesController().pendingSuggestions) {
            if (showSuggestion(str)) {
                this.showingSuggestion = str;
                return;
            }
        }
    }

    private void showOrUpdateActionMode(long j, View view) {
        addOrRemoveSelectedDialog(j, view);
        boolean z = true;
        if (!this.actionBar.isActionModeShowed()) {
            if (this.searchIsShowed) {
                createActionMode("search_dialogs_action_mode");
                if (this.actionBar.getBackButton().getDrawable() instanceof MenuDrawable) {
                    this.actionBar.setBackButtonDrawable(new BackDrawable(false));
                }
            } else {
                createActionMode(null);
            }
            AndroidUtilities.hideKeyboard(this.fragmentView.findFocus());
            this.actionBar.setActionModeOverrideColor(Theme.getColor(Theme.key_windowBackgroundWhite));
            this.actionBar.showActionMode();
            if (!this.hasStories) {
                resetScroll();
            }
            if (this.menuDrawable != null) {
                this.actionBar.setBackButtonContentDescription(LocaleController.getString(R.string.AccDescrGoBack));
            }
            if (getPinnedCount() > 1) {
                if (this.viewPages != null) {
                    int i = 0;
                    while (true) {
                        ViewPage[] viewPageArr = this.viewPages;
                        if (i >= viewPageArr.length) {
                            break;
                        }
                        viewPageArr[i].dialogsAdapter.onReorderStateChanged(true);
                        i++;
                    }
                }
                updateVisibleRows(MessagesController.UPDATE_MASK_REORDER);
            }
            if (!this.searchIsShowed) {
                AnimatorSet animatorSet = new AnimatorSet();
                ArrayList arrayList = new ArrayList();
                for (int i2 = 0; i2 < this.actionModeViews.size(); i2++) {
                    View view2 = (View) this.actionModeViews.get(i2);
                    view2.setPivotY(ActionBar.getCurrentActionBarHeight() / 2);
                    AndroidUtilities.clearDrawableAnimation(view2);
                    arrayList.add(ObjectAnimator.ofFloat(view2, (Property<View, Float>) View.SCALE_Y, 0.1f, 1.0f));
                }
                animatorSet.playTogether(arrayList);
                animatorSet.setDuration(200L);
                animatorSet.start();
            }
            ValueAnimator valueAnimator = this.actionBarColorAnimator;
            if (valueAnimator != null) {
                valueAnimator.cancel();
            }
            this.actionBarColorAnimator = ValueAnimator.ofFloat(this.progressToActionMode, 1.0f);
            final float f = 0.0f;
            if (this.hasStories) {
                int i3 = 0;
                while (true) {
                    ViewPage[] viewPageArr2 = this.viewPages;
                    if (i3 >= viewPageArr2.length) {
                        break;
                    }
                    ViewPage viewPage = viewPageArr2[i3];
                    if (viewPage != null) {
                        viewPage.listView.cancelClickRunnables(true);
                    }
                    i3++;
                }
                float max = Math.max(0.0f, AndroidUtilities.dp(81.0f) + this.scrollYOffset);
                if (max != 0.0f) {
                    this.actionModeAdditionalHeight = (int) max;
                    this.fragmentView.requestLayout();
                }
                f = max;
            }
            this.actionBarColorAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public final void onAnimationUpdate(ValueAnimator valueAnimator2) {
                    DialogsActivity.this.lambda$showOrUpdateActionMode$101(f, valueAnimator2);
                }
            });
            this.actionBarColorAnimator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animator) {
                    super.onAnimationEnd(animator);
                    DialogsActivity.this.actionBarColorAnimator = null;
                    DialogsActivity.this.actionModeAdditionalHeight = 0;
                    DialogsActivity.this.actionModeFullyShowed = true;
                    DialogsActivity dialogsActivity = DialogsActivity.this;
                    if (dialogsActivity.hasStories) {
                        dialogsActivity.scrollAdditionalOffset = AndroidUtilities.dp(81.0f) - f;
                        DialogsActivity.this.viewPages[0].setTranslationY(0.0f);
                        for (int i4 = 0; i4 < DialogsActivity.this.viewPages.length; i4++) {
                            if (DialogsActivity.this.viewPages[i4] != null) {
                                DialogsActivity.this.viewPages[i4].listView.requestLayout();
                            }
                        }
                        DialogsActivity.this.dialogStoriesCell.setProgressToCollapse(1.0f, false);
                        DialogsActivity.this.fragmentView.requestLayout();
                    }
                }
            });
            this.actionBarColorAnimator.setInterpolator(CubicBezierInterpolator.DEFAULT);
            this.actionBarColorAnimator.setDuration(200L);
            this.actionBarColorAnimator.start();
            FilterTabsView filterTabsView = this.filterTabsView;
            if (filterTabsView != null) {
                filterTabsView.animateColorsTo(Theme.key_profile_tabSelectedLine, Theme.key_profile_tabSelectedText, Theme.key_profile_tabText, Theme.key_profile_tabSelector, Theme.key_actionBarActionModeDefault);
            }
            MenuDrawable menuDrawable = this.menuDrawable;
            if (menuDrawable != null) {
                menuDrawable.setRotateToBack(false);
                this.menuDrawable.setRotation(1.0f, true);
            } else {
                BackDrawable backDrawable = this.backDrawable;
                if (backDrawable != null) {
                    backDrawable.setRotation(1.0f, true);
                }
            }
            z = false;
        } else if (this.selectedDialogs.isEmpty()) {
            hideActionMode(true);
            return;
        }
        updateCounters(false);
        this.selectedDialogsCountTextView.setNumber(this.selectedDialogs.size(), z);
    }

    public void showPremiumBlockedToast(View view, long j) {
        int i = -this.shiftDp;
        this.shiftDp = i;
        AndroidUtilities.shakeViewSpring(view, i);
        BotWebViewVibrationEffect.APP_ERROR.vibrate();
        String userName = j >= 0 ? UserObject.getUserName(MessagesController.getInstance(this.currentAccount).getUser(Long.valueOf(j))) : "";
        (getMessagesController().premiumFeaturesBlocked() ? BulletinFactory.of(this).createSimpleBulletin(R.raw.star_premium_2, AndroidUtilities.replaceTags(LocaleController.formatString(R.string.UserBlockedNonPremium, userName))) : BulletinFactory.of(this).createSimpleBulletin(R.raw.star_premium_2, AndroidUtilities.replaceTags(LocaleController.formatString(R.string.UserBlockedNonPremium, userName)), LocaleController.getString(R.string.UserBlockedNonPremiumButton), new Runnable() {
            @Override
            public final void run() {
                DialogsActivity.this.lambda$showPremiumBlockedToast$23();
            }
        })).show();
    }

    public void showScrollbars(boolean z) {
        if (this.viewPages == null || this.scrollBarVisible == z) {
            return;
        }
        this.scrollBarVisible = z;
        int i = 0;
        while (true) {
            ViewPage[] viewPageArr = this.viewPages;
            if (i >= viewPageArr.length) {
                return;
            }
            if (z) {
                viewPageArr[i].listView.setScrollbarFadingEnabled(false);
            }
            this.viewPages[i].listView.setVerticalScrollBarEnabled(z);
            if (z) {
                this.viewPages[i].listView.setScrollbarFadingEnabled(true);
            }
            i++;
        }
    }

    public void showSearch(boolean z, boolean z2, boolean z3) {
        showSearch(z, z2, z3, false);
    }

    private void showSearch(final boolean r16, boolean r17, boolean r18, boolean r19) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.DialogsActivity.showSearch(boolean, boolean, boolean, boolean):void");
    }

    public void showSendToBotAlert(TLRPC.Chat chat, final Runnable runnable, final Runnable runnable2) {
        CharSequence charSequence;
        TLRPC.User user = getMessagesController().getUser(Long.valueOf(this.requestPeerBotId));
        boolean isChannelAndNotMegaGroup = ChatObject.isChannelAndNotMegaGroup(chat);
        AlertDialog.Builder title = new AlertDialog.Builder(getContext()).setTitle(LocaleController.formatString(R.string.AreYouSureSendChatToBotTitle, chat.title, UserObject.getFirstName(user)));
        SpannableStringBuilder replaceTags = AndroidUtilities.replaceTags(LocaleController.formatString(R.string.AreYouSureSendChatToBotMessage, chat.title, UserObject.getFirstName(user)));
        Boolean bool = this.requestPeerType.bot_participant;
        if ((bool == null || !bool.booleanValue() || getMessagesController().isInChatCached(chat, user)) && this.requestPeerType.bot_admin_rights == null) {
            charSequence = "";
        } else {
            charSequence = TextUtils.concat("\n\n", AndroidUtilities.replaceTags(this.requestPeerType.bot_admin_rights == null ? LocaleController.formatString(R.string.AreYouSureSendChatToBotAdd, UserObject.getFirstName(user), chat.title) : LocaleController.formatString(R.string.AreYouSureSendChatToBotAddRights, UserObject.getFirstName(user), chat.title, RequestPeerRequirementsCell.rightsToString(this.requestPeerType.bot_admin_rights, isChannelAndNotMegaGroup))));
        }
        showDialog(title.setMessage(TextUtils.concat(replaceTags, charSequence)).setPositiveButton(LocaleController.formatString("Send", R.string.Send, new Object[0]), new DialogInterface.OnClickListener() {
            @Override
            public final void onClick(DialogInterface dialogInterface, int i) {
                runnable.run();
            }
        }).setNegativeButton(LocaleController.formatString("Cancel", R.string.Cancel, new Object[0]), new DialogInterface.OnClickListener() {
            @Override
            public final void onClick(DialogInterface dialogInterface, int i) {
                DialogsActivity.lambda$showSendToBotAlert$124(runnable2, dialogInterface, i);
            }
        }).create());
    }

    private void showSendToBotAlert(TLRPC.User user, final Runnable runnable, final Runnable runnable2) {
        TLRPC.User user2 = getMessagesController().getUser(Long.valueOf(this.requestPeerBotId));
        showDialog(new AlertDialog.Builder(getContext()).setTitle(LocaleController.formatString(R.string.AreYouSureSendChatToBotTitle, UserObject.getFirstName(user), UserObject.getFirstName(user2))).setMessage(TextUtils.concat(AndroidUtilities.replaceTags(LocaleController.formatString(R.string.AreYouSureSendChatToBotMessage, UserObject.getFirstName(user), UserObject.getFirstName(user2))))).setPositiveButton(LocaleController.formatString("Send", R.string.Send, new Object[0]), new DialogInterface.OnClickListener() {
            @Override
            public final void onClick(DialogInterface dialogInterface, int i) {
                runnable.run();
            }
        }).setNegativeButton(LocaleController.formatString("Cancel", R.string.Cancel, new Object[0]), new DialogInterface.OnClickListener() {
            @Override
            public final void onClick(DialogInterface dialogInterface, int i) {
                DialogsActivity.lambda$showSendToBotAlert$122(runnable2, dialogInterface, i);
            }
        }).create());
    }

    private boolean showSuggestion(String str) {
        if (!"AUTOARCHIVE_POPULAR".equals(str)) {
            return false;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(getParentActivity());
        builder.setTitle(LocaleController.getString(R.string.HideNewChatsAlertTitle));
        builder.setMessage(AndroidUtilities.replaceTags(LocaleController.getString(R.string.HideNewChatsAlertText)));
        builder.setNegativeButton(LocaleController.getString(R.string.Cancel), null);
        builder.setPositiveButton(LocaleController.getString(R.string.GoToSettings), new DialogInterface.OnClickListener() {
            @Override
            public final void onClick(DialogInterface dialogInterface, int i) {
                DialogsActivity.this.lambda$showSuggestion$107(dialogInterface, i);
            }
        });
        showDialog(builder.create(), new DialogInterface.OnDismissListener() {
            @Override
            public final void onDismiss(DialogInterface dialogInterface) {
                DialogsActivity.this.lambda$showSuggestion$108(dialogInterface);
            }
        });
        return true;
    }

    public void toggleArciveForStory(final long j) {
        final boolean z = !isArchive();
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                DialogsActivity.this.lambda$toggleArciveForStory$22(j, z);
            }
        }, 200L);
    }

    private void updateAppUpdateViews(boolean z) {
        boolean z2;
        AnimatorSet animatorSet;
        AnimatorListenerAdapter animatorListenerAdapter;
        if (this.updateLayout == null) {
            return;
        }
        if (SharedConfig.isAppUpdateAvailable()) {
            FileLoader.getAttachFileName(SharedConfig.pendingAppUpdate.document);
            z2 = getFileLoader().getPathToAttach(SharedConfig.pendingAppUpdate.document, true).exists();
        } else {
            z2 = false;
        }
        if (z2) {
            if (this.updateLayout.getTag() != null) {
                return;
            }
            AnimatorSet animatorSet2 = this.updateLayoutAnimator;
            if (animatorSet2 != null) {
                animatorSet2.cancel();
            }
            this.updateLayout.setVisibility(0);
            this.updateLayout.setTag(1);
            if (!z) {
                this.updateLayout.setTranslationY(0.0f);
                return;
            }
            AnimatorSet animatorSet3 = new AnimatorSet();
            this.updateLayoutAnimator = animatorSet3;
            animatorSet3.setDuration(180L);
            this.updateLayoutAnimator.setInterpolator(CubicBezierInterpolator.EASE_OUT);
            this.updateLayoutAnimator.playTogether(ObjectAnimator.ofFloat(this.updateLayout, (Property<FrameLayout, Float>) View.TRANSLATION_Y, 0.0f));
            animatorSet = this.updateLayoutAnimator;
            animatorListenerAdapter = new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animator) {
                    DialogsActivity.this.updateLayoutAnimator = null;
                }
            };
        } else {
            if (this.updateLayout.getTag() == null) {
                return;
            }
            this.updateLayout.setTag(null);
            if (!z) {
                this.updateLayout.setTranslationY(AndroidUtilities.dp(48.0f));
                this.updateLayout.setVisibility(4);
                return;
            }
            AnimatorSet animatorSet4 = new AnimatorSet();
            this.updateLayoutAnimator = animatorSet4;
            animatorSet4.setDuration(180L);
            this.updateLayoutAnimator.setInterpolator(CubicBezierInterpolator.EASE_OUT);
            this.updateLayoutAnimator.playTogether(ObjectAnimator.ofFloat(this.updateLayout, (Property<FrameLayout, Float>) View.TRANSLATION_Y, AndroidUtilities.dp(48.0f)));
            animatorSet = this.updateLayoutAnimator;
            animatorListenerAdapter = new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animator) {
                    if (DialogsActivity.this.updateLayout.getTag() == null) {
                        DialogsActivity.this.updateLayout.setVisibility(4);
                    }
                    DialogsActivity.this.updateLayoutAnimator = null;
                }
            };
        }
        animatorSet.addListener(animatorListenerAdapter);
        this.updateLayoutAnimator.start();
    }

    private void updateAuthHintCellVisibility(final boolean z) {
        final int i;
        if (this.authHintCellVisible != z) {
            this.authHintCellVisible = z;
            if (this.authHintCell == null) {
                return;
            }
            ValueAnimator valueAnimator = this.authHintCellAnimator;
            if (valueAnimator != null) {
                valueAnimator.cancel();
                this.authHintCellAnimator = null;
            }
            if (z) {
                this.authHintCell.setVisibility(0);
            }
            this.authHintCell.setAlpha(1.0f);
            this.viewPages[0].listView.requestLayout();
            this.fragmentView.requestLayout();
            this.notificationsLocker.lock();
            this.authHintCellAnimating = true;
            final ValueAnimator ofFloat = ValueAnimator.ofFloat(this.authHintCellProgress, z ? 1.0f : 0.0f);
            final int findFirstVisibleItemPosition = this.viewPages[0].layoutManager.findFirstVisibleItemPosition();
            if (findFirstVisibleItemPosition != -1) {
                i = (z ? 0 : -this.authHintCell.getMeasuredHeight()) + this.viewPages[0].layoutManager.findViewByPosition(findFirstVisibleItemPosition).getTop();
            } else {
                i = 0;
            }
            AndroidUtilities.doOnLayout(this.fragmentView, new Runnable() {
                @Override
                public final void run() {
                    DialogsActivity.this.lambda$updateAuthHintCellVisibility$26(z, findFirstVisibleItemPosition, i, ofFloat);
                }
            });
        }
    }

    public void updateCommentView() {
    }

    public void updateContextViewPosition() {
        float f;
        FilterTabsView filterTabsView = this.filterTabsView;
        float f2 = 0.0f;
        float measuredHeight = (filterTabsView == null || filterTabsView.getVisibility() == 8) ? 0.0f : this.filterTabsView.getMeasuredHeight();
        ViewPagerFixed.TabsView tabsView = this.searchTabsView;
        float measuredHeight2 = (tabsView == null || tabsView.getVisibility() == 8) ? 0.0f : this.searchTabsView.getMeasuredHeight();
        float dp = this.hasStories ? AndroidUtilities.dp(81.0f) : 0.0f;
        if (this.hasStories) {
            float f3 = this.scrollYOffset;
            float f4 = this.searchAnimationProgress;
            float f5 = 1.0f - f4;
            f = (f3 * f5) + (dp * f5) + (measuredHeight * f5) + (measuredHeight2 * f4) + this.tabsYOffset;
        } else {
            float f6 = this.scrollYOffset;
            float f7 = this.searchAnimationProgress;
            f = f6 + (measuredHeight * (1.0f - f7)) + (measuredHeight2 * f7) + this.tabsYOffset;
        }
        float f8 = f + this.storiesOverscroll;
        DialogsHintCell dialogsHintCell = this.dialogsHintCell;
        if (dialogsHintCell != null && dialogsHintCell.getVisibility() == 0) {
            RightSlidingDialogContainer rightSlidingDialogContainer = this.rightSlidingDialogContainer;
            if (rightSlidingDialogContainer != null && rightSlidingDialogContainer.hasFragment()) {
                f8 -= this.dialogsHintCell.getMeasuredHeight() * this.rightSlidingDialogContainer.openedProgress;
            }
            this.dialogsHintCell.setTranslationY(f8);
            f8 += this.dialogsHintCell.getMeasuredHeight() * (1.0f - this.searchAnimationProgress);
        }
        UnconfirmedAuthHintCell unconfirmedAuthHintCell = this.authHintCell;
        if (unconfirmedAuthHintCell != null && unconfirmedAuthHintCell.getVisibility() == 0) {
            RightSlidingDialogContainer rightSlidingDialogContainer2 = this.rightSlidingDialogContainer;
            if (rightSlidingDialogContainer2 != null && rightSlidingDialogContainer2.hasFragment()) {
                f8 -= this.authHintCell.getMeasuredHeight() * this.rightSlidingDialogContainer.openedProgress;
            }
            float measuredHeight3 = this.authHintCell.getMeasuredHeight() * (1.0f - this.authHintCellProgress);
            this.authHintCell.setTranslationY((-measuredHeight3) + f8);
            f8 += this.authHintCell.getMeasuredHeight() - measuredHeight3;
        }
        if (this.fragmentContextView != null) {
            FragmentContextView fragmentContextView = this.fragmentLocationContextView;
            float dp2 = (fragmentContextView == null || fragmentContextView.getVisibility() != 0) ? 0.0f : AndroidUtilities.dp(36.0f) + 0.0f;
            FragmentContextView fragmentContextView2 = this.fragmentContextView;
            fragmentContextView2.setTranslationY(dp2 + fragmentContextView2.getTopPadding() + f8);
        }
        if (this.fragmentLocationContextView != null) {
            FragmentContextView fragmentContextView3 = this.fragmentContextView;
            if (fragmentContextView3 != null && fragmentContextView3.getVisibility() == 0) {
                f2 = 0.0f + AndroidUtilities.dp(this.fragmentContextView.getStyleHeight()) + this.fragmentContextView.getTopPadding();
            }
            FragmentContextView fragmentContextView4 = this.fragmentLocationContextView;
            fragmentContextView4.setTranslationY(f2 + fragmentContextView4.getTopPadding() + f8);
        }
    }

    public void updateCounters(boolean r21) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.DialogsActivity.updateCounters(boolean):void");
    }

    private void updateDialogIndices() {
        if (this.viewPages == null) {
            return;
        }
        int i = 0;
        while (true) {
            ViewPage[] viewPageArr = this.viewPages;
            if (i >= viewPageArr.length) {
                return;
            }
            if (viewPageArr[i].getVisibility() == 0 && !this.viewPages[i].dialogsAdapter.getDialogsListIsFrozen()) {
                this.viewPages[i].updateList(false);
            }
            i++;
        }
    }

    public void lambda$updateDialogsHint$29() {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.DialogsActivity.lambda$updateDialogsHint$29():void");
    }

    public void updateDrawerSwipeEnabled() {
        RightSlidingDialogContainer rightSlidingDialogContainer;
        INavigationLayout iNavigationLayout = this.parentLayout;
        if (iNavigationLayout == null || iNavigationLayout.getDrawerLayoutContainer() == null) {
            return;
        }
        this.parentLayout.getDrawerLayoutContainer().setAllowOpenDrawerBySwipe(((this.isFirstTab && SharedConfig.getChatSwipeAction(this.currentAccount) == 5) || SharedConfig.getChatSwipeAction(this.currentAccount) != 5) && !this.searchIsShowed && ((rightSlidingDialogContainer = this.rightSlidingDialogContainer) == null || !rightSlidingDialogContainer.hasFragment()));
    }

    public void updateFilterTabs(boolean z, boolean z2) {
        int findFirstVisibleItemPosition;
        MessagesController.DialogFilter dialogFilter;
        boolean z3;
        int i;
        FilterTabsView filterTabsView;
        int i2;
        String str;
        int i3;
        boolean z4;
        boolean z5;
        if (this.filterTabsView == null || this.inPreviewMode || this.searchIsShowed) {
            return;
        }
        RightSlidingDialogContainer rightSlidingDialogContainer = this.rightSlidingDialogContainer;
        if (rightSlidingDialogContainer == null || !rightSlidingDialogContainer.hasFragment()) {
            ItemOptions itemOptions = this.filterOptions;
            if (itemOptions != null) {
                itemOptions.dismiss();
                this.filterOptions = null;
            }
            ArrayList<MessagesController.DialogFilter> dialogFilters = getMessagesController().getDialogFilters();
            if (dialogFilters.size() <= 1) {
                if (this.filterTabsView.getVisibility() != 8) {
                    this.filterTabsView.setIsEditing(false);
                    showDoneItem(false);
                    this.maybeStartTracking = false;
                    if (this.startedTracking) {
                        this.startedTracking = false;
                        this.viewPages[0].setTranslationX(0.0f);
                        this.viewPages[1].setTranslationX(r3[0].getMeasuredWidth());
                    }
                    if (this.viewPages[0].selectedType != this.filterTabsView.getDefaultTabId()) {
                        this.viewPages[0].selectedType = this.filterTabsView.getDefaultTabId();
                        this.viewPages[0].dialogsAdapter.setDialogsType(0);
                        this.viewPages[0].dialogsType = this.initialDialogsType;
                        this.viewPages[0].dialogsAdapter.notifyDataSetChanged();
                    }
                    this.viewPages[1].setVisibility(8);
                    this.viewPages[1].selectedType = 0;
                    this.viewPages[1].dialogsAdapter.setDialogsType(0);
                    this.viewPages[1].dialogsType = this.initialDialogsType;
                    this.viewPages[1].dialogsAdapter.notifyDataSetChanged();
                    this.canShowFilterTabsView = false;
                    updateFilterTabsVisibility(z2);
                    int i4 = 0;
                    while (true) {
                        ViewPage[] viewPageArr = this.viewPages;
                        if (i4 >= viewPageArr.length) {
                            break;
                        }
                        if (viewPageArr[i4].dialogsType == 0 && this.viewPages[i4].archivePullViewState == 2 && hasHiddenArchive() && ((findFirstVisibleItemPosition = this.viewPages[i4].layoutManager.findFirstVisibleItemPosition()) == 0 || findFirstVisibleItemPosition == 1)) {
                            this.viewPages[i4].layoutManager.scrollToPositionWithOffset(1, (int) this.scrollYOffset);
                        }
                        this.viewPages[i4].listView.setScrollingTouchSlop(0);
                        this.viewPages[i4].listView.requestLayout();
                        this.viewPages[i4].requestLayout();
                        i4++;
                    }
                    this.filterTabsView.resetTabId();
                }
                updateDrawerSwipeEnabled();
            } else if (z || this.filterTabsView.getVisibility() != 0) {
                boolean z6 = this.filterTabsView.getVisibility() != 0 ? false : z2;
                this.canShowFilterTabsView = true;
                boolean isEmpty = this.filterTabsView.isEmpty();
                updateFilterTabsVisibility(z2);
                int currentTabId = this.filterTabsView.getCurrentTabId();
                int currentTabStableId = this.filterTabsView.getCurrentTabStableId();
                if (currentTabId == this.filterTabsView.getDefaultTabId() || currentTabId < dialogFilters.size()) {
                    z3 = false;
                } else {
                    this.filterTabsView.resetTabId();
                    z3 = true;
                }
                this.filterTabsView.removeTabs();
                int size = dialogFilters.size();
                int i5 = 0;
                while (i5 < size) {
                    if (dialogFilters.get(i5).isDefault()) {
                        filterTabsView = this.filterTabsView;
                        str = LocaleController.getString(R.string.FilterAllChats);
                        boolean z7 = dialogFilters.get(i5).locked;
                        i3 = i5;
                        i2 = 0;
                        i = i5;
                        z4 = true;
                        z5 = z7;
                    } else {
                        i = i5;
                        filterTabsView = this.filterTabsView;
                        i2 = dialogFilters.get(i).localId;
                        str = dialogFilters.get(i).name;
                        i3 = i;
                        z4 = false;
                        z5 = dialogFilters.get(i).locked;
                    }
                    filterTabsView.addTab(i3, i2, str, z4, z5);
                    i5 = i + 1;
                }
                if (currentTabStableId >= 0) {
                    if (z3 && !this.filterTabsView.selectTabWithStableId(currentTabStableId)) {
                        while (currentTabId >= 0) {
                            FilterTabsView filterTabsView2 = this.filterTabsView;
                            if (filterTabsView2.selectTabWithStableId(filterTabsView2.getStableId(currentTabId))) {
                                break;
                            } else {
                                currentTabId--;
                            }
                        }
                        if (currentTabId < 0) {
                            currentTabId = 0;
                        }
                    }
                    if (this.filterTabsView.getStableId(this.viewPages[0].selectedType) != currentTabStableId) {
                        this.viewPages[0].selectedType = currentTabId;
                        isEmpty = true;
                    }
                }
                int i6 = 0;
                while (true) {
                    ViewPage[] viewPageArr2 = this.viewPages;
                    if (i6 >= viewPageArr2.length) {
                        break;
                    }
                    if (viewPageArr2[i6].selectedType >= dialogFilters.size()) {
                        this.viewPages[i6].selectedType = dialogFilters.size() - 1;
                    }
                    this.viewPages[i6].listView.setScrollingTouchSlop(1);
                    i6++;
                }
                this.filterTabsView.finishAddingTabs(z6);
                if (isEmpty) {
                    switchToCurrentSelectedMode(false);
                }
                this.isFirstTab = currentTabId == this.filterTabsView.getFirstTabId();
                updateDrawerSwipeEnabled();
                FilterTabsView filterTabsView3 = this.filterTabsView;
                if (filterTabsView3.isLocked(filterTabsView3.getCurrentTabId())) {
                    this.filterTabsView.selectFirstTab();
                }
            }
            updateCounters(false);
            int i7 = this.viewPages[0].dialogsType;
            if ((i7 == 7 || i7 == 8) && (dialogFilter = getMessagesController().selectedDialogFilter[i7 - 7]) != null) {
                for (int i8 = 0; i8 < dialogFilters.size(); i8++) {
                    MessagesController.DialogFilter dialogFilter2 = dialogFilters.get(i8);
                    if (dialogFilter2 != null && dialogFilter2.id == dialogFilter.id) {
                        return;
                    }
                }
                switchToCurrentSelectedMode(false);
            }
        }
    }

    private void updateFilterTabsVisibility(boolean z) {
        ValueAnimator ofFloat;
        if (this.fragmentView == null) {
            return;
        }
        if (this.isPaused || this.databaseMigrationHint != null) {
            z = false;
        }
        if (this.searchIsShowed) {
            ValueAnimator valueAnimator = this.filtersTabAnimator;
            if (valueAnimator != null) {
                valueAnimator.cancel();
            }
            boolean z2 = this.canShowFilterTabsView;
            this.filterTabsViewIsVisible = z2;
            this.filterTabsProgress = z2 ? 1.0f : 0.0f;
            return;
        }
        final boolean z3 = this.canShowFilterTabsView;
        if (this.filterTabsViewIsVisible != z3) {
            ValueAnimator valueAnimator2 = this.filtersTabAnimator;
            if (valueAnimator2 != null) {
                valueAnimator2.cancel();
            }
            this.filterTabsViewIsVisible = z3;
            if (!z) {
                this.filterTabsProgress = z3 ? 1.0f : 0.0f;
                this.filterTabsView.setVisibility(z3 ? 0 : 8);
                View view = this.fragmentView;
                if (view != null) {
                    view.invalidate();
                    return;
                }
                return;
            }
            if (z3) {
                if (this.filterTabsView.getVisibility() != 0) {
                    this.filterTabsView.setVisibility(0);
                }
                ofFloat = ValueAnimator.ofFloat(0.0f, 1.0f);
            } else {
                ofFloat = ValueAnimator.ofFloat(1.0f, 0.0f);
            }
            this.filtersTabAnimator = ofFloat;
            this.filterTabsMoveFrom = getActionBarMoveFrom(true);
            final float f = this.scrollYOffset;
            this.filtersTabAnimator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animator) {
                    DialogsActivity.this.filtersTabAnimator = null;
                    DialogsActivity.this.scrollAdditionalOffset = 0.0f;
                    if (!z3) {
                        DialogsActivity.this.filterTabsView.setVisibility(8);
                    }
                    View view2 = DialogsActivity.this.fragmentView;
                    if (view2 != null) {
                        view2.requestLayout();
                    }
                    DialogsActivity.this.notificationsLocker.unlock();
                }
            });
            this.filtersTabAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public final void onAnimationUpdate(ValueAnimator valueAnimator3) {
                    DialogsActivity.this.lambda$updateFilterTabsVisibility$76(z3, f, valueAnimator3);
                }
            });
            this.filtersTabAnimator.setDuration(220L);
            this.filtersTabAnimator.setInterpolator(CubicBezierInterpolator.DEFAULT);
            this.notificationsLocker.lock();
            this.filtersTabAnimator.start();
            this.fragmentView.requestLayout();
        }
    }

    public void updateFiltersView(boolean r11, java.util.ArrayList r12, java.util.ArrayList r13, boolean r14, boolean r15) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.DialogsActivity.updateFiltersView(boolean, java.util.ArrayList, java.util.ArrayList, boolean, boolean):void");
    }

    private void updateFloatingButtonColor() {
        if (getParentActivity() == null) {
            return;
        }
        if (this.floatingButtonContainer != null) {
            Drawable createSimpleSelectorCircleDrawable = Theme.createSimpleSelectorCircleDrawable(AndroidUtilities.dp(56.0f), Theme.getColor(Theme.key_chats_actionBackground), Theme.getColor(Theme.key_chats_actionPressedBackground));
            if (Build.VERSION.SDK_INT < 21) {
                Drawable mutate = ContextCompat.getDrawable(getParentActivity(), R.drawable.floating_shadow).mutate();
                mutate.setColorFilter(new PorterDuffColorFilter(-16777216, PorterDuff.Mode.MULTIPLY));
                CombinedDrawable combinedDrawable = new CombinedDrawable(mutate, createSimpleSelectorCircleDrawable, 0, 0);
                combinedDrawable.setIconSize(AndroidUtilities.dp(56.0f), AndroidUtilities.dp(56.0f));
                createSimpleSelectorCircleDrawable = combinedDrawable;
            }
            this.floatingButtonContainer.setBackground(createSimpleSelectorCircleDrawable);
        }
        if (this.floatingButton2Container != null) {
            int dp = AndroidUtilities.dp(36.0f);
            int i = Theme.key_windowBackgroundWhite;
            Drawable createSimpleSelectorCircleDrawable2 = Theme.createSimpleSelectorCircleDrawable(dp, ColorUtils.blendARGB(Theme.getColor(i), -1, 0.1f), Theme.blendOver(Theme.getColor(i), Theme.getColor(Theme.key_listSelector)));
            if (Build.VERSION.SDK_INT < 21) {
                Drawable mutate2 = ContextCompat.getDrawable(getParentActivity(), R.drawable.floating_shadow).mutate();
                mutate2.setColorFilter(new PorterDuffColorFilter(-16777216, PorterDuff.Mode.MULTIPLY));
                CombinedDrawable combinedDrawable2 = new CombinedDrawable(mutate2, createSimpleSelectorCircleDrawable2, 0, 0);
                combinedDrawable2.setIconSize(AndroidUtilities.dp(36.0f), AndroidUtilities.dp(36.0f));
                createSimpleSelectorCircleDrawable2 = combinedDrawable2;
            }
            this.floatingButton2Container.setBackground(createSimpleSelectorCircleDrawable2);
        }
    }

    public void updateFloatingButtonOffset() {
        FrameLayout frameLayout = this.floatingButtonContainer;
        if (frameLayout != null) {
            frameLayout.setTranslationY((this.floatingButtonTranslation - this.floatingButtonPanOffset) - (Math.max(this.additionalFloatingTranslation, this.additionalFloatingTranslation2) * (1.0f - this.floatingButtonHideProgress)));
            HintView2 hintView2 = this.storyHint;
            if (hintView2 != null) {
                hintView2.setTranslationY(this.floatingButtonContainer.getTranslationY());
            }
        }
        FrameLayout frameLayout2 = this.floatingButton2Container;
        if (frameLayout2 != null) {
            frameLayout2.setTranslationY(((this.floatingButtonTranslation - this.floatingButtonPanOffset) - (Math.max(this.additionalFloatingTranslation, this.additionalFloatingTranslation2) * (1.0f - this.floatingButtonHideProgress))) + (AndroidUtilities.dp(44.0f) * this.floatingButtonHideProgress));
        }
    }

    private void updateMenuButton(boolean z) {
        int i;
        if (this.menuDrawable == null || this.updateLayout == null) {
            return;
        }
        float f = 0.0f;
        if (SharedConfig.isAppUpdateAvailable()) {
            String attachFileName = FileLoader.getAttachFileName(SharedConfig.pendingAppUpdate.document);
            if (getFileLoader().isLoadingFile(attachFileName)) {
                i = MenuDrawable.TYPE_UDPATE_DOWNLOADING;
                Float fileProgress = ImageLoader.getInstance().getFileProgress(attachFileName);
                if (fileProgress != null) {
                    f = fileProgress.floatValue();
                }
            } else {
                i = MenuDrawable.TYPE_UDPATE_AVAILABLE;
            }
        } else {
            i = MenuDrawable.TYPE_DEFAULT;
        }
        updateAppUpdateViews(z);
        this.menuDrawable.setType(i, z);
        this.menuDrawable.setUpdateDownloadProgress(f, z);
    }

    public void updatePasscodeButton() {
        if (this.passcodeItem == null) {
            return;
        }
        if (SharedConfig.passcodeHash.length() == 0 || this.searching) {
            this.passcodeItem.setVisibility(8);
            this.passcodeItemVisible = false;
            return;
        }
        ActionBarMenuItem actionBarMenuItem = this.doneItem;
        if (actionBarMenuItem == null || actionBarMenuItem.getVisibility() != 0) {
            this.passcodeItem.setVisibility(0);
        }
        this.passcodeItem.setIcon(this.passcodeDrawable);
        this.passcodeItemVisible = true;
    }

    public void updateProxyButton(boolean z, boolean z2) {
        boolean z3;
        ActionBarMenuItem actionBarMenuItem;
        if (this.proxyDrawable != null) {
            ActionBarMenuItem actionBarMenuItem2 = this.doneItem;
            if (actionBarMenuItem2 == null || actionBarMenuItem2.getVisibility() != 0) {
                int i = 0;
                while (true) {
                    if (i >= getDownloadController().downloadingFiles.size()) {
                        z3 = false;
                        break;
                    } else {
                        if (getFileLoader().isLoadingFile(getDownloadController().downloadingFiles.get(i).getFileName())) {
                            z3 = true;
                            break;
                        }
                        i++;
                    }
                }
                if (this.searching || !(getDownloadController().hasUnviewedDownloads() || z3 || (this.downloadsItem.getVisibility() == 0 && this.downloadsItem.getAlpha() == 1.0f && !z2))) {
                    this.downloadsItem.setVisibility(8);
                    this.downloadsItemVisible = false;
                } else {
                    this.downloadsItemVisible = true;
                    this.downloadsItem.setVisibility(0);
                }
                SharedPreferences sharedPreferences = ApplicationLoader.applicationContext.getSharedPreferences("mainconfig", 0);
                String string = sharedPreferences.getString("proxy_ip", "");
                boolean z4 = sharedPreferences.getBoolean("proxy_enabled", false);
                if ((this.downloadsItemVisible || this.searching || !z4 || TextUtils.isEmpty(string)) && (!getMessagesController().blockedCountry || SharedConfig.proxyList.isEmpty())) {
                    this.proxyItemVisible = false;
                    this.proxyItem.setVisibility(8);
                    return;
                }
                if (!this.actionBar.isSearchFieldVisible() && ((actionBarMenuItem = this.doneItem) == null || actionBarMenuItem.getVisibility() != 0)) {
                    this.proxyItem.setVisibility(0);
                }
                this.proxyItemVisible = true;
                ProxyDrawable proxyDrawable = this.proxyDrawable;
                int i2 = this.currentConnectionState;
                proxyDrawable.setConnected(z4, i2 == 3 || i2 == 5, z);
            }
        }
    }

    public void updateSelectedCount() {
        ActionBar actionBar;
        int i;
        if (this.commentView != null) {
            if (this.selectedDialogs.isEmpty()) {
                if (this.initialDialogsType == 3 && this.selectAlertString == null) {
                    actionBar = this.actionBar;
                    i = R.string.ForwardTo;
                } else {
                    actionBar = this.actionBar;
                    i = R.string.SelectChat;
                }
                actionBar.setTitle(LocaleController.getString(i));
                if (this.commentView.getTag() != null) {
                    this.commentView.hidePopup(false);
                    this.commentView.closeKeyboard();
                    AnimatorSet animatorSet = this.commentViewAnimator;
                    if (animatorSet != null) {
                        animatorSet.cancel();
                    }
                    this.commentViewAnimator = new AnimatorSet();
                    this.commentView.setTranslationY(0.0f);
                    AnimatorSet animatorSet2 = this.commentViewAnimator;
                    ObjectAnimator ofFloat = ObjectAnimator.ofFloat(this.commentView, (Property<ChatActivityEnterView, Float>) View.TRANSLATION_Y, r14.getMeasuredHeight());
                    FrameLayout frameLayout = this.writeButtonContainer;
                    Property property = View.SCALE_X;
                    ObjectAnimator ofFloat2 = ObjectAnimator.ofFloat(frameLayout, (Property<FrameLayout, Float>) property, 0.2f);
                    FrameLayout frameLayout2 = this.writeButtonContainer;
                    Property property2 = View.SCALE_Y;
                    ObjectAnimator ofFloat3 = ObjectAnimator.ofFloat(frameLayout2, (Property<FrameLayout, Float>) property2, 0.2f);
                    FrameLayout frameLayout3 = this.writeButtonContainer;
                    Property property3 = View.ALPHA;
                    animatorSet2.playTogether(ofFloat, ofFloat2, ofFloat3, ObjectAnimator.ofFloat(frameLayout3, (Property<FrameLayout, Float>) property3, 0.0f), ObjectAnimator.ofFloat(this.selectedCountView, (Property<View, Float>) property, 0.2f), ObjectAnimator.ofFloat(this.selectedCountView, (Property<View, Float>) property2, 0.2f), ObjectAnimator.ofFloat(this.selectedCountView, (Property<View, Float>) property3, 0.0f));
                    this.commentViewAnimator.setDuration(180L);
                    this.commentViewAnimator.setInterpolator(new DecelerateInterpolator());
                    this.commentViewAnimator.addListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animator) {
                            DialogsActivity.this.commentView.setVisibility(8);
                            DialogsActivity.this.writeButtonContainer.setVisibility(8);
                        }
                    });
                    this.commentViewAnimator.start();
                    this.commentView.setTag(null);
                    this.fragmentView.requestLayout();
                }
            } else {
                this.selectedCountView.invalidate();
                if (this.commentView.getTag() == null) {
                    this.commentView.setFieldText("");
                    AnimatorSet animatorSet3 = this.commentViewAnimator;
                    if (animatorSet3 != null) {
                        animatorSet3.cancel();
                    }
                    this.commentView.setVisibility(0);
                    this.writeButtonContainer.setVisibility(0);
                    AnimatorSet animatorSet4 = new AnimatorSet();
                    this.commentViewAnimator = animatorSet4;
                    ObjectAnimator ofFloat4 = ObjectAnimator.ofFloat(this.commentView, (Property<ChatActivityEnterView, Float>) View.TRANSLATION_Y, r2.getMeasuredHeight(), 0.0f);
                    FrameLayout frameLayout4 = this.writeButtonContainer;
                    Property property4 = View.SCALE_X;
                    ObjectAnimator ofFloat5 = ObjectAnimator.ofFloat(frameLayout4, (Property<FrameLayout, Float>) property4, 1.0f);
                    FrameLayout frameLayout5 = this.writeButtonContainer;
                    Property property5 = View.SCALE_Y;
                    ObjectAnimator ofFloat6 = ObjectAnimator.ofFloat(frameLayout5, (Property<FrameLayout, Float>) property5, 1.0f);
                    FrameLayout frameLayout6 = this.writeButtonContainer;
                    Property property6 = View.ALPHA;
                    animatorSet4.playTogether(ofFloat4, ofFloat5, ofFloat6, ObjectAnimator.ofFloat(frameLayout6, (Property<FrameLayout, Float>) property6, 1.0f), ObjectAnimator.ofFloat(this.selectedCountView, (Property<View, Float>) property4, 1.0f), ObjectAnimator.ofFloat(this.selectedCountView, (Property<View, Float>) property5, 1.0f), ObjectAnimator.ofFloat(this.selectedCountView, (Property<View, Float>) property6, 1.0f));
                    this.commentViewAnimator.setDuration(180L);
                    this.commentViewAnimator.setInterpolator(new DecelerateInterpolator());
                    this.commentViewAnimator.addListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animator) {
                            DialogsActivity.this.commentView.setTag(2);
                            DialogsActivity.this.commentView.requestLayout();
                        }
                    });
                    this.commentViewAnimator.start();
                    this.commentView.setTag(1);
                }
                this.actionBar.setTitle(LocaleController.formatPluralString("Recipient", this.selectedDialogs.size(), new Object[0]));
            }
        } else if (this.initialDialogsType == 10) {
            hideFloatingButton(this.selectedDialogs.isEmpty());
        }
        ArrayList arrayList = this.selectedDialogs;
        ChatActivityEnterView chatActivityEnterView = this.commentView;
        boolean shouldShowNextButton = shouldShowNextButton(this, arrayList, chatActivityEnterView != null ? chatActivityEnterView.getFieldText() : "", false);
        this.isNextButton = shouldShowNextButton;
        AndroidUtilities.updateViewVisibilityAnimated(this.writeButton[0], !shouldShowNextButton, 0.5f, true);
        AndroidUtilities.updateViewVisibilityAnimated(this.writeButton[1], this.isNextButton, 0.5f, true);
    }

    private void updateStoriesPosting() {
        FrameLayout frameLayout;
        int i;
        HintView2 hintView2;
        ActionBarMenuItem actionBarMenuItem;
        boolean storiesEnabled = getMessagesController().storiesEnabled();
        if (this.storiesEnabled != storiesEnabled) {
            FrameLayout frameLayout2 = this.floatingButton2Container;
            if (frameLayout2 != null) {
                frameLayout2.setVisibility(((!this.onlySelect || this.initialDialogsType == 10) && this.folderId == 0 && storiesEnabled && ((actionBarMenuItem = this.searchItem) == null || !actionBarMenuItem.isSearchFieldVisible()) && !isInPreviewMode()) ? 0 : 8);
            }
            updateFloatingButtonOffset();
            if (!this.storiesEnabled && storiesEnabled && (hintView2 = this.storyHint) != null) {
                hintView2.show();
            }
            this.storiesEnabled = storiesEnabled;
        }
        RLottieImageView rLottieImageView = this.floatingButton;
        if (rLottieImageView == null || this.floatingButtonContainer == null) {
            return;
        }
        if (this.initialDialogsType == 10) {
            rLottieImageView.setImageResource(R.drawable.floating_check);
            frameLayout = this.floatingButtonContainer;
            i = R.string.Done;
        } else if (storiesEnabled) {
            rLottieImageView.setAnimation(R.raw.write_contacts_fab_icon_camera, 56, 56);
            frameLayout = this.floatingButtonContainer;
            i = R.string.AccDescrCaptureStory;
        } else {
            rLottieImageView.setAnimation(R.raw.write_contacts_fab_icon, 52, 52);
            frameLayout = this.floatingButtonContainer;
            i = R.string.NewMessageTitle;
        }
        frameLayout.setContentDescription(LocaleController.getString(i));
    }

    public void updateStoriesViewAlpha(float f) {
        float f2;
        this.dialogStoriesCell.setAlpha((1.0f - this.progressToActionMode) * f * this.progressToDialogStoriesCell * (1.0f - Utilities.clamp(this.searchAnimationProgress / 0.5f, 1.0f, 0.0f)));
        if (this.hasStories || this.animateToHasStories) {
            float clamp = Utilities.clamp((-this.scrollYOffset) / AndroidUtilities.dp(81.0f), 1.0f, 0.0f);
            if (this.progressToActionMode == 1.0f) {
                clamp = 1.0f;
            }
            float clamp2 = Utilities.clamp(clamp / 0.5f, 1.0f, 0.0f);
            this.dialogStoriesCell.setClipTop(0);
            if (this.hasStories || !this.animateToHasStories) {
                this.dialogStoriesCell.setTranslationY(((this.scrollYOffset + this.storiesYOffset) + (this.storiesOverscroll / 2.0f)) - AndroidUtilities.dp(8.0f));
                this.dialogStoriesCell.setProgressToCollapse(clamp, !this.rightSlidingDialogContainer.hasFragment());
                if (this.animateToHasStories) {
                    f2 = 1.0f - clamp2;
                    this.actionBar.setTranslationY(0.0f);
                }
            } else {
                this.dialogStoriesCell.setTranslationY((-AndroidUtilities.dp(81.0f)) - AndroidUtilities.dp(8.0f));
                this.dialogStoriesCell.setProgressToCollapse(1.0f);
            }
            f2 = 1.0f - this.progressToDialogStoriesCell;
            this.actionBar.setTranslationY(0.0f);
        } else {
            if (this.hasOnlySlefStories) {
                this.dialogStoriesCell.setTranslationY(((-AndroidUtilities.dp(81.0f)) + this.scrollYOffset) - AndroidUtilities.dp(8.0f));
                this.dialogStoriesCell.setProgressToCollapse(1.0f);
                DialogStoriesCell dialogStoriesCell = this.dialogStoriesCell;
                dialogStoriesCell.setClipTop((int) (AndroidUtilities.statusBarHeight - dialogStoriesCell.getY()));
            }
            f2 = 1.0f - this.progressToDialogStoriesCell;
            this.actionBar.setTranslationY(this.scrollYOffset);
        }
        if (f2 == 1.0f) {
            this.actionBar.getTitlesContainer().setScaleY(1.0f);
            this.actionBar.getTitlesContainer().setScaleY(1.0f);
            this.actionBar.getTitlesContainer().setScaleX(1.0f);
            this.actionBar.getTitlesContainer().setAlpha(1.0f - this.progressToActionMode);
            return;
        }
        this.actionBar.getTitlesContainer().setPivotY(AndroidUtilities.statusBarHeight + (ActionBar.getCurrentActionBarHeight() / 2.0f));
        this.actionBar.getTitlesContainer().setPivotX(AndroidUtilities.dp(72.0f));
        float f3 = (0.2f * f2) + 0.8f;
        this.actionBar.getTitlesContainer().setScaleY(f3);
        this.actionBar.getTitlesContainer().setScaleX(f3);
        this.actionBar.getTitlesContainer().setAlpha(f2 * (1.0f - this.progressToActionMode));
    }

    public void updateTopPadding() {
        SearchViewPager searchViewPager;
        this.topPadding = this.fragmentContextTopPadding;
        updateContextViewPosition();
        RightSlidingDialogContainer rightSlidingDialogContainer = this.rightSlidingDialogContainer;
        if (rightSlidingDialogContainer != null) {
            rightSlidingDialogContainer.setFragmentViewPadding(this.topPadding);
        }
        if (!this.whiteActionBar || (searchViewPager = this.searchViewPager) == null) {
            this.fragmentView.requestLayout();
        } else {
            searchViewPager.setTranslationY((this.topPadding - this.lastMeasuredTopPadding) + this.searchViewPagerTranslationY);
        }
    }

    public void updateVisibleRows(int i) {
        updateVisibleRows(i, true);
    }

    private void updateVisibleRows(int r18, boolean r19) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.DialogsActivity.updateVisibleRows(int, boolean):void");
    }

    public boolean validateSlowModeDialog(long j) {
        TLRPC.Chat chat;
        ChatActivityEnterView chatActivityEnterView;
        if ((this.messagesCount <= 1 && ((chatActivityEnterView = this.commentView) == null || chatActivityEnterView.getVisibility() != 0 || TextUtils.isEmpty(this.commentView.getFieldText()))) || !DialogObject.isChatDialog(j) || (chat = getMessagesController().getChat(Long.valueOf(-j))) == null || ChatObject.hasAdminRights(chat) || !chat.slowmode_enabled) {
            return true;
        }
        AlertsCreator.showSimpleAlert(this, LocaleController.getString(R.string.Slowmode), LocaleController.getString(R.string.SlowmodeSendError));
        return false;
    }

    public boolean waitingForDialogsAnimationEnd(ViewPage viewPage) {
        return viewPage.dialogsItemAnimator.isRunning();
    }

    public boolean addOrRemoveSelectedDialog(long j, View view) {
        if (this.onlySelect && getMessagesController().isForum(j)) {
            return false;
        }
        if (this.selectedDialogs.contains(Long.valueOf(j))) {
            this.selectedDialogs.remove(Long.valueOf(j));
            if (view instanceof DialogCell) {
                ((DialogCell) view).setChecked(false, true);
            } else if (view instanceof ProfileSearchCell) {
                ((ProfileSearchCell) view).setChecked(false, true);
            }
            return false;
        }
        this.selectedDialogs.add(Long.valueOf(j));
        if (view instanceof DialogCell) {
            ((DialogCell) view).setChecked(true, true);
        } else if (view instanceof ProfileSearchCell) {
            ((ProfileSearchCell) view).setChecked(true, true);
        }
        return true;
    }

    public void animateContactsAlpha(float f) {
        ValueAnimator valueAnimator = this.contactsAlphaAnimator;
        if (valueAnimator != null) {
            valueAnimator.cancel();
        }
        ValueAnimator duration = ValueAnimator.ofFloat(this.contactsAlpha, f).setDuration(250L);
        this.contactsAlphaAnimator = duration;
        duration.setInterpolator(CubicBezierInterpolator.DEFAULT);
        this.contactsAlphaAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public final void onAnimationUpdate(ValueAnimator valueAnimator2) {
                DialogsActivity.this.lambda$animateContactsAlpha$113(valueAnimator2);
            }
        });
        this.contactsAlphaAnimator.start();
    }

    @Override
    public boolean canBeginSlide() {
        FilterTabsView filterTabsView;
        if (this.rightSlidingDialogContainer.hasFragment()) {
            return false;
        }
        if (this.initialDialogsType == 3 && (filterTabsView = this.filterTabsView) != null && filterTabsView.getVisibility() == 0) {
            return this.filterTabsView.isFirstTab();
        }
        return true;
    }

    @Override
    public boolean closeLastFragment() {
        if (!this.rightSlidingDialogContainer.hasFragment()) {
            return super.closeLastFragment();
        }
        this.rightSlidingDialogContainer.lambda$presentFragment$1();
        SearchViewPager searchViewPager = this.searchViewPager;
        if (searchViewPager == null) {
            return true;
        }
        searchViewPager.updateTabs();
        return true;
    }

    public void closeSearching() {
        ActionBar actionBar = this.actionBar;
        if (actionBar == null || !actionBar.isSearchFieldVisible()) {
            return;
        }
        this.actionBar.closeSearchField();
        this.searchIsShowed = false;
        updateFilterTabs(true, true);
    }

    @Override
    public ActionBar createActionBar(Context context) {
        ActionBar actionBar = new ActionBar(context) {
            @Override
            public boolean drawChild(Canvas canvas, View view, long j) {
                if (!((BaseFragment) DialogsActivity.this).inPreviewMode || DialogsActivity.this.avatarContainer == null || view == DialogsActivity.this.avatarContainer) {
                    return super.drawChild(canvas, view, j);
                }
                return false;
            }

            @Override
            protected boolean onSearchChangedIgnoreTitles() {
                RightSlidingDialogContainer rightSlidingDialogContainer = DialogsActivity.this.rightSlidingDialogContainer;
                return rightSlidingDialogContainer != null && rightSlidingDialogContainer.hasFragment();
            }

            @Override
            public void onSearchFieldVisibilityChanged(boolean z) {
                RightSlidingDialogContainer rightSlidingDialogContainer = DialogsActivity.this.rightSlidingDialogContainer;
                if (rightSlidingDialogContainer != null && rightSlidingDialogContainer.hasFragment()) {
                    getBackButton().animate().alpha(z ? 1.0f : 0.0f).start();
                }
                super.onSearchFieldVisibilityChanged(z);
            }

            @Override
            public void setTitleOverlayText(String str, int i, Runnable runnable) {
                super.setTitleOverlayText(str, i, runnable);
                if (DialogsActivity.this.selectAnimatedEmojiDialog != null && (DialogsActivity.this.selectAnimatedEmojiDialog.getContentView() instanceof SelectAnimatedEmojiDialog)) {
                    SimpleTextView titleTextView = getTitleTextView();
                    ((SelectAnimatedEmojiDialog) DialogsActivity.this.selectAnimatedEmojiDialog.getContentView()).setScrimDrawable((titleTextView == null || titleTextView.getRightDrawable() != DialogsActivity.this.statusDrawable) ? null : DialogsActivity.this.statusDrawable, titleTextView);
                }
                DialogStoriesCell dialogStoriesCell = DialogsActivity.this.dialogStoriesCell;
                if (dialogStoriesCell != null) {
                    dialogStoriesCell.setTitleOverlayText(str, i);
                }
            }

            @Override
            public void setTranslationY(float f) {
                View view;
                if (f != getTranslationY() && (view = DialogsActivity.this.fragmentView) != null) {
                    view.invalidate();
                }
                super.setTranslationY(f);
            }

            @Override
            public boolean shouldClipChild(View view) {
                return super.shouldClipChild(view) || view == DialogsActivity.this.doneItem;
            }
        };
        actionBar.setUseContainerForTitles();
        actionBar.setItemsBackgroundColor(Theme.getColor(Theme.key_actionBarDefaultSelector), false);
        actionBar.setItemsBackgroundColor(Theme.getColor(Theme.key_actionBarActionModeDefaultSelector), true);
        actionBar.setItemsColor(Theme.getColor(Theme.key_actionBarDefaultIcon), false);
        actionBar.setItemsColor(Theme.getColor(Theme.key_actionBarActionModeDefaultIcon), true);
        if (this.inPreviewMode || (AndroidUtilities.isTablet() && this.folderId != 0)) {
            actionBar.setOccupyStatusBar(false);
        }
        return actionBar;
    }

    public void createSearchViewPager() {
        SearchViewPager searchViewPager = this.searchViewPager;
        if ((searchViewPager != null && searchViewPager.getParent() == this.fragmentView) || this.fragmentView == null || getContext() == null) {
            return;
        }
        SearchViewPager searchViewPager2 = new SearchViewPager(getContext(), this, this.searchString != null ? 2 : !this.onlySelect ? 1 : 0, this.initialDialogsType, this.folderId, new SearchViewPager.ChatPreviewDelegate() {
            @Override
            public void finish() {
                Point point = AndroidUtilities.displaySize;
                if (point.x > point.y) {
                    DialogsActivity.this.finishPreviewFragment();
                }
            }

            @Override
            public void move(float f) {
                Point point = AndroidUtilities.displaySize;
                if (point.x > point.y) {
                    DialogsActivity.this.movePreviewFragment(f);
                }
            }

            @Override
            public void startChatPreview(RecyclerListView recyclerListView, DialogCell dialogCell) {
                DialogsActivity.this.showChatPreview(dialogCell);
            }
        }) {
            @Override
            protected boolean includeDownloads() {
                RightSlidingDialogContainer rightSlidingDialogContainer = DialogsActivity.this.rightSlidingDialogContainer;
                return rightSlidingDialogContainer == null || !rightSlidingDialogContainer.hasFragment();
            }

            @Override
            protected boolean onBackProgress(float f) {
                return false;
            }

            @Override
            protected void onTabPageSelected(int i) {
                DialogsActivity.this.updateSpeedItem(i == 2);
            }
        };
        this.searchViewPager = searchViewPager2;
        ((ContentView) this.fragmentView).addView(searchViewPager2, this.searchViewPagerIndex);
        this.searchViewPager.dialogsSearchAdapter.setDelegate(new AnonymousClass60());
        this.searchViewPager.channelsSearchListView.setOnItemClickListener(new RecyclerListView.OnItemClickListenerExtended() {
            @Override
            public boolean hasDoubleTap(View view, int i) {
                return RecyclerListView.OnItemClickListenerExtended.CC.$default$hasDoubleTap(this, view, i);
            }

            @Override
            public void onDoubleTap(View view, int i, float f, float f2) {
                RecyclerListView.OnItemClickListenerExtended.CC.$default$onDoubleTap(this, view, i, f, f2);
            }

            @Override
            public final void onItemClick(View view, int i, float f, float f2) {
                DialogsActivity.this.lambda$createSearchViewPager$134(view, i, f, f2);
            }
        });
        this.searchViewPager.botsSearchListView.setOnItemClickListener(new RecyclerListView.OnItemClickListenerExtended() {
            @Override
            public boolean hasDoubleTap(View view, int i) {
                return RecyclerListView.OnItemClickListenerExtended.CC.$default$hasDoubleTap(this, view, i);
            }

            @Override
            public void onDoubleTap(View view, int i, float f, float f2) {
                RecyclerListView.OnItemClickListenerExtended.CC.$default$onDoubleTap(this, view, i, f, f2);
            }

            @Override
            public final void onItemClick(View view, int i, float f, float f2) {
                DialogsActivity.this.lambda$createSearchViewPager$135(view, i, f, f2);
            }
        });
        this.searchViewPager.hashtagSearchListView.setOnItemClickListener(new RecyclerListView.OnItemClickListener() {
            @Override
            public final void onItemClick(View view, int i) {
                DialogsActivity.this.lambda$createSearchViewPager$136(view, i);
            }
        });
        this.searchViewPager.botsSearchListView.setOnItemLongClickListener(new RecyclerListView.OnItemLongClickListener() {
            @Override
            public final boolean onItemClick(View view, int i) {
                boolean lambda$createSearchViewPager$138;
                lambda$createSearchViewPager$138 = DialogsActivity.this.lambda$createSearchViewPager$138(view, i);
                return lambda$createSearchViewPager$138;
            }
        });
        this.searchViewPager.searchListView.setOnItemClickListener(new RecyclerListView.OnItemClickListenerExtended() {
            @Override
            public boolean hasDoubleTap(View view, int i) {
                return RecyclerListView.OnItemClickListenerExtended.CC.$default$hasDoubleTap(this, view, i);
            }

            @Override
            public void onDoubleTap(View view, int i, float f, float f2) {
                RecyclerListView.OnItemClickListenerExtended.CC.$default$onDoubleTap(this, view, i, f, f2);
            }

            @Override
            public final void onItemClick(View view, int i, float f, float f2) {
                DialogsActivity.this.lambda$createSearchViewPager$139(view, i, f, f2);
            }
        });
        this.searchViewPager.searchListView.setOnItemLongClickListener(new RecyclerListView.OnItemLongClickListenerExtended() {
            @Override
            public boolean onItemClick(View view, int i, float f, float f2) {
                if (view instanceof ProfileSearchCell) {
                    ProfileSearchCell profileSearchCell = (ProfileSearchCell) view;
                    if (profileSearchCell.isBlocked()) {
                        DialogsActivity.this.showPremiumBlockedToast(view, profileSearchCell.getDialogId());
                        return true;
                    }
                }
                DialogsActivity dialogsActivity = DialogsActivity.this;
                return dialogsActivity.onItemLongClick(dialogsActivity.searchViewPager.searchListView, view, i, f, f2, -1, DialogsActivity.this.searchViewPager.dialogsSearchAdapter);
            }

            @Override
            public void onLongClickRelease() {
                Point point = AndroidUtilities.displaySize;
                if (point.x > point.y) {
                    DialogsActivity.this.finishPreviewFragment();
                }
            }

            @Override
            public void onMove(float f, float f2) {
                Point point = AndroidUtilities.displaySize;
                if (point.x > point.y) {
                    DialogsActivity.this.movePreviewFragment(f2);
                }
            }
        });
        this.searchViewPager.setFilteredSearchViewDelegate(new FilteredSearchView.Delegate() {
            @Override
            public final void updateFiltersView(boolean z, ArrayList arrayList, ArrayList arrayList2, boolean z2) {
                DialogsActivity.this.lambda$createSearchViewPager$140(z, arrayList, arrayList2, z2);
            }
        });
        this.searchViewPager.setVisibility(8);
    }

    public void createUndoView() {
        Context context;
        if (this.undoView[0] == null && (context = getContext()) != null) {
            for (int i = 0; i < 2; i++) {
                this.undoView[i] = new AnonymousClass45(context);
                ContentView contentView = (ContentView) this.fragmentView;
                UndoView undoView = this.undoView[i];
                int i2 = this.undoViewIndex + 1;
                this.undoViewIndex = i2;
                contentView.addView(undoView, i2, LayoutHelper.createFrame(-1, -2.0f, 83, 8.0f, 0.0f, 8.0f, 8.0f));
            }
        }
    }

    @Override
    public android.view.View createView(final android.content.Context r48) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.DialogsActivity.createView(android.content.Context):android.view.View");
    }

    @Override
    public void didReceivedNotification(int i, int i2, final Object... objArr) {
        int i3;
        MessagesController.DialogFilter dialogFilter;
        TLRPC.User user;
        final boolean booleanValue;
        final boolean z;
        DialogsSearchAdapter dialogsSearchAdapter;
        DialogsSearchAdapter dialogsSearchAdapter2;
        int i4 = 0;
        if (i == NotificationCenter.dialogsNeedReload) {
            if (this.viewPages == null || this.dialogsListFrozen) {
                return;
            }
            int i5 = 0;
            while (true) {
                ViewPage[] viewPageArr = this.viewPages;
                if (i5 >= viewPageArr.length) {
                    break;
                }
                final ViewPage viewPage = viewPageArr[i5];
                MessagesController.DialogFilter dialogFilter2 = (viewPageArr[0].dialogsType == 7 || this.viewPages[0].dialogsType == 8) ? getMessagesController().selectedDialogFilter[this.viewPages[0].dialogsType == 8 ? (char) 1 : (char) 0] : null;
                boolean z2 = (dialogFilter2 == null || (dialogFilter2.flags & MessagesController.DIALOG_FILTER_FLAG_EXCLUDE_READ) == 0) ? false : true;
                if (this.slowedReloadAfterDialogClick && z2) {
                    AndroidUtilities.runOnUIThread(new Runnable() {
                        @Override
                        public final void run() {
                            DialogsActivity.this.lambda$didReceivedNotification$105(viewPage, objArr);
                        }
                    }, 160L);
                } else {
                    reloadViewPageDialogs(viewPage, objArr.length > 0);
                }
                i5++;
            }
            FilterTabsView filterTabsView = this.filterTabsView;
            if (filterTabsView != null && filterTabsView.getVisibility() == 0) {
                this.filterTabsView.checkTabsCounter();
            }
            this.slowedReloadAfterDialogClick = false;
            return;
        }
        if (i == NotificationCenter.dialogsUnreadCounterChanged) {
            FilterTabsView filterTabsView2 = this.filterTabsView;
            if (filterTabsView2 == null || filterTabsView2.getVisibility() != 0) {
                return;
            }
            FilterTabsView filterTabsView3 = this.filterTabsView;
            filterTabsView3.notifyTabCounterChanged(filterTabsView3.getDefaultTabId());
            return;
        }
        if (i != NotificationCenter.dialogsUnreadReactionsCounterChanged) {
            if (i == NotificationCenter.emojiLoaded) {
                if (this.viewPages != null) {
                    int i6 = 0;
                    while (true) {
                        ViewPage[] viewPageArr2 = this.viewPages;
                        if (i6 >= viewPageArr2.length) {
                            break;
                        }
                        DialogsRecyclerView dialogsRecyclerView = viewPageArr2[i6].listView;
                        if (dialogsRecyclerView != null) {
                            for (int i7 = 0; i7 < dialogsRecyclerView.getChildCount(); i7++) {
                                View childAt = dialogsRecyclerView.getChildAt(i7);
                                if (childAt != null) {
                                    childAt.invalidate();
                                }
                            }
                        }
                        i6++;
                    }
                }
                FilterTabsView filterTabsView4 = this.filterTabsView;
                if (filterTabsView4 != null) {
                    filterTabsView4.getTabsContainer().invalidateViews();
                    return;
                }
                return;
            }
            if (i == NotificationCenter.closeSearchByActiveAction) {
                ActionBar actionBar = this.actionBar;
                if (actionBar != null) {
                    actionBar.closeSearchField();
                    return;
                }
                return;
            }
            if (i == NotificationCenter.proxySettingsChanged) {
                updateProxyButton(false, false);
                return;
            }
            if (i == NotificationCenter.updateInterfaces) {
                Integer num = (Integer) objArr[0];
                updateVisibleRows(num.intValue());
                FilterTabsView filterTabsView5 = this.filterTabsView;
                if (filterTabsView5 != null && filterTabsView5.getVisibility() == 0 && (num.intValue() & MessagesController.UPDATE_MASK_READ_DIALOG_MESSAGE) != 0) {
                    this.filterTabsView.checkTabsCounter();
                }
                if (this.viewPages != null) {
                    while (i4 < this.viewPages.length) {
                        if ((num.intValue() & MessagesController.UPDATE_MASK_STATUS) != 0) {
                            this.viewPages[i4].dialogsAdapter.sortOnlineContacts(true);
                        }
                        i4++;
                    }
                }
                user = UserConfig.getInstance(i2).getCurrentUser();
            } else {
                if (i == NotificationCenter.appDidLogout) {
                    dialogsLoaded[this.currentAccount] = false;
                    return;
                }
                if (i != NotificationCenter.encryptedChatUpdated) {
                    if (i != NotificationCenter.contactsDidLoad) {
                        if (i == NotificationCenter.openedChatChanged) {
                            if (this.viewPages == null) {
                                return;
                            }
                            int i8 = 0;
                            while (true) {
                                ViewPage[] viewPageArr3 = this.viewPages;
                                if (i8 >= viewPageArr3.length) {
                                    break;
                                }
                                if (viewPageArr3[i8].isDefaultDialogType() && AndroidUtilities.isTablet()) {
                                    boolean booleanValue2 = ((Boolean) objArr[2]).booleanValue();
                                    long longValue = ((Long) objArr[0]).longValue();
                                    long longValue2 = ((Long) objArr[1]).longValue();
                                    if (booleanValue2) {
                                        MessagesStorage.TopicKey topicKey = this.openedDialogId;
                                        if (longValue == topicKey.dialogId && longValue2 == topicKey.topicId) {
                                            topicKey.dialogId = 0L;
                                            topicKey.topicId = 0L;
                                        }
                                    } else {
                                        MessagesStorage.TopicKey topicKey2 = this.openedDialogId;
                                        topicKey2.dialogId = longValue;
                                        topicKey2.topicId = longValue2;
                                    }
                                    this.viewPages[i8].dialogsAdapter.setOpenedDialogId(this.openedDialogId.dialogId);
                                }
                                i8++;
                            }
                            i3 = MessagesController.UPDATE_MASK_SELECT_DIALOG;
                        } else if (i != NotificationCenter.notificationsSettingsUpdated) {
                            if (i == NotificationCenter.messageReceivedByAck || i == NotificationCenter.messageReceivedByServer || i == NotificationCenter.messageSendError) {
                                i3 = MessagesController.UPDATE_MASK_SEND_STATE;
                            } else {
                                if (i == NotificationCenter.didSetPasscode) {
                                    updatePasscodeButton();
                                    return;
                                }
                                if (i == NotificationCenter.needReloadRecentDialogsSearch) {
                                    SearchViewPager searchViewPager = this.searchViewPager;
                                    if (searchViewPager == null || (dialogsSearchAdapter2 = searchViewPager.dialogsSearchAdapter) == null) {
                                        return;
                                    }
                                    dialogsSearchAdapter2.loadRecentSearch();
                                    return;
                                }
                                if (i != NotificationCenter.replyMessagesDidLoad) {
                                    if (i == NotificationCenter.reloadHints) {
                                        SearchViewPager searchViewPager2 = this.searchViewPager;
                                        if (searchViewPager2 == null || (dialogsSearchAdapter = searchViewPager2.dialogsSearchAdapter) == null) {
                                            return;
                                        }
                                        dialogsSearchAdapter.notifyDataSetChanged();
                                        return;
                                    }
                                    if (i == NotificationCenter.didUpdateConnectionState) {
                                        int connectionState = AccountInstance.getInstance(i2).getConnectionsManager().getConnectionState();
                                        if (this.currentConnectionState != connectionState) {
                                            this.currentConnectionState = connectionState;
                                            updateProxyButton(true, false);
                                            return;
                                        }
                                        return;
                                    }
                                    if (i == NotificationCenter.onDownloadingFilesChanged) {
                                        updateProxyButton(true, false);
                                        SearchViewPager searchViewPager3 = this.searchViewPager;
                                        if (searchViewPager3 != null) {
                                            updateSpeedItem(searchViewPager3.getCurrentPosition() == 2);
                                            return;
                                        }
                                        return;
                                    }
                                    if (i == NotificationCenter.needDeleteDialog) {
                                        if (this.fragmentView == null || this.isPaused) {
                                            return;
                                        }
                                        final long longValue3 = ((Long) objArr[0]).longValue();
                                        final TLRPC.User user2 = (TLRPC.User) objArr[1];
                                        final TLRPC.Chat chat = (TLRPC.Chat) objArr[2];
                                        if (user2 == null || !user2.bot) {
                                            booleanValue = ((Boolean) objArr[3]).booleanValue();
                                            z = false;
                                        } else {
                                            z = ((Boolean) objArr[3]).booleanValue();
                                            booleanValue = false;
                                        }
                                        Runnable runnable = new Runnable() {
                                            @Override
                                            public final void run() {
                                                DialogsActivity.this.lambda$didReceivedNotification$106(chat, longValue3, booleanValue, user2, z);
                                            }
                                        };
                                        createUndoView();
                                        if (this.undoView[0] == null || ChatObject.isForum(chat)) {
                                            runnable.run();
                                            return;
                                        }
                                        UndoView undoView = getUndoView();
                                        if (undoView != null) {
                                            undoView.showWithAction(longValue3, 1, runnable);
                                            return;
                                        }
                                        return;
                                    }
                                    if (i == NotificationCenter.folderBecomeEmpty) {
                                        int intValue = ((Integer) objArr[0]).intValue();
                                        int i9 = this.folderId;
                                        if (i9 != intValue || i9 == 0) {
                                            return;
                                        }
                                        lambda$onBackPressed$319();
                                        return;
                                    }
                                    if (i == NotificationCenter.dialogFiltersUpdated) {
                                        updateFilterTabs(true, true);
                                        return;
                                    }
                                    if (i == NotificationCenter.filterSettingsUpdated) {
                                        showFiltersHint();
                                        return;
                                    }
                                    if (i != NotificationCenter.newSuggestionsAvailable) {
                                        if (i == NotificationCenter.forceImportContactsStart) {
                                            setFloatingProgressVisible(true, true);
                                            ViewPage[] viewPageArr4 = this.viewPages;
                                            if (viewPageArr4 != null) {
                                                for (ViewPage viewPage2 : viewPageArr4) {
                                                    viewPage2.dialogsAdapter.setForceShowEmptyCell(false);
                                                    viewPage2.dialogsAdapter.setForceUpdatingContacts(true);
                                                    viewPage2.dialogsAdapter.notifyDataSetChanged();
                                                }
                                                return;
                                            }
                                            return;
                                        }
                                        if (i == NotificationCenter.messagesDeleted) {
                                            if (!this.searchIsShowed || this.searchViewPager == null) {
                                                return;
                                            }
                                            this.searchViewPager.messagesDeleted(((Long) objArr[1]).longValue(), (ArrayList) objArr[0]);
                                            return;
                                        }
                                        if (i == NotificationCenter.didClearDatabase) {
                                            if (this.viewPages != null) {
                                                while (true) {
                                                    ViewPage[] viewPageArr5 = this.viewPages;
                                                    if (i4 >= viewPageArr5.length) {
                                                        break;
                                                    }
                                                    viewPageArr5[i4].dialogsAdapter.didDatabaseCleared();
                                                    i4++;
                                                }
                                            }
                                            SuggestClearDatabaseBottomSheet.dismissDialog();
                                            return;
                                        }
                                        if (i != NotificationCenter.appUpdateAvailable) {
                                            if (i == NotificationCenter.fileLoaded || i == NotificationCenter.fileLoadFailed || i == NotificationCenter.fileLoadProgressChanged) {
                                                String str = (String) objArr[0];
                                                if (!SharedConfig.isAppUpdateAvailable() || !FileLoader.getAttachFileName(SharedConfig.pendingAppUpdate.document).equals(str)) {
                                                    return;
                                                }
                                            } else {
                                                if (i == NotificationCenter.onDatabaseMigration) {
                                                    boolean booleanValue3 = ((Boolean) objArr[0]).booleanValue();
                                                    if (this.fragmentView != null) {
                                                        if (booleanValue3) {
                                                            if (this.databaseMigrationHint == null) {
                                                                DatabaseMigrationHint databaseMigrationHint = new DatabaseMigrationHint(this.fragmentView.getContext(), this.currentAccount);
                                                                this.databaseMigrationHint = databaseMigrationHint;
                                                                databaseMigrationHint.setAlpha(0.0f);
                                                                ((ContentView) this.fragmentView).addView(this.databaseMigrationHint);
                                                                this.databaseMigrationHint.animate().alpha(1.0f).setDuration(300L).setStartDelay(1000L).start();
                                                            }
                                                            this.databaseMigrationHint.setTag(1);
                                                            return;
                                                        }
                                                        View view = this.databaseMigrationHint;
                                                        if (view == null || view.getTag() == null) {
                                                            return;
                                                        }
                                                        final View view2 = this.databaseMigrationHint;
                                                        view2.animate().setListener(null).cancel();
                                                        view2.animate().setListener(new AnimatorListenerAdapter() {
                                                            @Override
                                                            public void onAnimationEnd(Animator animator) {
                                                                if (view2.getParent() != null) {
                                                                    ((ViewGroup) view2.getParent()).removeView(view2);
                                                                }
                                                                DialogsActivity.this.databaseMigrationHint = null;
                                                            }
                                                        }).alpha(0.0f).setStartDelay(0L).setDuration(150L).start();
                                                        this.databaseMigrationHint.setTag(null);
                                                        return;
                                                    }
                                                    return;
                                                }
                                                if (i == NotificationCenter.onDatabaseOpened) {
                                                    checkSuggestClearDatabase();
                                                    return;
                                                }
                                                if (i != NotificationCenter.userEmojiStatusUpdated) {
                                                    if (i == NotificationCenter.currentUserPremiumStatusChanged) {
                                                        updateStatus(UserConfig.getInstance(i2).getCurrentUser(), true);
                                                    } else {
                                                        if (i == NotificationCenter.onDatabaseReset) {
                                                            dialogsLoaded[this.currentAccount] = false;
                                                            loadDialogs(getAccountInstance());
                                                            getMessagesController().loadPinnedDialogs(this.folderId, 0L, null);
                                                            return;
                                                        }
                                                        if (i == NotificationCenter.chatlistFolderUpdate) {
                                                            int intValue2 = ((Integer) objArr[0]).intValue();
                                                            while (true) {
                                                                ViewPage[] viewPageArr6 = this.viewPages;
                                                                if (i4 >= viewPageArr6.length) {
                                                                    return;
                                                                }
                                                                ViewPage viewPage3 = viewPageArr6[i4];
                                                                if (viewPage3 != null && ((viewPage3.dialogsType == 7 || viewPage3.dialogsType == 8) && (dialogFilter = getMessagesController().selectedDialogFilter[viewPage3.dialogsType - 7]) != null && intValue2 == dialogFilter.id)) {
                                                                    viewPage3.updateList(true);
                                                                    return;
                                                                }
                                                                i4++;
                                                            }
                                                        } else if (i == NotificationCenter.dialogTranslate) {
                                                            long longValue4 = ((Long) objArr[0]).longValue();
                                                            int i10 = 0;
                                                            while (true) {
                                                                ViewPage[] viewPageArr7 = this.viewPages;
                                                                if (i10 >= viewPageArr7.length) {
                                                                    return;
                                                                }
                                                                ViewPage viewPage4 = viewPageArr7[i10];
                                                                if (viewPage4.listView != null) {
                                                                    int i11 = 0;
                                                                    while (true) {
                                                                        if (i11 < viewPage4.listView.getChildCount()) {
                                                                            View childAt2 = viewPage4.listView.getChildAt(i11);
                                                                            if (childAt2 instanceof DialogCell) {
                                                                                DialogCell dialogCell = (DialogCell) childAt2;
                                                                                if (longValue4 == dialogCell.getDialogId()) {
                                                                                    dialogCell.buildLayout();
                                                                                    break;
                                                                                }
                                                                            }
                                                                            i11++;
                                                                        }
                                                                    }
                                                                }
                                                                i10++;
                                                            }
                                                        } else if (i == NotificationCenter.storiesUpdated) {
                                                            updateStoriesVisibility(this.wasDrawn);
                                                        } else if (i != NotificationCenter.storiesEnabledUpdate) {
                                                            if (i != NotificationCenter.unconfirmedAuthUpdate && i != NotificationCenter.premiumPromoUpdated && i != NotificationCenter.starBalanceUpdated && i != NotificationCenter.starSubscriptionsLoaded) {
                                                                return;
                                                            }
                                                        }
                                                    }
                                                    updateStoriesPosting();
                                                    return;
                                                }
                                                user = (TLRPC.User) objArr[0];
                                            }
                                        }
                                        updateMenuButton(true);
                                        return;
                                    }
                                    showNextSupportedSuggestion();
                                    lambda$updateDialogsHint$29();
                                    return;
                                }
                                i3 = MessagesController.UPDATE_MASK_MESSAGE_TEXT;
                            }
                        }
                        updateVisibleRows(i3);
                        return;
                    }
                    if (this.viewPages == null || this.dialogsListFrozen) {
                        return;
                    }
                    boolean z3 = this.floatingProgressVisible;
                    setFloatingProgressVisible(false, true);
                    for (ViewPage viewPage5 : this.viewPages) {
                        viewPage5.dialogsAdapter.setForceUpdatingContacts(false);
                    }
                    if (z3) {
                        setContactsAlpha(0.0f);
                        animateContactsAlpha(1.0f);
                    }
                    int i12 = 0;
                    boolean z4 = false;
                    while (true) {
                        ViewPage[] viewPageArr8 = this.viewPages;
                        if (i12 >= viewPageArr8.length) {
                            break;
                        }
                        if (!viewPageArr8[i12].isDefaultDialogType() || getMessagesController().getAllFoldersDialogsCount() > 10) {
                            z4 = true;
                        } else {
                            this.viewPages[i12].dialogsAdapter.notifyDataSetChanged();
                        }
                        i12++;
                    }
                    if (!z4) {
                        return;
                    }
                }
            }
            updateStatus(user, true);
            return;
        }
        updateVisibleRows(0);
    }

    public void didSelectResult(long j, int i, boolean z, boolean z2) {
        didSelectResult(j, i, z, z2, null);
    }

    public void didSelectResult(final long r16, final int r18, boolean r19, final boolean r20, final org.telegram.ui.TopicsFragment r21) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.DialogsActivity.didSelectResult(long, int, boolean, boolean, org.telegram.ui.TopicsFragment):void");
    }

    @Override
    public boolean dismissDialogOnPause(Dialog dialog) {
        return !(dialog instanceof BotWebViewSheet) && super.dismissDialogOnPause(dialog);
    }

    @Override
    public void lambda$onBackPressed$319() {
        super.lambda$onBackPressed$319();
        ItemOptions itemOptions = this.filterOptions;
        if (itemOptions != null) {
            itemOptions.dismiss();
        }
    }

    public boolean getAllowGlobalSearch() {
        return this.allowGlobalSearch;
    }

    @Override
    public Animator getCustomSlideTransition(boolean z, boolean z2, float f) {
        if (z2) {
            ValueAnimator ofFloat = ValueAnimator.ofFloat(this.slideFragmentProgress, 1.0f);
            this.slideBackTransitionAnimator = ofFloat;
            return ofFloat;
        }
        int max = getLayoutContainer() != null ? (int) (Math.max((int) ((200.0f / getLayoutContainer().getMeasuredWidth()) * f), 80) * 1.2f) : 150;
        ValueAnimator ofFloat2 = ValueAnimator.ofFloat(this.slideFragmentProgress, 1.0f);
        this.slideBackTransitionAnimator = ofFloat2;
        ofFloat2.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                DialogsActivity.this.lambda$getCustomSlideTransition$131(valueAnimator);
            }
        });
        this.slideBackTransitionAnimator.setInterpolator(CubicBezierInterpolator.EASE_OUT);
        this.slideBackTransitionAnimator.setDuration(max);
        this.slideBackTransitionAnimator.start();
        return this.slideBackTransitionAnimator;
    }

    public ArrayList getDialogsArray(int i, int i2, int i3, boolean z) {
        boolean z2;
        ArrayList arrayList;
        if (z && (arrayList = this.frozenDialogsList) != null) {
            return arrayList;
        }
        MessagesController messagesController = AccountInstance.getInstance(i).getMessagesController();
        if (i2 == 0) {
            return messagesController.getDialogs(i3);
        }
        if (i2 == 10 || i2 == 13) {
            return messagesController.dialogsServerOnly;
        }
        boolean z3 = true;
        if (i2 == 2) {
            ArrayList arrayList2 = new ArrayList(messagesController.dialogsCanAddUsers.size() + messagesController.dialogsMyChannels.size() + messagesController.dialogsMyGroups.size() + 2);
            if (messagesController.dialogsMyChannels.size() > 0 && this.allowChannels) {
                arrayList2.add(new DialogsHeader(0));
                arrayList2.addAll(messagesController.dialogsMyChannels);
            }
            if (messagesController.dialogsMyGroups.size() > 0 && this.allowGroups) {
                arrayList2.add(new DialogsHeader(1));
                arrayList2.addAll(messagesController.dialogsMyGroups);
            }
            if (messagesController.dialogsCanAddUsers.size() > 0) {
                int size = messagesController.dialogsCanAddUsers.size();
                for (int i4 = 0; i4 < size; i4++) {
                    TLRPC.Dialog dialog = messagesController.dialogsCanAddUsers.get(i4);
                    if ((this.allowChannels && ChatObject.isChannelAndNotMegaGroup(-dialog.id, i)) || (this.allowGroups && (ChatObject.isMegagroup(i, -dialog.id) || !ChatObject.isChannel(-dialog.id, i)))) {
                        if (z3) {
                            arrayList2.add(new DialogsHeader(2));
                            z3 = false;
                        }
                        arrayList2.add(dialog);
                    }
                }
            }
            return arrayList2;
        }
        if (i2 == 3) {
            return messagesController.dialogsForward;
        }
        if (i2 == 4 || i2 == 12) {
            return messagesController.dialogsUsersOnly;
        }
        if (i2 == 5) {
            return messagesController.dialogsChannelsOnly;
        }
        if (i2 == 6 || i2 == 11) {
            return messagesController.dialogsGroupsOnly;
        }
        if (i2 == 7 || i2 == 8) {
            MessagesController.DialogFilter dialogFilter = messagesController.selectedDialogFilter[i2 != 7 ? (char) 1 : (char) 0];
            return dialogFilter == null ? messagesController.getDialogs(i3) : this.initialDialogsType == 3 ? dialogFilter.dialogsForward : dialogFilter.dialogs;
        }
        if (i2 == 9) {
            return messagesController.dialogsForBlock;
        }
        if (i2 == 1 || i2 == 14) {
            ArrayList arrayList3 = this.botShareDialogs;
            if (arrayList3 != null) {
                return arrayList3;
            }
            this.botShareDialogs = new ArrayList();
            if (this.allowUsers || this.allowBots) {
                Iterator<TLRPC.Dialog> it = messagesController.dialogsUsersOnly.iterator();
                while (it.hasNext()) {
                    TLRPC.Dialog next = it.next();
                    TLRPC.User user = messagesController.getUser(Long.valueOf(next.id));
                    if (user != null && !UserObject.isUserSelf(user)) {
                        if (user.bot) {
                            if (this.allowBots) {
                                this.botShareDialogs.add(next);
                            }
                        } else if (this.allowUsers) {
                            this.botShareDialogs.add(next);
                        }
                    }
                }
            }
            if (this.allowGroups || ((z2 = this.allowLegacyGroups) && this.allowMegagroups)) {
                Iterator<TLRPC.Dialog> it2 = messagesController.dialogsGroupsOnly.iterator();
                while (it2.hasNext()) {
                    TLRPC.Dialog next2 = it2.next();
                    TLRPC.Chat chat = messagesController.getChat(Long.valueOf(-next2.id));
                    if (chat != null && !ChatObject.isChannelAndNotMegaGroup(chat) && messagesController.canAddToForward(next2)) {
                        this.botShareDialogs.add(next2);
                    }
                }
            } else if (z2 || this.allowMegagroups) {
                Iterator<TLRPC.Dialog> it3 = messagesController.dialogsGroupsOnly.iterator();
                while (it3.hasNext()) {
                    TLRPC.Dialog next3 = it3.next();
                    TLRPC.Chat chat2 = messagesController.getChat(Long.valueOf(-next3.id));
                    if (chat2 != null && !ChatObject.isChannelAndNotMegaGroup(chat2) && messagesController.canAddToForward(next3) && ((this.allowLegacyGroups && !ChatObject.isMegagroup(chat2)) || (this.allowMegagroups && ChatObject.isMegagroup(chat2)))) {
                        this.botShareDialogs.add(next3);
                    }
                }
            }
            if (this.allowChannels) {
                Iterator<TLRPC.Dialog> it4 = messagesController.dialogsChannelsOnly.iterator();
                while (it4.hasNext()) {
                    TLRPC.Dialog next4 = it4.next();
                    if (messagesController.canAddToForward(next4)) {
                        this.botShareDialogs.add(next4);
                    }
                }
            }
            getMessagesController().sortDialogsList(this.botShareDialogs);
            return this.botShareDialogs;
        }
        if (i2 != 15) {
            return new ArrayList();
        }
        ArrayList arrayList4 = new ArrayList();
        TLRPC.User user2 = messagesController.getUser(Long.valueOf(this.requestPeerBotId));
        TLRPC.RequestPeerType requestPeerType = this.requestPeerType;
        if (requestPeerType instanceof TLRPC.TL_requestPeerTypeUser) {
            ConcurrentHashMap<Long, TLRPC.User> users = messagesController.getUsers();
            Iterator<TLRPC.Dialog> it5 = messagesController.dialogsUsersOnly.iterator();
            while (it5.hasNext()) {
                TLRPC.Dialog next5 = it5.next();
                if (meetRequestPeerRequirements(getMessagesController().getUser(Long.valueOf(next5.id)))) {
                    arrayList4.add(next5);
                }
            }
            for (TLRPC.User user3 : users.values()) {
                if (user3 != null && !messagesController.dialogs_dict.containsKey(user3.id) && meetRequestPeerRequirements(user3)) {
                    TLRPC.TL_dialog tL_dialog = new TLRPC.TL_dialog();
                    TLRPC.TL_peerUser tL_peerUser = new TLRPC.TL_peerUser();
                    tL_dialog.peer = tL_peerUser;
                    long j = user3.id;
                    tL_peerUser.user_id = j;
                    tL_dialog.id = j;
                    arrayList4.add(tL_dialog);
                }
            }
        } else if ((requestPeerType instanceof TLRPC.TL_requestPeerTypeChat) || (requestPeerType instanceof TLRPC.TL_requestPeerTypeBroadcast)) {
            ConcurrentHashMap<Long, TLRPC.Chat> chats = messagesController.getChats();
            Iterator<TLRPC.Dialog> it6 = (this.requestPeerType instanceof TLRPC.TL_requestPeerTypeChat ? messagesController.dialogsGroupsOnly : messagesController.dialogsChannelsOnly).iterator();
            while (it6.hasNext()) {
                TLRPC.Dialog next6 = it6.next();
                if (meetRequestPeerRequirements(user2, getMessagesController().getChat(Long.valueOf(-next6.id)))) {
                    arrayList4.add(next6);
                }
            }
            for (TLRPC.Chat chat3 : chats.values()) {
                if (chat3 != null && !messagesController.dialogs_dict.containsKey(-chat3.id) && meetRequestPeerRequirements(user2, chat3)) {
                    TLRPC.TL_dialog tL_dialog2 = new TLRPC.TL_dialog();
                    if (ChatObject.isChannel(chat3)) {
                        TLRPC.TL_peerChannel tL_peerChannel = new TLRPC.TL_peerChannel();
                        tL_dialog2.peer = tL_peerChannel;
                        tL_peerChannel.channel_id = chat3.id;
                    } else {
                        TLRPC.TL_peerChat tL_peerChat = new TLRPC.TL_peerChat();
                        tL_dialog2.peer = tL_peerChat;
                        tL_peerChat.chat_id = chat3.id;
                    }
                    tL_dialog2.id = -chat3.id;
                    arrayList4.add(tL_dialog2);
                }
            }
        }
        return arrayList4;
    }

    public RLottieImageView getFloatingButton() {
        return this.floatingButton;
    }

    public RecyclerListView getListView() {
        return this.viewPages[0].listView;
    }

    public MessagesStorage.TopicKey getOpenedDialogId() {
        return this.openedDialogId;
    }

    public ActionBarMenuItem getSearchItem() {
        return this.searchItem;
    }

    public RecyclerListView getSearchListView() {
        createSearchViewPager();
        SearchViewPager searchViewPager = this.searchViewPager;
        if (searchViewPager != null) {
            return searchViewPager.searchListView;
        }
        return null;
    }

    @Override
    public ArrayList getThemeDescriptions() {
        ArrayList arrayList;
        ThemeDescription.ThemeDescriptionDelegate themeDescriptionDelegate;
        ThemeDescription themeDescription;
        String str;
        ThemeDescription themeDescription2;
        Class<DrawerAddCell> cls;
        Class<DrawerActionCell> cls2;
        String str2;
        String str3;
        Class<DialogsEmptyCell> cls3;
        RecyclerListView recyclerListView;
        int i;
        ThemeDescription themeDescription3;
        final DialogsActivity dialogsActivity = this;
        Class<DrawerAddCell> cls4 = DrawerAddCell.class;
        Class<DrawerActionCell> cls5 = DrawerActionCell.class;
        String str4 = "imageView";
        String str5 = "nameTextView";
        Class<DialogsEmptyCell> cls6 = DialogsEmptyCell.class;
        int i2 = 1;
        ThemeDescription.ThemeDescriptionDelegate themeDescriptionDelegate2 = new ThemeDescription.ThemeDescriptionDelegate() {
            @Override
            public final void didSetColor() {
                DialogsActivity.this.lambda$getThemeDescriptions$128();
            }

            @Override
            public void onAnimationProgress(float f) {
                ThemeDescription.ThemeDescriptionDelegate.CC.$default$onAnimationProgress(this, f);
            }
        };
        ArrayList arrayList2 = new ArrayList();
        View view = dialogsActivity.fragmentView;
        int i3 = ThemeDescription.FLAG_BACKGROUND;
        int i4 = Theme.key_windowBackgroundWhite;
        arrayList2.add(new ThemeDescription(view, i3, null, null, null, null, i4));
        if (dialogsActivity.movingView != null) {
            arrayList2.add(new ThemeDescription(dialogsActivity.movingView, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, i4));
        }
        if (dialogsActivity.doneItem != null) {
            arrayList2.add(new ThemeDescription(dialogsActivity.doneItem, ThemeDescription.FLAG_BACKGROUNDFILTER, null, null, null, null, Theme.key_actionBarDefaultSelector));
        }
        if (dialogsActivity.folderId == 0) {
            if (dialogsActivity.onlySelect) {
                arrayList2.add(new ThemeDescription(dialogsActivity.actionBar, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, Theme.key_actionBarDefault));
            }
            View view2 = dialogsActivity.fragmentView;
            Paint paint = dialogsActivity.actionBarDefaultPaint;
            int i5 = Theme.key_actionBarDefault;
            arrayList2.add(new ThemeDescription(view2, 0, null, paint, null, null, i5));
            if (dialogsActivity.searchViewPager != null) {
                arrayList2.add(new ThemeDescription(dialogsActivity.searchViewPager.searchListView, ThemeDescription.FLAG_LISTGLOWCOLOR, null, null, null, null, i5));
            }
            arrayList = arrayList2;
            themeDescriptionDelegate = themeDescriptionDelegate2;
            arrayList.add(new ThemeDescription(dialogsActivity.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, null, null, null, themeDescriptionDelegate, Theme.key_actionBarDefaultIcon));
            arrayList.add(new ThemeDescription(dialogsActivity.actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, null, null, new Drawable[]{Theme.dialogs_holidayDrawable}, null, Theme.key_actionBarDefaultTitle));
            arrayList.add(new ThemeDescription(dialogsActivity.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, null, null, null, null, Theme.key_actionBarDefaultSelector));
            arrayList.add(new ThemeDescription(dialogsActivity.actionBar, ThemeDescription.FLAG_AB_SEARCH, null, null, null, null, Theme.key_actionBarDefaultSearch));
            themeDescription = new ThemeDescription(dialogsActivity.actionBar, ThemeDescription.FLAG_AB_SEARCHPLACEHOLDER, null, null, null, null, Theme.key_actionBarDefaultSearchPlaceholder);
        } else {
            arrayList = arrayList2;
            themeDescriptionDelegate = themeDescriptionDelegate2;
            View view3 = dialogsActivity.fragmentView;
            Paint paint2 = dialogsActivity.actionBarDefaultPaint;
            int i6 = Theme.key_actionBarDefaultArchived;
            arrayList.add(new ThemeDescription(view3, 0, null, paint2, null, null, i6));
            if (dialogsActivity.searchViewPager != null) {
                arrayList.add(new ThemeDescription(dialogsActivity.searchViewPager.searchListView, ThemeDescription.FLAG_LISTGLOWCOLOR, null, null, null, null, i6));
            }
            arrayList.add(new ThemeDescription(dialogsActivity.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, null, null, null, null, Theme.key_actionBarDefaultArchivedIcon));
            arrayList.add(new ThemeDescription(dialogsActivity.actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, null, null, new Drawable[]{Theme.dialogs_holidayDrawable}, null, Theme.key_actionBarDefaultArchivedTitle));
            arrayList.add(new ThemeDescription(dialogsActivity.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, null, null, null, null, Theme.key_actionBarDefaultArchivedSelector));
            arrayList.add(new ThemeDescription(dialogsActivity.actionBar, ThemeDescription.FLAG_AB_SEARCH, null, null, null, null, Theme.key_actionBarDefaultArchivedSearch));
            themeDescription = new ThemeDescription(dialogsActivity.actionBar, ThemeDescription.FLAG_AB_SEARCHPLACEHOLDER, null, null, null, null, Theme.key_actionBarDefaultArchivedSearchPlaceholder);
        }
        arrayList.add(themeDescription);
        ActionBar actionBar = dialogsActivity.actionBar;
        int i7 = ThemeDescription.FLAG_AB_AM_ITEMSCOLOR;
        int i8 = Theme.key_actionBarActionModeDefaultIcon;
        arrayList.add(new ThemeDescription(actionBar, i7, null, null, null, null, i8));
        arrayList.add(new ThemeDescription(dialogsActivity.actionBar, ThemeDescription.FLAG_AB_AM_TOPBACKGROUND, null, null, null, null, Theme.key_actionBarActionModeDefaultTop));
        arrayList.add(new ThemeDescription(dialogsActivity.actionBar, ThemeDescription.FLAG_AB_AM_SELECTORCOLOR, null, null, null, null, Theme.key_actionBarActionModeDefaultSelector));
        arrayList.add(new ThemeDescription(dialogsActivity.selectedDialogsCountTextView, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, i8));
        ThemeDescription.ThemeDescriptionDelegate themeDescriptionDelegate3 = themeDescriptionDelegate;
        arrayList.add(new ThemeDescription(null, 0, null, null, null, themeDescriptionDelegate3, Theme.key_actionBarDefaultSubmenuBackground));
        arrayList.add(new ThemeDescription(null, 0, null, null, null, themeDescriptionDelegate3, Theme.key_actionBarDefaultSubmenuItem));
        arrayList.add(new ThemeDescription(null, 0, null, null, null, themeDescriptionDelegate3, Theme.key_actionBarDefaultSubmenuItemIcon));
        arrayList.add(new ThemeDescription(null, 0, null, null, null, themeDescriptionDelegate3, Theme.key_dialogButtonSelector));
        if (dialogsActivity.filterTabsView != null) {
            if (dialogsActivity.actionBar.isActionModeShowed()) {
                str = "frameLayout";
                arrayList.add(new ThemeDescription(dialogsActivity.filterTabsView, 0, new Class[]{FilterTabsView.class}, new String[]{"selectorDrawable"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_profile_tabSelectedLine));
                arrayList.add(new ThemeDescription(dialogsActivity.filterTabsView.getTabsContainer(), ThemeDescription.FLAG_TEXTCOLOR | ThemeDescription.FLAG_CHECKTAG, new Class[]{FilterTabsView.TabView.class}, null, null, null, Theme.key_profile_tabSelectedText));
                arrayList.add(new ThemeDescription(dialogsActivity.filterTabsView.getTabsContainer(), ThemeDescription.FLAG_TEXTCOLOR | ThemeDescription.FLAG_CHECKTAG, new Class[]{FilterTabsView.TabView.class}, null, null, null, Theme.key_profile_tabText));
                themeDescription3 = new ThemeDescription(dialogsActivity.filterTabsView.getTabsContainer(), ThemeDescription.FLAG_BACKGROUNDFILTER | ThemeDescription.FLAG_DRAWABLESELECTEDSTATE, new Class[]{FilterTabsView.TabView.class}, null, null, null, Theme.key_profile_tabSelector);
            } else {
                str = "frameLayout";
                arrayList.add(new ThemeDescription(dialogsActivity.filterTabsView, 0, new Class[]{FilterTabsView.class}, new String[]{"selectorDrawable"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_actionBarTabLine));
                arrayList.add(new ThemeDescription(dialogsActivity.filterTabsView.getTabsContainer(), ThemeDescription.FLAG_TEXTCOLOR | ThemeDescription.FLAG_CHECKTAG, new Class[]{FilterTabsView.TabView.class}, null, null, null, Theme.key_actionBarTabActiveText));
                arrayList.add(new ThemeDescription(dialogsActivity.filterTabsView.getTabsContainer(), ThemeDescription.FLAG_TEXTCOLOR | ThemeDescription.FLAG_CHECKTAG, new Class[]{FilterTabsView.TabView.class}, null, null, null, Theme.key_actionBarTabUnactiveText));
                themeDescription3 = new ThemeDescription(dialogsActivity.filterTabsView.getTabsContainer(), ThemeDescription.FLAG_SELECTOR, null, null, null, null, Theme.key_actionBarTabSelector);
            }
            arrayList.add(themeDescription3);
            arrayList.add(new ThemeDescription(dialogsActivity.filterTabsView.getTabsContainer(), 0, new Class[]{FilterTabsView.TabView.class}, null, null, null, Theme.key_chats_tabUnreadActiveBackground));
            arrayList.add(new ThemeDescription(dialogsActivity.filterTabsView.getTabsContainer(), 0, new Class[]{FilterTabsView.TabView.class}, null, null, null, Theme.key_chats_tabUnreadUnactiveBackground));
        } else {
            str = "frameLayout";
        }
        arrayList.add(new ThemeDescription(dialogsActivity.floatingButton, ThemeDescription.FLAG_IMAGECOLOR, null, null, null, null, Theme.key_chats_actionIcon));
        arrayList.add(new ThemeDescription(dialogsActivity.floatingButton, ThemeDescription.FLAG_BACKGROUNDFILTER, null, null, null, null, Theme.key_chats_actionBackground));
        arrayList.add(new ThemeDescription(dialogsActivity.floatingButton, ThemeDescription.FLAG_BACKGROUNDFILTER | ThemeDescription.FLAG_DRAWABLESELECTEDSTATE, null, null, null, null, Theme.key_chats_actionPressedBackground));
        arrayList.addAll(SimpleThemeDescription.createThemeDescriptions(new ThemeDescription.ThemeDescriptionDelegate() {
            @Override
            public final void didSetColor() {
                DialogsActivity.this.lambda$getThemeDescriptions$129();
            }

            @Override
            public void onAnimationProgress(float f) {
                ThemeDescription.ThemeDescriptionDelegate.CC.$default$onAnimationProgress(this, f);
            }
        }, Theme.key_actionBarActionModeDefault, i8));
        int i9 = 0;
        while (true) {
            if (i9 >= 3) {
                break;
            }
            if (i9 == 2) {
                SearchViewPager searchViewPager = dialogsActivity.searchViewPager;
                if (searchViewPager != null) {
                    recyclerListView = searchViewPager.searchListView;
                }
                cls = cls4;
                cls2 = cls5;
                str2 = str4;
                str3 = str5;
                cls3 = cls6;
                i = 1;
                i9 += i;
                i2 = 1;
                dialogsActivity = this;
                cls4 = cls;
                cls5 = cls2;
                str4 = str2;
                str5 = str3;
                cls6 = cls3;
            } else {
                ViewPage[] viewPageArr = dialogsActivity.viewPages;
                if (viewPageArr != null) {
                    recyclerListView = i9 < viewPageArr.length ? viewPageArr[i9].listView : null;
                } else {
                    cls = cls4;
                    cls2 = cls5;
                    str2 = str4;
                    str3 = str5;
                    cls3 = cls6;
                    i = 1;
                    i9 += i;
                    i2 = 1;
                    dialogsActivity = this;
                    cls4 = cls;
                    cls5 = cls2;
                    str4 = str2;
                    str5 = str3;
                    cls6 = cls3;
                }
            }
            if (recyclerListView != null) {
                RecyclerListView recyclerListView2 = recyclerListView;
                arrayList.add(new ThemeDescription(recyclerListView2, ThemeDescription.FLAG_SELECTOR, null, null, null, null, Theme.key_listSelector));
                Class[] clsArr = new Class[i2];
                clsArr[0] = View.class;
                arrayList.add(new ThemeDescription(recyclerListView2, 0, clsArr, Theme.dividerPaint, null, null, Theme.key_divider));
                cls = cls4;
                arrayList.add(new ThemeDescription(recyclerListView2, 0, new Class[]{DialogCell.class, ProfileSearchCell.class}, null, Theme.avatarDrawables, null, Theme.key_avatar_text));
                arrayList.add(new ThemeDescription(recyclerListView2, 0, new Class[]{DialogCell.class}, Theme.dialogs_countPaint, null, null, Theme.key_chats_unreadCounter));
                arrayList.add(new ThemeDescription(recyclerListView2, 0, new Class[]{DialogCell.class}, Theme.dialogs_countGrayPaint, null, null, Theme.key_chats_unreadCounterMuted));
                arrayList.add(new ThemeDescription(recyclerListView2, 0, new Class[]{DialogCell.class}, Theme.dialogs_countTextPaint, null, null, Theme.key_chats_unreadCounterText));
                cls2 = cls5;
                arrayList.add(new ThemeDescription(recyclerListView2, 0, new Class[]{DialogCell.class, ProfileSearchCell.class}, null, new Drawable[]{Theme.dialogs_lockDrawable}, null, Theme.key_chats_secretIcon));
                str2 = str4;
                Drawable[] drawableArr = {Theme.dialogs_scamDrawable, Theme.dialogs_fakeDrawable};
                int i10 = Theme.key_chats_draft;
                arrayList.add(new ThemeDescription(recyclerListView2, 0, new Class[]{DialogCell.class, ProfileSearchCell.class}, null, drawableArr, null, i10));
                str3 = str5;
                arrayList.add(new ThemeDescription(recyclerListView2, 0, new Class[]{DialogCell.class}, null, new Drawable[]{Theme.dialogs_pinnedDrawable, Theme.dialogs_reorderDrawable}, null, Theme.key_chats_pinnedIcon));
                TextPaint[] textPaintArr = Theme.dialogs_namePaint;
                cls3 = cls6;
                arrayList.add(new ThemeDescription(recyclerListView2, 0, new Class[]{DialogCell.class, ProfileSearchCell.class}, (String[]) null, new Paint[]{textPaintArr[0], textPaintArr[1], Theme.dialogs_searchNamePaint}, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_chats_name));
                TextPaint[] textPaintArr2 = Theme.dialogs_nameEncryptedPaint;
                arrayList.add(new ThemeDescription(recyclerListView2, 0, new Class[]{DialogCell.class, ProfileSearchCell.class}, (String[]) null, new Paint[]{textPaintArr2[0], textPaintArr2[1], Theme.dialogs_searchNameEncryptedPaint}, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_chats_secretName));
                arrayList.add(new ThemeDescription(recyclerListView2, 0, new Class[]{DialogCell.class}, Theme.dialogs_messagePaint[1], null, null, Theme.key_chats_message_threeLines));
                arrayList.add(new ThemeDescription(recyclerListView2, 0, new Class[]{DialogCell.class}, Theme.dialogs_messagePaint[0], null, null, Theme.key_chats_message));
                arrayList.add(new ThemeDescription(recyclerListView2, 0, new Class[]{DialogCell.class}, Theme.dialogs_messageNamePaint, null, null, Theme.key_chats_nameMessage_threeLines));
                arrayList.add(new ThemeDescription(recyclerListView2, 0, new Class[]{DialogCell.class}, null, null, null, i10));
                arrayList.add(new ThemeDescription(recyclerListView2, 0, new Class[]{DialogCell.class}, (String[]) null, Theme.dialogs_messagePrintingPaint, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_chats_actionMessage));
                arrayList.add(new ThemeDescription(recyclerListView2, 0, new Class[]{DialogCell.class}, Theme.dialogs_timePaint, null, null, Theme.key_chats_date));
                arrayList.add(new ThemeDescription(recyclerListView2, 0, new Class[]{DialogCell.class}, Theme.dialogs_pinnedPaint, null, null, Theme.key_chats_pinnedOverlay));
                arrayList.add(new ThemeDescription(recyclerListView2, 0, new Class[]{DialogCell.class}, Theme.dialogs_tabletSeletedPaint, null, null, Theme.key_chats_tabletSelectedOverlay));
                arrayList.add(new ThemeDescription(recyclerListView2, 0, new Class[]{DialogCell.class}, null, new Drawable[]{Theme.dialogs_checkDrawable}, null, Theme.key_chats_sentCheck));
                arrayList.add(new ThemeDescription(recyclerListView2, 0, new Class[]{DialogCell.class}, null, new Drawable[]{Theme.dialogs_checkReadDrawable, Theme.dialogs_halfCheckDrawable}, null, Theme.key_chats_sentReadCheck));
                arrayList.add(new ThemeDescription(recyclerListView2, 0, new Class[]{DialogCell.class}, null, new Drawable[]{Theme.dialogs_clockDrawable}, null, Theme.key_chats_sentClock));
                arrayList.add(new ThemeDescription(recyclerListView2, 0, new Class[]{DialogCell.class}, Theme.dialogs_errorPaint, null, null, Theme.key_chats_sentError));
                arrayList.add(new ThemeDescription(recyclerListView2, 0, new Class[]{DialogCell.class}, null, new Drawable[]{Theme.dialogs_errorDrawable}, null, Theme.key_chats_sentErrorIcon));
                arrayList.add(new ThemeDescription(recyclerListView2, 0, new Class[]{DialogCell.class, ProfileSearchCell.class}, null, new Drawable[]{Theme.dialogs_verifiedCheckDrawable}, null, Theme.key_chats_verifiedCheck));
                arrayList.add(new ThemeDescription(recyclerListView2, 0, new Class[]{DialogCell.class, ProfileSearchCell.class}, null, new Drawable[]{Theme.dialogs_verifiedDrawable}, null, Theme.key_chats_verifiedBackground));
                arrayList.add(new ThemeDescription(recyclerListView2, 0, new Class[]{DialogCell.class}, null, new Drawable[]{Theme.dialogs_muteDrawable}, null, Theme.key_chats_muteIcon));
                arrayList.add(new ThemeDescription(recyclerListView2, 0, new Class[]{DialogCell.class}, null, new Drawable[]{Theme.dialogs_mentionDrawable}, null, Theme.key_chats_mentionIcon));
                arrayList.add(new ThemeDescription(recyclerListView2, 0, new Class[]{DialogCell.class}, null, null, null, Theme.key_chats_archivePinBackground));
                arrayList.add(new ThemeDescription(recyclerListView2, 0, new Class[]{DialogCell.class}, null, null, null, Theme.key_chats_archiveBackground));
                arrayList.add(new ThemeDescription(recyclerListView2, 0, new Class[]{DialogCell.class}, null, null, null, Theme.key_chats_onlineCircle));
                int i11 = Theme.key_windowBackgroundWhite;
                arrayList.add(new ThemeDescription(recyclerListView2, 0, new Class[]{DialogCell.class}, null, null, null, i11));
                arrayList.add(new ThemeDescription(recyclerListView2, ThemeDescription.FLAG_CHECKBOX, new Class[]{DialogCell.class}, new String[]{"checkBox"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, i11));
                arrayList.add(new ThemeDescription(recyclerListView2, ThemeDescription.FLAG_CHECKBOXCHECK, new Class[]{DialogCell.class}, new String[]{"checkBox"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_checkboxCheck));
                arrayList.add(new ThemeDescription(recyclerListView2, 0, new Class[]{LoadingCell.class}, new String[]{"progressBar"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_progressCircle));
                arrayList.add(new ThemeDescription(recyclerListView2, 0, new Class[]{ProfileSearchCell.class}, Theme.dialogs_offlinePaint, null, null, Theme.key_windowBackgroundWhiteGrayText3));
                arrayList.add(new ThemeDescription(recyclerListView2, 0, new Class[]{ProfileSearchCell.class}, Theme.dialogs_onlinePaint, null, null, Theme.key_windowBackgroundWhiteBlueText3));
                GraySectionCell.createThemeDescriptions(arrayList, recyclerListView);
                arrayList.add(new ThemeDescription(recyclerListView2, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{HashtagSearchCell.class}, null, null, null, Theme.key_windowBackgroundWhiteBlackText));
                int i12 = Theme.key_windowBackgroundGrayShadow;
                arrayList.add(new ThemeDescription(recyclerListView2, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{ShadowSectionCell.class}, null, null, null, i12));
                int i13 = Theme.key_windowBackgroundGray;
                arrayList.add(new ThemeDescription(recyclerListView2, ThemeDescription.FLAG_BACKGROUNDFILTER | ThemeDescription.FLAG_CELLBACKGROUNDCOLOR, new Class[]{ShadowSectionCell.class}, null, null, null, i13));
                arrayList.add(new ThemeDescription(recyclerListView2, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{TextInfoPrivacyCell.class}, null, null, null, i12));
                arrayList.add(new ThemeDescription(recyclerListView2, ThemeDescription.FLAG_BACKGROUNDFILTER | ThemeDescription.FLAG_CELLBACKGROUNDCOLOR, new Class[]{TextInfoPrivacyCell.class}, null, null, null, i13));
                arrayList.add(new ThemeDescription(recyclerListView2, 0, new Class[]{TextInfoPrivacyCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteGrayText4));
                arrayList.add(new ThemeDescription(recyclerListView2, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{TextCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteBlueText2));
                i = 1;
                i9 += i;
                i2 = 1;
                dialogsActivity = this;
                cls4 = cls;
                cls5 = cls2;
                str4 = str2;
                str5 = str3;
                cls6 = cls3;
            }
            cls = cls4;
            cls2 = cls5;
            str2 = str4;
            str3 = str5;
            cls3 = cls6;
            i = 1;
            i9 += i;
            i2 = 1;
            dialogsActivity = this;
            cls4 = cls;
            cls5 = cls2;
            str4 = str2;
            str5 = str3;
            cls6 = cls3;
        }
        Class<DrawerAddCell> cls7 = cls4;
        Class<DrawerActionCell> cls8 = cls5;
        String str6 = str4;
        String str7 = str5;
        Class<DialogsEmptyCell> cls9 = cls6;
        int i14 = Theme.key_avatar_backgroundRed;
        ThemeDescription.ThemeDescriptionDelegate themeDescriptionDelegate4 = themeDescriptionDelegate;
        arrayList.add(new ThemeDescription(null, 0, null, null, null, themeDescriptionDelegate4, i14));
        arrayList.add(new ThemeDescription(null, 0, null, null, null, themeDescriptionDelegate4, Theme.key_avatar_backgroundOrange));
        arrayList.add(new ThemeDescription(null, 0, null, null, null, themeDescriptionDelegate4, Theme.key_avatar_backgroundViolet));
        arrayList.add(new ThemeDescription(null, 0, null, null, null, themeDescriptionDelegate4, Theme.key_avatar_backgroundGreen));
        arrayList.add(new ThemeDescription(null, 0, null, null, null, themeDescriptionDelegate4, Theme.key_avatar_backgroundCyan));
        arrayList.add(new ThemeDescription(null, 0, null, null, null, themeDescriptionDelegate4, Theme.key_avatar_backgroundBlue));
        arrayList.add(new ThemeDescription(null, 0, null, null, null, themeDescriptionDelegate4, Theme.key_avatar_backgroundPink));
        arrayList.add(new ThemeDescription(null, 0, null, null, null, themeDescriptionDelegate4, Theme.key_avatar_backgroundSaved));
        arrayList.add(new ThemeDescription(null, 0, null, null, null, themeDescriptionDelegate4, i14));
        arrayList.add(new ThemeDescription(null, 0, null, null, null, themeDescriptionDelegate4, Theme.key_avatar_background2Red));
        arrayList.add(new ThemeDescription(null, 0, null, null, null, themeDescriptionDelegate4, Theme.key_avatar_background2Orange));
        arrayList.add(new ThemeDescription(null, 0, null, null, null, themeDescriptionDelegate4, Theme.key_avatar_background2Violet));
        arrayList.add(new ThemeDescription(null, 0, null, null, null, themeDescriptionDelegate4, Theme.key_avatar_background2Green));
        arrayList.add(new ThemeDescription(null, 0, null, null, null, themeDescriptionDelegate4, Theme.key_avatar_background2Cyan));
        arrayList.add(new ThemeDescription(null, 0, null, null, null, themeDescriptionDelegate4, Theme.key_avatar_background2Blue));
        arrayList.add(new ThemeDescription(null, 0, null, null, null, themeDescriptionDelegate4, Theme.key_avatar_background2Pink));
        arrayList.add(new ThemeDescription(null, 0, null, null, null, themeDescriptionDelegate4, Theme.key_avatar_background2Saved));
        arrayList.add(new ThemeDescription(null, 0, null, null, null, themeDescriptionDelegate4, Theme.key_avatar_backgroundArchived));
        arrayList.add(new ThemeDescription(null, 0, null, null, null, themeDescriptionDelegate4, Theme.key_avatar_backgroundArchivedHidden));
        arrayList.add(new ThemeDescription(null, 0, null, null, null, themeDescriptionDelegate4, Theme.key_chats_nameMessage));
        arrayList.add(new ThemeDescription(null, 0, null, null, null, themeDescriptionDelegate4, Theme.key_chats_draft));
        arrayList.add(new ThemeDescription(null, 0, null, null, null, themeDescriptionDelegate4, Theme.key_chats_attachMessage));
        arrayList.add(new ThemeDescription(null, 0, null, null, null, themeDescriptionDelegate4, Theme.key_chats_nameArchived));
        arrayList.add(new ThemeDescription(null, 0, null, null, null, themeDescriptionDelegate4, Theme.key_chats_nameMessageArchived));
        arrayList.add(new ThemeDescription(null, 0, null, null, null, themeDescriptionDelegate4, Theme.key_chats_nameMessageArchived_threeLines));
        arrayList.add(new ThemeDescription(null, 0, null, null, null, themeDescriptionDelegate4, Theme.key_chats_messageArchived));
        if (this.viewPages != null) {
            for (int i15 = 0; i15 < this.viewPages.length; i15++) {
                arrayList.add(this.folderId == 0 ? new ThemeDescription(this.viewPages[i15].listView, ThemeDescription.FLAG_LISTGLOWCOLOR, null, null, null, null, Theme.key_actionBarDefault) : new ThemeDescription(this.viewPages[i15].listView, ThemeDescription.FLAG_LISTGLOWCOLOR, null, null, null, null, Theme.key_actionBarDefaultArchived));
                int i16 = Theme.key_chats_nameMessage_threeLines;
                arrayList.add(new ThemeDescription(this.viewPages[i15].listView, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{cls9}, new String[]{"emptyTextView1"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, i16));
                int i17 = Theme.key_chats_message;
                arrayList.add(new ThemeDescription(this.viewPages[i15].listView, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{cls9}, new String[]{"emptyTextView2"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, i17));
                if (SharedConfig.archiveHidden) {
                    RLottieDrawable[] rLottieDrawableArr = {Theme.dialogs_archiveAvatarDrawable};
                    int i18 = Theme.key_avatar_backgroundArchivedHidden;
                    arrayList.add(new ThemeDescription(this.viewPages[i15].listView, 0, new Class[]{DialogCell.class}, rLottieDrawableArr, "Arrow1", i18));
                    themeDescription2 = new ThemeDescription(this.viewPages[i15].listView, 0, new Class[]{DialogCell.class}, new RLottieDrawable[]{Theme.dialogs_archiveAvatarDrawable}, "Arrow2", i18);
                } else {
                    RLottieDrawable[] rLottieDrawableArr2 = {Theme.dialogs_archiveAvatarDrawable};
                    int i19 = Theme.key_avatar_backgroundArchived;
                    arrayList.add(new ThemeDescription(this.viewPages[i15].listView, 0, new Class[]{DialogCell.class}, rLottieDrawableArr2, "Arrow1", i19));
                    themeDescription2 = new ThemeDescription(this.viewPages[i15].listView, 0, new Class[]{DialogCell.class}, new RLottieDrawable[]{Theme.dialogs_archiveAvatarDrawable}, "Arrow2", i19);
                }
                arrayList.add(themeDescription2);
                RLottieDrawable[] rLottieDrawableArr3 = {Theme.dialogs_archiveAvatarDrawable};
                int i20 = Theme.key_avatar_text;
                arrayList.add(new ThemeDescription(this.viewPages[i15].listView, 0, new Class[]{DialogCell.class}, rLottieDrawableArr3, "Box2", i20));
                arrayList.add(new ThemeDescription(this.viewPages[i15].listView, 0, new Class[]{DialogCell.class}, new RLottieDrawable[]{Theme.dialogs_archiveAvatarDrawable}, "Box1", i20));
                RLottieDrawable[] rLottieDrawableArr4 = {Theme.dialogs_pinArchiveDrawable};
                int i21 = Theme.key_chats_archiveIcon;
                arrayList.add(new ThemeDescription(this.viewPages[i15].listView, 0, new Class[]{DialogCell.class}, rLottieDrawableArr4, "Arrow", i21));
                arrayList.add(new ThemeDescription(this.viewPages[i15].listView, 0, new Class[]{DialogCell.class}, new RLottieDrawable[]{Theme.dialogs_pinArchiveDrawable}, "Line", i21));
                arrayList.add(new ThemeDescription(this.viewPages[i15].listView, 0, new Class[]{DialogCell.class}, new RLottieDrawable[]{Theme.dialogs_unpinArchiveDrawable}, "Arrow", i21));
                arrayList.add(new ThemeDescription(this.viewPages[i15].listView, 0, new Class[]{DialogCell.class}, new RLottieDrawable[]{Theme.dialogs_unpinArchiveDrawable}, "Line", i21));
                RLottieDrawable[] rLottieDrawableArr5 = {Theme.dialogs_archiveDrawable};
                int i22 = Theme.key_chats_archiveBackground;
                arrayList.add(new ThemeDescription(this.viewPages[i15].listView, 0, new Class[]{DialogCell.class}, rLottieDrawableArr5, "Arrow", i22));
                arrayList.add(new ThemeDescription(this.viewPages[i15].listView, 0, new Class[]{DialogCell.class}, new RLottieDrawable[]{Theme.dialogs_archiveDrawable}, "Box2", i21));
                arrayList.add(new ThemeDescription(this.viewPages[i15].listView, 0, new Class[]{DialogCell.class}, new RLottieDrawable[]{Theme.dialogs_archiveDrawable}, "Box1", i21));
                arrayList.add(new ThemeDescription(this.viewPages[i15].listView, 0, new Class[]{DialogCell.class}, new RLottieDrawable[]{Theme.dialogs_hidePsaDrawable}, "Line 1", i22));
                arrayList.add(new ThemeDescription(this.viewPages[i15].listView, 0, new Class[]{DialogCell.class}, new RLottieDrawable[]{Theme.dialogs_hidePsaDrawable}, "Line 2", i22));
                arrayList.add(new ThemeDescription(this.viewPages[i15].listView, 0, new Class[]{DialogCell.class}, new RLottieDrawable[]{Theme.dialogs_hidePsaDrawable}, "Line 3", i22));
                arrayList.add(new ThemeDescription(this.viewPages[i15].listView, 0, new Class[]{DialogCell.class}, new RLottieDrawable[]{Theme.dialogs_hidePsaDrawable}, "Cup Red", i21));
                arrayList.add(new ThemeDescription(this.viewPages[i15].listView, 0, new Class[]{DialogCell.class}, new RLottieDrawable[]{Theme.dialogs_hidePsaDrawable}, "Box", i21));
                arrayList.add(new ThemeDescription(this.viewPages[i15].listView, 0, new Class[]{DialogCell.class}, new RLottieDrawable[]{Theme.dialogs_unarchiveDrawable}, "Arrow1", i21));
                arrayList.add(new ThemeDescription(this.viewPages[i15].listView, 0, new Class[]{DialogCell.class}, new RLottieDrawable[]{Theme.dialogs_unarchiveDrawable}, "Arrow2", Theme.key_chats_archivePinBackground));
                arrayList.add(new ThemeDescription(this.viewPages[i15].listView, 0, new Class[]{DialogCell.class}, new RLottieDrawable[]{Theme.dialogs_unarchiveDrawable}, "Box2", i21));
                arrayList.add(new ThemeDescription(this.viewPages[i15].listView, 0, new Class[]{DialogCell.class}, new RLottieDrawable[]{Theme.dialogs_unarchiveDrawable}, "Box1", i21));
                arrayList.add(new ThemeDescription(this.viewPages[i15].listView, 0, new Class[]{UserCell.class}, new String[]{str7}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteBlackText));
                ThemeDescription.ThemeDescriptionDelegate themeDescriptionDelegate5 = themeDescriptionDelegate;
                arrayList.add(new ThemeDescription(this.viewPages[i15].listView, 0, new Class[]{UserCell.class}, new String[]{"statusColor"}, (Paint[]) null, (Drawable[]) null, themeDescriptionDelegate5, Theme.key_windowBackgroundWhiteGrayText));
                arrayList.add(new ThemeDescription(this.viewPages[i15].listView, 0, new Class[]{UserCell.class}, new String[]{"statusOnlineColor"}, (Paint[]) null, (Drawable[]) null, themeDescriptionDelegate5, Theme.key_windowBackgroundWhiteBlueText));
                int i23 = Theme.key_windowBackgroundWhiteBlueText4;
                arrayList.add(new ThemeDescription(this.viewPages[i15].listView, 0, new Class[]{TextCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, i23));
                arrayList.add(new ThemeDescription(this.viewPages[i15].listView, 0, new Class[]{TextCell.class}, new String[]{str6}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, i23));
                arrayList.add(new ThemeDescription(this.viewPages[i15].progressView, ThemeDescription.FLAG_PROGRESSBAR, null, null, null, null, Theme.key_progressCircle));
                ViewPager archiveHintCellPager = this.viewPages[i15].dialogsAdapter.getArchiveHintCellPager();
                arrayList.add(new ThemeDescription(archiveHintCellPager, 0, new Class[]{ArchiveHintInnerCell.class}, new String[]{str6}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, i16));
                arrayList.add(new ThemeDescription(archiveHintCellPager, 0, new Class[]{ArchiveHintInnerCell.class}, new String[]{"imageView2"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_chats_unreadCounter));
                arrayList.add(new ThemeDescription(archiveHintCellPager, 0, new Class[]{ArchiveHintInnerCell.class}, new String[]{"headerTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, i16));
                arrayList.add(new ThemeDescription(archiveHintCellPager, 0, new Class[]{ArchiveHintInnerCell.class}, new String[]{"messageTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, i17));
                arrayList.add(new ThemeDescription(archiveHintCellPager, ThemeDescription.FLAG_LISTGLOWCOLOR, null, null, null, null, Theme.key_actionBarDefaultArchived));
            }
        }
        ThemeDescription.ThemeDescriptionDelegate themeDescriptionDelegate6 = themeDescriptionDelegate;
        arrayList.add(new ThemeDescription(null, 0, null, null, null, themeDescriptionDelegate6, Theme.key_chats_archivePullDownBackground));
        arrayList.add(new ThemeDescription(null, 0, null, null, null, themeDescriptionDelegate6, Theme.key_chats_archivePullDownBackgroundActive));
        RecyclerView recyclerView = this.sideMenu;
        int i24 = ThemeDescription.FLAG_BACKGROUND;
        int i25 = Theme.key_chats_menuBackground;
        arrayList.add(new ThemeDescription(recyclerView, i24, null, null, null, null, i25));
        int i26 = Theme.key_chats_menuName;
        arrayList.add(new ThemeDescription(this.sideMenu, 0, new Class[]{DrawerProfileCell.class}, null, null, null, i26));
        arrayList.add(new ThemeDescription(this.sideMenu, 0, new Class[]{DrawerProfileCell.class}, null, null, null, Theme.key_chats_menuPhone));
        arrayList.add(new ThemeDescription(this.sideMenu, 0, new Class[]{DrawerProfileCell.class}, null, null, null, Theme.key_chats_menuPhoneCats));
        arrayList.add(new ThemeDescription(this.sideMenu, 0, new Class[]{DrawerProfileCell.class}, null, null, null, Theme.key_chat_serviceBackground));
        arrayList.add(new ThemeDescription(this.sideMenu, 0, new Class[]{DrawerProfileCell.class}, null, null, null, Theme.key_chats_menuTopShadow));
        arrayList.add(new ThemeDescription(this.sideMenu, 0, new Class[]{DrawerProfileCell.class}, null, null, null, Theme.key_chats_menuTopShadowCats));
        arrayList.add(new ThemeDescription(this.sideMenu, ThemeDescription.FLAG_IMAGECOLOR, new Class[]{DrawerProfileCell.class}, new String[]{"darkThemeView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, i26));
        ThemeDescription.ThemeDescriptionDelegate themeDescriptionDelegate7 = themeDescriptionDelegate;
        arrayList.add(new ThemeDescription(this.sideMenu, ThemeDescription.FLAG_CELLBACKGROUNDCOLOR | ThemeDescription.FLAG_CHECKTAG, new Class[]{DrawerProfileCell.class}, null, null, themeDescriptionDelegate7, Theme.key_chats_menuTopBackgroundCats));
        arrayList.add(new ThemeDescription(this.sideMenu, ThemeDescription.FLAG_CELLBACKGROUNDCOLOR | ThemeDescription.FLAG_CHECKTAG, new Class[]{DrawerProfileCell.class}, null, null, themeDescriptionDelegate7, Theme.key_chats_menuTopBackground));
        int i27 = Theme.key_chats_menuItemIcon;
        arrayList.add(new ThemeDescription(this.sideMenu, ThemeDescription.FLAG_IMAGECOLOR, new Class[]{cls8}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, i27));
        int i28 = Theme.key_chats_menuItemText;
        arrayList.add(new ThemeDescription(this.sideMenu, 0, new Class[]{cls8}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, i28));
        arrayList.add(new ThemeDescription(this.sideMenu, 0, new Class[]{DrawerUserCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, i28));
        int i29 = Theme.key_chats_unreadCounterText;
        arrayList.add(new ThemeDescription(this.sideMenu, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{DrawerUserCell.class}, new String[]{"checkBox"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, i29));
        int i30 = Theme.key_chats_unreadCounter;
        arrayList.add(new ThemeDescription(this.sideMenu, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{DrawerUserCell.class}, new String[]{"checkBox"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, i30));
        arrayList.add(new ThemeDescription(this.sideMenu, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{DrawerUserCell.class}, new String[]{"checkBox"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, i25));
        arrayList.add(new ThemeDescription(this.sideMenu, ThemeDescription.FLAG_IMAGECOLOR, new Class[]{cls7}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, i27));
        arrayList.add(new ThemeDescription(this.sideMenu, 0, new Class[]{cls7}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, i28));
        arrayList.add(new ThemeDescription(this.sideMenu, 0, new Class[]{DividerCell.class}, Theme.dividerPaint, null, null, Theme.key_divider));
        if (this.searchViewPager != null) {
            DialogsSearchAdapter dialogsSearchAdapter = this.searchViewPager.dialogsSearchAdapter;
            arrayList.add(new ThemeDescription(dialogsSearchAdapter != null ? dialogsSearchAdapter.getInnerListView() : null, 0, new Class[]{HintDialogCell.class}, Theme.dialogs_countPaint, null, null, i30));
            DialogsSearchAdapter dialogsSearchAdapter2 = this.searchViewPager.dialogsSearchAdapter;
            arrayList.add(new ThemeDescription(dialogsSearchAdapter2 != null ? dialogsSearchAdapter2.getInnerListView() : null, 0, new Class[]{HintDialogCell.class}, Theme.dialogs_countGrayPaint, null, null, Theme.key_chats_unreadCounterMuted));
            DialogsSearchAdapter dialogsSearchAdapter3 = this.searchViewPager.dialogsSearchAdapter;
            arrayList.add(new ThemeDescription(dialogsSearchAdapter3 != null ? dialogsSearchAdapter3.getInnerListView() : null, 0, new Class[]{HintDialogCell.class}, Theme.dialogs_countTextPaint, null, null, i29));
            DialogsSearchAdapter dialogsSearchAdapter4 = this.searchViewPager.dialogsSearchAdapter;
            arrayList.add(new ThemeDescription(dialogsSearchAdapter4 != null ? dialogsSearchAdapter4.getInnerListView() : null, 0, new Class[]{HintDialogCell.class}, Theme.dialogs_archiveTextPaint, null, null, Theme.key_chats_archiveText));
            DialogsSearchAdapter dialogsSearchAdapter5 = this.searchViewPager.dialogsSearchAdapter;
            arrayList.add(new ThemeDescription(dialogsSearchAdapter5 != null ? dialogsSearchAdapter5.getInnerListView() : null, 0, new Class[]{HintDialogCell.class}, new String[]{str7}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteBlackText));
            DialogsSearchAdapter dialogsSearchAdapter6 = this.searchViewPager.dialogsSearchAdapter;
            arrayList.add(new ThemeDescription(dialogsSearchAdapter6 != null ? dialogsSearchAdapter6.getInnerListView() : null, 0, new Class[]{HintDialogCell.class}, null, null, null, Theme.key_chats_onlineCircle));
        }
        arrayList.add(new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_BACKGROUND | ThemeDescription.FLAG_CHECKTAG, new Class[]{FragmentContextView.class}, new String[]{str}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_inappPlayerBackground));
        arrayList.add(new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_IMAGECOLOR, new Class[]{FragmentContextView.class}, new String[]{"playButton"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_inappPlayerPlayPause));
        arrayList.add(new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_TEXTCOLOR | ThemeDescription.FLAG_CHECKTAG, new Class[]{FragmentContextView.class}, new String[]{"titleTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_inappPlayerTitle));
        arrayList.add(new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_TEXTCOLOR | ThemeDescription.FLAG_FASTSCROLL, new Class[]{FragmentContextView.class}, new String[]{"titleTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_inappPlayerPerformer));
        arrayList.add(new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_IMAGECOLOR, new Class[]{FragmentContextView.class}, new String[]{"closeButton"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_inappPlayerClose));
        arrayList.add(new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_BACKGROUND | ThemeDescription.FLAG_CHECKTAG, new Class[]{FragmentContextView.class}, new String[]{str}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_returnToCallBackground));
        arrayList.add(new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_TEXTCOLOR | ThemeDescription.FLAG_CHECKTAG, new Class[]{FragmentContextView.class}, new String[]{"titleTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_returnToCallText));
        for (int i31 = 0; i31 < this.undoView.length; i31++) {
            UndoView undoView = this.undoView[i31];
            int i32 = ThemeDescription.FLAG_BACKGROUNDFILTER;
            int i33 = Theme.key_undo_background;
            arrayList.add(new ThemeDescription(undoView, i32, null, null, null, null, i33));
            int i34 = Theme.key_undo_cancelColor;
            arrayList.add(new ThemeDescription(this.undoView[i31], 0, new Class[]{UndoView.class}, new String[]{"undoImageView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, i34));
            arrayList.add(new ThemeDescription(this.undoView[i31], 0, new Class[]{UndoView.class}, new String[]{"undoTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, i34));
            int i35 = Theme.key_undo_infoColor;
            arrayList.add(new ThemeDescription(this.undoView[i31], 0, new Class[]{UndoView.class}, new String[]{"infoTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, i35));
            arrayList.add(new ThemeDescription(this.undoView[i31], 0, new Class[]{UndoView.class}, new String[]{"subinfoTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, i35));
            arrayList.add(new ThemeDescription(this.undoView[i31], 0, new Class[]{UndoView.class}, new String[]{"textPaint"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, i35));
            arrayList.add(new ThemeDescription(this.undoView[i31], 0, new Class[]{UndoView.class}, new String[]{"progressPaint"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, i35));
            arrayList.add(new ThemeDescription(this.undoView[i31], 0, new Class[]{UndoView.class}, new String[]{"leftImageView"}, "info1", i33));
            arrayList.add(new ThemeDescription(this.undoView[i31], 0, new Class[]{UndoView.class}, new String[]{"leftImageView"}, "info2", i33));
            arrayList.add(new ThemeDescription(this.undoView[i31], 0, new Class[]{UndoView.class}, new String[]{"leftImageView"}, "luc12", i35));
            arrayList.add(new ThemeDescription(this.undoView[i31], 0, new Class[]{UndoView.class}, new String[]{"leftImageView"}, "luc11", i35));
            arrayList.add(new ThemeDescription(this.undoView[i31], 0, new Class[]{UndoView.class}, new String[]{"leftImageView"}, "luc10", i35));
            arrayList.add(new ThemeDescription(this.undoView[i31], 0, new Class[]{UndoView.class}, new String[]{"leftImageView"}, "luc9", i35));
            arrayList.add(new ThemeDescription(this.undoView[i31], 0, new Class[]{UndoView.class}, new String[]{"leftImageView"}, "luc8", i35));
            arrayList.add(new ThemeDescription(this.undoView[i31], 0, new Class[]{UndoView.class}, new String[]{"leftImageView"}, "luc7", i35));
            arrayList.add(new ThemeDescription(this.undoView[i31], 0, new Class[]{UndoView.class}, new String[]{"leftImageView"}, "luc6", i35));
            arrayList.add(new ThemeDescription(this.undoView[i31], 0, new Class[]{UndoView.class}, new String[]{"leftImageView"}, "luc5", i35));
            arrayList.add(new ThemeDescription(this.undoView[i31], 0, new Class[]{UndoView.class}, new String[]{"leftImageView"}, "luc4", i35));
            arrayList.add(new ThemeDescription(this.undoView[i31], 0, new Class[]{UndoView.class}, new String[]{"leftImageView"}, "luc3", i35));
            arrayList.add(new ThemeDescription(this.undoView[i31], 0, new Class[]{UndoView.class}, new String[]{"leftImageView"}, "luc2", i35));
            arrayList.add(new ThemeDescription(this.undoView[i31], 0, new Class[]{UndoView.class}, new String[]{"leftImageView"}, "luc1", i35));
            arrayList.add(new ThemeDescription(this.undoView[i31], 0, new Class[]{UndoView.class}, new String[]{"leftImageView"}, "Oval", i35));
        }
        arrayList.add(new ThemeDescription(null, 0, null, null, null, null, Theme.key_dialogBackground));
        arrayList.add(new ThemeDescription(null, 0, null, null, null, null, Theme.key_dialogBackgroundGray));
        arrayList.add(new ThemeDescription(null, 0, null, null, null, null, Theme.key_dialogTextBlack));
        arrayList.add(new ThemeDescription(null, 0, null, null, null, null, Theme.key_dialogTextLink));
        arrayList.add(new ThemeDescription(null, 0, null, null, null, null, Theme.key_dialogLinkSelection));
        arrayList.add(new ThemeDescription(null, 0, null, null, null, null, Theme.key_dialogTextBlue));
        arrayList.add(new ThemeDescription(null, 0, null, null, null, null, Theme.key_dialogTextBlue2));
        arrayList.add(new ThemeDescription(null, 0, null, null, null, null, Theme.key_dialogTextBlue4));
        int i36 = Theme.key_text_RedBold;
        arrayList.add(new ThemeDescription(null, 0, null, null, null, null, i36));
        arrayList.add(new ThemeDescription(null, 0, null, null, null, null, Theme.key_dialogTextGray));
        arrayList.add(new ThemeDescription(null, 0, null, null, null, null, Theme.key_dialogTextGray2));
        arrayList.add(new ThemeDescription(null, 0, null, null, null, null, Theme.key_dialogTextGray3));
        arrayList.add(new ThemeDescription(null, 0, null, null, null, null, Theme.key_dialogTextGray4));
        arrayList.add(new ThemeDescription(null, 0, null, null, null, null, Theme.key_dialogIcon));
        arrayList.add(new ThemeDescription(null, 0, null, null, null, null, Theme.key_text_RedRegular));
        arrayList.add(new ThemeDescription(null, 0, null, null, null, null, Theme.key_dialogTextHint));
        arrayList.add(new ThemeDescription(null, 0, null, null, null, null, Theme.key_dialogInputField));
        arrayList.add(new ThemeDescription(null, 0, null, null, null, null, Theme.key_dialogInputFieldActivated));
        arrayList.add(new ThemeDescription(null, 0, null, null, null, null, Theme.key_dialogCheckboxSquareBackground));
        arrayList.add(new ThemeDescription(null, 0, null, null, null, null, Theme.key_dialogCheckboxSquareCheck));
        arrayList.add(new ThemeDescription(null, 0, null, null, null, null, Theme.key_dialogCheckboxSquareUnchecked));
        arrayList.add(new ThemeDescription(null, 0, null, null, null, null, Theme.key_dialogCheckboxSquareDisabled));
        arrayList.add(new ThemeDescription(null, 0, null, null, null, null, Theme.key_dialogRadioBackground));
        arrayList.add(new ThemeDescription(null, 0, null, null, null, null, Theme.key_dialogRadioBackgroundChecked));
        arrayList.add(new ThemeDescription(null, 0, null, null, null, null, Theme.key_dialogButton));
        arrayList.add(new ThemeDescription(null, 0, null, null, null, null, Theme.key_dialogButtonSelector));
        arrayList.add(new ThemeDescription(null, 0, null, null, null, null, Theme.key_dialogScrollGlow));
        arrayList.add(new ThemeDescription(null, 0, null, null, null, null, Theme.key_dialogRoundCheckBox));
        arrayList.add(new ThemeDescription(null, 0, null, null, null, null, Theme.key_dialogRoundCheckBoxCheck));
        arrayList.add(new ThemeDescription(null, 0, null, null, null, null, Theme.key_dialogLineProgress));
        arrayList.add(new ThemeDescription(null, 0, null, null, null, null, Theme.key_dialogLineProgressBackground));
        arrayList.add(new ThemeDescription(null, 0, null, null, null, null, Theme.key_dialogGrayLine));
        arrayList.add(new ThemeDescription(null, 0, null, null, null, null, Theme.key_dialog_inlineProgressBackground));
        arrayList.add(new ThemeDescription(null, 0, null, null, null, null, Theme.key_dialog_inlineProgress));
        arrayList.add(new ThemeDescription(null, 0, null, null, null, null, Theme.key_dialogSearchBackground));
        arrayList.add(new ThemeDescription(null, 0, null, null, null, null, Theme.key_dialogSearchHint));
        arrayList.add(new ThemeDescription(null, 0, null, null, null, null, Theme.key_dialogSearchIcon));
        arrayList.add(new ThemeDescription(null, 0, null, null, null, null, Theme.key_dialogSearchText));
        arrayList.add(new ThemeDescription(null, 0, null, null, null, null, Theme.key_dialogFloatingButton));
        arrayList.add(new ThemeDescription(null, 0, null, null, null, null, Theme.key_dialogFloatingIcon));
        arrayList.add(new ThemeDescription(null, 0, null, null, null, null, Theme.key_dialogShadowLine));
        arrayList.add(new ThemeDescription(null, 0, null, null, null, null, Theme.key_sheet_scrollUp));
        arrayList.add(new ThemeDescription(null, 0, null, null, null, null, Theme.key_sheet_other));
        arrayList.add(new ThemeDescription(null, 0, null, null, null, null, Theme.key_player_actionBarSelector));
        arrayList.add(new ThemeDescription(null, 0, null, null, null, null, Theme.key_player_actionBarTitle));
        arrayList.add(new ThemeDescription(null, 0, null, null, null, null, Theme.key_player_actionBarSubtitle));
        arrayList.add(new ThemeDescription(null, 0, null, null, null, null, Theme.key_player_actionBarItems));
        arrayList.add(new ThemeDescription(null, 0, null, null, null, null, Theme.key_player_background));
        int i37 = Theme.key_player_time;
        arrayList.add(new ThemeDescription(null, 0, null, null, null, null, i37));
        arrayList.add(new ThemeDescription(null, 0, null, null, null, null, Theme.key_player_progressBackground));
        arrayList.add(new ThemeDescription(null, 0, null, null, null, null, Theme.key_player_progressCachedBackground));
        arrayList.add(new ThemeDescription(null, 0, null, null, null, null, Theme.key_player_progress));
        arrayList.add(new ThemeDescription(null, 0, null, null, null, null, Theme.key_player_button));
        arrayList.add(new ThemeDescription(null, 0, null, null, null, null, Theme.key_player_buttonActive));
        if (this.commentView != null) {
            arrayList.add(new ThemeDescription(this.commentView, 0, null, Theme.chat_composeBackgroundPaint, null, null, Theme.key_chat_messagePanelBackground));
            arrayList.add(new ThemeDescription(this.commentView, 0, null, null, new Drawable[]{Theme.chat_composeShadowDrawable}, null, Theme.key_chat_messagePanelShadow));
            arrayList.add(new ThemeDescription(this.commentView, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{ChatActivityEnterView.class}, new String[]{"messageEditText"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_chat_messagePanelText));
            arrayList.add(new ThemeDescription(this.commentView, ThemeDescription.FLAG_CURSORCOLOR, new Class[]{ChatActivityEnterView.class}, new String[]{"messageEditText"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_chat_messagePanelCursor));
            arrayList.add(new ThemeDescription(this.commentView, ThemeDescription.FLAG_HINTTEXTCOLOR, new Class[]{ChatActivityEnterView.class}, new String[]{"messageEditText"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_chat_messagePanelHint));
        }
        int i38 = Theme.key_windowBackgroundWhiteBlackText;
        ThemeDescription.ThemeDescriptionDelegate themeDescriptionDelegate8 = themeDescriptionDelegate;
        arrayList.add(new ThemeDescription(null, 0, null, null, null, themeDescriptionDelegate8, i38));
        arrayList.add(new ThemeDescription(null, 0, null, null, null, themeDescriptionDelegate8, i37));
        arrayList.add(new ThemeDescription(null, 0, null, null, null, themeDescriptionDelegate8, Theme.key_chat_messagePanelCursor));
        arrayList.add(new ThemeDescription(null, 0, null, null, null, themeDescriptionDelegate8, Theme.key_avatar_actionBarIconBlue));
        arrayList.add(new ThemeDescription(null, 0, null, null, null, themeDescriptionDelegate8, Theme.key_groupcreate_spanBackground));
        arrayList.add(new ThemeDescription(null, 0, null, null, null, null, Theme.key_voipgroup_overlayGreen1));
        arrayList.add(new ThemeDescription(null, 0, null, null, null, null, Theme.key_voipgroup_overlayGreen2));
        arrayList.add(new ThemeDescription(null, 0, null, null, null, null, Theme.key_voipgroup_overlayBlue1));
        arrayList.add(new ThemeDescription(null, 0, null, null, null, null, Theme.key_voipgroup_overlayBlue2));
        arrayList.add(new ThemeDescription(null, 0, null, null, null, null, Theme.key_voipgroup_topPanelGreen1));
        arrayList.add(new ThemeDescription(null, 0, null, null, null, null, Theme.key_voipgroup_topPanelGreen2));
        arrayList.add(new ThemeDescription(null, 0, null, null, null, null, Theme.key_voipgroup_topPanelBlue1));
        arrayList.add(new ThemeDescription(null, 0, null, null, null, null, Theme.key_voipgroup_topPanelBlue2));
        arrayList.add(new ThemeDescription(null, 0, null, null, null, null, Theme.key_voipgroup_topPanelGray));
        arrayList.add(new ThemeDescription(null, 0, null, null, null, null, Theme.key_voipgroup_overlayAlertGradientMuted));
        arrayList.add(new ThemeDescription(null, 0, null, null, null, null, Theme.key_voipgroup_overlayAlertGradientMuted2));
        arrayList.add(new ThemeDescription(null, 0, null, null, null, null, Theme.key_voipgroup_overlayAlertGradientUnmuted));
        arrayList.add(new ThemeDescription(null, 0, null, null, null, null, Theme.key_voipgroup_overlayAlertGradientUnmuted2));
        arrayList.add(new ThemeDescription(null, 0, null, null, null, null, Theme.key_voipgroup_mutedByAdminGradient));
        arrayList.add(new ThemeDescription(null, 0, null, null, null, null, Theme.key_voipgroup_mutedByAdminGradient2));
        arrayList.add(new ThemeDescription(null, 0, null, null, null, null, Theme.key_voipgroup_mutedByAdminGradient3));
        arrayList.add(new ThemeDescription(null, 0, null, null, null, null, Theme.key_voipgroup_overlayAlertMutedByAdmin));
        arrayList.add(new ThemeDescription(null, 0, null, null, null, null, Theme.key_voipgroup_overlayAlertMutedByAdmin2));
        ThemeDescription.ThemeDescriptionDelegate themeDescriptionDelegate9 = themeDescriptionDelegate;
        arrayList.add(new ThemeDescription(null, 0, null, null, null, themeDescriptionDelegate9, Theme.key_stories_circle_dialog1));
        arrayList.add(new ThemeDescription(null, 0, null, null, null, themeDescriptionDelegate9, Theme.key_stories_circle_dialog2));
        arrayList.add(new ThemeDescription(null, 0, null, null, null, themeDescriptionDelegate9, Theme.key_stories_circle_closeFriends1));
        arrayList.add(new ThemeDescription(null, 0, null, null, null, themeDescriptionDelegate9, Theme.key_stories_circle_closeFriends2));
        arrayList.add(new ThemeDescription(null, 0, null, null, null, themeDescriptionDelegate9, Theme.key_stories_circle1));
        arrayList.add(new ThemeDescription(null, 0, null, null, null, themeDescriptionDelegate9, Theme.key_stories_circle2));
        FiltersView filtersView = this.filtersView;
        if (filtersView != null) {
            arrayList.addAll(filtersView.getThemeDescriptions());
            this.filtersView.updateColors();
        }
        SearchViewPager searchViewPager2 = this.searchViewPager;
        if (searchViewPager2 != null) {
            searchViewPager2.getThemeDescriptions(arrayList);
        }
        if (this.speedItem != null) {
            arrayList.addAll(SimpleThemeDescription.createThemeDescriptions(new ThemeDescription.ThemeDescriptionDelegate() {
                @Override
                public final void didSetColor() {
                    DialogsActivity.this.lambda$getThemeDescriptions$130();
                }

                @Override
                public void onAnimationProgress(float f) {
                    ThemeDescription.ThemeDescriptionDelegate.CC.$default$onAnimationProgress(this, f);
                }
            }, Theme.key_actionBarActionModeDefaultIcon, Theme.key_actionBarActionModeDefaultSelector));
        }
        final DialogsHintCell dialogsHintCell = this.dialogsHintCell;
        if (dialogsHintCell != null) {
            SimpleThemeDescription.add(arrayList, new Runnable() {
                @Override
                public final void run() {
                    DialogsHintCell.this.updateColors();
                }
            }, Theme.key_windowBackgroundWhite, i38, Theme.key_windowBackgroundWhiteGrayText);
        }
        final UnconfirmedAuthHintCell unconfirmedAuthHintCell = this.authHintCell;
        if (unconfirmedAuthHintCell != null) {
            SimpleThemeDescription.add(arrayList, new Runnable() {
                @Override
                public final void run() {
                    UnconfirmedAuthHintCell.this.updateColors();
                }
            }, Theme.key_windowBackgroundWhite, i38, Theme.key_windowBackgroundWhiteGrayText, Theme.key_windowBackgroundWhiteValueText, i36);
        }
        return arrayList;
    }

    public UndoView getUndoView() {
        createUndoView();
        UndoView undoView = this.undoView[0];
        if (undoView != null && undoView.getVisibility() == 0) {
            UndoView[] undoViewArr = this.undoView;
            UndoView undoView2 = undoViewArr[0];
            undoViewArr[0] = undoViewArr[1];
            undoViewArr[1] = undoView2;
            undoView2.hide(true, 2);
            ContentView contentView = (ContentView) this.fragmentView;
            contentView.removeView(this.undoView[0]);
            contentView.addView(this.undoView[0]);
        }
        return this.undoView[0];
    }

    public boolean isArchive() {
        return this.folderId == 1;
    }

    @Override
    public boolean isLightStatusBar() {
        RightSlidingDialogContainer rightSlidingDialogContainer;
        if (!this.searching && (rightSlidingDialogContainer = this.rightSlidingDialogContainer) != null && rightSlidingDialogContainer.getFragment() != null) {
            return this.rightSlidingDialogContainer.getFragment().isLightStatusBar();
        }
        int color = Theme.getColor((this.searching && this.whiteActionBar) ? Theme.key_windowBackgroundWhite : this.folderId == 0 ? Theme.key_actionBarDefault : Theme.key_actionBarDefaultArchived);
        if (this.actionBar.isActionModeShowed()) {
            color = Theme.getColor(Theme.key_actionBarActionModeDefault);
        }
        return ColorUtils.calculateLuminance(color) > 0.699999988079071d;
    }

    public boolean isMainDialogList() {
        return this.delegate == null && this.searchString == null;
    }

    public boolean isPremiumChristmasHintVisible() {
        if (MessagesController.getInstance(this.currentAccount).premiumFeaturesBlocked() || this.folderId != 0) {
            return false;
        }
        return MessagesController.getInstance(this.currentAccount).pendingSuggestions.contains("PREMIUM_CHRISTMAS");
    }

    public boolean isPremiumHintVisible() {
        if (MessagesController.getInstance(this.currentAccount).premiumFeaturesBlocked() || this.folderId != 0 || ((!(MessagesController.getInstance(this.currentAccount).pendingSuggestions.contains("PREMIUM_UPGRADE") && getUserConfig().isPremium()) && (!MessagesController.getInstance(this.currentAccount).pendingSuggestions.contains("PREMIUM_ANNUAL") || getUserConfig().isPremium())) || (!UserConfig.getInstance(this.currentAccount).isPremium() ? MediaDataController.getInstance(this.currentAccount).getPremiumHintAnnualDiscount(false) != null : !(BuildVars.useInvoiceBilling() || MediaDataController.getInstance(this.currentAccount).getPremiumHintAnnualDiscount(true) == null)))) {
            return false;
        }
        this.isPremiumHintUpgrade = MessagesController.getInstance(this.currentAccount).pendingSuggestions.contains("PREMIUM_UPGRADE");
        return true;
    }

    public boolean isPremiumRestoreHintVisible() {
        return (MessagesController.getInstance(this.currentAccount).premiumFeaturesBlocked() || this.folderId != 0 || !MessagesController.getInstance(this.currentAccount).pendingSuggestions.contains("PREMIUM_RESTORE") || getUserConfig().isPremium() || MediaDataController.getInstance(this.currentAccount).getPremiumHintAnnualDiscount(false) == null) ? false : true;
    }

    public boolean isStarsSubscriptionHintVisible() {
        if (this.folderId != 0 || !MessagesController.getInstance(this.currentAccount).pendingSuggestions.contains("STARS_SUBSCRIPTION_LOW_BALANCE")) {
            return false;
        }
        StarsController starsController = StarsController.getInstance(this.currentAccount);
        if (!starsController.hasInsufficientSubscriptions()) {
            starsController.loadInsufficientSubscriptions();
            return false;
        }
        long j = -starsController.balance;
        for (int i = 0; i < starsController.insufficientSubscriptions.size(); i++) {
            TL_stars.StarsSubscription starsSubscription = (TL_stars.StarsSubscription) starsController.insufficientSubscriptions.get(i);
            if (getMessagesController().getChat(Long.valueOf(-DialogObject.getPeerDialogId(starsSubscription.peer))) != null) {
                j += starsSubscription.pricing.amount;
            }
        }
        return j > 0;
    }

    @Override
    public boolean onBackPressed() {
        if (closeSheet()) {
            return false;
        }
        if (this.rightSlidingDialogContainer.hasFragment()) {
            if (this.rightSlidingDialogContainer.getFragment().onBackPressed()) {
                this.rightSlidingDialogContainer.lambda$presentFragment$1();
                SearchViewPager searchViewPager = this.searchViewPager;
                if (searchViewPager != null) {
                    searchViewPager.updateTabs();
                }
            }
            return false;
        }
        ItemOptions itemOptions = this.filterOptions;
        if (itemOptions != null) {
            itemOptions.dismiss();
            this.filterOptions = null;
            return false;
        }
        FilterTabsView filterTabsView = this.filterTabsView;
        if (filterTabsView != null && filterTabsView.isEditing()) {
            this.filterTabsView.setIsEditing(false);
            showDoneItem(false);
            return false;
        }
        ActionBar actionBar = this.actionBar;
        if (actionBar != null && actionBar.isActionModeShowed()) {
            SearchViewPager searchViewPager2 = this.searchViewPager;
            if (searchViewPager2 != null && searchViewPager2.getVisibility() == 0) {
                this.searchViewPager.hideActionMode();
            }
            hideActionMode(true);
            return false;
        }
        FilterTabsView filterTabsView2 = this.filterTabsView;
        if (filterTabsView2 != null && filterTabsView2.getVisibility() == 0 && !this.tabsAnimationInProgress && !this.filterTabsView.isAnimatingIndicator() && !this.startedTracking && !this.filterTabsView.isFirstTabSelected()) {
            this.filterTabsView.selectFirstTab();
            return false;
        }
        ChatActivityEnterView chatActivityEnterView = this.commentView;
        if (chatActivityEnterView != null && chatActivityEnterView.isPopupShowing()) {
            this.commentView.hidePopup(true);
            return false;
        }
        if (this.dialogStoriesCell.isFullExpanded() && this.dialogStoriesCell.scrollToFirst()) {
            return false;
        }
        return super.onBackPressed();
    }

    @Override
    public void onBecomeFullyHidden() {
        View view;
        FilterTabsView filterTabsView;
        float f;
        if (this.closeSearchFieldOnHide) {
            ActionBar actionBar = this.actionBar;
            if (actionBar != null) {
                actionBar.closeSearchField();
            }
            TLObject tLObject = this.searchObject;
            if (tLObject != null) {
                SearchViewPager searchViewPager = this.searchViewPager;
                if (searchViewPager != null) {
                    searchViewPager.dialogsSearchAdapter.putRecentSearch(this.searchDialogId, tLObject);
                }
                this.searchObject = null;
            }
            this.closeSearchFieldOnHide = false;
        }
        if (!this.hasStories && (filterTabsView = this.filterTabsView) != null && filterTabsView.getVisibility() == 0 && this.filterTabsViewIsVisible) {
            int i = (int) (-this.scrollYOffset);
            int currentActionBarHeight = ActionBar.getCurrentActionBarHeight();
            if (i != 0 && i != currentActionBarHeight) {
                if (i >= currentActionBarHeight / 2) {
                    f = this.viewPages[0].listView.canScrollVertically(1) ? -currentActionBarHeight : 0.0f;
                }
                setScrollY(f);
            }
        }
        UndoView undoView = this.undoView[0];
        if (undoView != null) {
            undoView.hide(true, 0);
        }
        if (!isInPreviewMode() && (view = this.blurredView) != null && view.getVisibility() == 0) {
            this.blurredView.setVisibility(8);
            this.blurredView.setBackground(null);
        }
        super.onBecomeFullyHidden();
        this.canShowStoryHint = true;
    }

    @Override
    public void onBecomeFullyVisible() {
        HintView2 hintView2;
        super.onBecomeFullyVisible();
        if (isArchive()) {
            SharedPreferences globalMainSettings = MessagesController.getGlobalMainSettings();
            boolean z = globalMainSettings.getBoolean("archivehint", true);
            boolean isEmpty = getDialogsArray(this.currentAccount, this.initialDialogsType, this.folderId, false).isEmpty();
            if (z && isEmpty) {
                MessagesController.getGlobalMainSettings().edit().putBoolean("archivehint", false).commit();
                z = false;
            }
            if (z) {
                globalMainSettings.edit().putBoolean("archivehint", false).commit();
                showArchiveHelp();
            }
            ActionBarMenuItem actionBarMenuItem = this.optionsItem;
            if (actionBarMenuItem != null) {
                if (isEmpty) {
                    actionBarMenuItem.hideSubItem(6);
                } else {
                    actionBarMenuItem.showSubItem(6);
                }
            }
        }
        updateFloatingButtonOffset();
        if (this.canShowStoryHint && !this.storyHintShown && (hintView2 = this.storyHint) != null && this.storiesEnabled) {
            this.storyHintShown = true;
            this.canShowStoryHint = false;
            hintView2.show();
        }
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                DialogsActivity.this.createSearchViewPager();
            }
        }, 200L);
    }

    @Override
    public void onConfigurationChanged(Configuration configuration) {
        FrameLayout frameLayout;
        super.onConfigurationChanged(configuration);
        ItemOptions itemOptions = this.filterOptions;
        if (itemOptions != null) {
            itemOptions.dismiss();
        }
        if (this.onlySelect || (frameLayout = this.floatingButtonContainer) == null) {
            return;
        }
        frameLayout.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                DialogsActivity dialogsActivity = DialogsActivity.this;
                dialogsActivity.floatingButtonTranslation = dialogsActivity.floatingHidden ? AndroidUtilities.dp(100.0f) : 0.0f;
                DialogsActivity.this.updateFloatingButtonOffset();
                DialogsActivity.this.floatingButtonContainer.setClickable(!DialogsActivity.this.floatingHidden);
                if (DialogsActivity.this.floatingButtonContainer != null) {
                    DialogsActivity.this.floatingButtonContainer.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                }
            }
        });
    }

    @Override
    public void onDialogDismiss(Dialog dialog) {
        AlertDialog alertDialog;
        super.onDialogDismiss(dialog);
        if (this.folderId != 0 || (alertDialog = this.permissionDialog) == null || dialog != alertDialog || getParentActivity() == null) {
            return;
        }
        askForPermissons(false);
    }

    @Override
    public boolean onFragmentCreate() {
        super.onFragmentCreate();
        Bundle bundle = this.arguments;
        if (bundle != null) {
            this.onlySelect = bundle.getBoolean("onlySelect", false);
            this.canSelectTopics = this.arguments.getBoolean("canSelectTopics", false);
            this.cantSendToChannels = this.arguments.getBoolean("cantSendToChannels", false);
            this.initialDialogsType = this.arguments.getInt("dialogsType", 0);
            this.isQuote = this.arguments.getBoolean("quote", false);
            this.isReplyTo = this.arguments.getBoolean("reply_to", false);
            this.selectAlertString = this.arguments.getString("selectAlertString");
            this.selectAlertStringGroup = this.arguments.getString("selectAlertStringGroup");
            this.addToGroupAlertString = this.arguments.getString("addToGroupAlertString");
            this.allowSwitchAccount = this.arguments.getBoolean("allowSwitchAccount");
            this.checkCanWrite = this.arguments.getBoolean("checkCanWrite", true);
            this.afterSignup = this.arguments.getBoolean("afterSignup", false);
            this.folderId = this.arguments.getInt("folderId", 0);
            this.resetDelegate = this.arguments.getBoolean("resetDelegate", true);
            this.messagesCount = this.arguments.getInt("messagesCount", 0);
            this.hasPoll = this.arguments.getInt("hasPoll", 0);
            this.hasInvoice = this.arguments.getBoolean("hasInvoice", false);
            this.showSetPasswordConfirm = this.arguments.getBoolean("showSetPasswordConfirm", this.showSetPasswordConfirm);
            this.otherwiseReloginDays = this.arguments.getInt("otherwiseRelogin");
            this.allowGroups = this.arguments.getBoolean("allowGroups", true);
            this.allowMegagroups = this.arguments.getBoolean("allowMegagroups", true);
            this.allowLegacyGroups = this.arguments.getBoolean("allowLegacyGroups", true);
            this.allowChannels = this.arguments.getBoolean("allowChannels", true);
            this.allowUsers = this.arguments.getBoolean("allowUsers", true);
            this.allowBots = this.arguments.getBoolean("allowBots", true);
            this.closeFragment = this.arguments.getBoolean("closeFragment", true);
            this.allowGlobalSearch = this.arguments.getBoolean("allowGlobalSearch", true);
            byte[] byteArray = this.arguments.getByteArray("requestPeerType");
            if (byteArray != null) {
                try {
                    SerializedData serializedData = new SerializedData(byteArray);
                    this.requestPeerType = TLRPC.RequestPeerType.TLdeserialize(serializedData, serializedData.readInt32(true), true);
                    serializedData.cleanup();
                } catch (Exception unused) {
                }
            }
            this.requestPeerBotId = this.arguments.getLong("requestPeerBotId", 0L);
        }
        if (this.initialDialogsType == 0) {
            this.askAboutContacts = MessagesController.getGlobalNotificationsSettings().getBoolean("askAboutContacts", true);
            SharedConfig.loadProxyList();
        }
        if (this.searchString == null) {
            this.currentConnectionState = getConnectionsManager().getConnectionState();
            getNotificationCenter().addObserver(this, NotificationCenter.dialogsNeedReload);
            NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.emojiLoaded);
            if (!this.onlySelect) {
                NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.closeSearchByActiveAction);
                NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.proxySettingsChanged);
                getNotificationCenter().addObserver(this, NotificationCenter.filterSettingsUpdated);
                getNotificationCenter().addObserver(this, NotificationCenter.dialogFiltersUpdated);
                getNotificationCenter().addObserver(this, NotificationCenter.dialogsUnreadCounterChanged);
            }
            getNotificationCenter().addObserver(this, NotificationCenter.updateInterfaces);
            getNotificationCenter().addObserver(this, NotificationCenter.encryptedChatUpdated);
            getNotificationCenter().addObserver(this, NotificationCenter.contactsDidLoad);
            getNotificationCenter().addObserver(this, NotificationCenter.appDidLogout);
            getNotificationCenter().addObserver(this, NotificationCenter.openedChatChanged);
            getNotificationCenter().addObserver(this, NotificationCenter.notificationsSettingsUpdated);
            getNotificationCenter().addObserver(this, NotificationCenter.messageReceivedByAck);
            getNotificationCenter().addObserver(this, NotificationCenter.messageReceivedByServer);
            getNotificationCenter().addObserver(this, NotificationCenter.messageSendError);
            getNotificationCenter().addObserver(this, NotificationCenter.needReloadRecentDialogsSearch);
            getNotificationCenter().addObserver(this, NotificationCenter.replyMessagesDidLoad);
            getNotificationCenter().addObserver(this, NotificationCenter.reloadHints);
            getNotificationCenter().addObserver(this, NotificationCenter.didUpdateConnectionState);
            getNotificationCenter().addObserver(this, NotificationCenter.onDownloadingFilesChanged);
            getNotificationCenter().addObserver(this, NotificationCenter.needDeleteDialog);
            getNotificationCenter().addObserver(this, NotificationCenter.folderBecomeEmpty);
            getNotificationCenter().addObserver(this, NotificationCenter.newSuggestionsAvailable);
            getNotificationCenter().addObserver(this, NotificationCenter.fileLoaded);
            getNotificationCenter().addObserver(this, NotificationCenter.fileLoadFailed);
            getNotificationCenter().addObserver(this, NotificationCenter.fileLoadProgressChanged);
            getNotificationCenter().addObserver(this, NotificationCenter.dialogsUnreadReactionsCounterChanged);
            getNotificationCenter().addObserver(this, NotificationCenter.forceImportContactsStart);
            getNotificationCenter().addObserver(this, NotificationCenter.userEmojiStatusUpdated);
            getNotificationCenter().addObserver(this, NotificationCenter.currentUserPremiumStatusChanged);
            NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.didSetPasscode);
            NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.appUpdateAvailable);
        }
        getNotificationCenter().addObserver(this, NotificationCenter.messagesDeleted);
        getNotificationCenter().addObserver(this, NotificationCenter.onDatabaseMigration);
        getNotificationCenter().addObserver(this, NotificationCenter.onDatabaseOpened);
        getNotificationCenter().addObserver(this, NotificationCenter.didClearDatabase);
        getNotificationCenter().addObserver(this, NotificationCenter.onDatabaseReset);
        getNotificationCenter().addObserver(this, NotificationCenter.storiesUpdated);
        getNotificationCenter().addObserver(this, NotificationCenter.storiesEnabledUpdate);
        getNotificationCenter().addObserver(this, NotificationCenter.unconfirmedAuthUpdate);
        getNotificationCenter().addObserver(this, NotificationCenter.premiumPromoUpdated);
        if (this.initialDialogsType == 0) {
            getNotificationCenter().addObserver(this, NotificationCenter.chatlistFolderUpdate);
            getNotificationCenter().addObserver(this, NotificationCenter.dialogTranslate);
        }
        getNotificationCenter().addObserver(this, NotificationCenter.starBalanceUpdated);
        getNotificationCenter().addObserver(this, NotificationCenter.starSubscriptionsLoaded);
        loadDialogs(getAccountInstance());
        getMessagesController().getStoriesController().loadAllStories();
        getMessagesController().loadPinnedDialogs(this.folderId, 0L, null);
        if (this.databaseMigrationHint != null && !getMessagesStorage().isDatabaseMigrationInProgress()) {
            View view = this.databaseMigrationHint;
            if (view.getParent() != null) {
                ((ViewGroup) view.getParent()).removeView(view);
            }
            this.databaseMigrationHint = null;
        }
        if (isArchive()) {
            getMessagesController().getStoriesController().loadHiddenStories();
        } else {
            getMessagesController().getStoriesController().loadStories();
        }
        getContactsController().loadGlobalPrivacySetting();
        if (getMessagesController().savedViewAsChats) {
            getMessagesController().getSavedMessagesController().preloadDialogs(true);
        }
        BirthdayController.getInstance(this.currentAccount).check();
        return true;
    }

    @Override
    public void onFragmentDestroy() {
        super.onFragmentDestroy();
        if (this.searchString == null) {
            getNotificationCenter().removeObserver(this, NotificationCenter.dialogsNeedReload);
            NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.emojiLoaded);
            if (!this.onlySelect) {
                NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.closeSearchByActiveAction);
                NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.proxySettingsChanged);
                getNotificationCenter().removeObserver(this, NotificationCenter.filterSettingsUpdated);
                getNotificationCenter().removeObserver(this, NotificationCenter.dialogFiltersUpdated);
                getNotificationCenter().removeObserver(this, NotificationCenter.dialogsUnreadCounterChanged);
            }
            getNotificationCenter().removeObserver(this, NotificationCenter.updateInterfaces);
            getNotificationCenter().removeObserver(this, NotificationCenter.encryptedChatUpdated);
            getNotificationCenter().removeObserver(this, NotificationCenter.contactsDidLoad);
            getNotificationCenter().removeObserver(this, NotificationCenter.appDidLogout);
            getNotificationCenter().removeObserver(this, NotificationCenter.openedChatChanged);
            getNotificationCenter().removeObserver(this, NotificationCenter.notificationsSettingsUpdated);
            getNotificationCenter().removeObserver(this, NotificationCenter.messageReceivedByAck);
            getNotificationCenter().removeObserver(this, NotificationCenter.messageReceivedByServer);
            getNotificationCenter().removeObserver(this, NotificationCenter.messageSendError);
            getNotificationCenter().removeObserver(this, NotificationCenter.needReloadRecentDialogsSearch);
            getNotificationCenter().removeObserver(this, NotificationCenter.replyMessagesDidLoad);
            getNotificationCenter().removeObserver(this, NotificationCenter.reloadHints);
            getNotificationCenter().removeObserver(this, NotificationCenter.didUpdateConnectionState);
            getNotificationCenter().removeObserver(this, NotificationCenter.onDownloadingFilesChanged);
            getNotificationCenter().removeObserver(this, NotificationCenter.needDeleteDialog);
            getNotificationCenter().removeObserver(this, NotificationCenter.folderBecomeEmpty);
            getNotificationCenter().removeObserver(this, NotificationCenter.newSuggestionsAvailable);
            getNotificationCenter().removeObserver(this, NotificationCenter.fileLoaded);
            getNotificationCenter().removeObserver(this, NotificationCenter.fileLoadFailed);
            getNotificationCenter().removeObserver(this, NotificationCenter.fileLoadProgressChanged);
            getNotificationCenter().removeObserver(this, NotificationCenter.dialogsUnreadReactionsCounterChanged);
            getNotificationCenter().removeObserver(this, NotificationCenter.forceImportContactsStart);
            getNotificationCenter().removeObserver(this, NotificationCenter.userEmojiStatusUpdated);
            getNotificationCenter().removeObserver(this, NotificationCenter.currentUserPremiumStatusChanged);
            NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.didSetPasscode);
            NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.appUpdateAvailable);
        }
        getNotificationCenter().removeObserver(this, NotificationCenter.messagesDeleted);
        getNotificationCenter().removeObserver(this, NotificationCenter.onDatabaseMigration);
        getNotificationCenter().removeObserver(this, NotificationCenter.onDatabaseOpened);
        getNotificationCenter().removeObserver(this, NotificationCenter.didClearDatabase);
        getNotificationCenter().removeObserver(this, NotificationCenter.onDatabaseReset);
        getNotificationCenter().removeObserver(this, NotificationCenter.storiesUpdated);
        getNotificationCenter().removeObserver(this, NotificationCenter.storiesEnabledUpdate);
        getNotificationCenter().removeObserver(this, NotificationCenter.unconfirmedAuthUpdate);
        getNotificationCenter().removeObserver(this, NotificationCenter.premiumPromoUpdated);
        if (this.initialDialogsType == 0) {
            getNotificationCenter().removeObserver(this, NotificationCenter.chatlistFolderUpdate);
            getNotificationCenter().removeObserver(this, NotificationCenter.dialogTranslate);
        }
        getNotificationCenter().removeObserver(this, NotificationCenter.starBalanceUpdated);
        getNotificationCenter().removeObserver(this, NotificationCenter.starSubscriptionsLoaded);
        ChatActivityEnterView chatActivityEnterView = this.commentView;
        if (chatActivityEnterView != null) {
            chatActivityEnterView.onDestroy();
        }
        UndoView undoView = this.undoView[0];
        if (undoView != null) {
            undoView.hide(true, 0);
        }
        this.notificationsLocker.unlock();
        this.delegate = null;
        SuggestClearDatabaseBottomSheet.dismissDialog();
    }

    @Override
    public List onGetDebugItems() {
        return Arrays.asList(new FloatingDebugController.DebugItem(LocaleController.getString(R.string.DebugDialogsActivity)), new FloatingDebugController.DebugItem(LocaleController.getString(R.string.ClearLocalDatabase), new Runnable() {
            @Override
            public final void run() {
                DialogsActivity.this.lambda$onGetDebugItems$132();
            }
        }), new FloatingDebugController.DebugItem(LocaleController.getString(R.string.DebugClearSendMessageAsPeers), new Runnable() {
            @Override
            public final void run() {
                DialogsActivity.this.lambda$onGetDebugItems$133();
            }
        }));
    }

    @Override
    public void onPause() {
        super.onPause();
        Bulletin bulletin = this.storiesBulletin;
        if (bulletin != null) {
            bulletin.hide();
            this.storiesBulletin = null;
        }
        RightSlidingDialogContainer rightSlidingDialogContainer = this.rightSlidingDialogContainer;
        if (rightSlidingDialogContainer != null) {
            rightSlidingDialogContainer.onPause();
        }
        ItemOptions itemOptions = this.filterOptions;
        if (itemOptions != null) {
            itemOptions.dismiss();
        }
        ChatActivityEnterView chatActivityEnterView = this.commentView;
        if (chatActivityEnterView != null) {
            chatActivityEnterView.onPause();
        }
        int i = 0;
        UndoView undoView = this.undoView[0];
        if (undoView != null) {
            undoView.hide(true, 0);
        }
        Bulletin.removeDelegate(this);
        if (this.viewPages == null) {
            return;
        }
        while (true) {
            ViewPage[] viewPageArr = this.viewPages;
            if (i >= viewPageArr.length) {
                return;
            }
            viewPageArr[i].dialogsAdapter.pause();
            i++;
        }
    }

    @Override
    public void onRequestPermissionsResultFragment(int r6, java.lang.String[] r7, int[] r8) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.DialogsActivity.onRequestPermissionsResultFragment(int, java.lang.String[], int[]):void");
    }

    @Override
    public void onResume() {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.DialogsActivity.onResume():void");
    }

    @Override
    public void onSlideProgress(boolean z, float f) {
        if (SharedConfig.getDevicePerformanceClass() > 0 && this.isSlideBackTransition && this.slideBackTransitionAnimator == null) {
            setSlideTransitionProgress(f);
        }
    }

    @Override
    public void onTransitionAnimationEnd(boolean z, boolean z2) {
        View view;
        RightSlidingDialogContainer rightSlidingDialogContainer = this.rightSlidingDialogContainer;
        if (rightSlidingDialogContainer != null && rightSlidingDialogContainer.hasFragment()) {
            this.rightSlidingDialogContainer.getFragment().onTransitionAnimationEnd(z, z2);
            return;
        }
        if (z && (view = this.blurredView) != null && view.getVisibility() == 0) {
            this.blurredView.setVisibility(8);
            this.blurredView.setBackground(null);
        }
        if (z && this.afterSignup) {
            try {
                this.fragmentView.performHapticFeedback(3, 2);
            } catch (Exception unused) {
            }
            if (getParentActivity() instanceof LaunchActivity) {
                ((LaunchActivity) getParentActivity()).getFireworksOverlay().start();
            }
        }
    }

    @Override
    public void onTransitionAnimationProgress(boolean z, float f) {
        RightSlidingDialogContainer rightSlidingDialogContainer = this.rightSlidingDialogContainer;
        if (rightSlidingDialogContainer != null && rightSlidingDialogContainer.hasFragment()) {
            this.rightSlidingDialogContainer.getFragment().onTransitionAnimationProgress(z, f);
            return;
        }
        View view = this.blurredView;
        if (view == null || view.getVisibility() != 0) {
            return;
        }
        if (z) {
            this.blurredView.setAlpha(1.0f - f);
        } else {
            this.blurredView.setAlpha(f);
        }
    }

    public boolean onlyDialogsAdapter() {
        SearchViewPager searchViewPager;
        return this.onlySelect || !((searchViewPager = this.searchViewPager) == null || searchViewPager.dialogsSearchAdapter.hasRecentSearch()) || (getMessagesController().getTotalDialogsCount() <= 10 && !this.hasStories);
    }

    @Override
    public void prepareFragmentToSlide(boolean z, boolean z2) {
        if (!z && z2) {
            this.isSlideBackTransition = true;
            setFragmentIsSliding(true);
        } else {
            this.slideBackTransitionAnimator = null;
            this.isSlideBackTransition = false;
            setFragmentIsSliding(false);
            setSlideTransitionProgress(1.0f);
        }
    }

    @Override
    public boolean presentFragment(BaseFragment baseFragment) {
        boolean presentFragment = super.presentFragment(baseFragment);
        if (presentFragment && this.viewPages != null) {
            int i = 0;
            while (true) {
                ViewPage[] viewPageArr = this.viewPages;
                if (i >= viewPageArr.length) {
                    break;
                }
                viewPageArr[i].dialogsAdapter.pause();
                i++;
            }
        }
        HintView2 hintView2 = this.storyHint;
        if (hintView2 != null) {
            hintView2.hide();
        }
        Bulletin.hideVisible();
        return presentFragment;
    }

    public void scrollToFolder(int i) {
        if (this.filterTabsView == null) {
            updateFilterTabs(true, true);
            if (this.filterTabsView == null) {
                return;
            }
        }
        int tabsCount = this.filterTabsView.getTabsCount() - 1;
        ArrayList<MessagesController.DialogFilter> dialogFilters = getMessagesController().getDialogFilters();
        int i2 = 0;
        while (true) {
            if (i2 >= dialogFilters.size()) {
                break;
            }
            if (dialogFilters.get(i2).id == i) {
                tabsCount = i2;
                break;
            }
            i2++;
        }
        FilterTabsView.Tab tab = this.filterTabsView.getTab(tabsCount);
        if (tab != null) {
            this.filterTabsView.scrollToTab(tab, tabsCount);
        } else {
            this.filterTabsView.selectLastTab();
        }
    }

    public void scrollToTop(boolean z, boolean z2) {
        int i = (this.viewPages[0].dialogsType == 0 && hasHiddenArchive() && this.viewPages[0].archivePullViewState == 2) ? 1 : 0;
        int i2 = (!this.hasStories || z2 || this.dialogStoriesCell.isExpanded()) ? 0 : -AndroidUtilities.dp(81.0f);
        if (z) {
            this.viewPages[0].scrollHelper.setScrollDirection(1);
            this.viewPages[0].scrollHelper.scrollToPosition(i, i2, false, true);
        } else {
            this.viewPages[0].layoutManager.scrollToPositionWithOffset(i, i2);
        }
        resetScroll();
    }

    public void search(String str, boolean z) {
        showSearch(true, false, z);
        this.actionBar.openSearchField(str, false);
    }

    public void setContactsAlpha(float f) {
        this.contactsAlpha = f;
        for (ViewPage viewPage : this.viewPages) {
            DialogsRecyclerView dialogsRecyclerView = viewPage.listView;
            for (int i = 0; i < dialogsRecyclerView.getChildCount(); i++) {
                View childAt = dialogsRecyclerView.getChildAt(i);
                if (childAt != null && dialogsRecyclerView.getChildAdapterPosition(childAt) >= viewPage.dialogsAdapter.getDialogsCount() + 1) {
                    childAt.setAlpha(f);
                }
            }
        }
    }

    public void setDelegate(DialogsActivityDelegate dialogsActivityDelegate) {
        this.delegate = dialogsActivityDelegate;
    }

    @Override
    public void setInPreviewMode(boolean z) {
        ActionBarMenuItem actionBarMenuItem;
        super.setInPreviewMode(z);
        if (!z && this.avatarContainer != null) {
            this.actionBar.setBackground(null);
            ((ViewGroup.MarginLayoutParams) this.actionBar.getLayoutParams()).topMargin = 0;
            this.actionBar.removeView(this.avatarContainer);
            this.avatarContainer = null;
            updateFilterTabs(false, false);
            this.floatingButton.setVisibility(0);
            ContentView contentView = (ContentView) this.fragmentView;
            FragmentContextView fragmentContextView = this.fragmentContextView;
            if (fragmentContextView != null) {
                contentView.addView(fragmentContextView);
            }
            FragmentContextView fragmentContextView2 = this.fragmentLocationContextView;
            if (fragmentContextView2 != null) {
                contentView.addView(fragmentContextView2);
            }
        }
        DialogStoriesCell dialogStoriesCell = this.dialogStoriesCell;
        if (dialogStoriesCell != null) {
            if (!this.dialogStoriesCellVisible || z) {
                dialogStoriesCell.setVisibility(8);
            } else {
                dialogStoriesCell.setVisibility(0);
            }
        }
        FrameLayout frameLayout = this.floatingButtonContainer;
        if (frameLayout != null) {
            frameLayout.setVisibility(((!this.onlySelect || this.initialDialogsType == 10) && this.folderId == 0 && !z) ? 0 : 8);
        }
        FrameLayout frameLayout2 = this.floatingButton2Container;
        if (frameLayout2 != null) {
            frameLayout2.setVisibility(((this.onlySelect && this.initialDialogsType != 10) || this.folderId != 0 || !this.storiesEnabled || ((actionBarMenuItem = this.searchItem) != null && actionBarMenuItem.isSearchFieldVisible()) || z) ? 8 : 0);
        }
        lambda$updateDialogsHint$29();
    }

    public void setInitialSearchString(String str) {
        this.initialSearchString = str;
    }

    public void setInitialSearchType(int i) {
        this.initialSearchType = i;
    }

    public void setOpenedDialogId(long j, long j2) {
        MessagesStorage.TopicKey topicKey = this.openedDialogId;
        topicKey.dialogId = j;
        topicKey.topicId = j2;
        ViewPage[] viewPageArr = this.viewPages;
        if (viewPageArr == null) {
            return;
        }
        for (ViewPage viewPage : viewPageArr) {
            if (viewPage.isDefaultDialogType() && AndroidUtilities.isTablet()) {
                viewPage.dialogsAdapter.setOpenedDialogId(this.openedDialogId.dialogId);
            }
        }
        updateVisibleRows(MessagesController.UPDATE_MASK_SELECT_DIALOG);
    }

    public void setPanTranslationOffset(float f) {
        this.floatingButtonPanOffset = f;
        updateFloatingButtonOffset();
    }

    @Override
    public void setProgressToDrawerOpened(float f) {
        if (SharedConfig.getDevicePerformanceClass() <= 0 || this.isSlideBackTransition) {
            return;
        }
        boolean z = f > 0.0f;
        if (this.searchIsShowed) {
            f = 0.0f;
            z = false;
        }
        if (z != this.isDrawerTransition) {
            this.isDrawerTransition = z;
            if (z) {
                setFragmentIsSliding(true);
            } else {
                setFragmentIsSliding(false);
            }
            View view = this.fragmentView;
            if (view != null) {
                view.requestLayout();
            }
        }
        setSlideTransitionProgress(1.0f - f);
    }

    public void setScrollDisabled(boolean z) {
        for (ViewPage viewPage : this.viewPages) {
            ((LinearLayoutManager) viewPage.listView.getLayoutManager()).setScrollDisabled(z);
        }
    }

    public void setSearchAnimationProgress(float f, boolean z) {
        this.searchAnimationProgress = f;
        boolean z2 = true;
        if (this.whiteActionBar) {
            int color = Theme.getColor(this.folderId != 0 ? Theme.key_actionBarDefaultArchivedIcon : Theme.key_actionBarDefaultIcon);
            ActionBar actionBar = this.actionBar;
            int i = Theme.key_actionBarActionModeDefaultIcon;
            actionBar.setItemsColor(ColorUtils.blendARGB(color, Theme.getColor(i), this.searchAnimationProgress), false);
            this.actionBar.setItemsColor(ColorUtils.blendARGB(Theme.getColor(i), Theme.getColor(i), this.searchAnimationProgress), true);
            this.actionBar.setItemsBackgroundColor(ColorUtils.blendARGB(Theme.getColor(this.folderId != 0 ? Theme.key_actionBarDefaultArchivedSelector : Theme.key_actionBarDefaultSelector), Theme.getColor(Theme.key_actionBarActionModeDefaultSelector), this.searchAnimationProgress), false);
        }
        View view = this.fragmentView;
        if (view != null) {
            view.invalidate();
        }
        DialogsHintCell dialogsHintCell = this.dialogsHintCell;
        if (dialogsHintCell != null) {
            dialogsHintCell.setAlpha(1.0f - f);
            if (this.dialogsHintCellVisible) {
                if (this.dialogsHintCell.getAlpha() == 0.0f) {
                    this.dialogsHintCell.setVisibility(4);
                } else {
                    this.dialogsHintCell.setVisibility(0);
                    ViewParent parent = this.dialogsHintCell.getParent();
                    if (parent != null) {
                        parent.requestLayout();
                    }
                }
            }
        }
        UnconfirmedAuthHintCell unconfirmedAuthHintCell = this.authHintCell;
        if (unconfirmedAuthHintCell != null) {
            unconfirmedAuthHintCell.setAlpha(1.0f - f);
            if (this.authHintCellVisible) {
                if (this.authHintCell.getAlpha() == 0.0f) {
                    this.authHintCell.setVisibility(4);
                } else {
                    this.authHintCell.setVisibility(0);
                }
            }
        }
        if (SharedConfig.getDevicePerformanceClass() != 0 && LiteMode.isEnabled(32768)) {
            z2 = false;
        }
        if (z) {
            ViewPage viewPage = this.viewPages[0];
            if (viewPage != null) {
                if (f < 1.0f) {
                    viewPage.setVisibility(0);
                }
                this.viewPages[0].setAlpha(1.0f - f);
                if (!z2) {
                    float f2 = (0.1f * f) + 0.9f;
                    this.viewPages[0].setScaleX(f2);
                    this.viewPages[0].setScaleY(f2);
                }
            }
            RightSlidingDialogContainer rightSlidingDialogContainer = this.rightSlidingDialogContainer;
            if (rightSlidingDialogContainer != null) {
                if (f >= 1.0f) {
                    rightSlidingDialogContainer.setVisibility(8);
                } else {
                    rightSlidingDialogContainer.setVisibility(0);
                    this.rightSlidingDialogContainer.setAlpha(1.0f - f);
                }
            }
            SearchViewPager searchViewPager = this.searchViewPager;
            if (searchViewPager != null) {
                searchViewPager.setAlpha(f);
                if (!z2) {
                    float f3 = ((1.0f - f) * 0.05f) + 1.0f;
                    this.searchViewPager.setScaleX(f3);
                    this.searchViewPager.setScaleY(f3);
                }
            }
            ActionBarMenuItem actionBarMenuItem = this.passcodeItem;
            if (actionBarMenuItem != null) {
                actionBarMenuItem.getIconView().setAlpha(1.0f - f);
            }
            ActionBarMenuItem actionBarMenuItem2 = this.downloadsItem;
            if (actionBarMenuItem2 != null) {
                actionBarMenuItem2.setAlpha(1.0f - f);
            }
            FilterTabsView filterTabsView = this.filterTabsView;
            if (filterTabsView != null && filterTabsView.getVisibility() == 0) {
                this.filterTabsView.getTabsContainer().setAlpha(1.0f - f);
            }
        }
        updateContextViewPosition();
    }

    public void setSearchString(String str) {
        this.searchString = str;
    }

    public void setShowSearch(String str, int i) {
        int positionForType;
        if (!this.searching) {
            this.initialSearchType = i;
            this.actionBar.openSearchField(str, false);
            return;
        }
        if (!this.searchItem.getSearchField().getText().toString().equals(str)) {
            this.searchItem.getSearchField().setText(str);
        }
        SearchViewPager searchViewPager = this.searchViewPager;
        if (searchViewPager == null || (positionForType = searchViewPager.getPositionForType(i)) < 0 || this.searchViewPager.getTabsView().getCurrentTabId() == positionForType) {
            return;
        }
        this.searchViewPager.getTabsView().scrollToTab(positionForType, positionForType);
    }

    public void setSideMenu(RecyclerView recyclerView) {
        this.sideMenu = recyclerView;
        int i = Theme.key_chats_menuBackground;
        recyclerView.setBackgroundColor(Theme.getColor(i));
        this.sideMenu.setGlowColor(Theme.getColor(i));
    }

    public boolean shouldShowNextButton(DialogsActivity dialogsActivity, ArrayList arrayList, CharSequence charSequence, boolean z) {
        return false;
    }

    public boolean showChatPreview(final org.telegram.ui.Cells.DialogCell r30) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.DialogsActivity.showChatPreview(org.telegram.ui.Cells.DialogCell):boolean");
    }

    public void showSelectStatusDialog() {
        int i;
        int i2;
        if (this.selectAnimatedEmojiDialog != null || SharedConfig.appLocked) {
            return;
        }
        if (!this.hasStories || this.dialogStoriesCell.isExpanded()) {
            final SelectAnimatedEmojiDialog.SelectAnimatedEmojiDialogWindow[] selectAnimatedEmojiDialogWindowArr = new SelectAnimatedEmojiDialog.SelectAnimatedEmojiDialogWindow[1];
            TLRPC.User currentUser = UserConfig.getInstance(UserConfig.selectedAccount).getCurrentUser();
            SimpleTextView titleTextView = this.actionBar.getTitleTextView();
            if (titleTextView == null || titleTextView.getRightDrawable() == null) {
                i = 0;
                i2 = 0;
            } else {
                this.statusDrawable.play();
                boolean z = this.statusDrawable.getDrawable() instanceof AnimatedEmojiDrawable;
                Rect rect = AndroidUtilities.rectTmp2;
                rect.set(titleTextView.getRightDrawable().getBounds());
                rect.offset((int) titleTextView.getX(), (int) titleTextView.getY());
                int dp = (-(this.actionBar.getHeight() - rect.centerY())) - AndroidUtilities.dp(16.0f);
                i = rect.centerX() - AndroidUtilities.dp(16.0f);
                DrawerProfileCell.AnimatedStatusView animatedStatusView = this.animatedStatusView;
                if (animatedStatusView != null) {
                    animatedStatusView.translate(rect.centerX(), rect.centerY());
                }
                i2 = dp;
            }
            int i3 = i2;
            SelectAnimatedEmojiDialog selectAnimatedEmojiDialog = new SelectAnimatedEmojiDialog(this, getContext(), true, Integer.valueOf(i), 0, getResourceProvider()) {
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
                    DialogsActivity.this.getMessagesController().updateEmojiStatus(emojiStatus);
                    if (l != null) {
                        DialogsActivity.this.animatedStatusView.animateChange(ReactionsLayoutInBubble.VisibleReaction.fromCustomEmoji(l));
                    }
                    if (selectAnimatedEmojiDialogWindowArr[0] != null) {
                        DialogsActivity.this.selectAnimatedEmojiDialog = null;
                        selectAnimatedEmojiDialogWindowArr[0].dismiss();
                    }
                }
            };
            if (currentUser != null && DialogObject.getEmojiStatusUntil(currentUser.emoji_status) > 0) {
                selectAnimatedEmojiDialog.setExpireDateHint(DialogObject.getEmojiStatusUntil(currentUser.emoji_status));
            }
            selectAnimatedEmojiDialog.setSelected(this.statusDrawable.getDrawable() instanceof AnimatedEmojiDrawable ? Long.valueOf(((AnimatedEmojiDrawable) this.statusDrawable.getDrawable()).getDocumentId()) : null);
            selectAnimatedEmojiDialog.setSaveState(1);
            selectAnimatedEmojiDialog.setScrimDrawable(this.statusDrawable, titleTextView);
            int i4 = -2;
            SelectAnimatedEmojiDialog.SelectAnimatedEmojiDialogWindow selectAnimatedEmojiDialogWindow = new SelectAnimatedEmojiDialog.SelectAnimatedEmojiDialogWindow(selectAnimatedEmojiDialog, i4, i4) {
                @Override
                public void dismiss() {
                    super.dismiss();
                    DialogsActivity.this.selectAnimatedEmojiDialog = null;
                }
            };
            this.selectAnimatedEmojiDialog = selectAnimatedEmojiDialogWindow;
            selectAnimatedEmojiDialogWindowArr[0] = selectAnimatedEmojiDialogWindow;
            selectAnimatedEmojiDialogWindow.showAsDropDown(this.actionBar, AndroidUtilities.dp(16.0f), i3, 48);
            selectAnimatedEmojiDialogWindowArr[0].dimBehind();
        }
    }

    public void switchToCurrentSelectedMode(boolean z) {
        ViewPage[] viewPageArr;
        int i = 0;
        int i2 = 0;
        while (true) {
            viewPageArr = this.viewPages;
            if (i2 >= viewPageArr.length) {
                break;
            }
            viewPageArr[i2].listView.stopScroll();
            i2++;
        }
        if (viewPageArr[z ? 1 : 0].selectedType < 0 || this.viewPages[z ? 1 : 0].selectedType >= getMessagesController().getDialogFilters().size()) {
            return;
        }
        MessagesController.DialogFilter dialogFilter = getMessagesController().getDialogFilters().get(this.viewPages[z ? 1 : 0].selectedType);
        if (dialogFilter.isDefault()) {
            this.viewPages[z ? 1 : 0].dialogsType = this.initialDialogsType;
            this.viewPages[z ? 1 : 0].listView.updatePullState();
        } else {
            if (this.viewPages[!z ? 1 : 0].dialogsType == 7) {
                this.viewPages[z ? 1 : 0].dialogsType = 8;
            } else {
                this.viewPages[z ? 1 : 0].dialogsType = 7;
            }
            this.viewPages[z ? 1 : 0].listView.setScrollEnabled(true);
            getMessagesController().selectDialogFilter(dialogFilter, this.viewPages[z ? 1 : 0].dialogsType == 8 ? 1 : 0);
        }
        this.viewPages[1].isLocked = dialogFilter.locked;
        this.viewPages[z ? 1 : 0].dialogsAdapter.setDialogsType(this.viewPages[z ? 1 : 0].dialogsType);
        LinearLayoutManager linearLayoutManager = this.viewPages[z ? 1 : 0].layoutManager;
        if (this.viewPages[z ? 1 : 0].dialogsType == 0 && hasHiddenArchive() && this.viewPages[z ? 1 : 0].archivePullViewState == 2) {
            i = 1;
        }
        linearLayoutManager.scrollToPositionWithOffset(i, (int) this.scrollYOffset);
        checkListLoad(this.viewPages[z ? 1 : 0]);
    }

    public void updateSpeedItem(boolean z) {
        boolean z2;
        if (this.speedItem == null) {
            return;
        }
        Iterator<MessageObject> it = getDownloadController().downloadingFiles.iterator();
        while (true) {
            if (!it.hasNext()) {
                z2 = false;
                break;
            }
            MessageObject next = it.next();
            if (next.getDocument() != null && next.getDocument().size >= 157286400) {
                z2 = true;
                break;
            }
        }
        Iterator<MessageObject> it2 = getDownloadController().recentDownloadingFiles.iterator();
        while (true) {
            if (!it2.hasNext()) {
                break;
            }
            MessageObject next2 = it2.next();
            if (next2.getDocument() != null && next2.getDocument().size >= 157286400) {
                z2 = true;
                break;
            }
        }
        final boolean z3 = !getUserConfig().isPremium() && !getMessagesController().premiumFeaturesBlocked() && z2 && z;
        if (z3 != (this.speedItem.getTag() != null)) {
            this.speedItem.setTag(z3 ? Boolean.TRUE : null);
            this.speedItem.setClickable(z3);
            AnimatorSet animatorSet = this.speedAnimator;
            if (animatorSet != null) {
                animatorSet.cancel();
            }
            AnimatorSet animatorSet2 = new AnimatorSet();
            animatorSet2.setDuration(180L);
            animatorSet2.playTogether(ObjectAnimator.ofFloat(this.speedItem, (Property<ActionBarMenuItem, Float>) View.ALPHA, z3 ? 1.0f : 0.0f), ObjectAnimator.ofFloat(this.speedItem, (Property<ActionBarMenuItem, Float>) View.SCALE_X, z3 ? 1.0f : 0.5f), ObjectAnimator.ofFloat(this.speedItem, (Property<ActionBarMenuItem, Float>) View.SCALE_Y, z3 ? 1.0f : 0.5f));
            animatorSet2.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animator) {
                    int i = Build.VERSION.SDK_INT;
                    if (i >= 21) {
                        AnimatedVectorDrawable m = AnimatedVectorDrawableCompat$$ExternalSyntheticApiModelOutline0.m(DialogsActivity.this.speedItem.getIconView().getDrawable());
                        if (!z3) {
                            if (i >= 23) {
                                m.reset();
                                return;
                            } else {
                                m.setVisible(false, true);
                                return;
                            }
                        }
                        m.start();
                        if (SharedConfig.getDevicePerformanceClass() != 0) {
                            TLRPC.TL_help_premiumPromo premiumPromo = MediaDataController.getInstance(((BaseFragment) DialogsActivity.this).currentAccount).getPremiumPromo();
                            String featureTypeToServerString = PremiumPreviewFragment.featureTypeToServerString(2);
                            if (premiumPromo != null) {
                                int i2 = 0;
                                while (true) {
                                    if (i2 >= premiumPromo.video_sections.size()) {
                                        i2 = -1;
                                        break;
                                    } else if (premiumPromo.video_sections.get(i2).equals(featureTypeToServerString)) {
                                        break;
                                    } else {
                                        i2++;
                                    }
                                }
                                if (i2 != -1) {
                                    FileLoader.getInstance(((BaseFragment) DialogsActivity.this).currentAccount).loadFile(premiumPromo.videos.get(i2), premiumPromo, 3, 0);
                                }
                            }
                        }
                    }
                }
            });
            animatorSet2.start();
            this.speedAnimator = animatorSet2;
        }
    }

    public void updateStatus(org.telegram.tgnet.TLRPC.User r5, boolean r6) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.DialogsActivity.updateStatus(org.telegram.tgnet.TLRPC$User, boolean):void");
    }

    public void updateStoriesVisibility(boolean z) {
        final boolean z2;
        int i = 0;
        if (this.dialogStoriesCell == null || this.storiesVisibilityAnimator != null) {
            return;
        }
        RightSlidingDialogContainer rightSlidingDialogContainer = this.rightSlidingDialogContainer;
        if ((rightSlidingDialogContainer != null && rightSlidingDialogContainer.hasFragment()) || this.searchIsShowed || this.actionBar.isActionModeShowed() || this.onlySelect) {
            return;
        }
        if (StoryRecorder.isVisible() || (getLastStoryViewer() != null && getLastStoryViewer().isFullyVisible())) {
            z = false;
        }
        boolean z3 = !isArchive() && getStoriesController().hasOnlySelfStories();
        if (isArchive()) {
            z2 = !getStoriesController().getHiddenList().isEmpty();
        } else {
            z2 = !z3 && getStoriesController().hasStories();
            z3 = getStoriesController().hasOnlySelfStories();
        }
        this.hasOnlySlefStories = z3;
        boolean z4 = this.dialogStoriesCellVisible;
        boolean z5 = z3 || z2;
        this.dialogStoriesCellVisible = z5;
        if (z2 || z5) {
            this.dialogStoriesCell.updateItems(z, z5 != z4);
        }
        boolean z6 = this.dialogStoriesCellVisible;
        int i2 = 8;
        if (z6 != z4) {
            if (z) {
                ValueAnimator valueAnimator = this.storiesVisibilityAnimator2;
                if (valueAnimator != null) {
                    valueAnimator.cancel();
                }
                if (this.dialogStoriesCellVisible && !isInPreviewMode()) {
                    this.dialogStoriesCell.setVisibility(0);
                }
                ValueAnimator ofFloat = ValueAnimator.ofFloat(this.progressToDialogStoriesCell, this.dialogStoriesCellVisible ? 1.0f : 0.0f);
                this.storiesVisibilityAnimator2 = ofFloat;
                ofFloat.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator valueAnimator2) {
                        DialogsActivity.this.progressToDialogStoriesCell = ((Float) valueAnimator2.getAnimatedValue()).floatValue();
                        View view = DialogsActivity.this.fragmentView;
                        if (view != null) {
                            view.invalidate();
                        }
                    }
                });
                this.storiesVisibilityAnimator2.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animator) {
                        DialogsActivity dialogsActivity = DialogsActivity.this;
                        boolean z7 = dialogsActivity.dialogStoriesCellVisible;
                        dialogsActivity.progressToDialogStoriesCell = z7 ? 1.0f : 0.0f;
                        if (!z7) {
                            dialogsActivity.dialogStoriesCell.setVisibility(8);
                        }
                        View view = DialogsActivity.this.fragmentView;
                        if (view != null) {
                            view.invalidate();
                        }
                    }
                });
                this.storiesVisibilityAnimator2.setDuration(200L);
                this.storiesVisibilityAnimator2.setInterpolator(CubicBezierInterpolator.DEFAULT);
                this.storiesVisibilityAnimator2.start();
            } else {
                this.dialogStoriesCell.setVisibility((!z6 || isInPreviewMode()) ? 8 : 0);
                this.progressToDialogStoriesCell = this.dialogStoriesCellVisible ? 1.0f : 0.0f;
                View view = this.fragmentView;
                if (view != null) {
                    view.invalidate();
                }
            }
        }
        if (z2 == this.animateToHasStories) {
            return;
        }
        this.animateToHasStories = z2;
        if (z2) {
            this.dialogStoriesCell.setProgressToCollapse(1.0f, false);
        }
        if (z && !isInPreviewMode()) {
            this.dialogStoriesCell.setVisibility(0);
            float f = -this.scrollYOffset;
            float maxScrollYOffset = z2 ? 0.0f : getMaxScrollYOffset();
            ValueAnimator ofFloat2 = ValueAnimator.ofFloat(0.0f, 1.0f);
            this.storiesVisibilityAnimator = ofFloat2;
            ofFloat2.addUpdateListener(new ValueAnimator.AnimatorUpdateListener(f, z2, maxScrollYOffset) {
                int currentValue;
                final float val$fromScrollY;
                final boolean val$newVisibility;
                final float val$toScrollY;

                {
                    this.val$fromScrollY = f;
                    this.val$newVisibility = z2;
                    this.val$toScrollY = maxScrollYOffset;
                    this.currentValue = (int) f;
                }

                @Override
                public void onAnimationUpdate(ValueAnimator valueAnimator2) {
                    DialogsActivity.this.progressToShowStories = ((Float) valueAnimator2.getAnimatedValue()).floatValue();
                    if (!this.val$newVisibility) {
                        DialogsActivity dialogsActivity = DialogsActivity.this;
                        dialogsActivity.progressToShowStories = 1.0f - dialogsActivity.progressToShowStories;
                    }
                    int lerp = (int) AndroidUtilities.lerp(this.val$fromScrollY, this.val$toScrollY, ((Float) valueAnimator2.getAnimatedValue()).floatValue());
                    int i3 = lerp - this.currentValue;
                    this.currentValue = lerp;
                    DialogsActivity.this.viewPages[0].listView.scrollBy(0, i3);
                    View view2 = DialogsActivity.this.fragmentView;
                    if (view2 != null) {
                        view2.invalidate();
                    }
                }
            });
            this.storiesVisibilityAnimator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animator) {
                    DialogsActivity dialogsActivity = DialogsActivity.this;
                    dialogsActivity.storiesVisibilityAnimator = null;
                    boolean z7 = z2;
                    dialogsActivity.hasStories = z7;
                    if (!z7 && !dialogsActivity.hasOnlySlefStories) {
                        dialogsActivity.dialogStoriesCell.setVisibility(8);
                    }
                    if (z2) {
                        DialogsActivity.this.scrollAdditionalOffset = -AndroidUtilities.dp(81.0f);
                        DialogsActivity.this.setScrollY(-r3.getMaxScrollYOffset());
                    } else {
                        DialogsActivity.this.setScrollY(0.0f);
                        DialogsActivity.this.scrollAdditionalOffset = AndroidUtilities.dp(81.0f);
                    }
                    for (int i3 = 0; i3 < DialogsActivity.this.viewPages.length; i3++) {
                        if (DialogsActivity.this.viewPages[i3] != null) {
                            DialogsActivity.this.viewPages[i3].listView.requestLayout();
                        }
                    }
                    View view2 = DialogsActivity.this.fragmentView;
                    if (view2 != null) {
                        view2.requestLayout();
                    }
                }
            });
            this.storiesVisibilityAnimator.setDuration(200L);
            this.storiesVisibilityAnimator.setInterpolator(CubicBezierInterpolator.DEFAULT);
            this.storiesVisibilityAnimator.start();
            return;
        }
        this.progressToShowStories = z2 ? 1.0f : 0.0f;
        this.hasStories = z2;
        DialogStoriesCell dialogStoriesCell = this.dialogStoriesCell;
        if ((z2 || this.hasOnlySlefStories) && !isInPreviewMode()) {
            i2 = 0;
        }
        dialogStoriesCell.setVisibility(i2);
        if (z2) {
            this.scrollAdditionalOffset = -AndroidUtilities.dp(81.0f);
            setScrollY(-getMaxScrollYOffset());
        } else {
            setScrollY(0.0f);
        }
        while (true) {
            ViewPage[] viewPageArr = this.viewPages;
            if (i >= viewPageArr.length) {
                break;
            }
            ViewPage viewPage = viewPageArr[i];
            if (viewPage != null) {
                viewPage.listView.requestLayout();
            }
            i++;
        }
        View view2 = this.fragmentView;
        if (view2 != null) {
            view2.requestLayout();
            this.fragmentView.invalidate();
        }
    }
}
