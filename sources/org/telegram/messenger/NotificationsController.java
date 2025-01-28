package org.telegram.messenger;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationChannelGroup;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.ImageDecoder;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.PostProcessor;
import android.graphics.Rect;
import android.graphics.Shader;
import android.media.AudioManager;
import android.media.SoundPool;
import android.net.Uri;
import android.os.Build;
import android.os.PowerManager;
import android.os.SystemClock;
import android.text.TextPaint;
import android.util.Pair;
import android.util.SparseArray;
import androidx.collection.LongSparseArray;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.app.Person;
import androidx.core.graphics.drawable.IconCompat;
import j$.util.Comparator$CC;
import j$.util.function.Consumer;
import j$.util.function.ToLongFunction;
import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import org.telegram.messenger.NotificationsController;
import org.telegram.messenger.support.LongSparseIntArray;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.tgnet.tl.TL_account;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.AvatarDrawable;
import org.telegram.ui.Components.spoilers.SpoilerEffect;
import org.telegram.ui.PopupNotificationActivity;
import org.telegram.ui.Stories.recorder.StoryEntry;

public class NotificationsController extends BaseController {
    public static final String EXTRA_VOICE_REPLY = "extra_voice_reply";
    private static volatile NotificationsController[] Instance = null;
    public static String OTHER_NOTIFICATIONS_CHANNEL = null;
    public static final int SETTING_MUTE_2_DAYS = 2;
    public static final int SETTING_MUTE_8_HOURS = 1;
    public static final int SETTING_MUTE_CUSTOM = 5;
    public static final int SETTING_MUTE_FOREVER = 3;
    public static final int SETTING_MUTE_HOUR = 0;
    public static final int SETTING_MUTE_UNMUTE = 4;
    public static final int SETTING_SOUND_OFF = 1;
    public static final int SETTING_SOUND_ON = 0;
    public static final int TYPE_CHANNEL = 2;
    public static final int TYPE_GROUP = 0;
    public static final int TYPE_PRIVATE = 1;
    public static final int TYPE_REACTIONS_MESSAGES = 4;
    public static final int TYPE_REACTIONS_STORIES = 5;
    public static final int TYPE_STORIES = 3;
    protected static AudioManager audioManager;
    private static final Object[] lockObjects;
    private static NotificationManagerCompat notificationManager;
    private static final LongSparseArray sharedPrefCachedKeys;
    private static NotificationManager systemNotificationManager;
    private AlarmManager alarmManager;
    private boolean channelGroupsCreated;
    private Runnable checkStoryPushesRunnable;
    private final ArrayList<MessageObject> delayedPushMessages;
    NotificationsSettingsFacade dialogsNotificationsFacade;
    private final LongSparseArray fcmRandomMessagesDict;
    private Boolean groupsCreated;
    private boolean inChatSoundEnabled;
    private int lastBadgeCount;
    private int lastButtonId;
    public long lastNotificationChannelCreateTime;
    private int lastOnlineFromOtherDevice;
    private long lastSoundOutPlay;
    private long lastSoundPlay;
    private final LongSparseArray lastWearNotifiedMessageId;
    private String launcherClassName;
    private SpoilerEffect mediaSpoilerEffect;
    private Runnable notificationDelayRunnable;
    private PowerManager.WakeLock notificationDelayWakelock;
    private String notificationGroup;
    private int notificationId;
    private boolean notifyCheck;
    private long openedDialogId;
    private final HashSet<Long> openedInBubbleDialogs;
    private long openedTopicId;
    private int personalCount;
    public final ArrayList<MessageObject> popupMessages;
    public ArrayList<MessageObject> popupReplyMessages;
    private final LongSparseArray pushDialogs;
    private final LongSparseArray pushDialogsOverrideMention;
    private final ArrayList<MessageObject> pushMessages;
    private final LongSparseArray pushMessagesDict;
    public boolean showBadgeMessages;
    public boolean showBadgeMuted;
    public boolean showBadgeNumber;
    private final LongSparseArray smartNotificationsDialogs;
    private int soundIn;
    private boolean soundInLoaded;
    private int soundOut;
    private boolean soundOutLoaded;
    private SoundPool soundPool;
    private int soundRecord;
    private boolean soundRecordLoaded;
    char[] spoilerChars;
    private final ArrayList<StoryNotification> storyPushMessages;
    private final LongSparseArray storyPushMessagesDict;
    private int total_unread_count;
    private final LongSparseArray wearNotificationsIds;
    private static final DispatchQueue notificationsQueue = new DispatchQueue("notificationsQueue");
    public static long globalSecretChatId = DialogObject.makeEncryptedDialogId(1);

    public class C1NotificationHolder {
        TLRPC.Chat chat;
        long dialogId;
        int id;
        String name;
        NotificationCompat.Builder notification;
        boolean story;
        long topicId;
        TLRPC.User user;
        final String val$chatName;
        final int val$chatType;
        final int val$importance;
        final boolean val$isDefault;
        final boolean val$isInApp;
        final boolean val$isSilent;
        final long val$lastTopicId;
        final int val$ledColor;
        final Uri val$sound;
        final long[] val$vibrationPattern;

        C1NotificationHolder(int i, long j, boolean z, long j2, String str, TLRPC.User user, TLRPC.Chat chat, NotificationCompat.Builder builder, long j3, String str2, long[] jArr, int i2, Uri uri, int i3, boolean z2, boolean z3, boolean z4, int i4) {
            this.val$lastTopicId = j3;
            this.val$chatName = str2;
            this.val$vibrationPattern = jArr;
            this.val$ledColor = i2;
            this.val$sound = uri;
            this.val$importance = i3;
            this.val$isDefault = z2;
            this.val$isInApp = z3;
            this.val$isSilent = z4;
            this.val$chatType = i4;
            this.id = i;
            this.name = str;
            this.user = user;
            this.chat = chat;
            this.notification = builder;
            this.dialogId = j;
            this.story = z;
            this.topicId = j2;
        }

        void call() {
            if (BuildVars.LOGS_ENABLED) {
                FileLog.w("show dialog notification with id " + this.id + " " + this.dialogId + " user=" + this.user + " chat=" + this.chat);
            }
            try {
                NotificationsController.notificationManager.notify(this.id, this.notification.build());
            } catch (SecurityException e) {
                FileLog.e(e);
                NotificationsController.this.resetNotificationSound(this.notification, this.dialogId, this.val$lastTopicId, this.val$chatName, this.val$vibrationPattern, this.val$ledColor, this.val$sound, this.val$importance, this.val$isDefault, this.val$isInApp, this.val$isSilent, this.val$chatType);
            }
        }
    }

    public static class DialogKey {
        final long dialogId;
        final boolean story;
        final long topicId;

        private DialogKey(long j, long j2, boolean z) {
            this.dialogId = j;
            this.topicId = j2;
            this.story = z;
        }
    }

    public static class StoryNotification {
        public long date;
        final HashMap<Integer, Pair<Long, Long>> dateByIds;
        final long dialogId;
        boolean hidden;
        String localName;

        public StoryNotification(long j, String str, int i, long j2) {
            this(j, str, i, j2, j2 + 86400000);
        }

        public StoryNotification(long j, String str, int i, long j2, long j3) {
            HashMap<Integer, Pair<Long, Long>> hashMap = new HashMap<>();
            this.dateByIds = hashMap;
            this.dialogId = j;
            this.localName = str;
            hashMap.put(Integer.valueOf(i), new Pair<>(Long.valueOf(j2), Long.valueOf(j3)));
            this.date = j2;
        }

        public long getLeastDate() {
            long j = -1;
            for (Pair<Long, Long> pair : this.dateByIds.values()) {
                if (j == -1 || j > ((Long) pair.first).longValue()) {
                    j = ((Long) pair.first).longValue();
                }
            }
            return j;
        }
    }

    static {
        notificationManager = null;
        systemNotificationManager = null;
        if (Build.VERSION.SDK_INT >= 26 && ApplicationLoader.applicationContext != null) {
            notificationManager = NotificationManagerCompat.from(ApplicationLoader.applicationContext);
            systemNotificationManager = (NotificationManager) ApplicationLoader.applicationContext.getSystemService("notification");
            checkOtherNotificationsChannel();
        }
        audioManager = (AudioManager) ApplicationLoader.applicationContext.getSystemService("audio");
        Instance = new NotificationsController[4];
        lockObjects = new Object[4];
        for (int i = 0; i < 4; i++) {
            lockObjects[i] = new Object();
        }
        sharedPrefCachedKeys = new LongSparseArray();
    }

    public NotificationsController(int i) {
        super(i);
        this.pushMessages = new ArrayList<>();
        this.delayedPushMessages = new ArrayList<>();
        this.pushMessagesDict = new LongSparseArray();
        this.fcmRandomMessagesDict = new LongSparseArray();
        this.smartNotificationsDialogs = new LongSparseArray();
        this.pushDialogs = new LongSparseArray();
        this.wearNotificationsIds = new LongSparseArray();
        this.lastWearNotifiedMessageId = new LongSparseArray();
        this.pushDialogsOverrideMention = new LongSparseArray();
        this.popupMessages = new ArrayList<>();
        this.popupReplyMessages = new ArrayList<>();
        this.openedInBubbleDialogs = new HashSet<>();
        this.storyPushMessages = new ArrayList<>();
        this.storyPushMessagesDict = new LongSparseArray();
        this.openedDialogId = 0L;
        this.openedTopicId = 0L;
        this.lastButtonId = 5000;
        this.total_unread_count = 0;
        this.personalCount = 0;
        this.notifyCheck = false;
        this.lastOnlineFromOtherDevice = 0;
        this.lastBadgeCount = -1;
        this.mediaSpoilerEffect = new SpoilerEffect();
        this.spoilerChars = new char[]{10252, 10338, 10385, 10280, 10277, 10286, 10321};
        this.checkStoryPushesRunnable = new Runnable() {
            @Override
            public final void run() {
                NotificationsController.this.checkStoryPushes();
            }
        };
        this.notificationId = this.currentAccount + 1;
        StringBuilder sb = new StringBuilder();
        sb.append("messages");
        int i2 = this.currentAccount;
        sb.append(i2 == 0 ? "" : Integer.valueOf(i2));
        this.notificationGroup = sb.toString();
        SharedPreferences notificationsSettings = getAccountInstance().getNotificationsSettings();
        this.inChatSoundEnabled = notificationsSettings.getBoolean("EnableInChatSound", true);
        this.showBadgeNumber = notificationsSettings.getBoolean("badgeNumber", true);
        this.showBadgeMuted = notificationsSettings.getBoolean("badgeNumberMuted", false);
        this.showBadgeMessages = notificationsSettings.getBoolean("badgeNumberMessages", true);
        notificationManager = NotificationManagerCompat.from(ApplicationLoader.applicationContext);
        systemNotificationManager = (NotificationManager) ApplicationLoader.applicationContext.getSystemService("notification");
        try {
            audioManager = (AudioManager) ApplicationLoader.applicationContext.getSystemService("audio");
        } catch (Exception e) {
            FileLog.e(e);
        }
        try {
            this.alarmManager = (AlarmManager) ApplicationLoader.applicationContext.getSystemService("alarm");
        } catch (Exception e2) {
            FileLog.e(e2);
        }
        try {
            PowerManager.WakeLock newWakeLock = ((PowerManager) ApplicationLoader.applicationContext.getSystemService("power")).newWakeLock(1, "telegram:notification_delay_lock");
            this.notificationDelayWakelock = newWakeLock;
            newWakeLock.setReferenceCounted(false);
        } catch (Exception e3) {
            FileLog.e(e3);
        }
        this.notificationDelayRunnable = new Runnable() {
            @Override
            public final void run() {
                NotificationsController.this.lambda$new$0();
            }
        };
        this.dialogsNotificationsFacade = new NotificationsSettingsFacade(this.currentAccount);
    }

    private int addToPopupMessages(java.util.ArrayList<org.telegram.messenger.MessageObject> r4, org.telegram.messenger.MessageObject r5, long r6, boolean r8, android.content.SharedPreferences r9) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.NotificationsController.addToPopupMessages(java.util.ArrayList, org.telegram.messenger.MessageObject, long, boolean, android.content.SharedPreferences):int");
    }

    private void appendMessage(MessageObject messageObject) {
        for (int i = 0; i < this.pushMessages.size(); i++) {
            if (this.pushMessages.get(i).getId() == messageObject.getId() && this.pushMessages.get(i).getDialogId() == messageObject.getDialogId() && this.pushMessages.get(i).isStoryPush == messageObject.isStoryPush) {
                return;
            }
        }
        this.pushMessages.add(0, messageObject);
    }

    public static void checkOtherNotificationsChannel() {
        SharedPreferences sharedPreferences;
        NotificationChannel notificationChannel;
        int importance;
        if (Build.VERSION.SDK_INT < 26) {
            return;
        }
        if (OTHER_NOTIFICATIONS_CHANNEL == null) {
            sharedPreferences = ApplicationLoader.applicationContext.getSharedPreferences("Notifications", 0);
            OTHER_NOTIFICATIONS_CHANNEL = sharedPreferences.getString("OtherKey", "Other3");
        } else {
            sharedPreferences = null;
        }
        notificationChannel = systemNotificationManager.getNotificationChannel(OTHER_NOTIFICATIONS_CHANNEL);
        if (notificationChannel != null) {
            importance = notificationChannel.getImportance();
            if (importance == 0) {
                try {
                    systemNotificationManager.deleteNotificationChannel(OTHER_NOTIFICATIONS_CHANNEL);
                } catch (Exception e) {
                    FileLog.e(e);
                }
                OTHER_NOTIFICATIONS_CHANNEL = null;
                notificationChannel = null;
            }
        }
        if (OTHER_NOTIFICATIONS_CHANNEL == null) {
            if (sharedPreferences == null) {
                sharedPreferences = ApplicationLoader.applicationContext.getSharedPreferences("Notifications", 0);
            }
            OTHER_NOTIFICATIONS_CHANNEL = "Other" + Utilities.random.nextLong();
            sharedPreferences.edit().putString("OtherKey", OTHER_NOTIFICATIONS_CHANNEL).commit();
        }
        if (notificationChannel == null) {
            NotificationChannel notificationChannel2 = new NotificationChannel(OTHER_NOTIFICATIONS_CHANNEL, "Internal notifications", 3);
            notificationChannel2.enableLights(false);
            notificationChannel2.enableVibration(false);
            notificationChannel2.setSound(null, null);
            try {
                systemNotificationManager.createNotificationChannel(notificationChannel2);
            } catch (Exception e2) {
                FileLog.e(e2);
            }
        }
    }

    public void checkStoryPushes() {
        long currentTimeMillis = System.currentTimeMillis();
        int i = 0;
        boolean z = false;
        while (i < this.storyPushMessages.size()) {
            StoryNotification storyNotification = this.storyPushMessages.get(i);
            Iterator<Map.Entry<Integer, Pair<Long, Long>>> it = storyNotification.dateByIds.entrySet().iterator();
            while (it.hasNext()) {
                if (currentTimeMillis >= ((Long) it.next().getValue().second).longValue()) {
                    it.remove();
                    z = true;
                }
            }
            if (z) {
                if (storyNotification.dateByIds.isEmpty()) {
                    getMessagesStorage().deleteStoryPushMessage(storyNotification.dialogId);
                    this.storyPushMessages.remove(i);
                    i--;
                } else {
                    getMessagesStorage().putStoryPushMessage(storyNotification);
                }
            }
            i++;
        }
        if (z) {
            showOrUpdateNotification(false);
        }
        updateStoryPushesRunnable();
    }

    private java.lang.String createNotificationShortcut(androidx.core.app.NotificationCompat.Builder r18, long r19, java.lang.String r21, org.telegram.tgnet.TLRPC.User r22, org.telegram.tgnet.TLRPC.Chat r23, androidx.core.app.Person r24, boolean r25) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.NotificationsController.createNotificationShortcut(androidx.core.app.NotificationCompat$Builder, long, java.lang.String, org.telegram.tgnet.TLRPC$User, org.telegram.tgnet.TLRPC$Chat, androidx.core.app.Person, boolean):java.lang.String");
    }

    private String cutLastName(String str) {
        if (str == null) {
            return null;
        }
        int indexOf = str.indexOf(32);
        if (indexOf < 0) {
            return str;
        }
        StringBuilder sb = new StringBuilder();
        sb.append(str.substring(0, indexOf));
        sb.append(str.endsWith("…") ? "…" : "");
        return sb.toString();
    }

    public void lambda$deleteNotificationChannel$39(long j, long j2, int i) {
        if (Build.VERSION.SDK_INT < 26) {
            return;
        }
        try {
            SharedPreferences notificationsSettings = getAccountInstance().getNotificationsSettings();
            SharedPreferences.Editor edit = notificationsSettings.edit();
            if (i == 0 || i == -1) {
                String str = "org.telegram.key" + j;
                if (j2 != 0) {
                    str = str + ".topic" + j2;
                }
                String string = notificationsSettings.getString(str, null);
                if (string != null) {
                    edit.remove(str).remove(str + "_s");
                    try {
                        systemNotificationManager.deleteNotificationChannel(string);
                    } catch (Exception e) {
                        FileLog.e(e);
                    }
                    if (BuildVars.LOGS_ENABLED) {
                        FileLog.d("delete channel internal " + string);
                    }
                }
            }
            if (i == 1 || i == -1) {
                String str2 = "org.telegram.keyia" + j;
                String string2 = notificationsSettings.getString(str2, null);
                if (string2 != null) {
                    edit.remove(str2).remove(str2 + "_s");
                    try {
                        systemNotificationManager.deleteNotificationChannel(string2);
                    } catch (Exception e2) {
                        FileLog.e(e2);
                    }
                    if (BuildVars.LOGS_ENABLED) {
                        FileLog.d("delete channel internal " + string2);
                    }
                }
            }
            edit.commit();
        } catch (Exception e3) {
            FileLog.e(e3);
        }
    }

    private void dismissNotification() {
        FileLog.d("NotificationsController dismissNotification");
        try {
            notificationManager.cancel(this.notificationId);
            this.pushMessages.clear();
            this.pushMessagesDict.clear();
            this.lastWearNotifiedMessageId.clear();
            for (int i = 0; i < this.wearNotificationsIds.size(); i++) {
                if (!this.openedInBubbleDialogs.contains(Long.valueOf(this.wearNotificationsIds.keyAt(i)))) {
                    notificationManager.cancel(((Integer) this.wearNotificationsIds.valueAt(i)).intValue());
                }
            }
            this.wearNotificationsIds.clear();
            AndroidUtilities.runOnUIThread(new Runnable() {
                @Override
                public final void run() {
                    NotificationsController.lambda$dismissNotification$35();
                }
            });
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    public static String getGlobalNotificationsKey(int i) {
        return i == 0 ? "EnableGroup2" : i == 1 ? "EnableAll2" : "EnableChannel2";
    }

    private TLRPC.NotificationSound getInputSound(SharedPreferences sharedPreferences, String str, String str2, String str3) {
        long j = sharedPreferences.getLong(str2, 0L);
        String string = sharedPreferences.getString(str3, "NoSound");
        if (j != 0) {
            TLRPC.TL_notificationSoundRingtone tL_notificationSoundRingtone = new TLRPC.TL_notificationSoundRingtone();
            tL_notificationSoundRingtone.id = j;
            return tL_notificationSoundRingtone;
        }
        if (string == null) {
            return new TLRPC.TL_notificationSoundDefault();
        }
        if (string.equalsIgnoreCase("NoSound")) {
            return new TLRPC.TL_notificationSoundNone();
        }
        TLRPC.TL_notificationSoundLocal tL_notificationSoundLocal = new TLRPC.TL_notificationSoundLocal();
        tL_notificationSoundLocal.title = sharedPreferences.getString(str, null);
        tL_notificationSoundLocal.data = string;
        return tL_notificationSoundLocal;
    }

    public static NotificationsController getInstance(int i) {
        NotificationsController notificationsController = Instance[i];
        if (notificationsController == null) {
            synchronized (lockObjects[i]) {
                try {
                    notificationsController = Instance[i];
                    if (notificationsController == null) {
                        NotificationsController[] notificationsControllerArr = Instance;
                        NotificationsController notificationsController2 = new NotificationsController(i);
                        notificationsControllerArr[i] = notificationsController2;
                        notificationsController = notificationsController2;
                    }
                } finally {
                }
            }
        }
        return notificationsController;
    }

    private int getNotifyOverride(SharedPreferences sharedPreferences, long j, long j2) {
        int property = this.dialogsNotificationsFacade.getProperty("notify2_", j, j2, -1);
        if (property != 3 || this.dialogsNotificationsFacade.getProperty("notifyuntil_", j, j2, 0) < getConnectionsManager().getCurrentTime()) {
            return property;
        }
        return 2;
    }

    public static String getSharedPrefKey(long j, long j2) {
        return getSharedPrefKey(j, j2, false);
    }

    public static String getSharedPrefKey(long j, long j2, boolean z) {
        if (z) {
            return j2 != 0 ? String.format(Locale.US, "%d_%d", Long.valueOf(j), Long.valueOf(j2)) : String.valueOf(j);
        }
        long j3 = (j2 << 12) + j;
        LongSparseArray longSparseArray = sharedPrefCachedKeys;
        int indexOfKey = longSparseArray.indexOfKey(j3);
        if (indexOfKey >= 0) {
            return (String) longSparseArray.valueAt(indexOfKey);
        }
        String format = j2 != 0 ? String.format(Locale.US, "%d_%d", Long.valueOf(j), Long.valueOf(j2)) : String.valueOf(j);
        longSparseArray.put(j3, format);
        return format;
    }

    private java.lang.String getShortStringForMessage(org.telegram.messenger.MessageObject r23, java.lang.String[] r24, boolean[] r25) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.NotificationsController.getShortStringForMessage(org.telegram.messenger.MessageObject, java.lang.String[], boolean[]):java.lang.String");
    }

    private java.lang.String getStringForMessage(org.telegram.messenger.MessageObject r28, boolean r29, boolean[] r30, boolean[] r31) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.NotificationsController.getStringForMessage(org.telegram.messenger.MessageObject, boolean, boolean[], boolean[]):java.lang.String");
    }

    private int getTotalAllUnreadCount() {
        int size;
        int i = 0;
        for (int i2 = 0; i2 < 4; i2++) {
            if (UserConfig.getInstance(i2).isClientActivated() && (SharedConfig.showNotificationsForAllAccounts || UserConfig.selectedAccount == i2)) {
                NotificationsController notificationsController = getInstance(i2);
                if (notificationsController.showBadgeNumber) {
                    if (notificationsController.showBadgeMessages) {
                        if (notificationsController.showBadgeMuted) {
                            try {
                                ArrayList arrayList = new ArrayList(MessagesController.getInstance(i2).allDialogs);
                                int size2 = arrayList.size();
                                for (int i3 = 0; i3 < size2; i3++) {
                                    TLRPC.Dialog dialog = (TLRPC.Dialog) arrayList.get(i3);
                                    if ((dialog == null || !DialogObject.isChatDialog(dialog.id) || !ChatObject.isNotInChat(getMessagesController().getChat(Long.valueOf(-dialog.id)))) && dialog != null) {
                                        i += MessagesController.getInstance(i2).getDialogUnreadCount(dialog);
                                    }
                                }
                            } catch (Exception e) {
                                FileLog.e(e);
                            }
                        } else {
                            size = notificationsController.total_unread_count;
                        }
                    } else if (notificationsController.showBadgeMuted) {
                        try {
                            int size3 = MessagesController.getInstance(i2).allDialogs.size();
                            for (int i4 = 0; i4 < size3; i4++) {
                                TLRPC.Dialog dialog2 = MessagesController.getInstance(i2).allDialogs.get(i4);
                                if ((!DialogObject.isChatDialog(dialog2.id) || !ChatObject.isNotInChat(getMessagesController().getChat(Long.valueOf(-dialog2.id)))) && MessagesController.getInstance(i2).getDialogUnreadCount(dialog2) != 0) {
                                    i++;
                                }
                            }
                        } catch (Exception e2) {
                            FileLog.e((Throwable) e2, false);
                        }
                    } else {
                        size = notificationsController.pushDialogs.size();
                    }
                    i += size;
                }
            }
        }
        return i;
    }

    private boolean isEmptyVibration(long[] jArr) {
        if (jArr == null || jArr.length == 0) {
            return false;
        }
        for (long j : jArr) {
            if (j != 0) {
                return false;
            }
        }
        return true;
    }

    private boolean isPersonalMessage(MessageObject messageObject) {
        TLRPC.MessageAction messageAction;
        TLRPC.Message message = messageObject.messageOwner;
        TLRPC.Peer peer = message.peer_id;
        return (peer != null && peer.chat_id == 0 && peer.channel_id == 0 && ((messageAction = message.action) == null || (messageAction instanceof TLRPC.TL_messageActionEmpty))) || messageObject.isStoryReactionPush;
    }

    private boolean isSilentMessage(MessageObject messageObject) {
        return messageObject.messageOwner.silent || messageObject.isReactionPush;
    }

    public void lambda$cleanup$1() {
        List notificationChannels;
        String id;
        this.openedDialogId = 0L;
        this.openedTopicId = 0L;
        this.total_unread_count = 0;
        this.personalCount = 0;
        this.pushMessages.clear();
        this.pushMessagesDict.clear();
        this.fcmRandomMessagesDict.clear();
        this.pushDialogs.clear();
        this.wearNotificationsIds.clear();
        this.lastWearNotifiedMessageId.clear();
        this.openedInBubbleDialogs.clear();
        this.delayedPushMessages.clear();
        this.notifyCheck = false;
        this.lastBadgeCount = 0;
        try {
            if (this.notificationDelayWakelock.isHeld()) {
                this.notificationDelayWakelock.release();
            }
        } catch (Exception e) {
            FileLog.e(e);
        }
        dismissNotification();
        setBadge(getTotalAllUnreadCount());
        SharedPreferences.Editor edit = getAccountInstance().getNotificationsSettings().edit();
        edit.clear();
        edit.commit();
        if (Build.VERSION.SDK_INT >= 26) {
            try {
                systemNotificationManager.deleteNotificationChannelGroup("channels" + this.currentAccount);
                systemNotificationManager.deleteNotificationChannelGroup("groups" + this.currentAccount);
                systemNotificationManager.deleteNotificationChannelGroup("private" + this.currentAccount);
                systemNotificationManager.deleteNotificationChannelGroup("stories" + this.currentAccount);
                systemNotificationManager.deleteNotificationChannelGroup("other" + this.currentAccount);
                String str = this.currentAccount + "channel";
                notificationChannels = systemNotificationManager.getNotificationChannels();
                int size = notificationChannels.size();
                for (int i = 0; i < size; i++) {
                    id = NotificationsController$$ExternalSyntheticApiModelOutline13.m(notificationChannels.get(i)).getId();
                    if (id.startsWith(str)) {
                        try {
                            systemNotificationManager.deleteNotificationChannel(id);
                        } catch (Exception e2) {
                            FileLog.e(e2);
                        }
                        if (BuildVars.LOGS_ENABLED) {
                            FileLog.d("delete channel cleanup " + id);
                        }
                    }
                }
            } catch (Throwable th) {
                FileLog.e(th);
            }
        }
    }

    public void lambda$deleteAllNotificationChannels$41() {
        try {
            SharedPreferences notificationsSettings = getAccountInstance().getNotificationsSettings();
            Map<String, ?> all = notificationsSettings.getAll();
            SharedPreferences.Editor edit = notificationsSettings.edit();
            for (Map.Entry<String, ?> entry : all.entrySet()) {
                String key = entry.getKey();
                if (key.startsWith("org.telegram.key")) {
                    if (!key.endsWith("_s")) {
                        String str = (String) entry.getValue();
                        systemNotificationManager.deleteNotificationChannel(str);
                        if (BuildVars.LOGS_ENABLED) {
                            FileLog.d("delete all channel " + str);
                        }
                    }
                    edit.remove(key);
                }
            }
            edit.commit();
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    public static void lambda$dismissNotification$35() {
        NotificationCenter.getGlobalInstance().lambda$postNotificationNameOnUIThread$1(NotificationCenter.pushMessagesUpdated, new Object[0]);
    }

    public void lambda$forceShowPopupForReply$5(ArrayList arrayList) {
        this.popupReplyMessages = arrayList;
        Intent intent = new Intent(ApplicationLoader.applicationContext, (Class<?>) PopupNotificationActivity.class);
        intent.putExtra("force", true);
        intent.putExtra("currentAccount", this.currentAccount);
        intent.setFlags(268763140);
        ApplicationLoader.applicationContext.startActivity(intent);
        ApplicationLoader.applicationContext.sendBroadcast(new Intent("android.intent.action.CLOSE_SYSTEM_DIALOGS"));
    }

    public void lambda$forceShowPopupForReply$6() {
        final ArrayList arrayList = new ArrayList();
        for (int i = 0; i < this.pushMessages.size(); i++) {
            MessageObject messageObject = this.pushMessages.get(i);
            long dialogId = messageObject.getDialogId();
            TLRPC.Message message = messageObject.messageOwner;
            if ((!message.mentioned || !(message.action instanceof TLRPC.TL_messageActionPinMessage)) && !DialogObject.isEncryptedDialog(dialogId) && (messageObject.messageOwner.peer_id.channel_id == 0 || messageObject.isSupergroup())) {
                arrayList.add(0, messageObject);
            }
        }
        if (arrayList.isEmpty() || AndroidUtilities.needShowPasscode() || SharedConfig.isWaitingForPasscodeEnter) {
            return;
        }
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                NotificationsController.this.lambda$forceShowPopupForReply$5(arrayList);
            }
        });
    }

    public void lambda$hideNotifications$34() {
        notificationManager.cancel(this.notificationId);
        this.lastWearNotifiedMessageId.clear();
        for (int i = 0; i < this.wearNotificationsIds.size(); i++) {
            notificationManager.cancel(((Integer) this.wearNotificationsIds.valueAt(i)).intValue());
        }
        this.wearNotificationsIds.clear();
    }

    public static int lambda$loadRoundAvatar$43(Canvas canvas) {
        Path path = new Path();
        path.setFillType(Path.FillType.INVERSE_EVEN_ODD);
        int width = canvas.getWidth();
        float f = width / 2;
        path.addRoundRect(0.0f, 0.0f, width, canvas.getHeight(), f, f, Path.Direction.CW);
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setColor(0);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC));
        canvas.drawPath(path, paint);
        return -3;
    }

    public static void lambda$loadRoundAvatar$44(ImageDecoder imageDecoder, ImageDecoder.ImageInfo imageInfo, ImageDecoder.Source source) {
        imageDecoder.setPostProcessor(new PostProcessor() {
            @Override
            public final int onPostProcess(Canvas canvas) {
                int lambda$loadRoundAvatar$43;
                lambda$loadRoundAvatar$43 = NotificationsController.lambda$loadRoundAvatar$43(canvas);
                return lambda$loadRoundAvatar$43;
            }
        });
    }

    public static void lambda$loadTopicsNotificationsExceptions$50(Consumer consumer, HashSet hashSet) {
        if (consumer != null) {
            consumer.r(hashSet);
        }
    }

    public void lambda$loadTopicsNotificationsExceptions$51(long j, final Consumer consumer) {
        final HashSet hashSet = new HashSet();
        Iterator<Map.Entry<String, ?>> it = MessagesController.getNotificationsSettings(this.currentAccount).getAll().entrySet().iterator();
        while (it.hasNext()) {
            String key = it.next().getKey();
            if (key.startsWith("notify2_" + j)) {
                Integer parseInt = Utilities.parseInt((CharSequence) key.replace("notify2_" + j, ""));
                int intValue = parseInt.intValue();
                if (intValue != 0 && getMessagesController().isDialogMuted(j, intValue) != getMessagesController().isDialogMuted(j, 0L)) {
                    hashSet.add(parseInt);
                }
            }
        }
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                NotificationsController.lambda$loadTopicsNotificationsExceptions$50(Consumer.this, hashSet);
            }
        });
    }

    public void lambda$new$0() {
        if (BuildVars.LOGS_ENABLED) {
            FileLog.d("delay reached");
        }
        if (!this.delayedPushMessages.isEmpty()) {
            showOrUpdateNotification(true);
            this.delayedPushMessages.clear();
        }
        try {
            if (this.notificationDelayWakelock.isHeld()) {
                this.notificationDelayWakelock.release();
            }
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    public static void lambda$playInChatSound$36(SoundPool soundPool, int i, int i2) {
        if (i2 == 0) {
            try {
                soundPool.play(i, 1.0f, 1.0f, 1, 0, 1.0f);
            } catch (Exception e) {
                FileLog.e(e);
            }
        }
    }

    public void lambda$playInChatSound$37() {
        if (Math.abs(SystemClock.elapsedRealtime() - this.lastSoundPlay) <= 500) {
            return;
        }
        try {
            if (this.soundPool == null) {
                SoundPool soundPool = new SoundPool(3, 1, 0);
                this.soundPool = soundPool;
                soundPool.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {
                    @Override
                    public final void onLoadComplete(SoundPool soundPool2, int i, int i2) {
                        NotificationsController.lambda$playInChatSound$36(soundPool2, i, i2);
                    }
                });
            }
            if (this.soundIn == 0 && !this.soundInLoaded) {
                this.soundInLoaded = true;
                this.soundIn = this.soundPool.load(ApplicationLoader.applicationContext, R.raw.sound_in, 1);
            }
            int i = this.soundIn;
            if (i != 0) {
                try {
                    this.soundPool.play(i, 1.0f, 1.0f, 1, 0, 1.0f);
                } catch (Exception e) {
                    FileLog.e(e);
                }
            }
        } catch (Exception e2) {
            FileLog.e(e2);
        }
    }

    public static void lambda$playOutChatSound$45(SoundPool soundPool, int i, int i2) {
        if (i2 == 0) {
            try {
                soundPool.play(i, 1.0f, 1.0f, 1, 0, 1.0f);
            } catch (Exception e) {
                FileLog.e(e);
            }
        }
    }

    public void lambda$playOutChatSound$46() {
        try {
            if (Math.abs(SystemClock.elapsedRealtime() - this.lastSoundOutPlay) <= 100) {
                return;
            }
            this.lastSoundOutPlay = SystemClock.elapsedRealtime();
            if (this.soundPool == null) {
                SoundPool soundPool = new SoundPool(3, 1, 0);
                this.soundPool = soundPool;
                soundPool.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {
                    @Override
                    public final void onLoadComplete(SoundPool soundPool2, int i, int i2) {
                        NotificationsController.lambda$playOutChatSound$45(soundPool2, i, i2);
                    }
                });
            }
            if (this.soundOut == 0 && !this.soundOutLoaded) {
                this.soundOutLoaded = true;
                this.soundOut = this.soundPool.load(ApplicationLoader.applicationContext, R.raw.sound_out, 1);
            }
            int i = this.soundOut;
            if (i != 0) {
                try {
                    this.soundPool.play(i, 1.0f, 1.0f, 1, 0, 1.0f);
                } catch (Exception e) {
                    FileLog.e(e);
                }
            }
        } catch (Exception e2) {
            FileLog.e(e2);
        }
    }

    public void lambda$processDeleteStory$14(long j, int i) {
        StoryNotification storyNotification = (StoryNotification) this.storyPushMessagesDict.get(j);
        if (storyNotification != null) {
            storyNotification.dateByIds.remove(Integer.valueOf(i));
            if (!storyNotification.dateByIds.isEmpty()) {
                getMessagesStorage().putStoryPushMessage(storyNotification);
                return;
            }
            this.storyPushMessagesDict.remove(j);
            this.storyPushMessages.remove(storyNotification);
            getMessagesStorage().deleteStoryPushMessage(j);
            showOrUpdateNotification(false);
        }
    }

    public void lambda$processDialogsUpdateRead$26(ArrayList arrayList) {
        int size = arrayList.size();
        for (int i = 0; i < size; i++) {
            this.popupMessages.remove(arrayList.get(i));
        }
        NotificationCenter.getGlobalInstance().lambda$postNotificationNameOnUIThread$1(NotificationCenter.pushMessagesUpdated, new Object[0]);
    }

    public void lambda$processDialogsUpdateRead$27(int i) {
        NotificationCenter.getGlobalInstance().lambda$postNotificationNameOnUIThread$1(NotificationCenter.notificationsCountUpdated, Integer.valueOf(this.currentAccount));
        getNotificationCenter().lambda$postNotificationNameOnUIThread$1(NotificationCenter.dialogsUnreadCounterChanged, Integer.valueOf(i));
    }

    public void lambda$processDialogsUpdateRead$28(org.telegram.messenger.support.LongSparseIntArray r19, final java.util.ArrayList r20) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.NotificationsController.lambda$processDialogsUpdateRead$28(org.telegram.messenger.support.LongSparseIntArray, java.util.ArrayList):void");
    }

    public void lambda$processEditedMessages$21(LongSparseArray longSparseArray) {
        long j;
        int size = longSparseArray.size();
        boolean z = false;
        for (int i = 0; i < size; i++) {
            longSparseArray.keyAt(i);
            ArrayList arrayList = (ArrayList) longSparseArray.valueAt(i);
            int size2 = arrayList.size();
            for (int i2 = 0; i2 < size2; i2++) {
                MessageObject messageObject = (MessageObject) arrayList.get(i2);
                if (messageObject.isStoryReactionPush) {
                    j = messageObject.getDialogId();
                } else {
                    long j2 = messageObject.messageOwner.peer_id.channel_id;
                    j = j2 != 0 ? -j2 : 0L;
                }
                SparseArray sparseArray = (SparseArray) this.pushMessagesDict.get(j);
                if (sparseArray == null) {
                    break;
                }
                MessageObject messageObject2 = (MessageObject) sparseArray.get(messageObject.getId());
                if (messageObject2 != null && (messageObject2.isReactionPush || messageObject2.isStoryReactionPush)) {
                    messageObject2 = null;
                }
                if (messageObject2 != null) {
                    sparseArray.put(messageObject.getId(), messageObject);
                    int indexOf = this.pushMessages.indexOf(messageObject2);
                    if (indexOf >= 0) {
                        this.pushMessages.set(indexOf, messageObject);
                    }
                    int indexOf2 = this.delayedPushMessages.indexOf(messageObject2);
                    if (indexOf2 >= 0) {
                        this.delayedPushMessages.set(indexOf2, messageObject);
                    }
                    z = true;
                }
            }
        }
        if (z) {
            showOrUpdateNotification(false);
        }
    }

    public void lambda$processIgnoreStories$16() {
        boolean z = !this.storyPushMessages.isEmpty();
        this.storyPushMessages.clear();
        this.storyPushMessagesDict.clear();
        getMessagesStorage().deleteAllStoryPushMessages();
        if (z) {
            showOrUpdateNotification(false);
        }
    }

    public void lambda$processIgnoreStories$18(long j) {
        boolean z = !this.storyPushMessages.isEmpty();
        this.storyPushMessages.clear();
        this.storyPushMessagesDict.clear();
        getMessagesStorage().deleteStoryPushMessage(j);
        if (z) {
            showOrUpdateNotification(false);
        }
    }

    public void lambda$processIgnoreStoryReactions$17() {
        int i = 0;
        boolean z = false;
        while (i < this.pushMessages.size()) {
            MessageObject messageObject = this.pushMessages.get(i);
            if (messageObject != null && messageObject.isStoryReactionPush) {
                this.pushMessages.remove(i);
                i--;
                SparseArray sparseArray = (SparseArray) this.pushMessagesDict.get(messageObject.getDialogId());
                if (sparseArray != null) {
                    sparseArray.remove(messageObject.getId());
                }
                if (sparseArray != null && sparseArray.size() <= 0) {
                    this.pushMessagesDict.remove(messageObject.getDialogId());
                }
                z = true;
            }
            i++;
        }
        getMessagesStorage().deleteAllStoryReactionPushMessages();
        if (z) {
            showOrUpdateNotification(false);
        }
    }

    public void lambda$processLoadedUnreadMessages$30(int i) {
        if (this.total_unread_count == 0) {
            this.popupMessages.clear();
            NotificationCenter.getGlobalInstance().lambda$postNotificationNameOnUIThread$1(NotificationCenter.pushMessagesUpdated, new Object[0]);
        }
        NotificationCenter.getGlobalInstance().lambda$postNotificationNameOnUIThread$1(NotificationCenter.notificationsCountUpdated, Integer.valueOf(this.currentAccount));
        getNotificationCenter().lambda$postNotificationNameOnUIThread$1(NotificationCenter.dialogsUnreadCounterChanged, Integer.valueOf(i));
    }

    public void lambda$processLoadedUnreadMessages$31(ArrayList arrayList, LongSparseArray longSparseArray, ArrayList arrayList2, Collection collection) {
        long j;
        long j2;
        boolean z;
        LongSparseArray longSparseArray2;
        int i;
        long j3;
        boolean z2;
        TLRPC.MessageFwdHeader messageFwdHeader;
        SharedPreferences sharedPreferences;
        MessageObject messageObject;
        SparseArray sparseArray;
        long j4;
        long j5;
        int i2;
        TLRPC.Message message;
        boolean isGlobalNotificationsEnabled;
        SparseArray sparseArray2;
        ArrayList arrayList3 = arrayList;
        this.pushDialogs.clear();
        this.pushMessages.clear();
        this.pushMessagesDict.clear();
        this.storyPushMessages.clear();
        this.storyPushMessagesDict.clear();
        boolean z3 = false;
        this.total_unread_count = 0;
        this.personalCount = 0;
        SharedPreferences notificationsSettings = getAccountInstance().getNotificationsSettings();
        LongSparseArray longSparseArray3 = new LongSparseArray();
        long j6 = 0;
        if (arrayList3 != null) {
            int i3 = 0;
            while (i3 < arrayList.size()) {
                TLRPC.Message message2 = (TLRPC.Message) arrayList3.get(i3);
                if (message2 != null && ((messageFwdHeader = message2.fwd_from) == null || !messageFwdHeader.imported)) {
                    TLRPC.MessageAction messageAction = message2.action;
                    if (!(messageAction instanceof TLRPC.TL_messageActionSetMessagesTTL) && (!message2.silent || (!(messageAction instanceof TLRPC.TL_messageActionContactSignUp) && !(messageAction instanceof TLRPC.TL_messageActionUserJoined)))) {
                        long j7 = message2.peer_id.channel_id;
                        long j8 = j7 != j6 ? -j7 : j6;
                        SparseArray sparseArray3 = (SparseArray) this.pushMessagesDict.get(j8);
                        if (sparseArray3 == null || sparseArray3.indexOfKey(message2.id) < 0) {
                            MessageObject messageObject2 = new MessageObject(this.currentAccount, message2, z3, z3);
                            if (isPersonalMessage(messageObject2)) {
                                this.personalCount++;
                            }
                            sharedPreferences = notificationsSettings;
                            long dialogId = messageObject2.getDialogId();
                            long topicId = MessageObject.getTopicId(this.currentAccount, messageObject2.messageOwner, getMessagesController().isForum(messageObject2));
                            long fromChatId = messageObject2.messageOwner.mentioned ? messageObject2.getFromChatId() : dialogId;
                            int indexOfKey = longSparseArray3.indexOfKey(fromChatId);
                            if (indexOfKey < 0 || topicId != 0) {
                                messageObject = messageObject2;
                                sparseArray = sparseArray3;
                                j4 = dialogId;
                                j5 = j8;
                                i2 = i3;
                                message = message2;
                                int notifyOverride = getNotifyOverride(sharedPreferences, fromChatId, topicId);
                                isGlobalNotificationsEnabled = notifyOverride == -1 ? isGlobalNotificationsEnabled(fromChatId, messageObject.isReactionPush, messageObject.isStoryReactionPush) : notifyOverride != 2;
                                longSparseArray3.put(fromChatId, Boolean.valueOf(isGlobalNotificationsEnabled));
                            } else {
                                isGlobalNotificationsEnabled = ((Boolean) longSparseArray3.valueAt(indexOfKey)).booleanValue();
                                messageObject = messageObject2;
                                sparseArray = sparseArray3;
                                i2 = i3;
                                j4 = dialogId;
                                j5 = j8;
                                message = message2;
                            }
                            if (isGlobalNotificationsEnabled && (fromChatId != this.openedDialogId || !ApplicationLoader.isScreenOn)) {
                                if (sparseArray == null) {
                                    sparseArray2 = new SparseArray();
                                    this.pushMessagesDict.put(j5, sparseArray2);
                                } else {
                                    sparseArray2 = sparseArray;
                                }
                                sparseArray2.put(message.id, messageObject);
                                appendMessage(messageObject);
                                if (j4 != fromChatId) {
                                    long j9 = j4;
                                    Integer num = (Integer) this.pushDialogsOverrideMention.get(j9);
                                    this.pushDialogsOverrideMention.put(j9, Integer.valueOf(num == null ? 1 : num.intValue() + 1));
                                }
                            }
                            i3 = i2 + 1;
                            arrayList3 = arrayList;
                            notificationsSettings = sharedPreferences;
                            z3 = false;
                            j6 = 0;
                        }
                    }
                }
                i2 = i3;
                sharedPreferences = notificationsSettings;
                i3 = i2 + 1;
                arrayList3 = arrayList;
                notificationsSettings = sharedPreferences;
                z3 = false;
                j6 = 0;
            }
        }
        SharedPreferences sharedPreferences2 = notificationsSettings;
        for (int i4 = 0; i4 < longSparseArray.size(); i4++) {
            long keyAt = longSparseArray.keyAt(i4);
            int indexOfKey2 = longSparseArray3.indexOfKey(keyAt);
            if (indexOfKey2 >= 0) {
                z2 = ((Boolean) longSparseArray3.valueAt(indexOfKey2)).booleanValue();
            } else {
                int notifyOverride2 = getNotifyOverride(sharedPreferences2, keyAt, 0L);
                boolean isGlobalNotificationsEnabled2 = notifyOverride2 == -1 ? isGlobalNotificationsEnabled(keyAt, false, false) : notifyOverride2 != 2;
                longSparseArray3.put(keyAt, Boolean.valueOf(isGlobalNotificationsEnabled2));
                z2 = isGlobalNotificationsEnabled2;
            }
            if (z2) {
                Integer num2 = (Integer) longSparseArray.valueAt(i4);
                int intValue = num2.intValue();
                this.pushDialogs.put(keyAt, num2);
                this.total_unread_count = getMessagesController().isForum(keyAt) ? this.total_unread_count + (intValue > 0 ? 1 : 0) : this.total_unread_count + intValue;
            }
        }
        if (arrayList2 != null) {
            int i5 = 0;
            while (i5 < arrayList2.size()) {
                MessageObject messageObject3 = (MessageObject) arrayList2.get(i5);
                int id = messageObject3.getId();
                if (this.pushMessagesDict.indexOfKey(id) < 0) {
                    if (isPersonalMessage(messageObject3)) {
                        this.personalCount++;
                    }
                    long dialogId2 = messageObject3.getDialogId();
                    long topicId2 = MessageObject.getTopicId(this.currentAccount, messageObject3.messageOwner, getMessagesController().isForum(messageObject3));
                    TLRPC.Message message3 = messageObject3.messageOwner;
                    long j10 = message3.random_id;
                    long fromChatId2 = message3.mentioned ? messageObject3.getFromChatId() : dialogId2;
                    int indexOfKey3 = longSparseArray3.indexOfKey(fromChatId2);
                    if (indexOfKey3 < 0 || topicId2 != 0) {
                        long j11 = fromChatId2;
                        j = j10;
                        int notifyOverride3 = getNotifyOverride(sharedPreferences2, j11, topicId2);
                        if (notifyOverride3 == -1) {
                            j2 = j11;
                            z = isGlobalNotificationsEnabled(j2, messageObject3.isReactionPush, messageObject3.isStoryReactionPush);
                        } else {
                            j2 = j11;
                            z = notifyOverride3 != 2;
                        }
                        longSparseArray3.put(j2, Boolean.valueOf(z));
                    } else {
                        j = j10;
                        z = ((Boolean) longSparseArray3.valueAt(indexOfKey3)).booleanValue();
                        j2 = fromChatId2;
                    }
                    if (z && (j2 != this.openedDialogId || !ApplicationLoader.isScreenOn)) {
                        if (id != 0) {
                            if (messageObject3.isStoryReactionPush) {
                                j3 = messageObject3.getDialogId();
                            } else {
                                long j12 = messageObject3.messageOwner.peer_id.channel_id;
                                j3 = j12 != 0 ? -j12 : 0L;
                            }
                            SparseArray sparseArray4 = (SparseArray) this.pushMessagesDict.get(j3);
                            if (sparseArray4 == null) {
                                sparseArray4 = new SparseArray();
                                this.pushMessagesDict.put(j3, sparseArray4);
                            }
                            sparseArray4.put(id, messageObject3);
                            longSparseArray2 = longSparseArray3;
                        } else {
                            longSparseArray2 = longSparseArray3;
                            long j13 = j;
                            if (j13 != 0) {
                                this.fcmRandomMessagesDict.put(j13, messageObject3);
                            }
                        }
                        appendMessage(messageObject3);
                        if (dialogId2 != j2) {
                            Integer num3 = (Integer) this.pushDialogsOverrideMention.get(dialogId2);
                            this.pushDialogsOverrideMention.put(dialogId2, Integer.valueOf(num3 == null ? 1 : num3.intValue() + 1));
                        }
                        Integer num4 = (Integer) this.pushDialogs.get(j2);
                        int intValue2 = num4 != null ? num4.intValue() + 1 : 1;
                        if (getMessagesController().isForum(j2)) {
                            if (num4 != null) {
                                this.total_unread_count -= num4.intValue() > 0 ? 1 : 0;
                            }
                            i = this.total_unread_count + (intValue2 > 0 ? 1 : 0);
                        } else {
                            if (num4 != null) {
                                this.total_unread_count -= num4.intValue();
                            }
                            i = this.total_unread_count + intValue2;
                        }
                        this.total_unread_count = i;
                        this.pushDialogs.put(j2, Integer.valueOf(intValue2));
                        i5++;
                        longSparseArray3 = longSparseArray2;
                    }
                }
                longSparseArray2 = longSparseArray3;
                i5++;
                longSparseArray3 = longSparseArray2;
            }
        }
        if (collection != null) {
            Iterator it = collection.iterator();
            while (it.hasNext()) {
                StoryNotification storyNotification = (StoryNotification) it.next();
                long j14 = storyNotification.dialogId;
                StoryNotification storyNotification2 = (StoryNotification) this.storyPushMessagesDict.get(j14);
                if (storyNotification2 != null) {
                    storyNotification2.dateByIds.putAll(storyNotification.dateByIds);
                } else {
                    this.storyPushMessages.add(storyNotification);
                    this.storyPushMessagesDict.put(j14, storyNotification);
                }
            }
            Collections.sort(this.storyPushMessages, Comparator$CC.comparingLong(new ToLongFunction() {
                @Override
                public final long applyAsLong(Object obj) {
                    long j15;
                    j15 = ((NotificationsController.StoryNotification) obj).date;
                    return j15;
                }
            }));
        }
        final int size = this.pushDialogs.size();
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                NotificationsController.this.lambda$processLoadedUnreadMessages$30(size);
            }
        });
        showOrUpdateNotification(SystemClock.elapsedRealtime() / 1000 < 60);
        if (this.showBadgeNumber) {
            setBadge(getTotalAllUnreadCount());
        }
    }

    public void lambda$processNewMessages$23(ArrayList arrayList, int i) {
        this.popupMessages.addAll(0, arrayList);
        if (ApplicationLoader.mainInterfacePaused || !ApplicationLoader.isScreenOn) {
            if (i == 3 || ((i == 1 && ApplicationLoader.isScreenOn) || (i == 2 && !ApplicationLoader.isScreenOn))) {
                Intent intent = new Intent(ApplicationLoader.applicationContext, (Class<?>) PopupNotificationActivity.class);
                intent.setFlags(268763140);
                try {
                    ApplicationLoader.applicationContext.startActivity(intent);
                } catch (Throwable unused) {
                }
            }
        }
    }

    public void lambda$processNewMessages$24(int i) {
        NotificationCenter.getGlobalInstance().lambda$postNotificationNameOnUIThread$1(NotificationCenter.notificationsCountUpdated, Integer.valueOf(this.currentAccount));
        getNotificationCenter().lambda$postNotificationNameOnUIThread$1(NotificationCenter.dialogsUnreadCounterChanged, Integer.valueOf(i));
    }

    public void lambda$processNewMessages$25(java.util.ArrayList r42, final java.util.ArrayList r43, boolean r44, boolean r45, java.util.concurrent.CountDownLatch r46) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.NotificationsController.lambda$processNewMessages$25(java.util.ArrayList, java.util.ArrayList, boolean, boolean, java.util.concurrent.CountDownLatch):void");
    }

    public void lambda$processReadMessages$19(ArrayList arrayList) {
        int size = arrayList.size();
        for (int i = 0; i < size; i++) {
            this.popupMessages.remove(arrayList.get(i));
        }
        NotificationCenter.getGlobalInstance().lambda$postNotificationNameOnUIThread$1(NotificationCenter.pushMessagesUpdated, new Object[0]);
    }

    public void lambda$processReadMessages$20(org.telegram.messenger.support.LongSparseIntArray r18, final java.util.ArrayList r19, long r20, int r22, int r23, boolean r24) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.NotificationsController.lambda$processReadMessages$20(org.telegram.messenger.support.LongSparseIntArray, java.util.ArrayList, long, int, int, boolean):void");
    }

    public void lambda$processReadStories$15(long j) {
        StoryNotification storyNotification = (StoryNotification) this.storyPushMessagesDict.get(j);
        if (storyNotification != null) {
            this.storyPushMessagesDict.remove(j);
            this.storyPushMessages.remove(storyNotification);
            getMessagesStorage().deleteStoryPushMessage(j);
            showOrUpdateNotification(false);
            updateStoryPushesRunnable();
        }
    }

    public void lambda$processSeenStoryReactions$13(int i) {
        int i2 = 0;
        boolean z = false;
        while (i2 < this.pushMessages.size()) {
            MessageObject messageObject = this.pushMessages.get(i2);
            if (messageObject.isStoryReactionPush && Math.abs(messageObject.getId()) == i) {
                this.pushMessages.remove(i2);
                SparseArray sparseArray = (SparseArray) this.pushMessagesDict.get(messageObject.getDialogId());
                if (sparseArray != null) {
                    sparseArray.remove(messageObject.getId());
                }
                if (sparseArray != null && sparseArray.size() <= 0) {
                    this.pushMessagesDict.remove(messageObject.getDialogId());
                }
                ArrayList<Integer> arrayList = new ArrayList<>();
                arrayList.add(Integer.valueOf(messageObject.getId()));
                getMessagesStorage().deletePushMessages(messageObject.getDialogId(), arrayList);
                i2--;
                z = true;
            }
            i2++;
        }
        if (z) {
            showOrUpdateNotification(false);
        }
    }

    public void lambda$removeDeletedHisoryFromNotifications$10(ArrayList arrayList) {
        int size = arrayList.size();
        for (int i = 0; i < size; i++) {
            this.popupMessages.remove(arrayList.get(i));
        }
        NotificationCenter.getGlobalInstance().lambda$postNotificationNameOnUIThread$1(NotificationCenter.pushMessagesUpdated, new Object[0]);
    }

    public void lambda$removeDeletedHisoryFromNotifications$11(int i) {
        NotificationCenter.getGlobalInstance().lambda$postNotificationNameOnUIThread$1(NotificationCenter.notificationsCountUpdated, Integer.valueOf(this.currentAccount));
        getNotificationCenter().lambda$postNotificationNameOnUIThread$1(NotificationCenter.dialogsUnreadCounterChanged, Integer.valueOf(i));
    }

    public void lambda$removeDeletedHisoryFromNotifications$12(LongSparseIntArray longSparseIntArray, final ArrayList arrayList) {
        int intValue;
        Integer num;
        int i = this.total_unread_count;
        getAccountInstance().getNotificationsSettings();
        Integer num2 = 0;
        int i2 = 0;
        while (true) {
            if (i2 >= longSparseIntArray.size()) {
                break;
            }
            long keyAt = longSparseIntArray.keyAt(i2);
            long j = -keyAt;
            long j2 = longSparseIntArray.get(keyAt);
            Integer num3 = (Integer) this.pushDialogs.get(j);
            if (num3 == null) {
                num3 = num2;
            }
            Integer num4 = num3;
            int i3 = 0;
            while (i3 < this.pushMessages.size()) {
                MessageObject messageObject = this.pushMessages.get(i3);
                if (messageObject.getDialogId() == j) {
                    num = num2;
                    if (messageObject.getId() <= j2) {
                        SparseArray sparseArray = (SparseArray) this.pushMessagesDict.get(j);
                        if (sparseArray != null) {
                            sparseArray.remove(messageObject.getId());
                            if (sparseArray.size() == 0) {
                                this.pushMessagesDict.remove(j);
                            }
                        }
                        this.delayedPushMessages.remove(messageObject);
                        this.pushMessages.remove(messageObject);
                        i3--;
                        if (isPersonalMessage(messageObject)) {
                            this.personalCount--;
                        }
                        arrayList.add(messageObject);
                        num4 = Integer.valueOf(num4.intValue() - 1);
                    }
                } else {
                    num = num2;
                }
                i3++;
                num2 = num;
            }
            Integer num5 = num2;
            if (num4.intValue() <= 0) {
                this.smartNotificationsDialogs.remove(j);
                num4 = num5;
            }
            if (!num4.equals(num3)) {
                if (getMessagesController().isForum(j)) {
                    int i4 = this.total_unread_count - (num3.intValue() > 0 ? 1 : 0);
                    this.total_unread_count = i4;
                    intValue = i4 + (num4.intValue() <= 0 ? 0 : 1);
                } else {
                    int intValue2 = this.total_unread_count - num3.intValue();
                    this.total_unread_count = intValue2;
                    intValue = intValue2 + num4.intValue();
                }
                this.total_unread_count = intValue;
                this.pushDialogs.put(j, num4);
            }
            if (num4.intValue() == 0) {
                this.pushDialogs.remove(j);
                this.pushDialogsOverrideMention.remove(j);
            }
            i2++;
            num2 = num5;
        }
        if (arrayList.isEmpty()) {
            AndroidUtilities.runOnUIThread(new Runnable() {
                @Override
                public final void run() {
                    NotificationsController.this.lambda$removeDeletedHisoryFromNotifications$10(arrayList);
                }
            });
        }
        if (i != this.total_unread_count) {
            if (this.notifyCheck) {
                scheduleNotificationDelay(this.lastOnlineFromOtherDevice > getConnectionsManager().getCurrentTime());
            } else {
                this.delayedPushMessages.clear();
                showOrUpdateNotification(this.notifyCheck);
            }
            final int size = this.pushDialogs.size();
            AndroidUtilities.runOnUIThread(new Runnable() {
                @Override
                public final void run() {
                    NotificationsController.this.lambda$removeDeletedHisoryFromNotifications$11(size);
                }
            });
        }
        this.notifyCheck = false;
        if (this.showBadgeNumber) {
            setBadge(getTotalAllUnreadCount());
        }
    }

    public void lambda$removeDeletedMessagesFromNotifications$7(ArrayList arrayList) {
        int size = arrayList.size();
        for (int i = 0; i < size; i++) {
            this.popupMessages.remove(arrayList.get(i));
        }
        NotificationCenter.getGlobalInstance().lambda$postNotificationNameOnUIThread$1(NotificationCenter.pushMessagesUpdated, new Object[0]);
    }

    public void lambda$removeDeletedMessagesFromNotifications$8(int i) {
        NotificationCenter.getGlobalInstance().lambda$postNotificationNameOnUIThread$1(NotificationCenter.notificationsCountUpdated, Integer.valueOf(this.currentAccount));
        getNotificationCenter().lambda$postNotificationNameOnUIThread$1(NotificationCenter.dialogsUnreadCounterChanged, Integer.valueOf(i));
    }

    public void lambda$removeDeletedMessagesFromNotifications$9(LongSparseArray longSparseArray, boolean z, final ArrayList arrayList) {
        long j;
        Integer num;
        int intValue;
        int intValue2;
        LongSparseArray longSparseArray2 = longSparseArray;
        int i = this.total_unread_count;
        getAccountInstance().getNotificationsSettings();
        int i2 = 0;
        while (i2 < longSparseArray.size()) {
            long keyAt = longSparseArray2.keyAt(i2);
            SparseArray sparseArray = (SparseArray) this.pushMessagesDict.get(keyAt);
            if (sparseArray != null) {
                ArrayList arrayList2 = (ArrayList) longSparseArray2.get(keyAt);
                int size = arrayList2.size();
                int i3 = 0;
                while (i3 < size) {
                    int intValue3 = ((Integer) arrayList2.get(i3)).intValue();
                    MessageObject messageObject = (MessageObject) sparseArray.get(intValue3);
                    if (messageObject == null || messageObject.isStoryReactionPush || (z && !messageObject.isReactionPush)) {
                        j = keyAt;
                    } else {
                        j = keyAt;
                        long dialogId = messageObject.getDialogId();
                        Integer num2 = (Integer) this.pushDialogs.get(dialogId);
                        if (num2 == null) {
                            num2 = 0;
                        }
                        int intValue4 = num2.intValue() - 1;
                        Integer valueOf = Integer.valueOf(intValue4);
                        if (intValue4 <= 0) {
                            this.smartNotificationsDialogs.remove(dialogId);
                            num = 0;
                        } else {
                            num = valueOf;
                        }
                        if (!num.equals(num2)) {
                            if (getMessagesController().isForum(dialogId)) {
                                intValue = this.total_unread_count - (num2.intValue() > 0 ? 1 : 0);
                                this.total_unread_count = intValue;
                                intValue2 = num.intValue() > 0 ? 1 : 0;
                            } else {
                                intValue = this.total_unread_count - num2.intValue();
                                this.total_unread_count = intValue;
                                intValue2 = num.intValue();
                            }
                            this.total_unread_count = intValue + intValue2;
                            this.pushDialogs.put(dialogId, num);
                        }
                        if (num.intValue() == 0) {
                            this.pushDialogs.remove(dialogId);
                            this.pushDialogsOverrideMention.remove(dialogId);
                        }
                        sparseArray.remove(intValue3);
                        this.delayedPushMessages.remove(messageObject);
                        this.pushMessages.remove(messageObject);
                        if (isPersonalMessage(messageObject)) {
                            this.personalCount--;
                        }
                        arrayList.add(messageObject);
                    }
                    i3++;
                    keyAt = j;
                }
                long j2 = keyAt;
                if (sparseArray.size() == 0) {
                    this.pushMessagesDict.remove(j2);
                }
            }
            i2++;
            longSparseArray2 = longSparseArray;
        }
        if (!arrayList.isEmpty()) {
            AndroidUtilities.runOnUIThread(new Runnable() {
                @Override
                public final void run() {
                    NotificationsController.this.lambda$removeDeletedMessagesFromNotifications$7(arrayList);
                }
            });
        }
        if (i != this.total_unread_count) {
            if (this.notifyCheck) {
                scheduleNotificationDelay(this.lastOnlineFromOtherDevice > getConnectionsManager().getCurrentTime());
            } else {
                this.delayedPushMessages.clear();
                showOrUpdateNotification(this.notifyCheck);
            }
            final int size2 = this.pushDialogs.size();
            AndroidUtilities.runOnUIThread(new Runnable() {
                @Override
                public final void run() {
                    NotificationsController.this.lambda$removeDeletedMessagesFromNotifications$8(size2);
                }
            });
        }
        this.notifyCheck = false;
        if (this.showBadgeNumber) {
            setBadge(getTotalAllUnreadCount());
        }
    }

    public void lambda$repeatNotificationMaybe$38() {
        int i = Calendar.getInstance().get(11);
        if (i < 11 || i > 22) {
            scheduleNotificationRepeat();
        } else {
            notificationManager.cancel(this.notificationId);
            showOrUpdateNotification(true);
        }
    }

    public void lambda$setLastOnlineFromOtherDevice$4(int i) {
        if (BuildVars.LOGS_ENABLED) {
            FileLog.d("set last online from other device = " + i);
        }
        this.lastOnlineFromOtherDevice = i;
    }

    public void lambda$setOpenedDialogId$2(long j, long j2) {
        this.openedDialogId = j;
        this.openedTopicId = j2;
    }

    public void lambda$setOpenedInBubble$3(boolean z, long j) {
        if (z) {
            this.openedInBubbleDialogs.add(Long.valueOf(j));
        } else {
            this.openedInBubbleDialogs.remove(Long.valueOf(j));
        }
    }

    public static void lambda$showExtraNotifications$42(Uri uri, File file) {
        try {
            ApplicationLoader.applicationContext.revokeUriPermission(uri, 1);
        } catch (Exception e) {
            FileLog.e(e);
        }
        if (file != null) {
            try {
                file.delete();
            } catch (Exception e2) {
                FileLog.e(e2);
            }
        }
    }

    public void lambda$showNotifications$33() {
        showOrUpdateNotification(false);
    }

    public void lambda$updateBadge$32() {
        setBadge(getTotalAllUnreadCount());
    }

    public static void lambda$updateServerNotificationsSettings$47(TLObject tLObject, TLRPC.TL_error tL_error) {
    }

    public static void lambda$updateServerNotificationsSettings$48(TLObject tLObject, TLRPC.TL_error tL_error) {
    }

    public static void lambda$updateServerNotificationsSettings$49(TLObject tLObject, TLRPC.TL_error tL_error) {
    }

    public static Bitmap loadMultipleAvatars(ArrayList<Object> arrayList) {
        int i;
        Bitmap bitmap;
        Paint paint;
        Rect rect;
        float f;
        int i2;
        TextPaint textPaint;
        float size;
        float size2;
        float f2;
        float f3;
        float f4;
        float f5;
        Object obj;
        ArrayList<Object> arrayList2 = arrayList;
        if (Build.VERSION.SDK_INT < 28 || arrayList2 == null || arrayList.size() == 0) {
            return null;
        }
        int dp = AndroidUtilities.dp(64.0f);
        Bitmap createBitmap = Bitmap.createBitmap(dp, dp, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(createBitmap);
        Matrix matrix = new Matrix();
        Paint paint2 = new Paint(3);
        Paint paint3 = new Paint(1);
        Rect rect2 = new Rect();
        paint3.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        float f6 = 1.0f;
        float f7 = arrayList.size() == 1 ? 1.0f : arrayList.size() == 2 ? 0.65f : 0.5f;
        int i3 = 0;
        TextPaint textPaint2 = null;
        while (i3 < arrayList.size()) {
            float f8 = dp;
            float f9 = (f6 - f7) * f8;
            try {
                size = ((arrayList.size() - 1) - i3) * (f9 / arrayList.size());
                size2 = i3 * (f9 / arrayList.size());
                f2 = f8 * f7;
                f3 = f2 / 2.0f;
                i = dp;
                f4 = size + f3;
                f = f7;
                f5 = size2 + f3;
                bitmap = createBitmap;
                try {
                    canvas.drawCircle(f4, f5, AndroidUtilities.dp(2.0f) + f3, paint3);
                    obj = arrayList2.get(i3);
                    paint = paint3;
                } catch (Throwable unused) {
                    paint = paint3;
                }
            } catch (Throwable unused2) {
                i = dp;
                bitmap = createBitmap;
                paint = paint3;
                rect = rect2;
                f = f7;
            }
            try {
            } catch (Throwable unused3) {
                rect = rect2;
                i2 = i3;
                textPaint = textPaint2;
                textPaint2 = textPaint;
                i3 = i2 + 1;
                arrayList2 = arrayList;
                rect2 = rect;
                dp = i;
                f7 = f;
                createBitmap = bitmap;
                paint3 = paint;
                f6 = 1.0f;
            }
            if (obj instanceof File) {
                String absolutePath = ((File) arrayList2.get(i3)).getAbsolutePath();
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inJustDecodeBounds = true;
                BitmapFactory.decodeFile(absolutePath, options);
                int i4 = (int) f2;
                options.inSampleSize = StoryEntry.calculateInSampleSize(options, i4, i4);
                options.inJustDecodeBounds = false;
                options.inDither = true;
                Bitmap decodeFile = BitmapFactory.decodeFile(absolutePath, options);
                Shader.TileMode tileMode = Shader.TileMode.CLAMP;
                BitmapShader bitmapShader = new BitmapShader(decodeFile, tileMode, tileMode);
                matrix.reset();
                matrix.postScale(f2 / decodeFile.getWidth(), f2 / decodeFile.getHeight());
                matrix.postTranslate(size, size2);
                bitmapShader.setLocalMatrix(matrix);
                paint2.setShader(bitmapShader);
                canvas.drawCircle(f4, f5, f3, paint2);
                decodeFile.recycle();
            } else if (obj instanceof TLRPC.User) {
                TLRPC.User user = (TLRPC.User) obj;
                Rect rect3 = rect2;
                try {
                    i2 = i3;
                    textPaint = textPaint2;
                    try {
                        try {
                            paint2.setShader(new LinearGradient(size, size2, size, size2 + f2, new int[]{Theme.getColor(Theme.keys_avatar_background[AvatarDrawable.getColorIndex(user.id)]), Theme.getColor(Theme.keys_avatar_background2[AvatarDrawable.getColorIndex(user.id)])}, new float[]{0.0f, 1.0f}, Shader.TileMode.CLAMP));
                            canvas.drawCircle(f4, f5, f3, paint2);
                            if (textPaint == null) {
                                TextPaint textPaint3 = new TextPaint(1);
                                try {
                                    textPaint3.setTypeface(AndroidUtilities.bold());
                                    textPaint3.setTextSize(f8 * 0.25f);
                                    textPaint3.setColor(-1);
                                    textPaint2 = textPaint3;
                                } catch (Throwable unused4) {
                                    textPaint2 = textPaint3;
                                    rect = rect3;
                                }
                            } else {
                                textPaint2 = textPaint;
                            }
                            try {
                                StringBuilder sb = new StringBuilder();
                                AvatarDrawable.getAvatarSymbols(user.first_name, user.last_name, null, sb);
                                String sb2 = sb.toString();
                                rect = rect3;
                                try {
                                    textPaint2.getTextBounds(sb2, 0, sb2.length(), rect);
                                    canvas.drawText(sb2, (f4 - (rect.width() / 2.0f)) - rect.left, (f5 - (rect.height() / 2.0f)) - rect.top, textPaint2);
                                } catch (Throwable unused5) {
                                }
                            } catch (Throwable unused6) {
                                rect = rect3;
                            }
                        } catch (Throwable unused7) {
                            rect = rect3;
                        }
                    } catch (Throwable unused8) {
                        rect = rect3;
                        textPaint2 = textPaint;
                        i3 = i2 + 1;
                        arrayList2 = arrayList;
                        rect2 = rect;
                        dp = i;
                        f7 = f;
                        createBitmap = bitmap;
                        paint3 = paint;
                        f6 = 1.0f;
                    }
                } catch (Throwable unused9) {
                    i2 = i3;
                    textPaint = textPaint2;
                }
                i3 = i2 + 1;
                arrayList2 = arrayList;
                rect2 = rect;
                dp = i;
                f7 = f;
                createBitmap = bitmap;
                paint3 = paint;
                f6 = 1.0f;
            }
            rect = rect2;
            i2 = i3;
            textPaint = textPaint2;
            textPaint2 = textPaint;
            i3 = i2 + 1;
            arrayList2 = arrayList;
            rect2 = rect;
            dp = i;
            f7 = f;
            createBitmap = bitmap;
            paint3 = paint;
            f6 = 1.0f;
        }
        return createBitmap;
    }

    public static Person.Builder loadRoundAvatar(File file, Person.Builder builder) {
        ImageDecoder.Source createSource;
        Bitmap decodeBitmap;
        if (file != null && Build.VERSION.SDK_INT >= 28) {
            try {
                createSource = ImageDecoder.createSource(file);
                decodeBitmap = ImageDecoder.decodeBitmap(createSource, new ImageDecoder.OnHeaderDecodedListener() {
                    @Override
                    public final void onHeaderDecoded(ImageDecoder imageDecoder, ImageDecoder.ImageInfo imageInfo, ImageDecoder.Source source) {
                        NotificationsController.lambda$loadRoundAvatar$44(imageDecoder, imageInfo, source);
                    }
                });
                builder.setIcon(IconCompat.createWithBitmap(decodeBitmap));
            } catch (Throwable unused) {
            }
        }
        return builder;
    }

    private Pair<Integer, Boolean> parseStoryPushes(ArrayList<String> arrayList, ArrayList<Object> arrayList2) {
        int i;
        String str;
        TLRPC.FileLocation fileLocation;
        int min = Math.min(3, this.storyPushMessages.size());
        boolean z = false;
        int i2 = 0;
        while (i < min) {
            StoryNotification storyNotification = this.storyPushMessages.get(i);
            i2 += storyNotification.dateByIds.size();
            z |= storyNotification.hidden;
            TLRPC.User user = getMessagesController().getUser(Long.valueOf(storyNotification.dialogId));
            if (user == null && (user = getMessagesStorage().getUserSync(storyNotification.dialogId)) != null) {
                getMessagesController().putUser(user, true);
            }
            Object obj = null;
            if (user != null) {
                str = UserObject.getUserName(user);
                TLRPC.UserProfilePhoto userProfilePhoto = user.photo;
                if (userProfilePhoto != null && (fileLocation = userProfilePhoto.photo_small) != null && fileLocation.volume_id != 0 && fileLocation.local_id != 0) {
                    File pathToAttach = getFileLoader().getPathToAttach(user.photo.photo_small, true);
                    if (!pathToAttach.exists()) {
                        pathToAttach = user.photo.photo_big != null ? getFileLoader().getPathToAttach(user.photo.photo_big, true) : null;
                        if (pathToAttach != null && !pathToAttach.exists()) {
                            pathToAttach = null;
                        }
                    }
                    if (pathToAttach != null) {
                        obj = pathToAttach;
                    }
                }
            } else {
                str = storyNotification.localName;
                i = str == null ? i + 1 : 0;
            }
            if (str.length() > 50) {
                str = str.substring(0, 25) + "…";
            }
            arrayList.add(str);
            if (obj == null && user != null) {
                arrayList2.add(user);
            } else if (obj != null) {
                arrayList2.add(obj);
            }
        }
        if (z) {
            arrayList2.clear();
        }
        return new Pair<>(Integer.valueOf(i2), Boolean.valueOf(z));
    }

    private void playInChatSound() {
        if (!this.inChatSoundEnabled || MediaController.getInstance().isRecordingAudio()) {
            return;
        }
        try {
            if (audioManager.getRingerMode() == 0) {
                return;
            }
        } catch (Exception e) {
            FileLog.e(e);
        }
        try {
            if (getNotifyOverride(getAccountInstance().getNotificationsSettings(), this.openedDialogId, this.openedTopicId) == 2) {
                return;
            }
            notificationsQueue.postRunnable(new Runnable() {
                @Override
                public final void run() {
                    NotificationsController.this.lambda$playInChatSound$37();
                }
            });
        } catch (Exception e2) {
            FileLog.e(e2);
        }
    }

    private String replaceSpoilers(MessageObject messageObject) {
        TLRPC.Message message;
        String str;
        if (messageObject == null || (message = messageObject.messageOwner) == null || (str = message.message) == null || message.entities == null) {
            return null;
        }
        StringBuilder sb = new StringBuilder(str);
        if (messageObject.didSpoilLoginCode()) {
            return sb.toString();
        }
        for (int i = 0; i < messageObject.messageOwner.entities.size(); i++) {
            if (messageObject.messageOwner.entities.get(i) instanceof TLRPC.TL_messageEntitySpoiler) {
                TLRPC.TL_messageEntitySpoiler tL_messageEntitySpoiler = (TLRPC.TL_messageEntitySpoiler) messageObject.messageOwner.entities.get(i);
                for (int i2 = 0; i2 < tL_messageEntitySpoiler.length; i2++) {
                    int i3 = tL_messageEntitySpoiler.offset + i2;
                    char[] cArr = this.spoilerChars;
                    sb.setCharAt(i3, cArr[i2 % cArr.length]);
                }
            }
        }
        return sb.toString();
    }

    public void resetNotificationSound(androidx.core.app.NotificationCompat.Builder r21, long r22, long r24, java.lang.String r26, long[] r27, int r28, android.net.Uri r29, int r30, boolean r31, boolean r32, boolean r33, int r34) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.NotificationsController.resetNotificationSound(androidx.core.app.NotificationCompat$Builder, long, long, java.lang.String, long[], int, android.net.Uri, int, boolean, boolean, boolean, int):void");
    }

    private void scheduleNotificationDelay(boolean z) {
        try {
            if (BuildVars.LOGS_ENABLED) {
                FileLog.d("delay notification start, onlineReason = " + z);
            }
            this.notificationDelayWakelock.acquire(10000L);
            DispatchQueue dispatchQueue = notificationsQueue;
            dispatchQueue.cancelRunnable(this.notificationDelayRunnable);
            dispatchQueue.postRunnable(this.notificationDelayRunnable, z ? 3000 : 1000);
        } catch (Exception e) {
            FileLog.e(e);
            showOrUpdateNotification(this.notifyCheck);
        }
    }

    private void scheduleNotificationRepeat() {
        try {
            Intent intent = new Intent(ApplicationLoader.applicationContext, (Class<?>) NotificationRepeat.class);
            intent.putExtra("currentAccount", this.currentAccount);
            PendingIntent service = PendingIntent.getService(ApplicationLoader.applicationContext, 0, intent, 33554432);
            if (getAccountInstance().getNotificationsSettings().getInt("repeat_messages", 60) <= 0 || this.personalCount <= 0) {
                this.alarmManager.cancel(service);
            } else {
                this.alarmManager.set(2, SystemClock.elapsedRealtime() + (r1 * 60000), service);
            }
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    private void setBadge(int i) {
        if (this.lastBadgeCount == i) {
            return;
        }
        FileLog.d("setBadge " + i);
        this.lastBadgeCount = i;
        NotificationBadge.applyCount(i);
    }

    private void setNotificationChannel(Notification notification, NotificationCompat.Builder builder, boolean z) {
        builder.setChannelId(z ? OTHER_NOTIFICATIONS_CHANNEL : notification.getChannelId());
    }

    private void showExtraNotifications(androidx.core.app.NotificationCompat.Builder r84, java.lang.String r85, long r86, long r88, java.lang.String r90, long[] r91, int r92, android.net.Uri r93, int r94, boolean r95, boolean r96, boolean r97, int r98) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.NotificationsController.showExtraNotifications(androidx.core.app.NotificationCompat$Builder, java.lang.String, long, long, java.lang.String, long[], int, android.net.Uri, int, boolean, boolean, boolean, int):void");
    }

    private void showOrUpdateNotification(boolean r57) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.NotificationsController.showOrUpdateNotification(boolean):void");
    }

    private boolean unsupportedNotificationShortcut() {
        return Build.VERSION.SDK_INT < 29 || !SharedConfig.chatBubbles;
    }

    private void updateStoryPushesRunnable() {
        long j = Long.MAX_VALUE;
        for (int i = 0; i < this.storyPushMessages.size(); i++) {
            Iterator<Pair<Long, Long>> it = this.storyPushMessages.get(i).dateByIds.values().iterator();
            while (it.hasNext()) {
                j = Math.min(j, ((Long) it.next().second).longValue());
            }
        }
        DispatchQueue dispatchQueue = notificationsQueue;
        dispatchQueue.cancelRunnable(this.checkStoryPushesRunnable);
        long currentTimeMillis = j - System.currentTimeMillis();
        if (j != Long.MAX_VALUE) {
            dispatchQueue.postRunnable(this.checkStoryPushesRunnable, Math.max(0L, currentTimeMillis));
        }
    }

    private java.lang.String validateChannelId(long r31, long r33, java.lang.String r35, long[] r36, int r37, android.net.Uri r38, int r39, boolean r40, boolean r41, boolean r42, int r43) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.NotificationsController.validateChannelId(long, long, java.lang.String, long[], int, android.net.Uri, int, boolean, boolean, boolean, int):java.lang.String");
    }

    public void cleanup() {
        this.popupMessages.clear();
        this.popupReplyMessages.clear();
        this.channelGroupsCreated = false;
        notificationsQueue.postRunnable(new Runnable() {
            @Override
            public final void run() {
                NotificationsController.this.lambda$cleanup$1();
            }
        });
    }

    public void clearDialogNotificationsSettings(long j, long j2) {
        SharedPreferences.Editor edit = getAccountInstance().getNotificationsSettings().edit();
        String sharedPrefKey = getSharedPrefKey(j, j2);
        edit.remove("notify2_" + sharedPrefKey).remove("custom_" + sharedPrefKey);
        getMessagesStorage().setDialogFlags(j, 0L);
        TLRPC.Dialog dialog = (TLRPC.Dialog) getMessagesController().dialogs_dict.get(j);
        if (dialog != null) {
            dialog.notify_settings = new TLRPC.TL_peerNotifySettings();
        }
        edit.commit();
        getNotificationsController().updateServerNotificationsSettings(j, j2, true);
    }

    public void deleteAllNotificationChannels() {
        if (Build.VERSION.SDK_INT < 26) {
            return;
        }
        notificationsQueue.postRunnable(new Runnable() {
            @Override
            public final void run() {
                NotificationsController.this.lambda$deleteAllNotificationChannels$41();
            }
        });
    }

    public void deleteNotificationChannel(long j, long j2) {
        deleteNotificationChannel(j, j2, -1);
    }

    public void deleteNotificationChannel(final long j, final long j2, final int i) {
        if (Build.VERSION.SDK_INT < 26) {
            return;
        }
        notificationsQueue.postRunnable(new Runnable() {
            @Override
            public final void run() {
                NotificationsController.this.lambda$deleteNotificationChannel$39(j, j2, i);
            }
        });
    }

    public void deleteNotificationChannelGlobal(int i) {
        deleteNotificationChannelGlobal(i, -1);
    }

    public void deleteNotificationChannelGlobal(final int i, final int i2) {
        if (Build.VERSION.SDK_INT < 26) {
            return;
        }
        notificationsQueue.postRunnable(new Runnable() {
            @Override
            public final void run() {
                NotificationsController.this.lambda$deleteNotificationChannelGlobal$40(i, i2);
            }
        });
    }

    public void lambda$deleteNotificationChannelGlobal$40(int i, int i2) {
        String str;
        String str2;
        String str3;
        if (Build.VERSION.SDK_INT < 26) {
            return;
        }
        try {
            SharedPreferences notificationsSettings = getAccountInstance().getNotificationsSettings();
            SharedPreferences.Editor edit = notificationsSettings.edit();
            if (i2 == 0 || i2 == -1) {
                if (i == 2) {
                    str = "channels";
                } else if (i == 0) {
                    str = "groups";
                } else if (i == 3) {
                    str = "stories";
                } else {
                    if (i != 4 && i != 5) {
                        str = "private";
                    }
                    str = "reactions";
                }
                String string = notificationsSettings.getString(str, null);
                if (string != null) {
                    edit.remove(str).remove(str + "_s");
                    try {
                        systemNotificationManager.deleteNotificationChannel(string);
                    } catch (Exception e) {
                        FileLog.e(e);
                    }
                    if (BuildVars.LOGS_ENABLED) {
                        FileLog.d("delete channel global internal " + string);
                    }
                }
            }
            if (i2 == 1 || i2 == -1) {
                if (i == 2) {
                    str2 = "channels_ia";
                } else if (i == 0) {
                    str2 = "groups_ia";
                } else if (i == 3) {
                    str2 = "stories_ia";
                } else {
                    if (i != 4 && i != 5) {
                        str2 = "private_ia";
                    }
                    str2 = "reactions_ia";
                }
                String string2 = notificationsSettings.getString(str2, null);
                if (string2 != null) {
                    edit.remove(str2).remove(str2 + "_s");
                    try {
                        systemNotificationManager.deleteNotificationChannel(string2);
                    } catch (Exception e2) {
                        FileLog.e(e2);
                    }
                    if (BuildVars.LOGS_ENABLED) {
                        FileLog.d("delete channel global internal " + string2);
                    }
                }
            }
            if (i == 2) {
                str3 = "overwrite_channel";
            } else if (i == 0) {
                str3 = "overwrite_group";
            } else if (i == 3) {
                str3 = "overwrite_stories";
            } else {
                if (i != 4 && i != 5) {
                    str3 = "overwrite_private";
                }
                str3 = "overwrite_reactions";
            }
            edit.remove(str3);
            edit.commit();
        } catch (Exception e3) {
            FileLog.e(e3);
        }
    }

    protected void ensureGroupsCreated() {
        List notificationChannels;
        String id;
        int importance;
        SharedPreferences.Editor remove;
        String str;
        List notificationChannelGroups;
        String id2;
        SharedPreferences notificationsSettings = getAccountInstance().getNotificationsSettings();
        if (this.groupsCreated == null) {
            this.groupsCreated = Boolean.valueOf(notificationsSettings.getBoolean("groupsCreated5", false));
        }
        if (!this.groupsCreated.booleanValue()) {
            try {
                String str2 = this.currentAccount + "channel";
                notificationChannels = systemNotificationManager.getNotificationChannels();
                int size = notificationChannels.size();
                SharedPreferences.Editor editor = null;
                for (int i = 0; i < size; i++) {
                    NotificationChannel m = NotificationsController$$ExternalSyntheticApiModelOutline13.m(notificationChannels.get(i));
                    id = m.getId();
                    if (id.startsWith(str2)) {
                        importance = m.getImportance();
                        if (importance != 4 && importance != 5 && !id.contains("_ia_")) {
                            if (id.contains("_channels_")) {
                                if (editor == null) {
                                    editor = getAccountInstance().getNotificationsSettings().edit();
                                }
                                remove = editor.remove("priority_channel").remove("vibrate_channel").remove("ChannelSoundPath");
                                str = "ChannelSound";
                            } else if (id.contains("_reactions_")) {
                                if (editor == null) {
                                    editor = getAccountInstance().getNotificationsSettings().edit();
                                }
                                remove = editor.remove("priority_react").remove("vibrate_react").remove("ReactionSoundPath");
                                str = "ReactionSound";
                            } else if (id.contains("_groups_")) {
                                if (editor == null) {
                                    editor = getAccountInstance().getNotificationsSettings().edit();
                                }
                                remove = editor.remove("priority_group").remove("vibrate_group").remove("GroupSoundPath");
                                str = "GroupSound";
                            } else if (id.contains("_private_")) {
                                if (editor == null) {
                                    editor = getAccountInstance().getNotificationsSettings().edit();
                                }
                                editor.remove("priority_messages");
                                remove = editor.remove("priority_group").remove("vibrate_messages").remove("GlobalSoundPath");
                                str = "GlobalSound";
                            } else {
                                long longValue = Utilities.parseLong(id.substring(9, id.indexOf(95, 9))).longValue();
                                if (longValue != 0) {
                                    if (editor == null) {
                                        editor = getAccountInstance().getNotificationsSettings().edit();
                                    }
                                    remove = editor.remove("priority_" + longValue).remove("vibrate_" + longValue).remove("sound_path_" + longValue);
                                    str = "sound_" + longValue;
                                }
                            }
                            remove.remove(str);
                        }
                        systemNotificationManager.deleteNotificationChannel(id);
                    }
                }
                if (editor != null) {
                    editor.commit();
                }
            } catch (Exception e) {
                FileLog.e(e);
            }
            notificationsSettings.edit().putBoolean("groupsCreated5", true).commit();
            this.groupsCreated = Boolean.TRUE;
        }
        if (this.channelGroupsCreated) {
            return;
        }
        notificationChannelGroups = systemNotificationManager.getNotificationChannelGroups();
        String str3 = "channels" + this.currentAccount;
        String str4 = "groups" + this.currentAccount;
        String str5 = "private" + this.currentAccount;
        String str6 = "stories" + this.currentAccount;
        String str7 = "reactions" + this.currentAccount;
        String str8 = "other" + this.currentAccount;
        int size2 = notificationChannelGroups.size();
        String str9 = str8;
        String str10 = str7;
        String str11 = str6;
        String str12 = str5;
        for (int i2 = 0; i2 < size2; i2++) {
            id2 = NotificationsController$$ExternalSyntheticApiModelOutline16.m(notificationChannelGroups.get(i2)).getId();
            if (str3 != null && str3.equals(id2)) {
                str3 = null;
            } else if (str4 != null && str4.equals(id2)) {
                str4 = null;
            } else if (str11 != null && str11.equals(id2)) {
                str11 = null;
            } else if (str10 != null && str10.equals(id2)) {
                str10 = null;
            } else if (str12 != null && str12.equals(id2)) {
                str12 = null;
            } else if (str9 != null && str9.equals(id2)) {
                str9 = null;
            }
            if (str3 == null && str11 == null && str10 == null && str4 == null && str12 == null && str9 == null) {
                break;
            }
        }
        if (str3 != null || str4 != null || str10 != null || str11 != null || str12 != null || str9 != null) {
            TLRPC.User user = getMessagesController().getUser(Long.valueOf(getUserConfig().getClientUserId()));
            if (user == null) {
                getUserConfig().getCurrentUser();
            }
            String str13 = user != null ? " (" + ContactsController.formatName(user.first_name, user.last_name) + ")" : "";
            ArrayList arrayList = new ArrayList();
            if (str3 != null) {
                arrayList.add(new NotificationChannelGroup(str3, LocaleController.getString(R.string.NotificationsChannels) + str13));
            }
            if (str4 != null) {
                arrayList.add(new NotificationChannelGroup(str4, LocaleController.getString(R.string.NotificationsGroups) + str13));
            }
            if (str11 != null) {
                arrayList.add(new NotificationChannelGroup(str11, LocaleController.getString(R.string.NotificationsStories) + str13));
            }
            if (str10 != null) {
                arrayList.add(new NotificationChannelGroup(str10, LocaleController.getString(R.string.NotificationsReactions) + str13));
            }
            if (str12 != null) {
                arrayList.add(new NotificationChannelGroup(str12, LocaleController.getString(R.string.NotificationsPrivateChats) + str13));
            }
            if (str9 != null) {
                arrayList.add(new NotificationChannelGroup(str9, LocaleController.getString(R.string.NotificationsOther) + str13));
            }
            systemNotificationManager.createNotificationChannelGroups(arrayList);
        }
        this.channelGroupsCreated = true;
    }

    public void forceShowPopupForReply() {
        notificationsQueue.postRunnable(new Runnable() {
            @Override
            public final void run() {
                NotificationsController.this.lambda$forceShowPopupForReply$6();
            }
        });
    }

    public NotificationsSettingsFacade getNotificationsSettingsFacade() {
        return this.dialogsNotificationsFacade;
    }

    public int getTotalUnreadCount() {
        return this.total_unread_count;
    }

    public boolean hasMessagesToReply() {
        for (int i = 0; i < this.pushMessages.size(); i++) {
            MessageObject messageObject = this.pushMessages.get(i);
            long dialogId = messageObject.getDialogId();
            if (!messageObject.isReactionPush) {
                TLRPC.Message message = messageObject.messageOwner;
                if ((!message.mentioned || !(message.action instanceof TLRPC.TL_messageActionPinMessage)) && !DialogObject.isEncryptedDialog(dialogId) && ((messageObject.messageOwner.peer_id.channel_id == 0 || messageObject.isSupergroup()) && dialogId != 489000)) {
                    return true;
                }
            }
        }
        return false;
    }

    public void hideNotifications() {
        notificationsQueue.postRunnable(new Runnable() {
            @Override
            public final void run() {
                NotificationsController.this.lambda$hideNotifications$34();
            }
        });
    }

    public boolean isGlobalNotificationsEnabled(int i) {
        SharedPreferences notificationsSettings;
        String str;
        if (i == 4) {
            notificationsSettings = getAccountInstance().getNotificationsSettings();
            str = "EnableReactionsMessages";
        } else if (i == 5) {
            notificationsSettings = getAccountInstance().getNotificationsSettings();
            str = "EnableReactionsStories";
        } else {
            if (i != 3) {
                return getAccountInstance().getNotificationsSettings().getInt(getGlobalNotificationsKey(i), 0) < getConnectionsManager().getCurrentTime();
            }
            notificationsSettings = getAccountInstance().getNotificationsSettings();
            str = "EnableAllStories";
        }
        return notificationsSettings.getBoolean(str, true);
    }

    public boolean isGlobalNotificationsEnabled(long r1, java.lang.Boolean r3, boolean r4, boolean r5) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.NotificationsController.isGlobalNotificationsEnabled(long, java.lang.Boolean, boolean, boolean):boolean");
    }

    public boolean isGlobalNotificationsEnabled(long j, boolean z, boolean z2) {
        return isGlobalNotificationsEnabled(j, null, z, z2);
    }

    public void loadTopicsNotificationsExceptions(final long j, final Consumer<HashSet<Integer>> consumer) {
        getMessagesStorage().getStorageQueue().postRunnable(new Runnable() {
            @Override
            public final void run() {
                NotificationsController.this.lambda$loadTopicsNotificationsExceptions$51(j, consumer);
            }
        });
    }

    public void muteDialog(long j, long j2, boolean z) {
        if (z) {
            getInstance(this.currentAccount).muteUntil(j, j2, Integer.MAX_VALUE);
            return;
        }
        boolean isGlobalNotificationsEnabled = getInstance(this.currentAccount).isGlobalNotificationsEnabled(j, false, false);
        boolean z2 = j2 != 0;
        SharedPreferences.Editor edit = MessagesController.getNotificationsSettings(this.currentAccount).edit();
        if (!isGlobalNotificationsEnabled || z2) {
            edit.putInt("notify2_" + getSharedPrefKey(j, j2), 0);
        } else {
            edit.remove("notify2_" + getSharedPrefKey(j, j2));
        }
        if (j2 == 0) {
            getMessagesStorage().setDialogFlags(j, 0L);
            TLRPC.Dialog dialog = (TLRPC.Dialog) getMessagesController().dialogs_dict.get(j);
            if (dialog != null) {
                dialog.notify_settings = new TLRPC.TL_peerNotifySettings();
            }
        }
        edit.apply();
        updateServerNotificationsSettings(j, j2);
    }

    public void muteUntil(long j, long j2, int i) {
        if (j != 0) {
            SharedPreferences.Editor edit = MessagesController.getNotificationsSettings(this.currentAccount).edit();
            boolean z = j2 != 0;
            boolean isGlobalNotificationsEnabled = getInstance(this.currentAccount).isGlobalNotificationsEnabled(j, false, false);
            String sharedPrefKey = getSharedPrefKey(j, j2);
            long j3 = 1;
            if (i != Integer.MAX_VALUE) {
                edit.putInt("notify2_" + sharedPrefKey, 3);
                edit.putInt("notifyuntil_" + sharedPrefKey, getConnectionsManager().getCurrentTime() + i);
                j3 = 1 | (((long) i) << 32);
            } else if (isGlobalNotificationsEnabled || z) {
                edit.putInt("notify2_" + sharedPrefKey, 2);
            } else {
                edit.remove("notify2_" + sharedPrefKey);
                j3 = 0L;
            }
            edit.apply();
            if (j2 == 0) {
                getInstance(this.currentAccount).removeNotificationsForDialog(j);
                MessagesStorage.getInstance(this.currentAccount).setDialogFlags(j, j3);
                TLRPC.Dialog dialog = (TLRPC.Dialog) MessagesController.getInstance(this.currentAccount).dialogs_dict.get(j);
                if (dialog != null) {
                    TLRPC.TL_peerNotifySettings tL_peerNotifySettings = new TLRPC.TL_peerNotifySettings();
                    dialog.notify_settings = tL_peerNotifySettings;
                    if (i != Integer.MAX_VALUE || isGlobalNotificationsEnabled) {
                        tL_peerNotifySettings.mute_until = i;
                    }
                }
            }
            getInstance(this.currentAccount).updateServerNotificationsSettings(j, j2);
        }
    }

    public void playOutChatSound() {
        if (!this.inChatSoundEnabled || MediaController.getInstance().isRecordingAudio()) {
            return;
        }
        try {
            if (audioManager.getRingerMode() == 0) {
                return;
            }
        } catch (Exception e) {
            FileLog.e(e);
        }
        notificationsQueue.postRunnable(new Runnable() {
            @Override
            public final void run() {
                NotificationsController.this.lambda$playOutChatSound$46();
            }
        });
    }

    public void processDeleteStory(final long j, final int i) {
        notificationsQueue.postRunnable(new Runnable() {
            @Override
            public final void run() {
                NotificationsController.this.lambda$processDeleteStory$14(j, i);
            }
        });
    }

    public void processDialogsUpdateRead(final LongSparseIntArray longSparseIntArray) {
        final ArrayList arrayList = new ArrayList();
        notificationsQueue.postRunnable(new Runnable() {
            @Override
            public final void run() {
                NotificationsController.this.lambda$processDialogsUpdateRead$28(longSparseIntArray, arrayList);
            }
        });
    }

    public void processEditedMessages(final LongSparseArray longSparseArray) {
        if (longSparseArray.size() == 0) {
            return;
        }
        new ArrayList(0);
        notificationsQueue.postRunnable(new Runnable() {
            @Override
            public final void run() {
                NotificationsController.this.lambda$processEditedMessages$21(longSparseArray);
            }
        });
    }

    public void processIgnoreStories() {
        notificationsQueue.postRunnable(new Runnable() {
            @Override
            public final void run() {
                NotificationsController.this.lambda$processIgnoreStories$16();
            }
        });
    }

    public void processIgnoreStories(final long j) {
        notificationsQueue.postRunnable(new Runnable() {
            @Override
            public final void run() {
                NotificationsController.this.lambda$processIgnoreStories$18(j);
            }
        });
    }

    public void processIgnoreStoryReactions() {
        notificationsQueue.postRunnable(new Runnable() {
            @Override
            public final void run() {
                NotificationsController.this.lambda$processIgnoreStoryReactions$17();
            }
        });
    }

    public void processLoadedUnreadMessages(final LongSparseArray longSparseArray, final ArrayList<TLRPC.Message> arrayList, final ArrayList<MessageObject> arrayList2, ArrayList<TLRPC.User> arrayList3, ArrayList<TLRPC.Chat> arrayList4, ArrayList<TLRPC.EncryptedChat> arrayList5, final Collection<StoryNotification> collection) {
        getMessagesController().putUsers(arrayList3, true);
        getMessagesController().putChats(arrayList4, true);
        getMessagesController().putEncryptedChats(arrayList5, true);
        notificationsQueue.postRunnable(new Runnable() {
            @Override
            public final void run() {
                NotificationsController.this.lambda$processLoadedUnreadMessages$31(arrayList, longSparseArray, arrayList2, collection);
            }
        });
    }

    public void processNewMessages(final ArrayList<MessageObject> arrayList, final boolean z, final boolean z2, final CountDownLatch countDownLatch) {
        StringBuilder sb = new StringBuilder();
        sb.append("NotificationsController: processNewMessages msgs.size()=");
        sb.append(arrayList == null ? "null" : Integer.valueOf(arrayList.size()));
        sb.append(" isLast=");
        sb.append(z);
        sb.append(" isFcm=");
        sb.append(z2);
        sb.append(")");
        FileLog.d(sb.toString());
        if (!arrayList.isEmpty()) {
            final ArrayList arrayList2 = new ArrayList(0);
            notificationsQueue.postRunnable(new Runnable() {
                @Override
                public final void run() {
                    NotificationsController.this.lambda$processNewMessages$25(arrayList, arrayList2, z2, z, countDownLatch);
                }
            });
        } else if (countDownLatch != null) {
            countDownLatch.countDown();
        }
    }

    public void processReadMessages(final LongSparseIntArray longSparseIntArray, final long j, final int i, final int i2, final boolean z) {
        final ArrayList arrayList = new ArrayList(0);
        notificationsQueue.postRunnable(new Runnable() {
            @Override
            public final void run() {
                NotificationsController.this.lambda$processReadMessages$20(longSparseIntArray, arrayList, j, i2, i, z);
            }
        });
    }

    public void processReadStories() {
    }

    public void processReadStories(final long j, int i) {
        notificationsQueue.postRunnable(new Runnable() {
            @Override
            public final void run() {
                NotificationsController.this.lambda$processReadStories$15(j);
            }
        });
    }

    public void processSeenStoryReactions(long j, final int i) {
        if (j != getUserConfig().getClientUserId()) {
            return;
        }
        notificationsQueue.postRunnable(new Runnable() {
            @Override
            public final void run() {
                NotificationsController.this.lambda$processSeenStoryReactions$13(i);
            }
        });
    }

    public void removeDeletedHisoryFromNotifications(final LongSparseIntArray longSparseIntArray) {
        final ArrayList arrayList = new ArrayList(0);
        notificationsQueue.postRunnable(new Runnable() {
            @Override
            public final void run() {
                NotificationsController.this.lambda$removeDeletedHisoryFromNotifications$12(longSparseIntArray, arrayList);
            }
        });
    }

    public void removeDeletedMessagesFromNotifications(final LongSparseArray longSparseArray, final boolean z) {
        final ArrayList arrayList = new ArrayList(0);
        notificationsQueue.postRunnable(new Runnable() {
            @Override
            public final void run() {
                NotificationsController.this.lambda$removeDeletedMessagesFromNotifications$9(longSparseArray, z, arrayList);
            }
        });
    }

    public void removeNotificationsForDialog(long j) {
        processReadMessages(null, j, 0, Integer.MAX_VALUE, false);
        LongSparseIntArray longSparseIntArray = new LongSparseIntArray();
        longSparseIntArray.put(j, 0);
        processDialogsUpdateRead(longSparseIntArray);
    }

    public void repeatNotificationMaybe() {
        notificationsQueue.postRunnable(new Runnable() {
            @Override
            public final void run() {
                NotificationsController.this.lambda$repeatNotificationMaybe$38();
            }
        });
    }

    public void setDialogNotificationsSettings(long j, long j2, int i) {
        SharedPreferences.Editor edit = getAccountInstance().getNotificationsSettings().edit();
        TLRPC.Dialog dialog = (TLRPC.Dialog) MessagesController.getInstance(UserConfig.selectedAccount).dialogs_dict.get(j);
        if (i == 4) {
            if (isGlobalNotificationsEnabled(j, false, false)) {
                edit.remove("notify2_" + getSharedPrefKey(j, j2));
            } else {
                edit.putInt("notify2_" + getSharedPrefKey(j, j2), 0);
            }
            getMessagesStorage().setDialogFlags(j, 0L);
            if (dialog != null) {
                dialog.notify_settings = new TLRPC.TL_peerNotifySettings();
            }
        } else {
            int currentTime = ConnectionsManager.getInstance(UserConfig.selectedAccount).getCurrentTime();
            if (i == 0) {
                currentTime += 3600;
            } else if (i == 1) {
                currentTime += 28800;
            } else if (i == 2) {
                currentTime += 172800;
            } else if (i == 3) {
                currentTime = Integer.MAX_VALUE;
            }
            long j3 = 1;
            if (i == 3) {
                edit.putInt("notify2_" + getSharedPrefKey(j, j2), 2);
            } else {
                edit.putInt("notify2_" + getSharedPrefKey(j, j2), 3);
                edit.putInt("notifyuntil_" + getSharedPrefKey(j, j2), currentTime);
                j3 = 1 | (((long) currentTime) << 32);
            }
            getInstance(UserConfig.selectedAccount).removeNotificationsForDialog(j);
            MessagesStorage.getInstance(UserConfig.selectedAccount).setDialogFlags(j, j3);
            if (dialog != null) {
                TLRPC.TL_peerNotifySettings tL_peerNotifySettings = new TLRPC.TL_peerNotifySettings();
                dialog.notify_settings = tL_peerNotifySettings;
                tL_peerNotifySettings.mute_until = currentTime;
            }
        }
        edit.commit();
        updateServerNotificationsSettings(j, j2);
    }

    public void setGlobalNotificationsEnabled(int i, int i2) {
        getAccountInstance().getNotificationsSettings().edit().putInt(getGlobalNotificationsKey(i), i2).commit();
        updateServerNotificationsSettings(i);
        getMessagesStorage().updateMutedDialogsFiltersCounters();
        deleteNotificationChannelGlobal(i);
    }

    public void setInChatSoundEnabled(boolean z) {
        this.inChatSoundEnabled = z;
    }

    public void setLastOnlineFromOtherDevice(final int i) {
        notificationsQueue.postRunnable(new Runnable() {
            @Override
            public final void run() {
                NotificationsController.this.lambda$setLastOnlineFromOtherDevice$4(i);
            }
        });
    }

    public void setOpenedDialogId(final long j, final long j2) {
        notificationsQueue.postRunnable(new Runnable() {
            @Override
            public final void run() {
                NotificationsController.this.lambda$setOpenedDialogId$2(j, j2);
            }
        });
    }

    public void setOpenedInBubble(final long j, final boolean z) {
        notificationsQueue.postRunnable(new Runnable() {
            @Override
            public final void run() {
                NotificationsController.this.lambda$setOpenedInBubble$3(z, j);
            }
        });
    }

    public void showNotifications() {
        notificationsQueue.postRunnable(new Runnable() {
            @Override
            public final void run() {
                NotificationsController.this.lambda$showNotifications$33();
            }
        });
    }

    public void updateBadge() {
        notificationsQueue.postRunnable(new Runnable() {
            @Override
            public final void run() {
                NotificationsController.this.lambda$updateBadge$32();
            }
        });
    }

    public void updateServerNotificationsSettings(int i) {
        TL_account.TL_reactionsNotifySettings tL_reactionsNotifySettings;
        TL_account.ReactionNotificationsFrom tL_reactionNotificationsFromAll;
        TL_account.TL_reactionsNotifySettings tL_reactionsNotifySettings2;
        TL_account.ReactionNotificationsFrom tL_reactionNotificationsFromAll2;
        TLRPC.TL_inputPeerNotifySettings tL_inputPeerNotifySettings;
        String str;
        String str2;
        String str3;
        SharedPreferences notificationsSettings = getAccountInstance().getNotificationsSettings();
        if (i == 4 || i == 5) {
            TL_account.setReactionsNotifySettings setreactionsnotifysettings = new TL_account.setReactionsNotifySettings();
            setreactionsnotifysettings.settings = new TL_account.TL_reactionsNotifySettings();
            if (notificationsSettings.getBoolean("EnableReactionsMessages", true)) {
                setreactionsnotifysettings.settings.flags |= 1;
                if (notificationsSettings.getBoolean("EnableReactionsMessagesContacts", false)) {
                    tL_reactionsNotifySettings2 = setreactionsnotifysettings.settings;
                    tL_reactionNotificationsFromAll2 = new TL_account.TL_reactionNotificationsFromContacts();
                } else {
                    tL_reactionsNotifySettings2 = setreactionsnotifysettings.settings;
                    tL_reactionNotificationsFromAll2 = new TL_account.TL_reactionNotificationsFromAll();
                }
                tL_reactionsNotifySettings2.messages_notify_from = tL_reactionNotificationsFromAll2;
            }
            if (notificationsSettings.getBoolean("EnableReactionsStories", true)) {
                setreactionsnotifysettings.settings.flags |= 2;
                if (notificationsSettings.getBoolean("EnableReactionsStoriesContacts", false)) {
                    tL_reactionsNotifySettings = setreactionsnotifysettings.settings;
                    tL_reactionNotificationsFromAll = new TL_account.TL_reactionNotificationsFromContacts();
                } else {
                    tL_reactionsNotifySettings = setreactionsnotifysettings.settings;
                    tL_reactionNotificationsFromAll = new TL_account.TL_reactionNotificationsFromAll();
                }
                tL_reactionsNotifySettings.stories_notify_from = tL_reactionNotificationsFromAll;
            }
            setreactionsnotifysettings.settings.show_previews = notificationsSettings.getBoolean("EnableReactionsPreview", true);
            setreactionsnotifysettings.settings.sound = getInputSound(notificationsSettings, "ReactionSound", "ReactionSoundDocId", "ReactionSoundPath");
            getConnectionsManager().sendRequest(setreactionsnotifysettings, new RequestDelegate() {
                @Override
                public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                    NotificationsController.lambda$updateServerNotificationsSettings$48(tLObject, tL_error);
                }
            });
            return;
        }
        TL_account.updateNotifySettings updatenotifysettings = new TL_account.updateNotifySettings();
        TLRPC.TL_inputPeerNotifySettings tL_inputPeerNotifySettings2 = new TLRPC.TL_inputPeerNotifySettings();
        updatenotifysettings.settings = tL_inputPeerNotifySettings2;
        tL_inputPeerNotifySettings2.flags = 5;
        if (i == 0) {
            updatenotifysettings.peer = new TLRPC.TL_inputNotifyChats();
            updatenotifysettings.settings.mute_until = notificationsSettings.getInt("EnableGroup2", 0);
            updatenotifysettings.settings.show_previews = notificationsSettings.getBoolean("EnablePreviewGroup", true);
            tL_inputPeerNotifySettings = updatenotifysettings.settings;
            tL_inputPeerNotifySettings.flags |= 8;
            str = "GroupSoundDocId";
            str2 = "GroupSoundPath";
            str3 = "GroupSound";
        } else {
            if (i == 1 || i == 3) {
                updatenotifysettings.peer = new TLRPC.TL_inputNotifyUsers();
                updatenotifysettings.settings.mute_until = notificationsSettings.getInt("EnableAll2", 0);
                updatenotifysettings.settings.show_previews = notificationsSettings.getBoolean("EnablePreviewAll", true);
                TLRPC.TL_inputPeerNotifySettings tL_inputPeerNotifySettings3 = updatenotifysettings.settings;
                tL_inputPeerNotifySettings3.flags |= 128;
                tL_inputPeerNotifySettings3.stories_hide_sender = notificationsSettings.getBoolean("EnableHideStoriesSenders", false);
                if (notificationsSettings.contains("EnableAllStories")) {
                    TLRPC.TL_inputPeerNotifySettings tL_inputPeerNotifySettings4 = updatenotifysettings.settings;
                    tL_inputPeerNotifySettings4.flags |= 64;
                    tL_inputPeerNotifySettings4.stories_muted = !notificationsSettings.getBoolean("EnableAllStories", true);
                }
                TLRPC.TL_inputPeerNotifySettings tL_inputPeerNotifySettings5 = updatenotifysettings.settings;
                tL_inputPeerNotifySettings5.flags |= 8;
                tL_inputPeerNotifySettings5.sound = getInputSound(notificationsSettings, "GlobalSound", "GlobalSoundDocId", "GlobalSoundPath");
                TLRPC.TL_inputPeerNotifySettings tL_inputPeerNotifySettings6 = updatenotifysettings.settings;
                tL_inputPeerNotifySettings6.flags |= 256;
                tL_inputPeerNotifySettings6.stories_sound = getInputSound(notificationsSettings, "StoriesSound", "StoriesSoundDocId", "StoriesSoundPath");
                getConnectionsManager().sendRequest(updatenotifysettings, new RequestDelegate() {
                    @Override
                    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                        NotificationsController.lambda$updateServerNotificationsSettings$49(tLObject, tL_error);
                    }
                });
            }
            updatenotifysettings.peer = new TLRPC.TL_inputNotifyBroadcasts();
            updatenotifysettings.settings.mute_until = notificationsSettings.getInt("EnableChannel2", 0);
            updatenotifysettings.settings.show_previews = notificationsSettings.getBoolean("EnablePreviewChannel", true);
            tL_inputPeerNotifySettings = updatenotifysettings.settings;
            tL_inputPeerNotifySettings.flags |= 8;
            str = "ChannelSoundDocId";
            str2 = "ChannelSoundPath";
            str3 = "ChannelSound";
        }
        tL_inputPeerNotifySettings.sound = getInputSound(notificationsSettings, str3, str, str2);
        getConnectionsManager().sendRequest(updatenotifysettings, new RequestDelegate() {
            @Override
            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                NotificationsController.lambda$updateServerNotificationsSettings$49(tLObject, tL_error);
            }
        });
    }

    public void updateServerNotificationsSettings(long j, long j2) {
        updateServerNotificationsSettings(j, j2, true);
    }

    public void updateServerNotificationsSettings(long j, long j2, boolean z) {
        int i = 0;
        if (z) {
            getNotificationCenter().lambda$postNotificationNameOnUIThread$1(NotificationCenter.notificationsSettingsUpdated, new Object[0]);
        }
        if (DialogObject.isEncryptedDialog(j)) {
            return;
        }
        SharedPreferences notificationsSettings = getAccountInstance().getNotificationsSettings();
        TL_account.updateNotifySettings updatenotifysettings = new TL_account.updateNotifySettings();
        updatenotifysettings.settings = new TLRPC.TL_inputPeerNotifySettings();
        String sharedPrefKey = getSharedPrefKey(j, j2);
        TLRPC.TL_inputPeerNotifySettings tL_inputPeerNotifySettings = updatenotifysettings.settings;
        tL_inputPeerNotifySettings.flags |= 1;
        tL_inputPeerNotifySettings.show_previews = notificationsSettings.getBoolean("content_preview_" + sharedPrefKey, true);
        TLRPC.TL_inputPeerNotifySettings tL_inputPeerNotifySettings2 = updatenotifysettings.settings;
        tL_inputPeerNotifySettings2.flags = tL_inputPeerNotifySettings2.flags | 2;
        tL_inputPeerNotifySettings2.silent = notificationsSettings.getBoolean("silent_" + sharedPrefKey, false);
        if (notificationsSettings.contains("stories_" + sharedPrefKey)) {
            TLRPC.TL_inputPeerNotifySettings tL_inputPeerNotifySettings3 = updatenotifysettings.settings;
            tL_inputPeerNotifySettings3.flags |= 64;
            tL_inputPeerNotifySettings3.stories_muted = !notificationsSettings.getBoolean("stories_" + sharedPrefKey, true);
        }
        int i2 = notificationsSettings.getInt("notify2_" + getSharedPrefKey(j, j2), -1);
        if (i2 != -1) {
            TLRPC.TL_inputPeerNotifySettings tL_inputPeerNotifySettings4 = updatenotifysettings.settings;
            tL_inputPeerNotifySettings4.flags |= 4;
            if (i2 == 3) {
                i = notificationsSettings.getInt("notifyuntil_" + getSharedPrefKey(j, j2), 0);
            } else if (i2 == 2) {
                i = Integer.MAX_VALUE;
            }
            tL_inputPeerNotifySettings4.mute_until = i;
        }
        long j3 = notificationsSettings.getLong("sound_document_id_" + getSharedPrefKey(j, j2), 0L);
        String string = notificationsSettings.getString("sound_path_" + getSharedPrefKey(j, j2), null);
        TLRPC.TL_inputPeerNotifySettings tL_inputPeerNotifySettings5 = updatenotifysettings.settings;
        tL_inputPeerNotifySettings5.flags = tL_inputPeerNotifySettings5.flags | 8;
        if (j3 != 0) {
            TLRPC.TL_notificationSoundRingtone tL_notificationSoundRingtone = new TLRPC.TL_notificationSoundRingtone();
            tL_notificationSoundRingtone.id = j3;
            updatenotifysettings.settings.sound = tL_notificationSoundRingtone;
        } else if (string == null) {
            tL_inputPeerNotifySettings5.sound = new TLRPC.TL_notificationSoundDefault();
        } else if (string.equalsIgnoreCase("NoSound")) {
            updatenotifysettings.settings.sound = new TLRPC.TL_notificationSoundNone();
        } else {
            TLRPC.TL_notificationSoundLocal tL_notificationSoundLocal = new TLRPC.TL_notificationSoundLocal();
            tL_notificationSoundLocal.title = notificationsSettings.getString("sound_" + getSharedPrefKey(j, j2), null);
            tL_notificationSoundLocal.data = string;
            updatenotifysettings.settings.sound = tL_notificationSoundLocal;
        }
        if (j2 == 0 || j == getUserConfig().getClientUserId()) {
            TLRPC.TL_inputNotifyPeer tL_inputNotifyPeer = new TLRPC.TL_inputNotifyPeer();
            updatenotifysettings.peer = tL_inputNotifyPeer;
            tL_inputNotifyPeer.peer = getMessagesController().getInputPeer(j);
        } else {
            TLRPC.TL_inputNotifyForumTopic tL_inputNotifyForumTopic = new TLRPC.TL_inputNotifyForumTopic();
            tL_inputNotifyForumTopic.peer = getMessagesController().getInputPeer(j);
            tL_inputNotifyForumTopic.top_msg_id = (int) j2;
            updatenotifysettings.peer = tL_inputNotifyForumTopic;
        }
        getConnectionsManager().sendRequest(updatenotifysettings, new RequestDelegate() {
            @Override
            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                NotificationsController.lambda$updateServerNotificationsSettings$47(tLObject, tL_error);
            }
        });
    }
}
