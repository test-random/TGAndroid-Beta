package org.telegram.messenger;

import android.app.Activity;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.Bundle;
import android.os.Looper;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Pair;
import android.util.SparseArray;
import android.util.SparseBooleanArray;
import android.util.SparseIntArray;
import android.view.View;
import android.widget.FrameLayout;
import androidx.collection.LongSparseArray;
import androidx.core.graphics.ColorUtils;
import androidx.core.util.Consumer;
import j$.util.concurrent.ConcurrentHashMap;
import j$.util.function.Consumer;
import java.io.File;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;
import org.telegram.SQLite.SQLiteCursor;
import org.telegram.SQLite.SQLiteDatabase;
import org.telegram.SQLite.SQLiteException;
import org.telegram.SQLite.SQLitePreparedStatement;
import org.telegram.messenger.ChatObject;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.MessagesStorage;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.SavedMessagesController;
import org.telegram.messenger.Timer;
import org.telegram.messenger.Utilities;
import org.telegram.messenger.browser.Browser;
import org.telegram.messenger.support.LongSparseIntArray;
import org.telegram.messenger.support.LongSparseLongArray;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.NativeByteBuffer;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.SerializedData;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.tgnet.Vector;
import org.telegram.tgnet.tl.TL_account;
import org.telegram.tgnet.tl.TL_bots;
import org.telegram.tgnet.tl.TL_chatlists;
import org.telegram.tgnet.tl.TL_phone;
import org.telegram.ui.ActionBar.ActionBarLayout;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Cells.CheckBoxCell;
import org.telegram.ui.ChatActivity;
import org.telegram.ui.ChatRightsEditActivity;
import org.telegram.ui.Components.AlertsCreator;
import org.telegram.ui.Components.AnimatedEmojiDrawable;
import org.telegram.ui.Components.BulletinFactory;
import org.telegram.ui.Components.ImageUpdater;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.Premium.LimitReachedBottomSheet;
import org.telegram.ui.Components.Reactions.ReactionsLayoutInBubble;
import org.telegram.ui.DialogsActivity;
import org.telegram.ui.LaunchActivity;
import org.telegram.ui.PremiumPreviewFragment;
import org.telegram.ui.SecretMediaViewer;
import org.telegram.ui.Stars.StarsController;
import org.telegram.ui.Stories.StoriesController;
import org.telegram.ui.ThemeActivity;
import org.telegram.ui.bots.BotWebViewSheet;
import org.telegram.ui.bots.WebViewRequestProps;

public class MessagesController extends BaseController implements NotificationCenter.NotificationCenterDelegate {
    public static int DIALOG_FILTER_FLAG_ALL_CHATS = 31;
    public static int DIALOG_FILTER_FLAG_BOTS = 16;
    public static int DIALOG_FILTER_FLAG_CHANNELS = 8;
    public static int DIALOG_FILTER_FLAG_CHATLIST = 512;
    public static int DIALOG_FILTER_FLAG_CHATLIST_ADMIN = 1024;
    public static int DIALOG_FILTER_FLAG_CONTACTS = 1;
    public static int DIALOG_FILTER_FLAG_EXCLUDE_ARCHIVED = 128;
    public static int DIALOG_FILTER_FLAG_EXCLUDE_MUTED = 32;
    public static int DIALOG_FILTER_FLAG_EXCLUDE_READ = 64;
    public static int DIALOG_FILTER_FLAG_GROUPS = 4;
    public static int DIALOG_FILTER_FLAG_NON_CONTACTS = 2;
    public static int DIALOG_FILTER_FLAG_ONLY_ARCHIVED = 256;
    public static final int LOAD_AROUND_DATE = 4;
    public static final int LOAD_AROUND_MESSAGE = 3;
    public static final int LOAD_BACKWARD = 0;
    public static final int LOAD_FORWARD = 1;
    public static final int LOAD_FROM_UNREAD = 2;
    public static int PROMO_TYPE_OTHER = 2;
    public static int PROMO_TYPE_PROXY = 0;
    public static int PROMO_TYPE_PSA = 1;
    public static int UPDATE_MASK_ALL = 1050111;
    public static int UPDATE_MASK_AVATAR = 2;
    public static int UPDATE_MASK_CHAT = 8192;
    public static int UPDATE_MASK_CHAT_AVATAR = 8;
    public static int UPDATE_MASK_CHAT_MEMBERS = 32;
    public static int UPDATE_MASK_CHAT_NAME = 16;
    public static int UPDATE_MASK_CHECK = 65536;
    public static int UPDATE_MASK_EMOJI_INTERACTIONS = 262144;
    public static int UPDATE_MASK_EMOJI_STATUS = 524288;
    public static int UPDATE_MASK_MESSAGE_TEXT = 32768;
    public static int UPDATE_MASK_NAME = 1;
    public static int UPDATE_MASK_NEW_MESSAGE = 2048;
    public static int UPDATE_MASK_PHONE = 1024;
    public static int UPDATE_MASK_REACTIONS_READ = 1048576;
    public static int UPDATE_MASK_READ_DIALOG_MESSAGE = 256;
    public static int UPDATE_MASK_REORDER = 131072;
    public static int UPDATE_MASK_SELECT_DIALOG = 512;
    public static int UPDATE_MASK_SEND_STATE = 4096;
    public static int UPDATE_MASK_STATUS = 4;
    public static int UPDATE_MASK_USER_PHONE = 128;
    public static int UPDATE_MASK_USER_PRINT = 64;
    private static volatile long lastPasswordCheckTime = 0;
    private static volatile long lastThemeCheckTime = 0;
    public static int stableIdPointer = 100;
    private int DIALOGS_LOAD_TYPE_CACHE;
    private int DIALOGS_LOAD_TYPE_CHANNEL;
    private int DIALOGS_LOAD_TYPE_UNKNOWN;
    public int aboutLengthLimitDefault;
    public int aboutLengthLimitPremium;
    private final HashMap<Long, TLRPC.Chat> activeVoiceChatsMap;
    protected final ArrayList<TLRPC.Dialog> allDialogs;
    public boolean androidDisableRoundCamera2;
    public float animatedEmojisZoom;
    private final CacheFetcher<Integer, TLRPC.TL_help_appConfig> appConfigFetcher;
    public Set<String> authDomains;
    public int authorizationAutoconfirmPeriod;
    public boolean autoarchiveAvailable;
    public Set<String> autologinDomains;
    public String autologinToken;
    private TLRPC.messages_AvailableEffects availableEffects;
    public int availableMapProviders;
    public boolean backgroundConnection;
    public LongSparseIntArray blockePeers;
    public boolean blockedCountry;
    public boolean blockedEndReached;
    public int boostsChannelLevelMax;
    public long boostsPerSentGift;
    public int botPreviewMediasMax;
    public int botVerificationDescriptionLengthLimit;
    public int businessChatLinksLimit;
    public SparseIntArray businessFeaturesTypesToPosition;
    private CacheByChatsController cacheByChatsController;
    private HashMap<Long, ChannelRecommendations> cachedChannelRecommendations;
    private TLRPC.TL_exportedContactToken cachedContactToken;
    private LongSparseArray cachedIsUserPremiumBlocked;
    public int callConnectTimeout;
    public int callPacketTimeout;
    public int callReceiveTimeout;
    public int callRingTimeout;
    public boolean canEditFactcheck;
    public boolean canRevokePmInbox;
    public int captionLengthLimitDefault;
    public int captionLengthLimitPremium;
    private LongSparseArray channelAdmins;
    public int channelBgIconLevelMin;
    private ChannelBoostsController channelBoostsControler;
    public int channelCustomWallpaperLevelMin;
    public int channelEmojiStatusLevelMin;
    public int channelProfileIconLevelMin;
    public int channelRestrictSponsoredLevelMin;
    public boolean channelRevenueWithdrawalEnabled;
    private LongSparseArray channelViewsToSend;
    public int channelWallpaperLevelMin;
    public int channelsLimitDefault;
    public int channelsLimitPremium;
    private LongSparseIntArray channelsPts;
    public int chatReadMarkExpirePeriod;
    public int chatReadMarkSizeThreshold;
    private SparseArray<ChatlistUpdatesStat> chatlistFoldersUpdates;
    public int chatlistInvitesLimitDefault;
    public int chatlistInvitesLimitPremium;
    public int chatlistJoinedLimitDefault;
    public int chatlistJoinedLimitPremium;
    private int chatlistUpdatePeriod;
    private final ConcurrentHashMap<Long, TLRPC.Chat> chats;
    public int checkResetLangpack;
    private LongSparseArray checkingLastMessagesDialogs;
    private boolean checkingPromoInfo;
    private int checkingPromoInfoRequestId;
    private boolean checkingTosUpdate;
    private LongSparseArray clearingHistoryDialogs;
    public boolean collectDeviceStats;
    private TL_account.contentSettings contentSettings;
    private ArrayList<Utilities.Callback<TL_account.contentSettings>> contentSettingsCallbacks;
    private long contentSettingsLoadedTime;
    private boolean contentSettingsLoading;
    private ArrayList<Long> createdDialogIds;
    private ArrayList<Long> createdDialogMainThreadIds;
    private ArrayList<Long> createdScheduledDialogIds;
    private Runnable currentDeleteTaskRunnable;
    private LongSparseArray currentDeletingTaskMediaMids;
    private LongSparseArray currentDeletingTaskMids;
    private int currentDeletingTaskTime;
    public String dcDomainName;
    public LongSparseIntArray deletedHistory;
    private LongSparseArray deletingDialogs;
    private Comparator<TLRPC.Dialog> dialogComparator;
    private final Comparator<TLRPC.Dialog> dialogDateComparator;
    public ArrayList<DialogFilter> dialogFilters;
    public SparseArray<DialogFilter> dialogFiltersById;
    public int dialogFiltersChatsLimitDefault;
    public int dialogFiltersChatsLimitPremium;
    public int dialogFiltersLimitDefault;
    public int dialogFiltersLimitPremium;
    public boolean dialogFiltersLoaded;
    public int dialogFiltersPinnedLimitDefault;
    public int dialogFiltersPinnedLimitPremium;
    public LongSparseArray dialogMessage;
    public SparseArray<MessageObject> dialogMessagesByIds;
    public LongSparseArray dialogMessagesByRandomIds;
    private LongSparseArray dialogPhotos;
    public final SparseArray<ArrayList<TLRPC.Dialog>> dialogsByFolder;
    public ArrayList<TLRPC.Dialog> dialogsCanAddUsers;
    public ArrayList<TLRPC.Dialog> dialogsChannelsOnly;
    private SparseBooleanArray dialogsEndReached;
    public ArrayList<TLRPC.Dialog> dialogsForBlock;
    public ArrayList<TLRPC.Dialog> dialogsForward;
    public ArrayList<TLRPC.Dialog> dialogsGroupsOnly;
    private boolean dialogsInTransaction;
    public boolean dialogsLoaded;
    private int dialogsLoadedTillDate;
    public ArrayList<TLRPC.Dialog> dialogsMyChannels;
    public ArrayList<TLRPC.Dialog> dialogsMyGroups;
    public ArrayList<TLRPC.Dialog> dialogsServerOnly;
    public ArrayList<TLRPC.Dialog> dialogsUsersOnly;
    public LongSparseArray dialogs_dict;
    public ConcurrentHashMap<Long, Integer> dialogs_read_inbox_max;
    public ConcurrentHashMap<Long, Integer> dialogs_read_outbox_max;
    public HashSet<String> diceEmojies;
    public HashMap<String, DiceFrameSuccess> diceSuccess;
    public List<String> directPaymentsCurrency;
    public boolean disableBotFullscreenBlur;
    public Set<String> dismissedSuggestions;
    private final CacheFetcher<Integer, TLRPC.messages_AvailableEffects> effectsFetcher;
    public HashMap<Long, ArrayList<TLRPC.TL_sendMessageEmojiInteraction>> emojiInteractions;
    private final SharedPreferences emojiPreferences;
    public HashMap<String, EmojiSound> emojiSounds;
    private LongSparseArray emojiStatusUntilValues;
    public boolean enableJoined;
    private final ConcurrentHashMap<Integer, TLRPC.EncryptedChat> encryptedChats;
    public Set<String> exportGroupUri;
    public Set<String> exportPrivateUri;
    public Set<String> exportUri;
    private final LongSparseArray exportedChats;
    public int factcheckLengthLimit;
    public ArrayList<FaqSearchResult> faqSearchArray;
    public TLRPC.WebPage faqWebPage;
    public boolean filtersEnabled;
    public boolean firstGettingTask;
    public boolean folderTags;
    public int forumUpgradeParticipantsMin;
    public ArrayList<DialogFilter> frozenDialogFilters;
    private LongSparseArray fullChats;
    private LongSparseArray fullUsers;
    private boolean getDifferenceFirstSync;
    public boolean getfileExperimentalParams;
    private LongSparseArray gettingChatInviters;
    public boolean gettingDifference;
    private LongSparseArray gettingDifferenceChannels;
    private boolean gettingNewDeleteTask;
    private LongSparseArray gettingUnknownChannels;
    private LongSparseArray gettingUnknownDialogs;
    public String gifSearchBot;
    public ArrayList<String> gifSearchEmojies;
    public boolean giftAttachMenuIcon;
    public boolean giftTextFieldIcon;
    public long giveawayAddPeersMax;
    public long giveawayBoostsPerPremium;
    public long giveawayCountriesMax;
    public boolean giveawayGiftsPurchaseAvailable;
    public long giveawayPeriodMax;
    public int groupCallVideoMaxParticipants;
    private LongSparseArray groupCalls;
    private LongSparseArray groupCallsByChatId;
    public int groupCustomWallpaperLevelMin;
    public int groupEmojiStatusLevelMin;
    public int groupEmojiStickersLevelMin;
    public int groupProfileBgIconLevelMin;
    public int groupTranscribeLevelMin;
    public int groupWallpaperLevelMin;
    private boolean hasArchivedChats;
    private boolean hasStories;
    public int hiddenMembersGroupSizeMin;
    public ArrayList<Long> hiddenUndoChats;
    public ArrayList<TLRPC.RecentMeUrl> hintDialogs;
    public Set<String> ignoreRestrictionReasons;
    public volatile boolean ignoreSetOnline;
    public String imageSearchBot;
    private String installReferer;
    public int introDescriptionLengthLimit;
    public int introTitleLengthLimit;
    private boolean isLeftPromoChannel;
    private final ArrayList<Long> joiningToChannels;
    public boolean keepAliveService;
    public int largeQueueMaxActiveOperations;
    private int lastCheckPromoId;
    public int lastKnownSessionsCount;
    private int lastPrintingStringCount;
    private long lastPushRegisterSendTime;
    private LongSparseArray lastQuickReplyServerQueryTime;
    private LongSparseArray lastSavedServerQueryTime;
    private LongSparseArray lastScheduledServerQueryTime;
    private LongSparseArray lastServerQueryTime;
    private long lastStatusUpdateTime;
    private long lastViewsCheckTime;
    public String linkPrefix;
    private Runnable loadAppConfigRunnable;
    public LongSparseLongArray loadedFullChats;
    private HashSet<Long> loadedFullParticipants;
    private LongSparseLongArray loadedFullUsers;
    private boolean loadingArePaidReactionsAnonymous;
    private boolean loadingAvailableEffects;
    public boolean loadingBlockedPeers;
    private LongSparseIntArray loadingChannelAdmins;
    private SparseBooleanArray loadingDialogs;
    private HashSet<Long> loadingFullChats;
    private HashSet<Long> loadingFullParticipants;
    private HashSet<Long> loadingFullUsers;
    private HashSet<Long> loadingGroupCalls;
    private HashSet<Long> loadingIsUserPremiumBlocked;
    private int loadingNotificationSettings;
    private boolean loadingNotificationSignUpSettings;
    private boolean loadingPeerColors;
    private LongSparseArray loadingPeerSettings;
    private SparseIntArray loadingPinnedDialogs;
    private boolean loadingProfilePeerColors;
    private HashSet<Long> loadingReactionTags;
    private boolean loadingRemoteFilters;
    private boolean loadingSuggestedFilters;
    private boolean loadingUnreadDialogs;
    private boolean loggedDeviceStats;
    private final SharedPreferences mainPreferences;
    public String mapKey;
    public int mapProvider;
    public int maxBroadcastCount;
    public int maxCaptionLength;
    public int maxEditTime;
    public int maxFaveStickersCount;
    public int maxFolderPinnedDialogsCountDefault;
    public int maxFolderPinnedDialogsCountPremium;
    public int maxGroupCount;
    public int maxMegagroupCount;
    public int maxMessageLength;
    public int maxPinnedDialogsCountDefault;
    public int maxPinnedDialogsCountPremium;
    public int maxRecentGifsCount;
    public int maxRecentStickersCount;
    private SparseIntArray migratedChats;
    private boolean migratingDialogs;
    public int minGroupConvertSize;
    private LongSparseArray needShortPollChannels;
    private LongSparseArray needShortPollOnlines;
    public NewMessageCallback newMessageCallback;
    public boolean newNoncontactPeersRequirePremiumWithoutOwnpremium;
    private SparseIntArray nextDialogsCacheOffset;
    private int nextPromoInfoCheckTime;
    private int nextTosCheckTime;
    private final SharedPreferences notificationsPreferences;
    private final Runnable notifyTranscriptionAudioCooldownUpdate;
    private final ConcurrentHashMap<String, TLObject> objectsByUsernames;
    private boolean offlineSent;
    private Utilities.Callback<Boolean> onLoadedRemoteFilters;
    public ConcurrentHashMap<Long, Integer> onlinePrivacy;
    public Boolean paidReactionsAnonymous;
    public long paidReactionsAnonymousTime;
    private Runnable passwordCheckRunnable;
    public PeerColors peerColors;
    private final long peerDialogRequestTimeout;
    private final LongSparseArray peerDialogsRequested;
    private final HashSet<Pair<StarsController.MessageId, AtomicBoolean>> pendingReportMessageDelivery;
    public Set<String> pendingSuggestions;
    private LongSparseIntArray pendingUnreadCounter;
    public SparseArray<ImageUpdater> photoSuggestion;
    public int pmReadDateExpirePeriod;
    private LongSparseArray pollsToCheck;
    private int pollsToCheckSize;
    public boolean preloadFeaturedStickers;
    public String premiumBotUsername;
    public SparseIntArray premiumFeaturesTypesToPosition;
    public String premiumInvoiceSlug;
    public boolean premiumLocked;
    public String premiumManageSubscriptionUrl;
    public LongSparseArray printingStrings;
    public LongSparseArray printingStringsTypes;
    public ConcurrentHashMap<Long, ConcurrentHashMap<Integer, ArrayList<PrintingUser>>> printingUsers;
    public PeerColors profilePeerColors;
    private TLRPC.Dialog promoDialog;
    private long promoDialogId;
    public int promoDialogType;
    public String promoPsaMessage;
    public String promoPsaType;
    private String proxyDialogAddress;
    public int publicLinksLimitDefault;
    public int publicLinksLimitPremium;
    public boolean qrLoginCamera;
    public int quickRepliesLimit;
    public int quickReplyMessagesLimit;
    public int quoteLengthMax;
    public int ratingDecay;
    private LongSparseArray reactionTags;
    public int reactionsInChatMax;
    public int reactionsUniqMax;
    public int reactionsUserMaxDefault;
    public int reactionsUserMaxPremium;
    private ArrayList<ReadTask> readTasks;
    private LongSparseArray readTasksMap;
    private Runnable recentEmojiStatusUpdateRunnable;
    private long recentEmojiStatusUpdateRunnableTime;
    private long recentEmojiStatusUpdateRunnableTimeout;
    public int recommendedChannelsLimitDefault;
    public int recommendedChannelsLimitPremium;
    public boolean registeringForPush;
    private LongSparseArray reloadingMessages;
    private HashMap<String, ArrayList<MessageObject>> reloadingSavedWebpages;
    private LongSparseArray reloadingSavedWebpagesPending;
    private HashMap<String, ArrayList<MessageObject>> reloadingScheduledWebpages;
    private LongSparseArray reloadingScheduledWebpagesPending;
    private HashMap<String, ArrayList<MessageObject>> reloadingWebpages;
    private LongSparseArray reloadingWebpagesPending;
    public boolean remoteConfigLoaded;
    private ArrayList<ReadTask> repliesReadTasks;
    private final HashSet<StarsController.MessageId> reportedMessageDelivery;
    private Runnable requestIsUserPremiumBlockedRunnable;
    private boolean requestingContactToken;
    private TLRPC.messages_Dialogs resetDialogsAll;
    private TLRPC.TL_messages_peerDialogs resetDialogsPinned;
    private boolean resetingDialogs;
    public int revokeTimeLimit;
    public int revokeTimePmLimit;
    public int ringtoneDurationMax;
    public int ringtoneSizeMax;
    public int roundAudioBitrate;
    public int roundVideoBitrate;
    public int roundVideoSize;
    public boolean saveGifsWithStickers;
    public int savedDialogsPinnedLimitDefault;
    public int savedDialogsPinnedLimitPremium;
    public int savedGifsLimitDefault;
    public int savedGifsLimitPremium;
    public SavedMessagesController savedMessagesController;
    public boolean savedViewAsChats;
    public int secretWebpagePreview;
    public DialogFilter[] selectedDialogFilter;
    private LongSparseArray sendAsPeers;
    private final Runnable sendReportMessageDeliver;
    public LongSparseArray[] sendingTypings;
    private final HashSet<Long> sensitiveAgreed;
    private SparseBooleanArray serverDialogsEndReached;
    private LongSparseIntArray shortPollChannels;
    private LongSparseIntArray shortPollOnlines;
    public boolean showAnnualPerMonth;
    public boolean showFiltersTooltip;
    public int smallQueueMaxActiveOperations;
    public boolean smsjobsStickyNotificationEnabled;
    private DialogFilter sortingDialogFilter;
    public boolean sponsoredLinksInappAllow;
    private LongSparseArray sponsoredMessages;
    public boolean stargiftsBlocked;
    public int stargiftsConvertPeriodMax;
    public int stargiftsMessageLengthMax;
    public boolean starrefConnectAllowed;
    public int starrefMaxCommissionPermille;
    public int starrefMinCommissionPermille;
    public boolean starrefProgramAllowed;
    public Set<String> starrefStartParamPrefixes;
    public boolean starsGiftsEnabled;
    public boolean starsLocked;
    public long starsPaidPostAmountMax;
    public long starsPaidReactionAmountMax;
    public long starsRevenueWithdrawalMin;
    public long starsSubscriptionAmountMax;
    public float starsUsdSellRate1000;
    public float starsUsdWithdrawRate1000;
    private int statusRequest;
    private int statusSettingState;
    public int stealthModeCooldown;
    public int stealthModeFuture;
    public int stealthModePast;
    public int stickersFavedLimitDefault;
    public int stickersFavedLimitPremium;
    public long storiesChangelogUserId;
    public StoriesController storiesController;
    public String storiesEntities;
    public boolean storiesExportNopublicLink;
    public int storiesPinnedToTopCountMax;
    public String storiesPosting;
    public int storiesSentMonthlyLimitDefault;
    public int storiesSentMonthlyLimitPremium;
    public int storiesSentWeeklyLimitDefault;
    public int storiesSentWeeklyLimitPremium;
    public int storiesSuggestedReactionsLimitDefault;
    public int storiesSuggestedReactionsLimitPremium;
    public int storyCaptionLengthLimitDefault;
    public int storyCaptionLengthLimitPremium;
    public int storyExpiringLimitDefault;
    public int storyExpiringLimitPremium;
    public boolean storyQualityFull;
    public String storyVenueSearchBot;
    public boolean storyWeatherPreload;
    public boolean suggestContacts;
    public boolean suggestStickersApiOnly;
    public ArrayList<TLRPC.TL_dialogFilterSuggested> suggestedFilters;
    public String suggestedLangCode;
    public int telegramAntispamGroupSizeMin;
    public long telegramAntispamUserId;
    private Runnable themeCheckRunnable;
    private HashMap<String, ReadTask> threadsReadTasksMap;
    public String tonProxyAddress;
    private TopicsController topicsController;
    public int topicsPinnedLimit;
    public int totalBlockedCount;
    public int transcribeAudioTrialCooldownUntil;
    public int transcribeAudioTrialCurrentNumber;
    public int transcribeAudioTrialDurationMax;
    public int transcribeAudioTrialWeeklyNumber;
    public int transcribeButtonPressed;
    private TranslateController translateController;
    public UnconfirmedAuthController unconfirmedAuthController;
    public int unreadUnmutedDialogs;
    public int updateCheckDelay;
    private Comparator<TLRPC.Update> updatesComparator;
    private final LongSparseArray updatesQueueChannels;
    private ArrayList<TLRPC.Updates> updatesQueuePts;
    private ArrayList<TLRPC.Updates> updatesQueueQts;
    private ArrayList<TLRPC.Updates> updatesQueueSeq;
    private LongSparseLongArray updatesStartWaitTimeChannels;
    private long updatesStartWaitTimePts;
    private long updatesStartWaitTimeQts;
    private long updatesStartWaitTimeSeq;
    public boolean updatingState;
    public boolean uploadMarkupVideo;
    public int uploadMaxFileParts;
    public int uploadMaxFilePartsPremium;
    public float uploadPremiumSpeedupDownload;
    public int uploadPremiumSpeedupNotifyPeriod;
    public float uploadPremiumSpeedupUpload;
    private String uploadingAvatar;
    private HashMap<String, Object> uploadingThemes;
    public String uploadingWallpaper;
    public Theme.OverrideWallpaperInfo uploadingWallpaperInfo;
    private UserNameResolver userNameResolver;
    private final ConcurrentHashMap<Long, TLRPC.User> users;
    public String venueSearchBot;
    public boolean videoIgnoreAltDocuments;
    private ArrayList<Long> visibleDialogMainThreadIds;
    private ArrayList<Long> visibleScheduledDialogMainThreadIds;
    public String weatherSearchUsername;
    public Set<String> webAppAllowedProtocols;
    public int webFileDatacenterId;
    public String youtubePipType;
    private static volatile MessagesController[] Instance = new MessagesController[4];
    private static final Object[] lockObjects = new Object[4];

    public class AnonymousClass1 extends CacheFetcher<Integer, TLRPC.TL_help_appConfig> {
        AnonymousClass1() {
        }

        public static void lambda$getLocal$1(int r7, org.telegram.messenger.Utilities.Callback2 r8) {
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MessagesController.AnonymousClass1.lambda$getLocal$1(int, org.telegram.messenger.Utilities$Callback2):void");
        }

        public static void lambda$getRemote$0(Utilities.Callback4 callback4, TLObject tLObject, TLRPC.TL_error tL_error) {
            String str;
            int i;
            if (tLObject instanceof TLRPC.TL_help_appConfigNotModified) {
                Boolean bool = Boolean.TRUE;
                callback4.run(bool, null, 0L, bool);
                return;
            }
            if (tLObject instanceof TLRPC.TL_help_appConfig) {
                callback4.run(Boolean.FALSE, (TLRPC.TL_help_appConfig) tLObject, Long.valueOf(r5.hash), Boolean.TRUE);
                return;
            }
            StringBuilder sb = new StringBuilder();
            sb.append("getting appconfig error ");
            if (tL_error != null) {
                str = tL_error.code + " " + tL_error.text;
            } else {
                str = "";
            }
            sb.append(str);
            FileLog.e(sb.toString());
            callback4.run(Boolean.FALSE, null, 0L, Boolean.valueOf(tL_error == null || !((i = tL_error.code) == -2000 || i == -2001)));
        }

        public static void lambda$setLocal$2(int i, TLRPC.TL_help_appConfig tL_help_appConfig) {
            try {
                SQLiteDatabase database = MessagesStorage.getInstance(i).getDatabase();
                if (database != null) {
                    database.executeFast("DELETE FROM app_config").stepThis().dispose();
                    if (tL_help_appConfig != null) {
                        SQLitePreparedStatement executeFast = database.executeFast("INSERT INTO app_config VALUES(?)");
                        executeFast.requery();
                        NativeByteBuffer nativeByteBuffer = new NativeByteBuffer(tL_help_appConfig.getObjectSize());
                        tL_help_appConfig.serializeToStream(nativeByteBuffer);
                        executeFast.bindByteBuffer(1, nativeByteBuffer);
                        executeFast.step();
                        nativeByteBuffer.reuse();
                        executeFast.dispose();
                    }
                }
            } catch (Exception e) {
                FileLog.e(e);
            }
        }

        @Override
        public void getLocal(final int i, Integer num, final Utilities.Callback2<Long, TLRPC.TL_help_appConfig> callback2) {
            MessagesController.this.getMessagesStorage().getStorageQueue().postRunnable(new Runnable() {
                @Override
                public final void run() {
                    MessagesController.AnonymousClass1.lambda$getLocal$1(i, callback2);
                }
            });
        }

        @Override
        public void getRemote(int i, Integer num, long j, final Utilities.Callback4<Boolean, TLRPC.TL_help_appConfig, Long, Boolean> callback4) {
            TLRPC.TL_help_getAppConfig tL_help_getAppConfig = new TLRPC.TL_help_getAppConfig();
            tL_help_getAppConfig.hash = (int) j;
            MessagesController.this.getConnectionsManager().sendRequest(tL_help_getAppConfig, new RequestDelegate() {
                @Override
                public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                    MessagesController.AnonymousClass1.lambda$getRemote$0(Utilities.Callback4.this, tLObject, tL_error);
                }
            });
        }

        @Override
        public void setLocal(final int i, Integer num, final TLRPC.TL_help_appConfig tL_help_appConfig, long j) {
            MessagesStorage.getInstance(i).getStorageQueue().postRunnable(new Runnable() {
                @Override
                public final void run() {
                    MessagesController.AnonymousClass1.lambda$setLocal$2(i, tL_help_appConfig);
                }
            });
        }

        @Override
        public boolean useCache(Integer num) {
            return false;
        }
    }

    public class AnonymousClass2 implements MessagesLoadedCallback {
        final Bundle val$bundle;
        final BaseFragment val$fragment;
        final Browser.Progress val$progress;

        AnonymousClass2(Browser.Progress progress, BaseFragment baseFragment, Bundle bundle) {
            r2 = progress;
            r3 = baseFragment;
            r4 = bundle;
        }

        @Override
        public void onError() {
            r2.end();
            r3.presentFragment(new ChatActivity(r4));
        }

        @Override
        public void onMessagesLoaded(boolean z) {
            r2.end();
            r3.presentFragment(new ChatActivity(r4));
        }
    }

    public class AnonymousClass3 implements NotificationCenter.NotificationCenterDelegate {
        final MessagesLoadedCallback val$callback;
        final int val$classGuid;
        final int val$count;
        final long val$dialogId;
        final int val$finalMessageId;

        AnonymousClass3(int i, int i2, int i3, long j, MessagesLoadedCallback messagesLoadedCallback) {
            r2 = i;
            r3 = i2;
            r4 = i3;
            r5 = j;
            r7 = messagesLoadedCallback;
        }

        @Override
        public void didReceivedNotification(int i, int i2, Object... objArr) {
            int i3 = NotificationCenter.messagesDidLoadWithoutProcess;
            if (i != i3 || ((Integer) objArr[0]).intValue() != r2) {
                int i4 = NotificationCenter.loadingMessagesFailed;
                if (i == i4 && ((Integer) objArr[0]).intValue() == r2) {
                    MessagesController.this.getNotificationCenter().removeObserver(this, i3);
                    MessagesController.this.getNotificationCenter().removeObserver(this, i4);
                    MessagesLoadedCallback messagesLoadedCallback = r7;
                    if (messagesLoadedCallback != null) {
                        messagesLoadedCallback.onError();
                        return;
                    }
                    return;
                }
                return;
            }
            int intValue = ((Integer) objArr[1]).intValue();
            boolean booleanValue = ((Boolean) objArr[2]).booleanValue();
            boolean booleanValue2 = ((Boolean) objArr[3]).booleanValue();
            int intValue2 = ((Integer) objArr[4]).intValue();
            int i5 = r3;
            if (intValue < i5 / 2 && !booleanValue2 && booleanValue) {
                int i6 = r4;
                MessagesController.this.loadMessagesInternal(r5, 0L, false, i5, i6, 0, false, 0, r2, i6 != 0 ? 3 : 2, intValue2, 0, 0L, -1, 0, 0, 0, false, 0, true, false, false, null, 0L);
                return;
            }
            MessagesController.this.getNotificationCenter().removeObserver(this, i3);
            MessagesController.this.getNotificationCenter().removeObserver(this, NotificationCenter.loadingMessagesFailed);
            MessagesLoadedCallback messagesLoadedCallback2 = r7;
            if (messagesLoadedCallback2 != null) {
                messagesLoadedCallback2.onMessagesLoaded(booleanValue);
            }
        }
    }

    public class AnonymousClass4 extends CacheFetcher<Integer, TLRPC.messages_AvailableEffects> {
        AnonymousClass4() {
        }

        public static void lambda$getLocal$1(int r7, org.telegram.messenger.Utilities.Callback2 r8) {
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MessagesController.AnonymousClass4.lambda$getLocal$1(int, org.telegram.messenger.Utilities$Callback2):void");
        }

        public static void lambda$getRemote$0(Utilities.Callback4 callback4, TLObject tLObject, TLRPC.TL_error tL_error) {
            String str;
            int i;
            if (tLObject instanceof TLRPC.TL_messages_availableEffectsNotModified) {
                Boolean bool = Boolean.TRUE;
                callback4.run(bool, null, 0L, bool);
                return;
            }
            if (tLObject instanceof TLRPC.TL_messages_availableEffects) {
                callback4.run(Boolean.FALSE, (TLRPC.TL_messages_availableEffects) tLObject, Long.valueOf(r5.hash), Boolean.TRUE);
                return;
            }
            StringBuilder sb = new StringBuilder();
            sb.append("getting available effects error ");
            if (tL_error != null) {
                str = tL_error.code + " " + tL_error.text;
            } else {
                str = "";
            }
            sb.append(str);
            FileLog.e(sb.toString());
            callback4.run(Boolean.FALSE, null, 0L, Boolean.valueOf(tL_error == null || !((i = tL_error.code) == -2000 || i == -2001)));
        }

        public static void lambda$setLocal$2(int i, TLRPC.messages_AvailableEffects messages_availableeffects) {
            try {
                SQLiteDatabase database = MessagesStorage.getInstance(i).getDatabase();
                if (database != null) {
                    database.executeFast("DELETE FROM effects").stepThis().dispose();
                    if (messages_availableeffects != null) {
                        SQLitePreparedStatement executeFast = database.executeFast("INSERT INTO effects VALUES(?)");
                        executeFast.requery();
                        NativeByteBuffer nativeByteBuffer = new NativeByteBuffer(messages_availableeffects.getObjectSize());
                        messages_availableeffects.serializeToStream(nativeByteBuffer);
                        executeFast.bindByteBuffer(1, nativeByteBuffer);
                        executeFast.step();
                        nativeByteBuffer.reuse();
                        executeFast.dispose();
                    }
                }
            } catch (Exception e) {
                FileLog.e(e);
            }
        }

        @Override
        public boolean emitLocal(Integer num) {
            return true;
        }

        @Override
        public void getLocal(final int i, Integer num, final Utilities.Callback2<Long, TLRPC.messages_AvailableEffects> callback2) {
            MessagesController.this.getMessagesStorage().getStorageQueue().postRunnable(new Runnable() {
                @Override
                public final void run() {
                    MessagesController.AnonymousClass4.lambda$getLocal$1(i, callback2);
                }
            });
        }

        @Override
        public void getRemote(int i, Integer num, long j, final Utilities.Callback4<Boolean, TLRPC.messages_AvailableEffects, Long, Boolean> callback4) {
            TLRPC.TL_messages_getAvailableEffects tL_messages_getAvailableEffects = new TLRPC.TL_messages_getAvailableEffects();
            tL_messages_getAvailableEffects.hash = (int) j;
            MessagesController.this.getConnectionsManager().sendRequest(tL_messages_getAvailableEffects, new RequestDelegate() {
                @Override
                public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                    MessagesController.AnonymousClass4.lambda$getRemote$0(Utilities.Callback4.this, tLObject, tL_error);
                }
            });
        }

        @Override
        protected long getSavedLastTimeRequested(int i) {
            return MessagesController.this.mainPreferences.getLong("effects_last_" + i, 0L);
        }

        @Override
        protected boolean saveLastTimeRequested() {
            return true;
        }

        @Override
        public void setLocal(final int i, Integer num, final TLRPC.messages_AvailableEffects messages_availableeffects, long j) {
            MessagesStorage.getInstance(i).getStorageQueue().postRunnable(new Runnable() {
                @Override
                public final void run() {
                    MessagesController.AnonymousClass4.lambda$setLocal$2(i, messages_availableeffects);
                }
            });
        }

        @Override
        protected void setSavedLastTimeRequested(int i, long j) {
            MessagesController.this.mainPreferences.edit().putLong("effects_last_" + i, j).apply();
        }
    }

    public static class ChannelRecommendations {
        public final ArrayList<TLObject> chats = new ArrayList<>();
        public int more;
        public boolean wasPremium;

        public static boolean hasRecommendations(int i, long j) {
            return hasRecommendations(MessagesController.getInstance(i).getChannelRecommendations(j));
        }

        public static boolean hasRecommendations(ChannelRecommendations channelRecommendations) {
            return (channelRecommendations == null || channelRecommendations.chats.isEmpty()) ? false : true;
        }
    }

    public class ChatlistUpdatesStat {
        long lastRequestTime;
        TL_chatlists.TL_chatlists_chatlistUpdates lastValue;
        boolean loading;

        public ChatlistUpdatesStat() {
            this.loading = true;
        }

        public ChatlistUpdatesStat(TL_chatlists.TL_chatlists_chatlistUpdates tL_chatlists_chatlistUpdates) {
            this.loading = false;
            this.lastRequestTime = System.currentTimeMillis();
            this.lastValue = tL_chatlists_chatlistUpdates;
        }
    }

    public static class DialogFilter {
        private static int dialogFilterPointer = 10;
        public int color;
        public int flags;
        public int id;
        public int localId;
        public boolean locked;
        public String name;
        public int order;
        public volatile int pendingUnreadCount;
        public boolean title_noanimate;
        public int unreadCount;
        public ArrayList<TLRPC.MessageEntity> entities = new ArrayList<>();
        public ArrayList<Long> alwaysShow = new ArrayList<>();
        public ArrayList<Long> neverShow = new ArrayList<>();
        public LongSparseIntArray pinnedDialogs = new LongSparseIntArray();
        public ArrayList<TLRPC.Dialog> dialogs = new ArrayList<>();
        public ArrayList<TLRPC.Dialog> dialogsForward = new ArrayList<>();
        public ArrayList<TL_chatlists.TL_exportedChatlistInvite> invites = null;

        public DialogFilter() {
            int i = dialogFilterPointer;
            dialogFilterPointer = i + 1;
            this.localId = i;
        }

        public boolean alwaysShow(int i, TLRPC.Dialog dialog) {
            TLRPC.EncryptedChat encryptedChat;
            if (dialog == null) {
                return false;
            }
            long j = dialog.id;
            if (DialogObject.isEncryptedDialog(j) && (encryptedChat = MessagesController.getInstance(i).getEncryptedChat(Integer.valueOf(DialogObject.getEncryptedChatId(j)))) != null) {
                j = encryptedChat.user_id;
            }
            return this.alwaysShow.contains(Long.valueOf(j));
        }

        public boolean includesDialog(AccountInstance accountInstance, long j) {
            TLRPC.Dialog dialog = (TLRPC.Dialog) accountInstance.getMessagesController().dialogs_dict.get(j);
            if (dialog == null) {
                return false;
            }
            return includesDialog(accountInstance, j, dialog);
        }

        public boolean includesDialog(AccountInstance accountInstance, long j, TLRPC.Dialog dialog) {
            TLRPC.Chat chat;
            if (this.neverShow.contains(Long.valueOf(j))) {
                return false;
            }
            if (this.alwaysShow.contains(Long.valueOf(j))) {
                return true;
            }
            if (dialog.folder_id != 0 && (this.flags & MessagesController.DIALOG_FILTER_FLAG_EXCLUDE_ARCHIVED) != 0) {
                return false;
            }
            MessagesController messagesController = accountInstance.getMessagesController();
            ContactsController contactsController = accountInstance.getContactsController();
            if (((this.flags & MessagesController.DIALOG_FILTER_FLAG_EXCLUDE_MUTED) != 0 && messagesController.isDialogMuted(dialog.id, 0L) && dialog.unread_mentions_count == 0) || ((this.flags & MessagesController.DIALOG_FILTER_FLAG_EXCLUDE_READ) != 0 && messagesController.getDialogUnreadCount(dialog) == 0 && !dialog.unread_mark && dialog.unread_mentions_count == 0)) {
                return false;
            }
            if (j > 0) {
                TLRPC.User user = messagesController.getUser(Long.valueOf(j));
                if (user != null) {
                    if (user.bot) {
                        if ((this.flags & MessagesController.DIALOG_FILTER_FLAG_BOTS) != 0) {
                            return true;
                        }
                    } else if (user.self || user.contact || contactsController.isContact(j)) {
                        if ((this.flags & MessagesController.DIALOG_FILTER_FLAG_CONTACTS) != 0) {
                            return true;
                        }
                    } else if ((this.flags & MessagesController.DIALOG_FILTER_FLAG_NON_CONTACTS) != 0) {
                        return true;
                    }
                }
            } else if (j < 0 && (chat = messagesController.getChat(Long.valueOf(-j))) != null) {
                if (!ChatObject.isChannel(chat) || chat.megagroup) {
                    if ((this.flags & MessagesController.DIALOG_FILTER_FLAG_GROUPS) != 0) {
                        return true;
                    }
                } else if ((this.flags & MessagesController.DIALOG_FILTER_FLAG_CHANNELS) != 0) {
                    return true;
                }
            }
            return false;
        }

        public boolean isChatlist() {
            return (this.flags & MessagesController.DIALOG_FILTER_FLAG_CHATLIST) > 0;
        }

        public boolean isDefault() {
            return this.id == 0;
        }

        public boolean isMyChatlist() {
            return isChatlist() && (this.flags & MessagesController.DIALOG_FILTER_FLAG_CHATLIST_ADMIN) > 0;
        }
    }

    public class DialogPhotos {
        public static final int STEP = 80;
        public final long dialogId;
        private boolean loading;
        public final ArrayList<TLRPC.Photo> photos = new ArrayList<>();
        public boolean fromCache = true;
        public boolean loaded = false;

        public DialogPhotos(long j) {
            this.dialogId = j;
        }

        public void lambda$load$0(TLRPC.photos_Photos photos_photos, int i, int i2) {
            MessagesController.this.putUsers(photos_photos.users, false);
            onLoaded(i, i2, photos_photos);
        }

        public void lambda$load$1(final int i, final int i2, TLObject tLObject, TLRPC.TL_error tL_error) {
            if (tL_error == null) {
                final TLRPC.photos_Photos photos_photos = (TLRPC.photos_Photos) tLObject;
                MessagesController.this.getMessagesStorage().putUsersAndChats(photos_photos.users, null, true, true);
                AndroidUtilities.runOnUIThread(new Runnable() {
                    @Override
                    public final void run() {
                        MessagesController.DialogPhotos.this.lambda$load$0(photos_photos, i, i2);
                    }
                });
            }
        }

        public void lambda$load$2(TLRPC.messages_Messages messages_messages, int i, int i2) {
            TLRPC.Photo photo;
            MessagesController.this.putUsers(messages_messages.users, false);
            MessagesController.this.putChats(messages_messages.chats, false);
            TLRPC.TL_photos_photos tL_photos_photos = new TLRPC.TL_photos_photos();
            tL_photos_photos.count = messages_messages.count;
            for (int i3 = 0; i3 < messages_messages.messages.size(); i3++) {
                TLRPC.MessageAction messageAction = messages_messages.messages.get(i3).action;
                if (messageAction != null && (photo = messageAction.photo) != null) {
                    tL_photos_photos.photos.add(photo);
                }
            }
            onLoaded(i, i2, tL_photos_photos);
        }

        public void lambda$load$3(final int i, final int i2, TLObject tLObject, TLRPC.TL_error tL_error) {
            if (tL_error == null) {
                final TLRPC.messages_Messages messages_messages = (TLRPC.messages_Messages) tLObject;
                MessagesController.this.getMessagesStorage().putUsersAndChats(messages_messages.users, messages_messages.chats, true, true);
                AndroidUtilities.runOnUIThread(new Runnable() {
                    @Override
                    public final void run() {
                        MessagesController.DialogPhotos.this.lambda$load$2(messages_messages, i, i2);
                    }
                });
            }
        }

        public void lambda$loadCache$4(int i, HashMap hashMap) {
            this.photos.clear();
            for (int i2 = 0; i2 < i; i2++) {
                this.photos.add(null);
            }
            for (Map.Entry entry : hashMap.entrySet()) {
                this.photos.set(((Integer) entry.getKey()).intValue(), (TLRPC.Photo) entry.getValue());
            }
            MessagesController.this.getNotificationCenter().lambda$postNotificationNameOnUIThread$1(NotificationCenter.dialogPhotosUpdate, this);
            load(0, 80);
        }

        public void lambda$loadCache$5() {
            int i;
            int readInt32;
            int i2 = 0;
            SQLiteDatabase database = MessagesController.this.getMessagesStorage().getDatabase();
            final HashMap hashMap = new HashMap();
            SQLiteCursor sQLiteCursor = null;
            try {
                try {
                    Locale locale = Locale.US;
                    SQLiteCursor queryFinalized = database.queryFinalized(String.format(locale, "SELECT count FROM dialog_photos_count WHERE uid = %d", Long.valueOf(this.dialogId)), new Object[0]);
                    try {
                        try {
                            i = queryFinalized.next() ? queryFinalized.intValue(0) : 0;
                            try {
                                queryFinalized.dispose();
                                try {
                                    SQLiteCursor queryFinalized2 = database.queryFinalized(String.format(locale, "SELECT num, data FROM dialog_photos WHERE uid = %d", Long.valueOf(this.dialogId)), new Object[0]);
                                    while (queryFinalized2.next()) {
                                        try {
                                            int intValue = queryFinalized2.intValue(0);
                                            NativeByteBuffer byteBufferValue = queryFinalized2.byteBufferValue(1);
                                            TLRPC.Photo TLdeserialize = (byteBufferValue == null || (readInt32 = byteBufferValue.readInt32(false)) == 1450380236) ? null : TLRPC.Photo.TLdeserialize(byteBufferValue, readInt32, false);
                                            if (TLdeserialize != null) {
                                                i = Math.max(intValue + 1, i);
                                                hashMap.put(Integer.valueOf(intValue), TLdeserialize);
                                            }
                                        } catch (Exception unused) {
                                            sQLiteCursor = queryFinalized2;
                                            i2 = i;
                                            if (sQLiteCursor != null) {
                                                sQLiteCursor.dispose();
                                            }
                                            i = i2;
                                            final int max = Math.max(i, hashMap.size());
                                            AndroidUtilities.runOnUIThread(new Runnable() {
                                                @Override
                                                public final void run() {
                                                    MessagesController.DialogPhotos.this.lambda$loadCache$4(max, hashMap);
                                                }
                                            });
                                        } catch (Throwable th) {
                                            th = th;
                                            sQLiteCursor = queryFinalized2;
                                            if (sQLiteCursor != null) {
                                                sQLiteCursor.dispose();
                                            }
                                            throw th;
                                        }
                                    }
                                    queryFinalized2.dispose();
                                } catch (Exception unused2) {
                                }
                            } catch (Exception unused3) {
                                sQLiteCursor = queryFinalized;
                            }
                        } catch (Throwable th2) {
                            th = th2;
                            sQLiteCursor = queryFinalized;
                        }
                    } catch (Exception unused4) {
                        sQLiteCursor = queryFinalized;
                    }
                } catch (Throwable th3) {
                    th = th3;
                }
            } catch (Exception unused5) {
            }
            final int max2 = Math.max(i, hashMap.size());
            AndroidUtilities.runOnUIThread(new Runnable() {
                @Override
                public final void run() {
                    MessagesController.DialogPhotos.this.lambda$loadCache$4(max2, hashMap);
                }
            });
        }

        public void lambda$saveCache$6() {
            SQLiteDatabase database = MessagesController.this.getMessagesStorage().getDatabase();
            SQLitePreparedStatement sQLitePreparedStatement = null;
            try {
                database.executeFast("DELETE FROM dialog_photos WHERE uid = " + this.dialogId).stepThis().dispose();
                database.executeFast("DELETE FROM dialog_photos_count WHERE uid = " + this.dialogId).stepThis().dispose();
                database.executeFast("REPLACE INTO dialog_photos_count VALUES(" + this.dialogId + ", " + this.photos.size() + ")").stepThis().dispose();
                sQLitePreparedStatement = database.executeFast("REPLACE INTO dialog_photos VALUES(?, ?, ?, ?)");
                for (int i = 0; i < this.photos.size(); i++) {
                    TLRPC.Photo photo = this.photos.get(i);
                    if (photo != null) {
                        if (photo.file_reference == null) {
                            photo.file_reference = new byte[0];
                        }
                        sQLitePreparedStatement.requery();
                        NativeByteBuffer nativeByteBuffer = new NativeByteBuffer(photo.getObjectSize());
                        photo.serializeToStream(nativeByteBuffer);
                        sQLitePreparedStatement.bindLong(1, this.dialogId);
                        sQLitePreparedStatement.bindLong(2, photo.id);
                        sQLitePreparedStatement.bindInteger(3, i);
                        sQLitePreparedStatement.bindByteBuffer(4, nativeByteBuffer);
                        sQLitePreparedStatement.step();
                        nativeByteBuffer.reuse();
                    }
                }
                sQLitePreparedStatement.dispose();
            } catch (Exception unused) {
                if (sQLitePreparedStatement != null) {
                    sQLitePreparedStatement.dispose();
                }
            } catch (Throwable th) {
                if (sQLitePreparedStatement != null) {
                    sQLitePreparedStatement.dispose();
                }
                throw th;
            }
        }

        private void onLoaded(int i, int i2, TLRPC.photos_Photos photos_photos) {
            boolean z = this.loaded;
            this.loading = false;
            this.loaded = true;
            this.fromCache = false;
            int max = Math.max(photos_photos.count, photos_photos.photos.size());
            photos_photos.count = max;
            boolean z2 = max != this.photos.size() || i + i2 > this.photos.size();
            if (!z2) {
                int i3 = 0;
                while (true) {
                    if (i3 >= photos_photos.photos.size()) {
                        break;
                    }
                    int i4 = i + i3;
                    if (this.photos.get(i4) != null && this.photos.get(i4).id != photos_photos.photos.get(i3).id) {
                        z2 = true;
                        break;
                    }
                    i3++;
                }
            }
            if (z2) {
                this.photos.clear();
                for (int i5 = 0; i5 < photos_photos.count; i5++) {
                    int i6 = i5 - i;
                    this.photos.add((i6 < 0 || i6 >= photos_photos.photos.size()) ? null : photos_photos.photos.get(i6));
                }
            } else {
                for (int i7 = 0; i7 < photos_photos.photos.size(); i7++) {
                    this.photos.set(i + i7, photos_photos.photos.get(i7));
                }
            }
            saveCache();
            MessagesController.this.getNotificationCenter().lambda$postNotificationNameOnUIThread$1(NotificationCenter.dialogPhotosUpdate, this);
            if (z || i != 0 || i2 >= this.photos.size() || this.photos.size() - i2 <= 80) {
                return;
            }
            load(this.photos.size() - 80, 80);
        }

        private boolean removePhotoInternal(long j) {
            int i = 0;
            boolean z = false;
            while (i < this.photos.size()) {
                TLRPC.Photo photo = this.photos.get(i);
                if (photo != null && photo.id == j) {
                    this.photos.remove(i);
                    i--;
                    z = true;
                }
                i++;
            }
            return z;
        }

        private void saveCache() {
            MessagesController.this.getMessagesStorage().getStorageQueue().postRunnable(new Runnable() {
                @Override
                public final void run() {
                    MessagesController.DialogPhotos.this.lambda$saveCache$6();
                }
            });
        }

        public void addPhotoAtStart(TLRPC.Photo photo) {
        }

        public int getCount() {
            return this.photos.size();
        }

        public void load(final int i, final int i2) {
            if (this.loading || i2 <= 0 || i < 0) {
                return;
            }
            this.loading = true;
            long j = this.dialogId;
            if (j < 0) {
                TLRPC.TL_messages_search tL_messages_search = new TLRPC.TL_messages_search();
                tL_messages_search.filter = new TLRPC.TL_inputMessagesFilterChatPhotos();
                tL_messages_search.add_offset = i;
                tL_messages_search.limit = i2;
                tL_messages_search.offset_id = 0;
                tL_messages_search.q = "";
                tL_messages_search.peer = MessagesController.this.getInputPeer(this.dialogId);
                MessagesController.this.getConnectionsManager().sendRequest(tL_messages_search, new RequestDelegate() {
                    @Override
                    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                        MessagesController.DialogPhotos.this.lambda$load$3(i, i2, tLObject, tL_error);
                    }
                });
                return;
            }
            TLRPC.User user = MessagesController.this.getUser(Long.valueOf(j));
            if (user == null) {
                this.loading = false;
                return;
            }
            TLRPC.TL_photos_getUserPhotos tL_photos_getUserPhotos = new TLRPC.TL_photos_getUserPhotos();
            tL_photos_getUserPhotos.offset = i;
            tL_photos_getUserPhotos.limit = i2;
            tL_photos_getUserPhotos.max_id = 0L;
            tL_photos_getUserPhotos.user_id = MessagesController.this.getInputUser(user);
            MessagesController.this.getConnectionsManager().sendRequest(tL_photos_getUserPhotos, new RequestDelegate() {
                @Override
                public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                    MessagesController.DialogPhotos.this.lambda$load$1(i, i2, tLObject, tL_error);
                }
            });
        }

        public void loadAfter(int i, boolean z) {
            int i2 = 0;
            if (this.photos.isEmpty()) {
                load(0, 80);
                return;
            }
            if (i < 0) {
                i += this.photos.size();
            }
            if (i >= this.photos.size()) {
                i -= this.photos.size();
            }
            if (i < 0 || i >= this.photos.size()) {
                return;
            }
            for (int i3 = 0; i3 < this.photos.size(); i3++) {
                if (this.photos.get(i3) == null) {
                    if (z) {
                        while (this.photos.get(i) != null) {
                            i++;
                            if (i >= this.photos.size()) {
                                i = 0;
                            }
                        }
                        while (i2 <= 80) {
                            int i4 = i + i2;
                            if (i4 >= this.photos.size() || this.photos.get(i4) != null) {
                                break;
                            } else {
                                i2++;
                            }
                        }
                        if (i2 <= 0) {
                            return;
                        }
                    } else {
                        while (this.photos.get(i) != null) {
                            i--;
                            if (i < 0) {
                                i = this.photos.size() - 1;
                            }
                        }
                        while (i2 <= 80) {
                            int i5 = i - i2;
                            if (i5 < 0 || this.photos.get(i5) != null) {
                                break;
                            } else {
                                i2++;
                            }
                        }
                        if (i2 <= 0) {
                            return;
                        } else {
                            i -= i2;
                        }
                    }
                    load(i, i2);
                    return;
                }
            }
        }

        public void loadCache() {
            MessagesController.this.getMessagesStorage().getStorageQueue().postRunnable(new Runnable() {
                @Override
                public final void run() {
                    MessagesController.DialogPhotos.this.lambda$loadCache$5();
                }
            });
        }

        public void moveToStart(int i) {
            if (i < 0 || i >= this.photos.size()) {
                return;
            }
            ArrayList<TLRPC.Photo> arrayList = this.photos;
            arrayList.add(0, arrayList.remove(i));
            saveCache();
            MessagesController.this.getNotificationCenter().lambda$postNotificationNameOnUIThread$1(NotificationCenter.dialogPhotosUpdate, this);
        }

        public void removePhoto(long j) {
            if (removePhotoInternal(j)) {
                saveCache();
                MessagesController.this.getNotificationCenter().lambda$postNotificationNameOnUIThread$1(NotificationCenter.dialogPhotosUpdate, this);
            }
        }

        public void reset() {
            this.photos.clear();
            this.fromCache = true;
            saveCache();
        }
    }

    public static class DiceFrameSuccess {
        public int frame;
        public int num;

        public DiceFrameSuccess(int i, int i2) {
            this.frame = i;
            this.num = i2;
        }

        public boolean equals(Object obj) {
            if (!(obj instanceof DiceFrameSuccess)) {
                return false;
            }
            DiceFrameSuccess diceFrameSuccess = (DiceFrameSuccess) obj;
            return this.frame == diceFrameSuccess.frame && this.num == diceFrameSuccess.num;
        }
    }

    public static class EmojiSound {
        public long accessHash;
        public byte[] fileReference;
        public long id;

        public EmojiSound(long j, long j2, String str) {
            this.id = j;
            this.accessHash = j2;
            this.fileReference = Base64.decode(str, 8);
        }

        public EmojiSound(long j, long j2, byte[] bArr) {
            this.id = j;
            this.accessHash = j2;
            this.fileReference = bArr;
        }

        public boolean equals(Object obj) {
            if (!(obj instanceof EmojiSound)) {
                return false;
            }
            EmojiSound emojiSound = (EmojiSound) obj;
            return this.id == emojiSound.id && this.accessHash == emojiSound.accessHash && Arrays.equals(this.fileReference, emojiSound.fileReference);
        }
    }

    public interface ErrorDelegate {
        boolean run(TLRPC.TL_error tL_error);
    }

    public static class FaqSearchResult {
        public int num;
        public String[] path;
        public String title;
        public String url;

        public FaqSearchResult(String str, String[] strArr, String str2) {
            this.title = str;
            this.path = strArr;
            this.url = str2;
        }

        public boolean equals(Object obj) {
            if (obj instanceof FaqSearchResult) {
                return this.title.equals(((FaqSearchResult) obj).title);
            }
            return false;
        }

        public String toString() {
            SerializedData serializedData = new SerializedData();
            serializedData.writeInt32(this.num);
            int i = 0;
            serializedData.writeInt32(0);
            serializedData.writeString(this.title);
            String[] strArr = this.path;
            serializedData.writeInt32(strArr != null ? strArr.length : 0);
            if (this.path != null) {
                while (true) {
                    String[] strArr2 = this.path;
                    if (i >= strArr2.length) {
                        break;
                    }
                    serializedData.writeString(strArr2[i]);
                    i++;
                }
            }
            serializedData.writeString(this.url);
            return Utilities.bytesToHex(serializedData.toByteArray());
        }
    }

    public interface IsInChatCheckedCallback {
        void run(boolean z, TLRPC.TL_chatAdminRights tL_chatAdminRights, String str);
    }

    public interface MessagesLoadedCallback {
        void onError();

        void onMessagesLoaded(boolean z);
    }

    public interface NewMessageCallback {
        boolean onMessageReceived(TLRPC.Message message);
    }

    public static class PeerColor {
        public int channelLvl;
        private final int[] colors = new int[6];
        private final int[] darkColors = new int[6];
        public int groupLvl;
        public boolean hidden;
        public int id;
        public boolean isDefaultName;

        public static PeerColor fromString(String str) {
            int i;
            int i2;
            int i3;
            int indexOf;
            if (str != null && !str.isEmpty()) {
                if (str.charAt(0) == '#') {
                    boolean z = str.length() > 1 && str.charAt(1) == 'H';
                    int i4 = z ? 2 : 1;
                    if (str.length() <= i4 || str.charAt(i4) != '[' || (indexOf = str.indexOf(93)) <= i4) {
                        i = i4;
                        i2 = 0;
                        i3 = 0;
                    } else {
                        String substring = str.substring(i4 + 1, indexOf);
                        if (substring.contains(",")) {
                            String[] split = substring.split(",");
                            i3 = Utilities.parseInt((CharSequence) split[0]).intValue();
                            i2 = Utilities.parseInt((CharSequence) split[1]).intValue();
                        } else {
                            i3 = Utilities.parseInt((CharSequence) substring).intValue();
                            i2 = 0;
                        }
                        i = indexOf + 1;
                    }
                    int indexOf2 = str.indexOf(123);
                    if (indexOf2 < 0) {
                        return null;
                    }
                    try {
                        PeerColor peerColor = new PeerColor();
                        peerColor.id = Utilities.parseInt((CharSequence) str.substring(i, indexOf2)).intValue();
                        peerColor.hidden = z;
                        peerColor.channelLvl = i3;
                        peerColor.groupLvl = i2;
                        String[] split2 = str.substring(indexOf2 + 1, str.length() - 1).split("@");
                        String[] split3 = split2[0].split(",");
                        int i5 = 0;
                        while (i5 < 6) {
                            int[] iArr = peerColor.colors;
                            int i6 = i5 + 1;
                            iArr[i5] = split3.length >= i6 ? Utilities.parseInt((CharSequence) split3[i5]).intValue() : iArr[0];
                            i5 = i6;
                        }
                        if (split2.length >= 2) {
                            String[] split4 = split2[1].split(",");
                            int i7 = 0;
                            while (i7 < 6) {
                                int[] iArr2 = peerColor.darkColors;
                                int i8 = i7 + 1;
                                iArr2[i7] = split4.length >= i8 ? Utilities.parseInt((CharSequence) split4[i7]).intValue() : iArr2[0];
                                i7 = i8;
                            }
                        } else {
                            for (int i9 = 0; i9 < 6; i9++) {
                                peerColor.darkColors[i9] = peerColor.colors[i9];
                            }
                        }
                        return peerColor;
                    } catch (Exception e) {
                        FileLog.e(e);
                    }
                }
            }
            return null;
        }

        public static PeerColor fromTL(TLRPC.TL_help_peerColorOption tL_help_peerColorOption) {
            if (tL_help_peerColorOption == null) {
                return null;
            }
            PeerColor peerColor = new PeerColor();
            peerColor.id = tL_help_peerColorOption.color_id;
            peerColor.hidden = tL_help_peerColorOption.hidden;
            int i = tL_help_peerColorOption.flags;
            if ((i & 8) != 0) {
                peerColor.channelLvl = tL_help_peerColorOption.channel_min_level;
            }
            if ((i & 16) != 0) {
                peerColor.groupLvl = tL_help_peerColorOption.group_min_level;
            }
            System.arraycopy(optionToColors(tL_help_peerColorOption.colors), 0, peerColor.colors, 0, 6);
            System.arraycopy(optionToColors(tL_help_peerColorOption.dark_colors), 0, peerColor.darkColors, 0, 6);
            return peerColor;
        }

        public static int[] optionToColors(TLRPC.help_PeerColorSet help_peercolorset) {
            ArrayList<Integer> arrayList;
            int[] iArr = new int[6];
            iArr[0] = 0;
            iArr[1] = 0;
            iArr[2] = 0;
            iArr[3] = 0;
            iArr[4] = 0;
            iArr[5] = 0;
            if (help_peercolorset instanceof TLRPC.TL_help_peerColorSet) {
                arrayList = ((TLRPC.TL_help_peerColorSet) help_peercolorset).colors;
            } else if (help_peercolorset instanceof TLRPC.TL_help_peerColorProfileSet) {
                TLRPC.TL_help_peerColorProfileSet tL_help_peerColorProfileSet = (TLRPC.TL_help_peerColorProfileSet) help_peercolorset;
                ArrayList<Integer> arrayList2 = tL_help_peerColorProfileSet.palette_colors;
                ArrayList<Integer> arrayList3 = tL_help_peerColorProfileSet.bg_colors;
                ArrayList<Integer> arrayList4 = tL_help_peerColorProfileSet.story_colors;
                ArrayList<Integer> arrayList5 = new ArrayList<>();
                if (arrayList2 != null) {
                    for (int i = 0; i < Math.min(2, arrayList2.size()); i++) {
                        arrayList5.add(arrayList2.get(i));
                    }
                }
                if (arrayList3 != null) {
                    for (int i2 = 0; i2 < Math.min(2, arrayList3.size()); i2++) {
                        arrayList5.add(arrayList3.get(i2));
                    }
                }
                if (arrayList4 != null) {
                    for (int i3 = 0; i3 < Math.min(2, arrayList4.size()); i3++) {
                        arrayList5.add(arrayList4.get(i3));
                    }
                }
                arrayList = arrayList5;
            } else {
                arrayList = null;
            }
            if (arrayList != null) {
                if (arrayList.size() > 0) {
                    Arrays.fill(iArr, arrayList.get(0).intValue() | (-16777216));
                }
                for (int i4 = 0; i4 < Math.min(6, arrayList.size()); i4++) {
                    iArr[i4] = arrayList.get(i4).intValue() | (-16777216);
                }
            }
            return iArr;
        }

        public void appendString(StringBuilder sb) {
            sb.append("#");
            if (this.hidden) {
                sb.append("H");
            }
            if (this.channelLvl != 0 || this.groupLvl != 0) {
                sb.append("[");
                sb.append(this.channelLvl);
                sb.append(",");
                sb.append(this.groupLvl);
                sb.append("]");
            }
            sb.append(this.id);
            sb.append("{");
            sb.append(this.colors[0]);
            int[] iArr = this.colors;
            if (iArr[1] != iArr[0]) {
                sb.append(",");
                sb.append(this.colors[1]);
                int[] iArr2 = this.colors;
                int i = iArr2[2];
                int i2 = iArr2[0];
                if (i != i2 || iArr2[3] != i2) {
                    sb.append(",");
                    sb.append(this.colors[2]);
                    sb.append(",");
                    sb.append(this.colors[3]);
                    int[] iArr3 = this.colors;
                    int i3 = iArr3[4];
                    int i4 = iArr3[0];
                    if (i3 != i4 || iArr3[5] != i4) {
                        sb.append(",");
                        sb.append(this.colors[4]);
                        sb.append(",");
                        sb.append(this.colors[5]);
                    }
                }
            }
            int[] iArr4 = this.darkColors;
            int i5 = iArr4[0];
            int[] iArr5 = this.colors;
            if (i5 != iArr5[0] || iArr4[1] != iArr5[1] || iArr4[2] != iArr5[2]) {
                sb.append("@");
                sb.append(this.darkColors[0]);
                int[] iArr6 = this.darkColors;
                if (iArr6[1] != iArr6[0]) {
                    sb.append(",");
                    sb.append(this.darkColors[1]);
                    int[] iArr7 = this.darkColors;
                    int i6 = iArr7[2];
                    int i7 = iArr7[0];
                    if (i6 != i7 || iArr7[3] != i7) {
                        sb.append(",");
                        sb.append(this.darkColors[2]);
                        sb.append(",");
                        sb.append(this.darkColors[3]);
                        int[] iArr8 = this.darkColors;
                        int i8 = iArr8[4];
                        int i9 = iArr8[0];
                        if (i8 != i9 || iArr8[5] != i9) {
                            sb.append(",");
                            sb.append(this.darkColors[4]);
                            sb.append(",");
                            sb.append(this.darkColors[5]);
                        }
                    }
                }
            }
            sb.append("}");
        }

        public int getAvatarColor1() {
            return ColorUtils.blendARGB(getBgColor2(false), getStoryColor2(false), 0.5f);
        }

        public int getAvatarColor2() {
            return ColorUtils.blendARGB(getBgColor1(false), getStoryColor1(false), 0.5f);
        }

        public int getBgColor1(boolean z) {
            return hasColor6(z) ? getColor3(z) : getColor2(z);
        }

        public int getBgColor2(boolean z) {
            return hasColor6(z) ? getColor4(z) : getColor2(z);
        }

        public int getColor(int i, Theme.ResourcesProvider resourcesProvider) {
            int i2;
            if (i < 0 || i > 5) {
                return 0;
            }
            if (!this.isDefaultName || (i2 = this.id) < 0 || i2 >= 7) {
                return (resourcesProvider != null ? resourcesProvider.isDark() : Theme.isCurrentThemeDark() ? this.darkColors : this.colors)[i];
            }
            return Theme.getColor(Theme.keys_avatar_nameInMessage[i2], resourcesProvider);
        }

        public int getColor1() {
            return (Theme.isCurrentThemeDark() ? this.darkColors : this.colors)[0];
        }

        public int getColor1(boolean z) {
            return (z ? this.darkColors : this.colors)[0];
        }

        public int getColor2() {
            return (Theme.isCurrentThemeDark() ? this.darkColors : this.colors)[1];
        }

        public int getColor2(boolean z) {
            return (z ? this.darkColors : this.colors)[1];
        }

        public int getColor3() {
            return (Theme.isCurrentThemeDark() ? this.darkColors : this.colors)[2];
        }

        public int getColor3(boolean z) {
            return (z ? this.darkColors : this.colors)[2];
        }

        public int getColor4() {
            return (Theme.isCurrentThemeDark() ? this.darkColors : this.colors)[3];
        }

        public int getColor4(boolean z) {
            return (z ? this.darkColors : this.colors)[3];
        }

        public int getColor5() {
            return (Theme.isCurrentThemeDark() ? this.darkColors : this.colors)[4];
        }

        public int getColor5(boolean z) {
            return (z ? this.darkColors : this.colors)[4];
        }

        public int getColor6(boolean z) {
            return (z ? this.darkColors : this.colors)[5];
        }

        public int getLvl(boolean z) {
            return z ? this.groupLvl : this.channelLvl;
        }

        public int getStoryColor1(boolean z) {
            return hasColor6(z) ? getColor5(z) : getColor3(z);
        }

        public int getStoryColor2(boolean z) {
            return hasColor6(z) ? getColor6(z) : getColor4(z);
        }

        public boolean hasColor2() {
            return getColor2() != getColor1();
        }

        public boolean hasColor2(boolean z) {
            return getColor2(z) != getColor1(z);
        }

        public boolean hasColor3() {
            return getColor3() != getColor1();
        }

        public boolean hasColor3(boolean z) {
            return getColor3(z) != getColor1(z);
        }

        public boolean hasColor6(boolean z) {
            return getColor6(z) != getColor1(z);
        }
    }

    public static class PeerColors {
        public static final int TYPE_NAME = 0;
        public static final int TYPE_PROFILE = 1;
        public final ArrayList<PeerColor> colors = new ArrayList<>();
        private final LongSparseArray colorsById = new LongSparseArray();
        public final int hash;
        public final int type;

        private PeerColors(int i, int i2) {
            this.type = i;
            this.hash = i2;
        }

        private static int color(String str) {
            return MessagesController$PeerColors$$ExternalSyntheticBackport0.m("ff" + str, 16);
        }

        public static PeerColors fromJSON(int i, TLRPC.TL_jsonObject tL_jsonObject, TLRPC.TL_jsonObject tL_jsonObject2, TLRPC.TL_jsonArray tL_jsonArray) {
            try {
                PeerColors peerColors = new PeerColors(i, 0);
                if (tL_jsonObject != null) {
                    Iterator<TLRPC.TL_jsonObjectValue> it = tL_jsonObject.value.iterator();
                    while (it.hasNext()) {
                        TLRPC.TL_jsonObjectValue next = it.next();
                        int intValue = Utilities.parseInt((CharSequence) next.key).intValue();
                        TLRPC.JSONValue jSONValue = next.value;
                        if (jSONValue instanceof TLRPC.TL_jsonArray) {
                            ArrayList<TLRPC.JSONValue> arrayList = ((TLRPC.TL_jsonArray) jSONValue).value;
                            if (!arrayList.isEmpty()) {
                                PeerColor peerColor = new PeerColor();
                                try {
                                    peerColor.id = intValue;
                                    int i2 = 0;
                                    while (i2 < 6) {
                                        int[] iArr = peerColor.colors;
                                        int[] iArr2 = peerColor.darkColors;
                                        int color = arrayList.size() > i2 ? color(((TLRPC.TL_jsonString) arrayList.get(i2)).value) : peerColor.colors[0];
                                        iArr2[i2] = color;
                                        iArr[i2] = color;
                                        i2++;
                                    }
                                    peerColor.isDefaultName = peerColor.id < 7 && i == 0;
                                    peerColors.colorsById.put(intValue, peerColor);
                                } catch (Exception e) {
                                    FileLog.e(e);
                                }
                            }
                        }
                    }
                }
                if (tL_jsonObject2 != null) {
                    Iterator<TLRPC.TL_jsonObjectValue> it2 = tL_jsonObject2.value.iterator();
                    while (it2.hasNext()) {
                        TLRPC.TL_jsonObjectValue next2 = it2.next();
                        int intValue2 = Utilities.parseInt((CharSequence) next2.key).intValue();
                        TLRPC.JSONValue jSONValue2 = next2.value;
                        if (jSONValue2 instanceof TLRPC.TL_jsonArray) {
                            ArrayList<TLRPC.JSONValue> arrayList2 = ((TLRPC.TL_jsonArray) jSONValue2).value;
                            if (!arrayList2.isEmpty()) {
                                long j = intValue2;
                                PeerColor peerColor2 = (PeerColor) peerColors.colorsById.get(j);
                                if (peerColor2 != null) {
                                    try {
                                        peerColor2.id = intValue2;
                                        int i3 = 0;
                                        while (i3 < 6) {
                                            peerColor2.darkColors[i3] = arrayList2.size() > i3 ? color(((TLRPC.TL_jsonString) arrayList2.get(i3)).value) : peerColor2.darkColors[0];
                                            i3++;
                                        }
                                        peerColors.colorsById.put(j, peerColor2);
                                    } catch (Exception e2) {
                                        FileLog.e(e2);
                                    }
                                }
                            }
                        }
                    }
                }
                peerColors.colors.clear();
                if (tL_jsonArray != null) {
                    Iterator<TLRPC.JSONValue> it3 = tL_jsonArray.value.iterator();
                    while (it3.hasNext()) {
                        if (it3.next() instanceof TLRPC.TL_jsonNumber) {
                            PeerColor peerColor3 = (PeerColor) peerColors.colorsById.get((int) ((TLRPC.TL_jsonNumber) r11).value);
                            if (peerColor3 != null) {
                                peerColors.colors.add(peerColor3);
                            }
                        }
                    }
                }
                return peerColors;
            } catch (Exception e3) {
                FileLog.e(e3);
                return null;
            }
        }

        public static PeerColors fromString(int i, String str) {
            int i2;
            int indexOf;
            if (str == null) {
                return null;
            }
            if (!str.startsWith("@") || (indexOf = str.indexOf("^")) < 0) {
                i2 = 0;
            } else {
                i2 = Utilities.parseInt((CharSequence) str.substring(1, indexOf)).intValue();
                str = str.substring(indexOf + 1);
            }
            PeerColors peerColors = new PeerColors(i, i2);
            for (String str2 : str.split(";")) {
                PeerColor fromString = PeerColor.fromString(str2);
                if (fromString != null) {
                    fromString.isDefaultName = fromString.id < 7 && i == 0;
                    if (!fromString.hidden) {
                        peerColors.colors.add(fromString);
                    }
                    peerColors.colorsById.put(fromString.id, fromString);
                }
            }
            return peerColors;
        }

        public static PeerColors fromTL(int i, TLRPC.TL_help_peerColors tL_help_peerColors) {
            if (tL_help_peerColors == null) {
                return null;
            }
            try {
                PeerColors peerColors = new PeerColors(i, tL_help_peerColors.hash);
                for (int i2 = 0; i2 < tL_help_peerColors.colors.size(); i2++) {
                    PeerColor fromTL = PeerColor.fromTL(tL_help_peerColors.colors.get(i2));
                    if (fromTL != null) {
                        fromTL.isDefaultName = fromTL.id < 7 && i == 0;
                        if (!fromTL.hidden) {
                            peerColors.colors.add(fromTL);
                        }
                        peerColors.colorsById.put(fromTL.id, fromTL);
                    }
                }
                return peerColors;
            } catch (Exception e) {
                FileLog.e(e);
                return null;
            }
        }

        public int colorsAvailable(int i, boolean z) {
            int i2 = 0;
            for (int i3 = 0; i3 < this.colors.size(); i3++) {
                PeerColor peerColor = this.colors.get(i3);
                if (!peerColor.hidden && i >= peerColor.getLvl(z)) {
                    i2++;
                }
            }
            return i2;
        }

        public PeerColor getColor(int i) {
            return (PeerColor) this.colorsById.get(i);
        }

        public int maxLevel() {
            return maxLevel(false);
        }

        public int maxLevel(boolean z) {
            int i = 0;
            for (int i2 = 0; i2 < this.colors.size(); i2++) {
                PeerColor peerColor = this.colors.get(i2);
                if (!peerColor.hidden) {
                    i = Math.max(i, peerColor.getLvl(z));
                }
            }
            return i;
        }

        public int minLevel() {
            return minLevel(false);
        }

        public int minLevel(boolean z) {
            int maxLevel = maxLevel(z);
            for (int i = 0; i < this.colors.size(); i++) {
                PeerColor peerColor = this.colors.get(i);
                if (!peerColor.hidden) {
                    maxLevel = Math.min(maxLevel, peerColor.getLvl(z));
                }
            }
            return maxLevel;
        }

        public boolean needUpdate() {
            boolean z = true;
            boolean z2 = false;
            for (int i = 0; i < this.colors.size(); i++) {
                if (this.colors.get(i).channelLvl > 0) {
                    z = false;
                }
                if (this.colors.get(i).id < 7) {
                    z2 = true;
                }
            }
            if (this.type == 1 && !z) {
                Iterator<PeerColor> it = this.colors.iterator();
                while (true) {
                    if (!it.hasNext()) {
                        z = true;
                        break;
                    }
                    if (it.next().groupLvl > 0) {
                        z = false;
                        break;
                    }
                }
            }
            if (z) {
                return true;
            }
            return this.type == 0 && !z2;
        }

        public String toString() {
            StringBuilder sb = new StringBuilder();
            if (this.hash != 0) {
                sb.append("@");
                sb.append(this.hash);
                sb.append("^");
            }
            for (int i = 0; i < this.colors.size(); i++) {
                PeerColor peerColor = this.colors.get(i);
                if (i > 0) {
                    sb.append(";");
                }
                peerColor.appendString(sb);
            }
            return sb.toString();
        }
    }

    public static class PrintingUser {
        public TLRPC.SendMessageAction action;
        public long lastTime;
        public long userId;
    }

    public static class ReadTask {
        public long dialogId;
        public int maxDate;
        public int maxId;
        public long replyId;
        public long sendRequestTime;

        private ReadTask() {
        }

        ReadTask(AnonymousClass1 anonymousClass1) {
            this();
        }
    }

    public class SendAsPeersInfo {
        private long loadTime;
        private boolean loading;
        private TLRPC.TL_channels_sendAsPeers sendAsPeers;

        private SendAsPeersInfo() {
        }

        SendAsPeersInfo(MessagesController messagesController, AnonymousClass1 anonymousClass1) {
            this();
        }
    }

    public class SponsoredMessagesInfo {
        public long loadTime;
        public boolean loading;
        public ArrayList<MessageObject> messages;
        public Integer posts_between;

        public SponsoredMessagesInfo() {
        }
    }

    public static class UserActionUpdatesPts extends TLRPC.Updates {
        private UserActionUpdatesPts() {
        }

        UserActionUpdatesPts(AnonymousClass1 anonymousClass1) {
            this();
        }
    }

    public static class UserActionUpdatesSeq extends TLRPC.Updates {
        private UserActionUpdatesSeq() {
        }

        UserActionUpdatesSeq(AnonymousClass1 anonymousClass1) {
            this();
        }
    }

    static {
        for (int i = 0; i < 4; i++) {
            lockObjects[i] = new Object();
        }
    }

    public MessagesController(int i) {
        super(i);
        Context context;
        String str;
        this.chats = new ConcurrentHashMap<>(100, 1.0f, 2);
        this.encryptedChats = new ConcurrentHashMap<>(10, 1.0f, 2);
        this.users = new ConcurrentHashMap<>(100, 1.0f, 3);
        this.objectsByUsernames = new ConcurrentHashMap<>(100, 1.0f, 2);
        this.activeVoiceChatsMap = new HashMap<>();
        this.joiningToChannels = new ArrayList<>();
        this.exportedChats = new LongSparseArray();
        this.hintDialogs = new ArrayList<>();
        this.dialogsByFolder = new SparseArray<>();
        this.allDialogs = new ArrayList<>();
        this.dialogsForward = new ArrayList<>();
        this.dialogsServerOnly = new ArrayList<>();
        this.dialogsCanAddUsers = new ArrayList<>();
        this.dialogsMyChannels = new ArrayList<>();
        this.dialogsMyGroups = new ArrayList<>();
        this.dialogsChannelsOnly = new ArrayList<>();
        this.dialogsUsersOnly = new ArrayList<>();
        this.dialogsForBlock = new ArrayList<>();
        this.dialogsGroupsOnly = new ArrayList<>();
        this.selectedDialogFilter = new DialogFilter[2];
        this.dialogsLoadedTillDate = Integer.MAX_VALUE;
        this.dialogs_read_inbox_max = new ConcurrentHashMap<>(100, 1.0f, 2);
        this.dialogs_read_outbox_max = new ConcurrentHashMap<>(100, 1.0f, 2);
        this.dialogs_dict = new LongSparseArray();
        this.dialogMessage = new LongSparseArray();
        this.dialogMessagesByRandomIds = new LongSparseArray();
        this.deletedHistory = new LongSparseIntArray();
        this.dialogMessagesByIds = new SparseArray<>();
        this.printingUsers = new ConcurrentHashMap<>(20, 1.0f, 2);
        this.printingStrings = new LongSparseArray();
        this.printingStringsTypes = new LongSparseArray();
        this.sendingTypings = new LongSparseArray[12];
        this.onlinePrivacy = new ConcurrentHashMap<>(20, 1.0f, 2);
        this.pendingUnreadCounter = new LongSparseIntArray();
        this.chatlistFoldersUpdates = new SparseArray<>();
        this.largeQueueMaxActiveOperations = 2;
        this.smallQueueMaxActiveOperations = 5;
        this.storiesChangelogUserId = 777000L;
        this.giveawayAddPeersMax = 10L;
        this.giveawayPeriodMax = 7L;
        this.giveawayCountriesMax = 10L;
        this.giveawayBoostsPerPremium = 4L;
        this.boostsPerSentGift = 3L;
        this.loadingPeerSettings = new LongSparseArray();
        this.createdDialogIds = new ArrayList<>();
        this.createdScheduledDialogIds = new ArrayList<>();
        this.createdDialogMainThreadIds = new ArrayList<>();
        this.visibleDialogMainThreadIds = new ArrayList<>();
        this.visibleScheduledDialogMainThreadIds = new ArrayList<>();
        this.shortPollChannels = new LongSparseIntArray();
        this.needShortPollChannels = new LongSparseArray();
        this.shortPollOnlines = new LongSparseIntArray();
        this.needShortPollOnlines = new LongSparseArray();
        this.deletingDialogs = new LongSparseArray();
        this.clearingHistoryDialogs = new LongSparseArray();
        this.loadingBlockedPeers = false;
        this.blockePeers = new LongSparseIntArray();
        this.totalBlockedCount = -1;
        this.channelViewsToSend = new LongSparseArray();
        this.pollsToCheck = new LongSparseArray();
        this.premiumFeaturesTypesToPosition = new SparseIntArray();
        this.businessFeaturesTypesToPosition = new SparseIntArray();
        this.dialogFilters = new ArrayList<>();
        this.frozenDialogFilters = null;
        this.hiddenUndoChats = new ArrayList<>();
        this.dialogFiltersById = new SparseArray<>();
        this.suggestedFilters = new ArrayList<>();
        this.updatesQueueChannels = new LongSparseArray();
        this.updatesStartWaitTimeChannels = new LongSparseLongArray();
        this.channelsPts = new LongSparseIntArray();
        this.gettingDifferenceChannels = new LongSparseArray();
        this.gettingChatInviters = new LongSparseArray();
        this.gettingUnknownChannels = new LongSparseArray();
        this.gettingUnknownDialogs = new LongSparseArray();
        this.checkingLastMessagesDialogs = new LongSparseArray();
        this.updatesQueueSeq = new ArrayList<>();
        this.updatesQueuePts = new ArrayList<>();
        this.updatesQueueQts = new ArrayList<>();
        this.fullUsers = new LongSparseArray();
        this.fullChats = new LongSparseArray();
        this.groupCalls = new LongSparseArray();
        this.groupCallsByChatId = new LongSparseArray();
        this.loadingFullUsers = new HashSet<>();
        this.loadedFullUsers = new LongSparseLongArray();
        this.loadingFullChats = new HashSet<>();
        this.loadingGroupCalls = new HashSet<>();
        this.loadingFullParticipants = new HashSet<>();
        this.loadedFullParticipants = new HashSet<>();
        this.loadedFullChats = new LongSparseLongArray();
        this.channelAdmins = new LongSparseArray();
        this.loadingChannelAdmins = new LongSparseIntArray();
        this.migratedChats = new SparseIntArray();
        this.sponsoredMessages = new LongSparseArray();
        this.sendAsPeers = new LongSparseArray();
        this.reloadingWebpages = new HashMap<>();
        this.reloadingWebpagesPending = new LongSparseArray();
        this.reloadingScheduledWebpages = new HashMap<>();
        this.reloadingScheduledWebpagesPending = new LongSparseArray();
        this.reloadingSavedWebpages = new HashMap<>();
        this.reloadingSavedWebpagesPending = new LongSparseArray();
        this.lastScheduledServerQueryTime = new LongSparseArray();
        this.lastQuickReplyServerQueryTime = new LongSparseArray();
        this.lastSavedServerQueryTime = new LongSparseArray();
        this.lastServerQueryTime = new LongSparseArray();
        this.reloadingMessages = new LongSparseArray();
        this.readTasks = new ArrayList<>();
        this.readTasksMap = new LongSparseArray();
        this.repliesReadTasks = new ArrayList<>();
        this.threadsReadTasksMap = new HashMap<>();
        this.nextDialogsCacheOffset = new SparseIntArray();
        this.loadingDialogs = new SparseBooleanArray();
        this.dialogsEndReached = new SparseBooleanArray();
        this.serverDialogsEndReached = new SparseBooleanArray();
        this.getDifferenceFirstSync = true;
        this.loadingPinnedDialogs = new SparseIntArray();
        this.faqSearchArray = new ArrayList<>();
        this.suggestContacts = true;
        this.themeCheckRunnable = new MessagesController$$ExternalSyntheticLambda96();
        this.passwordCheckRunnable = new Runnable() {
            @Override
            public final void run() {
                MessagesController.this.lambda$new$0();
            }
        };
        this.uploadingThemes = new HashMap<>();
        this.appConfigFetcher = new AnonymousClass1();
        this.maxBroadcastCount = 100;
        this.minGroupConvertSize = 200;
        this.gifSearchEmojies = new ArrayList<>();
        this.diceSuccess = new HashMap<>();
        this.emojiSounds = new HashMap<>();
        this.emojiInteractions = new HashMap<>();
        this.showAnnualPerMonth = false;
        this.starrefStartParamPrefixes = new HashSet();
        this.directPaymentsCurrency = new ArrayList();
        this.emojiStatusUntilValues = new LongSparseArray();
        this.photoSuggestion = new SparseArray<>();
        this.dialogDateComparator = new Comparator() {
            @Override
            public final int compare(Object obj, Object obj2) {
                int lambda$new$10;
                lambda$new$10 = MessagesController.this.lambda$new$10((TLRPC.Dialog) obj, (TLRPC.Dialog) obj2);
                return lambda$new$10;
            }
        };
        this.dialogComparator = new Comparator() {
            @Override
            public final int compare(Object obj, Object obj2) {
                int lambda$new$11;
                lambda$new$11 = MessagesController.this.lambda$new$11((TLRPC.Dialog) obj, (TLRPC.Dialog) obj2);
                return lambda$new$11;
            }
        };
        this.updatesComparator = new Comparator() {
            @Override
            public final int compare(Object obj, Object obj2) {
                int lambda$new$12;
                lambda$new$12 = MessagesController.this.lambda$new$12((TLRPC.Update) obj, (TLRPC.Update) obj2);
                return lambda$new$12;
            }
        };
        this.loadAppConfigRunnable = new Runnable() {
            @Override
            public final void run() {
                MessagesController.this.loadAppConfig();
            }
        };
        this.notifyTranscriptionAudioCooldownUpdate = new Runnable() {
            @Override
            public final void run() {
                MessagesController.this.lambda$new$34();
            }
        };
        this.peerDialogsRequested = new LongSparseArray();
        this.peerDialogRequestTimeout = 240000L;
        this.dialogPhotos = new LongSparseArray();
        this.DIALOGS_LOAD_TYPE_CACHE = 1;
        this.DIALOGS_LOAD_TYPE_CHANNEL = 2;
        this.DIALOGS_LOAD_TYPE_UNKNOWN = 3;
        this.sensitiveAgreed = new HashSet<>();
        this.cachedIsUserPremiumBlocked = new LongSparseArray();
        this.loadingIsUserPremiumBlocked = new HashSet<>();
        this.requestIsUserPremiumBlockedRunnable = new Runnable() {
            @Override
            public final void run() {
                MessagesController.this.requestIsUserPremiumBlocked();
            }
        };
        this.effectsFetcher = new AnonymousClass4();
        this.reportedMessageDelivery = new HashSet<>();
        this.pendingReportMessageDelivery = new HashSet<>();
        this.sendReportMessageDeliver = new Runnable() {
            @Override
            public final void run() {
                MessagesController.this.lambda$new$457();
            }
        };
        ImageLoader.getInstance();
        getMessagesStorage();
        getLocationController();
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                MessagesController.this.lambda$new$13();
            }
        });
        addSupportUser();
        if (this.currentAccount == 0) {
            this.notificationsPreferences = ApplicationLoader.applicationContext.getSharedPreferences("Notifications", 0);
            this.mainPreferences = ApplicationLoader.applicationContext.getSharedPreferences("mainconfig", 0);
            context = ApplicationLoader.applicationContext;
            str = "emoji";
        } else {
            this.notificationsPreferences = ApplicationLoader.applicationContext.getSharedPreferences("Notifications" + this.currentAccount, 0);
            this.mainPreferences = ApplicationLoader.applicationContext.getSharedPreferences("mainconfig" + this.currentAccount, 0);
            context = ApplicationLoader.applicationContext;
            str = "emoji" + this.currentAccount;
        }
        this.emojiPreferences = context.getSharedPreferences(str, 0);
        System.currentTimeMillis();
        this.remoteConfigLoaded = this.mainPreferences.getBoolean("remoteConfigLoaded", false);
        this.secretWebpagePreview = this.mainPreferences.getInt("secretWebpage2", 2);
        this.maxGroupCount = this.mainPreferences.getInt("maxGroupCount", 200);
        this.maxMegagroupCount = this.mainPreferences.getInt("maxMegagroupCount", 10000);
        this.maxRecentGifsCount = this.mainPreferences.getInt("maxRecentGifsCount", 200);
        this.maxRecentStickersCount = this.mainPreferences.getInt("maxRecentStickersCount", 30);
        this.maxFaveStickersCount = this.mainPreferences.getInt("maxFaveStickersCount", 5);
        this.maxEditTime = this.mainPreferences.getInt("maxEditTime", 3600);
        this.ratingDecay = this.mainPreferences.getInt("ratingDecay", 2419200);
        this.linkPrefix = this.mainPreferences.getString("linkPrefix", "t.me");
        this.callReceiveTimeout = this.mainPreferences.getInt("callReceiveTimeout", 20000);
        this.callRingTimeout = this.mainPreferences.getInt("callRingTimeout", 90000);
        this.callConnectTimeout = this.mainPreferences.getInt("callConnectTimeout", 30000);
        this.callPacketTimeout = this.mainPreferences.getInt("callPacketTimeout", 10000);
        this.updateCheckDelay = this.mainPreferences.getInt("updateCheckDelay", 86400);
        this.maxFolderPinnedDialogsCountDefault = this.mainPreferences.getInt("maxFolderPinnedDialogsCountDefault", 100);
        this.maxFolderPinnedDialogsCountPremium = this.mainPreferences.getInt("maxFolderPinnedDialogsCountPremium", 100);
        this.maxMessageLength = this.mainPreferences.getInt("maxMessageLength", 4096);
        this.maxCaptionLength = this.mainPreferences.getInt("maxCaptionLength", 1024);
        this.mapProvider = this.mainPreferences.getInt("mapProvider", 0);
        this.availableMapProviders = this.mainPreferences.getInt("availableMapProviders", 3);
        this.mapKey = this.mainPreferences.getString("pk", null);
        this.installReferer = this.mainPreferences.getString("installReferer", null);
        this.revokeTimeLimit = this.mainPreferences.getInt("revokeTimeLimit", Integer.MAX_VALUE);
        this.revokeTimePmLimit = this.mainPreferences.getInt("revokeTimePmLimit", Integer.MAX_VALUE);
        this.canRevokePmInbox = this.mainPreferences.getBoolean("canRevokePmInbox", this.canRevokePmInbox);
        this.preloadFeaturedStickers = this.mainPreferences.getBoolean("preloadFeaturedStickers", false);
        this.youtubePipType = this.mainPreferences.getString("youtubePipType", "disabled");
        this.keepAliveService = this.mainPreferences.getBoolean("keepAliveService", false);
        this.backgroundConnection = this.mainPreferences.getBoolean("keepAliveService", false);
        this.promoDialogId = this.mainPreferences.getLong("proxy_dialog", 0L);
        this.nextPromoInfoCheckTime = this.mainPreferences.getInt("nextPromoInfoCheckTime", 0);
        this.promoDialogType = this.mainPreferences.getInt("promo_dialog_type", 0);
        this.promoPsaMessage = this.mainPreferences.getString("promo_psa_message", null);
        this.promoPsaType = this.mainPreferences.getString("promo_psa_type", null);
        this.proxyDialogAddress = this.mainPreferences.getString("proxyDialogAddress", null);
        this.venueSearchBot = this.mainPreferences.getString("venueSearchBot", "foursquare");
        this.storyVenueSearchBot = this.mainPreferences.getString("storyVenueSearchBot", "foursquare");
        this.gifSearchBot = this.mainPreferences.getString("gifSearchBot", "gif");
        this.imageSearchBot = this.mainPreferences.getString("imageSearchBot", "pic");
        this.blockedCountry = this.mainPreferences.getBoolean("blockedCountry", false);
        this.suggestedLangCode = this.mainPreferences.getString("suggestedLangCode", "en");
        this.animatedEmojisZoom = this.mainPreferences.getFloat("animatedEmojisZoom", 0.625f);
        this.qrLoginCamera = this.mainPreferences.getBoolean("qrLoginCamera", true);
        this.saveGifsWithStickers = this.mainPreferences.getBoolean("saveGifsWithStickers", false);
        this.filtersEnabled = this.mainPreferences.getBoolean("filtersEnabled", false);
        this.getfileExperimentalParams = this.mainPreferences.getBoolean("getfileExperimentalParams", false);
        this.smsjobsStickyNotificationEnabled = this.mainPreferences.getBoolean("smsjobsStickyNotificationEnabled", false);
        this.showFiltersTooltip = this.mainPreferences.getBoolean("showFiltersTooltip", false);
        this.autoarchiveAvailable = this.mainPreferences.getBoolean("autoarchiveAvailable", false);
        this.groupCallVideoMaxParticipants = this.mainPreferences.getInt("groipCallVideoMaxParticipants", 30);
        this.chatReadMarkSizeThreshold = this.mainPreferences.getInt("chatReadMarkSizeThreshold", 100);
        this.chatReadMarkExpirePeriod = this.mainPreferences.getInt("chatReadMarkExpirePeriod", 604800);
        this.ringtoneDurationMax = this.mainPreferences.getInt("ringtoneDurationMax", 5);
        this.ringtoneSizeMax = this.mainPreferences.getInt("ringtoneSizeMax", 102400);
        this.pmReadDateExpirePeriod = this.mainPreferences.getInt("pmReadDateExpirePeriod", 604800);
        this.suggestStickersApiOnly = this.mainPreferences.getBoolean("suggestStickersApiOnly", false);
        this.roundVideoSize = this.mainPreferences.getInt("roundVideoSize", 384);
        this.roundVideoBitrate = this.mainPreferences.getInt("roundVideoBitrate", 1000);
        this.roundAudioBitrate = this.mainPreferences.getInt("roundAudioBitrate", 64);
        this.pendingSuggestions = this.mainPreferences.getStringSet("pendingSuggestions", null);
        this.dismissedSuggestions = this.mainPreferences.getStringSet("dismissedSuggestions", null);
        int i2 = this.mainPreferences.getInt("channelsLimitDefault", 500);
        this.channelsLimitDefault = i2;
        this.channelsLimitPremium = this.mainPreferences.getInt("channelsLimitPremium", i2 * 2);
        this.savedGifsLimitDefault = this.mainPreferences.getInt("savedGifsLimitDefault", 200);
        this.savedGifsLimitPremium = this.mainPreferences.getInt("savedGifsLimitPremium", 400);
        this.stickersFavedLimitDefault = this.mainPreferences.getInt("stickersFavedLimitDefault", 5);
        this.stickersFavedLimitPremium = this.mainPreferences.getInt("stickersFavedLimitPremium", 200);
        this.maxPinnedDialogsCountDefault = this.mainPreferences.getInt("maxPinnedDialogsCountDefault", 5);
        this.maxPinnedDialogsCountPremium = this.mainPreferences.getInt("maxPinnedDialogsCountPremium", 5);
        this.maxPinnedDialogsCountDefault = this.mainPreferences.getInt("maxPinnedDialogsCountDefault", 5);
        this.maxPinnedDialogsCountPremium = this.mainPreferences.getInt("maxPinnedDialogsCountPremium", 5);
        this.dialogFiltersLimitDefault = this.mainPreferences.getInt("dialogFiltersLimitDefault", 10);
        this.dialogFiltersLimitPremium = this.mainPreferences.getInt("dialogFiltersLimitPremium", 20);
        this.dialogFiltersChatsLimitDefault = this.mainPreferences.getInt("dialogFiltersChatsLimitDefault", 100);
        this.dialogFiltersChatsLimitPremium = this.mainPreferences.getInt("dialogFiltersChatsLimitPremium", 200);
        this.dialogFiltersPinnedLimitDefault = this.mainPreferences.getInt("dialogFiltersPinnedLimitDefault", 5);
        this.dialogFiltersPinnedLimitPremium = this.mainPreferences.getInt("dialogFiltersPinnedLimitPremium", 10);
        this.publicLinksLimitDefault = this.mainPreferences.getInt("publicLinksLimitDefault", 10);
        this.publicLinksLimitPremium = this.mainPreferences.getInt("publicLinksLimitPremium", 20);
        this.captionLengthLimitDefault = this.mainPreferences.getInt("captionLengthLimitDefault", 1024);
        this.captionLengthLimitPremium = this.mainPreferences.getInt("captionLengthLimitPremium", 4096);
        this.storyCaptionLengthLimitDefault = this.mainPreferences.getInt("storyCaptionLengthLimit", 200);
        this.storyCaptionLengthLimitPremium = this.mainPreferences.getInt("storyCaptionLengthLimitPremium", 2048);
        this.aboutLengthLimitDefault = this.mainPreferences.getInt("aboutLengthLimitDefault", 70);
        this.aboutLengthLimitPremium = this.mainPreferences.getInt("aboutLengthLimitPremium", 140);
        this.reactionsUserMaxDefault = this.mainPreferences.getInt("reactionsUserMaxDefault", 1);
        this.reactionsUserMaxPremium = this.mainPreferences.getInt("reactionsUserMaxPremium", 3);
        this.reactionsInChatMax = this.mainPreferences.getInt("reactionsInChatMax", 3);
        int i3 = this.mainPreferences.getInt("uploadMaxFileParts", 4000);
        this.uploadMaxFileParts = i3;
        this.uploadMaxFilePartsPremium = this.mainPreferences.getInt("uploadMaxFilePartsPremium", i3 * 2);
        this.premiumInvoiceSlug = this.mainPreferences.getString("premiumInvoiceSlug", null);
        this.premiumBotUsername = this.mainPreferences.getString("premiumBotUsername", null);
        this.premiumLocked = this.mainPreferences.getBoolean("premiumLocked", false);
        this.starsLocked = this.mainPreferences.getBoolean("starsLocked", true);
        this.transcribeButtonPressed = this.mainPreferences.getInt("transcribeButtonPressed", 0);
        this.forumUpgradeParticipantsMin = this.mainPreferences.getInt("forumUpgradeParticipantsMin", 200);
        this.topicsPinnedLimit = this.mainPreferences.getInt("topicsPinnedLimit", 3);
        this.telegramAntispamUserId = this.mainPreferences.getLong("telegramAntispamUserId", -1L);
        this.telegramAntispamGroupSizeMin = this.mainPreferences.getInt("telegramAntispamGroupSizeMin", 100);
        this.hiddenMembersGroupSizeMin = this.mainPreferences.getInt("hiddenMembersGroupSizeMin", 100);
        this.chatlistUpdatePeriod = this.mainPreferences.getInt("chatlistUpdatePeriod", 3600);
        this.uploadMarkupVideo = this.mainPreferences.getBoolean("uploadMarkupVideo", true);
        this.giftAttachMenuIcon = this.mainPreferences.getBoolean("giftAttachMenuIcon", false);
        this.giftTextFieldIcon = this.mainPreferences.getBoolean("giftTextFieldIcon", false);
        this.checkResetLangpack = this.mainPreferences.getInt("checkResetLangpack", 0);
        this.smallQueueMaxActiveOperations = this.mainPreferences.getInt("smallQueueMaxActiveOperations", 5);
        this.largeQueueMaxActiveOperations = this.mainPreferences.getInt("largeQueueMaxActiveOperations", 2);
        this.stealthModeFuture = this.mainPreferences.getInt("stories_stealth_future_period", 1500);
        this.storiesChangelogUserId = this.mainPreferences.getLong("stories_changelog_user_id", 777000L);
        this.giveawayAddPeersMax = this.mainPreferences.getLong("giveaway_add_peers_max", 10L);
        this.giveawayCountriesMax = this.mainPreferences.getLong("giveaway_countries_max", 10L);
        this.giveawayBoostsPerPremium = this.mainPreferences.getLong("giveaway_boosts_per_premium", 4L);
        this.boostsPerSentGift = this.mainPreferences.getLong("boosts_per_sent_gift", 3L);
        this.giveawayPeriodMax = this.mainPreferences.getLong("giveaway_period_max", 7L);
        this.stealthModePast = this.mainPreferences.getInt("stories_stealth_past_period", 300);
        this.stealthModeCooldown = this.mainPreferences.getInt("stories_stealth_cooldown_period", 3600);
        boolean z = ConnectionsManager.native_isTestBackend(this.currentAccount) != 0;
        this.chatlistInvitesLimitDefault = this.mainPreferences.getInt("chatlistInvitesLimitDefault", 3);
        this.storyExpiringLimitDefault = this.mainPreferences.getInt("storyExpiringLimitDefault", 50);
        this.storyExpiringLimitPremium = this.mainPreferences.getInt("storyExpiringLimitPremium", 100);
        this.storiesSentWeeklyLimitDefault = this.mainPreferences.getInt("storiesSentWeeklyLimitDefault", 7);
        this.storiesSuggestedReactionsLimitDefault = this.mainPreferences.getInt("storiesSuggestedReactionsLimitDefault", 1);
        this.storiesSuggestedReactionsLimitPremium = this.mainPreferences.getInt("storiesSuggestedReactionsLimitPremium", 5);
        this.storiesSentWeeklyLimitPremium = this.mainPreferences.getInt("storiesSentWeeklyLimitPremium", 70);
        this.storiesSentMonthlyLimitDefault = this.mainPreferences.getInt("storiesSentMonthlyLimitDefault", 30);
        this.storiesSentMonthlyLimitPremium = this.mainPreferences.getInt("storiesSentMonthlyLimitPremium", 300);
        this.channelBgIconLevelMin = this.mainPreferences.getInt("channelBgIconLevelMin", 1);
        this.channelProfileIconLevelMin = this.mainPreferences.getInt("channelProfileIconLevelMin", 1);
        this.channelEmojiStatusLevelMin = this.mainPreferences.getInt("channelEmojiStatusLevelMin", 1);
        this.groupProfileBgIconLevelMin = this.mainPreferences.getInt("groupProfileBgIconLevelMin", 1);
        this.groupEmojiStatusLevelMin = this.mainPreferences.getInt("groupEmojiStatusLevelMin", 1);
        this.groupEmojiStickersLevelMin = this.mainPreferences.getInt("groupEmojiStickersLevelMin", 1);
        this.groupWallpaperLevelMin = this.mainPreferences.getInt("groupWallpaperLevelMin", 1);
        this.groupCustomWallpaperLevelMin = this.mainPreferences.getInt("groupCustomWallpaperLevelMin", 1);
        this.groupTranscribeLevelMin = this.mainPreferences.getInt("groupTranscribeLevelMin", 1);
        this.quickRepliesLimit = this.mainPreferences.getInt("quickRepliesLimit", 10);
        this.quickReplyMessagesLimit = this.mainPreferences.getInt("quickReplyMessagesLimit", 20);
        this.channelWallpaperLevelMin = this.mainPreferences.getInt("channelWallpaperLevelMin", 1);
        this.channelCustomWallpaperLevelMin = this.mainPreferences.getInt("channelCustomWallpaperLevelMin", 1);
        this.chatlistInvitesLimitPremium = this.mainPreferences.getInt("chatlistInvitesLimitPremium", z ? 5 : 20);
        this.chatlistJoinedLimitDefault = this.mainPreferences.getInt("chatlistJoinedLimitDefault", 2);
        this.chatlistJoinedLimitPremium = this.mainPreferences.getInt("chatlistJoinedLimitPremium", z ? 5 : 20);
        this.stargiftsMessageLengthMax = this.mainPreferences.getInt("stargiftsMessageLengthMax", 255);
        this.stargiftsConvertPeriodMax = this.mainPreferences.getInt("stargiftsConvertPeriodMax", z ? 300 : 7776000);
        this.videoIgnoreAltDocuments = this.mainPreferences.getBoolean("videoIgnoreAltDocuments", false);
        this.disableBotFullscreenBlur = this.mainPreferences.getBoolean("disableBotFullscreenBlur", false);
        this.storiesPosting = this.mainPreferences.getString("storiesPosting", "enabled");
        this.storiesEntities = this.mainPreferences.getString("storiesEntities", "premium");
        this.storiesExportNopublicLink = this.mainPreferences.getBoolean("storiesExportNopublicLink", false);
        this.authorizationAutoconfirmPeriod = this.mainPreferences.getInt("authorization_autoconfirm_period", 604800);
        this.quoteLengthMax = this.mainPreferences.getInt("quoteLengthMax", 1024);
        this.giveawayGiftsPurchaseAvailable = this.mainPreferences.getBoolean("giveawayGiftsPurchaseAvailable", false);
        this.peerColors = PeerColors.fromString(0, this.mainPreferences.getString("peerColors", ""));
        this.profilePeerColors = PeerColors.fromString(1, this.mainPreferences.getString("profilePeerColors", ""));
        int i4 = this.mainPreferences.getInt("transcribeAudioTrialWeeklyNumber", BuildVars.DEBUG_PRIVATE_VERSION ? 2 : 0);
        this.transcribeAudioTrialWeeklyNumber = i4;
        this.transcribeAudioTrialCurrentNumber = this.mainPreferences.getInt("transcribeAudioTrialCurrentNumber", i4);
        this.transcribeAudioTrialDurationMax = this.mainPreferences.getInt("transcribeAudioTrialDurationMax", 300);
        this.transcribeAudioTrialCooldownUntil = this.mainPreferences.getInt("transcribeAudioTrialCooldownUntil", 0);
        this.recommendedChannelsLimitDefault = this.mainPreferences.getInt("recommendedChannelsLimitDefault", 10);
        this.recommendedChannelsLimitPremium = this.mainPreferences.getInt("recommendedChannelsLimitPremium", 100);
        this.boostsChannelLevelMax = this.mainPreferences.getInt("boostsChannelLevelMax", 100);
        this.channelRestrictSponsoredLevelMin = this.mainPreferences.getInt("channelRestrictSponsoredLevelMin", 30);
        this.savedDialogsPinnedLimitDefault = this.mainPreferences.getInt("savedDialogsPinnedLimitDefault", 4);
        this.savedDialogsPinnedLimitPremium = this.mainPreferences.getInt("savedDialogsPinnedLimitPremium", 6);
        this.storyQualityFull = this.mainPreferences.getBoolean("storyQualityFull", true);
        this.savedViewAsChats = this.mainPreferences.getBoolean("savedViewAsChats", false);
        this.folderTags = this.mainPreferences.getBoolean("folderTags", false);
        this.uploadPremiumSpeedupUpload = this.mainPreferences.getFloat("uploadPremiumSpeedupUpload", 10.0f);
        this.uploadPremiumSpeedupDownload = this.mainPreferences.getFloat("uploadPremiumSpeedupDownload", 10.0f);
        this.uploadPremiumSpeedupNotifyPeriod = this.mainPreferences.getInt("uploadPremiumSpeedupNotifyPeriod2", 3600);
        this.introTitleLengthLimit = this.mainPreferences.getInt("introTitleLengthLimit", 32);
        this.introDescriptionLengthLimit = this.mainPreferences.getInt("introDescriptionLengthLimit", 72);
        this.businessChatLinksLimit = this.mainPreferences.getInt("businessChatLinksLimit", 100);
        this.channelRevenueWithdrawalEnabled = this.mainPreferences.getBoolean("channelRevenueWithdrawalEnabled", false);
        this.newNoncontactPeersRequirePremiumWithoutOwnpremium = this.mainPreferences.getBoolean("newNoncontactPeersRequirePremiumWithoutOwnpremium", false);
        this.reactionsUniqMax = this.mainPreferences.getInt("reactionsUniqMax", 11);
        this.premiumManageSubscriptionUrl = this.mainPreferences.getString("premiumManageSubscriptionUrl", ApplicationLoader.isStandaloneBuild() ? "https://t.me/premiumbot?start=status" : "https://play.google.com/store/account/subscriptions?sku=telegram_premium&package=org.telegram.messenger");
        this.androidDisableRoundCamera2 = this.mainPreferences.getBoolean("androidDisableRoundCamera2", true);
        this.storiesPinnedToTopCountMax = this.mainPreferences.getInt("storiesPinnedToTopCountMax", 3);
        this.showAnnualPerMonth = this.mainPreferences.getBoolean("showAnnualPerMonth", false);
        this.canEditFactcheck = this.mainPreferences.getBoolean("canEditFactcheck", false);
        this.factcheckLengthLimit = this.mainPreferences.getInt("factcheckLengthLimit", 1024);
        this.starsRevenueWithdrawalMin = this.mainPreferences.getLong("starsRevenueWithdrawalMin", 1000L);
        this.starsPaidPostAmountMax = this.mainPreferences.getLong("starsPaidPostAmountMax", 10000L);
        this.botPreviewMediasMax = this.mainPreferences.getInt("botPreviewMediasMax", 10);
        this.webAppAllowedProtocols = this.mainPreferences.getStringSet("webAppAllowedProtocols", new HashSet(Arrays.asList("http", "https")));
        this.ignoreRestrictionReasons = this.mainPreferences.getStringSet("ignoreRestrictionReasons", new HashSet(Arrays.asList(new String[0])));
        this.tonProxyAddress = this.mainPreferences.getString("tonProxyAddress", "magic.org");
        this.weatherSearchUsername = this.mainPreferences.getString("weatherSearchUsername", "izweatherbot");
        this.storyWeatherPreload = this.mainPreferences.getBoolean("storyWeatherPreload", true);
        this.starsGiftsEnabled = this.mainPreferences.getBoolean("starsGiftsEnabled", true);
        this.stargiftsBlocked = this.mainPreferences.getBoolean("stargiftsBlocked", true);
        this.starsPaidReactionAmountMax = this.mainPreferences.getLong("starsPaidReactionAmountMax", 10000L);
        this.starsSubscriptionAmountMax = this.mainPreferences.getLong("starsSubscriptionAmountMax", 2500L);
        this.starsUsdSellRate1000 = this.mainPreferences.getFloat("starsUsdSellRate1000", 2000.0f);
        this.starsUsdWithdrawRate1000 = this.mainPreferences.getFloat("starsUsdWithdrawRate1000", 1200.0f);
        this.sponsoredLinksInappAllow = this.mainPreferences.getBoolean("sponsoredLinksInappAllow", false);
        this.starrefProgramAllowed = this.mainPreferences.getBoolean("starrefProgramAllowed", false);
        this.starrefConnectAllowed = this.mainPreferences.getBoolean("starrefConnectAllowed", false);
        this.starrefStartParamPrefixes = this.mainPreferences.getStringSet("starrefStartParamPrefixes", new HashSet(Arrays.asList("_tgr_")));
        this.starrefMinCommissionPermille = this.mainPreferences.getInt("starrefMinCommissionPermille", 1);
        this.starrefMaxCommissionPermille = this.mainPreferences.getInt("starrefMaxCommissionPermille", 400);
        this.botVerificationDescriptionLengthLimit = this.mainPreferences.getInt("botVerificationDescriptionLengthLimit", 70);
        this.paidReactionsAnonymousTime = this.mainPreferences.getLong("paidReactionsAnonymousTime", 0L);
        this.paidReactionsAnonymous = (!this.mainPreferences.contains("paidReactionsAnonymous") || System.currentTimeMillis() - this.paidReactionsAnonymousTime >= 7200000) ? null : Boolean.valueOf(this.mainPreferences.getBoolean("paidReactionsAnonymous", false));
        scheduleTranscriptionUpdate();
        BuildVars.GOOGLE_AUTH_CLIENT_ID = this.mainPreferences.getString("googleAuthClientId", BuildVars.GOOGLE_AUTH_CLIENT_ID);
        this.dcDomainName = this.mainPreferences.contains("dcDomainName2") ? this.mainPreferences.getString("dcDomainName2", "apv3.stel.com") : z ? "tapv3.stel.com" : "apv3.stel.com";
        if (this.mainPreferences.contains("webFileDatacenterId")) {
            this.webFileDatacenterId = this.mainPreferences.getInt("webFileDatacenterId", 4);
        } else {
            this.webFileDatacenterId = z ? 2 : 4;
        }
        Set<String> stringSet = this.mainPreferences.getStringSet("directPaymentsCurrency", null);
        if (stringSet != null) {
            this.directPaymentsCurrency.clear();
            this.directPaymentsCurrency.addAll(stringSet);
        }
        loadPremiumFeaturesPreviewOrder(this.premiumFeaturesTypesToPosition, this.mainPreferences.getString("premiumFeaturesTypesToPosition", null));
        loadPremiumFeaturesPreviewOrder(this.businessFeaturesTypesToPosition, this.mainPreferences.getString("businessFeaturesTypesToPosition", null));
        this.pendingSuggestions = this.pendingSuggestions != null ? new HashSet(this.pendingSuggestions) : new HashSet();
        this.dismissedSuggestions = this.dismissedSuggestions != null ? new HashSet(this.dismissedSuggestions) : new HashSet();
        Set<String> stringSet2 = this.mainPreferences.getStringSet("exportUri2", null);
        this.exportUri = stringSet2;
        if (stringSet2 != null) {
            this.exportUri = new HashSet(this.exportUri);
        } else {
            HashSet hashSet = new HashSet();
            this.exportUri = hashSet;
            hashSet.add("content://(\\d+@)?com\\.whatsapp\\.provider\\.media/export_chat/");
            this.exportUri.add("content://(\\d+@)?com\\.whatsapp\\.w4b\\.provider\\.media/export_chat/");
            this.exportUri.add("content://jp\\.naver\\.line\\.android\\.line\\.common\\.FileProvider/export-chat/");
            this.exportUri.add(".*WhatsApp.*\\.txt$");
            this.exportUri.add(".*WhatsApp.*\\.zip$");
        }
        Set<String> stringSet3 = this.mainPreferences.getStringSet("exportGroupUri", null);
        this.exportGroupUri = stringSet3;
        if (stringSet3 != null) {
            this.exportGroupUri = new HashSet(this.exportGroupUri);
        } else {
            HashSet hashSet2 = new HashSet();
            this.exportGroupUri = hashSet2;
            hashSet2.add("@g.us/");
        }
        Set<String> stringSet4 = this.mainPreferences.getStringSet("exportPrivateUri", null);
        this.exportPrivateUri = stringSet4;
        if (stringSet4 != null) {
            this.exportPrivateUri = new HashSet(this.exportPrivateUri);
        } else {
            HashSet hashSet3 = new HashSet();
            this.exportPrivateUri = hashSet3;
            hashSet3.add("@s.whatsapp.net/");
        }
        Set<String> stringSet5 = this.mainPreferences.getStringSet("autologinDomains", null);
        this.autologinDomains = stringSet5;
        this.autologinDomains = stringSet5 != null ? new HashSet(this.autologinDomains) : new HashSet();
        Set<String> stringSet6 = this.mainPreferences.getStringSet("authDomains", null);
        this.authDomains = stringSet6;
        this.authDomains = stringSet6 != null ? new HashSet(this.authDomains) : new HashSet();
        this.autologinToken = this.mainPreferences.getString("autologinToken", null);
        Set<String> stringSet7 = this.mainPreferences.getStringSet("diceEmojies", null);
        if (stringSet7 == null) {
            HashSet<String> hashSet4 = new HashSet<>();
            this.diceEmojies = hashSet4;
            hashSet4.add("🎲");
            this.diceEmojies.add("🎯");
        } else {
            this.diceEmojies = new HashSet<>(stringSet7);
        }
        String string = this.mainPreferences.getString("diceSuccess", null);
        if (string == null) {
            this.diceSuccess.put("🎯", new DiceFrameSuccess(62, 6));
        } else {
            try {
                byte[] decode = Base64.decode(string, 0);
                if (decode != null) {
                    SerializedData serializedData = new SerializedData(decode);
                    int readInt32 = serializedData.readInt32(true);
                    for (int i5 = 0; i5 < readInt32; i5++) {
                        this.diceSuccess.put(serializedData.readString(true), new DiceFrameSuccess(serializedData.readInt32(true), serializedData.readInt32(true)));
                    }
                    serializedData.cleanup();
                }
            } catch (Exception e) {
                FileLog.e(e);
            }
        }
        String string2 = this.mainPreferences.getString("emojiSounds", null);
        if (string2 != null) {
            try {
                byte[] decode2 = Base64.decode(string2, 0);
                if (decode2 != null) {
                    SerializedData serializedData2 = new SerializedData(decode2);
                    int readInt322 = serializedData2.readInt32(true);
                    for (int i6 = 0; i6 < readInt322; i6++) {
                        this.emojiSounds.put(serializedData2.readString(true), new EmojiSound(serializedData2.readInt64(true), serializedData2.readInt64(true), serializedData2.readByteArray(true)));
                    }
                    serializedData2.cleanup();
                }
            } catch (Exception e2) {
                FileLog.e(e2);
            }
        }
        String string3 = this.mainPreferences.getString("gifSearchEmojies", null);
        if (string3 == null) {
            this.gifSearchEmojies.add("👍");
            this.gifSearchEmojies.add("👎");
            this.gifSearchEmojies.add("😍");
            this.gifSearchEmojies.add("😂");
            this.gifSearchEmojies.add("😮");
            this.gifSearchEmojies.add("🙄");
            this.gifSearchEmojies.add("😥");
            this.gifSearchEmojies.add("😡");
            this.gifSearchEmojies.add("🥳");
            this.gifSearchEmojies.add("😎");
        } else {
            try {
                byte[] decode3 = Base64.decode(string3, 0);
                if (decode3 != null) {
                    SerializedData serializedData3 = new SerializedData(decode3);
                    int readInt323 = serializedData3.readInt32(true);
                    for (int i7 = 0; i7 < readInt323; i7++) {
                        this.gifSearchEmojies.add(serializedData3.readString(true));
                    }
                    serializedData3.cleanup();
                }
            } catch (Exception e3) {
                FileLog.e(e3);
            }
        }
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                MessagesController.this.loadAppConfig();
            }
        }, 2000L);
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                MessagesController.this.lambda$new$14();
            }
        }, 400L);
        this.topicsController = new TopicsController(i);
        this.cacheByChatsController = new CacheByChatsController(i);
        this.translateController = new TranslateController(this);
        Utilities.globalQueue.postRunnable(new Runnable() {
            @Override
            public final void run() {
                MessagesController.this.lambda$new$15();
            }
        });
    }

    private void addDialogToItsFolder(int i, TLRPC.Dialog dialog) {
        int i2 = dialog instanceof TLRPC.TL_dialogFolder ? 0 : dialog.folder_id;
        ArrayList<TLRPC.Dialog> arrayList = this.dialogsByFolder.get(i2);
        if (arrayList == null) {
            arrayList = new ArrayList<>();
            this.dialogsByFolder.put(i2, arrayList);
        }
        if (i == -1) {
            arrayList.add(dialog);
            return;
        }
        if (i == -2) {
            if (arrayList.isEmpty() || !(arrayList.get(0) instanceof TLRPC.TL_dialogFolder)) {
                arrayList.add(0, dialog);
                return;
            }
            i = 1;
        }
        arrayList.add(i, dialog);
    }

    private void addOrRemoveActiveVoiceChat(final TLRPC.Chat chat) {
        if (Thread.currentThread() != Looper.getMainLooper().getThread()) {
            AndroidUtilities.runOnUIThread(new Runnable() {
                @Override
                public final void run() {
                    MessagesController.this.lambda$addOrRemoveActiveVoiceChat$55(chat);
                }
            });
        } else {
            lambda$addOrRemoveActiveVoiceChat$55(chat);
        }
    }

    public void lambda$addOrRemoveActiveVoiceChat$55(TLRPC.Chat chat) {
        TLRPC.Chat chat2 = this.activeVoiceChatsMap.get(Long.valueOf(chat.id));
        if (chat.call_active && chat.call_not_empty && chat.migrated_to == null && !ChatObject.isNotInChat(chat)) {
            if (chat2 != null) {
                return;
            }
            this.activeVoiceChatsMap.put(Long.valueOf(chat.id), chat);
            getNotificationCenter().lambda$postNotificationNameOnUIThread$1(NotificationCenter.activeGroupCallsUpdated, new Object[0]);
            return;
        }
        if (chat2 == null) {
            return;
        }
        this.activeVoiceChatsMap.remove(Long.valueOf(chat.id));
        getNotificationCenter().lambda$postNotificationNameOnUIThread$1(NotificationCenter.activeGroupCallsUpdated, new Object[0]);
    }

    private void applyAppConfig(org.telegram.tgnet.TLRPC.TL_jsonObject r30) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MessagesController.applyAppConfig(org.telegram.tgnet.TLRPC$TL_jsonObject):void");
    }

    private void applyDialogNotificationsSettings(long j, long j2, TLRPC.PeerNotifySettings peerNotifySettings) {
        getNotificationsController().getNotificationsSettingsFacade().applyDialogNotificationsSettings(j, j2, peerNotifySettings);
    }

    private void applyDialogsNotificationsSettings(ArrayList<TLRPC.Dialog> arrayList) {
        SharedPreferences.Editor edit = getNotificationsSettings(this.currentAccount).edit();
        for (int i = 0; i < arrayList.size(); i++) {
            TLRPC.Dialog dialog = arrayList.get(i);
            if (dialog.peer != null && (dialog.notify_settings instanceof TLRPC.TL_peerNotifySettings)) {
                getNotificationsController().getNotificationsSettingsFacade().setSettingsForDialog(edit, dialog, dialog.notify_settings);
            }
        }
        edit.apply();
    }

    private boolean applyFoldersUpdates(ArrayList<TLRPC.TL_updateFolderPeers> arrayList) {
        if (arrayList == null) {
            return false;
        }
        int size = arrayList.size();
        int i = 0;
        boolean z = false;
        while (i < size) {
            TLRPC.TL_updateFolderPeers tL_updateFolderPeers = arrayList.get(i);
            int size2 = tL_updateFolderPeers.folder_peers.size();
            for (int i2 = 0; i2 < size2; i2++) {
                TLRPC.TL_folderPeer tL_folderPeer = tL_updateFolderPeers.folder_peers.get(i2);
                TLRPC.Dialog dialog = (TLRPC.Dialog) this.dialogs_dict.get(DialogObject.getPeerDialogId(tL_folderPeer.peer));
                if (dialog != null) {
                    int i3 = dialog.folder_id;
                    int i4 = tL_folderPeer.folder_id;
                    if (i3 != i4) {
                        dialog.pinned = false;
                        dialog.pinnedNum = 0;
                        dialog.folder_id = i4;
                        this.hasArchivedChats = true;
                        checkArchiveFolder();
                    }
                }
            }
            getMessagesStorage().setDialogsFolderId(arrayList.get(i).folder_peers, null, 0L, 0);
            i++;
            z = true;
        }
        return z;
    }

    private void checkChannelError(String str, long j) {
        str.hashCode();
        char c = 65535;
        switch (str.hashCode()) {
            case -1809401834:
                if (str.equals("USER_BANNED_IN_CHANNEL")) {
                    c = 0;
                    break;
                }
                break;
            case -795226617:
                if (str.equals("CHANNEL_PRIVATE")) {
                    c = 1;
                    break;
                }
                break;
            case -471086771:
                if (str.equals("CHANNEL_PUBLIC_GROUP_NA")) {
                    c = 2;
                    break;
                }
                break;
        }
        switch (c) {
            case 0:
                getNotificationCenter().lambda$postNotificationNameOnUIThread$1(NotificationCenter.chatInfoCantLoad, Long.valueOf(j), 2);
                return;
            case 1:
                getNotificationCenter().lambda$postNotificationNameOnUIThread$1(NotificationCenter.chatInfoCantLoad, Long.valueOf(j), 0);
                return;
            case 2:
                getNotificationCenter().lambda$postNotificationNameOnUIThread$1(NotificationCenter.chatInfoCantLoad, Long.valueOf(j), 1);
                return;
            default:
                return;
        }
    }

    private boolean checkDeletingTask(boolean z) {
        int i;
        int currentTime = getConnectionsManager().getCurrentTime();
        if ((this.currentDeletingTaskMids == null && this.currentDeletingTaskMediaMids == null) || (!z && ((i = this.currentDeletingTaskTime) == 0 || i > currentTime))) {
            return false;
        }
        this.currentDeletingTaskTime = 0;
        if (this.currentDeleteTaskRunnable != null && !z) {
            Utilities.stageQueue.cancelRunnable(this.currentDeleteTaskRunnable);
        }
        this.currentDeleteTaskRunnable = null;
        LongSparseArray longSparseArray = this.currentDeletingTaskMids;
        final LongSparseArray m2clone = longSparseArray != null ? longSparseArray.m2clone() : null;
        LongSparseArray longSparseArray2 = this.currentDeletingTaskMediaMids;
        final LongSparseArray m2clone2 = longSparseArray2 != null ? longSparseArray2.m2clone() : null;
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                MessagesController.this.lambda$checkDeletingTask$80(m2clone, m2clone2);
            }
        });
        return true;
    }

    public void lambda$checkPromoInfo$154(boolean z) {
        String str;
        boolean z2 = false;
        if (z && this.checkingPromoInfo) {
            this.checkingPromoInfo = false;
        }
        if ((z || this.nextPromoInfoCheckTime <= getConnectionsManager().getCurrentTime()) && !this.checkingPromoInfo) {
            if (this.checkingPromoInfoRequestId != 0) {
                getConnectionsManager().cancelRequest(this.checkingPromoInfoRequestId, true);
                this.checkingPromoInfoRequestId = 0;
            }
            SharedPreferences globalMainSettings = getGlobalMainSettings();
            globalMainSettings.getBoolean("proxy_enabled", false);
            final String string = globalMainSettings.getString("proxy_ip", "");
            final String string2 = globalMainSettings.getString("proxy_secret", "");
            if (this.promoDialogId != 0 && this.promoDialogType == PROMO_TYPE_PROXY && (str = this.proxyDialogAddress) != null) {
                if (!str.equals(string + string2)) {
                    z2 = true;
                }
            }
            final int i = this.lastCheckPromoId + 1;
            this.lastCheckPromoId = i;
            this.checkingPromoInfo = true;
            this.checkingPromoInfoRequestId = getConnectionsManager().sendRequest(new TLRPC.TL_help_getPromoData(), new RequestDelegate() {
                @Override
                public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                    MessagesController.this.lambda$checkPromoInfoInternal$159(i, string, string2, tLObject, tL_error);
                }
            });
            if (z2) {
                this.promoDialogId = 0L;
                this.proxyDialogAddress = null;
                this.nextPromoInfoCheckTime = getConnectionsManager().getCurrentTime() + 3600;
                getGlobalMainSettings().edit().putLong("proxy_dialog", this.promoDialogId).remove("proxyDialogAddress").putInt("nextPromoInfoCheckTime", this.nextPromoInfoCheckTime).commit();
                AndroidUtilities.runOnUIThread(new MessagesController$$ExternalSyntheticLambda69(this));
            }
        }
    }

    private void checkReadTasks() {
        long elapsedRealtime = SystemClock.elapsedRealtime();
        int size = this.readTasks.size();
        int i = 0;
        int i2 = 0;
        while (i2 < size) {
            ReadTask readTask = this.readTasks.get(i2);
            if (readTask.sendRequestTime <= elapsedRealtime) {
                completeReadTask(readTask);
                this.readTasks.remove(i2);
                this.readTasksMap.remove(readTask.dialogId);
                i2--;
                size--;
            }
            i2++;
        }
        int size2 = this.repliesReadTasks.size();
        while (i < size2) {
            ReadTask readTask2 = this.repliesReadTasks.get(i);
            if (readTask2.sendRequestTime <= elapsedRealtime) {
                completeReadTask(readTask2);
                this.repliesReadTasks.remove(i);
                this.threadsReadTasksMap.remove(readTask2.dialogId + "_" + readTask2.replyId);
                i += -1;
                size2 += -1;
            }
            i++;
        }
    }

    private void checkTosUpdate() {
        if (this.nextTosCheckTime > getConnectionsManager().getCurrentTime() || this.checkingTosUpdate || !getUserConfig().isClientActivated()) {
            return;
        }
        this.checkingTosUpdate = true;
        getConnectionsManager().sendRequest(new TLRPC.TL_help_getTermsOfServiceUpdate(), new RequestDelegate() {
            @Override
            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                MessagesController.this.lambda$checkTosUpdate$153(tLObject, tL_error);
            }
        });
    }

    private void completeReadTask(ReadTask readTask) {
        TLRPC.TL_messages_readHistory tL_messages_readHistory;
        RequestDelegate requestDelegate;
        ConnectionsManager connectionsManager;
        TLRPC.TL_messages_readHistory tL_messages_readHistory2;
        if (readTask.replyId != 0) {
            TLRPC.TL_messages_readDiscussion tL_messages_readDiscussion = new TLRPC.TL_messages_readDiscussion();
            tL_messages_readDiscussion.msg_id = (int) readTask.replyId;
            tL_messages_readDiscussion.peer = getInputPeer(readTask.dialogId);
            tL_messages_readDiscussion.read_max_id = readTask.maxId;
            ConnectionsManager connectionsManager2 = getConnectionsManager();
            requestDelegate = new RequestDelegate() {
                @Override
                public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                    MessagesController.lambda$completeReadTask$225(tLObject, tL_error);
                }
            };
            tL_messages_readHistory2 = tL_messages_readDiscussion;
            connectionsManager = connectionsManager2;
        } else {
            if (DialogObject.isEncryptedDialog(readTask.dialogId)) {
                TLRPC.EncryptedChat encryptedChat = getEncryptedChat(Integer.valueOf(DialogObject.getEncryptedChatId(readTask.dialogId)));
                byte[] bArr = encryptedChat.auth_key;
                if (bArr == null || bArr.length <= 1 || !(encryptedChat instanceof TLRPC.TL_encryptedChat)) {
                    return;
                }
                TLRPC.TL_messages_readEncryptedHistory tL_messages_readEncryptedHistory = new TLRPC.TL_messages_readEncryptedHistory();
                TLRPC.TL_inputEncryptedChat tL_inputEncryptedChat = new TLRPC.TL_inputEncryptedChat();
                tL_messages_readEncryptedHistory.peer = tL_inputEncryptedChat;
                tL_inputEncryptedChat.chat_id = encryptedChat.id;
                tL_inputEncryptedChat.access_hash = encryptedChat.access_hash;
                tL_messages_readEncryptedHistory.max_date = readTask.maxDate;
                getConnectionsManager().sendRequest(tL_messages_readEncryptedHistory, new RequestDelegate() {
                    @Override
                    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                        MessagesController.lambda$completeReadTask$227(tLObject, tL_error);
                    }
                });
                return;
            }
            TLRPC.InputPeer inputPeer = getInputPeer(readTask.dialogId);
            if (inputPeer instanceof TLRPC.TL_inputPeerChannel) {
                TLRPC.TL_channels_readHistory tL_channels_readHistory = new TLRPC.TL_channels_readHistory();
                tL_channels_readHistory.channel = getInputChannel(-readTask.dialogId);
                tL_channels_readHistory.max_id = readTask.maxId;
                tL_messages_readHistory = tL_channels_readHistory;
            } else {
                TLRPC.TL_messages_readHistory tL_messages_readHistory3 = new TLRPC.TL_messages_readHistory();
                tL_messages_readHistory3.peer = inputPeer;
                tL_messages_readHistory3.max_id = readTask.maxId;
                tL_messages_readHistory = tL_messages_readHistory3;
            }
            ConnectionsManager connectionsManager3 = getConnectionsManager();
            requestDelegate = new RequestDelegate() {
                @Override
                public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                    MessagesController.this.lambda$completeReadTask$226(tLObject, tL_error);
                }
            };
            tL_messages_readHistory2 = tL_messages_readHistory;
            connectionsManager = connectionsManager3;
        }
        connectionsManager.sendRequest(tL_messages_readHistory2, requestDelegate);
    }

    public static boolean equals(TLRPC.MessageMedia messageMedia, TLRPC.MessageMedia messageMedia2) {
        return messageMedia instanceof TLRPC.TL_messageMediaDocument ? (messageMedia2 instanceof TLRPC.TL_messageMediaDocument) && messageMedia.document.id == messageMedia2.document.id : (messageMedia2 instanceof TLRPC.TL_messageMediaPhoto) && (messageMedia2 instanceof TLRPC.TL_messageMediaPhoto) && messageMedia.photo.id == messageMedia2.photo.id;
    }

    private void fetchFolderInLoadedPinnedDialogs(TLRPC.TL_messages_peerDialogs tL_messages_peerDialogs) {
        TLRPC.InputPeer tL_inputPeerUser;
        long j;
        int size = tL_messages_peerDialogs.dialogs.size();
        int i = 0;
        for (int i2 = 0; i2 < size; i2++) {
            TLRPC.Dialog dialog = tL_messages_peerDialogs.dialogs.get(i2);
            if (dialog instanceof TLRPC.TL_dialogFolder) {
                TLRPC.TL_dialogFolder tL_dialogFolder = (TLRPC.TL_dialogFolder) dialog;
                long peerDialogId = DialogObject.getPeerDialogId(dialog.peer);
                if (tL_dialogFolder.top_message != 0 && peerDialogId != 0) {
                    int size2 = tL_messages_peerDialogs.messages.size();
                    for (int i3 = 0; i3 < size2; i3++) {
                        TLRPC.Message message = tL_messages_peerDialogs.messages.get(i3);
                        if (peerDialogId == MessageObject.getDialogId(message) && dialog.top_message == message.id) {
                            TLRPC.TL_dialog tL_dialog = new TLRPC.TL_dialog();
                            tL_dialog.peer = dialog.peer;
                            tL_dialog.top_message = dialog.top_message;
                            tL_dialog.folder_id = tL_dialogFolder.folder.id;
                            tL_dialog.flags |= 16;
                            tL_messages_peerDialogs.dialogs.add(tL_dialog);
                            TLRPC.Peer peer = dialog.peer;
                            if (peer instanceof TLRPC.TL_peerChannel) {
                                tL_inputPeerUser = new TLRPC.TL_inputPeerChannel();
                                tL_inputPeerUser.channel_id = dialog.peer.channel_id;
                                int size3 = tL_messages_peerDialogs.chats.size();
                                while (i < size3) {
                                    TLRPC.Chat chat = tL_messages_peerDialogs.chats.get(i);
                                    if (chat.id == tL_inputPeerUser.channel_id) {
                                        j = chat.access_hash;
                                        tL_inputPeerUser.access_hash = j;
                                        break;
                                    }
                                    i++;
                                }
                                loadUnknownDialog(tL_inputPeerUser, 0L);
                                return;
                            }
                            if (peer instanceof TLRPC.TL_peerChat) {
                                tL_inputPeerUser = new TLRPC.TL_inputPeerChat();
                                tL_inputPeerUser.chat_id = dialog.peer.chat_id;
                            } else {
                                tL_inputPeerUser = new TLRPC.TL_inputPeerUser();
                                tL_inputPeerUser.user_id = dialog.peer.user_id;
                                int size4 = tL_messages_peerDialogs.users.size();
                                while (i < size4) {
                                    TLRPC.User user = tL_messages_peerDialogs.users.get(i);
                                    if (user.id == tL_inputPeerUser.user_id) {
                                        j = user.access_hash;
                                        tL_inputPeerUser.access_hash = j;
                                        break;
                                    }
                                    i++;
                                }
                            }
                            loadUnknownDialog(tL_inputPeerUser, 0L);
                            return;
                        }
                    }
                    return;
                }
                tL_messages_peerDialogs.dialogs.remove(tL_dialogFolder);
            }
        }
    }

    private void getChannelDifference(long j) {
        getChannelDifference(j, 0, 0L, null);
    }

    public static SharedPreferences getEmojiSettings(int i) {
        return getInstance(i).emojiPreferences;
    }

    public static SharedPreferences getGlobalEmojiSettings() {
        return getInstance(0).emojiPreferences;
    }

    public static SharedPreferences getGlobalMainSettings() {
        return getInstance(0).mainPreferences;
    }

    public static SharedPreferences getGlobalNotificationsSettings() {
        return getInstance(0).notificationsPreferences;
    }

    public static TLRPC.InputChannel getInputChannel(TLRPC.Chat chat) {
        if (!(chat instanceof TLRPC.TL_channel) && !(chat instanceof TLRPC.TL_channelForbidden)) {
            return new TLRPC.TL_inputChannelEmpty();
        }
        TLRPC.TL_inputChannel tL_inputChannel = new TLRPC.TL_inputChannel();
        tL_inputChannel.channel_id = chat.id;
        tL_inputChannel.access_hash = chat.access_hash;
        return tL_inputChannel;
    }

    public static TLRPC.InputChannel getInputChannel(TLRPC.InputPeer inputPeer) {
        TLRPC.TL_inputChannel tL_inputChannel = new TLRPC.TL_inputChannel();
        tL_inputChannel.channel_id = inputPeer.channel_id;
        tL_inputChannel.access_hash = inputPeer.access_hash;
        return tL_inputChannel;
    }

    public static TLRPC.InputPeer getInputPeer(TLObject tLObject) {
        if (tLObject instanceof TLRPC.User) {
            return getInputPeer((TLRPC.User) tLObject);
        }
        if (tLObject instanceof TLRPC.Chat) {
            return getInputPeer((TLRPC.Chat) tLObject);
        }
        return null;
    }

    public static TLRPC.InputPeer getInputPeer(TLRPC.Chat chat) {
        if (!ChatObject.isChannel(chat)) {
            TLRPC.TL_inputPeerChat tL_inputPeerChat = new TLRPC.TL_inputPeerChat();
            tL_inputPeerChat.chat_id = chat.id;
            return tL_inputPeerChat;
        }
        TLRPC.TL_inputPeerChannel tL_inputPeerChannel = new TLRPC.TL_inputPeerChannel();
        tL_inputPeerChannel.channel_id = chat.id;
        tL_inputPeerChannel.access_hash = chat.access_hash;
        return tL_inputPeerChannel;
    }

    public static TLRPC.InputPeer getInputPeer(TLRPC.User user) {
        TLRPC.TL_inputPeerUser tL_inputPeerUser = new TLRPC.TL_inputPeerUser();
        tL_inputPeerUser.user_id = user.id;
        tL_inputPeerUser.access_hash = user.access_hash;
        return tL_inputPeerUser;
    }

    public static TLRPC.InputWallPaper getInputWallpaper(Theme.OverrideWallpaperInfo overrideWallpaperInfo) {
        if (overrideWallpaperInfo.wallpaperId <= 0) {
            TLRPC.TL_inputWallPaperSlug tL_inputWallPaperSlug = new TLRPC.TL_inputWallPaperSlug();
            tL_inputWallPaperSlug.slug = overrideWallpaperInfo.slug;
            return tL_inputWallPaperSlug;
        }
        TLRPC.TL_inputWallPaper tL_inputWallPaper = new TLRPC.TL_inputWallPaper();
        tL_inputWallPaper.id = overrideWallpaperInfo.wallpaperId;
        tL_inputWallPaper.access_hash = overrideWallpaperInfo.accessHash;
        return tL_inputWallPaper;
    }

    public static MessagesController getInstance(int i) {
        MessagesController messagesController = Instance[i];
        if (messagesController == null) {
            synchronized (lockObjects[i]) {
                try {
                    messagesController = Instance[i];
                    if (messagesController == null) {
                        MessagesController[] messagesControllerArr = Instance;
                        MessagesController messagesController2 = new MessagesController(i);
                        messagesControllerArr[i] = messagesController2;
                        messagesController = messagesController2;
                    }
                } finally {
                }
            }
        }
        return messagesController;
    }

    public static SharedPreferences getMainSettings(int i) {
        return getInstance(i).mainPreferences;
    }

    public static SharedPreferences getNotificationsSettings(int i) {
        return getInstance(i).notificationsPreferences;
    }

    public static TLRPC.Peer getPeerFromInputPeer(TLRPC.InputPeer inputPeer) {
        if (inputPeer.chat_id != 0) {
            TLRPC.TL_peerChat tL_peerChat = new TLRPC.TL_peerChat();
            tL_peerChat.chat_id = inputPeer.chat_id;
            return tL_peerChat;
        }
        if (inputPeer.channel_id != 0) {
            TLRPC.TL_peerChannel tL_peerChannel = new TLRPC.TL_peerChannel();
            tL_peerChannel.channel_id = inputPeer.channel_id;
            return tL_peerChannel;
        }
        TLRPC.TL_peerUser tL_peerUser = new TLRPC.TL_peerUser();
        tL_peerUser.user_id = inputPeer.user_id;
        return tL_peerUser;
    }

    private long getTagLongId(TLRPC.Reaction reaction) {
        long j;
        if (reaction == null) {
            return 0L;
        }
        long j2 = reaction.tag_long_id;
        if (j2 != 0) {
            return j2;
        }
        if (reaction instanceof TLRPC.TL_reactionEmoji) {
            j = MessagesController$$ExternalSyntheticBackport8.m(r0, 0, Utilities.MD5(((TLRPC.TL_reactionEmoji) reaction).emoticon).substring(0, 16).length(), 16);
        } else {
            if (!(reaction instanceof TLRPC.TL_reactionCustomEmoji)) {
                return 0L;
            }
            j = ((TLRPC.TL_reactionCustomEmoji) reaction).document_id;
        }
        reaction.tag_long_id = j;
        return j;
    }

    public static long getUpdateChannelId(TLRPC.Update update) {
        if (update instanceof TLRPC.TL_updateNewChannelMessage) {
            return ((TLRPC.TL_updateNewChannelMessage) update).message.peer_id.channel_id;
        }
        if (update instanceof TLRPC.TL_updateEditChannelMessage) {
            return ((TLRPC.TL_updateEditChannelMessage) update).message.peer_id.channel_id;
        }
        if (update instanceof TLRPC.TL_updateReadChannelOutbox) {
            return ((TLRPC.TL_updateReadChannelOutbox) update).channel_id;
        }
        if (update instanceof TLRPC.TL_updateChannelMessageViews) {
            return ((TLRPC.TL_updateChannelMessageViews) update).channel_id;
        }
        if (update instanceof TLRPC.TL_updateChannelMessageForwards) {
            return ((TLRPC.TL_updateChannelMessageForwards) update).channel_id;
        }
        if (update instanceof TLRPC.TL_updateChannelTooLong) {
            return ((TLRPC.TL_updateChannelTooLong) update).channel_id;
        }
        if (update instanceof TLRPC.TL_updateChannelReadMessagesContents) {
            return ((TLRPC.TL_updateChannelReadMessagesContents) update).channel_id;
        }
        if (update instanceof TLRPC.TL_updateChannelAvailableMessages) {
            return ((TLRPC.TL_updateChannelAvailableMessages) update).channel_id;
        }
        if (update instanceof TLRPC.TL_updateChannel) {
            return ((TLRPC.TL_updateChannel) update).channel_id;
        }
        if (update instanceof TLRPC.TL_updateChannelWebPage) {
            return ((TLRPC.TL_updateChannelWebPage) update).channel_id;
        }
        if (update instanceof TLRPC.TL_updateDeleteChannelMessages) {
            return ((TLRPC.TL_updateDeleteChannelMessages) update).channel_id;
        }
        if (update instanceof TLRPC.TL_updateReadChannelInbox) {
            return ((TLRPC.TL_updateReadChannelInbox) update).channel_id;
        }
        if (update instanceof TLRPC.TL_updateChannelPinnedTopic) {
            return ((TLRPC.TL_updateChannelPinnedTopic) update).channel_id;
        }
        if (update instanceof TLRPC.TL_updateChannelPinnedTopics) {
            return ((TLRPC.TL_updateChannelPinnedTopics) update).channel_id;
        }
        if (update instanceof TLRPC.TL_updateReadChannelDiscussionInbox) {
            return ((TLRPC.TL_updateReadChannelDiscussionInbox) update).channel_id;
        }
        if (update instanceof TLRPC.TL_updateReadChannelDiscussionOutbox) {
            return ((TLRPC.TL_updateReadChannelDiscussionOutbox) update).channel_id;
        }
        if (update instanceof TLRPC.TL_updateChannelUserTyping) {
            return ((TLRPC.TL_updateChannelUserTyping) update).channel_id;
        }
        if (update instanceof TLRPC.TL_updatePinnedChannelMessages) {
            return ((TLRPC.TL_updatePinnedChannelMessages) update).channel_id;
        }
        if (update instanceof TLRPC.TL_updateChannelViewForumAsMessages) {
            return ((TLRPC.TL_updateChannelViewForumAsMessages) update).channel_id;
        }
        if (!BuildVars.LOGS_ENABLED) {
            return 0L;
        }
        FileLog.e("trying to get unknown update channel_id for " + update);
        return 0L;
    }

    private static int getUpdatePts(TLRPC.Update update) {
        if (update instanceof TLRPC.TL_updateDeleteMessages) {
            return ((TLRPC.TL_updateDeleteMessages) update).pts;
        }
        if (update instanceof TLRPC.TL_updateNewChannelMessage) {
            return ((TLRPC.TL_updateNewChannelMessage) update).pts;
        }
        if (update instanceof TLRPC.TL_updateReadHistoryOutbox) {
            return ((TLRPC.TL_updateReadHistoryOutbox) update).pts;
        }
        if (update instanceof TLRPC.TL_updateNewMessage) {
            return ((TLRPC.TL_updateNewMessage) update).pts;
        }
        if (update instanceof TLRPC.TL_updateEditMessage) {
            return ((TLRPC.TL_updateEditMessage) update).pts;
        }
        if (update instanceof TLRPC.TL_updateWebPage) {
            return ((TLRPC.TL_updateWebPage) update).pts;
        }
        if (update instanceof TLRPC.TL_updateReadHistoryInbox) {
            return ((TLRPC.TL_updateReadHistoryInbox) update).pts;
        }
        if (update instanceof TLRPC.TL_updateChannelWebPage) {
            return ((TLRPC.TL_updateChannelWebPage) update).pts;
        }
        if (update instanceof TLRPC.TL_updateDeleteChannelMessages) {
            return ((TLRPC.TL_updateDeleteChannelMessages) update).pts;
        }
        if (update instanceof TLRPC.TL_updateEditChannelMessage) {
            return ((TLRPC.TL_updateEditChannelMessage) update).pts;
        }
        if (update instanceof TLRPC.TL_updateReadMessagesContents) {
            return ((TLRPC.TL_updateReadMessagesContents) update).pts;
        }
        if (update instanceof TLRPC.TL_updateChannelTooLong) {
            return ((TLRPC.TL_updateChannelTooLong) update).pts;
        }
        if (update instanceof TLRPC.TL_updateFolderPeers) {
            return ((TLRPC.TL_updateFolderPeers) update).pts;
        }
        if (update instanceof TLRPC.TL_updatePinnedChannelMessages) {
            return ((TLRPC.TL_updatePinnedChannelMessages) update).pts;
        }
        if (update instanceof TLRPC.TL_updatePinnedMessages) {
            return ((TLRPC.TL_updatePinnedMessages) update).pts;
        }
        return 0;
    }

    private static int getUpdatePtsCount(TLRPC.Update update) {
        if (update instanceof TLRPC.TL_updateDeleteMessages) {
            return ((TLRPC.TL_updateDeleteMessages) update).pts_count;
        }
        if (update instanceof TLRPC.TL_updateNewChannelMessage) {
            return ((TLRPC.TL_updateNewChannelMessage) update).pts_count;
        }
        if (update instanceof TLRPC.TL_updateReadHistoryOutbox) {
            return ((TLRPC.TL_updateReadHistoryOutbox) update).pts_count;
        }
        if (update instanceof TLRPC.TL_updateNewMessage) {
            return ((TLRPC.TL_updateNewMessage) update).pts_count;
        }
        if (update instanceof TLRPC.TL_updateEditMessage) {
            return ((TLRPC.TL_updateEditMessage) update).pts_count;
        }
        if (update instanceof TLRPC.TL_updateWebPage) {
            return ((TLRPC.TL_updateWebPage) update).pts_count;
        }
        if (update instanceof TLRPC.TL_updateReadHistoryInbox) {
            return ((TLRPC.TL_updateReadHistoryInbox) update).pts_count;
        }
        if (update instanceof TLRPC.TL_updateChannelWebPage) {
            return ((TLRPC.TL_updateChannelWebPage) update).pts_count;
        }
        if (update instanceof TLRPC.TL_updateDeleteChannelMessages) {
            return ((TLRPC.TL_updateDeleteChannelMessages) update).pts_count;
        }
        if (update instanceof TLRPC.TL_updateEditChannelMessage) {
            return ((TLRPC.TL_updateEditChannelMessage) update).pts_count;
        }
        if (update instanceof TLRPC.TL_updateReadMessagesContents) {
            return ((TLRPC.TL_updateReadMessagesContents) update).pts_count;
        }
        if (update instanceof TLRPC.TL_updateFolderPeers) {
            return ((TLRPC.TL_updateFolderPeers) update).pts_count;
        }
        if (update instanceof TLRPC.TL_updatePinnedChannelMessages) {
            return ((TLRPC.TL_updatePinnedChannelMessages) update).pts_count;
        }
        if (update instanceof TLRPC.TL_updatePinnedMessages) {
            return ((TLRPC.TL_updatePinnedMessages) update).pts_count;
        }
        return 0;
    }

    private static int getUpdateQts(TLRPC.Update update) {
        if (update instanceof TLRPC.TL_updateNewEncryptedMessage) {
            return ((TLRPC.TL_updateNewEncryptedMessage) update).qts;
        }
        return 0;
    }

    private int getUpdateSeq(TLRPC.Updates updates) {
        return updates instanceof TLRPC.TL_updatesCombined ? updates.seq_start : updates.seq;
    }

    private int getUpdateType(TLRPC.Update update) {
        if ((update instanceof TLRPC.TL_updateNewMessage) || (update instanceof TLRPC.TL_updateReadMessagesContents) || (update instanceof TLRPC.TL_updateReadHistoryInbox) || (update instanceof TLRPC.TL_updateReadHistoryOutbox) || (update instanceof TLRPC.TL_updateDeleteMessages) || (update instanceof TLRPC.TL_updateWebPage) || (update instanceof TLRPC.TL_updateEditMessage) || (update instanceof TLRPC.TL_updateFolderPeers) || (update instanceof TLRPC.TL_updatePinnedMessages)) {
            return 0;
        }
        if (update instanceof TLRPC.TL_updateNewEncryptedMessage) {
            return 1;
        }
        return ((update instanceof TLRPC.TL_updateNewChannelMessage) || (update instanceof TLRPC.TL_updateDeleteChannelMessages) || (update instanceof TLRPC.TL_updateEditChannelMessage) || (update instanceof TLRPC.TL_updateChannelWebPage) || (update instanceof TLRPC.TL_updatePinnedChannelMessages)) ? 2 : 3;
    }

    private String getUserNameForTyping(TLRPC.User user) {
        String str;
        if (user == null) {
            return "";
        }
        String str2 = user.first_name;
        if (str2 == null || str2.length() <= 0) {
            String str3 = user.last_name;
            if (str3 == null || str3.length() <= 0) {
                return "";
            }
            str = user.last_name;
        } else {
            str = user.first_name;
        }
        return AndroidUtilities.removeDiacritics(str);
    }

    public static org.telegram.tgnet.TLRPC.TL_wallPaperSettings getWallpaperSetting(org.telegram.ui.ActionBar.Theme.OverrideWallpaperInfo r5) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MessagesController.getWallpaperSetting(org.telegram.ui.ActionBar.Theme$OverrideWallpaperInfo):org.telegram.tgnet.TLRPC$TL_wallPaperSettings");
    }

    public static boolean isStoryQualityFull() {
        return getInstance(UserConfig.selectedAccount).isStoryQualityFullOnAccount();
    }

    public static boolean isSupportUser(TLRPC.User user) {
        if (user != null) {
            if (!user.support) {
                long j = user.id;
                if (j == 777000 || j == 333000 || j == 4240000 || j == 4244000 || j == 4245000 || j == 4246000 || j == 410000 || j == 420000 || j == 431000 || j == 431415000 || j == 434000 || j == 4243000 || j == 439000 || j == 449000 || j == 450000 || j == 452000 || j == 454000 || j == 4254000 || j == 455000 || j == 460000 || j == 470000 || j == 479000 || j == 796000 || j == 482000 || j == 490000 || j == 496000 || j == 497000 || j == 498000 || j == 4298000) {
                }
            }
            return true;
        }
        return false;
    }

    private int isValidUpdate(TLRPC.Updates updates, int i) {
        if (i == 0) {
            int updateSeq = getUpdateSeq(updates);
            if (getMessagesStorage().getLastSeqValue() + 1 == updateSeq || getMessagesStorage().getLastSeqValue() == updateSeq) {
                return 0;
            }
            return getMessagesStorage().getLastSeqValue() < updateSeq ? 1 : 2;
        }
        if (i == 1) {
            if (updates.pts <= getMessagesStorage().getLastPtsValue()) {
                return 2;
            }
            return getMessagesStorage().getLastPtsValue() + updates.pts_count == updates.pts ? 0 : 1;
        }
        if (i != 2) {
            return 0;
        }
        if (updates.pts <= getMessagesStorage().getLastQtsValue()) {
            return 2;
        }
        return getMessagesStorage().getLastQtsValue() + updates.updates.size() == updates.pts ? 0 : 1;
    }

    public void lambda$addDialogToFolder$186(long j, TLObject tLObject, TLRPC.TL_error tL_error) {
        if (tL_error == null) {
            processUpdates((TLRPC.Updates) tLObject, false);
        }
        if (j != 0) {
            getMessagesStorage().removePendingTask(j);
        }
    }

    public void lambda$addToViewsQueue$218(MessageObject messageObject) {
        long dialogId = messageObject.getDialogId();
        int id = messageObject.getId();
        ArrayList arrayList = (ArrayList) this.channelViewsToSend.get(dialogId);
        if (arrayList == null) {
            arrayList = new ArrayList();
            this.channelViewsToSend.put(dialogId, arrayList);
        }
        if (arrayList.contains(Integer.valueOf(id))) {
            return;
        }
        arrayList.add(Integer.valueOf(id));
    }

    public void lambda$addUserToChat$278(long j) {
        this.joiningToChannels.remove(Long.valueOf(j));
    }

    public void lambda$addUserToChat$279(Utilities.Callback callback, TLRPC.TL_messages_invitedUsers tL_messages_invitedUsers, long j) {
        if (callback != null) {
            callback.run(tL_messages_invitedUsers);
        } else {
            AlertsCreator.checkRestrictedInviteUsers(this.currentAccount, getChat(Long.valueOf(j)), tL_messages_invitedUsers);
        }
    }

    public static void lambda$addUserToChat$280(Utilities.Callback callback) {
        if (callback != null) {
            callback.run(null);
        }
    }

    public void lambda$addUserToChat$281(ErrorDelegate errorDelegate, TLRPC.TL_error tL_error, BaseFragment baseFragment, TLObject tLObject, boolean z, boolean z2) {
        if (errorDelegate.run(tL_error)) {
            AlertsCreator.processError(this.currentAccount, tL_error, baseFragment, tLObject, Boolean.valueOf(z && !z2));
        }
    }

    public void lambda$addUserToChat$282(ErrorDelegate errorDelegate, TLRPC.TL_error tL_error, BaseFragment baseFragment, TLObject tLObject, boolean z, boolean z2, TLRPC.InputUser inputUser) {
        if (errorDelegate == null) {
            AlertsCreator.processError(this.currentAccount, tL_error, baseFragment, tLObject, Boolean.valueOf(z && !z2));
        }
        if (z && (inputUser instanceof TLRPC.TL_inputUserSelf)) {
            getNotificationCenter().lambda$postNotificationNameOnUIThread$1(NotificationCenter.updateInterfaces, Integer.valueOf(UPDATE_MASK_CHAT));
        }
    }

    public static void lambda$addUserToChat$283(Utilities.Callback callback) {
        if (callback != null) {
            callback.run(null);
        }
    }

    public void lambda$addUserToChat$284(TLRPC.Updates updates, Utilities.Callback callback, TLRPC.TL_messages_invitedUsers tL_messages_invitedUsers, long j) {
        putUsers(updates.users, false);
        putChats(updates.chats, false);
        if (callback != null) {
            callback.run(tL_messages_invitedUsers);
        } else {
            AlertsCreator.checkRestrictedInviteUsers(this.currentAccount, getChat(Long.valueOf(j)), tL_messages_invitedUsers);
        }
    }

    public void lambda$addUserToChat$285(long j) {
        loadFullChat(j, 0, true);
    }

    public void lambda$addUserToChat$287(final boolean z, final TLRPC.InputUser inputUser, final long j, TLRPC.User user, final Utilities.Callback callback, boolean z2, Runnable runnable, final ErrorDelegate errorDelegate, final BaseFragment baseFragment, final TLObject tLObject, final boolean z3, TLObject tLObject2, final TLRPC.TL_error tL_error) {
        TLRPC.Updates updates;
        TLRPC.TL_messages_invitedUsers tL_messages_invitedUsers;
        boolean z4;
        if (z && (inputUser instanceof TLRPC.TL_inputUserSelf)) {
            AndroidUtilities.runOnUIThread(new Runnable() {
                @Override
                public final void run() {
                    MessagesController.this.lambda$addUserToChat$278(j);
                }
            });
        }
        if (tL_error != null) {
            if ("PRIVACY_PREMIUM_REQUIRED".equals(tL_error.text)) {
                final TLRPC.TL_messages_invitedUsers tL_messages_invitedUsers2 = new TLRPC.TL_messages_invitedUsers();
                tL_messages_invitedUsers2.updates = new TLRPC.TL_updates();
                TLRPC.TL_missingInvitee tL_missingInvitee = new TLRPC.TL_missingInvitee();
                tL_missingInvitee.user_id = user.id;
                tL_missingInvitee.premium_would_allow_invite = true;
                tL_missingInvitee.premium_required_for_pm = true;
                tL_messages_invitedUsers2.missing_invitees.add(tL_missingInvitee);
                AndroidUtilities.runOnUIThread(new Runnable() {
                    @Override
                    public final void run() {
                        MessagesController.this.lambda$addUserToChat$279(callback, tL_messages_invitedUsers2, j);
                    }
                });
                return;
            }
            AndroidUtilities.runOnUIThread(new Runnable() {
                @Override
                public final void run() {
                    MessagesController.lambda$addUserToChat$280(Utilities.Callback.this);
                }
            });
            if ("USER_ALREADY_PARTICIPANT".equals(tL_error.text) && z2) {
                if (runnable != null) {
                    AndroidUtilities.runOnUIThread(runnable);
                    return;
                }
                return;
            } else {
                if (errorDelegate != null) {
                    AndroidUtilities.runOnUIThread(new Runnable() {
                        @Override
                        public final void run() {
                            MessagesController.this.lambda$addUserToChat$281(errorDelegate, tL_error, baseFragment, tLObject, z, z3);
                        }
                    });
                }
                AndroidUtilities.runOnUIThread(new Runnable() {
                    @Override
                    public final void run() {
                        MessagesController.this.lambda$addUserToChat$282(errorDelegate, tL_error, baseFragment, tLObject, z, z3, inputUser);
                    }
                });
                return;
            }
        }
        if (tLObject2 instanceof TLRPC.TL_messages_invitedUsers) {
            tL_messages_invitedUsers = (TLRPC.TL_messages_invitedUsers) tLObject2;
            updates = tL_messages_invitedUsers.updates;
        } else {
            if (!(tLObject2 instanceof TLRPC.Updates)) {
                FileLog.e("unexpected " + tLObject2 + " in addUserToChat");
                AndroidUtilities.runOnUIThread(new Runnable() {
                    @Override
                    public final void run() {
                        MessagesController.lambda$addUserToChat$283(Utilities.Callback.this);
                    }
                });
                return;
            }
            updates = (TLRPC.Updates) tLObject2;
            tL_messages_invitedUsers = null;
        }
        int i = 0;
        while (true) {
            if (i >= updates.updates.size()) {
                z4 = false;
                break;
            }
            TLRPC.Update update = updates.updates.get(i);
            if ((update instanceof TLRPC.TL_updateNewChannelMessage) && (((TLRPC.TL_updateNewChannelMessage) update).message.action instanceof TLRPC.TL_messageActionChatAddUser)) {
                z4 = true;
                break;
            }
            i++;
        }
        processUpdates(updates, false);
        if (tL_messages_invitedUsers != null && !tL_messages_invitedUsers.missing_invitees.isEmpty()) {
            final TLRPC.Updates updates2 = updates;
            final TLRPC.TL_messages_invitedUsers tL_messages_invitedUsers3 = tL_messages_invitedUsers;
            AndroidUtilities.runOnUIThread(new Runnable() {
                @Override
                public final void run() {
                    MessagesController.this.lambda$addUserToChat$284(updates2, callback, tL_messages_invitedUsers3, j);
                }
            });
            return;
        }
        if (z) {
            if (!z4 && (inputUser instanceof TLRPC.TL_inputUserSelf)) {
                generateJoinMessage(j, true);
            }
            AndroidUtilities.runOnUIThread(new Runnable() {
                @Override
                public final void run() {
                    MessagesController.this.lambda$addUserToChat$285(j);
                }
            }, 1000L);
        }
        if (z && (inputUser instanceof TLRPC.TL_inputUserSelf)) {
            getMessagesStorage().updateDialogsWithDeletedMessages(-j, j, new ArrayList<>(), null, true);
        }
        if (runnable != null) {
            AndroidUtilities.runOnUIThread(runnable);
        }
        if (callback != null) {
            AndroidUtilities.runOnUIThread(new Runnable() {
                @Override
                public final void run() {
                    Utilities.Callback.this.run(null);
                }
            });
        }
    }

    public void lambda$addUsersToChannel$251(TLRPC.TL_error tL_error, BaseFragment baseFragment, TLRPC.TL_channels_inviteToChannel tL_channels_inviteToChannel) {
        AlertsCreator.processError(this.currentAccount, tL_error, baseFragment, tL_channels_inviteToChannel, Boolean.TRUE);
    }

    public void lambda$addUsersToChannel$252(TLRPC.TL_messages_invitedUsers tL_messages_invitedUsers, long j) {
        putUsers(tL_messages_invitedUsers.updates.users, false);
        putChats(tL_messages_invitedUsers.updates.chats, false);
        AlertsCreator.checkRestrictedInviteUsers(this.currentAccount, getChat(Long.valueOf(j)), tL_messages_invitedUsers);
    }

    public void lambda$addUsersToChannel$253(final BaseFragment baseFragment, final TLRPC.TL_channels_inviteToChannel tL_channels_inviteToChannel, final long j, TLObject tLObject, final TLRPC.TL_error tL_error) {
        if (tL_error != null) {
            AndroidUtilities.runOnUIThread(new Runnable() {
                @Override
                public final void run() {
                    MessagesController.this.lambda$addUsersToChannel$251(tL_error, baseFragment, tL_channels_inviteToChannel);
                }
            });
        } else if (tLObject instanceof TLRPC.TL_messages_invitedUsers) {
            final TLRPC.TL_messages_invitedUsers tL_messages_invitedUsers = (TLRPC.TL_messages_invitedUsers) tLObject;
            processUpdates(tL_messages_invitedUsers.updates, false);
            AndroidUtilities.runOnUIThread(new Runnable() {
                @Override
                public final void run() {
                    MessagesController.this.lambda$addUsersToChannel$252(tL_messages_invitedUsers, j);
                }
            });
        }
    }

    public static void lambda$addUsersToChat$274(Consumer consumer, TLRPC.User user) {
        if (consumer != null) {
            consumer.accept(user);
        }
    }

    public static boolean lambda$addUsersToChat$275(Consumer consumer, TLRPC.User user, TLRPC.TL_error tL_error) {
        if (consumer != null) {
            consumer.accept(user);
        }
        return tL_error == null || !"USER_PRIVACY_RESTRICTED".equals(tL_error.text);
    }

    public void lambda$addUsersToChat$276(TLRPC.Chat chat, TLRPC.TL_messages_invitedUsers tL_messages_invitedUsers) {
        AlertsCreator.checkRestrictedInviteUsers(this.currentAccount, chat, tL_messages_invitedUsers);
    }

    public void lambda$addUsersToChat$277(final TLRPC.TL_messages_invitedUsers tL_messages_invitedUsers, int[] iArr, int i, final TLRPC.Chat chat, Runnable runnable, TLRPC.TL_messages_invitedUsers tL_messages_invitedUsers2) {
        if (tL_messages_invitedUsers2 != null) {
            tL_messages_invitedUsers.missing_invitees.addAll(tL_messages_invitedUsers2.missing_invitees);
        }
        int i2 = iArr[0] + 1;
        iArr[0] = i2;
        if (i2 >= i) {
            if (!tL_messages_invitedUsers.missing_invitees.isEmpty()) {
                AndroidUtilities.runOnUIThread(new Runnable() {
                    @Override
                    public final void run() {
                        MessagesController.this.lambda$addUsersToChat$276(chat, tL_messages_invitedUsers);
                    }
                });
            }
            if (runnable != null) {
                runnable.run();
            }
        }
    }

    public void lambda$applyAppConfig$32() {
        getNotificationCenter().lambda$postNotificationNameOnUIThread$1(NotificationCenter.storiesEnabledUpdate, new Object[0]);
    }

    public static void lambda$blockPeer$83(TLObject tLObject, TLRPC.TL_error tL_error) {
    }

    public void lambda$changeChatAvatar$295(Runnable runnable) {
        if (runnable != null) {
            runnable.run();
        }
        getNotificationCenter().lambda$postNotificationNameOnUIThread$1(NotificationCenter.updateInterfaces, Integer.valueOf(UPDATE_MASK_AVATAR));
        getNotificationCenter().lambda$postNotificationNameOnUIThread$1(NotificationCenter.reloadDialogPhotos, new Object[0]);
    }

    public void lambda$changeChatAvatar$296(TLRPC.TL_inputChatPhoto tL_inputChatPhoto, TLRPC.FileLocation fileLocation, TLRPC.FileLocation fileLocation2, String str, long j, final Runnable runnable, TLObject tLObject, TLRPC.TL_error tL_error) {
        TLRPC.Photo photo;
        if (tL_error != null) {
            return;
        }
        TLRPC.Updates updates = (TLRPC.Updates) tLObject;
        if (tL_inputChatPhoto == null) {
            int size = updates.updates.size();
            int i = 0;
            while (true) {
                if (i >= size) {
                    photo = null;
                    break;
                }
                TLRPC.Update update = updates.updates.get(i);
                if (update instanceof TLRPC.TL_updateNewChannelMessage) {
                    TLRPC.MessageAction messageAction = ((TLRPC.TL_updateNewChannelMessage) update).message.action;
                    if (messageAction instanceof TLRPC.TL_messageActionChatEditPhoto) {
                        photo = messageAction.photo;
                        if (photo instanceof TLRPC.TL_photo) {
                            break;
                        }
                    } else {
                        continue;
                    }
                    i++;
                } else {
                    if (update instanceof TLRPC.TL_updateNewMessage) {
                        TLRPC.MessageAction messageAction2 = ((TLRPC.TL_updateNewMessage) update).message.action;
                        if (messageAction2 instanceof TLRPC.TL_messageActionChatEditPhoto) {
                            photo = messageAction2.photo;
                            if (photo instanceof TLRPC.TL_photo) {
                                break;
                            }
                        } else {
                            continue;
                        }
                    } else {
                        continue;
                    }
                    i++;
                }
            }
            if (photo != null) {
                TLRPC.PhotoSize closestPhotoSizeWithSize = FileLoader.getClosestPhotoSizeWithSize(photo.sizes, 150);
                TLRPC.VideoSize videoSize = photo.video_sizes.isEmpty() ? null : photo.video_sizes.get(0);
                if (closestPhotoSizeWithSize != null && fileLocation != null) {
                    getFileLoader().getPathToAttach(fileLocation, true).renameTo(getFileLoader().getPathToAttach(closestPhotoSizeWithSize, true));
                    ImageLoader.getInstance().replaceImageInCache(fileLocation.volume_id + "_" + fileLocation.local_id + "@50_50", closestPhotoSizeWithSize.location.volume_id + "_" + closestPhotoSizeWithSize.location.local_id + "@50_50", ImageLocation.getForPhoto(closestPhotoSizeWithSize, photo), true);
                }
                TLRPC.PhotoSize closestPhotoSizeWithSize2 = FileLoader.getClosestPhotoSizeWithSize(photo.sizes, 800);
                if (closestPhotoSizeWithSize2 != null && fileLocation2 != null) {
                    getFileLoader().getPathToAttach(fileLocation2, true).renameTo(getFileLoader().getPathToAttach(closestPhotoSizeWithSize2, true));
                }
                if (videoSize != null && str != null) {
                    new File(str).renameTo(getFileLoader().getPathToAttach(videoSize, "mp4", true));
                }
                getDialogPhotos(-j).addPhotoAtStart(photo);
            }
        }
        processUpdates(updates, false);
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                MessagesController.this.lambda$changeChatAvatar$295(runnable);
            }
        });
    }

    public void lambda$changeChatTitle$294(TLObject tLObject, TLRPC.TL_error tL_error) {
        if (tL_error != null) {
            return;
        }
        processUpdates((TLRPC.Updates) tLObject, false);
    }

    public void lambda$checkCanOpenChat$403(AlertDialog alertDialog, Browser.Progress progress, TLObject tLObject, BaseFragment baseFragment, Bundle bundle) {
        if (alertDialog != null) {
            try {
                alertDialog.dismiss();
            } catch (Exception e) {
                FileLog.e(e);
            }
        }
        if (progress != null) {
            progress.end();
        }
        TLRPC.messages_Messages messages_messages = (TLRPC.messages_Messages) tLObject;
        putUsers(messages_messages.users, false);
        putChats(messages_messages.chats, false);
        getMessagesStorage().putUsersAndChats(messages_messages.users, messages_messages.chats, true, true);
        baseFragment.presentFragment(new ChatActivity(bundle), true);
    }

    public void lambda$checkCanOpenChat$404(final AlertDialog alertDialog, final Browser.Progress progress, final BaseFragment baseFragment, final Bundle bundle, final TLObject tLObject, TLRPC.TL_error tL_error) {
        if (tLObject != null) {
            AndroidUtilities.runOnUIThread(new Runnable() {
                @Override
                public final void run() {
                    MessagesController.this.lambda$checkCanOpenChat$403(alertDialog, progress, tLObject, baseFragment, bundle);
                }
            });
        }
    }

    public void lambda$checkCanOpenChat$405(int i, BaseFragment baseFragment, DialogInterface dialogInterface) {
        getConnectionsManager().cancelRequest(i, true);
        baseFragment.setVisibleDialog(null);
    }

    public void lambda$checkCanOpenChat$406(int i, BaseFragment baseFragment) {
        getConnectionsManager().cancelRequest(i, true);
        baseFragment.setVisibleDialog(null);
    }

    public void lambda$checkChatInviter$348(TLRPC.TL_channels_channelParticipant tL_channels_channelParticipant) {
        putUsers(tL_channels_channelParticipant.users, false);
        putChats(tL_channels_channelParticipant.chats, false);
    }

    public void lambda$checkChatInviter$349(ArrayList arrayList) {
        getNotificationsController().processNewMessages(arrayList, true, false, null);
    }

    public void lambda$checkChatInviter$350(final ArrayList arrayList) {
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                MessagesController.this.lambda$checkChatInviter$349(arrayList);
            }
        });
    }

    public void lambda$checkChatInviter$351(long j, ArrayList arrayList, TLRPC.TL_channels_channelParticipant tL_channels_channelParticipant) {
        this.gettingChatInviters.delete(j);
        if (arrayList != null) {
            updateInterfaceWithMessages(-j, arrayList, 0);
            getNotificationCenter().lambda$postNotificationNameOnUIThread$1(NotificationCenter.dialogsNeedReload, new Object[0]);
        }
        getNotificationCenter().lambda$postNotificationNameOnUIThread$1(NotificationCenter.didLoadChatInviter, Long.valueOf(j), Long.valueOf(tL_channels_channelParticipant.participant.inviter_id));
    }

    public void lambda$checkChatInviter$352(TLRPC.Chat chat, boolean z, final long j, TLObject tLObject, TLRPC.TL_error tL_error) {
        final ArrayList arrayList;
        final TLRPC.TL_channels_channelParticipant tL_channels_channelParticipant = (TLRPC.TL_channels_channelParticipant) tLObject;
        if (tL_channels_channelParticipant != null) {
            TLRPC.ChannelParticipant channelParticipant = tL_channels_channelParticipant.participant;
            if (channelParticipant instanceof TLRPC.TL_channelParticipantSelf) {
                TLRPC.TL_channelParticipantSelf tL_channelParticipantSelf = (TLRPC.TL_channelParticipantSelf) channelParticipant;
                if (chat.megagroup && getMessagesStorage().isMigratedChat(chat.id)) {
                    return;
                }
                AndroidUtilities.runOnUIThread(new Runnable() {
                    @Override
                    public final void run() {
                        MessagesController.this.lambda$checkChatInviter$348(tL_channels_channelParticipant);
                    }
                });
                getMessagesStorage().putUsersAndChats(tL_channels_channelParticipant.users, tL_channels_channelParticipant.chats, true, true);
                if (!z || Math.abs(getConnectionsManager().getCurrentTime() - tL_channels_channelParticipant.participant.date) >= 86400 || getMessagesStorage().hasInviteMeMessage(j)) {
                    arrayList = null;
                } else {
                    TLRPC.TL_messageService tL_messageService = new TLRPC.TL_messageService();
                    tL_messageService.flags = 256;
                    int newMessageId = getUserConfig().getNewMessageId();
                    tL_messageService.id = newMessageId;
                    tL_messageService.local_id = newMessageId;
                    tL_messageService.date = tL_channels_channelParticipant.participant.date;
                    TLRPC.TL_peerUser tL_peerUser = new TLRPC.TL_peerUser();
                    tL_messageService.from_id = tL_peerUser;
                    tL_peerUser.user_id = tL_channels_channelParticipant.participant.inviter_id;
                    TLRPC.TL_peerChannel tL_peerChannel = new TLRPC.TL_peerChannel();
                    tL_messageService.peer_id = tL_peerChannel;
                    tL_peerChannel.channel_id = j;
                    tL_messageService.media_unread = true;
                    tL_messageService.unread = true;
                    tL_messageService.post = true;
                    tL_messageService.action = (tL_channelParticipantSelf.via_invite && tL_channelParticipantSelf.inviter_id == getUserConfig().getClientUserId()) ? new TLRPC.TL_messageActionChatJoinedByRequest() : new TLRPC.TL_messageActionChatAddUser();
                    tL_messageService.action.users.add(Long.valueOf(getUserConfig().getClientUserId()));
                    tL_messageService.dialog_id = -j;
                    getUserConfig().saveConfig(false);
                    final ArrayList arrayList2 = new ArrayList();
                    ArrayList<TLRPC.Message> arrayList3 = new ArrayList<>();
                    ConcurrentHashMap concurrentHashMap = new ConcurrentHashMap();
                    for (int i = 0; i < tL_channels_channelParticipant.users.size(); i++) {
                        TLRPC.User user = tL_channels_channelParticipant.users.get(i);
                        concurrentHashMap.put(Long.valueOf(user.id), user);
                    }
                    arrayList3.add(tL_messageService);
                    arrayList2.add(new MessageObject(this.currentAccount, (TLRPC.Message) tL_messageService, (AbstractMap<Long, TLRPC.User>) concurrentHashMap, true, false));
                    if (tL_messageService.from_id.user_id != getUserConfig().getClientUserId()) {
                        getMessagesStorage().getStorageQueue().postRunnable(new Runnable() {
                            @Override
                            public final void run() {
                                MessagesController.this.lambda$checkChatInviter$350(arrayList2);
                            }
                        });
                    }
                    getMessagesStorage().putMessages(arrayList3, true, true, false, 0, 0, 0L);
                    arrayList = arrayList2;
                }
                getMessagesStorage().saveChatInviter(j, tL_channels_channelParticipant.participant.inviter_id);
                AndroidUtilities.runOnUIThread(new Runnable() {
                    @Override
                    public final void run() {
                        MessagesController.this.lambda$checkChatInviter$351(j, arrayList, tL_channels_channelParticipant);
                    }
                });
            }
        }
    }

    public void lambda$checkChatlistFolderUpdate$429(TLObject tLObject, int i, ChatlistUpdatesStat chatlistUpdatesStat) {
        if (!(tLObject instanceof TL_chatlists.TL_chatlists_chatlistUpdates)) {
            chatlistUpdatesStat.loading = false;
            return;
        }
        TL_chatlists.TL_chatlists_chatlistUpdates tL_chatlists_chatlistUpdates = (TL_chatlists.TL_chatlists_chatlistUpdates) tLObject;
        putChats(tL_chatlists_chatlistUpdates.chats, false);
        putUsers(tL_chatlists_chatlistUpdates.users, false);
        this.chatlistFoldersUpdates.put(i, new ChatlistUpdatesStat(tL_chatlists_chatlistUpdates));
        getNotificationCenter().lambda$postNotificationNameOnUIThread$1(NotificationCenter.chatlistFolderUpdate, Integer.valueOf(i));
    }

    public void lambda$checkChatlistFolderUpdate$430(final int i, final ChatlistUpdatesStat chatlistUpdatesStat, final TLObject tLObject, TLRPC.TL_error tL_error) {
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                MessagesController.this.lambda$checkChatlistFolderUpdate$429(tLObject, i, chatlistUpdatesStat);
            }
        });
    }

    public void lambda$checkDeletingTask$79(LongSparseArray longSparseArray, LongSparseArray longSparseArray2) {
        getNewDeleteTask(longSparseArray, longSparseArray2);
        this.currentDeletingTaskTime = 0;
        this.currentDeletingTaskMids = null;
        this.currentDeletingTaskMediaMids = null;
    }

    public void lambda$checkDeletingTask$80(final LongSparseArray longSparseArray, final LongSparseArray longSparseArray2) {
        long j;
        ArrayList<Integer> arrayList;
        boolean z = true;
        if (longSparseArray != null) {
            int size = longSparseArray.size();
            for (int i = 0; i < size; i++) {
                ArrayList<Integer> arrayList2 = (ArrayList) longSparseArray.valueAt(i);
                deleteMessages(arrayList2, null, null, longSparseArray.keyAt(i), 0, true, 0, !arrayList2.isEmpty() && arrayList2.get(0).intValue() > 0);
            }
        }
        if (longSparseArray2 != null) {
            boolean z2 = SecretMediaViewer.hasInstance() && SecretMediaViewer.getInstance().isVisible();
            MessageObject currentMessageObject = z2 ? SecretMediaViewer.getInstance().getCurrentMessageObject() : null;
            int size2 = longSparseArray2.size();
            int i2 = 0;
            while (i2 < size2) {
                final long keyAt = longSparseArray2.keyAt(i2);
                ArrayList<Integer> arrayList3 = (ArrayList) longSparseArray2.valueAt(i2);
                if (z2 && currentMessageObject != null && currentMessageObject.currentAccount == this.currentAccount && currentMessageObject.getDialogId() == keyAt && arrayList3.contains(Integer.valueOf(currentMessageObject.getId()))) {
                    final int id = currentMessageObject.getId();
                    arrayList3.remove(Integer.valueOf(id));
                    currentMessageObject.forceExpired = z;
                    final long createDeleteShowOnceTask = createDeleteShowOnceTask(keyAt, id);
                    j = keyAt;
                    arrayList = arrayList3;
                    SecretMediaViewer.getInstance().setOnClose(new Runnable() {
                        @Override
                        public final void run() {
                            MessagesController.this.lambda$checkDeletingTask$78(createDeleteShowOnceTask, keyAt, id);
                        }
                    });
                    getNotificationCenter().lambda$postNotificationNameOnUIThread$1(NotificationCenter.updateMessageMedia, currentMessageObject.messageOwner);
                } else {
                    j = keyAt;
                    arrayList = arrayList3;
                }
                if (!arrayList.isEmpty()) {
                    getMessagesStorage().emptyMessagesMedia(j, arrayList);
                }
                i2++;
                z = true;
            }
        }
        Utilities.stageQueue.postRunnable(new Runnable() {
            @Override
            public final void run() {
                MessagesController.this.lambda$checkDeletingTask$79(longSparseArray, longSparseArray2);
            }
        });
    }

    public static void lambda$checkIsInChat$425(IsInChatCheckedCallback isInChatCheckedCallback, TLObject tLObject, TLRPC.TL_error tL_error) {
        if (isInChatCheckedCallback != null) {
            TLRPC.ChannelParticipant channelParticipant = tLObject instanceof TLRPC.TL_channels_channelParticipant ? ((TLRPC.TL_channels_channelParticipant) tLObject).participant : null;
            isInChatCheckedCallback.run((tL_error != null || channelParticipant == null || channelParticipant.left) ? false : true, channelParticipant != null ? channelParticipant.admin_rights : null, channelParticipant != null ? channelParticipant.rank : null);
        }
    }

    public void lambda$checkLastDialogMessage$212(TLRPC.Dialog dialog) {
        deleteDialog(dialog.id, 3);
    }

    public void lambda$checkLastDialogMessage$213(final TLRPC.Dialog dialog) {
        if (BuildVars.LOGS_ENABLED) {
            FileLog.d("checkLastDialogMessage for " + dialog.id + " has not message");
        }
        if (getMediaDataController().getDraft(dialog.id, 0L) == null) {
            TLRPC.Dialog dialog2 = (TLRPC.Dialog) this.dialogs_dict.get(dialog.id);
            if (dialog2 == null) {
                if (BuildVars.LOGS_ENABLED) {
                    FileLog.d("checkLastDialogMessage for " + dialog.id + " current dialog not found");
                }
                getMessagesStorage().isDialogHasTopMessage(dialog.id, new Runnable() {
                    @Override
                    public final void run() {
                        MessagesController.this.lambda$checkLastDialogMessage$212(dialog);
                    }
                });
                return;
            }
            if (BuildVars.LOGS_ENABLED) {
                FileLog.d("checkLastDialogMessage for " + dialog.id + " current dialog top message " + dialog2.top_message);
            }
            if (dialog2.top_message == 0) {
                deleteDialog(dialog.id, 3);
            }
        }
    }

    public void lambda$checkLastDialogMessage$214(TLRPC.Dialog dialog) {
        this.checkingLastMessagesDialogs.delete(dialog.id);
    }

    public void lambda$checkLastDialogMessage$215(final TLRPC.Dialog dialog, long j, TLObject tLObject, TLRPC.TL_error tL_error) {
        if (tLObject != null) {
            TLRPC.messages_Messages messages_messages = (TLRPC.messages_Messages) tLObject;
            removeDeletedMessagesFromArray(dialog.id, messages_messages.messages);
            if (messages_messages.messages.isEmpty()) {
                AndroidUtilities.runOnUIThread(new Runnable() {
                    @Override
                    public final void run() {
                        MessagesController.this.lambda$checkLastDialogMessage$213(dialog);
                    }
                });
            } else {
                if (BuildVars.LOGS_ENABLED) {
                    FileLog.d("checkLastDialogMessage for " + dialog.id + " has message");
                }
                TLRPC.TL_messages_dialogs tL_messages_dialogs = new TLRPC.TL_messages_dialogs();
                TLRPC.Message message = messages_messages.messages.get(0);
                TLRPC.TL_dialog tL_dialog = new TLRPC.TL_dialog();
                tL_dialog.flags = dialog.flags;
                tL_dialog.top_message = message.id;
                tL_dialog.last_message_date = message.date;
                tL_dialog.notify_settings = dialog.notify_settings;
                tL_dialog.pts = dialog.pts;
                tL_dialog.unread_count = dialog.unread_count;
                tL_dialog.unread_mark = dialog.unread_mark;
                tL_dialog.unread_mentions_count = dialog.unread_mentions_count;
                tL_dialog.unread_reactions_count = dialog.unread_reactions_count;
                tL_dialog.read_inbox_max_id = dialog.read_inbox_max_id;
                tL_dialog.read_outbox_max_id = dialog.read_outbox_max_id;
                tL_dialog.pinned = dialog.pinned;
                tL_dialog.pinnedNum = dialog.pinnedNum;
                tL_dialog.folder_id = dialog.folder_id;
                long j2 = dialog.id;
                tL_dialog.id = j2;
                message.dialog_id = j2;
                tL_messages_dialogs.users.addAll(messages_messages.users);
                tL_messages_dialogs.chats.addAll(messages_messages.chats);
                tL_messages_dialogs.dialogs.add(tL_dialog);
                tL_messages_dialogs.messages.addAll(messages_messages.messages);
                tL_messages_dialogs.count = 1;
                processDialogsUpdate(tL_messages_dialogs, null, false);
                getMessagesStorage().putMessages(messages_messages.messages, true, true, false, getDownloadController().getAutodownloadMask(), true, 0, 0L);
            }
        }
        if (j != 0) {
            getMessagesStorage().removePendingTask(j);
        }
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                MessagesController.this.lambda$checkLastDialogMessage$214(dialog);
            }
        });
    }

    public void lambda$checkPeerColors$442(TLObject tLObject) {
        this.loadingPeerColors = false;
        this.peerColors = PeerColors.fromTL(0, (TLRPC.TL_help_peerColors) tLObject);
        this.mainPreferences.edit().putString("peerColors", this.peerColors.toString()).apply();
    }

    public void lambda$checkPeerColors$443(final TLObject tLObject, TLRPC.TL_error tL_error) {
        if (tLObject instanceof TLRPC.TL_help_peerColors) {
            AndroidUtilities.runOnUIThread(new Runnable() {
                @Override
                public final void run() {
                    MessagesController.this.lambda$checkPeerColors$442(tLObject);
                }
            });
        }
    }

    public void lambda$checkPeerColors$444(TLObject tLObject) {
        this.loadingProfilePeerColors = false;
        this.profilePeerColors = PeerColors.fromTL(1, (TLRPC.TL_help_peerColors) tLObject);
        this.mainPreferences.edit().putString("profilePeerColors", this.profilePeerColors.toString()).apply();
    }

    public void lambda$checkPeerColors$445(final TLObject tLObject, TLRPC.TL_error tL_error) {
        if (tLObject instanceof TLRPC.TL_help_peerColors) {
            AndroidUtilities.runOnUIThread(new Runnable() {
                @Override
                public final void run() {
                    MessagesController.this.lambda$checkPeerColors$444(tLObject);
                }
            });
        }
    }

    public void lambda$checkPromoInfoInternal$155(TLRPC.TL_help_promoData tL_help_promoData, TLRPC.TL_messages_peerDialogs tL_messages_peerDialogs, long j) {
        putUsers(tL_help_promoData.users, false);
        putChats(tL_help_promoData.chats, false);
        putUsers(tL_messages_peerDialogs.users, false);
        putChats(tL_messages_peerDialogs.chats, false);
        TLRPC.Dialog dialog = this.promoDialog;
        if (dialog != null) {
            long j2 = dialog.id;
            if (j2 < 0) {
                TLRPC.Chat chat = getChat(Long.valueOf(-j2));
                if (ChatObject.isNotInChat(chat) || chat.restricted) {
                    dialog = this.promoDialog;
                }
            }
            removeDialog(dialog);
        }
        TLRPC.Dialog dialog2 = tL_messages_peerDialogs.dialogs.get(0);
        this.promoDialog = dialog2;
        dialog2.id = j;
        dialog2.folder_id = 0;
        if (DialogObject.isChannel(dialog2)) {
            LongSparseIntArray longSparseIntArray = this.channelsPts;
            TLRPC.Dialog dialog3 = this.promoDialog;
            longSparseIntArray.put(-dialog3.id, dialog3.pts);
        }
        Integer num = this.dialogs_read_inbox_max.get(Long.valueOf(this.promoDialog.id));
        if (num == null) {
            num = r3;
        }
        this.dialogs_read_inbox_max.put(Long.valueOf(this.promoDialog.id), Integer.valueOf(Math.max(num.intValue(), this.promoDialog.read_inbox_max_id)));
        Integer num2 = this.dialogs_read_outbox_max.get(Long.valueOf(this.promoDialog.id));
        this.dialogs_read_outbox_max.put(Long.valueOf(this.promoDialog.id), Integer.valueOf(Math.max((num2 != null ? num2 : 0).intValue(), this.promoDialog.read_outbox_max_id)));
        this.dialogs_dict.put(j, this.promoDialog);
        if (!tL_messages_peerDialogs.messages.isEmpty()) {
            LongSparseArray longSparseArray = new LongSparseArray();
            LongSparseArray longSparseArray2 = new LongSparseArray();
            for (int i = 0; i < tL_messages_peerDialogs.users.size(); i++) {
                TLRPC.User user = tL_messages_peerDialogs.users.get(i);
                longSparseArray.put(user.id, user);
            }
            for (int i2 = 0; i2 < tL_messages_peerDialogs.chats.size(); i2++) {
                TLRPC.Chat chat2 = tL_messages_peerDialogs.chats.get(i2);
                longSparseArray2.put(chat2.id, chat2);
            }
            MessageObject messageObject = new MessageObject(this.currentAccount, tL_messages_peerDialogs.messages.get(0), longSparseArray, longSparseArray2, false, true);
            ArrayList arrayList = (ArrayList) this.dialogMessage.get(j);
            if (arrayList == null) {
                arrayList = new ArrayList(1);
            }
            if (arrayList.size() > 0 && arrayList.get(0) != null && ((MessageObject) arrayList.get(0)).hasValidGroupId() && ((MessageObject) arrayList.get(0)).getGroupIdForUse() != messageObject.getGroupIdForUse()) {
                arrayList.clear();
            }
            arrayList.add(messageObject);
            this.dialogMessage.put(j, arrayList);
            TLRPC.Dialog dialog4 = this.promoDialog;
            if (dialog4.last_message_date == 0) {
                dialog4.last_message_date = messageObject.messageOwner.date;
            }
            getTranslateController().checkDialogMessage(j);
        }
        sortDialogs(null);
        getNotificationCenter().lambda$postNotificationNameOnUIThread$1(NotificationCenter.dialogsNeedReload, Boolean.TRUE);
    }

    public void lambda$checkPromoInfoInternal$156() {
        TLRPC.Dialog dialog = this.promoDialog;
        if (dialog != null) {
            long j = dialog.id;
            if (j < 0) {
                TLRPC.Chat chat = getChat(Long.valueOf(-j));
                if (ChatObject.isNotInChat(chat) || chat.restricted) {
                    dialog = this.promoDialog;
                }
                this.promoDialog = null;
                sortDialogs(null);
                getNotificationCenter().lambda$postNotificationNameOnUIThread$1(NotificationCenter.dialogsNeedReload, new Object[0]);
            }
            removeDialog(dialog);
            this.promoDialog = null;
            sortDialogs(null);
            getNotificationCenter().lambda$postNotificationNameOnUIThread$1(NotificationCenter.dialogsNeedReload, new Object[0]);
        }
    }

    public void lambda$checkPromoInfoInternal$157(int i, final TLRPC.TL_help_promoData tL_help_promoData, final long j, TLObject tLObject, TLRPC.TL_error tL_error) {
        if (i != this.lastCheckPromoId) {
            return;
        }
        this.checkingPromoInfoRequestId = 0;
        final TLRPC.TL_messages_peerDialogs tL_messages_peerDialogs = (TLRPC.TL_messages_peerDialogs) tLObject;
        if (tL_messages_peerDialogs == null || tL_messages_peerDialogs.dialogs.isEmpty()) {
            AndroidUtilities.runOnUIThread(new Runnable() {
                @Override
                public final void run() {
                    MessagesController.this.lambda$checkPromoInfoInternal$156();
                }
            });
        } else {
            getMessagesStorage().putUsersAndChats(tL_help_promoData.users, tL_help_promoData.chats, true, true);
            TLRPC.TL_messages_dialogs tL_messages_dialogs = new TLRPC.TL_messages_dialogs();
            tL_messages_dialogs.chats = tL_messages_peerDialogs.chats;
            tL_messages_dialogs.users = tL_messages_peerDialogs.users;
            tL_messages_dialogs.dialogs = tL_messages_peerDialogs.dialogs;
            tL_messages_dialogs.messages = tL_messages_peerDialogs.messages;
            getMessagesStorage().putDialogs(tL_messages_dialogs, 2);
            AndroidUtilities.runOnUIThread(new Runnable() {
                @Override
                public final void run() {
                    MessagesController.this.lambda$checkPromoInfoInternal$155(tL_help_promoData, tL_messages_peerDialogs, j);
                }
            });
        }
        this.checkingPromoInfo = false;
    }

    public void lambda$checkPromoInfoInternal$158(final long r11, final org.telegram.tgnet.TLRPC.TL_help_promoData r13, final int r14) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MessagesController.lambda$checkPromoInfoInternal$158(long, org.telegram.tgnet.TLRPC$TL_help_promoData, int):void");
    }

    public void lambda$checkPromoInfoInternal$159(final int r17, java.lang.String r18, java.lang.String r19, org.telegram.tgnet.TLObject r20, org.telegram.tgnet.TLRPC.TL_error r21) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MessagesController.lambda$checkPromoInfoInternal$159(int, java.lang.String, java.lang.String, org.telegram.tgnet.TLObject, org.telegram.tgnet.TLRPC$TL_error):void");
    }

    public static void lambda$checkSensitive$398(boolean[] zArr, View view) {
        boolean z = !zArr[0];
        zArr[0] = z;
        ((CheckBoxCell) view).setChecked(z, true);
    }

    public static void lambda$checkSensitive$399(BaseFragment baseFragment) {
        baseFragment.presentFragment(new ThemeActivity(0).highlightSensitiveRow());
    }

    public void lambda$checkSensitive$400(boolean[] zArr, long j, boolean[] zArr2, TL_account.contentSettings contentsettings, final BaseFragment baseFragment, Runnable runnable, DialogInterface dialogInterface, int i) {
        zArr[0] = true;
        this.sensitiveAgreed.add(Long.valueOf(j));
        if (zArr2[0] && contentsettings != null && contentsettings.sensitive_can_change) {
            getMessagesController().setContentSettings(true);
            BulletinFactory.of(baseFragment).createSimpleBulletinDetail(R.raw.chats_infotip, AndroidUtilities.replaceArrows(AndroidUtilities.premiumText(LocaleController.getString(R.string.SensitiveContentSettingsToast), new Runnable() {
                @Override
                public final void run() {
                    MessagesController.lambda$checkSensitive$399(BaseFragment.this);
                }
            }), true)).show(true);
        }
        if (runnable != null) {
            runnable.run();
        }
    }

    public static void lambda$checkSensitive$401(boolean[] zArr, Runnable runnable, DialogInterface dialogInterface) {
        if (zArr[0] || runnable == null) {
            return;
        }
        runnable.run();
    }

    public void lambda$checkSensitive$402(AlertDialog alertDialog, final BaseFragment baseFragment, final long j, final Runnable runnable, final Runnable runnable2, final TL_account.contentSettings contentsettings) {
        alertDialog.dismissUnless(200L);
        final boolean[] zArr = new boolean[1];
        FrameLayout frameLayout = new FrameLayout(baseFragment.getContext());
        if (contentsettings != null && contentsettings.sensitive_can_change) {
            CheckBoxCell checkBoxCell = new CheckBoxCell(baseFragment.getContext(), 1, baseFragment.getResourceProvider());
            checkBoxCell.setBackground(Theme.getSelectorDrawable(false));
            checkBoxCell.setText(LocaleController.getString(R.string.MessageShowSensitiveContentAlways), "", zArr[0], false);
            checkBoxCell.setPadding(LocaleController.isRTL ? AndroidUtilities.dp(16.0f) : AndroidUtilities.dp(8.0f), 0, LocaleController.isRTL ? AndroidUtilities.dp(8.0f) : AndroidUtilities.dp(16.0f), 0);
            frameLayout.addView(checkBoxCell, LayoutHelper.createFrame(-1, 48.0f, 51, 0.0f, 0.0f, 0.0f, 0.0f));
            checkBoxCell.setOnClickListener(new View.OnClickListener() {
                @Override
                public final void onClick(View view) {
                    MessagesController.lambda$checkSensitive$398(zArr, view);
                }
            });
        }
        final boolean[] zArr2 = new boolean[1];
        new AlertDialog.Builder(baseFragment.getContext(), baseFragment.getResourceProvider()).setTitle(LocaleController.getString(R.string.MessageShowSensitiveContentChannelTitle)).setMessage(LocaleController.getString(R.string.MessageShowSensitiveContentChannelText)).setView(frameLayout).setCustomViewOffset(9).setNegativeButton(LocaleController.getString(R.string.Cancel), null).setPositiveButton(LocaleController.getString(R.string.MessageShowSensitiveContentButton), new DialogInterface.OnClickListener() {
            @Override
            public final void onClick(DialogInterface dialogInterface, int i) {
                MessagesController.this.lambda$checkSensitive$400(zArr2, j, zArr, contentsettings, baseFragment, runnable, dialogInterface, i);
            }
        }).setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public final void onDismiss(DialogInterface dialogInterface) {
                MessagesController.lambda$checkSensitive$401(zArr2, runnable2, dialogInterface);
            }
        }).show();
    }

    public void lambda$checkTosUpdate$152(TLRPC.TL_help_termsOfServiceUpdate tL_help_termsOfServiceUpdate) {
        getNotificationCenter().lambda$postNotificationNameOnUIThread$1(NotificationCenter.needShowAlert, 4, tL_help_termsOfServiceUpdate.terms_of_service);
    }

    public void lambda$checkTosUpdate$153(TLObject tLObject, TLRPC.TL_error tL_error) {
        int currentTime;
        this.checkingTosUpdate = false;
        if (tLObject instanceof TLRPC.TL_help_termsOfServiceUpdateEmpty) {
            currentTime = ((TLRPC.TL_help_termsOfServiceUpdateEmpty) tLObject).expires;
        } else {
            if (tLObject instanceof TLRPC.TL_help_termsOfServiceUpdate) {
                final TLRPC.TL_help_termsOfServiceUpdate tL_help_termsOfServiceUpdate = (TLRPC.TL_help_termsOfServiceUpdate) tLObject;
                this.nextTosCheckTime = tL_help_termsOfServiceUpdate.expires;
                AndroidUtilities.runOnUIThread(new Runnable() {
                    @Override
                    public final void run() {
                        MessagesController.this.lambda$checkTosUpdate$152(tL_help_termsOfServiceUpdate);
                    }
                });
                this.notificationsPreferences.edit().putInt("nextTosCheckTime", this.nextTosCheckTime).commit();
            }
            currentTime = getConnectionsManager().getCurrentTime() + 3600;
        }
        this.nextTosCheckTime = currentTime;
        this.notificationsPreferences.edit().putInt("nextTosCheckTime", this.nextTosCheckTime).commit();
    }

    public void lambda$checkUnreadReactions$384(long j, int i, long j2, ArrayList arrayList) {
        TLRPC.Dialog dialog = (TLRPC.Dialog) this.dialogs_dict.get(j);
        if (dialog == null) {
            getMessagesStorage().updateDialogUnreadReactions(j, 0L, i, false);
            return;
        }
        dialog.unread_reactions_count = i;
        getMessagesStorage().updateUnreadReactionsCount(j, j2, i);
        getNotificationCenter().lambda$postNotificationNameOnUIThread$1(NotificationCenter.dialogsUnreadReactionsCounterChanged, Long.valueOf(j), Long.valueOf(j2), Integer.valueOf(i), arrayList);
    }

    public void lambda$checkUnreadReactions$385(final long j, final long j2, final ArrayList arrayList, TLObject tLObject, TLRPC.TL_error tL_error) {
        if (tLObject != null) {
            TLRPC.TL_messages_peerDialogs tL_messages_peerDialogs = (TLRPC.TL_messages_peerDialogs) tLObject;
            final int i = tL_messages_peerDialogs.dialogs.size() == 0 ? 0 : tL_messages_peerDialogs.dialogs.get(0).unread_reactions_count;
            AndroidUtilities.runOnUIThread(new Runnable() {
                @Override
                public final void run() {
                    MessagesController.this.lambda$checkUnreadReactions$384(j, i, j2, arrayList);
                }
            });
        }
    }

    public void lambda$checkUnreadReactions$386(long j, long j2, int i, ArrayList arrayList) {
        getMessagesController().getTopicsController().updateReactionsUnread(j, j2, i, false);
        getMessagesStorage().updateUnreadReactionsCount(j, j2, i);
        getNotificationCenter().lambda$postNotificationNameOnUIThread$1(NotificationCenter.dialogsUnreadReactionsCounterChanged, Long.valueOf(j), Long.valueOf(j2), Integer.valueOf(i), arrayList);
    }

    public void lambda$checkUnreadReactions$387(final long j, final long j2, final ArrayList arrayList, TLObject tLObject, TLRPC.TL_error tL_error) {
        if (tLObject != null) {
            TLRPC.TL_messages_forumTopics tL_messages_forumTopics = (TLRPC.TL_messages_forumTopics) tLObject;
            final int i = tL_messages_forumTopics.topics.size() == 0 ? 0 : tL_messages_forumTopics.topics.get(0).unread_reactions_count;
            AndroidUtilities.runOnUIThread(new Runnable() {
                @Override
                public final void run() {
                    MessagesController.this.lambda$checkUnreadReactions$386(j, j2, i, arrayList);
                }
            });
        }
    }

    public void lambda$checkUnreadReactions$388(long j, long j2, int i) {
        if (j != 0) {
            int updateReactionsUnread = getMessagesController().getTopicsController().updateReactionsUnread(j2, j, i, true);
            if (updateReactionsUnread >= 0) {
                getMessagesStorage().updateUnreadReactionsCount(j2, j, updateReactionsUnread, true);
                return;
            }
            return;
        }
        TLRPC.Dialog dialog = (TLRPC.Dialog) this.dialogs_dict.get(j2);
        if (dialog == null) {
            getMessagesStorage().updateDialogUnreadReactions(j2, 0L, i, true);
            return;
        }
        int i2 = dialog.unread_reactions_count + i;
        dialog.unread_reactions_count = i2;
        if (i2 < 0) {
            dialog.unread_reactions_count = 0;
        }
        getMessagesStorage().updateUnreadReactionsCount(j2, 0L, dialog.unread_reactions_count);
    }

    public void lambda$checkUnreadReactions$389(SparseBooleanArray sparseBooleanArray, final long j, final long j2) {
        RequestDelegate requestDelegate;
        ConnectionsManager connectionsManager;
        TLRPC.TL_channels_getForumTopicsByID tL_channels_getForumTopicsByID;
        int i;
        boolean z;
        boolean z2;
        SQLitePreparedStatement executeFast;
        final ArrayList arrayList = new ArrayList();
        StringBuilder sb = new StringBuilder();
        int i2 = 0;
        for (int i3 = 0; i3 < sparseBooleanArray.size(); i3++) {
            int keyAt = sparseBooleanArray.keyAt(i3);
            if (sb.length() > 0) {
                sb.append(", ");
            }
            sb.append(keyAt);
        }
        SparseBooleanArray sparseBooleanArray2 = new SparseBooleanArray();
        try {
            SQLiteCursor queryFinalized = j != 0 ? getMessagesStorage().getDatabase().queryFinalized(String.format(Locale.US, "SELECT message_id, state FROM reaction_mentions WHERE message_id IN (%s) AND dialog_id = %d", sb, Long.valueOf(j2)), new Object[0]) : getMessagesStorage().getDatabase().queryFinalized(String.format(Locale.US, "SELECT message_id, state FROM reaction_mentions_topics WHERE message_id IN (%s) AND dialog_id = %d AND topic_id = %d", sb, Long.valueOf(j2), Long.valueOf(j)), new Object[0]);
            while (queryFinalized.next()) {
                sparseBooleanArray2.put(queryFinalized.intValue(0), queryFinalized.intValue(1) == 1);
            }
            queryFinalized.dispose();
        } catch (SQLiteException e) {
            e.printStackTrace();
        }
        boolean z3 = false;
        int i4 = 0;
        boolean z4 = false;
        while (i4 < sparseBooleanArray.size()) {
            int keyAt2 = sparseBooleanArray.keyAt(i4);
            boolean valueAt = sparseBooleanArray.valueAt(i4);
            if (sparseBooleanArray2.indexOfKey(keyAt2) < 0) {
                i = i2;
                z = z4;
                z2 = true;
            } else if (sparseBooleanArray2.get(keyAt2) != valueAt) {
                z2 = z3;
                i = i2 + (valueAt ? 1 : -1);
                z = true;
            } else {
                i = i2;
                z = z4;
                z2 = z3;
            }
            if (valueAt) {
                arrayList.add(Integer.valueOf(keyAt2));
            }
            if (j == 0) {
                try {
                    executeFast = getMessagesStorage().getDatabase().executeFast("REPLACE INTO reaction_mentions VALUES(?, ?, ?)");
                    executeFast.requery();
                    executeFast.bindInteger(1, keyAt2);
                    executeFast.bindInteger(2, valueAt ? 1 : 0);
                    executeFast.bindLong(3, j2);
                    executeFast.step();
                } catch (SQLiteException e2) {
                    e = e2;
                    e.printStackTrace();
                    i4++;
                    z3 = z2;
                    z4 = z;
                    i2 = i;
                }
            } else {
                executeFast = getMessagesStorage().getDatabase().executeFast("REPLACE INTO reaction_mentions_topics VALUES(?, ?, ?, ?)");
                executeFast.requery();
                executeFast.bindInteger(1, keyAt2);
                try {
                    executeFast.bindInteger(2, valueAt ? 1 : 0);
                } catch (SQLiteException e3) {
                    e = e3;
                    e.printStackTrace();
                    i4++;
                    z3 = z2;
                    z4 = z;
                    i2 = i;
                }
                try {
                    executeFast.bindLong(3, j2);
                    executeFast.bindLong(4, j);
                    executeFast.step();
                } catch (SQLiteException e4) {
                    e = e4;
                    e.printStackTrace();
                    i4++;
                    z3 = z2;
                    z4 = z;
                    i2 = i;
                }
            }
            executeFast.dispose();
            i4++;
            z3 = z2;
            z4 = z;
            i2 = i;
        }
        if (!z3) {
            if (z4) {
                final int i5 = i2;
                AndroidUtilities.runOnUIThread(new Runnable() {
                    @Override
                    public final void run() {
                        MessagesController.this.lambda$checkUnreadReactions$388(j, j2, i5);
                    }
                });
                return;
            }
            return;
        }
        if (j == 0) {
            TLRPC.TL_messages_getPeerDialogs tL_messages_getPeerDialogs = new TLRPC.TL_messages_getPeerDialogs();
            TLRPC.TL_inputDialogPeer tL_inputDialogPeer = new TLRPC.TL_inputDialogPeer();
            tL_inputDialogPeer.peer = getInputPeer(j2);
            tL_messages_getPeerDialogs.peers.add(tL_inputDialogPeer);
            ConnectionsManager connectionsManager2 = ConnectionsManager.getInstance(this.currentAccount);
            requestDelegate = new RequestDelegate() {
                @Override
                public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                    MessagesController.this.lambda$checkUnreadReactions$385(j2, j, arrayList, tLObject, tL_error);
                }
            };
            tL_channels_getForumTopicsByID = tL_messages_getPeerDialogs;
            connectionsManager = connectionsManager2;
        } else {
            TLRPC.TL_channels_getForumTopicsByID tL_channels_getForumTopicsByID2 = new TLRPC.TL_channels_getForumTopicsByID();
            tL_channels_getForumTopicsByID2.topics.add(Integer.valueOf((int) j));
            tL_channels_getForumTopicsByID2.channel = getMessagesController().getInputChannel(-j2);
            ConnectionsManager connectionsManager3 = ConnectionsManager.getInstance(this.currentAccount);
            requestDelegate = new RequestDelegate() {
                @Override
                public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                    MessagesController.this.lambda$checkUnreadReactions$387(j2, j, arrayList, tLObject, tL_error);
                }
            };
            tL_channels_getForumTopicsByID = tL_channels_getForumTopicsByID2;
            connectionsManager = connectionsManager3;
        }
        connectionsManager.sendRequest(tL_channels_getForumTopicsByID, requestDelegate);
    }

    public void lambda$cleanup$47() {
        this.readTasks.clear();
        this.readTasksMap.clear();
        this.repliesReadTasks.clear();
        this.threadsReadTasksMap.clear();
        this.updatesQueueSeq.clear();
        this.updatesQueuePts.clear();
        this.updatesQueueQts.clear();
        this.gettingUnknownChannels.clear();
        this.gettingUnknownDialogs.clear();
        this.updatesStartWaitTimeSeq = 0L;
        this.updatesStartWaitTimePts = 0L;
        this.updatesStartWaitTimeQts = 0L;
        this.createdDialogIds.clear();
        this.createdScheduledDialogIds.clear();
        this.gettingDifference = false;
        this.resetDialogsPinned = null;
        this.resetDialogsAll = null;
    }

    public void lambda$cleanup$48() {
        FileLog.d("cleanup: isUpdating = false");
        getConnectionsManager().setIsUpdating(false);
        this.updatesQueueChannels.clear();
        this.updatesStartWaitTimeChannels.clear();
        this.gettingDifferenceChannels.clear();
        this.channelsPts.clear();
        this.shortPollChannels.clear();
        this.needShortPollChannels.clear();
        this.shortPollOnlines.clear();
        this.needShortPollOnlines.clear();
    }

    public void lambda$cleanup$49() {
        getNotificationCenter().lambda$postNotificationNameOnUIThread$1(NotificationCenter.suggestedFiltersLoaded, new Object[0]);
        getNotificationCenter().lambda$postNotificationNameOnUIThread$1(NotificationCenter.dialogFiltersUpdated, new Object[0]);
        getNotificationCenter().lambda$postNotificationNameOnUIThread$1(NotificationCenter.dialogsNeedReload, new Object[0]);
    }

    public void lambda$completeDialogsReset$199(TLRPC.messages_Dialogs messages_dialogs, LongSparseArray longSparseArray, LongSparseArray longSparseArray2) {
        MediaDataController mediaDataController;
        long j;
        this.resetingDialogs = false;
        applyDialogsNotificationsSettings(messages_dialogs.dialogs);
        MediaDataController mediaDataController2 = getMediaDataController();
        mediaDataController2.clearAllDrafts(false);
        mediaDataController2.loadDraftsIfNeed();
        putUsers(messages_dialogs.users, false);
        putChats(messages_dialogs.chats, false);
        for (int i = 0; i < this.allDialogs.size(); i++) {
            TLRPC.Dialog dialog = this.allDialogs.get(i);
            if (!DialogObject.isEncryptedDialog(dialog.id)) {
                this.dialogs_dict.remove(dialog.id);
                ArrayList arrayList = (ArrayList) this.dialogMessage.get(dialog.id);
                this.dialogMessage.remove(dialog.id);
                if (arrayList != null) {
                    for (int i2 = 0; i2 < arrayList.size(); i2++) {
                        MessageObject messageObject = (MessageObject) arrayList.get(i2);
                        if (messageObject != null) {
                            if (messageObject.messageOwner.peer_id.channel_id == 0) {
                                this.dialogMessagesByIds.remove(messageObject.getId());
                            }
                            long j2 = messageObject.messageOwner.random_id;
                            if (j2 != 0) {
                                this.dialogMessagesByRandomIds.remove(j2);
                            }
                        }
                    }
                }
            }
        }
        int i3 = 0;
        while (i3 < longSparseArray.size()) {
            long keyAt = longSparseArray.keyAt(i3);
            TLRPC.Dialog dialog2 = (TLRPC.Dialog) longSparseArray.valueAt(i3);
            TLRPC.DraftMessage draftMessage = dialog2.draft;
            if (draftMessage instanceof TLRPC.TL_draftMessage) {
                mediaDataController = mediaDataController2;
                j = keyAt;
                mediaDataController2.saveDraft(dialog2.id, 0L, draftMessage, null, false);
            } else {
                mediaDataController = mediaDataController2;
                j = keyAt;
            }
            this.dialogs_dict.put(j, dialog2);
            ArrayList arrayList2 = (ArrayList) longSparseArray2.get(dialog2.id);
            this.dialogMessage.put(j, arrayList2);
            if (arrayList2 != null) {
                for (int i4 = 0; i4 < arrayList2.size(); i4++) {
                    MessageObject messageObject2 = (MessageObject) arrayList2.get(i4);
                    if (messageObject2 != null && messageObject2.messageOwner.peer_id.channel_id == 0) {
                        this.dialogMessagesByIds.put(messageObject2.getId(), messageObject2);
                        this.dialogsLoadedTillDate = Math.min(this.dialogsLoadedTillDate, messageObject2.messageOwner.date);
                        long j3 = messageObject2.messageOwner.random_id;
                        if (j3 != 0) {
                            this.dialogMessagesByRandomIds.put(j3, messageObject2);
                        }
                    }
                }
            }
            getTranslateController().checkDialogMessage(j);
            i3++;
            mediaDataController2 = mediaDataController;
        }
        this.allDialogs.clear();
        int size = this.dialogs_dict.size();
        for (int i5 = 0; i5 < size; i5++) {
            TLRPC.Dialog dialog3 = (TLRPC.Dialog) this.dialogs_dict.valueAt(i5);
            if (this.deletingDialogs.indexOfKey(dialog3.id) < 0) {
                this.allDialogs.add(dialog3);
            }
        }
        sortDialogs(null);
        this.dialogsEndReached.put(0, true);
        this.serverDialogsEndReached.put(0, false);
        this.dialogsEndReached.put(1, true);
        this.serverDialogsEndReached.put(1, false);
        int totalDialogsCount = getUserConfig().getTotalDialogsCount(0);
        long[] dialogLoadOffsets = getUserConfig().getDialogLoadOffsets(0);
        if (totalDialogsCount < 400) {
            long j4 = dialogLoadOffsets[0];
            if (j4 != -1 && j4 != 2147483647L) {
                loadDialogs(0, 0, 100, false);
            }
        }
        getNotificationCenter().lambda$postNotificationNameOnUIThread$1(NotificationCenter.dialogsNeedReload, new Object[0]);
    }

    public void lambda$completeDialogsReset$200(int i, int i2, int i3, final TLRPC.messages_Dialogs messages_dialogs, final LongSparseArray longSparseArray, final LongSparseArray longSparseArray2) {
        this.gettingDifference = false;
        getMessagesStorage().setLastPtsValue(i);
        getMessagesStorage().setLastDateValue(i2);
        getMessagesStorage().setLastQtsValue(i3);
        getDifference();
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                MessagesController.this.lambda$completeDialogsReset$199(messages_dialogs, longSparseArray, longSparseArray2);
            }
        });
    }

    public static void lambda$completeReadTask$225(TLObject tLObject, TLRPC.TL_error tL_error) {
    }

    public void lambda$completeReadTask$226(TLObject tLObject, TLRPC.TL_error tL_error) {
        if (tL_error == null && (tLObject instanceof TLRPC.TL_messages_affectedMessages)) {
            TLRPC.TL_messages_affectedMessages tL_messages_affectedMessages = (TLRPC.TL_messages_affectedMessages) tLObject;
            processNewDifferenceParams(-1, tL_messages_affectedMessages.pts, -1, tL_messages_affectedMessages.pts_count);
        }
    }

    public static void lambda$completeReadTask$227(TLObject tLObject, TLRPC.TL_error tL_error) {
    }

    public static void lambda$convertToGigaGroup$246(Context context, AlertDialog alertDialog) {
        if (((Activity) context).isFinishing()) {
            return;
        }
        try {
            alertDialog.dismiss();
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    public static void lambda$convertToGigaGroup$247(MessagesStorage.BooleanCallback booleanCallback) {
        if (booleanCallback != null) {
            booleanCallback.run(true);
        }
    }

    public void lambda$convertToGigaGroup$248(MessagesStorage.BooleanCallback booleanCallback, Context context, AlertDialog alertDialog, TLRPC.TL_error tL_error, BaseFragment baseFragment, TLRPC.TL_channels_convertToGigagroup tL_channels_convertToGigagroup) {
        if (booleanCallback != null) {
            booleanCallback.run(false);
        }
        if (context == null || ((Activity) context).isFinishing()) {
            return;
        }
        try {
            alertDialog.dismiss();
        } catch (Exception e) {
            FileLog.e(e);
        }
        AlertsCreator.processError(this.currentAccount, tL_error, baseFragment, tL_channels_convertToGigagroup, Boolean.FALSE);
    }

    public void lambda$convertToGigaGroup$249(final Context context, final AlertDialog alertDialog, final MessagesStorage.BooleanCallback booleanCallback, final BaseFragment baseFragment, final TLRPC.TL_channels_convertToGigagroup tL_channels_convertToGigagroup, TLObject tLObject, final TLRPC.TL_error tL_error) {
        if (tL_error != null) {
            AndroidUtilities.runOnUIThread(new Runnable() {
                @Override
                public final void run() {
                    MessagesController.this.lambda$convertToGigaGroup$248(booleanCallback, context, alertDialog, tL_error, baseFragment, tL_channels_convertToGigagroup);
                }
            });
            return;
        }
        if (context != null) {
            AndroidUtilities.runOnUIThread(new Runnable() {
                @Override
                public final void run() {
                    MessagesController.lambda$convertToGigaGroup$246(context, alertDialog);
                }
            });
        }
        processUpdates((TLRPC.Updates) tLObject, false);
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                MessagesController.lambda$convertToGigaGroup$247(MessagesStorage.BooleanCallback.this);
            }
        });
    }

    public void lambda$convertToGigaGroup$250(int i, DialogInterface dialogInterface) {
        getConnectionsManager().cancelRequest(i, true);
    }

    public static void lambda$convertToMegaGroup$241(Context context, AlertDialog alertDialog) {
        if (((Activity) context).isFinishing()) {
            return;
        }
        try {
            alertDialog.dismiss();
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    public static void lambda$convertToMegaGroup$242(MessagesStorage.LongCallback longCallback, TLRPC.Updates updates, long j) {
        TLRPC.Chat chat;
        TLRPC.Chat chat2;
        TLRPC.InputChannel inputChannel;
        if (longCallback != null) {
            int i = 0;
            int i2 = 0;
            while (true) {
                chat = null;
                if (i2 >= updates.chats.size()) {
                    chat2 = null;
                    break;
                }
                chat2 = updates.chats.get(i2);
                if (j == chat2.id) {
                    break;
                } else {
                    i2++;
                }
            }
            if (chat2 == null || (inputChannel = chat2.migrated_to) == null) {
                return;
            }
            long j2 = inputChannel.channel_id;
            while (true) {
                if (i >= updates.chats.size()) {
                    break;
                }
                TLRPC.Chat chat3 = updates.chats.get(i);
                if (j2 == chat3.id) {
                    chat = chat3;
                    break;
                }
                i++;
            }
            if (chat != null) {
                longCallback.run(j2);
            }
        }
    }

    public void lambda$convertToMegaGroup$243(MessagesStorage.LongCallback longCallback, Context context, AlertDialog alertDialog, TLRPC.TL_error tL_error, BaseFragment baseFragment, TLRPC.TL_messages_migrateChat tL_messages_migrateChat) {
        if (longCallback != null) {
            longCallback.run(0L);
        }
        if (context == null || ((Activity) context).isFinishing()) {
            return;
        }
        try {
            alertDialog.dismiss();
        } catch (Exception e) {
            FileLog.e(e);
        }
        AlertsCreator.processError(this.currentAccount, tL_error, baseFragment, tL_messages_migrateChat, Boolean.FALSE);
    }

    public void lambda$convertToMegaGroup$244(final Context context, final AlertDialog alertDialog, final MessagesStorage.LongCallback longCallback, final long j, Runnable runnable, final BaseFragment baseFragment, final TLRPC.TL_messages_migrateChat tL_messages_migrateChat, TLObject tLObject, final TLRPC.TL_error tL_error) {
        if (tL_error != null) {
            if (runnable != null) {
                runnable.run();
            }
            AndroidUtilities.runOnUIThread(new Runnable() {
                @Override
                public final void run() {
                    MessagesController.this.lambda$convertToMegaGroup$243(longCallback, context, alertDialog, tL_error, baseFragment, tL_messages_migrateChat);
                }
            });
        } else {
            if (context != null) {
                AndroidUtilities.runOnUIThread(new Runnable() {
                    @Override
                    public final void run() {
                        MessagesController.lambda$convertToMegaGroup$241(context, alertDialog);
                    }
                });
            }
            final TLRPC.Updates updates = (TLRPC.Updates) tLObject;
            processUpdates(updates, false);
            AndroidUtilities.runOnUIThread(new Runnable() {
                @Override
                public final void run() {
                    MessagesController.lambda$convertToMegaGroup$242(MessagesStorage.LongCallback.this, updates, j);
                }
            });
        }
    }

    public void lambda$convertToMegaGroup$245(int i, DialogInterface dialogInterface) {
        getConnectionsManager().cancelRequest(i, true);
    }

    public void lambda$createChat$235(TLRPC.TL_error tL_error, BaseFragment baseFragment, TLRPC.TL_messages_createChat tL_messages_createChat) {
        AlertsCreator.processError(this.currentAccount, tL_error, baseFragment, tL_messages_createChat, new Object[0]);
        getNotificationCenter().lambda$postNotificationNameOnUIThread$1(NotificationCenter.chatDidFailCreate, new Object[0]);
    }

    public void lambda$createChat$236(TLRPC.TL_messages_invitedUsers tL_messages_invitedUsers) {
        putUsers(tL_messages_invitedUsers.updates.users, false);
        putChats(tL_messages_invitedUsers.updates.chats, false);
        ArrayList<TLRPC.Chat> arrayList = tL_messages_invitedUsers.updates.chats;
        if (arrayList == null || arrayList.isEmpty()) {
            getNotificationCenter().lambda$postNotificationNameOnUIThread$1(NotificationCenter.chatDidFailCreate, new Object[0]);
        } else {
            getNotificationCenter().lambda$postNotificationNameOnUIThread$1(NotificationCenter.chatDidCreated, Long.valueOf(tL_messages_invitedUsers.updates.chats.get(0).id));
            AlertsCreator.checkRestrictedInviteUsers(this.currentAccount, tL_messages_invitedUsers.updates.chats.get(0), tL_messages_invitedUsers);
        }
    }

    public void lambda$createChat$237(final BaseFragment baseFragment, final TLRPC.TL_messages_createChat tL_messages_createChat, TLObject tLObject, final TLRPC.TL_error tL_error) {
        if (tL_error != null) {
            AndroidUtilities.runOnUIThread(new Runnable() {
                @Override
                public final void run() {
                    MessagesController.this.lambda$createChat$235(tL_error, baseFragment, tL_messages_createChat);
                }
            });
        } else if (tLObject instanceof TLRPC.TL_messages_invitedUsers) {
            final TLRPC.TL_messages_invitedUsers tL_messages_invitedUsers = (TLRPC.TL_messages_invitedUsers) tLObject;
            processUpdates(tL_messages_invitedUsers.updates, false);
            AndroidUtilities.runOnUIThread(new Runnable() {
                @Override
                public final void run() {
                    MessagesController.this.lambda$createChat$236(tL_messages_invitedUsers);
                }
            });
        }
    }

    public void lambda$createChat$238(TLRPC.TL_error tL_error, BaseFragment baseFragment, TLRPC.TL_channels_createChannel tL_channels_createChannel) {
        AlertsCreator.processError(this.currentAccount, tL_error, baseFragment, tL_channels_createChannel, new Object[0]);
        getNotificationCenter().lambda$postNotificationNameOnUIThread$1(NotificationCenter.chatDidFailCreate, new Object[0]);
    }

    public void lambda$createChat$239(TLRPC.Updates updates) {
        putUsers(updates.users, false);
        putChats(updates.chats, false);
        ArrayList<TLRPC.Chat> arrayList = updates.chats;
        if (arrayList == null || arrayList.isEmpty()) {
            getNotificationCenter().lambda$postNotificationNameOnUIThread$1(NotificationCenter.chatDidFailCreate, new Object[0]);
        } else {
            getNotificationCenter().lambda$postNotificationNameOnUIThread$1(NotificationCenter.chatDidCreated, Long.valueOf(updates.chats.get(0).id));
        }
    }

    public void lambda$createChat$240(final BaseFragment baseFragment, final TLRPC.TL_channels_createChannel tL_channels_createChannel, TLObject tLObject, final TLRPC.TL_error tL_error) {
        if (tL_error != null) {
            AndroidUtilities.runOnUIThread(new Runnable() {
                @Override
                public final void run() {
                    MessagesController.this.lambda$createChat$238(tL_error, baseFragment, tL_channels_createChannel);
                }
            });
            return;
        }
        final TLRPC.Updates updates = (TLRPC.Updates) tLObject;
        processUpdates(updates, false);
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                MessagesController.this.lambda$createChat$239(updates);
            }
        });
    }

    public void lambda$deleteDialog$128(long j, int i, boolean z, TLRPC.InputPeer inputPeer, long j2, int i2) {
        if (j == getUserConfig().getClientUserId()) {
            getSavedMessagesController().deleteAllDialogs();
        }
        deleteDialog(j, 2, i, Math.max(0, i2), z, inputPeer, j2);
        checkIfFolderEmpty(1);
    }

    public void lambda$deleteDialog$129(long j) {
        getNotificationsController().removeNotificationsForDialog(j);
    }

    public void lambda$deleteDialog$130(final long j) {
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                MessagesController.this.lambda$deleteDialog$129(j);
            }
        });
    }

    public void lambda$deleteDialog$131(long j, TLObject tLObject, TLRPC.TL_error tL_error) {
        if (j != 0) {
            getMessagesStorage().removePendingTask(j);
        }
        if (tLObject != null) {
            processUpdates((TLRPC.Updates) tLObject, false);
        }
    }

    public void lambda$deleteDialog$132(long j, long j2, int i, int i2, boolean z, TLRPC.InputPeer inputPeer, TLObject tLObject, TLRPC.TL_error tL_error) {
        if (j != 0) {
            getMessagesStorage().removePendingTask(j);
        }
        if (tL_error == null) {
            TLRPC.TL_messages_affectedHistory tL_messages_affectedHistory = (TLRPC.TL_messages_affectedHistory) tLObject;
            if (tL_messages_affectedHistory.offset > 0) {
                deleteDialog(j2, 0, i, i2, z, inputPeer, 0L);
            }
            processNewDifferenceParams(-1, tL_messages_affectedHistory.pts, -1, tL_messages_affectedHistory.pts_count);
            getMessagesStorage().onDeleteQueryComplete(j2);
        }
    }

    public void lambda$deleteMessages$117(long j, TLObject tLObject, TLRPC.TL_error tL_error) {
        if (tL_error == null) {
            processUpdates((TLRPC.Updates) tLObject, false);
        }
        if (j != 0) {
            getMessagesStorage().removePendingTask(j);
        }
    }

    public void lambda$deleteMessages$118(long j, TLObject tLObject, TLRPC.TL_error tL_error) {
        if (tL_error == null) {
            processUpdates((TLRPC.Updates) tLObject, false);
        }
        if (j != 0) {
            getMessagesStorage().removePendingTask(j);
        }
    }

    public void lambda$deleteMessages$119(long j, long j2, TLObject tLObject, TLRPC.TL_error tL_error) {
        if (tL_error == null) {
            TLRPC.TL_messages_affectedMessages tL_messages_affectedMessages = (TLRPC.TL_messages_affectedMessages) tLObject;
            processNewChannelDifferenceParams(tL_messages_affectedMessages.pts, tL_messages_affectedMessages.pts_count, j);
        }
        if (j2 != 0) {
            getMessagesStorage().removePendingTask(j2);
        }
    }

    public void lambda$deleteMessages$120(long j, TLObject tLObject, TLRPC.TL_error tL_error) {
        if (tL_error == null) {
            TLRPC.TL_messages_affectedMessages tL_messages_affectedMessages = (TLRPC.TL_messages_affectedMessages) tLObject;
            processNewDifferenceParams(-1, tL_messages_affectedMessages.pts, -1, tL_messages_affectedMessages.pts_count);
        }
        if (j != 0) {
            getMessagesStorage().removePendingTask(j);
        }
    }

    public void lambda$deleteMessagesByPush$346(ArrayList arrayList, long j) {
        getNotificationCenter().lambda$postNotificationNameOnUIThread$1(NotificationCenter.messagesDeleted, arrayList, Long.valueOf(j), Boolean.FALSE);
        if (j == 0) {
            int size = arrayList.size();
            for (int i = 0; i < size; i++) {
                MessageObject messageObject = this.dialogMessagesByIds.get(((Integer) arrayList.get(i)).intValue());
                if (messageObject != null) {
                    messageObject.deleted = true;
                }
            }
            return;
        }
        ArrayList arrayList2 = (ArrayList) this.dialogMessage.get(-j);
        if (arrayList2 != null) {
            for (int i2 = 0; i2 < arrayList2.size(); i2++) {
                MessageObject messageObject2 = (MessageObject) arrayList2.get(i2);
                int size2 = arrayList.size();
                int i3 = 0;
                while (true) {
                    if (i3 >= size2) {
                        break;
                    }
                    if (messageObject2.getId() == ((Integer) arrayList.get(i3)).intValue()) {
                        messageObject2.deleted = true;
                        break;
                    }
                    i3++;
                }
            }
        }
    }

    public void lambda$deleteMessagesByPush$347(final ArrayList arrayList, final long j, long j2) {
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                MessagesController.this.lambda$deleteMessagesByPush$346(arrayList, j);
            }
        });
        getMessagesStorage().deletePushMessages(j2, arrayList);
        getMessagesStorage().updateDialogsWithDeletedMessages(j2, j, arrayList, getMessagesStorage().markMessagesAsDeleted(j2, arrayList, false, true, 0, 0), false);
    }

    public void lambda$deleteMessagesRange$415(ArrayList arrayList, long j, TLRPC.TL_messages_affectedHistory tL_messages_affectedHistory, long j2, int i, int i2, boolean z, Runnable runnable) {
        getNotificationCenter().lambda$postNotificationNameOnUIThread$1(NotificationCenter.messagesDeleted, arrayList, Long.valueOf(j), Boolean.FALSE);
        if (tL_messages_affectedHistory.offset > 0) {
            deleteMessagesRange(j2, j, i, i2, z, runnable);
        } else {
            runnable.run();
        }
    }

    public void lambda$deleteMessagesRange$416(final long j, final int i, final int i2, final long j2, final TLRPC.TL_messages_affectedHistory tL_messages_affectedHistory, final boolean z, final Runnable runnable) {
        final ArrayList<Integer> cachedMessagesInRange = getMessagesStorage().getCachedMessagesInRange(j, i, i2);
        getMessagesStorage().markMessagesAsDeleted(j, cachedMessagesInRange, false, true, 0, 0);
        getMessagesStorage().updateDialogsWithDeletedMessages(j, 0L, cachedMessagesInRange, null, false);
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                MessagesController.this.lambda$deleteMessagesRange$415(cachedMessagesInRange, j2, tL_messages_affectedHistory, j, i, i2, z, runnable);
            }
        });
    }

    public void lambda$deleteMessagesRange$418(final long j, final int i, final int i2, final long j2, final boolean z, final Runnable runnable, TLObject tLObject, TLRPC.TL_error tL_error) {
        if (tL_error != null) {
            AndroidUtilities.runOnUIThread(new Runnable() {
                @Override
                public final void run() {
                    runnable.run();
                }
            });
            return;
        }
        final TLRPC.TL_messages_affectedHistory tL_messages_affectedHistory = (TLRPC.TL_messages_affectedHistory) tLObject;
        processNewDifferenceParams(-1, tL_messages_affectedHistory.pts, -1, tL_messages_affectedHistory.pts_count);
        getMessagesStorage().getStorageQueue().postRunnable(new Runnable() {
            @Override
            public final void run() {
                MessagesController.this.lambda$deleteMessagesRange$416(j, i, i2, j2, tL_messages_affectedHistory, z, runnable);
            }
        });
    }

    public static void lambda$deleteParticipantFromChat$288(TLObject tLObject, TLRPC.TL_error tL_error) {
    }

    public void lambda$deleteParticipantFromChat$289(long j) {
        loadFullChat(j, 0, true);
    }

    public void lambda$deleteParticipantFromChat$290(boolean z, boolean z2, final long j, Runnable runnable, TLObject tLObject, TLRPC.TL_error tL_error) {
        if (tL_error != null) {
            return;
        }
        processUpdates((TLRPC.Updates) tLObject, false);
        if (z && !z2) {
            AndroidUtilities.runOnUIThread(new Runnable() {
                @Override
                public final void run() {
                    MessagesController.this.lambda$deleteParticipantFromChat$289(j);
                }
            }, 1000L);
        }
        if (runnable != null) {
            AndroidUtilities.runOnUIThread(runnable);
        }
    }

    public static void lambda$deleteParticipantFromChat$291(TLObject tLObject, TLRPC.TL_error tL_error) {
    }

    public void lambda$deleteParticipantFromChat$292(long j) {
        loadFullChat(j, 0, true);
    }

    public void lambda$deleteParticipantFromChat$293(boolean z, TLRPC.User user, final long j, TLObject tLObject, TLRPC.TL_error tL_error) {
        if (tL_error != null) {
            return;
        }
        processUpdates((TLRPC.Updates) tLObject, false);
        if (!z || UserObject.isUserSelf(user)) {
            return;
        }
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                MessagesController.this.lambda$deleteParticipantFromChat$292(j);
            }
        }, 1000L);
    }

    public void lambda$deleteSavedDialog$133(long j, int[] iArr, TLObject tLObject, TLRPC.TL_error tL_error) {
        if (tL_error == null) {
            TLRPC.TL_messages_affectedHistory tL_messages_affectedHistory = (TLRPC.TL_messages_affectedHistory) tLObject;
            if (tL_messages_affectedHistory.offset > 0) {
                deleteSavedDialog(j, iArr[0]);
            }
            processNewDifferenceParams(-1, tL_messages_affectedHistory.pts, -1, tL_messages_affectedHistory.pts_count);
            getMessagesStorage().onDeleteQueryComplete(j);
        }
    }

    public void lambda$deleteSavedDialog$134(final long j, int i, final int[] iArr) {
        SavedMessagesController.SavedDialog savedDialog;
        getMessagesStorage().deleteSavedDialog(j);
        TLRPC.TL_messages_deleteSavedHistory tL_messages_deleteSavedHistory = new TLRPC.TL_messages_deleteSavedHistory();
        tL_messages_deleteSavedHistory.peer = getInputPeer(j);
        if (i == 0) {
            int i2 = 0;
            while (true) {
                if (i2 >= getSavedMessagesController().allDialogs.size()) {
                    savedDialog = null;
                    break;
                } else {
                    if (getSavedMessagesController().allDialogs.get(i2).dialogId == j) {
                        savedDialog = getSavedMessagesController().allDialogs.get(i2);
                        break;
                    }
                    i2++;
                }
            }
            if (savedDialog != null) {
                iArr[0] = Math.max(iArr[0], savedDialog.top_message_id);
                getSavedMessagesController().deleteDialog(j);
            }
            int i3 = iArr[0];
            if (i3 <= 0) {
                i3 = Integer.MAX_VALUE;
            }
            tL_messages_deleteSavedHistory.max_id = i3;
        }
        getConnectionsManager().sendRequest(tL_messages_deleteSavedHistory, new RequestDelegate() {
            @Override
            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                MessagesController.this.lambda$deleteSavedDialog$133(j, iArr, tLObject, tL_error);
            }
        }, 64);
    }

    public static void lambda$deleteSavedDialog$135(int[] iArr, Runnable runnable, int i) {
        iArr[0] = i;
        runnable.run();
    }

    public void lambda$deleteUserChannelHistory$123(TLRPC.Chat chat, TLRPC.User user, TLRPC.Chat chat2, TLObject tLObject, TLRPC.TL_error tL_error) {
        if (tL_error == null) {
            TLRPC.TL_messages_affectedHistory tL_messages_affectedHistory = (TLRPC.TL_messages_affectedHistory) tLObject;
            int i = tL_messages_affectedHistory.offset;
            if (i > 0) {
                deleteUserChannelHistory(chat, user, chat2, i);
            }
            processNewChannelDifferenceParams(tL_messages_affectedHistory.pts, tL_messages_affectedHistory.pts_count, chat.id);
        }
    }

    public void lambda$deleteUserPhoto$109(TLObject tLObject, long j) {
        TLRPC.TL_photos_photo tL_photos_photo = (TLRPC.TL_photos_photo) tLObject;
        TLRPC.User user = getUser(Long.valueOf(getUserConfig().getClientUserId()));
        if (user == null) {
            user = getUserConfig().getCurrentUser();
            putUser(user, false);
        } else {
            getUserConfig().setCurrentUser(user);
        }
        if (user == null) {
            return;
        }
        ArrayList arrayList = new ArrayList();
        arrayList.add(user);
        getMessagesStorage().putUsersAndChats(arrayList, null, false, true);
        if (tL_photos_photo.photo instanceof TLRPC.TL_photo) {
            TLRPC.TL_userProfilePhoto tL_userProfilePhoto = new TLRPC.TL_userProfilePhoto();
            user.photo = tL_userProfilePhoto;
            tL_userProfilePhoto.has_video = !tL_photos_photo.photo.video_sizes.isEmpty();
            TLRPC.UserProfilePhoto userProfilePhoto = user.photo;
            TLRPC.Photo photo = tL_photos_photo.photo;
            userProfilePhoto.photo_id = photo.id;
            userProfilePhoto.photo_small = FileLoader.getClosestPhotoSizeWithSize(photo.sizes, 150).location;
            user.photo.photo_big = FileLoader.getClosestPhotoSizeWithSize(tL_photos_photo.photo.sizes, 800).location;
            user.photo.dc_id = tL_photos_photo.photo.dc_id;
        } else {
            user.photo = new TLRPC.TL_userProfilePhotoEmpty();
        }
        TLRPC.UserFull userFull = getUserFull(j);
        if (userFull != null) {
            userFull.profile_photo = tL_photos_photo.photo;
            getMessagesStorage().updateUserInfo(userFull, false);
        }
        getUserConfig().getCurrentUser().photo = user.photo;
        putUser(user, false);
        getNotificationCenter().lambda$postNotificationNameOnUIThread$1(NotificationCenter.mainUserInfoChanged, new Object[0]);
        NotificationCenter notificationCenter = getNotificationCenter();
        int i = NotificationCenter.updateInterfaces;
        notificationCenter.lambda$postNotificationNameOnUIThread$1(i, Integer.valueOf(UPDATE_MASK_ALL));
        getNotificationCenter().lambda$postNotificationNameOnUIThread$1(i, Integer.valueOf(UPDATE_MASK_AVATAR));
        getUserConfig().saveConfig(true);
    }

    public void lambda$deleteUserPhoto$110(final long j, final TLObject tLObject, TLRPC.TL_error tL_error) {
        if (tL_error == null) {
            AndroidUtilities.runOnUIThread(new Runnable() {
                @Override
                public final void run() {
                    MessagesController.this.lambda$deleteUserPhoto$109(tLObject, j);
                }
            });
        }
    }

    public void lambda$didAddedNewTask$75(int i) {
        int i2;
        if (!(this.currentDeletingTaskMids == null && this.currentDeletingTaskMediaMids == null && !this.gettingNewDeleteTask) && ((i2 = this.currentDeletingTaskTime) == 0 || i >= i2)) {
            return;
        }
        getNewDeleteTask(null, null);
    }

    public void lambda$didAddedNewTask$76(long j, SparseArray sparseArray) {
        getNotificationCenter().lambda$postNotificationNameOnUIThread$1(NotificationCenter.didCreatedNewDeleteTask, Long.valueOf(j), sparseArray);
    }

    public void lambda$didReceivedNotification$37() {
        getNotificationCenter().lambda$postNotificationNameOnUIThread$1(NotificationCenter.mainUserInfoChanged, new Object[0]);
        getNotificationCenter().lambda$postNotificationNameOnUIThread$1(NotificationCenter.updateInterfaces, Integer.valueOf(UPDATE_MASK_AVATAR));
        getUserConfig().saveConfig(true);
    }

    public void lambda$didReceivedNotification$38(TLObject tLObject, TLRPC.TL_error tL_error) {
        if (tL_error == null) {
            TLRPC.User user = getUser(Long.valueOf(getUserConfig().getClientUserId()));
            if (user == null) {
                user = getUserConfig().getCurrentUser();
                putUser(user, true);
            } else {
                getUserConfig().setCurrentUser(user);
            }
            if (user == null) {
                return;
            }
            TLRPC.TL_photos_photo tL_photos_photo = (TLRPC.TL_photos_photo) tLObject;
            ArrayList<TLRPC.PhotoSize> arrayList = tL_photos_photo.photo.sizes;
            TLRPC.PhotoSize closestPhotoSizeWithSize = FileLoader.getClosestPhotoSizeWithSize(arrayList, 100);
            TLRPC.PhotoSize closestPhotoSizeWithSize2 = FileLoader.getClosestPhotoSizeWithSize(arrayList, 1000);
            TLRPC.TL_userProfilePhoto tL_userProfilePhoto = new TLRPC.TL_userProfilePhoto();
            user.photo = tL_userProfilePhoto;
            tL_userProfilePhoto.photo_id = tL_photos_photo.photo.id;
            if (closestPhotoSizeWithSize != null) {
                tL_userProfilePhoto.photo_small = closestPhotoSizeWithSize.location;
            }
            if (closestPhotoSizeWithSize2 != null) {
                tL_userProfilePhoto.photo_big = closestPhotoSizeWithSize2.location;
            }
            getDialogPhotos(user.id).reset();
            getDialogPhotos(user.id).load(0, 80);
            ArrayList arrayList2 = new ArrayList();
            arrayList2.add(user);
            getMessagesStorage().putUsersAndChats(arrayList2, null, false, true);
            AndroidUtilities.runOnUIThread(new Runnable() {
                @Override
                public final void run() {
                    MessagesController.this.lambda$didReceivedNotification$37();
                }
            });
        }
    }

    public void lambda$didReceivedNotification$39(TLRPC.WallPaper wallPaper, TLRPC.TL_wallPaperSettings tL_wallPaperSettings, Theme.OverrideWallpaperInfo overrideWallpaperInfo, File file, String str) {
        if (this.uploadingWallpaper == null || this.uploadingWallpaperInfo.requestIds == null || wallPaper == null) {
            return;
        }
        wallPaper.settings = tL_wallPaperSettings;
        wallPaper.flags |= 4;
        overrideWallpaperInfo.slug = wallPaper.slug;
        overrideWallpaperInfo.saveOverrideWallpaper();
        ArrayList<TLRPC.WallPaper> arrayList = new ArrayList<>();
        arrayList.add(wallPaper);
        getMessagesStorage().putWallpapers(arrayList, 2);
        TLRPC.PhotoSize closestPhotoSizeWithSize = FileLoader.getClosestPhotoSizeWithSize(wallPaper.document.thumbs, 320);
        if (closestPhotoSizeWithSize != null) {
            ImageLoader.getInstance().replaceImageInCache(Utilities.MD5(file.getAbsolutePath()) + "@100_100", closestPhotoSizeWithSize.location.volume_id + "_" + closestPhotoSizeWithSize.location.local_id + "@100_100", ImageLocation.getForDocument(closestPhotoSizeWithSize, wallPaper.document), false);
        }
        NotificationCenter.getGlobalInstance().lambda$postNotificationNameOnUIThread$1(NotificationCenter.wallpapersNeedReload, wallPaper.slug);
        ArrayList arrayList2 = this.uploadingWallpaperInfo.requestIds;
        if (arrayList2 == null || overrideWallpaperInfo.dialogId == 0) {
            return;
        }
        arrayList2.add(Integer.valueOf(ChatThemeController.getInstance(this.currentAccount).setWallpaperToPeer(overrideWallpaperInfo.dialogId, str, overrideWallpaperInfo, null, null)));
    }

    public void lambda$didReceivedNotification$40(final Theme.OverrideWallpaperInfo overrideWallpaperInfo, final TLRPC.TL_wallPaperSettings tL_wallPaperSettings, final String str, TLObject tLObject, TLRPC.TL_error tL_error) {
        final TLRPC.WallPaper wallPaper = (TLRPC.WallPaper) tLObject;
        final File file = new File(ApplicationLoader.getFilesDirFixed(), overrideWallpaperInfo.originalFileName);
        if (wallPaper != null) {
            try {
                AndroidUtilities.copyFile(file, getFileLoader().getPathToAttach(wallPaper.document, true));
            } catch (Exception unused) {
            }
        }
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                MessagesController.this.lambda$didReceivedNotification$39(wallPaper, tL_wallPaperSettings, overrideWallpaperInfo, file, str);
            }
        });
    }

    public void lambda$didReceivedNotification$41(TLObject tLObject, Theme.ThemeInfo themeInfo, Theme.ThemeAccent themeAccent) {
        if (!(tLObject instanceof TLRPC.TL_theme)) {
            getNotificationCenter().lambda$postNotificationNameOnUIThread$1(NotificationCenter.themeUploadError, themeInfo, themeAccent);
            return;
        }
        Theme.setThemeUploadInfo(themeInfo, themeAccent, (TLRPC.TL_theme) tLObject, this.currentAccount, false);
        installTheme(themeInfo, themeAccent, themeInfo == Theme.getCurrentNightTheme());
        getNotificationCenter().lambda$postNotificationNameOnUIThread$1(NotificationCenter.themeUploadedToServer, themeInfo, themeAccent);
    }

    public void lambda$didReceivedNotification$42(final Theme.ThemeInfo themeInfo, final Theme.ThemeAccent themeAccent, final TLObject tLObject, TLRPC.TL_error tL_error) {
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                MessagesController.this.lambda$didReceivedNotification$41(tLObject, themeInfo, themeAccent);
            }
        });
    }

    public void lambda$didReceivedNotification$43(TLObject tLObject, Theme.ThemeInfo themeInfo, Theme.ThemeAccent themeAccent) {
        if (!(tLObject instanceof TLRPC.TL_theme)) {
            getNotificationCenter().lambda$postNotificationNameOnUIThread$1(NotificationCenter.themeUploadError, themeInfo, themeAccent);
        } else {
            Theme.setThemeUploadInfo(themeInfo, themeAccent, (TLRPC.TL_theme) tLObject, this.currentAccount, false);
            getNotificationCenter().lambda$postNotificationNameOnUIThread$1(NotificationCenter.themeUploadedToServer, themeInfo, themeAccent);
        }
    }

    public void lambda$didReceivedNotification$44(final Theme.ThemeInfo themeInfo, final Theme.ThemeAccent themeAccent, final TLObject tLObject, TLRPC.TL_error tL_error) {
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                MessagesController.this.lambda$didReceivedNotification$43(tLObject, themeInfo, themeAccent);
            }
        });
    }

    public void lambda$didReceivedNotification$45(Theme.ThemeInfo themeInfo, Theme.ThemeAccent themeAccent) {
        getNotificationCenter().lambda$postNotificationNameOnUIThread$1(NotificationCenter.themeUploadError, themeInfo, themeAccent);
    }

    public void lambda$didReceivedNotification$46(TLRPC.TL_theme tL_theme, final Theme.ThemeInfo themeInfo, TLRPC.TL_inputThemeSettings tL_inputThemeSettings, final Theme.ThemeAccent themeAccent, TLObject tLObject, TLRPC.TL_error tL_error) {
        RequestDelegate requestDelegate;
        TL_account.createTheme createtheme;
        ConnectionsManager connectionsManager;
        String name = tL_theme != null ? tL_theme.title : themeInfo.getName();
        int lastIndexOf = name.lastIndexOf(".attheme");
        if (lastIndexOf > 0) {
            name = name.substring(0, lastIndexOf);
        }
        if (tLObject == null) {
            AndroidUtilities.runOnUIThread(new Runnable() {
                @Override
                public final void run() {
                    MessagesController.this.lambda$didReceivedNotification$45(themeInfo, themeAccent);
                }
            });
            return;
        }
        TLRPC.Document document = (TLRPC.Document) tLObject;
        TLRPC.TL_inputDocument tL_inputDocument = new TLRPC.TL_inputDocument();
        tL_inputDocument.access_hash = document.access_hash;
        tL_inputDocument.id = document.id;
        tL_inputDocument.file_reference = document.file_reference;
        if (tL_theme == null || !tL_theme.creator) {
            TL_account.createTheme createtheme2 = new TL_account.createTheme();
            createtheme2.document = tL_inputDocument;
            createtheme2.flags |= 4;
            createtheme2.slug = (tL_theme == null || TextUtils.isEmpty(tL_theme.slug)) ? "" : tL_theme.slug;
            createtheme2.title = name;
            if (tL_inputThemeSettings != null) {
                createtheme2.settings = tL_inputThemeSettings;
                createtheme2.flags |= 8;
            }
            ConnectionsManager connectionsManager2 = getConnectionsManager();
            requestDelegate = new RequestDelegate() {
                @Override
                public final void run(TLObject tLObject2, TLRPC.TL_error tL_error2) {
                    MessagesController.this.lambda$didReceivedNotification$42(themeInfo, themeAccent, tLObject2, tL_error2);
                }
            };
            connectionsManager = connectionsManager2;
            createtheme = createtheme2;
        } else {
            TL_account.updateTheme updatetheme = new TL_account.updateTheme();
            TLRPC.TL_inputTheme tL_inputTheme = new TLRPC.TL_inputTheme();
            tL_inputTheme.id = tL_theme.id;
            tL_inputTheme.access_hash = tL_theme.access_hash;
            updatetheme.theme = tL_inputTheme;
            updatetheme.slug = tL_theme.slug;
            int i = updatetheme.flags;
            updatetheme.title = name;
            updatetheme.document = tL_inputDocument;
            updatetheme.flags = i | 7;
            if (tL_inputThemeSettings != null) {
                updatetheme.settings = tL_inputThemeSettings;
                updatetheme.flags = i | 15;
            }
            updatetheme.format = "android";
            ConnectionsManager connectionsManager3 = getConnectionsManager();
            requestDelegate = new RequestDelegate() {
                @Override
                public final void run(TLObject tLObject2, TLRPC.TL_error tL_error2) {
                    MessagesController.this.lambda$didReceivedNotification$44(themeInfo, themeAccent, tLObject2, tL_error2);
                }
            };
            connectionsManager = connectionsManager3;
            createtheme = updatetheme;
        }
        connectionsManager.sendRequest(createtheme, requestDelegate);
    }

    public void lambda$ensureMessagesLoaded$411(boolean[] zArr, TLRPC.Chat chat, Runnable[] runnableArr, long j, int i, MessagesLoadedCallback messagesLoadedCallback) {
        if (zArr[0]) {
            return;
        }
        if (chat != null) {
            getMessagesController().putChat(chat, true);
            runnableArr[0] = ensureMessagesLoaded(j, i, messagesLoadedCallback);
        } else if (messagesLoadedCallback != null) {
            messagesLoadedCallback.onError();
        }
    }

    public void lambda$ensureMessagesLoaded$412(final boolean[] zArr, MessagesStorage messagesStorage, long j, final Runnable[] runnableArr, final long j2, final int i, final MessagesLoadedCallback messagesLoadedCallback) {
        if (zArr[0]) {
            return;
        }
        final TLRPC.Chat chat = messagesStorage.getChat(j);
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                MessagesController.this.lambda$ensureMessagesLoaded$411(zArr, chat, runnableArr, j2, i, messagesLoadedCallback);
            }
        });
    }

    public static void lambda$ensureMessagesLoaded$413(boolean[] zArr, Runnable[] runnableArr) {
        zArr[0] = true;
        Runnable runnable = runnableArr[0];
        if (runnable != null) {
            runnable.run();
        }
    }

    public void lambda$ensureMessagesLoaded$414(int i) {
        getConnectionsManager().cancelRequestsForGuid(i);
    }

    public void lambda$generateJoinMessage$345(long j, ArrayList arrayList) {
        updateInterfaceWithMessages(-j, arrayList, 0);
        getNotificationCenter().lambda$postNotificationNameOnUIThread$1(NotificationCenter.dialogsNeedReload, new Object[0]);
    }

    public void lambda$getAvailableEffects$448(TLRPC.messages_AvailableEffects messages_availableeffects) {
        if (this.availableEffects != messages_availableeffects) {
            this.availableEffects = messages_availableeffects;
            if (messages_availableeffects != null) {
                AnimatedEmojiDrawable.getDocumentFetcher(this.currentAccount).putDocuments(this.availableEffects.documents);
            }
            getNotificationCenter().lambda$postNotificationNameOnUIThread$1(NotificationCenter.availableEffectsUpdate, new Object[0]);
        }
        this.loadingAvailableEffects = false;
    }

    public void lambda$getBlockedPeers$107(TLObject tLObject, boolean z, TLRPC.TL_contacts_getBlocked tL_contacts_getBlocked) {
        if (tLObject != null) {
            TLRPC.contacts_Blocked contacts_blocked = (TLRPC.contacts_Blocked) tLObject;
            putUsers(contacts_blocked.users, false);
            putChats(contacts_blocked.chats, false);
            getMessagesStorage().putUsersAndChats(contacts_blocked.users, contacts_blocked.chats, true, true);
            if (z) {
                this.blockePeers.clear();
            }
            this.totalBlockedCount = Math.max(contacts_blocked.count, contacts_blocked.blocked.size());
            this.blockedEndReached = contacts_blocked.blocked.size() < tL_contacts_getBlocked.limit;
            int size = contacts_blocked.blocked.size();
            for (int i = 0; i < size; i++) {
                this.blockePeers.put(MessageObject.getPeerId(contacts_blocked.blocked.get(i).peer_id), 1);
            }
            this.loadingBlockedPeers = false;
            getNotificationCenter().lambda$postNotificationNameOnUIThread$1(NotificationCenter.blockedUsersDidLoad, new Object[0]);
        }
    }

    public void lambda$getBlockedPeers$108(final boolean z, final TLRPC.TL_contacts_getBlocked tL_contacts_getBlocked, final TLObject tLObject, TLRPC.TL_error tL_error) {
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                MessagesController.this.lambda$getBlockedPeers$107(tLObject, z, tL_contacts_getBlocked);
            }
        });
    }

    public void lambda$getChannelDifference$311(long j) {
        NotificationCenter.getInstance(this.currentAccount).lambda$postNotificationNameOnUIThread$1(NotificationCenter.onReceivedChannelDifference, Long.valueOf(j));
    }

    public void lambda$getChannelDifference$312(long j) {
        NotificationCenter.getInstance(this.currentAccount).lambda$postNotificationNameOnUIThread$1(NotificationCenter.onReceivedChannelDifference, Long.valueOf(j));
    }

    public void lambda$getChannelDifference$313(long j) {
        NotificationCenter.getInstance(this.currentAccount).lambda$postNotificationNameOnUIThread$1(NotificationCenter.onReceivedChannelDifference, Long.valueOf(j));
    }

    public void lambda$getChannelDifference$314(long j) {
        NotificationCenter.getInstance(this.currentAccount).lambda$postNotificationNameOnUIThread$1(NotificationCenter.onReceivedChannelDifference, Long.valueOf(j));
    }

    public void lambda$getChannelDifference$315(TLRPC.updates_ChannelDifference updates_channeldifference) {
        putUsers(updates_channeldifference.users, false);
        putChats(updates_channeldifference.chats, false);
    }

    public void lambda$getChannelDifference$316(SparseArray sparseArray) {
        for (int i = 0; i < sparseArray.size(); i++) {
            int keyAt = sparseArray.keyAt(i);
            long[] jArr = (long[]) sparseArray.valueAt(i);
            getSendMessagesHelper().processSentMessage((int) jArr[1]);
            NotificationCenter notificationCenter = getNotificationCenter();
            int i2 = NotificationCenter.messageReceivedByServer;
            Integer valueOf = Integer.valueOf((int) jArr[1]);
            Integer valueOf2 = Integer.valueOf(keyAt);
            Long valueOf3 = Long.valueOf(jArr[0]);
            Boolean bool = Boolean.FALSE;
            notificationCenter.lambda$postNotificationNameOnUIThread$1(i2, valueOf, valueOf2, null, valueOf3, 0L, -1, bool);
            getNotificationCenter().lambda$postNotificationNameOnUIThread$1(NotificationCenter.messageReceivedByServer2, Integer.valueOf((int) jArr[1]), Integer.valueOf(keyAt), null, Long.valueOf(jArr[0]), 0L, -1, bool);
        }
    }

    public void lambda$getChannelDifference$317(LongSparseArray longSparseArray) {
        for (int i = 0; i < longSparseArray.size(); i++) {
            updateInterfaceWithMessages(longSparseArray.keyAt(i), (ArrayList) longSparseArray.valueAt(i), 0);
        }
        getNotificationCenter().lambda$postNotificationNameOnUIThread$1(NotificationCenter.dialogsNeedReload, new Object[0]);
    }

    public void lambda$getChannelDifference$318(ArrayList arrayList) {
        getNotificationsController().processNewMessages(arrayList, true, false, null);
    }

    public void lambda$getChannelDifference$319(final ArrayList arrayList, TLRPC.updates_ChannelDifference updates_channeldifference) {
        if (!arrayList.isEmpty()) {
            AndroidUtilities.runOnUIThread(new Runnable() {
                @Override
                public final void run() {
                    MessagesController.this.lambda$getChannelDifference$318(arrayList);
                }
            });
        }
        getMessagesStorage().putMessages(updates_channeldifference.new_messages, true, false, false, getDownloadController().getAutodownloadMask(), 0, 0L);
    }

    public void lambda$getChannelDifference$320(long j) {
        NotificationCenter.getInstance(this.currentAccount).lambda$postNotificationNameOnUIThread$1(NotificationCenter.onReceivedChannelDifference, Long.valueOf(j));
    }

    public void lambda$getChannelDifference$321(final long j) {
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                MessagesController.this.lambda$getChannelDifference$320(j);
            }
        });
    }

    public void lambda$getChannelDifference$322(long j) {
        NotificationCenter.getInstance(this.currentAccount).lambda$postNotificationNameOnUIThread$1(NotificationCenter.onReceivedChannelDifference, Long.valueOf(j));
    }

    public void lambda$getChannelDifference$323(final org.telegram.tgnet.TLRPC.updates_ChannelDifference r23, final long r24, org.telegram.tgnet.TLRPC.Chat r26, androidx.collection.LongSparseArray r27, int r28, long r29) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MessagesController.lambda$getChannelDifference$323(org.telegram.tgnet.TLRPC$updates_ChannelDifference, long, org.telegram.tgnet.TLRPC$Chat, androidx.collection.LongSparseArray, int, long):void");
    }

    public void lambda$getChannelDifference$324(ArrayList arrayList, final long j, final TLRPC.updates_ChannelDifference updates_channeldifference, final TLRPC.Chat chat, final LongSparseArray longSparseArray, final int i, final long j2) {
        if (!arrayList.isEmpty()) {
            final SparseArray sparseArray = new SparseArray();
            Iterator it = arrayList.iterator();
            while (it.hasNext()) {
                TLRPC.TL_updateMessageID tL_updateMessageID = (TLRPC.TL_updateMessageID) it.next();
                long[] updateMessageStateAndId = getMessagesStorage().updateMessageStateAndId(tL_updateMessageID.random_id, -j, null, tL_updateMessageID.id, 0, false, -1, 0);
                if (updateMessageStateAndId != null) {
                    sparseArray.put(tL_updateMessageID.id, updateMessageStateAndId);
                }
            }
            if (sparseArray.size() != 0) {
                AndroidUtilities.runOnUIThread(new Runnable() {
                    @Override
                    public final void run() {
                        MessagesController.this.lambda$getChannelDifference$316(sparseArray);
                    }
                });
            }
        }
        Utilities.stageQueue.postRunnable(new Runnable() {
            @Override
            public final void run() {
                MessagesController.this.lambda$getChannelDifference$323(updates_channeldifference, j, chat, longSparseArray, i, j2);
            }
        });
    }

    public void lambda$getChannelDifference$325(TLRPC.TL_error tL_error, long j) {
        checkChannelError(tL_error.text, j);
        NotificationCenter.getInstance(this.currentAccount).lambda$postNotificationNameOnUIThread$1(NotificationCenter.onReceivedChannelDifference, Long.valueOf(j));
    }

    public void lambda$getChannelDifference$326(final long j, final int i, final long j2, TLObject tLObject, final TLRPC.TL_error tL_error) {
        final TLRPC.Chat chat;
        if (tLObject == null) {
            if (tL_error != null) {
                AndroidUtilities.runOnUIThread(new Runnable() {
                    @Override
                    public final void run() {
                        MessagesController.this.lambda$getChannelDifference$325(tL_error, j);
                    }
                });
                this.gettingDifferenceChannels.delete(j);
                if (j2 != 0) {
                    getMessagesStorage().removePendingTask(j2);
                    return;
                }
                return;
            }
            return;
        }
        final TLRPC.updates_ChannelDifference updates_channeldifference = (TLRPC.updates_ChannelDifference) tLObject;
        final LongSparseArray longSparseArray = new LongSparseArray();
        int i2 = 0;
        for (int i3 = 0; i3 < updates_channeldifference.users.size(); i3++) {
            TLRPC.User user = updates_channeldifference.users.get(i3);
            longSparseArray.put(user.id, user);
        }
        int i4 = 0;
        while (true) {
            if (i4 >= updates_channeldifference.chats.size()) {
                chat = null;
                break;
            }
            TLRPC.Chat chat2 = updates_channeldifference.chats.get(i4);
            if (chat2.id == j) {
                chat = chat2;
                break;
            }
            i4++;
        }
        final ArrayList arrayList = new ArrayList();
        if (!updates_channeldifference.other_updates.isEmpty()) {
            while (i2 < updates_channeldifference.other_updates.size()) {
                TLRPC.Update update = updates_channeldifference.other_updates.get(i2);
                if (update instanceof TLRPC.TL_updateMessageID) {
                    arrayList.add((TLRPC.TL_updateMessageID) update);
                    updates_channeldifference.other_updates.remove(i2);
                    i2--;
                }
                i2++;
            }
        }
        getMessagesStorage().putUsersAndChats(updates_channeldifference.users, updates_channeldifference.chats, true, true);
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                MessagesController.this.lambda$getChannelDifference$315(updates_channeldifference);
            }
        });
        getMessagesStorage().getStorageQueue().postRunnable(new Runnable() {
            @Override
            public final void run() {
                MessagesController.this.lambda$getChannelDifference$324(arrayList, j, updates_channeldifference, chat, longSparseArray, i, j2);
            }
        });
    }

    public static void lambda$getChannelParticipant$424(Utilities.Callback callback, TLObject tLObject, TLRPC.TL_error tL_error) {
        if (callback != null) {
            callback.run(tLObject instanceof TLRPC.TL_channels_channelParticipant ? ((TLRPC.TL_channels_channelParticipant) tLObject).participant : null);
        }
    }

    public void lambda$getChannelRecommendations$433(TLObject tLObject, boolean z, long j) {
        if (tLObject instanceof TLRPC.messages_Chats) {
            ArrayList<TLRPC.Chat> arrayList = ((TLRPC.messages_Chats) tLObject).chats;
            putChats(arrayList, false);
            ChannelRecommendations channelRecommendations = new ChannelRecommendations();
            channelRecommendations.wasPremium = z;
            channelRecommendations.chats.addAll(arrayList);
            if (tLObject instanceof TLRPC.TL_messages_chatsSlice) {
                channelRecommendations.more = Math.max(0, ((TLRPC.TL_messages_chatsSlice) tLObject).count - arrayList.size());
            } else if (!getUserConfig().isPremium() && BuildVars.DEBUG_PRIVATE_VERSION) {
                channelRecommendations.more = 90;
            }
            this.cachedChannelRecommendations.put(Long.valueOf(j), channelRecommendations);
            getNotificationCenter().lambda$postNotificationNameOnUIThread$1(NotificationCenter.channelRecommendationsLoaded, Long.valueOf(j));
            return;
        }
        if (tLObject instanceof TLRPC.Users) {
            ArrayList<TLRPC.User> arrayList2 = ((TLRPC.Users) tLObject).users;
            putUsers(arrayList2, false);
            ChannelRecommendations channelRecommendations2 = new ChannelRecommendations();
            channelRecommendations2.wasPremium = z;
            channelRecommendations2.chats.addAll(arrayList2);
            if (tLObject instanceof TLRPC.TL_usersSlice) {
                channelRecommendations2.more = Math.max(0, ((TLRPC.TL_usersSlice) tLObject).count - arrayList2.size());
            } else if (!getUserConfig().isPremium() && BuildVars.DEBUG_PRIVATE_VERSION) {
                channelRecommendations2.more = 90;
            }
            this.cachedChannelRecommendations.put(Long.valueOf(j), channelRecommendations2);
            getNotificationCenter().lambda$postNotificationNameOnUIThread$1(NotificationCenter.channelRecommendationsLoaded, Long.valueOf(j));
        }
    }

    public void lambda$getChannelRecommendations$434(final boolean z, final long j, final TLObject tLObject, TLRPC.TL_error tL_error) {
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                MessagesController.this.lambda$getChannelRecommendations$433(tLObject, z, j);
            }
        });
    }

    public void lambda$getContentSettings$453(TLObject tLObject) {
        if (tLObject instanceof TL_account.contentSettings) {
            this.contentSettings = (TL_account.contentSettings) tLObject;
            this.contentSettingsLoadedTime = System.currentTimeMillis();
        }
        this.contentSettingsLoading = false;
        ArrayList<Utilities.Callback<TL_account.contentSettings>> arrayList = this.contentSettingsCallbacks;
        if (arrayList != null) {
            Iterator<Utilities.Callback<TL_account.contentSettings>> it = arrayList.iterator();
            while (it.hasNext()) {
                it.next().run(this.contentSettings);
            }
            this.contentSettingsCallbacks.clear();
            this.contentSettingsCallbacks = null;
        }
    }

    public void lambda$getContentSettings$454(final TLObject tLObject, TLRPC.TL_error tL_error) {
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                MessagesController.this.lambda$getContentSettings$453(tLObject);
            }
        });
    }

    public void lambda$getDifference$327(TLRPC.updates_Difference updates_difference, int i, int i2) {
        this.loadedFullUsers.clear();
        this.loadedFullChats.clear();
        resetDialogs(true, getMessagesStorage().getLastSeqValue(), updates_difference.pts, i, i2);
        getStoriesController().cleanup();
    }

    public void lambda$getDifference$328(TLRPC.updates_Difference updates_difference) {
        this.loadedFullUsers.clear();
        this.loadedFullChats.clear();
        putUsers(updates_difference.users, false);
        putChats(updates_difference.chats, false);
    }

    public void lambda$getDifference$329(SparseArray sparseArray) {
        for (int i = 0; i < sparseArray.size(); i++) {
            int keyAt = sparseArray.keyAt(i);
            long[] jArr = (long[]) sparseArray.valueAt(i);
            getSendMessagesHelper().processSentMessage((int) jArr[1]);
            NotificationCenter notificationCenter = getNotificationCenter();
            int i2 = NotificationCenter.messageReceivedByServer;
            Integer valueOf = Integer.valueOf((int) jArr[1]);
            Integer valueOf2 = Integer.valueOf(keyAt);
            Long valueOf3 = Long.valueOf(jArr[0]);
            Boolean bool = Boolean.FALSE;
            notificationCenter.lambda$postNotificationNameOnUIThread$1(i2, valueOf, valueOf2, null, valueOf3, 0L, -1, bool);
            getNotificationCenter().lambda$postNotificationNameOnUIThread$1(NotificationCenter.messageReceivedByServer2, Integer.valueOf((int) jArr[1]), Integer.valueOf(keyAt), null, Long.valueOf(jArr[0]), 0L, -1, bool);
        }
    }

    public void lambda$getDifference$330(ArrayList arrayList, TLRPC.updates_Difference updates_difference) {
        getNotificationsController().processNewMessages(arrayList, !(updates_difference instanceof TLRPC.TL_updates_differenceSlice), false, null);
    }

    public void lambda$getDifference$331(long j, ArrayList arrayList) {
        updateInterfaceWithMessages(j, arrayList, 0);
        getNotificationCenter().lambda$postNotificationNameOnUIThread$1(NotificationCenter.dialogsNeedReload, new Object[0]);
    }

    public void lambda$getDifference$332(final long j, final ArrayList arrayList) {
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                MessagesController.this.lambda$getDifference$331(j, arrayList);
            }
        });
    }

    public void lambda$getDifference$333(final ArrayList arrayList, final TLRPC.updates_Difference updates_difference, LongSparseArray longSparseArray) {
        if (!arrayList.isEmpty()) {
            AndroidUtilities.runOnUIThread(new Runnable() {
                @Override
                public final void run() {
                    MessagesController.this.lambda$getDifference$330(arrayList, updates_difference);
                }
            });
        }
        getMessagesStorage().putMessages(updates_difference.new_messages, true, false, false, getDownloadController().getAutodownloadMask(), 0, 0L);
        for (int i = 0; i < longSparseArray.size(); i++) {
            final long keyAt = longSparseArray.keyAt(i);
            final ArrayList<MessageObject> arrayList2 = (ArrayList) longSparseArray.valueAt(i);
            getMediaDataController().loadReplyMessagesForMessages(arrayList2, keyAt, 0, 0L, new Runnable() {
                @Override
                public final void run() {
                    MessagesController.this.lambda$getDifference$332(keyAt, arrayList2);
                }
            }, 0, null);
        }
    }

    public void lambda$getDifference$334(final org.telegram.tgnet.TLRPC.updates_Difference r19, androidx.collection.LongSparseArray r20, androidx.collection.LongSparseArray r21) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MessagesController.lambda$getDifference$334(org.telegram.tgnet.TLRPC$updates_Difference, androidx.collection.LongSparseArray, androidx.collection.LongSparseArray):void");
    }

    public void lambda$getDifference$335(final TLRPC.updates_Difference updates_difference, ArrayList arrayList, final LongSparseArray longSparseArray, final LongSparseArray longSparseArray2) {
        getMessagesStorage().putUsersAndChats(updates_difference.users, updates_difference.chats, true, false);
        if (!arrayList.isEmpty()) {
            final SparseArray sparseArray = new SparseArray();
            for (int i = 0; i < arrayList.size(); i++) {
                TLRPC.TL_updateMessageID tL_updateMessageID = (TLRPC.TL_updateMessageID) arrayList.get(i);
                long[] updateMessageStateAndId = getMessagesStorage().updateMessageStateAndId(tL_updateMessageID.random_id, 0L, null, tL_updateMessageID.id, 0, false, -1, 0);
                if (updateMessageStateAndId != null) {
                    sparseArray.put(tL_updateMessageID.id, updateMessageStateAndId);
                }
            }
            if (sparseArray.size() != 0) {
                AndroidUtilities.runOnUIThread(new Runnable() {
                    @Override
                    public final void run() {
                        MessagesController.this.lambda$getDifference$329(sparseArray);
                    }
                });
            }
        }
        Utilities.stageQueue.postRunnable(new Runnable() {
            @Override
            public final void run() {
                MessagesController.this.lambda$getDifference$334(updates_difference, longSparseArray, longSparseArray2);
            }
        });
    }

    public void lambda$getDifference$336(final int i, final int i2, TLObject tLObject, TLRPC.TL_error tL_error) {
        int i3 = 0;
        if (tL_error != null) {
            this.gettingDifference = false;
            getConnectionsManager().setIsUpdating(false);
            FileLog.d("received: isUpdating = false");
            return;
        }
        final TLRPC.updates_Difference updates_difference = (TLRPC.updates_Difference) tLObject;
        if (updates_difference instanceof TLRPC.TL_updates_differenceTooLong) {
            AndroidUtilities.runOnUIThread(new Runnable() {
                @Override
                public final void run() {
                    MessagesController.this.lambda$getDifference$327(updates_difference, i, i2);
                }
            });
            return;
        }
        if (updates_difference instanceof TLRPC.TL_updates_differenceSlice) {
            TLRPC.TL_updates_state tL_updates_state = updates_difference.intermediate_state;
            getDifference(tL_updates_state.pts, tL_updates_state.date, tL_updates_state.qts, true);
        }
        final LongSparseArray longSparseArray = new LongSparseArray();
        final LongSparseArray longSparseArray2 = new LongSparseArray();
        for (int i4 = 0; i4 < updates_difference.users.size(); i4++) {
            TLRPC.User user = updates_difference.users.get(i4);
            longSparseArray.put(user.id, user);
        }
        for (int i5 = 0; i5 < updates_difference.chats.size(); i5++) {
            TLRPC.Chat chat = updates_difference.chats.get(i5);
            longSparseArray2.put(chat.id, chat);
        }
        final ArrayList arrayList = new ArrayList();
        if (!updates_difference.other_updates.isEmpty()) {
            while (i3 < updates_difference.other_updates.size()) {
                TLRPC.Update update = updates_difference.other_updates.get(i3);
                if (update instanceof TLRPC.TL_updateMessageID) {
                    arrayList.add((TLRPC.TL_updateMessageID) update);
                } else {
                    if (getUpdateType(update) == 2) {
                        long updateChannelId = getUpdateChannelId(update);
                        int i6 = this.channelsPts.get(updateChannelId);
                        if (i6 == 0 && (i6 = getMessagesStorage().getChannelPtsSync(updateChannelId)) != 0) {
                            this.channelsPts.put(updateChannelId, i6);
                        }
                        if (i6 != 0) {
                            if (getUpdatePts(update) > i6) {
                            }
                        }
                    }
                    i3++;
                }
                updates_difference.other_updates.remove(i3);
                i3--;
                i3++;
            }
        }
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                MessagesController.this.lambda$getDifference$328(updates_difference);
            }
        });
        getMessagesStorage().getStorageQueue().postRunnable(new Runnable() {
            @Override
            public final void run() {
                MessagesController.this.lambda$getDifference$335(updates_difference, arrayList, longSparseArray, longSparseArray2);
            }
        });
    }

    public void lambda$getGroupCall$56(TLObject tLObject, long j, Runnable runnable) {
        if (tLObject != null) {
            TL_phone.groupCall groupcall = (TL_phone.groupCall) tLObject;
            putUsers(groupcall.users, false);
            putChats(groupcall.chats, false);
            ChatObject.Call call = new ChatObject.Call();
            call.setCall(getAccountInstance(), j, groupcall);
            this.groupCalls.put(groupcall.call.id, call);
            this.groupCallsByChatId.put(j, call);
            getNotificationCenter().lambda$postNotificationNameOnUIThread$1(NotificationCenter.groupCallUpdated, Long.valueOf(j), Long.valueOf(groupcall.call.id), Boolean.FALSE);
            if (runnable != null) {
                runnable.run();
            }
        }
        this.loadingGroupCalls.remove(Long.valueOf(j));
    }

    public void lambda$getGroupCall$57(final long j, final Runnable runnable, final TLObject tLObject, TLRPC.TL_error tL_error) {
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                MessagesController.this.lambda$getGroupCall$56(tLObject, j, runnable);
            }
        });
    }

    public void lambda$getNewDeleteTask$77(LongSparseArray longSparseArray, LongSparseArray longSparseArray2) {
        this.gettingNewDeleteTask = true;
        getMessagesStorage().getNewTask(longSparseArray, longSparseArray2);
    }

    public static void lambda$getNextReactionMention$1(Consumer consumer, int i) {
        consumer.accept(Integer.valueOf(i));
    }

    public static void lambda$getNextReactionMention$2(Consumer consumer, int i) {
        consumer.accept(Integer.valueOf(i));
    }

    public static void lambda$getNextReactionMention$3(TLObject tLObject, TLRPC.TL_error tL_error, final Consumer consumer) {
        ArrayList<TLRPC.Message> arrayList;
        TLRPC.messages_Messages messages_messages = (TLRPC.messages_Messages) tLObject;
        final int i = 0;
        if (tL_error != null && messages_messages != null && (arrayList = messages_messages.messages) != null && !arrayList.isEmpty()) {
            i = messages_messages.messages.get(0).id;
        }
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                MessagesController.lambda$getNextReactionMention$2(Consumer.this, i);
            }
        });
    }

    public static void lambda$getNextReactionMention$4(final Consumer consumer, final TLObject tLObject, final TLRPC.TL_error tL_error) {
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                MessagesController.lambda$getNextReactionMention$3(TLObject.this, tL_error, consumer);
            }
        });
    }

    public void lambda$getNextReactionMention$5(long r13, long r15, final androidx.core.util.Consumer r17, int r18) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MessagesController.lambda$getNextReactionMention$5(long, long, androidx.core.util.Consumer, int):void");
    }

    public void lambda$getSavedReactionTags$437(TLObject tLObject, long j, TLRPC.messages_SavedReactionTags messages_savedreactiontags, TLRPC.TL_messages_getSavedReactionTags tL_messages_getSavedReactionTags) {
        TLRPC.TL_messages_savedReactionsTags tL_messages_savedReactionsTags;
        if (tLObject instanceof TLRPC.TL_messages_savedReactionsTags) {
            tL_messages_savedReactionsTags = (TLRPC.TL_messages_savedReactionsTags) tLObject;
            this.reactionTags.put(j, tL_messages_savedReactionsTags);
            getNotificationCenter().lambda$postNotificationNameOnUIThread$1(NotificationCenter.savedReactionTagsUpdate, Long.valueOf(j));
        } else {
            if (!(tLObject instanceof TLRPC.TL_messages_savedReactionsTagsNotModified) || messages_savedreactiontags != null || tL_messages_getSavedReactionTags.hash != 0) {
                return;
            }
            tL_messages_savedReactionsTags = new TLRPC.TL_messages_savedReactionsTags();
            this.reactionTags.put(j, tL_messages_savedReactionsTags);
            getNotificationCenter().lambda$postNotificationNameOnUIThread$1(NotificationCenter.savedReactionTagsUpdate, Long.valueOf(j));
        }
        saveSavedReactionsTags(j, tL_messages_savedReactionsTags);
    }

    public void lambda$getSavedReactionTags$438(final long j, final TLRPC.messages_SavedReactionTags messages_savedreactiontags, final TLRPC.TL_messages_getSavedReactionTags tL_messages_getSavedReactionTags, final TLObject tLObject, TLRPC.TL_error tL_error) {
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                MessagesController.this.lambda$getSavedReactionTags$437(tLObject, j, messages_savedreactiontags, tL_messages_getSavedReactionTags);
            }
        });
    }

    public void lambda$getSavedReactionTags$439(final TLRPC.messages_SavedReactionTags messages_savedreactiontags, final long j) {
        if (this.reactionTags == null) {
            this.reactionTags = new LongSparseArray();
        }
        boolean z = messages_savedreactiontags instanceof TLRPC.TL_messages_savedReactionsTags;
        if (z) {
            this.reactionTags.put(j, (TLRPC.TL_messages_savedReactionsTags) messages_savedreactiontags);
            getNotificationCenter().lambda$postNotificationNameOnUIThread$1(NotificationCenter.savedReactionTagsUpdate, Long.valueOf(j));
        }
        final TLRPC.TL_messages_getSavedReactionTags tL_messages_getSavedReactionTags = new TLRPC.TL_messages_getSavedReactionTags();
        if (z) {
            tL_messages_getSavedReactionTags.hash = messages_savedreactiontags.hash;
        }
        if (j != 0) {
            tL_messages_getSavedReactionTags.flags = 1 | tL_messages_getSavedReactionTags.flags;
            tL_messages_getSavedReactionTags.peer = getInputPeer(j);
        }
        getConnectionsManager().sendRequest(tL_messages_getSavedReactionTags, new RequestDelegate() {
            @Override
            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                MessagesController.this.lambda$getSavedReactionTags$438(j, messages_savedreactiontags, tL_messages_getSavedReactionTags, tLObject, tL_error);
            }
        });
    }

    public void lambda$getSavedReactionTags$440(final long r8) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MessagesController.lambda$getSavedReactionTags$440(long):void");
    }

    public void lambda$getSendAsPeers$394(TLRPC.TL_channels_sendAsPeers tL_channels_sendAsPeers) {
        putUsers(tL_channels_sendAsPeers.users, false);
        putChats(tL_channels_sendAsPeers.chats, false);
    }

    public void lambda$getSendAsPeers$395(TLRPC.TL_channels_sendAsPeers tL_channels_sendAsPeers, long j, SendAsPeersInfo sendAsPeersInfo) {
        if (tL_channels_sendAsPeers == null) {
            this.sendAsPeers.remove(j);
            return;
        }
        sendAsPeersInfo.loadTime = SystemClock.elapsedRealtime();
        sendAsPeersInfo.sendAsPeers = tL_channels_sendAsPeers;
        getNotificationCenter().lambda$postNotificationNameOnUIThread$1(NotificationCenter.didLoadSendAsPeers, Long.valueOf(j), tL_channels_sendAsPeers);
    }

    public void lambda$getSendAsPeers$396(final long j, final SendAsPeersInfo sendAsPeersInfo, TLObject tLObject, TLRPC.TL_error tL_error) {
        final TLRPC.TL_channels_sendAsPeers tL_channels_sendAsPeers;
        if (tLObject != null) {
            tL_channels_sendAsPeers = (TLRPC.TL_channels_sendAsPeers) tLObject;
            if (!tL_channels_sendAsPeers.peers.isEmpty()) {
                AndroidUtilities.runOnUIThread(new Runnable() {
                    @Override
                    public final void run() {
                        MessagesController.this.lambda$getSendAsPeers$394(tL_channels_sendAsPeers);
                    }
                });
                final TLRPC.TL_channels_sendAsPeers tL_channels_sendAsPeers2 = tL_channels_sendAsPeers;
                AndroidUtilities.runOnUIThread(new Runnable() {
                    @Override
                    public final void run() {
                        MessagesController.this.lambda$getSendAsPeers$395(tL_channels_sendAsPeers2, j, sendAsPeersInfo);
                    }
                });
            }
        }
        tL_channels_sendAsPeers = null;
        final TLRPC.TL_channels_sendAsPeers tL_channels_sendAsPeers22 = tL_channels_sendAsPeers;
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                MessagesController.this.lambda$getSendAsPeers$395(tL_channels_sendAsPeers22, j, sendAsPeersInfo);
            }
        });
    }

    public void lambda$getSponsoredMessages$391(TLRPC.messages_SponsoredMessages messages_sponsoredmessages) {
        putUsers(messages_sponsoredmessages.users, false);
        putChats(messages_sponsoredmessages.chats, false);
    }

    public void lambda$getSponsoredMessages$392(ArrayList arrayList, long j, SponsoredMessagesInfo sponsoredMessagesInfo, Integer num) {
        if (arrayList == null) {
            this.sponsoredMessages.remove(j);
            return;
        }
        sponsoredMessagesInfo.loadTime = SystemClock.elapsedRealtime();
        sponsoredMessagesInfo.messages = arrayList;
        sponsoredMessagesInfo.posts_between = num;
        getNotificationCenter().lambda$postNotificationNameOnUIThread$1(NotificationCenter.didLoadSponsoredMessages, Long.valueOf(j), arrayList);
    }

    public void lambda$getSponsoredMessages$393(final long j, final SponsoredMessagesInfo sponsoredMessagesInfo, TLObject tLObject, TLRPC.TL_error tL_error) {
        final Integer num;
        final ArrayList arrayList = null;
        r2 = null;
        Integer num2 = null;
        if (tLObject instanceof TLRPC.messages_SponsoredMessages) {
            final TLRPC.messages_SponsoredMessages messages_sponsoredmessages = (TLRPC.messages_SponsoredMessages) tLObject;
            if (!messages_sponsoredmessages.messages.isEmpty()) {
                if ((messages_sponsoredmessages instanceof TLRPC.TL_messages_sponsoredMessages) && (messages_sponsoredmessages.flags & 1) > 0) {
                    num2 = Integer.valueOf(messages_sponsoredmessages.posts_between);
                }
                ArrayList arrayList2 = new ArrayList();
                AndroidUtilities.runOnUIThread(new Runnable() {
                    @Override
                    public final void run() {
                        MessagesController.this.lambda$getSponsoredMessages$391(messages_sponsoredmessages);
                    }
                });
                LongSparseArray longSparseArray = new LongSparseArray();
                LongSparseArray longSparseArray2 = new LongSparseArray();
                int i = 0;
                for (int i2 = 0; i2 < messages_sponsoredmessages.users.size(); i2++) {
                    TLRPC.User user = messages_sponsoredmessages.users.get(i2);
                    longSparseArray.put(user.id, user);
                }
                for (int i3 = 0; i3 < messages_sponsoredmessages.chats.size(); i3++) {
                    TLRPC.Chat chat = messages_sponsoredmessages.chats.get(i3);
                    longSparseArray2.put(chat.id, chat);
                }
                int size = messages_sponsoredmessages.messages.size();
                int i4 = -10000000;
                while (i < size) {
                    TLRPC.TL_sponsoredMessage tL_sponsoredMessage = messages_sponsoredmessages.messages.get(i);
                    TLRPC.TL_message tL_message = new TLRPC.TL_message();
                    if (!tL_sponsoredMessage.entities.isEmpty()) {
                        tL_message.entities = tL_sponsoredMessage.entities;
                        tL_message.flags |= 128;
                    }
                    tL_message.peer_id = getPeer(j);
                    tL_message.flags |= 256;
                    tL_message.date = getConnectionsManager().getCurrentTime();
                    int i5 = i4 - 1;
                    tL_message.id = i4;
                    tL_message.message = tL_sponsoredMessage.message;
                    TLRPC.MessageMedia messageMedia = tL_sponsoredMessage.media;
                    if (messageMedia != null) {
                        tL_message.flags |= 512;
                    }
                    tL_message.media = messageMedia;
                    MessageObject messageObject = new MessageObject(this.currentAccount, (TLRPC.Message) tL_message, longSparseArray, longSparseArray2, true, true);
                    messageObject.sponsoredId = tL_sponsoredMessage.random_id;
                    messageObject.sponsoredTitle = tL_sponsoredMessage.title;
                    messageObject.sponsoredUrl = tL_sponsoredMessage.url;
                    messageObject.sponsoredRecommended = tL_sponsoredMessage.recommended;
                    messageObject.sponsoredPhoto = tL_sponsoredMessage.photo;
                    messageObject.sponsoredInfo = tL_sponsoredMessage.sponsor_info;
                    messageObject.sponsoredAdditionalInfo = tL_sponsoredMessage.additional_info;
                    messageObject.sponsoredButtonText = tL_sponsoredMessage.button_text;
                    messageObject.sponsoredCanReport = tL_sponsoredMessage.can_report;
                    messageObject.sponsoredColor = tL_sponsoredMessage.color;
                    messageObject.sponsoredMedia = tL_sponsoredMessage.media;
                    messageObject.setType();
                    messageObject.textLayoutBlocks = new ArrayList<>();
                    messageObject.generateThumbs(true);
                    arrayList2.add(messageObject);
                    i++;
                    messages_sponsoredmessages = messages_sponsoredmessages;
                    i4 = i5;
                }
                num = num2;
                arrayList = arrayList2;
                AndroidUtilities.runOnUIThread(new Runnable() {
                    @Override
                    public final void run() {
                        MessagesController.this.lambda$getSponsoredMessages$392(arrayList, j, sponsoredMessagesInfo, num);
                    }
                });
            }
        }
        num = null;
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                MessagesController.this.lambda$getSponsoredMessages$392(arrayList, j, sponsoredMessagesInfo, num);
            }
        });
    }

    public static void lambda$hidePeerSettingsBar$69(TLObject tLObject, TLRPC.TL_error tL_error) {
    }

    public static void lambda$hidePromoDialog$125(TLObject tLObject, TLRPC.TL_error tL_error) {
    }

    public void lambda$hidePromoDialog$126() {
        this.promoDialogId = 0L;
        this.proxyDialogAddress = null;
        this.nextPromoInfoCheckTime = getConnectionsManager().getCurrentTime() + 3600;
        getGlobalMainSettings().edit().putLong("proxy_dialog", this.promoDialogId).remove("proxyDialogAddress").putInt("nextPromoInfoCheckTime", this.nextPromoInfoCheckTime).commit();
    }

    public static void lambda$installTheme$112(TLObject tLObject, TLRPC.TL_error tL_error) {
    }

    public static void lambda$installTheme$113(TLObject tLObject, TLRPC.TL_error tL_error) {
    }

    public void lambda$loadAppConfig$30(TLRPC.TL_help_appConfig tL_help_appConfig) {
        if (tL_help_appConfig != null) {
            TLRPC.JSONValue jSONValue = tL_help_appConfig.config;
            if (jSONValue instanceof TLRPC.TL_jsonObject) {
                applyAppConfig((TLRPC.TL_jsonObject) jSONValue);
            }
        }
        AndroidUtilities.cancelRunOnUIThread(this.loadAppConfigRunnable);
        AndroidUtilities.runOnUIThread(this.loadAppConfigRunnable, 240010L);
    }

    public void lambda$loadAppConfig$31(final TLRPC.TL_help_appConfig tL_help_appConfig) {
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                MessagesController.this.lambda$loadAppConfig$30(tL_help_appConfig);
            }
        });
    }

    public void lambda$loadChannelAdmins$59(long j, TLObject tLObject, TLRPC.TL_error tL_error) {
        if (tLObject instanceof TLRPC.TL_channels_channelParticipants) {
            processLoadedAdminsResponse(j, (TLRPC.TL_channels_channelParticipants) tLObject);
        }
    }

    public void lambda$loadChannelParticipants$138(TLRPC.TL_error tL_error, TLObject tLObject, Long l, Utilities.Callback callback) {
        if (tL_error == null) {
            TLRPC.TL_channels_channelParticipants tL_channels_channelParticipants = (TLRPC.TL_channels_channelParticipants) tLObject;
            putUsers(tL_channels_channelParticipants.users, false);
            putChats(tL_channels_channelParticipants.chats, false);
            getMessagesStorage().putUsersAndChats(tL_channels_channelParticipants.users, tL_channels_channelParticipants.chats, true, true);
            getMessagesStorage().updateChannelUsers(l.longValue(), tL_channels_channelParticipants.participants);
            this.loadedFullParticipants.add(l);
        }
        this.loadingFullParticipants.remove(l);
        if (callback != null) {
            callback.run(tLObject instanceof TLRPC.TL_channels_channelParticipants ? (TLRPC.TL_channels_channelParticipants) tLObject : null);
        }
    }

    public void lambda$loadChannelParticipants$139(final Long l, final Utilities.Callback callback, final TLObject tLObject, final TLRPC.TL_error tL_error) {
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                MessagesController.this.lambda$loadChannelParticipants$138(tL_error, tLObject, l, callback);
            }
        });
    }

    public void lambda$loadCurrentState$302(TLObject tLObject, TLRPC.TL_error tL_error) {
        this.updatingState = false;
        if (tL_error != null) {
            if (tL_error.code != 401) {
                loadCurrentState();
                return;
            }
            return;
        }
        TLRPC.TL_updates_state tL_updates_state = (TLRPC.TL_updates_state) tLObject;
        getMessagesStorage().setLastDateValue(tL_updates_state.date);
        getMessagesStorage().setLastPtsValue(tL_updates_state.pts);
        getMessagesStorage().setLastSeqValue(tL_updates_state.seq);
        getMessagesStorage().setLastQtsValue(tL_updates_state.qts);
        for (int i = 0; i < 3; i++) {
            processUpdatesQueue(i, 2);
        }
        getMessagesStorage().saveDiffParams(getMessagesStorage().getLastSeqValue(), getMessagesStorage().getLastPtsValue(), getMessagesStorage().getLastDateValue(), getMessagesStorage().getLastQtsValue());
    }

    public void lambda$loadDialogs$187(int i, int i2, Runnable runnable, TLObject tLObject, TLRPC.TL_error tL_error) {
        if (tL_error == null) {
            TLRPC.messages_Dialogs messages_dialogs = (TLRPC.messages_Dialogs) tLObject;
            processLoadedDialogs(messages_dialogs, null, null, i, 0, i2, 0, false, false, false);
            if (runnable == null || !messages_dialogs.dialogs.isEmpty()) {
                return;
            }
            AndroidUtilities.runOnUIThread(runnable);
        }
    }

    public void lambda$loadFilterPeers$17(HashMap hashMap, TLRPC.messages_Dialogs messages_dialogs, TLRPC.messages_Dialogs messages_dialogs2, ArrayList arrayList, ArrayList arrayList2, ArrayList arrayList3, SparseArray sparseArray, ArrayList arrayList4, HashMap hashMap2, HashSet hashSet, Runnable runnable, HashMap hashMap3, HashMap hashMap4) {
        TLRPC.TL_users_getUsers tL_users_getUsers;
        int i;
        TLRPC.TL_messages_getPeerDialogs tL_messages_getPeerDialogs;
        TLRPC.TL_messages_getChats tL_messages_getChats;
        TLRPC.TL_channels_getChannels tL_channels_getChannels;
        ArrayList<TLObject> arrayList5 = new ArrayList<>();
        loop0: while (true) {
            tL_users_getUsers = null;
            for (Map.Entry entry : hashMap.entrySet()) {
                if (tL_users_getUsers == null) {
                    tL_users_getUsers = new TLRPC.TL_users_getUsers();
                    arrayList5.add(tL_users_getUsers);
                }
                tL_users_getUsers.id.add(getInputUser((TLRPC.InputPeer) entry.getValue()));
                if (tL_users_getUsers.id.size() == 100) {
                    break;
                }
            }
            sendLoadPeersRequest(tL_users_getUsers, arrayList5, messages_dialogs, messages_dialogs2, arrayList, arrayList2, arrayList3, sparseArray, arrayList4, hashMap2, hashSet, runnable);
        }
        if (tL_users_getUsers != null) {
            i = 100;
            sendLoadPeersRequest(tL_users_getUsers, arrayList5, messages_dialogs, messages_dialogs2, arrayList, arrayList2, arrayList3, sparseArray, arrayList4, hashMap2, hashSet, runnable);
        } else {
            i = 100;
        }
        TLRPC.TL_messages_getChats tL_messages_getChats2 = null;
        TLRPC.TL_channels_getChannels tL_channels_getChannels2 = null;
        for (Map.Entry entry2 : hashMap3.entrySet()) {
            TLRPC.InputPeer inputPeer = (TLRPC.InputPeer) entry2.getValue();
            if (inputPeer.chat_id != 0) {
                if (tL_messages_getChats2 == null) {
                    tL_messages_getChats = new TLRPC.TL_messages_getChats();
                    arrayList5.add(tL_messages_getChats);
                } else {
                    tL_messages_getChats = tL_messages_getChats2;
                }
                tL_messages_getChats.id.add((Long) entry2.getKey());
                if (tL_messages_getChats.id.size() == i) {
                    sendLoadPeersRequest(tL_messages_getChats, arrayList5, messages_dialogs, messages_dialogs2, arrayList, arrayList2, arrayList3, sparseArray, arrayList4, hashMap2, hashSet, runnable);
                    tL_messages_getChats2 = null;
                } else {
                    tL_messages_getChats2 = tL_messages_getChats;
                }
            } else if (inputPeer.channel_id != 0) {
                if (tL_channels_getChannels2 == null) {
                    TLRPC.TL_channels_getChannels tL_channels_getChannels3 = new TLRPC.TL_channels_getChannels();
                    arrayList5.add(tL_channels_getChannels3);
                    tL_channels_getChannels = tL_channels_getChannels3;
                } else {
                    tL_channels_getChannels = tL_channels_getChannels2;
                }
                tL_channels_getChannels.id.add(getInputChannel(inputPeer));
                if (tL_channels_getChannels.id.size() == i) {
                    sendLoadPeersRequest(tL_channels_getChannels, arrayList5, messages_dialogs, messages_dialogs2, arrayList, arrayList2, arrayList3, sparseArray, arrayList4, hashMap2, hashSet, runnable);
                    tL_channels_getChannels2 = null;
                } else {
                    tL_channels_getChannels2 = tL_channels_getChannels;
                }
            }
        }
        if (tL_messages_getChats2 != null) {
            sendLoadPeersRequest(tL_messages_getChats2, arrayList5, messages_dialogs, messages_dialogs2, arrayList, arrayList2, arrayList3, sparseArray, arrayList4, hashMap2, hashSet, runnable);
        }
        if (tL_channels_getChannels2 != null) {
            sendLoadPeersRequest(tL_channels_getChannels2, arrayList5, messages_dialogs, messages_dialogs2, arrayList, arrayList2, arrayList3, sparseArray, arrayList4, hashMap2, hashSet, runnable);
        }
        loop3: while (true) {
            tL_messages_getPeerDialogs = null;
            for (Map.Entry entry3 : hashMap4.entrySet()) {
                if (tL_messages_getPeerDialogs == null) {
                    tL_messages_getPeerDialogs = new TLRPC.TL_messages_getPeerDialogs();
                    arrayList5.add(tL_messages_getPeerDialogs);
                }
                TLRPC.TL_inputDialogPeer tL_inputDialogPeer = new TLRPC.TL_inputDialogPeer();
                tL_inputDialogPeer.peer = (TLRPC.InputPeer) entry3.getValue();
                tL_messages_getPeerDialogs.peers.add(tL_inputDialogPeer);
                if (tL_messages_getPeerDialogs.peers.size() == i) {
                    break;
                }
            }
            sendLoadPeersRequest(tL_messages_getPeerDialogs, arrayList5, messages_dialogs, messages_dialogs2, arrayList, arrayList2, arrayList3, sparseArray, arrayList4, hashMap2, hashSet, runnable);
        }
        if (tL_messages_getPeerDialogs != null) {
            sendLoadPeersRequest(tL_messages_getPeerDialogs, arrayList5, messages_dialogs, messages_dialogs2, arrayList, arrayList2, arrayList3, sparseArray, arrayList4, hashMap2, hashSet, runnable);
        }
    }

    public void lambda$loadFullChat$61(long j, TLRPC.TL_messages_chatFull tL_messages_chatFull, int i, long j2) {
        TLRPC.ChatFull chatFull = (TLRPC.ChatFull) this.fullChats.get(j);
        if (chatFull != null) {
            tL_messages_chatFull.full_chat.inviterId = chatFull.inviterId;
        }
        this.fullChats.put(j, tL_messages_chatFull.full_chat);
        long j3 = -j;
        getTranslateController().updateDialogFull(j3);
        applyDialogNotificationsSettings(j3, 0L, tL_messages_chatFull.full_chat.notify_settings);
        for (int i2 = 0; i2 < tL_messages_chatFull.full_chat.bot_info.size(); i2++) {
            getMediaDataController().putBotInfo(j3, tL_messages_chatFull.full_chat.bot_info.get(i2));
        }
        int indexOfKey = this.blockePeers.indexOfKey(j3);
        if (tL_messages_chatFull.full_chat.blocked) {
            if (indexOfKey < 0) {
                this.blockePeers.put(j3, 1);
                getNotificationCenter().lambda$postNotificationNameOnUIThread$1(NotificationCenter.blockedUsersDidLoad, new Object[0]);
            }
        } else if (indexOfKey >= 0) {
            this.blockePeers.removeAt(indexOfKey);
            getNotificationCenter().lambda$postNotificationNameOnUIThread$1(NotificationCenter.blockedUsersDidLoad, new Object[0]);
        }
        this.exportedChats.put(j, tL_messages_chatFull.full_chat.exported_invite);
        this.loadingFullChats.remove(Long.valueOf(j));
        this.loadedFullChats.put(j, System.currentTimeMillis());
        putUsers(tL_messages_chatFull.users, false);
        putChats(tL_messages_chatFull.chats, false);
        if (tL_messages_chatFull.full_chat.stickerset != null) {
            getMediaDataController().getGroupStickerSetById(tL_messages_chatFull.full_chat.stickerset);
        }
        if (tL_messages_chatFull.full_chat.emojiset != null) {
            getMediaDataController().getGroupStickerSetById(tL_messages_chatFull.full_chat.emojiset);
        }
        getNotificationCenter().lambda$postNotificationNameOnUIThread$1(NotificationCenter.chatInfoDidLoad, tL_messages_chatFull.full_chat, Integer.valueOf(i), Boolean.FALSE, Boolean.TRUE);
        TLRPC.Dialog dialog = (TLRPC.Dialog) this.dialogs_dict.get(j3);
        if (dialog != null) {
            TLRPC.ChatFull chatFull2 = tL_messages_chatFull.full_chat;
            if ((chatFull2.flags & 2048) != 0) {
                int i3 = dialog.folder_id;
                int i4 = chatFull2.folder_id;
                if (i3 != i4) {
                    dialog.folder_id = i4;
                    sortDialogs(null);
                    getNotificationCenter().lambda$postNotificationNameOnUIThread$1(NotificationCenter.dialogsNeedReload, new Object[0]);
                }
            }
            int i5 = dialog.ttl_period;
            int i6 = tL_messages_chatFull.full_chat.ttl_period;
            if (i5 != i6) {
                dialog.ttl_period = i6;
                getNotificationCenter().lambda$postNotificationNameOnUIThread$1(NotificationCenter.dialogsNeedReload, new Object[0]);
            }
            boolean z = dialog.view_forum_as_messages;
            boolean z2 = tL_messages_chatFull.full_chat.view_forum_as_messages;
            if (z != z2) {
                dialog.view_forum_as_messages = z2;
                getMessagesStorage().setDialogViewThreadAsMessages(j2, tL_messages_chatFull.full_chat.view_forum_as_messages);
            }
        }
    }

    public void lambda$loadFullChat$62(TLRPC.TL_error tL_error, long j) {
        checkChannelError(tL_error.text, j);
        this.loadingFullChats.remove(Long.valueOf(j));
    }

    public void lambda$loadFullChat$63(final long j, final long j2, TLRPC.Chat chat, final int i, TLObject tLObject, final TLRPC.TL_error tL_error) {
        if (tL_error != null) {
            AndroidUtilities.runOnUIThread(new Runnable() {
                @Override
                public final void run() {
                    MessagesController.this.lambda$loadFullChat$62(tL_error, j2);
                }
            });
            return;
        }
        final TLRPC.TL_messages_chatFull tL_messages_chatFull = (TLRPC.TL_messages_chatFull) tLObject;
        getMessagesStorage().putUsersAndChats(tL_messages_chatFull.users, tL_messages_chatFull.chats, true, true);
        getMessagesStorage().updateChatInfo(tL_messages_chatFull.full_chat, false);
        getStoriesController().updateStoriesFromFullPeer(j, tL_messages_chatFull.full_chat.stories);
        ChatThemeController.getInstance(this.currentAccount).saveChatWallpaper(-j2, tL_messages_chatFull.full_chat.wallpaper);
        if (ChatObject.isChannel(chat)) {
            Integer num = this.dialogs_read_inbox_max.get(Long.valueOf(j));
            if (num == null) {
                num = Integer.valueOf(getMessagesStorage().getDialogReadMax(false, j));
            }
            this.dialogs_read_inbox_max.put(Long.valueOf(j), Integer.valueOf(Math.max(tL_messages_chatFull.full_chat.read_inbox_max_id, num.intValue())));
            if (tL_messages_chatFull.full_chat.read_inbox_max_id > num.intValue()) {
                ArrayList<TLRPC.Update> arrayList = new ArrayList<>();
                TLRPC.TL_updateReadChannelInbox tL_updateReadChannelInbox = new TLRPC.TL_updateReadChannelInbox();
                tL_updateReadChannelInbox.channel_id = j2;
                TLRPC.ChatFull chatFull = tL_messages_chatFull.full_chat;
                tL_updateReadChannelInbox.max_id = chatFull.read_inbox_max_id;
                tL_updateReadChannelInbox.still_unread_count = chatFull.unread_count;
                arrayList.add(tL_updateReadChannelInbox);
                processUpdateArray(arrayList, null, null, false, 0);
            }
            Integer num2 = this.dialogs_read_outbox_max.get(Long.valueOf(j));
            if (num2 == null) {
                num2 = Integer.valueOf(getMessagesStorage().getDialogReadMax(true, j));
            }
            this.dialogs_read_outbox_max.put(Long.valueOf(j), Integer.valueOf(Math.max(tL_messages_chatFull.full_chat.read_outbox_max_id, num2.intValue())));
            if (tL_messages_chatFull.full_chat.read_outbox_max_id > num2.intValue()) {
                ArrayList<TLRPC.Update> arrayList2 = new ArrayList<>();
                TLRPC.TL_updateReadChannelOutbox tL_updateReadChannelOutbox = new TLRPC.TL_updateReadChannelOutbox();
                tL_updateReadChannelOutbox.channel_id = j2;
                tL_updateReadChannelOutbox.max_id = tL_messages_chatFull.full_chat.read_outbox_max_id;
                arrayList2.add(tL_updateReadChannelOutbox);
                processUpdateArray(arrayList2, null, null, false, 0);
            }
        }
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                MessagesController.this.lambda$loadFullChat$61(j2, tL_messages_chatFull, i, j);
            }
        });
    }

    public void lambda$loadFullUser$64(TLRPC.UserFull userFull, TLRPC.User user, int i) {
        savePeerSettings(userFull.user.id, userFull.settings, false);
        applyDialogNotificationsSettings(user.id, 0L, userFull.notify_settings);
        TL_bots.BotInfo botInfo = userFull.bot_info;
        if (botInfo instanceof TL_bots.TL_botInfo) {
            botInfo.user_id = user.id;
            getMediaDataController().putBotInfo(user.id, userFull.bot_info);
        }
        int indexOfKey = this.blockePeers.indexOfKey(user.id);
        if (userFull.blocked) {
            if (indexOfKey < 0) {
                this.blockePeers.put(user.id, 1);
                getNotificationCenter().lambda$postNotificationNameOnUIThread$1(NotificationCenter.blockedUsersDidLoad, new Object[0]);
            }
        } else if (indexOfKey >= 0) {
            this.blockePeers.removeAt(indexOfKey);
            getNotificationCenter().lambda$postNotificationNameOnUIThread$1(NotificationCenter.blockedUsersDidLoad, new Object[0]);
        }
        this.fullUsers.put(user.id, userFull);
        getTranslateController().updateDialogFull(user.id);
        StarsController.getInstance(this.currentAccount).invalidateProfileGifts(userFull);
        this.loadingFullUsers.remove(Long.valueOf(user.id));
        this.loadedFullUsers.put(user.id, System.currentTimeMillis());
        String str = user.first_name + user.last_name + UserObject.getPublicUsername(user);
        ArrayList<TLRPC.User> arrayList = new ArrayList<>();
        arrayList.add(userFull.user);
        putUsers(arrayList, false);
        getMessagesStorage().putUsersAndChats(arrayList, null, false, true);
        if (!str.equals(userFull.user.first_name + userFull.user.last_name + UserObject.getPublicUsername(userFull.user))) {
            getNotificationCenter().lambda$postNotificationNameOnUIThread$1(NotificationCenter.updateInterfaces, Integer.valueOf(UPDATE_MASK_NAME));
        }
        TLRPC.UserProfilePhoto userProfilePhoto = userFull.user.photo;
        if (userProfilePhoto != null && userProfilePhoto.has_video) {
            getNotificationCenter().lambda$postNotificationNameOnUIThread$1(NotificationCenter.updateInterfaces, Integer.valueOf(UPDATE_MASK_AVATAR));
        }
        TL_bots.BotInfo botInfo2 = userFull.bot_info;
        if (botInfo2 instanceof TL_bots.TL_botInfo) {
            botInfo2.user_id = userFull.id;
            getNotificationCenter().lambda$postNotificationNameOnUIThread$1(NotificationCenter.botInfoDidLoad, userFull.bot_info, Integer.valueOf(i));
        }
        getNotificationCenter().lambda$postNotificationNameOnUIThread$1(NotificationCenter.userInfoDidLoad, Long.valueOf(user.id), userFull);
        TLRPC.Dialog dialog = (TLRPC.Dialog) this.dialogs_dict.get(user.id);
        if (dialog != null) {
            if ((userFull.flags & 2048) != 0) {
                int i2 = dialog.folder_id;
                int i3 = userFull.folder_id;
                if (i2 != i3) {
                    dialog.folder_id = i3;
                    sortDialogs(null);
                    getNotificationCenter().lambda$postNotificationNameOnUIThread$1(NotificationCenter.dialogsNeedReload, new Object[0]);
                }
            }
            if ((userFull.flags & 16384) != 0) {
                int i4 = dialog.ttl_period;
                int i5 = userFull.ttl_period;
                if (i4 != i5) {
                    dialog.ttl_period = i5;
                    getNotificationCenter().lambda$postNotificationNameOnUIThread$1(NotificationCenter.dialogsNeedReload, new Object[0]);
                }
            }
        }
    }

    public void lambda$loadFullUser$65(TLRPC.User user) {
        this.loadingFullUsers.remove(Long.valueOf(user.id));
    }

    public void lambda$loadFullUser$66(long j, Utilities.Callback callback, final TLRPC.User user, final int i, TLObject tLObject, TLRPC.TL_error tL_error) {
        Runnable runnable;
        if (tL_error == null) {
            TLRPC.TL_users_userFull tL_users_userFull = (TLRPC.TL_users_userFull) tLObject;
            final TLRPC.UserFull userFull = tL_users_userFull.full_user;
            putUsers(tL_users_userFull.users, false);
            putChats(tL_users_userFull.chats, false);
            TLRPC.UserFull userFull2 = tL_users_userFull.full_user;
            userFull2.user = getUser(Long.valueOf(userFull2.id));
            getMessagesStorage().updateUserInfo(userFull, false);
            getStoriesController().updateStoriesFromFullPeer(j, userFull.stories);
            ChatThemeController chatThemeController = ChatThemeController.getInstance(this.currentAccount);
            TLRPC.UserFull userFull3 = tL_users_userFull.full_user;
            chatThemeController.saveChatWallpaper(userFull3.id, userFull3.wallpaper);
            if (callback != null) {
                callback.run(userFull);
            }
            runnable = new Runnable() {
                @Override
                public final void run() {
                    MessagesController.this.lambda$loadFullUser$64(userFull, user, i);
                }
            };
        } else {
            runnable = new Runnable() {
                @Override
                public final void run() {
                    MessagesController.this.lambda$loadFullUser$65(user);
                }
            };
        }
        AndroidUtilities.runOnUIThread(runnable);
    }

    public void lambda$loadGlobalNotificationsSettings$188(org.telegram.tgnet.TLObject r12, int r13) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MessagesController.lambda$loadGlobalNotificationsSettings$188(org.telegram.tgnet.TLObject, int):void");
    }

    public void lambda$loadGlobalNotificationsSettings$189(final int i, final TLObject tLObject, TLRPC.TL_error tL_error) {
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                MessagesController.this.lambda$loadGlobalNotificationsSettings$188(tLObject, i);
            }
        });
    }

    public void lambda$loadGlobalNotificationsSettings$190(TLObject tLObject) {
        this.loadingNotificationSettings--;
        if (tLObject instanceof TL_account.TL_reactionsNotifySettings) {
            TL_account.TL_reactionsNotifySettings tL_reactionsNotifySettings = (TL_account.TL_reactionsNotifySettings) tLObject;
            SharedPreferences.Editor edit = this.notificationsPreferences.edit();
            edit.putBoolean("EnableReactionsMessages", tL_reactionsNotifySettings.messages_notify_from != null);
            TL_account.ReactionNotificationsFrom reactionNotificationsFrom = tL_reactionsNotifySettings.messages_notify_from;
            if (reactionNotificationsFrom != null) {
                edit.putBoolean("EnableReactionsMessagesContacts", reactionNotificationsFrom instanceof TL_account.TL_reactionNotificationsFromContacts);
            }
            edit.putBoolean("EnableReactionsStories", tL_reactionsNotifySettings.stories_notify_from != null);
            TL_account.ReactionNotificationsFrom reactionNotificationsFrom2 = tL_reactionsNotifySettings.stories_notify_from;
            if (reactionNotificationsFrom2 != null) {
                edit.putBoolean("EnableReactionsStoriesContacts", reactionNotificationsFrom2 instanceof TL_account.TL_reactionNotificationsFromContacts);
            }
            edit.putBoolean("EnableReactionsPreview", tL_reactionsNotifySettings.show_previews);
            getNotificationsController().getNotificationsSettingsFacade().applySoundSettings(tL_reactionsNotifySettings.sound, edit, 0L, 0L, 4, false);
            edit.apply();
        }
        if (this.loadingNotificationSettings == 0) {
            getUserConfig().notificationsSettingsLoaded = true;
            getUserConfig().saveConfig(false);
        }
    }

    public void lambda$loadGlobalNotificationsSettings$191(final TLObject tLObject, TLRPC.TL_error tL_error) {
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                MessagesController.this.lambda$loadGlobalNotificationsSettings$190(tLObject);
            }
        });
    }

    public void lambda$loadHintDialogs$183(TLObject tLObject) {
        TLRPC.TL_help_recentMeUrls tL_help_recentMeUrls = (TLRPC.TL_help_recentMeUrls) tLObject;
        putUsers(tL_help_recentMeUrls.users, false);
        putChats(tL_help_recentMeUrls.chats, false);
        this.hintDialogs.clear();
        this.hintDialogs.addAll(tL_help_recentMeUrls.urls);
        getNotificationCenter().lambda$postNotificationNameOnUIThread$1(NotificationCenter.dialogsNeedReload, new Object[0]);
    }

    public void lambda$loadHintDialogs$184(final TLObject tLObject, TLRPC.TL_error tL_error) {
        if (tL_error == null) {
            AndroidUtilities.runOnUIThread(new Runnable() {
                @Override
                public final void run() {
                    MessagesController.this.lambda$loadHintDialogs$183(tLObject);
                }
            });
        }
    }

    public void lambda$loadMessagesInternal$165(long j, long j2, int i, int i2, int i3, int i4, int i5, int i6, int i7, int i8, int i9, int i10, long j3, int i11, boolean z, int i12, boolean z2, boolean z3, TLObject tLObject, TLRPC.TL_error tL_error) {
        if (tLObject != null) {
            TLRPC.messages_Messages messages_messages = (TLRPC.messages_Messages) tLObject;
            if (messages_messages instanceof TLRPC.TL_messages_messagesNotModified) {
                return;
            }
            processLoadedMessages(messages_messages, messages_messages.messages.size(), j, j2, i, i2, i3, false, i4, i5, i6, i7, i8, i9, false, i10, j3, i11, z, i12, z2, z3, null);
            return;
        }
        if (tL_error == null || !"SHORTCUT_INVALID".equals(tL_error.text)) {
            return;
        }
        processLoadedMessages(new TLRPC.TL_messages_messages(), 0, j, j2, i, i2, i3, false, i4, i5, i6, i7, i8, i9, false, i10, j3, i11, z, i12, z2, z3, null);
    }

    public void lambda$loadMessagesInternal$166(int i, TLRPC.TL_messages_getSavedHistory tL_messages_getSavedHistory, TLRPC.TL_error tL_error) {
        getNotificationCenter().lambda$postNotificationNameOnUIThread$1(NotificationCenter.loadingMessagesFailed, Integer.valueOf(i), tL_messages_getSavedHistory, tL_error);
    }

    public void lambda$loadMessagesInternal$167(long j, int i, int i2, int i3, long j2, final int i4, int i5, int i6, int i7, int i8, int i9, int i10, long j3, int i11, boolean z, int i12, boolean z2, boolean z3, final TLRPC.TL_messages_getSavedHistory tL_messages_getSavedHistory, TLObject tLObject, final TLRPC.TL_error tL_error) {
        int i13;
        if (tLObject == null) {
            AndroidUtilities.runOnUIThread(new Runnable() {
                @Override
                public final void run() {
                    MessagesController.this.lambda$loadMessagesInternal$166(i4, tL_messages_getSavedHistory, tL_error);
                }
            });
            return;
        }
        TLRPC.messages_Messages messages_messages = (TLRPC.messages_Messages) tLObject;
        removeDeletedMessagesFromArray(j, messages_messages.messages);
        if (messages_messages.messages.size() > i) {
            messages_messages.messages.remove(0);
        }
        if (i3 == 0 || messages_messages.messages.isEmpty()) {
            i13 = i2;
        } else {
            int i14 = messages_messages.messages.get(r0.size() - 1).id;
            int size = messages_messages.messages.size() - 1;
            while (true) {
                if (size < 0) {
                    break;
                }
                TLRPC.Message message = messages_messages.messages.get(size);
                if (message.date > i3) {
                    i14 = message.id;
                    break;
                }
                size--;
            }
            i13 = i14;
        }
        processLoadedMessages(messages_messages, messages_messages.messages.size(), j, j2, i, i13, i3, false, i4, i5, i6, i7, i8, i9, false, i10, j3, i11, z, i12, z2, z3, null);
    }

    public void lambda$loadMessagesInternal$168(int i, TLRPC.TL_messages_getReplies tL_messages_getReplies, TLRPC.TL_error tL_error) {
        getNotificationCenter().lambda$postNotificationNameOnUIThread$1(NotificationCenter.loadingMessagesFailed, Integer.valueOf(i), tL_messages_getReplies, tL_error);
    }

    public void lambda$loadMessagesInternal$169(int i, int i2, int i3, int i4, int i5, long j, long j2, final int i6, int i7, int i8, int i9, int i10, long j3, int i11, boolean z, int i12, boolean z2, boolean z3, final TLRPC.TL_messages_getReplies tL_messages_getReplies, TLObject tLObject, final TLRPC.TL_error tL_error) {
        int i13;
        int i14;
        if (tLObject == null) {
            AndroidUtilities.runOnUIThread(new Runnable() {
                @Override
                public final void run() {
                    MessagesController.this.lambda$loadMessagesInternal$168(i6, tL_messages_getReplies, tL_error);
                }
            });
            return;
        }
        TLRPC.messages_Messages messages_messages = (TLRPC.messages_Messages) tLObject;
        if (messages_messages.messages.size() > i) {
            messages_messages.messages.remove(0);
        }
        if (!messages_messages.messages.isEmpty()) {
            if (i3 != 0) {
                int i15 = messages_messages.messages.get(r0.size() - 1).id;
                int size = messages_messages.messages.size() - 1;
                while (true) {
                    if (size < 0) {
                        break;
                    }
                    TLRPC.Message message = messages_messages.messages.get(size);
                    if (message.date > i3) {
                        i15 = message.id;
                        break;
                    }
                    size--;
                }
                i13 = i15;
                i14 = 0;
                processLoadedMessages(messages_messages, messages_messages.messages.size(), j, j2, i, i13, i3, false, i6, i14, i7, i8, i9, i5, false, i10, j3, i11, z, i12, z2, z3, null);
            }
            if (i4 != 0) {
                if (i5 == 2 && i2 > 0) {
                    for (int size2 = messages_messages.messages.size() - 1; size2 >= 0; size2--) {
                        TLRPC.Message message2 = messages_messages.messages.get(size2);
                        int i16 = message2.id;
                        if (i16 > i4 && !message2.out) {
                            i13 = i2;
                            i14 = i16;
                            break;
                        }
                    }
                }
                i13 = i2;
                i14 = 0;
                processLoadedMessages(messages_messages, messages_messages.messages.size(), j, j2, i, i13, i3, false, i6, i14, i7, i8, i9, i5, false, i10, j3, i11, z, i12, z2, z3, null);
            }
        }
        i13 = i2;
        i14 = 0;
        processLoadedMessages(messages_messages, messages_messages.messages.size(), j, j2, i, i13, i3, false, i6, i14, i7, i8, i9, i5, false, i10, j3, i11, z, i12, z2, z3, null);
    }

    public void lambda$loadMessagesInternal$170(int i, int i2, long j, long j2, int i3, int i4, int i5, int i6, int i7, int i8, int i9, int i10, long j3, int i11, boolean z, int i12, boolean z2, boolean z3, TLObject tLObject, TLRPC.TL_error tL_error) {
        int i13;
        if (tLObject != null) {
            TLRPC.messages_Messages messages_messages = (TLRPC.messages_Messages) tLObject;
            if (messages_messages instanceof TLRPC.TL_messages_messagesNotModified) {
                return;
            }
            if (i2 == 0 || messages_messages.messages.isEmpty()) {
                i13 = i;
            } else {
                int i14 = messages_messages.messages.get(r0.size() - 1).id;
                int size = messages_messages.messages.size() - 1;
                while (true) {
                    if (size < 0) {
                        break;
                    }
                    TLRPC.Message message = messages_messages.messages.get(size);
                    if (message.date > i2) {
                        i14 = message.id;
                        break;
                    }
                    size--;
                }
                i13 = i14;
            }
            processLoadedMessages(messages_messages, messages_messages.messages.size(), j, j2, i3, i13, i2, false, i4, i5, i6, i7, i8, i9, false, i10, j3, i11, z, i12, z2, z3, null);
        }
    }

    public void lambda$loadMessagesInternal$171(int i, TLRPC.TL_messages_getPeerDialogs tL_messages_getPeerDialogs, TLRPC.TL_error tL_error) {
        getNotificationCenter().lambda$postNotificationNameOnUIThread$1(NotificationCenter.loadingMessagesFailed, Integer.valueOf(i), tL_messages_getPeerDialogs, tL_error);
    }

    public void lambda$loadMessagesInternal$172(long j, long j2, boolean z, int i, int i2, int i3, int i4, final int i5, int i6, long j3, int i7, int i8, int i9, boolean z2, boolean z3, boolean z4, final TLRPC.TL_messages_getPeerDialogs tL_messages_getPeerDialogs, TLObject tLObject, final TLRPC.TL_error tL_error) {
        if (tLObject == null) {
            AndroidUtilities.runOnUIThread(new Runnable() {
                @Override
                public final void run() {
                    MessagesController.this.lambda$loadMessagesInternal$171(i5, tL_messages_getPeerDialogs, tL_error);
                }
            });
            return;
        }
        TLRPC.TL_messages_peerDialogs tL_messages_peerDialogs = (TLRPC.TL_messages_peerDialogs) tLObject;
        if (!tL_messages_peerDialogs.dialogs.isEmpty()) {
            TLRPC.Dialog dialog = tL_messages_peerDialogs.dialogs.get(0);
            if (dialog.top_message != 0) {
                TLRPC.TL_messages_dialogs tL_messages_dialogs = new TLRPC.TL_messages_dialogs();
                tL_messages_dialogs.chats = tL_messages_peerDialogs.chats;
                tL_messages_dialogs.users = tL_messages_peerDialogs.users;
                tL_messages_dialogs.dialogs = tL_messages_peerDialogs.dialogs;
                tL_messages_dialogs.messages = tL_messages_peerDialogs.messages;
                getMessagesStorage().putDialogs(tL_messages_dialogs, 2);
            }
            loadMessagesInternal(j, j2, z, i, i2, i3, false, i4, i5, i6, dialog.top_message, 0, j3, i7, i8, dialog.unread_count, i9, z2, dialog.unread_mentions_count, false, z3, z4, null, 0L);
        }
    }

    public void lambda$loadMessagesInternal$173(int i, TLRPC.TL_messages_getHistory tL_messages_getHistory, TLRPC.TL_error tL_error) {
        getNotificationCenter().lambda$postNotificationNameOnUIThread$1(NotificationCenter.loadingMessagesFailed, Integer.valueOf(i), tL_messages_getHistory, tL_error);
    }

    public void lambda$loadMessagesInternal$174(long j, int i, int i2, int i3, long j2, final int i4, int i5, int i6, int i7, int i8, int i9, int i10, long j3, int i11, boolean z, int i12, boolean z2, boolean z3, final TLRPC.TL_messages_getHistory tL_messages_getHistory, TLObject tLObject, final TLRPC.TL_error tL_error) {
        int i13;
        if (tLObject == null) {
            AndroidUtilities.runOnUIThread(new Runnable() {
                @Override
                public final void run() {
                    MessagesController.this.lambda$loadMessagesInternal$173(i4, tL_messages_getHistory, tL_error);
                }
            });
            return;
        }
        TLRPC.messages_Messages messages_messages = (TLRPC.messages_Messages) tLObject;
        removeDeletedMessagesFromArray(j, messages_messages.messages);
        if (messages_messages.messages.size() > i) {
            messages_messages.messages.remove(0);
        }
        if (i3 == 0 || messages_messages.messages.isEmpty()) {
            i13 = i2;
        } else {
            int i14 = messages_messages.messages.get(r0.size() - 1).id;
            int size = messages_messages.messages.size() - 1;
            while (true) {
                if (size < 0) {
                    break;
                }
                TLRPC.Message message = messages_messages.messages.get(size);
                if (message.date > i3) {
                    i14 = message.id;
                    break;
                }
                size--;
            }
            i13 = i14;
        }
        processLoadedMessages(messages_messages, messages_messages.messages.size(), j, j2, i, i13, i3, false, i4, i5, i6, i7, i8, i9, false, i10, j3, i11, z, i12, z2, z3, null);
    }

    public void lambda$loadPeerSettings$73(long j, TLObject tLObject) {
        this.loadingPeerSettings.remove(j);
        if (tLObject != null) {
            TLRPC.TL_messages_peerSettings tL_messages_peerSettings = (TLRPC.TL_messages_peerSettings) tLObject;
            TLRPC.PeerSettings peerSettings = tL_messages_peerSettings.settings;
            putUsers(tL_messages_peerSettings.users, false);
            putChats(tL_messages_peerSettings.chats, false);
            savePeerSettings(j, peerSettings, false);
        }
    }

    public void lambda$loadPeerSettings$74(final long j, final TLObject tLObject, TLRPC.TL_error tL_error) {
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                MessagesController.this.lambda$loadPeerSettings$73(j, tLObject);
            }
        });
    }

    public void lambda$loadPinnedDialogs$342(int i, ArrayList arrayList, boolean z, TLRPC.TL_messages_peerDialogs tL_messages_peerDialogs, LongSparseArray longSparseArray, TLRPC.TL_messages_dialogs tL_messages_dialogs) {
        boolean z2;
        boolean z3;
        boolean z4;
        ArrayList<Integer> arrayList2;
        ArrayList arrayList3;
        this.loadingPinnedDialogs.delete(i);
        applyDialogsNotificationsSettings(arrayList);
        ArrayList<TLRPC.Dialog> dialogs = getDialogs(i);
        int i2 = z ? 1 : 0;
        int i3 = 0;
        int i4 = 0;
        boolean z5 = false;
        while (true) {
            z2 = true;
            if (i3 >= dialogs.size()) {
                break;
            }
            TLRPC.Dialog dialog = dialogs.get(i3);
            if (!(dialog instanceof TLRPC.TL_dialogFolder)) {
                if (!DialogObject.isEncryptedDialog(dialog.id)) {
                    if (!dialog.pinned) {
                        if (dialog.id != this.promoDialogId) {
                            break;
                        }
                    } else {
                        i4 = Math.max(dialog.pinnedNum, i4);
                        dialog.pinned = false;
                        dialog.pinnedNum = 0;
                        i2++;
                        z5 = true;
                    }
                } else {
                    if (i2 < arrayList.size()) {
                        arrayList.add(i2, dialog);
                    } else {
                        arrayList.add(dialog);
                    }
                    i2++;
                }
            }
            i3++;
        }
        ArrayList<Long> arrayList4 = new ArrayList<>();
        if (arrayList.isEmpty()) {
            z3 = false;
        } else {
            putUsers(tL_messages_peerDialogs.users, false);
            putChats(tL_messages_peerDialogs.chats, false);
            ArrayList<Long> arrayList5 = new ArrayList<>();
            ArrayList<Integer> arrayList6 = new ArrayList<>();
            int size = arrayList.size();
            int i5 = 0;
            z3 = false;
            while (i5 < size) {
                TLRPC.Dialog dialog2 = (TLRPC.Dialog) arrayList.get(i5);
                dialog2.pinnedNum = (size - i5) + i4;
                arrayList4.add(Long.valueOf(dialog2.id));
                TLRPC.Dialog dialog3 = (TLRPC.Dialog) this.dialogs_dict.get(dialog2.id);
                if (dialog3 != null) {
                    dialog3.pinned = z2;
                    dialog3.pinnedNum = dialog2.pinnedNum;
                    arrayList5.add(Long.valueOf(dialog2.id));
                    arrayList6.add(Integer.valueOf(dialog2.pinnedNum));
                    arrayList2 = arrayList6;
                } else {
                    this.dialogs_dict.put(dialog2.id, dialog2);
                    ArrayList arrayList7 = (ArrayList) longSparseArray.get(dialog2.id);
                    arrayList2 = arrayList6;
                    this.dialogMessage.put(dialog2.id, arrayList7);
                    if (arrayList7 != null) {
                        int i6 = 0;
                        while (i6 < arrayList7.size()) {
                            MessageObject messageObject = (MessageObject) arrayList7.get(i6);
                            if (messageObject != null) {
                                arrayList3 = arrayList7;
                                if (messageObject.messageOwner.peer_id.channel_id == 0) {
                                    this.dialogMessagesByIds.put(messageObject.getId(), messageObject);
                                    this.dialogsLoadedTillDate = Math.min(this.dialogsLoadedTillDate, messageObject.messageOwner.date);
                                    long j = messageObject.messageOwner.random_id;
                                    if (j != 0) {
                                        this.dialogMessagesByRandomIds.put(j, messageObject);
                                    }
                                }
                            } else {
                                arrayList3 = arrayList7;
                            }
                            i6++;
                            arrayList7 = arrayList3;
                        }
                    }
                    getTranslateController().checkDialogMessage(dialog2.id);
                    z3 = true;
                }
                i5++;
                arrayList6 = arrayList2;
                z5 = true;
                z2 = true;
            }
            getMessagesStorage().setDialogsPinned(arrayList5, arrayList6);
        }
        if (z5) {
            if (z3) {
                this.allDialogs.clear();
                int size2 = this.dialogs_dict.size();
                for (int i7 = 0; i7 < size2; i7++) {
                    TLRPC.Dialog dialog4 = (TLRPC.Dialog) this.dialogs_dict.valueAt(i7);
                    if (this.deletingDialogs.indexOfKey(dialog4.id) < 0) {
                        this.allDialogs.add(dialog4);
                    }
                }
            }
            sortDialogs(null);
            z4 = false;
            getNotificationCenter().lambda$postNotificationNameOnUIThread$1(NotificationCenter.dialogsNeedReload, new Object[0]);
        } else {
            z4 = false;
        }
        getMessagesStorage().unpinAllDialogsExceptNew(arrayList4, i);
        getMessagesStorage().putDialogs(tL_messages_dialogs, 1);
        getUserConfig().setPinnedDialogsLoaded(i, true);
        getUserConfig().saveConfig(z4);
    }

    public void lambda$loadPinnedDialogs$343(final int i, final ArrayList arrayList, final boolean z, final TLRPC.TL_messages_peerDialogs tL_messages_peerDialogs, final LongSparseArray longSparseArray, final TLRPC.TL_messages_dialogs tL_messages_dialogs) {
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                MessagesController.this.lambda$loadPinnedDialogs$342(i, arrayList, z, tL_messages_peerDialogs, longSparseArray, tL_messages_dialogs);
            }
        });
    }

    public void lambda$loadPinnedDialogs$344(final int r19, org.telegram.tgnet.TLObject r20, org.telegram.tgnet.TLRPC.TL_error r21) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MessagesController.lambda$loadPinnedDialogs$344(int, org.telegram.tgnet.TLObject, org.telegram.tgnet.TLRPC$TL_error):void");
    }

    public void lambda$loadRemoteFilters$23() {
        Utilities.Callback<Boolean> callback = this.onLoadedRemoteFilters;
        if (callback != null) {
            callback.run(Boolean.TRUE);
            this.onLoadedRemoteFilters = null;
        }
    }

    public void lambda$loadRemoteFilters$24() {
        getNotificationCenter().lambda$postNotificationNameOnUIThread$1(NotificationCenter.dialogFiltersUpdated, new Object[0]);
    }

    public void lambda$loadRemoteFilters$25() {
        Utilities.Callback<Boolean> callback = this.onLoadedRemoteFilters;
        if (callback != null) {
            callback.run(Boolean.TRUE);
            this.onLoadedRemoteFilters = null;
        }
    }

    public void lambda$loadRemoteFilters$26() {
        this.loadingRemoteFilters = false;
        Utilities.Callback<Boolean> callback = this.onLoadedRemoteFilters;
        if (callback != null) {
            callback.run(Boolean.FALSE);
            this.onLoadedRemoteFilters = null;
        }
    }

    public void lambda$loadRemoteFilters$27(TLObject tLObject, TLRPC.TL_error tL_error) {
        if (tLObject instanceof Vector) {
            ArrayList<TLRPC.DialogFilter> arrayList = new ArrayList<>();
            Vector vector = (Vector) tLObject;
            for (int i = 0; i < vector.objects.size(); i++) {
                arrayList.add((TLRPC.DialogFilter) vector.objects.get(i));
            }
            getMessagesStorage().checkLoadedRemoteFilters(arrayList, new Runnable() {
                @Override
                public final void run() {
                    MessagesController.this.lambda$loadRemoteFilters$23();
                }
            });
            return;
        }
        if (!(tLObject instanceof TLRPC.TL_messages_dialogFilters)) {
            AndroidUtilities.runOnUIThread(new Runnable() {
                @Override
                public final void run() {
                    MessagesController.this.lambda$loadRemoteFilters$26();
                }
            });
            return;
        }
        TLRPC.TL_messages_dialogFilters tL_messages_dialogFilters = (TLRPC.TL_messages_dialogFilters) tLObject;
        boolean z = this.folderTags;
        boolean z2 = tL_messages_dialogFilters.tags_enabled;
        if (z != z2) {
            setFolderTags(z2);
            AndroidUtilities.runOnUIThread(new Runnable() {
                @Override
                public final void run() {
                    MessagesController.this.lambda$loadRemoteFilters$24();
                }
            });
        }
        getMessagesStorage().checkLoadedRemoteFilters(tL_messages_dialogFilters.filters, new Runnable() {
            @Override
            public final void run() {
                MessagesController.this.lambda$loadRemoteFilters$25();
            }
        });
    }

    public void lambda$loadSignUpNotificationsSettings$194(TLObject tLObject) {
        this.loadingNotificationSignUpSettings = false;
        SharedPreferences.Editor edit = this.notificationsPreferences.edit();
        boolean z = tLObject instanceof TLRPC.TL_boolFalse;
        this.enableJoined = z;
        edit.putBoolean("EnableContactJoined", z);
        edit.commit();
        getUserConfig().notificationsSignUpSettingsLoaded = true;
        getUserConfig().saveConfig(false);
    }

    public void lambda$loadSignUpNotificationsSettings$195(final TLObject tLObject, TLRPC.TL_error tL_error) {
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                MessagesController.this.lambda$loadSignUpNotificationsSettings$194(tLObject);
            }
        });
    }

    public void lambda$loadSuggestedFilters$21(TLObject tLObject) {
        this.loadingSuggestedFilters = false;
        this.suggestedFilters.clear();
        if (tLObject instanceof Vector) {
            this.suggestedFilters.addAll(((Vector) tLObject).objects);
        }
        getNotificationCenter().lambda$postNotificationNameOnUIThread$1(NotificationCenter.suggestedFiltersLoaded, new Object[0]);
    }

    public void lambda$loadSuggestedFilters$22(final TLObject tLObject, TLRPC.TL_error tL_error) {
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                MessagesController.this.lambda$loadSuggestedFilters$21(tLObject);
            }
        });
    }

    public void lambda$loadUnknownChannel$307(long j, TLRPC.Chat chat, TLObject tLObject, TLRPC.TL_error tL_error) {
        if (tLObject != null) {
            TLRPC.TL_messages_peerDialogs tL_messages_peerDialogs = (TLRPC.TL_messages_peerDialogs) tLObject;
            if (!tL_messages_peerDialogs.dialogs.isEmpty() && !tL_messages_peerDialogs.chats.isEmpty()) {
                TLRPC.TL_dialog tL_dialog = (TLRPC.TL_dialog) tL_messages_peerDialogs.dialogs.get(0);
                TLRPC.TL_messages_dialogs tL_messages_dialogs = new TLRPC.TL_messages_dialogs();
                tL_messages_dialogs.dialogs.addAll(tL_messages_peerDialogs.dialogs);
                tL_messages_dialogs.messages.addAll(tL_messages_peerDialogs.messages);
                tL_messages_dialogs.users.addAll(tL_messages_peerDialogs.users);
                tL_messages_dialogs.chats.addAll(tL_messages_peerDialogs.chats);
                processLoadedDialogs(tL_messages_dialogs, null, null, tL_dialog.folder_id, 0, 1, this.DIALOGS_LOAD_TYPE_CHANNEL, false, false, false);
            }
        }
        if (j != 0) {
            getMessagesStorage().removePendingTask(j);
        }
        this.gettingUnknownChannels.delete(chat.id);
    }

    public void lambda$loadUnknownDialog$196(long j, long j2, TLObject tLObject, TLRPC.TL_error tL_error) {
        if (tLObject != null) {
            TLRPC.TL_messages_peerDialogs tL_messages_peerDialogs = (TLRPC.TL_messages_peerDialogs) tLObject;
            if (!tL_messages_peerDialogs.dialogs.isEmpty()) {
                TLRPC.TL_dialog tL_dialog = (TLRPC.TL_dialog) tL_messages_peerDialogs.dialogs.get(0);
                TLRPC.TL_messages_dialogs tL_messages_dialogs = new TLRPC.TL_messages_dialogs();
                tL_messages_dialogs.dialogs.addAll(tL_messages_peerDialogs.dialogs);
                tL_messages_dialogs.messages.addAll(tL_messages_peerDialogs.messages);
                tL_messages_dialogs.users.addAll(tL_messages_peerDialogs.users);
                tL_messages_dialogs.chats.addAll(tL_messages_peerDialogs.chats);
                processLoadedDialogs(tL_messages_dialogs, null, null, tL_dialog.folder_id, 0, 1, this.DIALOGS_LOAD_TYPE_UNKNOWN, false, false, false);
            }
        }
        if (j != 0) {
            getMessagesStorage().removePendingTask(j);
        }
        this.gettingUnknownDialogs.delete(j2);
    }

    public void lambda$loadUnreadDialogs$338(TLObject tLObject) {
        if (tLObject instanceof Vector) {
            Vector vector = (Vector) tLObject;
            int size = vector.objects.size();
            for (int i = 0; i < size; i++) {
                TLRPC.DialogPeer dialogPeer = (TLRPC.DialogPeer) vector.objects.get(i);
                if (dialogPeer instanceof TLRPC.TL_dialogPeer) {
                    TLRPC.Peer peer = ((TLRPC.TL_dialogPeer) dialogPeer).peer;
                    long j = peer.user_id;
                    if (j == 0) {
                        long j2 = peer.chat_id;
                        j = j2 != 0 ? -j2 : -peer.channel_id;
                    }
                    getMessagesStorage().setDialogUnread(j, true);
                    TLRPC.Dialog dialog = (TLRPC.Dialog) this.dialogs_dict.get(j);
                    if (dialog != null && !dialog.unread_mark) {
                        dialog.unread_mark = true;
                        if (dialog.unread_count == 0 && !isDialogMuted(j, 0L)) {
                            this.unreadUnmutedDialogs++;
                        }
                    }
                }
            }
            getUserConfig().unreadDialogsLoaded = true;
            getUserConfig().saveConfig(false);
            getNotificationCenter().lambda$postNotificationNameOnUIThread$1(NotificationCenter.updateInterfaces, Integer.valueOf(UPDATE_MASK_READ_DIALOG_MESSAGE));
            this.loadingUnreadDialogs = false;
        }
    }

    public void lambda$loadUnreadDialogs$339(final TLObject tLObject, TLRPC.TL_error tL_error) {
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                MessagesController.this.lambda$loadUnreadDialogs$338(tLObject);
            }
        });
    }

    public static void lambda$loadUsersPremiumLocked$272(TLObject tLObject, ArrayList arrayList, Utilities.Callback callback) {
        ArrayList arrayList2 = new ArrayList();
        if (tLObject instanceof Vector) {
            Vector vector = (Vector) tLObject;
            for (int i = 0; i < vector.objects.size(); i++) {
                if (i < arrayList.size() && (vector.objects.get(i) instanceof TLRPC.TL_boolTrue)) {
                    arrayList2.add(Long.valueOf(((TLRPC.User) arrayList.get(i)).id));
                }
            }
        }
        callback.run(arrayList2);
    }

    public static void lambda$loadUsersPremiumLocked$273(final ArrayList arrayList, final Utilities.Callback callback, final TLObject tLObject, TLRPC.TL_error tL_error) {
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                MessagesController.lambda$loadUsersPremiumLocked$272(TLObject.this, arrayList, callback);
            }
        });
    }

    public static void lambda$logDeviceStats$28(TLObject tLObject, TLRPC.TL_error tL_error) {
    }

    public void lambda$markAllTopicsAsRead$6() {
        getMessagesController().sortDialogs(null);
        getNotificationCenter().lambda$postNotificationNameOnUIThread$1(NotificationCenter.dialogsNeedReload, Boolean.TRUE);
    }

    public void lambda$markAllTopicsAsRead$7() {
        getMessagesStorage().resetAllUnreadCounters(false);
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                MessagesController.this.lambda$markAllTopicsAsRead$6();
            }
        });
    }

    public void lambda$markAllTopicsAsRead$8(ArrayList arrayList, long j) {
        if (arrayList != null) {
            for (int i = 0; i < arrayList.size(); i++) {
                TLRPC.TL_forumTopic tL_forumTopic = (TLRPC.TL_forumTopic) arrayList.get(i);
                MessagesController messagesController = getMessagesController();
                int i2 = tL_forumTopic.top_message;
                TLRPC.Message message = tL_forumTopic.topMessage;
                messagesController.markDialogAsRead(j, i2, 0, message != null ? message.date : 0, false, tL_forumTopic.id, 0, true, 0);
                getMessagesStorage().updateRepliesMaxReadId(-j, tL_forumTopic.id, tL_forumTopic.top_message, 0, true);
            }
        }
        getMessagesStorage().getStorageQueue().postRunnable(new Runnable() {
            @Override
            public final void run() {
                MessagesController.this.lambda$markAllTopicsAsRead$7();
            }
        });
    }

    public void lambda$markAllTopicsAsRead$9(final long j, final ArrayList arrayList) {
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                MessagesController.this.lambda$markAllTopicsAsRead$8(arrayList, j);
            }
        });
    }

    public void lambda$markDialogAsRead$230(long j, int i, int i2, boolean z) {
        LongSparseIntArray longSparseIntArray;
        int i3;
        TLRPC.Dialog dialog = (TLRPC.Dialog) this.dialogs_dict.get(j);
        if (dialog != null) {
            int i4 = dialog.unread_count;
            if (i == 0 || i2 >= dialog.top_message) {
                dialog.unread_count = 0;
            } else {
                int max = Math.max(i4 - i, 0);
                dialog.unread_count = max;
                if (i2 != Integer.MIN_VALUE && max > (i3 = dialog.top_message - i2)) {
                    dialog.unread_count = i3;
                }
            }
            boolean z2 = dialog.unread_mark;
            if (z2) {
                dialog.unread_mark = false;
                getMessagesStorage().setDialogUnread(dialog.id, false);
            }
            if ((i4 != 0 || z2) && dialog.unread_count == 0) {
                if (!isDialogMuted(j, 0L)) {
                    this.unreadUnmutedDialogs--;
                }
                int i5 = 0;
                while (true) {
                    DialogFilter[] dialogFilterArr = this.selectedDialogFilter;
                    if (i5 < dialogFilterArr.length) {
                        DialogFilter dialogFilter = dialogFilterArr[i5];
                        if (dialogFilter != null && (dialogFilter.flags & DIALOG_FILTER_FLAG_EXCLUDE_READ) != 0) {
                            sortDialogs(null);
                            getNotificationCenter().lambda$postNotificationNameOnUIThread$1(NotificationCenter.dialogsNeedReload, new Object[0]);
                            break;
                        }
                        i5++;
                    } else {
                        break;
                    }
                }
            }
            getNotificationCenter().lambda$postNotificationNameOnUIThread$1(NotificationCenter.updateInterfaces, Integer.valueOf(UPDATE_MASK_READ_DIALOG_MESSAGE));
        }
        NotificationsController notificationsController = getNotificationsController();
        if (z) {
            notificationsController.processReadMessages(null, j, 0, i2, true);
            longSparseIntArray = new LongSparseIntArray(1);
            longSparseIntArray.put(j, -1);
        } else {
            notificationsController.processReadMessages(null, j, 0, i2, false);
            longSparseIntArray = new LongSparseIntArray(1);
            longSparseIntArray.put(j, 0);
        }
        getNotificationsController().processDialogsUpdateRead(longSparseIntArray);
    }

    public void lambda$markDialogAsRead$231(final long j, final int i, final int i2, final boolean z) {
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                MessagesController.this.lambda$markDialogAsRead$230(j, i, i2, z);
            }
        });
    }

    public void lambda$markDialogAsRead$232(long j, int i, boolean z, int i2, int i3) {
        int i4;
        getNotificationsController().processReadMessages(null, j, i, 0, z);
        TLRPC.Dialog dialog = (TLRPC.Dialog) this.dialogs_dict.get(j);
        if (dialog != null) {
            int i5 = dialog.unread_count;
            if (i2 == 0 || i3 <= dialog.top_message) {
                dialog.unread_count = 0;
            } else {
                int max = Math.max(i5 - i2, 0);
                dialog.unread_count = max;
                if (i3 != Integer.MAX_VALUE && max > (i4 = i3 - dialog.top_message)) {
                    dialog.unread_count = i4;
                }
            }
            boolean z2 = dialog.unread_mark;
            if (z2) {
                dialog.unread_mark = false;
                getMessagesStorage().setDialogUnread(dialog.id, false);
            }
            if ((i5 != 0 || z2) && dialog.unread_count == 0) {
                if (!isDialogMuted(j, 0L)) {
                    this.unreadUnmutedDialogs--;
                }
                int i6 = 0;
                while (true) {
                    DialogFilter[] dialogFilterArr = this.selectedDialogFilter;
                    if (i6 < dialogFilterArr.length) {
                        DialogFilter dialogFilter = dialogFilterArr[i6];
                        if (dialogFilter != null && (dialogFilter.flags & DIALOG_FILTER_FLAG_EXCLUDE_READ) != 0) {
                            sortDialogs(null);
                            getNotificationCenter().lambda$postNotificationNameOnUIThread$1(NotificationCenter.dialogsNeedReload, new Object[0]);
                            break;
                        }
                        i6++;
                    } else {
                        break;
                    }
                }
            }
            getNotificationCenter().lambda$postNotificationNameOnUIThread$1(NotificationCenter.updateInterfaces, Integer.valueOf(UPDATE_MASK_READ_DIALOG_MESSAGE));
        }
        LongSparseIntArray longSparseIntArray = new LongSparseIntArray(1);
        longSparseIntArray.put(j, 0);
        getNotificationsController().processDialogsUpdateRead(longSparseIntArray);
    }

    public void lambda$markDialogAsRead$233(final long j, final int i, final boolean z, final int i2, final int i3) {
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                MessagesController.this.lambda$markDialogAsRead$232(j, i, z, i2, i3);
            }
        });
    }

    public void lambda$markDialogAsRead$234(long j, long j2, boolean z, int i, int i2) {
        Object obj;
        ArrayList<ReadTask> arrayList;
        if (j != 0) {
            obj = this.threadsReadTasksMap.get(j2 + "_" + j);
        } else {
            obj = this.readTasksMap.get(j2);
        }
        ReadTask readTask = (ReadTask) obj;
        if (readTask == null) {
            readTask = new ReadTask(null);
            readTask.dialogId = j2;
            readTask.replyId = j;
            readTask.sendRequestTime = SystemClock.elapsedRealtime() + 5000;
            if (!z) {
                if (j != 0) {
                    this.threadsReadTasksMap.put(j2 + "_" + j, readTask);
                    arrayList = this.repliesReadTasks;
                } else {
                    this.readTasksMap.put(j2, readTask);
                    arrayList = this.readTasks;
                }
                arrayList.add(readTask);
            }
        }
        readTask.maxDate = i;
        readTask.maxId = i2;
        if (z) {
            completeReadTask(readTask);
        }
    }

    public void lambda$markDialogAsReadNow$228(long j, long j2) {
        if (j == 0) {
            ReadTask readTask = (ReadTask) this.readTasksMap.get(j2);
            if (readTask == null) {
                return;
            }
            completeReadTask(readTask);
            this.readTasks.remove(readTask);
            this.readTasksMap.remove(j2);
            return;
        }
        String str = j2 + "_" + j;
        ReadTask readTask2 = this.threadsReadTasksMap.get(str);
        if (readTask2 == null) {
            return;
        }
        completeReadTask(readTask2);
        this.repliesReadTasks.remove(readTask2);
        this.threadsReadTasksMap.remove(str);
    }

    public void lambda$markDialogAsUnread$337(long j, TLObject tLObject, TLRPC.TL_error tL_error) {
        if (j != 0) {
            getMessagesStorage().removePendingTask(j);
        }
    }

    public static void lambda$markMentionMessageAsRead$221(TLObject tLObject, TLRPC.TL_error tL_error) {
    }

    public void lambda$markMentionMessageAsRead$222(TLObject tLObject, TLRPC.TL_error tL_error) {
        if (tL_error == null) {
            TLRPC.TL_messages_affectedMessages tL_messages_affectedMessages = (TLRPC.TL_messages_affectedMessages) tLObject;
            processNewDifferenceParams(-1, tL_messages_affectedMessages.pts, -1, tL_messages_affectedMessages.pts_count);
        }
    }

    public static void lambda$markMentionsAsRead$229(TLObject tLObject, TLRPC.TL_error tL_error) {
    }

    public void lambda$markMessageAsRead2$223(long j, TLObject tLObject, TLRPC.TL_error tL_error) {
        if (j != 0) {
            getMessagesStorage().removePendingTask(j);
        }
    }

    public void lambda$markMessageAsRead2$224(long j, TLObject tLObject, TLRPC.TL_error tL_error) {
        if (tL_error == null) {
            TLRPC.TL_messages_affectedMessages tL_messages_affectedMessages = (TLRPC.TL_messages_affectedMessages) tLObject;
            processNewDifferenceParams(-1, tL_messages_affectedMessages.pts, -1, tL_messages_affectedMessages.pts_count);
        }
        if (j != 0) {
            getMessagesStorage().removePendingTask(j);
        }
    }

    public static void lambda$markMessageContentAsRead$219(TLObject tLObject, TLRPC.TL_error tL_error) {
    }

    public void lambda$markMessageContentAsRead$220(TLObject tLObject, TLRPC.TL_error tL_error) {
        if (tL_error == null) {
            TLRPC.TL_messages_affectedMessages tL_messages_affectedMessages = (TLRPC.TL_messages_affectedMessages) tLObject;
            processNewDifferenceParams(-1, tL_messages_affectedMessages.pts, -1, tL_messages_affectedMessages.pts_count);
        }
    }

    public static void lambda$markReactionsAsRead$390(TLObject tLObject, TLRPC.TL_error tL_error) {
    }

    public void lambda$migrateDialogs$201() {
        this.migratingDialogs = false;
    }

    public void lambda$migrateDialogs$202(org.telegram.tgnet.TLRPC.messages_Dialogs r38, int r39) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MessagesController.lambda$migrateDialogs$202(org.telegram.tgnet.TLRPC$messages_Dialogs, int):void");
    }

    public void lambda$migrateDialogs$203() {
        this.migratingDialogs = false;
    }

    public void lambda$migrateDialogs$204(final int i, TLObject tLObject, TLRPC.TL_error tL_error) {
        if (tL_error != null) {
            AndroidUtilities.runOnUIThread(new Runnable() {
                @Override
                public final void run() {
                    MessagesController.this.lambda$migrateDialogs$203();
                }
            });
        } else {
            final TLRPC.messages_Dialogs messages_dialogs = (TLRPC.messages_Dialogs) tLObject;
            getMessagesStorage().getStorageQueue().postRunnable(new Runnable() {
                @Override
                public final void run() {
                    MessagesController.this.lambda$migrateDialogs$202(messages_dialogs, i);
                }
            });
        }
    }

    public void lambda$new$0() {
        getUserConfig().checkSavedPassword();
    }

    public int lambda$new$10(TLRPC.Dialog dialog, TLRPC.Dialog dialog2) {
        DialogFilter dialogFilter = this.sortingDialogFilter;
        int i = dialogFilter == null ? Integer.MIN_VALUE : dialogFilter.pinnedDialogs.get(dialog.id, Integer.MIN_VALUE);
        DialogFilter dialogFilter2 = this.sortingDialogFilter;
        int i2 = dialogFilter2 == null ? Integer.MIN_VALUE : dialogFilter2.pinnedDialogs.get(dialog2.id, Integer.MIN_VALUE);
        boolean z = dialog instanceof TLRPC.TL_dialogFolder;
        if (z && !(dialog2 instanceof TLRPC.TL_dialogFolder)) {
            return -1;
        }
        if (!z && (dialog2 instanceof TLRPC.TL_dialogFolder)) {
            return 1;
        }
        if (i == Integer.MIN_VALUE && i2 != Integer.MIN_VALUE) {
            return 1;
        }
        if (i != Integer.MIN_VALUE && i2 == Integer.MIN_VALUE) {
            return -1;
        }
        if (i != Integer.MIN_VALUE) {
            if (i > i2) {
                return 1;
            }
            return i < i2 ? -1 : 0;
        }
        MediaDataController mediaDataController = getMediaDataController();
        long lastMessageOrDraftDate = DialogObject.getLastMessageOrDraftDate(dialog, mediaDataController.getDraft(dialog.id, 0L));
        long lastMessageOrDraftDate2 = DialogObject.getLastMessageOrDraftDate(dialog2, mediaDataController.getDraft(dialog2.id, 0L));
        if (lastMessageOrDraftDate < lastMessageOrDraftDate2) {
            return 1;
        }
        return lastMessageOrDraftDate > lastMessageOrDraftDate2 ? -1 : 0;
    }

    public int lambda$new$11(TLRPC.Dialog dialog, TLRPC.Dialog dialog2) {
        boolean z = dialog instanceof TLRPC.TL_dialogFolder;
        if (z && !(dialog2 instanceof TLRPC.TL_dialogFolder)) {
            return -1;
        }
        if (!z && (dialog2 instanceof TLRPC.TL_dialogFolder)) {
            return 1;
        }
        boolean z2 = dialog.pinned;
        if (!z2 && dialog2.pinned) {
            return 1;
        }
        if (z2 && !dialog2.pinned) {
            return -1;
        }
        if (z2) {
            int i = dialog.pinnedNum;
            int i2 = dialog2.pinnedNum;
            if (i < i2) {
                return 1;
            }
            return i > i2 ? -1 : 0;
        }
        MediaDataController mediaDataController = getMediaDataController();
        long lastMessageOrDraftDate = DialogObject.getLastMessageOrDraftDate(dialog, mediaDataController.getDraft(dialog.id, 0L));
        long lastMessageOrDraftDate2 = DialogObject.getLastMessageOrDraftDate(dialog2, mediaDataController.getDraft(dialog2.id, 0L));
        if (lastMessageOrDraftDate < lastMessageOrDraftDate2) {
            return 1;
        }
        return lastMessageOrDraftDate > lastMessageOrDraftDate2 ? -1 : 0;
    }

    public int lambda$new$12(TLRPC.Update update, TLRPC.Update update2) {
        int updateQts;
        int updateQts2;
        int updateType = getUpdateType(update);
        int updateType2 = getUpdateType(update2);
        if (updateType != updateType2) {
            return AndroidUtilities.compare(updateType, updateType2);
        }
        if (updateType != 0) {
            if (updateType == 1) {
                updateQts = getUpdateQts(update);
                updateQts2 = getUpdateQts(update2);
                return AndroidUtilities.compare(updateQts, updateQts2);
            }
            if (updateType != 2) {
                return 0;
            }
            long updateChannelId = getUpdateChannelId(update);
            long updateChannelId2 = getUpdateChannelId(update2);
            if (updateChannelId != updateChannelId2) {
                return AndroidUtilities.compare(updateChannelId, updateChannelId2);
            }
        }
        updateQts = getUpdatePts(update);
        updateQts2 = getUpdatePts(update2);
        return AndroidUtilities.compare(updateQts, updateQts2);
    }

    public void lambda$new$13() {
        MessagesController messagesController = getMessagesController();
        getNotificationCenter().addObserver(messagesController, NotificationCenter.fileUploaded);
        getNotificationCenter().addObserver(messagesController, NotificationCenter.fileUploadFailed);
        getNotificationCenter().addObserver(messagesController, NotificationCenter.fileUploadProgressChanged);
        getNotificationCenter().addObserver(messagesController, NotificationCenter.fileLoaded);
        getNotificationCenter().addObserver(messagesController, NotificationCenter.fileLoadFailed);
        getNotificationCenter().addObserver(messagesController, NotificationCenter.messageReceivedByServer);
        getNotificationCenter().addObserver(messagesController, NotificationCenter.updateMessageMedia);
    }

    public void lambda$new$14() {
        checkPeerColors(false);
    }

    public void lambda$new$15() {
        this.enableJoined = this.notificationsPreferences.getBoolean("EnableContactJoined", true);
        this.nextTosCheckTime = this.notificationsPreferences.getInt("nextTosCheckTime", 0);
    }

    public void lambda$new$34() {
        getNotificationCenter().lambda$postNotificationNameOnUIThread$1(NotificationCenter.updateTranscriptionLock, new Object[0]);
    }

    public void lambda$new$457() {
        int i;
        LongSparseArray longSparseArray = new LongSparseArray();
        Iterator<Pair<StarsController.MessageId, AtomicBoolean>> it = this.pendingReportMessageDelivery.iterator();
        while (true) {
            if (!it.hasNext()) {
                break;
            }
            Pair<StarsController.MessageId, AtomicBoolean> next = it.next();
            Pair pair = (Pair) longSparseArray.get(((StarsController.MessageId) next.first).did);
            if (pair == null) {
                long j = ((StarsController.MessageId) next.first).did;
                Pair pair2 = new Pair(new HashSet(), (AtomicBoolean) next.second);
                longSparseArray.put(j, pair2);
                pair = pair2;
            }
            if (!((AtomicBoolean) next.second).get()) {
                ((AtomicBoolean) pair.second).set(false);
            }
            ((HashSet) pair.first).add(Integer.valueOf(((StarsController.MessageId) next.first).mid));
        }
        this.pendingReportMessageDelivery.clear();
        for (i = 0; i < longSparseArray.size(); i++) {
            Pair pair3 = (Pair) longSparseArray.valueAt(i);
            long keyAt = longSparseArray.keyAt(i);
            HashSet hashSet = (HashSet) pair3.first;
            boolean z = ((AtomicBoolean) pair3.second).get();
            TLRPC.TL_reportMessagesDelivery tL_reportMessagesDelivery = new TLRPC.TL_reportMessagesDelivery();
            tL_reportMessagesDelivery.peer = getInputPeer(keyAt);
            tL_reportMessagesDelivery.id.addAll(hashSet);
            tL_reportMessagesDelivery.push = z;
            getConnectionsManager().sendRequest(tL_reportMessagesDelivery, null);
        }
    }

    public void lambda$onFolderEmpty$185(int i) {
        if (i != 1) {
            removeFolder(i);
        } else {
            this.hasArchivedChats = false;
            checkArchiveFolder();
        }
    }

    public static void lambda$openApp$449(boolean[] zArr) {
        zArr[0] = true;
    }

    public void lambda$openApp$450(BaseFragment baseFragment, Browser.Progress progress, boolean[] zArr, TLRPC.User user, String str, boolean z, boolean z2, TL_bots.BotInfo[] botInfoArr) {
        BaseFragment baseFragment2;
        WebViewRequestProps of;
        BotWebViewSheet botWebViewSheet;
        BaseFragment safeLastFragment = baseFragment != null ? baseFragment : LaunchActivity.getSafeLastFragment();
        if (safeLastFragment == null) {
            return;
        }
        if (progress != null) {
            progress.end();
        }
        if (zArr[0]) {
            return;
        }
        if (user.bot_has_main_app) {
            if (safeLastFragment.getParentLayout() instanceof ActionBarLayout) {
                safeLastFragment = ((ActionBarLayout) safeLastFragment.getParentLayout()).getSheetFragment();
            }
            BaseFragment baseFragment3 = safeLastFragment;
            AndroidUtilities.hideKeyboard(baseFragment3.getFragmentView());
            int i = this.currentAccount;
            long j = user.id;
            baseFragment2 = baseFragment3;
            of = WebViewRequestProps.of(i, j, j, null, null, 4, 0, false, null, false, str, user, 0, z, z2);
            LaunchActivity launchActivity = LaunchActivity.instance;
            if (launchActivity != null && launchActivity.getBottomSheetTabs() != null && LaunchActivity.instance.getBottomSheetTabs().tryReopenTab(of) != null) {
                return;
            }
            botWebViewSheet = new BotWebViewSheet(baseFragment2.getContext(), baseFragment2.getResourceProvider());
            botWebViewSheet.setDefaultFullsize(true);
            botWebViewSheet.setNeedsContext(false);
        } else {
            TL_bots.BotInfo botInfo = botInfoArr[0];
            if (botInfo == null || !(botInfo.menu_button instanceof TL_bots.TL_botMenuButton)) {
                safeLastFragment.presentFragment(ChatActivity.of(user.id));
                return;
            }
            if (safeLastFragment.getParentLayout() instanceof ActionBarLayout) {
                safeLastFragment = ((ActionBarLayout) safeLastFragment.getParentLayout()).getSheetFragment();
            }
            BaseFragment baseFragment4 = safeLastFragment;
            TL_bots.TL_botMenuButton tL_botMenuButton = (TL_bots.TL_botMenuButton) botInfoArr[0].menu_button;
            AndroidUtilities.hideKeyboard(baseFragment4.getFragmentView());
            int i2 = this.currentAccount;
            long j2 = user.id;
            baseFragment2 = baseFragment4;
            of = WebViewRequestProps.of(i2, j2, j2, tL_botMenuButton.text, tL_botMenuButton.url, 2, 0, false, null, false, str, user, 0, z, z2);
            LaunchActivity launchActivity2 = LaunchActivity.instance;
            if (launchActivity2 != null && launchActivity2.getBottomSheetTabs() != null && LaunchActivity.instance.getBottomSheetTabs().tryReopenTab(of) != null) {
                return;
            }
            botWebViewSheet = new BotWebViewSheet(baseFragment2.getContext(), baseFragment2.getResourceProvider());
            botWebViewSheet.setDefaultFullsize(false);
            botWebViewSheet.setNeedsContext(true);
        }
        botWebViewSheet.setParentActivity(baseFragment2.getParentActivity());
        botWebViewSheet.requestWebView(baseFragment2, of);
        botWebViewSheet.show();
    }

    public static void lambda$openApp$451(boolean[] zArr, TL_bots.BotInfo[] botInfoArr, Runnable runnable, TLRPC.UserFull userFull) {
        if (zArr[0]) {
            return;
        }
        if (userFull != null) {
            botInfoArr[0] = userFull.bot_info;
        }
        AndroidUtilities.runOnUIThread(runnable);
    }

    public void lambda$openApp$452(final boolean[] zArr, final TL_bots.BotInfo[] botInfoArr, TLRPC.User user, int i, final Runnable runnable, TL_bots.BotInfo botInfo) {
        if (zArr[0]) {
            return;
        }
        botInfoArr[0] = botInfo;
        if (botInfo == null) {
            TLRPC.UserFull userFull = getUserFull(user.id);
            if (userFull == null) {
                if (zArr[0]) {
                    return;
                }
                loadFullUser(user, i, true, new Utilities.Callback() {
                    @Override
                    public final void run(Object obj) {
                        MessagesController.lambda$openApp$451(zArr, botInfoArr, runnable, (TLRPC.UserFull) obj);
                    }
                });
                return;
            }
            botInfoArr[0] = userFull.bot_info;
        }
        runnable.run();
    }

    public void lambda$openByUserName$407(Browser.Progress progress, AlertDialog[] alertDialogArr, BaseFragment baseFragment, boolean[] zArr, int i, Long l) {
        TLRPC.User user;
        TLRPC.Chat chat;
        boolean z;
        MessagesController messagesController;
        BaseFragment baseFragment2;
        int i2;
        try {
            if (progress != null) {
                progress.end();
            } else {
                alertDialogArr[0].dismiss();
            }
        } catch (Exception unused) {
        }
        alertDialogArr[0] = null;
        baseFragment.setVisibleDialog(null);
        if (zArr[0]) {
            return;
        }
        if (l == null) {
            if (baseFragment.getParentActivity() != null) {
                try {
                    if (baseFragment instanceof ChatActivity) {
                        ((ChatActivity) baseFragment).shakeContent();
                    }
                    BulletinFactory.of(baseFragment).createErrorBulletin(LocaleController.getString(R.string.NoUsernameFound)).show();
                    return;
                } catch (Exception e) {
                    FileLog.e(e);
                    return;
                }
            }
            return;
        }
        if (l.longValue() < 0) {
            chat = getChat(Long.valueOf(-l.longValue()));
            i2 = 1;
            z = false;
            user = null;
            messagesController = this;
            baseFragment2 = baseFragment;
        } else {
            user = getUser(l);
            chat = null;
            z = false;
            messagesController = this;
            baseFragment2 = baseFragment;
            i2 = i;
        }
        messagesController.openChatOrProfileWith(user, chat, baseFragment2, i2, z);
    }

    public static void lambda$openByUserName$408(boolean[] zArr) {
        zArr[0] = true;
    }

    public static void lambda$openByUserName$409(boolean[] zArr, DialogInterface dialogInterface) {
        zArr[0] = true;
    }

    public static void lambda$openByUserName$410(AlertDialog[] alertDialogArr, final boolean[] zArr, BaseFragment baseFragment) {
        AlertDialog alertDialog = alertDialogArr[0];
        if (alertDialog == null) {
            return;
        }
        alertDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public final void onCancel(DialogInterface dialogInterface) {
                MessagesController.lambda$openByUserName$409(zArr, dialogInterface);
            }
        });
        baseFragment.showDialog(alertDialogArr[0]);
    }

    public static void lambda$performLogout$298(TLObject tLObject) {
        if (tLObject instanceof TLRPC.TL_auth_loggedOut) {
            TLRPC.TL_auth_loggedOut tL_auth_loggedOut = (TLRPC.TL_auth_loggedOut) tLObject;
            if (tL_auth_loggedOut.future_auth_token != null) {
                AuthTokensHelper.addLogOutToken(tL_auth_loggedOut);
            }
        }
    }

    public void lambda$performLogout$299(final TLObject tLObject, TLRPC.TL_error tL_error) {
        getConnectionsManager().cleanup(false);
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                MessagesController.lambda$performLogout$298(TLObject.this);
            }
        });
    }

    public void lambda$pinDialog$341(long j, TLObject tLObject, TLRPC.TL_error tL_error) {
        if (j != 0) {
            getMessagesStorage().removePendingTask(j);
        }
    }

    public void lambda$pinMessage$122(int i, TLRPC.Chat chat, TLRPC.User user, boolean z, TLObject tLObject, TLRPC.TL_error tL_error) {
        if (tL_error == null) {
            ArrayList<Integer> arrayList = new ArrayList<>();
            arrayList.add(Integer.valueOf(i));
            getMessagesStorage().updatePinnedMessages(chat != null ? -chat.id : user.id, arrayList, !z, -1, 0, false, null);
            processUpdates((TLRPC.Updates) tLObject, false);
        }
    }

    public static int lambda$processChannelsUpdatesQueue$303(TLRPC.Updates updates, TLRPC.Updates updates2) {
        return AndroidUtilities.compare(updates.pts, updates2.pts);
    }

    public void lambda$processChatInfo$140(boolean z, long j, boolean z2, boolean z3, TLRPC.ChatFull chatFull, ArrayList arrayList, ArrayList arrayList2, HashMap hashMap, int i, boolean z4) {
        if (z && j > 0 && !z2 && System.currentTimeMillis() - this.loadedFullChats.get(j, 0L) > 60000) {
            loadFullChat(j, 0, z3);
        }
        if (chatFull != null) {
            if (this.fullChats.get(j) == null) {
                this.fullChats.put(j, chatFull);
                getTranslateController().updateDialogFull(-j);
            }
            putUsers(arrayList, z);
            if (chatFull.stickerset != null) {
                getMediaDataController().getGroupStickerSetById(chatFull.stickerset);
            }
            if (chatFull.emojiset != null) {
                getMediaDataController().getGroupStickerSetById(chatFull.emojiset);
            }
            getNotificationCenter().lambda$postNotificationNameOnUIThread$1(NotificationCenter.chatInfoDidLoad, chatFull, 0, Boolean.valueOf(z2), Boolean.FALSE);
        }
        if (arrayList2 != null) {
            getNotificationCenter().lambda$postNotificationNameOnUIThread$1(NotificationCenter.pinnedInfoDidLoad, Long.valueOf(-j), arrayList2, hashMap, Integer.valueOf(i), Boolean.valueOf(z4));
        }
    }

    public void lambda$processDialogsUpdate$216(org.telegram.tgnet.TLRPC.messages_Dialogs r23, androidx.collection.LongSparseArray r24, androidx.collection.LongSparseArray r25, boolean r26, org.telegram.messenger.support.LongSparseIntArray r27) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MessagesController.lambda$processDialogsUpdate$216(org.telegram.tgnet.TLRPC$messages_Dialogs, androidx.collection.LongSparseArray, androidx.collection.LongSparseArray, boolean, org.telegram.messenger.support.LongSparseIntArray):void");
    }

    public void lambda$processDialogsUpdate$217(final org.telegram.tgnet.TLRPC.messages_Dialogs r20, final boolean r21) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MessagesController.lambda$processDialogsUpdate$217(org.telegram.tgnet.TLRPC$messages_Dialogs, boolean):void");
    }

    public void lambda$processDialogsUpdateRead$211(org.telegram.messenger.support.LongSparseIntArray r14, org.telegram.messenger.support.LongSparseIntArray r15) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MessagesController.lambda$processDialogsUpdateRead$211(org.telegram.messenger.support.LongSparseIntArray, org.telegram.messenger.support.LongSparseIntArray):void");
    }

    public void lambda$processLoadedChannelAdmins$60(long j, LongSparseArray longSparseArray, boolean z) {
        this.channelAdmins.put(j, longSparseArray);
        if (z) {
            this.loadingChannelAdmins.delete(j);
            loadChannelAdmins(j, false);
            getNotificationCenter().lambda$postNotificationNameOnUIThread$1(NotificationCenter.didLoadChatAdmins, Long.valueOf(j));
        }
    }

    public void lambda$processLoadedDeleteTask$81() {
        checkDeletingTask(true);
    }

    public void lambda$processLoadedDeleteTask$82(LongSparseArray longSparseArray, LongSparseArray longSparseArray2, int i) {
        this.gettingNewDeleteTask = false;
        if (longSparseArray == null && longSparseArray2 == null) {
            this.currentDeletingTaskTime = 0;
            this.currentDeletingTaskMids = null;
            this.currentDeletingTaskMediaMids = null;
            return;
        }
        this.currentDeletingTaskTime = i;
        this.currentDeletingTaskMids = longSparseArray;
        this.currentDeletingTaskMediaMids = longSparseArray2;
        if (this.currentDeleteTaskRunnable != null) {
            Utilities.stageQueue.cancelRunnable(this.currentDeleteTaskRunnable);
            this.currentDeleteTaskRunnable = null;
        }
        if (checkDeletingTask(false)) {
            return;
        }
        this.currentDeleteTaskRunnable = new Runnable() {
            @Override
            public final void run() {
                MessagesController.this.lambda$processLoadedDeleteTask$81();
            }
        };
        Utilities.stageQueue.postRunnable(this.currentDeleteTaskRunnable, Math.abs(getConnectionsManager().getCurrentTime() - this.currentDeletingTaskTime) * 1000);
    }

    public static int lambda$processLoadedDialogFilters$18(DialogFilter dialogFilter, DialogFilter dialogFilter2) {
        int i = dialogFilter.order;
        int i2 = dialogFilter2.order;
        if (i > i2) {
            return 1;
        }
        return i < i2 ? -1 : 0;
    }

    public void lambda$processLoadedDialogFilters$19(int i, ArrayList arrayList, ArrayList arrayList2, ArrayList arrayList3, TLRPC.messages_Dialogs messages_dialogs, ArrayList arrayList4, LongSparseArray longSparseArray, LongSparseArray longSparseArray2, Runnable runnable) {
        int i2;
        ArrayList arrayList5;
        ArrayList arrayList6;
        if (i != 2) {
            this.dialogFilters = arrayList;
            this.dialogFiltersById.clear();
            int size = this.dialogFilters.size();
            for (int i3 = 0; i3 < size; i3++) {
                DialogFilter dialogFilter = this.dialogFilters.get(i3);
                this.dialogFiltersById.put(dialogFilter.id, dialogFilter);
            }
            Collections.sort(this.dialogFilters, new Comparator() {
                @Override
                public final int compare(Object obj, Object obj2) {
                    int lambda$processLoadedDialogFilters$18;
                    lambda$processLoadedDialogFilters$18 = MessagesController.lambda$processLoadedDialogFilters$18((MessagesController.DialogFilter) obj, (MessagesController.DialogFilter) obj2);
                    return lambda$processLoadedDialogFilters$18;
                }
            });
            putUsers(arrayList2, true);
            putChats(arrayList3, true);
            this.dialogFiltersLoaded = true;
            getNotificationCenter().lambda$postNotificationNameOnUIThread$1(NotificationCenter.dialogFiltersUpdated, new Object[0]);
            if (i == 0) {
                loadRemoteFilters(false);
            }
            if (messages_dialogs != null && !messages_dialogs.dialogs.isEmpty()) {
                applyDialogsNotificationsSettings(messages_dialogs.dialogs);
            }
            if (arrayList4 != null) {
                for (int i4 = 0; i4 < arrayList4.size(); i4++) {
                    TLRPC.EncryptedChat encryptedChat = (TLRPC.EncryptedChat) arrayList4.get(i4);
                    if ((encryptedChat instanceof TLRPC.TL_encryptedChat) && AndroidUtilities.getMyLayerVersion(encryptedChat.layer) < SecretChatHelper.CURRENT_SECRET_CHAT_LAYER) {
                        getSecretChatHelper().sendNotifyLayerMessage(encryptedChat, null);
                    }
                    putEncryptedChat(encryptedChat, true);
                }
            }
            for (int i5 = 0; i5 < longSparseArray.size(); i5++) {
                long keyAt = longSparseArray.keyAt(i5);
                TLRPC.Dialog dialog = (TLRPC.Dialog) longSparseArray.valueAt(i5);
                TLRPC.Dialog dialog2 = (TLRPC.Dialog) this.dialogs_dict.get(keyAt);
                if (messages_dialogs != null && messages_dialogs.dialogs.contains(dialog)) {
                    if (dialog.draft instanceof TLRPC.TL_draftMessage) {
                        getMediaDataController().saveDraft(dialog.id, 0L, dialog.draft, null, false);
                    }
                    if (dialog2 != null) {
                        dialog2.notify_settings = dialog.notify_settings;
                    }
                }
                ArrayList arrayList7 = (ArrayList) longSparseArray2.get(dialog.id);
                long j = 0;
                if (dialog2 == null) {
                    this.dialogs_dict.put(keyAt, dialog);
                    this.dialogMessage.put(keyAt, arrayList7);
                    if (arrayList7 != null) {
                        for (int i6 = 0; i6 < arrayList7.size(); i6++) {
                            MessageObject messageObject = (MessageObject) arrayList7.get(i6);
                            if (messageObject != null && messageObject.messageOwner.peer_id.channel_id == 0) {
                                this.dialogMessagesByIds.put(messageObject.getId(), messageObject);
                                long j2 = messageObject.messageOwner.random_id;
                                if (j2 != 0) {
                                    this.dialogMessagesByRandomIds.put(j2, messageObject);
                                }
                            }
                        }
                    }
                } else {
                    dialog2.pinned = dialog.pinned;
                    dialog2.pinnedNum = dialog.pinnedNum;
                    ArrayList arrayList8 = (ArrayList) this.dialogMessage.get(keyAt);
                    for (int i7 = 0; arrayList8 != null && i7 < arrayList8.size(); i7++) {
                        if (arrayList8.get(i7) != null && ((MessageObject) arrayList8.get(i7)).deleted) {
                            break;
                        }
                    }
                    if (arrayList8 != null && dialog2.top_message <= 0) {
                        this.dialogs_dict.put(keyAt, dialog);
                        this.dialogMessage.put(keyAt, arrayList7);
                        int i8 = 0;
                        while (i8 < arrayList8.size()) {
                            MessageObject messageObject2 = (MessageObject) arrayList8.get(i8);
                            if (messageObject2 == null) {
                                arrayList6 = arrayList8;
                            } else {
                                arrayList6 = arrayList8;
                                if (messageObject2.messageOwner.peer_id.channel_id == 0) {
                                    this.dialogMessagesByIds.remove(messageObject2.getId());
                                }
                                long j3 = messageObject2.messageOwner.random_id;
                                if (j3 != 0) {
                                    this.dialogMessagesByRandomIds.remove(j3);
                                }
                            }
                            i8++;
                            arrayList8 = arrayList6;
                        }
                        ArrayList arrayList9 = arrayList8;
                        if (arrayList7 != null) {
                            int i9 = 0;
                            while (i9 < arrayList7.size()) {
                                MessageObject messageObject3 = (MessageObject) arrayList7.get(i9);
                                if (messageObject3 == null || messageObject3.messageOwner.peer_id.channel_id != j) {
                                    arrayList5 = arrayList9;
                                } else {
                                    int i10 = 0;
                                    while (true) {
                                        arrayList5 = arrayList9;
                                        if (i10 >= arrayList9.size()) {
                                            break;
                                        }
                                        MessageObject messageObject4 = (MessageObject) arrayList5.get(i10);
                                        if (messageObject4 != null && messageObject4.getId() == messageObject3.getId()) {
                                            messageObject3.deleted = messageObject4.deleted;
                                            break;
                                        } else {
                                            i10++;
                                            arrayList9 = arrayList5;
                                        }
                                    }
                                    this.dialogMessagesByIds.put(messageObject3.getId(), messageObject3);
                                    long j4 = messageObject3.messageOwner.random_id;
                                    if (j4 != 0) {
                                        this.dialogMessagesByRandomIds.put(j4, messageObject3);
                                    }
                                }
                                i9++;
                                arrayList9 = arrayList5;
                                j = 0;
                            }
                        }
                    }
                    if (dialog.top_message < dialog2.top_message) {
                        if ((arrayList8 == null) == (arrayList7 == null)) {
                            if (arrayList8 != null) {
                                if (arrayList7 != null) {
                                    if (arrayList8.size() == arrayList7.size()) {
                                    }
                                }
                            }
                        }
                    }
                    this.dialogs_dict.put(keyAt, dialog);
                    this.dialogMessage.put(keyAt, arrayList7);
                    if (arrayList8 != null) {
                        for (int i11 = 0; i11 < arrayList8.size(); i11++) {
                            MessageObject messageObject5 = (MessageObject) arrayList8.get(i11);
                            if (messageObject5 != null) {
                                if (messageObject5.messageOwner.peer_id.channel_id == 0) {
                                    this.dialogMessagesByIds.remove(messageObject5.getId());
                                }
                                long j5 = messageObject5.messageOwner.random_id;
                                if (j5 != 0) {
                                    this.dialogMessagesByRandomIds.remove(j5);
                                }
                            }
                        }
                    }
                    if (arrayList7 != null) {
                        for (int i12 = 0; i12 < arrayList7.size(); i12++) {
                            MessageObject messageObject6 = (MessageObject) arrayList7.get(i12);
                            if (messageObject6 != null && messageObject6.messageOwner.peer_id.channel_id == 0) {
                                int i13 = 0;
                                while (true) {
                                    if (arrayList8 == null || i13 >= arrayList8.size()) {
                                        break;
                                    }
                                    MessageObject messageObject7 = (MessageObject) arrayList8.get(i13);
                                    if (messageObject7 != null && messageObject7.getId() == messageObject6.getId()) {
                                        messageObject6.deleted = messageObject7.deleted;
                                        break;
                                    }
                                    i13++;
                                }
                                this.dialogMessagesByIds.put(messageObject6.getId(), messageObject6);
                                long j6 = messageObject6.messageOwner.random_id;
                                if (j6 != 0) {
                                    this.dialogMessagesByRandomIds.put(j6, messageObject6);
                                }
                            }
                        }
                    }
                }
                getTranslateController().checkDialogMessage(keyAt);
            }
            this.allDialogs.clear();
            int size2 = this.dialogs_dict.size();
            for (int i14 = 0; i14 < size2; i14++) {
                TLRPC.Dialog dialog3 = (TLRPC.Dialog) this.dialogs_dict.valueAt(i14);
                if (this.deletingDialogs.indexOfKey(dialog3.id) < 0) {
                    this.allDialogs.add(dialog3);
                }
            }
            sortDialogs(null);
            i2 = 0;
            getNotificationCenter().lambda$postNotificationNameOnUIThread$1(NotificationCenter.dialogsNeedReload, new Object[0]);
        } else {
            i2 = 0;
        }
        if (i != 0) {
            getUserConfig().filtersLoaded = true;
            getUserConfig().saveConfig(i2);
            this.loadingRemoteFilters = i2;
            getNotificationCenter().lambda$postNotificationNameOnUIThread$1(NotificationCenter.filterSettingsUpdated, new Object[i2]);
        }
        lockFiltersInternal();
        if (runnable != null) {
            runnable.run();
        }
    }

    public void lambda$processLoadedDialogFilters$20(org.telegram.tgnet.TLRPC.messages_Dialogs r20, final java.util.ArrayList r21, final org.telegram.tgnet.TLRPC.messages_Dialogs r22, final int r23, final java.util.ArrayList r24, final java.util.ArrayList r25, final java.util.ArrayList r26, final java.lang.Runnable r27) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MessagesController.lambda$processLoadedDialogFilters$20(org.telegram.tgnet.TLRPC$messages_Dialogs, java.util.ArrayList, org.telegram.tgnet.TLRPC$messages_Dialogs, int, java.util.ArrayList, java.util.ArrayList, java.util.ArrayList, java.lang.Runnable):void");
    }

    public void lambda$processLoadedDialogs$205(TLRPC.messages_Dialogs messages_dialogs, ArrayList arrayList, int i, boolean z, long[] jArr, int i2) {
        putUsers(messages_dialogs.users, true);
        if (arrayList != null) {
            for (int i3 = 0; i3 < arrayList.size(); i3++) {
                long j = ((TLRPC.UserFull) arrayList.get(i3)).id;
                this.fullUsers.put(j, (TLRPC.UserFull) arrayList.get(i3));
                getTranslateController().updateDialogFull(j);
            }
        }
        this.loadingDialogs.put(i, false);
        if (z) {
            this.dialogsEndReached.put(i, false);
            this.serverDialogsEndReached.put(i, false);
        } else if (jArr[0] == 2147483647L) {
            this.dialogsEndReached.put(i, true);
            this.serverDialogsEndReached.put(i, true);
        } else {
            loadDialogs(i, 0, i2, false);
        }
        getNotificationCenter().lambda$postNotificationNameOnUIThread$1(NotificationCenter.dialogsNeedReload, new Object[0]);
    }

    public void lambda$processLoadedDialogs$206(TLRPC.Chat chat) {
        checkChatInviter(chat.id, true);
    }

    public void lambda$processLoadedDialogs$207(org.telegram.tgnet.TLRPC.Message r28, int r29, org.telegram.tgnet.TLRPC.messages_Dialogs r30, java.util.ArrayList r31, java.util.ArrayList r32, boolean r33, int r34, androidx.collection.LongSparseArray r35, androidx.collection.LongSparseArray r36, androidx.collection.LongSparseArray r37, int r38, boolean r39, int r40, java.util.ArrayList r41) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MessagesController.lambda$processLoadedDialogs$207(org.telegram.tgnet.TLRPC$Message, int, org.telegram.tgnet.TLRPC$messages_Dialogs, java.util.ArrayList, java.util.ArrayList, boolean, int, androidx.collection.LongSparseArray, androidx.collection.LongSparseArray, androidx.collection.LongSparseArray, int, boolean, int, java.util.ArrayList):void");
    }

    public void lambda$processLoadedDialogs$208(final int r32, final int r33, final org.telegram.tgnet.TLRPC.messages_Dialogs r34, final java.util.ArrayList r35, final boolean r36, final int r37, final java.util.ArrayList r38, final int r39, final boolean r40, final boolean r41) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MessagesController.lambda$processLoadedDialogs$208(int, int, org.telegram.tgnet.TLRPC$messages_Dialogs, java.util.ArrayList, boolean, int, java.util.ArrayList, int, boolean, boolean):void");
    }

    public void lambda$processLoadedMessages$177(long j, long j2, int i, int i2, boolean z, int i3, int i4, int i5, int i6, int i7, int i8, long j3, int i9, int i10, int i11, int i12, boolean z2, boolean z3, Timer timer, long j4) {
        loadMessagesInternal(j, j2, false, i, (i2 == 2 && z) ? i3 : i4, i5, false, 0, i6, i2, i7, i8, j3, i9, i3, i10, i11, z, i12, true, z2, z3, timer, j4);
    }

    public void lambda$processLoadedMessages$178(int i, TLRPC.messages_Messages messages_messages, boolean z, boolean z2, int i2) {
        getNotificationCenter().lambda$postNotificationNameOnUIThread$1(NotificationCenter.messagesDidLoadWithoutProcess, Integer.valueOf(i), Integer.valueOf(messages_messages.messages.size()), Boolean.valueOf(z), Boolean.valueOf(z2), Integer.valueOf(i2));
    }

    public static int lambda$processLoadedMessages$179(MessageObject messageObject, MessageObject messageObject2) {
        int i;
        int i2;
        if (messageObject.messageOwner.date != messageObject2.messageOwner.date || messageObject.getId() < 0 || messageObject2.getId() < 0) {
            i = messageObject2.messageOwner.date;
            i2 = messageObject.messageOwner.date;
        } else {
            i = messageObject2.getId();
            i2 = messageObject.getId();
        }
        return i - i2;
    }

    public static int lambda$processLoadedMessages$180(MessageObject messageObject, MessageObject messageObject2) {
        return messageObject2.getId() - messageObject.getId();
    }

    public void lambda$processLoadedMessages$181(Timer.Task task, boolean z, int i, int i2, boolean z2, boolean z3, int i3, long j, int i4, ArrayList arrayList, int i5, int i6, int i7, int i8, int i9, int i10, int i11, int i12) {
        Timer.done(task);
        NotificationCenter notificationCenter = getNotificationCenter();
        if (z) {
            notificationCenter.lambda$postNotificationNameOnUIThread$1(NotificationCenter.messagesDidLoad, Long.valueOf(j), Integer.valueOf(i4), arrayList, Boolean.valueOf(z2), Integer.valueOf(i5), Integer.valueOf(i3), Integer.valueOf(i6), Integer.valueOf(i7), Integer.valueOf(i8), Boolean.valueOf(z3), Integer.valueOf(i), Integer.valueOf(i9), Integer.valueOf(i10), Integer.valueOf(i11), Integer.valueOf(i12));
        } else {
            notificationCenter.lambda$postNotificationNameOnUIThread$1(NotificationCenter.messagesDidLoadWithoutProcess, Integer.valueOf(i), Integer.valueOf(i2), Boolean.valueOf(z2), Boolean.valueOf(z3), Integer.valueOf(i3));
        }
    }

    public void lambda$processLoadedMessages$182(Timer.Task task, Timer timer, TLRPC.messages_Messages messages_messages, final boolean z, final boolean z2, final int i, boolean z3, final int i2, int i3, final int i4, final long j, final ArrayList arrayList, long j2, final int i5, final int i6, final boolean z4, final int i7, final int i8, final int i9, final int i10, final int i11, final int i12, ArrayList arrayList2, HashMap hashMap) {
        int i13;
        int i14;
        int i15;
        int i16;
        MessagesController messagesController;
        int i17;
        long j3;
        Timer.done(task);
        Timer.Task start = Timer.start(timer, "processLoadedMessages: post runOnUIThread");
        putUsers(messages_messages.users, z);
        putChats(messages_messages.chats, z);
        if (messages_messages.animatedEmoji != null && z2) {
            AnimatedEmojiDrawable.getDocumentFetcher(this.currentAccount).processDocuments(messages_messages.animatedEmoji);
        }
        if (i == 1) {
            i15 = 0;
        } else {
            if (z3 && i2 == 2) {
                i14 = Integer.MAX_VALUE;
                for (int i18 = 0; i18 < messages_messages.messages.size(); i18++) {
                    TLRPC.Message message = messages_messages.messages.get(i18);
                    if ((!message.out || message.from_scheduled) && (i16 = message.id) > i3 && i16 < i14) {
                        i14 = i16;
                    }
                }
                i13 = i3;
            } else {
                i13 = i3;
                i14 = Integer.MAX_VALUE;
            }
            i15 = i14 == Integer.MAX_VALUE ? i13 : i14;
        }
        if (i == 1 && i4 == 1) {
            getNotificationCenter().lambda$postNotificationNameOnUIThread$1(NotificationCenter.scheduledMessagesUpdated, Long.valueOf(j), Integer.valueOf(arrayList.size()), Boolean.FALSE);
        }
        if (DialogObject.isEncryptedDialog(j) || i == 5) {
            getNotificationCenter().lambda$postNotificationNameOnUIThread$1(NotificationCenter.messagesDidLoad, Long.valueOf(j), Integer.valueOf(i4), arrayList, Boolean.valueOf(z), Integer.valueOf(i15), Integer.valueOf(i7), Integer.valueOf(i8), Integer.valueOf(i9), Integer.valueOf(i2), Boolean.valueOf(z4), Integer.valueOf(i5), Integer.valueOf(i10), Integer.valueOf(i11), Integer.valueOf(i12), Integer.valueOf(i));
        } else {
            final Timer.Task start2 = Timer.start(timer, "loadReplyMessagesForMessages");
            final int i19 = i15;
            getMediaDataController().loadReplyMessagesForMessages(arrayList, j, i, j2, new Runnable() {
                @Override
                public final void run() {
                    MessagesController.this.lambda$processLoadedMessages$181(start2, z2, i5, i6, z, z4, i7, j, i4, arrayList, i19, i8, i9, i2, i10, i11, i12, i);
                }
            }, i5, timer);
        }
        if (arrayList2.isEmpty()) {
            messagesController = this;
            i17 = i;
            j3 = j;
        } else {
            messagesController = this;
            i17 = i;
            j3 = j;
            messagesController.reloadMessages(arrayList2, j3, i17);
        }
        if (!hashMap.isEmpty()) {
            messagesController.reloadWebPages(j3, hashMap, i17);
        }
        Timer.done(start);
        Timer.finish(timer);
    }

    public void lambda$processUpdateArray$358(ArrayList arrayList, ArrayList arrayList2) {
        putUsers(arrayList, false);
        putChats(arrayList2, false);
    }

    public void lambda$processUpdateArray$359(ArrayList arrayList, ArrayList arrayList2) {
        putUsers(arrayList, false);
        putChats(arrayList2, false);
    }

    public void lambda$processUpdateArray$360(TLRPC.TL_updateUserTyping tL_updateUserTyping) {
        getNotificationCenter().lambda$postNotificationNameOnUIThread$1(NotificationCenter.onEmojiInteractionsReceived, Long.valueOf(tL_updateUserTyping.user_id), tL_updateUserTyping.action);
    }

    public void lambda$processUpdateArray$361(TLRPC.TL_updateChatUserTyping tL_updateChatUserTyping) {
        getNotificationCenter().lambda$postNotificationNameOnUIThread$1(NotificationCenter.onEmojiInteractionsReceived, Long.valueOf(-tL_updateChatUserTyping.chat_id), tL_updateChatUserTyping.action);
    }

    public void lambda$processUpdateArray$362(TLRPC.TL_updatePeerBlocked tL_updatePeerBlocked) {
        long peerId = MessageObject.getPeerId(tL_updatePeerBlocked.peer_id);
        if (!tL_updatePeerBlocked.blocked) {
            this.blockePeers.delete(peerId);
        } else if (this.blockePeers.indexOfKey(peerId) < 0) {
            this.blockePeers.put(peerId, 1);
        }
        getNotificationCenter().lambda$postNotificationNameOnUIThread$1(NotificationCenter.blockedUsersDidLoad, new Object[0]);
        getStoriesController().updateBlockUser(peerId, tL_updatePeerBlocked.blocked_my_stories_from, false);
    }

    public void lambda$processUpdateArray$363(final TLRPC.TL_updatePeerBlocked tL_updatePeerBlocked) {
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                MessagesController.this.lambda$processUpdateArray$362(tL_updatePeerBlocked);
            }
        });
    }

    public void lambda$processUpdateArray$364(TLRPC.TL_updateServiceNotification tL_updateServiceNotification) {
        getNotificationCenter().lambda$postNotificationNameOnUIThread$1(NotificationCenter.needShowAlert, 2, tL_updateServiceNotification.message, tL_updateServiceNotification.type);
    }

    public void lambda$processUpdateArray$365(TLRPC.Message message) {
        getSendMessagesHelper().onMessageEdited(message);
    }

    public void lambda$processUpdateArray$366(TLRPC.TL_updateLangPack tL_updateLangPack) {
        LocaleController.getInstance().saveRemoteLocaleStringsForCurrentLocale(tL_updateLangPack.difference, this.currentAccount);
    }

    public void lambda$processUpdateArray$367(ArrayList arrayList) {
        getNotificationsController().processNewMessages(arrayList, true, false, null);
    }

    public void lambda$processUpdateArray$368(final ArrayList arrayList) {
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                MessagesController.this.lambda$processUpdateArray$367(arrayList);
            }
        });
    }

    public void lambda$processUpdateArray$369(LongSparseArray longSparseArray) {
        getNotificationsController().processEditedMessages(longSparseArray);
        getTopicsController().processEditedMessages(longSparseArray);
    }

    public void lambda$processUpdateArray$370(final LongSparseArray longSparseArray) {
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                MessagesController.this.lambda$processUpdateArray$369(longSparseArray);
            }
        });
    }

    public void lambda$processUpdateArray$371(TLRPC.User user) {
        getContactsController().addContactToPhoneBook(user, true);
    }

    public void lambda$processUpdateArray$372() {
        getNotificationsController().deleteNotificationChannelGlobal(0);
    }

    public void lambda$processUpdateArray$373() {
        getNotificationsController().deleteNotificationChannelGlobal(1);
    }

    public void lambda$processUpdateArray$374() {
        getNotificationsController().deleteNotificationChannelGlobal(2);
    }

    public void lambda$processUpdateArray$375(TLRPC.TL_updateChannel tL_updateChannel) {
        getChannelDifference(tL_updateChannel.channel_id, 1, 0L, null);
    }

    public void lambda$processUpdateArray$376(TLRPC.Chat chat) {
        getNotificationCenter().lambda$postNotificationNameOnUIThread$1(NotificationCenter.channelRightsUpdated, chat);
    }

    public void lambda$processUpdateArray$377(TLObject tLObject, TLRPC.TL_error tL_error) {
        if (tLObject != null) {
            processUpdates((TLRPC.Updates) tLObject, false);
        }
    }

    public void lambda$processUpdateArray$378(int r44, java.util.ArrayList r45, java.util.ArrayList r46, androidx.collection.LongSparseArray r47, int r48, org.telegram.messenger.support.LongSparseIntArray r49, androidx.collection.LongSparseArray r50, androidx.collection.LongSparseArray r51, java.util.ArrayList r52, androidx.collection.LongSparseArray r53, androidx.collection.LongSparseArray r54, boolean r55, java.util.ArrayList r56, java.util.ArrayList r57, androidx.collection.LongSparseArray r58, androidx.collection.LongSparseArray r59, androidx.collection.LongSparseArray r60, java.util.ArrayList r61) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MessagesController.lambda$processUpdateArray$378(int, java.util.ArrayList, java.util.ArrayList, androidx.collection.LongSparseArray, int, org.telegram.messenger.support.LongSparseIntArray, androidx.collection.LongSparseArray, androidx.collection.LongSparseArray, java.util.ArrayList, androidx.collection.LongSparseArray, androidx.collection.LongSparseArray, boolean, java.util.ArrayList, java.util.ArrayList, androidx.collection.LongSparseArray, androidx.collection.LongSparseArray, androidx.collection.LongSparseArray, java.util.ArrayList):void");
    }

    public void lambda$processUpdateArray$379(org.telegram.messenger.support.LongSparseIntArray r22, org.telegram.messenger.support.LongSparseIntArray r23, android.util.SparseIntArray r24, androidx.collection.LongSparseArray r25, androidx.collection.LongSparseArray r26, androidx.collection.LongSparseArray r27, androidx.collection.LongSparseArray r28, androidx.collection.LongSparseArray r29, org.telegram.messenger.support.LongSparseIntArray r30) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MessagesController.lambda$processUpdateArray$379(org.telegram.messenger.support.LongSparseIntArray, org.telegram.messenger.support.LongSparseIntArray, android.util.SparseIntArray, androidx.collection.LongSparseArray, androidx.collection.LongSparseArray, androidx.collection.LongSparseArray, androidx.collection.LongSparseArray, androidx.collection.LongSparseArray, org.telegram.messenger.support.LongSparseIntArray):void");
    }

    public void lambda$processUpdateArray$380(final LongSparseIntArray longSparseIntArray, final LongSparseIntArray longSparseIntArray2, final SparseIntArray sparseIntArray, final LongSparseArray longSparseArray, final LongSparseArray longSparseArray2, final LongSparseArray longSparseArray3, final LongSparseArray longSparseArray4, final LongSparseArray longSparseArray5, final LongSparseIntArray longSparseIntArray3) {
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                MessagesController.this.lambda$processUpdateArray$379(longSparseIntArray, longSparseIntArray2, sparseIntArray, longSparseArray, longSparseArray2, longSparseArray3, longSparseArray4, longSparseArray5, longSparseIntArray3);
            }
        });
    }

    public void lambda$processUpdateArray$381(long j, ArrayList arrayList) {
        getMessagesStorage().updateDialogsWithDeletedMessages(j, -j, arrayList, getMessagesStorage().markMessagesAsDeleted(j, arrayList, false, true, 0, 0), false);
    }

    public void lambda$processUpdateArray$382(long j, ArrayList arrayList, long j2) {
        getMessagesStorage().updateDialogsWithDeletedMessages(j, -j, arrayList, getMessagesStorage().markMessagesAsDeleted(j, arrayList, false, true, 5, (int) j2), false);
    }

    public void lambda$processUpdateArray$383(long j, int i) {
        getMessagesStorage().updateDialogsWithDeletedMessages(j, -j, new ArrayList<>(), getMessagesStorage().markMessagesAsDeleted(j, i, false, true), false);
    }

    public void lambda$processUpdates$353(boolean z, long j, ArrayList arrayList) {
        if (z) {
            getNotificationCenter().lambda$postNotificationNameOnUIThread$1(NotificationCenter.updateInterfaces, Integer.valueOf(UPDATE_MASK_USER_PRINT));
        }
        updateInterfaceWithMessages(j, arrayList, 0);
        getNotificationCenter().lambda$postNotificationNameOnUIThread$1(NotificationCenter.dialogsNeedReload, new Object[0]);
    }

    public void lambda$processUpdates$354(boolean z, TLRPC.Updates updates, ArrayList arrayList) {
        if (z) {
            getNotificationCenter().lambda$postNotificationNameOnUIThread$1(NotificationCenter.updateInterfaces, Integer.valueOf(UPDATE_MASK_USER_PRINT));
        }
        updateInterfaceWithMessages(-updates.chat_id, arrayList, 0);
        getNotificationCenter().lambda$postNotificationNameOnUIThread$1(NotificationCenter.dialogsNeedReload, new Object[0]);
    }

    public void lambda$processUpdates$355(ArrayList arrayList) {
        getNotificationsController().processNewMessages(arrayList, true, false, null);
    }

    public void lambda$processUpdates$356(final ArrayList arrayList) {
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                MessagesController.this.lambda$processUpdates$355(arrayList);
            }
        });
    }

    public void lambda$processUpdates$357() {
        getNotificationCenter().lambda$postNotificationNameOnUIThread$1(NotificationCenter.updateInterfaces, Integer.valueOf(UPDATE_MASK_STATUS));
    }

    public int lambda$processUpdatesQueue$304(TLRPC.Updates updates, TLRPC.Updates updates2) {
        return AndroidUtilities.compare(getUpdateSeq(updates), getUpdateSeq(updates2));
    }

    public static int lambda$processUpdatesQueue$305(TLRPC.Updates updates, TLRPC.Updates updates2) {
        return AndroidUtilities.compare(updates.pts, updates2.pts);
    }

    public static int lambda$processUpdatesQueue$306(TLRPC.Updates updates, TLRPC.Updates updates2) {
        return AndroidUtilities.compare(updates.pts, updates2.pts);
    }

    public void lambda$processUserInfo$141(boolean z, TLRPC.User user, int i, boolean z2, TLRPC.UserFull userFull, ArrayList arrayList, HashMap hashMap, int i2, boolean z3) {
        if (z && System.currentTimeMillis() - this.loadedFullUsers.get(user.id, 0L) > 60000) {
            loadFullUser(user, i, z2);
        }
        if (userFull != null) {
            if (this.fullUsers.get(user.id) == null) {
                this.fullUsers.put(user.id, userFull);
                getTranslateController().updateDialogFull(user.id);
                StarsController.getInstance(this.currentAccount).invalidateProfileGifts(userFull);
                int indexOfKey = this.blockePeers.indexOfKey(user.id);
                if (userFull.blocked) {
                    if (indexOfKey < 0) {
                        this.blockePeers.put(user.id, 1);
                        getNotificationCenter().lambda$postNotificationNameOnUIThread$1(NotificationCenter.blockedUsersDidLoad, new Object[0]);
                    }
                } else if (indexOfKey >= 0) {
                    this.blockePeers.removeAt(indexOfKey);
                    getNotificationCenter().lambda$postNotificationNameOnUIThread$1(NotificationCenter.blockedUsersDidLoad, new Object[0]);
                }
            }
            getNotificationCenter().lambda$postNotificationNameOnUIThread$1(NotificationCenter.userInfoDidLoad, Long.valueOf(user.id), userFull);
        }
        if (arrayList != null) {
            getNotificationCenter().lambda$postNotificationNameOnUIThread$1(NotificationCenter.pinnedInfoDidLoad, Long.valueOf(user.id), arrayList, hashMap, Integer.valueOf(i2), Boolean.valueOf(z3));
        }
    }

    public void lambda$putChat$53(TLRPC.Chat chat) {
        getNotificationCenter().lambda$postNotificationNameOnUIThread$1(NotificationCenter.channelRightsUpdated, chat);
    }

    public void lambda$putChat$54(TLRPC.Chat chat) {
        getNotificationCenter().lambda$postNotificationNameOnUIThread$1(NotificationCenter.chatSwithcedToForum, Long.valueOf(chat.id));
    }

    public void lambda$putUsers$52() {
        getNotificationCenter().lambda$postNotificationNameOnUIThread$1(NotificationCenter.updateInterfaces, Integer.valueOf(UPDATE_MASK_STATUS));
    }

    public void lambda$registerForPush$300() {
        this.registeringForPush = false;
    }

    public void lambda$registerForPush$301(int i, String str, TLObject tLObject, TLRPC.TL_error tL_error) {
        if (tLObject instanceof TLRPC.TL_boolTrue) {
            if (BuildVars.LOGS_ENABLED) {
                FileLog.d("account " + this.currentAccount + " registered for push, push type: " + i);
            }
            getUserConfig().registeredForPush = true;
            SharedConfig.pushString = str;
            SharedConfig.pushType = i;
            getUserConfig().saveConfig(false);
        }
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                MessagesController.this.lambda$registerForPush$300();
            }
        });
    }

    public void lambda$reloadDialogsReadValue$58(TLObject tLObject, TLRPC.TL_error tL_error) {
        TLRPC.TL_updateReadHistoryOutbox tL_updateReadHistoryOutbox;
        TLRPC.TL_updateReadHistoryInbox tL_updateReadHistoryInbox;
        if (tLObject != null) {
            TLRPC.TL_messages_peerDialogs tL_messages_peerDialogs = (TLRPC.TL_messages_peerDialogs) tLObject;
            ArrayList<TLRPC.Update> arrayList = new ArrayList<>();
            for (int i = 0; i < tL_messages_peerDialogs.dialogs.size(); i++) {
                TLRPC.Dialog dialog = tL_messages_peerDialogs.dialogs.get(i);
                DialogObject.initDialog(dialog);
                Integer num = this.dialogs_read_inbox_max.get(Long.valueOf(dialog.id));
                if (num == null) {
                    num = 0;
                }
                this.dialogs_read_inbox_max.put(Long.valueOf(dialog.id), Integer.valueOf(Math.max(dialog.read_inbox_max_id, num.intValue())));
                if (num.intValue() == 0) {
                    if (dialog.peer.channel_id != 0) {
                        TLRPC.TL_updateReadChannelInbox tL_updateReadChannelInbox = new TLRPC.TL_updateReadChannelInbox();
                        tL_updateReadChannelInbox.channel_id = dialog.peer.channel_id;
                        tL_updateReadChannelInbox.max_id = dialog.read_inbox_max_id;
                        tL_updateReadChannelInbox.still_unread_count = dialog.unread_count;
                        tL_updateReadHistoryInbox = tL_updateReadChannelInbox;
                    } else {
                        TLRPC.TL_updateReadHistoryInbox tL_updateReadHistoryInbox2 = new TLRPC.TL_updateReadHistoryInbox();
                        tL_updateReadHistoryInbox2.peer = dialog.peer;
                        tL_updateReadHistoryInbox2.max_id = dialog.read_inbox_max_id;
                        tL_updateReadHistoryInbox = tL_updateReadHistoryInbox2;
                    }
                    arrayList.add(tL_updateReadHistoryInbox);
                }
                Integer num2 = this.dialogs_read_outbox_max.get(Long.valueOf(dialog.id));
                if (num2 == null) {
                    num2 = 0;
                }
                this.dialogs_read_outbox_max.put(Long.valueOf(dialog.id), Integer.valueOf(Math.max(dialog.read_outbox_max_id, num2.intValue())));
                if (dialog.read_outbox_max_id > num2.intValue()) {
                    if (dialog.peer.channel_id != 0) {
                        TLRPC.TL_updateReadChannelOutbox tL_updateReadChannelOutbox = new TLRPC.TL_updateReadChannelOutbox();
                        tL_updateReadChannelOutbox.channel_id = dialog.peer.channel_id;
                        tL_updateReadChannelOutbox.max_id = dialog.read_outbox_max_id;
                        tL_updateReadHistoryOutbox = tL_updateReadChannelOutbox;
                    } else {
                        TLRPC.TL_updateReadHistoryOutbox tL_updateReadHistoryOutbox2 = new TLRPC.TL_updateReadHistoryOutbox();
                        tL_updateReadHistoryOutbox2.peer = dialog.peer;
                        tL_updateReadHistoryOutbox2.max_id = dialog.read_outbox_max_id;
                        tL_updateReadHistoryOutbox = tL_updateReadHistoryOutbox2;
                    }
                    arrayList.add(tL_updateReadHistoryOutbox);
                }
            }
            if (arrayList.isEmpty()) {
                return;
            }
            processUpdateArray(arrayList, null, null, false, 0);
        }
    }

    public void lambda$reloadMentionsCountForChannel$209(TLRPC.InputPeer inputPeer, long j, TLObject tLObject, TLRPC.TL_error tL_error) {
        TLRPC.messages_Messages messages_messages = (TLRPC.messages_Messages) tLObject;
        if (messages_messages != null) {
            int i = messages_messages.count;
            if (i == 0) {
                i = messages_messages.messages.size();
            }
            getMessagesStorage().resetMentionsCount(-inputPeer.channel_id, 0L, i);
        }
        if (j != 0) {
            getMessagesStorage().removePendingTask(j);
        }
    }

    public void lambda$reloadMentionsCountForChannels$210(ArrayList arrayList) {
        for (int i = 0; i < arrayList.size(); i++) {
            reloadMentionsCountForChannel(getInputPeer(-((Long) arrayList.get(i)).longValue()), 0L);
        }
    }

    public void lambda$reloadMessages$67(long j, ArrayList arrayList, ArrayList arrayList2) {
        ArrayList arrayList3 = (ArrayList) this.reloadingMessages.get(j);
        if (arrayList3 != null) {
            arrayList3.removeAll(arrayList);
            if (arrayList3.isEmpty()) {
                this.reloadingMessages.remove(j);
            }
        }
        ArrayList arrayList4 = (ArrayList) this.dialogMessage.get(j);
        if (arrayList4 != null) {
            for (int i = 0; i < arrayList4.size(); i++) {
                MessageObject messageObject = (MessageObject) arrayList4.get(i);
                int i2 = 0;
                while (true) {
                    if (i2 < arrayList2.size()) {
                        MessageObject messageObject2 = (MessageObject) arrayList2.get(i2);
                        if (messageObject.getId() == messageObject2.getId()) {
                            arrayList4.set(i, messageObject2);
                            if (messageObject2.messageOwner.peer_id.channel_id == 0) {
                                MessageObject messageObject3 = this.dialogMessagesByIds.get(messageObject2.getId());
                                this.dialogMessagesByIds.remove(messageObject2.getId());
                                if (messageObject3 != null) {
                                    this.dialogMessagesByIds.put(messageObject3.getId(), messageObject3);
                                }
                            }
                            getNotificationCenter().lambda$postNotificationNameOnUIThread$1(NotificationCenter.dialogsNeedReload, new Object[0]);
                        } else {
                            i2++;
                        }
                    }
                }
            }
        }
        getNotificationCenter().lambda$postNotificationNameOnUIThread$1(NotificationCenter.replaceMessagesObjects, Long.valueOf(j), arrayList2);
    }

    public void lambda$reloadMessages$68(final long j, boolean z, int i, final ArrayList arrayList, TLObject tLObject, TLRPC.TL_error tL_error) {
        if (tL_error == null) {
            TLRPC.messages_Messages messages_messages = (TLRPC.messages_Messages) tLObject;
            LongSparseArray longSparseArray = new LongSparseArray();
            for (int i2 = 0; i2 < messages_messages.users.size(); i2++) {
                TLRPC.User user = messages_messages.users.get(i2);
                longSparseArray.put(user.id, user);
            }
            LongSparseArray longSparseArray2 = new LongSparseArray();
            for (int i3 = 0; i3 < messages_messages.chats.size(); i3++) {
                TLRPC.Chat chat = messages_messages.chats.get(i3);
                longSparseArray2.put(chat.id, chat);
            }
            Integer num = this.dialogs_read_inbox_max.get(Long.valueOf(j));
            if (num == null) {
                num = Integer.valueOf(getMessagesStorage().getDialogReadMax(false, j));
                this.dialogs_read_inbox_max.put(Long.valueOf(j), num);
            }
            Integer num2 = this.dialogs_read_outbox_max.get(Long.valueOf(j));
            if (num2 == null) {
                num2 = Integer.valueOf(getMessagesStorage().getDialogReadMax(true, j));
                this.dialogs_read_outbox_max.put(Long.valueOf(j), num2);
            }
            ArrayList arrayList2 = new ArrayList();
            int i4 = 0;
            while (i4 < messages_messages.messages.size()) {
                TLRPC.Message message = messages_messages.messages.get(i4);
                message.dialog_id = j;
                if (!z) {
                    message.unread = (message.out ? num2 : num).intValue() < message.id;
                }
                Integer num3 = num;
                ArrayList arrayList3 = arrayList2;
                arrayList3.add(new MessageObject(this.currentAccount, message, longSparseArray, longSparseArray2, true, true));
                i4++;
                arrayList2 = arrayList3;
                num = num3;
            }
            final ArrayList arrayList4 = arrayList2;
            ImageLoader.saveMessagesThumbs(messages_messages.messages);
            getMessagesStorage().putMessages(messages_messages, j, -1, 0, false, i, 0L);
            AndroidUtilities.runOnUIThread(new Runnable() {
                @Override
                public final void run() {
                    MessagesController.this.lambda$reloadMessages$67(j, arrayList, arrayList4);
                }
            });
        }
    }

    public void lambda$reloadReactionsNotifySettings$192(TLObject tLObject) {
        if (tLObject instanceof TL_account.TL_reactionsNotifySettings) {
            TL_account.TL_reactionsNotifySettings tL_reactionsNotifySettings = (TL_account.TL_reactionsNotifySettings) tLObject;
            SharedPreferences.Editor edit = this.notificationsPreferences.edit();
            edit.putBoolean("EnableReactionsMessages", tL_reactionsNotifySettings.messages_notify_from != null);
            TL_account.ReactionNotificationsFrom reactionNotificationsFrom = tL_reactionsNotifySettings.messages_notify_from;
            if (reactionNotificationsFrom != null) {
                edit.putBoolean("EnableReactionsMessagesContacts", reactionNotificationsFrom instanceof TL_account.TL_reactionNotificationsFromContacts);
            }
            edit.putBoolean("EnableReactionsStories", tL_reactionsNotifySettings.stories_notify_from != null);
            TL_account.ReactionNotificationsFrom reactionNotificationsFrom2 = tL_reactionsNotifySettings.stories_notify_from;
            if (reactionNotificationsFrom2 != null) {
                edit.putBoolean("EnableReactionsStoriesContacts", reactionNotificationsFrom2 instanceof TL_account.TL_reactionNotificationsFromContacts);
            }
            edit.putBoolean("EnableReactionsPreview", tL_reactionsNotifySettings.show_previews);
            getNotificationsController().getNotificationsSettingsFacade().applySoundSettings(tL_reactionsNotifySettings.sound, edit, 0L, 0L, 4, false);
            edit.apply();
            getNotificationCenter().lambda$postNotificationNameOnUIThread$1(NotificationCenter.notificationsSettingsUpdated, new Object[0]);
        }
    }

    public void lambda$reloadReactionsNotifySettings$193(final TLObject tLObject, TLRPC.TL_error tL_error) {
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                MessagesController.this.lambda$reloadReactionsNotifySettings$192(tLObject);
            }
        });
    }

    public void lambda$reloadUser$51(TLObject tLObject, TLRPC.TL_error tL_error) {
        if (tLObject instanceof Vector) {
            ArrayList<T> arrayList = ((Vector) tLObject).objects;
            ArrayList<TLRPC.User> arrayList2 = new ArrayList<>();
            for (int i = 0; i < arrayList.size(); i++) {
                if (arrayList.get(i) instanceof TLRPC.User) {
                    arrayList2.add((TLRPC.User) arrayList.get(i));
                }
            }
            getMessagesController().putUsers(arrayList2, false);
        }
    }

    public void lambda$reloadWebPages$175(java.util.HashMap r16, java.lang.String r17, org.telegram.tgnet.TLObject r18, androidx.collection.LongSparseArray r19, long r20, int r22) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MessagesController.lambda$reloadWebPages$175(java.util.HashMap, java.lang.String, org.telegram.tgnet.TLObject, androidx.collection.LongSparseArray, long, int):void");
    }

    public void lambda$reloadWebPages$176(final HashMap hashMap, final String str, final LongSparseArray longSparseArray, final long j, final int i, final TLObject tLObject, TLRPC.TL_error tL_error) {
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                MessagesController.this.lambda$reloadWebPages$175(hashMap, str, tLObject, longSparseArray, j, i);
            }
        });
    }

    public void lambda$removeDialog$124(long j) {
        long j2 = -j;
        this.channelsPts.delete(j2);
        this.shortPollChannels.delete(j2);
        this.needShortPollChannels.delete(j2);
        this.shortPollOnlines.delete(j2);
        this.needShortPollOnlines.delete(j2);
    }

    public void lambda$removeFolderTemporarily$431(int i, boolean z) {
        int i2 = 0;
        while (i2 < this.dialogFilters.size()) {
            if (this.dialogFilters.get(i2).id == i) {
                this.dialogFilters.remove(i2);
                i2--;
            }
            i2++;
        }
        this.frozenDialogFilters = null;
        this.hiddenUndoChats.clear();
        getNotificationCenter().lambda$postNotificationNameOnUIThread$1(NotificationCenter.dialogFiltersUpdated, new Object[0]);
        if (z) {
            getNotificationCenter().lambda$postNotificationNameOnUIThread$1(NotificationCenter.dialogsNeedReload, new Object[0]);
        }
    }

    public void lambda$removeFolderTemporarily$432(boolean z) {
        this.frozenDialogFilters = null;
        this.hiddenUndoChats.clear();
        getNotificationCenter().lambda$postNotificationNameOnUIThread$1(NotificationCenter.dialogFiltersUpdated, new Object[0]);
        if (z) {
            getNotificationCenter().lambda$postNotificationNameOnUIThread$1(NotificationCenter.dialogsNeedReload, new Object[0]);
        }
    }

    public static void lambda$removeSuggestion$35(TLObject tLObject, TLRPC.TL_error tL_error) {
    }

    public int lambda$renameSavedReactionTag$436(TLRPC.TL_savedReactionTag tL_savedReactionTag, TLRPC.TL_savedReactionTag tL_savedReactionTag2) {
        int compare;
        int i = tL_savedReactionTag.count;
        int i2 = tL_savedReactionTag2.count;
        if (i != i2) {
            return i2 - i;
        }
        compare = Long.compare(getTagLongId(tL_savedReactionTag2.reaction) ^ Long.MIN_VALUE, getTagLongId(tL_savedReactionTag.reaction) ^ Long.MIN_VALUE);
        return compare;
    }

    public void lambda$reorderPinnedDialogs$340(long j, TLObject tLObject, TLRPC.TL_error tL_error) {
        if (j != 0) {
            getMessagesStorage().removePendingTask(j);
        }
    }

    public static void lambda$reportSpam$70(TLObject tLObject, TLRPC.TL_error tL_error) {
    }

    public static void lambda$reportSpam$71(TLObject tLObject, TLRPC.TL_error tL_error) {
    }

    public static void lambda$reportSpam$72(TLObject tLObject, TLRPC.TL_error tL_error) {
    }

    public void lambda$requestContactToken$427(Utilities.Callback callback) {
        callback.run(this.cachedContactToken);
        this.requestingContactToken = false;
    }

    public void lambda$requestContactToken$428(final Utilities.Callback callback, long j, long j2, TLObject tLObject, TLRPC.TL_error tL_error) {
        if (!(tLObject instanceof TLRPC.TL_exportedContactToken)) {
            this.requestingContactToken = false;
        } else {
            this.cachedContactToken = (TLRPC.TL_exportedContactToken) tLObject;
            AndroidUtilities.runOnUIThread(new Runnable() {
                @Override
                public final void run() {
                    MessagesController.this.lambda$requestContactToken$427(callback);
                }
            }, Math.max(0L, j - (System.currentTimeMillis() - j2)));
        }
    }

    public void lambda$requestIsUserPremiumBlocked$446(TLObject tLObject, ArrayList arrayList) {
        boolean z;
        if (tLObject instanceof Vector) {
            ArrayList<T> arrayList2 = ((Vector) tLObject).objects;
            z = false;
            for (int i = 0; i < Math.min(arrayList.size(), arrayList2.size()); i++) {
                long longValue = ((Long) arrayList.get(i)).longValue();
                boolean z2 = arrayList2.get(i) instanceof TLRPC.TL_boolTrue;
                Boolean bool = (Boolean) this.cachedIsUserPremiumBlocked.get(longValue);
                if (bool == null || bool.booleanValue() != z2) {
                    this.cachedIsUserPremiumBlocked.put(longValue, Boolean.valueOf(z2));
                    z = true;
                }
                TLRPC.UserFull userFull = getUserFull(longValue);
                if (userFull != null && userFull.contact_require_premium != z2) {
                    userFull.contact_require_premium = z2;
                    getMessagesStorage().updateUserInfo(userFull, true);
                } else if (userFull == null) {
                    getMessagesStorage().updateUserInfoPremiumBlocked(longValue, z2);
                }
                z = true;
            }
        } else {
            z = false;
        }
        if (z) {
            getNotificationCenter().lambda$postNotificationNameOnUIThread$1(NotificationCenter.userIsPremiumBlockedUpadted, new Object[0]);
        }
    }

    public void lambda$requestIsUserPremiumBlocked$447(final ArrayList arrayList, final TLObject tLObject, TLRPC.TL_error tL_error) {
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                MessagesController.this.lambda$requestIsUserPremiumBlocked$446(tLObject, arrayList);
            }
        });
    }

    public void lambda$resetDialogs$197(int i, int i2, int i3, int i4, TLObject tLObject, TLRPC.TL_error tL_error) {
        if (tLObject != null) {
            this.resetDialogsPinned = (TLRPC.TL_messages_peerDialogs) tLObject;
            for (int i5 = 0; i5 < this.resetDialogsPinned.dialogs.size(); i5++) {
                this.resetDialogsPinned.dialogs.get(i5).pinned = true;
            }
            resetDialogs(false, i, i2, i3, i4);
        }
    }

    public void lambda$resetDialogs$198(int i, int i2, int i3, int i4, TLObject tLObject, TLRPC.TL_error tL_error) {
        if (tL_error == null) {
            this.resetDialogsAll = (TLRPC.messages_Dialogs) tLObject;
            resetDialogs(false, i, i2, i3, i4);
        }
    }

    public void lambda$saveGif$136(Object obj, TLRPC.TL_messages_saveGif tL_messages_saveGif, TLObject tLObject, TLRPC.TL_error tL_error) {
        if (tL_error == null || !FileRefController.isFileRefError(tL_error.text)) {
            return;
        }
        getFileRefController().requestReference(obj, tL_messages_saveGif);
    }

    public void lambda$saveRecentSticker$137(Object obj, TLRPC.TL_messages_saveRecentSticker tL_messages_saveRecentSticker, TLObject tLObject, TLRPC.TL_error tL_error) {
        if (tL_error == null || !FileRefController.isFileRefError(tL_error.text)) {
            return;
        }
        getFileRefController().requestReference(obj, tL_messages_saveRecentSticker);
    }

    public void lambda$saveSavedReactionsTags$441(long j, TLRPC.TL_messages_savedReactionsTags tL_messages_savedReactionsTags) {
        SQLiteDatabase database = getMessagesStorage().getDatabase();
        SQLitePreparedStatement sQLitePreparedStatement = null;
        try {
            try {
                database.executeFast("DELETE FROM saved_reaction_tags WHERE topic_id = " + j).stepThis().dispose();
                sQLitePreparedStatement = database.executeFast("REPLACE INTO saved_reaction_tags VALUES(?, ?)");
                sQLitePreparedStatement.requery();
                NativeByteBuffer nativeByteBuffer = new NativeByteBuffer(tL_messages_savedReactionsTags.getObjectSize());
                tL_messages_savedReactionsTags.serializeToStream(nativeByteBuffer);
                sQLitePreparedStatement.bindLong(1, j);
                sQLitePreparedStatement.bindByteBuffer(2, nativeByteBuffer);
                sQLitePreparedStatement.step();
            } catch (Exception e) {
                FileLog.e(e);
                if (sQLitePreparedStatement == null) {
                    return;
                }
            }
            sQLitePreparedStatement.dispose();
        } catch (Throwable th) {
            if (sQLitePreparedStatement != null) {
                sQLitePreparedStatement.dispose();
            }
            throw th;
        }
    }

    public static void lambda$saveTheme$111(TLObject tLObject, TLRPC.TL_error tL_error) {
    }

    public void lambda$saveThemeToServer$114(String str, String str2, Theme.ThemeAccent themeAccent, Theme.ThemeInfo themeInfo) {
        if (str == null) {
            this.uploadingThemes.remove(str2);
            return;
        }
        this.uploadingThemes.put(str, themeAccent != null ? themeAccent : themeInfo);
        if (themeAccent == null) {
            themeInfo.uploadingFile = str2;
            themeInfo.uploadingThumb = str;
        } else {
            themeAccent.uploadingFile = str2;
            themeAccent.uploadingThumb = str;
        }
        getFileLoader().uploadFile(str2, false, true, 67108864);
        getFileLoader().uploadFile(str, false, true, 16777216);
    }

    public void lambda$saveThemeToServer$115(final String str, File file, final Theme.ThemeAccent themeAccent, final Theme.ThemeInfo themeInfo) {
        final String createThemePreviewImage = Theme.createThemePreviewImage(str, file != null ? file.getAbsolutePath() : null, themeAccent);
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                MessagesController.this.lambda$saveThemeToServer$114(createThemePreviewImage, str, themeAccent, themeInfo);
            }
        });
    }

    public void lambda$saveWallpaperToServer$116(long j, TLObject tLObject, TLRPC.TL_error tL_error) {
        getMessagesStorage().removePendingTask(j);
    }

    public void lambda$scheduleTranscriptionUpdate$33() {
        AndroidUtilities.cancelRunOnUIThread(this.notifyTranscriptionAudioCooldownUpdate);
        long currentTime = this.transcribeAudioTrialCooldownUntil - getConnectionsManager().getCurrentTime();
        if (currentTime > 0) {
            AndroidUtilities.runOnUIThread(this.notifyTranscriptionAudioCooldownUpdate, currentTime);
        }
    }

    public void lambda$sendBotStart$271(TLObject tLObject, TLRPC.TL_error tL_error) {
        if (tL_error != null) {
            return;
        }
        processUpdates((TLRPC.Updates) tLObject, false);
    }

    public void lambda$sendLoadPeersRequest$16(java.util.ArrayList r12, java.util.ArrayList r13, org.telegram.tgnet.TLRPC.messages_Dialogs r14, org.telegram.tgnet.TLRPC.messages_Dialogs r15, java.util.ArrayList r16, org.telegram.tgnet.TLObject r17, java.util.ArrayList r18, android.util.SparseArray r19, java.util.ArrayList r20, java.util.HashMap r21, java.util.HashSet r22, java.lang.Runnable r23, org.telegram.tgnet.TLObject r24, org.telegram.tgnet.TLRPC.TL_error r25) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MessagesController.lambda$sendLoadPeersRequest$16(java.util.ArrayList, java.util.ArrayList, org.telegram.tgnet.TLRPC$messages_Dialogs, org.telegram.tgnet.TLRPC$messages_Dialogs, java.util.ArrayList, org.telegram.tgnet.TLObject, java.util.ArrayList, android.util.SparseArray, java.util.ArrayList, java.util.HashMap, java.util.HashSet, java.lang.Runnable, org.telegram.tgnet.TLObject, org.telegram.tgnet.TLRPC$TL_error):void");
    }

    public void lambda$sendTyping$162(final int i, final long j, final long j2, TLObject tLObject, TLRPC.TL_error tL_error) {
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                MessagesController.this.lambda$sendTyping$161(i, j, j2);
            }
        });
    }

    public void lambda$sendTyping$164(final int i, final long j, final long j2, TLObject tLObject, TLRPC.TL_error tL_error) {
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                MessagesController.this.lambda$sendTyping$163(i, j, j2);
            }
        });
    }

    public void lambda$setBoostsToUnblockRestrictions$89(long j) {
        loadFullChat(j, 0, true);
    }

    public void lambda$setBoostsToUnblockRestrictions$90(final long j, TLObject tLObject, TLRPC.TL_error tL_error) {
        if (tL_error == null) {
            getMessagesController().processUpdates((TLRPC.Updates) tLObject, false);
            AndroidUtilities.runOnUIThread(new Runnable() {
                @Override
                public final void run() {
                    MessagesController.this.lambda$setBoostsToUnblockRestrictions$89(j);
                }
            }, 1000L);
        }
    }

    public void lambda$setChannelSlowMode$87(long j) {
        loadFullChat(j, 0, true);
    }

    public void lambda$setChannelSlowMode$88(final long j, TLObject tLObject, TLRPC.TL_error tL_error) {
        if (tL_error == null) {
            getMessagesController().processUpdates((TLRPC.Updates) tLObject, false);
            AndroidUtilities.runOnUIThread(new Runnable() {
                @Override
                public final void run() {
                    MessagesController.this.lambda$setChannelSlowMode$87(j);
                }
            }, 1000L);
        }
    }

    public void lambda$setChatReactions$422(long j) {
        getNotificationCenter().lambda$postNotificationNameOnUIThread$1(NotificationCenter.chatAvailableReactionsUpdated, Long.valueOf(j), 0L);
    }

    public void lambda$setChatReactions$423(final long j, TLRPC.TL_messages_setChatAvailableReactions tL_messages_setChatAvailableReactions, TLObject tLObject, TLRPC.TL_error tL_error) {
        if (tLObject != null) {
            processUpdates((TLRPC.Updates) tLObject, false);
            TLRPC.ChatFull chatFull = getChatFull(j);
            if (chatFull != null) {
                if (chatFull instanceof TLRPC.TL_chatFull) {
                    chatFull.flags |= 262144;
                }
                if (chatFull instanceof TLRPC.TL_channelFull) {
                    chatFull.flags |= 1073741824;
                }
                chatFull.available_reactions = tL_messages_setChatAvailableReactions.available_reactions;
                getMessagesStorage().updateChatInfo(chatFull, false);
            }
            AndroidUtilities.runOnUIThread(new Runnable() {
                @Override
                public final void run() {
                    MessagesController.this.lambda$setChatReactions$422(j);
                }
            });
        }
    }

    public static void lambda$setContentSettings$455(TLRPC.TL_error tL_error) {
        if (tL_error != null) {
            BulletinFactory.showError(tL_error);
        }
    }

    public static void lambda$setContentSettings$456(TLObject tLObject, final TLRPC.TL_error tL_error) {
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                MessagesController.lambda$setContentSettings$455(TLRPC.TL_error.this);
            }
        });
    }

    public void lambda$setCustomChatReactions$419(Runnable runnable, long j) {
        if (runnable != null) {
            runnable.run();
        }
        getNotificationCenter().lambda$postNotificationNameOnUIThread$1(NotificationCenter.chatAvailableReactionsUpdated, Long.valueOf(j), 0L);
    }

    public static void lambda$setCustomChatReactions$420(Utilities.Callback callback, TLRPC.TL_error tL_error) {
        if (callback != null) {
            callback.run(tL_error);
        }
    }

    public void lambda$setCustomChatReactions$421(final long j, TLRPC.TL_messages_setChatAvailableReactions tL_messages_setChatAvailableReactions, final Runnable runnable, final Utilities.Callback callback, TLObject tLObject, final TLRPC.TL_error tL_error) {
        if (tLObject == null) {
            AndroidUtilities.runOnUIThread(new Runnable() {
                @Override
                public final void run() {
                    MessagesController.lambda$setCustomChatReactions$420(Utilities.Callback.this, tL_error);
                }
            });
            return;
        }
        processUpdates((TLRPC.Updates) tLObject, false);
        TLRPC.ChatFull chatFull = getChatFull(j);
        if (chatFull != null) {
            if (chatFull instanceof TLRPC.TL_chatFull) {
                chatFull.flags |= 262144;
            }
            if (chatFull instanceof TLRPC.TL_channelFull) {
                chatFull.flags |= 1073741824;
            }
            chatFull.available_reactions = tL_messages_setChatAvailableReactions.available_reactions;
            getMessagesStorage().updateChatInfo(chatFull, false);
        }
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                MessagesController.this.lambda$setCustomChatReactions$419(runnable, j);
            }
        });
    }

    public void lambda$setDefaultBannedRole$91(long j) {
        loadFullChat(j, 0, true);
    }

    public void lambda$setDefaultBannedRole$92(TLRPC.TL_error tL_error, BaseFragment baseFragment, TLRPC.TL_messages_editChatDefaultBannedRights tL_messages_editChatDefaultBannedRights, boolean z) {
        AlertsCreator.processError(this.currentAccount, tL_error, baseFragment, tL_messages_editChatDefaultBannedRights, Boolean.valueOf(z));
    }

    public void lambda$setDefaultBannedRole$93(final long j, final BaseFragment baseFragment, final TLRPC.TL_messages_editChatDefaultBannedRights tL_messages_editChatDefaultBannedRights, final boolean z, TLObject tLObject, final TLRPC.TL_error tL_error) {
        if (tL_error != null) {
            AndroidUtilities.runOnUIThread(new Runnable() {
                @Override
                public final void run() {
                    MessagesController.this.lambda$setDefaultBannedRole$92(tL_error, baseFragment, tL_messages_editChatDefaultBannedRights, z);
                }
            });
        } else {
            processUpdates((TLRPC.Updates) tLObject, false);
            AndroidUtilities.runOnUIThread(new Runnable() {
                @Override
                public final void run() {
                    MessagesController.this.lambda$setDefaultBannedRole$91(j);
                }
            }, 1000L);
        }
    }

    public void lambda$setDefaultSendAs$254(long j, TLObject tLObject, TLRPC.TL_error tL_error) {
        long j2;
        if (tLObject instanceof TLRPC.TL_boolTrue) {
            j2 = -j;
            if (getChatFull(j2) != null) {
                return;
            }
        } else if (tL_error == null || tL_error.code != 400) {
            return;
        } else {
            j2 = -j;
        }
        loadFullChat(j2, 0, true);
    }

    public void lambda$setDialogHistoryTTL$127(TLObject tLObject, TLRPC.TL_error tL_error) {
        if (tLObject != null) {
            processUpdates((TLRPC.Updates) tLObject, false);
        }
    }

    public void lambda$setLastCreatedDialogId$50(boolean z, boolean z2, long j) {
        ArrayList<Long> arrayList = z ? this.createdScheduledDialogIds : this.createdDialogIds;
        if (!z2) {
            arrayList.remove(Long.valueOf(j));
        } else {
            if (arrayList.contains(Long.valueOf(j))) {
                return;
            }
            arrayList.add(Long.valueOf(j));
        }
    }

    public void lambda$setParticipantBannedRole$84(long j) {
        loadFullChat(j, 0, true);
    }

    public void lambda$setParticipantBannedRole$85(TLRPC.TL_error tL_error, BaseFragment baseFragment, TLRPC.TL_channels_editBanned tL_channels_editBanned, boolean z) {
        AlertsCreator.processError(this.currentAccount, tL_error, baseFragment, tL_channels_editBanned, Boolean.valueOf(z));
    }

    public void lambda$setParticipantBannedRole$86(final long j, Runnable runnable, final BaseFragment baseFragment, final TLRPC.TL_channels_editBanned tL_channels_editBanned, final boolean z, TLObject tLObject, final TLRPC.TL_error tL_error) {
        if (tL_error != null) {
            AndroidUtilities.runOnUIThread(new Runnable() {
                @Override
                public final void run() {
                    MessagesController.this.lambda$setParticipantBannedRole$85(tL_error, baseFragment, tL_channels_editBanned, z);
                }
            });
            return;
        }
        processUpdates((TLRPC.Updates) tLObject, false);
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                MessagesController.this.lambda$setParticipantBannedRole$84(j);
            }
        }, 1000L);
        if (runnable != null) {
            AndroidUtilities.runOnUIThread(runnable);
        }
    }

    public void lambda$setUserAdminRole$100(long j, Runnable runnable) {
        loadFullChat(j, 0, true);
        if (runnable != null) {
            runnable.run();
        }
    }

    public void lambda$setUserAdminRole$101(TLRPC.TL_error tL_error, BaseFragment baseFragment, TLRPC.TL_messages_editChatAdmin tL_messages_editChatAdmin) {
        AlertsCreator.processError(this.currentAccount, tL_error, baseFragment, tL_messages_editChatAdmin, Boolean.FALSE);
    }

    public void lambda$setUserAdminRole$103(final long j, final Runnable runnable, final BaseFragment baseFragment, final TLRPC.TL_messages_editChatAdmin tL_messages_editChatAdmin, final ErrorDelegate errorDelegate, TLObject tLObject, final TLRPC.TL_error tL_error) {
        if (tL_error == null) {
            AndroidUtilities.runOnUIThread(new Runnable() {
                @Override
                public final void run() {
                    MessagesController.this.lambda$setUserAdminRole$100(j, runnable);
                }
            }, 1000L);
            return;
        }
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                MessagesController.this.lambda$setUserAdminRole$101(tL_error, baseFragment, tL_messages_editChatAdmin);
            }
        });
        if (errorDelegate != null) {
            AndroidUtilities.runOnUIThread(new Runnable() {
                @Override
                public final void run() {
                    MessagesController.ErrorDelegate.this.run(tL_error);
                }
            });
        }
    }

    public void lambda$setUserAdminRole$104(TLRPC.TL_messages_editChatAdmin tL_messages_editChatAdmin, RequestDelegate requestDelegate) {
        getConnectionsManager().sendRequest(tL_messages_editChatAdmin, requestDelegate);
    }

    public void lambda$setUserAdminRole$94(long j, Runnable runnable) {
        loadFullChat(j, 0, true);
        if (runnable != null) {
            runnable.run();
        }
    }

    public void lambda$setUserAdminRole$95(TLRPC.User user, TLRPC.Chat chat, ErrorDelegate errorDelegate, TLRPC.TL_error tL_error) {
        BaseFragment lastFragment = LaunchActivity.getLastFragment();
        if (lastFragment != null && lastFragment.getParentActivity() != null) {
            LimitReachedBottomSheet limitReachedBottomSheet = new LimitReachedBottomSheet(lastFragment, lastFragment.getParentActivity(), 11, this.currentAccount, null);
            ArrayList arrayList = new ArrayList();
            arrayList.add(user);
            limitReachedBottomSheet.setRestrictedUsers(chat, arrayList, null, null);
            limitReachedBottomSheet.show();
        }
        errorDelegate.run(tL_error);
    }

    public void lambda$setUserAdminRole$96(TLRPC.TL_error tL_error, BaseFragment baseFragment, TLRPC.TL_channels_editAdmin tL_channels_editAdmin, boolean z) {
        AlertsCreator.processError(this.currentAccount, tL_error, baseFragment, tL_channels_editAdmin, Boolean.valueOf(z));
    }

    public void lambda$setUserAdminRole$98(final long j, final Runnable runnable, final TLRPC.Chat chat, final TLRPC.User user, final ErrorDelegate errorDelegate, final BaseFragment baseFragment, final TLRPC.TL_channels_editAdmin tL_channels_editAdmin, final boolean z, TLObject tLObject, final TLRPC.TL_error tL_error) {
        if (tL_error == null) {
            processUpdates((TLRPC.Updates) tLObject, false);
            AndroidUtilities.runOnUIThread(new Runnable() {
                @Override
                public final void run() {
                    MessagesController.this.lambda$setUserAdminRole$94(j, runnable);
                }
            }, 1000L);
        } else {
            if ("USER_PRIVACY_RESTRICTED".equals(tL_error.text) && ChatObject.canUserDoAdminAction(chat, 3)) {
                AndroidUtilities.runOnUIThread(new Runnable() {
                    @Override
                    public final void run() {
                        MessagesController.this.lambda$setUserAdminRole$95(user, chat, errorDelegate, tL_error);
                    }
                });
                return;
            }
            AndroidUtilities.runOnUIThread(new Runnable() {
                @Override
                public final void run() {
                    MessagesController.this.lambda$setUserAdminRole$96(tL_error, baseFragment, tL_channels_editAdmin, z);
                }
            });
            if (errorDelegate != null) {
                AndroidUtilities.runOnUIThread(new Runnable() {
                    @Override
                    public final void run() {
                        MessagesController.ErrorDelegate.this.run(tL_error);
                    }
                });
            }
        }
    }

    public void lambda$setUserAdminRole$99(TLRPC.TL_channels_editAdmin tL_channels_editAdmin, RequestDelegate requestDelegate) {
        getConnectionsManager().sendRequest(tL_channels_editAdmin, requestDelegate);
    }

    public static void lambda$startShortPoll$308(Consumer consumer) {
        consumer.accept(Boolean.TRUE);
    }

    public static void lambda$startShortPoll$309(Consumer consumer) {
        consumer.accept(Boolean.FALSE);
    }

    public void lambda$startShortPoll$310(TLRPC.Chat chat, boolean z, int i, final Consumer consumer) {
        ArrayList arrayList = (ArrayList) this.needShortPollChannels.get(chat.id);
        ArrayList arrayList2 = (ArrayList) this.needShortPollOnlines.get(chat.id);
        if (z) {
            if (arrayList != null) {
                arrayList.remove(Integer.valueOf(i));
            }
            if (arrayList == null || arrayList.isEmpty()) {
                this.needShortPollChannels.delete(chat.id);
            }
            if (chat.megagroup) {
                if (arrayList2 != null) {
                    arrayList2.remove(Integer.valueOf(i));
                }
                if (arrayList2 == null || arrayList2.isEmpty()) {
                    this.needShortPollOnlines.delete(chat.id);
                    return;
                }
                return;
            }
            return;
        }
        if (arrayList == null) {
            arrayList = new ArrayList();
            this.needShortPollChannels.put(chat.id, arrayList);
        }
        if (!arrayList.contains(Integer.valueOf(i))) {
            arrayList.add(Integer.valueOf(i));
        }
        if (this.shortPollChannels.indexOfKey(chat.id) < 0) {
            if (consumer != null) {
                AndroidUtilities.runOnUIThread(new Runnable() {
                    @Override
                    public final void run() {
                        MessagesController.lambda$startShortPoll$308(Consumer.this);
                    }
                });
            }
            getChannelDifference(chat.id, 3, 0L, null);
        } else if (consumer != null) {
            AndroidUtilities.runOnUIThread(new Runnable() {
                @Override
                public final void run() {
                    MessagesController.lambda$startShortPoll$309(Consumer.this);
                }
            });
        }
        if (chat.megagroup) {
            if (arrayList2 == null) {
                arrayList2 = new ArrayList();
                this.needShortPollOnlines.put(chat.id, arrayList2);
            }
            if (!arrayList2.contains(Integer.valueOf(i))) {
                arrayList2.add(Integer.valueOf(i));
            }
            if (this.shortPollOnlines.indexOfKey(chat.id) < 0) {
                this.shortPollOnlines.put(chat.id, 0);
            }
        }
    }

    public void lambda$toggleChannelForum$263() {
        getNotificationCenter().lambda$postNotificationNameOnUIThread$1(NotificationCenter.updateInterfaces, Integer.valueOf(UPDATE_MASK_CHAT));
    }

    public void lambda$toggleChannelForum$264(TLObject tLObject, TLRPC.TL_error tL_error) {
        if (tLObject != null) {
            processUpdates((TLRPC.Updates) tLObject, false);
            AndroidUtilities.runOnUIThread(new Runnable() {
                @Override
                public final void run() {
                    MessagesController.this.lambda$toggleChannelForum$263();
                }
            });
        }
    }

    public void lambda$toggleChannelInvitesHistory$265() {
        getNotificationCenter().lambda$postNotificationNameOnUIThread$1(NotificationCenter.updateInterfaces, Integer.valueOf(UPDATE_MASK_CHAT));
    }

    public void lambda$toggleChannelInvitesHistory$266(TLObject tLObject, TLRPC.TL_error tL_error) {
        if (tLObject != null) {
            processUpdates((TLRPC.Updates) tLObject, false);
            AndroidUtilities.runOnUIThread(new Runnable() {
                @Override
                public final void run() {
                    MessagesController.this.lambda$toggleChannelInvitesHistory$265();
                }
            });
        }
    }

    public void lambda$toggleChannelSignatures$261() {
        getNotificationCenter().lambda$postNotificationNameOnUIThread$1(NotificationCenter.updateInterfaces, Integer.valueOf(UPDATE_MASK_CHAT));
    }

    public void lambda$toggleChannelSignatures$262(TLObject tLObject, TLRPC.TL_error tL_error) {
        if (tLObject != null) {
            processUpdates((TLRPC.Updates) tLObject, false);
            AndroidUtilities.runOnUIThread(new Runnable() {
                @Override
                public final void run() {
                    MessagesController.this.lambda$toggleChannelSignatures$261();
                }
            });
        }
    }

    public void lambda$toggleChatJoinRequest$259() {
        getNotificationCenter().lambda$postNotificationNameOnUIThread$1(NotificationCenter.updateInterfaces, Integer.valueOf(UPDATE_MASK_CHAT));
    }

    public void lambda$toggleChatJoinRequest$260(Runnable runnable, Runnable runnable2, TLObject tLObject, TLRPC.TL_error tL_error) {
        if (tLObject != null) {
            processUpdates((TLRPC.Updates) tLObject, false);
            AndroidUtilities.runOnUIThread(new Runnable() {
                @Override
                public final void run() {
                    MessagesController.this.lambda$toggleChatJoinRequest$259();
                }
            });
            if (runnable == null) {
                return;
            }
        } else if (tL_error != null && !"CHAT_NOT_MODIFIED".equals(tL_error.text)) {
            if (runnable2 != null) {
                runnable2.run();
                return;
            }
            return;
        } else if (runnable == null) {
            return;
        }
        runnable.run();
    }

    public void lambda$toggleChatJoinToSend$257() {
        getNotificationCenter().lambda$postNotificationNameOnUIThread$1(NotificationCenter.updateInterfaces, Integer.valueOf(UPDATE_MASK_CHAT));
    }

    public void lambda$toggleChatJoinToSend$258(Runnable runnable, Runnable runnable2, TLObject tLObject, TLRPC.TL_error tL_error) {
        if (tLObject != null) {
            processUpdates((TLRPC.Updates) tLObject, false);
            AndroidUtilities.runOnUIThread(new Runnable() {
                @Override
                public final void run() {
                    MessagesController.this.lambda$toggleChatJoinToSend$257();
                }
            });
            if (runnable == null) {
                return;
            }
        } else if (tL_error != null && !"CHAT_NOT_MODIFIED".equals(tL_error.text)) {
            if (runnable2 != null) {
                runnable2.run();
                return;
            }
            return;
        } else if (runnable == null) {
            return;
        }
        runnable.run();
    }

    public void lambda$toggleChatNoForwards$255() {
        getNotificationCenter().lambda$postNotificationNameOnUIThread$1(NotificationCenter.updateInterfaces, Integer.valueOf(UPDATE_MASK_CHAT));
    }

    public void lambda$toggleChatNoForwards$256(TLObject tLObject, TLRPC.TL_error tL_error) {
        if (tLObject != null) {
            processUpdates((TLRPC.Updates) tLObject, false);
            AndroidUtilities.runOnUIThread(new Runnable() {
                @Override
                public final void run() {
                    MessagesController.this.lambda$toggleChatNoForwards$255();
                }
            });
        }
    }

    public static void lambda$unblockPeer$105(Runnable runnable) {
        if (runnable != null) {
            runnable.run();
        }
    }

    public static void lambda$unblockPeer$106(final Runnable runnable, TLObject tLObject, TLRPC.TL_error tL_error) {
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                MessagesController.lambda$unblockPeer$105(runnable);
            }
        });
    }

    public void lambda$unpinAllMessages$121(TLRPC.Chat chat, TLRPC.User user, TLObject tLObject, TLRPC.TL_error tL_error) {
        if (tLObject != null) {
            TLRPC.TL_messages_affectedHistory tL_messages_affectedHistory = (TLRPC.TL_messages_affectedHistory) tLObject;
            if (ChatObject.isChannel(chat)) {
                processNewChannelDifferenceParams(tL_messages_affectedHistory.pts, tL_messages_affectedHistory.pts_count, chat.id);
            } else {
                processNewDifferenceParams(-1, tL_messages_affectedHistory.pts, -1, tL_messages_affectedHistory.pts_count);
            }
            new ArrayList();
            getMessagesStorage().updatePinnedMessages(chat != null ? -chat.id : user.id, null, false, 0, 0, false, null);
        }
    }

    public static void lambda$unregistedPush$297(TLObject tLObject, TLRPC.TL_error tL_error) {
    }

    public void lambda$updateChannelUserName$269(long j, String str, Runnable runnable) {
        TLRPC.Chat chat = getChat(Long.valueOf(j));
        if (chat.usernames != null) {
            for (int i = 0; i < chat.usernames.size(); i++) {
                TLRPC.TL_username tL_username = chat.usernames.get(i);
                if (tL_username != null && tL_username.editable) {
                    tL_username.username = str;
                    break;
                }
            }
        }
        ArrayList<TLRPC.TL_username> arrayList = chat.usernames;
        if (arrayList == null || arrayList.size() >= 1) {
            if (chat.usernames == null) {
                chat.usernames = new ArrayList<>();
            }
            TLRPC.TL_username tL_username2 = new TLRPC.TL_username();
            tL_username2.username = str;
            tL_username2.active = true;
            tL_username2.editable = true;
            chat.usernames.add(0, tL_username2);
        } else {
            chat.username = str;
        }
        ArrayList arrayList2 = new ArrayList();
        arrayList2.add(chat);
        getMessagesStorage().putUsersAndChats(null, arrayList2, true, true);
        getNotificationCenter().lambda$postNotificationNameOnUIThread$1(NotificationCenter.updateInterfaces, Integer.valueOf(UPDATE_MASK_CHAT));
        if (runnable != null) {
            runnable.run();
        }
    }

    public void lambda$updateChannelUserName$270(final long j, final String str, final Runnable runnable, BaseFragment baseFragment, TLRPC.TL_channels_updateUsername tL_channels_updateUsername, Runnable runnable2, TLObject tLObject, TLRPC.TL_error tL_error) {
        if ((tLObject instanceof TLRPC.TL_boolTrue) || (tL_error != null && "USERNAME_NOT_MODIFIED".equals(tL_error.text))) {
            AndroidUtilities.runOnUIThread(new Runnable() {
                @Override
                public final void run() {
                    MessagesController.this.lambda$updateChannelUserName$269(j, str, runnable);
                }
            });
            return;
        }
        AlertsCreator.processError(UserConfig.selectedAccount, tL_error, baseFragment, tL_channels_updateUsername, new Object[0]);
        if (runnable2 != null) {
            runnable2.run();
        }
    }

    public void lambda$updateChatAbout$267(TLRPC.ChatFull chatFull, String str) {
        chatFull.about = str;
        getMessagesStorage().updateChatInfo(chatFull, false);
        NotificationCenter notificationCenter = getNotificationCenter();
        int i = NotificationCenter.chatInfoDidLoad;
        Boolean bool = Boolean.FALSE;
        notificationCenter.lambda$postNotificationNameOnUIThread$1(i, chatFull, 0, bool, bool);
    }

    public void lambda$updateChatAbout$268(final TLRPC.ChatFull chatFull, final String str, TLObject tLObject, TLRPC.TL_error tL_error) {
        if (!(tLObject instanceof TLRPC.TL_boolTrue) || chatFull == null) {
            return;
        }
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                MessagesController.this.lambda$updateChatAbout$267(chatFull, str);
            }
        });
    }

    public void lambda$updateConfig$36(org.telegram.tgnet.TLRPC.TL_config r13) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MessagesController.lambda$updateConfig$36(org.telegram.tgnet.TLRPC$TL_config):void");
    }

    public static void lambda$updateEmojiStatus$29(TLObject tLObject, TLRPC.TL_error tL_error) {
        boolean z = tLObject instanceof TLRPC.TL_boolTrue;
    }

    public void lambda$updateEmojiStatusUntil$426() {
        getNotificationCenter().lambda$postNotificationNameOnUIThread$1(NotificationCenter.updateInterfaces, Integer.valueOf(UPDATE_MASK_EMOJI_STATUS));
        updateEmojiStatusUntil();
    }

    public void lambda$updateInterfaceWithMessages$397(TLRPC.Dialog dialog, int i, long j, int i2) {
        if (i2 == -1) {
            if (i <= 0 || DialogObject.isEncryptedDialog(j)) {
                return;
            }
            loadUnknownDialog(getInputPeer(j), 0L);
            return;
        }
        if (i2 != 0) {
            dialog.folder_id = i2;
            sortDialogs(null);
            getNotificationCenter().lambda$postNotificationNameOnUIThread$1(NotificationCenter.dialogsNeedReload, Boolean.TRUE);
        }
    }

    public void lambda$updatePrintingStrings$160(LongSparseArray longSparseArray, LongSparseArray longSparseArray2) {
        this.printingStrings = longSparseArray;
        this.printingStringsTypes = longSparseArray2;
    }

    public int lambda$updateSavedReactionTags$435(TLRPC.TL_savedReactionTag tL_savedReactionTag, TLRPC.TL_savedReactionTag tL_savedReactionTag2) {
        int compare;
        int i = tL_savedReactionTag.count;
        int i2 = tL_savedReactionTag2.count;
        if (i != i2) {
            return i2 - i;
        }
        compare = Long.compare(getTagLongId(tL_savedReactionTag2.reaction) ^ Long.MIN_VALUE, getTagLongId(tL_savedReactionTag.reaction) ^ Long.MIN_VALUE);
        return compare;
    }

    public void lambda$updateTimerProc$142(TLObject tLObject, TLRPC.TL_error tL_error) {
        if (tL_error == null) {
            this.lastStatusUpdateTime = System.currentTimeMillis();
            this.offlineSent = false;
            this.statusSettingState = 0;
        } else {
            long j = this.lastStatusUpdateTime;
            if (j != 0) {
                this.lastStatusUpdateTime = j + 5000;
            }
        }
        this.statusRequest = 0;
    }

    public void lambda$updateTimerProc$143(TLObject tLObject, TLRPC.TL_error tL_error) {
        if (tL_error == null) {
            this.offlineSent = true;
        } else {
            long j = this.lastStatusUpdateTime;
            if (j != 0) {
                this.lastStatusUpdateTime = j + 5000;
            }
        }
        this.statusRequest = 0;
    }

    public void lambda$updateTimerProc$144(TLRPC.TL_messages_messageViews tL_messages_messageViews, LongSparseArray longSparseArray, LongSparseArray longSparseArray2, LongSparseArray longSparseArray3) {
        putUsers(tL_messages_messageViews.users, false);
        putChats(tL_messages_messageViews.chats, false);
        getNotificationCenter().lambda$postNotificationNameOnUIThread$1(NotificationCenter.didUpdateMessagesViews, longSparseArray, longSparseArray2, longSparseArray3, Boolean.FALSE);
    }

    public void lambda$updateTimerProc$145(long j, TLRPC.TL_messages_getMessagesViews tL_messages_getMessagesViews, TLObject tLObject, TLRPC.TL_error tL_error) {
        if (tLObject != null) {
            final TLRPC.TL_messages_messageViews tL_messages_messageViews = (TLRPC.TL_messages_messageViews) tLObject;
            final LongSparseArray longSparseArray = new LongSparseArray();
            final LongSparseArray longSparseArray2 = new LongSparseArray();
            final LongSparseArray longSparseArray3 = new LongSparseArray();
            SparseIntArray sparseIntArray = (SparseIntArray) longSparseArray.get(j);
            SparseIntArray sparseIntArray2 = (SparseIntArray) longSparseArray2.get(j);
            SparseArray sparseArray = (SparseArray) longSparseArray3.get(j);
            for (int i = 0; i < tL_messages_getMessagesViews.id.size() && i < tL_messages_messageViews.views.size(); i++) {
                TLRPC.TL_messageViews tL_messageViews = tL_messages_messageViews.views.get(i);
                if ((1 & tL_messageViews.flags) != 0) {
                    if (sparseIntArray == null) {
                        sparseIntArray = new SparseIntArray();
                        longSparseArray.put(j, sparseIntArray);
                    }
                    sparseIntArray.put(tL_messages_getMessagesViews.id.get(i).intValue(), tL_messageViews.views);
                }
                if ((tL_messageViews.flags & 2) != 0) {
                    if (sparseIntArray2 == null) {
                        sparseIntArray2 = new SparseIntArray();
                        longSparseArray2.put(j, sparseIntArray2);
                    }
                    sparseIntArray2.put(tL_messages_getMessagesViews.id.get(i).intValue(), tL_messageViews.forwards);
                }
                if ((tL_messageViews.flags & 4) != 0) {
                    if (sparseArray == null) {
                        sparseArray = new SparseArray();
                        longSparseArray3.put(j, sparseArray);
                    }
                    sparseArray.put(tL_messages_getMessagesViews.id.get(i).intValue(), tL_messageViews.replies);
                }
            }
            getMessagesStorage().putUsersAndChats(tL_messages_messageViews.users, tL_messages_messageViews.chats, true, true);
            getMessagesStorage().putChannelViews(longSparseArray, longSparseArray2, longSparseArray3, false);
            AndroidUtilities.runOnUIThread(new Runnable() {
                @Override
                public final void run() {
                    MessagesController.this.lambda$updateTimerProc$144(tL_messages_messageViews, longSparseArray, longSparseArray2, longSparseArray3);
                }
            });
        }
    }

    public void lambda$updateTimerProc$146(boolean z, TLObject tLObject, TLRPC.TL_error tL_error) {
        TLRPC.Poll poll;
        if (tL_error == null) {
            TLRPC.Updates updates = (TLRPC.Updates) tLObject;
            if (z) {
                for (int i = 0; i < updates.updates.size(); i++) {
                    TLRPC.Update update = updates.updates.get(i);
                    if ((update instanceof TLRPC.TL_updateMessagePoll) && (poll = ((TLRPC.TL_updateMessagePoll) update).poll) != null && !poll.closed) {
                        this.lastViewsCheckTime = System.currentTimeMillis() - 4000;
                    }
                }
            }
            processUpdates(updates, false);
        }
    }

    public void lambda$updateTimerProc$147(int r19) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MessagesController.lambda$updateTimerProc$147(int):void");
    }

    public void lambda$updateTimerProc$148() {
        getNotificationCenter().lambda$postNotificationNameOnUIThread$1(NotificationCenter.updateInterfaces, Integer.valueOf(UPDATE_MASK_STATUS));
    }

    public void lambda$updateTimerProc$149(long j, TLRPC.TL_chatOnlines tL_chatOnlines) {
        getNotificationCenter().lambda$postNotificationNameOnUIThread$1(NotificationCenter.chatOnlineCountDidLoad, Long.valueOf(j), Integer.valueOf(tL_chatOnlines.onlines));
    }

    public void lambda$updateTimerProc$150(final long j, TLObject tLObject, TLRPC.TL_error tL_error) {
        if (tLObject != null) {
            final TLRPC.TL_chatOnlines tL_chatOnlines = (TLRPC.TL_chatOnlines) tLObject;
            getMessagesStorage().updateChatOnlineCount(j, tL_chatOnlines.onlines);
            AndroidUtilities.runOnUIThread(new Runnable() {
                @Override
                public final void run() {
                    MessagesController.this.lambda$updateTimerProc$149(j, tL_chatOnlines);
                }
            });
        }
    }

    public void lambda$updateTimerProc$151() {
        getNotificationCenter().lambda$postNotificationNameOnUIThread$1(NotificationCenter.updateInterfaces, Integer.valueOf(UPDATE_MASK_USER_PRINT));
    }

    public void loadMessagesInternal(final long j, final long j2, final boolean z, final int i, final int i2, final int i3, boolean z2, final int i4, final int i5, final int i6, final int i7, final int i8, final long j3, final int i9, final int i10, final int i11, final int i12, final boolean z3, final int i13, boolean z4, final boolean z5, final boolean z6, Timer timer, long j4) {
        Timer timer2;
        int i14;
        final int i15;
        int sendRequest;
        ConnectionsManager connectionsManager;
        int i16;
        TLRPC.TL_forumTopic findTopic;
        int i17;
        if (BuildVars.LOGS_ENABLED) {
            FileLog.d("load messages in chat " + j + " topic_id " + j3 + " count " + i + " max_id " + i2 + " cache " + z2 + " mindate = " + i4 + " guid " + i5 + " load_type " + i6 + " last_message_id " + i7 + " mode " + i8 + " index " + i9 + " firstUnread " + i10 + " unread_count " + i11 + " last_date " + i12 + " queryFromServer " + z3 + " isTopic " + z6);
        }
        if (BuildVars.LOGS_ENABLED && timer == null && i8 == 0) {
            timer2 = new Timer("MessageLoaderLogger dialogId=" + j + " index=" + i9 + " count=" + i);
        } else {
            timer2 = timer;
        }
        if ((j3 == 0 || z6 || i8 == 3 || i8 == 5) && i8 != 2 && (z2 || DialogObject.isEncryptedDialog(j))) {
            getMessagesStorage().getMessages(j, j2, z, i, i2, i3, i4, i5, i6, i8, j3, i9, z5, z6, timer2);
            return;
        }
        if (i8 == 5) {
            TLRPC.TL_messages_getQuickReplyMessages tL_messages_getQuickReplyMessages = new TLRPC.TL_messages_getQuickReplyMessages();
            tL_messages_getQuickReplyMessages.shortcut_id = (int) j3;
            tL_messages_getQuickReplyMessages.hash = j4;
            getConnectionsManager().bindRequestToGuid(getConnectionsManager().sendRequest(tL_messages_getQuickReplyMessages, new RequestDelegate() {
                @Override
                public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                    MessagesController.this.lambda$loadMessagesInternal$165(j, j2, i, i2, i3, i5, i10, i7, i11, i12, i6, i8, j3, i9, z3, i13, z5, z6, tLObject, tL_error);
                }
            }), i5);
            return;
        }
        if (j3 != 0) {
            if (i8 == 3) {
                final TLRPC.TL_messages_getSavedHistory tL_messages_getSavedHistory = new TLRPC.TL_messages_getSavedHistory();
                tL_messages_getSavedHistory.peer = getInputPeer(j3);
                if (i6 == 4) {
                    i17 = (-i) + 5;
                } else {
                    if (i6 != 3) {
                        if (i6 == 1) {
                            tL_messages_getSavedHistory.add_offset = (-i) - 1;
                        } else if (i6 == 2 && i2 != 0) {
                            i17 = (-i) + 6;
                        } else if (j < 0 && i2 != 0 && ChatObject.isChannel(getChat(Long.valueOf(-j)))) {
                            tL_messages_getSavedHistory.add_offset = -1;
                            tL_messages_getSavedHistory.limit++;
                        }
                        tL_messages_getSavedHistory.limit = i;
                        tL_messages_getSavedHistory.offset_id = i2;
                        tL_messages_getSavedHistory.offset_date = i3;
                        getConnectionsManager().bindRequestToGuid(getConnectionsManager().sendRequest(tL_messages_getSavedHistory, new RequestDelegate() {
                            @Override
                            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                                MessagesController.this.lambda$loadMessagesInternal$167(j, i, i2, i3, j2, i5, i10, i7, i11, i12, i6, i8, j3, i9, z3, i13, z5, z6, tL_messages_getSavedHistory, tLObject, tL_error);
                            }
                        }), i5);
                        return;
                    }
                    i17 = (-i) / 2;
                }
                tL_messages_getSavedHistory.add_offset = i17;
                tL_messages_getSavedHistory.limit = i;
                tL_messages_getSavedHistory.offset_id = i2;
                tL_messages_getSavedHistory.offset_date = i3;
                getConnectionsManager().bindRequestToGuid(getConnectionsManager().sendRequest(tL_messages_getSavedHistory, new RequestDelegate() {
                    @Override
                    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                        MessagesController.this.lambda$loadMessagesInternal$167(j, i, i2, i3, j2, i5, i10, i7, i11, i12, i6, i8, j3, i9, z3, i13, z5, z6, tL_messages_getSavedHistory, tLObject, tL_error);
                    }
                }), i5);
                return;
            }
            if (z4 && z6 && i6 == 2 && i7 == 0 && (findTopic = this.topicsController.findTopic(-j, j3)) != null) {
                loadMessagesInternal(j, j2, z, i, i2, i3, false, i4, i5, i6, findTopic.top_message, i8, j3, i9, i10, findTopic.unread_count, i12, z3, findTopic.unread_mentions_count, false, z5, z6, timer2, 0L);
                return;
            }
            if (i8 != 0) {
                return;
            }
            final TLRPC.TL_messages_getReplies tL_messages_getReplies = new TLRPC.TL_messages_getReplies();
            tL_messages_getReplies.peer = getInputPeer(j);
            tL_messages_getReplies.msg_id = (int) j3;
            tL_messages_getReplies.offset_date = i3;
            if (i6 == 4) {
                i16 = (-i) + 5;
            } else if (i6 == 3) {
                i16 = (-i) / 2;
            } else {
                if (i6 == 1) {
                    tL_messages_getReplies.add_offset = (-i) - 1;
                } else if (i6 == 2 && i2 != 0) {
                    i16 = (-i) + 10;
                } else if (j < 0 && i2 != 0) {
                    if (ChatObject.isChannel(getChat(Long.valueOf(-j)))) {
                        tL_messages_getReplies.add_offset = -1;
                        tL_messages_getReplies.limit++;
                    }
                    tL_messages_getReplies.limit = i;
                    tL_messages_getReplies.offset_id = i2;
                    tL_messages_getReplies.hash = j4;
                    System.currentTimeMillis();
                    sendRequest = getConnectionsManager().sendRequest(tL_messages_getReplies, new RequestDelegate() {
                        @Override
                        public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                            MessagesController.this.lambda$loadMessagesInternal$169(i, i2, i3, i10, i6, j, j2, i5, i7, i11, i12, i8, j3, i9, z3, i13, z5, z6, tL_messages_getReplies, tLObject, tL_error);
                        }
                    });
                    connectionsManager = getConnectionsManager();
                    i15 = i5;
                }
                tL_messages_getReplies.limit = i;
                tL_messages_getReplies.offset_id = i2;
                tL_messages_getReplies.hash = j4;
                System.currentTimeMillis();
                sendRequest = getConnectionsManager().sendRequest(tL_messages_getReplies, new RequestDelegate() {
                    @Override
                    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                        MessagesController.this.lambda$loadMessagesInternal$169(i, i2, i3, i10, i6, j, j2, i5, i7, i11, i12, i8, j3, i9, z3, i13, z5, z6, tL_messages_getReplies, tLObject, tL_error);
                    }
                });
                connectionsManager = getConnectionsManager();
                i15 = i5;
            }
            tL_messages_getReplies.add_offset = i16;
            tL_messages_getReplies.limit = i;
            tL_messages_getReplies.offset_id = i2;
            tL_messages_getReplies.hash = j4;
            System.currentTimeMillis();
            sendRequest = getConnectionsManager().sendRequest(tL_messages_getReplies, new RequestDelegate() {
                @Override
                public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                    MessagesController.this.lambda$loadMessagesInternal$169(i, i2, i3, i10, i6, j, j2, i5, i7, i11, i12, i8, j3, i9, z3, i13, z5, z6, tL_messages_getReplies, tLObject, tL_error);
                }
            });
            connectionsManager = getConnectionsManager();
            i15 = i5;
        } else {
            if (i8 == 2) {
                return;
            }
            if (i8 != 1) {
                if (z4 && ((i6 == 3 || i6 == 2) && i7 == 0)) {
                    final TLRPC.TL_messages_getPeerDialogs tL_messages_getPeerDialogs = new TLRPC.TL_messages_getPeerDialogs();
                    TLRPC.InputPeer inputPeer = getInputPeer(j);
                    TLRPC.TL_inputDialogPeer tL_inputDialogPeer = new TLRPC.TL_inputDialogPeer();
                    tL_inputDialogPeer.peer = inputPeer;
                    tL_messages_getPeerDialogs.peers.add(tL_inputDialogPeer);
                    getConnectionsManager().sendRequest(tL_messages_getPeerDialogs, new RequestDelegate() {
                        @Override
                        public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                            MessagesController.this.lambda$loadMessagesInternal$172(j, j2, z, i, i2, i3, i4, i5, i6, j3, i9, i10, i12, z3, z5, z6, tL_messages_getPeerDialogs, tLObject, tL_error);
                        }
                    });
                    return;
                }
                final TLRPC.TL_messages_getHistory tL_messages_getHistory = new TLRPC.TL_messages_getHistory();
                tL_messages_getHistory.peer = getInputPeer(j);
                if (i6 == 4) {
                    i14 = (-i) + 5;
                } else {
                    if (i6 != 3) {
                        if (i6 == 1) {
                            tL_messages_getHistory.add_offset = (-i) - 1;
                        } else if (i6 == 2 && i2 != 0) {
                            i14 = (-i) + 6;
                        } else if (j < 0 && i2 != 0) {
                            if (ChatObject.isChannel(getChat(Long.valueOf(-j)))) {
                                tL_messages_getHistory.add_offset = -1;
                                tL_messages_getHistory.limit++;
                            }
                            tL_messages_getHistory.limit = i;
                            tL_messages_getHistory.offset_id = i2;
                            tL_messages_getHistory.offset_date = i3;
                            getConnectionsManager().bindRequestToGuid(getConnectionsManager().sendRequest(tL_messages_getHistory, new RequestDelegate() {
                                @Override
                                public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                                    MessagesController.this.lambda$loadMessagesInternal$174(j, i, i2, i3, j2, i5, i10, i7, i11, i12, i6, i8, j3, i9, z3, i13, z5, z6, tL_messages_getHistory, tLObject, tL_error);
                                }
                            }), i5);
                            return;
                        }
                        tL_messages_getHistory.limit = i;
                        tL_messages_getHistory.offset_id = i2;
                        tL_messages_getHistory.offset_date = i3;
                        getConnectionsManager().bindRequestToGuid(getConnectionsManager().sendRequest(tL_messages_getHistory, new RequestDelegate() {
                            @Override
                            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                                MessagesController.this.lambda$loadMessagesInternal$174(j, i, i2, i3, j2, i5, i10, i7, i11, i12, i6, i8, j3, i9, z3, i13, z5, z6, tL_messages_getHistory, tLObject, tL_error);
                            }
                        }), i5);
                        return;
                    }
                    i14 = (-i) / 2;
                }
                tL_messages_getHistory.add_offset = i14;
                tL_messages_getHistory.limit = i;
                tL_messages_getHistory.offset_id = i2;
                tL_messages_getHistory.offset_date = i3;
                getConnectionsManager().bindRequestToGuid(getConnectionsManager().sendRequest(tL_messages_getHistory, new RequestDelegate() {
                    @Override
                    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                        MessagesController.this.lambda$loadMessagesInternal$174(j, i, i2, i3, j2, i5, i10, i7, i11, i12, i6, i8, j3, i9, z3, i13, z5, z6, tL_messages_getHistory, tLObject, tL_error);
                    }
                }), i5);
                return;
            }
            TLRPC.TL_messages_getScheduledHistory tL_messages_getScheduledHistory = new TLRPC.TL_messages_getScheduledHistory();
            tL_messages_getScheduledHistory.peer = getInputPeer(j);
            tL_messages_getScheduledHistory.hash = j4;
            i15 = i5;
            sendRequest = getConnectionsManager().sendRequest(tL_messages_getScheduledHistory, new RequestDelegate() {
                @Override
                public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                    MessagesController.this.lambda$loadMessagesInternal$170(i2, i3, j, j2, i, i15, i10, i7, i11, i12, i6, i8, j3, i9, z3, i13, z5, z6, tLObject, tL_error);
                }
            });
            connectionsManager = getConnectionsManager();
        }
        connectionsManager.bindRequestToGuid(sendRequest, i15);
    }

    private void loadPremiumFeaturesPreviewOrder(SparseIntArray sparseIntArray, String str) {
        sparseIntArray.clear();
        if (str != null) {
            String[] split = str.split("_");
            for (int i = 0; i < split.length; i++) {
                sparseIntArray.put(Integer.parseInt(split[i]), i);
            }
        }
    }

    private int messagesMaxDate(ArrayList<MessageObject> arrayList) {
        TLRPC.Message message;
        int i;
        int i2 = Integer.MIN_VALUE;
        for (int i3 = 0; arrayList != null && i3 < arrayList.size(); i3++) {
            MessageObject messageObject = arrayList.get(i3);
            if (messageObject != null && (message = messageObject.messageOwner) != null && (i = message.date) > i2) {
                i2 = i;
            }
        }
        return i2;
    }

    private void migrateDialogs(final int i, int i2, long j, long j2, long j3, long j4) {
        if (this.migratingDialogs || i == -1) {
            return;
        }
        this.migratingDialogs = true;
        TLRPC.TL_messages_getDialogs tL_messages_getDialogs = new TLRPC.TL_messages_getDialogs();
        tL_messages_getDialogs.exclude_pinned = true;
        tL_messages_getDialogs.limit = 100;
        tL_messages_getDialogs.offset_id = i;
        tL_messages_getDialogs.offset_date = i2;
        if (BuildVars.LOGS_ENABLED) {
            FileLog.d("start migrate with id " + i + " date " + LocaleController.getInstance().getFormatterStats().format(i2 * 1000));
        }
        if (i == 0) {
            tL_messages_getDialogs.offset_peer = new TLRPC.TL_inputPeerEmpty();
        } else {
            if (j3 != 0) {
                TLRPC.TL_inputPeerChannel tL_inputPeerChannel = new TLRPC.TL_inputPeerChannel();
                tL_messages_getDialogs.offset_peer = tL_inputPeerChannel;
                tL_inputPeerChannel.channel_id = j3;
            } else if (j != 0) {
                TLRPC.TL_inputPeerUser tL_inputPeerUser = new TLRPC.TL_inputPeerUser();
                tL_messages_getDialogs.offset_peer = tL_inputPeerUser;
                tL_inputPeerUser.user_id = j;
            } else {
                TLRPC.TL_inputPeerChat tL_inputPeerChat = new TLRPC.TL_inputPeerChat();
                tL_messages_getDialogs.offset_peer = tL_inputPeerChat;
                tL_inputPeerChat.chat_id = j2;
            }
            tL_messages_getDialogs.offset_peer.access_hash = j4;
        }
        getConnectionsManager().sendRequest(tL_messages_getDialogs, new RequestDelegate() {
            @Override
            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                MessagesController.this.lambda$migrateDialogs$204(i, tLObject, tL_error);
            }
        });
    }

    private void processChannelsUpdatesQueue(long j, int i) {
        ArrayList arrayList = (ArrayList) this.updatesQueueChannels.get(j);
        if (arrayList == null) {
            return;
        }
        int i2 = this.channelsPts.get(j);
        if (arrayList.isEmpty() || i2 == 0) {
            this.updatesQueueChannels.remove(j);
            return;
        }
        Collections.sort(arrayList, new Comparator() {
            @Override
            public final int compare(Object obj, Object obj2) {
                int lambda$processChannelsUpdatesQueue$303;
                lambda$processChannelsUpdatesQueue$303 = MessagesController.lambda$processChannelsUpdatesQueue$303((TLRPC.Updates) obj, (TLRPC.Updates) obj2);
                return lambda$processChannelsUpdatesQueue$303;
            }
        });
        if (i == 2) {
            this.channelsPts.put(j, ((TLRPC.Updates) arrayList.get(0)).pts);
        }
        boolean z = false;
        while (arrayList.size() > 0) {
            TLRPC.Updates updates = (TLRPC.Updates) arrayList.get(0);
            int i3 = updates.pts;
            char c = i3 <= i2 ? (char) 2 : updates.pts_count + i2 == i3 ? (char) 0 : (char) 1;
            if (c == 0) {
                processUpdates(updates, true);
                if (arrayList.size() <= 0) {
                    break;
                }
                z = true;
                arrayList.remove(0);
            } else {
                if (c == 1) {
                    long j2 = this.updatesStartWaitTimeChannels.get(j);
                    if (j2 == 0 || (!z && Math.abs(System.currentTimeMillis() - j2) > 1500)) {
                        if (BuildVars.LOGS_ENABLED) {
                            FileLog.d("HOLE IN CHANNEL " + j + " UPDATES QUEUE - getChannelDifference ");
                        }
                        this.updatesStartWaitTimeChannels.delete(j);
                        this.updatesQueueChannels.remove(j);
                        getChannelDifference(j);
                        return;
                    }
                    if (BuildVars.LOGS_ENABLED) {
                        FileLog.d("HOLE IN CHANNEL " + j + " UPDATES QUEUE - will wait more time");
                    }
                    if (z) {
                        this.updatesStartWaitTimeChannels.put(j, System.currentTimeMillis());
                        return;
                    }
                    return;
                }
                if (arrayList.size() <= 0) {
                    break;
                } else {
                    arrayList.remove(0);
                }
            }
        }
        this.updatesQueueChannels.remove(j);
        this.updatesStartWaitTimeChannels.delete(j);
        if (BuildVars.LOGS_ENABLED) {
            FileLog.d("UPDATES CHANNEL " + j + " QUEUE PROCEED - OK");
        }
    }

    private void processUpdatesQueue(int r9, int r10) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MessagesController.processUpdatesQueue(int, int):void");
    }

    private void reloadDialogsReadValue(ArrayList<TLRPC.Dialog> arrayList, long j) {
        if (j == 0 && (arrayList == null || arrayList.isEmpty())) {
            return;
        }
        TLRPC.TL_messages_getPeerDialogs tL_messages_getPeerDialogs = new TLRPC.TL_messages_getPeerDialogs();
        if (arrayList != null) {
            for (int i = 0; i < arrayList.size(); i++) {
                TLRPC.InputPeer inputPeer = getInputPeer(arrayList.get(i).id);
                if (!(inputPeer instanceof TLRPC.TL_inputPeerChannel) || inputPeer.access_hash != 0) {
                    TLRPC.TL_inputDialogPeer tL_inputDialogPeer = new TLRPC.TL_inputDialogPeer();
                    tL_inputDialogPeer.peer = inputPeer;
                    long peerDialogId = DialogObject.getPeerDialogId(inputPeer);
                    Long l = (Long) this.peerDialogsRequested.get(peerDialogId);
                    if (l == null || System.currentTimeMillis() - l.longValue() > 240000) {
                        tL_messages_getPeerDialogs.peers.add(tL_inputDialogPeer);
                        this.peerDialogsRequested.put(peerDialogId, Long.valueOf(System.currentTimeMillis()));
                    }
                }
            }
        } else {
            TLRPC.InputPeer inputPeer2 = getInputPeer(j);
            if ((inputPeer2 instanceof TLRPC.TL_inputPeerChannel) && inputPeer2.access_hash == 0) {
                return;
            }
            TLRPC.TL_inputDialogPeer tL_inputDialogPeer2 = new TLRPC.TL_inputDialogPeer();
            tL_inputDialogPeer2.peer = inputPeer2;
            long peerDialogId2 = DialogObject.getPeerDialogId(inputPeer2);
            Long l2 = (Long) this.peerDialogsRequested.get(peerDialogId2);
            if (l2 == null || System.currentTimeMillis() - l2.longValue() > 240000) {
                tL_messages_getPeerDialogs.peers.add(tL_inputDialogPeer2);
                this.peerDialogsRequested.put(peerDialogId2, Long.valueOf(System.currentTimeMillis()));
            }
        }
        if (tL_messages_getPeerDialogs.peers.isEmpty()) {
            return;
        }
        getConnectionsManager().sendRequest(tL_messages_getPeerDialogs, new RequestDelegate() {
            @Override
            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                MessagesController.this.lambda$reloadDialogsReadValue$58(tLObject, tL_error);
            }
        });
    }

    private void reloadMessages(ArrayList<Integer> arrayList, final long j, final int i) {
        TLRPC.TL_messages_getMessages tL_messages_getMessages;
        if (arrayList.isEmpty()) {
            return;
        }
        final boolean z = i == 1;
        final ArrayList<Integer> arrayList2 = new ArrayList<>();
        TLRPC.Chat chat = DialogObject.isChatDialog(j) ? getChat(Long.valueOf(-j)) : null;
        if (ChatObject.isChannel(chat)) {
            TLRPC.TL_channels_getMessages tL_channels_getMessages = new TLRPC.TL_channels_getMessages();
            tL_channels_getMessages.channel = getInputChannel(chat);
            tL_channels_getMessages.id = arrayList2;
            tL_messages_getMessages = tL_channels_getMessages;
        } else {
            TLRPC.TL_messages_getMessages tL_messages_getMessages2 = new TLRPC.TL_messages_getMessages();
            tL_messages_getMessages2.id = arrayList2;
            tL_messages_getMessages = tL_messages_getMessages2;
        }
        TLRPC.TL_messages_getMessages tL_messages_getMessages3 = tL_messages_getMessages;
        ArrayList arrayList3 = (ArrayList) this.reloadingMessages.get(j);
        for (int i2 = 0; i2 < arrayList.size(); i2++) {
            Integer num = arrayList.get(i2);
            if (arrayList3 == null || !arrayList3.contains(num)) {
                arrayList2.add(num);
            }
        }
        if (arrayList2.isEmpty()) {
            return;
        }
        if (arrayList3 == null) {
            arrayList3 = new ArrayList();
            this.reloadingMessages.put(j, arrayList3);
        }
        arrayList3.addAll(arrayList2);
        getConnectionsManager().sendRequest(tL_messages_getMessages3, new RequestDelegate() {
            @Override
            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                MessagesController.this.lambda$reloadMessages$68(j, z, i, arrayList2, tLObject, tL_error);
            }
        });
    }

    private void removeDialog(TLRPC.Dialog dialog) {
        if (dialog == null) {
            return;
        }
        final long j = dialog.id;
        if (this.dialogsServerOnly.remove(dialog) && DialogObject.isChannel(dialog)) {
            Utilities.stageQueue.postRunnable(new Runnable() {
                @Override
                public final void run() {
                    MessagesController.this.lambda$removeDialog$124(j);
                }
            });
        }
        this.allDialogs.remove(dialog);
        this.dialogsMyChannels.remove(dialog);
        this.dialogsMyGroups.remove(dialog);
        this.dialogsCanAddUsers.remove(dialog);
        this.dialogsChannelsOnly.remove(dialog);
        this.dialogsGroupsOnly.remove(dialog);
        this.dialogsUsersOnly.remove(dialog);
        this.dialogsForBlock.remove(dialog);
        this.dialogsForward.remove(dialog);
        int i = 0;
        while (true) {
            DialogFilter[] dialogFilterArr = this.selectedDialogFilter;
            if (i >= dialogFilterArr.length) {
                break;
            }
            DialogFilter dialogFilter = dialogFilterArr[i];
            if (dialogFilter != null) {
                dialogFilter.dialogs.remove(dialog);
                this.selectedDialogFilter[i].dialogsForward.remove(dialog);
            }
            i++;
        }
        this.dialogs_dict.remove(j);
        ArrayList<TLRPC.Dialog> arrayList = this.dialogsByFolder.get(dialog.folder_id);
        if (arrayList != null) {
            arrayList.remove(dialog);
        }
    }

    private void removeFolder(int i) {
        long makeFolderDialogId = DialogObject.makeFolderDialogId(i);
        TLRPC.Dialog dialog = (TLRPC.Dialog) this.dialogs_dict.get(makeFolderDialogId);
        if (dialog == null) {
            return;
        }
        this.dialogs_dict.remove(makeFolderDialogId);
        this.allDialogs.remove(dialog);
        sortDialogs(null);
        getNotificationCenter().lambda$postNotificationNameOnUIThread$1(NotificationCenter.dialogsNeedReload, new Object[0]);
        getNotificationCenter().lambda$postNotificationNameOnUIThread$1(NotificationCenter.folderBecomeEmpty, Integer.valueOf(i));
    }

    public void removePromoDialog() {
        TLRPC.Dialog dialog = this.promoDialog;
        if (dialog == null) {
            return;
        }
        long j = dialog.id;
        if (j < 0) {
            TLRPC.Chat chat = getChat(Long.valueOf(-j));
            if (ChatObject.isNotInChat(chat) || chat.restricted) {
                dialog = this.promoDialog;
            }
            this.promoDialog = null;
            sortDialogs(null);
            getNotificationCenter().lambda$postNotificationNameOnUIThread$1(NotificationCenter.dialogsNeedReload, new Object[0]);
        }
        removeDialog(dialog);
        this.promoDialog = null;
        sortDialogs(null);
        getNotificationCenter().lambda$postNotificationNameOnUIThread$1(NotificationCenter.dialogsNeedReload, new Object[0]);
    }

    public void requestIsUserPremiumBlocked() {
        if (this.loadingIsUserPremiumBlocked.isEmpty()) {
            return;
        }
        TLRPC.TL_users_getIsPremiumRequiredToContact tL_users_getIsPremiumRequiredToContact = new TLRPC.TL_users_getIsPremiumRequiredToContact();
        final ArrayList arrayList = new ArrayList();
        Iterator<Long> it = this.loadingIsUserPremiumBlocked.iterator();
        while (it.hasNext()) {
            Long next = it.next();
            TLRPC.InputUser inputUser = getInputUser(next.longValue());
            if (inputUser != null) {
                tL_users_getIsPremiumRequiredToContact.id.add(inputUser);
                arrayList.add(next);
            }
        }
        this.loadingIsUserPremiumBlocked.clear();
        if (tL_users_getIsPremiumRequiredToContact.id.isEmpty()) {
            return;
        }
        getConnectionsManager().sendRequest(tL_users_getIsPremiumRequiredToContact, new RequestDelegate() {
            @Override
            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                MessagesController.this.lambda$requestIsUserPremiumBlocked$447(arrayList, tLObject, tL_error);
            }
        });
    }

    private void resetAppConfig() {
        this.getfileExperimentalParams = false;
        this.channelRevenueWithdrawalEnabled = false;
        this.collectDeviceStats = false;
        this.smsjobsStickyNotificationEnabled = false;
        this.showAnnualPerMonth = false;
        this.canEditFactcheck = false;
        this.starsLocked = true;
        this.factcheckLengthLimit = 1024;
        this.videoIgnoreAltDocuments = false;
        this.mainPreferences.edit().remove("starsLocked").remove("getfileExperimentalParams").remove("smsjobsStickyNotificationEnabled").remove("channelRevenueWithdrawalEnabled").remove("showAnnualPerMonth").remove("canEditFactcheck").remove("factcheckLengthLimit").remove("videoIgnoreAltDocuments").apply();
    }

    private void resetDialogs(boolean r27, final int r28, final int r29, final int r30, final int r31) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MessagesController.resetDialogs(boolean, int, int, int, int):void");
    }

    private void savePeerSettings(long j, TLRPC.PeerSettings peerSettings, boolean z) {
        if (peerSettings == null) {
            return;
        }
        SharedPreferences.Editor edit = this.notificationsPreferences.edit();
        if (peerSettings.business_bot_id != 0) {
            edit.putLong("dialog_botid" + j, peerSettings.business_bot_id);
            edit.putString("dialog_boturl" + j, peerSettings.business_bot_manage_url);
            edit.putInt("dialog_botflags" + j, (peerSettings.business_bot_paused ? 1 : 0) + (peerSettings.business_bot_can_reply ? 2 : 0));
        } else {
            edit.remove("dialog_botid" + j).remove("dialog_boturl" + j).remove("dialog_botflags" + j);
        }
        if (this.notificationsPreferences.getInt("dialog_bar_vis3" + j, 0) == 3) {
            edit.apply();
            getNotificationCenter().lambda$postNotificationNameOnUIThread$1(NotificationCenter.peerSettingsDidLoad, Long.valueOf(j));
            return;
        }
        boolean z2 = peerSettings.flags == 0;
        if (BuildVars.LOGS_ENABLED) {
            FileLog.d("peer settings loaded for " + j + " add = " + peerSettings.add_contact + " block = " + peerSettings.block_contact + " spam = " + peerSettings.report_spam + " share = " + peerSettings.share_contact + " geo = " + peerSettings.report_geo + " hide = " + z2 + " distance = " + peerSettings.geo_distance + " invite = " + peerSettings.invite_members);
        }
        edit.putInt("dialog_bar_vis3" + j, z2 ? 1 : 2);
        edit.putBoolean("dialog_bar_share" + j, peerSettings.share_contact);
        edit.putBoolean("dialog_bar_report" + j, peerSettings.report_spam);
        edit.putBoolean("dialog_bar_add" + j, peerSettings.add_contact);
        edit.putBoolean("dialog_bar_block" + j, peerSettings.block_contact);
        edit.putBoolean("dialog_bar_exception" + j, peerSettings.need_contacts_exception);
        edit.putBoolean("dialog_bar_location" + j, peerSettings.report_geo);
        edit.putBoolean("dialog_bar_archived" + j, peerSettings.autoarchived);
        edit.putBoolean("dialog_bar_invite" + j, peerSettings.invite_members);
        edit.putString("dialog_bar_chat_with_admin_title" + j, peerSettings.request_chat_title);
        edit.putBoolean("dialog_bar_chat_with_channel" + j, peerSettings.request_chat_broadcast);
        edit.putInt("dialog_bar_chat_with_date" + j, peerSettings.request_chat_date);
        if (this.notificationsPreferences.getInt("dialog_bar_distance" + j, -1) != -2) {
            if ((peerSettings.flags & 64) != 0) {
                edit.putInt("dialog_bar_distance" + j, peerSettings.geo_distance);
            } else {
                edit.remove("dialog_bar_distance" + j);
            }
        }
        if (j == getUserConfig().getClientUserId()) {
            peerSettings.business_bot_id = 1271266957L;
            peerSettings.business_bot_manage_url = "https://telegram.org/";
        }
        edit.apply();
        getNotificationCenter().lambda$postNotificationNameOnUIThread$1(NotificationCenter.peerSettingsDidLoad, Long.valueOf(j));
    }

    private boolean savePremiumFeaturesPreviewOrder(String str, SparseIntArray sparseIntArray, SharedPreferences.Editor editor, ArrayList<TLRPC.JSONValue> arrayList) {
        int serverStringToFeatureType;
        StringBuilder sb = new StringBuilder();
        sparseIntArray.clear();
        int i = 0;
        while (true) {
            if (i >= arrayList.size()) {
                break;
            }
            String str2 = arrayList.get(i) instanceof TLRPC.TL_jsonString ? ((TLRPC.TL_jsonString) arrayList.get(i)).value : null;
            if (str2 != null && (serverStringToFeatureType = PremiumPreviewFragment.serverStringToFeatureType(str2)) >= 0) {
                sparseIntArray.put(serverStringToFeatureType, i);
                if (sb.length() > 0) {
                    sb.append('_');
                }
                sb.append(serverStringToFeatureType);
            }
            i++;
        }
        if (sb.length() <= 0) {
            editor.remove(str);
            return this.mainPreferences.getString(str, null) != null;
        }
        String sb2 = sb.toString();
        boolean z = !sb2.equals(this.mainPreferences.getString(str, null));
        editor.putString(str, sb2);
        return z;
    }

    private void saveSavedReactionsTags(final long j, final TLRPC.TL_messages_savedReactionsTags tL_messages_savedReactionsTags) {
        getMessagesStorage().getStorageQueue().postRunnable(new Runnable() {
            @Override
            public final void run() {
                MessagesController.this.lambda$saveSavedReactionsTags$441(j, tL_messages_savedReactionsTags);
            }
        });
    }

    private void scheduleTranscriptionUpdate() {
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                MessagesController.this.lambda$scheduleTranscriptionUpdate$33();
            }
        });
    }

    private void sendLoadPeersRequest(final TLObject tLObject, final ArrayList<TLObject> arrayList, final TLRPC.messages_Dialogs messages_dialogs, final TLRPC.messages_Dialogs messages_dialogs2, final ArrayList<TLRPC.User> arrayList2, final ArrayList<TLRPC.Chat> arrayList3, final ArrayList<DialogFilter> arrayList4, final SparseArray<DialogFilter> sparseArray, final ArrayList<Integer> arrayList5, final HashMap<Integer, HashSet<Long>> hashMap, final HashSet<Integer> hashSet, final Runnable runnable) {
        getConnectionsManager().sendRequest(tLObject, new RequestDelegate() {
            @Override
            public final void run(TLObject tLObject2, TLRPC.TL_error tL_error) {
                MessagesController.this.lambda$sendLoadPeersRequest$16(arrayList3, arrayList2, messages_dialogs, messages_dialogs2, arrayList, tLObject, arrayList4, sparseArray, arrayList5, hashMap, hashSet, runnable, tLObject2, tL_error);
            }
        });
    }

    private void setUpdatesStartTime(int i, long j) {
        if (i == 0) {
            this.updatesStartWaitTimeSeq = j;
        } else if (i == 1) {
            this.updatesStartWaitTimePts = j;
        } else if (i == 2) {
            this.updatesStartWaitTimeQts = j;
        }
    }

    public static void showCantOpenAlert(BaseFragment baseFragment, String str) {
        if (baseFragment == null || baseFragment.getParentActivity() == null) {
            return;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(baseFragment.getParentActivity(), baseFragment.getResourceProvider());
        builder.setTitle(LocaleController.getString(R.string.DialogNotAvailable));
        HashMap hashMap = new HashMap();
        int i = Theme.key_dialogTopBackground;
        hashMap.put("info1.**", Integer.valueOf(baseFragment.getThemedColor(i)));
        hashMap.put("info2.**", Integer.valueOf(baseFragment.getThemedColor(i)));
        builder.setTopAnimation(R.raw.not_available, 52, false, baseFragment.getThemedColor(i), hashMap);
        builder.setTopAnimationIsNew(true);
        builder.setPositiveButton(LocaleController.getString(R.string.Close), null);
        builder.setMessage(str);
        baseFragment.showDialog(builder.create());
    }

    public static TLRPC.InputDocument toInputDocument(TLRPC.Document document) {
        TLRPC.TL_inputDocument tL_inputDocument = new TLRPC.TL_inputDocument();
        tL_inputDocument.id = document.id;
        tL_inputDocument.access_hash = document.access_hash;
        tL_inputDocument.file_reference = document.file_reference;
        return tL_inputDocument;
    }

    public static TLRPC.InputMedia toInputMedia(TLRPC.MessageMedia messageMedia) {
        if (messageMedia instanceof TLRPC.TL_messageMediaDocument) {
            TLRPC.TL_inputMediaDocument tL_inputMediaDocument = new TLRPC.TL_inputMediaDocument();
            TLRPC.TL_inputDocument tL_inputDocument = new TLRPC.TL_inputDocument();
            tL_inputMediaDocument.id = tL_inputDocument;
            TLRPC.Document document = messageMedia.document;
            tL_inputDocument.id = document.id;
            tL_inputDocument.access_hash = document.access_hash;
            tL_inputDocument.file_reference = document.file_reference;
            return tL_inputMediaDocument;
        }
        if (!(messageMedia instanceof TLRPC.TL_messageMediaPhoto)) {
            return null;
        }
        TLRPC.TL_inputMediaPhoto tL_inputMediaPhoto = new TLRPC.TL_inputMediaPhoto();
        TLRPC.TL_inputPhoto tL_inputPhoto = new TLRPC.TL_inputPhoto();
        tL_inputMediaPhoto.id = tL_inputPhoto;
        TLRPC.Photo photo = messageMedia.photo;
        tL_inputPhoto.id = photo.id;
        tL_inputPhoto.access_hash = photo.access_hash;
        tL_inputPhoto.file_reference = photo.file_reference;
        return tL_inputMediaPhoto;
    }

    private void updatePrintingStrings() {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MessagesController.updatePrintingStrings():void");
    }

    private boolean updatePrintingUsersWithNewMessages(long j, ArrayList<MessageObject> arrayList) {
        boolean z;
        if (j > 0) {
            if (this.printingUsers.get(Long.valueOf(j)) != null) {
                this.printingUsers.remove(Long.valueOf(j));
                return true;
            }
        } else if (j < 0) {
            ArrayList arrayList2 = new ArrayList();
            Iterator<MessageObject> it = arrayList.iterator();
            while (it.hasNext()) {
                MessageObject next = it.next();
                if (next.isFromUser() && !arrayList2.contains(Long.valueOf(next.messageOwner.from_id.user_id))) {
                    arrayList2.add(Long.valueOf(next.messageOwner.from_id.user_id));
                }
            }
            ConcurrentHashMap<Integer, ArrayList<PrintingUser>> concurrentHashMap = this.printingUsers.get(Long.valueOf(j));
            if (concurrentHashMap != null) {
                ArrayList arrayList3 = null;
                z = false;
                for (Map.Entry<Integer, ArrayList<PrintingUser>> entry : concurrentHashMap.entrySet()) {
                    Integer key = entry.getKey();
                    ArrayList<PrintingUser> value = entry.getValue();
                    int i = 0;
                    while (i < value.size()) {
                        if (arrayList2.contains(Long.valueOf(value.get(i).userId))) {
                            value.remove(i);
                            i--;
                            if (value.isEmpty()) {
                                if (arrayList3 == null) {
                                    arrayList3 = new ArrayList();
                                }
                                arrayList3.add(key);
                            }
                            z = true;
                        }
                        i++;
                    }
                }
                if (arrayList3 != null) {
                    int size = arrayList3.size();
                    for (int i2 = 0; i2 < size; i2++) {
                        concurrentHashMap.remove(arrayList3.get(i2));
                    }
                    if (concurrentHashMap.isEmpty()) {
                        this.printingUsers.remove(Long.valueOf(j));
                    }
                }
            } else {
                z = false;
            }
            if (z) {
                return true;
            }
        }
        return false;
    }

    public void addDialogAction(long j, boolean z) {
        TLRPC.Dialog dialog = (TLRPC.Dialog) this.dialogs_dict.get(j);
        if (dialog == null) {
            return;
        }
        if (z) {
            this.clearingHistoryDialogs.put(j, dialog);
        } else {
            this.deletingDialogs.put(j, dialog);
            this.allDialogs.remove(dialog);
            sortDialogs(null);
        }
        getNotificationCenter().lambda$postNotificationNameOnUIThread$1(NotificationCenter.dialogsNeedReload, Boolean.TRUE);
    }

    public int addDialogToFolder(long j, int i, int i2, long j2) {
        ArrayList<Long> arrayList = new ArrayList<>(1);
        arrayList.add(Long.valueOf(j));
        return addDialogToFolder(arrayList, i, i2, null, j2);
    }

    public int addDialogToFolder(java.util.ArrayList<java.lang.Long> r22, int r23, int r24, java.util.ArrayList<org.telegram.tgnet.TLRPC.TL_inputFolderPeer> r25, long r26) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MessagesController.addDialogToFolder(java.util.ArrayList, int, int, java.util.ArrayList, long):int");
    }

    public void addFilter(DialogFilter dialogFilter, boolean z) {
        if (z) {
            int size = this.dialogFilters.size();
            int i = 254;
            for (int i2 = 0; i2 < size; i2++) {
                i = Math.min(i, this.dialogFilters.get(i2).order);
            }
            dialogFilter.order = i - 1;
            if (this.dialogFilters.get(0).isDefault()) {
                this.dialogFilters.add(1, dialogFilter);
            } else {
                this.dialogFilters.add(0, dialogFilter);
            }
        } else {
            int size2 = this.dialogFilters.size();
            int i3 = 0;
            for (int i4 = 0; i4 < size2; i4++) {
                i3 = Math.max(i3, this.dialogFilters.get(i4).order);
            }
            dialogFilter.order = i3 + 1;
            this.dialogFilters.add(dialogFilter);
        }
        this.dialogFiltersById.put(dialogFilter.id, dialogFilter);
        if (this.dialogFilters.size() == 1 && SharedConfig.getChatSwipeAction(this.currentAccount) != 5) {
            SharedConfig.updateChatListSwipeSetting(5);
        }
        lockFiltersInternal();
    }

    public void addSupportUser() {
        TLRPC.TL_userForeign_old2 tL_userForeign_old2 = new TLRPC.TL_userForeign_old2();
        tL_userForeign_old2.phone = "333";
        tL_userForeign_old2.id = 333000L;
        tL_userForeign_old2.first_name = "Telegram";
        tL_userForeign_old2.last_name = "";
        tL_userForeign_old2.status = null;
        tL_userForeign_old2.photo = new TLRPC.TL_userProfilePhotoEmpty();
        putUser(tL_userForeign_old2, true);
        TLRPC.TL_userForeign_old2 tL_userForeign_old22 = new TLRPC.TL_userForeign_old2();
        tL_userForeign_old22.phone = "42777";
        tL_userForeign_old22.id = 777000L;
        tL_userForeign_old22.verified = true;
        tL_userForeign_old22.first_name = "Telegram";
        tL_userForeign_old22.last_name = "Notifications";
        tL_userForeign_old22.status = null;
        tL_userForeign_old22.photo = new TLRPC.TL_userProfilePhotoEmpty();
        putUser(tL_userForeign_old22, true);
    }

    public void addToPollsQueue(long j, ArrayList<MessageObject> arrayList) {
        long min;
        int i;
        SparseArray sparseArray = (SparseArray) this.pollsToCheck.get(j);
        if (sparseArray == null) {
            sparseArray = new SparseArray();
            this.pollsToCheck.put(j, sparseArray);
            this.pollsToCheckSize++;
        }
        int size = sparseArray.size();
        for (int i2 = 0; i2 < size; i2++) {
            ((MessageObject) sparseArray.valueAt(i2)).pollVisibleOnScreen = false;
        }
        int currentTime = getConnectionsManager().getCurrentTime();
        int size2 = arrayList.size();
        boolean z = false;
        int i3 = Integer.MAX_VALUE;
        for (int i4 = 0; i4 < size2; i4++) {
            MessageObject messageObject = arrayList.get(i4);
            if (messageObject.type == 17) {
                TLRPC.Poll poll = ((TLRPC.TL_messageMediaPoll) messageObject.messageOwner.media).poll;
                if (!poll.closed && (i = poll.close_date) != 0) {
                    if (i <= currentTime) {
                        z = true;
                    } else {
                        i3 = Math.min(i3, i - currentTime);
                    }
                }
                int id = messageObject.getId();
                MessageObject messageObject2 = (MessageObject) sparseArray.get(id);
                if (messageObject2 != null) {
                    messageObject2.pollVisibleOnScreen = true;
                } else {
                    sparseArray.put(id, messageObject);
                }
            }
        }
        if (z) {
            min = 0;
        } else if (i3 >= 5) {
            return;
        } else {
            min = Math.min(this.lastViewsCheckTime, System.currentTimeMillis() - ((5 - i3) * 1000));
        }
        this.lastViewsCheckTime = min;
    }

    public void addToViewsQueue(final MessageObject messageObject) {
        if (messageObject == null) {
            return;
        }
        Utilities.stageQueue.postRunnable(new Runnable() {
            @Override
            public final void run() {
                MessagesController.this.lambda$addToViewsQueue$218(messageObject);
            }
        });
    }

    public void addUserToChat(long j, TLRPC.User user, int i, String str, BaseFragment baseFragment, Runnable runnable) {
        addUserToChat(j, user, i, str, baseFragment, false, runnable, null);
    }

    public void addUserToChat(long j, TLRPC.User user, int i, String str, BaseFragment baseFragment, boolean z, Runnable runnable, ErrorDelegate errorDelegate) {
        addUserToChat(j, user, i, str, baseFragment, z, runnable, errorDelegate, null);
    }

    public void addUserToChat(final long j, final TLRPC.User user, int i, String str, final BaseFragment baseFragment, final boolean z, final Runnable runnable, final ErrorDelegate errorDelegate, final Utilities.Callback<TLRPC.TL_messages_invitedUsers> callback) {
        TLRPC.TL_messages_addChatUser tL_messages_addChatUser;
        if (user == null) {
            if (errorDelegate != null) {
                errorDelegate.run(null);
            }
            if (callback != null) {
                callback.run(null);
                return;
            }
            return;
        }
        final boolean isChannel = ChatObject.isChannel(j, this.currentAccount);
        final boolean z2 = isChannel && getChat(Long.valueOf(j)).megagroup;
        final TLRPC.InputUser inputUser = getInputUser(user);
        if (str != null && (!isChannel || z2)) {
            TLRPC.TL_messages_startBot tL_messages_startBot = new TLRPC.TL_messages_startBot();
            tL_messages_startBot.bot = inputUser;
            if (isChannel) {
                tL_messages_startBot.peer = getInputPeer(-j);
            } else {
                TLRPC.TL_inputPeerChat tL_inputPeerChat = new TLRPC.TL_inputPeerChat();
                tL_messages_startBot.peer = tL_inputPeerChat;
                tL_inputPeerChat.chat_id = j;
            }
            tL_messages_startBot.start_param = str;
            tL_messages_startBot.random_id = Utilities.random.nextLong();
            tL_messages_addChatUser = tL_messages_startBot;
        } else if (!isChannel) {
            TLRPC.TL_messages_addChatUser tL_messages_addChatUser2 = new TLRPC.TL_messages_addChatUser();
            tL_messages_addChatUser2.chat_id = j;
            tL_messages_addChatUser2.fwd_limit = i;
            tL_messages_addChatUser2.user_id = inputUser;
            tL_messages_addChatUser = tL_messages_addChatUser2;
        } else if (!(inputUser instanceof TLRPC.TL_inputUserSelf)) {
            TLRPC.TL_channels_inviteToChannel tL_channels_inviteToChannel = new TLRPC.TL_channels_inviteToChannel();
            tL_channels_inviteToChannel.channel = getInputChannel(j);
            tL_channels_inviteToChannel.users.add(inputUser);
            tL_messages_addChatUser = tL_channels_inviteToChannel;
        } else if (this.joiningToChannels.contains(Long.valueOf(j))) {
            if (errorDelegate != null) {
                errorDelegate.run(null);
                return;
            }
            return;
        } else {
            TLRPC.TL_channels_joinChannel tL_channels_joinChannel = new TLRPC.TL_channels_joinChannel();
            tL_channels_joinChannel.channel = getInputChannel(j);
            this.joiningToChannels.add(Long.valueOf(j));
            tL_messages_addChatUser = tL_channels_joinChannel;
        }
        final TLRPC.TL_messages_addChatUser tL_messages_addChatUser3 = tL_messages_addChatUser;
        getConnectionsManager().sendRequest(tL_messages_addChatUser3, new RequestDelegate() {
            @Override
            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                MessagesController.this.lambda$addUserToChat$287(isChannel, inputUser, j, user, callback, z, runnable, errorDelegate, baseFragment, tL_messages_addChatUser3, z2, tLObject, tL_error);
            }
        });
    }

    public void addUsersToChannel(final long j, ArrayList<TLRPC.InputUser> arrayList, final BaseFragment baseFragment) {
        if (arrayList == null || arrayList.isEmpty()) {
            return;
        }
        final TLRPC.TL_channels_inviteToChannel tL_channels_inviteToChannel = new TLRPC.TL_channels_inviteToChannel();
        tL_channels_inviteToChannel.channel = getInputChannel(j);
        tL_channels_inviteToChannel.users = arrayList;
        getConnectionsManager().sendRequest(tL_channels_inviteToChannel, new RequestDelegate() {
            @Override
            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                MessagesController.this.lambda$addUsersToChannel$253(baseFragment, tL_channels_inviteToChannel, j, tLObject, tL_error);
            }
        });
    }

    public void addUsersToChat(final TLRPC.Chat chat, BaseFragment baseFragment, ArrayList<TLRPC.User> arrayList, int i, final Consumer consumer, final Consumer consumer2, final Runnable runnable) {
        final int size = arrayList.size();
        final int[] iArr = {0};
        final TLRPC.TL_messages_invitedUsers tL_messages_invitedUsers = new TLRPC.TL_messages_invitedUsers();
        tL_messages_invitedUsers.updates = new TLRPC.TL_updates();
        long j = chat.id;
        for (int i2 = 0; i2 < size; i2++) {
            final TLRPC.User user = arrayList.get(i2);
            addUserToChat(j, user, i, null, baseFragment, false, new Runnable() {
                @Override
                public final void run() {
                    MessagesController.lambda$addUsersToChat$274(Consumer.this, user);
                }
            }, new ErrorDelegate() {
                @Override
                public final boolean run(TLRPC.TL_error tL_error) {
                    boolean lambda$addUsersToChat$275;
                    lambda$addUsersToChat$275 = MessagesController.lambda$addUsersToChat$275(Consumer.this, user, tL_error);
                    return lambda$addUsersToChat$275;
                }
            }, new Utilities.Callback() {
                @Override
                public final void run(Object obj) {
                    MessagesController.this.lambda$addUsersToChat$277(tL_messages_invitedUsers, iArr, size, chat, runnable, (TLRPC.TL_messages_invitedUsers) obj);
                }
            });
            putUser(user, false);
        }
    }

    public Boolean arePaidReactionsAnonymous() {
        if (this.paidReactionsAnonymous == null && !this.loadingArePaidReactionsAnonymous) {
            this.loadingArePaidReactionsAnonymous = true;
            getConnectionsManager().sendRequest(new TLRPC.TL_messages_getPaidReactionPrivacy(), null);
        }
        return this.paidReactionsAnonymous;
    }

    public void blockPeer(long j) {
        TLRPC.Chat chat;
        TLRPC.User user = null;
        if (j > 0) {
            TLRPC.User user2 = getUser(Long.valueOf(j));
            if (user2 == null) {
                return;
            }
            user = user2;
            chat = null;
        } else {
            chat = getChat(Long.valueOf(-j));
            if (chat == null) {
                return;
            }
        }
        if (this.blockePeers.indexOfKey(j) >= 0) {
            return;
        }
        this.blockePeers.put(j, 1);
        if (user != null) {
            if (user.bot) {
                getMediaDataController().removeInline(j);
            } else {
                getMediaDataController().removePeer(j);
            }
        }
        int i = this.totalBlockedCount;
        if (i >= 0) {
            this.totalBlockedCount = i + 1;
        }
        getNotificationCenter().lambda$postNotificationNameOnUIThread$1(NotificationCenter.blockedUsersDidLoad, new Object[0]);
        TLRPC.TL_contacts_block tL_contacts_block = new TLRPC.TL_contacts_block();
        tL_contacts_block.id = user != null ? getInputPeer(user) : getInputPeer(chat);
        getConnectionsManager().sendRequest(tL_contacts_block, new RequestDelegate() {
            @Override
            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                MessagesController.lambda$blockPeer$83(tLObject, tL_error);
            }
        });
    }

    public boolean canAddToForward(TLRPC.Dialog dialog) {
        if (dialog == null) {
            return false;
        }
        if (!DialogObject.isEncryptedDialog(dialog.id) && DialogObject.isChannel(dialog)) {
            TLRPC.Chat chat = getChat(Long.valueOf(-dialog.id));
            if (chat == null || !chat.megagroup) {
                if (!ChatObject.hasAdminRights(chat) || !ChatObject.canPost(chat)) {
                    return false;
                }
            } else if (chat.gigagroup && !ChatObject.hasAdminRights(chat)) {
                return false;
            }
        }
        return true;
    }

    public void cancelLoadFullChat(long j) {
        this.loadingFullChats.remove(Long.valueOf(j));
    }

    public void cancelLoadFullUser(long j) {
        this.loadingFullUsers.remove(Long.valueOf(j));
    }

    public void lambda$sendTyping$163(int i, long j, long j2) {
        LongSparseArray longSparseArray;
        LongSparseArray longSparseArray2;
        if (i >= 0) {
            LongSparseArray[] longSparseArrayArr = this.sendingTypings;
            if (i >= longSparseArrayArr.length || (longSparseArray = longSparseArrayArr[i]) == null || (longSparseArray2 = (LongSparseArray) longSparseArray.get(j)) == null) {
                return;
            }
            longSparseArray2.remove(j2);
            if (longSparseArray2.size() == 0) {
                longSparseArray.remove(j);
            }
        }
    }

    public void cancelUploadWallpaper() {
        Theme.OverrideWallpaperInfo overrideWallpaperInfo = this.uploadingWallpaperInfo;
        if (overrideWallpaperInfo != null) {
            if (overrideWallpaperInfo.requestIds != null) {
                for (int i = 0; i < this.uploadingWallpaperInfo.requestIds.size(); i++) {
                    ConnectionsManager.getInstance(this.currentAccount).cancelRequest(((Integer) this.uploadingWallpaperInfo.requestIds.get(i)).intValue(), true);
                }
            }
            FileLoader.getInstance(this.currentAccount).cancelFileUpload(this.uploadingWallpaper, false);
            long j = this.uploadingWallpaperInfo.dialogId;
            if (j != 0) {
                if (j >= 0) {
                    TLRPC.UserFull userFull = getUserFull(j);
                    if (userFull != null) {
                        userFull.wallpaper = this.uploadingWallpaperInfo.prevUserWallpaper;
                        NotificationCenter.getInstance(this.currentAccount).lambda$postNotificationNameOnUIThread$1(NotificationCenter.userInfoDidLoad, Long.valueOf(this.uploadingWallpaperInfo.dialogId), userFull);
                    }
                } else {
                    TLRPC.ChatFull chatFull = getChatFull(-j);
                    if (chatFull != null) {
                        chatFull.wallpaper = this.uploadingWallpaperInfo.prevUserWallpaper;
                        NotificationCenter notificationCenter = NotificationCenter.getInstance(this.currentAccount);
                        int i2 = NotificationCenter.chatInfoDidLoad;
                        Boolean bool = Boolean.FALSE;
                        notificationCenter.lambda$postNotificationNameOnUIThread$1(i2, chatFull, 0, bool, bool);
                    }
                }
            }
            this.uploadingWallpaperInfo = null;
            this.uploadingWallpaper = null;
        }
    }

    public void changeChatAvatar(final long j, final TLRPC.TL_inputChatPhoto tL_inputChatPhoto, TLRPC.InputFile inputFile, TLRPC.InputFile inputFile2, TLRPC.VideoSize videoSize, double d, final String str, final TLRPC.FileLocation fileLocation, final TLRPC.FileLocation fileLocation2, final Runnable runnable) {
        MessagesController messagesController;
        TLRPC.InputChatPhoto inputChatPhoto;
        TLRPC.TL_messages_editChatPhoto tL_messages_editChatPhoto;
        if (tL_inputChatPhoto != null) {
            messagesController = this;
            inputChatPhoto = tL_inputChatPhoto;
        } else if (inputFile == null && inputFile2 == null && videoSize == null) {
            inputChatPhoto = new TLRPC.TL_inputChatPhotoEmpty();
            messagesController = this;
        } else {
            TLRPC.TL_inputChatUploadedPhoto tL_inputChatUploadedPhoto = new TLRPC.TL_inputChatUploadedPhoto();
            if (inputFile != null) {
                tL_inputChatUploadedPhoto.file = inputFile;
                tL_inputChatUploadedPhoto.flags |= 1;
            }
            if (inputFile2 != null) {
                tL_inputChatUploadedPhoto.video = inputFile2;
                int i = tL_inputChatUploadedPhoto.flags;
                tL_inputChatUploadedPhoto.video_start_ts = d;
                tL_inputChatUploadedPhoto.flags = i | 6;
            }
            if (videoSize != null) {
                tL_inputChatUploadedPhoto.video_emoji_markup = videoSize;
                tL_inputChatUploadedPhoto.flags |= 8;
            }
            messagesController = this;
            inputChatPhoto = tL_inputChatUploadedPhoto;
        }
        if (ChatObject.isChannel(j, messagesController.currentAccount)) {
            TLRPC.TL_channels_editPhoto tL_channels_editPhoto = new TLRPC.TL_channels_editPhoto();
            tL_channels_editPhoto.channel = getInputChannel(j);
            tL_channels_editPhoto.photo = inputChatPhoto;
            tL_messages_editChatPhoto = tL_channels_editPhoto;
        } else {
            TLRPC.TL_messages_editChatPhoto tL_messages_editChatPhoto2 = new TLRPC.TL_messages_editChatPhoto();
            tL_messages_editChatPhoto2.chat_id = j;
            tL_messages_editChatPhoto2.photo = inputChatPhoto;
            tL_messages_editChatPhoto = tL_messages_editChatPhoto2;
        }
        getConnectionsManager().sendRequest(tL_messages_editChatPhoto, new RequestDelegate() {
            @Override
            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                MessagesController.this.lambda$changeChatAvatar$296(tL_inputChatPhoto, fileLocation, fileLocation2, str, j, runnable, tLObject, tL_error);
            }
        }, 64);
    }

    public void changeChatTitle(long j, String str) {
        TLRPC.TL_messages_editChatTitle tL_messages_editChatTitle;
        if (ChatObject.isChannel(j, this.currentAccount)) {
            TLRPC.TL_channels_editTitle tL_channels_editTitle = new TLRPC.TL_channels_editTitle();
            tL_channels_editTitle.channel = getInputChannel(j);
            tL_channels_editTitle.title = str;
            tL_messages_editChatTitle = tL_channels_editTitle;
        } else {
            TLRPC.TL_messages_editChatTitle tL_messages_editChatTitle2 = new TLRPC.TL_messages_editChatTitle();
            tL_messages_editChatTitle2.chat_id = j;
            tL_messages_editChatTitle2.title = str;
            tL_messages_editChatTitle = tL_messages_editChatTitle2;
        }
        getConnectionsManager().sendRequest(tL_messages_editChatTitle, new RequestDelegate() {
            @Override
            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                MessagesController.this.lambda$changeChatTitle$294(tLObject, tL_error);
            }
        }, 64);
    }

    public void checkArchiveFolder() {
        if (this.hasArchivedChats || getStoriesController().hasHiddenStories()) {
            boolean[] zArr = {false};
            ensureFolderDialogExists(1, zArr);
            if (zArr[0]) {
                sortDialogs(null);
                getNotificationCenter().lambda$postNotificationNameOnUIThread$1(NotificationCenter.dialogsNeedReload, new Object[0]);
            }
        } else {
            removeFolder(1);
        }
        getNotificationCenter().lambda$postNotificationNameOnUIThread$1(NotificationCenter.updateInterfaces, 0);
    }

    public boolean checkCanOpenChat(Bundle bundle, BaseFragment baseFragment) {
        return checkCanOpenChat(bundle, baseFragment, null);
    }

    public boolean checkCanOpenChat(Bundle bundle, BaseFragment baseFragment, MessageObject messageObject) {
        return checkCanOpenChat(bundle, baseFragment, messageObject, null);
    }

    public boolean checkCanOpenChat(final Bundle bundle, final BaseFragment baseFragment, MessageObject messageObject, final Browser.Progress progress) {
        TLRPC.User user;
        TLRPC.Chat chat;
        ArrayList<Integer> arrayList;
        TLRPC.TL_messages_getMessages tL_messages_getMessages;
        if (bundle != null && baseFragment != null) {
            long j = bundle.getLong("user_id", 0L);
            long j2 = bundle.getLong("chat_id", 0L);
            int i = bundle.getInt("message_id", 0);
            AlertDialog alertDialog = null;
            if (j != 0) {
                user = getUser(Long.valueOf(j));
                chat = null;
            } else if (j2 != 0) {
                chat = getChat(Long.valueOf(j2));
                user = null;
            } else {
                user = null;
                chat = null;
            }
            if (user == null && chat == null) {
                return true;
            }
            String restrictionReason = getRestrictionReason(chat != null ? chat.restriction_reason : user.restriction_reason);
            if (restrictionReason != null) {
                showCantOpenAlert(baseFragment, restrictionReason);
                return false;
            }
            if (i != 0 && messageObject != null && chat != null && chat.access_hash == 0) {
                long dialogId = messageObject.getDialogId();
                if (!DialogObject.isEncryptedDialog(dialogId)) {
                    if (progress != null) {
                        progress.init();
                    } else {
                        alertDialog = new AlertDialog(baseFragment.getParentActivity(), 3);
                    }
                    final AlertDialog alertDialog2 = alertDialog;
                    if (dialogId < 0) {
                        chat = getChat(Long.valueOf(-dialogId));
                    }
                    if (dialogId > 0 || !ChatObject.isChannel(chat)) {
                        TLRPC.TL_messages_getMessages tL_messages_getMessages2 = new TLRPC.TL_messages_getMessages();
                        arrayList = tL_messages_getMessages2.id;
                        tL_messages_getMessages = tL_messages_getMessages2;
                    } else {
                        TLRPC.Chat chat2 = getChat(Long.valueOf(-dialogId));
                        TLRPC.TL_channels_getMessages tL_channels_getMessages = new TLRPC.TL_channels_getMessages();
                        tL_channels_getMessages.channel = getInputChannel(chat2);
                        arrayList = tL_channels_getMessages.id;
                        tL_messages_getMessages = tL_channels_getMessages;
                    }
                    arrayList.add(Integer.valueOf(messageObject.getId()));
                    final int sendRequest = getConnectionsManager().sendRequest(tL_messages_getMessages, new RequestDelegate() {
                        @Override
                        public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                            MessagesController.this.lambda$checkCanOpenChat$404(alertDialog2, progress, baseFragment, bundle, tLObject, tL_error);
                        }
                    });
                    if (alertDialog2 != null) {
                        alertDialog2.setOnCancelListener(new DialogInterface.OnCancelListener() {
                            @Override
                            public final void onCancel(DialogInterface dialogInterface) {
                                MessagesController.this.lambda$checkCanOpenChat$405(sendRequest, baseFragment, dialogInterface);
                            }
                        });
                        baseFragment.setVisibleDialog(alertDialog2);
                        alertDialog2.show();
                    } else {
                        progress.onCancel(new Runnable() {
                            @Override
                            public final void run() {
                                MessagesController.this.lambda$checkCanOpenChat$406(sendRequest, baseFragment);
                            }
                        });
                        progress.init();
                    }
                    return false;
                }
            } else if (i != 0 && messageObject != null && progress != null) {
                progress.init();
                progress.onCancel(ensureMessagesLoaded(-j2, i, new MessagesLoadedCallback() {
                    final Bundle val$bundle;
                    final BaseFragment val$fragment;
                    final Browser.Progress val$progress;

                    AnonymousClass2(final Browser.Progress progress2, final BaseFragment baseFragment2, final Bundle bundle2) {
                        r2 = progress2;
                        r3 = baseFragment2;
                        r4 = bundle2;
                    }

                    @Override
                    public void onError() {
                        r2.end();
                        r3.presentFragment(new ChatActivity(r4));
                    }

                    @Override
                    public void onMessagesLoaded(boolean z) {
                        r2.end();
                        r3.presentFragment(new ChatActivity(r4));
                    }
                }));
                return false;
            }
        }
        return true;
    }

    public void checkChatInviter(final long j, final boolean z) {
        final TLRPC.Chat chat = getChat(Long.valueOf(j));
        if (!ChatObject.isChannel(chat) || chat.creator || this.gettingChatInviters.indexOfKey(j) >= 0) {
            return;
        }
        this.gettingChatInviters.put(j, Boolean.TRUE);
        TLRPC.TL_channels_getParticipant tL_channels_getParticipant = new TLRPC.TL_channels_getParticipant();
        tL_channels_getParticipant.channel = getInputChannel(j);
        tL_channels_getParticipant.participant = getInputPeer(getUserConfig().getClientUserId());
        getConnectionsManager().sendRequest(tL_channels_getParticipant, new RequestDelegate() {
            @Override
            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                MessagesController.this.lambda$checkChatInviter$352(chat, z, j, tLObject, tL_error);
            }
        });
    }

    public void checkChatlistFolderUpdate(final int i, boolean z) {
        if (i < 0) {
            return;
        }
        final ChatlistUpdatesStat chatlistUpdatesStat = this.chatlistFoldersUpdates.get(i);
        if (chatlistUpdatesStat != null) {
            if (chatlistUpdatesStat.loading) {
                return;
            }
            if (System.currentTimeMillis() - chatlistUpdatesStat.lastRequestTime <= this.chatlistUpdatePeriod * 1000 && !z) {
                return;
            }
        }
        if (chatlistUpdatesStat == null) {
            chatlistUpdatesStat = new ChatlistUpdatesStat();
            this.chatlistFoldersUpdates.put(i, chatlistUpdatesStat);
        }
        chatlistUpdatesStat.loading = false;
        TL_chatlists.TL_chatlists_getChatlistUpdates tL_chatlists_getChatlistUpdates = new TL_chatlists.TL_chatlists_getChatlistUpdates();
        TL_chatlists.TL_inputChatlistDialogFilter tL_inputChatlistDialogFilter = new TL_chatlists.TL_inputChatlistDialogFilter();
        tL_chatlists_getChatlistUpdates.chatlist = tL_inputChatlistDialogFilter;
        tL_inputChatlistDialogFilter.filter_id = i;
        getConnectionsManager().sendRequest(tL_chatlists_getChatlistUpdates, new RequestDelegate() {
            @Override
            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                MessagesController.this.lambda$checkChatlistFolderUpdate$430(i, chatlistUpdatesStat, tLObject, tL_error);
            }
        });
    }

    public void checkIfFolderEmpty(int i) {
        if (i == 0) {
            return;
        }
        getMessagesStorage().checkIfFolderEmpty(i);
    }

    public void checkIsInChat(boolean z, TLRPC.Chat chat, TLRPC.User user, final IsInChatCheckedCallback isInChatCheckedCallback) {
        TLRPC.ChatFull chatFull;
        TLRPC.ChatParticipant chatParticipant;
        ArrayList<TLRPC.ChatParticipant> arrayList;
        TLRPC.ChatParticipant chatParticipant2;
        ArrayList<TLRPC.ChatParticipant> arrayList2;
        if (chat == null || user == null) {
            if (isInChatCheckedCallback != null) {
                isInChatCheckedCallback.run(false, null, null);
                return;
            }
            return;
        }
        if (chat.megagroup || ChatObject.isChannel(chat)) {
            if (z && (chatFull = getChatFull(chat.id)) != null) {
                TLRPC.ChatParticipants chatParticipants = chatFull.participants;
                if (chatParticipants != null && (arrayList = chatParticipants.participants) != null) {
                    int size = arrayList.size();
                    for (int i = 0; i < size; i++) {
                        chatParticipant = chatFull.participants.participants.get(i);
                        if (chatParticipant != null && chatParticipant.user_id == user.id) {
                            break;
                        }
                    }
                }
                chatParticipant = null;
                if (isInChatCheckedCallback != null && chatParticipant != null) {
                    TLRPC.ChatParticipants chatParticipants2 = chatFull.participants;
                    isInChatCheckedCallback.run(true, (chatParticipants2 == null || chatParticipants2.admin_id != user.id) ? null : ChatRightsEditActivity.emptyAdminRights(true), null);
                    return;
                }
            }
            TLRPC.TL_channels_getParticipant tL_channels_getParticipant = new TLRPC.TL_channels_getParticipant();
            tL_channels_getParticipant.channel = getInputChannel(chat.id);
            tL_channels_getParticipant.participant = getInputPeer(user);
            getConnectionsManager().sendRequest(tL_channels_getParticipant, new RequestDelegate() {
                @Override
                public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                    MessagesController.lambda$checkIsInChat$425(MessagesController.IsInChatCheckedCallback.this, tLObject, tL_error);
                }
            });
            return;
        }
        TLRPC.ChatFull chatFull2 = getChatFull(chat.id);
        if (chatFull2 == null) {
            if (isInChatCheckedCallback != null) {
                isInChatCheckedCallback.run(false, null, null);
                return;
            }
            return;
        }
        TLRPC.ChatParticipants chatParticipants3 = chatFull2.participants;
        if (chatParticipants3 != null && (arrayList2 = chatParticipants3.participants) != null) {
            int size2 = arrayList2.size();
            for (int i2 = 0; i2 < size2; i2++) {
                chatParticipant2 = chatFull2.participants.participants.get(i2);
                if (chatParticipant2 != null && chatParticipant2.user_id == user.id) {
                    break;
                }
            }
        }
        chatParticipant2 = null;
        if (isInChatCheckedCallback != null) {
            boolean z2 = chatParticipant2 != null;
            TLRPC.ChatParticipants chatParticipants4 = chatFull2.participants;
            isInChatCheckedCallback.run(z2, (chatParticipants4 == null || chatParticipants4.admin_id != user.id) ? null : ChatRightsEditActivity.emptyAdminRights(true), null);
        }
    }

    public void checkLastDialogMessage(final TLRPC.Dialog dialog, TLRPC.InputPeer inputPeer, final long j) {
        Exception e;
        NativeByteBuffer nativeByteBuffer;
        if (DialogObject.isEncryptedDialog(dialog.id) || this.checkingLastMessagesDialogs.indexOfKey(dialog.id) >= 0) {
            return;
        }
        TLRPC.TL_messages_getHistory tL_messages_getHistory = new TLRPC.TL_messages_getHistory();
        if (inputPeer == null) {
            inputPeer = getInputPeer(dialog.id);
        }
        tL_messages_getHistory.peer = inputPeer;
        if (inputPeer == null) {
            return;
        }
        tL_messages_getHistory.limit = 1;
        this.checkingLastMessagesDialogs.put(dialog.id, Boolean.TRUE);
        if (BuildVars.LOGS_ENABLED) {
            FileLog.d("checkLastDialogMessage for " + dialog.id);
        }
        if (j == 0) {
            try {
                nativeByteBuffer = new NativeByteBuffer(tL_messages_getHistory.peer.getObjectSize() + 60);
            } catch (Exception e2) {
                e = e2;
                nativeByteBuffer = null;
            }
            try {
                nativeByteBuffer.writeInt32(14);
                nativeByteBuffer.writeInt64(dialog.id);
                nativeByteBuffer.writeInt32(dialog.top_message);
                nativeByteBuffer.writeInt32(dialog.read_inbox_max_id);
                nativeByteBuffer.writeInt32(dialog.read_outbox_max_id);
                nativeByteBuffer.writeInt32(dialog.unread_count);
                nativeByteBuffer.writeInt32(dialog.last_message_date);
                nativeByteBuffer.writeInt32(dialog.pts);
                nativeByteBuffer.writeInt32(dialog.flags);
                nativeByteBuffer.writeBool(dialog.pinned);
                nativeByteBuffer.writeInt32(dialog.pinnedNum);
                nativeByteBuffer.writeInt32(dialog.unread_mentions_count);
                nativeByteBuffer.writeBool(dialog.unread_mark);
                nativeByteBuffer.writeInt32(dialog.folder_id);
                tL_messages_getHistory.peer.serializeToStream(nativeByteBuffer);
            } catch (Exception e3) {
                e = e3;
                FileLog.e(e);
                j = getMessagesStorage().createPendingTask(nativeByteBuffer);
                getConnectionsManager().sendRequest(tL_messages_getHistory, new RequestDelegate() {
                    @Override
                    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                        MessagesController.this.lambda$checkLastDialogMessage$215(dialog, j, tLObject, tL_error);
                    }
                });
            }
            j = getMessagesStorage().createPendingTask(nativeByteBuffer);
        }
        getConnectionsManager().sendRequest(tL_messages_getHistory, new RequestDelegate() {
            @Override
            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                MessagesController.this.lambda$checkLastDialogMessage$215(dialog, j, tLObject, tL_error);
            }
        });
    }

    public void checkPeerColors(boolean z) {
        PeerColors peerColors;
        if (getUserConfig().getCurrentUser() == null) {
            return;
        }
        if (!this.loadingPeerColors && ((peerColors = this.peerColors) == null || peerColors.needUpdate() || z)) {
            this.loadingPeerColors = true;
            TLRPC.TL_help_getPeerColors tL_help_getPeerColors = new TLRPC.TL_help_getPeerColors();
            PeerColors peerColors2 = this.peerColors;
            tL_help_getPeerColors.hash = peerColors2 != null ? peerColors2.hash : 0;
            if (peerColors2 != null && peerColors2.needUpdate()) {
                tL_help_getPeerColors.hash = 0;
            }
            getConnectionsManager().sendRequest(tL_help_getPeerColors, new RequestDelegate() {
                @Override
                public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                    MessagesController.this.lambda$checkPeerColors$443(tLObject, tL_error);
                }
            });
        }
        if (this.loadingProfilePeerColors) {
            return;
        }
        PeerColors peerColors3 = this.profilePeerColors;
        if (peerColors3 == null || peerColors3.needUpdate() || z) {
            this.loadingProfilePeerColors = true;
            TLRPC.TL_help_getPeerProfileColors tL_help_getPeerProfileColors = new TLRPC.TL_help_getPeerProfileColors();
            PeerColors peerColors4 = this.profilePeerColors;
            tL_help_getPeerProfileColors.hash = peerColors4 != null ? peerColors4.hash : 0;
            if (peerColors4 != null && peerColors4.needUpdate()) {
                tL_help_getPeerProfileColors.hash = 0;
            }
            getConnectionsManager().sendRequest(tL_help_getPeerProfileColors, new RequestDelegate() {
                @Override
                public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                    MessagesController.this.lambda$checkPeerColors$445(tLObject, tL_error);
                }
            });
        }
    }

    public void checkPromoInfo(final boolean z) {
        Utilities.stageQueue.postRunnable(new Runnable() {
            @Override
            public final void run() {
                MessagesController.this.lambda$checkPromoInfo$154(z);
            }
        });
    }

    public void checkSensitive(final BaseFragment baseFragment, final long j, final Runnable runnable, final Runnable runnable2) {
        TLRPC.User user;
        ArrayList<TLRPC.RestrictionReason> arrayList = null;
        if (j < 0) {
            TLRPC.Chat chat = getChat(Long.valueOf(-j));
            if (chat != null) {
                arrayList = chat.restriction_reason;
            }
        } else if (j >= 0 && (user = getUser(Long.valueOf(j))) != null) {
            arrayList = user.restriction_reason;
        }
        if (!isSensitive(arrayList) || this.sensitiveAgreed.contains(Long.valueOf(j))) {
            if (runnable != null) {
                runnable.run();
            }
        } else {
            final AlertDialog alertDialog = new AlertDialog(baseFragment.getContext(), 3);
            alertDialog.showDelayed(200L);
            getContentSettings(new Utilities.Callback() {
                @Override
                public final void run(Object obj) {
                    MessagesController.this.lambda$checkSensitive$402(alertDialog, baseFragment, j, runnable, runnable2, (TL_account.contentSettings) obj);
                }
            });
        }
    }

    public void checkUnreadReactions(final long j, final long j2, final SparseBooleanArray sparseBooleanArray) {
        getMessagesStorage().getStorageQueue().postRunnable(new Runnable() {
            @Override
            public final void run() {
                MessagesController.this.lambda$checkUnreadReactions$389(sparseBooleanArray, j2, j);
            }
        });
    }

    public void cleanup() {
        getContactsController().cleanup();
        MediaController.getInstance().cleanup();
        getNotificationsController().cleanup();
        getSendMessagesHelper().cleanup();
        getSecretChatHelper().cleanup();
        getLocationController().cleanup();
        getMediaDataController().cleanup();
        getColorPalette().cleanup();
        getTranslateController().cleanup();
        getSavedMessagesController().cleanup();
        StoriesController storiesController = this.storiesController;
        if (storiesController != null) {
            storiesController.cleanup();
        }
        UnconfirmedAuthController unconfirmedAuthController = this.unconfirmedAuthController;
        if (unconfirmedAuthController != null) {
            unconfirmedAuthController.cleanup();
        }
        this.showFiltersTooltip = false;
        DialogsActivity.dialogsLoaded[this.currentAccount] = false;
        this.notificationsPreferences.edit().clear().commit();
        this.emojiPreferences.edit().putLong("lastGifLoadTime", 0L).putLong("lastStickersLoadTime", 0L).putLong("lastStickersLoadTimeMask", 0L).putLong("lastStickersLoadTimeFavs", 0L).commit();
        this.mainPreferences.edit().remove("archivehint").remove("proximityhint").remove("archivehint_l").remove("gifhint").remove("reminderhint").remove("soundHint").remove("dcDomainName2").remove("webFileDatacenterId").remove("themehint").remove("showFiltersTooltip").remove("transcribeButtonPressed").commit();
        SharedPreferences sharedPreferences = ApplicationLoader.applicationContext.getSharedPreferences("shortcut_widget", 0);
        SharedPreferences.Editor editor = null;
        ArrayList arrayList = null;
        ArrayList arrayList2 = null;
        AppWidgetManager appWidgetManager = null;
        for (Map.Entry<String, ?> entry : sharedPreferences.getAll().entrySet()) {
            String key = entry.getKey();
            if (key.startsWith("account") && ((Integer) entry.getValue()).intValue() == this.currentAccount) {
                Integer parseInt = Utilities.parseInt((CharSequence) key);
                int intValue = parseInt.intValue();
                if (editor == null) {
                    editor = sharedPreferences.edit();
                    appWidgetManager = AppWidgetManager.getInstance(ApplicationLoader.applicationContext);
                }
                editor.putBoolean("deleted" + intValue, true);
                if (sharedPreferences.getInt("type" + intValue, 0) == 0) {
                    if (arrayList == null) {
                        arrayList = new ArrayList();
                    }
                    arrayList.add(parseInt);
                } else {
                    if (arrayList2 == null) {
                        arrayList2 = new ArrayList();
                    }
                    arrayList2.add(parseInt);
                }
            }
        }
        if (editor != null) {
            editor.commit();
        }
        if (arrayList != null) {
            int size = arrayList.size();
            for (int i = 0; i < size; i++) {
                ChatsWidgetProvider.updateWidget(ApplicationLoader.applicationContext, appWidgetManager, ((Integer) arrayList.get(i)).intValue());
            }
        }
        if (arrayList2 != null) {
            int size2 = arrayList2.size();
            for (int i2 = 0; i2 < size2; i2++) {
                ContactsWidgetProvider.updateWidget(ApplicationLoader.applicationContext, appWidgetManager, ((Integer) arrayList2.get(i2)).intValue());
            }
        }
        this.lastScheduledServerQueryTime.clear();
        this.lastQuickReplyServerQueryTime.clear();
        this.lastSavedServerQueryTime.clear();
        this.lastServerQueryTime.clear();
        this.reloadingWebpages.clear();
        this.reloadingWebpagesPending.clear();
        this.reloadingScheduledWebpages.clear();
        this.reloadingScheduledWebpagesPending.clear();
        this.reloadingSavedWebpages.clear();
        this.reloadingSavedWebpagesPending.clear();
        this.sponsoredMessages.clear();
        this.sendAsPeers.clear();
        this.dialogs_dict.clear();
        this.dialogs_read_inbox_max.clear();
        this.loadingPinnedDialogs.clear();
        this.dialogs_read_outbox_max.clear();
        this.exportedChats.clear();
        this.fullUsers.clear();
        this.fullChats.clear();
        this.activeVoiceChatsMap.clear();
        this.loadingGroupCalls.clear();
        this.groupCallsByChatId.clear();
        this.dialogsByFolder.clear();
        this.unreadUnmutedDialogs = 0;
        this.joiningToChannels.clear();
        this.migratedChats.clear();
        this.channelViewsToSend.clear();
        this.pollsToCheck.clear();
        this.pollsToCheckSize = 0;
        this.dialogsServerOnly.clear();
        this.dialogsForward.clear();
        this.allDialogs.clear();
        this.dialogsLoadedTillDate = Integer.MAX_VALUE;
        this.dialogsCanAddUsers.clear();
        this.dialogsMyChannels.clear();
        this.dialogsMyGroups.clear();
        this.dialogsChannelsOnly.clear();
        this.dialogsGroupsOnly.clear();
        this.dialogsUsersOnly.clear();
        this.dialogsForBlock.clear();
        this.dialogMessagesByIds.clear();
        this.dialogMessagesByRandomIds.clear();
        this.channelAdmins.clear();
        this.loadingChannelAdmins.clear();
        this.users.clear();
        this.objectsByUsernames.clear();
        this.chats.clear();
        this.dialogMessage.clear();
        this.deletedHistory.clear();
        this.printingUsers.clear();
        this.printingStrings.clear();
        this.printingStringsTypes.clear();
        this.onlinePrivacy.clear();
        this.loadingPeerSettings.clear();
        this.deletingDialogs.clear();
        this.clearingHistoryDialogs.clear();
        this.lastPrintingStringCount = 0;
        DialogFilter[] dialogFilterArr = this.selectedDialogFilter;
        dialogFilterArr[1] = null;
        dialogFilterArr[0] = null;
        this.dialogFilters.clear();
        this.dialogFiltersById.clear();
        this.loadingSuggestedFilters = false;
        this.loadingRemoteFilters = false;
        this.suggestedFilters.clear();
        this.dialogFiltersLoaded = false;
        this.ignoreSetOnline = false;
        Utilities.stageQueue.postRunnable(new Runnable() {
            @Override
            public final void run() {
                MessagesController.this.lambda$cleanup$47();
            }
        });
        this.createdDialogMainThreadIds.clear();
        this.visibleDialogMainThreadIds.clear();
        this.visibleScheduledDialogMainThreadIds.clear();
        this.blockePeers.clear();
        int i3 = 0;
        while (true) {
            LongSparseArray[] longSparseArrayArr = this.sendingTypings;
            if (i3 >= longSparseArrayArr.length) {
                break;
            }
            LongSparseArray longSparseArray = longSparseArrayArr[i3];
            if (longSparseArray != null) {
                longSparseArray.clear();
            }
            i3++;
        }
        this.loadingFullUsers.clear();
        this.loadedFullUsers.clear();
        this.reloadingMessages.clear();
        this.loadingFullChats.clear();
        this.loadingFullParticipants.clear();
        this.loadedFullParticipants.clear();
        this.loadedFullChats.clear();
        this.dialogsLoaded = false;
        this.nextDialogsCacheOffset.clear();
        this.loadingDialogs.clear();
        this.dialogsEndReached.clear();
        this.serverDialogsEndReached.clear();
        this.checkingTosUpdate = false;
        this.nextTosCheckTime = 0;
        this.nextPromoInfoCheckTime = 0;
        this.checkingPromoInfo = false;
        this.loadingUnreadDialogs = false;
        this.currentDeletingTaskTime = 0;
        this.currentDeletingTaskMids = null;
        this.currentDeletingTaskMediaMids = null;
        this.gettingNewDeleteTask = false;
        this.loadingBlockedPeers = false;
        this.totalBlockedCount = -1;
        this.blockedEndReached = false;
        this.firstGettingTask = false;
        this.updatingState = false;
        this.resetingDialogs = false;
        this.lastStatusUpdateTime = 0L;
        this.offlineSent = false;
        this.registeringForPush = false;
        this.getDifferenceFirstSync = true;
        this.uploadingAvatar = null;
        this.uploadingWallpaper = null;
        this.uploadingWallpaperInfo = null;
        this.uploadingThemes.clear();
        this.gettingChatInviters.clear();
        this.statusRequest = 0;
        this.statusSettingState = 0;
        Utilities.stageQueue.postRunnable(new Runnable() {
            @Override
            public final void run() {
                MessagesController.this.lambda$cleanup$48();
            }
        });
        if (this.currentDeleteTaskRunnable != null) {
            Utilities.stageQueue.cancelRunnable(this.currentDeleteTaskRunnable);
            this.currentDeleteTaskRunnable = null;
        }
        addSupportUser();
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                MessagesController.this.lambda$cleanup$49();
            }
        });
    }

    public void clearFullUsers() {
        this.loadedFullUsers.clear();
        this.loadedFullChats.clear();
    }

    public void clearQueryTime() {
        this.lastServerQueryTime.clear();
        this.lastScheduledServerQueryTime.clear();
        this.lastQuickReplyServerQueryTime.clear();
        this.lastSavedServerQueryTime.clear();
    }

    public void clearSendAsPeers() {
        this.sendAsPeers.clear();
    }

    public void completeDialogsReset(final TLRPC.messages_Dialogs messages_dialogs, int i, int i2, final int i3, final int i4, final int i5, final LongSparseArray longSparseArray, final LongSparseArray longSparseArray2, TLRPC.Message message) {
        Utilities.stageQueue.postRunnable(new Runnable() {
            @Override
            public final void run() {
                MessagesController.this.lambda$completeDialogsReset$200(i3, i4, i5, messages_dialogs, longSparseArray, longSparseArray2);
            }
        });
    }

    public void convertToGigaGroup(final Context context, TLRPC.Chat chat, final BaseFragment baseFragment, final MessagesStorage.BooleanCallback booleanCallback) {
        final TLRPC.TL_channels_convertToGigagroup tL_channels_convertToGigagroup = new TLRPC.TL_channels_convertToGigagroup();
        tL_channels_convertToGigagroup.channel = getInputChannel(chat);
        AlertDialog alertDialog = context != null ? new AlertDialog(context, 3) : null;
        final AlertDialog alertDialog2 = alertDialog;
        final int sendRequest = getConnectionsManager().sendRequest(tL_channels_convertToGigagroup, new RequestDelegate() {
            @Override
            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                MessagesController.this.lambda$convertToGigaGroup$249(context, alertDialog2, booleanCallback, baseFragment, tL_channels_convertToGigagroup, tLObject, tL_error);
            }
        });
        if (alertDialog != null) {
            alertDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public final void onCancel(DialogInterface dialogInterface) {
                    MessagesController.this.lambda$convertToGigaGroup$250(sendRequest, dialogInterface);
                }
            });
            try {
                alertDialog.showDelayed(400L);
            } catch (Exception unused) {
            }
        }
    }

    public void convertToMegaGroup(Context context, long j, BaseFragment baseFragment, MessagesStorage.LongCallback longCallback) {
        convertToMegaGroup(context, j, baseFragment, longCallback, null);
    }

    public void convertToMegaGroup(final Context context, final long j, final BaseFragment baseFragment, final MessagesStorage.LongCallback longCallback, final Runnable runnable) {
        final TLRPC.TL_messages_migrateChat tL_messages_migrateChat = new TLRPC.TL_messages_migrateChat();
        tL_messages_migrateChat.chat_id = j;
        final AlertDialog alertDialog = context != null ? new AlertDialog(context, 3) : null;
        final int sendRequest = getConnectionsManager().sendRequest(tL_messages_migrateChat, new RequestDelegate() {
            @Override
            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                MessagesController.this.lambda$convertToMegaGroup$244(context, alertDialog, longCallback, j, runnable, baseFragment, tL_messages_migrateChat, tLObject, tL_error);
            }
        });
        if (alertDialog != null) {
            alertDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public final void onCancel(DialogInterface dialogInterface) {
                    MessagesController.this.lambda$convertToMegaGroup$245(sendRequest, dialogInterface);
                }
            });
            try {
                alertDialog.show();
            } catch (Exception unused) {
            }
        }
    }

    public int createChat(String str, ArrayList<Long> arrayList, String str2, int i, boolean z, Location location, String str3, int i2, final BaseFragment baseFragment) {
        if (i == 0 && !z) {
            final TLRPC.TL_messages_createChat tL_messages_createChat = new TLRPC.TL_messages_createChat();
            tL_messages_createChat.title = str;
            if (i2 >= 0) {
                tL_messages_createChat.ttl_period = i2;
                tL_messages_createChat.flags |= 1;
            }
            for (int i3 = 0; i3 < arrayList.size(); i3++) {
                TLRPC.User user = getUser(arrayList.get(i3));
                if (user != null) {
                    tL_messages_createChat.users.add(getInputUser(user));
                }
            }
            return getConnectionsManager().sendRequest(tL_messages_createChat, new RequestDelegate() {
                @Override
                public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                    MessagesController.this.lambda$createChat$237(baseFragment, tL_messages_createChat, tLObject, tL_error);
                }
            }, 2);
        }
        if (!z && i != 2 && i != 4 && i != 5) {
            return 0;
        }
        final TLRPC.TL_channels_createChannel tL_channels_createChannel = new TLRPC.TL_channels_createChannel();
        tL_channels_createChannel.title = str;
        if (str2 == null) {
            str2 = "";
        }
        tL_channels_createChannel.about = str2;
        tL_channels_createChannel.for_import = z;
        if (z || i == 4 || i == 5) {
            tL_channels_createChannel.megagroup = true;
        } else {
            tL_channels_createChannel.broadcast = true;
        }
        tL_channels_createChannel.forum = i == 5;
        if (location != null) {
            TLRPC.TL_inputGeoPoint tL_inputGeoPoint = new TLRPC.TL_inputGeoPoint();
            tL_channels_createChannel.geo_point = tL_inputGeoPoint;
            tL_inputGeoPoint.lat = location.getLatitude();
            tL_channels_createChannel.geo_point._long = location.getLongitude();
            tL_channels_createChannel.address = str3;
            tL_channels_createChannel.flags |= 4;
        }
        return getConnectionsManager().sendRequest(tL_channels_createChannel, new RequestDelegate() {
            @Override
            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                MessagesController.this.lambda$createChat$240(baseFragment, tL_channels_createChannel, tLObject, tL_error);
            }
        }, 2);
    }

    public long createDeleteShowOnceTask(long j, int i) {
        NativeByteBuffer nativeByteBuffer;
        try {
            nativeByteBuffer = new NativeByteBuffer(16);
        } catch (Exception e) {
            e = e;
            nativeByteBuffer = null;
        }
        try {
            nativeByteBuffer.writeInt32(102);
            nativeByteBuffer.writeInt64(j);
            nativeByteBuffer.writeInt32(i);
        } catch (Exception e2) {
            e = e2;
            FileLog.e(e);
            return getMessagesStorage().createPendingTask(nativeByteBuffer);
        }
        return getMessagesStorage().createPendingTask(nativeByteBuffer);
    }

    public void deleteDialog(long j, int i) {
        deleteDialog(j, i, false);
    }

    public void deleteDialog(final long r32, int r34, final int r35, int r36, final boolean r37, final org.telegram.tgnet.TLRPC.InputPeer r38, final long r39) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MessagesController.deleteDialog(long, int, int, int, boolean, org.telegram.tgnet.TLRPC$InputPeer, long):void");
    }

    public void deleteDialog(long j, int i, boolean z) {
        deleteDialog(j, 1, i, 0, z, null, 0L);
    }

    public void deleteMessages(ArrayList<Integer> arrayList, ArrayList<Long> arrayList2, TLRPC.EncryptedChat encryptedChat, long j, int i, boolean z, int i2) {
        deleteMessages(arrayList, arrayList2, encryptedChat, j, z, i2, false, 0L, null, i);
    }

    public void deleteMessages(ArrayList<Integer> arrayList, ArrayList<Long> arrayList2, TLRPC.EncryptedChat encryptedChat, long j, int i, boolean z, int i2, boolean z2) {
        deleteMessages(arrayList, arrayList2, encryptedChat, j, z, i2, z2, 0L, null, i);
    }

    public void deleteMessages(ArrayList<Integer> arrayList, ArrayList<Long> arrayList2, TLRPC.EncryptedChat encryptedChat, long j, boolean z, int i, boolean z2, long j2, TLObject tLObject, int i2) {
        deleteMessages(arrayList, arrayList2, encryptedChat, j, z, i, z2, j2, tLObject, i2, false, 0);
    }

    public void deleteMessages(java.util.ArrayList<java.lang.Integer> r28, java.util.ArrayList<java.lang.Long> r29, org.telegram.tgnet.TLRPC.EncryptedChat r30, long r31, boolean r33, int r34, boolean r35, long r36, org.telegram.tgnet.TLObject r38, int r39, boolean r40, int r41) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MessagesController.deleteMessages(java.util.ArrayList, java.util.ArrayList, org.telegram.tgnet.TLRPC$EncryptedChat, long, boolean, int, boolean, long, org.telegram.tgnet.TLObject, int, boolean, int):void");
    }

    public void deleteMessagesByPush(final long j, final ArrayList<Integer> arrayList, final long j2) {
        getMessagesStorage().getStorageQueue().postRunnable(new Runnable() {
            @Override
            public final void run() {
                MessagesController.this.lambda$deleteMessagesByPush$347(arrayList, j2, j);
            }
        });
    }

    public void deleteMessagesRange(final long j, final long j2, final int i, final int i2, final boolean z, final Runnable runnable) {
        TLRPC.TL_messages_deleteHistory tL_messages_deleteHistory = new TLRPC.TL_messages_deleteHistory();
        tL_messages_deleteHistory.peer = getInputPeer(j);
        tL_messages_deleteHistory.flags = 12;
        tL_messages_deleteHistory.min_date = i;
        tL_messages_deleteHistory.max_date = i2;
        tL_messages_deleteHistory.revoke = z;
        getConnectionsManager().sendRequest(tL_messages_deleteHistory, new RequestDelegate() {
            @Override
            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                MessagesController.this.lambda$deleteMessagesRange$418(j, i, i2, j2, z, runnable, tLObject, tL_error);
            }
        });
    }

    public void deleteParticipantFromChat(long j, TLRPC.InputPeer inputPeer) {
        deleteParticipantFromChat(j, inputPeer, false, false);
    }

    public void deleteParticipantFromChat(long j, TLRPC.InputPeer inputPeer, boolean z, boolean z2) {
        deleteParticipantFromChat(j, inputPeer, z, z2, (Runnable) null);
    }

    public void deleteParticipantFromChat(final long j, TLRPC.InputPeer inputPeer, boolean z, boolean z2, final Runnable runnable) {
        TLRPC.TL_messages_deleteChatUser tL_messages_deleteChatUser;
        TLRPC.TL_messages_deleteChatUser tL_messages_deleteChatUser2;
        if (inputPeer == null) {
            return;
        }
        TLRPC.Chat chat = getChat(Long.valueOf(j));
        boolean z3 = (inputPeer instanceof TLRPC.TL_inputPeerUser) && UserObject.isUserSelf(getMessagesController().getUser(Long.valueOf(inputPeer.user_id)));
        final boolean isChannel = ChatObject.isChannel(chat);
        if (isChannel) {
            if (z3) {
                if (chat.creator && z) {
                    TLRPC.TL_channels_deleteChannel tL_channels_deleteChannel = new TLRPC.TL_channels_deleteChannel();
                    tL_channels_deleteChannel.channel = getInputChannel(chat);
                    tL_messages_deleteChatUser2 = tL_channels_deleteChannel;
                } else {
                    TLRPC.TL_channels_leaveChannel tL_channels_leaveChannel = new TLRPC.TL_channels_leaveChannel();
                    tL_channels_leaveChannel.channel = getInputChannel(chat);
                    tL_messages_deleteChatUser2 = tL_channels_leaveChannel;
                }
                tL_messages_deleteChatUser = tL_messages_deleteChatUser2;
            } else {
                TLRPC.TL_channels_editBanned tL_channels_editBanned = new TLRPC.TL_channels_editBanned();
                tL_channels_editBanned.channel = getInputChannel(chat);
                tL_channels_editBanned.participant = inputPeer;
                TLRPC.TL_chatBannedRights tL_chatBannedRights = new TLRPC.TL_chatBannedRights();
                tL_channels_editBanned.banned_rights = tL_chatBannedRights;
                tL_chatBannedRights.view_messages = true;
                tL_chatBannedRights.send_media = true;
                tL_chatBannedRights.send_messages = true;
                tL_chatBannedRights.send_stickers = true;
                tL_chatBannedRights.send_gifs = true;
                tL_chatBannedRights.send_games = true;
                tL_chatBannedRights.send_inline = true;
                tL_chatBannedRights.embed_links = true;
                tL_chatBannedRights.pin_messages = true;
                tL_chatBannedRights.send_polls = true;
                tL_chatBannedRights.invite_users = true;
                tL_chatBannedRights.change_info = true;
                tL_messages_deleteChatUser = tL_channels_editBanned;
            }
        } else if (z) {
            TLRPC.TL_messages_deleteChat tL_messages_deleteChat = new TLRPC.TL_messages_deleteChat();
            tL_messages_deleteChat.chat_id = j;
            getConnectionsManager().sendRequest(tL_messages_deleteChat, new RequestDelegate() {
                @Override
                public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                    MessagesController.lambda$deleteParticipantFromChat$288(tLObject, tL_error);
                }
            });
            return;
        } else {
            TLRPC.TL_messages_deleteChatUser tL_messages_deleteChatUser3 = new TLRPC.TL_messages_deleteChatUser();
            tL_messages_deleteChatUser3.chat_id = j;
            tL_messages_deleteChatUser3.user_id = getInputUser(inputPeer);
            tL_messages_deleteChatUser3.revoke_history = true;
            tL_messages_deleteChatUser = tL_messages_deleteChatUser3;
        }
        if (z3) {
            deleteDialog(-j, 0, z2);
        }
        final boolean z4 = z3;
        getConnectionsManager().sendRequest(tL_messages_deleteChatUser, new RequestDelegate() {
            @Override
            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                MessagesController.this.lambda$deleteParticipantFromChat$290(isChannel, z4, j, runnable, tLObject, tL_error);
            }
        }, 64);
    }

    public void deleteParticipantFromChat(long j, TLRPC.User user) {
        deleteParticipantFromChat(j, user, (TLRPC.Chat) null, false, false);
    }

    public void deleteParticipantFromChat(final long j, final TLRPC.User user, TLRPC.Chat chat, boolean z, boolean z2) {
        TLRPC.TL_messages_deleteChatUser tL_messages_deleteChatUser;
        if (user == null && chat == null) {
            return;
        }
        TLRPC.InputPeer inputPeer = user != null ? getInputPeer(user) : getInputPeer(chat);
        TLRPC.Chat chat2 = getChat(Long.valueOf(j));
        final boolean isChannel = ChatObject.isChannel(chat2);
        if (isChannel) {
            if (!UserObject.isUserSelf(user)) {
                TLRPC.TL_channels_editBanned tL_channels_editBanned = new TLRPC.TL_channels_editBanned();
                tL_channels_editBanned.channel = getInputChannel(chat2);
                tL_channels_editBanned.participant = inputPeer;
                TLRPC.TL_chatBannedRights tL_chatBannedRights = new TLRPC.TL_chatBannedRights();
                tL_channels_editBanned.banned_rights = tL_chatBannedRights;
                tL_chatBannedRights.view_messages = true;
                tL_chatBannedRights.send_media = true;
                tL_chatBannedRights.send_messages = true;
                tL_chatBannedRights.send_stickers = true;
                tL_chatBannedRights.send_gifs = true;
                tL_chatBannedRights.send_games = true;
                tL_chatBannedRights.send_inline = true;
                tL_chatBannedRights.embed_links = true;
                tL_chatBannedRights.pin_messages = true;
                tL_chatBannedRights.send_polls = true;
                tL_chatBannedRights.invite_users = true;
                tL_chatBannedRights.change_info = true;
                tL_messages_deleteChatUser = tL_channels_editBanned;
            } else if (chat2.creator && z) {
                TLRPC.TL_channels_deleteChannel tL_channels_deleteChannel = new TLRPC.TL_channels_deleteChannel();
                tL_channels_deleteChannel.channel = getInputChannel(chat2);
                tL_messages_deleteChatUser = tL_channels_deleteChannel;
            } else {
                TLRPC.TL_channels_leaveChannel tL_channels_leaveChannel = new TLRPC.TL_channels_leaveChannel();
                tL_channels_leaveChannel.channel = getInputChannel(chat2);
                tL_messages_deleteChatUser = tL_channels_leaveChannel;
            }
        } else if (z) {
            TLRPC.TL_messages_deleteChat tL_messages_deleteChat = new TLRPC.TL_messages_deleteChat();
            tL_messages_deleteChat.chat_id = j;
            getConnectionsManager().sendRequest(tL_messages_deleteChat, new RequestDelegate() {
                @Override
                public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                    MessagesController.lambda$deleteParticipantFromChat$291(tLObject, tL_error);
                }
            });
            return;
        } else {
            TLRPC.TL_messages_deleteChatUser tL_messages_deleteChatUser2 = new TLRPC.TL_messages_deleteChatUser();
            tL_messages_deleteChatUser2.chat_id = j;
            tL_messages_deleteChatUser2.user_id = getInputUser(user);
            tL_messages_deleteChatUser2.revoke_history = true;
            tL_messages_deleteChatUser = tL_messages_deleteChatUser2;
        }
        if (UserObject.isUserSelf(user)) {
            deleteDialog(-j, 0, z2);
        }
        getConnectionsManager().sendRequest(tL_messages_deleteChatUser, new RequestDelegate() {
            @Override
            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                MessagesController.this.lambda$deleteParticipantFromChat$293(isChannel, user, j, tLObject, tL_error);
            }
        }, 64);
    }

    public void deleteSavedDialog(long j) {
        deleteSavedDialog(j, 0);
    }

    protected void deleteSavedDialog(final long j, final int i) {
        final int[] iArr = {i};
        final Runnable runnable = new Runnable() {
            @Override
            public final void run() {
                MessagesController.this.lambda$deleteSavedDialog$134(j, i, iArr);
            }
        };
        if (iArr[0] <= 0) {
            getMessagesStorage().getSavedDialogMaxMessageId(j, new MessagesStorage.IntCallback() {
                @Override
                public final void run(int i2) {
                    MessagesController.lambda$deleteSavedDialog$135(iArr, runnable, i2);
                }
            });
        } else {
            runnable.run();
        }
    }

    public void deleteUserChannelHistory(final TLRPC.Chat chat, final TLRPC.User user, final TLRPC.Chat chat2, int i) {
        long j = user != null ? user.id : chat2 != null ? chat2.id : 0L;
        if (i == 0) {
            getMessagesStorage().deleteUserChatHistory(-chat.id, j);
        }
        TLRPC.TL_channels_deleteParticipantHistory tL_channels_deleteParticipantHistory = new TLRPC.TL_channels_deleteParticipantHistory();
        tL_channels_deleteParticipantHistory.channel = getInputChannel(chat);
        tL_channels_deleteParticipantHistory.participant = user != null ? getInputPeer(user) : getInputPeer(chat2);
        getConnectionsManager().sendRequest(tL_channels_deleteParticipantHistory, new RequestDelegate() {
            @Override
            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                MessagesController.this.lambda$deleteUserChannelHistory$123(chat, user, chat2, tLObject, tL_error);
            }
        });
    }

    public void deleteUserPhoto(TLRPC.InputPhoto inputPhoto) {
        TLRPC.Photo photo;
        final long clientUserId = getUserConfig().getClientUserId();
        if (inputPhoto != null) {
            TLRPC.TL_photos_deletePhotos tL_photos_deletePhotos = new TLRPC.TL_photos_deletePhotos();
            tL_photos_deletePhotos.id.add(inputPhoto);
            getDialogPhotos(clientUserId).removePhoto(inputPhoto.id);
            getConnectionsManager().sendRequest(tL_photos_deletePhotos, null);
            return;
        }
        DialogPhotos dialogPhotos = getDialogPhotos(clientUserId);
        if (dialogPhotos != null && dialogPhotos.photos.size() > 0 && (photo = dialogPhotos.photos.get(0)) != null) {
            dialogPhotos.removePhoto(photo.id);
        }
        TLRPC.TL_photos_updateProfilePhoto tL_photos_updateProfilePhoto = new TLRPC.TL_photos_updateProfilePhoto();
        tL_photos_updateProfilePhoto.id = new TLRPC.TL_inputPhotoEmpty();
        TLRPC.User user = getUser(Long.valueOf(getUserConfig().getClientUserId()));
        if (user == null) {
            user = getUserConfig().getCurrentUser();
        }
        if (user == null) {
            return;
        }
        if (user.photo != null) {
            getMessagesStorage().clearUserPhoto(user.id, user.photo.photo_id);
        }
        getNotificationCenter().lambda$postNotificationNameOnUIThread$1(NotificationCenter.mainUserInfoChanged, new Object[0]);
        getNotificationCenter().lambda$postNotificationNameOnUIThread$1(NotificationCenter.updateInterfaces, Integer.valueOf(UPDATE_MASK_ALL));
        getConnectionsManager().sendRequest(tL_photos_updateProfilePhoto, new RequestDelegate() {
            @Override
            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                MessagesController.this.lambda$deleteUserPhoto$110(clientUserId, tLObject, tL_error);
            }
        });
    }

    public void didAddedNewTask(final int i, final long j, final SparseArray<ArrayList<Integer>> sparseArray) {
        Utilities.stageQueue.postRunnable(new Runnable() {
            @Override
            public final void run() {
                MessagesController.this.lambda$didAddedNewTask$75(i);
            }
        });
        if (sparseArray != null) {
            AndroidUtilities.runOnUIThread(new Runnable() {
                @Override
                public final void run() {
                    MessagesController.this.lambda$didAddedNewTask$76(j, sparseArray);
                }
            });
        }
    }

    public boolean didPressTranscribeButtonEnough() {
        return this.transcribeButtonPressed >= 2;
    }

    @Override
    public void didReceivedNotification(int i, int i2, Object... objArr) {
        MessageObject messageObject;
        TLRPC.InputFile inputFile;
        final Theme.ThemeInfo themeInfo;
        TLRPC.InputFile inputFile2;
        final Theme.ThemeAccent themeAccent;
        TLRPC.TL_theme tL_theme;
        TLRPC.TL_inputThemeSettings tL_inputThemeSettings = null;
        if (i == NotificationCenter.fileUploaded) {
            String str = (String) objArr[0];
            TLRPC.InputFile inputFile3 = (TLRPC.InputFile) objArr[1];
            String str2 = this.uploadingAvatar;
            if (str2 == null || !str2.equals(str)) {
                String str3 = this.uploadingWallpaper;
                if (str3 == null || !str3.equals(str)) {
                    Object obj = this.uploadingThemes.get(str);
                    if (obj instanceof Theme.ThemeInfo) {
                        themeInfo = (Theme.ThemeInfo) obj;
                        if (str.equals(themeInfo.uploadingThumb)) {
                            themeInfo.uploadedThumb = inputFile3;
                            themeInfo.uploadingThumb = null;
                        } else if (str.equals(themeInfo.uploadingFile)) {
                            themeInfo.uploadedFile = inputFile3;
                            themeInfo.uploadingFile = null;
                        }
                        inputFile = themeInfo.uploadedThumb;
                        inputFile2 = themeInfo.uploadedFile;
                        themeAccent = null;
                    } else if (obj instanceof Theme.ThemeAccent) {
                        Theme.ThemeAccent themeAccent2 = (Theme.ThemeAccent) obj;
                        if (str.equals(themeAccent2.uploadingThumb)) {
                            themeAccent2.uploadedThumb = inputFile3;
                            themeAccent2.uploadingThumb = null;
                        } else if (str.equals(themeAccent2.uploadingFile)) {
                            themeAccent2.uploadedFile = inputFile3;
                            themeAccent2.uploadingFile = null;
                        }
                        Theme.ThemeInfo themeInfo2 = themeAccent2.parentTheme;
                        TLRPC.InputFile inputFile4 = themeAccent2.uploadedThumb;
                        TLRPC.InputFile inputFile5 = themeAccent2.uploadedFile;
                        themeInfo = themeInfo2;
                        inputFile = inputFile4;
                        inputFile2 = inputFile5;
                        themeAccent = themeAccent2;
                    } else {
                        inputFile = null;
                        themeInfo = null;
                        inputFile2 = null;
                        themeAccent = null;
                    }
                    this.uploadingThemes.remove(str);
                    if (inputFile2 != null && inputFile != null) {
                        new File(str);
                        TL_account.uploadTheme uploadtheme = new TL_account.uploadTheme();
                        uploadtheme.mime_type = "application/x-tgtheme-android";
                        uploadtheme.file_name = "theme.attheme";
                        uploadtheme.file = inputFile2;
                        inputFile2.name = "theme.attheme";
                        uploadtheme.thumb = inputFile;
                        inputFile.name = "theme-preview.jpg";
                        uploadtheme.flags |= 1;
                        if (themeAccent != null) {
                            themeAccent.uploadedFile = null;
                            themeAccent.uploadedThumb = null;
                            tL_theme = themeAccent.info;
                            tL_inputThemeSettings = new TLRPC.TL_inputThemeSettings();
                            tL_inputThemeSettings.base_theme = Theme.getBaseThemeByKey(themeInfo.name);
                            tL_inputThemeSettings.accent_color = themeAccent.accentColor;
                            int i3 = themeAccent.accentColor2;
                            if (i3 != 0) {
                                tL_inputThemeSettings.flags |= 8;
                                tL_inputThemeSettings.outbox_accent_color = i3;
                            }
                            int i4 = themeAccent.myMessagesAccentColor;
                            if (i4 != 0) {
                                tL_inputThemeSettings.message_colors.add(Integer.valueOf(i4));
                                tL_inputThemeSettings.flags |= 1;
                                int i5 = themeAccent.myMessagesGradientAccentColor1;
                                if (i5 != 0) {
                                    tL_inputThemeSettings.message_colors.add(Integer.valueOf(i5));
                                    int i6 = themeAccent.myMessagesGradientAccentColor2;
                                    if (i6 != 0) {
                                        tL_inputThemeSettings.message_colors.add(Integer.valueOf(i6));
                                        int i7 = themeAccent.myMessagesGradientAccentColor3;
                                        if (i7 != 0) {
                                            tL_inputThemeSettings.message_colors.add(Integer.valueOf(i7));
                                        }
                                    }
                                }
                                tL_inputThemeSettings.message_colors_animated = themeAccent.myMessagesAnimated;
                            }
                            tL_inputThemeSettings.flags |= 2;
                            tL_inputThemeSettings.wallpaper_settings = new TLRPC.TL_wallPaperSettings();
                            if (TextUtils.isEmpty(themeAccent.patternSlug)) {
                                TLRPC.TL_inputWallPaperNoFile tL_inputWallPaperNoFile = new TLRPC.TL_inputWallPaperNoFile();
                                tL_inputWallPaperNoFile.id = 0L;
                                tL_inputThemeSettings.wallpaper = tL_inputWallPaperNoFile;
                            } else {
                                TLRPC.TL_inputWallPaperSlug tL_inputWallPaperSlug = new TLRPC.TL_inputWallPaperSlug();
                                tL_inputWallPaperSlug.slug = themeAccent.patternSlug;
                                tL_inputThemeSettings.wallpaper = tL_inputWallPaperSlug;
                                TLRPC.WallPaperSettings wallPaperSettings = tL_inputThemeSettings.wallpaper_settings;
                                wallPaperSettings.intensity = (int) (themeAccent.patternIntensity * 100.0f);
                                wallPaperSettings.flags |= 8;
                            }
                            TLRPC.WallPaperSettings wallPaperSettings2 = tL_inputThemeSettings.wallpaper_settings;
                            wallPaperSettings2.motion = themeAccent.patternMotion;
                            long j = themeAccent.backgroundOverrideColor;
                            if (j != 0) {
                                wallPaperSettings2.background_color = (int) j;
                                wallPaperSettings2.flags |= 1;
                            }
                            long j2 = themeAccent.backgroundGradientOverrideColor1;
                            if (j2 != 0) {
                                wallPaperSettings2.second_background_color = (int) j2;
                                wallPaperSettings2.flags |= 16;
                                wallPaperSettings2.rotation = AndroidUtilities.getWallpaperRotation(themeAccent.backgroundRotation, true);
                            }
                            long j3 = themeAccent.backgroundGradientOverrideColor2;
                            if (j3 != 0) {
                                TLRPC.WallPaperSettings wallPaperSettings3 = tL_inputThemeSettings.wallpaper_settings;
                                wallPaperSettings3.third_background_color = (int) j3;
                                wallPaperSettings3.flags |= 32;
                            }
                            long j4 = themeAccent.backgroundGradientOverrideColor3;
                            if (j4 != 0) {
                                TLRPC.WallPaperSettings wallPaperSettings4 = tL_inputThemeSettings.wallpaper_settings;
                                wallPaperSettings4.fourth_background_color = (int) j4;
                                wallPaperSettings4.flags |= 64;
                            }
                        } else {
                            themeInfo.uploadedFile = null;
                            themeInfo.uploadedThumb = null;
                            tL_theme = themeInfo.info;
                        }
                        final TLRPC.TL_theme tL_theme2 = tL_theme;
                        final TLRPC.TL_inputThemeSettings tL_inputThemeSettings2 = tL_inputThemeSettings;
                        getConnectionsManager().sendRequest(uploadtheme, new RequestDelegate() {
                            @Override
                            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                                MessagesController.this.lambda$didReceivedNotification$46(tL_theme2, themeInfo, tL_inputThemeSettings2, themeAccent, tLObject, tL_error);
                            }
                        });
                    }
                } else {
                    TL_account.uploadWallPaper uploadwallpaper = new TL_account.uploadWallPaper();
                    uploadwallpaper.file = inputFile3;
                    uploadwallpaper.mime_type = "image/jpeg";
                    final Theme.OverrideWallpaperInfo overrideWallpaperInfo = this.uploadingWallpaperInfo;
                    final String str4 = this.uploadingWallpaper;
                    final TLRPC.TL_wallPaperSettings tL_wallPaperSettings = new TLRPC.TL_wallPaperSettings();
                    tL_wallPaperSettings.blur = overrideWallpaperInfo.isBlurred;
                    tL_wallPaperSettings.motion = overrideWallpaperInfo.isMotion;
                    uploadwallpaper.settings = tL_wallPaperSettings;
                    Theme.OverrideWallpaperInfo overrideWallpaperInfo2 = this.uploadingWallpaperInfo;
                    overrideWallpaperInfo2.uploadingProgress = 1.0f;
                    overrideWallpaperInfo2.requestIds = new ArrayList();
                    this.uploadingWallpaperInfo.requestIds.add(Integer.valueOf(getConnectionsManager().sendRequest(uploadwallpaper, new RequestDelegate() {
                        @Override
                        public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                            MessagesController.this.lambda$didReceivedNotification$40(overrideWallpaperInfo, tL_wallPaperSettings, str4, tLObject, tL_error);
                        }
                    })));
                }
            } else {
                TLRPC.TL_photos_uploadProfilePhoto tL_photos_uploadProfilePhoto = new TLRPC.TL_photos_uploadProfilePhoto();
                tL_photos_uploadProfilePhoto.file = inputFile3;
                tL_photos_uploadProfilePhoto.flags |= 1;
                getConnectionsManager().sendRequest(tL_photos_uploadProfilePhoto, new RequestDelegate() {
                    @Override
                    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                        MessagesController.this.lambda$didReceivedNotification$38(tLObject, tL_error);
                    }
                });
            }
        } else if (i == NotificationCenter.fileUploadFailed) {
            String str5 = (String) objArr[0];
            String str6 = this.uploadingAvatar;
            if (str6 == null || !str6.equals(str5)) {
                String str7 = this.uploadingWallpaper;
                if (str7 == null || !str7.equals(str5)) {
                    Object remove = this.uploadingThemes.remove(str5);
                    if (remove instanceof Theme.ThemeInfo) {
                        Theme.ThemeInfo themeInfo3 = (Theme.ThemeInfo) remove;
                        themeInfo3.uploadedFile = null;
                        themeInfo3.uploadedThumb = null;
                        getNotificationCenter().lambda$postNotificationNameOnUIThread$1(NotificationCenter.themeUploadError, themeInfo3, null);
                    } else if (remove instanceof Theme.ThemeAccent) {
                        Theme.ThemeAccent themeAccent3 = (Theme.ThemeAccent) remove;
                        themeAccent3.uploadingThumb = null;
                        getNotificationCenter().lambda$postNotificationNameOnUIThread$1(NotificationCenter.themeUploadError, themeAccent3.parentTheme, themeAccent3);
                    }
                } else {
                    this.uploadingWallpaper = null;
                    this.uploadingWallpaperInfo = null;
                }
            } else {
                this.uploadingAvatar = null;
            }
        }
        if (i == NotificationCenter.fileUploadProgressChanged) {
            String str8 = (String) objArr[0];
            String str9 = this.uploadingWallpaper;
            if (str9 == null || !str9.equals(str8)) {
                return;
            }
            this.uploadingWallpaperInfo.uploadingProgress = ((float) ((Long) objArr[1]).longValue()) / ((float) ((Long) objArr[2]).longValue());
            return;
        }
        if (i != NotificationCenter.messageReceivedByServer) {
            if (i != NotificationCenter.updateMessageMedia) {
                if (i == NotificationCenter.currentUserPremiumStatusChanged) {
                    loadAppConfig(false);
                    getContactsController().reloadContactsStatusesMaybe(true);
                    if ((!this.storyQualityFull || getUserConfig().isPremium()) && !getUserConfig().isPremium()) {
                        return;
                    }
                    getNotificationCenter().lambda$postNotificationNameOnUIThread$1(NotificationCenter.storyQualityUpdate, new Object[0]);
                    return;
                }
                return;
            }
            TLRPC.Message message = (TLRPC.Message) objArr[0];
            if (message.peer_id.channel_id != 0 || (messageObject = this.dialogMessagesByIds.get(message.id)) == null) {
                return;
            }
            messageObject.messageOwner.media = MessageObject.getMedia(message);
            if (MessageObject.getMedia(message).ttl_seconds != 0) {
                if ((MessageObject.getMedia(message).photo instanceof TLRPC.TL_photoEmpty) || (MessageObject.getMedia(message).document instanceof TLRPC.TL_documentEmpty)) {
                    messageObject.setType();
                    getNotificationCenter().lambda$postNotificationNameOnUIThread$1(NotificationCenter.notificationsSettingsUpdated, new Object[0]);
                    return;
                }
                return;
            }
            return;
        }
        if (((Boolean) objArr[6]).booleanValue()) {
            return;
        }
        Integer num = (Integer) objArr[0];
        Integer num2 = (Integer) objArr[1];
        Long l = (Long) objArr[3];
        ArrayList arrayList = (ArrayList) this.dialogMessage.get(l.longValue());
        for (int i8 = 0; arrayList != null && i8 < arrayList.size(); i8++) {
            MessageObject messageObject2 = (MessageObject) arrayList.get(i8);
            if (messageObject2 != null && (messageObject2.getId() == num.intValue() || messageObject2.messageOwner.local_id == num.intValue())) {
                messageObject2.messageOwner.id = num2.intValue();
                messageObject2.messageOwner.send_state = 0;
            }
            MessageObject messageObject3 = this.dialogMessagesByIds.get(num.intValue());
            if (messageObject3 != null) {
                this.dialogMessagesByIds.remove(num.intValue());
                this.dialogMessagesByIds.put(num2.intValue(), messageObject3);
            }
        }
        TLRPC.Dialog dialog = (TLRPC.Dialog) this.dialogs_dict.get(l.longValue());
        if (dialog != null && dialog.top_message == num.intValue()) {
            dialog.top_message = num2.intValue();
            getNotificationCenter().lambda$postNotificationNameOnUIThread$1(NotificationCenter.dialogsNeedReload, new Object[0]);
        }
        if (DialogObject.isChatDialog(l.longValue())) {
            TLRPC.ChatFull chatFull = (TLRPC.ChatFull) this.fullChats.get(-l.longValue());
            TLRPC.Chat chat = getChat(Long.valueOf(-l.longValue()));
            if (chat == null || ChatObject.hasAdminRights(chat) || chatFull == null || chatFull.slowmode_seconds == 0) {
                return;
            }
            chatFull.slowmode_next_send_date = getConnectionsManager().getCurrentTime() + chatFull.slowmode_seconds;
            chatFull.flags |= 262144;
            getMessagesStorage().updateChatInfo(chatFull, false);
        }
    }

    public void disableAds(boolean z) {
        TLRPC.UserFull userFull = getUserFull(getUserConfig().getClientUserId());
        if (userFull == null) {
            return;
        }
        userFull.sponsored_enabled = false;
        getMessagesStorage().updateUserInfo(userFull, false);
        if (z) {
            TL_account.toggleSponsoredMessages togglesponsoredmessages = new TL_account.toggleSponsoredMessages();
            togglesponsoredmessages.enabled = false;
            getConnectionsManager().sendRequest(togglesponsoredmessages, null);
        }
    }

    public void lambda$checkDeletingTask$78(long j, long j2, int i) {
        getMessagesStorage().removePendingTask(j);
        ArrayList<Integer> arrayList = new ArrayList<>();
        arrayList.add(Integer.valueOf(i));
        getMessagesStorage().emptyMessagesMedia(j2, arrayList);
    }

    public TLRPC.TL_dialogFolder ensureFolderDialogExists(int i, boolean[] zArr) {
        if (i == 0) {
            return null;
        }
        long makeFolderDialogId = DialogObject.makeFolderDialogId(i);
        TLRPC.Dialog dialog = (TLRPC.Dialog) this.dialogs_dict.get(makeFolderDialogId);
        if (dialog instanceof TLRPC.TL_dialogFolder) {
            if (zArr != null) {
                zArr[0] = false;
            }
            return (TLRPC.TL_dialogFolder) dialog;
        }
        if (zArr != null) {
            zArr[0] = true;
        }
        TLRPC.TL_dialogFolder tL_dialogFolder = new TLRPC.TL_dialogFolder();
        tL_dialogFolder.id = makeFolderDialogId;
        tL_dialogFolder.peer = new TLRPC.TL_peerUser();
        TLRPC.TL_folder tL_folder = new TLRPC.TL_folder();
        tL_dialogFolder.folder = tL_folder;
        tL_folder.id = i;
        tL_folder.title = LocaleController.getString(R.string.ArchivedChats);
        tL_dialogFolder.pinned = true;
        int i2 = 0;
        for (int i3 = 0; i3 < this.allDialogs.size(); i3++) {
            TLRPC.Dialog dialog2 = this.allDialogs.get(i3);
            if (!dialog2.pinned) {
                if (dialog2.id != this.promoDialogId) {
                    break;
                }
            } else {
                i2 = Math.max(dialog2.pinnedNum, i2);
            }
        }
        tL_dialogFolder.pinnedNum = i2 + 1;
        TLRPC.TL_messages_dialogs tL_messages_dialogs = new TLRPC.TL_messages_dialogs();
        tL_messages_dialogs.dialogs.add(tL_dialogFolder);
        getMessagesStorage().putDialogs(tL_messages_dialogs, 1);
        this.dialogs_dict.put(makeFolderDialogId, tL_dialogFolder);
        this.allDialogs.add(0, tL_dialogFolder);
        return tL_dialogFolder;
    }

    public Runnable ensureMessagesLoaded(final long j, int i, final MessagesLoadedCallback messagesLoadedCallback) {
        int i2;
        int i3;
        Timer timer;
        long j2;
        long j3;
        boolean z;
        int i4;
        boolean z2;
        int i5;
        int i6;
        int i7;
        int i8;
        long j4;
        int i9;
        int i10;
        int i11;
        int i12;
        boolean z3;
        int i13;
        boolean z4;
        boolean z5;
        boolean z6;
        MessagesController messagesController;
        long j5;
        int i14;
        SharedPreferences notificationsSettings = getNotificationsSettings(this.currentAccount);
        if (i == 0) {
            i2 = notificationsSettings.getInt("diditem" + j, 0);
        } else {
            i2 = i;
        }
        int generateClassGuid = ConnectionsManager.generateClassGuid();
        final long j6 = DialogObject.isChatDialog(j) ? -j : 0L;
        if (j6 != 0 && getMessagesController().getChat(Long.valueOf(j6)) == null) {
            final boolean[] zArr = new boolean[1];
            final Runnable[] runnableArr = new Runnable[1];
            final MessagesStorage messagesStorage = getMessagesStorage();
            final int i15 = i2;
            messagesStorage.getStorageQueue().postRunnable(new Runnable() {
                @Override
                public final void run() {
                    MessagesController.this.lambda$ensureMessagesLoaded$412(zArr, messagesStorage, j6, runnableArr, j, i15, messagesLoadedCallback);
                }
            });
            return new Runnable() {
                @Override
                public final void run() {
                    MessagesController.lambda$ensureMessagesLoaded$413(zArr, runnableArr);
                }
            };
        }
        int i16 = AndroidUtilities.isTablet() ? 30 : 20;
        AnonymousClass3 anonymousClass3 = new NotificationCenter.NotificationCenterDelegate() {
            final MessagesLoadedCallback val$callback;
            final int val$classGuid;
            final int val$count;
            final long val$dialogId;
            final int val$finalMessageId;

            AnonymousClass3(int generateClassGuid2, int i162, int i22, final long j7, final MessagesLoadedCallback messagesLoadedCallback2) {
                r2 = generateClassGuid2;
                r3 = i162;
                r4 = i22;
                r5 = j7;
                r7 = messagesLoadedCallback2;
            }

            @Override
            public void didReceivedNotification(int i17, int i22, Object... objArr) {
                int i32 = NotificationCenter.messagesDidLoadWithoutProcess;
                if (i17 != i32 || ((Integer) objArr[0]).intValue() != r2) {
                    int i42 = NotificationCenter.loadingMessagesFailed;
                    if (i17 == i42 && ((Integer) objArr[0]).intValue() == r2) {
                        MessagesController.this.getNotificationCenter().removeObserver(this, i32);
                        MessagesController.this.getNotificationCenter().removeObserver(this, i42);
                        MessagesLoadedCallback messagesLoadedCallback2 = r7;
                        if (messagesLoadedCallback2 != null) {
                            messagesLoadedCallback2.onError();
                            return;
                        }
                        return;
                    }
                    return;
                }
                int intValue = ((Integer) objArr[1]).intValue();
                boolean booleanValue = ((Boolean) objArr[2]).booleanValue();
                boolean booleanValue2 = ((Boolean) objArr[3]).booleanValue();
                int intValue2 = ((Integer) objArr[4]).intValue();
                int i52 = r3;
                if (intValue < i52 / 2 && !booleanValue2 && booleanValue) {
                    int i62 = r4;
                    MessagesController.this.loadMessagesInternal(r5, 0L, false, i52, i62, 0, false, 0, r2, i62 != 0 ? 3 : 2, intValue2, 0, 0L, -1, 0, 0, 0, false, 0, true, false, false, null, 0L);
                    return;
                }
                MessagesController.this.getNotificationCenter().removeObserver(this, i32);
                MessagesController.this.getNotificationCenter().removeObserver(this, NotificationCenter.loadingMessagesFailed);
                MessagesLoadedCallback messagesLoadedCallback22 = r7;
                if (messagesLoadedCallback22 != null) {
                    messagesLoadedCallback22.onMessagesLoaded(booleanValue);
                }
            }
        };
        getNotificationCenter().addObserver(anonymousClass3, NotificationCenter.messagesDidLoadWithoutProcess);
        getNotificationCenter().addObserver(anonymousClass3, NotificationCenter.loadingMessagesFailed);
        if (i22 != 0) {
            timer = null;
            j2 = 0;
            j3 = 0;
            z = true;
            i4 = 0;
            z2 = true;
            i5 = 0;
            i6 = 3;
            i7 = 0;
            i8 = 0;
            j4 = 0;
            i9 = -1;
            i10 = 0;
            i11 = 0;
            i12 = 0;
            z3 = false;
            i13 = 0;
            z4 = true;
            z5 = false;
            z6 = false;
            messagesController = this;
            j5 = j7;
            i14 = i162;
            i3 = generateClassGuid2;
        } else {
            i3 = generateClassGuid2;
            timer = null;
            j2 = 0;
            j3 = 0;
            z = true;
            i4 = 0;
            z2 = true;
            i5 = 0;
            i6 = 2;
            i7 = 0;
            i8 = 0;
            j4 = 0;
            i9 = -1;
            i10 = 0;
            i11 = 0;
            i12 = 0;
            z3 = false;
            i13 = 0;
            z4 = true;
            z5 = false;
            z6 = false;
            messagesController = this;
            j5 = j7;
            i14 = i162;
        }
        messagesController.loadMessagesInternal(j5, j3, z, i14, i22, i4, z2, i5, i3, i6, i7, i8, j4, i9, i10, i11, i12, z3, i13, z4, z5, z6, timer, j2);
        final int i17 = i3;
        return new Runnable() {
            @Override
            public final void run() {
                MessagesController.this.lambda$ensureMessagesLoaded$414(i17);
            }
        };
    }

    public ArrayList<TLRPC.TL_messages_stickerSet> filterPremiumStickers(ArrayList<TLRPC.TL_messages_stickerSet> arrayList) {
        if (!premiumFeaturesBlocked()) {
            return arrayList;
        }
        int i = 0;
        while (i < arrayList.size()) {
            TLRPC.TL_messages_stickerSet filterPremiumStickers = getInstance(this.currentAccount).filterPremiumStickers(arrayList.get(i));
            if (filterPremiumStickers == null) {
                arrayList.remove(i);
                i--;
            } else {
                arrayList.set(i, filterPremiumStickers);
            }
            i++;
        }
        return arrayList;
    }

    public TLRPC.TL_messages_stickerSet filterPremiumStickers(TLRPC.TL_messages_stickerSet tL_messages_stickerSet) {
        TLRPC.TL_messages_stickerSet tL_messages_stickerSet2;
        Exception e;
        if (!premiumFeaturesBlocked() || tL_messages_stickerSet == null) {
            return tL_messages_stickerSet;
        }
        int i = 0;
        for (int i2 = 0; i2 < tL_messages_stickerSet.documents.size(); i2++) {
            try {
            } catch (Exception e2) {
                tL_messages_stickerSet2 = tL_messages_stickerSet;
                e = e2;
            }
            if (MessageObject.isPremiumSticker(tL_messages_stickerSet.documents.get(i2))) {
                NativeByteBuffer nativeByteBuffer = new NativeByteBuffer(tL_messages_stickerSet.getObjectSize());
                tL_messages_stickerSet.serializeToStream(nativeByteBuffer);
                nativeByteBuffer.position(0);
                tL_messages_stickerSet2 = new TLRPC.TL_messages_stickerSet();
                nativeByteBuffer.readInt32(true);
                tL_messages_stickerSet2.readParams(nativeByteBuffer, true);
                nativeByteBuffer.reuse();
                while (i < tL_messages_stickerSet2.documents.size()) {
                    try {
                        if (MessageObject.isPremiumSticker(tL_messages_stickerSet2.documents.get(i))) {
                            tL_messages_stickerSet2.documents.remove(i);
                            tL_messages_stickerSet2.packs.remove(i);
                            i--;
                            if (tL_messages_stickerSet2.documents.isEmpty()) {
                                return null;
                            }
                        }
                        i++;
                    } catch (Exception e3) {
                        e = e3;
                        e.printStackTrace();
                        return tL_messages_stickerSet2;
                    }
                }
                return tL_messages_stickerSet2;
            }
        }
        return tL_messages_stickerSet;
    }

    public void forceNoReload(long j, int i) {
        LongSparseArray longSparseArray;
        if (i == 1) {
            longSparseArray = this.lastScheduledServerQueryTime;
        } else if (i != 0) {
            return;
        } else {
            longSparseArray = this.lastServerQueryTime;
        }
        longSparseArray.put(j, Long.valueOf(SystemClock.elapsedRealtime()));
    }

    public void forceResetDialogs() {
        resetDialogs(true, getMessagesStorage().getLastSeqValue(), getMessagesStorage().getLastPtsValue(), getMessagesStorage().getLastDateValue(), getMessagesStorage().getLastQtsValue());
        getNotificationsController().deleteAllNotificationChannels();
    }

    public void generateJoinMessage(final long j, boolean z) {
        TLRPC.Chat chat = getChat(Long.valueOf(j));
        if (chat == null || !ChatObject.isChannel(j, this.currentAccount)) {
            return;
        }
        if ((!ChatObject.isNotInChat(chat) || z) && !chat.creator) {
            TLRPC.TL_messageService tL_messageService = new TLRPC.TL_messageService();
            tL_messageService.flags = 256;
            int newMessageId = getUserConfig().getNewMessageId();
            tL_messageService.id = newMessageId;
            tL_messageService.local_id = newMessageId;
            tL_messageService.date = (!(chat instanceof TLRPC.TL_channel) || z) ? getConnectionsManager().getCurrentTime() : chat.date;
            TLRPC.TL_peerUser tL_peerUser = new TLRPC.TL_peerUser();
            tL_messageService.from_id = tL_peerUser;
            tL_peerUser.user_id = getUserConfig().getClientUserId();
            TLRPC.TL_peerChannel tL_peerChannel = new TLRPC.TL_peerChannel();
            tL_messageService.peer_id = tL_peerChannel;
            tL_peerChannel.channel_id = j;
            tL_messageService.dialog_id = -j;
            tL_messageService.post = true;
            TLRPC.TL_messageActionChatAddUser tL_messageActionChatAddUser = new TLRPC.TL_messageActionChatAddUser();
            tL_messageService.action = tL_messageActionChatAddUser;
            tL_messageActionChatAddUser.users.add(Long.valueOf(getUserConfig().getClientUserId()));
            getUserConfig().saveConfig(false);
            final ArrayList arrayList = new ArrayList();
            ArrayList<TLRPC.Message> arrayList2 = new ArrayList<>();
            arrayList2.add(tL_messageService);
            arrayList.add(new MessageObject(this.currentAccount, tL_messageService, true, false));
            getMessagesStorage().putMessages(arrayList2, true, true, false, 0, 0, 0L);
            AndroidUtilities.runOnUIThread(new Runnable() {
                @Override
                public final void run() {
                    MessagesController.this.lambda$generateJoinMessage$345(j, arrayList);
                }
            });
        }
    }

    public int getAboutLimit() {
        return getUserConfig().isPremium() ? this.aboutLengthLimitPremium : this.aboutLengthLimitDefault;
    }

    public ArrayList<Long> getActiveGroupCalls() {
        return new ArrayList<>(this.activeVoiceChatsMap.keySet());
    }

    public TLRPC.ChannelParticipant getAdminInChannel(long j, long j2) {
        LongSparseArray longSparseArray = (LongSparseArray) this.channelAdmins.get(j2);
        if (longSparseArray == null) {
            return null;
        }
        return (TLRPC.ChannelParticipant) longSparseArray.get(j);
    }

    public String getAdminRank(long j, long j2) {
        TLRPC.ChannelParticipant channelParticipant;
        if (j == j2) {
            return "";
        }
        LongSparseArray longSparseArray = (LongSparseArray) this.channelAdmins.get(j);
        if (longSparseArray == null || (channelParticipant = (TLRPC.ChannelParticipant) longSparseArray.get(j2)) == null) {
            return null;
        }
        String str = channelParticipant.rank;
        return str != null ? str : "";
    }

    public ArrayList<TLRPC.Dialog> getAllDialogs() {
        return this.allDialogs;
    }

    public int getAllFoldersDialogsCount() {
        int i = 0;
        for (int i2 = 0; i2 < this.dialogsByFolder.size(); i2++) {
            SparseArray<ArrayList<TLRPC.Dialog>> sparseArray = this.dialogsByFolder;
            ArrayList<TLRPC.Dialog> arrayList = sparseArray.get(sparseArray.keyAt(i2));
            if (arrayList != null) {
                i += arrayList.size();
            }
        }
        return i;
    }

    public TLRPC.messages_AvailableEffects getAvailableEffects() {
        if (!this.loadingAvailableEffects) {
            this.loadingAvailableEffects = true;
            this.effectsFetcher.fetch(this.currentAccount, 0, new Utilities.Callback() {
                @Override
                public final void run(Object obj) {
                    MessagesController.this.lambda$getAvailableEffects$448((TLRPC.messages_AvailableEffects) obj);
                }
            });
        }
        return this.availableEffects;
    }

    public void getBlockedPeers(final boolean z) {
        if (!getUserConfig().isClientActivated() || this.loadingBlockedPeers) {
            return;
        }
        this.loadingBlockedPeers = true;
        final TLRPC.TL_contacts_getBlocked tL_contacts_getBlocked = new TLRPC.TL_contacts_getBlocked();
        tL_contacts_getBlocked.offset = z ? 0 : this.blockePeers.size();
        tL_contacts_getBlocked.limit = z ? 20 : 100;
        getConnectionsManager().sendRequest(tL_contacts_getBlocked, new RequestDelegate() {
            @Override
            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                MessagesController.this.lambda$getBlockedPeers$108(z, tL_contacts_getBlocked, tLObject, tL_error);
            }
        });
    }

    public ChannelBoostsController getBoostsController() {
        ChannelBoostsController channelBoostsController = this.channelBoostsControler;
        if (channelBoostsController != null) {
            return channelBoostsController;
        }
        synchronized (lockObjects[this.currentAccount]) {
            try {
                ChannelBoostsController channelBoostsController2 = this.channelBoostsControler;
                if (channelBoostsController2 != null) {
                    return channelBoostsController2;
                }
                ChannelBoostsController channelBoostsController3 = new ChannelBoostsController(this.currentAccount);
                this.channelBoostsControler = channelBoostsController3;
                return channelBoostsController3;
            } catch (Throwable th) {
                throw th;
            }
        }
    }

    public CacheByChatsController getCacheByChatsController() {
        return this.cacheByChatsController;
    }

    public ChannelRecommendations getCachedChannelRecommendations(long j) {
        HashMap<Long, ChannelRecommendations> hashMap = this.cachedChannelRecommendations;
        if (hashMap == null) {
            return null;
        }
        return hashMap.get(Long.valueOf(j));
    }

    public TLRPC.TL_exportedContactToken getCachedContactToken() {
        if (this.cachedContactToken == null || r0.expires <= System.currentTimeMillis() / 1000) {
            return null;
        }
        return this.cachedContactToken;
    }

    public int getCaptionMaxLengthLimit() {
        return getUserConfig().isPremium() ? this.captionLengthLimitPremium : this.captionLengthLimitDefault;
    }

    public void getChannelDifference(final long r17, final int r19, long r20, org.telegram.tgnet.TLRPC.InputChannel r22) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MessagesController.getChannelDifference(long, int, long, org.telegram.tgnet.TLRPC$InputChannel):void");
    }

    public void getChannelParticipant(TLRPC.Chat chat, TLRPC.User user, final Utilities.Callback<TLRPC.ChannelParticipant> callback) {
        if (chat == null || user == null) {
            if (callback != null) {
                callback.run(null);
            }
        } else {
            TLRPC.TL_channels_getParticipant tL_channels_getParticipant = new TLRPC.TL_channels_getParticipant();
            tL_channels_getParticipant.channel = getInputChannel(chat.id);
            tL_channels_getParticipant.participant = getInputPeer(user);
            getConnectionsManager().sendRequest(tL_channels_getParticipant, new RequestDelegate() {
                @Override
                public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                    MessagesController.lambda$getChannelParticipant$424(Utilities.Callback.this, tLObject, tL_error);
                }
            });
        }
    }

    public ChannelRecommendations getChannelRecommendations(final long j) {
        ChannelRecommendations channelRecommendations;
        TLRPC.TL_channels_getChannelRecommendations tL_channels_getChannelRecommendations;
        if (this.cachedChannelRecommendations == null) {
            this.cachedChannelRecommendations = new HashMap<>();
        }
        final boolean isPremium = getUserConfig().isPremium();
        if (this.cachedChannelRecommendations.containsKey(Long.valueOf(j))) {
            channelRecommendations = this.cachedChannelRecommendations.get(Long.valueOf(j));
            if (channelRecommendations == null || channelRecommendations.wasPremium == isPremium) {
                return channelRecommendations;
            }
        } else {
            channelRecommendations = null;
        }
        if (j > 0) {
            TL_bots.getBotRecommendations getbotrecommendations = new TL_bots.getBotRecommendations();
            TLRPC.InputUser inputUser = getInputUser(j);
            getbotrecommendations.bot = inputUser;
            tL_channels_getChannelRecommendations = getbotrecommendations;
            if (inputUser == null) {
                return null;
            }
        } else {
            TLRPC.TL_channels_getChannelRecommendations tL_channels_getChannelRecommendations2 = new TLRPC.TL_channels_getChannelRecommendations();
            if (j != 0) {
                tL_channels_getChannelRecommendations2.flags |= 1;
                TLRPC.InputChannel inputChannel = getInputChannel(-j);
                tL_channels_getChannelRecommendations2.channel = inputChannel;
                if (inputChannel == null) {
                    return null;
                }
            }
            tL_channels_getChannelRecommendations = tL_channels_getChannelRecommendations2;
        }
        this.cachedChannelRecommendations.put(Long.valueOf(j), null);
        getConnectionsManager().sendRequest(tL_channels_getChannelRecommendations, new RequestDelegate() {
            @Override
            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                MessagesController.this.lambda$getChannelRecommendations$434(isPremium, j, tLObject, tL_error);
            }
        });
        return channelRecommendations;
    }

    public TLRPC.Chat getChat(Long l) {
        return this.chats.get(l);
    }

    public TLRPC.TL_chatAdminRights getChatAdminRightsCached(TLRPC.Chat chat, TLRPC.User user) {
        TLRPC.ChatParticipants chatParticipants;
        ArrayList<TLRPC.ChatParticipant> arrayList;
        TLRPC.ChannelParticipant channelParticipant;
        if (chat != null && user != null) {
            if (UserObject.isUserSelf(user)) {
                return chat.admin_rights;
            }
            TLRPC.ChatFull chatFull = getChatFull(chat.id);
            if (chatFull != null && (chatParticipants = chatFull.participants) != null && (arrayList = chatParticipants.participants) != null) {
                for (int i = 0; i < arrayList.size(); i++) {
                    TLRPC.ChatParticipant chatParticipant = arrayList.get(i);
                    if (chatParticipant != null && chatParticipant.user_id == user.id) {
                        if (!(chatParticipant instanceof TLRPC.TL_chatChannelParticipant) || (channelParticipant = ((TLRPC.TL_chatChannelParticipant) chatParticipant).channelParticipant) == null) {
                            return null;
                        }
                        return channelParticipant.admin_rights;
                    }
                }
            }
        }
        return null;
    }

    public TLRPC.ChatFull getChatFull(long j) {
        return (TLRPC.ChatFull) this.fullChats.get(j);
    }

    public int getChatMaxUniqReactions(long j) {
        TLRPC.ChatFull chatFull = getInstance(this.currentAccount).getChatFull(-j);
        return (chatFull == null || (!(chatFull instanceof TLRPC.TL_chatFull) ? (chatFull.flags2 & 8192) != 0 : (chatFull.flags & 1048576) != 0)) ? this.reactionsUniqMax : chatFull.reactions_limit;
    }

    public int getChatPendingRequestsOnClosed(long j) {
        return this.mainPreferences.getInt("chatPendingRequests" + j, 0);
    }

    public int getChatReactionsCount() {
        if (getUserConfig().isPremium()) {
            return this.reactionsInChatMax;
        }
        return 1;
    }

    public TL_chatlists.TL_chatlists_chatlistUpdates getChatlistFolderUpdates(int i) {
        ChatlistUpdatesStat chatlistUpdatesStat = this.chatlistFoldersUpdates.get(i);
        if (chatlistUpdatesStat == null) {
            return null;
        }
        return chatlistUpdatesStat.lastValue;
    }

    public ConcurrentHashMap<Long, TLRPC.Chat> getChats() {
        return this.chats;
    }

    public TL_account.contentSettings getContentSettings() {
        return this.contentSettings;
    }

    public void getContentSettings(Utilities.Callback<TL_account.contentSettings> callback) {
        if (this.contentSettings != null && System.currentTimeMillis() - this.contentSettingsLoadedTime < 3600000) {
            if (callback != null) {
                callback.run(this.contentSettings);
                return;
            }
            return;
        }
        if (this.contentSettingsCallbacks == null) {
            this.contentSettingsCallbacks = new ArrayList<>();
        }
        if (callback != null) {
            this.contentSettingsCallbacks.add(callback);
        }
        if (this.contentSettingsLoading) {
            return;
        }
        this.contentSettingsLoading = true;
        getConnectionsManager().sendRequest(new TL_account.getContentSettings(), new RequestDelegate() {
            @Override
            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                MessagesController.this.lambda$getContentSettings$454(tLObject, tL_error);
            }
        });
    }

    public TLRPC.Dialog getDialog(long j) {
        return (TLRPC.Dialog) this.dialogs_dict.get(j);
    }

    public ArrayList<DialogFilter> getDialogFilters() {
        ArrayList<DialogFilter> arrayList = this.frozenDialogFilters;
        return arrayList != null ? arrayList : this.dialogFilters;
    }

    public DialogPhotos getDialogPhotos(long j) {
        DialogPhotos dialogPhotos = (DialogPhotos) this.dialogPhotos.get(j);
        if (dialogPhotos != null) {
            return dialogPhotos;
        }
        LongSparseArray longSparseArray = this.dialogPhotos;
        DialogPhotos dialogPhotos2 = new DialogPhotos(j);
        longSparseArray.put(j, dialogPhotos2);
        return dialogPhotos2;
    }

    public int getDialogUnreadCount(TLRPC.Dialog dialog) {
        if (dialog == null) {
            return 0;
        }
        int i = dialog.unread_count;
        TLRPC.Chat chat = getChat(Long.valueOf(-dialog.id));
        return (chat == null || !chat.forum) ? i : this.topicsController.getForumUnreadCount(-dialog.id)[0];
    }

    public ArrayList<TLRPC.Dialog> getDialogs(int i) {
        ArrayList<TLRPC.Dialog> arrayList = this.dialogsByFolder.get(i);
        return arrayList == null ? new ArrayList<>() : arrayList;
    }

    public void getDifference() {
        getDifference(getMessagesStorage().getLastPtsValue(), getMessagesStorage().getLastDateValue(), getMessagesStorage().getLastQtsValue(), false);
    }

    public void getDifference(int i, final int i2, final int i3, boolean z) {
        registerForPush(SharedConfig.pushType, SharedConfig.pushString);
        if (getMessagesStorage().getLastPtsValue() == 0) {
            loadCurrentState();
            return;
        }
        if (z || !this.gettingDifference) {
            this.gettingDifference = true;
            TLRPC.TL_updates_getDifference tL_updates_getDifference = new TLRPC.TL_updates_getDifference();
            tL_updates_getDifference.pts = i;
            tL_updates_getDifference.date = i2;
            tL_updates_getDifference.qts = i3;
            if (this.getDifferenceFirstSync) {
                tL_updates_getDifference.flags |= 1;
                tL_updates_getDifference.pts_total_limit = ApplicationLoader.isConnectedOrConnectingToWiFi() ? 5000 : 1000;
                this.getDifferenceFirstSync = false;
            }
            if (tL_updates_getDifference.date == 0) {
                tL_updates_getDifference.date = getConnectionsManager().getCurrentTime();
            }
            if (BuildVars.LOGS_ENABLED) {
                FileLog.d("start getDifference with date = " + i2 + " pts = " + i + " qts = " + i3);
                FileLog.d("getDifference: isUpdating = true");
            }
            getConnectionsManager().setIsUpdating(true);
            getConnectionsManager().sendRequest(tL_updates_getDifference, new RequestDelegate() {
                @Override
                public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                    MessagesController.this.lambda$getDifference$336(i2, i3, tLObject, tL_error);
                }
            });
        }
    }

    public TLRPC.TL_availableEffect getEffect(long j) {
        getAvailableEffects();
        if (this.availableEffects == null) {
            return null;
        }
        for (int i = 0; i < this.availableEffects.effects.size(); i++) {
            if (this.availableEffects.effects.get(i).id == j) {
                return this.availableEffects.effects.get(i);
            }
        }
        return null;
    }

    public TLRPC.Document getEffectDocument(long j) {
        if (this.availableEffects == null) {
            return null;
        }
        for (int i = 0; i < this.availableEffects.documents.size(); i++) {
            if (this.availableEffects.documents.get(i).id == j) {
                return this.availableEffects.documents.get(i);
            }
        }
        return null;
    }

    public TLRPC.EncryptedChat getEncryptedChat(Integer num) {
        return this.encryptedChats.get(num);
    }

    public TLRPC.EncryptedChat getEncryptedChatDB(int i, boolean z) {
        TLRPC.EncryptedChat encryptedChat = this.encryptedChats.get(Integer.valueOf(i));
        if (encryptedChat != null) {
            if (!z) {
                return encryptedChat;
            }
            if (!(encryptedChat instanceof TLRPC.TL_encryptedChatWaiting) && !(encryptedChat instanceof TLRPC.TL_encryptedChatRequested)) {
                return encryptedChat;
            }
        }
        CountDownLatch countDownLatch = new CountDownLatch(1);
        ArrayList<TLObject> arrayList = new ArrayList<>();
        getMessagesStorage().getEncryptedChat(i, countDownLatch, arrayList);
        try {
            countDownLatch.await();
        } catch (Exception e) {
            FileLog.e(e);
        }
        if (arrayList.size() != 2) {
            return encryptedChat;
        }
        TLRPC.EncryptedChat encryptedChat2 = (TLRPC.EncryptedChat) arrayList.get(0);
        TLRPC.User user = (TLRPC.User) arrayList.get(1);
        putEncryptedChat(encryptedChat2, false);
        putUser(user, true);
        return encryptedChat2;
    }

    public TLRPC.TL_chatInviteExported getExportedInvite(long j) {
        return (TLRPC.TL_chatInviteExported) this.exportedChats.get(j);
    }

    public int getFilterIdByDialogsType(int i) {
        if (i != 7 && i != 8) {
            return 0;
        }
        DialogFilter dialogFilter = this.selectedDialogFilter[i - 7];
        if (dialogFilter == null) {
            return -1;
        }
        return dialogFilter.id;
    }

    public String getFullName(long j) {
        if (j > 0) {
            TLRPC.User user = getUser(Long.valueOf(j));
            if (user != null) {
                return ContactsController.formatName(user.first_name, user.last_name);
            }
            return null;
        }
        TLRPC.Chat chat = getChat(Long.valueOf(-j));
        if (chat != null) {
            return chat.title;
        }
        return null;
    }

    public ChatObject.Call getGroupCall(long j, boolean z) {
        return getGroupCall(j, z, null);
    }

    public ChatObject.Call getGroupCall(final long j, boolean z, final Runnable runnable) {
        TLRPC.TL_inputGroupCall tL_inputGroupCall;
        TLRPC.ChatFull chatFull = getChatFull(j);
        if (chatFull == null || (tL_inputGroupCall = chatFull.call) == null) {
            return null;
        }
        ChatObject.Call call = (ChatObject.Call) this.groupCalls.get(tL_inputGroupCall.id);
        if (call == null && z && !this.loadingGroupCalls.contains(Long.valueOf(j))) {
            this.loadingGroupCalls.add(Long.valueOf(j));
            if (chatFull.call != null) {
                TL_phone.getGroupCall getgroupcall = new TL_phone.getGroupCall();
                getgroupcall.call = chatFull.call;
                getgroupcall.limit = 20;
                getConnectionsManager().sendRequest(getgroupcall, new RequestDelegate() {
                    @Override
                    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                        MessagesController.this.lambda$getGroupCall$57(j, runnable, tLObject, tL_error);
                    }
                });
            }
        }
        if (call == null || !(call.call instanceof TLRPC.TL_groupCallDiscarded)) {
            return call;
        }
        return null;
    }

    public TLRPC.InputChannel getInputChannel(long j) {
        return getInputChannel(getChat(Long.valueOf(j)));
    }

    public TLRPC.InputDocument getInputDocument(TLRPC.Document document) {
        if (document == null) {
            return null;
        }
        TLRPC.TL_inputDocument tL_inputDocument = new TLRPC.TL_inputDocument();
        tL_inputDocument.id = document.id;
        tL_inputDocument.access_hash = document.access_hash;
        byte[] bArr = document.file_reference;
        tL_inputDocument.file_reference = bArr;
        if (bArr == null) {
            tL_inputDocument.file_reference = new byte[0];
        }
        return tL_inputDocument;
    }

    public TLRPC.InputPeer getInputPeer(long j) {
        TLRPC.InputPeer tL_inputPeerUser;
        long j2;
        if (j == getUserConfig().getClientUserId()) {
            return new TLRPC.TL_inputPeerSelf();
        }
        if (j >= 0) {
            TLRPC.User user = getUser(Long.valueOf(j));
            tL_inputPeerUser = new TLRPC.TL_inputPeerUser();
            tL_inputPeerUser.user_id = j;
            if (user != null) {
                j2 = user.access_hash;
            }
            return tL_inputPeerUser;
        }
        long j3 = -j;
        TLRPC.Chat chat = getChat(Long.valueOf(j3));
        if (!ChatObject.isChannel(chat)) {
            TLRPC.TL_inputPeerChat tL_inputPeerChat = new TLRPC.TL_inputPeerChat();
            tL_inputPeerChat.chat_id = j3;
            return tL_inputPeerChat;
        }
        tL_inputPeerUser = new TLRPC.TL_inputPeerChannel();
        tL_inputPeerUser.channel_id = j3;
        j2 = chat.access_hash;
        tL_inputPeerUser.access_hash = j2;
        return tL_inputPeerUser;
    }

    public TLRPC.InputPeer getInputPeer(TLRPC.Peer peer) {
        TLRPC.InputPeer tL_inputPeerUser;
        long j;
        if (peer instanceof TLRPC.TL_peerChat) {
            TLRPC.TL_inputPeerChat tL_inputPeerChat = new TLRPC.TL_inputPeerChat();
            tL_inputPeerChat.chat_id = peer.chat_id;
            return tL_inputPeerChat;
        }
        if (peer instanceof TLRPC.TL_peerChannel) {
            tL_inputPeerUser = new TLRPC.TL_inputPeerChannel();
            long j2 = peer.channel_id;
            tL_inputPeerUser.channel_id = j2;
            TLRPC.Chat chat = getChat(Long.valueOf(j2));
            if (chat == null) {
                return tL_inputPeerUser;
            }
            j = chat.access_hash;
        } else {
            tL_inputPeerUser = new TLRPC.TL_inputPeerUser();
            long j3 = peer.user_id;
            tL_inputPeerUser.user_id = j3;
            TLRPC.User user = getUser(Long.valueOf(j3));
            if (user == null) {
                return tL_inputPeerUser;
            }
            j = user.access_hash;
        }
        tL_inputPeerUser.access_hash = j;
        return tL_inputPeerUser;
    }

    public TLRPC.InputUser getInputUser(long j) {
        return getInputUser(getUser(Long.valueOf(j)));
    }

    public TLRPC.InputUser getInputUser(TLRPC.InputPeer inputPeer) {
        if (inputPeer == null) {
            return new TLRPC.TL_inputUserEmpty();
        }
        if (inputPeer instanceof TLRPC.TL_inputPeerSelf) {
            return new TLRPC.TL_inputUserSelf();
        }
        TLRPC.TL_inputUser tL_inputUser = new TLRPC.TL_inputUser();
        tL_inputUser.user_id = inputPeer.user_id;
        tL_inputUser.access_hash = inputPeer.access_hash;
        return tL_inputUser;
    }

    public TLRPC.InputUser getInputUser(TLRPC.User user) {
        if (user == null) {
            return new TLRPC.TL_inputUserEmpty();
        }
        if (user.id == getUserConfig().getClientUserId()) {
            return new TLRPC.TL_inputUserSelf();
        }
        TLRPC.TL_inputUser tL_inputUser = new TLRPC.TL_inputUser();
        tL_inputUser.user_id = user.id;
        tL_inputUser.access_hash = user.access_hash;
        return tL_inputUser;
    }

    public SharedPreferences getMainSettings() {
        return this.mainPreferences;
    }

    public int getMaxUserReactionsCount() {
        return getUserConfig().isPremium() ? this.reactionsUserMaxPremium : this.reactionsUserMaxDefault;
    }

    public String getMutedString(long j, long j2) {
        if (!getMessagesController().isDialogMuted(j, j2)) {
            return LocaleController.getString(R.string.NotificationsUnmuted);
        }
        int i = this.notificationsPreferences.getInt("notifyuntil_" + NotificationsController.getSharedPrefKey(j, j2), 0);
        return i >= getConnectionsManager().getCurrentTime() ? LocaleController.formatString("NotificationsMutedForHint", R.string.NotificationsMutedForHint, LocaleController.formatTTLString(i)) : LocaleController.getString(R.string.NotificationsMuted);
    }

    public void getNewDeleteTask(final LongSparseArray longSparseArray, final LongSparseArray longSparseArray2) {
        Utilities.stageQueue.postRunnable(new Runnable() {
            @Override
            public final void run() {
                MessagesController.this.lambda$getNewDeleteTask$77(longSparseArray, longSparseArray2);
            }
        });
    }

    public void getNextReactionMention(final long j, final long j2, final int i, final Consumer consumer) {
        getMessagesStorage().getStorageQueue().postRunnable(new Runnable() {
            @Override
            public final void run() {
                MessagesController.this.lambda$getNextReactionMention$5(j2, j, consumer, i);
            }
        });
    }

    public TLRPC.Peer getPeer(long j) {
        if (j >= 0) {
            TLRPC.TL_peerUser tL_peerUser = new TLRPC.TL_peerUser();
            tL_peerUser.user_id = j;
            return tL_peerUser;
        }
        long j2 = -j;
        TLRPC.Chat chat = getChat(Long.valueOf(j2));
        if ((chat instanceof TLRPC.TL_channel) || (chat instanceof TLRPC.TL_channelForbidden)) {
            TLRPC.TL_peerChannel tL_peerChannel = new TLRPC.TL_peerChannel();
            tL_peerChannel.channel_id = j2;
            return tL_peerChannel;
        }
        TLRPC.TL_peerChat tL_peerChat = new TLRPC.TL_peerChat();
        tL_peerChat.chat_id = j2;
        return tL_peerChat;
    }

    public String getPeerName(long j) {
        return getPeerName(j, false);
    }

    public String getPeerName(long j, boolean z) {
        if (j >= 0) {
            TLRPC.User user = getUser(Long.valueOf(j));
            return AndroidUtilities.removeDiacritics(z ? UserObject.getFirstName(user, true) : UserObject.getUserName(user));
        }
        TLRPC.Chat chat = getChat(Long.valueOf(-j));
        return AndroidUtilities.removeDiacritics(chat == null ? "" : chat.title);
    }

    public CharSequence getPrintingString(long j, long j2, boolean z) {
        LongSparseArray longSparseArray;
        TLRPC.User user;
        TLRPC.UserStatus userStatus;
        if ((!z || !DialogObject.isUserDialog(j) || (user = getUser(Long.valueOf(j))) == null || (userStatus = user.status) == null || userStatus.expires >= 0) && (longSparseArray = (LongSparseArray) this.printingStrings.get(j)) != null) {
            return (CharSequence) longSparseArray.get(j2);
        }
        return null;
    }

    public Integer getPrintingStringType(long j, long j2) {
        LongSparseArray longSparseArray = (LongSparseArray) this.printingStringsTypes.get(j);
        if (longSparseArray == null) {
            return null;
        }
        return (Integer) longSparseArray.get(j2);
    }

    public String getRestrictionReason(ArrayList<TLRPC.RestrictionReason> arrayList) {
        if (arrayList.isEmpty()) {
            return null;
        }
        int size = arrayList.size();
        for (int i = 0; i < size; i++) {
            TLRPC.RestrictionReason restrictionReason = arrayList.get(i);
            Set<String> set = this.ignoreRestrictionReasons;
            if ((set == null || !set.contains(restrictionReason.reason)) && !"sensitive".equals(restrictionReason.reason) && ("all".equals(restrictionReason.platform) || !(ApplicationLoader.isStandaloneBuild() || BuildVars.isBetaApp() || !"android".equals(restrictionReason.platform)))) {
                return restrictionReason.text;
            }
        }
        return null;
    }

    public SavedMessagesController getSavedMessagesController() {
        SavedMessagesController savedMessagesController = this.savedMessagesController;
        if (savedMessagesController != null) {
            return savedMessagesController;
        }
        synchronized (lockObjects[this.currentAccount]) {
            try {
                SavedMessagesController savedMessagesController2 = this.savedMessagesController;
                if (savedMessagesController2 != null) {
                    return savedMessagesController2;
                }
                SavedMessagesController savedMessagesController3 = new SavedMessagesController(this.currentAccount);
                this.savedMessagesController = savedMessagesController3;
                return savedMessagesController3;
            } catch (Throwable th) {
                throw th;
            }
        }
    }

    public TLRPC.TL_messages_savedReactionsTags getSavedReactionTags(long j) {
        return getSavedReactionTags(j, false);
    }

    public TLRPC.TL_messages_savedReactionsTags getSavedReactionTags(final long j, boolean z) {
        HashSet<Long> hashSet = this.loadingReactionTags;
        if (hashSet != null && hashSet.contains(Long.valueOf(j)) && !z) {
            LongSparseArray longSparseArray = this.reactionTags;
            if (longSparseArray == null) {
                return null;
            }
            return (TLRPC.TL_messages_savedReactionsTags) longSparseArray.get(j);
        }
        if (this.loadingReactionTags == null) {
            this.loadingReactionTags = new HashSet<>();
        }
        this.loadingReactionTags.add(Long.valueOf(j));
        getMessagesStorage().getStorageQueue().postRunnable(new Runnable() {
            @Override
            public final void run() {
                MessagesController.this.lambda$getSavedReactionTags$440(j);
            }
        });
        return null;
    }

    public int getSavedTagCount(long j, ReactionsLayoutInBubble.VisibleReaction visibleReaction) {
        TLRPC.TL_messages_savedReactionsTags tL_messages_savedReactionsTags;
        LongSparseArray longSparseArray = this.reactionTags;
        if (longSparseArray == null || (tL_messages_savedReactionsTags = (TLRPC.TL_messages_savedReactionsTags) longSparseArray.get(j)) == null) {
            return 0;
        }
        for (int i = 0; i < tL_messages_savedReactionsTags.tags.size(); i++) {
            if (visibleReaction.isSame(tL_messages_savedReactionsTags.tags.get(i).reaction)) {
                return tL_messages_savedReactionsTags.tags.get(i).count;
            }
        }
        return 0;
    }

    public String getSavedTagName(TLRPC.Reaction reaction) {
        TLRPC.TL_messages_savedReactionsTags tL_messages_savedReactionsTags;
        LongSparseArray longSparseArray = this.reactionTags;
        if (longSparseArray == null || (tL_messages_savedReactionsTags = (TLRPC.TL_messages_savedReactionsTags) longSparseArray.get(0L)) == null) {
            return null;
        }
        for (int i = 0; i < tL_messages_savedReactionsTags.tags.size(); i++) {
            if (ReactionsLayoutInBubble.reactionsEqual(reaction, tL_messages_savedReactionsTags.tags.get(i).reaction)) {
                return tL_messages_savedReactionsTags.tags.get(i).title;
            }
        }
        return null;
    }

    public String getSavedTagName(ReactionsLayoutInBubble.VisibleReaction visibleReaction) {
        TLRPC.TL_messages_savedReactionsTags tL_messages_savedReactionsTags;
        LongSparseArray longSparseArray = this.reactionTags;
        if (longSparseArray == null || (tL_messages_savedReactionsTags = (TLRPC.TL_messages_savedReactionsTags) longSparseArray.get(0L)) == null) {
            return null;
        }
        for (int i = 0; i < tL_messages_savedReactionsTags.tags.size(); i++) {
            if (visibleReaction.isSame(tL_messages_savedReactionsTags.tags.get(i).reaction)) {
                return tL_messages_savedReactionsTags.tags.get(i).title;
            }
        }
        return null;
    }

    public TLRPC.TL_channels_sendAsPeers getSendAsPeers(final long j) {
        SendAsPeersInfo sendAsPeersInfo = (SendAsPeersInfo) this.sendAsPeers.get(j);
        if (sendAsPeersInfo != null && (sendAsPeersInfo.loading || Math.abs(SystemClock.elapsedRealtime() - sendAsPeersInfo.loadTime) <= 300000)) {
            return sendAsPeersInfo.sendAsPeers;
        }
        TLRPC.Chat chat = getChat(Long.valueOf(-j));
        if (chat != null && ChatObject.canSendAsPeers(chat)) {
            final SendAsPeersInfo sendAsPeersInfo2 = new SendAsPeersInfo(this, null);
            sendAsPeersInfo2.loading = true;
            this.sendAsPeers.put(j, sendAsPeersInfo2);
            TLRPC.TL_channels_getSendAs tL_channels_getSendAs = new TLRPC.TL_channels_getSendAs();
            tL_channels_getSendAs.peer = getInputPeer(j);
            getConnectionsManager().sendRequest(tL_channels_getSendAs, new RequestDelegate() {
                @Override
                public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                    MessagesController.this.lambda$getSendAsPeers$396(j, sendAsPeersInfo2, tLObject, tL_error);
                }
            });
        }
        return null;
    }

    public TLRPC.Peer getSendAsSelectedPeer(long j) {
        TLRPC.Peer peer;
        TLRPC.ChatFull chatFull = getChatFull(-j);
        if (chatFull != null && (peer = chatFull.default_send_as) != null) {
            return peer;
        }
        TLRPC.TL_peerUser tL_peerUser = new TLRPC.TL_peerUser();
        tL_peerUser.user_id = getAccountInstance().getUserConfig().clientUserId;
        return tL_peerUser;
    }

    public SponsoredMessagesInfo getSponsoredMessages(final long j) {
        SponsoredMessagesInfo sponsoredMessagesInfo = (SponsoredMessagesInfo) this.sponsoredMessages.get(j);
        if (sponsoredMessagesInfo != null && (sponsoredMessagesInfo.loading || Math.abs(SystemClock.elapsedRealtime() - sponsoredMessagesInfo.loadTime) <= 300000)) {
            return sponsoredMessagesInfo;
        }
        if (j >= 0 ? !UserObject.isBot(getUser(Long.valueOf(j))) : !ChatObject.isChannel(getChat(Long.valueOf(-j)))) {
            return null;
        }
        final SponsoredMessagesInfo sponsoredMessagesInfo2 = new SponsoredMessagesInfo();
        sponsoredMessagesInfo2.loading = true;
        this.sponsoredMessages.put(j, sponsoredMessagesInfo2);
        TLRPC.TL_messages_getSponsoredMessages tL_messages_getSponsoredMessages = new TLRPC.TL_messages_getSponsoredMessages();
        tL_messages_getSponsoredMessages.peer = getInputPeer(j);
        getConnectionsManager().sendRequest(tL_messages_getSponsoredMessages, new RequestDelegate() {
            @Override
            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                MessagesController.this.lambda$getSponsoredMessages$393(j, sponsoredMessagesInfo2, tLObject, tL_error);
            }
        });
        return null;
    }

    public StoriesController getStoriesController() {
        StoriesController storiesController = this.storiesController;
        if (storiesController != null) {
            return storiesController;
        }
        synchronized (lockObjects[this.currentAccount]) {
            try {
                StoriesController storiesController2 = this.storiesController;
                if (storiesController2 != null) {
                    return storiesController2;
                }
                StoriesController storiesController3 = new StoriesController(this.currentAccount);
                this.storiesController = storiesController3;
                return storiesController3;
            } catch (Throwable th) {
                throw th;
            }
        }
    }

    public TopicsController getTopicsController() {
        return this.topicsController;
    }

    public int getTotalDialogsCount() {
        ArrayList<TLRPC.Dialog> arrayList = this.dialogsByFolder.get(0);
        if (arrayList != null) {
            return arrayList.size();
        }
        return 0;
    }

    public TranslateController getTranslateController() {
        return this.translateController;
    }

    public UnconfirmedAuthController getUnconfirmedAuthController() {
        UnconfirmedAuthController unconfirmedAuthController = this.unconfirmedAuthController;
        if (unconfirmedAuthController != null) {
            return unconfirmedAuthController;
        }
        synchronized (lockObjects[this.currentAccount]) {
            try {
                UnconfirmedAuthController unconfirmedAuthController2 = this.unconfirmedAuthController;
                if (unconfirmedAuthController2 != null) {
                    return unconfirmedAuthController2;
                }
                UnconfirmedAuthController unconfirmedAuthController3 = new UnconfirmedAuthController(this.currentAccount);
                this.unconfirmedAuthController = unconfirmedAuthController3;
                return unconfirmedAuthController3;
            } catch (Throwable th) {
                throw th;
            }
        }
    }

    public long getUpdatesStartTime(int i) {
        if (i == 0) {
            return this.updatesStartWaitTimeSeq;
        }
        if (i == 1) {
            return this.updatesStartWaitTimePts;
        }
        if (i == 2) {
            return this.updatesStartWaitTimeQts;
        }
        return 0L;
    }

    public TLRPC.User getUser(Long l) {
        return l.longValue() == 0 ? UserConfig.getInstance(this.currentAccount).getCurrentUser() : this.users.get(l);
    }

    public TLRPC.User getUser(String str) {
        TLObject userOrChat = getUserOrChat(str);
        if (userOrChat instanceof TLRPC.User) {
            return (TLRPC.User) userOrChat;
        }
        return null;
    }

    public TLRPC.UserFull getUserFull(long j) {
        return (TLRPC.UserFull) this.fullUsers.get(j);
    }

    public UserNameResolver getUserNameResolver() {
        if (this.userNameResolver == null) {
            this.userNameResolver = new UserNameResolver(this.currentAccount);
        }
        return this.userNameResolver;
    }

    public TLObject getUserOrChat(long j) {
        Map map;
        if (this.users.containsKey(Long.valueOf(j))) {
            map = this.users;
        } else {
            j = -j;
            if (!this.chats.containsKey(Long.valueOf(j))) {
                return null;
            }
            map = this.chats;
        }
        return (TLObject) map.get(Long.valueOf(j));
    }

    public TLObject getUserOrChat(String str) {
        if (str == null || str.length() == 0) {
            return null;
        }
        return this.objectsByUsernames.get(str.toLowerCase());
    }

    public ConcurrentHashMap<Long, TLRPC.User> getUsers() {
        return this.users;
    }

    public boolean hasAvailableEffects() {
        TLRPC.messages_AvailableEffects messages_availableeffects = this.availableEffects;
        return (messages_availableeffects == null || messages_availableeffects.effects.isEmpty()) ? false : true;
    }

    public boolean hasHiddenArchive() {
        return SharedConfig.archiveHidden && this.dialogs_dict.get(DialogObject.makeFolderDialogId(1)) != null;
    }

    public void hidePeerSettingsBar(long j, TLRPC.User user, TLRPC.Chat chat) {
        if (user == null && chat == null) {
            return;
        }
        SharedPreferences.Editor edit = this.notificationsPreferences.edit();
        edit.putInt("dialog_bar_vis3" + j, 3);
        edit.remove("dialog_bar_invite" + j);
        edit.commit();
        if (DialogObject.isEncryptedDialog(j)) {
            return;
        }
        TLRPC.TL_messages_hidePeerSettingsBar tL_messages_hidePeerSettingsBar = new TLRPC.TL_messages_hidePeerSettingsBar();
        tL_messages_hidePeerSettingsBar.peer = getInputPeer(user != null ? user.id : -chat.id);
        getConnectionsManager().sendRequest(tL_messages_hidePeerSettingsBar, new RequestDelegate() {
            @Override
            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                MessagesController.lambda$hidePeerSettingsBar$69(tLObject, tL_error);
            }
        });
    }

    public void hidePromoDialog() {
        if (this.promoDialog == null) {
            return;
        }
        TLRPC.TL_help_hidePromoData tL_help_hidePromoData = new TLRPC.TL_help_hidePromoData();
        tL_help_hidePromoData.peer = getInputPeer(this.promoDialog.id);
        getConnectionsManager().sendRequest(tL_help_hidePromoData, new RequestDelegate() {
            @Override
            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                MessagesController.lambda$hidePromoDialog$125(tLObject, tL_error);
            }
        });
        Utilities.stageQueue.postRunnable(new Runnable() {
            @Override
            public final void run() {
                MessagesController.this.lambda$hidePromoDialog$126();
            }
        });
        removePromoDialog();
    }

    public void incrementMoveCaptionHint() {
        getMainSettings().edit().putInt("movecaptionhint", getMainSettings().getInt("movecaptionhint", 0) + 1).apply();
    }

    public void installTheme(Theme.ThemeInfo themeInfo, Theme.ThemeAccent themeAccent, boolean z) {
        TLRPC.TL_theme tL_theme = themeAccent != null ? themeAccent.info : themeInfo.info;
        String str = themeAccent != null ? themeAccent.patternSlug : themeInfo.slug;
        boolean z2 = themeAccent == null && themeInfo.isBlured;
        boolean z3 = themeAccent != null ? themeAccent.patternMotion : themeInfo.isMotion;
        TL_account.installTheme installtheme = new TL_account.installTheme();
        installtheme.dark = z;
        if (tL_theme != null) {
            installtheme.format = "android";
            TLRPC.TL_inputTheme tL_inputTheme = new TLRPC.TL_inputTheme();
            tL_inputTheme.id = tL_theme.id;
            tL_inputTheme.access_hash = tL_theme.access_hash;
            installtheme.theme = tL_inputTheme;
            installtheme.flags |= 2;
        }
        getConnectionsManager().sendRequest(installtheme, new RequestDelegate() {
            @Override
            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                MessagesController.lambda$installTheme$112(tLObject, tL_error);
            }
        });
        if (TextUtils.isEmpty(str)) {
            return;
        }
        TL_account.installWallPaper installwallpaper = new TL_account.installWallPaper();
        TLRPC.TL_inputWallPaperSlug tL_inputWallPaperSlug = new TLRPC.TL_inputWallPaperSlug();
        tL_inputWallPaperSlug.slug = str;
        installwallpaper.wallpaper = tL_inputWallPaperSlug;
        TLRPC.TL_wallPaperSettings tL_wallPaperSettings = new TLRPC.TL_wallPaperSettings();
        installwallpaper.settings = tL_wallPaperSettings;
        tL_wallPaperSettings.blur = z2;
        tL_wallPaperSettings.motion = z3;
        getConnectionsManager().sendRequest(installwallpaper, new RequestDelegate() {
            @Override
            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                MessagesController.lambda$installTheme$113(tLObject, tL_error);
            }
        });
    }

    public void invalidateChatlistFolderUpdate(int i) {
        this.chatlistFoldersUpdates.remove(i);
        getNotificationCenter().lambda$postNotificationNameOnUIThread$1(NotificationCenter.chatlistFolderUpdate, Integer.valueOf(i));
    }

    public void invalidateContentSettings() {
        this.contentSettings = null;
        this.contentSettingsLoadedTime = 0L;
    }

    public void invalidateUserPremiumBlocked(long j, int i) {
        if (this.loadingFullUsers.contains(Long.valueOf(j))) {
            return;
        }
        int indexOfKey = this.loadedFullUsers.indexOfKey(j);
        if (indexOfKey >= 0) {
            this.loadedFullUsers.removeAt(indexOfKey);
        }
        loadFullUser(getUser(Long.valueOf(j)), i, true);
    }

    public boolean isChannelAdminsLoaded(long j) {
        return this.channelAdmins.get(j) != null;
    }

    public boolean isChatNoForwards(long j) {
        return isChatNoForwards(getChat(Long.valueOf(j)));
    }

    public boolean isChatNoForwards(TLRPC.Chat chat) {
        TLRPC.Chat chat2;
        if (chat == null) {
            return false;
        }
        TLRPC.InputChannel inputChannel = chat.migrated_to;
        return (inputChannel == null || (chat2 = getChat(Long.valueOf(inputChannel.channel_id))) == null) ? chat.noforwards : chat2.noforwards;
    }

    public boolean isClearingDialog(long j) {
        return this.clearingHistoryDialogs.get(j) != null;
    }

    public boolean isDialogMuted(long j, long j2) {
        return isDialogMuted(j, j2, null);
    }

    public boolean isDialogMuted(long j, long j2, TLRPC.Chat chat) {
        Boolean bool;
        int i = this.notificationsPreferences.getInt("notify2_" + NotificationsController.getSharedPrefKey(j, j2), -1);
        boolean z = false;
        if (i == -1) {
            if (chat != null) {
                if (ChatObject.isChannel(chat) && !chat.megagroup) {
                    z = true;
                }
                bool = Boolean.valueOf(z);
            } else {
                bool = null;
            }
            return j2 != 0 ? isDialogMuted(j, 0L, chat) : !getNotificationsController().isGlobalNotificationsEnabled(j, bool, false, false);
        }
        if (i == 2) {
            return true;
        }
        if (i == 3) {
            if (this.notificationsPreferences.getInt("notifyuntil_" + NotificationsController.getSharedPrefKey(j, j2), 0) >= getConnectionsManager().getCurrentTime()) {
                return true;
            }
        }
        return false;
    }

    public boolean isDialogNotificationsSoundEnabled(long j, long j2) {
        return this.notificationsPreferences.getBoolean("sound_enabled_" + NotificationsController.getSharedPrefKey(j, j2), true);
    }

    public boolean isDialogVisible(long j, boolean z) {
        return (z ? this.visibleScheduledDialogMainThreadIds : this.visibleDialogMainThreadIds).contains(Long.valueOf(j));
    }

    public boolean isDialogsEndReached(int i) {
        return this.dialogsEndReached.get(i);
    }

    public boolean isForum(long j) {
        TLRPC.Chat chat = getChat(Long.valueOf(-j));
        return chat != null && chat.forum;
    }

    public boolean isForum(MessageObject messageObject) {
        return messageObject != null && isForum(messageObject.getDialogId());
    }

    public boolean isForum(TLRPC.Message message) {
        return message != null && isForum(MessageObject.getDialogId(message));
    }

    public boolean isHiddenByUndo(long j) {
        return !this.hiddenUndoChats.isEmpty() && this.hiddenUndoChats.contains(Long.valueOf(j));
    }

    public boolean isInChatCached(TLRPC.Chat chat, TLRPC.User user) {
        TLRPC.ChatParticipants chatParticipants;
        ArrayList<TLRPC.ChatParticipant> arrayList;
        if (chat != null && user != null) {
            if (UserObject.isUserSelf(user)) {
                return !ChatObject.isNotInChat(chat);
            }
            TLRPC.ChatFull chatFull = getChatFull(chat.id);
            if (chatFull != null && (chatParticipants = chatFull.participants) != null && (arrayList = chatParticipants.participants) != null) {
                for (int i = 0; i < arrayList.size(); i++) {
                    TLRPC.ChatParticipant chatParticipant = arrayList.get(i);
                    if (chatParticipant != null && chatParticipant.user_id == user.id) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public boolean isJoiningChannel(long j) {
        return this.joiningToChannels.contains(Long.valueOf(j));
    }

    public boolean isLoadingDialogs(int i) {
        return this.loadingDialogs.get(i);
    }

    public boolean isPremiumUser(TLRPC.User user) {
        return (premiumFeaturesBlocked() || !user.premium || isSupportUser(user)) ? false : true;
    }

    public boolean isPromoDialog(long j, boolean z) {
        TLRPC.Dialog dialog = this.promoDialog;
        return dialog != null && dialog.id == j && (!z || this.isLeftPromoChannel);
    }

    public boolean isSensitive(ArrayList<TLRPC.RestrictionReason> arrayList) {
        if (arrayList != null && !arrayList.isEmpty()) {
            int size = arrayList.size();
            for (int i = 0; i < size; i++) {
                TLRPC.RestrictionReason restrictionReason = arrayList.get(i);
                Set<String> set = this.ignoreRestrictionReasons;
                if ((set == null || !set.contains(restrictionReason.reason)) && (("all".equals(restrictionReason.platform) || !(ApplicationLoader.isStandaloneBuild() || BuildVars.isBetaApp() || !"android".equals(restrictionReason.platform))) && "sensitive".equals(restrictionReason.reason))) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean isServerDialogsEndReached(int i) {
        return this.serverDialogsEndReached.get(i);
    }

    public boolean isSponsoredDisabled() {
        if (getUserFull(getUserConfig().getClientUserId()) == null) {
            return false;
        }
        return !r0.sponsored_enabled;
    }

    public boolean isStoryQualityFullOnAccount() {
        return getUserConfig().isPremium() && this.storyQualityFull;
    }

    public boolean isUserPremiumBlocked(long j) {
        return isUserPremiumBlocked(j, false);
    }

    public boolean isUserPremiumBlocked(long j, boolean z) {
        if (!getUserConfig().isPremium() && getUserConfig().getClientUserId() != j) {
            Boolean bool = (Boolean) this.cachedIsUserPremiumBlocked.get(j);
            if (bool != null) {
                return bool.booleanValue();
            }
            TLRPC.User user = getUser(Long.valueOf(j));
            if (user != null && !user.contact_require_premium) {
                return false;
            }
            TLRPC.UserFull userFull = getUserFull(j);
            if (userFull != null) {
                return userFull.contact_require_premium;
            }
            if (getInputUser(j) == null || z) {
                return false;
            }
            this.loadingIsUserPremiumBlocked.add(Long.valueOf(j));
            AndroidUtilities.cancelRunOnUIThread(this.requestIsUserPremiumBlockedRunnable);
            AndroidUtilities.runOnUIThread(this.requestIsUserPremiumBlockedRunnable, 60L);
        }
        return false;
    }

    public void loadAppConfig() {
        loadAppConfig(true);
    }

    public void loadAppConfig(boolean z) {
        AndroidUtilities.cancelRunOnUIThread(this.loadAppConfigRunnable);
        if (z) {
            this.appConfigFetcher.forceRequest(this.currentAccount, 0);
        }
        this.appConfigFetcher.fetch(this.currentAccount, 0, new Utilities.Callback() {
            @Override
            public final void run(Object obj) {
                MessagesController.this.lambda$loadAppConfig$31((TLRPC.TL_help_appConfig) obj);
            }
        });
    }

    public void loadChannelAdmins(final long j, boolean z) {
        if ((SystemClock.elapsedRealtime() / 1000) - this.loadingChannelAdmins.get(j) < 60) {
            return;
        }
        this.loadingChannelAdmins.put(j, (int) (SystemClock.elapsedRealtime() / 1000));
        if (z) {
            getMessagesStorage().loadChannelAdmins(j);
            return;
        }
        TLRPC.TL_channels_getParticipants tL_channels_getParticipants = new TLRPC.TL_channels_getParticipants();
        tL_channels_getParticipants.channel = getInputChannel(j);
        tL_channels_getParticipants.limit = 100;
        tL_channels_getParticipants.filter = new TLRPC.TL_channelParticipantsAdmins();
        getConnectionsManager().sendRequest(tL_channels_getParticipants, new RequestDelegate() {
            @Override
            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                MessagesController.this.lambda$loadChannelAdmins$59(j, tLObject, tL_error);
            }
        });
    }

    public void loadChannelParticipants(Long l) {
        loadChannelParticipants(l, null, 32);
    }

    public void loadChannelParticipants(final Long l, final Utilities.Callback<TLRPC.TL_channels_channelParticipants> callback, int i) {
        if (callback == null && (this.loadingFullParticipants.contains(l) || this.loadedFullParticipants.contains(l))) {
            return;
        }
        this.loadingFullParticipants.add(l);
        TLRPC.TL_channels_getParticipants tL_channels_getParticipants = new TLRPC.TL_channels_getParticipants();
        tL_channels_getParticipants.channel = getInputChannel(l.longValue());
        tL_channels_getParticipants.filter = new TLRPC.TL_channelParticipantsRecent();
        tL_channels_getParticipants.offset = 0;
        tL_channels_getParticipants.limit = i;
        getConnectionsManager().sendRequest(tL_channels_getParticipants, new RequestDelegate() {
            @Override
            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                MessagesController.this.lambda$loadChannelParticipants$139(l, callback, tLObject, tL_error);
            }
        });
    }

    public void loadCurrentState() {
        if (this.updatingState) {
            return;
        }
        this.updatingState = true;
        getConnectionsManager().sendRequest(new TLRPC.TL_updates_getState(), new RequestDelegate() {
            @Override
            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                MessagesController.this.lambda$loadCurrentState$302(tLObject, tL_error);
            }
        });
    }

    public void loadDialogs(int i, int i2, int i3, boolean z) {
        loadDialogs(i, i2, i3, z, null);
    }

    public void loadDialogs(final int i, int i2, final int i3, boolean z, final Runnable runnable) {
        TLRPC.InputPeer tL_inputPeerEmpty;
        long j;
        if (this.loadingDialogs.get(i) || this.resetingDialogs) {
            return;
        }
        this.loadingDialogs.put(i, true);
        NotificationCenter notificationCenter = getNotificationCenter();
        int i4 = NotificationCenter.dialogsNeedReload;
        notificationCenter.lambda$postNotificationNameOnUIThread$1(i4, new Object[0]);
        if (BuildVars.LOGS_ENABLED) {
            FileLog.d("folderId = " + i + " load cacheOffset = " + i2 + " count = " + i3 + " cache = " + z);
        }
        if (z) {
            getMessagesStorage().getDialogs(i, i2 == 0 ? 0 : this.nextDialogsCacheOffset.get(i, 0), i3, i == 0 && i2 == 0);
            return;
        }
        TLRPC.TL_messages_getDialogs tL_messages_getDialogs = new TLRPC.TL_messages_getDialogs();
        tL_messages_getDialogs.limit = i3;
        tL_messages_getDialogs.exclude_pinned = true;
        if (i != 0) {
            tL_messages_getDialogs.flags |= 2;
            tL_messages_getDialogs.folder_id = i;
        }
        long[] dialogLoadOffsets = getUserConfig().getDialogLoadOffsets(i);
        long j2 = dialogLoadOffsets[0];
        if (j2 == -1) {
            ArrayList<TLRPC.Dialog> dialogs = getDialogs(i);
            int size = dialogs.size() - 1;
            while (true) {
                if (size < 0) {
                    tL_inputPeerEmpty = new TLRPC.TL_inputPeerEmpty();
                    break;
                }
                TLRPC.Dialog dialog = dialogs.get(size);
                if (!dialog.pinned && !DialogObject.isEncryptedDialog(dialog.id) && dialog.top_message > 0) {
                    ArrayList arrayList = (ArrayList) this.dialogMessage.get(dialog.id);
                    MessageObject messageObject = null;
                    if (arrayList != null) {
                        for (int i5 = 0; i5 < arrayList.size(); i5++) {
                            MessageObject messageObject2 = (MessageObject) arrayList.get(i5);
                            if (messageObject2 != null && (messageObject == null || messageObject2.getId() > messageObject.getId())) {
                                messageObject = (MessageObject) arrayList.get(i5);
                            }
                        }
                    }
                    if (messageObject != null && messageObject.getId() > 0) {
                        TLRPC.Message message = messageObject.messageOwner;
                        tL_messages_getDialogs.offset_date = message.date;
                        tL_messages_getDialogs.offset_id = message.id;
                        TLRPC.Peer peer = message.peer_id;
                        long j3 = peer.channel_id;
                        if (j3 == 0) {
                            j3 = peer.chat_id;
                            if (j3 == 0) {
                                j = peer.user_id;
                                tL_inputPeerEmpty = getInputPeer(j);
                            }
                        }
                        j = -j3;
                        tL_inputPeerEmpty = getInputPeer(j);
                    }
                }
                size--;
            }
        } else {
            if (j2 == 2147483647L) {
                this.dialogsEndReached.put(i, true);
                this.serverDialogsEndReached.put(i, true);
                this.loadingDialogs.put(i, false);
                getNotificationCenter().lambda$postNotificationNameOnUIThread$1(i4, new Object[0]);
                return;
            }
            int i6 = (int) j2;
            tL_messages_getDialogs.offset_id = i6;
            tL_messages_getDialogs.offset_date = (int) dialogLoadOffsets[1];
            if (i6 != 0) {
                if (dialogLoadOffsets[4] != 0) {
                    TLRPC.TL_inputPeerChannel tL_inputPeerChannel = new TLRPC.TL_inputPeerChannel();
                    tL_messages_getDialogs.offset_peer = tL_inputPeerChannel;
                    tL_inputPeerChannel.channel_id = dialogLoadOffsets[4];
                } else if (dialogLoadOffsets[2] != 0) {
                    TLRPC.TL_inputPeerUser tL_inputPeerUser = new TLRPC.TL_inputPeerUser();
                    tL_messages_getDialogs.offset_peer = tL_inputPeerUser;
                    tL_inputPeerUser.user_id = dialogLoadOffsets[2];
                } else {
                    TLRPC.TL_inputPeerChat tL_inputPeerChat = new TLRPC.TL_inputPeerChat();
                    tL_messages_getDialogs.offset_peer = tL_inputPeerChat;
                    tL_inputPeerChat.chat_id = dialogLoadOffsets[3];
                }
                tL_messages_getDialogs.offset_peer.access_hash = dialogLoadOffsets[5];
                getConnectionsManager().sendRequest(tL_messages_getDialogs, new RequestDelegate() {
                    @Override
                    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                        MessagesController.this.lambda$loadDialogs$187(i, i3, runnable, tLObject, tL_error);
                    }
                });
            }
            tL_inputPeerEmpty = new TLRPC.TL_inputPeerEmpty();
        }
        tL_messages_getDialogs.offset_peer = tL_inputPeerEmpty;
        getConnectionsManager().sendRequest(tL_messages_getDialogs, new RequestDelegate() {
            @Override
            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                MessagesController.this.lambda$loadDialogs$187(i, i3, runnable, tLObject, tL_error);
            }
        });
    }

    public void loadFilterPeers(final HashMap<Long, TLRPC.InputPeer> hashMap, final HashMap<Long, TLRPC.InputPeer> hashMap2, final HashMap<Long, TLRPC.InputPeer> hashMap3, final TLRPC.messages_Dialogs messages_dialogs, final TLRPC.messages_Dialogs messages_dialogs2, final ArrayList<TLRPC.User> arrayList, final ArrayList<TLRPC.Chat> arrayList2, final ArrayList<DialogFilter> arrayList3, final SparseArray<DialogFilter> sparseArray, final ArrayList<Integer> arrayList4, final HashMap<Integer, HashSet<Long>> hashMap4, final HashSet<Integer> hashSet, final Runnable runnable) {
        Utilities.stageQueue.postRunnable(new Runnable() {
            @Override
            public final void run() {
                MessagesController.this.lambda$loadFilterPeers$17(hashMap2, messages_dialogs, messages_dialogs2, arrayList, arrayList2, arrayList3, sparseArray, arrayList4, hashMap4, hashSet, runnable, hashMap3, hashMap);
            }
        });
    }

    public void loadFullChat(final long r12, final int r14, boolean r15) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MessagesController.loadFullChat(long, int, boolean):void");
    }

    public void loadFullUser(TLRPC.User user, int i, boolean z) {
        loadFullUser(user, i, z, null);
    }

    public void loadFullUser(final TLRPC.User user, final int i, boolean z, final Utilities.Callback<TLRPC.UserFull> callback) {
        if (user != null) {
            if (callback == null) {
                if (this.loadingFullUsers.contains(Long.valueOf(user.id))) {
                    return;
                }
                if (!z && this.loadedFullUsers.get(user.id) > 0) {
                    return;
                }
            }
            this.loadingFullUsers.add(Long.valueOf(user.id));
            TLRPC.TL_users_getFullUser tL_users_getFullUser = new TLRPC.TL_users_getFullUser();
            tL_users_getFullUser.id = getInputUser(user);
            final long j = user.id;
            if (this.dialogs_read_inbox_max.get(Long.valueOf(j)) == null || this.dialogs_read_outbox_max.get(Long.valueOf(j)) == null) {
                reloadDialogsReadValue(null, j);
            }
            getConnectionsManager().bindRequestToGuid(getConnectionsManager().sendRequest(tL_users_getFullUser, new RequestDelegate() {
                @Override
                public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                    MessagesController.this.lambda$loadFullUser$66(j, callback, user, i, tLObject, tL_error);
                }
            }), i);
        }
    }

    public void loadGlobalNotificationsSettings() {
        SharedPreferences.Editor editor;
        if (this.loadingNotificationSettings == 0 && !getUserConfig().notificationsSettingsLoaded) {
            SharedPreferences notificationsSettings = getNotificationsSettings(this.currentAccount);
            if (notificationsSettings.contains("EnableGroup")) {
                boolean z = notificationsSettings.getBoolean("EnableGroup", true);
                editor = notificationsSettings.edit();
                if (!z) {
                    editor.putInt("EnableGroup2", Integer.MAX_VALUE);
                    editor.putInt("EnableChannel2", Integer.MAX_VALUE);
                }
                editor.remove("EnableGroup").commit();
            } else {
                editor = null;
            }
            if (notificationsSettings.contains("EnableAll")) {
                boolean z2 = notificationsSettings.getBoolean("EnableAll", true);
                if (editor == null) {
                    editor = notificationsSettings.edit();
                }
                if (!z2) {
                    editor.putInt("EnableAll2", Integer.MAX_VALUE);
                }
                editor.remove("EnableAll").commit();
            }
            if (editor != null) {
                editor.commit();
            }
            this.loadingNotificationSettings = 4;
            final int i = 0;
            while (i < 3) {
                TL_account.getNotifySettings getnotifysettings = new TL_account.getNotifySettings();
                getnotifysettings.peer = i == 0 ? new TLRPC.TL_inputNotifyChats() : i == 1 ? new TLRPC.TL_inputNotifyUsers() : new TLRPC.TL_inputNotifyBroadcasts();
                getConnectionsManager().sendRequest(getnotifysettings, new RequestDelegate() {
                    @Override
                    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                        MessagesController.this.lambda$loadGlobalNotificationsSettings$189(i, tLObject, tL_error);
                    }
                });
                i++;
            }
            getConnectionsManager().sendRequest(new TL_account.getReactionsNotifySettings(), new RequestDelegate() {
                @Override
                public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                    MessagesController.this.lambda$loadGlobalNotificationsSettings$191(tLObject, tL_error);
                }
            });
        }
        if (getUserConfig().notificationsSignUpSettingsLoaded) {
            return;
        }
        loadSignUpNotificationsSettings();
    }

    public void loadHintDialogs() {
        if (!this.hintDialogs.isEmpty() || TextUtils.isEmpty(this.installReferer)) {
            return;
        }
        TLRPC.TL_help_getRecentMeUrls tL_help_getRecentMeUrls = new TLRPC.TL_help_getRecentMeUrls();
        tL_help_getRecentMeUrls.referer = this.installReferer;
        getConnectionsManager().sendRequest(tL_help_getRecentMeUrls, new RequestDelegate() {
            @Override
            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                MessagesController.this.lambda$loadHintDialogs$184(tLObject, tL_error);
            }
        });
    }

    public void loadMessages(long j, long j2, boolean z, int i, int i2, int i3, boolean z2, int i4, int i5, int i6, int i7, int i8, long j3, int i9, int i10, int i11, int i12, boolean z3, int i13, boolean z4) {
        loadMessagesInternal(j, j2, z, i, i2, i3, z2, i4, i5, i6, i7, i8, j3, i9, i10, i11, i12, z3, i13, true, true, z4, null, 0L);
    }

    public void loadMessages(long j, long j2, boolean z, int i, int i2, int i3, boolean z2, int i4, int i5, int i6, int i7, int i8, long j3, int i9, int i10, boolean z3) {
        loadMessages(j, j2, z, i, i2, i3, z2, i4, i5, i6, i7, i8, j3, i10, j3 != 0 ? i9 : 0, 0, 0, false, 0, z3);
    }

    public void loadPeerSettings(TLRPC.User user, TLRPC.Chat chat) {
        if (user == null && chat == null) {
            return;
        }
        final long j = user != null ? user.id : -chat.id;
        if (this.loadingPeerSettings.indexOfKey(j) >= 0) {
            return;
        }
        this.loadingPeerSettings.put(j, Boolean.TRUE);
        if (BuildVars.LOGS_ENABLED) {
            FileLog.d("request spam button for " + j);
        }
        int i = this.notificationsPreferences.getInt("dialog_bar_vis3" + j, 0);
        if (i != 1 && i != 3) {
            TLRPC.TL_messages_getPeerSettings tL_messages_getPeerSettings = new TLRPC.TL_messages_getPeerSettings();
            tL_messages_getPeerSettings.peer = getInputPeer(user != null ? user.id : -chat.id);
            getConnectionsManager().sendRequest(tL_messages_getPeerSettings, new RequestDelegate() {
                @Override
                public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                    MessagesController.this.lambda$loadPeerSettings$74(j, tLObject, tL_error);
                }
            });
        } else if (BuildVars.LOGS_ENABLED) {
            FileLog.d("dialog bar already hidden for " + j);
        }
    }

    public void loadPinnedDialogs(final int i, long j, ArrayList<Long> arrayList) {
        if (this.loadingPinnedDialogs.indexOfKey(i) >= 0 || getUserConfig().isPinnedDialogsLoaded(i)) {
            return;
        }
        this.loadingPinnedDialogs.put(i, 1);
        TLRPC.TL_messages_getPinnedDialogs tL_messages_getPinnedDialogs = new TLRPC.TL_messages_getPinnedDialogs();
        tL_messages_getPinnedDialogs.folder_id = i;
        getConnectionsManager().sendRequest(tL_messages_getPinnedDialogs, new RequestDelegate() {
            @Override
            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                MessagesController.this.lambda$loadPinnedDialogs$344(i, tLObject, tL_error);
            }
        });
    }

    public void loadRemoteFilters(boolean z) {
        loadRemoteFilters(z, null);
    }

    public void loadRemoteFilters(boolean z, Utilities.Callback<Boolean> callback) {
        if (callback != null) {
            this.onLoadedRemoteFilters = callback;
        }
        if (this.loadingRemoteFilters || !getUserConfig().isClientActivated()) {
            return;
        }
        if (z || !getUserConfig().filtersLoaded) {
            if (z) {
                getUserConfig().filtersLoaded = false;
                getUserConfig().saveConfig(false);
            }
            getConnectionsManager().sendRequest(new TLRPC.TL_messages_getDialogFilters(), new RequestDelegate() {
                @Override
                public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                    MessagesController.this.lambda$loadRemoteFilters$27(tLObject, tL_error);
                }
            });
        }
    }

    public void loadSignUpNotificationsSettings() {
        if (this.loadingNotificationSignUpSettings) {
            return;
        }
        this.loadingNotificationSignUpSettings = true;
        getConnectionsManager().sendRequest(new TL_account.getContactSignUpNotification(), new RequestDelegate() {
            @Override
            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                MessagesController.this.lambda$loadSignUpNotificationsSettings$195(tLObject, tL_error);
            }
        });
    }

    public void loadSuggestedFilters() {
        if (this.loadingSuggestedFilters) {
            return;
        }
        this.loadingSuggestedFilters = true;
        getConnectionsManager().sendRequest(new TLRPC.TL_messages_getSuggestedDialogFilters(), new RequestDelegate() {
            @Override
            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                MessagesController.this.lambda$loadSuggestedFilters$22(tLObject, tL_error);
            }
        });
    }

    public void loadUnknownChannel(final TLRPC.Chat chat, final long j) {
        Exception e;
        NativeByteBuffer nativeByteBuffer;
        if (!(chat instanceof TLRPC.TL_channel) || this.gettingUnknownChannels.indexOfKey(chat.id) >= 0) {
            return;
        }
        if (chat.access_hash == 0) {
            if (j != 0) {
                getMessagesStorage().removePendingTask(j);
                return;
            }
            return;
        }
        TLRPC.TL_inputPeerChannel tL_inputPeerChannel = new TLRPC.TL_inputPeerChannel();
        long j2 = chat.id;
        tL_inputPeerChannel.channel_id = j2;
        tL_inputPeerChannel.access_hash = chat.access_hash;
        this.gettingUnknownChannels.put(j2, Boolean.TRUE);
        TLRPC.TL_messages_getPeerDialogs tL_messages_getPeerDialogs = new TLRPC.TL_messages_getPeerDialogs();
        TLRPC.TL_inputDialogPeer tL_inputDialogPeer = new TLRPC.TL_inputDialogPeer();
        tL_inputDialogPeer.peer = tL_inputPeerChannel;
        tL_messages_getPeerDialogs.peers.add(tL_inputDialogPeer);
        if (j == 0) {
            try {
                nativeByteBuffer = new NativeByteBuffer(chat.getObjectSize() + 4);
                try {
                    nativeByteBuffer.writeInt32(0);
                    chat.serializeToStream(nativeByteBuffer);
                } catch (Exception e2) {
                    e = e2;
                    FileLog.e(e);
                    j = getMessagesStorage().createPendingTask(nativeByteBuffer);
                    getConnectionsManager().sendRequest(tL_messages_getPeerDialogs, new RequestDelegate() {
                        @Override
                        public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                            MessagesController.this.lambda$loadUnknownChannel$307(j, chat, tLObject, tL_error);
                        }
                    });
                }
            } catch (Exception e3) {
                e = e3;
                nativeByteBuffer = null;
            }
            j = getMessagesStorage().createPendingTask(nativeByteBuffer);
        }
        getConnectionsManager().sendRequest(tL_messages_getPeerDialogs, new RequestDelegate() {
            @Override
            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                MessagesController.this.lambda$loadUnknownChannel$307(j, chat, tLObject, tL_error);
            }
        });
    }

    public void loadUnknownDialog(TLRPC.InputPeer inputPeer, long j) {
        NativeByteBuffer nativeByteBuffer;
        if (inputPeer == null) {
            return;
        }
        final long peerDialogId = DialogObject.getPeerDialogId(inputPeer);
        if (this.gettingUnknownDialogs.indexOfKey(peerDialogId) >= 0) {
            return;
        }
        this.gettingUnknownDialogs.put(peerDialogId, Boolean.TRUE);
        if (BuildVars.LOGS_ENABLED) {
            FileLog.d("load unknown dialog " + peerDialogId);
        }
        TLRPC.TL_messages_getPeerDialogs tL_messages_getPeerDialogs = new TLRPC.TL_messages_getPeerDialogs();
        TLRPC.TL_inputDialogPeer tL_inputDialogPeer = new TLRPC.TL_inputDialogPeer();
        tL_inputDialogPeer.peer = inputPeer;
        tL_messages_getPeerDialogs.peers.add(tL_inputDialogPeer);
        if (j == 0) {
            try {
                nativeByteBuffer = new NativeByteBuffer(inputPeer.getObjectSize() + 4);
            } catch (Exception e) {
                e = e;
                nativeByteBuffer = null;
            }
            try {
                nativeByteBuffer.writeInt32(15);
                inputPeer.serializeToStream(nativeByteBuffer);
            } catch (Exception e2) {
                e = e2;
                FileLog.e(e);
                j = getMessagesStorage().createPendingTask(nativeByteBuffer);
                final long j2 = j;
                getConnectionsManager().sendRequest(tL_messages_getPeerDialogs, new RequestDelegate() {
                    @Override
                    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                        MessagesController.this.lambda$loadUnknownDialog$196(j2, peerDialogId, tLObject, tL_error);
                    }
                });
            }
            j = getMessagesStorage().createPendingTask(nativeByteBuffer);
        }
        final long j22 = j;
        getConnectionsManager().sendRequest(tL_messages_getPeerDialogs, new RequestDelegate() {
            @Override
            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                MessagesController.this.lambda$loadUnknownDialog$196(j22, peerDialogId, tLObject, tL_error);
            }
        });
    }

    public void loadUnreadDialogs() {
        if (this.loadingUnreadDialogs || getUserConfig().unreadDialogsLoaded) {
            return;
        }
        this.loadingUnreadDialogs = true;
        getConnectionsManager().sendRequest(new TLRPC.TL_messages_getDialogUnreadMarks(), new RequestDelegate() {
            @Override
            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                MessagesController.this.lambda$loadUnreadDialogs$339(tLObject, tL_error);
            }
        });
    }

    public void loadUserInfo(TLRPC.User user, boolean z, int i) {
        loadUserInfo(user, z, i, 0);
    }

    public void loadUserInfo(TLRPC.User user, boolean z, int i, int i2) {
        getMessagesStorage().loadUserInfo(user, z, i, i2);
    }

    public void loadUsersPremiumLocked(final ArrayList<TLRPC.User> arrayList, final Utilities.Callback<ArrayList<Long>> callback) {
        if (callback == null) {
            return;
        }
        if (getUserConfig().isPremium()) {
            callback.run(null);
            return;
        }
        TLRPC.TL_users_getIsPremiumRequiredToContact tL_users_getIsPremiumRequiredToContact = new TLRPC.TL_users_getIsPremiumRequiredToContact();
        MessagesController messagesController = getInstance(this.currentAccount);
        Iterator<TLRPC.User> it = arrayList.iterator();
        while (it.hasNext()) {
            tL_users_getIsPremiumRequiredToContact.id.add(messagesController.getInputUser(it.next()));
        }
        ConnectionsManager.getInstance(this.currentAccount).sendRequest(tL_users_getIsPremiumRequiredToContact, new RequestDelegate() {
            @Override
            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                MessagesController.lambda$loadUsersPremiumLocked$273(arrayList, callback, tLObject, tL_error);
            }
        });
    }

    public void lockFiltersInternal() {
        boolean z;
        if (getUserConfig().isPremium() || this.dialogFilters.size() - 1 <= this.dialogFiltersLimitDefault) {
            z = false;
        } else {
            int size = (this.dialogFilters.size() - 1) - this.dialogFiltersLimitDefault;
            ArrayList arrayList = new ArrayList(this.dialogFilters);
            Collections.reverse(arrayList);
            z = false;
            for (int i = 0; i < arrayList.size(); i++) {
                boolean z2 = ((DialogFilter) arrayList.get(i)).locked;
                if (i < size) {
                    if (!z2) {
                        z = true;
                    }
                    ((DialogFilter) arrayList.get(i)).locked = true;
                } else {
                    if (z2) {
                        z = true;
                    }
                    ((DialogFilter) arrayList.get(i)).locked = false;
                }
            }
        }
        if (z) {
            getNotificationCenter().lambda$postNotificationNameOnUIThread$1(NotificationCenter.dialogFiltersUpdated, new Object[0]);
        }
    }

    public void logDeviceStats() {
        if (!this.collectDeviceStats || this.loggedDeviceStats) {
            return;
        }
        ArrayList<File> rootDirs = AndroidUtilities.getRootDirs();
        if (!rootDirs.isEmpty()) {
            int i = 0;
            String absolutePath = rootDirs.get(0).getAbsolutePath();
            if (!TextUtils.isEmpty(SharedConfig.storageCacheDir)) {
                int size = rootDirs.size();
                while (true) {
                    if (i >= size) {
                        break;
                    }
                    String absolutePath2 = rootDirs.get(i).getAbsolutePath();
                    if (absolutePath2.startsWith(SharedConfig.storageCacheDir)) {
                        absolutePath = absolutePath2;
                        break;
                    }
                    i++;
                }
            }
            boolean contains = absolutePath.contains("/storage/emulated/");
            TLRPC.TL_help_saveAppLog tL_help_saveAppLog = new TLRPC.TL_help_saveAppLog();
            TLRPC.TL_inputAppEvent tL_inputAppEvent = new TLRPC.TL_inputAppEvent();
            tL_inputAppEvent.time = getConnectionsManager().getCurrentTime();
            tL_inputAppEvent.type = "android_sdcard_exists";
            TLRPC.TL_jsonBool tL_jsonBool = new TLRPC.TL_jsonBool();
            tL_jsonBool.value = contains;
            tL_inputAppEvent.data = tL_jsonBool;
            tL_inputAppEvent.peer = contains ? 1L : 0L;
            tL_help_saveAppLog.events.add(tL_inputAppEvent);
            getConnectionsManager().sendRequest(tL_help_saveAppLog, new RequestDelegate() {
                @Override
                public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                    MessagesController.lambda$logDeviceStats$28(tLObject, tL_error);
                }
            });
        }
        this.loggedDeviceStats = true;
    }

    public void markAllTopicsAsRead(final long j) {
        getMessagesStorage().loadTopics(j, new j$.util.function.Consumer() {
            @Override
            public final void r(Object obj) {
                MessagesController.this.lambda$markAllTopicsAsRead$9(j, (ArrayList) obj);
            }

            @Override
            public j$.util.function.Consumer andThen(j$.util.function.Consumer consumer) {
                return Consumer.CC.$default$andThen(this, consumer);
            }
        });
    }

    public void markDialogAsRead(final long j, final int i, final int i2, final int i3, final boolean z, final long j2, final int i4, final boolean z2, int i5) {
        if (j2 == 0) {
            boolean z3 = getNotificationsController().showBadgeMessages;
            if (DialogObject.isEncryptedDialog(j)) {
                if (i3 == 0) {
                    return;
                }
                TLRPC.EncryptedChat encryptedChat = getEncryptedChat(Integer.valueOf(DialogObject.getEncryptedChatId(j)));
                getMessagesStorage().processPendingRead(j, i, i2, i5);
                getMessagesStorage().getStorageQueue().postRunnable(new Runnable() {
                    @Override
                    public final void run() {
                        MessagesController.this.lambda$markDialogAsRead$233(j, i3, z, i4, i2);
                    }
                });
                if (encryptedChat != null && encryptedChat.ttl > 0) {
                    getMessagesStorage().createTaskForSecretChat(encryptedChat.id, i3, Math.max(getConnectionsManager().getCurrentTime(), i3), 0, null);
                }
            } else {
                if (i == 0) {
                    return;
                }
                Integer num = this.dialogs_read_inbox_max.get(Long.valueOf(j));
                if (num == null) {
                    num = 0;
                }
                this.dialogs_read_inbox_max.put(Long.valueOf(j), Integer.valueOf(Math.max(num.intValue(), i)));
                getMessagesStorage().processPendingRead(j, i, i2, i5);
                getMessagesStorage().getStorageQueue().postRunnable(new Runnable() {
                    @Override
                    public final void run() {
                        MessagesController.this.lambda$markDialogAsRead$231(j, i4, i, z);
                    }
                });
                if (i == Integer.MAX_VALUE) {
                    return;
                }
            }
        } else if (i == Integer.MAX_VALUE) {
            return;
        }
        Utilities.stageQueue.postRunnable(new Runnable() {
            @Override
            public final void run() {
                MessagesController.this.lambda$markDialogAsRead$234(j2, j, z2, i3, i);
            }
        });
    }

    public void markDialogAsReadNow(final long j, final long j2) {
        Utilities.stageQueue.postRunnable(new Runnable() {
            @Override
            public final void run() {
                MessagesController.this.lambda$markDialogAsReadNow$228(j2, j);
            }
        });
    }

    public void markDialogAsUnread(long j, TLRPC.InputPeer inputPeer, final long j2) {
        NativeByteBuffer nativeByteBuffer;
        TLRPC.Dialog dialog = (TLRPC.Dialog) this.dialogs_dict.get(j);
        NativeByteBuffer nativeByteBuffer2 = null;
        if (dialog != null) {
            dialog.unread_mark = true;
            if (dialog.unread_count == 0 && !isDialogMuted(j, 0L)) {
                this.unreadUnmutedDialogs++;
            }
            getNotificationCenter().lambda$postNotificationNameOnUIThread$1(NotificationCenter.updateInterfaces, Integer.valueOf(UPDATE_MASK_READ_DIALOG_MESSAGE));
            getMessagesStorage().setDialogUnread(j, true);
            int i = 0;
            while (true) {
                DialogFilter[] dialogFilterArr = this.selectedDialogFilter;
                if (i < dialogFilterArr.length) {
                    DialogFilter dialogFilter = dialogFilterArr[i];
                    if (dialogFilter != null && (dialogFilter.flags & DIALOG_FILTER_FLAG_EXCLUDE_READ) != 0) {
                        sortDialogs(null);
                        getNotificationCenter().lambda$postNotificationNameOnUIThread$1(NotificationCenter.dialogsNeedReload, new Object[0]);
                        break;
                    }
                    i++;
                } else {
                    break;
                }
            }
        }
        if (DialogObject.isEncryptedDialog(j)) {
            return;
        }
        TLRPC.TL_messages_markDialogUnread tL_messages_markDialogUnread = new TLRPC.TL_messages_markDialogUnread();
        tL_messages_markDialogUnread.unread = true;
        if (inputPeer == null) {
            inputPeer = getInputPeer(j);
        }
        if (inputPeer instanceof TLRPC.TL_inputPeerEmpty) {
            return;
        }
        TLRPC.TL_inputDialogPeer tL_inputDialogPeer = new TLRPC.TL_inputDialogPeer();
        tL_inputDialogPeer.peer = inputPeer;
        tL_messages_markDialogUnread.peer = tL_inputDialogPeer;
        if (j2 == 0) {
            try {
                nativeByteBuffer = new NativeByteBuffer(inputPeer.getObjectSize() + 12);
            } catch (Exception e) {
                e = e;
            }
            try {
                nativeByteBuffer.writeInt32(9);
                nativeByteBuffer.writeInt64(j);
                inputPeer.serializeToStream(nativeByteBuffer);
            } catch (Exception e2) {
                e = e2;
                nativeByteBuffer2 = nativeByteBuffer;
                FileLog.e(e);
                nativeByteBuffer = nativeByteBuffer2;
                j2 = getMessagesStorage().createPendingTask(nativeByteBuffer);
                getConnectionsManager().sendRequest(tL_messages_markDialogUnread, new RequestDelegate() {
                    @Override
                    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                        MessagesController.this.lambda$markDialogAsUnread$337(j2, tLObject, tL_error);
                    }
                });
            }
            j2 = getMessagesStorage().createPendingTask(nativeByteBuffer);
        }
        getConnectionsManager().sendRequest(tL_messages_markDialogUnread, new RequestDelegate() {
            @Override
            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                MessagesController.this.lambda$markDialogAsUnread$337(j2, tLObject, tL_error);
            }
        });
    }

    public void markDialogMessageAsDeleted(long j, ArrayList<Integer> arrayList) {
        ArrayList arrayList2 = (ArrayList) this.dialogMessage.get(j);
        if (arrayList2 != null) {
            for (int i = 0; i < arrayList2.size(); i++) {
                MessageObject messageObject = (MessageObject) arrayList2.get(i);
                if (messageObject != null) {
                    int i2 = 0;
                    while (true) {
                        if (i2 < arrayList.size()) {
                            if (messageObject.getId() == arrayList.get(i2).intValue()) {
                                messageObject.deleted = true;
                                break;
                            }
                            i2++;
                        }
                    }
                }
            }
        }
    }

    public void markMentionMessageAsRead(int i, long j, long j2) {
        getMessagesStorage().markMentionMessageAsRead(-j, i, j2);
        if (j == 0) {
            TLRPC.TL_messages_readMessageContents tL_messages_readMessageContents = new TLRPC.TL_messages_readMessageContents();
            tL_messages_readMessageContents.id.add(Integer.valueOf(i));
            getConnectionsManager().sendRequest(tL_messages_readMessageContents, new RequestDelegate() {
                @Override
                public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                    MessagesController.this.lambda$markMentionMessageAsRead$222(tLObject, tL_error);
                }
            });
            return;
        }
        TLRPC.TL_channels_readMessageContents tL_channels_readMessageContents = new TLRPC.TL_channels_readMessageContents();
        TLRPC.InputChannel inputChannel = getInputChannel(j);
        tL_channels_readMessageContents.channel = inputChannel;
        if (inputChannel == null) {
            return;
        }
        tL_channels_readMessageContents.id.add(Integer.valueOf(i));
        getConnectionsManager().sendRequest(tL_channels_readMessageContents, new RequestDelegate() {
            @Override
            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                MessagesController.lambda$markMentionMessageAsRead$221(tLObject, tL_error);
            }
        });
    }

    public void markMentionsAsRead(long j, long j2) {
        if (DialogObject.isEncryptedDialog(j) || j == getUserConfig().getClientUserId()) {
            return;
        }
        getMessagesStorage().resetMentionsCount(j, j2, 0);
        TLRPC.TL_messages_readMentions tL_messages_readMentions = new TLRPC.TL_messages_readMentions();
        tL_messages_readMentions.peer = getInputPeer(j);
        if (j2 != 0) {
            tL_messages_readMentions.top_msg_id = (int) j2;
            tL_messages_readMentions.flags |= 1;
        }
        getConnectionsManager().sendRequest(tL_messages_readMentions, new RequestDelegate() {
            @Override
            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                MessagesController.lambda$markMentionsAsRead$229(tLObject, tL_error);
            }
        });
    }

    public void markMessageAsRead(long j, long j2, int i) {
        TLRPC.EncryptedChat encryptedChat;
        if (j2 == 0 || j == 0) {
            return;
        }
        if ((i > 0 || i == Integer.MIN_VALUE) && DialogObject.isEncryptedDialog(j) && (encryptedChat = getEncryptedChat(Integer.valueOf(DialogObject.getEncryptedChatId(j)))) != null) {
            ArrayList<Long> arrayList = new ArrayList<>();
            arrayList.add(Long.valueOf(j2));
            getSecretChatHelper().sendMessagesReadMessage(encryptedChat, arrayList, null);
            if (i > 0) {
                int currentTime = getConnectionsManager().getCurrentTime();
                getMessagesStorage().createTaskForSecretChat(encryptedChat.id, currentTime, currentTime, 0, arrayList);
            }
        }
    }

    public void markMessageAsRead2(long j, int i, TLRPC.InputChannel inputChannel, int i2, long j2) {
        markMessageAsRead2(j, i, inputChannel, i2, j2, true);
    }

    public void markMessageAsRead2(long r15, int r17, org.telegram.tgnet.TLRPC.InputChannel r18, int r19, long r20, boolean r22) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MessagesController.markMessageAsRead2(long, int, org.telegram.tgnet.TLRPC$InputChannel, int, long, boolean):void");
    }

    public void markMessageContentAsRead(MessageObject messageObject) {
        RequestDelegate requestDelegate;
        ConnectionsManager connectionsManager;
        TLRPC.TL_messages_readMessageContents tL_messages_readMessageContents;
        if (messageObject.scheduled) {
            return;
        }
        ArrayList<Integer> arrayList = new ArrayList<>();
        if (messageObject.messageOwner.mentioned) {
            getMessagesStorage().markMentionMessageAsRead(-messageObject.messageOwner.peer_id.channel_id, messageObject.getId(), messageObject.getDialogId());
        }
        arrayList.add(Integer.valueOf(messageObject.getId()));
        long dialogId = messageObject.getDialogId();
        getMessagesStorage().markMessagesContentAsRead(dialogId, arrayList, 0, 0);
        getNotificationCenter().lambda$postNotificationNameOnUIThread$1(NotificationCenter.messagesReadContent, Long.valueOf(dialogId), arrayList);
        if (messageObject.getId() < 0) {
            markMessageAsRead(messageObject.getDialogId(), messageObject.messageOwner.random_id, Integer.MIN_VALUE);
            return;
        }
        if (messageObject.messageOwner.peer_id.channel_id != 0) {
            TLRPC.TL_channels_readMessageContents tL_channels_readMessageContents = new TLRPC.TL_channels_readMessageContents();
            TLRPC.InputChannel inputChannel = getInputChannel(messageObject.messageOwner.peer_id.channel_id);
            tL_channels_readMessageContents.channel = inputChannel;
            if (inputChannel == null) {
                return;
            }
            tL_channels_readMessageContents.id.add(Integer.valueOf(messageObject.getId()));
            ConnectionsManager connectionsManager2 = getConnectionsManager();
            requestDelegate = new RequestDelegate() {
                @Override
                public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                    MessagesController.lambda$markMessageContentAsRead$219(tLObject, tL_error);
                }
            };
            tL_messages_readMessageContents = tL_channels_readMessageContents;
            connectionsManager = connectionsManager2;
        } else {
            TLRPC.TL_messages_readMessageContents tL_messages_readMessageContents2 = new TLRPC.TL_messages_readMessageContents();
            tL_messages_readMessageContents2.id.add(Integer.valueOf(messageObject.getId()));
            ConnectionsManager connectionsManager3 = getConnectionsManager();
            requestDelegate = new RequestDelegate() {
                @Override
                public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                    MessagesController.this.lambda$markMessageContentAsRead$220(tLObject, tL_error);
                }
            };
            tL_messages_readMessageContents = tL_messages_readMessageContents2;
            connectionsManager = connectionsManager3;
        }
        connectionsManager.sendRequest(tL_messages_readMessageContents, requestDelegate);
    }

    public void markReactionsAsRead(long j, long j2) {
        if (j2 == 0) {
            TLRPC.Dialog dialog = (TLRPC.Dialog) this.dialogs_dict.get(j);
            if (dialog != null) {
                dialog.unread_reactions_count = 0;
            }
        } else {
            this.topicsController.markAllReactionsAsRead(-j, j2);
        }
        getMessagesStorage().updateUnreadReactionsCount(j, j2, 0);
        TLRPC.TL_messages_readReactions tL_messages_readReactions = new TLRPC.TL_messages_readReactions();
        tL_messages_readReactions.peer = getInputPeer(j);
        if (j2 != 0) {
            tL_messages_readReactions.top_msg_id = (int) j2;
        }
        getConnectionsManager().sendRequest(tL_messages_readReactions, new RequestDelegate() {
            @Override
            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                MessagesController.lambda$markReactionsAsRead$390(tLObject, tL_error);
            }
        });
        NotificationCenter.getInstance(this.currentAccount).lambda$postNotificationNameOnUIThread$1(NotificationCenter.updateInterfaces, Integer.valueOf(UPDATE_MASK_REACTIONS_READ));
    }

    public void markSponsoredAsRead(long j, MessageObject messageObject) {
    }

    public boolean matchesAdminRights(TLRPC.Chat chat, TLRPC.User user, TLRPC.TL_chatAdminRights tL_chatAdminRights) {
        if (tL_chatAdminRights == null) {
            return true;
        }
        TLRPC.TL_chatAdminRights chatAdminRightsCached = getChatAdminRightsCached(chat, user);
        if ((!tL_chatAdminRights.change_info || (chatAdminRightsCached != null && chatAdminRightsCached.change_info)) && ((!tL_chatAdminRights.post_messages || (chatAdminRightsCached != null && chatAdminRightsCached.post_messages)) && ((!tL_chatAdminRights.edit_messages || (chatAdminRightsCached != null && chatAdminRightsCached.edit_messages)) && ((!tL_chatAdminRights.delete_messages || (chatAdminRightsCached != null && chatAdminRightsCached.delete_messages)) && ((!tL_chatAdminRights.ban_users || (chatAdminRightsCached != null && chatAdminRightsCached.ban_users)) && ((!tL_chatAdminRights.invite_users || (chatAdminRightsCached != null && chatAdminRightsCached.invite_users)) && ((!tL_chatAdminRights.pin_messages || (chatAdminRightsCached != null && chatAdminRightsCached.pin_messages)) && ((!tL_chatAdminRights.add_admins || (chatAdminRightsCached != null && chatAdminRightsCached.add_admins)) && ((!tL_chatAdminRights.anonymous || (chatAdminRightsCached != null && chatAdminRightsCached.anonymous)) && ((!tL_chatAdminRights.manage_call || (chatAdminRightsCached != null && chatAdminRightsCached.manage_call)) && (!tL_chatAdminRights.other || (chatAdminRightsCached != null && chatAdminRightsCached.other)))))))))))) {
            if (!tL_chatAdminRights.manage_topics) {
                return true;
            }
            if (chatAdminRightsCached != null && chatAdminRightsCached.manage_topics) {
                return true;
            }
        }
        return false;
    }

    public void onFilterUpdate(DialogFilter dialogFilter) {
        for (int i = 0; i < 2; i++) {
            if (this.selectedDialogFilter[i] == dialogFilter) {
                sortDialogs(null);
                getNotificationCenter().lambda$postNotificationNameOnUIThread$1(NotificationCenter.dialogsNeedReload, Boolean.TRUE);
                return;
            }
        }
    }

    public void onFolderEmpty(final int i) {
        if (getUserConfig().getDialogLoadOffsets(i)[0] != 2147483647L) {
            loadDialogs(i, 0, 10, false, new Runnable() {
                @Override
                public final void run() {
                    MessagesController.this.lambda$onFolderEmpty$185(i);
                }
            });
        } else if (i != 1) {
            removeFolder(i);
        } else {
            this.hasArchivedChats = false;
            checkArchiveFolder();
        }
    }

    public void openApp(TLRPC.User user, int i) {
        openApp(null, user, null, i, null);
    }

    public void openApp(BaseFragment baseFragment, TLRPC.User user, String str, int i, Browser.Progress progress) {
        openApp(baseFragment, user, str, i, progress, false, false);
    }

    public void openApp(final BaseFragment baseFragment, final TLRPC.User user, final String str, final int i, final Browser.Progress progress, final boolean z, final boolean z2) {
        if (user == null) {
            return;
        }
        final boolean[] zArr = {false};
        if (progress != null) {
            progress.onCancel(new Runnable() {
                @Override
                public final void run() {
                    MessagesController.lambda$openApp$449(zArr);
                }
            });
            progress.init();
        }
        final Runnable runnable = new Runnable() {
            @Override
            public final void run() {
                MessagesController.this.lambda$openApp$450(baseFragment, progress, zArr, user, str, z, z2, r9);
            }
        };
        MediaDataController mediaDataController = getMediaDataController();
        long j = user.id;
        TL_bots.BotInfo botInfoCached = mediaDataController.getBotInfoCached(j, j);
        final TL_bots.BotInfo[] botInfoArr = {botInfoCached};
        if (user.bot_has_main_app || botInfoCached != null) {
            runnable.run();
            return;
        }
        MediaDataController mediaDataController2 = getMediaDataController();
        long j2 = user.id;
        mediaDataController2.loadBotInfo(j2, j2, false, i, new Utilities.Callback() {
            @Override
            public final void run(Object obj) {
                MessagesController.this.lambda$openApp$452(zArr, botInfoArr, user, i, runnable, (TL_bots.BotInfo) obj);
            }
        });
    }

    public void openByUserName(String str, BaseFragment baseFragment, int i) {
        openByUserName(str, baseFragment, i, null);
    }

    public void openByUserName(java.lang.String r16, final org.telegram.ui.ActionBar.BaseFragment r17, final int r18, final org.telegram.messenger.browser.Browser.Progress r19) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MessagesController.openByUserName(java.lang.String, org.telegram.ui.ActionBar.BaseFragment, int, org.telegram.messenger.browser.Browser$Progress):void");
    }

    public void openChatOrProfileWith(org.telegram.tgnet.TLRPC.User r6, org.telegram.tgnet.TLRPC.Chat r7, org.telegram.ui.ActionBar.BaseFragment r8, int r9, boolean r10) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MessagesController.openChatOrProfileWith(org.telegram.tgnet.TLRPC$User, org.telegram.tgnet.TLRPC$Chat, org.telegram.ui.ActionBar.BaseFragment, int, boolean):void");
    }

    public void performLogout(int i) {
        if (i == 1) {
            unregistedPush();
            getConnectionsManager().sendRequest(new TLRPC.TL_auth_logOut(), new RequestDelegate() {
                @Override
                public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                    MessagesController.this.lambda$performLogout$299(tLObject, tL_error);
                }
            });
        } else {
            getConnectionsManager().cleanup(i == 2);
        }
        getUserConfig().clearConfig();
        SharedPrefsHelper.cleanupAccount(this.currentAccount);
        ArrayList<NotificationCenter.NotificationCenterDelegate> observers = getNotificationCenter().getObservers(NotificationCenter.appDidLogout);
        if (observers != null) {
            int size = observers.size();
            for (int i2 = 0; i2 < size; i2++) {
                if (observers.get(i2) instanceof LaunchActivity) {
                    break;
                }
            }
        }
        if (UserConfig.selectedAccount == this.currentAccount) {
            int i3 = 0;
            while (true) {
                if (i3 >= 4) {
                    i3 = -1;
                    break;
                } else if (UserConfig.getInstance(i3).isClientActivated()) {
                    break;
                } else {
                    i3++;
                }
            }
            if (i3 != -1) {
                UserConfig.selectedAccount = i3;
                UserConfig.getInstance(0).saveConfig(false);
                LaunchActivity.clearFragments();
            }
        }
        getNotificationCenter().lambda$postNotificationNameOnUIThread$1(NotificationCenter.appDidLogout, new Object[0]);
        getMessagesStorage().cleanup(false);
        cleanup();
        getContactsController().deleteUnknownAppAccounts();
    }

    public boolean pinDialog(long j, boolean z, TLRPC.InputPeer inputPeer, long j2) {
        NativeByteBuffer nativeByteBuffer;
        final long createPendingTask;
        TLRPC.Dialog dialog = (TLRPC.Dialog) this.dialogs_dict.get(j);
        if (dialog == null || dialog.pinned == z) {
            return dialog != null;
        }
        int i = dialog.folder_id;
        ArrayList<TLRPC.Dialog> dialogs = getDialogs(i);
        dialog.pinned = z;
        if (z) {
            int i2 = 0;
            for (int i3 = 0; i3 < dialogs.size(); i3++) {
                TLRPC.Dialog dialog2 = dialogs.get(i3);
                if (!(dialog2 instanceof TLRPC.TL_dialogFolder)) {
                    if (!dialog2.pinned) {
                        if (dialog2.id != this.promoDialogId) {
                            break;
                        }
                    } else {
                        i2 = Math.max(dialog2.pinnedNum, i2);
                    }
                }
            }
            dialog.pinnedNum = i2 + 1;
        } else {
            dialog.pinnedNum = 0;
        }
        NativeByteBuffer nativeByteBuffer2 = null;
        sortDialogs(null);
        if (!z && !dialogs.isEmpty() && dialogs.get(dialogs.size() - 1) == dialog && !this.dialogsEndReached.get(i)) {
            dialogs.remove(dialogs.size() - 1);
        }
        getNotificationCenter().lambda$postNotificationNameOnUIThread$1(NotificationCenter.dialogsNeedReload, new Object[0]);
        if (!DialogObject.isEncryptedDialog(j) && j2 != -1) {
            TLRPC.TL_messages_toggleDialogPin tL_messages_toggleDialogPin = new TLRPC.TL_messages_toggleDialogPin();
            tL_messages_toggleDialogPin.pinned = z;
            TLRPC.InputPeer inputPeer2 = inputPeer == null ? getInputPeer(j) : inputPeer;
            if (inputPeer2 instanceof TLRPC.TL_inputPeerEmpty) {
                return false;
            }
            TLRPC.TL_inputDialogPeer tL_inputDialogPeer = new TLRPC.TL_inputDialogPeer();
            tL_inputDialogPeer.peer = inputPeer2;
            tL_messages_toggleDialogPin.peer = tL_inputDialogPeer;
            if (j2 == 0) {
                try {
                    nativeByteBuffer = new NativeByteBuffer(inputPeer2.getObjectSize() + 16);
                } catch (Exception e) {
                    e = e;
                }
                try {
                    nativeByteBuffer.writeInt32(4);
                    nativeByteBuffer.writeInt64(j);
                    nativeByteBuffer.writeBool(z);
                    inputPeer2.serializeToStream(nativeByteBuffer);
                } catch (Exception e2) {
                    e = e2;
                    nativeByteBuffer2 = nativeByteBuffer;
                    FileLog.e(e);
                    nativeByteBuffer = nativeByteBuffer2;
                    createPendingTask = getMessagesStorage().createPendingTask(nativeByteBuffer);
                    getConnectionsManager().sendRequest(tL_messages_toggleDialogPin, new RequestDelegate() {
                        @Override
                        public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                            MessagesController.this.lambda$pinDialog$341(createPendingTask, tLObject, tL_error);
                        }
                    });
                    getMessagesStorage().setDialogPinned(j, dialog.pinnedNum);
                    return true;
                }
                createPendingTask = getMessagesStorage().createPendingTask(nativeByteBuffer);
            } else {
                createPendingTask = j2;
            }
            getConnectionsManager().sendRequest(tL_messages_toggleDialogPin, new RequestDelegate() {
                @Override
                public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                    MessagesController.this.lambda$pinDialog$341(createPendingTask, tLObject, tL_error);
                }
            });
        }
        getMessagesStorage().setDialogPinned(j, dialog.pinnedNum);
        return true;
    }

    public void pinMessage(final TLRPC.Chat chat, final TLRPC.User user, final int i, final boolean z, boolean z2, boolean z3) {
        if (chat == null && user == null) {
            return;
        }
        TLRPC.TL_messages_updatePinnedMessage tL_messages_updatePinnedMessage = new TLRPC.TL_messages_updatePinnedMessage();
        tL_messages_updatePinnedMessage.peer = getInputPeer(chat != null ? -chat.id : user.id);
        tL_messages_updatePinnedMessage.id = i;
        tL_messages_updatePinnedMessage.unpin = z;
        tL_messages_updatePinnedMessage.silent = !z3;
        tL_messages_updatePinnedMessage.pm_oneside = z2;
        getConnectionsManager().sendRequest(tL_messages_updatePinnedMessage, new RequestDelegate() {
            @Override
            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                MessagesController.this.lambda$pinMessage$122(i, chat, user, z, tLObject, tL_error);
            }
        });
    }

    public boolean premiumFeaturesBlocked() {
        return this.premiumLocked && !getUserConfig().isPremium();
    }

    public boolean premiumPurchaseBlocked() {
        return this.premiumLocked;
    }

    public void pressTranscribeButton() {
        int i = this.transcribeButtonPressed;
        if (i < 2) {
            this.transcribeButtonPressed = i + 1;
            SharedPreferences sharedPreferences = this.mainPreferences;
            if (sharedPreferences != null) {
                sharedPreferences.edit().putInt("transcribeButtonPressed", this.transcribeButtonPressed).apply();
            }
        }
    }

    public void processChatInfo(final long j, final TLRPC.ChatFull chatFull, final ArrayList<TLRPC.User> arrayList, final boolean z, final boolean z2, final boolean z3, final ArrayList<Integer> arrayList2, final HashMap<Integer, MessageObject> hashMap, final int i, final boolean z4) {
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                MessagesController.this.lambda$processChatInfo$140(z, j, z3, z2, chatFull, arrayList, arrayList2, hashMap, i, z4);
            }
        });
    }

    public boolean processDeletedReactionTags(TLRPC.Message message) {
        TLRPC.TL_messageReactions tL_messageReactions;
        if (message == null || DialogObject.getPeerDialogId(message.peer_id) != getUserConfig().getClientUserId() || (tL_messageReactions = message.reactions) == null || !tL_messageReactions.reactions_as_tags || tL_messageReactions.results == null) {
            return false;
        }
        long savedDialogId = MessageObject.getSavedDialogId(getUserConfig().getClientUserId(), message);
        boolean z = false;
        for (int i = 0; i < message.reactions.results.size(); i++) {
            if (updateSavedReactionTags(savedDialogId, ReactionsLayoutInBubble.VisibleReaction.fromTL(message.reactions.results.get(i).reaction), false, false)) {
                z = true;
            }
        }
        return z;
    }

    public void processDialogsUpdate(final TLRPC.messages_Dialogs messages_dialogs, ArrayList<TLRPC.EncryptedChat> arrayList, final boolean z) {
        Utilities.stageQueue.postRunnable(new Runnable() {
            @Override
            public final void run() {
                MessagesController.this.lambda$processDialogsUpdate$217(messages_dialogs, z);
            }
        });
    }

    public void processDialogsUpdateRead(final LongSparseIntArray longSparseIntArray, final LongSparseIntArray longSparseIntArray2) {
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                MessagesController.this.lambda$processDialogsUpdateRead$211(longSparseIntArray, longSparseIntArray2);
            }
        });
    }

    public void processLoadedAdminsResponse(long j, TLRPC.TL_channels_channelParticipants tL_channels_channelParticipants) {
        LongSparseArray longSparseArray = new LongSparseArray(tL_channels_channelParticipants.participants.size());
        for (int i = 0; i < tL_channels_channelParticipants.participants.size(); i++) {
            TLRPC.ChannelParticipant channelParticipant = tL_channels_channelParticipants.participants.get(i);
            longSparseArray.put(MessageObject.getPeerId(channelParticipant.peer), channelParticipant);
        }
        processLoadedChannelAdmins(longSparseArray, j, false);
    }

    public void processLoadedChannelAdmins(final LongSparseArray longSparseArray, final long j, final boolean z) {
        if (!z) {
            getMessagesStorage().putChannelAdmins(j, longSparseArray);
        }
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                MessagesController.this.lambda$processLoadedChannelAdmins$60(j, longSparseArray, z);
            }
        });
    }

    public void processLoadedDeleteTask(final int i, final LongSparseArray longSparseArray, final LongSparseArray longSparseArray2) {
        Utilities.stageQueue.postRunnable(new Runnable() {
            @Override
            public final void run() {
                MessagesController.this.lambda$processLoadedDeleteTask$82(longSparseArray, longSparseArray2, i);
            }
        });
    }

    public void processLoadedDialogFilters(final ArrayList<DialogFilter> arrayList, final TLRPC.messages_Dialogs messages_dialogs, final TLRPC.messages_Dialogs messages_dialogs2, final ArrayList<TLRPC.User> arrayList2, final ArrayList<TLRPC.Chat> arrayList3, final ArrayList<TLRPC.EncryptedChat> arrayList4, final int i, final Runnable runnable) {
        Utilities.stageQueue.postRunnable(new Runnable() {
            @Override
            public final void run() {
                MessagesController.this.lambda$processLoadedDialogFilters$20(messages_dialogs, arrayList4, messages_dialogs2, i, arrayList, arrayList2, arrayList3, runnable);
            }
        });
    }

    public void processLoadedDialogs(final TLRPC.messages_Dialogs messages_dialogs, final ArrayList<TLRPC.EncryptedChat> arrayList, final ArrayList<TLRPC.UserFull> arrayList2, final int i, final int i2, final int i3, final int i4, final boolean z, final boolean z2, final boolean z3) {
        Utilities.stageQueue.postRunnable(new Runnable() {
            @Override
            public final void run() {
                MessagesController.this.lambda$processLoadedDialogs$208(i, i4, messages_dialogs, arrayList2, z, i3, arrayList, i2, z3, z2);
            }
        });
    }

    public void processLoadedMessages(final org.telegram.tgnet.TLRPC.messages_Messages r36, final int r37, final long r38, final long r40, final int r42, final int r43, final int r44, final boolean r45, final int r46, final int r47, final int r48, final int r49, final int r50, final int r51, final boolean r52, final int r53, final long r54, final int r56, final boolean r57, final int r58, final boolean r59, final boolean r60, final org.telegram.messenger.Timer r61) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MessagesController.processLoadedMessages(org.telegram.tgnet.TLRPC$messages_Messages, int, long, long, int, int, int, boolean, int, int, int, int, int, int, boolean, int, long, int, boolean, int, boolean, boolean, org.telegram.messenger.Timer):void");
    }

    public void processNewChannelDifferenceParams(int i, int i2, long j) {
        if (BuildVars.LOGS_ENABLED) {
            FileLog.d("processNewChannelDifferenceParams pts = " + i + " pts_count = " + i2 + " channeldId = " + j);
        }
        int i3 = this.channelsPts.get(j);
        if (i3 == 0) {
            i3 = getMessagesStorage().getChannelPtsSync(j);
            if (i3 == 0) {
                i3 = 1;
            }
            this.channelsPts.put(j, i3);
        }
        if (i3 + i2 == i) {
            if (BuildVars.LOGS_ENABLED) {
                FileLog.d("APPLY CHANNEL PTS");
            }
            this.channelsPts.put(j, i);
            getMessagesStorage().saveChannelPts(j, i);
            return;
        }
        if (i3 != i) {
            long j2 = this.updatesStartWaitTimeChannels.get(j);
            if (!((Boolean) this.gettingDifferenceChannels.get(j, Boolean.FALSE)).booleanValue() && j2 != 0 && Math.abs(System.currentTimeMillis() - j2) > 1500) {
                getChannelDifference(j);
                return;
            }
            if (BuildVars.LOGS_ENABLED) {
                FileLog.d("ADD CHANNEL UPDATE TO QUEUE pts = " + i + " pts_count = " + i2);
            }
            if (j2 == 0) {
                this.updatesStartWaitTimeChannels.put(j, System.currentTimeMillis());
            }
            UserActionUpdatesPts userActionUpdatesPts = new UserActionUpdatesPts(null);
            userActionUpdatesPts.pts = i;
            userActionUpdatesPts.pts_count = i2;
            userActionUpdatesPts.chat_id = j;
            ArrayList arrayList = (ArrayList) this.updatesQueueChannels.get(j);
            if (arrayList == null) {
                arrayList = new ArrayList();
                this.updatesQueueChannels.put(j, arrayList);
            }
            arrayList.add(userActionUpdatesPts);
        }
    }

    public void processNewDifferenceParams(int i, int i2, int i3, int i4) {
        if (BuildVars.LOGS_ENABLED) {
            FileLog.d("processNewDifferenceParams seq = " + i + " pts = " + i2 + " date = " + i3 + " pts_count = " + i4);
        }
        if (i2 != -1) {
            if (getMessagesStorage().getLastPtsValue() + i4 == i2) {
                if (BuildVars.LOGS_ENABLED) {
                    FileLog.d("APPLY PTS");
                }
                getMessagesStorage().setLastPtsValue(i2);
                getMessagesStorage().saveDiffParams(getMessagesStorage().getLastSeqValue(), getMessagesStorage().getLastPtsValue(), getMessagesStorage().getLastDateValue(), getMessagesStorage().getLastQtsValue());
            } else if (getMessagesStorage().getLastPtsValue() != i2) {
                if (this.gettingDifference || this.updatesStartWaitTimePts == 0 || Math.abs(System.currentTimeMillis() - this.updatesStartWaitTimePts) <= 1500) {
                    if (BuildVars.LOGS_ENABLED) {
                        FileLog.d("ADD UPDATE TO QUEUE pts = " + i2 + " pts_count = " + i4);
                    }
                    if (this.updatesStartWaitTimePts == 0) {
                        this.updatesStartWaitTimePts = System.currentTimeMillis();
                    }
                    UserActionUpdatesPts userActionUpdatesPts = new UserActionUpdatesPts(null);
                    userActionUpdatesPts.pts = i2;
                    userActionUpdatesPts.pts_count = i4;
                    this.updatesQueuePts.add(userActionUpdatesPts);
                } else {
                    getDifference();
                }
            }
        }
        if (i != -1) {
            if (getMessagesStorage().getLastSeqValue() + 1 == i) {
                if (BuildVars.LOGS_ENABLED) {
                    FileLog.d("APPLY SEQ");
                }
                getMessagesStorage().setLastSeqValue(i);
                if (i3 != -1) {
                    getMessagesStorage().setLastDateValue(i3);
                }
                getMessagesStorage().saveDiffParams(getMessagesStorage().getLastSeqValue(), getMessagesStorage().getLastPtsValue(), getMessagesStorage().getLastDateValue(), getMessagesStorage().getLastQtsValue());
                return;
            }
            if (getMessagesStorage().getLastSeqValue() != i) {
                if (!this.gettingDifference && this.updatesStartWaitTimeSeq != 0 && Math.abs(System.currentTimeMillis() - this.updatesStartWaitTimeSeq) > 1500) {
                    getDifference();
                    return;
                }
                if (BuildVars.LOGS_ENABLED) {
                    FileLog.d("ADD UPDATE TO QUEUE seq = " + i);
                }
                if (this.updatesStartWaitTimeSeq == 0) {
                    this.updatesStartWaitTimeSeq = System.currentTimeMillis();
                }
                UserActionUpdatesSeq userActionUpdatesSeq = new UserActionUpdatesSeq(null);
                userActionUpdatesSeq.seq = i;
                this.updatesQueueSeq.add(userActionUpdatesSeq);
            }
        }
    }

    public boolean processUpdateArray(java.util.ArrayList<org.telegram.tgnet.TLRPC.Update> r76, java.util.ArrayList<org.telegram.tgnet.TLRPC.User> r77, java.util.ArrayList<org.telegram.tgnet.TLRPC.Chat> r78, boolean r79, int r80) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MessagesController.processUpdateArray(java.util.ArrayList, java.util.ArrayList, java.util.ArrayList, boolean, int):boolean");
    }

    public void processUpdates(final org.telegram.tgnet.TLRPC.Updates r30, boolean r31) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MessagesController.processUpdates(org.telegram.tgnet.TLRPC$Updates, boolean):void");
    }

    public void processUserInfo(final TLRPC.User user, final TLRPC.UserFull userFull, final boolean z, final boolean z2, final int i, final ArrayList<Integer> arrayList, final HashMap<Integer, MessageObject> hashMap, final int i2, final boolean z3) {
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                MessagesController.this.lambda$processUserInfo$141(z, user, i, z2, userFull, arrayList, hashMap, i2, z3);
            }
        });
    }

    public void putAllNeededDraftDialogs() {
        LongSparseArray drafts = getMediaDataController().getDrafts();
        int size = drafts.size();
        for (int i = 0; i < size; i++) {
            TLRPC.DraftMessage draftMessage = (TLRPC.DraftMessage) ((LongSparseArray) drafts.valueAt(i)).get(0L);
            if (draftMessage != null) {
                putDraftDialogIfNeed(drafts.keyAt(i), draftMessage);
            }
        }
    }

    public void putChat(final org.telegram.tgnet.TLRPC.Chat r9, boolean r10) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MessagesController.putChat(org.telegram.tgnet.TLRPC$Chat, boolean):void");
    }

    public void putChatFull(TLRPC.ChatFull chatFull) {
        this.fullChats.put(chatFull.id, chatFull);
        getTranslateController().updateDialogFull(-chatFull.id);
    }

    public void putChats(ArrayList<TLRPC.Chat> arrayList, boolean z) {
        if (arrayList == null || arrayList.isEmpty()) {
            return;
        }
        int size = arrayList.size();
        for (int i = 0; i < size; i++) {
            putChat(arrayList.get(i), z);
        }
    }

    public void putDialogsEndReachedAfterRegistration() {
        this.dialogsEndReached.put(0, true);
        this.serverDialogsEndReached.put(0, true);
    }

    public void putDraftDialogIfNeed(long j, TLRPC.DraftMessage draftMessage) {
        if (this.dialogs_dict.indexOfKey(j) < 0) {
            MediaDataController mediaDataController = getMediaDataController();
            int size = this.allDialogs.size();
            if (size > 0) {
                TLRPC.Dialog dialog = this.allDialogs.get(size - 1);
                if (draftMessage.date < DialogObject.getLastMessageOrDraftDate(dialog, mediaDataController.getDraft(dialog.id, 0L))) {
                    return;
                }
            }
            TLRPC.TL_dialog tL_dialog = new TLRPC.TL_dialog();
            tL_dialog.id = j;
            tL_dialog.draft = draftMessage;
            tL_dialog.folder_id = mediaDataController.getDraftFolderId(j);
            tL_dialog.flags = (j >= 0 || !ChatObject.isChannel(getChat(Long.valueOf(-j)))) ? 0 : 1;
            this.dialogs_dict.put(j, tL_dialog);
            this.allDialogs.add(tL_dialog);
            sortDialogs(null);
        }
    }

    public void putEncryptedChat(TLRPC.EncryptedChat encryptedChat, boolean z) {
        if (encryptedChat == null) {
            return;
        }
        if (z) {
            this.encryptedChats.putIfAbsent(Integer.valueOf(encryptedChat.id), encryptedChat);
        } else {
            this.encryptedChats.put(Integer.valueOf(encryptedChat.id), encryptedChat);
        }
    }

    public void putEncryptedChats(ArrayList<TLRPC.EncryptedChat> arrayList, boolean z) {
        if (arrayList == null || arrayList.isEmpty()) {
            return;
        }
        int size = arrayList.size();
        for (int i = 0; i < size; i++) {
            putEncryptedChat(arrayList.get(i), z);
        }
    }

    public void putGroupCall(long j, ChatObject.Call call) {
        this.groupCalls.put(call.call.id, call);
        this.groupCallsByChatId.put(j, call);
        TLRPC.ChatFull chatFull = getChatFull(j);
        if (chatFull != null) {
            chatFull.call = call.getInputGroupCall();
        }
        getNotificationCenter().lambda$postNotificationNameOnUIThread$1(NotificationCenter.groupCallUpdated, Long.valueOf(j), Long.valueOf(call.call.id), Boolean.FALSE);
        loadFullChat(j, 0, true);
    }

    public boolean putUser(TLRPC.User user, boolean z) {
        return putUser(user, z, false);
    }

    public boolean putUser(org.telegram.tgnet.TLRPC.User r9, boolean r10, boolean r11) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MessagesController.putUser(org.telegram.tgnet.TLRPC$User, boolean, boolean):boolean");
    }

    public void putUsers(ArrayList<TLRPC.User> arrayList, boolean z) {
        if (arrayList == null || arrayList.isEmpty()) {
            return;
        }
        int size = arrayList.size();
        boolean z2 = false;
        for (int i = 0; i < size; i++) {
            if (putUser(arrayList.get(i), z)) {
                z2 = true;
            }
        }
        if (z2) {
            AndroidUtilities.runOnUIThread(new Runnable() {
                @Override
                public final void run() {
                    MessagesController.this.lambda$putUsers$52();
                }
            });
        }
    }

    public void registerForPush(final int i, final String str) {
        if (TextUtils.isEmpty(str) || this.registeringForPush || getUserConfig().getClientUserId() == 0) {
            return;
        }
        if (getUserConfig().registeredForPush && str.equals(SharedConfig.pushString)) {
            return;
        }
        this.registeringForPush = true;
        this.lastPushRegisterSendTime = SystemClock.elapsedRealtime();
        if (SharedConfig.pushAuthKey == null) {
            SharedConfig.pushAuthKey = new byte[256];
            Utilities.random.nextBytes(SharedConfig.pushAuthKey);
            SharedConfig.saveConfig();
        }
        TL_account.registerDevice registerdevice = new TL_account.registerDevice();
        registerdevice.token_type = i;
        registerdevice.token = str;
        registerdevice.no_muted = false;
        registerdevice.secret = SharedConfig.pushAuthKey;
        for (int i2 = 0; i2 < 4; i2++) {
            UserConfig userConfig = UserConfig.getInstance(i2);
            if (i2 != this.currentAccount && userConfig.isClientActivated()) {
                long clientUserId = userConfig.getClientUserId();
                registerdevice.other_uids.add(Long.valueOf(clientUserId));
                if (BuildVars.LOGS_ENABLED) {
                    FileLog.d("add other uid = " + clientUserId + " for account " + this.currentAccount);
                }
            }
        }
        getConnectionsManager().sendRequest(registerdevice, new RequestDelegate() {
            @Override
            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                MessagesController.this.lambda$registerForPush$301(i, str, tLObject, tL_error);
            }
        });
    }

    public void reloadMentionsCountForChannel(final TLRPC.InputPeer inputPeer, final long j) {
        Exception e;
        NativeByteBuffer nativeByteBuffer;
        if (j == 0) {
            try {
                nativeByteBuffer = new NativeByteBuffer(inputPeer.getObjectSize() + 4);
            } catch (Exception e2) {
                e = e2;
                nativeByteBuffer = null;
            }
            try {
                nativeByteBuffer.writeInt32(22);
                inputPeer.serializeToStream(nativeByteBuffer);
            } catch (Exception e3) {
                e = e3;
                FileLog.e(e);
                j = getMessagesStorage().createPendingTask(nativeByteBuffer);
                TLRPC.TL_messages_getUnreadMentions tL_messages_getUnreadMentions = new TLRPC.TL_messages_getUnreadMentions();
                tL_messages_getUnreadMentions.peer = inputPeer;
                tL_messages_getUnreadMentions.limit = 1;
                getConnectionsManager().sendRequest(tL_messages_getUnreadMentions, new RequestDelegate() {
                    @Override
                    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                        MessagesController.this.lambda$reloadMentionsCountForChannel$209(inputPeer, j, tLObject, tL_error);
                    }
                });
            }
            j = getMessagesStorage().createPendingTask(nativeByteBuffer);
        }
        TLRPC.TL_messages_getUnreadMentions tL_messages_getUnreadMentions2 = new TLRPC.TL_messages_getUnreadMentions();
        tL_messages_getUnreadMentions2.peer = inputPeer;
        tL_messages_getUnreadMentions2.limit = 1;
        getConnectionsManager().sendRequest(tL_messages_getUnreadMentions2, new RequestDelegate() {
            @Override
            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                MessagesController.this.lambda$reloadMentionsCountForChannel$209(inputPeer, j, tLObject, tL_error);
            }
        });
    }

    public void reloadMentionsCountForChannels(final ArrayList<Long> arrayList) {
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                MessagesController.this.lambda$reloadMentionsCountForChannels$210(arrayList);
            }
        });
    }

    public void reloadReactionsNotifySettings() {
        getConnectionsManager().sendRequest(new TL_account.getReactionsNotifySettings(), new RequestDelegate() {
            @Override
            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                MessagesController.this.lambda$reloadReactionsNotifySettings$193(tLObject, tL_error);
            }
        });
    }

    public void reloadUser(long j) {
        TLRPC.TL_users_getUsers tL_users_getUsers = new TLRPC.TL_users_getUsers();
        TLRPC.InputUser inputUser = getInputUser(j);
        if (inputUser == null) {
            return;
        }
        tL_users_getUsers.id.add(inputUser);
        ConnectionsManager.getInstance(this.currentAccount).sendRequest(tL_users_getUsers, new RequestDelegate() {
            @Override
            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                MessagesController.this.lambda$reloadUser$51(tLObject, tL_error);
            }
        });
    }

    public void reloadWebPages(final long j, HashMap<String, ArrayList<MessageObject>> hashMap, final int i) {
        HashMap<String, ArrayList<MessageObject>> hashMap2;
        LongSparseArray longSparseArray;
        boolean z = i == 1;
        boolean z2 = i == 3;
        if (z) {
            hashMap2 = this.reloadingScheduledWebpages;
            longSparseArray = this.reloadingScheduledWebpagesPending;
        } else if (z2) {
            hashMap2 = this.reloadingSavedWebpages;
            longSparseArray = this.reloadingSavedWebpagesPending;
        } else {
            hashMap2 = this.reloadingWebpages;
            longSparseArray = this.reloadingWebpagesPending;
        }
        final HashMap<String, ArrayList<MessageObject>> hashMap3 = hashMap2;
        final LongSparseArray longSparseArray2 = longSparseArray;
        for (Map.Entry<String, ArrayList<MessageObject>> entry : hashMap.entrySet()) {
            final String key = entry.getKey();
            ArrayList<MessageObject> value = entry.getValue();
            ArrayList<MessageObject> arrayList = hashMap3.get(key);
            if (arrayList == null) {
                arrayList = new ArrayList<>();
                hashMap3.put(key, arrayList);
            }
            arrayList.addAll(value);
            TL_account.getWebPagePreview getwebpagepreview = new TL_account.getWebPagePreview();
            getwebpagepreview.message = key;
            getConnectionsManager().sendRequest(getwebpagepreview, new RequestDelegate() {
                @Override
                public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                    MessagesController.this.lambda$reloadWebPages$176(hashMap3, key, longSparseArray2, j, i, tLObject, tL_error);
                }
            });
        }
    }

    public void removeDeletedMessagesFromArray(long j, ArrayList<TLRPC.Message> arrayList) {
        int i = 0;
        int i2 = this.deletedHistory.get(j, 0);
        if (i2 == 0) {
            return;
        }
        int size = arrayList.size();
        while (i < size) {
            if (arrayList.get(i).id <= i2) {
                arrayList.remove(i);
                i--;
                size--;
            }
            i++;
        }
    }

    public void removeDialogAction(long j, boolean z, boolean z2) {
        TLRPC.Dialog dialog = (TLRPC.Dialog) this.dialogs_dict.get(j);
        if (dialog == null) {
            return;
        }
        if (z) {
            this.clearingHistoryDialogs.remove(j);
        } else {
            this.deletingDialogs.remove(j);
            if (!z2) {
                this.allDialogs.add(dialog);
                sortDialogs(null);
            }
        }
        if (z2) {
            return;
        }
        getNotificationCenter().lambda$postNotificationNameOnUIThread$1(NotificationCenter.dialogsNeedReload, Boolean.TRUE);
    }

    public void removeDraftDialogIfNeed(long j) {
        TLRPC.Dialog dialog = (TLRPC.Dialog) this.dialogs_dict.get(j);
        if (dialog == null || dialog.top_message != 0) {
            return;
        }
        this.dialogs_dict.remove(dialog.id);
        this.allDialogs.remove(dialog);
    }

    public void removeFilter(DialogFilter dialogFilter) {
        this.dialogFilters.remove(dialogFilter);
        this.dialogFiltersById.remove(dialogFilter.id);
        getNotificationCenter().lambda$postNotificationNameOnUIThread$1(NotificationCenter.dialogFiltersUpdated, new Object[0]);
    }

    public Pair<Runnable, Runnable> removeFolderTemporarily(final int i, ArrayList<Long> arrayList) {
        this.frozenDialogFilters = new ArrayList<>(this.dialogFilters);
        int i2 = 0;
        while (i2 < this.frozenDialogFilters.size()) {
            if (this.frozenDialogFilters.get(i2).id == i) {
                this.frozenDialogFilters.remove(i2);
                i2--;
            }
            i2++;
        }
        this.hiddenUndoChats.clear();
        if (arrayList != null) {
            this.hiddenUndoChats.addAll(arrayList);
        }
        final boolean z = !this.hiddenUndoChats.isEmpty();
        getNotificationCenter().lambda$postNotificationNameOnUIThread$1(NotificationCenter.dialogFiltersUpdated, new Object[0]);
        if (z) {
            getNotificationCenter().lambda$postNotificationNameOnUIThread$1(NotificationCenter.dialogsNeedReload, new Object[0]);
        }
        return new Pair<>(new Runnable() {
            @Override
            public final void run() {
                MessagesController.this.lambda$removeFolderTemporarily$431(i, z);
            }
        }, new Runnable() {
            @Override
            public final void run() {
                MessagesController.this.lambda$removeFolderTemporarily$432(z);
            }
        });
    }

    public void removeSuggestion(long j, String str) {
        if (TextUtils.isEmpty(str)) {
            return;
        }
        if (j == 0) {
            if (!this.pendingSuggestions.remove(str) && this.dismissedSuggestions.contains(str)) {
                return;
            }
            this.dismissedSuggestions.add(str);
            SharedPreferences.Editor edit = this.mainPreferences.edit();
            edit.putStringSet("pendingSuggestions", this.pendingSuggestions);
            edit.putStringSet("dismissedSuggestions", this.dismissedSuggestions);
            edit.commit();
            getNotificationCenter().lambda$postNotificationNameOnUIThread$1(NotificationCenter.newSuggestionsAvailable, new Object[0]);
        }
        TLRPC.TL_help_dismissSuggestion tL_help_dismissSuggestion = new TLRPC.TL_help_dismissSuggestion();
        tL_help_dismissSuggestion.suggestion = str;
        tL_help_dismissSuggestion.peer = j == 0 ? new TLRPC.TL_inputPeerEmpty() : getInputPeer(j);
        getConnectionsManager().sendRequest(tL_help_dismissSuggestion, new RequestDelegate() {
            @Override
            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                MessagesController.lambda$removeSuggestion$35(tLObject, tL_error);
            }
        });
    }

    public void renameSavedReactionTag(ReactionsLayoutInBubble.VisibleReaction visibleReaction, String str) {
        boolean z;
        boolean z2;
        long j;
        String str2;
        long m;
        LongSparseArray longSparseArray = this.reactionTags;
        if (longSparseArray == null) {
            return;
        }
        TLRPC.TL_messages_savedReactionsTags tL_messages_savedReactionsTags = (TLRPC.TL_messages_savedReactionsTags) longSparseArray.get(0L);
        if (tL_messages_savedReactionsTags == null) {
            LongSparseArray longSparseArray2 = this.reactionTags;
            TLRPC.TL_messages_savedReactionsTags tL_messages_savedReactionsTags2 = new TLRPC.TL_messages_savedReactionsTags();
            longSparseArray2.put(0L, tL_messages_savedReactionsTags2);
            tL_messages_savedReactionsTags = tL_messages_savedReactionsTags2;
        }
        int i = 0;
        while (true) {
            if (i >= tL_messages_savedReactionsTags.tags.size()) {
                z = false;
                z2 = false;
                break;
            }
            TLRPC.TL_savedReactionTag tL_savedReactionTag = tL_messages_savedReactionsTags.tags.get(i);
            if (visibleReaction.isSame(tL_savedReactionTag.reaction)) {
                if (TextUtils.isEmpty(str)) {
                    z = tL_savedReactionTag.title != null;
                    tL_savedReactionTag.flags &= -2;
                    tL_savedReactionTag.title = null;
                } else {
                    z = !TextUtils.equals(tL_savedReactionTag.title, str);
                    tL_savedReactionTag.flags |= 1;
                    tL_savedReactionTag.title = str;
                }
                z2 = true;
            } else {
                i++;
            }
        }
        if (!z2) {
            TLRPC.TL_savedReactionTag tL_savedReactionTag2 = new TLRPC.TL_savedReactionTag();
            tL_savedReactionTag2.reaction = visibleReaction.toTLReaction();
            if (!TextUtils.isEmpty(str)) {
                tL_savedReactionTag2.title = str;
            }
            tL_savedReactionTag2.count = 1;
            tL_messages_savedReactionsTags.tags.add(tL_savedReactionTag2);
            z = true;
        }
        if (z) {
            TLRPC.TL_messages_updateSavedReactionTag tL_messages_updateSavedReactionTag = new TLRPC.TL_messages_updateSavedReactionTag();
            tL_messages_updateSavedReactionTag.reaction = visibleReaction.toTLReaction();
            if (!TextUtils.isEmpty(str)) {
                tL_messages_updateSavedReactionTag.flags |= 1;
                tL_messages_updateSavedReactionTag.title = str;
            }
            getConnectionsManager().sendRequest(tL_messages_updateSavedReactionTag, null);
            Collections.sort(tL_messages_savedReactionsTags.tags, new Comparator() {
                @Override
                public final int compare(Object obj, Object obj2) {
                    int lambda$renameSavedReactionTag$436;
                    lambda$renameSavedReactionTag$436 = MessagesController.this.lambda$renameSavedReactionTag$436((TLRPC.TL_savedReactionTag) obj, (TLRPC.TL_savedReactionTag) obj2);
                    return lambda$renameSavedReactionTag$436;
                }
            });
            long j2 = 0;
            for (int i2 = 0; i2 < tL_messages_savedReactionsTags.tags.size(); i2++) {
                TLRPC.TL_savedReactionTag tL_savedReactionTag3 = tL_messages_savedReactionsTags.tags.get(i2);
                if (tL_savedReactionTag3.count > 0) {
                    TLRPC.Reaction reaction = tL_savedReactionTag3.reaction;
                    if (reaction instanceof TLRPC.TL_reactionEmoji) {
                        j = MessagesController$$ExternalSyntheticBackport9.m(r7, 0, Utilities.MD5(((TLRPC.TL_reactionEmoji) reaction).emoticon).substring(0, 16).length(), 16);
                    } else {
                        if (reaction instanceof TLRPC.TL_reactionCustomEmoji) {
                            j = ((TLRPC.TL_reactionCustomEmoji) reaction).document_id;
                        }
                        if ((tL_savedReactionTag3.flags & 1) != 0 && (str2 = tL_savedReactionTag3.title) != null) {
                            m = MessagesController$$ExternalSyntheticBackport11.m(r7, 0, Utilities.MD5(str2).substring(0, 16).length(), 16);
                            j2 = MediaDataController.calcHash(j2, m);
                        }
                        j2 = MediaDataController.calcHash(j2, tL_savedReactionTag3.count);
                    }
                    j2 = MediaDataController.calcHash(j2, j);
                    if ((tL_savedReactionTag3.flags & 1) != 0) {
                        m = MessagesController$$ExternalSyntheticBackport11.m(r7, 0, Utilities.MD5(str2).substring(0, 16).length(), 16);
                        j2 = MediaDataController.calcHash(j2, m);
                    }
                    j2 = MediaDataController.calcHash(j2, tL_savedReactionTag3.count);
                }
            }
            tL_messages_savedReactionsTags.hash = j2;
            saveSavedReactionsTags(0L, tL_messages_savedReactionsTags);
            getNotificationCenter().lambda$postNotificationNameOnUIThread$1(NotificationCenter.savedReactionTagsUpdate, 0L);
        }
    }

    public void reorderPinnedDialogs(int i, ArrayList<TLRPC.InputDialogPeer> arrayList, final long j) {
        NativeByteBuffer nativeByteBuffer;
        TLRPC.TL_messages_reorderPinnedDialogs tL_messages_reorderPinnedDialogs = new TLRPC.TL_messages_reorderPinnedDialogs();
        tL_messages_reorderPinnedDialogs.folder_id = i;
        tL_messages_reorderPinnedDialogs.force = true;
        if (j == 0) {
            ArrayList<TLRPC.Dialog> dialogs = getDialogs(i);
            if (dialogs.isEmpty()) {
                return;
            }
            ArrayList<Long> arrayList2 = new ArrayList<>();
            ArrayList<Integer> arrayList3 = new ArrayList<>();
            int size = dialogs.size();
            int i2 = 0;
            for (int i3 = 0; i3 < size; i3++) {
                TLRPC.Dialog dialog = dialogs.get(i3);
                if (!(dialog instanceof TLRPC.TL_dialogFolder)) {
                    if (!dialog.pinned) {
                        if (dialog.id != this.promoDialogId) {
                            break;
                        }
                    } else {
                        arrayList2.add(Long.valueOf(dialog.id));
                        arrayList3.add(Integer.valueOf(dialog.pinnedNum));
                        if (!DialogObject.isEncryptedDialog(dialog.id)) {
                            TLRPC.InputPeer inputPeer = getInputPeer(dialog.id);
                            TLRPC.TL_inputDialogPeer tL_inputDialogPeer = new TLRPC.TL_inputDialogPeer();
                            tL_inputDialogPeer.peer = inputPeer;
                            tL_messages_reorderPinnedDialogs.order.add(tL_inputDialogPeer);
                            i2 += tL_inputDialogPeer.getObjectSize();
                        }
                    }
                }
            }
            getMessagesStorage().setDialogsPinned(arrayList2, arrayList3);
            try {
                nativeByteBuffer = new NativeByteBuffer(i2 + 12);
                try {
                    nativeByteBuffer.writeInt32(16);
                    nativeByteBuffer.writeInt32(i);
                    nativeByteBuffer.writeInt32(tL_messages_reorderPinnedDialogs.order.size());
                    int size2 = tL_messages_reorderPinnedDialogs.order.size();
                    for (int i4 = 0; i4 < size2; i4++) {
                        tL_messages_reorderPinnedDialogs.order.get(i4).serializeToStream(nativeByteBuffer);
                    }
                } catch (Exception e) {
                    e = e;
                    FileLog.e(e);
                    j = getMessagesStorage().createPendingTask(nativeByteBuffer);
                    getConnectionsManager().sendRequest(tL_messages_reorderPinnedDialogs, new RequestDelegate() {
                        @Override
                        public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                            MessagesController.this.lambda$reorderPinnedDialogs$340(j, tLObject, tL_error);
                        }
                    });
                }
            } catch (Exception e2) {
                e = e2;
                nativeByteBuffer = null;
            }
            j = getMessagesStorage().createPendingTask(nativeByteBuffer);
        } else {
            tL_messages_reorderPinnedDialogs.order = arrayList;
        }
        getConnectionsManager().sendRequest(tL_messages_reorderPinnedDialogs, new RequestDelegate() {
            @Override
            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                MessagesController.this.lambda$reorderPinnedDialogs$340(j, tLObject, tL_error);
            }
        });
    }

    public void reportMessageDelivery(long j, int i, boolean z) {
        StarsController.MessageId from = StarsController.MessageId.from(j, i);
        if (this.reportedMessageDelivery.contains(from)) {
            return;
        }
        this.reportedMessageDelivery.add(from);
        this.pendingReportMessageDelivery.add(new Pair<>(from, new AtomicBoolean(z)));
        AndroidUtilities.cancelRunOnUIThread(this.sendReportMessageDeliver);
        AndroidUtilities.runOnUIThread(this.sendReportMessageDeliver);
    }

    public void reportSpam(long j, TLRPC.User user, TLRPC.Chat chat, TLRPC.EncryptedChat encryptedChat, boolean z) {
        long j2;
        RequestDelegate requestDelegate;
        long j3;
        ConnectionsManager connectionsManager;
        TLRPC.TL_messages_reportSpam tL_messages_reportSpam;
        if (user == null && chat == null && encryptedChat == null) {
            return;
        }
        SharedPreferences.Editor edit = this.notificationsPreferences.edit();
        edit.putInt("dialog_bar_vis3" + j, 3);
        edit.commit();
        if (DialogObject.isEncryptedDialog(j)) {
            if (encryptedChat == null || encryptedChat.access_hash == 0) {
                return;
            }
            TLRPC.TL_messages_reportEncryptedSpam tL_messages_reportEncryptedSpam = new TLRPC.TL_messages_reportEncryptedSpam();
            TLRPC.TL_inputEncryptedChat tL_inputEncryptedChat = new TLRPC.TL_inputEncryptedChat();
            tL_messages_reportEncryptedSpam.peer = tL_inputEncryptedChat;
            tL_inputEncryptedChat.chat_id = encryptedChat.id;
            tL_inputEncryptedChat.access_hash = encryptedChat.access_hash;
            ConnectionsManager connectionsManager2 = getConnectionsManager();
            requestDelegate = new RequestDelegate() {
                @Override
                public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                    MessagesController.lambda$reportSpam$70(tLObject, tL_error);
                }
            };
            tL_messages_reportSpam = tL_messages_reportEncryptedSpam;
            connectionsManager = connectionsManager2;
        } else if (z) {
            TL_account.reportPeer reportpeer = new TL_account.reportPeer();
            if (chat != null) {
                j3 = -chat.id;
            } else {
                if (user != null) {
                    j3 = user.id;
                }
                reportpeer.message = "";
                reportpeer.reason = new TLRPC.TL_inputReportReasonGeoIrrelevant();
                ConnectionsManager connectionsManager3 = getConnectionsManager();
                requestDelegate = new RequestDelegate() {
                    @Override
                    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                        MessagesController.lambda$reportSpam$71(tLObject, tL_error);
                    }
                };
                tL_messages_reportSpam = reportpeer;
                connectionsManager = connectionsManager3;
            }
            reportpeer.peer = getInputPeer(j3);
            reportpeer.message = "";
            reportpeer.reason = new TLRPC.TL_inputReportReasonGeoIrrelevant();
            ConnectionsManager connectionsManager32 = getConnectionsManager();
            requestDelegate = new RequestDelegate() {
                @Override
                public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                    MessagesController.lambda$reportSpam$71(tLObject, tL_error);
                }
            };
            tL_messages_reportSpam = reportpeer;
            connectionsManager = connectionsManager32;
        } else {
            TLRPC.TL_messages_reportSpam tL_messages_reportSpam2 = new TLRPC.TL_messages_reportSpam();
            if (chat != null) {
                j2 = -chat.id;
            } else {
                if (user != null) {
                    j2 = user.id;
                }
                ConnectionsManager connectionsManager4 = getConnectionsManager();
                requestDelegate = new RequestDelegate() {
                    @Override
                    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                        MessagesController.lambda$reportSpam$72(tLObject, tL_error);
                    }
                };
                tL_messages_reportSpam = tL_messages_reportSpam2;
                connectionsManager = connectionsManager4;
            }
            tL_messages_reportSpam2.peer = getInputPeer(j2);
            ConnectionsManager connectionsManager42 = getConnectionsManager();
            requestDelegate = new RequestDelegate() {
                @Override
                public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                    MessagesController.lambda$reportSpam$72(tLObject, tL_error);
                }
            };
            tL_messages_reportSpam = tL_messages_reportSpam2;
            connectionsManager = connectionsManager42;
        }
        connectionsManager.sendRequest(tL_messages_reportSpam, requestDelegate, 2);
    }

    public void requestContactToken(final long j, final Utilities.Callback<TLRPC.TL_exportedContactToken> callback) {
        if (callback == null || this.requestingContactToken) {
            return;
        }
        if (this.cachedContactToken != null && r0.expires > System.currentTimeMillis() / 1000) {
            callback.run(this.cachedContactToken);
            return;
        }
        this.requestingContactToken = true;
        final long currentTimeMillis = System.currentTimeMillis();
        getConnectionsManager().sendRequest(new TLRPC.TL_contacts_exportContactToken(), new RequestDelegate() {
            @Override
            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                MessagesController.this.lambda$requestContactToken$428(callback, j, currentTimeMillis, tLObject, tL_error);
            }
        });
    }

    public void requestContactToken(Utilities.Callback<TLRPC.TL_exportedContactToken> callback) {
        requestContactToken(0L, callback);
    }

    public void saveGif(final Object obj, TLRPC.Document document) {
        if (obj == null || !MessageObject.isGifDocument(document)) {
            return;
        }
        final TLRPC.TL_messages_saveGif tL_messages_saveGif = new TLRPC.TL_messages_saveGif();
        TLRPC.TL_inputDocument tL_inputDocument = new TLRPC.TL_inputDocument();
        tL_messages_saveGif.id = tL_inputDocument;
        tL_inputDocument.id = document.id;
        tL_inputDocument.access_hash = document.access_hash;
        byte[] bArr = document.file_reference;
        tL_inputDocument.file_reference = bArr;
        if (bArr == null) {
            tL_inputDocument.file_reference = new byte[0];
        }
        tL_messages_saveGif.unsave = false;
        getConnectionsManager().sendRequest(tL_messages_saveGif, new RequestDelegate() {
            @Override
            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                MessagesController.this.lambda$saveGif$136(obj, tL_messages_saveGif, tLObject, tL_error);
            }
        });
    }

    public void saveRecentSticker(final Object obj, TLRPC.Document document, boolean z) {
        if (obj == null || document == null) {
            return;
        }
        final TLRPC.TL_messages_saveRecentSticker tL_messages_saveRecentSticker = new TLRPC.TL_messages_saveRecentSticker();
        TLRPC.TL_inputDocument tL_inputDocument = new TLRPC.TL_inputDocument();
        tL_messages_saveRecentSticker.id = tL_inputDocument;
        tL_inputDocument.id = document.id;
        tL_inputDocument.access_hash = document.access_hash;
        byte[] bArr = document.file_reference;
        tL_inputDocument.file_reference = bArr;
        if (bArr == null) {
            tL_inputDocument.file_reference = new byte[0];
        }
        tL_messages_saveRecentSticker.unsave = false;
        tL_messages_saveRecentSticker.attached = z;
        getConnectionsManager().sendRequest(tL_messages_saveRecentSticker, new RequestDelegate() {
            @Override
            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                MessagesController.this.lambda$saveRecentSticker$137(obj, tL_messages_saveRecentSticker, tLObject, tL_error);
            }
        });
    }

    public void saveTheme(Theme.ThemeInfo themeInfo, Theme.ThemeAccent themeAccent, boolean z, boolean z2) {
        TLRPC.TL_theme tL_theme = themeAccent != null ? themeAccent.info : themeInfo.info;
        if (tL_theme != null) {
            TL_account.saveTheme savetheme = new TL_account.saveTheme();
            TLRPC.TL_inputTheme tL_inputTheme = new TLRPC.TL_inputTheme();
            tL_inputTheme.id = tL_theme.id;
            tL_inputTheme.access_hash = tL_theme.access_hash;
            savetheme.theme = tL_inputTheme;
            savetheme.unsave = z2;
            getConnectionsManager().sendRequest(savetheme, new RequestDelegate() {
                @Override
                public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                    MessagesController.lambda$saveTheme$111(tLObject, tL_error);
                }
            });
            getConnectionsManager().resumeNetworkMaybe();
        }
        if (z2) {
            return;
        }
        installTheme(themeInfo, themeAccent, z);
    }

    public void saveThemeToServer(final Theme.ThemeInfo themeInfo, final Theme.ThemeAccent themeAccent) {
        String str;
        File file;
        if (themeInfo == null) {
            return;
        }
        if (themeAccent != 0) {
            str = themeAccent.saveToFile().getAbsolutePath();
            file = themeAccent.getPathToWallpaper();
        } else {
            str = themeInfo.pathToFile;
            file = null;
        }
        final String str2 = str;
        final File file2 = file;
        if (str2 == null || this.uploadingThemes.containsKey(str2)) {
            return;
        }
        this.uploadingThemes.put(str2, themeAccent != 0 ? themeAccent : themeInfo);
        Utilities.globalQueue.postRunnable(new Runnable() {
            @Override
            public final void run() {
                MessagesController.this.lambda$saveThemeToServer$115(str2, file2, themeAccent, themeInfo);
            }
        });
    }

    public void saveWallpaperToServer(java.io.File r8, org.telegram.ui.ActionBar.Theme.OverrideWallpaperInfo r9, boolean r10, final long r11) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MessagesController.saveWallpaperToServer(java.io.File, org.telegram.ui.ActionBar.Theme$OverrideWallpaperInfo, boolean, long):void");
    }

    public void selectDialogFilter(DialogFilter dialogFilter, int i) {
        DialogFilter[] dialogFilterArr = this.selectedDialogFilter;
        DialogFilter dialogFilter2 = dialogFilterArr[i];
        if (dialogFilter2 == dialogFilter) {
            return;
        }
        dialogFilterArr[i] = dialogFilter;
        if (dialogFilterArr[i == 0 ? (char) 1 : (char) 0] == dialogFilter) {
            dialogFilterArr[i == 0 ? (char) 1 : (char) 0] = null;
        }
        if (dialogFilterArr[i] != null) {
            sortDialogs(null);
        } else if (dialogFilter2 != null) {
            dialogFilter2.dialogs.clear();
            dialogFilter2.dialogsForward.clear();
        }
    }

    public void sendBotStart(TLRPC.User user, String str) {
        if (user == null) {
            return;
        }
        TLRPC.TL_messages_startBot tL_messages_startBot = new TLRPC.TL_messages_startBot();
        tL_messages_startBot.bot = getInputUser(user);
        tL_messages_startBot.peer = getInputPeer(user.id);
        tL_messages_startBot.start_param = str;
        tL_messages_startBot.random_id = Utilities.random.nextLong();
        getConnectionsManager().sendRequest(tL_messages_startBot, new RequestDelegate() {
            @Override
            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                MessagesController.this.lambda$sendBotStart$271(tLObject, tL_error);
            }
        });
    }

    public boolean sendTyping(long j, long j2, int i, int i2) {
        return sendTyping(j, j2, i, null, i2);
    }

    public boolean sendTyping(final long r17, final long r19, final int r21, java.lang.String r22, int r23) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MessagesController.sendTyping(long, long, int, java.lang.String, int):boolean");
    }

    public void setBoostsToUnblockRestrictions(final long j, int i) {
        TLRPC.TL_channels_setBoostsToUnblockRestrictions tL_channels_setBoostsToUnblockRestrictions = new TLRPC.TL_channels_setBoostsToUnblockRestrictions();
        tL_channels_setBoostsToUnblockRestrictions.boosts = i;
        tL_channels_setBoostsToUnblockRestrictions.channel = getInputChannel(j);
        getConnectionsManager().sendRequest(tL_channels_setBoostsToUnblockRestrictions, new RequestDelegate() {
            @Override
            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                MessagesController.this.lambda$setBoostsToUnblockRestrictions$90(j, tLObject, tL_error);
            }
        });
    }

    public void setChannelSlowMode(final long j, int i) {
        TLRPC.TL_channels_toggleSlowMode tL_channels_toggleSlowMode = new TLRPC.TL_channels_toggleSlowMode();
        tL_channels_toggleSlowMode.seconds = i;
        tL_channels_toggleSlowMode.channel = getInputChannel(j);
        getConnectionsManager().sendRequest(tL_channels_toggleSlowMode, new RequestDelegate() {
            @Override
            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                MessagesController.this.lambda$setChannelSlowMode$88(j, tLObject, tL_error);
            }
        });
    }

    public void setChatPendingRequestsOnClose(long j, int i) {
        this.mainPreferences.edit().putInt("chatPendingRequests" + j, i).apply();
    }

    public void setChatReactions(final long j, int i, List<String> list) {
        TLRPC.ChatReactions tL_chatReactionsAll;
        final TLRPC.TL_messages_setChatAvailableReactions tL_messages_setChatAvailableReactions = new TLRPC.TL_messages_setChatAvailableReactions();
        tL_messages_setChatAvailableReactions.peer = getInputPeer(-j);
        if (i == 2) {
            tL_chatReactionsAll = new TLRPC.TL_chatReactionsNone();
        } else {
            if (i != 0) {
                TLRPC.TL_chatReactionsSome tL_chatReactionsSome = new TLRPC.TL_chatReactionsSome();
                tL_messages_setChatAvailableReactions.available_reactions = tL_chatReactionsSome;
                for (int i2 = 0; i2 < list.size(); i2++) {
                    TLRPC.TL_reactionEmoji tL_reactionEmoji = new TLRPC.TL_reactionEmoji();
                    tL_reactionEmoji.emoticon = list.get(i2);
                    tL_chatReactionsSome.reactions.add(tL_reactionEmoji);
                }
                getConnectionsManager().sendRequest(tL_messages_setChatAvailableReactions, new RequestDelegate() {
                    @Override
                    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                        MessagesController.this.lambda$setChatReactions$423(j, tL_messages_setChatAvailableReactions, tLObject, tL_error);
                    }
                });
            }
            tL_chatReactionsAll = new TLRPC.TL_chatReactionsAll();
        }
        tL_messages_setChatAvailableReactions.available_reactions = tL_chatReactionsAll;
        getConnectionsManager().sendRequest(tL_messages_setChatAvailableReactions, new RequestDelegate() {
            @Override
            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                MessagesController.this.lambda$setChatReactions$423(j, tL_messages_setChatAvailableReactions, tLObject, tL_error);
            }
        });
    }

    public void setContentSettings(boolean z) {
        TL_account.contentSettings contentsettings = this.contentSettings;
        if (contentsettings != null) {
            if (!contentsettings.sensitive_can_change) {
                return;
            } else {
                contentsettings.sensitive_enabled = z;
            }
        }
        if (this.ignoreRestrictionReasons == null) {
            this.ignoreRestrictionReasons = new HashSet();
        }
        Set<String> set = this.ignoreRestrictionReasons;
        if (z) {
            set.add("sensitive");
        } else {
            set.remove("sensitive");
        }
        TL_account.setContentSettings setcontentsettings = new TL_account.setContentSettings();
        setcontentsettings.sensitive_enabled = z;
        getConnectionsManager().sendRequest(setcontentsettings, new RequestDelegate() {
            @Override
            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                MessagesController.lambda$setContentSettings$456(tLObject, tL_error);
            }
        });
    }

    public void setCustomChatReactions(final long r15, int r17, java.util.List<org.telegram.tgnet.TLRPC.Reaction> r18, int r19, java.lang.Boolean r20, final org.telegram.messenger.Utilities.Callback<org.telegram.tgnet.TLRPC.TL_error> r21, final java.lang.Runnable r22) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MessagesController.setCustomChatReactions(long, int, java.util.List, int, java.lang.Boolean, org.telegram.messenger.Utilities$Callback, java.lang.Runnable):void");
    }

    public void setDefaultBannedRole(final long j, TLRPC.TL_chatBannedRights tL_chatBannedRights, final boolean z, final BaseFragment baseFragment) {
        if (tL_chatBannedRights == null) {
            return;
        }
        final TLRPC.TL_messages_editChatDefaultBannedRights tL_messages_editChatDefaultBannedRights = new TLRPC.TL_messages_editChatDefaultBannedRights();
        tL_messages_editChatDefaultBannedRights.peer = getInputPeer(-j);
        tL_messages_editChatDefaultBannedRights.banned_rights = tL_chatBannedRights;
        getConnectionsManager().sendRequest(tL_messages_editChatDefaultBannedRights, new RequestDelegate() {
            @Override
            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                MessagesController.this.lambda$setDefaultBannedRole$93(j, baseFragment, tL_messages_editChatDefaultBannedRights, z, tLObject, tL_error);
            }
        });
    }

    public void setDefaultSendAs(final long j, long j2) {
        TLRPC.ChatFull chatFull = getChatFull(-j);
        if (chatFull != null) {
            chatFull.default_send_as = getPeer(j2);
            getMessagesStorage().updateChatInfo(chatFull, false);
            getNotificationCenter().lambda$postNotificationNameOnUIThread$1(NotificationCenter.updateDefaultSendAsPeer, Long.valueOf(j), chatFull.default_send_as);
        }
        TLRPC.TL_messages_saveDefaultSendAs tL_messages_saveDefaultSendAs = new TLRPC.TL_messages_saveDefaultSendAs();
        tL_messages_saveDefaultSendAs.peer = getInputPeer(j);
        tL_messages_saveDefaultSendAs.send_as = getInputPeer(j2);
        getConnectionsManager().sendRequest(tL_messages_saveDefaultSendAs, new RequestDelegate() {
            @Override
            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                MessagesController.this.lambda$setDefaultSendAs$254(j, tLObject, tL_error);
            }
        }, 64);
    }

    public void setDialogHistoryTTL(long j, int i) {
        TLRPC.UserFull userFull;
        TLRPC.TL_messages_setHistoryTTL tL_messages_setHistoryTTL = new TLRPC.TL_messages_setHistoryTTL();
        tL_messages_setHistoryTTL.peer = getInputPeer(j);
        tL_messages_setHistoryTTL.period = i;
        getConnectionsManager().sendRequest(tL_messages_setHistoryTTL, new RequestDelegate() {
            @Override
            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                MessagesController.this.lambda$setDialogHistoryTTL$127(tLObject, tL_error);
            }
        });
        TLRPC.Dialog dialog = (TLRPC.Dialog) getMessagesController().dialogs_dict.get(j);
        if (dialog != null) {
            dialog.ttl_period = i;
        }
        getMessagesStorage().setDialogTtl(j, i);
        TLRPC.ChatFull chatFull = null;
        if (j > 0) {
            userFull = getUserFull(j);
            if (userFull != null) {
                userFull.ttl_period = i;
                userFull.flags |= 16384;
            }
        } else {
            TLRPC.ChatFull chatFull2 = getChatFull(-j);
            if (chatFull2 != null) {
                chatFull2.ttl_period = i;
                chatFull2.flags = chatFull2 instanceof TLRPC.TL_channelFull ? chatFull2.flags | 16777216 : chatFull2.flags | 16384;
            }
            chatFull = chatFull2;
            userFull = null;
        }
        if (chatFull == null) {
            if (userFull != null) {
                getNotificationCenter().lambda$postNotificationNameOnUIThread$1(NotificationCenter.userInfoDidLoad, Long.valueOf(j), userFull);
            }
        } else {
            NotificationCenter notificationCenter = getNotificationCenter();
            int i2 = NotificationCenter.chatInfoDidLoad;
            Boolean bool = Boolean.FALSE;
            notificationCenter.lambda$postNotificationNameOnUIThread$1(i2, chatFull, 0, bool, bool);
        }
    }

    public void setDialogsInTransaction(boolean z) {
        this.dialogsInTransaction = z;
        if (z) {
            return;
        }
        getNotificationCenter().lambda$postNotificationNameOnUIThread$1(NotificationCenter.dialogsNeedReload, Boolean.TRUE);
    }

    public void setFolderTags(boolean z) {
        SharedPreferences.Editor edit = this.mainPreferences.edit();
        this.folderTags = z;
        edit.putBoolean("folderTags", z).apply();
    }

    public void setLastCreatedDialogId(final long j, final boolean z, final boolean z2) {
        if (!z) {
            ArrayList<Long> arrayList = this.createdDialogMainThreadIds;
            Long valueOf = Long.valueOf(j);
            if (!z2) {
                arrayList.remove(valueOf);
                SparseArray sparseArray = (SparseArray) this.pollsToCheck.get(j);
                if (sparseArray != null) {
                    int size = sparseArray.size();
                    for (int i = 0; i < size; i++) {
                        ((MessageObject) sparseArray.valueAt(i)).pollVisibleOnScreen = false;
                    }
                }
            } else if (arrayList.contains(valueOf)) {
                return;
            } else {
                arrayList.add(Long.valueOf(j));
            }
        }
        Utilities.stageQueue.postRunnable(new Runnable() {
            @Override
            public final void run() {
                MessagesController.this.lambda$setLastCreatedDialogId$50(z, z2, j);
            }
        });
    }

    public void setLastVisibleDialogId(long j, boolean z, boolean z2) {
        ArrayList<Long> arrayList = z ? this.visibleScheduledDialogMainThreadIds : this.visibleDialogMainThreadIds;
        if (!z2) {
            arrayList.remove(Long.valueOf(j));
        } else {
            if (arrayList.contains(Long.valueOf(j))) {
                return;
            }
            arrayList.add(Long.valueOf(j));
        }
    }

    public void setParticipantBannedRole(long j, TLRPC.User user, TLRPC.Chat chat, TLRPC.TL_chatBannedRights tL_chatBannedRights, boolean z, BaseFragment baseFragment) {
        setParticipantBannedRole(j, user, chat, tL_chatBannedRights, z, baseFragment, null);
    }

    public void setParticipantBannedRole(final long j, TLRPC.User user, TLRPC.Chat chat, TLRPC.TL_chatBannedRights tL_chatBannedRights, final boolean z, final BaseFragment baseFragment, final Runnable runnable) {
        if ((user == null && chat == null) || tL_chatBannedRights == null) {
            return;
        }
        final TLRPC.TL_channels_editBanned tL_channels_editBanned = new TLRPC.TL_channels_editBanned();
        tL_channels_editBanned.channel = getInputChannel(j);
        tL_channels_editBanned.participant = user != null ? getInputPeer(user) : getInputPeer(chat);
        tL_channels_editBanned.banned_rights = tL_chatBannedRights;
        getConnectionsManager().sendRequest(tL_channels_editBanned, new RequestDelegate() {
            @Override
            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                MessagesController.this.lambda$setParticipantBannedRole$86(j, runnable, baseFragment, tL_channels_editBanned, z, tLObject, tL_error);
            }
        });
    }

    public void setReferer(String str) {
        if (str == null) {
            return;
        }
        this.installReferer = str;
        this.mainPreferences.edit().putString("installReferer", str).commit();
    }

    public void setSavedViewAs(boolean z) {
        if (this.savedViewAsChats != z) {
            SharedPreferences.Editor edit = this.mainPreferences.edit();
            this.savedViewAsChats = z;
            edit.putBoolean("savedViewAsChats", z).apply();
        }
    }

    public void setStoryQuality(boolean z) {
        if (this.storyQualityFull != z) {
            SharedPreferences.Editor edit = this.mainPreferences.edit();
            this.storyQualityFull = z;
            edit.putBoolean("storyQualityFull", z).apply();
            getNotificationCenter().lambda$postNotificationNameOnUIThread$1(NotificationCenter.storyQualityUpdate, new Object[0]);
        }
    }

    public void setUserAdminRole(long j, TLRPC.User user, TLRPC.TL_chatAdminRights tL_chatAdminRights, String str, boolean z, BaseFragment baseFragment, boolean z2, boolean z3, String str2, Runnable runnable) {
        setUserAdminRole(j, user, tL_chatAdminRights, str, z, baseFragment, z2, z3, str2, runnable, null);
    }

    public void setUserAdminRole(final long j, final TLRPC.User user, TLRPC.TL_chatAdminRights tL_chatAdminRights, String str, final boolean z, final BaseFragment baseFragment, boolean z2, boolean z3, String str2, final Runnable runnable, final ErrorDelegate errorDelegate) {
        int i;
        boolean z4;
        MessagesController messagesController;
        long j2;
        TLRPC.User user2;
        String str3;
        BaseFragment baseFragment2;
        Runnable runnable2;
        if (user == null || tL_chatAdminRights == null) {
            return;
        }
        final TLRPC.Chat chat = getChat(Long.valueOf(j));
        if (ChatObject.isChannel(chat)) {
            final TLRPC.TL_channels_editAdmin tL_channels_editAdmin = new TLRPC.TL_channels_editAdmin();
            tL_channels_editAdmin.channel = getInputChannel(chat);
            tL_channels_editAdmin.user_id = getInputUser(user);
            tL_channels_editAdmin.admin_rights = tL_chatAdminRights;
            tL_channels_editAdmin.rank = str;
            final RequestDelegate requestDelegate = new RequestDelegate() {
                @Override
                public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                    MessagesController.this.lambda$setUserAdminRole$98(j, runnable, chat, user, errorDelegate, baseFragment, tL_channels_editAdmin, z, tLObject, tL_error);
                }
            };
            if (user.bot || !z2) {
                getConnectionsManager().sendRequest(tL_channels_editAdmin, requestDelegate);
                return;
            }
            runnable2 = new Runnable() {
                @Override
                public final void run() {
                    MessagesController.this.lambda$setUserAdminRole$99(tL_channels_editAdmin, requestDelegate);
                }
            };
            i = 0;
            z4 = true;
            messagesController = this;
            j2 = j;
            user2 = user;
            str3 = str2;
            baseFragment2 = baseFragment;
        } else {
            final TLRPC.TL_messages_editChatAdmin tL_messages_editChatAdmin = new TLRPC.TL_messages_editChatAdmin();
            tL_messages_editChatAdmin.chat_id = j;
            tL_messages_editChatAdmin.user_id = getInputUser(user);
            tL_messages_editChatAdmin.is_admin = z3 || tL_chatAdminRights.change_info || tL_chatAdminRights.delete_messages || tL_chatAdminRights.ban_users || tL_chatAdminRights.invite_users || tL_chatAdminRights.pin_messages || tL_chatAdminRights.add_admins || tL_chatAdminRights.manage_call;
            final RequestDelegate requestDelegate2 = new RequestDelegate() {
                @Override
                public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                    MessagesController.this.lambda$setUserAdminRole$103(j, runnable, baseFragment, tL_messages_editChatAdmin, errorDelegate, tLObject, tL_error);
                }
            };
            if (!tL_messages_editChatAdmin.is_admin && !z2 && TextUtils.isEmpty(str2)) {
                getConnectionsManager().sendRequest(tL_messages_editChatAdmin, requestDelegate2);
                return;
            }
            i = 0;
            z4 = true;
            messagesController = this;
            j2 = j;
            user2 = user;
            str3 = str2;
            baseFragment2 = baseFragment;
            runnable2 = new Runnable() {
                @Override
                public final void run() {
                    MessagesController.this.lambda$setUserAdminRole$104(tL_messages_editChatAdmin, requestDelegate2);
                }
            };
        }
        messagesController.addUserToChat(j2, user2, i, str3, baseFragment2, z4, runnable2, errorDelegate);
    }

    public boolean shouldShowMoveCaptionHint() {
        return getMainSettings().getInt("movecaptionhint", 0) < 24 || BuildVars.DEBUG_PRIVATE_VERSION;
    }

    public boolean showSensitiveContent() {
        if (this.contentSettings != null && System.currentTimeMillis() - this.contentSettingsLoadedTime < 3600000) {
            return this.contentSettings.sensitive_enabled;
        }
        Set<String> set = this.ignoreRestrictionReasons;
        return set == null || set.contains("sensitive");
    }

    public void sortDialogs(androidx.collection.LongSparseArray r17) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MessagesController.sortDialogs(androidx.collection.LongSparseArray):void");
    }

    public void sortDialogsList(ArrayList<TLRPC.Dialog> arrayList) {
        if (arrayList == null) {
            return;
        }
        Collections.sort(arrayList, this.dialogComparator);
    }

    public boolean starsPurchaseAvailable() {
        return !this.starsLocked;
    }

    public void startShortPoll(TLRPC.Chat chat, int i, boolean z) {
        startShortPoll(chat, i, z, null);
    }

    public void startShortPoll(final TLRPC.Chat chat, final int i, final boolean z, final androidx.core.util.Consumer consumer) {
        if (chat == null) {
            return;
        }
        Utilities.stageQueue.postRunnable(new Runnable() {
            @Override
            public final void run() {
                MessagesController.this.lambda$startShortPoll$310(chat, z, i, consumer);
            }
        });
    }

    public boolean storiesEnabled() {
        char c;
        String str = this.storiesPosting;
        int hashCode = str.hashCode();
        if (hashCode == -1609594047) {
            if (str.equals("enabled")) {
                c = 1;
            }
            c = 65535;
        } else if (hashCode != -318452137) {
            if (hashCode == 270940796 && str.equals("disabled")) {
                c = 3;
            }
            c = 65535;
        } else {
            if (str.equals("premium")) {
                c = 0;
            }
            c = 65535;
        }
        return c != 0 ? c == 1 : getUserConfig().isPremium();
    }

    public boolean storyEntitiesAllowed() {
        char c;
        String str = this.storiesEntities;
        int hashCode = str.hashCode();
        if (hashCode == -1609594047) {
            if (str.equals("enabled")) {
                c = 1;
            }
            c = 65535;
        } else if (hashCode != -318452137) {
            if (hashCode == 270940796 && str.equals("disabled")) {
                c = 3;
            }
            c = 65535;
        } else {
            if (str.equals("premium")) {
                c = 0;
            }
            c = 65535;
        }
        return c != 0 ? c == 1 : getUserConfig().isPremium();
    }

    public boolean storyEntitiesAllowed(TLRPC.User user) {
        char c;
        if (user != null && user.id == this.storiesChangelogUserId) {
            return true;
        }
        String str = this.storiesEntities;
        int hashCode = str.hashCode();
        if (hashCode == -1609594047) {
            if (str.equals("enabled")) {
                c = 1;
            }
            c = 65535;
        } else if (hashCode != -318452137) {
            if (hashCode == 270940796 && str.equals("disabled")) {
                c = 3;
            }
            c = 65535;
        } else {
            if (str.equals("premium")) {
                c = 0;
            }
            c = 65535;
        }
        return c != 0 ? c == 1 : user != null && user.premium;
    }

    public void toggleChannelForum(long j, boolean z) {
        TLRPC.TL_channels_toggleForum tL_channels_toggleForum = new TLRPC.TL_channels_toggleForum();
        tL_channels_toggleForum.channel = getInputChannel(j);
        tL_channels_toggleForum.enabled = z;
        getConnectionsManager().sendRequest(tL_channels_toggleForum, new RequestDelegate() {
            @Override
            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                MessagesController.this.lambda$toggleChannelForum$264(tLObject, tL_error);
            }
        }, 64);
    }

    public void toggleChannelInvitesHistory(long j, boolean z) {
        TLRPC.TL_channels_togglePreHistoryHidden tL_channels_togglePreHistoryHidden = new TLRPC.TL_channels_togglePreHistoryHidden();
        tL_channels_togglePreHistoryHidden.channel = getInputChannel(j);
        tL_channels_togglePreHistoryHidden.enabled = z;
        getConnectionsManager().sendRequest(tL_channels_togglePreHistoryHidden, new RequestDelegate() {
            @Override
            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                MessagesController.this.lambda$toggleChannelInvitesHistory$266(tLObject, tL_error);
            }
        }, 64);
    }

    public void toggleChannelSignatures(long j, boolean z, boolean z2) {
        TLRPC.Chat chat = getChat(Long.valueOf(j));
        if (chat != null) {
            chat.signatures = z;
            chat.signature_profiles = z2;
            putChat(chat, true);
        }
        TLRPC.TL_channels_toggleSignatures tL_channels_toggleSignatures = new TLRPC.TL_channels_toggleSignatures();
        tL_channels_toggleSignatures.channel = getInputChannel(j);
        tL_channels_toggleSignatures.signatures_enabled = z;
        tL_channels_toggleSignatures.profiles_enabled = z2;
        getConnectionsManager().sendRequest(tL_channels_toggleSignatures, new RequestDelegate() {
            @Override
            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                MessagesController.this.lambda$toggleChannelSignatures$262(tLObject, tL_error);
            }
        }, 64);
        getNotificationCenter().lambda$postNotificationNameOnUIThread$1(NotificationCenter.updateAllMessages, Long.valueOf(-j));
    }

    public void toggleChatJoinRequest(long j, boolean z, final Runnable runnable, final Runnable runnable2) {
        TLRPC.TL_channels_toggleJoinRequest tL_channels_toggleJoinRequest = new TLRPC.TL_channels_toggleJoinRequest();
        tL_channels_toggleJoinRequest.channel = getInputChannel(j);
        tL_channels_toggleJoinRequest.enabled = z;
        getConnectionsManager().sendRequest(tL_channels_toggleJoinRequest, new RequestDelegate() {
            @Override
            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                MessagesController.this.lambda$toggleChatJoinRequest$260(runnable, runnable2, tLObject, tL_error);
            }
        }, 64);
    }

    public void toggleChatJoinToSend(long j, boolean z, final Runnable runnable, final Runnable runnable2) {
        TLRPC.TL_channels_toggleJoinToSend tL_channels_toggleJoinToSend = new TLRPC.TL_channels_toggleJoinToSend();
        tL_channels_toggleJoinToSend.channel = getInputChannel(j);
        tL_channels_toggleJoinToSend.enabled = z;
        getConnectionsManager().sendRequest(tL_channels_toggleJoinToSend, new RequestDelegate() {
            @Override
            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                MessagesController.this.lambda$toggleChatJoinToSend$258(runnable, runnable2, tLObject, tL_error);
            }
        }, 64);
    }

    public void toggleChatNoForwards(long j, boolean z) {
        TLRPC.TL_messages_toggleNoForwards tL_messages_toggleNoForwards = new TLRPC.TL_messages_toggleNoForwards();
        tL_messages_toggleNoForwards.peer = getInputPeer(-j);
        tL_messages_toggleNoForwards.enabled = z;
        getConnectionsManager().sendRequest(tL_messages_toggleNoForwards, new RequestDelegate() {
            @Override
            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                MessagesController.this.lambda$toggleChatNoForwards$256(tLObject, tL_error);
            }
        }, 64);
    }

    public void unblockPeer(long j) {
        unblockPeer(j, null);
    }

    public void unblockPeer(long j, final Runnable runnable) {
        TLRPC.Chat chat;
        TLRPC.TL_contacts_unblock tL_contacts_unblock = new TLRPC.TL_contacts_unblock();
        TLRPC.User user = null;
        if (j > 0) {
            TLRPC.User user2 = getUser(Long.valueOf(j));
            if (user2 == null) {
                return;
            }
            user = user2;
            chat = null;
        } else {
            chat = getChat(Long.valueOf(-j));
            if (chat == null) {
                return;
            }
        }
        this.totalBlockedCount--;
        this.blockePeers.delete(j);
        tL_contacts_unblock.id = user != null ? getInputPeer(user) : getInputPeer(chat);
        getNotificationCenter().lambda$postNotificationNameOnUIThread$1(NotificationCenter.blockedUsersDidLoad, new Object[0]);
        getConnectionsManager().sendRequest(tL_contacts_unblock, new RequestDelegate() {
            @Override
            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                MessagesController.lambda$unblockPeer$106(runnable, tLObject, tL_error);
            }
        });
    }

    public void unpinAllMessages(final TLRPC.Chat chat, final TLRPC.User user) {
        if (chat == null && user == null) {
            return;
        }
        TLRPC.TL_messages_unpinAllMessages tL_messages_unpinAllMessages = new TLRPC.TL_messages_unpinAllMessages();
        tL_messages_unpinAllMessages.peer = getInputPeer(chat != null ? -chat.id : user.id);
        getConnectionsManager().sendRequest(tL_messages_unpinAllMessages, new RequestDelegate() {
            @Override
            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                MessagesController.this.lambda$unpinAllMessages$121(chat, user, tLObject, tL_error);
            }
        });
    }

    public void unregistedPush() {
        if (getUserConfig().registeredForPush && SharedConfig.pushString.length() == 0) {
            TL_account.unregisterDevice unregisterdevice = new TL_account.unregisterDevice();
            unregisterdevice.token = SharedConfig.pushString;
            unregisterdevice.token_type = SharedConfig.pushType;
            for (int i = 0; i < 4; i++) {
                UserConfig userConfig = UserConfig.getInstance(i);
                if (i != this.currentAccount && userConfig.isClientActivated()) {
                    unregisterdevice.other_uids.add(Long.valueOf(userConfig.getClientUserId()));
                }
            }
            getConnectionsManager().sendRequest(unregisterdevice, new RequestDelegate() {
                @Override
                public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                    MessagesController.lambda$unregistedPush$297(tLObject, tL_error);
                }
            });
        }
    }

    public void updateChannelUserName(final BaseFragment baseFragment, final long j, final String str, final Runnable runnable, final Runnable runnable2) {
        final TLRPC.TL_channels_updateUsername tL_channels_updateUsername = new TLRPC.TL_channels_updateUsername();
        tL_channels_updateUsername.channel = getInputChannel(j);
        tL_channels_updateUsername.username = str;
        getConnectionsManager().sendRequest(tL_channels_updateUsername, new RequestDelegate() {
            @Override
            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                MessagesController.this.lambda$updateChannelUserName$270(j, str, runnable, baseFragment, tL_channels_updateUsername, runnable2, tLObject, tL_error);
            }
        }, 64);
    }

    public void updateChatAbout(long j, final String str, final TLRPC.ChatFull chatFull) {
        TLRPC.TL_messages_editChatAbout tL_messages_editChatAbout = new TLRPC.TL_messages_editChatAbout();
        tL_messages_editChatAbout.peer = getInputPeer(-j);
        tL_messages_editChatAbout.about = str;
        getConnectionsManager().sendRequest(tL_messages_editChatAbout, new RequestDelegate() {
            @Override
            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                MessagesController.this.lambda$updateChatAbout$268(chatFull, str, tLObject, tL_error);
            }
        }, 64);
    }

    public void updateConfig(final TLRPC.TL_config tL_config) {
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                MessagesController.this.lambda$updateConfig$36(tL_config);
            }
        });
    }

    public void updateEmojiStatus(TLRPC.EmojiStatus emojiStatus) {
        TL_account.updateEmojiStatus updateemojistatus = new TL_account.updateEmojiStatus();
        updateemojistatus.emoji_status = emojiStatus;
        TLRPC.User currentUser = getUserConfig().getCurrentUser();
        if (currentUser != null) {
            currentUser.emoji_status = updateemojistatus.emoji_status;
            getNotificationCenter().lambda$postNotificationNameOnUIThread$1(NotificationCenter.userEmojiStatusUpdated, currentUser);
            getMessagesController().updateEmojiStatusUntilUpdate(currentUser.id, currentUser.emoji_status);
        }
        getConnectionsManager().sendRequest(updateemojistatus, new RequestDelegate() {
            @Override
            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                MessagesController.lambda$updateEmojiStatus$29(tLObject, tL_error);
            }
        });
    }

    public void updateEmojiStatusUntil() {
        int currentTimeMillis = (int) (System.currentTimeMillis() / 1000);
        Long l = null;
        int i = 0;
        while (i < this.emojiStatusUntilValues.size()) {
            if (((Integer) this.emojiStatusUntilValues.valueAt(i)).intValue() > currentTimeMillis) {
                l = Long.valueOf(Math.min(l == null ? Long.MAX_VALUE : l.longValue(), r5 - currentTimeMillis));
            } else {
                this.emojiStatusUntilValues.removeAt(i);
                i--;
            }
            i++;
        }
        if (l == null) {
            Runnable runnable = this.recentEmojiStatusUpdateRunnable;
            if (runnable != null) {
                this.recentEmojiStatusUpdateRunnableTime = -1L;
                this.recentEmojiStatusUpdateRunnableTimeout = -1L;
                AndroidUtilities.cancelRunOnUIThread(runnable);
                return;
            }
            return;
        }
        long longValue = l.longValue() + 2;
        long j = currentTimeMillis;
        if (j + longValue != this.recentEmojiStatusUpdateRunnableTime + this.recentEmojiStatusUpdateRunnableTimeout) {
            AndroidUtilities.cancelRunOnUIThread(this.recentEmojiStatusUpdateRunnable);
            this.recentEmojiStatusUpdateRunnableTime = j;
            this.recentEmojiStatusUpdateRunnableTimeout = longValue;
            Runnable runnable2 = new Runnable() {
                @Override
                public final void run() {
                    MessagesController.this.lambda$updateEmojiStatusUntil$426();
                }
            };
            this.recentEmojiStatusUpdateRunnable = runnable2;
            AndroidUtilities.runOnUIThread(runnable2, longValue * 1000);
        }
    }

    public void updateEmojiStatusUntilUpdate(long j, TLRPC.EmojiStatus emojiStatus) {
        if (emojiStatus instanceof TLRPC.TL_emojiStatusUntil) {
            this.emojiStatusUntilValues.put(j, Integer.valueOf(((TLRPC.TL_emojiStatusUntil) emojiStatus).until));
        } else if (!this.emojiStatusUntilValues.containsKey(j)) {
            return;
        } else {
            this.emojiStatusUntilValues.remove(j);
        }
        updateEmojiStatusUntil();
    }

    public void updateFilterDialogs(DialogFilter dialogFilter) {
        TLRPC.EncryptedChat encryptedChat;
        if (dialogFilter == null) {
            return;
        }
        ArrayList<TLRPC.Dialog> arrayList = dialogFilter.dialogs;
        ArrayList<TLRPC.Dialog> arrayList2 = dialogFilter.dialogsForward;
        arrayList.clear();
        arrayList2.clear();
        this.sortingDialogFilter = dialogFilter;
        try {
            Collections.sort(this.allDialogs, this.dialogDateComparator);
        } catch (Exception e) {
            FileLog.e(e);
        }
        int size = this.allDialogs.size();
        for (int i = 0; i < size; i++) {
            TLRPC.Dialog dialog = this.allDialogs.get(i);
            if (dialog instanceof TLRPC.TL_dialog) {
                long j = dialog.id;
                if (DialogObject.isEncryptedDialog(j) && (encryptedChat = getEncryptedChat(Integer.valueOf(DialogObject.getEncryptedChatId(j)))) != null) {
                    j = encryptedChat.user_id;
                }
                if (dialogFilter.includesDialog(getAccountInstance(), j, dialog)) {
                    if (canAddToForward(dialog)) {
                        arrayList2.add(dialog);
                    }
                    arrayList.add(dialog);
                }
            }
        }
        try {
            Collections.sort(this.allDialogs, this.dialogComparator);
        } catch (Exception unused) {
        }
    }

    public boolean updateInterfaceWithMessages(final long r27, java.util.ArrayList<org.telegram.messenger.MessageObject> r29, int r30) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MessagesController.updateInterfaceWithMessages(long, java.util.ArrayList, int):boolean");
    }

    public void updatePremium(boolean z) {
        if (this.dialogFilters.isEmpty()) {
            return;
        }
        if (z) {
            for (int i = 0; i < this.dialogFilters.size(); i++) {
                this.dialogFilters.get(i).locked = false;
            }
        } else {
            if (!this.dialogFilters.get(0).isDefault()) {
                int i2 = 1;
                while (true) {
                    if (i2 >= this.dialogFilters.size()) {
                        break;
                    }
                    if (this.dialogFilters.get(i2).isDefault()) {
                        this.dialogFilters.add(0, this.dialogFilters.remove(i2));
                        break;
                    }
                    i2++;
                }
            }
            lockFiltersInternal();
        }
        getMessagesStorage().saveDialogFiltersOrder();
        getNotificationCenter().lambda$postNotificationNameOnUIThread$1(NotificationCenter.dialogFiltersUpdated, new Object[0]);
        getStoriesController().onPremiumChanged();
    }

    public void updateSavedReactionTags(long j) {
        long j2;
        String str;
        long m;
        LongSparseArray longSparseArray = this.reactionTags;
        if (longSparseArray == null) {
            return;
        }
        TLRPC.TL_messages_savedReactionsTags tL_messages_savedReactionsTags = (TLRPC.TL_messages_savedReactionsTags) longSparseArray.get(j);
        if (tL_messages_savedReactionsTags == null) {
            if (j == 0) {
                return;
            }
            LongSparseArray longSparseArray2 = this.reactionTags;
            TLRPC.TL_messages_savedReactionsTags tL_messages_savedReactionsTags2 = new TLRPC.TL_messages_savedReactionsTags();
            longSparseArray2.put(j, tL_messages_savedReactionsTags2);
            tL_messages_savedReactionsTags = tL_messages_savedReactionsTags2;
        }
        Collections.sort(tL_messages_savedReactionsTags.tags, new Comparator() {
            @Override
            public final int compare(Object obj, Object obj2) {
                int lambda$updateSavedReactionTags$435;
                lambda$updateSavedReactionTags$435 = MessagesController.this.lambda$updateSavedReactionTags$435((TLRPC.TL_savedReactionTag) obj, (TLRPC.TL_savedReactionTag) obj2);
                return lambda$updateSavedReactionTags$435;
            }
        });
        long j3 = 0;
        for (int i = 0; i < tL_messages_savedReactionsTags.tags.size(); i++) {
            TLRPC.TL_savedReactionTag tL_savedReactionTag = tL_messages_savedReactionsTags.tags.get(i);
            if (tL_savedReactionTag.count > 0) {
                TLRPC.Reaction reaction = tL_savedReactionTag.reaction;
                if (reaction instanceof TLRPC.TL_reactionEmoji) {
                    j2 = MessagesController$$ExternalSyntheticBackport12.m(r9, 0, Utilities.MD5(((TLRPC.TL_reactionEmoji) reaction).emoticon).substring(0, 16).length(), 16);
                } else {
                    if (reaction instanceof TLRPC.TL_reactionCustomEmoji) {
                        j2 = ((TLRPC.TL_reactionCustomEmoji) reaction).document_id;
                    }
                    if (j == 0 && (tL_savedReactionTag.flags & 1) != 0 && (str = tL_savedReactionTag.title) != null) {
                        m = MessagesController$$ExternalSyntheticBackport10.m(r9, 0, Utilities.MD5(str).substring(0, 16).length(), 16);
                        j3 = MediaDataController.calcHash(j3, m);
                    }
                    j3 = MediaDataController.calcHash(j3, tL_savedReactionTag.count);
                }
                j3 = MediaDataController.calcHash(j3, j2);
                if (j == 0) {
                    m = MessagesController$$ExternalSyntheticBackport10.m(r9, 0, Utilities.MD5(str).substring(0, 16).length(), 16);
                    j3 = MediaDataController.calcHash(j3, m);
                }
                j3 = MediaDataController.calcHash(j3, tL_savedReactionTag.count);
            }
        }
        tL_messages_savedReactionsTags.hash = j3;
        saveSavedReactionsTags(j, tL_messages_savedReactionsTags);
        getNotificationCenter().lambda$postNotificationNameOnUIThread$1(NotificationCenter.savedReactionTagsUpdate, Long.valueOf(j));
    }

    public void updateSavedReactionTags(HashSet<Long> hashSet) {
        updateSavedReactionTags(0L);
        Iterator<Long> it = hashSet.iterator();
        while (it.hasNext()) {
            updateSavedReactionTags(it.next().longValue());
        }
    }

    public boolean updateSavedReactionTags(long j, ReactionsLayoutInBubble.VisibleReaction visibleReaction, boolean z, boolean z2) {
        if (this.reactionTags == null) {
            return false;
        }
        int i = 0;
        boolean z3 = false;
        while (i < 2) {
            long j2 = i == 0 ? 0L : j;
            boolean z4 = true;
            if (i != 1 || j2 != 0) {
                TLRPC.TL_messages_savedReactionsTags tL_messages_savedReactionsTags = (TLRPC.TL_messages_savedReactionsTags) this.reactionTags.get(j2);
                if (tL_messages_savedReactionsTags == null) {
                    if (j2 != 0) {
                        LongSparseArray longSparseArray = this.reactionTags;
                        tL_messages_savedReactionsTags = new TLRPC.TL_messages_savedReactionsTags();
                        longSparseArray.put(j2, tL_messages_savedReactionsTags);
                    }
                }
                int i2 = 0;
                boolean z5 = false;
                boolean z6 = false;
                while (i2 < tL_messages_savedReactionsTags.tags.size()) {
                    TLRPC.TL_savedReactionTag tL_savedReactionTag = tL_messages_savedReactionsTags.tags.get(i2);
                    if (visibleReaction.isSame(tL_savedReactionTag.reaction)) {
                        int i3 = tL_savedReactionTag.count;
                        int max = Math.max(0, (z ? 1 : -1) + i3);
                        tL_savedReactionTag.count = max;
                        if (max <= 0) {
                            tL_messages_savedReactionsTags.tags.remove(i2);
                            i2--;
                        } else if (max == i3) {
                            z5 = true;
                        }
                        z3 = true;
                        z5 = true;
                        z6 = true;
                    }
                    i2++;
                }
                if (z5 || !z) {
                    z4 = z6;
                } else {
                    TLRPC.TL_savedReactionTag tL_savedReactionTag2 = new TLRPC.TL_savedReactionTag();
                    tL_savedReactionTag2.reaction = visibleReaction.toTLReaction();
                    tL_savedReactionTag2.count = 1;
                    tL_messages_savedReactionsTags.tags.add(tL_savedReactionTag2);
                    z3 = true;
                }
                if (z2 && z4) {
                    updateSavedReactionTags(j2);
                }
                i++;
            }
            i++;
        }
        return z3;
    }

    public void updateTimerProc() {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MessagesController.updateTimerProc():void");
    }

    public void updateTranscribeAudioTrialCooldownUntil(int i) {
        if (i != this.transcribeAudioTrialCooldownUntil) {
            this.transcribeAudioTrialCooldownUntil = i;
            this.mainPreferences.edit().putInt("transcribeAudioTrialCooldownUntil", this.transcribeAudioTrialCooldownUntil).apply();
            scheduleTranscriptionUpdate();
        }
    }

    public void updateTranscribeAudioTrialCurrentNumber(int i) {
        if (i != this.transcribeAudioTrialCurrentNumber) {
            this.transcribeAudioTrialCurrentNumber = i;
            this.mainPreferences.edit().putInt("transcribeAudioTrialCurrentNumber", this.transcribeAudioTrialCurrentNumber).apply();
        }
    }

    public void updateUsernameActiveness(TLObject tLObject, String str, boolean z) {
        if (TextUtils.isEmpty(str)) {
            return;
        }
        this.objectsByUsernames.remove(str);
        if (z) {
            this.objectsByUsernames.put(str.toLowerCase(), tLObject);
        }
    }

    public void uploadAndApplyUserAvatar(TLRPC.FileLocation fileLocation) {
        if (fileLocation == null) {
            return;
        }
        this.uploadingAvatar = FileLoader.getDirectory(4) + "/" + fileLocation.volume_id + "_" + fileLocation.local_id + ".jpg";
        getFileLoader().uploadFile(this.uploadingAvatar, false, true, 16777216);
    }
}
