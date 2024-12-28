package org.telegram.ui.Adapters;

import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.os.SystemClock;
import android.view.View;
import android.widget.FrameLayout;
import androidx.core.util.Consumer;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Objects;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.BuildVars;
import org.telegram.messenger.ChatObject;
import org.telegram.messenger.DialogObject;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.R;
import org.telegram.messenger.SharedConfig;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.UserObject;
import org.telegram.messenger.Utilities;
import org.telegram.messenger.support.LongSparseIntArray;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.tgnet.tl.TL_chatlists;
import org.telegram.tgnet.tl.TL_stories;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Adapters.DialogsAdapter;
import org.telegram.ui.Cells.DialogCell;
import org.telegram.ui.Cells.DialogMeUrlCell;
import org.telegram.ui.Cells.DialogsEmptyCell;
import org.telegram.ui.Cells.DialogsHintCell;
import org.telegram.ui.Cells.DialogsRequestedEmptyCell;
import org.telegram.ui.Cells.HeaderCell;
import org.telegram.ui.Cells.ProfileSearchCell;
import org.telegram.ui.Cells.RequestPeerRequirementsCell;
import org.telegram.ui.Cells.TextCell;
import org.telegram.ui.Cells.TextInfoPrivacyCell;
import org.telegram.ui.Cells.UserCell;
import org.telegram.ui.Components.LinkSpanDrawable;
import org.telegram.ui.Components.ListView.AdapterWithDiffUtils;
import org.telegram.ui.Components.PullForegroundDrawable;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.DialogsActivity;
import org.telegram.ui.Stories.StoriesController;
import org.telegram.ui.Stories.StoriesListPlaceProvider;

public class DialogsAdapter extends RecyclerListView.SelectionAdapter implements DialogCell.DialogCellDelegate {
    private static final boolean ALLOW_UPDATE_IN_BACKGROUND = BuildVars.DEBUG_PRIVATE_VERSION;
    private Drawable arrowDrawable;
    private boolean collapsedView;
    private int currentAccount;
    private int currentCount;
    private int dialogsCount;
    private boolean dialogsListFrozen;
    private int dialogsType;
    private int folderId;
    private boolean forceShowEmptyCell;
    private boolean forceUpdatingContacts;
    private boolean hasChatlistHint;
    private boolean hasHints;
    boolean isCalculatingDiff;
    public boolean isEmpty;
    private boolean isOnlySelect;
    private boolean isReordering;
    private boolean isTransitionSupport;
    private long lastSortTime;
    private Context mContext;
    private ArrayList onlineContacts;
    private long openedDialogId;
    private DialogsActivity parentFragment;
    private DialogsPreloader preloader;
    private PullForegroundDrawable pullForegroundDrawable;
    RecyclerListView recyclerListView;
    private TLRPC.RequestPeerType requestPeerType;
    private ArrayList selectedDialogs;
    boolean updateListPending;
    private boolean firstUpdate = true;
    ArrayList itemInternals = new ArrayList();
    ArrayList oldItems = new ArrayList();
    int stableIdPointer = 10;
    LongSparseIntArray dialogsStableIds = new LongSparseIntArray();
    public int lastDialogsEmptyType = -1;

    public static class DialogsPreloader {
        int currentRequestCount;
        int networkRequestCount;
        boolean resumed;
        private final int MAX_REQUEST_COUNT = 4;
        private final int MAX_NETWORK_REQUEST_COUNT = 6;
        private final int NETWORK_REQUESTS_RESET_TIME = 60000;
        HashSet dialogsReadyMap = new HashSet();
        HashSet preloadedErrorMap = new HashSet();
        HashSet loadingDialogs = new HashSet();
        ArrayList preloadDialogsPool = new ArrayList();
        Runnable clearNetworkRequestCount = new Runnable() {
            @Override
            public final void run() {
                DialogsAdapter.DialogsPreloader.this.lambda$new$0();
            }
        };

        public class AnonymousClass1 implements MessagesController.MessagesLoadedCallback {
            final long val$dialog_id;

            AnonymousClass1(long j) {
                this.val$dialog_id = j;
            }

            public void lambda$onError$1(long j) {
                if (DialogsPreloader.this.loadingDialogs.remove(Long.valueOf(j))) {
                    DialogsPreloader.this.preloadedErrorMap.add(Long.valueOf(j));
                    r3.currentRequestCount--;
                    DialogsPreloader.this.start();
                }
            }

            public void lambda$onMessagesLoaded$0(boolean z, long j) {
                if (!z) {
                    DialogsPreloader dialogsPreloader = DialogsPreloader.this;
                    int i = dialogsPreloader.networkRequestCount + 1;
                    dialogsPreloader.networkRequestCount = i;
                    if (i >= 6) {
                        AndroidUtilities.cancelRunOnUIThread(dialogsPreloader.clearNetworkRequestCount);
                        AndroidUtilities.runOnUIThread(DialogsPreloader.this.clearNetworkRequestCount, 60000L);
                    }
                }
                if (DialogsPreloader.this.loadingDialogs.remove(Long.valueOf(j))) {
                    DialogsPreloader.this.dialogsReadyMap.add(Long.valueOf(j));
                    DialogsPreloader.this.updateList();
                    r3.currentRequestCount--;
                    DialogsPreloader.this.start();
                }
            }

            @Override
            public void onError() {
                final long j = this.val$dialog_id;
                AndroidUtilities.runOnUIThread(new Runnable() {
                    @Override
                    public final void run() {
                        DialogsAdapter.DialogsPreloader.AnonymousClass1.this.lambda$onError$1(j);
                    }
                });
            }

            @Override
            public void onMessagesLoaded(final boolean z) {
                final long j = this.val$dialog_id;
                AndroidUtilities.runOnUIThread(new Runnable() {
                    @Override
                    public final void run() {
                        DialogsAdapter.DialogsPreloader.AnonymousClass1.this.lambda$onMessagesLoaded$0(z, j);
                    }
                });
            }
        }

        public void lambda$new$0() {
            this.networkRequestCount = 0;
            start();
        }

        private boolean preloadIsAvilable() {
            return false;
        }

        public void start() {
            if (!preloadIsAvilable() || !this.resumed || this.preloadDialogsPool.isEmpty() || this.currentRequestCount >= 4 || this.networkRequestCount > 6) {
                return;
            }
            Long l = (Long) this.preloadDialogsPool.remove(0);
            long longValue = l.longValue();
            this.currentRequestCount++;
            this.loadingDialogs.add(l);
            MessagesController.getInstance(UserConfig.selectedAccount).ensureMessagesLoaded(longValue, 0, new AnonymousClass1(longValue));
        }

        public void add(long j) {
            if (isReady(j) || this.preloadedErrorMap.contains(Long.valueOf(j)) || this.loadingDialogs.contains(Long.valueOf(j)) || this.preloadDialogsPool.contains(Long.valueOf(j))) {
                return;
            }
            this.preloadDialogsPool.add(Long.valueOf(j));
            start();
        }

        public void clear() {
            this.dialogsReadyMap.clear();
            this.preloadedErrorMap.clear();
            this.loadingDialogs.clear();
            this.preloadDialogsPool.clear();
            this.currentRequestCount = 0;
            this.networkRequestCount = 0;
            AndroidUtilities.cancelRunOnUIThread(this.clearNetworkRequestCount);
            updateList();
        }

        public boolean isReady(long j) {
            return this.dialogsReadyMap.contains(Long.valueOf(j));
        }

        public void pause() {
            this.resumed = false;
        }

        public void remove(long j) {
            this.preloadDialogsPool.remove(Long.valueOf(j));
        }

        public void resume() {
            this.resumed = true;
            start();
        }

        public void updateList() {
        }
    }

    public class ItemInternal extends AdapterWithDiffUtils.Item {
        TL_chatlists.TL_chatlists_chatlistUpdates chatlistUpdates;
        TLRPC.TL_contact contact;
        TLRPC.Dialog dialog;
        private int emptyType;
        private boolean isFolder;
        boolean isForumCell;
        private boolean pinned;
        TLRPC.RecentMeUrl recentMeUrl;
        private final int stableId;

        public ItemInternal(int i) {
            super(i, true);
            this.emptyType = i;
            if (i == 10) {
                this.stableId = 1;
            } else {
                if (this.viewType == 19) {
                    this.stableId = 5;
                    return;
                }
                int i2 = DialogsAdapter.this.stableIdPointer;
                DialogsAdapter.this.stableIdPointer = i2 + 1;
                this.stableId = i2;
            }
        }

        public ItemInternal(int i, int i2) {
            super(i, true);
            this.emptyType = i2;
            int i3 = DialogsAdapter.this.stableIdPointer;
            DialogsAdapter.this.stableIdPointer = i3 + 1;
            this.stableId = i3;
        }

        public ItemInternal(int r6, org.telegram.tgnet.TLRPC.Dialog r7) {
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Adapters.DialogsAdapter.ItemInternal.<init>(org.telegram.ui.Adapters.DialogsAdapter, int, org.telegram.tgnet.TLRPC$Dialog):void");
        }

        public ItemInternal(int i, TLRPC.RecentMeUrl recentMeUrl) {
            super(i, true);
            this.recentMeUrl = recentMeUrl;
            int i2 = DialogsAdapter.this.stableIdPointer;
            DialogsAdapter.this.stableIdPointer = i2 + 1;
            this.stableId = i2;
        }

        public ItemInternal(int i, TLRPC.TL_contact tL_contact) {
            super(i, true);
            int i2;
            this.contact = tL_contact;
            if (tL_contact != null) {
                i2 = DialogsAdapter.this.dialogsStableIds.get(tL_contact.user_id, -1);
                if (i2 <= 0) {
                    int i3 = DialogsAdapter.this.stableIdPointer;
                    DialogsAdapter.this.stableIdPointer = i3 + 1;
                    this.stableId = i3;
                    DialogsAdapter.this.dialogsStableIds.put(this.contact.user_id, i3);
                    return;
                }
            } else {
                i2 = DialogsAdapter.this.stableIdPointer;
                DialogsAdapter.this.stableIdPointer = i2 + 1;
            }
            this.stableId = i2;
        }

        public ItemInternal(TL_chatlists.TL_chatlists_chatlistUpdates tL_chatlists_chatlistUpdates) {
            super(17, true);
            this.chatlistUpdates = tL_chatlists_chatlistUpdates;
            int i = DialogsAdapter.this.stableIdPointer;
            DialogsAdapter.this.stableIdPointer = i + 1;
            this.stableId = i;
        }

        boolean compare(ItemInternal itemInternal) {
            TLRPC.TL_contact tL_contact;
            String str;
            TLRPC.Dialog dialog;
            TLRPC.Dialog dialog2;
            int i = this.viewType;
            if (i != itemInternal.viewType) {
                return false;
            }
            if (i == 0) {
                TLRPC.Dialog dialog3 = this.dialog;
                return dialog3 != null && (dialog2 = itemInternal.dialog) != null && dialog3.id == dialog2.id && this.isFolder == itemInternal.isFolder && this.isForumCell == itemInternal.isForumCell && this.pinned == itemInternal.pinned;
            }
            if (i == 14) {
                TLRPC.Dialog dialog4 = this.dialog;
                return dialog4 != null && (dialog = itemInternal.dialog) != null && dialog4.id == dialog.id && dialog4.isFolder == dialog.isFolder;
            }
            if (i == 4) {
                TLRPC.RecentMeUrl recentMeUrl = this.recentMeUrl;
                return (recentMeUrl == null || itemInternal.recentMeUrl == null || (str = recentMeUrl.url) == null || !str.equals(str)) ? false : true;
            }
            if (i != 6) {
                return i == 5 ? this.emptyType == itemInternal.emptyType : i != 10;
            }
            TLRPC.TL_contact tL_contact2 = this.contact;
            return (tL_contact2 == null || (tL_contact = itemInternal.contact) == null || tL_contact2.user_id != tL_contact.user_id) ? false : true;
        }

        public int hashCode() {
            return Objects.hash(this.dialog, this.recentMeUrl, this.contact);
        }
    }

    public class LastEmptyView extends FrameLayout {
        public boolean moving;

        public LastEmptyView(Context context) {
            super(context);
        }

        @Override
        protected void onMeasure(int r12, int r13) {
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Adapters.DialogsAdapter.LastEmptyView.onMeasure(int, int):void");
        }
    }

    public DialogsAdapter(DialogsActivity dialogsActivity, Context context, int i, int i2, boolean z, ArrayList arrayList, int i3, TLRPC.RequestPeerType requestPeerType) {
        this.mContext = context;
        this.parentFragment = dialogsActivity;
        this.dialogsType = i;
        this.folderId = i2;
        this.isOnlySelect = z;
        this.hasHints = i2 == 0 && i == 0 && !z;
        this.selectedDialogs = arrayList;
        this.currentAccount = i3;
        if (i2 == 0) {
            this.preloader = new DialogsPreloader();
        }
        this.requestPeerType = requestPeerType;
    }

    public void lambda$onBindViewHolder$4() {
        this.parentFragment.setScrollDisabled(false);
    }

    public void lambda$onBindViewHolder$5(Float f) {
        this.parentFragment.setContactsAlpha(f.floatValue());
    }

    public void lambda$onCreateViewHolder$3(View view) {
        MessagesController.getInstance(this.currentAccount).hintDialogs.clear();
        MessagesController.getGlobalMainSettings().edit().remove("installReferer").commit();
        notifyDataSetChanged();
    }

    public static int lambda$sortOnlineContacts$0(org.telegram.messenger.MessagesController r2, int r3, org.telegram.tgnet.TLRPC.TL_contact r4, org.telegram.tgnet.TLRPC.TL_contact r5) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Adapters.DialogsAdapter.lambda$sortOnlineContacts$0(org.telegram.messenger.MessagesController, int, org.telegram.tgnet.TLRPC$TL_contact, org.telegram.tgnet.TLRPC$TL_contact):int");
    }

    public void lambda$updateList$1(Runnable runnable, ArrayList arrayList, DiffUtil.DiffResult diffResult) {
        if (this.isCalculatingDiff) {
            this.isCalculatingDiff = false;
            if (runnable != null) {
                runnable.run();
            }
            this.itemInternals = arrayList;
            diffResult.dispatchUpdatesTo(this);
            if (this.updateListPending) {
                this.updateListPending = false;
                updateList(runnable);
            }
        }
    }

    public void lambda$updateList$2(DiffUtil.Callback callback, final Runnable runnable, final ArrayList arrayList) {
        final DiffUtil.DiffResult calculateDiff = DiffUtil.calculateDiff(callback);
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                DialogsAdapter.this.lambda$updateList$1(runnable, arrayList, calculateDiff);
            }
        });
    }

    private void updateItemList() {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Adapters.DialogsAdapter.updateItemList():void");
    }

    @Override
    public boolean canClickButtonInside() {
        return this.selectedDialogs.isEmpty();
    }

    public int dialogsEmptyType() {
        int i = this.dialogsType;
        if (i == 7 || i == 8) {
            return MessagesController.getInstance(this.currentAccount).isDialogsEndReached(this.folderId) ? 2 : 3;
        }
        if (this.folderId == 1) {
            return 2;
        }
        return this.onlineContacts != null ? 1 : 0;
    }

    public void didDatabaseCleared() {
        DialogsPreloader dialogsPreloader = this.preloader;
        if (dialogsPreloader != null) {
            dialogsPreloader.clear();
        }
    }

    public int findDialogPosition(long j) {
        for (int i = 0; i < this.itemInternals.size(); i++) {
            if (((ItemInternal) this.itemInternals.get(i)).dialog != null && ((ItemInternal) this.itemInternals.get(i)).dialog.id == j) {
                return i;
            }
        }
        return -1;
    }

    public int fixPosition(int i) {
        if (this.hasChatlistHint) {
            i--;
        }
        if (this.hasHints) {
            i -= MessagesController.getInstance(this.currentAccount).hintDialogs.size() + 2;
        }
        int i2 = this.dialogsType;
        return (i2 == 11 || i2 == 13) ? i - 2 : i2 == 12 ? i - 1 : i;
    }

    public int fixScrollGap(org.telegram.ui.Components.RecyclerListView r3, int r4, int r5, boolean r6, boolean r7, boolean r8, boolean r9) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Adapters.DialogsAdapter.fixScrollGap(org.telegram.ui.Components.RecyclerListView, int, int, boolean, boolean, boolean, boolean):int");
    }

    public ViewPager getArchiveHintCellPager() {
        return null;
    }

    public TL_chatlists.TL_chatlists_chatlistUpdates getChatlistUpdate() {
        ItemInternal itemInternal = (ItemInternal) this.itemInternals.get(0);
        if (itemInternal == null || itemInternal.viewType != 17) {
            return null;
        }
        return itemInternal.chatlistUpdates;
    }

    public int getCurrentCount() {
        return this.currentCount;
    }

    public int getDialogsCount() {
        return this.dialogsCount;
    }

    public boolean getDialogsListIsFrozen() {
        return this.dialogsListFrozen;
    }

    public int getDialogsType() {
        return this.dialogsType;
    }

    public TLObject getItem(int i) {
        if (i >= 0 && i < this.itemInternals.size()) {
            if (((ItemInternal) this.itemInternals.get(i)).dialog != null) {
                return ((ItemInternal) this.itemInternals.get(i)).dialog;
            }
            if (((ItemInternal) this.itemInternals.get(i)).contact != null) {
                return MessagesController.getInstance(this.currentAccount).getUser(Long.valueOf(((ItemInternal) this.itemInternals.get(i)).contact.user_id));
            }
            if (((ItemInternal) this.itemInternals.get(i)).recentMeUrl != null) {
                return ((ItemInternal) this.itemInternals.get(i)).recentMeUrl;
            }
        }
        return null;
    }

    @Override
    public int getItemCount() {
        int size = this.itemInternals.size();
        this.currentCount = size;
        return size;
    }

    public int getItemHeight(int i) {
        int dp = AndroidUtilities.dp(SharedConfig.useThreeLinesLayout ? 78.0f : 72.0f);
        if (((ItemInternal) this.itemInternals.get(i)).viewType != 0) {
            return 0;
        }
        if (!((ItemInternal) this.itemInternals.get(i)).isForumCell || this.collapsedView) {
            return dp;
        }
        return AndroidUtilities.dp(SharedConfig.useThreeLinesLayout ? 86.0f : 91.0f);
    }

    @Override
    public long getItemId(int i) {
        return ((ItemInternal) this.itemInternals.get(i)).stableId;
    }

    @Override
    public int getItemViewType(int i) {
        return ((ItemInternal) this.itemInternals.get(i)).viewType;
    }

    public boolean isDataSetChanged() {
        return true;
    }

    @Override
    public boolean isEnabled(RecyclerView.ViewHolder viewHolder) {
        int itemViewType = viewHolder.getItemViewType();
        return (itemViewType == 1 || itemViewType == 5 || itemViewType == 3 || itemViewType == 8 || itemViewType == 7 || itemViewType == 10 || itemViewType == 11 || itemViewType == 13 || itemViewType == 15 || itemViewType == 16 || itemViewType == 18 || itemViewType == 19) ? false : true;
    }

    public void moveDialogs(RecyclerListView recyclerListView, int i, int i2) {
        ArrayList dialogsArray = this.parentFragment.getDialogsArray(this.currentAccount, this.dialogsType, this.folderId, false);
        int fixPosition = fixPosition(i);
        int fixPosition2 = fixPosition(i2);
        TLRPC.Dialog dialog = (TLRPC.Dialog) dialogsArray.get(fixPosition);
        TLRPC.Dialog dialog2 = (TLRPC.Dialog) dialogsArray.get(fixPosition2);
        int i3 = this.dialogsType;
        if (i3 == 7 || i3 == 8) {
            MessagesController.DialogFilter dialogFilter = MessagesController.getInstance(this.currentAccount).selectedDialogFilter[this.dialogsType == 8 ? (char) 1 : (char) 0];
            int i4 = dialogFilter.pinnedDialogs.get(dialog.id);
            dialogFilter.pinnedDialogs.put(dialog.id, dialogFilter.pinnedDialogs.get(dialog2.id));
            dialogFilter.pinnedDialogs.put(dialog2.id, i4);
        } else {
            int i5 = dialog.pinnedNum;
            dialog.pinnedNum = dialog2.pinnedNum;
            dialog2.pinnedNum = i5;
        }
        Collections.swap(dialogsArray, fixPosition, fixPosition2);
        updateList(null);
    }

    @Override
    public void notifyDataSetChanged() {
        if (this.isCalculatingDiff) {
            this.itemInternals = new ArrayList();
        }
        this.isCalculatingDiff = false;
        updateItemList();
        super.notifyDataSetChanged();
    }

    @Override
    public void notifyItemMoved(int i, int i2) {
        super.notifyItemMoved(i, i2);
    }

    public void onArchiveSettingsClick() {
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
        TLRPC.Chat chat;
        String str;
        TLRPC.Chat chat2;
        String str2;
        String str3;
        int i2;
        int i3;
        String string;
        String str4;
        TLRPC.Chat chat3;
        HeaderCell headerCell;
        int i4;
        String string2;
        int i5;
        int itemViewType = viewHolder.getItemViewType();
        if (itemViewType == 0) {
            TLRPC.Dialog dialog = (TLRPC.Dialog) getItem(i);
            TLRPC.Dialog dialog2 = (TLRPC.Dialog) getItem(i + 1);
            int i6 = this.dialogsType;
            if (i6 == 2 || i6 == 15) {
                ProfileSearchCell profileSearchCell = (ProfileSearchCell) viewHolder.itemView;
                long dialogId = profileSearchCell.getDialogId();
                if (dialog.id != 0) {
                    chat = MessagesController.getInstance(this.currentAccount).getChat(Long.valueOf(-dialog.id));
                    if (chat != null && chat.migrated_to != null && (chat3 = MessagesController.getInstance(this.currentAccount).getChat(Long.valueOf(chat.migrated_to.channel_id))) != null) {
                        chat = chat3;
                    }
                } else {
                    chat = null;
                }
                if (chat != null) {
                    String str5 = chat.title;
                    if (!ChatObject.isChannel(chat) || chat.megagroup) {
                        i2 = chat.participants_count;
                        if (i2 != 0) {
                            str4 = "Members";
                            string = LocaleController.formatPluralStringComma(str4, i2);
                        } else if (chat.has_geo) {
                            string = LocaleController.getString(R.string.MegaLocation);
                        } else {
                            i3 = !ChatObject.isPublic(chat) ? R.string.MegaPrivate : R.string.MegaPublic;
                            string = LocaleController.getString(i3).toLowerCase();
                        }
                    } else {
                        i2 = chat.participants_count;
                        if (i2 != 0) {
                            str4 = "Subscribers";
                            string = LocaleController.formatPluralStringComma(str4, i2);
                        } else {
                            i3 = !ChatObject.isPublic(chat) ? R.string.ChannelPrivate : R.string.ChannelPublic;
                            string = LocaleController.getString(i3).toLowerCase();
                        }
                    }
                    str3 = string;
                    str2 = str5;
                    chat2 = chat;
                } else {
                    TLRPC.User user = MessagesController.getInstance(this.currentAccount).getUser(Long.valueOf(dialog.id));
                    str = "";
                    if (user != 0) {
                        String userName = UserObject.getUserName(user);
                        str = UserObject.isReplyUser(user) ? "" : user.bot ? LocaleController.getString(R.string.Bot) : LocaleController.formatUserStatus(this.currentAccount, user);
                        chat2 = user;
                        str2 = userName;
                    } else {
                        chat2 = null;
                        str2 = null;
                    }
                    str3 = str;
                }
                profileSearchCell.useSeparator = dialog2 != null;
                profileSearchCell.setData(chat2, null, str2, str3, false, false);
                profileSearchCell.setChecked(this.selectedDialogs.contains(Long.valueOf(profileSearchCell.getDialogId())), dialogId == profileSearchCell.getDialogId());
            } else {
                DialogCell dialogCell = (DialogCell) viewHolder.itemView;
                dialogCell.useSeparator = dialog2 != null;
                dialogCell.fullSeparator = (!dialog.pinned || dialog2 == null || dialog2.pinned) ? false : true;
                if (i6 == 0 && AndroidUtilities.isTablet()) {
                    dialogCell.setDialogSelected(dialog.id == this.openedDialogId);
                }
                dialogCell.setChecked(this.selectedDialogs.contains(Long.valueOf(dialog.id)), false);
                dialogCell.setDialog(dialog, this.dialogsType, this.folderId);
                dialogCell.checkHeight();
                boolean z = dialogCell.collapsed;
                boolean z2 = this.collapsedView;
                if (z != z2) {
                    dialogCell.collapsed = z2;
                    dialogCell.requestLayout();
                }
                DialogsPreloader dialogsPreloader = this.preloader;
                if (dialogsPreloader != null && i < 10) {
                    dialogsPreloader.add(dialog.id);
                }
            }
        } else if (itemViewType == 4) {
            ((DialogMeUrlCell) viewHolder.itemView).setRecentMeUrl((TLRPC.RecentMeUrl) getItem(i));
        } else if (itemViewType == 5) {
            DialogsEmptyCell dialogsEmptyCell = (DialogsEmptyCell) viewHolder.itemView;
            int i7 = this.lastDialogsEmptyType;
            int dialogsEmptyType = dialogsEmptyType();
            this.lastDialogsEmptyType = dialogsEmptyType;
            dialogsEmptyCell.setType(dialogsEmptyType, this.isOnlySelect);
            int i8 = this.dialogsType;
            if (i8 != 7 && i8 != 8) {
                dialogsEmptyCell.setOnUtyanAnimationEndListener(new Runnable() {
                    @Override
                    public final void run() {
                        DialogsAdapter.this.lambda$onBindViewHolder$4();
                    }
                });
                dialogsEmptyCell.setOnUtyanAnimationUpdateListener(new Consumer() {
                    @Override
                    public final void accept(Object obj) {
                        DialogsAdapter.this.lambda$onBindViewHolder$5((Float) obj);
                    }
                });
                if (!dialogsEmptyCell.isUtyanAnimationTriggered() && this.dialogsCount == 0) {
                    this.parentFragment.setContactsAlpha(0.0f);
                    this.parentFragment.setScrollDisabled(true);
                }
                if (this.onlineContacts == null || i7 != 0) {
                    if (this.forceUpdatingContacts) {
                        if (this.dialogsCount == 0) {
                            dialogsEmptyCell.startUtyanCollapseAnimation(false);
                        }
                    } else if (dialogsEmptyCell.isUtyanAnimationTriggered() && this.lastDialogsEmptyType == 0) {
                        dialogsEmptyCell.startUtyanExpandAnimation();
                    }
                } else if (!dialogsEmptyCell.isUtyanAnimationTriggered()) {
                    dialogsEmptyCell.startUtyanCollapseAnimation(true);
                }
            }
        } else if (itemViewType != 6) {
            if (itemViewType == 7) {
                headerCell = (HeaderCell) viewHolder.itemView;
                int i9 = this.dialogsType;
                i4 = (i9 == 11 || i9 == 12 || i9 == 13) ? i == 0 ? R.string.ImportHeader : R.string.ImportHeaderContacts : (this.dialogsCount == 0 && this.forceUpdatingContacts) ? R.string.ConnectingYourContacts : R.string.YourContacts;
            } else if (itemViewType == 11) {
                TextInfoPrivacyCell textInfoPrivacyCell = (TextInfoPrivacyCell) viewHolder.itemView;
                textInfoPrivacyCell.setText(LocaleController.getString(R.string.TapOnThePencil));
                if (this.arrowDrawable == null) {
                    Drawable drawable = this.mContext.getResources().getDrawable(R.drawable.arrow_newchat);
                    this.arrowDrawable = drawable;
                    drawable.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_windowBackgroundWhiteGrayText4), PorterDuff.Mode.MULTIPLY));
                }
                LinkSpanDrawable.LinksTextView textView = textInfoPrivacyCell.getTextView();
                textView.setCompoundDrawablePadding(AndroidUtilities.dp(4.0f));
                DialogsActivity dialogsActivity = this.parentFragment;
                textView.setCompoundDrawablesWithIntrinsicBounds((Drawable) null, (Drawable) null, (dialogsActivity == null || !dialogsActivity.storiesEnabled) ? this.arrowDrawable : null, (Drawable) null);
                textView.getLayoutParams().width = -2;
            } else if (itemViewType != 12) {
                switch (itemViewType) {
                    case 14:
                        headerCell = (HeaderCell) viewHolder.itemView;
                        headerCell.setTextSize(14.0f);
                        headerCell.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteGrayText));
                        headerCell.setBackgroundColor(Theme.getColor(Theme.key_graySection));
                        int i10 = ((DialogsActivity.DialogsHeader) getItem(i)).headerType;
                        if (i10 == 0) {
                            i4 = R.string.MyChannels;
                            break;
                        } else if (i10 == 1) {
                            i4 = R.string.MyGroups;
                            break;
                        } else if (i10 == 2) {
                            i4 = R.string.FilterGroups;
                            break;
                        }
                        break;
                    case 15:
                        ((RequestPeerRequirementsCell) viewHolder.itemView).set(this.requestPeerType);
                        break;
                    case 16:
                        ((DialogsRequestedEmptyCell) viewHolder.itemView).set(this.requestPeerType);
                        break;
                    case 17:
                        DialogsHintCell dialogsHintCell = (DialogsHintCell) viewHolder.itemView;
                        TL_chatlists.TL_chatlists_chatlistUpdates tL_chatlists_chatlistUpdates = ((ItemInternal) this.itemInternals.get(i)).chatlistUpdates;
                        if (tL_chatlists_chatlistUpdates != null) {
                            int size = tL_chatlists_chatlistUpdates.missing_peers.size();
                            dialogsHintCell.setText(AndroidUtilities.replaceSingleTag(LocaleController.formatPluralString("FolderUpdatesTitle", size, new Object[0]), Theme.key_windowBackgroundWhiteValueText, 0, null), LocaleController.formatPluralString("FolderUpdatesSubtitle", size, new Object[0]));
                            break;
                        }
                        break;
                }
            } else {
                View view = viewHolder.itemView;
                if (!(view instanceof TextCell)) {
                    return;
                }
                TextCell textCell = (TextCell) view;
                int i11 = Theme.key_windowBackgroundWhiteBlueText4;
                textCell.setColors(i11, i11);
                TLRPC.RequestPeerType requestPeerType = this.requestPeerType;
                if (requestPeerType != null) {
                    if (requestPeerType instanceof TLRPC.TL_requestPeerTypeBroadcast) {
                        string2 = LocaleController.getString(R.string.CreateChannelForThis);
                        i5 = R.drawable.msg_channel_create;
                    } else {
                        string2 = LocaleController.getString(R.string.CreateGroupForThis);
                        i5 = R.drawable.msg_groups_create;
                    }
                    textCell.setTextAndIcon((CharSequence) string2, i5, true);
                } else {
                    textCell.setTextAndIcon(LocaleController.getString(R.string.CreateGroupForImport), R.drawable.msg_groups_create, this.dialogsCount != 0);
                }
                textCell.setIsInDialogs();
                textCell.setOffsetFromImage(75);
            }
            headerCell.setText(LocaleController.getString(i4));
        } else {
            ((UserCell) viewHolder.itemView).setData((TLRPC.User) getItem(i), null, null, 0);
        }
        if (i >= this.dialogsCount + 1) {
            viewHolder.itemView.setAlpha(1.0f);
        }
    }

    @Override
    public void onButtonClicked(DialogCell dialogCell) {
    }

    @Override
    public void onButtonLongPress(DialogCell dialogCell) {
    }

    public void onCreateGroupForThisClick() {
    }

    @Override
    public androidx.recyclerview.widget.RecyclerView.ViewHolder onCreateViewHolder(android.view.ViewGroup r20, int r21) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Adapters.DialogsAdapter.onCreateViewHolder(android.view.ViewGroup, int):androidx.recyclerview.widget.RecyclerView$ViewHolder");
    }

    public void onOpenBot(TLRPC.User user) {
    }

    public void onReorderStateChanged(boolean z) {
        this.isReordering = z;
    }

    @Override
    public void onViewAttachedToWindow(RecyclerView.ViewHolder viewHolder) {
        View view = viewHolder.itemView;
        if (view instanceof DialogCell) {
            DialogCell dialogCell = (DialogCell) view;
            dialogCell.onReorderStateChanged(this.isReordering, false);
            dialogCell.checkCurrentDialogIndex(this.dialogsListFrozen);
            dialogCell.setChecked(this.selectedDialogs.contains(Long.valueOf(dialogCell.getDialogId())), false);
        }
    }

    @Override
    public void openHiddenStories() {
        StoriesController storiesController = MessagesController.getInstance(this.currentAccount).getStoriesController();
        if (storiesController.getHiddenList().isEmpty()) {
            return;
        }
        boolean z = storiesController.getUnreadState(DialogObject.getPeerDialogId(((TL_stories.PeerStories) storiesController.getHiddenList().get(0)).peer)) != 0;
        ArrayList arrayList = new ArrayList();
        for (int i = 0; i < storiesController.getHiddenList().size(); i++) {
            long peerDialogId = DialogObject.getPeerDialogId(((TL_stories.PeerStories) storiesController.getHiddenList().get(i)).peer);
            if (!z || storiesController.getUnreadState(peerDialogId) != 0) {
                arrayList.add(Long.valueOf(peerDialogId));
            }
        }
        this.parentFragment.getOrCreateStoryViewer().open(this.mContext, null, arrayList, 0, null, null, StoriesListPlaceProvider.of(this.recyclerListView, true), false);
    }

    @Override
    public void openStory(DialogCell dialogCell, Runnable runnable) {
        MessagesController.getInstance(this.currentAccount);
        if (MessagesController.getInstance(this.currentAccount).getStoriesController().hasStories(dialogCell.getDialogId())) {
            this.parentFragment.getOrCreateStoryViewer().doOnAnimationReady(runnable);
            this.parentFragment.getOrCreateStoryViewer().open(this.parentFragment.getContext(), dialogCell.getDialogId(), StoriesListPlaceProvider.of((RecyclerListView) dialogCell.getParent()));
        }
    }

    public void pause() {
        DialogsPreloader dialogsPreloader = this.preloader;
        if (dialogsPreloader != null) {
            dialogsPreloader.pause();
        }
    }

    public void resume() {
        DialogsPreloader dialogsPreloader = this.preloader;
        if (dialogsPreloader != null) {
            dialogsPreloader.resume();
        }
    }

    public void setArchivedPullDrawable(PullForegroundDrawable pullForegroundDrawable) {
        this.pullForegroundDrawable = pullForegroundDrawable;
    }

    public void setCollapsedView(boolean z, RecyclerListView recyclerListView) {
        this.collapsedView = z;
        for (int i = 0; i < recyclerListView.getChildCount(); i++) {
            if (recyclerListView.getChildAt(i) instanceof DialogCell) {
                ((DialogCell) recyclerListView.getChildAt(i)).collapsed = z;
            }
        }
        for (int i2 = 0; i2 < recyclerListView.getCachedChildCount(); i2++) {
            if (recyclerListView.getCachedChildAt(i2) instanceof DialogCell) {
                ((DialogCell) recyclerListView.getCachedChildAt(i2)).collapsed = z;
            }
        }
        for (int i3 = 0; i3 < recyclerListView.getHiddenChildCount(); i3++) {
            if (recyclerListView.getHiddenChildAt(i3) instanceof DialogCell) {
                ((DialogCell) recyclerListView.getHiddenChildAt(i3)).collapsed = z;
            }
        }
        for (int i4 = 0; i4 < recyclerListView.getAttachedScrapChildCount(); i4++) {
            if (recyclerListView.getAttachedScrapChildAt(i4) instanceof DialogCell) {
                ((DialogCell) recyclerListView.getAttachedScrapChildAt(i4)).collapsed = z;
            }
        }
    }

    public void setDialogsListFrozen(boolean z) {
        this.dialogsListFrozen = z;
    }

    public void setDialogsType(int i) {
        this.dialogsType = i;
        notifyDataSetChanged();
    }

    public void setForceShowEmptyCell(boolean z) {
        this.forceShowEmptyCell = z;
    }

    public void setForceUpdatingContacts(boolean z) {
        this.forceUpdatingContacts = z;
    }

    public void setIsTransitionSupport() {
        this.isTransitionSupport = true;
    }

    public void setOpenedDialogId(long j) {
        this.openedDialogId = j;
    }

    public void setRecyclerListView(RecyclerListView recyclerListView) {
        this.recyclerListView = recyclerListView;
    }

    @Override
    public void showChatPreview(DialogCell dialogCell) {
        this.parentFragment.showChatPreview(dialogCell);
    }

    protected boolean showOpenBotButton() {
        return false;
    }

    public void sortOnlineContacts(boolean z) {
        if (this.onlineContacts != null) {
            if (!z || SystemClock.elapsedRealtime() - this.lastSortTime >= 2000) {
                this.lastSortTime = SystemClock.elapsedRealtime();
                try {
                    final int currentTime = ConnectionsManager.getInstance(this.currentAccount).getCurrentTime();
                    final MessagesController messagesController = MessagesController.getInstance(this.currentAccount);
                    Collections.sort(this.onlineContacts, new Comparator() {
                        @Override
                        public final int compare(Object obj, Object obj2) {
                            int lambda$sortOnlineContacts$0;
                            lambda$sortOnlineContacts$0 = DialogsAdapter.lambda$sortOnlineContacts$0(MessagesController.this, currentTime, (TLRPC.TL_contact) obj, (TLRPC.TL_contact) obj2);
                            return lambda$sortOnlineContacts$0;
                        }
                    });
                    if (z) {
                        notifyDataSetChanged();
                    }
                } catch (Exception e) {
                    FileLog.e(e);
                }
            }
        }
    }

    public void updateHasHints() {
        this.hasHints = this.folderId == 0 && this.dialogsType == 0 && !this.isOnlySelect && !MessagesController.getInstance(this.currentAccount).hintDialogs.isEmpty();
    }

    public void updateList(final Runnable runnable) {
        if (this.isCalculatingDiff) {
            this.updateListPending = true;
            return;
        }
        this.isCalculatingDiff = true;
        ArrayList arrayList = new ArrayList();
        this.oldItems = arrayList;
        arrayList.addAll(this.itemInternals);
        updateItemList();
        final ArrayList arrayList2 = new ArrayList(this.itemInternals);
        this.itemInternals = this.oldItems;
        final DiffUtil.Callback callback = new DiffUtil.Callback() {
            @Override
            public boolean areContentsTheSame(int i, int i2) {
                return ((ItemInternal) DialogsAdapter.this.oldItems.get(i)).viewType == ((ItemInternal) arrayList2.get(i2)).viewType;
            }

            @Override
            public boolean areItemsTheSame(int i, int i2) {
                return ((ItemInternal) DialogsAdapter.this.oldItems.get(i)).compare((ItemInternal) arrayList2.get(i2));
            }

            @Override
            public int getNewListSize() {
                return arrayList2.size();
            }

            @Override
            public int getOldListSize() {
                return DialogsAdapter.this.oldItems.size();
            }
        };
        if (this.itemInternals.size() >= 50 && ALLOW_UPDATE_IN_BACKGROUND) {
            Utilities.searchQueue.postRunnable(new Runnable() {
                @Override
                public final void run() {
                    DialogsAdapter.this.lambda$updateList$2(callback, runnable, arrayList2);
                }
            });
            return;
        }
        DiffUtil.DiffResult calculateDiff = DiffUtil.calculateDiff(callback);
        this.isCalculatingDiff = false;
        if (runnable != null) {
            runnable.run();
        }
        this.itemInternals = arrayList2;
        calculateDiff.dispatchUpdatesTo(this);
    }
}
