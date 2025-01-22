package org.telegram.messenger;

import android.appwidget.AppWidgetManager;
import android.text.TextUtils;
import android.util.Pair;
import android.util.SparseArray;
import android.util.SparseIntArray;
import androidx.collection.LongSparseArray;
import j$.util.function.Consumer;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;
import org.telegram.SQLite.SQLiteCursor;
import org.telegram.SQLite.SQLiteDatabase;
import org.telegram.SQLite.SQLiteException;
import org.telegram.SQLite.SQLitePreparedStatement;
import org.telegram.messenger.ContactsController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.MessagesStorage;
import org.telegram.messenger.NotificationsController;
import org.telegram.messenger.Timer;
import org.telegram.messenger.TopicsController;
import org.telegram.messenger.Utilities;
import org.telegram.messenger.support.LongSparseIntArray;
import org.telegram.tgnet.NativeByteBuffer;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.tgnet.Vector;
import org.telegram.tgnet.tl.TL_stories;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Adapters.DialogsSearchAdapter;
import org.telegram.ui.Components.Reactions.ReactionsLayoutInBubble;

public class MessagesStorage extends BaseController {
    public static final String[] DATABASE_TABLES;
    public static final int LAST_DB_VERSION = 162;
    private int archiveUnreadCount;
    private int[][] bots;
    private File cacheFile;
    private int[][] channels;
    private int[][] contacts;
    private SQLiteDatabase database;
    private boolean databaseCreated;
    private boolean databaseMigrationInProgress;
    private final ArrayList<MessagesController.DialogFilter> dialogFilters;
    private final SparseArray<MessagesController.DialogFilter> dialogFiltersMap;
    private final LongSparseIntArray dialogIsForum;
    private LongSparseArray dialogsWithMentions;
    private LongSparseArray dialogsWithUnread;
    private int[][] groups;
    private int lastDateValue;
    private int lastPtsValue;
    private int lastQtsValue;
    private int lastSavedDate;
    private int lastSavedPts;
    private int lastSavedQts;
    private int lastSavedSeq;
    private int lastSecretVersion;
    private int lastSeqValue;
    private final AtomicLong lastTaskId;
    private int mainUnreadCount;
    private int[] mentionChannels;
    private int[] mentionGroups;
    private int[][] nonContacts;
    private final CountDownLatch openSync;
    private volatile int pendingArchiveUnreadCount;
    private volatile int pendingMainUnreadCount;
    private int secretG;
    private byte[] secretPBytes;
    private File shmCacheFile;
    public boolean showClearDatabaseAlert;
    private DispatchQueue storageQueue;
    private final SparseArray<ArrayList<Runnable>> tasks;
    boolean tryRecover;
    private final LongSparseArray unknownDialogsIds;
    private File walCacheFile;
    private static volatile MessagesStorage[] Instance = new MessagesStorage[4];
    private static final Object[] lockObjects = new Object[4];

    public interface BooleanCallback {
        void run(boolean z);
    }

    public static class Hole {
        public int end;
        public int start;
        public int type;

        public Hole(int i, int i2) {
            this.start = i;
            this.end = i2;
        }

        public Hole(int i, int i2, int i3) {
            this.type = i;
            this.start = i2;
            this.end = i3;
        }
    }

    public interface IntCallback {
        void run(int i);
    }

    public interface LongCallback {
        void run(long j);
    }

    public static class ReadDialog {
        public int date;
        public int lastMid;
        public int unreadCount;

        private ReadDialog() {
        }
    }

    public class SavedReactionsUpdate {
        TLRPC.TL_messageReactions last;
        TLRPC.TL_messageReactions old;
        long topic_id;

        public SavedReactionsUpdate(long j, TLRPC.Message message, TLRPC.Message message2) {
            this.topic_id = MessageObject.getSavedDialogId(j, message2);
            this.old = message.reactions;
            this.last = message2.reactions;
        }
    }

    public interface StringCallback {
        void run(String str);
    }

    public static class TopicKey {
        public long dialogId;
        public long topicId;

        public static TopicKey of(long j, long j2) {
            TopicKey topicKey = new TopicKey();
            topicKey.dialogId = j;
            topicKey.topicId = j2;
            return topicKey;
        }

        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null || getClass() != obj.getClass()) {
                return false;
            }
            TopicKey topicKey = (TopicKey) obj;
            return this.dialogId == topicKey.dialogId && this.topicId == topicKey.topicId;
        }

        public int hashCode() {
            return Objects.hash(Long.valueOf(this.dialogId), Long.valueOf(this.topicId));
        }

        public String toString() {
            return "TopicKey{dialogId=" + this.dialogId + ", topicId=" + this.topicId + '}';
        }
    }

    static {
        for (int i = 0; i < 4; i++) {
            lockObjects[i] = new Object();
        }
        DATABASE_TABLES = new String[]{"messages_holes", "media_holes_v2", "scheduled_messages_v2", "quick_replies", "messages_v2", "download_queue", "user_contacts_v7", "user_phones_v7", "dialogs", "dialog_filter", "dialog_filter_ep", "dialog_filter_pin_v2", "randoms_v2", "enc_tasks_v4", "messages_seq", "params", "media_v4", "bot_keyboard", "bot_keyboard_topics", "chat_settings_v2", "user_settings", "chat_pinned_v2", "chat_pinned_count", "chat_hints", "botcache", "users_data", "users", "chats", "enc_chats", "channel_users_v2", "channel_admins_v3", "contacts", "dialog_photos", "dialog_settings", "web_recent_v3", "stickers_v2", "stickers_featured", "stickers_dice", "stickersets", "hashtag_recent_v2", "webpage_pending_v2", "sent_files_v2", "search_recent", "media_counts_v2", "keyvalue", "bot_info_v2", "pending_tasks", "requested_holes", "sharing_locations", "shortcut_widget", "emoji_keywords_v2", "emoji_keywords_info_v2", "wallpapers2", "unread_push_messages", "polls_v2", "reactions", "reaction_mentions", "downloading_documents", "animated_emoji", "attach_menu_bots", "premium_promo", "emoji_statuses", "messages_holes_topics", "messages_topics", "saved_dialogs", "media_topics", "media_holes_topics", "topics", "media_counts_topics", "reaction_mentions_topics", "emoji_groups"};
    }

    public MessagesStorage(int i) {
        super(i);
        this.lastTaskId = new AtomicLong(System.currentTimeMillis());
        this.tasks = new SparseArray<>();
        this.lastDateValue = 0;
        this.lastPtsValue = 0;
        this.lastQtsValue = 0;
        this.lastSeqValue = 0;
        this.lastSecretVersion = 0;
        this.secretPBytes = null;
        this.secretG = 0;
        this.lastSavedSeq = 0;
        this.lastSavedPts = 0;
        this.lastSavedDate = 0;
        this.lastSavedQts = 0;
        this.dialogFilters = new ArrayList<>();
        this.dialogFiltersMap = new SparseArray<>();
        this.unknownDialogsIds = new LongSparseArray();
        this.openSync = new CountDownLatch(1);
        this.dialogIsForum = new LongSparseIntArray();
        this.contacts = new int[][]{new int[2], new int[2]};
        this.nonContacts = new int[][]{new int[2], new int[2]};
        this.bots = new int[][]{new int[2], new int[2]};
        this.channels = new int[][]{new int[2], new int[2]};
        this.groups = new int[][]{new int[2], new int[2]};
        this.mentionChannels = new int[2];
        this.mentionGroups = new int[2];
        this.dialogsWithMentions = new LongSparseArray();
        this.dialogsWithUnread = new LongSparseArray();
        DispatchQueue dispatchQueue = new DispatchQueue("storageQueue_" + i);
        this.storageQueue = dispatchQueue;
        dispatchQueue.setPriority(8);
        this.storageQueue.postRunnable(new Runnable() {
            @Override
            public final void run() {
                MessagesStorage.this.lambda$new$0();
            }
        });
    }

    private boolean addFilesToDelete(TLRPC.Message message, ArrayList<File> arrayList, ArrayList<Pair<Long, Integer>> arrayList2, ArrayList<String> arrayList3, boolean z) {
        long j;
        int i;
        int i2 = 0;
        if (message == null) {
            return false;
        }
        TLRPC.Document document = MessageObject.getDocument(message);
        TLRPC.Photo photo = MessageObject.getPhoto(message);
        if (!MessageObject.isVoiceMessage(message)) {
            if (MessageObject.isStickerMessage(message) || MessageObject.isAnimatedStickerMessage(message)) {
                if (document == null) {
                    return false;
                }
                j = document.id;
            } else if (MessageObject.isVideoMessage(message) || MessageObject.isRoundVideoMessage(message) || MessageObject.isGifMessage(message)) {
                if (document == null) {
                    return false;
                }
                j = document.id;
                i = 4;
            } else if (document != null) {
                if (getMediaDataController().ringtoneDataStore.contains(document.id)) {
                    return false;
                }
                j = document.id;
                i = 8;
            } else if (photo == null || FileLoader.getClosestPhotoSizeWithSize(photo.sizes, AndroidUtilities.getPhotoSize()) == null) {
                j = 0;
                i = 0;
            } else {
                j = photo.id;
            }
            i = 1;
        } else {
            if (document == null || getMediaDataController().ringtoneDataStore.contains(document.id)) {
                return false;
            }
            j = document.id;
            i = 2;
        }
        if (j != 0) {
            arrayList2.add(new Pair<>(Long.valueOf(j), Integer.valueOf(i)));
        }
        if (photo != null) {
            int size = photo.sizes.size();
            while (i2 < size) {
                TLRPC.PhotoSize photoSize = photo.sizes.get(i2);
                String attachFileName = FileLoader.getAttachFileName(photoSize);
                if (!TextUtils.isEmpty(attachFileName)) {
                    arrayList3.add(attachFileName);
                }
                File pathToAttach = getFileLoader().getPathToAttach(photoSize, z);
                if (pathToAttach.toString().length() > 0) {
                    arrayList.add(pathToAttach);
                }
                i2++;
            }
            return true;
        }
        if (document == null) {
            return false;
        }
        String attachFileName2 = FileLoader.getAttachFileName(document);
        if (!TextUtils.isEmpty(attachFileName2)) {
            arrayList3.add(attachFileName2);
        }
        File pathToAttach2 = getFileLoader().getPathToAttach(document, z);
        if (pathToAttach2.toString().length() > 0) {
            arrayList.add(pathToAttach2);
        }
        int size2 = document.thumbs.size();
        while (i2 < size2) {
            File pathToAttach3 = getFileLoader().getPathToAttach(document.thumbs.get(i2));
            if (pathToAttach3.toString().length() > 0) {
                arrayList.add(pathToAttach3);
            }
            i2++;
        }
        return true;
    }

    public static void addLoadPeerInfo(TLRPC.Peer peer, ArrayList<Long> arrayList, ArrayList<Long> arrayList2) {
        long j;
        if (peer instanceof TLRPC.TL_peerUser) {
            if (arrayList.contains(Long.valueOf(peer.user_id))) {
                return;
            }
            arrayList.add(Long.valueOf(peer.user_id));
            return;
        }
        if (peer instanceof TLRPC.TL_peerChannel) {
            if (arrayList2.contains(Long.valueOf(peer.channel_id))) {
                return;
            } else {
                j = peer.channel_id;
            }
        } else if (!(peer instanceof TLRPC.TL_peerChat) || arrayList2.contains(Long.valueOf(peer.chat_id))) {
            return;
        } else {
            j = peer.chat_id;
        }
        arrayList2.add(Long.valueOf(j));
    }

    public static void addReplyMessages(TLRPC.Message message, LongSparseArray longSparseArray, LongSparseArray longSparseArray2) {
        int i = message.reply_to.reply_to_msg_id;
        long replyToDialogId = (message.flags & 1073741824) != 0 ? message.quick_reply_shortcut_id : MessageObject.getReplyToDialogId(message);
        SparseArray sparseArray = (SparseArray) longSparseArray.get(replyToDialogId);
        ArrayList arrayList = (ArrayList) longSparseArray2.get(replyToDialogId);
        if (sparseArray == null) {
            sparseArray = new SparseArray();
            longSparseArray.put(replyToDialogId, sparseArray);
        }
        if (arrayList == null) {
            arrayList = new ArrayList();
            longSparseArray2.put(replyToDialogId, arrayList);
        }
        ArrayList arrayList2 = (ArrayList) sparseArray.get(message.reply_to.reply_to_msg_id);
        if (arrayList2 == null) {
            arrayList2 = new ArrayList();
            sparseArray.put(message.reply_to.reply_to_msg_id, arrayList2);
            if (!arrayList.contains(Integer.valueOf(message.reply_to.reply_to_msg_id))) {
                arrayList.add(Integer.valueOf(message.reply_to.reply_to_msg_id));
            }
        }
        arrayList2.add(message);
    }

    public static void addUsersAndChatsFromMessage(TLRPC.Message message, ArrayList<Long> arrayList, ArrayList<Long> arrayList2, ArrayList<Long> arrayList3) {
        TLRPC.Peer peer;
        String str;
        TLRPC.MessageFwdHeader messageFwdHeader;
        TLRPC.Peer peer2;
        TLRPC.Peer peer3;
        TLRPC.WebPage webPage;
        TLRPC.Peer peer4;
        TL_stories.StoryFwdHeader storyFwdHeader;
        TL_stories.StoryItem storyItem;
        TLRPC.Peer peer5;
        long j;
        long fromChatId = MessageObject.getFromChatId(message);
        if (DialogObject.isUserDialog(fromChatId)) {
            if (!arrayList.contains(Long.valueOf(fromChatId))) {
                arrayList.add(Long.valueOf(fromChatId));
            }
        } else if (DialogObject.isChatDialog(fromChatId)) {
            long j2 = -fromChatId;
            if (!arrayList2.contains(Long.valueOf(j2))) {
                arrayList2.add(Long.valueOf(j2));
            }
        }
        long j3 = message.via_bot_id;
        if (j3 != 0 && !arrayList.contains(Long.valueOf(j3))) {
            arrayList.add(Long.valueOf(message.via_bot_id));
        }
        TLRPC.MessageAction messageAction = message.action;
        if (messageAction != null) {
            long j4 = messageAction.user_id;
            if (j4 != 0 && !arrayList.contains(Long.valueOf(j4))) {
                arrayList.add(Long.valueOf(message.action.user_id));
            }
            long j5 = message.action.channel_id;
            if (j5 != 0 && !arrayList2.contains(Long.valueOf(j5))) {
                arrayList2.add(Long.valueOf(message.action.channel_id));
            }
            long j6 = message.action.chat_id;
            if (j6 != 0 && !arrayList2.contains(Long.valueOf(j6))) {
                arrayList2.add(Long.valueOf(message.action.chat_id));
            }
            TLRPC.MessageAction messageAction2 = message.action;
            if (messageAction2 instanceof TLRPC.TL_messageActionGiftCode) {
                addLoadPeerInfo(((TLRPC.TL_messageActionGiftCode) messageAction2).boost_peer, arrayList, arrayList2);
            }
            TLRPC.MessageAction messageAction3 = message.action;
            if (messageAction3 instanceof TLRPC.TL_messageActionGeoProximityReached) {
                TLRPC.TL_messageActionGeoProximityReached tL_messageActionGeoProximityReached = (TLRPC.TL_messageActionGeoProximityReached) messageAction3;
                addLoadPeerInfo(tL_messageActionGeoProximityReached.from_id, arrayList, arrayList2);
                addLoadPeerInfo(tL_messageActionGeoProximityReached.to_id, arrayList, arrayList2);
            }
            if (!message.action.users.isEmpty()) {
                for (int i = 0; i < message.action.users.size(); i++) {
                    Long l = message.action.users.get(i);
                    if (!arrayList.contains(l)) {
                        arrayList.add(l);
                    }
                }
            }
        }
        if (!message.entities.isEmpty()) {
            for (int i2 = 0; i2 < message.entities.size(); i2++) {
                TLRPC.MessageEntity messageEntity = message.entities.get(i2);
                if (messageEntity instanceof TLRPC.TL_messageEntityMentionName) {
                    j = ((TLRPC.TL_messageEntityMentionName) messageEntity).user_id;
                } else if (messageEntity instanceof TLRPC.TL_inputMessageEntityMentionName) {
                    j = ((TLRPC.TL_inputMessageEntityMentionName) messageEntity).user_id.user_id;
                } else {
                    if (arrayList3 != null && (messageEntity instanceof TLRPC.TL_messageEntityCustomEmoji)) {
                        arrayList3.add(Long.valueOf(((TLRPC.TL_messageEntityCustomEmoji) messageEntity).document_id));
                    }
                }
                arrayList.add(Long.valueOf(j));
            }
        }
        TLRPC.MessageMedia messageMedia = message.media;
        if (messageMedia != null) {
            long j7 = messageMedia.user_id;
            if (j7 != 0 && !arrayList.contains(Long.valueOf(j7))) {
                arrayList.add(Long.valueOf(message.media.user_id));
            }
            TLRPC.MessageMedia messageMedia2 = message.media;
            if (messageMedia2 instanceof TLRPC.TL_messageMediaGiveaway) {
                Iterator<Long> it = ((TLRPC.TL_messageMediaGiveaway) messageMedia2).channels.iterator();
                while (it.hasNext()) {
                    Long next = it.next();
                    if (!arrayList2.contains(next)) {
                        arrayList2.add(next);
                    }
                }
            }
            TLRPC.MessageMedia messageMedia3 = message.media;
            if (messageMedia3 instanceof TLRPC.TL_messageMediaGiveawayResults) {
                Iterator<Long> it2 = ((TLRPC.TL_messageMediaGiveawayResults) messageMedia3).winners.iterator();
                while (it2.hasNext()) {
                    Long next2 = it2.next();
                    if (!arrayList.contains(next2)) {
                        arrayList.add(next2);
                    }
                }
            }
            TLRPC.MessageMedia messageMedia4 = message.media;
            if (messageMedia4 instanceof TLRPC.TL_messageMediaPoll) {
                TLRPC.TL_messageMediaPoll tL_messageMediaPoll = (TLRPC.TL_messageMediaPoll) messageMedia4;
                if (!tL_messageMediaPoll.results.recent_voters.isEmpty()) {
                    for (int i3 = 0; i3 < tL_messageMediaPoll.results.recent_voters.size(); i3++) {
                        addLoadPeerInfo(tL_messageMediaPoll.results.recent_voters.get(i3), arrayList, arrayList2);
                    }
                }
            }
            TLRPC.MessageMedia messageMedia5 = message.media;
            if ((messageMedia5 instanceof TLRPC.TL_messageMediaStory) && (storyItem = messageMedia5.storyItem) != null) {
                TL_stories.StoryFwdHeader storyFwdHeader2 = storyItem.fwd_from;
                if (storyFwdHeader2 != null) {
                    addLoadPeerInfo(storyFwdHeader2.from, arrayList, arrayList2);
                }
                TL_stories.StoryItem storyItem2 = message.media.storyItem;
                if (storyItem2 != null && storyItem2.media_areas != null) {
                    for (int i4 = 0; i4 < message.media.storyItem.media_areas.size(); i4++) {
                        if (message.media.storyItem.media_areas.get(i4) instanceof TL_stories.TL_mediaAreaChannelPost) {
                            long j8 = ((TL_stories.TL_mediaAreaChannelPost) message.media.storyItem.media_areas.get(i4)).channel_id;
                            if (!arrayList2.contains(Long.valueOf(j8))) {
                                arrayList2.add(Long.valueOf(j8));
                            }
                        }
                    }
                }
                TL_stories.StoryItem storyItem3 = message.media.storyItem;
                if (storyItem3 != null && (peer5 = storyItem3.from_id) != null) {
                    addLoadPeerInfo(peer5, arrayList, arrayList2);
                }
            }
            TLRPC.MessageMedia messageMedia6 = message.media;
            if ((messageMedia6 instanceof TLRPC.TL_messageMediaWebPage) && (webPage = messageMedia6.webpage) != null && webPage.attributes != null) {
                for (int i5 = 0; i5 < message.media.webpage.attributes.size(); i5++) {
                    if (message.media.webpage.attributes.get(i5) instanceof TLRPC.TL_webPageAttributeStory) {
                        TLRPC.TL_webPageAttributeStory tL_webPageAttributeStory = (TLRPC.TL_webPageAttributeStory) message.media.webpage.attributes.get(i5);
                        TL_stories.StoryItem storyItem4 = tL_webPageAttributeStory.storyItem;
                        if (storyItem4 != null && (storyFwdHeader = storyItem4.fwd_from) != null) {
                            addLoadPeerInfo(storyFwdHeader.from, arrayList, arrayList2);
                        }
                        TL_stories.StoryItem storyItem5 = tL_webPageAttributeStory.storyItem;
                        if (storyItem5 != null && storyItem5.media_areas != null) {
                            for (int i6 = 0; i6 < tL_webPageAttributeStory.storyItem.media_areas.size(); i6++) {
                                if (tL_webPageAttributeStory.storyItem.media_areas.get(i6) instanceof TL_stories.TL_mediaAreaChannelPost) {
                                    long j9 = ((TL_stories.TL_mediaAreaChannelPost) tL_webPageAttributeStory.storyItem.media_areas.get(i6)).channel_id;
                                    if (!arrayList2.contains(Long.valueOf(j9))) {
                                        arrayList2.add(Long.valueOf(j9));
                                    }
                                }
                            }
                        }
                        TL_stories.StoryItem storyItem6 = tL_webPageAttributeStory.storyItem;
                        if (storyItem6 != null && (peer4 = storyItem6.from_id) != null) {
                            addLoadPeerInfo(peer4, arrayList, arrayList2);
                        }
                    }
                }
            }
            TLRPC.Peer peer6 = message.media.peer;
            if (peer6 != null) {
                addLoadPeerInfo(peer6, arrayList, arrayList2);
            }
        }
        TLRPC.MessageReplies messageReplies = message.replies;
        if (messageReplies != null) {
            int size = messageReplies.recent_repliers.size();
            for (int i7 = 0; i7 < size; i7++) {
                addLoadPeerInfo(message.replies.recent_repliers.get(i7), arrayList, arrayList2);
            }
        }
        TLRPC.MessageReplyHeader messageReplyHeader = message.reply_to;
        if (messageReplyHeader != null && (peer3 = messageReplyHeader.reply_to_peer_id) != null) {
            addLoadPeerInfo(peer3, arrayList, arrayList2);
        }
        TLRPC.MessageFwdHeader messageFwdHeader2 = message.fwd_from;
        if (messageFwdHeader2 != null) {
            addLoadPeerInfo(messageFwdHeader2.from_id, arrayList, arrayList2);
            addLoadPeerInfo(message.fwd_from.saved_from_peer, arrayList, arrayList2);
        }
        TLRPC.MessageReplyHeader messageReplyHeader2 = message.reply_to;
        if (messageReplyHeader2 != null && (messageFwdHeader = messageReplyHeader2.reply_from) != null && (peer2 = messageFwdHeader.from_id) != null) {
            addLoadPeerInfo(peer2, arrayList, arrayList2);
        }
        HashMap<String, String> hashMap = message.params;
        if (hashMap != null && (str = hashMap.get("fwd_peer")) != null) {
            long longValue = Utilities.parseLong(str).longValue();
            if (longValue < 0) {
                long j10 = -longValue;
                if (!arrayList2.contains(Long.valueOf(j10))) {
                    arrayList2.add(Long.valueOf(j10));
                }
            }
        }
        TLRPC.TL_messageReactions tL_messageReactions = message.reactions;
        if (tL_messageReactions == null || tL_messageReactions.top_reactors == null) {
            return;
        }
        for (int i8 = 0; i8 < message.reactions.top_reactors.size(); i8++) {
            TLRPC.MessageReactor messageReactor = message.reactions.top_reactors.get(i8);
            if (messageReactor != null && (peer = messageReactor.peer_id) != null) {
                addLoadPeerInfo(peer, arrayList, arrayList2);
            }
        }
    }

    private void bindMessageTags(SQLitePreparedStatement sQLitePreparedStatement, TLRPC.Message message) {
        ArrayList<TLRPC.ReactionCount> arrayList;
        long clientUserId = getUserConfig().getClientUserId();
        TLRPC.TL_messageReactions tL_messageReactions = message.reactions;
        if (tL_messageReactions == null || !tL_messageReactions.reactions_as_tags || (arrayList = tL_messageReactions.results) == null || arrayList.isEmpty()) {
            return;
        }
        LocaleController localeController = LocaleController.getInstance();
        String str = message.message;
        if (str == null) {
            str = "";
        }
        String translitString = localeController.getTranslitString(str);
        Iterator<TLRPC.ReactionCount> it = message.reactions.results.iterator();
        while (it.hasNext()) {
            TLRPC.ReactionCount next = it.next();
            TLRPC.Reaction reaction = next.reaction;
            if ((reaction instanceof TLRPC.TL_reactionEmoji) || (reaction instanceof TLRPC.TL_reactionCustomEmoji)) {
                sQLitePreparedStatement.requery();
                sQLitePreparedStatement.bindLong(1, message.id);
                sQLitePreparedStatement.bindLong(2, MessageObject.getSavedDialogId(clientUserId, message));
                TLRPC.Reaction reaction2 = next.reaction;
                sQLitePreparedStatement.bindLong(3, reaction2 instanceof TLRPC.TL_reactionEmoji ? ((TLRPC.TL_reactionEmoji) reaction2).emoticon.hashCode() : reaction2 instanceof TLRPC.TL_reactionCustomEmoji ? ((TLRPC.TL_reactionCustomEmoji) reaction2).document_id : 0L);
                sQLitePreparedStatement.bindString(4, translitString == null ? "" : translitString);
                sQLitePreparedStatement.step();
            }
        }
    }

    private void broadcastQuickRepliesMessagesChange(Long l, long j) {
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                MessagesStorage.this.lambda$broadcastQuickRepliesMessagesChange$204();
            }
        });
    }

    private void broadcastScheduledMessagesChange(final Long l) {
        SQLiteCursor queryFinalized;
        SQLiteCursor sQLiteCursor = null;
        try {
            try {
                queryFinalized = this.database.queryFinalized(String.format(Locale.US, "SELECT COUNT(mid) FROM scheduled_messages_v2 WHERE uid = %d", l), new Object[0]);
            } catch (Throwable th) {
                th = th;
            }
        } catch (Exception e) {
            e = e;
        }
        try {
            final int intValue = queryFinalized.next() ? queryFinalized.intValue(0) : 0;
            queryFinalized.dispose();
            AndroidUtilities.runOnUIThread(new Runnable() {
                @Override
                public final void run() {
                    MessagesStorage.this.lambda$broadcastScheduledMessagesChange$203(l, intValue);
                }
            });
        } catch (Exception e2) {
            e = e2;
            sQLiteCursor = queryFinalized;
            checkSQLException(e);
            if (sQLiteCursor != null) {
                sQLiteCursor.dispose();
            }
        } catch (Throwable th2) {
            th = th2;
            sQLiteCursor = queryFinalized;
            if (sQLiteCursor != null) {
                sQLiteCursor.dispose();
            }
            throw th;
        }
    }

    private void calcUnreadCounters(boolean r31) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MessagesStorage.calcUnreadCounters(boolean):void");
    }

    public void lambda$checkIfFolderEmpty$227(final int i) {
        boolean z = false;
        SQLiteCursor sQLiteCursor = null;
        try {
            try {
                sQLiteCursor = this.database.queryFinalized("SELECT did FROM dialogs WHERE folder_id = ?", Integer.valueOf(i));
                while (true) {
                    if (!sQLiteCursor.next()) {
                        z = true;
                        break;
                    }
                    long longValue = sQLiteCursor.longValue(0);
                    if (!DialogObject.isUserDialog(longValue) && !DialogObject.isEncryptedDialog(longValue)) {
                        TLRPC.Chat chat = getChat(-longValue);
                        if (!ChatObject.isNotInChat(chat) && chat.migrated_to == null) {
                            break;
                        }
                    } else {
                        break;
                    }
                }
                sQLiteCursor.dispose();
                if (z) {
                    AndroidUtilities.runOnUIThread(new Runnable() {
                        @Override
                        public final void run() {
                            MessagesStorage.this.lambda$checkIfFolderEmptyInternal$226(i);
                        }
                    });
                    this.database.executeFast("DELETE FROM dialogs WHERE did = " + DialogObject.makeFolderDialogId(i)).stepThis().dispose();
                }
            } catch (Exception e) {
                checkSQLException(e);
                if (sQLiteCursor == null) {
                    return;
                }
            }
            sQLiteCursor.dispose();
        } catch (Throwable th) {
            if (sQLiteCursor != null) {
                sQLiteCursor.dispose();
            }
            throw th;
        }
    }

    private void checkSQLException(Throwable th, boolean z) {
        if (!(th instanceof SQLiteException) || th.getMessage() == null || !th.getMessage().contains("is malformed") || this.tryRecover) {
            FileLog.e(th, z);
            return;
        }
        this.tryRecover = true;
        FileLog.e("disk image malformed detected, try recover");
        if (!recoverDatabase()) {
            FileLog.e(new Exception(th), z);
            return;
        }
        this.tryRecover = false;
        clearLoadingDialogsOffsets();
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                MessagesStorage.this.lambda$checkSQLException$8();
            }
        });
        FileLog.e(new Exception("database restored!!"));
    }

    private void cleanupInternal(boolean z) {
        if (z) {
            reset();
        } else {
            clearDatabaseValues();
        }
        SQLiteDatabase sQLiteDatabase = this.database;
        if (sQLiteDatabase != null) {
            sQLiteDatabase.close();
            this.database = null;
        }
        if (z) {
            File file = this.cacheFile;
            if (file != null) {
                file.delete();
                this.cacheFile = null;
            }
            File file2 = this.walCacheFile;
            if (file2 != null) {
                file2.delete();
                this.walCacheFile = null;
            }
            File file3 = this.shmCacheFile;
            if (file3 != null) {
                file3.delete();
                this.shmCacheFile = null;
            }
        }
    }

    private void clearLoadingDialogsOffsets() {
        for (int i = 0; i < 2; i++) {
            getUserConfig().setDialogsLoadOffset(i, 0, 0, 0L, 0L, 0L, 0L);
            getUserConfig().setTotalDialogsCount(i, 0);
        }
        getUserConfig().saveConfig(false);
    }

    private void closeHolesInTable(java.lang.String r36, long r37, int r39, int r40, long r41) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MessagesStorage.closeHolesInTable(java.lang.String, long, int, int, long):void");
    }

    public static void createFirstHoles(long j, SQLitePreparedStatement sQLitePreparedStatement, SQLitePreparedStatement sQLitePreparedStatement2, int i, long j2) {
        int i2;
        int i3;
        sQLitePreparedStatement.requery();
        sQLitePreparedStatement.bindLong(1, j);
        if (j2 != 0) {
            sQLitePreparedStatement.bindLong(2, j2);
            i2 = 3;
        } else {
            i2 = 2;
        }
        int i4 = i2 + 1;
        sQLitePreparedStatement.bindInteger(i2, i == 1 ? 1 : 0);
        sQLitePreparedStatement.bindInteger(i4, i);
        sQLitePreparedStatement.step();
        for (int i5 = 0; i5 < 8; i5++) {
            sQLitePreparedStatement2.requery();
            sQLitePreparedStatement2.bindLong(1, j);
            if (j2 != 0) {
                sQLitePreparedStatement2.bindLong(2, j2);
                i3 = 3;
            } else {
                i3 = 2;
            }
            int i6 = i3 + 1;
            sQLitePreparedStatement2.bindInteger(i3, i5);
            int i7 = i3 + 2;
            sQLitePreparedStatement2.bindInteger(i6, i == 1 ? 1 : 0);
            sQLitePreparedStatement2.bindInteger(i7, i);
            sQLitePreparedStatement2.step();
        }
    }

    private void createOrEditTopic(final long j, TLRPC.Message message) {
        final TLRPC.TL_forumTopic tL_forumTopic = new TLRPC.TL_forumTopic();
        tL_forumTopic.topicStartMessage = message;
        tL_forumTopic.top_message = message.id;
        tL_forumTopic.topMessage = message;
        tL_forumTopic.from_id = message.from_id;
        tL_forumTopic.notify_settings = new TLRPC.TL_peerNotifySettings();
        tL_forumTopic.unread_count = 0;
        TLRPC.MessageAction messageAction = message.action;
        if (messageAction instanceof TLRPC.TL_messageActionTopicCreate) {
            TLRPC.TL_messageActionTopicCreate tL_messageActionTopicCreate = (TLRPC.TL_messageActionTopicCreate) messageAction;
            tL_forumTopic.id = message.id;
            long j2 = tL_messageActionTopicCreate.icon_emoji_id;
            tL_forumTopic.icon_emoji_id = j2;
            tL_forumTopic.title = tL_messageActionTopicCreate.title;
            tL_forumTopic.icon_color = tL_messageActionTopicCreate.icon_color;
            if (j2 != 0) {
                tL_forumTopic.flags |= 1;
            }
            ArrayList arrayList = new ArrayList();
            arrayList.add(tL_forumTopic);
            saveTopics(j, arrayList, false, false, message.date);
            AndroidUtilities.runOnUIThread(new Runnable() {
                @Override
                public final void run() {
                    MessagesStorage.this.lambda$createOrEditTopic$192(j, tL_forumTopic);
                }
            });
            return;
        }
        if (messageAction instanceof TLRPC.TL_messageActionTopicEdit) {
            TLRPC.TL_messageActionTopicEdit tL_messageActionTopicEdit = (TLRPC.TL_messageActionTopicEdit) messageAction;
            tL_forumTopic.id = (int) MessageObject.getTopicId(this.currentAccount, message, true);
            tL_forumTopic.icon_emoji_id = tL_messageActionTopicEdit.icon_emoji_id;
            tL_forumTopic.title = tL_messageActionTopicEdit.title;
            tL_forumTopic.closed = tL_messageActionTopicEdit.closed;
            tL_forumTopic.hidden = tL_messageActionTopicEdit.hidden;
            int i = tL_messageActionTopicEdit.flags;
            int i2 = (i & 1) != 0 ? 1 : 0;
            if ((i & 2) != 0) {
                i2 += 2;
            }
            if ((i & 4) != 0) {
                i2 += 8;
            }
            if ((i & 8) != 0) {
                i2 += 32;
            }
            updateTopicData(j, tL_forumTopic, i2, message.date);
        }
    }

    public static void createTables(SQLiteDatabase sQLiteDatabase) {
        sQLiteDatabase.executeFast("CREATE TABLE messages_holes(uid INTEGER, start INTEGER, end INTEGER, PRIMARY KEY(uid, start));").stepThis().dispose();
        sQLiteDatabase.executeFast("CREATE INDEX IF NOT EXISTS uid_end_messages_holes ON messages_holes(uid, end);").stepThis().dispose();
        sQLiteDatabase.executeFast("CREATE TABLE media_holes_v2(uid INTEGER, type INTEGER, start INTEGER, end INTEGER, PRIMARY KEY(uid, type, start));").stepThis().dispose();
        sQLiteDatabase.executeFast("CREATE INDEX IF NOT EXISTS uid_end_media_holes_v2 ON media_holes_v2(uid, type, end);").stepThis().dispose();
        sQLiteDatabase.executeFast("CREATE TABLE scheduled_messages_v2(mid INTEGER, uid INTEGER, send_state INTEGER, date INTEGER, data BLOB, ttl INTEGER, replydata BLOB, reply_to_message_id INTEGER, PRIMARY KEY(mid, uid))").stepThis().dispose();
        sQLiteDatabase.executeFast("CREATE INDEX IF NOT EXISTS send_state_idx_scheduled_messages_v2 ON scheduled_messages_v2(mid, send_state, date);").stepThis().dispose();
        sQLiteDatabase.executeFast("CREATE INDEX IF NOT EXISTS uid_date_idx_scheduled_messages_v2 ON scheduled_messages_v2(uid, date);").stepThis().dispose();
        sQLiteDatabase.executeFast("CREATE INDEX IF NOT EXISTS reply_to_idx_scheduled_messages_v2 ON scheduled_messages_v2(mid, reply_to_message_id);").stepThis().dispose();
        sQLiteDatabase.executeFast("CREATE INDEX IF NOT EXISTS idx_to_reply_scheduled_messages_v2 ON scheduled_messages_v2(reply_to_message_id, mid);").stepThis().dispose();
        sQLiteDatabase.executeFast("CREATE TABLE messages_v2(mid INTEGER, uid INTEGER, read_state INTEGER, send_state INTEGER, date INTEGER, data BLOB, out INTEGER, ttl INTEGER, media INTEGER, replydata BLOB, imp INTEGER, mention INTEGER, forwards INTEGER, replies_data BLOB, thread_reply_id INTEGER, is_channel INTEGER, reply_to_message_id INTEGER, custom_params BLOB, group_id INTEGER, reply_to_story_id INTEGER, PRIMARY KEY(mid, uid))").stepThis().dispose();
        sQLiteDatabase.executeFast("CREATE INDEX IF NOT EXISTS uid_mid_read_out_idx_messages_v2 ON messages_v2(uid, mid, read_state, out);").stepThis().dispose();
        sQLiteDatabase.executeFast("CREATE INDEX IF NOT EXISTS uid_date_mid_idx_messages_v2 ON messages_v2(uid, date, mid);").stepThis().dispose();
        sQLiteDatabase.executeFast("CREATE INDEX IF NOT EXISTS mid_out_idx_messages_v2 ON messages_v2(mid, out);").stepThis().dispose();
        sQLiteDatabase.executeFast("CREATE INDEX IF NOT EXISTS task_idx_messages_v2 ON messages_v2(uid, out, read_state, ttl, date, send_state);").stepThis().dispose();
        sQLiteDatabase.executeFast("CREATE INDEX IF NOT EXISTS send_state_idx_messages_v2 ON messages_v2(mid, send_state, date);").stepThis().dispose();
        sQLiteDatabase.executeFast("CREATE INDEX IF NOT EXISTS uid_mention_idx_messages_v2 ON messages_v2(uid, mention, read_state);").stepThis().dispose();
        sQLiteDatabase.executeFast("CREATE INDEX IF NOT EXISTS is_channel_idx_messages_v2 ON messages_v2(mid, is_channel);").stepThis().dispose();
        sQLiteDatabase.executeFast("CREATE INDEX IF NOT EXISTS reply_to_idx_messages_v2 ON messages_v2(mid, reply_to_message_id);").stepThis().dispose();
        sQLiteDatabase.executeFast("CREATE INDEX IF NOT EXISTS idx_to_reply_messages_v2 ON messages_v2(reply_to_message_id, mid);").stepThis().dispose();
        sQLiteDatabase.executeFast("CREATE INDEX IF NOT EXISTS uid_mid_groupid_messages_v2 ON messages_v2(uid, mid, group_id);").stepThis().dispose();
        sQLiteDatabase.executeFast("CREATE TABLE saved_dialogs(did INTEGER PRIMARY KEY, date INTEGER, last_mid INTEGER, pinned INTEGER, flags INTEGER, folder_id INTEGER, last_mid_group INTEGER, count INTEGER)").stepThis().dispose();
        sQLiteDatabase.executeFast("CREATE INDEX IF NOT EXISTS date_idx_dialogs ON saved_dialogs(date);").stepThis().dispose();
        sQLiteDatabase.executeFast("CREATE INDEX IF NOT EXISTS last_mid_idx_dialogs ON saved_dialogs(last_mid);").stepThis().dispose();
        sQLiteDatabase.executeFast("CREATE INDEX IF NOT EXISTS folder_id_idx_dialogs ON saved_dialogs(folder_id);").stepThis().dispose();
        sQLiteDatabase.executeFast("CREATE INDEX IF NOT EXISTS flags_idx_dialogs ON saved_dialogs(flags);").stepThis().dispose();
        sQLiteDatabase.executeFast("CREATE TABLE download_queue(uid INTEGER, type INTEGER, date INTEGER, data BLOB, parent TEXT, PRIMARY KEY (uid, type));").stepThis().dispose();
        sQLiteDatabase.executeFast("CREATE INDEX IF NOT EXISTS type_date_idx_download_queue ON download_queue(type, date);").stepThis().dispose();
        sQLiteDatabase.executeFast("CREATE TABLE user_contacts_v7(key TEXT PRIMARY KEY, uid INTEGER, fname TEXT, sname TEXT, imported INTEGER)").stepThis().dispose();
        sQLiteDatabase.executeFast("CREATE TABLE user_phones_v7(key TEXT, phone TEXT, sphone TEXT, deleted INTEGER, PRIMARY KEY (key, phone))").stepThis().dispose();
        sQLiteDatabase.executeFast("CREATE INDEX IF NOT EXISTS sphone_deleted_idx_user_phones ON user_phones_v7(sphone, deleted);").stepThis().dispose();
        sQLiteDatabase.executeFast("CREATE TABLE dialogs(did INTEGER PRIMARY KEY, date INTEGER, unread_count INTEGER, last_mid INTEGER, inbox_max INTEGER, outbox_max INTEGER, last_mid_i INTEGER, unread_count_i INTEGER, pts INTEGER, date_i INTEGER, pinned INTEGER, flags INTEGER, folder_id INTEGER, data BLOB, unread_reactions INTEGER, last_mid_group INTEGER, ttl_period INTEGER)").stepThis().dispose();
        sQLiteDatabase.executeFast("CREATE INDEX IF NOT EXISTS date_idx_dialogs ON dialogs(date);").stepThis().dispose();
        sQLiteDatabase.executeFast("CREATE INDEX IF NOT EXISTS last_mid_idx_dialogs ON dialogs(last_mid);").stepThis().dispose();
        sQLiteDatabase.executeFast("CREATE INDEX IF NOT EXISTS unread_count_idx_dialogs ON dialogs(unread_count);").stepThis().dispose();
        sQLiteDatabase.executeFast("CREATE INDEX IF NOT EXISTS last_mid_i_idx_dialogs ON dialogs(last_mid_i);").stepThis().dispose();
        sQLiteDatabase.executeFast("CREATE INDEX IF NOT EXISTS unread_count_i_idx_dialogs ON dialogs(unread_count_i);").stepThis().dispose();
        sQLiteDatabase.executeFast("CREATE INDEX IF NOT EXISTS folder_id_idx_dialogs ON dialogs(folder_id);").stepThis().dispose();
        sQLiteDatabase.executeFast("CREATE INDEX IF NOT EXISTS flags_idx_dialogs ON dialogs(flags);").stepThis().dispose();
        sQLiteDatabase.executeFast("CREATE TABLE dialog_filter(id INTEGER PRIMARY KEY, ord INTEGER, unread_count INTEGER, flags INTEGER, title TEXT, color INTEGER DEFAULT -1, entities BLOB, noanimate INTEGER)").stepThis().dispose();
        sQLiteDatabase.executeFast("CREATE TABLE dialog_filter_ep(id INTEGER, peer INTEGER, PRIMARY KEY (id, peer))").stepThis().dispose();
        sQLiteDatabase.executeFast("CREATE TABLE dialog_filter_pin_v2(id INTEGER, peer INTEGER, pin INTEGER, PRIMARY KEY (id, peer))").stepThis().dispose();
        sQLiteDatabase.executeFast("CREATE TABLE randoms_v2(random_id INTEGER, mid INTEGER, uid INTEGER, PRIMARY KEY (random_id, mid, uid))").stepThis().dispose();
        sQLiteDatabase.executeFast("CREATE INDEX IF NOT EXISTS mid_idx_randoms_v2 ON randoms_v2(mid, uid);").stepThis().dispose();
        sQLiteDatabase.executeFast("CREATE TABLE enc_tasks_v4(mid INTEGER, uid INTEGER, date INTEGER, media INTEGER, PRIMARY KEY(mid, uid, media))").stepThis().dispose();
        sQLiteDatabase.executeFast("CREATE INDEX IF NOT EXISTS date_idx_enc_tasks_v4 ON enc_tasks_v4(date);").stepThis().dispose();
        sQLiteDatabase.executeFast("CREATE TABLE messages_seq(mid INTEGER PRIMARY KEY, seq_in INTEGER, seq_out INTEGER);").stepThis().dispose();
        sQLiteDatabase.executeFast("CREATE INDEX IF NOT EXISTS seq_idx_messages_seq ON messages_seq(seq_in, seq_out);").stepThis().dispose();
        sQLiteDatabase.executeFast("CREATE TABLE params(id INTEGER PRIMARY KEY, seq INTEGER, pts INTEGER, date INTEGER, qts INTEGER, lsv INTEGER, sg INTEGER, pbytes BLOB)").stepThis().dispose();
        sQLiteDatabase.executeFast("INSERT INTO params VALUES(1, 0, 0, 0, 0, 0, 0, NULL)").stepThis().dispose();
        sQLiteDatabase.executeFast("CREATE TABLE media_v4(mid INTEGER, uid INTEGER, date INTEGER, type INTEGER, data BLOB, PRIMARY KEY(mid, uid, type))").stepThis().dispose();
        sQLiteDatabase.executeFast("CREATE INDEX IF NOT EXISTS uid_mid_type_date_idx_media_v4 ON media_v4(uid, mid, type, date);").stepThis().dispose();
        sQLiteDatabase.executeFast("CREATE TABLE bot_keyboard(uid INTEGER PRIMARY KEY, mid INTEGER, info BLOB)").stepThis().dispose();
        sQLiteDatabase.executeFast("CREATE INDEX IF NOT EXISTS bot_keyboard_idx_mid_v2 ON bot_keyboard(mid, uid);").stepThis().dispose();
        sQLiteDatabase.executeFast("CREATE TABLE bot_keyboard_topics(uid INTEGER, tid INTEGER, mid INTEGER, info BLOB, PRIMARY KEY(uid, tid))").stepThis().dispose();
        sQLiteDatabase.executeFast("CREATE INDEX IF NOT EXISTS bot_keyboard_topics_idx_mid_v2 ON bot_keyboard_topics(mid, uid, tid);").stepThis().dispose();
        sQLiteDatabase.executeFast("CREATE TABLE chat_settings_v2(uid INTEGER PRIMARY KEY, info BLOB, pinned INTEGER, online INTEGER, inviter INTEGER, links INTEGER, participants_count INTEGER)").stepThis().dispose();
        sQLiteDatabase.executeFast("CREATE INDEX IF NOT EXISTS chat_settings_pinned_idx ON chat_settings_v2(uid, pinned) WHERE pinned != 0;").stepThis().dispose();
        sQLiteDatabase.executeFast("CREATE TABLE user_settings(uid INTEGER PRIMARY KEY, info BLOB, pinned INTEGER)").stepThis().dispose();
        sQLiteDatabase.executeFast("CREATE INDEX IF NOT EXISTS user_settings_pinned_idx ON user_settings(uid, pinned) WHERE pinned != 0;").stepThis().dispose();
        sQLiteDatabase.executeFast("CREATE TABLE chat_pinned_v2(uid INTEGER, mid INTEGER, data BLOB, PRIMARY KEY (uid, mid));").stepThis().dispose();
        sQLiteDatabase.executeFast("CREATE TABLE chat_pinned_count(uid INTEGER PRIMARY KEY, count INTEGER, end INTEGER);").stepThis().dispose();
        sQLiteDatabase.executeFast("CREATE TABLE chat_hints(did INTEGER, type INTEGER, rating REAL, date INTEGER, PRIMARY KEY(did, type))").stepThis().dispose();
        sQLiteDatabase.executeFast("CREATE INDEX IF NOT EXISTS chat_hints_rating_idx ON chat_hints(rating);").stepThis().dispose();
        sQLiteDatabase.executeFast("CREATE TABLE botcache(id TEXT PRIMARY KEY, date INTEGER, data BLOB)").stepThis().dispose();
        sQLiteDatabase.executeFast("CREATE INDEX IF NOT EXISTS botcache_date_idx ON botcache(date);").stepThis().dispose();
        sQLiteDatabase.executeFast("CREATE TABLE users_data(uid INTEGER PRIMARY KEY, about TEXT)").stepThis().dispose();
        sQLiteDatabase.executeFast("CREATE TABLE users(uid INTEGER PRIMARY KEY, name TEXT, status INTEGER, data BLOB)").stepThis().dispose();
        sQLiteDatabase.executeFast("CREATE TABLE chats(uid INTEGER PRIMARY KEY, name TEXT, data BLOB)").stepThis().dispose();
        sQLiteDatabase.executeFast("CREATE TABLE enc_chats(uid INTEGER PRIMARY KEY, user INTEGER, name TEXT, data BLOB, g BLOB, authkey BLOB, ttl INTEGER, layer INTEGER, seq_in INTEGER, seq_out INTEGER, use_count INTEGER, exchange_id INTEGER, key_date INTEGER, fprint INTEGER, fauthkey BLOB, khash BLOB, in_seq_no INTEGER, admin_id INTEGER, mtproto_seq INTEGER)").stepThis().dispose();
        sQLiteDatabase.executeFast("CREATE TABLE channel_users_v2(did INTEGER, uid INTEGER, date INTEGER, data BLOB, PRIMARY KEY(did, uid))").stepThis().dispose();
        sQLiteDatabase.executeFast("CREATE TABLE channel_admins_v3(did INTEGER, uid INTEGER, data BLOB, PRIMARY KEY(did, uid))").stepThis().dispose();
        sQLiteDatabase.executeFast("CREATE TABLE contacts(uid INTEGER PRIMARY KEY, mutual INTEGER)").stepThis().dispose();
        sQLiteDatabase.executeFast("CREATE TABLE dialog_photos(uid INTEGER, id INTEGER, num INTEGER, data BLOB, PRIMARY KEY (uid, id))").stepThis().dispose();
        sQLiteDatabase.executeFast("CREATE TABLE dialog_photos_count(uid INTEGER PRIMARY KEY, count INTEGER)").stepThis().dispose();
        sQLiteDatabase.executeFast("CREATE TABLE dialog_settings(did INTEGER PRIMARY KEY, flags INTEGER);").stepThis().dispose();
        sQLiteDatabase.executeFast("CREATE TABLE web_recent_v3(id TEXT, type INTEGER, image_url TEXT, thumb_url TEXT, local_url TEXT, width INTEGER, height INTEGER, size INTEGER, date INTEGER, document BLOB, PRIMARY KEY (id, type));").stepThis().dispose();
        sQLiteDatabase.executeFast("CREATE TABLE stickers_v2(id INTEGER PRIMARY KEY, data BLOB, date INTEGER, hash INTEGER);").stepThis().dispose();
        sQLiteDatabase.executeFast("CREATE TABLE stickers_featured(id INTEGER PRIMARY KEY, data BLOB, unread BLOB, date INTEGER, hash INTEGER, premium INTEGER, emoji INTEGER);").stepThis().dispose();
        sQLiteDatabase.executeFast("CREATE TABLE stickers_dice(emoji TEXT PRIMARY KEY, data BLOB, date INTEGER);").stepThis().dispose();
        sQLiteDatabase.executeFast("CREATE TABLE hashtag_recent_v2(id TEXT PRIMARY KEY, date INTEGER);").stepThis().dispose();
        sQLiteDatabase.executeFast("CREATE TABLE webpage_pending_v2(id INTEGER, mid INTEGER, uid INTEGER, PRIMARY KEY (id, mid, uid));").stepThis().dispose();
        sQLiteDatabase.executeFast("CREATE TABLE sent_files_v2(uid TEXT, type INTEGER, data BLOB, parent TEXT, PRIMARY KEY (uid, type))").stepThis().dispose();
        sQLiteDatabase.executeFast("CREATE TABLE search_recent(did INTEGER PRIMARY KEY, date INTEGER);").stepThis().dispose();
        sQLiteDatabase.executeFast("CREATE TABLE media_counts_v2(uid INTEGER, type INTEGER, count INTEGER, old INTEGER, PRIMARY KEY(uid, type))").stepThis().dispose();
        sQLiteDatabase.executeFast("CREATE TABLE keyvalue(id TEXT PRIMARY KEY, value TEXT)").stepThis().dispose();
        sQLiteDatabase.executeFast("CREATE TABLE bot_info_v2(uid INTEGER, dialogId INTEGER, info BLOB, PRIMARY KEY(uid, dialogId))").stepThis().dispose();
        sQLiteDatabase.executeFast("CREATE TABLE pending_tasks(id INTEGER PRIMARY KEY, data BLOB);").stepThis().dispose();
        sQLiteDatabase.executeFast("CREATE TABLE requested_holes(uid INTEGER, seq_out_start INTEGER, seq_out_end INTEGER, PRIMARY KEY (uid, seq_out_start, seq_out_end));").stepThis().dispose();
        sQLiteDatabase.executeFast("CREATE TABLE sharing_locations(uid INTEGER PRIMARY KEY, mid INTEGER, date INTEGER, period INTEGER, message BLOB, proximity INTEGER);").stepThis().dispose();
        sQLiteDatabase.executeFast("CREATE TABLE stickersets2(id INTEGER PRIMATE KEY, data BLOB, hash INTEGER, date INTEGER, short_name TEXT);").stepThis().dispose();
        sQLiteDatabase.executeFast("CREATE INDEX IF NOT EXISTS stickersets2_id_index ON stickersets2(id);").stepThis().dispose();
        sQLiteDatabase.executeFast("CREATE INDEX IF NOT EXISTS stickersets2_id_short_name ON stickersets2(id, short_name);").stepThis().dispose();
        sQLiteDatabase.executeFast("CREATE INDEX IF NOT EXISTS stickers_featured_emoji_index ON stickers_featured(emoji);").stepThis().dispose();
        sQLiteDatabase.executeFast("CREATE TABLE shortcut_widget(id INTEGER, did INTEGER, ord INTEGER, PRIMARY KEY (id, did));").stepThis().dispose();
        sQLiteDatabase.executeFast("CREATE INDEX IF NOT EXISTS shortcut_widget_did ON shortcut_widget(did);").stepThis().dispose();
        sQLiteDatabase.executeFast("CREATE TABLE emoji_keywords_v2(lang TEXT, keyword TEXT, emoji TEXT, PRIMARY KEY(lang, keyword, emoji));").stepThis().dispose();
        sQLiteDatabase.executeFast("CREATE INDEX IF NOT EXISTS emoji_keywords_v2_keyword ON emoji_keywords_v2(keyword);").stepThis().dispose();
        sQLiteDatabase.executeFast("CREATE TABLE emoji_keywords_info_v2(lang TEXT PRIMARY KEY, alias TEXT, version INTEGER, date INTEGER);").stepThis().dispose();
        sQLiteDatabase.executeFast("CREATE TABLE wallpapers2(uid INTEGER PRIMARY KEY, data BLOB, num INTEGER)").stepThis().dispose();
        sQLiteDatabase.executeFast("CREATE INDEX IF NOT EXISTS wallpapers_num ON wallpapers2(num);").stepThis().dispose();
        sQLiteDatabase.executeFast("CREATE TABLE unread_push_messages(uid INTEGER, mid INTEGER, random INTEGER, date INTEGER, data BLOB, fm TEXT, name TEXT, uname TEXT, flags INTEGER, topicId INTEGER, is_reaction INTEGER, PRIMARY KEY(uid, mid))").stepThis().dispose();
        sQLiteDatabase.executeFast("CREATE INDEX IF NOT EXISTS unread_push_messages_idx_date ON unread_push_messages(date);").stepThis().dispose();
        sQLiteDatabase.executeFast("CREATE INDEX IF NOT EXISTS unread_push_messages_idx_random ON unread_push_messages(random);").stepThis().dispose();
        sQLiteDatabase.executeFast("CREATE TABLE polls_v2(mid INTEGER, uid INTEGER, id INTEGER, PRIMARY KEY (mid, uid));").stepThis().dispose();
        sQLiteDatabase.executeFast("CREATE INDEX IF NOT EXISTS polls_id_v2 ON polls_v2(id);").stepThis().dispose();
        sQLiteDatabase.executeFast("CREATE TABLE reactions(data BLOB, hash INTEGER, date INTEGER);").stepThis().dispose();
        sQLiteDatabase.executeFast("CREATE TABLE reaction_mentions(message_id INTEGER, state INTEGER, dialog_id INTEGER, PRIMARY KEY(message_id, dialog_id))").stepThis().dispose();
        sQLiteDatabase.executeFast("CREATE INDEX IF NOT EXISTS reaction_mentions_did ON reaction_mentions(dialog_id);").stepThis().dispose();
        sQLiteDatabase.executeFast("CREATE TABLE downloading_documents(data BLOB, hash INTEGER, id INTEGER, state INTEGER, date INTEGER, PRIMARY KEY(hash, id));").stepThis().dispose();
        sQLiteDatabase.executeFast("CREATE TABLE animated_emoji(document_id INTEGER PRIMARY KEY, data BLOB);").stepThis().dispose();
        sQLiteDatabase.executeFast("CREATE TABLE attach_menu_bots(data BLOB, hash INTEGER, date INTEGER);").stepThis().dispose();
        sQLiteDatabase.executeFast("CREATE TABLE premium_promo(data BLOB, date INTEGER);").stepThis().dispose();
        sQLiteDatabase.executeFast("CREATE TABLE emoji_statuses(data BLOB, type INTEGER);").stepThis().dispose();
        sQLiteDatabase.executeFast("CREATE TABLE messages_holes_topics(uid INTEGER, topic_id INTEGER, start INTEGER, end INTEGER, PRIMARY KEY(uid, topic_id, start));").stepThis().dispose();
        sQLiteDatabase.executeFast("CREATE INDEX IF NOT EXISTS uid_end_messages_holes ON messages_holes_topics(uid, topic_id, end);").stepThis().dispose();
        sQLiteDatabase.executeFast("CREATE TABLE messages_topics(mid INTEGER, uid INTEGER, topic_id INTEGER, read_state INTEGER, send_state INTEGER, date INTEGER, data BLOB, out INTEGER, ttl INTEGER, media INTEGER, replydata BLOB, imp INTEGER, mention INTEGER, forwards INTEGER, replies_data BLOB, thread_reply_id INTEGER, is_channel INTEGER, reply_to_message_id INTEGER, custom_params BLOB, reply_to_story_id INTEGER, PRIMARY KEY(mid, topic_id, uid))").stepThis().dispose();
        sQLiteDatabase.executeFast("CREATE INDEX IF NOT EXISTS uid_date_mid_idx_messages_topics ON messages_topics(uid, date, mid);").stepThis().dispose();
        sQLiteDatabase.executeFast("CREATE INDEX IF NOT EXISTS mid_out_idx_messages_topics ON messages_topics(mid, out);").stepThis().dispose();
        sQLiteDatabase.executeFast("CREATE INDEX IF NOT EXISTS task_idx_messages_topics ON messages_topics(uid, out, read_state, ttl, date, send_state);").stepThis().dispose();
        sQLiteDatabase.executeFast("CREATE INDEX IF NOT EXISTS send_state_idx_messages_topics ON messages_topics(mid, send_state, date);").stepThis().dispose();
        sQLiteDatabase.executeFast("CREATE INDEX IF NOT EXISTS is_channel_idx_messages_topics ON messages_topics(mid, is_channel);").stepThis().dispose();
        sQLiteDatabase.executeFast("CREATE INDEX IF NOT EXISTS reply_to_idx_messages_topics ON messages_topics(mid, reply_to_message_id);").stepThis().dispose();
        sQLiteDatabase.executeFast("CREATE INDEX IF NOT EXISTS idx_to_reply_messages_topics ON messages_topics(reply_to_message_id, mid);").stepThis().dispose();
        sQLiteDatabase.executeFast("CREATE INDEX IF NOT EXISTS mid_uid_messages_topics ON messages_topics(mid, uid);").stepThis().dispose();
        sQLiteDatabase.executeFast("CREATE INDEX IF NOT EXISTS uid_mid_read_out_idx_messages_topics ON messages_topics(uid, topic_id, mid, read_state, out);").stepThis().dispose();
        sQLiteDatabase.executeFast("CREATE INDEX IF NOT EXISTS uid_mention_idx_messages_topics ON messages_topics(uid, topic_id, mention, read_state);").stepThis().dispose();
        sQLiteDatabase.executeFast("CREATE INDEX IF NOT EXISTS uid_topic_id_messages_topics ON messages_topics(uid, topic_id);").stepThis().dispose();
        sQLiteDatabase.executeFast("CREATE INDEX IF NOT EXISTS uid_topic_id_date_mid_messages_topics ON messages_topics(uid, topic_id, date, mid);").stepThis().dispose();
        sQLiteDatabase.executeFast("CREATE INDEX IF NOT EXISTS uid_topic_id_mid_messages_topics ON messages_topics(uid, topic_id, mid);").stepThis().dispose();
        sQLiteDatabase.executeFast("CREATE TABLE media_topics(mid INTEGER, uid INTEGER, topic_id INTEGER, date INTEGER, type INTEGER, data BLOB, PRIMARY KEY(mid, uid, topic_id, type))").stepThis().dispose();
        sQLiteDatabase.executeFast("CREATE INDEX IF NOT EXISTS uid_mid_type_date_idx_media_topics ON media_topics(uid, topic_id, mid, type, date);").stepThis().dispose();
        sQLiteDatabase.executeFast("CREATE TABLE media_holes_topics(uid INTEGER, topic_id INTEGER, type INTEGER, start INTEGER, end INTEGER, PRIMARY KEY(uid, topic_id, type, start));").stepThis().dispose();
        sQLiteDatabase.executeFast("CREATE INDEX IF NOT EXISTS uid_end_media_holes_topics ON media_holes_topics(uid, topic_id, type, end);").stepThis().dispose();
        sQLiteDatabase.executeFast("CREATE TABLE topics(did INTEGER, topic_id INTEGER, data BLOB, top_message INTEGER, topic_message BLOB, unread_count INTEGER, max_read_id INTEGER, unread_mentions INTEGER, unread_reactions INTEGER, read_outbox INTEGER, pinned INTEGER, total_messages_count INTEGER, hidden INTEGER, edit_date INTEGER, PRIMARY KEY(did, topic_id));").stepThis().dispose();
        sQLiteDatabase.executeFast("CREATE INDEX IF NOT EXISTS did_top_message_topics ON topics(did, top_message);").stepThis().dispose();
        sQLiteDatabase.executeFast("CREATE INDEX IF NOT EXISTS did_topics ON topics(did);").stepThis().dispose();
        sQLiteDatabase.executeFast("CREATE TABLE media_counts_topics(uid INTEGER, topic_id INTEGER, type INTEGER, count INTEGER, old INTEGER, PRIMARY KEY(uid, topic_id, type))").stepThis().dispose();
        sQLiteDatabase.executeFast("CREATE TABLE reaction_mentions_topics(message_id INTEGER, state INTEGER, dialog_id INTEGER, topic_id INTEGER, PRIMARY KEY(message_id, dialog_id, topic_id))").stepThis().dispose();
        sQLiteDatabase.executeFast("CREATE INDEX IF NOT EXISTS reaction_mentions_topics_did ON reaction_mentions_topics(dialog_id, topic_id);").stepThis().dispose();
        sQLiteDatabase.executeFast("CREATE TABLE emoji_groups(type INTEGER PRIMARY KEY, data BLOB)").stepThis().dispose();
        sQLiteDatabase.executeFast("CREATE TABLE app_config(data BLOB)").stepThis().dispose();
        sQLiteDatabase.executeFast("CREATE TABLE effects(data BLOB)").stepThis().dispose();
        sQLiteDatabase.executeFast("CREATE TABLE stories (dialog_id INTEGER, story_id INTEGER, data BLOB, custom_params BLOB, PRIMARY KEY (dialog_id, story_id));").stepThis().dispose();
        sQLiteDatabase.executeFast("CREATE TABLE stories_counter (dialog_id INTEGER PRIMARY KEY, count INTEGER, max_read INTEGER);").stepThis().dispose();
        sQLiteDatabase.executeFast("CREATE TABLE profile_stories (dialog_id INTEGER, story_id INTEGER, data BLOB, type INTEGER, seen INTEGER, pin INTEGER, PRIMARY KEY(dialog_id, story_id));").stepThis().dispose();
        sQLiteDatabase.executeFast("CREATE TABLE story_drafts (id INTEGER PRIMARY KEY, date INTEGER, data BLOB, type INTEGER);").stepThis().dispose();
        sQLiteDatabase.executeFast("CREATE TABLE story_pushes (uid INTEGER, sid INTEGER, date INTEGER, localName TEXT, flags INTEGER, expire_date INTEGER, PRIMARY KEY(uid, sid));").stepThis().dispose();
        sQLiteDatabase.executeFast("CREATE TABLE unconfirmed_auth (data BLOB);").stepThis().dispose();
        sQLiteDatabase.executeFast("CREATE TABLE saved_reaction_tags (topic_id INTEGER PRIMARY KEY, data BLOB);").stepThis().dispose();
        sQLiteDatabase.executeFast("CREATE TABLE tag_message_id(mid INTEGER, topic_id INTEGER, tag INTEGER, text TEXT);").stepThis().dispose();
        sQLiteDatabase.executeFast("CREATE INDEX IF NOT EXISTS tag_idx_tag_message_id ON tag_message_id(tag);").stepThis().dispose();
        sQLiteDatabase.executeFast("CREATE INDEX IF NOT EXISTS tag_text_idx_tag_message_id ON tag_message_id(tag, text COLLATE NOCASE);").stepThis().dispose();
        sQLiteDatabase.executeFast("CREATE INDEX IF NOT EXISTS tag_topic_idx_tag_message_id ON tag_message_id(topic_id, tag);").stepThis().dispose();
        sQLiteDatabase.executeFast("CREATE INDEX IF NOT EXISTS tag_topic_text_idx_tag_message_id ON tag_message_id(topic_id, tag, text COLLATE NOCASE);").stepThis().dispose();
        sQLiteDatabase.executeFast("CREATE TABLE business_replies(topic_id INTEGER PRIMARY KEY, name TEXT, order_value INTEGER, count INTEGER);").stepThis().dispose();
        sQLiteDatabase.executeFast("CREATE TABLE quick_replies_messages(mid INTEGER, topic_id INTEGER, send_state INTEGER, date INTEGER, data BLOB, ttl INTEGER, replydata BLOB, reply_to_message_id INTEGER, PRIMARY KEY(mid, topic_id))").stepThis().dispose();
        sQLiteDatabase.executeFast("CREATE INDEX IF NOT EXISTS topic_date_idx_quick_replies_messages ON quick_replies_messages(topic_id, date);").stepThis().dispose();
        sQLiteDatabase.executeFast("CREATE INDEX IF NOT EXISTS reply_to_idx_quick_replies_messages ON quick_replies_messages(mid, reply_to_message_id);").stepThis().dispose();
        sQLiteDatabase.executeFast("CREATE INDEX IF NOT EXISTS idx_to_reply_quick_replies_messages ON quick_replies_messages(reply_to_message_id, mid);").stepThis().dispose();
        sQLiteDatabase.executeFast("CREATE TABLE business_links(data BLOB, order_value INTEGER);").stepThis().dispose();
        sQLiteDatabase.executeFast("CREATE TABLE fact_checks(hash INTEGER PRIMARY KEY, data BLOB, expires INTEGER);").stepThis().dispose();
        sQLiteDatabase.executeFast("CREATE TABLE popular_bots(uid INTEGER PRIMARY KEY, time INTEGER, offset TEXT, pos INTEGER);").stepThis().dispose();
        sQLiteDatabase.executeFast("CREATE TABLE star_gifts2(id INTEGER PRIMARY KEY, data BLOB, hash INTEGER, time INTEGER, pos INTEGER);").stepThis().dispose();
        sQLiteDatabase.executeFast("PRAGMA user_version = 162").stepThis().dispose();
    }

    private void createTaskForSecretMedia(long r17, android.util.SparseArray<java.util.ArrayList<java.lang.Integer>> r19) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MessagesStorage.createTaskForSecretMedia(long, android.util.SparseArray):void");
    }

    public void lambda$deleteDialogFilter$69(MessagesController.DialogFilter dialogFilter) {
        try {
            this.dialogFilters.remove(dialogFilter);
            this.dialogFiltersMap.remove(dialogFilter.id);
            this.database.executeFast("DELETE FROM dialog_filter WHERE id = " + dialogFilter.id).stepThis().dispose();
            this.database.executeFast("DELETE FROM dialog_filter_ep WHERE id = " + dialogFilter.id).stepThis().dispose();
            this.database.executeFast("DELETE FROM dialog_filter_pin_v2 WHERE id = " + dialogFilter.id).stepThis().dispose();
        } catch (Exception e) {
            checkSQLException(e);
        }
    }

    private void deleteFromDownloadQueue(final java.util.ArrayList<android.util.Pair<java.lang.Long, java.lang.Integer>> r9, boolean r10) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MessagesStorage.deleteFromDownloadQueue(java.util.ArrayList, boolean):void");
    }

    private void doneHolesInTable(String str, long j, int i, long j2) {
        SQLiteDatabase sQLiteDatabase;
        String format;
        SQLiteDatabase sQLiteDatabase2;
        String str2;
        int i2 = 2;
        if (j2 != 0) {
            sQLiteDatabase = this.database;
            Locale locale = Locale.US;
            if (i == 0) {
                format = String.format(locale, "DELETE FROM " + str + " WHERE uid = %d AND topic_id = %d", Long.valueOf(j), Long.valueOf(j2));
            } else {
                format = String.format(locale, "DELETE FROM " + str + " WHERE uid = %d AND topic_id = %d AND start = 0", Long.valueOf(j), Long.valueOf(j2));
            }
        } else {
            sQLiteDatabase = this.database;
            Locale locale2 = Locale.US;
            if (i == 0) {
                format = String.format(locale2, "DELETE FROM " + str + " WHERE uid = %d", Long.valueOf(j));
            } else {
                format = String.format(locale2, "DELETE FROM " + str + " WHERE uid = %d AND start = 0", Long.valueOf(j));
            }
        }
        sQLiteDatabase.executeFast(format).stepThis().dispose();
        SQLitePreparedStatement sQLitePreparedStatement = null;
        try {
            try {
                if (j2 != 0) {
                    sQLiteDatabase2 = this.database;
                    str2 = "REPLACE INTO " + str + " VALUES(?, ?, ?, ?)";
                } else {
                    sQLiteDatabase2 = this.database;
                    str2 = "REPLACE INTO " + str + " VALUES(?, ?, ?)";
                }
                sQLitePreparedStatement = sQLiteDatabase2.executeFast(str2);
                sQLitePreparedStatement.requery();
                sQLitePreparedStatement.bindLong(1, j);
                if (j2 != 0) {
                    sQLitePreparedStatement.bindLong(2, j2);
                    i2 = 3;
                }
                sQLitePreparedStatement.bindInteger(i2, 1);
                sQLitePreparedStatement.bindInteger(i2 + 1, 1);
                sQLitePreparedStatement.step();
                sQLitePreparedStatement.dispose();
            } catch (Exception e) {
                throw e;
            }
        } catch (Throwable th) {
            if (sQLitePreparedStatement != null) {
                sQLitePreparedStatement.dispose();
            }
            throw th;
        }
    }

    private void ensureOpened() {
        try {
            this.openSync.await();
        } catch (Throwable unused) {
        }
    }

    private void fixUnsupportedMedia(TLRPC.Message message) {
        if (message == null) {
            return;
        }
        TLRPC.MessageMedia messageMedia = message.media;
        if (messageMedia instanceof TLRPC.TL_messageMediaUnsupported_old) {
            if (messageMedia.bytes.length == 0) {
                messageMedia.bytes = Utilities.intToBytes(198);
            }
        } else if (messageMedia instanceof TLRPC.TL_messageMediaUnsupported) {
            TLRPC.TL_messageMediaUnsupported_old tL_messageMediaUnsupported_old = new TLRPC.TL_messageMediaUnsupported_old();
            message.media = tL_messageMediaUnsupported_old;
            tL_messageMediaUnsupported_old.bytes = Utilities.intToBytes(198);
            message.flags |= 512;
        }
    }

    private String formatUserSearchName(TLRPC.User user) {
        StringBuilder sb = new StringBuilder();
        String str = user.first_name;
        if (str != null && str.length() > 0) {
            sb.append(user.first_name);
        }
        String str2 = user.last_name;
        if (str2 != null && str2.length() > 0) {
            if (sb.length() > 0) {
                sb.append(" ");
            }
            sb.append(user.last_name);
        }
        sb.append(";;;");
        String str3 = user.username;
        if (str3 == null || str3.length() <= 0) {
            ArrayList<TLRPC.TL_username> arrayList = user.usernames;
            if (arrayList != null && arrayList.size() > 0) {
                for (int i = 0; i < user.usernames.size(); i++) {
                    TLRPC.TL_username tL_username = user.usernames.get(i);
                    if (tL_username != null && tL_username.active) {
                        sb.append(tL_username.username);
                        sb.append(";;");
                    }
                }
            }
        } else {
            sb.append(user.username);
        }
        return sb.toString().toLowerCase();
    }

    private int getDialogFolderIdInternal(long j) {
        SQLiteCursor sQLiteCursor = null;
        try {
            try {
                if (this.unknownDialogsIds.get(j) == null) {
                    sQLiteCursor = this.database.queryFinalized("SELECT folder_id FROM dialogs WHERE did = ?", Long.valueOf(j));
                    r3 = sQLiteCursor.next() ? sQLiteCursor.intValue(0) : -1;
                    sQLiteCursor.dispose();
                }
                return r3;
            } catch (Exception e) {
                checkSQLException(e);
                if (sQLiteCursor != null) {
                    sQLiteCursor.dispose();
                }
                return 0;
            }
        } finally {
            if (sQLiteCursor != null) {
                sQLiteCursor.dispose();
            }
        }
    }

    public static MessagesStorage getInstance(int i) {
        MessagesStorage messagesStorage = Instance[i];
        if (messagesStorage == null) {
            synchronized (lockObjects[i]) {
                try {
                    messagesStorage = Instance[i];
                    if (messagesStorage == null) {
                        MessagesStorage[] messagesStorageArr = Instance;
                        MessagesStorage messagesStorage2 = new MessagesStorage(i);
                        messagesStorageArr[i] = messagesStorage2;
                        messagesStorage = messagesStorage2;
                    }
                } finally {
                }
            }
        }
        return messagesStorage;
    }

    private static boolean isEmpty(SparseArray<?> sparseArray) {
        return sparseArray == null || sparseArray.size() == 0;
    }

    private static boolean isEmpty(SparseIntArray sparseIntArray) {
        return sparseIntArray == null || sparseIntArray.size() == 0;
    }

    private static boolean isEmpty(LongSparseArray longSparseArray) {
        return longSparseArray == null || longSparseArray.size() == 0;
    }

    private static boolean isEmpty(List<?> list) {
        return list == null || list.isEmpty();
    }

    private static boolean isEmpty(LongSparseIntArray longSparseIntArray) {
        return longSparseIntArray == null || longSparseIntArray.size() == 0;
    }

    private boolean isForum(long j) {
        int i = this.dialogIsForum.get(j, -1);
        if (i == -1) {
            TLRPC.Chat chat = getChat(-j);
            i = (chat == null || !chat.forum) ? 0 : 1;
            this.dialogIsForum.put(j, i);
        }
        return i == 1;
    }

    private boolean isValidKeyboardToSave(TLRPC.Message message) {
        TLRPC.ReplyMarkup replyMarkup = message.reply_markup;
        return (replyMarkup == null || (replyMarkup instanceof TLRPC.TL_replyInlineMarkup) || (replyMarkup.selective && !message.mentioned)) ? false : true;
    }

    public void lambda$addRecentLocalFile$79(TLRPC.Document document, String str, String str2) {
        SQLitePreparedStatement sQLitePreparedStatement = null;
        try {
            try {
                if (document != null) {
                    sQLitePreparedStatement = this.database.executeFast("UPDATE web_recent_v3 SET document = ? WHERE image_url = ?");
                    sQLitePreparedStatement.requery();
                    NativeByteBuffer nativeByteBuffer = new NativeByteBuffer(document.getObjectSize());
                    document.serializeToStream(nativeByteBuffer);
                    sQLitePreparedStatement.bindByteBuffer(1, nativeByteBuffer);
                    sQLitePreparedStatement.bindString(2, str);
                    sQLitePreparedStatement.step();
                    sQLitePreparedStatement.dispose();
                    nativeByteBuffer.reuse();
                } else {
                    sQLitePreparedStatement = this.database.executeFast("UPDATE web_recent_v3 SET local_url = ? WHERE image_url = ?");
                    sQLitePreparedStatement.requery();
                    sQLitePreparedStatement.bindString(1, str2);
                    sQLitePreparedStatement.bindString(2, str);
                    sQLitePreparedStatement.step();
                    sQLitePreparedStatement.dispose();
                }
            } catch (Exception e) {
                checkSQLException(e);
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

    public void lambda$applyPhoneBookUpdates$142(String str, String str2) {
        try {
            if (str.length() != 0) {
                this.database.executeFast(String.format(Locale.US, "UPDATE user_phones_v7 SET deleted = 0 WHERE sphone IN(%s)", str)).stepThis().dispose();
            }
            if (str2.length() != 0) {
                this.database.executeFast(String.format(Locale.US, "UPDATE user_phones_v7 SET deleted = 1 WHERE sphone IN(%s)", str2)).stepThis().dispose();
            }
        } catch (Exception e) {
            checkSQLException(e);
        }
    }

    public void lambda$broadcastQuickRepliesMessagesChange$204() {
        getNotificationCenter().lambda$postNotificationNameOnUIThread$1(NotificationCenter.quickRepliesUpdated, new Object[0]);
    }

    public void lambda$broadcastScheduledMessagesChange$203(Long l, int i) {
        getNotificationCenter().lambda$postNotificationNameOnUIThread$1(NotificationCenter.scheduledMessagesUpdated, l, Integer.valueOf(i), Boolean.TRUE);
    }

    public void lambda$checkIfFolderEmptyInternal$226(int i) {
        getMessagesController().onFolderEmpty(i);
    }

    public static int lambda$checkLoadedRemoteFilters$65(LongSparseIntArray longSparseIntArray, Long l, Long l2) {
        int i = longSparseIntArray.get(l.longValue());
        int i2 = longSparseIntArray.get(l2.longValue());
        if (i > i2) {
            return 1;
        }
        return i < i2 ? -1 : 0;
    }

    public void lambda$checkLoadedRemoteFilters$66(java.util.ArrayList r36, java.lang.Runnable r37) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MessagesStorage.lambda$checkLoadedRemoteFilters$66(java.util.ArrayList, java.lang.Runnable):void");
    }

    public void lambda$checkMessageByRandomId$147(long r7, boolean[] r9, java.util.concurrent.CountDownLatch r10) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MessagesStorage.lambda$checkMessageByRandomId$147(long, boolean[], java.util.concurrent.CountDownLatch):void");
    }

    public void lambda$checkMessageId$148(long r7, int r9, boolean[] r10, java.util.concurrent.CountDownLatch r11) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MessagesStorage.lambda$checkMessageId$148(long, int, boolean[], java.util.concurrent.CountDownLatch):void");
    }

    public void lambda$checkSQLException$8() {
        getNotificationCenter().lambda$postNotificationNameOnUIThread$1(NotificationCenter.onDatabaseReset, new Object[0]);
    }

    public void lambda$cleanup$5() {
        getMessagesController().getDifference();
    }

    public void lambda$cleanup$6(boolean z) {
        cleanupInternal(true);
        openDatabase(1);
        if (z) {
            Utilities.stageQueue.postRunnable(new Runnable() {
                @Override
                public final void run() {
                    MessagesStorage.this.lambda$cleanup$5();
                }
            });
        }
    }

    public void lambda$clearDownloadQueue$177(int i) {
        try {
            (i == 0 ? this.database.executeFast("DELETE FROM download_queue WHERE 1") : this.database.executeFast(String.format(Locale.US, "DELETE FROM download_queue WHERE type = %d", Integer.valueOf(i)))).stepThis().dispose();
        } catch (Exception e) {
            checkSQLException(e);
        }
    }

    public void lambda$clearLocalDatabase$43() {
        getMessagesController().getSavedMessagesController().cleanup();
    }

    public void lambda$clearLocalDatabase$44() {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MessagesStorage.lambda$clearLocalDatabase$44():void");
    }

    public void lambda$clearSentMedia$157() {
        try {
            this.database.executeFast("DELETE FROM sent_files_v2 WHERE 1").stepThis().dispose();
        } catch (Exception e) {
            checkSQLException(e);
        }
    }

    public void lambda$clearUserPhoto$88(long j, long j2) {
        try {
            this.database.executeFast("DELETE FROM dialog_photos WHERE uid = " + j + " AND id = " + j2).stepThis().dispose();
            this.database.executeFast("UPDATE dialog_photos_count SET count = count - 1 WHERE uid = " + j + " AND count > 0").stepThis().dispose();
        } catch (Exception e) {
            checkSQLException(e);
        }
    }

    public void lambda$clearUserPhotos$87(long j) {
        try {
            this.database.executeFast("DELETE FROM dialog_photos WHERE uid = " + j).stepThis().dispose();
            this.database.executeFast("DELETE FROM dialog_photos_count WHERE uid = " + j).stepThis().dispose();
        } catch (Exception e) {
            checkSQLException(e);
        }
    }

    public void lambda$clearWidgetDialogs$160(int i) {
        try {
            this.database.executeFast("DELETE FROM shortcut_widget WHERE id = " + i).stepThis().dispose();
        } catch (Exception e) {
            checkSQLException(e);
        }
    }

    public void lambda$containsLocalDialog$172(long r5, java.lang.Boolean[] r7, java.util.concurrent.CountDownLatch r8) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MessagesStorage.lambda$containsLocalDialog$172(long, java.lang.Boolean[], java.util.concurrent.CountDownLatch):void");
    }

    public void lambda$createOrEditTopic$192(long j, TLRPC.TL_forumTopic tL_forumTopic) {
        getMessagesController().getTopicsController().onTopicCreated(j, tL_forumTopic, false);
    }

    public void lambda$createPendingTask$10(long j, NativeByteBuffer nativeByteBuffer) {
        try {
            try {
                SQLitePreparedStatement executeFast = this.database.executeFast("REPLACE INTO pending_tasks VALUES(?, ?)");
                executeFast.bindLong(1, j);
                executeFast.bindByteBuffer(2, nativeByteBuffer);
                executeFast.step();
                executeFast.dispose();
            } catch (Exception e) {
                checkSQLException(e);
            }
        } finally {
            nativeByteBuffer.reuse();
        }
    }

    public void lambda$createTaskForMid$109(boolean z, long j, ArrayList arrayList) {
        if (!z) {
            markMessagesContentAsRead(j, arrayList, 0, 0);
        }
        getNotificationCenter().lambda$postNotificationNameOnUIThread$1(NotificationCenter.messagesReadContent, Long.valueOf(j), arrayList);
    }

    public void lambda$createTaskForMid$110(int i, int i2, int i3, int i4, final boolean z, final long j) {
        SQLitePreparedStatement sQLitePreparedStatement = null;
        try {
            try {
                int max = Math.max(i, i2) + i3;
                SparseArray<ArrayList<Integer>> sparseArray = new SparseArray<>();
                final ArrayList<Integer> arrayList = new ArrayList<>();
                arrayList.add(Integer.valueOf(i4));
                sparseArray.put(max, arrayList);
                AndroidUtilities.runOnUIThread(new Runnable() {
                    @Override
                    public final void run() {
                        MessagesStorage.this.lambda$createTaskForMid$109(z, j, arrayList);
                    }
                });
                SQLitePreparedStatement executeFast = this.database.executeFast("REPLACE INTO enc_tasks_v4 VALUES(?, ?, ?, ?)");
                for (int i5 = 0; i5 < sparseArray.size(); i5++) {
                    try {
                        int keyAt = sparseArray.keyAt(i5);
                        ArrayList<Integer> arrayList2 = sparseArray.get(keyAt);
                        for (int i6 = 0; i6 < arrayList2.size(); i6++) {
                            executeFast.requery();
                            executeFast.bindInteger(1, arrayList2.get(i6).intValue());
                            executeFast.bindLong(2, j);
                            executeFast.bindInteger(3, keyAt);
                            executeFast.bindInteger(4, 1);
                            executeFast.step();
                        }
                    } catch (Exception e) {
                        e = e;
                        sQLitePreparedStatement = executeFast;
                        checkSQLException(e);
                        if (sQLitePreparedStatement != null) {
                            sQLitePreparedStatement.dispose();
                            return;
                        }
                        return;
                    } catch (Throwable th) {
                        th = th;
                        sQLitePreparedStatement = executeFast;
                        if (sQLitePreparedStatement != null) {
                            sQLitePreparedStatement.dispose();
                        }
                        throw th;
                    }
                }
                executeFast.dispose();
                this.database.executeFast(String.format(Locale.US, "UPDATE messages_v2 SET ttl = 0 WHERE mid = %d AND uid = %d", Integer.valueOf(i4), Long.valueOf(j))).stepThis().dispose();
                getMessagesController().didAddedNewTask(max, j, sparseArray);
            } catch (Exception e2) {
                e = e2;
            }
        } catch (Throwable th2) {
            th = th2;
        }
    }

    public void lambda$createTaskForSecretChat$111(long j, ArrayList arrayList) {
        markMessagesContentAsRead(j, arrayList, 0, 0);
        getNotificationCenter().lambda$postNotificationNameOnUIThread$1(NotificationCenter.messagesReadContent, Long.valueOf(j), arrayList);
    }

    public void lambda$createTaskForSecretChat$112(int r20, java.util.ArrayList r21, int r22, int r23, int r24) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MessagesStorage.lambda$createTaskForSecretChat$112(int, java.util.ArrayList, int, int, int):void");
    }

    public void lambda$deleteAllStoryPushMessages$40() {
        try {
            this.database.executeFast("DELETE FROM story_pushes").stepThis().dispose();
        } catch (Exception e) {
            checkSQLException(e);
        }
    }

    public void lambda$deleteAllStoryReactionPushMessages$41() {
        try {
            this.database.executeFast("DELETE FROM unread_push_messages WHERE is_reaction = 2").stepThis().dispose();
        } catch (Exception e) {
            checkSQLException(e);
        }
    }

    public void lambda$deleteContacts$141(ArrayList arrayList) {
        try {
            String join = TextUtils.join(",", arrayList);
            this.database.executeFast("DELETE FROM contacts WHERE uid IN(" + join + ")").stepThis().dispose();
        } catch (Exception e) {
            checkSQLException(e);
        }
    }

    public void lambda$deleteDialog$83(ArrayList arrayList) {
        getFileLoader().cancelLoadFiles(arrayList);
    }

    public void lambda$deleteDialog$84() {
        getNotificationCenter().lambda$postNotificationNameOnUIThread$1(NotificationCenter.needReloadRecentDialogsSearch, new Object[0]);
    }

    public void lambda$deleteDialog$85(int r28, long r29) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MessagesStorage.lambda$deleteDialog$85(int, long):void");
    }

    public void lambda$deleteFromDownloadQueue$176(ArrayList arrayList) {
        getDownloadController().cancelDownloading(arrayList);
    }

    public void lambda$deleteSavedDialog$52(long j, ArrayList arrayList) {
        getMessagesController().markDialogMessageAsDeleted(j, arrayList);
        getNotificationCenter().lambda$postNotificationNameOnUIThread$1(NotificationCenter.messagesDeleted, arrayList, 0L, Boolean.FALSE);
    }

    public void lambda$deleteSavedDialog$53(long j) {
        final long clientUserId;
        SQLiteCursor queryFinalized;
        SQLiteCursor sQLiteCursor = null;
        try {
            try {
                clientUserId = getUserConfig().getClientUserId();
                queryFinalized = this.database.queryFinalized("SELECT mid FROM messages_topics WHERE uid = ? AND topic_id = ?", Long.valueOf(clientUserId), Long.valueOf(j));
            } catch (Exception e) {
                e = e;
            }
        } catch (Throwable th) {
            th = th;
        }
        try {
            final ArrayList<Integer> arrayList = new ArrayList<>();
            while (queryFinalized.next()) {
                arrayList.add(Integer.valueOf(queryFinalized.intValue(0)));
            }
            queryFinalized.dispose();
            queryFinalized = this.database.queryFinalized("SELECT mid, data FROM messages_v2 WHERE uid = ?", Long.valueOf(clientUserId));
            while (queryFinalized.next()) {
                int intValue = queryFinalized.intValue(0);
                if (!arrayList.contains(Integer.valueOf(intValue))) {
                    NativeByteBuffer byteBufferValue = queryFinalized.byteBufferValue(1);
                    if (MessageObject.getSavedDialogId(clientUserId, TLRPC.Message.TLdeserialize(byteBufferValue, byteBufferValue.readInt32(false), false)) == j) {
                        arrayList.add(Integer.valueOf(intValue));
                    }
                    byteBufferValue.reuse();
                }
            }
            queryFinalized.dispose();
            if (arrayList.isEmpty()) {
                return;
            }
            lambda$markMessagesAsDeleted$210(clientUserId, arrayList, true, 0, 0);
            updateDialogsWithDeletedMessages(clientUserId, -clientUserId, arrayList, null, false);
            AndroidUtilities.runOnUIThread(new Runnable() {
                @Override
                public final void run() {
                    MessagesStorage.this.lambda$deleteSavedDialog$52(clientUserId, arrayList);
                }
            });
        } catch (Exception e2) {
            e = e2;
            sQLiteCursor = queryFinalized;
            checkSQLException(e);
            if (sQLiteCursor != null) {
                sQLiteCursor.dispose();
            }
        } catch (Throwable th2) {
            th = th2;
            sQLiteCursor = queryFinalized;
            if (sQLiteCursor != null) {
                sQLiteCursor.dispose();
            }
            throw th;
        }
    }

    public void lambda$deleteStoryPushMessage$39(long j) {
        try {
            this.database.executeFast("DELETE FROM story_pushes WHERE uid = " + j).stepThis().dispose();
        } catch (Exception e) {
            checkSQLException(e);
        }
    }

    public void lambda$deleteUserChatHistory$80(ArrayList arrayList, long j, ArrayList arrayList2) {
        getFileLoader().cancelLoadFiles(arrayList);
        getMessagesController().markDialogMessageAsDeleted(j, arrayList2);
    }

    public void lambda$deleteUserChatHistory$81(ArrayList arrayList, long j) {
        getNotificationCenter().lambda$postNotificationNameOnUIThread$1(NotificationCenter.messagesDeleted, arrayList, Long.valueOf(DialogObject.isChatDialog(j) ? -j : 0L), Boolean.FALSE);
    }

    public void lambda$deleteUserChatHistory$82(final long r18, long r20) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MessagesStorage.lambda$deleteUserChatHistory$82(long, long):void");
    }

    public void lambda$deleteWallpaper$76(long j) {
        try {
            this.database.executeFast("DELETE FROM wallpapers2 WHERE uid = " + j).stepThis().dispose();
        } catch (Exception e) {
            checkSQLException(e);
        }
    }

    public void lambda$emptyMessagesMedia$91(ArrayList arrayList) {
        for (int i = 0; i < arrayList.size(); i++) {
            getNotificationCenter().lambda$postNotificationNameOnUIThread$1(NotificationCenter.updateMessageMedia, arrayList.get(i));
        }
    }

    public void lambda$emptyMessagesMedia$92(ArrayList arrayList) {
        getFileLoader().cancelLoadFiles(arrayList);
    }

    private void lambda$emptyMessagesMedia$93(ArrayList arrayList) {
        if (getMessagesController().getSavedMessagesController().updateSavedDialogs(arrayList)) {
            getMessagesController().getSavedMessagesController().update();
        }
    }

    public void lambda$emptyMessagesMedia$94(java.util.ArrayList r18, long r19) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MessagesStorage.lambda$emptyMessagesMedia$94(java.util.ArrayList, long):void");
    }

    public void lambda$fixNotificationSettings$9() {
        try {
            LongSparseArray longSparseArray = new LongSparseArray();
            Map<String, ?> all = MessagesController.getNotificationsSettings(this.currentAccount).getAll();
            for (Map.Entry<String, ?> entry : all.entrySet()) {
                String key = entry.getKey();
                if (key.startsWith("notify2_")) {
                    Integer num = (Integer) entry.getValue();
                    if (num.intValue() != 2 && num.intValue() != 3) {
                    }
                    String replace = key.replace("notify2_", "");
                    long j = 1;
                    if (num.intValue() != 2) {
                        if (((Integer) all.get("notifyuntil_" + replace)) != null) {
                            j = 1 | (r4.intValue() << 32);
                        }
                    }
                    try {
                        longSparseArray.put(Long.parseLong(replace), Long.valueOf(j));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
            try {
                this.database.beginTransaction();
                SQLitePreparedStatement executeFast = this.database.executeFast("REPLACE INTO dialog_settings VALUES(?, ?)");
                for (int i = 0; i < longSparseArray.size(); i++) {
                    executeFast.requery();
                    executeFast.bindLong(1, longSparseArray.keyAt(i));
                    executeFast.bindLong(2, ((Long) longSparseArray.valueAt(i)).longValue());
                    executeFast.step();
                }
                executeFast.dispose();
                this.database.commitTransaction();
            } catch (Exception e2) {
                checkSQLException(e2);
            }
        } catch (Throwable th) {
            checkSQLException(th);
        }
    }

    public void lambda$fullReset$59() {
        getNotificationCenter().lambda$postNotificationNameOnUIThread$1(NotificationCenter.onDatabaseReset, new Object[0]);
        getNotificationCenter().lambda$postNotificationNameOnUIThread$1(NotificationCenter.didClearDatabase, new Object[0]);
        getMessagesController().getSavedMessagesController().cleanup();
    }

    public void lambda$fullReset$60() {
        cleanupInternal(true);
        clearLoadingDialogsOffsets();
        openDatabase(1);
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                MessagesStorage.this.lambda$fullReset$59();
            }
        });
    }

    public void lambda$getBotCache$121(int r6, java.lang.String r7, org.telegram.tgnet.RequestDelegate r8) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MessagesStorage.lambda$getBotCache$121(int, java.lang.String, org.telegram.tgnet.RequestDelegate):void");
    }

    public void lambda$getCachedPhoneBook$144(boolean r25) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MessagesStorage.lambda$getCachedPhoneBook$144(boolean):void");
    }

    public void lambda$getChannelPtsSync$238(long r5, java.lang.Integer[] r7, java.util.concurrent.CountDownLatch r8) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MessagesStorage.lambda$getChannelPtsSync$238(long, java.lang.Integer[], java.util.concurrent.CountDownLatch):void");
    }

    public void lambda$getChatSync$240(TLRPC.Chat[] chatArr, long j, CountDownLatch countDownLatch) {
        chatArr[0] = getChat(j);
        countDownLatch.countDown();
    }

    public void lambda$getContacts$145() {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MessagesStorage.lambda$getContacts$145():void");
    }

    public void lambda$getDialogFolderId$224(long j, final IntCallback intCallback) {
        SQLiteCursor sQLiteCursor = null;
        try {
            try {
                if (this.unknownDialogsIds.get(j) == null) {
                    sQLiteCursor = this.database.queryFinalized("SELECT folder_id FROM dialogs WHERE did = ?", Long.valueOf(j));
                    r3 = sQLiteCursor.next() ? sQLiteCursor.intValue(0) : -1;
                    sQLiteCursor.dispose();
                }
                AndroidUtilities.runOnUIThread(new Runnable() {
                    @Override
                    public final void run() {
                        MessagesStorage.IntCallback.this.run(r2);
                    }
                });
                if (sQLiteCursor == null) {
                    return;
                }
            } catch (Exception e) {
                checkSQLException(e);
                if (sQLiteCursor == null) {
                    return;
                }
            }
            sQLiteCursor.dispose();
        } catch (Throwable th) {
            if (sQLiteCursor != null) {
                sQLiteCursor.dispose();
            }
            throw th;
        }
    }

    public static void lambda$getDialogMaxMessageId$235(IntCallback intCallback, int[] iArr) {
        intCallback.run(iArr[0]);
    }

    public void lambda$getDialogMaxMessageId$236(long r6, final org.telegram.messenger.MessagesStorage.IntCallback r8) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MessagesStorage.lambda$getDialogMaxMessageId$236(long, org.telegram.messenger.MessagesStorage$IntCallback):void");
    }

    public void lambda$getDialogReadMax$237(boolean r5, long r6, java.lang.Integer[] r8, java.util.concurrent.CountDownLatch r9) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MessagesStorage.lambda$getDialogReadMax$237(boolean, long, java.lang.Integer[], java.util.concurrent.CountDownLatch):void");
    }

    public void lambda$getDialogs$220(LongSparseArray longSparseArray) {
        MediaDataController mediaDataController = getMediaDataController();
        mediaDataController.clearDraftsFolderIds();
        if (longSparseArray != null) {
            int size = longSparseArray.size();
            for (int i = 0; i < size; i++) {
                mediaDataController.setDraftFolderId(longSparseArray.keyAt(i), ((Integer) longSparseArray.valueAt(i)).intValue());
            }
        }
    }

    public void lambda$getDialogs$221(int r35, int r36, int r37, long[] r38) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MessagesStorage.lambda$getDialogs$221(int, int, int, long[]):void");
    }

    public void lambda$getDownloadQueue$178(int i, ArrayList arrayList) {
        getDownloadController().processDownloadObjects(i, arrayList);
    }

    public void lambda$getDownloadQueue$179(final int r11) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MessagesStorage.lambda$getDownloadQueue$179(int):void");
    }

    public void lambda$getEncryptedChat$170(long j, ArrayList arrayList, CountDownLatch countDownLatch) {
        try {
            try {
                ArrayList<Long> arrayList2 = new ArrayList<>();
                ArrayList<TLRPC.EncryptedChat> arrayList3 = new ArrayList<>();
                getEncryptedChatsInternal("" + j, arrayList3, arrayList2);
                if (!arrayList3.isEmpty() && !arrayList2.isEmpty()) {
                    ArrayList<TLRPC.User> arrayList4 = new ArrayList<>();
                    getUsersInternal(arrayList2, arrayList4);
                    if (!arrayList4.isEmpty()) {
                        arrayList.add(arrayList3.get(0));
                        arrayList.add(arrayList4.get(0));
                    }
                }
            } catch (Exception e) {
                checkSQLException(e);
            }
        } finally {
            countDownLatch.countDown();
        }
    }

    public void lambda$getMessage$136(long j, long j2, AtomicReference atomicReference, CountDownLatch countDownLatch) {
        SQLiteCursor sQLiteCursor = null;
        try {
            try {
                sQLiteCursor = this.database.queryFinalized("SELECT data FROM messages_v2 WHERE uid = " + j + " AND mid = " + j2 + " LIMIT 1", new Object[0]);
                while (sQLiteCursor.next()) {
                    NativeByteBuffer byteBufferValue = sQLiteCursor.byteBufferValue(0);
                    if (byteBufferValue != null) {
                        TLRPC.Message TLdeserialize = TLRPC.Message.TLdeserialize(byteBufferValue, byteBufferValue.readInt32(false), false);
                        byteBufferValue.reuse();
                        atomicReference.set(TLdeserialize);
                    }
                }
                sQLiteCursor.dispose();
            } catch (Exception e) {
                checkSQLException(e);
                if (sQLiteCursor != null) {
                    sQLiteCursor.dispose();
                }
            }
            countDownLatch.countDown();
        } catch (Throwable th) {
            if (sQLiteCursor != null) {
                sQLiteCursor.dispose();
            }
            countDownLatch.countDown();
            throw th;
        }
    }

    public static void lambda$getMessages$155(Timer.Task task, Runnable runnable) {
        Timer.done(task);
        runnable.run();
    }

    public void lambda$getMessages$156(Timer.Task task, Timer timer, long j, long j2, int i, int i2, int i3, int i4, int i5, int i6, int i7, long j3, int i8, boolean z, boolean z2) {
        Timer.done(task);
        Timer.Task start = Timer.start(timer, "MessagesStorage.getMessages");
        final Runnable messagesInternal = getMessagesInternal(j, j2, i, i2, i3, i4, i5, i6, i7, j3, i8, z, z2, timer);
        Timer.done(start);
        final Timer.Task start2 = Timer.start(timer, "MessagesStorage.getMessages: stageQueue.postRunnable");
        Utilities.stageQueue.postRunnable(new Runnable() {
            @Override
            public final void run() {
                MessagesStorage.lambda$getMessages$155(Timer.Task.this, messagesInternal);
            }
        });
    }

    public void lambda$getMessagesCount$152(long j, final IntCallback intCallback) {
        SQLiteCursor sQLiteCursor = null;
        try {
            try {
                sQLiteCursor = this.database.queryFinalized(String.format(Locale.US, "SELECT COUNT(mid) FROM messages_v2 WHERE uid = %d", Long.valueOf(j)), new Object[0]);
                final int intValue = sQLiteCursor.next() ? sQLiteCursor.intValue(0) : 0;
                sQLiteCursor.dispose();
                AndroidUtilities.runOnUIThread(new Runnable() {
                    @Override
                    public final void run() {
                        MessagesStorage.IntCallback.this.run(intValue);
                    }
                });
            } catch (Exception e) {
                checkSQLException(e);
                if (sQLiteCursor == null) {
                    return;
                }
            }
            sQLiteCursor.dispose();
        } catch (Throwable th) {
            if (sQLiteCursor != null) {
                sQLiteCursor.dispose();
            }
            throw th;
        }
    }

    public static int lambda$getMessagesInternal$153(TLRPC.Message message, TLRPC.Message message2) {
        int i;
        int i2;
        int i3 = message.id;
        if (i3 > 0 && (i2 = message2.id) > 0) {
            if (i3 > i2) {
                return -1;
            }
            return i3 < i2 ? 1 : 0;
        }
        if (i3 < 0 && (i = message2.id) < 0) {
            if (i3 < i) {
                return -1;
            }
            return i3 > i ? 1 : 0;
        }
        int i4 = message.date;
        int i5 = message2.date;
        if (i4 > i5) {
            return -1;
        }
        return i4 < i5 ? 1 : 0;
    }

    public void lambda$getMessagesInternal$154(TLRPC.TL_messages_messages tL_messages_messages, int i, long j, long j2, int i2, int i3, int i4, int i5, int i6, int i7, int i8, int i9, int i10, boolean z, int i11, long j3, int i12, boolean z2, int i13, boolean z3, boolean z4, Timer timer) {
        getMessagesController().processLoadedMessages(tL_messages_messages, i, j, j2, i2, i3, i4, true, i5, i6, i7, i8, i9, i10, z, i11, j3, i12, z2, i13, z3, z4, timer);
    }

    public void lambda$getNewTask$105(androidx.collection.LongSparseArray r14, androidx.collection.LongSparseArray r15) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MessagesStorage.lambda$getNewTask$105(androidx.collection.LongSparseArray, androidx.collection.LongSparseArray):void");
    }

    public static void lambda$getSavedDialogMaxMessageId$50(IntCallback intCallback, int[] iArr) {
        intCallback.run(iArr[0]);
    }

    public void lambda$getSavedDialogMaxMessageId$51(long r9, final org.telegram.messenger.MessagesStorage.IntCallback r11) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MessagesStorage.lambda$getSavedDialogMaxMessageId$51(long, org.telegram.messenger.MessagesStorage$IntCallback):void");
    }

    public void lambda$getSentFile$158(String str, int i, Object[] objArr, CountDownLatch countDownLatch) {
        NativeByteBuffer byteBufferValue;
        try {
            try {
                String MD5 = Utilities.MD5(str);
                if (MD5 != null) {
                    SQLiteCursor queryFinalized = this.database.queryFinalized(String.format(Locale.US, "SELECT data, parent FROM sent_files_v2 WHERE uid = '%s' AND type = %d", MD5, Integer.valueOf(i)), new Object[0]);
                    if (queryFinalized.next() && (byteBufferValue = queryFinalized.byteBufferValue(0)) != null) {
                        TLRPC.MessageMedia TLdeserialize = TLRPC.MessageMedia.TLdeserialize(byteBufferValue, byteBufferValue.readInt32(false), false);
                        byteBufferValue.reuse();
                        if (TLdeserialize instanceof TLRPC.TL_messageMediaDocument) {
                            objArr[0] = ((TLRPC.TL_messageMediaDocument) TLdeserialize).document;
                        } else if (TLdeserialize instanceof TLRPC.TL_messageMediaPhoto) {
                            objArr[0] = ((TLRPC.TL_messageMediaPhoto) TLdeserialize).photo;
                        }
                        if (objArr[0] != null) {
                            objArr[1] = queryFinalized.stringValue(1);
                        }
                    }
                    queryFinalized.dispose();
                }
            } catch (Exception e) {
                checkSQLException(e);
            }
            countDownLatch.countDown();
        } catch (Throwable th) {
            countDownLatch.countDown();
            throw th;
        }
    }

    public void lambda$getUnreadMention$150(long j, long j2, final IntCallback intCallback) {
        SQLiteCursor sQLiteCursor = null;
        try {
            try {
                sQLiteCursor = j != 0 ? this.database.queryFinalized(String.format(Locale.US, "SELECT MIN(mid) FROM messages_topics WHERE uid = %d AND topic_id = %d AND mention = 1 AND read_state IN(0, 1)", Long.valueOf(j2), Long.valueOf(j)), new Object[0]) : this.database.queryFinalized(String.format(Locale.US, "SELECT MIN(mid) FROM messages_v2 WHERE uid = %d AND mention = 1 AND read_state IN(0, 1)", Long.valueOf(j2)), new Object[0]);
                final int intValue = sQLiteCursor.next() ? sQLiteCursor.intValue(0) : 0;
                sQLiteCursor.dispose();
                AndroidUtilities.runOnUIThread(new Runnable() {
                    @Override
                    public final void run() {
                        MessagesStorage.IntCallback.this.run(intValue);
                    }
                });
            } catch (Exception e) {
                checkSQLException(e);
                if (sQLiteCursor == null) {
                    return;
                }
            }
            sQLiteCursor.dispose();
        } catch (Throwable th) {
            if (sQLiteCursor != null) {
                sQLiteCursor.dispose();
            }
            throw th;
        }
    }

    public void lambda$getUnsentMessages$146(int r23) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MessagesStorage.lambda$getUnsentMessages$146(int):void");
    }

    public void lambda$getUserSync$239(TLRPC.User[] userArr, long j, CountDownLatch countDownLatch) {
        userArr[0] = getUser(j);
        countDownLatch.countDown();
    }

    public static void lambda$getWallpapers$77(ArrayList arrayList) {
        NotificationCenter.getGlobalInstance().lambda$postNotificationNameOnUIThread$1(NotificationCenter.wallpapersDidLoad, arrayList);
    }

    public void lambda$getWallpapers$78() {
        SQLiteCursor sQLiteCursor = null;
        try {
            try {
                sQLiteCursor = this.database.queryFinalized("SELECT data FROM wallpapers2 WHERE 1 ORDER BY num ASC", new Object[0]);
                final ArrayList arrayList = new ArrayList();
                while (sQLiteCursor.next()) {
                    NativeByteBuffer byteBufferValue = sQLiteCursor.byteBufferValue(0);
                    if (byteBufferValue != null) {
                        TLRPC.WallPaper TLdeserialize = TLRPC.WallPaper.TLdeserialize(byteBufferValue, byteBufferValue.readInt32(false), false);
                        byteBufferValue.reuse();
                        if (TLdeserialize != null) {
                            arrayList.add(TLdeserialize);
                        }
                    }
                }
                AndroidUtilities.runOnUIThread(new Runnable() {
                    @Override
                    public final void run() {
                        MessagesStorage.lambda$getWallpapers$77(arrayList);
                    }
                });
            } catch (Exception e) {
                checkSQLException(e);
                if (sQLiteCursor == null) {
                    return;
                }
            }
            sQLiteCursor.dispose();
        } catch (Throwable th) {
            if (sQLiteCursor != null) {
                sQLiteCursor.dispose();
            }
            throw th;
        }
    }

    public void lambda$getWidgetDialogIds$161(int i, ArrayList arrayList, ArrayList arrayList2, ArrayList arrayList3, boolean z, int i2, CountDownLatch countDownLatch) {
        Long valueOf;
        ArrayList<Long> arrayList4;
        Long valueOf2;
        ArrayList<Long> arrayList5;
        Long valueOf3;
        ArrayList<Long> arrayList6;
        SQLiteCursor sQLiteCursor = null;
        try {
            try {
                ArrayList<Long> arrayList7 = new ArrayList<>();
                ArrayList<Long> arrayList8 = new ArrayList<>();
                SQLiteCursor queryFinalized = this.database.queryFinalized(String.format(Locale.US, "SELECT did FROM shortcut_widget WHERE id = %d ORDER BY ord ASC", Integer.valueOf(i)), new Object[0]);
                while (queryFinalized.next()) {
                    try {
                        long longValue = queryFinalized.longValue(0);
                        if (longValue != -1) {
                            arrayList.add(Long.valueOf(longValue));
                            if (arrayList2 != null && arrayList3 != null) {
                                if (DialogObject.isUserDialog(longValue)) {
                                    valueOf3 = Long.valueOf(longValue);
                                    arrayList6 = arrayList7;
                                } else {
                                    valueOf3 = Long.valueOf(-longValue);
                                    arrayList6 = arrayList8;
                                }
                                arrayList6.add(valueOf3);
                            }
                        }
                    } catch (Exception e) {
                        e = e;
                        sQLiteCursor = queryFinalized;
                        checkSQLException(e);
                        if (sQLiteCursor != null) {
                            sQLiteCursor.dispose();
                        }
                        countDownLatch.countDown();
                    } catch (Throwable th) {
                        th = th;
                        sQLiteCursor = queryFinalized;
                        if (sQLiteCursor != null) {
                            sQLiteCursor.dispose();
                        }
                        countDownLatch.countDown();
                        throw th;
                    }
                }
                queryFinalized.dispose();
                if (!z && arrayList.isEmpty()) {
                    if (i2 == 0) {
                        queryFinalized = this.database.queryFinalized("SELECT did FROM dialogs WHERE folder_id = 0 ORDER BY pinned DESC, date DESC LIMIT 0,10", new Object[0]);
                        while (queryFinalized.next()) {
                            long longValue2 = queryFinalized.longValue(0);
                            if (!DialogObject.isFolderDialogId(longValue2)) {
                                arrayList.add(Long.valueOf(longValue2));
                                if (arrayList2 != null && arrayList3 != null) {
                                    if (DialogObject.isUserDialog(longValue2)) {
                                        valueOf2 = Long.valueOf(longValue2);
                                        arrayList5 = arrayList7;
                                    } else {
                                        valueOf2 = Long.valueOf(-longValue2);
                                        arrayList5 = arrayList8;
                                    }
                                    arrayList5.add(valueOf2);
                                }
                            }
                        }
                    } else {
                        queryFinalized = getMessagesStorage().getDatabase().queryFinalized("SELECT did FROM chat_hints WHERE type = 0 ORDER BY rating DESC LIMIT 4", new Object[0]);
                        while (queryFinalized.next()) {
                            long longValue3 = queryFinalized.longValue(0);
                            arrayList.add(Long.valueOf(longValue3));
                            if (arrayList2 != null && arrayList3 != null) {
                                if (DialogObject.isUserDialog(longValue3)) {
                                    valueOf = Long.valueOf(longValue3);
                                    arrayList4 = arrayList7;
                                } else {
                                    valueOf = Long.valueOf(-longValue3);
                                    arrayList4 = arrayList8;
                                }
                                arrayList4.add(valueOf);
                            }
                        }
                    }
                    queryFinalized.dispose();
                }
                if (arrayList2 != null && arrayList3 != null) {
                    if (!arrayList8.isEmpty()) {
                        getChatsInternal(TextUtils.join(",", arrayList8), arrayList3);
                    }
                    if (!arrayList7.isEmpty()) {
                        getUsersInternal(arrayList7, (ArrayList<TLRPC.User>) arrayList2);
                    }
                }
            } catch (Exception e2) {
                e = e2;
            }
            countDownLatch.countDown();
        } catch (Throwable th2) {
            th = th2;
        }
    }

    public void lambda$getWidgetDialogs$162(int i, ArrayList arrayList, int i2, LongSparseArray longSparseArray, LongSparseArray longSparseArray2, ArrayList arrayList2, ArrayList arrayList3, CountDownLatch countDownLatch) {
        boolean z;
        Long valueOf;
        ArrayList<Long> arrayList4;
        Long valueOf2;
        ArrayList<Long> arrayList5;
        SQLiteCursor sQLiteCursor = null;
        try {
            try {
                ArrayList<Long> arrayList6 = new ArrayList<>();
                ArrayList<Long> arrayList7 = new ArrayList<>();
                SQLiteCursor queryFinalized = this.database.queryFinalized(String.format(Locale.US, "SELECT did FROM shortcut_widget WHERE id = %d ORDER BY ord ASC", Integer.valueOf(i)), new Object[0]);
                while (queryFinalized.next()) {
                    try {
                        long longValue = queryFinalized.longValue(0);
                        if (longValue != -1) {
                            arrayList.add(Long.valueOf(longValue));
                            if (DialogObject.isUserDialog(longValue)) {
                                valueOf2 = Long.valueOf(longValue);
                                arrayList5 = arrayList6;
                            } else {
                                valueOf2 = Long.valueOf(-longValue);
                                arrayList5 = arrayList7;
                            }
                            arrayList5.add(valueOf2);
                        }
                    } catch (Exception e) {
                        e = e;
                        sQLiteCursor = queryFinalized;
                        checkSQLException(e);
                        if (sQLiteCursor != null) {
                            sQLiteCursor.dispose();
                        }
                        countDownLatch.countDown();
                    } catch (Throwable th) {
                        th = th;
                        sQLiteCursor = queryFinalized;
                        if (sQLiteCursor != null) {
                            sQLiteCursor.dispose();
                        }
                        countDownLatch.countDown();
                        throw th;
                    }
                }
                queryFinalized.dispose();
                if (arrayList.isEmpty() && i2 == 1) {
                    SQLiteCursor queryFinalized2 = getMessagesStorage().getDatabase().queryFinalized("SELECT did FROM chat_hints WHERE type = 0 ORDER BY rating DESC LIMIT 4", new Object[0]);
                    while (queryFinalized2.next()) {
                        long longValue2 = queryFinalized2.longValue(0);
                        arrayList.add(Long.valueOf(longValue2));
                        if (DialogObject.isUserDialog(longValue2)) {
                            valueOf = Long.valueOf(longValue2);
                            arrayList4 = arrayList6;
                        } else {
                            valueOf = Long.valueOf(-longValue2);
                            arrayList4 = arrayList7;
                        }
                        arrayList4.add(valueOf);
                    }
                    queryFinalized2.dispose();
                }
                if (arrayList.isEmpty()) {
                    queryFinalized = this.database.queryFinalized("SELECT d.did, d.last_mid, d.unread_count, d.date, m.data, m.read_state, m.mid, m.send_state, m.date FROM dialogs as d LEFT JOIN messages_v2 as m ON d.last_mid = m.mid AND d.did = m.uid WHERE d.folder_id = 0 ORDER BY d.pinned DESC, d.date DESC LIMIT 0,10", new Object[0]);
                    z = true;
                } else {
                    queryFinalized = this.database.queryFinalized(String.format(Locale.US, "SELECT d.did, d.last_mid, d.unread_count, d.date, m.data, m.read_state, m.mid, m.send_state, m.date FROM dialogs as d LEFT JOIN messages_v2 as m ON d.last_mid = m.mid AND d.did = m.uid WHERE d.did IN(%s)", TextUtils.join(",", arrayList)), new Object[0]);
                    z = false;
                }
                while (queryFinalized.next()) {
                    long longValue3 = queryFinalized.longValue(0);
                    if (!DialogObject.isFolderDialogId(longValue3)) {
                        if (z) {
                            arrayList.add(Long.valueOf(longValue3));
                        }
                        TLRPC.TL_dialog tL_dialog = new TLRPC.TL_dialog();
                        tL_dialog.id = longValue3;
                        tL_dialog.top_message = queryFinalized.intValue(1);
                        tL_dialog.unread_count = queryFinalized.intValue(2);
                        tL_dialog.last_message_date = queryFinalized.intValue(3);
                        longSparseArray.put(tL_dialog.id, tL_dialog);
                        NativeByteBuffer byteBufferValue = queryFinalized.byteBufferValue(4);
                        if (byteBufferValue != null) {
                            TLRPC.Message TLdeserialize = TLRPC.Message.TLdeserialize(byteBufferValue, byteBufferValue.readInt32(false), false);
                            TLdeserialize.readAttachPath(byteBufferValue, getUserConfig().clientUserId);
                            byteBufferValue.reuse();
                            MessageObject.setUnreadFlags(TLdeserialize, queryFinalized.intValue(5));
                            TLdeserialize.id = queryFinalized.intValue(6);
                            TLdeserialize.send_state = queryFinalized.intValue(7);
                            int intValue = queryFinalized.intValue(8);
                            if (intValue != 0) {
                                tL_dialog.last_message_date = intValue;
                            }
                            long j = tL_dialog.id;
                            TLdeserialize.dialog_id = j;
                            longSparseArray2.put(j, TLdeserialize);
                            addUsersAndChatsFromMessage(TLdeserialize, arrayList6, arrayList7, null);
                        }
                    }
                }
                queryFinalized.dispose();
                if (!z && arrayList.size() > longSparseArray.size()) {
                    int size = arrayList.size();
                    for (int i3 = 0; i3 < size; i3++) {
                        Long l = (Long) arrayList.get(i3);
                        long longValue4 = l.longValue();
                        if (longSparseArray.get(((Long) arrayList.get(i3)).longValue()) == null) {
                            TLRPC.TL_dialog tL_dialog2 = new TLRPC.TL_dialog();
                            tL_dialog2.id = longValue4;
                            longSparseArray.put(longValue4, tL_dialog2);
                            if (DialogObject.isChatDialog(longValue4)) {
                                long j2 = -longValue4;
                                if (arrayList7.contains(Long.valueOf(j2))) {
                                    arrayList7.add(Long.valueOf(j2));
                                }
                            } else if (arrayList6.contains(l)) {
                                arrayList6.add(l);
                            }
                        }
                    }
                }
                if (!arrayList7.isEmpty()) {
                    getChatsInternal(TextUtils.join(",", arrayList7), arrayList2);
                }
                if (!arrayList6.isEmpty()) {
                    getUsersInternal(arrayList6, (ArrayList<TLRPC.User>) arrayList3);
                }
            } catch (Throwable th2) {
                th = th2;
            }
        } catch (Exception e2) {
            e = e2;
        }
        countDownLatch.countDown();
    }

    public void lambda$hasAuthMessage$169(int r7, boolean[] r8, java.util.concurrent.CountDownLatch r9) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MessagesStorage.lambda$hasAuthMessage$169(int, boolean[], java.util.concurrent.CountDownLatch):void");
    }

    public void lambda$hasInviteMeMessage$137(long j, boolean[] zArr, CountDownLatch countDownLatch) {
        SQLiteCursor sQLiteCursor = null;
        try {
            try {
                long clientUserId = getUserConfig().getClientUserId();
                sQLiteCursor = this.database.queryFinalized("SELECT data FROM messages_v2 WHERE uid = " + (-j) + " AND out = 0 ORDER BY mid DESC LIMIT 100", new Object[0]);
                while (true) {
                    if (!sQLiteCursor.next()) {
                        break;
                    }
                    NativeByteBuffer byteBufferValue = sQLiteCursor.byteBufferValue(0);
                    if (byteBufferValue != null) {
                        TLRPC.Message TLdeserialize = TLRPC.Message.TLdeserialize(byteBufferValue, byteBufferValue.readInt32(false), false);
                        byteBufferValue.reuse();
                        TLRPC.MessageAction messageAction = TLdeserialize.action;
                        if ((messageAction instanceof TLRPC.TL_messageActionChatAddUser) && messageAction.users.contains(Long.valueOf(clientUserId))) {
                            zArr[0] = true;
                            break;
                        }
                    }
                }
                sQLiteCursor.dispose();
            } catch (Exception e) {
                checkSQLException(e);
                if (sQLiteCursor != null) {
                    sQLiteCursor.dispose();
                }
            }
            countDownLatch.countDown();
        } catch (Throwable th) {
            if (sQLiteCursor != null) {
                sQLiteCursor.dispose();
            }
            countDownLatch.countDown();
            throw th;
        }
    }

    public void lambda$isDialogHasTopMessage$168(long r7, java.lang.Runnable r9) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MessagesStorage.lambda$isDialogHasTopMessage$168(long, java.lang.Runnable):void");
    }

    public void lambda$isMigratedChat$135(long j, boolean[] zArr, CountDownLatch countDownLatch) {
        SQLiteCursor queryFinalized;
        TLRPC.ChatFull chatFull;
        NativeByteBuffer byteBufferValue;
        SQLiteCursor sQLiteCursor = null;
        try {
            try {
                queryFinalized = this.database.queryFinalized("SELECT info FROM chat_settings_v2 WHERE uid = " + j, new Object[0]);
            } catch (Throwable th) {
                th = th;
            }
        } catch (Exception e) {
            e = e;
        }
        try {
            new ArrayList();
            if (!queryFinalized.next() || (byteBufferValue = queryFinalized.byteBufferValue(0)) == null) {
                chatFull = null;
            } else {
                chatFull = TLRPC.ChatFull.TLdeserialize(byteBufferValue, byteBufferValue.readInt32(false), false);
                byteBufferValue.reuse();
            }
            queryFinalized.dispose();
            zArr[0] = (chatFull instanceof TLRPC.TL_channelFull) && chatFull.migrated_from_chat_id != 0;
            countDownLatch.countDown();
        } catch (Exception e2) {
            e = e2;
            sQLiteCursor = queryFinalized;
            checkSQLException(e);
            if (sQLiteCursor != null) {
                sQLiteCursor.dispose();
            }
            countDownLatch.countDown();
        } catch (Throwable th2) {
            th = th2;
            sQLiteCursor = queryFinalized;
            if (sQLiteCursor != null) {
                sQLiteCursor.dispose();
            }
            countDownLatch.countDown();
            throw th;
        }
        countDownLatch.countDown();
    }

    public void lambda$loadChannelAdmins$117(long j) {
        SQLiteCursor queryFinalized;
        SQLiteCursor sQLiteCursor = null;
        try {
            try {
                queryFinalized = this.database.queryFinalized("SELECT uid, data FROM channel_admins_v3 WHERE did = " + j, new Object[0]);
            } catch (Throwable th) {
                th = th;
            }
        } catch (Exception e) {
            e = e;
        }
        try {
            LongSparseArray longSparseArray = new LongSparseArray();
            while (queryFinalized.next()) {
                NativeByteBuffer byteBufferValue = queryFinalized.byteBufferValue(1);
                if (byteBufferValue != null) {
                    TLRPC.ChannelParticipant TLdeserialize = TLRPC.ChannelParticipant.TLdeserialize(byteBufferValue, byteBufferValue.readInt32(false), false);
                    byteBufferValue.reuse();
                    if (TLdeserialize != null) {
                        longSparseArray.put(queryFinalized.longValue(0), TLdeserialize);
                    }
                }
            }
            queryFinalized.dispose();
            getMessagesController().processLoadedChannelAdmins(longSparseArray, j, true);
        } catch (Exception e2) {
            e = e2;
            sQLiteCursor = queryFinalized;
            checkSQLException(e);
            if (sQLiteCursor != null) {
                sQLiteCursor.dispose();
            }
        } catch (Throwable th2) {
            th = th2;
            sQLiteCursor = queryFinalized;
            if (sQLiteCursor != null) {
                sQLiteCursor.dispose();
            }
            throw th;
        }
    }

    public void lambda$loadChatInfo$138(TLRPC.ChatFull[] chatFullArr, long j, boolean z, boolean z2, boolean z3, int i, CountDownLatch countDownLatch) {
        chatFullArr[0] = loadChatInfoInternal(j, z, z2, z3, i);
        if (countDownLatch != null) {
            countDownLatch.countDown();
        }
    }

    public static int lambda$loadDialogFilters$63(MessagesController.DialogFilter dialogFilter, MessagesController.DialogFilter dialogFilter2) {
        int i = dialogFilter.order;
        int i2 = dialogFilter2.order;
        if (i > i2) {
            return 1;
        }
        return i < i2 ? -1 : 0;
    }

    public void lambda$loadDialogFilters$64() {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MessagesStorage.lambda$loadDialogFilters$64():void");
    }

    public void lambda$loadMessageAttachPaths$216(ArrayList arrayList, Runnable runnable) {
        NativeByteBuffer byteBufferValue;
        long clientUserId = getUserConfig().getClientUserId();
        Iterator it = arrayList.iterator();
        while (it.hasNext()) {
            MessageObject messageObject = (MessageObject) it.next();
            if (!messageObject.scheduled && !messageObject.isQuickReply()) {
                SQLiteCursor sQLiteCursor = null;
                try {
                    try {
                        sQLiteCursor = this.database.queryFinalized("SELECT data FROM messages_v2 WHERE uid = ? AND mid = ?", Long.valueOf(messageObject.getDialogId()), Integer.valueOf(messageObject.getId()));
                        if (sQLiteCursor.next() && (byteBufferValue = sQLiteCursor.byteBufferValue(0)) != null) {
                            TLRPC.Message TLdeserialize = TLRPC.Message.TLdeserialize(byteBufferValue, byteBufferValue.readInt32(false), false);
                            TLdeserialize.readAttachPath(byteBufferValue, clientUserId);
                            byteBufferValue.reuse();
                            messageObject.messageOwner.attachPath = TLdeserialize.attachPath;
                            messageObject.checkMediaExistance();
                        }
                    } catch (Exception e) {
                        FileLog.e(e);
                        if (sQLiteCursor != null) {
                        }
                    }
                    sQLiteCursor.dispose();
                } catch (Throwable th) {
                    if (sQLiteCursor != null) {
                        sQLiteCursor.dispose();
                    }
                    throw th;
                }
            }
        }
        AndroidUtilities.runOnUIThread(runnable);
    }

    public void lambda$loadPendingTasks$12(TLRPC.Chat chat, long j) {
        getMessagesController().loadUnknownChannel(chat, j);
    }

    public void lambda$loadPendingTasks$13(long j, int i, long j2) {
        getMessagesController().getChannelDifference(j, i, j2, null);
    }

    public void lambda$loadPendingTasks$14(TLRPC.Dialog dialog, TLRPC.InputPeer inputPeer, long j) {
        getMessagesController().checkLastDialogMessage(dialog, inputPeer, j);
    }

    public void lambda$loadPendingTasks$15(long j, boolean z, TLRPC.InputPeer inputPeer, long j2) {
        getMessagesController().pinDialog(j, z, inputPeer, j2);
    }

    public void lambda$loadPendingTasks$16(long j, int i, long j2, TLRPC.InputChannel inputChannel) {
        getMessagesController().getChannelDifference(j, i, j2, inputChannel);
    }

    public void lambda$loadPendingTasks$17(long j, int i, long j2, TLRPC.InputChannel inputChannel) {
        getMessagesController().getChannelDifference(j, i, j2, inputChannel);
    }

    public void lambda$loadPendingTasks$18(long j, long j2, TLObject tLObject) {
        getMessagesController().deleteMessages(null, null, null, -j, true, 0, false, j2, tLObject, 0);
    }

    public void lambda$loadPendingTasks$19(long j, long j2, TLObject tLObject) {
        getMessagesController().deleteMessages(null, null, null, j, true, 0, false, j2, tLObject, 0);
    }

    public void lambda$loadPendingTasks$20(long j, long j2, TLObject tLObject, int i) {
        getMessagesController().deleteMessages(null, null, null, j, true, 0, false, j2, tLObject, i);
    }

    public void lambda$loadPendingTasks$21(long j, TLRPC.InputPeer inputPeer, long j2) {
        getMessagesController().markDialogAsUnread(j, inputPeer, j2);
    }

    public void lambda$loadPendingTasks$22(long j, int i, TLRPC.InputChannel inputChannel, int i2, long j2) {
        getMessagesController().markMessageAsRead2(-j, i, inputChannel, i2, j2);
    }

    public void lambda$loadPendingTasks$23(long j, int i, TLRPC.InputChannel inputChannel, int i2, long j2, int i3) {
        getMessagesController().markMessageAsRead2(j, i, inputChannel, i2, j2, i3 == 23);
    }

    public void lambda$loadPendingTasks$24(Theme.OverrideWallpaperInfo overrideWallpaperInfo, boolean z, long j) {
        getMessagesController().saveWallpaperToServer(null, overrideWallpaperInfo, z, j);
    }

    public void lambda$loadPendingTasks$25(long j, boolean z, int i, int i2, boolean z2, TLRPC.InputPeer inputPeer, long j2) {
        getMessagesController().deleteDialog(j, z ? 1 : 0, i, i2, z2, inputPeer, j2);
    }

    public void lambda$loadPendingTasks$26(TLRPC.InputPeer inputPeer, long j) {
        getMessagesController().loadUnknownDialog(inputPeer, j);
    }

    public void lambda$loadPendingTasks$27(int i, ArrayList arrayList, long j) {
        getMessagesController().reorderPinnedDialogs(i, arrayList, j);
    }

    public void lambda$loadPendingTasks$28(int i, ArrayList arrayList, long j) {
        getMessagesController().addDialogToFolder(null, i, -1, arrayList, j);
    }

    public void lambda$loadPendingTasks$29(long j, long j2, TLObject tLObject) {
        getMessagesController().deleteMessages(null, null, null, j, true, 1, false, j2, tLObject, 0);
    }

    public void lambda$loadPendingTasks$30(TLRPC.InputPeer inputPeer, long j) {
        getMessagesController().reloadMentionsCountForChannel(inputPeer, j);
    }

    public void lambda$loadPendingTasks$31(int i, boolean z, long j) {
        getSecretChatHelper().declineSecretChat(i, z, j);
    }

    public void lambda$loadPendingTasks$32(long j, long j2, int i) {
        getMessagesController().lambda$checkDeletingTask$77(j, j2, i);
    }

    public void lambda$loadPendingTasks$33() {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MessagesStorage.lambda$loadPendingTasks$33():void");
    }

    public void lambda$loadTopics$48(ArrayList arrayList, ArrayList arrayList2) {
        if (!arrayList.isEmpty()) {
            getMessagesController().putUsers(arrayList, true);
        }
        if (arrayList2.isEmpty()) {
            return;
        }
        getMessagesController().putChats(arrayList2, true);
    }

    public void lambda$loadTopics$49(long r21, j$.util.function.Consumer r23) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MessagesStorage.lambda$loadTopics$49(long, j$.util.function.Consumer):void");
    }

    public void lambda$loadUnreadMessages$73(LongSparseArray longSparseArray, ArrayList arrayList, ArrayList arrayList2, ArrayList arrayList3, ArrayList arrayList4, ArrayList arrayList5, HashMap hashMap) {
        getNotificationsController().processLoadedUnreadMessages(longSparseArray, arrayList, arrayList2, arrayList3, arrayList4, arrayList5, hashMap.values());
    }

    public void lambda$loadUnreadMessages$74() {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MessagesStorage.lambda$loadUnreadMessages$74():void");
    }

    public void lambda$loadUserInfo$122(ArrayList arrayList) {
        getMessagesController().putChats(arrayList, true);
    }

    public void lambda$loadUserInfo$123(org.telegram.tgnet.TLRPC.User r20, boolean r21, int r22) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MessagesStorage.lambda$loadUserInfo$123(org.telegram.tgnet.TLRPC$User, boolean, int):void");
    }

    public static int lambda$localSearch$241(DialogsSearchAdapter.DialogSearchResult dialogSearchResult, DialogsSearchAdapter.DialogSearchResult dialogSearchResult2) {
        int i = dialogSearchResult.date;
        int i2 = dialogSearchResult2.date;
        if (i < i2) {
            return 1;
        }
        return i > i2 ? -1 : 0;
    }

    public void lambda$markMentionMessageAsRead$106(int i, long j, long j2) {
        SQLiteCursor sQLiteCursor = null;
        try {
            try {
                SQLiteDatabase sQLiteDatabase = this.database;
                Locale locale = Locale.US;
                sQLiteDatabase.executeFast(String.format(locale, "UPDATE messages_v2 SET read_state = read_state | 2 WHERE mid = %d AND uid = %d", Integer.valueOf(i), Long.valueOf(j))).stepThis().dispose();
                SQLiteCursor queryFinalized = this.database.queryFinalized("SELECT unread_count_i FROM dialogs WHERE did = " + j2, new Object[0]);
                try {
                    int max = queryFinalized.next() ? Math.max(0, queryFinalized.intValue(0) - 1) : 0;
                    queryFinalized.dispose();
                    this.database.executeFast(String.format(locale, "UPDATE dialogs SET unread_count_i = %d WHERE did = %d", Integer.valueOf(max), Long.valueOf(j2))).stepThis().dispose();
                    LongSparseIntArray longSparseIntArray = new LongSparseIntArray(1);
                    longSparseIntArray.put(j2, max);
                    if (max == 0) {
                        updateFiltersReadCounter(null, longSparseIntArray, true);
                    }
                    getMessagesController().processDialogsUpdateRead(null, longSparseIntArray);
                    this.database.executeFast(String.format(locale, "UPDATE messages_topics SET read_state = read_state | 2 WHERE mid = %d AND uid = %d", Integer.valueOf(i), Long.valueOf(j))).stepThis().dispose();
                    queryFinalized = this.database.queryFinalized(String.format(locale, "SELECT data FROM messages_topics WHERE mid = %d AND uid = %d", Integer.valueOf(i), Long.valueOf(j)), new Object[0]);
                    long j3 = 0;
                    while (queryFinalized.next()) {
                        NativeByteBuffer byteBufferValue = queryFinalized.byteBufferValue(0);
                        if (byteBufferValue != null) {
                            TLRPC.Message TLdeserialize = TLRPC.Message.TLdeserialize(byteBufferValue, byteBufferValue.readInt32(false), false);
                            byteBufferValue.reuse();
                            j3 = MessageObject.getTopicId(this.currentAccount, TLdeserialize, isForum(j));
                        }
                    }
                    queryFinalized.dispose();
                    if (j3 != 0) {
                        SQLiteDatabase sQLiteDatabase2 = this.database;
                        Locale locale2 = Locale.US;
                        SQLiteCursor queryFinalized2 = sQLiteDatabase2.queryFinalized(String.format(locale2, "SELECT unread_mentions FROM topics WHERE did = %d AND topic_id = %d", Long.valueOf(j2), Long.valueOf(j3)), new Object[0]);
                        try {
                            int max2 = queryFinalized2.next() ? Math.max(0, queryFinalized2.intValue(0) - 1) : 0;
                            queryFinalized2.dispose();
                            this.database.executeFast(String.format(locale2, "UPDATE topics SET unread_mentions = %d WHERE did = %d AND topic_id = %d", Integer.valueOf(max2), Long.valueOf(j), Long.valueOf(j3))).stepThis().dispose();
                            getMessagesController().getTopicsController().updateMentionsUnread(j, j3, max2);
                        } catch (Exception e) {
                            e = e;
                            sQLiteCursor = queryFinalized2;
                            checkSQLException(e);
                            if (sQLiteCursor != null) {
                                sQLiteCursor.dispose();
                            }
                        } catch (Throwable th) {
                            th = th;
                            sQLiteCursor = queryFinalized2;
                            if (sQLiteCursor != null) {
                                sQLiteCursor.dispose();
                            }
                            throw th;
                        }
                    }
                } catch (Exception e2) {
                    e = e2;
                    sQLiteCursor = queryFinalized;
                } catch (Throwable th2) {
                    th = th2;
                    sQLiteCursor = queryFinalized;
                }
            } catch (Exception e3) {
                e = e3;
            }
        } catch (Throwable th3) {
            th = th3;
        }
    }

    public void lambda$markMessageAsMention$107(int i, long j) {
        try {
            this.database.executeFast(String.format(Locale.US, "UPDATE messages_v2 SET mention = 1, read_state = read_state & ~2 WHERE mid = %d AND uid = %d", Integer.valueOf(i), Long.valueOf(j))).stepThis().dispose();
        } catch (Exception e) {
            checkSQLException(e);
        }
    }

    public void lambda$markMessageAsSendError$194(int i, TLRPC.Message message) {
        SQLitePreparedStatement executeFast;
        try {
            long j = message.id;
            if (MessageObject.isQuickReply(message)) {
                i = 5;
            }
            if (i == 5) {
                executeFast = this.database.executeFast(String.format(Locale.US, "UPDATE quick_replies_messages SET send_state = 2 WHERE mid = %d AND topic_id = %d", Long.valueOf(j), Integer.valueOf(MessageObject.getQuickReplyId(this.currentAccount, message))));
            } else if (i == 1) {
                executeFast = this.database.executeFast(String.format(Locale.US, "UPDATE scheduled_messages_v2 SET send_state = 2 WHERE mid = %d AND uid = %d", Long.valueOf(j), Long.valueOf(MessageObject.getDialogId(message))));
            } else {
                SQLiteDatabase sQLiteDatabase = this.database;
                Locale locale = Locale.US;
                sQLiteDatabase.executeFast(String.format(locale, "UPDATE messages_v2 SET send_state = 2 WHERE mid = %d AND uid = %d", Long.valueOf(j), Long.valueOf(MessageObject.getDialogId(message)))).stepThis().dispose();
                executeFast = this.database.executeFast(String.format(locale, "UPDATE messages_topics SET send_state = 2 WHERE mid = %d AND uid = %d", Long.valueOf(j), Long.valueOf(MessageObject.getDialogId(message))));
            }
            executeFast.stepThis().dispose();
        } catch (Exception e) {
            checkSQLException(e);
        }
    }

    public void lambda$markMessagesAsDeletedByRandoms$201(ArrayList arrayList) {
        getNotificationCenter().lambda$postNotificationNameOnUIThread$1(NotificationCenter.messagesDeleted, arrayList, 0L, Boolean.FALSE);
    }

    public void lambda$markMessagesAsDeletedByRandoms$202(ArrayList arrayList) {
        SQLiteCursor queryFinalized;
        SQLiteCursor sQLiteCursor = null;
        try {
            try {
                try {
                    queryFinalized = this.database.queryFinalized(String.format(Locale.US, "SELECT mid, uid FROM randoms_v2 WHERE random_id IN(%s)", TextUtils.join(",", arrayList)), new Object[0]);
                } catch (Exception e) {
                    e = e;
                } catch (Throwable th) {
                    th = th;
                }
            } catch (Throwable th2) {
                th = th2;
            }
        } catch (Exception e2) {
            e = e2;
        }
        try {
            LongSparseArray longSparseArray = new LongSparseArray();
            while (queryFinalized.next()) {
                long longValue = queryFinalized.longValue(1);
                ArrayList arrayList2 = (ArrayList) longSparseArray.get(longValue);
                if (arrayList2 == null) {
                    arrayList2 = new ArrayList();
                    longSparseArray.put(longValue, arrayList2);
                }
                arrayList2.add(Integer.valueOf(queryFinalized.intValue(0)));
            }
            queryFinalized.dispose();
            if (longSparseArray.isEmpty()) {
                return;
            }
            int size = longSparseArray.size();
            for (int i = 0; i < size; i++) {
                long keyAt = longSparseArray.keyAt(i);
                final ArrayList<Integer> arrayList3 = (ArrayList) longSparseArray.valueAt(i);
                AndroidUtilities.runOnUIThread(new Runnable() {
                    @Override
                    public final void run() {
                        MessagesStorage.this.lambda$markMessagesAsDeletedByRandoms$201(arrayList3);
                    }
                });
                updateDialogsWithReadMessagesInternal(arrayList3, null, null, null, null);
                lambda$markMessagesAsDeleted$210(keyAt, arrayList3, true, 0, 0);
                lambda$updateDialogsWithDeletedMessages$209(keyAt, 0L, arrayList3, null);
            }
        } catch (Exception e3) {
            e = e3;
            sQLiteCursor = queryFinalized;
            checkSQLException(e);
            if (sQLiteCursor != null) {
                sQLiteCursor.dispose();
            }
        } catch (Throwable th3) {
            th = th3;
            sQLiteCursor = queryFinalized;
            if (sQLiteCursor != null) {
                sQLiteCursor.dispose();
            }
            throw th;
        }
    }

    public void lambda$markMessagesAsDeletedInternal$205(ArrayList arrayList) {
        getFileLoader().cancelLoadFiles(arrayList);
    }

    public void lambda$markMessagesAsDeletedInternal$206(LongSparseArray longSparseArray) {
        getMessagesController().getSavedMessagesController().updateDeleted(longSparseArray);
    }

    public void lambda$markMessagesAsDeletedInternal$207(ArrayList arrayList, long j) {
        HashSet<Long> hashSet = new HashSet<>();
        Iterator it = arrayList.iterator();
        boolean z = false;
        while (it.hasNext()) {
            TLRPC.Message message = (TLRPC.Message) it.next();
            if (getMessagesController().processDeletedReactionTags(message)) {
                hashSet.add(Long.valueOf(MessageObject.getSavedDialogId(j, message)));
                z = true;
            }
        }
        if (z) {
            getMessagesController().updateSavedReactionTags(hashSet);
        }
    }

    public void lambda$markMessagesAsDeletedInternal$208(ArrayList arrayList) {
        HashSet<Long> hashSet = new HashSet<>();
        long[] jArr = new long[1];
        boolean z = false;
        for (int i = 0; i < arrayList.size(); i++) {
            if (getMediaDataController().processDeletedMessage(((Integer) arrayList.get(i)).intValue(), jArr)) {
                hashSet.add(Long.valueOf(jArr[0]));
                z = true;
            }
        }
        if (z) {
            getMessagesController().updateSavedReactionTags(hashSet);
        }
    }

    public void lambda$markMessagesAsDeletedInternal$211(ArrayList arrayList) {
        getFileLoader().cancelLoadFiles(arrayList);
    }

    public void lambda$markMessagesContentAsRead$199(long r9, java.util.ArrayList r11, int r12, int r13) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MessagesStorage.lambda$markMessagesContentAsRead$199(long, java.util.ArrayList, int, int):void");
    }

    public void lambda$new$0() {
        openDatabase(1);
    }

    public void lambda$onDeleteQueryComplete$86(long j) {
        try {
            this.database.executeFast("DELETE FROM media_counts_v2 WHERE uid = " + j).stepThis().dispose();
        } catch (Exception e) {
            checkSQLException(e);
        }
    }

    public void lambda$onReactionsUpdate$100(TLRPC.TL_messageReactions tL_messageReactions, TLRPC.TL_messageReactions tL_messageReactions2, long j) {
        LongSparseArray longSparseArray = new LongSparseArray();
        LongSparseArray longSparseArray2 = new LongSparseArray();
        if (tL_messageReactions != null && tL_messageReactions.results != null && tL_messageReactions.reactions_as_tags) {
            for (int i = 0; i < tL_messageReactions.results.size(); i++) {
                ReactionsLayoutInBubble.VisibleReaction fromTL = ReactionsLayoutInBubble.VisibleReaction.fromTL(tL_messageReactions.results.get(i).reaction);
                longSparseArray.put(fromTL.hash, fromTL);
            }
        }
        if (tL_messageReactions2 != null && tL_messageReactions2.results != null && tL_messageReactions2.reactions_as_tags) {
            for (int i2 = 0; i2 < tL_messageReactions2.results.size(); i2++) {
                ReactionsLayoutInBubble.VisibleReaction fromTL2 = ReactionsLayoutInBubble.VisibleReaction.fromTL(tL_messageReactions2.results.get(i2).reaction);
                longSparseArray2.put(fromTL2.hash, fromTL2);
            }
        }
        boolean z = false;
        for (int i3 = 0; i3 < longSparseArray.size(); i3++) {
            long keyAt = longSparseArray.keyAt(i3);
            ReactionsLayoutInBubble.VisibleReaction visibleReaction = (ReactionsLayoutInBubble.VisibleReaction) longSparseArray.valueAt(i3);
            if (!longSparseArray2.containsKey(keyAt)) {
                z = getMessagesController().updateSavedReactionTags(j, visibleReaction, false, false) || z;
            }
        }
        for (int i4 = 0; i4 < longSparseArray2.size(); i4++) {
            long keyAt2 = longSparseArray2.keyAt(i4);
            ReactionsLayoutInBubble.VisibleReaction visibleReaction2 = (ReactionsLayoutInBubble.VisibleReaction) longSparseArray2.valueAt(i4);
            if (!longSparseArray.containsKey(keyAt2)) {
                z = getMessagesController().updateSavedReactionTags(j, visibleReaction2, true, false) || z;
            }
        }
        if (z) {
            if (j != 0) {
                getMessagesController().updateSavedReactionTags(0L);
            }
            getMessagesController().updateSavedReactionTags(j);
        }
    }

    public void lambda$onReactionsUpdate$99(ArrayList arrayList) {
        HashSet<Long> hashSet = new HashSet<>();
        LongSparseArray longSparseArray = new LongSparseArray();
        LongSparseArray longSparseArray2 = new LongSparseArray();
        boolean z = false;
        for (int i = 0; i < arrayList.size(); i++) {
            SavedReactionsUpdate savedReactionsUpdate = (SavedReactionsUpdate) arrayList.get(i);
            TLRPC.TL_messageReactions tL_messageReactions = savedReactionsUpdate.old;
            TLRPC.TL_messageReactions tL_messageReactions2 = savedReactionsUpdate.last;
            longSparseArray.clear();
            longSparseArray2.clear();
            if (tL_messageReactions != null && tL_messageReactions.results != null && tL_messageReactions.reactions_as_tags) {
                for (int i2 = 0; i2 < tL_messageReactions.results.size(); i2++) {
                    ReactionsLayoutInBubble.VisibleReaction fromTL = ReactionsLayoutInBubble.VisibleReaction.fromTL(tL_messageReactions.results.get(i2).reaction);
                    if (fromTL != null) {
                        longSparseArray.put(fromTL.hash, fromTL);
                    }
                }
            }
            if (tL_messageReactions2 != null && tL_messageReactions2.results != null && tL_messageReactions2.reactions_as_tags) {
                for (int i3 = 0; i3 < tL_messageReactions2.results.size(); i3++) {
                    ReactionsLayoutInBubble.VisibleReaction fromTL2 = ReactionsLayoutInBubble.VisibleReaction.fromTL(tL_messageReactions2.results.get(i3).reaction);
                    if (fromTL2 != null) {
                        longSparseArray2.put(fromTL2.hash, fromTL2);
                    }
                }
            }
            for (int i4 = 0; i4 < longSparseArray.size(); i4++) {
                long keyAt = longSparseArray.keyAt(i4);
                ReactionsLayoutInBubble.VisibleReaction visibleReaction = (ReactionsLayoutInBubble.VisibleReaction) longSparseArray.valueAt(i4);
                if (!longSparseArray2.containsKey(keyAt) && getMessagesController().updateSavedReactionTags(savedReactionsUpdate.topic_id, visibleReaction, false, false)) {
                    hashSet.add(Long.valueOf(savedReactionsUpdate.topic_id));
                    z = true;
                }
            }
            for (int i5 = 0; i5 < longSparseArray2.size(); i5++) {
                long keyAt2 = longSparseArray2.keyAt(i5);
                ReactionsLayoutInBubble.VisibleReaction visibleReaction2 = (ReactionsLayoutInBubble.VisibleReaction) longSparseArray2.valueAt(i5);
                if (!longSparseArray.containsKey(keyAt2) && getMessagesController().updateSavedReactionTags(savedReactionsUpdate.topic_id, visibleReaction2, true, false)) {
                    hashSet.add(Long.valueOf(savedReactionsUpdate.topic_id));
                    z = true;
                }
            }
        }
        if (!z || hashSet.isEmpty()) {
            return;
        }
        getMessagesController().updateSavedReactionTags(hashSet);
    }

    public void lambda$openDatabase$1() {
        if (this.databaseMigrationInProgress) {
            this.databaseMigrationInProgress = false;
            NotificationCenter.getInstance(this.currentAccount).lambda$postNotificationNameOnUIThread$1(NotificationCenter.onDatabaseMigration, Boolean.FALSE);
        }
    }

    public void lambda$openDatabase$2() {
        this.showClearDatabaseAlert = false;
        NotificationCenter.getInstance(this.currentAccount).lambda$postNotificationNameOnUIThread$1(NotificationCenter.onDatabaseOpened, new Object[0]);
    }

    public void lambda$overwriteChannel$182(long j, TLRPC.TL_updates_channelDifferenceTooLong tL_updates_channelDifferenceTooLong) {
        getNotificationCenter().lambda$postNotificationNameOnUIThread$1(NotificationCenter.removeAllMessagesFromDialog, Long.valueOf(j), Boolean.TRUE, tL_updates_channelDifferenceTooLong);
    }

    public void lambda$overwriteChannel$183(long r20, int r22, final org.telegram.tgnet.TLRPC.TL_updates_channelDifferenceTooLong r23, java.lang.Runnable r24) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MessagesStorage.lambda$overwriteChannel$183(long, int, org.telegram.tgnet.TLRPC$TL_updates_channelDifferenceTooLong, java.lang.Runnable):void");
    }

    public static int lambda$processLoadedFilterPeersInternal$67(MessagesController.DialogFilter dialogFilter, MessagesController.DialogFilter dialogFilter2) {
        int i = dialogFilter.order;
        int i2 = dialogFilter2.order;
        if (i > i2) {
            return 1;
        }
        return i < i2 ? -1 : 0;
    }

    public void lambda$processPendingRead$139(long r17, int r19, int r20, int r21, int r22) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MessagesStorage.lambda$processPendingRead$139(long, int, int, int, int):void");
    }

    public void lambda$putCachedPhoneBook$143(java.util.HashMap r12, boolean r13) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MessagesStorage.lambda$putCachedPhoneBook$143(java.util.HashMap, boolean):void");
    }

    public void lambda$putChannelAdmins$118(long j, LongSparseArray longSparseArray) {
        SQLitePreparedStatement sQLitePreparedStatement = null;
        try {
            try {
                this.database.executeFast("DELETE FROM channel_admins_v3 WHERE did = " + j).stepThis().dispose();
                this.database.beginTransaction();
                SQLitePreparedStatement executeFast = this.database.executeFast("REPLACE INTO channel_admins_v3 VALUES(?, ?, ?)");
                for (int i = 0; i < longSparseArray.size(); i++) {
                    try {
                        executeFast.requery();
                        executeFast.bindLong(1, j);
                        executeFast.bindLong(2, longSparseArray.keyAt(i));
                        TLRPC.ChannelParticipant channelParticipant = (TLRPC.ChannelParticipant) longSparseArray.valueAt(i);
                        NativeByteBuffer nativeByteBuffer = new NativeByteBuffer(channelParticipant.getObjectSize());
                        channelParticipant.serializeToStream(nativeByteBuffer);
                        executeFast.bindByteBuffer(3, nativeByteBuffer);
                        executeFast.step();
                        nativeByteBuffer.reuse();
                    } catch (Exception e) {
                        e = e;
                        sQLitePreparedStatement = executeFast;
                        checkSQLException(e);
                        SQLiteDatabase sQLiteDatabase = this.database;
                        if (sQLiteDatabase != null) {
                            sQLiteDatabase.commitTransaction();
                        }
                        if (sQLitePreparedStatement != null) {
                            sQLitePreparedStatement.dispose();
                            return;
                        }
                        return;
                    } catch (Throwable th) {
                        th = th;
                        sQLitePreparedStatement = executeFast;
                        SQLiteDatabase sQLiteDatabase2 = this.database;
                        if (sQLiteDatabase2 != null) {
                            sQLiteDatabase2.commitTransaction();
                        }
                        if (sQLitePreparedStatement != null) {
                            sQLitePreparedStatement.dispose();
                        }
                        throw th;
                    }
                }
                executeFast.dispose();
                this.database.commitTransaction();
                SQLiteDatabase sQLiteDatabase3 = this.database;
                if (sQLiteDatabase3 != null) {
                    sQLiteDatabase3.commitTransaction();
                }
            } catch (Exception e2) {
                e = e2;
            }
        } catch (Throwable th2) {
            th = th2;
        }
    }

    public void lambda$putChannelViews$184(androidx.collection.LongSparseArray r21, androidx.collection.LongSparseArray r22, androidx.collection.LongSparseArray r23, boolean r24) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MessagesStorage.lambda$putChannelViews$184(androidx.collection.LongSparseArray, androidx.collection.LongSparseArray, androidx.collection.LongSparseArray, boolean):void");
    }

    public void lambda$putContacts$140(boolean r7, java.util.ArrayList r8) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MessagesStorage.lambda$putContacts$140(boolean, java.util.ArrayList):void");
    }

    public void lambda$putDialogs$234(TLRPC.messages_Dialogs messages_dialogs, int i) {
        putDialogsInternal(messages_dialogs, i);
        try {
            loadUnreadMessages();
        } catch (Exception e) {
            checkSQLException(e);
        }
    }

    public void lambda$putEncryptedChat$171(org.telegram.tgnet.TLRPC.EncryptedChat r17, org.telegram.tgnet.TLRPC.User r18, org.telegram.tgnet.TLRPC.Dialog r19) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MessagesStorage.lambda$putEncryptedChat$171(org.telegram.tgnet.TLRPC$EncryptedChat, org.telegram.tgnet.TLRPC$User, org.telegram.tgnet.TLRPC$Dialog):void");
    }

    public void lambda$putMessages$217(ArrayList arrayList) {
        getFileLoader().cancelLoadFiles(arrayList);
    }

    public void lambda$putMessages$218(ArrayList arrayList) {
        if (getMessagesController().getSavedMessagesController().updateSavedDialogs(arrayList)) {
            getMessagesController().getSavedMessagesController().update();
        }
    }

    public void lambda$putMessages$219(int r50, org.telegram.tgnet.TLRPC.messages_Messages r51, long r52, long r54, int r56, int r57, boolean r58) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MessagesStorage.lambda$putMessages$219(int, org.telegram.tgnet.TLRPC$messages_Messages, long, long, int, int, boolean):void");
    }

    public void lambda$putMessagesInternal$189(int i) {
        getDownloadController().newDownloadObjectsAvailable(i);
    }

    public void lambda$putMessagesInternal$190(ArrayList arrayList) {
        if (getMessagesController().getSavedMessagesController().updateSavedDialogs(arrayList)) {
            getMessagesController().getSavedMessagesController().update();
        }
    }

    public void lambda$putMessagesInternal$191(ArrayList arrayList) {
        Iterator it = arrayList.iterator();
        while (it.hasNext()) {
            Pair pair = (Pair) it.next();
            getMessagesController().reportMessageDelivery(((Long) pair.first).longValue(), ((Integer) pair.second).intValue(), false);
        }
    }

    public void lambda$putPushMessage$42(MessageObject messageObject) {
        try {
            NativeByteBuffer nativeByteBuffer = new NativeByteBuffer(messageObject.messageOwner.getObjectSize());
            messageObject.messageOwner.serializeToStream(nativeByteBuffer);
            int i = messageObject.localType == 2 ? 1 : 0;
            if (messageObject.localChannel) {
                i |= 2;
            }
            SQLitePreparedStatement executeFast = this.database.executeFast("REPLACE INTO unread_push_messages VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
            executeFast.requery();
            executeFast.bindLong(1, messageObject.getDialogId());
            executeFast.bindInteger(2, messageObject.getId());
            executeFast.bindLong(3, messageObject.messageOwner.random_id);
            executeFast.bindInteger(4, messageObject.messageOwner.date);
            executeFast.bindByteBuffer(5, nativeByteBuffer);
            CharSequence charSequence = messageObject.messageText;
            if (charSequence == null) {
                executeFast.bindNull(6);
            } else {
                executeFast.bindString(6, charSequence.toString());
            }
            String str = messageObject.localName;
            if (str == null) {
                executeFast.bindNull(7);
            } else {
                executeFast.bindString(7, str);
            }
            String str2 = messageObject.localUserName;
            if (str2 == null) {
                executeFast.bindNull(8);
            } else {
                executeFast.bindString(8, str2);
            }
            executeFast.bindInteger(9, i);
            executeFast.bindLong(10, MessageObject.getTopicId(this.currentAccount, messageObject.messageOwner, false));
            executeFast.bindInteger(11, (messageObject.isReactionPush ? 1 : 0) + (messageObject.isStoryReactionPush ? 1 : 0));
            executeFast.step();
            nativeByteBuffer.reuse();
            executeFast.dispose();
        } catch (Exception e) {
            checkSQLException(e);
        }
    }

    public void lambda$putSentFile$163(java.lang.String r5, org.telegram.tgnet.TLObject r6, int r7, java.lang.String r8) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MessagesStorage.lambda$putSentFile$163(java.lang.String, org.telegram.tgnet.TLObject, int, java.lang.String):void");
    }

    public void lambda$putStoryPushMessage$38(NotificationsController.StoryNotification storyNotification) {
        try {
            this.database.executeFast("DELETE FROM story_pushes WHERE uid = " + storyNotification.dialogId).stepThis().dispose();
            SQLitePreparedStatement executeFast = this.database.executeFast("REPLACE INTO story_pushes VALUES(?, ?, ?, ?, ?, ?)");
            for (Map.Entry<Integer, Pair<Long, Long>> entry : storyNotification.dateByIds.entrySet()) {
                int intValue = entry.getKey().intValue();
                long longValue = ((Long) entry.getValue().first).longValue();
                long longValue2 = ((Long) entry.getValue().second).longValue();
                executeFast.requery();
                executeFast.bindLong(1, storyNotification.dialogId);
                executeFast.bindInteger(2, intValue);
                executeFast.bindLong(3, longValue);
                if (storyNotification.localName == null) {
                    storyNotification.localName = "";
                }
                executeFast.bindString(4, storyNotification.localName);
                executeFast.bindInteger(5, storyNotification.hidden ? 1 : 0);
                executeFast.bindLong(6, longValue2);
                executeFast.step();
            }
            executeFast.dispose();
        } catch (Exception e) {
            checkSQLException(e);
        }
    }

    public void lambda$putWallpapers$75(int r11, java.util.ArrayList r12) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MessagesStorage.lambda$putWallpapers$75(int, java.util.ArrayList):void");
    }

    public void lambda$putWebPages$180(ArrayList arrayList) {
        getNotificationCenter().lambda$postNotificationNameOnUIThread$1(NotificationCenter.didReceivedWebpages, arrayList);
    }

    public void lambda$putWebPages$181(androidx.collection.LongSparseArray r18) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MessagesStorage.lambda$putWebPages$181(androidx.collection.LongSparseArray):void");
    }

    public void lambda$putWidgetDialogs$159(int i, ArrayList arrayList) {
        try {
            this.database.beginTransaction();
            this.database.executeFast("DELETE FROM shortcut_widget WHERE id = " + i).stepThis().dispose();
            SQLitePreparedStatement executeFast = this.database.executeFast("REPLACE INTO shortcut_widget VALUES(?, ?, ?)");
            if (arrayList.isEmpty()) {
                executeFast.requery();
                executeFast.bindInteger(1, i);
                executeFast.bindLong(2, -1L);
                executeFast.bindInteger(3, 0);
                executeFast.step();
            } else {
                int size = arrayList.size();
                for (int i2 = 0; i2 < size; i2++) {
                    long j = ((TopicKey) arrayList.get(i2)).dialogId;
                    executeFast.requery();
                    executeFast.bindInteger(1, i);
                    executeFast.bindLong(2, j);
                    executeFast.bindInteger(3, i2);
                    executeFast.step();
                }
            }
            executeFast.dispose();
            this.database.commitTransaction();
        } catch (Exception e) {
            checkSQLException(e);
        }
    }

    public void lambda$readAllDialogs$61(ArrayList arrayList, ArrayList arrayList2, ArrayList arrayList3, LongSparseArray longSparseArray) {
        getMessagesController().putUsers(arrayList, true);
        getMessagesController().putChats(arrayList2, true);
        getMessagesController().putEncryptedChats(arrayList3, true);
        for (int i = 0; i < longSparseArray.size(); i++) {
            long keyAt = longSparseArray.keyAt(i);
            ReadDialog readDialog = (ReadDialog) longSparseArray.valueAt(i);
            if (getMessagesController().isForum(keyAt)) {
                getMessagesController().markAllTopicsAsRead(keyAt);
            }
            MessagesController messagesController = getMessagesController();
            int i2 = readDialog.lastMid;
            messagesController.markDialogAsRead(keyAt, i2, i2, readDialog.date, false, 0L, readDialog.unreadCount, true, 0);
        }
    }

    public void lambda$readAllDialogs$62(int i) {
        Long valueOf;
        ArrayList<Long> arrayList;
        SQLiteCursor sQLiteCursor = 0;
        SQLiteCursor sQLiteCursor2 = null;
        try {
            try {
                ArrayList<Long> arrayList2 = new ArrayList<>();
                ArrayList<Long> arrayList3 = new ArrayList<>();
                ArrayList arrayList4 = new ArrayList();
                final LongSparseArray longSparseArray = new LongSparseArray();
                SQLiteCursor queryFinalized = i >= 0 ? this.database.queryFinalized(String.format(Locale.US, "SELECT did, last_mid, unread_count, date FROM dialogs WHERE unread_count > 0 AND folder_id = %1$d", Integer.valueOf(i)), new Object[0]) : this.database.queryFinalized("SELECT did, last_mid, unread_count, date FROM dialogs WHERE unread_count > 0", new Object[0]);
                while (queryFinalized.next()) {
                    try {
                        long longValue = queryFinalized.longValue(0);
                        if (!DialogObject.isFolderDialogId(longValue)) {
                            ReadDialog readDialog = new ReadDialog();
                            readDialog.lastMid = queryFinalized.intValue(1);
                            readDialog.unreadCount = queryFinalized.intValue(2);
                            readDialog.date = queryFinalized.intValue(3);
                            longSparseArray.put(longValue, readDialog);
                            if (DialogObject.isEncryptedDialog(longValue)) {
                                int encryptedChatId = DialogObject.getEncryptedChatId(longValue);
                                if (!arrayList4.contains(Integer.valueOf(encryptedChatId))) {
                                    arrayList4.add(Integer.valueOf(encryptedChatId));
                                }
                            } else if (DialogObject.isChatDialog(longValue)) {
                                long j = -longValue;
                                if (!arrayList3.contains(Long.valueOf(j))) {
                                    valueOf = Long.valueOf(j);
                                    arrayList = arrayList3;
                                    arrayList.add(valueOf);
                                }
                            } else if (!arrayList2.contains(Long.valueOf(longValue))) {
                                valueOf = Long.valueOf(longValue);
                                arrayList = arrayList2;
                                arrayList.add(valueOf);
                            }
                        }
                    } catch (Exception e) {
                        sQLiteCursor = queryFinalized;
                        e = e;
                        checkSQLException(e);
                        if (sQLiteCursor != 0) {
                            sQLiteCursor.dispose();
                            return;
                        }
                        return;
                    } catch (Throwable th) {
                        sQLiteCursor2 = queryFinalized;
                        th = th;
                        if (sQLiteCursor2 != null) {
                            sQLiteCursor2.dispose();
                        }
                        throw th;
                    }
                }
                queryFinalized.dispose();
                final ArrayList<TLRPC.User> arrayList5 = new ArrayList<>();
                final ArrayList<TLRPC.Chat> arrayList6 = new ArrayList<>();
                final ArrayList<TLRPC.EncryptedChat> arrayList7 = new ArrayList<>();
                if (!arrayList4.isEmpty()) {
                    getEncryptedChatsInternal(TextUtils.join(",", arrayList4), arrayList7, arrayList2);
                }
                if (!arrayList2.isEmpty()) {
                    getUsersInternal(arrayList2, arrayList5);
                }
                if (!arrayList3.isEmpty()) {
                    getChatsInternal(TextUtils.join(",", arrayList3), arrayList6);
                }
                AndroidUtilities.runOnUIThread(new Runnable() {
                    @Override
                    public final void run() {
                        MessagesStorage.this.lambda$readAllDialogs$61(arrayList5, arrayList6, arrayList7, longSparseArray);
                    }
                });
            } catch (Throwable th2) {
                th = th2;
            }
        } catch (Exception e2) {
            e = e2;
        }
    }

    public void lambda$removeFromDownloadQueue$175(boolean z, int i, long j) {
        Throwable th;
        Exception e;
        SQLitePreparedStatement executeFast;
        SQLiteCursor sQLiteCursor = null;
        try {
            try {
                try {
                    if (z) {
                        SQLiteDatabase sQLiteDatabase = this.database;
                        Locale locale = Locale.US;
                        SQLiteCursor queryFinalized = sQLiteDatabase.queryFinalized(String.format(locale, "SELECT min(date) FROM download_queue WHERE type = %d", Integer.valueOf(i)), new Object[0]);
                        try {
                            int intValue = queryFinalized.next() ? queryFinalized.intValue(0) : -1;
                            queryFinalized.dispose();
                            if (intValue == -1) {
                                return;
                            } else {
                                executeFast = this.database.executeFast(String.format(locale, "UPDATE download_queue SET date = %d WHERE uid = %d AND type = %d", Integer.valueOf(intValue - 1), Long.valueOf(j), Integer.valueOf(i)));
                            }
                        } catch (Exception e2) {
                            e = e2;
                            sQLiteCursor = queryFinalized;
                            checkSQLException(e);
                            if (sQLiteCursor != null) {
                                sQLiteCursor.dispose();
                                return;
                            }
                            return;
                        } catch (Throwable th2) {
                            th = th2;
                            sQLiteCursor = queryFinalized;
                            if (sQLiteCursor != null) {
                                sQLiteCursor.dispose();
                            }
                            throw th;
                        }
                    } else {
                        executeFast = this.database.executeFast(String.format(Locale.US, "DELETE FROM download_queue WHERE uid = %d AND type = %d", Long.valueOf(j), Integer.valueOf(i)));
                    }
                    executeFast.stepThis().dispose();
                } catch (Exception e3) {
                    e = e3;
                }
            } catch (Throwable th3) {
                th = th3;
            }
        } catch (Exception e4) {
            e = e4;
        } catch (Throwable th4) {
            th = th4;
        }
    }

    public void lambda$removePendingTask$11(long j) {
        try {
            this.database.executeFast("DELETE FROM pending_tasks WHERE id = " + j).stepThis().dispose();
        } catch (Exception e) {
            checkSQLException(e);
        }
    }

    public void lambda$removeTopic$54(long j, int i) {
        try {
            SQLiteDatabase sQLiteDatabase = this.database;
            Locale locale = Locale.US;
            sQLiteDatabase.executeFast(String.format(locale, "DELETE FROM topics WHERE did = %d AND topic_id = %d", Long.valueOf(j), Integer.valueOf(i))).stepThis().dispose();
            this.database.executeFast(String.format(locale, "DELETE FROM messages_topics WHERE uid = %d AND topic_id = %d", Long.valueOf(j), Integer.valueOf(i))).stepThis().dispose();
        } catch (SQLiteException e) {
            e.printStackTrace();
        }
    }

    public void lambda$removeTopics$55(ArrayList arrayList, long j) {
        try {
            String join = TextUtils.join(", ", arrayList);
            SQLiteDatabase sQLiteDatabase = this.database;
            Locale locale = Locale.US;
            sQLiteDatabase.executeFast(String.format(locale, "DELETE FROM topics WHERE did = %d AND topic_id IN (%s)", Long.valueOf(j), join)).stepThis().dispose();
            this.database.executeFast(String.format(locale, "DELETE FROM messages_topics WHERE uid = %d AND topic_id IN (%s)", Long.valueOf(j), join)).stepThis().dispose();
        } catch (SQLiteException e) {
            e.printStackTrace();
        }
    }

    public void lambda$replaceMessageIfExists$213(MessageObject messageObject, ArrayList arrayList) {
        getNotificationCenter().lambda$postNotificationNameOnUIThread$1(NotificationCenter.replaceMessagesObjects, Long.valueOf(messageObject.getDialogId()), arrayList);
    }

    public void lambda$replaceMessageIfExists$214(ArrayList arrayList) {
        if (getMessagesController().getSavedMessagesController().updateSavedDialogs(arrayList)) {
            getMessagesController().getSavedMessagesController().update();
        }
    }

    public void lambda$replaceMessageIfExists$215(org.telegram.tgnet.TLRPC.Message r25, boolean r26, java.util.ArrayList r27, java.util.ArrayList r28) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MessagesStorage.lambda$replaceMessageIfExists$215(org.telegram.tgnet.TLRPC$Message, boolean, java.util.ArrayList, java.util.ArrayList):void");
    }

    public void lambda$reset$58() {
        for (int i = 0; i < 2; i++) {
            getUserConfig().setDialogsLoadOffset(i, 0, 0, 0L, 0L, 0L, 0L);
            getUserConfig().setTotalDialogsCount(i, 0);
        }
        getUserConfig().clearFilters();
        getUserConfig().clearPinnedDialogsLoaded();
        getNotificationCenter().lambda$postNotificationNameOnUIThread$1(NotificationCenter.didClearDatabase, new Object[0]);
        getMediaDataController().loadAttachMenuBots(false, true);
        getNotificationCenter().lambda$postNotificationNameOnUIThread$1(NotificationCenter.onDatabaseReset, new Object[0]);
        getMessagesController().getStoriesController().cleanup();
    }

    public void lambda$resetAllUnreadCounters$231() {
        ArrayList<MessagesController.DialogFilter> arrayList = getMessagesController().dialogFilters;
        int size = arrayList.size();
        for (int i = 0; i < size; i++) {
            arrayList.get(i).unreadCount = arrayList.get(i).pendingUnreadCount;
        }
        this.mainUnreadCount = this.pendingMainUnreadCount;
        this.archiveUnreadCount = this.pendingArchiveUnreadCount;
        getNotificationCenter().lambda$postNotificationNameOnUIThread$1(NotificationCenter.updateInterfaces, Integer.valueOf(MessagesController.UPDATE_MASK_READ_DIALOG_MESSAGE));
    }

    public static int lambda$resetDialogs$89(LongSparseIntArray longSparseIntArray, Long l, Long l2) {
        int i = longSparseIntArray.get(l.longValue());
        int i2 = longSparseIntArray.get(l2.longValue());
        if (i < i2) {
            return 1;
        }
        return i > i2 ? -1 : 0;
    }

    public void lambda$resetDialogs$90(org.telegram.tgnet.TLRPC.messages_Dialogs r35, int r36, int r37, int r38, int r39, int r40, org.telegram.tgnet.TLRPC.Message r41, int r42, androidx.collection.LongSparseArray r43, androidx.collection.LongSparseArray r44) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MessagesStorage.lambda$resetDialogs$90(org.telegram.tgnet.TLRPC$messages_Dialogs, int, int, int, int, int, org.telegram.tgnet.TLRPC$Message, int, androidx.collection.LongSparseArray, androidx.collection.LongSparseArray):void");
    }

    public void lambda$resetMentionsCount$108(long r17, long r19, int r21) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MessagesStorage.lambda$resetMentionsCount$108(long, long, int):void");
    }

    public void lambda$saveBotCache$120(TLObject tLObject, String str) {
        int currentTime;
        int i;
        SQLitePreparedStatement executeFast;
        SQLitePreparedStatement sQLitePreparedStatement = null;
        try {
            try {
                currentTime = getConnectionsManager().getCurrentTime();
            } catch (Throwable th) {
                th = th;
            }
        } catch (Exception e) {
            e = e;
        }
        try {
            if (!(tLObject instanceof TLRPC.TL_messages_botCallbackAnswer)) {
                if (tLObject instanceof TLRPC.TL_messages_botResults) {
                    i = ((TLRPC.TL_messages_botResults) tLObject).cache_time;
                }
                executeFast = this.database.executeFast("REPLACE INTO botcache VALUES(?, ?, ?)");
                NativeByteBuffer nativeByteBuffer = new NativeByteBuffer(tLObject.getObjectSize());
                tLObject.serializeToStream(nativeByteBuffer);
                executeFast.bindString(1, str);
                executeFast.bindInteger(2, currentTime);
                executeFast.bindByteBuffer(3, nativeByteBuffer);
                executeFast.step();
                executeFast.dispose();
                nativeByteBuffer.reuse();
                return;
            }
            i = ((TLRPC.TL_messages_botCallbackAnswer) tLObject).cache_time;
            NativeByteBuffer nativeByteBuffer2 = new NativeByteBuffer(tLObject.getObjectSize());
            tLObject.serializeToStream(nativeByteBuffer2);
            executeFast.bindString(1, str);
            executeFast.bindInteger(2, currentTime);
            executeFast.bindByteBuffer(3, nativeByteBuffer2);
            executeFast.step();
            executeFast.dispose();
            nativeByteBuffer2.reuse();
            return;
        } catch (Exception e2) {
            e = e2;
            sQLitePreparedStatement = executeFast;
            checkSQLException(e);
            if (sQLitePreparedStatement != null) {
                sQLitePreparedStatement.dispose();
                return;
            }
            return;
        } catch (Throwable th2) {
            th = th2;
            sQLitePreparedStatement = executeFast;
            if (sQLitePreparedStatement != null) {
                sQLitePreparedStatement.dispose();
            }
            throw th;
        }
        currentTime += i;
        executeFast = this.database.executeFast("REPLACE INTO botcache VALUES(?, ?, ?)");
    }

    public void lambda$saveChannelPts$34(int i, long j) {
        try {
            SQLitePreparedStatement executeFast = this.database.executeFast("UPDATE dialogs SET pts = ? WHERE did = ?");
            executeFast.bindInteger(1, i);
            executeFast.bindLong(2, -j);
            executeFast.step();
            executeFast.dispose();
        } catch (Exception e) {
            checkSQLException(e);
        }
    }

    public void lambda$saveChatInviter$126(long j, long j2) {
        SQLitePreparedStatement sQLitePreparedStatement = null;
        try {
            try {
                sQLitePreparedStatement = this.database.executeFast("UPDATE chat_settings_v2 SET inviter = ? WHERE uid = ?");
                sQLitePreparedStatement.requery();
                sQLitePreparedStatement.bindLong(1, j);
                sQLitePreparedStatement.bindLong(2, j2);
                sQLitePreparedStatement.step();
                sQLitePreparedStatement.dispose();
            } catch (Exception e) {
                checkSQLException(e);
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

    public void lambda$saveChatLinksCount$127(int i, long j) {
        SQLitePreparedStatement sQLitePreparedStatement = null;
        try {
            try {
                sQLitePreparedStatement = this.database.executeFast("UPDATE chat_settings_v2 SET links = ? WHERE uid = ?");
                sQLitePreparedStatement.requery();
                sQLitePreparedStatement.bindInteger(1, i);
                sQLitePreparedStatement.bindLong(2, j);
                sQLitePreparedStatement.step();
                sQLitePreparedStatement.dispose();
            } catch (Exception e) {
                checkSQLException(e);
                if (sQLitePreparedStatement != null) {
                    sQLitePreparedStatement.dispose();
                }
            }
        } catch (Throwable th) {
            if (sQLitePreparedStatement != null) {
                sQLitePreparedStatement.dispose();
            }
            throw th;
        }
    }

    public void lambda$saveDialogFilter$70() {
        ArrayList<MessagesController.DialogFilter> arrayList = getMessagesController().dialogFilters;
        int size = arrayList.size();
        for (int i = 0; i < size; i++) {
            arrayList.get(i).unreadCount = arrayList.get(i).pendingUnreadCount;
        }
        this.mainUnreadCount = this.pendingMainUnreadCount;
        this.archiveUnreadCount = this.pendingArchiveUnreadCount;
        getNotificationCenter().lambda$postNotificationNameOnUIThread$1(NotificationCenter.updateInterfaces, Integer.valueOf(MessagesController.UPDATE_MASK_READ_DIALOG_MESSAGE));
    }

    public void lambda$saveDialogFilter$71(MessagesController.DialogFilter dialogFilter, boolean z, boolean z2) {
        saveDialogFilterInternal(dialogFilter, z, z2);
        calcUnreadCounters(false);
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                MessagesStorage.this.lambda$saveDialogFilter$70();
            }
        });
    }

    public void lambda$saveDialogFiltersOrder$72(ArrayList arrayList) {
        this.dialogFilters.clear();
        this.dialogFiltersMap.clear();
        this.dialogFilters.addAll(arrayList);
        for (int i = 0; i < arrayList.size(); i++) {
            ((MessagesController.DialogFilter) arrayList.get(i)).order = i;
            this.dialogFiltersMap.put(((MessagesController.DialogFilter) arrayList.get(i)).id, (MessagesController.DialogFilter) arrayList.get(i));
        }
        saveDialogFiltersOrderInternal();
    }

    public void lambda$saveSecretParams$7(int i, int i2, byte[] bArr) {
        try {
            SQLitePreparedStatement executeFast = this.database.executeFast("UPDATE params SET lsv = ?, sg = ?, pbytes = ? WHERE id = 1");
            executeFast.bindInteger(1, i);
            executeFast.bindInteger(2, i2);
            NativeByteBuffer nativeByteBuffer = new NativeByteBuffer(bArr != null ? bArr.length : 1);
            if (bArr != null) {
                nativeByteBuffer.writeBytes(bArr);
            }
            executeFast.bindByteBuffer(3, nativeByteBuffer);
            executeFast.step();
            executeFast.dispose();
            nativeByteBuffer.reuse();
        } catch (Exception e) {
            checkSQLException(e);
        }
    }

    public void lambda$saveTopics$45(long j, List list, boolean z, int i) {
        saveTopicsInternal(j, list, z, true, i);
    }

    public void lambda$searchSavedByTag$97(java.lang.String r38, long r39, org.telegram.tgnet.TLRPC.Reaction r41, int r42, int r43, boolean r44, final org.telegram.messenger.Utilities.Callback4 r45) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MessagesStorage.lambda$searchSavedByTag$97(java.lang.String, long, org.telegram.tgnet.TLRPC$Reaction, int, int, boolean, org.telegram.messenger.Utilities$Callback4):void");
    }

    public void lambda$setDialogFlags$37(long j, long j2) {
        try {
            SQLiteCursor queryFinalized = this.database.queryFinalized("SELECT flags FROM dialog_settings WHERE did = " + j, new Object[0]);
            int intValue = queryFinalized.next() ? queryFinalized.intValue(0) : 0;
            queryFinalized.dispose();
            if (j2 == intValue) {
                return;
            }
            this.database.executeFast(String.format(Locale.US, "REPLACE INTO dialog_settings VALUES(%d, %d)", Long.valueOf(j), Long.valueOf(j2))).stepThis().dispose();
            resetAllUnreadCounters(true);
        } catch (Exception e) {
            checkSQLException(e);
        }
    }

    public void lambda$setDialogPinned$232(int i, long j) {
        SQLitePreparedStatement sQLitePreparedStatement = null;
        try {
            try {
                sQLitePreparedStatement = this.database.executeFast("UPDATE dialogs SET pinned = ? WHERE did = ?");
                sQLitePreparedStatement.bindInteger(1, i);
                sQLitePreparedStatement.bindLong(2, j);
                sQLitePreparedStatement.step();
                sQLitePreparedStatement.dispose();
            } catch (Exception e) {
                checkSQLException(e);
                if (sQLitePreparedStatement != null) {
                    sQLitePreparedStatement.dispose();
                }
            }
        } catch (Throwable th) {
            if (sQLitePreparedStatement != null) {
                sQLitePreparedStatement.dispose();
            }
            throw th;
        }
    }

    public void lambda$setDialogTtl$57(int i, long j) {
        try {
            this.database.executeFast(String.format(Locale.US, "UPDATE dialogs SET ttl_period = %d WHERE did = %d", Integer.valueOf(i), Long.valueOf(j))).stepThis().dispose();
        } catch (SQLiteException e) {
            checkSQLException(e);
        }
    }

    public void lambda$setDialogUnread$229(long r6, boolean r8) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MessagesStorage.lambda$setDialogUnread$229(long, boolean):void");
    }

    public void lambda$setDialogViewThreadAsMessages$230(long r6, boolean r8) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MessagesStorage.lambda$setDialogViewThreadAsMessages$230(long, boolean):void");
    }

    public void lambda$setDialogsFolderId$225(ArrayList arrayList, ArrayList arrayList2, int i, long j) {
        SQLitePreparedStatement executeFast;
        boolean z;
        SQLitePreparedStatement sQLitePreparedStatement = null;
        try {
            try {
                this.database.beginTransaction();
                executeFast = this.database.executeFast("UPDATE dialogs SET folder_id = ?, pinned = ? WHERE did = ?");
            } catch (Throwable th) {
                th = th;
            }
        } catch (Exception e) {
            e = e;
        }
        try {
            if (arrayList != null) {
                int size = arrayList.size();
                z = false;
                for (int i2 = 0; i2 < size; i2++) {
                    TLRPC.TL_folderPeer tL_folderPeer = (TLRPC.TL_folderPeer) arrayList.get(i2);
                    long peerDialogId = DialogObject.getPeerDialogId(tL_folderPeer.peer);
                    executeFast.requery();
                    executeFast.bindInteger(1, tL_folderPeer.folder_id);
                    if (tL_folderPeer.folder_id == 1) {
                        z = true;
                    }
                    executeFast.bindInteger(2, 0);
                    executeFast.bindLong(3, peerDialogId);
                    executeFast.step();
                    this.unknownDialogsIds.remove(peerDialogId);
                }
            } else if (arrayList2 != null) {
                int size2 = arrayList2.size();
                z = false;
                for (int i3 = 0; i3 < size2; i3++) {
                    TLRPC.TL_inputFolderPeer tL_inputFolderPeer = (TLRPC.TL_inputFolderPeer) arrayList2.get(i3);
                    long peerDialogId2 = DialogObject.getPeerDialogId(tL_inputFolderPeer.peer);
                    executeFast.requery();
                    executeFast.bindInteger(1, tL_inputFolderPeer.folder_id);
                    if (tL_inputFolderPeer.folder_id == 1) {
                        z = true;
                    }
                    executeFast.bindInteger(2, 0);
                    executeFast.bindLong(3, peerDialogId2);
                    executeFast.step();
                    this.unknownDialogsIds.remove(peerDialogId2);
                }
            } else {
                executeFast.requery();
                executeFast.bindInteger(1, i);
                boolean z2 = i == 1;
                executeFast.bindInteger(2, 0);
                executeFast.bindLong(3, j);
                executeFast.step();
                z = z2;
            }
            executeFast.dispose();
            this.database.commitTransaction();
            if (!z) {
                lambda$checkIfFolderEmpty$227(1);
            }
            resetAllUnreadCounters(false);
            SQLiteDatabase sQLiteDatabase = this.database;
            if (sQLiteDatabase != null) {
                sQLiteDatabase.commitTransaction();
            }
        } catch (Exception e2) {
            e = e2;
            sQLitePreparedStatement = executeFast;
            checkSQLException(e);
            SQLiteDatabase sQLiteDatabase2 = this.database;
            if (sQLiteDatabase2 != null) {
                sQLiteDatabase2.commitTransaction();
            }
            if (sQLitePreparedStatement != null) {
                sQLitePreparedStatement.dispose();
            }
        } catch (Throwable th2) {
            th = th2;
            sQLitePreparedStatement = executeFast;
            SQLiteDatabase sQLiteDatabase3 = this.database;
            if (sQLiteDatabase3 != null) {
                sQLiteDatabase3.commitTransaction();
            }
            if (sQLitePreparedStatement != null) {
                sQLitePreparedStatement.dispose();
            }
            throw th;
        }
    }

    public void lambda$setDialogsPinned$233(ArrayList arrayList, ArrayList arrayList2) {
        SQLitePreparedStatement sQLitePreparedStatement = null;
        try {
            try {
                sQLitePreparedStatement = this.database.executeFast("UPDATE dialogs SET pinned = ? WHERE did = ?");
                int size = arrayList.size();
                for (int i = 0; i < size; i++) {
                    sQLitePreparedStatement.requery();
                    sQLitePreparedStatement.bindInteger(1, ((Integer) arrayList2.get(i)).intValue());
                    sQLitePreparedStatement.bindLong(2, ((Long) arrayList.get(i)).longValue());
                    sQLitePreparedStatement.step();
                }
                sQLitePreparedStatement.dispose();
            } catch (Exception e) {
                checkSQLException(e);
                if (sQLitePreparedStatement != null) {
                    sQLitePreparedStatement.dispose();
                }
            }
        } catch (Throwable th) {
            if (sQLitePreparedStatement != null) {
                sQLitePreparedStatement.dispose();
            }
            throw th;
        }
    }

    public void lambda$setMessageSeq$195(int i, int i2, int i3) {
        SQLitePreparedStatement sQLitePreparedStatement = null;
        try {
            try {
                sQLitePreparedStatement = this.database.executeFast("REPLACE INTO messages_seq VALUES(?, ?, ?)");
                sQLitePreparedStatement.requery();
                sQLitePreparedStatement.bindInteger(1, i);
                sQLitePreparedStatement.bindInteger(2, i2);
                sQLitePreparedStatement.bindInteger(3, i3);
                sQLitePreparedStatement.step();
            } catch (Exception e) {
                checkSQLException(e);
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

    public void lambda$unpinAllDialogsExceptNew$228(java.util.ArrayList r10, int r11) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MessagesStorage.lambda$unpinAllDialogsExceptNew$228(java.util.ArrayList, int):void");
    }

    public void lambda$updateChannelUsers$119(long j, ArrayList arrayList) {
        SQLitePreparedStatement executeFast;
        long j2 = -j;
        SQLitePreparedStatement sQLitePreparedStatement = null;
        try {
            try {
                this.database.executeFast("DELETE FROM channel_users_v2 WHERE did = " + j2).stepThis().dispose();
                this.database.beginTransaction();
                executeFast = this.database.executeFast("REPLACE INTO channel_users_v2 VALUES(?, ?, ?, ?)");
            } catch (Throwable th) {
                th = th;
            }
        } catch (Exception e) {
            e = e;
        }
        try {
            int currentTimeMillis = (int) (System.currentTimeMillis() / 1000);
            for (int i = 0; i < arrayList.size(); i++) {
                TLRPC.ChannelParticipant channelParticipant = (TLRPC.ChannelParticipant) arrayList.get(i);
                executeFast.requery();
                executeFast.bindLong(1, j2);
                executeFast.bindLong(2, MessageObject.getPeerId(channelParticipant.peer));
                executeFast.bindInteger(3, currentTimeMillis);
                NativeByteBuffer nativeByteBuffer = new NativeByteBuffer(channelParticipant.getObjectSize());
                channelParticipant.serializeToStream(nativeByteBuffer);
                executeFast.bindByteBuffer(4, nativeByteBuffer);
                executeFast.step();
                nativeByteBuffer.reuse();
                currentTimeMillis--;
            }
            executeFast.dispose();
            this.database.commitTransaction();
            loadChatInfo(j, true, null, false, true);
            SQLiteDatabase sQLiteDatabase = this.database;
            if (sQLiteDatabase != null) {
                sQLiteDatabase.commitTransaction();
            }
        } catch (Exception e2) {
            e = e2;
            sQLitePreparedStatement = executeFast;
            checkSQLException(e);
            SQLiteDatabase sQLiteDatabase2 = this.database;
            if (sQLiteDatabase2 != null) {
                sQLiteDatabase2.commitTransaction();
            }
            if (sQLitePreparedStatement != null) {
                sQLitePreparedStatement.dispose();
            }
        } catch (Throwable th2) {
            th = th2;
            sQLitePreparedStatement = executeFast;
            SQLiteDatabase sQLiteDatabase3 = this.database;
            if (sQLiteDatabase3 != null) {
                sQLiteDatabase3.commitTransaction();
            }
            if (sQLitePreparedStatement != null) {
                sQLitePreparedStatement.dispose();
            }
            throw th;
        }
    }

    public void lambda$updateChatDefaultBannedRights$173(long r8, int r10, org.telegram.tgnet.TLRPC.TL_chatBannedRights r11) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MessagesStorage.lambda$updateChatDefaultBannedRights$173(long, int, org.telegram.tgnet.TLRPC$TL_chatBannedRights):void");
    }

    public void lambda$updateChatInfo$128(org.telegram.tgnet.TLRPC.ChatFull r12, boolean r13) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MessagesStorage.lambda$updateChatInfo$128(org.telegram.tgnet.TLRPC$ChatFull, boolean):void");
    }

    public void lambda$updateChatInfo$133(TLRPC.ChatFull chatFull) {
        NotificationCenter notificationCenter = getNotificationCenter();
        int i = NotificationCenter.chatInfoDidLoad;
        Boolean bool = Boolean.FALSE;
        notificationCenter.lambda$postNotificationNameOnUIThread$1(i, chatFull, 0, bool, bool);
    }

    public void lambda$updateChatInfo$134(long j, int i, long j2, long j3, int i2) {
        int i3;
        SQLiteCursor queryFinalized;
        final TLRPC.ChatFull chatFull;
        NativeByteBuffer byteBufferValue;
        SQLiteCursor sQLiteCursor = null;
        try {
            try {
                i3 = 0;
                queryFinalized = this.database.queryFinalized("SELECT info, pinned, online, inviter FROM chat_settings_v2 WHERE uid = " + j, new Object[0]);
            } catch (Exception e) {
                e = e;
            }
        } catch (Throwable th) {
            th = th;
        }
        try {
            new ArrayList();
            if (!queryFinalized.next() || (byteBufferValue = queryFinalized.byteBufferValue(0)) == null) {
                chatFull = null;
            } else {
                chatFull = TLRPC.ChatFull.TLdeserialize(byteBufferValue, byteBufferValue.readInt32(false), false);
                byteBufferValue.reuse();
                chatFull.pinned_msg_id = queryFinalized.intValue(1);
                chatFull.online_count = queryFinalized.intValue(2);
                chatFull.inviterId = queryFinalized.longValue(3);
            }
            queryFinalized.dispose();
            if (chatFull instanceof TLRPC.TL_chatFull) {
                if (i == 1) {
                    while (true) {
                        if (i3 >= chatFull.participants.participants.size()) {
                            break;
                        }
                        if (chatFull.participants.participants.get(i3).user_id == j2) {
                            chatFull.participants.participants.remove(i3);
                            break;
                        }
                        i3++;
                    }
                } else if (i == 0) {
                    Iterator<TLRPC.ChatParticipant> it = chatFull.participants.participants.iterator();
                    while (it.hasNext()) {
                        if (it.next().user_id == j2) {
                            return;
                        }
                    }
                    TLRPC.TL_chatParticipant tL_chatParticipant = new TLRPC.TL_chatParticipant();
                    tL_chatParticipant.user_id = j2;
                    tL_chatParticipant.inviter_id = j3;
                    tL_chatParticipant.date = getConnectionsManager().getCurrentTime();
                    chatFull.participants.participants.add(tL_chatParticipant);
                } else if (i == 2) {
                    while (true) {
                        if (i3 >= chatFull.participants.participants.size()) {
                            break;
                        }
                        TLRPC.ChatParticipant chatParticipant = chatFull.participants.participants.get(i3);
                        if (chatParticipant.user_id == j2) {
                            TLRPC.ChatParticipant tL_chatParticipantAdmin = j3 == 1 ? new TLRPC.TL_chatParticipantAdmin() : new TLRPC.TL_chatParticipant();
                            tL_chatParticipantAdmin.user_id = chatParticipant.user_id;
                            tL_chatParticipantAdmin.date = chatParticipant.date;
                            tL_chatParticipantAdmin.inviter_id = chatParticipant.inviter_id;
                            chatFull.participants.participants.set(i3, tL_chatParticipantAdmin);
                        } else {
                            i3++;
                        }
                    }
                }
                chatFull.participants.version = i2;
                AndroidUtilities.runOnUIThread(new Runnable() {
                    @Override
                    public final void run() {
                        MessagesStorage.this.lambda$updateChatInfo$133(chatFull);
                    }
                });
                SQLitePreparedStatement executeFast = this.database.executeFast("REPLACE INTO chat_settings_v2 VALUES(?, ?, ?, ?, ?, ?, ?)");
                NativeByteBuffer nativeByteBuffer = new NativeByteBuffer(chatFull.getObjectSize());
                chatFull.serializeToStream(nativeByteBuffer);
                executeFast.bindLong(1, j);
                executeFast.bindByteBuffer(2, nativeByteBuffer);
                executeFast.bindInteger(3, chatFull.pinned_msg_id);
                executeFast.bindInteger(4, chatFull.online_count);
                executeFast.bindLong(5, chatFull.inviterId);
                executeFast.bindInteger(6, chatFull.invitesCount);
                executeFast.bindInteger(7, chatFull.participants_count);
                executeFast.step();
                executeFast.dispose();
                nativeByteBuffer.reuse();
            }
        } catch (Exception e2) {
            e = e2;
            sQLiteCursor = queryFinalized;
            checkSQLException(e);
            if (sQLiteCursor != null) {
                sQLiteCursor.dispose();
            }
        } catch (Throwable th2) {
            th = th2;
            sQLiteCursor = queryFinalized;
            if (sQLiteCursor != null) {
                sQLiteCursor.dispose();
            }
            throw th;
        }
    }

    public void lambda$updateChatOnlineCount$129(int i, long j) {
        SQLitePreparedStatement sQLitePreparedStatement = null;
        try {
            try {
                sQLitePreparedStatement = this.database.executeFast("UPDATE chat_settings_v2 SET online = ? WHERE uid = ?");
                sQLitePreparedStatement.requery();
                sQLitePreparedStatement.bindInteger(1, i);
                sQLitePreparedStatement.bindLong(2, j);
                sQLitePreparedStatement.step();
                sQLitePreparedStatement.dispose();
            } catch (Exception e) {
                checkSQLException(e);
                if (sQLitePreparedStatement != null) {
                    sQLitePreparedStatement.dispose();
                }
            }
        } catch (Throwable th) {
            if (sQLitePreparedStatement != null) {
                sQLitePreparedStatement.dispose();
            }
            throw th;
        }
    }

    public void lambda$updateChatParticipants$115(TLRPC.ChatFull chatFull) {
        NotificationCenter notificationCenter = getNotificationCenter();
        int i = NotificationCenter.chatInfoDidLoad;
        Boolean bool = Boolean.FALSE;
        notificationCenter.lambda$postNotificationNameOnUIThread$1(i, chatFull, 0, bool, bool);
    }

    public void lambda$updateChatParticipants$116(TLRPC.ChatParticipants chatParticipants) {
        SQLiteCursor queryFinalized;
        final TLRPC.ChatFull chatFull;
        NativeByteBuffer byteBufferValue;
        SQLiteCursor sQLiteCursor = null;
        try {
            try {
                queryFinalized = this.database.queryFinalized("SELECT info, pinned, online, inviter FROM chat_settings_v2 WHERE uid = " + chatParticipants.chat_id, new Object[0]);
            } catch (Exception e) {
                e = e;
            }
        } catch (Throwable th) {
            th = th;
        }
        try {
            new ArrayList();
            if (!queryFinalized.next() || (byteBufferValue = queryFinalized.byteBufferValue(0)) == null) {
                chatFull = null;
            } else {
                chatFull = TLRPC.ChatFull.TLdeserialize(byteBufferValue, byteBufferValue.readInt32(false), false);
                byteBufferValue.reuse();
                chatFull.pinned_msg_id = queryFinalized.intValue(1);
                chatFull.online_count = queryFinalized.intValue(2);
                chatFull.inviterId = queryFinalized.longValue(3);
            }
            queryFinalized.dispose();
            if (chatFull instanceof TLRPC.TL_chatFull) {
                chatFull.participants = chatParticipants;
                AndroidUtilities.runOnUIThread(new Runnable() {
                    @Override
                    public final void run() {
                        MessagesStorage.this.lambda$updateChatParticipants$115(chatFull);
                    }
                });
                SQLitePreparedStatement executeFast = this.database.executeFast("REPLACE INTO chat_settings_v2 VALUES(?, ?, ?, ?, ?, ?, ?)");
                NativeByteBuffer nativeByteBuffer = new NativeByteBuffer(chatFull.getObjectSize());
                chatFull.serializeToStream(nativeByteBuffer);
                executeFast.bindLong(1, chatFull.id);
                executeFast.bindByteBuffer(2, nativeByteBuffer);
                executeFast.bindInteger(3, chatFull.pinned_msg_id);
                executeFast.bindInteger(4, chatFull.online_count);
                executeFast.bindLong(5, chatFull.inviterId);
                executeFast.bindInteger(6, chatFull.invitesCount);
                executeFast.bindInteger(7, chatFull.participants_count);
                executeFast.step();
                executeFast.dispose();
                nativeByteBuffer.reuse();
            }
        } catch (Exception e2) {
            e = e2;
            sQLiteCursor = queryFinalized;
            checkSQLException(e);
            if (sQLiteCursor != null) {
                sQLiteCursor.dispose();
            }
        } catch (Throwable th2) {
            th = th2;
            sQLiteCursor = queryFinalized;
            if (sQLiteCursor != null) {
                sQLiteCursor.dispose();
            }
            throw th;
        }
    }

    public void lambda$updateDbToLastVersion$3() {
        this.databaseMigrationInProgress = true;
        NotificationCenter.getInstance(this.currentAccount).lambda$postNotificationNameOnUIThread$1(NotificationCenter.onDatabaseMigration, Boolean.TRUE);
    }

    public void lambda$updateDbToLastVersion$4() {
        this.databaseMigrationInProgress = false;
        NotificationCenter.getInstance(this.currentAccount).lambda$postNotificationNameOnUIThread$1(NotificationCenter.onDatabaseMigration, Boolean.FALSE);
    }

    public void lambda$updateDialogData$222(org.telegram.tgnet.TLRPC.Dialog r7) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MessagesStorage.lambda$updateDialogData$222(org.telegram.tgnet.TLRPC$Dialog):void");
    }

    public void lambda$updateDialogUnreadReactions$244(boolean r17, long r18, int r20, long r21) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MessagesStorage.lambda$updateDialogUnreadReactions$244(boolean, long, int, long):void");
    }

    public void lambda$updateDialogsWithReadMessages$114(LongSparseIntArray longSparseIntArray, LongSparseIntArray longSparseIntArray2, LongSparseArray longSparseArray, LongSparseIntArray longSparseIntArray3) {
        updateDialogsWithReadMessagesInternal(null, longSparseIntArray, longSparseIntArray2, longSparseArray, longSparseIntArray3);
    }

    public void lambda$updateEncryptedChat$167(org.telegram.tgnet.TLRPC.EncryptedChat r11) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MessagesStorage.lambda$updateEncryptedChat$167(org.telegram.tgnet.TLRPC$EncryptedChat):void");
    }

    public void lambda$updateEncryptedChatLayer$166(TLRPC.EncryptedChat encryptedChat) {
        SQLitePreparedStatement sQLitePreparedStatement = null;
        try {
            try {
                sQLitePreparedStatement = this.database.executeFast("UPDATE enc_chats SET layer = ? WHERE uid = ?");
                sQLitePreparedStatement.bindInteger(1, encryptedChat.layer);
                sQLitePreparedStatement.bindInteger(2, encryptedChat.id);
                sQLitePreparedStatement.step();
            } catch (Exception e) {
                checkSQLException(e);
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

    public void lambda$updateEncryptedChatSeq$164(TLRPC.EncryptedChat encryptedChat, boolean z) {
        SQLitePreparedStatement sQLitePreparedStatement = null;
        try {
            try {
                sQLitePreparedStatement = this.database.executeFast("UPDATE enc_chats SET seq_in = ?, seq_out = ?, use_count = ?, in_seq_no = ?, mtproto_seq = ? WHERE uid = ?");
                sQLitePreparedStatement.bindInteger(1, encryptedChat.seq_in);
                sQLitePreparedStatement.bindInteger(2, encryptedChat.seq_out);
                sQLitePreparedStatement.bindInteger(3, (encryptedChat.key_use_count_in << 16) | encryptedChat.key_use_count_out);
                sQLitePreparedStatement.bindInteger(4, encryptedChat.in_seq_no);
                sQLitePreparedStatement.bindInteger(5, encryptedChat.mtproto_seq);
                sQLitePreparedStatement.bindInteger(6, encryptedChat.id);
                sQLitePreparedStatement.step();
                if (z && encryptedChat.in_seq_no != 0) {
                    long encryptedChatId = DialogObject.getEncryptedChatId(encryptedChat.id);
                    this.database.executeFast(String.format(Locale.US, "DELETE FROM messages_v2 WHERE mid IN (SELECT m.mid FROM messages_v2 as m LEFT JOIN messages_seq as s ON m.mid = s.mid WHERE m.uid = %d AND m.date = 0 AND m.mid < 0 AND s.seq_out <= %d) AND uid = %d", Long.valueOf(encryptedChatId), Integer.valueOf(encryptedChat.in_seq_no), Long.valueOf(encryptedChatId))).stepThis().dispose();
                }
            } catch (Exception e) {
                checkSQLException(e);
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

    public void lambda$updateEncryptedChatTTL$165(TLRPC.EncryptedChat encryptedChat) {
        SQLitePreparedStatement sQLitePreparedStatement = null;
        try {
            try {
                sQLitePreparedStatement = this.database.executeFast("UPDATE enc_chats SET ttl = ? WHERE uid = ?");
                sQLitePreparedStatement.bindInteger(1, encryptedChat.ttl);
                sQLitePreparedStatement.bindInteger(2, encryptedChat.id);
                sQLitePreparedStatement.step();
            } catch (Exception e) {
                checkSQLException(e);
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

    public void lambda$updateFiltersReadCounter$113() {
        ArrayList<MessagesController.DialogFilter> arrayList = getMessagesController().dialogFilters;
        int size = arrayList.size();
        for (int i = 0; i < size; i++) {
            arrayList.get(i).unreadCount = arrayList.get(i).pendingUnreadCount;
        }
        this.mainUnreadCount = this.pendingMainUnreadCount;
        this.archiveUnreadCount = this.pendingArchiveUnreadCount;
    }

    public void lambda$updateMessageCustomParams$104(TLRPC.Message message, long j) {
        SQLiteDatabase sQLiteDatabase;
        String str;
        SQLitePreparedStatement sQLitePreparedStatement = null;
        try {
            try {
                this.database.beginTransaction();
                TLRPC.Message messageWithCustomParamsOnlyInternal = getMessageWithCustomParamsOnlyInternal(message.id, j);
                MessageCustomParamsHelper.copyParams(message, messageWithCustomParamsOnlyInternal);
                for (int i = 0; i < 2; i++) {
                    if (i == 0) {
                        sQLiteDatabase = this.database;
                        str = "UPDATE messages_v2 SET custom_params = ? WHERE mid = ? AND uid = ?";
                    } else {
                        sQLiteDatabase = this.database;
                        str = "UPDATE messages_topics SET custom_params = ? WHERE mid = ? AND uid = ?";
                    }
                    SQLitePreparedStatement executeFast = sQLiteDatabase.executeFast(str);
                    try {
                        executeFast.requery();
                        NativeByteBuffer writeLocalParams = MessageCustomParamsHelper.writeLocalParams(messageWithCustomParamsOnlyInternal);
                        if (writeLocalParams != null) {
                            executeFast.bindByteBuffer(1, writeLocalParams);
                        } else {
                            executeFast.bindNull(1);
                        }
                        executeFast.bindInteger(2, message.id);
                        executeFast.bindLong(3, j);
                        executeFast.step();
                        executeFast.dispose();
                        if (writeLocalParams != null) {
                            writeLocalParams.reuse();
                        }
                    } catch (Exception e) {
                        e = e;
                        sQLitePreparedStatement = executeFast;
                        checkSQLException(e);
                        SQLiteDatabase sQLiteDatabase2 = this.database;
                        if (sQLiteDatabase2 != null) {
                            sQLiteDatabase2.commitTransaction();
                        }
                        if (sQLitePreparedStatement != null) {
                            sQLitePreparedStatement.dispose();
                            return;
                        }
                        return;
                    } catch (Throwable th) {
                        th = th;
                        sQLitePreparedStatement = executeFast;
                        SQLiteDatabase sQLiteDatabase3 = this.database;
                        if (sQLiteDatabase3 != null) {
                            sQLiteDatabase3.commitTransaction();
                        }
                        if (sQLitePreparedStatement != null) {
                            sQLitePreparedStatement.dispose();
                        }
                        throw th;
                    }
                }
                this.database.commitTransaction();
                SQLiteDatabase sQLiteDatabase4 = this.database;
                if (sQLiteDatabase4 != null) {
                    sQLiteDatabase4.commitTransaction();
                }
            } catch (Exception e2) {
                e = e2;
            }
        } catch (Throwable th2) {
            th = th2;
        }
    }

    public void lambda$updateMessagePollResults$95(long r23, org.telegram.tgnet.TLRPC.Poll r25, org.telegram.tgnet.TLRPC.PollResults r26) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MessagesStorage.lambda$updateMessagePollResults$95(long, org.telegram.tgnet.TLRPC$Poll, org.telegram.tgnet.TLRPC$PollResults):void");
    }

    public void lambda$updateMessageReactions$98(int r22, long r23, org.telegram.tgnet.TLRPC.TL_messageReactions r25) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MessagesStorage.lambda$updateMessageReactions$98(int, long, org.telegram.tgnet.TLRPC$TL_messageReactions):void");
    }

    public void lambda$updateMessageStateAndIdInternal$196(TLRPC.TL_updates tL_updates) {
        getMessagesController().processUpdates(tL_updates, false);
    }

    public void lambda$updateMessageVerifyFlags$188(ArrayList arrayList) {
        SQLiteDatabase sQLiteDatabase;
        SQLiteDatabase sQLiteDatabase2;
        boolean z = false;
        SQLitePreparedStatement sQLitePreparedStatement = null;
        try {
            try {
                this.database.beginTransaction();
                try {
                    SQLitePreparedStatement executeFast = this.database.executeFast("UPDATE messages_v2 SET imp = ? WHERE mid = ? AND uid = ?");
                    try {
                        int size = arrayList.size();
                        for (int i = 0; i < size; i++) {
                            TLRPC.Message message = (TLRPC.Message) arrayList.get(i);
                            executeFast.requery();
                            int i2 = message.stickerVerified;
                            executeFast.bindInteger(1, i2 == 0 ? 1 : i2 == 2 ? 2 : 0);
                            executeFast.bindInteger(2, message.id);
                            executeFast.bindLong(3, MessageObject.getDialogId(message));
                            executeFast.step();
                        }
                        executeFast.dispose();
                        this.database.commitTransaction();
                    } catch (Exception e) {
                        e = e;
                        sQLitePreparedStatement = executeFast;
                        z = true;
                        checkSQLException(e);
                        if (z && (sQLiteDatabase2 = this.database) != null) {
                            sQLiteDatabase2.commitTransaction();
                        }
                        if (sQLitePreparedStatement != null) {
                            sQLitePreparedStatement.dispose();
                        }
                    } catch (Throwable th) {
                        th = th;
                        sQLitePreparedStatement = executeFast;
                        z = true;
                        if (z && (sQLiteDatabase = this.database) != null) {
                            sQLiteDatabase.commitTransaction();
                        }
                        if (sQLitePreparedStatement != null) {
                            sQLitePreparedStatement.dispose();
                        }
                        throw th;
                    }
                } catch (Exception e2) {
                    e = e2;
                } catch (Throwable th2) {
                    th = th2;
                }
            } catch (Exception e3) {
                e = e3;
            }
        } catch (Throwable th3) {
            th = th3;
        }
    }

    public void lambda$updateMessageVoiceTranscription$102(int i, long j, boolean z, long j2, String str) {
        TLRPC.Message messageWithCustomParamsOnlyInternal;
        SQLitePreparedStatement executeFast;
        SQLitePreparedStatement sQLitePreparedStatement = null;
        try {
            try {
                this.database.beginTransaction();
                messageWithCustomParamsOnlyInternal = getMessageWithCustomParamsOnlyInternal(i, j);
                messageWithCustomParamsOnlyInternal.voiceTranscriptionFinal = z;
                messageWithCustomParamsOnlyInternal.voiceTranscriptionId = j2;
                messageWithCustomParamsOnlyInternal.voiceTranscription = str;
                executeFast = this.database.executeFast("UPDATE messages_v2 SET custom_params = ? WHERE mid = ? AND uid = ?");
            } catch (Exception e) {
                e = e;
            }
        } catch (Throwable th) {
            th = th;
        }
        try {
            executeFast.requery();
            NativeByteBuffer writeLocalParams = MessageCustomParamsHelper.writeLocalParams(messageWithCustomParamsOnlyInternal);
            if (writeLocalParams != null) {
                executeFast.bindByteBuffer(1, writeLocalParams);
            } else {
                executeFast.bindNull(1);
            }
            executeFast.bindInteger(2, i);
            executeFast.bindLong(3, j);
            executeFast.step();
            executeFast.dispose();
            this.database.commitTransaction();
            if (writeLocalParams != null) {
                writeLocalParams.reuse();
            }
            SQLiteDatabase sQLiteDatabase = this.database;
            if (sQLiteDatabase != null) {
                sQLiteDatabase.commitTransaction();
            }
        } catch (Exception e2) {
            e = e2;
            sQLitePreparedStatement = executeFast;
            checkSQLException(e);
            SQLiteDatabase sQLiteDatabase2 = this.database;
            if (sQLiteDatabase2 != null) {
                sQLiteDatabase2.commitTransaction();
            }
            if (sQLitePreparedStatement != null) {
                sQLitePreparedStatement.dispose();
            }
        } catch (Throwable th2) {
            th = th2;
            sQLitePreparedStatement = executeFast;
            SQLiteDatabase sQLiteDatabase3 = this.database;
            if (sQLiteDatabase3 != null) {
                sQLiteDatabase3.commitTransaction();
            }
            if (sQLitePreparedStatement != null) {
                sQLitePreparedStatement.dispose();
            }
            throw th;
        }
    }

    public void lambda$updateMessageVoiceTranscription$103(int i, long j, TLRPC.Message message, String str) {
        SQLiteDatabase sQLiteDatabase;
        String str2;
        SQLitePreparedStatement sQLitePreparedStatement = null;
        try {
            try {
                this.database.beginTransaction();
                TLRPC.Message messageWithCustomParamsOnlyInternal = getMessageWithCustomParamsOnlyInternal(i, j);
                messageWithCustomParamsOnlyInternal.voiceTranscriptionOpen = message.voiceTranscriptionOpen;
                messageWithCustomParamsOnlyInternal.voiceTranscriptionRated = message.voiceTranscriptionRated;
                messageWithCustomParamsOnlyInternal.voiceTranscriptionFinal = message.voiceTranscriptionFinal;
                messageWithCustomParamsOnlyInternal.voiceTranscriptionForce = message.voiceTranscriptionForce;
                messageWithCustomParamsOnlyInternal.voiceTranscriptionId = message.voiceTranscriptionId;
                messageWithCustomParamsOnlyInternal.voiceTranscription = str;
                for (int i2 = 0; i2 < 2; i2++) {
                    if (i2 == 0) {
                        sQLiteDatabase = this.database;
                        str2 = "UPDATE messages_v2 SET custom_params = ? WHERE mid = ? AND uid = ?";
                    } else {
                        sQLiteDatabase = this.database;
                        str2 = "UPDATE messages_topics SET custom_params = ? WHERE mid = ? AND uid = ?";
                    }
                    SQLitePreparedStatement executeFast = sQLiteDatabase.executeFast(str2);
                    try {
                        executeFast.requery();
                        NativeByteBuffer writeLocalParams = MessageCustomParamsHelper.writeLocalParams(messageWithCustomParamsOnlyInternal);
                        if (writeLocalParams != null) {
                            executeFast.bindByteBuffer(1, writeLocalParams);
                        } else {
                            executeFast.bindNull(1);
                        }
                        executeFast.bindInteger(2, i);
                        executeFast.bindLong(3, j);
                        executeFast.step();
                        executeFast.dispose();
                        this.database.commitTransaction();
                        if (writeLocalParams != null) {
                            writeLocalParams.reuse();
                        }
                    } catch (Exception e) {
                        e = e;
                        sQLitePreparedStatement = executeFast;
                        checkSQLException(e);
                        SQLiteDatabase sQLiteDatabase2 = this.database;
                        if (sQLiteDatabase2 != null) {
                            sQLiteDatabase2.commitTransaction();
                        }
                        if (sQLitePreparedStatement != null) {
                            sQLitePreparedStatement.dispose();
                            return;
                        }
                        return;
                    } catch (Throwable th) {
                        th = th;
                        sQLitePreparedStatement = executeFast;
                        SQLiteDatabase sQLiteDatabase3 = this.database;
                        if (sQLiteDatabase3 != null) {
                            sQLiteDatabase3.commitTransaction();
                        }
                        if (sQLitePreparedStatement != null) {
                            sQLitePreparedStatement.dispose();
                        }
                        throw th;
                    }
                }
                SQLiteDatabase sQLiteDatabase4 = this.database;
                if (sQLiteDatabase4 != null) {
                    sQLiteDatabase4.commitTransaction();
                }
            } catch (Throwable th2) {
                th = th2;
            }
        } catch (Exception e2) {
            e = e2;
        }
    }

    public void lambda$updateMessageVoiceTranscriptionOpen$101(int i, long j, TLRPC.Message message) {
        SQLiteDatabase sQLiteDatabase;
        String str;
        SQLitePreparedStatement sQLitePreparedStatement = null;
        try {
            try {
                this.database.beginTransaction();
                TLRPC.Message messageWithCustomParamsOnlyInternal = getMessageWithCustomParamsOnlyInternal(i, j);
                messageWithCustomParamsOnlyInternal.voiceTranscriptionOpen = message.voiceTranscriptionOpen;
                messageWithCustomParamsOnlyInternal.voiceTranscriptionRated = message.voiceTranscriptionRated;
                messageWithCustomParamsOnlyInternal.voiceTranscriptionFinal = message.voiceTranscriptionFinal;
                messageWithCustomParamsOnlyInternal.voiceTranscriptionForce = message.voiceTranscriptionForce;
                messageWithCustomParamsOnlyInternal.voiceTranscriptionId = message.voiceTranscriptionId;
                for (int i2 = 0; i2 < 2; i2++) {
                    if (i2 == 0) {
                        sQLiteDatabase = this.database;
                        str = "UPDATE messages_v2 SET custom_params = ? WHERE mid = ? AND uid = ?";
                    } else {
                        sQLiteDatabase = this.database;
                        str = "UPDATE messages_topics SET custom_params = ? WHERE mid = ? AND uid = ?";
                    }
                    SQLitePreparedStatement executeFast = sQLiteDatabase.executeFast(str);
                    try {
                        executeFast.requery();
                        NativeByteBuffer writeLocalParams = MessageCustomParamsHelper.writeLocalParams(messageWithCustomParamsOnlyInternal);
                        if (writeLocalParams != null) {
                            executeFast.bindByteBuffer(1, writeLocalParams);
                        } else {
                            executeFast.bindNull(1);
                        }
                        executeFast.bindInteger(2, i);
                        executeFast.bindLong(3, j);
                        executeFast.step();
                        executeFast.dispose();
                        if (writeLocalParams != null) {
                            writeLocalParams.reuse();
                        }
                    } catch (Exception e) {
                        e = e;
                        sQLitePreparedStatement = executeFast;
                        checkSQLException(e);
                        SQLiteDatabase sQLiteDatabase2 = this.database;
                        if (sQLiteDatabase2 != null) {
                            sQLiteDatabase2.commitTransaction();
                        }
                        if (sQLitePreparedStatement != null) {
                            sQLitePreparedStatement.dispose();
                            return;
                        }
                        return;
                    } catch (Throwable th) {
                        th = th;
                        sQLitePreparedStatement = executeFast;
                        SQLiteDatabase sQLiteDatabase3 = this.database;
                        if (sQLiteDatabase3 != null) {
                            sQLiteDatabase3.commitTransaction();
                        }
                        if (sQLitePreparedStatement != null) {
                            sQLitePreparedStatement.dispose();
                        }
                        throw th;
                    }
                }
                this.database.commitTransaction();
                SQLiteDatabase sQLiteDatabase4 = this.database;
                if (sQLiteDatabase4 != null) {
                    sQLiteDatabase4.commitTransaction();
                }
            } catch (Exception e2) {
                e = e2;
            }
        } catch (Throwable th2) {
            th = th2;
        }
    }

    public void lambda$updateMutedDialogsFiltersCounters$36() {
        resetAllUnreadCounters(true);
    }

    public void lambda$updatePinnedMessages$130(long j, ArrayList arrayList, HashMap hashMap, int i, int i2, boolean z) {
        getNotificationCenter().lambda$postNotificationNameOnUIThread$1(NotificationCenter.didLoadPinnedMessages, Long.valueOf(j), arrayList, Boolean.TRUE, null, hashMap, Integer.valueOf(i), Integer.valueOf(i2), Boolean.valueOf(z));
    }

    public void lambda$updatePinnedMessages$131(long j, ArrayList arrayList, HashMap hashMap, int i, int i2, boolean z) {
        getNotificationCenter().lambda$postNotificationNameOnUIThread$1(NotificationCenter.didLoadPinnedMessages, Long.valueOf(j), arrayList, Boolean.FALSE, null, hashMap, Integer.valueOf(i), Integer.valueOf(i2), Boolean.valueOf(z));
    }

    public void lambda$updatePinnedMessages$132(boolean r19, final java.util.HashMap r20, final int r21, final long r22, final java.util.ArrayList r24, int r25, boolean r26) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MessagesStorage.lambda$updatePinnedMessages$132(boolean, java.util.HashMap, int, long, java.util.ArrayList, int, boolean):void");
    }

    public void lambda$updateRepliesCount$187(int r17, long r18, int r20, java.util.ArrayList r21, int r22) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MessagesStorage.lambda$updateRepliesCount$187(int, long, int, java.util.ArrayList, int):void");
    }

    public void lambda$updateRepliesMaxReadIdInternal$185(long j, int i, int i2, int i3, int i4) {
        getMessagesController().getTopicsController().updateMaxReadId(j, i, i2, i3, i4);
    }

    public void lambda$updateTopicData$46(long j, TLRPC.TL_forumTopic tL_forumTopic, int i) {
        getMessagesController().getTopicsController().updateTopicInUi(j, tL_forumTopic, i);
    }

    public void lambda$updateTopicData$47(final int r19, final org.telegram.tgnet.TLRPC.TL_forumTopic r20, final long r21, int r23) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MessagesStorage.lambda$updateTopicData$47(int, org.telegram.tgnet.TLRPC$TL_forumTopic, long, int):void");
    }

    public void lambda$updateTopicsWithReadMessages$56(HashMap hashMap) {
        for (TopicKey topicKey : hashMap.keySet()) {
            Integer num = (Integer) hashMap.get(topicKey);
            num.intValue();
            try {
                this.database.executeFast(String.format(Locale.US, "UPDATE topics SET read_outbox = max((SELECT read_outbox FROM topics WHERE did = %d AND topic_id = %d), %d) WHERE did = %d AND topic_id = %d", Long.valueOf(topicKey.dialogId), Long.valueOf(topicKey.topicId), num, Long.valueOf(topicKey.dialogId), Long.valueOf(topicKey.topicId))).stepThis().dispose();
            } catch (SQLiteException e) {
                checkSQLException(e);
            }
        }
    }

    public void lambda$updateUnreadReactionsCount$242(long r10, boolean r12, long r13, int r15) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MessagesStorage.lambda$updateUnreadReactionsCount$242(long, boolean, long, int):void");
    }

    public void lambda$updateUserInfo$124(org.telegram.tgnet.TLRPC.UserFull r10, boolean r11) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MessagesStorage.lambda$updateUserInfo$124(org.telegram.tgnet.TLRPC$UserFull, boolean):void");
    }

    public void lambda$updateUserInfoPremiumBlocked$125(long j, boolean z) {
        SQLiteCursor sQLiteCursor;
        TLRPC.UserFull userFull;
        SQLitePreparedStatement sQLitePreparedStatement = null;
        try {
            sQLiteCursor = this.database.queryFinalized("SELECT uid, info, pinned FROM user_settings WHERE uid = " + j, new Object[0]);
            try {
                try {
                    boolean next = sQLiteCursor.next();
                    if (next) {
                        NativeByteBuffer byteBufferValue = sQLiteCursor.byteBufferValue(1);
                        userFull = TLRPC.UserFull.TLdeserialize(byteBufferValue, byteBufferValue.readInt32(true), true);
                        if (userFull != null) {
                            userFull.pinned_msg_id = sQLiteCursor.intValue(2);
                        }
                        byteBufferValue.reuse();
                    } else {
                        userFull = null;
                    }
                    sQLiteCursor.dispose();
                    if (next && userFull != null && userFull.contact_require_premium != z) {
                        userFull.contact_require_premium = z;
                        SQLitePreparedStatement executeFast = this.database.executeFast("REPLACE INTO user_settings VALUES(?, ?, ?)");
                        try {
                            NativeByteBuffer nativeByteBuffer = new NativeByteBuffer(userFull.getObjectSize());
                            userFull.serializeToStream(nativeByteBuffer);
                            executeFast.bindLong(1, j);
                            executeFast.bindByteBuffer(2, nativeByteBuffer);
                            executeFast.bindInteger(3, userFull.pinned_msg_id);
                            executeFast.step();
                            executeFast.dispose();
                            nativeByteBuffer.reuse();
                        } catch (Exception e) {
                            e = e;
                            sQLiteCursor = null;
                            sQLitePreparedStatement = executeFast;
                            checkSQLException(e);
                            if (sQLitePreparedStatement != null) {
                                sQLitePreparedStatement.dispose();
                            }
                            if (sQLiteCursor != null) {
                                sQLiteCursor.dispose();
                            }
                        } catch (Throwable th) {
                            th = th;
                            sQLiteCursor = null;
                            sQLitePreparedStatement = executeFast;
                            if (sQLitePreparedStatement != null) {
                                sQLitePreparedStatement.dispose();
                            }
                            if (sQLiteCursor != null) {
                                sQLiteCursor.dispose();
                            }
                            throw th;
                        }
                    }
                } catch (Exception e2) {
                    e = e2;
                }
            } catch (Throwable th2) {
                th = th2;
            }
        } catch (Exception e3) {
            e = e3;
            sQLiteCursor = null;
        } catch (Throwable th3) {
            th = th3;
            sQLiteCursor = null;
        }
    }

    private org.telegram.tgnet.TLRPC.ChatFull loadChatInfoInternal(long r22, boolean r24, boolean r25, boolean r26, int r27) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MessagesStorage.loadChatInfoInternal(long, boolean, boolean, boolean, int):org.telegram.tgnet.TLRPC$ChatFull");
    }

    private void loadDialogFilters() {
        this.storageQueue.postRunnable(new Runnable() {
            @Override
            public final void run() {
                MessagesStorage.this.lambda$loadDialogFilters$64();
            }
        });
    }

    private org.telegram.tgnet.TLRPC.messages_Dialogs loadDialogsByIds(java.lang.String r19, java.util.ArrayList<java.lang.Long> r20, java.util.ArrayList<java.lang.Long> r21, java.util.ArrayList<java.lang.Integer> r22) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MessagesStorage.loadDialogsByIds(java.lang.String, java.util.ArrayList, java.util.ArrayList, java.util.ArrayList):org.telegram.tgnet.TLRPC$messages_Dialogs");
    }

    private void loadPendingTasks() {
        this.storageQueue.postRunnable(new Runnable() {
            @Override
            public final void run() {
                MessagesStorage.this.lambda$loadPendingTasks$33();
            }
        });
    }

    public java.util.ArrayList<java.lang.Long> lambda$markMessagesAsDeleted$212(long r25, int r27, boolean r28) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MessagesStorage.lambda$markMessagesAsDeleted$212(long, int, boolean):java.util.ArrayList");
    }

    public java.util.ArrayList<java.lang.Long> lambda$markMessagesAsDeleted$210(long r46, java.util.ArrayList<java.lang.Integer> r48, boolean r49, int r50, int r51) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MessagesStorage.lambda$markMessagesAsDeleted$210(long, java.util.ArrayList, boolean, int, int):java.util.ArrayList");
    }

    public void lambda$markMessagesAsRead$200(org.telegram.messenger.support.LongSparseIntArray r19, org.telegram.messenger.support.LongSparseIntArray r20, android.util.SparseIntArray r21) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MessagesStorage.lambda$markMessagesAsRead$200(org.telegram.messenger.support.LongSparseIntArray, org.telegram.messenger.support.LongSparseIntArray, android.util.SparseIntArray):void");
    }

    private void markMessagesContentAsReadInternal(long j, ArrayList<Integer> arrayList, int i) {
        SQLiteCursor sQLiteCursor = null;
        ArrayList<Integer> arrayList2 = null;
        sQLiteCursor = null;
        try {
            try {
                String join = TextUtils.join(",", arrayList);
                SQLiteDatabase sQLiteDatabase = this.database;
                Locale locale = Locale.US;
                sQLiteDatabase.executeFast(String.format(locale, "UPDATE messages_v2 SET read_state = read_state | 2 WHERE mid IN (%s) AND uid = %d", join, Long.valueOf(j))).stepThis().dispose();
                if (i != 0) {
                    SQLiteCursor queryFinalized = this.database.queryFinalized(String.format(locale, "SELECT mid, ttl FROM messages_v2 WHERE mid IN (%s) AND uid = %d AND ttl > 0", join, Long.valueOf(j)), new Object[0]);
                    while (queryFinalized.next()) {
                        try {
                            if (arrayList2 == null) {
                                arrayList2 = new ArrayList<>();
                            }
                            arrayList2.add(Integer.valueOf(queryFinalized.intValue(0)));
                        } catch (Exception e) {
                            e = e;
                            sQLiteCursor = queryFinalized;
                            checkSQLException(e);
                            if (sQLiteCursor != null) {
                                sQLiteCursor.dispose();
                                return;
                            }
                            return;
                        } catch (Throwable th) {
                            th = th;
                            sQLiteCursor = queryFinalized;
                            if (sQLiteCursor != null) {
                                sQLiteCursor.dispose();
                            }
                            throw th;
                        }
                    }
                    if (arrayList2 != null) {
                        emptyMessagesMedia(j, arrayList2);
                    }
                    queryFinalized.dispose();
                }
            } catch (Exception e2) {
                e = e2;
            }
        } catch (Throwable th2) {
            th = th2;
        }
    }

    private void onReactionsUpdate(final long j, final TLRPC.TL_messageReactions tL_messageReactions, final TLRPC.TL_messageReactions tL_messageReactions2) {
        ArrayList<TLRPC.ReactionCount> arrayList;
        if (tL_messageReactions == null || (arrayList = tL_messageReactions.results) == null) {
            return;
        }
        if (arrayList.isEmpty() && tL_messageReactions2 != null && tL_messageReactions2.results.isEmpty()) {
            return;
        }
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                MessagesStorage.this.lambda$onReactionsUpdate$100(tL_messageReactions, tL_messageReactions2, j);
            }
        });
    }

    private void onReactionsUpdate(final ArrayList<SavedReactionsUpdate> arrayList) {
        if (arrayList == null || arrayList.isEmpty()) {
            return;
        }
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                MessagesStorage.this.lambda$onReactionsUpdate$99(arrayList);
            }
        });
    }

    public void lambda$processLoadedFilterPeers$68(TLRPC.messages_Dialogs messages_dialogs, TLRPC.messages_Dialogs messages_dialogs2, ArrayList<TLRPC.User> arrayList, ArrayList<TLRPC.Chat> arrayList2, ArrayList<MessagesController.DialogFilter> arrayList3, SparseArray<MessagesController.DialogFilter> sparseArray, ArrayList<Integer> arrayList4, HashMap<Integer, HashSet<Long>> hashMap, HashSet<Integer> hashSet, Runnable runnable) {
        putUsersAndChats(arrayList, arrayList2, true, false);
        int size = sparseArray.size();
        int i = 0;
        boolean z = false;
        while (i < size) {
            lambda$deleteDialogFilter$69(sparseArray.valueAt(i));
            i++;
            z = true;
        }
        Iterator<Integer> it = hashSet.iterator();
        while (it.hasNext()) {
            MessagesController.DialogFilter dialogFilter = this.dialogFiltersMap.get(it.next().intValue());
            if (dialogFilter != null) {
                dialogFilter.pendingUnreadCount = -1;
            }
        }
        for (Map.Entry<Integer, HashSet<Long>> entry : hashMap.entrySet()) {
            MessagesController.DialogFilter dialogFilter2 = this.dialogFiltersMap.get(entry.getKey().intValue());
            if (dialogFilter2 != null) {
                Iterator<Long> it2 = entry.getValue().iterator();
                while (it2.hasNext()) {
                    dialogFilter2.pinnedDialogs.delete(it2.next().longValue());
                }
                z = true;
            }
        }
        int size2 = arrayList3.size();
        int i2 = 0;
        while (i2 < size2) {
            saveDialogFilterInternal(arrayList3.get(i2), false, true);
            i2++;
            z = true;
        }
        int size3 = this.dialogFilters.size();
        boolean z2 = false;
        for (int i3 = 0; i3 < size3; i3++) {
            MessagesController.DialogFilter dialogFilter3 = this.dialogFilters.get(i3);
            int indexOf = arrayList4.indexOf(Integer.valueOf(dialogFilter3.id));
            if (dialogFilter3.order != indexOf) {
                dialogFilter3.order = indexOf;
                z2 = true;
                z = true;
            }
        }
        if (z2) {
            Collections.sort(this.dialogFilters, new Comparator() {
                @Override
                public final int compare(Object obj, Object obj2) {
                    int lambda$processLoadedFilterPeersInternal$67;
                    lambda$processLoadedFilterPeersInternal$67 = MessagesStorage.lambda$processLoadedFilterPeersInternal$67((MessagesController.DialogFilter) obj, (MessagesController.DialogFilter) obj2);
                    return lambda$processLoadedFilterPeersInternal$67;
                }
            });
            saveDialogFiltersOrderInternal();
        }
        int i4 = z ? 1 : 2;
        calcUnreadCounters(true);
        getMessagesController().processLoadedDialogFilters(new ArrayList<>(this.dialogFilters), messages_dialogs, messages_dialogs2, arrayList, arrayList2, null, i4, runnable);
    }

    private void putChatsInternal(List<TLRPC.Chat> list) {
        int i;
        if (list == null || list.isEmpty()) {
            return;
        }
        SQLitePreparedStatement executeFast = this.database.executeFast("REPLACE INTO chats VALUES(?, ?, ?)");
        for (int i2 = 0; i2 < list.size(); i2++) {
            TLRPC.Chat chat = list.get(i2);
            if (chat.min) {
                SQLiteCursor queryFinalized = this.database.queryFinalized(String.format(Locale.US, "SELECT data FROM chats WHERE uid = %d", Long.valueOf(chat.id)), new Object[0]);
                if (queryFinalized.next()) {
                    try {
                        NativeByteBuffer byteBufferValue = queryFinalized.byteBufferValue(0);
                        if (byteBufferValue != null) {
                            TLRPC.Chat TLdeserialize = TLRPC.Chat.TLdeserialize(byteBufferValue, byteBufferValue.readInt32(false), false);
                            byteBufferValue.reuse();
                            if (TLdeserialize != null) {
                                TLdeserialize.title = chat.title;
                                TLdeserialize.photo = chat.photo;
                                TLdeserialize.broadcast = chat.broadcast;
                                TLdeserialize.verified = chat.verified;
                                TLdeserialize.megagroup = chat.megagroup;
                                TLdeserialize.call_not_empty = chat.call_not_empty;
                                TLdeserialize.call_active = chat.call_active;
                                TLRPC.TL_chatBannedRights tL_chatBannedRights = chat.default_banned_rights;
                                if (tL_chatBannedRights != null) {
                                    TLdeserialize.default_banned_rights = tL_chatBannedRights;
                                    TLdeserialize.flags |= 262144;
                                }
                                TLRPC.TL_chatAdminRights tL_chatAdminRights = chat.admin_rights;
                                if (tL_chatAdminRights != null) {
                                    TLdeserialize.admin_rights = tL_chatAdminRights;
                                    TLdeserialize.flags |= 16384;
                                }
                                TLRPC.TL_chatBannedRights tL_chatBannedRights2 = chat.banned_rights;
                                if (tL_chatBannedRights2 != null) {
                                    TLdeserialize.banned_rights = tL_chatBannedRights2;
                                    TLdeserialize.flags |= 32768;
                                }
                                String str = chat.username;
                                if (str != null) {
                                    TLdeserialize.username = str;
                                    i = TLdeserialize.flags | 64;
                                } else {
                                    TLdeserialize.username = null;
                                    i = TLdeserialize.flags & (-65);
                                }
                                TLdeserialize.flags = i;
                                int i3 = chat.participants_count;
                                if (i3 > 0) {
                                    TLdeserialize.participants_count = i3;
                                }
                                chat = TLdeserialize;
                            }
                        }
                    } catch (Exception e) {
                        FileLog.e(e);
                    }
                }
                queryFinalized.dispose();
            }
            executeFast.requery();
            chat.flags |= 131072;
            NativeByteBuffer nativeByteBuffer = new NativeByteBuffer(chat.getObjectSize());
            chat.serializeToStream(nativeByteBuffer);
            executeFast.bindLong(1, chat.id);
            String str2 = chat.title;
            executeFast.bindString(2, str2 != null ? str2.toLowerCase() : "");
            executeFast.bindByteBuffer(3, nativeByteBuffer);
            executeFast.step();
            nativeByteBuffer.reuse();
            this.dialogIsForum.put(-chat.id, chat.forum ? 1 : 0);
        }
        executeFast.dispose();
    }

    private void putDialogsInternal(org.telegram.tgnet.TLRPC.messages_Dialogs r36, int r37) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MessagesStorage.putDialogsInternal(org.telegram.tgnet.TLRPC$messages_Dialogs, int):void");
    }

    public void lambda$putMessages$193(java.util.ArrayList<org.telegram.tgnet.TLRPC.Message> r60, boolean r61, boolean r62, int r63, boolean r64, int r65, long r66) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MessagesStorage.lambda$putMessages$193(java.util.ArrayList, boolean, boolean, int, boolean, int, long):void");
    }

    public void lambda$putUsersAndChats$174(List<TLRPC.User> list, List<TLRPC.Chat> list2, boolean z) {
        SQLiteDatabase sQLiteDatabase;
        if (z) {
            try {
                try {
                    this.database.beginTransaction();
                } catch (Exception e) {
                    checkSQLException(e);
                    sQLiteDatabase = this.database;
                    if (sQLiteDatabase == null) {
                        return;
                    }
                }
            } catch (Throwable th) {
                SQLiteDatabase sQLiteDatabase2 = this.database;
                if (sQLiteDatabase2 != null) {
                    sQLiteDatabase2.commitTransaction();
                }
                throw th;
            }
        }
        putUsersInternal(list);
        putChatsInternal(list2);
        sQLiteDatabase = this.database;
        if (sQLiteDatabase == null) {
            return;
        }
        sQLiteDatabase.commitTransaction();
    }

    private void putUsersInternal(List<TLRPC.User> list) {
        int i;
        int i2;
        int i3;
        if (list == null || list.isEmpty()) {
            return;
        }
        SQLitePreparedStatement executeFast = this.database.executeFast("REPLACE INTO users VALUES(?, ?, ?, ?)");
        for (int i4 = 0; i4 < list.size(); i4++) {
            TLRPC.User user = list.get(i4);
            if (user != null) {
                if (user.min) {
                    SQLiteCursor queryFinalized = this.database.queryFinalized(String.format(Locale.US, "SELECT data FROM users WHERE uid = %d", Long.valueOf(user.id)), new Object[0]);
                    if (queryFinalized.next()) {
                        try {
                            NativeByteBuffer byteBufferValue = queryFinalized.byteBufferValue(0);
                            if (byteBufferValue != null) {
                                TLRPC.User TLdeserialize = TLRPC.User.TLdeserialize(byteBufferValue, byteBufferValue.readInt32(false), false);
                                byteBufferValue.reuse();
                                if (TLdeserialize != null) {
                                    String str = user.username;
                                    if (str != null) {
                                        TLdeserialize.username = str;
                                        i2 = TLdeserialize.flags | 8;
                                    } else {
                                        TLdeserialize.username = null;
                                        i2 = TLdeserialize.flags & (-9);
                                    }
                                    TLdeserialize.flags = i2;
                                    if (user.apply_min_photo) {
                                        TLRPC.UserProfilePhoto userProfilePhoto = user.photo;
                                        if (userProfilePhoto != null) {
                                            TLdeserialize.photo = userProfilePhoto;
                                            i3 = i2 | 32;
                                        } else {
                                            TLdeserialize.photo = null;
                                            i3 = i2 & (-33);
                                        }
                                        TLdeserialize.flags = i3;
                                    }
                                    user = TLdeserialize;
                                }
                            }
                        } catch (Exception e) {
                            checkSQLException(e);
                        }
                    }
                    queryFinalized.dispose();
                }
                executeFast.requery();
                NativeByteBuffer nativeByteBuffer = new NativeByteBuffer(user.getObjectSize());
                user.serializeToStream(nativeByteBuffer);
                executeFast.bindLong(1, user.id);
                executeFast.bindString(2, formatUserSearchName(user));
                TLRPC.UserStatus userStatus = user.status;
                if (userStatus != null) {
                    if (userStatus instanceof TLRPC.TL_userStatusRecently) {
                        i = userStatus.by_me ? -1000 : -100;
                    } else if (userStatus instanceof TLRPC.TL_userStatusLastWeek) {
                        i = userStatus.by_me ? -1001 : -101;
                    } else {
                        if (userStatus instanceof TLRPC.TL_userStatusLastMonth) {
                            i = userStatus.by_me ? -1002 : -102;
                        }
                        executeFast.bindInteger(3, userStatus.expires);
                    }
                    userStatus.expires = i;
                    executeFast.bindInteger(3, userStatus.expires);
                } else {
                    executeFast.bindInteger(3, 0);
                }
                executeFast.bindByteBuffer(4, nativeByteBuffer);
                executeFast.step();
                nativeByteBuffer.reuse();
            }
        }
        executeFast.dispose();
    }

    private boolean recoverDatabase() {
        this.database.close();
        boolean recoverDatabase = DatabaseMigrationHelper.recoverDatabase(this.cacheFile, this.walCacheFile, this.shmCacheFile, this.currentAccount);
        FileLog.e("Database restored = " + recoverDatabase);
        if (recoverDatabase) {
            try {
                SQLiteDatabase sQLiteDatabase = new SQLiteDatabase(this.cacheFile.getPath());
                this.database = sQLiteDatabase;
                sQLiteDatabase.executeFast("PRAGMA secure_delete = ON").stepThis().dispose();
                this.database.executeFast("PRAGMA temp_store = MEMORY").stepThis().dispose();
                this.database.executeFast("PRAGMA journal_mode = WAL").stepThis().dispose();
                this.database.executeFast("PRAGMA journal_size_limit = 10485760").stepThis().dispose();
            } catch (SQLiteException e) {
                FileLog.e(new Exception(e));
                recoverDatabase = false;
            }
        }
        if (!recoverDatabase) {
            cleanupInternal(true);
            openDatabase(1);
            recoverDatabase = this.databaseCreated;
            FileLog.e("Try create new database = " + recoverDatabase);
        }
        if (recoverDatabase) {
            reset();
        }
        return recoverDatabase;
    }

    private void resetForumBadgeIfNeed(long j) {
        LongSparseIntArray longSparseIntArray;
        SQLiteCursor sQLiteCursor = null;
        try {
            SQLiteDatabase sQLiteDatabase = this.database;
            Locale locale = Locale.ENGLISH;
            SQLiteCursor queryFinalized = sQLiteDatabase.queryFinalized(String.format(locale, "SELECT topic_id FROM topics WHERE did = %d AND unread_count > 0", Long.valueOf(j)), new Object[0]);
            try {
                if (queryFinalized.next()) {
                    longSparseIntArray = null;
                } else {
                    longSparseIntArray = new LongSparseIntArray();
                    longSparseIntArray.put(j, 0);
                }
                queryFinalized.dispose();
                if (longSparseIntArray != null) {
                    this.database.executeFast(String.format(locale, "UPDATE dialogs SET unread_count = 0, unread_count_i = 0 WHERE did = %d", Long.valueOf(j))).stepThis().dispose();
                }
                updateFiltersReadCounter(longSparseIntArray, null, true);
                getMessagesController().processDialogsUpdateRead(longSparseIntArray, null);
            } catch (Throwable th) {
                th = th;
                sQLiteCursor = queryFinalized;
                try {
                    checkSQLException(th);
                } finally {
                    if (sQLiteCursor != null) {
                        sQLiteCursor.dispose();
                    }
                }
            }
        } catch (Throwable th2) {
            th = th2;
        }
    }

    private void saveDialogFilterInternal(MessagesController.DialogFilter dialogFilter, boolean z, boolean z2) {
        int i;
        SQLitePreparedStatement executeFast;
        SQLitePreparedStatement sQLitePreparedStatement = null;
        try {
            try {
                if (!this.dialogFilters.contains(dialogFilter)) {
                    if (!z) {
                        this.dialogFilters.add(dialogFilter);
                    } else if (this.dialogFilters.get(0).isDefault()) {
                        this.dialogFilters.add(1, dialogFilter);
                    } else {
                        this.dialogFilters.add(0, dialogFilter);
                    }
                    this.dialogFiltersMap.put(dialogFilter.id, dialogFilter);
                }
                executeFast = this.database.executeFast("REPLACE INTO dialog_filter VALUES(?, ?, ?, ?, ?, ?, ?, ?)");
            } catch (Throwable th) {
                th = th;
            }
        } catch (Exception e) {
            e = e;
        }
        try {
            executeFast.bindInteger(1, dialogFilter.id);
            executeFast.bindInteger(2, dialogFilter.order);
            executeFast.bindInteger(3, dialogFilter.unreadCount);
            executeFast.bindInteger(4, dialogFilter.flags);
            executeFast.bindString(5, dialogFilter.id == 0 ? "ALL_CHATS" : dialogFilter.name);
            executeFast.bindInteger(6, dialogFilter.color);
            Vector vector = new Vector(new MessagesStorage$$ExternalSyntheticLambda42());
            vector.objects.addAll(dialogFilter.entities);
            NativeByteBuffer nativeByteBuffer = new NativeByteBuffer(vector.getObjectSize());
            vector.serializeToStream(nativeByteBuffer);
            executeFast.bindByteBuffer(7, nativeByteBuffer);
            executeFast.bindInteger(8, dialogFilter.title_noanimate ? 1 : 0);
            executeFast.step();
            executeFast.dispose();
            nativeByteBuffer.reuse();
            if (z2) {
                this.database.executeFast("DELETE FROM dialog_filter_ep WHERE id = " + dialogFilter.id).stepThis().dispose();
                this.database.executeFast("DELETE FROM dialog_filter_pin_v2 WHERE id = " + dialogFilter.id).stepThis().dispose();
                this.database.beginTransaction();
                SQLitePreparedStatement executeFast2 = this.database.executeFast("REPLACE INTO dialog_filter_pin_v2 VALUES(?, ?, ?)");
                int size = dialogFilter.alwaysShow.size();
                for (int i2 = 0; i2 < size; i2++) {
                    long longValue = dialogFilter.alwaysShow.get(i2).longValue();
                    executeFast2.requery();
                    executeFast2.bindInteger(1, dialogFilter.id);
                    executeFast2.bindLong(2, longValue);
                    executeFast2.bindInteger(3, dialogFilter.pinnedDialogs.get(longValue, Integer.MIN_VALUE));
                    executeFast2.step();
                }
                int size2 = dialogFilter.pinnedDialogs.size();
                for (int i3 = 0; i3 < size2; i3++) {
                    long keyAt = dialogFilter.pinnedDialogs.keyAt(i3);
                    if (DialogObject.isEncryptedDialog(keyAt)) {
                        executeFast2.requery();
                        executeFast2.bindInteger(1, dialogFilter.id);
                        executeFast2.bindLong(2, keyAt);
                        executeFast2.bindInteger(3, dialogFilter.pinnedDialogs.valueAt(i3));
                        executeFast2.step();
                    }
                }
                executeFast2.dispose();
                executeFast = this.database.executeFast("REPLACE INTO dialog_filter_ep VALUES(?, ?)");
                int size3 = dialogFilter.neverShow.size();
                for (i = 0; i < size3; i++) {
                    executeFast.requery();
                    executeFast.bindInteger(1, dialogFilter.id);
                    executeFast.bindLong(2, dialogFilter.neverShow.get(i).longValue());
                    executeFast.step();
                }
                executeFast.dispose();
                this.database.commitTransaction();
            }
            SQLiteDatabase sQLiteDatabase = this.database;
            if (sQLiteDatabase != null) {
                sQLiteDatabase.commitTransaction();
            }
        } catch (Exception e2) {
            e = e2;
            sQLitePreparedStatement = executeFast;
            checkSQLException(e);
            SQLiteDatabase sQLiteDatabase2 = this.database;
            if (sQLiteDatabase2 != null) {
                sQLiteDatabase2.commitTransaction();
            }
            if (sQLitePreparedStatement != null) {
                sQLitePreparedStatement.dispose();
            }
        } catch (Throwable th2) {
            th = th2;
            sQLitePreparedStatement = executeFast;
            SQLiteDatabase sQLiteDatabase3 = this.database;
            if (sQLiteDatabase3 != null) {
                sQLiteDatabase3.commitTransaction();
            }
            if (sQLitePreparedStatement != null) {
                sQLitePreparedStatement.dispose();
            }
            throw th;
        }
    }

    public void lambda$saveDiffParams$35(int i, int i2, int i3, int i4) {
        try {
            if (this.lastSavedSeq == i && this.lastSavedPts == i2 && this.lastSavedDate == i3 && this.lastQtsValue == i4) {
                return;
            }
            SQLitePreparedStatement executeFast = this.database.executeFast("UPDATE params SET seq = ?, pts = ?, date = ?, qts = ? WHERE id = 1");
            executeFast.bindInteger(1, i);
            executeFast.bindInteger(2, i2);
            executeFast.bindInteger(3, i3);
            executeFast.bindInteger(4, i4);
            executeFast.step();
            executeFast.dispose();
            this.lastSavedSeq = i;
            this.lastSavedPts = i2;
            this.lastSavedDate = i3;
            this.lastSavedQts = i4;
        } catch (Exception e) {
            checkSQLException(e);
        }
    }

    private void saveTopicsInternal(long r21, java.util.List<org.telegram.tgnet.TLRPC.TL_forumTopic> r23, boolean r24, boolean r25, int r26) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MessagesStorage.saveTopicsInternal(long, java.util.List, boolean, boolean, int):void");
    }

    private ArrayList<Long> toPeerIds(ArrayList<TLRPC.InputPeer> arrayList) {
        ArrayList<Long> arrayList2 = new ArrayList<>();
        if (arrayList == null) {
            return arrayList2;
        }
        int size = arrayList.size();
        for (int i = 0; i < size; i++) {
            TLRPC.InputPeer inputPeer = arrayList.get(i);
            if (inputPeer != null) {
                long j = inputPeer.user_id;
                if (j == 0) {
                    long j2 = inputPeer.chat_id;
                    if (j2 == 0) {
                        j2 = inputPeer.channel_id;
                    }
                    j = -j2;
                }
                arrayList2.add(Long.valueOf(j));
            }
        }
        return arrayList2;
    }

    private void updateDbToLastVersion(int i) {
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                MessagesStorage.this.lambda$updateDbToLastVersion$3();
            }
        });
        FileLog.d("MessagesStorage start db migration from " + i + " to 162");
        int migrate = DatabaseMigrationHelper.migrate(this, i);
        StringBuilder sb = new StringBuilder();
        sb.append("MessagesStorage db migration finished to varsion ");
        sb.append(migrate);
        FileLog.d(sb.toString());
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                MessagesStorage.this.lambda$updateDbToLastVersion$4();
            }
        });
    }

    public void lambda$updateDialogsWithDeletedMessages$209(long r24, long r26, java.util.ArrayList<java.lang.Integer> r28, java.util.ArrayList<java.lang.Long> r29) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MessagesStorage.lambda$updateDialogsWithDeletedMessages$209(long, long, java.util.ArrayList, java.util.ArrayList):void");
    }

    private void updateDialogsWithReadMessagesInternal(java.util.ArrayList<java.lang.Integer> r20, org.telegram.messenger.support.LongSparseIntArray r21, org.telegram.messenger.support.LongSparseIntArray r22, androidx.collection.LongSparseArray r23, org.telegram.messenger.support.LongSparseIntArray r24) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MessagesStorage.updateDialogsWithReadMessagesInternal(java.util.ArrayList, org.telegram.messenger.support.LongSparseIntArray, org.telegram.messenger.support.LongSparseIntArray, androidx.collection.LongSparseArray, org.telegram.messenger.support.LongSparseIntArray):void");
    }

    private void updateFiltersReadCounter(org.telegram.messenger.support.LongSparseIntArray r29, org.telegram.messenger.support.LongSparseIntArray r30, boolean r31) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MessagesStorage.updateFiltersReadCounter(org.telegram.messenger.support.LongSparseIntArray, org.telegram.messenger.support.LongSparseIntArray, boolean):void");
    }

    public long[] lambda$updateMessageStateAndId$197(long r19, long r21, java.lang.Integer r23, int r24, int r25, int r26, int r27) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MessagesStorage.lambda$updateMessageStateAndId$197(long, long, java.lang.Integer, int, int, int, int):long[]");
    }

    public void lambda$updateRepliesMaxReadId$186(final long r22, final int r24, final int r25, int r26) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MessagesStorage.lambda$updateRepliesMaxReadId$186(long, int, int, int):void");
    }

    public void lambda$updateUsers$198(java.util.ArrayList<org.telegram.tgnet.TLRPC.User> r9, boolean r10, boolean r11) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MessagesStorage.lambda$updateUsers$198(java.util.ArrayList, boolean, boolean):void");
    }

    private void updateWidgets(long j) {
        ArrayList<Long> arrayList = new ArrayList<>();
        arrayList.add(Long.valueOf(j));
        updateWidgets(arrayList);
    }

    private void updateWidgets(ArrayList<Long> arrayList) {
        if (arrayList.isEmpty()) {
            return;
        }
        try {
            TextUtils.join(",", arrayList);
            SQLiteCursor queryFinalized = this.database.queryFinalized(String.format(Locale.US, "SELECT DISTINCT id FROM shortcut_widget WHERE did IN(%s,-1)", TextUtils.join(",", arrayList)), new Object[0]);
            AppWidgetManager appWidgetManager = null;
            while (queryFinalized.next()) {
                if (appWidgetManager == null) {
                    appWidgetManager = AppWidgetManager.getInstance(ApplicationLoader.applicationContext);
                }
                appWidgetManager.notifyAppWidgetViewDataChanged(queryFinalized.intValue(0), R.id.list_view);
            }
            queryFinalized.dispose();
        } catch (Exception e) {
            checkSQLException(e);
        }
    }

    public void addRecentLocalFile(final String str, final String str2, final TLRPC.Document document) {
        if (str == null || str.length() == 0) {
            return;
        }
        if ((str2 == null || str2.length() == 0) && document == null) {
            return;
        }
        this.storageQueue.postRunnable(new Runnable() {
            @Override
            public final void run() {
                MessagesStorage.this.lambda$addRecentLocalFile$79(document, str, str2);
            }
        });
    }

    public void applyPhoneBookUpdates(final String str, final String str2) {
        if (TextUtils.isEmpty(str)) {
            return;
        }
        this.storageQueue.postRunnable(new Runnable() {
            @Override
            public final void run() {
                MessagesStorage.this.lambda$applyPhoneBookUpdates$142(str, str2);
            }
        });
    }

    public void bindTaskToGuid(Runnable runnable, int i) {
        ArrayList<Runnable> arrayList = this.tasks.get(i);
        if (arrayList == null) {
            arrayList = new ArrayList<>();
            this.tasks.put(i, arrayList);
        }
        arrayList.add(runnable);
    }

    public void cancelTasksForGuid(int i) {
        ArrayList<Runnable> arrayList = this.tasks.get(i);
        if (arrayList == null) {
            return;
        }
        int size = arrayList.size();
        for (int i2 = 0; i2 < size; i2++) {
            this.storageQueue.cancelRunnable(arrayList.get(i2));
        }
        this.tasks.remove(i);
    }

    public void checkIfFolderEmpty(final int i) {
        this.storageQueue.postRunnable(new Runnable() {
            @Override
            public final void run() {
                MessagesStorage.this.lambda$checkIfFolderEmpty$227(i);
            }
        });
    }

    public void checkLoadedRemoteFilters(final ArrayList<TLRPC.DialogFilter> arrayList, final Runnable runnable) {
        this.storageQueue.postRunnable(new Runnable() {
            @Override
            public final void run() {
                MessagesStorage.this.lambda$checkLoadedRemoteFilters$66(arrayList, runnable);
            }
        });
    }

    public boolean checkMessageByRandomId(final long j) {
        final boolean[] zArr = new boolean[1];
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        this.storageQueue.postRunnable(new Runnable() {
            @Override
            public final void run() {
                MessagesStorage.this.lambda$checkMessageByRandomId$147(j, zArr, countDownLatch);
            }
        });
        try {
            countDownLatch.await();
        } catch (Exception e) {
            checkSQLException(e);
        }
        return zArr[0];
    }

    public boolean checkMessageId(final long j, final int i) {
        final boolean[] zArr = new boolean[1];
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        this.storageQueue.postRunnable(new Runnable() {
            @Override
            public final void run() {
                MessagesStorage.this.lambda$checkMessageId$148(j, i, zArr, countDownLatch);
            }
        });
        try {
            countDownLatch.await();
        } catch (Exception e) {
            checkSQLException(e);
        }
        return zArr[0];
    }

    public void checkSQLException(Throwable th) {
        checkSQLException(th, true);
    }

    public void cleanup(final boolean z) {
        this.storageQueue.postRunnable(new Runnable() {
            @Override
            public final void run() {
                MessagesStorage.this.lambda$cleanup$6(z);
            }
        });
    }

    public void clearDatabaseValues() {
        this.lastDateValue = 0;
        this.lastSeqValue = 0;
        this.lastPtsValue = 0;
        this.lastQtsValue = 0;
        this.lastSecretVersion = 0;
        this.mainUnreadCount = 0;
        this.archiveUnreadCount = 0;
        this.pendingMainUnreadCount = 0;
        this.pendingArchiveUnreadCount = 0;
        this.dialogFilters.clear();
        this.dialogFiltersMap.clear();
        this.unknownDialogsIds.clear();
        this.lastSavedSeq = 0;
        this.lastSavedPts = 0;
        this.lastSavedDate = 0;
        this.lastSavedQts = 0;
        this.secretPBytes = null;
        this.secretG = 0;
    }

    public void clearDownloadQueue(final int i) {
        this.storageQueue.postRunnable(new Runnable() {
            @Override
            public final void run() {
                MessagesStorage.this.lambda$clearDownloadQueue$177(i);
            }
        });
    }

    public void clearLocalDatabase() {
        this.storageQueue.postRunnable(new Runnable() {
            @Override
            public final void run() {
                MessagesStorage.this.lambda$clearLocalDatabase$44();
            }
        });
    }

    public void clearSentMedia() {
        this.storageQueue.postRunnable(new Runnable() {
            @Override
            public final void run() {
                MessagesStorage.this.lambda$clearSentMedia$157();
            }
        });
    }

    public void clearUserPhoto(final long j, final long j2) {
        this.storageQueue.postRunnable(new Runnable() {
            @Override
            public final void run() {
                MessagesStorage.this.lambda$clearUserPhoto$88(j, j2);
            }
        });
    }

    public void clearUserPhotos(final long j) {
        this.storageQueue.postRunnable(new Runnable() {
            @Override
            public final void run() {
                MessagesStorage.this.lambda$clearUserPhotos$87(j);
            }
        });
    }

    public void clearWidgetDialogs(final int i) {
        this.storageQueue.postRunnable(new Runnable() {
            @Override
            public final void run() {
                MessagesStorage.this.lambda$clearWidgetDialogs$160(i);
            }
        });
    }

    public void closeHolesInMedia(long r39, int r41, int r42, int r43, long r44) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MessagesStorage.closeHolesInMedia(long, int, int, int, long):void");
    }

    public void completeTaskForGuid(Runnable runnable, int i) {
        ArrayList<Runnable> arrayList = this.tasks.get(i);
        if (arrayList == null) {
            return;
        }
        arrayList.remove(runnable);
        if (arrayList.isEmpty()) {
            this.tasks.remove(i);
        }
    }

    public boolean containsLocalDialog(final long j) {
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        final Boolean[] boolArr = {Boolean.FALSE};
        this.storageQueue.postRunnable(new Runnable() {
            @Override
            public final void run() {
                MessagesStorage.this.lambda$containsLocalDialog$172(j, boolArr, countDownLatch);
            }
        });
        try {
            countDownLatch.await();
        } catch (Exception e) {
            checkSQLException(e);
        }
        return boolArr[0].booleanValue();
    }

    public long createPendingTask(final NativeByteBuffer nativeByteBuffer) {
        if (nativeByteBuffer == null) {
            return 0L;
        }
        final long andAdd = this.lastTaskId.getAndAdd(1L);
        this.storageQueue.postRunnable(new Runnable() {
            @Override
            public final void run() {
                MessagesStorage.this.lambda$createPendingTask$10(andAdd, nativeByteBuffer);
            }
        });
        return andAdd;
    }

    public void createTaskForMid(final long j, final int i, final int i2, final int i3, final int i4, final boolean z) {
        this.storageQueue.postRunnable(new Runnable() {
            @Override
            public final void run() {
                MessagesStorage.this.lambda$createTaskForMid$110(i2, i3, i4, i, z, j);
            }
        });
    }

    public void createTaskForSecretChat(final int i, final int i2, final int i3, final int i4, final ArrayList<Long> arrayList) {
        this.storageQueue.postRunnable(new Runnable() {
            @Override
            public final void run() {
                MessagesStorage.this.lambda$createTaskForSecretChat$112(i, arrayList, i4, i2, i3);
            }
        });
    }

    public void deleteAllStoryPushMessages() {
        this.storageQueue.postRunnable(new Runnable() {
            @Override
            public final void run() {
                MessagesStorage.this.lambda$deleteAllStoryPushMessages$40();
            }
        });
    }

    public void deleteAllStoryReactionPushMessages() {
        this.storageQueue.postRunnable(new Runnable() {
            @Override
            public final void run() {
                MessagesStorage.this.lambda$deleteAllStoryReactionPushMessages$41();
            }
        });
    }

    public void deleteContacts(final ArrayList<Long> arrayList) {
        if (arrayList == null || arrayList.isEmpty()) {
            return;
        }
        this.storageQueue.postRunnable(new Runnable() {
            @Override
            public final void run() {
                MessagesStorage.this.lambda$deleteContacts$141(arrayList);
            }
        });
    }

    public void deleteDialog(final long j, final int i) {
        this.storageQueue.postRunnable(new Runnable() {
            @Override
            public final void run() {
                MessagesStorage.this.lambda$deleteDialog$85(i, j);
            }
        });
    }

    public void deleteDialogFilter(final MessagesController.DialogFilter dialogFilter) {
        this.storageQueue.postRunnable(new Runnable() {
            @Override
            public final void run() {
                MessagesStorage.this.lambda$deleteDialogFilter$69(dialogFilter);
            }
        });
    }

    public void deletePushMessages(long j, ArrayList<Integer> arrayList) {
        try {
            this.database.executeFast(String.format(Locale.US, "DELETE FROM unread_push_messages WHERE uid = %d AND mid IN(%s)", Long.valueOf(j), TextUtils.join(",", arrayList))).stepThis().dispose();
        } catch (Exception e) {
            checkSQLException(e);
        }
    }

    public void deleteSavedDialog(final long j) {
        this.storageQueue.postRunnable(new Runnable() {
            @Override
            public final void run() {
                MessagesStorage.this.lambda$deleteSavedDialog$53(j);
            }
        });
    }

    public void deleteStoryPushMessage(final long j) {
        this.storageQueue.postRunnable(new Runnable() {
            @Override
            public final void run() {
                MessagesStorage.this.lambda$deleteStoryPushMessage$39(j);
            }
        });
    }

    public void deleteUserChatHistory(final long j, final long j2) {
        this.storageQueue.postRunnable(new Runnable() {
            @Override
            public final void run() {
                MessagesStorage.this.lambda$deleteUserChatHistory$82(j, j2);
            }
        });
    }

    public void deleteWallpaper(final long j) {
        this.storageQueue.postRunnable(new Runnable() {
            @Override
            public final void run() {
                MessagesStorage.this.lambda$deleteWallpaper$76(j);
            }
        });
    }

    public void doneHolesInMedia(long r19, int r21, int r22, long r23) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MessagesStorage.doneHolesInMedia(long, int, int, long):void");
    }

    public void emptyMessagesMedia(final long j, final ArrayList<Integer> arrayList) {
        this.storageQueue.postRunnable(new Runnable() {
            @Override
            public final void run() {
                MessagesStorage.this.lambda$emptyMessagesMedia$94(arrayList, j);
            }
        });
    }

    public void executeNoException(String str) {
        try {
            this.database.executeFast(str).stepThis().dispose();
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    public void fixNotificationSettings() {
        this.storageQueue.postRunnable(new Runnable() {
            @Override
            public final void run() {
                MessagesStorage.this.lambda$fixNotificationSettings$9();
            }
        });
    }

    public void fullReset() {
        this.storageQueue.postRunnable(new Runnable() {
            @Override
            public final void run() {
                MessagesStorage.this.lambda$fullReset$60();
            }
        });
    }

    public void getAnimatedEmoji(String str, ArrayList<TLRPC.Document> arrayList) {
        SQLiteCursor sQLiteCursor = null;
        try {
            try {
                sQLiteCursor = this.database.queryFinalized(String.format(Locale.US, "SELECT data FROM animated_emoji WHERE document_id IN (%s)", str), new Object[0]);
                while (sQLiteCursor.next()) {
                    NativeByteBuffer byteBufferValue = sQLiteCursor.byteBufferValue(0);
                    try {
                        TLRPC.Document TLdeserialize = TLRPC.Document.TLdeserialize(byteBufferValue, byteBufferValue.readInt32(true), true);
                        if (TLdeserialize != null && TLdeserialize.id != 0) {
                            arrayList.add(TLdeserialize);
                        }
                    } catch (Exception e) {
                        checkSQLException(e);
                    }
                    if (byteBufferValue != null) {
                        byteBufferValue.reuse();
                    }
                }
            } catch (SQLiteException e2) {
                e2.printStackTrace();
                if (sQLiteCursor == null) {
                    return;
                }
            }
            sQLiteCursor.dispose();
        } catch (Throwable th) {
            if (sQLiteCursor != null) {
                sQLiteCursor.dispose();
            }
            throw th;
        }
    }

    public int getArchiveUnreadCount() {
        return this.archiveUnreadCount;
    }

    public void getBotCache(final String str, final RequestDelegate requestDelegate) {
        if (str == null || requestDelegate == null) {
            return;
        }
        final int currentTime = getConnectionsManager().getCurrentTime();
        this.storageQueue.postRunnable(new Runnable() {
            @Override
            public final void run() {
                MessagesStorage.this.lambda$getBotCache$121(currentTime, str, requestDelegate);
            }
        });
    }

    public java.util.ArrayList<java.lang.Integer> getCachedMessagesInRange(long r7, int r9, int r10) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MessagesStorage.getCachedMessagesInRange(long, int, int):java.util.ArrayList");
    }

    public void getCachedPhoneBook(final boolean z) {
        this.storageQueue.postRunnable(new Runnable() {
            @Override
            public final void run() {
                MessagesStorage.this.lambda$getCachedPhoneBook$144(z);
            }
        });
    }

    public int getChannelPtsSync(final long j) {
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        final Integer[] numArr = {0};
        this.storageQueue.postRunnable(new Runnable() {
            @Override
            public final void run() {
                MessagesStorage.this.lambda$getChannelPtsSync$238(j, numArr, countDownLatch);
            }
        });
        try {
            countDownLatch.await();
        } catch (Exception e) {
            checkSQLException(e);
        }
        return numArr[0].intValue();
    }

    public TLRPC.Chat getChat(long j) {
        try {
            ArrayList<TLRPC.Chat> arrayList = new ArrayList<>();
            getChatsInternal("" + j, arrayList);
            if (!arrayList.isEmpty()) {
                return arrayList.get(0);
            }
        } catch (Exception e) {
            checkSQLException(e);
        }
        return null;
    }

    public TLRPC.Chat getChatSync(final long j) {
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        final TLRPC.Chat[] chatArr = new TLRPC.Chat[1];
        this.storageQueue.postRunnable(new Runnable() {
            @Override
            public final void run() {
                MessagesStorage.this.lambda$getChatSync$240(chatArr, j, countDownLatch);
            }
        });
        try {
            countDownLatch.await();
        } catch (Exception e) {
            checkSQLException(e);
        }
        return chatArr[0];
    }

    public ArrayList<TLRPC.Chat> getChats(ArrayList<Long> arrayList) {
        ArrayList<TLRPC.Chat> arrayList2 = new ArrayList<>();
        try {
            getChatsInternal(TextUtils.join(",", arrayList), arrayList2);
        } catch (Exception e) {
            arrayList2.clear();
            checkSQLException(e);
        }
        return arrayList2;
    }

    public void getChatsInternal(String str, ArrayList<TLRPC.Chat> arrayList) {
        getChatsInternal(str, arrayList, true);
    }

    public void getChatsInternal(String str, ArrayList<TLRPC.Chat> arrayList, boolean z) {
        if (str == null || str.length() == 0 || arrayList == null) {
            return;
        }
        SQLiteCursor queryFinalized = this.database.queryFinalized(String.format(Locale.US, "SELECT data FROM chats WHERE uid IN(%s)", str), new Object[0]);
        while (queryFinalized.next()) {
            try {
                NativeByteBuffer byteBufferValue = queryFinalized.byteBufferValue(0);
                if (byteBufferValue != null) {
                    TLRPC.Chat TLdeserialize = TLRPC.Chat.TLdeserialize(byteBufferValue, byteBufferValue.readInt32(false), false, z);
                    byteBufferValue.reuse();
                    if (TLdeserialize != null) {
                        arrayList.add(TLdeserialize);
                    }
                }
            } catch (Exception e) {
                checkSQLException(e);
            }
        }
        queryFinalized.dispose();
    }

    public void getContacts() {
        this.storageQueue.postRunnable(new Runnable() {
            @Override
            public final void run() {
                MessagesStorage.this.lambda$getContacts$145();
            }
        });
    }

    public SQLiteDatabase getDatabase() {
        return this.database;
    }

    public ArrayList<File> getDatabaseFiles() {
        ArrayList<File> arrayList = new ArrayList<>();
        arrayList.add(this.cacheFile);
        arrayList.add(this.walCacheFile);
        arrayList.add(this.shmCacheFile);
        return arrayList;
    }

    public long getDatabaseSize() {
        File file = this.cacheFile;
        long length = file != null ? file.length() : 0L;
        File file2 = this.shmCacheFile;
        return file2 != null ? length + file2.length() : length;
    }

    public void getDialogFolderId(final long j, final IntCallback intCallback) {
        this.storageQueue.postRunnable(new Runnable() {
            @Override
            public final void run() {
                MessagesStorage.this.lambda$getDialogFolderId$224(j, intCallback);
            }
        });
    }

    public void getDialogMaxMessageId(final long j, final IntCallback intCallback) {
        this.storageQueue.postRunnable(new Runnable() {
            @Override
            public final void run() {
                MessagesStorage.this.lambda$getDialogMaxMessageId$236(j, intCallback);
            }
        });
    }

    public int getDialogReadMax(final boolean z, final long j) {
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        final Integer[] numArr = {0};
        this.storageQueue.postRunnable(new Runnable() {
            @Override
            public final void run() {
                MessagesStorage.this.lambda$getDialogReadMax$237(z, j, numArr, countDownLatch);
            }
        });
        try {
            countDownLatch.await();
        } catch (Exception e) {
            checkSQLException(e);
        }
        return numArr[0].intValue();
    }

    public int getDialogReadMaxSync(boolean r5, long r6) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MessagesStorage.getDialogReadMaxSync(boolean, long):int");
    }

    public void getDialogs(final int i, final int i2, final int i3, boolean z) {
        long[] jArr;
        LongSparseArray drafts;
        int size;
        if (!z || (size = (drafts = getMediaDataController().getDrafts()).size()) <= 0) {
            jArr = null;
        } else {
            jArr = new long[size];
            for (int i4 = 0; i4 < size; i4++) {
                if (((LongSparseArray) drafts.valueAt(i4)).get(0L) != null) {
                    jArr[i4] = drafts.keyAt(i4);
                }
            }
        }
        final long[] jArr2 = jArr;
        this.storageQueue.postRunnable(new Runnable() {
            @Override
            public final void run() {
                MessagesStorage.this.lambda$getDialogs$221(i, i2, i3, jArr2);
            }
        });
    }

    public void getDownloadQueue(final int i) {
        this.storageQueue.postRunnable(new Runnable() {
            @Override
            public final void run() {
                MessagesStorage.this.lambda$getDownloadQueue$179(i);
            }
        });
    }

    public TLRPC.EncryptedChat getEncryptedChat(long j) {
        try {
            ArrayList<TLRPC.EncryptedChat> arrayList = new ArrayList<>();
            getEncryptedChatsInternal("" + j, arrayList, null);
            if (arrayList.isEmpty()) {
                return null;
            }
            return arrayList.get(0);
        } catch (Exception e) {
            checkSQLException(e);
            return null;
        }
    }

    public void getEncryptedChat(final long j, final CountDownLatch countDownLatch, final ArrayList<TLObject> arrayList) {
        if (countDownLatch == null || arrayList == null) {
            return;
        }
        this.storageQueue.postRunnable(new Runnable() {
            @Override
            public final void run() {
                MessagesStorage.this.lambda$getEncryptedChat$170(j, arrayList, countDownLatch);
            }
        });
    }

    public void getEncryptedChatsInternal(String str, ArrayList<TLRPC.EncryptedChat> arrayList, ArrayList<Long> arrayList2) {
        if (str == null || str.length() == 0 || arrayList == null) {
            return;
        }
        SQLiteCursor queryFinalized = this.database.queryFinalized(String.format(Locale.US, "SELECT data, user, g, authkey, ttl, layer, seq_in, seq_out, use_count, exchange_id, key_date, fprint, fauthkey, khash, in_seq_no, admin_id, mtproto_seq FROM enc_chats WHERE uid IN(%s)", str), new Object[0]);
        while (queryFinalized.next()) {
            try {
                NativeByteBuffer byteBufferValue = queryFinalized.byteBufferValue(0);
                if (byteBufferValue != null) {
                    TLRPC.EncryptedChat TLdeserialize = TLRPC.EncryptedChat.TLdeserialize(byteBufferValue, byteBufferValue.readInt32(false), false);
                    byteBufferValue.reuse();
                    if (TLdeserialize != null) {
                        long longValue = queryFinalized.longValue(1);
                        TLdeserialize.user_id = longValue;
                        if (arrayList2 != null && !arrayList2.contains(Long.valueOf(longValue))) {
                            arrayList2.add(Long.valueOf(TLdeserialize.user_id));
                        }
                        TLdeserialize.a_or_b = queryFinalized.byteArrayValue(2);
                        TLdeserialize.auth_key = queryFinalized.byteArrayValue(3);
                        TLdeserialize.ttl = queryFinalized.intValue(4);
                        TLdeserialize.layer = queryFinalized.intValue(5);
                        TLdeserialize.seq_in = queryFinalized.intValue(6);
                        TLdeserialize.seq_out = queryFinalized.intValue(7);
                        int intValue = queryFinalized.intValue(8);
                        TLdeserialize.key_use_count_in = (short) (intValue >> 16);
                        TLdeserialize.key_use_count_out = (short) intValue;
                        TLdeserialize.exchange_id = queryFinalized.longValue(9);
                        TLdeserialize.key_create_date = queryFinalized.intValue(10);
                        TLdeserialize.future_key_fingerprint = queryFinalized.longValue(11);
                        TLdeserialize.future_auth_key = queryFinalized.byteArrayValue(12);
                        TLdeserialize.key_hash = queryFinalized.byteArrayValue(13);
                        TLdeserialize.in_seq_no = queryFinalized.intValue(14);
                        long longValue2 = queryFinalized.longValue(15);
                        if (longValue2 != 0) {
                            TLdeserialize.admin_id = longValue2;
                        }
                        TLdeserialize.mtproto_seq = queryFinalized.intValue(16);
                        arrayList.add(TLdeserialize);
                    }
                }
            } catch (Exception e) {
                checkSQLException(e);
            }
        }
        queryFinalized.dispose();
    }

    public int getLastDateValue() {
        ensureOpened();
        return this.lastDateValue;
    }

    public int getLastPtsValue() {
        ensureOpened();
        return this.lastPtsValue;
    }

    public int getLastQtsValue() {
        ensureOpened();
        return this.lastQtsValue;
    }

    public int getLastSecretVersion() {
        ensureOpened();
        return this.lastSecretVersion;
    }

    public int getLastSeqValue() {
        ensureOpened();
        return this.lastSeqValue;
    }

    public int getMainUnreadCount() {
        return this.mainUnreadCount;
    }

    public TLRPC.Message getMessage(final long j, final long j2) {
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        final AtomicReference atomicReference = new AtomicReference();
        this.storageQueue.postRunnable(new Runnable() {
            @Override
            public final void run() {
                MessagesStorage.this.lambda$getMessage$136(j, j2, atomicReference, countDownLatch);
            }
        });
        try {
            countDownLatch.await();
        } catch (Exception e) {
            checkSQLException(e);
        }
        return (TLRPC.Message) atomicReference.get();
    }

    public int getMessageMediaType(TLRPC.Message message) {
        if (!(message instanceof TLRPC.TL_message_secret)) {
            if (message instanceof TLRPC.TL_message) {
                TLRPC.MessageMedia messageMedia = message.media;
                if (((messageMedia instanceof TLRPC.TL_messageMediaPhoto) || (messageMedia instanceof TLRPC.TL_messageMediaDocument)) && messageMedia.ttl_seconds != 0) {
                    return 1;
                }
            }
            return ((message.media instanceof TLRPC.TL_messageMediaPhoto) || MessageObject.isVideoMessage(message)) ? 0 : -1;
        }
        if (!(message.media instanceof TLRPC.TL_messageMediaPhoto) && !MessageObject.isGifMessage(message) && !MessageObject.isVoiceMessage(message) && !MessageObject.isVideoMessage(message) && !MessageObject.isRoundVideoMessage(message)) {
            return -1;
        }
        int i = message.ttl;
        return (i <= 0 || i > 60) ? 0 : 1;
    }

    public TLRPC.Message getMessageWithCustomParamsOnlyInternal(int i, long j) {
        SQLiteCursor queryFinalized;
        boolean z;
        TLRPC.TL_message tL_message = new TLRPC.TL_message();
        SQLiteCursor sQLiteCursor = null;
        try {
            try {
                queryFinalized = this.database.queryFinalized("SELECT custom_params FROM messages_v2 WHERE mid = ? AND uid = ?", Integer.valueOf(i), Long.valueOf(j));
            } catch (SQLiteException e) {
                e = e;
            }
        } catch (Throwable th) {
            th = th;
        }
        try {
            if (queryFinalized.next()) {
                MessageCustomParamsHelper.readLocalParams(tL_message, queryFinalized.byteBufferValue(0));
                z = true;
            } else {
                z = false;
            }
            queryFinalized.dispose();
            if (!z) {
                sQLiteCursor = this.database.queryFinalized("SELECT custom_params FROM messages_topics WHERE mid = ? AND uid = ?", Integer.valueOf(i), Long.valueOf(j));
                if (sQLiteCursor.next()) {
                    MessageCustomParamsHelper.readLocalParams(tL_message, sQLiteCursor.byteBufferValue(0));
                }
                sQLiteCursor.dispose();
            }
        } catch (SQLiteException e2) {
            e = e2;
            sQLiteCursor = queryFinalized;
            checkSQLException(e);
            if (sQLiteCursor != null) {
                sQLiteCursor.dispose();
            }
            return tL_message;
        } catch (Throwable th2) {
            th = th2;
            sQLiteCursor = queryFinalized;
            if (sQLiteCursor != null) {
                sQLiteCursor.dispose();
            }
            throw th;
        }
        return tL_message;
    }

    public void getMessages(final long j, final long j2, boolean z, final int i, final int i2, final int i3, final int i4, final int i5, final int i6, final int i7, final long j3, final int i8, final boolean z2, final boolean z3, final Timer timer) {
        final Timer.Task start = Timer.start(timer, "MessagesStorage.getMessages: storageQueue.postRunnable");
        this.storageQueue.postRunnable(new Runnable() {
            @Override
            public final void run() {
                MessagesStorage.this.lambda$getMessages$156(start, timer, j, j2, i, i2, i3, i4, i5, i6, i7, j3, i8, z2, z3);
            }
        });
    }

    public void getMessagesCount(final long j, final IntCallback intCallback) {
        this.storageQueue.postRunnable(new Runnable() {
            @Override
            public final void run() {
                MessagesStorage.this.lambda$getMessagesCount$152(j, intCallback);
            }
        });
    }

    public java.lang.Runnable getMessagesInternal(long r66, long r68, int r70, int r71, int r72, int r73, int r74, int r75, int r76, long r77, int r79, boolean r80, boolean r81, org.telegram.messenger.Timer r82) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MessagesStorage.getMessagesInternal(long, long, int, int, int, int, int, int, int, long, int, boolean, boolean, org.telegram.messenger.Timer):java.lang.Runnable");
    }

    public void getNewTask(final LongSparseArray longSparseArray, final LongSparseArray longSparseArray2) {
        this.storageQueue.postRunnable(new Runnable() {
            @Override
            public final void run() {
                MessagesStorage.this.lambda$getNewTask$105(longSparseArray, longSparseArray2);
            }
        });
    }

    public void getSavedDialogMaxMessageId(final long j, final IntCallback intCallback) {
        this.storageQueue.postRunnable(new Runnable() {
            @Override
            public final void run() {
                MessagesStorage.this.lambda$getSavedDialogMaxMessageId$51(j, intCallback);
            }
        });
    }

    public int getSecretG() {
        ensureOpened();
        return this.secretG;
    }

    public byte[] getSecretPBytes() {
        ensureOpened();
        return this.secretPBytes;
    }

    public Object[] getSentFile(final String str, final int i) {
        if (str == null || str.toLowerCase().endsWith("attheme")) {
            return null;
        }
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        final Object[] objArr = new Object[2];
        this.storageQueue.postRunnable(new Runnable() {
            @Override
            public final void run() {
                MessagesStorage.this.lambda$getSentFile$158(str, i, objArr, countDownLatch);
            }
        });
        try {
            countDownLatch.await();
        } catch (Exception e) {
            checkSQLException(e);
        }
        if (objArr[0] != null) {
            return objArr;
        }
        return null;
    }

    public HashMap<Long, Integer> getSmallGroupsParticipantsCount() {
        HashMap<Long, Integer> hashMap = new HashMap<>();
        SQLiteCursor sQLiteCursor = null;
        try {
            try {
                sQLiteCursor = this.database.queryFinalized("SELECT uid, info, participants_count FROM chat_settings_v2 WHERE participants_count > 1", new Object[0]);
                while (sQLiteCursor.next()) {
                    long longValue = sQLiteCursor.longValue(0);
                    NativeByteBuffer byteBufferValue = sQLiteCursor.byteBufferValue(1);
                    int intValue = sQLiteCursor.intValue(2);
                    if (byteBufferValue != null) {
                        TLRPC.ChatFull TLdeserialize = TLRPC.ChatFull.TLdeserialize(byteBufferValue, byteBufferValue.readInt32(false), false);
                        byteBufferValue.reuse();
                        if (TLdeserialize instanceof TLRPC.TL_channelFull) {
                            hashMap.put(Long.valueOf(longValue), Integer.valueOf(intValue));
                        }
                    }
                }
                sQLiteCursor.dispose();
            } catch (Exception e) {
                checkSQLException(e);
                if (sQLiteCursor != null) {
                    sQLiteCursor.dispose();
                }
            }
            return hashMap;
        } catch (Throwable th) {
            if (sQLiteCursor != null) {
                sQLiteCursor.dispose();
            }
            throw th;
        }
    }

    public DispatchQueue getStorageQueue() {
        return this.storageQueue;
    }

    public void getUnreadMention(final long j, final long j2, final IntCallback intCallback) {
        this.storageQueue.postRunnable(new Runnable() {
            @Override
            public final void run() {
                MessagesStorage.this.lambda$getUnreadMention$150(j2, j, intCallback);
            }
        });
    }

    public void getUnsentMessages(final int i) {
        this.storageQueue.postRunnable(new Runnable() {
            @Override
            public final void run() {
                MessagesStorage.this.lambda$getUnsentMessages$146(i);
            }
        });
    }

    public TLRPC.User getUser(long j) {
        try {
            ArrayList<TLRPC.User> arrayList = new ArrayList<>();
            ArrayList<Long> arrayList2 = new ArrayList<>();
            arrayList2.add(Long.valueOf(j));
            getUsersInternal(arrayList2, arrayList);
            if (!arrayList.isEmpty()) {
                return arrayList.get(0);
            }
        } catch (Exception e) {
            checkSQLException(e);
        }
        return null;
    }

    public TLRPC.User getUserSync(final long j) {
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        final TLRPC.User[] userArr = new TLRPC.User[1];
        this.storageQueue.postRunnable(new Runnable() {
            @Override
            public final void run() {
                MessagesStorage.this.lambda$getUserSync$239(userArr, j, countDownLatch);
            }
        });
        try {
            countDownLatch.await();
        } catch (Exception e) {
            checkSQLException(e);
        }
        return userArr[0];
    }

    public ArrayList<TLRPC.User> getUsers(ArrayList<Long> arrayList) {
        ArrayList<TLRPC.User> arrayList2 = new ArrayList<>();
        try {
            getUsersInternal(arrayList, arrayList2);
        } catch (Exception e) {
            arrayList2.clear();
            checkSQLException(e);
        }
        return arrayList2;
    }

    public void getUsersInternal(ArrayList<Long> arrayList, ArrayList<TLRPC.User> arrayList2) {
        getUsersInternal(arrayList, arrayList2, false);
    }

    public void getUsersInternal(ArrayList<Long> arrayList, ArrayList<TLRPC.User> arrayList2, boolean z) {
        if (arrayList == null || arrayList.isEmpty() || arrayList2 == null) {
            return;
        }
        if (arrayList.size() > 50) {
            int i = 0;
            while (i < arrayList.size()) {
                Long l = arrayList.get(i);
                l.longValue();
                TLRPC.User user = getMessagesController().getUser(l);
                if (user != null) {
                    arrayList2.add(user);
                    arrayList.remove(i);
                    i--;
                }
                i++;
            }
        }
        if (arrayList.isEmpty()) {
            return;
        }
        SQLiteCursor queryFinalized = this.database.queryFinalized(String.format(Locale.US, "SELECT data, status FROM users WHERE uid IN(%s)", TextUtils.join(",", arrayList)), new Object[0]);
        while (queryFinalized.next()) {
            try {
                NativeByteBuffer byteBufferValue = queryFinalized.byteBufferValue(0);
                if (byteBufferValue != null) {
                    TLRPC.User TLdeserialize = TLRPC.User.TLdeserialize(byteBufferValue, byteBufferValue.readInt32(false), false);
                    byteBufferValue.reuse();
                    if (TLdeserialize != null) {
                        TLRPC.UserStatus userStatus = TLdeserialize.status;
                        if (userStatus != null) {
                            userStatus.expires = queryFinalized.intValue(1);
                        }
                        arrayList2.add(TLdeserialize);
                        if (arrayList.size() > 50 && z) {
                            getMessagesController().putUser(TLdeserialize, true, false);
                        }
                    }
                }
            } catch (Exception e) {
                checkSQLException(e);
            }
        }
        queryFinalized.dispose();
    }

    public void getUsersInternal(HashSet<Long> hashSet, ArrayList<TLRPC.User> arrayList) {
        if (hashSet == null || hashSet.isEmpty() || arrayList == null) {
            return;
        }
        if (hashSet.size() > 50) {
            Iterator<Long> it = hashSet.iterator();
            while (it.hasNext()) {
                TLRPC.User user = getMessagesController().getUser(it.next());
                if (user != null) {
                    arrayList.add(user);
                    it.remove();
                }
            }
        }
        if (hashSet.isEmpty()) {
            return;
        }
        SQLiteCursor queryFinalized = this.database.queryFinalized(String.format(Locale.US, "SELECT data, status FROM users WHERE uid IN(%s)", TextUtils.join(",", hashSet)), new Object[0]);
        while (queryFinalized.next()) {
            try {
                NativeByteBuffer byteBufferValue = queryFinalized.byteBufferValue(0);
                if (byteBufferValue != null) {
                    TLRPC.User TLdeserialize = TLRPC.User.TLdeserialize(byteBufferValue, byteBufferValue.readInt32(false), false);
                    byteBufferValue.reuse();
                    if (TLdeserialize != null) {
                        TLRPC.UserStatus userStatus = TLdeserialize.status;
                        if (userStatus != null) {
                            userStatus.expires = queryFinalized.intValue(1);
                        }
                        arrayList.add(TLdeserialize);
                    }
                }
            } catch (Exception e) {
                checkSQLException(e);
            }
        }
        queryFinalized.dispose();
    }

    public void getWallpapers() {
        this.storageQueue.postRunnable(new Runnable() {
            @Override
            public final void run() {
                MessagesStorage.this.lambda$getWallpapers$78();
            }
        });
    }

    public void getWidgetDialogIds(final int i, final int i2, final ArrayList<Long> arrayList, final ArrayList<TLRPC.User> arrayList2, final ArrayList<TLRPC.Chat> arrayList3, final boolean z) {
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        this.storageQueue.postRunnable(new Runnable() {
            @Override
            public final void run() {
                MessagesStorage.this.lambda$getWidgetDialogIds$161(i, arrayList, arrayList2, arrayList3, z, i2, countDownLatch);
            }
        });
        try {
            countDownLatch.await();
        } catch (Exception e) {
            checkSQLException(e);
        }
    }

    public void getWidgetDialogs(final int i, final int i2, final ArrayList<Long> arrayList, final LongSparseArray longSparseArray, final LongSparseArray longSparseArray2, final ArrayList<TLRPC.User> arrayList2, final ArrayList<TLRPC.Chat> arrayList3) {
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        this.storageQueue.postRunnable(new Runnable() {
            @Override
            public final void run() {
                MessagesStorage.this.lambda$getWidgetDialogs$162(i, arrayList, i2, longSparseArray, longSparseArray2, arrayList3, arrayList2, countDownLatch);
            }
        });
        try {
            countDownLatch.await();
        } catch (Exception e) {
            checkSQLException(e);
        }
    }

    public boolean hasAuthMessage(final int i) {
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        final boolean[] zArr = new boolean[1];
        this.storageQueue.postRunnable(new Runnable() {
            @Override
            public final void run() {
                MessagesStorage.this.lambda$hasAuthMessage$169(i, zArr, countDownLatch);
            }
        });
        try {
            countDownLatch.await();
        } catch (Exception e) {
            checkSQLException(e);
        }
        return zArr[0];
    }

    public boolean hasInviteMeMessage(final long j) {
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        final boolean[] zArr = new boolean[1];
        this.storageQueue.postRunnable(new Runnable() {
            @Override
            public final void run() {
                MessagesStorage.this.lambda$hasInviteMeMessage$137(j, zArr, countDownLatch);
            }
        });
        try {
            countDownLatch.await();
        } catch (Exception e) {
            checkSQLException(e);
        }
        return zArr[0];
    }

    public boolean isDatabaseMigrationInProgress() {
        return this.databaseMigrationInProgress;
    }

    public void isDialogHasTopMessage(final long j, final Runnable runnable) {
        this.storageQueue.postRunnable(new Runnable() {
            @Override
            public final void run() {
                MessagesStorage.this.lambda$isDialogHasTopMessage$168(j, runnable);
            }
        });
    }

    public boolean isMigratedChat(final long j) {
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        final boolean[] zArr = new boolean[1];
        this.storageQueue.postRunnable(new Runnable() {
            @Override
            public final void run() {
                MessagesStorage.this.lambda$isMigratedChat$135(j, zArr, countDownLatch);
            }
        });
        try {
            countDownLatch.await();
        } catch (Exception e) {
            checkSQLException(e);
        }
        return zArr[0];
    }

    public void loadChannelAdmins(final long j) {
        this.storageQueue.postRunnable(new Runnable() {
            @Override
            public final void run() {
                MessagesStorage.this.lambda$loadChannelAdmins$117(j);
            }
        });
    }

    public TLRPC.ChatFull loadChatInfo(long j, boolean z, CountDownLatch countDownLatch, boolean z2, boolean z3) {
        return loadChatInfo(j, z, countDownLatch, z2, z3, 0);
    }

    public TLRPC.ChatFull loadChatInfo(final long j, final boolean z, final CountDownLatch countDownLatch, final boolean z2, final boolean z3, final int i) {
        final TLRPC.ChatFull[] chatFullArr = new TLRPC.ChatFull[1];
        this.storageQueue.postRunnable(new Runnable() {
            @Override
            public final void run() {
                MessagesStorage.this.lambda$loadChatInfo$138(chatFullArr, j, z, z2, z3, i, countDownLatch);
            }
        });
        if (countDownLatch != null) {
            try {
                countDownLatch.await();
            } catch (Throwable unused) {
            }
        }
        return chatFullArr[0];
    }

    public TLRPC.ChatFull loadChatInfoInQueue(long j, boolean z, boolean z2, boolean z3, int i) {
        return loadChatInfoInternal(j, z, z2, z3, i);
    }

    public void loadGroupedMessagesForTopicUpdates(ArrayList<TopicsController.TopicUpdate> arrayList) {
        if (arrayList == null) {
            return;
        }
        try {
            LongSparseArray longSparseArray = new LongSparseArray();
            for (int i = 0; i < arrayList.size(); i++) {
                if (!arrayList.get(i).reloadTopic && !arrayList.get(i).onlyCounters && arrayList.get(i).topMessage != null) {
                    long j = arrayList.get(i).topMessage.grouped_id;
                    if (j != 0) {
                        ArrayList arrayList2 = (ArrayList) longSparseArray.get(j);
                        if (arrayList2 == null) {
                            arrayList2 = new ArrayList();
                            longSparseArray.put(j, arrayList2);
                        }
                        arrayList2.add(arrayList.get(i));
                    }
                }
            }
            for (int i2 = 0; i2 < longSparseArray.size(); i2++) {
                long keyAt = longSparseArray.keyAt(i2);
                ArrayList arrayList3 = (ArrayList) longSparseArray.valueAt(i2);
                SQLiteCursor queryFinalized = this.database.queryFinalized(String.format(Locale.US, "SELECT data FROM messages_v2 WHERE uid = %s AND group_id = %s ORDER BY date DESC", Long.valueOf(((TopicsController.TopicUpdate) arrayList3.get(0)).dialogId), Long.valueOf(keyAt)), new Object[0]);
                ArrayList<MessageObject> arrayList4 = null;
                while (queryFinalized.next()) {
                    NativeByteBuffer byteBufferValue = queryFinalized.byteBufferValue(0);
                    TLRPC.Message TLdeserialize = TLRPC.Message.TLdeserialize(byteBufferValue, byteBufferValue.readInt32(false), false);
                    if (TLdeserialize != null) {
                        TLdeserialize.readAttachPath(byteBufferValue, UserConfig.getInstance(this.currentAccount).clientUserId);
                    }
                    if (arrayList4 == null) {
                        arrayList4 = new ArrayList<>();
                    }
                    arrayList4.add(new MessageObject(this.currentAccount, TLdeserialize, false, false));
                }
                queryFinalized.dispose();
                for (int i3 = 0; i3 < arrayList3.size(); i3++) {
                    ((TopicsController.TopicUpdate) arrayList3.get(i3)).groupedMessages = arrayList4;
                }
            }
        } catch (Throwable th) {
            checkSQLException(th);
        }
    }

    public void loadGroupedMessagesForTopics(long j, ArrayList<TLRPC.TL_forumTopic> arrayList) {
        if (arrayList == null) {
            return;
        }
        try {
            LongSparseArray longSparseArray = new LongSparseArray();
            for (int i = 0; i < arrayList.size(); i++) {
                if (arrayList.get(i).topMessage != null) {
                    long j2 = arrayList.get(i).topMessage.grouped_id;
                    if (j2 != 0) {
                        ArrayList arrayList2 = (ArrayList) longSparseArray.get(j2);
                        if (arrayList2 == null) {
                            arrayList2 = new ArrayList();
                            longSparseArray.put(j2, arrayList2);
                        }
                        arrayList2.add(arrayList.get(i));
                    }
                }
            }
            for (int i2 = 0; i2 < longSparseArray.size(); i2++) {
                long keyAt = longSparseArray.keyAt(i2);
                ArrayList arrayList3 = (ArrayList) longSparseArray.valueAt(i2);
                SQLiteCursor queryFinalized = this.database.queryFinalized(String.format(Locale.US, "SELECT data FROM messages_v2 WHERE uid = %s AND group_id = %s ORDER BY date DESC", Long.valueOf(j), Long.valueOf(keyAt)), new Object[0]);
                ArrayList<MessageObject> arrayList4 = null;
                while (queryFinalized.next()) {
                    NativeByteBuffer byteBufferValue = queryFinalized.byteBufferValue(0);
                    TLRPC.Message TLdeserialize = TLRPC.Message.TLdeserialize(byteBufferValue, byteBufferValue.readInt32(false), false);
                    if (TLdeserialize != null) {
                        TLdeserialize.readAttachPath(byteBufferValue, UserConfig.getInstance(this.currentAccount).clientUserId);
                    }
                    if (arrayList4 == null) {
                        arrayList4 = new ArrayList<>();
                    }
                    arrayList4.add(new MessageObject(this.currentAccount, TLdeserialize, false, false));
                }
                queryFinalized.dispose();
                for (int i3 = 0; i3 < arrayList3.size(); i3++) {
                    ((TLRPC.TL_forumTopic) arrayList3.get(i3)).groupedMessages = arrayList4;
                }
            }
        } catch (Throwable th) {
            checkSQLException(th);
        }
    }

    public void loadMessageAttachPaths(final ArrayList<MessageObject> arrayList, final Runnable runnable) {
        this.storageQueue.postRunnable(new Runnable() {
            @Override
            public final void run() {
                MessagesStorage.this.lambda$loadMessageAttachPaths$216(arrayList, runnable);
            }
        });
    }

    public void loadReplyMessages(androidx.collection.LongSparseArray r23, androidx.collection.LongSparseArray r24, java.util.ArrayList<java.lang.Long> r25, java.util.ArrayList<java.lang.Long> r26, int r27) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MessagesStorage.loadReplyMessages(androidx.collection.LongSparseArray, androidx.collection.LongSparseArray, java.util.ArrayList, java.util.ArrayList, int):void");
    }

    public void loadTopics(final long j, final Consumer<ArrayList<TLRPC.TL_forumTopic>> consumer) {
        this.storageQueue.postRunnable(new Runnable() {
            @Override
            public final void run() {
                MessagesStorage.this.lambda$loadTopics$49(j, consumer);
            }
        });
    }

    public void loadUnreadMessages() {
        this.storageQueue.postRunnable(new Runnable() {
            @Override
            public final void run() {
                MessagesStorage.this.lambda$loadUnreadMessages$74();
            }
        });
    }

    public void loadUserInfo(final TLRPC.User user, final boolean z, final int i, int i2) {
        if (user == null) {
            return;
        }
        this.storageQueue.postRunnable(new Runnable() {
            @Override
            public final void run() {
                MessagesStorage.this.lambda$loadUserInfo$123(user, z, i);
            }
        });
    }

    public ArrayList<TLRPC.UserFull> loadUserInfos(HashSet<Long> hashSet) {
        ArrayList<TLRPC.UserFull> arrayList = new ArrayList<>();
        try {
            String join = TextUtils.join(",", hashSet);
            SQLiteCursor queryFinalized = this.database.queryFinalized("SELECT info, pinned FROM user_settings WHERE uid IN(" + join + ")", new Object[0]);
            while (queryFinalized.next()) {
                NativeByteBuffer byteBufferValue = queryFinalized.byteBufferValue(0);
                if (byteBufferValue != null) {
                    TLRPC.UserFull TLdeserialize = TLRPC.UserFull.TLdeserialize(byteBufferValue, byteBufferValue.readInt32(false), false);
                    TLdeserialize.pinned_msg_id = queryFinalized.intValue(1);
                    arrayList.add(TLdeserialize);
                    byteBufferValue.reuse();
                }
            }
            queryFinalized.dispose();
        } catch (Exception e) {
            checkSQLException(e);
        }
        return arrayList;
    }

    public void localSearch(int r30, java.lang.String r31, java.util.ArrayList<java.lang.Object> r32, java.util.ArrayList<java.lang.CharSequence> r33, java.util.ArrayList<org.telegram.tgnet.TLRPC.User> r34, java.util.ArrayList<java.lang.Long> r35, int r36) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MessagesStorage.localSearch(int, java.lang.String, java.util.ArrayList, java.util.ArrayList, java.util.ArrayList, java.util.ArrayList, int):void");
    }

    public void markMentionMessageAsRead(final long j, final int i, final long j2) {
        this.storageQueue.postRunnable(new Runnable() {
            @Override
            public final void run() {
                MessagesStorage.this.lambda$markMentionMessageAsRead$106(i, j, j2);
            }
        });
    }

    public void markMessageAsMention(final long j, final int i) {
        this.storageQueue.postRunnable(new Runnable() {
            @Override
            public final void run() {
                MessagesStorage.this.lambda$markMessageAsMention$107(i, j);
            }
        });
    }

    public void markMessageAsSendError(final TLRPC.Message message, final int i) {
        this.storageQueue.postRunnable(new Runnable() {
            @Override
            public final void run() {
                MessagesStorage.this.lambda$markMessageAsSendError$194(i, message);
            }
        });
    }

    public void markMessageReactionsAsRead(final long j, final long j2, final int i, boolean z) {
        if (z) {
            getStorageQueue().postRunnable(new Runnable() {
                @Override
                public final void run() {
                    MessagesStorage.this.lambda$markMessageReactionsAsRead$243(j, j2, i);
                }
            });
        } else {
            lambda$markMessageReactionsAsRead$243(j, j2, i);
        }
    }

    public void lambda$markMessageReactionsAsRead$243(long r19, long r21, int r23) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MessagesStorage.lambda$markMessageReactionsAsRead$243(long, long, int):void");
    }

    public ArrayList<Long> markMessagesAsDeleted(final long j, final int i, boolean z, final boolean z2) {
        if (!z) {
            return lambda$markMessagesAsDeleted$212(j, i, z2);
        }
        this.storageQueue.postRunnable(new Runnable() {
            @Override
            public final void run() {
                MessagesStorage.this.lambda$markMessagesAsDeleted$212(j, i, z2);
            }
        });
        return null;
    }

    public ArrayList<Long> markMessagesAsDeleted(final long j, final ArrayList<Integer> arrayList, boolean z, final boolean z2, final int i, final int i2) {
        if (arrayList.isEmpty()) {
            return null;
        }
        if (!z) {
            return lambda$markMessagesAsDeleted$210(j, arrayList, z2, i, i2);
        }
        this.storageQueue.postRunnable(new Runnable() {
            @Override
            public final void run() {
                MessagesStorage.this.lambda$markMessagesAsDeleted$210(j, arrayList, z2, i, i2);
            }
        });
        return null;
    }

    public void markMessagesAsDeletedByRandoms(final ArrayList<Long> arrayList) {
        if (arrayList.isEmpty()) {
            return;
        }
        this.storageQueue.postRunnable(new Runnable() {
            @Override
            public final void run() {
                MessagesStorage.this.lambda$markMessagesAsDeletedByRandoms$202(arrayList);
            }
        });
    }

    public void markMessagesAsRead(final LongSparseIntArray longSparseIntArray, final LongSparseIntArray longSparseIntArray2, final SparseIntArray sparseIntArray, boolean z) {
        if (z) {
            this.storageQueue.postRunnable(new Runnable() {
                @Override
                public final void run() {
                    MessagesStorage.this.lambda$markMessagesAsRead$200(longSparseIntArray, longSparseIntArray2, sparseIntArray);
                }
            });
        } else {
            lambda$markMessagesAsRead$200(longSparseIntArray, longSparseIntArray2, sparseIntArray);
        }
    }

    public void markMessagesContentAsRead(final long j, final ArrayList<Integer> arrayList, final int i, final int i2) {
        if (isEmpty(arrayList)) {
            return;
        }
        this.storageQueue.postRunnable(new Runnable() {
            @Override
            public final void run() {
                MessagesStorage.this.lambda$markMessagesContentAsRead$199(j, arrayList, i2, i);
            }
        });
    }

    public void onDeleteQueryComplete(final long j) {
        this.storageQueue.postRunnable(new Runnable() {
            @Override
            public final void run() {
                MessagesStorage.this.lambda$onDeleteQueryComplete$86(j);
            }
        });
    }

    public void openDatabase(int r11) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MessagesStorage.openDatabase(int):void");
    }

    public void overwriteChannel(final long j, final TLRPC.TL_updates_channelDifferenceTooLong tL_updates_channelDifferenceTooLong, final int i, final Runnable runnable) {
        this.storageQueue.postRunnable(new Runnable() {
            @Override
            public final void run() {
                MessagesStorage.this.lambda$overwriteChannel$183(j, i, tL_updates_channelDifferenceTooLong, runnable);
            }
        });
    }

    public void processLoadedFilterPeers(final TLRPC.messages_Dialogs messages_dialogs, final TLRPC.messages_Dialogs messages_dialogs2, final ArrayList<TLRPC.User> arrayList, final ArrayList<TLRPC.Chat> arrayList2, final ArrayList<MessagesController.DialogFilter> arrayList3, final SparseArray<MessagesController.DialogFilter> sparseArray, final ArrayList<Integer> arrayList4, final HashMap<Integer, HashSet<Long>> hashMap, final HashSet<Integer> hashSet, final Runnable runnable) {
        this.storageQueue.postRunnable(new Runnable() {
            @Override
            public final void run() {
                MessagesStorage.this.lambda$processLoadedFilterPeers$68(messages_dialogs, messages_dialogs2, arrayList, arrayList2, arrayList3, sparseArray, arrayList4, hashMap, hashSet, runnable);
            }
        });
    }

    public void processPendingRead(final long j, final int i, final int i2, final int i3) {
        final int i4 = this.lastSavedDate;
        this.storageQueue.postRunnable(new Runnable() {
            @Override
            public final void run() {
                MessagesStorage.this.lambda$processPendingRead$139(j, i, i3, i4, i2);
            }
        });
    }

    public void putCachedPhoneBook(final HashMap<String, ContactsController.Contact> hashMap, final boolean z, boolean z2) {
        if (hashMap != null) {
            if (!hashMap.isEmpty() || z || z2) {
                this.storageQueue.postRunnable(new Runnable() {
                    @Override
                    public final void run() {
                        MessagesStorage.this.lambda$putCachedPhoneBook$143(hashMap, z);
                    }
                });
            }
        }
    }

    public void putChannelAdmins(final long j, final LongSparseArray longSparseArray) {
        this.storageQueue.postRunnable(new Runnable() {
            @Override
            public final void run() {
                MessagesStorage.this.lambda$putChannelAdmins$118(j, longSparseArray);
            }
        });
    }

    public void putChannelViews(final LongSparseArray longSparseArray, final LongSparseArray longSparseArray2, final LongSparseArray longSparseArray3, final boolean z) {
        if (isEmpty(longSparseArray) && isEmpty(longSparseArray2) && isEmpty(longSparseArray3)) {
            return;
        }
        this.storageQueue.postRunnable(new Runnable() {
            @Override
            public final void run() {
                MessagesStorage.this.lambda$putChannelViews$184(longSparseArray, longSparseArray2, longSparseArray3, z);
            }
        });
    }

    public void putContacts(ArrayList<TLRPC.TL_contact> arrayList, final boolean z) {
        if (!arrayList.isEmpty() || z) {
            final ArrayList arrayList2 = new ArrayList(arrayList);
            this.storageQueue.postRunnable(new Runnable() {
                @Override
                public final void run() {
                    MessagesStorage.this.lambda$putContacts$140(z, arrayList2);
                }
            });
        }
    }

    public void putDialogs(final TLRPC.messages_Dialogs messages_dialogs, final int i) {
        if (messages_dialogs.dialogs.isEmpty()) {
            return;
        }
        this.storageQueue.postRunnable(new Runnable() {
            @Override
            public final void run() {
                MessagesStorage.this.lambda$putDialogs$234(messages_dialogs, i);
            }
        });
    }

    public void putEncryptedChat(final TLRPC.EncryptedChat encryptedChat, final TLRPC.User user, final TLRPC.Dialog dialog) {
        if (encryptedChat == null) {
            return;
        }
        this.storageQueue.postRunnable(new Runnable() {
            @Override
            public final void run() {
                MessagesStorage.this.lambda$putEncryptedChat$171(encryptedChat, user, dialog);
            }
        });
    }

    public void putMessages(ArrayList<TLRPC.Message> arrayList, boolean z, boolean z2, boolean z3, int i, int i2, long j) {
        putMessages(arrayList, z, z2, z3, i, false, i2, j);
    }

    public void putMessages(final ArrayList<TLRPC.Message> arrayList, final boolean z, boolean z2, final boolean z3, final int i, final boolean z4, final int i2, final long j) {
        if (arrayList.size() == 0) {
            return;
        }
        if (z2) {
            this.storageQueue.postRunnable(new Runnable() {
                @Override
                public final void run() {
                    MessagesStorage.this.lambda$putMessages$193(arrayList, z, z3, i, z4, i2, j);
                }
            });
        } else {
            lambda$putMessages$193(arrayList, z, z3, i, z4, i2, j);
        }
    }

    public void putMessages(final TLRPC.messages_Messages messages_messages, final long j, final int i, final int i2, final boolean z, final int i3, final long j2) {
        this.storageQueue.postRunnable(new Runnable() {
            @Override
            public final void run() {
                MessagesStorage.this.lambda$putMessages$219(i3, messages_messages, j, j2, i, i2, z);
            }
        });
    }

    public void putPushMessage(final MessageObject messageObject) {
        this.storageQueue.postRunnable(new Runnable() {
            @Override
            public final void run() {
                MessagesStorage.this.lambda$putPushMessage$42(messageObject);
            }
        });
    }

    public void putSentFile(final String str, final TLObject tLObject, final int i, final String str2) {
        if (str == null || tLObject == null || str2 == null) {
            return;
        }
        this.storageQueue.postRunnable(new Runnable() {
            @Override
            public final void run() {
                MessagesStorage.this.lambda$putSentFile$163(str, tLObject, i, str2);
            }
        });
    }

    public void putStoryPushMessage(final NotificationsController.StoryNotification storyNotification) {
        this.storageQueue.postRunnable(new Runnable() {
            @Override
            public final void run() {
                MessagesStorage.this.lambda$putStoryPushMessage$38(storyNotification);
            }
        });
    }

    public void putUsersAndChats(final List<TLRPC.User> list, final List<TLRPC.Chat> list2, final boolean z, boolean z2) {
        if (list == null || !list.isEmpty() || list2 == null || !list2.isEmpty()) {
            if (z2) {
                this.storageQueue.postRunnable(new Runnable() {
                    @Override
                    public final void run() {
                        MessagesStorage.this.lambda$putUsersAndChats$174(list, list2, z);
                    }
                });
            } else {
                lambda$putUsersAndChats$174(list, list2, z);
            }
        }
    }

    public void putWallpapers(final ArrayList<TLRPC.WallPaper> arrayList, final int i) {
        this.storageQueue.postRunnable(new Runnable() {
            @Override
            public final void run() {
                MessagesStorage.this.lambda$putWallpapers$75(i, arrayList);
            }
        });
    }

    public void putWebPages(final LongSparseArray longSparseArray) {
        if (isEmpty(longSparseArray)) {
            return;
        }
        this.storageQueue.postRunnable(new Runnable() {
            @Override
            public final void run() {
                MessagesStorage.this.lambda$putWebPages$181(longSparseArray);
            }
        });
    }

    public void putWidgetDialogs(final int i, final ArrayList<TopicKey> arrayList) {
        this.storageQueue.postRunnable(new Runnable() {
            @Override
            public final void run() {
                MessagesStorage.this.lambda$putWidgetDialogs$159(i, arrayList);
            }
        });
    }

    public void readAllDialogs(final int i) {
        this.storageQueue.postRunnable(new Runnable() {
            @Override
            public final void run() {
                MessagesStorage.this.lambda$readAllDialogs$62(i);
            }
        });
    }

    public void removeFromDownloadQueue(final long j, final int i, final boolean z) {
        this.storageQueue.postRunnable(new Runnable() {
            @Override
            public final void run() {
                MessagesStorage.this.lambda$removeFromDownloadQueue$175(z, i, j);
            }
        });
    }

    public void removePendingTask(final long j) {
        this.storageQueue.postRunnable(new Runnable() {
            @Override
            public final void run() {
                MessagesStorage.this.lambda$removePendingTask$11(j);
            }
        });
    }

    public void removeTopic(final long j, final int i) {
        this.storageQueue.postRunnable(new Runnable() {
            @Override
            public final void run() {
                MessagesStorage.this.lambda$removeTopic$54(j, i);
            }
        });
    }

    public void removeTopics(final long j, final ArrayList<Integer> arrayList) {
        this.storageQueue.postRunnable(new Runnable() {
            @Override
            public final void run() {
                MessagesStorage.this.lambda$removeTopics$55(arrayList, j);
            }
        });
    }

    public void replaceMessageIfExists(final TLRPC.Message message, final ArrayList<TLRPC.User> arrayList, final ArrayList<TLRPC.Chat> arrayList2, final boolean z) {
        if (message == null) {
            return;
        }
        this.storageQueue.postRunnable(new Runnable() {
            @Override
            public final void run() {
                MessagesStorage.this.lambda$replaceMessageIfExists$215(message, z, arrayList, arrayList2);
            }
        });
    }

    public void reset() {
        clearDatabaseValues();
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                MessagesStorage.this.lambda$reset$58();
            }
        });
    }

    public void resetAllUnreadCounters(boolean z) {
        int size = this.dialogFilters.size();
        for (int i = 0; i < size; i++) {
            MessagesController.DialogFilter dialogFilter = this.dialogFilters.get(i);
            if (!z || (dialogFilter.flags & MessagesController.DIALOG_FILTER_FLAG_EXCLUDE_MUTED) != 0) {
                dialogFilter.pendingUnreadCount = -1;
            }
        }
        calcUnreadCounters(false);
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                MessagesStorage.this.lambda$resetAllUnreadCounters$231();
            }
        });
    }

    public void resetDialogs(final TLRPC.messages_Dialogs messages_dialogs, final int i, final int i2, final int i3, final int i4, final int i5, final LongSparseArray longSparseArray, final LongSparseArray longSparseArray2, final TLRPC.Message message, final int i6) {
        this.storageQueue.postRunnable(new Runnable() {
            @Override
            public final void run() {
                MessagesStorage.this.lambda$resetDialogs$90(messages_dialogs, i6, i2, i3, i4, i5, message, i, longSparseArray, longSparseArray2);
            }
        });
    }

    public void resetMentionsCount(final long j, final long j2, final int i) {
        this.storageQueue.postRunnable(new Runnable() {
            @Override
            public final void run() {
                MessagesStorage.this.lambda$resetMentionsCount$108(j2, j, i);
            }
        });
    }

    public void saveBotCache(final String str, final TLObject tLObject) {
        if (tLObject == null || TextUtils.isEmpty(str)) {
            return;
        }
        this.storageQueue.postRunnable(new Runnable() {
            @Override
            public final void run() {
                MessagesStorage.this.lambda$saveBotCache$120(tLObject, str);
            }
        });
    }

    public void saveChannelPts(final long j, final int i) {
        this.storageQueue.postRunnable(new Runnable() {
            @Override
            public final void run() {
                MessagesStorage.this.lambda$saveChannelPts$34(i, j);
            }
        });
    }

    public void saveChatInviter(final long j, final long j2) {
        this.storageQueue.postRunnable(new Runnable() {
            @Override
            public final void run() {
                MessagesStorage.this.lambda$saveChatInviter$126(j2, j);
            }
        });
    }

    public void saveChatLinksCount(final long j, final int i) {
        this.storageQueue.postRunnable(new Runnable() {
            @Override
            public final void run() {
                MessagesStorage.this.lambda$saveChatLinksCount$127(i, j);
            }
        });
    }

    public void saveDialogFilter(final MessagesController.DialogFilter dialogFilter, final boolean z, final boolean z2) {
        this.storageQueue.postRunnable(new Runnable() {
            @Override
            public final void run() {
                MessagesStorage.this.lambda$saveDialogFilter$71(dialogFilter, z, z2);
            }
        });
    }

    public void saveDialogFiltersOrder() {
        final ArrayList arrayList = new ArrayList(getMessagesController().dialogFilters);
        this.storageQueue.postRunnable(new Runnable() {
            @Override
            public final void run() {
                MessagesStorage.this.lambda$saveDialogFiltersOrder$72(arrayList);
            }
        });
    }

    public void saveDialogFiltersOrderInternal() {
        SQLitePreparedStatement sQLitePreparedStatement = null;
        try {
            try {
                sQLitePreparedStatement = this.database.executeFast("UPDATE dialog_filter SET ord = ?, flags = ? WHERE id = ?");
                int size = this.dialogFilters.size();
                for (int i = 0; i < size; i++) {
                    MessagesController.DialogFilter dialogFilter = this.dialogFilters.get(i);
                    sQLitePreparedStatement.requery();
                    sQLitePreparedStatement.bindInteger(1, dialogFilter.order);
                    sQLitePreparedStatement.bindInteger(2, dialogFilter.flags);
                    sQLitePreparedStatement.bindInteger(3, dialogFilter.id);
                    sQLitePreparedStatement.step();
                }
                sQLitePreparedStatement.dispose();
            } catch (Exception e) {
                checkSQLException(e);
                if (sQLitePreparedStatement != null) {
                    sQLitePreparedStatement.dispose();
                }
            }
        } catch (Throwable th) {
            if (sQLitePreparedStatement != null) {
                sQLitePreparedStatement.dispose();
            }
            throw th;
        }
    }

    public void saveDiffParams(final int i, final int i2, final int i3, final int i4) {
        this.storageQueue.postRunnable(new Runnable() {
            @Override
            public final void run() {
                MessagesStorage.this.lambda$saveDiffParams$35(i, i2, i3, i4);
            }
        });
    }

    public void saveSecretParams(final int i, final int i2, final byte[] bArr) {
        this.storageQueue.postRunnable(new Runnable() {
            @Override
            public final void run() {
                MessagesStorage.this.lambda$saveSecretParams$7(i, i2, bArr);
            }
        });
    }

    public void saveTopics(final long j, final List<TLRPC.TL_forumTopic> list, final boolean z, boolean z2, final int i) {
        if (z2) {
            this.storageQueue.postRunnable(new Runnable() {
                @Override
                public final void run() {
                    MessagesStorage.this.lambda$saveTopics$45(j, list, z, i);
                }
            });
        } else {
            saveTopicsInternal(j, list, z, false, i);
        }
    }

    public void searchSavedByTag(final TLRPC.Reaction reaction, final long j, final String str, final int i, final int i2, final Utilities.Callback4<ArrayList<MessageObject>, ArrayList<TLRPC.User>, ArrayList<TLRPC.Chat>, ArrayList<TLRPC.Document>> callback4, final boolean z) {
        if (callback4 == null) {
            return;
        }
        this.storageQueue.postRunnable(new Runnable() {
            @Override
            public final void run() {
                MessagesStorage.this.lambda$searchSavedByTag$97(str, j, reaction, i, i2, z, callback4);
            }
        });
    }

    public void setDialogFlags(final long j, final long j2) {
        this.storageQueue.postRunnable(new Runnable() {
            @Override
            public final void run() {
                MessagesStorage.this.lambda$setDialogFlags$37(j, j2);
            }
        });
    }

    public void setDialogPinned(final long j, final int i) {
        this.storageQueue.postRunnable(new Runnable() {
            @Override
            public final void run() {
                MessagesStorage.this.lambda$setDialogPinned$232(i, j);
            }
        });
    }

    public void setDialogTtl(final long j, final int i) {
        this.storageQueue.postRunnable(new Runnable() {
            @Override
            public final void run() {
                MessagesStorage.this.lambda$setDialogTtl$57(i, j);
            }
        });
    }

    public void setDialogUnread(final long j, final boolean z) {
        this.storageQueue.postRunnable(new Runnable() {
            @Override
            public final void run() {
                MessagesStorage.this.lambda$setDialogUnread$229(j, z);
            }
        });
    }

    public void setDialogViewThreadAsMessages(final long j, final boolean z) {
        this.storageQueue.postRunnable(new Runnable() {
            @Override
            public final void run() {
                MessagesStorage.this.lambda$setDialogViewThreadAsMessages$230(j, z);
            }
        });
    }

    public void setDialogsFolderId(final ArrayList<TLRPC.TL_folderPeer> arrayList, final ArrayList<TLRPC.TL_inputFolderPeer> arrayList2, final long j, final int i) {
        if (arrayList == null && arrayList2 == null && j == 0) {
            return;
        }
        this.storageQueue.postRunnable(new Runnable() {
            @Override
            public final void run() {
                MessagesStorage.this.lambda$setDialogsFolderId$225(arrayList, arrayList2, i, j);
            }
        });
    }

    public void setDialogsPinned(final ArrayList<Long> arrayList, final ArrayList<Integer> arrayList2) {
        this.storageQueue.postRunnable(new Runnable() {
            @Override
            public final void run() {
                MessagesStorage.this.lambda$setDialogsPinned$233(arrayList, arrayList2);
            }
        });
    }

    public void setLastDateValue(int i) {
        ensureOpened();
        this.lastDateValue = i;
    }

    public void setLastPtsValue(int i) {
        ensureOpened();
        this.lastPtsValue = i;
    }

    public void setLastQtsValue(int i) {
        ensureOpened();
        this.lastQtsValue = i;
    }

    public void setLastSecretVersion(int i) {
        ensureOpened();
        this.lastSecretVersion = i;
    }

    public void setLastSeqValue(int i) {
        ensureOpened();
        this.lastSeqValue = i;
    }

    public void setMessageSeq(final int i, final int i2, final int i3) {
        this.storageQueue.postRunnable(new Runnable() {
            @Override
            public final void run() {
                MessagesStorage.this.lambda$setMessageSeq$195(i, i2, i3);
            }
        });
    }

    public void setSecretG(int i) {
        ensureOpened();
        this.secretG = i;
    }

    public void setSecretPBytes(byte[] bArr) {
        ensureOpened();
        this.secretPBytes = bArr;
    }

    public void unpinAllDialogsExceptNew(final ArrayList<Long> arrayList, final int i) {
        this.storageQueue.postRunnable(new Runnable() {
            @Override
            public final void run() {
                MessagesStorage.this.lambda$unpinAllDialogsExceptNew$228(arrayList, i);
            }
        });
    }

    public void updateChannelUsers(final long j, final ArrayList<TLRPC.ChannelParticipant> arrayList) {
        this.storageQueue.postRunnable(new Runnable() {
            @Override
            public final void run() {
                MessagesStorage.this.lambda$updateChannelUsers$119(j, arrayList);
            }
        });
    }

    public void updateChatDefaultBannedRights(final long j, final TLRPC.TL_chatBannedRights tL_chatBannedRights, final int i) {
        if (tL_chatBannedRights == null || j == 0) {
            return;
        }
        this.storageQueue.postRunnable(new Runnable() {
            @Override
            public final void run() {
                MessagesStorage.this.lambda$updateChatDefaultBannedRights$173(j, i, tL_chatBannedRights);
            }
        });
    }

    public void updateChatInfo(final long j, final long j2, final int i, final long j3, final int i2) {
        this.storageQueue.postRunnable(new Runnable() {
            @Override
            public final void run() {
                MessagesStorage.this.lambda$updateChatInfo$134(j, i, j2, j3, i2);
            }
        });
    }

    public void updateChatInfo(final TLRPC.ChatFull chatFull, final boolean z) {
        this.storageQueue.postRunnable(new Runnable() {
            @Override
            public final void run() {
                MessagesStorage.this.lambda$updateChatInfo$128(chatFull, z);
            }
        });
    }

    public void updateChatOnlineCount(final long j, final int i) {
        this.storageQueue.postRunnable(new Runnable() {
            @Override
            public final void run() {
                MessagesStorage.this.lambda$updateChatOnlineCount$129(i, j);
            }
        });
    }

    public void updateChatParticipants(final TLRPC.ChatParticipants chatParticipants) {
        if (chatParticipants == null) {
            return;
        }
        this.storageQueue.postRunnable(new Runnable() {
            @Override
            public final void run() {
                MessagesStorage.this.lambda$updateChatParticipants$116(chatParticipants);
            }
        });
    }

    public void updateDialogData(final TLRPC.Dialog dialog) {
        if (dialog == null) {
            return;
        }
        this.storageQueue.postRunnable(new Runnable() {
            @Override
            public final void run() {
                MessagesStorage.this.lambda$updateDialogData$222(dialog);
            }
        });
    }

    public void updateDialogUnreadReactions(final long j, final long j2, final int i, final boolean z) {
        this.storageQueue.postRunnable(new Runnable() {
            @Override
            public final void run() {
                MessagesStorage.this.lambda$updateDialogUnreadReactions$244(z, j, i, j2);
            }
        });
    }

    public void updateDialogsWithDeletedMessages(final long j, final long j2, final ArrayList<Integer> arrayList, final ArrayList<Long> arrayList2, boolean z) {
        if (z) {
            this.storageQueue.postRunnable(new Runnable() {
                @Override
                public final void run() {
                    MessagesStorage.this.lambda$updateDialogsWithDeletedMessages$209(j, j2, arrayList, arrayList2);
                }
            });
        } else {
            lambda$updateDialogsWithDeletedMessages$209(j, j2, arrayList, arrayList2);
        }
    }

    public void updateDialogsWithReadMessages(final LongSparseIntArray longSparseIntArray, final LongSparseIntArray longSparseIntArray2, final LongSparseArray longSparseArray, final LongSparseIntArray longSparseIntArray3, boolean z) {
        if (isEmpty(longSparseIntArray) && isEmpty(longSparseIntArray2) && isEmpty(longSparseArray) && isEmpty(longSparseIntArray3)) {
            return;
        }
        if (z) {
            this.storageQueue.postRunnable(new Runnable() {
                @Override
                public final void run() {
                    MessagesStorage.this.lambda$updateDialogsWithReadMessages$114(longSparseIntArray, longSparseIntArray2, longSparseArray, longSparseIntArray3);
                }
            });
        } else {
            updateDialogsWithReadMessagesInternal(null, longSparseIntArray, longSparseIntArray2, longSparseArray, longSparseIntArray3);
        }
    }

    public void updateEncryptedChat(final TLRPC.EncryptedChat encryptedChat) {
        if (encryptedChat == null) {
            return;
        }
        this.storageQueue.postRunnable(new Runnable() {
            @Override
            public final void run() {
                MessagesStorage.this.lambda$updateEncryptedChat$167(encryptedChat);
            }
        });
    }

    public void updateEncryptedChatLayer(final TLRPC.EncryptedChat encryptedChat) {
        if (encryptedChat == null) {
            return;
        }
        this.storageQueue.postRunnable(new Runnable() {
            @Override
            public final void run() {
                MessagesStorage.this.lambda$updateEncryptedChatLayer$166(encryptedChat);
            }
        });
    }

    public void updateEncryptedChatSeq(final TLRPC.EncryptedChat encryptedChat, final boolean z) {
        if (encryptedChat == null) {
            return;
        }
        this.storageQueue.postRunnable(new Runnable() {
            @Override
            public final void run() {
                MessagesStorage.this.lambda$updateEncryptedChatSeq$164(encryptedChat, z);
            }
        });
    }

    public void updateEncryptedChatTTL(final TLRPC.EncryptedChat encryptedChat) {
        if (encryptedChat == null) {
            return;
        }
        this.storageQueue.postRunnable(new Runnable() {
            @Override
            public final void run() {
                MessagesStorage.this.lambda$updateEncryptedChatTTL$165(encryptedChat);
            }
        });
    }

    public void updateMessageCustomParams(final long j, final TLRPC.Message message) {
        this.storageQueue.postRunnable(new Runnable() {
            @Override
            public final void run() {
                MessagesStorage.this.lambda$updateMessageCustomParams$104(message, j);
            }
        });
    }

    public void updateMessagePollResults(final long j, final TLRPC.Poll poll, final TLRPC.PollResults pollResults) {
        this.storageQueue.postRunnable(new Runnable() {
            @Override
            public final void run() {
                MessagesStorage.this.lambda$updateMessagePollResults$95(j, poll, pollResults);
            }
        });
    }

    public void updateMessageReactions(final long j, final int i, final TLRPC.TL_messageReactions tL_messageReactions) {
        this.storageQueue.postRunnable(new Runnable() {
            @Override
            public final void run() {
                MessagesStorage.this.lambda$updateMessageReactions$98(i, j, tL_messageReactions);
            }
        });
    }

    public long[] updateMessageStateAndId(final long j, final long j2, final Integer num, final int i, final int i2, boolean z, final int i3, final int i4) {
        if (!z) {
            return lambda$updateMessageStateAndId$197(j, j2, num, i, i2, i3, i4);
        }
        this.storageQueue.postRunnable(new Runnable() {
            @Override
            public final void run() {
                MessagesStorage.this.lambda$updateMessageStateAndId$197(j, j2, num, i, i2, i3, i4);
            }
        });
        return null;
    }

    public void updateMessageVerifyFlags(final ArrayList<TLRPC.Message> arrayList) {
        Utilities.stageQueue.postRunnable(new Runnable() {
            @Override
            public final void run() {
                MessagesStorage.this.lambda$updateMessageVerifyFlags$188(arrayList);
            }
        });
    }

    public void updateMessageVoiceTranscription(final long j, final int i, final String str, final long j2, final boolean z) {
        this.storageQueue.postRunnable(new Runnable() {
            @Override
            public final void run() {
                MessagesStorage.this.lambda$updateMessageVoiceTranscription$102(i, j, z, j2, str);
            }
        });
    }

    public void updateMessageVoiceTranscription(final long j, final int i, final String str, final TLRPC.Message message) {
        this.storageQueue.postRunnable(new Runnable() {
            @Override
            public final void run() {
                MessagesStorage.this.lambda$updateMessageVoiceTranscription$103(i, j, message, str);
            }
        });
    }

    public void updateMessageVoiceTranscriptionOpen(final long j, final int i, final TLRPC.Message message) {
        this.storageQueue.postRunnable(new Runnable() {
            @Override
            public final void run() {
                MessagesStorage.this.lambda$updateMessageVoiceTranscriptionOpen$101(i, j, message);
            }
        });
    }

    public void updateMutedDialogsFiltersCounters() {
        this.storageQueue.postRunnable(new Runnable() {
            @Override
            public final void run() {
                MessagesStorage.this.lambda$updateMutedDialogsFiltersCounters$36();
            }
        });
    }

    public void updatePinnedMessages(final long j, final ArrayList<Integer> arrayList, final boolean z, final int i, final int i2, final boolean z2, final HashMap<Integer, MessageObject> hashMap) {
        this.storageQueue.postRunnable(new Runnable() {
            @Override
            public final void run() {
                MessagesStorage.this.lambda$updatePinnedMessages$132(z, hashMap, i2, j, arrayList, i, z2);
            }
        });
    }

    public void updateRepliesCount(final long j, final int i, final ArrayList<TLRPC.Peer> arrayList, final int i2, final int i3) {
        this.storageQueue.postRunnable(new Runnable() {
            @Override
            public final void run() {
                MessagesStorage.this.lambda$updateRepliesCount$187(i, j, i3, arrayList, i2);
            }
        });
    }

    public void updateRepliesMaxReadId(final long j, final int i, final int i2, final int i3, boolean z) {
        if (z) {
            this.storageQueue.postRunnable(new Runnable() {
                @Override
                public final void run() {
                    MessagesStorage.this.lambda$updateRepliesMaxReadId$186(j, i, i2, i3);
                }
            });
        } else {
            lambda$updateRepliesMaxReadId$186(j, i, i2, i3);
        }
    }

    public void updateTopicData(long j, TLRPC.TL_forumTopic tL_forumTopic, int i) {
        updateTopicData(j, tL_forumTopic, i, getConnectionsManager().getCurrentTime());
    }

    public void updateTopicData(final long j, final TLRPC.TL_forumTopic tL_forumTopic, final int i, final int i2) {
        if (tL_forumTopic == null) {
            return;
        }
        this.storageQueue.postRunnable(new Runnable() {
            @Override
            public final void run() {
                MessagesStorage.this.lambda$updateTopicData$47(i, tL_forumTopic, j, i2);
            }
        });
    }

    public void updateTopicsWithReadMessages(final HashMap<TopicKey, Integer> hashMap) {
        this.storageQueue.postRunnable(new Runnable() {
            @Override
            public final void run() {
                MessagesStorage.this.lambda$updateTopicsWithReadMessages$56(hashMap);
            }
        });
    }

    public void updateUnreadReactionsCount(long j, long j2, int i) {
        updateUnreadReactionsCount(j, j2, i, false);
    }

    public void updateUnreadReactionsCount(final long j, final long j2, final int i, final boolean z) {
        this.storageQueue.postRunnable(new Runnable() {
            @Override
            public final void run() {
                MessagesStorage.this.lambda$updateUnreadReactionsCount$242(j2, z, j, i);
            }
        });
    }

    public void updateUserInfo(final TLRPC.UserFull userFull, final boolean z) {
        this.storageQueue.postRunnable(new Runnable() {
            @Override
            public final void run() {
                MessagesStorage.this.lambda$updateUserInfo$124(userFull, z);
            }
        });
    }

    public void updateUserInfoPremiumBlocked(final long j, final boolean z) {
        this.storageQueue.postRunnable(new Runnable() {
            @Override
            public final void run() {
                MessagesStorage.this.lambda$updateUserInfoPremiumBlocked$125(j, z);
            }
        });
    }

    public void updateUsers(final ArrayList<TLRPC.User> arrayList, final boolean z, final boolean z2, boolean z3) {
        if (arrayList == null || arrayList.isEmpty()) {
            return;
        }
        if (z3) {
            this.storageQueue.postRunnable(new Runnable() {
                @Override
                public final void run() {
                    MessagesStorage.this.lambda$updateUsers$198(arrayList, z, z2);
                }
            });
        } else {
            lambda$updateUsers$198(arrayList, z, z2);
        }
    }
}
