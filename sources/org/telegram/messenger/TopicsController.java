package org.telegram.messenger;

import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.text.TextPaint;
import android.util.SparseArray;
import androidx.collection.LongSparseArray;
import j$.util.function.Consumer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import org.telegram.messenger.MessagesStorage;
import org.telegram.messenger.support.LongSparseIntArray;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.Components.Forum.ForumUtilities;

public class TopicsController extends BaseController {
    public static final int LOAD_TYPE_LOAD_NEXT = 1;
    public static final int LOAD_TYPE_LOAD_UNKNOWN = 2;
    public static final int LOAD_TYPE_PRELOAD = 0;
    private static final int MAX_PRELOAD_COUNT = 20;
    public static final int TOPIC_FLAG_CLOSE = 8;
    public static final int TOPIC_FLAG_HIDE = 32;
    public static final int TOPIC_FLAG_ICON = 2;
    public static final int TOPIC_FLAG_PIN = 4;
    public static final int TOPIC_FLAG_TITLE = 1;
    public static final int TOPIC_FLAG_TOTAL_MESSAGES_COUNT = 16;
    private static final int[] countsTmp = new int[4];
    LongSparseIntArray currentOpenTopicsCounter;
    LongSparseIntArray endIsReached;
    LongSparseArray offsets;
    LongSparseIntArray openedTopicsBuChatId;
    LongSparseArray topicsByChatId;
    LongSparseArray topicsByTopMsgId;
    LongSparseIntArray topicsIsLoading;
    LongSparseArray topicsMapByChatId;

    public static class TopicUpdate {
        long dialogId;
        ArrayList<MessageObject> groupedMessages;
        boolean onlyCounters;
        boolean reloadTopic;
        TLRPC.Message topMessage;
        int topMessageId;
        long topicId;
        public int totalMessagesCount = -1;
        int unreadCount;
        int unreadMentions;
    }

    public class TopicsLoadOffset {
        int lastMessageDate;
        int lastMessageId;
        int lastTopicId;

        private TopicsLoadOffset() {
        }
    }

    public TopicsController(int i) {
        super(i);
        this.topicsByChatId = new LongSparseArray();
        this.topicsMapByChatId = new LongSparseArray();
        this.topicsIsLoading = new LongSparseIntArray();
        this.endIsReached = new LongSparseIntArray();
        this.topicsByTopMsgId = new LongSparseArray();
        this.currentOpenTopicsCounter = new LongSparseIntArray();
        this.openedTopicsBuChatId = new LongSparseIntArray();
        this.offsets = new LongSparseArray();
    }

    public void deleteTopic(final long j, final int i, int i2) {
        TLRPC.TL_channels_deleteTopicHistory tL_channels_deleteTopicHistory = new TLRPC.TL_channels_deleteTopicHistory();
        tL_channels_deleteTopicHistory.channel = getMessagesController().getInputChannel(j);
        tL_channels_deleteTopicHistory.top_msg_id = i;
        if (i2 == 0) {
            getMessagesStorage().removeTopic(-j, i);
        }
        ConnectionsManager.getInstance(this.currentAccount).sendRequest(tL_channels_deleteTopicHistory, new RequestDelegate() {
            @Override
            public void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                if (tL_error == null) {
                    TLRPC.TL_messages_affectedHistory tL_messages_affectedHistory = (TLRPC.TL_messages_affectedHistory) tLObject;
                    TopicsController.this.getMessagesController().processNewChannelDifferenceParams(tL_messages_affectedHistory.pts, tL_messages_affectedHistory.pts_count, j);
                    int i3 = tL_messages_affectedHistory.offset;
                    if (i3 > 0) {
                        TopicsController.this.deleteTopic(j, i, i3);
                    }
                }
            }
        });
    }

    public void lambda$applyPinnedOrder$13() {
        NotificationCenter.getInstance(this.currentAccount).lambda$postNotificationNameOnUIThread$1(NotificationCenter.updateInterfaces, Integer.valueOf(MessagesController.UPDATE_MASK_SELECT_DIALOG));
    }

    public void lambda$databaseCleared$21() {
        this.topicsByChatId.clear();
        this.topicsMapByChatId.clear();
        this.endIsReached.clear();
        SharedPreferences.Editor edit = getUserConfig().getPreferences().edit();
        for (String str : getUserConfig().getPreferences().getAll().keySet()) {
            if (str.startsWith("topics_load_offset_message_id_")) {
                edit.remove(str);
            }
            if (str.startsWith("topics_load_offset_date_")) {
                edit.remove(str);
            }
            if (str.startsWith("topics_load_offset_topic_id_")) {
                edit.remove(str);
            }
            if (str.startsWith("topics_end_reached_")) {
                edit.remove(str);
            }
        }
        edit.apply();
    }

    public void lambda$getTopicRepliesCount$25(TLObject tLObject, TLRPC.TL_forumTopic tL_forumTopic, long j) {
        if (tLObject != null) {
            tL_forumTopic.totalMessagesCount = ((TLRPC.messages_Messages) tLObject).count;
            getMessagesStorage().updateTopicData(j, tL_forumTopic, 16);
            NotificationCenter.getInstance(this.currentAccount).lambda$postNotificationNameOnUIThread$1(NotificationCenter.topicsDidLoaded, Long.valueOf(-j), Boolean.TRUE);
        }
    }

    public void lambda$getTopicRepliesCount$26(final TLRPC.TL_forumTopic tL_forumTopic, final long j, final TLObject tLObject, TLRPC.TL_error tL_error) {
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                TopicsController.this.lambda$getTopicRepliesCount$25(tLObject, tL_forumTopic, j);
            }
        });
    }

    public void lambda$loadTopic$23(long j, ArrayList arrayList, long j2, Runnable runnable) {
        if (BuildVars.LOGS_ENABLED) {
            StringBuilder sb = new StringBuilder();
            sb.append("loaded from cache ");
            sb.append(j);
            sb.append(" topics_count=");
            sb.append(arrayList == null ? 0 : arrayList.size());
            FileLog.d(sb.toString());
        }
        processTopics(j, arrayList, null, true, 0, -1);
        sortTopics(j);
        if (findTopic(j, j2) != null) {
            runnable.run();
            return;
        }
        ArrayList<TLRPC.TL_forumTopic> arrayList2 = new ArrayList<>();
        new TLRPC.TL_forumTopic().id = (int) j2;
        reloadTopics(j, arrayList2, runnable);
    }

    public void lambda$loadTopic$24(final long j, final long j2, final Runnable runnable, final ArrayList arrayList) {
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                TopicsController.this.lambda$loadTopic$23(j, arrayList, j2, runnable);
            }
        });
    }

    public void lambda$loadTopics$0(long j, ArrayList arrayList, boolean z, int i) {
        if (BuildVars.LOGS_ENABLED) {
            StringBuilder sb = new StringBuilder();
            sb.append("loaded from cache ");
            sb.append(j);
            sb.append(" topics_count=");
            sb.append(arrayList == null ? 0 : arrayList.size());
            FileLog.d(sb.toString());
        }
        this.topicsIsLoading.put(j, 0);
        processTopics(j, arrayList, null, z, i, -1);
        sortTopics(j);
    }

    public void lambda$loadTopics$1(final long j, final boolean z, final int i, final ArrayList arrayList) {
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                TopicsController.this.lambda$loadTopics$0(j, arrayList, z, i);
            }
        });
    }

    public void lambda$loadTopics$2(TLObject tLObject, long j, TLRPC.TL_messages_forumTopics tL_messages_forumTopics, SparseArray sparseArray, int i) {
        TLRPC.TL_messages_forumTopics tL_messages_forumTopics2 = (TLRPC.TL_messages_forumTopics) tLObject;
        getMessagesStorage().putUsersAndChats(tL_messages_forumTopics2.users, tL_messages_forumTopics2.chats, true, true);
        getMessagesController().putUsers(tL_messages_forumTopics2.users, false);
        getMessagesController().putChats(tL_messages_forumTopics2.chats, false);
        this.topicsIsLoading.put(j, 0);
        processTopics(j, tL_messages_forumTopics.topics, sparseArray, false, i, tL_messages_forumTopics2.count);
        sortTopics(j);
        getMessagesStorage().saveTopics(-j, (List) this.topicsByChatId.get(j), true, true, getConnectionsManager().getCurrentTime());
        getMessagesStorage().putMessages(tL_messages_forumTopics.messages, false, true, false, 0, false, 0, 0L);
        if (!tL_messages_forumTopics.topics.isEmpty() && i == 1) {
            ArrayList<TLRPC.TL_forumTopic> arrayList = tL_messages_forumTopics.topics;
            TLRPC.TL_forumTopic tL_forumTopic = arrayList.get(arrayList.size() - 1);
            TLRPC.Message message = (TLRPC.Message) sparseArray.get(tL_forumTopic.top_message);
            saveLoadOffset(j, tL_forumTopic.top_message, message == null ? 0 : message.date, tL_forumTopic.id);
            return;
        }
        if (getTopics(j) == null || getTopics(j).size() < tL_messages_forumTopics.count) {
            clearLoadingOffset(j);
            loadTopics(j);
        }
    }

    public void lambda$loadTopics$3(long j) {
        this.topicsIsLoading.put(j, 0);
        getNotificationCenter().lambda$postNotificationNameOnUIThread$1(NotificationCenter.topicsDidLoaded, Long.valueOf(j), Boolean.FALSE);
    }

    public void lambda$loadTopics$4(final long j, final int i, final TLObject tLObject, TLRPC.TL_error tL_error) {
        if (tLObject == null) {
            AndroidUtilities.runOnUIThread(new Runnable() {
                @Override
                public final void run() {
                    TopicsController.this.lambda$loadTopics$3(j);
                }
            });
            return;
        }
        final SparseArray sparseArray = new SparseArray();
        final TLRPC.TL_messages_forumTopics tL_messages_forumTopics = (TLRPC.TL_messages_forumTopics) tLObject;
        for (int i2 = 0; i2 < tL_messages_forumTopics.messages.size(); i2++) {
            sparseArray.put(tL_messages_forumTopics.messages.get(i2).id, tL_messages_forumTopics.messages.get(i2));
        }
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                TopicsController.this.lambda$loadTopics$2(tLObject, j, tL_messages_forumTopics, sparseArray, i);
            }
        });
    }

    public void lambda$onTopicsDeletedServerSide$19(ArrayList arrayList) {
        HashSet hashSet = new HashSet();
        for (int i = 0; i < arrayList.size(); i++) {
            MessagesStorage.TopicKey topicKey = (MessagesStorage.TopicKey) arrayList.get(i);
            long j = -topicKey.dialogId;
            LongSparseArray longSparseArray = (LongSparseArray) this.topicsMapByChatId.get(j);
            if (longSparseArray != null) {
                longSparseArray.remove(topicKey.topicId);
            }
            ArrayList arrayList2 = (ArrayList) this.topicsByChatId.get(j);
            if (arrayList2 != null) {
                int i2 = 0;
                while (true) {
                    if (i2 >= arrayList2.size()) {
                        break;
                    }
                    if (((TLRPC.TL_forumTopic) arrayList2.get(i2)).id == topicKey.topicId) {
                        arrayList2.remove(i2);
                        getNotificationCenter().lambda$postNotificationNameOnUIThread$1(NotificationCenter.dialogDeleted, Long.valueOf(-j), Long.valueOf(topicKey.topicId));
                        hashSet.add(Long.valueOf(j));
                        break;
                    }
                    i2++;
                }
            }
        }
        Iterator it = hashSet.iterator();
        while (it.hasNext()) {
            sortTopics(((Long) it.next()).longValue(), true);
        }
    }

    public void lambda$pinTopic$15(BaseFragment baseFragment) {
        baseFragment.showDialog(new AlertDialog.Builder(baseFragment.getContext()).setTitle(LocaleController.getString(R.string.LimitReached)).setMessage(LocaleController.formatString("LimitReachedPinnedTopics", R.string.LimitReachedPinnedTopics, Integer.valueOf(MessagesController.getInstance(this.currentAccount).topicsPinnedLimit))).setPositiveButton(LocaleController.getString(R.string.OK), null).create());
    }

    public void lambda$pinTopic$16(final BaseFragment baseFragment, long j, ArrayList arrayList, TLObject tLObject, TLRPC.TL_error tL_error) {
        if (tL_error != null) {
            if (!"PINNED_TOO_MUCH".equals(tL_error.text)) {
                if ("PINNED_TOPIC_NOT_MODIFIED".equals(tL_error.text)) {
                    reloadTopics(j, false);
                }
            } else {
                if (baseFragment == null) {
                    return;
                }
                applyPinnedOrder(j, arrayList);
                AndroidUtilities.runOnUIThread(new Runnable() {
                    @Override
                    public final void run() {
                        TopicsController.this.lambda$pinTopic$15(baseFragment);
                    }
                });
            }
        }
    }

    public void lambda$processTopics$5(long j) {
        loadTopics(j, false, 0);
    }

    public void lambda$processUpdate$18(List list) {
        HashSet hashSet = new HashSet();
        LongSparseArray longSparseArray = null;
        for (int i = 0; i < list.size(); i++) {
            TopicUpdate topicUpdate = (TopicUpdate) list.get(i);
            if (topicUpdate.reloadTopic) {
                if (longSparseArray == null) {
                    longSparseArray = new LongSparseArray();
                }
                ArrayList arrayList = (ArrayList) longSparseArray.get(topicUpdate.dialogId);
                if (arrayList == null) {
                    arrayList = new ArrayList();
                    longSparseArray.put(topicUpdate.dialogId, arrayList);
                }
                TLRPC.TL_forumTopic tL_forumTopic = new TLRPC.TL_forumTopic();
                tL_forumTopic.id = (int) topicUpdate.topicId;
                arrayList.add(tL_forumTopic);
            } else {
                TLRPC.TL_forumTopic findTopic = findTopic(-topicUpdate.dialogId, topicUpdate.topicId);
                if (findTopic != null) {
                    if (topicUpdate.onlyCounters) {
                        int i2 = topicUpdate.unreadCount;
                        if (i2 >= 0) {
                            findTopic.unread_count = i2;
                        }
                        int i3 = topicUpdate.unreadMentions;
                        if (i3 >= 0) {
                            findTopic.unread_mentions_count = i3;
                        }
                    } else {
                        this.topicsByTopMsgId.remove(messageHash(findTopic.top_message, -topicUpdate.dialogId));
                        findTopic.topMessage = topicUpdate.topMessage;
                        findTopic.groupedMessages = topicUpdate.groupedMessages;
                        int i4 = topicUpdate.topMessageId;
                        findTopic.top_message = i4;
                        findTopic.unread_count = topicUpdate.unreadCount;
                        findTopic.unread_mentions_count = topicUpdate.unreadMentions;
                        this.topicsByTopMsgId.put(messageHash(i4, -topicUpdate.dialogId), findTopic);
                    }
                    int i5 = topicUpdate.totalMessagesCount;
                    if (i5 > 0) {
                        findTopic.totalMessagesCount = i5;
                    }
                    hashSet.add(Long.valueOf(-topicUpdate.dialogId));
                }
            }
        }
        Iterator it = hashSet.iterator();
        while (it.hasNext()) {
            sortTopics(((Long) it.next()).longValue(), true);
        }
        if (longSparseArray != null) {
            for (int i6 = 0; i6 < longSparseArray.size(); i6++) {
                reloadTopics(-longSparseArray.keyAt(i6), (ArrayList) longSparseArray.valueAt(i6), null);
            }
        }
    }

    public void lambda$reloadTopics$10(TLObject tLObject, long j, TLRPC.TL_messages_forumTopics tL_messages_forumTopics, SparseArray sparseArray, Runnable runnable) {
        TLRPC.TL_messages_forumTopics tL_messages_forumTopics2 = (TLRPC.TL_messages_forumTopics) tLObject;
        getMessagesController().putUsers(tL_messages_forumTopics2.users, false);
        getMessagesController().putChats(tL_messages_forumTopics2.chats, false);
        processTopics(j, tL_messages_forumTopics.topics, sparseArray, false, 2, -1);
        getMessagesStorage().saveTopics(-j, (List) this.topicsByChatId.get(j), true, true, getConnectionsManager().getCurrentTime());
        getMessagesStorage().putMessages(tL_messages_forumTopics.messages, false, true, false, 0, false, 0, 0L);
        if (runnable != null) {
            runnable.run();
        }
    }

    public void lambda$reloadTopics$11(final TLObject tLObject, final long j, final Runnable runnable) {
        if (tLObject != null) {
            final SparseArray sparseArray = new SparseArray();
            final TLRPC.TL_messages_forumTopics tL_messages_forumTopics = (TLRPC.TL_messages_forumTopics) tLObject;
            for (int i = 0; i < tL_messages_forumTopics.messages.size(); i++) {
                sparseArray.put(tL_messages_forumTopics.messages.get(i).id, tL_messages_forumTopics.messages.get(i));
            }
            AndroidUtilities.runOnUIThread(new Runnable() {
                @Override
                public final void run() {
                    TopicsController.this.lambda$reloadTopics$10(tLObject, j, tL_messages_forumTopics, sparseArray, runnable);
                }
            });
        }
    }

    public void lambda$reloadTopics$12(final long j, final Runnable runnable, final TLObject tLObject, TLRPC.TL_error tL_error) {
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                TopicsController.this.lambda$reloadTopics$11(tLObject, j, runnable);
            }
        });
    }

    public void lambda$reloadTopics$20(long j, boolean z) {
        getUserConfig().getPreferences().edit().remove("topics_end_reached_" + j).apply();
        this.topicsByChatId.remove(j);
        this.topicsMapByChatId.remove(j);
        this.endIsReached.delete(j);
        clearLoadingOffset(j);
        TLRPC.Chat chat = getMessagesController().getChat(Long.valueOf(j));
        if (chat != null && chat.forum) {
            loadTopics(j, z, 0);
        }
        sortTopics(j);
    }

    public static int lambda$sortTopics$6(TLRPC.TL_forumTopic tL_forumTopic, TLRPC.TL_forumTopic tL_forumTopic2) {
        boolean z = tL_forumTopic.hidden;
        if (z != tL_forumTopic2.hidden) {
            return z ? -1 : 1;
        }
        boolean z2 = tL_forumTopic.pinned;
        boolean z3 = tL_forumTopic2.pinned;
        if (z2 != z3) {
            return z2 ? -1 : 1;
        }
        if (z2 && z3) {
            return tL_forumTopic.pinnedOrder - tL_forumTopic2.pinnedOrder;
        }
        TLRPC.Message message = tL_forumTopic2.topMessage;
        int i = message != null ? message.date : 0;
        TLRPC.Message message2 = tL_forumTopic.topMessage;
        return i - (message2 != null ? message2.date : 0);
    }

    public void lambda$toggleViewForumAsMessages$14(TLObject tLObject, TLRPC.TL_error tL_error) {
        if (tLObject != null) {
            getMessagesController().processUpdates((TLRPC.Updates) tLObject, false);
        }
    }

    public void lambda$updateMentionsUnread$17(long j, long j2, int i) {
        long j3 = -j;
        TLRPC.TL_forumTopic findTopic = findTopic(j3, j2);
        if (findTopic != null) {
            findTopic.unread_mentions_count = i;
            sortTopics(j3, true);
        }
    }

    public void lambda$updateReadOutbox$22(HashMap hashMap) {
        HashSet hashSet = new HashSet();
        for (MessagesStorage.TopicKey topicKey : hashMap.keySet()) {
            int intValue = ((Integer) hashMap.get(topicKey)).intValue();
            TLRPC.TL_forumTopic findTopic = findTopic(-topicKey.dialogId, topicKey.topicId);
            if (findTopic != null) {
                findTopic.read_outbox_max_id = Math.max(findTopic.read_outbox_max_id, intValue);
                hashSet.add(Long.valueOf(-topicKey.dialogId));
                TLRPC.Message message = findTopic.topMessage;
                if (message != null && findTopic.read_outbox_max_id >= message.id) {
                    message.unread = false;
                }
            }
        }
        Iterator it = hashSet.iterator();
        while (it.hasNext()) {
            NotificationCenter.getInstance(this.currentAccount).lambda$postNotificationNameOnUIThread$1(NotificationCenter.topicsDidLoaded, (Long) it.next(), Boolean.TRUE);
        }
    }

    public void lambda$updateTopicsWithDeletedMessages$7(ArrayList arrayList, long j) {
        ArrayList<TLRPC.TL_forumTopic> arrayList2 = null;
        boolean z = false;
        for (int i = 0; i < arrayList.size(); i++) {
            TLRPC.TL_forumTopic tL_forumTopic = (TLRPC.TL_forumTopic) arrayList.get(i);
            LongSparseArray longSparseArray = (LongSparseArray) this.topicsMapByChatId.get(j);
            if (longSparseArray != null) {
                TLRPC.TL_forumTopic tL_forumTopic2 = (TLRPC.TL_forumTopic) longSparseArray.get(tL_forumTopic.id);
                if (tL_forumTopic2 != null && tL_forumTopic.top_message != -1 && tL_forumTopic.topMessage != null) {
                    this.topicsByTopMsgId.remove(messageHash(tL_forumTopic2.top_message, j));
                    TLRPC.Message message = tL_forumTopic.topMessage;
                    int i2 = message.id;
                    tL_forumTopic2.top_message = i2;
                    tL_forumTopic2.topMessage = message;
                    tL_forumTopic2.groupedMessages = tL_forumTopic.groupedMessages;
                    this.topicsByTopMsgId.put(messageHash(i2, j), tL_forumTopic2);
                    z = true;
                } else if (tL_forumTopic.top_message == -1 || tL_forumTopic.topMessage == null) {
                    if (arrayList2 == null) {
                        arrayList2 = new ArrayList<>();
                    }
                    arrayList2.add(tL_forumTopic);
                }
            }
        }
        if (z) {
            sortTopics(j);
        }
        if (arrayList2 != null) {
            reloadTopics(j, arrayList2, null);
        }
    }

    public void lambda$updateTopicsWithDeletedMessages$8(long r17, java.util.ArrayList r19, final long r20) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.TopicsController.lambda$updateTopicsWithDeletedMessages$8(long, java.util.ArrayList, long):void");
    }

    public void lambda$updateTopicsWithDeletedMessages$9(final long j, final ArrayList arrayList, final long j2) {
        getMessagesStorage().getStorageQueue().postRunnable(new Runnable() {
            @Override
            public final void run() {
                TopicsController.this.lambda$updateTopicsWithDeletedMessages$8(j, arrayList, j2);
            }
        });
    }

    private long messageHash(int i, long j) {
        return j + (i << 12);
    }

    private void sortTopics(long j) {
        sortTopics(j, true);
    }

    public void applyPinnedOrder(long j, ArrayList<Integer> arrayList) {
        applyPinnedOrder(j, arrayList, true);
    }

    public void applyPinnedOrder(long j, ArrayList<Integer> arrayList, boolean z) {
        if (arrayList == null) {
            return;
        }
        ArrayList<TLRPC.TL_forumTopic> topics = getTopics(j);
        boolean z2 = true;
        if (topics != null) {
            boolean z3 = false;
            for (int i = 0; i < topics.size(); i++) {
                TLRPC.TL_forumTopic tL_forumTopic = topics.get(i);
                if (tL_forumTopic != null) {
                    int indexOf = arrayList.indexOf(Integer.valueOf(tL_forumTopic.id));
                    boolean z4 = indexOf >= 0;
                    if (tL_forumTopic.pinned != z4 || (z4 && tL_forumTopic.pinnedOrder != indexOf)) {
                        tL_forumTopic.pinned = z4;
                        tL_forumTopic.pinnedOrder = indexOf;
                        getMessagesStorage().updateTopicData(j, tL_forumTopic, 4);
                        z3 = true;
                    }
                }
            }
            z2 = z3;
        }
        if (z && z2) {
            AndroidUtilities.runOnUIThread(new Runnable() {
                @Override
                public final void run() {
                    TopicsController.this.lambda$applyPinnedOrder$13();
                }
            });
        }
    }

    public void clearLoadingOffset(long j) {
        this.offsets.remove(j);
    }

    public void databaseCleared() {
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                TopicsController.this.lambda$databaseCleared$21();
            }
        });
    }

    public void deleteTopics(long j, ArrayList<Integer> arrayList) {
        ArrayList arrayList2 = (ArrayList) this.topicsByChatId.get(j);
        LongSparseArray longSparseArray = (LongSparseArray) this.topicsMapByChatId.get(j);
        if (longSparseArray != null && arrayList2 != null) {
            for (int i = 0; i < arrayList.size(); i++) {
                long intValue = arrayList.get(i).intValue();
                TLRPC.TL_forumTopic tL_forumTopic = (TLRPC.TL_forumTopic) longSparseArray.get(intValue);
                longSparseArray.remove(intValue);
                if (tL_forumTopic != null) {
                    this.topicsByTopMsgId.remove(messageHash(tL_forumTopic.top_message, j));
                    arrayList2.remove(tL_forumTopic);
                }
            }
            sortTopics(j);
        }
        for (int i2 = 0; i2 < arrayList.size(); i2++) {
            deleteTopic(j, arrayList.get(i2).intValue(), 0);
        }
    }

    public boolean endIsReached(long j) {
        return this.endIsReached.get(j, 0) == 1;
    }

    public TLRPC.TL_forumTopic findTopic(long j, long j2) {
        LongSparseArray longSparseArray = (LongSparseArray) this.topicsMapByChatId.get(j);
        if (longSparseArray != null) {
            return (TLRPC.TL_forumTopic) longSparseArray.get(j2);
        }
        return null;
    }

    public ArrayList<Integer> getCurrentPinnedOrder(long j) {
        ArrayList<TLRPC.TL_forumTopic> topics = getTopics(j);
        ArrayList<Integer> arrayList = new ArrayList<>();
        if (topics != null) {
            for (int i = 0; i < topics.size(); i++) {
                TLRPC.TL_forumTopic tL_forumTopic = topics.get(i);
                if (tL_forumTopic != null && tL_forumTopic.pinned) {
                    arrayList.add(Integer.valueOf(tL_forumTopic.id));
                }
            }
        }
        return arrayList;
    }

    public int[] getForumUnreadCount(long j) {
        ArrayList arrayList = (ArrayList) this.topicsByChatId.get(j);
        Arrays.fill(countsTmp, 0);
        if (arrayList != null) {
            for (int i = 0; i < arrayList.size(); i++) {
                TLRPC.TL_forumTopic tL_forumTopic = (TLRPC.TL_forumTopic) arrayList.get(i);
                int[] iArr = countsTmp;
                iArr[0] = iArr[0] + (tL_forumTopic.unread_count > 0 ? 1 : 0);
                iArr[1] = iArr[1] + (tL_forumTopic.unread_mentions_count > 0 ? 1 : 0);
                iArr[2] = iArr[2] + (tL_forumTopic.unread_reactions_count <= 0 ? 0 : 1);
                if (!getMessagesController().isDialogMuted(-j, tL_forumTopic.id)) {
                    iArr[3] = iArr[3] + tL_forumTopic.unread_count;
                }
            }
        }
        return countsTmp;
    }

    public TopicsLoadOffset getLoadOffset(long j) {
        TopicsLoadOffset topicsLoadOffset = (TopicsLoadOffset) this.offsets.get(j);
        return topicsLoadOffset != null ? topicsLoadOffset : new TopicsLoadOffset();
    }

    public CharSequence getTopicIconName(TLRPC.Chat chat, MessageObject messageObject, TextPaint textPaint) {
        return getTopicIconName(chat, messageObject, textPaint, null);
    }

    public CharSequence getTopicIconName(TLRPC.Chat chat, MessageObject messageObject, TextPaint textPaint, Drawable[] drawableArr) {
        TLRPC.TL_forumTopic findTopic;
        TLRPC.MessageReplyHeader messageReplyHeader = messageObject.messageOwner.reply_to;
        if (messageReplyHeader == null) {
            return null;
        }
        int i = messageReplyHeader.reply_to_top_id;
        if (i == 0) {
            i = messageReplyHeader.reply_to_msg_id;
        }
        if (i == 0 || (findTopic = findTopic(chat.id, i)) == null) {
            return null;
        }
        return ForumUtilities.getTopicSpannedName(findTopic, textPaint, drawableArr, false);
    }

    public String getTopicName(TLRPC.Chat chat, MessageObject messageObject) {
        TLRPC.TL_forumTopic findTopic;
        TLRPC.MessageReplyHeader messageReplyHeader = messageObject.messageOwner.reply_to;
        if (messageReplyHeader == null) {
            return null;
        }
        int i = messageReplyHeader.reply_to_top_id;
        if (i == 0) {
            i = messageReplyHeader.reply_to_msg_id;
        }
        return (i == 0 || (findTopic = findTopic(chat.id, (long) i)) == null) ? "" : findTopic.title;
    }

    public void getTopicRepliesCount(final long j, long j2) {
        final TLRPC.TL_forumTopic findTopic = findTopic(-j, j2);
        if (findTopic == null || findTopic.totalMessagesCount != 0) {
            return;
        }
        TLRPC.TL_messages_getReplies tL_messages_getReplies = new TLRPC.TL_messages_getReplies();
        tL_messages_getReplies.peer = getMessagesController().getInputPeer(j);
        tL_messages_getReplies.msg_id = (int) j2;
        tL_messages_getReplies.limit = 1;
        getConnectionsManager().sendRequest(tL_messages_getReplies, new RequestDelegate() {
            @Override
            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                TopicsController.this.lambda$getTopicRepliesCount$26(findTopic, j, tLObject, tL_error);
            }
        });
    }

    public ArrayList<TLRPC.TL_forumTopic> getTopics(long j) {
        return (ArrayList) this.topicsByChatId.get(j);
    }

    public boolean isLoading(long j) {
        if (this.topicsIsLoading.get(j, 0) == 1) {
            return this.topicsByChatId.get(j) == null || ((ArrayList) this.topicsByChatId.get(j)).isEmpty();
        }
        return false;
    }

    public void loadTopic(final long j, final long j2, final Runnable runnable) {
        getMessagesStorage().loadTopics(-j, new Consumer() {
            @Override
            public final void r(Object obj) {
                TopicsController.this.lambda$loadTopic$24(j, j2, runnable, (ArrayList) obj);
            }

            @Override
            public Consumer andThen(Consumer consumer) {
                return Consumer.CC.$default$andThen(this, consumer);
            }
        });
    }

    public void loadTopics(long j) {
        loadTopics(j, false, 1);
    }

    public void loadTopics(final long j, final boolean z, final int i) {
        if (this.topicsIsLoading.get(j, 0) != 0) {
            return;
        }
        if (BuildVars.LOGS_ENABLED) {
            FileLog.d("load topics " + j + " fromCache=" + z + " loadType=" + i);
        }
        this.topicsIsLoading.put(j, 1);
        if (z) {
            getMessagesStorage().loadTopics(-j, new Consumer() {
                @Override
                public final void r(Object obj) {
                    TopicsController.this.lambda$loadTopics$1(j, z, i, (ArrayList) obj);
                }

                @Override
                public Consumer andThen(Consumer consumer) {
                    return Consumer.CC.$default$andThen(this, consumer);
                }
            });
            return;
        }
        TLRPC.TL_channels_getForumTopics tL_channels_getForumTopics = new TLRPC.TL_channels_getForumTopics();
        tL_channels_getForumTopics.channel = getMessagesController().getInputChannel(j);
        if (i == 0) {
            tL_channels_getForumTopics.limit = 20;
        } else if (i == 1) {
            tL_channels_getForumTopics.limit = 100;
            TopicsLoadOffset loadOffset = getLoadOffset(j);
            tL_channels_getForumTopics.offset_date = loadOffset.lastMessageDate;
            tL_channels_getForumTopics.offset_id = loadOffset.lastMessageId;
            tL_channels_getForumTopics.offset_topic = loadOffset.lastTopicId;
            if (BuildVars.LOGS_ENABLED) {
                FileLog.d("offset_date=" + loadOffset.lastMessageDate + " offset_id=" + loadOffset.lastMessageId + " offset_topic=" + loadOffset.lastTopicId);
            }
        }
        getConnectionsManager().sendRequest(tL_channels_getForumTopics, new RequestDelegate() {
            @Override
            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                TopicsController.this.lambda$loadTopics$4(j, i, tLObject, tL_error);
            }
        });
    }

    public void markAllReactionsAsRead(long j, long j2) {
        TLRPC.TL_forumTopic findTopic = findTopic(j, j2);
        if (findTopic == null || findTopic.unread_reactions_count <= 0) {
            return;
        }
        findTopic.unread_reactions_count = 0;
        sortTopics(j);
    }

    public void onTopicCreated(long j, TLRPC.TL_forumTopic tL_forumTopic, boolean z) {
        long j2 = -j;
        LongSparseArray longSparseArray = (LongSparseArray) this.topicsMapByChatId.get(j2);
        if (findTopic(j2, tL_forumTopic.id) != null) {
            return;
        }
        if (longSparseArray == null) {
            longSparseArray = new LongSparseArray();
            this.topicsMapByChatId.put(j2, longSparseArray);
        }
        ArrayList arrayList = (ArrayList) this.topicsByChatId.get(j2);
        if (arrayList == null) {
            arrayList = new ArrayList();
            this.topicsByChatId.put(j2, arrayList);
        }
        longSparseArray.put(tL_forumTopic.id, tL_forumTopic);
        arrayList.add(tL_forumTopic);
        if (z) {
            getMessagesStorage().saveTopics(j, Collections.singletonList(tL_forumTopic), false, true, getConnectionsManager().getCurrentTime());
        }
        sortTopics(j2, true);
    }

    public void onTopicEdited(long j, TLRPC.TL_forumTopic tL_forumTopic) {
        getMessagesStorage().updateTopicData(j, tL_forumTopic, 35);
        sortTopics(-j);
    }

    public void onTopicFragmentPause(long j) {
        int i = this.openedTopicsBuChatId.get(j, 0) - 1;
        this.openedTopicsBuChatId.put(j, i >= 0 ? i : 0);
    }

    public void onTopicFragmentResume(long j) {
        this.openedTopicsBuChatId.put(j, this.openedTopicsBuChatId.get(j, 0) + 1);
        sortTopics(j);
    }

    public void onTopicsDeletedServerSide(final ArrayList<MessagesStorage.TopicKey> arrayList) {
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                TopicsController.this.lambda$onTopicsDeletedServerSide$19(arrayList);
            }
        });
    }

    public void pinTopic(final long j, int i, boolean z, final BaseFragment baseFragment) {
        TLRPC.TL_channels_updatePinnedForumTopic tL_channels_updatePinnedForumTopic = new TLRPC.TL_channels_updatePinnedForumTopic();
        tL_channels_updatePinnedForumTopic.channel = getMessagesController().getInputChannel(j);
        tL_channels_updatePinnedForumTopic.topic_id = i;
        tL_channels_updatePinnedForumTopic.pinned = z;
        final ArrayList<Integer> currentPinnedOrder = getCurrentPinnedOrder(j);
        ArrayList<Integer> arrayList = new ArrayList<>(currentPinnedOrder);
        arrayList.remove(Integer.valueOf(i));
        if (z) {
            arrayList.add(0, Integer.valueOf(i));
        }
        applyPinnedOrder(j, arrayList);
        ConnectionsManager.getInstance(this.currentAccount).sendRequest(tL_channels_updatePinnedForumTopic, new RequestDelegate() {
            @Override
            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                TopicsController.this.lambda$pinTopic$16(baseFragment, j, currentPinnedOrder, tLObject, tL_error);
            }
        });
    }

    public void preloadTopics(long j) {
        loadTopics(j, true, 0);
    }

    public void processEditedMessage(TLRPC.Message message) {
        TLRPC.TL_forumTopic tL_forumTopic = (TLRPC.TL_forumTopic) this.topicsByTopMsgId.get(messageHash(message.id, -message.dialog_id));
        if (tL_forumTopic != null) {
            tL_forumTopic.topMessage = message;
            sortTopics(-message.dialog_id, true);
        }
    }

    public void processEditedMessages(LongSparseArray longSparseArray) {
        HashSet hashSet = new HashSet();
        for (int i = 0; i < longSparseArray.size(); i++) {
            ArrayList arrayList = (ArrayList) longSparseArray.valueAt(i);
            for (int i2 = 0; i2 < arrayList.size(); i2++) {
                TLRPC.TL_forumTopic tL_forumTopic = (TLRPC.TL_forumTopic) this.topicsByTopMsgId.get(messageHash(((MessageObject) arrayList.get(i2)).getId(), -((MessageObject) arrayList.get(i2)).getDialogId()));
                if (tL_forumTopic != null) {
                    tL_forumTopic.topMessage = ((MessageObject) arrayList.get(i2)).messageOwner;
                    hashSet.add(Long.valueOf(-((MessageObject) arrayList.get(i2)).getDialogId()));
                }
            }
        }
        Iterator it = hashSet.iterator();
        while (it.hasNext()) {
            sortTopics(((Long) it.next()).longValue(), true);
        }
    }

    public void processTopics(final long r20, java.util.ArrayList<org.telegram.tgnet.TLRPC.TL_forumTopic> r22, android.util.SparseArray<org.telegram.tgnet.TLRPC.Message> r23, boolean r24, int r25, int r26) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.TopicsController.processTopics(long, java.util.ArrayList, android.util.SparseArray, boolean, int, int):void");
    }

    public void processUpdate(final List<TopicUpdate> list) {
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                TopicsController.this.lambda$processUpdate$18(list);
            }
        });
    }

    public void reloadTopics(long j) {
        reloadTopics(j, true);
    }

    public void reloadTopics(final long j, ArrayList<TLRPC.TL_forumTopic> arrayList, final Runnable runnable) {
        TLRPC.TL_channels_getForumTopicsByID tL_channels_getForumTopicsByID = new TLRPC.TL_channels_getForumTopicsByID();
        for (int i = 0; i < arrayList.size(); i++) {
            tL_channels_getForumTopicsByID.topics.add(Integer.valueOf(arrayList.get(i).id));
        }
        tL_channels_getForumTopicsByID.channel = getMessagesController().getInputChannel(j);
        getConnectionsManager().sendRequest(tL_channels_getForumTopicsByID, new RequestDelegate() {
            @Override
            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                TopicsController.this.lambda$reloadTopics$12(j, runnable, tLObject, tL_error);
            }
        });
    }

    public void reloadTopics(final long j, final boolean z) {
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                TopicsController.this.lambda$reloadTopics$20(j, z);
            }
        });
    }

    public void reorderPinnedTopics(long j, ArrayList<Integer> arrayList) {
        TLRPC.TL_channels_reorderPinnedForumTopics tL_channels_reorderPinnedForumTopics = new TLRPC.TL_channels_reorderPinnedForumTopics();
        tL_channels_reorderPinnedForumTopics.channel = getMessagesController().getInputChannel(j);
        if (arrayList != null) {
            tL_channels_reorderPinnedForumTopics.order.addAll(arrayList);
        }
        tL_channels_reorderPinnedForumTopics.force = true;
        applyPinnedOrder(j, arrayList, false);
        ConnectionsManager.getInstance(this.currentAccount).sendRequest(tL_channels_reorderPinnedForumTopics, null);
    }

    public void saveLoadOffset(long j, int i, int i2, int i3) {
        TopicsLoadOffset topicsLoadOffset = new TopicsLoadOffset();
        topicsLoadOffset.lastMessageId = i;
        topicsLoadOffset.lastMessageDate = i2;
        topicsLoadOffset.lastTopicId = i3;
        this.offsets.put(j, topicsLoadOffset);
    }

    public void sortTopics(long j, boolean z) {
        ArrayList arrayList = (ArrayList) this.topicsByChatId.get(j);
        if (arrayList != null) {
            if (this.openedTopicsBuChatId.get(j, 0) > 0) {
                Collections.sort(arrayList, new Comparator() {
                    @Override
                    public final int compare(Object obj, Object obj2) {
                        int lambda$sortTopics$6;
                        lambda$sortTopics$6 = TopicsController.lambda$sortTopics$6((TLRPC.TL_forumTopic) obj, (TLRPC.TL_forumTopic) obj2);
                        return lambda$sortTopics$6;
                    }
                });
            }
            if (z) {
                getNotificationCenter().lambda$postNotificationNameOnUIThread$1(NotificationCenter.topicsDidLoaded, Long.valueOf(j), Boolean.TRUE);
            }
        }
    }

    public void toggleCloseTopic(long j, int i, boolean z) {
        TLRPC.TL_forumTopic tL_forumTopic;
        TLRPC.TL_channels_editForumTopic tL_channels_editForumTopic = new TLRPC.TL_channels_editForumTopic();
        tL_channels_editForumTopic.channel = getMessagesController().getInputChannel(j);
        tL_channels_editForumTopic.topic_id = i;
        tL_channels_editForumTopic.flags |= 4;
        tL_channels_editForumTopic.closed = z;
        LongSparseArray longSparseArray = (LongSparseArray) this.topicsMapByChatId.get(j);
        if (longSparseArray != null && (tL_forumTopic = (TLRPC.TL_forumTopic) longSparseArray.get(i)) != null) {
            tL_forumTopic.closed = z;
            getMessagesStorage().updateTopicData(-j, tL_forumTopic, 8);
        }
        ConnectionsManager.getInstance(this.currentAccount).sendRequest(tL_channels_editForumTopic, new RequestDelegate() {
            @Override
            public void run(TLObject tLObject, TLRPC.TL_error tL_error) {
            }
        });
    }

    public void toggleShowTopic(long j, int i, boolean z) {
        TLRPC.TL_channels_editForumTopic tL_channels_editForumTopic = new TLRPC.TL_channels_editForumTopic();
        tL_channels_editForumTopic.channel = getMessagesController().getInputChannel(j);
        tL_channels_editForumTopic.topic_id = i;
        tL_channels_editForumTopic.flags = 8;
        tL_channels_editForumTopic.hidden = !z;
        TLRPC.TL_forumTopic findTopic = findTopic(j, i);
        if (findTopic != null) {
            boolean z2 = tL_channels_editForumTopic.hidden;
            findTopic.hidden = z2;
            if (z2) {
                findTopic.closed = true;
            }
            long j2 = -j;
            updateTopicInUi(j2, findTopic, 44);
            getMessagesStorage().updateTopicData(j2, findTopic, 44);
        }
        ConnectionsManager.getInstance(this.currentAccount).sendRequest(tL_channels_editForumTopic, null);
    }

    public void toggleViewForumAsMessages(long j, boolean z) {
        TLRPC.TL_channels_toggleViewForumAsMessages tL_channels_toggleViewForumAsMessages = new TLRPC.TL_channels_toggleViewForumAsMessages();
        tL_channels_toggleViewForumAsMessages.channel_id = getMessagesController().getInputChannel(j);
        tL_channels_toggleViewForumAsMessages.enabled = z;
        getConnectionsManager().sendRequest(tL_channels_toggleViewForumAsMessages, new RequestDelegate() {
            @Override
            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                TopicsController.this.lambda$toggleViewForumAsMessages$14(tLObject, tL_error);
            }
        });
    }

    public void updateMaxReadId(long j, long j2, int i, int i2, int i3) {
        TLRPC.TL_forumTopic findTopic = findTopic(j, j2);
        if (findTopic != null) {
            findTopic.read_inbox_max_id = i;
            findTopic.unread_count = i2;
            if (i3 >= 0) {
                findTopic.unread_mentions_count = i3;
            }
            sortTopics(j);
        }
    }

    public void updateMentionsUnread(final long j, final long j2, final int i) {
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                TopicsController.this.lambda$updateMentionsUnread$17(j, j2, i);
            }
        });
    }

    public int updateReactionsUnread(long j, long j2, int i, boolean z) {
        long j3 = -j;
        TLRPC.TL_forumTopic findTopic = findTopic(j3, j2);
        if (findTopic == null) {
            return -1;
        }
        if (z) {
            int i2 = findTopic.unread_reactions_count + i;
            findTopic.unread_reactions_count = i2;
            if (i2 < 0) {
                findTopic.unread_reactions_count = 0;
            }
        } else {
            findTopic.unread_reactions_count = i;
        }
        int i3 = findTopic.unread_reactions_count;
        sortTopics(j3, true);
        return i3;
    }

    public void updateReadOutbox(final HashMap<MessagesStorage.TopicKey, Integer> hashMap) {
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                TopicsController.this.lambda$updateReadOutbox$22(hashMap);
            }
        });
    }

    public void updateTopicInUi(long j, TLRPC.TL_forumTopic tL_forumTopic, int i) {
        long j2 = -j;
        TLRPC.TL_forumTopic findTopic = findTopic(j2, tL_forumTopic.id);
        if (findTopic != null) {
            if ((i & 1) != 0) {
                findTopic.title = tL_forumTopic.title;
            }
            if ((i & 2) != 0) {
                findTopic.icon_emoji_id = tL_forumTopic.icon_emoji_id;
            }
            if ((i & 8) != 0) {
                findTopic.closed = tL_forumTopic.closed;
            }
            if ((i & 4) != 0) {
                findTopic.pinned = tL_forumTopic.pinned;
            }
            if ((i & 32) != 0) {
                findTopic.hidden = tL_forumTopic.hidden;
            }
            sortTopics(j2);
        }
    }

    public void updateTopicsWithDeletedMessages(final long j, final ArrayList<Integer> arrayList) {
        if (j > 0) {
            return;
        }
        final long j2 = -j;
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                TopicsController.this.lambda$updateTopicsWithDeletedMessages$9(j, arrayList, j2);
            }
        });
    }
}
