package org.telegram.ui.Components;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.text.SpannableString;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.FrameLayout;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.Iterator;
import org.telegram.messenger.AccountInstance;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.DispatchQueue;
import org.telegram.messenger.DownloadController;
import org.telegram.messenger.FileLoader;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MediaController;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.R;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.Utilities;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.Cells.GraySectionCell;
import org.telegram.ui.Cells.SharedAudioCell;
import org.telegram.ui.Cells.SharedDocumentCell;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.FilteredSearchView;
import org.telegram.ui.PhotoViewer;
import org.telegram.ui.PremiumPreviewFragment;

public class SearchDownloadsContainer extends FrameLayout implements NotificationCenter.NotificationCenterDelegate {
    DownloadsAdapter adapter;
    boolean checkingFilesExist;
    private final int currentAccount;
    ArrayList currentLoadingFiles;
    ArrayList currentLoadingFilesTmp;
    int downloadingFilesEndRow;
    int downloadingFilesHeader;
    int downloadingFilesStartRow;
    StickerEmptyView emptyView;
    private boolean hasCurrentDownload;
    RecyclerItemsEnterAnimator itemsEnterAnimator;
    String lastQueryString;
    Runnable lastSearchRunnable;
    private final FlickerLoadingView loadingView;
    private final FilteredSearchView.MessageHashId messageHashIdTmp;
    Activity parentActivity;
    BaseFragment parentFragment;
    int recentFilesEndRow;
    int recentFilesHeader;
    int recentFilesStartRow;
    ArrayList recentLoadingFiles;
    ArrayList recentLoadingFilesTmp;
    public RecyclerListView recyclerListView;
    int rowCount;
    String searchQuery;
    FilteredSearchView.UiCallback uiCallback;

    public class Cell extends FrameLayout {
        SharedDocumentCell sharedDocumentCell;

        public Cell(Context context) {
            super(context);
            SharedDocumentCell sharedDocumentCell = new SharedDocumentCell(context, 2);
            this.sharedDocumentCell = sharedDocumentCell;
            sharedDocumentCell.rightDateTextView.setVisibility(8);
            addView(this.sharedDocumentCell);
        }

        @Override
        public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo accessibilityNodeInfo) {
            super.onInitializeAccessibilityNodeInfo(accessibilityNodeInfo);
            this.sharedDocumentCell.onInitializeAccessibilityNodeInfo(accessibilityNodeInfo);
        }
    }

    public class DownloadsAdapter extends RecyclerListView.SelectionAdapter {
        private DownloadsAdapter() {
        }

        public MessageObject getMessage(int i) {
            ArrayList arrayList;
            SearchDownloadsContainer searchDownloadsContainer = SearchDownloadsContainer.this;
            int i2 = searchDownloadsContainer.downloadingFilesStartRow;
            if (i < i2 || i >= searchDownloadsContainer.downloadingFilesEndRow) {
                i2 = searchDownloadsContainer.recentFilesStartRow;
                if (i < i2 || i >= searchDownloadsContainer.recentFilesEndRow) {
                    return null;
                }
                arrayList = searchDownloadsContainer.recentLoadingFiles;
            } else {
                arrayList = searchDownloadsContainer.currentLoadingFiles;
            }
            return (MessageObject) arrayList.get(i - i2);
        }

        public void lambda$onBindViewHolder$0(View view) {
            SearchDownloadsContainer searchDownloadsContainer = SearchDownloadsContainer.this;
            DownloadsInfoBottomSheet.show(searchDownloadsContainer.parentActivity, searchDownloadsContainer.parentFragment);
        }

        @Override
        public int getItemCount() {
            return SearchDownloadsContainer.this.rowCount;
        }

        @Override
        public int getItemViewType(int i) {
            SearchDownloadsContainer searchDownloadsContainer = SearchDownloadsContainer.this;
            if (i == searchDownloadsContainer.downloadingFilesHeader || i == searchDownloadsContainer.recentFilesHeader) {
                return 0;
            }
            MessageObject message = getMessage(i);
            return (message != null && message.isMusic()) ? 2 : 1;
        }

        @Override
        public boolean isEnabled(RecyclerView.ViewHolder viewHolder) {
            return viewHolder.getItemViewType() == 1 || viewHolder.getItemViewType() == 2;
        }

        @Override
        public void onBindViewHolder(androidx.recyclerview.widget.RecyclerView.ViewHolder r9, int r10) {
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.SearchDownloadsContainer.DownloadsAdapter.onBindViewHolder(androidx.recyclerview.widget.RecyclerView$ViewHolder, int):void");
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View graySectionCell = i == 0 ? new GraySectionCell(viewGroup.getContext()) : i == 1 ? new Cell(viewGroup.getContext()) : new SharedAudioCell(viewGroup.getContext()) {
                @Override
                public boolean needPlayMessage(MessageObject messageObject) {
                    return MediaController.getInstance().playMessage(messageObject);
                }
            };
            graySectionCell.setLayoutParams(new RecyclerView.LayoutParams(-1, -2));
            return new RecyclerListView.Holder(graySectionCell);
        }
    }

    public class TouchHelperCallback extends ItemTouchHelper.Callback {
        public TouchHelperCallback() {
        }

        @Override
        public void clearView(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
            super.clearView(recyclerView, viewHolder);
            viewHolder.itemView.setPressed(false);
        }

        @Override
        public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
            return (viewHolder.getAdapterPosition() < SearchDownloadsContainer.this.downloadingFilesStartRow || viewHolder.getAdapterPosition() >= SearchDownloadsContainer.this.downloadingFilesEndRow) ? ItemTouchHelper.Callback.makeMovementFlags(0, 0) : ItemTouchHelper.Callback.makeMovementFlags(3, 0);
        }

        @Override
        public boolean isLongPressDragEnabled() {
            return SearchDownloadsContainer.this.uiCallback.actionModeShowing();
        }

        @Override
        public void onChildDraw(Canvas canvas, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float f, float f2, int i, boolean z) {
            super.onChildDraw(canvas, recyclerView, viewHolder, f, f2, i, z);
        }

        @Override
        public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder viewHolder2) {
            if (viewHolder2.getAdapterPosition() >= SearchDownloadsContainer.this.downloadingFilesStartRow && viewHolder2.getAdapterPosition() < SearchDownloadsContainer.this.downloadingFilesEndRow) {
                int adapterPosition = viewHolder.getAdapterPosition();
                int adapterPosition2 = viewHolder2.getAdapterPosition();
                SearchDownloadsContainer searchDownloadsContainer = SearchDownloadsContainer.this;
                int i = searchDownloadsContainer.downloadingFilesStartRow;
                int i2 = adapterPosition - i;
                int i3 = adapterPosition2 - i;
                searchDownloadsContainer.currentLoadingFiles.indexOf(Integer.valueOf(i2));
                SearchDownloadsContainer searchDownloadsContainer2 = SearchDownloadsContainer.this;
                searchDownloadsContainer2.currentLoadingFiles.get(adapterPosition - searchDownloadsContainer2.downloadingFilesStartRow);
                MessageObject messageObject = (MessageObject) SearchDownloadsContainer.this.currentLoadingFiles.get(i2);
                MessageObject messageObject2 = (MessageObject) SearchDownloadsContainer.this.currentLoadingFiles.get(i3);
                SearchDownloadsContainer.this.currentLoadingFiles.set(i2, messageObject2);
                SearchDownloadsContainer.this.currentLoadingFiles.set(i3, messageObject);
                DownloadController.getInstance(SearchDownloadsContainer.this.currentAccount).swapLoadingPriority(messageObject, messageObject2);
                SearchDownloadsContainer.this.adapter.notifyItemMoved(adapterPosition, adapterPosition2);
            }
            return false;
        }

        @Override
        public void onSelectedChanged(RecyclerView.ViewHolder viewHolder, int i) {
            if (i != 0) {
                SearchDownloadsContainer.this.recyclerListView.cancelClickRunnables(false);
                viewHolder.itemView.setPressed(true);
            }
            super.onSelectedChanged(viewHolder, i);
        }

        @Override
        public void onSwiped(RecyclerView.ViewHolder viewHolder, int i) {
        }
    }

    public SearchDownloadsContainer(BaseFragment baseFragment, final int i) {
        super(baseFragment.getParentActivity());
        this.adapter = new DownloadsAdapter();
        this.currentLoadingFiles = new ArrayList();
        this.recentLoadingFiles = new ArrayList();
        this.currentLoadingFilesTmp = new ArrayList();
        this.recentLoadingFilesTmp = new ArrayList();
        this.downloadingFilesHeader = -1;
        this.downloadingFilesStartRow = -1;
        this.downloadingFilesEndRow = -1;
        this.recentFilesHeader = -1;
        this.recentFilesStartRow = -1;
        this.recentFilesEndRow = -1;
        this.messageHashIdTmp = new FilteredSearchView.MessageHashId(0, 0L);
        this.parentFragment = baseFragment;
        this.parentActivity = baseFragment.getParentActivity();
        this.currentAccount = i;
        this.recyclerListView = new BlurredRecyclerView(getContext()) {
            @Override
            public void onLayout(boolean z, int i2, int i3, int i4, int i5) {
                super.onLayout(z, i2, i3, i4, i5);
                SearchDownloadsContainer.this.checkItemsFloodWait();
            }
        };
        new ItemTouchHelper(new TouchHelperCallback()).attachToRecyclerView(this.recyclerListView);
        addView(this.recyclerListView);
        this.recyclerListView.setLayoutManager(new LinearLayoutManager(baseFragment.getParentActivity()) {
            @Override
            public boolean supportsPredictiveItemAnimations() {
                return true;
            }
        });
        this.recyclerListView.setAdapter(this.adapter);
        this.recyclerListView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int i2) {
                if (i2 == 1) {
                    AndroidUtilities.hideKeyboard(SearchDownloadsContainer.this.parentActivity.getCurrentFocus());
                }
                SearchDownloadsContainer.this.checkItemsFloodWait();
            }
        });
        DefaultItemAnimator defaultItemAnimator = new DefaultItemAnimator();
        defaultItemAnimator.setDelayAnimations(false);
        defaultItemAnimator.setSupportsChangeAnimations(false);
        this.recyclerListView.setItemAnimator(defaultItemAnimator);
        this.recyclerListView.setOnItemClickListener(new RecyclerListView.OnItemClickListener() {
            @Override
            public final void onItemClick(View view, int i2) {
                SearchDownloadsContainer.this.lambda$new$0(i, view, i2);
            }
        });
        this.recyclerListView.setOnItemLongClickListener(new RecyclerListView.OnItemLongClickListener() {
            @Override
            public final boolean onItemClick(View view, int i2) {
                boolean lambda$new$1;
                lambda$new$1 = SearchDownloadsContainer.this.lambda$new$1(view, i2);
                return lambda$new$1;
            }
        });
        this.itemsEnterAnimator = new RecyclerItemsEnterAnimator(this.recyclerListView, true);
        FlickerLoadingView flickerLoadingView = new FlickerLoadingView(getContext());
        this.loadingView = flickerLoadingView;
        addView(flickerLoadingView);
        flickerLoadingView.setUseHeaderOffset(true);
        flickerLoadingView.setViewType(3);
        flickerLoadingView.setVisibility(8);
        StickerEmptyView stickerEmptyView = new StickerEmptyView(getContext(), flickerLoadingView, 1);
        this.emptyView = stickerEmptyView;
        addView(stickerEmptyView);
        this.recyclerListView.setEmptyView(this.emptyView);
        FileLoader.getInstance(i).getCurrentLoadingFiles(this.currentLoadingFiles);
    }

    private void checkFilesExist() {
        if (this.checkingFilesExist) {
            return;
        }
        this.checkingFilesExist = true;
        Utilities.searchQueue.postRunnable(new Runnable() {
            @Override
            public final void run() {
                SearchDownloadsContainer.this.lambda$checkFilesExist$3();
            }
        });
    }

    private boolean isEmptyDownloads() {
        return DownloadController.getInstance(this.currentAccount).downloadingFiles.isEmpty() && DownloadController.getInstance(this.currentAccount).recentDownloadingFiles.isEmpty();
    }

    public void lambda$checkFilesExist$2(ArrayList arrayList, ArrayList arrayList2) {
        for (int i = 0; i < arrayList.size(); i++) {
            DownloadController.getInstance(this.currentAccount).onDownloadComplete((MessageObject) arrayList.get(i));
        }
        if (!arrayList2.isEmpty()) {
            DownloadController.getInstance(this.currentAccount).deleteRecentFiles(arrayList2);
        }
        this.checkingFilesExist = false;
        update(true);
    }

    public void lambda$checkFilesExist$3() {
        ArrayList<MessageObject> arrayList = new ArrayList<>();
        ArrayList<MessageObject> arrayList2 = new ArrayList<>();
        final ArrayList arrayList3 = new ArrayList();
        final ArrayList arrayList4 = new ArrayList();
        FileLoader.getInstance(this.currentAccount).getCurrentLoadingFiles(arrayList);
        FileLoader.getInstance(this.currentAccount).getRecentLoadingFiles(arrayList2);
        for (int i = 0; i < arrayList.size(); i++) {
            if (FileLoader.getInstance(this.currentAccount).getPathToMessage(arrayList.get(i).messageOwner).exists()) {
                arrayList3.add(arrayList.get(i));
            }
        }
        for (int i2 = 0; i2 < arrayList2.size(); i2++) {
            if (!FileLoader.getInstance(this.currentAccount).getPathToMessage(arrayList2.get(i2).messageOwner).exists()) {
                arrayList4.add(arrayList2.get(i2));
            }
        }
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                SearchDownloadsContainer.this.lambda$checkFilesExist$2(arrayList3, arrayList4);
            }
        });
    }

    public void lambda$new$0(int i, View view, int i2) {
        MessageObject message = this.adapter.getMessage(i2);
        if (message == null) {
            return;
        }
        boolean z = false;
        if (this.uiCallback.actionModeShowing()) {
            this.uiCallback.toggleItemSelection(message, view, 0);
            this.messageHashIdTmp.set(message.getId(), message.getDialogId());
            this.adapter.notifyItemChanged(i2);
            if (this.uiCallback.actionModeShowing()) {
                return;
            }
            DownloadsAdapter downloadsAdapter = this.adapter;
            downloadsAdapter.notifyItemRangeChanged(0, downloadsAdapter.getItemCount());
            return;
        }
        if (view instanceof Cell) {
            SharedDocumentCell sharedDocumentCell = ((Cell) view).sharedDocumentCell;
            MessageObject message2 = sharedDocumentCell.getMessage();
            TLRPC.Document document = message2.getDocument();
            if (sharedDocumentCell.isLoaded()) {
                if (message2.isRoundVideo() || message2.isVoice()) {
                    MediaController.getInstance().playMessage(message2);
                    return;
                }
                boolean canPreviewDocument = message2.canPreviewDocument();
                if (canPreviewDocument) {
                    z = canPreviewDocument;
                } else {
                    TLRPC.Message message3 = message2.messageOwner;
                    boolean z2 = message3 != null && message3.noforwards;
                    TLRPC.Chat chat = null;
                    TLRPC.Chat chat2 = message.messageOwner.peer_id.channel_id != 0 ? MessagesController.getInstance(UserConfig.selectedAccount).getChat(Long.valueOf(message.messageOwner.peer_id.channel_id)) : null;
                    if (chat2 != null) {
                        chat = chat2;
                    } else if (message.messageOwner.peer_id.chat_id != 0) {
                        chat = MessagesController.getInstance(UserConfig.selectedAccount).getChat(Long.valueOf(message.messageOwner.peer_id.chat_id));
                    }
                    if (chat != null) {
                        z2 = chat.noforwards;
                    }
                    if (canPreviewDocument || z2) {
                        z = true;
                    }
                }
                if (z) {
                    PhotoViewer.getInstance().setParentActivity(this.parentFragment);
                    ArrayList arrayList = new ArrayList();
                    arrayList.add(message2);
                    PhotoViewer.getInstance().setParentActivity(this.parentFragment);
                    PhotoViewer.getInstance().openPhoto(arrayList, 0, 0L, 0L, 0L, new PhotoViewer.EmptyPhotoViewerProvider());
                    return;
                }
                AndroidUtilities.openDocument(message2, this.parentActivity, this.parentFragment);
            } else if (sharedDocumentCell.isLoading()) {
                AccountInstance.getInstance(UserConfig.selectedAccount).getFileLoader().cancelLoadFile(document);
                sharedDocumentCell.updateFileExistIcon(true);
            } else {
                message.putInDownloadsStore = true;
                AccountInstance.getInstance(UserConfig.selectedAccount).getFileLoader().loadFile(document, message, 0, 0);
                sharedDocumentCell.updateFileExistIcon(true);
                DownloadController.getInstance(i).updateFilesLoadingPriority();
            }
            update(true);
        }
        if (view instanceof SharedAudioCell) {
            ((SharedAudioCell) view).didPressedButton();
        }
    }

    public boolean lambda$new$1(View view, int i) {
        MessageObject message = this.adapter.getMessage(i);
        if (message == null) {
            return false;
        }
        if (!this.uiCallback.actionModeShowing()) {
            this.uiCallback.showActionMode();
            DownloadsAdapter downloadsAdapter = this.adapter;
            downloadsAdapter.notifyItemRangeChanged(0, downloadsAdapter.getItemCount());
        }
        if (!this.uiCallback.actionModeShowing()) {
            return true;
        }
        this.uiCallback.toggleItemSelection(message, view, 0);
        if (!this.uiCallback.actionModeShowing()) {
            DownloadsAdapter downloadsAdapter2 = this.adapter;
            downloadsAdapter2.notifyItemRangeChanged(0, downloadsAdapter2.getItemCount());
        }
        this.messageHashIdTmp.set(message.getId(), message.getDialogId());
        return true;
    }

    public void lambda$showPremiumFloodWaitBulletin$6(boolean z) {
        this.parentFragment.presentFragment(new PremiumPreviewFragment(z ? "upload_speed" : "download_speed"));
    }

    public void lambda$update$4(String str, ArrayList arrayList, ArrayList arrayList2) {
        if (str.equals(this.lastQueryString)) {
            if (this.rowCount == 0) {
                this.itemsEnterAnimator.showItemsAnimated(0);
            }
            updateListInternal(true, arrayList, arrayList2);
            if (this.rowCount == 0) {
                this.emptyView.showProgress(false, true);
                this.emptyView.title.setText(LocaleController.getString(R.string.SearchEmptyViewTitle2));
                this.emptyView.subtitle.setVisibility(0);
                this.emptyView.subtitle.setText(LocaleController.getString(R.string.SearchEmptyViewFilteredSubtitle2));
            }
        }
    }

    public void lambda$update$5(ArrayList arrayList, final String str, ArrayList arrayList2) {
        final ArrayList arrayList3 = new ArrayList();
        final ArrayList arrayList4 = new ArrayList();
        for (int i = 0; i < arrayList.size(); i++) {
            if (FileLoader.getDocumentFileName(((MessageObject) arrayList.get(i)).getDocument()).toLowerCase().contains(str)) {
                MessageObject messageObject = new MessageObject(this.currentAccount, ((MessageObject) arrayList.get(i)).messageOwner, false, false);
                messageObject.mediaExists = ((MessageObject) arrayList.get(i)).mediaExists;
                messageObject.setQuery(this.searchQuery);
                arrayList3.add(messageObject);
            }
        }
        for (int i2 = 0; i2 < arrayList2.size(); i2++) {
            String documentFileName = FileLoader.getDocumentFileName(((MessageObject) arrayList2.get(i2)).getDocument());
            if (documentFileName != null && documentFileName.toLowerCase().contains(str)) {
                MessageObject messageObject2 = new MessageObject(this.currentAccount, ((MessageObject) arrayList2.get(i2)).messageOwner, false, false);
                messageObject2.mediaExists = ((MessageObject) arrayList2.get(i2)).mediaExists;
                messageObject2.setQuery(this.searchQuery);
                arrayList4.add(messageObject2);
            }
        }
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                SearchDownloadsContainer.this.lambda$update$4(str, arrayList3, arrayList4);
            }
        });
    }

    private void updateListInternal(boolean z, ArrayList arrayList, ArrayList arrayList2) {
        RecyclerView.ViewHolder childViewHolder;
        if (!z) {
            updateRows(arrayList, arrayList2);
            this.adapter.notifyDataSetChanged();
            return;
        }
        final int i = this.downloadingFilesHeader;
        final int i2 = this.downloadingFilesStartRow;
        final int i3 = this.downloadingFilesEndRow;
        final int i4 = this.recentFilesHeader;
        final int i5 = this.recentFilesStartRow;
        final int i6 = this.recentFilesEndRow;
        final int i7 = this.rowCount;
        final ArrayList arrayList3 = new ArrayList(this.currentLoadingFiles);
        final ArrayList arrayList4 = new ArrayList(this.recentLoadingFiles);
        updateRows(arrayList, arrayList2);
        DiffUtil.calculateDiff(new DiffUtil.Callback() {
            @Override
            public boolean areContentsTheSame(int i8, int i9) {
                return areItemsTheSame(i8, i9);
            }

            @Override
            public boolean areItemsTheSame(int r6, int r7) {
                throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.SearchDownloadsContainer.AnonymousClass4.areItemsTheSame(int, int):boolean");
            }

            @Override
            public int getNewListSize() {
                return SearchDownloadsContainer.this.rowCount;
            }

            @Override
            public int getOldListSize() {
                return i7;
            }
        }).dispatchUpdatesTo(this.adapter);
        for (int i8 = 0; i8 < this.recyclerListView.getChildCount(); i8++) {
            View childAt = this.recyclerListView.getChildAt(i8);
            int childAdapterPosition = this.recyclerListView.getChildAdapterPosition(childAt);
            if (childAdapterPosition >= 0 && (childViewHolder = this.recyclerListView.getChildViewHolder(childAt)) != null && !childViewHolder.shouldIgnore()) {
                if (childAt instanceof GraySectionCell) {
                    this.adapter.onBindViewHolder(childViewHolder, childAdapterPosition);
                } else if (childAt instanceof Cell) {
                    Cell cell = (Cell) childAt;
                    cell.sharedDocumentCell.updateFileExistIcon(true);
                    this.messageHashIdTmp.set(cell.sharedDocumentCell.getMessage().getId(), cell.sharedDocumentCell.getMessage().getDialogId());
                    cell.sharedDocumentCell.setChecked(this.uiCallback.isSelected(this.messageHashIdTmp), true);
                }
            }
        }
    }

    private void updateRows(ArrayList arrayList, ArrayList arrayList2) {
        this.currentLoadingFiles.clear();
        Iterator it = arrayList.iterator();
        while (it.hasNext()) {
            MessageObject messageObject = (MessageObject) it.next();
            if (!messageObject.isRoundVideo() && !messageObject.isVoice()) {
                this.currentLoadingFiles.add(messageObject);
            }
        }
        this.recentLoadingFiles.clear();
        Iterator it2 = arrayList2.iterator();
        while (it2.hasNext()) {
            MessageObject messageObject2 = (MessageObject) it2.next();
            if (!messageObject2.isRoundVideo() && !messageObject2.isVoice()) {
                this.recentLoadingFiles.add(messageObject2);
            }
        }
        int i = 0;
        this.rowCount = 0;
        this.downloadingFilesHeader = -1;
        this.downloadingFilesStartRow = -1;
        this.downloadingFilesEndRow = -1;
        this.recentFilesHeader = -1;
        this.recentFilesStartRow = -1;
        this.recentFilesEndRow = -1;
        this.hasCurrentDownload = false;
        if (!this.currentLoadingFiles.isEmpty()) {
            int i2 = this.rowCount;
            int i3 = i2 + 1;
            this.rowCount = i3;
            this.downloadingFilesHeader = i2;
            this.downloadingFilesStartRow = i3;
            int size = i3 + this.currentLoadingFiles.size();
            this.rowCount = size;
            this.downloadingFilesEndRow = size;
            while (true) {
                if (i >= this.currentLoadingFiles.size()) {
                    break;
                }
                if (FileLoader.getInstance(this.currentAccount).isLoadingFile(((MessageObject) this.currentLoadingFiles.get(i)).getFileName())) {
                    this.hasCurrentDownload = true;
                    break;
                }
                i++;
            }
        }
        if (this.recentLoadingFiles.isEmpty()) {
            return;
        }
        int i4 = this.rowCount;
        int i5 = i4 + 1;
        this.rowCount = i5;
        this.recentFilesHeader = i4;
        this.recentFilesStartRow = i5;
        int size2 = i5 + this.recentLoadingFiles.size();
        this.rowCount = size2;
        this.recentFilesEndRow = size2;
    }

    public void checkItemsFloodWait() {
        MessageObject message;
        if (UserConfig.getInstance(this.currentAccount).isPremium() || this.recyclerListView == null) {
            return;
        }
        for (int i = 0; i < this.recyclerListView.getChildCount(); i++) {
            try {
                View childAt = this.recyclerListView.getChildAt(i);
                if ((childAt instanceof Cell) && (message = ((Cell) childAt).sharedDocumentCell.getMessage()) != null) {
                    if (FileLoader.getInstance(this.currentAccount).checkLoadCaughtPremiumFloodWait(message.getFileName())) {
                        showPremiumFloodWaitBulletin(false);
                        return;
                    } else if (FileLoader.getInstance(this.currentAccount).checkLoadCaughtPremiumFloodWait(message.getFileName())) {
                        showPremiumFloodWaitBulletin(true);
                        return;
                    }
                }
            } catch (Exception e) {
                FileLog.e(e);
            }
        }
    }

    @Override
    public void didReceivedNotification(int i, int i2, Object... objArr) {
        if (i == NotificationCenter.onDownloadingFilesChanged) {
            if (getVisibility() == 0) {
                DownloadController.getInstance(this.currentAccount).clearUnviewedDownloads();
            }
            update(true);
        } else if (i == NotificationCenter.premiumFloodWaitReceived) {
            checkItemsFloodWait();
        }
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.onDownloadingFilesChanged);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.premiumFloodWaitReceived);
        if (getVisibility() == 0) {
            DownloadController.getInstance(this.currentAccount).clearUnviewedDownloads();
        }
        checkFilesExist();
        update(false);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.onDownloadingFilesChanged);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.premiumFloodWaitReceived);
    }

    public void search(String str) {
        this.searchQuery = str;
        update(false);
    }

    public void setKeyboardHeight(int i, boolean z) {
        this.emptyView.setKeyboardHeight(i, z);
    }

    public void setUiCallback(FilteredSearchView.UiCallback uiCallback) {
        this.uiCallback = uiCallback;
    }

    public void showPremiumFloodWaitBulletin(final boolean z) {
        if (this.parentFragment == null || !this.recyclerListView.isAttachedToWindow()) {
            return;
        }
        long currentTimeMillis = System.currentTimeMillis();
        if (currentTimeMillis - ConnectionsManager.lastPremiumFloodWaitShown < MessagesController.getInstance(this.currentAccount).uploadPremiumSpeedupNotifyPeriod * 1000) {
            return;
        }
        ConnectionsManager.lastPremiumFloodWaitShown = currentTimeMillis;
        if (UserConfig.getInstance(this.currentAccount).isPremium() || MessagesController.getInstance(this.currentAccount).premiumFeaturesBlocked()) {
            return;
        }
        MessagesController messagesController = MessagesController.getInstance(this.currentAccount);
        double round = Math.round((z ? messagesController.uploadPremiumSpeedupUpload : messagesController.uploadPremiumSpeedupDownload) * 10.0f);
        Double.isNaN(round);
        SpannableString spannableString = new SpannableString(Double.toString(round / 10.0d).replaceAll("\\.0$", ""));
        spannableString.setSpan(new TypefaceSpan(AndroidUtilities.bold()), 0, spannableString.length(), 33);
        if (this.parentFragment.hasStoryViewer()) {
            return;
        }
        BulletinFactory.of(this.parentFragment).createSimpleBulletin(R.raw.speed_limit, LocaleController.getString(z ? R.string.UploadSpeedLimited : R.string.DownloadSpeedLimited), AndroidUtilities.replaceCharSequence("%d", AndroidUtilities.premiumText(LocaleController.getString(z ? R.string.UploadSpeedLimitedMessage : R.string.DownloadSpeedLimitedMessage), new Runnable() {
            @Override
            public final void run() {
                SearchDownloadsContainer.this.lambda$showPremiumFloodWaitBulletin$6(z);
            }
        }), spannableString)).setDuration(8000).show(false);
    }

    public void update(boolean z) {
        DownloadsAdapter downloadsAdapter = this.adapter;
        downloadsAdapter.notifyItemRangeChanged(0, downloadsAdapter.getItemCount());
        if (!TextUtils.isEmpty(this.searchQuery) && !isEmptyDownloads()) {
            this.emptyView.setStickerType(1);
            final ArrayList<MessageObject> arrayList = new ArrayList<>();
            final ArrayList<MessageObject> arrayList2 = new ArrayList<>();
            FileLoader.getInstance(this.currentAccount).getCurrentLoadingFiles(arrayList);
            FileLoader.getInstance(this.currentAccount).getRecentLoadingFiles(arrayList2);
            final String lowerCase = this.searchQuery.toLowerCase();
            boolean equals = lowerCase.equals(this.lastQueryString);
            this.lastQueryString = lowerCase;
            Utilities.searchQueue.cancelRunnable(this.lastSearchRunnable);
            DispatchQueue dispatchQueue = Utilities.searchQueue;
            Runnable runnable = new Runnable() {
                @Override
                public final void run() {
                    SearchDownloadsContainer.this.lambda$update$5(arrayList, lowerCase, arrayList2);
                }
            };
            this.lastSearchRunnable = runnable;
            dispatchQueue.postRunnable(runnable, equals ? 0L : 300L);
            this.recentLoadingFilesTmp.clear();
            this.currentLoadingFilesTmp.clear();
            if (equals) {
                return;
            }
            this.emptyView.showProgress(true, true);
            updateListInternal(z, this.currentLoadingFilesTmp, this.recentLoadingFilesTmp);
            return;
        }
        if (this.rowCount == 0) {
            this.itemsEnterAnimator.showItemsAnimated(0);
        }
        if (this.checkingFilesExist) {
            this.currentLoadingFilesTmp.clear();
            this.recentLoadingFilesTmp.clear();
        }
        FileLoader.getInstance(this.currentAccount).getCurrentLoadingFiles(this.currentLoadingFilesTmp);
        FileLoader.getInstance(this.currentAccount).getRecentLoadingFiles(this.recentLoadingFilesTmp);
        for (int i = 0; i < this.currentLoadingFiles.size(); i++) {
            ((MessageObject) this.currentLoadingFiles.get(i)).setQuery(null);
        }
        for (int i2 = 0; i2 < this.recentLoadingFiles.size(); i2++) {
            ((MessageObject) this.recentLoadingFiles.get(i2)).setQuery(null);
        }
        this.lastQueryString = null;
        updateListInternal(z, this.currentLoadingFilesTmp, this.recentLoadingFilesTmp);
        if (this.rowCount == 0) {
            this.emptyView.showProgress(false, false);
            this.emptyView.title.setText(LocaleController.getString(R.string.SearchEmptyViewDownloads));
            this.emptyView.subtitle.setVisibility(8);
        }
        this.emptyView.setStickerType(9);
    }
}
