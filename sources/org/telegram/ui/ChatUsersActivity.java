package org.telegram.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Property;
import android.util.SparseIntArray;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import androidx.collection.LongSparseArray;
import androidx.core.content.ContextCompat;
import androidx.core.util.Consumer;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicInteger;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ChatObject;
import org.telegram.messenger.DispatchQueue;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.MessagesStorage;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.R;
import org.telegram.messenger.UserObject;
import org.telegram.messenger.Utilities;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.ActionBarMenuItem;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.INavigationLayout;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ActionBar.ThemeDescription;
import org.telegram.ui.Adapters.SearchAdapterHelper;
import org.telegram.ui.Cells.CheckBoxCell;
import org.telegram.ui.Cells.GraySectionCell;
import org.telegram.ui.Cells.HeaderCell;
import org.telegram.ui.Cells.LoadingCell;
import org.telegram.ui.Cells.ManageChatTextCell;
import org.telegram.ui.Cells.ManageChatUserCell;
import org.telegram.ui.Cells.ShadowSectionCell;
import org.telegram.ui.Cells.TextCell;
import org.telegram.ui.Cells.TextCheckCell;
import org.telegram.ui.Cells.TextCheckCell2;
import org.telegram.ui.Cells.TextInfoPrivacyCell;
import org.telegram.ui.Cells.TextSettingsCell;
import org.telegram.ui.ChatRightsEditActivity;
import org.telegram.ui.ChatUsersActivity;
import org.telegram.ui.Components.BulletinFactory;
import org.telegram.ui.Components.FlickerLoadingView;
import org.telegram.ui.Components.GigagroupConvertAlert;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.Components.SlideChooseView;
import org.telegram.ui.Components.StickerEmptyView;
import org.telegram.ui.Components.UndoView;
import org.telegram.ui.GroupCreateActivity;

public class ChatUsersActivity extends BaseFragment implements NotificationCenter.NotificationCenterDelegate {
    private int addNew2Row;
    private int addNewRow;
    private int addNewSectionRow;
    private int addUsersRow;
    private int antiSpamInfoRow;
    private int antiSpamRow;
    private boolean antiSpamToggleLoading;
    private int blockedEmptyRow;
    private int botEndRow;
    private int botHeaderRow;
    private int botStartRow;
    private ArrayList bots;
    private boolean botsEndReached;
    private LongSparseArray botsMap;
    private int changeInfoRow;
    private long chatId;
    private ArrayList contacts;
    private boolean contactsEndReached;
    private int contactsEndRow;
    private int contactsHeaderRow;
    private LongSparseArray contactsMap;
    private int contactsStartRow;
    private TLRPC.Chat currentChat;
    private TLRPC.TL_chatBannedRights defaultBannedRights;
    private int delayResults;
    private ChatUsersActivityDelegate delegate;
    private ActionBarMenuItem doneItem;
    private int dontRestrictBoostersInfoRow;
    private int dontRestrictBoostersRow;
    private int dontRestrictBoostersSliderRow;
    private int embedLinksRow;
    private StickerEmptyView emptyView;
    private boolean firstLoaded;
    private FlickerLoadingView flickerLoadingView;
    private int gigaConvertRow;
    private int gigaHeaderRow;
    private int gigaInfoRow;
    private int hideMembersInfoRow;
    private int hideMembersRow;
    private boolean hideMembersToggleLoading;
    private LongSparseArray ignoredUsers;
    private TLRPC.ChatFull info;
    private String initialBannedRights;
    private boolean initialProfiles;
    private boolean initialSignatures;
    private int initialSlowmode;
    private boolean isChannel;
    private boolean isEnabledNotRestrictBoosters;
    private boolean isForum;
    private LinearLayoutManager layoutManager;
    private RecyclerListView listView;
    private ListAdapter listViewAdapter;
    private int loadingHeaderRow;
    private int loadingProgressRow;
    private int loadingUserCellRow;
    private boolean loadingUsers;
    private int manageTopicsRow;
    private int membersHeaderRow;
    private boolean needOpenSearch;
    private int notRestrictBoosters;
    private boolean openTransitionStarted;
    private ArrayList participants;
    private int participantsDivider2Row;
    private int participantsDividerRow;
    private int participantsEndRow;
    private int participantsInfoRow;
    private LongSparseArray participantsMap;
    private int participantsStartRow;
    private int permissionsSectionRow;
    private int pinMessagesRow;
    private boolean profiles;
    private View progressBar;
    private int recentActionsRow;
    private int removedUsersRow;
    private int restricted1SectionRow;
    private int rowCount;
    private ActionBarMenuItem searchItem;
    private SearchAdapter searchListViewAdapter;
    private boolean searching;
    private int selectType;
    private int selectedSlowmode;
    private int sendMediaEmbededLinksRow;
    private boolean sendMediaExpanded;
    private int sendMediaFilesRow;
    private int sendMediaMusicRow;
    private int sendMediaPhotosRow;
    private int sendMediaRow;
    private int sendMediaStickerGifsRow;
    private int sendMediaVideoMessagesRow;
    private int sendMediaVideosRow;
    private int sendMediaVoiceMessagesRow;
    private int sendMessagesRow;
    private int sendPollsRow;
    private int sendStickersRow;
    private int signMessagesInfoRow;
    private int signMessagesProfilesRow;
    private int signMessagesRow;
    private boolean signatures;
    private int slowmodeInfoRow;
    private int slowmodeRow;
    private int slowmodeSelectRow;
    private int type;
    private UndoView undoView;

    public class AnonymousClass10 extends GigagroupConvertAlert {
        AnonymousClass10(Context context, BaseFragment baseFragment) {
            super(context, baseFragment);
        }

        public void lambda$onCovert$0(boolean z) {
            if (!z || ((BaseFragment) ChatUsersActivity.this).parentLayout == null) {
                return;
            }
            BaseFragment baseFragment = (BaseFragment) ((BaseFragment) ChatUsersActivity.this).parentLayout.getFragmentStack().get(((BaseFragment) ChatUsersActivity.this).parentLayout.getFragmentStack().size() - 2);
            if (!(baseFragment instanceof ChatEditActivity)) {
                ChatUsersActivity.this.lambda$onBackPressed$323();
                return;
            }
            baseFragment.removeSelfFromStack();
            Bundle bundle = new Bundle();
            bundle.putLong("chat_id", ChatUsersActivity.this.chatId);
            ChatEditActivity chatEditActivity = new ChatEditActivity(bundle);
            chatEditActivity.setInfo(ChatUsersActivity.this.info);
            ((BaseFragment) ChatUsersActivity.this).parentLayout.addFragmentToStack(chatEditActivity, ((BaseFragment) ChatUsersActivity.this).parentLayout.getFragmentStack().size() - 1);
            ChatUsersActivity.this.lambda$onBackPressed$323();
            chatEditActivity.showConvertTooltip();
        }

        @Override
        protected void onCancel() {
        }

        @Override
        protected void onCovert() {
            ChatUsersActivity.this.getMessagesController().convertToGigaGroup(ChatUsersActivity.this.getParentActivity(), ChatUsersActivity.this.currentChat, ChatUsersActivity.this, new MessagesStorage.BooleanCallback() {
                @Override
                public final void run(boolean z) {
                    ChatUsersActivity.AnonymousClass10.this.lambda$onCovert$0(z);
                }
            });
        }
    }

    public class AnonymousClass8 implements ChatUsersActivityDelegate {
        AnonymousClass8() {
        }

        public void lambda$didSelectUser$0(TLRPC.User user) {
            if (BulletinFactory.canShowBulletin(ChatUsersActivity.this)) {
                BulletinFactory.createPromoteToAdminBulletin(ChatUsersActivity.this, user.first_name).show();
            }
        }

        @Override
        public void didAddParticipantToList(long j, TLObject tLObject) {
            if (tLObject == null || ChatUsersActivity.this.participantsMap.get(j) != null) {
                return;
            }
            DiffCallback saveState = ChatUsersActivity.this.saveState();
            ChatUsersActivity.this.participants.add(tLObject);
            ChatUsersActivity.this.participantsMap.put(j, tLObject);
            ChatUsersActivity chatUsersActivity = ChatUsersActivity.this;
            chatUsersActivity.sortAdmins(chatUsersActivity.participants);
            ChatUsersActivity.this.updateListAnimated(saveState);
        }

        @Override
        public void didChangeOwner(TLRPC.User user) {
            ChatUsersActivity.this.onOwnerChaged(user);
        }

        @Override
        public void didKickParticipant(long j) {
            ChatUsersActivityDelegate.CC.$default$didKickParticipant(this, j);
        }

        @Override
        public void didSelectUser(long j) {
            final TLRPC.User user = ChatUsersActivity.this.getMessagesController().getUser(Long.valueOf(j));
            if (user != null) {
                AndroidUtilities.runOnUIThread(new Runnable() {
                    @Override
                    public final void run() {
                        ChatUsersActivity.AnonymousClass8.this.lambda$didSelectUser$0(user);
                    }
                }, 200L);
            }
            if (ChatUsersActivity.this.participantsMap.get(j) == null) {
                DiffCallback saveState = ChatUsersActivity.this.saveState();
                TLRPC.TL_channelParticipantAdmin tL_channelParticipantAdmin = new TLRPC.TL_channelParticipantAdmin();
                TLRPC.TL_peerUser tL_peerUser = new TLRPC.TL_peerUser();
                tL_channelParticipantAdmin.peer = tL_peerUser;
                tL_peerUser.user_id = user.id;
                tL_channelParticipantAdmin.date = ChatUsersActivity.this.getConnectionsManager().getCurrentTime();
                tL_channelParticipantAdmin.promoted_by = ChatUsersActivity.this.getAccountInstance().getUserConfig().clientUserId;
                ChatUsersActivity.this.participants.add(tL_channelParticipantAdmin);
                ChatUsersActivity.this.participantsMap.put(user.id, tL_channelParticipantAdmin);
                ChatUsersActivity chatUsersActivity = ChatUsersActivity.this;
                chatUsersActivity.sortAdmins(chatUsersActivity.participants);
                ChatUsersActivity.this.updateListAnimated(saveState);
            }
        }
    }

    public class AnonymousClass9 implements GroupCreateActivity.ContactsAddActivityDelegate {
        final GroupCreateActivity val$fragment;

        AnonymousClass9(GroupCreateActivity groupCreateActivity) {
            this.val$fragment = groupCreateActivity;
        }

        public void lambda$didSelectUsers$0(TLRPC.User user) {
            TLRPC.TL_chatParticipant tL_chatParticipant;
            DiffCallback saveState = ChatUsersActivity.this.saveState();
            ArrayList arrayList = (ChatUsersActivity.this.contactsMap == null || ChatUsersActivity.this.contactsMap.size() == 0) ? ChatUsersActivity.this.participants : ChatUsersActivity.this.contacts;
            LongSparseArray longSparseArray = (ChatUsersActivity.this.contactsMap == null || ChatUsersActivity.this.contactsMap.size() == 0) ? ChatUsersActivity.this.participantsMap : ChatUsersActivity.this.contactsMap;
            if (longSparseArray.get(user.id) == null) {
                if (ChatObject.isChannel(ChatUsersActivity.this.currentChat)) {
                    ?? tL_channelParticipant = new TLRPC.TL_channelParticipant();
                    tL_channelParticipant.inviter_id = ChatUsersActivity.this.getUserConfig().getClientUserId();
                    TLRPC.TL_peerUser tL_peerUser = new TLRPC.TL_peerUser();
                    tL_channelParticipant.peer = tL_peerUser;
                    tL_peerUser.user_id = user.id;
                    tL_channelParticipant.date = ChatUsersActivity.this.getConnectionsManager().getCurrentTime();
                    tL_chatParticipant = tL_channelParticipant;
                } else {
                    TLRPC.TL_chatParticipant tL_chatParticipant2 = new TLRPC.TL_chatParticipant();
                    tL_chatParticipant2.user_id = user.id;
                    tL_chatParticipant2.inviter_id = ChatUsersActivity.this.getUserConfig().getClientUserId();
                    tL_chatParticipant = tL_chatParticipant2;
                }
                arrayList.add(0, tL_chatParticipant);
                longSparseArray.put(user.id, tL_chatParticipant);
            }
            if (arrayList == ChatUsersActivity.this.participants) {
                ChatUsersActivity chatUsersActivity = ChatUsersActivity.this;
                chatUsersActivity.sortAdmins(chatUsersActivity.participants);
            }
            ChatUsersActivity.this.updateListAnimated(saveState);
        }

        public static void lambda$didSelectUsers$1(TLRPC.User user) {
        }

        @Override
        public void didSelectUsers(ArrayList arrayList, int i) {
            if (this.val$fragment.getParentActivity() == null) {
                return;
            }
            ChatUsersActivity.this.getMessagesController().addUsersToChat(ChatUsersActivity.this.currentChat, ChatUsersActivity.this, arrayList, i, new Consumer() {
                @Override
                public final void accept(Object obj) {
                    ChatUsersActivity.AnonymousClass9.this.lambda$didSelectUsers$0((TLRPC.User) obj);
                }
            }, new Consumer() {
                @Override
                public final void accept(Object obj) {
                    ChatUsersActivity.AnonymousClass9.lambda$didSelectUsers$1((TLRPC.User) obj);
                }
            }, null);
        }

        @Override
        public void needAddBot(TLRPC.User user) {
            ChatUsersActivity.this.openRightsEdit(user.id, null, null, null, "", true, 0, false);
        }
    }

    public interface ChatUsersActivityDelegate {

        public abstract class CC {
            public static void $default$didChangeOwner(ChatUsersActivityDelegate chatUsersActivityDelegate, TLRPC.User user) {
            }

            public static void $default$didKickParticipant(ChatUsersActivityDelegate chatUsersActivityDelegate, long j) {
            }

            public static void $default$didSelectUser(ChatUsersActivityDelegate chatUsersActivityDelegate, long j) {
            }
        }

        void didAddParticipantToList(long j, TLObject tLObject);

        void didChangeOwner(TLRPC.User user);

        void didKickParticipant(long j);

        void didSelectUser(long j);
    }

    public class DiffCallback extends DiffUtil.Callback {
        SparseIntArray newPositionToItem;
        int oldBotEndRow;
        int oldBotStartRow;
        private ArrayList oldBots;
        private ArrayList oldContacts;
        int oldContactsEndRow;
        int oldContactsStartRow;
        private ArrayList oldParticipants;
        int oldParticipantsEndRow;
        int oldParticipantsStartRow;
        SparseIntArray oldPositionToItem;
        int oldRowCount;

        private DiffCallback() {
            this.oldPositionToItem = new SparseIntArray();
            this.newPositionToItem = new SparseIntArray();
            this.oldParticipants = new ArrayList();
            this.oldBots = new ArrayList();
            this.oldContacts = new ArrayList();
        }

        private void put(int i, int i2, SparseIntArray sparseIntArray) {
            if (i2 >= 0) {
                sparseIntArray.put(i2, i);
            }
        }

        @Override
        public boolean areContentsTheSame(int i, int i2) {
            return areItemsTheSame(i, i2) && ChatUsersActivity.this.restricted1SectionRow != i2;
        }

        @Override
        public boolean areItemsTheSame(int i, int i2) {
            TLObject tLObject;
            ArrayList arrayList;
            int i3;
            if (i >= this.oldBotStartRow && i < this.oldBotEndRow && i2 >= ChatUsersActivity.this.botStartRow && i2 < ChatUsersActivity.this.botEndRow) {
                tLObject = (TLObject) this.oldBots.get(i - this.oldBotStartRow);
                arrayList = ChatUsersActivity.this.bots;
                i3 = ChatUsersActivity.this.botStartRow;
            } else if (i >= this.oldContactsStartRow && i < this.oldContactsEndRow && i2 >= ChatUsersActivity.this.contactsStartRow && i2 < ChatUsersActivity.this.contactsEndRow) {
                tLObject = (TLObject) this.oldContacts.get(i - this.oldContactsStartRow);
                arrayList = ChatUsersActivity.this.contacts;
                i3 = ChatUsersActivity.this.contactsStartRow;
            } else {
                if (i < this.oldParticipantsStartRow || i >= this.oldParticipantsEndRow || i2 < ChatUsersActivity.this.participantsStartRow || i2 >= ChatUsersActivity.this.participantsEndRow) {
                    return this.oldPositionToItem.get(i) == this.newPositionToItem.get(i2);
                }
                tLObject = (TLObject) this.oldParticipants.get(i - this.oldParticipantsStartRow);
                arrayList = ChatUsersActivity.this.participants;
                i3 = ChatUsersActivity.this.participantsStartRow;
            }
            return tLObject.equals(arrayList.get(i2 - i3));
        }

        public void fillPositions(SparseIntArray sparseIntArray) {
            sparseIntArray.clear();
            put(1, ChatUsersActivity.this.recentActionsRow, sparseIntArray);
            put(2, ChatUsersActivity.this.addNewRow, sparseIntArray);
            put(3, ChatUsersActivity.this.addNew2Row, sparseIntArray);
            put(4, ChatUsersActivity.this.addNewSectionRow, sparseIntArray);
            put(5, ChatUsersActivity.this.restricted1SectionRow, sparseIntArray);
            put(6, ChatUsersActivity.this.participantsDividerRow, sparseIntArray);
            put(7, ChatUsersActivity.this.participantsDivider2Row, sparseIntArray);
            put(8, ChatUsersActivity.this.gigaHeaderRow, sparseIntArray);
            put(9, ChatUsersActivity.this.gigaConvertRow, sparseIntArray);
            put(10, ChatUsersActivity.this.gigaInfoRow, sparseIntArray);
            put(11, ChatUsersActivity.this.participantsInfoRow, sparseIntArray);
            put(12, ChatUsersActivity.this.blockedEmptyRow, sparseIntArray);
            put(13, ChatUsersActivity.this.permissionsSectionRow, sparseIntArray);
            put(14, ChatUsersActivity.this.sendMessagesRow, sparseIntArray);
            put(15, ChatUsersActivity.this.sendMediaRow, sparseIntArray);
            put(16, ChatUsersActivity.this.sendStickersRow, sparseIntArray);
            put(17, ChatUsersActivity.this.sendPollsRow, sparseIntArray);
            put(18, ChatUsersActivity.this.embedLinksRow, sparseIntArray);
            put(19, ChatUsersActivity.this.addUsersRow, sparseIntArray);
            int i = 20;
            put(20, ChatUsersActivity.this.pinMessagesRow, sparseIntArray);
            if (ChatUsersActivity.this.isForum) {
                i = 21;
                put(21, ChatUsersActivity.this.manageTopicsRow, sparseIntArray);
            }
            put(i + 1, ChatUsersActivity.this.changeInfoRow, sparseIntArray);
            put(i + 2, ChatUsersActivity.this.removedUsersRow, sparseIntArray);
            put(i + 3, ChatUsersActivity.this.contactsHeaderRow, sparseIntArray);
            put(i + 4, ChatUsersActivity.this.botHeaderRow, sparseIntArray);
            put(i + 5, ChatUsersActivity.this.membersHeaderRow, sparseIntArray);
            put(i + 6, ChatUsersActivity.this.slowmodeRow, sparseIntArray);
            put(i + 7, ChatUsersActivity.this.slowmodeSelectRow, sparseIntArray);
            put(i + 8, ChatUsersActivity.this.slowmodeInfoRow, sparseIntArray);
            put(i + 9, ChatUsersActivity.this.dontRestrictBoostersRow, sparseIntArray);
            put(i + 10, ChatUsersActivity.this.dontRestrictBoostersSliderRow, sparseIntArray);
            put(i + 11, ChatUsersActivity.this.dontRestrictBoostersInfoRow, sparseIntArray);
            put(i + 12, ChatUsersActivity.this.loadingProgressRow, sparseIntArray);
            put(i + 13, ChatUsersActivity.this.loadingUserCellRow, sparseIntArray);
            put(i + 14, ChatUsersActivity.this.loadingHeaderRow, sparseIntArray);
            put(i + 15, ChatUsersActivity.this.signMessagesRow, sparseIntArray);
            put(i + 16, ChatUsersActivity.this.signMessagesProfilesRow, sparseIntArray);
            put(i + 17, ChatUsersActivity.this.signMessagesInfoRow, sparseIntArray);
        }

        @Override
        public int getNewListSize() {
            return ChatUsersActivity.this.rowCount;
        }

        @Override
        public int getOldListSize() {
            return this.oldRowCount;
        }
    }

    public class ListAdapter extends RecyclerListView.SelectionAdapter {
        private Context mContext;

        public ListAdapter(Context context) {
            this.mContext = context;
        }

        public boolean lambda$onCreateViewHolder$0(ManageChatUserCell manageChatUserCell, boolean z) {
            return ChatUsersActivity.this.createMenuForParticipant(ChatUsersActivity.this.listViewAdapter.getItem(((Integer) manageChatUserCell.getTag()).intValue()), !z, manageChatUserCell);
        }

        public void lambda$onCreateViewHolder$1(int i) {
            if (ChatUsersActivity.this.info == null) {
                return;
            }
            boolean z = (ChatUsersActivity.this.selectedSlowmode > 0 && i == 0) || (ChatUsersActivity.this.selectedSlowmode == 0 && i > 0);
            ChatUsersActivity.this.selectedSlowmode = i;
            if (z) {
                DiffCallback saveState = ChatUsersActivity.this.saveState();
                ChatUsersActivity.this.updateRows();
                ChatUsersActivity.this.updateListAnimated(saveState);
            }
            ChatUsersActivity.this.listViewAdapter.notifyItemChanged(ChatUsersActivity.this.slowmodeInfoRow);
        }

        public void lambda$onCreateViewHolder$2(int i) {
            ChatUsersActivity.this.notRestrictBoosters = i + 1;
        }

        public TLObject getItem(int i) {
            ArrayList arrayList;
            int i2;
            if (i >= ChatUsersActivity.this.participantsStartRow && i < ChatUsersActivity.this.participantsEndRow) {
                arrayList = ChatUsersActivity.this.participants;
                i2 = ChatUsersActivity.this.participantsStartRow;
            } else if (i >= ChatUsersActivity.this.contactsStartRow && i < ChatUsersActivity.this.contactsEndRow) {
                arrayList = ChatUsersActivity.this.contacts;
                i2 = ChatUsersActivity.this.contactsStartRow;
            } else {
                if (i < ChatUsersActivity.this.botStartRow || i >= ChatUsersActivity.this.botEndRow) {
                    return null;
                }
                arrayList = ChatUsersActivity.this.bots;
                i2 = ChatUsersActivity.this.botStartRow;
            }
            return (TLObject) arrayList.get(i - i2);
        }

        @Override
        public int getItemCount() {
            return ChatUsersActivity.this.rowCount;
        }

        @Override
        public int getItemViewType(int i) {
            if (i == ChatUsersActivity.this.addNewRow || i == ChatUsersActivity.this.addNew2Row || i == ChatUsersActivity.this.recentActionsRow || i == ChatUsersActivity.this.gigaConvertRow) {
                return 2;
            }
            if ((i >= ChatUsersActivity.this.participantsStartRow && i < ChatUsersActivity.this.participantsEndRow) || ((i >= ChatUsersActivity.this.botStartRow && i < ChatUsersActivity.this.botEndRow) || (i >= ChatUsersActivity.this.contactsStartRow && i < ChatUsersActivity.this.contactsEndRow))) {
                return 0;
            }
            if (i == ChatUsersActivity.this.addNewSectionRow || i == ChatUsersActivity.this.participantsDividerRow || i == ChatUsersActivity.this.participantsDivider2Row) {
                return 3;
            }
            if (i == ChatUsersActivity.this.restricted1SectionRow || i == ChatUsersActivity.this.permissionsSectionRow || i == ChatUsersActivity.this.slowmodeRow || i == ChatUsersActivity.this.gigaHeaderRow) {
                return 5;
            }
            if (i == ChatUsersActivity.this.participantsInfoRow || i == ChatUsersActivity.this.slowmodeInfoRow || i == ChatUsersActivity.this.dontRestrictBoostersInfoRow || i == ChatUsersActivity.this.gigaInfoRow || i == ChatUsersActivity.this.antiSpamInfoRow || i == ChatUsersActivity.this.hideMembersInfoRow || i == ChatUsersActivity.this.signMessagesInfoRow) {
                return 1;
            }
            if (i == ChatUsersActivity.this.blockedEmptyRow) {
                return 4;
            }
            if (i == ChatUsersActivity.this.removedUsersRow) {
                return 6;
            }
            if (i == ChatUsersActivity.this.changeInfoRow || i == ChatUsersActivity.this.addUsersRow || i == ChatUsersActivity.this.pinMessagesRow || i == ChatUsersActivity.this.sendMessagesRow || i == ChatUsersActivity.this.sendStickersRow || i == ChatUsersActivity.this.embedLinksRow || i == ChatUsersActivity.this.manageTopicsRow || i == ChatUsersActivity.this.dontRestrictBoostersRow) {
                return 7;
            }
            if (i == ChatUsersActivity.this.membersHeaderRow || i == ChatUsersActivity.this.contactsHeaderRow || i == ChatUsersActivity.this.botHeaderRow || i == ChatUsersActivity.this.loadingHeaderRow) {
                return 8;
            }
            if (i == ChatUsersActivity.this.slowmodeSelectRow) {
                return 9;
            }
            if (i == ChatUsersActivity.this.loadingProgressRow) {
                return 10;
            }
            if (i == ChatUsersActivity.this.loadingUserCellRow) {
                return 11;
            }
            if (i == ChatUsersActivity.this.antiSpamRow || i == ChatUsersActivity.this.hideMembersRow) {
                return 12;
            }
            if (ChatUsersActivity.this.isExpandableSendMediaRow(i)) {
                return 13;
            }
            if (i == ChatUsersActivity.this.sendMediaRow) {
                return 14;
            }
            if (i == ChatUsersActivity.this.dontRestrictBoostersSliderRow) {
                return 15;
            }
            return (i == ChatUsersActivity.this.signMessagesRow || i == ChatUsersActivity.this.signMessagesProfilesRow) ? 16 : 0;
        }

        @Override
        public boolean isEnabled(RecyclerView.ViewHolder viewHolder) {
            int itemViewType = viewHolder.getItemViewType();
            if (itemViewType == 16) {
                return true;
            }
            if (itemViewType == 7 || itemViewType == 14) {
                return ChatObject.canBlockUsers(ChatUsersActivity.this.currentChat);
            }
            if (itemViewType == 0) {
                Object currentObject = ((ManageChatUserCell) viewHolder.itemView).getCurrentObject();
                return (ChatUsersActivity.this.type != 1 && (currentObject instanceof TLRPC.User) && ((TLRPC.User) currentObject).self) ? false : true;
            }
            int adapterPosition = viewHolder.getAdapterPosition();
            if (itemViewType == 0 || itemViewType == 2 || itemViewType == 6) {
                return true;
            }
            if (itemViewType == 12) {
                if (adapterPosition == ChatUsersActivity.this.antiSpamRow) {
                    return ChatObject.canUserDoAdminAction(ChatUsersActivity.this.currentChat, 13);
                }
                if (adapterPosition == ChatUsersActivity.this.hideMembersRow) {
                    return ChatObject.canUserDoAdminAction(ChatUsersActivity.this.currentChat, 2);
                }
            }
            return itemViewType == 13;
        }

        @Override
        public void onBindViewHolder(androidx.recyclerview.widget.RecyclerView.ViewHolder r20, int r21) {
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.ChatUsersActivity.ListAdapter.onBindViewHolder(androidx.recyclerview.widget.RecyclerView$ViewHolder, int):void");
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            Drawable themedDrawableByKey;
            View view;
            View view2;
            View view3;
            switch (i) {
                case 0:
                    ManageChatUserCell manageChatUserCell = new ManageChatUserCell(this.mContext, (ChatUsersActivity.this.type == 0 || ChatUsersActivity.this.type == 3) ? 7 : 6, (ChatUsersActivity.this.type == 0 || ChatUsersActivity.this.type == 3) ? 6 : 2, ChatUsersActivity.this.selectType == 0);
                    manageChatUserCell.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                    manageChatUserCell.setDelegate(new ManageChatUserCell.ManageChatUserCellDelegate() {
                        @Override
                        public final boolean onOptionsButtonCheck(ManageChatUserCell manageChatUserCell2, boolean z) {
                            boolean lambda$onCreateViewHolder$0;
                            lambda$onCreateViewHolder$0 = ChatUsersActivity.ListAdapter.this.lambda$onCreateViewHolder$0(manageChatUserCell2, z);
                            return lambda$onCreateViewHolder$0;
                        }
                    });
                    view2 = manageChatUserCell;
                    break;
                case 1:
                    view2 = new TextInfoPrivacyCell(this.mContext);
                    break;
                case 2:
                    view3 = new ManageChatTextCell(this.mContext);
                    view3.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                    view2 = view3;
                    break;
                case 3:
                    view2 = new ShadowSectionCell(this.mContext);
                    break;
                case 4:
                    TextInfoPrivacyCell textInfoPrivacyCell = new TextInfoPrivacyCell(this.mContext);
                    textInfoPrivacyCell.setText(LocaleController.getString(ChatUsersActivity.this.isChannel ? R.string.NoBlockedChannel2 : R.string.NoBlockedGroup2));
                    themedDrawableByKey = Theme.getThemedDrawableByKey(this.mContext, R.drawable.greydivider_bottom, Theme.key_windowBackgroundGrayShadow);
                    view = textInfoPrivacyCell;
                    view.setBackground(themedDrawableByKey);
                    view2 = view;
                    break;
                case 5:
                    HeaderCell headerCell = new HeaderCell(this.mContext, Theme.key_windowBackgroundWhiteBlueHeader, 21, 11, false);
                    headerCell.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                    headerCell.setHeight(43);
                    view2 = headerCell;
                    break;
                case 6:
                    view3 = new TextSettingsCell(this.mContext);
                    view3.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                    view2 = view3;
                    break;
                case 7:
                case 14:
                    view3 = new TextCheckCell2(this.mContext);
                    view3.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                    view2 = view3;
                    break;
                case 8:
                    themedDrawableByKey = null;
                    view = new GraySectionCell(this.mContext);
                    view.setBackground(themedDrawableByKey);
                    view2 = view;
                    break;
                case 9:
                default:
                    SlideChooseView slideChooseView = new SlideChooseView(this.mContext);
                    slideChooseView.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                    int i2 = ChatUsersActivity.this.selectedSlowmode;
                    String string = LocaleController.getString("SlowmodeOff", R.string.SlowmodeOff);
                    int i3 = R.string.SlowmodeSeconds;
                    String formatString = LocaleController.formatString("SlowmodeSeconds", i3, 10);
                    String formatString2 = LocaleController.formatString("SlowmodeSeconds", i3, 30);
                    int i4 = R.string.SlowmodeMinutes;
                    slideChooseView.setOptions(i2, string, formatString, formatString2, LocaleController.formatString("SlowmodeMinutes", i4, 1), LocaleController.formatString("SlowmodeMinutes", i4, 5), LocaleController.formatString("SlowmodeMinutes", i4, 15), LocaleController.formatString("SlowmodeHours", R.string.SlowmodeHours, 1));
                    slideChooseView.setCallback(new SlideChooseView.Callback() {
                        @Override
                        public final void onOptionSelected(int i5) {
                            ChatUsersActivity.ListAdapter.this.lambda$onCreateViewHolder$1(i5);
                        }

                        @Override
                        public void onTouchEnd() {
                            SlideChooseView.Callback.CC.$default$onTouchEnd(this);
                        }
                    });
                    view2 = slideChooseView;
                    break;
                case 10:
                    view2 = new LoadingCell(this.mContext, AndroidUtilities.dp(40.0f), AndroidUtilities.dp(120.0f));
                    break;
                case 11:
                    FlickerLoadingView flickerLoadingView = new FlickerLoadingView(this.mContext);
                    flickerLoadingView.setIsSingleCell(true);
                    flickerLoadingView.setViewType(6);
                    flickerLoadingView.showDate(false);
                    flickerLoadingView.setPaddingLeft(AndroidUtilities.dp(5.0f));
                    flickerLoadingView.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                    flickerLoadingView.setLayoutParams(new RecyclerView.LayoutParams(-1, -1));
                    view2 = flickerLoadingView;
                    break;
                case 12:
                    TextCell textCell = new TextCell(this.mContext, 23, false, true, ChatUsersActivity.this.getResourceProvider());
                    textCell.heightDp = 50;
                    view3 = textCell;
                    view3.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                    view2 = view3;
                    break;
                case 13:
                    CheckBoxCell checkBoxCell = new CheckBoxCell(this.mContext, 4, 21, ChatUsersActivity.this.getResourceProvider());
                    checkBoxCell.getCheckBoxRound().setDrawBackgroundAsArc(14);
                    checkBoxCell.getCheckBoxRound().setColor(Theme.key_switch2TrackChecked, Theme.key_radioBackground, Theme.key_checkboxCheck);
                    checkBoxCell.setEnabled(true);
                    checkBoxCell.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                    view2 = checkBoxCell;
                    break;
                case 15:
                    SlideChooseView slideChooseView2 = new SlideChooseView(this.mContext);
                    slideChooseView2.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                    Drawable drawable = ContextCompat.getDrawable(ChatUsersActivity.this.getContext(), R.drawable.mini_boost_profile_badge);
                    Context context = ChatUsersActivity.this.getContext();
                    int i5 = R.drawable.mini_boost_profile_badge2;
                    slideChooseView2.setOptions(ChatUsersActivity.this.notRestrictBoosters > 0 ? ChatUsersActivity.this.notRestrictBoosters - 1 : 0, new Drawable[]{drawable, ContextCompat.getDrawable(context, i5), ContextCompat.getDrawable(ChatUsersActivity.this.getContext(), i5), ContextCompat.getDrawable(ChatUsersActivity.this.getContext(), i5), ContextCompat.getDrawable(ChatUsersActivity.this.getContext(), i5)}, "1", "2", "3", "4", "5");
                    slideChooseView2.setCallback(new SlideChooseView.Callback() {
                        @Override
                        public final void onOptionSelected(int i6) {
                            ChatUsersActivity.ListAdapter.this.lambda$onCreateViewHolder$2(i6);
                        }

                        @Override
                        public void onTouchEnd() {
                            SlideChooseView.Callback.CC.$default$onTouchEnd(this);
                        }
                    });
                    view2 = slideChooseView2;
                    break;
                case 16:
                    view3 = new TextCheckCell(this.mContext, ChatUsersActivity.this.getResourceProvider());
                    view3.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                    view2 = view3;
                    break;
            }
            return new RecyclerListView.Holder(view2);
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
        private int contactsStartRow;
        private int globalStartRow;
        private int groupStartRow;
        private Context mContext;
        private SearchAdapterHelper searchAdapterHelper;
        private boolean searchInProgress;
        private Runnable searchRunnable;
        private ArrayList searchResult = new ArrayList();
        private LongSparseArray searchResultMap = new LongSparseArray();
        private ArrayList searchResultNames = new ArrayList();
        private int totalCount = 0;

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
                    return SearchAdapterHelper.SearchAdapterHelperDelegate.CC.$default$getExcludeCallParticipants(this);
                }

                @Override
                public LongSparseArray getExcludeUsers() {
                    return SearchAdapterHelper.SearchAdapterHelperDelegate.CC.$default$getExcludeUsers(this);
                }

                @Override
                public final void onDataSetChanged(int i) {
                    ChatUsersActivity.SearchAdapter.this.lambda$new$0(i);
                }

                @Override
                public void onSetHashtags(ArrayList arrayList, HashMap hashMap) {
                    SearchAdapterHelper.SearchAdapterHelperDelegate.CC.$default$onSetHashtags(this, arrayList, hashMap);
                }
            });
        }

        public void lambda$new$0(int i) {
            if (this.searchAdapterHelper.isSearchInProgress()) {
                return;
            }
            int itemCount = getItemCount();
            notifyDataSetChanged();
            if (getItemCount() > itemCount) {
                ChatUsersActivity.this.showItemsAnimated(itemCount);
            }
            if (this.searchInProgress || getItemCount() != 0 || i == 0) {
                return;
            }
            ChatUsersActivity.this.emptyView.showProgress(false, true);
        }

        public boolean lambda$onCreateViewHolder$5(ManageChatUserCell manageChatUserCell, boolean z) {
            TLObject item = getItem(((Integer) manageChatUserCell.getTag()).intValue());
            if (!(item instanceof TLRPC.ChannelParticipant)) {
                return false;
            }
            return ChatUsersActivity.this.createMenuForParticipant((TLRPC.ChannelParticipant) item, !z, manageChatUserCell);
        }

        public void lambda$processSearch$2(java.lang.String r24, java.util.ArrayList r25, java.util.ArrayList r26) {
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.ChatUsersActivity.SearchAdapter.lambda$processSearch$2(java.lang.String, java.util.ArrayList, java.util.ArrayList):void");
        }

        public void lambda$processSearch$3(final String str) {
            Runnable runnable = null;
            this.searchRunnable = null;
            final ArrayList arrayList = (ChatObject.isChannel(ChatUsersActivity.this.currentChat) || ChatUsersActivity.this.info == null) ? null : new ArrayList(ChatUsersActivity.this.info.participants.participants);
            final ArrayList arrayList2 = ChatUsersActivity.this.selectType == 1 ? new ArrayList(ChatUsersActivity.this.getContactsController().contacts) : null;
            if (arrayList == null && arrayList2 == null) {
                this.searchInProgress = false;
            } else {
                runnable = new Runnable() {
                    @Override
                    public final void run() {
                        ChatUsersActivity.SearchAdapter.this.lambda$processSearch$2(str, arrayList, arrayList2);
                    }
                };
            }
            this.searchAdapterHelper.queryServerSearch(str, ChatUsersActivity.this.selectType != 0, false, true, false, false, ChatObject.isChannel(ChatUsersActivity.this.currentChat) ? ChatUsersActivity.this.chatId : 0L, false, ChatUsersActivity.this.type, 1, 0L, runnable);
        }

        public void lambda$updateSearchResults$4(ArrayList arrayList, LongSparseArray longSparseArray, ArrayList arrayList2, ArrayList arrayList3) {
            if (ChatUsersActivity.this.searching) {
                this.searchInProgress = false;
                this.searchResult = arrayList;
                this.searchResultMap = longSparseArray;
                this.searchResultNames = arrayList2;
                this.searchAdapterHelper.mergeResults(arrayList);
                if (!ChatObject.isChannel(ChatUsersActivity.this.currentChat)) {
                    ArrayList groupSearch = this.searchAdapterHelper.getGroupSearch();
                    groupSearch.clear();
                    groupSearch.addAll(arrayList3);
                }
                int itemCount = getItemCount();
                notifyDataSetChanged();
                if (getItemCount() > itemCount) {
                    ChatUsersActivity.this.showItemsAnimated(itemCount);
                }
                if (this.searchAdapterHelper.isSearchInProgress() || getItemCount() != 0) {
                    return;
                }
                ChatUsersActivity.this.emptyView.showProgress(false, true);
            }
        }

        public void lambda$searchUsers$1(final String str) {
            AndroidUtilities.runOnUIThread(new Runnable() {
                @Override
                public final void run() {
                    ChatUsersActivity.SearchAdapter.this.lambda$processSearch$3(str);
                }
            });
        }

        private void updateSearchResults(final ArrayList arrayList, final LongSparseArray longSparseArray, final ArrayList arrayList2, final ArrayList arrayList3) {
            AndroidUtilities.runOnUIThread(new Runnable() {
                @Override
                public final void run() {
                    ChatUsersActivity.SearchAdapter.this.lambda$updateSearchResults$4(arrayList, longSparseArray, arrayList2, arrayList3);
                }
            });
        }

        public TLObject getItem(int i) {
            ArrayList globalSearch;
            int size = this.searchAdapterHelper.getGroupSearch().size();
            if (size != 0) {
                int i2 = size + 1;
                if (i2 > i) {
                    if (i == 0) {
                        return null;
                    }
                    globalSearch = this.searchAdapterHelper.getGroupSearch();
                    return (TLObject) globalSearch.get(i - 1);
                }
                i -= i2;
            }
            int size2 = this.searchResult.size();
            if (size2 != 0) {
                int i3 = size2 + 1;
                if (i3 > i) {
                    if (i == 0) {
                        return null;
                    }
                    globalSearch = this.searchResult;
                    return (TLObject) globalSearch.get(i - 1);
                }
                i -= i3;
            }
            int size3 = this.searchAdapterHelper.getGlobalSearch().size();
            if (size3 == 0 || size3 + 1 <= i || i == 0) {
                return null;
            }
            globalSearch = this.searchAdapterHelper.getGlobalSearch();
            return (TLObject) globalSearch.get(i - 1);
        }

        @Override
        public int getItemCount() {
            return this.totalCount;
        }

        @Override
        public int getItemViewType(int i) {
            return (i == this.globalStartRow || i == this.groupStartRow || i == this.contactsStartRow) ? 1 : 0;
        }

        @Override
        public boolean isEnabled(RecyclerView.ViewHolder viewHolder) {
            return viewHolder.getItemViewType() != 1;
        }

        @Override
        public void notifyDataSetChanged() {
            this.totalCount = 0;
            int size = this.searchAdapterHelper.getGroupSearch().size();
            if (size != 0) {
                this.groupStartRow = 0;
                this.totalCount += size + 1;
            } else {
                this.groupStartRow = -1;
            }
            int size2 = this.searchResult.size();
            if (size2 != 0) {
                int i = this.totalCount;
                this.contactsStartRow = i;
                this.totalCount = i + size2 + 1;
            } else {
                this.contactsStartRow = -1;
            }
            int size3 = this.searchAdapterHelper.getGlobalSearch().size();
            if (size3 != 0) {
                int i2 = this.totalCount;
                this.globalStartRow = i2;
                this.totalCount = i2 + size3 + 1;
            } else {
                this.globalStartRow = -1;
            }
            if (ChatUsersActivity.this.searching && ChatUsersActivity.this.listView != null && ChatUsersActivity.this.listView.getAdapter() != ChatUsersActivity.this.searchListViewAdapter) {
                ChatUsersActivity.this.listView.setAnimateEmptyView(true, 0);
                ChatUsersActivity.this.listView.setAdapter(ChatUsersActivity.this.searchListViewAdapter);
                ChatUsersActivity.this.listView.setFastScrollVisible(false);
                ChatUsersActivity.this.listView.setVerticalScrollBarEnabled(true);
            }
            super.notifyDataSetChanged();
        }

        @Override
        public void onBindViewHolder(androidx.recyclerview.widget.RecyclerView.ViewHolder r14, int r15) {
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.ChatUsersActivity.SearchAdapter.onBindViewHolder(androidx.recyclerview.widget.RecyclerView$ViewHolder, int):void");
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            FrameLayout frameLayout;
            if (i != 0) {
                frameLayout = new GraySectionCell(this.mContext);
            } else {
                ManageChatUserCell manageChatUserCell = new ManageChatUserCell(this.mContext, 2, 2, ChatUsersActivity.this.selectType == 0);
                manageChatUserCell.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                manageChatUserCell.setDelegate(new ManageChatUserCell.ManageChatUserCellDelegate() {
                    @Override
                    public final boolean onOptionsButtonCheck(ManageChatUserCell manageChatUserCell2, boolean z) {
                        boolean lambda$onCreateViewHolder$5;
                        lambda$onCreateViewHolder$5 = ChatUsersActivity.SearchAdapter.this.lambda$onCreateViewHolder$5(manageChatUserCell2, z);
                        return lambda$onCreateViewHolder$5;
                    }
                });
                frameLayout = manageChatUserCell;
            }
            return new RecyclerListView.Holder(frameLayout);
        }

        @Override
        public void onViewRecycled(RecyclerView.ViewHolder viewHolder) {
            View view = viewHolder.itemView;
            if (view instanceof ManageChatUserCell) {
                ((ManageChatUserCell) view).recycle();
            }
        }

        public void removeUserId(long j) {
            this.searchAdapterHelper.removeUserId(j);
            Object obj = this.searchResultMap.get(j);
            if (obj != null) {
                this.searchResult.remove(obj);
            }
            notifyDataSetChanged();
        }

        public void searchUsers(final String str) {
            if (this.searchRunnable != null) {
                Utilities.searchQueue.cancelRunnable(this.searchRunnable);
                this.searchRunnable = null;
            }
            this.searchResult.clear();
            this.searchResultMap.clear();
            this.searchResultNames.clear();
            this.searchAdapterHelper.mergeResults(null);
            this.searchAdapterHelper.queryServerSearch(null, ChatUsersActivity.this.type != 0, false, true, false, false, ChatObject.isChannel(ChatUsersActivity.this.currentChat) ? ChatUsersActivity.this.chatId : 0L, false, ChatUsersActivity.this.type, 0);
            notifyDataSetChanged();
            if (TextUtils.isEmpty(str)) {
                return;
            }
            this.searchInProgress = true;
            ChatUsersActivity.this.emptyView.showProgress(true, true);
            DispatchQueue dispatchQueue = Utilities.searchQueue;
            Runnable runnable = new Runnable() {
                @Override
                public final void run() {
                    ChatUsersActivity.SearchAdapter.this.lambda$searchUsers$1(str);
                }
            };
            this.searchRunnable = runnable;
            dispatchQueue.postRunnable(runnable, 300L);
        }
    }

    public ChatUsersActivity(Bundle bundle) {
        super(bundle);
        TLRPC.TL_chatBannedRights tL_chatBannedRights;
        this.defaultBannedRights = new TLRPC.TL_chatBannedRights();
        this.participants = new ArrayList();
        this.bots = new ArrayList();
        this.contacts = new ArrayList();
        this.participantsMap = new LongSparseArray();
        this.botsMap = new LongSparseArray();
        this.contactsMap = new LongSparseArray();
        this.chatId = this.arguments.getLong("chat_id");
        this.type = this.arguments.getInt("type");
        this.needOpenSearch = this.arguments.getBoolean("open_search");
        this.selectType = this.arguments.getInt("selectType");
        TLRPC.Chat chat = getMessagesController().getChat(Long.valueOf(this.chatId));
        this.currentChat = chat;
        boolean z = false;
        if (chat != null && (tL_chatBannedRights = chat.default_banned_rights) != null) {
            TLRPC.TL_chatBannedRights tL_chatBannedRights2 = this.defaultBannedRights;
            tL_chatBannedRights2.view_messages = tL_chatBannedRights.view_messages;
            tL_chatBannedRights2.send_stickers = tL_chatBannedRights.send_stickers;
            boolean z2 = tL_chatBannedRights.send_media;
            tL_chatBannedRights2.send_media = z2;
            tL_chatBannedRights2.embed_links = tL_chatBannedRights.embed_links;
            tL_chatBannedRights2.send_messages = tL_chatBannedRights.send_messages;
            tL_chatBannedRights2.send_games = tL_chatBannedRights.send_games;
            tL_chatBannedRights2.send_inline = tL_chatBannedRights.send_inline;
            tL_chatBannedRights2.send_gifs = tL_chatBannedRights.send_gifs;
            tL_chatBannedRights2.pin_messages = tL_chatBannedRights.pin_messages;
            tL_chatBannedRights2.send_polls = tL_chatBannedRights.send_polls;
            tL_chatBannedRights2.invite_users = tL_chatBannedRights.invite_users;
            tL_chatBannedRights2.manage_topics = tL_chatBannedRights.manage_topics;
            tL_chatBannedRights2.change_info = tL_chatBannedRights.change_info;
            boolean z3 = tL_chatBannedRights.send_photos;
            tL_chatBannedRights2.send_photos = z3;
            boolean z4 = tL_chatBannedRights.send_videos;
            tL_chatBannedRights2.send_videos = z4;
            boolean z5 = tL_chatBannedRights.send_roundvideos;
            tL_chatBannedRights2.send_roundvideos = z5;
            boolean z6 = tL_chatBannedRights.send_audios;
            tL_chatBannedRights2.send_audios = z6;
            boolean z7 = tL_chatBannedRights.send_voices;
            tL_chatBannedRights2.send_voices = z7;
            boolean z8 = tL_chatBannedRights.send_docs;
            tL_chatBannedRights2.send_docs = z8;
            tL_chatBannedRights2.send_plain = tL_chatBannedRights.send_plain;
            if (!z2 && z8 && z7 && z6 && z5 && z4 && z3) {
                tL_chatBannedRights2.send_photos = false;
                tL_chatBannedRights2.send_videos = false;
                tL_chatBannedRights2.send_roundvideos = false;
                tL_chatBannedRights2.send_audios = false;
                tL_chatBannedRights2.send_voices = false;
                tL_chatBannedRights2.send_docs = false;
            }
        }
        this.initialBannedRights = ChatObject.getBannedRightsString(this.defaultBannedRights);
        if (ChatObject.isChannel(this.currentChat) && !this.currentChat.megagroup) {
            z = true;
        }
        this.isChannel = z;
        this.isForum = ChatObject.isForum(this.currentChat);
        TLRPC.Chat chat2 = this.currentChat;
        if (chat2 != null) {
            boolean z9 = chat2.signatures;
            this.signatures = z9;
            this.initialSignatures = z9;
            boolean z10 = chat2.signature_profiles;
            this.profiles = z10;
            this.initialProfiles = z10;
        }
    }

    public boolean checkDiscard() {
        int i;
        String str;
        boolean z;
        if (ChatObject.getBannedRightsString(this.defaultBannedRights).equals(this.initialBannedRights) && this.initialSlowmode == this.selectedSlowmode && !hasNotRestrictBoostersChanges() && (z = this.signatures) == this.initialSignatures) {
            if ((z && this.profiles) == this.initialProfiles) {
                return true;
            }
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(getParentActivity());
        builder.setTitle(LocaleController.getString("UserRestrictionsApplyChanges", R.string.UserRestrictionsApplyChanges));
        if (this.isChannel) {
            i = R.string.ChannelSettingsChangedAlert;
            str = "ChannelSettingsChangedAlert";
        } else {
            i = R.string.GroupSettingsChangedAlert;
            str = "GroupSettingsChangedAlert";
        }
        builder.setMessage(LocaleController.getString(str, i));
        builder.setPositiveButton(LocaleController.getString("ApplyTheme", R.string.ApplyTheme), new AlertDialog.OnButtonClickListener() {
            @Override
            public final void onClick(AlertDialog alertDialog, int i2) {
                ChatUsersActivity.this.lambda$checkDiscard$23(alertDialog, i2);
            }
        });
        builder.setNegativeButton(LocaleController.getString("PassportDiscard", R.string.PassportDiscard), new AlertDialog.OnButtonClickListener() {
            @Override
            public final void onClick(AlertDialog alertDialog, int i2) {
                ChatUsersActivity.this.lambda$checkDiscard$24(alertDialog, i2);
            }
        });
        showDialog(builder.create());
        return false;
    }

    public boolean createMenuForParticipant(final org.telegram.tgnet.TLObject r31, boolean r32, android.view.View r33) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.ChatUsersActivity.createMenuForParticipant(org.telegram.tgnet.TLObject, boolean, android.view.View):boolean");
    }

    public void lambda$createMenuForParticipant$17(long j) {
        TLRPC.TL_channels_editBanned tL_channels_editBanned = new TLRPC.TL_channels_editBanned();
        tL_channels_editBanned.participant = getMessagesController().getInputPeer(j);
        tL_channels_editBanned.channel = getMessagesController().getInputChannel(this.chatId);
        tL_channels_editBanned.banned_rights = new TLRPC.TL_chatBannedRights();
        getConnectionsManager().sendRequest(tL_channels_editBanned, new RequestDelegate() {
            @Override
            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                ChatUsersActivity.this.lambda$deletePeer$21(tLObject, tL_error);
            }
        });
    }

    public String formatSeconds(int i) {
        return i < 60 ? LocaleController.formatPluralString("Seconds", i, new Object[0]) : i < 3600 ? LocaleController.formatPluralString("Minutes", i / 60, new Object[0]) : LocaleController.formatPluralString("Hours", (i / 60) / 60, new Object[0]);
    }

    public java.lang.String formatUserPermissions(org.telegram.tgnet.TLRPC.TL_chatBannedRights r5) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.ChatUsersActivity.formatUserPermissions(org.telegram.tgnet.TLRPC$TL_chatBannedRights):java.lang.String");
    }

    private TLObject getAnyParticipant(long j) {
        int i = 0;
        while (i < 3) {
            TLObject tLObject = (TLObject) (i == 0 ? this.contactsMap : i == 1 ? this.botsMap : this.participantsMap).get(j);
            if (tLObject != null) {
                return tLObject;
            }
            i++;
        }
        return null;
    }

    private int getChannelAdminParticipantType(TLObject tLObject) {
        if ((tLObject instanceof TLRPC.TL_channelParticipantCreator) || (tLObject instanceof TLRPC.TL_channelParticipantSelf)) {
            return 0;
        }
        return ((tLObject instanceof TLRPC.TL_channelParticipantAdmin) || (tLObject instanceof TLRPC.TL_channelParticipant)) ? 1 : 2;
    }

    private int getCurrentSlowmode() {
        TLRPC.ChatFull chatFull = this.info;
        if (chatFull == null) {
            return 0;
        }
        int i = chatFull.slowmode_seconds;
        if (i == 10) {
            return 1;
        }
        if (i == 30) {
            return 2;
        }
        if (i == 60) {
            return 3;
        }
        if (i == 300) {
            return 4;
        }
        if (i == 900) {
            return 5;
        }
        return i == 3600 ? 6 : 0;
    }

    public int getParticipantsCount() {
        ArrayList<TLRPC.ChatParticipant> arrayList;
        TLRPC.ChatFull chatFull = this.info;
        if (chatFull == null) {
            return 0;
        }
        int i = chatFull.participants_count;
        TLRPC.ChatParticipants chatParticipants = chatFull.participants;
        return (chatParticipants == null || (arrayList = chatParticipants.participants) == null) ? i : Math.max(i, arrayList.size());
    }

    public int getSecondsForIndex(int i) {
        if (i == 1) {
            return 10;
        }
        if (i == 2) {
            return 30;
        }
        if (i == 3) {
            return 60;
        }
        if (i == 4) {
            return 300;
        }
        if (i == 5) {
            return 900;
        }
        return i == 6 ? 3600 : 0;
    }

    public int getSendMediaSelectedCount() {
        return getSendMediaSelectedCount(this.defaultBannedRights);
    }

    public static int getSendMediaSelectedCount(TLRPC.TL_chatBannedRights tL_chatBannedRights) {
        int i = !tL_chatBannedRights.send_photos ? 1 : 0;
        if (!tL_chatBannedRights.send_videos) {
            i++;
        }
        if (!tL_chatBannedRights.send_stickers) {
            i++;
        }
        if (!tL_chatBannedRights.send_audios) {
            i++;
        }
        if (!tL_chatBannedRights.send_docs) {
            i++;
        }
        if (!tL_chatBannedRights.send_voices) {
            i++;
        }
        if (!tL_chatBannedRights.send_roundvideos) {
            i++;
        }
        if (!tL_chatBannedRights.embed_links && !tL_chatBannedRights.send_plain) {
            i++;
        }
        return !tL_chatBannedRights.send_polls ? i + 1 : i;
    }

    private boolean hasNotRestrictBoostersChanges() {
        boolean z = this.isEnabledNotRestrictBoosters && isNotRestrictBoostersVisible();
        TLRPC.ChatFull chatFull = this.info;
        if (chatFull == null) {
            return false;
        }
        int i = chatFull.boosts_unrestrict;
        int i2 = this.notRestrictBoosters;
        return i != i2 || (z && i2 == 0) || !(z || i2 == 0);
    }

    public boolean isExpandableSendMediaRow(int i) {
        return i == this.sendMediaPhotosRow || i == this.sendMediaVideosRow || i == this.sendMediaStickerGifsRow || i == this.sendMediaMusicRow || i == this.sendMediaFilesRow || i == this.sendMediaVoiceMessagesRow || i == this.sendMediaVideoMessagesRow || i == this.sendMediaEmbededLinksRow || i == this.sendPollsRow;
    }

    private boolean isNotRestrictBoostersVisible() {
        TLRPC.Chat chat = this.currentChat;
        if (chat.megagroup && !chat.gigagroup && ChatObject.canUserDoAdminAction(chat, 13)) {
            if (this.selectedSlowmode <= 0) {
                TLRPC.TL_chatBannedRights tL_chatBannedRights = this.defaultBannedRights;
                if (tL_chatBannedRights.send_plain || tL_chatBannedRights.send_media || tL_chatBannedRights.send_photos || tL_chatBannedRights.send_videos || tL_chatBannedRights.send_stickers || tL_chatBannedRights.send_audios || tL_chatBannedRights.send_docs || tL_chatBannedRights.send_voices || tL_chatBannedRights.send_roundvideos || tL_chatBannedRights.embed_links || tL_chatBannedRights.send_polls) {
                }
            }
            return true;
        }
        return false;
    }

    public void lambda$checkDiscard$23(AlertDialog alertDialog, int i) {
        processDone();
    }

    public void lambda$checkDiscard$24(AlertDialog alertDialog, int i) {
        lambda$onBackPressed$323();
    }

    public static void lambda$createMenuForParticipant$10(Utilities.Callback callback) {
        callback.run(0);
    }

    public static void lambda$createMenuForParticipant$11(Utilities.Callback callback, AlertDialog alertDialog, int i) {
        callback.run(1);
    }

    public void lambda$createMenuForParticipant$12(TLObject tLObject, TLRPC.User user, final Utilities.Callback callback) {
        if ((tLObject instanceof TLRPC.TL_channelParticipantAdmin) || (tLObject instanceof TLRPC.TL_chatParticipantAdmin)) {
            showDialog(new AlertDialog.Builder(getParentActivity()).setTitle(LocaleController.getString("AppName", R.string.AppName)).setMessage(LocaleController.formatString("AdminWillBeRemoved", R.string.AdminWillBeRemoved, UserObject.getUserName(user))).setPositiveButton(LocaleController.getString("OK", R.string.OK), new AlertDialog.OnButtonClickListener() {
                @Override
                public final void onClick(AlertDialog alertDialog, int i) {
                    ChatUsersActivity.lambda$createMenuForParticipant$11(Utilities.Callback.this, alertDialog, i);
                }
            }).setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), null).create());
        } else {
            callback.run(1);
        }
    }

    public void lambda$createMenuForParticipant$13(TLRPC.User user, long j) {
        getMessagesController().deleteParticipantFromChat(this.chatId, user);
        removeParticipants(j);
        if (this.currentChat == null || user == null || !BulletinFactory.canShowBulletin(this)) {
            return;
        }
        BulletinFactory.createRemoveFromChatBulletin(this, user, this.currentChat.title).show();
    }

    public void lambda$createMenuForParticipant$14(long j, TLRPC.TL_chatBannedRights tL_chatBannedRights, String str, final TLObject tLObject) {
        ChatRightsEditActivity chatRightsEditActivity = new ChatRightsEditActivity(j, this.chatId, null, this.defaultBannedRights, tL_chatBannedRights, str, 1, true, false, null);
        chatRightsEditActivity.setDelegate(new ChatRightsEditActivity.ChatRightsEditActivityDelegate() {
            @Override
            public void didChangeOwner(TLRPC.User user) {
                ChatUsersActivity.this.onOwnerChaged(user);
            }

            @Override
            public void didSetRights(int i, TLRPC.TL_chatAdminRights tL_chatAdminRights, TLRPC.TL_chatBannedRights tL_chatBannedRights2, String str2) {
                TLObject tLObject2 = tLObject;
                if (tLObject2 instanceof TLRPC.ChannelParticipant) {
                    TLRPC.ChannelParticipant channelParticipant = (TLRPC.ChannelParticipant) tLObject2;
                    channelParticipant.admin_rights = tL_chatAdminRights;
                    channelParticipant.banned_rights = tL_chatBannedRights2;
                    channelParticipant.rank = str2;
                    ChatUsersActivity.this.updateParticipantWithRights(channelParticipant, tL_chatAdminRights, tL_chatBannedRights2, 0L, false);
                }
            }
        });
        presentFragment(chatRightsEditActivity);
    }

    public void lambda$createMenuForParticipant$16(long j) {
        lambda$createMenuForParticipant$17(j);
        getMessagesController().addUserToChat(this.chatId, getMessagesController().getUser(Long.valueOf(j)), 0, null, this, null);
    }

    public void lambda$createMenuForParticipant$18(long j, TLRPC.TL_chatAdminRights tL_chatAdminRights, String str, final TLObject tLObject) {
        ChatRightsEditActivity chatRightsEditActivity = new ChatRightsEditActivity(j, this.chatId, tL_chatAdminRights, null, null, str, 0, true, false, null);
        chatRightsEditActivity.setDelegate(new ChatRightsEditActivity.ChatRightsEditActivityDelegate() {
            @Override
            public void didChangeOwner(TLRPC.User user) {
                ChatUsersActivity.this.onOwnerChaged(user);
            }

            @Override
            public void didSetRights(int i, TLRPC.TL_chatAdminRights tL_chatAdminRights2, TLRPC.TL_chatBannedRights tL_chatBannedRights, String str2) {
                TLObject tLObject2 = tLObject;
                if (tLObject2 instanceof TLRPC.ChannelParticipant) {
                    TLRPC.ChannelParticipant channelParticipant = (TLRPC.ChannelParticipant) tLObject2;
                    channelParticipant.admin_rights = tL_chatAdminRights2;
                    channelParticipant.banned_rights = tL_chatBannedRights;
                    channelParticipant.rank = str2;
                    ChatUsersActivity.this.updateParticipantWithRights(channelParticipant, tL_chatAdminRights2, tL_chatBannedRights, 0L, false);
                }
            }
        });
        presentFragment(chatRightsEditActivity);
    }

    public void lambda$createMenuForParticipant$19(long j) {
        getMessagesController().setUserAdminRole(this.chatId, getMessagesController().getUser(Long.valueOf(j)), new TLRPC.TL_chatAdminRights(), "", !this.isChannel, this, false, false, null, null);
        removeParticipants(j);
    }

    public void lambda$createMenuForParticipant$9(long j, int i, TLObject tLObject, TLRPC.TL_chatAdminRights tL_chatAdminRights, TLRPC.TL_chatBannedRights tL_chatBannedRights, String str, boolean z, Integer num) {
        openRightsEdit2(j, i, tLObject, tL_chatAdminRights, tL_chatBannedRights, str, z, num.intValue(), false);
    }

    public void lambda$createView$0(TextCell textCell, boolean z) {
        TLRPC.ChatFull chatFull;
        if (getParentActivity() == null) {
            return;
        }
        this.info.antispam = z;
        textCell.setChecked(z);
        textCell.getCheckBox().setIcon((!ChatObject.canUserDoAdminAction(this.currentChat, 13) || ((chatFull = this.info) != null && chatFull.antispam && getParticipantsCount() < getMessagesController().telegramAntispamGroupSizeMin)) ? R.drawable.permission_locked : 0);
        BulletinFactory.of(this).createSimpleBulletin(R.raw.error, LocaleController.getString("UnknownError", R.string.UnknownError)).show();
    }

    public void lambda$createView$1(final TextCell textCell, final boolean z, TLObject tLObject, TLRPC.TL_error tL_error) {
        if (tLObject != null) {
            getMessagesController().processUpdates((TLRPC.Updates) tLObject, false);
            getMessagesController().putChatFull(this.info);
        }
        if (tL_error != null && !"CHAT_NOT_MODIFIED".equals(tL_error.text)) {
            AndroidUtilities.runOnUIThread(new Runnable() {
                @Override
                public final void run() {
                    ChatUsersActivity.this.lambda$createView$0(textCell, z);
                }
            });
        }
        this.antiSpamToggleLoading = false;
    }

    public void lambda$createView$2(TextCell textCell, boolean z) {
        TLRPC.ChatFull chatFull;
        if (getParentActivity() == null) {
            return;
        }
        this.info.participants_hidden = z;
        textCell.setChecked(z);
        textCell.getCheckBox().setIcon((!ChatObject.canUserDoAdminAction(this.currentChat, 2) || ((chatFull = this.info) != null && chatFull.participants_hidden && getParticipantsCount() < getMessagesController().hiddenMembersGroupSizeMin)) ? R.drawable.permission_locked : 0);
        BulletinFactory.of(this).createSimpleBulletin(R.raw.error, LocaleController.getString("UnknownError", R.string.UnknownError)).show();
    }

    public void lambda$createView$3(final TextCell textCell, final boolean z, TLObject tLObject, TLRPC.TL_error tL_error) {
        if (tLObject != null) {
            getMessagesController().processUpdates((TLRPC.Updates) tLObject, false);
            getMessagesController().putChatFull(this.info);
        }
        if (tL_error != null && !"CHAT_NOT_MODIFIED".equals(tL_error.text)) {
            AndroidUtilities.runOnUIThread(new Runnable() {
                @Override
                public final void run() {
                    ChatUsersActivity.this.lambda$createView$2(textCell, z);
                }
            });
        }
        this.hideMembersToggleLoading = false;
    }

    public void lambda$createView$4(TLRPC.User user, TLObject tLObject, TLRPC.TL_chatAdminRights tL_chatAdminRights, TLRPC.TL_chatBannedRights tL_chatBannedRights, String str, boolean z, AlertDialog alertDialog, int i) {
        openRightsEdit(user.id, tLObject, tL_chatAdminRights, tL_chatBannedRights, str, z, this.selectType == 1 ? 0 : 1, false);
    }

    public void lambda$createView$5(android.view.View r28, int r29, float r30, float r31) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.ChatUsersActivity.lambda$createView$5(android.view.View, int, float, float):void");
    }

    public boolean lambda$createView$6(View view, int i) {
        if (getParentActivity() != null) {
            RecyclerView.Adapter adapter = this.listView.getAdapter();
            ListAdapter listAdapter = this.listViewAdapter;
            if (adapter == listAdapter) {
                return createMenuForParticipant(listAdapter.getItem(i), false, view);
            }
        }
        return false;
    }

    public void lambda$deletePeer$20(TLRPC.Updates updates) {
        getMessagesController().loadFullChat(updates.chats.get(0).id, 0, true);
    }

    public void lambda$deletePeer$21(TLObject tLObject, TLRPC.TL_error tL_error) {
        if (tLObject != null) {
            final TLRPC.Updates updates = (TLRPC.Updates) tLObject;
            getMessagesController().processUpdates(updates, false);
            if (updates.chats.isEmpty()) {
                return;
            }
            AndroidUtilities.runOnUIThread(new Runnable() {
                @Override
                public final void run() {
                    ChatUsersActivity.this.lambda$deletePeer$20(updates);
                }
            }, 1000L);
        }
    }

    public void lambda$didReceivedNotification$22() {
        loadChatParticipants(0, 200);
    }

    public void lambda$getThemeDescriptions$30() {
        RecyclerListView recyclerListView = this.listView;
        if (recyclerListView != null) {
            int childCount = recyclerListView.getChildCount();
            for (int i = 0; i < childCount; i++) {
                View childAt = this.listView.getChildAt(i);
                if (childAt instanceof ManageChatUserCell) {
                    ((ManageChatUserCell) childAt).update(0);
                }
            }
        }
    }

    public void lambda$loadChatParticipants$26(ArrayList arrayList, ArrayList arrayList2) {
        int i;
        ArrayList arrayList3;
        LongSparseArray longSparseArray;
        int i2;
        TLRPC.Chat chat;
        LongSparseArray longSparseArray2;
        boolean z = false;
        int i3 = 0;
        int i4 = 0;
        while (i4 < arrayList.size()) {
            TLRPC.TL_channels_getParticipants tL_channels_getParticipants = (TLRPC.TL_channels_getParticipants) arrayList.get(i4);
            TLRPC.TL_channels_channelParticipants tL_channels_channelParticipants = (TLRPC.TL_channels_channelParticipants) arrayList2.get(i4);
            if (tL_channels_getParticipants == null || tL_channels_channelParticipants == null) {
                i = i4;
            } else {
                if (this.type == 1) {
                    getMessagesController().processLoadedAdminsResponse(this.chatId, tL_channels_channelParticipants);
                }
                getMessagesController().putUsers(tL_channels_channelParticipants.users, z);
                getMessagesController().putChats(tL_channels_channelParticipants.chats, z);
                long clientUserId = getUserConfig().getClientUserId();
                if (this.selectType != 0) {
                    int i5 = 0;
                    while (true) {
                        if (i5 >= tL_channels_channelParticipants.participants.size()) {
                            break;
                        }
                        if (MessageObject.getPeerId(tL_channels_channelParticipants.participants.get(i5).peer) == clientUserId) {
                            tL_channels_channelParticipants.participants.remove(i5);
                            break;
                        }
                        i5++;
                    }
                }
                if (this.type == 2) {
                    this.delayResults--;
                    TLRPC.ChannelParticipantsFilter channelParticipantsFilter = tL_channels_getParticipants.filter;
                    if (channelParticipantsFilter instanceof TLRPC.TL_channelParticipantsContacts) {
                        arrayList3 = this.contacts;
                        longSparseArray = this.contactsMap;
                    } else if (channelParticipantsFilter instanceof TLRPC.TL_channelParticipantsBots) {
                        arrayList3 = this.bots;
                        longSparseArray = this.botsMap;
                    } else {
                        arrayList3 = this.participants;
                        longSparseArray = this.participantsMap;
                    }
                } else {
                    arrayList3 = this.participants;
                    longSparseArray = this.participantsMap;
                    longSparseArray.clear();
                }
                arrayList3.clear();
                arrayList3.addAll(tL_channels_channelParticipants.participants);
                int size = tL_channels_channelParticipants.participants.size();
                int i6 = 0;
                while (i6 < size) {
                    TLRPC.ChannelParticipant channelParticipant = tL_channels_channelParticipants.participants.get(i6);
                    int i7 = i4;
                    if (channelParticipant.user_id == clientUserId) {
                        arrayList3.remove(channelParticipant);
                    } else {
                        longSparseArray.put(MessageObject.getPeerId(channelParticipant.peer), channelParticipant);
                    }
                    i6++;
                    i4 = i7;
                }
                i = i4;
                int size2 = arrayList3.size() + i3;
                if (this.type == 2) {
                    int size3 = this.participants.size();
                    int i8 = 0;
                    while (i8 < size3) {
                        TLObject tLObject = (TLObject) this.participants.get(i8);
                        if (tLObject instanceof TLRPC.ChannelParticipant) {
                            long peerId = MessageObject.getPeerId(((TLRPC.ChannelParticipant) tLObject).peer);
                            if (this.contactsMap.get(peerId) != null || this.botsMap.get(peerId) != null || ((this.selectType == 1 && peerId > 0 && UserObject.isDeleted(getMessagesController().getUser(Long.valueOf(peerId)))) || ((longSparseArray2 = this.ignoredUsers) != null && longSparseArray2.indexOfKey(peerId) >= 0))) {
                                this.participants.remove(i8);
                                this.participantsMap.remove(peerId);
                            }
                            i8++;
                        } else {
                            this.participants.remove(i8);
                        }
                        i8--;
                        size3--;
                        i8++;
                    }
                }
                try {
                    i2 = this.type;
                } catch (Exception e) {
                    FileLog.e(e);
                }
                if ((i2 == 0 || i2 == 3 || i2 == 2) && (chat = this.currentChat) != null && chat.megagroup) {
                    TLRPC.ChatFull chatFull = this.info;
                    if ((chatFull instanceof TLRPC.TL_channelFull) && chatFull.participants_count <= 200) {
                        sortUsers(arrayList3);
                        i3 = size2;
                    }
                }
                if (i2 == 1) {
                    sortAdmins(this.participants);
                }
                i3 = size2;
            }
            i4 = i + 1;
            z = false;
        }
        if (this.type != 2 || this.delayResults <= 0) {
            ListAdapter listAdapter = this.listViewAdapter;
            showItemsAnimated(listAdapter != null ? listAdapter.getItemCount() : 0);
            this.loadingUsers = false;
            this.firstLoaded = true;
            ActionBarMenuItem actionBarMenuItem = this.searchItem;
            if (actionBarMenuItem != null) {
                actionBarMenuItem.setVisibility((this.type != 0 || i3 > 5) ? 0 : 8);
            }
        }
        updateRows();
        if (this.listViewAdapter != null) {
            this.listView.setAnimateEmptyView(this.openTransitionStarted, 0);
            this.listViewAdapter.notifyDataSetChanged();
            if (this.emptyView != null && this.listViewAdapter.getItemCount() == 0 && this.firstLoaded) {
                this.emptyView.showProgress(false, true);
            }
        }
        resumeDelayedFragmentAnimation();
    }

    public static void lambda$loadChatParticipants$27(TLRPC.TL_error tL_error, TLObject tLObject, ArrayList arrayList, int i, AtomicInteger atomicInteger, ArrayList arrayList2, Runnable runnable) {
        if (tL_error == null && (tLObject instanceof TLRPC.TL_channels_channelParticipants)) {
            arrayList.set(i, (TLRPC.TL_channels_channelParticipants) tLObject);
        }
        atomicInteger.getAndIncrement();
        if (atomicInteger.get() == arrayList2.size()) {
            runnable.run();
        }
    }

    public static void lambda$loadChatParticipants$28(final ArrayList arrayList, final int i, final AtomicInteger atomicInteger, final ArrayList arrayList2, final Runnable runnable, final TLObject tLObject, final TLRPC.TL_error tL_error) {
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                ChatUsersActivity.lambda$loadChatParticipants$27(TLRPC.TL_error.this, tLObject, arrayList, i, atomicInteger, arrayList2, runnable);
            }
        });
    }

    public int lambda$onOwnerChaged$8(TLObject tLObject, TLObject tLObject2) {
        int channelAdminParticipantType = getChannelAdminParticipantType(tLObject);
        int channelAdminParticipantType2 = getChannelAdminParticipantType(tLObject2);
        if (channelAdminParticipantType > channelAdminParticipantType2) {
            return 1;
        }
        return channelAdminParticipantType < channelAdminParticipantType2 ? -1 : 0;
    }

    public void lambda$processDone$25(long j) {
        if (j != 0) {
            this.chatId = j;
            this.currentChat = MessagesController.getInstance(this.currentAccount).getChat(Long.valueOf(j));
            processDone();
        }
    }

    public int lambda$sortAdmins$7(TLObject tLObject, TLObject tLObject2) {
        int channelAdminParticipantType = getChannelAdminParticipantType(tLObject);
        int channelAdminParticipantType2 = getChannelAdminParticipantType(tLObject2);
        if (channelAdminParticipantType > channelAdminParticipantType2) {
            return 1;
        }
        if (channelAdminParticipantType < channelAdminParticipantType2) {
            return -1;
        }
        if ((tLObject instanceof TLRPC.ChannelParticipant) && (tLObject2 instanceof TLRPC.ChannelParticipant)) {
            return (int) (MessageObject.getPeerId(((TLRPC.ChannelParticipant) tLObject).peer) - MessageObject.getPeerId(((TLRPC.ChannelParticipant) tLObject2).peer));
        }
        return 0;
    }

    public int lambda$sortUsers$29(int i, TLObject tLObject, TLObject tLObject2) {
        int i2;
        TLRPC.UserStatus userStatus;
        TLRPC.UserStatus userStatus2;
        TLRPC.ChannelParticipant channelParticipant = (TLRPC.ChannelParticipant) tLObject;
        TLRPC.ChannelParticipant channelParticipant2 = (TLRPC.ChannelParticipant) tLObject2;
        long peerId = MessageObject.getPeerId(channelParticipant.peer);
        long peerId2 = MessageObject.getPeerId(channelParticipant2.peer);
        int i3 = -100;
        if (peerId > 0) {
            TLRPC.User user = getMessagesController().getUser(Long.valueOf(MessageObject.getPeerId(channelParticipant.peer)));
            i2 = (user == null || (userStatus2 = user.status) == null) ? 0 : user.self ? i + 50000 : userStatus2.expires;
        } else {
            i2 = -100;
        }
        if (peerId2 > 0) {
            TLRPC.User user2 = getMessagesController().getUser(Long.valueOf(MessageObject.getPeerId(channelParticipant2.peer)));
            i3 = (user2 == null || (userStatus = user2.status) == null) ? 0 : user2.self ? i + 50000 : userStatus.expires;
        }
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

    public void loadChatParticipants(int i, int i2) {
        if (this.loadingUsers) {
            return;
        }
        this.contactsEndReached = false;
        this.botsEndReached = false;
        loadChatParticipants(i, i2, true);
    }

    private void loadChatParticipants(int i, int i2, boolean z) {
        LongSparseArray longSparseArray;
        LongSparseArray longSparseArray2;
        int i3 = 0;
        if (ChatObject.isChannel(this.currentChat)) {
            this.loadingUsers = true;
            StickerEmptyView stickerEmptyView = this.emptyView;
            if (stickerEmptyView != null) {
                stickerEmptyView.showProgress(true, false);
            }
            ListAdapter listAdapter = this.listViewAdapter;
            if (listAdapter != null) {
                listAdapter.notifyDataSetChanged();
            }
            final ArrayList loadChatParticipantsRequests = loadChatParticipantsRequests(i, i2, z);
            final ArrayList arrayList = new ArrayList();
            final Runnable runnable = new Runnable() {
                @Override
                public final void run() {
                    ChatUsersActivity.this.lambda$loadChatParticipants$26(loadChatParticipantsRequests, arrayList);
                }
            };
            final AtomicInteger atomicInteger = new AtomicInteger(0);
            while (i3 < loadChatParticipantsRequests.size()) {
                arrayList.add(null);
                final int i4 = i3;
                getConnectionsManager().bindRequestToGuid(getConnectionsManager().sendRequest((TLObject) loadChatParticipantsRequests.get(i3), new RequestDelegate() {
                    @Override
                    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                        ChatUsersActivity.lambda$loadChatParticipants$28(arrayList, i4, atomicInteger, loadChatParticipantsRequests, runnable, tLObject, tL_error);
                    }
                }), this.classGuid);
                i3++;
            }
            return;
        }
        this.loadingUsers = false;
        this.participants.clear();
        this.bots.clear();
        this.contacts.clear();
        this.participantsMap.clear();
        this.contactsMap.clear();
        this.botsMap.clear();
        int i5 = this.type;
        if (i5 == 1) {
            TLRPC.ChatFull chatFull = this.info;
            if (chatFull != null) {
                int size = chatFull.participants.participants.size();
                while (i3 < size) {
                    TLRPC.ChatParticipant chatParticipant = this.info.participants.participants.get(i3);
                    if ((chatParticipant instanceof TLRPC.TL_chatParticipantCreator) || (chatParticipant instanceof TLRPC.TL_chatParticipantAdmin)) {
                        this.participants.add(chatParticipant);
                    }
                    this.participantsMap.put(chatParticipant.user_id, chatParticipant);
                    i3++;
                }
            }
        } else if (i5 == 2 && this.info != null) {
            long j = getUserConfig().clientUserId;
            int size2 = this.info.participants.participants.size();
            while (i3 < size2) {
                TLRPC.ChatParticipant chatParticipant2 = this.info.participants.participants.get(i3);
                if ((this.selectType == 0 || chatParticipant2.user_id != j) && ((longSparseArray = this.ignoredUsers) == null || longSparseArray.indexOfKey(chatParticipant2.user_id) < 0)) {
                    if (this.selectType == 1) {
                        if (!getContactsController().isContact(chatParticipant2.user_id)) {
                            if (UserObject.isDeleted(getMessagesController().getUser(Long.valueOf(chatParticipant2.user_id)))) {
                            }
                            this.participants.add(chatParticipant2);
                            longSparseArray2 = this.participantsMap;
                        }
                        this.contacts.add(chatParticipant2);
                        longSparseArray2 = this.contactsMap;
                    } else {
                        if (!getContactsController().isContact(chatParticipant2.user_id)) {
                            TLRPC.User user = getMessagesController().getUser(Long.valueOf(chatParticipant2.user_id));
                            if (user != null && user.bot) {
                                this.bots.add(chatParticipant2);
                                longSparseArray2 = this.botsMap;
                            }
                            this.participants.add(chatParticipant2);
                            longSparseArray2 = this.participantsMap;
                        }
                        this.contacts.add(chatParticipant2);
                        longSparseArray2 = this.contactsMap;
                    }
                    longSparseArray2.put(chatParticipant2.user_id, chatParticipant2);
                }
                i3++;
            }
        }
        ListAdapter listAdapter2 = this.listViewAdapter;
        if (listAdapter2 != null) {
            listAdapter2.notifyDataSetChanged();
        }
        updateRows();
        ListAdapter listAdapter3 = this.listViewAdapter;
        if (listAdapter3 != null) {
            listAdapter3.notifyDataSetChanged();
        }
    }

    private ArrayList loadChatParticipantsRequests(int i, int i2, boolean z) {
        TLRPC.ChannelParticipantsFilter tL_channelParticipantsBanned;
        TLRPC.TL_channelParticipantsContacts tL_channelParticipantsContacts;
        TLRPC.Chat chat;
        TLRPC.TL_channels_getParticipants tL_channels_getParticipants = new TLRPC.TL_channels_getParticipants();
        ArrayList arrayList = new ArrayList();
        arrayList.add(tL_channels_getParticipants);
        tL_channels_getParticipants.channel = getMessagesController().getInputChannel(this.chatId);
        int i3 = this.type;
        if (i3 == 0) {
            tL_channelParticipantsBanned = new TLRPC.TL_channelParticipantsKicked();
        } else {
            if (i3 != 1) {
                if (i3 == 2) {
                    TLRPC.ChatFull chatFull = this.info;
                    if (chatFull != null && chatFull.participants_count <= 200 && (chat = this.currentChat) != null && chat.megagroup) {
                        tL_channelParticipantsBanned = new TLRPC.TL_channelParticipantsRecent();
                    } else if (this.selectType == 1) {
                        if (this.contactsEndReached) {
                            tL_channelParticipantsBanned = new TLRPC.TL_channelParticipantsRecent();
                        } else {
                            this.delayResults = 2;
                            tL_channelParticipantsContacts = new TLRPC.TL_channelParticipantsContacts();
                            tL_channels_getParticipants.filter = tL_channelParticipantsContacts;
                            this.contactsEndReached = true;
                            arrayList.addAll(loadChatParticipantsRequests(0, 200, false));
                        }
                    } else if (!this.contactsEndReached) {
                        this.delayResults = 3;
                        tL_channelParticipantsContacts = new TLRPC.TL_channelParticipantsContacts();
                        tL_channels_getParticipants.filter = tL_channelParticipantsContacts;
                        this.contactsEndReached = true;
                        arrayList.addAll(loadChatParticipantsRequests(0, 200, false));
                    } else if (this.botsEndReached) {
                        tL_channelParticipantsBanned = new TLRPC.TL_channelParticipantsRecent();
                    } else {
                        tL_channels_getParticipants.filter = new TLRPC.TL_channelParticipantsBots();
                        this.botsEndReached = true;
                        arrayList.addAll(loadChatParticipantsRequests(0, 200, false));
                    }
                } else if (i3 == 3) {
                    tL_channelParticipantsBanned = new TLRPC.TL_channelParticipantsBanned();
                }
                tL_channels_getParticipants.filter.q = "";
                tL_channels_getParticipants.offset = i;
                tL_channels_getParticipants.limit = i2;
                return arrayList;
            }
            tL_channelParticipantsBanned = new TLRPC.TL_channelParticipantsAdmins();
        }
        tL_channels_getParticipants.filter = tL_channelParticipantsBanned;
        tL_channels_getParticipants.filter.q = "";
        tL_channels_getParticipants.offset = i;
        tL_channels_getParticipants.limit = i2;
        return arrayList;
    }

    public void onOwnerChaged(TLRPC.User user) {
        LongSparseArray longSparseArray;
        ArrayList arrayList;
        boolean z;
        this.undoView.showWithAction(-this.chatId, this.isChannel ? 9 : 10, user);
        this.currentChat.creator = false;
        boolean z2 = false;
        for (int i = 0; i < 3; i++) {
            boolean z3 = true;
            if (i == 0) {
                longSparseArray = this.contactsMap;
                arrayList = this.contacts;
            } else if (i == 1) {
                longSparseArray = this.botsMap;
                arrayList = this.bots;
            } else {
                longSparseArray = this.participantsMap;
                arrayList = this.participants;
            }
            TLObject tLObject = (TLObject) longSparseArray.get(user.id);
            if (tLObject instanceof TLRPC.ChannelParticipant) {
                TLRPC.TL_channelParticipantCreator tL_channelParticipantCreator = new TLRPC.TL_channelParticipantCreator();
                TLRPC.TL_peerUser tL_peerUser = new TLRPC.TL_peerUser();
                tL_channelParticipantCreator.peer = tL_peerUser;
                long j = user.id;
                tL_peerUser.user_id = j;
                longSparseArray.put(j, tL_channelParticipantCreator);
                int indexOf = arrayList.indexOf(tLObject);
                if (indexOf >= 0) {
                    arrayList.set(indexOf, tL_channelParticipantCreator);
                }
                z2 = true;
                z = true;
            } else {
                z = false;
            }
            long clientUserId = getUserConfig().getClientUserId();
            TLObject tLObject2 = (TLObject) longSparseArray.get(clientUserId);
            if (tLObject2 instanceof TLRPC.ChannelParticipant) {
                TLRPC.TL_channelParticipantAdmin tL_channelParticipantAdmin = new TLRPC.TL_channelParticipantAdmin();
                TLRPC.TL_peerUser tL_peerUser2 = new TLRPC.TL_peerUser();
                tL_channelParticipantAdmin.peer = tL_peerUser2;
                tL_peerUser2.user_id = clientUserId;
                tL_channelParticipantAdmin.self = true;
                tL_channelParticipantAdmin.inviter_id = clientUserId;
                tL_channelParticipantAdmin.promoted_by = clientUserId;
                tL_channelParticipantAdmin.date = (int) (System.currentTimeMillis() / 1000);
                TLRPC.TL_chatAdminRights tL_chatAdminRights = new TLRPC.TL_chatAdminRights();
                tL_channelParticipantAdmin.admin_rights = tL_chatAdminRights;
                tL_chatAdminRights.add_admins = true;
                tL_chatAdminRights.pin_messages = true;
                tL_chatAdminRights.manage_topics = true;
                tL_chatAdminRights.invite_users = true;
                tL_chatAdminRights.ban_users = true;
                tL_chatAdminRights.delete_messages = true;
                tL_chatAdminRights.edit_messages = true;
                tL_chatAdminRights.post_messages = true;
                tL_chatAdminRights.change_info = true;
                if (!this.isChannel) {
                    tL_chatAdminRights.manage_call = true;
                }
                longSparseArray.put(clientUserId, tL_channelParticipantAdmin);
                int indexOf2 = arrayList.indexOf(tLObject2);
                if (indexOf2 >= 0) {
                    arrayList.set(indexOf2, tL_channelParticipantAdmin);
                }
            } else {
                z3 = z;
            }
            if (z3) {
                Collections.sort(arrayList, new Comparator() {
                    @Override
                    public final int compare(Object obj, Object obj2) {
                        int lambda$onOwnerChaged$8;
                        lambda$onOwnerChaged$8 = ChatUsersActivity.this.lambda$onOwnerChaged$8((TLObject) obj, (TLObject) obj2);
                        return lambda$onOwnerChaged$8;
                    }
                });
            }
        }
        if (!z2) {
            TLRPC.TL_channelParticipantCreator tL_channelParticipantCreator2 = new TLRPC.TL_channelParticipantCreator();
            TLRPC.TL_peerUser tL_peerUser3 = new TLRPC.TL_peerUser();
            tL_channelParticipantCreator2.peer = tL_peerUser3;
            long j2 = user.id;
            tL_peerUser3.user_id = j2;
            this.participantsMap.put(j2, tL_channelParticipantCreator2);
            this.participants.add(tL_channelParticipantCreator2);
            sortAdmins(this.participants);
            updateRows();
        }
        this.listViewAdapter.notifyDataSetChanged();
        ChatUsersActivityDelegate chatUsersActivityDelegate = this.delegate;
        if (chatUsersActivityDelegate != null) {
            chatUsersActivityDelegate.didChangeOwner(user);
        }
    }

    public void openRightsEdit(final long j, final TLObject tLObject, TLRPC.TL_chatAdminRights tL_chatAdminRights, TLRPC.TL_chatBannedRights tL_chatBannedRights, String str, boolean z, int i, final boolean z2) {
        ChatRightsEditActivity chatRightsEditActivity = new ChatRightsEditActivity(j, this.chatId, tL_chatAdminRights, this.defaultBannedRights, tL_chatBannedRights, str, i, z, tLObject == null, null);
        chatRightsEditActivity.setDelegate(new ChatRightsEditActivity.ChatRightsEditActivityDelegate() {
            @Override
            public void didChangeOwner(TLRPC.User user) {
                ChatUsersActivity.this.onOwnerChaged(user);
            }

            @Override
            public void didSetRights(int i2, TLRPC.TL_chatAdminRights tL_chatAdminRights2, TLRPC.TL_chatBannedRights tL_chatBannedRights2, String str2) {
                TLObject tLObject2 = tLObject;
                if (tLObject2 instanceof TLRPC.ChannelParticipant) {
                    TLRPC.ChannelParticipant channelParticipant = (TLRPC.ChannelParticipant) tLObject2;
                    channelParticipant.admin_rights = tL_chatAdminRights2;
                    channelParticipant.banned_rights = tL_chatBannedRights2;
                    channelParticipant.rank = str2;
                }
                if (ChatUsersActivity.this.delegate != null && i2 == 1) {
                    ChatUsersActivity.this.delegate.didSelectUser(j);
                } else if (ChatUsersActivity.this.delegate != null) {
                    ChatUsersActivity.this.delegate.didAddParticipantToList(j, tLObject);
                }
                if (z2) {
                    ChatUsersActivity.this.removeSelfFromStack();
                }
            }
        });
        presentFragment(chatRightsEditActivity, z2);
    }

    private void openRightsEdit2(final long j, final int i, TLObject tLObject, TLRPC.TL_chatAdminRights tL_chatAdminRights, TLRPC.TL_chatBannedRights tL_chatBannedRights, String str, boolean z, final int i2, boolean z2) {
        final boolean[] zArr = new boolean[1];
        boolean z3 = (tLObject instanceof TLRPC.TL_channelParticipantAdmin) || (tLObject instanceof TLRPC.TL_chatParticipantAdmin);
        ChatRightsEditActivity chatRightsEditActivity = new ChatRightsEditActivity(j, this.chatId, tL_chatAdminRights, this.defaultBannedRights, tL_chatBannedRights, str, i2, true, false, null) {
            @Override
            public void onTransitionAnimationEnd(boolean z4, boolean z5) {
                ChatUsersActivity chatUsersActivity;
                String str2;
                if (!z4 && z5 && zArr[0] && BulletinFactory.canShowBulletin(ChatUsersActivity.this)) {
                    long j2 = j;
                    MessagesController messagesController = getMessagesController();
                    long j3 = j;
                    if (j2 > 0) {
                        TLRPC.User user = messagesController.getUser(Long.valueOf(j3));
                        if (user == null) {
                            return;
                        }
                        chatUsersActivity = ChatUsersActivity.this;
                        str2 = user.first_name;
                    } else {
                        TLRPC.Chat chat = messagesController.getChat(Long.valueOf(-j3));
                        if (chat == null) {
                            return;
                        }
                        chatUsersActivity = ChatUsersActivity.this;
                        str2 = chat.title;
                    }
                    BulletinFactory.createPromoteToAdminBulletin(chatUsersActivity, str2).show();
                }
            }
        };
        final boolean z4 = z3;
        chatRightsEditActivity.setDelegate(new ChatRightsEditActivity.ChatRightsEditActivityDelegate() {
            @Override
            public void didChangeOwner(TLRPC.User user) {
                ChatUsersActivity.this.onOwnerChaged(user);
            }

            @Override
            public void didSetRights(int i3, TLRPC.TL_chatAdminRights tL_chatAdminRights2, TLRPC.TL_chatBannedRights tL_chatBannedRights2, String str2) {
                int i4 = i2;
                if (i4 != 0) {
                    if (i4 == 1 && i3 == 0) {
                        ChatUsersActivity.this.removeParticipants(j);
                        return;
                    }
                    return;
                }
                int i5 = 0;
                while (true) {
                    if (i5 >= ChatUsersActivity.this.participants.size()) {
                        break;
                    }
                    TLObject tLObject2 = (TLObject) ChatUsersActivity.this.participants.get(i5);
                    if (tLObject2 instanceof TLRPC.ChannelParticipant) {
                        if (MessageObject.getPeerId(((TLRPC.ChannelParticipant) tLObject2).peer) == j) {
                            TLRPC.ChannelParticipant tL_channelParticipantAdmin = i3 == 1 ? new TLRPC.TL_channelParticipantAdmin() : new TLRPC.TL_channelParticipant();
                            tL_channelParticipantAdmin.admin_rights = tL_chatAdminRights2;
                            tL_channelParticipantAdmin.banned_rights = tL_chatBannedRights2;
                            tL_channelParticipantAdmin.inviter_id = ChatUsersActivity.this.getUserConfig().getClientUserId();
                            if (j > 0) {
                                TLRPC.TL_peerUser tL_peerUser = new TLRPC.TL_peerUser();
                                tL_channelParticipantAdmin.peer = tL_peerUser;
                                tL_peerUser.user_id = j;
                            } else {
                                TLRPC.TL_peerChannel tL_peerChannel = new TLRPC.TL_peerChannel();
                                tL_channelParticipantAdmin.peer = tL_peerChannel;
                                tL_peerChannel.channel_id = -j;
                            }
                            tL_channelParticipantAdmin.date = i;
                            tL_channelParticipantAdmin.flags |= 4;
                            tL_channelParticipantAdmin.rank = str2;
                            ChatUsersActivity.this.participants.set(i5, tL_channelParticipantAdmin);
                        }
                    } else if (tLObject2 instanceof TLRPC.ChatParticipant) {
                        TLRPC.ChatParticipant chatParticipant = (TLRPC.ChatParticipant) tLObject2;
                        TLRPC.ChatParticipant tL_chatParticipantAdmin = i3 == 1 ? new TLRPC.TL_chatParticipantAdmin() : new TLRPC.TL_chatParticipant();
                        tL_chatParticipantAdmin.user_id = chatParticipant.user_id;
                        tL_chatParticipantAdmin.date = chatParticipant.date;
                        tL_chatParticipantAdmin.inviter_id = chatParticipant.inviter_id;
                        int indexOf = ChatUsersActivity.this.info.participants.participants.indexOf(chatParticipant);
                        if (indexOf >= 0) {
                            ChatUsersActivity.this.info.participants.participants.set(indexOf, tL_chatParticipantAdmin);
                        }
                        ChatUsersActivity.this.loadChatParticipants(0, 200);
                    }
                    i5++;
                }
                if (i3 != 1 || z4) {
                    return;
                }
                zArr[0] = true;
            }
        });
        presentFragment(chatRightsEditActivity);
    }

    public void processDone() {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.ChatUsersActivity.processDone():void");
    }

    private void removeParticipant(long j) {
        if (ChatObject.isChannel(this.currentChat)) {
            getMessagesController().deleteParticipantFromChat(this.chatId, getMessagesController().getUser(Long.valueOf(j)));
            ChatUsersActivityDelegate chatUsersActivityDelegate = this.delegate;
            if (chatUsersActivityDelegate != null) {
                chatUsersActivityDelegate.didKickParticipant(j);
            }
            lambda$onBackPressed$323();
        }
    }

    public void removeParticipants(long j) {
        LongSparseArray longSparseArray;
        ArrayList arrayList;
        TLRPC.ChatFull chatFull;
        DiffCallback saveState = saveState();
        boolean z = false;
        for (int i = 0; i < 3; i++) {
            if (i == 0) {
                longSparseArray = this.contactsMap;
                arrayList = this.contacts;
            } else if (i == 1) {
                longSparseArray = this.botsMap;
                arrayList = this.bots;
            } else {
                longSparseArray = this.participantsMap;
                arrayList = this.participants;
            }
            TLObject tLObject = (TLObject) longSparseArray.get(j);
            if (tLObject != null) {
                longSparseArray.remove(j);
                arrayList.remove(tLObject);
                if (this.type == 0 && (chatFull = this.info) != null) {
                    chatFull.kicked_count--;
                }
                z = true;
            }
        }
        if (z) {
            updateListAnimated(saveState);
        }
        RecyclerView.Adapter adapter = this.listView.getAdapter();
        SearchAdapter searchAdapter = this.searchListViewAdapter;
        if (adapter == searchAdapter) {
            searchAdapter.removeUserId(j);
        }
    }

    private void setBannedRights(TLRPC.TL_chatBannedRights tL_chatBannedRights) {
        if (tL_chatBannedRights != null) {
            this.defaultBannedRights = tL_chatBannedRights;
        }
    }

    public void setSendMediaEnabled(boolean z) {
        TLRPC.TL_chatBannedRights tL_chatBannedRights = this.defaultBannedRights;
        boolean z2 = !z;
        tL_chatBannedRights.send_media = z2;
        tL_chatBannedRights.send_gifs = z2;
        tL_chatBannedRights.send_inline = z2;
        tL_chatBannedRights.send_games = z2;
        tL_chatBannedRights.send_photos = z2;
        tL_chatBannedRights.send_videos = z2;
        tL_chatBannedRights.send_stickers = z2;
        tL_chatBannedRights.send_audios = z2;
        tL_chatBannedRights.send_docs = z2;
        tL_chatBannedRights.send_voices = z2;
        tL_chatBannedRights.send_roundvideos = z2;
        tL_chatBannedRights.embed_links = z2;
        tL_chatBannedRights.send_polls = z2;
        AndroidUtilities.updateVisibleRows(this.listView);
        DiffCallback saveState = saveState();
        updateRows();
        updateListAnimated(saveState);
    }

    public void showItemsAnimated(final int i) {
        if (this.isPaused || !this.openTransitionStarted) {
            return;
        }
        if (this.listView.getAdapter() == this.listViewAdapter && this.firstLoaded) {
            return;
        }
        final View view = null;
        for (int i2 = 0; i2 < this.listView.getChildCount(); i2++) {
            View childAt = this.listView.getChildAt(i2);
            if (childAt instanceof FlickerLoadingView) {
                view = childAt;
            }
        }
        if (view != null) {
            this.listView.removeView(view);
            i--;
        }
        this.listView.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                ChatUsersActivity.this.listView.getViewTreeObserver().removeOnPreDrawListener(this);
                int childCount = ChatUsersActivity.this.listView.getChildCount();
                AnimatorSet animatorSet = new AnimatorSet();
                for (int i3 = 0; i3 < childCount; i3++) {
                    View childAt2 = ChatUsersActivity.this.listView.getChildAt(i3);
                    if (childAt2 != view && ChatUsersActivity.this.listView.getChildAdapterPosition(childAt2) >= i) {
                        childAt2.setAlpha(0.0f);
                        int min = (int) ((Math.min(ChatUsersActivity.this.listView.getMeasuredHeight(), Math.max(0, childAt2.getTop())) / ChatUsersActivity.this.listView.getMeasuredHeight()) * 100.0f);
                        ObjectAnimator ofFloat = ObjectAnimator.ofFloat(childAt2, (Property<View, Float>) View.ALPHA, 0.0f, 1.0f);
                        ofFloat.setStartDelay(min);
                        ofFloat.setDuration(200L);
                        animatorSet.playTogether(ofFloat);
                    }
                }
                View view2 = view;
                if (view2 != null && view2.getParent() == null) {
                    ChatUsersActivity.this.listView.addView(view);
                    final RecyclerView.LayoutManager layoutManager = ChatUsersActivity.this.listView.getLayoutManager();
                    if (layoutManager != null) {
                        layoutManager.ignoreView(view);
                        View view3 = view;
                        ObjectAnimator ofFloat2 = ObjectAnimator.ofFloat(view3, (Property<View, Float>) View.ALPHA, view3.getAlpha(), 0.0f);
                        ofFloat2.addListener(new AnimatorListenerAdapter() {
                            @Override
                            public void onAnimationEnd(Animator animator) {
                                view.setAlpha(1.0f);
                                layoutManager.stopIgnoringView(view);
                                ChatUsersActivity.this.listView.removeView(view);
                            }
                        });
                        ofFloat2.start();
                    }
                }
                animatorSet.start();
                return true;
            }
        });
    }

    public void sortAdmins(ArrayList arrayList) {
        Collections.sort(arrayList, new Comparator() {
            @Override
            public final int compare(Object obj, Object obj2) {
                int lambda$sortAdmins$7;
                lambda$sortAdmins$7 = ChatUsersActivity.this.lambda$sortAdmins$7((TLObject) obj, (TLObject) obj2);
                return lambda$sortAdmins$7;
            }
        });
    }

    public void sortUsers(ArrayList arrayList) {
        final int currentTime = getConnectionsManager().getCurrentTime();
        Collections.sort(arrayList, new Comparator() {
            @Override
            public final int compare(Object obj, Object obj2) {
                int lambda$sortUsers$29;
                lambda$sortUsers$29 = ChatUsersActivity.this.lambda$sortUsers$29(currentTime, (TLObject) obj, (TLObject) obj2);
                return lambda$sortUsers$29;
            }
        });
    }

    public void updateParticipantWithRights(TLRPC.ChannelParticipant channelParticipant, TLRPC.TL_chatAdminRights tL_chatAdminRights, TLRPC.TL_chatBannedRights tL_chatBannedRights, long j, boolean z) {
        ChatUsersActivityDelegate chatUsersActivityDelegate;
        int i = 0;
        boolean z2 = false;
        while (i < 3) {
            TLObject tLObject = (TLObject) (i == 0 ? this.contactsMap : i == 1 ? this.botsMap : this.participantsMap).get(MessageObject.getPeerId(channelParticipant.peer));
            if (tLObject instanceof TLRPC.ChannelParticipant) {
                channelParticipant = (TLRPC.ChannelParticipant) tLObject;
                channelParticipant.admin_rights = tL_chatAdminRights;
                channelParticipant.banned_rights = tL_chatBannedRights;
                if (z) {
                    channelParticipant.promoted_by = getUserConfig().getClientUserId();
                }
            }
            if (z && tLObject != null && !z2 && (chatUsersActivityDelegate = this.delegate) != null) {
                chatUsersActivityDelegate.didAddParticipantToList(j, tLObject);
                z2 = true;
            }
            i++;
        }
    }

    public void updateRows() {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.ChatUsersActivity.updateRows():void");
    }

    @Override
    public boolean canBeginSlide() {
        return checkDiscard();
    }

    @Override
    public android.view.View createView(android.content.Context r12) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.ChatUsersActivity.createView(android.content.Context):android.view.View");
    }

    @Override
    public void didReceivedNotification(int i, int i2, Object... objArr) {
        if (i != NotificationCenter.chatInfoDidLoad) {
            if (i == NotificationCenter.dialogDeleted && ((Long) objArr[0]).longValue() == (-this.chatId)) {
                INavigationLayout iNavigationLayout = this.parentLayout;
                if (iNavigationLayout == null || iNavigationLayout.getLastFragment() != this) {
                    removeSelfFromStack();
                    return;
                } else {
                    lambda$onBackPressed$323();
                    return;
                }
            }
            return;
        }
        TLRPC.ChatFull chatFull = (TLRPC.ChatFull) objArr[0];
        boolean booleanValue = ((Boolean) objArr[2]).booleanValue();
        if (chatFull.id == this.chatId) {
            if (booleanValue && ChatObject.isChannel(this.currentChat)) {
                return;
            }
            boolean z = this.info != null;
            this.info = chatFull;
            if (!z) {
                int currentSlowmode = getCurrentSlowmode();
                this.initialSlowmode = currentSlowmode;
                this.selectedSlowmode = currentSlowmode;
                int i3 = this.info.boosts_unrestrict;
                this.isEnabledNotRestrictBoosters = i3 > 0;
                this.notRestrictBoosters = i3;
            }
            AndroidUtilities.runOnUIThread(new Runnable() {
                @Override
                public final void run() {
                    ChatUsersActivity.this.lambda$didReceivedNotification$22();
                }
            });
        }
    }

    @Override
    public ArrayList getThemeDescriptions() {
        ArrayList arrayList = new ArrayList();
        ThemeDescription.ThemeDescriptionDelegate themeDescriptionDelegate = new ThemeDescription.ThemeDescriptionDelegate() {
            @Override
            public final void didSetColor() {
                ChatUsersActivity.this.lambda$getThemeDescriptions$30();
            }

            @Override
            public void onAnimationProgress(float f) {
                ThemeDescription.ThemeDescriptionDelegate.CC.$default$onAnimationProgress(this, f);
            }
        };
        arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_CELLBACKGROUNDCOLOR, new Class[]{HeaderCell.class, ManageChatUserCell.class, ManageChatTextCell.class, TextCheckCell2.class, TextSettingsCell.class, SlideChooseView.class}, null, null, null, Theme.key_windowBackgroundWhite));
        arrayList.add(new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, Theme.key_windowBackgroundGray));
        ActionBar actionBar = this.actionBar;
        int i = ThemeDescription.FLAG_BACKGROUND;
        int i2 = Theme.key_actionBarDefault;
        arrayList.add(new ThemeDescription(actionBar, i, null, null, null, null, i2));
        arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_LISTGLOWCOLOR, null, null, null, null, i2));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, null, null, null, null, Theme.key_actionBarDefaultIcon));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, null, null, null, null, Theme.key_actionBarDefaultTitle));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, null, null, null, null, Theme.key_actionBarDefaultSelector));
        arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_SELECTOR, null, null, null, null, Theme.key_listSelector));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{View.class}, Theme.dividerPaint, null, null, Theme.key_divider));
        int i3 = Theme.key_windowBackgroundGrayShadow;
        arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{TextInfoPrivacyCell.class}, null, null, null, i3));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{TextInfoPrivacyCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteGrayText4));
        arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{ShadowSectionCell.class}, null, null, null, i3));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{HeaderCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteBlueHeader));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{GraySectionCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_graySectionText));
        arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_CELLBACKGROUNDCOLOR, new Class[]{GraySectionCell.class}, null, null, null, Theme.key_graySection));
        int i4 = Theme.key_windowBackgroundWhiteBlackText;
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{TextSettingsCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, i4));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{TextSettingsCell.class}, new String[]{"valueTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteValueText));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{TextCheckCell2.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, i4));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{TextCheckCell2.class}, new String[]{"valueTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteGrayText2));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{TextCheckCell2.class}, new String[]{"checkBox"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_switch2Track));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{TextCheckCell2.class}, new String[]{"checkBox"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_switch2TrackChecked));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{ManageChatUserCell.class}, new String[]{"nameTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, i4));
        int i5 = Theme.key_windowBackgroundWhiteGrayText;
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{ManageChatUserCell.class}, new String[]{"statusColor"}, (Paint[]) null, (Drawable[]) null, themeDescriptionDelegate, i5));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{ManageChatUserCell.class}, new String[]{"statusOnlineColor"}, (Paint[]) null, (Drawable[]) null, themeDescriptionDelegate, Theme.key_windowBackgroundWhiteBlueText));
        arrayList.add(new ThemeDescription(this.undoView, ThemeDescription.FLAG_BACKGROUNDFILTER, null, null, null, null, Theme.key_undo_background));
        int i6 = Theme.key_undo_cancelColor;
        arrayList.add(new ThemeDescription(this.undoView, 0, new Class[]{UndoView.class}, new String[]{"undoImageView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, i6));
        arrayList.add(new ThemeDescription(this.undoView, 0, new Class[]{UndoView.class}, new String[]{"undoTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, i6));
        int i7 = Theme.key_undo_infoColor;
        arrayList.add(new ThemeDescription(this.undoView, 0, new Class[]{UndoView.class}, new String[]{"infoTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, i7));
        arrayList.add(new ThemeDescription(this.undoView, 0, new Class[]{UndoView.class}, new String[]{"textPaint"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, i7));
        arrayList.add(new ThemeDescription(this.undoView, 0, new Class[]{UndoView.class}, new String[]{"progressPaint"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, i7));
        arrayList.add(new ThemeDescription(this.undoView, ThemeDescription.FLAG_IMAGECOLOR, new Class[]{UndoView.class}, new String[]{"leftImageView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, i7));
        arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_CHECKTAG, new Class[]{ManageChatTextCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, i4));
        arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_CHECKTAG, new Class[]{ManageChatTextCell.class}, new String[]{"imageView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteGrayIcon));
        arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_CHECKTAG, new Class[]{ManageChatTextCell.class}, new String[]{"imageView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteBlueButton));
        arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_CHECKTAG, new Class[]{ManageChatTextCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteBlueIcon));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{StickerEmptyView.class}, new String[]{"title"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, i4));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{StickerEmptyView.class}, new String[]{"subtitle"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, i4));
        arrayList.add(new ThemeDescription(this.emptyView.title, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, i4));
        arrayList.add(new ThemeDescription(this.emptyView.subtitle, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, i5));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{ManageChatUserCell.class}, null, Theme.avatarDrawables, null, Theme.key_avatar_text));
        arrayList.add(new ThemeDescription(null, 0, null, null, null, themeDescriptionDelegate, Theme.key_avatar_backgroundRed));
        arrayList.add(new ThemeDescription(null, 0, null, null, null, themeDescriptionDelegate, Theme.key_avatar_backgroundOrange));
        arrayList.add(new ThemeDescription(null, 0, null, null, null, themeDescriptionDelegate, Theme.key_avatar_backgroundViolet));
        arrayList.add(new ThemeDescription(null, 0, null, null, null, themeDescriptionDelegate, Theme.key_avatar_backgroundGreen));
        arrayList.add(new ThemeDescription(null, 0, null, null, null, themeDescriptionDelegate, Theme.key_avatar_backgroundCyan));
        arrayList.add(new ThemeDescription(null, 0, null, null, null, themeDescriptionDelegate, Theme.key_avatar_backgroundBlue));
        arrayList.add(new ThemeDescription(null, 0, null, null, null, themeDescriptionDelegate, Theme.key_avatar_backgroundPink));
        return arrayList;
    }

    public boolean hasSelectType() {
        return this.selectType != 0;
    }

    @Override
    public boolean needDelayOpenAnimation() {
        return true;
    }

    @Override
    public boolean onBackPressed() {
        return checkDiscard();
    }

    @Override
    public void onBecomeFullyHidden() {
        UndoView undoView = this.undoView;
        if (undoView != null) {
            undoView.hide(true, 0);
        }
    }

    @Override
    public boolean onFragmentCreate() {
        super.onFragmentCreate();
        getNotificationCenter().addObserver(this, NotificationCenter.chatInfoDidLoad);
        getNotificationCenter().addObserver(this, NotificationCenter.dialogDeleted);
        loadChatParticipants(0, 200);
        return true;
    }

    @Override
    public void onFragmentDestroy() {
        super.onFragmentDestroy();
        getNotificationCenter().removeObserver(this, NotificationCenter.chatInfoDidLoad);
        getNotificationCenter().removeObserver(this, NotificationCenter.dialogDeleted);
    }

    @Override
    public void onPause() {
        super.onPause();
        UndoView undoView = this.undoView;
        if (undoView != null) {
            undoView.hide(true, 0);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        AndroidUtilities.requestAdjustResize(getParentActivity(), this.classGuid);
        ListAdapter listAdapter = this.listViewAdapter;
        if (listAdapter != null) {
            listAdapter.notifyDataSetChanged();
        }
        StickerEmptyView stickerEmptyView = this.emptyView;
        if (stickerEmptyView != null) {
            stickerEmptyView.requestLayout();
        }
    }

    @Override
    public void onTransitionAnimationEnd(boolean z, boolean z2) {
        if (z) {
            this.openTransitionStarted = true;
        }
        if (z && !z2 && this.needOpenSearch) {
            this.searchItem.getSearchField().requestFocus();
            AndroidUtilities.showKeyboard(this.searchItem.getSearchField());
            this.searchItem.setVisibility(8);
        }
    }

    public DiffCallback saveState() {
        DiffCallback diffCallback = new DiffCallback();
        diffCallback.oldRowCount = this.rowCount;
        diffCallback.oldBotStartRow = this.botStartRow;
        diffCallback.oldBotEndRow = this.botEndRow;
        diffCallback.oldBots.clear();
        diffCallback.oldBots.addAll(this.bots);
        diffCallback.oldContactsEndRow = this.contactsEndRow;
        diffCallback.oldContactsStartRow = this.contactsStartRow;
        diffCallback.oldContacts.clear();
        diffCallback.oldContacts.addAll(this.contacts);
        diffCallback.oldParticipantsStartRow = this.participantsStartRow;
        diffCallback.oldParticipantsEndRow = this.participantsEndRow;
        diffCallback.oldParticipants.clear();
        diffCallback.oldParticipants.addAll(this.participants);
        diffCallback.fillPositions(diffCallback.oldPositionToItem);
        return diffCallback;
    }

    public void setDelegate(ChatUsersActivityDelegate chatUsersActivityDelegate) {
        this.delegate = chatUsersActivityDelegate;
    }

    public void setInfo(TLRPC.ChatFull chatFull) {
        this.info = chatFull;
        if (chatFull != null) {
            int currentSlowmode = getCurrentSlowmode();
            this.initialSlowmode = currentSlowmode;
            this.selectedSlowmode = currentSlowmode;
            int i = this.info.boosts_unrestrict;
            this.isEnabledNotRestrictBoosters = i > 0;
            this.notRestrictBoosters = i;
        }
    }

    public void updateListAnimated(DiffCallback diffCallback) {
        View view;
        ListAdapter listAdapter = this.listViewAdapter;
        updateRows();
        if (listAdapter == null) {
            return;
        }
        diffCallback.fillPositions(diffCallback.newPositionToItem);
        DiffUtil.calculateDiff(diffCallback).dispatchUpdatesTo(this.listViewAdapter);
        RecyclerListView recyclerListView = this.listView;
        if (recyclerListView == null || this.layoutManager == null || recyclerListView.getChildCount() <= 0) {
            return;
        }
        int i = 0;
        int i2 = -1;
        while (true) {
            if (i >= this.listView.getChildCount()) {
                view = null;
                break;
            }
            RecyclerListView recyclerListView2 = this.listView;
            i2 = recyclerListView2.getChildAdapterPosition(recyclerListView2.getChildAt(i));
            if (i2 != -1) {
                view = this.listView.getChildAt(i);
                break;
            }
            i++;
        }
        if (view != null) {
            this.layoutManager.scrollToPositionWithOffset(i2, view.getTop() - this.listView.getPaddingTop());
        }
    }
}
