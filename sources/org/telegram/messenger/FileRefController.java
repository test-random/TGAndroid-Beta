package org.telegram.messenger;

import android.os.SystemClock;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import org.telegram.messenger.SendMessagesHelper;
import org.telegram.messenger.Utilities;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.tgnet.tl.TL_account;
import org.telegram.tgnet.tl.TL_bots;
import org.telegram.tgnet.tl.TL_stories;
import org.telegram.ui.Stories.StoriesController;

public class FileRefController extends BaseController {
    private static volatile FileRefController[] Instance = new FileRefController[4];
    private ArrayList<Waiter> favStickersWaiter;
    private long lastCleanupTime;
    private HashMap<String, ArrayList<Requester>> locationRequester;
    private HashMap<TLObject, Object[]> multiMediaCache;
    private HashMap<String, ArrayList<Requester>> parentRequester;
    private ArrayList<Waiter> recentStickersWaiter;
    private HashMap<String, CachedResult> responseCache;
    private ArrayList<Waiter> savedGifsWaiters;
    private ArrayList<Waiter> wallpaperWaiters;

    public static class CachedResult {
        private long firstQueryTime;
        private TLObject response;

        private CachedResult() {
        }
    }

    public static class Requester {
        private Object[] args;
        private boolean completed;
        private TLRPC.InputFileLocation location;
        private String locationKey;

        private Requester() {
        }
    }

    public static class Waiter {
        private String locationKey;
        private String parentKey;

        public Waiter(String str, String str2) {
            this.locationKey = str;
            this.parentKey = str2;
        }
    }

    public FileRefController(int i) {
        super(i);
        this.locationRequester = new HashMap<>();
        this.parentRequester = new HashMap<>();
        this.responseCache = new HashMap<>();
        this.multiMediaCache = new HashMap<>();
        this.lastCleanupTime = SystemClock.elapsedRealtime();
        this.wallpaperWaiters = new ArrayList<>();
        this.savedGifsWaiters = new ArrayList<>();
        this.recentStickersWaiter = new ArrayList<>();
        this.favStickersWaiter = new ArrayList<>();
    }

    private void broadcastWaitersData(ArrayList<Waiter> arrayList, TLObject tLObject, TLRPC.TL_error tL_error) {
        int size = arrayList.size();
        int i = 0;
        while (i < size) {
            Waiter waiter = arrayList.get(i);
            onRequestComplete(waiter.locationKey, waiter.parentKey, tLObject, tL_error, i == size + (-1), false);
            i++;
        }
        arrayList.clear();
    }

    private void cleanupCache() {
        if (Math.abs(SystemClock.elapsedRealtime() - this.lastCleanupTime) < 600000) {
            return;
        }
        this.lastCleanupTime = SystemClock.elapsedRealtime();
        ArrayList arrayList = null;
        for (Map.Entry<String, CachedResult> entry : this.responseCache.entrySet()) {
            if (Math.abs(System.currentTimeMillis() - entry.getValue().firstQueryTime) >= 60000) {
                if (arrayList == null) {
                    arrayList = new ArrayList();
                }
                arrayList.add(entry.getKey());
            }
        }
        if (arrayList != null) {
            int size = arrayList.size();
            for (int i = 0; i < size; i++) {
                this.responseCache.remove(arrayList.get(i));
            }
        }
    }

    private CachedResult getCachedResponse(String str) {
        CachedResult cachedResult = this.responseCache.get(str);
        if (cachedResult == null || Math.abs(System.currentTimeMillis() - cachedResult.firstQueryTime) < 60000) {
            return cachedResult;
        }
        this.responseCache.remove(str);
        return null;
    }

    public static int getFileRefErrorIndex(String str) {
        if (str != null && str.startsWith("FILE_REFERENCE_") && str.endsWith("_EXPIRED")) {
            try {
                return Integer.parseInt(str.substring(15, str.length() - 8));
            } catch (Exception unused) {
            }
        }
        return -1;
    }

    private byte[] getFileReference(TLRPC.Chat chat, TLRPC.InputFileLocation inputFileLocation, boolean[] zArr, TLRPC.InputFileLocation[] inputFileLocationArr) {
        TLRPC.ChatPhoto chatPhoto;
        byte[] bArr = null;
        if (chat != null && (chatPhoto = chat.photo) != null && ((inputFileLocation instanceof TLRPC.TL_inputFileLocation) || (inputFileLocation instanceof TLRPC.TL_inputPeerPhotoFileLocation))) {
            if (inputFileLocation instanceof TLRPC.TL_inputPeerPhotoFileLocation) {
                zArr[0] = true;
                if (getPeerReferenceReplacement(null, chat, false, inputFileLocation, inputFileLocationArr, zArr)) {
                    return new byte[0];
                }
                return null;
            }
            bArr = getFileReference(chatPhoto.photo_small, inputFileLocation, zArr);
            if (getPeerReferenceReplacement(null, chat, false, inputFileLocation, inputFileLocationArr, zArr)) {
                return new byte[0];
            }
            if (bArr == null) {
                bArr = getFileReference(chat.photo.photo_big, inputFileLocation, zArr);
                if (getPeerReferenceReplacement(null, chat, true, inputFileLocation, inputFileLocationArr, zArr)) {
                    return new byte[0];
                }
            }
        }
        return bArr;
    }

    private byte[] getFileReference(TLRPC.Document document, ArrayList<TLRPC.Document> arrayList, TLRPC.InputFileLocation inputFileLocation, boolean[] zArr, TLRPC.InputFileLocation[] inputFileLocationArr) {
        if (document != null && inputFileLocation != null) {
            if (!(inputFileLocation instanceof TLRPC.TL_inputDocumentFileLocation)) {
                int size = document.thumbs.size();
                for (int i = 0; i < size; i++) {
                    TLRPC.PhotoSize photoSize = document.thumbs.get(i);
                    byte[] fileReference = getFileReference(photoSize, inputFileLocation, zArr);
                    if (zArr != null && zArr[0]) {
                        TLRPC.TL_inputDocumentFileLocation tL_inputDocumentFileLocation = new TLRPC.TL_inputDocumentFileLocation();
                        inputFileLocationArr[0] = tL_inputDocumentFileLocation;
                        tL_inputDocumentFileLocation.id = document.id;
                        tL_inputDocumentFileLocation.volume_id = inputFileLocation.volume_id;
                        tL_inputDocumentFileLocation.local_id = inputFileLocation.local_id;
                        tL_inputDocumentFileLocation.access_hash = document.access_hash;
                        byte[] bArr = document.file_reference;
                        tL_inputDocumentFileLocation.file_reference = bArr;
                        tL_inputDocumentFileLocation.thumb_size = photoSize.type;
                        return bArr;
                    }
                    if (fileReference != null) {
                        return fileReference;
                    }
                }
            } else if (document.id == inputFileLocation.id) {
                return document.file_reference;
            }
            if (arrayList != null) {
                for (int i2 = 0; i2 < arrayList.size(); i2++) {
                    byte[] fileReference2 = getFileReference(arrayList.get(i2), null, inputFileLocation, zArr, inputFileLocationArr);
                    if (fileReference2 != null) {
                        return fileReference2;
                    }
                }
            }
        }
        return null;
    }

    private byte[] getFileReference(TLRPC.FileLocation fileLocation, TLRPC.InputFileLocation inputFileLocation, boolean[] zArr) {
        if (fileLocation == null || !(inputFileLocation instanceof TLRPC.TL_inputFileLocation) || fileLocation.local_id != inputFileLocation.local_id || fileLocation.volume_id != inputFileLocation.volume_id) {
            return null;
        }
        byte[] bArr = fileLocation.file_reference;
        if (bArr == null && zArr != null) {
            zArr[0] = true;
        }
        return bArr;
    }

    private byte[] getFileReference(TLRPC.Photo photo, TLRPC.InputFileLocation inputFileLocation, boolean[] zArr, TLRPC.InputFileLocation[] inputFileLocationArr) {
        if (photo == null) {
            return null;
        }
        if (inputFileLocation instanceof TLRPC.TL_inputPhotoFileLocation) {
            if (photo.id == inputFileLocation.id) {
                return photo.file_reference;
            }
            return null;
        }
        if (inputFileLocation instanceof TLRPC.TL_inputFileLocation) {
            int size = photo.sizes.size();
            for (int i = 0; i < size; i++) {
                TLRPC.PhotoSize photoSize = photo.sizes.get(i);
                byte[] fileReference = getFileReference(photoSize, inputFileLocation, zArr);
                if (zArr != null && zArr[0]) {
                    TLRPC.TL_inputPhotoFileLocation tL_inputPhotoFileLocation = new TLRPC.TL_inputPhotoFileLocation();
                    inputFileLocationArr[0] = tL_inputPhotoFileLocation;
                    tL_inputPhotoFileLocation.id = photo.id;
                    tL_inputPhotoFileLocation.volume_id = inputFileLocation.volume_id;
                    tL_inputPhotoFileLocation.local_id = inputFileLocation.local_id;
                    tL_inputPhotoFileLocation.access_hash = photo.access_hash;
                    byte[] bArr = photo.file_reference;
                    tL_inputPhotoFileLocation.file_reference = bArr;
                    tL_inputPhotoFileLocation.thumb_size = photoSize.type;
                    return bArr;
                }
                if (fileReference != null) {
                    return fileReference;
                }
            }
        }
        return null;
    }

    private byte[] getFileReference(TLRPC.PhotoSize photoSize, TLRPC.InputFileLocation inputFileLocation, boolean[] zArr) {
        if (photoSize == null || !(inputFileLocation instanceof TLRPC.TL_inputFileLocation)) {
            return null;
        }
        return getFileReference(photoSize.location, inputFileLocation, zArr);
    }

    private byte[] getFileReference(TLRPC.User user, TLRPC.InputFileLocation inputFileLocation, boolean[] zArr, TLRPC.InputFileLocation[] inputFileLocationArr) {
        TLRPC.UserProfilePhoto userProfilePhoto;
        if (user == null || (userProfilePhoto = user.photo) == null || !(inputFileLocation instanceof TLRPC.TL_inputFileLocation)) {
            return null;
        }
        byte[] fileReference = getFileReference(userProfilePhoto.photo_small, inputFileLocation, zArr);
        if (getPeerReferenceReplacement(user, null, false, inputFileLocation, inputFileLocationArr, zArr)) {
            return new byte[0];
        }
        if (fileReference == null) {
            fileReference = getFileReference(user.photo.photo_big, inputFileLocation, zArr);
            if (getPeerReferenceReplacement(user, null, true, inputFileLocation, inputFileLocationArr, zArr)) {
                return new byte[0];
            }
        }
        return fileReference;
    }

    private byte[] getFileReference(TLRPC.WebPage webPage, TLRPC.InputFileLocation inputFileLocation, boolean[] zArr, TLRPC.InputFileLocation[] inputFileLocationArr) {
        byte[] fileReference = getFileReference(webPage.document, null, inputFileLocation, zArr, inputFileLocationArr);
        if (fileReference != null) {
            return fileReference;
        }
        byte[] fileReference2 = getFileReference(webPage.photo, inputFileLocation, zArr, inputFileLocationArr);
        if (fileReference2 != null) {
            return fileReference2;
        }
        if (!webPage.attributes.isEmpty()) {
            int size = webPage.attributes.size();
            for (int i = 0; i < size; i++) {
                TLRPC.WebPageAttribute webPageAttribute = webPage.attributes.get(i);
                if (webPageAttribute instanceof TLRPC.TL_webPageAttributeTheme) {
                    TLRPC.TL_webPageAttributeTheme tL_webPageAttributeTheme = (TLRPC.TL_webPageAttributeTheme) webPageAttribute;
                    int size2 = tL_webPageAttributeTheme.documents.size();
                    int i2 = 0;
                    while (i2 < size2) {
                        int i3 = i2;
                        byte[] fileReference3 = getFileReference(tL_webPageAttributeTheme.documents.get(i2), null, inputFileLocation, zArr, inputFileLocationArr);
                        if (fileReference3 != null) {
                            return fileReference3;
                        }
                        i2 = i3 + 1;
                    }
                }
            }
        }
        TLRPC.Page page = webPage.cached_page;
        if (page == null) {
            return null;
        }
        int size3 = page.documents.size();
        int i4 = 0;
        while (true) {
            TLRPC.Page page2 = webPage.cached_page;
            if (i4 >= size3) {
                int size4 = page2.photos.size();
                for (int i5 = 0; i5 < size4; i5++) {
                    byte[] fileReference4 = getFileReference(webPage.cached_page.photos.get(i5), inputFileLocation, zArr, inputFileLocationArr);
                    if (fileReference4 != null) {
                        return fileReference4;
                    }
                }
                return null;
            }
            byte[] fileReference5 = getFileReference(page2.documents.get(i4), null, inputFileLocation, zArr, inputFileLocationArr);
            if (fileReference5 != null) {
                return fileReference5;
            }
            i4++;
        }
    }

    public static FileRefController getInstance(int i) {
        FileRefController fileRefController = Instance[i];
        if (fileRefController == null) {
            synchronized (FileRefController.class) {
                try {
                    fileRefController = Instance[i];
                    if (fileRefController == null) {
                        FileRefController[] fileRefControllerArr = Instance;
                        FileRefController fileRefController2 = new FileRefController(i);
                        fileRefControllerArr[i] = fileRefController2;
                        fileRefController = fileRefController2;
                    }
                } finally {
                }
            }
        }
        return fileRefController;
    }

    public static String getKeyForParentObject(Object obj) {
        TLRPC.Message message;
        TLRPC.MessageFwdHeader messageFwdHeader;
        TLRPC.Peer peer;
        String str;
        if (obj instanceof StoriesController.BotPreview) {
            StoriesController.BotPreview botPreview = (StoriesController.BotPreview) obj;
            if (botPreview.list != null) {
                TLRPC.MessageMedia messageMedia = botPreview.media;
                if (messageMedia.document != null) {
                    return "botstory_doc_" + botPreview.media.document.id;
                }
                if (messageMedia.photo != null) {
                    return "botstory_photo_" + botPreview.media.photo.id;
                }
                return "botstory_" + botPreview.id;
            }
            str = "failed request reference can't find list in botpreview";
        } else {
            if (!(obj instanceof TL_stories.StoryItem)) {
                if (obj instanceof TLRPC.TL_help_premiumPromo) {
                    return "premium_promo";
                }
                if (obj instanceof TLRPC.TL_availableReaction) {
                    return "available_reaction_" + ((TLRPC.TL_availableReaction) obj).reaction;
                }
                if (obj instanceof TL_bots.BotInfo) {
                    return "bot_info_" + ((TL_bots.BotInfo) obj).user_id;
                }
                if (obj instanceof TLRPC.TL_attachMenuBot) {
                    return "attach_menu_bot_" + ((TLRPC.TL_attachMenuBot) obj).bot_id;
                }
                if (obj instanceof MessageObject) {
                    MessageObject messageObject = (MessageObject) obj;
                    long channelId = messageObject.getChannelId();
                    if (messageObject.type == 29 && (message = messageObject.messageOwner) != null && (messageFwdHeader = message.fwd_from) != null && (peer = messageFwdHeader.from_id) != null) {
                        channelId = DialogObject.getPeerDialogId(peer);
                    }
                    return "message" + messageObject.getRealId() + "_" + channelId + "_" + messageObject.scheduled + "_" + messageObject.getQuickReplyId();
                }
                if (obj instanceof TLRPC.Message) {
                    TLRPC.Message message2 = (TLRPC.Message) obj;
                    TLRPC.Peer peer2 = message2.peer_id;
                    return "message" + message2.id + "_" + (peer2 != null ? peer2.channel_id : 0L) + "_" + message2.from_scheduled;
                }
                if (obj instanceof TLRPC.WebPage) {
                    return "webpage" + ((TLRPC.WebPage) obj).id;
                }
                if (obj instanceof TLRPC.User) {
                    return "user" + ((TLRPC.User) obj).id;
                }
                if (obj instanceof TLRPC.Chat) {
                    return "chat" + ((TLRPC.Chat) obj).id;
                }
                if (obj instanceof String) {
                    return "str" + ((String) obj);
                }
                if (obj instanceof TLRPC.TL_messages_stickerSet) {
                    return "set" + ((TLRPC.TL_messages_stickerSet) obj).set.id;
                }
                if (obj instanceof TLRPC.StickerSetCovered) {
                    return "set" + ((TLRPC.StickerSetCovered) obj).set.id;
                }
                if (obj instanceof TLRPC.InputStickerSet) {
                    return "set" + ((TLRPC.InputStickerSet) obj).id;
                }
                if (obj instanceof TLRPC.TL_wallPaper) {
                    return "wallpaper" + ((TLRPC.TL_wallPaper) obj).id;
                }
                if (obj instanceof TLRPC.TL_theme) {
                    return "theme" + ((TLRPC.TL_theme) obj).id;
                }
                if (obj == null) {
                    return null;
                }
                return "" + obj;
            }
            TL_stories.StoryItem storyItem = (TL_stories.StoryItem) obj;
            if (storyItem.dialogId != 0) {
                return "story_" + storyItem.dialogId + "_" + storyItem.id;
            }
            str = "failed request reference can't find dialogId";
        }
        FileLog.d(str);
        return null;
    }

    private String getObjectString(Object obj) {
        if (obj instanceof String) {
            return (String) obj;
        }
        if (obj instanceof TL_stories.StoryItem) {
            TL_stories.StoryItem storyItem = (TL_stories.StoryItem) obj;
            return "story(dialogId=" + storyItem.dialogId + " id=" + storyItem.id + ")";
        }
        if (!(obj instanceof MessageObject)) {
            if (obj == null) {
                return null;
            }
            return obj.getClass().getSimpleName();
        }
        MessageObject messageObject = (MessageObject) obj;
        return "message(dialogId=" + messageObject.getDialogId() + "messageId" + messageObject.getId() + ")";
    }

    private boolean getPeerReferenceReplacement(TLRPC.User user, TLRPC.Chat chat, boolean z, TLRPC.InputFileLocation inputFileLocation, TLRPC.InputFileLocation[] inputFileLocationArr, boolean[] zArr) {
        TLRPC.InputPeer tL_inputPeerChat;
        TLRPC.InputPeer inputPeer;
        if (zArr == null || !zArr[0]) {
            return false;
        }
        TLRPC.TL_inputPeerPhotoFileLocation tL_inputPeerPhotoFileLocation = new TLRPC.TL_inputPeerPhotoFileLocation();
        long j = inputFileLocation.volume_id;
        tL_inputPeerPhotoFileLocation.id = j;
        tL_inputPeerPhotoFileLocation.volume_id = j;
        tL_inputPeerPhotoFileLocation.local_id = inputFileLocation.local_id;
        tL_inputPeerPhotoFileLocation.big = z;
        if (user != null) {
            inputPeer = new TLRPC.TL_inputPeerUser();
            inputPeer.user_id = user.id;
            inputPeer.access_hash = user.access_hash;
            tL_inputPeerPhotoFileLocation.photo_id = user.photo.photo_id;
        } else {
            if (ChatObject.isChannel(chat)) {
                tL_inputPeerChat = new TLRPC.TL_inputPeerChannel();
                tL_inputPeerChat.channel_id = chat.id;
                tL_inputPeerChat.access_hash = chat.access_hash;
            } else {
                tL_inputPeerChat = new TLRPC.TL_inputPeerChat();
                tL_inputPeerChat.chat_id = chat.id;
            }
            tL_inputPeerPhotoFileLocation.photo_id = chat.photo.photo_id;
            inputPeer = tL_inputPeerChat;
        }
        tL_inputPeerPhotoFileLocation.peer = inputPeer;
        inputFileLocationArr[0] = tL_inputPeerPhotoFileLocation;
        return true;
    }

    public static boolean isFileRefError(String str) {
        return "FILEREF_EXPIRED".equals(str) || "FILE_REFERENCE_EXPIRED".equals(str) || "FILE_REFERENCE_EMPTY".equals(str) || (str != null && str.startsWith("FILE_REFERENCE_"));
    }

    public static boolean isFileRefErrorCover(String str) {
        return str != null && isFileRefError(str) && str.endsWith("COVER_EXPIRED");
    }

    private boolean isSameReference(byte[] bArr, byte[] bArr2) {
        return Arrays.equals(bArr, bArr2);
    }

    public void lambda$onRequestComplete$41(TLRPC.User user) {
        getMessagesController().putUser(user, false);
    }

    public void lambda$onRequestComplete$42(TLRPC.Chat chat) {
        getMessagesController().putChat(chat, false);
    }

    public void lambda$onRequestComplete$43(TLRPC.Chat chat) {
        getMessagesController().putChat(chat, false);
    }

    public void lambda$onRequestComplete$44(TLRPC.TL_messages_stickerSet tL_messages_stickerSet) {
        getMediaDataController().replaceStickerSet(tL_messages_stickerSet);
    }

    public void lambda$onUpdateObjectReference$29(TLRPC.TL_messages_sendMultiMedia tL_messages_sendMultiMedia, Object[] objArr) {
        getSendMessagesHelper().performSendMessageRequestMulti(tL_messages_sendMultiMedia, (ArrayList) objArr[1], (ArrayList) objArr[2], null, (SendMessagesHelper.DelayedMessage) objArr[4], ((Boolean) objArr[5]).booleanValue());
    }

    public void lambda$onUpdateObjectReference$30(TLRPC.TL_messages_sendMedia tL_messages_sendMedia, Object[] objArr) {
        getSendMessagesHelper().performSendMessageRequestMulti(tL_messages_sendMedia, (ArrayList) objArr[1], (ArrayList) objArr[2], null, (SendMessagesHelper.DelayedMessage) objArr[4], ((Boolean) objArr[5]).booleanValue());
    }

    public void lambda$onUpdateObjectReference$31(Requester requester) {
        getSendMessagesHelper().performSendMessageRequest((TLObject) requester.args[0], (MessageObject) requester.args[1], (String) requester.args[2], (SendMessagesHelper.DelayedMessage) requester.args[3], ((Boolean) requester.args[4]).booleanValue(), (SendMessagesHelper.DelayedMessage) requester.args[5], null, null, ((Boolean) requester.args[6]).booleanValue());
    }

    public void lambda$onUpdateObjectReference$32(Requester requester) {
        getSendMessagesHelper().performSendMessageRequest((TLObject) requester.args[0], (MessageObject) requester.args[1], (String) requester.args[2], (SendMessagesHelper.DelayedMessage) requester.args[3], ((Boolean) requester.args[4]).booleanValue(), (SendMessagesHelper.DelayedMessage) requester.args[5], null, null, ((Boolean) requester.args[6]).booleanValue());
    }

    public static void lambda$onUpdateObjectReference$33(TLObject tLObject, TLRPC.TL_error tL_error) {
    }

    public static void lambda$onUpdateObjectReference$34(TLObject tLObject, TLRPC.TL_error tL_error) {
    }

    public static void lambda$onUpdateObjectReference$35(TLObject tLObject, TLRPC.TL_error tL_error) {
    }

    public static void lambda$onUpdateObjectReference$36(TLObject tLObject, TLRPC.TL_error tL_error) {
    }

    public void lambda$requestReferenceFromServer$0(String str, String str2, StoriesController.BotPreview botPreview) {
        onRequestComplete(str, str2, botPreview, null, true, false);
    }

    public void lambda$requestReferenceFromServer$1(final String str, final String str2, final StoriesController.BotPreview botPreview) {
        Utilities.stageQueue.postRunnable(new Runnable() {
            @Override
            public final void run() {
                FileRefController.this.lambda$requestReferenceFromServer$0(str, str2, botPreview);
            }
        });
    }

    public void lambda$requestReferenceFromServer$10(String str, String str2, TLObject tLObject, TLRPC.TL_error tL_error) {
        onRequestComplete(str, str2, tLObject, tL_error, true, false);
    }

    public void lambda$requestReferenceFromServer$11(String str, String str2, TLObject tLObject, TLRPC.TL_error tL_error) {
        onRequestComplete(str, str2, tLObject, tL_error, true, false);
    }

    public void lambda$requestReferenceFromServer$12(String str, String str2, TLObject tLObject, TLRPC.TL_error tL_error) {
        onRequestComplete(str, str2, tLObject, tL_error, true, false);
    }

    public void lambda$requestReferenceFromServer$13(String str, String str2, TLObject tLObject, TLRPC.TL_error tL_error) {
        onRequestComplete(str, str2, tLObject, tL_error, true, false);
    }

    public void lambda$requestReferenceFromServer$14(String str, String str2, TLObject tLObject, TLRPC.TL_error tL_error) {
        onRequestComplete(str, str2, tLObject, tL_error, true, false);
    }

    public void lambda$requestReferenceFromServer$15(String str, String str2, TLObject tLObject, TLRPC.TL_error tL_error) {
        onRequestComplete(str, str2, tLObject, tL_error, true, false);
    }

    public void lambda$requestReferenceFromServer$16(String str, String str2, TLObject tLObject, TLRPC.TL_error tL_error) {
        onRequestComplete(str, str2, tLObject, tL_error, true, false);
    }

    public void lambda$requestReferenceFromServer$17(TLObject tLObject, TLRPC.TL_error tL_error) {
        broadcastWaitersData(this.wallpaperWaiters, tLObject, tL_error);
    }

    public void lambda$requestReferenceFromServer$18(TLObject tLObject, TLRPC.TL_error tL_error) {
        broadcastWaitersData(this.savedGifsWaiters, tLObject, tL_error);
    }

    public void lambda$requestReferenceFromServer$19(TLObject tLObject, TLRPC.TL_error tL_error) {
        broadcastWaitersData(this.recentStickersWaiter, tLObject, tL_error);
    }

    public void lambda$requestReferenceFromServer$2(String str, String str2, TLObject tLObject, TLRPC.TL_error tL_error) {
        onRequestComplete(str, str2, tLObject, tL_error, true, false);
    }

    public void lambda$requestReferenceFromServer$20(TLObject tLObject, TLRPC.TL_error tL_error) {
        broadcastWaitersData(this.favStickersWaiter, tLObject, tL_error);
    }

    public void lambda$requestReferenceFromServer$21(String str, String str2, TLObject tLObject, TLRPC.TL_error tL_error) {
        onRequestComplete(str, str2, tLObject, tL_error, true, false);
    }

    public void lambda$requestReferenceFromServer$22(String str, String str2, TLObject tLObject, TLRPC.TL_error tL_error) {
        onRequestComplete(str, str2, tLObject, tL_error, true, false);
    }

    public void lambda$requestReferenceFromServer$23(String str, String str2, TLObject tLObject, TLRPC.TL_error tL_error) {
        onRequestComplete(str, str2, tLObject, tL_error, true, false);
    }

    public void lambda$requestReferenceFromServer$24(String str, String str2, TLObject tLObject, TLRPC.TL_error tL_error) {
        onRequestComplete(str, str2, tLObject, tL_error, false, false);
    }

    public void lambda$requestReferenceFromServer$25(String str, String str2, TLObject tLObject, TLRPC.TL_error tL_error) {
        onRequestComplete(str, str2, tLObject, tL_error, false, false);
    }

    public void lambda$requestReferenceFromServer$26(String str, String str2, TLObject tLObject, TLRPC.TL_error tL_error) {
        onRequestComplete(str, str2, tLObject, tL_error, true, false);
    }

    public void lambda$requestReferenceFromServer$27(String str, String str2, TLObject tLObject, TLRPC.TL_error tL_error) {
        onRequestComplete(str, str2, tLObject, tL_error, true, false);
    }

    public void lambda$requestReferenceFromServer$28(String str, String str2, TLObject tLObject, TLRPC.TL_error tL_error) {
        onRequestComplete(str, str2, tLObject, tL_error, true, false);
    }

    public void lambda$requestReferenceFromServer$3(String str, String str2, TLObject tLObject, TLRPC.TL_error tL_error) {
        int currentTimeMillis = (int) (System.currentTimeMillis() / 1000);
        if (tLObject instanceof TLRPC.TL_help_premiumPromo) {
            getMediaDataController().processLoadedPremiumPromo((TLRPC.TL_help_premiumPromo) tLObject, currentTimeMillis, false);
        }
        onRequestComplete(str, str2, tLObject, tL_error, true, false);
    }

    public void lambda$requestReferenceFromServer$4(String str, String str2, TLObject tLObject, TLRPC.TL_error tL_error) {
        onRequestComplete(str, str2, tLObject, tL_error, true, false);
    }

    public void lambda$requestReferenceFromServer$5(String str, String str2, TLObject tLObject, TLRPC.TL_error tL_error) {
        onRequestComplete(str, str2, tLObject, tL_error, true, false);
    }

    public void lambda$requestReferenceFromServer$6(String str, String str2, TLObject tLObject, TLRPC.TL_error tL_error) {
        onRequestComplete(str, str2, tLObject, tL_error, true, false);
    }

    public void lambda$requestReferenceFromServer$7(String str, String str2, TLObject tLObject, TLRPC.TL_error tL_error) {
        onRequestComplete(str, str2, tLObject, tL_error, true, false);
    }

    public void lambda$requestReferenceFromServer$8(String str, String str2, TLObject tLObject, TLRPC.TL_error tL_error) {
        onRequestComplete(str, str2, tLObject, tL_error, true, false);
    }

    public void lambda$requestReferenceFromServer$9(String str, String str2, TLObject tLObject, TLRPC.TL_error tL_error) {
        onRequestComplete(str, str2, tLObject, tL_error, true, false);
    }

    public void lambda$sendErrorToObject$37(TLRPC.TL_messages_sendMultiMedia tL_messages_sendMultiMedia, Object[] objArr) {
        getSendMessagesHelper().performSendMessageRequestMulti(tL_messages_sendMultiMedia, (ArrayList) objArr[1], (ArrayList) objArr[2], null, (SendMessagesHelper.DelayedMessage) objArr[4], ((Boolean) objArr[5]).booleanValue());
    }

    public void lambda$sendErrorToObject$38(TLRPC.TL_messages_sendMedia tL_messages_sendMedia, Object[] objArr) {
        getSendMessagesHelper().performSendMessageRequestMulti(tL_messages_sendMedia, (ArrayList) objArr[1], (ArrayList) objArr[2], null, (SendMessagesHelper.DelayedMessage) objArr[4], ((Boolean) objArr[5]).booleanValue());
    }

    public void lambda$sendErrorToObject$39(Object[] objArr) {
        getSendMessagesHelper().performSendMessageRequest((TLObject) objArr[0], (MessageObject) objArr[1], (String) objArr[2], (SendMessagesHelper.DelayedMessage) objArr[3], ((Boolean) objArr[4]).booleanValue(), (SendMessagesHelper.DelayedMessage) objArr[5], null, null, ((Boolean) objArr[6]).booleanValue());
    }

    private boolean onRequestComplete(java.lang.String r31, java.lang.String r32, org.telegram.tgnet.TLObject r33, org.telegram.tgnet.TLRPC.TL_error r34, boolean r35, boolean r36) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.FileRefController.onRequestComplete(java.lang.String, java.lang.String, org.telegram.tgnet.TLObject, org.telegram.tgnet.TLRPC$TL_error, boolean, boolean):boolean");
    }

    private boolean onUpdateObjectReference(final Requester requester, byte[] bArr, TLRPC.InputFileLocation inputFileLocation, boolean z) {
        String str;
        RequestDelegate requestDelegate;
        ConnectionsManager connectionsManager;
        TLRPC.TL_messages_faveSticker tL_messages_faveSticker;
        Runnable runnable;
        TLRPC.TL_inputMediaPhoto tL_inputMediaPhoto;
        Runnable runnable2;
        if (BuildVars.DEBUG_VERSION) {
            FileLog.d("fileref updated for " + requester.args[0] + " " + requester.locationKey);
        }
        if (requester.args[0] instanceof TL_stories.TL_storyItem) {
            ((TL_stories.TL_storyItem) requester.args[0]).media.document.file_reference = bArr;
            return true;
        }
        if (requester.args[0] instanceof TLRPC.TL_inputSingleMedia) {
            final TLRPC.TL_messages_sendMultiMedia tL_messages_sendMultiMedia = (TLRPC.TL_messages_sendMultiMedia) requester.args[1];
            final Object[] objArr = this.multiMediaCache.get(tL_messages_sendMultiMedia);
            if (objArr == null) {
                return true;
            }
            TLRPC.TL_inputSingleMedia tL_inputSingleMedia = (TLRPC.TL_inputSingleMedia) requester.args[0];
            TLRPC.InputMedia inputMedia = tL_inputSingleMedia.media;
            if (inputMedia instanceof TLRPC.TL_inputMediaDocument) {
                TLRPC.TL_inputMediaDocument tL_inputMediaDocument = (TLRPC.TL_inputMediaDocument) inputMedia;
                if (z && isSameReference(tL_inputMediaDocument.id.file_reference, bArr)) {
                    return false;
                }
                tL_inputMediaDocument.id.file_reference = bArr;
            } else if (inputMedia instanceof TLRPC.TL_inputMediaPhoto) {
                TLRPC.TL_inputMediaPhoto tL_inputMediaPhoto2 = (TLRPC.TL_inputMediaPhoto) inputMedia;
                if (z && isSameReference(tL_inputMediaPhoto2.id.file_reference, bArr)) {
                    return false;
                }
                tL_inputMediaPhoto2.id.file_reference = bArr;
            }
            int indexOf = tL_messages_sendMultiMedia.multi_media.indexOf(tL_inputSingleMedia);
            if (indexOf < 0) {
                return true;
            }
            ArrayList arrayList = (ArrayList) objArr[3];
            arrayList.set(indexOf, null);
            boolean z2 = true;
            for (int i = 0; i < arrayList.size(); i++) {
                if (arrayList.get(i) != null) {
                    z2 = false;
                }
            }
            if (z2) {
                this.multiMediaCache.remove(tL_messages_sendMultiMedia);
                runnable2 = new Runnable() {
                    @Override
                    public final void run() {
                        FileRefController.this.lambda$onUpdateObjectReference$29(tL_messages_sendMultiMedia, objArr);
                    }
                };
                AndroidUtilities.runOnUIThread(runnable2);
            }
            return true;
        }
        if (requester.args.length >= 2 && (requester.args[1] instanceof TLRPC.TL_messages_sendMedia) && (((TLRPC.TL_messages_sendMedia) requester.args[1]).media instanceof TLRPC.TL_inputMediaPaidMedia) && ((requester.args[0] instanceof TLRPC.TL_inputMediaPhoto) || (requester.args[0] instanceof TLRPC.TL_inputMediaDocument))) {
            final TLRPC.TL_messages_sendMedia tL_messages_sendMedia = (TLRPC.TL_messages_sendMedia) requester.args[1];
            final Object[] objArr2 = this.multiMediaCache.get(tL_messages_sendMedia);
            if (objArr2 == null) {
                return true;
            }
            if (requester.args[0] instanceof TLRPC.TL_inputMediaDocument) {
                TLRPC.TL_inputMediaDocument tL_inputMediaDocument2 = (TLRPC.TL_inputMediaDocument) requester.args[0];
                if (z && isSameReference(tL_inputMediaDocument2.id.file_reference, bArr)) {
                    return false;
                }
                tL_inputMediaDocument2.id.file_reference = bArr;
                tL_inputMediaPhoto = tL_inputMediaDocument2;
            } else if (requester.args[0] instanceof TLRPC.TL_inputMediaPhoto) {
                TLRPC.TL_inputMediaPhoto tL_inputMediaPhoto3 = (TLRPC.TL_inputMediaPhoto) requester.args[0];
                if (z && isSameReference(tL_inputMediaPhoto3.id.file_reference, bArr)) {
                    return false;
                }
                tL_inputMediaPhoto3.id.file_reference = bArr;
                tL_inputMediaPhoto = tL_inputMediaPhoto3;
            } else {
                tL_inputMediaPhoto = null;
            }
            int indexOf2 = ((TLRPC.TL_inputMediaPaidMedia) tL_messages_sendMedia.media).extended_media.indexOf(tL_inputMediaPhoto);
            if (indexOf2 < 0) {
                return true;
            }
            ArrayList arrayList2 = (ArrayList) objArr2[3];
            arrayList2.set(indexOf2, null);
            boolean z3 = true;
            for (int i2 = 0; i2 < arrayList2.size(); i2++) {
                if (arrayList2.get(i2) != null) {
                    z3 = false;
                }
            }
            if (z3) {
                this.multiMediaCache.remove(tL_messages_sendMedia);
                runnable2 = new Runnable() {
                    @Override
                    public final void run() {
                        FileRefController.this.lambda$onUpdateObjectReference$30(tL_messages_sendMedia, objArr2);
                    }
                };
                AndroidUtilities.runOnUIThread(runnable2);
            }
        } else {
            if (requester.args[0] instanceof TLRPC.TL_messages_sendMedia) {
                TLRPC.InputMedia inputMedia2 = ((TLRPC.TL_messages_sendMedia) requester.args[0]).media;
                if (inputMedia2 instanceof TLRPC.TL_inputMediaDocument) {
                    TLRPC.TL_inputMediaDocument tL_inputMediaDocument3 = (TLRPC.TL_inputMediaDocument) inputMedia2;
                    if (z && isSameReference(tL_inputMediaDocument3.id.file_reference, bArr)) {
                        return false;
                    }
                    tL_inputMediaDocument3.id.file_reference = bArr;
                } else if (inputMedia2 instanceof TLRPC.TL_inputMediaPhoto) {
                    TLRPC.TL_inputMediaPhoto tL_inputMediaPhoto4 = (TLRPC.TL_inputMediaPhoto) inputMedia2;
                    if (z && isSameReference(tL_inputMediaPhoto4.id.file_reference, bArr)) {
                        return false;
                    }
                    tL_inputMediaPhoto4.id.file_reference = bArr;
                }
                runnable = new Runnable() {
                    @Override
                    public final void run() {
                        FileRefController.this.lambda$onUpdateObjectReference$31(requester);
                    }
                };
            } else if (requester.args[0] instanceof TLRPC.TL_messages_editMessage) {
                TLRPC.InputMedia inputMedia3 = ((TLRPC.TL_messages_editMessage) requester.args[0]).media;
                if (inputMedia3 instanceof TLRPC.TL_inputMediaDocument) {
                    TLRPC.TL_inputMediaDocument tL_inputMediaDocument4 = (TLRPC.TL_inputMediaDocument) inputMedia3;
                    if (z && isSameReference(tL_inputMediaDocument4.id.file_reference, bArr)) {
                        return false;
                    }
                    tL_inputMediaDocument4.id.file_reference = bArr;
                } else if (inputMedia3 instanceof TLRPC.TL_inputMediaPhoto) {
                    TLRPC.TL_inputMediaPhoto tL_inputMediaPhoto5 = (TLRPC.TL_inputMediaPhoto) inputMedia3;
                    if (z && isSameReference(tL_inputMediaPhoto5.id.file_reference, bArr)) {
                        return false;
                    }
                    tL_inputMediaPhoto5.id.file_reference = bArr;
                }
                runnable = new Runnable() {
                    @Override
                    public final void run() {
                        FileRefController.this.lambda$onUpdateObjectReference$32(requester);
                    }
                };
            } else {
                if (requester.args[0] instanceof TLRPC.TL_messages_saveGif) {
                    TLRPC.TL_messages_saveGif tL_messages_saveGif = (TLRPC.TL_messages_saveGif) requester.args[0];
                    if (z && isSameReference(tL_messages_saveGif.id.file_reference, bArr)) {
                        return false;
                    }
                    tL_messages_saveGif.id.file_reference = bArr;
                    ConnectionsManager connectionsManager2 = getConnectionsManager();
                    requestDelegate = new RequestDelegate() {
                        @Override
                        public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                            FileRefController.lambda$onUpdateObjectReference$33(tLObject, tL_error);
                        }
                    };
                    tL_messages_faveSticker = tL_messages_saveGif;
                    connectionsManager = connectionsManager2;
                } else if (requester.args[0] instanceof TLRPC.TL_messages_saveRecentSticker) {
                    TLRPC.TL_messages_saveRecentSticker tL_messages_saveRecentSticker = (TLRPC.TL_messages_saveRecentSticker) requester.args[0];
                    if (z && isSameReference(tL_messages_saveRecentSticker.id.file_reference, bArr)) {
                        return false;
                    }
                    tL_messages_saveRecentSticker.id.file_reference = bArr;
                    ConnectionsManager connectionsManager3 = getConnectionsManager();
                    requestDelegate = new RequestDelegate() {
                        @Override
                        public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                            FileRefController.lambda$onUpdateObjectReference$34(tLObject, tL_error);
                        }
                    };
                    tL_messages_faveSticker = tL_messages_saveRecentSticker;
                    connectionsManager = connectionsManager3;
                } else if (requester.args[0] instanceof TLRPC.TL_stickers_addStickerToSet) {
                    TLRPC.TL_stickers_addStickerToSet tL_stickers_addStickerToSet = (TLRPC.TL_stickers_addStickerToSet) requester.args[0];
                    if (z && isSameReference(tL_stickers_addStickerToSet.sticker.document.file_reference, bArr)) {
                        return false;
                    }
                    tL_stickers_addStickerToSet.sticker.document.file_reference = bArr;
                    ConnectionsManager connectionsManager4 = getConnectionsManager();
                    requestDelegate = new RequestDelegate() {
                        @Override
                        public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                            FileRefController.lambda$onUpdateObjectReference$35(tLObject, tL_error);
                        }
                    };
                    tL_messages_faveSticker = tL_stickers_addStickerToSet;
                    connectionsManager = connectionsManager4;
                } else if (requester.args[0] instanceof TLRPC.TL_messages_faveSticker) {
                    TLRPC.TL_messages_faveSticker tL_messages_faveSticker2 = (TLRPC.TL_messages_faveSticker) requester.args[0];
                    if (z && isSameReference(tL_messages_faveSticker2.id.file_reference, bArr)) {
                        return false;
                    }
                    tL_messages_faveSticker2.id.file_reference = bArr;
                    ConnectionsManager connectionsManager5 = getConnectionsManager();
                    requestDelegate = new RequestDelegate() {
                        @Override
                        public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                            FileRefController.lambda$onUpdateObjectReference$36(tLObject, tL_error);
                        }
                    };
                    tL_messages_faveSticker = tL_messages_faveSticker2;
                    connectionsManager = connectionsManager5;
                } else if (requester.args[0] instanceof TLRPC.TL_messages_getAttachedStickers) {
                    TLRPC.TL_messages_getAttachedStickers tL_messages_getAttachedStickers = (TLRPC.TL_messages_getAttachedStickers) requester.args[0];
                    TLRPC.InputStickeredMedia inputStickeredMedia = tL_messages_getAttachedStickers.media;
                    if (inputStickeredMedia instanceof TLRPC.TL_inputStickeredMediaDocument) {
                        TLRPC.TL_inputStickeredMediaDocument tL_inputStickeredMediaDocument = (TLRPC.TL_inputStickeredMediaDocument) inputStickeredMedia;
                        if (z && isSameReference(tL_inputStickeredMediaDocument.id.file_reference, bArr)) {
                            return false;
                        }
                        tL_inputStickeredMediaDocument.id.file_reference = bArr;
                    } else if (inputStickeredMedia instanceof TLRPC.TL_inputStickeredMediaPhoto) {
                        TLRPC.TL_inputStickeredMediaPhoto tL_inputStickeredMediaPhoto = (TLRPC.TL_inputStickeredMediaPhoto) inputStickeredMedia;
                        if (z && isSameReference(tL_inputStickeredMediaPhoto.id.file_reference, bArr)) {
                            return false;
                        }
                        tL_inputStickeredMediaPhoto.id.file_reference = bArr;
                    }
                    getConnectionsManager().sendRequest(tL_messages_getAttachedStickers, (RequestDelegate) requester.args[1]);
                } else if (requester.args[1] instanceof FileLoadOperation) {
                    FileLoadOperation fileLoadOperation = (FileLoadOperation) requester.args[1];
                    if (inputFileLocation != null) {
                        if (z && isSameReference(fileLoadOperation.location.file_reference, inputFileLocation.file_reference)) {
                            return false;
                        }
                        str = BuildVars.LOGS_ENABLED ? Utilities.bytesToHex(fileLoadOperation.location.file_reference) : null;
                        fileLoadOperation.location = inputFileLocation;
                        if (BuildVars.LOGS_ENABLED) {
                            r5 = Utilities.bytesToHex(inputFileLocation.file_reference);
                        }
                    } else {
                        if (z && isSameReference(requester.location.file_reference, bArr)) {
                            return false;
                        }
                        String bytesToHex = BuildVars.LOGS_ENABLED ? Utilities.bytesToHex(fileLoadOperation.location.file_reference) : null;
                        TLRPC.InputFileLocation inputFileLocation2 = fileLoadOperation.location;
                        requester.location.file_reference = bArr;
                        inputFileLocation2.file_reference = bArr;
                        r5 = BuildVars.LOGS_ENABLED ? Utilities.bytesToHex(fileLoadOperation.location.file_reference) : null;
                        str = bytesToHex;
                    }
                    fileLoadOperation.requestingReference = false;
                    if (BuildVars.LOGS_ENABLED) {
                        FileLog.d("debug_loading: " + fileLoadOperation.getCacheFileFinal().getName() + " " + str + " " + r5 + " reference updated resume download");
                    }
                    fileLoadOperation.startDownloadRequest(-1);
                }
                connectionsManager.sendRequest(tL_messages_faveSticker, requestDelegate);
            }
            AndroidUtilities.runOnUIThread(runnable);
        }
        return true;
    }

    private void putReponseToCache(String str, TLObject tLObject) {
        if (this.responseCache.get(str) == null) {
            CachedResult cachedResult = new CachedResult();
            cachedResult.response = tLObject;
            cachedResult.firstQueryTime = System.currentTimeMillis();
            this.responseCache.put(str, cachedResult);
        }
    }

    private void requestReferenceFromServer(Object obj, final String str, final String str2, Object[] objArr) {
        RequestDelegate requestDelegate;
        TLObject tLObject;
        ArrayList<Waiter> arrayList;
        Waiter waiter;
        ConnectionsManager connectionsManager;
        if (obj instanceof StoriesController.BotPreview) {
            StoriesController.BotPreview botPreview = (StoriesController.BotPreview) obj;
            StoriesController.BotPreviewsList botPreviewsList = botPreview.list;
            if (botPreviewsList == null) {
                sendErrorToObject(objArr, 0);
                return;
            } else {
                botPreviewsList.requestReference(botPreview, new Utilities.Callback() {
                    @Override
                    public final void run(Object obj2) {
                        FileRefController.this.lambda$requestReferenceFromServer$1(str, str2, (StoriesController.BotPreview) obj2);
                    }
                });
                return;
            }
        }
        if (obj instanceof TL_stories.StoryItem) {
            TL_stories.StoryItem storyItem = (TL_stories.StoryItem) obj;
            TL_stories.TL_stories_getStoriesByID tL_stories_getStoriesByID = new TL_stories.TL_stories_getStoriesByID();
            tL_stories_getStoriesByID.peer = getMessagesController().getInputPeer(storyItem.dialogId);
            tL_stories_getStoriesByID.id.add(Integer.valueOf(storyItem.id));
            ConnectionsManager connectionsManager2 = getConnectionsManager();
            requestDelegate = new RequestDelegate() {
                @Override
                public final void run(TLObject tLObject2, TLRPC.TL_error tL_error) {
                    FileRefController.this.lambda$requestReferenceFromServer$2(str, str2, tLObject2, tL_error);
                }
            };
            connectionsManager = connectionsManager2;
            tLObject = tL_stories_getStoriesByID;
        } else if (obj instanceof TLRPC.TL_help_premiumPromo) {
            TLRPC.TL_help_getPremiumPromo tL_help_getPremiumPromo = new TLRPC.TL_help_getPremiumPromo();
            ConnectionsManager connectionsManager3 = getConnectionsManager();
            requestDelegate = new RequestDelegate() {
                @Override
                public final void run(TLObject tLObject2, TLRPC.TL_error tL_error) {
                    FileRefController.this.lambda$requestReferenceFromServer$3(str, str2, tLObject2, tL_error);
                }
            };
            connectionsManager = connectionsManager3;
            tLObject = tL_help_getPremiumPromo;
        } else if (obj instanceof TLRPC.TL_availableReaction) {
            TLRPC.TL_messages_getAvailableReactions tL_messages_getAvailableReactions = new TLRPC.TL_messages_getAvailableReactions();
            tL_messages_getAvailableReactions.hash = 0;
            ConnectionsManager connectionsManager4 = getConnectionsManager();
            requestDelegate = new RequestDelegate() {
                @Override
                public final void run(TLObject tLObject2, TLRPC.TL_error tL_error) {
                    FileRefController.this.lambda$requestReferenceFromServer$4(str, str2, tLObject2, tL_error);
                }
            };
            connectionsManager = connectionsManager4;
            tLObject = tL_messages_getAvailableReactions;
        } else if (obj instanceof TL_bots.BotInfo) {
            TLRPC.TL_users_getFullUser tL_users_getFullUser = new TLRPC.TL_users_getFullUser();
            tL_users_getFullUser.id = getMessagesController().getInputUser(((TL_bots.BotInfo) obj).user_id);
            ConnectionsManager connectionsManager5 = getConnectionsManager();
            requestDelegate = new RequestDelegate() {
                @Override
                public final void run(TLObject tLObject2, TLRPC.TL_error tL_error) {
                    FileRefController.this.lambda$requestReferenceFromServer$5(str, str2, tLObject2, tL_error);
                }
            };
            connectionsManager = connectionsManager5;
            tLObject = tL_users_getFullUser;
        } else if (obj instanceof TLRPC.TL_attachMenuBot) {
            TLRPC.TL_messages_getAttachMenuBot tL_messages_getAttachMenuBot = new TLRPC.TL_messages_getAttachMenuBot();
            tL_messages_getAttachMenuBot.bot = getMessagesController().getInputUser(((TLRPC.TL_attachMenuBot) obj).bot_id);
            ConnectionsManager connectionsManager6 = getConnectionsManager();
            requestDelegate = new RequestDelegate() {
                @Override
                public final void run(TLObject tLObject2, TLRPC.TL_error tL_error) {
                    FileRefController.this.lambda$requestReferenceFromServer$6(str, str2, tLObject2, tL_error);
                }
            };
            connectionsManager = connectionsManager6;
            tLObject = tL_messages_getAttachMenuBot;
        } else if (obj instanceof MessageObject) {
            MessageObject messageObject = (MessageObject) obj;
            long channelId = messageObject.getChannelId();
            if (messageObject.scheduled) {
                TLRPC.TL_messages_getScheduledMessages tL_messages_getScheduledMessages = new TLRPC.TL_messages_getScheduledMessages();
                tL_messages_getScheduledMessages.peer = getMessagesController().getInputPeer(messageObject.getDialogId());
                tL_messages_getScheduledMessages.id.add(Integer.valueOf(messageObject.getRealId()));
                ConnectionsManager connectionsManager7 = getConnectionsManager();
                requestDelegate = new RequestDelegate() {
                    @Override
                    public final void run(TLObject tLObject2, TLRPC.TL_error tL_error) {
                        FileRefController.this.lambda$requestReferenceFromServer$7(str, str2, tLObject2, tL_error);
                    }
                };
                connectionsManager = connectionsManager7;
                tLObject = tL_messages_getScheduledMessages;
            } else if (messageObject.isQuickReply()) {
                TLRPC.TL_messages_getQuickReplyMessages tL_messages_getQuickReplyMessages = new TLRPC.TL_messages_getQuickReplyMessages();
                tL_messages_getQuickReplyMessages.shortcut_id = messageObject.getQuickReplyId();
                tL_messages_getQuickReplyMessages.flags |= 1;
                tL_messages_getQuickReplyMessages.id.add(Integer.valueOf(messageObject.getRealId()));
                ConnectionsManager connectionsManager8 = getConnectionsManager();
                requestDelegate = new RequestDelegate() {
                    @Override
                    public final void run(TLObject tLObject2, TLRPC.TL_error tL_error) {
                        FileRefController.this.lambda$requestReferenceFromServer$8(str, str2, tLObject2, tL_error);
                    }
                };
                connectionsManager = connectionsManager8;
                tLObject = tL_messages_getQuickReplyMessages;
            } else if (channelId != 0) {
                TLRPC.TL_channels_getMessages tL_channels_getMessages = new TLRPC.TL_channels_getMessages();
                tL_channels_getMessages.channel = getMessagesController().getInputChannel(channelId);
                tL_channels_getMessages.id.add(Integer.valueOf(messageObject.getRealId()));
                ConnectionsManager connectionsManager9 = getConnectionsManager();
                requestDelegate = new RequestDelegate() {
                    @Override
                    public final void run(TLObject tLObject2, TLRPC.TL_error tL_error) {
                        FileRefController.this.lambda$requestReferenceFromServer$9(str, str2, tLObject2, tL_error);
                    }
                };
                connectionsManager = connectionsManager9;
                tLObject = tL_channels_getMessages;
            } else {
                TLRPC.TL_messages_getMessages tL_messages_getMessages = new TLRPC.TL_messages_getMessages();
                tL_messages_getMessages.id.add(Integer.valueOf(messageObject.getRealId()));
                ConnectionsManager connectionsManager10 = getConnectionsManager();
                requestDelegate = new RequestDelegate() {
                    @Override
                    public final void run(TLObject tLObject2, TLRPC.TL_error tL_error) {
                        FileRefController.this.lambda$requestReferenceFromServer$10(str, str2, tLObject2, tL_error);
                    }
                };
                connectionsManager = connectionsManager10;
                tLObject = tL_messages_getMessages;
            }
        } else if (obj instanceof TLRPC.TL_wallPaper) {
            TLRPC.TL_wallPaper tL_wallPaper = (TLRPC.TL_wallPaper) obj;
            TL_account.getWallPaper getwallpaper = new TL_account.getWallPaper();
            TLRPC.TL_inputWallPaper tL_inputWallPaper = new TLRPC.TL_inputWallPaper();
            tL_inputWallPaper.id = tL_wallPaper.id;
            tL_inputWallPaper.access_hash = tL_wallPaper.access_hash;
            getwallpaper.wallpaper = tL_inputWallPaper;
            ConnectionsManager connectionsManager11 = getConnectionsManager();
            requestDelegate = new RequestDelegate() {
                @Override
                public final void run(TLObject tLObject2, TLRPC.TL_error tL_error) {
                    FileRefController.this.lambda$requestReferenceFromServer$11(str, str2, tLObject2, tL_error);
                }
            };
            connectionsManager = connectionsManager11;
            tLObject = getwallpaper;
        } else if (obj instanceof TLRPC.TL_theme) {
            TLRPC.TL_theme tL_theme = (TLRPC.TL_theme) obj;
            TL_account.getTheme gettheme = new TL_account.getTheme();
            TLRPC.TL_inputTheme tL_inputTheme = new TLRPC.TL_inputTheme();
            tL_inputTheme.id = tL_theme.id;
            tL_inputTheme.access_hash = tL_theme.access_hash;
            gettheme.theme = tL_inputTheme;
            gettheme.format = "android";
            ConnectionsManager connectionsManager12 = getConnectionsManager();
            requestDelegate = new RequestDelegate() {
                @Override
                public final void run(TLObject tLObject2, TLRPC.TL_error tL_error) {
                    FileRefController.this.lambda$requestReferenceFromServer$12(str, str2, tLObject2, tL_error);
                }
            };
            connectionsManager = connectionsManager12;
            tLObject = gettheme;
        } else if (obj instanceof TLRPC.WebPage) {
            TLRPC.TL_messages_getWebPage tL_messages_getWebPage = new TLRPC.TL_messages_getWebPage();
            tL_messages_getWebPage.url = ((TLRPC.WebPage) obj).url;
            tL_messages_getWebPage.hash = 0;
            ConnectionsManager connectionsManager13 = getConnectionsManager();
            requestDelegate = new RequestDelegate() {
                @Override
                public final void run(TLObject tLObject2, TLRPC.TL_error tL_error) {
                    FileRefController.this.lambda$requestReferenceFromServer$13(str, str2, tLObject2, tL_error);
                }
            };
            connectionsManager = connectionsManager13;
            tLObject = tL_messages_getWebPage;
        } else if (obj instanceof TLRPC.User) {
            TLRPC.TL_users_getUsers tL_users_getUsers = new TLRPC.TL_users_getUsers();
            tL_users_getUsers.id.add(getMessagesController().getInputUser((TLRPC.User) obj));
            ConnectionsManager connectionsManager14 = getConnectionsManager();
            requestDelegate = new RequestDelegate() {
                @Override
                public final void run(TLObject tLObject2, TLRPC.TL_error tL_error) {
                    FileRefController.this.lambda$requestReferenceFromServer$14(str, str2, tLObject2, tL_error);
                }
            };
            connectionsManager = connectionsManager14;
            tLObject = tL_users_getUsers;
        } else if (obj instanceof TLRPC.Chat) {
            TLRPC.Chat chat = (TLRPC.Chat) obj;
            if (chat instanceof TLRPC.TL_chat) {
                TLRPC.TL_messages_getChats tL_messages_getChats = new TLRPC.TL_messages_getChats();
                tL_messages_getChats.id.add(Long.valueOf(chat.id));
                ConnectionsManager connectionsManager15 = getConnectionsManager();
                requestDelegate = new RequestDelegate() {
                    @Override
                    public final void run(TLObject tLObject2, TLRPC.TL_error tL_error) {
                        FileRefController.this.lambda$requestReferenceFromServer$15(str, str2, tLObject2, tL_error);
                    }
                };
                connectionsManager = connectionsManager15;
                tLObject = tL_messages_getChats;
            } else {
                if (!(chat instanceof TLRPC.TL_channel)) {
                    return;
                }
                TLRPC.TL_channels_getChannels tL_channels_getChannels = new TLRPC.TL_channels_getChannels();
                tL_channels_getChannels.id.add(MessagesController.getInputChannel(chat));
                ConnectionsManager connectionsManager16 = getConnectionsManager();
                requestDelegate = new RequestDelegate() {
                    @Override
                    public final void run(TLObject tLObject2, TLRPC.TL_error tL_error) {
                        FileRefController.this.lambda$requestReferenceFromServer$16(str, str2, tLObject2, tL_error);
                    }
                };
                connectionsManager = connectionsManager16;
                tLObject = tL_channels_getChannels;
            }
        } else {
            if (obj instanceof String) {
                String str3 = (String) obj;
                if ("wallpaper".equals(str3)) {
                    if (this.wallpaperWaiters.isEmpty()) {
                        getConnectionsManager().sendRequest(new TL_account.getWallPapers(), new RequestDelegate() {
                            @Override
                            public final void run(TLObject tLObject2, TLRPC.TL_error tL_error) {
                                FileRefController.this.lambda$requestReferenceFromServer$17(tLObject2, tL_error);
                            }
                        });
                    }
                    arrayList = this.wallpaperWaiters;
                    waiter = new Waiter(str, str2);
                } else if (str3.startsWith("gif")) {
                    if (this.savedGifsWaiters.isEmpty()) {
                        getConnectionsManager().sendRequest(new TLRPC.TL_messages_getSavedGifs(), new RequestDelegate() {
                            @Override
                            public final void run(TLObject tLObject2, TLRPC.TL_error tL_error) {
                                FileRefController.this.lambda$requestReferenceFromServer$18(tLObject2, tL_error);
                            }
                        });
                    }
                    arrayList = this.savedGifsWaiters;
                    waiter = new Waiter(str, str2);
                } else if ("recent".equals(str3)) {
                    if (this.recentStickersWaiter.isEmpty()) {
                        getConnectionsManager().sendRequest(new TLRPC.TL_messages_getRecentStickers(), new RequestDelegate() {
                            @Override
                            public final void run(TLObject tLObject2, TLRPC.TL_error tL_error) {
                                FileRefController.this.lambda$requestReferenceFromServer$19(tLObject2, tL_error);
                            }
                        });
                    }
                    arrayList = this.recentStickersWaiter;
                    waiter = new Waiter(str, str2);
                } else if ("fav".equals(str3)) {
                    if (this.favStickersWaiter.isEmpty()) {
                        getConnectionsManager().sendRequest(new TLRPC.TL_messages_getFavedStickers(), new RequestDelegate() {
                            @Override
                            public final void run(TLObject tLObject2, TLRPC.TL_error tL_error) {
                                FileRefController.this.lambda$requestReferenceFromServer$20(tLObject2, tL_error);
                            }
                        });
                    }
                    arrayList = this.favStickersWaiter;
                    waiter = new Waiter(str, str2);
                } else if ("update".equals(str3)) {
                    TLRPC.TL_help_getAppUpdate tL_help_getAppUpdate = new TLRPC.TL_help_getAppUpdate();
                    try {
                        tL_help_getAppUpdate.source = ApplicationLoader.applicationContext.getPackageManager().getInstallerPackageName(ApplicationLoader.applicationContext.getPackageName());
                    } catch (Exception unused) {
                    }
                    if (tL_help_getAppUpdate.source == null) {
                        tL_help_getAppUpdate.source = "";
                    }
                    ConnectionsManager connectionsManager17 = getConnectionsManager();
                    requestDelegate = new RequestDelegate() {
                        @Override
                        public final void run(TLObject tLObject2, TLRPC.TL_error tL_error) {
                            FileRefController.this.lambda$requestReferenceFromServer$21(str, str2, tLObject2, tL_error);
                        }
                    };
                    connectionsManager = connectionsManager17;
                    tLObject = tL_help_getAppUpdate;
                } else {
                    if (!str3.startsWith("avatar_")) {
                        if (str3.startsWith("sent_")) {
                            String[] split = str3.split("_");
                            if (split.length >= 3) {
                                long longValue = Utilities.parseLong(split[1]).longValue();
                                if (longValue != 0) {
                                    TLRPC.TL_channels_getMessages tL_channels_getMessages2 = new TLRPC.TL_channels_getMessages();
                                    tL_channels_getMessages2.channel = getMessagesController().getInputChannel(longValue);
                                    tL_channels_getMessages2.id.add(Utilities.parseInt((CharSequence) split[2]));
                                    ConnectionsManager connectionsManager18 = getConnectionsManager();
                                    requestDelegate = new RequestDelegate() {
                                        @Override
                                        public final void run(TLObject tLObject2, TLRPC.TL_error tL_error) {
                                            FileRefController.this.lambda$requestReferenceFromServer$24(str, str2, tLObject2, tL_error);
                                        }
                                    };
                                    tLObject = tL_channels_getMessages2;
                                    connectionsManager = connectionsManager18;
                                } else {
                                    TLRPC.TL_messages_getMessages tL_messages_getMessages2 = new TLRPC.TL_messages_getMessages();
                                    tL_messages_getMessages2.id.add(Utilities.parseInt((CharSequence) split[2]));
                                    ConnectionsManager connectionsManager19 = getConnectionsManager();
                                    requestDelegate = new RequestDelegate() {
                                        @Override
                                        public final void run(TLObject tLObject2, TLRPC.TL_error tL_error) {
                                            FileRefController.this.lambda$requestReferenceFromServer$25(str, str2, tLObject2, tL_error);
                                        }
                                    };
                                    tLObject = tL_messages_getMessages2;
                                    connectionsManager = connectionsManager19;
                                }
                            }
                        }
                        sendErrorToObject(objArr, 0);
                        return;
                    }
                    long longValue2 = Utilities.parseLong(str3).longValue();
                    if (longValue2 > 0) {
                        TLRPC.TL_photos_getUserPhotos tL_photos_getUserPhotos = new TLRPC.TL_photos_getUserPhotos();
                        tL_photos_getUserPhotos.limit = 80;
                        tL_photos_getUserPhotos.offset = 0;
                        tL_photos_getUserPhotos.max_id = 0L;
                        tL_photos_getUserPhotos.user_id = getMessagesController().getInputUser(longValue2);
                        ConnectionsManager connectionsManager20 = getConnectionsManager();
                        requestDelegate = new RequestDelegate() {
                            @Override
                            public final void run(TLObject tLObject2, TLRPC.TL_error tL_error) {
                                FileRefController.this.lambda$requestReferenceFromServer$22(str, str2, tLObject2, tL_error);
                            }
                        };
                        connectionsManager = connectionsManager20;
                        tLObject = tL_photos_getUserPhotos;
                    } else {
                        TLRPC.TL_messages_search tL_messages_search = new TLRPC.TL_messages_search();
                        tL_messages_search.filter = new TLRPC.TL_inputMessagesFilterChatPhotos();
                        tL_messages_search.limit = 80;
                        tL_messages_search.offset_id = 0;
                        tL_messages_search.q = "";
                        tL_messages_search.peer = getMessagesController().getInputPeer(longValue2);
                        ConnectionsManager connectionsManager21 = getConnectionsManager();
                        requestDelegate = new RequestDelegate() {
                            @Override
                            public final void run(TLObject tLObject2, TLRPC.TL_error tL_error) {
                                FileRefController.this.lambda$requestReferenceFromServer$23(str, str2, tLObject2, tL_error);
                            }
                        };
                        connectionsManager = connectionsManager21;
                        tLObject = tL_messages_search;
                    }
                }
                arrayList.add(waiter);
                return;
            }
            if (obj instanceof TLRPC.TL_messages_stickerSet) {
                TLRPC.TL_messages_getStickerSet tL_messages_getStickerSet = new TLRPC.TL_messages_getStickerSet();
                TLRPC.TL_inputStickerSetID tL_inputStickerSetID = new TLRPC.TL_inputStickerSetID();
                tL_messages_getStickerSet.stickerset = tL_inputStickerSetID;
                TLRPC.StickerSet stickerSet = ((TLRPC.TL_messages_stickerSet) obj).set;
                tL_inputStickerSetID.id = stickerSet.id;
                tL_inputStickerSetID.access_hash = stickerSet.access_hash;
                ConnectionsManager connectionsManager22 = getConnectionsManager();
                requestDelegate = new RequestDelegate() {
                    @Override
                    public final void run(TLObject tLObject2, TLRPC.TL_error tL_error) {
                        FileRefController.this.lambda$requestReferenceFromServer$26(str, str2, tLObject2, tL_error);
                    }
                };
                connectionsManager = connectionsManager22;
                tLObject = tL_messages_getStickerSet;
            } else {
                if (!(obj instanceof TLRPC.StickerSetCovered)) {
                    if (obj instanceof TLRPC.InputStickerSet) {
                        TLRPC.TL_messages_getStickerSet tL_messages_getStickerSet2 = new TLRPC.TL_messages_getStickerSet();
                        tL_messages_getStickerSet2.stickerset = (TLRPC.InputStickerSet) obj;
                        ConnectionsManager connectionsManager23 = getConnectionsManager();
                        requestDelegate = new RequestDelegate() {
                            @Override
                            public final void run(TLObject tLObject2, TLRPC.TL_error tL_error) {
                                FileRefController.this.lambda$requestReferenceFromServer$28(str, str2, tLObject2, tL_error);
                            }
                        };
                        connectionsManager = connectionsManager23;
                        tLObject = tL_messages_getStickerSet2;
                    }
                    sendErrorToObject(objArr, 0);
                    return;
                }
                TLRPC.TL_messages_getStickerSet tL_messages_getStickerSet3 = new TLRPC.TL_messages_getStickerSet();
                TLRPC.TL_inputStickerSetID tL_inputStickerSetID2 = new TLRPC.TL_inputStickerSetID();
                tL_messages_getStickerSet3.stickerset = tL_inputStickerSetID2;
                TLRPC.StickerSet stickerSet2 = ((TLRPC.StickerSetCovered) obj).set;
                tL_inputStickerSetID2.id = stickerSet2.id;
                tL_inputStickerSetID2.access_hash = stickerSet2.access_hash;
                ConnectionsManager connectionsManager24 = getConnectionsManager();
                requestDelegate = new RequestDelegate() {
                    @Override
                    public final void run(TLObject tLObject2, TLRPC.TL_error tL_error) {
                        FileRefController.this.lambda$requestReferenceFromServer$27(str, str2, tLObject2, tL_error);
                    }
                };
                connectionsManager = connectionsManager24;
                tLObject = tL_messages_getStickerSet3;
            }
        }
        connectionsManager.sendRequest(tLObject, requestDelegate);
    }

    private void sendErrorToObject(final Object[] objArr, int i) {
        Runnable runnable;
        Object obj = objArr[0];
        if (obj instanceof TLRPC.TL_inputSingleMedia) {
            final TLRPC.TL_messages_sendMultiMedia tL_messages_sendMultiMedia = (TLRPC.TL_messages_sendMultiMedia) objArr[1];
            final Object[] objArr2 = this.multiMediaCache.get(tL_messages_sendMultiMedia);
            if (objArr2 != null) {
                this.multiMediaCache.remove(tL_messages_sendMultiMedia);
                AndroidUtilities.runOnUIThread(new Runnable() {
                    @Override
                    public final void run() {
                        FileRefController.this.lambda$sendErrorToObject$37(tL_messages_sendMultiMedia, objArr2);
                    }
                });
                return;
            }
            return;
        }
        if ((obj instanceof TLRPC.TL_inputMediaDocument) || (obj instanceof TLRPC.TL_inputMediaPhoto)) {
            Object obj2 = objArr[1];
            if (obj2 instanceof TLRPC.TL_messages_sendMedia) {
                final TLRPC.TL_messages_sendMedia tL_messages_sendMedia = (TLRPC.TL_messages_sendMedia) obj2;
                final Object[] objArr3 = this.multiMediaCache.get(tL_messages_sendMedia);
                if (objArr3 != null) {
                    this.multiMediaCache.remove(tL_messages_sendMedia);
                    runnable = new Runnable() {
                        @Override
                        public final void run() {
                            FileRefController.this.lambda$sendErrorToObject$38(tL_messages_sendMedia, objArr3);
                        }
                    };
                    AndroidUtilities.runOnUIThread(runnable);
                    return;
                }
                return;
            }
        }
        if (((obj instanceof TLRPC.TL_messages_sendMedia) && !(((TLRPC.TL_messages_sendMedia) obj).media instanceof TLRPC.TL_inputMediaPaidMedia)) || (obj instanceof TLRPC.TL_messages_editMessage)) {
            runnable = new Runnable() {
                @Override
                public final void run() {
                    FileRefController.this.lambda$sendErrorToObject$39(objArr);
                }
            };
            AndroidUtilities.runOnUIThread(runnable);
            return;
        }
        if ((obj instanceof TLRPC.TL_messages_saveGif) || (obj instanceof TLRPC.TL_messages_saveRecentSticker) || (obj instanceof TLRPC.TL_stickers_addStickerToSet) || (obj instanceof TLRPC.TL_messages_faveSticker)) {
            return;
        }
        if (obj instanceof TLRPC.TL_messages_getAttachedStickers) {
            getConnectionsManager().sendRequest((TLRPC.TL_messages_getAttachedStickers) obj, (RequestDelegate) objArr[1]);
            return;
        }
        Object obj3 = objArr[1];
        if (obj3 instanceof FileLoadOperation) {
            FileLoadOperation fileLoadOperation = (FileLoadOperation) obj3;
            fileLoadOperation.requestingReference = false;
            FileLog.e("debug_loading: " + fileLoadOperation.getCacheFileFinal().getName() + " reference can't update: fail operation ");
            fileLoadOperation.onFail(false, 0);
        }
    }

    public void requestReference(java.lang.Object r14, java.lang.Object... r15) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.FileRefController.requestReference(java.lang.Object, java.lang.Object[]):void");
    }
}
