package org.telegram.messenger;

import android.os.SystemClock;
import android.util.SparseArray;
import android.view.View;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import org.telegram.messenger.Utilities;

public class NotificationCenter {
    private static final long EXPIRE_NOTIFICATIONS_TIME = 5017;
    private static volatile NotificationCenter[] Instance = new NotificationCenter[4];
    public static final int activeGroupCallsUpdated = 165;
    public static final int activityPermissionsGranted = 290;
    public static final int albumsDidLoad = 157;
    public static boolean alreadyLogged = false;
    public static final int animatedEmojiDocumentLoaded = 112;
    public static final int appDidLogout = 171;
    public static final int appUpdateAvailable = 267;
    public static final int applyGroupCallVisibleParticipants = 166;
    public static final int archivedStickersCountDidLoad = 84;
    public static final int articleClosed = 88;
    public static final int attachMenuBotsDidLoad = 272;
    public static final int audioDidSent = 158;
    public static final int audioRecordTooShort = 159;
    public static final int audioRouteChanged = 160;
    public static final int availableEffectsUpdate = 208;
    public static final int billingConfirmPurchaseError = 285;
    public static final int billingProductDetailsUpdated = 284;
    public static final int blockedUsersDidLoad = 40;
    public static final int bookmarkAdded = 119;
    public static final int boostByChannelCreated = 184;
    public static final int boostedChannelByUser = 183;
    public static final int botDownloadsUpdate = 225;
    public static final int botInfoDidLoad = 63;
    public static final int botKeyboardDidLoad = 66;
    public static final int botStarsTransactionsLoaded = 217;
    public static final int botStarsUpdated = 216;
    public static final int businessLinkCreated = 122;
    public static final int businessLinksUpdated = 121;
    public static final int businessMessagesUpdated = 116;
    public static final int cameraInitied = 236;
    public static final int changeRepliesCounter = 10;
    public static final int channelConnectedBotsUpdate = 227;
    public static final int channelRecommendationsLoaded = 193;
    public static final int channelRightsUpdated = 86;
    public static final int channelStarsUpdated = 218;
    public static final int channelSuggestedBotsUpdate = 226;
    public static final int chatAvailableReactionsUpdated = 273;
    public static final int chatDidCreated = 25;
    public static final int chatDidFailCreate = 26;
    public static final int chatInfoCantLoad = 28;
    public static final int chatInfoDidLoad = 27;
    public static final int chatOnlineCountDidLoad = 91;
    public static final int chatSearchResultsAvailable = 67;
    public static final int chatSearchResultsLoading = 69;
    public static final int chatSwithcedToForum = 292;
    public static final int chatWasBoostedByUser = 203;
    public static final int chatlistFolderUpdate = 299;
    public static final int closeChats = 4;
    public static final int closeInCallActivity = 169;
    public static final int closeOtherAppActivities = 235;
    public static final int closeSearchByActiveAction = 259;
    public static final int commentsRead = 9;
    public static final int configLoaded = 172;
    public static final int contactsDidLoad = 22;
    public static final int contactsImported = 23;
    public static final int currentUserPremiumStatusChanged = 280;
    public static final int currentUserShowLimitReachedDialog = 283;
    public static final int customStickerCreated = 206;
    public static final int customTypefacesLoaded = 302;
    public static final int dialogDeleted = 109;
    public static final int dialogFiltersUpdated = 177;
    public static final int dialogIsTranslatable = 126;
    public static final int dialogPhotosLoaded = 35;
    public static final int dialogPhotosUpdate = 192;
    public static final int dialogTranslate = 127;
    public static final int dialogsNeedReload = 3;
    public static final int dialogsUnreadCounterChanged = 143;
    public static final int dialogsUnreadReactionsCounterChanged = 274;
    public static final int diceStickersDidLoad = 58;
    public static final int didApplyNewTheme = 240;
    public static final int didClearDatabase = 262;
    public static final int didCreatedNewDeleteTask = 42;
    public static final int didEndCall = 168;
    public static final int didGenerateFingerprintKeyPair = 128;
    public static final int didLoadChatAdmins = 105;
    public static final int didLoadChatInviter = 104;
    public static final int didLoadPinnedMessages = 53;
    public static final int didLoadSendAsPeers = 13;
    public static final int didLoadSponsoredMessages = 12;
    public static final int didReceiveCall = 232;
    public static final int didReceiveNewMessages = 1;
    public static final int didReceiveSmsCode = 231;
    public static final int didReceivedWebpages = 55;
    public static final int didReceivedWebpagesInUpdates = 56;
    public static final int didRemoveTwoStepPassword = 51;
    public static final int didReplacedPhotoInMemCache = 237;
    public static final int didSetNewTheme = 238;
    public static final int didSetNewWallpapper = 250;
    public static final int didSetOrRemoveTwoStepPassword = 50;
    public static final int didSetPasscode = 47;
    public static final int didStartedCall = 161;
    public static final int didStartedMultiGiftsSelector = 182;
    public static final int didUpdateConnectionState = 133;
    public static final int didUpdateExtendedMedia = 100;
    public static final int didUpdateGlobalAutoDeleteTimer = 293;
    public static final int didUpdateMessagesViews = 74;
    public static final int didUpdatePollResults = 90;
    public static final int didUpdatePremiumGiftFieldIcon = 186;
    public static final int didUpdatePremiumGiftStickers = 185;
    public static final int didUpdateReactions = 99;
    public static final int didVerifyMessagesStickers = 101;
    public static final int emojiKeywordsLoaded = 198;
    public static final int emojiLoaded = 233;
    public static final int emojiPreviewThemesChanged = 270;
    public static final int encryptedChatCreated = 34;
    public static final int encryptedChatUpdated = 32;
    public static final int factCheckLoaded = 215;
    public static final int featuredEmojiDidLoad = 60;
    public static final int featuredStickersDidLoad = 59;
    public static final int fileLoadFailed = 139;
    public static final int fileLoadProgressChanged = 137;
    public static final int fileLoaded = 138;
    public static final int fileNewChunkAvailable = 141;
    public static final int filePreparingFailed = 142;
    public static final int filePreparingStarted = 140;
    public static final int fileUploadFailed = 135;
    public static final int fileUploadProgressChanged = 136;
    public static final int fileUploaded = 134;
    public static final int filterSettingsUpdated = 178;
    public static final int folderBecomeEmpty = 37;
    public static final int forceImportContactsStart = 21;
    public static final int giftsToUserSent = 181;
    private static volatile NotificationCenter globalInstance = null;
    public static final int goingToPreviewTheme = 245;
    public static final int groupCallScreencastStateChanged = 164;
    public static final int groupCallSpeakingUsersUpdated = 163;
    public static final int groupCallTypingsUpdated = 167;
    public static final int groupCallUpdated = 162;
    public static final int groupCallVisibilityChanged = 170;
    public static final int groupPackUpdated = 204;
    public static final int groupRestrictionsUnlockedByBoosts = 202;
    public static final int groupStickersDidLoad = 61;
    public static final int hasNewContactsToImport = 24;
    public static final int hashtagSearchUpdated = 68;
    public static final int historyCleared = 6;
    public static final int historyImportProgressChanged = 106;
    public static final int httpFileDidFailedLoad = 132;
    public static final int httpFileDidLoad = 131;
    public static final int invalidateMotionBackground = 234;
    public static final int liveLocationsCacheChanged = 256;
    public static final int liveLocationsChanged = 254;
    public static final int loadingMessagesFailed = 16;
    public static final int locationPermissionDenied = 247;
    public static final int locationPermissionGranted = 246;
    public static final int mainUserInfoChanged = 43;
    public static final int mediaCountDidLoad = 30;
    public static final int mediaCountsDidLoad = 31;
    public static final int mediaDidLoad = 29;
    public static final int messagePlayingDidReset = 145;
    public static final int messagePlayingDidSeek = 148;
    public static final int messagePlayingDidStart = 147;
    public static final int messagePlayingGoingToStop = 149;
    public static final int messagePlayingPlayStateChanged = 146;
    public static final int messagePlayingProgressDidChanged = 144;
    public static final int messagePlayingSpeedChanged = 260;
    public static final int messageReceivedByAck = 17;
    public static final int messageReceivedByServer = 18;
    public static final int messageReceivedByServer2 = 19;
    public static final int messageSendError = 20;
    public static final int messageTranslated = 124;
    public static final int messageTranslating = 125;
    public static final int messagesDeleted = 5;
    public static final int messagesDidLoad = 11;
    public static final int messagesDidLoadWithoutProcess = 15;
    public static final int messagesRead = 7;
    public static final int messagesReadContent = 62;
    public static final int messagesReadEncrypted = 33;
    public static final int moreMusicDidLoad = 71;
    public static final int musicDidLoad = 70;
    public static final int nearEarEvent = 306;
    public static final int needAddArchivedStickers = 83;
    public static final int needCheckSystemBarColors = 242;
    public static final int needDeleteBusinessLink = 123;
    public static final int needDeleteDialog = 173;
    public static final int needReloadRecentDialogsSearch = 75;
    public static final int needSetDayNightTheme = 244;
    public static final int needShareTheme = 243;
    public static final int needShowAlert = 72;
    public static final int needShowPlayServicesAlert = 73;
    public static final int newDraftReceived = 81;
    public static final int newEmojiSuggestionsAvailable = 174;
    public static final int newLocationAvailable = 255;
    public static final int newPeopleNearbyAvailable = 93;
    public static final int newSessionReceived = 54;
    public static final int newSuggestionsAvailable = 103;
    public static final int notificationsCountUpdated = 257;
    public static final int notificationsSettingsUpdated = 39;
    public static final int onActivityResultReceived = 277;
    public static final int onDatabaseMigration = 268;
    public static final int onDatabaseOpened = 275;
    public static final int onDatabaseReset = 294;
    public static final int onDownloadingFilesChanged = 276;
    public static final int onEmojiInteractionsReceived = 269;
    public static final int onReceivedChannelDifference = 304;
    public static final int onRequestPermissionResultReceived = 278;
    public static final int onUserRingtonesUpdated = 279;
    public static final int openArticle = 87;
    public static final int openBoostForUsersDialog = 201;
    public static final int openedChatChanged = 41;
    public static final int passcodeDismissed = 48;
    public static final int paymentFinished = 85;
    public static final int peerSettingsDidLoad = 76;
    public static final int permissionsGranted = 289;
    public static final int pinnedInfoDidLoad = 65;
    public static final int playerDidStartPlaying = 258;
    public static final int premiumFloodWaitReceived = 207;
    public static final int premiumPromoUpdated = 281;
    public static final int premiumStatusChangedGlobal = 282;
    public static final int premiumStickersPreviewLoaded = 286;
    public static final int privacyRulesUpdated = 44;
    public static final int proxyChangedByRotation = 253;
    public static final int proxyCheckDone = 252;
    public static final int proxySettingsChanged = 251;
    public static final int pushMessagesUpdated = 228;
    public static final int quickRepliesDeleted = 118;
    public static final int quickRepliesUpdated = 117;
    public static final int reactionsDidLoad = 271;
    public static final int recentDocumentsDidLoad = 82;
    public static final int recentEmojiStatusesUpdate = 113;
    public static final int recordPaused = 154;
    public static final int recordProgressChanged = 150;
    public static final int recordResumed = 155;
    public static final int recordStartError = 152;
    public static final int recordStarted = 151;
    public static final int recordStopped = 153;
    public static final int reloadDialogPhotos = 36;
    public static final int reloadHints = 78;
    public static final int reloadInlineHints = 79;
    public static final int reloadInterface = 248;
    public static final int reloadWebappsHints = 80;
    public static final int removeAllMessagesFromDialog = 38;
    public static final int replaceMessagesObjects = 46;
    public static final int replyMessagesDidLoad = 52;
    public static final int requestPermissions = 288;
    public static final int savedMessagesDialogsUpdate = 194;
    public static final int savedMessagesForwarded = 197;
    public static final int savedReactionTagsUpdate = 195;
    public static final int scheduledMessagesUpdated = 102;
    public static final int screenStateChanged = 261;
    public static final int screenshotTook = 156;
    public static final int sendingMessagesChanged = 98;
    public static final int showBulletin = 266;
    public static final int smsJobStatusUpdate = 199;
    public static final int starBalanceUpdated = 212;
    public static final int starGiftOptionsLoaded = 210;
    public static final int starGiftSoldOut = 223;
    public static final int starGiftsLoaded = 221;
    public static final int starGiveawayOptionsLoaded = 211;
    public static final int starOptionsLoaded = 209;
    public static final int starReactionAnonymousUpdate = 120;
    public static final int starSubscriptionsLoaded = 214;
    public static final int starTransactionsLoaded = 213;
    public static final int starUserGiftsLoaded = 222;
    public static final int startAllHeavyOperations = 95;
    public static final int startSpoilers = 97;
    public static final int stealthModeChanged = 303;
    public static final int stickersDidLoad = 57;
    public static final int stickersImportComplete = 108;
    public static final int stickersImportProgressChanged = 107;
    public static final int stopAllHeavyOperations = 94;
    public static final int stopSpoilers = 96;
    public static final int storiesBlocklistUpdate = 188;
    public static final int storiesDraftsUpdated = 298;
    public static final int storiesEnabledUpdate = 187;
    public static final int storiesLimitUpdate = 189;
    public static final int storiesListUpdated = 297;
    public static final int storiesReadUpdated = 305;
    public static final int storiesSendAsUpdate = 190;
    public static final int storiesUpdated = 296;
    public static final int storyQualityUpdate = 200;
    public static final int suggestedFiltersLoaded = 179;
    public static final int suggestedLangpack = 249;
    public static final int themeAccentListUpdated = 241;
    public static final int themeListUpdated = 239;
    public static final int themeUploadError = 176;
    public static final int themeUploadedToServer = 175;
    public static final int threadMessagesRead = 8;
    public static final int timezonesUpdated = 205;
    public static final int topicsDidLoaded = 291;
    private static int totalEvents = 307;
    public static final int twoStepPasswordChanged = 49;
    public static final int unconfirmedAuthUpdate = 191;
    public static final int updateAllMessages = 220;
    public static final int updateBotMenuButton = 180;
    public static final int updateDefaultSendAsPeer = 14;
    public static final int updateInterfaces = 2;
    public static final int updateMentionsCount = 89;
    public static final int updateMessageMedia = 45;
    public static final int updateSearchSettings = 114;
    public static final int updateStories = 224;
    public static final int updateTranscriptionLock = 115;
    public static final int uploadStoryEnd = 301;
    public static final int uploadStoryProgress = 300;
    public static final int userEmojiStatusUpdated = 287;
    public static final int userInfoDidLoad = 64;
    public static final int userIsPremiumBlockedUpadted = 196;
    public static final int videoLoadingStateChanged = 92;
    public static final int voiceTranscriptionUpdate = 111;
    public static final int voipServiceCreated = 263;
    public static final int walletPendingTransactionsChanged = 129;
    public static final int walletSyncProgressChanged = 130;
    public static final int wallpaperSettedToUser = 295;
    public static final int wallpapersDidLoad = 229;
    public static final int wallpapersNeedReload = 230;
    public static final int wasUnableToFindCurrentLocation = 77;
    public static final int webRtcMicAmplitudeEvent = 264;
    public static final int webRtcSpeakerAmplitudeEvent = 265;
    public static final int webViewResolved = 219;
    public static final int webViewResultSent = 110;
    private int animationInProgressCount;
    private Runnable checkForExpiredNotifications;
    private int currentAccount;
    private int currentHeavyOperationFlags;
    private SparseArray<ArrayList<NotificationCenterDelegate>> observers = new SparseArray<>();
    private SparseArray<ArrayList<NotificationCenterDelegate>> removeAfterBroadcast = new SparseArray<>();
    private SparseArray<ArrayList<NotificationCenterDelegate>> addAfterBroadcast = new SparseArray<>();
    private ArrayList<DelayedPost> delayedPosts = new ArrayList<>(10);
    private ArrayList<Runnable> delayedRunnables = new ArrayList<>(10);
    private ArrayList<Runnable> delayedRunnablesTmp = new ArrayList<>(10);
    private ArrayList<DelayedPost> delayedPostsTmp = new ArrayList<>(10);
    private ArrayList<PostponeNotificationCallback> postponeCallbackList = new ArrayList<>(10);
    private int broadcasting = 0;
    private int animationInProgressPointer = 1;
    HashSet<Integer> heavyOperationsCounter = new HashSet<>();
    private final SparseArray<AllowedNotifications> allowedNotifications = new SparseArray<>();
    SparseArray<Runnable> alreadyPostedRunnubles = new SparseArray<>();

    public class AnonymousClass1 implements View.OnAttachStateChangeListener {
        final NotificationCenterDelegate val$delegate;
        final int val$id;

        AnonymousClass1(NotificationCenterDelegate notificationCenterDelegate, int i) {
            r2 = notificationCenterDelegate;
            r3 = i;
        }

        @Override
        public void onViewAttachedToWindow(View view) {
            NotificationCenter.getGlobalInstance().addObserver(r2, r3);
        }

        @Override
        public void onViewDetachedFromWindow(View view) {
            NotificationCenter.getGlobalInstance().removeObserver(r2, r3);
        }
    }

    public class AnonymousClass2 implements View.OnAttachStateChangeListener {
        final NotificationCenterDelegate val$delegate;
        final int val$id;

        AnonymousClass2(NotificationCenterDelegate notificationCenterDelegate, int i) {
            r2 = notificationCenterDelegate;
            r3 = i;
        }

        @Override
        public void onViewAttachedToWindow(View view) {
            NotificationCenter.this.addObserver(r2, r3);
        }

        @Override
        public void onViewDetachedFromWindow(View view) {
            NotificationCenter.this.removeObserver(r2, r3);
        }
    }

    public static class AllowedNotifications {
        int[] allowedIds;
        final long time;

        private AllowedNotifications() {
            this.time = SystemClock.elapsedRealtime();
        }

        AllowedNotifications(AnonymousClass1 anonymousClass1) {
            this();
        }
    }

    public static class DelayedPost {
        private Object[] args;
        private int id;

        private DelayedPost(int i, Object[] objArr) {
            this.id = i;
            this.args = objArr;
        }

        DelayedPost(int i, Object[] objArr, AnonymousClass1 anonymousClass1) {
            this(i, objArr);
        }
    }

    public interface NotificationCenterDelegate {
        void didReceivedNotification(int i, int i2, Object... objArr);
    }

    public interface PostponeNotificationCallback {
        boolean needPostpone(int i, int i2, Object[] objArr);
    }

    public class UniqArrayList<T> extends ArrayList<T> {
        HashSet<T> set;

        private UniqArrayList() {
            this.set = new HashSet<>();
        }

        UniqArrayList(NotificationCenter notificationCenter, AnonymousClass1 anonymousClass1) {
            this();
        }

        @Override
        public void add(int i, T t) {
            if (this.set.add(t)) {
                super.add(i, t);
            }
        }

        @Override
        public boolean add(T t) {
            if (this.set.add(t)) {
                return super.add(t);
            }
            return false;
        }

        @Override
        public boolean addAll(int i, Collection<? extends T> collection) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean addAll(Collection<? extends T> collection) {
            Iterator<? extends T> it = collection.iterator();
            boolean z = false;
            while (it.hasNext()) {
                if (add(it.next())) {
                    z = true;
                }
            }
            return z;
        }

        @Override
        public void clear() {
            this.set.clear();
            super.clear();
        }

        @Override
        public boolean contains(Object obj) {
            return this.set.contains(obj);
        }

        @Override
        public T remove(int i) {
            T t = (T) super.remove(i);
            if (t != null) {
                this.set.remove(t);
            }
            return t;
        }

        @Override
        public boolean remove(Object obj) {
            if (this.set.remove(obj)) {
                return super.remove(obj);
            }
            return false;
        }

        @Override
        public boolean removeAll(Collection<?> collection) {
            throw new UnsupportedOperationException();
        }
    }

    public NotificationCenter(int i) {
        this.currentAccount = i;
    }

    public void checkForExpiredNotifications() {
        ArrayList arrayList = null;
        this.checkForExpiredNotifications = null;
        if (this.allowedNotifications.size() == 0) {
            return;
        }
        long elapsedRealtime = SystemClock.elapsedRealtime();
        long j = Long.MAX_VALUE;
        for (int i = 0; i < this.allowedNotifications.size(); i++) {
            long j2 = this.allowedNotifications.valueAt(i).time;
            if (elapsedRealtime - j2 > 1000) {
                if (arrayList == null) {
                    arrayList = new ArrayList();
                }
                arrayList.add(Integer.valueOf(this.allowedNotifications.keyAt(i)));
            } else {
                j = Math.min(j2, j);
            }
        }
        if (arrayList != null) {
            for (int i2 = 0; i2 < arrayList.size(); i2++) {
                onAnimationFinish(((Integer) arrayList.get(i2)).intValue());
            }
        }
        if (j != Long.MAX_VALUE) {
            AndroidUtilities.runOnUIThread(new Runnable() {
                @Override
                public final void run() {
                    NotificationCenter.this.lambda$checkForExpiredNotifications$0();
                }
            }, Math.max(17L, 5017 - (elapsedRealtime - j)));
        }
    }

    private ArrayList<NotificationCenterDelegate> createArrayForId(int i) {
        return (i == didReplacedPhotoInMemCache || i == stopAllHeavyOperations || i == startAllHeavyOperations) ? new UniqArrayList() : new ArrayList<>();
    }

    public static NotificationCenter getGlobalInstance() {
        NotificationCenter notificationCenter = globalInstance;
        if (notificationCenter == null) {
            synchronized (NotificationCenter.class) {
                try {
                    notificationCenter = globalInstance;
                    if (notificationCenter == null) {
                        notificationCenter = new NotificationCenter(-1);
                        globalInstance = notificationCenter;
                    }
                } finally {
                }
            }
        }
        return notificationCenter;
    }

    public static NotificationCenter getInstance(int i) {
        NotificationCenter notificationCenter = Instance[i];
        if (notificationCenter == null) {
            synchronized (NotificationCenter.class) {
                try {
                    notificationCenter = Instance[i];
                    if (notificationCenter == null) {
                        NotificationCenter[] notificationCenterArr = Instance;
                        NotificationCenter notificationCenter2 = new NotificationCenter(i);
                        notificationCenterArr[i] = notificationCenter2;
                        notificationCenter = notificationCenter2;
                    }
                } finally {
                }
            }
        }
        return notificationCenter;
    }

    public void lambda$checkForExpiredNotifications$0() {
        this.checkForExpiredNotifications = new NotificationCenter$$ExternalSyntheticLambda9(this);
    }

    public static void lambda$listen$6() {
    }

    public static void lambda$listen$7(int i, Utilities.Callback callback, int i2, int i3, Object[] objArr) {
        if (i2 == i) {
            callback.run(objArr);
        }
    }

    public void lambda$listen$8(View view, View.OnAttachStateChangeListener onAttachStateChangeListener, NotificationCenterDelegate notificationCenterDelegate, int i) {
        view.removeOnAttachStateChangeListener(onAttachStateChangeListener);
        removeObserver(notificationCenterDelegate, i);
    }

    public static void lambda$listenGlobal$3() {
    }

    public static void lambda$listenGlobal$4(int i, Utilities.Callback callback, int i2, int i3, Object[] objArr) {
        if (i2 == i) {
            callback.run(objArr);
        }
    }

    public static void lambda$listenGlobal$5(View view, View.OnAttachStateChangeListener onAttachStateChangeListener, NotificationCenterDelegate notificationCenterDelegate, int i) {
        view.removeOnAttachStateChangeListener(onAttachStateChangeListener);
        getGlobalInstance().removeObserver(notificationCenterDelegate, i);
    }

    public void lambda$listenOnce$10(int i, NotificationCenterDelegate[] notificationCenterDelegateArr, Runnable runnable, int i2, int i3, Object[] objArr) {
        if (i2 != i || notificationCenterDelegateArr[0] == null) {
            return;
        }
        if (runnable != null) {
            runnable.run();
        }
        removeObserver(notificationCenterDelegateArr[0], i);
        notificationCenterDelegateArr[0] = null;
    }

    public void lambda$postNotificationDebounced$2(int i, Object[] objArr, int i2) {
        postNotificationNameInternal(i, false, objArr);
        this.alreadyPostedRunnubles.remove(i2);
    }

    public static void listenEmojiLoading(final View view) {
        getGlobalInstance().listenGlobal(view, emojiLoaded, new Utilities.Callback() {
            @Override
            public final void run(Object obj) {
                view.invalidate();
            }
        });
    }

    private void postNotificationDebounced(final int i, final Object[] objArr) {
        final int hashCode = (Arrays.hashCode(objArr) << 16) + i;
        if (this.alreadyPostedRunnubles.indexOfKey(hashCode) >= 0) {
            return;
        }
        Runnable runnable = new Runnable() {
            @Override
            public final void run() {
                NotificationCenter.this.lambda$postNotificationDebounced$2(i, objArr, hashCode);
            }
        };
        this.alreadyPostedRunnubles.put(hashCode, runnable);
        AndroidUtilities.runOnUIThread(runnable, 250L);
    }

    private boolean shouldDebounce(int i, Object[] objArr) {
        return i == updateInterfaces;
    }

    public void addObserver(NotificationCenterDelegate notificationCenterDelegate, int i) {
        if (BuildVars.DEBUG_VERSION && Thread.currentThread() != ApplicationLoader.applicationHandler.getLooper().getThread()) {
            throw new RuntimeException("addObserver allowed only from MAIN thread");
        }
        if (this.broadcasting != 0) {
            ArrayList<NotificationCenterDelegate> arrayList = this.addAfterBroadcast.get(i);
            if (arrayList == null) {
                arrayList = new ArrayList<>();
                this.addAfterBroadcast.put(i, arrayList);
            }
            arrayList.add(notificationCenterDelegate);
            return;
        }
        ArrayList<NotificationCenterDelegate> arrayList2 = this.observers.get(i);
        if (arrayList2 == null) {
            SparseArray<ArrayList<NotificationCenterDelegate>> sparseArray = this.observers;
            ArrayList<NotificationCenterDelegate> createArrayForId = createArrayForId(i);
            sparseArray.put(i, createArrayForId);
            arrayList2 = createArrayForId;
        }
        if (arrayList2.contains(notificationCenterDelegate)) {
            return;
        }
        arrayList2.add(notificationCenterDelegate);
        if (!BuildVars.DEBUG_VERSION || alreadyLogged || arrayList2.size() <= 1000) {
            return;
        }
        alreadyLogged = true;
        FileLog.e((Throwable) new RuntimeException("Total observers more than 1000, need check for memory leak. " + i), true);
    }

    public void addPostponeNotificationsCallback(PostponeNotificationCallback postponeNotificationCallback) {
        if (BuildVars.DEBUG_VERSION && Thread.currentThread() != ApplicationLoader.applicationHandler.getLooper().getThread()) {
            throw new RuntimeException("PostponeNotificationsCallback allowed only from MAIN thread");
        }
        if (this.postponeCallbackList.contains(postponeNotificationCallback)) {
            return;
        }
        this.postponeCallbackList.add(postponeNotificationCallback);
    }

    public void doOnIdle(Runnable runnable) {
        if (isAnimationInProgress()) {
            this.delayedRunnables.add(runnable);
        } else {
            runnable.run();
        }
    }

    public int getCurrentHeavyOperationFlags() {
        return this.currentHeavyOperationFlags;
    }

    public ArrayList<NotificationCenterDelegate> getObservers(int i) {
        return this.observers.get(i);
    }

    public boolean hasObservers(int i) {
        return this.observers.indexOfKey(i) >= 0;
    }

    public boolean isAnimationInProgress() {
        return this.animationInProgressCount > 0;
    }

    public Runnable listen(final View view, final int i, final Utilities.Callback<Object[]> callback) {
        if (view == null || callback == null) {
            return new Runnable() {
                @Override
                public final void run() {
                    NotificationCenter.lambda$listen$6();
                }
            };
        }
        final NotificationCenterDelegate notificationCenterDelegate = new NotificationCenterDelegate() {
            @Override
            public final void didReceivedNotification(int i2, int i3, Object[] objArr) {
                NotificationCenter.lambda$listen$7(i, callback, i2, i3, objArr);
            }
        };
        final AnonymousClass2 anonymousClass2 = new View.OnAttachStateChangeListener() {
            final NotificationCenterDelegate val$delegate;
            final int val$id;

            AnonymousClass2(final NotificationCenterDelegate notificationCenterDelegate2, final int i2) {
                r2 = notificationCenterDelegate2;
                r3 = i2;
            }

            @Override
            public void onViewAttachedToWindow(View view2) {
                NotificationCenter.this.addObserver(r2, r3);
            }

            @Override
            public void onViewDetachedFromWindow(View view2) {
                NotificationCenter.this.removeObserver(r2, r3);
            }
        };
        view.addOnAttachStateChangeListener(anonymousClass2);
        return new Runnable() {
            @Override
            public final void run() {
                NotificationCenter.this.lambda$listen$8(view, anonymousClass2, notificationCenterDelegate2, i2);
            }
        };
    }

    public Runnable listenGlobal(final View view, final int i, final Utilities.Callback<Object[]> callback) {
        if (view == null || callback == null) {
            return new Runnable() {
                @Override
                public final void run() {
                    NotificationCenter.lambda$listenGlobal$3();
                }
            };
        }
        final NotificationCenterDelegate notificationCenterDelegate = new NotificationCenterDelegate() {
            @Override
            public final void didReceivedNotification(int i2, int i3, Object[] objArr) {
                NotificationCenter.lambda$listenGlobal$4(i, callback, i2, i3, objArr);
            }
        };
        final AnonymousClass1 anonymousClass1 = new View.OnAttachStateChangeListener() {
            final NotificationCenterDelegate val$delegate;
            final int val$id;

            AnonymousClass1(final NotificationCenterDelegate notificationCenterDelegate2, final int i2) {
                r2 = notificationCenterDelegate2;
                r3 = i2;
            }

            @Override
            public void onViewAttachedToWindow(View view2) {
                NotificationCenter.getGlobalInstance().addObserver(r2, r3);
            }

            @Override
            public void onViewDetachedFromWindow(View view2) {
                NotificationCenter.getGlobalInstance().removeObserver(r2, r3);
            }
        };
        view.addOnAttachStateChangeListener(anonymousClass1);
        return new Runnable() {
            @Override
            public final void run() {
                NotificationCenter.lambda$listenGlobal$5(view, anonymousClass1, notificationCenterDelegate2, i2);
            }
        };
    }

    public void listenOnce(final int i, final Runnable runnable) {
        NotificationCenterDelegate notificationCenterDelegate = new NotificationCenterDelegate() {
            @Override
            public final void didReceivedNotification(int i2, int i3, Object[] objArr) {
                NotificationCenter.this.lambda$listenOnce$10(i, r3, runnable, i2, i3, objArr);
            }
        };
        final NotificationCenterDelegate[] notificationCenterDelegateArr = {notificationCenterDelegate};
        addObserver(notificationCenterDelegate, i);
    }

    public void onAnimationFinish(int i) {
        AllowedNotifications allowedNotifications = this.allowedNotifications.get(i);
        this.allowedNotifications.delete(i);
        if (allowedNotifications != null) {
            this.animationInProgressCount--;
            if (!this.heavyOperationsCounter.isEmpty()) {
                this.heavyOperationsCounter.remove(Integer.valueOf(i));
                if (this.heavyOperationsCounter.isEmpty()) {
                    getGlobalInstance().lambda$postNotificationNameOnUIThread$1(startAllHeavyOperations, 512);
                }
            }
            if (this.animationInProgressCount == 0) {
                runDelayedNotifications();
            }
        }
        if (this.checkForExpiredNotifications == null || this.allowedNotifications.size() != 0) {
            return;
        }
        AndroidUtilities.cancelRunOnUIThread(this.checkForExpiredNotifications);
        this.checkForExpiredNotifications = null;
    }

    public void lambda$postNotificationNameOnUIThread$1(int r18, java.lang.Object... r19) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.NotificationCenter.lambda$postNotificationNameOnUIThread$1(int, java.lang.Object[]):void");
    }

    public void postNotificationNameInternal(int i, boolean z, Object... objArr) {
        if (BuildVars.DEBUG_VERSION && Thread.currentThread() != ApplicationLoader.applicationHandler.getLooper().getThread()) {
            throw new RuntimeException("postNotificationName allowed only from MAIN thread");
        }
        if (!z && isAnimationInProgress()) {
            this.delayedPosts.add(new DelayedPost(i, objArr));
            return;
        }
        if (!this.postponeCallbackList.isEmpty()) {
            for (int i2 = 0; i2 < this.postponeCallbackList.size(); i2++) {
                if (this.postponeCallbackList.get(i2).needPostpone(i, this.currentAccount, objArr)) {
                    this.delayedPosts.add(new DelayedPost(i, objArr));
                    return;
                }
            }
        }
        this.broadcasting++;
        ArrayList<NotificationCenterDelegate> arrayList = this.observers.get(i);
        if (arrayList != null && !arrayList.isEmpty()) {
            for (int i3 = 0; i3 < arrayList.size(); i3++) {
                arrayList.get(i3).didReceivedNotification(i, this.currentAccount, objArr);
            }
        }
        int i4 = this.broadcasting - 1;
        this.broadcasting = i4;
        if (i4 == 0) {
            if (this.removeAfterBroadcast.size() != 0) {
                for (int i5 = 0; i5 < this.removeAfterBroadcast.size(); i5++) {
                    int keyAt = this.removeAfterBroadcast.keyAt(i5);
                    ArrayList<NotificationCenterDelegate> arrayList2 = this.removeAfterBroadcast.get(keyAt);
                    for (int i6 = 0; i6 < arrayList2.size(); i6++) {
                        removeObserver(arrayList2.get(i6), keyAt);
                    }
                }
                this.removeAfterBroadcast.clear();
            }
            if (this.addAfterBroadcast.size() != 0) {
                for (int i7 = 0; i7 < this.addAfterBroadcast.size(); i7++) {
                    int keyAt2 = this.addAfterBroadcast.keyAt(i7);
                    ArrayList<NotificationCenterDelegate> arrayList3 = this.addAfterBroadcast.get(keyAt2);
                    for (int i8 = 0; i8 < arrayList3.size(); i8++) {
                        addObserver(arrayList3.get(i8), keyAt2);
                    }
                }
                this.addAfterBroadcast.clear();
            }
        }
    }

    public void postNotificationNameOnUIThread(final int i, final Object... objArr) {
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                NotificationCenter.this.lambda$postNotificationNameOnUIThread$1(i, objArr);
            }
        });
    }

    public void removeDelayed(Runnable runnable) {
        this.delayedRunnables.remove(runnable);
    }

    public void removeObserver(NotificationCenterDelegate notificationCenterDelegate, int i) {
        if (BuildVars.DEBUG_VERSION && Thread.currentThread() != ApplicationLoader.applicationHandler.getLooper().getThread()) {
            throw new RuntimeException("removeObserver allowed only from MAIN thread");
        }
        if (this.broadcasting == 0) {
            ArrayList<NotificationCenterDelegate> arrayList = this.observers.get(i);
            if (arrayList != null) {
                arrayList.remove(notificationCenterDelegate);
                return;
            }
            return;
        }
        ArrayList<NotificationCenterDelegate> arrayList2 = this.removeAfterBroadcast.get(i);
        if (arrayList2 == null) {
            arrayList2 = new ArrayList<>();
            this.removeAfterBroadcast.put(i, arrayList2);
        }
        arrayList2.add(notificationCenterDelegate);
    }

    public void removePostponeNotificationsCallback(PostponeNotificationCallback postponeNotificationCallback) {
        if (BuildVars.DEBUG_VERSION && Thread.currentThread() != ApplicationLoader.applicationHandler.getLooper().getThread()) {
            throw new RuntimeException("removePostponeNotificationsCallback allowed only from MAIN thread");
        }
        if (this.postponeCallbackList.remove(postponeNotificationCallback)) {
            runDelayedNotifications();
        }
    }

    public void runDelayedNotifications() {
        if (!this.delayedPosts.isEmpty()) {
            this.delayedPostsTmp.clear();
            this.delayedPostsTmp.addAll(this.delayedPosts);
            this.delayedPosts.clear();
            for (int i = 0; i < this.delayedPostsTmp.size(); i++) {
                DelayedPost delayedPost = this.delayedPostsTmp.get(i);
                postNotificationNameInternal(delayedPost.id, true, delayedPost.args);
            }
            this.delayedPostsTmp.clear();
        }
        if (this.delayedRunnables.isEmpty()) {
            return;
        }
        this.delayedRunnablesTmp.clear();
        this.delayedRunnablesTmp.addAll(this.delayedRunnables);
        this.delayedRunnables.clear();
        for (int i2 = 0; i2 < this.delayedRunnablesTmp.size(); i2++) {
            AndroidUtilities.runOnUIThread(this.delayedRunnablesTmp.get(i2));
        }
        this.delayedRunnablesTmp.clear();
    }

    public int setAnimationInProgress(int i, int[] iArr) {
        return setAnimationInProgress(i, iArr, true);
    }

    public int setAnimationInProgress(int i, int[] iArr, boolean z) {
        onAnimationFinish(i);
        if (this.heavyOperationsCounter.isEmpty() && z) {
            getGlobalInstance().lambda$postNotificationNameOnUIThread$1(stopAllHeavyOperations, 512);
        }
        this.animationInProgressCount++;
        int i2 = this.animationInProgressPointer + 1;
        this.animationInProgressPointer = i2;
        if (z) {
            this.heavyOperationsCounter.add(Integer.valueOf(i2));
        }
        AllowedNotifications allowedNotifications = new AllowedNotifications();
        allowedNotifications.allowedIds = iArr;
        this.allowedNotifications.put(this.animationInProgressPointer, allowedNotifications);
        if (this.checkForExpiredNotifications == null) {
            NotificationCenter$$ExternalSyntheticLambda9 notificationCenter$$ExternalSyntheticLambda9 = new NotificationCenter$$ExternalSyntheticLambda9(this);
            this.checkForExpiredNotifications = notificationCenter$$ExternalSyntheticLambda9;
            AndroidUtilities.runOnUIThread(notificationCenter$$ExternalSyntheticLambda9, 5017L);
        }
        return this.animationInProgressPointer;
    }

    public void updateAllowedNotifications(int i, int[] iArr) {
        AllowedNotifications allowedNotifications = this.allowedNotifications.get(i);
        if (allowedNotifications != null) {
            allowedNotifications.allowedIds = iArr;
        }
    }
}
