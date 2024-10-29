package org.telegram.messenger;

import android.content.SharedPreferences;
import android.text.TextUtils;
import com.google.android.exoplayer2.util.Consumer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Objects;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;

public class HashtagSearchController {
    public static final int HISTORY_LIMIT = 100;
    private static volatile HashtagSearchController[] Instance = new HashtagSearchController[4];
    private static final Object[] lockObjects = new Object[4];
    private final SearchResult channelPostsSearch;
    public final int currentAccount;
    public final ArrayList<String> history = new ArrayList<>();
    private final SharedPreferences historyPreferences;
    private final SearchResult localPostsSearch;
    private final SearchResult myMessagesSearch;

    public static final class MessageCompositeID {
        final long dialog_id;
        final int id;

        MessageCompositeID(long j, int i) {
            this.dialog_id = j;
            this.id = i;
        }

        MessageCompositeID(TLRPC.Message message) {
            this(MessageObject.getDialogId(message), message.id);
        }

        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null || MessageCompositeID.class != obj.getClass()) {
                return false;
            }
            MessageCompositeID messageCompositeID = (MessageCompositeID) obj;
            return this.dialog_id == messageCompositeID.dialog_id && this.id == messageCompositeID.id;
        }

        public int hashCode() {
            return Objects.hash(Long.valueOf(this.dialog_id), Integer.valueOf(this.id));
        }
    }

    public static class SearchResult {
        public int count;
        private final int currentAccount;
        public boolean endReached;
        public String lastHashtag;
        public int lastOffsetId;
        public TLRPC.Peer lastOffsetPeer;
        public int lastOffsetRate;
        public boolean loading;
        public int selectedIndex;
        public final ArrayList<MessageObject> messages = new ArrayList<>();
        public final HashMap<MessageCompositeID, Integer> generatedIds = new HashMap<>();
        public int reqId = -1;
        public int lastGeneratedId = Integer.MAX_VALUE;

        public SearchResult(int i) {
            this.currentAccount = i;
        }

        void clear() {
            if (this.reqId >= 0) {
                ConnectionsManager.getInstance(this.currentAccount).cancelRequest(this.reqId, true);
                this.reqId = -1;
            }
            this.messages.clear();
            this.generatedIds.clear();
            this.lastOffsetRate = 0;
            this.lastOffsetId = 0;
            this.lastOffsetPeer = null;
            this.lastGeneratedId = 2147483637;
            this.lastHashtag = null;
            this.selectedIndex = 0;
            this.count = 0;
            this.endReached = false;
        }

        int getMask() {
            int i = this.selectedIndex >= this.messages.size() - 1 ? 0 : 1;
            return this.selectedIndex > 0 ? i | 2 : i;
        }
    }

    static {
        for (int i = 0; i < 4; i++) {
            lockObjects[i] = new Object();
        }
    }

    private HashtagSearchController(int i) {
        this.currentAccount = i;
        this.myMessagesSearch = new SearchResult(i);
        this.channelPostsSearch = new SearchResult(i);
        this.localPostsSearch = new SearchResult(i);
        this.historyPreferences = ApplicationLoader.applicationContext.getSharedPreferences("hashtag_search_history" + i, 0);
        loadHistoryFromPref();
    }

    public static HashtagSearchController getInstance(int i) {
        HashtagSearchController hashtagSearchController = Instance[i];
        if (hashtagSearchController == null) {
            synchronized (lockObjects[i]) {
                try {
                    hashtagSearchController = Instance[i];
                    if (hashtagSearchController == null) {
                        HashtagSearchController[] hashtagSearchControllerArr = Instance;
                        HashtagSearchController hashtagSearchController2 = new HashtagSearchController(i);
                        hashtagSearchControllerArr[i] = hashtagSearchController2;
                        hashtagSearchController = hashtagSearchController2;
                    }
                } finally {
                }
            }
        }
        return hashtagSearchController;
    }

    public void lambda$searchHashtag$0(SearchResult searchResult, String str, String str2, int[] iArr, int i, int i2, int i3, Long l) {
        if (TextUtils.equals(searchResult.lastHashtag, str)) {
            if (MessagesController.getInstance(this.currentAccount).getUserOrChat(str2) != null) {
                searchHashtag(str, i, i2, i3);
                return;
            }
            if (iArr[0] == searchResult.reqId) {
                searchResult.reqId = -1;
                searchResult.loading = false;
                searchResult.endReached = true;
                searchResult.count = 0;
                NotificationCenter.getInstance(this.currentAccount).lambda$postNotificationNameOnUIThread$1(NotificationCenter.hashtagSearchUpdated, Integer.valueOf(i), Integer.valueOf(searchResult.count), Boolean.valueOf(searchResult.endReached), Integer.valueOf(searchResult.getMask()), Integer.valueOf(searchResult.selectedIndex), 0);
            }
        }
    }

    public void lambda$searchHashtag$1(int[] iArr, SearchResult searchResult, TLRPC.messages_Messages messages_messages, ArrayList arrayList, int i, int i2, int i3) {
        if (iArr[0] == searchResult.reqId) {
            searchResult.reqId = -1;
            searchResult.loading = false;
            searchResult.lastOffsetRate = messages_messages.next_rate;
            Iterator it = arrayList.iterator();
            while (it.hasNext()) {
                MessageObject messageObject = (MessageObject) it.next();
                MessageCompositeID messageCompositeID = new MessageCompositeID(messageObject.messageOwner);
                Integer num = searchResult.generatedIds.get(messageCompositeID);
                if (num == null) {
                    int i4 = searchResult.lastGeneratedId;
                    searchResult.lastGeneratedId = i4 - 1;
                    num = Integer.valueOf(i4);
                    searchResult.generatedIds.put(messageCompositeID, num);
                    searchResult.messages.add(messageObject);
                }
                TLRPC.Message message = messageObject.messageOwner;
                message.realId = message.id;
                message.id = num.intValue();
            }
            if (!messages_messages.messages.isEmpty()) {
                ArrayList<TLRPC.Message> arrayList2 = messages_messages.messages;
                TLRPC.Message message2 = arrayList2.get(arrayList2.size() - 1);
                searchResult.lastOffsetId = message2.realId;
                searchResult.lastOffsetPeer = message2.peer_id;
            }
            MessagesStorage.getInstance(this.currentAccount).putUsersAndChats(messages_messages.users, messages_messages.chats, true, true);
            MessagesController.getInstance(this.currentAccount).putUsers(messages_messages.users, false);
            MessagesController.getInstance(this.currentAccount).putChats(messages_messages.chats, false);
            searchResult.endReached = messages_messages.messages.size() < i;
            searchResult.count = Math.max(messages_messages.count, messages_messages.messages.size());
            NotificationCenter.getInstance(this.currentAccount).lambda$postNotificationNameOnUIThread$1(NotificationCenter.messagesDidLoad, 0L, Integer.valueOf(arrayList.size()), arrayList, Boolean.FALSE, 0, 0, 0, 0, 2, Boolean.TRUE, Integer.valueOf(i2), Integer.valueOf(i3), 0, 0, 7);
            NotificationCenter.getInstance(this.currentAccount).lambda$postNotificationNameOnUIThread$1(NotificationCenter.hashtagSearchUpdated, Integer.valueOf(i2), Integer.valueOf(searchResult.count), Boolean.valueOf(searchResult.endReached), Integer.valueOf(searchResult.getMask()), Integer.valueOf(searchResult.selectedIndex), 0);
        }
    }

    public void lambda$searchHashtag$2(int i, String str, final int[] iArr, final SearchResult searchResult, final int i2, final int i3, final int i4, TLObject tLObject, TLRPC.TL_error tL_error) {
        if (tLObject instanceof TLRPC.messages_Messages) {
            final TLRPC.messages_Messages messages_messages = (TLRPC.messages_Messages) tLObject;
            final ArrayList arrayList = new ArrayList();
            Iterator<TLRPC.Message> it = messages_messages.messages.iterator();
            while (it.hasNext()) {
                MessageObject messageObject = new MessageObject(this.currentAccount, it.next(), null, null, null, null, null, true, true, 0L, false, false, false, i);
                if (messageObject.hasValidGroupId()) {
                    messageObject.isPrimaryGroupMessage = true;
                }
                messageObject.setQuery(str, false);
                arrayList.add(messageObject);
            }
            AndroidUtilities.runOnUIThread(new Runnable() {
                @Override
                public final void run() {
                    HashtagSearchController.this.lambda$searchHashtag$1(iArr, searchResult, messages_messages, arrayList, i2, i3, i4);
                }
            });
        }
    }

    private void loadHistoryFromPref() {
        int i = this.historyPreferences.getInt("count", 0);
        this.history.clear();
        this.history.ensureCapacity(i);
        for (int i2 = 0; i2 < i; i2++) {
            String string = this.historyPreferences.getString("e_" + i2, "");
            if (!string.startsWith("#") && !string.startsWith("$")) {
                string = "#" + string;
            }
            this.history.add(string);
        }
    }

    private void saveHistoryToPref() {
        SharedPreferences.Editor edit = this.historyPreferences.edit();
        edit.clear();
        edit.putInt("count", this.history.size());
        for (int i = 0; i < this.history.size(); i++) {
            edit.putString("e_" + i, this.history.get(i));
        }
        edit.apply();
    }

    public void clearHistory() {
        this.history.clear();
        saveHistoryToPref();
    }

    public void clearSearchResults() {
        this.myMessagesSearch.clear();
        this.channelPostsSearch.clear();
    }

    public void clearSearchResults(int i) {
        getSearchResult(i).clear();
    }

    public int getCount(int i) {
        return getSearchResult(i).count;
    }

    public ArrayList<MessageObject> getMessages(int i) {
        return getSearchResult(i).messages;
    }

    public SearchResult getSearchResult(int i) {
        if (i == 1) {
            return this.myMessagesSearch;
        }
        if (i == 2) {
            return this.channelPostsSearch;
        }
        if (i == 3) {
            return this.localPostsSearch;
        }
        throw new RuntimeException("Unknown search type");
    }

    public boolean isEndReached(int i) {
        return getSearchResult(i).endReached;
    }

    public void jumpToMessage(int i, int i2, int i3) {
        SearchResult searchResult = getSearchResult(i3);
        if (i2 < 0 || i2 >= searchResult.messages.size()) {
            return;
        }
        searchResult.selectedIndex = i2;
        NotificationCenter.getInstance(this.currentAccount).lambda$postNotificationNameOnUIThread$1(NotificationCenter.hashtagSearchUpdated, Integer.valueOf(i), Integer.valueOf(searchResult.count), Boolean.valueOf(searchResult.endReached), Integer.valueOf(searchResult.getMask()), Integer.valueOf(searchResult.selectedIndex), Integer.valueOf(searchResult.messages.get(i2).messageOwner.id));
    }

    public void putToHistory(String str) {
        if (str.startsWith("#") || str.startsWith("$")) {
            int indexOf = this.history.indexOf(str);
            if (indexOf != -1) {
                if (indexOf == 0) {
                    return;
                } else {
                    this.history.remove(indexOf);
                }
            }
            this.history.add(0, str);
            if (this.history.size() >= 100) {
                ArrayList<String> arrayList = this.history;
                arrayList.subList(99, arrayList.size()).clear();
            }
            saveHistoryToPref();
        }
    }

    public void removeHashtagFromHistory(String str) {
        int indexOf = this.history.indexOf(str);
        if (indexOf != -1) {
            this.history.remove(indexOf);
            saveHistoryToPref();
        }
    }

    public void searchHashtag(String str, final int i, final int i2, final int i3) {
        String str2;
        String str3;
        TLRPC.TL_channels_searchPosts tL_channels_searchPosts;
        TLRPC.TL_channels_searchPosts tL_channels_searchPosts2;
        String str4 = str;
        final SearchResult searchResult = getSearchResult(i2);
        if (searchResult.lastHashtag == null && str4 == null) {
            return;
        }
        if (str4 == null || !str.isEmpty()) {
            if (str4 == null) {
                str4 = searchResult.lastHashtag;
            } else if (!TextUtils.equals(str4, searchResult.lastHashtag)) {
                searchResult.clear();
            } else if (searchResult.loading) {
                return;
            }
            final String str5 = str4;
            searchResult.lastHashtag = str5;
            int indexOf = str5.indexOf(64);
            TLObject tLObject = null;
            if (indexOf >= 0) {
                String substring = str5.substring(indexOf + 1);
                str3 = str5.substring(0, indexOf);
                str2 = substring;
            } else {
                str2 = null;
                str3 = str5;
            }
            searchResult.loading = true;
            final int[] iArr = new int[1];
            if (!TextUtils.isEmpty(str2) && (tLObject = MessagesController.getInstance(this.currentAccount).getUserOrChat(str2)) == null) {
                final String str6 = str2;
                int resolve = MessagesController.getInstance(this.currentAccount).getUserNameResolver().resolve(str2, new Consumer() {
                    @Override
                    public final void accept(Object obj) {
                        HashtagSearchController.this.lambda$searchHashtag$0(searchResult, str5, str6, iArr, i, i2, i3, (Long) obj);
                    }
                });
                searchResult.reqId = resolve;
                iArr[0] = resolve;
                return;
            }
            final int i4 = 21;
            if (i2 == 1) {
                TLRPC.TL_messages_searchGlobal tL_messages_searchGlobal = new TLRPC.TL_messages_searchGlobal();
                tL_messages_searchGlobal.limit = 21;
                tL_messages_searchGlobal.q = str5;
                tL_messages_searchGlobal.filter = new TLRPC.TL_inputMessagesFilterEmpty();
                tL_messages_searchGlobal.offset_peer = new TLRPC.TL_inputPeerEmpty();
                tL_channels_searchPosts2 = tL_messages_searchGlobal;
                if (searchResult.lastOffsetPeer != null) {
                    tL_messages_searchGlobal.offset_rate = searchResult.lastOffsetRate;
                    tL_messages_searchGlobal.offset_id = searchResult.lastOffsetId;
                    tL_messages_searchGlobal.offset_peer = MessagesController.getInstance(this.currentAccount).getInputPeer(searchResult.lastOffsetPeer);
                    tL_channels_searchPosts2 = tL_messages_searchGlobal;
                }
            } else {
                if (tLObject != null) {
                    TLRPC.TL_messages_search tL_messages_search = new TLRPC.TL_messages_search();
                    tL_messages_search.filter = new TLRPC.TL_inputMessagesFilterEmpty();
                    tL_messages_search.peer = MessagesController.getInputPeer(tLObject);
                    tL_messages_search.q = str3;
                    tL_messages_search.limit = 21;
                    int i5 = searchResult.lastOffsetId;
                    if (i5 != 0) {
                        tL_messages_search.offset_id = i5;
                    }
                    tL_channels_searchPosts = tL_messages_search;
                    int sendRequest = ConnectionsManager.getInstance(this.currentAccount).sendRequest(tL_channels_searchPosts, new RequestDelegate() {
                        @Override
                        public final void run(TLObject tLObject2, TLRPC.TL_error tL_error) {
                            HashtagSearchController.this.lambda$searchHashtag$2(i2, str5, iArr, searchResult, i4, i, i3, tLObject2, tL_error);
                        }
                    });
                    searchResult.reqId = sendRequest;
                    iArr[0] = sendRequest;
                }
                TLRPC.TL_channels_searchPosts tL_channels_searchPosts3 = new TLRPC.TL_channels_searchPosts();
                tL_channels_searchPosts3.limit = 21;
                tL_channels_searchPosts3.hashtag = str5;
                tL_channels_searchPosts3.offset_peer = new TLRPC.TL_inputPeerEmpty();
                tL_channels_searchPosts2 = tL_channels_searchPosts3;
                if (searchResult.lastOffsetPeer != null) {
                    tL_channels_searchPosts3.offset_rate = searchResult.lastOffsetRate;
                    tL_channels_searchPosts3.offset_id = searchResult.lastOffsetId;
                    tL_channels_searchPosts3.offset_peer = MessagesController.getInstance(this.currentAccount).getInputPeer(searchResult.lastOffsetPeer);
                    tL_channels_searchPosts2 = tL_channels_searchPosts3;
                }
            }
            tL_channels_searchPosts = tL_channels_searchPosts2;
            int sendRequest2 = ConnectionsManager.getInstance(this.currentAccount).sendRequest(tL_channels_searchPosts, new RequestDelegate() {
                @Override
                public final void run(TLObject tLObject2, TLRPC.TL_error tL_error) {
                    HashtagSearchController.this.lambda$searchHashtag$2(i2, str5, iArr, searchResult, i4, i, i3, tLObject2, tL_error);
                }
            });
            searchResult.reqId = sendRequest2;
            iArr[0] = sendRequest2;
        }
    }
}
