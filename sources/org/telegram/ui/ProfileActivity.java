package org.telegram.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.StateListAnimator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ConfigurationInfo;
import android.content.pm.PackageInfo;
import android.content.res.Configuration;
import android.database.DataSetObserver;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.LinearGradient;
import android.graphics.Outline;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.media.MediaCodecInfo;
import android.media.MediaCodecList;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.style.CharacterStyle;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.URLSpan;
import android.util.Property;
import android.util.SparseArray;
import android.util.SparseIntArray;
import android.view.MotionEvent;
import android.view.TextureView;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewOutlineProvider;
import android.view.ViewTreeObserver;
import android.view.accessibility.AccessibilityNodeInfo;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.webkit.CookieManager;
import android.webkit.WebStorage;
import android.webkit.WebView;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;
import androidx.collection.LongSparseArray;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.core.graphics.ColorUtils;
import androidx.core.math.MathUtils;
import androidx.core.view.NestedScrollingParent3;
import androidx.core.view.NestedScrollingParentHelper;
import androidx.interpolator.view.animation.FastOutSlowInInterpolator;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;
import com.google.android.exoplayer2.util.Consumer;
import j$.util.Comparator$CC;
import j$.util.Comparator$EL;
import j$.util.function.Consumer;
import j$.util.function.ToIntFunction;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.lang.Thread;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicReference;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import org.telegram.PhoneFormat.PhoneFormat;
import org.telegram.messenger.AccountInstance;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.AuthTokensHelper;
import org.telegram.messenger.BillingController;
import org.telegram.messenger.BillingController$$ExternalSyntheticLambda10;
import org.telegram.messenger.BirthdayController;
import org.telegram.messenger.BuildVars;
import org.telegram.messenger.ChannelBoostsController;
import org.telegram.messenger.ChatObject;
import org.telegram.messenger.ChatThemeController;
import org.telegram.messenger.ContactsController;
import org.telegram.messenger.DialogObject;
import org.telegram.messenger.DispatchQueue;
import org.telegram.messenger.DocumentObject;
import org.telegram.messenger.FileLoader;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.FlagSecureReason;
import org.telegram.messenger.ImageLoader;
import org.telegram.messenger.ImageLocation;
import org.telegram.messenger.ImageReceiver;
import org.telegram.messenger.LanguageDetector;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MediaDataController;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.MessagesStorage;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.NotificationsController;
import org.telegram.messenger.R;
import org.telegram.messenger.SendMessagesHelper;
import org.telegram.messenger.SharedConfig;
import org.telegram.messenger.SvgHelper;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.UserObject;
import org.telegram.messenger.Utilities;
import org.telegram.messenger.browser.Browser;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.SerializedData;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.tgnet.tl.TL_account;
import org.telegram.tgnet.tl.TL_bots;
import org.telegram.tgnet.tl.TL_fragment;
import org.telegram.tgnet.tl.TL_payments;
import org.telegram.tgnet.tl.TL_stories;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.ActionBarMenu;
import org.telegram.ui.ActionBar.ActionBarMenuItem;
import org.telegram.ui.ActionBar.ActionBarMenuSubItem;
import org.telegram.ui.ActionBar.ActionBarPopupWindow;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.BackDrawable;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.INavigationLayout;
import org.telegram.ui.ActionBar.OKLCH;
import org.telegram.ui.ActionBar.SimpleTextView;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ActionBar.ThemeDescription;
import org.telegram.ui.Business.OpeningHoursActivity;
import org.telegram.ui.Business.ProfileHoursCell;
import org.telegram.ui.Business.ProfileLocationCell;
import org.telegram.ui.Cells.AboutLinkCell;
import org.telegram.ui.Cells.CheckBoxCell;
import org.telegram.ui.Cells.DividerCell;
import org.telegram.ui.Cells.DrawerProfileCell;
import org.telegram.ui.Cells.GraySectionCell;
import org.telegram.ui.Cells.HeaderCell;
import org.telegram.ui.Cells.NotificationsCheckCell;
import org.telegram.ui.Cells.ProfileChannelCell;
import org.telegram.ui.Cells.SettingsSearchCell;
import org.telegram.ui.Cells.SettingsSuggestionCell;
import org.telegram.ui.Cells.ShadowSectionCell;
import org.telegram.ui.Cells.TextCell;
import org.telegram.ui.Cells.TextCheckCell;
import org.telegram.ui.Cells.TextDetailCell;
import org.telegram.ui.Cells.TextInfoPrivacyCell;
import org.telegram.ui.Cells.UserCell;
import org.telegram.ui.ChatActivity;
import org.telegram.ui.ChatRightsEditActivity;
import org.telegram.ui.Components.AlertsCreator;
import org.telegram.ui.Components.AnimatedColor;
import org.telegram.ui.Components.AnimatedEmojiDrawable;
import org.telegram.ui.Components.AnimatedFileDrawable;
import org.telegram.ui.Components.AnimatedFloat;
import org.telegram.ui.Components.AnimatedTextView;
import org.telegram.ui.Components.AnimationProperties;
import org.telegram.ui.Components.AudioPlayerAlert;
import org.telegram.ui.Components.AutoDeletePopupWrapper;
import org.telegram.ui.Components.AvatarDrawable;
import org.telegram.ui.Components.BackButtonMenu;
import org.telegram.ui.Components.BackupImageView;
import org.telegram.ui.Components.Bulletin;
import org.telegram.ui.Components.BulletinFactory;
import org.telegram.ui.Components.ButtonBounce;
import org.telegram.ui.Components.CanvasButton;
import org.telegram.ui.Components.ChatActivityInterface;
import org.telegram.ui.Components.ChatNotificationsPopupWrapper;
import org.telegram.ui.Components.CombinedDrawable;
import org.telegram.ui.Components.CrossfadeDrawable;
import org.telegram.ui.Components.CubicBezierInterpolator;
import org.telegram.ui.Components.EmojiPacksAlert;
import org.telegram.ui.Components.FloatingDebug.FloatingDebugController;
import org.telegram.ui.Components.HintView;
import org.telegram.ui.Components.ImageUpdater;
import org.telegram.ui.Components.InstantCameraView;
import org.telegram.ui.Components.ItemOptions;
import org.telegram.ui.Components.JoinGroupAlert;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.LinkSpanDrawable;
import org.telegram.ui.Components.MessagePrivateSeenView;
import org.telegram.ui.Components.Paint.PersistColorPalette;
import org.telegram.ui.Components.Premium.LimitReachedBottomSheet;
import org.telegram.ui.Components.Premium.PremiumFeatureBottomSheet;
import org.telegram.ui.Components.Premium.PremiumPreviewBottomSheet;
import org.telegram.ui.Components.Premium.ProfilePremiumCell;
import org.telegram.ui.Components.Premium.boosts.UserSelectorBottomSheet;
import org.telegram.ui.Components.ProfileGalleryView;
import org.telegram.ui.Components.RLottieDrawable;
import org.telegram.ui.Components.RLottieImageView;
import org.telegram.ui.Components.RadialProgressView;
import org.telegram.ui.Components.Reactions.ReactionsLayoutInBubble;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.Components.ScamDrawable;
import org.telegram.ui.Components.ShareAlert;
import org.telegram.ui.Components.SharedMediaLayout;
import org.telegram.ui.Components.SizeNotifierFrameLayout;
import org.telegram.ui.Components.StickerEmptyView;
import org.telegram.ui.Components.TimerDrawable;
import org.telegram.ui.Components.TranslateAlert2;
import org.telegram.ui.Components.TypefaceSpan;
import org.telegram.ui.Components.UndoView;
import org.telegram.ui.Components.VectorAvatarThumbDrawable;
import org.telegram.ui.Components.voip.VoIPHelper;
import org.telegram.ui.ContactAddActivity;
import org.telegram.ui.DialogsActivity;
import org.telegram.ui.Gifts.GiftSheet;
import org.telegram.ui.Gifts.ProfileGiftsContainer;
import org.telegram.ui.GroupCreateActivity;
import org.telegram.ui.PhotoViewer;
import org.telegram.ui.PinchToZoomHelper;
import org.telegram.ui.ProfileActivity;
import org.telegram.ui.ProfileBirthdayEffect;
import org.telegram.ui.SelectAnimatedEmojiDialog;
import org.telegram.ui.Stars.BotStarsActivity;
import org.telegram.ui.Stars.BotStarsController;
import org.telegram.ui.Stars.StarsIntroActivity;
import org.telegram.ui.Stories.ProfileStoriesView;
import org.telegram.ui.Stories.StoriesController;
import org.telegram.ui.Stories.StoriesListPlaceProvider;
import org.telegram.ui.Stories.StoryViewer;
import org.telegram.ui.Stories.recorder.ButtonWithCounterView;
import org.telegram.ui.Stories.recorder.DualCameraView;
import org.telegram.ui.Stories.recorder.StoryRecorder;
import org.telegram.ui.bots.AffiliateProgramFragment;
import org.telegram.ui.bots.BotBiometry;
import org.telegram.ui.bots.BotDownloads;
import org.telegram.ui.bots.BotLocation;
import org.telegram.ui.bots.ChannelAffiliateProgramsFragment;
import org.telegram.ui.bots.SetupEmojiStatusSheet;

public class ProfileActivity extends BaseFragment implements NotificationCenter.NotificationCenterDelegate, DialogsActivity.DialogsActivityDelegate, SharedMediaLayout.SharedMediaPreloaderDelegate, ImageUpdater.ImageUpdaterDelegate, SharedMediaLayout.Delegate {
    private Property ACTIONBAR_HEADER_PROGRESS;
    private final Property HEADER_SHADOW;
    private AboutLinkCell aboutLinkCell;
    private int actionBarAnimationColorFrom;
    private int actionBarBackgroundColor;
    private Paint actionBarBackgroundPaint;
    private final SparseIntArray adaptedColors;
    private int addMemberRow;
    private int addToContactsRow;
    private int addToGroupButtonRow;
    private int addToGroupInfoRow;
    private int administratorsRow;
    private int affiliateRow;
    private boolean allowProfileAnimation;
    private boolean allowPullingDown;
    private DrawerProfileCell.AnimatedStatusView animatedStatusView;
    private ActionBarMenuItem animatingItem;
    private Runnable applyBulletin;
    private ActionBarMenuSubItem autoDeleteItem;
    TimerDrawable autoDeleteItemDrawable;
    AutoDeletePopupWrapper autoDeletePopupWrapper;
    private TLRPC.FileLocation avatar;
    private AnimatorSet avatarAnimation;
    private float avatarAnimationProgress;
    private TLRPC.FileLocation avatarBig;
    private int avatarColor;
    private FrameLayout avatarContainer;
    private FrameLayout avatarContainer2;
    private AvatarDrawable avatarDrawable;
    private AvatarImageView avatarImage;
    private RadialProgressView avatarProgressView;
    private float avatarScale;
    int avatarUploadingRequest;
    private float avatarX;
    private float avatarY;
    private ProfileGalleryView avatarsViewPager;
    private PagerIndicatorView avatarsViewPagerIndicatorView;
    private int balanceDividerRow;
    private long banFromGroup;
    private int bioRow;
    private ProfileBirthdayEffect birthdayEffect;
    public ProfileBirthdayEffect.BirthdayEffectFetcher birthdayFetcher;
    public int birthdayRow;
    private int bizHoursRow;
    private int bizLocationRow;
    private int blockedUsersRow;
    private View blurredView;
    private int botAppRow;
    private BotBiometry botBiometry;
    private TL_bots.BotInfo botInfo;
    private BotLocation botLocation;
    private int botPermissionBiometry;
    private int botPermissionEmojiStatus;
    private int botPermissionEmojiStatusReqId;
    private int botPermissionLocation;
    private int botPermissionsDivider;
    private int botPermissionsHeader;
    private int botStarsBalanceRow;
    private int botTonBalanceRow;
    private final AnimatedEmojiDrawable.SwapAnimatedEmojiDrawable[] botVerificationDrawable;
    private ButtonWithCounterView[] bottomButton;
    private FrameLayout[] bottomButtonContainer;
    private SpannableStringBuilder bottomButtonPostText;
    private FrameLayout bottomButtonsContainer;
    private int bottomPaddingRow;
    private int businessRow;
    private ActionBarMenuItem callItem;
    private boolean callItemVisible;
    private RLottieDrawable cameraDrawable;
    private boolean canSearchMembers;
    private RLottieDrawable cellCameraDrawable;
    private int channelBalanceRow;
    private int channelDividerRow;
    private int channelInfoRow;
    private int channelRow;
    private long chatId;
    private TLRPC.ChatFull chatInfo;
    private int chatRow;
    private int clearLogsRow;
    private NestedFrameLayout contentView;
    public boolean createdBirthdayFetcher;
    private boolean creatingChat;
    private CharSequence currentBio;
    private TLRPC.ChannelParticipant currentChannelParticipant;
    private TLRPC.Chat currentChat;
    private TLRPC.EncryptedChat currentEncryptedChat;
    private float currentExpanAnimatorFracture;
    private float currentExpandAnimatorValue;
    private TL_account.TL_password currentPassword;
    private float customAvatarProgress;
    private float customPhotoOffset;
    private int dataRow;
    private int debugHeaderRow;
    private int devicesRow;
    private int devicesSectionRow;
    private long dialogId;
    private boolean disableProfileAnimation;
    private boolean doNotSetForeground;
    private ActionBarMenuSubItem editColorItem;
    private ActionBarMenuItem editItem;
    private boolean editItemVisible;
    private final AnimatedEmojiDrawable.SwapAnimatedEmojiDrawable[] emojiStatusDrawable;
    private int emptyRow;
    private StickerEmptyView emptyView;
    private ValueAnimator expandAnimator;
    private float[] expandAnimatorValues;
    private boolean expandPhoto;
    private float expandProgress;
    private float extraHeight;
    private ImageReceiver fallbackImage;
    private int faqRow;
    private int filtersRow;
    private boolean firstLayout;
    private FlagSecureReason flagSecure;
    RLottieImageView floatingButton;
    FrameLayout floatingButtonContainer;
    float floatingButtonHideProgress;
    boolean floatingHidden;
    private final AccelerateDecelerateInterpolator floatingInterpolator;
    private boolean fragmentOpened;
    private boolean fragmentViewAttached;
    private boolean fullyVisible;
    private HintView fwdRestrictedHint;
    private int graceSuggestionRow;
    private int graceSuggestionSectionRow;
    private boolean hasCustomPhoto;
    private boolean hasFallbackPhoto;
    private boolean hasVoiceChatItem;
    private AnimatorSet headerAnimatorSet;
    protected float headerShadowAlpha;
    private AnimatorSet headerShadowAnimatorSet;
    private int helpHeaderRow;
    private int helpSectionCell;
    private boolean hoursExpanded;
    private boolean hoursShownMine;
    private ImageUpdater imageUpdater;
    private int infoAffiliateRow;
    private int infoEndRow;
    private int infoHeaderRow;
    private int infoSectionRow;
    private int infoStartRow;
    private float initialAnimationExtraHeight;
    private boolean invalidateScroll;
    private boolean isBot;
    public boolean isFragmentOpened;
    private boolean isFragmentPhoneNumber;
    private boolean isInLandscapeMode;
    private boolean[] isOnline;
    private boolean isPulledDown;
    private boolean isQrItemVisible;
    private boolean isTopic;
    private int joinRow;
    private int languageRow;
    private float lastEmojiStatusProgress;
    private int lastMeasuredContentHeight;
    private int lastMeasuredContentWidth;
    private int lastSectionRow;
    private LinearLayoutManager layoutManager;
    private ActionBarMenuSubItem linkItem;
    private ListAdapter listAdapter;
    private int listContentHeight;
    private RecyclerListView listView;
    private float listViewVelocityY;
    private int liteModeRow;
    private boolean loadingBoostsStats;
    private CharacterStyle loadingSpan;
    private boolean loadingUsers;
    private int locationRow;
    private Drawable lockIconDrawable;
    private AudioPlayerAlert.ClippingTextViewSwitcher mediaCounterTextView;
    private float mediaHeaderAnimationProgress;
    private boolean mediaHeaderVisible;
    private int membersEndRow;
    private int membersHeaderRow;
    private int membersSectionRow;
    private int membersStartRow;
    private long mergeDialogId;
    public boolean myProfile;
    private SimpleTextView[] nameTextView;
    private String nameTextViewRightDrawable2ContentDescription;
    private String nameTextViewRightDrawableContentDescription;
    private float nameX;
    private float nameY;
    private int navigationBarAnimationColorFrom;
    private boolean needSendMessage;
    private boolean needStarImage;
    private boolean needTimerImage;
    private int notificationRow;
    private int notificationsDividerRow;
    HashSet notificationsExceptionTopics;
    private int notificationsRow;
    private int notificationsSimpleRow;
    private int numberRow;
    private int numberSectionRow;
    private int onlineCount;
    private SimpleTextView[] onlineTextView;
    private float onlineX;
    private float onlineY;
    private boolean openAnimationInProgress;
    public boolean openGifts;
    private boolean openSimilar;
    private boolean openingAvatar;
    private ActionBarMenuItem otherItem;
    private int overlayCountVisible;
    private OverlaysView overlaysView;
    private LongSparseArray participantsMap;
    private int passwordSuggestionRow;
    private int passwordSuggestionSectionRow;
    private MessagesController.PeerColor peerColor;
    private int phoneRow;
    private int phoneSuggestionRow;
    private int phoneSuggestionSectionRow;
    float photoDescriptionProgress;
    PinchToZoomHelper pinchToZoomHelper;
    private int playProfileAnimation;
    private int policyRow;
    private HashMap positionToOffset;
    private boolean preloadedChannelEmojiStatuses;
    private final CrossfadeDrawable[] premiumCrossfadeDrawable;
    private int premiumGiftingRow;
    private int premiumRow;
    private int premiumSectionsRow;
    private final Drawable[] premiumStarDrawable;
    private ImageLocation prevLoadedImageLocation;
    ChatActivityInterface previousTransitionFragment;
    BaseFragment previousTransitionMainFragment;
    private int privacyRow;
    public ProfileChannelCell.ChannelMessageFetcher profileChannelMessageFetcher;
    boolean profileTransitionInProgress;
    private PhotoViewer.PhotoViewerProvider provider;
    private ActionBarMenuItem qrItem;
    private AnimatorSet qrItemAnimation;
    private int questionRow;
    private boolean recreateMenuAfterAnimation;
    private Rect rect;
    private int reportDividerRow;
    private long reportReactionFromDialogId;
    private int reportReactionMessageId;
    private int reportReactionRow;
    private int reportRow;
    private boolean reportSpam;
    private Theme.ResourcesProvider resourcesProvider;
    private int rowCount;
    public boolean saved;
    int savedScrollOffset;
    int savedScrollPosition;
    boolean savedScrollToSharedMedia;
    private ScamDrawable scamDrawable;
    private AnimatorSet scrimAnimatorSet;
    private Paint scrimPaint;
    private View scrimView;
    private boolean scrolling;
    private SearchAdapter searchAdapter;
    private ActionBarMenuItem searchItem;
    private RecyclerListView searchListView;
    private boolean searchMode;
    private int searchTransitionOffset;
    private float searchTransitionProgress;
    private Animator searchViewTransition;
    private int secretSettingsSectionRow;
    private SelectAnimatedEmojiDialog.SelectAnimatedEmojiDialogWindow selectAnimatedEmojiDialog;
    private long selectedUser;
    private int sendLastLogsRow;
    private int sendLogsRow;
    private int sendMessageRow;
    private TextCell setAvatarCell;
    private int setAvatarRow;
    private int setAvatarSectionRow;
    private ActionBarMenuSubItem setUsernameItem;
    private int setUsernameRow;
    private int settingsKeyRow;
    private int settingsRow;
    private int settingsSectionRow;
    private int settingsSectionRow2;
    private int settingsTimerRow;
    public SharedMediaLayout sharedMediaLayout;
    private boolean sharedMediaLayoutAttached;
    private SharedMediaLayout.SharedMediaPreloader sharedMediaPreloader;
    private int sharedMediaRow;
    private boolean showAddToContacts;
    boolean showBoostsAlert;
    private ShowDrawable showStatusButton;
    private ArrayList sortedUsers;
    private ImageView starBgItem;
    private ImageView starFgItem;
    private int starsRow;
    private int stickersRow;
    private ProfileStoriesView storyView;
    private int subscribersRequestsRow;
    private int subscribersRow;
    private int switchBackendRow;
    private ImageView timeItem;
    private TimerDrawable timerDrawable;
    private float titleAnimationsYDiff;
    private TopView topView;
    private long topicId;
    private boolean transitionAnimationInProress;
    private int transitionIndex;
    private View transitionOnlineText;
    private ImageView ttlIconView;
    private int unblockRow;
    private UndoView undoView;
    private ImageLocation uploadingImageLocation;
    private boolean userBlocked;
    private long userId;
    private TLRPC.UserFull userInfo;
    private int userInfoRow;
    private int usernameRow;
    private boolean usersEndReached;
    private int usersForceShowingIn;
    private String vcardFirstName;
    private String vcardLastName;
    private String vcardPhone;
    private final Drawable[] verifiedCheckDrawable;
    private final CrossfadeDrawable[] verifiedCrossfadeDrawable;
    private final Drawable[] verifiedDrawable;
    private int versionRow;
    private ActionBarMenuItem videoCallItem;
    private boolean videoCallItemVisible;
    private final ArrayList visibleChatParticipants;
    private final ArrayList visibleSortedUsers;
    private boolean waitCanSendStoryRequest;
    private Paint whitePaint;
    private RLottieImageView writeButton;
    private AnimatorSet writeButtonAnimation;

    public class AnonymousClass1 extends Paint {
        AnonymousClass1(int i) {
            super(i);
        }

        @Override
        public void setAlpha(int i) {
            super.setAlpha(i);
            ProfileActivity.this.fragmentView.invalidate();
        }
    }

    class AnonymousClass10 extends ActionBarMenuItem.ActionBarMenuItemSearchListener {
        AnonymousClass10() {
        }

        @Override
        public Animator getCustomToggleTransition() {
            ProfileActivity.this.searchMode = !r0.searchMode;
            if (!ProfileActivity.this.searchMode) {
                ProfileActivity.this.searchItem.clearFocusOnSearchView();
            }
            if (ProfileActivity.this.searchMode) {
                ProfileActivity.this.searchItem.getSearchField().setText("");
            }
            ProfileActivity profileActivity = ProfileActivity.this;
            return profileActivity.searchExpandTransition(profileActivity.searchMode);
        }

        @Override
        public void onTextChanged(EditText editText) {
            ProfileActivity.this.searchAdapter.search(editText.getText().toString().toLowerCase());
        }
    }

    class AnonymousClass11 extends ClippedListView {
        private VelocityTracker velocityTracker;

        AnonymousClass11(Context context) {
            super(context);
        }

        @Override
        public boolean allowSelectChildAtPosition(View view) {
            return view != ProfileActivity.this.sharedMediaLayout;
        }

        @Override
        public boolean canHighlightChildAt(View view, float f, float f2) {
            return !(view instanceof AboutLinkCell);
        }

        @Override
        public void dispatchDraw(Canvas canvas) {
            if (ProfileActivity.this.bizHoursRow >= 0 && ProfileActivity.this.infoStartRow >= 0 && ProfileActivity.this.infoEndRow >= 0) {
                drawSectionBackground(canvas, ProfileActivity.this.infoStartRow, ProfileActivity.this.infoEndRow, getThemedColor(Theme.key_windowBackgroundWhite));
            }
            super.dispatchDraw(canvas);
        }

        @Override
        public boolean drawChild(Canvas canvas, View view, long j) {
            if (getItemAnimator().isRunning() && view.getBackground() == null && view.getTranslationY() != 0.0f) {
                boolean z = ProfileActivity.this.listView.getChildAdapterPosition(view) == ProfileActivity.this.sharedMediaRow && view.getAlpha() != 1.0f;
                if (z) {
                    ProfileActivity.this.whitePaint.setAlpha((int) (ProfileActivity.this.listView.getAlpha() * 255.0f * view.getAlpha()));
                }
                canvas.drawRect(ProfileActivity.this.listView.getX(), view.getY(), ProfileActivity.this.listView.getX() + ProfileActivity.this.listView.getMeasuredWidth(), view.getY() + view.getHeight(), ProfileActivity.this.whitePaint);
                if (z) {
                    ProfileActivity.this.whitePaint.setAlpha((int) (ProfileActivity.this.listView.getAlpha() * 255.0f));
                }
            }
            return super.drawChild(canvas, view, j);
        }

        @Override
        public boolean hasOverlappingRendering() {
            return false;
        }

        @Override
        public void invalidate() {
            super.invalidate();
            View view = ProfileActivity.this.fragmentView;
            if (view != null) {
                view.invalidate();
            }
        }

        @Override
        public boolean onInterceptTouchEvent(MotionEvent motionEvent) {
            SharedMediaLayout sharedMediaLayout = ProfileActivity.this.sharedMediaLayout;
            if (sharedMediaLayout != null) {
                if (sharedMediaLayout.canEditStories() && ProfileActivity.this.sharedMediaLayout.isActionModeShown() && ProfileActivity.this.sharedMediaLayout.getClosestTab() == 13) {
                    return false;
                }
                if (ProfileActivity.this.sharedMediaLayout.canEditStories() && ProfileActivity.this.sharedMediaLayout.isActionModeShown() && ProfileActivity.this.sharedMediaLayout.getClosestTab() == 8) {
                    return false;
                }
            }
            return super.onInterceptTouchEvent(motionEvent);
        }

        @Override
        public void onLayout(boolean z, int i, int i2, int i3, int i4) {
            super.onLayout(z, i, i2, i3, i4);
            ProfileActivity.this.updateBottomButtonY();
        }

        @Override
        public boolean onTouchEvent(MotionEvent motionEvent) {
            VelocityTracker velocityTracker;
            View findViewByPosition;
            int action = motionEvent.getAction();
            if (action == 0) {
                VelocityTracker velocityTracker2 = this.velocityTracker;
                if (velocityTracker2 == null) {
                    this.velocityTracker = VelocityTracker.obtain();
                } else {
                    velocityTracker2.clear();
                }
                this.velocityTracker.addMovement(motionEvent);
            } else if (action == 2) {
                VelocityTracker velocityTracker3 = this.velocityTracker;
                if (velocityTracker3 != null) {
                    velocityTracker3.addMovement(motionEvent);
                    this.velocityTracker.computeCurrentVelocity(1000);
                    ProfileActivity.this.listViewVelocityY = this.velocityTracker.getYVelocity(motionEvent.getPointerId(motionEvent.getActionIndex()));
                }
            } else if ((action == 1 || action == 3) && (velocityTracker = this.velocityTracker) != null) {
                velocityTracker.recycle();
                this.velocityTracker = null;
            }
            boolean onTouchEvent = super.onTouchEvent(motionEvent);
            if ((action == 1 || action == 3) && ProfileActivity.this.allowPullingDown && (findViewByPosition = ProfileActivity.this.layoutManager.findViewByPosition(0)) != null) {
                if (ProfileActivity.this.isPulledDown) {
                    ProfileActivity.this.listView.smoothScrollBy(0, (findViewByPosition.getTop() - ProfileActivity.this.listView.getMeasuredWidth()) + ActionBar.getCurrentActionBarHeight() + (((BaseFragment) ProfileActivity.this).actionBar.getOccupyStatusBar() ? AndroidUtilities.statusBarHeight : 0), CubicBezierInterpolator.EASE_OUT_QUINT);
                } else {
                    ProfileActivity.this.listView.smoothScrollBy(0, findViewByPosition.getTop() - AndroidUtilities.dp(88.0f), CubicBezierInterpolator.EASE_OUT_QUINT);
                }
            }
            return onTouchEvent;
        }

        @Override
        public void requestChildOnScreen(View view, View view2) {
        }
    }

    public class AnonymousClass12 extends DefaultItemAnimator {
        int animationIndex = -1;

        AnonymousClass12() {
        }

        public void lambda$onAllAnimationsDone$0() {
            ProfileActivity.this.getNotificationCenter().onAnimationFinish(this.animationIndex);
        }

        public void lambda$runPendingAnimations$1(ValueAnimator valueAnimator) {
            ProfileActivity.this.listView.invalidate();
        }

        @Override
        protected long getAddAnimationDelay(long j, long j2, long j3) {
            return 0L;
        }

        @Override
        public void onAllAnimationsDone() {
            super.onAllAnimationsDone();
            AndroidUtilities.runOnUIThread(new Runnable() {
                @Override
                public final void run() {
                    ProfileActivity.AnonymousClass12.this.lambda$onAllAnimationsDone$0();
                }
            });
        }

        @Override
        public void onMoveAnimationUpdate(RecyclerView.ViewHolder viewHolder) {
            super.onMoveAnimationUpdate(viewHolder);
            ProfileActivity.this.updateBottomButtonY();
        }

        @Override
        public void runPendingAnimations() {
            boolean z = !this.mPendingRemovals.isEmpty();
            boolean z2 = !this.mPendingMoves.isEmpty();
            boolean z3 = !this.mPendingChanges.isEmpty();
            boolean z4 = !this.mPendingAdditions.isEmpty();
            if (z || z2 || z4 || z3) {
                ValueAnimator ofFloat = ValueAnimator.ofFloat(0.0f, 1.0f);
                ofFloat.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                        ProfileActivity.AnonymousClass12.this.lambda$runPendingAnimations$1(valueAnimator);
                    }
                });
                ofFloat.setDuration(getMoveDuration());
                ofFloat.start();
                this.animationIndex = ProfileActivity.this.getNotificationCenter().setAnimationInProgress(this.animationIndex, null);
            }
            super.runPendingAnimations();
        }
    }

    class AnonymousClass13 extends LinearLayoutManager {
        AnonymousClass13(Context context) {
            super(context);
        }

        @Override
        public int scrollVerticallyBy(int i, RecyclerView.Recycler recycler, RecyclerView.State state) {
            ProfileActivity profileActivity;
            boolean z = false;
            View findViewByPosition = ProfileActivity.this.layoutManager.findViewByPosition(0);
            if (findViewByPosition != null && !ProfileActivity.this.openingAvatar) {
                int top = findViewByPosition.getTop() - AndroidUtilities.dp(88.0f);
                if (!ProfileActivity.this.allowPullingDown && top > i) {
                    if (ProfileActivity.this.avatarsViewPager.hasImages() && ProfileActivity.this.avatarImage.getImageReceiver().hasNotThumb() && !AndroidUtilities.isAccessibilityScreenReaderEnabled() && !ProfileActivity.this.isInLandscapeMode && !AndroidUtilities.isTablet()) {
                        profileActivity = ProfileActivity.this;
                        if (profileActivity.avatarBig == null) {
                            z = true;
                        }
                    }
                    i = top;
                } else if (ProfileActivity.this.allowPullingDown) {
                    if (i >= top) {
                        profileActivity = ProfileActivity.this;
                    } else if (ProfileActivity.this.listView.getScrollState() == 1 && !ProfileActivity.this.isPulledDown) {
                        i /= 2;
                    }
                }
                profileActivity.allowPullingDown = z;
                i = top;
            }
            return super.scrollVerticallyBy(i, recycler, state);
        }

        @Override
        public boolean supportsPredictiveItemAnimations() {
            return ProfileActivity.this.imageUpdater != null;
        }
    }

    public class AnonymousClass14 implements DialogInterface.OnClickListener {
        final CheckBoxCell[] val$cells;

        AnonymousClass14(CheckBoxCell[] checkBoxCellArr) {
            this.val$cells = checkBoxCellArr;
        }

        public static void lambda$onClick$0(TLObject tLObject, TLRPC.TL_error tL_error) {
        }

        @Override
        public void onClick(DialogInterface dialogInterface, int i) {
            TLRPC.TL_messages_reportReaction tL_messages_reportReaction = new TLRPC.TL_messages_reportReaction();
            tL_messages_reportReaction.user_id = ProfileActivity.this.getMessagesController().getInputUser(ProfileActivity.this.userId);
            tL_messages_reportReaction.peer = ProfileActivity.this.getMessagesController().getInputPeer(ProfileActivity.this.reportReactionFromDialogId);
            tL_messages_reportReaction.id = ProfileActivity.this.reportReactionMessageId;
            ConnectionsManager.getInstance(((BaseFragment) ProfileActivity.this).currentAccount).sendRequest(tL_messages_reportReaction, new RequestDelegate() {
                @Override
                public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                    ProfileActivity.AnonymousClass14.lambda$onClick$0(tLObject, tL_error);
                }
            });
            CheckBoxCell checkBoxCell = this.val$cells[0];
            if (checkBoxCell != null && checkBoxCell.isChecked()) {
                ProfileActivity.this.getMessagesController().deleteParticipantFromChat(-ProfileActivity.this.reportReactionFromDialogId, ProfileActivity.this.getMessagesController().getUser(Long.valueOf(ProfileActivity.this.userId)));
            }
            ProfileActivity.this.reportReactionMessageId = 0;
            ProfileActivity.this.updateListAnimated(false);
            BulletinFactory.of(ProfileActivity.this).createReportSent(ProfileActivity.this.resourcesProvider).show();
        }
    }

    public class AnonymousClass15 implements DialogInterface.OnClickListener {
        AnonymousClass15() {
        }

        @Override
        public void onClick(DialogInterface dialogInterface, int i) {
            dialogInterface.dismiss();
        }
    }

    public class AnonymousClass16 implements ChatNotificationsPopupWrapper.Callback {
        final long val$did;

        AnonymousClass16(long j) {
            r2 = j;
        }

        @Override
        public void dismiss() {
            ChatNotificationsPopupWrapper.Callback.CC.$default$dismiss(this);
        }

        @Override
        public void muteFor(int i) {
            ProfileActivity profileActivity = ProfileActivity.this;
            if (i == 0) {
                if (profileActivity.getMessagesController().isDialogMuted(r2, ProfileActivity.this.topicId)) {
                    toggleMute();
                }
                if (BulletinFactory.canShowBulletin(ProfileActivity.this)) {
                    ProfileActivity profileActivity2 = ProfileActivity.this;
                    BulletinFactory.createMuteBulletin(profileActivity2, 4, i, profileActivity2.getResourceProvider()).show();
                    return;
                }
                return;
            }
            profileActivity.getNotificationsController().muteUntil(r2, ProfileActivity.this.topicId, i);
            if (BulletinFactory.canShowBulletin(ProfileActivity.this)) {
                ProfileActivity profileActivity3 = ProfileActivity.this;
                BulletinFactory.createMuteBulletin(profileActivity3, 5, i, profileActivity3.getResourceProvider()).show();
            }
            ProfileActivity.this.updateExceptions();
            if (ProfileActivity.this.notificationsRow < 0 || ProfileActivity.this.listAdapter == null) {
                return;
            }
            ProfileActivity.this.listAdapter.notifyItemChanged(ProfileActivity.this.notificationsRow);
        }

        @Override
        public void openExceptions() {
            Bundle bundle = new Bundle();
            bundle.putLong("dialog_id", r2);
            TopicsNotifySettingsFragments topicsNotifySettingsFragments = new TopicsNotifySettingsFragments(bundle);
            topicsNotifySettingsFragments.setExceptions(ProfileActivity.this.notificationsExceptionTopics);
            ProfileActivity.this.presentFragment(topicsNotifySettingsFragments);
        }

        @Override
        public void showCustomize() {
            if (r2 != 0) {
                Bundle bundle = new Bundle();
                bundle.putLong("dialog_id", r2);
                bundle.putLong("topic_id", ProfileActivity.this.topicId);
                ProfileActivity profileActivity = ProfileActivity.this;
                profileActivity.presentFragment(new ProfileNotificationsActivity(bundle, profileActivity.resourcesProvider));
            }
        }

        @Override
        public void toggleMute() {
            boolean z = !ProfileActivity.this.getMessagesController().isDialogMuted(r2, ProfileActivity.this.topicId);
            ProfileActivity.this.getNotificationsController().muteDialog(r2, ProfileActivity.this.topicId, z);
            ProfileActivity profileActivity = ProfileActivity.this;
            if (profileActivity.fragmentView != null) {
                BulletinFactory.createMuteBulletin(profileActivity, z, null).show();
            }
            ProfileActivity.this.updateExceptions();
            if (ProfileActivity.this.notificationsRow < 0 || ProfileActivity.this.listAdapter == null) {
                return;
            }
            ProfileActivity.this.listAdapter.notifyItemChanged(ProfileActivity.this.notificationsRow);
        }

        @Override
        public void toggleSound() {
            SharedPreferences notificationsSettings = MessagesController.getNotificationsSettings(((BaseFragment) ProfileActivity.this).currentAccount);
            boolean z = notificationsSettings.getBoolean("sound_enabled_" + NotificationsController.getSharedPrefKey(r2, ProfileActivity.this.topicId), true);
            boolean z2 = !z;
            notificationsSettings.edit().putBoolean("sound_enabled_" + NotificationsController.getSharedPrefKey(r2, ProfileActivity.this.topicId), z2).apply();
            if (BulletinFactory.canShowBulletin(ProfileActivity.this)) {
                ProfileActivity profileActivity = ProfileActivity.this;
                BulletinFactory.createSoundEnabledBulletin(profileActivity, z ? 1 : 0, profileActivity.getResourceProvider()).show();
            }
        }
    }

    public class AnonymousClass17 implements RecyclerListView.OnItemLongClickListener {
        private int pressCount = 0;
        final Context val$context;

        public class AnonymousClass1 extends ShareAlert {
            AnonymousClass1(Context context, ArrayList arrayList, String str, boolean z, String str2, boolean z2) {
                super(context, arrayList, str, z, str2, z2);
            }

            public void lambda$onSend$0(LongSparseArray longSparseArray, int i) {
                BulletinFactory.createInviteSentBulletin(ProfileActivity.this.getParentActivity(), ProfileActivity.this.contentView, longSparseArray.size(), longSparseArray.size() == 1 ? ((TLRPC.Dialog) longSparseArray.valueAt(0)).id : 0L, i, getThemedColor(Theme.key_undo_background), getThemedColor(Theme.key_undo_infoColor)).show();
            }

            @Override
            public void onSend(final LongSparseArray longSparseArray, final int i, TLRPC.TL_forumTopic tL_forumTopic) {
                AndroidUtilities.runOnUIThread(new Runnable() {
                    @Override
                    public final void run() {
                        ProfileActivity.AnonymousClass17.AnonymousClass1.this.lambda$onSend$0(longSparseArray, i);
                    }
                }, 250L);
            }
        }

        AnonymousClass17(Context context) {
            this.val$context = context;
        }

        public static void lambda$onItemClick$0(Thread thread, Throwable th) {
            if (thread == Looper.getMainLooper().getThread()) {
                FileLog.fatal(th, true);
            }
        }

        public void lambda$onItemClick$1(TLObject tLObject, TLRPC.TL_error tL_error) {
            ProfileActivity.this.getMessagesController().loadAppConfig();
        }

        public void lambda$onItemClick$2(TLObject tLObject, TLRPC.TL_error tL_error) {
            TLRPC.TL_help_dismissSuggestion tL_help_dismissSuggestion = new TLRPC.TL_help_dismissSuggestion();
            tL_help_dismissSuggestion.suggestion = "VALIDATE_PASSWORD";
            tL_help_dismissSuggestion.peer = new TLRPC.TL_inputPeerEmpty();
            ProfileActivity.this.getConnectionsManager().sendRequest(tL_help_dismissSuggestion, new RequestDelegate() {
                @Override
                public final void run(TLObject tLObject2, TLRPC.TL_error tL_error2) {
                    ProfileActivity.AnonymousClass17.this.lambda$onItemClick$1(tLObject2, tL_error2);
                }
            });
        }

        public static void lambda$onItemClick$3(int i, DialogInterface dialogInterface, int i2) {
            int i3 = 2 - i2;
            if (i3 == i) {
                SharedConfig.overrideDevicePerformanceClass(-1);
            } else {
                SharedConfig.overrideDevicePerformanceClass(i3);
            }
        }

        public void lambda$onItemClick$4(Context context, DialogInterface dialogInterface, int i) {
            int i2;
            String str;
            String str2;
            int i3;
            int i4;
            int i5 = 0;
            if (i == 0) {
                ProfileActivity.this.getUserConfig().syncContacts = true;
                ProfileActivity.this.getUserConfig().saveConfig(false);
                ProfileActivity.this.getContactsController().forceImportContacts();
                return;
            }
            if (i == 1) {
                ProfileActivity.this.getContactsController().loadContacts(false, 0L);
                return;
            }
            if (i == 2) {
                ProfileActivity.this.getContactsController().resetImportedContacts();
                return;
            }
            if (i == 3) {
                ProfileActivity.this.getMessagesController().forceResetDialogs();
                return;
            }
            if (i == 4) {
                boolean z = !BuildVars.LOGS_ENABLED;
                BuildVars.LOGS_ENABLED = z;
                Thread.setDefaultUncaughtExceptionHandler(z ? new Thread.UncaughtExceptionHandler() {
                    @Override
                    public final void uncaughtException(Thread thread, Throwable th) {
                        ProfileActivity.AnonymousClass17.lambda$onItemClick$0(thread, th);
                    }
                } : null);
                ApplicationLoader.applicationContext.getSharedPreferences("systemConfig", 0).edit().putBoolean("logsEnabled", BuildVars.LOGS_ENABLED).commit();
                ProfileActivity.this.updateRowsIds();
                ProfileActivity.this.listAdapter.notifyDataSetChanged();
                if (BuildVars.LOGS_ENABLED) {
                    FileLog.d("app start time = " + ApplicationLoader.startTime);
                    try {
                        FileLog.d("buildVersion = " + ApplicationLoader.applicationContext.getPackageManager().getPackageInfo(ApplicationLoader.applicationContext.getPackageName(), 0).versionCode);
                        return;
                    } catch (Exception e) {
                        FileLog.e(e);
                        return;
                    }
                }
                return;
            }
            if (i == 5) {
                SharedConfig.toggleInappCamera();
                return;
            }
            if (i == 6) {
                ProfileActivity.this.getMessagesStorage().clearSentMedia();
                SharedConfig.setNoSoundHintShowed(false);
                MessagesController.getGlobalMainSettings().edit().remove("archivehint").remove("proximityhint").remove("archivehint_l").remove("speedhint").remove("gifhint").remove("reminderhint").remove("soundHint").remove("themehint").remove("bganimationhint").remove("filterhint").remove("n_0").remove("storyprvhint").remove("storyhint").remove("storyhint2").remove("storydualhint").remove("storysvddualhint").remove("stories_camera").remove("dualcam").remove("dualmatrix").remove("dual_available").remove("archivehint").remove("askNotificationsAfter").remove("askNotificationsDuration").remove("viewoncehint").remove("taptostorysoundhint").remove("nothanos").remove("voiceoncehint").remove("savedhint").remove("savedsearchhint").remove("savedsearchtaghint").remove("groupEmojiPackHintShown").remove("newppsms").remove("monetizationadshint").remove("seekSpeedHintShowed").remove("unsupport_video/av01").apply();
                MessagesController.getEmojiSettings(((BaseFragment) ProfileActivity.this).currentAccount).edit().remove("featured_hidden").remove("emoji_featured_hidden").commit();
                SharedConfig.textSelectionHintShows = 0;
                SharedConfig.lockRecordAudioVideoHint = 0;
                SharedConfig.stickersReorderingHintUsed = false;
                SharedConfig.forwardingOptionsHintShown = false;
                SharedConfig.replyingOptionsHintShown = false;
                SharedConfig.messageSeenHintCount = 3;
                SharedConfig.emojiInteractionsHintCount = 3;
                SharedConfig.dayNightThemeSwitchHintCount = 3;
                SharedConfig.fastScrollHintCount = 3;
                SharedConfig.stealthModeSendMessageConfirm = 2;
                SharedConfig.updateStealthModeSendMessageConfirm(2);
                SharedConfig.setStoriesReactionsLongPressHintUsed(false);
                SharedConfig.setStoriesIntroShown(false);
                SharedConfig.setMultipleReactionsPromoShowed(false);
                ChatThemeController.getInstance(((BaseFragment) ProfileActivity.this).currentAccount).clearCache();
                ProfileActivity.this.getNotificationCenter().lambda$postNotificationNameOnUIThread$1(NotificationCenter.newSuggestionsAvailable, new Object[0]);
                RestrictedLanguagesSelectActivity.cleanup();
                PersistColorPalette.getInstance(((BaseFragment) ProfileActivity.this).currentAccount).cleanup();
                SharedPreferences mainSettings = ProfileActivity.this.getMessagesController().getMainSettings();
                SharedPreferences.Editor edit = mainSettings.edit();
                edit.remove("peerColors").remove("profilePeerColors").remove("boostingappearance").remove("bizbothint").remove("movecaptionhint");
                for (String str3 : mainSettings.getAll().keySet()) {
                    if (str3.contains("show_gift_for_") || str3.contains("bdayhint_") || str3.contains("bdayanim_")) {
                        edit.remove(str3);
                    }
                }
                edit.apply();
                SharedPreferences.Editor edit2 = MessagesController.getNotificationsSettings(((BaseFragment) ProfileActivity.this).currentAccount).edit();
                for (String str4 : MessagesController.getNotificationsSettings(((BaseFragment) ProfileActivity.this).currentAccount).getAll().keySet()) {
                    if (str4.startsWith("dialog_bar_botver")) {
                        edit2.remove(str4);
                    }
                }
                edit2.apply();
                return;
            }
            if (i == 7) {
                VoIPHelper.showCallDebugSettings(ProfileActivity.this.getParentActivity());
                return;
            }
            if (i == 8) {
                SharedConfig.toggleRoundCamera16to9();
                return;
            }
            if (i == 9) {
                ((LaunchActivity) ProfileActivity.this.getParentActivity()).checkAppUpdate(true, null);
                return;
            }
            if (i == 10) {
                ProfileActivity.this.getMessagesStorage().readAllDialogs(-1);
                return;
            }
            if (i == 11) {
                SharedConfig.toggleDisableVoiceAudioEffects();
                return;
            }
            if (i == 12) {
                SharedConfig.pendingAppUpdate = null;
                SharedConfig.saveConfig();
                NotificationCenter.getGlobalInstance().lambda$postNotificationNameOnUIThread$1(NotificationCenter.appUpdateAvailable, new Object[0]);
                return;
            }
            if (i == 13) {
                Set<String> set = ProfileActivity.this.getMessagesController().pendingSuggestions;
                set.add("VALIDATE_PHONE_NUMBER");
                set.add("VALIDATE_PASSWORD");
                ProfileActivity.this.getNotificationCenter().lambda$postNotificationNameOnUIThread$1(NotificationCenter.newSuggestionsAvailable, new Object[0]);
                return;
            }
            try {
                if (i == 14) {
                    ApplicationLoader.applicationContext.deleteDatabase("webview.db");
                    ApplicationLoader.applicationContext.deleteDatabase("webviewCache.db");
                    WebStorage.getInstance().deleteAllData();
                    WebView webView = new WebView(ApplicationLoader.applicationContext);
                    webView.clearHistory();
                    webView.destroy();
                    return;
                }
                if (i == 15) {
                    CookieManager cookieManager = CookieManager.getInstance();
                    if (Build.VERSION.SDK_INT >= 21) {
                        cookieManager.removeAllCookies(null);
                        cookieManager.flush();
                        return;
                    }
                    return;
                }
                if (i == 16) {
                    SharedConfig.toggleDebugWebView();
                    Toast.makeText(ProfileActivity.this.getParentActivity(), LocaleController.getString(SharedConfig.debugWebView ? R.string.DebugMenuWebViewDebugEnabled : R.string.DebugMenuWebViewDebugDisabled), 0).show();
                    return;
                }
                if (i == 17) {
                    SharedConfig.toggleForceDisableTabletMode();
                    Activity findActivity = AndroidUtilities.findActivity(context);
                    Intent launchIntentForPackage = findActivity.getPackageManager().getLaunchIntentForPackage(findActivity.getPackageName());
                    findActivity.finishAffinity();
                    findActivity.startActivity(launchIntentForPackage);
                    System.exit(0);
                    return;
                }
                if (i == 18) {
                    FloatingDebugController.setActive((LaunchActivity) ProfileActivity.this.getParentActivity(), !FloatingDebugController.isActive());
                    return;
                }
                if (i == 19) {
                    ProfileActivity.this.getMessagesController().loadAppConfig();
                    TLRPC.TL_help_dismissSuggestion tL_help_dismissSuggestion = new TLRPC.TL_help_dismissSuggestion();
                    tL_help_dismissSuggestion.suggestion = "VALIDATE_PHONE_NUMBER";
                    tL_help_dismissSuggestion.peer = new TLRPC.TL_inputPeerEmpty();
                    ProfileActivity.this.getConnectionsManager().sendRequest(tL_help_dismissSuggestion, new RequestDelegate() {
                        @Override
                        public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                            ProfileActivity.AnonymousClass17.this.lambda$onItemClick$2(tLObject, tL_error);
                        }
                    });
                    return;
                }
                if (i != 20) {
                    if (i == 21) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(ProfileActivity.this.getParentActivity(), ProfileActivity.this.resourcesProvider);
                        builder.setTitle("Force performance class");
                        int devicePerformanceClass = SharedConfig.getDevicePerformanceClass();
                        final int measureDevicePerformanceClass = SharedConfig.measureDevicePerformanceClass();
                        StringBuilder sb = new StringBuilder();
                        sb.append(devicePerformanceClass == 2 ? "**HIGH**" : "HIGH");
                        sb.append(measureDevicePerformanceClass == 2 ? " (measured)" : "");
                        SpannableStringBuilder replaceTags = AndroidUtilities.replaceTags(sb.toString());
                        StringBuilder sb2 = new StringBuilder();
                        sb2.append(devicePerformanceClass == 1 ? "**AVERAGE**" : "AVERAGE");
                        sb2.append(measureDevicePerformanceClass == 1 ? " (measured)" : "");
                        SpannableStringBuilder replaceTags2 = AndroidUtilities.replaceTags(sb2.toString());
                        StringBuilder sb3 = new StringBuilder();
                        sb3.append(devicePerformanceClass == 0 ? "**LOW**" : "LOW");
                        sb3.append(measureDevicePerformanceClass == 0 ? " (measured)" : "");
                        builder.setItems(new CharSequence[]{replaceTags, replaceTags2, AndroidUtilities.replaceTags(sb3.toString())}, new DialogInterface.OnClickListener() {
                            @Override
                            public final void onClick(DialogInterface dialogInterface2, int i6) {
                                ProfileActivity.AnonymousClass17.lambda$onItemClick$3(measureDevicePerformanceClass, dialogInterface2, i6);
                            }
                        });
                        builder.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), null);
                        builder.show();
                        return;
                    }
                    if (i == 22) {
                        SharedConfig.toggleRoundCamera();
                        return;
                    }
                    if (i == 23) {
                        boolean dualAvailableStatic = DualCameraView.dualAvailableStatic(ProfileActivity.this.getContext());
                        MessagesController.getGlobalMainSettings().edit().putBoolean("dual_available", true ^ dualAvailableStatic).apply();
                        Toast.makeText(ProfileActivity.this.getParentActivity(), LocaleController.getString(!dualAvailableStatic ? R.string.DebugMenuDualOnToast : R.string.DebugMenuDualOffToast), 0).show();
                        return;
                    }
                    if (i == 24) {
                        SharedConfig.toggleSurfaceInStories();
                        while (i5 < ProfileActivity.this.getParentLayout().getFragmentStack().size()) {
                            ((BaseFragment) ProfileActivity.this.getParentLayout().getFragmentStack().get(i5)).clearSheets();
                            i5++;
                        }
                        return;
                    }
                    if (i == 25) {
                        SharedConfig.togglePhotoViewerBlur();
                        return;
                    }
                    if (i == 26) {
                        SharedConfig.togglePaymentByInvoice();
                        return;
                    }
                    if (i == 27) {
                        ProfileActivity.this.getMediaDataController().loadAttachMenuBots(false, true);
                        return;
                    }
                    if (i == 28) {
                        SharedConfig.toggleUseCamera2(((BaseFragment) ProfileActivity.this).currentAccount);
                        return;
                    }
                    if (i == 29) {
                        BotBiometry.clear();
                        BotLocation.clear();
                        BotDownloads.clear();
                        SetupEmojiStatusSheet.clear();
                        return;
                    }
                    if (i == 30) {
                        AuthTokensHelper.clearLogInTokens();
                        return;
                    }
                    if (i == 31) {
                        SharedConfig.toggleUseNewBlur();
                        return;
                    } else if (i == 32) {
                        SharedConfig.toggleBrowserAdaptableColors();
                        return;
                    } else {
                        if (i == 33) {
                            SharedConfig.toggleDebugVideoQualities();
                            return;
                        }
                        return;
                    }
                }
                int i6 = ConnectionsManager.CPU_COUNT;
                int memoryClass = ((ActivityManager) ApplicationLoader.applicationContext.getSystemService("activity")).getMemoryClass();
                StringBuilder sb4 = new StringBuilder();
                long j = 0;
                long j2 = 0;
                long j3 = 0;
                long j4 = 0;
                long j5 = 0;
                long j6 = 0;
                long j7 = 0;
                long j8 = 0;
                while (i5 < i6) {
                    Long sysInfoLong = AndroidUtilities.getSysInfoLong("/sys/devices/system/cpu/cpu" + i5 + "/cpufreq/cpuinfo_min_freq");
                    Long sysInfoLong2 = AndroidUtilities.getSysInfoLong("/sys/devices/system/cpu/cpu" + i5 + "/cpufreq/cpuinfo_cur_freq");
                    Long sysInfoLong3 = AndroidUtilities.getSysInfoLong("/sys/devices/system/cpu/cpu" + i5 + "/cpufreq/cpuinfo_max_freq");
                    Long sysInfoLong4 = AndroidUtilities.getSysInfoLong("/sys/devices/system/cpu/cpu" + i5 + "/cpu_capacity");
                    sb4.append("#");
                    sb4.append(i5);
                    sb4.append(" ");
                    if (sysInfoLong != null) {
                        sb4.append("min=");
                        i3 = i6;
                        i4 = memoryClass;
                        sb4.append(sysInfoLong.longValue() / 1000);
                        sb4.append(" ");
                        j2 += sysInfoLong.longValue() / 1000;
                        j++;
                    } else {
                        i3 = i6;
                        i4 = memoryClass;
                    }
                    if (sysInfoLong2 != null) {
                        sb4.append("cur=");
                        sb4.append(sysInfoLong2.longValue() / 1000);
                        sb4.append(" ");
                        j4 += sysInfoLong2.longValue() / 1000;
                        j3++;
                    }
                    if (sysInfoLong3 != null) {
                        sb4.append("max=");
                        sb4.append(sysInfoLong3.longValue() / 1000);
                        sb4.append(" ");
                        j6 += sysInfoLong3.longValue() / 1000;
                        j5++;
                    }
                    if (sysInfoLong4 != null) {
                        sb4.append("cpc=");
                        sb4.append(sysInfoLong4);
                        sb4.append(" ");
                        j8 += sysInfoLong4.longValue();
                        j7++;
                    }
                    sb4.append("\n");
                    i5++;
                    i6 = i3;
                    memoryClass = i4;
                }
                int i7 = i6;
                int i8 = memoryClass;
                StringBuilder sb5 = new StringBuilder();
                sb5.append(Build.MANUFACTURER);
                sb5.append(", ");
                sb5.append(Build.MODEL);
                sb5.append(" (");
                sb5.append(Build.PRODUCT);
                sb5.append(", ");
                sb5.append(Build.DEVICE);
                sb5.append(") ");
                sb5.append(" (android ");
                int i9 = Build.VERSION.SDK_INT;
                sb5.append(i9);
                sb5.append(")\n");
                if (i9 >= 31) {
                    sb5.append("SoC: ");
                    str = Build.SOC_MANUFACTURER;
                    sb5.append(str);
                    sb5.append(", ");
                    str2 = Build.SOC_MODEL;
                    sb5.append(str2);
                    sb5.append("\n");
                }
                String sysInfoString = AndroidUtilities.getSysInfoString("/sys/kernel/gpu/gpu_model");
                if (sysInfoString != null) {
                    sb5.append("GPU: ");
                    sb5.append(sysInfoString);
                    Long sysInfoLong5 = AndroidUtilities.getSysInfoLong("/sys/kernel/gpu/gpu_min_clock");
                    Long sysInfoLong6 = AndroidUtilities.getSysInfoLong("/sys/kernel/gpu/gpu_mm_min_clock");
                    Long sysInfoLong7 = AndroidUtilities.getSysInfoLong("/sys/kernel/gpu/gpu_max_clock");
                    if (sysInfoLong5 != null) {
                        sb5.append(", min=");
                        sb5.append(sysInfoLong5.longValue() / 1000);
                    }
                    if (sysInfoLong6 != null) {
                        sb5.append(", mmin=");
                        sb5.append(sysInfoLong6.longValue() / 1000);
                    }
                    if (sysInfoLong7 != null) {
                        sb5.append(", max=");
                        sb5.append(sysInfoLong7.longValue() / 1000);
                    }
                    sb5.append("\n");
                }
                ConfigurationInfo deviceConfigurationInfo = ((ActivityManager) ApplicationLoader.applicationContext.getSystemService("activity")).getDeviceConfigurationInfo();
                sb5.append("GLES Version: ");
                sb5.append(deviceConfigurationInfo.getGlEsVersion());
                sb5.append("\n");
                sb5.append("Memory: class=");
                sb5.append(AndroidUtilities.formatFileSize(i8 * 1048576));
                ActivityManager.MemoryInfo memoryInfo = new ActivityManager.MemoryInfo();
                ((ActivityManager) ApplicationLoader.applicationContext.getSystemService("activity")).getMemoryInfo(memoryInfo);
                sb5.append(", total=");
                sb5.append(AndroidUtilities.formatFileSize(memoryInfo.totalMem));
                sb5.append(", avail=");
                sb5.append(AndroidUtilities.formatFileSize(memoryInfo.availMem));
                sb5.append(", low?=");
                sb5.append(memoryInfo.lowMemory);
                sb5.append(" (threshold=");
                sb5.append(AndroidUtilities.formatFileSize(memoryInfo.threshold));
                sb5.append(")");
                sb5.append("\n");
                sb5.append("Current class: ");
                sb5.append(SharedConfig.performanceClassName(SharedConfig.getDevicePerformanceClass()));
                sb5.append(", measured: ");
                sb5.append(SharedConfig.performanceClassName(SharedConfig.measureDevicePerformanceClass()));
                if (i9 >= 31) {
                    sb5.append(", suggest=");
                    i2 = Build.VERSION.MEDIA_PERFORMANCE_CLASS;
                    sb5.append(i2);
                }
                sb5.append("\n");
                sb5.append(i7);
                sb5.append(" CPUs");
                if (j > 0) {
                    sb5.append(", avgMinFreq=");
                    sb5.append(j2 / j);
                }
                if (j3 > 0) {
                    sb5.append(", avgCurFreq=");
                    sb5.append(j4 / j3);
                }
                if (j5 > 0) {
                    sb5.append(", avgMaxFreq=");
                    sb5.append(j6 / j5);
                }
                if (j7 > 0) {
                    sb5.append(", avgCapacity=");
                    sb5.append(j8 / j7);
                }
                sb5.append("\n");
                sb5.append((CharSequence) sb4);
                ProfileActivity.this.listCodecs("video/avc", sb5);
                ProfileActivity.this.listCodecs("video/hevc", sb5);
                ProfileActivity.this.listCodecs("video/x-vnd.on2.vp8", sb5);
                ProfileActivity.this.listCodecs("video/x-vnd.on2.vp9", sb5);
                ProfileActivity.this.showDialog(new AnonymousClass1(ProfileActivity.this.getParentActivity(), null, sb5.toString(), false, null, false));
            } catch (Exception unused) {
            }
        }

        @Override
        public boolean onItemClick(View view, int i) {
            ArrayList arrayList;
            int i2;
            int i3;
            String str;
            String string;
            int i4;
            String str2;
            String str3;
            if (i != ProfileActivity.this.versionRow) {
                if (i >= ProfileActivity.this.membersStartRow && i < ProfileActivity.this.membersEndRow) {
                    if (ProfileActivity.this.sortedUsers.isEmpty()) {
                        arrayList = ProfileActivity.this.visibleChatParticipants;
                        i2 = i - ProfileActivity.this.membersStartRow;
                    } else {
                        arrayList = ProfileActivity.this.visibleChatParticipants;
                        i2 = ((Integer) ProfileActivity.this.sortedUsers.get(i - ProfileActivity.this.membersStartRow)).intValue();
                    }
                    return ProfileActivity.this.onMemberClick((TLRPC.ChatParticipant) arrayList.get(i2), true, view);
                }
                ProfileActivity profileActivity = ProfileActivity.this;
                if (i != profileActivity.birthdayRow) {
                    if (profileActivity.editRow(view, i)) {
                        return true;
                    }
                    return ProfileActivity.this.processOnClickOrPress(i, view, view.getWidth() / 2.0f, (int) (view.getHeight() * 0.75f));
                }
                if (profileActivity.editRow(view, i)) {
                    return true;
                }
                if (ProfileActivity.this.userInfo == null) {
                    return false;
                }
                try {
                    AndroidUtilities.addToClipboard(UserInfoActivity.birthdayString(ProfileActivity.this.userInfo.birthday));
                    BulletinFactory.of(ProfileActivity.this).createCopyBulletin(LocaleController.getString(R.string.BirthdayCopied)).show();
                    return true;
                } catch (Exception e) {
                    FileLog.e(e);
                    return true;
                }
            }
            int i5 = this.pressCount + 1;
            this.pressCount = i5;
            if (i5 < 2 && !BuildVars.DEBUG_PRIVATE_VERSION) {
                try {
                    Toast.makeText(ProfileActivity.this.getParentActivity(), LocaleController.getString("DebugMenuLongPress", R.string.DebugMenuLongPress), 0).show();
                } catch (Exception e2) {
                    FileLog.e(e2);
                }
                return true;
            }
            AlertDialog.Builder builder = new AlertDialog.Builder(ProfileActivity.this.getParentActivity(), ProfileActivity.this.resourcesProvider);
            builder.setTitle(LocaleController.getString("DebugMenu", R.string.DebugMenu));
            String string2 = LocaleController.getString("DebugMenuImportContacts", R.string.DebugMenuImportContacts);
            String string3 = LocaleController.getString("DebugMenuReloadContacts", R.string.DebugMenuReloadContacts);
            String string4 = LocaleController.getString("DebugMenuResetContacts", R.string.DebugMenuResetContacts);
            String string5 = LocaleController.getString("DebugMenuResetDialogs", R.string.DebugMenuResetDialogs);
            if (BuildVars.DEBUG_VERSION) {
                string = null;
            } else {
                if (BuildVars.LOGS_ENABLED) {
                    i3 = R.string.DebugMenuDisableLogs;
                    str = "DebugMenuDisableLogs";
                } else {
                    i3 = R.string.DebugMenuEnableLogs;
                    str = "DebugMenuEnableLogs";
                }
                string = LocaleController.getString(str, i3);
            }
            if (SharedConfig.inappCamera) {
                i4 = R.string.DebugMenuDisableCamera;
                str2 = "DebugMenuDisableCamera";
            } else {
                i4 = R.string.DebugMenuEnableCamera;
                str2 = "DebugMenuEnableCamera";
            }
            String string6 = LocaleController.getString(str2, i4);
            String string7 = LocaleController.getString("DebugMenuClearMediaCache", R.string.DebugMenuClearMediaCache);
            String string8 = LocaleController.getString("DebugMenuCallSettings", R.string.DebugMenuCallSettings);
            String string9 = (BuildVars.DEBUG_PRIVATE_VERSION || ApplicationLoader.isStandaloneBuild()) ? LocaleController.getString("DebugMenuCheckAppUpdate", R.string.DebugMenuCheckAppUpdate) : null;
            String string10 = LocaleController.getString("DebugMenuReadAllDialogs", R.string.DebugMenuReadAllDialogs);
            String str4 = BuildVars.DEBUG_PRIVATE_VERSION ? SharedConfig.disableVoiceAudioEffects ? "Enable voip audio effects" : "Disable voip audio effects" : null;
            boolean z = BuildVars.DEBUG_PRIVATE_VERSION;
            String str5 = z ? "Clean app update" : null;
            String str6 = z ? "Reset suggestions" : null;
            String string11 = z ? LocaleController.getString(R.string.DebugMenuClearWebViewCache) : null;
            String string12 = LocaleController.getString(R.string.DebugMenuClearWebViewCookies);
            int i6 = Build.VERSION.SDK_INT;
            String string13 = LocaleController.getString(SharedConfig.debugWebView ? R.string.DebugMenuDisableWebViewDebug : R.string.DebugMenuEnableWebViewDebug);
            String str7 = (AndroidUtilities.isTabletInternal() && BuildVars.DEBUG_PRIVATE_VERSION) ? SharedConfig.forceDisableTabletMode ? "Enable tablet mode" : "Disable tablet mode" : null;
            if (BuildVars.DEBUG_PRIVATE_VERSION) {
                str3 = LocaleController.getString(SharedConfig.isFloatingDebugActive ? R.string.FloatingDebugDisable : R.string.FloatingDebugEnable);
            } else {
                str3 = null;
            }
            boolean z2 = BuildVars.DEBUG_PRIVATE_VERSION;
            CharSequence[] charSequenceArr = {string2, string3, string4, string5, string, string6, string7, string8, null, string9, string10, str4, str5, str6, string11, string12, string13, str7, str3, z2 ? "Force remove premium suggestions" : null, z2 ? "Share device info" : null, z2 ? "Force performance class" : null, (!z2 || InstantCameraView.allowBigSizeCameraDebug()) ? null : !SharedConfig.bigCameraForRound ? "Force big camera for round" : "Disable big camera for round", LocaleController.getString(DualCameraView.dualAvailableStatic(ProfileActivity.this.getContext()) ? "DebugMenuDualOff" : "DebugMenuDualOn"), BuildVars.DEBUG_VERSION ? SharedConfig.useSurfaceInStories ? "back to TextureView in stories" : "use SurfaceView in stories" : null, BuildVars.DEBUG_PRIVATE_VERSION ? SharedConfig.photoViewerBlur ? "do not blur in photoviewer" : "blur in photoviewer" : null, !SharedConfig.payByInvoice ? "Enable Invoice Payment" : "Disable Invoice Payment", BuildVars.DEBUG_PRIVATE_VERSION ? "Update Attach Bots" : null, i6 >= 21 ? !SharedConfig.isUsingCamera2(((BaseFragment) ProfileActivity.this).currentAccount) ? "Use Camera 2 API" : "Use old Camera 1 API" : null, BuildVars.DEBUG_VERSION ? "Clear Mini Apps Permissions and Files" : null, BuildVars.DEBUG_PRIVATE_VERSION ? "Clear all login tokens" : null, (!SharedConfig.canBlurChat() || i6 < 31) ? null : SharedConfig.useNewBlur ? "back to cpu blur" : "use new gpu blur", SharedConfig.adaptableColorInBrowser ? "Disabled adaptive browser colors" : "Enable adaptive browser colors", SharedConfig.debugVideoQualities ? "Disable video qualities debug" : "Enable video qualities debug"};
            final Context context = this.val$context;
            builder.setItems(charSequenceArr, new DialogInterface.OnClickListener() {
                @Override
                public final void onClick(DialogInterface dialogInterface, int i7) {
                    ProfileActivity.AnonymousClass17.this.lambda$onItemClick$4(context, dialogInterface, i7);
                }
            });
            builder.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), null);
            ProfileActivity.this.showDialog(builder.create());
            return true;
        }
    }

    class AnonymousClass18 extends RecyclerView.OnScrollListener {
        AnonymousClass18() {
        }

        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int i) {
            if (i == 1) {
                AndroidUtilities.hideKeyboard(ProfileActivity.this.getParentActivity().getCurrentFocus());
            }
        }
    }

    class AnonymousClass19 extends FrameLayout {
        AnonymousClass19(Context context) {
            super(context);
        }

        @Override
        protected void onDraw(Canvas canvas) {
            int intrinsicHeight = Theme.chat_composeShadowDrawable.getIntrinsicHeight();
            Theme.chat_composeShadowDrawable.setBounds(0, 0, getMeasuredWidth(), intrinsicHeight);
            Theme.chat_composeShadowDrawable.draw(canvas);
            canvas.drawRect(0.0f, intrinsicHeight, getMeasuredWidth(), getMeasuredHeight(), Theme.chat_composeBackgroundPaint);
        }
    }

    public class AnonymousClass2 extends AnimationProperties.FloatProperty {
        AnonymousClass2(String str) {
            super(str);
        }

        @Override
        public Float get(ProfileActivity profileActivity) {
            return Float.valueOf(ProfileActivity.this.headerShadowAlpha);
        }

        @Override
        public void setValue(ProfileActivity profileActivity, float f) {
            ProfileActivity profileActivity2 = ProfileActivity.this;
            profileActivity2.headerShadowAlpha = f;
            profileActivity2.topView.invalidate();
        }
    }

    public class AnonymousClass20 implements ChatRightsEditActivity.ChatRightsEditActivityDelegate {
        final TLRPC.Chat val$chat;
        final ChatRightsEditActivity val$fragment;

        AnonymousClass20(TLRPC.Chat chat, ChatRightsEditActivity chatRightsEditActivity) {
            this.val$chat = chat;
            this.val$fragment = chatRightsEditActivity;
        }

        public static void lambda$didSetRights$0(BaseFragment baseFragment, TLRPC.User user, TLRPC.Chat chat) {
            BulletinFactory.createRemoveFromChatBulletin(baseFragment, user, chat.title).show();
        }

        @Override
        public void didChangeOwner(TLRPC.User user) {
            ProfileActivity.this.undoView.showWithAction(-ProfileActivity.this.chatId, ProfileActivity.this.currentChat.megagroup ? 10 : 9, user);
        }

        @Override
        public void didSetRights(int i, TLRPC.TL_chatAdminRights tL_chatAdminRights, TLRPC.TL_chatBannedRights tL_chatBannedRights, String str) {
            ChatRightsEditActivity chatRightsEditActivity;
            ProfileActivity.this.removeSelfFromStack();
            final TLRPC.User user = ProfileActivity.this.getMessagesController().getUser(Long.valueOf(ProfileActivity.this.userId));
            if (user == null || this.val$chat == null || ProfileActivity.this.userId == 0 || (chatRightsEditActivity = this.val$fragment) == null || !chatRightsEditActivity.banning || chatRightsEditActivity.getParentLayout() == null) {
                return;
            }
            for (final BaseFragment baseFragment : this.val$fragment.getParentLayout().getFragmentStack()) {
                if (baseFragment instanceof ChannelAdminLogActivity) {
                    ((ChannelAdminLogActivity) baseFragment).lambda$processSelectedOption$20();
                    final TLRPC.Chat chat = this.val$chat;
                    AndroidUtilities.runOnUIThread(new Runnable() {
                        @Override
                        public final void run() {
                            ProfileActivity.AnonymousClass20.lambda$didSetRights$0(BaseFragment.this, user, chat);
                        }
                    });
                    return;
                }
            }
        }
    }

    public class AnonymousClass21 extends FrameLayout {
        CanvasButton canvasButton;

        AnonymousClass21(Context context) {
            super(context);
        }

        public void lambda$dispatchDraw$0() {
            if (ProfileActivity.this.customAvatarProgress != 1.0f) {
                ProfileActivity.this.avatarsViewPager.scrollToLastItem();
            }
        }

        @Override
        protected void dispatchDraw(Canvas canvas) {
            super.dispatchDraw(canvas);
            if (ProfileActivity.this.transitionOnlineText != null) {
                canvas.save();
                canvas.translate(ProfileActivity.this.onlineTextView[0].getX(), ProfileActivity.this.onlineTextView[0].getY());
                canvas.saveLayerAlpha(0.0f, 0.0f, ProfileActivity.this.transitionOnlineText.getMeasuredWidth(), ProfileActivity.this.transitionOnlineText.getMeasuredHeight(), (int) ((1.0f - ProfileActivity.this.avatarAnimationProgress) * 255.0f), 31);
                ProfileActivity.this.transitionOnlineText.draw(canvas);
                canvas.restore();
                canvas.restore();
                invalidate();
            }
            if (ProfileActivity.this.hasFallbackPhoto) {
                ProfileActivity profileActivity = ProfileActivity.this;
                if (profileActivity.photoDescriptionProgress == 0.0f || profileActivity.customAvatarProgress == 1.0f) {
                    return;
                }
                float y = ProfileActivity.this.onlineTextView[1].getY() + (ProfileActivity.this.onlineTextView[1].getMeasuredHeight() / 2.0f);
                float dp = AndroidUtilities.dp(22.0f);
                float dp2 = ((AndroidUtilities.dp(28.0f) - ProfileActivity.this.customPhotoOffset) + ProfileActivity.this.onlineTextView[1].getX()) - dp;
                ProfileActivity.this.fallbackImage.setImageCoords(dp2, y - (dp / 2.0f), dp, dp);
                ProfileActivity.this.fallbackImage.setAlpha(ProfileActivity.this.photoDescriptionProgress);
                canvas.save();
                ProfileActivity profileActivity2 = ProfileActivity.this;
                float f = profileActivity2.photoDescriptionProgress;
                canvas.scale(f, f, profileActivity2.fallbackImage.getCenterX(), ProfileActivity.this.fallbackImage.getCenterY());
                ProfileActivity.this.fallbackImage.draw(canvas);
                canvas.restore();
                if (ProfileActivity.this.customAvatarProgress != 0.0f) {
                    CanvasButton canvasButton = this.canvasButton;
                    if (canvasButton != null) {
                        canvasButton.cancelRipple();
                        return;
                    }
                    return;
                }
                if (this.canvasButton == null) {
                    CanvasButton canvasButton2 = new CanvasButton(this);
                    this.canvasButton = canvasButton2;
                    canvasButton2.setDelegate(new Runnable() {
                        @Override
                        public final void run() {
                            ProfileActivity.AnonymousClass21.this.lambda$dispatchDraw$0();
                        }
                    });
                }
                RectF rectF = AndroidUtilities.rectTmp;
                rectF.set(dp2 - AndroidUtilities.dp(4.0f), y - AndroidUtilities.dp(14.0f), dp2 + ProfileActivity.this.onlineTextView[2].getTextWidth() + (AndroidUtilities.dp(28.0f) * (1.0f - ProfileActivity.this.customAvatarProgress)) + AndroidUtilities.dp(4.0f), y + AndroidUtilities.dp(14.0f));
                this.canvasButton.setRect(rectF);
                this.canvasButton.setRounded(true);
                this.canvasButton.setColor(0, ColorUtils.setAlphaComponent(-1, 50));
                this.canvasButton.draw(canvas);
            }
        }

        @Override
        protected void onAttachedToWindow() {
            super.onAttachedToWindow();
            ProfileActivity.this.fallbackImage.onAttachedToWindow();
        }

        @Override
        protected void onDetachedFromWindow() {
            super.onDetachedFromWindow();
            ProfileActivity.this.fallbackImage.onDetachedFromWindow();
        }

        @Override
        public boolean onInterceptTouchEvent(MotionEvent motionEvent) {
            CanvasButton canvasButton = this.canvasButton;
            return (canvasButton != null && canvasButton.checkTouchEvent(motionEvent)) || super.onInterceptTouchEvent(motionEvent);
        }

        @Override
        public boolean onTouchEvent(MotionEvent motionEvent) {
            CanvasButton canvasButton = this.canvasButton;
            return (canvasButton != null && canvasButton.checkTouchEvent(motionEvent)) || super.onTouchEvent(motionEvent);
        }
    }

    class AnonymousClass22 extends AvatarImageView {
        AnonymousClass22(Context context) {
            super(context);
        }

        @Override
        protected void dispatchDraw(Canvas canvas) {
            super.dispatchDraw(canvas);
            AnimatedEmojiDrawable animatedEmojiDrawable = this.animatedEmojiDrawable;
            if (animatedEmojiDrawable == null || animatedEmojiDrawable.getImageReceiver() == null) {
                return;
            }
            this.animatedEmojiDrawable.getImageReceiver().startAnimation();
        }

        @Override
        public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo accessibilityNodeInfo) {
            super.onInitializeAccessibilityNodeInfo(accessibilityNodeInfo);
            if (!getImageReceiver().hasNotThumb()) {
                accessibilityNodeInfo.setVisibleToUser(false);
                return;
            }
            accessibilityNodeInfo.setText(LocaleController.getString(R.string.AccDescrProfilePicture));
            if (Build.VERSION.SDK_INT >= 21) {
                accessibilityNodeInfo.addAction(new AccessibilityNodeInfo.AccessibilityAction(16, LocaleController.getString(R.string.Open)));
                accessibilityNodeInfo.addAction(new AccessibilityNodeInfo.AccessibilityAction(32, LocaleController.getString(R.string.AccDescrOpenInPhotoViewer)));
            }
        }
    }

    class AnonymousClass23 extends RadialProgressView {
        private Paint paint;

        AnonymousClass23(Context context) {
            super(context);
            Paint paint = new Paint(1);
            this.paint = paint;
            paint.setColor(1426063360);
        }

        @Override
        public void onDraw(Canvas canvas) {
            if (ProfileActivity.this.avatarImage != null && ProfileActivity.this.avatarImage.getImageReceiver().hasNotThumb()) {
                this.paint.setAlpha((int) (ProfileActivity.this.avatarImage.getImageReceiver().getCurrentAlpha() * 85.0f));
                canvas.drawCircle(getMeasuredWidth() / 2.0f, getMeasuredHeight() / 2.0f, getMeasuredWidth() / 2.0f, this.paint);
            }
            super.onDraw(canvas);
        }
    }

    class AnonymousClass24 extends ProfileGalleryView {
        AnonymousClass24(Context context, long j, ActionBar actionBar, RecyclerListView recyclerListView, AvatarImageView avatarImageView, int i, ProfileGalleryView.Callback callback) {
            super(context, j, actionBar, recyclerListView, avatarImageView, i, callback);
        }

        @Override
        protected void setCustomAvatarProgress(float f) {
            ProfileActivity.this.customAvatarProgress = f;
            ProfileActivity.this.checkPhotoDescriptionAlpha();
        }
    }

    class AnonymousClass25 extends SimpleTextView {
        AnonymousClass25(Context context) {
            super(context);
        }

        @Override
        public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo accessibilityNodeInfo) {
            super.onInitializeAccessibilityNodeInfo(accessibilityNodeInfo);
            if (isFocusable()) {
                if (ProfileActivity.this.nameTextViewRightDrawableContentDescription == null && ProfileActivity.this.nameTextViewRightDrawable2ContentDescription == null) {
                    return;
                }
                StringBuilder sb = new StringBuilder(getText());
                if (ProfileActivity.this.nameTextViewRightDrawable2ContentDescription != null) {
                    if (sb.length() > 0) {
                        sb.append(", ");
                    }
                    sb.append(ProfileActivity.this.nameTextViewRightDrawable2ContentDescription);
                }
                if (ProfileActivity.this.nameTextViewRightDrawableContentDescription != null) {
                    if (sb.length() > 0) {
                        sb.append(", ");
                    }
                    sb.append(ProfileActivity.this.nameTextViewRightDrawableContentDescription);
                }
                accessibilityNodeInfo.setText(sb);
            }
        }
    }

    class AnonymousClass26 extends LinkSpanDrawable.ClickableSmallTextView {
        AnonymousClass26(Context context) {
            super(context);
        }

        @Override
        public void setAlpha(float f) {
            super.setAlpha(f);
            ProfileActivity.this.checkPhotoDescriptionAlpha();
        }

        @Override
        public void setTextColor(int i) {
            super.setTextColor(i);
            if (ProfileActivity.this.onlineTextView[2] != null) {
                ProfileActivity.this.onlineTextView[2].setTextColor(i);
                ProfileActivity.this.onlineTextView[3].setTextColor(i);
            }
            if (ProfileActivity.this.showStatusButton != null) {
                ProfileActivity.this.showStatusButton.setTextColor(Theme.multAlpha(Theme.adaptHSV(i, -0.02f, 0.15f), 1.4f));
            }
        }

        @Override
        public void setTranslationX(float f) {
            super.setTranslationX(f);
            ProfileActivity.this.onlineTextView[2].setTranslationX(f);
            ProfileActivity.this.onlineTextView[3].setTranslationX(f);
        }

        @Override
        public void setTranslationY(float f) {
            super.setTranslationY(f);
            ProfileActivity.this.onlineTextView[2].setTranslationY(f);
            ProfileActivity.this.onlineTextView[3].setTranslationY(f);
        }
    }

    class AnonymousClass27 extends AudioPlayerAlert.ClippingTextViewSwitcher {
        final Context val$context;

        AnonymousClass27(Context context, Context context2) {
            super(context);
            r3 = context2;
        }

        @Override
        protected TextView createTextView() {
            TextView textView = new TextView(r3);
            textView.setTextColor(ProfileActivity.this.getThemedColor(Theme.key_player_actionBarSubtitle));
            textView.setTextSize(0, AndroidUtilities.dp(14.0f));
            textView.setSingleLine(true);
            textView.setEllipsize(TextUtils.TruncateAt.END);
            textView.setGravity(3);
            return textView;
        }
    }

    class AnonymousClass28 extends ProfileStoriesView {
        final Context val$context;

        AnonymousClass28(Context context, int i, long j, boolean z, View view, AvatarImageView avatarImageView, Theme.ResourcesProvider resourcesProvider, Context context2) {
            super(context, i, j, z, view, avatarImageView, resourcesProvider);
            r20 = context2;
        }

        @Override
        public void lambda$new$4() {
            ProfileActivity.this.openAvatar();
        }

        @Override
        protected void onTap(StoryViewer.PlaceProvider placeProvider) {
            StoryViewer orCreateStoryViewer;
            Context context;
            TL_stories.PeerStories peerStories;
            long dialogId = ProfileActivity.this.getDialogId();
            StoriesController storiesController = ProfileActivity.this.getMessagesController().getStoriesController();
            if (storiesController.hasStories(dialogId) || storiesController.hasUploadingStories(dialogId) || storiesController.isLastUploadingFailed(dialogId)) {
                ProfileActivity.this.getOrCreateStoryViewer().open(r20, dialogId, placeProvider);
                return;
            }
            if (ProfileActivity.this.userInfo != null && ProfileActivity.this.userInfo.stories != null && !ProfileActivity.this.userInfo.stories.stories.isEmpty() && ProfileActivity.this.userId != ProfileActivity.this.getUserConfig().clientUserId) {
                orCreateStoryViewer = ProfileActivity.this.getOrCreateStoryViewer();
                context = r20;
                peerStories = ProfileActivity.this.userInfo.stories;
            } else if (ProfileActivity.this.chatInfo == null || ProfileActivity.this.chatInfo.stories == null || ProfileActivity.this.chatInfo.stories.stories.isEmpty()) {
                ProfileActivity.this.expandAvatar();
                return;
            } else {
                orCreateStoryViewer = ProfileActivity.this.getOrCreateStoryViewer();
                context = r20;
                peerStories = ProfileActivity.this.chatInfo.stories;
            }
            orCreateStoryViewer.open(context, peerStories, placeProvider);
        }
    }

    class AnonymousClass29 extends RecyclerView.OnScrollListener {
        AnonymousClass29() {
        }

        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int i) {
            if (i == 1) {
                AndroidUtilities.hideKeyboard(ProfileActivity.this.getParentActivity().getCurrentFocus());
            }
            if (ProfileActivity.this.openingAvatar && i != 2) {
                ProfileActivity.this.openingAvatar = false;
            }
            if (ProfileActivity.this.searchItem != null) {
                ProfileActivity.this.scrolling = i != 0;
                ProfileActivity.this.searchItem.setEnabled((ProfileActivity.this.scrolling || ProfileActivity.this.isPulledDown) ? false : true);
            }
            ProfileActivity profileActivity = ProfileActivity.this;
            profileActivity.sharedMediaLayout.scrollingByUser = profileActivity.listView.scrollingByUser;
        }

        @Override
        public void onScrolled(RecyclerView recyclerView, int i, int i2) {
            if (ProfileActivity.this.fwdRestrictedHint != null) {
                ProfileActivity.this.fwdRestrictedHint.hide();
            }
            ProfileActivity.this.checkListViewScroll();
            if (ProfileActivity.this.participantsMap != null && !ProfileActivity.this.usersEndReached && ProfileActivity.this.layoutManager.findLastVisibleItemPosition() > ProfileActivity.this.membersEndRow - 8) {
                ProfileActivity.this.getChannelParticipants(false);
            }
            SharedMediaLayout sharedMediaLayout = ProfileActivity.this.sharedMediaLayout;
            sharedMediaLayout.setPinnedToTop(sharedMediaLayout.getY() <= 0.0f);
            ProfileActivity.this.updateBottomButtonY();
        }
    }

    public class AnonymousClass3 extends PhotoViewer.EmptyPhotoViewerProvider {
        AnonymousClass3() {
        }

        @Override
        public org.telegram.ui.PhotoViewer.PlaceProviderObject getPlaceForPhoto(org.telegram.messenger.MessageObject r7, org.telegram.tgnet.TLRPC.FileLocation r8, int r9, boolean r10) {
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.ProfileActivity.AnonymousClass3.getPlaceForPhoto(org.telegram.messenger.MessageObject, org.telegram.tgnet.TLRPC$FileLocation, int, boolean):org.telegram.ui.PhotoViewer$PlaceProviderObject");
        }

        @Override
        public void openPhotoForEdit(String str, String str2, boolean z) {
            ProfileActivity.this.imageUpdater.openPhotoForEdit(str, str2, 0, z);
        }

        @Override
        public void willHidePhotoViewer() {
            ProfileActivity.this.avatarImage.getImageReceiver().setVisible(true, true);
        }
    }

    class AnonymousClass30 extends AnimatorListenerAdapter {
        AnonymousClass30() {
        }

        @Override
        public void onAnimationEnd(Animator animator) {
            ((BaseFragment) ProfileActivity.this).actionBar.setItemsBackgroundColor(ProfileActivity.this.isPulledDown ? 1090519039 : ProfileActivity.this.peerColor != null ? 553648127 : ProfileActivity.this.getThemedColor(Theme.key_avatar_actionBarSelectorBlue), false);
            ProfileActivity.this.avatarImage.clearForeground();
            ProfileActivity.this.doNotSetForeground = false;
            ProfileActivity.this.updateStoriesViewBounds(false);
        }

        @Override
        public void onAnimationStart(Animator animator) {
        }
    }

    class AnonymousClass31 extends PinchToZoomHelper {
        AnonymousClass31(ViewGroup viewGroup, ViewGroup viewGroup2) {
            super(viewGroup, viewGroup2);
        }

        @Override
        protected void drawOverlays(Canvas canvas, float f, float f2, float f3, float f4, float f5) {
            if (f > 0.0f) {
                RectF rectF = AndroidUtilities.rectTmp;
                rectF.set(0.0f, 0.0f, ProfileActivity.this.avatarsViewPager.getMeasuredWidth(), ProfileActivity.this.avatarsViewPager.getMeasuredHeight() + AndroidUtilities.dp(30.0f));
                canvas.saveLayerAlpha(rectF, (int) (255.0f * f), 31);
                ProfileActivity.this.avatarContainer2.draw(canvas);
                ((BaseFragment) ProfileActivity.this).actionBar.getOccupyStatusBar();
                canvas.save();
                canvas.translate(((BaseFragment) ProfileActivity.this).actionBar.getX(), ((BaseFragment) ProfileActivity.this).actionBar.getY());
                ((BaseFragment) ProfileActivity.this).actionBar.draw(canvas);
                canvas.restore();
                if (ProfileActivity.this.writeButton != null && ProfileActivity.this.writeButton.getVisibility() == 0 && ProfileActivity.this.writeButton.getAlpha() > 0.0f) {
                    canvas.save();
                    float f6 = (f * 0.5f) + 0.5f;
                    canvas.scale(f6, f6, ProfileActivity.this.writeButton.getX() + (ProfileActivity.this.writeButton.getMeasuredWidth() / 2.0f), ProfileActivity.this.writeButton.getY() + (ProfileActivity.this.writeButton.getMeasuredHeight() / 2.0f));
                    canvas.translate(ProfileActivity.this.writeButton.getX(), ProfileActivity.this.writeButton.getY());
                    ProfileActivity.this.writeButton.draw(canvas);
                    canvas.restore();
                }
                canvas.restore();
            }
        }

        @Override
        public void invalidateViews() {
            super.invalidateViews();
            ProfileActivity.this.fragmentView.invalidate();
            for (int i = 0; i < ProfileActivity.this.avatarsViewPager.getChildCount(); i++) {
                ProfileActivity.this.avatarsViewPager.getChildAt(i).invalidate();
            }
            if (ProfileActivity.this.writeButton != null) {
                ProfileActivity.this.writeButton.invalidate();
            }
        }

        @Override
        public boolean zoomEnabled(View view, ImageReceiver imageReceiver) {
            return super.zoomEnabled(view, imageReceiver) && ProfileActivity.this.listView.getScrollState() != 1;
        }
    }

    class AnonymousClass32 implements PinchToZoomHelper.Callback {
        AnonymousClass32() {
        }

        @Override
        public TextureView getCurrentTextureView() {
            return PinchToZoomHelper.Callback.CC.$default$getCurrentTextureView(this);
        }

        @Override
        public void onZoomFinished(MessageObject messageObject) {
            PinchToZoomHelper.Callback.CC.$default$onZoomFinished(this, messageObject);
        }

        @Override
        public void onZoomStarted(MessageObject messageObject) {
            ProfileActivity.this.listView.cancelClickRunnables(true);
            SharedMediaLayout sharedMediaLayout = ProfileActivity.this.sharedMediaLayout;
            if (sharedMediaLayout != null && sharedMediaLayout.getCurrentListView() != null) {
                ProfileActivity.this.sharedMediaLayout.getCurrentListView().cancelClickRunnables(true);
            }
            TopView topView = ProfileActivity.this.topView;
            ProfileActivity profileActivity = ProfileActivity.this;
            topView.setBackgroundColor(ColorUtils.blendARGB(profileActivity.getAverageColor(profileActivity.pinchToZoomHelper.getPhotoImage()), ProfileActivity.this.getThemedColor(Theme.key_windowBackgroundWhite), 0.1f));
        }
    }

    class AnonymousClass33 extends View {
        AnonymousClass33(Context context) {
            super(context);
        }

        @Override
        public void setAlpha(float f) {
            super.setAlpha(f);
            View view = ProfileActivity.this.fragmentView;
            if (view != null) {
                view.invalidate();
            }
        }
    }

    public class AnonymousClass34 implements StoryRecorder.ClosingViewProvider {
        final TLRPC.Chat val$currentChat;

        AnonymousClass34(TLRPC.Chat chat) {
            r2 = chat;
        }

        @Override
        public StoryRecorder.SourceView getView(long j) {
            if (j != ProfileActivity.this.getDialogId()) {
                return null;
            }
            ProfileActivity.this.updateAvatarRoundRadius();
            return StoryRecorder.SourceView.fromAvatarImage(ProfileActivity.this.avatarImage, ChatObject.isForum(r2));
        }

        @Override
        public void preLayout(long j, Runnable runnable) {
            ProfileActivity.this.avatarImage.setHasStories(ProfileActivity.this.needInsetForStories());
            if (j == ProfileActivity.this.getDialogId()) {
                ProfileActivity.this.collapseAvatarInstant();
            }
            AndroidUtilities.runOnUIThread(runnable, 30L);
        }
    }

    public class AnonymousClass35 extends ViewOutlineProvider {
        AnonymousClass35() {
        }

        @Override
        public void getOutline(View view, Outline outline) {
            outline.setOval(0, 0, AndroidUtilities.dp(56.0f), AndroidUtilities.dp(56.0f));
        }
    }

    public class AnonymousClass36 extends SelectAnimatedEmojiDialog {
        final SelectAnimatedEmojiDialog.SelectAnimatedEmojiDialogWindow[] val$popup;

        AnonymousClass36(BaseFragment baseFragment, Context context, boolean z, Integer num, int i, boolean z2, Theme.ResourcesProvider resourcesProvider, int i2, SelectAnimatedEmojiDialog.SelectAnimatedEmojiDialogWindow[] selectAnimatedEmojiDialogWindowArr) {
            super(baseFragment, context, z, num, i, z2, resourcesProvider, i2);
            r20 = selectAnimatedEmojiDialogWindowArr;
        }

        @Override
        protected void onEmojiSelected(View view, Long l, TLRPC.Document document, Integer num) {
            TLRPC.TL_channels_updateEmojiStatus tL_channels_updateEmojiStatus;
            AnimatedEmojiDrawable.SwapAnimatedEmojiDrawable swapAnimatedEmojiDrawable;
            if (ProfileActivity.this.currentChat == null) {
                TL_account.updateEmojiStatus updateemojistatus = new TL_account.updateEmojiStatus();
                if (l == null) {
                    updateemojistatus.emoji_status = new TLRPC.TL_emojiStatusEmpty();
                } else if (num != null) {
                    TLRPC.TL_emojiStatusUntil tL_emojiStatusUntil = new TLRPC.TL_emojiStatusUntil();
                    updateemojistatus.emoji_status = tL_emojiStatusUntil;
                    tL_emojiStatusUntil.document_id = l.longValue();
                    ((TLRPC.TL_emojiStatusUntil) updateemojistatus.emoji_status).until = num.intValue();
                } else {
                    TLRPC.TL_emojiStatus tL_emojiStatus = new TLRPC.TL_emojiStatus();
                    updateemojistatus.emoji_status = tL_emojiStatus;
                    tL_emojiStatus.document_id = l.longValue();
                }
                TLRPC.User user = MessagesController.getInstance(((BaseFragment) ProfileActivity.this).currentAccount).getUser(Long.valueOf(UserConfig.getInstance(((BaseFragment) ProfileActivity.this).currentAccount).getClientUserId()));
                tL_channels_updateEmojiStatus = updateemojistatus;
                if (user != null) {
                    user.emoji_status = updateemojistatus.emoji_status;
                    MessagesController.getInstance(((BaseFragment) ProfileActivity.this).currentAccount).updateEmojiStatusUntilUpdate(user.id, user.emoji_status);
                    NotificationCenter.getInstance(((BaseFragment) ProfileActivity.this).currentAccount).lambda$postNotificationNameOnUIThread$1(NotificationCenter.userEmojiStatusUpdated, user);
                    tL_channels_updateEmojiStatus = updateemojistatus;
                }
            } else {
                TLRPC.TL_channels_updateEmojiStatus tL_channels_updateEmojiStatus2 = new TLRPC.TL_channels_updateEmojiStatus();
                tL_channels_updateEmojiStatus2.channel = MessagesController.getInputChannel(ProfileActivity.this.currentChat);
                if (l == null) {
                    tL_channels_updateEmojiStatus2.emoji_status = new TLRPC.TL_emojiStatusEmpty();
                } else if (num != null) {
                    TLRPC.TL_emojiStatusUntil tL_emojiStatusUntil2 = new TLRPC.TL_emojiStatusUntil();
                    tL_channels_updateEmojiStatus2.emoji_status = tL_emojiStatusUntil2;
                    tL_emojiStatusUntil2.document_id = l.longValue();
                    ((TLRPC.TL_emojiStatusUntil) tL_channels_updateEmojiStatus2.emoji_status).until = num.intValue();
                } else {
                    TLRPC.TL_emojiStatus tL_emojiStatus2 = new TLRPC.TL_emojiStatus();
                    tL_channels_updateEmojiStatus2.emoji_status = tL_emojiStatus2;
                    tL_emojiStatus2.document_id = l.longValue();
                }
                tL_channels_updateEmojiStatus = tL_channels_updateEmojiStatus2;
                if (ProfileActivity.this.currentChat != null) {
                    ProfileActivity.this.currentChat.emoji_status = tL_channels_updateEmojiStatus2.emoji_status;
                    MessagesController.getInstance(((BaseFragment) ProfileActivity.this).currentAccount).updateEmojiStatusUntilUpdate(-ProfileActivity.this.currentChat.id, ProfileActivity.this.currentChat.emoji_status);
                    NotificationCenter.getInstance(((BaseFragment) ProfileActivity.this).currentAccount).lambda$postNotificationNameOnUIThread$1(NotificationCenter.updateInterfaces, Integer.valueOf(MessagesController.UPDATE_MASK_EMOJI_STATUS));
                    tL_channels_updateEmojiStatus = tL_channels_updateEmojiStatus2;
                }
            }
            int i = 0;
            while (true) {
                Drawable drawable = null;
                if (i >= 2) {
                    break;
                }
                if (ProfileActivity.this.emojiStatusDrawable[i] != null) {
                    if (l == null && ProfileActivity.this.currentChat == null) {
                        swapAnimatedEmojiDrawable = ProfileActivity.this.emojiStatusDrawable[i];
                        drawable = ProfileActivity.this.getPremiumCrossfadeDrawable(i);
                    } else {
                        AnimatedEmojiDrawable.SwapAnimatedEmojiDrawable[] swapAnimatedEmojiDrawableArr = ProfileActivity.this.emojiStatusDrawable;
                        if (l != null) {
                            swapAnimatedEmojiDrawableArr[i].set(l.longValue(), true);
                        } else {
                            swapAnimatedEmojiDrawable = swapAnimatedEmojiDrawableArr[i];
                        }
                    }
                    swapAnimatedEmojiDrawable.set(drawable, true);
                }
                i++;
            }
            if (l != null) {
                ProfileActivity.this.animatedStatusView.animateChange(ReactionsLayoutInBubble.VisibleReaction.fromCustomEmoji(l));
            }
            ProfileActivity.this.updateEmojiStatusDrawableColor();
            ProfileActivity.this.updateEmojiStatusEffectPosition();
            ConnectionsManager.getInstance(((BaseFragment) ProfileActivity.this).currentAccount).sendRequest(tL_channels_updateEmojiStatus, null);
            if (r20[0] != null) {
                ProfileActivity.this.selectAnimatedEmojiDialog = null;
                r20[0].dismiss();
            }
        }
    }

    public class AnonymousClass37 extends SelectAnimatedEmojiDialog.SelectAnimatedEmojiDialogWindow {
        AnonymousClass37(View view, int i, int i2) {
            super(view, i, i2);
        }

        @Override
        public void dismiss() {
            super.dismiss();
            ProfileActivity.this.selectAnimatedEmojiDialog = null;
        }
    }

    public class AnonymousClass38 extends ChatRightsEditActivity {
        final boolean[] val$needShowBulletin;
        final TLRPC.User val$user;

        AnonymousClass38(long j, long j2, TLRPC.TL_chatAdminRights tL_chatAdminRights, TLRPC.TL_chatBannedRights tL_chatBannedRights, TLRPC.TL_chatBannedRights tL_chatBannedRights2, String str, int i, boolean z, boolean z2, String str2, boolean[] zArr, TLRPC.User user) {
            super(j, j2, tL_chatAdminRights, tL_chatBannedRights, tL_chatBannedRights2, str, i, z, z2, str2);
            r28 = zArr;
            r29 = user;
        }

        @Override
        public void onTransitionAnimationEnd(boolean z, boolean z2) {
            if (!z && z2 && r28[0] && BulletinFactory.canShowBulletin(ProfileActivity.this)) {
                BulletinFactory.createPromoteToAdminBulletin(ProfileActivity.this, r29.first_name).show();
            }
        }
    }

    public class AnonymousClass39 implements ChatRightsEditActivity.ChatRightsEditActivityDelegate {
        final int val$action;
        final boolean val$editingAdmin;
        final boolean[] val$needShowBulletin;
        final TLRPC.ChatParticipant val$participant;

        AnonymousClass39(int i, TLRPC.ChatParticipant chatParticipant, boolean z, boolean[] zArr) {
            r2 = i;
            r3 = chatParticipant;
            r4 = z;
            r5 = zArr;
        }

        @Override
        public void didChangeOwner(TLRPC.User user) {
            ProfileActivity.this.undoView.showWithAction(-ProfileActivity.this.chatId, ProfileActivity.this.currentChat.megagroup ? 10 : 9, user);
        }

        @Override
        public void didSetRights(int i, TLRPC.TL_chatAdminRights tL_chatAdminRights, TLRPC.TL_chatBannedRights tL_chatBannedRights, String str) {
            boolean z;
            int i2 = r2;
            int i3 = 0;
            if (i2 == 0) {
                TLRPC.ChatParticipant chatParticipant = r3;
                if (chatParticipant instanceof TLRPC.TL_chatChannelParticipant) {
                    TLRPC.TL_chatChannelParticipant tL_chatChannelParticipant = (TLRPC.TL_chatChannelParticipant) chatParticipant;
                    if (i == 1) {
                        TLRPC.TL_channelParticipantAdmin tL_channelParticipantAdmin = new TLRPC.TL_channelParticipantAdmin();
                        tL_chatChannelParticipant.channelParticipant = tL_channelParticipantAdmin;
                        tL_channelParticipantAdmin.flags |= 4;
                    } else {
                        tL_chatChannelParticipant.channelParticipant = new TLRPC.TL_channelParticipant();
                    }
                    tL_chatChannelParticipant.channelParticipant.inviter_id = ProfileActivity.this.getUserConfig().getClientUserId();
                    tL_chatChannelParticipant.channelParticipant.peer = new TLRPC.TL_peerUser();
                    TLRPC.ChannelParticipant channelParticipant = tL_chatChannelParticipant.channelParticipant;
                    TLRPC.Peer peer = channelParticipant.peer;
                    TLRPC.ChatParticipant chatParticipant2 = r3;
                    peer.user_id = chatParticipant2.user_id;
                    channelParticipant.date = chatParticipant2.date;
                    channelParticipant.banned_rights = tL_chatBannedRights;
                    channelParticipant.admin_rights = tL_chatAdminRights;
                    channelParticipant.rank = str;
                } else if (chatParticipant != null) {
                    TLRPC.ChatParticipant tL_chatParticipantAdmin = i == 1 ? new TLRPC.TL_chatParticipantAdmin() : new TLRPC.TL_chatParticipant();
                    TLRPC.ChatParticipant chatParticipant3 = r3;
                    tL_chatParticipantAdmin.user_id = chatParticipant3.user_id;
                    tL_chatParticipantAdmin.date = chatParticipant3.date;
                    tL_chatParticipantAdmin.inviter_id = chatParticipant3.inviter_id;
                    int indexOf = ProfileActivity.this.chatInfo.participants.participants.indexOf(r3);
                    if (indexOf >= 0) {
                        ProfileActivity.this.chatInfo.participants.participants.set(indexOf, tL_chatParticipantAdmin);
                    }
                }
                if (i != 1 || r4) {
                    return;
                }
                r5[0] = true;
                return;
            }
            if (i2 == 1 && i == 0 && ProfileActivity.this.currentChat.megagroup && ProfileActivity.this.chatInfo != null && ProfileActivity.this.chatInfo.participants != null) {
                int i4 = 0;
                while (true) {
                    if (i4 >= ProfileActivity.this.chatInfo.participants.participants.size()) {
                        z = false;
                        break;
                    } else {
                        if (MessageObject.getPeerId(((TLRPC.TL_chatChannelParticipant) ProfileActivity.this.chatInfo.participants.participants.get(i4)).channelParticipant.peer) == r3.user_id) {
                            ProfileActivity.this.chatInfo.participants_count--;
                            ProfileActivity.this.chatInfo.participants.participants.remove(i4);
                            z = true;
                            break;
                        }
                        i4++;
                    }
                }
                if (ProfileActivity.this.chatInfo != null && ProfileActivity.this.chatInfo.participants != null) {
                    while (true) {
                        if (i3 >= ProfileActivity.this.chatInfo.participants.participants.size()) {
                            break;
                        }
                        if (ProfileActivity.this.chatInfo.participants.participants.get(i3).user_id == r3.user_id) {
                            ProfileActivity.this.chatInfo.participants.participants.remove(i3);
                            z = true;
                            break;
                        }
                        i3++;
                    }
                }
                if (z) {
                    ProfileActivity.this.updateOnlineCount(true);
                    ProfileActivity.this.updateRowsIds();
                    ProfileActivity.this.listAdapter.notifyDataSetChanged();
                }
            }
        }
    }

    class AnonymousClass4 implements Bulletin.Delegate {
        AnonymousClass4() {
        }

        @Override
        public boolean allowLayoutChanges() {
            return Bulletin.Delegate.CC.$default$allowLayoutChanges(this);
        }

        @Override
        public boolean bottomOffsetAnimated() {
            return ProfileActivity.this.bottomButtonsContainer == null;
        }

        @Override
        public boolean clipWithGradient(int i) {
            return Bulletin.Delegate.CC.$default$clipWithGradient(this, i);
        }

        @Override
        public int getBottomOffset(int i) {
            if (ProfileActivity.this.bottomButtonsContainer == null) {
                return 0;
            }
            return (int) (((AndroidUtilities.dp(72.0f) - ProfileActivity.this.bottomButtonsContainer.getTranslationY()) - (Math.min(1.0f, 1.0f - Math.abs(ProfileActivity.this.sharedMediaLayout.getTabProgress() - 9.0f)) * ProfileActivity.this.bottomButtonContainer[1].getTranslationY())) - (Math.min(1.0f, 1.0f - Math.abs(ProfileActivity.this.sharedMediaLayout.getTabProgress() - 8.0f)) * ProfileActivity.this.bottomButtonContainer[0].getTranslationY()));
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
    }

    public class AnonymousClass40 extends ShareAlert {
        AnonymousClass40(Context context, ArrayList arrayList, String str, boolean z, String str2, boolean z2) {
            super(context, arrayList, str, z, str2, z2);
        }

        public void lambda$onSend$0(LongSparseArray longSparseArray, int i) {
            BulletinFactory.createInviteSentBulletin(ProfileActivity.this.getParentActivity(), ProfileActivity.this.contentView, longSparseArray.size(), longSparseArray.size() == 1 ? ((TLRPC.Dialog) longSparseArray.valueAt(0)).id : 0L, i, getThemedColor(Theme.key_undo_background), getThemedColor(Theme.key_undo_infoColor)).show();
        }

        @Override
        public void onSend(final LongSparseArray longSparseArray, final int i, TLRPC.TL_forumTopic tL_forumTopic) {
            AndroidUtilities.runOnUIThread(new Runnable() {
                @Override
                public final void run() {
                    ProfileActivity.AnonymousClass40.this.lambda$onSend$0(longSparseArray, i);
                }
            }, 250L);
        }
    }

    public class AnonymousClass41 extends ActionBarPopupWindow.ActionBarPopupWindowLayout {
        Path path = new Path();

        AnonymousClass41(Context context, int i, Theme.ResourcesProvider resourcesProvider) {
            super(context, i, resourcesProvider);
            this.path = new Path();
        }

        @Override
        protected boolean drawChild(Canvas canvas, View view, long j) {
            canvas.save();
            this.path.rewind();
            RectF rectF = AndroidUtilities.rectTmp;
            rectF.set(view.getLeft(), view.getTop(), view.getRight(), view.getBottom());
            this.path.addRoundRect(rectF, AndroidUtilities.dp(6.0f), AndroidUtilities.dp(6.0f), Path.Direction.CW);
            canvas.clipPath(this.path);
            boolean drawChild = super.drawChild(canvas, view, j);
            canvas.restore();
            return drawChild;
        }
    }

    public class AnonymousClass42 extends ActionBarPopupWindow.ActionBarPopupWindowLayout {
        Path path = new Path();

        AnonymousClass42(Context context, int i, Theme.ResourcesProvider resourcesProvider) {
            super(context, i, resourcesProvider);
            this.path = new Path();
        }

        @Override
        protected boolean drawChild(Canvas canvas, View view, long j) {
            canvas.save();
            this.path.rewind();
            RectF rectF = AndroidUtilities.rectTmp;
            rectF.set(view.getLeft(), view.getTop(), view.getRight(), view.getBottom());
            this.path.addRoundRect(rectF, AndroidUtilities.dp(6.0f), AndroidUtilities.dp(6.0f), Path.Direction.CW);
            canvas.clipPath(this.path);
            boolean drawChild = super.drawChild(canvas, view, j);
            canvas.restore();
            return drawChild;
        }
    }

    public class AnonymousClass43 extends ActionBarPopupWindow.ActionBarPopupWindowLayout {
        Path path = new Path();

        AnonymousClass43(Context context, int i, Theme.ResourcesProvider resourcesProvider) {
            super(context, i, resourcesProvider);
            this.path = new Path();
        }

        @Override
        protected boolean drawChild(Canvas canvas, View view, long j) {
            canvas.save();
            this.path.rewind();
            RectF rectF = AndroidUtilities.rectTmp;
            rectF.set(view.getLeft(), view.getTop(), view.getRight(), view.getBottom());
            this.path.addRoundRect(rectF, AndroidUtilities.dp(6.0f), AndroidUtilities.dp(6.0f), Path.Direction.CW);
            canvas.clipPath(this.path);
            boolean drawChild = super.drawChild(canvas, view, j);
            canvas.restore();
            return drawChild;
        }
    }

    public class AnonymousClass44 extends AnimationProperties.FloatProperty {
        AnonymousClass44(String str) {
            super(str);
        }

        @Override
        public Float get(ActionBar actionBar) {
            return Float.valueOf(ProfileActivity.this.mediaHeaderAnimationProgress);
        }

        @Override
        public void setValue(ActionBar actionBar, float f) {
            int themedColor;
            ProfileActivity.this.mediaHeaderAnimationProgress = f;
            if (ProfileActivity.this.storyView != null) {
                ProfileActivity.this.storyView.setActionBarActionMode(f);
            }
            ProfileActivity.this.topView.invalidate();
            int themedColor2 = ProfileActivity.this.getThemedColor(Theme.key_profile_title);
            ProfileActivity profileActivity = ProfileActivity.this;
            int i = Theme.key_player_actionBarTitle;
            int themedColor3 = profileActivity.getThemedColor(i);
            int offsetColor = AndroidUtilities.getOffsetColor(themedColor2, themedColor3, f, 1.0f);
            ProfileActivity.this.nameTextView[1].setTextColor(offsetColor);
            if (ProfileActivity.this.lockIconDrawable != null) {
                ProfileActivity.this.lockIconDrawable.setColorFilter(offsetColor, PorterDuff.Mode.MULTIPLY);
            }
            if (ProfileActivity.this.scamDrawable != null) {
                ProfileActivity.this.scamDrawable.setColor(AndroidUtilities.getOffsetColor(ProfileActivity.this.getThemedColor(Theme.key_avatar_subtitleInProfileBlue), themedColor3, f, 1.0f));
            }
            int i2 = -1;
            ((BaseFragment) ProfileActivity.this).actionBar.setItemsColor(AndroidUtilities.getOffsetColor(ProfileActivity.this.peerColor != null ? -1 : ProfileActivity.this.getThemedColor(Theme.key_actionBarDefaultIcon), ProfileActivity.this.getThemedColor(Theme.key_actionBarActionModeDefaultIcon), f, 1.0f), false);
            ((BaseFragment) ProfileActivity.this).actionBar.setItemsBackgroundColor(AndroidUtilities.getOffsetColor(ProfileActivity.this.peerColor != null ? 1090519039 : ProfileActivity.this.peerColor != null ? 553648127 : ProfileActivity.this.getThemedColor(Theme.key_avatar_actionBarSelectorBlue), ProfileActivity.this.getThemedColor(Theme.key_actionBarActionModeDefaultSelector), f, 1.0f), false);
            ProfileActivity.this.topView.invalidate();
            ProfileActivity.this.otherItem.setIconColor(ProfileActivity.this.peerColor != null ? -1 : ProfileActivity.this.getThemedColor(Theme.key_actionBarDefaultIcon));
            ProfileActivity.this.callItem.setIconColor(ProfileActivity.this.peerColor != null ? -1 : ProfileActivity.this.getThemedColor(Theme.key_actionBarDefaultIcon));
            ProfileActivity.this.videoCallItem.setIconColor(ProfileActivity.this.peerColor != null ? -1 : ProfileActivity.this.getThemedColor(Theme.key_actionBarDefaultIcon));
            ProfileActivity.this.editItem.setIconColor(ProfileActivity.this.peerColor != null ? -1 : ProfileActivity.this.getThemedColor(Theme.key_actionBarDefaultIcon));
            if (ProfileActivity.this.verifiedDrawable[0] != null) {
                ProfileActivity.this.verifiedDrawable[0].setColorFilter(AndroidUtilities.getOffsetColor(ProfileActivity.this.getThemedColor(Theme.key_profile_verifiedBackground), ProfileActivity.this.getThemedColor(i), f, 1.0f), PorterDuff.Mode.MULTIPLY);
            }
            if (ProfileActivity.this.verifiedDrawable[1] != null) {
                if (ProfileActivity.this.peerColor != null) {
                    themedColor = Theme.adaptHSV(ColorUtils.blendARGB(ProfileActivity.this.peerColor.getColor2(), ProfileActivity.this.peerColor.hasColor6(Theme.isCurrentThemeDark()) ? ProfileActivity.this.peerColor.getColor5() : ProfileActivity.this.peerColor.getColor3(), 0.4f), 0.1f, Theme.isCurrentThemeDark() ? -0.1f : -0.08f);
                } else {
                    themedColor = ProfileActivity.this.getThemedColor(Theme.key_profile_verifiedBackground);
                }
                ProfileActivity.this.verifiedDrawable[1].setColorFilter(AndroidUtilities.getOffsetColor(themedColor, ProfileActivity.this.getThemedColor(i), f, 1.0f), PorterDuff.Mode.MULTIPLY);
            }
            if (ProfileActivity.this.verifiedCheckDrawable[0] != null) {
                ProfileActivity.this.verifiedCheckDrawable[0].setColorFilter(AndroidUtilities.getOffsetColor(ProfileActivity.this.getThemedColor(Theme.key_profile_verifiedCheck), ProfileActivity.this.getThemedColor(Theme.key_windowBackgroundWhite), f, 1.0f), PorterDuff.Mode.MULTIPLY);
            }
            if (ProfileActivity.this.verifiedCheckDrawable[1] != null) {
                if (ProfileActivity.this.peerColor == null) {
                    ProfileActivity profileActivity2 = ProfileActivity.this;
                    i2 = profileActivity2.applyPeerColor(profileActivity2.getThemedColor(Theme.key_profile_verifiedCheck));
                }
                ProfileActivity.this.verifiedCheckDrawable[1].setColorFilter(AndroidUtilities.getOffsetColor(i2, ProfileActivity.this.getThemedColor(Theme.key_windowBackgroundWhite), f, 1.0f), PorterDuff.Mode.MULTIPLY);
            }
            if (ProfileActivity.this.premiumStarDrawable[0] != null) {
                ProfileActivity.this.premiumStarDrawable[0].setColorFilter(AndroidUtilities.getOffsetColor(ProfileActivity.this.getThemedColor(Theme.key_profile_verifiedBackground), ProfileActivity.this.getThemedColor(i), f, 1.0f), PorterDuff.Mode.MULTIPLY);
            }
            if (ProfileActivity.this.premiumStarDrawable[1] != null) {
                ProfileActivity profileActivity3 = ProfileActivity.this;
                int applyPeerColor = profileActivity3.applyPeerColor(profileActivity3.getThemedColor(Theme.key_profile_verifiedBackground));
                ProfileActivity profileActivity4 = ProfileActivity.this;
                ProfileActivity.this.premiumStarDrawable[1].setColorFilter(AndroidUtilities.getOffsetColor(applyPeerColor, profileActivity4.applyPeerColor(profileActivity4.getThemedColor(i)), f, 1.0f), PorterDuff.Mode.MULTIPLY);
            }
            ProfileActivity.this.updateEmojiStatusDrawableColor();
            if (ProfileActivity.this.avatarsViewPagerIndicatorView.getSecondaryMenuItem() != null) {
                if (ProfileActivity.this.videoCallItemVisible || ProfileActivity.this.editItemVisible || ProfileActivity.this.callItemVisible) {
                    ProfileActivity profileActivity5 = ProfileActivity.this;
                    profileActivity5.needLayoutText(Math.min(1.0f, profileActivity5.extraHeight / AndroidUtilities.dp(88.0f)));
                }
            }
        }
    }

    public class AnonymousClass45 extends AnimatorListenerAdapter {
        final ActionBarMenuItem val$mediaSearchItem;

        class AnonymousClass1 extends AnimatorListenerAdapter {
            AnonymousClass1() {
            }

            @Override
            public void onAnimationEnd(Animator animator) {
                ProfileActivity.this.headerShadowAnimatorSet = null;
            }
        }

        AnonymousClass45(ActionBarMenuItem actionBarMenuItem) {
            r2 = actionBarMenuItem;
        }

        @Override
        public void onAnimationCancel(Animator animator) {
            ProfileActivity.this.headerAnimatorSet = null;
        }

        @Override
        public void onAnimationEnd(Animator animator) {
            if (ProfileActivity.this.headerAnimatorSet != null) {
                if (ProfileActivity.this.mediaHeaderVisible) {
                    if (ProfileActivity.this.callItemVisible) {
                        ProfileActivity.this.callItem.setVisibility(8);
                    }
                    if (ProfileActivity.this.videoCallItemVisible) {
                        ProfileActivity.this.videoCallItem.setVisibility(8);
                    }
                    if (ProfileActivity.this.editItemVisible) {
                        ProfileActivity.this.editItem.setVisibility(8);
                    }
                    ProfileActivity.this.otherItem.setVisibility(8);
                } else {
                    if (ProfileActivity.this.sharedMediaLayout.isSearchItemVisible()) {
                        r2.setVisibility(0);
                    }
                    ProfileActivity.this.sharedMediaLayout.photoVideoOptionsItem.setVisibility(4);
                    ProfileActivity.this.headerShadowAnimatorSet = new AnimatorSet();
                    AnimatorSet animatorSet = ProfileActivity.this.headerShadowAnimatorSet;
                    ProfileActivity profileActivity = ProfileActivity.this;
                    animatorSet.playTogether(ObjectAnimator.ofFloat(profileActivity, (Property<ProfileActivity, Float>) profileActivity.HEADER_SHADOW, 1.0f));
                    ProfileActivity.this.headerShadowAnimatorSet.setDuration(100L);
                    ProfileActivity.this.headerShadowAnimatorSet.addListener(new AnimatorListenerAdapter() {
                        AnonymousClass1() {
                        }

                        @Override
                        public void onAnimationEnd(Animator animator2) {
                            ProfileActivity.this.headerShadowAnimatorSet = null;
                        }
                    });
                    ProfileActivity.this.headerShadowAnimatorSet.start();
                }
            }
            ProfileActivity.this.updateStoriesViewBounds(false);
            ProfileActivity.this.headerAnimatorSet = null;
        }
    }

    public class AnonymousClass46 extends AnimatorListenerAdapter {
        AnonymousClass46() {
        }

        @Override
        public void onAnimationEnd(Animator animator) {
            if (ProfileActivity.this.writeButtonAnimation == null || !ProfileActivity.this.writeButtonAnimation.equals(animator)) {
                return;
            }
            ProfileActivity.this.writeButtonAnimation = null;
        }
    }

    public class AnonymousClass47 extends AnimatorListenerAdapter {
        AnonymousClass47() {
        }

        @Override
        public void onAnimationEnd(Animator animator) {
            ProfileActivity.this.expandAnimator.removeListener(this);
            ProfileActivity.this.topView.setBackgroundColor(-16777216);
            ProfileActivity.this.avatarContainer.setVisibility(8);
            ProfileActivity.this.avatarsViewPager.setVisibility(0);
        }

        @Override
        public void onAnimationStart(Animator animator) {
            ProfileActivity.this.setForegroundImage(false);
            ProfileActivity.this.avatarsViewPager.setAnimatedFileMaybe(ProfileActivity.this.avatarImage.getImageReceiver().getAnimation());
            ProfileActivity.this.avatarsViewPager.resetCurrentItem();
        }
    }

    public class AnonymousClass48 extends AnimatorListenerAdapter {
        AnonymousClass48() {
        }

        @Override
        public void onAnimationEnd(Animator animator) {
            ProfileActivity.this.qrItemAnimation = null;
        }
    }

    public class AnonymousClass49 implements ViewTreeObserver.OnPreDrawListener {
        AnonymousClass49() {
        }

        @Override
        public boolean onPreDraw() {
            ProfileActivity profileActivity = ProfileActivity.this;
            if (profileActivity.fragmentView != null) {
                profileActivity.checkListViewScroll();
                ProfileActivity.this.needLayout(true);
                ProfileActivity.this.fragmentView.getViewTreeObserver().removeOnPreDrawListener(this);
            }
            return true;
        }
    }

    class AnonymousClass5 extends ActionBar {
        AnonymousClass5(Context context, Theme.ResourcesProvider resourcesProvider) {
            super(context, resourcesProvider);
        }

        @Override
        public void onLayout(boolean z, int i, int i2, int i3, int i4) {
            super.onLayout(z, i, i2, i3, i4);
            ProfileActivity.this.updateStoriesViewBounds(false);
        }

        @Override
        public boolean onTouchEvent(MotionEvent motionEvent) {
            ProfileActivity.this.avatarContainer.getHitRect(ProfileActivity.this.rect);
            if (ProfileActivity.this.rect.contains((int) motionEvent.getX(), (int) motionEvent.getY())) {
                return false;
            }
            return super.onTouchEvent(motionEvent);
        }

        @Override
        public void setItemsColor(int i, boolean z) {
            super.setItemsColor(i, z);
            if (z || ProfileActivity.this.ttlIconView == null) {
                return;
            }
            ProfileActivity.this.ttlIconView.setColorFilter(new PorterDuffColorFilter(i, PorterDuff.Mode.MULTIPLY));
        }
    }

    class AnonymousClass50 extends AnimatorListenerAdapter {
        final Runnable val$callback;

        AnonymousClass50(Runnable runnable) {
            r2 = runnable;
        }

        @Override
        public void onAnimationEnd(Animator animator) {
            ProfileActivity profileActivity = ProfileActivity.this;
            if (profileActivity.fragmentView == null) {
                r2.run();
                return;
            }
            profileActivity.avatarImage.setProgressToExpand(0.0f);
            ProfileActivity.this.listView.setLayerType(0, null);
            if (ProfileActivity.this.animatingItem != null) {
                ((BaseFragment) ProfileActivity.this).actionBar.createMenu().clearItems();
                ProfileActivity.this.animatingItem = null;
            }
            r2.run();
            if (ProfileActivity.this.playProfileAnimation == 2) {
                ProfileActivity.this.playProfileAnimation = 1;
                ProfileActivity.this.avatarImage.setForegroundAlpha(1.0f);
                ProfileActivity.this.avatarContainer.setVisibility(8);
                ProfileActivity.this.avatarsViewPager.resetCurrentItem();
                ProfileActivity.this.avatarsViewPager.setVisibility(0);
            }
            ProfileActivity.this.transitionOnlineText = null;
            ProfileActivity.this.avatarContainer2.invalidate();
            ProfileActivity profileActivity2 = ProfileActivity.this;
            profileActivity2.profileTransitionInProgress = false;
            profileActivity2.previousTransitionFragment = null;
            profileActivity2.previousTransitionMainFragment = null;
            profileActivity2.fragmentView.invalidate();
        }
    }

    public class AnonymousClass51 implements AutoDeletePopupWrapper.Callback {
        AnonymousClass51() {
        }

        @Override
        public void dismiss() {
            ProfileActivity.this.otherItem.toggleSubMenu();
        }

        @Override
        public void setAutoDeleteHistory(int i, int i2) {
            ProfileActivity.this.setAutoDeleteHistory(i, i2);
        }

        @Override
        public void showGlobalAutoDeleteScreen() {
            ProfileActivity.this.presentFragment(new AutoDeleteMessagesActivity());
            dismiss();
        }
    }

    public class AnonymousClass52 extends AnimatorListenerAdapter {
        final boolean val$enter;

        AnonymousClass52(boolean z) {
            r2 = z;
        }

        @Override
        public void onAnimationEnd(Animator animator) {
            ProfileActivity.this.updateSearchViewState(r2);
            ProfileActivity.this.avatarContainer.setClickable(true);
            if (r2) {
                ProfileActivity.this.searchItem.requestFocusOnSearchView();
            }
            ProfileActivity.this.needLayout(true);
            ProfileActivity.this.searchViewTransition = null;
            ProfileActivity.this.fragmentView.invalidate();
            if (r2) {
                ProfileActivity.this.invalidateScroll = true;
                ProfileActivity.this.saveScrollPosition();
                AndroidUtilities.requestAdjustResize(ProfileActivity.this.getParentActivity(), ((BaseFragment) ProfileActivity.this).classGuid);
                ProfileActivity.this.emptyView.setPreventMoving(false);
            }
        }
    }

    public class AnonymousClass53 extends AnimatorListenerAdapter {
        final boolean val$show;

        AnonymousClass53(boolean z) {
            r2 = z;
        }

        @Override
        public void onAnimationCancel(Animator animator) {
            ProfileActivity.this.avatarAnimation = null;
        }

        @Override
        public void onAnimationEnd(Animator animator) {
            if (ProfileActivity.this.avatarAnimation == null || ProfileActivity.this.avatarProgressView == null) {
                return;
            }
            if (!r2) {
                ProfileActivity.this.avatarProgressView.setVisibility(4);
            }
            ProfileActivity.this.avatarAnimation = null;
        }
    }

    public class AnonymousClass54 extends AnimatorListenerAdapter {
        AnonymousClass54() {
        }

        @Override
        public void onAnimationEnd(Animator animator) {
            ProfileActivity.this.scrimView = null;
            ProfileActivity.this.fragmentView.invalidate();
        }
    }

    public class AnonymousClass55 extends LocationActivity {
        AnonymousClass55(int i) {
            super(i);
        }

        @Override
        protected boolean disablePermissionCheck() {
            return true;
        }
    }

    public class AnonymousClass56 extends CombinedDrawable {
        AnonymousClass56(Drawable drawable, Drawable drawable2, int i, int i2) {
            super(drawable, drawable2, i, i2);
        }

        @Override
        public void setColorFilter(ColorFilter colorFilter) {
        }
    }

    public class AnonymousClass6 extends ActionBar.ActionBarMenuOnItemClick {

        class AnonymousClass1 implements DialogInterface.OnClickListener {
            AnonymousClass1() {
            }

            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                ArrayList<Integer> arrayList = new ArrayList<>();
                arrayList.add(Integer.valueOf((int) ProfileActivity.this.topicId));
                ProfileActivity.this.getMessagesController().getTopicsController().deleteTopics(ProfileActivity.this.chatId, arrayList);
                ProfileActivity.this.playProfileAnimation = 0;
                if (((BaseFragment) ProfileActivity.this).parentLayout != null && ((BaseFragment) ProfileActivity.this).parentLayout.getFragmentStack() != null) {
                    for (int i2 = 0; i2 < ((BaseFragment) ProfileActivity.this).parentLayout.getFragmentStack().size(); i2++) {
                        BaseFragment baseFragment = (BaseFragment) ((BaseFragment) ProfileActivity.this).parentLayout.getFragmentStack().get(i2);
                        if ((baseFragment instanceof ChatActivity) && ((ChatActivity) baseFragment).getTopicId() == ProfileActivity.this.topicId) {
                            baseFragment.removeSelfFromStack();
                        }
                    }
                }
                ProfileActivity.this.lambda$onBackPressed$321();
                Context context = ProfileActivity.this.getContext();
                if (context != null) {
                    BulletinFactory.of(Bulletin.BulletinWindow.make(context), ProfileActivity.this.resourcesProvider).createSimpleBulletin(R.raw.ic_delete, LocaleController.getPluralString("TopicsDeleted", 1)).show();
                }
                dialogInterface.dismiss();
            }
        }

        class AnonymousClass2 implements DialogInterface.OnClickListener {
            AnonymousClass2() {
            }

            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        }

        public class AnonymousClass3 implements ChatRightsEditActivity.ChatRightsEditActivityDelegate {
            final DialogsActivity val$fragment;

            AnonymousClass3(DialogsActivity dialogsActivity) {
                r2 = dialogsActivity;
            }

            @Override
            public void didChangeOwner(TLRPC.User user) {
            }

            @Override
            public void didSetRights(int i, TLRPC.TL_chatAdminRights tL_chatAdminRights, TLRPC.TL_chatBannedRights tL_chatBannedRights, String str) {
                ProfileActivity.this.disableProfileAnimation = true;
                r2.removeSelfFromStack();
                NotificationCenter notificationCenter = ProfileActivity.this.getNotificationCenter();
                ProfileActivity profileActivity = ProfileActivity.this;
                int i2 = NotificationCenter.closeChats;
                notificationCenter.removeObserver(profileActivity, i2);
                ProfileActivity.this.getNotificationCenter().lambda$postNotificationNameOnUIThread$1(i2, new Object[0]);
            }
        }

        AnonymousClass6() {
        }

        public void lambda$onItemClick$0(int i) {
            if (i != 1) {
                ProfileActivity.this.getNotificationCenter().lambda$postNotificationNameOnUIThread$1(NotificationCenter.peerSettingsDidLoad, Long.valueOf(ProfileActivity.this.userId));
                return;
            }
            NotificationCenter notificationCenter = ProfileActivity.this.getNotificationCenter();
            ProfileActivity profileActivity = ProfileActivity.this;
            int i2 = NotificationCenter.closeChats;
            notificationCenter.removeObserver(profileActivity, i2);
            ProfileActivity.this.getNotificationCenter().lambda$postNotificationNameOnUIThread$1(i2, new Object[0]);
            ProfileActivity.this.playProfileAnimation = 0;
            ProfileActivity.this.lambda$onBackPressed$321();
        }

        public void lambda$onItemClick$1(DialogInterface dialogInterface, int i) {
            ProfileActivity.this.getMessagesController().blockPeer(ProfileActivity.this.userId);
            if (BulletinFactory.canShowBulletin(ProfileActivity.this)) {
                BulletinFactory.createBanBulletin(ProfileActivity.this, true).show();
            }
        }

        public void lambda$onItemClick$10(boolean z, Uri uri) {
            if (ProfileActivity.this.getParentActivity() == null) {
                return;
            }
            BulletinFactory.createSaveToGalleryBulletin(ProfileActivity.this, z, (Theme.ResourcesProvider) null).show();
        }

        public void lambda$onItemClick$11(TLObject tLObject, UserConfig userConfig, TLRPC.Photo photo) {
            ProfileActivity.this.avatarsViewPager.finishSettingMainPhoto();
            if (tLObject instanceof TLRPC.TL_photos_photo) {
                TLRPC.TL_photos_photo tL_photos_photo = (TLRPC.TL_photos_photo) tLObject;
                ProfileActivity.this.getMessagesController().putUsers(tL_photos_photo.users, false);
                TLRPC.User user = ProfileActivity.this.getMessagesController().getUser(Long.valueOf(userConfig.clientUserId));
                if (tL_photos_photo.photo instanceof TLRPC.TL_photo) {
                    ProfileActivity.this.avatarsViewPager.replaceFirstPhoto(photo, tL_photos_photo.photo);
                    if (user != null) {
                        user.photo.photo_id = tL_photos_photo.photo.id;
                        userConfig.setCurrentUser(user);
                        userConfig.saveConfig(true);
                    }
                }
            }
        }

        public void lambda$onItemClick$12(final UserConfig userConfig, final TLRPC.Photo photo, final TLObject tLObject, TLRPC.TL_error tL_error) {
            AndroidUtilities.runOnUIThread(new Runnable() {
                @Override
                public final void run() {
                    ProfileActivity.AnonymousClass6.this.lambda$onItemClick$11(tLObject, userConfig, photo);
                }
            });
        }

        public void lambda$onItemClick$13(DialogInterface dialogInterface, int i) {
            TLRPC.Photo photo;
            int realPosition = ProfileActivity.this.avatarsViewPager.getRealPosition();
            TLRPC.Photo photo2 = ProfileActivity.this.avatarsViewPager.getPhoto(realPosition);
            TLRPC.UserFull userInfo = ProfileActivity.this.getUserInfo();
            if (ProfileActivity.this.avatar != null && realPosition == 0) {
                ProfileActivity.this.imageUpdater.cancel();
                ProfileActivity profileActivity = ProfileActivity.this;
                if (profileActivity.avatarUploadingRequest != 0) {
                    profileActivity.getConnectionsManager().cancelRequest(ProfileActivity.this.avatarUploadingRequest, true);
                }
                ProfileActivity.this.allowPullingDown = (AndroidUtilities.isTablet() || ProfileActivity.this.isInLandscapeMode || !ProfileActivity.this.avatarImage.getImageReceiver().hasNotThumb() || AndroidUtilities.isAccessibilityScreenReaderEnabled()) ? false : true;
                ProfileActivity.this.avatar = null;
                ProfileActivity.this.avatarBig = null;
                ProfileActivity.this.avatarsViewPager.scrolledByUser = true;
                ProfileActivity.this.avatarsViewPager.removeUploadingImage(ProfileActivity.this.uploadingImageLocation);
                ProfileActivity.this.avatarsViewPager.setCreateThumbFromParent(false);
                ProfileActivity.this.updateProfileData(true);
                ProfileActivity.this.showAvatarProgress(false, true);
                ProfileActivity.this.getNotificationCenter().lambda$postNotificationNameOnUIThread$1(NotificationCenter.updateInterfaces, Integer.valueOf(MessagesController.UPDATE_MASK_ALL));
                ProfileActivity.this.getNotificationCenter().lambda$postNotificationNameOnUIThread$1(NotificationCenter.mainUserInfoChanged, new Object[0]);
                ProfileActivity.this.getUserConfig().saveConfig(true);
                return;
            }
            if (ProfileActivity.this.hasFallbackPhoto && photo2 != null && userInfo != null && (photo = userInfo.fallback_photo) != null && photo.id == photo2.id) {
                userInfo.fallback_photo = null;
                userInfo.flags &= -4194305;
                ProfileActivity.this.getMessagesStorage().updateUserInfo(userInfo, true);
                ProfileActivity.this.updateProfileData(false);
            }
            if (ProfileActivity.this.avatarsViewPager.getRealCount() == 1) {
                ProfileActivity.this.setForegroundImage(true);
            }
            if (photo2 == null || ProfileActivity.this.avatarsViewPager.getRealPosition() == 0) {
                TLRPC.Photo photo3 = ProfileActivity.this.avatarsViewPager.getPhoto(1);
                if (photo3 != null) {
                    ProfileActivity.this.getUserConfig().getCurrentUser().photo = new TLRPC.TL_userProfilePhoto();
                    TLRPC.PhotoSize closestPhotoSizeWithSize = FileLoader.getClosestPhotoSizeWithSize(photo3.sizes, 90);
                    TLRPC.PhotoSize closestPhotoSizeWithSize2 = FileLoader.getClosestPhotoSizeWithSize(photo3.sizes, 1000);
                    if (closestPhotoSizeWithSize != null && closestPhotoSizeWithSize2 != null) {
                        ProfileActivity.this.getUserConfig().getCurrentUser().photo.photo_small = closestPhotoSizeWithSize.location;
                        ProfileActivity.this.getUserConfig().getCurrentUser().photo.photo_big = closestPhotoSizeWithSize2.location;
                    }
                } else {
                    ProfileActivity.this.getUserConfig().getCurrentUser().photo = new TLRPC.TL_userProfilePhotoEmpty();
                }
                ProfileActivity.this.getMessagesController().deleteUserPhoto(null);
            } else {
                TLRPC.TL_inputPhoto tL_inputPhoto = new TLRPC.TL_inputPhoto();
                tL_inputPhoto.id = photo2.id;
                tL_inputPhoto.access_hash = photo2.access_hash;
                byte[] bArr = photo2.file_reference;
                tL_inputPhoto.file_reference = bArr;
                if (bArr == null) {
                    tL_inputPhoto.file_reference = new byte[0];
                }
                ProfileActivity.this.getMessagesController().deleteUserPhoto(tL_inputPhoto);
                ProfileActivity.this.getMessagesStorage().clearUserPhoto(ProfileActivity.this.userId, photo2.id);
            }
            if (ProfileActivity.this.avatarsViewPager.removePhotoAtIndex(realPosition) || ProfileActivity.this.avatarsViewPager.getRealCount() <= 0) {
                ProfileActivity.this.avatarsViewPager.setVisibility(8);
                ProfileActivity.this.avatarImage.setForegroundAlpha(1.0f);
                ProfileActivity.this.avatarContainer.setVisibility(0);
                ProfileActivity.this.doNotSetForeground = true;
                View findViewByPosition = ProfileActivity.this.layoutManager.findViewByPosition(0);
                if (findViewByPosition != null) {
                    ProfileActivity.this.listView.smoothScrollBy(0, findViewByPosition.getTop() - AndroidUtilities.dp(88.0f), CubicBezierInterpolator.EASE_OUT_QUINT);
                }
            }
        }

        public void lambda$onItemClick$2(TLRPC.User user, boolean z) {
            if (ProfileActivity.this.getParentLayout() != null) {
                List fragmentStack = ProfileActivity.this.getParentLayout().getFragmentStack();
                if (((fragmentStack == null || fragmentStack.size() < 2) ? null : (BaseFragment) fragmentStack.get(fragmentStack.size() - 2)) instanceof ChatActivity) {
                    ProfileActivity.this.getParentLayout().removeFragmentFromStack(fragmentStack.size() - 2);
                }
            }
            ProfileActivity.this.lambda$onBackPressed$321();
            ProfileActivity.this.getNotificationCenter().lambda$postNotificationNameOnUIThread$1(NotificationCenter.needDeleteDialog, Long.valueOf(ProfileActivity.this.dialogId), user, ProfileActivity.this.currentChat, Boolean.valueOf(z));
        }

        public void lambda$onItemClick$3() {
            ProfileActivity.this.getSendMessagesHelper().sendMessage(SendMessagesHelper.SendMessageParams.of("/start", ProfileActivity.this.userId, null, null, null, false, null, null, null, true, 0, null, false));
        }

        public void lambda$onItemClick$4(TLRPC.User user, DialogInterface dialogInterface, int i) {
            ArrayList<TLRPC.User> arrayList = new ArrayList<>();
            arrayList.add(user);
            ProfileActivity.this.getContactsController().deleteContact(arrayList, true);
            if (user != null) {
                user.contact = false;
                ProfileActivity.this.updateListAnimated(false);
            }
        }

        public void lambda$onItemClick$5(long j, TLRPC.TL_chatAdminRights tL_chatAdminRights, String str, boolean z, DialogsActivity dialogsActivity) {
            ChatRightsEditActivity chatRightsEditActivity = new ChatRightsEditActivity(ProfileActivity.this.userId, -j, tL_chatAdminRights, null, null, str, 2, true, !z, null);
            chatRightsEditActivity.setDelegate(new ChatRightsEditActivity.ChatRightsEditActivityDelegate() {
                final DialogsActivity val$fragment;

                AnonymousClass3(DialogsActivity dialogsActivity2) {
                    r2 = dialogsActivity2;
                }

                @Override
                public void didChangeOwner(TLRPC.User user) {
                }

                @Override
                public void didSetRights(int i, TLRPC.TL_chatAdminRights tL_chatAdminRights2, TLRPC.TL_chatBannedRights tL_chatBannedRights, String str2) {
                    ProfileActivity.this.disableProfileAnimation = true;
                    r2.removeSelfFromStack();
                    NotificationCenter notificationCenter = ProfileActivity.this.getNotificationCenter();
                    ProfileActivity profileActivity = ProfileActivity.this;
                    int i2 = NotificationCenter.closeChats;
                    notificationCenter.removeObserver(profileActivity, i2);
                    ProfileActivity.this.getNotificationCenter().lambda$postNotificationNameOnUIThread$1(i2, new Object[0]);
                }
            });
            ProfileActivity.this.presentFragment(chatRightsEditActivity);
        }

        public void lambda$onItemClick$6(final long j, final DialogsActivity dialogsActivity, final boolean z, final TLRPC.TL_chatAdminRights tL_chatAdminRights, final String str) {
            AndroidUtilities.runOnUIThread(new Runnable() {
                @Override
                public final void run() {
                    ProfileActivity.AnonymousClass6.this.lambda$onItemClick$5(j, tL_chatAdminRights, str, z, dialogsActivity);
                }
            });
        }

        public void lambda$onItemClick$7(long j, DialogsActivity dialogsActivity, TLRPC.User user, DialogInterface dialogInterface, int i) {
            ProfileActivity.this.disableProfileAnimation = true;
            Bundle bundle = new Bundle();
            bundle.putBoolean("scrollToTopOnResume", true);
            long j2 = -j;
            bundle.putLong("chat_id", j2);
            if (ProfileActivity.this.getMessagesController().checkCanOpenChat(bundle, dialogsActivity)) {
                ChatActivity chatActivity = new ChatActivity(bundle);
                NotificationCenter notificationCenter = ProfileActivity.this.getNotificationCenter();
                ProfileActivity profileActivity = ProfileActivity.this;
                int i2 = NotificationCenter.closeChats;
                notificationCenter.removeObserver(profileActivity, i2);
                ProfileActivity.this.getNotificationCenter().lambda$postNotificationNameOnUIThread$1(i2, new Object[0]);
                ProfileActivity.this.getMessagesController().addUserToChat(j2, user, 0, null, chatActivity, true, null, null);
                ProfileActivity.this.presentFragment(chatActivity, true);
            }
        }

        public boolean lambda$onItemClick$8(final TLRPC.User user, final DialogsActivity dialogsActivity, final DialogsActivity dialogsActivity2, ArrayList arrayList, CharSequence charSequence, boolean z, boolean z2, int i, TopicsFragment topicsFragment) {
            TLRPC.TL_chatAdminRights tL_chatAdminRights;
            final long j = ((MessagesStorage.TopicKey) arrayList.get(0)).dialogId;
            TLRPC.Chat chat = MessagesController.getInstance(((BaseFragment) ProfileActivity.this).currentAccount).getChat(Long.valueOf(-j));
            if (chat == null || (!chat.creator && ((tL_chatAdminRights = chat.admin_rights) == null || !tL_chatAdminRights.add_admins))) {
                AlertDialog.Builder builder = new AlertDialog.Builder(ProfileActivity.this.getParentActivity(), ProfileActivity.this.resourcesProvider);
                int i2 = R.string.AddBot;
                builder.setTitle(LocaleController.getString(i2));
                builder.setMessage(AndroidUtilities.replaceTags(LocaleController.formatString("AddMembersAlertNamesText", R.string.AddMembersAlertNamesText, UserObject.getUserName(user), chat == null ? "" : chat.title)));
                builder.setNegativeButton(LocaleController.getString(R.string.Cancel), null);
                builder.setPositiveButton(LocaleController.getString(i2), new DialogInterface.OnClickListener() {
                    @Override
                    public final void onClick(DialogInterface dialogInterface, int i3) {
                        ProfileActivity.AnonymousClass6.this.lambda$onItemClick$7(j, dialogsActivity2, user, dialogInterface, i3);
                    }
                });
                ProfileActivity.this.showDialog(builder.create());
            } else {
                ProfileActivity.this.getMessagesController().checkIsInChat(false, chat, user, new MessagesController.IsInChatCheckedCallback() {
                    @Override
                    public final void run(boolean z3, TLRPC.TL_chatAdminRights tL_chatAdminRights2, String str) {
                        ProfileActivity.AnonymousClass6.this.lambda$onItemClick$6(j, dialogsActivity, z3, tL_chatAdminRights2, str);
                    }
                });
            }
            return true;
        }

        public void lambda$onItemClick$9(DialogInterface dialogInterface, int i) {
            ProfileActivity.this.creatingChat = true;
            ProfileActivity.this.getSecretChatHelper().startSecretChat(ProfileActivity.this.getParentActivity(), ProfileActivity.this.getMessagesController().getUser(Long.valueOf(ProfileActivity.this.userId)));
        }

        @Override
        public void onItemClick(int r18) {
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.ProfileActivity.AnonymousClass6.onItemClick(int):void");
        }
    }

    public class AnonymousClass7 extends NestedFrameLayout {
        private Paint grayPaint;
        private boolean ignoreLayout;
        private final ArrayList sortedChildren;
        private final Comparator viewComparator;
        private boolean wasPortrait;

        AnonymousClass7(Context context) {
            super(context);
            this.grayPaint = new Paint();
            this.sortedChildren = new ArrayList();
            this.viewComparator = new Comparator() {
                @Override
                public final int compare(Object obj, Object obj2) {
                    int lambda$$1;
                    lambda$$1 = ProfileActivity.AnonymousClass7.lambda$$1((View) obj, (View) obj2);
                    return lambda$$1;
                }
            };
        }

        public static int lambda$$1(View view, View view2) {
            return (int) (view.getY() - view2.getY());
        }

        public void lambda$onMeasure$0() {
            if (ProfileActivity.this.selectAnimatedEmojiDialog != null) {
                ProfileActivity.this.selectAnimatedEmojiDialog.dismiss();
                ProfileActivity.this.selectAnimatedEmojiDialog = null;
            }
        }

        @Override
        public void dispatchDraw(android.graphics.Canvas r21) {
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.ProfileActivity.AnonymousClass7.dispatchDraw(android.graphics.Canvas):void");
        }

        @Override
        public boolean dispatchTouchEvent(MotionEvent motionEvent) {
            if (ProfileActivity.this.pinchToZoomHelper.isInOverlayMode()) {
                return ProfileActivity.this.pinchToZoomHelper.onTouchEvent(motionEvent);
            }
            SharedMediaLayout sharedMediaLayout = ProfileActivity.this.sharedMediaLayout;
            if (sharedMediaLayout != null && sharedMediaLayout.isInFastScroll() && ProfileActivity.this.sharedMediaLayout.isPinnedToTop()) {
                return ProfileActivity.this.sharedMediaLayout.dispatchFastScrollEvent(motionEvent);
            }
            SharedMediaLayout sharedMediaLayout2 = ProfileActivity.this.sharedMediaLayout;
            if (sharedMediaLayout2 == null || !sharedMediaLayout2.checkPinchToZoom(motionEvent)) {
                return super.dispatchTouchEvent(motionEvent);
            }
            return true;
        }

        @Override
        protected boolean drawChild(Canvas canvas, View view, long j) {
            if ((ProfileActivity.this.pinchToZoomHelper.isInOverlayMode() && (view == ProfileActivity.this.avatarContainer2 || view == ((BaseFragment) ProfileActivity.this).actionBar || view == ProfileActivity.this.writeButton)) || view == ProfileActivity.this.blurredView) {
                return true;
            }
            return super.drawChild(canvas, view, j);
        }

        @Override
        public boolean hasOverlappingRendering() {
            return false;
        }

        @Override
        public void onAttachedToWindow() {
            super.onAttachedToWindow();
            ProfileActivity.this.fragmentViewAttached = true;
            for (int i = 0; i < ProfileActivity.this.emojiStatusDrawable.length; i++) {
                if (ProfileActivity.this.emojiStatusDrawable[i] != null) {
                    ProfileActivity.this.emojiStatusDrawable[i].attach();
                }
            }
            for (int i2 = 0; i2 < ProfileActivity.this.botVerificationDrawable.length; i2++) {
                if (ProfileActivity.this.botVerificationDrawable[i2] != null) {
                    ProfileActivity.this.botVerificationDrawable[i2].attach();
                }
            }
        }

        @Override
        public void onDetachedFromWindow() {
            super.onDetachedFromWindow();
            ProfileActivity.this.fragmentViewAttached = false;
            for (int i = 0; i < ProfileActivity.this.emojiStatusDrawable.length; i++) {
                if (ProfileActivity.this.emojiStatusDrawable[i] != null) {
                    ProfileActivity.this.emojiStatusDrawable[i].detach();
                }
            }
            for (int i2 = 0; i2 < ProfileActivity.this.botVerificationDrawable.length; i2++) {
                if (ProfileActivity.this.botVerificationDrawable[i2] != null) {
                    ProfileActivity.this.botVerificationDrawable[i2].detach();
                }
            }
        }

        @Override
        public void onLayout(boolean z, int i, int i2, int i3, int i4) {
            super.onLayout(z, i, i2, i3, i4);
            ProfileActivity profileActivity = ProfileActivity.this;
            profileActivity.savedScrollPosition = -1;
            profileActivity.firstLayout = false;
            ProfileActivity.this.invalidateScroll = false;
            ProfileActivity.this.checkListViewScroll();
        }

        @Override
        protected void onMeasure(int r18, int r19) {
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.ProfileActivity.AnonymousClass7.onMeasure(int, int):void");
        }

        @Override
        public void requestLayout() {
            if (this.ignoreLayout) {
                return;
            }
            super.requestLayout();
        }
    }

    public class AnonymousClass8 implements StoryRecorder.ClosingViewProvider {
        AnonymousClass8() {
        }

        @Override
        public StoryRecorder.SourceView getView(long j) {
            if (j != ProfileActivity.this.getDialogId()) {
                return null;
            }
            ProfileActivity.this.updateAvatarRoundRadius();
            return StoryRecorder.SourceView.fromAvatarImage(ProfileActivity.this.avatarImage, ChatObject.isForum(ProfileActivity.this.currentChat));
        }

        @Override
        public void preLayout(long j, Runnable runnable) {
            ProfileActivity.this.avatarImage.setHasStories(ProfileActivity.this.needInsetForStories());
            if (j == ProfileActivity.this.getDialogId()) {
                ProfileActivity.this.collapseAvatarInstant();
            }
            AndroidUtilities.runOnUIThread(runnable, 30L);
        }
    }

    public class AnonymousClass9 extends SharedMediaLayout {

        class AnonymousClass1 implements StoryRecorder.ClosingViewProvider {
            AnonymousClass1() {
            }

            @Override
            public StoryRecorder.SourceView getView(long j) {
                if (j != ProfileActivity.this.getDialogId()) {
                    return null;
                }
                ProfileActivity.this.updateAvatarRoundRadius();
                return StoryRecorder.SourceView.fromAvatarImage(ProfileActivity.this.avatarImage, ChatObject.isForum(ProfileActivity.this.currentChat));
            }

            @Override
            public void preLayout(long j, Runnable runnable) {
                ProfileActivity.this.avatarImage.setHasStories(ProfileActivity.this.needInsetForStories());
                if (j == ProfileActivity.this.getDialogId()) {
                    ProfileActivity.this.collapseAvatarInstant();
                }
                AndroidUtilities.runOnUIThread(runnable, 30L);
            }
        }

        AnonymousClass9(Context context, long j, SharedMediaLayout.SharedMediaPreloader sharedMediaPreloader, int i, ArrayList arrayList, TLRPC.ChatFull chatFull, TLRPC.UserFull userFull, int i2, BaseFragment baseFragment, SharedMediaLayout.Delegate delegate, int i3, Theme.ResourcesProvider resourcesProvider) {
            super(context, j, sharedMediaPreloader, i, arrayList, chatFull, userFull, i2, baseFragment, delegate, i3, resourcesProvider);
        }

        public void lambda$showActionMode$0(ValueAnimator valueAnimator) {
            ProfileActivity.this.updateBottomButtonY();
        }

        @Override
        protected void drawBackgroundWithBlur(Canvas canvas, float f, Rect rect, Paint paint) {
            ProfileActivity.this.contentView.drawBlurRect(canvas, ProfileActivity.this.listView.getY() + getY() + f, rect, paint, true);
        }

        @Override
        protected int getInitialTab() {
            return 8;
        }

        @Override
        protected boolean includeSavedDialogs() {
            return ProfileActivity.this.dialogId == ProfileActivity.this.getUserConfig().getClientUserId() && !ProfileActivity.this.saved;
        }

        @Override
        protected void invalidateBlur() {
            if (ProfileActivity.this.contentView != null) {
                ProfileActivity.this.contentView.invalidateBlur();
            }
        }

        @Override
        protected boolean isSelf() {
            return ProfileActivity.this.myProfile;
        }

        @Override
        protected boolean isStoriesView() {
            return ProfileActivity.this.myProfile;
        }

        @Override
        public void onActionModeSelectedUpdate(SparseArray sparseArray) {
            super.onActionModeSelectedUpdate(sparseArray);
            if (ProfileActivity.this.myProfile) {
                int size = sparseArray.size();
                int selectedTab = getSelectedTab() - 8;
                if (selectedTab < 0 || selectedTab > 1) {
                    return;
                }
                if (selectedTab == 0) {
                    ProfileActivity.this.bottomButton[selectedTab].setText((size > 0 || !MessagesController.getInstance(((BaseFragment) ProfileActivity.this).currentAccount).storiesEnabled()) ? LocaleController.formatPluralString("ArchiveStories", size, new Object[0]) : ProfileActivity.this.bottomButtonPostText, true);
                }
                ProfileActivity.this.bottomButton[selectedTab].setCount(size, true);
            }
        }

        @Override
        protected boolean onMemberClick(TLRPC.ChatParticipant chatParticipant, boolean z, View view) {
            return ProfileActivity.this.onMemberClick(chatParticipant, z, view);
        }

        @Override
        protected void onSearchStateChanged(boolean z) {
            AndroidUtilities.removeAdjustResize(ProfileActivity.this.getParentActivity(), ((BaseFragment) ProfileActivity.this).classGuid);
            ProfileActivity.this.listView.stopScroll();
            ProfileActivity.this.avatarContainer2.setPivotY(ProfileActivity.this.avatarContainer.getPivotY() + (ProfileActivity.this.avatarContainer.getMeasuredHeight() / 2.0f));
            ProfileActivity.this.avatarContainer2.setPivotX(ProfileActivity.this.avatarContainer2.getMeasuredWidth() / 2.0f);
            AndroidUtilities.updateViewVisibilityAnimated(ProfileActivity.this.avatarContainer2, !z, 0.95f, true);
            ProfileActivity.this.callItem.setVisibility((z || !ProfileActivity.this.callItemVisible) ? 8 : 4);
            ProfileActivity.this.videoCallItem.setVisibility((z || !ProfileActivity.this.videoCallItemVisible) ? 8 : 4);
            ProfileActivity.this.editItem.setVisibility((z || !ProfileActivity.this.editItemVisible) ? 8 : 4);
            ProfileActivity.this.otherItem.setVisibility(z ? 8 : 4);
            if (ProfileActivity.this.qrItem != null) {
                ProfileActivity.this.qrItem.setVisibility(z ? 8 : 4);
            }
            ProfileActivity.this.updateStoriesViewBounds(false);
        }

        @Override
        public void onSelectedTabChanged() {
            ProfileActivity.this.updateSelectedMediaTabText();
        }

        @Override
        public void onTabProgress(float f) {
            super.onTabProgress(f);
            ProfileActivity profileActivity = ProfileActivity.this;
            if (profileActivity.myProfile) {
                SharedMediaLayout sharedMediaLayout = profileActivity.sharedMediaLayout;
                int measuredWidth = sharedMediaLayout == null ? AndroidUtilities.displaySize.x : sharedMediaLayout.getMeasuredWidth();
                if (ProfileActivity.this.bottomButtonContainer[0] != null) {
                    ProfileActivity.this.bottomButtonContainer[0].setTranslationX((8.0f - f) * measuredWidth);
                }
                if (ProfileActivity.this.bottomButtonContainer[1] != null) {
                    ProfileActivity.this.bottomButtonContainer[1].setTranslationX((9.0f - f) * measuredWidth);
                }
                ProfileActivity.this.updateBottomButtonY();
            }
        }

        @Override
        public void openStoryRecorder() {
            StoryRecorder.getInstance(ProfileActivity.this.getParentActivity(), ((BaseFragment) ProfileActivity.this).currentAccount).selectedPeerId(ProfileActivity.this.getDialogId()).canChangePeer(false).closeToWhenSent(new StoryRecorder.ClosingViewProvider() {
                AnonymousClass1() {
                }

                @Override
                public StoryRecorder.SourceView getView(long j) {
                    if (j != ProfileActivity.this.getDialogId()) {
                        return null;
                    }
                    ProfileActivity.this.updateAvatarRoundRadius();
                    return StoryRecorder.SourceView.fromAvatarImage(ProfileActivity.this.avatarImage, ChatObject.isForum(ProfileActivity.this.currentChat));
                }

                @Override
                public void preLayout(long j, Runnable runnable) {
                    ProfileActivity.this.avatarImage.setHasStories(ProfileActivity.this.needInsetForStories());
                    if (j == ProfileActivity.this.getDialogId()) {
                        ProfileActivity.this.collapseAvatarInstant();
                    }
                    AndroidUtilities.runOnUIThread(runnable, 30L);
                }
            }).open(StoryRecorder.SourceView.fromFloatingButton(ProfileActivity.this.floatingButtonContainer), true);
        }

        @Override
        protected int processColor(int i) {
            return ProfileActivity.this.applyPeerColor(i, false);
        }

        @Override
        public void showActionMode(boolean z) {
            super.showActionMode(z);
            if (ProfileActivity.this.myProfile) {
                disableScroll(z);
                int selectedTab = getSelectedTab() - 8;
                if (selectedTab < 0 || selectedTab > 1) {
                    return;
                }
                ProfileActivity.this.bottomButtonContainer[selectedTab].animate().translationY((z || (selectedTab == 0 && MessagesController.getInstance(((BaseFragment) ProfileActivity.this).currentAccount).storiesEnabled())) ? 0.0f : AndroidUtilities.dp(72.0f)).setDuration(320L).setInterpolator(CubicBezierInterpolator.EASE_OUT_QUINT).setUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                        ProfileActivity.AnonymousClass9.this.lambda$showActionMode$0(valueAnimator);
                    }
                }).start();
            }
        }
    }

    public static class AvatarImageView extends BackupImageView {
        ProfileGalleryView avatarsViewPager;
        public float bounceScale;
        public boolean drawAvatar;
        boolean drawForeground;
        private ImageReceiver.BitmapHolder drawableHolder;
        private float foregroundAlpha;
        private ImageReceiver foregroundImageReceiver;
        private boolean hasStories;
        private final Paint placeholderPaint;
        float progressToExpand;
        private float progressToInsets;
        private final RectF rect;

        public AvatarImageView(Context context) {
            super(context);
            this.rect = new RectF();
            this.drawAvatar = true;
            this.bounceScale = 1.0f;
            this.drawForeground = true;
            this.progressToInsets = 1.0f;
            this.foregroundImageReceiver = new ImageReceiver(this);
            Paint paint = new Paint(1);
            this.placeholderPaint = paint;
            paint.setColor(-16777216);
        }

        public void clearForeground() {
            AnimatedFileDrawable animation = this.foregroundImageReceiver.getAnimation();
            if (animation != null) {
                animation.removeSecondParentView(this);
            }
            this.foregroundImageReceiver.clearImage();
            ImageReceiver.BitmapHolder bitmapHolder = this.drawableHolder;
            if (bitmapHolder != null) {
                bitmapHolder.release();
                this.drawableHolder = null;
            }
            this.foregroundAlpha = 0.0f;
            invalidate();
        }

        public void drawForeground(boolean z) {
            this.drawForeground = z;
        }

        public float getForegroundAlpha() {
            return this.foregroundAlpha;
        }

        public ChatActivityInterface getPrevFragment() {
            return null;
        }

        @Override
        public void invalidate() {
            super.invalidate();
            ProfileGalleryView profileGalleryView = this.avatarsViewPager;
            if (profileGalleryView != null) {
                profileGalleryView.invalidate();
            }
        }

        @Override
        public void onAttachedToWindow() {
            super.onAttachedToWindow();
            this.foregroundImageReceiver.onAttachedToWindow();
        }

        @Override
        public void onDetachedFromWindow() {
            super.onDetachedFromWindow();
            this.foregroundImageReceiver.onDetachedFromWindow();
            ImageReceiver.BitmapHolder bitmapHolder = this.drawableHolder;
            if (bitmapHolder != null) {
                bitmapHolder.release();
                this.drawableHolder = null;
            }
        }

        @Override
        public void onDraw(Canvas canvas) {
            AnimatedEmojiDrawable animatedEmojiDrawable = this.animatedEmojiDrawable;
            ImageReceiver imageReceiver = animatedEmojiDrawable != null ? animatedEmojiDrawable.getImageReceiver() : this.imageReceiver;
            canvas.save();
            float f = this.bounceScale;
            canvas.scale(f, f, getMeasuredWidth() / 2.0f, getMeasuredHeight() / 2.0f);
            float dpf2 = (this.hasStories ? (int) AndroidUtilities.dpf2(3.5f) : 0.0f) * (1.0f - this.progressToExpand);
            float f2 = this.progressToInsets;
            float f3 = this.foregroundAlpha;
            float f4 = dpf2 * f2 * (1.0f - f3);
            if (imageReceiver != null && (f3 < 1.0f || !this.drawForeground)) {
                float f5 = f4 * 2.0f;
                imageReceiver.setImageCoords(f4, f4, getMeasuredWidth() - f5, getMeasuredHeight() - f5);
                if (this.drawAvatar) {
                    imageReceiver.draw(canvas);
                }
            }
            if (this.foregroundAlpha > 0.0f && this.drawForeground) {
                if (this.foregroundImageReceiver.getDrawable() != null) {
                    float f6 = 2.0f * f4;
                    this.foregroundImageReceiver.setImageCoords(f4, f4, getMeasuredWidth() - f6, getMeasuredHeight() - f6);
                    this.foregroundImageReceiver.setAlpha(this.foregroundAlpha);
                    this.foregroundImageReceiver.draw(canvas);
                } else {
                    this.rect.set(0.0f, 0.0f, getMeasuredWidth(), getMeasuredHeight());
                    this.placeholderPaint.setAlpha((int) (this.foregroundAlpha * 255.0f));
                    float f7 = this.foregroundImageReceiver.getRoundRadius()[0];
                    canvas.drawRoundRect(this.rect, f7, f7, this.placeholderPaint);
                }
            }
            canvas.restore();
        }

        public void setAvatarsViewPager(ProfileGalleryView profileGalleryView) {
            this.avatarsViewPager = profileGalleryView;
        }

        public void setForegroundAlpha(float f) {
            this.foregroundAlpha = f;
            invalidate();
        }

        public void setForegroundImage(ImageLocation imageLocation, String str, Drawable drawable) {
            this.foregroundImageReceiver.setImage(imageLocation, str, drawable, 0L, (String) null, (Object) null, 0);
            ImageReceiver.BitmapHolder bitmapHolder = this.drawableHolder;
            if (bitmapHolder != null) {
                bitmapHolder.release();
                this.drawableHolder = null;
            }
        }

        public void setForegroundImageDrawable(ImageReceiver.BitmapHolder bitmapHolder) {
            if (bitmapHolder != null) {
                this.foregroundImageReceiver.setImageBitmap(bitmapHolder.drawable);
            }
            ImageReceiver.BitmapHolder bitmapHolder2 = this.drawableHolder;
            if (bitmapHolder2 != null) {
                bitmapHolder2.release();
                this.drawableHolder = null;
            }
            this.drawableHolder = bitmapHolder;
        }

        public void setHasStories(boolean z) {
            if (this.hasStories == z) {
                return;
            }
            this.hasStories = z;
            invalidate();
        }

        public void setProgressToExpand(float f) {
            if (this.progressToExpand == f) {
                return;
            }
            this.progressToExpand = f;
            invalidate();
        }

        public void setProgressToStoriesInsets(float f) {
            if (f == this.progressToInsets) {
                return;
            }
            this.progressToInsets = f;
            invalidate();
        }

        @Override
        public void setRoundRadius(int i) {
            super.setRoundRadius(i);
            this.foregroundImageReceiver.setRoundRadius(i);
        }
    }

    public class ClippedListView extends RecyclerListView implements StoriesListPlaceProvider.ClippedView {
        public ClippedListView(Context context) {
            super(context);
        }

        @Override
        public void updateClip(int[] iArr) {
            iArr[0] = ((BaseFragment) ProfileActivity.this).actionBar.getMeasuredHeight();
            iArr[1] = getMeasuredHeight() - getPaddingBottom();
        }
    }

    public class DiffCallback extends DiffUtil.Callback {
        SparseIntArray newPositionToItem;
        ArrayList oldChatParticipant;
        ArrayList oldChatParticipantSorted;
        int oldMembersEndRow;
        int oldMembersStartRow;
        SparseIntArray oldPositionToItem;
        int oldRowCount;

        private DiffCallback() {
            this.oldPositionToItem = new SparseIntArray();
            this.newPositionToItem = new SparseIntArray();
            this.oldChatParticipant = new ArrayList();
            this.oldChatParticipantSorted = new ArrayList();
        }

        DiffCallback(ProfileActivity profileActivity, AnonymousClass1 anonymousClass1) {
            this();
        }

        private void put(int i, int i2, SparseIntArray sparseIntArray) {
            if (i2 >= 0) {
                sparseIntArray.put(i2, i);
            }
        }

        @Override
        public boolean areContentsTheSame(int i, int i2) {
            return areItemsTheSame(i, i2);
        }

        @Override
        public boolean areItemsTheSame(int i, int i2) {
            ArrayList arrayList;
            int i3;
            ArrayList arrayList2;
            int i4;
            if (i2 < ProfileActivity.this.membersStartRow || i2 >= ProfileActivity.this.membersEndRow || i < this.oldMembersStartRow || i >= this.oldMembersEndRow) {
                int i5 = this.oldPositionToItem.get(i, -1);
                return i5 == this.newPositionToItem.get(i2, -1) && i5 >= 0;
            }
            if (this.oldChatParticipantSorted.isEmpty()) {
                arrayList = this.oldChatParticipant;
                i3 = i - this.oldMembersStartRow;
            } else {
                arrayList = this.oldChatParticipant;
                i3 = ((Integer) this.oldChatParticipantSorted.get(i - this.oldMembersStartRow)).intValue();
            }
            TLRPC.ChatParticipant chatParticipant = (TLRPC.ChatParticipant) arrayList.get(i3);
            if (ProfileActivity.this.sortedUsers.isEmpty()) {
                arrayList2 = ProfileActivity.this.visibleChatParticipants;
                i4 = i2 - ProfileActivity.this.membersStartRow;
            } else {
                arrayList2 = ProfileActivity.this.visibleChatParticipants;
                i4 = ((Integer) ProfileActivity.this.visibleSortedUsers.get(i2 - ProfileActivity.this.membersStartRow)).intValue();
            }
            return chatParticipant.user_id == ((TLRPC.ChatParticipant) arrayList2.get(i4)).user_id;
        }

        public void fillPositions(SparseIntArray sparseIntArray) {
            sparseIntArray.clear();
            put(1, ProfileActivity.this.setAvatarRow, sparseIntArray);
            put(2, ProfileActivity.this.setAvatarSectionRow, sparseIntArray);
            put(3, ProfileActivity.this.numberSectionRow, sparseIntArray);
            put(4, ProfileActivity.this.numberRow, sparseIntArray);
            put(5, ProfileActivity.this.setUsernameRow, sparseIntArray);
            put(6, ProfileActivity.this.bioRow, sparseIntArray);
            put(7, ProfileActivity.this.phoneSuggestionRow, sparseIntArray);
            put(8, ProfileActivity.this.phoneSuggestionSectionRow, sparseIntArray);
            put(9, ProfileActivity.this.passwordSuggestionRow, sparseIntArray);
            put(10, ProfileActivity.this.passwordSuggestionSectionRow, sparseIntArray);
            put(11, ProfileActivity.this.graceSuggestionRow, sparseIntArray);
            put(12, ProfileActivity.this.graceSuggestionSectionRow, sparseIntArray);
            put(13, ProfileActivity.this.settingsSectionRow, sparseIntArray);
            put(14, ProfileActivity.this.settingsSectionRow2, sparseIntArray);
            put(15, ProfileActivity.this.notificationRow, sparseIntArray);
            put(16, ProfileActivity.this.languageRow, sparseIntArray);
            put(17, ProfileActivity.this.premiumRow, sparseIntArray);
            put(18, ProfileActivity.this.starsRow, sparseIntArray);
            put(19, ProfileActivity.this.businessRow, sparseIntArray);
            put(20, ProfileActivity.this.premiumSectionsRow, sparseIntArray);
            put(21, ProfileActivity.this.premiumGiftingRow, sparseIntArray);
            put(22, ProfileActivity.this.privacyRow, sparseIntArray);
            put(23, ProfileActivity.this.dataRow, sparseIntArray);
            put(24, ProfileActivity.this.liteModeRow, sparseIntArray);
            put(25, ProfileActivity.this.chatRow, sparseIntArray);
            put(26, ProfileActivity.this.filtersRow, sparseIntArray);
            put(27, ProfileActivity.this.stickersRow, sparseIntArray);
            put(28, ProfileActivity.this.devicesRow, sparseIntArray);
            put(29, ProfileActivity.this.devicesSectionRow, sparseIntArray);
            put(30, ProfileActivity.this.helpHeaderRow, sparseIntArray);
            put(31, ProfileActivity.this.questionRow, sparseIntArray);
            put(32, ProfileActivity.this.faqRow, sparseIntArray);
            put(33, ProfileActivity.this.policyRow, sparseIntArray);
            put(34, ProfileActivity.this.helpSectionCell, sparseIntArray);
            put(35, ProfileActivity.this.debugHeaderRow, sparseIntArray);
            put(36, ProfileActivity.this.sendLogsRow, sparseIntArray);
            put(37, ProfileActivity.this.sendLastLogsRow, sparseIntArray);
            put(38, ProfileActivity.this.clearLogsRow, sparseIntArray);
            put(39, ProfileActivity.this.switchBackendRow, sparseIntArray);
            put(40, ProfileActivity.this.versionRow, sparseIntArray);
            put(41, ProfileActivity.this.emptyRow, sparseIntArray);
            put(42, ProfileActivity.this.bottomPaddingRow, sparseIntArray);
            put(43, ProfileActivity.this.infoHeaderRow, sparseIntArray);
            put(44, ProfileActivity.this.phoneRow, sparseIntArray);
            put(45, ProfileActivity.this.locationRow, sparseIntArray);
            put(46, ProfileActivity.this.userInfoRow, sparseIntArray);
            put(47, ProfileActivity.this.channelInfoRow, sparseIntArray);
            put(48, ProfileActivity.this.usernameRow, sparseIntArray);
            put(49, ProfileActivity.this.notificationsDividerRow, sparseIntArray);
            put(50, ProfileActivity.this.reportDividerRow, sparseIntArray);
            put(51, ProfileActivity.this.notificationsRow, sparseIntArray);
            put(52, ProfileActivity.this.infoSectionRow, sparseIntArray);
            put(53, ProfileActivity.this.affiliateRow, sparseIntArray);
            put(54, ProfileActivity.this.infoAffiliateRow, sparseIntArray);
            put(55, ProfileActivity.this.sendMessageRow, sparseIntArray);
            put(56, ProfileActivity.this.reportRow, sparseIntArray);
            put(57, ProfileActivity.this.reportReactionRow, sparseIntArray);
            put(58, ProfileActivity.this.addToContactsRow, sparseIntArray);
            put(59, ProfileActivity.this.settingsTimerRow, sparseIntArray);
            put(60, ProfileActivity.this.settingsKeyRow, sparseIntArray);
            put(61, ProfileActivity.this.secretSettingsSectionRow, sparseIntArray);
            put(62, ProfileActivity.this.membersHeaderRow, sparseIntArray);
            put(63, ProfileActivity.this.addMemberRow, sparseIntArray);
            put(64, ProfileActivity.this.subscribersRow, sparseIntArray);
            put(65, ProfileActivity.this.subscribersRequestsRow, sparseIntArray);
            put(66, ProfileActivity.this.administratorsRow, sparseIntArray);
            put(67, ProfileActivity.this.settingsRow, sparseIntArray);
            put(68, ProfileActivity.this.blockedUsersRow, sparseIntArray);
            put(69, ProfileActivity.this.membersSectionRow, sparseIntArray);
            put(70, ProfileActivity.this.sharedMediaRow, sparseIntArray);
            put(71, ProfileActivity.this.unblockRow, sparseIntArray);
            put(72, ProfileActivity.this.addToGroupButtonRow, sparseIntArray);
            put(73, ProfileActivity.this.addToGroupInfoRow, sparseIntArray);
            put(74, ProfileActivity.this.joinRow, sparseIntArray);
            put(75, ProfileActivity.this.lastSectionRow, sparseIntArray);
            put(76, ProfileActivity.this.notificationsSimpleRow, sparseIntArray);
            put(77, ProfileActivity.this.bizHoursRow, sparseIntArray);
            put(78, ProfileActivity.this.bizLocationRow, sparseIntArray);
            put(79, ProfileActivity.this.birthdayRow, sparseIntArray);
            put(80, ProfileActivity.this.channelRow, sparseIntArray);
            put(81, ProfileActivity.this.botStarsBalanceRow, sparseIntArray);
            put(82, ProfileActivity.this.botTonBalanceRow, sparseIntArray);
            put(83, ProfileActivity.this.channelBalanceRow, sparseIntArray);
            put(84, ProfileActivity.this.balanceDividerRow, sparseIntArray);
            put(85, ProfileActivity.this.botAppRow, sparseIntArray);
            put(86, ProfileActivity.this.botPermissionsHeader, sparseIntArray);
            put(87, ProfileActivity.this.botPermissionLocation, sparseIntArray);
            put(88, ProfileActivity.this.botPermissionEmojiStatus, sparseIntArray);
            put(89, ProfileActivity.this.botPermissionBiometry, sparseIntArray);
            put(90, ProfileActivity.this.botPermissionsDivider, sparseIntArray);
        }

        @Override
        public int getNewListSize() {
            return ProfileActivity.this.rowCount;
        }

        @Override
        public int getOldListSize() {
            return this.oldRowCount;
        }
    }

    public class ListAdapter extends RecyclerListView.SelectionAdapter {
        private Context mContext;
        private final HashMap usernameSpans = new HashMap();

        class AnonymousClass1 extends TextDetailCell {
            AnonymousClass1(Context context, Theme.ResourcesProvider resourcesProvider, boolean z) {
                super(context, resourcesProvider, z);
            }

            @Override
            protected int processColor(int i) {
                return ProfileActivity.this.applyPeerColor(i, false);
            }
        }

        public class AnonymousClass10 extends URLSpan {
            final String val$url;

            AnonymousClass10(String str, String str2) {
                super(str);
                r3 = str2;
            }

            @Override
            public void onClick(View view) {
                Browser.openUrl(ProfileActivity.this.getContext(), r3);
            }

            @Override
            public void updateDrawState(TextPaint textPaint) {
                textPaint.setUnderlineText(true);
            }
        }

        public class AnonymousClass11 extends ClickableSpan {
            final TLRPC.TL_username val$usernameObj;
            final String val$usernameRaw;

            AnonymousClass11(TLRPC.TL_username tL_username, String str) {
                this.val$usernameObj = tL_username;
                this.val$usernameRaw = str;
            }

            public void lambda$onClick$0(TLObject tLObject, TLRPC.TL_username tL_username, TLRPC.TL_error tL_error) {
                ProfileActivity.this.setLoadingSpan(null);
                if (!(tLObject instanceof TL_fragment.TL_collectibleInfo)) {
                    BulletinFactory.showError(tL_error);
                    return;
                }
                TLObject user = ProfileActivity.this.userId != 0 ? ProfileActivity.this.getMessagesController().getUser(Long.valueOf(ProfileActivity.this.userId)) : ProfileActivity.this.getMessagesController().getChat(Long.valueOf(ProfileActivity.this.chatId));
                if (ProfileActivity.this.getContext() == null) {
                    return;
                }
                FragmentUsernameBottomSheet.open(ProfileActivity.this.getContext(), 0, tL_username.username, user, (TL_fragment.TL_collectibleInfo) tLObject, ProfileActivity.this.getResourceProvider());
            }

            public void lambda$onClick$1(final TLRPC.TL_username tL_username, final TLObject tLObject, final TLRPC.TL_error tL_error) {
                AndroidUtilities.runOnUIThread(new Runnable() {
                    @Override
                    public final void run() {
                        ProfileActivity.ListAdapter.AnonymousClass11.this.lambda$onClick$0(tLObject, tL_username, tL_error);
                    }
                });
            }

            @Override
            public void onClick(View view) {
                if (this.val$usernameObj.editable) {
                    ProfileActivity.this.setLoadingSpan(null);
                    String str = ProfileActivity.this.getMessagesController().linkPrefix + "/" + this.val$usernameRaw;
                    if (ProfileActivity.this.currentChat == null || !ProfileActivity.this.currentChat.noforwards) {
                        AndroidUtilities.addToClipboard(str);
                        ProfileActivity.this.undoView.showWithAction(0L, 56, (Runnable) null);
                        return;
                    }
                    return;
                }
                if (ProfileActivity.this.loadingSpan == this) {
                    return;
                }
                ProfileActivity.this.setLoadingSpan(this);
                TL_fragment.TL_getCollectibleInfo tL_getCollectibleInfo = new TL_fragment.TL_getCollectibleInfo();
                TL_fragment.TL_inputCollectibleUsername tL_inputCollectibleUsername = new TL_fragment.TL_inputCollectibleUsername();
                tL_inputCollectibleUsername.username = this.val$usernameObj.username;
                tL_getCollectibleInfo.collectible = tL_inputCollectibleUsername;
                ConnectionsManager connectionsManager = ProfileActivity.this.getConnectionsManager();
                final TLRPC.TL_username tL_username = this.val$usernameObj;
                ProfileActivity.this.getConnectionsManager().bindRequestToGuid(connectionsManager.sendRequest(tL_getCollectibleInfo, new RequestDelegate() {
                    @Override
                    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                        ProfileActivity.ListAdapter.AnonymousClass11.this.lambda$onClick$1(tL_username, tLObject, tL_error);
                    }
                }), ProfileActivity.this.getClassGuid());
            }

            @Override
            public void updateDrawState(TextPaint textPaint) {
                textPaint.setUnderlineText(false);
                textPaint.setColor(textPaint.linkColor);
            }
        }

        class AnonymousClass2 extends AboutLinkCell {
            AnonymousClass2(Context context, BaseFragment baseFragment, Theme.ResourcesProvider resourcesProvider) {
                super(context, baseFragment, resourcesProvider);
            }

            @Override
            protected void didPressUrl(String str, Browser.Progress progress) {
                ProfileActivity.this.openUrl(str, progress);
            }

            @Override
            protected void didResizeEnd() {
                ProfileActivity.this.layoutManager.mIgnoreTopPadding = false;
            }

            @Override
            protected void didResizeStart() {
                ProfileActivity.this.layoutManager.mIgnoreTopPadding = true;
            }

            @Override
            protected int processColor(int i) {
                return ProfileActivity.this.applyPeerColor(i, false);
            }
        }

        class AnonymousClass3 extends TextCell {
            AnonymousClass3(Context context, Theme.ResourcesProvider resourcesProvider) {
                super(context, resourcesProvider);
            }

            @Override
            protected int processColor(int i) {
                return ProfileActivity.this.applyPeerColor(i, false);
            }
        }

        class AnonymousClass4 extends NotificationsCheckCell {
            AnonymousClass4(Context context, int i, int i2, boolean z, Theme.ResourcesProvider resourcesProvider) {
                super(context, i, i2, z, resourcesProvider);
            }

            @Override
            protected int processColor(int i) {
                return ProfileActivity.this.applyPeerColor(i, false);
            }
        }

        class AnonymousClass5 extends View {
            AnonymousClass5(Context context) {
                super(context);
            }

            @Override
            protected void onMeasure(int i, int i2) {
                super.onMeasure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(i), 1073741824), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(32.0f), 1073741824));
            }
        }

        class AnonymousClass6 extends View {
            private int lastPaddingHeight = 0;
            private int lastListViewHeight = 0;

            AnonymousClass6(Context context) {
                super(context);
                this.lastPaddingHeight = 0;
                this.lastListViewHeight = 0;
            }

            @Override
            protected void onMeasure(int i, int i2) {
                if (this.lastListViewHeight != ProfileActivity.this.listView.getMeasuredHeight()) {
                    this.lastPaddingHeight = 0;
                }
                this.lastListViewHeight = ProfileActivity.this.listView.getMeasuredHeight();
                int childCount = ProfileActivity.this.listView.getChildCount();
                if (childCount != ProfileActivity.this.listAdapter.getItemCount()) {
                    setMeasuredDimension(ProfileActivity.this.listView.getMeasuredWidth(), this.lastPaddingHeight);
                    return;
                }
                int i3 = 0;
                for (int i4 = 0; i4 < childCount; i4++) {
                    int childAdapterPosition = ProfileActivity.this.listView.getChildAdapterPosition(ProfileActivity.this.listView.getChildAt(i4));
                    if (childAdapterPosition >= 0 && childAdapterPosition != ProfileActivity.this.bottomPaddingRow) {
                        i3 += ProfileActivity.this.listView.getChildAt(i4).getMeasuredHeight();
                    }
                }
                View view = ProfileActivity.this.fragmentView;
                int measuredHeight = (((view == null ? 0 : view.getMeasuredHeight()) - ActionBar.getCurrentActionBarHeight()) - AndroidUtilities.statusBarHeight) - i3;
                if (measuredHeight > AndroidUtilities.dp(88.0f)) {
                    measuredHeight = 0;
                }
                int i5 = measuredHeight > 0 ? measuredHeight : 0;
                int measuredWidth = ProfileActivity.this.listView.getMeasuredWidth();
                this.lastPaddingHeight = i5;
                setMeasuredDimension(measuredWidth, i5);
            }
        }

        class AnonymousClass7 extends ProfileHoursCell {
            AnonymousClass7(Context context, Theme.ResourcesProvider resourcesProvider) {
                super(context, resourcesProvider);
            }

            @Override
            protected int processColor(int i) {
                return ProfileActivity.this.applyPeerColor(i, false);
            }
        }

        class AnonymousClass8 extends SettingsSuggestionCell {
            AnonymousClass8(Context context, Theme.ResourcesProvider resourcesProvider) {
                super(context, resourcesProvider);
            }

            @Override
            protected void onNoClick(int i) {
                ProfileActivity profileActivity;
                BaseFragment twoStepVerificationSetupActivity;
                if (i == 0) {
                    profileActivity = ProfileActivity.this;
                    twoStepVerificationSetupActivity = new ActionIntroActivity(3);
                } else {
                    profileActivity = ProfileActivity.this;
                    twoStepVerificationSetupActivity = new TwoStepVerificationSetupActivity(8, null);
                }
                profileActivity.presentFragment(twoStepVerificationSetupActivity);
            }

            @Override
            protected void onYesClick(int i) {
                NotificationCenter notificationCenter = ProfileActivity.this.getNotificationCenter();
                ProfileActivity profileActivity = ProfileActivity.this;
                int i2 = NotificationCenter.newSuggestionsAvailable;
                notificationCenter.removeObserver(profileActivity, i2);
                if (i == 2) {
                    ProfileActivity.this.getMessagesController().removeSuggestion(0L, "PREMIUM_GRACE");
                    ProfileActivity.this.updateRowsIds();
                    if (ProfileActivity.this.listAdapter != null) {
                        ProfileActivity.this.listAdapter.notifyDataSetChanged();
                    }
                    Browser.openUrl(getContext(), ProfileActivity.this.getMessagesController().premiumManageSubscriptionUrl);
                } else {
                    ProfileActivity.this.getMessagesController().removeSuggestion(0L, i == 0 ? "VALIDATE_PHONE_NUMBER" : "VALIDATE_PASSWORD");
                    ProfileActivity.this.updateRowsIds();
                    if (ProfileActivity.this.listAdapter != null) {
                        ProfileActivity.this.listAdapter.notifyDataSetChanged();
                    }
                }
                ProfileActivity.this.getNotificationCenter().addObserver(ProfileActivity.this, i2);
            }
        }

        class AnonymousClass9 extends ProfileChannelCell {
            AnonymousClass9(BaseFragment baseFragment) {
                super(baseFragment);
            }

            @Override
            public int processColor(int i) {
                return ProfileActivity.this.applyPeerColor(i, false);
            }
        }

        public ListAdapter(Context context) {
            this.mContext = context;
        }

        private CharSequence alsoUsernamesString(String str, ArrayList arrayList, CharSequence charSequence) {
            if (arrayList == null) {
                return charSequence;
            }
            ArrayList arrayList2 = new ArrayList(arrayList);
            int i = 0;
            while (i < arrayList2.size()) {
                if (!((TLRPC.TL_username) arrayList2.get(i)).active || (str != null && str.equals(((TLRPC.TL_username) arrayList2.get(i)).username))) {
                    arrayList2.remove(i);
                    i--;
                }
                i++;
            }
            CharSequence charSequence2 = charSequence;
            if (arrayList2.size() > 0) {
                SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder();
                for (int i2 = 0; i2 < arrayList2.size(); i2++) {
                    TLRPC.TL_username tL_username = (TLRPC.TL_username) arrayList2.get(i2);
                    SpannableString spannableString = new SpannableString("@" + tL_username.username);
                    spannableString.setSpan(makeUsernameLinkSpan(tL_username), 0, spannableString.length(), 33);
                    ProfileActivity profileActivity = ProfileActivity.this;
                    spannableString.setSpan(new ForegroundColorSpan(profileActivity.applyPeerColor(profileActivity.getThemedColor(Theme.key_chat_messageLinkIn), false)), 0, spannableString.length(), 33);
                    spannableStringBuilder.append((CharSequence) spannableString);
                    if (i2 < arrayList2.size() - 1) {
                        spannableStringBuilder.append((CharSequence) ", ");
                    }
                }
                String string = LocaleController.getString(R.string.UsernameAlso);
                SpannableStringBuilder spannableStringBuilder2 = new SpannableStringBuilder(string);
                int indexOf = string.indexOf("%1$s");
                charSequence2 = spannableStringBuilder2;
                if (indexOf >= 0) {
                    spannableStringBuilder2.replace(indexOf, indexOf + 4, (CharSequence) spannableStringBuilder);
                    charSequence2 = spannableStringBuilder2;
                }
            }
            return charSequence2;
        }

        public void lambda$onBindViewHolder$1(RecyclerView.ViewHolder viewHolder, int i) {
            if (viewHolder.getAdapterPosition() == i && ProfileActivity.this.birthdayRow == i && viewHolder.getItemViewType() == 2) {
                onBindViewHolder(viewHolder, i);
            }
        }

        public void lambda$onBindViewHolder$4(boolean z) {
            Browser.openUrl(ProfileActivity.this.getContext(), LocaleController.getString(z ? R.string.ProfileBotOpenAppInfoOwnerLink : R.string.ProfileBotOpenAppInfoLink));
        }

        public void lambda$onBindViewHolder$5(View view) {
            ProfileActivity.this.hoursShownMine = !r0.hoursShownMine;
            if (!ProfileActivity.this.hoursExpanded) {
                ProfileActivity.this.hoursExpanded = true;
            }
            ProfileActivity.this.saveScrollPosition();
            view.requestLayout();
            ProfileActivity.this.listAdapter.notifyItemChanged(ProfileActivity.this.bizHoursRow);
            ProfileActivity profileActivity = ProfileActivity.this;
            if (profileActivity.savedScrollPosition >= 0) {
                LinearLayoutManager linearLayoutManager = profileActivity.layoutManager;
                ProfileActivity profileActivity2 = ProfileActivity.this;
                linearLayoutManager.scrollToPositionWithOffset(profileActivity2.savedScrollPosition, profileActivity2.savedScrollOffset - profileActivity2.listView.getPaddingTop());
            }
        }

        public void lambda$onCreateViewHolder$0(View view) {
            TLRPC.User user = ProfileActivity.this.getMessagesController().getUser(Long.valueOf(ProfileActivity.this.userId));
            MessagesController messagesController = ProfileActivity.this.getMessagesController();
            ProfileActivity profileActivity = ProfileActivity.this;
            messagesController.openApp(profileActivity, user, null, profileActivity.getClassGuid(), null);
        }

        @Override
        public int getItemCount() {
            return ProfileActivity.this.rowCount;
        }

        @Override
        public int getItemViewType(int i) {
            if (i == ProfileActivity.this.infoHeaderRow || i == ProfileActivity.this.membersHeaderRow || i == ProfileActivity.this.settingsSectionRow2 || i == ProfileActivity.this.numberSectionRow || i == ProfileActivity.this.helpHeaderRow || i == ProfileActivity.this.debugHeaderRow || i == ProfileActivity.this.botPermissionsHeader) {
                return 1;
            }
            if (i == ProfileActivity.this.phoneRow || i == ProfileActivity.this.locationRow || i == ProfileActivity.this.numberRow) {
                return 2;
            }
            ProfileActivity profileActivity = ProfileActivity.this;
            if (i == profileActivity.birthdayRow) {
                return 2;
            }
            if (i == profileActivity.usernameRow || i == ProfileActivity.this.setUsernameRow) {
                return 19;
            }
            if (i == ProfileActivity.this.userInfoRow || i == ProfileActivity.this.channelInfoRow || i == ProfileActivity.this.bioRow) {
                return 3;
            }
            if (i == ProfileActivity.this.settingsTimerRow || i == ProfileActivity.this.settingsKeyRow || i == ProfileActivity.this.reportRow || i == ProfileActivity.this.reportReactionRow || i == ProfileActivity.this.subscribersRow || i == ProfileActivity.this.subscribersRequestsRow || i == ProfileActivity.this.administratorsRow || i == ProfileActivity.this.settingsRow || i == ProfileActivity.this.blockedUsersRow || i == ProfileActivity.this.addMemberRow || i == ProfileActivity.this.joinRow || i == ProfileActivity.this.unblockRow || i == ProfileActivity.this.sendMessageRow || i == ProfileActivity.this.notificationRow || i == ProfileActivity.this.privacyRow || i == ProfileActivity.this.languageRow || i == ProfileActivity.this.dataRow || i == ProfileActivity.this.chatRow || i == ProfileActivity.this.questionRow || i == ProfileActivity.this.devicesRow || i == ProfileActivity.this.filtersRow || i == ProfileActivity.this.stickersRow || i == ProfileActivity.this.faqRow || i == ProfileActivity.this.policyRow || i == ProfileActivity.this.sendLogsRow || i == ProfileActivity.this.sendLastLogsRow || i == ProfileActivity.this.clearLogsRow || i == ProfileActivity.this.switchBackendRow || i == ProfileActivity.this.setAvatarRow || i == ProfileActivity.this.addToGroupButtonRow || i == ProfileActivity.this.addToContactsRow || i == ProfileActivity.this.liteModeRow || i == ProfileActivity.this.premiumGiftingRow || i == ProfileActivity.this.businessRow || i == ProfileActivity.this.botStarsBalanceRow || i == ProfileActivity.this.botTonBalanceRow || i == ProfileActivity.this.channelBalanceRow || i == ProfileActivity.this.botPermissionLocation || i == ProfileActivity.this.botPermissionBiometry || i == ProfileActivity.this.botPermissionEmojiStatus) {
                return 4;
            }
            if (i == ProfileActivity.this.notificationsDividerRow) {
                return 5;
            }
            if (i == ProfileActivity.this.notificationsRow) {
                return 6;
            }
            if (i == ProfileActivity.this.notificationsSimpleRow) {
                return 20;
            }
            if (i == ProfileActivity.this.lastSectionRow || i == ProfileActivity.this.membersSectionRow || i == ProfileActivity.this.secretSettingsSectionRow || i == ProfileActivity.this.settingsSectionRow || i == ProfileActivity.this.devicesSectionRow || i == ProfileActivity.this.helpSectionCell || i == ProfileActivity.this.setAvatarSectionRow || i == ProfileActivity.this.passwordSuggestionSectionRow || i == ProfileActivity.this.phoneSuggestionSectionRow || i == ProfileActivity.this.premiumSectionsRow || i == ProfileActivity.this.reportDividerRow || i == ProfileActivity.this.channelDividerRow || i == ProfileActivity.this.graceSuggestionSectionRow || i == ProfileActivity.this.balanceDividerRow || i == ProfileActivity.this.botPermissionsDivider) {
                return 7;
            }
            if (i >= ProfileActivity.this.membersStartRow && i < ProfileActivity.this.membersEndRow) {
                return 8;
            }
            if (i == ProfileActivity.this.emptyRow) {
                return 11;
            }
            if (i == ProfileActivity.this.bottomPaddingRow) {
                return 12;
            }
            if (i == ProfileActivity.this.sharedMediaRow) {
                return 13;
            }
            if (i == ProfileActivity.this.versionRow) {
                return 14;
            }
            if (i == ProfileActivity.this.passwordSuggestionRow || i == ProfileActivity.this.phoneSuggestionRow || i == ProfileActivity.this.graceSuggestionRow) {
                return 15;
            }
            if (i == ProfileActivity.this.addToGroupInfoRow) {
                return 17;
            }
            if (i == ProfileActivity.this.premiumRow) {
                return 18;
            }
            if (i == ProfileActivity.this.starsRow) {
                return 24;
            }
            if (i == ProfileActivity.this.bizLocationRow) {
                return 21;
            }
            if (i == ProfileActivity.this.bizHoursRow) {
                return 22;
            }
            if (i == ProfileActivity.this.channelRow) {
                return 23;
            }
            if (i == ProfileActivity.this.botAppRow) {
                return 25;
            }
            if (i == ProfileActivity.this.infoSectionRow || i == ProfileActivity.this.infoAffiliateRow) {
                return 26;
            }
            return i == ProfileActivity.this.affiliateRow ? 27 : 0;
        }

        @Override
        public boolean isEnabled(RecyclerView.ViewHolder viewHolder) {
            if (ProfileActivity.this.notificationRow == -1) {
                View view = viewHolder.itemView;
                if (view instanceof UserCell) {
                    Object currentObject = ((UserCell) view).getCurrentObject();
                    if ((currentObject instanceof TLRPC.User) && UserObject.isUserSelf((TLRPC.User) currentObject)) {
                        return false;
                    }
                }
                int itemViewType = viewHolder.getItemViewType();
                return (itemViewType == 1 || itemViewType == 5 || itemViewType == 7 || itemViewType == 11 || itemViewType == 12 || itemViewType == 13 || itemViewType == 9 || itemViewType == 10 || itemViewType == 25) ? false : true;
            }
            int adapterPosition = viewHolder.getAdapterPosition();
            if (adapterPosition == ProfileActivity.this.notificationRow || adapterPosition == ProfileActivity.this.numberRow || adapterPosition == ProfileActivity.this.privacyRow || adapterPosition == ProfileActivity.this.languageRow || adapterPosition == ProfileActivity.this.setUsernameRow || adapterPosition == ProfileActivity.this.bioRow || adapterPosition == ProfileActivity.this.versionRow || adapterPosition == ProfileActivity.this.dataRow || adapterPosition == ProfileActivity.this.chatRow || adapterPosition == ProfileActivity.this.questionRow || adapterPosition == ProfileActivity.this.devicesRow || adapterPosition == ProfileActivity.this.filtersRow || adapterPosition == ProfileActivity.this.stickersRow || adapterPosition == ProfileActivity.this.faqRow || adapterPosition == ProfileActivity.this.policyRow || adapterPosition == ProfileActivity.this.sendLogsRow || adapterPosition == ProfileActivity.this.sendLastLogsRow || adapterPosition == ProfileActivity.this.clearLogsRow || adapterPosition == ProfileActivity.this.switchBackendRow || adapterPosition == ProfileActivity.this.setAvatarRow || adapterPosition == ProfileActivity.this.addToGroupButtonRow || adapterPosition == ProfileActivity.this.premiumRow || adapterPosition == ProfileActivity.this.premiumGiftingRow || adapterPosition == ProfileActivity.this.businessRow || adapterPosition == ProfileActivity.this.liteModeRow) {
                return true;
            }
            ProfileActivity profileActivity = ProfileActivity.this;
            return adapterPosition == profileActivity.birthdayRow || adapterPosition == profileActivity.channelRow || adapterPosition == ProfileActivity.this.starsRow;
        }

        public ClickableSpan makeUsernameLinkSpan(TLRPC.TL_username tL_username) {
            ClickableSpan clickableSpan = (ClickableSpan) this.usernameSpans.get(tL_username);
            if (clickableSpan != null) {
                return clickableSpan;
            }
            AnonymousClass11 anonymousClass11 = new AnonymousClass11(tL_username, tL_username.username);
            this.usernameSpans.put(tL_username, anonymousClass11);
            return anonymousClass11;
        }

        @Override
        public void onBindViewHolder(final androidx.recyclerview.widget.RecyclerView.ViewHolder r31, final int r32) {
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.ProfileActivity.ListAdapter.onBindViewHolder(androidx.recyclerview.widget.RecyclerView$ViewHolder, int):void");
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View view;
            View view2;
            StringBuilder sb;
            String str;
            switch (i) {
                case 1:
                    view = new HeaderCell(this.mContext, 23, ProfileActivity.this.resourcesProvider);
                    View view3 = view;
                    view3.setBackgroundColor(ProfileActivity.this.getThemedColor(Theme.key_windowBackgroundWhite));
                    view2 = view3;
                    break;
                case 2:
                case 19:
                    AnonymousClass1 anonymousClass1 = new TextDetailCell(this.mContext, ProfileActivity.this.resourcesProvider, i == 19) {
                        AnonymousClass1(Context context, Theme.ResourcesProvider resourcesProvider, boolean z) {
                            super(context, resourcesProvider, z);
                        }

                        @Override
                        protected int processColor(int i2) {
                            return ProfileActivity.this.applyPeerColor(i2, false);
                        }
                    };
                    anonymousClass1.setContentDescriptionValueFirst(true);
                    view = anonymousClass1;
                    View view32 = view;
                    view32.setBackgroundColor(ProfileActivity.this.getThemedColor(Theme.key_windowBackgroundWhite));
                    view2 = view32;
                    break;
                case 3:
                    ProfileActivity profileActivity = ProfileActivity.this;
                    Context context = this.mContext;
                    ProfileActivity profileActivity2 = ProfileActivity.this;
                    view = profileActivity.aboutLinkCell = new AboutLinkCell(context, profileActivity2, profileActivity2.resourcesProvider) {
                        AnonymousClass2(Context context2, BaseFragment profileActivity22, Theme.ResourcesProvider resourcesProvider) {
                            super(context2, profileActivity22, resourcesProvider);
                        }

                        @Override
                        protected void didPressUrl(String str2, Browser.Progress progress) {
                            ProfileActivity.this.openUrl(str2, progress);
                        }

                        @Override
                        protected void didResizeEnd() {
                            ProfileActivity.this.layoutManager.mIgnoreTopPadding = false;
                        }

                        @Override
                        protected void didResizeStart() {
                            ProfileActivity.this.layoutManager.mIgnoreTopPadding = true;
                        }

                        @Override
                        protected int processColor(int i2) {
                            return ProfileActivity.this.applyPeerColor(i2, false);
                        }
                    };
                    View view322 = view;
                    view322.setBackgroundColor(ProfileActivity.this.getThemedColor(Theme.key_windowBackgroundWhite));
                    view2 = view322;
                    break;
                case 4:
                    view = new TextCell(this.mContext, ProfileActivity.this.resourcesProvider) {
                        AnonymousClass3(Context context2, Theme.ResourcesProvider resourcesProvider) {
                            super(context2, resourcesProvider);
                        }

                        @Override
                        protected int processColor(int i2) {
                            return ProfileActivity.this.applyPeerColor(i2, false);
                        }
                    };
                    View view3222 = view;
                    view3222.setBackgroundColor(ProfileActivity.this.getThemedColor(Theme.key_windowBackgroundWhite));
                    view2 = view3222;
                    break;
                case 5:
                    View dividerCell = new DividerCell(this.mContext, ProfileActivity.this.resourcesProvider);
                    dividerCell.setBackgroundColor(ProfileActivity.this.getThemedColor(Theme.key_windowBackgroundWhite));
                    dividerCell.setPadding(AndroidUtilities.dp(20.0f), AndroidUtilities.dp(4.0f), 0, 0);
                    view2 = dividerCell;
                    break;
                case 6:
                    view = new NotificationsCheckCell(this.mContext, 23, 70, false, ProfileActivity.this.resourcesProvider) {
                        AnonymousClass4(Context context2, int i2, int i22, boolean z, Theme.ResourcesProvider resourcesProvider) {
                            super(context2, i2, i22, z, resourcesProvider);
                        }

                        @Override
                        protected int processColor(int i2) {
                            return ProfileActivity.this.applyPeerColor(i2, false);
                        }
                    };
                    View view32222 = view;
                    view32222.setBackgroundColor(ProfileActivity.this.getThemedColor(Theme.key_windowBackgroundWhite));
                    view2 = view32222;
                    break;
                case 7:
                    view2 = new ShadowSectionCell(this.mContext, ProfileActivity.this.resourcesProvider);
                    break;
                case 8:
                    view = new UserCell(this.mContext, ProfileActivity.this.addMemberRow == -1 ? 9 : 6, 0, true, ProfileActivity.this.resourcesProvider);
                    View view322222 = view;
                    view322222.setBackgroundColor(ProfileActivity.this.getThemedColor(Theme.key_windowBackgroundWhite));
                    view2 = view322222;
                    break;
                case 9:
                case 10:
                case 14:
                case 16:
                default:
                    TextInfoPrivacyCell textInfoPrivacyCell = new TextInfoPrivacyCell(this.mContext, 10, ProfileActivity.this.resourcesProvider);
                    textInfoPrivacyCell.getTextView().setGravity(1);
                    textInfoPrivacyCell.getTextView().setTextColor(ProfileActivity.this.getThemedColor(Theme.key_windowBackgroundWhiteGrayText3));
                    textInfoPrivacyCell.getTextView().setMovementMethod(null);
                    try {
                        PackageInfo packageInfo = ApplicationLoader.applicationContext.getPackageManager().getPackageInfo(ApplicationLoader.applicationContext.getPackageName(), 0);
                        int i2 = packageInfo.versionCode;
                        int i3 = i2 / 10;
                        int i4 = i2 % 10;
                        if (i4 == 1 || i4 == 2) {
                            sb = new StringBuilder();
                            sb.append("store bundled ");
                            sb.append(Build.CPU_ABI);
                            sb.append(" ");
                            str = Build.CPU_ABI2;
                        } else if (ApplicationLoader.isStandaloneBuild()) {
                            sb = new StringBuilder();
                            sb.append("direct ");
                            sb.append(Build.CPU_ABI);
                            sb.append(" ");
                            str = Build.CPU_ABI2;
                        } else {
                            sb = new StringBuilder();
                            sb.append("universal ");
                            sb.append(Build.CPU_ABI);
                            sb.append(" ");
                            str = Build.CPU_ABI2;
                        }
                        sb.append(str);
                        textInfoPrivacyCell.setText(LocaleController.formatString("TelegramVersion", R.string.TelegramVersion, String.format(Locale.US, "v%s (%d) %s", packageInfo.versionName, Integer.valueOf(i3), sb.toString())));
                    } catch (Exception e) {
                        FileLog.e(e);
                    }
                    textInfoPrivacyCell.getTextView().setPadding(0, AndroidUtilities.dp(14.0f), 0, AndroidUtilities.dp(14.0f));
                    textInfoPrivacyCell.setBackgroundDrawable(Theme.getThemedDrawable(this.mContext, R.drawable.greydivider_bottom, ProfileActivity.this.getThemedColor(Theme.key_windowBackgroundGrayShadow)));
                    view2 = textInfoPrivacyCell;
                    break;
                case 11:
                    view2 = new View(this.mContext) {
                        AnonymousClass5(Context context2) {
                            super(context2);
                        }

                        @Override
                        protected void onMeasure(int i5, int i22) {
                            super.onMeasure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(i5), 1073741824), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(32.0f), 1073741824));
                        }
                    };
                    break;
                case 12:
                    View anonymousClass6 = new View(this.mContext) {
                        private int lastPaddingHeight = 0;
                        private int lastListViewHeight = 0;

                        AnonymousClass6(Context context2) {
                            super(context2);
                            this.lastPaddingHeight = 0;
                            this.lastListViewHeight = 0;
                        }

                        @Override
                        protected void onMeasure(int i5, int i22) {
                            if (this.lastListViewHeight != ProfileActivity.this.listView.getMeasuredHeight()) {
                                this.lastPaddingHeight = 0;
                            }
                            this.lastListViewHeight = ProfileActivity.this.listView.getMeasuredHeight();
                            int childCount = ProfileActivity.this.listView.getChildCount();
                            if (childCount != ProfileActivity.this.listAdapter.getItemCount()) {
                                setMeasuredDimension(ProfileActivity.this.listView.getMeasuredWidth(), this.lastPaddingHeight);
                                return;
                            }
                            int i32 = 0;
                            for (int i42 = 0; i42 < childCount; i42++) {
                                int childAdapterPosition = ProfileActivity.this.listView.getChildAdapterPosition(ProfileActivity.this.listView.getChildAt(i42));
                                if (childAdapterPosition >= 0 && childAdapterPosition != ProfileActivity.this.bottomPaddingRow) {
                                    i32 += ProfileActivity.this.listView.getChildAt(i42).getMeasuredHeight();
                                }
                            }
                            View view4 = ProfileActivity.this.fragmentView;
                            int measuredHeight = (((view4 == null ? 0 : view4.getMeasuredHeight()) - ActionBar.getCurrentActionBarHeight()) - AndroidUtilities.statusBarHeight) - i32;
                            if (measuredHeight > AndroidUtilities.dp(88.0f)) {
                                measuredHeight = 0;
                            }
                            int i52 = measuredHeight > 0 ? measuredHeight : 0;
                            int measuredWidth = ProfileActivity.this.listView.getMeasuredWidth();
                            this.lastPaddingHeight = i52;
                            setMeasuredDimension(measuredWidth, i52);
                        }
                    };
                    anonymousClass6.setBackground(new ColorDrawable(0));
                    view2 = anonymousClass6;
                    break;
                case 13:
                    if (ProfileActivity.this.sharedMediaLayout.getParent() != null) {
                        ((ViewGroup) ProfileActivity.this.sharedMediaLayout.getParent()).removeView(ProfileActivity.this.sharedMediaLayout);
                    }
                    view2 = ProfileActivity.this.sharedMediaLayout;
                    break;
                case 15:
                    view2 = new SettingsSuggestionCell(this.mContext, ProfileActivity.this.resourcesProvider) {
                        AnonymousClass8(Context context2, Theme.ResourcesProvider resourcesProvider) {
                            super(context2, resourcesProvider);
                        }

                        @Override
                        protected void onNoClick(int i5) {
                            ProfileActivity profileActivity3;
                            BaseFragment twoStepVerificationSetupActivity;
                            if (i5 == 0) {
                                profileActivity3 = ProfileActivity.this;
                                twoStepVerificationSetupActivity = new ActionIntroActivity(3);
                            } else {
                                profileActivity3 = ProfileActivity.this;
                                twoStepVerificationSetupActivity = new TwoStepVerificationSetupActivity(8, null);
                            }
                            profileActivity3.presentFragment(twoStepVerificationSetupActivity);
                        }

                        @Override
                        protected void onYesClick(int i5) {
                            NotificationCenter notificationCenter = ProfileActivity.this.getNotificationCenter();
                            ProfileActivity profileActivity3 = ProfileActivity.this;
                            int i22 = NotificationCenter.newSuggestionsAvailable;
                            notificationCenter.removeObserver(profileActivity3, i22);
                            if (i5 == 2) {
                                ProfileActivity.this.getMessagesController().removeSuggestion(0L, "PREMIUM_GRACE");
                                ProfileActivity.this.updateRowsIds();
                                if (ProfileActivity.this.listAdapter != null) {
                                    ProfileActivity.this.listAdapter.notifyDataSetChanged();
                                }
                                Browser.openUrl(getContext(), ProfileActivity.this.getMessagesController().premiumManageSubscriptionUrl);
                            } else {
                                ProfileActivity.this.getMessagesController().removeSuggestion(0L, i5 == 0 ? "VALIDATE_PHONE_NUMBER" : "VALIDATE_PASSWORD");
                                ProfileActivity.this.updateRowsIds();
                                if (ProfileActivity.this.listAdapter != null) {
                                    ProfileActivity.this.listAdapter.notifyDataSetChanged();
                                }
                            }
                            ProfileActivity.this.getNotificationCenter().addObserver(ProfileActivity.this, i22);
                        }
                    };
                    break;
                case 17:
                    view = new TextInfoPrivacyCell(this.mContext, ProfileActivity.this.resourcesProvider);
                    View view3222222 = view;
                    view3222222.setBackgroundColor(ProfileActivity.this.getThemedColor(Theme.key_windowBackgroundWhite));
                    view2 = view3222222;
                    break;
                case 18:
                case 24:
                    view = new ProfilePremiumCell(this.mContext, i == 18 ? 0 : 1, ProfileActivity.this.resourcesProvider);
                    View view32222222 = view;
                    view32222222.setBackgroundColor(ProfileActivity.this.getThemedColor(Theme.key_windowBackgroundWhite));
                    view2 = view32222222;
                    break;
                case 20:
                    view = new TextCheckCell(this.mContext, ProfileActivity.this.resourcesProvider);
                    View view322222222 = view;
                    view322222222.setBackgroundColor(ProfileActivity.this.getThemedColor(Theme.key_windowBackgroundWhite));
                    view2 = view322222222;
                    break;
                case 21:
                    view = new ProfileLocationCell(this.mContext, ProfileActivity.this.resourcesProvider);
                    View view3222222222 = view;
                    view3222222222.setBackgroundColor(ProfileActivity.this.getThemedColor(Theme.key_windowBackgroundWhite));
                    view2 = view3222222222;
                    break;
                case 22:
                    view = new ProfileHoursCell(this.mContext, ProfileActivity.this.resourcesProvider) {
                        AnonymousClass7(Context context2, Theme.ResourcesProvider resourcesProvider) {
                            super(context2, resourcesProvider);
                        }

                        @Override
                        protected int processColor(int i5) {
                            return ProfileActivity.this.applyPeerColor(i5, false);
                        }
                    };
                    View view32222222222 = view;
                    view32222222222.setBackgroundColor(ProfileActivity.this.getThemedColor(Theme.key_windowBackgroundWhite));
                    view2 = view32222222222;
                    break;
                case 23:
                    view = new ProfileChannelCell(ProfileActivity.this) {
                        AnonymousClass9(BaseFragment baseFragment) {
                            super(baseFragment);
                        }

                        @Override
                        public int processColor(int i5) {
                            return ProfileActivity.this.applyPeerColor(i5, false);
                        }
                    };
                    View view322222222222 = view;
                    view322222222222.setBackgroundColor(ProfileActivity.this.getThemedColor(Theme.key_windowBackgroundWhite));
                    view2 = view322222222222;
                    break;
                case 25:
                    FrameLayout frameLayout = new FrameLayout(this.mContext);
                    ButtonWithCounterView buttonWithCounterView = new ButtonWithCounterView(this.mContext, ProfileActivity.this.resourcesProvider);
                    buttonWithCounterView.setText(LocaleController.getString(R.string.ProfileBotOpenApp), false);
                    buttonWithCounterView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public final void onClick(View view4) {
                            ProfileActivity.ListAdapter.this.lambda$onCreateViewHolder$0(view4);
                        }
                    });
                    frameLayout.addView(buttonWithCounterView, LayoutHelper.createFrame(-1, 48.0f, 119, 18.0f, 14.0f, 18.0f, 14.0f));
                    view = frameLayout;
                    View view3222222222222 = view;
                    view3222222222222.setBackgroundColor(ProfileActivity.this.getThemedColor(Theme.key_windowBackgroundWhite));
                    view2 = view3222222222222;
                    break;
                case 26:
                    view2 = new TextInfoPrivacyCell(this.mContext, ProfileActivity.this.resourcesProvider);
                    break;
                case 27:
                    view = new AffiliateProgramFragment.ColorfulTextCell(this.mContext, ProfileActivity.this.resourcesProvider);
                    View view32222222222222 = view;
                    view32222222222222.setBackgroundColor(ProfileActivity.this.getThemedColor(Theme.key_windowBackgroundWhite));
                    view2 = view32222222222222;
                    break;
            }
            if (i != 13) {
                view2.setLayoutParams(new RecyclerView.LayoutParams(-1, -2));
            }
            return new RecyclerListView.Holder(view2);
        }

        @Override
        public void onViewAttachedToWindow(RecyclerView.ViewHolder viewHolder) {
            View view = viewHolder.itemView;
            ProfileActivity profileActivity = ProfileActivity.this;
            if (view == profileActivity.sharedMediaLayout) {
                profileActivity.sharedMediaLayoutAttached = true;
            }
            View view2 = viewHolder.itemView;
            if (view2 instanceof TextDetailCell) {
                ((TextDetailCell) view2).textView.setLoading(ProfileActivity.this.loadingSpan);
                ((TextDetailCell) viewHolder.itemView).valueTextView.setLoading(ProfileActivity.this.loadingSpan);
            }
        }

        @Override
        public void onViewDetachedFromWindow(RecyclerView.ViewHolder viewHolder) {
            View view = viewHolder.itemView;
            ProfileActivity profileActivity = ProfileActivity.this;
            if (view == profileActivity.sharedMediaLayout) {
                profileActivity.sharedMediaLayoutAttached = false;
            }
        }

        @Override
        public void onViewRecycled(RecyclerView.ViewHolder viewHolder) {
            if (viewHolder.getAdapterPosition() == ProfileActivity.this.setAvatarRow) {
                ProfileActivity.this.setAvatarCell = null;
            }
        }
    }

    public class NestedFrameLayout extends SizeNotifierFrameLayout implements NestedScrollingParent3 {
        private NestedScrollingParentHelper nestedScrollingParentHelper;

        public NestedFrameLayout(Context context) {
            super(context);
            this.nestedScrollingParentHelper = new NestedScrollingParentHelper(this);
        }

        public void lambda$onNestedScroll$0() {
            try {
                RecyclerListView currentListView = ProfileActivity.this.sharedMediaLayout.getCurrentListView();
                if (currentListView == null || currentListView.getAdapter() == null) {
                    return;
                }
                currentListView.getAdapter().notifyDataSetChanged();
            } catch (Throwable unused) {
            }
        }

        @Override
        public void drawList(Canvas canvas, boolean z, ArrayList arrayList) {
            super.drawList(canvas, z, arrayList);
            canvas.save();
            canvas.translate(0.0f, ProfileActivity.this.listView.getY());
            ProfileActivity.this.sharedMediaLayout.drawListForBlur(canvas, arrayList);
            canvas.restore();
        }

        @Override
        public boolean onNestedPreFling(View view, float f, float f2) {
            return super.onNestedPreFling(view, f, f2);
        }

        @Override
        public void onNestedPreScroll(View view, int i, int i2, int[] iArr, int i3) {
            int i4;
            RecyclerListView currentListView;
            int findFirstVisibleItemPosition;
            if (view == ProfileActivity.this.listView) {
                if (ProfileActivity.this.sharedMediaRow == -1 || !ProfileActivity.this.sharedMediaLayoutAttached) {
                    return;
                }
                boolean isSearchFieldVisible = ((BaseFragment) ProfileActivity.this).actionBar.isSearchFieldVisible();
                int top = ProfileActivity.this.sharedMediaLayout.getTop();
                boolean z = false;
                if (i2 >= 0) {
                    if (isSearchFieldVisible) {
                        RecyclerListView currentListView2 = ProfileActivity.this.sharedMediaLayout.getCurrentListView();
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
                    return;
                }
                if (top <= 0 && (currentListView = ProfileActivity.this.sharedMediaLayout.getCurrentListView()) != null && (findFirstVisibleItemPosition = ((LinearLayoutManager) currentListView.getLayoutManager()).findFirstVisibleItemPosition()) != -1) {
                    RecyclerView.ViewHolder findViewHolderForAdapterPosition = currentListView.findViewHolderForAdapterPosition(findFirstVisibleItemPosition);
                    int top2 = findViewHolderForAdapterPosition != null ? findViewHolderForAdapterPosition.itemView.getTop() : -1;
                    int paddingTop = currentListView.getPaddingTop();
                    if (top2 != paddingTop || findFirstVisibleItemPosition != 0) {
                        iArr[1] = findFirstVisibleItemPosition != 0 ? i2 : Math.max(i2, top2 - paddingTop);
                        currentListView.scrollBy(0, i2);
                        z = true;
                    }
                }
                if (isSearchFieldVisible) {
                    if (z || top >= 0) {
                        iArr[1] = i2;
                    } else {
                        iArr[1] = i2 - Math.max(top, i2);
                    }
                }
            }
        }

        @Override
        public void onNestedScroll(View view, int i, int i2, int i3, int i4, int i5) {
        }

        @Override
        public void onNestedScroll(View view, int i, int i2, int i3, int i4, int i5, int[] iArr) {
            try {
                if (view == ProfileActivity.this.listView && ProfileActivity.this.sharedMediaLayoutAttached) {
                    RecyclerListView currentListView = ProfileActivity.this.sharedMediaLayout.getCurrentListView();
                    if (ProfileActivity.this.sharedMediaLayout.getTop() == 0) {
                        iArr[1] = i4;
                        currentListView.scrollBy(0, i4);
                    }
                }
                if (i2 == 0 || i5 != 0) {
                    return;
                }
                ProfileActivity.this.hideFloatingButton(i2 > 0);
            } catch (Throwable th) {
                FileLog.e(th);
                AndroidUtilities.runOnUIThread(new Runnable() {
                    @Override
                    public final void run() {
                        ProfileActivity.NestedFrameLayout.this.lambda$onNestedScroll$0();
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
            return ProfileActivity.this.sharedMediaRow != -1 && i == 2;
        }

        @Override
        public void onStopNestedScroll(View view) {
        }

        @Override
        public void onStopNestedScroll(View view, int i) {
            this.nestedScrollingParentHelper.onStopNestedScroll(view);
        }
    }

    public class OverlaysView extends View implements ProfileGalleryView.Callback {
        private float alpha;
        private float[] alphas;
        private final ValueAnimator animator;
        private final float[] animatorValues;
        private final Paint backgroundPaint;
        private final Paint barPaint;
        private final GradientDrawable bottomOverlayGradient;
        private final Rect bottomOverlayRect;
        private float currentAnimationValue;
        private int currentLoadingAnimationDirection;
        private float currentLoadingAnimationProgress;
        private float currentProgress;
        private boolean isOverlaysVisible;
        private long lastTime;
        private final float[] pressedOverlayAlpha;
        private final GradientDrawable[] pressedOverlayGradient;
        private final boolean[] pressedOverlayVisible;
        private int previousSelectedPotision;
        private float previousSelectedProgress;
        private final RectF rect;
        private final Paint selectedBarPaint;
        private int selectedPosition;
        private final int statusBarHeight;
        private final GradientDrawable topOverlayGradient;
        private final Rect topOverlayRect;

        public class AnonymousClass1 extends AnimatorListenerAdapter {
            final ProfileActivity val$this$0;

            AnonymousClass1(ProfileActivity profileActivity) {
                r2 = profileActivity;
            }

            @Override
            public void onAnimationEnd(Animator animator) {
                if (OverlaysView.this.isOverlaysVisible) {
                    return;
                }
                OverlaysView.this.setVisibility(8);
            }

            @Override
            public void onAnimationStart(Animator animator) {
                OverlaysView.this.setVisibility(0);
            }
        }

        public OverlaysView(Context context) {
            super(context);
            this.statusBarHeight = (!((BaseFragment) ProfileActivity.this).actionBar.getOccupyStatusBar() || ((BaseFragment) ProfileActivity.this).inBubbleMode) ? 0 : AndroidUtilities.statusBarHeight;
            this.topOverlayRect = new Rect();
            this.bottomOverlayRect = new Rect();
            this.rect = new RectF();
            this.animatorValues = new float[]{0.0f, 1.0f};
            this.pressedOverlayGradient = new GradientDrawable[2];
            this.pressedOverlayVisible = new boolean[2];
            this.pressedOverlayAlpha = new float[2];
            this.alpha = 0.0f;
            this.alphas = null;
            this.previousSelectedPotision = -1;
            this.currentLoadingAnimationDirection = 1;
            setVisibility(8);
            Paint paint = new Paint(1);
            this.barPaint = paint;
            paint.setColor(1442840575);
            Paint paint2 = new Paint(1);
            this.selectedBarPaint = paint2;
            paint2.setColor(-1);
            GradientDrawable gradientDrawable = new GradientDrawable(GradientDrawable.Orientation.TOP_BOTTOM, new int[]{1107296256, 0});
            this.topOverlayGradient = gradientDrawable;
            gradientDrawable.setShape(0);
            GradientDrawable gradientDrawable2 = new GradientDrawable(GradientDrawable.Orientation.BOTTOM_TOP, new int[]{1107296256, 0});
            this.bottomOverlayGradient = gradientDrawable2;
            gradientDrawable2.setShape(0);
            int i = 0;
            while (i < 2) {
                this.pressedOverlayGradient[i] = new GradientDrawable(i == 0 ? GradientDrawable.Orientation.LEFT_RIGHT : GradientDrawable.Orientation.RIGHT_LEFT, new int[]{838860800, 0});
                this.pressedOverlayGradient[i].setShape(0);
                i++;
            }
            Paint paint3 = new Paint(1);
            this.backgroundPaint = paint3;
            paint3.setColor(-16777216);
            paint3.setAlpha(66);
            ValueAnimator ofFloat = ValueAnimator.ofFloat(0.0f, 1.0f);
            this.animator = ofFloat;
            ofFloat.setDuration(250L);
            ofFloat.setInterpolator(CubicBezierInterpolator.EASE_BOTH);
            ofFloat.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                    ProfileActivity.OverlaysView.this.lambda$new$0(valueAnimator);
                }
            });
            ofFloat.addListener(new AnimatorListenerAdapter() {
                final ProfileActivity val$this$0;

                AnonymousClass1(ProfileActivity profileActivity) {
                    r2 = profileActivity;
                }

                @Override
                public void onAnimationEnd(Animator animator) {
                    if (OverlaysView.this.isOverlaysVisible) {
                        return;
                    }
                    OverlaysView.this.setVisibility(8);
                }

                @Override
                public void onAnimationStart(Animator animator) {
                    OverlaysView.this.setVisibility(0);
                }
            });
        }

        public void lambda$new$0(ValueAnimator valueAnimator) {
            float[] fArr = this.animatorValues;
            float animatedFraction = valueAnimator.getAnimatedFraction();
            this.currentAnimationValue = animatedFraction;
            setAlphaValue(AndroidUtilities.lerp(fArr, animatedFraction), true);
        }

        public boolean isOverlaysVisible() {
            return this.isOverlaysVisible;
        }

        @Override
        public void onDown(boolean z) {
            this.pressedOverlayVisible[!z ? 1 : 0] = true;
            postInvalidateOnAnimation();
        }

        @Override
        protected void onDraw(android.graphics.Canvas r22) {
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.ProfileActivity.OverlaysView.onDraw(android.graphics.Canvas):void");
        }

        @Override
        public void onPhotosLoaded() {
            ProfileActivity.this.updateProfileData(false);
        }

        @Override
        public void onRelease() {
            Arrays.fill(this.pressedOverlayVisible, false);
            postInvalidateOnAnimation();
        }

        @Override
        protected void onSizeChanged(int i, int i2, int i3, int i4) {
            int currentActionBarHeight = this.statusBarHeight + ActionBar.getCurrentActionBarHeight();
            this.topOverlayRect.set(0, 0, i, (int) (currentActionBarHeight * 0.5f));
            this.bottomOverlayRect.set(0, (int) (i2 - (AndroidUtilities.dp(72.0f) * 0.5f)), i, i2);
            this.topOverlayGradient.setBounds(0, this.topOverlayRect.bottom, i, currentActionBarHeight + AndroidUtilities.dp(16.0f));
            this.bottomOverlayGradient.setBounds(0, (i2 - AndroidUtilities.dp(72.0f)) - AndroidUtilities.dp(24.0f), i, this.bottomOverlayRect.top);
            int i5 = i / 5;
            this.pressedOverlayGradient[0].setBounds(0, 0, i5, i2);
            this.pressedOverlayGradient[1].setBounds(i - i5, 0, i, i2);
        }

        @Override
        public void onVideoSet() {
            invalidate();
        }

        public void saveCurrentPageProgress() {
            this.previousSelectedProgress = this.currentProgress;
            this.previousSelectedPotision = this.selectedPosition;
            this.currentLoadingAnimationProgress = 0.0f;
            this.currentLoadingAnimationDirection = 1;
        }

        public void setAlphaValue(float f, boolean z) {
            int i = (int) (255.0f * f);
            this.topOverlayGradient.setAlpha(i);
            this.bottomOverlayGradient.setAlpha(i);
            this.backgroundPaint.setAlpha((int) (66.0f * f));
            this.barPaint.setAlpha((int) (85.0f * f));
            this.selectedBarPaint.setAlpha(i);
            this.alpha = f;
            if (!z) {
                this.currentAnimationValue = f;
            }
            invalidate();
        }

        public void setOverlaysVisible() {
            this.isOverlaysVisible = true;
            setVisibility(0);
        }

        public void setOverlaysVisible(boolean z, float f) {
            if (z != this.isOverlaysVisible) {
                this.isOverlaysVisible = z;
                this.animator.cancel();
                float lerp = AndroidUtilities.lerp(this.animatorValues, this.currentAnimationValue);
                this.animator.setDuration(z ? ((1.0f - lerp) * 250.0f) / f : (250.0f * lerp) / f);
                float[] fArr = this.animatorValues;
                fArr[0] = lerp;
                fArr[1] = z ? 1.0f : 0.0f;
                this.animator.start();
            }
        }
    }

    public class PagerIndicatorView extends View {
        private final PagerAdapter adapter;
        private final ValueAnimator animator;
        private final float[] animatorValues;
        private final Paint backgroundPaint;
        private final RectF indicatorRect;
        private boolean isIndicatorVisible;
        private final TextPaint textPaint;

        public class AnonymousClass1 extends AnimatorListenerAdapter {
            final boolean val$expanded;
            final ProfileActivity val$this$0;

            AnonymousClass1(ProfileActivity profileActivity, boolean z) {
                r2 = profileActivity;
                r3 = z;
            }

            @Override
            public void onAnimationEnd(Animator animator) {
                View view;
                if (PagerIndicatorView.this.isIndicatorVisible) {
                    if (ProfileActivity.this.searchItem != null) {
                        ProfileActivity.this.searchItem.setClickable(false);
                    }
                    if (ProfileActivity.this.editItemVisible) {
                        ProfileActivity.this.editItem.setVisibility(8);
                    }
                    if (ProfileActivity.this.callItemVisible) {
                        ProfileActivity.this.callItem.setVisibility(8);
                    }
                    if (ProfileActivity.this.videoCallItemVisible) {
                        view = ProfileActivity.this.videoCallItem;
                    }
                    ProfileActivity.this.updateStoriesViewBounds(false);
                }
                view = PagerIndicatorView.this;
                view.setVisibility(8);
                ProfileActivity.this.updateStoriesViewBounds(false);
            }

            @Override
            public void onAnimationStart(Animator animator) {
                if (ProfileActivity.this.searchItem != null && !r3) {
                    ProfileActivity.this.searchItem.setClickable(true);
                }
                if (ProfileActivity.this.editItemVisible) {
                    ProfileActivity.this.editItem.setVisibility(0);
                }
                if (ProfileActivity.this.callItemVisible) {
                    ProfileActivity.this.callItem.setVisibility(0);
                }
                if (ProfileActivity.this.videoCallItemVisible) {
                    ProfileActivity.this.videoCallItem.setVisibility(0);
                }
                PagerIndicatorView.this.setVisibility(0);
                ProfileActivity.this.updateStoriesViewBounds(false);
            }
        }

        public class AnonymousClass2 implements ViewPager.OnPageChangeListener {
            private int prevPage;
            final ProfileActivity val$this$0;

            AnonymousClass2(ProfileActivity profileActivity) {
                r2 = profileActivity;
            }

            @Override
            public void onPageScrollStateChanged(int i) {
            }

            @Override
            public void onPageScrolled(int i, float f, int i2) {
            }

            @Override
            public void onPageSelected(int i) {
                int realPosition = ProfileActivity.this.avatarsViewPager.getRealPosition(i);
                PagerIndicatorView.this.invalidateIndicatorRect(this.prevPage != realPosition);
                this.prevPage = realPosition;
                PagerIndicatorView.this.updateAvatarItems();
            }
        }

        public class AnonymousClass3 extends DataSetObserver {
            final ProfileActivity val$this$0;

            AnonymousClass3(ProfileActivity profileActivity) {
                r2 = profileActivity;
            }

            @Override
            public void onChanged() {
                int realCount = ProfileActivity.this.avatarsViewPager.getRealCount();
                if (ProfileActivity.this.overlayCountVisible == 0 && realCount > 1 && realCount <= 20 && ProfileActivity.this.overlaysView.isOverlaysVisible()) {
                    ProfileActivity.this.overlayCountVisible = 1;
                }
                PagerIndicatorView.this.invalidateIndicatorRect(false);
                PagerIndicatorView.this.refreshVisibility(1.0f);
                PagerIndicatorView.this.updateAvatarItems();
            }
        }

        public PagerIndicatorView(Context context) {
            super(context);
            this.indicatorRect = new RectF();
            this.animatorValues = new float[]{0.0f, 1.0f};
            PagerAdapter adapter = ProfileActivity.this.avatarsViewPager.getAdapter();
            this.adapter = adapter;
            setVisibility(8);
            TextPaint textPaint = new TextPaint(1);
            this.textPaint = textPaint;
            textPaint.setColor(-1);
            textPaint.setTypeface(Typeface.SANS_SERIF);
            textPaint.setTextAlign(Paint.Align.CENTER);
            textPaint.setTextSize(AndroidUtilities.dpf2(15.0f));
            Paint paint = new Paint(1);
            this.backgroundPaint = paint;
            paint.setColor(637534208);
            ValueAnimator ofFloat = ValueAnimator.ofFloat(0.0f, 1.0f);
            this.animator = ofFloat;
            ofFloat.setInterpolator(CubicBezierInterpolator.EASE_BOTH);
            ofFloat.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                    ProfileActivity.PagerIndicatorView.this.lambda$new$0(valueAnimator);
                }
            });
            ofFloat.addListener(new AnimatorListenerAdapter() {
                final boolean val$expanded;
                final ProfileActivity val$this$0;

                AnonymousClass1(ProfileActivity profileActivity, boolean z) {
                    r2 = profileActivity;
                    r3 = z;
                }

                @Override
                public void onAnimationEnd(Animator animator) {
                    View view;
                    if (PagerIndicatorView.this.isIndicatorVisible) {
                        if (ProfileActivity.this.searchItem != null) {
                            ProfileActivity.this.searchItem.setClickable(false);
                        }
                        if (ProfileActivity.this.editItemVisible) {
                            ProfileActivity.this.editItem.setVisibility(8);
                        }
                        if (ProfileActivity.this.callItemVisible) {
                            ProfileActivity.this.callItem.setVisibility(8);
                        }
                        if (ProfileActivity.this.videoCallItemVisible) {
                            view = ProfileActivity.this.videoCallItem;
                        }
                        ProfileActivity.this.updateStoriesViewBounds(false);
                    }
                    view = PagerIndicatorView.this;
                    view.setVisibility(8);
                    ProfileActivity.this.updateStoriesViewBounds(false);
                }

                @Override
                public void onAnimationStart(Animator animator) {
                    if (ProfileActivity.this.searchItem != null && !r3) {
                        ProfileActivity.this.searchItem.setClickable(true);
                    }
                    if (ProfileActivity.this.editItemVisible) {
                        ProfileActivity.this.editItem.setVisibility(0);
                    }
                    if (ProfileActivity.this.callItemVisible) {
                        ProfileActivity.this.callItem.setVisibility(0);
                    }
                    if (ProfileActivity.this.videoCallItemVisible) {
                        ProfileActivity.this.videoCallItem.setVisibility(0);
                    }
                    PagerIndicatorView.this.setVisibility(0);
                    ProfileActivity.this.updateStoriesViewBounds(false);
                }
            });
            ProfileActivity.this.avatarsViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                private int prevPage;
                final ProfileActivity val$this$0;

                AnonymousClass2(ProfileActivity profileActivity) {
                    r2 = profileActivity;
                }

                @Override
                public void onPageScrollStateChanged(int i) {
                }

                @Override
                public void onPageScrolled(int i, float f, int i2) {
                }

                @Override
                public void onPageSelected(int i) {
                    int realPosition = ProfileActivity.this.avatarsViewPager.getRealPosition(i);
                    PagerIndicatorView.this.invalidateIndicatorRect(this.prevPage != realPosition);
                    this.prevPage = realPosition;
                    PagerIndicatorView.this.updateAvatarItems();
                }
            });
            adapter.registerDataSetObserver(new DataSetObserver() {
                final ProfileActivity val$this$0;

                AnonymousClass3(ProfileActivity profileActivity) {
                    r2 = profileActivity;
                }

                @Override
                public void onChanged() {
                    int realCount = ProfileActivity.this.avatarsViewPager.getRealCount();
                    if (ProfileActivity.this.overlayCountVisible == 0 && realCount > 1 && realCount <= 20 && ProfileActivity.this.overlaysView.isOverlaysVisible()) {
                        ProfileActivity.this.overlayCountVisible = 1;
                    }
                    PagerIndicatorView.this.invalidateIndicatorRect(false);
                    PagerIndicatorView.this.refreshVisibility(1.0f);
                    PagerIndicatorView.this.updateAvatarItems();
                }
            });
        }

        private String getCurrentTitle() {
            return this.adapter.getPageTitle(ProfileActivity.this.avatarsViewPager.getCurrentItem()).toString();
        }

        public ActionBarMenuItem getSecondaryMenuItem() {
            if (ProfileActivity.this.callItemVisible) {
                return ProfileActivity.this.callItem;
            }
            if (ProfileActivity.this.editItemVisible) {
                return ProfileActivity.this.editItem;
            }
            if (ProfileActivity.this.searchItem != null) {
                return ProfileActivity.this.searchItem;
            }
            return null;
        }

        public void invalidateIndicatorRect(boolean z) {
            if (z) {
                ProfileActivity.this.overlaysView.saveCurrentPageProgress();
            }
            ProfileActivity.this.overlaysView.invalidate();
            float measureText = this.textPaint.measureText(getCurrentTitle());
            this.indicatorRect.right = (getMeasuredWidth() - AndroidUtilities.dp(54.0f)) - (ProfileActivity.this.qrItem != null ? AndroidUtilities.dp(48.0f) : 0);
            RectF rectF = this.indicatorRect;
            rectF.left = rectF.right - (measureText + AndroidUtilities.dpf2(16.0f));
            this.indicatorRect.top = (((BaseFragment) ProfileActivity.this).actionBar.getOccupyStatusBar() ? AndroidUtilities.statusBarHeight : 0) + AndroidUtilities.dp(15.0f);
            RectF rectF2 = this.indicatorRect;
            rectF2.bottom = rectF2.top + AndroidUtilities.dp(26.0f);
            setPivotX(this.indicatorRect.centerX());
            setPivotY(this.indicatorRect.centerY());
            invalidate();
        }

        public void lambda$new$0(ValueAnimator valueAnimator) {
            float lerp = AndroidUtilities.lerp(this.animatorValues, valueAnimator.getAnimatedFraction());
            if (ProfileActivity.this.searchItem != null && !ProfileActivity.this.isPulledDown) {
                float f = 1.0f - lerp;
                ProfileActivity.this.searchItem.setScaleX(f);
                ProfileActivity.this.searchItem.setScaleY(f);
                ProfileActivity.this.searchItem.setAlpha(f);
            }
            if (ProfileActivity.this.editItemVisible) {
                float f2 = 1.0f - lerp;
                ProfileActivity.this.editItem.setScaleX(f2);
                ProfileActivity.this.editItem.setScaleY(f2);
                ProfileActivity.this.editItem.setAlpha(f2);
            }
            if (ProfileActivity.this.callItemVisible) {
                float f3 = 1.0f - lerp;
                ProfileActivity.this.callItem.setScaleX(f3);
                ProfileActivity.this.callItem.setScaleY(f3);
                ProfileActivity.this.callItem.setAlpha(f3);
            }
            if (ProfileActivity.this.videoCallItemVisible) {
                float f4 = 1.0f - lerp;
                ProfileActivity.this.videoCallItem.setScaleX(f4);
                ProfileActivity.this.videoCallItem.setScaleY(f4);
                ProfileActivity.this.videoCallItem.setAlpha(f4);
            }
            setScaleX(lerp);
            setScaleY(lerp);
            setAlpha(lerp);
        }

        public void updateAvatarItems() {
            if (ProfileActivity.this.imageUpdater == null) {
                return;
            }
            if (ProfileActivity.this.otherItem.isSubMenuShowing()) {
                AndroidUtilities.runOnUIThread(new Runnable() {
                    @Override
                    public final void run() {
                        ProfileActivity.PagerIndicatorView.this.updateAvatarItemsInternal();
                    }
                }, 500L);
            } else {
                updateAvatarItemsInternal();
            }
        }

        public void updateAvatarItemsInternal() {
            if (ProfileActivity.this.otherItem == null || ProfileActivity.this.avatarsViewPager == null || !ProfileActivity.this.isPulledDown) {
                return;
            }
            if (ProfileActivity.this.avatarsViewPager.getRealPosition() == 0) {
                ProfileActivity.this.otherItem.hideSubItem(33);
                ProfileActivity.this.otherItem.showSubItem(36);
            } else {
                ProfileActivity.this.otherItem.showSubItem(33);
                ProfileActivity.this.otherItem.hideSubItem(36);
            }
        }

        public boolean isIndicatorFullyVisible() {
            return this.isIndicatorVisible && !this.animator.isRunning();
        }

        @Override
        protected void onDraw(Canvas canvas) {
            float dpf2 = AndroidUtilities.dpf2(12.0f);
            canvas.drawRoundRect(this.indicatorRect, dpf2, dpf2, this.backgroundPaint);
            canvas.drawText(getCurrentTitle(), this.indicatorRect.centerX(), this.indicatorRect.top + AndroidUtilities.dpf2(18.5f), this.textPaint);
        }

        @Override
        protected void onSizeChanged(int i, int i2, int i3, int i4) {
            invalidateIndicatorRect(false);
        }

        public void refreshVisibility(float f) {
            setIndicatorVisible(ProfileActivity.this.isPulledDown && ProfileActivity.this.avatarsViewPager.getRealCount() > 20, f);
        }

        public void setIndicatorVisible(boolean z, float f) {
            if (z != this.isIndicatorVisible) {
                this.isIndicatorVisible = z;
                this.animator.cancel();
                float lerp = AndroidUtilities.lerp(this.animatorValues, this.animator.getAnimatedFraction());
                if (f <= 0.0f) {
                    this.animator.setDuration(0L);
                } else {
                    this.animator.setDuration(z ? ((1.0f - lerp) * 250.0f) / f : (250.0f * lerp) / f);
                }
                float[] fArr = this.animatorValues;
                fArr[0] = lerp;
                fArr[1] = z ? 1.0f : 0.0f;
                this.animator.start();
            }
        }
    }

    public class SearchAdapter extends RecyclerListView.SelectionAdapter {
        private TLRPC.WebPage faqWebPage;
        private String lastSearchString;
        private boolean loadingFaqPage;
        private Context mContext;
        private Runnable searchRunnable;
        private boolean searchWas;
        private SearchResult[] searchArray = onCreateSearchArray();
        private ArrayList faqSearchArray = new ArrayList();
        private ArrayList resultNames = new ArrayList();
        private ArrayList searchResults = new ArrayList();
        private ArrayList faqSearchResults = new ArrayList();
        private ArrayList recentSearches = new ArrayList();

        public class SearchResult {
            private int guid;
            private int iconResId;
            private int num;
            private Runnable openRunnable;
            private String[] path;
            private String rowName;
            private String searchTitle;

            public SearchResult(SearchAdapter searchAdapter, int i, String str, int i2, Runnable runnable) {
                this(i, str, null, null, null, i2, runnable);
            }

            public SearchResult(SearchAdapter searchAdapter, int i, String str, String str2, int i2, Runnable runnable) {
                this(i, str, null, str2, null, i2, runnable);
            }

            public SearchResult(SearchAdapter searchAdapter, int i, String str, String str2, String str3, int i2, Runnable runnable) {
                this(i, str, str2, str3, null, i2, runnable);
            }

            public SearchResult(int i, String str, String str2, String str3, String str4, int i2, Runnable runnable) {
                this.guid = i;
                this.searchTitle = str;
                this.rowName = str2;
                this.openRunnable = runnable;
                this.iconResId = i2;
                if (str3 != null && str4 != null) {
                    this.path = new String[]{str3, str4};
                } else if (str3 != null) {
                    this.path = new String[]{str3};
                }
            }

            public void open() {
                this.openRunnable.run();
                AndroidUtilities.scrollToFragmentRow(((BaseFragment) ProfileActivity.this).parentLayout, this.rowName);
            }

            public boolean equals(Object obj) {
                return (obj instanceof SearchResult) && this.guid == ((SearchResult) obj).guid;
            }

            public String toString() {
                SerializedData serializedData = new SerializedData();
                serializedData.writeInt32(this.num);
                serializedData.writeInt32(1);
                serializedData.writeInt32(this.guid);
                return Utilities.bytesToHex(serializedData.toByteArray());
            }
        }

        public SearchAdapter(Context context) {
            this.mContext = context;
            updateSearchArray();
        }

        private int getNum(Object obj) {
            if (obj instanceof SearchResult) {
                return ((SearchResult) obj).num;
            }
            if (obj instanceof MessagesController.FaqSearchResult) {
                return ((MessagesController.FaqSearchResult) obj).num;
            }
            return 0;
        }

        private boolean isPremiumFeatureAvailable(int i) {
            if (!ProfileActivity.this.getMessagesController().premiumFeaturesBlocked() || ProfileActivity.this.getUserConfig().isPremium()) {
                return i == -1 || ProfileActivity.this.getMessagesController().premiumFeaturesTypesToPosition.get(i, -1) != -1;
            }
            return false;
        }

        public void lambda$loadFaqWebPage$143(ArrayList arrayList) {
            this.faqSearchArray.addAll(arrayList);
            ProfileActivity.this.getMessagesController().faqSearchArray = arrayList;
            ProfileActivity.this.getMessagesController().faqWebPage = this.faqWebPage;
            if (this.searchWas) {
                return;
            }
            notifyDataSetChanged();
        }

        public void lambda$loadFaqWebPage$144(org.telegram.tgnet.TLObject r13, org.telegram.tgnet.TLRPC.TL_error r14) {
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.ProfileActivity.SearchAdapter.lambda$loadFaqWebPage$144(org.telegram.tgnet.TLObject, org.telegram.tgnet.TLRPC$TL_error):void");
        }

        public void lambda$onCreateSearchArray$1() {
            ProfileActivity profileActivity = ProfileActivity.this;
            profileActivity.presentFragment(new ChangeNameActivity(profileActivity.resourcesProvider));
        }

        public void lambda$onCreateSearchArray$10() {
            ProfileActivity.this.presentFragment(new NotificationsSettingsActivity());
        }

        public void lambda$onCreateSearchArray$100() {
            ProfileActivity.this.presentFragment(new StickersActivity(5, null));
        }

        public void lambda$onCreateSearchArray$101() {
            ProfileActivity.this.presentFragment(new StickersActivity(5, null));
        }

        public void lambda$onCreateSearchArray$102() {
            ProfileActivity.this.presentFragment(new ReactionsDoubleTapManageActivity());
        }

        public void lambda$onCreateSearchArray$103() {
            ProfileActivity.this.presentFragment(new FiltersSetupActivity());
        }

        public void lambda$onCreateSearchArray$104() {
            ProfileActivity.this.presentFragment(new FiltersSetupActivity());
        }

        public void lambda$onCreateSearchArray$105() {
            ProfileActivity.this.presentFragment(new PremiumPreviewFragment("settings"));
        }

        public void lambda$onCreateSearchArray$106() {
            ProfileActivity.this.showDialog(new PremiumFeatureBottomSheet(ProfileActivity.this, 0, false).setForceAbout());
        }

        public void lambda$onCreateSearchArray$107() {
            ProfileActivity.this.showDialog(new PremiumFeatureBottomSheet(ProfileActivity.this, 11, false).setForceAbout());
        }

        public void lambda$onCreateSearchArray$108() {
            ProfileActivity.this.showDialog(new PremiumFeatureBottomSheet(ProfileActivity.this, 1, false).setForceAbout());
        }

        public void lambda$onCreateSearchArray$109() {
            ProfileActivity.this.showDialog(new PremiumFeatureBottomSheet(ProfileActivity.this, 2, false).setForceAbout());
        }

        public void lambda$onCreateSearchArray$11() {
            ProfileActivity.this.presentFragment(new NotificationsSettingsActivity());
        }

        public void lambda$onCreateSearchArray$110() {
            ProfileActivity.this.showDialog(new PremiumFeatureBottomSheet(ProfileActivity.this, 8, false).setForceAbout());
        }

        public void lambda$onCreateSearchArray$111() {
            ProfileActivity.this.showDialog(new PremiumFeatureBottomSheet(ProfileActivity.this, 3, false).setForceAbout());
        }

        public void lambda$onCreateSearchArray$112() {
            ProfileActivity.this.showDialog(new PremiumFeatureBottomSheet(ProfileActivity.this, 4, false).setForceAbout());
        }

        public void lambda$onCreateSearchArray$113() {
            ProfileActivity.this.showDialog(new PremiumFeatureBottomSheet(ProfileActivity.this, 5, false).setForceAbout());
        }

        public void lambda$onCreateSearchArray$114() {
            ProfileActivity.this.showDialog(new PremiumFeatureBottomSheet(ProfileActivity.this, 9, false).setForceAbout());
        }

        public void lambda$onCreateSearchArray$115() {
            ProfileActivity.this.showDialog(new PremiumFeatureBottomSheet(ProfileActivity.this, 6, false).setForceAbout());
        }

        public void lambda$onCreateSearchArray$116() {
            ProfileActivity.this.showDialog(new PremiumFeatureBottomSheet(ProfileActivity.this, 7, false).setForceAbout());
        }

        public void lambda$onCreateSearchArray$117() {
            ProfileActivity.this.showDialog(new PremiumFeatureBottomSheet(ProfileActivity.this, 10, false).setForceAbout());
        }

        public void lambda$onCreateSearchArray$118() {
            ProfileActivity.this.showDialog(new PremiumFeatureBottomSheet(ProfileActivity.this, 12, false).setForceAbout());
        }

        public void lambda$onCreateSearchArray$119() {
            ProfileActivity.this.presentFragment(new LiteModeSettingsActivity());
        }

        public void lambda$onCreateSearchArray$12() {
            ProfileActivity.this.presentFragment(new NotificationsSettingsActivity());
        }

        public void lambda$onCreateSearchArray$120() {
            LiteModeSettingsActivity liteModeSettingsActivity = new LiteModeSettingsActivity();
            ProfileActivity.this.presentFragment(liteModeSettingsActivity);
            liteModeSettingsActivity.scrollToFlags(3);
        }

        public void lambda$onCreateSearchArray$121() {
            LiteModeSettingsActivity liteModeSettingsActivity = new LiteModeSettingsActivity();
            ProfileActivity.this.presentFragment(liteModeSettingsActivity);
            liteModeSettingsActivity.setExpanded(3, true);
            liteModeSettingsActivity.scrollToFlags(1);
        }

        public void lambda$onCreateSearchArray$122() {
            LiteModeSettingsActivity liteModeSettingsActivity = new LiteModeSettingsActivity();
            ProfileActivity.this.presentFragment(liteModeSettingsActivity);
            liteModeSettingsActivity.setExpanded(3, true);
            liteModeSettingsActivity.scrollToFlags(2);
        }

        public void lambda$onCreateSearchArray$123() {
            LiteModeSettingsActivity liteModeSettingsActivity = new LiteModeSettingsActivity();
            ProfileActivity.this.presentFragment(liteModeSettingsActivity);
            liteModeSettingsActivity.scrollToFlags(28700);
        }

        public void lambda$onCreateSearchArray$124() {
            LiteModeSettingsActivity liteModeSettingsActivity = new LiteModeSettingsActivity();
            ProfileActivity.this.presentFragment(liteModeSettingsActivity);
            liteModeSettingsActivity.setExpanded(28700, true);
            liteModeSettingsActivity.scrollToFlags(16388);
        }

        public void lambda$onCreateSearchArray$125() {
            LiteModeSettingsActivity liteModeSettingsActivity = new LiteModeSettingsActivity();
            ProfileActivity.this.presentFragment(liteModeSettingsActivity);
            liteModeSettingsActivity.setExpanded(28700, true);
            liteModeSettingsActivity.scrollToFlags(8200);
        }

        public void lambda$onCreateSearchArray$126() {
            LiteModeSettingsActivity liteModeSettingsActivity = new LiteModeSettingsActivity();
            ProfileActivity.this.presentFragment(liteModeSettingsActivity);
            liteModeSettingsActivity.setExpanded(28700, true);
            liteModeSettingsActivity.scrollToFlags(4112);
        }

        public void lambda$onCreateSearchArray$127() {
            LiteModeSettingsActivity liteModeSettingsActivity = new LiteModeSettingsActivity();
            ProfileActivity.this.presentFragment(liteModeSettingsActivity);
            liteModeSettingsActivity.scrollToFlags(98784);
        }

        public void lambda$onCreateSearchArray$128() {
            LiteModeSettingsActivity liteModeSettingsActivity = new LiteModeSettingsActivity();
            ProfileActivity.this.presentFragment(liteModeSettingsActivity);
            liteModeSettingsActivity.setExpanded(98784, true);
            liteModeSettingsActivity.scrollToFlags(32);
        }

        public void lambda$onCreateSearchArray$129() {
            LiteModeSettingsActivity liteModeSettingsActivity = new LiteModeSettingsActivity();
            ProfileActivity.this.presentFragment(liteModeSettingsActivity);
            liteModeSettingsActivity.setExpanded(98784, true);
            liteModeSettingsActivity.scrollToFlags(64);
        }

        public void lambda$onCreateSearchArray$13() {
            ProfileActivity.this.presentFragment(new NotificationsSettingsActivity());
        }

        public void lambda$onCreateSearchArray$130() {
            LiteModeSettingsActivity liteModeSettingsActivity = new LiteModeSettingsActivity();
            ProfileActivity.this.presentFragment(liteModeSettingsActivity);
            liteModeSettingsActivity.setExpanded(98784, true);
            liteModeSettingsActivity.scrollToFlags(128);
        }

        public void lambda$onCreateSearchArray$131() {
            LiteModeSettingsActivity liteModeSettingsActivity = new LiteModeSettingsActivity();
            ProfileActivity.this.presentFragment(liteModeSettingsActivity);
            liteModeSettingsActivity.setExpanded(98784, true);
            liteModeSettingsActivity.scrollToFlags(256);
        }

        public void lambda$onCreateSearchArray$132() {
            LiteModeSettingsActivity liteModeSettingsActivity = new LiteModeSettingsActivity();
            ProfileActivity.this.presentFragment(liteModeSettingsActivity);
            liteModeSettingsActivity.setExpanded(98784, true);
            liteModeSettingsActivity.scrollToFlags(32768);
        }

        public void lambda$onCreateSearchArray$133() {
            LiteModeSettingsActivity liteModeSettingsActivity = new LiteModeSettingsActivity();
            ProfileActivity.this.presentFragment(liteModeSettingsActivity);
            liteModeSettingsActivity.scrollToFlags(512);
        }

        public void lambda$onCreateSearchArray$134() {
            LiteModeSettingsActivity liteModeSettingsActivity = new LiteModeSettingsActivity();
            ProfileActivity.this.presentFragment(liteModeSettingsActivity);
            liteModeSettingsActivity.scrollToFlags(1024);
        }

        public void lambda$onCreateSearchArray$135() {
            LiteModeSettingsActivity liteModeSettingsActivity = new LiteModeSettingsActivity();
            ProfileActivity.this.presentFragment(liteModeSettingsActivity);
            liteModeSettingsActivity.scrollToFlags(2048);
        }

        public void lambda$onCreateSearchArray$136() {
            LiteModeSettingsActivity liteModeSettingsActivity = new LiteModeSettingsActivity();
            ProfileActivity.this.presentFragment(liteModeSettingsActivity);
            liteModeSettingsActivity.scrollToType(1);
        }

        public void lambda$onCreateSearchArray$137() {
            ProfileActivity.this.presentFragment(new LanguageSelectActivity());
        }

        public void lambda$onCreateSearchArray$138() {
            ProfileActivity.this.presentFragment(new LanguageSelectActivity());
        }

        public void lambda$onCreateSearchArray$139() {
            ProfileActivity.this.presentFragment(new LanguageSelectActivity());
        }

        public void lambda$onCreateSearchArray$14() {
            ProfileActivity.this.presentFragment(new NotificationsSettingsActivity());
        }

        public void lambda$onCreateSearchArray$140() {
            ProfileActivity profileActivity = ProfileActivity.this;
            profileActivity.showDialog(AlertsCreator.createSupportAlert(profileActivity, null));
        }

        public void lambda$onCreateSearchArray$141() {
            Browser.openUrl(ProfileActivity.this.getParentActivity(), LocaleController.getString(R.string.TelegramFaqUrl));
        }

        public void lambda$onCreateSearchArray$142() {
            Browser.openUrl(ProfileActivity.this.getParentActivity(), LocaleController.getString(R.string.PrivacyPolicyUrl));
        }

        public void lambda$onCreateSearchArray$15() {
            ProfileActivity.this.presentFragment(new NotificationsSettingsActivity());
        }

        public void lambda$onCreateSearchArray$16() {
            ProfileActivity.this.presentFragment(new NotificationsSettingsActivity());
        }

        public void lambda$onCreateSearchArray$17() {
            ProfileActivity.this.presentFragment(new NotificationsSettingsActivity());
        }

        public void lambda$onCreateSearchArray$18() {
            ProfileActivity.this.presentFragment(new NotificationsSettingsActivity());
        }

        public void lambda$onCreateSearchArray$19() {
            ProfileActivity.this.presentFragment(new PrivacySettingsActivity());
        }

        public void lambda$onCreateSearchArray$2() {
            ProfileActivity.this.presentFragment(new ActionIntroActivity(3));
        }

        public void lambda$onCreateSearchArray$20() {
            ProfileActivity.this.presentFragment(new TwoStepVerificationActivity());
        }

        public void lambda$onCreateSearchArray$21() {
            if (ProfileActivity.this.getUserConfig().getGlobalTTl() >= 0) {
                ProfileActivity.this.presentFragment(new AutoDeleteMessagesActivity());
            }
        }

        public void lambda$onCreateSearchArray$22() {
            ProfileActivity.this.presentFragment(PasscodeActivity.determineOpenFragment());
        }

        public void lambda$onCreateSearchArray$23() {
            ProfileActivity.this.presentFragment(new PrivacySettingsActivity());
        }

        public void lambda$onCreateSearchArray$24() {
            ProfileActivity.this.presentFragment(new PrivacyUsersActivity().loadBlocked());
        }

        public void lambda$onCreateSearchArray$25() {
            ProfileActivity.this.presentFragment(new SessionsActivity(0));
        }

        public void lambda$onCreateSearchArray$26() {
            ProfileActivity.this.presentFragment(new PrivacyControlActivity(6, true));
        }

        public void lambda$onCreateSearchArray$27() {
            ProfileActivity.this.presentFragment(new PrivacyControlActivity(0, true));
        }

        public void lambda$onCreateSearchArray$28() {
            ProfileActivity.this.presentFragment(new PrivacyControlActivity(4, true));
        }

        public void lambda$onCreateSearchArray$29() {
            ProfileActivity.this.presentFragment(new PrivacyControlActivity(5, true));
        }

        public void lambda$onCreateSearchArray$3() {
            int i = 0;
            while (true) {
                if (i >= 4) {
                    i = -1;
                    break;
                } else if (!UserConfig.getInstance(i).isClientActivated()) {
                    break;
                } else {
                    i++;
                }
            }
            if (i >= 0) {
                ProfileActivity.this.presentFragment(new LoginActivity(i));
            }
        }

        public void lambda$onCreateSearchArray$30() {
            ProfileActivity.this.presentFragment(new PrivacyControlActivity(3, true));
        }

        public void lambda$onCreateSearchArray$31() {
            ProfileActivity.this.presentFragment(new PrivacyControlActivity(2, true));
        }

        public void lambda$onCreateSearchArray$32() {
            ProfileActivity.this.presentFragment(new PrivacyControlActivity(1, true));
        }

        public void lambda$onCreateSearchArray$33() {
            if (ProfileActivity.this.getUserConfig().isPremium()) {
                ProfileActivity.this.presentFragment(new PrivacyControlActivity(8, true));
            } else {
                try {
                    ProfileActivity.this.fragmentView.performHapticFeedback(3, 2);
                } catch (Exception unused) {
                }
                BulletinFactory.of(ProfileActivity.this).createRestrictVoiceMessagesPremiumBulletin().show();
            }
        }

        public void lambda$onCreateSearchArray$34() {
            ProfileActivity.this.presentFragment(new PrivacySettingsActivity());
        }

        public void lambda$onCreateSearchArray$35() {
            ProfileActivity.this.presentFragment(new PrivacySettingsActivity());
        }

        public void lambda$onCreateSearchArray$36() {
            ProfileActivity.this.presentFragment(new PrivacySettingsActivity());
        }

        public void lambda$onCreateSearchArray$37() {
            ProfileActivity.this.presentFragment(new SessionsActivity(1));
        }

        public void lambda$onCreateSearchArray$38() {
            ProfileActivity.this.presentFragment(new PrivacySettingsActivity());
        }

        public void lambda$onCreateSearchArray$39() {
            ProfileActivity.this.presentFragment(new PrivacySettingsActivity());
        }

        public void lambda$onCreateSearchArray$4() {
            if (ProfileActivity.this.userInfo != null) {
                ProfileActivity.this.presentFragment(new ChangeBioActivity());
            }
        }

        public void lambda$onCreateSearchArray$40() {
            ProfileActivity.this.presentFragment(new PrivacySettingsActivity());
        }

        public void lambda$onCreateSearchArray$41() {
            ProfileActivity.this.presentFragment(new PrivacySettingsActivity());
        }

        public void lambda$onCreateSearchArray$42() {
            ProfileActivity.this.presentFragment(new PrivacySettingsActivity());
        }

        public void lambda$onCreateSearchArray$43() {
            ProfileActivity.this.presentFragment(new SessionsActivity(0));
        }

        public void lambda$onCreateSearchArray$44() {
            ProfileActivity.this.presentFragment(new SessionsActivity(0));
        }

        public void lambda$onCreateSearchArray$45() {
            ProfileActivity.this.presentFragment(new SessionsActivity(0).setHighlightLinkDesktopDevice());
        }

        public void lambda$onCreateSearchArray$46() {
            ProfileActivity.this.presentFragment(new DataSettingsActivity());
        }

        public void lambda$onCreateSearchArray$47() {
            ProfileActivity.this.presentFragment(new DataSettingsActivity());
        }

        public void lambda$onCreateSearchArray$48() {
            ProfileActivity.this.presentFragment(new CacheControlActivity());
        }

        public void lambda$onCreateSearchArray$49() {
            ProfileActivity.this.presentFragment(new CacheControlActivity());
        }

        public void lambda$onCreateSearchArray$50() {
            ProfileActivity.this.presentFragment(new CacheControlActivity());
        }

        public void lambda$onCreateSearchArray$51() {
            ProfileActivity.this.presentFragment(new CacheControlActivity());
        }

        public void lambda$onCreateSearchArray$52() {
            ProfileActivity.this.presentFragment(new DataUsage2Activity());
        }

        public void lambda$onCreateSearchArray$53() {
            ProfileActivity.this.presentFragment(new DataSettingsActivity());
        }

        public void lambda$onCreateSearchArray$54() {
            ProfileActivity.this.presentFragment(new DataAutoDownloadActivity(0));
        }

        public void lambda$onCreateSearchArray$55() {
            ProfileActivity.this.presentFragment(new DataAutoDownloadActivity(1));
        }

        public void lambda$onCreateSearchArray$56() {
            ProfileActivity.this.presentFragment(new DataAutoDownloadActivity(2));
        }

        public void lambda$onCreateSearchArray$57() {
            ProfileActivity.this.presentFragment(new DataSettingsActivity());
        }

        public void lambda$onCreateSearchArray$58() {
            ProfileActivity.this.presentFragment(new DataSettingsActivity());
        }

        public void lambda$onCreateSearchArray$59() {
            ProfileActivity.this.presentFragment(new DataSettingsActivity());
        }

        public void lambda$onCreateSearchArray$6() {
            ProfileActivity.this.presentFragment(new NotificationsSettingsActivity());
        }

        public void lambda$onCreateSearchArray$60() {
            ProfileActivity.this.presentFragment(new DataSettingsActivity());
        }

        public void lambda$onCreateSearchArray$61() {
            ProfileActivity.this.presentFragment(new DataSettingsActivity());
        }

        public void lambda$onCreateSearchArray$62() {
            ProfileActivity.this.presentFragment(new DataSettingsActivity());
        }

        public void lambda$onCreateSearchArray$63() {
            ProfileActivity.this.presentFragment(new ProxyListActivity());
        }

        public void lambda$onCreateSearchArray$64() {
            ProfileActivity.this.presentFragment(new ProxyListActivity());
        }

        public void lambda$onCreateSearchArray$65() {
            ProfileActivity.this.presentFragment(new DataSettingsActivity());
        }

        public void lambda$onCreateSearchArray$66() {
            ProfileActivity.this.presentFragment(new DataSettingsActivity());
        }

        public void lambda$onCreateSearchArray$67() {
            ProfileActivity.this.presentFragment(new DataSettingsActivity());
        }

        public void lambda$onCreateSearchArray$68() {
            ProfileActivity.this.presentFragment(new DataSettingsActivity());
        }

        public void lambda$onCreateSearchArray$69() {
            ProfileActivity.this.presentFragment(new DataSettingsActivity());
        }

        public void lambda$onCreateSearchArray$7() {
            ProfileActivity.this.presentFragment(new NotificationsCustomSettingsActivity(1, new ArrayList(), null, true));
        }

        public void lambda$onCreateSearchArray$70() {
            ProfileActivity.this.presentFragment(new ThemeActivity(0));
        }

        public void lambda$onCreateSearchArray$71() {
            ProfileActivity.this.presentFragment(new ThemeActivity(0));
        }

        public void lambda$onCreateSearchArray$72() {
            ProfileActivity.this.presentFragment(new WallpapersListActivity(0));
        }

        public void lambda$onCreateSearchArray$73() {
            ProfileActivity.this.presentFragment(new WallpapersListActivity(1));
        }

        public void lambda$onCreateSearchArray$74() {
            ProfileActivity.this.presentFragment(new WallpapersListActivity(0));
        }

        public void lambda$onCreateSearchArray$75() {
            ProfileActivity.this.presentFragment(new ThemeActivity(0));
        }

        public void lambda$onCreateSearchArray$76() {
            ProfileActivity.this.presentFragment(new ThemeActivity(3));
        }

        public void lambda$onCreateSearchArray$77() {
            ProfileActivity.this.presentFragment(new ThemeActivity(3));
        }

        public void lambda$onCreateSearchArray$78() {
            ProfileActivity.this.presentFragment(new ThemeActivity(0));
        }

        public void lambda$onCreateSearchArray$79() {
            ProfileActivity.this.presentFragment(new ThemeActivity(0));
        }

        public void lambda$onCreateSearchArray$8() {
            ProfileActivity.this.presentFragment(new NotificationsCustomSettingsActivity(0, new ArrayList(), null, true));
        }

        public void lambda$onCreateSearchArray$80() {
            ProfileActivity.this.presentFragment(new ThemeActivity(0));
        }

        public void lambda$onCreateSearchArray$81() {
            ProfileActivity.this.presentFragment(new ThemeActivity(0));
        }

        public void lambda$onCreateSearchArray$82() {
            ProfileActivity.this.presentFragment(new ThemeActivity(1));
        }

        public void lambda$onCreateSearchArray$83() {
            ProfileActivity.this.presentFragment(new ThemeActivity(0));
        }

        public void lambda$onCreateSearchArray$84() {
            ProfileActivity.this.presentFragment(new ThemeActivity(0));
        }

        public void lambda$onCreateSearchArray$85() {
            ProfileActivity.this.presentFragment(new ThemeActivity(0));
        }

        public void lambda$onCreateSearchArray$86() {
            ProfileActivity.this.presentFragment(new ThemeActivity(0));
        }

        public void lambda$onCreateSearchArray$87() {
            ProfileActivity.this.presentFragment(new ThemeActivity(0));
        }

        public void lambda$onCreateSearchArray$88() {
            ProfileActivity.this.presentFragment(new ThemeActivity(0));
        }

        public void lambda$onCreateSearchArray$89() {
            ProfileActivity.this.presentFragment(new ThemeActivity(0));
        }

        public void lambda$onCreateSearchArray$9() {
            ProfileActivity.this.presentFragment(new NotificationsCustomSettingsActivity(2, new ArrayList(), null, true));
        }

        public void lambda$onCreateSearchArray$90() {
            ProfileActivity.this.presentFragment(new ThemeActivity(0));
        }

        public void lambda$onCreateSearchArray$91() {
            ProfileActivity.this.presentFragment(new StickersActivity(0, null));
        }

        public void lambda$onCreateSearchArray$92() {
            ProfileActivity.this.presentFragment(new StickersActivity(0, null));
        }

        public void lambda$onCreateSearchArray$93() {
            ProfileActivity.this.presentFragment(new StickersActivity(0, null));
        }

        public void lambda$onCreateSearchArray$94() {
            ProfileActivity.this.presentFragment(new StickersActivity(1, null));
        }

        public void lambda$onCreateSearchArray$95() {
            ProfileActivity.this.presentFragment(new ArchivedStickersActivity(0));
        }

        public void lambda$onCreateSearchArray$96() {
            ProfileActivity.this.presentFragment(new ArchivedStickersActivity(1));
        }

        public void lambda$onCreateSearchArray$97() {
            ProfileActivity.this.presentFragment(new StickersActivity(0, null));
        }

        public void lambda$onCreateSearchArray$98() {
            ProfileActivity.this.presentFragment(new StickersActivity(0, null));
        }

        public void lambda$onCreateSearchArray$99() {
            ProfileActivity.this.presentFragment(new StickersActivity(5, null));
        }

        public void lambda$search$145(String str, ArrayList arrayList, ArrayList arrayList2, ArrayList arrayList3) {
            if (str.equals(this.lastSearchString)) {
                if (!this.searchWas) {
                    ProfileActivity.this.emptyView.stickerView.getImageReceiver().startAnimation();
                    ProfileActivity.this.emptyView.title.setText(LocaleController.getString(R.string.SettingsNoResults));
                }
                this.searchWas = true;
                this.searchResults = arrayList;
                this.faqSearchResults = arrayList2;
                this.resultNames = arrayList3;
                notifyDataSetChanged();
                ProfileActivity.this.emptyView.stickerView.getImageReceiver().startAnimation();
            }
        }

        public void lambda$search$146(final String str) {
            SpannableStringBuilder spannableStringBuilder;
            String str2;
            int i;
            String str3;
            String str4;
            String str5;
            final ArrayList arrayList = new ArrayList();
            final ArrayList arrayList2 = new ArrayList();
            final ArrayList arrayList3 = new ArrayList();
            String str6 = " ";
            String[] split = str.split(" ");
            String[] strArr = new String[split.length];
            int i2 = 0;
            while (true) {
                spannableStringBuilder = null;
                if (i2 >= split.length) {
                    break;
                }
                String translitString = LocaleController.getInstance().getTranslitString(split[i2]);
                strArr[i2] = translitString;
                if (translitString.equals(split[i2])) {
                    strArr[i2] = null;
                }
                i2++;
            }
            int i3 = 0;
            while (true) {
                SearchResult[] searchResultArr = this.searchArray;
                if (i3 >= searchResultArr.length) {
                    break;
                }
                SearchResult searchResult = searchResultArr[i3];
                if (searchResult != null) {
                    String str7 = " " + searchResult.searchTitle.toLowerCase();
                    SpannableStringBuilder spannableStringBuilder2 = spannableStringBuilder;
                    int i4 = 0;
                    while (i4 < split.length) {
                        if (split[i4].length() != 0) {
                            String str8 = split[i4];
                            int indexOf = str7.indexOf(" " + str8);
                            if (indexOf < 0 && (str5 = strArr[i4]) != null) {
                                indexOf = str7.indexOf(" " + str5);
                                str8 = str5;
                            }
                            if (indexOf >= 0) {
                                if (spannableStringBuilder2 == null) {
                                    spannableStringBuilder2 = new SpannableStringBuilder(searchResult.searchTitle);
                                }
                                str4 = str7;
                                spannableStringBuilder2.setSpan(new ForegroundColorSpan(ProfileActivity.this.getThemedColor(Theme.key_windowBackgroundWhiteBlueText4)), indexOf, str8.length() + indexOf, 33);
                            }
                        } else {
                            str4 = str7;
                        }
                        if (spannableStringBuilder2 != null && i4 == split.length - 1) {
                            if (searchResult.guid == 502) {
                                int i5 = 0;
                                while (true) {
                                    if (i5 >= 4) {
                                        i5 = -1;
                                        break;
                                    } else if (!UserConfig.getInstance(i5).isClientActivated()) {
                                        break;
                                    } else {
                                        i5++;
                                    }
                                }
                                if (i5 < 0) {
                                }
                            }
                            arrayList.add(searchResult);
                            arrayList3.add(spannableStringBuilder2);
                        }
                        i4++;
                        str7 = str4;
                    }
                }
                i3++;
                spannableStringBuilder = null;
            }
            if (this.faqWebPage != null) {
                int size = this.faqSearchArray.size();
                int i6 = 0;
                while (i6 < size) {
                    MessagesController.FaqSearchResult faqSearchResult = (MessagesController.FaqSearchResult) this.faqSearchArray.get(i6);
                    String str9 = str6 + faqSearchResult.title.toLowerCase();
                    int i7 = 0;
                    SpannableStringBuilder spannableStringBuilder3 = null;
                    while (i7 < split.length) {
                        if (split[i7].length() != 0) {
                            String str10 = split[i7];
                            int indexOf2 = str9.indexOf(str6 + str10);
                            if (indexOf2 < 0 && (str3 = strArr[i7]) != null) {
                                indexOf2 = str9.indexOf(str6 + str3);
                                str10 = str3;
                            }
                            if (indexOf2 < 0) {
                                break;
                            }
                            if (spannableStringBuilder3 == null) {
                                spannableStringBuilder3 = new SpannableStringBuilder(faqSearchResult.title);
                            }
                            str2 = str6;
                            i = size;
                            spannableStringBuilder3.setSpan(new ForegroundColorSpan(ProfileActivity.this.getThemedColor(Theme.key_windowBackgroundWhiteBlueText4)), indexOf2, str10.length() + indexOf2, 33);
                        } else {
                            str2 = str6;
                            i = size;
                        }
                        if (spannableStringBuilder3 != null && i7 == split.length - 1) {
                            arrayList2.add(faqSearchResult);
                            arrayList3.add(spannableStringBuilder3);
                        }
                        i7++;
                        str6 = str2;
                        size = i;
                    }
                    i6++;
                    str6 = str6;
                    size = size;
                }
            }
            AndroidUtilities.runOnUIThread(new Runnable() {
                @Override
                public final void run() {
                    ProfileActivity.SearchAdapter.this.lambda$search$145(str, arrayList, arrayList2, arrayList3);
                }
            });
        }

        public int lambda$updateSearchArray$0(Object obj, Object obj2) {
            int num = getNum(obj);
            int num2 = getNum(obj2);
            if (num < num2) {
                return -1;
            }
            return num > num2 ? 1 : 0;
        }

        public void loadFaqWebPage() {
            TLRPC.WebPage webPage = ProfileActivity.this.getMessagesController().faqWebPage;
            this.faqWebPage = webPage;
            if (webPage != null) {
                this.faqSearchArray.addAll(ProfileActivity.this.getMessagesController().faqSearchArray);
            }
            if (this.faqWebPage != null || this.loadingFaqPage) {
                return;
            }
            this.loadingFaqPage = true;
            TLRPC.TL_messages_getWebPage tL_messages_getWebPage = new TLRPC.TL_messages_getWebPage();
            tL_messages_getWebPage.url = LocaleController.getString(R.string.TelegramFaqUrl);
            tL_messages_getWebPage.hash = 0;
            ProfileActivity.this.getConnectionsManager().sendRequest(tL_messages_getWebPage, new RequestDelegate() {
                @Override
                public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                    ProfileActivity.SearchAdapter.this.lambda$loadFaqWebPage$144(tLObject, tL_error);
                }
            });
        }

        public SearchResult[] onCreateSearchArray() {
            SearchResult searchResult = new SearchResult(this, 500, LocaleController.getString(R.string.EditName), 0, new Runnable() {
                @Override
                public final void run() {
                    ProfileActivity.SearchAdapter.this.lambda$onCreateSearchArray$1();
                }
            });
            SearchResult searchResult2 = new SearchResult(this, 501, LocaleController.getString(R.string.ChangePhoneNumber), 0, new Runnable() {
                @Override
                public final void run() {
                    ProfileActivity.SearchAdapter.this.lambda$onCreateSearchArray$2();
                }
            });
            SearchResult searchResult3 = new SearchResult(this, 502, LocaleController.getString(R.string.AddAnotherAccount), 0, new Runnable() {
                @Override
                public final void run() {
                    ProfileActivity.SearchAdapter.this.lambda$onCreateSearchArray$3();
                }
            });
            SearchResult searchResult4 = new SearchResult(this, 503, LocaleController.getString(R.string.UserBio), 0, new Runnable() {
                @Override
                public final void run() {
                    ProfileActivity.SearchAdapter.this.lambda$onCreateSearchArray$4();
                }
            });
            String string = LocaleController.getString(R.string.AddPhoto);
            final ProfileActivity profileActivity = ProfileActivity.this;
            SearchResult searchResult5 = new SearchResult(this, 504, string, 0, new Runnable() {
                @Override
                public final void run() {
                    ProfileActivity.access$7700(ProfileActivity.this);
                }
            });
            int i = R.string.NotificationsAndSounds;
            String string2 = LocaleController.getString(i);
            int i2 = R.drawable.msg_notifications;
            SearchResult searchResult6 = new SearchResult(this, 1, string2, i2, new Runnable() {
                @Override
                public final void run() {
                    ProfileActivity.SearchAdapter.this.lambda$onCreateSearchArray$6();
                }
            });
            SearchResult searchResult7 = new SearchResult(this, 2, LocaleController.getString(R.string.NotificationsPrivateChats), LocaleController.getString(i), i2, new Runnable() {
                @Override
                public final void run() {
                    ProfileActivity.SearchAdapter.this.lambda$onCreateSearchArray$7();
                }
            });
            SearchResult searchResult8 = new SearchResult(this, 3, LocaleController.getString(R.string.NotificationsGroups), LocaleController.getString(i), i2, new Runnable() {
                @Override
                public final void run() {
                    ProfileActivity.SearchAdapter.this.lambda$onCreateSearchArray$8();
                }
            });
            SearchResult searchResult9 = new SearchResult(this, 4, LocaleController.getString(R.string.NotificationsChannels), LocaleController.getString(i), i2, new Runnable() {
                @Override
                public final void run() {
                    ProfileActivity.SearchAdapter.this.lambda$onCreateSearchArray$9();
                }
            });
            SearchResult searchResult10 = new SearchResult(this, 5, LocaleController.getString(R.string.VoipNotificationSettings), "callsSectionRow", LocaleController.getString(i), i2, new Runnable() {
                @Override
                public final void run() {
                    ProfileActivity.SearchAdapter.this.lambda$onCreateSearchArray$10();
                }
            });
            SearchResult searchResult11 = new SearchResult(this, 6, LocaleController.getString(R.string.BadgeNumber), "badgeNumberSection", LocaleController.getString(i), i2, new Runnable() {
                @Override
                public final void run() {
                    ProfileActivity.SearchAdapter.this.lambda$onCreateSearchArray$11();
                }
            });
            SearchResult searchResult12 = new SearchResult(this, 7, LocaleController.getString(R.string.InAppNotifications), "inappSectionRow", LocaleController.getString(i), i2, new Runnable() {
                @Override
                public final void run() {
                    ProfileActivity.SearchAdapter.this.lambda$onCreateSearchArray$12();
                }
            });
            SearchResult searchResult13 = new SearchResult(this, 8, LocaleController.getString(R.string.ContactJoined), "contactJoinedRow", LocaleController.getString(i), i2, new Runnable() {
                @Override
                public final void run() {
                    ProfileActivity.SearchAdapter.this.lambda$onCreateSearchArray$13();
                }
            });
            SearchResult searchResult14 = new SearchResult(this, 9, LocaleController.getString(R.string.PinnedMessages), "pinnedMessageRow", LocaleController.getString(i), i2, new Runnable() {
                @Override
                public final void run() {
                    ProfileActivity.SearchAdapter.this.lambda$onCreateSearchArray$14();
                }
            });
            SearchResult searchResult15 = new SearchResult(this, 10, LocaleController.getString(R.string.ResetAllNotifications), "resetNotificationsRow", LocaleController.getString(i), i2, new Runnable() {
                @Override
                public final void run() {
                    ProfileActivity.SearchAdapter.this.lambda$onCreateSearchArray$15();
                }
            });
            SearchResult searchResult16 = new SearchResult(this, 11, LocaleController.getString(R.string.NotificationsService), "notificationsServiceRow", LocaleController.getString(i), i2, new Runnable() {
                @Override
                public final void run() {
                    ProfileActivity.SearchAdapter.this.lambda$onCreateSearchArray$16();
                }
            });
            SearchResult searchResult17 = new SearchResult(this, 12, LocaleController.getString(R.string.NotificationsServiceConnection), "notificationsServiceConnectionRow", LocaleController.getString(i), i2, new Runnable() {
                @Override
                public final void run() {
                    ProfileActivity.SearchAdapter.this.lambda$onCreateSearchArray$17();
                }
            });
            SearchResult searchResult18 = new SearchResult(this, 13, LocaleController.getString(R.string.RepeatNotifications), "repeatRow", LocaleController.getString(i), i2, new Runnable() {
                @Override
                public final void run() {
                    ProfileActivity.SearchAdapter.this.lambda$onCreateSearchArray$18();
                }
            });
            int i3 = R.string.PrivacySettings;
            String string3 = LocaleController.getString(i3);
            int i4 = R.drawable.msg_secret;
            SearchResult searchResult19 = new SearchResult(this, 100, string3, i4, new Runnable() {
                @Override
                public final void run() {
                    ProfileActivity.SearchAdapter.this.lambda$onCreateSearchArray$19();
                }
            });
            String string4 = LocaleController.getString(R.string.TwoStepVerification);
            String string5 = LocaleController.getString(i3);
            int i5 = R.drawable.msg2_secret;
            SearchResult searchResult20 = new SearchResult(this, 109, string4, string5, i5, new Runnable() {
                @Override
                public final void run() {
                    ProfileActivity.SearchAdapter.this.lambda$onCreateSearchArray$20();
                }
            });
            SearchResult searchResult21 = new SearchResult(this, 124, LocaleController.getString(R.string.AutoDeleteMessages), LocaleController.getString(i3), i5, new Runnable() {
                @Override
                public final void run() {
                    ProfileActivity.SearchAdapter.this.lambda$onCreateSearchArray$21();
                }
            });
            SearchResult searchResult22 = new SearchResult(this, 108, LocaleController.getString(R.string.Passcode), LocaleController.getString(i3), i5, new Runnable() {
                @Override
                public final void run() {
                    ProfileActivity.SearchAdapter.this.lambda$onCreateSearchArray$22();
                }
            });
            SearchResult searchResult23 = SharedConfig.hasEmailLogin ? new SearchResult(this, 125, LocaleController.getString(R.string.EmailLogin), "emailLoginRow", LocaleController.getString(i3), i5, new Runnable() {
                @Override
                public final void run() {
                    ProfileActivity.SearchAdapter.this.lambda$onCreateSearchArray$23();
                }
            }) : null;
            SearchResult searchResult24 = new SearchResult(this, 101, LocaleController.getString(R.string.BlockedUsers), LocaleController.getString(i3), i4, new Runnable() {
                @Override
                public final void run() {
                    ProfileActivity.SearchAdapter.this.lambda$onCreateSearchArray$24();
                }
            });
            SearchResult searchResult25 = new SearchResult(this, 110, LocaleController.getString(R.string.SessionsTitle), i5, new Runnable() {
                @Override
                public final void run() {
                    ProfileActivity.SearchAdapter.this.lambda$onCreateSearchArray$25();
                }
            });
            SearchResult searchResult26 = new SearchResult(this, 105, LocaleController.getString(R.string.PrivacyPhone), LocaleController.getString(i3), i4, new Runnable() {
                @Override
                public final void run() {
                    ProfileActivity.SearchAdapter.this.lambda$onCreateSearchArray$26();
                }
            });
            SearchResult searchResult27 = new SearchResult(this, 102, LocaleController.getString(R.string.PrivacyLastSeen), LocaleController.getString(i3), i4, new Runnable() {
                @Override
                public final void run() {
                    ProfileActivity.SearchAdapter.this.lambda$onCreateSearchArray$27();
                }
            });
            SearchResult searchResult28 = new SearchResult(this, 103, LocaleController.getString(R.string.PrivacyProfilePhoto), LocaleController.getString(i3), i4, new Runnable() {
                @Override
                public final void run() {
                    ProfileActivity.SearchAdapter.this.lambda$onCreateSearchArray$28();
                }
            });
            SearchResult searchResult29 = new SearchResult(this, 104, LocaleController.getString(R.string.PrivacyForwards), LocaleController.getString(i3), i4, new Runnable() {
                @Override
                public final void run() {
                    ProfileActivity.SearchAdapter.this.lambda$onCreateSearchArray$29();
                }
            });
            SearchResult searchResult30 = new SearchResult(this, 122, LocaleController.getString(R.string.PrivacyP2P), LocaleController.getString(i3), i4, new Runnable() {
                @Override
                public final void run() {
                    ProfileActivity.SearchAdapter.this.lambda$onCreateSearchArray$30();
                }
            });
            int i6 = R.string.Calls;
            SearchResult searchResult31 = new SearchResult(this, 106, LocaleController.getString(i6), LocaleController.getString(i3), i4, new Runnable() {
                @Override
                public final void run() {
                    ProfileActivity.SearchAdapter.this.lambda$onCreateSearchArray$31();
                }
            });
            SearchResult searchResult32 = new SearchResult(this, 107, LocaleController.getString(R.string.PrivacyInvites), LocaleController.getString(i3), i4, new Runnable() {
                @Override
                public final void run() {
                    ProfileActivity.SearchAdapter.this.lambda$onCreateSearchArray$32();
                }
            });
            SearchResult searchResult33 = new SearchResult(this, 123, LocaleController.getString(R.string.PrivacyVoiceMessages), LocaleController.getString(i3), i4, new Runnable() {
                @Override
                public final void run() {
                    ProfileActivity.SearchAdapter.this.lambda$onCreateSearchArray$33();
                }
            });
            SearchResult searchResult34 = ProfileActivity.this.getMessagesController().autoarchiveAvailable ? new SearchResult(this, 121, LocaleController.getString(R.string.ArchiveAndMute), "newChatsRow", LocaleController.getString(i3), i5, new Runnable() {
                @Override
                public final void run() {
                    ProfileActivity.SearchAdapter.this.lambda$onCreateSearchArray$34();
                }
            }) : null;
            SearchResult searchResult35 = new SearchResult(this, 112, LocaleController.getString(R.string.DeleteAccountIfAwayFor2), "deleteAccountRow", LocaleController.getString(i3), i5, new Runnable() {
                @Override
                public final void run() {
                    ProfileActivity.SearchAdapter.this.lambda$onCreateSearchArray$35();
                }
            });
            SearchResult searchResult36 = new SearchResult(this, 113, LocaleController.getString(R.string.PrivacyPaymentsClear), "paymentsClearRow", LocaleController.getString(i3), i5, new Runnable() {
                @Override
                public final void run() {
                    ProfileActivity.SearchAdapter.this.lambda$onCreateSearchArray$36();
                }
            });
            SearchResult searchResult37 = new SearchResult(this, 114, LocaleController.getString(R.string.WebSessionsTitle), LocaleController.getString(i3), i5, new Runnable() {
                @Override
                public final void run() {
                    ProfileActivity.SearchAdapter.this.lambda$onCreateSearchArray$37();
                }
            });
            SearchResult searchResult38 = new SearchResult(this, 115, LocaleController.getString(R.string.SyncContactsDelete), "contactsDeleteRow", LocaleController.getString(i3), i5, new Runnable() {
                @Override
                public final void run() {
                    ProfileActivity.SearchAdapter.this.lambda$onCreateSearchArray$38();
                }
            });
            SearchResult searchResult39 = new SearchResult(this, 116, LocaleController.getString(R.string.SyncContacts), "contactsSyncRow", LocaleController.getString(i3), i5, new Runnable() {
                @Override
                public final void run() {
                    ProfileActivity.SearchAdapter.this.lambda$onCreateSearchArray$39();
                }
            });
            SearchResult searchResult40 = new SearchResult(this, 117, LocaleController.getString(R.string.SuggestContacts), "contactsSuggestRow", LocaleController.getString(i3), i5, new Runnable() {
                @Override
                public final void run() {
                    ProfileActivity.SearchAdapter.this.lambda$onCreateSearchArray$40();
                }
            });
            SearchResult searchResult41 = new SearchResult(this, 118, LocaleController.getString(R.string.MapPreviewProvider), "secretMapRow", LocaleController.getString(i3), i5, new Runnable() {
                @Override
                public final void run() {
                    ProfileActivity.SearchAdapter.this.lambda$onCreateSearchArray$41();
                }
            });
            SearchResult searchResult42 = new SearchResult(this, 119, LocaleController.getString(R.string.SecretWebPage), "secretWebpageRow", LocaleController.getString(i3), i5, new Runnable() {
                @Override
                public final void run() {
                    ProfileActivity.SearchAdapter.this.lambda$onCreateSearchArray$42();
                }
            });
            int i7 = R.string.Devices;
            String string6 = LocaleController.getString(i7);
            int i8 = R.drawable.msg2_devices;
            SearchResult searchResult43 = new SearchResult(this, 120, string6, i8, new Runnable() {
                @Override
                public final void run() {
                    ProfileActivity.SearchAdapter.this.lambda$onCreateSearchArray$43();
                }
            });
            SearchResult searchResult44 = new SearchResult(this, 121, LocaleController.getString(R.string.TerminateAllSessions), "terminateAllSessionsRow", LocaleController.getString(i7), i8, new Runnable() {
                @Override
                public final void run() {
                    ProfileActivity.SearchAdapter.this.lambda$onCreateSearchArray$44();
                }
            });
            SearchResult searchResult45 = new SearchResult(this, 122, LocaleController.getString(R.string.LinkDesktopDevice), LocaleController.getString(i7), i8, new Runnable() {
                @Override
                public final void run() {
                    ProfileActivity.SearchAdapter.this.lambda$onCreateSearchArray$45();
                }
            });
            int i9 = R.string.DataSettings;
            String string7 = LocaleController.getString(i9);
            int i10 = R.drawable.msg2_data;
            SearchResult searchResult46 = new SearchResult(this, 200, string7, i10, new Runnable() {
                @Override
                public final void run() {
                    ProfileActivity.SearchAdapter.this.lambda$onCreateSearchArray$46();
                }
            });
            SearchResult searchResult47 = new SearchResult(this, 201, LocaleController.getString(R.string.DataUsage), "usageSectionRow", LocaleController.getString(i9), i10, new Runnable() {
                @Override
                public final void run() {
                    ProfileActivity.SearchAdapter.this.lambda$onCreateSearchArray$47();
                }
            });
            int i11 = R.string.StorageUsage;
            SearchResult searchResult48 = new SearchResult(this, 202, LocaleController.getString(i11), LocaleController.getString(i9), i10, new Runnable() {
                @Override
                public final void run() {
                    ProfileActivity.SearchAdapter.this.lambda$onCreateSearchArray$48();
                }
            });
            SearchResult searchResult49 = new SearchResult(203, LocaleController.getString(R.string.KeepMedia), "keepMediaRow", LocaleController.getString(i9), LocaleController.getString(i11), i10, new Runnable() {
                @Override
                public final void run() {
                    ProfileActivity.SearchAdapter.this.lambda$onCreateSearchArray$49();
                }
            });
            SearchResult searchResult50 = new SearchResult(204, LocaleController.getString(R.string.ClearMediaCache), "cacheRow", LocaleController.getString(i9), LocaleController.getString(i11), i10, new Runnable() {
                @Override
                public final void run() {
                    ProfileActivity.SearchAdapter.this.lambda$onCreateSearchArray$50();
                }
            });
            SearchResult searchResult51 = new SearchResult(205, LocaleController.getString(R.string.LocalDatabase), "databaseRow", LocaleController.getString(i9), LocaleController.getString(i11), i10, new Runnable() {
                @Override
                public final void run() {
                    ProfileActivity.SearchAdapter.this.lambda$onCreateSearchArray$51();
                }
            });
            SearchResult searchResult52 = new SearchResult(this, 206, LocaleController.getString(R.string.NetworkUsage), LocaleController.getString(i9), i10, new Runnable() {
                @Override
                public final void run() {
                    ProfileActivity.SearchAdapter.this.lambda$onCreateSearchArray$52();
                }
            });
            SearchResult searchResult53 = new SearchResult(this, 207, LocaleController.getString(R.string.AutomaticMediaDownload), "mediaDownloadSectionRow", LocaleController.getString(i9), i10, new Runnable() {
                @Override
                public final void run() {
                    ProfileActivity.SearchAdapter.this.lambda$onCreateSearchArray$53();
                }
            });
            SearchResult searchResult54 = new SearchResult(this, 208, LocaleController.getString(R.string.WhenUsingMobileData), LocaleController.getString(i9), i10, new Runnable() {
                @Override
                public final void run() {
                    ProfileActivity.SearchAdapter.this.lambda$onCreateSearchArray$54();
                }
            });
            SearchResult searchResult55 = new SearchResult(this, 209, LocaleController.getString(R.string.WhenConnectedOnWiFi), LocaleController.getString(i9), i10, new Runnable() {
                @Override
                public final void run() {
                    ProfileActivity.SearchAdapter.this.lambda$onCreateSearchArray$55();
                }
            });
            SearchResult searchResult56 = new SearchResult(this, 210, LocaleController.getString(R.string.WhenRoaming), LocaleController.getString(i9), i10, new Runnable() {
                @Override
                public final void run() {
                    ProfileActivity.SearchAdapter.this.lambda$onCreateSearchArray$56();
                }
            });
            SearchResult searchResult57 = new SearchResult(this, 211, LocaleController.getString(R.string.ResetAutomaticMediaDownload), "resetDownloadRow", LocaleController.getString(i9), i10, new Runnable() {
                @Override
                public final void run() {
                    ProfileActivity.SearchAdapter.this.lambda$onCreateSearchArray$57();
                }
            });
            SearchResult searchResult58 = new SearchResult(this, 215, LocaleController.getString(R.string.Streaming), "streamSectionRow", LocaleController.getString(i9), i10, new Runnable() {
                @Override
                public final void run() {
                    ProfileActivity.SearchAdapter.this.lambda$onCreateSearchArray$58();
                }
            });
            SearchResult searchResult59 = new SearchResult(this, 216, LocaleController.getString(R.string.EnableStreaming), "enableStreamRow", LocaleController.getString(i9), i10, new Runnable() {
                @Override
                public final void run() {
                    ProfileActivity.SearchAdapter.this.lambda$onCreateSearchArray$59();
                }
            });
            SearchResult searchResult60 = new SearchResult(this, 217, LocaleController.getString(i6), "callsSectionRow", LocaleController.getString(i9), i10, new Runnable() {
                @Override
                public final void run() {
                    ProfileActivity.SearchAdapter.this.lambda$onCreateSearchArray$60();
                }
            });
            SearchResult searchResult61 = new SearchResult(this, 218, LocaleController.getString(R.string.VoipUseLessData), "useLessDataForCallsRow", LocaleController.getString(i9), i10, new Runnable() {
                @Override
                public final void run() {
                    ProfileActivity.SearchAdapter.this.lambda$onCreateSearchArray$61();
                }
            });
            SearchResult searchResult62 = new SearchResult(this, 219, LocaleController.getString(R.string.VoipQuickReplies), "quickRepliesRow", LocaleController.getString(i9), i10, new Runnable() {
                @Override
                public final void run() {
                    ProfileActivity.SearchAdapter.this.lambda$onCreateSearchArray$62();
                }
            });
            int i12 = R.string.ProxySettings;
            SearchResult searchResult63 = new SearchResult(this, 220, LocaleController.getString(i12), LocaleController.getString(i9), i10, new Runnable() {
                @Override
                public final void run() {
                    ProfileActivity.SearchAdapter.this.lambda$onCreateSearchArray$63();
                }
            });
            SearchResult searchResult64 = new SearchResult(221, LocaleController.getString(R.string.UseProxyForCalls), "callsRow", LocaleController.getString(i9), LocaleController.getString(i12), i10, new Runnable() {
                @Override
                public final void run() {
                    ProfileActivity.SearchAdapter.this.lambda$onCreateSearchArray$64();
                }
            });
            SearchResult searchResult65 = new SearchResult(this, 111, LocaleController.getString(R.string.PrivacyDeleteCloudDrafts), "clearDraftsRow", LocaleController.getString(i9), i10, new Runnable() {
                @Override
                public final void run() {
                    ProfileActivity.SearchAdapter.this.lambda$onCreateSearchArray$65();
                }
            });
            int i13 = R.string.SaveToGallery;
            SearchResult searchResult66 = new SearchResult(this, 222, LocaleController.getString(i13), "saveToGallerySectionRow", LocaleController.getString(i9), i10, new Runnable() {
                @Override
                public final void run() {
                    ProfileActivity.SearchAdapter.this.lambda$onCreateSearchArray$66();
                }
            });
            SearchResult searchResult67 = new SearchResult(223, LocaleController.getString(R.string.SaveToGalleryPrivate), "saveToGalleryPeerRow", LocaleController.getString(i9), LocaleController.getString(i13), i10, new Runnable() {
                @Override
                public final void run() {
                    ProfileActivity.SearchAdapter.this.lambda$onCreateSearchArray$67();
                }
            });
            SearchResult searchResult68 = new SearchResult(224, LocaleController.getString(R.string.SaveToGalleryGroups), "saveToGalleryGroupsRow", LocaleController.getString(i9), LocaleController.getString(i13), i10, new Runnable() {
                @Override
                public final void run() {
                    ProfileActivity.SearchAdapter.this.lambda$onCreateSearchArray$68();
                }
            });
            SearchResult searchResult69 = new SearchResult(225, LocaleController.getString(R.string.SaveToGalleryChannels), "saveToGalleryChannelsRow", LocaleController.getString(i9), LocaleController.getString(i13), i10, new Runnable() {
                @Override
                public final void run() {
                    ProfileActivity.SearchAdapter.this.lambda$onCreateSearchArray$69();
                }
            });
            int i14 = R.string.ChatSettings;
            String string8 = LocaleController.getString(i14);
            int i15 = R.drawable.msg2_discussion;
            SearchResult searchResult70 = new SearchResult(this, 300, string8, i15, new Runnable() {
                @Override
                public final void run() {
                    ProfileActivity.SearchAdapter.this.lambda$onCreateSearchArray$70();
                }
            });
            SearchResult searchResult71 = new SearchResult(this, 301, LocaleController.getString(R.string.TextSizeHeader), "textSizeHeaderRow", LocaleController.getString(i14), i15, new Runnable() {
                @Override
                public final void run() {
                    ProfileActivity.SearchAdapter.this.lambda$onCreateSearchArray$71();
                }
            });
            SearchResult searchResult72 = new SearchResult(this, 302, LocaleController.getString(R.string.ChangeChatBackground), LocaleController.getString(i14), i15, new Runnable() {
                @Override
                public final void run() {
                    ProfileActivity.SearchAdapter.this.lambda$onCreateSearchArray$72();
                }
            });
            String string9 = LocaleController.getString(R.string.SetColor);
            String string10 = LocaleController.getString(i14);
            int i16 = R.string.ChatBackground;
            SearchResult searchResult73 = new SearchResult(303, string9, null, string10, LocaleController.getString(i16), i15, new Runnable() {
                @Override
                public final void run() {
                    ProfileActivity.SearchAdapter.this.lambda$onCreateSearchArray$73();
                }
            });
            SearchResult searchResult74 = new SearchResult(304, LocaleController.getString(R.string.ResetChatBackgrounds), "resetRow", LocaleController.getString(i14), LocaleController.getString(i16), i15, new Runnable() {
                @Override
                public final void run() {
                    ProfileActivity.SearchAdapter.this.lambda$onCreateSearchArray$74();
                }
            });
            SearchResult searchResult75 = new SearchResult(this, 306, LocaleController.getString(R.string.ColorTheme), "themeHeaderRow", LocaleController.getString(i14), i15, new Runnable() {
                @Override
                public final void run() {
                    ProfileActivity.SearchAdapter.this.lambda$onCreateSearchArray$75();
                }
            });
            int i17 = R.string.BrowseThemes;
            SearchResult searchResult76 = new SearchResult(this, 319, LocaleController.getString(i17), null, LocaleController.getString(i14), i15, new Runnable() {
                @Override
                public final void run() {
                    ProfileActivity.SearchAdapter.this.lambda$onCreateSearchArray$76();
                }
            });
            SearchResult searchResult77 = new SearchResult(320, LocaleController.getString(R.string.CreateNewTheme), "createNewThemeRow", LocaleController.getString(i14), LocaleController.getString(i17), i15, new Runnable() {
                @Override
                public final void run() {
                    ProfileActivity.SearchAdapter.this.lambda$onCreateSearchArray$77();
                }
            });
            SearchResult searchResult78 = new SearchResult(this, 321, LocaleController.getString(R.string.BubbleRadius), "bubbleRadiusHeaderRow", LocaleController.getString(i14), i15, new Runnable() {
                @Override
                public final void run() {
                    ProfileActivity.SearchAdapter.this.lambda$onCreateSearchArray$78();
                }
            });
            SearchResult searchResult79 = new SearchResult(this, 322, LocaleController.getString(R.string.ChatList), "chatListHeaderRow", LocaleController.getString(i14), i15, new Runnable() {
                @Override
                public final void run() {
                    ProfileActivity.SearchAdapter.this.lambda$onCreateSearchArray$79();
                }
            });
            SearchResult searchResult80 = new SearchResult(this, 323, LocaleController.getString(R.string.ChatListSwipeGesture), "swipeGestureHeaderRow", LocaleController.getString(i14), i15, new Runnable() {
                @Override
                public final void run() {
                    ProfileActivity.SearchAdapter.this.lambda$onCreateSearchArray$80();
                }
            });
            SearchResult searchResult81 = new SearchResult(this, 324, LocaleController.getString(R.string.AppIcon), "appIconHeaderRow", LocaleController.getString(i14), i15, new Runnable() {
                @Override
                public final void run() {
                    ProfileActivity.SearchAdapter.this.lambda$onCreateSearchArray$81();
                }
            });
            SearchResult searchResult82 = new SearchResult(this, 305, LocaleController.getString(R.string.AutoNightTheme), LocaleController.getString(i14), i15, new Runnable() {
                @Override
                public final void run() {
                    ProfileActivity.SearchAdapter.this.lambda$onCreateSearchArray$82();
                }
            });
            SearchResult searchResult83 = new SearchResult(this, 328, LocaleController.getString(R.string.NextMediaTap), "nextMediaTapRow", LocaleController.getString(i14), i15, new Runnable() {
                @Override
                public final void run() {
                    ProfileActivity.SearchAdapter.this.lambda$onCreateSearchArray$83();
                }
            });
            SearchResult searchResult84 = new SearchResult(this, 327, LocaleController.getString(R.string.RaiseToListen), "raiseToListenRow", LocaleController.getString(i14), i15, new Runnable() {
                @Override
                public final void run() {
                    ProfileActivity.SearchAdapter.this.lambda$onCreateSearchArray$84();
                }
            });
            SearchResult searchResult85 = new SearchResult(this, 310, LocaleController.getString(R.string.RaiseToSpeak), "raiseToSpeakRow", LocaleController.getString(i14), i15, new Runnable() {
                @Override
                public final void run() {
                    ProfileActivity.SearchAdapter.this.lambda$onCreateSearchArray$85();
                }
            });
            SearchResult searchResult86 = new SearchResult(this, 326, LocaleController.getString(R.string.PauseMusicOnMedia), "pauseOnMediaRow", LocaleController.getString(i14), i15, new Runnable() {
                @Override
                public final void run() {
                    ProfileActivity.SearchAdapter.this.lambda$onCreateSearchArray$86();
                }
            });
            SearchResult searchResult87 = new SearchResult(this, 325, LocaleController.getString(R.string.MicrophoneForVoiceMessages), "bluetoothScoRow", LocaleController.getString(i14), i15, new Runnable() {
                @Override
                public final void run() {
                    ProfileActivity.SearchAdapter.this.lambda$onCreateSearchArray$87();
                }
            });
            SearchResult searchResult88 = new SearchResult(this, 308, LocaleController.getString(R.string.DirectShare), "directShareRow", LocaleController.getString(i14), i15, new Runnable() {
                @Override
                public final void run() {
                    ProfileActivity.SearchAdapter.this.lambda$onCreateSearchArray$88();
                }
            });
            SearchResult searchResult89 = new SearchResult(this, 311, LocaleController.getString(R.string.SendByEnter), "sendByEnterRow", LocaleController.getString(i14), i15, new Runnable() {
                @Override
                public final void run() {
                    ProfileActivity.SearchAdapter.this.lambda$onCreateSearchArray$89();
                }
            });
            SearchResult searchResult90 = new SearchResult(this, 318, LocaleController.getString(R.string.DistanceUnits), "distanceRow", LocaleController.getString(i14), i15, new Runnable() {
                @Override
                public final void run() {
                    ProfileActivity.SearchAdapter.this.lambda$onCreateSearchArray$90();
                }
            });
            int i18 = R.string.StickersName;
            String string11 = LocaleController.getString(i18);
            int i19 = R.drawable.msg2_sticker;
            SearchResult searchResult91 = new SearchResult(this, 600, string11, i19, new Runnable() {
                @Override
                public final void run() {
                    ProfileActivity.SearchAdapter.this.lambda$onCreateSearchArray$91();
                }
            });
            SearchResult searchResult92 = new SearchResult(this, 601, LocaleController.getString(R.string.SuggestStickers), "suggestRow", LocaleController.getString(i18), i19, new Runnable() {
                @Override
                public final void run() {
                    ProfileActivity.SearchAdapter.this.lambda$onCreateSearchArray$92();
                }
            });
            SearchResult searchResult93 = new SearchResult(this, 602, LocaleController.getString(R.string.FeaturedStickers), "featuredStickersHeaderRow", LocaleController.getString(i18), i19, new Runnable() {
                @Override
                public final void run() {
                    ProfileActivity.SearchAdapter.this.lambda$onCreateSearchArray$93();
                }
            });
            SearchResult searchResult94 = new SearchResult(this, 603, LocaleController.getString(R.string.Masks), null, LocaleController.getString(i18), i19, new Runnable() {
                @Override
                public final void run() {
                    ProfileActivity.SearchAdapter.this.lambda$onCreateSearchArray$94();
                }
            });
            SearchResult searchResult95 = new SearchResult(this, 604, LocaleController.getString(R.string.ArchivedStickers), null, LocaleController.getString(i18), i19, new Runnable() {
                @Override
                public final void run() {
                    ProfileActivity.SearchAdapter.this.lambda$onCreateSearchArray$95();
                }
            });
            SearchResult searchResult96 = new SearchResult(this, 605, LocaleController.getString(R.string.ArchivedMasks), null, LocaleController.getString(i18), i19, new Runnable() {
                @Override
                public final void run() {
                    ProfileActivity.SearchAdapter.this.lambda$onCreateSearchArray$96();
                }
            });
            SearchResult searchResult97 = new SearchResult(this, 606, LocaleController.getString(R.string.LargeEmoji), "largeEmojiRow", LocaleController.getString(i18), i19, new Runnable() {
                @Override
                public final void run() {
                    ProfileActivity.SearchAdapter.this.lambda$onCreateSearchArray$97();
                }
            });
            SearchResult searchResult98 = new SearchResult(this, 607, LocaleController.getString(R.string.LoopAnimatedStickers), "loopRow", LocaleController.getString(i18), i19, new Runnable() {
                @Override
                public final void run() {
                    ProfileActivity.SearchAdapter.this.lambda$onCreateSearchArray$98();
                }
            });
            int i20 = R.string.Emoji;
            String string12 = LocaleController.getString(i20);
            String string13 = LocaleController.getString(i18);
            int i21 = R.drawable.input_smile;
            SearchResult searchResult99 = new SearchResult(this, 608, string12, null, string13, i21, new Runnable() {
                @Override
                public final void run() {
                    ProfileActivity.SearchAdapter.this.lambda$onCreateSearchArray$99();
                }
            });
            SearchResult searchResult100 = new SearchResult(609, LocaleController.getString(R.string.SuggestAnimatedEmoji), "suggestAnimatedEmojiRow", LocaleController.getString(i18), LocaleController.getString(i20), i21, new Runnable() {
                @Override
                public final void run() {
                    ProfileActivity.SearchAdapter.this.lambda$onCreateSearchArray$100();
                }
            });
            SearchResult searchResult101 = new SearchResult(610, LocaleController.getString(R.string.FeaturedEmojiPacks), "featuredStickersHeaderRow", LocaleController.getString(i18), LocaleController.getString(i20), i21, new Runnable() {
                @Override
                public final void run() {
                    ProfileActivity.SearchAdapter.this.lambda$onCreateSearchArray$101();
                }
            });
            SearchResult searchResult102 = new SearchResult(this, 611, LocaleController.getString(R.string.DoubleTapSetting), null, LocaleController.getString(i18), i19, new Runnable() {
                @Override
                public final void run() {
                    ProfileActivity.SearchAdapter.this.lambda$onCreateSearchArray$102();
                }
            });
            int i22 = R.string.Filters;
            String string14 = LocaleController.getString(i22);
            int i23 = R.drawable.msg2_folder;
            SearchResult searchResult103 = new SearchResult(this, 700, string14, null, i23, new Runnable() {
                @Override
                public final void run() {
                    ProfileActivity.SearchAdapter.this.lambda$onCreateSearchArray$103();
                }
            });
            SearchResult searchResult104 = new SearchResult(this, 701, LocaleController.getString(R.string.CreateNewFilter), "createFilterRow", LocaleController.getString(i22), i23, new Runnable() {
                @Override
                public final void run() {
                    ProfileActivity.SearchAdapter.this.lambda$onCreateSearchArray$104();
                }
            });
            SearchResult searchResult105 = isPremiumFeatureAvailable(-1) ? new SearchResult(this, 800, LocaleController.getString(R.string.TelegramPremium), R.drawable.msg_settings_premium, new Runnable() {
                @Override
                public final void run() {
                    ProfileActivity.SearchAdapter.this.lambda$onCreateSearchArray$105();
                }
            }) : null;
            SearchResult searchResult106 = isPremiumFeatureAvailable(0) ? new SearchResult(this, 801, LocaleController.getString(R.string.PremiumPreviewLimits), LocaleController.getString(R.string.TelegramPremium), R.drawable.msg_settings_premium, new Runnable() {
                @Override
                public final void run() {
                    ProfileActivity.SearchAdapter.this.lambda$onCreateSearchArray$106();
                }
            }) : null;
            SearchResult searchResult107 = isPremiumFeatureAvailable(11) ? new SearchResult(this, 802, LocaleController.getString(R.string.PremiumPreviewEmoji), LocaleController.getString(R.string.TelegramPremium), R.drawable.msg_settings_premium, new Runnable() {
                @Override
                public final void run() {
                    ProfileActivity.SearchAdapter.this.lambda$onCreateSearchArray$107();
                }
            }) : null;
            SearchResult searchResult108 = isPremiumFeatureAvailable(1) ? new SearchResult(this, 803, LocaleController.getString(R.string.PremiumPreviewUploads), LocaleController.getString(R.string.TelegramPremium), R.drawable.msg_settings_premium, new Runnable() {
                @Override
                public final void run() {
                    ProfileActivity.SearchAdapter.this.lambda$onCreateSearchArray$108();
                }
            }) : null;
            SearchResult searchResult109 = isPremiumFeatureAvailable(2) ? new SearchResult(this, 804, LocaleController.getString(R.string.PremiumPreviewDownloadSpeed), LocaleController.getString(R.string.TelegramPremium), R.drawable.msg_settings_premium, new Runnable() {
                @Override
                public final void run() {
                    ProfileActivity.SearchAdapter.this.lambda$onCreateSearchArray$109();
                }
            }) : null;
            SearchResult searchResult110 = isPremiumFeatureAvailable(8) ? new SearchResult(this, 805, LocaleController.getString(R.string.PremiumPreviewVoiceToText), LocaleController.getString(R.string.TelegramPremium), R.drawable.msg_settings_premium, new Runnable() {
                @Override
                public final void run() {
                    ProfileActivity.SearchAdapter.this.lambda$onCreateSearchArray$110();
                }
            }) : null;
            SearchResult searchResult111 = isPremiumFeatureAvailable(3) ? new SearchResult(this, 806, LocaleController.getString(R.string.PremiumPreviewNoAds), LocaleController.getString(R.string.TelegramPremium), R.drawable.msg_settings_premium, new Runnable() {
                @Override
                public final void run() {
                    ProfileActivity.SearchAdapter.this.lambda$onCreateSearchArray$111();
                }
            }) : null;
            SearchResult searchResult112 = isPremiumFeatureAvailable(4) ? new SearchResult(this, 807, LocaleController.getString(R.string.PremiumPreviewReactions), LocaleController.getString(R.string.TelegramPremium), R.drawable.msg_settings_premium, new Runnable() {
                @Override
                public final void run() {
                    ProfileActivity.SearchAdapter.this.lambda$onCreateSearchArray$112();
                }
            }) : null;
            SearchResult searchResult113 = isPremiumFeatureAvailable(5) ? new SearchResult(this, 808, LocaleController.getString(R.string.PremiumPreviewStickers), LocaleController.getString(R.string.TelegramPremium), R.drawable.msg_settings_premium, new Runnable() {
                @Override
                public final void run() {
                    ProfileActivity.SearchAdapter.this.lambda$onCreateSearchArray$113();
                }
            }) : null;
            SearchResult searchResult114 = isPremiumFeatureAvailable(9) ? new SearchResult(this, 809, LocaleController.getString(R.string.PremiumPreviewAdvancedChatManagement), LocaleController.getString(R.string.TelegramPremium), R.drawable.msg_settings_premium, new Runnable() {
                @Override
                public final void run() {
                    ProfileActivity.SearchAdapter.this.lambda$onCreateSearchArray$114();
                }
            }) : null;
            SearchResult searchResult115 = isPremiumFeatureAvailable(6) ? new SearchResult(this, 810, LocaleController.getString(R.string.PremiumPreviewProfileBadge), LocaleController.getString(R.string.TelegramPremium), R.drawable.msg_settings_premium, new Runnable() {
                @Override
                public final void run() {
                    ProfileActivity.SearchAdapter.this.lambda$onCreateSearchArray$115();
                }
            }) : null;
            SearchResult searchResult116 = isPremiumFeatureAvailable(7) ? new SearchResult(this, 811, LocaleController.getString(R.string.PremiumPreviewAnimatedProfiles), LocaleController.getString(R.string.TelegramPremium), R.drawable.msg_settings_premium, new Runnable() {
                @Override
                public final void run() {
                    ProfileActivity.SearchAdapter.this.lambda$onCreateSearchArray$116();
                }
            }) : null;
            SearchResult searchResult117 = isPremiumFeatureAvailable(10) ? new SearchResult(this, 812, LocaleController.getString(R.string.PremiumPreviewAppIcon), LocaleController.getString(R.string.TelegramPremium), R.drawable.msg_settings_premium, new Runnable() {
                @Override
                public final void run() {
                    ProfileActivity.SearchAdapter.this.lambda$onCreateSearchArray$117();
                }
            }) : null;
            SearchResult searchResult118 = isPremiumFeatureAvailable(12) ? new SearchResult(this, 813, LocaleController.getString(R.string.PremiumPreviewEmojiStatus), LocaleController.getString(R.string.TelegramPremium), R.drawable.msg_settings_premium, new Runnable() {
                @Override
                public final void run() {
                    ProfileActivity.SearchAdapter.this.lambda$onCreateSearchArray$118();
                }
            }) : null;
            int i24 = R.string.PowerUsage;
            String string15 = LocaleController.getString(i24);
            int i25 = R.drawable.msg2_battery;
            SearchResult searchResult119 = new SearchResult(this, 900, string15, null, i25, new Runnable() {
                @Override
                public final void run() {
                    ProfileActivity.SearchAdapter.this.lambda$onCreateSearchArray$119();
                }
            });
            int i26 = R.string.LiteOptionsStickers;
            SearchResult searchResult120 = new SearchResult(this, 901, LocaleController.getString(i26), LocaleController.getString(i24), i25, new Runnable() {
                @Override
                public final void run() {
                    ProfileActivity.SearchAdapter.this.lambda$onCreateSearchArray$120();
                }
            });
            int i27 = R.string.LiteOptionsAutoplayKeyboard;
            SearchResult searchResult121 = new SearchResult(902, LocaleController.getString(i27), null, LocaleController.getString(i24), LocaleController.getString(i26), i25, new Runnable() {
                @Override
                public final void run() {
                    ProfileActivity.SearchAdapter.this.lambda$onCreateSearchArray$121();
                }
            });
            int i28 = R.string.LiteOptionsAutoplayChat;
            SearchResult searchResult122 = new SearchResult(903, LocaleController.getString(i28), null, LocaleController.getString(i24), LocaleController.getString(i26), i25, new Runnable() {
                @Override
                public final void run() {
                    ProfileActivity.SearchAdapter.this.lambda$onCreateSearchArray$122();
                }
            });
            int i29 = R.string.LiteOptionsEmoji;
            SearchResult searchResult123 = new SearchResult(this, 904, LocaleController.getString(i29), LocaleController.getString(i24), i25, new Runnable() {
                @Override
                public final void run() {
                    ProfileActivity.SearchAdapter.this.lambda$onCreateSearchArray$123();
                }
            });
            SearchResult searchResult124 = new SearchResult(905, LocaleController.getString(i27), null, LocaleController.getString(i24), LocaleController.getString(i29), i25, new Runnable() {
                @Override
                public final void run() {
                    ProfileActivity.SearchAdapter.this.lambda$onCreateSearchArray$124();
                }
            });
            SearchResult searchResult125 = new SearchResult(906, LocaleController.getString(R.string.LiteOptionsAutoplayReactions), null, LocaleController.getString(i24), LocaleController.getString(i29), i25, new Runnable() {
                @Override
                public final void run() {
                    ProfileActivity.SearchAdapter.this.lambda$onCreateSearchArray$125();
                }
            });
            SearchResult searchResult126 = new SearchResult(907, LocaleController.getString(i28), null, LocaleController.getString(i24), LocaleController.getString(i29), i25, new Runnable() {
                @Override
                public final void run() {
                    ProfileActivity.SearchAdapter.this.lambda$onCreateSearchArray$126();
                }
            });
            int i30 = R.string.LiteOptionsChat;
            SearchResult searchResult127 = new SearchResult(this, 908, LocaleController.getString(i30), LocaleController.getString(i24), i25, new Runnable() {
                @Override
                public final void run() {
                    ProfileActivity.SearchAdapter.this.lambda$onCreateSearchArray$127();
                }
            });
            SearchResult searchResult128 = new SearchResult(909, LocaleController.getString(R.string.LiteOptionsBackground), null, LocaleController.getString(i24), LocaleController.getString(i30), i25, new Runnable() {
                @Override
                public final void run() {
                    ProfileActivity.SearchAdapter.this.lambda$onCreateSearchArray$128();
                }
            });
            SearchResult searchResult129 = new SearchResult(910, LocaleController.getString(R.string.LiteOptionsTopics), null, LocaleController.getString(i24), LocaleController.getString(i30), i25, new Runnable() {
                @Override
                public final void run() {
                    ProfileActivity.SearchAdapter.this.lambda$onCreateSearchArray$129();
                }
            });
            SearchResult searchResult130 = new SearchResult(911, LocaleController.getString(R.string.LiteOptionsSpoiler), null, LocaleController.getString(i24), LocaleController.getString(i30), i25, new Runnable() {
                @Override
                public final void run() {
                    ProfileActivity.SearchAdapter.this.lambda$onCreateSearchArray$130();
                }
            });
            SearchResult searchResult131 = SharedConfig.getDevicePerformanceClass() >= 1 ? new SearchResult(326, LocaleController.getString(R.string.LiteOptionsBlur), null, LocaleController.getString(i24), LocaleController.getString(i30), i25, new Runnable() {
                @Override
                public final void run() {
                    ProfileActivity.SearchAdapter.this.lambda$onCreateSearchArray$131();
                }
            }) : null;
            SearchResult searchResult132 = new SearchResult(912, LocaleController.getString(R.string.LiteOptionsScale), null, LocaleController.getString(i24), LocaleController.getString(i30), i25, new Runnable() {
                @Override
                public final void run() {
                    ProfileActivity.SearchAdapter.this.lambda$onCreateSearchArray$132();
                }
            });
            SearchResult searchResult133 = new SearchResult(this, 913, LocaleController.getString(R.string.LiteOptionsCalls), LocaleController.getString(i24), i25, new Runnable() {
                @Override
                public final void run() {
                    ProfileActivity.SearchAdapter.this.lambda$onCreateSearchArray$133();
                }
            });
            SearchResult searchResult134 = new SearchResult(this, 214, LocaleController.getString(R.string.LiteOptionsAutoplayVideo), LocaleController.getString(i24), i25, new Runnable() {
                @Override
                public final void run() {
                    ProfileActivity.SearchAdapter.this.lambda$onCreateSearchArray$134();
                }
            });
            SearchResult searchResult135 = new SearchResult(this, 213, LocaleController.getString(R.string.LiteOptionsAutoplayGifs), LocaleController.getString(i24), i25, new Runnable() {
                @Override
                public final void run() {
                    ProfileActivity.SearchAdapter.this.lambda$onCreateSearchArray$135();
                }
            });
            SearchResult searchResult136 = new SearchResult(this, 914, LocaleController.getString(R.string.LiteSmoothTransitions), LocaleController.getString(i24), i25, new Runnable() {
                @Override
                public final void run() {
                    ProfileActivity.SearchAdapter.this.lambda$onCreateSearchArray$136();
                }
            });
            int i31 = R.string.Language;
            String string16 = LocaleController.getString(i31);
            int i32 = R.drawable.msg2_language;
            SearchResult searchResult137 = new SearchResult(this, 400, string16, i32, new Runnable() {
                @Override
                public final void run() {
                    ProfileActivity.SearchAdapter.this.lambda$onCreateSearchArray$137();
                }
            });
            SearchResult searchResult138 = new SearchResult(this, 405, LocaleController.getString(R.string.ShowTranslateButton), LocaleController.getString(i31), i32, new Runnable() {
                @Override
                public final void run() {
                    ProfileActivity.SearchAdapter.this.lambda$onCreateSearchArray$138();
                }
            });
            SearchResult searchResult139 = MessagesController.getInstance(((BaseFragment) ProfileActivity.this).currentAccount).getTranslateController().isContextTranslateEnabled() ? new SearchResult(this, 406, LocaleController.getString(R.string.DoNotTranslate), LocaleController.getString(i31), i32, new Runnable() {
                @Override
                public final void run() {
                    ProfileActivity.SearchAdapter.this.lambda$onCreateSearchArray$139();
                }
            }) : null;
            String string17 = LocaleController.getString(R.string.AskAQuestion);
            int i33 = R.string.SettingsHelp;
            String string18 = LocaleController.getString(i33);
            int i34 = R.drawable.msg2_help;
            return new SearchResult[]{searchResult, searchResult2, searchResult3, searchResult4, searchResult5, searchResult6, searchResult7, searchResult8, searchResult9, searchResult10, searchResult11, searchResult12, searchResult13, searchResult14, searchResult15, searchResult16, searchResult17, searchResult18, searchResult19, searchResult20, searchResult21, searchResult22, searchResult23, searchResult24, searchResult25, searchResult26, searchResult27, searchResult28, searchResult29, searchResult30, searchResult31, searchResult32, searchResult33, searchResult34, searchResult35, searchResult36, searchResult37, searchResult38, searchResult39, searchResult40, searchResult41, searchResult42, searchResult43, searchResult44, searchResult45, searchResult46, searchResult47, searchResult48, searchResult49, searchResult50, searchResult51, searchResult52, searchResult53, searchResult54, searchResult55, searchResult56, searchResult57, searchResult58, searchResult59, searchResult60, searchResult61, searchResult62, searchResult63, searchResult64, searchResult65, searchResult66, searchResult67, searchResult68, searchResult69, searchResult70, searchResult71, searchResult72, searchResult73, searchResult74, searchResult75, searchResult76, searchResult77, searchResult78, searchResult79, searchResult80, searchResult81, searchResult82, searchResult83, searchResult84, searchResult85, searchResult86, searchResult87, searchResult88, searchResult89, searchResult90, searchResult91, searchResult92, searchResult93, searchResult94, searchResult95, searchResult96, searchResult97, searchResult98, searchResult99, searchResult100, searchResult101, searchResult102, searchResult103, searchResult104, searchResult105, searchResult106, searchResult107, searchResult108, searchResult109, searchResult110, searchResult111, searchResult112, searchResult113, searchResult114, searchResult115, searchResult116, searchResult117, searchResult118, searchResult119, searchResult120, searchResult121, searchResult122, searchResult123, searchResult124, searchResult125, searchResult126, searchResult127, searchResult128, searchResult129, searchResult130, searchResult131, searchResult132, searchResult133, searchResult134, searchResult135, searchResult136, searchResult137, searchResult138, searchResult139, new SearchResult(this, 402, string17, string18, i34, new Runnable() {
                @Override
                public final void run() {
                    ProfileActivity.SearchAdapter.this.lambda$onCreateSearchArray$140();
                }
            }), new SearchResult(this, 403, LocaleController.getString(R.string.TelegramFAQ), LocaleController.getString(i33), i34, new Runnable() {
                @Override
                public final void run() {
                    ProfileActivity.SearchAdapter.this.lambda$onCreateSearchArray$141();
                }
            }), new SearchResult(this, 404, LocaleController.getString(R.string.PrivacyPolicy), LocaleController.getString(i33), i34, new Runnable() {
                @Override
                public final void run() {
                    ProfileActivity.SearchAdapter.this.lambda$onCreateSearchArray$142();
                }
            })};
        }

        public void updateSearchArray() {
            String[] strArr;
            SearchResult searchResult;
            HashMap hashMap = new HashMap();
            int i = 0;
            while (true) {
                SearchResult[] searchResultArr = this.searchArray;
                if (i >= searchResultArr.length) {
                    break;
                }
                SearchResult searchResult2 = searchResultArr[i];
                if (searchResult2 != null) {
                    hashMap.put(Integer.valueOf(searchResult2.guid), this.searchArray[i]);
                }
                i++;
            }
            Set<String> stringSet = MessagesController.getGlobalMainSettings().getStringSet("settingsSearchRecent2", null);
            if (stringSet != null) {
                Iterator<String> it = stringSet.iterator();
                while (it.hasNext()) {
                    try {
                        SerializedData serializedData = new SerializedData(Utilities.hexToBytes(it.next()));
                        int readInt32 = serializedData.readInt32(false);
                        int readInt322 = serializedData.readInt32(false);
                        if (readInt322 == 0) {
                            String readString = serializedData.readString(false);
                            int readInt323 = serializedData.readInt32(false);
                            if (readInt323 > 0) {
                                strArr = new String[readInt323];
                                for (int i2 = 0; i2 < readInt323; i2++) {
                                    strArr[i2] = serializedData.readString(false);
                                }
                            } else {
                                strArr = null;
                            }
                            MessagesController.FaqSearchResult faqSearchResult = new MessagesController.FaqSearchResult(readString, strArr, serializedData.readString(false));
                            faqSearchResult.num = readInt32;
                            this.recentSearches.add(faqSearchResult);
                        } else if (readInt322 == 1 && (searchResult = (SearchResult) hashMap.get(Integer.valueOf(serializedData.readInt32(false)))) != null) {
                            searchResult.num = readInt32;
                            this.recentSearches.add(searchResult);
                        }
                    } catch (Exception unused) {
                    }
                }
            }
            Collections.sort(this.recentSearches, new Comparator() {
                @Override
                public final int compare(Object obj, Object obj2) {
                    int lambda$updateSearchArray$0;
                    lambda$updateSearchArray$0 = ProfileActivity.SearchAdapter.this.lambda$updateSearchArray$0(obj, obj2);
                    return lambda$updateSearchArray$0;
                }
            });
        }

        public void addRecent(Object obj) {
            int indexOf = this.recentSearches.indexOf(obj);
            if (indexOf >= 0) {
                this.recentSearches.remove(indexOf);
            }
            this.recentSearches.add(0, obj);
            if (!this.searchWas) {
                notifyDataSetChanged();
            }
            if (this.recentSearches.size() > 20) {
                this.recentSearches.remove(r5.size() - 1);
            }
            LinkedHashSet linkedHashSet = new LinkedHashSet();
            int size = this.recentSearches.size();
            for (int i = 0; i < size; i++) {
                Object obj2 = this.recentSearches.get(i);
                if (obj2 instanceof SearchResult) {
                    ((SearchResult) obj2).num = i;
                } else if (obj2 instanceof MessagesController.FaqSearchResult) {
                    ((MessagesController.FaqSearchResult) obj2).num = i;
                }
                linkedHashSet.add(obj2.toString());
            }
            MessagesController.getGlobalMainSettings().edit().putStringSet("settingsSearchRecent2", linkedHashSet).commit();
        }

        public void clearRecent() {
            this.recentSearches.clear();
            MessagesController.getGlobalMainSettings().edit().remove("settingsSearchRecent2").commit();
            notifyDataSetChanged();
        }

        @Override
        public int getItemCount() {
            if (this.searchWas) {
                return this.searchResults.size() + (this.faqSearchResults.isEmpty() ? 0 : this.faqSearchResults.size() + 1);
            }
            return (this.recentSearches.isEmpty() ? 0 : this.recentSearches.size() + 1) + (this.faqSearchArray.isEmpty() ? 0 : this.faqSearchArray.size() + 1);
        }

        @Override
        public int getItemViewType(int i) {
            if (this.searchWas) {
                if (i >= this.searchResults.size() && i == this.searchResults.size()) {
                    return 1;
                }
            } else {
                if (i == 0) {
                    return !this.recentSearches.isEmpty() ? 2 : 1;
                }
                if (!this.recentSearches.isEmpty() && i == this.recentSearches.size() + 1) {
                    return 1;
                }
            }
            return 0;
        }

        @Override
        public boolean isEnabled(RecyclerView.ViewHolder viewHolder) {
            return viewHolder.getItemViewType() == 0;
        }

        public boolean isSearchWas() {
            return this.searchWas;
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
            int itemViewType = viewHolder.getItemViewType();
            if (itemViewType != 0) {
                if (itemViewType == 1) {
                    ((GraySectionCell) viewHolder.itemView).setText(LocaleController.getString(R.string.SettingsFaqSearchTitle));
                    return;
                } else {
                    if (itemViewType != 2) {
                        return;
                    }
                    ((HeaderCell) viewHolder.itemView).setText(LocaleController.getString(R.string.SettingsRecent));
                    return;
                }
            }
            SettingsSearchCell settingsSearchCell = (SettingsSearchCell) viewHolder.itemView;
            if (this.searchWas) {
                if (i >= this.searchResults.size()) {
                    int size = i - (this.searchResults.size() + 1);
                    settingsSearchCell.setTextAndValue((CharSequence) this.resultNames.get(this.searchResults.size() + size), ((MessagesController.FaqSearchResult) this.faqSearchResults.get(size)).path, true, size < this.searchResults.size() - 1);
                    return;
                } else {
                    SearchResult searchResult = (SearchResult) this.searchResults.get(i);
                    SearchResult searchResult2 = i > 0 ? (SearchResult) this.searchResults.get(i - 1) : null;
                    settingsSearchCell.setTextAndValueAndIcon((CharSequence) this.resultNames.get(i), searchResult.path, (searchResult2 == null || searchResult2.iconResId != searchResult.iconResId) ? searchResult.iconResId : 0, i < this.searchResults.size() - 1);
                    return;
                }
            }
            if (!this.recentSearches.isEmpty()) {
                i--;
            }
            if (i >= this.recentSearches.size()) {
                int size2 = i - (this.recentSearches.size() + 1);
                MessagesController.FaqSearchResult faqSearchResult = (MessagesController.FaqSearchResult) this.faqSearchArray.get(size2);
                settingsSearchCell.setTextAndValue(faqSearchResult.title, faqSearchResult.path, true, size2 < this.recentSearches.size() - 1);
                return;
            }
            Object obj = this.recentSearches.get(i);
            if (obj instanceof SearchResult) {
                SearchResult searchResult3 = (SearchResult) obj;
                settingsSearchCell.setTextAndValue(searchResult3.searchTitle, searchResult3.path, false, i < this.recentSearches.size() - 1);
            } else if (obj instanceof MessagesController.FaqSearchResult) {
                MessagesController.FaqSearchResult faqSearchResult2 = (MessagesController.FaqSearchResult) obj;
                settingsSearchCell.setTextAndValue(faqSearchResult2.title, faqSearchResult2.path, true, i < this.recentSearches.size() - 1);
            }
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View headerCell = i != 0 ? i != 1 ? new HeaderCell(this.mContext, 16) : new GraySectionCell(this.mContext) : new SettingsSearchCell(this.mContext);
            headerCell.setLayoutParams(new RecyclerView.LayoutParams(-1, -2));
            return new RecyclerListView.Holder(headerCell);
        }

        public void search(final String str) {
            this.lastSearchString = str;
            if (this.searchRunnable != null) {
                Utilities.searchQueue.cancelRunnable(this.searchRunnable);
                this.searchRunnable = null;
            }
            if (!TextUtils.isEmpty(str)) {
                DispatchQueue dispatchQueue = Utilities.searchQueue;
                Runnable runnable = new Runnable() {
                    @Override
                    public final void run() {
                        ProfileActivity.SearchAdapter.this.lambda$search$146(str);
                    }
                };
                this.searchRunnable = runnable;
                dispatchQueue.postRunnable(runnable, 300L);
                return;
            }
            this.searchWas = false;
            this.searchResults.clear();
            this.faqSearchResults.clear();
            this.resultNames.clear();
            ProfileActivity.this.emptyView.stickerView.getImageReceiver().startAnimation();
            ProfileActivity.this.emptyView.title.setText(LocaleController.getString(R.string.SettingsNoRecent));
            notifyDataSetChanged();
        }
    }

    public static class ShowDrawable extends Drawable implements SimpleTextView.PressableDrawable {
        private float alpha;
        private float alpha2;
        public final Paint backgroundPaint;
        private final ButtonBounce bounce;
        private boolean pressed;
        private int textColor;
        public final AnimatedTextView.AnimatedTextDrawable textDrawable;
        private View view;

        public class AnonymousClass1 implements Drawable.Callback {
            AnonymousClass1() {
            }

            @Override
            public void invalidateDrawable(Drawable drawable) {
                if (ShowDrawable.this.view != null) {
                    ShowDrawable.this.view.invalidate();
                }
            }

            @Override
            public void scheduleDrawable(Drawable drawable, Runnable runnable, long j) {
            }

            @Override
            public void unscheduleDrawable(Drawable drawable, Runnable runnable) {
            }
        }

        public class AnonymousClass2 extends ButtonBounce {
            AnonymousClass2(View view) {
                super(view);
            }

            @Override
            public void invalidate() {
                ShowDrawable.this.invalidateSelf();
            }
        }

        public ShowDrawable(String str) {
            Paint paint = new Paint(1);
            this.backgroundPaint = paint;
            this.alpha = 1.0f;
            this.alpha2 = 1.0f;
            this.bounce = new ButtonBounce(null) {
                AnonymousClass2(View view) {
                    super(view);
                }

                @Override
                public void invalidate() {
                    ShowDrawable.this.invalidateSelf();
                }
            };
            AnimatedTextView.AnimatedTextDrawable animatedTextDrawable = new AnimatedTextView.AnimatedTextDrawable();
            this.textDrawable = animatedTextDrawable;
            animatedTextDrawable.setCallback(new Drawable.Callback() {
                AnonymousClass1() {
                }

                @Override
                public void invalidateDrawable(Drawable drawable) {
                    if (ShowDrawable.this.view != null) {
                        ShowDrawable.this.view.invalidate();
                    }
                }

                @Override
                public void scheduleDrawable(Drawable drawable, Runnable runnable, long j) {
                }

                @Override
                public void unscheduleDrawable(Drawable drawable, Runnable runnable) {
                }
            });
            animatedTextDrawable.setText(str);
            animatedTextDrawable.setTextSize(AndroidUtilities.dp(11.0f));
            animatedTextDrawable.setGravity(17);
            paint.setColor(520093696);
        }

        @Override
        public void draw(Canvas canvas) {
            float f = this.alpha * this.alpha2;
            if (f <= 0.0f) {
                return;
            }
            RectF rectF = AndroidUtilities.rectTmp;
            rectF.set(getBounds());
            canvas.save();
            float scale = this.bounce.getScale(0.1f);
            canvas.scale(scale, scale, rectF.centerX(), rectF.centerY());
            int alpha = this.backgroundPaint.getAlpha();
            this.backgroundPaint.setAlpha((int) (alpha * f));
            canvas.drawRoundRect(rectF, AndroidUtilities.dp(20.0f), AndroidUtilities.dp(20.0f), this.backgroundPaint);
            this.backgroundPaint.setAlpha(alpha);
            this.textDrawable.setTextColor(this.textColor);
            this.textDrawable.setAlpha((int) (f * 255.0f));
            this.textDrawable.setBounds((int) rectF.left, (int) rectF.top, (int) rectF.right, (int) rectF.bottom);
            this.textDrawable.draw(canvas);
            canvas.restore();
        }

        @Override
        public int getIntrinsicHeight() {
            return AndroidUtilities.dp(17.33f);
        }

        @Override
        public int getIntrinsicWidth() {
            return (int) (this.textDrawable.getAnimateToWidth() + AndroidUtilities.dp(11.0f));
        }

        @Override
        public int getOpacity() {
            return -2;
        }

        @Override
        public void setAlpha(int i) {
            this.alpha = i / 255.0f;
            invalidateSelf();
        }

        public void setAlpha2(float f) {
            this.alpha2 = f;
            invalidateSelf();
        }

        public void setBackgroundColor(int i) {
            if (this.backgroundPaint.getColor() != i) {
                this.backgroundPaint.setColor(i);
                invalidateSelf();
            }
        }

        @Override
        public void setColorFilter(ColorFilter colorFilter) {
        }

        @Override
        public void setPressed(boolean z) {
            this.bounce.setPressed(z);
            this.pressed = z;
        }

        public void setTextColor(int i) {
            if (this.textColor != i) {
                this.textColor = i;
                invalidateSelf();
            }
        }

        public void setView(View view) {
            this.view = view;
        }
    }

    public class TopView extends View {
        private LinearGradient backgroundGradient;
        private int backgroundGradientColor1;
        private int backgroundGradientColor2;
        private int backgroundGradientHeight;
        private final Paint backgroundPaint;
        private Rect blurBounds;
        public int color1;
        private final AnimatedColor color1Animated;
        public int color2;
        private final AnimatedColor color2Animated;
        private int currentColor;
        private final AnimatedEmojiDrawable.SwapAnimatedEmojiDrawable emoji;
        private int emojiColor;
        private boolean emojiLoaded;
        public final AnimatedFloat emojiLoadedT;
        private final AnimatedFloat hasColorAnimated;
        private boolean hasColorById;
        private boolean hasEmoji;
        private Paint paint;

        public TopView(Context context) {
            super(context);
            this.paint = new Paint();
            CubicBezierInterpolator cubicBezierInterpolator = CubicBezierInterpolator.EASE_OUT_QUINT;
            this.hasColorAnimated = new AnimatedFloat(this, 350L, cubicBezierInterpolator);
            this.color1Animated = new AnimatedColor(this, 350L, cubicBezierInterpolator);
            this.color2Animated = new AnimatedColor(this, 350L, cubicBezierInterpolator);
            this.backgroundPaint = new Paint(1);
            this.emoji = new AnimatedEmojiDrawable.SwapAnimatedEmojiDrawable(this, false, AndroidUtilities.dp(20.0f), 13);
            this.emojiLoadedT = new AnimatedFloat(this, 0L, 440L, cubicBezierInterpolator);
            this.blurBounds = new Rect();
        }

        private boolean isEmojiLoaded() {
            if (this.emojiLoaded) {
                return true;
            }
            AnimatedEmojiDrawable.SwapAnimatedEmojiDrawable swapAnimatedEmojiDrawable = this.emoji;
            if (swapAnimatedEmojiDrawable == null || !(swapAnimatedEmojiDrawable.getDrawable() instanceof AnimatedEmojiDrawable)) {
                return false;
            }
            AnimatedEmojiDrawable animatedEmojiDrawable = (AnimatedEmojiDrawable) this.emoji.getDrawable();
            if (animatedEmojiDrawable.getImageReceiver() == null || !animatedEmojiDrawable.getImageReceiver().hasImageLoaded()) {
                return false;
            }
            this.emojiLoaded = true;
            return true;
        }

        public void lambda$onDraw$0(Canvas canvas, Float f, Float f2, Float f3, Float f4) {
            this.emoji.setAlpha((int) (f4.floatValue() * 255.0f * Math.min(1.0f, ProfileActivity.this.extraHeight / AndroidUtilities.dp(88.0f))));
            this.emoji.setBounds((int) (f.floatValue() - (f3.floatValue() * 0.45f)), (int) (f2.floatValue() - (f3.floatValue() * 0.45f)), (int) (f.floatValue() + (f3.floatValue() * 0.45f)), (int) (f2.floatValue() + (f3.floatValue() * 0.45f)));
            this.emoji.draw(canvas);
        }

        @Override
        protected void onAttachedToWindow() {
            super.onAttachedToWindow();
            this.emoji.attach();
        }

        @Override
        protected void onDetachedFromWindow() {
            super.onDetachedFromWindow();
            this.emoji.detach();
        }

        @Override
        protected void onDraw(final Canvas canvas) {
            ActionBar actionBar;
            ActionBarMenu actionBarMenu;
            SizeNotifierFrameLayout contentView;
            float y;
            Rect rect;
            ActionBar actionBar2;
            float currentActionBarHeight = ProfileActivity.this.extraHeight + ActionBar.getCurrentActionBarHeight() + (((BaseFragment) ProfileActivity.this).actionBar.getOccupyStatusBar() ? AndroidUtilities.statusBarHeight : 0) + ProfileActivity.this.searchTransitionOffset;
            int i = (int) ((1.0f - ProfileActivity.this.mediaHeaderAnimationProgress) * currentActionBarHeight);
            if (i != 0) {
                ChatActivityInterface chatActivityInterface = ProfileActivity.this.previousTransitionFragment;
                if (chatActivityInterface != null && chatActivityInterface.getContentView() != null) {
                    this.blurBounds.set(0, 0, getMeasuredWidth(), i);
                    if (ProfileActivity.this.previousTransitionFragment.getActionBar() == null || ProfileActivity.this.previousTransitionFragment.getContentView().blurWasDrawn() || ProfileActivity.this.previousTransitionFragment.getActionBar().getBackground() != null) {
                        BaseFragment baseFragment = ProfileActivity.this.previousTransitionMainFragment;
                        if (baseFragment != null && (baseFragment instanceof DialogsActivity) && (baseFragment.getFragmentView() instanceof SizeNotifierFrameLayout)) {
                            ProfileActivity.this.previousTransitionMainFragment.getActionBar().blurScrimPaint.setColor(Theme.getColor(Theme.key_actionBarDefault, ProfileActivity.this.previousTransitionMainFragment.getResourceProvider()));
                            contentView = (SizeNotifierFrameLayout) ProfileActivity.this.previousTransitionMainFragment.getFragmentView();
                            y = getY();
                            rect = this.blurBounds;
                            actionBar2 = ProfileActivity.this.previousTransitionMainFragment.getActionBar();
                        } else {
                            contentView = ProfileActivity.this.previousTransitionFragment.getContentView();
                            y = getY();
                            rect = this.blurBounds;
                            actionBar2 = ProfileActivity.this.previousTransitionFragment.getActionBar();
                        }
                        contentView.drawBlurRect(canvas, y, rect, actionBar2.blurScrimPaint, true);
                    } else {
                        this.paint.setColor(Theme.getColor(Theme.key_actionBarDefault, ProfileActivity.this.previousTransitionFragment.getResourceProvider()));
                        canvas.drawRect(this.blurBounds, this.paint);
                    }
                }
                this.paint.setColor(this.currentColor);
                int i2 = this.color1Animated.set(this.color1);
                int i3 = this.color2Animated.set(this.color2);
                int dp = AndroidUtilities.statusBarHeight + AndroidUtilities.dp(144.0f);
                if (this.backgroundGradient == null || this.backgroundGradientColor1 != i2 || this.backgroundGradientColor2 != i3 || this.backgroundGradientHeight != dp) {
                    this.backgroundGradientHeight = dp;
                    this.backgroundGradientColor2 = i3;
                    this.backgroundGradientColor1 = i2;
                    LinearGradient linearGradient = new LinearGradient(0.0f, 0.0f, 0.0f, dp, new int[]{i3, i2}, new float[]{0.0f, 1.0f}, Shader.TileMode.CLAMP);
                    this.backgroundGradient = linearGradient;
                    this.backgroundPaint.setShader(linearGradient);
                }
                float f = (ProfileActivity.this.playProfileAnimation == 0 ? 1.0f : ProfileActivity.this.avatarAnimationProgress) * this.hasColorAnimated.set(this.hasColorById);
                if (f < 1.0f) {
                    canvas.drawRect(0.0f, 0.0f, getMeasuredWidth(), i, this.paint);
                }
                if (f > 0.0f) {
                    this.backgroundPaint.setAlpha((int) (f * 255.0f));
                    canvas.drawRect(0.0f, 0.0f, getMeasuredWidth(), i, this.backgroundPaint);
                }
                if (this.hasEmoji && this.emojiLoadedT.set(isEmojiLoaded()) > 0.0f) {
                    canvas.save();
                    canvas.clipRect(0, 0, getMeasuredWidth(), i);
                    PeerColorActivity.drawProfileIconPattern(getMeasuredWidth() - AndroidUtilities.dp(46.0f), ((((BaseFragment) ProfileActivity.this).actionBar.getOccupyStatusBar() ? AndroidUtilities.statusBarHeight : 0) + AndroidUtilities.dp(144.0f)) - ((1.0f - (ProfileActivity.this.extraHeight / AndroidUtilities.dp(88.0f))) * AndroidUtilities.dp(33.0f)), (((ProfileActivity.this.extraHeight / AndroidUtilities.dp(88.0f)) - 1.0f) * 0.2f) + 1.0f, new Utilities.Callback4() {
                        @Override
                        public final void run(Object obj, Object obj2, Object obj3, Object obj4) {
                            ProfileActivity.TopView.this.lambda$onDraw$0(canvas, (Float) obj, (Float) obj2, (Float) obj3, (Float) obj4);
                        }
                    });
                    canvas.restore();
                }
                ChatActivityInterface chatActivityInterface2 = ProfileActivity.this.previousTransitionFragment;
                if (chatActivityInterface2 != null && (actionBarMenu = (actionBar = chatActivityInterface2.getActionBar()).menu) != null) {
                    int save = canvas.save();
                    canvas.translate(actionBar.getX() + actionBarMenu.getX(), actionBar.getY() + actionBarMenu.getY());
                    canvas.saveLayerAlpha(0.0f, 0.0f, actionBarMenu.getMeasuredWidth(), actionBarMenu.getMeasuredHeight(), (int) ((1.0f - ProfileActivity.this.avatarAnimationProgress) * 255.0f), 31);
                    actionBarMenu.draw(canvas);
                    canvas.restoreToCount(save);
                }
            }
            if (i != currentActionBarHeight) {
                this.paint.setColor(ProfileActivity.this.getThemedColor(Theme.key_windowBackgroundWhite));
                this.blurBounds.set(0, i, getMeasuredWidth(), (int) currentActionBarHeight);
                ProfileActivity.this.contentView.drawBlurRect(canvas, getY(), this.blurBounds, this.paint, true);
            }
            if (((BaseFragment) ProfileActivity.this).parentLayout != null) {
                ((BaseFragment) ProfileActivity.this).parentLayout.drawHeaderShadow(canvas, (int) (ProfileActivity.this.headerShadowAlpha * 255.0f), (int) currentActionBarHeight);
            }
        }

        @Override
        protected void onMeasure(int i, int i2) {
            setMeasuredDimension(View.MeasureSpec.getSize(i), View.MeasureSpec.getSize(i) + AndroidUtilities.dp(3.0f));
        }

        @Override
        public void setBackgroundColor(int i) {
            if (i != this.currentColor) {
                this.currentColor = i;
                this.paint.setColor(i);
                invalidate();
                if (this.hasColorById) {
                    return;
                }
                ProfileActivity.this.actionBarBackgroundColor = this.currentColor;
            }
        }

        public void setBackgroundColorId(org.telegram.messenger.MessagesController.PeerColor r5, boolean r6) {
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.ProfileActivity.TopView.setBackgroundColorId(org.telegram.messenger.MessagesController$PeerColor, boolean):void");
        }

        public void setBackgroundEmojiId(long j, boolean z) {
            this.emoji.set(j, z);
            this.emoji.setColor(Integer.valueOf(this.emojiColor));
            this.hasEmoji = this.hasEmoji || !(j == 0 || j == -1);
            invalidate();
        }
    }

    public ProfileActivity(Bundle bundle) {
        this(bundle, null);
    }

    public ProfileActivity(Bundle bundle, SharedMediaLayout.SharedMediaPreloader sharedMediaPreloader) {
        super(bundle);
        this.nameTextView = new SimpleTextView[2];
        this.nameTextViewRightDrawableContentDescription = null;
        this.nameTextViewRightDrawable2ContentDescription = null;
        this.onlineTextView = new SimpleTextView[4];
        this.verifiedDrawable = new Drawable[2];
        this.premiumStarDrawable = new Drawable[2];
        this.emojiStatusDrawable = new AnimatedEmojiDrawable.SwapAnimatedEmojiDrawable[2];
        this.botVerificationDrawable = new AnimatedEmojiDrawable.SwapAnimatedEmojiDrawable[2];
        this.verifiedCheckDrawable = new Drawable[2];
        this.verifiedCrossfadeDrawable = new CrossfadeDrawable[2];
        this.premiumCrossfadeDrawable = new CrossfadeDrawable[2];
        this.scrimView = null;
        this.scrimPaint = new Paint(1) {
            AnonymousClass1(int i) {
                super(i);
            }

            @Override
            public void setAlpha(int i) {
                super.setAlpha(i);
                ProfileActivity.this.fragmentView.invalidate();
            }
        };
        this.actionBarBackgroundPaint = new Paint(1);
        this.isOnline = new boolean[1];
        this.headerShadowAlpha = 1.0f;
        this.participantsMap = new LongSparseArray();
        this.allowProfileAnimation = true;
        this.disableProfileAnimation = false;
        this.positionToOffset = new HashMap();
        this.expandAnimatorValues = new float[]{0.0f, 1.0f};
        this.whitePaint = new Paint();
        this.onlineCount = -1;
        this.rect = new Rect();
        this.visibleChatParticipants = new ArrayList();
        this.visibleSortedUsers = new ArrayList();
        this.usersForceShowingIn = 0;
        this.firstLayout = true;
        this.invalidateScroll = true;
        this.isQrItemVisible = true;
        this.actionBarAnimationColorFrom = 0;
        this.navigationBarAnimationColorFrom = 0;
        this.reportReactionMessageId = 0;
        this.reportReactionFromDialogId = 0L;
        this.notificationsExceptionTopics = new HashSet();
        this.HEADER_SHADOW = new AnimationProperties.FloatProperty("headerShadow") {
            AnonymousClass2(String str) {
                super(str);
            }

            @Override
            public Float get(ProfileActivity profileActivity) {
                return Float.valueOf(ProfileActivity.this.headerShadowAlpha);
            }

            @Override
            public void setValue(ProfileActivity profileActivity, float f) {
                ProfileActivity profileActivity2 = ProfileActivity.this;
                profileActivity2.headerShadowAlpha = f;
                profileActivity2.topView.invalidate();
            }
        };
        this.provider = new PhotoViewer.EmptyPhotoViewerProvider() {
            AnonymousClass3() {
            }

            @Override
            public org.telegram.ui.PhotoViewer.PlaceProviderObject getPlaceForPhoto(org.telegram.messenger.MessageObject r7, org.telegram.tgnet.TLRPC.FileLocation r8, int r9, boolean r10) {
                throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.ProfileActivity.AnonymousClass3.getPlaceForPhoto(org.telegram.messenger.MessageObject, org.telegram.tgnet.TLRPC$FileLocation, int, boolean):org.telegram.ui.PhotoViewer$PlaceProviderObject");
            }

            @Override
            public void openPhotoForEdit(String str, String str2, boolean z) {
                ProfileActivity.this.imageUpdater.openPhotoForEdit(str, str2, 0, z);
            }

            @Override
            public void willHidePhotoViewer() {
                ProfileActivity.this.avatarImage.getImageReceiver().setVisible(true, true);
            }
        };
        this.floatingInterpolator = new AccelerateDecelerateInterpolator();
        this.ACTIONBAR_HEADER_PROGRESS = new AnimationProperties.FloatProperty("avatarAnimationProgress") {
            AnonymousClass44(String str) {
                super(str);
            }

            @Override
            public Float get(ActionBar actionBar) {
                return Float.valueOf(ProfileActivity.this.mediaHeaderAnimationProgress);
            }

            @Override
            public void setValue(ActionBar actionBar, float f) {
                int themedColor;
                ProfileActivity.this.mediaHeaderAnimationProgress = f;
                if (ProfileActivity.this.storyView != null) {
                    ProfileActivity.this.storyView.setActionBarActionMode(f);
                }
                ProfileActivity.this.topView.invalidate();
                int themedColor2 = ProfileActivity.this.getThemedColor(Theme.key_profile_title);
                ProfileActivity profileActivity = ProfileActivity.this;
                int i = Theme.key_player_actionBarTitle;
                int themedColor3 = profileActivity.getThemedColor(i);
                int offsetColor = AndroidUtilities.getOffsetColor(themedColor2, themedColor3, f, 1.0f);
                ProfileActivity.this.nameTextView[1].setTextColor(offsetColor);
                if (ProfileActivity.this.lockIconDrawable != null) {
                    ProfileActivity.this.lockIconDrawable.setColorFilter(offsetColor, PorterDuff.Mode.MULTIPLY);
                }
                if (ProfileActivity.this.scamDrawable != null) {
                    ProfileActivity.this.scamDrawable.setColor(AndroidUtilities.getOffsetColor(ProfileActivity.this.getThemedColor(Theme.key_avatar_subtitleInProfileBlue), themedColor3, f, 1.0f));
                }
                int i2 = -1;
                ((BaseFragment) ProfileActivity.this).actionBar.setItemsColor(AndroidUtilities.getOffsetColor(ProfileActivity.this.peerColor != null ? -1 : ProfileActivity.this.getThemedColor(Theme.key_actionBarDefaultIcon), ProfileActivity.this.getThemedColor(Theme.key_actionBarActionModeDefaultIcon), f, 1.0f), false);
                ((BaseFragment) ProfileActivity.this).actionBar.setItemsBackgroundColor(AndroidUtilities.getOffsetColor(ProfileActivity.this.peerColor != null ? 1090519039 : ProfileActivity.this.peerColor != null ? 553648127 : ProfileActivity.this.getThemedColor(Theme.key_avatar_actionBarSelectorBlue), ProfileActivity.this.getThemedColor(Theme.key_actionBarActionModeDefaultSelector), f, 1.0f), false);
                ProfileActivity.this.topView.invalidate();
                ProfileActivity.this.otherItem.setIconColor(ProfileActivity.this.peerColor != null ? -1 : ProfileActivity.this.getThemedColor(Theme.key_actionBarDefaultIcon));
                ProfileActivity.this.callItem.setIconColor(ProfileActivity.this.peerColor != null ? -1 : ProfileActivity.this.getThemedColor(Theme.key_actionBarDefaultIcon));
                ProfileActivity.this.videoCallItem.setIconColor(ProfileActivity.this.peerColor != null ? -1 : ProfileActivity.this.getThemedColor(Theme.key_actionBarDefaultIcon));
                ProfileActivity.this.editItem.setIconColor(ProfileActivity.this.peerColor != null ? -1 : ProfileActivity.this.getThemedColor(Theme.key_actionBarDefaultIcon));
                if (ProfileActivity.this.verifiedDrawable[0] != null) {
                    ProfileActivity.this.verifiedDrawable[0].setColorFilter(AndroidUtilities.getOffsetColor(ProfileActivity.this.getThemedColor(Theme.key_profile_verifiedBackground), ProfileActivity.this.getThemedColor(i), f, 1.0f), PorterDuff.Mode.MULTIPLY);
                }
                if (ProfileActivity.this.verifiedDrawable[1] != null) {
                    if (ProfileActivity.this.peerColor != null) {
                        themedColor = Theme.adaptHSV(ColorUtils.blendARGB(ProfileActivity.this.peerColor.getColor2(), ProfileActivity.this.peerColor.hasColor6(Theme.isCurrentThemeDark()) ? ProfileActivity.this.peerColor.getColor5() : ProfileActivity.this.peerColor.getColor3(), 0.4f), 0.1f, Theme.isCurrentThemeDark() ? -0.1f : -0.08f);
                    } else {
                        themedColor = ProfileActivity.this.getThemedColor(Theme.key_profile_verifiedBackground);
                    }
                    ProfileActivity.this.verifiedDrawable[1].setColorFilter(AndroidUtilities.getOffsetColor(themedColor, ProfileActivity.this.getThemedColor(i), f, 1.0f), PorterDuff.Mode.MULTIPLY);
                }
                if (ProfileActivity.this.verifiedCheckDrawable[0] != null) {
                    ProfileActivity.this.verifiedCheckDrawable[0].setColorFilter(AndroidUtilities.getOffsetColor(ProfileActivity.this.getThemedColor(Theme.key_profile_verifiedCheck), ProfileActivity.this.getThemedColor(Theme.key_windowBackgroundWhite), f, 1.0f), PorterDuff.Mode.MULTIPLY);
                }
                if (ProfileActivity.this.verifiedCheckDrawable[1] != null) {
                    if (ProfileActivity.this.peerColor == null) {
                        ProfileActivity profileActivity2 = ProfileActivity.this;
                        i2 = profileActivity2.applyPeerColor(profileActivity2.getThemedColor(Theme.key_profile_verifiedCheck));
                    }
                    ProfileActivity.this.verifiedCheckDrawable[1].setColorFilter(AndroidUtilities.getOffsetColor(i2, ProfileActivity.this.getThemedColor(Theme.key_windowBackgroundWhite), f, 1.0f), PorterDuff.Mode.MULTIPLY);
                }
                if (ProfileActivity.this.premiumStarDrawable[0] != null) {
                    ProfileActivity.this.premiumStarDrawable[0].setColorFilter(AndroidUtilities.getOffsetColor(ProfileActivity.this.getThemedColor(Theme.key_profile_verifiedBackground), ProfileActivity.this.getThemedColor(i), f, 1.0f), PorterDuff.Mode.MULTIPLY);
                }
                if (ProfileActivity.this.premiumStarDrawable[1] != null) {
                    ProfileActivity profileActivity3 = ProfileActivity.this;
                    int applyPeerColor = profileActivity3.applyPeerColor(profileActivity3.getThemedColor(Theme.key_profile_verifiedBackground));
                    ProfileActivity profileActivity4 = ProfileActivity.this;
                    ProfileActivity.this.premiumStarDrawable[1].setColorFilter(AndroidUtilities.getOffsetColor(applyPeerColor, profileActivity4.applyPeerColor(profileActivity4.getThemedColor(i)), f, 1.0f), PorterDuff.Mode.MULTIPLY);
                }
                ProfileActivity.this.updateEmojiStatusDrawableColor();
                if (ProfileActivity.this.avatarsViewPagerIndicatorView.getSecondaryMenuItem() != null) {
                    if (ProfileActivity.this.videoCallItemVisible || ProfileActivity.this.editItemVisible || ProfileActivity.this.callItemVisible) {
                        ProfileActivity profileActivity5 = ProfileActivity.this;
                        profileActivity5.needLayoutText(Math.min(1.0f, profileActivity5.extraHeight / AndroidUtilities.dp(88.0f)));
                    }
                }
            }
        };
        this.adaptedColors = new SparseIntArray();
        this.scrimAnimatorSet = null;
        this.savedScrollPosition = -1;
        this.photoDescriptionProgress = -1.0f;
        this.sharedMediaPreloader = sharedMediaPreloader;
    }

    public static void access$36200(ProfileActivity profileActivity, View view) {
        profileActivity.onTextDetailCellImageClicked(view);
    }

    public static void access$7700(ProfileActivity profileActivity) {
        profileActivity.onWriteButtonClick();
    }

    static int access$9812(ProfileActivity profileActivity, int i) {
        int i2 = profileActivity.listContentHeight + i;
        profileActivity.listContentHeight = i2;
        return i2;
    }

    public int applyPeerColor(int i) {
        return applyPeerColor(i, true, null);
    }

    public int applyPeerColor(int i, boolean z) {
        return applyPeerColor(i, z, null);
    }

    private int applyPeerColor(int i, boolean z, Boolean bool) {
        if ((!z && isSettings()) || this.peerColor == null) {
            return i;
        }
        if (!z) {
            int indexOfKey = this.adaptedColors.indexOfKey(i);
            if (indexOfKey >= 0) {
                return this.adaptedColors.valueAt(indexOfKey);
            }
            int adapt = OKLCH.adapt(i, Theme.adaptHSV(this.peerColor.getBgColor1(Theme.isCurrentThemeDark()), Theme.isCurrentThemeDark() ? 0.0f : 0.05f, Theme.isCurrentThemeDark() ? -0.1f : -0.04f));
            this.adaptedColors.put(i, adapt);
            return adapt;
        }
        int themedColor = getThemedColor(z ? Theme.key_actionBarDefault : Theme.key_windowBackgroundWhiteBlueIcon);
        int blendARGB = ColorUtils.blendARGB(this.peerColor.getStoryColor1(Theme.isCurrentThemeDark()), this.peerColor.getStoryColor2(Theme.isCurrentThemeDark()), 0.5f);
        int bgColor1 = z ? blendARGB : this.peerColor.getBgColor1(Theme.isCurrentThemeDark());
        if (!Theme.hasHue(themedColor)) {
            return (bool == null || bool.booleanValue()) ? blendARGB : Theme.adaptHSV(Theme.multAlpha(blendARGB, 0.7f), -0.2f, 0.2f);
        }
        boolean isCurrentThemeDark = Theme.isCurrentThemeDark();
        if (bool != null && !bool.booleanValue()) {
            blendARGB = Theme.multAlpha(blendARGB, 0.7f);
        }
        return Theme.changeColorAccent(themedColor, bgColor1, i, isCurrentThemeDark, blendARGB);
    }

    private int applyPeerColor2(int i) {
        MessagesController.PeerColor peerColor = this.peerColor;
        if (peerColor == null) {
            return i;
        }
        int bgColor2 = peerColor.getBgColor2(Theme.isCurrentThemeDark());
        return Theme.changeColorAccent(getThemedColor(Theme.key_windowBackgroundWhiteBlueIcon), bgColor2, i, Theme.isCurrentThemeDark(), bgColor2);
    }

    private void checkCanSendStoryForPosting() {
        if (ChatObject.isBoostSupported(MessagesController.getInstance(this.currentAccount).getChat(Long.valueOf(this.chatId)))) {
            StoriesController storiesController = getMessagesController().getStoriesController();
            this.waitCanSendStoryRequest = true;
            storiesController.canSendStoryFor(getDialogId(), new Consumer() {
                @Override
                public final void accept(Object obj) {
                    ProfileActivity.this.lambda$checkCanSendStoryForPosting$31((Boolean) obj);
                }
            }, false, this.resourcesProvider);
        }
    }

    public void checkListViewScroll() {
        View view;
        if (this.listView.getVisibility() != 0) {
            return;
        }
        if (this.sharedMediaLayoutAttached) {
            this.sharedMediaLayout.setVisibleHeight(this.listView.getMeasuredHeight() - this.sharedMediaLayout.getTop());
        }
        if (this.listView.getChildCount() <= 0 || this.openAnimationInProgress) {
            return;
        }
        int i = 0;
        while (true) {
            if (i >= this.listView.getChildCount()) {
                view = null;
                break;
            }
            RecyclerListView recyclerListView = this.listView;
            if (recyclerListView.getChildAdapterPosition(recyclerListView.getChildAt(i)) == 0) {
                view = this.listView.getChildAt(i);
                break;
            }
            i++;
        }
        RecyclerListView.Holder holder = view != null ? (RecyclerListView.Holder) this.listView.findContainingViewHolder(view) : null;
        int top = view == null ? 0 : view.getTop();
        int adapterPosition = holder != null ? holder.getAdapterPosition() : -1;
        if (top < 0 || adapterPosition != 0) {
            top = 0;
        }
        boolean z = this.imageUpdater == null && this.actionBar.isSearchFieldVisible();
        int i2 = this.sharedMediaRow;
        if (i2 != -1 && !z) {
            RecyclerListView.Holder holder2 = (RecyclerListView.Holder) this.listView.findViewHolderForAdapterPosition(i2);
            z = holder2 != null && holder2.itemView.getTop() <= 0;
        }
        setMediaHeaderVisible(z);
        float f = top;
        if (this.extraHeight == f || this.transitionAnimationInProress) {
            return;
        }
        this.extraHeight = f;
        this.topView.invalidate();
        if (this.playProfileAnimation != 0) {
            this.allowProfileAnimation = this.extraHeight != 0.0f;
        }
        needLayout(true);
    }

    public void checkPhotoDescriptionAlpha() {
        float f;
        float f2;
        float f3;
        ShowDrawable showDrawable;
        float f4;
        int i = this.playProfileAnimation;
        float f5 = 1.0f;
        if (i != 1 || (this.fragmentOpened && !this.openAnimationInProgress)) {
            if (i != 2 || (this.fragmentOpened && !this.openAnimationInProgress)) {
                if (this.userId == UserConfig.getInstance(this.currentAccount).clientUserId) {
                    f = this.currentExpandAnimatorValue;
                    f2 = 1.0f - this.customAvatarProgress;
                } else {
                    f = this.currentExpandAnimatorValue;
                    f2 = this.customAvatarProgress;
                }
                f3 = f * f2;
            } else {
                SimpleTextView simpleTextView = this.onlineTextView[1];
                f3 = simpleTextView == null ? 0.0f : simpleTextView.getAlpha();
            }
            this.photoDescriptionProgress = f3;
        } else {
            this.photoDescriptionProgress = 0.0f;
        }
        if (this.userId != UserConfig.getInstance(this.currentAccount).clientUserId) {
            if (!this.hasCustomPhoto) {
                SimpleTextView simpleTextView2 = this.onlineTextView[2];
                if (simpleTextView2 != null) {
                    simpleTextView2.setAlpha(0.0f);
                }
                showDrawable = this.showStatusButton;
                if (showDrawable == null) {
                    return;
                }
                showDrawable.setAlpha2(f5);
            }
            SimpleTextView simpleTextView3 = this.onlineTextView[2];
            if (simpleTextView3 != null) {
                simpleTextView3.setAlpha(this.photoDescriptionProgress);
            }
            showDrawable = this.showStatusButton;
            if (showDrawable != null) {
                f4 = this.photoDescriptionProgress;
                f5 = 1.0f - f4;
                showDrawable.setAlpha2(f5);
            }
            return;
        }
        if (!this.hasFallbackPhoto) {
            SimpleTextView simpleTextView4 = this.onlineTextView[2];
            if (simpleTextView4 != null) {
                simpleTextView4.setAlpha(0.0f);
                this.onlineTextView[3].setAlpha(0.0f);
            }
            showDrawable = this.showStatusButton;
            if (showDrawable == null) {
                return;
            }
            showDrawable.setAlpha2(f5);
        }
        this.customPhotoOffset = AndroidUtilities.dp(28.0f) * this.photoDescriptionProgress;
        SimpleTextView simpleTextView5 = this.onlineTextView[2];
        if (simpleTextView5 != null) {
            simpleTextView5.setAlpha(this.currentExpandAnimatorValue);
            this.onlineTextView[3].setAlpha(1.0f - this.currentExpandAnimatorValue);
            this.onlineTextView[1].setTranslationX(this.onlineX + this.customPhotoOffset);
            this.avatarContainer2.invalidate();
            showDrawable = this.showStatusButton;
            if (showDrawable != null) {
                f4 = this.currentExpandAnimatorValue;
                f5 = 1.0f - f4;
                showDrawable.setAlpha2(f5);
            }
        }
    }

    public void collapseAvatarInstant() {
        if (!this.allowPullingDown || this.currentExpandAnimatorValue <= 0.0f) {
            return;
        }
        this.layoutManager.scrollToPositionWithOffset(0, AndroidUtilities.dp(88.0f) - this.listView.getPaddingTop());
        this.listView.post(new Runnable() {
            @Override
            public final void run() {
                ProfileActivity.this.lambda$collapseAvatarInstant$36();
            }
        });
    }

    private void createActionBarMenu(boolean r15) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.ProfileActivity.createActionBarMenu(boolean):void");
    }

    private void createAutoDeleteItem(Context context) {
        this.autoDeletePopupWrapper = new AutoDeletePopupWrapper(context, this.otherItem.getPopupLayout().getSwipeBack(), new AutoDeletePopupWrapper.Callback() {
            AnonymousClass51() {
            }

            @Override
            public void dismiss() {
                ProfileActivity.this.otherItem.toggleSubMenu();
            }

            @Override
            public void setAutoDeleteHistory(int i, int i2) {
                ProfileActivity.this.setAutoDeleteHistory(i, i2);
            }

            @Override
            public void showGlobalAutoDeleteScreen() {
                ProfileActivity.this.presentFragment(new AutoDeleteMessagesActivity());
                dismiss();
            }
        }, false, 0, this.resourcesProvider);
        if (this.dialogId > 0 || this.userId > 0) {
            this.autoDeletePopupWrapper.allowExtendedHint(applyPeerColor(getThemedColor(Theme.key_windowBackgroundWhiteBlueText), false));
        }
        TLRPC.UserFull userFull = this.userInfo;
        TimerDrawable ttlIcon = TimerDrawable.getTtlIcon((userFull == null && this.chatInfo == null) ? 0 : userFull != null ? userFull.ttl_period : this.chatInfo.ttl_period);
        this.autoDeleteItemDrawable = ttlIcon;
        this.autoDeleteItem = this.otherItem.addSwipeBackItem(0, ttlIcon, LocaleController.getString(R.string.AutoDeletePopupTitle), this.autoDeletePopupWrapper.windowLayout);
        this.otherItem.addColoredGap();
        updateAutoDeleteItem();
    }

    public void createBirthdayEffect() {
        if (this.fragmentView == null || !this.fullyVisible || this.birthdayFetcher == null || getContext() == null) {
            return;
        }
        ProfileBirthdayEffect profileBirthdayEffect = this.birthdayEffect;
        if (profileBirthdayEffect != null) {
            profileBirthdayEffect.updateFetcher(this.birthdayFetcher);
            this.birthdayEffect.invalidate();
        } else {
            ProfileBirthdayEffect profileBirthdayEffect2 = new ProfileBirthdayEffect(this, this.birthdayFetcher);
            this.birthdayEffect = profileBirthdayEffect2;
            ((FrameLayout) this.fragmentView).addView(profileBirthdayEffect2, LayoutHelper.createFrame(-1, -1, 55));
        }
    }

    private void createFloatingActionButton(Context context) {
        Property property;
        if (getMessagesController().storiesEnabled() && getDialogId() <= 0) {
            final TLRPC.Chat chat = MessagesController.getInstance(this.currentAccount).getChat(Long.valueOf(this.chatId));
            if (ChatObject.isBoostSupported(chat) && getMessagesController().getStoriesController().canPostStories(getDialogId())) {
                checkCanSendStoryForPosting();
                final long dialogId = getDialogId();
                FrameLayout frameLayout = new FrameLayout(context);
                this.floatingButtonContainer = frameLayout;
                frameLayout.setVisibility(0);
                NestedFrameLayout nestedFrameLayout = this.contentView;
                FrameLayout frameLayout2 = this.floatingButtonContainer;
                int i = Build.VERSION.SDK_INT;
                int i2 = i >= 21 ? 56 : 60;
                float f = i >= 21 ? 56 : 60;
                boolean z = LocaleController.isRTL;
                nestedFrameLayout.addView(frameLayout2, LayoutHelper.createFrame(i2, f, (z ? 3 : 5) | 80, z ? 14.0f : 0.0f, 0.0f, z ? 0.0f : 14.0f, 14.0f));
                this.floatingButtonContainer.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public final void onClick(View view) {
                        ProfileActivity.this.lambda$createFloatingActionButton$35(dialogId, chat, view);
                    }
                });
                RLottieImageView rLottieImageView = new RLottieImageView(context);
                this.floatingButton = rLottieImageView;
                rLottieImageView.setScaleType(ImageView.ScaleType.CENTER);
                this.floatingButton.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_chats_actionIcon), PorterDuff.Mode.MULTIPLY));
                if (i >= 21) {
                    StateListAnimator stateListAnimator = new StateListAnimator();
                    FrameLayout frameLayout3 = this.floatingButtonContainer;
                    property = View.TRANSLATION_Z;
                    stateListAnimator.addState(new int[]{16842919}, ObjectAnimator.ofFloat(frameLayout3, (Property<FrameLayout, Float>) property, AndroidUtilities.dp(2.0f), AndroidUtilities.dp(4.0f)).setDuration(200L));
                    stateListAnimator.addState(new int[0], ObjectAnimator.ofFloat(this.floatingButtonContainer, (Property<FrameLayout, Float>) property, AndroidUtilities.dp(4.0f), AndroidUtilities.dp(2.0f)).setDuration(200L));
                    this.floatingButtonContainer.setStateListAnimator(stateListAnimator);
                    this.floatingButtonContainer.setOutlineProvider(new ViewOutlineProvider() {
                        AnonymousClass35() {
                        }

                        @Override
                        public void getOutline(View view, Outline outline) {
                            outline.setOval(0, 0, AndroidUtilities.dp(56.0f), AndroidUtilities.dp(56.0f));
                        }
                    });
                }
                this.floatingButtonContainer.addView(this.floatingButton, LayoutHelper.createFrame(-1, -1.0f));
                this.floatingButton.setAnimation(R.raw.write_contacts_fab_icon_camera, 56, 56);
                this.floatingButtonContainer.setContentDescription(LocaleController.getString(R.string.AccDescrCaptureStory));
                updateFloatingButtonColor();
            }
        }
    }

    private void dimBehindView(float f) {
        boolean z = f > 0.0f;
        this.fragmentView.invalidate();
        AnimatorSet animatorSet = this.scrimAnimatorSet;
        if (animatorSet != null) {
            animatorSet.cancel();
        }
        this.scrimAnimatorSet = new AnimatorSet();
        ArrayList arrayList = new ArrayList();
        ValueAnimator ofFloat = z ? ValueAnimator.ofFloat(0.0f, f) : ValueAnimator.ofFloat(this.scrimPaint.getAlpha() / 255.0f, 0.0f);
        arrayList.add(ofFloat);
        ofFloat.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                ProfileActivity.this.lambda$dimBehindView$88(valueAnimator);
            }
        });
        this.scrimAnimatorSet.playTogether(arrayList);
        this.scrimAnimatorSet.setDuration(z ? 150L : 220L);
        if (!z) {
            this.scrimAnimatorSet.addListener(new AnimatorListenerAdapter() {
                AnonymousClass54() {
                }

                @Override
                public void onAnimationEnd(Animator animator) {
                    ProfileActivity.this.scrimView = null;
                    ProfileActivity.this.fragmentView.invalidate();
                }
            });
        }
        this.scrimAnimatorSet.start();
    }

    private void dimBehindView(View view, float f) {
        this.scrimView = view;
        dimBehindView(f);
    }

    private void dimBehindView(boolean z) {
        dimBehindView(z ? 0.2f : 0.0f);
    }

    public boolean editRow(android.view.View r8, final int r9) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.ProfileActivity.editRow(android.view.View, int):boolean");
    }

    public boolean expandAvatar() {
        View view;
        RecyclerView.ViewHolder findContainingViewHolder;
        Integer num;
        if (!AndroidUtilities.isTablet() && !this.isInLandscapeMode && this.avatarImage.getImageReceiver().hasNotThumb() && !AndroidUtilities.isAccessibilityScreenReaderEnabled()) {
            this.openingAvatar = true;
            this.allowPullingDown = true;
            int i = 0;
            while (true) {
                if (i >= this.listView.getChildCount()) {
                    view = null;
                    break;
                }
                RecyclerListView recyclerListView = this.listView;
                if (recyclerListView.getChildAdapterPosition(recyclerListView.getChildAt(i)) == 0) {
                    view = this.listView.getChildAt(i);
                    break;
                }
                i++;
            }
            if (view != null && (findContainingViewHolder = this.listView.findContainingViewHolder(view)) != null && (num = (Integer) this.positionToOffset.get(Integer.valueOf(findContainingViewHolder.getAdapterPosition()))) != null) {
                this.listView.smoothScrollBy(0, -(num.intValue() + ((this.listView.getPaddingTop() - view.getTop()) - this.actionBar.getMeasuredHeight())), CubicBezierInterpolator.EASE_OUT_QUINT);
                return true;
            }
        }
        return false;
    }

    private void fetchUsersFromChannelInfo() {
        TLRPC.Chat chat = this.currentChat;
        if (chat == null || !chat.megagroup) {
            return;
        }
        TLRPC.ChatFull chatFull = this.chatInfo;
        if (!(chatFull instanceof TLRPC.TL_channelFull) || chatFull.participants == null) {
            return;
        }
        for (int i = 0; i < this.chatInfo.participants.participants.size(); i++) {
            TLRPC.ChatParticipant chatParticipant = this.chatInfo.participants.participants.get(i);
            this.participantsMap.put(chatParticipant.user_id, chatParticipant);
        }
    }

    private void fixLayout() {
        View view = this.fragmentView;
        if (view == null) {
            return;
        }
        view.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            AnonymousClass49() {
            }

            @Override
            public boolean onPreDraw() {
                ProfileActivity profileActivity = ProfileActivity.this;
                if (profileActivity.fragmentView != null) {
                    profileActivity.checkListViewScroll();
                    ProfileActivity.this.needLayout(true);
                    ProfileActivity.this.fragmentView.getViewTreeObserver().removeOnPreDrawListener(this);
                }
                return true;
            }
        });
    }

    public int getAverageColor(ImageReceiver imageReceiver) {
        return imageReceiver.getDrawable() instanceof VectorAvatarThumbDrawable ? ((VectorAvatarThumbDrawable) imageReceiver.getDrawable()).gradientTools.getAverageColor() : AndroidUtilities.calcBitmapColor(this.avatarImage.getImageReceiver().getBitmap());
    }

    private Drawable getBotVerificationDrawable(long j, boolean z, int i) {
        AnimatedEmojiDrawable.SwapAnimatedEmojiDrawable[] swapAnimatedEmojiDrawableArr = this.botVerificationDrawable;
        if (swapAnimatedEmojiDrawableArr[i] == null) {
            swapAnimatedEmojiDrawableArr[i] = new AnimatedEmojiDrawable.SwapAnimatedEmojiDrawable(this.nameTextView[i], AndroidUtilities.dp(17.0f), i == 0 ? 7 : 2);
            this.botVerificationDrawable[i].offset(0, AndroidUtilities.dp(1.0f));
            if (this.fragmentViewAttached) {
                this.botVerificationDrawable[i].attach();
            }
        }
        if (j != 0) {
            this.botVerificationDrawable[i].set(j, z);
        } else {
            this.botVerificationDrawable[i].set((Drawable) null, z);
        }
        updateEmojiStatusDrawableColor();
        return this.botVerificationDrawable[i];
    }

    public void getChannelParticipants(boolean z) {
        LongSparseArray longSparseArray;
        if (this.loadingUsers || (longSparseArray = this.participantsMap) == null || this.chatInfo == null) {
            return;
        }
        this.loadingUsers = true;
        final int i = (longSparseArray.size() == 0 || !z) ? 0 : 300;
        final TLRPC.TL_channels_getParticipants tL_channels_getParticipants = new TLRPC.TL_channels_getParticipants();
        tL_channels_getParticipants.channel = getMessagesController().getInputChannel(this.chatId);
        tL_channels_getParticipants.filter = new TLRPC.TL_channelParticipantsRecent();
        tL_channels_getParticipants.offset = z ? 0 : this.participantsMap.size();
        tL_channels_getParticipants.limit = 200;
        getConnectionsManager().bindRequestToGuid(getConnectionsManager().sendRequest(tL_channels_getParticipants, new RequestDelegate() {
            @Override
            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                ProfileActivity.this.lambda$getChannelParticipants$64(tL_channels_getParticipants, i, tLObject, tL_error);
            }
        }), this.classGuid);
    }

    private Drawable getEmojiStatusDrawable(TLRPC.EmojiStatus emojiStatus, boolean z, boolean z2, int i) {
        AnimatedEmojiDrawable.SwapAnimatedEmojiDrawable swapAnimatedEmojiDrawable;
        long j;
        AnimatedEmojiDrawable.SwapAnimatedEmojiDrawable[] swapAnimatedEmojiDrawableArr = this.emojiStatusDrawable;
        if (swapAnimatedEmojiDrawableArr[i] == null) {
            swapAnimatedEmojiDrawableArr[i] = new AnimatedEmojiDrawable.SwapAnimatedEmojiDrawable(this.nameTextView[i], AndroidUtilities.dp(24.0f), i == 0 ? 7 : 2);
            if (this.fragmentViewAttached) {
                this.emojiStatusDrawable[i].attach();
            }
        }
        if (!(emojiStatus instanceof TLRPC.TL_emojiStatus)) {
            if (emojiStatus instanceof TLRPC.TL_emojiStatusUntil) {
                TLRPC.TL_emojiStatusUntil tL_emojiStatusUntil = (TLRPC.TL_emojiStatusUntil) emojiStatus;
                if (tL_emojiStatusUntil.until > ((int) (System.currentTimeMillis() / 1000)) && !this.reportSpam) {
                    swapAnimatedEmojiDrawable = this.emojiStatusDrawable[i];
                    j = tL_emojiStatusUntil.document_id;
                }
            }
            this.emojiStatusDrawable[i].set(getPremiumCrossfadeDrawable(i), z2);
            updateEmojiStatusDrawableColor();
            return this.emojiStatusDrawable[i];
        }
        swapAnimatedEmojiDrawable = this.emojiStatusDrawable[i];
        j = ((TLRPC.TL_emojiStatus) emojiStatus).document_id;
        swapAnimatedEmojiDrawable.set(j, z2);
        updateEmojiStatusDrawableColor();
        return this.emojiStatusDrawable[i];
    }

    private Drawable getLockIconDrawable() {
        if (this.lockIconDrawable == null) {
            this.lockIconDrawable = Theme.chat_lockIconDrawable.getConstantState().newDrawable().mutate();
        }
        return this.lockIconDrawable;
    }

    public Drawable getPremiumCrossfadeDrawable(int i) {
        if (this.premiumCrossfadeDrawable[i] == null) {
            this.premiumStarDrawable[i] = ContextCompat.getDrawable(getParentActivity(), R.drawable.msg_premium_liststar).mutate();
            int themedColor = getThemedColor(Theme.key_profile_verifiedBackground);
            if (i == 1) {
                themedColor = applyPeerColor(themedColor);
            }
            this.premiumStarDrawable[i].setColorFilter(themedColor, PorterDuff.Mode.MULTIPLY);
            this.premiumCrossfadeDrawable[i] = new CrossfadeDrawable(this.premiumStarDrawable[i], ContextCompat.getDrawable(getParentActivity(), R.drawable.msg_premium_prolfilestar).mutate());
        }
        return this.premiumCrossfadeDrawable[i];
    }

    private Drawable getScamDrawable(int i) {
        if (this.scamDrawable == null) {
            ScamDrawable scamDrawable = new ScamDrawable(11, i);
            this.scamDrawable = scamDrawable;
            scamDrawable.setColor(getThemedColor(Theme.key_avatar_subtitleInProfileBlue));
        }
        return this.scamDrawable;
    }

    private int getSmallAvatarRoundRadius() {
        if (this.chatId == 0 || !ChatObject.isForum(getMessagesController().getChat(Long.valueOf(this.chatId)))) {
            return AndroidUtilities.dp(21.0f);
        }
        return AndroidUtilities.dp(needInsetForStories() ? 11.0f : 16.0f);
    }

    private Drawable getVerifiedCrossfadeDrawable(int i) {
        MessagesController.PeerColor peerColor;
        if (this.verifiedCrossfadeDrawable[i] == null) {
            this.verifiedDrawable[i] = Theme.profile_verifiedDrawable.getConstantState().newDrawable().mutate();
            this.verifiedCheckDrawable[i] = Theme.profile_verifiedCheckDrawable.getConstantState().newDrawable().mutate();
            if (i == 1 && (peerColor = this.peerColor) != null) {
                int adaptHSV = Theme.adaptHSV(peerColor.hasColor6(Theme.isCurrentThemeDark()) ? this.peerColor.getColor5() : this.peerColor.getColor3(), 0.1f, Theme.isCurrentThemeDark() ? -0.1f : -0.08f);
                Drawable drawable = this.verifiedDrawable[1];
                int offsetColor = AndroidUtilities.getOffsetColor(adaptHSV, getThemedColor(Theme.key_player_actionBarTitle), this.mediaHeaderAnimationProgress, 1.0f);
                PorterDuff.Mode mode = PorterDuff.Mode.MULTIPLY;
                drawable.setColorFilter(offsetColor, mode);
                this.verifiedCheckDrawable[1].setColorFilter(AndroidUtilities.getOffsetColor(-1, getThemedColor(Theme.key_windowBackgroundWhite), this.mediaHeaderAnimationProgress, 1.0f), mode);
            }
            this.verifiedCrossfadeDrawable[i] = new CrossfadeDrawable(new CombinedDrawable(this.verifiedDrawable[i], this.verifiedCheckDrawable[i]), ContextCompat.getDrawable(getParentActivity(), R.drawable.verified_profile));
        }
        return this.verifiedCrossfadeDrawable[i];
    }

    public void hideFloatingButton(boolean z) {
        TLRPC.User user = getMessagesController().getUser(Long.valueOf(this.userId));
        if (user != null && user.bot && user.bot_can_edit && user.bot_has_main_app) {
            StoriesController.BotPreviewsList botPreviewsList = (StoriesController.BotPreviewsList) getMessagesController().getStoriesController().getStoriesList(this.userId, 4);
            ArrayList uploadingStories = getMessagesController().getStoriesController().getUploadingStories(this.userId);
            if (botPreviewsList != null) {
                if (botPreviewsList.getCount() + (uploadingStories == null ? 0 : uploadingStories.size()) >= getMessagesController().botPreviewMediasMax) {
                    z = true;
                }
            }
        }
        if (this.floatingHidden == z || this.floatingButtonContainer == null || this.waitCanSendStoryRequest) {
            return;
        }
        this.floatingHidden = z;
        AnimatorSet animatorSet = new AnimatorSet();
        ValueAnimator ofFloat = ValueAnimator.ofFloat(this.floatingButtonHideProgress, this.floatingHidden ? 1.0f : 0.0f);
        ofFloat.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                ProfileActivity.this.lambda$hideFloatingButton$37(valueAnimator);
            }
        });
        animatorSet.playTogether(ofFloat);
        animatorSet.setDuration(300L);
        animatorSet.setInterpolator(this.floatingInterpolator);
        this.floatingButtonContainer.setClickable(!z);
        animatorSet.start();
    }

    private void invalidateIsInLandscapeMode() {
        Point point = new Point();
        getParentActivity().getWindowManager().getDefaultDisplay().getSize(point);
        this.isInLandscapeMode = point.x > point.y;
    }

    private boolean isQrNeedVisible() {
        return true;
    }

    private void kickUser(long j, TLRPC.ChatParticipant chatParticipant) {
        if (j != 0) {
            TLRPC.User user = getMessagesController().getUser(Long.valueOf(j));
            getMessagesController().deleteParticipantFromChat(this.chatId, user);
            if (this.currentChat != null && user != null && BulletinFactory.canShowBulletin(this)) {
                BulletinFactory.createRemoveFromChatBulletin(this, user, this.currentChat.title).show();
            }
            if (this.chatInfo.participants.participants.remove(chatParticipant)) {
                updateListAnimated(true);
                return;
            }
            return;
        }
        NotificationCenter notificationCenter = getNotificationCenter();
        int i = NotificationCenter.closeChats;
        notificationCenter.removeObserver(this, i);
        if (AndroidUtilities.isTablet()) {
            getNotificationCenter().lambda$postNotificationNameOnUIThread$1(i, Long.valueOf(-this.chatId));
        } else {
            getNotificationCenter().lambda$postNotificationNameOnUIThread$1(i, new Object[0]);
        }
        getMessagesController().deleteParticipantFromChat(this.chatId, getMessagesController().getUser(Long.valueOf(getUserConfig().getClientUserId())));
        this.playProfileAnimation = 0;
        lambda$onBackPressed$321();
    }

    public void lambda$checkCanSendStoryForPosting$31(Boolean bool) {
        this.waitCanSendStoryRequest = false;
        this.showBoostsAlert = !bool.booleanValue();
        hideFloatingButton(false);
    }

    public void lambda$collapseAvatarInstant$36() {
        needLayout(true);
        if (this.expandAnimator.isRunning()) {
            this.expandAnimator.cancel();
        }
        setAvatarExpandProgress(1.0f);
    }

    public void lambda$createActionBar$3() {
        dimBehindView(false);
    }

    public boolean lambda$createActionBar$4(ImageView imageView, View view) {
        ActionBarPopupWindow show = BackButtonMenu.show(this, imageView, getDialogId(), getTopicId(), this.resourcesProvider);
        if (show == null) {
            return false;
        }
        show.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public final void onDismiss() {
                ProfileActivity.this.lambda$createActionBar$3();
            }
        });
        dimBehindView(imageView, 0.3f);
        UndoView undoView = this.undoView;
        if (undoView != null) {
            undoView.hide(true, 1);
        }
        return true;
    }

    public void lambda$createFloatingActionButton$32() {
        presentFragment(StatisticActivity.create(getMessagesController().getChat(Long.valueOf(this.chatId))));
    }

    public void lambda$createFloatingActionButton$33(long j, TL_stories.TL_premium_boostsStatus tL_premium_boostsStatus, ChannelBoostsController.CanApplyBoost canApplyBoost) {
        if (canApplyBoost == null) {
            return;
        }
        LimitReachedBottomSheet.openBoostsForPostingStories(LaunchActivity.getLastFragment(), j, canApplyBoost, tL_premium_boostsStatus, new Runnable() {
            @Override
            public final void run() {
                ProfileActivity.this.lambda$createFloatingActionButton$32();
            }
        });
    }

    public void lambda$createFloatingActionButton$34(MessagesController messagesController, final long j, final TL_stories.TL_premium_boostsStatus tL_premium_boostsStatus) {
        this.loadingBoostsStats = false;
        if (tL_premium_boostsStatus == null) {
            return;
        }
        messagesController.getBoostsController().userCanBoostChannel(j, tL_premium_boostsStatus, new Consumer() {
            @Override
            public final void accept(Object obj) {
                ProfileActivity.this.lambda$createFloatingActionButton$33(j, tL_premium_boostsStatus, (ChannelBoostsController.CanApplyBoost) obj);
            }
        });
    }

    public void lambda$createFloatingActionButton$35(final long j, TLRPC.Chat chat, View view) {
        if (!this.showBoostsAlert) {
            StoryRecorder.getInstance(getParentActivity(), this.currentAccount).selectedPeerId(getDialogId()).canChangePeer(false).closeToWhenSent(new StoryRecorder.ClosingViewProvider() {
                final TLRPC.Chat val$currentChat;

                AnonymousClass34(TLRPC.Chat chat2) {
                    r2 = chat2;
                }

                @Override
                public StoryRecorder.SourceView getView(long j2) {
                    if (j2 != ProfileActivity.this.getDialogId()) {
                        return null;
                    }
                    ProfileActivity.this.updateAvatarRoundRadius();
                    return StoryRecorder.SourceView.fromAvatarImage(ProfileActivity.this.avatarImage, ChatObject.isForum(r2));
                }

                @Override
                public void preLayout(long j2, Runnable runnable) {
                    ProfileActivity.this.avatarImage.setHasStories(ProfileActivity.this.needInsetForStories());
                    if (j2 == ProfileActivity.this.getDialogId()) {
                        ProfileActivity.this.collapseAvatarInstant();
                    }
                    AndroidUtilities.runOnUIThread(runnable, 30L);
                }
            }).open(StoryRecorder.SourceView.fromFloatingButton(this.floatingButtonContainer), true);
        } else {
            if (this.loadingBoostsStats) {
                return;
            }
            final MessagesController messagesController = MessagesController.getInstance(this.currentAccount);
            this.loadingBoostsStats = true;
            messagesController.getBoostsController().getBoostsStats(j, new Consumer() {
                @Override
                public final void accept(Object obj) {
                    ProfileActivity.this.lambda$createFloatingActionButton$34(messagesController, j, (TL_stories.TL_premium_boostsStatus) obj);
                }
            });
        }
    }

    public void lambda$createView$10(Context context, long j, TL_payments.connectedBotStarRef connectedbotstarref) {
        int i = this.currentAccount;
        if (connectedbotstarref == null) {
            ChannelAffiliateProgramsFragment.showConnectAffiliateAlert(context, i, this.userInfo.starref_program, getUserConfig().getClientUserId(), this.resourcesProvider, false);
        } else {
            ChannelAffiliateProgramsFragment.showShareAffiliateAlert(context, i, connectedbotstarref, j, this.resourcesProvider);
        }
    }

    public static void lambda$createView$11(CheckBoxCell[] checkBoxCellArr, View view) {
        checkBoxCellArr[0].setChecked(!r1.isChecked(), true);
    }

    public void lambda$createView$12() {
        updateRowsIds();
        ListAdapter listAdapter = this.listAdapter;
        if (listAdapter != null) {
            listAdapter.notifyDataSetChanged();
        }
    }

    public boolean lambda$createView$13(Context context, BaseFragment baseFragment, TLRPC.TL_error tL_error) {
        if (tL_error == null || !"INVITE_REQUEST_SENT".equals(tL_error.text)) {
            return true;
        }
        MessagesController.getNotificationsSettings(this.currentAccount).edit().putLong("dialog_join_requested_time_" + this.dialogId, System.currentTimeMillis()).commit();
        JoinGroupAlert.showBulletin(context, this, ChatObject.isChannel(this.currentChat) && !this.currentChat.megagroup);
        updateRowsIds();
        ListAdapter listAdapter = this.listAdapter;
        if (listAdapter != null) {
            listAdapter.notifyDataSetChanged();
        }
        if (baseFragment instanceof ChatActivity) {
            ((ChatActivity) baseFragment).showBottomOverlayProgress(false, true);
        }
        return false;
    }

    public void lambda$createView$14(DialogInterface dialogInterface, int i) {
        SharedConfig.pushAuthKey = null;
        SharedConfig.pushAuthKeyId = null;
        SharedConfig.saveConfig();
        getConnectionsManager().switchBackend(true);
    }

    public void lambda$createView$15(View view) {
        ((TextCell) view).setChecked(this.botLocation.granted());
    }

    public void lambda$createView$16(TLObject tLObject, TLRPC.TL_error tL_error, int[] iArr) {
        if (!(tLObject instanceof TLRPC.TL_boolTrue)) {
            BulletinFactory.of(this).showForError(tL_error);
        }
        if (this.botPermissionEmojiStatusReqId == iArr[0]) {
            this.botPermissionEmojiStatusReqId = 0;
        }
    }

    public void lambda$createView$17(final int[] iArr, final TLObject tLObject, final TLRPC.TL_error tL_error) {
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                ProfileActivity.this.lambda$createView$16(tLObject, tL_error, iArr);
            }
        });
    }

    public void lambda$createView$18(final Context context, long j, final BaseFragment baseFragment, final View view, int i, float f, float f2) {
        AlertDialog.Builder builder;
        Activity parentActivity;
        int i2;
        AlertDialog createSupportAlert;
        ChatUsersActivity chatUsersActivity;
        ArrayList<TLRPC.ChatParticipant> arrayList;
        int i3;
        long j2;
        ListAdapter listAdapter;
        BaseFragment baseFragment2;
        BaseFragment baseFragment3;
        float f3 = f;
        float f4 = f2;
        if (getParentActivity() == null) {
            return;
        }
        this.listView.stopScroll();
        if (i != this.affiliateRow) {
            if (i == this.notificationsSimpleRow) {
                boolean z = !getMessagesController().isDialogMuted(j, this.topicId);
                getNotificationsController().muteDialog(j, this.topicId, z);
                BulletinFactory.createMuteBulletin(this, z, null).show();
                updateExceptions();
                int i4 = this.notificationsSimpleRow;
                if (i4 < 0 || (listAdapter = this.listAdapter) == null) {
                    return;
                }
                listAdapter.notifyItemChanged(i4);
                return;
            }
            if (i == this.addToContactsRow) {
                TLRPC.User user = getMessagesController().getUser(Long.valueOf(this.userId));
                Bundle bundle = new Bundle();
                bundle.putLong("user_id", user.id);
                bundle.putBoolean("addContact", true);
                bundle.putString("phone", this.vcardPhone);
                bundle.putString("first_name_card", this.vcardFirstName);
                bundle.putString("last_name_card", this.vcardLastName);
                openAddToContact(user, bundle);
                return;
            }
            if (i == this.reportReactionRow) {
                AlertDialog.Builder builder2 = new AlertDialog.Builder(getParentActivity(), this.resourcesProvider);
                builder2.setTitle(LocaleController.getString(R.string.ReportReaction));
                builder2.setMessage(LocaleController.getString(R.string.ReportAlertReaction));
                TLRPC.Chat chat = getMessagesController().getChat(Long.valueOf(-this.reportReactionFromDialogId));
                final CheckBoxCell[] checkBoxCellArr = new CheckBoxCell[1];
                if (chat != null && ChatObject.canBlockUsers(chat)) {
                    LinearLayout linearLayout = new LinearLayout(getParentActivity());
                    linearLayout.setOrientation(1);
                    CheckBoxCell checkBoxCell = new CheckBoxCell(getParentActivity(), 1, this.resourcesProvider);
                    checkBoxCellArr[0] = checkBoxCell;
                    checkBoxCell.setBackgroundDrawable(Theme.getSelectorDrawable(false));
                    checkBoxCellArr[0].setText(LocaleController.getString(R.string.BanUser), "", true, false);
                    checkBoxCellArr[0].setPadding(LocaleController.isRTL ? AndroidUtilities.dp(16.0f) : AndroidUtilities.dp(8.0f), 0, LocaleController.isRTL ? AndroidUtilities.dp(8.0f) : AndroidUtilities.dp(16.0f), 0);
                    linearLayout.addView(checkBoxCellArr[0], LayoutHelper.createLinear(-1, -2));
                    checkBoxCellArr[0].setOnClickListener(new View.OnClickListener() {
                        @Override
                        public final void onClick(View view2) {
                            ProfileActivity.lambda$createView$11(checkBoxCellArr, view2);
                        }
                    });
                    builder2.setView(linearLayout);
                }
                builder2.setPositiveButton(LocaleController.getString(R.string.ReportChat), new AnonymousClass14(checkBoxCellArr));
                builder2.setNegativeButton(LocaleController.getString(R.string.Cancel), new DialogInterface.OnClickListener() {
                    AnonymousClass15() {
                    }

                    @Override
                    public void onClick(DialogInterface dialogInterface, int i5) {
                        dialogInterface.dismiss();
                    }
                });
                TextView textView = (TextView) builder2.show().getButton(-1);
                if (textView != null) {
                    textView.setTextColor(Theme.getColor(Theme.key_text_RedBold));
                    return;
                }
                return;
            }
            if (i != this.settingsKeyRow) {
                if (i != this.settingsTimerRow) {
                    if (i == this.notificationsRow) {
                        if ((!LocaleController.isRTL || f3 > AndroidUtilities.dp(76.0f)) && (LocaleController.isRTL || f3 < view.getMeasuredWidth() - AndroidUtilities.dp(76.0f))) {
                            ChatNotificationsPopupWrapper chatNotificationsPopupWrapper = new ChatNotificationsPopupWrapper(context, this.currentAccount, null, true, true, new ChatNotificationsPopupWrapper.Callback() {
                                final long val$did;

                                AnonymousClass16(long j3) {
                                    r2 = j3;
                                }

                                @Override
                                public void dismiss() {
                                    ChatNotificationsPopupWrapper.Callback.CC.$default$dismiss(this);
                                }

                                @Override
                                public void muteFor(int i5) {
                                    ProfileActivity profileActivity = ProfileActivity.this;
                                    if (i5 == 0) {
                                        if (profileActivity.getMessagesController().isDialogMuted(r2, ProfileActivity.this.topicId)) {
                                            toggleMute();
                                        }
                                        if (BulletinFactory.canShowBulletin(ProfileActivity.this)) {
                                            ProfileActivity profileActivity2 = ProfileActivity.this;
                                            BulletinFactory.createMuteBulletin(profileActivity2, 4, i5, profileActivity2.getResourceProvider()).show();
                                            return;
                                        }
                                        return;
                                    }
                                    profileActivity.getNotificationsController().muteUntil(r2, ProfileActivity.this.topicId, i5);
                                    if (BulletinFactory.canShowBulletin(ProfileActivity.this)) {
                                        ProfileActivity profileActivity3 = ProfileActivity.this;
                                        BulletinFactory.createMuteBulletin(profileActivity3, 5, i5, profileActivity3.getResourceProvider()).show();
                                    }
                                    ProfileActivity.this.updateExceptions();
                                    if (ProfileActivity.this.notificationsRow < 0 || ProfileActivity.this.listAdapter == null) {
                                        return;
                                    }
                                    ProfileActivity.this.listAdapter.notifyItemChanged(ProfileActivity.this.notificationsRow);
                                }

                                @Override
                                public void openExceptions() {
                                    Bundle bundle2 = new Bundle();
                                    bundle2.putLong("dialog_id", r2);
                                    TopicsNotifySettingsFragments topicsNotifySettingsFragments = new TopicsNotifySettingsFragments(bundle2);
                                    topicsNotifySettingsFragments.setExceptions(ProfileActivity.this.notificationsExceptionTopics);
                                    ProfileActivity.this.presentFragment(topicsNotifySettingsFragments);
                                }

                                @Override
                                public void showCustomize() {
                                    if (r2 != 0) {
                                        Bundle bundle2 = new Bundle();
                                        bundle2.putLong("dialog_id", r2);
                                        bundle2.putLong("topic_id", ProfileActivity.this.topicId);
                                        ProfileActivity profileActivity = ProfileActivity.this;
                                        profileActivity.presentFragment(new ProfileNotificationsActivity(bundle2, profileActivity.resourcesProvider));
                                    }
                                }

                                @Override
                                public void toggleMute() {
                                    boolean z2 = !ProfileActivity.this.getMessagesController().isDialogMuted(r2, ProfileActivity.this.topicId);
                                    ProfileActivity.this.getNotificationsController().muteDialog(r2, ProfileActivity.this.topicId, z2);
                                    ProfileActivity profileActivity = ProfileActivity.this;
                                    if (profileActivity.fragmentView != null) {
                                        BulletinFactory.createMuteBulletin(profileActivity, z2, null).show();
                                    }
                                    ProfileActivity.this.updateExceptions();
                                    if (ProfileActivity.this.notificationsRow < 0 || ProfileActivity.this.listAdapter == null) {
                                        return;
                                    }
                                    ProfileActivity.this.listAdapter.notifyItemChanged(ProfileActivity.this.notificationsRow);
                                }

                                @Override
                                public void toggleSound() {
                                    SharedPreferences notificationsSettings = MessagesController.getNotificationsSettings(((BaseFragment) ProfileActivity.this).currentAccount);
                                    boolean z2 = notificationsSettings.getBoolean("sound_enabled_" + NotificationsController.getSharedPrefKey(r2, ProfileActivity.this.topicId), true);
                                    boolean z22 = !z2;
                                    notificationsSettings.edit().putBoolean("sound_enabled_" + NotificationsController.getSharedPrefKey(r2, ProfileActivity.this.topicId), z22).apply();
                                    if (BulletinFactory.canShowBulletin(ProfileActivity.this)) {
                                        ProfileActivity profileActivity = ProfileActivity.this;
                                        BulletinFactory.createSoundEnabledBulletin(profileActivity, z2 ? 1 : 0, profileActivity.getResourceProvider()).show();
                                    }
                                }
                            }, getResourceProvider());
                            chatNotificationsPopupWrapper.lambda$update$11(j3, this.topicId, this.notificationsExceptionTopics);
                            if (AndroidUtilities.isTablet()) {
                                ViewGroup view2 = this.parentLayout.getView();
                                float x = view2.getX() + view2.getPaddingLeft() + f3;
                                f4 += view2.getY() + view2.getPaddingTop();
                                f3 = x;
                            }
                            chatNotificationsPopupWrapper.showAsOptions(this, view, f3, f4);
                            return;
                        }
                        NotificationsCheckCell notificationsCheckCell = (NotificationsCheckCell) view;
                        boolean z2 = !notificationsCheckCell.isChecked();
                        boolean isGlobalNotificationsEnabled = getNotificationsController().isGlobalNotificationsEnabled(j3, false, false);
                        String sharedPrefKey = NotificationsController.getSharedPrefKey(j3, this.topicId);
                        if (z2) {
                            SharedPreferences.Editor edit = MessagesController.getNotificationsSettings(this.currentAccount).edit();
                            if (isGlobalNotificationsEnabled) {
                                edit.remove("notify2_" + sharedPrefKey);
                            } else {
                                edit.putInt("notify2_" + sharedPrefKey, 0);
                            }
                            if (this.topicId == 0) {
                                getMessagesStorage().setDialogFlags(j3, 0L);
                                TLRPC.Dialog dialog = (TLRPC.Dialog) getMessagesController().dialogs_dict.get(j3);
                                if (dialog != null) {
                                    dialog.notify_settings = new TLRPC.TL_peerNotifySettings();
                                }
                            }
                            edit.apply();
                        } else {
                            SharedPreferences.Editor edit2 = MessagesController.getNotificationsSettings(this.currentAccount).edit();
                            if (isGlobalNotificationsEnabled) {
                                edit2.putInt("notify2_" + sharedPrefKey, 2);
                                j2 = 1;
                            } else {
                                edit2.remove("notify2_" + sharedPrefKey);
                                j2 = 0L;
                            }
                            getNotificationsController().removeNotificationsForDialog(j3);
                            if (this.topicId == 0) {
                                getMessagesStorage().setDialogFlags(j3, j2);
                                TLRPC.Dialog dialog2 = (TLRPC.Dialog) getMessagesController().dialogs_dict.get(j3);
                                if (dialog2 != null) {
                                    TLRPC.TL_peerNotifySettings tL_peerNotifySettings = new TLRPC.TL_peerNotifySettings();
                                    dialog2.notify_settings = tL_peerNotifySettings;
                                    if (isGlobalNotificationsEnabled) {
                                        tL_peerNotifySettings.mute_until = Integer.MAX_VALUE;
                                    }
                                }
                            }
                            edit2.apply();
                        }
                        updateExceptions();
                        getNotificationsController().updateServerNotificationsSettings(j3, this.topicId);
                        notificationsCheckCell.setChecked(z2);
                        RecyclerListView.Holder holder = (RecyclerListView.Holder) this.listView.findViewHolderForPosition(this.notificationsRow);
                        if (holder != null) {
                            this.listAdapter.onBindViewHolder(holder, this.notificationsRow);
                            return;
                        }
                        return;
                    }
                    if (i == this.unblockRow) {
                        getMessagesController().unblockPeer(this.userId);
                        if (BulletinFactory.canShowBulletin(this)) {
                            BulletinFactory.createBanBulletin(this, false).show();
                            return;
                        }
                        return;
                    }
                    if (i == this.addToGroupButtonRow) {
                        try {
                            this.actionBar.getActionBarMenuOnItemClick().onItemClick(9);
                            return;
                        } catch (Exception e) {
                            FileLog.e(e);
                            return;
                        }
                    }
                    if (i != this.sendMessageRow) {
                        if (i == this.reportRow) {
                            ReportBottomSheet.openChat(this, getDialogId());
                            return;
                        }
                        if (i >= this.membersStartRow && i < this.membersEndRow) {
                            if (this.sortedUsers.isEmpty()) {
                                arrayList = this.chatInfo.participants.participants;
                                i3 = i - this.membersStartRow;
                            } else {
                                arrayList = this.chatInfo.participants.participants;
                                i3 = ((Integer) this.sortedUsers.get(i - this.membersStartRow)).intValue();
                            }
                            onMemberClick(arrayList.get(i3), false, view);
                            return;
                        }
                        if (i == this.addMemberRow) {
                            openAddMember();
                            return;
                        }
                        if (i != this.usernameRow) {
                            if (i == this.locationRow) {
                                if (!(this.chatInfo.location instanceof TLRPC.TL_channelLocation)) {
                                    return;
                                }
                                LocationActivity locationActivity = new LocationActivity(5);
                                locationActivity.setChatLocation(this.chatId, (TLRPC.TL_channelLocation) this.chatInfo.location);
                                baseFragment3 = locationActivity;
                            } else {
                                if (i == this.joinRow) {
                                    getMessagesController().addUserToChat(this.currentChat.id, getUserConfig().getCurrentUser(), 0, null, this, true, new Runnable() {
                                        @Override
                                        public final void run() {
                                            ProfileActivity.this.lambda$createView$12();
                                        }
                                    }, new MessagesController.ErrorDelegate() {
                                        @Override
                                        public final boolean run(TLRPC.TL_error tL_error) {
                                            boolean lambda$createView$13;
                                            lambda$createView$13 = ProfileActivity.this.lambda$createView$13(context, baseFragment, tL_error);
                                            return lambda$createView$13;
                                        }
                                    });
                                    NotificationCenter.getGlobalInstance().lambda$postNotificationNameOnUIThread$1(NotificationCenter.closeSearchByActiveAction, new Object[0]);
                                    return;
                                }
                                if (i == this.subscribersRow) {
                                    Bundle bundle2 = new Bundle();
                                    bundle2.putLong("chat_id", this.chatId);
                                    bundle2.putInt("type", 2);
                                    chatUsersActivity = new ChatUsersActivity(bundle2);
                                } else if (i == this.subscribersRequestsRow) {
                                    baseFragment3 = new MemberRequestsActivity(this.chatId);
                                } else if (i == this.administratorsRow) {
                                    Bundle bundle3 = new Bundle();
                                    bundle3.putLong("chat_id", this.chatId);
                                    bundle3.putInt("type", 1);
                                    chatUsersActivity = new ChatUsersActivity(bundle3);
                                } else {
                                    if (i == this.settingsRow) {
                                        this.editItem.performClick();
                                        return;
                                    }
                                    if (i == this.botStarsBalanceRow) {
                                        baseFragment3 = new BotStarsActivity(0, this.userId);
                                    } else if (i == this.botTonBalanceRow) {
                                        baseFragment3 = new BotStarsActivity(1, this.userId);
                                    } else if (i == this.channelBalanceRow) {
                                        Bundle bundle4 = new Bundle();
                                        bundle4.putLong("chat_id", this.chatId);
                                        bundle4.putBoolean("start_from_monetization", true);
                                        baseFragment2 = new StatisticActivity(bundle4);
                                    } else if (i == this.blockedUsersRow) {
                                        Bundle bundle5 = new Bundle();
                                        bundle5.putLong("chat_id", this.chatId);
                                        bundle5.putInt("type", 0);
                                        chatUsersActivity = new ChatUsersActivity(bundle5);
                                    } else if (i == this.notificationRow) {
                                        baseFragment3 = new NotificationsSettingsActivity();
                                    } else if (i == this.privacyRow) {
                                        baseFragment2 = new PrivacySettingsActivity().setCurrentPassword(this.currentPassword);
                                    } else if (i == this.dataRow) {
                                        baseFragment3 = new DataSettingsActivity();
                                    } else if (i == this.chatRow) {
                                        baseFragment3 = new ThemeActivity(0);
                                    } else if (i == this.filtersRow) {
                                        baseFragment3 = new FiltersSetupActivity();
                                    } else if (i == this.stickersRow) {
                                        baseFragment3 = new StickersActivity(0, null);
                                    } else if (i == this.liteModeRow) {
                                        baseFragment3 = new LiteModeSettingsActivity();
                                    } else {
                                        if (i != this.devicesRow) {
                                            if (i == this.questionRow) {
                                                createSupportAlert = AlertsCreator.createSupportAlert(this, this.resourcesProvider);
                                                showDialog(createSupportAlert);
                                                return;
                                            }
                                            if (i == this.faqRow) {
                                                parentActivity = getParentActivity();
                                                i2 = R.string.TelegramFaqUrl;
                                            } else if (i == this.policyRow) {
                                                parentActivity = getParentActivity();
                                                i2 = R.string.PrivacyPolicyUrl;
                                            } else {
                                                if (i == this.sendLogsRow) {
                                                    sendLogs(getParentActivity(), false);
                                                    return;
                                                }
                                                if (i == this.sendLastLogsRow) {
                                                    sendLogs(getParentActivity(), true);
                                                    return;
                                                }
                                                if (i == this.clearLogsRow) {
                                                    FileLog.cleanupLogs();
                                                    return;
                                                }
                                                if (i == this.switchBackendRow) {
                                                    if (getParentActivity() == null) {
                                                        return;
                                                    }
                                                    builder = new AlertDialog.Builder(getParentActivity(), this.resourcesProvider);
                                                    builder.setMessage(LocaleController.getString(R.string.AreYouSure));
                                                    builder.setTitle(LocaleController.getString(R.string.AppName));
                                                    builder.setPositiveButton(LocaleController.getString(R.string.OK), new DialogInterface.OnClickListener() {
                                                        @Override
                                                        public final void onClick(DialogInterface dialogInterface, int i5) {
                                                            ProfileActivity.this.lambda$createView$14(dialogInterface, i5);
                                                        }
                                                    });
                                                    builder.setNegativeButton(LocaleController.getString(R.string.Cancel), null);
                                                } else if (i == this.languageRow) {
                                                    baseFragment3 = new LanguageSelectActivity();
                                                } else if (i == this.setUsernameRow) {
                                                    baseFragment3 = new ChangeUsernameActivity();
                                                } else if (i == this.bioRow) {
                                                    baseFragment3 = new UserInfoActivity();
                                                } else if (i == this.numberRow) {
                                                    baseFragment3 = new ActionIntroActivity(3);
                                                } else if (i != this.setAvatarRow) {
                                                    if (i == this.premiumRow) {
                                                        baseFragment3 = new PremiumPreviewFragment("settings");
                                                    } else if (i == this.starsRow) {
                                                        baseFragment3 = new StarsIntroActivity();
                                                    } else if (i == this.businessRow) {
                                                        baseFragment3 = new PremiumPreviewFragment(1, "settings");
                                                    } else {
                                                        if (i == this.premiumGiftingRow) {
                                                            UserSelectorBottomSheet.open(0L, BirthdayController.getInstance(this.currentAccount).getState());
                                                            return;
                                                        }
                                                        if (i == this.botPermissionLocation) {
                                                            BotLocation botLocation = this.botLocation;
                                                            if (botLocation != null) {
                                                                botLocation.setGranted(!botLocation.granted(), new Runnable() {
                                                                    @Override
                                                                    public final void run() {
                                                                        ProfileActivity.this.lambda$createView$15(view);
                                                                    }
                                                                });
                                                                return;
                                                            }
                                                            return;
                                                        }
                                                        if (i == this.botPermissionBiometry) {
                                                            BotBiometry botBiometry = this.botBiometry;
                                                            if (botBiometry != null) {
                                                                botBiometry.setGranted(!botBiometry.granted());
                                                                ((TextCell) view).setChecked(this.botBiometry.granted());
                                                                return;
                                                            }
                                                            return;
                                                        }
                                                        if (i == this.botPermissionEmojiStatus) {
                                                            TextCell textCell = (TextCell) view;
                                                            textCell.setChecked(!textCell.isChecked());
                                                            if (this.botPermissionEmojiStatusReqId > 0) {
                                                                getConnectionsManager().cancelRequest(this.botPermissionEmojiStatusReqId, true);
                                                            }
                                                            TL_bots.toggleUserEmojiStatusPermission toggleuseremojistatuspermission = new TL_bots.toggleUserEmojiStatusPermission();
                                                            toggleuseremojistatuspermission.bot = getMessagesController().getInputUser(this.userId);
                                                            boolean isChecked = textCell.isChecked();
                                                            toggleuseremojistatuspermission.enabled = isChecked;
                                                            TLRPC.UserFull userFull = this.userInfo;
                                                            if (userFull != null) {
                                                                userFull.bot_can_manage_emoji_status = isChecked;
                                                            }
                                                            int sendRequest = getConnectionsManager().sendRequest(toggleuseremojistatuspermission, new RequestDelegate() {
                                                                @Override
                                                                public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                                                                    ProfileActivity.this.lambda$createView$17(r2, tLObject, tL_error);
                                                                }
                                                            });
                                                            this.botPermissionEmojiStatusReqId = sendRequest;
                                                            final int[] iArr = {sendRequest};
                                                            return;
                                                        }
                                                        if (i == this.bizHoursRow) {
                                                            this.hoursExpanded = !this.hoursExpanded;
                                                            saveScrollPosition();
                                                            view.requestLayout();
                                                            this.listAdapter.notifyItemChanged(this.bizHoursRow);
                                                            int i5 = this.savedScrollPosition;
                                                            if (i5 >= 0) {
                                                                this.layoutManager.scrollToPositionWithOffset(i5, this.savedScrollOffset - this.listView.getPaddingTop());
                                                                return;
                                                            }
                                                            return;
                                                        }
                                                        if (i == this.bizLocationRow) {
                                                            openLocation(false);
                                                            return;
                                                        }
                                                        if (i == this.channelRow) {
                                                            if (this.userInfo == null) {
                                                                return;
                                                            }
                                                            Bundle bundle6 = new Bundle();
                                                            bundle6.putLong("chat_id", this.userInfo.personal_channel_id);
                                                            baseFragment2 = new ChatActivity(bundle6);
                                                        } else if (i == this.birthdayRow) {
                                                            ProfileBirthdayEffect profileBirthdayEffect = this.birthdayEffect;
                                                            if ((profileBirthdayEffect == null || !profileBirthdayEffect.lambda$onDraw$0()) && !editRow(view, i)) {
                                                                TextDetailCell textDetailCell = (TextDetailCell) view;
                                                                if (textDetailCell.hasImage()) {
                                                                    onTextDetailCellImageClicked(textDetailCell.getImageView());
                                                                    return;
                                                                }
                                                                return;
                                                            }
                                                            return;
                                                        }
                                                    }
                                                }
                                            }
                                            Browser.openUrl(parentActivity, LocaleController.getString(i2));
                                            return;
                                        }
                                        baseFragment3 = new SessionsActivity(0);
                                    }
                                }
                                chatUsersActivity.setInfo(this.chatInfo);
                                baseFragment2 = chatUsersActivity;
                            }
                        }
                        processOnClickOrPress(i, view, f3, f4);
                        return;
                    }
                    onWriteButtonClick();
                    return;
                }
                builder = AlertsCreator.createTTLAlert(getParentActivity(), this.currentEncryptedChat, this.resourcesProvider);
                createSupportAlert = builder.create();
                showDialog(createSupportAlert);
                return;
            }
            Bundle bundle7 = new Bundle();
            bundle7.putInt("chat_id", DialogObject.getEncryptedChatId(this.dialogId));
            baseFragment2 = new IdenticonActivity(bundle7);
            presentFragment(baseFragment2);
        }
        TLRPC.User user2 = getMessagesController().getUser(Long.valueOf(this.userId));
        TLRPC.UserFull userFull2 = this.userInfo;
        if (userFull2 != null && userFull2.starref_program != null) {
            final long clientUserId = getUserConfig().getClientUserId();
            BotStarsController.getInstance(this.currentAccount).getConnectedBot(getContext(), clientUserId, this.userId, new Utilities.Callback() {
                @Override
                public final void run(Object obj) {
                    ProfileActivity.this.lambda$createView$10(context, clientUserId, (TL_payments.connectedBotStarRef) obj);
                }
            });
            return;
        } else if (user2 == null || !user2.bot_can_edit) {
            return;
        } else {
            baseFragment3 = new AffiliateProgramFragment(this.userId);
        }
        baseFragment2 = baseFragment3;
        presentFragment(baseFragment2);
    }

    public void lambda$createView$19(View view, int i) {
        boolean z;
        ArrayList arrayList;
        if (i < 0) {
            return;
        }
        Object valueOf = Integer.valueOf(this.numberRow);
        if (this.searchAdapter.searchWas) {
            if (i < this.searchAdapter.searchResults.size()) {
                arrayList = this.searchAdapter.searchResults;
            } else {
                i -= this.searchAdapter.searchResults.size() + 1;
                if (i >= 0 && i < this.searchAdapter.faqSearchResults.size()) {
                    arrayList = this.searchAdapter.faqSearchResults;
                }
                z = true;
            }
            valueOf = arrayList.get(i);
            z = true;
        } else {
            if (!this.searchAdapter.recentSearches.isEmpty()) {
                i--;
            }
            if (i < 0 || i >= this.searchAdapter.recentSearches.size()) {
                int size = i - (this.searchAdapter.recentSearches.size() + 1);
                if (size >= 0 && size < this.searchAdapter.faqSearchArray.size()) {
                    valueOf = this.searchAdapter.faqSearchArray.get(size);
                    z = false;
                }
                z = true;
            } else {
                arrayList = this.searchAdapter.recentSearches;
                valueOf = arrayList.get(i);
                z = true;
            }
        }
        if (valueOf instanceof SearchAdapter.SearchResult) {
            ((SearchAdapter.SearchResult) valueOf).open();
        } else if (valueOf instanceof MessagesController.FaqSearchResult) {
            NotificationCenter.getInstance(this.currentAccount).lambda$postNotificationNameOnUIThread$1(NotificationCenter.openArticle, this.searchAdapter.faqWebPage, ((MessagesController.FaqSearchResult) valueOf).url);
        }
        if (!z || valueOf == null) {
            return;
        }
        this.searchAdapter.addRecent(valueOf);
    }

    public void lambda$createView$20(DialogInterface dialogInterface, int i) {
        this.searchAdapter.clearRecent();
    }

    public boolean lambda$createView$21(View view, int i) {
        if (this.searchAdapter.isSearchWas() || this.searchAdapter.recentSearches.isEmpty()) {
            return false;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(getParentActivity(), this.resourcesProvider);
        builder.setTitle(LocaleController.getString(R.string.ClearSearchAlertTitle));
        builder.setMessage(LocaleController.getString(R.string.ClearSearchAlert));
        builder.setPositiveButton(LocaleController.getString(R.string.ClearButton), new DialogInterface.OnClickListener() {
            @Override
            public final void onClick(DialogInterface dialogInterface, int i2) {
                ProfileActivity.this.lambda$createView$20(dialogInterface, i2);
            }
        });
        builder.setNegativeButton(LocaleController.getString(R.string.Cancel), null);
        AlertDialog create = builder.create();
        showDialog(create);
        TextView textView = (TextView) create.getButton(-1);
        if (textView == null) {
            return true;
        }
        textView.setTextColor(Theme.getColor(Theme.key_text_RedBold));
        return true;
    }

    public void lambda$createView$22(TLObject tLObject) {
        this.currentChannelParticipant = ((TLRPC.TL_channels_channelParticipant) tLObject).participant;
    }

    public void lambda$createView$23(final TLObject tLObject, TLRPC.TL_error tL_error) {
        if (tLObject != null) {
            AndroidUtilities.runOnUIThread(new Runnable() {
                @Override
                public final void run() {
                    ProfileActivity.this.lambda$createView$22(tLObject);
                }
            });
        }
    }

    public void lambda$createView$24(TLRPC.Chat chat, View view) {
        long j = this.userId;
        long j2 = this.banFromGroup;
        TLRPC.TL_chatBannedRights tL_chatBannedRights = chat.default_banned_rights;
        TLRPC.ChannelParticipant channelParticipant = this.currentChannelParticipant;
        ChatRightsEditActivity chatRightsEditActivity = new ChatRightsEditActivity(j, j2, null, tL_chatBannedRights, channelParticipant != null ? channelParticipant.banned_rights : null, "", 1, true, false, null);
        chatRightsEditActivity.setDelegate(new AnonymousClass20(chat, chatRightsEditActivity));
        presentFragment(chatRightsEditActivity);
    }

    public void lambda$createView$25(TLRPC.InputStickerSet inputStickerSet) {
        ArrayList arrayList = new ArrayList(1);
        arrayList.add(inputStickerSet);
        showDialog(new EmojiPacksAlert(this, getParentActivity(), this.resourcesProvider, arrayList));
    }

    public void lambda$createView$26(View view) {
        TLRPC.Document findDocument;
        Bulletin createContainsEmojiBulletin;
        if (this.avatarBig != null) {
            return;
        }
        if (!this.isTopic || getMessagesController().premiumFeaturesBlocked()) {
            if (expandAvatar()) {
                return;
            }
            openAvatar();
            return;
        }
        ArrayList<TLRPC.TL_forumTopic> topics = getMessagesController().getTopicsController().getTopics(this.chatId);
        if (topics != null) {
            TLRPC.TL_forumTopic tL_forumTopic = null;
            for (int i = 0; tL_forumTopic == null && i < topics.size(); i++) {
                TLRPC.TL_forumTopic tL_forumTopic2 = topics.get(i);
                if (tL_forumTopic2 != null && tL_forumTopic2.id == this.topicId) {
                    tL_forumTopic = tL_forumTopic2;
                }
            }
            if (tL_forumTopic != null) {
                long j = tL_forumTopic.icon_emoji_id;
                if (j == 0 || (findDocument = AnimatedEmojiDrawable.findDocument(this.currentAccount, j)) == null || (createContainsEmojiBulletin = BulletinFactory.of(this).createContainsEmojiBulletin(findDocument, 1, new Utilities.Callback() {
                    @Override
                    public final void run(Object obj) {
                        ProfileActivity.this.lambda$createView$25((TLRPC.InputStickerSet) obj);
                    }
                })) == null) {
                    return;
                }
                createContainsEmojiBulletin.show();
            }
        }
    }

    public boolean lambda$createView$27(View view) {
        if (this.avatarBig == null && !this.isTopic) {
            openAvatar();
        }
        return false;
    }

    public void lambda$createView$28(View view) {
        if (this.writeButton.getTag() != null) {
            return;
        }
        onWriteButtonClick();
    }

    public void lambda$createView$29(ValueAnimator valueAnimator) {
        setAvatarExpandProgress(valueAnimator.getAnimatedFraction());
    }

    public void lambda$createView$30(View view) {
        finishPreviewFragment();
    }

    public void lambda$createView$6(long j, ArrayList arrayList, boolean z) {
        getMessagesController().getStoriesController().updateStoriesPinned(j, arrayList, z, null);
    }

    public void lambda$createView$7(boolean[] zArr, ArrayList arrayList, boolean[] zArr2, long j) {
        zArr[0] = true;
        AndroidUtilities.cancelRunOnUIThread(this.applyBulletin);
        for (int i = 0; i < arrayList.size(); i++) {
            ((TL_stories.StoryItem) arrayList.get(i)).pinned = zArr2[i];
        }
        getMessagesController().getStoriesController().updateStoriesInLists(j, arrayList);
    }

    public void lambda$createView$8(boolean[] zArr) {
        Runnable runnable;
        if (!zArr[0] && (runnable = this.applyBulletin) != null) {
            runnable.run();
        }
        this.applyBulletin = null;
    }

    public void lambda$createView$9(int i, View view) {
        int i2;
        if (i == 0 && !this.sharedMediaLayout.isActionModeShown()) {
            if (!getMessagesController().storiesEnabled()) {
                showDialog(new PremiumFeatureBottomSheet(this, 14, true));
                return;
            } else {
                getMessagesController().getMainSettings().edit().putBoolean("story_keep", true).apply();
                StoryRecorder.getInstance(getParentActivity(), getCurrentAccount()).closeToWhenSent(new StoryRecorder.ClosingViewProvider() {
                    AnonymousClass8() {
                    }

                    @Override
                    public StoryRecorder.SourceView getView(long j) {
                        if (j != ProfileActivity.this.getDialogId()) {
                            return null;
                        }
                        ProfileActivity.this.updateAvatarRoundRadius();
                        return StoryRecorder.SourceView.fromAvatarImage(ProfileActivity.this.avatarImage, ChatObject.isForum(ProfileActivity.this.currentChat));
                    }

                    @Override
                    public void preLayout(long j, Runnable runnable) {
                        ProfileActivity.this.avatarImage.setHasStories(ProfileActivity.this.needInsetForStories());
                        if (j == ProfileActivity.this.getDialogId()) {
                            ProfileActivity.this.collapseAvatarInstant();
                        }
                        AndroidUtilities.runOnUIThread(runnable, 30L);
                    }
                }).open(null);
                return;
            }
        }
        final long clientUserId = getUserConfig().getClientUserId();
        Runnable runnable = this.applyBulletin;
        if (runnable != null) {
            runnable.run();
            this.applyBulletin = null;
        }
        Bulletin.hideVisible();
        boolean z = this.sharedMediaLayout.getClosestTab() == 9;
        final ArrayList arrayList = new ArrayList();
        SparseArray<MessageObject> actionModeSelected = this.sharedMediaLayout.getActionModeSelected();
        if (actionModeSelected != null) {
            int i3 = 0;
            for (int i4 = 0; i4 < actionModeSelected.size(); i4++) {
                TL_stories.StoryItem storyItem = actionModeSelected.valueAt(i4).storyItem;
                if (storyItem != null) {
                    arrayList.add(storyItem);
                    i3++;
                }
            }
            i2 = i3;
        } else {
            i2 = 0;
        }
        this.sharedMediaLayout.closeActionMode(false);
        if (z) {
            this.sharedMediaLayout.scrollToPage(8);
        }
        if (arrayList.isEmpty()) {
            return;
        }
        final boolean[] zArr = new boolean[arrayList.size()];
        for (int i5 = 0; i5 < arrayList.size(); i5++) {
            TL_stories.StoryItem storyItem2 = (TL_stories.StoryItem) arrayList.get(i5);
            zArr[i5] = storyItem2.pinned;
            storyItem2.pinned = z;
        }
        getMessagesController().getStoriesController().updateStoriesInLists(clientUserId, arrayList);
        final boolean[] zArr2 = {false};
        final boolean z2 = z;
        this.applyBulletin = new Runnable() {
            @Override
            public final void run() {
                ProfileActivity.this.lambda$createView$6(clientUserId, arrayList, z2);
            }
        };
        Runnable runnable2 = new Runnable() {
            @Override
            public final void run() {
                ProfileActivity.this.lambda$createView$7(zArr2, arrayList, zArr, clientUserId);
            }
        };
        BulletinFactory of = BulletinFactory.of(this);
        (z ? of.createSimpleBulletin(R.raw.contact_check, LocaleController.formatPluralString("StorySavedTitle", i2, new Object[0]), LocaleController.getString(R.string.StorySavedSubtitle), LocaleController.getString(R.string.Undo), runnable2) : of.createSimpleBulletin(R.raw.chats_archived, LocaleController.formatPluralString("StoryArchived", i2, new Object[0]), LocaleController.getString(R.string.Undo), 5000, runnable2)).show().setOnHideListener(new Runnable() {
            @Override
            public final void run() {
                ProfileActivity.this.lambda$createView$8(zArr2);
            }
        });
    }

    public void lambda$didReceivedNotification$70(Object[] objArr) {
        NotificationCenter notificationCenter = getNotificationCenter();
        int i = NotificationCenter.closeChats;
        notificationCenter.removeObserver(this, i);
        getNotificationCenter().lambda$postNotificationNameOnUIThread$1(i, new Object[0]);
        TLRPC.EncryptedChat encryptedChat = (TLRPC.EncryptedChat) objArr[0];
        Bundle bundle = new Bundle();
        bundle.putInt("enc_id", encryptedChat.id);
        presentFragment(new ChatActivity(bundle), true);
    }

    public void lambda$didReceivedNotification$71() {
        updateListAnimated(false);
    }

    public void lambda$didUploadPhoto$83(TLRPC.TL_error tL_error, TLObject tLObject, String str) {
        if (tL_error == null) {
            TLRPC.User user = getMessagesController().getUser(Long.valueOf(getUserConfig().getClientUserId()));
            if (user == null) {
                user = getUserConfig().getCurrentUser();
                if (user == null) {
                    return;
                } else {
                    getMessagesController().putUser(user, false);
                }
            } else {
                getUserConfig().setCurrentUser(user);
            }
            TLRPC.TL_photos_photo tL_photos_photo = (TLRPC.TL_photos_photo) tLObject;
            ArrayList<TLRPC.PhotoSize> arrayList = tL_photos_photo.photo.sizes;
            TLRPC.PhotoSize closestPhotoSizeWithSize = FileLoader.getClosestPhotoSizeWithSize(arrayList, 150);
            TLRPC.PhotoSize closestPhotoSizeWithSize2 = FileLoader.getClosestPhotoSizeWithSize(arrayList, 800);
            TLRPC.VideoSize closestVideoSizeWithSize = tL_photos_photo.photo.video_sizes.isEmpty() ? null : FileLoader.getClosestVideoSizeWithSize(tL_photos_photo.photo.video_sizes, 1000);
            TLRPC.TL_userProfilePhoto tL_userProfilePhoto = new TLRPC.TL_userProfilePhoto();
            user.photo = tL_userProfilePhoto;
            tL_userProfilePhoto.photo_id = tL_photos_photo.photo.id;
            if (closestPhotoSizeWithSize != null) {
                tL_userProfilePhoto.photo_small = closestPhotoSizeWithSize.location;
            }
            if (closestPhotoSizeWithSize2 != null) {
                tL_userProfilePhoto.photo_big = closestPhotoSizeWithSize2.location;
            }
            if (closestPhotoSizeWithSize != null && this.avatar != null) {
                FileLoader.getInstance(this.currentAccount).getPathToAttach(this.avatar, true).renameTo(FileLoader.getInstance(this.currentAccount).getPathToAttach(closestPhotoSizeWithSize, true));
                ImageLoader.getInstance().replaceImageInCache(this.avatar.volume_id + "_" + this.avatar.local_id + "@50_50", closestPhotoSizeWithSize.location.volume_id + "_" + closestPhotoSizeWithSize.location.local_id + "@50_50", ImageLocation.getForUserOrChat(user, 1), false);
            }
            if (closestVideoSizeWithSize != null && str != null) {
                new File(str).renameTo(FileLoader.getInstance(this.currentAccount).getPathToAttach(closestVideoSizeWithSize, "mp4", true));
            } else if (closestPhotoSizeWithSize2 != null && this.avatarBig != null) {
                FileLoader.getInstance(this.currentAccount).getPathToAttach(this.avatarBig, true).renameTo(FileLoader.getInstance(this.currentAccount).getPathToAttach(closestPhotoSizeWithSize2, true));
            }
            getMessagesController().getDialogPhotos(user.id).addPhotoAtStart(tL_photos_photo.photo);
            ArrayList arrayList2 = new ArrayList();
            arrayList2.add(user);
            getMessagesStorage().putUsersAndChats(arrayList2, null, false, true);
            TLRPC.UserFull userFull = getMessagesController().getUserFull(this.userId);
            if (userFull != null) {
                userFull.profile_photo = tL_photos_photo.photo;
                getMessagesStorage().updateUserInfo(userFull, false);
            }
        }
        this.allowPullingDown = (AndroidUtilities.isTablet() || this.isInLandscapeMode || !this.avatarImage.getImageReceiver().hasNotThumb() || AndroidUtilities.isAccessibilityScreenReaderEnabled()) ? false : true;
        this.avatar = null;
        this.avatarBig = null;
        ProfileGalleryView profileGalleryView = this.avatarsViewPager;
        profileGalleryView.scrolledByUser = true;
        profileGalleryView.removeUploadingImage(this.uploadingImageLocation);
        this.avatarsViewPager.setCreateThumbFromParent(false);
        updateProfileData(true);
        showAvatarProgress(false, true);
        getNotificationCenter().lambda$postNotificationNameOnUIThread$1(NotificationCenter.updateInterfaces, Integer.valueOf(MessagesController.UPDATE_MASK_ALL));
        getNotificationCenter().lambda$postNotificationNameOnUIThread$1(NotificationCenter.mainUserInfoChanged, new Object[0]);
        getUserConfig().saveConfig(true);
    }

    public void lambda$didUploadPhoto$84(final String str, final TLObject tLObject, final TLRPC.TL_error tL_error) {
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                ProfileActivity.this.lambda$didUploadPhoto$83(tL_error, tLObject, str);
            }
        });
    }

    public void lambda$didUploadPhoto$85(TLRPC.InputFile inputFile, TLRPC.InputFile inputFile2, TLRPC.VideoSize videoSize, double d, final String str, TLRPC.PhotoSize photoSize, TLRPC.PhotoSize photoSize2) {
        if (inputFile == null && inputFile2 == null && videoSize == null) {
            TLRPC.FileLocation fileLocation = photoSize.location;
            this.avatar = fileLocation;
            this.avatarBig = photoSize2.location;
            this.avatarImage.setImage(ImageLocation.getForLocal(fileLocation), "50_50", this.avatarDrawable, (Object) null);
            if (this.setAvatarRow != -1) {
                updateRowsIds();
                ListAdapter listAdapter = this.listAdapter;
                if (listAdapter != null) {
                    listAdapter.notifyDataSetChanged();
                }
                needLayout(true);
            }
            ProfileGalleryView profileGalleryView = this.avatarsViewPager;
            ImageLocation forLocal = ImageLocation.getForLocal(this.avatarBig);
            this.uploadingImageLocation = forLocal;
            profileGalleryView.addUploadingImage(forLocal, ImageLocation.getForLocal(this.avatar));
            showAvatarProgress(true, false);
        } else {
            if (this.avatar == null) {
                return;
            }
            TLRPC.TL_photos_uploadProfilePhoto tL_photos_uploadProfilePhoto = new TLRPC.TL_photos_uploadProfilePhoto();
            if (inputFile != null) {
                tL_photos_uploadProfilePhoto.file = inputFile;
                tL_photos_uploadProfilePhoto.flags |= 1;
            }
            if (inputFile2 != null) {
                tL_photos_uploadProfilePhoto.video = inputFile2;
                int i = tL_photos_uploadProfilePhoto.flags;
                tL_photos_uploadProfilePhoto.video_start_ts = d;
                tL_photos_uploadProfilePhoto.flags = i | 6;
            }
            if (videoSize != null) {
                tL_photos_uploadProfilePhoto.video_emoji_markup = videoSize;
                tL_photos_uploadProfilePhoto.flags |= 16;
            }
            this.avatarUploadingRequest = getConnectionsManager().sendRequest(tL_photos_uploadProfilePhoto, new RequestDelegate() {
                @Override
                public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                    ProfileActivity.this.lambda$didUploadPhoto$84(str, tLObject, tL_error);
                }
            });
        }
        this.actionBar.createMenu().requestLayout();
    }

    public void lambda$dimBehindView$88(ValueAnimator valueAnimator) {
        this.scrimPaint.setAlpha((int) (((Float) valueAnimator.getAnimatedValue()).floatValue() * 255.0f));
    }

    public void lambda$editRow$100(final TLRPC.UserFull userFull, final int i) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getParentActivity());
        builder.setTitle(LocaleController.getString(R.string.BusinessHoursClearTitle));
        builder.setMessage(LocaleController.getString(R.string.BusinessHoursClearMessage));
        builder.setPositiveButton(LocaleController.getString(R.string.Remove), new DialogInterface.OnClickListener() {
            @Override
            public final void onClick(DialogInterface dialogInterface, int i2) {
                ProfileActivity.this.lambda$editRow$99(userFull, i, dialogInterface, i2);
            }
        });
        builder.setNegativeButton(LocaleController.getString(R.string.Cancel), null);
        showDialog(builder.create());
    }

    public void lambda$editRow$101() {
        presentFragment(new org.telegram.ui.Business.LocationActivity());
    }

    public void lambda$editRow$102(TLRPC.TL_error tL_error, TLObject tLObject) {
        if (tL_error != null) {
            BulletinFactory.showError(tL_error);
        } else if (tLObject instanceof TLRPC.TL_boolFalse) {
            BulletinFactory.of(this).createErrorBulletin(LocaleController.getString(R.string.UnknownError)).show();
        }
    }

    public void lambda$editRow$103(final TLObject tLObject, final TLRPC.TL_error tL_error) {
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                ProfileActivity.this.lambda$editRow$102(tL_error, tLObject);
            }
        });
    }

    public void lambda$editRow$104(TLRPC.UserFull userFull, int i, DialogInterface dialogInterface, int i2) {
        TL_account.updateBusinessLocation updatebusinesslocation = new TL_account.updateBusinessLocation();
        if (userFull != null) {
            userFull.business_location = null;
            userFull.flags2 &= -3;
        }
        getConnectionsManager().sendRequest(updatebusinesslocation, new RequestDelegate() {
            @Override
            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                ProfileActivity.this.lambda$editRow$103(tLObject, tL_error);
            }
        });
        updateRowsIds();
        this.listAdapter.notifyItemRemoved(i);
        getMessagesStorage().updateUserInfo(userFull, false);
    }

    public void lambda$editRow$105(final TLRPC.UserFull userFull, final int i) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getParentActivity());
        builder.setTitle(LocaleController.getString(R.string.BusinessLocationClearTitle));
        builder.setMessage(LocaleController.getString(R.string.BusinessLocationClearMessage));
        builder.setPositiveButton(LocaleController.getString(R.string.Remove), new DialogInterface.OnClickListener() {
            @Override
            public final void onClick(DialogInterface dialogInterface, int i2) {
                ProfileActivity.this.lambda$editRow$104(userFull, i, dialogInterface, i2);
            }
        });
        builder.setNegativeButton(LocaleController.getString(R.string.Cancel), null);
        showDialog(builder.create());
    }

    public void lambda$editRow$106() {
        presentFragment(new ChangeUsernameActivity());
    }

    public void lambda$editRow$107() {
        presentFragment(new UserInfoActivity());
    }

    public void lambda$editRow$108() {
        presentFragment(new ActionIntroActivity(3));
    }

    public void lambda$editRow$109(TLObject tLObject, TLRPC.UserFull userFull, TL_account.TL_birthday tL_birthday, TLRPC.TL_error tL_error) {
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

    public void lambda$editRow$110(final TLRPC.UserFull userFull, final TL_account.TL_birthday tL_birthday, final TLObject tLObject, final TLRPC.TL_error tL_error) {
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                ProfileActivity.this.lambda$editRow$109(tLObject, userFull, tL_birthday, tL_error);
            }
        });
    }

    public void lambda$editRow$111(final TLRPC.UserFull userFull, TL_account.TL_birthday tL_birthday) {
        TL_account.updateBirthday updatebirthday = new TL_account.updateBirthday();
        updatebirthday.flags |= 1;
        updatebirthday.birthday = tL_birthday;
        final TL_account.TL_birthday tL_birthday2 = userFull != null ? userFull.birthday : null;
        if (userFull != null) {
            userFull.flags2 |= 32;
            userFull.birthday = tL_birthday;
        }
        getMessagesController().invalidateContentSettings();
        getConnectionsManager().sendRequest(updatebirthday, new RequestDelegate() {
            @Override
            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                ProfileActivity.this.lambda$editRow$110(userFull, tL_birthday2, tLObject, tL_error);
            }
        }, 1024);
    }

    public void lambda$editRow$112() {
        BaseFragment.BottomSheetParams bottomSheetParams = new BaseFragment.BottomSheetParams();
        bottomSheetParams.transitionFromLeft = true;
        bottomSheetParams.allowNestedScroll = false;
        showAsSheet(new PrivacyControlActivity(11), bottomSheetParams);
    }

    public void lambda$editRow$113(final TLRPC.UserFull userFull) {
        showDialog(AlertsCreator.createBirthdayPickerDialog(getContext(), LocaleController.getString(R.string.EditProfileBirthdayTitle), LocaleController.getString(R.string.EditProfileBirthdayButton), userFull.birthday, new Utilities.Callback() {
            @Override
            public final void run(Object obj) {
                ProfileActivity.this.lambda$editRow$111(userFull, (TL_account.TL_birthday) obj);
            }
        }, new Runnable() {
            @Override
            public final void run() {
                ProfileActivity.this.lambda$editRow$112();
            }
        }, getResourceProvider()).create());
    }

    public void lambda$editRow$114(TLRPC.TL_error tL_error, TLObject tLObject) {
        if (tL_error != null) {
            BulletinFactory.showError(tL_error);
        } else if (tLObject instanceof TLRPC.TL_boolFalse) {
            BulletinFactory.of(this).createErrorBulletin(LocaleController.getString(R.string.UnknownError)).show();
        }
    }

    public void lambda$editRow$115(final TLObject tLObject, final TLRPC.TL_error tL_error) {
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                ProfileActivity.this.lambda$editRow$114(tL_error, tLObject);
            }
        });
    }

    public void lambda$editRow$116(TLRPC.UserFull userFull, DialogInterface dialogInterface, int i) {
        TL_account.updateBirthday updatebirthday = new TL_account.updateBirthday();
        if (userFull != null) {
            userFull.birthday = null;
            userFull.flags2 &= -33;
        }
        getMessagesController().invalidateContentSettings();
        getConnectionsManager().sendRequest(updatebirthday, new RequestDelegate() {
            @Override
            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                ProfileActivity.this.lambda$editRow$115(tLObject, tL_error);
            }
        });
        updateListAnimated(false);
        getMessagesStorage().updateUserInfo(userFull, false);
    }

    public void lambda$editRow$117(final TLRPC.UserFull userFull) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getParentActivity());
        builder.setTitle(LocaleController.getString(R.string.BirthdayClearTitle));
        builder.setMessage(LocaleController.getString(R.string.BirthdayClearMessage));
        builder.setPositiveButton(LocaleController.getString(R.string.Remove), new DialogInterface.OnClickListener() {
            @Override
            public final void onClick(DialogInterface dialogInterface, int i) {
                ProfileActivity.this.lambda$editRow$116(userFull, dialogInterface, i);
            }
        });
        builder.setNegativeButton(LocaleController.getString(R.string.Cancel), null);
        showDialog(builder.create());
    }

    public void lambda$editRow$118(TLRPC.Chat chat) {
        AndroidUtilities.addToClipboard("https://" + getMessagesController().linkPrefix + "/" + ChatObject.getPublicUsername(chat));
    }

    public void lambda$editRow$119() {
        presentFragment(new UserInfoActivity());
    }

    public void lambda$editRow$120(TLRPC.TL_error tL_error, TLObject tLObject) {
        if (tL_error != null) {
            BulletinFactory.showError(tL_error);
        } else if (tLObject instanceof TLRPC.TL_boolFalse) {
            BulletinFactory.of(this).createErrorBulletin(LocaleController.getString(R.string.UnknownError)).show();
        }
    }

    public void lambda$editRow$121(final TLObject tLObject, final TLRPC.TL_error tL_error) {
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                ProfileActivity.this.lambda$editRow$120(tL_error, tLObject);
            }
        });
    }

    public void lambda$editRow$122(TLRPC.UserFull userFull, DialogInterface dialogInterface, int i) {
        TL_account.updatePersonalChannel updatepersonalchannel = new TL_account.updatePersonalChannel();
        updatepersonalchannel.channel = new TLRPC.TL_inputChannelEmpty();
        if (userFull != null) {
            userFull.personal_channel_id = 0L;
            userFull.personal_channel_message = 0;
            userFull.flags2 &= -65;
        }
        getConnectionsManager().sendRequest(updatepersonalchannel, new RequestDelegate() {
            @Override
            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                ProfileActivity.this.lambda$editRow$121(tLObject, tL_error);
            }
        });
        updateListAnimated(false);
        getMessagesStorage().updateUserInfo(userFull, false);
    }

    public void lambda$editRow$123(final TLRPC.UserFull userFull) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getParentActivity());
        builder.setTitle(LocaleController.getString(R.string.ProfileChannelClearTitle));
        builder.setMessage(LocaleController.getString(R.string.ProfileChannelClearMessage));
        builder.setPositiveButton(LocaleController.getString(R.string.Remove), new DialogInterface.OnClickListener() {
            @Override
            public final void onClick(DialogInterface dialogInterface, int i) {
                ProfileActivity.this.lambda$editRow$122(userFull, dialogInterface, i);
            }
        });
        builder.setNegativeButton(LocaleController.getString(R.string.Cancel), null);
        showDialog(builder.create());
    }

    public void lambda$editRow$93() {
        openLocation(false);
    }

    public void lambda$editRow$94() {
        openLocation(true);
    }

    public void lambda$editRow$96() {
        presentFragment(new OpeningHoursActivity());
    }

    public void lambda$editRow$97(TLRPC.TL_error tL_error, TLObject tLObject) {
        if (tL_error != null) {
            BulletinFactory.showError(tL_error);
        } else if (tLObject instanceof TLRPC.TL_boolFalse) {
            BulletinFactory.of(this).createErrorBulletin(LocaleController.getString(R.string.UnknownError)).show();
        }
    }

    public void lambda$editRow$98(final TLObject tLObject, final TLRPC.TL_error tL_error) {
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                ProfileActivity.this.lambda$editRow$97(tL_error, tLObject);
            }
        });
    }

    public void lambda$editRow$99(TLRPC.UserFull userFull, int i, DialogInterface dialogInterface, int i2) {
        TL_account.updateBusinessWorkHours updatebusinessworkhours = new TL_account.updateBusinessWorkHours();
        if (userFull != null) {
            userFull.business_work_hours = null;
            userFull.flags2 &= -2;
        }
        getConnectionsManager().sendRequest(updatebusinessworkhours, new RequestDelegate() {
            @Override
            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                ProfileActivity.this.lambda$editRow$98(tLObject, tL_error);
            }
        });
        updateRowsIds();
        this.listAdapter.notifyItemRemoved(i);
        getMessagesStorage().updateUserInfo(userFull, false);
    }

    public void lambda$getChannelParticipants$62(TLRPC.TL_error tL_error, TLObject tLObject, TLRPC.TL_channels_getParticipants tL_channels_getParticipants) {
        if (tL_error == null) {
            TLRPC.TL_channels_channelParticipants tL_channels_channelParticipants = (TLRPC.TL_channels_channelParticipants) tLObject;
            getMessagesController().putUsers(tL_channels_channelParticipants.users, false);
            getMessagesController().putChats(tL_channels_channelParticipants.chats, false);
            if (tL_channels_channelParticipants.users.size() < 200) {
                this.usersEndReached = true;
            }
            if (tL_channels_getParticipants.offset == 0) {
                this.participantsMap.clear();
                this.chatInfo.participants = new TLRPC.TL_chatParticipants();
                getMessagesStorage().putUsersAndChats(tL_channels_channelParticipants.users, tL_channels_channelParticipants.chats, true, true);
                getMessagesStorage().updateChannelUsers(this.chatId, tL_channels_channelParticipants.participants);
            }
            for (int i = 0; i < tL_channels_channelParticipants.participants.size(); i++) {
                TLRPC.TL_chatChannelParticipant tL_chatChannelParticipant = new TLRPC.TL_chatChannelParticipant();
                TLRPC.ChannelParticipant channelParticipant = tL_channels_channelParticipants.participants.get(i);
                tL_chatChannelParticipant.channelParticipant = channelParticipant;
                tL_chatChannelParticipant.inviter_id = channelParticipant.inviter_id;
                long peerId = MessageObject.getPeerId(channelParticipant.peer);
                tL_chatChannelParticipant.user_id = peerId;
                tL_chatChannelParticipant.date = tL_chatChannelParticipant.channelParticipant.date;
                if (this.participantsMap.indexOfKey(peerId) < 0) {
                    TLRPC.ChatFull chatFull = this.chatInfo;
                    if (chatFull.participants == null) {
                        chatFull.participants = new TLRPC.TL_chatParticipants();
                    }
                    this.chatInfo.participants.participants.add(tL_chatChannelParticipant);
                    this.participantsMap.put(tL_chatChannelParticipant.user_id, tL_chatChannelParticipant);
                }
            }
        }
        this.loadingUsers = false;
        saveScrollPosition();
        updateListAnimated(true);
    }

    public void lambda$getChannelParticipants$63(final TLRPC.TL_error tL_error, final TLObject tLObject, final TLRPC.TL_channels_getParticipants tL_channels_getParticipants) {
        getNotificationCenter().doOnIdle(new Runnable() {
            @Override
            public final void run() {
                ProfileActivity.this.lambda$getChannelParticipants$62(tL_error, tLObject, tL_channels_getParticipants);
            }
        });
    }

    public void lambda$getChannelParticipants$64(final TLRPC.TL_channels_getParticipants tL_channels_getParticipants, int i, final TLObject tLObject, final TLRPC.TL_error tL_error) {
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                ProfileActivity.this.lambda$getChannelParticipants$63(tL_error, tLObject, tL_channels_getParticipants);
            }
        }, i);
    }

    public void lambda$getThemeDescriptions$89() {
        SimpleTextView simpleTextView;
        int themedColor;
        Boolean bool;
        RecyclerListView recyclerListView = this.listView;
        if (recyclerListView != null) {
            int childCount = recyclerListView.getChildCount();
            for (int i = 0; i < childCount; i++) {
                View childAt = this.listView.getChildAt(i);
                if (childAt instanceof UserCell) {
                    ((UserCell) childAt).update(0);
                }
            }
        }
        if (!this.isPulledDown) {
            SimpleTextView simpleTextView2 = this.onlineTextView[1];
            if (simpleTextView2 != null) {
                Object tag = simpleTextView2.getTag();
                for (int i2 = 0; i2 < 2; i2++) {
                    if (tag instanceof Integer) {
                        simpleTextView = this.onlineTextView[i2 + 1];
                        themedColor = getThemedColor(((Integer) tag).intValue());
                        bool = Boolean.valueOf(this.isOnline[0]);
                    } else {
                        simpleTextView = this.onlineTextView[i2 + 1];
                        themedColor = getThemedColor(Theme.key_avatar_subtitleInProfileBlue);
                        bool = Boolean.TRUE;
                    }
                    simpleTextView.setTextColor(applyPeerColor(themedColor, true, bool));
                }
            }
            Drawable drawable = this.lockIconDrawable;
            if (drawable != null) {
                drawable.setColorFilter(getThemedColor(Theme.key_chat_lockIcon), PorterDuff.Mode.MULTIPLY);
            }
            ScamDrawable scamDrawable = this.scamDrawable;
            if (scamDrawable != null) {
                scamDrawable.setColor(getThemedColor(Theme.key_avatar_subtitleInProfileBlue));
            }
            SimpleTextView simpleTextView3 = this.nameTextView[1];
            if (simpleTextView3 != null) {
                simpleTextView3.setTextColor(getThemedColor(Theme.key_profile_title));
            }
            ActionBar actionBar = this.actionBar;
            if (actionBar != null) {
                actionBar.setItemsColor(this.peerColor != null ? -1 : getThemedColor(Theme.key_actionBarDefaultIcon), false);
                this.actionBar.setItemsBackgroundColor(this.peerColor != null ? 553648127 : getThemedColor(Theme.key_avatar_actionBarSelectorBlue), false);
            }
        }
        updateEmojiStatusDrawableColor();
    }

    public void lambda$hideFloatingButton$37(ValueAnimator valueAnimator) {
        this.floatingButtonHideProgress = ((Float) valueAnimator.getAnimatedValue()).floatValue();
        updateFloatingButtonOffset();
    }

    public void lambda$leaveChatPressed$61(boolean z) {
        this.playProfileAnimation = 0;
        NotificationCenter notificationCenter = getNotificationCenter();
        int i = NotificationCenter.closeChats;
        notificationCenter.removeObserver(this, i);
        getNotificationCenter().lambda$postNotificationNameOnUIThread$1(i, new Object[0]);
        lambda$onBackPressed$321();
        getNotificationCenter().lambda$postNotificationNameOnUIThread$1(NotificationCenter.needDeleteDialog, Long.valueOf(-this.currentChat.id), null, this.currentChat, Boolean.valueOf(z));
    }

    public void lambda$onCustomTransitionAnimation$72(ValueAnimator valueAnimator) {
        View view = this.fragmentView;
        if (view != null) {
            view.invalidate();
        }
        updateStoriesViewBounds(true);
    }

    public void lambda$onFragmentCreate$0(CountDownLatch countDownLatch) {
        this.currentChat = getMessagesStorage().getChat(this.chatId);
        countDownLatch.countDown();
    }

    public void lambda$onFragmentCreate$1(TLObject tLObject, TLRPC.TL_error tL_error) {
        if (tLObject instanceof TL_account.TL_password) {
            this.currentPassword = (TL_account.TL_password) tLObject;
        }
    }

    public void lambda$onMemberClick$40(TLRPC.ChannelParticipant channelParticipant, TLRPC.User user, TLRPC.ChatParticipant chatParticipant, boolean z, Integer num) {
        if (channelParticipant != null) {
            openRightsEdit(num.intValue(), user, chatParticipant, channelParticipant.admin_rights, channelParticipant.banned_rights, channelParticipant.rank, z);
        } else {
            openRightsEdit(num.intValue(), user, chatParticipant, null, null, "", z);
        }
    }

    public static void lambda$onMemberClick$41(Utilities.Callback callback) {
        callback.run(0);
    }

    public static void lambda$onMemberClick$42(Utilities.Callback callback, DialogInterface dialogInterface, int i) {
        callback.run(1);
    }

    public void lambda$onMemberClick$43(TLRPC.ChannelParticipant channelParticipant, TLRPC.ChatParticipant chatParticipant, TLRPC.User user, final Utilities.Callback callback) {
        if ((channelParticipant instanceof TLRPC.TL_channelParticipantAdmin) || (chatParticipant instanceof TLRPC.TL_chatParticipantAdmin)) {
            showDialog(new AlertDialog.Builder(getParentActivity(), this.resourcesProvider).setTitle(LocaleController.getString(R.string.AppName)).setMessage(LocaleController.formatString("AdminWillBeRemoved", R.string.AdminWillBeRemoved, ContactsController.formatName(user.first_name, user.last_name))).setPositiveButton(LocaleController.getString(R.string.OK), new DialogInterface.OnClickListener() {
                @Override
                public final void onClick(DialogInterface dialogInterface, int i) {
                    ProfileActivity.lambda$onMemberClick$42(Utilities.Callback.this, dialogInterface, i);
                }
            }).setNegativeButton(LocaleController.getString(R.string.Cancel), null).create());
        } else {
            callback.run(1);
        }
    }

    public void lambda$onMemberClick$44(TLRPC.ChatParticipant chatParticipant) {
        kickUser(this.selectedUser, chatParticipant);
    }

    public void lambda$onWriteButtonClick$38() {
        MessagesController.getInstance(this.currentAccount).deleteUserPhoto(null);
        this.cameraDrawable.setCurrentFrame(0);
        this.cellCameraDrawable.setCurrentFrame(0);
    }

    public void lambda$onWriteButtonClick$39(DialogInterface dialogInterface) {
        if (this.imageUpdater.isUploadingImage()) {
            this.cameraDrawable.setCurrentFrame(0, false);
            this.cellCameraDrawable.setCurrentFrame(0, false);
            return;
        }
        this.cameraDrawable.setCustomEndFrame(86);
        this.cellCameraDrawable.setCustomEndFrame(86);
        this.writeButton.playAnimation();
        TextCell textCell = this.setAvatarCell;
        if (textCell != null) {
            textCell.getImageView().playAnimation();
        }
    }

    public void lambda$openAddMember$67(TLRPC.User user) {
        for (int i = 0; i < this.chatInfo.participants.participants.size(); i++) {
            if (this.chatInfo.participants.participants.get(i).user_id == user.id) {
                this.chatInfo.participants.participants.remove(i);
                updateListAnimated(true);
                return;
            }
        }
    }

    public void lambda$openAddMember$68(ArrayList arrayList, HashSet hashSet) {
        TLRPC.TL_chatParticipant tL_chatParticipant;
        int size = arrayList.size();
        for (int i = 0; i < size; i++) {
            TLRPC.User user = (TLRPC.User) arrayList.get(i);
            if (!hashSet.contains(Long.valueOf(user.id))) {
                TLRPC.ChatFull chatFull = this.chatInfo;
                if (chatFull.participants == null) {
                    chatFull.participants = new TLRPC.TL_chatParticipants();
                }
                if (ChatObject.isChannel(this.currentChat)) {
                    TLRPC.TL_chatChannelParticipant tL_chatChannelParticipant = new TLRPC.TL_chatChannelParticipant();
                    TLRPC.TL_channelParticipant tL_channelParticipant = new TLRPC.TL_channelParticipant();
                    tL_chatChannelParticipant.channelParticipant = tL_channelParticipant;
                    tL_channelParticipant.inviter_id = getUserConfig().getClientUserId();
                    tL_chatChannelParticipant.channelParticipant.peer = new TLRPC.TL_peerUser();
                    TLRPC.ChannelParticipant channelParticipant = tL_chatChannelParticipant.channelParticipant;
                    channelParticipant.peer.user_id = user.id;
                    channelParticipant.date = getConnectionsManager().getCurrentTime();
                    tL_chatChannelParticipant.user_id = user.id;
                    tL_chatParticipant = tL_chatChannelParticipant;
                } else {
                    TLRPC.TL_chatParticipant tL_chatParticipant2 = new TLRPC.TL_chatParticipant();
                    tL_chatParticipant2.user_id = user.id;
                    tL_chatParticipant2.inviter_id = getAccountInstance().getUserConfig().clientUserId;
                    tL_chatParticipant = tL_chatParticipant2;
                }
                this.chatInfo.participants.participants.add(tL_chatParticipant);
                this.chatInfo.participants_count++;
                getMessagesController().putUser(user, false);
            }
        }
        updateListAnimated(true);
    }

    public void lambda$openAddMember$69(ArrayList arrayList, int i) {
        TLRPC.ChatParticipants chatParticipants;
        final HashSet hashSet = new HashSet();
        final ArrayList arrayList2 = new ArrayList();
        TLRPC.ChatFull chatFull = this.chatInfo;
        if (chatFull != null && (chatParticipants = chatFull.participants) != null && chatParticipants.participants != null) {
            for (int i2 = 0; i2 < this.chatInfo.participants.participants.size(); i2++) {
                hashSet.add(Long.valueOf(this.chatInfo.participants.participants.get(i2).user_id));
            }
        }
        getMessagesController().addUsersToChat(this.currentChat, this, arrayList, i, new androidx.core.util.Consumer() {
            @Override
            public final void accept(Object obj) {
                arrayList2.add((TLRPC.User) obj);
            }
        }, new androidx.core.util.Consumer() {
            @Override
            public final void accept(Object obj) {
                ProfileActivity.this.lambda$openAddMember$67((TLRPC.User) obj);
            }
        }, new Runnable() {
            @Override
            public final void run() {
                ProfileActivity.this.lambda$openAddMember$68(arrayList2, hashSet);
            }
        });
    }

    public void lambda$openAddToContact$91(TLRPC.User user) {
        if (this.addToContactsRow >= 0) {
            if (this.sharedMediaRow == -1) {
                updateRowsIds();
                this.listAdapter.notifyDataSetChanged();
            } else {
                updateListAnimated(false);
            }
        }
        if (this.sharedMediaRow == -1) {
            if (this.isInLandscapeMode || AndroidUtilities.isTablet()) {
                this.listView.setPadding(0, AndroidUtilities.dp(88.0f), 0, 0);
                this.expandAnimator.cancel();
                float[] fArr = this.expandAnimatorValues;
                fArr[0] = 1.0f;
                fArr[1] = 0.0f;
                setAvatarExpandProgress(1.0f);
                this.extraHeight = AndroidUtilities.dp(88.0f);
            } else {
                int currentActionBarHeight = ActionBar.getCurrentActionBarHeight() + (this.actionBar.getOccupyStatusBar() ? AndroidUtilities.statusBarHeight : 0);
                int makeMeasureSpec = View.MeasureSpec.makeMeasureSpec(this.listView.getMeasuredWidth(), 1073741824);
                int makeMeasureSpec2 = View.MeasureSpec.makeMeasureSpec(this.listView.getMeasuredHeight(), 0);
                int i = 0;
                for (int i2 = 0; i2 < this.listAdapter.getItemCount(); i2++) {
                    ListAdapter listAdapter = this.listAdapter;
                    RecyclerView.ViewHolder createViewHolder = listAdapter.createViewHolder(null, listAdapter.getItemViewType(i2));
                    this.listAdapter.onBindViewHolder(createViewHolder, i2);
                    createViewHolder.itemView.measure(makeMeasureSpec, makeMeasureSpec2);
                    i += createViewHolder.itemView.getMeasuredHeight();
                }
                int max = Math.max(0, this.fragmentView.getMeasuredHeight() - ((i + AndroidUtilities.dp(88.0f)) + currentActionBarHeight));
                RecyclerListView recyclerListView = this.listView;
                recyclerListView.setPadding(0, recyclerListView.getPaddingTop(), 0, max);
            }
        }
        this.undoView.showWithAction(this.dialogId, 8, user);
    }

    public void lambda$processOnClickOrPress$45(TL_fragment.TL_collectibleInfo tL_collectibleInfo) {
        Bulletin.hideVisible();
        Browser.openUrl(getContext(), tL_collectibleInfo.url);
    }

    public void lambda$processOnClickOrPress$46(TL_fragment.TL_collectibleInfo tL_collectibleInfo, View view) {
        Bulletin.hideVisible();
        Browser.openUrl(getContext(), tL_collectibleInfo.url);
    }

    public void lambda$processOnClickOrPress$47(TLObject tLObject, TLRPC.TL_username tL_username, ShareAlert shareAlert, TLRPC.TL_error tL_error) {
        String str;
        if (!(tLObject instanceof TL_fragment.TL_collectibleInfo)) {
            BulletinFactory.showError(tL_error);
            return;
        }
        final TL_fragment.TL_collectibleInfo tL_collectibleInfo = (TL_fragment.TL_collectibleInfo) tLObject;
        if (this.userId != 0) {
            getMessagesController().getUser(Long.valueOf(this.userId));
        } else {
            getMessagesController().getChat(Long.valueOf(this.chatId));
        }
        String str2 = "@" + tL_username.username;
        String format = LocaleController.getInstance().getFormatterBoostExpired().format(new Date(tL_collectibleInfo.purchase_date * 1000));
        String formatCurrency = BillingController.getInstance().formatCurrency(tL_collectibleInfo.crypto_amount, tL_collectibleInfo.crypto_currency);
        String formatCurrency2 = BillingController.getInstance().formatCurrency(tL_collectibleInfo.amount, tL_collectibleInfo.currency);
        BulletinFactory of = BulletinFactory.of(shareAlert.bulletinContainer2, this.resourcesProvider);
        int i = R.drawable.filled_username;
        int i2 = R.string.FragmentChannelUsername;
        if (TextUtils.isEmpty(formatCurrency2)) {
            str = "";
        } else {
            str = "(" + formatCurrency2 + ")";
        }
        of.createImageBulletin(i, AndroidUtilities.withLearnMore(AndroidUtilities.replaceTags(LocaleController.formatString(i2, str2, format, formatCurrency, str)), new Runnable() {
            @Override
            public final void run() {
                ProfileActivity.this.lambda$processOnClickOrPress$45(tL_collectibleInfo);
            }
        })).setOnClickListener(new View.OnClickListener() {
            @Override
            public final void onClick(View view) {
                ProfileActivity.this.lambda$processOnClickOrPress$46(tL_collectibleInfo, view);
            }
        }).show(false);
    }

    public void lambda$processOnClickOrPress$48(final TLRPC.TL_username tL_username, final ShareAlert shareAlert, final TLObject tLObject, final TLRPC.TL_error tL_error) {
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                ProfileActivity.this.lambda$processOnClickOrPress$47(tLObject, tL_username, shareAlert, tL_error);
            }
        });
    }

    public void lambda$processOnClickOrPress$49(TLObject tLObject, TLRPC.TL_username tL_username, TLRPC.TL_error tL_error) {
        if (tLObject instanceof TL_fragment.TL_collectibleInfo) {
            FragmentUsernameBottomSheet.open(getContext(), 0, tL_username.username, this.userId != 0 ? getMessagesController().getUser(Long.valueOf(this.userId)) : getMessagesController().getChat(Long.valueOf(this.chatId)), (TL_fragment.TL_collectibleInfo) tLObject, getResourceProvider());
        } else {
            BulletinFactory.showError(tL_error);
        }
    }

    public void lambda$processOnClickOrPress$50(final TLRPC.TL_username tL_username, final TLObject tLObject, final TLRPC.TL_error tL_error) {
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                ProfileActivity.this.lambda$processOnClickOrPress$49(tLObject, tL_username, tL_error);
            }
        });
    }

    public void lambda$processOnClickOrPress$51(TLObject tLObject, String str, TLRPC.User user, TLRPC.TL_error tL_error) {
        if (tLObject instanceof TL_fragment.TL_collectibleInfo) {
            FragmentUsernameBottomSheet.open(getContext(), 1, str, user, (TL_fragment.TL_collectibleInfo) tLObject, getResourceProvider());
        } else {
            BulletinFactory.showError(tL_error);
        }
    }

    public void lambda$processOnClickOrPress$52(final String str, final TLRPC.User user, final TLObject tLObject, final TLRPC.TL_error tL_error) {
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                ProfileActivity.this.lambda$processOnClickOrPress$51(tLObject, str, user, tL_error);
            }
        });
    }

    public void lambda$processOnClickOrPress$53(AtomicReference atomicReference, int i, TLRPC.User user, View view) {
        ((ActionBarPopupWindow) atomicReference.get()).dismiss();
        try {
            if (i == 0) {
                Intent intent = new Intent("android.intent.action.DIAL", Uri.parse("tel:+" + user.phone));
                intent.addFlags(268435456);
                getParentActivity().startActivityForResult(intent, 500);
            } else {
                if (i != 1) {
                    if ((i == 2 || i == 3) && getParentActivity() != null) {
                        boolean z = i == 3;
                        TLRPC.UserFull userFull = this.userInfo;
                        VoIPHelper.startCall(user, z, userFull != null && userFull.video_calls_available, getParentActivity(), this.userInfo, getAccountInstance());
                        return;
                    }
                    return;
                }
                ((ClipboardManager) ApplicationLoader.applicationContext.getSystemService("clipboard")).setPrimaryClip(ClipData.newPlainText("label", "+" + user.phone));
                if (AndroidUtilities.shouldShowClipboardToast()) {
                    BulletinFactory.of(this).createCopyBulletin(LocaleController.getString(R.string.PhoneCopied)).show();
                }
            }
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    public static void lambda$processOnClickOrPress$54(View view) {
        try {
            view.getContext().startActivity(new Intent("android.intent.action.VIEW", Uri.parse("https://fragment.com")));
        } catch (ActivityNotFoundException e) {
            FileLog.e(e);
        }
    }

    public Boolean lambda$processOnClickOrPress$55(URLSpan uRLSpan) {
        if (uRLSpan == null) {
            return Boolean.FALSE;
        }
        openUrl(uRLSpan.getURL(), null);
        return Boolean.TRUE;
    }

    public void lambda$processOnClickOrPress$56(AtomicReference atomicReference, int i, String str, int i2, String[] strArr, String str2, View view) {
        BulletinFactory of;
        String string;
        ((ActionBarPopupWindow) atomicReference.get()).dismiss();
        try {
            if (i != 0) {
                if (i == 1) {
                    TranslateAlert2.showAlert(this.fragmentView.getContext(), this, this.currentAccount, strArr[0], str2, str, null, false, new Utilities.CallbackReturn() {
                        @Override
                        public final Object run(Object obj) {
                            Boolean lambda$processOnClickOrPress$55;
                            lambda$processOnClickOrPress$55 = ProfileActivity.this.lambda$processOnClickOrPress$55((URLSpan) obj);
                            return lambda$processOnClickOrPress$55;
                        }
                    }, null);
                    return;
                }
                return;
            }
            AndroidUtilities.addToClipboard(str);
            if (i2 == this.bioRow) {
                of = BulletinFactory.of(this);
                string = LocaleController.getString(R.string.BioCopied);
            } else {
                of = BulletinFactory.of(this);
                string = LocaleController.getString(R.string.TextCopied);
            }
            of.createCopyBulletin(string).show();
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    public void lambda$processOnClickOrPress$57(boolean[] zArr, final String str, final int i, final String[] strArr, final String str2, float f, float f2, View view) {
        if (getParentActivity() == null) {
            return;
        }
        boolean z = false;
        CharSequence[] charSequenceArr = zArr[0] ? new CharSequence[]{LocaleController.getString(R.string.Copy), LocaleController.getString(R.string.TranslateMessage)} : new CharSequence[]{LocaleController.getString(R.string.Copy)};
        int[] iArr = zArr[0] ? new int[]{R.drawable.msg_copy, R.drawable.msg_translate} : new int[]{R.drawable.msg_copy};
        final AtomicReference atomicReference = new AtomicReference();
        AnonymousClass42 anonymousClass42 = new ActionBarPopupWindow.ActionBarPopupWindowLayout(getContext(), R.drawable.popup_fixed_alert, this.resourcesProvider) {
            Path path = new Path();

            AnonymousClass42(Context context, int i2, Theme.ResourcesProvider resourcesProvider) {
                super(context, i2, resourcesProvider);
                this.path = new Path();
            }

            @Override
            protected boolean drawChild(Canvas canvas, View view2, long j) {
                canvas.save();
                this.path.rewind();
                RectF rectF = AndroidUtilities.rectTmp;
                rectF.set(view2.getLeft(), view2.getTop(), view2.getRight(), view2.getBottom());
                this.path.addRoundRect(rectF, AndroidUtilities.dp(6.0f), AndroidUtilities.dp(6.0f), Path.Direction.CW);
                canvas.clipPath(this.path);
                boolean drawChild = super.drawChild(canvas, view2, j);
                canvas.restore();
                return drawChild;
            }
        };
        anonymousClass42.setFitItems(true);
        int i2 = 0;
        while (i2 < iArr.length) {
            final int i3 = i2;
            ActionBarMenuItem.addItem(anonymousClass42, iArr[i2], charSequenceArr[i2], z, this.resourcesProvider).setOnClickListener(new View.OnClickListener() {
                @Override
                public final void onClick(View view2) {
                    ProfileActivity.this.lambda$processOnClickOrPress$56(atomicReference, i3, str, i, strArr, str2, view2);
                }
            });
            i2++;
            z = false;
        }
        ActionBarPopupWindow actionBarPopupWindow = new ActionBarPopupWindow(anonymousClass42, -2, -2);
        actionBarPopupWindow.setPauseNotifications(true);
        actionBarPopupWindow.setDismissAnimationDuration(220);
        actionBarPopupWindow.setOutsideTouchable(true);
        actionBarPopupWindow.setClippingEnabled(true);
        actionBarPopupWindow.setAnimationStyle(R.style.PopupContextAnimation);
        actionBarPopupWindow.setFocusable(true);
        anonymousClass42.measure(View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(1000.0f), Integer.MIN_VALUE), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(1000.0f), Integer.MIN_VALUE));
        actionBarPopupWindow.setInputMethodMode(2);
        actionBarPopupWindow.getContentView().setFocusableInTouchMode(true);
        atomicReference.set(actionBarPopupWindow);
        float f3 = f;
        float f4 = f2;
        for (View view2 = view; view2 != getFragmentView(); view2 = (View) view2.getParent()) {
            f3 += view2.getX();
            f4 += view2.getY();
        }
        if (AndroidUtilities.isTablet()) {
            ViewGroup view3 = this.parentLayout.getView();
            f3 += view3.getX() + view3.getPaddingLeft();
            f4 += view3.getY() + view3.getPaddingTop();
        }
        actionBarPopupWindow.showAtLocation(getFragmentView(), 0, (int) (f3 - (anonymousClass42.getMeasuredWidth() / 2.0f)), (int) f4);
        actionBarPopupWindow.dimBehind();
    }

    public void lambda$processOnClickOrPress$58(String[] strArr, boolean[] zArr, String str, boolean z, Runnable runnable, String str2) {
        TLRPC.Chat chat;
        strArr[0] = str2;
        zArr[0] = str2 != null && (!str2.equals(str) || str2.equals("und")) && ((z && !RestrictedLanguagesSelectActivity.getRestrictedLanguages().contains(str2)) || ((chat = this.currentChat) != null && ((chat.has_link || ChatObject.isPublic(chat)) && ("uk".equals(str2) || "ru".equals(str2)))));
        runnable.run();
    }

    public static void lambda$processOnClickOrPress$59(Runnable runnable, Exception exc) {
        FileLog.e("mlkit: failed to detect language in selection", exc);
        runnable.run();
    }

    public void lambda$processOnClickOrPress$60(AtomicReference atomicReference, String str, int i, View view) {
        BulletinFactory of;
        String string;
        ((ActionBarPopupWindow) atomicReference.get()).dismiss();
        try {
            AndroidUtilities.addToClipboard(str);
            if (i == this.bizHoursRow) {
                of = BulletinFactory.of(this);
                string = LocaleController.getString(R.string.BusinessHoursCopied);
            } else {
                of = BulletinFactory.of(this);
                string = LocaleController.getString(R.string.BusinessLocationCopied);
            }
            of.createCopyBulletin(string).show();
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    public void lambda$searchExpandTransition$82(ValueAnimator valueAnimator, float f, boolean z, ValueAnimator valueAnimator2) {
        float floatValue = ((Float) valueAnimator.getAnimatedValue()).floatValue();
        this.searchTransitionProgress = floatValue;
        float f2 = (floatValue - 0.5f) / 0.5f;
        float f3 = (0.5f - floatValue) / 0.5f;
        if (f2 < 0.0f) {
            f2 = 0.0f;
        }
        if (f3 < 0.0f) {
            f3 = 0.0f;
        }
        float f4 = -f;
        this.searchTransitionOffset = (int) ((1.0f - floatValue) * f4);
        this.searchListView.setTranslationY(floatValue * f);
        this.emptyView.setTranslationY(f * this.searchTransitionProgress);
        this.listView.setTranslationY(f4 * (1.0f - this.searchTransitionProgress));
        this.listView.setScaleX(1.0f - ((1.0f - this.searchTransitionProgress) * 0.01f));
        this.listView.setScaleY(1.0f - ((1.0f - this.searchTransitionProgress) * 0.01f));
        this.listView.setAlpha(this.searchTransitionProgress);
        needLayout(true);
        this.listView.setAlpha(f2);
        this.searchListView.setAlpha(1.0f - this.searchTransitionProgress);
        this.searchListView.setScaleX((this.searchTransitionProgress * 0.05f) + 1.0f);
        this.searchListView.setScaleY((this.searchTransitionProgress * 0.05f) + 1.0f);
        this.emptyView.setAlpha(1.0f - f2);
        this.avatarContainer.setAlpha(f2);
        ProfileStoriesView profileStoriesView = this.storyView;
        if (profileStoriesView != null) {
            profileStoriesView.setAlpha(f2);
        }
        this.nameTextView[1].setAlpha(f2);
        this.onlineTextView[1].setAlpha(f2);
        this.onlineTextView[3].setAlpha(f2);
        this.searchItem.getSearchField().setAlpha(f3);
        if (z && this.searchTransitionProgress < 0.7f) {
            this.searchItem.requestFocusOnSearchView();
        }
        this.searchItem.getSearchContainer().setVisibility(this.searchTransitionProgress < 0.5f ? 0 : 8);
        int i = this.searchTransitionProgress > 0.5f ? 0 : 8;
        ActionBarMenuItem actionBarMenuItem = this.otherItem;
        if (actionBarMenuItem != null) {
            actionBarMenuItem.setVisibility(i);
            this.otherItem.setAlpha(f2);
        }
        ActionBarMenuItem actionBarMenuItem2 = this.qrItem;
        if (actionBarMenuItem2 != null) {
            actionBarMenuItem2.setAlpha(f2);
            updateQrItemVisibility(false);
        }
        this.searchItem.setVisibility(i);
        this.actionBar.onSearchFieldVisibilityChanged(this.searchTransitionProgress < 0.5f);
        ActionBarMenuItem actionBarMenuItem3 = this.otherItem;
        if (actionBarMenuItem3 != null) {
            actionBarMenuItem3.setAlpha(f2);
        }
        ActionBarMenuItem actionBarMenuItem4 = this.qrItem;
        if (actionBarMenuItem4 != null) {
            actionBarMenuItem4.setAlpha(f2);
        }
        this.searchItem.setAlpha(f2);
        this.topView.invalidate();
        this.fragmentView.invalidate();
    }

    public static void lambda$sendLogs$86(AlertDialog alertDialog, boolean[] zArr, Activity activity, File file) {
        Uri fromFile;
        try {
            alertDialog.dismiss();
        } catch (Exception unused) {
        }
        if (!zArr[0]) {
            if (activity != null) {
                Toast.makeText(activity, LocaleController.getString(R.string.ErrorOccurred), 0).show();
                return;
            }
            return;
        }
        int i = Build.VERSION.SDK_INT;
        if (i >= 24) {
            fromFile = FileProvider.getUriForFile(activity, ApplicationLoader.getApplicationId() + ".provider", file);
        } else {
            fromFile = Uri.fromFile(file);
        }
        Intent intent = new Intent("android.intent.action.SEND");
        if (i >= 24) {
            intent.addFlags(1);
        }
        intent.setType("message/rfc822");
        intent.putExtra("android.intent.extra.EMAIL", "");
        intent.putExtra("android.intent.extra.SUBJECT", "Logs from " + LocaleController.getInstance().getFormatterStats().format(System.currentTimeMillis()));
        intent.putExtra("android.intent.extra.STREAM", fromFile);
        if (activity != null) {
            try {
                activity.startActivityForResult(Intent.createChooser(intent, "Select email application."), 500);
            } catch (Exception e) {
                FileLog.e(e);
            }
        }
    }

    public static void lambda$sendLogs$87(final AlertDialog alertDialog, boolean z, final Activity activity) {
        ZipOutputStream zipOutputStream;
        int i;
        try {
            File logsDir = AndroidUtilities.getLogsDir();
            if (logsDir == null) {
                Objects.requireNonNull(alertDialog);
                AndroidUtilities.runOnUIThread(new BillingController$$ExternalSyntheticLambda10(alertDialog));
                return;
            }
            final File file = new File(logsDir, "logs.zip");
            if (file.exists()) {
                file.delete();
            }
            ArrayList arrayList = new ArrayList();
            for (File file2 : logsDir.listFiles()) {
                arrayList.add(file2);
            }
            File file3 = new File(ApplicationLoader.getFilesDirFixed(), "malformed_database/");
            if (file3.exists() && file3.isDirectory()) {
                for (File file4 : file3.listFiles()) {
                    arrayList.add(file4);
                }
            }
            final boolean[] zArr = new boolean[1];
            long currentTimeMillis = System.currentTimeMillis();
            BufferedInputStream bufferedInputStream = null;
            try {
                zipOutputStream = new ZipOutputStream(new BufferedOutputStream(new FileOutputStream(file)));
                try {
                    try {
                        byte[] bArr = new byte[65536];
                        while (i < arrayList.size()) {
                            File file5 = (File) arrayList.get(i);
                            if (!file5.getName().contains("cache4")) {
                                if (!z) {
                                    if (file5.getName().contains("_mtproto")) {
                                    }
                                }
                                i = currentTimeMillis - file5.lastModified() > 86400000 ? i + 1 : 0;
                            }
                            if (file5.exists()) {
                                BufferedInputStream bufferedInputStream2 = new BufferedInputStream(new FileInputStream(file5), 65536);
                                try {
                                    zipOutputStream.putNextEntry(new ZipEntry(file5.getName()));
                                    while (true) {
                                        int read = bufferedInputStream2.read(bArr, 0, 65536);
                                        if (read == -1) {
                                            break;
                                        } else {
                                            zipOutputStream.write(bArr, 0, read);
                                        }
                                    }
                                    bufferedInputStream2.close();
                                } catch (Exception e) {
                                    e = e;
                                    bufferedInputStream = bufferedInputStream2;
                                    e.printStackTrace();
                                    if (bufferedInputStream != null) {
                                        bufferedInputStream.close();
                                    }
                                    if (zipOutputStream != null) {
                                        zipOutputStream.close();
                                    }
                                    AndroidUtilities.runOnUIThread(new Runnable() {
                                        @Override
                                        public final void run() {
                                            ProfileActivity.lambda$sendLogs$86(AlertDialog.this, zArr, activity, file);
                                        }
                                    });
                                } catch (Throwable th) {
                                    th = th;
                                    bufferedInputStream = bufferedInputStream2;
                                    if (bufferedInputStream != null) {
                                        bufferedInputStream.close();
                                    }
                                    if (zipOutputStream != null) {
                                        zipOutputStream.close();
                                    }
                                    throw th;
                                }
                            }
                        }
                        zArr[0] = true;
                    } catch (Exception e2) {
                        e = e2;
                    }
                } catch (Throwable th2) {
                    th = th2;
                }
            } catch (Exception e3) {
                e = e3;
                zipOutputStream = null;
            } catch (Throwable th3) {
                th = th3;
                zipOutputStream = null;
            }
            zipOutputStream.close();
            AndroidUtilities.runOnUIThread(new Runnable() {
                @Override
                public final void run() {
                    ProfileActivity.lambda$sendLogs$86(AlertDialog.this, zArr, activity, file);
                }
            });
        } catch (Exception e4) {
            e4.printStackTrace();
        }
    }

    public void lambda$setLoadingSpan$92(View view) {
        if (view instanceof TextDetailCell) {
            TextDetailCell textDetailCell = (TextDetailCell) view;
            textDetailCell.textView.setLoading(this.loadingSpan);
            textDetailCell.valueTextView.setLoading(this.loadingSpan);
        }
    }

    public void lambda$setMediaHeaderVisible$65(ValueAnimator valueAnimator) {
        updateStoriesViewBounds(true);
    }

    public boolean lambda$setParentLayout$5() {
        return this.currentEncryptedChat != null || getMessagesController().isChatNoForwards(this.currentChat);
    }

    public void lambda$setUserInfo$74() {
        updateListAnimated(false);
    }

    public void lambda$updateExceptions$2(HashSet hashSet) {
        ListAdapter listAdapter;
        ArrayList arrayList = new ArrayList(hashSet);
        int i = 0;
        while (i < arrayList.size()) {
            if (getMessagesController().getTopicsController().findTopic(this.chatId, ((Integer) arrayList.get(i)).intValue()) == null) {
                arrayList.remove(i);
                i--;
            }
            i++;
        }
        this.notificationsExceptionTopics.clear();
        this.notificationsExceptionTopics.addAll(arrayList);
        int i2 = this.notificationsRow;
        if (i2 < 0 || (listAdapter = this.listAdapter) == null) {
            return;
        }
        listAdapter.notifyItemChanged(i2);
    }

    public void lambda$updateListAnimated$90(boolean z) {
        updateListAnimated(z, true);
    }

    public static int lambda$updateOnlineCount$73(ArrayList arrayList, Object obj) {
        return ((Integer) arrayList.get(((Integer) obj).intValue())).intValue();
    }

    public void lambda$updateProfileData$75() {
        getMessagesController().reloadUser(getDialogId());
    }

    public void lambda$updateProfileData$76(View view) {
        MessagePrivateSeenView.showSheet(getContext(), this.currentAccount, getDialogId(), true, null, new Runnable() {
            @Override
            public final void run() {
                ProfileActivity.this.lambda$updateProfileData$75();
            }
        }, this.resourcesProvider);
    }

    public void lambda$updateProfileData$77(View view) {
        showStatusSelect();
    }

    public void lambda$updateProfileData$78(TLRPC.User user, SimpleTextView simpleTextView, View view) {
        ImageLocation forDocument;
        PremiumPreviewBottomSheet premiumPreviewBottomSheet = new PremiumPreviewBottomSheet(this, this.currentAccount, user, this.resourcesProvider);
        simpleTextView.getLocationOnScreen(new int[2]);
        premiumPreviewBottomSheet.startEnterFromX = simpleTextView.rightDrawableX;
        premiumPreviewBottomSheet.startEnterFromY = simpleTextView.rightDrawableY;
        premiumPreviewBottomSheet.startEnterFromScale = simpleTextView.getScaleX();
        premiumPreviewBottomSheet.startEnterFromX1 = simpleTextView.getLeft();
        premiumPreviewBottomSheet.startEnterFromY1 = simpleTextView.getTop();
        premiumPreviewBottomSheet.startEnterFromView = simpleTextView;
        Drawable rightDrawable = simpleTextView.getRightDrawable();
        AnimatedEmojiDrawable.SwapAnimatedEmojiDrawable swapAnimatedEmojiDrawable = this.emojiStatusDrawable[1];
        if (rightDrawable == swapAnimatedEmojiDrawable && swapAnimatedEmojiDrawable != null && (swapAnimatedEmojiDrawable.getDrawable() instanceof AnimatedEmojiDrawable)) {
            premiumPreviewBottomSheet.startEnterFromScale *= 0.98f;
            TLRPC.Document document = ((AnimatedEmojiDrawable) this.emojiStatusDrawable[1].getDrawable()).getDocument();
            if (document != null) {
                BackupImageView backupImageView = new BackupImageView(getContext());
                SvgHelper.SvgDrawable svgThumb = DocumentObject.getSvgThumb(document.thumbs, Theme.key_windowBackgroundWhiteGrayIcon, 0.2f);
                TLRPC.PhotoSize closestPhotoSizeWithSize = FileLoader.getClosestPhotoSizeWithSize(document.thumbs, 90);
                String str = "160_160";
                if ("video/webm".equals(document.mime_type)) {
                    forDocument = ImageLocation.getForDocument(document);
                    str = "160_160_g";
                    if (svgThumb != null) {
                        svgThumb.overrideWidthAndHeight(512, 512);
                    }
                } else {
                    if (svgThumb != null && MessageObject.isAnimatedStickerDocument(document, false)) {
                        svgThumb.overrideWidthAndHeight(512, 512);
                    }
                    forDocument = ImageLocation.getForDocument(document);
                }
                String str2 = str;
                backupImageView.setLayerNum(7);
                backupImageView.setRoundRadius(AndroidUtilities.dp(4.0f));
                backupImageView.setImage(forDocument, str2, ImageLocation.getForDocument(closestPhotoSizeWithSize, document), "140_140", svgThumb, document);
                if (((AnimatedEmojiDrawable) this.emojiStatusDrawable[1].getDrawable()).canOverrideColor()) {
                    backupImageView.setColorFilter(new PorterDuffColorFilter(getThemedColor(Theme.key_windowBackgroundWhiteBlueIcon), PorterDuff.Mode.SRC_IN));
                }
                premiumPreviewBottomSheet.statusStickerSet = MessageObject.getInputStickerSet(document);
                premiumPreviewBottomSheet.overrideTitleIcon = backupImageView;
                premiumPreviewBottomSheet.isEmojiStatus = true;
            }
        }
        showDialog(premiumPreviewBottomSheet);
    }

    public void lambda$updateProfileData$79(View view) {
        showStatusSelect();
    }

    public void lambda$updateProfileData$80(View view) {
        goToForum();
    }

    public void lambda$updatedPeerColor$81(View view) {
        if (view instanceof HeaderCell) {
            ((HeaderCell) view).setTextColor(applyPeerColor(getThemedColor(Theme.key_windowBackgroundWhiteBlueHeader), false));
            return;
        }
        if (view instanceof TextDetailCell) {
            ((TextDetailCell) view).updateColors();
            return;
        }
        if (view instanceof TextCell) {
            ((TextCell) view).updateColors();
            return;
        }
        if (view instanceof AboutLinkCell) {
            ((AboutLinkCell) view).updateColors();
            return;
        }
        if (view instanceof NotificationsCheckCell) {
            ((NotificationsCheckCell) view).getCheckBox().invalidate();
        } else if (view instanceof ProfileHoursCell) {
            ((ProfileHoursCell) view).updateColors();
        } else if (view instanceof ProfileChannelCell) {
            ((ProfileChannelCell) view).updateColors();
        }
    }

    public void leaveChatPressed() {
        boolean isForum = ChatObject.isForum(this.currentChat);
        AlertsCreator.createClearOrDeleteDialogAlert(this, false, this.currentChat, null, false, isForum, !isForum, new MessagesStorage.BooleanCallback() {
            @Override
            public final void run(boolean z) {
                ProfileActivity.this.lambda$leaveChatPressed$61(z);
            }
        }, this.resourcesProvider);
    }

    public void listCodecs(String str, StringBuilder sb) {
        int maxSupportedInstances;
        boolean isHardwareAccelerated;
        boolean isSoftwareOnly;
        boolean isVendor;
        int maxSupportedInstances2;
        boolean isHardwareAccelerated2;
        boolean isSoftwareOnly2;
        boolean isVendor2;
        String[] supportedTypes;
        if (Build.VERSION.SDK_INT < 23) {
            return;
        }
        try {
            int codecCount = MediaCodecList.getCodecCount();
            ArrayList arrayList = new ArrayList();
            ArrayList arrayList2 = new ArrayList();
            for (int i = 0; i < codecCount; i++) {
                MediaCodecInfo codecInfoAt = MediaCodecList.getCodecInfoAt(i);
                if (codecInfoAt != null && (supportedTypes = codecInfoAt.getSupportedTypes()) != null) {
                    int i2 = 0;
                    while (true) {
                        if (i2 >= supportedTypes.length) {
                            break;
                        } else if (supportedTypes[i2].equals(str)) {
                            (codecInfoAt.isEncoder() ? arrayList2 : arrayList).add(Integer.valueOf(i));
                        } else {
                            i2++;
                        }
                    }
                }
            }
            if (arrayList.isEmpty() && arrayList2.isEmpty()) {
                return;
            }
            sb.append("\n");
            sb.append(arrayList.size());
            sb.append("+");
            sb.append(arrayList2.size());
            sb.append(" ");
            sb.append(str.substring(6));
            sb.append(" codecs:\n");
            for (int i3 = 0; i3 < arrayList.size(); i3++) {
                if (i3 > 0) {
                    sb.append("\n");
                }
                MediaCodecInfo codecInfoAt2 = MediaCodecList.getCodecInfoAt(((Integer) arrayList.get(i3)).intValue());
                sb.append("{d} ");
                sb.append(codecInfoAt2.getName());
                sb.append(" (");
                if (Build.VERSION.SDK_INT >= 29) {
                    isHardwareAccelerated2 = codecInfoAt2.isHardwareAccelerated();
                    if (isHardwareAccelerated2) {
                        sb.append("gpu");
                    }
                    isSoftwareOnly2 = codecInfoAt2.isSoftwareOnly();
                    if (isSoftwareOnly2) {
                        sb.append("cpu");
                    }
                    isVendor2 = codecInfoAt2.isVendor();
                    if (isVendor2) {
                        sb.append(", v");
                    }
                }
                MediaCodecInfo.CodecCapabilities capabilitiesForType = codecInfoAt2.getCapabilitiesForType(str);
                sb.append("; mi=");
                maxSupportedInstances2 = capabilitiesForType.getMaxSupportedInstances();
                sb.append(maxSupportedInstances2);
                sb.append(")");
            }
            for (int i4 = 0; i4 < arrayList2.size(); i4++) {
                if (i4 > 0 || !arrayList.isEmpty()) {
                    sb.append("\n");
                }
                MediaCodecInfo codecInfoAt3 = MediaCodecList.getCodecInfoAt(((Integer) arrayList2.get(i4)).intValue());
                sb.append("{e} ");
                sb.append(codecInfoAt3.getName());
                sb.append(" (");
                if (Build.VERSION.SDK_INT >= 29) {
                    isHardwareAccelerated = codecInfoAt3.isHardwareAccelerated();
                    if (isHardwareAccelerated) {
                        sb.append("gpu");
                    }
                    isSoftwareOnly = codecInfoAt3.isSoftwareOnly();
                    if (isSoftwareOnly) {
                        sb.append("cpu");
                    }
                    isVendor = codecInfoAt3.isVendor();
                    if (isVendor) {
                        sb.append(", v");
                    }
                }
                MediaCodecInfo.CodecCapabilities capabilitiesForType2 = codecInfoAt3.getCapabilitiesForType(str);
                sb.append("; mi=");
                maxSupportedInstances = capabilitiesForType2.getMaxSupportedInstances();
                sb.append(maxSupportedInstances);
                sb.append(")");
            }
            sb.append("\n");
        } catch (Exception unused) {
        }
    }

    public boolean needInsetForStories() {
        return getMessagesController().getStoriesController().hasStories(getDialogId()) && !this.isTopic;
    }

    public void needLayout(boolean z) {
        OverlaysView overlaysView;
        AudioPlayerAlert.ClippingTextViewSwitcher clippingTextViewSwitcher;
        float f;
        BackupImageView currentItemView;
        float f2;
        float f3;
        ValueAnimator valueAnimator;
        TLRPC.ChatFull chatFull;
        int i = 0;
        int currentActionBarHeight = (this.actionBar.getOccupyStatusBar() ? AndroidUtilities.statusBarHeight : 0) + ActionBar.getCurrentActionBarHeight();
        RecyclerListView recyclerListView = this.listView;
        if (recyclerListView != null && !this.openAnimationInProgress) {
            FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) recyclerListView.getLayoutParams();
            if (layoutParams.topMargin != currentActionBarHeight) {
                layoutParams.topMargin = currentActionBarHeight;
                this.listView.setLayoutParams(layoutParams);
            }
        }
        if (this.avatarContainer != null) {
            float min = Math.min(1.0f, this.extraHeight / AndroidUtilities.dp(88.0f));
            this.listView.setTopGlowOffset((int) this.extraHeight);
            this.listView.setOverScrollMode((this.extraHeight <= ((float) AndroidUtilities.dp(88.0f)) || this.extraHeight >= ((float) (this.listView.getMeasuredWidth() - currentActionBarHeight))) ? 0 : 2);
            RLottieImageView rLottieImageView = this.writeButton;
            if (rLottieImageView != null) {
                rLottieImageView.setTranslationY(((((this.actionBar.getOccupyStatusBar() ? AndroidUtilities.statusBarHeight : 0) + ActionBar.getCurrentActionBarHeight()) + this.extraHeight) + this.searchTransitionOffset) - AndroidUtilities.dp(29.5f));
                boolean z2 = min > 0.2f && !this.searchMode && (this.imageUpdater == null || this.setAvatarRow == -1);
                boolean z3 = (!z2 || this.chatId == 0) ? z2 : (!ChatObject.isChannel(this.currentChat) || this.currentChat.megagroup || (chatFull = this.chatInfo) == null || chatFull.linked_chat_id == 0 || this.infoHeaderRow == -1) ? false : true;
                if (!this.openAnimationInProgress) {
                    if (z3 != (this.writeButton.getTag() == null)) {
                        RLottieImageView rLottieImageView2 = this.writeButton;
                        if (z3) {
                            rLottieImageView2.setTag(null);
                        } else {
                            rLottieImageView2.setTag(0);
                        }
                        AnimatorSet animatorSet = this.writeButtonAnimation;
                        if (animatorSet != null) {
                            this.writeButtonAnimation = null;
                            animatorSet.cancel();
                        }
                        if (z) {
                            AnimatorSet animatorSet2 = new AnimatorSet();
                            this.writeButtonAnimation = animatorSet2;
                            if (z3) {
                                animatorSet2.setInterpolator(new DecelerateInterpolator());
                                this.writeButtonAnimation.playTogether(ObjectAnimator.ofFloat(this.writeButton, (Property<RLottieImageView, Float>) View.SCALE_X, 1.0f), ObjectAnimator.ofFloat(this.writeButton, (Property<RLottieImageView, Float>) View.SCALE_Y, 1.0f), ObjectAnimator.ofFloat(this.writeButton, (Property<RLottieImageView, Float>) View.ALPHA, 1.0f));
                            } else {
                                animatorSet2.setInterpolator(new AccelerateInterpolator());
                                this.writeButtonAnimation.playTogether(ObjectAnimator.ofFloat(this.writeButton, (Property<RLottieImageView, Float>) View.SCALE_X, 0.2f), ObjectAnimator.ofFloat(this.writeButton, (Property<RLottieImageView, Float>) View.SCALE_Y, 0.2f), ObjectAnimator.ofFloat(this.writeButton, (Property<RLottieImageView, Float>) View.ALPHA, 0.0f));
                            }
                            this.writeButtonAnimation.setDuration(150L);
                            this.writeButtonAnimation.addListener(new AnimatorListenerAdapter() {
                                AnonymousClass46() {
                                }

                                @Override
                                public void onAnimationEnd(Animator animator) {
                                    if (ProfileActivity.this.writeButtonAnimation == null || !ProfileActivity.this.writeButtonAnimation.equals(animator)) {
                                        return;
                                    }
                                    ProfileActivity.this.writeButtonAnimation = null;
                                }
                            });
                            this.writeButtonAnimation.start();
                        } else {
                            this.writeButton.setScaleX(z3 ? 1.0f : 0.2f);
                            this.writeButton.setScaleY(z3 ? 1.0f : 0.2f);
                            this.writeButton.setAlpha(z3 ? 1.0f : 0.0f);
                        }
                    }
                    if (this.qrItem != null) {
                        updateQrItemVisibility(z);
                        if (!z) {
                            float dp = AndroidUtilities.dp(48.0f) * this.qrItem.getAlpha();
                            this.qrItem.setTranslationX(dp);
                            PagerIndicatorView pagerIndicatorView = this.avatarsViewPagerIndicatorView;
                            if (pagerIndicatorView != null) {
                                pagerIndicatorView.setTranslationX(dp - AndroidUtilities.dp(48.0f));
                            }
                        }
                    }
                }
                ProfileStoriesView profileStoriesView = this.storyView;
                if (profileStoriesView != null) {
                    profileStoriesView.setExpandCoords(this.avatarContainer2.getMeasuredWidth() - AndroidUtilities.dp(40.0f), z3, (this.actionBar.getOccupyStatusBar() ? AndroidUtilities.statusBarHeight : 0) + ActionBar.getCurrentActionBarHeight() + this.extraHeight + this.searchTransitionOffset);
                }
            }
            this.avatarX = (-AndroidUtilities.dpf2(47.0f)) * min;
            float f4 = AndroidUtilities.density;
            this.avatarY = (((this.actionBar.getOccupyStatusBar() ? AndroidUtilities.statusBarHeight : 0) + ((ActionBar.getCurrentActionBarHeight() / 2.0f) * (min + 1.0f))) - (f4 * 21.0f)) + (f4 * 27.0f * min) + this.actionBar.getTranslationY();
            float f5 = this.openAnimationInProgress ? this.initialAnimationExtraHeight : this.extraHeight;
            if (f5 > AndroidUtilities.dp(88.0f) || this.isPulledDown) {
                float max = Math.max(0.0f, Math.min(1.0f, (f5 - AndroidUtilities.dp(88.0f)) / ((this.listView.getMeasuredWidth() - currentActionBarHeight) - AndroidUtilities.dp(88.0f))));
                this.expandProgress = max;
                this.avatarScale = AndroidUtilities.lerp(1.4285715f, 2.4285715f, Math.min(1.0f, max * 3.0f));
                ProfileStoriesView profileStoriesView2 = this.storyView;
                if (profileStoriesView2 != null) {
                    profileStoriesView2.invalidate();
                }
                float min2 = Math.min(AndroidUtilities.dpf2(2000.0f), Math.max(AndroidUtilities.dpf2(1100.0f), Math.abs(this.listViewVelocityY))) / AndroidUtilities.dpf2(1100.0f);
                if (!this.allowPullingDown || (!this.openingAvatar && this.expandProgress < 0.33f)) {
                    if (this.isPulledDown) {
                        this.isPulledDown = false;
                        NotificationCenter.getGlobalInstance().lambda$postNotificationNameOnUIThread$1(NotificationCenter.needCheckSystemBarColors, Boolean.TRUE);
                        ActionBarMenuItem actionBarMenuItem = this.otherItem;
                        if (actionBarMenuItem != null) {
                            actionBarMenuItem.hideSubItem(21);
                            if (this.imageUpdater != null) {
                                this.otherItem.hideSubItem(33);
                                this.otherItem.hideSubItem(34);
                                this.otherItem.hideSubItem(35);
                                this.otherItem.showSubItem(36);
                                this.otherItem.showSubItem(31);
                            }
                        }
                        ActionBarMenuItem actionBarMenuItem2 = this.searchItem;
                        if (actionBarMenuItem2 != null) {
                            actionBarMenuItem2.setEnabled(!this.scrolling);
                        }
                        this.overlaysView.setOverlaysVisible(false, min2);
                        this.avatarsViewPagerIndicatorView.refreshVisibility(min2);
                        this.expandAnimator.cancel();
                        this.avatarImage.getImageReceiver().setAllowStartAnimation(true);
                        this.avatarImage.getImageReceiver().startAnimation();
                        float lerp = AndroidUtilities.lerp(this.expandAnimatorValues, this.currentExpanAnimatorFracture);
                        float[] fArr = this.expandAnimatorValues;
                        fArr[0] = lerp;
                        fArr[1] = 0.0f;
                        this.expandAnimator.setInterpolator(CubicBezierInterpolator.EASE_BOTH);
                        if (this.isInLandscapeMode) {
                            this.expandAnimator.setDuration(0L);
                        } else {
                            this.expandAnimator.setDuration((lerp * 250.0f) / min2);
                        }
                        this.topView.setBackgroundColor(getThemedColor(Theme.key_avatar_backgroundActionBarBlue));
                        if (!this.doNotSetForeground && (currentItemView = this.avatarsViewPager.getCurrentItemView()) != null) {
                            if (currentItemView.getImageReceiver().getDrawable() instanceof VectorAvatarThumbDrawable) {
                                this.avatarImage.drawForeground(false);
                            } else {
                                this.avatarImage.drawForeground(true);
                                this.avatarImage.setForegroundImageDrawable(currentItemView.getImageReceiver().getDrawableSafe());
                            }
                        }
                        this.avatarImage.setForegroundAlpha(1.0f);
                        this.avatarContainer.setVisibility(0);
                        this.avatarsViewPager.setVisibility(8);
                        this.expandAnimator.start();
                    }
                    this.avatarContainer.setScaleX(this.avatarScale);
                    this.avatarContainer.setScaleY(this.avatarScale);
                    ValueAnimator valueAnimator2 = this.expandAnimator;
                    if (valueAnimator2 == null || !valueAnimator2.isRunning()) {
                        refreshNameAndOnlineXY();
                        this.nameTextView[1].setTranslationX(this.nameX);
                        this.nameTextView[1].setTranslationY(this.nameY);
                        this.onlineTextView[1].setTranslationX(this.onlineX + this.customPhotoOffset);
                        this.onlineTextView[1].setTranslationY(this.onlineY);
                        this.mediaCounterTextView.setTranslationX(this.onlineX);
                        clippingTextViewSwitcher = this.mediaCounterTextView;
                        f = this.onlineY;
                        clippingTextViewSwitcher.setTranslationY(f);
                    }
                } else {
                    if (!this.isPulledDown) {
                        if (this.otherItem != null) {
                            if (getMessagesController().isChatNoForwards(this.currentChat)) {
                                this.otherItem.hideSubItem(21);
                            } else {
                                this.otherItem.showSubItem(21);
                            }
                            if (this.imageUpdater != null) {
                                this.otherItem.showSubItem(36);
                                this.otherItem.showSubItem(34);
                                this.otherItem.showSubItem(35);
                                this.otherItem.hideSubItem(33);
                                this.otherItem.hideSubItem(31);
                            }
                        }
                        ActionBarMenuItem actionBarMenuItem3 = this.searchItem;
                        if (actionBarMenuItem3 != null) {
                            actionBarMenuItem3.setEnabled(false);
                        }
                        this.isPulledDown = true;
                        NotificationCenter.getGlobalInstance().lambda$postNotificationNameOnUIThread$1(NotificationCenter.needCheckSystemBarColors, Boolean.TRUE);
                        this.overlaysView.setOverlaysVisible(true, min2);
                        this.avatarsViewPagerIndicatorView.refreshVisibility(min2);
                        this.avatarsViewPager.setCreateThumbFromParent(true);
                        this.avatarsViewPager.getAdapter().notifyDataSetChanged();
                        this.expandAnimator.cancel();
                        float lerp2 = AndroidUtilities.lerp(this.expandAnimatorValues, this.currentExpanAnimatorFracture);
                        float[] fArr2 = this.expandAnimatorValues;
                        fArr2[0] = lerp2;
                        fArr2[1] = 1.0f;
                        ProfileStoriesView profileStoriesView3 = this.storyView;
                        if (profileStoriesView3 == null || profileStoriesView3.isEmpty()) {
                            this.expandAnimator.setInterpolator(CubicBezierInterpolator.EASE_BOTH);
                            this.expandAnimator.setDuration(((1.0f - lerp2) * 250.0f) / min2);
                        } else {
                            this.expandAnimator.setInterpolator(new FastOutSlowInInterpolator());
                            this.expandAnimator.setDuration((((1.0f - lerp2) * 1.3f) * 250.0f) / min2);
                        }
                        this.expandAnimator.addListener(new AnimatorListenerAdapter() {
                            AnonymousClass47() {
                            }

                            @Override
                            public void onAnimationEnd(Animator animator) {
                                ProfileActivity.this.expandAnimator.removeListener(this);
                                ProfileActivity.this.topView.setBackgroundColor(-16777216);
                                ProfileActivity.this.avatarContainer.setVisibility(8);
                                ProfileActivity.this.avatarsViewPager.setVisibility(0);
                            }

                            @Override
                            public void onAnimationStart(Animator animator) {
                                ProfileActivity.this.setForegroundImage(false);
                                ProfileActivity.this.avatarsViewPager.setAnimatedFileMaybe(ProfileActivity.this.avatarImage.getImageReceiver().getAnimation());
                                ProfileActivity.this.avatarsViewPager.resetCurrentItem();
                            }
                        });
                        this.expandAnimator.start();
                    }
                    ViewGroup.LayoutParams layoutParams2 = this.avatarsViewPager.getLayoutParams();
                    layoutParams2.width = this.listView.getMeasuredWidth();
                    float f6 = f5 + currentActionBarHeight;
                    layoutParams2.height = (int) f6;
                    this.avatarsViewPager.requestLayout();
                    if (!this.expandAnimator.isRunning()) {
                        if (this.openAnimationInProgress && this.playProfileAnimation == 2) {
                            f3 = (-(1.0f - this.avatarAnimationProgress)) * AndroidUtilities.dp(50.0f);
                            f2 = 16.0f;
                        } else {
                            f2 = 16.0f;
                            f3 = 0.0f;
                        }
                        this.onlineX = AndroidUtilities.dpf2(f2) - this.onlineTextView[1].getLeft();
                        this.nameTextView[1].setTranslationX(AndroidUtilities.dpf2(18.0f) - this.nameTextView[1].getLeft());
                        this.nameTextView[1].setTranslationY(((f6 - AndroidUtilities.dpf2(38.0f)) - this.nameTextView[1].getBottom()) + f3);
                        this.onlineTextView[1].setTranslationX(this.onlineX + this.customPhotoOffset);
                        this.onlineTextView[1].setTranslationY(((f6 - AndroidUtilities.dpf2(18.0f)) - this.onlineTextView[1].getBottom()) + f3);
                        this.mediaCounterTextView.setTranslationX(this.onlineTextView[1].getTranslationX());
                        clippingTextViewSwitcher = this.mediaCounterTextView;
                        f = this.onlineTextView[1].getTranslationY();
                        clippingTextViewSwitcher.setTranslationY(f);
                    }
                }
            }
            if (this.openAnimationInProgress && this.playProfileAnimation == 2) {
                float currentActionBarHeight2 = (((this.actionBar.getOccupyStatusBar() ? AndroidUtilities.statusBarHeight : 0) + (ActionBar.getCurrentActionBarHeight() / 2.0f)) - (AndroidUtilities.density * 21.0f)) + this.actionBar.getTranslationY();
                this.nameTextView[0].setTranslationX(0.0f);
                double d = currentActionBarHeight2;
                this.nameTextView[0].setTranslationY(((float) Math.floor(d)) + AndroidUtilities.dp(1.3f));
                this.onlineTextView[0].setTranslationX(0.0f);
                this.onlineTextView[0].setTranslationY(((float) Math.floor(d)) + AndroidUtilities.dp(24.0f));
                this.nameTextView[0].setScaleX(1.0f);
                this.nameTextView[0].setScaleY(1.0f);
                this.nameTextView[1].setPivotY(r4.getMeasuredHeight());
                this.nameTextView[1].setScaleX(1.67f);
                this.nameTextView[1].setScaleY(1.67f);
                this.avatarScale = AndroidUtilities.lerp(1.0f, 2.4285715f, this.avatarAnimationProgress);
                ProfileStoriesView profileStoriesView4 = this.storyView;
                if (profileStoriesView4 != null) {
                    profileStoriesView4.setExpandProgress(1.0f);
                }
                this.avatarImage.setRoundRadius((int) AndroidUtilities.lerp(getSmallAvatarRoundRadius(), 0.0f, this.avatarAnimationProgress));
                this.avatarContainer.setTranslationX(AndroidUtilities.lerp(0.0f, 0.0f, this.avatarAnimationProgress));
                this.avatarContainer.setTranslationY(AndroidUtilities.lerp((float) Math.ceil(d), 0.0f, this.avatarAnimationProgress));
                float measuredWidth = (this.avatarContainer.getMeasuredWidth() - AndroidUtilities.dp(42.0f)) * this.avatarScale;
                this.timeItem.setTranslationX(this.avatarContainer.getX() + AndroidUtilities.dp(16.0f) + measuredWidth);
                this.timeItem.setTranslationY(this.avatarContainer.getY() + AndroidUtilities.dp(15.0f) + measuredWidth);
                this.starBgItem.setTranslationX(this.avatarContainer.getX() + AndroidUtilities.dp(28.0f) + measuredWidth);
                this.starBgItem.setTranslationY(this.avatarContainer.getY() + AndroidUtilities.dp(24.0f) + measuredWidth);
                this.starFgItem.setTranslationX(this.avatarContainer.getX() + AndroidUtilities.dp(28.0f) + measuredWidth);
                this.starFgItem.setTranslationY(this.avatarContainer.getY() + AndroidUtilities.dp(24.0f) + measuredWidth);
                this.avatarContainer.setScaleX(this.avatarScale);
                this.avatarContainer.setScaleY(this.avatarScale);
                this.overlaysView.setAlphaValue(this.avatarAnimationProgress, false);
                this.actionBar.setItemsColor(ColorUtils.blendARGB(this.peerColor != null ? -1 : getThemedColor(Theme.key_actionBarDefaultIcon), -1, this.avatarAnimationProgress), false);
                ScamDrawable scamDrawable = this.scamDrawable;
                if (scamDrawable != null) {
                    scamDrawable.setColor(ColorUtils.blendARGB(getThemedColor(Theme.key_avatar_subtitleInProfileBlue), Color.argb(179, 255, 255, 255), this.avatarAnimationProgress));
                }
                Drawable drawable = this.lockIconDrawable;
                if (drawable != null) {
                    drawable.setColorFilter(ColorUtils.blendARGB(getThemedColor(Theme.key_chat_lockIcon), -1, this.avatarAnimationProgress), PorterDuff.Mode.MULTIPLY);
                }
                CrossfadeDrawable crossfadeDrawable = this.verifiedCrossfadeDrawable[1];
                if (crossfadeDrawable != null) {
                    crossfadeDrawable.setProgress(this.avatarAnimationProgress);
                    this.nameTextView[1].invalidate();
                }
                CrossfadeDrawable crossfadeDrawable2 = this.premiumCrossfadeDrawable[1];
                if (crossfadeDrawable2 != null) {
                    crossfadeDrawable2.setProgress(this.avatarAnimationProgress);
                    this.nameTextView[1].invalidate();
                }
                updateEmojiStatusDrawableColor(this.avatarAnimationProgress);
                FrameLayout.LayoutParams layoutParams3 = (FrameLayout.LayoutParams) this.avatarContainer.getLayoutParams();
                int lerp3 = (int) AndroidUtilities.lerp(AndroidUtilities.dpf2(42.0f), (this.extraHeight + currentActionBarHeight) / this.avatarScale, this.avatarAnimationProgress);
                layoutParams3.height = lerp3;
                layoutParams3.width = lerp3;
                layoutParams3.leftMargin = (int) AndroidUtilities.lerp(AndroidUtilities.dpf2(64.0f), 0.0f, this.avatarAnimationProgress);
                this.avatarContainer.requestLayout();
            } else if (this.extraHeight <= AndroidUtilities.dp(88.0f)) {
                this.avatarScale = ((min * 18.0f) + 42.0f) / 42.0f;
                ProfileStoriesView profileStoriesView5 = this.storyView;
                if (profileStoriesView5 != null) {
                    profileStoriesView5.invalidate();
                }
                float f7 = (0.12f * min) + 1.0f;
                ValueAnimator valueAnimator3 = this.expandAnimator;
                if (valueAnimator3 == null || !valueAnimator3.isRunning()) {
                    this.avatarContainer.setScaleX(this.avatarScale);
                    this.avatarContainer.setScaleY(this.avatarScale);
                    this.avatarContainer.setTranslationX(this.avatarX);
                    this.avatarContainer.setTranslationY((float) Math.ceil(this.avatarY));
                    float dp2 = (AndroidUtilities.dp(42.0f) * this.avatarScale) - AndroidUtilities.dp(42.0f);
                    this.timeItem.setTranslationX(this.avatarContainer.getX() + AndroidUtilities.dp(16.0f) + dp2);
                    this.timeItem.setTranslationY(this.avatarContainer.getY() + AndroidUtilities.dp(15.0f) + dp2);
                    this.starBgItem.setTranslationX(this.avatarContainer.getX() + AndroidUtilities.dp(28.0f) + dp2);
                    this.starBgItem.setTranslationY(this.avatarContainer.getY() + AndroidUtilities.dp(24.0f) + dp2);
                    this.starFgItem.setTranslationX(this.avatarContainer.getX() + AndroidUtilities.dp(28.0f) + dp2);
                    this.starFgItem.setTranslationY(this.avatarContainer.getY() + AndroidUtilities.dp(24.0f) + dp2);
                }
                this.nameX = AndroidUtilities.density * (-21.0f) * min;
                this.nameY = ((float) Math.floor(this.avatarY)) + AndroidUtilities.dp(1.3f) + (AndroidUtilities.dp(7.0f) * min) + (this.titleAnimationsYDiff * (1.0f - this.avatarAnimationProgress));
                this.onlineX = AndroidUtilities.density * (-21.0f) * min;
                this.onlineY = ((float) Math.floor(this.avatarY)) + AndroidUtilities.dp(24.0f) + (((float) Math.floor(AndroidUtilities.density * 11.0f)) * min);
                ShowDrawable showDrawable = this.showStatusButton;
                if (showDrawable != null) {
                    showDrawable.setAlpha((int) (255.0f * min));
                }
                while (true) {
                    SimpleTextView[] simpleTextViewArr = this.nameTextView;
                    if (i >= simpleTextViewArr.length) {
                        break;
                    }
                    if (simpleTextViewArr[i] != null) {
                        ValueAnimator valueAnimator4 = this.expandAnimator;
                        if (valueAnimator4 == null || !valueAnimator4.isRunning()) {
                            this.nameTextView[i].setTranslationX(this.nameX);
                            this.nameTextView[i].setTranslationY(this.nameY);
                            this.onlineTextView[i].setTranslationX(this.onlineX + this.customPhotoOffset);
                            this.onlineTextView[i].setTranslationY(this.onlineY);
                            if (i == 1) {
                                this.mediaCounterTextView.setTranslationX(this.onlineX);
                                this.mediaCounterTextView.setTranslationY(this.onlineY);
                            }
                        }
                        this.nameTextView[i].setScaleX(f7);
                        this.nameTextView[i].setScaleY(f7);
                    }
                    i++;
                }
            }
            if (!this.openAnimationInProgress && ((valueAnimator = this.expandAnimator) == null || !valueAnimator.isRunning())) {
                needLayoutText(min);
            }
        }
        if (this.isPulledDown || ((overlaysView = this.overlaysView) != null && overlaysView.animator != null && this.overlaysView.animator.isRunning())) {
            ViewGroup.LayoutParams layoutParams4 = this.overlaysView.getLayoutParams();
            layoutParams4.width = this.listView.getMeasuredWidth();
            layoutParams4.height = (int) (this.extraHeight + currentActionBarHeight);
            this.overlaysView.requestLayout();
        }
        updateEmojiStatusEffectPosition();
    }

    public void needLayoutText(float f) {
        float scaleX = this.nameTextView[1].getScaleX();
        float f2 = this.extraHeight > ((float) AndroidUtilities.dp(88.0f)) ? 1.67f : 1.12f;
        if (this.extraHeight <= AndroidUtilities.dp(88.0f) || scaleX == f2) {
            int dp = AndroidUtilities.isTablet() ? AndroidUtilities.dp(490.0f) : AndroidUtilities.displaySize.x;
            this.avatarsViewPagerIndicatorView.getSecondaryMenuItem();
            int i = this.editItemVisible ? 48 : 0;
            if (this.callItemVisible) {
                i += 48;
            }
            if (this.videoCallItemVisible) {
                i += 48;
            }
            if (this.searchItem != null) {
                i += 48;
            }
            int dp2 = AndroidUtilities.dp((i * (1.0f - this.mediaHeaderAnimationProgress)) + 40.0f + 126.0f);
            int i2 = dp - dp2;
            float f3 = dp;
            int max = (int) ((f3 - (dp2 * Math.max(0.0f, 1.0f - (f != 1.0f ? (0.15f * f) / (1.0f - f) : 1.0f)))) - this.nameTextView[1].getTranslationX());
            float measureText = (this.nameTextView[1].getPaint().measureText(this.nameTextView[1].getText().toString()) * scaleX) + this.nameTextView[1].getSideDrawablesSize();
            FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) this.nameTextView[1].getLayoutParams();
            int i3 = layoutParams.width;
            float f4 = max;
            layoutParams.width = f4 < measureText ? Math.max(i2, (int) Math.ceil((max - AndroidUtilities.dp(24.0f)) / (((f2 - scaleX) * 7.0f) + scaleX))) : (int) Math.ceil(measureText);
            int min = (int) Math.min(((f3 - this.nameTextView[1].getX()) / scaleX) - AndroidUtilities.dp(8.0f), layoutParams.width);
            layoutParams.width = min;
            if (min != i3) {
                this.nameTextView[1].requestLayout();
            }
            float measureText2 = this.onlineTextView[1].getPaint().measureText(this.onlineTextView[1].getText().toString()) + this.onlineTextView[1].getRightDrawableWidth();
            FrameLayout.LayoutParams layoutParams2 = (FrameLayout.LayoutParams) this.onlineTextView[1].getLayoutParams();
            FrameLayout.LayoutParams layoutParams3 = (FrameLayout.LayoutParams) this.mediaCounterTextView.getLayoutParams();
            int i4 = layoutParams2.width;
            int ceil = (int) Math.ceil(this.onlineTextView[1].getTranslationX() + AndroidUtilities.dp(8.0f) + (AndroidUtilities.dp(40.0f) * (1.0f - f)));
            layoutParams2.rightMargin = ceil;
            layoutParams3.rightMargin = ceil;
            int ceil2 = f4 < measureText2 ? (int) Math.ceil(max) : -2;
            layoutParams2.width = ceil2;
            layoutParams3.width = ceil2;
            if (i4 != layoutParams2.width) {
                this.onlineTextView[2].getLayoutParams().width = layoutParams2.width;
                this.onlineTextView[2].requestLayout();
                this.onlineTextView[3].getLayoutParams().width = layoutParams2.width;
                this.onlineTextView[3].requestLayout();
                this.onlineTextView[1].requestLayout();
                this.mediaCounterTextView.requestLayout();
            }
        }
    }

    public static ProfileActivity of(long j) {
        String str;
        Bundle bundle = new Bundle();
        if (j >= 0) {
            str = "user_id";
        } else {
            j = -j;
            str = "chat_id";
        }
        bundle.putLong(str, j);
        return new ProfileActivity(bundle);
    }

    public void onTextDetailCellImageClicked(View view) {
        View view2 = (View) view.getParent();
        if (view2.getTag() != null && ((Integer) view2.getTag()).intValue() == this.usernameRow) {
            Bundle bundle = new Bundle();
            bundle.putLong("chat_id", this.chatId);
            bundle.putLong("user_id", this.userId);
            presentFragment(new QrActivity(bundle));
            return;
        }
        if (view2.getTag() == null || ((Integer) view2.getTag()).intValue() != this.birthdayRow) {
            return;
        }
        if (this.userId == getUserConfig().getClientUserId()) {
            presentFragment(new PremiumPreviewFragment("my_profile_gift"));
        } else {
            showDialog(new GiftSheet(getContext(), this.currentAccount, this.userId, null, null));
        }
    }

    public void onWriteButtonClick() {
        INavigationLayout iNavigationLayout;
        if (this.userId == 0) {
            openDiscussion();
            return;
        }
        if (this.imageUpdater != null) {
            TLRPC.User user = MessagesController.getInstance(this.currentAccount).getUser(Long.valueOf(UserConfig.getInstance(this.currentAccount).getClientUserId()));
            if (user == null) {
                user = UserConfig.getInstance(this.currentAccount).getCurrentUser();
            }
            if (user == null) {
                return;
            }
            ImageUpdater imageUpdater = this.imageUpdater;
            TLRPC.UserProfilePhoto userProfilePhoto = user.photo;
            imageUpdater.openMenu((userProfilePhoto == null || userProfilePhoto.photo_big == null || (userProfilePhoto instanceof TLRPC.TL_userProfilePhotoEmpty)) ? false : true, new Runnable() {
                @Override
                public final void run() {
                    ProfileActivity.this.lambda$onWriteButtonClick$38();
                }
            }, new DialogInterface.OnDismissListener() {
                @Override
                public final void onDismiss(DialogInterface dialogInterface) {
                    ProfileActivity.this.lambda$onWriteButtonClick$39(dialogInterface);
                }
            }, 0);
            this.cameraDrawable.setCurrentFrame(0);
            this.cameraDrawable.setCustomEndFrame(43);
            this.cellCameraDrawable.setCurrentFrame(0);
            this.cellCameraDrawable.setCustomEndFrame(43);
            this.writeButton.playAnimation();
            TextCell textCell = this.setAvatarCell;
            if (textCell != null) {
                textCell.getImageView().playAnimation();
                return;
            }
            return;
        }
        if (this.playProfileAnimation == 0 || (iNavigationLayout = this.parentLayout) == null || iNavigationLayout.getFragmentStack() == null || this.parentLayout.getFragmentStack().size() < 2 || !(this.parentLayout.getFragmentStack().get(this.parentLayout.getFragmentStack().size() - 2) instanceof ChatActivity)) {
            TLRPC.User user2 = getMessagesController().getUser(Long.valueOf(this.userId));
            if (user2 == null || (user2 instanceof TLRPC.TL_userEmpty)) {
                return;
            }
            Bundle bundle = new Bundle();
            bundle.putLong("user_id", this.userId);
            if (!getMessagesController().checkCanOpenChat(bundle, this)) {
                return;
            }
            boolean z = this.arguments.getBoolean("removeFragmentOnChatOpen", true);
            if (!AndroidUtilities.isTablet() && z) {
                NotificationCenter notificationCenter = getNotificationCenter();
                int i = NotificationCenter.closeChats;
                notificationCenter.removeObserver(this, i);
                getNotificationCenter().lambda$postNotificationNameOnUIThread$1(i, new Object[0]);
            }
            int i2 = getArguments().getInt("nearby_distance", -1);
            if (i2 >= 0) {
                bundle.putInt("nearby_distance", i2);
            }
            ChatActivity chatActivity = new ChatActivity(bundle);
            chatActivity.setPreloadedSticker(getMediaDataController().getGreetingsSticker(), false);
            presentFragment(chatActivity, z);
            if (!AndroidUtilities.isTablet()) {
                return;
            }
        }
        lambda$onBackPressed$321();
    }

    public void openAddMember() {
        Bundle bundle = new Bundle();
        bundle.putBoolean("addToGroup", true);
        bundle.putLong("chatId", this.currentChat.id);
        GroupCreateActivity groupCreateActivity = new GroupCreateActivity(bundle);
        groupCreateActivity.setInfo(this.chatInfo);
        TLRPC.ChatFull chatFull = this.chatInfo;
        if (chatFull != null && chatFull.participants != null) {
            LongSparseArray longSparseArray = new LongSparseArray();
            for (int i = 0; i < this.chatInfo.participants.participants.size(); i++) {
                longSparseArray.put(this.chatInfo.participants.participants.get(i).user_id, null);
            }
            groupCreateActivity.setIgnoreUsers(longSparseArray);
        }
        groupCreateActivity.setDelegate2(new GroupCreateActivity.ContactsAddActivityDelegate() {
            @Override
            public final void didSelectUsers(ArrayList arrayList, int i2) {
                ProfileActivity.this.lambda$openAddMember$69(arrayList, i2);
            }

            @Override
            public void needAddBot(TLRPC.User user) {
                GroupCreateActivity.ContactsAddActivityDelegate.CC.$default$needAddBot(this, user);
            }
        });
        presentFragment(groupCreateActivity);
    }

    public void openAddToContact(final TLRPC.User user, Bundle bundle) {
        ContactAddActivity contactAddActivity = new ContactAddActivity(bundle, this.resourcesProvider);
        contactAddActivity.setDelegate(new ContactAddActivity.ContactAddActivityDelegate() {
            @Override
            public final void didAddToContacts() {
                ProfileActivity.this.lambda$openAddToContact$91(user);
            }
        });
        presentFragment(contactAddActivity);
    }

    public void openAvatar() {
        TLRPC.Chat chat;
        TLRPC.ChatPhoto chatPhoto;
        ImageLocation imageLocation;
        if (this.listView.getScrollState() == 1) {
            return;
        }
        if (this.userId != 0) {
            TLRPC.User user = getMessagesController().getUser(Long.valueOf(this.userId));
            TLRPC.UserProfilePhoto userProfilePhoto = user.photo;
            if (userProfilePhoto == null || userProfilePhoto.photo_big == null) {
                return;
            }
            PhotoViewer.getInstance().setParentActivity(this);
            TLRPC.UserProfilePhoto userProfilePhoto2 = user.photo;
            int i = userProfilePhoto2.dc_id;
            if (i != 0) {
                userProfilePhoto2.photo_big.dc_id = i;
            }
            PhotoViewer.getInstance().openPhoto(user.photo.photo_big, this.provider);
            return;
        }
        if (this.chatId == 0 || (chatPhoto = (chat = getMessagesController().getChat(Long.valueOf(this.chatId))).photo) == null || chatPhoto.photo_big == null) {
            return;
        }
        PhotoViewer.getInstance().setParentActivity(this);
        TLRPC.ChatPhoto chatPhoto2 = chat.photo;
        int i2 = chatPhoto2.dc_id;
        if (i2 != 0) {
            chatPhoto2.photo_big.dc_id = i2;
        }
        TLRPC.ChatFull chatFull = this.chatInfo;
        if (chatFull != null) {
            TLRPC.Photo photo = chatFull.chat_photo;
            if ((photo instanceof TLRPC.TL_photo) && !photo.video_sizes.isEmpty()) {
                imageLocation = ImageLocation.getForPhoto(this.chatInfo.chat_photo.video_sizes.get(0), this.chatInfo.chat_photo);
                PhotoViewer.getInstance().openPhotoWithVideo(chat.photo.photo_big, imageLocation, this.provider);
            }
        }
        imageLocation = null;
        PhotoViewer.getInstance().openPhotoWithVideo(chat.photo.photo_big, imageLocation, this.provider);
    }

    public void openDiscussion() {
        TLRPC.ChatFull chatFull = this.chatInfo;
        if (chatFull == null || chatFull.linked_chat_id == 0) {
            return;
        }
        Bundle bundle = new Bundle();
        bundle.putLong("chat_id", this.chatInfo.linked_chat_id);
        if (getMessagesController().checkCanOpenChat(bundle, this)) {
            presentFragment(new ChatActivity(bundle));
        }
    }

    private void openLocation(boolean z) {
        TLRPC.TL_businessLocation tL_businessLocation;
        TLRPC.UserFull userFull = this.userInfo;
        if (userFull == null || (tL_businessLocation = userFull.business_location) == null) {
            return;
        }
        if (tL_businessLocation.geo_point == null || z) {
            String str = BuildVars.isHuaweiStoreApp() ? "mapapp://navigation" : "http://maps.google.com/maps";
            try {
                getParentActivity().startActivity(new Intent("android.intent.action.VIEW", Uri.parse(String.format(Locale.US, str + "?q=" + this.userInfo.business_location.address, new Object[0]))));
                return;
            } catch (Exception e) {
                FileLog.e(e);
                return;
            }
        }
        AnonymousClass55 anonymousClass55 = new LocationActivity(3) {
            AnonymousClass55(int i) {
                super(i);
            }

            @Override
            protected boolean disablePermissionCheck() {
                return true;
            }
        };
        anonymousClass55.setResourceProvider(this.resourcesProvider);
        TLRPC.TL_message tL_message = new TLRPC.TL_message();
        tL_message.local_id = -1;
        tL_message.peer_id = getMessagesController().getPeer(getDialogId());
        TLRPC.TL_messageMediaGeo tL_messageMediaGeo = new TLRPC.TL_messageMediaGeo();
        TLRPC.TL_businessLocation tL_businessLocation2 = this.userInfo.business_location;
        tL_messageMediaGeo.geo = tL_businessLocation2.geo_point;
        tL_messageMediaGeo.address = tL_businessLocation2.address;
        tL_message.media = tL_messageMediaGeo;
        anonymousClass55.setSharingAllowed(false);
        anonymousClass55.setMessageObject(new MessageObject(UserConfig.selectedAccount, tL_message, false, false));
        presentFragment(anonymousClass55);
    }

    private void openRightsEdit(int i, TLRPC.User user, TLRPC.ChatParticipant chatParticipant, TLRPC.TL_chatAdminRights tL_chatAdminRights, TLRPC.TL_chatBannedRights tL_chatBannedRights, String str, boolean z) {
        boolean[] zArr = new boolean[1];
        AnonymousClass38 anonymousClass38 = new ChatRightsEditActivity(user.id, this.chatId, tL_chatAdminRights, this.currentChat.default_banned_rights, tL_chatBannedRights, str, i, true, false, null) {
            final boolean[] val$needShowBulletin;
            final TLRPC.User val$user;

            AnonymousClass38(long j, long j2, TLRPC.TL_chatAdminRights tL_chatAdminRights2, TLRPC.TL_chatBannedRights tL_chatBannedRights2, TLRPC.TL_chatBannedRights tL_chatBannedRights3, String str2, int i2, boolean z2, boolean z22, String str22, boolean[] zArr2, TLRPC.User user2) {
                super(j, j2, tL_chatAdminRights2, tL_chatBannedRights2, tL_chatBannedRights3, str2, i2, z2, z22, str22);
                r28 = zArr2;
                r29 = user2;
            }

            @Override
            public void onTransitionAnimationEnd(boolean z2, boolean z22) {
                if (!z2 && z22 && r28[0] && BulletinFactory.canShowBulletin(ProfileActivity.this)) {
                    BulletinFactory.createPromoteToAdminBulletin(ProfileActivity.this, r29.first_name).show();
                }
            }
        };
        anonymousClass38.setDelegate(new ChatRightsEditActivity.ChatRightsEditActivityDelegate() {
            final int val$action;
            final boolean val$editingAdmin;
            final boolean[] val$needShowBulletin;
            final TLRPC.ChatParticipant val$participant;

            AnonymousClass39(int i2, TLRPC.ChatParticipant chatParticipant2, boolean z2, boolean[] zArr2) {
                r2 = i2;
                r3 = chatParticipant2;
                r4 = z2;
                r5 = zArr2;
            }

            @Override
            public void didChangeOwner(TLRPC.User user2) {
                ProfileActivity.this.undoView.showWithAction(-ProfileActivity.this.chatId, ProfileActivity.this.currentChat.megagroup ? 10 : 9, user2);
            }

            @Override
            public void didSetRights(int i2, TLRPC.TL_chatAdminRights tL_chatAdminRights2, TLRPC.TL_chatBannedRights tL_chatBannedRights2, String str2) {
                boolean z2;
                int i22 = r2;
                int i3 = 0;
                if (i22 == 0) {
                    TLRPC.ChatParticipant chatParticipant2 = r3;
                    if (chatParticipant2 instanceof TLRPC.TL_chatChannelParticipant) {
                        TLRPC.TL_chatChannelParticipant tL_chatChannelParticipant = (TLRPC.TL_chatChannelParticipant) chatParticipant2;
                        if (i2 == 1) {
                            TLRPC.TL_channelParticipantAdmin tL_channelParticipantAdmin = new TLRPC.TL_channelParticipantAdmin();
                            tL_chatChannelParticipant.channelParticipant = tL_channelParticipantAdmin;
                            tL_channelParticipantAdmin.flags |= 4;
                        } else {
                            tL_chatChannelParticipant.channelParticipant = new TLRPC.TL_channelParticipant();
                        }
                        tL_chatChannelParticipant.channelParticipant.inviter_id = ProfileActivity.this.getUserConfig().getClientUserId();
                        tL_chatChannelParticipant.channelParticipant.peer = new TLRPC.TL_peerUser();
                        TLRPC.ChannelParticipant channelParticipant = tL_chatChannelParticipant.channelParticipant;
                        TLRPC.Peer peer = channelParticipant.peer;
                        TLRPC.ChatParticipant chatParticipant22 = r3;
                        peer.user_id = chatParticipant22.user_id;
                        channelParticipant.date = chatParticipant22.date;
                        channelParticipant.banned_rights = tL_chatBannedRights2;
                        channelParticipant.admin_rights = tL_chatAdminRights2;
                        channelParticipant.rank = str2;
                    } else if (chatParticipant2 != null) {
                        TLRPC.ChatParticipant tL_chatParticipantAdmin = i2 == 1 ? new TLRPC.TL_chatParticipantAdmin() : new TLRPC.TL_chatParticipant();
                        TLRPC.ChatParticipant chatParticipant3 = r3;
                        tL_chatParticipantAdmin.user_id = chatParticipant3.user_id;
                        tL_chatParticipantAdmin.date = chatParticipant3.date;
                        tL_chatParticipantAdmin.inviter_id = chatParticipant3.inviter_id;
                        int indexOf = ProfileActivity.this.chatInfo.participants.participants.indexOf(r3);
                        if (indexOf >= 0) {
                            ProfileActivity.this.chatInfo.participants.participants.set(indexOf, tL_chatParticipantAdmin);
                        }
                    }
                    if (i2 != 1 || r4) {
                        return;
                    }
                    r5[0] = true;
                    return;
                }
                if (i22 == 1 && i2 == 0 && ProfileActivity.this.currentChat.megagroup && ProfileActivity.this.chatInfo != null && ProfileActivity.this.chatInfo.participants != null) {
                    int i4 = 0;
                    while (true) {
                        if (i4 >= ProfileActivity.this.chatInfo.participants.participants.size()) {
                            z2 = false;
                            break;
                        } else {
                            if (MessageObject.getPeerId(((TLRPC.TL_chatChannelParticipant) ProfileActivity.this.chatInfo.participants.participants.get(i4)).channelParticipant.peer) == r3.user_id) {
                                ProfileActivity.this.chatInfo.participants_count--;
                                ProfileActivity.this.chatInfo.participants.participants.remove(i4);
                                z2 = true;
                                break;
                            }
                            i4++;
                        }
                    }
                    if (ProfileActivity.this.chatInfo != null && ProfileActivity.this.chatInfo.participants != null) {
                        while (true) {
                            if (i3 >= ProfileActivity.this.chatInfo.participants.participants.size()) {
                                break;
                            }
                            if (ProfileActivity.this.chatInfo.participants.participants.get(i3).user_id == r3.user_id) {
                                ProfileActivity.this.chatInfo.participants.participants.remove(i3);
                                z2 = true;
                                break;
                            }
                            i3++;
                        }
                    }
                    if (z2) {
                        ProfileActivity.this.updateOnlineCount(true);
                        ProfileActivity.this.updateRowsIds();
                        ProfileActivity.this.listAdapter.notifyDataSetChanged();
                    }
                }
            }
        });
        presentFragment(anonymousClass38);
    }

    public void openUrl(String str, Browser.Progress progress) {
        if (str.startsWith("@")) {
            getMessagesController().openByUserName(str.substring(1), this, 0, progress);
            return;
        }
        if (str.startsWith("#") || str.startsWith("$")) {
            DialogsActivity dialogsActivity = new DialogsActivity(null);
            dialogsActivity.setSearchString(str);
            presentFragment(dialogsActivity);
        } else {
            if (!str.startsWith("/") || this.parentLayout.getFragmentStack().size() <= 1) {
                return;
            }
            BaseFragment baseFragment = (BaseFragment) this.parentLayout.getFragmentStack().get(this.parentLayout.getFragmentStack().size() - 2);
            if (baseFragment instanceof ChatActivity) {
                lambda$onBackPressed$321();
                ((ChatActivity) baseFragment).chatActivityEnterView.setCommand(null, str, false, false);
            }
        }
    }

    public boolean processOnClickOrPress(final int i, final View view, final float f, final float f2) {
        TLRPC.Chat chat;
        String publicUsername;
        TLRPC.TL_username findUsername;
        StringBuilder sb;
        StringBuilder sb2;
        String str;
        int i2;
        String str2;
        TLRPC.UserFull userFull;
        TLRPC.UserFull userFull2;
        final String str3;
        View view2 = view;
        String str4 = "";
        if (i == this.usernameRow || i == this.setUsernameRow) {
            if (this.userId != 0) {
                TLRPC.User user = getMessagesController().getUser(Long.valueOf(this.userId));
                publicUsername = UserObject.getPublicUsername(user);
                if (user == null || publicUsername == null) {
                    return false;
                }
                findUsername = DialogObject.findUsername(publicUsername, user);
            } else {
                if (this.chatId == 0 || (chat = getMessagesController().getChat(Long.valueOf(this.chatId))) == null || (this.topicId == 0 && !ChatObject.isPublic(chat))) {
                    return false;
                }
                publicUsername = ChatObject.getPublicUsername(chat);
                findUsername = DialogObject.findUsername(publicUsername, chat);
            }
            final TLRPC.TL_username tL_username = findUsername;
            if (this.userId != 0) {
                if (editRow(view2, i)) {
                    return true;
                }
                if (tL_username != null && !tL_username.editable) {
                    TL_fragment.TL_getCollectibleInfo tL_getCollectibleInfo = new TL_fragment.TL_getCollectibleInfo();
                    TL_fragment.TL_inputCollectibleUsername tL_inputCollectibleUsername = new TL_fragment.TL_inputCollectibleUsername();
                    tL_inputCollectibleUsername.username = tL_username.username;
                    tL_getCollectibleInfo.collectible = tL_inputCollectibleUsername;
                    getConnectionsManager().bindRequestToGuid(getConnectionsManager().sendRequest(tL_getCollectibleInfo, new RequestDelegate() {
                        @Override
                        public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                            ProfileActivity.this.lambda$processOnClickOrPress$50(tL_username, tLObject, tL_error);
                        }
                    }), getClassGuid());
                    return true;
                }
                try {
                    BulletinFactory.of(this).createCopyBulletin(LocaleController.getString(R.string.UsernameCopied), this.resourcesProvider).show();
                    ((ClipboardManager) ApplicationLoader.applicationContext.getSystemService("clipboard")).setPrimaryClip(ClipData.newPlainText("label", "@" + publicUsername));
                    return true;
                } catch (Exception e) {
                    FileLog.e(e);
                    return true;
                }
            }
            TLRPC.Chat chat2 = getMessagesController().getChat(Long.valueOf(this.chatId));
            if (!ChatObject.isPublic(chat2)) {
                sb = new StringBuilder();
                sb.append("https://");
                sb.append(getMessagesController().linkPrefix);
                sb.append("/c/");
                sb.append(chat2.id);
                if (this.topicId != 0) {
                    sb2 = new StringBuilder();
                    sb2.append("/");
                    sb2.append(this.topicId);
                    str4 = sb2.toString();
                }
                sb.append(str4);
                String sb3 = sb.toString();
                final AnonymousClass40 anonymousClass40 = new AnonymousClass40(getParentActivity(), null, sb3, false, sb3, false);
                showDialog(anonymousClass40);
                return tL_username == null ? true : true;
            }
            sb = new StringBuilder();
            sb.append("https://");
            sb.append(getMessagesController().linkPrefix);
            sb.append("/");
            sb.append(ChatObject.getPublicUsername(chat2));
            if (this.topicId != 0) {
                sb2 = new StringBuilder();
                sb2.append("/");
                sb2.append(this.topicId);
                str4 = sb2.toString();
            }
            sb.append(str4);
            String sb32 = sb.toString();
            final AnonymousClass40 anonymousClass402 = new AnonymousClass40(getParentActivity(), null, sb32, false, sb32, false);
            showDialog(anonymousClass402);
            if (tL_username == null && !tL_username.editable) {
                TL_fragment.TL_getCollectibleInfo tL_getCollectibleInfo2 = new TL_fragment.TL_getCollectibleInfo();
                TL_fragment.TL_inputCollectibleUsername tL_inputCollectibleUsername2 = new TL_fragment.TL_inputCollectibleUsername();
                tL_inputCollectibleUsername2.username = tL_username.username;
                tL_getCollectibleInfo2.collectible = tL_inputCollectibleUsername2;
                getConnectionsManager().bindRequestToGuid(getConnectionsManager().sendRequest(tL_getCollectibleInfo2, new RequestDelegate() {
                    @Override
                    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                        ProfileActivity.this.lambda$processOnClickOrPress$48(tL_username, anonymousClass402, tLObject, tL_error);
                    }
                }), getClassGuid());
                return true;
            }
        }
        if (i != this.phoneRow && i != this.numberRow) {
            if (i == this.channelInfoRow || i == this.userInfoRow || i == this.locationRow || i == this.bioRow) {
                if (i == this.bioRow && ((userFull = this.userInfo) == null || TextUtils.isEmpty(userFull.about))) {
                    return false;
                }
                if (editRow(view2, i)) {
                    return true;
                }
                if ((view2 instanceof AboutLinkCell) && ((AboutLinkCell) view2).onClick()) {
                    return false;
                }
                if (i == this.locationRow) {
                    TLRPC.ChatFull chatFull = this.chatInfo;
                    if (chatFull != null) {
                        TLRPC.ChannelLocation channelLocation = chatFull.location;
                        if (channelLocation instanceof TLRPC.TL_channelLocation) {
                            str2 = ((TLRPC.TL_channelLocation) channelLocation).address;
                        }
                    }
                    str2 = null;
                } else if (i == this.channelInfoRow) {
                    TLRPC.ChatFull chatFull2 = this.chatInfo;
                    if (chatFull2 != null) {
                        str2 = chatFull2.about;
                    }
                    str2 = null;
                } else {
                    TLRPC.UserFull userFull3 = this.userInfo;
                    if (userFull3 != null) {
                        str2 = userFull3.about;
                    }
                    str2 = null;
                }
                final String str5 = str2;
                if (TextUtils.isEmpty(str5)) {
                    return false;
                }
                final String[] strArr = {"und"};
                final boolean isContextTranslateEnabled = MessagesController.getInstance(this.currentAccount).getTranslateController().isContextTranslateEnabled();
                final boolean[] zArr = {i == this.bioRow || i == this.channelInfoRow || i == this.userInfoRow};
                final String language = LocaleController.getInstance().getCurrentLocale().getLanguage();
                final Runnable runnable = new Runnable() {
                    @Override
                    public final void run() {
                        ProfileActivity.this.lambda$processOnClickOrPress$57(zArr, str5, i, strArr, language, f, f2, view);
                    }
                };
                if (zArr[0] && LanguageDetector.hasSupport()) {
                    LanguageDetector.detectLanguage(str5, new LanguageDetector.StringCallback() {
                        @Override
                        public final void run(String str6) {
                            ProfileActivity.this.lambda$processOnClickOrPress$58(strArr, zArr, language, isContextTranslateEnabled, runnable, str6);
                        }
                    }, new LanguageDetector.ExceptionCallback() {
                        @Override
                        public final void run(Exception exc) {
                            ProfileActivity.lambda$processOnClickOrPress$59(runnable, exc);
                        }
                    });
                } else {
                    runnable.run();
                }
                return true;
            }
            if ((i != this.bizHoursRow && i != this.bizLocationRow) || getParentActivity() == null || (userFull2 = this.userInfo) == null) {
                return false;
            }
            if (i == this.bizHoursRow) {
                TL_account.TL_businessWorkHours tL_businessWorkHours = userFull2.business_work_hours;
                if (tL_businessWorkHours == null) {
                    return false;
                }
                str3 = OpeningHoursActivity.toString(this.currentAccount, userFull2.user, tL_businessWorkHours);
            } else {
                if (i != this.bizLocationRow || editRow(view2, i)) {
                    return true;
                }
                TLRPC.TL_businessLocation tL_businessLocation = this.userInfo.business_location;
                if (tL_businessLocation == null) {
                    return false;
                }
                str3 = tL_businessLocation.address;
            }
            final AtomicReference atomicReference = new AtomicReference();
            AnonymousClass43 anonymousClass43 = new ActionBarPopupWindow.ActionBarPopupWindowLayout(getContext(), R.drawable.popup_fixed_alert, this.resourcesProvider) {
                Path path = new Path();

                AnonymousClass43(Context context, int i3, Theme.ResourcesProvider resourcesProvider) {
                    super(context, i3, resourcesProvider);
                    this.path = new Path();
                }

                @Override
                protected boolean drawChild(Canvas canvas, View view3, long j) {
                    canvas.save();
                    this.path.rewind();
                    RectF rectF = AndroidUtilities.rectTmp;
                    rectF.set(view3.getLeft(), view3.getTop(), view3.getRight(), view3.getBottom());
                    this.path.addRoundRect(rectF, AndroidUtilities.dp(6.0f), AndroidUtilities.dp(6.0f), Path.Direction.CW);
                    canvas.clipPath(this.path);
                    boolean drawChild = super.drawChild(canvas, view3, j);
                    canvas.restore();
                    return drawChild;
                }
            };
            anonymousClass43.setFitItems(true);
            ActionBarMenuItem.addItem(anonymousClass43, R.drawable.msg_copy, LocaleController.getString(R.string.Copy), false, this.resourcesProvider).setOnClickListener(new View.OnClickListener() {
                @Override
                public final void onClick(View view3) {
                    ProfileActivity.this.lambda$processOnClickOrPress$60(atomicReference, str3, i, view3);
                }
            });
            ActionBarPopupWindow actionBarPopupWindow = new ActionBarPopupWindow(anonymousClass43, -2, -2);
            actionBarPopupWindow.setPauseNotifications(true);
            actionBarPopupWindow.setDismissAnimationDuration(220);
            actionBarPopupWindow.setOutsideTouchable(true);
            actionBarPopupWindow.setClippingEnabled(true);
            actionBarPopupWindow.setAnimationStyle(R.style.PopupContextAnimation);
            actionBarPopupWindow.setFocusable(true);
            anonymousClass43.measure(View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(1000.0f), Integer.MIN_VALUE), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(1000.0f), Integer.MIN_VALUE));
            actionBarPopupWindow.setInputMethodMode(2);
            actionBarPopupWindow.getContentView().setFocusableInTouchMode(true);
            atomicReference.set(actionBarPopupWindow);
            float f3 = f;
            float f4 = f2;
            while (view2 != getFragmentView()) {
                f3 += view2.getX();
                f4 += view2.getY();
                view2 = (View) view2.getParent();
            }
            if (AndroidUtilities.isTablet()) {
                ViewGroup view3 = this.parentLayout.getView();
                f3 += view3.getX() + view3.getPaddingLeft();
                f4 += view3.getY() + view3.getPaddingTop();
            }
            actionBarPopupWindow.showAtLocation(getFragmentView(), 0, (int) (f3 - (anonymousClass43.getMeasuredWidth() / 2.0f)), (int) f4);
            actionBarPopupWindow.dimBehind();
            return true;
        }
        if (editRow(view2, i)) {
            return true;
        }
        final TLRPC.User user2 = getMessagesController().getUser(Long.valueOf(this.userId));
        if (user2 == null || (str = user2.phone) == null || str.length() == 0 || getParentActivity() == null) {
            return false;
        }
        if (i == this.phoneRow && user2.phone.startsWith("888")) {
            TL_fragment.TL_inputCollectiblePhone tL_inputCollectiblePhone = new TL_fragment.TL_inputCollectiblePhone();
            final String str6 = user2.phone;
            tL_inputCollectiblePhone.phone = str6;
            TL_fragment.TL_getCollectibleInfo tL_getCollectibleInfo3 = new TL_fragment.TL_getCollectibleInfo();
            tL_getCollectibleInfo3.collectible = tL_inputCollectiblePhone;
            getConnectionsManager().bindRequestToGuid(getConnectionsManager().sendRequest(tL_getCollectibleInfo3, new RequestDelegate() {
                @Override
                public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                    ProfileActivity.this.lambda$processOnClickOrPress$52(str6, user2, tLObject, tL_error);
                }
            }), getClassGuid());
            return true;
        }
        ArrayList arrayList = new ArrayList();
        ArrayList arrayList2 = new ArrayList();
        ArrayList arrayList3 = new ArrayList();
        if (i == this.phoneRow) {
            TLRPC.UserFull userFull4 = this.userInfo;
            if (userFull4 != null && userFull4.phone_calls_available) {
                arrayList3.add(Integer.valueOf(R.drawable.msg_calls));
                arrayList.add(LocaleController.getString(R.string.CallViaTelegram));
                arrayList2.add(2);
                if (this.userInfo.video_calls_available) {
                    arrayList3.add(Integer.valueOf(R.drawable.msg_videocall));
                    arrayList.add(LocaleController.getString(R.string.VideoCallViaTelegram));
                    arrayList2.add(3);
                }
            }
            if (!this.isFragmentPhoneNumber) {
                arrayList3.add(Integer.valueOf(R.drawable.msg_calls_regular));
                arrayList.add(LocaleController.getString(R.string.Call));
                arrayList2.add(0);
            }
        }
        arrayList3.add(Integer.valueOf(R.drawable.msg_copy));
        arrayList.add(LocaleController.getString(R.string.Copy));
        arrayList2.add(1);
        final AtomicReference atomicReference2 = new AtomicReference();
        AnonymousClass41 anonymousClass41 = new ActionBarPopupWindow.ActionBarPopupWindowLayout(getContext(), R.drawable.popup_fixed_alert, this.resourcesProvider) {
            Path path = new Path();

            AnonymousClass41(Context context, int i3, Theme.ResourcesProvider resourcesProvider) {
                super(context, i3, resourcesProvider);
                this.path = new Path();
            }

            @Override
            protected boolean drawChild(Canvas canvas, View view4, long j) {
                canvas.save();
                this.path.rewind();
                RectF rectF = AndroidUtilities.rectTmp;
                rectF.set(view4.getLeft(), view4.getTop(), view4.getRight(), view4.getBottom());
                this.path.addRoundRect(rectF, AndroidUtilities.dp(6.0f), AndroidUtilities.dp(6.0f), Path.Direction.CW);
                canvas.clipPath(this.path);
                boolean drawChild = super.drawChild(canvas, view4, j);
                canvas.restore();
                return drawChild;
            }
        };
        anonymousClass41.setFitItems(true);
        for (int i3 = 0; i3 < arrayList3.size(); i3++) {
            final int intValue = ((Integer) arrayList2.get(i3)).intValue();
            ActionBarMenuItem.addItem(anonymousClass41, ((Integer) arrayList3.get(i3)).intValue(), (CharSequence) arrayList.get(i3), false, this.resourcesProvider).setOnClickListener(new View.OnClickListener() {
                @Override
                public final void onClick(View view4) {
                    ProfileActivity.this.lambda$processOnClickOrPress$53(atomicReference2, intValue, user2, view4);
                }
            });
        }
        if (this.isFragmentPhoneNumber) {
            View frameLayout = new FrameLayout(getContext());
            frameLayout.setBackgroundColor(Theme.getColor(Theme.key_actionBarDefaultSubmenuSeparator, this.resourcesProvider));
            anonymousClass41.addView(frameLayout, LayoutHelper.createLinear(-1, 8));
            TextView textView = new TextView(getContext());
            textView.setPadding(AndroidUtilities.dp(13.0f), AndroidUtilities.dp(8.0f), AndroidUtilities.dp(13.0f), AndroidUtilities.dp(8.0f));
            textView.setTextSize(1, 13.0f);
            textView.setTextColor(Theme.getColor(Theme.key_actionBarDefaultSubmenuItem, this.resourcesProvider));
            textView.setLinkTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteValueText, this.resourcesProvider));
            textView.setBackground(Theme.createRadSelectorDrawable(Theme.getColor(Theme.key_dialogButtonSelector, this.resourcesProvider), 0, 6));
            SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(AndroidUtilities.replaceTags(LocaleController.getString(R.string.AnonymousNumberNotice)));
            int indexOf = TextUtils.indexOf((CharSequence) spannableStringBuilder, '*');
            int lastIndexOf = TextUtils.lastIndexOf(spannableStringBuilder, '*');
            if (indexOf != -1 && lastIndexOf != -1 && indexOf != lastIndexOf) {
                spannableStringBuilder.replace(lastIndexOf, lastIndexOf + 1, (CharSequence) "");
                spannableStringBuilder.replace(indexOf, indexOf + 1, (CharSequence) "");
                int i4 = lastIndexOf - 1;
                spannableStringBuilder.setSpan(new TypefaceSpan(AndroidUtilities.bold()), indexOf, i4, 33);
                spannableStringBuilder.setSpan(new ForegroundColorSpan(textView.getLinkTextColors().getDefaultColor()), indexOf, i4, 33);
            }
            textView.setText(spannableStringBuilder);
            textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public final void onClick(View view4) {
                    ProfileActivity.lambda$processOnClickOrPress$54(view4);
                }
            });
            int i5 = R.id.fit_width_tag;
            frameLayout.setTag(i5, 1);
            textView.setTag(i5, 1);
            i2 = -2;
            anonymousClass41.addView((View) textView, LayoutHelper.createLinear(-2, -2));
        } else {
            i2 = -2;
        }
        ActionBarPopupWindow actionBarPopupWindow2 = new ActionBarPopupWindow(anonymousClass41, i2, i2);
        actionBarPopupWindow2.setPauseNotifications(true);
        actionBarPopupWindow2.setDismissAnimationDuration(220);
        actionBarPopupWindow2.setOutsideTouchable(true);
        actionBarPopupWindow2.setClippingEnabled(true);
        actionBarPopupWindow2.setAnimationStyle(R.style.PopupContextAnimation);
        actionBarPopupWindow2.setFocusable(true);
        anonymousClass41.measure(View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(1000.0f), Integer.MIN_VALUE), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(1000.0f), Integer.MIN_VALUE));
        actionBarPopupWindow2.setInputMethodMode(2);
        actionBarPopupWindow2.getContentView().setFocusableInTouchMode(true);
        atomicReference2.set(actionBarPopupWindow2);
        float f5 = f;
        float f6 = f2;
        while (view2 != getFragmentView() && view2 != null) {
            f5 += view2.getX();
            f6 += view2.getY();
            view2 = (View) view2.getParent();
        }
        if (AndroidUtilities.isTablet()) {
            ViewGroup view4 = this.parentLayout.getView();
            f5 += view4.getX() + view4.getPaddingLeft();
            f6 += view4.getY() + view4.getPaddingTop();
        }
        actionBarPopupWindow2.showAtLocation(getFragmentView(), 0, (int) (f5 - (anonymousClass41.getMeasuredWidth() / 2.0f)), (int) f6);
        actionBarPopupWindow2.dimBehind();
        return true;
    }

    private void refreshNameAndOnlineXY() {
        this.nameX = AndroidUtilities.dp(-21.0f) + (this.avatarContainer.getMeasuredWidth() * (this.avatarScale - 1.4285715f));
        this.nameY = ((float) Math.floor(this.avatarY)) + AndroidUtilities.dp(1.3f) + AndroidUtilities.dp(7.0f) + ((this.avatarContainer.getMeasuredHeight() * (this.avatarScale - 1.4285715f)) / 2.0f);
        this.onlineX = AndroidUtilities.dp(-21.0f) + (this.avatarContainer.getMeasuredWidth() * (this.avatarScale - 1.4285715f));
        this.onlineY = ((float) Math.floor(this.avatarY)) + AndroidUtilities.dp(24.0f) + ((float) Math.floor(AndroidUtilities.density * 11.0f)) + ((this.avatarContainer.getMeasuredHeight() * (this.avatarScale - 1.4285715f)) / 2.0f);
    }

    public void saveScrollPosition() {
        RecyclerListView recyclerListView = this.listView;
        if (recyclerListView == null || this.layoutManager == null || recyclerListView.getChildCount() <= 0 || this.savedScrollToSharedMedia) {
            return;
        }
        View view = null;
        int i = Integer.MAX_VALUE;
        int i2 = -1;
        for (int i3 = 0; i3 < this.listView.getChildCount(); i3++) {
            RecyclerListView recyclerListView2 = this.listView;
            int childAdapterPosition = recyclerListView2.getChildAdapterPosition(recyclerListView2.getChildAt(i3));
            View childAt = this.listView.getChildAt(i3);
            if (childAdapterPosition != -1 && childAt.getTop() < i) {
                i = childAt.getTop();
                i2 = childAdapterPosition;
                view = childAt;
            }
        }
        if (view != null) {
            this.savedScrollPosition = i2;
            int top = view.getTop();
            this.savedScrollOffset = top;
            if (this.savedScrollPosition == 0 && !this.allowPullingDown && top > AndroidUtilities.dp(88.0f)) {
                this.savedScrollOffset = AndroidUtilities.dp(88.0f);
            }
            this.layoutManager.scrollToPositionWithOffset(i2, view.getTop() - this.listView.getPaddingTop());
        }
    }

    public Animator searchExpandTransition(final boolean z) {
        if (z) {
            AndroidUtilities.requestAdjustResize(getParentActivity(), this.classGuid);
            AndroidUtilities.setAdjustResizeToNothing(getParentActivity(), this.classGuid);
        }
        Animator animator = this.searchViewTransition;
        if (animator != null) {
            animator.removeAllListeners();
            this.searchViewTransition.cancel();
        }
        final ValueAnimator ofFloat = ValueAnimator.ofFloat(this.searchTransitionProgress, z ? 0.0f : 1.0f);
        final float f = this.extraHeight;
        this.searchListView.setTranslationY(f);
        this.searchListView.setVisibility(0);
        this.searchItem.setVisibility(0);
        this.listView.setVisibility(0);
        needLayout(true);
        this.avatarContainer.setVisibility(0);
        this.nameTextView[1].setVisibility(0);
        this.onlineTextView[1].setVisibility(0);
        this.onlineTextView[3].setVisibility(0);
        this.actionBar.onSearchFieldVisibilityChanged(this.searchTransitionProgress > 0.5f);
        int i = this.searchTransitionProgress > 0.5f ? 0 : 8;
        ActionBarMenuItem actionBarMenuItem = this.otherItem;
        if (actionBarMenuItem != null) {
            actionBarMenuItem.setVisibility(i);
        }
        if (this.qrItem != null) {
            updateQrItemVisibility(false);
        }
        this.searchItem.setVisibility(i);
        this.searchItem.getSearchContainer().setVisibility(this.searchTransitionProgress <= 0.5f ? 0 : 8);
        this.searchListView.setEmptyView(this.emptyView);
        this.avatarContainer.setClickable(false);
        ofFloat.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                ProfileActivity.this.lambda$searchExpandTransition$82(ofFloat, f, z, valueAnimator);
            }
        });
        ofFloat.addListener(new AnimatorListenerAdapter() {
            final boolean val$enter;

            AnonymousClass52(final boolean z2) {
                r2 = z2;
            }

            @Override
            public void onAnimationEnd(Animator animator2) {
                ProfileActivity.this.updateSearchViewState(r2);
                ProfileActivity.this.avatarContainer.setClickable(true);
                if (r2) {
                    ProfileActivity.this.searchItem.requestFocusOnSearchView();
                }
                ProfileActivity.this.needLayout(true);
                ProfileActivity.this.searchViewTransition = null;
                ProfileActivity.this.fragmentView.invalidate();
                if (r2) {
                    ProfileActivity.this.invalidateScroll = true;
                    ProfileActivity.this.saveScrollPosition();
                    AndroidUtilities.requestAdjustResize(ProfileActivity.this.getParentActivity(), ((BaseFragment) ProfileActivity.this).classGuid);
                    ProfileActivity.this.emptyView.setPreventMoving(false);
                }
            }
        });
        if (!z2) {
            this.invalidateScroll = true;
            saveScrollPosition();
            AndroidUtilities.requestAdjustNothing(getParentActivity(), this.classGuid);
            this.emptyView.setPreventMoving(true);
        }
        ofFloat.setDuration(220L);
        ofFloat.setInterpolator(CubicBezierInterpolator.DEFAULT);
        this.searchViewTransition = ofFloat;
        return ofFloat;
    }

    public static void sendLogs(final Activity activity, final boolean z) {
        if (activity == null) {
            return;
        }
        final AlertDialog alertDialog = new AlertDialog(activity, 3);
        alertDialog.setCanCancel(false);
        alertDialog.show();
        Utilities.globalQueue.postRunnable(new Runnable() {
            @Override
            public final void run() {
                ProfileActivity.lambda$sendLogs$87(AlertDialog.this, z, activity);
            }
        });
    }

    public void setAutoDeleteHistory(int i, int i2) {
        long dialogId = getDialogId();
        getMessagesController().setDialogHistoryTTL(dialogId, i);
        if (this.userInfo == null && this.chatInfo == null) {
            return;
        }
        UndoView undoView = this.undoView;
        TLRPC.User user = getMessagesController().getUser(Long.valueOf(dialogId));
        TLRPC.UserFull userFull = this.userInfo;
        undoView.showWithAction(dialogId, i2, user, Integer.valueOf(userFull != null ? userFull.ttl_period : this.chatInfo.ttl_period), (Runnable) null, (Runnable) null);
    }

    private void setAvatarExpandProgress(float r23) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.ProfileActivity.setAvatarExpandProgress(float):void");
    }

    public void setForegroundImage(boolean z) {
        Drawable drawable = this.avatarImage.getImageReceiver().getDrawable();
        String str = null;
        if (drawable instanceof VectorAvatarThumbDrawable) {
            this.avatarImage.setForegroundImage(null, null, drawable);
            return;
        }
        if (drawable instanceof AnimatedFileDrawable) {
            AnimatedFileDrawable animatedFileDrawable = (AnimatedFileDrawable) drawable;
            this.avatarImage.setForegroundImage(null, null, animatedFileDrawable);
            if (z) {
                animatedFileDrawable.addSecondParentView(this.avatarImage);
                return;
            }
            return;
        }
        ImageLocation imageLocation = this.avatarsViewPager.getImageLocation(0);
        if (imageLocation != null && imageLocation.imageType == 2) {
            str = "avatar";
        }
        this.avatarImage.setForegroundImage(imageLocation, str, drawable);
    }

    private void setMediaHeaderVisible(boolean z) {
        if (this.mediaHeaderVisible == z) {
            return;
        }
        this.mediaHeaderVisible = z;
        AnimatorSet animatorSet = this.headerAnimatorSet;
        if (animatorSet != null) {
            animatorSet.cancel();
        }
        AnimatorSet animatorSet2 = this.headerShadowAnimatorSet;
        if (animatorSet2 != null) {
            animatorSet2.cancel();
        }
        ActionBarMenuItem searchItem = this.sharedMediaLayout.getSearchItem();
        RLottieImageView searchOptionsItem = this.sharedMediaLayout.getSearchOptionsItem();
        if (this.mediaHeaderVisible) {
            if (this.sharedMediaLayout.isSearchItemVisible()) {
                searchItem.setVisibility(0);
            }
            if (searchOptionsItem != null) {
                searchOptionsItem.setVisibility(0);
            }
            if (this.sharedMediaLayout.isOptionsItemVisible()) {
                this.sharedMediaLayout.photoVideoOptionsItem.setVisibility(0);
                this.sharedMediaLayout.animateSearchToOptions(true, false);
            } else {
                this.sharedMediaLayout.photoVideoOptionsItem.setVisibility(4);
                this.sharedMediaLayout.animateSearchToOptions(false, false);
            }
        } else {
            if (this.callItemVisible) {
                this.callItem.setVisibility(0);
            }
            if (this.videoCallItemVisible) {
                this.videoCallItem.setVisibility(0);
            }
            if (this.editItemVisible) {
                this.editItem.setVisibility(0);
            }
            this.otherItem.setVisibility(0);
            if (searchOptionsItem != null) {
                searchOptionsItem.setVisibility(8);
            }
        }
        updateStoriesViewBounds(false);
        ActionBar actionBar = this.actionBar;
        if (actionBar != null) {
            actionBar.createMenu().requestLayout();
        }
        ArrayList arrayList = new ArrayList();
        ActionBarMenuItem actionBarMenuItem = this.callItem;
        Property property = View.ALPHA;
        arrayList.add(ObjectAnimator.ofFloat(actionBarMenuItem, (Property<ActionBarMenuItem, Float>) property, z ? 0.0f : 1.0f));
        arrayList.add(ObjectAnimator.ofFloat(this.videoCallItem, (Property<ActionBarMenuItem, Float>) property, z ? 0.0f : 1.0f));
        arrayList.add(ObjectAnimator.ofFloat(this.otherItem, (Property<ActionBarMenuItem, Float>) property, z ? 0.0f : 1.0f));
        arrayList.add(ObjectAnimator.ofFloat(this.editItem, (Property<ActionBarMenuItem, Float>) property, z ? 0.0f : 1.0f));
        ActionBarMenuItem actionBarMenuItem2 = this.callItem;
        Property property2 = View.TRANSLATION_Y;
        arrayList.add(ObjectAnimator.ofFloat(actionBarMenuItem2, (Property<ActionBarMenuItem, Float>) property2, z ? -AndroidUtilities.dp(10.0f) : 0.0f));
        arrayList.add(ObjectAnimator.ofFloat(this.videoCallItem, (Property<ActionBarMenuItem, Float>) property2, z ? -AndroidUtilities.dp(10.0f) : 0.0f));
        arrayList.add(ObjectAnimator.ofFloat(this.otherItem, (Property<ActionBarMenuItem, Float>) property2, z ? -AndroidUtilities.dp(10.0f) : 0.0f));
        arrayList.add(ObjectAnimator.ofFloat(this.editItem, (Property<ActionBarMenuItem, Float>) property2, z ? -AndroidUtilities.dp(10.0f) : 0.0f));
        arrayList.add(ObjectAnimator.ofFloat(searchItem, (Property<ActionBarMenuItem, Float>) property, z ? 1.0f : 0.0f));
        arrayList.add(ObjectAnimator.ofFloat(searchItem, (Property<ActionBarMenuItem, Float>) property2, z ? 0.0f : AndroidUtilities.dp(10.0f)));
        arrayList.add(ObjectAnimator.ofFloat(this.sharedMediaLayout.photoVideoOptionsItem, (Property<ImageView, Float>) property, z ? 1.0f : 0.0f));
        arrayList.add(ObjectAnimator.ofFloat(this.sharedMediaLayout.photoVideoOptionsItem, (Property<ImageView, Float>) property2, z ? 0.0f : AndroidUtilities.dp(10.0f)));
        arrayList.add(ObjectAnimator.ofFloat(this.actionBar, (Property<ActionBar, Float>) this.ACTIONBAR_HEADER_PROGRESS, z ? 1.0f : 0.0f));
        arrayList.add(ObjectAnimator.ofFloat(this.onlineTextView[1], (Property<SimpleTextView, Float>) property, z ? 0.0f : 1.0f));
        if (this.myProfile) {
            arrayList.add(ObjectAnimator.ofFloat(this.onlineTextView[3], (Property<SimpleTextView, Float>) property, z ? 0.0f : 1.0f));
        }
        arrayList.add(ObjectAnimator.ofFloat(this.mediaCounterTextView, (Property<AudioPlayerAlert.ClippingTextViewSwitcher, Float>) property, z ? 1.0f : 0.0f));
        if (z) {
            arrayList.add(ObjectAnimator.ofFloat(this, (Property<ProfileActivity, Float>) this.HEADER_SHADOW, 0.0f));
        }
        if (this.storyView != null) {
            ValueAnimator ofFloat = ValueAnimator.ofFloat(0.0f, 1.0f);
            ofFloat.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                    ProfileActivity.this.lambda$setMediaHeaderVisible$65(valueAnimator);
                }
            });
            arrayList.add(ofFloat);
        }
        AnimatorSet animatorSet3 = new AnimatorSet();
        this.headerAnimatorSet = animatorSet3;
        animatorSet3.playTogether(arrayList);
        this.headerAnimatorSet.setInterpolator(CubicBezierInterpolator.DEFAULT);
        this.headerAnimatorSet.addListener(new AnimatorListenerAdapter() {
            final ActionBarMenuItem val$mediaSearchItem;

            class AnonymousClass1 extends AnimatorListenerAdapter {
                AnonymousClass1() {
                }

                @Override
                public void onAnimationEnd(Animator animator2) {
                    ProfileActivity.this.headerShadowAnimatorSet = null;
                }
            }

            AnonymousClass45(ActionBarMenuItem searchItem2) {
                r2 = searchItem2;
            }

            @Override
            public void onAnimationCancel(Animator animator) {
                ProfileActivity.this.headerAnimatorSet = null;
            }

            @Override
            public void onAnimationEnd(Animator animator) {
                if (ProfileActivity.this.headerAnimatorSet != null) {
                    if (ProfileActivity.this.mediaHeaderVisible) {
                        if (ProfileActivity.this.callItemVisible) {
                            ProfileActivity.this.callItem.setVisibility(8);
                        }
                        if (ProfileActivity.this.videoCallItemVisible) {
                            ProfileActivity.this.videoCallItem.setVisibility(8);
                        }
                        if (ProfileActivity.this.editItemVisible) {
                            ProfileActivity.this.editItem.setVisibility(8);
                        }
                        ProfileActivity.this.otherItem.setVisibility(8);
                    } else {
                        if (ProfileActivity.this.sharedMediaLayout.isSearchItemVisible()) {
                            r2.setVisibility(0);
                        }
                        ProfileActivity.this.sharedMediaLayout.photoVideoOptionsItem.setVisibility(4);
                        ProfileActivity.this.headerShadowAnimatorSet = new AnimatorSet();
                        AnimatorSet animatorSet4 = ProfileActivity.this.headerShadowAnimatorSet;
                        ProfileActivity profileActivity = ProfileActivity.this;
                        animatorSet4.playTogether(ObjectAnimator.ofFloat(profileActivity, (Property<ProfileActivity, Float>) profileActivity.HEADER_SHADOW, 1.0f));
                        ProfileActivity.this.headerShadowAnimatorSet.setDuration(100L);
                        ProfileActivity.this.headerShadowAnimatorSet.addListener(new AnimatorListenerAdapter() {
                            AnonymousClass1() {
                            }

                            @Override
                            public void onAnimationEnd(Animator animator2) {
                                ProfileActivity.this.headerShadowAnimatorSet = null;
                            }
                        });
                        ProfileActivity.this.headerShadowAnimatorSet.start();
                    }
                }
                ProfileActivity.this.updateStoriesViewBounds(false);
                ProfileActivity.this.headerAnimatorSet = null;
            }
        });
        this.headerAnimatorSet.setDuration(150L);
        this.headerAnimatorSet.start();
        NotificationCenter.getGlobalInstance().lambda$postNotificationNameOnUIThread$1(NotificationCenter.needCheckSystemBarColors, Boolean.TRUE);
    }

    public void showAvatarProgress(boolean z, boolean z2) {
        if (this.avatarProgressView == null) {
            return;
        }
        AnimatorSet animatorSet = this.avatarAnimation;
        if (animatorSet != null) {
            animatorSet.cancel();
            this.avatarAnimation = null;
        }
        if (!z2) {
            if (z) {
                this.avatarProgressView.setAlpha(1.0f);
                this.avatarProgressView.setVisibility(0);
                return;
            } else {
                this.avatarProgressView.setAlpha(0.0f);
                this.avatarProgressView.setVisibility(4);
                return;
            }
        }
        AnimatorSet animatorSet2 = new AnimatorSet();
        this.avatarAnimation = animatorSet2;
        if (z) {
            this.avatarProgressView.setVisibility(0);
            this.avatarAnimation.playTogether(ObjectAnimator.ofFloat(this.avatarProgressView, (Property<RadialProgressView, Float>) View.ALPHA, 1.0f));
        } else {
            animatorSet2.playTogether(ObjectAnimator.ofFloat(this.avatarProgressView, (Property<RadialProgressView, Float>) View.ALPHA, 0.0f));
        }
        this.avatarAnimation.setDuration(180L);
        this.avatarAnimation.addListener(new AnimatorListenerAdapter() {
            final boolean val$show;

            AnonymousClass53(boolean z3) {
                r2 = z3;
            }

            @Override
            public void onAnimationCancel(Animator animator) {
                ProfileActivity.this.avatarAnimation = null;
            }

            @Override
            public void onAnimationEnd(Animator animator) {
                if (ProfileActivity.this.avatarAnimation == null || ProfileActivity.this.avatarProgressView == null) {
                    return;
                }
                if (!r2) {
                    ProfileActivity.this.avatarProgressView.setVisibility(4);
                }
                ProfileActivity.this.avatarAnimation = null;
            }
        });
        this.avatarAnimation.start();
    }

    private void updateAutoDeleteItem() {
        if (this.autoDeleteItem == null || this.autoDeletePopupWrapper == null) {
            return;
        }
        TLRPC.UserFull userFull = this.userInfo;
        int i = (userFull == null && this.chatInfo == null) ? 0 : userFull != null ? userFull.ttl_period : this.chatInfo.ttl_period;
        this.autoDeleteItemDrawable.setTime(i);
        this.autoDeletePopupWrapper.lambda$updateItems$7(i);
    }

    public void updateAvatarRoundRadius() {
        this.avatarImage.setRoundRadius((int) AndroidUtilities.lerp(getSmallAvatarRoundRadius(), 0.0f, this.currentExpandAnimatorValue));
    }

    public void updateBottomButtonY() {
        FrameLayout frameLayout = this.bottomButtonsContainer;
        if (frameLayout == null) {
            return;
        }
        SharedMediaLayout sharedMediaLayout = this.sharedMediaLayout;
        frameLayout.setTranslationY((sharedMediaLayout == null || !sharedMediaLayout.isAttachedToWindow()) ? AndroidUtilities.dp(72.0f) : Math.max(0.0f, AndroidUtilities.dp(184.0f) - (this.listView.getMeasuredHeight() - this.sharedMediaLayout.getY())));
        Bulletin visibleBulletin = Bulletin.getVisibleBulletin();
        if (visibleBulletin != null) {
            visibleBulletin.updatePosition();
        }
    }

    private void updateEditColorIcon() {
        if (getContext() == null || this.editColorItem == null) {
            return;
        }
        if (getUserConfig().isPremium()) {
            this.editColorItem.setIcon(R.drawable.menu_profile_colors);
            return;
        }
        Drawable drawable = ContextCompat.getDrawable(getContext(), R.drawable.menu_profile_colors_locked);
        drawable.setColorFilter(new PorterDuffColorFilter(getThemedColor(Theme.key_actionBarDefaultSubmenuItemIcon), PorterDuff.Mode.SRC_IN));
        Drawable drawable2 = ContextCompat.getDrawable(getContext(), R.drawable.msg_gallery_locked2);
        drawable2.setColorFilter(new PorterDuffColorFilter(ColorUtils.blendARGB(-1, -16777216, 0.5f), PorterDuff.Mode.MULTIPLY));
        this.editColorItem.setIcon(new CombinedDrawable(drawable, drawable2, AndroidUtilities.dp(1.0f), -AndroidUtilities.dp(1.0f)) {
            AnonymousClass56(Drawable drawable3, Drawable drawable22, int i, int i2) {
                super(drawable3, drawable22, i, i2);
            }

            @Override
            public void setColorFilter(ColorFilter colorFilter) {
            }
        });
    }

    public void updateEmojiStatusDrawableColor() {
        updateEmojiStatusDrawableColor(this.lastEmojiStatusProgress);
    }

    public void updateEmojiStatusDrawableColor(float f) {
        for (int i = 0; i < 2; i++) {
            MessagesController.PeerColor peerColor = this.peerColor;
            int offsetColor = (peerColor == null || i != 1) ? AndroidUtilities.getOffsetColor(getThemedColor(Theme.key_profile_verifiedBackground), getThemedColor(Theme.key_player_actionBarTitle), this.mediaHeaderAnimationProgress, 1.0f) : ColorUtils.blendARGB(peerColor.getColor2(), this.peerColor.hasColor6(Theme.isCurrentThemeDark()) ? this.peerColor.getColor5() : this.peerColor.getColor3(), 0.5f);
            int blendARGB = ColorUtils.blendARGB(offsetColor, -1, f);
            int i2 = Theme.key_player_actionBarTitle;
            int blendARGB2 = ColorUtils.blendARGB(blendARGB, getThemedColor(i2), this.mediaHeaderAnimationProgress);
            AnimatedEmojiDrawable.SwapAnimatedEmojiDrawable swapAnimatedEmojiDrawable = this.emojiStatusDrawable[i];
            if (swapAnimatedEmojiDrawable != null) {
                swapAnimatedEmojiDrawable.setColor(Integer.valueOf(blendARGB2));
            }
            AnimatedEmojiDrawable.SwapAnimatedEmojiDrawable swapAnimatedEmojiDrawable2 = this.botVerificationDrawable[i];
            if (swapAnimatedEmojiDrawable2 != null) {
                swapAnimatedEmojiDrawable2.setColor(Integer.valueOf(ColorUtils.blendARGB(ColorUtils.blendARGB(offsetColor, -1711276033, f), getThemedColor(i2), this.mediaHeaderAnimationProgress)));
            }
            if (i == 1) {
                this.animatedStatusView.setColor(blendARGB2);
            }
        }
        this.lastEmojiStatusProgress = f;
    }

    public void updateEmojiStatusEffectPosition() {
        this.animatedStatusView.setScaleX(this.nameTextView[1].getScaleX());
        this.animatedStatusView.setScaleY(this.nameTextView[1].getScaleY());
        this.animatedStatusView.translate(this.nameTextView[1].getX() + (this.nameTextView[1].getRightDrawableX() * this.nameTextView[1].getScaleX()), this.nameTextView[1].getY() + (this.nameTextView[1].getHeight() - ((this.nameTextView[1].getHeight() - this.nameTextView[1].getRightDrawableY()) * this.nameTextView[1].getScaleY())));
    }

    public void updateExceptions() {
        if (this.isTopic || !ChatObject.isForum(this.currentChat)) {
            return;
        }
        getNotificationsController().loadTopicsNotificationsExceptions(-this.chatId, new j$.util.function.Consumer() {
            @Override
            public final void r(Object obj) {
                ProfileActivity.this.lambda$updateExceptions$2((HashSet) obj);
            }

            @Override
            public j$.util.function.Consumer andThen(j$.util.function.Consumer consumer) {
                return Consumer.CC.$default$andThen(this, consumer);
            }
        });
    }

    private void updateFloatingButtonColor() {
        if (getParentActivity() == null || this.floatingButtonContainer == null) {
            return;
        }
        Drawable createSimpleSelectorCircleDrawable = Theme.createSimpleSelectorCircleDrawable(AndroidUtilities.dp(56.0f), applyPeerColor(Theme.getColor(Theme.key_chats_actionBackground), false), applyPeerColor(Theme.getColor(Theme.key_chats_actionPressedBackground), false));
        if (Build.VERSION.SDK_INT < 21) {
            Drawable mutate = ContextCompat.getDrawable(getParentActivity(), R.drawable.floating_shadow).mutate();
            mutate.setColorFilter(new PorterDuffColorFilter(-16777216, PorterDuff.Mode.MULTIPLY));
            CombinedDrawable combinedDrawable = new CombinedDrawable(mutate, createSimpleSelectorCircleDrawable, 0, 0);
            combinedDrawable.setIconSize(AndroidUtilities.dp(56.0f), AndroidUtilities.dp(56.0f));
            createSimpleSelectorCircleDrawable = combinedDrawable;
        }
        this.floatingButtonContainer.setBackground(createSimpleSelectorCircleDrawable);
    }

    private void updateFloatingButtonOffset() {
        FrameLayout frameLayout = this.floatingButtonContainer;
        if (frameLayout != null) {
            frameLayout.setTranslationY(AndroidUtilities.dp(100.0f) * this.floatingButtonHideProgress);
        }
    }

    private void updateItemsUsername() {
        TLRPC.User user;
        if (this.myProfile && (user = getMessagesController().getUser(Long.valueOf(this.userId))) != null) {
            boolean z = UserObject.getPublicUsername(user) != null;
            this.setUsernameItem.setIcon(z ? R.drawable.menu_username_change : R.drawable.menu_username_set);
            this.setUsernameItem.setText(LocaleController.getString(z ? R.string.ProfileUsernameEdit : R.string.ProfileUsernameSet));
            this.linkItem.setVisibility(UserObject.getPublicUsername(user) == null ? 8 : 0);
        }
    }

    private void updateListAnimated(final boolean z, boolean z2) {
        if (this.listAdapter == null) {
            if (z) {
                updateOnlineCount(false);
            }
            updateRowsIds();
            return;
        }
        if (!z2 && this.listView.isInLayout()) {
            if (this.listView.isAttachedToWindow()) {
                this.listView.post(new Runnable() {
                    @Override
                    public final void run() {
                        ProfileActivity.this.lambda$updateListAnimated$90(z);
                    }
                });
                return;
            }
            return;
        }
        DiffCallback diffCallback = new DiffCallback();
        diffCallback.oldRowCount = this.rowCount;
        diffCallback.fillPositions(diffCallback.oldPositionToItem);
        diffCallback.oldChatParticipant.clear();
        diffCallback.oldChatParticipantSorted.clear();
        diffCallback.oldChatParticipant.addAll(this.visibleChatParticipants);
        diffCallback.oldChatParticipantSorted.addAll(this.visibleSortedUsers);
        diffCallback.oldMembersStartRow = this.membersStartRow;
        diffCallback.oldMembersEndRow = this.membersEndRow;
        if (z) {
            updateOnlineCount(false);
        }
        saveScrollPosition();
        updateRowsIds();
        diffCallback.fillPositions(diffCallback.newPositionToItem);
        try {
            DiffUtil.calculateDiff(diffCallback).dispatchUpdatesTo(this.listAdapter);
        } catch (Exception unused) {
            this.listAdapter.notifyDataSetChanged();
        }
        int i = this.savedScrollPosition;
        if (i >= 0) {
            this.layoutManager.scrollToPositionWithOffset(i, this.savedScrollOffset - this.listView.getPaddingTop());
        }
        AndroidUtilities.updateVisibleRows(this.listView);
    }

    public void updateOnlineCount(boolean z) {
        int i;
        TLRPC.UserStatus userStatus;
        boolean z2;
        this.onlineCount = 0;
        int currentTime = getConnectionsManager().getCurrentTime();
        this.sortedUsers.clear();
        TLRPC.ChatFull chatFull = this.chatInfo;
        if (!(chatFull instanceof TLRPC.TL_chatFull) && (!((z2 = chatFull instanceof TLRPC.TL_channelFull)) || chatFull.participants_count > 200 || chatFull.participants == null)) {
            if (!z2 || chatFull.participants_count <= 200) {
                return;
            }
            this.onlineCount = chatFull.online_count;
            return;
        }
        final ArrayList arrayList = new ArrayList();
        for (int i2 = 0; i2 < this.chatInfo.participants.participants.size(); i2++) {
            TLRPC.User user = getMessagesController().getUser(Long.valueOf(this.chatInfo.participants.participants.get(i2).user_id));
            if (user != null && (userStatus = user.status) != null && ((userStatus.expires > currentTime || user.id == getUserConfig().getClientUserId()) && user.status.expires > 10000)) {
                this.onlineCount++;
            }
            this.sortedUsers.add(Integer.valueOf(i2));
            if (user != null) {
                if (user.bot) {
                    i = -110;
                } else if (user.self) {
                    i = 50000 + currentTime;
                } else {
                    TLRPC.UserStatus userStatus2 = user.status;
                    if (userStatus2 != null) {
                        i = userStatus2.expires;
                    }
                }
                arrayList.add(Integer.valueOf(i));
            }
            i = Integer.MIN_VALUE;
            arrayList.add(Integer.valueOf(i));
        }
        try {
            Collections.sort(this.sortedUsers, Comparator$EL.reversed(Comparator$CC.comparingInt(new ToIntFunction() {
                @Override
                public final int applyAsInt(Object obj) {
                    int lambda$updateOnlineCount$73;
                    lambda$updateOnlineCount$73 = ProfileActivity.lambda$updateOnlineCount$73(arrayList, obj);
                    return lambda$updateOnlineCount$73;
                }
            })));
        } catch (Exception e) {
            FileLog.e(e);
        }
        if (z && this.listAdapter != null && this.membersStartRow > 0) {
            AndroidUtilities.updateVisibleRows(this.listView);
        }
        if (this.sharedMediaLayout == null || this.sharedMediaRow == -1) {
            return;
        }
        if ((this.sortedUsers.size() > 5 || this.usersForceShowingIn == 2) && this.usersForceShowingIn != 1) {
            this.sharedMediaLayout.setChatUsers(this.sortedUsers, this.chatInfo);
        }
    }

    public void updateProfileData(boolean r33) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.ProfileActivity.updateProfileData(boolean):void");
    }

    public void updateRowsIds() {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.ProfileActivity.updateRowsIds():void");
    }

    public void updateSearchViewState(boolean z) {
        int i = z ? 8 : 0;
        this.listView.setVisibility(i);
        this.searchListView.setVisibility(z ? 0 : 8);
        this.searchItem.getSearchContainer().setVisibility(z ? 0 : 8);
        this.actionBar.onSearchFieldVisibilityChanged(z);
        this.avatarContainer.setVisibility(i);
        ProfileStoriesView profileStoriesView = this.storyView;
        if (profileStoriesView != null) {
            profileStoriesView.setVisibility(i);
        }
        this.nameTextView[1].setVisibility(i);
        this.onlineTextView[1].setVisibility(i);
        this.onlineTextView[3].setVisibility(i);
        ActionBarMenuItem actionBarMenuItem = this.otherItem;
        if (actionBarMenuItem != null) {
            actionBarMenuItem.setAlpha(1.0f);
            this.otherItem.setVisibility(i);
        }
        ActionBarMenuItem actionBarMenuItem2 = this.qrItem;
        if (actionBarMenuItem2 != null) {
            actionBarMenuItem2.setAlpha(1.0f);
            this.qrItem.setVisibility((z || !isQrNeedVisible()) ? 8 : 0);
        }
        this.searchItem.setVisibility(i);
        this.avatarContainer.setAlpha(1.0f);
        ProfileStoriesView profileStoriesView2 = this.storyView;
        if (profileStoriesView2 != null) {
            profileStoriesView2.setAlpha(1.0f);
        }
        this.nameTextView[1].setAlpha(1.0f);
        this.onlineTextView[1].setAlpha(1.0f);
        this.searchItem.setAlpha(1.0f);
        this.listView.setAlpha(1.0f);
        this.searchListView.setAlpha(1.0f);
        this.emptyView.setAlpha(1.0f);
        if (z) {
            this.searchListView.setEmptyView(this.emptyView);
        } else {
            this.emptyView.setVisibility(8);
        }
    }

    private void updateSharedMediaRows() {
        if (this.listAdapter == null) {
            return;
        }
        updateListAnimated(false);
    }

    private void updateStar() {
        ImageView imageView;
        Integer num;
        int i;
        ImageView imageView2;
        TLRPC.Chat chat;
        if (this.starBgItem == null || (imageView = this.starFgItem) == null) {
            return;
        }
        if (!this.needStarImage || (chat = this.currentChat) == null || (chat.flags2 & 2048) == 0) {
            num = null;
            imageView.setTag(null);
            i = 8;
            this.starFgItem.setVisibility(8);
            imageView2 = this.starBgItem;
        } else {
            imageView.setTag(1);
            i = 0;
            this.starFgItem.setVisibility(0);
            imageView2 = this.starBgItem;
            num = 1;
        }
        imageView2.setTag(num);
        this.starBgItem.setVisibility(i);
    }

    public void updateStoriesViewBounds(boolean z) {
        ActionBar actionBar;
        if (this.storyView == null || (actionBar = this.actionBar) == null) {
            return;
        }
        float f = actionBar.getOccupyStatusBar() ? AndroidUtilities.statusBarHeight : 0.0f;
        float width = this.actionBar.getWidth();
        float max = this.actionBar.getBackButton() != null ? Math.max(0.0f, this.actionBar.getBackButton().getRight()) : 0.0f;
        if (this.actionBar.menu != null) {
            for (int i = 0; i < this.actionBar.menu.getChildCount(); i++) {
                View childAt = this.actionBar.menu.getChildAt(i);
                if (childAt.getAlpha() > 0.0f && childAt.getVisibility() == 0) {
                    float left = this.actionBar.menu.getLeft() + ((int) childAt.getX());
                    if (left < width) {
                        width = AndroidUtilities.lerp(width, left, childAt.getAlpha());
                    }
                }
            }
        }
        this.storyView.setBounds(max, width, f + ((this.actionBar.getHeight() - f) / 2.0f), !z);
    }

    private void updateTimeItem() {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.ProfileActivity.updateTimeItem():void");
    }

    private void updateTtlIcon() {
        TLRPC.UserFull userFull;
        if (this.ttlIconView == null) {
            return;
        }
        AndroidUtilities.updateViewVisibilityAnimated(this.ttlIconView, this.currentEncryptedChat == null && (((userFull = this.userInfo) != null && userFull.ttl_period > 0) || (this.chatInfo != null && ChatObject.canUserDoAdminAction(this.currentChat, 13) && this.chatInfo.ttl_period > 0)), 0.8f, this.fragmentOpened);
    }

    private void updatedPeerColor() {
        ProfileGiftsContainer profileGiftsContainer;
        SharedMediaLayout.ScrollSlidingTextTabStripInner scrollSlidingTextTabStripInner;
        TextView textView;
        int themedColor;
        this.adaptedColors.clear();
        TopView topView = this.topView;
        if (topView != null) {
            topView.setBackgroundColorId(this.peerColor, true);
        }
        SimpleTextView simpleTextView = this.onlineTextView[1];
        if (simpleTextView != null) {
            this.onlineTextView[1].setTextColor(ColorUtils.blendARGB(applyPeerColor(getThemedColor(simpleTextView.getTag() instanceof Integer ? ((Integer) this.onlineTextView[1].getTag()).intValue() : Theme.key_avatar_subtitleInProfileBlue), true, Boolean.valueOf(this.isOnline[0])), -1275068417, this.currentExpandAnimatorValue));
        }
        ShowDrawable showDrawable = this.showStatusButton;
        if (showDrawable != null) {
            showDrawable.setBackgroundColor(ColorUtils.blendARGB(Theme.multAlpha(Theme.adaptHSV(this.actionBarBackgroundColor, 0.18f, -0.1f), 0.5f), 603979775, this.currentExpandAnimatorValue));
        }
        ActionBar actionBar = this.actionBar;
        if (actionBar != null) {
            actionBar.setItemsBackgroundColor(this.peerColor != null ? 553648127 : getThemedColor(Theme.key_avatar_actionBarSelectorBlue), false);
            this.actionBar.setItemsColor(ColorUtils.blendARGB(this.peerColor != null ? -1 : getThemedColor(Theme.key_actionBarDefaultIcon), AvatarDrawable.getIconColorForId((this.userId != 0 || (ChatObject.isChannel(this.chatId, this.currentAccount) && !this.currentChat.megagroup)) ? 5L : this.chatId, this.resourcesProvider), this.avatarAnimationProgress), false);
        }
        if (this.verifiedDrawable[1] != null) {
            MessagesController.PeerColor peerColor = this.peerColor;
            if (peerColor != null) {
                themedColor = Theme.adaptHSV(ColorUtils.blendARGB(peerColor.getColor2(), this.peerColor.hasColor6(Theme.isCurrentThemeDark()) ? this.peerColor.getColor5() : this.peerColor.getColor3(), 0.4f), 0.1f, Theme.isCurrentThemeDark() ? -0.1f : -0.08f);
            } else {
                themedColor = getThemedColor(Theme.key_profile_verifiedBackground);
            }
            this.verifiedDrawable[1].setColorFilter(AndroidUtilities.getOffsetColor(themedColor, getThemedColor(Theme.key_player_actionBarTitle), this.mediaHeaderAnimationProgress, 1.0f), PorterDuff.Mode.MULTIPLY);
        }
        if (this.verifiedCheckDrawable[1] != null) {
            this.verifiedCheckDrawable[1].setColorFilter(AndroidUtilities.getOffsetColor(this.peerColor != null ? -1 : applyPeerColor(getThemedColor(Theme.key_profile_verifiedCheck)), getThemedColor(Theme.key_windowBackgroundWhite), this.mediaHeaderAnimationProgress, 1.0f), PorterDuff.Mode.MULTIPLY);
        }
        SimpleTextView simpleTextView2 = this.nameTextView[1];
        if (simpleTextView2 != null) {
            simpleTextView2.setTextColor(ColorUtils.blendARGB(this.peerColor != null ? -1 : getThemedColor(Theme.key_profile_title), -1, this.currentExpandAnimatorValue));
        }
        AutoDeletePopupWrapper autoDeletePopupWrapper = this.autoDeletePopupWrapper;
        if (autoDeletePopupWrapper != null && (textView = autoDeletePopupWrapper.textView) != null) {
            textView.invalidate();
        }
        AndroidUtilities.forEachViews((RecyclerView) this.listView, new com.google.android.exoplayer2.util.Consumer() {
            @Override
            public final void accept(Object obj) {
                ProfileActivity.this.lambda$updatedPeerColor$81((View) obj);
            }
        });
        SharedMediaLayout sharedMediaLayout = this.sharedMediaLayout;
        if (sharedMediaLayout != null && (scrollSlidingTextTabStripInner = sharedMediaLayout.scrollSlidingTextTabStrip) != null) {
            scrollSlidingTextTabStripInner.updateColors();
        }
        SharedMediaLayout sharedMediaLayout2 = this.sharedMediaLayout;
        if (sharedMediaLayout2 != null && (profileGiftsContainer = sharedMediaLayout2.giftsContainer) != null) {
            profileGiftsContainer.updateColors();
        }
        writeButtonSetBackground();
        updateEmojiStatusDrawableColor();
    }

    private void writeButtonSetBackground() {
        if (this.writeButton == null) {
            return;
        }
        try {
            Drawable mutate = this.fragmentView.getContext().getResources().getDrawable(R.drawable.floating_shadow_profile).mutate();
            PorterDuff.Mode mode = PorterDuff.Mode.MULTIPLY;
            mutate.setColorFilter(new PorterDuffColorFilter(-16777216, mode));
            int themedColor = getThemedColor(Theme.key_profile_actionBackground);
            int themedColor2 = getThemedColor(Theme.key_profile_actionPressedBackground);
            int themedColor3 = getThemedColor(Theme.key_profile_actionIcon);
            if (this.peerColor != null && Theme.hasHue(themedColor)) {
                themedColor = Theme.adaptHSV(this.peerColor.getBgColor1(false), 0.05f, -0.04f);
                themedColor2 = applyPeerColor2(themedColor2);
                themedColor3 = -1;
            }
            CombinedDrawable combinedDrawable = new CombinedDrawable(mutate, Theme.createSimpleSelectorCircleDrawable(AndroidUtilities.dp(56.0f), themedColor, themedColor2), 0, 0);
            combinedDrawable.setIconSize(AndroidUtilities.dp(56.0f), AndroidUtilities.dp(56.0f));
            this.writeButton.setBackground(combinedDrawable);
            this.writeButton.setColorFilter(new PorterDuffColorFilter(themedColor3, mode));
        } catch (Exception unused) {
        }
    }

    @Override
    public boolean canBeginSlide() {
        if (this.sharedMediaLayout.isSwipeBackEnabled()) {
            return super.canBeginSlide();
        }
        return false;
    }

    @Override
    public boolean canFinishFragment() {
        return ImageUpdater.ImageUpdaterDelegate.CC.$default$canFinishFragment(this);
    }

    @Override
    public boolean canSearchMembers() {
        return this.canSearchMembers;
    }

    @Override
    public ActionBar createActionBar(Context context) {
        ChatActivity.ThemeDelegate themeDelegate;
        BaseFragment lastFragment = this.parentLayout.getLastFragment();
        if ((lastFragment instanceof ChatActivity) && (themeDelegate = ((ChatActivity) lastFragment).themeDelegate) != null && themeDelegate.getCurrentTheme() != null) {
            this.resourcesProvider = lastFragment.getResourceProvider();
        }
        AnonymousClass5 anonymousClass5 = new ActionBar(context, this.resourcesProvider) {
            AnonymousClass5(Context context2, Theme.ResourcesProvider resourcesProvider) {
                super(context2, resourcesProvider);
            }

            @Override
            public void onLayout(boolean z, int i, int i2, int i3, int i4) {
                super.onLayout(z, i, i2, i3, i4);
                ProfileActivity.this.updateStoriesViewBounds(false);
            }

            @Override
            public boolean onTouchEvent(MotionEvent motionEvent) {
                ProfileActivity.this.avatarContainer.getHitRect(ProfileActivity.this.rect);
                if (ProfileActivity.this.rect.contains((int) motionEvent.getX(), (int) motionEvent.getY())) {
                    return false;
                }
                return super.onTouchEvent(motionEvent);
            }

            @Override
            public void setItemsColor(int i, boolean z) {
                super.setItemsColor(i, z);
                if (z || ProfileActivity.this.ttlIconView == null) {
                    return;
                }
                ProfileActivity.this.ttlIconView.setColorFilter(new PorterDuffColorFilter(i, PorterDuff.Mode.MULTIPLY));
            }
        };
        anonymousClass5.setForceSkipTouches(true);
        anonymousClass5.setBackgroundColor(0);
        anonymousClass5.setItemsBackgroundColor(this.peerColor != null ? 553648127 : getThemedColor(Theme.key_avatar_actionBarSelectorBlue), false);
        int i = Theme.key_actionBarDefaultIcon;
        anonymousClass5.setItemsColor(getThemedColor(i), false);
        anonymousClass5.setItemsColor(getThemedColor(i), true);
        anonymousClass5.setBackButtonDrawable(new BackDrawable(false));
        anonymousClass5.setCastShadows(false);
        anonymousClass5.setAddToContainer(false);
        anonymousClass5.setClipContent(true);
        anonymousClass5.setOccupyStatusBar((Build.VERSION.SDK_INT < 21 || AndroidUtilities.isTablet() || this.inBubbleMode) ? false : true);
        final ImageView backButton = anonymousClass5.getBackButton();
        backButton.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public final boolean onLongClick(View view) {
                boolean lambda$createActionBar$4;
                lambda$createActionBar$4 = ProfileActivity.this.lambda$createActionBar$4(backButton, view);
                return lambda$createActionBar$4;
            }
        });
        return anonymousClass5;
    }

    @Override
    public android.view.View createView(final android.content.Context r45) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.ProfileActivity.createView(android.content.Context):android.view.View");
    }

    @Override
    public void didReceivedNotification(int i, int i2, final Object... objArr) {
        TL_stories.PeerStories peerStories;
        ListAdapter listAdapter;
        TLRPC.ChatFull chatFull;
        TLRPC.ChatFull chatFull2;
        TLRPC.TL_inputGroupCall tL_inputGroupCall;
        TLRPC.Chat chat;
        RecyclerListView recyclerListView;
        RecyclerListView recyclerListView2;
        RecyclerListView.Holder holder;
        if (i == NotificationCenter.uploadStoryEnd || i == NotificationCenter.chatWasBoostedByUser) {
            checkCanSendStoryForPosting();
            return;
        }
        r2 = 0;
        char c = 0;
        if (i == NotificationCenter.updateInterfaces) {
            int intValue = ((Integer) objArr[0]).intValue();
            boolean z = ((MessagesController.UPDATE_MASK_AVATAR & intValue) == 0 && (MessagesController.UPDATE_MASK_NAME & intValue) == 0 && (MessagesController.UPDATE_MASK_STATUS & intValue) == 0 && (MessagesController.UPDATE_MASK_EMOJI_STATUS & intValue) == 0) ? false : true;
            if (this.userId != 0) {
                if (z) {
                    updateProfileData(true);
                }
                if ((intValue & MessagesController.UPDATE_MASK_PHONE) == 0 || (recyclerListView2 = this.listView) == null || (holder = (RecyclerListView.Holder) recyclerListView2.findViewHolderForPosition(this.phoneRow)) == null) {
                    return;
                }
                this.listAdapter.onBindViewHolder(holder, this.phoneRow);
                return;
            }
            if (this.chatId != 0) {
                if ((MessagesController.UPDATE_MASK_CHAT & intValue) != 0 || (MessagesController.UPDATE_MASK_CHAT_AVATAR & intValue) != 0 || (MessagesController.UPDATE_MASK_CHAT_NAME & intValue) != 0 || (MessagesController.UPDATE_MASK_CHAT_MEMBERS & intValue) != 0 || (MessagesController.UPDATE_MASK_STATUS & intValue) != 0 || (MessagesController.UPDATE_MASK_EMOJI_STATUS & intValue) != 0) {
                    if ((MessagesController.UPDATE_MASK_CHAT & intValue) != 0) {
                        updateListAnimated(true);
                    } else {
                        updateOnlineCount(true);
                    }
                    updateProfileData(true);
                }
                if (!z || (recyclerListView = this.listView) == null) {
                    return;
                }
                int childCount = recyclerListView.getChildCount();
                for (int i3 = 0; i3 < childCount; i3++) {
                    View childAt = this.listView.getChildAt(i3);
                    if (childAt instanceof UserCell) {
                        ((UserCell) childAt).update(intValue);
                    }
                }
                return;
            }
            return;
        }
        if (i != NotificationCenter.chatOnlineCountDidLoad) {
            if (i == NotificationCenter.contactsDidLoad || i == NotificationCenter.channelRightsUpdated) {
                createActionBarMenu(true);
                return;
            }
            if (i == NotificationCenter.encryptedChatCreated) {
                if (this.creatingChat) {
                    AndroidUtilities.runOnUIThread(new Runnable() {
                        @Override
                        public final void run() {
                            ProfileActivity.this.lambda$didReceivedNotification$70(objArr);
                        }
                    });
                    return;
                }
                return;
            }
            if (i == NotificationCenter.encryptedChatUpdated) {
                TLRPC.EncryptedChat encryptedChat = (TLRPC.EncryptedChat) objArr[0];
                TLRPC.EncryptedChat encryptedChat2 = this.currentEncryptedChat;
                if (encryptedChat2 == null || encryptedChat.id != encryptedChat2.id) {
                    return;
                }
                this.currentEncryptedChat = encryptedChat;
                updateListAnimated(false);
                FlagSecureReason flagSecureReason = this.flagSecure;
                if (flagSecureReason != null) {
                    flagSecureReason.invalidate();
                    return;
                }
                return;
            }
            if (i == NotificationCenter.blockedUsersDidLoad) {
                boolean z2 = this.userBlocked;
                boolean z3 = getMessagesController().blockePeers.indexOfKey(this.userId) >= 0;
                this.userBlocked = z3;
                if (z2 == z3) {
                    return;
                } else {
                    createActionBarMenu(true);
                }
            } else {
                if (i == NotificationCenter.groupCallUpdated) {
                    Long l = (Long) objArr[0];
                    if (this.currentChat != null) {
                        long longValue = l.longValue();
                        TLRPC.Chat chat2 = this.currentChat;
                        if (longValue == chat2.id && ChatObject.canManageCalls(chat2)) {
                            TLRPC.ChatFull chatFull3 = MessagesController.getInstance(this.currentAccount).getChatFull(l.longValue());
                            if (chatFull3 != null) {
                                TLRPC.ChatFull chatFull4 = this.chatInfo;
                                if (chatFull4 != null) {
                                    chatFull3.participants = chatFull4.participants;
                                }
                                this.chatInfo = chatFull3;
                            }
                            SharedMediaLayout sharedMediaLayout = this.sharedMediaLayout;
                            if (sharedMediaLayout != null) {
                                sharedMediaLayout.setChatInfo(this.chatInfo);
                            }
                            TLRPC.ChatFull chatFull5 = this.chatInfo;
                            if (chatFull5 != null && (((tL_inputGroupCall = chatFull5.call) == null && !this.hasVoiceChatItem) || (tL_inputGroupCall != null && this.hasVoiceChatItem))) {
                                createActionBarMenu(false);
                            }
                            ProfileStoriesView profileStoriesView = this.storyView;
                            if (profileStoriesView != null && (chatFull2 = this.chatInfo) != null) {
                                profileStoriesView.setStories(chatFull2.stories);
                            }
                            AvatarImageView avatarImageView = this.avatarImage;
                            if (avatarImageView != null) {
                                avatarImageView.setHasStories(needInsetForStories());
                                return;
                            }
                            return;
                        }
                        return;
                    }
                    return;
                }
                if (i == NotificationCenter.chatInfoDidLoad) {
                    TLRPC.ChatFull chatFull6 = (TLRPC.ChatFull) objArr[0];
                    if (chatFull6.id == this.chatId) {
                        boolean booleanValue = ((Boolean) objArr[2]).booleanValue();
                        TLRPC.ChatFull chatFull7 = this.chatInfo;
                        if ((chatFull7 instanceof TLRPC.TL_channelFull) && chatFull6.participants == null) {
                            chatFull6.participants = chatFull7.participants;
                        }
                        if (chatFull7 == null && (chatFull6 instanceof TLRPC.TL_channelFull)) {
                            c = 1;
                        }
                        this.chatInfo = chatFull6;
                        if (this.mergeDialogId == 0) {
                            long j = chatFull6.migrated_from_chat_id;
                            if (j != 0) {
                                this.mergeDialogId = -j;
                                getMediaDataController().getMediaCount(this.mergeDialogId, this.topicId, 0, this.classGuid, true);
                            }
                        }
                        fetchUsersFromChannelInfo();
                        ProfileGalleryView profileGalleryView = this.avatarsViewPager;
                        if (profileGalleryView != null && !this.isTopic) {
                            profileGalleryView.setChatInfo(this.chatInfo);
                        }
                        updateListAnimated(true);
                        TLRPC.Chat chat3 = getMessagesController().getChat(Long.valueOf(this.chatId));
                        if (chat3 != null) {
                            this.currentChat = chat3;
                            createActionBarMenu(true);
                        }
                        FlagSecureReason flagSecureReason2 = this.flagSecure;
                        if (flagSecureReason2 != null) {
                            flagSecureReason2.invalidate();
                        }
                        if (this.currentChat.megagroup && (c != 0 || !booleanValue)) {
                            getChannelParticipants(true);
                        }
                        updateAutoDeleteItem();
                        updateTtlIcon();
                        ProfileStoriesView profileStoriesView2 = this.storyView;
                        if (profileStoriesView2 != null && (chatFull = this.chatInfo) != null) {
                            profileStoriesView2.setStories(chatFull.stories);
                        }
                        AvatarImageView avatarImageView2 = this.avatarImage;
                        if (avatarImageView2 != null) {
                            avatarImageView2.setHasStories(needInsetForStories());
                        }
                        SharedMediaLayout sharedMediaLayout2 = this.sharedMediaLayout;
                        if (sharedMediaLayout2 != null) {
                            sharedMediaLayout2.setChatInfo(this.chatInfo);
                            return;
                        }
                        return;
                    }
                    return;
                }
                if (i == NotificationCenter.closeChats) {
                    removeSelfFromStack(true);
                    return;
                }
                if (i == NotificationCenter.botInfoDidLoad) {
                    TL_bots.BotInfo botInfo = (TL_bots.BotInfo) objArr[0];
                    if (botInfo.user_id != this.userId) {
                        return;
                    } else {
                        this.botInfo = botInfo;
                    }
                } else {
                    if (i == NotificationCenter.userInfoDidLoad) {
                        if (((Long) objArr[0]).longValue() == this.userId) {
                            TLRPC.UserFull userFull = (TLRPC.UserFull) objArr[1];
                            this.userInfo = userFull;
                            ProfileStoriesView profileStoriesView3 = this.storyView;
                            if (profileStoriesView3 != null) {
                                profileStoriesView3.setStories(userFull.stories);
                            }
                            AvatarImageView avatarImageView3 = this.avatarImage;
                            if (avatarImageView3 != null) {
                                avatarImageView3.setHasStories(needInsetForStories());
                            }
                            SharedMediaLayout sharedMediaLayout3 = this.sharedMediaLayout;
                            if (sharedMediaLayout3 != null) {
                                sharedMediaLayout3.setUserInfo(this.userInfo);
                            }
                            if (this.imageUpdater == null) {
                                if (this.openAnimationInProgress || this.callItemVisible) {
                                    this.recreateMenuAfterAnimation = true;
                                } else {
                                    createActionBarMenu(true);
                                }
                                updateListAnimated(false);
                                SharedMediaLayout sharedMediaLayout4 = this.sharedMediaLayout;
                                if (sharedMediaLayout4 != null) {
                                    sharedMediaLayout4.setCommonGroupsCount(this.userInfo.common_chats_count);
                                    updateSelectedMediaTabText();
                                    SharedMediaLayout.SharedMediaPreloader sharedMediaPreloader = this.sharedMediaPreloader;
                                    if (sharedMediaPreloader == null || sharedMediaPreloader.isMediaWasLoaded()) {
                                        resumeDelayedFragmentAnimation();
                                        needLayout(true);
                                    }
                                }
                            } else if (this.listAdapter != null && !TextUtils.equals(this.userInfo.about, this.currentBio)) {
                                this.listAdapter.notifyItemChanged(this.bioRow);
                            }
                            updateAutoDeleteItem();
                            updateTtlIcon();
                            if (this.profileChannelMessageFetcher == null && !isSettings()) {
                                ProfileChannelCell.ChannelMessageFetcher channelMessageFetcher = new ProfileChannelCell.ChannelMessageFetcher(this.currentAccount);
                                this.profileChannelMessageFetcher = channelMessageFetcher;
                                channelMessageFetcher.subscribe(new Runnable() {
                                    @Override
                                    public final void run() {
                                        ProfileActivity.this.lambda$didReceivedNotification$71();
                                    }
                                });
                                this.profileChannelMessageFetcher.fetch(this.userInfo);
                            }
                            if (!isSettings()) {
                                ProfileBirthdayEffect.BirthdayEffectFetcher birthdayEffectFetcher = this.birthdayFetcher;
                                ProfileBirthdayEffect.BirthdayEffectFetcher of = ProfileBirthdayEffect.BirthdayEffectFetcher.of(this.currentAccount, this.userInfo, birthdayEffectFetcher);
                                this.birthdayFetcher = of;
                                this.createdBirthdayFetcher = of != birthdayEffectFetcher;
                                if (of != null) {
                                    of.subscribe(new ProfileActivity$$ExternalSyntheticLambda2(this));
                                }
                            }
                            if (this.otherItem != null) {
                                if (hasPrivacyCommand()) {
                                    this.otherItem.showSubItem(44);
                                    return;
                                } else {
                                    this.otherItem.hideSubItem(44);
                                    return;
                                }
                            }
                            return;
                        }
                        return;
                    }
                    if (i == NotificationCenter.privacyRulesUpdated) {
                        if (this.qrItem != null) {
                            updateQrItemVisibility(true);
                            return;
                        }
                        return;
                    }
                    if (i == NotificationCenter.didReceiveNewMessages) {
                        if (((Boolean) objArr[2]).booleanValue()) {
                            return;
                        }
                        long dialogId = getDialogId();
                        if (dialogId == ((Long) objArr[0]).longValue()) {
                            DialogObject.isEncryptedDialog(dialogId);
                            ArrayList arrayList = (ArrayList) objArr[1];
                            for (int i4 = 0; i4 < arrayList.size(); i4++) {
                                MessageObject messageObject = (MessageObject) arrayList.get(i4);
                                if (this.currentEncryptedChat != null) {
                                    TLRPC.MessageAction messageAction = messageObject.messageOwner.action;
                                    if ((messageAction instanceof TLRPC.TL_messageEncryptedAction) && (messageAction.encryptedAction instanceof TLRPC.TL_decryptedMessageActionSetMessageTTL) && (listAdapter = this.listAdapter) != null) {
                                        listAdapter.notifyDataSetChanged();
                                    }
                                }
                            }
                            return;
                        }
                        return;
                    }
                    if (i == NotificationCenter.emojiLoaded) {
                        RecyclerListView recyclerListView3 = this.listView;
                        if (recyclerListView3 != null) {
                            recyclerListView3.invalidateViews();
                            return;
                        }
                        return;
                    }
                    if (i != NotificationCenter.reloadInterface) {
                        if (i == NotificationCenter.newSuggestionsAvailable) {
                            int i5 = this.passwordSuggestionRow;
                            int i6 = this.phoneSuggestionRow;
                            int i7 = this.graceSuggestionRow;
                            updateRowsIds();
                            if (i5 == this.passwordSuggestionRow && i6 == this.phoneSuggestionRow && i7 == this.graceSuggestionRow) {
                                return;
                            }
                            this.listAdapter.notifyDataSetChanged();
                            return;
                        }
                        if (i != NotificationCenter.topicsDidLoaded) {
                            if (i == NotificationCenter.updateSearchSettings) {
                                SearchAdapter searchAdapter = this.searchAdapter;
                                if (searchAdapter != null) {
                                    searchAdapter.searchArray = searchAdapter.onCreateSearchArray();
                                    this.searchAdapter.recentSearches.clear();
                                    this.searchAdapter.updateSearchArray();
                                    SearchAdapter searchAdapter2 = this.searchAdapter;
                                    searchAdapter2.search(searchAdapter2.lastSearchString);
                                    return;
                                }
                                return;
                            }
                            if (i != NotificationCenter.reloadDialogPhotos) {
                                if (i == NotificationCenter.storiesUpdated || i == NotificationCenter.storiesReadUpdated) {
                                    AvatarImageView avatarImageView4 = this.avatarImage;
                                    if (avatarImageView4 != null) {
                                        avatarImageView4.setHasStories(needInsetForStories());
                                        updateAvatarRoundRadius();
                                    }
                                    ProfileStoriesView profileStoriesView4 = this.storyView;
                                    if (profileStoriesView4 != null) {
                                        TLRPC.UserFull userFull2 = this.userInfo;
                                        if (userFull2 != null) {
                                            peerStories = userFull2.stories;
                                        } else {
                                            TLRPC.ChatFull chatFull8 = this.chatInfo;
                                            if (chatFull8 == null) {
                                                return;
                                            } else {
                                                peerStories = chatFull8.stories;
                                            }
                                        }
                                        profileStoriesView4.setStories(peerStories);
                                        return;
                                    }
                                    return;
                                }
                                if (i == NotificationCenter.userIsPremiumBlockedUpadted) {
                                    ActionBarMenuItem actionBarMenuItem = this.otherItem;
                                    if (actionBarMenuItem != null) {
                                        actionBarMenuItem.setSubItemShown(20, !getMessagesController().isUserPremiumBlocked(this.userId));
                                    }
                                } else if (i != NotificationCenter.currentUserPremiumStatusChanged) {
                                    if (i != NotificationCenter.starBalanceUpdated && i != NotificationCenter.botStarsUpdated && i != NotificationCenter.botStarsTransactionsLoaded) {
                                        if (i == NotificationCenter.dialogDeleted) {
                                            if (getDialogId() == ((Long) objArr[0]).longValue()) {
                                                INavigationLayout iNavigationLayout = this.parentLayout;
                                                if (iNavigationLayout == null || iNavigationLayout.getLastFragment() != this) {
                                                    removeSelfFromStack();
                                                    return;
                                                } else {
                                                    lambda$onBackPressed$321();
                                                    return;
                                                }
                                            }
                                            return;
                                        }
                                        return;
                                    }
                                }
                                updateEditColorIcon();
                                return;
                            }
                        } else if (!this.isTopic) {
                            return;
                        }
                    }
                }
            }
            updateListAnimated(false);
            return;
        }
        Long l2 = (Long) objArr[0];
        if (this.chatInfo == null || (chat = this.currentChat) == null || chat.id != l2.longValue()) {
            return;
        }
        this.chatInfo.online_count = ((Integer) objArr[1]).intValue();
        updateOnlineCount(true);
        updateProfileData(false);
    }

    @Override
    public boolean didSelectDialogs(DialogsActivity dialogsActivity, ArrayList arrayList, CharSequence charSequence, boolean z, boolean z2, int i, TopicsFragment topicsFragment) {
        long j = ((MessagesStorage.TopicKey) arrayList.get(0)).dialogId;
        Bundle bundle = new Bundle();
        bundle.putBoolean("scrollToTopOnResume", true);
        if (DialogObject.isEncryptedDialog(j)) {
            bundle.putInt("enc_id", DialogObject.getEncryptedChatId(j));
        } else if (DialogObject.isUserDialog(j)) {
            bundle.putLong("user_id", j);
        } else if (DialogObject.isChatDialog(j)) {
            bundle.putLong("chat_id", -j);
        }
        if (!getMessagesController().checkCanOpenChat(bundle, dialogsActivity)) {
            return false;
        }
        NotificationCenter notificationCenter = getNotificationCenter();
        int i2 = NotificationCenter.closeChats;
        notificationCenter.removeObserver(this, i2);
        getNotificationCenter().lambda$postNotificationNameOnUIThread$1(i2, new Object[0]);
        presentFragment(new ChatActivity(bundle), true);
        removeSelfFromStack();
        getSendMessagesHelper().sendMessage(SendMessagesHelper.SendMessageParams.of(getMessagesController().getUser(Long.valueOf(this.userId)), j, (MessageObject) null, (MessageObject) null, (TLRPC.ReplyMarkup) null, (HashMap<String, String>) null, z2, i));
        if (!TextUtils.isEmpty(charSequence)) {
            SendMessagesHelper.prepareSendingText(AccountInstance.getInstance(this.currentAccount), charSequence.toString(), j, z2, i, 0L);
        }
        return true;
    }

    @Override
    public void didStartUpload(boolean z) {
        RadialProgressView radialProgressView = this.avatarProgressView;
        if (radialProgressView == null) {
            return;
        }
        radialProgressView.setProgress(0.0f);
    }

    @Override
    public void didUploadFailed() {
        ImageUpdater.ImageUpdaterDelegate.CC.$default$didUploadFailed(this);
    }

    @Override
    public void didUploadPhoto(final TLRPC.InputFile inputFile, final TLRPC.InputFile inputFile2, final double d, final String str, final TLRPC.PhotoSize photoSize, final TLRPC.PhotoSize photoSize2, boolean z, final TLRPC.VideoSize videoSize) {
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                ProfileActivity.this.lambda$didUploadPhoto$85(inputFile, inputFile2, videoSize, d, str, photoSize2, photoSize);
            }
        });
    }

    @Override
    public void dismissCurrentDialog() {
        ImageUpdater imageUpdater = this.imageUpdater;
        if (imageUpdater == null || !imageUpdater.dismissCurrentDialog(this.visibleDialog)) {
            super.dismissCurrentDialog();
        }
    }

    @Override
    public boolean dismissDialogOnPause(Dialog dialog) {
        ImageUpdater imageUpdater = this.imageUpdater;
        return (imageUpdater == null || imageUpdater.dismissDialogOnPause(dialog)) && super.dismissDialogOnPause(dialog);
    }

    @Override
    public TLRPC.Chat getCurrentChat() {
        return this.currentChat;
    }

    public long getDialogId() {
        long j = this.dialogId;
        if (j != 0) {
            return j;
        }
        long j2 = this.userId;
        return j2 != 0 ? j2 : -this.chatId;
    }

    public void getEmojiStatusLocation(Rect rect) {
        SimpleTextView simpleTextView = this.nameTextView[1];
        if (simpleTextView == null) {
            return;
        }
        if (simpleTextView.getRightDrawable() == null) {
            rect.set(this.nameTextView[1].getWidth() - 1, (this.nameTextView[1].getHeight() / 2) - 1, this.nameTextView[1].getWidth() + 1, (this.nameTextView[1].getHeight() / 2) + 1);
            return;
        }
        rect.set(this.nameTextView[1].getRightDrawable().getBounds());
        rect.offset((int) (rect.centerX() * (this.nameTextView[1].getScaleX() - 1.0f)), 0);
        rect.offset((int) this.nameTextView[1].getX(), (int) this.nameTextView[1].getY());
    }

    @Override
    public String getInitialSearchString() {
        return ImageUpdater.ImageUpdaterDelegate.CC.$default$getInitialSearchString(this);
    }

    @Override
    public RecyclerListView getListView() {
        return this.listView;
    }

    @Override
    public Theme.ResourcesProvider getResourceProvider() {
        return this.resourcesProvider;
    }

    public ShowDrawable getShowStatusButton() {
        if (this.showStatusButton == null) {
            ShowDrawable showDrawable = new ShowDrawable(LocaleController.getString(R.string.StatusHiddenShow));
            this.showStatusButton = showDrawable;
            showDrawable.setAlpha((int) (Math.min(1.0f, this.extraHeight / AndroidUtilities.dp(88.0f)) * 255.0f));
            this.showStatusButton.setBackgroundColor(ColorUtils.blendARGB(Theme.multAlpha(Theme.adaptHSV(this.actionBarBackgroundColor, 0.18f, -0.1f), 0.5f), 603979775, this.currentExpandAnimatorValue));
        }
        return this.showStatusButton;
    }

    @Override
    public ArrayList getThemeDescriptions() {
        ThemeDescription themeDescription;
        if (this.resourcesProvider != null) {
            return null;
        }
        ThemeDescription.ThemeDescriptionDelegate themeDescriptionDelegate = new ThemeDescription.ThemeDescriptionDelegate() {
            @Override
            public final void didSetColor() {
                ProfileActivity.this.lambda$getThemeDescriptions$89();
            }

            @Override
            public void onAnimationProgress(float f) {
                ThemeDescription.ThemeDescriptionDelegate.CC.$default$onAnimationProgress(this, f);
            }
        };
        ArrayList arrayList = new ArrayList();
        SharedMediaLayout sharedMediaLayout = this.sharedMediaLayout;
        if (sharedMediaLayout != null) {
            arrayList.addAll(sharedMediaLayout.getThemeDescriptions());
        }
        RecyclerListView recyclerListView = this.listView;
        int i = Theme.key_windowBackgroundWhite;
        arrayList.add(new ThemeDescription(recyclerListView, 0, null, null, null, null, i));
        arrayList.add(new ThemeDescription(this.searchListView, 0, null, null, null, null, i));
        arrayList.add(new ThemeDescription(this.listView, 0, null, null, null, null, Theme.key_windowBackgroundGray));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SUBMENUBACKGROUND, null, null, null, null, Theme.key_actionBarDefaultSubmenuBackground));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SUBMENUITEM, null, null, null, null, Theme.key_actionBarDefaultSubmenuItem));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SUBMENUITEM | ThemeDescription.FLAG_IMAGECOLOR, null, null, null, null, Theme.key_actionBarDefaultSubmenuItemIcon));
        arrayList.add(new ThemeDescription(null, 0, null, null, null, themeDescriptionDelegate, Theme.key_actionBarDefaultIcon));
        arrayList.add(new ThemeDescription(null, 0, null, null, null, themeDescriptionDelegate, Theme.key_avatar_actionBarSelectorBlue));
        arrayList.add(new ThemeDescription(null, 0, null, null, null, themeDescriptionDelegate, Theme.key_chat_lockIcon));
        int i2 = Theme.key_avatar_subtitleInProfileBlue;
        arrayList.add(new ThemeDescription(null, 0, null, null, null, themeDescriptionDelegate, i2));
        int i3 = Theme.key_avatar_backgroundActionBarBlue;
        arrayList.add(new ThemeDescription(null, 0, null, null, null, themeDescriptionDelegate, i3));
        arrayList.add(new ThemeDescription(null, 0, null, null, null, themeDescriptionDelegate, Theme.key_profile_title));
        arrayList.add(new ThemeDescription(null, 0, null, null, null, themeDescriptionDelegate, Theme.key_profile_status));
        arrayList.add(new ThemeDescription(null, 0, null, null, null, themeDescriptionDelegate, i2));
        if (this.mediaCounterTextView != null) {
            TextView textView = this.mediaCounterTextView.getTextView();
            int i4 = ThemeDescription.FLAG_TEXTCOLOR;
            int i5 = Theme.key_player_actionBarSubtitle;
            arrayList.add(new ThemeDescription(textView, i4, null, null, null, themeDescriptionDelegate, i5));
            arrayList.add(new ThemeDescription(this.mediaCounterTextView.getNextTextView(), ThemeDescription.FLAG_TEXTCOLOR, null, null, null, themeDescriptionDelegate, i5));
        }
        arrayList.add(new ThemeDescription(this.topView, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, i3));
        arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_SELECTOR, null, null, null, null, Theme.key_listSelector));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{View.class}, Theme.dividerPaint, null, null, Theme.key_divider));
        AvatarImageView avatarImageView = this.avatarImage;
        Drawable[] drawableArr = Theme.avatarDrawables;
        int i6 = Theme.key_avatar_text;
        arrayList.add(new ThemeDescription(avatarImageView, 0, null, null, drawableArr, null, i6));
        arrayList.add(new ThemeDescription(this.avatarImage, 0, null, null, new Drawable[]{this.avatarDrawable}, null, Theme.key_avatar_backgroundInProfileBlue));
        arrayList.add(new ThemeDescription(this.writeButton, ThemeDescription.FLAG_IMAGECOLOR, null, null, null, null, Theme.key_profile_actionIcon));
        arrayList.add(new ThemeDescription(this.writeButton, ThemeDescription.FLAG_BACKGROUNDFILTER, null, null, null, null, Theme.key_profile_actionBackground));
        arrayList.add(new ThemeDescription(this.writeButton, ThemeDescription.FLAG_BACKGROUNDFILTER | ThemeDescription.FLAG_DRAWABLESELECTEDSTATE, null, null, null, null, Theme.key_profile_actionPressedBackground));
        int i7 = Theme.key_windowBackgroundWhiteBlackText;
        arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_CHECKTAG, new Class[]{TextCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, i7));
        arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_CHECKTAG, new Class[]{TextCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteGreenText2));
        arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_CHECKTAG, new Class[]{TextCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_text_RedRegular));
        arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_CHECKTAG, new Class[]{TextCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteBlueText2));
        arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_CHECKTAG, new Class[]{TextCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteBlueButton));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{TextCell.class}, new String[]{"valueTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteValueText));
        int i8 = Theme.key_windowBackgroundWhiteGrayIcon;
        arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_CHECKTAG, new Class[]{TextCell.class}, new String[]{"imageView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, i8));
        arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_CHECKTAG, new Class[]{TextCell.class}, new String[]{"imageView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteBlueIcon));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{TextDetailCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, i7));
        int i9 = Theme.key_windowBackgroundWhiteGrayText2;
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{TextDetailCell.class}, new String[]{"valueTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, i9));
        int i10 = Theme.key_windowBackgroundWhiteBlueHeader;
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{HeaderCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, i10));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{SettingsSuggestionCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, i10));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{SettingsSuggestionCell.class}, new String[]{"detailTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, i9));
        int i11 = Theme.key_windowBackgroundWhiteLinkText;
        arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_LINKCOLOR, new Class[]{SettingsSuggestionCell.class}, new String[]{"detailTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, i11));
        int i12 = Theme.key_featuredStickers_buttonText;
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{SettingsSuggestionCell.class}, new String[]{"yesButton"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, i12));
        int i13 = Theme.key_featuredStickers_addButton;
        arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_USEBACKGROUNDDRAWABLE, new Class[]{SettingsSuggestionCell.class}, new String[]{"yesButton"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, i13));
        int i14 = Theme.key_featuredStickers_addButtonPressed;
        arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_USEBACKGROUNDDRAWABLE | ThemeDescription.FLAG_DRAWABLESELECTEDSTATE, new Class[]{SettingsSuggestionCell.class}, new String[]{"yesButton"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, i14));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{SettingsSuggestionCell.class}, new String[]{"noButton"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, i12));
        arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_USEBACKGROUNDDRAWABLE, new Class[]{SettingsSuggestionCell.class}, new String[]{"noButton"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, i13));
        arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_USEBACKGROUNDDRAWABLE | ThemeDescription.FLAG_DRAWABLESELECTEDSTATE, new Class[]{SettingsSuggestionCell.class}, new String[]{"noButton"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, i14));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{NotificationsCheckCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, i7));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{NotificationsCheckCell.class}, new String[]{"valueTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, i9));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{NotificationsCheckCell.class}, new String[]{"checkBox"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_switchTrack));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{NotificationsCheckCell.class}, new String[]{"checkBox"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_switchTrackChecked));
        arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{UserCell.class}, new String[]{"adminTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_profile_creatorIcon));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{UserCell.class}, new String[]{"imageView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, i8));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{UserCell.class}, new String[]{"nameTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, i7));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{UserCell.class}, new String[]{"statusColor"}, (Paint[]) null, (Drawable[]) null, themeDescriptionDelegate, Theme.key_windowBackgroundWhiteGrayText));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{UserCell.class}, new String[]{"statusOnlineColor"}, (Paint[]) null, (Drawable[]) null, themeDescriptionDelegate, Theme.key_windowBackgroundWhiteBlueText));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{UserCell.class}, null, Theme.avatarDrawables, null, i6));
        arrayList.add(new ThemeDescription(null, 0, null, null, null, themeDescriptionDelegate, Theme.key_avatar_backgroundRed));
        arrayList.add(new ThemeDescription(null, 0, null, null, null, themeDescriptionDelegate, Theme.key_avatar_backgroundOrange));
        arrayList.add(new ThemeDescription(null, 0, null, null, null, themeDescriptionDelegate, Theme.key_avatar_backgroundViolet));
        arrayList.add(new ThemeDescription(null, 0, null, null, null, themeDescriptionDelegate, Theme.key_avatar_backgroundGreen));
        arrayList.add(new ThemeDescription(null, 0, null, null, null, themeDescriptionDelegate, Theme.key_avatar_backgroundCyan));
        arrayList.add(new ThemeDescription(null, 0, null, null, null, themeDescriptionDelegate, Theme.key_avatar_backgroundBlue));
        arrayList.add(new ThemeDescription(null, 0, null, null, null, themeDescriptionDelegate, Theme.key_avatar_backgroundPink));
        arrayList.add(new ThemeDescription(this.undoView, ThemeDescription.FLAG_BACKGROUNDFILTER, null, null, null, null, Theme.key_undo_background));
        int i15 = Theme.key_undo_cancelColor;
        arrayList.add(new ThemeDescription(this.undoView, 0, new Class[]{UndoView.class}, new String[]{"undoImageView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, i15));
        arrayList.add(new ThemeDescription(this.undoView, 0, new Class[]{UndoView.class}, new String[]{"undoTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, i15));
        int i16 = Theme.key_undo_infoColor;
        arrayList.add(new ThemeDescription(this.undoView, 0, new Class[]{UndoView.class}, new String[]{"infoTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, i16));
        arrayList.add(new ThemeDescription(this.undoView, 0, new Class[]{UndoView.class}, new String[]{"textPaint"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, i16));
        arrayList.add(new ThemeDescription(this.undoView, 0, new Class[]{UndoView.class}, new String[]{"progressPaint"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, i16));
        arrayList.add(new ThemeDescription(this.undoView, ThemeDescription.FLAG_IMAGECOLOR, new Class[]{UndoView.class}, new String[]{"leftImageView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, i16));
        arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{AboutLinkCell.class}, Theme.profile_aboutTextPaint, null, null, i7));
        arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_LINKCOLOR, new Class[]{AboutLinkCell.class}, Theme.profile_aboutTextPaint, null, null, i11));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{AboutLinkCell.class}, Theme.linkSelectionPaint, null, null, Theme.key_windowBackgroundWhiteLinkSelection));
        int i17 = Theme.key_windowBackgroundGrayShadow;
        arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{ShadowSectionCell.class}, null, null, null, i17));
        arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{TextInfoPrivacyCell.class}, null, null, null, i17));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{TextInfoPrivacyCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteGrayText4));
        arrayList.add(new ThemeDescription(this.searchListView, 0, new Class[]{HeaderCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, i10));
        arrayList.add(new ThemeDescription(this.searchListView, 0, new Class[]{GraySectionCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_graySectionText));
        arrayList.add(new ThemeDescription(this.searchListView, ThemeDescription.FLAG_CELLBACKGROUNDCOLOR, new Class[]{GraySectionCell.class}, null, null, null, Theme.key_graySection));
        arrayList.add(new ThemeDescription(this.searchListView, 0, new Class[]{SettingsSearchCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, i7));
        arrayList.add(new ThemeDescription(this.searchListView, 0, new Class[]{SettingsSearchCell.class}, new String[]{"valueTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, i9));
        arrayList.add(new ThemeDescription(this.searchListView, 0, new Class[]{SettingsSearchCell.class}, new String[]{"imageView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, i8));
        if (this.mediaHeaderVisible) {
            arrayList.add(new ThemeDescription(this.nameTextView[1], 0, null, null, this.verifiedCheckDrawable, null, Theme.key_player_actionBarTitle));
            themeDescription = new ThemeDescription(this.nameTextView[1], 0, null, null, this.verifiedDrawable, null, i);
        } else {
            arrayList.add(new ThemeDescription(this.nameTextView[1], 0, null, null, this.verifiedCheckDrawable, null, Theme.key_profile_verifiedCheck));
            themeDescription = new ThemeDescription(this.nameTextView[1], 0, null, null, this.verifiedDrawable, null, Theme.key_profile_verifiedBackground);
        }
        arrayList.add(themeDescription);
        return arrayList;
    }

    @Override
    public int getThemedColor(int i) {
        return Theme.getColor(i, this.resourcesProvider);
    }

    @Override
    public Drawable getThemedDrawable(String str) {
        Theme.ResourcesProvider resourcesProvider = this.resourcesProvider;
        Drawable drawable = resourcesProvider != null ? resourcesProvider.getDrawable(str) : null;
        return drawable != null ? drawable : super.getThemedDrawable(str);
    }

    public long getTopicId() {
        return this.topicId;
    }

    public UndoView getUndoView() {
        return this.undoView;
    }

    public TLRPC.UserFull getUserInfo() {
        return this.userInfo;
    }

    public void goToForum() {
        if (getParentLayout() != null && getParentLayout().getFragmentStack() != null) {
            int i = 0;
            while (i < getParentLayout().getFragmentStack().size()) {
                BaseFragment baseFragment = (BaseFragment) getParentLayout().getFragmentStack().get(i);
                if (baseFragment instanceof DialogsActivity) {
                    DialogsActivity dialogsActivity = (DialogsActivity) baseFragment;
                    RightSlidingDialogContainer rightSlidingDialogContainer = dialogsActivity.rightSlidingDialogContainer;
                    if (rightSlidingDialogContainer != null) {
                        BaseFragment fragment = rightSlidingDialogContainer.getFragment();
                        if ((fragment instanceof TopicsFragment) && ((TopicsFragment) fragment).getDialogId() == getDialogId()) {
                            dialogsActivity.rightSlidingDialogContainer.lambda$presentFragment$1();
                        }
                    }
                } else if (baseFragment instanceof ChatActivity) {
                    if (((ChatActivity) baseFragment).getDialogId() != getDialogId()) {
                    }
                    getParentLayout().removeFragmentFromStack(baseFragment);
                    i--;
                } else if (baseFragment instanceof TopicsFragment) {
                    if (((TopicsFragment) baseFragment).getDialogId() != getDialogId()) {
                    }
                    getParentLayout().removeFragmentFromStack(baseFragment);
                    i--;
                } else if ((baseFragment instanceof ProfileActivity) && baseFragment != this) {
                    ProfileActivity profileActivity = (ProfileActivity) baseFragment;
                    if (profileActivity.getDialogId() == getDialogId()) {
                        if (!profileActivity.isTopic) {
                        }
                        getParentLayout().removeFragmentFromStack(baseFragment);
                        i--;
                    }
                }
                i++;
            }
        }
        this.playProfileAnimation = 0;
        Bundle bundle = new Bundle();
        bundle.putLong("chat_id", this.chatId);
        presentFragment(TopicsFragment.getTopicsOrChat(this, bundle));
    }

    public boolean hasPrivacyCommand() {
        TLRPC.UserFull userFull;
        TL_bots.BotInfo botInfo;
        if (!this.isBot || (userFull = this.userInfo) == null || (botInfo = userFull.bot_info) == null) {
            return false;
        }
        if (botInfo.privacy_policy_url != null) {
            return true;
        }
        Iterator<TLRPC.TL_botCommand> it = botInfo.commands.iterator();
        while (it.hasNext() && !"privacy".equals(it.next().command)) {
        }
        return true;
    }

    public boolean isChat() {
        return this.chatId != 0;
    }

    @Override
    public boolean isFragmentOpened() {
        return this.isFragmentOpened;
    }

    @Override
    public boolean isLightStatusBar() {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.ProfileActivity.isLightStatusBar():boolean");
    }

    public boolean isSettings() {
        return (this.imageUpdater == null || this.myProfile) ? false : true;
    }

    @Override
    public boolean isSwipeBackEnabled(MotionEvent motionEvent) {
        SharedMediaLayout sharedMediaLayout;
        ProfileGalleryView profileGalleryView = this.avatarsViewPager;
        if (profileGalleryView != null && profileGalleryView.getVisibility() == 0 && this.avatarsViewPager.getRealCount() > 1) {
            this.avatarsViewPager.getHitRect(this.rect);
            if (motionEvent != null && this.rect.contains((int) motionEvent.getX(), ((int) motionEvent.getY()) - this.actionBar.getMeasuredHeight())) {
                return false;
            }
        }
        if (this.sharedMediaRow == -1 || (sharedMediaLayout = this.sharedMediaLayout) == null) {
            return true;
        }
        if (!sharedMediaLayout.isSwipeBackEnabled()) {
            return false;
        }
        this.sharedMediaLayout.getHitRect(this.rect);
        if (motionEvent == null || this.rect.contains((int) motionEvent.getX(), ((int) motionEvent.getY()) - this.actionBar.getMeasuredHeight())) {
            return this.sharedMediaLayout.isCurrentTabFirst();
        }
        return true;
    }

    @Override
    public void mediaCountUpdated() {
        SharedMediaLayout.SharedMediaPreloader sharedMediaPreloader;
        SharedMediaLayout sharedMediaLayout = this.sharedMediaLayout;
        if (sharedMediaLayout != null && (sharedMediaPreloader = this.sharedMediaPreloader) != null) {
            sharedMediaLayout.setNewMediaCounts(sharedMediaPreloader.getLastMediaCount());
        }
        updateSharedMediaRows();
        updateSelectedMediaTabText();
        if (this.userInfo != null) {
            resumeDelayedFragmentAnimation();
        }
    }

    @Override
    public boolean needDelayOpenAnimation() {
        return this.playProfileAnimation == 0;
    }

    @Override
    public void onActivityResultFragment(int i, int i2, Intent intent) {
        ImageUpdater imageUpdater = this.imageUpdater;
        if (imageUpdater != null) {
            imageUpdater.onActivityResult(i, i2, intent);
        }
    }

    @Override
    public boolean onBackPressed() {
        SharedMediaLayout sharedMediaLayout;
        if (!closeSheet() && this.actionBar.isEnabled()) {
            return this.sharedMediaRow == -1 || (sharedMediaLayout = this.sharedMediaLayout) == null || !sharedMediaLayout.closeActionMode();
        }
        return false;
    }

    @Override
    public void onBecomeFullyHidden() {
        UndoView undoView = this.undoView;
        if (undoView != null) {
            undoView.hide(true, 0);
        }
        super.onBecomeFullyHidden();
        this.fullyVisible = false;
    }

    @Override
    public void onBecomeFullyVisible() {
        super.onBecomeFullyVisible();
        writeButtonSetBackground();
        this.fullyVisible = true;
        createBirthdayEffect();
    }

    @Override
    public void onConfigurationChanged(Configuration configuration) {
        View findViewByPosition;
        super.onConfigurationChanged(configuration);
        SharedMediaLayout sharedMediaLayout = this.sharedMediaLayout;
        if (sharedMediaLayout != null) {
            sharedMediaLayout.onConfigurationChanged(configuration);
        }
        invalidateIsInLandscapeMode();
        if (this.isInLandscapeMode && this.isPulledDown && (findViewByPosition = this.layoutManager.findViewByPosition(0)) != null) {
            this.listView.scrollBy(0, findViewByPosition.getTop() - AndroidUtilities.dp(88.0f));
        }
        fixLayout();
    }

    @Override
    public android.animation.AnimatorSet onCustomTransitionAnimation(boolean r17, java.lang.Runnable r18) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.ProfileActivity.onCustomTransitionAnimation(boolean, java.lang.Runnable):android.animation.AnimatorSet");
    }

    @Override
    public void onDialogDismiss(Dialog dialog) {
        RecyclerListView recyclerListView = this.listView;
        if (recyclerListView != null) {
            recyclerListView.invalidateViews();
        }
    }

    @Override
    public boolean onFragmentCreate() {
        this.userId = this.arguments.getLong("user_id", 0L);
        this.chatId = this.arguments.getLong("chat_id", 0L);
        this.topicId = this.arguments.getLong("topic_id", 0L);
        this.saved = this.arguments.getBoolean("saved", false);
        this.openSimilar = this.arguments.getBoolean("similar", false);
        this.isTopic = this.topicId != 0;
        this.banFromGroup = this.arguments.getLong("ban_chat_id", 0L);
        this.reportReactionMessageId = this.arguments.getInt("report_reaction_message_id", 0);
        this.reportReactionFromDialogId = this.arguments.getLong("report_reaction_from_dialog_id", 0L);
        this.showAddToContacts = this.arguments.getBoolean("show_add_to_contacts", true);
        this.vcardPhone = PhoneFormat.stripExceptNumbers(this.arguments.getString("vcard_phone"));
        this.vcardFirstName = this.arguments.getString("vcard_first_name");
        this.vcardLastName = this.arguments.getString("vcard_last_name");
        this.reportSpam = this.arguments.getBoolean("reportSpam", false);
        this.myProfile = this.arguments.getBoolean("my_profile", false);
        this.openGifts = this.arguments.getBoolean("open_gifts", false);
        if (!this.expandPhoto) {
            boolean z = this.arguments.getBoolean("expandPhoto", false);
            this.expandPhoto = z;
            if (z) {
                this.currentExpandAnimatorValue = 1.0f;
                this.needSendMessage = true;
            }
        }
        if (this.userId != 0) {
            long j = this.arguments.getLong("dialog_id", 0L);
            this.dialogId = j;
            if (j != 0) {
                this.currentEncryptedChat = getMessagesController().getEncryptedChat(Integer.valueOf(DialogObject.getEncryptedChatId(this.dialogId)));
            }
            FlagSecureReason flagSecureReason = this.flagSecure;
            if (flagSecureReason != null) {
                flagSecureReason.invalidate();
            }
            TLRPC.User user = getMessagesController().getUser(Long.valueOf(this.userId));
            if (user == null) {
                return false;
            }
            getNotificationCenter().addObserver(this, NotificationCenter.contactsDidLoad);
            getNotificationCenter().addObserver(this, NotificationCenter.newSuggestionsAvailable);
            getNotificationCenter().addObserver(this, NotificationCenter.encryptedChatCreated);
            getNotificationCenter().addObserver(this, NotificationCenter.encryptedChatUpdated);
            getNotificationCenter().addObserver(this, NotificationCenter.blockedUsersDidLoad);
            getNotificationCenter().addObserver(this, NotificationCenter.botInfoDidLoad);
            getNotificationCenter().addObserver(this, NotificationCenter.userInfoDidLoad);
            getNotificationCenter().addObserver(this, NotificationCenter.privacyRulesUpdated);
            NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.reloadInterface);
            this.userBlocked = getMessagesController().blockePeers.indexOfKey(this.userId) >= 0;
            if (user.bot) {
                this.isBot = true;
                MediaDataController mediaDataController = getMediaDataController();
                long j2 = user.id;
                mediaDataController.loadBotInfo(j2, j2, true, this.classGuid);
            }
            this.userInfo = getMessagesController().getUserFull(this.userId);
            getMessagesController().loadFullUser(getMessagesController().getUser(Long.valueOf(this.userId)), this.classGuid, true);
            this.participantsMap = null;
            if (UserObject.isUserSelf(user)) {
                ImageUpdater imageUpdater = new ImageUpdater(true, 0, true);
                this.imageUpdater = imageUpdater;
                imageUpdater.setOpenWithFrontfaceCamera(true);
                ImageUpdater imageUpdater2 = this.imageUpdater;
                imageUpdater2.parentFragment = this;
                imageUpdater2.setDelegate(this);
                getMediaDataController().checkFeaturedStickers();
                getMessagesController().loadSuggestedFilters();
                getMessagesController().loadUserInfo(getUserConfig().getCurrentUser(), true, this.classGuid);
            }
            this.actionBarAnimationColorFrom = this.arguments.getInt("actionBarColor", 0);
        } else {
            if (this.chatId == 0) {
                return false;
            }
            TLRPC.Chat chat = getMessagesController().getChat(Long.valueOf(this.chatId));
            this.currentChat = chat;
            if (chat == null) {
                final CountDownLatch countDownLatch = new CountDownLatch(1);
                getMessagesStorage().getStorageQueue().postRunnable(new Runnable() {
                    @Override
                    public final void run() {
                        ProfileActivity.this.lambda$onFragmentCreate$0(countDownLatch);
                    }
                });
                try {
                    countDownLatch.await();
                } catch (Exception e) {
                    FileLog.e(e);
                }
                if (this.currentChat == null) {
                    return false;
                }
                getMessagesController().putChat(this.currentChat, true);
            }
            FlagSecureReason flagSecureReason2 = this.flagSecure;
            if (flagSecureReason2 != null) {
                flagSecureReason2.invalidate();
            }
            if (this.currentChat.megagroup) {
                getChannelParticipants(true);
            } else {
                this.participantsMap = null;
            }
            getNotificationCenter().addObserver(this, NotificationCenter.chatInfoDidLoad);
            getNotificationCenter().addObserver(this, NotificationCenter.chatOnlineCountDidLoad);
            getNotificationCenter().addObserver(this, NotificationCenter.groupCallUpdated);
            getNotificationCenter().addObserver(this, NotificationCenter.channelRightsUpdated);
            getNotificationCenter().addObserver(this, NotificationCenter.chatWasBoostedByUser);
            NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.uploadStoryEnd);
            this.sortedUsers = new ArrayList();
            updateOnlineCount(true);
            if (this.chatInfo == null) {
                this.chatInfo = getMessagesController().getChatFull(this.chatId);
            }
            if (ChatObject.isChannel(this.currentChat)) {
                getMessagesController().loadFullChat(this.chatId, this.classGuid, true);
            } else if (this.chatInfo == null) {
                this.chatInfo = getMessagesStorage().loadChatInfo(this.chatId, false, null, false, false);
            }
            updateExceptions();
        }
        if (this.sharedMediaPreloader == null) {
            this.sharedMediaPreloader = new SharedMediaLayout.SharedMediaPreloader(this);
        }
        this.sharedMediaPreloader.addDelegate(this);
        getNotificationCenter().addObserver(this, NotificationCenter.updateInterfaces);
        getNotificationCenter().addObserver(this, NotificationCenter.didReceiveNewMessages);
        getNotificationCenter().addObserver(this, NotificationCenter.closeChats);
        getNotificationCenter().addObserver(this, NotificationCenter.topicsDidLoaded);
        getNotificationCenter().addObserver(this, NotificationCenter.updateSearchSettings);
        getNotificationCenter().addObserver(this, NotificationCenter.reloadDialogPhotos);
        getNotificationCenter().addObserver(this, NotificationCenter.storiesUpdated);
        getNotificationCenter().addObserver(this, NotificationCenter.storiesReadUpdated);
        getNotificationCenter().addObserver(this, NotificationCenter.userIsPremiumBlockedUpadted);
        getNotificationCenter().addObserver(this, NotificationCenter.currentUserPremiumStatusChanged);
        getNotificationCenter().addObserver(this, NotificationCenter.starBalanceUpdated);
        getNotificationCenter().addObserver(this, NotificationCenter.botStarsUpdated);
        getNotificationCenter().addObserver(this, NotificationCenter.botStarsTransactionsLoaded);
        getNotificationCenter().addObserver(this, NotificationCenter.dialogDeleted);
        NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.emojiLoaded);
        updateRowsIds();
        ListAdapter listAdapter = this.listAdapter;
        if (listAdapter != null) {
            listAdapter.notifyDataSetChanged();
        }
        if (this.arguments.containsKey("preload_messages")) {
            getMessagesController().ensureMessagesLoaded(this.userId, 0, null);
        }
        if (this.userId != 0 && UserObject.isUserSelf(getMessagesController().getUser(Long.valueOf(this.userId)))) {
            getConnectionsManager().sendRequest(new TL_account.getPassword(), new RequestDelegate() {
                @Override
                public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                    ProfileActivity.this.lambda$onFragmentCreate$1(tLObject, tL_error);
                }
            });
        }
        Bulletin.addDelegate(this, new Bulletin.Delegate() {
            AnonymousClass4() {
            }

            @Override
            public boolean allowLayoutChanges() {
                return Bulletin.Delegate.CC.$default$allowLayoutChanges(this);
            }

            @Override
            public boolean bottomOffsetAnimated() {
                return ProfileActivity.this.bottomButtonsContainer == null;
            }

            @Override
            public boolean clipWithGradient(int i) {
                return Bulletin.Delegate.CC.$default$clipWithGradient(this, i);
            }

            @Override
            public int getBottomOffset(int i) {
                if (ProfileActivity.this.bottomButtonsContainer == null) {
                    return 0;
                }
                return (int) (((AndroidUtilities.dp(72.0f) - ProfileActivity.this.bottomButtonsContainer.getTranslationY()) - (Math.min(1.0f, 1.0f - Math.abs(ProfileActivity.this.sharedMediaLayout.getTabProgress() - 9.0f)) * ProfileActivity.this.bottomButtonContainer[1].getTranslationY())) - (Math.min(1.0f, 1.0f - Math.abs(ProfileActivity.this.sharedMediaLayout.getTabProgress() - 8.0f)) * ProfileActivity.this.bottomButtonContainer[0].getTranslationY()));
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
        if (this.userId != 0 && UserObject.isUserSelf(getMessagesController().getUser(Long.valueOf(this.userId))) && !this.myProfile) {
            getMessagesController().getContentSettings(null);
        }
        return true;
    }

    @Override
    public void onFragmentDestroy() {
        super.onFragmentDestroy();
        SharedMediaLayout sharedMediaLayout = this.sharedMediaLayout;
        if (sharedMediaLayout != null) {
            sharedMediaLayout.onDestroy();
        }
        SharedMediaLayout.SharedMediaPreloader sharedMediaPreloader = this.sharedMediaPreloader;
        if (sharedMediaPreloader != null) {
            sharedMediaPreloader.onDestroy(this);
        }
        SharedMediaLayout.SharedMediaPreloader sharedMediaPreloader2 = this.sharedMediaPreloader;
        if (sharedMediaPreloader2 != null) {
            sharedMediaPreloader2.removeDelegate(this);
        }
        getNotificationCenter().removeObserver(this, NotificationCenter.updateInterfaces);
        getNotificationCenter().removeObserver(this, NotificationCenter.closeChats);
        getNotificationCenter().removeObserver(this, NotificationCenter.didReceiveNewMessages);
        getNotificationCenter().removeObserver(this, NotificationCenter.topicsDidLoaded);
        getNotificationCenter().removeObserver(this, NotificationCenter.updateSearchSettings);
        getNotificationCenter().removeObserver(this, NotificationCenter.reloadDialogPhotos);
        getNotificationCenter().removeObserver(this, NotificationCenter.storiesUpdated);
        getNotificationCenter().removeObserver(this, NotificationCenter.storiesReadUpdated);
        getNotificationCenter().removeObserver(this, NotificationCenter.userIsPremiumBlockedUpadted);
        getNotificationCenter().removeObserver(this, NotificationCenter.currentUserPremiumStatusChanged);
        getNotificationCenter().removeObserver(this, NotificationCenter.starBalanceUpdated);
        getNotificationCenter().removeObserver(this, NotificationCenter.botStarsUpdated);
        getNotificationCenter().removeObserver(this, NotificationCenter.botStarsTransactionsLoaded);
        getNotificationCenter().removeObserver(this, NotificationCenter.dialogDeleted);
        NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.emojiLoaded);
        ProfileGalleryView profileGalleryView = this.avatarsViewPager;
        if (profileGalleryView != null) {
            profileGalleryView.onDestroy();
        }
        if (this.userId != 0) {
            getNotificationCenter().removeObserver(this, NotificationCenter.newSuggestionsAvailable);
            getNotificationCenter().removeObserver(this, NotificationCenter.contactsDidLoad);
            getNotificationCenter().removeObserver(this, NotificationCenter.encryptedChatCreated);
            getNotificationCenter().removeObserver(this, NotificationCenter.encryptedChatUpdated);
            getNotificationCenter().removeObserver(this, NotificationCenter.blockedUsersDidLoad);
            getNotificationCenter().removeObserver(this, NotificationCenter.botInfoDidLoad);
            getNotificationCenter().removeObserver(this, NotificationCenter.userInfoDidLoad);
            getNotificationCenter().removeObserver(this, NotificationCenter.privacyRulesUpdated);
            NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.reloadInterface);
            getMessagesController().cancelLoadFullUser(this.userId);
        } else if (this.chatId != 0) {
            NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.uploadStoryEnd);
            getNotificationCenter().removeObserver(this, NotificationCenter.chatWasBoostedByUser);
            getNotificationCenter().removeObserver(this, NotificationCenter.chatInfoDidLoad);
            getNotificationCenter().removeObserver(this, NotificationCenter.chatOnlineCountDidLoad);
            getNotificationCenter().removeObserver(this, NotificationCenter.groupCallUpdated);
            getNotificationCenter().removeObserver(this, NotificationCenter.channelRightsUpdated);
        }
        AvatarImageView avatarImageView = this.avatarImage;
        if (avatarImageView != null) {
            avatarImageView.setImageDrawable(null);
        }
        ImageUpdater imageUpdater = this.imageUpdater;
        if (imageUpdater != null) {
            imageUpdater.clear();
        }
        PinchToZoomHelper pinchToZoomHelper = this.pinchToZoomHelper;
        if (pinchToZoomHelper != null) {
            pinchToZoomHelper.clear();
        }
        ProfileBirthdayEffect.BirthdayEffectFetcher birthdayEffectFetcher = this.birthdayFetcher;
        if (birthdayEffectFetcher != null && this.createdBirthdayFetcher) {
            birthdayEffectFetcher.detach(true);
            this.birthdayFetcher = null;
        }
        Runnable runnable = this.applyBulletin;
        if (runnable != null) {
            this.applyBulletin = null;
            AndroidUtilities.runOnUIThread(runnable);
        }
    }

    public boolean onMemberClick(TLRPC.ChatParticipant chatParticipant, boolean z, View view) {
        return onMemberClick(chatParticipant, z, false, view);
    }

    @Override
    public boolean onMemberClick(final TLRPC.ChatParticipant chatParticipant, boolean z, boolean z2, View view) {
        boolean z3;
        TLRPC.ChannelParticipant channelParticipant;
        boolean z4;
        boolean z5;
        boolean z6;
        if (getParentActivity() == null) {
            return false;
        }
        if (z) {
            final TLRPC.User user = getMessagesController().getUser(Long.valueOf(chatParticipant.user_id));
            if (user != null && chatParticipant.user_id != getUserConfig().getClientUserId()) {
                this.selectedUser = chatParticipant.user_id;
                if (ChatObject.isChannel(this.currentChat)) {
                    TLRPC.ChannelParticipant channelParticipant2 = ((TLRPC.TL_chatChannelParticipant) chatParticipant).channelParticipant;
                    getMessagesController().getUser(Long.valueOf(chatParticipant.user_id));
                    boolean canAddAdmins = ChatObject.canAddAdmins(this.currentChat);
                    if (canAddAdmins && ((channelParticipant2 instanceof TLRPC.TL_channelParticipantCreator) || ((channelParticipant2 instanceof TLRPC.TL_channelParticipantAdmin) && !channelParticipant2.can_edit))) {
                        canAddAdmins = false;
                    }
                    boolean z7 = ChatObject.canBlockUsers(this.currentChat) && (!((channelParticipant2 instanceof TLRPC.TL_channelParticipantAdmin) || (channelParticipant2 instanceof TLRPC.TL_channelParticipantCreator)) || channelParticipant2.can_edit);
                    z3 = channelParticipant2 instanceof TLRPC.TL_channelParticipantAdmin;
                    channelParticipant = channelParticipant2;
                    z4 = canAddAdmins;
                    z6 = z7;
                    z5 = this.currentChat.gigagroup ? false : z7;
                } else {
                    TLRPC.Chat chat = this.currentChat;
                    boolean z8 = chat.creator || ((chatParticipant instanceof TLRPC.TL_chatParticipant) && (ChatObject.canBlockUsers(chat) || chatParticipant.inviter_id == getUserConfig().getClientUserId()));
                    z3 = chatParticipant instanceof TLRPC.TL_chatParticipantAdmin;
                    channelParticipant = null;
                    z4 = this.currentChat.creator;
                    z5 = z4;
                    z6 = z8;
                }
                final boolean z9 = z3;
                r1 = z4 || z5 || z6;
                if (!z2 && r1) {
                    final TLRPC.ChannelParticipant channelParticipant3 = channelParticipant;
                    final Utilities.Callback callback = new Utilities.Callback() {
                        @Override
                        public final void run(Object obj) {
                            ProfileActivity.this.lambda$onMemberClick$40(channelParticipant3, user, chatParticipant, z9, (Integer) obj);
                        }
                    };
                    final TLRPC.ChannelParticipant channelParticipant4 = channelParticipant;
                    ItemOptions.makeOptions(this, view).setScrimViewBackground(new ColorDrawable(Theme.getColor(Theme.key_windowBackgroundWhite))).addIf(z4, R.drawable.msg_admins, LocaleController.getString(z9 ? R.string.EditAdminRights : R.string.SetAsAdmin), new Runnable() {
                        @Override
                        public final void run() {
                            ProfileActivity.lambda$onMemberClick$41(Utilities.Callback.this);
                        }
                    }).addIf(z5, R.drawable.msg_permissions, LocaleController.getString(R.string.ChangePermissions), new Runnable() {
                        @Override
                        public final void run() {
                            ProfileActivity.this.lambda$onMemberClick$43(channelParticipant4, chatParticipant, user, callback);
                        }
                    }).addIf(z6, R.drawable.msg_remove, (CharSequence) LocaleController.getString(R.string.KickFromGroup), true, new Runnable() {
                        @Override
                        public final void run() {
                            ProfileActivity.this.lambda$onMemberClick$44(chatParticipant);
                        }
                    }).setMinWidth(190).show();
                }
            }
            return r1;
        }
        if (chatParticipant.user_id == getUserConfig().getClientUserId()) {
            return false;
        }
        Bundle bundle = new Bundle();
        bundle.putLong("user_id", chatParticipant.user_id);
        bundle.putBoolean("preload_messages", true);
        presentFragment(new ProfileActivity(bundle));
        return true;
    }

    @Override
    public void onPause() {
        super.onPause();
        UndoView undoView = this.undoView;
        if (undoView != null) {
            undoView.hide(true, 0);
        }
        ImageUpdater imageUpdater = this.imageUpdater;
        if (imageUpdater != null) {
            imageUpdater.onPause();
        }
        FlagSecureReason flagSecureReason = this.flagSecure;
        if (flagSecureReason != null) {
            flagSecureReason.detach();
        }
        SharedMediaLayout sharedMediaLayout = this.sharedMediaLayout;
        if (sharedMediaLayout != null) {
            sharedMediaLayout.onPause();
        }
    }

    @Override
    public void onRequestPermissionsResultFragment(int i, String[] strArr, int[] iArr) {
        boolean z;
        boolean z2;
        ImageUpdater imageUpdater = this.imageUpdater;
        if (imageUpdater != null) {
            imageUpdater.onRequestPermissionsResultFragment(i, strArr, iArr);
        }
        if (i == 101 || i == 102) {
            TLRPC.User user = getMessagesController().getUser(Long.valueOf(this.userId));
            if (user == null) {
                return;
            }
            int i2 = 0;
            while (true) {
                if (i2 >= iArr.length) {
                    z = true;
                    break;
                } else {
                    if (iArr[i2] != 0) {
                        z = false;
                        break;
                    }
                    i2++;
                }
            }
            if (iArr.length > 0 && z) {
                boolean z3 = i == 102;
                TLRPC.UserFull userFull = this.userInfo;
                VoIPHelper.startCall(user, z3, userFull != null && userFull.video_calls_available, getParentActivity(), this.userInfo, getAccountInstance());
                return;
            }
        } else {
            if (i != 103 || this.currentChat == null) {
                return;
            }
            int i3 = 0;
            while (true) {
                if (i3 >= iArr.length) {
                    z2 = true;
                    break;
                } else {
                    if (iArr[i3] != 0) {
                        z2 = false;
                        break;
                    }
                    i3++;
                }
            }
            if (iArr.length > 0 && z2) {
                VoIPHelper.startCall(this.currentChat, null, null, getMessagesController().getGroupCall(this.chatId, false) == null, getParentActivity(), this, getAccountInstance());
                return;
            }
        }
        VoIPHelper.permissionDenied(getParentActivity(), null, i);
    }

    @Override
    public void onResume() {
        TLRPC.User user;
        View view;
        super.onResume();
        SharedMediaLayout sharedMediaLayout = this.sharedMediaLayout;
        if (sharedMediaLayout != null) {
            sharedMediaLayout.onResume();
        }
        invalidateIsInLandscapeMode();
        ListAdapter listAdapter = this.listAdapter;
        if (listAdapter != null) {
            this.firstLayout = true;
            listAdapter.notifyDataSetChanged();
        }
        if (!this.parentLayout.isInPreviewMode() && (view = this.blurredView) != null && view.getVisibility() == 0) {
            this.blurredView.setVisibility(8);
            this.blurredView.setBackground(null);
        }
        ImageUpdater imageUpdater = this.imageUpdater;
        if (imageUpdater != null) {
            imageUpdater.onResume();
            setParentActivityTitle(LocaleController.getString(R.string.Settings));
        }
        updateProfileData(true);
        fixLayout();
        SimpleTextView simpleTextView = this.nameTextView[1];
        if (simpleTextView != null) {
            setParentActivityTitle(simpleTextView.getText());
        }
        if (this.userId != 0 && (user = getMessagesController().getUser(Long.valueOf(this.userId))) != null && user.photo == null && this.extraHeight >= AndroidUtilities.dp(88.0f)) {
            this.expandAnimator.cancel();
            float[] fArr = this.expandAnimatorValues;
            fArr[0] = 1.0f;
            fArr[1] = 0.0f;
            setAvatarExpandProgress(1.0f);
            this.avatarsViewPager.setVisibility(8);
            this.extraHeight = AndroidUtilities.dp(88.0f);
            this.allowPullingDown = false;
            this.layoutManager.scrollToPositionWithOffset(0, AndroidUtilities.dp(88.0f) - this.listView.getPaddingTop());
        }
        FlagSecureReason flagSecureReason = this.flagSecure;
        if (flagSecureReason != null) {
            flagSecureReason.attach();
        }
        updateItemsUsername();
    }

    @Override
    public void onTransitionAnimationEnd(boolean z, boolean z2) {
        if (z) {
            if (!z2) {
                int i = this.playProfileAnimation;
                if (i != 0 && this.allowProfileAnimation) {
                    if (i == 1) {
                        this.currentExpandAnimatorValue = 0.0f;
                    }
                    this.openAnimationInProgress = false;
                    checkListViewScroll();
                    if (this.recreateMenuAfterAnimation) {
                        createActionBarMenu(true);
                    }
                }
                if (!this.fragmentOpened) {
                    this.fragmentOpened = true;
                    this.invalidateScroll = true;
                    this.fragmentView.requestLayout();
                }
            }
            getNotificationCenter().onAnimationFinish(this.transitionIndex);
            View view = this.blurredView;
            if (view != null && view.getVisibility() == 0) {
                this.blurredView.setVisibility(8);
                this.blurredView.setBackground(null);
            }
        }
        this.transitionAnimationInProress = false;
        checkPhotoDescriptionAlpha();
    }

    @Override
    public void onTransitionAnimationProgress(boolean z, float f) {
        super.onTransitionAnimationProgress(z, f);
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

    @Override
    public void onTransitionAnimationStart(boolean z, boolean z2) {
        int navigationBarColor;
        super.onTransitionAnimationStart(z, z2);
        this.isFragmentOpened = z;
        if (((!z && z2) || (z && !z2)) && this.playProfileAnimation != 0 && this.allowProfileAnimation && !this.isPulledDown) {
            this.openAnimationInProgress = true;
        }
        if (z) {
            this.transitionIndex = this.imageUpdater != null ? getNotificationCenter().setAnimationInProgress(this.transitionIndex, new int[]{NotificationCenter.dialogsNeedReload, NotificationCenter.closeChats, NotificationCenter.mediaCountDidLoad, NotificationCenter.mediaCountsDidLoad, NotificationCenter.userInfoDidLoad, NotificationCenter.needCheckSystemBarColors}) : getNotificationCenter().setAnimationInProgress(this.transitionIndex, new int[]{NotificationCenter.dialogsNeedReload, NotificationCenter.closeChats, NotificationCenter.mediaCountDidLoad, NotificationCenter.mediaCountsDidLoad, NotificationCenter.needCheckSystemBarColors});
            if (Build.VERSION.SDK_INT >= 21 && !z2 && getParentActivity() != null) {
                navigationBarColor = getParentActivity().getWindow().getNavigationBarColor();
                this.navigationBarAnimationColorFrom = navigationBarColor;
            }
        }
        this.transitionAnimationInProress = true;
        checkPhotoDescriptionAlpha();
    }

    @Override
    public void onUploadProgressChanged(float f) {
        RadialProgressView radialProgressView = this.avatarProgressView;
        if (radialProgressView == null) {
            return;
        }
        radialProgressView.setProgress(f);
        this.avatarsViewPager.setUploadProgress(this.uploadingImageLocation, f);
    }

    public void prepareBlurBitmap() {
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

    public void restoreSelfArgs(Bundle bundle) {
        ImageUpdater imageUpdater = this.imageUpdater;
        if (imageUpdater != null) {
            imageUpdater.currentPicturePath = bundle.getString("path");
        }
    }

    @Override
    public void saveSelfArgs(Bundle bundle) {
        String str;
        ImageUpdater imageUpdater = this.imageUpdater;
        if (imageUpdater == null || (str = imageUpdater.currentPicturePath) == null) {
            return;
        }
        bundle.putString("path", str);
    }

    @Override
    public void scrollToSharedMedia() {
        this.layoutManager.scrollToPositionWithOffset(this.sharedMediaRow, -this.listView.getPaddingTop());
    }

    public void setAvatarAnimationProgress(float f) {
        int profileBackColorForId;
        int profileTextColorForId;
        this.currentExpandAnimatorValue = f;
        this.avatarAnimationProgress = f;
        checkPhotoDescriptionAlpha();
        if (this.playProfileAnimation == 2) {
            this.avatarImage.setProgressToExpand(f);
        }
        this.listView.setAlpha(f);
        this.listView.setTranslationX(AndroidUtilities.dp(48.0f) - (AndroidUtilities.dp(48.0f) * f));
        long j = 5;
        if (this.playProfileAnimation != 2 || (profileBackColorForId = this.avatarColor) == 0) {
            profileBackColorForId = AvatarDrawable.getProfileBackColorForId((this.userId != 0 || (ChatObject.isChannel(this.chatId, this.currentAccount) && !this.currentChat.megagroup)) ? 5L : this.chatId, this.resourcesProvider);
        }
        int i = this.actionBarAnimationColorFrom;
        if (i == 0) {
            i = getThemedColor(Theme.key_actionBarDefault);
        }
        this.topView.setBackgroundColor(ColorUtils.blendARGB(SharedConfig.chatBlurEnabled() ? ColorUtils.setAlphaComponent(i, 0) : i, profileBackColorForId, f));
        this.timerDrawable.setBackgroundColor(ColorUtils.blendARGB(i, profileBackColorForId, f));
        this.actionBar.setItemsColor(ColorUtils.blendARGB(this.peerColor != null ? -1 : getThemedColor(Theme.key_actionBarDefaultIcon), AvatarDrawable.getIconColorForId((this.userId != 0 || (ChatObject.isChannel(this.chatId, this.currentAccount) && !this.currentChat.megagroup)) ? 5L : this.chatId, this.resourcesProvider), this.avatarAnimationProgress), false);
        int themedColor = getThemedColor(Theme.key_profile_title);
        int themedColor2 = getThemedColor(Theme.key_actionBarDefaultTitle);
        for (int i2 = 0; i2 < 2; i2++) {
            SimpleTextView simpleTextView = this.nameTextView[i2];
            if (simpleTextView != null && (i2 != 1 || this.playProfileAnimation != 2)) {
                simpleTextView.setTextColor(ColorUtils.blendARGB(themedColor2, themedColor, f));
            }
        }
        if (this.isOnline[0]) {
            profileTextColorForId = getThemedColor(Theme.key_profile_status);
        } else {
            if (this.userId == 0 && (!ChatObject.isChannel(this.chatId, this.currentAccount) || this.currentChat.megagroup)) {
                j = this.chatId;
            }
            profileTextColorForId = AvatarDrawable.getProfileTextColorForId(j, this.resourcesProvider);
        }
        int themedColor3 = getThemedColor(this.isOnline[0] ? Theme.key_chat_status : Theme.key_actionBarDefaultSubtitle);
        int i3 = 0;
        while (i3 < 3) {
            SimpleTextView simpleTextView2 = this.onlineTextView[i3];
            if (simpleTextView2 != null && i3 != 1 && (i3 != 2 || this.playProfileAnimation != 2)) {
                simpleTextView2.setTextColor(ColorUtils.blendARGB(i3 == 0 ? themedColor3 : applyPeerColor(themedColor3, true, Boolean.valueOf(this.isOnline[0])), i3 == 0 ? profileTextColorForId : applyPeerColor(profileTextColorForId, true, Boolean.valueOf(this.isOnline[0])), f));
            }
            i3++;
        }
        this.extraHeight = this.initialAnimationExtraHeight * f;
        long j2 = this.userId;
        if (j2 == 0) {
            j2 = this.chatId;
        }
        int profileColorForId = AvatarDrawable.getProfileColorForId(j2, this.resourcesProvider);
        long j3 = this.userId;
        if (j3 == 0) {
            j3 = this.chatId;
        }
        int colorForId = AvatarDrawable.getColorForId(j3);
        if (profileColorForId != colorForId) {
            this.avatarDrawable.setColor(ColorUtils.blendARGB(colorForId, profileColorForId, f));
            this.avatarImage.invalidate();
        }
        int i4 = this.navigationBarAnimationColorFrom;
        if (i4 != 0) {
            setNavigationBarColor(ColorUtils.blendARGB(i4, getNavigationBarColor(), f));
        }
        this.topView.invalidate();
        needLayout(true);
        View view = this.fragmentView;
        if (view != null) {
            view.invalidate();
        }
        AboutLinkCell aboutLinkCell = this.aboutLinkCell;
        if (aboutLinkCell != null) {
            aboutLinkCell.invalidate();
        }
        if (getDialogId() > 0) {
            AvatarImageView avatarImageView = this.avatarImage;
            if (avatarImageView != null) {
                avatarImageView.setProgressToStoriesInsets(this.avatarAnimationProgress);
            }
            ProfileStoriesView profileStoriesView = this.storyView;
            if (profileStoriesView != null) {
                profileStoriesView.setProgressToStoriesInsets(this.avatarAnimationProgress);
            }
        }
    }

    public void setChatInfo(TLRPC.ChatFull chatFull) {
        TLRPC.ChatFull chatFull2;
        this.chatInfo = chatFull;
        if (chatFull != null) {
            long j = chatFull.migrated_from_chat_id;
            if (j != 0 && this.mergeDialogId == 0) {
                this.mergeDialogId = -j;
                getMediaDataController().getMediaCounts(this.mergeDialogId, this.topicId, this.classGuid);
            }
        }
        SharedMediaLayout sharedMediaLayout = this.sharedMediaLayout;
        if (sharedMediaLayout != null) {
            sharedMediaLayout.setChatInfo(this.chatInfo);
        }
        ProfileGalleryView profileGalleryView = this.avatarsViewPager;
        if (profileGalleryView != null && !this.isTopic) {
            profileGalleryView.setChatInfo(this.chatInfo);
        }
        ProfileStoriesView profileStoriesView = this.storyView;
        if (profileStoriesView != null && (chatFull2 = this.chatInfo) != null) {
            profileStoriesView.setStories(chatFull2.stories);
        }
        AvatarImageView avatarImageView = this.avatarImage;
        if (avatarImageView != null) {
            avatarImageView.setHasStories(needInsetForStories());
        }
        fetchUsersFromChannelInfo();
    }

    public void setLoadingSpan(CharacterStyle characterStyle) {
        if (this.loadingSpan == characterStyle) {
            return;
        }
        this.loadingSpan = characterStyle;
        AndroidUtilities.forEachViews((RecyclerView) this.listView, new com.google.android.exoplayer2.util.Consumer() {
            @Override
            public final void accept(Object obj) {
                ProfileActivity.this.lambda$setLoadingSpan$92((View) obj);
            }
        });
    }

    @Override
    public void setParentLayout(INavigationLayout iNavigationLayout) {
        super.setParentLayout(iNavigationLayout);
        FlagSecureReason flagSecureReason = this.flagSecure;
        if (flagSecureReason != null) {
            flagSecureReason.detach();
            this.flagSecure = null;
        }
        if (iNavigationLayout == null || iNavigationLayout.getParentActivity() == null) {
            return;
        }
        this.flagSecure = new FlagSecureReason(iNavigationLayout.getParentActivity().getWindow(), new FlagSecureReason.FlagSecureCondition() {
            @Override
            public final boolean run() {
                boolean lambda$setParentLayout$5;
                lambda$setParentLayout$5 = ProfileActivity.this.lambda$setParentLayout$5();
                return lambda$setParentLayout$5;
            }
        });
    }

    public void setPlayProfileAnimation(int i) {
        SharedPreferences globalMainSettings = MessagesController.getGlobalMainSettings();
        if (AndroidUtilities.isTablet()) {
            return;
        }
        this.needTimerImage = i != 0;
        this.needStarImage = i != 0;
        if (globalMainSettings.getBoolean("view_animations", true)) {
            this.playProfileAnimation = i;
        } else if (i == 2) {
            this.expandPhoto = true;
        }
    }

    public void setUserInfo(TLRPC.UserFull userFull, ProfileChannelCell.ChannelMessageFetcher channelMessageFetcher, ProfileBirthdayEffect.BirthdayEffectFetcher birthdayEffectFetcher) {
        this.userInfo = userFull;
        ProfileStoriesView profileStoriesView = this.storyView;
        if (profileStoriesView != null) {
            profileStoriesView.setStories(userFull.stories);
        }
        AvatarImageView avatarImageView = this.avatarImage;
        if (avatarImageView != null) {
            avatarImageView.setHasStories(needInsetForStories());
        }
        SharedMediaLayout sharedMediaLayout = this.sharedMediaLayout;
        if (sharedMediaLayout != null) {
            sharedMediaLayout.setUserInfo(this.userInfo);
        }
        if (this.profileChannelMessageFetcher == null) {
            this.profileChannelMessageFetcher = channelMessageFetcher;
        }
        if (this.profileChannelMessageFetcher == null) {
            this.profileChannelMessageFetcher = new ProfileChannelCell.ChannelMessageFetcher(this.currentAccount);
        }
        this.profileChannelMessageFetcher.subscribe(new Runnable() {
            @Override
            public final void run() {
                ProfileActivity.this.lambda$setUserInfo$74();
            }
        });
        this.profileChannelMessageFetcher.fetch(this.userInfo);
        if (this.birthdayFetcher == null) {
            this.birthdayFetcher = birthdayEffectFetcher;
        }
        ProfileBirthdayEffect.BirthdayEffectFetcher birthdayEffectFetcher2 = this.birthdayFetcher;
        if (birthdayEffectFetcher2 == null) {
            ProfileBirthdayEffect.BirthdayEffectFetcher of = ProfileBirthdayEffect.BirthdayEffectFetcher.of(this.currentAccount, this.userInfo, birthdayEffectFetcher2);
            this.birthdayFetcher = of;
            this.createdBirthdayFetcher = of != null;
        }
        ProfileBirthdayEffect.BirthdayEffectFetcher birthdayEffectFetcher3 = this.birthdayFetcher;
        if (birthdayEffectFetcher3 != null) {
            birthdayEffectFetcher3.subscribe(new ProfileActivity$$ExternalSyntheticLambda2(this));
        }
        ActionBarMenuItem actionBarMenuItem = this.otherItem;
        if (actionBarMenuItem != null) {
            actionBarMenuItem.setSubItemShown(20, true ^ getMessagesController().isUserPremiumBlocked(this.userId));
            if (hasPrivacyCommand()) {
                this.otherItem.showSubItem(44);
            } else {
                this.otherItem.hideSubItem(44);
            }
        }
    }

    public void showStatusSelect() {
        if (this.selectAnimatedEmojiDialog != null) {
            return;
        }
        SelectAnimatedEmojiDialog.SelectAnimatedEmojiDialogWindow[] selectAnimatedEmojiDialogWindowArr = new SelectAnimatedEmojiDialog.SelectAnimatedEmojiDialogWindow[1];
        Rect rect = AndroidUtilities.rectTmp2;
        getEmojiStatusLocation(rect);
        int i = this.nameTextView[1].getScaleX() < 1.5f ? 16 : 32;
        int dp = (-(this.avatarContainer2.getHeight() - rect.centerY())) - AndroidUtilities.dp(i);
        int min = (int) Math.min(AndroidUtilities.dp(324.0f), AndroidUtilities.displaySize.x * 0.95f);
        int centerX = rect.centerX();
        int clamp = MathUtils.clamp(centerX - (min / 2), 0, AndroidUtilities.displaySize.x - min);
        AnonymousClass36 anonymousClass36 = new SelectAnimatedEmojiDialog(this, getContext(), true, Integer.valueOf(Math.max(0, centerX - clamp)), this.currentChat == null ? 0 : 9, true, this.resourcesProvider, i) {
            final SelectAnimatedEmojiDialog.SelectAnimatedEmojiDialogWindow[] val$popup;

            AnonymousClass36(BaseFragment this, Context context, boolean z, Integer num, int i2, boolean z2, Theme.ResourcesProvider resourcesProvider, int i3, SelectAnimatedEmojiDialog.SelectAnimatedEmojiDialogWindow[] selectAnimatedEmojiDialogWindowArr2) {
                super(this, context, z, num, i2, z2, resourcesProvider, i3);
                r20 = selectAnimatedEmojiDialogWindowArr2;
            }

            @Override
            protected void onEmojiSelected(View view, Long l, TLRPC.Document document, Integer num) {
                TLRPC.TL_channels_updateEmojiStatus tL_channels_updateEmojiStatus;
                AnimatedEmojiDrawable.SwapAnimatedEmojiDrawable swapAnimatedEmojiDrawable;
                if (ProfileActivity.this.currentChat == null) {
                    TL_account.updateEmojiStatus updateemojistatus = new TL_account.updateEmojiStatus();
                    if (l == null) {
                        updateemojistatus.emoji_status = new TLRPC.TL_emojiStatusEmpty();
                    } else if (num != null) {
                        TLRPC.TL_emojiStatusUntil tL_emojiStatusUntil = new TLRPC.TL_emojiStatusUntil();
                        updateemojistatus.emoji_status = tL_emojiStatusUntil;
                        tL_emojiStatusUntil.document_id = l.longValue();
                        ((TLRPC.TL_emojiStatusUntil) updateemojistatus.emoji_status).until = num.intValue();
                    } else {
                        TLRPC.TL_emojiStatus tL_emojiStatus = new TLRPC.TL_emojiStatus();
                        updateemojistatus.emoji_status = tL_emojiStatus;
                        tL_emojiStatus.document_id = l.longValue();
                    }
                    TLRPC.User user = MessagesController.getInstance(((BaseFragment) ProfileActivity.this).currentAccount).getUser(Long.valueOf(UserConfig.getInstance(((BaseFragment) ProfileActivity.this).currentAccount).getClientUserId()));
                    tL_channels_updateEmojiStatus = updateemojistatus;
                    if (user != null) {
                        user.emoji_status = updateemojistatus.emoji_status;
                        MessagesController.getInstance(((BaseFragment) ProfileActivity.this).currentAccount).updateEmojiStatusUntilUpdate(user.id, user.emoji_status);
                        NotificationCenter.getInstance(((BaseFragment) ProfileActivity.this).currentAccount).lambda$postNotificationNameOnUIThread$1(NotificationCenter.userEmojiStatusUpdated, user);
                        tL_channels_updateEmojiStatus = updateemojistatus;
                    }
                } else {
                    TLRPC.TL_channels_updateEmojiStatus tL_channels_updateEmojiStatus2 = new TLRPC.TL_channels_updateEmojiStatus();
                    tL_channels_updateEmojiStatus2.channel = MessagesController.getInputChannel(ProfileActivity.this.currentChat);
                    if (l == null) {
                        tL_channels_updateEmojiStatus2.emoji_status = new TLRPC.TL_emojiStatusEmpty();
                    } else if (num != null) {
                        TLRPC.TL_emojiStatusUntil tL_emojiStatusUntil2 = new TLRPC.TL_emojiStatusUntil();
                        tL_channels_updateEmojiStatus2.emoji_status = tL_emojiStatusUntil2;
                        tL_emojiStatusUntil2.document_id = l.longValue();
                        ((TLRPC.TL_emojiStatusUntil) tL_channels_updateEmojiStatus2.emoji_status).until = num.intValue();
                    } else {
                        TLRPC.TL_emojiStatus tL_emojiStatus2 = new TLRPC.TL_emojiStatus();
                        tL_channels_updateEmojiStatus2.emoji_status = tL_emojiStatus2;
                        tL_emojiStatus2.document_id = l.longValue();
                    }
                    tL_channels_updateEmojiStatus = tL_channels_updateEmojiStatus2;
                    if (ProfileActivity.this.currentChat != null) {
                        ProfileActivity.this.currentChat.emoji_status = tL_channels_updateEmojiStatus2.emoji_status;
                        MessagesController.getInstance(((BaseFragment) ProfileActivity.this).currentAccount).updateEmojiStatusUntilUpdate(-ProfileActivity.this.currentChat.id, ProfileActivity.this.currentChat.emoji_status);
                        NotificationCenter.getInstance(((BaseFragment) ProfileActivity.this).currentAccount).lambda$postNotificationNameOnUIThread$1(NotificationCenter.updateInterfaces, Integer.valueOf(MessagesController.UPDATE_MASK_EMOJI_STATUS));
                        tL_channels_updateEmojiStatus = tL_channels_updateEmojiStatus2;
                    }
                }
                int i2 = 0;
                while (true) {
                    Drawable drawable = null;
                    if (i2 >= 2) {
                        break;
                    }
                    if (ProfileActivity.this.emojiStatusDrawable[i2] != null) {
                        if (l == null && ProfileActivity.this.currentChat == null) {
                            swapAnimatedEmojiDrawable = ProfileActivity.this.emojiStatusDrawable[i2];
                            drawable = ProfileActivity.this.getPremiumCrossfadeDrawable(i2);
                        } else {
                            AnimatedEmojiDrawable.SwapAnimatedEmojiDrawable[] swapAnimatedEmojiDrawableArr = ProfileActivity.this.emojiStatusDrawable;
                            if (l != null) {
                                swapAnimatedEmojiDrawableArr[i2].set(l.longValue(), true);
                            } else {
                                swapAnimatedEmojiDrawable = swapAnimatedEmojiDrawableArr[i2];
                            }
                        }
                        swapAnimatedEmojiDrawable.set(drawable, true);
                    }
                    i2++;
                }
                if (l != null) {
                    ProfileActivity.this.animatedStatusView.animateChange(ReactionsLayoutInBubble.VisibleReaction.fromCustomEmoji(l));
                }
                ProfileActivity.this.updateEmojiStatusDrawableColor();
                ProfileActivity.this.updateEmojiStatusEffectPosition();
                ConnectionsManager.getInstance(((BaseFragment) ProfileActivity.this).currentAccount).sendRequest(tL_channels_updateEmojiStatus, null);
                if (r20[0] != null) {
                    ProfileActivity.this.selectAnimatedEmojiDialog = null;
                    r20[0].dismiss();
                }
            }
        };
        TLRPC.User user = getMessagesController().getUser(Long.valueOf(this.userId));
        if (user != null) {
            anonymousClass36.setExpireDateHint(DialogObject.getEmojiStatusUntil(user.emoji_status));
        }
        AnimatedEmojiDrawable.SwapAnimatedEmojiDrawable swapAnimatedEmojiDrawable = this.emojiStatusDrawable[1];
        anonymousClass36.setSelected((swapAnimatedEmojiDrawable == null || !(swapAnimatedEmojiDrawable.getDrawable() instanceof AnimatedEmojiDrawable)) ? null : Long.valueOf(((AnimatedEmojiDrawable) this.emojiStatusDrawable[1].getDrawable()).getDocumentId()));
        anonymousClass36.setSaveState(3);
        anonymousClass36.setScrimDrawable(this.emojiStatusDrawable[1], this.nameTextView[1]);
        AnonymousClass37 anonymousClass37 = new SelectAnimatedEmojiDialog.SelectAnimatedEmojiDialogWindow(anonymousClass36, -2, -2) {
            AnonymousClass37(View anonymousClass362, int i2, int i22) {
                super(anonymousClass362, i2, i22);
            }

            @Override
            public void dismiss() {
                super.dismiss();
                ProfileActivity.this.selectAnimatedEmojiDialog = null;
            }
        };
        this.selectAnimatedEmojiDialog = anonymousClass37;
        selectAnimatedEmojiDialogWindowArr2[0] = anonymousClass37;
        int[] iArr = new int[2];
        SimpleTextView simpleTextView = this.nameTextView[1];
        if (simpleTextView != null) {
            simpleTextView.getLocationOnScreen(iArr);
        }
        selectAnimatedEmojiDialogWindowArr2[0].showAsDropDown(this.fragmentView, clamp, dp, 51);
        selectAnimatedEmojiDialogWindowArr2[0].dimBehind();
    }

    public void updateListAnimated(boolean z) {
        updateListAnimated(z, false);
    }

    public void updateQrItemVisibility(boolean z) {
        if (this.qrItem == null) {
            return;
        }
        boolean z2 = isQrNeedVisible() && Math.min(1.0f, this.extraHeight / ((float) AndroidUtilities.dp(88.0f))) > 0.5f && this.searchTransitionProgress > 0.5f;
        if (!z) {
            AnimatorSet animatorSet = this.qrItemAnimation;
            if (animatorSet != null) {
                animatorSet.cancel();
                this.qrItemAnimation = null;
            }
            this.isQrItemVisible = z2;
            this.qrItem.setClickable(z2);
            this.qrItem.setAlpha(z2 ? 1.0f : 0.0f);
            this.qrItem.setVisibility(z2 ? 0 : 8);
            return;
        }
        if (z2 != this.isQrItemVisible) {
            this.isQrItemVisible = z2;
            AnimatorSet animatorSet2 = this.qrItemAnimation;
            if (animatorSet2 != null) {
                animatorSet2.cancel();
                this.qrItemAnimation = null;
            }
            this.qrItem.setClickable(this.isQrItemVisible);
            this.qrItemAnimation = new AnimatorSet();
            if (this.qrItem.getVisibility() != 8 || z2) {
                this.qrItem.setVisibility(0);
            }
            AnimatorSet animatorSet3 = this.qrItemAnimation;
            if (z2) {
                animatorSet3.setInterpolator(new DecelerateInterpolator());
                this.qrItemAnimation.playTogether(ObjectAnimator.ofFloat(this.qrItem, (Property<ActionBarMenuItem, Float>) View.ALPHA, 1.0f), ObjectAnimator.ofFloat(this.qrItem, (Property<ActionBarMenuItem, Float>) View.SCALE_Y, 1.0f), ObjectAnimator.ofFloat(this.avatarsViewPagerIndicatorView, (Property<PagerIndicatorView, Float>) View.TRANSLATION_X, -AndroidUtilities.dp(48.0f)));
            } else {
                animatorSet3.setInterpolator(new AccelerateInterpolator());
                this.qrItemAnimation.playTogether(ObjectAnimator.ofFloat(this.qrItem, (Property<ActionBarMenuItem, Float>) View.ALPHA, 0.0f), ObjectAnimator.ofFloat(this.qrItem, (Property<ActionBarMenuItem, Float>) View.SCALE_Y, 0.0f), ObjectAnimator.ofFloat(this.avatarsViewPagerIndicatorView, (Property<PagerIndicatorView, Float>) View.TRANSLATION_X, 0.0f));
            }
            this.qrItemAnimation.setDuration(150L);
            this.qrItemAnimation.addListener(new AnimatorListenerAdapter() {
                AnonymousClass48() {
                }

                @Override
                public void onAnimationEnd(Animator animator) {
                    ProfileActivity.this.qrItemAnimation = null;
                }
            });
            this.qrItemAnimation.start();
        }
    }

    @Override
    public void updateSelectedMediaTabText() {
        AudioPlayerAlert.ClippingTextViewSwitcher clippingTextViewSwitcher;
        CharSequence formatPluralStringComma;
        AudioPlayerAlert.ClippingTextViewSwitcher clippingTextViewSwitcher2;
        AudioPlayerAlert.ClippingTextViewSwitcher clippingTextViewSwitcher3;
        String formatPluralString;
        int i;
        int i2;
        SharedMediaLayout sharedMediaLayout = this.sharedMediaLayout;
        if (sharedMediaLayout == null || this.mediaCounterTextView == null) {
            return;
        }
        int closestTab = sharedMediaLayout.getClosestTab();
        int[] lastMediaCount = this.sharedMediaPreloader.getLastMediaCount();
        if (closestTab == 0) {
            if (lastMediaCount[7] > 0 || lastMediaCount[6] > 0) {
                if (this.sharedMediaLayout.getPhotosVideosTypeFilter() == 1 || lastMediaCount[7] <= 0) {
                    clippingTextViewSwitcher3 = this.mediaCounterTextView;
                    formatPluralString = LocaleController.formatPluralString("Photos", lastMediaCount[6], new Object[0]);
                } else if (this.sharedMediaLayout.getPhotosVideosTypeFilter() != 2 && (i2 = lastMediaCount[6]) > 0) {
                    this.mediaCounterTextView.setText(String.format("%s, %s", LocaleController.formatPluralString("Photos", i2, new Object[0]), LocaleController.formatPluralString("Videos", lastMediaCount[7], new Object[0])));
                    return;
                } else {
                    clippingTextViewSwitcher3 = this.mediaCounterTextView;
                    formatPluralString = LocaleController.formatPluralString("Videos", lastMediaCount[7], new Object[0]);
                }
                AudioPlayerAlert.ClippingTextViewSwitcher clippingTextViewSwitcher4 = clippingTextViewSwitcher3;
                formatPluralStringComma = formatPluralString;
                clippingTextViewSwitcher = clippingTextViewSwitcher4;
            } else {
                int i3 = lastMediaCount[0];
                if (i3 <= 0) {
                    clippingTextViewSwitcher = this.mediaCounterTextView;
                    i = R.string.SharedMedia;
                    formatPluralStringComma = LocaleController.getString(i);
                } else {
                    clippingTextViewSwitcher2 = this.mediaCounterTextView;
                    formatPluralStringComma = LocaleController.formatPluralString("Media", i3, new Object[0]);
                    clippingTextViewSwitcher = clippingTextViewSwitcher2;
                }
            }
        } else if (closestTab == 1) {
            int i4 = lastMediaCount[1];
            if (i4 <= 0) {
                clippingTextViewSwitcher = this.mediaCounterTextView;
                i = R.string.Files;
                formatPluralStringComma = LocaleController.getString(i);
            } else {
                clippingTextViewSwitcher2 = this.mediaCounterTextView;
                formatPluralStringComma = LocaleController.formatPluralString("Files", i4, new Object[0]);
                clippingTextViewSwitcher = clippingTextViewSwitcher2;
            }
        } else if (closestTab == 2) {
            int i5 = lastMediaCount[2];
            if (i5 <= 0) {
                clippingTextViewSwitcher = this.mediaCounterTextView;
                i = R.string.Voice;
                formatPluralStringComma = LocaleController.getString(i);
            } else {
                clippingTextViewSwitcher2 = this.mediaCounterTextView;
                formatPluralStringComma = LocaleController.formatPluralString("Voice", i5, new Object[0]);
                clippingTextViewSwitcher = clippingTextViewSwitcher2;
            }
        } else if (closestTab == 3) {
            int i6 = lastMediaCount[3];
            if (i6 <= 0) {
                clippingTextViewSwitcher = this.mediaCounterTextView;
                i = R.string.SharedLinks;
                formatPluralStringComma = LocaleController.getString(i);
            } else {
                clippingTextViewSwitcher2 = this.mediaCounterTextView;
                formatPluralStringComma = LocaleController.formatPluralString("Links", i6, new Object[0]);
                clippingTextViewSwitcher = clippingTextViewSwitcher2;
            }
        } else if (closestTab == 4) {
            int i7 = lastMediaCount[4];
            if (i7 <= 0) {
                clippingTextViewSwitcher = this.mediaCounterTextView;
                i = R.string.Music;
                formatPluralStringComma = LocaleController.getString(i);
            } else {
                clippingTextViewSwitcher2 = this.mediaCounterTextView;
                formatPluralStringComma = LocaleController.formatPluralString("MusicFiles", i7, new Object[0]);
                clippingTextViewSwitcher = clippingTextViewSwitcher2;
            }
        } else {
            if (closestTab == 5) {
                int i8 = lastMediaCount[5];
                if (i8 <= 0) {
                    clippingTextViewSwitcher = this.mediaCounterTextView;
                    i = R.string.AccDescrGIFs;
                    formatPluralStringComma = LocaleController.getString(i);
                } else {
                    clippingTextViewSwitcher2 = this.mediaCounterTextView;
                    formatPluralStringComma = LocaleController.formatPluralString("GIFs", i8, new Object[0]);
                }
            } else {
                if (closestTab == 6) {
                    clippingTextViewSwitcher3 = this.mediaCounterTextView;
                    formatPluralString = LocaleController.formatPluralString("CommonGroups", this.userInfo.common_chats_count, new Object[0]);
                } else if (closestTab == 7) {
                    clippingTextViewSwitcher = this.mediaCounterTextView;
                    formatPluralStringComma = this.onlineTextView[1].getText();
                } else if (closestTab == 8) {
                    if (this.isBot) {
                        clippingTextViewSwitcher3 = this.mediaCounterTextView;
                        formatPluralString = this.sharedMediaLayout.getBotPreviewsSubtitle(false);
                    } else {
                        clippingTextViewSwitcher3 = this.mediaCounterTextView;
                        formatPluralString = LocaleController.formatPluralString("ProfileStoriesCount", this.sharedMediaLayout.getStoriesCount(closestTab), new Object[0]);
                    }
                } else if (closestTab == 13) {
                    clippingTextViewSwitcher = this.mediaCounterTextView;
                    formatPluralStringComma = this.sharedMediaLayout.getBotPreviewsSubtitle(true);
                } else if (closestTab == 9) {
                    clippingTextViewSwitcher3 = this.mediaCounterTextView;
                    formatPluralString = LocaleController.formatPluralString("ProfileStoriesArchiveCount", this.sharedMediaLayout.getStoriesCount(closestTab), new Object[0]);
                } else if (closestTab == 10) {
                    MessagesController.ChannelRecommendations channelRecommendations = MessagesController.getInstance(this.currentAccount).getChannelRecommendations(this.chatId);
                    clippingTextViewSwitcher2 = this.mediaCounterTextView;
                    formatPluralStringComma = LocaleController.formatPluralString("Channels", channelRecommendations == null ? 0 : channelRecommendations.chats.size() + channelRecommendations.more, new Object[0]);
                } else if (closestTab == 12) {
                    int messagesCount = getMessagesController().getSavedMessagesController().getMessagesCount(getDialogId());
                    AudioPlayerAlert.ClippingTextViewSwitcher clippingTextViewSwitcher5 = this.mediaCounterTextView;
                    formatPluralStringComma = LocaleController.formatPluralString("SavedMessagesCount", Math.max(1, messagesCount), new Object[0]);
                    clippingTextViewSwitcher = clippingTextViewSwitcher5;
                } else {
                    if (closestTab != 14) {
                        return;
                    }
                    clippingTextViewSwitcher = this.mediaCounterTextView;
                    formatPluralStringComma = LocaleController.formatPluralStringComma("ProfileGiftsCount", this.sharedMediaLayout.giftsContainer.getGiftsCount());
                }
                AudioPlayerAlert.ClippingTextViewSwitcher clippingTextViewSwitcher42 = clippingTextViewSwitcher3;
                formatPluralStringComma = formatPluralString;
                clippingTextViewSwitcher = clippingTextViewSwitcher42;
            }
            clippingTextViewSwitcher = clippingTextViewSwitcher2;
        }
        clippingTextViewSwitcher.setText(formatPluralStringComma);
    }
}
