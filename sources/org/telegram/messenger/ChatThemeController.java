package org.telegram.messenger;

import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.text.TextUtils;
import android.util.LongSparseArray;
import android.util.Pair;
import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.ResultCallback;
import org.telegram.tgnet.SerializedData;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.EmojiThemes;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ChatBackgroundDrawable;

public class ChatThemeController extends BaseController {
    public static volatile DispatchQueue chatThemeQueue = new DispatchQueue("chatThemeQueue");
    private static final ChatThemeController[] instances = new ChatThemeController[4];
    private List<EmojiThemes> allChatThemes;
    private final LongSparseArray<String> dialogEmoticonsMap;
    private volatile long lastReloadTimeMs;
    private final long reloadTimeoutMs;
    private final HashMap<Long, Bitmap> themeIdWallpaperThumbMap;
    private volatile long themesHash;

    private ChatThemeController(int i) {
        super(i);
        this.reloadTimeoutMs = 7200000L;
        this.themeIdWallpaperThumbMap = new HashMap<>();
        this.dialogEmoticonsMap = new LongSparseArray<>();
        init();
    }

    public static boolean equals(TLRPC.WallPaper wallPaper, TLRPC.WallPaper wallPaper2) {
        if (wallPaper == null && wallPaper2 == null) {
            return true;
        }
        if (wallPaper == null || wallPaper2 == null) {
            return false;
        }
        String str = wallPaper.uploadingImage;
        return str != null ? TextUtils.equals(wallPaper2.uploadingImage, str) : wallPaper.id == wallPaper2.id && TextUtils.equals(ChatBackgroundDrawable.hash(wallPaper.settings), ChatBackgroundDrawable.hash(wallPaper2.settings)) && TextUtils.equals(getWallpaperEmoticon(wallPaper), getWallpaperEmoticon(wallPaper2));
    }

    private List<EmojiThemes> getAllChatThemesFromPrefs() {
        SharedPreferences sharedPreferences = getSharedPreferences();
        int i = sharedPreferences.getInt("count", 0);
        ArrayList arrayList = new ArrayList(i);
        for (int i2 = 0; i2 < i; i2++) {
            SerializedData serializedData = new SerializedData(Utilities.hexToBytes(sharedPreferences.getString("theme_" + i2, "")));
            try {
                TLRPC.TL_theme TLdeserialize = TLRPC.Theme.TLdeserialize(serializedData, serializedData.readInt32(true), true);
                if (TLdeserialize != null) {
                    arrayList.add(new EmojiThemes(this.currentAccount, TLdeserialize, false));
                }
            } catch (Throwable th) {
                FileLog.e(th);
            }
        }
        return arrayList;
    }

    private SharedPreferences getEmojiSharedPreferences() {
        return ApplicationLoader.applicationContext.getSharedPreferences("chatthemeconfig_emoji", 0);
    }

    public static ChatThemeController getInstance(int i) {
        ChatThemeController[] chatThemeControllerArr = instances;
        ChatThemeController chatThemeController = chatThemeControllerArr[i];
        if (chatThemeController == null) {
            synchronized (ChatThemeController.class) {
                try {
                    chatThemeController = chatThemeControllerArr[i];
                    if (chatThemeController == null) {
                        chatThemeController = new ChatThemeController(i);
                        chatThemeControllerArr[i] = chatThemeController;
                    }
                } finally {
                }
            }
        }
        return chatThemeController;
    }

    private File getPatternFile(long j) {
        return new File(ApplicationLoader.getFilesDirFixed(), String.format(Locale.US, "%d_%d.jpg", Long.valueOf(j), Long.valueOf(this.themesHash)));
    }

    private SharedPreferences getSharedPreferences() {
        return ApplicationLoader.applicationContext.getSharedPreferences("chatthemeconfig_" + this.currentAccount, 0);
    }

    public static String getWallpaperEmoticon(TLRPC.WallPaper wallPaper) {
        if (wallPaper == null) {
            return null;
        }
        TLRPC.WallPaperSettings wallPaperSettings = wallPaper.settings;
        return (wallPaperSettings == null || TextUtils.isEmpty(wallPaperSettings.emoticon)) ? "" : wallPaper.settings.emoticon;
    }

    private void init() {
        SharedPreferences sharedPreferences = getSharedPreferences();
        this.themesHash = 0L;
        this.lastReloadTimeMs = 0L;
        try {
            this.themesHash = sharedPreferences.getLong("hash", 0L);
            this.lastReloadTimeMs = sharedPreferences.getLong("lastReload", 0L);
        } catch (Exception e) {
            FileLog.e(e);
        }
        this.allChatThemes = getAllChatThemesFromPrefs();
        preloadSticker("❌");
        if (this.allChatThemes.isEmpty()) {
            return;
        }
        Iterator<EmojiThemes> it = this.allChatThemes.iterator();
        while (it.hasNext()) {
            preloadSticker(it.next().getEmoticon());
        }
    }

    public static boolean isNotEmoticonWallpaper(TLRPC.WallPaper wallPaper) {
        String wallpaperEmoticon = getWallpaperEmoticon(wallPaper);
        return wallpaperEmoticon != null && wallpaperEmoticon.length() == 0;
    }

    public static void lambda$clearWallpaper$10(TLObject tLObject, TLRPC.TL_error tL_error) {
    }

    public static void lambda$getWallpaperBitmap$6(java.io.File r1, final org.telegram.tgnet.ResultCallback r2) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.ChatThemeController.lambda$getWallpaperBitmap$6(java.io.File, org.telegram.tgnet.ResultCallback):void");
    }

    public void lambda$preloadAllWallpaperThumbs$4(Pair pair) {
        if (pair != null) {
            this.themeIdWallpaperThumbMap.put((Long) pair.first, (Bitmap) pair.second);
        }
    }

    public void lambda$processUpdate$8(long j, TLRPC.UserFull userFull) {
        NotificationCenter.getInstance(this.currentAccount).lambda$postNotificationNameOnUIThread$1(NotificationCenter.userInfoDidLoad, Long.valueOf(j), userFull);
    }

    public void lambda$processUpdate$9(TLRPC.ChatFull chatFull) {
        NotificationCenter notificationCenter = NotificationCenter.getInstance(this.currentAccount);
        int i = NotificationCenter.chatInfoDidLoad;
        Boolean bool = Boolean.FALSE;
        notificationCenter.lambda$postNotificationNameOnUIThread$1(i, chatFull, 0, bool, bool);
    }

    public void lambda$requestAllChatThemes$1(List list, ResultCallback resultCallback) {
        this.allChatThemes = new ArrayList(list);
        resultCallback.onComplete(list);
    }

    public void lambda$requestAllChatThemes$2(org.telegram.tgnet.TLObject r8, final org.telegram.tgnet.ResultCallback r9, final org.telegram.tgnet.TLRPC.TL_error r10, boolean r11) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.ChatThemeController.lambda$requestAllChatThemes$2(org.telegram.tgnet.TLObject, org.telegram.tgnet.ResultCallback, org.telegram.tgnet.TLRPC$TL_error, boolean):void");
    }

    public void lambda$requestAllChatThemes$3(final ResultCallback resultCallback, final boolean z, final TLObject tLObject, final TLRPC.TL_error tL_error) {
        chatThemeQueue.postRunnable(new Runnable() {
            @Override
            public final void run() {
                ChatThemeController.this.lambda$requestAllChatThemes$2(tLObject, resultCallback, tL_error, z);
            }
        });
    }

    public static void lambda$saveWallpaperBitmap$7(File file, Bitmap bitmap) {
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 87, fileOutputStream);
            fileOutputStream.close();
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    public void lambda$setWallpaperToPeer$11(TLObject tLObject, long j, boolean z, String str, Runnable runnable) {
        TLRPC.ChatFull chatFull;
        TLRPC.UserFull userFull;
        String str2;
        if (tLObject instanceof TLRPC.Updates) {
            TLRPC.Updates updates = (TLRPC.Updates) tLObject;
            TLRPC.WallPaper wallPaper = null;
            MessagesController messagesController = MessagesController.getInstance(this.currentAccount);
            if (j >= 0) {
                userFull = messagesController.getUserFull(j);
                chatFull = null;
            } else {
                chatFull = messagesController.getChatFull(-j);
                userFull = null;
            }
            if (userFull != null) {
                wallPaper = userFull.wallpaper;
            } else if (chatFull != null) {
                wallPaper = chatFull.wallpaper;
            }
            int i = 0;
            while (true) {
                if (i >= updates.updates.size()) {
                    break;
                }
                if (updates.updates.get(i) instanceof TLRPC.TL_updateNewMessage) {
                    TLRPC.MessageAction messageAction = ((TLRPC.TL_updateNewMessage) updates.updates.get(i)).message.action;
                    if (messageAction instanceof TLRPC.TL_messageActionSetChatWallPaper) {
                        if (z) {
                            TLRPC.TL_messageActionSetChatWallPaper tL_messageActionSetChatWallPaper = (TLRPC.TL_messageActionSetChatWallPaper) messageAction;
                            tL_messageActionSetChatWallPaper.wallpaper.uploadingImage = str;
                            if (wallPaper != null && (str2 = wallPaper.uploadingImage) != null && str2.equals(str)) {
                                tL_messageActionSetChatWallPaper.wallpaper.stripedThumb = wallPaper.stripedThumb;
                            }
                            if (userFull != null) {
                                TLRPC.WallPaper wallPaper2 = tL_messageActionSetChatWallPaper.wallpaper;
                                userFull.wallpaper = wallPaper2;
                                userFull.flags |= 16777216;
                                saveChatWallpaper(j, wallPaper2);
                                getMessagesStorage().updateUserInfo(userFull, false);
                                NotificationCenter.getInstance(this.currentAccount).lambda$postNotificationNameOnUIThread$1(NotificationCenter.userInfoDidLoad, Long.valueOf(j), userFull);
                            } else if (chatFull != null) {
                                TLRPC.WallPaper wallPaper3 = tL_messageActionSetChatWallPaper.wallpaper;
                                chatFull.wallpaper = wallPaper3;
                                chatFull.flags2 |= 128;
                                saveChatWallpaper(j, wallPaper3);
                                getMessagesStorage().updateChatInfo(chatFull, false);
                                NotificationCenter notificationCenter = NotificationCenter.getInstance(this.currentAccount);
                                int i2 = NotificationCenter.chatInfoDidLoad;
                                Boolean bool = Boolean.FALSE;
                                notificationCenter.lambda$postNotificationNameOnUIThread$1(i2, chatFull, 0, bool, bool);
                            }
                        }
                    }
                }
                i++;
            }
            MessagesController.getInstance(this.currentAccount).processUpdateArray(updates.updates, updates.users, updates.chats, false, updates.date);
            if (runnable != null) {
                runnable.run();
            }
            NotificationCenter.getInstance(this.currentAccount).lambda$postNotificationNameOnUIThread$1(NotificationCenter.wallpaperSettedToUser, new Object[0]);
        }
    }

    public void lambda$setWallpaperToPeer$12(final long j, final boolean z, final String str, final Runnable runnable, final TLObject tLObject, TLRPC.TL_error tL_error) {
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                ChatThemeController.this.lambda$setWallpaperToPeer$11(tLObject, j, z, str, runnable);
            }
        });
    }

    private void preloadSticker(String str) {
        new ImageReceiver().setImage(ImageLocation.getForDocument(MediaDataController.getInstance(UserConfig.selectedAccount).getEmojiAnimatedSticker(str)), "50_50", null, null, null, 0);
        Emoji.preloadEmoji(str);
    }

    public static boolean wallpaperEquals(TLRPC.WallPaper wallPaper, TLRPC.WallPaper wallPaper2) {
        if (wallPaper == null && wallPaper2 == null) {
            return true;
        }
        if ((wallPaper instanceof TLRPC.TL_wallPaper) && (wallPaper2 instanceof TLRPC.TL_wallPaper)) {
            return wallPaper.id == wallPaper2.id;
        }
        if ((wallPaper instanceof TLRPC.TL_wallPaperNoFile) && (wallPaper2 instanceof TLRPC.TL_wallPaperNoFile)) {
            return (wallPaper.settings == null || wallPaper2.settings == null) ? wallPaper.id == wallPaper2.id : TextUtils.equals(getWallpaperEmoticon(wallPaper), getWallpaperEmoticon(wallPaper2));
        }
        return false;
    }

    public void clearCache() {
        this.themesHash = 0L;
        this.lastReloadTimeMs = 0L;
        getSharedPreferences().edit().clear().apply();
    }

    public void clearWallpaper(long j, boolean z) {
        clearWallpaper(j, z, false);
    }

    public void clearWallpaper(long j, boolean z, boolean z2) {
        TLRPC.TL_messages_setChatWallPaper tL_messages_setChatWallPaper = new TLRPC.TL_messages_setChatWallPaper();
        if (j >= 0) {
            tL_messages_setChatWallPaper.peer = MessagesController.getInputPeer(MessagesController.getInstance(this.currentAccount).getUser(Long.valueOf(j)));
            tL_messages_setChatWallPaper.revert = z2;
            if (!z2) {
                TLRPC.UserFull userFull = getMessagesController().getUserFull(j);
                if (userFull != null) {
                    userFull.wallpaper = null;
                    userFull.flags &= -16777217;
                    getMessagesStorage().updateUserInfo(userFull, false);
                }
                saveChatWallpaper(j, null);
                if (z) {
                    NotificationCenter.getInstance(this.currentAccount).lambda$postNotificationNameOnUIThread$1(NotificationCenter.userInfoDidLoad, Long.valueOf(j), userFull);
                }
            }
        } else {
            long j2 = -j;
            tL_messages_setChatWallPaper.peer = MessagesController.getInputPeer(MessagesController.getInstance(this.currentAccount).getChat(Long.valueOf(j2)));
            TLRPC.ChatFull chatFull = getMessagesController().getChatFull(j2);
            if (chatFull != null) {
                chatFull.wallpaper = null;
                chatFull.flags2 &= -129;
                getMessagesStorage().updateChatInfo(chatFull, false);
            }
            saveChatWallpaper(j, null);
            if (z) {
                NotificationCenter notificationCenter = NotificationCenter.getInstance(this.currentAccount);
                int i = NotificationCenter.chatInfoDidLoad;
                Boolean bool = Boolean.FALSE;
                notificationCenter.lambda$postNotificationNameOnUIThread$1(i, chatFull, 0, bool, bool);
            }
        }
        getConnectionsManager().sendRequest(tL_messages_setChatWallPaper, new RequestDelegate() {
            @Override
            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                ChatThemeController.lambda$clearWallpaper$10(tLObject, tL_error);
            }
        });
    }

    public void clearWallpaperImages() {
    }

    public void clearWallpaperThumbImages() {
        this.themeIdWallpaperThumbMap.clear();
    }

    public EmojiThemes getDialogTheme(long j) {
        String str = this.dialogEmoticonsMap.get(j);
        if (str == null) {
            str = getEmojiSharedPreferences().getString("chatTheme_" + this.currentAccount + "_" + j, null);
            this.dialogEmoticonsMap.put(j, str);
        }
        return getTheme(str);
    }

    public TLRPC.WallPaper getDialogWallpaper(long j) {
        MessagesController messagesController = getMessagesController();
        if (j >= 0) {
            TLRPC.UserFull userFull = messagesController.getUserFull(j);
            if (userFull != null) {
                return userFull.wallpaper;
            }
        } else {
            TLRPC.ChatFull chatFull = messagesController.getChatFull(-j);
            if (chatFull != null) {
                return chatFull.wallpaper;
            }
        }
        String string = getEmojiSharedPreferences().getString("chatWallpaper_" + this.currentAccount + "_" + j, null);
        if (string != null) {
            SerializedData serializedData = new SerializedData(Utilities.hexToBytes(string));
            try {
                return TLRPC.WallPaper.TLdeserialize(serializedData, serializedData.readInt32(true), true);
            } catch (Throwable th) {
                FileLog.e(th);
            }
        }
        return null;
    }

    public EmojiThemes getTheme(String str) {
        if (str == null) {
            return null;
        }
        for (EmojiThemes emojiThemes : this.allChatThemes) {
            if (str.equals(emojiThemes.getEmoticon())) {
                return emojiThemes;
            }
        }
        return null;
    }

    public void getWallpaperBitmap(long j, final ResultCallback<Bitmap> resultCallback) {
        if (this.themesHash == 0) {
            resultCallback.onComplete(null);
        } else {
            final File patternFile = getPatternFile(j);
            chatThemeQueue.postRunnable(new Runnable() {
                @Override
                public final void run() {
                    ChatThemeController.lambda$getWallpaperBitmap$6(patternFile, resultCallback);
                }
            });
        }
    }

    public Bitmap getWallpaperThumbBitmap(long j) {
        return this.themeIdWallpaperThumbMap.get(Long.valueOf(j));
    }

    public void preloadAllWallpaperImages(boolean z) {
        for (EmojiThemes emojiThemes : this.allChatThemes) {
            TLRPC.TL_theme tlTheme = emojiThemes.getTlTheme(z ? 1 : 0);
            if (tlTheme != null && !getPatternFile(tlTheme.id).exists()) {
                emojiThemes.loadWallpaper(z ? 1 : 0, null);
            }
        }
    }

    public void preloadAllWallpaperThumbs(boolean z) {
        for (EmojiThemes emojiThemes : this.allChatThemes) {
            TLRPC.TL_theme tlTheme = emojiThemes.getTlTheme(z ? 1 : 0);
            if (tlTheme != null) {
                if (!this.themeIdWallpaperThumbMap.containsKey(Long.valueOf(tlTheme.id))) {
                    emojiThemes.loadWallpaperThumb(z ? 1 : 0, new ResultCallback() {
                        @Override
                        public final void onComplete(Object obj) {
                            ChatThemeController.this.lambda$preloadAllWallpaperThumbs$4((Pair) obj);
                        }

                        @Override
                        public void onError(Throwable th) {
                            ResultCallback.CC.$default$onError(this, th);
                        }

                        @Override
                        public void onError(TLRPC.TL_error tL_error) {
                            ResultCallback.CC.$default$onError(this, tL_error);
                        }
                    });
                }
            }
        }
    }

    public void processUpdate(TLRPC.TL_updatePeerWallpaper tL_updatePeerWallpaper) {
        int i;
        Runnable runnable;
        int i2;
        if (tL_updatePeerWallpaper.peer instanceof TLRPC.TL_peerUser) {
            final TLRPC.UserFull userFull = getMessagesController().getUserFull(tL_updatePeerWallpaper.peer.user_id);
            if (userFull == null || wallpaperEquals(userFull.wallpaper, tL_updatePeerWallpaper.wallpaper)) {
                return;
            }
            final long j = userFull.id;
            if ((tL_updatePeerWallpaper.flags & 1) != 0) {
                userFull.wallpaper_overridden = tL_updatePeerWallpaper.wallpaper_overridden;
                userFull.wallpaper = tL_updatePeerWallpaper.wallpaper;
                i2 = userFull.flags | 16777216;
            } else {
                userFull.wallpaper_overridden = false;
                userFull.wallpaper = null;
                i2 = userFull.flags & (-16777217);
            }
            userFull.flags = i2;
            getMessagesStorage().updateUserInfo(userFull, false);
            saveChatWallpaper(j, userFull.wallpaper);
            runnable = new Runnable() {
                @Override
                public final void run() {
                    ChatThemeController.this.lambda$processUpdate$8(j, userFull);
                }
            };
        } else {
            final TLRPC.ChatFull chatFull = getMessagesController().getChatFull(-DialogObject.getPeerDialogId(tL_updatePeerWallpaper.peer));
            if (chatFull == null || wallpaperEquals(chatFull.wallpaper, tL_updatePeerWallpaper.wallpaper)) {
                return;
            }
            long j2 = -chatFull.id;
            if ((tL_updatePeerWallpaper.flags & 1) != 0) {
                chatFull.wallpaper = tL_updatePeerWallpaper.wallpaper;
                i = chatFull.flags2 | 128;
            } else {
                chatFull.wallpaper = null;
                i = chatFull.flags2 & (-129);
            }
            chatFull.flags2 = i;
            getMessagesStorage().updateChatInfo(chatFull, false);
            saveChatWallpaper(j2, chatFull.wallpaper);
            runnable = new Runnable() {
                @Override
                public final void run() {
                    ChatThemeController.this.lambda$processUpdate$9(chatFull);
                }
            };
        }
        AndroidUtilities.runOnUIThread(runnable);
    }

    public void requestAllChatThemes(final ResultCallback<List<EmojiThemes>> resultCallback, final boolean z) {
        if (this.themesHash == 0 || this.lastReloadTimeMs == 0) {
            init();
        }
        boolean z2 = System.currentTimeMillis() - this.lastReloadTimeMs > 7200000;
        List<EmojiThemes> list = this.allChatThemes;
        if (list == null || list.isEmpty() || z2) {
            TLRPC.TL_account_getChatThemes tL_account_getChatThemes = new TLRPC.TL_account_getChatThemes();
            tL_account_getChatThemes.hash = this.themesHash;
            ConnectionsManager.getInstance(UserConfig.selectedAccount).sendRequest(tL_account_getChatThemes, new RequestDelegate() {
                @Override
                public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                    ChatThemeController.this.lambda$requestAllChatThemes$3(resultCallback, z, tLObject, tL_error);
                }
            });
        }
        List<EmojiThemes> list2 = this.allChatThemes;
        if (list2 == null || list2.isEmpty()) {
            return;
        }
        ArrayList arrayList = new ArrayList(this.allChatThemes);
        if (z && !arrayList.get(0).showAsDefaultStub) {
            arrayList.add(0, EmojiThemes.createChatThemesDefault(this.currentAccount));
        }
        Iterator<EmojiThemes> it = arrayList.iterator();
        while (it.hasNext()) {
            it.next().initColors();
        }
        resultCallback.onComplete(arrayList);
    }

    public void requestChatTheme(final String str, final ResultCallback<EmojiThemes> resultCallback) {
        if (TextUtils.isEmpty(str)) {
            resultCallback.onComplete(null);
        } else {
            requestAllChatThemes(new ResultCallback<List<EmojiThemes>>() {
                @Override
                public void onComplete(List<EmojiThemes> list) {
                    for (EmojiThemes emojiThemes : list) {
                        if (str.equals(emojiThemes.getEmoticon())) {
                            emojiThemes.initColors();
                            resultCallback.onComplete(emojiThemes);
                            return;
                        }
                    }
                }

                @Override
                public void onError(Throwable th) {
                    ResultCallback.CC.$default$onError(this, th);
                }

                @Override
                public void onError(TLRPC.TL_error tL_error) {
                    resultCallback.onComplete(null);
                }
            }, false);
        }
    }

    public void saveChatWallpaper(long j, TLRPC.WallPaper wallPaper) {
        SharedPreferences.Editor remove;
        if (wallPaper == null) {
            remove = getEmojiSharedPreferences().edit().remove("chatWallpaper_" + this.currentAccount + "_" + j);
        } else {
            if (wallPaper.document == null) {
                return;
            }
            SerializedData serializedData = new SerializedData(wallPaper.getObjectSize());
            wallPaper.serializeToStream(serializedData);
            String bytesToHex = Utilities.bytesToHex(serializedData.toByteArray());
            remove = getEmojiSharedPreferences().edit().putString("chatWallpaper_" + this.currentAccount + "_" + j, bytesToHex);
        }
        remove.apply();
    }

    public void saveWallpaperBitmap(final Bitmap bitmap, long j) {
        final File patternFile = getPatternFile(j);
        chatThemeQueue.postRunnable(new Runnable() {
            @Override
            public final void run() {
                ChatThemeController.lambda$saveWallpaperBitmap$7(patternFile, bitmap);
            }
        });
    }

    public void setDialogTheme(long j, String str, boolean z) {
        if (TextUtils.equals(this.dialogEmoticonsMap.get(j), str)) {
            return;
        }
        LongSparseArray<String> longSparseArray = this.dialogEmoticonsMap;
        if (str == null) {
            longSparseArray.delete(j);
        } else {
            longSparseArray.put(j, str);
        }
        MessagesController messagesController = getMessagesController();
        if (j >= 0) {
            TLRPC.UserFull userFull = messagesController.getUserFull(j);
            if (userFull != null) {
                userFull.theme_emoticon = str;
                getMessagesStorage().updateUserInfo(userFull, true);
            }
        } else {
            TLRPC.ChatFull chatFull = messagesController.getChatFull(-j);
            if (chatFull != null) {
                chatFull.theme_emoticon = str;
                getMessagesStorage().updateChatInfo(chatFull, true);
            }
        }
        getEmojiSharedPreferences().edit().putString("chatTheme_" + this.currentAccount + "_" + j, str).apply();
        if (z) {
            TLRPC.TL_messages_setChatTheme tL_messages_setChatTheme = new TLRPC.TL_messages_setChatTheme();
            if (str == null) {
                str = "";
            }
            tL_messages_setChatTheme.emoticon = str;
            tL_messages_setChatTheme.peer = getMessagesController().getInputPeer(j);
            getConnectionsManager().sendRequest(tL_messages_setChatTheme, null);
        }
    }

    public int setWallpaperToPeer(final long j, final String str, Theme.OverrideWallpaperInfo overrideWallpaperInfo, MessageObject messageObject, final Runnable runnable) {
        ChatThemeController chatThemeController;
        TLRPC.TL_messages_setChatWallPaper tL_messages_setChatWallPaper;
        boolean z;
        TLRPC.ChatFull chatFull;
        TLRPC.UserFull userFull;
        String str2;
        TLRPC.TL_messages_setChatWallPaper tL_messages_setChatWallPaper2 = new TLRPC.TL_messages_setChatWallPaper();
        tL_messages_setChatWallPaper2.peer = j >= 0 ? MessagesController.getInputPeer(MessagesController.getInstance(this.currentAccount).getUser(Long.valueOf(j))) : MessagesController.getInputPeer(MessagesController.getInstance(this.currentAccount).getChat(Long.valueOf(-j)));
        tL_messages_setChatWallPaper2.for_both = overrideWallpaperInfo.forBoth;
        if (messageObject == null || !(messageObject.messageOwner.action instanceof TLRPC.TL_messageActionSetChatWallPaper)) {
            chatThemeController = this;
            tL_messages_setChatWallPaper = tL_messages_setChatWallPaper2;
            z = true;
            tL_messages_setChatWallPaper.flags |= 1;
            tL_messages_setChatWallPaper.wallpaper = MessagesController.getInputWallpaper(overrideWallpaperInfo);
        } else {
            tL_messages_setChatWallPaper2.flags |= 2;
            tL_messages_setChatWallPaper2.id = messageObject.getId();
            TLRPC.WallPaper wallPaper = null;
            MessagesController messagesController = MessagesController.getInstance(this.currentAccount);
            if (j >= 0) {
                userFull = messagesController.getUserFull(j);
                chatFull = null;
            } else {
                chatFull = messagesController.getChatFull(-j);
                userFull = null;
            }
            TLRPC.TL_messageActionSetChatWallPaper tL_messageActionSetChatWallPaper = (TLRPC.TL_messageActionSetChatWallPaper) messageObject.messageOwner.action;
            TLRPC.TL_wallPaper tL_wallPaper = new TLRPC.TL_wallPaper();
            TLRPC.WallPaper wallPaper2 = tL_messageActionSetChatWallPaper.wallpaper;
            tL_wallPaper.id = wallPaper2.id;
            tL_wallPaper.document = wallPaper2.document;
            TLRPC.TL_wallPaperSettings tL_wallPaperSettings = new TLRPC.TL_wallPaperSettings();
            tL_wallPaper.settings = tL_wallPaperSettings;
            tL_wallPaperSettings.intensity = (int) (overrideWallpaperInfo.intensity * 100.0f);
            tL_wallPaperSettings.motion = overrideWallpaperInfo.isMotion;
            tL_wallPaperSettings.blur = overrideWallpaperInfo.isBlurred;
            tL_wallPaperSettings.background_color = overrideWallpaperInfo.color;
            tL_wallPaperSettings.second_background_color = overrideWallpaperInfo.gradientColor1;
            tL_wallPaperSettings.third_background_color = overrideWallpaperInfo.gradientColor2;
            tL_wallPaperSettings.fourth_background_color = overrideWallpaperInfo.gradientColor3;
            tL_wallPaperSettings.rotation = overrideWallpaperInfo.rotation;
            tL_wallPaper.uploadingImage = str;
            if (userFull != null) {
                wallPaper = userFull.wallpaper;
            } else if (chatFull != null) {
                wallPaper = chatFull.wallpaper;
            }
            if (wallPaper != null && (str2 = wallPaper.uploadingImage) != null && str2.equals(str)) {
                tL_wallPaper.stripedThumb = wallPaper.stripedThumb;
            }
            tL_wallPaper.settings.flags |= 121;
            TLRPC.TL_wallPaper tL_wallPaper2 = new TLRPC.TL_wallPaper();
            TLRPC.WallPaper wallPaper3 = tL_messageActionSetChatWallPaper.wallpaper;
            tL_wallPaper2.pattern = wallPaper3.pattern;
            tL_wallPaper2.id = wallPaper3.id;
            tL_wallPaper2.document = wallPaper3.document;
            int i = wallPaper3.flags;
            tL_wallPaper2.creator = wallPaper3.creator;
            tL_wallPaper2.dark = wallPaper3.dark;
            tL_wallPaper2.isDefault = wallPaper3.isDefault;
            tL_wallPaper2.slug = wallPaper3.slug;
            TLRPC.UserFull userFull2 = userFull;
            tL_wallPaper2.access_hash = wallPaper3.access_hash;
            tL_wallPaper2.stripedThumb = wallPaper3.stripedThumb;
            tL_wallPaper2.settings = tL_wallPaper.settings;
            tL_wallPaper2.flags = i | 4;
            if (userFull2 != null) {
                userFull2.wallpaper = tL_wallPaper2;
                userFull2.flags |= 16777216;
                getMessagesStorage().updateUserInfo(userFull2, false);
                chatThemeController = this;
                NotificationCenter.getInstance(chatThemeController.currentAccount).lambda$postNotificationNameOnUIThread$1(NotificationCenter.userInfoDidLoad, Long.valueOf(j), userFull2);
            } else {
                chatThemeController = this;
                if (chatFull != null) {
                    chatFull.wallpaper = tL_wallPaper2;
                    chatFull.flags2 |= 128;
                    getMessagesStorage().updateChatInfo(chatFull, false);
                    NotificationCenter notificationCenter = NotificationCenter.getInstance(chatThemeController.currentAccount);
                    int i2 = NotificationCenter.chatInfoDidLoad;
                    Boolean bool = Boolean.FALSE;
                    notificationCenter.lambda$postNotificationNameOnUIThread$1(i2, chatFull, 0, bool, bool);
                }
            }
            if (runnable != null) {
                runnable.run();
            }
            tL_messages_setChatWallPaper = tL_messages_setChatWallPaper2;
            z = false;
        }
        tL_messages_setChatWallPaper.flags |= 4;
        tL_messages_setChatWallPaper.settings = MessagesController.getWallpaperSetting(overrideWallpaperInfo);
        final boolean z2 = z;
        return ConnectionsManager.getInstance(chatThemeController.currentAccount).sendRequest(tL_messages_setChatWallPaper, new RequestDelegate() {
            @Override
            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                ChatThemeController.this.lambda$setWallpaperToPeer$12(j, z2, str, runnable, tLObject, tL_error);
            }
        });
    }
}
