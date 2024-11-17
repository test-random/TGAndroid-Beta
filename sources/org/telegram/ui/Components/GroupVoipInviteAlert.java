package org.telegram.ui.Components;

import android.content.Context;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import androidx.collection.LongSparseArray;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ChatObject;
import org.telegram.messenger.ContactsController;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.R;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.UserObject;
import org.telegram.messenger.Utilities;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.BottomSheet;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Adapters.SearchAdapterHelper;
import org.telegram.ui.Cells.GraySectionCell;
import org.telegram.ui.Cells.ManageChatTextCell;
import org.telegram.ui.Cells.ManageChatUserCell;
import org.telegram.ui.Components.GroupVoipInviteAlert;
import org.telegram.ui.Components.RecyclerListView;

public class GroupVoipInviteAlert extends UsersAlertBase {
    private int addNewRow;
    private ArrayList contacts;
    private boolean contactsEndReached;
    private int contactsEndRow;
    private int contactsHeaderRow;
    private LongSparseArray contactsMap;
    private int contactsStartRow;
    private TLRPC.Chat currentChat;
    private int delayResults;
    private GroupVoipInviteAlertDelegate delegate;
    private int emptyRow;
    private boolean firstLoaded;
    private int flickerProgressRow;
    private LongSparseArray ignoredUsers;
    private TLRPC.ChatFull info;
    private HashSet invitedUsers;
    private int lastRow;
    private boolean loadingUsers;
    private int membersHeaderRow;
    private ArrayList participants;
    private int participantsEndRow;
    private LongSparseArray participantsMap;
    private int participantsStartRow;
    private int rowCount;
    private final SearchAdapter searchAdapter;
    private boolean showContacts;

    public interface GroupVoipInviteAlertDelegate {
        void copyInviteLink();

        void inviteUser(long j);

        void needOpenSearch(MotionEvent motionEvent, EditTextBoldCursor editTextBoldCursor);
    }

    private class ListAdapter extends RecyclerListView.SelectionAdapter {
        private Context mContext;

        public ListAdapter(Context context) {
            this.mContext = context;
        }

        public TLObject getItem(int i) {
            ArrayList arrayList;
            int i2;
            if (i >= GroupVoipInviteAlert.this.participantsStartRow && i < GroupVoipInviteAlert.this.participantsEndRow) {
                arrayList = GroupVoipInviteAlert.this.participants;
                i2 = GroupVoipInviteAlert.this.participantsStartRow;
            } else {
                if (i < GroupVoipInviteAlert.this.contactsStartRow || i >= GroupVoipInviteAlert.this.contactsEndRow) {
                    return null;
                }
                arrayList = GroupVoipInviteAlert.this.contacts;
                i2 = GroupVoipInviteAlert.this.contactsStartRow;
            }
            return (TLObject) arrayList.get(i - i2);
        }

        @Override
        public int getItemCount() {
            return GroupVoipInviteAlert.this.rowCount;
        }

        @Override
        public int getItemViewType(int i) {
            if ((i >= GroupVoipInviteAlert.this.participantsStartRow && i < GroupVoipInviteAlert.this.participantsEndRow) || (i >= GroupVoipInviteAlert.this.contactsStartRow && i < GroupVoipInviteAlert.this.contactsEndRow)) {
                return 0;
            }
            if (i == GroupVoipInviteAlert.this.addNewRow) {
                return 1;
            }
            if (i == GroupVoipInviteAlert.this.membersHeaderRow || i == GroupVoipInviteAlert.this.contactsHeaderRow) {
                return 2;
            }
            if (i == GroupVoipInviteAlert.this.emptyRow) {
                return 3;
            }
            if (i == GroupVoipInviteAlert.this.lastRow) {
                return 4;
            }
            return i == GroupVoipInviteAlert.this.flickerProgressRow ? 5 : 0;
        }

        @Override
        public boolean isEnabled(RecyclerView.ViewHolder viewHolder) {
            View view = viewHolder.itemView;
            if ((view instanceof ManageChatUserCell) && GroupVoipInviteAlert.this.invitedUsers.contains(Long.valueOf(((ManageChatUserCell) view).getUserId()))) {
                return false;
            }
            int itemViewType = viewHolder.getItemViewType();
            return itemViewType == 0 || itemViewType == 1;
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
            int i2;
            int itemViewType = viewHolder.getItemViewType();
            if (itemViewType == 0) {
                ManageChatUserCell manageChatUserCell = (ManageChatUserCell) viewHolder.itemView;
                manageChatUserCell.setTag(Integer.valueOf(i));
                TLObject item = getItem(i);
                int i3 = (i < GroupVoipInviteAlert.this.participantsStartRow || i >= GroupVoipInviteAlert.this.participantsEndRow) ? GroupVoipInviteAlert.this.contactsEndRow : GroupVoipInviteAlert.this.participantsEndRow;
                TLRPC.User user = MessagesController.getInstance(((BottomSheet) GroupVoipInviteAlert.this).currentAccount).getUser(Long.valueOf(item instanceof TLRPC.TL_contact ? ((TLRPC.TL_contact) item).user_id : item instanceof TLRPC.User ? ((TLRPC.User) item).id : item instanceof TLRPC.ChannelParticipant ? MessageObject.getPeerId(((TLRPC.ChannelParticipant) item).peer) : ((TLRPC.ChatParticipant) item).user_id));
                if (user != null) {
                    manageChatUserCell.setCustomImageVisible(GroupVoipInviteAlert.this.invitedUsers.contains(Long.valueOf(user.id)));
                    manageChatUserCell.setData(user, null, null, i != i3 - 1);
                    return;
                }
                return;
            }
            if (itemViewType == 1) {
                ManageChatTextCell manageChatTextCell = (ManageChatTextCell) viewHolder.itemView;
                if (i == GroupVoipInviteAlert.this.addNewRow) {
                    manageChatTextCell.setText(LocaleController.getString(R.string.VoipGroupCopyInviteLink), null, R.drawable.msg_link, 7, (!GroupVoipInviteAlert.this.loadingUsers || GroupVoipInviteAlert.this.firstLoaded) && GroupVoipInviteAlert.this.membersHeaderRow == -1 && !GroupVoipInviteAlert.this.participants.isEmpty());
                    return;
                }
                return;
            }
            if (itemViewType != 2) {
                return;
            }
            GraySectionCell graySectionCell = (GraySectionCell) viewHolder.itemView;
            if (i == GroupVoipInviteAlert.this.membersHeaderRow) {
                i2 = R.string.ChannelOtherMembers;
            } else if (i != GroupVoipInviteAlert.this.contactsHeaderRow) {
                return;
            } else {
                i2 = GroupVoipInviteAlert.this.showContacts ? R.string.YourContactsToInvite : R.string.GroupContacts;
            }
            graySectionCell.setText(LocaleController.getString(i2));
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            ManageChatUserCell manageChatUserCell;
            ManageChatUserCell manageChatUserCell2;
            if (i != 0) {
                if (i == 1) {
                    ?? manageChatTextCell = new ManageChatTextCell(this.mContext);
                    int i2 = Theme.key_voipgroup_listeningText;
                    manageChatTextCell.setColors(i2, i2);
                    manageChatTextCell.setDividerColor(Theme.key_voipgroup_actionBar);
                    manageChatUserCell2 = manageChatTextCell;
                } else if (i == 2) {
                    ?? graySectionCell = new GraySectionCell(this.mContext);
                    graySectionCell.setBackgroundColor(Theme.getColor(Theme.key_voipgroup_actionBarUnscrolled));
                    graySectionCell.setTextColor(Theme.key_voipgroup_searchPlaceholder);
                    manageChatUserCell2 = graySectionCell;
                } else if (i == 3) {
                    ?? view = new View(this.mContext);
                    view.setLayoutParams(new RecyclerView.LayoutParams(-1, AndroidUtilities.dp(56.0f)));
                    manageChatUserCell2 = view;
                } else if (i != 5) {
                    manageChatUserCell2 = new View(this.mContext);
                } else {
                    ?? flickerLoadingView = new FlickerLoadingView(this.mContext);
                    flickerLoadingView.setViewType(6);
                    flickerLoadingView.setIsSingleCell(true);
                    flickerLoadingView.setColors(Theme.key_voipgroup_inviteMembersBackground, Theme.key_voipgroup_searchBackground, Theme.key_voipgroup_actionBarUnscrolled);
                    manageChatUserCell = flickerLoadingView;
                }
                return new RecyclerListView.Holder(manageChatUserCell2);
            }
            ManageChatUserCell manageChatUserCell3 = new ManageChatUserCell(this.mContext, 6, 2, false);
            manageChatUserCell3.setCustomRightImage(R.drawable.msg_invited);
            manageChatUserCell3.setNameColor(Theme.getColor(Theme.key_voipgroup_nameText));
            manageChatUserCell3.setStatusColors(Theme.getColor(Theme.key_voipgroup_lastSeenTextUnscrolled), Theme.getColor(Theme.key_voipgroup_listeningText));
            manageChatUserCell3.setDividerColor(Theme.key_voipgroup_actionBar);
            manageChatUserCell = manageChatUserCell3;
            manageChatUserCell2 = manageChatUserCell;
            return new RecyclerListView.Holder(manageChatUserCell2);
        }

        @Override
        public void onViewRecycled(RecyclerView.ViewHolder viewHolder) {
            View view = viewHolder.itemView;
            if (view instanceof ManageChatUserCell) {
                ((ManageChatUserCell) view).recycle();
            }
        }
    }

    public class SearchAdapter extends RecyclerListView.SelectionAdapter {
        private int emptyRow;
        private int globalStartRow;
        private int groupStartRow;
        private int lastRow;
        private int lastSearchId;
        private Context mContext;
        private SearchAdapterHelper searchAdapterHelper;
        private boolean searchInProgress;
        private Runnable searchRunnable;
        private int totalCount;

        public SearchAdapter(Context context) {
            this.mContext = context;
            SearchAdapterHelper searchAdapterHelper = new SearchAdapterHelper(true);
            this.searchAdapterHelper = searchAdapterHelper;
            searchAdapterHelper.setDelegate(new SearchAdapterHelper.SearchAdapterHelperDelegate() {
                @Override
                public boolean canApplySearchResults(int i) {
                    return SearchAdapterHelper.SearchAdapterHelperDelegate.CC.$default$canApplySearchResults(this, i);
                }

                @Override
                public LongSparseArray getExcludeCallParticipants() {
                    return GroupVoipInviteAlert.this.ignoredUsers;
                }

                @Override
                public LongSparseArray getExcludeUsers() {
                    return SearchAdapterHelper.SearchAdapterHelperDelegate.CC.$default$getExcludeUsers(this);
                }

                @Override
                public void onDataSetChanged(int i) {
                    if (i < 0 || i != SearchAdapter.this.lastSearchId || SearchAdapter.this.searchInProgress) {
                        return;
                    }
                    int itemCount = SearchAdapter.this.getItemCount() - 1;
                    boolean z = GroupVoipInviteAlert.this.emptyView.getVisibility() == 0;
                    SearchAdapter.this.notifyDataSetChanged();
                    if (SearchAdapter.this.getItemCount() > itemCount) {
                        GroupVoipInviteAlert.this.showItemsAnimated(itemCount);
                    }
                    if (SearchAdapter.this.searchAdapterHelper.isSearchInProgress() || !GroupVoipInviteAlert.this.listView.emptyViewIsVisible()) {
                        return;
                    }
                    GroupVoipInviteAlert.this.emptyView.showProgress(false, z);
                }

                @Override
                public void onSetHashtags(ArrayList arrayList, HashMap hashMap) {
                    SearchAdapterHelper.SearchAdapterHelperDelegate.CC.$default$onSetHashtags(this, arrayList, hashMap);
                }
            });
        }

        public void lambda$processSearch$1(java.lang.String r19, int r20, java.util.ArrayList r21) {
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.GroupVoipInviteAlert.SearchAdapter.lambda$processSearch$1(java.lang.String, int, java.util.ArrayList):void");
        }

        public void lambda$processSearch$2(final String str, final int i) {
            final ArrayList arrayList = null;
            this.searchRunnable = null;
            if (!ChatObject.isChannel(GroupVoipInviteAlert.this.currentChat) && GroupVoipInviteAlert.this.info != null) {
                arrayList = new ArrayList(GroupVoipInviteAlert.this.info.participants.participants);
            }
            if (arrayList != null) {
                Utilities.searchQueue.postRunnable(new Runnable() {
                    @Override
                    public final void run() {
                        GroupVoipInviteAlert.SearchAdapter.this.lambda$processSearch$1(str, i, arrayList);
                    }
                });
            } else {
                this.searchInProgress = false;
            }
            this.searchAdapterHelper.queryServerSearch(str, ChatObject.canAddUsers(GroupVoipInviteAlert.this.currentChat), false, true, false, false, ChatObject.isChannel(GroupVoipInviteAlert.this.currentChat) ? GroupVoipInviteAlert.this.currentChat.id : 0L, false, 2, i);
        }

        public void lambda$searchUsers$0(String str, int i) {
            if (this.searchRunnable == null) {
                return;
            }
            this.searchRunnable = null;
            processSearch(str, i);
        }

        public void lambda$updateSearchResults$3(int i, ArrayList arrayList) {
            if (i != this.lastSearchId) {
                return;
            }
            this.searchInProgress = false;
            if (!ChatObject.isChannel(GroupVoipInviteAlert.this.currentChat)) {
                this.searchAdapterHelper.addGroupMembers(arrayList);
            }
            int itemCount = getItemCount() - 1;
            boolean z = GroupVoipInviteAlert.this.emptyView.getVisibility() == 0;
            notifyDataSetChanged();
            if (getItemCount() > itemCount) {
                GroupVoipInviteAlert.this.showItemsAnimated(itemCount);
            }
            if (this.searchInProgress || this.searchAdapterHelper.isSearchInProgress() || !GroupVoipInviteAlert.this.listView.emptyViewIsVisible()) {
                return;
            }
            GroupVoipInviteAlert.this.emptyView.showProgress(false, z);
        }

        private void processSearch(final String str, final int i) {
            AndroidUtilities.runOnUIThread(new Runnable() {
                @Override
                public final void run() {
                    GroupVoipInviteAlert.SearchAdapter.this.lambda$processSearch$2(str, i);
                }
            });
        }

        private void updateSearchResults(final ArrayList arrayList, final int i) {
            AndroidUtilities.runOnUIThread(new Runnable() {
                @Override
                public final void run() {
                    GroupVoipInviteAlert.SearchAdapter.this.lambda$updateSearchResults$3(i, arrayList);
                }
            });
        }

        public TLObject getItem(int i) {
            ArrayList globalSearch;
            int i2;
            int i3 = this.groupStartRow;
            if (i3 < 0 || i <= i3 || i >= i3 + 1 + this.searchAdapterHelper.getGroupSearch().size()) {
                int i4 = this.globalStartRow;
                if (i4 < 0 || i <= i4 || i >= i4 + 1 + this.searchAdapterHelper.getGlobalSearch().size()) {
                    return null;
                }
                globalSearch = this.searchAdapterHelper.getGlobalSearch();
                i2 = this.globalStartRow;
            } else {
                globalSearch = this.searchAdapterHelper.getGroupSearch();
                i2 = this.groupStartRow;
            }
            return (TLObject) globalSearch.get((i - i2) - 1);
        }

        @Override
        public int getItemCount() {
            return this.totalCount;
        }

        @Override
        public int getItemViewType(int i) {
            if (i == this.emptyRow) {
                return 2;
            }
            if (i == this.lastRow) {
                return 3;
            }
            return (i == this.globalStartRow || i == this.groupStartRow) ? 1 : 0;
        }

        @Override
        public boolean isEnabled(RecyclerView.ViewHolder viewHolder) {
            View view = viewHolder.itemView;
            return !((view instanceof ManageChatUserCell) && GroupVoipInviteAlert.this.invitedUsers.contains(Long.valueOf(((ManageChatUserCell) view).getUserId()))) && viewHolder.getItemViewType() == 0;
        }

        @Override
        public void notifyDataSetChanged() {
            this.totalCount = 1;
            this.emptyRow = 0;
            int size = this.searchAdapterHelper.getGroupSearch().size();
            if (size != 0) {
                int i = this.totalCount;
                this.groupStartRow = i;
                this.totalCount = i + size + 1;
            } else {
                this.groupStartRow = -1;
            }
            int size2 = this.searchAdapterHelper.getGlobalSearch().size();
            if (size2 != 0) {
                int i2 = this.totalCount;
                this.globalStartRow = i2;
                this.totalCount = i2 + size2 + 1;
            } else {
                this.globalStartRow = -1;
            }
            int i3 = this.totalCount;
            this.totalCount = i3 + 1;
            this.lastRow = i3;
            super.notifyDataSetChanged();
        }

        @Override
        public void onBindViewHolder(androidx.recyclerview.widget.RecyclerView.ViewHolder r13, int r14) {
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.GroupVoipInviteAlert.SearchAdapter.onBindViewHolder(androidx.recyclerview.widget.RecyclerView$ViewHolder, int):void");
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            ManageChatUserCell manageChatUserCell;
            if (i == 0) {
                ManageChatUserCell manageChatUserCell2 = new ManageChatUserCell(this.mContext, 2, 2, false);
                manageChatUserCell2.setCustomRightImage(R.drawable.msg_invited);
                manageChatUserCell2.setNameColor(Theme.getColor(Theme.key_voipgroup_nameText));
                manageChatUserCell2.setStatusColors(Theme.getColor(Theme.key_voipgroup_lastSeenTextUnscrolled), Theme.getColor(Theme.key_voipgroup_listeningText));
                manageChatUserCell2.setDividerColor(Theme.key_voipgroup_listViewBackground);
                manageChatUserCell = manageChatUserCell2;
            } else if (i == 1) {
                ?? graySectionCell = new GraySectionCell(this.mContext);
                graySectionCell.setBackgroundColor(Theme.getColor(Theme.key_voipgroup_actionBarUnscrolled));
                graySectionCell.setTextColor(Theme.key_voipgroup_searchPlaceholder);
                manageChatUserCell = graySectionCell;
            } else if (i != 2) {
                manageChatUserCell = new View(this.mContext);
            } else {
                ?? view = new View(this.mContext);
                view.setLayoutParams(new RecyclerView.LayoutParams(-1, AndroidUtilities.dp(56.0f)));
                manageChatUserCell = view;
            }
            return new RecyclerListView.Holder(manageChatUserCell);
        }

        @Override
        public void onViewRecycled(RecyclerView.ViewHolder viewHolder) {
            View view = viewHolder.itemView;
            if (view instanceof ManageChatUserCell) {
                ((ManageChatUserCell) view).recycle();
            }
        }

        public void searchUsers(final String str) {
            Runnable runnable = this.searchRunnable;
            if (runnable != null) {
                AndroidUtilities.cancelRunOnUIThread(runnable);
                this.searchRunnable = null;
            }
            this.searchAdapterHelper.mergeResults(null);
            this.searchAdapterHelper.queryServerSearch(null, true, false, true, false, false, GroupVoipInviteAlert.this.currentChat.id, false, 2, -1);
            if (TextUtils.isEmpty(str)) {
                this.lastSearchId = -1;
                return;
            }
            GroupVoipInviteAlert.this.emptyView.showProgress(true, true);
            GroupVoipInviteAlert.this.listView.setAnimateEmptyView(false, 0);
            notifyDataSetChanged();
            GroupVoipInviteAlert.this.listView.setAnimateEmptyView(true, 0);
            this.searchInProgress = true;
            final int i = this.lastSearchId + 1;
            this.lastSearchId = i;
            Runnable runnable2 = new Runnable() {
                @Override
                public final void run() {
                    GroupVoipInviteAlert.SearchAdapter.this.lambda$searchUsers$0(str, i);
                }
            };
            this.searchRunnable = runnable2;
            AndroidUtilities.runOnUIThread(runnable2, 300L);
            RecyclerView.Adapter adapter = GroupVoipInviteAlert.this.listView.getAdapter();
            GroupVoipInviteAlert groupVoipInviteAlert = GroupVoipInviteAlert.this;
            RecyclerView.Adapter adapter2 = groupVoipInviteAlert.searchListViewAdapter;
            if (adapter != adapter2) {
                groupVoipInviteAlert.listView.setAdapter(adapter2);
            }
        }
    }

    public GroupVoipInviteAlert(Context context, int i, TLRPC.Chat chat, TLRPC.ChatFull chatFull, LongSparseArray longSparseArray, HashSet hashSet) {
        super(context, false, i, null);
        this.participants = new ArrayList();
        this.contacts = new ArrayList();
        this.participantsMap = new LongSparseArray();
        this.contactsMap = new LongSparseArray();
        setDimBehindAlpha(75);
        this.currentChat = chat;
        this.info = chatFull;
        this.ignoredUsers = longSparseArray;
        this.invitedUsers = hashSet;
        this.listView.setOnItemClickListener(new RecyclerListView.OnItemClickListener() {
            @Override
            public final void onItemClick(View view, int i2) {
                GroupVoipInviteAlert.this.lambda$new$0(view, i2);
            }
        });
        SearchAdapter searchAdapter = new SearchAdapter(context);
        this.searchAdapter = searchAdapter;
        this.searchListViewAdapter = searchAdapter;
        RecyclerListView recyclerListView = this.listView;
        ListAdapter listAdapter = new ListAdapter(context);
        this.listViewAdapter = listAdapter;
        recyclerListView.setAdapter(listAdapter);
        loadChatParticipants(0, 200);
        updateRows();
        setColorProgress(0.0f);
    }

    private void fillContacts() {
        if (this.showContacts) {
            this.contacts.addAll(ContactsController.getInstance(this.currentAccount).contacts);
            long j = UserConfig.getInstance(this.currentAccount).clientUserId;
            int size = this.contacts.size();
            int i = 0;
            while (i < size) {
                TLObject tLObject = (TLObject) this.contacts.get(i);
                if (tLObject instanceof TLRPC.TL_contact) {
                    long j2 = ((TLRPC.TL_contact) tLObject).user_id;
                    if (j2 == j || this.ignoredUsers.indexOfKey(j2) >= 0 || this.invitedUsers.contains(Long.valueOf(j2))) {
                        this.contacts.remove(i);
                        i--;
                        size--;
                    }
                }
                i++;
            }
            final int currentTime = ConnectionsManager.getInstance(this.currentAccount).getCurrentTime();
            final MessagesController messagesController = MessagesController.getInstance(this.currentAccount);
            Collections.sort(this.contacts, new Comparator() {
                @Override
                public final int compare(Object obj, Object obj2) {
                    int lambda$fillContacts$1;
                    lambda$fillContacts$1 = GroupVoipInviteAlert.lambda$fillContacts$1(MessagesController.this, currentTime, (TLObject) obj, (TLObject) obj2);
                    return lambda$fillContacts$1;
                }
            });
        }
    }

    public static int lambda$fillContacts$1(org.telegram.messenger.MessagesController r4, int r5, org.telegram.tgnet.TLObject r6, org.telegram.tgnet.TLObject r7) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.GroupVoipInviteAlert.lambda$fillContacts$1(org.telegram.messenger.MessagesController, int, org.telegram.tgnet.TLObject, org.telegram.tgnet.TLObject):int");
    }

    public int lambda$loadChatParticipants$2(int i, TLObject tLObject, TLObject tLObject2) {
        TLRPC.UserStatus userStatus;
        TLRPC.UserStatus userStatus2;
        TLRPC.User user = MessagesController.getInstance(this.currentAccount).getUser(Long.valueOf(MessageObject.getPeerId(((TLRPC.ChannelParticipant) tLObject).peer)));
        TLRPC.User user2 = MessagesController.getInstance(this.currentAccount).getUser(Long.valueOf(MessageObject.getPeerId(((TLRPC.ChannelParticipant) tLObject2).peer)));
        int i2 = (user == null || (userStatus2 = user.status) == null) ? 0 : user.self ? i + 50000 : userStatus2.expires;
        int i3 = (user2 == null || (userStatus = user2.status) == null) ? 0 : user2.self ? i + 50000 : userStatus.expires;
        if (i2 > 0 && i3 > 0) {
            if (i2 > i3) {
                return 1;
            }
            return i2 < i3 ? -1 : 0;
        }
        if (i2 < 0 && i3 < 0) {
            if (i2 > i3) {
                return 1;
            }
            return i2 < i3 ? -1 : 0;
        }
        if ((i2 >= 0 || i3 <= 0) && (i2 != 0 || i3 == 0)) {
            return ((i3 >= 0 || i2 <= 0) && (i3 != 0 || i2 == 0)) ? 0 : 1;
        }
        return -1;
    }

    public void lambda$loadChatParticipants$3(TLRPC.TL_error tL_error, TLObject tLObject, TLRPC.TL_channels_getParticipants tL_channels_getParticipants) {
        int itemCount;
        ArrayList arrayList;
        LongSparseArray longSparseArray;
        LongSparseArray longSparseArray2;
        if (tL_error == null) {
            TLRPC.TL_channels_channelParticipants tL_channels_channelParticipants = (TLRPC.TL_channels_channelParticipants) tLObject;
            MessagesController.getInstance(this.currentAccount).putUsers(tL_channels_channelParticipants.users, false);
            MessagesController.getInstance(this.currentAccount).putChats(tL_channels_channelParticipants.chats, false);
            long clientUserId = UserConfig.getInstance(this.currentAccount).getClientUserId();
            int i = 0;
            while (true) {
                if (i >= tL_channels_channelParticipants.participants.size()) {
                    break;
                }
                if (MessageObject.getPeerId(tL_channels_channelParticipants.participants.get(i).peer) == clientUserId) {
                    tL_channels_channelParticipants.participants.remove(i);
                    break;
                }
                i++;
            }
            this.delayResults--;
            if (tL_channels_getParticipants.filter instanceof TLRPC.TL_channelParticipantsContacts) {
                arrayList = this.contacts;
                longSparseArray = this.contactsMap;
            } else {
                arrayList = this.participants;
                longSparseArray = this.participantsMap;
            }
            arrayList.clear();
            arrayList.addAll(tL_channels_channelParticipants.participants);
            int size = tL_channels_channelParticipants.participants.size();
            for (int i2 = 0; i2 < size; i2++) {
                TLRPC.ChannelParticipant channelParticipant = tL_channels_channelParticipants.participants.get(i2);
                longSparseArray.put(MessageObject.getPeerId(channelParticipant.peer), channelParticipant);
            }
            int size2 = this.participants.size();
            int i3 = 0;
            while (i3 < size2) {
                long peerId = MessageObject.getPeerId(((TLRPC.ChannelParticipant) this.participants.get(i3)).peer);
                boolean z = this.contactsMap.get(peerId) != null || ((longSparseArray2 = this.ignoredUsers) != null && longSparseArray2.indexOfKey(peerId) >= 0);
                TLRPC.User user = MessagesController.getInstance(this.currentAccount).getUser(Long.valueOf(peerId));
                if ((user != null && user.bot) || UserObject.isDeleted(user)) {
                    z = true;
                }
                if (z) {
                    this.participants.remove(i3);
                    this.participantsMap.remove(peerId);
                    i3--;
                    size2--;
                }
                i3++;
            }
            try {
                if (this.info.participants_count <= 200) {
                    final int currentTime = ConnectionsManager.getInstance(this.currentAccount).getCurrentTime();
                    Collections.sort(arrayList, new Comparator() {
                        @Override
                        public final int compare(Object obj, Object obj2) {
                            int lambda$loadChatParticipants$2;
                            lambda$loadChatParticipants$2 = GroupVoipInviteAlert.this.lambda$loadChatParticipants$2(currentTime, (TLObject) obj, (TLObject) obj2);
                            return lambda$loadChatParticipants$2;
                        }
                    });
                }
            } catch (Exception e) {
                FileLog.e(e);
            }
        }
        if (this.delayResults <= 0) {
            this.loadingUsers = false;
            this.firstLoaded = true;
            if (this.flickerProgressRow == 1) {
                itemCount = 1;
            } else {
                RecyclerView.Adapter adapter = this.listViewAdapter;
                itemCount = adapter != null ? adapter.getItemCount() - 1 : 0;
            }
            showItemsAnimated(itemCount);
            if (this.participants.isEmpty()) {
                this.showContacts = true;
                fillContacts();
            }
        }
        updateRows();
        RecyclerView.Adapter adapter2 = this.listViewAdapter;
        if (adapter2 != null) {
            adapter2.notifyDataSetChanged();
            if (this.emptyView != null && this.listViewAdapter.getItemCount() == 0 && this.firstLoaded) {
                this.emptyView.showProgress(false, true);
            }
        }
    }

    public void lambda$loadChatParticipants$4(final TLRPC.TL_channels_getParticipants tL_channels_getParticipants, final TLObject tLObject, final TLRPC.TL_error tL_error) {
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                GroupVoipInviteAlert.this.lambda$loadChatParticipants$3(tL_error, tLObject, tL_channels_getParticipants);
            }
        });
    }

    public void lambda$new$0(View view, int i) {
        if (i == this.addNewRow) {
            this.delegate.copyInviteLink();
            dismiss();
        } else if (view instanceof ManageChatUserCell) {
            ManageChatUserCell manageChatUserCell = (ManageChatUserCell) view;
            if (this.invitedUsers.contains(Long.valueOf(manageChatUserCell.getUserId()))) {
                return;
            }
            this.delegate.inviteUser(manageChatUserCell.getUserId());
        }
    }

    private void loadChatParticipants(int i, int i2) {
        if (this.loadingUsers) {
            return;
        }
        this.contactsEndReached = false;
        loadChatParticipants(i, i2, true);
    }

    private void updateRows() {
        this.addNewRow = -1;
        this.participantsStartRow = -1;
        this.participantsEndRow = -1;
        this.contactsHeaderRow = -1;
        this.contactsStartRow = -1;
        this.contactsEndRow = -1;
        this.membersHeaderRow = -1;
        this.lastRow = -1;
        boolean z = true;
        this.rowCount = 1;
        this.emptyRow = 0;
        if (ChatObject.isPublic(this.currentChat) || ChatObject.canUserDoAdminAction(this.currentChat, 3)) {
            int i = this.rowCount;
            this.rowCount = i + 1;
            this.addNewRow = i;
        }
        if (!this.loadingUsers || this.firstLoaded) {
            if (this.contacts.isEmpty()) {
                z = false;
            } else {
                int i2 = this.rowCount;
                int i3 = i2 + 1;
                this.rowCount = i3;
                this.contactsHeaderRow = i2;
                this.contactsStartRow = i3;
                int size = i3 + this.contacts.size();
                this.rowCount = size;
                this.contactsEndRow = size;
            }
            if (!this.participants.isEmpty()) {
                if (z) {
                    int i4 = this.rowCount;
                    this.rowCount = i4 + 1;
                    this.membersHeaderRow = i4;
                }
                int i5 = this.rowCount;
                this.participantsStartRow = i5;
                int size2 = i5 + this.participants.size();
                this.rowCount = size2;
                this.participantsEndRow = size2;
            }
        }
        if (this.loadingUsers) {
            int i6 = this.rowCount;
            this.rowCount = i6 + 1;
            this.flickerProgressRow = i6;
        }
        int i7 = this.rowCount;
        this.rowCount = i7 + 1;
        this.lastRow = i7;
    }

    protected void loadChatParticipants(int i, int i2, boolean z) {
        TLRPC.TL_channelParticipantsRecent tL_channelParticipantsRecent;
        LongSparseArray longSparseArray;
        if (!ChatObject.isChannel(this.currentChat)) {
            this.loadingUsers = false;
            this.participants.clear();
            this.contacts.clear();
            this.participantsMap.clear();
            this.contactsMap.clear();
            if (this.info != null) {
                long j = UserConfig.getInstance(this.currentAccount).clientUserId;
                int size = this.info.participants.participants.size();
                for (int i3 = 0; i3 < size; i3++) {
                    TLRPC.ChatParticipant chatParticipant = this.info.participants.participants.get(i3);
                    long j2 = chatParticipant.user_id;
                    if (j2 != j && ((longSparseArray = this.ignoredUsers) == null || longSparseArray.indexOfKey(j2) < 0)) {
                        TLRPC.User user = MessagesController.getInstance(this.currentAccount).getUser(Long.valueOf(chatParticipant.user_id));
                        if (!UserObject.isDeleted(user) && !user.bot) {
                            this.participants.add(chatParticipant);
                            this.participantsMap.put(chatParticipant.user_id, chatParticipant);
                        }
                    }
                }
                if (this.participants.isEmpty()) {
                    this.showContacts = true;
                    fillContacts();
                }
            }
            updateRows();
            RecyclerView.Adapter adapter = this.listViewAdapter;
            if (adapter != null) {
                adapter.notifyDataSetChanged();
                return;
            }
            return;
        }
        this.loadingUsers = true;
        StickerEmptyView stickerEmptyView = this.emptyView;
        if (stickerEmptyView != null) {
            stickerEmptyView.showProgress(true, false);
        }
        RecyclerView.Adapter adapter2 = this.listViewAdapter;
        if (adapter2 != null) {
            adapter2.notifyDataSetChanged();
        }
        final TLRPC.TL_channels_getParticipants tL_channels_getParticipants = new TLRPC.TL_channels_getParticipants();
        tL_channels_getParticipants.channel = MessagesController.getInputChannel(this.currentChat);
        TLRPC.ChatFull chatFull = this.info;
        if (chatFull != null && chatFull.participants_count <= 200) {
            tL_channelParticipantsRecent = new TLRPC.TL_channelParticipantsRecent();
        } else {
            if (!this.contactsEndReached) {
                this.delayResults = 2;
                tL_channels_getParticipants.filter = new TLRPC.TL_channelParticipantsContacts();
                this.contactsEndReached = true;
                loadChatParticipants(0, 200, false);
                tL_channels_getParticipants.filter.q = "";
                tL_channels_getParticipants.offset = i;
                tL_channels_getParticipants.limit = i2;
                ConnectionsManager.getInstance(this.currentAccount).sendRequest(tL_channels_getParticipants, new RequestDelegate() {
                    @Override
                    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                        GroupVoipInviteAlert.this.lambda$loadChatParticipants$4(tL_channels_getParticipants, tLObject, tL_error);
                    }
                });
            }
            tL_channelParticipantsRecent = new TLRPC.TL_channelParticipantsRecent();
        }
        tL_channels_getParticipants.filter = tL_channelParticipantsRecent;
        tL_channels_getParticipants.filter.q = "";
        tL_channels_getParticipants.offset = i;
        tL_channels_getParticipants.limit = i2;
        ConnectionsManager.getInstance(this.currentAccount).sendRequest(tL_channels_getParticipants, new RequestDelegate() {
            @Override
            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                GroupVoipInviteAlert.this.lambda$loadChatParticipants$4(tL_channels_getParticipants, tLObject, tL_error);
            }
        });
    }

    @Override
    protected void onSearchViewTouched(MotionEvent motionEvent, EditTextBoldCursor editTextBoldCursor) {
        this.delegate.needOpenSearch(motionEvent, editTextBoldCursor);
    }

    @Override
    protected void search(String str) {
        this.searchAdapter.searchUsers(str);
    }

    public void setDelegate(GroupVoipInviteAlertDelegate groupVoipInviteAlertDelegate) {
        this.delegate = groupVoipInviteAlertDelegate;
    }

    @Override
    protected void updateColorKeys() {
        this.keyScrollUp = Theme.key_voipgroup_scrollUp;
        this.keyListSelector = Theme.key_voipgroup_listSelector;
        this.keySearchBackground = Theme.key_voipgroup_searchBackground;
        this.keyInviteMembersBackground = Theme.key_voipgroup_inviteMembersBackground;
        this.keyListViewBackground = Theme.key_voipgroup_listViewBackground;
        this.keyActionBarUnscrolled = Theme.key_voipgroup_actionBarUnscrolled;
        this.keyNameText = Theme.key_voipgroup_nameText;
        this.keyLastSeenText = Theme.key_voipgroup_lastSeenText;
        this.keyLastSeenTextUnscrolled = Theme.key_voipgroup_lastSeenTextUnscrolled;
        this.keySearchPlaceholder = Theme.key_voipgroup_searchPlaceholder;
        this.keySearchText = Theme.key_voipgroup_searchText;
        this.keySearchIcon = Theme.key_voipgroup_mutedIcon;
        this.keySearchIconUnscrolled = Theme.key_voipgroup_mutedIconUnscrolled;
    }
}
