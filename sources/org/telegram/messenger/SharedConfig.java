package org.telegram.messenger;

import android.app.ActivityManager;
import android.content.SharedPreferences;
import android.media.MediaCodecInfo;
import android.media.MediaCodecList;
import android.os.Build;
import android.os.Environment;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.Base64;
import android.webkit.WebView;
import androidx.core.content.pm.ShortcutManagerCompat;
import java.io.File;
import java.io.RandomAccessFile;
import java.io.UnsupportedEncodingException;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Locale;
import org.json.JSONObject;
import org.telegram.messenger.SharedConfig;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.SerializedData;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.LaunchActivity;

public class SharedConfig {
    private static final int[] LOW_SOC;
    public static final int PASSCODE_TYPE_PASSWORD = 1;
    public static final int PASSCODE_TYPE_PIN = 0;
    public static final int PERFORMANCE_CLASS_AVERAGE = 1;
    public static final int PERFORMANCE_CLASS_HIGH = 2;
    public static final int PERFORMANCE_CLASS_LOW = 0;
    private static final int PROXY_CURRENT_SCHEMA_VERSION = 2;
    private static final int PROXY_SCHEMA_V2 = 2;
    public static final int SAVE_TO_GALLERY_FLAG_CHANNELS = 4;
    public static final int SAVE_TO_GALLERY_FLAG_GROUP = 2;
    public static final int SAVE_TO_GALLERY_FLAG_PEER = 1;
    public static boolean adaptableColorInBrowser = false;
    public static boolean allowBigEmoji = false;
    static Boolean allowPreparingHevcPlayers = null;
    public static boolean allowScreenCapture = false;
    private static Boolean animationsEnabled = null;
    public static boolean appLocked = false;
    public static boolean archiveHidden = false;
    public static int autoLockIn = 0;
    public static int badPasscodeTries = 0;
    public static boolean bigCameraForRound = false;
    public static int bubbleRadius = 0;
    public static int callEncryptionHintDisplayedCount = 0;
    public static boolean chatBubbles = false;
    private static int chatSwipeAction = 0;
    private static boolean configLoaded = false;
    public static ProxyInfo currentProxy = null;
    public static boolean customTabs = false;
    public static int dayNightThemeSwitchHintCount = 0;
    public static int dayNightWallpaperSwitchHint = 0;
    public static boolean debugVideoQualities = false;
    public static boolean debugWebView = false;
    private static int devicePerformanceClass = 0;
    public static boolean directShare = false;
    public static String directShareHash = null;
    public static boolean disableVoiceAudioEffects = false;
    public static int distanceSystemType = 0;
    public static boolean dontAskManageStorage = false;
    public static boolean drawActionBarShadow = false;
    public static boolean drawDialogIcons = false;
    public static int emojiInteractionsHintCount = 0;
    public static int fastScrollHintCount = 0;
    public static int fontSize = 0;
    public static boolean fontSizeIsDefault = false;
    public static boolean forceDisableTabletMode = false;
    public static boolean forwardingOptionsHintShown = false;
    private static String goodHevcEncoder = null;
    public static boolean hasCameraCache = false;
    public static boolean hasEmailLogin = false;
    private static HashSet<String> hevcEncoderWhitelist = null;
    public static boolean inappBrowser = false;
    public static boolean inappCamera = false;
    public static boolean isFloatingDebugActive = false;
    public static boolean isWaitingForPasscodeEnter = false;
    public static int ivFontSize = 0;
    public static int keepMedia = 0;
    public static int lastKeepMediaCheckTime = 0;
    private static int lastLocalId = 0;
    public static int lastLogsCheckTime = 0;
    public static int lastPauseTime = 0;
    public static long lastUpdateCheckTime = 0;
    public static long lastUptimeMillis = 0;
    private static int legacyDevicePerformanceClass = -1;
    public static LiteMode liteMode = null;
    private static final Object localIdSync;
    public static int lockRecordAudioVideoHint = 0;
    public static int mapPreviewType = 0;
    public static int mediaColumnsCount = 0;
    public static int messageSeenHintCount = 0;
    public static boolean multipleReactionsPromoShowed = false;
    public static boolean nextMediaTap = false;
    public static boolean noSoundHintShowed = false;
    public static final boolean noStatusBar = true;
    public static boolean noiseSupression;
    public static boolean onlyLocalInstantView;
    private static int overrideDevicePerformanceClass;
    public static String passcodeHash;
    public static long passcodeRetryInMs;
    public static byte[] passcodeSalt;
    public static int passcodeType;
    public static int passportConfigHash;
    private static String passportConfigJson;
    private static HashMap<String, String> passportConfigMap;
    public static boolean pauseMusicOnMedia;
    public static boolean pauseMusicOnRecord;
    public static boolean payByInvoice;
    public static TLRPC.TL_help_appUpdate pendingAppUpdate;
    public static int pendingAppUpdateBuildVersion;
    public static boolean photoViewerBlur;
    public static boolean playOrderReversed;
    public static ArrayList<ProxyInfo> proxyList;
    private static boolean proxyListLoaded;
    public static boolean proxyRotationEnabled;
    public static int proxyRotationTimeout;
    public static byte[] pushAuthKey;
    public static byte[] pushAuthKeyId;
    public static boolean pushStatSent;
    public static String pushString;
    public static long pushStringGetTimeEnd;
    public static long pushStringGetTimeStart;
    public static String pushStringStatus;
    public static int pushType;
    public static boolean raiseToListen;
    public static boolean raiseToSpeak;
    public static boolean readOnlyStorageDirAlertShowed;
    public static boolean recordViaSco;
    public static int repeatMode;
    public static boolean replyingOptionsHintShown;
    public static boolean roundCamera16to9;
    public static boolean saveIncomingPhotos;
    public static boolean saveStreamMedia;
    public static long scheduledHintSeenAt;
    public static int scheduledHintShows;
    public static long scheduledOrNoSoundHintSeenAt;
    public static int scheduledOrNoSoundHintShows;
    public static String searchEngineCustomURLAutocomplete;
    public static String searchEngineCustomURLQuery;
    public static int searchEngineType;
    public static boolean searchMessagesAsListUsed;
    public static boolean showNotificationsForAllAccounts;
    public static boolean shuffleMusic;
    public static boolean sortContactsByName;
    public static boolean sortFilesByName;
    public static int stealthModeSendMessageConfirm;
    public static boolean stickersReorderingHintUsed;
    public static String storageCacheDir;
    public static int storiesColumnsCount;
    public static boolean storiesIntroShown;
    public static boolean storyReactionsLongPressHint;
    public static boolean streamAllVideo;
    public static boolean streamMedia;
    public static boolean streamMkv;
    public static boolean suggestAnimatedEmoji;
    public static int suggestStickers;
    private static final Object sync;
    public static int textSelectionHintShows;
    public static boolean translateChats;
    public static boolean updateStickersOrderOnSend;
    public static Boolean useCamera2Force;
    public static boolean useFaceLock;
    public static boolean useFingerprintLock;
    public static boolean useNewBlur;
    public static boolean useSurfaceInStories;
    public static boolean useSystemEmoji;
    public static boolean useThreeLinesLayout;

    public static class BackgroundActivityPrefs {
        private static SharedPreferences prefs;

        public static int getDismissedCount() {
            return prefs.getInt("dismissed_count", 0);
        }

        public static long getLastCheckedBackgroundActivity() {
            return prefs.getLong("last_checked", 0L);
        }

        public static void increaseDismissedCount() {
            prefs.edit().putInt("dismissed_count", getDismissedCount() + 1).apply();
        }

        public static void setLastCheckedBackgroundActivity(long j) {
            prefs.edit().putLong("last_checked", j).apply();
        }
    }

    @Retention(RetentionPolicy.SOURCE)
    public @interface PasscodeType {
    }

    @Retention(RetentionPolicy.SOURCE)
    public @interface PerformanceClass {
    }

    public static class ProxyInfo {
        public String address;
        public boolean available;
        public long availableCheckTime;
        public boolean checking;
        public String password;
        public long ping;
        public int port;
        public long proxyCheckPingId;
        public String secret;
        public String username;

        public ProxyInfo(String str, int i, String str2, String str3, String str4) {
            this.address = str;
            this.port = i;
            this.username = str2;
            this.password = str3;
            this.secret = str4;
            if (str == null) {
                this.address = "";
            }
            if (str3 == null) {
                this.password = "";
            }
            if (str2 == null) {
                this.username = "";
            }
            if (str4 == null) {
                this.secret = "";
            }
        }

        public String getLink() {
            StringBuilder sb = new StringBuilder(!TextUtils.isEmpty(this.secret) ? "https://t.me/proxy?" : "https://t.me/socks?");
            try {
                sb.append("server=");
                sb.append(URLEncoder.encode(this.address, "UTF-8"));
                sb.append("&");
                sb.append("port=");
                sb.append(this.port);
                if (!TextUtils.isEmpty(this.username)) {
                    sb.append("&user=");
                    sb.append(URLEncoder.encode(this.username, "UTF-8"));
                }
                if (!TextUtils.isEmpty(this.password)) {
                    sb.append("&pass=");
                    sb.append(URLEncoder.encode(this.password, "UTF-8"));
                }
                if (!TextUtils.isEmpty(this.secret)) {
                    sb.append("&secret=");
                    sb.append(URLEncoder.encode(this.secret, "UTF-8"));
                }
            } catch (UnsupportedEncodingException unused) {
            }
            return sb.toString();
        }
    }

    static {
        HashSet<String> hashSet = new HashSet<>();
        hevcEncoderWhitelist = hashSet;
        hashSet.add("c2.exynos.hevc.encoder");
        hevcEncoderWhitelist.add("OMX.Exynos.HEVC.Encoder".toLowerCase());
        pushType = 2;
        pushString = "";
        pushStringStatus = "";
        passcodeHash = "";
        passcodeSalt = new byte[0];
        autoLockIn = 3600;
        useFingerprintLock = true;
        useFaceLock = true;
        keepMedia = CacheByChatsController.KEEP_MEDIA_ONE_MONTH;
        updateStickersOrderOnSend = true;
        photoViewerBlur = true;
        stealthModeSendMessageConfirm = 2;
        lastLocalId = -210000;
        passportConfigJson = "";
        sync = new Object();
        localIdSync = new Object();
        mapPreviewType = 2;
        searchEngineType = 0;
        chatBubbles = Build.VERSION.SDK_INT >= 30;
        raiseToSpeak = false;
        raiseToListen = true;
        nextMediaTap = true;
        recordViaSco = false;
        customTabs = true;
        inappBrowser = true;
        adaptableColorInBrowser = true;
        onlyLocalInstantView = false;
        directShare = true;
        inappCamera = true;
        roundCamera16to9 = true;
        noSoundHintShowed = false;
        streamMedia = true;
        streamAllVideo = false;
        streamMkv = false;
        saveStreamMedia = true;
        pauseMusicOnRecord = false;
        pauseMusicOnMedia = false;
        showNotificationsForAllAccounts = true;
        debugVideoQualities = false;
        fontSize = 16;
        bubbleRadius = 17;
        ivFontSize = 16;
        mediaColumnsCount = 3;
        storiesColumnsCount = 3;
        fastScrollHintCount = 3;
        translateChats = true;
        LOW_SOC = new int[]{-1775228513, 802464304, 802464333, 802464302, 2067362118, 2067362060, 2067362084, 2067362241, 2067362117, 2067361998, -1853602818};
        loadConfig();
        proxyList = new ArrayList<>();
        drawActionBarShadow = true;
    }

    public static ProxyInfo addProxy(ProxyInfo proxyInfo) {
        loadProxyList();
        int size = proxyList.size();
        for (int i = 0; i < size; i++) {
            ProxyInfo proxyInfo2 = proxyList.get(i);
            if (proxyInfo.address.equals(proxyInfo2.address) && proxyInfo.port == proxyInfo2.port && proxyInfo.username.equals(proxyInfo2.username) && proxyInfo.password.equals(proxyInfo2.password) && proxyInfo.secret.equals(proxyInfo2.secret)) {
                return proxyInfo2;
            }
        }
        proxyList.add(0, proxyInfo);
        saveProxyList();
        return proxyInfo;
    }

    public static boolean allowPreparingHevcPlayers() {
        int maxSupportedInstances;
        if (Build.VERSION.SDK_INT < 23) {
            return false;
        }
        if (allowPreparingHevcPlayers == null) {
            int codecCount = MediaCodecList.getCodecCount();
            int i = 0;
            for (int i2 = 0; i2 < codecCount; i2++) {
                MediaCodecInfo codecInfoAt = MediaCodecList.getCodecInfoAt(i2);
                if (!codecInfoAt.isEncoder()) {
                    int i3 = 0;
                    while (true) {
                        if (i3 >= codecInfoAt.getSupportedTypes().length) {
                            break;
                        }
                        if (codecInfoAt.getSupportedTypes()[i3].contains("video/hevc")) {
                            maxSupportedInstances = codecInfoAt.getCapabilitiesForType("video/hevc").getMaxSupportedInstances();
                            if (maxSupportedInstances > i) {
                                i = maxSupportedInstances;
                            }
                        } else {
                            i3++;
                        }
                    }
                }
            }
            allowPreparingHevcPlayers = Boolean.valueOf(i >= 8);
        }
        return allowPreparingHevcPlayers.booleanValue();
    }

    public static boolean animationsEnabled() {
        if (animationsEnabled == null) {
            animationsEnabled = Boolean.valueOf(MessagesController.getGlobalMainSettings().getBoolean("view_animations", true));
        }
        return animationsEnabled.booleanValue();
    }

    public static int buildVersion() {
        try {
            return ApplicationLoader.applicationContext.getPackageManager().getPackageInfo(ApplicationLoader.applicationContext.getPackageName(), 0).versionCode;
        } catch (Exception e) {
            FileLog.e(e);
            return 0;
        }
    }

    public static boolean canBlurChat() {
        return getDevicePerformanceClass() >= (Build.VERSION.SDK_INT >= 31 ? 1 : 2) || BuildVars.DEBUG_PRIVATE_VERSION;
    }

    public static boolean chatBlurEnabled() {
        return canBlurChat() && LiteMode.isEnabled(256);
    }

    public static void checkLogsToDelete() {
        if (BuildVars.LOGS_ENABLED) {
            final int currentTimeMillis = (int) (System.currentTimeMillis() / 1000);
            if (Math.abs(currentTimeMillis - lastLogsCheckTime) < 3600) {
                return;
            }
            lastLogsCheckTime = currentTimeMillis;
            Utilities.cacheClearQueue.postRunnable(new Runnable() {
                @Override
                public final void run() {
                    SharedConfig.lambda$checkLogsToDelete$3(currentTimeMillis);
                }
            });
        }
    }

    public static boolean checkPasscode(String str) {
        if (passcodeSalt.length != 0) {
            try {
                byte[] bytes = str.getBytes("UTF-8");
                int length = bytes.length + 32;
                byte[] bArr = new byte[length];
                System.arraycopy(passcodeSalt, 0, bArr, 0, 16);
                System.arraycopy(bytes, 0, bArr, 16, bytes.length);
                System.arraycopy(passcodeSalt, 0, bArr, bytes.length + 16, 16);
                return passcodeHash.equals(Utilities.bytesToHex(Utilities.computeSHA256(bArr, 0, length)));
            } catch (Exception e) {
                FileLog.e(e);
                return false;
            }
        }
        boolean equals = Utilities.MD5(str).equals(passcodeHash);
        if (equals) {
            try {
                passcodeSalt = new byte[16];
                Utilities.random.nextBytes(passcodeSalt);
                byte[] bytes2 = str.getBytes("UTF-8");
                int length2 = bytes2.length + 32;
                byte[] bArr2 = new byte[length2];
                System.arraycopy(passcodeSalt, 0, bArr2, 0, 16);
                System.arraycopy(bytes2, 0, bArr2, 16, bytes2.length);
                System.arraycopy(passcodeSalt, 0, bArr2, bytes2.length + 16, 16);
                passcodeHash = Utilities.bytesToHex(Utilities.computeSHA256(bArr2, 0, length2));
                saveConfig();
            } catch (Exception e2) {
                FileLog.e(e2);
            }
        }
        return equals;
    }

    public static void checkSaveToGalleryFiles() {
        Utilities.globalQueue.postRunnable(new Runnable() {
            @Override
            public final void run() {
                SharedConfig.lambda$checkSaveToGalleryFiles$5();
            }
        });
    }

    public static void checkSdCard(File file) {
        if (file == null || storageCacheDir == null || readOnlyStorageDirAlertShowed || !file.getPath().startsWith(storageCacheDir)) {
            return;
        }
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                SharedConfig.lambda$checkSdCard$2();
            }
        });
    }

    public static void clearConfig() {
        saveIncomingPhotos = false;
        appLocked = false;
        passcodeType = 0;
        passcodeRetryInMs = 0L;
        lastUptimeMillis = 0L;
        badPasscodeTries = 0;
        passcodeHash = "";
        passcodeSalt = new byte[0];
        autoLockIn = 3600;
        lastPauseTime = 0;
        useFingerprintLock = true;
        isWaitingForPasscodeEnter = false;
        allowScreenCapture = false;
        textSelectionHintShows = 0;
        scheduledOrNoSoundHintShows = 0;
        scheduledOrNoSoundHintSeenAt = 0L;
        scheduledHintShows = 0;
        scheduledHintSeenAt = 0L;
        lockRecordAudioVideoHint = 0;
        forwardingOptionsHintShown = false;
        replyingOptionsHintShown = false;
        messageSeenHintCount = 3;
        emojiInteractionsHintCount = 3;
        dayNightThemeSwitchHintCount = 3;
        stealthModeSendMessageConfirm = 2;
        dayNightWallpaperSwitchHint = 0;
        saveConfig();
    }

    public static void deleteProxy(ProxyInfo proxyInfo) {
        if (currentProxy == proxyInfo) {
            currentProxy = null;
            SharedPreferences globalMainSettings = MessagesController.getGlobalMainSettings();
            boolean z = globalMainSettings.getBoolean("proxy_enabled", false);
            SharedPreferences.Editor edit = globalMainSettings.edit();
            edit.putString("proxy_ip", "");
            edit.putString("proxy_pass", "");
            edit.putString("proxy_user", "");
            edit.putString("proxy_secret", "");
            edit.putInt("proxy_port", 1080);
            edit.putBoolean("proxy_enabled", false);
            edit.putBoolean("proxy_enabled_calls", false);
            edit.apply();
            if (z) {
                ConnectionsManager.setProxySettings(false, "", 0, "", "", "");
            }
        }
        proxyList.remove(proxyInfo);
        saveProxyList();
    }

    public static boolean deviceIsAboveAverage() {
        return getDevicePerformanceClass() >= 1;
    }

    public static boolean deviceIsAverage() {
        return getDevicePerformanceClass() <= 1;
    }

    public static boolean deviceIsHigh() {
        return getDevicePerformanceClass() >= 2;
    }

    public static boolean deviceIsLow() {
        return getDevicePerformanceClass() == 0;
    }

    public static boolean enabledRaiseTo(boolean z) {
        return raiseToListen && (!z || raiseToSpeak);
    }

    public static String findGoodHevcEncoder() {
        boolean isHardwareAccelerated;
        if (goodHevcEncoder == null) {
            int codecCount = MediaCodecList.getCodecCount();
            for (int i = 0; i < codecCount; i++) {
                MediaCodecInfo codecInfoAt = MediaCodecList.getCodecInfoAt(i);
                if (codecInfoAt.isEncoder()) {
                    for (int i2 = 0; i2 < codecInfoAt.getSupportedTypes().length; i2++) {
                        if (codecInfoAt.getSupportedTypes()[i2].contains("video/hevc")) {
                            isHardwareAccelerated = codecInfoAt.isHardwareAccelerated();
                            if (isHardwareAccelerated && isWhitelisted(codecInfoAt)) {
                                String name = codecInfoAt.getName();
                                goodHevcEncoder = name;
                                return name;
                            }
                        }
                    }
                }
            }
            goodHevcEncoder = "";
        }
        if (TextUtils.isEmpty(goodHevcEncoder)) {
            return null;
        }
        return goodHevcEncoder;
    }

    public static void forwardingOptionsHintHintShowed() {
        SharedPreferences.Editor edit = MessagesController.getGlobalMainSettings().edit();
        forwardingOptionsHintShown = true;
        edit.putBoolean("forwardingOptionsHintShown", true);
        edit.apply();
    }

    public static int getChatSwipeAction(int i) {
        int i2 = chatSwipeAction;
        if (i2 < 0) {
            return !MessagesController.getInstance(i).dialogFilters.isEmpty() ? 5 : 2;
        }
        if (i2 == 5 && MessagesController.getInstance(i).dialogFilters.isEmpty()) {
            return 2;
        }
        return chatSwipeAction;
    }

    public static HashMap<String, String> getCountryLangs() {
        if (passportConfigMap == null) {
            passportConfigMap = new HashMap<>();
            try {
                JSONObject jSONObject = new JSONObject(passportConfigJson);
                Iterator<String> keys = jSONObject.keys();
                while (keys.hasNext()) {
                    String next = keys.next();
                    passportConfigMap.put(next.toUpperCase(), jSONObject.getString(next).toUpperCase());
                }
            } catch (Throwable th) {
                FileLog.e(th);
            }
        }
        return passportConfigMap;
    }

    public static int getDevicePerformanceClass() {
        int i = overrideDevicePerformanceClass;
        if (i != -1) {
            return i;
        }
        if (devicePerformanceClass == -1) {
            devicePerformanceClass = measureDevicePerformanceClass();
        }
        return devicePerformanceClass;
    }

    public static int getLastLocalId() {
        int i;
        synchronized (localIdSync) {
            i = lastLocalId;
            lastLocalId = i - 1;
        }
        return i;
    }

    @Deprecated
    public static int getLegacyDevicePerformanceClass() {
        if (legacyDevicePerformanceClass == -1) {
            int i = Build.VERSION.SDK_INT;
            int i2 = ConnectionsManager.CPU_COUNT;
            int memoryClass = ((ActivityManager) ApplicationLoader.applicationContext.getSystemService("activity")).getMemoryClass();
            int i3 = 0;
            int i4 = 0;
            for (int i5 = 0; i5 < i2; i5++) {
                try {
                    RandomAccessFile randomAccessFile = new RandomAccessFile(String.format(Locale.ENGLISH, "/sys/devices/system/cpu/cpu%d/cpufreq/cpuinfo_max_freq", Integer.valueOf(i5)), "r");
                    String readLine = randomAccessFile.readLine();
                    if (readLine != null) {
                        i4 += Utilities.parseInt((CharSequence) readLine).intValue() / 1000;
                        i3++;
                    }
                    randomAccessFile.close();
                } catch (Throwable unused) {
                }
            }
            int ceil = i3 == 0 ? -1 : (int) Math.ceil(i4 / i3);
            if (i < 21 || i2 <= 2 || memoryClass <= 100 || ((i2 <= 4 && ceil != -1 && ceil <= 1250) || ((i2 <= 4 && ceil <= 1600 && memoryClass <= 128 && i <= 21) || (i2 <= 4 && ceil <= 1300 && memoryClass <= 128 && i <= 24)))) {
                legacyDevicePerformanceClass = 0;
            } else if (i2 < 8 || memoryClass <= 160 || ((ceil != -1 && ceil <= 2050) || (ceil == -1 && i2 == 8 && i <= 23))) {
                legacyDevicePerformanceClass = 1;
            } else {
                legacyDevicePerformanceClass = 2;
            }
        }
        return legacyDevicePerformanceClass;
    }

    public static SharedPreferences getPreferences() {
        return ApplicationLoader.applicationContext.getSharedPreferences("userconfing", 0);
    }

    public static void increaseBadPasscodeTries() {
        int i = badPasscodeTries + 1;
        badPasscodeTries = i;
        if (i >= 3) {
            passcodeRetryInMs = i != 3 ? i != 4 ? i != 5 ? i != 6 ? i != 7 ? 30000L : 25000L : 20000L : 15000L : 10000L : 5000L;
            lastUptimeMillis = SystemClock.elapsedRealtime();
        }
        saveConfig();
    }

    public static void increaseDayNightWallpaperSiwtchHint() {
        SharedPreferences.Editor edit = MessagesController.getGlobalMainSettings().edit();
        int i = dayNightWallpaperSwitchHint + 1;
        dayNightWallpaperSwitchHint = i;
        edit.putInt("dayNightWallpaperSwitchHint", i);
        edit.apply();
    }

    public static void increaseLockRecordAudioVideoHintShowed() {
        SharedPreferences.Editor edit = MessagesController.getGlobalMainSettings().edit();
        int i = lockRecordAudioVideoHint + 1;
        lockRecordAudioVideoHint = i;
        edit.putInt("lockRecordAudioVideoHint", i);
        edit.apply();
    }

    public static void increaseScheduledHintShowed() {
        SharedPreferences.Editor edit = MessagesController.getGlobalMainSettings().edit();
        scheduledHintSeenAt = System.currentTimeMillis();
        int i = scheduledHintShows + 1;
        scheduledHintShows = i;
        edit.putInt("scheduledHintShows", i);
        edit.putLong("scheduledHintSeenAt", scheduledHintSeenAt);
        edit.apply();
    }

    public static void increaseScheduledOrNoSoundHintShowed() {
        SharedPreferences.Editor edit = MessagesController.getGlobalMainSettings().edit();
        scheduledOrNoSoundHintSeenAt = System.currentTimeMillis();
        int i = scheduledOrNoSoundHintShows + 1;
        scheduledOrNoSoundHintShows = i;
        edit.putInt("scheduledOrNoSoundHintShows", i);
        edit.putLong("scheduledOrNoSoundHintSeenAt", scheduledOrNoSoundHintSeenAt);
        edit.apply();
    }

    public static void increaseTextSelectionHintShowed() {
        SharedPreferences.Editor edit = MessagesController.getGlobalMainSettings().edit();
        int i = textSelectionHintShows + 1;
        textSelectionHintShows = i;
        edit.putInt("textSelectionHintShows", i);
        edit.apply();
    }

    public static void incrementCallEncryptionHintDisplayed(int i) {
        callEncryptionHintDisplayedCount += i;
        SharedPreferences.Editor edit = MessagesController.getGlobalMainSettings().edit();
        edit.putInt("callEncryptionHintDisplayedCount", callEncryptionHintDisplayedCount);
        edit.apply();
    }

    public static boolean isAppUpdateAvailable() {
        int buildVersion;
        TLRPC.TL_help_appUpdate tL_help_appUpdate = pendingAppUpdate;
        if (tL_help_appUpdate == null || tL_help_appUpdate.document == null || !ApplicationLoader.isStandaloneBuild()) {
            return false;
        }
        try {
            buildVersion = ApplicationLoader.applicationContext.getPackageManager().getPackageInfo(ApplicationLoader.applicationContext.getPackageName(), 0).versionCode;
        } catch (Exception e) {
            FileLog.e(e);
            buildVersion = buildVersion();
        }
        return pendingAppUpdateBuildVersion == buildVersion;
    }

    public static boolean isAutoplayGifs() {
        return LiteMode.isEnabled(2048);
    }

    public static boolean isAutoplayVideo() {
        return LiteMode.isEnabled(1024);
    }

    public static boolean isPassportConfigLoaded() {
        return passportConfigMap != null;
    }

    public static boolean isProxyEnabled() {
        return MessagesController.getGlobalMainSettings().getBoolean("proxy_enabled", false) && currentProxy != null;
    }

    public static boolean isSecretMapPreviewSet() {
        return MessagesController.getGlobalMainSettings().contains("mapPreviewType");
    }

    public static boolean isUsingCamera2(int i) {
        Boolean bool = useCamera2Force;
        return bool == null ? !MessagesController.getInstance(i).androidDisableRoundCamera2 : bool.booleanValue();
    }

    private static boolean isWhitelisted(MediaCodecInfo mediaCodecInfo) {
        if (BuildVars.DEBUG_PRIVATE_VERSION) {
            return true;
        }
        return hevcEncoderWhitelist.contains(mediaCodecInfo.getName().toLowerCase());
    }

    public static void lambda$checkLogsToDelete$3(int i) {
        File logsDir;
        long j = i - 864000;
        try {
            logsDir = AndroidUtilities.getLogsDir();
        } catch (Throwable th) {
            FileLog.e(th);
        }
        if (logsDir == null) {
            return;
        }
        Utilities.clearDir(logsDir.getAbsolutePath(), 0, j, false);
        SharedPreferences.Editor edit = MessagesController.getGlobalMainSettings().edit();
        edit.putInt("lastLogsCheckTime", lastLogsCheckTime);
        edit.apply();
    }

    public static void lambda$checkSaveToGalleryFiles$5() {
        try {
            File file = new File(Environment.getExternalStorageDirectory(), "Telegram");
            File file2 = new File(file, "Telegram Images");
            file2.mkdir();
            File file3 = new File(file, "Telegram Video");
            file3.mkdir();
            if (BuildVars.NO_SCOPED_STORAGE) {
                if (file2.isDirectory()) {
                    AndroidUtilities.createEmptyFile(new File(file2, ".nomedia"));
                }
                if (file3.isDirectory()) {
                    AndroidUtilities.createEmptyFile(new File(file3, ".nomedia"));
                    return;
                }
                return;
            }
            if (file2.isDirectory()) {
                new File(file2, ".nomedia").delete();
            }
            if (file3.isDirectory()) {
                new File(file3, ".nomedia").delete();
            }
        } catch (Throwable th) {
            FileLog.e(th);
        }
    }

    public static void lambda$checkSdCard$0() {
    }

    public static void lambda$checkSdCard$1(AlertDialog alertDialog, int i) {
    }

    public static void lambda$checkSdCard$2() {
        BaseFragment lastFragment;
        if (readOnlyStorageDirAlertShowed || (lastFragment = LaunchActivity.getLastFragment()) == null || lastFragment.getParentActivity() == null) {
            return;
        }
        storageCacheDir = null;
        saveConfig();
        ImageLoader.getInstance().checkMediaPaths(new Runnable() {
            @Override
            public final void run() {
                SharedConfig.lambda$checkSdCard$0();
            }
        });
        readOnlyStorageDirAlertShowed = true;
        AlertDialog.Builder builder = new AlertDialog.Builder(lastFragment.getParentActivity());
        builder.setTitle(LocaleController.getString(R.string.SdCardError));
        builder.setSubtitle(LocaleController.getString(R.string.SdCardErrorDescription));
        builder.setPositiveButton(LocaleController.getString(R.string.DoNotUseSDCard), new AlertDialog.OnButtonClickListener() {
            @Override
            public final void onClick(AlertDialog alertDialog, int i) {
                SharedConfig.lambda$checkSdCard$1(alertDialog, i);
            }
        });
        AlertDialog create = builder.create();
        create.setCanceledOnTouchOutside(false);
        create.show();
    }

    public static int lambda$saveProxyList$4(ProxyInfo proxyInfo, ProxyInfo proxyInfo2) {
        ProxyInfo proxyInfo3 = currentProxy;
        long j = proxyInfo3 == proxyInfo ? -200000L : 0L;
        if (!proxyInfo.available) {
            j += 100000;
        }
        long j2 = proxyInfo3 == proxyInfo2 ? -200000L : 0L;
        if (!proxyInfo2.available) {
            j2 += 100000;
        }
        return Long.compare(proxyInfo.ping + j, proxyInfo2.ping + j2);
    }

    public static void loadConfig() {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.SharedConfig.loadConfig():void");
    }

    private static void loadDebugConfig(SharedPreferences sharedPreferences) {
        drawActionBarShadow = sharedPreferences.getBoolean("drawActionBarShadow", true);
    }

    public static void loadProxyList() {
        if (proxyListLoaded) {
            return;
        }
        SharedPreferences sharedPreferences = ApplicationLoader.applicationContext.getSharedPreferences("mainconfig", 0);
        String string = sharedPreferences.getString("proxy_ip", "");
        String string2 = sharedPreferences.getString("proxy_user", "");
        String string3 = sharedPreferences.getString("proxy_pass", "");
        String string4 = sharedPreferences.getString("proxy_secret", "");
        int i = sharedPreferences.getInt("proxy_port", 1080);
        proxyListLoaded = true;
        proxyList.clear();
        currentProxy = null;
        String string5 = sharedPreferences.getString("proxy_list", null);
        if (!TextUtils.isEmpty(string5)) {
            SerializedData serializedData = new SerializedData(Base64.decode(string5, 0));
            int readInt32 = serializedData.readInt32(false);
            if (readInt32 == -1) {
                byte readByte = serializedData.readByte(false);
                if (readByte == 2) {
                    int readInt322 = serializedData.readInt32(false);
                    for (int i2 = 0; i2 < readInt322; i2++) {
                        ProxyInfo proxyInfo = new ProxyInfo(serializedData.readString(false), serializedData.readInt32(false), serializedData.readString(false), serializedData.readString(false), serializedData.readString(false));
                        proxyInfo.ping = serializedData.readInt64(false);
                        proxyInfo.availableCheckTime = serializedData.readInt64(false);
                        proxyList.add(0, proxyInfo);
                        if (currentProxy == null && !TextUtils.isEmpty(string) && string.equals(proxyInfo.address) && i == proxyInfo.port && string2.equals(proxyInfo.username) && string3.equals(proxyInfo.password)) {
                            currentProxy = proxyInfo;
                        }
                    }
                } else {
                    FileLog.e("Unknown proxy schema version: " + ((int) readByte));
                }
            } else {
                for (int i3 = 0; i3 < readInt32; i3++) {
                    ProxyInfo proxyInfo2 = new ProxyInfo(serializedData.readString(false), serializedData.readInt32(false), serializedData.readString(false), serializedData.readString(false), serializedData.readString(false));
                    proxyList.add(0, proxyInfo2);
                    if (currentProxy == null && !TextUtils.isEmpty(string) && string.equals(proxyInfo2.address) && i == proxyInfo2.port && string2.equals(proxyInfo2.username) && string3.equals(proxyInfo2.password)) {
                        currentProxy = proxyInfo2;
                    }
                }
            }
            serializedData.cleanup();
        }
        if (currentProxy != null || TextUtils.isEmpty(string)) {
            return;
        }
        ProxyInfo proxyInfo3 = new ProxyInfo(string, i, string2, string3, string4);
        currentProxy = proxyInfo3;
        proxyList.add(0, proxyInfo3);
    }

    public static boolean loopStickers() {
        return LiteMode.isEnabled(2);
    }

    public static int measureDevicePerformanceClass() {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.SharedConfig.measureDevicePerformanceClass():int");
    }

    public static void overrideDevicePerformanceClass(int i) {
        SharedPreferences.Editor edit = MessagesController.getGlobalMainSettings().edit();
        overrideDevicePerformanceClass = i;
        edit.putInt("overrideDevicePerformanceClass", i).remove("lite_mode").apply();
        if (liteMode != null) {
            LiteMode.loadPreference();
        }
    }

    public static String performanceClassName(int i) {
        return i != 0 ? i != 1 ? i != 2 ? "UNKNOWN" : "HIGH" : "AVERAGE" : "LOW";
    }

    public static void removeLockRecordAudioVideoHint() {
        SharedPreferences.Editor edit = MessagesController.getGlobalMainSettings().edit();
        edit.putInt("lockRecordAudioVideoHint", 3);
        edit.apply();
    }

    public static void removeScheduledHint() {
        SharedPreferences.Editor edit = MessagesController.getGlobalMainSettings().edit();
        edit.putInt("scheduledHintShows", 3);
        edit.apply();
    }

    public static void removeScheduledOrNoSoundHint() {
        SharedPreferences.Editor edit = MessagesController.getGlobalMainSettings().edit();
        edit.putInt("scheduledOrNoSoundHintShows", 3);
        edit.apply();
    }

    public static void removeTextSelectionHint() {
        SharedPreferences.Editor edit = MessagesController.getGlobalMainSettings().edit();
        edit.putInt("textSelectionHintShows", 3);
        edit.apply();
    }

    public static void replyingOptionsHintHintShowed() {
        SharedPreferences.Editor edit = MessagesController.getGlobalMainSettings().edit();
        replyingOptionsHintShown = true;
        edit.putBoolean("replyingOptionsHintShown", true);
        edit.apply();
    }

    public static void saveConfig() {
        synchronized (sync) {
            try {
                try {
                    SharedPreferences.Editor edit = ApplicationLoader.applicationContext.getSharedPreferences("userconfing", 0).edit();
                    edit.putBoolean("saveIncomingPhotos", saveIncomingPhotos);
                    edit.putString("passcodeHash1", passcodeHash);
                    byte[] bArr = passcodeSalt;
                    edit.putString("passcodeSalt", bArr.length > 0 ? Base64.encodeToString(bArr, 0) : "");
                    edit.putBoolean("appLocked", appLocked);
                    edit.putInt("passcodeType", passcodeType);
                    edit.putLong("passcodeRetryInMs", passcodeRetryInMs);
                    edit.putLong("lastUptimeMillis", lastUptimeMillis);
                    edit.putInt("badPasscodeTries", badPasscodeTries);
                    edit.putInt("autoLockIn", autoLockIn);
                    edit.putInt("lastPauseTime", lastPauseTime);
                    edit.putBoolean("useFingerprint", useFingerprintLock);
                    edit.putBoolean("allowScreenCapture", allowScreenCapture);
                    edit.putString("pushString2", pushString);
                    edit.putInt("pushType", pushType);
                    edit.putBoolean("pushStatSent", pushStatSent);
                    byte[] bArr2 = pushAuthKey;
                    edit.putString("pushAuthKey", bArr2 != null ? Base64.encodeToString(bArr2, 0) : "");
                    edit.putInt("lastLocalId", lastLocalId);
                    edit.putString("passportConfigJson", passportConfigJson);
                    edit.putInt("passportConfigHash", passportConfigHash);
                    edit.putBoolean("sortContactsByName", sortContactsByName);
                    edit.putBoolean("sortFilesByName", sortFilesByName);
                    edit.putInt("textSelectionHintShows", textSelectionHintShows);
                    edit.putInt("scheduledOrNoSoundHintShows", scheduledOrNoSoundHintShows);
                    edit.putLong("scheduledOrNoSoundHintSeenAt", scheduledOrNoSoundHintSeenAt);
                    edit.putInt("scheduledHintShows", scheduledHintShows);
                    edit.putLong("scheduledHintSeenAt", scheduledHintSeenAt);
                    edit.putBoolean("forwardingOptionsHintShown", forwardingOptionsHintShown);
                    edit.putBoolean("replyingOptionsHintShown", replyingOptionsHintShown);
                    edit.putInt("lockRecordAudioVideoHint", lockRecordAudioVideoHint);
                    edit.putString("storageCacheDir", !TextUtils.isEmpty(storageCacheDir) ? storageCacheDir : "");
                    edit.putBoolean("proxyRotationEnabled", proxyRotationEnabled);
                    edit.putInt("proxyRotationTimeout", proxyRotationTimeout);
                    TLRPC.TL_help_appUpdate tL_help_appUpdate = pendingAppUpdate;
                    if (tL_help_appUpdate != null) {
                        try {
                            SerializedData serializedData = new SerializedData(tL_help_appUpdate.getObjectSize());
                            pendingAppUpdate.serializeToStream(serializedData);
                            edit.putString("appUpdate", Base64.encodeToString(serializedData.toByteArray(), 0));
                            edit.putInt("appUpdateBuild", pendingAppUpdateBuildVersion);
                            serializedData.cleanup();
                        } catch (Exception unused) {
                        }
                    } else {
                        edit.remove("appUpdate");
                    }
                    edit.putLong("appUpdateCheckTime", lastUpdateCheckTime);
                    edit.apply();
                    SharedPreferences.Editor edit2 = ApplicationLoader.applicationContext.getSharedPreferences("mainconfig", 0).edit();
                    edit2.putBoolean("hasEmailLogin", hasEmailLogin);
                    edit2.putBoolean("floatingDebugActive", isFloatingDebugActive);
                    edit2.putBoolean("record_via_sco", recordViaSco);
                    edit2.apply();
                } catch (Exception e) {
                    FileLog.e(e);
                }
            } catch (Throwable th) {
                throw th;
            }
        }
    }

    public static void saveDebugConfig() {
        ApplicationLoader.applicationContext.getSharedPreferences("mainconfig", 0).edit().putBoolean("drawActionBarShadow", drawActionBarShadow);
    }

    public static void saveProxyList() {
        ArrayList arrayList = new ArrayList(proxyList);
        Collections.sort(arrayList, new Comparator() {
            @Override
            public final int compare(Object obj, Object obj2) {
                int lambda$saveProxyList$4;
                lambda$saveProxyList$4 = SharedConfig.lambda$saveProxyList$4((SharedConfig.ProxyInfo) obj, (SharedConfig.ProxyInfo) obj2);
                return lambda$saveProxyList$4;
            }
        });
        SerializedData serializedData = new SerializedData();
        serializedData.writeInt32(-1);
        serializedData.writeByte(2);
        int size = arrayList.size();
        serializedData.writeInt32(size);
        for (int i = size - 1; i >= 0; i--) {
            ProxyInfo proxyInfo = (ProxyInfo) arrayList.get(i);
            String str = proxyInfo.address;
            String str2 = "";
            if (str == null) {
                str = "";
            }
            serializedData.writeString(str);
            serializedData.writeInt32(proxyInfo.port);
            String str3 = proxyInfo.username;
            if (str3 == null) {
                str3 = "";
            }
            serializedData.writeString(str3);
            String str4 = proxyInfo.password;
            if (str4 == null) {
                str4 = "";
            }
            serializedData.writeString(str4);
            String str5 = proxyInfo.secret;
            if (str5 != null) {
                str2 = str5;
            }
            serializedData.writeString(str2);
            serializedData.writeInt64(proxyInfo.ping);
            serializedData.writeInt64(proxyInfo.availableCheckTime);
        }
        ApplicationLoader.applicationContext.getSharedPreferences("mainconfig", 0).edit().putString("proxy_list", Base64.encodeToString(serializedData.toByteArray(), 2)).apply();
        serializedData.cleanup();
    }

    public static void setAnimationsEnabled(boolean z) {
        animationsEnabled = Boolean.valueOf(z);
    }

    public static void setDistanceSystemType(int i) {
        distanceSystemType = i;
        SharedPreferences.Editor edit = MessagesController.getGlobalMainSettings().edit();
        edit.putInt("distanceSystemType", distanceSystemType);
        edit.apply();
        LocaleController.resetImperialSystemType();
    }

    public static void setDontAskManageStorage(boolean z) {
        dontAskManageStorage = z;
        ApplicationLoader.applicationContext.getSharedPreferences("mainconfig", 0).edit().putBoolean("dontAskManageStorage", dontAskManageStorage).apply();
    }

    public static void setFastScrollHintCount(int i) {
        if (fastScrollHintCount != i) {
            fastScrollHintCount = i;
            ApplicationLoader.applicationContext.getSharedPreferences("mainconfig", 0).edit().putInt("fastScrollHintCount", fastScrollHintCount).apply();
        }
    }

    public static void setKeepMedia(int i) {
        keepMedia = i;
        SharedPreferences.Editor edit = MessagesController.getGlobalMainSettings().edit();
        edit.putInt("keep_media", keepMedia);
        edit.apply();
    }

    public static void setMediaColumnsCount(int i) {
        if (mediaColumnsCount != i) {
            mediaColumnsCount = i;
            ApplicationLoader.applicationContext.getSharedPreferences("mainconfig", 0).edit().putInt("mediaColumnsCount", mediaColumnsCount).apply();
        }
    }

    public static void setMultipleReactionsPromoShowed(boolean z) {
        multipleReactionsPromoShowed = z;
        SharedPreferences.Editor edit = MessagesController.getGlobalMainSettings().edit();
        edit.putBoolean("multipleReactionsPromoShowed", multipleReactionsPromoShowed);
        edit.apply();
    }

    public static boolean setNewAppVersionAvailable(org.telegram.tgnet.TLRPC.TL_help_appUpdate r4) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.SharedConfig.setNewAppVersionAvailable(org.telegram.tgnet.TLRPC$TL_help_appUpdate):boolean");
    }

    public static void setNoSoundHintShowed(boolean z) {
        if (noSoundHintShowed == z) {
            return;
        }
        noSoundHintShowed = z;
        SharedPreferences.Editor edit = MessagesController.getGlobalMainSettings().edit();
        edit.putBoolean("noSoundHintShowed", noSoundHintShowed);
        edit.apply();
    }

    public static void setPassportConfig(String str, int i) {
        passportConfigMap = null;
        passportConfigJson = str;
        passportConfigHash = i;
        saveConfig();
        getCountryLangs();
    }

    public static void setPlaybackOrderType(int i) {
        if (i == 2) {
            shuffleMusic = true;
            playOrderReversed = false;
        } else {
            if (i == 1) {
                playOrderReversed = true;
            } else {
                playOrderReversed = false;
            }
            shuffleMusic = false;
        }
        MediaController.getInstance().checkIsNextMediaFileDownloaded();
        SharedPreferences.Editor edit = MessagesController.getGlobalMainSettings().edit();
        edit.putBoolean("shuffleMusic", shuffleMusic);
        edit.putBoolean("playOrderReversed", playOrderReversed);
        edit.apply();
    }

    public static void setRepeatMode(int i) {
        repeatMode = i;
        if (i < 0 || i > 2) {
            repeatMode = 0;
        }
        SharedPreferences.Editor edit = MessagesController.getGlobalMainSettings().edit();
        edit.putInt("repeatMode", repeatMode);
        edit.apply();
    }

    public static void setSearchEngineType(int i) {
        searchEngineType = i;
        SharedPreferences.Editor edit = MessagesController.getGlobalMainSettings().edit();
        edit.putInt("searchEngineType", searchEngineType);
        edit.apply();
    }

    public static void setSearchMessagesAsListUsed(boolean z) {
        searchMessagesAsListUsed = z;
        SharedPreferences.Editor edit = MessagesController.getGlobalMainSettings().edit();
        edit.putBoolean("searchMessagesAsListUsed", searchMessagesAsListUsed);
        edit.apply();
    }

    public static void setSecretMapPreviewType(int i) {
        mapPreviewType = i;
        SharedPreferences.Editor edit = MessagesController.getGlobalMainSettings().edit();
        edit.putInt("mapPreviewType", mapPreviewType);
        edit.apply();
    }

    public static void setStickersReorderingHintUsed(boolean z) {
        stickersReorderingHintUsed = z;
        SharedPreferences.Editor edit = MessagesController.getGlobalMainSettings().edit();
        edit.putBoolean("stickersReorderingHintUsed", stickersReorderingHintUsed);
        edit.apply();
    }

    public static void setStoriesColumnsCount(int i) {
        if (storiesColumnsCount != i) {
            storiesColumnsCount = i;
            ApplicationLoader.applicationContext.getSharedPreferences("mainconfig", 0).edit().putInt("storiesColumnsCount", storiesColumnsCount).apply();
        }
    }

    public static void setStoriesIntroShown(boolean z) {
        storiesIntroShown = z;
        SharedPreferences.Editor edit = MessagesController.getGlobalMainSettings().edit();
        edit.putBoolean("storiesIntroShown", storiesIntroShown);
        edit.apply();
    }

    public static void setStoriesReactionsLongPressHintUsed(boolean z) {
        storyReactionsLongPressHint = z;
        SharedPreferences.Editor edit = MessagesController.getGlobalMainSettings().edit();
        edit.putBoolean("storyReactionsLongPressHint", storyReactionsLongPressHint);
        edit.apply();
    }

    public static void setSuggestStickers(int i) {
        suggestStickers = i;
        SharedPreferences.Editor edit = MessagesController.getGlobalMainSettings().edit();
        edit.putInt("suggestStickers", suggestStickers);
        edit.apply();
    }

    public static void setUseThreeLinesLayout(boolean z) {
        useThreeLinesLayout = z;
        SharedPreferences.Editor edit = MessagesController.getGlobalMainSettings().edit();
        edit.putBoolean("useThreeLinesLayout", useThreeLinesLayout);
        edit.apply();
        NotificationCenter.getGlobalInstance().lambda$postNotificationNameOnUIThread$1(NotificationCenter.dialogsNeedReload, Boolean.TRUE);
    }

    public static void toggleArchiveHidden() {
        archiveHidden = !archiveHidden;
        SharedPreferences.Editor edit = MessagesController.getGlobalMainSettings().edit();
        edit.putBoolean("archiveHidden", archiveHidden);
        edit.apply();
    }

    public static void toggleAutoplayGifs() {
        LiteMode.toggleFlag(2048);
    }

    public static void toggleAutoplayVideo() {
        LiteMode.toggleFlag(1024);
    }

    public static void toggleBigEmoji() {
        allowBigEmoji = !allowBigEmoji;
        SharedPreferences.Editor edit = MessagesController.getGlobalMainSettings().edit();
        edit.putBoolean("allowBigEmoji", allowBigEmoji);
        edit.apply();
    }

    public static void toggleBrowserAdaptableColors() {
        adaptableColorInBrowser = !adaptableColorInBrowser;
        SharedPreferences.Editor edit = MessagesController.getGlobalMainSettings().edit();
        edit.putBoolean("adaptableBrowser", adaptableColorInBrowser);
        edit.apply();
    }

    public static void toggleChatBlur() {
        LiteMode.toggleFlag(256);
    }

    public static void toggleCustomTabs(boolean z) {
        customTabs = z;
        SharedPreferences.Editor edit = MessagesController.getGlobalMainSettings().edit();
        edit.putBoolean("custom_tabs", customTabs);
        edit.apply();
    }

    public static void toggleDebugVideoQualities() {
        debugVideoQualities = !debugVideoQualities;
        SharedPreferences.Editor edit = MessagesController.getGlobalMainSettings().edit();
        edit.putBoolean("debugVideoQualities", debugVideoQualities);
        edit.apply();
    }

    public static void toggleDebugWebView() {
        debugWebView = !debugWebView;
        WebView.setWebContentsDebuggingEnabled(debugWebView);
        SharedPreferences.Editor edit = MessagesController.getGlobalMainSettings().edit();
        edit.putBoolean("debugWebView", debugWebView);
        edit.apply();
    }

    public static void toggleDirectShare() {
        directShare = !directShare;
        SharedPreferences.Editor edit = MessagesController.getGlobalMainSettings().edit();
        edit.putBoolean("direct_share", directShare);
        edit.apply();
        ShortcutManagerCompat.removeAllDynamicShortcuts(ApplicationLoader.applicationContext);
        MediaDataController.getInstance(UserConfig.selectedAccount).buildShortcuts();
    }

    public static void toggleDisableVoiceAudioEffects() {
        disableVoiceAudioEffects = !disableVoiceAudioEffects;
        SharedPreferences.Editor edit = MessagesController.getGlobalMainSettings().edit();
        edit.putBoolean("disableVoiceAudioEffects", disableVoiceAudioEffects);
        edit.apply();
    }

    public static void toggleForceDisableTabletMode() {
        forceDisableTabletMode = !forceDisableTabletMode;
        SharedPreferences.Editor edit = MessagesController.getGlobalMainSettings().edit();
        edit.putBoolean("forceDisableTabletMode", forceDisableTabletMode);
        edit.apply();
    }

    public static void toggleInappBrowser() {
        inappBrowser = !inappBrowser;
        SharedPreferences.Editor edit = MessagesController.getGlobalMainSettings().edit();
        edit.putBoolean("inapp_browser", inappBrowser);
        edit.apply();
    }

    public static void toggleInappCamera() {
        inappCamera = !inappCamera;
        SharedPreferences.Editor edit = MessagesController.getGlobalMainSettings().edit();
        edit.putBoolean("inappCamera", inappCamera);
        edit.apply();
    }

    public static void toggleLocalInstantView() {
        onlyLocalInstantView = !onlyLocalInstantView;
        SharedPreferences.Editor edit = MessagesController.getGlobalMainSettings().edit();
        edit.putBoolean("onlyLocalInstantView", onlyLocalInstantView);
        edit.apply();
    }

    public static void toggleLoopStickers() {
        LiteMode.toggleFlag(2);
    }

    public static void toggleNextMediaTap() {
        nextMediaTap = !nextMediaTap;
        SharedPreferences.Editor edit = MessagesController.getGlobalMainSettings().edit();
        edit.putBoolean("next_media_on_tap", nextMediaTap);
        edit.apply();
    }

    public static void toggleNoiseSupression() {
        noiseSupression = !noiseSupression;
        SharedPreferences.Editor edit = MessagesController.getGlobalMainSettings().edit();
        edit.putBoolean("noiseSupression", noiseSupression);
        edit.apply();
    }

    public static void togglePauseMusicOnMedia() {
        pauseMusicOnMedia = !pauseMusicOnMedia;
        SharedPreferences.Editor edit = MessagesController.getGlobalMainSettings().edit();
        edit.putBoolean("pauseMusicOnMedia", pauseMusicOnMedia);
        edit.apply();
    }

    public static void togglePauseMusicOnRecord() {
        pauseMusicOnRecord = !pauseMusicOnRecord;
        SharedPreferences.Editor edit = MessagesController.getGlobalMainSettings().edit();
        edit.putBoolean("pauseMusicOnRecord", pauseMusicOnRecord);
        edit.apply();
    }

    public static void togglePaymentByInvoice() {
        payByInvoice = !payByInvoice;
        ApplicationLoader.applicationContext.getSharedPreferences("mainconfig", 0).edit().putBoolean("payByInvoice", payByInvoice).apply();
    }

    public static void togglePhotoViewerBlur() {
        photoViewerBlur = !photoViewerBlur;
        ApplicationLoader.applicationContext.getSharedPreferences("mainconfig", 0).edit().putBoolean("photoViewerBlur", photoViewerBlur).apply();
    }

    public static void toggleRaiseToListen() {
        raiseToListen = !raiseToListen;
        SharedPreferences.Editor edit = MessagesController.getGlobalMainSettings().edit();
        edit.putBoolean("raise_to_listen", raiseToListen);
        edit.apply();
    }

    public static void toggleRaiseToSpeak() {
        raiseToSpeak = !raiseToSpeak;
        SharedPreferences.Editor edit = MessagesController.getGlobalMainSettings().edit();
        edit.putBoolean("raise_to_speak", raiseToSpeak);
        edit.apply();
    }

    public static void toggleRoundCamera() {
        bigCameraForRound = !bigCameraForRound;
        ApplicationLoader.applicationContext.getSharedPreferences("mainconfig", 0).edit().putBoolean("bigCameraForRound", bigCameraForRound).apply();
    }

    public static void toggleRoundCamera16to9() {
        roundCamera16to9 = !roundCamera16to9;
        SharedPreferences.Editor edit = MessagesController.getGlobalMainSettings().edit();
        edit.putBoolean("roundCamera16to9", roundCamera16to9);
        edit.apply();
    }

    public static void toggleSaveStreamMedia() {
        saveStreamMedia = !saveStreamMedia;
        SharedPreferences.Editor edit = MessagesController.getGlobalMainSettings().edit();
        edit.putBoolean("saveStreamMedia", saveStreamMedia);
        edit.apply();
    }

    public static void toggleSortContactsByName() {
        sortContactsByName = !sortContactsByName;
        SharedPreferences.Editor edit = MessagesController.getGlobalMainSettings().edit();
        edit.putBoolean("sortContactsByName", sortContactsByName);
        edit.apply();
    }

    public static void toggleSortFilesByName() {
        sortFilesByName = !sortFilesByName;
        SharedPreferences.Editor edit = MessagesController.getGlobalMainSettings().edit();
        edit.putBoolean("sortFilesByName", sortFilesByName);
        edit.apply();
    }

    public static void toggleStreamAllVideo() {
        streamAllVideo = !streamAllVideo;
        SharedPreferences.Editor edit = MessagesController.getGlobalMainSettings().edit();
        edit.putBoolean("streamAllVideo", streamAllVideo);
        edit.apply();
    }

    public static void toggleStreamMedia() {
        streamMedia = !streamMedia;
        SharedPreferences.Editor edit = MessagesController.getGlobalMainSettings().edit();
        edit.putBoolean("streamMedia", streamMedia);
        edit.apply();
    }

    public static void toggleStreamMkv() {
        streamMkv = !streamMkv;
        SharedPreferences.Editor edit = MessagesController.getGlobalMainSettings().edit();
        edit.putBoolean("streamMkv", streamMkv);
        edit.apply();
    }

    public static void toggleSuggestAnimatedEmoji() {
        suggestAnimatedEmoji = !suggestAnimatedEmoji;
        SharedPreferences.Editor edit = MessagesController.getGlobalMainSettings().edit();
        edit.putBoolean("suggestAnimatedEmoji", suggestAnimatedEmoji);
        edit.apply();
    }

    public static void toggleSurfaceInStories() {
        useSurfaceInStories = !useSurfaceInStories;
        ApplicationLoader.applicationContext.getSharedPreferences("mainconfig", 0).edit().putBoolean("useSurfaceInStories", useSurfaceInStories).apply();
    }

    public static void toggleUpdateStickersOrderOnSend() {
        SharedPreferences.Editor edit = MessagesController.getGlobalMainSettings().edit();
        boolean z = !updateStickersOrderOnSend;
        updateStickersOrderOnSend = z;
        edit.putBoolean("updateStickersOrderOnSend", z);
        edit.apply();
    }

    public static void toggleUseCamera2(int i) {
        SharedPreferences.Editor edit = ApplicationLoader.applicationContext.getSharedPreferences("mainconfig", 0).edit();
        boolean z = !isUsingCamera2(i);
        useCamera2Force = Boolean.valueOf(z);
        edit.putBoolean("useCamera2Force_2", z).apply();
    }

    public static void toggleUseNewBlur() {
        useNewBlur = !useNewBlur;
        ApplicationLoader.applicationContext.getSharedPreferences("mainconfig", 0).edit().putBoolean("useNewBlur", useNewBlur).apply();
    }

    public static void updateChatListSwipeSetting(int i) {
        chatSwipeAction = i;
        ApplicationLoader.applicationContext.getSharedPreferences("mainconfig", 0).edit().putInt("ChatSwipeAction", chatSwipeAction).apply();
    }

    public static void updateDayNightThemeSwitchHintCount(int i) {
        dayNightThemeSwitchHintCount = i;
        ApplicationLoader.applicationContext.getSharedPreferences("mainconfig", 0).edit().putInt("dayNightThemeSwitchHintCount", dayNightThemeSwitchHintCount).apply();
    }

    public static void updateEmojiInteractionsHintCount(int i) {
        emojiInteractionsHintCount = i;
        ApplicationLoader.applicationContext.getSharedPreferences("mainconfig", 0).edit().putInt("emojiInteractionsHintCount", emojiInteractionsHintCount).apply();
    }

    public static void updateMessageSeenHintCount(int i) {
        messageSeenHintCount = i;
        ApplicationLoader.applicationContext.getSharedPreferences("mainconfig", 0).edit().putInt("messageSeenCount", messageSeenHintCount).apply();
    }

    public static void updateStealthModeSendMessageConfirm(int i) {
        stealthModeSendMessageConfirm = i;
        ApplicationLoader.applicationContext.getSharedPreferences("mainconfig", 0).edit().putInt("stealthModeSendMessageConfirm", stealthModeSendMessageConfirm).apply();
    }

    public static void updateTabletConfig() {
        if (fontSizeIsDefault) {
            SharedPreferences sharedPreferences = ApplicationLoader.applicationContext.getSharedPreferences("mainconfig", 0);
            int i = sharedPreferences.getInt("fons_size", AndroidUtilities.isTablet() ? 18 : 16);
            fontSize = i;
            ivFontSize = sharedPreferences.getInt("iv_font_size", i);
        }
    }

    private static boolean versionBiggerOrEqual(String str, String str2) {
        String[] split = str.split("\\.");
        String[] split2 = str2.split("\\.");
        for (int i = 0; i < Math.min(split.length, split2.length); i++) {
            int parseInt = Integer.parseInt(split[i]);
            int parseInt2 = Integer.parseInt(split2[i]);
            if (parseInt < parseInt2) {
                return false;
            }
            if (parseInt > parseInt2) {
                return true;
            }
        }
        return true;
    }
}
