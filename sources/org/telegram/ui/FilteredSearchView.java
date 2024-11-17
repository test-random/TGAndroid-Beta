package org.telegram.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.text.SpannableStringBuilder;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.Property;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import org.telegram.messenger.AccountInstance;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.AnimationNotificationsLocker;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.ChatObject;
import org.telegram.messenger.Emoji;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.ImageReceiver;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MediaController;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.MessagesStorage;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.R;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.UserObject;
import org.telegram.messenger.browser.Browser;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.BottomSheet;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ActionBar.ThemeDescription;
import org.telegram.ui.Adapters.FiltersView;
import org.telegram.ui.Business.QuickRepliesController;
import org.telegram.ui.Cells.ChatActionCell;
import org.telegram.ui.Cells.ContextLinkCell;
import org.telegram.ui.Cells.DialogCell;
import org.telegram.ui.Cells.GraySectionCell;
import org.telegram.ui.Cells.LoadingCell;
import org.telegram.ui.Cells.ProfileSearchCell;
import org.telegram.ui.Cells.SharedAudioCell;
import org.telegram.ui.Cells.SharedDocumentCell;
import org.telegram.ui.Cells.SharedLinkCell;
import org.telegram.ui.Cells.SharedMediaSectionCell;
import org.telegram.ui.Cells.SharedPhotoVideoCell;
import org.telegram.ui.Components.AlertsCreator;
import org.telegram.ui.Components.BackupImageView;
import org.telegram.ui.Components.BlurredRecyclerView;
import org.telegram.ui.Components.ColoredImageSpan;
import org.telegram.ui.Components.CubicBezierInterpolator;
import org.telegram.ui.Components.EmbedBottomSheet;
import org.telegram.ui.Components.FlickerLoadingView;
import org.telegram.ui.Components.Forum.ForumUtilities;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.Components.SearchViewPager;
import org.telegram.ui.Components.StickerEmptyView;
import org.telegram.ui.FilteredSearchView;
import org.telegram.ui.PhotoViewer;

public class FilteredSearchView extends FrameLayout implements NotificationCenter.NotificationCenterDelegate {
    private static SpannableStringBuilder[] arrowSpan = new SpannableStringBuilder[3];
    RecyclerView.Adapter adapter;
    private SearchViewPager.ChatPreviewDelegate chatPreviewDelegate;
    Runnable clearCurrentResultsRunnable;
    private int columnsCount;
    private String currentDataQuery;
    boolean currentIncludeFolder;
    long currentSearchDialogId;
    FiltersView.MediaFilterData currentSearchFilter;
    long currentSearchMaxDate;
    long currentSearchMinDate;
    String currentSearchString;
    private Delegate delegate;
    private OnlyUserFiltersAdapter dialogsAdapter;
    StickerEmptyView emptyView;
    private boolean endReached;
    private boolean firstLoading;
    private AnimatorSet floatingDateAnimation;
    private final ChatActionCell floatingDateView;
    private Runnable hideFloatingDateRunnable;
    boolean ignoreRequestLayout;
    private boolean isLoading;
    int lastAccount;
    String lastMessagesSearchString;
    String lastSearchFilterQueryString;
    public final LinearLayoutManager layoutManager;
    private final FlickerLoadingView loadingView;
    boolean localTipArchive;
    ArrayList localTipChats;
    ArrayList localTipDates;
    private final MessageHashId messageHashIdTmp;
    public ArrayList messages;
    public SparseArray messagesById;
    private int nextSearchRate;
    private AnimationNotificationsLocker notificationsLocker;
    Activity parentActivity;
    BaseFragment parentFragment;
    private int photoViewerClassGuid;
    private PhotoViewer.PhotoViewerProvider provider;
    public RecyclerListView recyclerListView;
    private int requestIndex;
    Runnable searchRunnable;
    public HashMap sectionArrays;
    public ArrayList sections;
    private SharedDocumentsAdapter sharedAudioAdapter;
    private SharedDocumentsAdapter sharedDocumentsAdapter;
    private SharedLinksAdapter sharedLinksAdapter;
    private SharedPhotoVideoAdapter sharedPhotoVideoAdapter;
    private SharedDocumentsAdapter sharedVoiceAdapter;
    private int totalCount;
    private UiCallback uiCallback;
    private boolean useFromUserAsAvatar;

    public class AnonymousClass6 extends RecyclerView.OnScrollListener {
        AnonymousClass6() {
        }

        public void lambda$onScrolled$0() {
            FilteredSearchView filteredSearchView = FilteredSearchView.this;
            filteredSearchView.search(filteredSearchView.currentSearchDialogId, filteredSearchView.currentSearchMinDate, filteredSearchView.currentSearchMaxDate, filteredSearchView.currentSearchFilter, filteredSearchView.currentIncludeFolder, filteredSearchView.lastMessagesSearchString, false);
        }

        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int i) {
            if (i == 1) {
                AndroidUtilities.hideKeyboard(FilteredSearchView.this.parentActivity.getCurrentFocus());
            }
        }

        @Override
        public void onScrolled(RecyclerView recyclerView, int i, int i2) {
            MessageObject messageObject;
            if (recyclerView.getAdapter() != null) {
                FilteredSearchView filteredSearchView = FilteredSearchView.this;
                if (filteredSearchView.adapter == null) {
                    return;
                }
                int findFirstVisibleItemPosition = filteredSearchView.layoutManager.findFirstVisibleItemPosition();
                int findLastVisibleItemPosition = FilteredSearchView.this.layoutManager.findLastVisibleItemPosition();
                int abs = Math.abs(findLastVisibleItemPosition - findFirstVisibleItemPosition) + 1;
                int itemCount = recyclerView.getAdapter().getItemCount();
                if (!FilteredSearchView.this.isLoading && abs > 0 && findLastVisibleItemPosition >= itemCount - 10 && !FilteredSearchView.this.endReached) {
                    AndroidUtilities.runOnUIThread(new Runnable() {
                        @Override
                        public final void run() {
                            FilteredSearchView.AnonymousClass6.this.lambda$onScrolled$0();
                        }
                    });
                }
                FilteredSearchView filteredSearchView2 = FilteredSearchView.this;
                if (filteredSearchView2.adapter == filteredSearchView2.sharedPhotoVideoAdapter) {
                    if (i2 != 0 && !FilteredSearchView.this.messages.isEmpty() && TextUtils.isEmpty(FilteredSearchView.this.currentDataQuery)) {
                        FilteredSearchView.this.showFloatingDateView();
                    }
                    RecyclerView.ViewHolder findViewHolderForAdapterPosition = recyclerView.findViewHolderForAdapterPosition(findFirstVisibleItemPosition);
                    if (findViewHolderForAdapterPosition == null || findViewHolderForAdapterPosition.getItemViewType() != 0) {
                        return;
                    }
                    View view = findViewHolderForAdapterPosition.itemView;
                    if (!(view instanceof SharedPhotoVideoCell) || (messageObject = ((SharedPhotoVideoCell) view).getMessageObject(0)) == null) {
                        return;
                    }
                    FilteredSearchView.this.floatingDateView.setCustomDate(messageObject.messageOwner.date, false, true);
                }
            }
        }
    }

    public interface Delegate {
        void updateFiltersView(boolean z, ArrayList arrayList, ArrayList arrayList2, boolean z2);
    }

    public static class MessageHashId {
        public long dialogId;
        public int messageId;

        public MessageHashId(int i, long j) {
            this.dialogId = j;
            this.messageId = i;
        }

        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null || getClass() != obj.getClass()) {
                return false;
            }
            MessageHashId messageHashId = (MessageHashId) obj;
            return this.dialogId == messageHashId.dialogId && this.messageId == messageHashId.messageId;
        }

        public int hashCode() {
            return this.messageId;
        }

        public void set(int i, long j) {
            this.dialogId = j;
            this.messageId = i;
        }
    }

    public class OnlyUserFiltersAdapter extends RecyclerListView.SelectionAdapter {
        OnlyUserFiltersAdapter() {
        }

        @Override
        public int getItemCount() {
            if (FilteredSearchView.this.messages.isEmpty()) {
                return 0;
            }
            return FilteredSearchView.this.messages.size() + (!FilteredSearchView.this.endReached ? 1 : 0);
        }

        @Override
        public int getItemViewType(int i) {
            return i >= FilteredSearchView.this.messages.size() ? 3 : 0;
        }

        @Override
        public boolean isEnabled(RecyclerView.ViewHolder viewHolder) {
            return true;
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
            if (viewHolder.getItemViewType() == 0) {
                final DialogCell dialogCell = (DialogCell) viewHolder.itemView;
                final MessageObject messageObject = (MessageObject) FilteredSearchView.this.messages.get(i);
                dialogCell.useFromUserAsAvatar = FilteredSearchView.this.useFromUserAsAvatar;
                dialogCell.setDialog(messageObject.getDialogId(), messageObject, messageObject.messageOwner.date, false, false);
                dialogCell.useSeparator = i != getItemCount() - 1;
                final boolean z = dialogCell.getMessage() != null && dialogCell.getMessage().getId() == messageObject.getId();
                dialogCell.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                    @Override
                    public boolean onPreDraw() {
                        dialogCell.getViewTreeObserver().removeOnPreDrawListener(this);
                        if (!FilteredSearchView.this.uiCallback.actionModeShowing()) {
                            dialogCell.setChecked(false, z);
                            return true;
                        }
                        FilteredSearchView.this.messageHashIdTmp.set(messageObject.getId(), messageObject.getDialogId());
                        dialogCell.setChecked(FilteredSearchView.this.uiCallback.isSelected(FilteredSearchView.this.messageHashIdTmp), z);
                        return true;
                    }
                });
            }
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            DialogCell dialogCell;
            if (i == 0) {
                dialogCell = new DialogCell(null, viewGroup.getContext(), true, true) {
                    @Override
                    public boolean isForumCell() {
                        return false;
                    }
                };
            } else if (i != 3) {
                GraySectionCell graySectionCell = new GraySectionCell(viewGroup.getContext());
                graySectionCell.setText(LocaleController.getString(R.string.SearchMessages));
                dialogCell = graySectionCell;
            } else {
                FlickerLoadingView flickerLoadingView = new FlickerLoadingView(viewGroup.getContext());
                flickerLoadingView.setIsSingleCell(true);
                flickerLoadingView.setViewType(1);
                dialogCell = flickerLoadingView;
            }
            dialogCell.setLayoutParams(new RecyclerView.LayoutParams(-1, -2));
            return new RecyclerListView.Holder(dialogCell);
        }
    }

    public class SharedDocumentsAdapter extends RecyclerListView.SectionsAdapter {
        private int currentType;
        private Context mContext;

        public SharedDocumentsAdapter(Context context, int i) {
            this.mContext = context;
            this.currentType = i;
        }

        @Override
        public int getCountForSection(int i) {
            if (i >= FilteredSearchView.this.sections.size()) {
                return 1;
            }
            FilteredSearchView filteredSearchView = FilteredSearchView.this;
            return ((ArrayList) filteredSearchView.sectionArrays.get(filteredSearchView.sections.get(i))).size() + (i == 0 ? 0 : 1);
        }

        @Override
        public Object getItem(int i, int i2) {
            return null;
        }

        @Override
        public int getItemViewType(int i, int i2) {
            if (i >= FilteredSearchView.this.sections.size()) {
                return 2;
            }
            if (i != 0 && i2 == 0) {
                return 0;
            }
            int i3 = this.currentType;
            return (i3 == 2 || i3 == 4) ? 3 : 1;
        }

        @Override
        public String getLetter(int i) {
            return null;
        }

        @Override
        public void getPositionForScrollProgress(RecyclerListView recyclerListView, float f, int[] iArr) {
            iArr[0] = 0;
            iArr[1] = 0;
        }

        @Override
        public int getSectionCount() {
            int i = 0;
            if (FilteredSearchView.this.sections.isEmpty()) {
                return 0;
            }
            int size = FilteredSearchView.this.sections.size();
            if (!FilteredSearchView.this.sections.isEmpty() && !FilteredSearchView.this.endReached) {
                i = 1;
            }
            return size + i;
        }

        @Override
        public View getSectionHeaderView(int i, View view) {
            if (view == null) {
                view = new GraySectionCell(this.mContext);
                view.setBackgroundColor(Theme.getColor(Theme.key_graySection) & (-218103809));
            }
            if (i == 0) {
                view.setAlpha(0.0f);
                return view;
            }
            if (i < FilteredSearchView.this.sections.size()) {
                view.setAlpha(1.0f);
                ((GraySectionCell) view).setText(LocaleController.formatSectionDate(((MessageObject) ((ArrayList) FilteredSearchView.this.sectionArrays.get((String) FilteredSearchView.this.sections.get(i))).get(0)).messageOwner.date));
            }
            return view;
        }

        @Override
        public boolean isEnabled(RecyclerView.ViewHolder viewHolder, int i, int i2) {
            return i == 0 || i2 != 0;
        }

        @Override
        public void onBindViewHolder(int i, int i2, RecyclerView.ViewHolder viewHolder) {
            ViewTreeObserver viewTreeObserver;
            ViewTreeObserver.OnPreDrawListener onPreDrawListener;
            if (viewHolder.getItemViewType() != 2) {
                ArrayList arrayList = (ArrayList) FilteredSearchView.this.sectionArrays.get((String) FilteredSearchView.this.sections.get(i));
                int itemViewType = viewHolder.getItemViewType();
                boolean z = false;
                if (itemViewType == 0) {
                    ((GraySectionCell) viewHolder.itemView).setText(LocaleController.formatSectionDate(((MessageObject) arrayList.get(0)).messageOwner.date));
                    return;
                }
                if (itemViewType == 1) {
                    if (i != 0) {
                        i2--;
                    }
                    final SharedDocumentCell sharedDocumentCell = (SharedDocumentCell) viewHolder.itemView;
                    final MessageObject messageObject = (MessageObject) arrayList.get(i2);
                    final boolean z2 = sharedDocumentCell.getMessage() != null && sharedDocumentCell.getMessage().getId() == messageObject.getId();
                    if (i2 != arrayList.size() - 1 || (i == FilteredSearchView.this.sections.size() - 1 && FilteredSearchView.this.isLoading)) {
                        z = true;
                    }
                    sharedDocumentCell.setDocument(messageObject, z);
                    viewTreeObserver = sharedDocumentCell.getViewTreeObserver();
                    onPreDrawListener = new ViewTreeObserver.OnPreDrawListener() {
                        @Override
                        public boolean onPreDraw() {
                            sharedDocumentCell.getViewTreeObserver().removeOnPreDrawListener(this);
                            if (!FilteredSearchView.this.uiCallback.actionModeShowing()) {
                                sharedDocumentCell.setChecked(false, z2);
                                return true;
                            }
                            FilteredSearchView.this.messageHashIdTmp.set(messageObject.getId(), messageObject.getDialogId());
                            sharedDocumentCell.setChecked(FilteredSearchView.this.uiCallback.isSelected(FilteredSearchView.this.messageHashIdTmp), z2);
                            return true;
                        }
                    };
                } else {
                    if (itemViewType != 3) {
                        return;
                    }
                    if (i != 0) {
                        i2--;
                    }
                    final SharedAudioCell sharedAudioCell = (SharedAudioCell) viewHolder.itemView;
                    final MessageObject messageObject2 = (MessageObject) arrayList.get(i2);
                    final boolean z3 = sharedAudioCell.getMessage() != null && sharedAudioCell.getMessage().getId() == messageObject2.getId();
                    if (i2 != arrayList.size() - 1 || (i == FilteredSearchView.this.sections.size() - 1 && FilteredSearchView.this.isLoading)) {
                        z = true;
                    }
                    sharedAudioCell.setMessageObject(messageObject2, z);
                    viewTreeObserver = sharedAudioCell.getViewTreeObserver();
                    onPreDrawListener = new ViewTreeObserver.OnPreDrawListener() {
                        @Override
                        public boolean onPreDraw() {
                            sharedAudioCell.getViewTreeObserver().removeOnPreDrawListener(this);
                            if (!FilteredSearchView.this.uiCallback.actionModeShowing()) {
                                sharedAudioCell.setChecked(false, z3);
                                return true;
                            }
                            FilteredSearchView.this.messageHashIdTmp.set(messageObject2.getId(), messageObject2.getDialogId());
                            sharedAudioCell.setChecked(FilteredSearchView.this.uiCallback.isSelected(FilteredSearchView.this.messageHashIdTmp), z3);
                            return true;
                        }
                    };
                }
                viewTreeObserver.addOnPreDrawListener(onPreDrawListener);
            }
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View graySectionCell;
            SharedDocumentCell sharedDocumentCell;
            if (i != 0) {
                int i2 = 1;
                if (i == 1) {
                    sharedDocumentCell = new SharedDocumentCell(this.mContext, 2);
                } else if (i != 2) {
                    graySectionCell = new SharedAudioCell(this.mContext, i2, null) {
                        @Override
                        public boolean needPlayMessage(MessageObject messageObject) {
                            if (messageObject.isVoice() || messageObject.isRoundVideo()) {
                                boolean playMessage = MediaController.getInstance().playMessage(messageObject);
                                MediaController.getInstance().setVoiceMessagesPlaylist(playMessage ? FilteredSearchView.this.messages : null, false);
                                return playMessage;
                            }
                            if (!messageObject.isMusic()) {
                                return false;
                            }
                            String str = FilteredSearchView.this.currentDataQuery;
                            FilteredSearchView filteredSearchView = FilteredSearchView.this;
                            long j = filteredSearchView.currentSearchDialogId;
                            long j2 = filteredSearchView.currentSearchMinDate;
                            MediaController.PlaylistGlobalSearchParams playlistGlobalSearchParams = new MediaController.PlaylistGlobalSearchParams(str, j, j2, j2, filteredSearchView.currentSearchFilter);
                            playlistGlobalSearchParams.endReached = FilteredSearchView.this.endReached;
                            playlistGlobalSearchParams.nextSearchRate = FilteredSearchView.this.nextSearchRate;
                            playlistGlobalSearchParams.totalCount = FilteredSearchView.this.totalCount;
                            playlistGlobalSearchParams.folderId = FilteredSearchView.this.currentIncludeFolder ? 1 : 0;
                            return MediaController.getInstance().setPlaylist(FilteredSearchView.this.messages, messageObject, 0L, playlistGlobalSearchParams);
                        }
                    };
                } else {
                    FlickerLoadingView flickerLoadingView = new FlickerLoadingView(this.mContext);
                    int i3 = this.currentType;
                    if (i3 == 2 || i3 == 4) {
                        flickerLoadingView.setViewType(4);
                    } else {
                        flickerLoadingView.setViewType(3);
                    }
                    flickerLoadingView.setIsSingleCell(true);
                    sharedDocumentCell = flickerLoadingView;
                }
                graySectionCell = sharedDocumentCell;
            } else {
                graySectionCell = new GraySectionCell(this.mContext);
            }
            graySectionCell.setLayoutParams(new RecyclerView.LayoutParams(-1, -2));
            return new RecyclerListView.Holder(graySectionCell);
        }
    }

    public class SharedLinksAdapter extends RecyclerListView.SectionsAdapter {
        private Context mContext;
        private final SharedLinkCell.SharedLinkCellDelegate sharedLinkCellDelegate = new AnonymousClass1();

        public class AnonymousClass1 implements SharedLinkCell.SharedLinkCellDelegate {
            AnonymousClass1() {
            }

            public void lambda$onLinkPress$0(String str, DialogInterface dialogInterface, int i) {
                int i2;
                if (i == 0) {
                    FilteredSearchView.this.openUrl(str);
                    return;
                }
                if (i == 1) {
                    if (!str.startsWith("mailto:")) {
                        i2 = str.startsWith("tel:") ? 4 : 7;
                        AndroidUtilities.addToClipboard(str);
                    }
                    str = str.substring(i2);
                    AndroidUtilities.addToClipboard(str);
                }
            }

            @Override
            public boolean canPerformActions() {
                return !FilteredSearchView.this.uiCallback.actionModeShowing();
            }

            @Override
            public void needOpenWebView(TLRPC.WebPage webPage, MessageObject messageObject) {
                FilteredSearchView.this.openWebView(webPage, messageObject);
            }

            @Override
            public void onLinkPress(final String str, boolean z) {
                if (!z) {
                    FilteredSearchView.this.openUrl(str);
                    return;
                }
                BottomSheet.Builder builder = new BottomSheet.Builder(FilteredSearchView.this.parentActivity);
                builder.setTitle(str);
                builder.setItems(new CharSequence[]{LocaleController.getString(R.string.Open), LocaleController.getString(R.string.Copy)}, new DialogInterface.OnClickListener() {
                    @Override
                    public final void onClick(DialogInterface dialogInterface, int i) {
                        FilteredSearchView.SharedLinksAdapter.AnonymousClass1.this.lambda$onLinkPress$0(str, dialogInterface, i);
                    }
                });
                FilteredSearchView.this.parentFragment.showDialog(builder.create());
            }
        }

        public SharedLinksAdapter(Context context) {
            this.mContext = context;
        }

        @Override
        public int getCountForSection(int i) {
            if (i >= FilteredSearchView.this.sections.size()) {
                return 1;
            }
            FilteredSearchView filteredSearchView = FilteredSearchView.this;
            return ((ArrayList) filteredSearchView.sectionArrays.get(filteredSearchView.sections.get(i))).size() + (i == 0 ? 0 : 1);
        }

        @Override
        public Object getItem(int i, int i2) {
            return null;
        }

        @Override
        public int getItemViewType(int i, int i2) {
            if (i < FilteredSearchView.this.sections.size()) {
                return (i == 0 || i2 != 0) ? 1 : 0;
            }
            return 2;
        }

        @Override
        public String getLetter(int i) {
            return null;
        }

        @Override
        public void getPositionForScrollProgress(RecyclerListView recyclerListView, float f, int[] iArr) {
            iArr[0] = 0;
            iArr[1] = 0;
        }

        @Override
        public int getSectionCount() {
            int i = 0;
            if (FilteredSearchView.this.messages.isEmpty()) {
                return 0;
            }
            if (FilteredSearchView.this.sections.isEmpty() && FilteredSearchView.this.isLoading) {
                return 0;
            }
            int size = FilteredSearchView.this.sections.size();
            if (!FilteredSearchView.this.sections.isEmpty() && !FilteredSearchView.this.endReached) {
                i = 1;
            }
            return size + i;
        }

        @Override
        public View getSectionHeaderView(int i, View view) {
            if (view == null) {
                view = new GraySectionCell(this.mContext);
                view.setBackgroundColor(Theme.getColor(Theme.key_graySection) & (-218103809));
            }
            if (i == 0) {
                view.setAlpha(0.0f);
                return view;
            }
            if (i < FilteredSearchView.this.sections.size()) {
                view.setAlpha(1.0f);
                ((GraySectionCell) view).setText(LocaleController.formatSectionDate(((MessageObject) ((ArrayList) FilteredSearchView.this.sectionArrays.get((String) FilteredSearchView.this.sections.get(i))).get(0)).messageOwner.date));
            }
            return view;
        }

        @Override
        public boolean isEnabled(RecyclerView.ViewHolder viewHolder, int i, int i2) {
            return true;
        }

        @Override
        public void onBindViewHolder(int i, int i2, RecyclerView.ViewHolder viewHolder) {
            if (viewHolder.getItemViewType() != 2) {
                ArrayList arrayList = (ArrayList) FilteredSearchView.this.sectionArrays.get((String) FilteredSearchView.this.sections.get(i));
                int itemViewType = viewHolder.getItemViewType();
                boolean z = false;
                if (itemViewType == 0) {
                    ((GraySectionCell) viewHolder.itemView).setText(LocaleController.formatSectionDate(((MessageObject) arrayList.get(0)).messageOwner.date));
                    return;
                }
                if (itemViewType != 1) {
                    return;
                }
                if (i != 0) {
                    i2--;
                }
                final SharedLinkCell sharedLinkCell = (SharedLinkCell) viewHolder.itemView;
                final MessageObject messageObject = (MessageObject) arrayList.get(i2);
                final boolean z2 = sharedLinkCell.getMessage() != null && sharedLinkCell.getMessage().getId() == messageObject.getId();
                if (i2 != arrayList.size() - 1 || (i == FilteredSearchView.this.sections.size() - 1 && FilteredSearchView.this.isLoading)) {
                    z = true;
                }
                sharedLinkCell.setLink(messageObject, z);
                sharedLinkCell.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                    @Override
                    public boolean onPreDraw() {
                        sharedLinkCell.getViewTreeObserver().removeOnPreDrawListener(this);
                        if (!FilteredSearchView.this.uiCallback.actionModeShowing()) {
                            sharedLinkCell.setChecked(false, z2);
                            return true;
                        }
                        FilteredSearchView.this.messageHashIdTmp.set(messageObject.getId(), messageObject.getDialogId());
                        sharedLinkCell.setChecked(FilteredSearchView.this.uiCallback.isSelected(FilteredSearchView.this.messageHashIdTmp), z2);
                        return true;
                    }
                });
            }
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            GraySectionCell graySectionCell;
            if (i == 0) {
                graySectionCell = new GraySectionCell(this.mContext);
            } else if (i != 1) {
                FlickerLoadingView flickerLoadingView = new FlickerLoadingView(this.mContext);
                flickerLoadingView.setViewType(5);
                flickerLoadingView.setIsSingleCell(true);
                graySectionCell = flickerLoadingView;
            } else {
                SharedLinkCell sharedLinkCell = new SharedLinkCell(this.mContext, 1);
                sharedLinkCell.setDelegate(this.sharedLinkCellDelegate);
                graySectionCell = sharedLinkCell;
            }
            graySectionCell.setLayoutParams(new RecyclerView.LayoutParams(-1, -2));
            return new RecyclerListView.Holder(graySectionCell);
        }
    }

    public class SharedPhotoVideoAdapter extends RecyclerListView.SelectionAdapter {
        private Context mContext;

        public SharedPhotoVideoAdapter(Context context) {
            this.mContext = context;
        }

        @Override
        public int getItemCount() {
            if (FilteredSearchView.this.messages.isEmpty()) {
                return 0;
            }
            return ((int) Math.ceil(FilteredSearchView.this.messages.size() / FilteredSearchView.this.columnsCount)) + (!FilteredSearchView.this.endReached ? 1 : 0);
        }

        @Override
        public int getItemViewType(int i) {
            return i < ((int) Math.ceil((double) (((float) FilteredSearchView.this.messages.size()) / ((float) FilteredSearchView.this.columnsCount)))) ? 0 : 1;
        }

        @Override
        public boolean isEnabled(RecyclerView.ViewHolder viewHolder) {
            return false;
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
            if (viewHolder.getItemViewType() != 0) {
                if (viewHolder.getItemViewType() != 3) {
                    if (viewHolder.getItemViewType() == 1) {
                        ((FlickerLoadingView) viewHolder.itemView).skipDrawItemsCount(FilteredSearchView.this.columnsCount - ((FilteredSearchView.this.columnsCount * ((int) Math.ceil(FilteredSearchView.this.messages.size() / FilteredSearchView.this.columnsCount))) - FilteredSearchView.this.messages.size()));
                        return;
                    }
                    return;
                }
                DialogCell dialogCell = (DialogCell) viewHolder.itemView;
                dialogCell.useSeparator = i != getItemCount() - 1;
                MessageObject messageObject = (MessageObject) FilteredSearchView.this.messages.get(i);
                boolean z = dialogCell.getMessage() != null && dialogCell.getMessage().getId() == messageObject.getId();
                dialogCell.useFromUserAsAvatar = FilteredSearchView.this.useFromUserAsAvatar;
                dialogCell.setDialog(messageObject.getDialogId(), messageObject, messageObject.messageOwner.date, false, false);
                if (!FilteredSearchView.this.uiCallback.actionModeShowing()) {
                    dialogCell.setChecked(false, z);
                    return;
                } else {
                    FilteredSearchView.this.messageHashIdTmp.set(messageObject.getId(), messageObject.getDialogId());
                    dialogCell.setChecked(FilteredSearchView.this.uiCallback.isSelected(FilteredSearchView.this.messageHashIdTmp), z);
                    return;
                }
            }
            FilteredSearchView filteredSearchView = FilteredSearchView.this;
            ArrayList arrayList = filteredSearchView.messages;
            SharedPhotoVideoCell sharedPhotoVideoCell = (SharedPhotoVideoCell) viewHolder.itemView;
            sharedPhotoVideoCell.setItemsCount(filteredSearchView.columnsCount);
            sharedPhotoVideoCell.setIsFirst(i == 0);
            for (int i2 = 0; i2 < FilteredSearchView.this.columnsCount; i2++) {
                int i3 = (FilteredSearchView.this.columnsCount * i) + i2;
                if (i3 < arrayList.size()) {
                    MessageObject messageObject2 = (MessageObject) arrayList.get(i3);
                    sharedPhotoVideoCell.setItem(i2, FilteredSearchView.this.messages.indexOf(messageObject2), messageObject2);
                    if (FilteredSearchView.this.uiCallback.actionModeShowing()) {
                        FilteredSearchView.this.messageHashIdTmp.set(messageObject2.getId(), messageObject2.getDialogId());
                        sharedPhotoVideoCell.setChecked(i2, FilteredSearchView.this.uiCallback.isSelected(FilteredSearchView.this.messageHashIdTmp), true);
                    } else {
                        sharedPhotoVideoCell.setChecked(i2, false, true);
                    }
                } else {
                    sharedPhotoVideoCell.setItem(i2, i3, null);
                }
            }
            sharedPhotoVideoCell.requestLayout();
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            FrameLayout frameLayout;
            if (i == 0) {
                SharedPhotoVideoCell sharedPhotoVideoCell = new SharedPhotoVideoCell(this.mContext, 1);
                sharedPhotoVideoCell.setDelegate(new SharedPhotoVideoCell.SharedPhotoVideoCellDelegate() {
                    @Override
                    public void didClickItem(SharedPhotoVideoCell sharedPhotoVideoCell2, int i2, MessageObject messageObject, int i3) {
                        FilteredSearchView.this.onItemClick(i2, sharedPhotoVideoCell2, messageObject, i3);
                    }

                    @Override
                    public boolean didLongClickItem(SharedPhotoVideoCell sharedPhotoVideoCell2, int i2, MessageObject messageObject, int i3) {
                        if (!FilteredSearchView.this.uiCallback.actionModeShowing()) {
                            return FilteredSearchView.this.onItemLongClick(messageObject, sharedPhotoVideoCell2, i3);
                        }
                        didClickItem(sharedPhotoVideoCell2, i2, messageObject, i3);
                        return true;
                    }
                });
                frameLayout = sharedPhotoVideoCell;
            } else if (i != 2) {
                FlickerLoadingView flickerLoadingView = new FlickerLoadingView(this.mContext) {
                    @Override
                    public int getColumnsCount() {
                        return FilteredSearchView.this.columnsCount;
                    }
                };
                flickerLoadingView.setIsSingleCell(true);
                flickerLoadingView.setViewType(2);
                frameLayout = flickerLoadingView;
            } else {
                FrameLayout graySectionCell = new GraySectionCell(this.mContext);
                graySectionCell.setBackgroundColor(Theme.getColor(Theme.key_graySection) & (-218103809));
                frameLayout = graySectionCell;
            }
            frameLayout.setLayoutParams(new RecyclerView.LayoutParams(-1, -2));
            return new RecyclerListView.Holder(frameLayout);
        }
    }

    public interface UiCallback {
        boolean actionModeShowing();

        void goToMessage(MessageObject messageObject);

        boolean isSelected(MessageHashId messageHashId);

        void showActionMode();

        void toggleItemSelection(MessageObject messageObject, View view, int i);
    }

    public FilteredSearchView(BaseFragment baseFragment) {
        super(baseFragment.getParentActivity());
        this.messages = new ArrayList();
        this.messagesById = new SparseArray();
        this.sections = new ArrayList();
        this.sectionArrays = new HashMap();
        this.columnsCount = 3;
        this.messageHashIdTmp = new MessageHashId(0, 0L);
        this.localTipChats = new ArrayList();
        this.localTipDates = new ArrayList();
        this.clearCurrentResultsRunnable = new Runnable() {
            @Override
            public void run() {
                if (FilteredSearchView.this.isLoading) {
                    FilteredSearchView.this.messages.clear();
                    FilteredSearchView.this.sections.clear();
                    FilteredSearchView.this.sectionArrays.clear();
                    RecyclerView.Adapter adapter = FilteredSearchView.this.adapter;
                    if (adapter != null) {
                        adapter.notifyDataSetChanged();
                    }
                }
            }
        };
        this.provider = new PhotoViewer.EmptyPhotoViewerProvider() {
            @Override
            public PhotoViewer.PlaceProviderObject getPlaceForPhoto(MessageObject messageObject, TLRPC.FileLocation fileLocation, int i, boolean z) {
                ImageReceiver photoImage;
                ContextLinkCell contextLinkCell;
                View pinnedHeader;
                int height;
                MessageObject messageObject2;
                if (messageObject == null) {
                    return null;
                }
                RecyclerListView recyclerListView = FilteredSearchView.this.recyclerListView;
                int childCount = recyclerListView.getChildCount();
                for (int i2 = 0; i2 < childCount; i2++) {
                    View childAt = recyclerListView.getChildAt(i2);
                    int[] iArr = new int[2];
                    if (childAt instanceof SharedPhotoVideoCell) {
                        SharedPhotoVideoCell sharedPhotoVideoCell = (SharedPhotoVideoCell) childAt;
                        photoImage = null;
                        for (int i3 = 0; i3 < 6 && (messageObject2 = sharedPhotoVideoCell.getMessageObject(i3)) != null; i3++) {
                            if (messageObject2.getId() == messageObject.getId()) {
                                BackupImageView imageView = sharedPhotoVideoCell.getImageView(i3);
                                ImageReceiver imageReceiver = imageView.getImageReceiver();
                                imageView.getLocationInWindow(iArr);
                                photoImage = imageReceiver;
                            }
                        }
                    } else if (childAt instanceof SharedDocumentCell) {
                        SharedDocumentCell sharedDocumentCell = (SharedDocumentCell) childAt;
                        if (sharedDocumentCell.getMessage().getId() == messageObject.getId()) {
                            BackupImageView imageView2 = sharedDocumentCell.getImageView();
                            photoImage = imageView2.getImageReceiver();
                            contextLinkCell = imageView2;
                            contextLinkCell.getLocationInWindow(iArr);
                        }
                        photoImage = null;
                    } else {
                        if (childAt instanceof ContextLinkCell) {
                            ContextLinkCell contextLinkCell2 = (ContextLinkCell) childAt;
                            MessageObject messageObject3 = (MessageObject) contextLinkCell2.getParentObject();
                            if (messageObject3 != null && messageObject3.getId() == messageObject.getId()) {
                                photoImage = contextLinkCell2.getPhotoImage();
                                contextLinkCell = contextLinkCell2;
                                contextLinkCell.getLocationInWindow(iArr);
                            }
                        }
                        photoImage = null;
                    }
                    if (photoImage != null) {
                        PhotoViewer.PlaceProviderObject placeProviderObject = new PhotoViewer.PlaceProviderObject();
                        placeProviderObject.viewX = iArr[0];
                        placeProviderObject.viewY = iArr[1] - (Build.VERSION.SDK_INT >= 21 ? 0 : AndroidUtilities.statusBarHeight);
                        placeProviderObject.parentView = recyclerListView;
                        recyclerListView.getLocationInWindow(iArr);
                        placeProviderObject.animatingImageViewYOffset = -iArr[1];
                        placeProviderObject.imageReceiver = photoImage;
                        placeProviderObject.allowTakeAnimation = false;
                        placeProviderObject.radius = photoImage.getRoundRadius(true);
                        placeProviderObject.thumb = placeProviderObject.imageReceiver.getBitmapSafe();
                        placeProviderObject.parentView.getLocationInWindow(iArr);
                        placeProviderObject.clipTopAddition = 0;
                        if (PhotoViewer.isShowingImage(messageObject) && (pinnedHeader = recyclerListView.getPinnedHeader()) != null) {
                            int dp = (childAt instanceof SharedDocumentCell ? AndroidUtilities.dp(8.0f) : 0) - placeProviderObject.viewY;
                            if (dp > childAt.getHeight()) {
                                height = -(dp + pinnedHeader.getHeight());
                            } else {
                                int height2 = placeProviderObject.viewY - recyclerListView.getHeight();
                                if (childAt instanceof SharedDocumentCell) {
                                    height2 -= AndroidUtilities.dp(8.0f);
                                }
                                if (height2 >= 0) {
                                    height = height2 + childAt.getHeight();
                                }
                            }
                            recyclerListView.scrollBy(0, height);
                        }
                        return placeProviderObject;
                    }
                }
                return null;
            }

            @Override
            public int getTotalImageCount() {
                return FilteredSearchView.this.totalCount;
            }

            @Override
            public boolean loadMore() {
                if (FilteredSearchView.this.endReached) {
                    return true;
                }
                FilteredSearchView filteredSearchView = FilteredSearchView.this;
                filteredSearchView.search(filteredSearchView.currentSearchDialogId, filteredSearchView.currentSearchMinDate, filteredSearchView.currentSearchMaxDate, filteredSearchView.currentSearchFilter, filteredSearchView.currentIncludeFolder, filteredSearchView.lastMessagesSearchString, false);
                return true;
            }
        };
        this.firstLoading = true;
        this.notificationsLocker = new AnimationNotificationsLocker();
        this.hideFloatingDateRunnable = new Runnable() {
            @Override
            public final void run() {
                FilteredSearchView.this.lambda$new$0();
            }
        };
        this.parentFragment = baseFragment;
        Activity parentActivity = baseFragment.getParentActivity();
        this.parentActivity = parentActivity;
        setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
        BlurredRecyclerView blurredRecyclerView = new BlurredRecyclerView(parentActivity) {
            @Override
            public void dispatchDraw(Canvas canvas) {
                if (getAdapter() == FilteredSearchView.this.sharedPhotoVideoAdapter) {
                    for (int i = 0; i < getChildCount(); i++) {
                        if (getChildViewHolder(getChildAt(i)).getItemViewType() == 1) {
                            canvas.save();
                            canvas.translate(getChildAt(i).getX(), (getChildAt(i).getY() - getChildAt(i).getMeasuredHeight()) + AndroidUtilities.dp(2.0f));
                            getChildAt(i).draw(canvas);
                            canvas.restore();
                            invalidate();
                        }
                    }
                }
                super.dispatchDraw(canvas);
            }

            @Override
            public boolean drawChild(Canvas canvas, View view, long j) {
                if (getAdapter() == FilteredSearchView.this.sharedPhotoVideoAdapter && getChildViewHolder(view).getItemViewType() == 1) {
                    return true;
                }
                return super.drawChild(canvas, view, j);
            }
        };
        this.recyclerListView = blurredRecyclerView;
        blurredRecyclerView.setOnItemClickListener(new RecyclerListView.OnItemClickListener() {
            @Override
            public final void onItemClick(View view, int i) {
                FilteredSearchView.this.lambda$new$1(view, i);
            }
        });
        this.recyclerListView.setOnItemLongClickListener(new RecyclerListView.OnItemLongClickListenerExtended() {
            @Override
            public boolean onItemClick(View view, int i, float f, float f2) {
                FilteredSearchView filteredSearchView;
                MessageObject message;
                if (view instanceof SharedDocumentCell) {
                    filteredSearchView = FilteredSearchView.this;
                    message = ((SharedDocumentCell) view).getMessage();
                } else if (view instanceof SharedLinkCell) {
                    filteredSearchView = FilteredSearchView.this;
                    message = ((SharedLinkCell) view).getMessage();
                } else if (view instanceof SharedAudioCell) {
                    filteredSearchView = FilteredSearchView.this;
                    message = ((SharedAudioCell) view).getMessage();
                } else {
                    if (!(view instanceof ContextLinkCell)) {
                        if (view instanceof DialogCell) {
                            if (!FilteredSearchView.this.uiCallback.actionModeShowing()) {
                                DialogCell dialogCell = (DialogCell) view;
                                if (dialogCell.isPointInsideAvatar(f, f2)) {
                                    FilteredSearchView.this.chatPreviewDelegate.startChatPreview(FilteredSearchView.this.recyclerListView, dialogCell);
                                    return true;
                                }
                            }
                            filteredSearchView = FilteredSearchView.this;
                            message = ((DialogCell) view).getMessage();
                        }
                        return true;
                    }
                    filteredSearchView = FilteredSearchView.this;
                    message = ((ContextLinkCell) view).getMessageObject();
                }
                filteredSearchView.onItemLongClick(message, view, 0);
                return true;
            }

            @Override
            public void onLongClickRelease() {
                FilteredSearchView.this.chatPreviewDelegate.finish();
            }

            @Override
            public void onMove(float f, float f2) {
                FilteredSearchView.this.chatPreviewDelegate.move(f2);
            }
        });
        this.recyclerListView.setPadding(0, 0, 0, AndroidUtilities.dp(3.0f));
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(parentActivity);
        this.layoutManager = linearLayoutManager;
        this.recyclerListView.setLayoutManager(linearLayoutManager);
        FlickerLoadingView flickerLoadingView = new FlickerLoadingView(parentActivity) {
            @Override
            public int getColumnsCount() {
                return FilteredSearchView.this.columnsCount;
            }
        };
        this.loadingView = flickerLoadingView;
        addView(flickerLoadingView);
        addView(this.recyclerListView);
        this.recyclerListView.setSectionsType(2);
        this.recyclerListView.setOnScrollListener(new AnonymousClass6());
        ChatActionCell chatActionCell = new ChatActionCell(parentActivity);
        this.floatingDateView = chatActionCell;
        chatActionCell.setCustomDate((int) (System.currentTimeMillis() / 1000), false, false);
        chatActionCell.setAlpha(0.0f);
        chatActionCell.setOverrideColor(Theme.key_chat_mediaTimeBackground, Theme.key_chat_mediaTimeText);
        chatActionCell.setTranslationY(-AndroidUtilities.dp(48.0f));
        addView(chatActionCell, LayoutHelper.createFrame(-2, -2.0f, 49, 0.0f, 4.0f, 0.0f, 0.0f));
        this.dialogsAdapter = new OnlyUserFiltersAdapter();
        this.sharedPhotoVideoAdapter = new SharedPhotoVideoAdapter(getContext());
        this.sharedDocumentsAdapter = new SharedDocumentsAdapter(getContext(), 1);
        this.sharedLinksAdapter = new SharedLinksAdapter(getContext());
        this.sharedAudioAdapter = new SharedDocumentsAdapter(getContext(), 4);
        this.sharedVoiceAdapter = new SharedDocumentsAdapter(getContext(), 2);
        StickerEmptyView stickerEmptyView = new StickerEmptyView(parentActivity, flickerLoadingView, 1);
        this.emptyView = stickerEmptyView;
        addView(stickerEmptyView);
        this.recyclerListView.setEmptyView(this.emptyView);
        this.emptyView.setVisibility(8);
    }

    public static CharSequence createFromInfoString(MessageObject messageObject, int i) {
        return createFromInfoString(messageObject, true, i);
    }

    public static CharSequence createFromInfoString(MessageObject messageObject, boolean z, int i) {
        return createFromInfoString(messageObject, z, i, null);
    }

    public static CharSequence createFromInfoString(MessageObject messageObject, boolean z, int i, TextPaint textPaint) {
        TLRPC.Chat chat;
        TLRPC.Chat chat2;
        TLRPC.User user;
        TLRPC.TL_forumTopic findTopic;
        TLRPC.TL_forumTopic findTopic2;
        int i2;
        if (messageObject == null || messageObject.messageOwner == null) {
            return "";
        }
        if (messageObject.isQuickReply()) {
            QuickRepliesController.QuickReply findReply = QuickRepliesController.getInstance(messageObject.currentAccount).findReply(messageObject.getQuickReplyId());
            return findReply == null ? "" : findReply.name;
        }
        if (messageObject.isSponsored()) {
            return LocaleController.getString(messageObject.sponsoredCanReport ? R.string.SponsoredMessageAd : messageObject.sponsoredRecommended ? R.string.SponsoredMessage2Recommended : R.string.SponsoredMessage2);
        }
        SpannableStringBuilder[] spannableStringBuilderArr = arrowSpan;
        if (spannableStringBuilderArr[i] == null) {
            spannableStringBuilderArr[i] = new SpannableStringBuilder(">");
            if (i == 0) {
                i2 = R.drawable.attach_arrow_right;
            } else if (i == 1) {
                i2 = R.drawable.msg_mini_arrow_mediathin;
            } else {
                if (i != 2) {
                    return "";
                }
                i2 = R.drawable.msg_mini_arrow_mediabold;
            }
            ColoredImageSpan coloredImageSpan = new ColoredImageSpan(ContextCompat.getDrawable(ApplicationLoader.applicationContext, i2).mutate(), i == 0 ? 2 : 1);
            if (i == 1 || i == 2) {
                coloredImageSpan.setScale(0.85f);
            }
            SpannableStringBuilder spannableStringBuilder = arrowSpan[i];
            spannableStringBuilder.setSpan(coloredImageSpan, 0, spannableStringBuilder.length(), 0);
        }
        TLRPC.Message message = messageObject.messageOwner;
        CharSequence charSequence = null;
        if (message.saved_peer_id != null) {
            if (messageObject.getSavedDialogId() >= 0) {
                user = MessagesController.getInstance(UserConfig.selectedAccount).getUser(Long.valueOf(messageObject.getSavedDialogId()));
                chat = null;
            } else if (messageObject.getSavedDialogId() < 0) {
                chat = MessagesController.getInstance(UserConfig.selectedAccount).getChat(Long.valueOf(-messageObject.getSavedDialogId()));
                user = null;
                chat2 = null;
            } else {
                user = null;
                chat = null;
            }
            chat2 = chat;
        } else {
            TLRPC.User user2 = message.from_id.user_id != 0 ? MessagesController.getInstance(UserConfig.selectedAccount).getUser(Long.valueOf(messageObject.messageOwner.from_id.user_id)) : null;
            chat = messageObject.messageOwner.from_id.chat_id != 0 ? MessagesController.getInstance(UserConfig.selectedAccount).getChat(Long.valueOf(messageObject.messageOwner.peer_id.chat_id)) : null;
            if (chat == null) {
                chat = messageObject.messageOwner.from_id.channel_id != 0 ? MessagesController.getInstance(UserConfig.selectedAccount).getChat(Long.valueOf(messageObject.messageOwner.peer_id.channel_id)) : null;
            }
            chat2 = messageObject.messageOwner.peer_id.channel_id != 0 ? MessagesController.getInstance(UserConfig.selectedAccount).getChat(Long.valueOf(messageObject.messageOwner.peer_id.channel_id)) : null;
            if (chat2 == null) {
                chat2 = messageObject.messageOwner.peer_id.chat_id != 0 ? MessagesController.getInstance(UserConfig.selectedAccount).getChat(Long.valueOf(messageObject.messageOwner.peer_id.chat_id)) : null;
            }
            if (ChatObject.isChannelAndNotMegaGroup(chat2) || z) {
                user = user2;
            } else {
                user = user2;
                chat2 = null;
            }
        }
        if (user != null && chat2 != null) {
            CharSequence charSequence2 = chat2.title;
            if (ChatObject.isForum(chat2) && (findTopic2 = MessagesController.getInstance(UserConfig.selectedAccount).getTopicsController().findTopic(chat2.id, MessageObject.getTopicId(messageObject.currentAccount, messageObject.messageOwner, true))) != null) {
                charSequence2 = ForumUtilities.getTopicSpannedName(findTopic2, null, false);
            }
            CharSequence replaceEmoji = Emoji.replaceEmoji(charSequence2, textPaint == null ? null : textPaint.getFontMetricsInt(), false);
            SpannableStringBuilder spannableStringBuilder2 = new SpannableStringBuilder();
            spannableStringBuilder2.append(Emoji.replaceEmoji(UserObject.getFirstName(user), textPaint != null ? textPaint.getFontMetricsInt() : null, false)).append((char) 8202).append((CharSequence) arrowSpan[i]).append((char) 8202).append(replaceEmoji);
            charSequence = spannableStringBuilder2;
        } else if (user != null) {
            charSequence = Emoji.replaceEmoji(UserObject.getUserName(user), textPaint != null ? textPaint.getFontMetricsInt() : null, false);
        } else if (chat != null) {
            CharSequence charSequence3 = chat.title;
            if (ChatObject.isForum(chat) && (findTopic = MessagesController.getInstance(UserConfig.selectedAccount).getTopicsController().findTopic(chat.id, MessageObject.getTopicId(messageObject.currentAccount, messageObject.messageOwner, true))) != null) {
                charSequence3 = ForumUtilities.getTopicSpannedName(findTopic, null, false);
            }
            charSequence = Emoji.replaceEmoji(charSequence3, textPaint != null ? textPaint.getFontMetricsInt() : null, false);
        }
        return charSequence == null ? "" : charSequence;
    }

    private void hideFloatingDateView(boolean z) {
        AndroidUtilities.cancelRunOnUIThread(this.hideFloatingDateRunnable);
        if (this.floatingDateView.getTag() == null) {
            return;
        }
        this.floatingDateView.setTag(null);
        AnimatorSet animatorSet = this.floatingDateAnimation;
        if (animatorSet != null) {
            animatorSet.cancel();
            this.floatingDateAnimation = null;
        }
        if (!z) {
            this.floatingDateView.setAlpha(0.0f);
            return;
        }
        AnimatorSet animatorSet2 = new AnimatorSet();
        this.floatingDateAnimation = animatorSet2;
        animatorSet2.setDuration(180L);
        this.floatingDateAnimation.playTogether(ObjectAnimator.ofFloat(this.floatingDateView, (Property<ChatActionCell, Float>) View.ALPHA, 0.0f), ObjectAnimator.ofFloat(this.floatingDateView, (Property<ChatActionCell, Float>) View.TRANSLATION_Y, -AndroidUtilities.dp(48.0f)));
        this.floatingDateAnimation.setInterpolator(CubicBezierInterpolator.EASE_OUT);
        this.floatingDateAnimation.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animator) {
                FilteredSearchView.this.floatingDateAnimation = null;
            }
        });
        this.floatingDateAnimation.start();
    }

    public void lambda$new$0() {
        hideFloatingDateView(true);
    }

    public void lambda$new$1(View view, int i) {
        MessageObject message;
        if (view instanceof SharedDocumentCell) {
            message = ((SharedDocumentCell) view).getMessage();
        } else if (view instanceof SharedLinkCell) {
            message = ((SharedLinkCell) view).getMessage();
        } else if (view instanceof SharedAudioCell) {
            message = ((SharedAudioCell) view).getMessage();
        } else if (view instanceof ContextLinkCell) {
            message = ((ContextLinkCell) view).getMessageObject();
        } else if (!(view instanceof DialogCell)) {
            return;
        } else {
            message = ((DialogCell) view).getMessage();
        }
        onItemClick(i, view, message, 0);
    }

    public void lambda$search$2(int r14, org.telegram.tgnet.TLRPC.TL_error r15, org.telegram.tgnet.TLObject r16, int r17, boolean r18, java.lang.String r19, java.util.ArrayList r20, org.telegram.ui.Adapters.FiltersView.MediaFilterData r21, long r22, long r24, java.util.ArrayList r26, java.util.ArrayList r27) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.FilteredSearchView.lambda$search$2(int, org.telegram.tgnet.TLRPC$TL_error, org.telegram.tgnet.TLObject, int, boolean, java.lang.String, java.util.ArrayList, org.telegram.ui.Adapters.FiltersView$MediaFilterData, long, long, java.util.ArrayList, java.util.ArrayList):void");
    }

    public void lambda$search$3(final int i, final String str, final int i2, final boolean z, final FiltersView.MediaFilterData mediaFilterData, final long j, final long j2, final ArrayList arrayList, final ArrayList arrayList2, final TLObject tLObject, final TLRPC.TL_error tL_error) {
        final ArrayList arrayList3 = new ArrayList();
        if (tL_error == null) {
            TLRPC.messages_Messages messages_messages = (TLRPC.messages_Messages) tLObject;
            int size = messages_messages.messages.size();
            for (int i3 = 0; i3 < size; i3++) {
                MessageObject messageObject = new MessageObject(i, messages_messages.messages.get(i3), false, true);
                messageObject.setQuery(str);
                arrayList3.add(messageObject);
            }
        }
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                FilteredSearchView.this.lambda$search$2(i2, tL_error, tLObject, i, z, str, arrayList3, mediaFilterData, j, j2, arrayList, arrayList2);
            }
        });
    }

    public void lambda$search$4(final long j, final String str, final FiltersView.MediaFilterData mediaFilterData, final int i, final long j2, long j3, final boolean z, boolean z2, String str2, final int i2) {
        TLRPC.InputPeer tL_inputPeerEmpty;
        TLRPC.TL_messages_searchGlobal tL_messages_searchGlobal;
        ArrayList<Object> arrayList = null;
        if (j != 0) {
            TLRPC.TL_messages_search tL_messages_search = new TLRPC.TL_messages_search();
            tL_messages_search.q = str;
            tL_messages_search.limit = 20;
            tL_messages_search.filter = mediaFilterData == null ? new TLRPC.TL_inputMessagesFilterEmpty() : mediaFilterData.filter;
            tL_messages_search.peer = AccountInstance.getInstance(i).getMessagesController().getInputPeer(j);
            if (j2 > 0) {
                tL_messages_search.min_date = (int) (j2 / 1000);
            }
            if (j3 > 0) {
                tL_messages_search.max_date = (int) (j3 / 1000);
            }
            if (z && str.equals(this.lastMessagesSearchString) && !this.messages.isEmpty()) {
                tL_messages_search.offset_id = ((MessageObject) this.messages.get(r0.size() - 1)).getId();
                tL_messages_searchGlobal = tL_messages_search;
            } else {
                tL_messages_search.offset_id = 0;
                tL_messages_searchGlobal = tL_messages_search;
            }
        } else {
            if (!TextUtils.isEmpty(str)) {
                arrayList = new ArrayList<>();
                MessagesStorage.getInstance(i).localSearch(0, str, arrayList, new ArrayList<>(), new ArrayList<>(), null, z2 ? 1 : 0);
            }
            TLRPC.TL_messages_searchGlobal tL_messages_searchGlobal2 = new TLRPC.TL_messages_searchGlobal();
            tL_messages_searchGlobal2.limit = 20;
            tL_messages_searchGlobal2.q = str;
            tL_messages_searchGlobal2.filter = mediaFilterData == null ? new TLRPC.TL_inputMessagesFilterEmpty() : mediaFilterData.filter;
            if (j2 > 0) {
                tL_messages_searchGlobal2.min_date = (int) (j2 / 1000);
            }
            if (j3 > 0) {
                tL_messages_searchGlobal2.max_date = (int) (j3 / 1000);
            }
            if (z && str.equals(this.lastMessagesSearchString) && !this.messages.isEmpty()) {
                MessageObject messageObject = (MessageObject) this.messages.get(r0.size() - 1);
                tL_messages_searchGlobal2.offset_id = messageObject.getId();
                tL_messages_searchGlobal2.offset_rate = this.nextSearchRate;
                tL_inputPeerEmpty = MessagesController.getInstance(i).getInputPeer(MessageObject.getPeerId(messageObject.messageOwner.peer_id));
            } else {
                tL_messages_searchGlobal2.offset_rate = 0;
                tL_messages_searchGlobal2.offset_id = 0;
                tL_inputPeerEmpty = new TLRPC.TL_inputPeerEmpty();
            }
            tL_messages_searchGlobal2.offset_peer = tL_inputPeerEmpty;
            tL_messages_searchGlobal2.flags |= 1;
            tL_messages_searchGlobal2.folder_id = z2 ? 1 : 0;
            tL_messages_searchGlobal = tL_messages_searchGlobal2;
        }
        TLRPC.TL_messages_searchGlobal tL_messages_searchGlobal3 = tL_messages_searchGlobal;
        final ArrayList<Object> arrayList2 = arrayList;
        this.lastMessagesSearchString = str;
        this.lastSearchFilterQueryString = str2;
        final ArrayList arrayList3 = new ArrayList();
        FiltersView.fillTipDates(this.lastMessagesSearchString, arrayList3);
        ConnectionsManager.getInstance(i).sendRequest(tL_messages_searchGlobal3, new RequestDelegate() {
            @Override
            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                FilteredSearchView.this.lambda$search$3(i, str, i2, z, mediaFilterData, j, j2, arrayList2, arrayList3, tLObject, tL_error);
            }
        });
    }

    public void onItemClick(int i, View view, MessageObject messageObject, int i2) {
        if (messageObject == null) {
            return;
        }
        if (this.uiCallback.actionModeShowing()) {
            this.uiCallback.toggleItemSelection(messageObject, view, i2);
            return;
        }
        if (view instanceof DialogCell) {
            this.uiCallback.goToMessage(messageObject);
            return;
        }
        int i3 = this.currentSearchFilter.filterType;
        if (i3 == 0) {
            PhotoViewer.getInstance().setParentActivity(this.parentFragment);
            PhotoViewer.getInstance().openPhoto(this.messages, i, 0L, 0L, 0L, this.provider);
            this.photoViewerClassGuid = PhotoViewer.getInstance().getClassGuid();
            return;
        }
        if (i3 == 3 || i3 == 5) {
            if (view instanceof SharedAudioCell) {
                ((SharedAudioCell) view).didPressedButton();
                return;
            }
            return;
        }
        if (i3 == 1) {
            if (view instanceof SharedDocumentCell) {
                SharedDocumentCell sharedDocumentCell = (SharedDocumentCell) view;
                TLRPC.Document document = messageObject.getDocument();
                if (!sharedDocumentCell.isLoaded()) {
                    if (sharedDocumentCell.isLoading()) {
                        AccountInstance.getInstance(UserConfig.selectedAccount).getFileLoader().cancelLoadFile(document);
                    } else {
                        MessageObject message = sharedDocumentCell.getMessage();
                        message.putInDownloadsStore = true;
                        AccountInstance.getInstance(UserConfig.selectedAccount).getFileLoader().loadFile(document, message, 0, 0);
                    }
                    sharedDocumentCell.updateFileExistIcon(true);
                    return;
                }
                if (!messageObject.canPreviewDocument()) {
                    AndroidUtilities.openDocument(messageObject, this.parentActivity, this.parentFragment);
                    return;
                }
                PhotoViewer.getInstance().setParentActivity(this.parentFragment);
                int indexOf = this.messages.indexOf(messageObject);
                if (indexOf < 0) {
                    ArrayList arrayList = new ArrayList();
                    arrayList.add(messageObject);
                    PhotoViewer.getInstance().setParentActivity(this.parentFragment);
                    PhotoViewer.getInstance().openPhoto(arrayList, 0, 0L, 0L, 0L, this.provider);
                } else {
                    PhotoViewer.getInstance().setParentActivity(this.parentFragment);
                    PhotoViewer.getInstance().openPhoto(this.messages, indexOf, 0L, 0L, 0L, this.provider);
                }
                this.photoViewerClassGuid = PhotoViewer.getInstance().getClassGuid();
                return;
            }
            return;
        }
        if (i3 == 2) {
            try {
                TLRPC.MessageMedia messageMedia = messageObject.messageOwner.media;
                String str = null;
                TLRPC.WebPage webPage = messageMedia != null ? messageMedia.webpage : null;
                if (webPage != null && !(webPage instanceof TLRPC.TL_webPageEmpty)) {
                    if (webPage.cached_page != null) {
                        LaunchActivity launchActivity = LaunchActivity.instance;
                        if (launchActivity == null || launchActivity.getBottomSheetTabs() == null || LaunchActivity.instance.getBottomSheetTabs().tryReopenTab(messageObject) == null) {
                            this.parentFragment.createArticleViewer(false).open(messageObject);
                            return;
                        }
                        return;
                    }
                    String str2 = webPage.embed_url;
                    if (str2 != null && str2.length() != 0) {
                        openWebView(webPage, messageObject);
                        return;
                    }
                    str = webPage.url;
                }
                if (str == null) {
                    str = ((SharedLinkCell) view).getLink(0);
                }
                if (str != null) {
                    openUrl(str);
                }
            } catch (Exception e) {
                FileLog.e(e);
            }
        }
    }

    public boolean onItemLongClick(MessageObject messageObject, View view, int i) {
        if (!this.uiCallback.actionModeShowing()) {
            this.uiCallback.showActionMode();
        }
        if (!this.uiCallback.actionModeShowing()) {
            return true;
        }
        this.uiCallback.toggleItemSelection(messageObject, view, i);
        return true;
    }

    public void openUrl(String str) {
        if (AndroidUtilities.shouldShowUrlInAlert(str)) {
            AlertsCreator.showOpenUrlAlert(this.parentFragment, str, true, true);
        } else {
            Browser.openUrl(this.parentActivity, str);
        }
    }

    public void openWebView(TLRPC.WebPage webPage, MessageObject messageObject) {
        EmbedBottomSheet.show(this.parentFragment, messageObject, this.provider, webPage.site_name, webPage.description, webPage.url, webPage.embed_url, webPage.embed_width, webPage.embed_height, false);
    }

    public void showFloatingDateView() {
        AndroidUtilities.cancelRunOnUIThread(this.hideFloatingDateRunnable);
        AndroidUtilities.runOnUIThread(this.hideFloatingDateRunnable, 650L);
        if (this.floatingDateView.getTag() != null) {
            return;
        }
        AnimatorSet animatorSet = this.floatingDateAnimation;
        if (animatorSet != null) {
            animatorSet.cancel();
        }
        this.floatingDateView.setTag(1);
        AnimatorSet animatorSet2 = new AnimatorSet();
        this.floatingDateAnimation = animatorSet2;
        animatorSet2.setDuration(180L);
        this.floatingDateAnimation.playTogether(ObjectAnimator.ofFloat(this.floatingDateView, (Property<ChatActionCell, Float>) View.ALPHA, 1.0f), ObjectAnimator.ofFloat(this.floatingDateView, (Property<ChatActionCell, Float>) View.TRANSLATION_Y, 0.0f));
        this.floatingDateAnimation.setInterpolator(CubicBezierInterpolator.EASE_OUT);
        this.floatingDateAnimation.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animator) {
                FilteredSearchView.this.floatingDateAnimation = null;
            }
        });
        this.floatingDateAnimation.start();
    }

    @Override
    public void didReceivedNotification(int i, int i2, Object... objArr) {
        if (i == NotificationCenter.emojiLoaded) {
            int childCount = this.recyclerListView.getChildCount();
            for (int i3 = 0; i3 < childCount; i3++) {
                if (this.recyclerListView.getChildAt(i3) instanceof DialogCell) {
                    ((DialogCell) this.recyclerListView.getChildAt(i3)).update(0);
                }
                this.recyclerListView.getChildAt(i3).invalidate();
            }
        }
    }

    public ArrayList<ThemeDescription> getThemeDescriptions() {
        ArrayList<ThemeDescription> arrayList = new ArrayList<>();
        int i = ThemeDescription.FLAG_BACKGROUND;
        int i2 = Theme.key_windowBackgroundWhite;
        arrayList.add(new ThemeDescription(this, i, null, null, null, null, i2));
        arrayList.add(new ThemeDescription(this, 0, null, null, null, null, Theme.key_dialogBackground));
        arrayList.add(new ThemeDescription(this, 0, null, null, null, null, Theme.key_windowBackgroundGray));
        int i3 = Theme.key_windowBackgroundWhiteBlackText;
        arrayList.add(new ThemeDescription(this.recyclerListView, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{SharedDocumentCell.class}, new String[]{"nameTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, i3));
        arrayList.add(new ThemeDescription(this.recyclerListView, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{SharedDocumentCell.class}, new String[]{"dateTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteGrayText3));
        int i4 = Theme.key_sharedMedia_startStopLoadIcon;
        arrayList.add(new ThemeDescription(this.recyclerListView, ThemeDescription.FLAG_PROGRESSBAR, new Class[]{SharedDocumentCell.class}, new String[]{"progressView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, i4));
        arrayList.add(new ThemeDescription(this.recyclerListView, ThemeDescription.FLAG_IMAGECOLOR, new Class[]{SharedDocumentCell.class}, new String[]{"statusImageView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, i4));
        int i5 = Theme.key_checkbox;
        arrayList.add(new ThemeDescription(this.recyclerListView, ThemeDescription.FLAG_CHECKBOX, new Class[]{SharedDocumentCell.class}, new String[]{"checkBox"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, i5));
        int i6 = Theme.key_checkboxCheck;
        arrayList.add(new ThemeDescription(this.recyclerListView, ThemeDescription.FLAG_CHECKBOXCHECK, new Class[]{SharedDocumentCell.class}, new String[]{"checkBox"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, i6));
        arrayList.add(new ThemeDescription(this.recyclerListView, ThemeDescription.FLAG_IMAGECOLOR, new Class[]{SharedDocumentCell.class}, new String[]{"thumbImageView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_files_folderIcon));
        arrayList.add(new ThemeDescription(this.recyclerListView, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{SharedDocumentCell.class}, new String[]{"extTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_files_iconText));
        arrayList.add(new ThemeDescription(this.recyclerListView, 0, new Class[]{LoadingCell.class}, new String[]{"progressBar"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_progressCircle));
        arrayList.add(new ThemeDescription(this.recyclerListView, ThemeDescription.FLAG_CHECKBOX, new Class[]{SharedAudioCell.class}, new String[]{"checkBox"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, i5));
        arrayList.add(new ThemeDescription(this.recyclerListView, ThemeDescription.FLAG_CHECKBOXCHECK, new Class[]{SharedAudioCell.class}, new String[]{"checkBox"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, i6));
        arrayList.add(new ThemeDescription(this.recyclerListView, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{SharedAudioCell.class}, Theme.chat_contextResult_titleTextPaint, null, null, i3));
        arrayList.add(new ThemeDescription(this.recyclerListView, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{SharedAudioCell.class}, Theme.chat_contextResult_descriptionTextPaint, null, null, Theme.key_windowBackgroundWhiteGrayText2));
        arrayList.add(new ThemeDescription(this.recyclerListView, ThemeDescription.FLAG_CHECKBOX, new Class[]{SharedLinkCell.class}, new String[]{"checkBox"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, i5));
        arrayList.add(new ThemeDescription(this.recyclerListView, ThemeDescription.FLAG_CHECKBOXCHECK, new Class[]{SharedLinkCell.class}, new String[]{"checkBox"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, i6));
        arrayList.add(new ThemeDescription(this.recyclerListView, 0, new Class[]{SharedLinkCell.class}, new String[]{"titleTextPaint"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, i3));
        arrayList.add(new ThemeDescription(this.recyclerListView, 0, new Class[]{SharedLinkCell.class}, null, null, null, Theme.key_windowBackgroundWhiteLinkText));
        arrayList.add(new ThemeDescription(this.recyclerListView, 0, new Class[]{SharedLinkCell.class}, Theme.linkSelectionPaint, null, null, Theme.key_windowBackgroundWhiteLinkSelection));
        arrayList.add(new ThemeDescription(this.recyclerListView, 0, new Class[]{SharedLinkCell.class}, new String[]{"letterDrawable"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_sharedMedia_linkPlaceholderText));
        arrayList.add(new ThemeDescription(this.recyclerListView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{SharedLinkCell.class}, new String[]{"letterDrawable"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_sharedMedia_linkPlaceholder));
        arrayList.add(new ThemeDescription(this.recyclerListView, ThemeDescription.FLAG_CELLBACKGROUNDCOLOR | ThemeDescription.FLAG_SECTIONS, new Class[]{SharedMediaSectionCell.class}, null, null, null, i2));
        arrayList.add(new ThemeDescription(this.recyclerListView, ThemeDescription.FLAG_SECTIONS, new Class[]{SharedMediaSectionCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, i3));
        arrayList.add(new ThemeDescription(this.recyclerListView, 0, new Class[]{SharedMediaSectionCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, i3));
        arrayList.add(new ThemeDescription(this.recyclerListView, 0, new Class[]{DialogCell.class, ProfileSearchCell.class}, null, Theme.avatarDrawables, null, Theme.key_avatar_text));
        arrayList.add(new ThemeDescription(this.recyclerListView, 0, new Class[]{DialogCell.class}, Theme.dialogs_countPaint, null, null, Theme.key_chats_unreadCounter));
        arrayList.add(new ThemeDescription(this.recyclerListView, 0, new Class[]{DialogCell.class}, Theme.dialogs_countGrayPaint, null, null, Theme.key_chats_unreadCounterMuted));
        arrayList.add(new ThemeDescription(this.recyclerListView, 0, new Class[]{DialogCell.class}, Theme.dialogs_countTextPaint, null, null, Theme.key_chats_unreadCounterText));
        arrayList.add(new ThemeDescription(this.recyclerListView, 0, new Class[]{DialogCell.class, ProfileSearchCell.class}, null, new Drawable[]{Theme.dialogs_lockDrawable}, null, Theme.key_chats_secretIcon));
        Drawable[] drawableArr = {Theme.dialogs_scamDrawable, Theme.dialogs_fakeDrawable};
        int i7 = Theme.key_chats_draft;
        arrayList.add(new ThemeDescription(this.recyclerListView, 0, new Class[]{DialogCell.class, ProfileSearchCell.class}, null, drawableArr, null, i7));
        arrayList.add(new ThemeDescription(this.recyclerListView, 0, new Class[]{DialogCell.class}, null, new Drawable[]{Theme.dialogs_pinnedDrawable, Theme.dialogs_reorderDrawable}, null, Theme.key_chats_pinnedIcon));
        TextPaint[] textPaintArr = Theme.dialogs_namePaint;
        arrayList.add(new ThemeDescription(this.recyclerListView, 0, new Class[]{DialogCell.class, ProfileSearchCell.class}, (String[]) null, new Paint[]{textPaintArr[0], textPaintArr[1], Theme.dialogs_searchNamePaint}, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_chats_name));
        TextPaint[] textPaintArr2 = Theme.dialogs_nameEncryptedPaint;
        arrayList.add(new ThemeDescription(this.recyclerListView, 0, new Class[]{DialogCell.class, ProfileSearchCell.class}, (String[]) null, new Paint[]{textPaintArr2[0], textPaintArr2[1], Theme.dialogs_searchNameEncryptedPaint}, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_chats_secretName));
        arrayList.add(new ThemeDescription(this.recyclerListView, 0, new Class[]{DialogCell.class}, Theme.dialogs_messagePaint[1], null, null, Theme.key_chats_message_threeLines));
        arrayList.add(new ThemeDescription(this.recyclerListView, 0, new Class[]{DialogCell.class}, Theme.dialogs_messagePaint[0], null, null, Theme.key_chats_message));
        arrayList.add(new ThemeDescription(this.recyclerListView, 0, new Class[]{DialogCell.class}, Theme.dialogs_messageNamePaint, null, null, Theme.key_chats_nameMessage_threeLines));
        arrayList.add(new ThemeDescription(this.recyclerListView, 0, new Class[]{DialogCell.class}, null, null, null, i7));
        arrayList.add(new ThemeDescription(this.recyclerListView, 0, new Class[]{DialogCell.class}, (String[]) null, Theme.dialogs_messagePrintingPaint, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_chats_actionMessage));
        arrayList.add(new ThemeDescription(this.recyclerListView, 0, new Class[]{DialogCell.class}, Theme.dialogs_timePaint, null, null, Theme.key_chats_date));
        arrayList.add(new ThemeDescription(this.recyclerListView, 0, new Class[]{DialogCell.class}, Theme.dialogs_pinnedPaint, null, null, Theme.key_chats_pinnedOverlay));
        arrayList.add(new ThemeDescription(this.recyclerListView, 0, new Class[]{DialogCell.class}, Theme.dialogs_tabletSeletedPaint, null, null, Theme.key_chats_tabletSelectedOverlay));
        arrayList.add(new ThemeDescription(this.recyclerListView, 0, new Class[]{DialogCell.class}, null, new Drawable[]{Theme.dialogs_checkDrawable}, null, Theme.key_chats_sentCheck));
        arrayList.add(new ThemeDescription(this.recyclerListView, 0, new Class[]{DialogCell.class}, null, new Drawable[]{Theme.dialogs_checkReadDrawable, Theme.dialogs_halfCheckDrawable}, null, Theme.key_chats_sentReadCheck));
        arrayList.add(new ThemeDescription(this.recyclerListView, 0, new Class[]{DialogCell.class}, null, new Drawable[]{Theme.dialogs_clockDrawable}, null, Theme.key_chats_sentClock));
        arrayList.add(new ThemeDescription(this.recyclerListView, 0, new Class[]{DialogCell.class}, Theme.dialogs_errorPaint, null, null, Theme.key_chats_sentError));
        arrayList.add(new ThemeDescription(this.recyclerListView, 0, new Class[]{DialogCell.class}, null, new Drawable[]{Theme.dialogs_errorDrawable}, null, Theme.key_chats_sentErrorIcon));
        arrayList.add(new ThemeDescription(this.recyclerListView, 0, new Class[]{DialogCell.class, ProfileSearchCell.class}, null, new Drawable[]{Theme.dialogs_verifiedCheckDrawable}, null, Theme.key_chats_verifiedCheck));
        arrayList.add(new ThemeDescription(this.recyclerListView, 0, new Class[]{DialogCell.class, ProfileSearchCell.class}, null, new Drawable[]{Theme.dialogs_verifiedDrawable}, null, Theme.key_chats_verifiedBackground));
        arrayList.add(new ThemeDescription(this.recyclerListView, 0, new Class[]{DialogCell.class}, null, new Drawable[]{Theme.dialogs_muteDrawable}, null, Theme.key_chats_muteIcon));
        arrayList.add(new ThemeDescription(this.recyclerListView, 0, new Class[]{DialogCell.class}, null, new Drawable[]{Theme.dialogs_mentionDrawable}, null, Theme.key_chats_mentionIcon));
        arrayList.add(new ThemeDescription(this.recyclerListView, 0, new Class[]{DialogCell.class}, null, null, null, Theme.key_chats_archivePinBackground));
        arrayList.add(new ThemeDescription(this.recyclerListView, 0, new Class[]{DialogCell.class}, null, null, null, Theme.key_chats_archiveBackground));
        arrayList.add(new ThemeDescription(this.recyclerListView, 0, new Class[]{DialogCell.class}, null, null, null, Theme.key_chats_onlineCircle));
        arrayList.add(new ThemeDescription(this.recyclerListView, 0, new Class[]{DialogCell.class}, null, null, null, i2));
        arrayList.add(new ThemeDescription(this.recyclerListView, ThemeDescription.FLAG_CHECKBOX, new Class[]{DialogCell.class}, new String[]{"checkBox"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, i2));
        arrayList.add(new ThemeDescription(this.recyclerListView, ThemeDescription.FLAG_CHECKBOXCHECK, new Class[]{DialogCell.class}, new String[]{"checkBox"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, i6));
        arrayList.add(new ThemeDescription(this.recyclerListView, ThemeDescription.FLAG_SECTIONS, new Class[]{GraySectionCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_graySectionText));
        arrayList.add(new ThemeDescription(this.recyclerListView, ThemeDescription.FLAG_CELLBACKGROUNDCOLOR | ThemeDescription.FLAG_SECTIONS, new Class[]{GraySectionCell.class}, null, null, null, Theme.key_graySection));
        arrayList.add(new ThemeDescription(this.emptyView.title, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, i3));
        arrayList.add(new ThemeDescription(this.emptyView.subtitle, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, Theme.key_windowBackgroundWhiteGrayText));
        return arrayList;
    }

    public void messagesDeleted(long r10, java.util.ArrayList r12) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.FilteredSearchView.messagesDeleted(long, java.util.ArrayList):void");
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        int i = UserConfig.selectedAccount;
        this.lastAccount = i;
        NotificationCenter.getInstance(i).addObserver(this, NotificationCenter.emojiLoaded);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        NotificationCenter.getInstance(this.lastAccount).removeObserver(this, NotificationCenter.emojiLoaded);
    }

    @Override
    protected void onMeasure(int i, int i2) {
        RecyclerView.Adapter adapter;
        int i3 = this.columnsCount;
        this.columnsCount = (!AndroidUtilities.isTablet() && getResources().getConfiguration().orientation == 2) ? 6 : 3;
        if (i3 != this.columnsCount && (adapter = this.adapter) == this.sharedPhotoVideoAdapter) {
            this.ignoreRequestLayout = true;
            adapter.notifyDataSetChanged();
            this.ignoreRequestLayout = false;
        }
        super.onMeasure(i, i2);
    }

    @Override
    public void requestLayout() {
        if (this.ignoreRequestLayout) {
            return;
        }
        super.requestLayout();
    }

    public void search(final long j, final long j2, final long j3, final FiltersView.MediaFilterData mediaFilterData, final boolean z, final String str, boolean z2) {
        FlickerLoadingView flickerLoadingView;
        int i;
        final String format = String.format(Locale.ENGLISH, "%d%d%d%d%s%s", Long.valueOf(j), Long.valueOf(j2), Long.valueOf(j3), Integer.valueOf(mediaFilterData == null ? -1 : mediaFilterData.filterType), str, Boolean.valueOf(z));
        String str2 = this.lastSearchFilterQueryString;
        boolean z3 = str2 != null && str2.equals(format);
        boolean z4 = !z3 && z2;
        this.currentSearchFilter = mediaFilterData;
        this.currentSearchDialogId = j;
        this.currentSearchMinDate = j2;
        this.currentSearchMaxDate = j3;
        this.currentSearchString = str;
        this.currentIncludeFolder = z;
        Runnable runnable = this.searchRunnable;
        if (runnable != null) {
            AndroidUtilities.cancelRunOnUIThread(runnable);
        }
        AndroidUtilities.cancelRunOnUIThread(this.clearCurrentResultsRunnable);
        if (z3 && z2) {
            return;
        }
        if (z4 || (mediaFilterData == null && j == 0 && j2 == 0 && j3 == 0)) {
            this.messages.clear();
            this.sections.clear();
            this.sectionArrays.clear();
            this.isLoading = true;
            this.emptyView.setVisibility(0);
            RecyclerView.Adapter adapter = this.adapter;
            if (adapter != null) {
                adapter.notifyDataSetChanged();
            }
            this.requestIndex++;
            this.firstLoading = true;
            if (this.recyclerListView.getPinnedHeader() != null) {
                this.recyclerListView.getPinnedHeader().setAlpha(0.0f);
            }
            this.localTipChats.clear();
            this.localTipDates.clear();
            if (!z4) {
                return;
            }
        } else if (z2 && !this.messages.isEmpty()) {
            return;
        }
        this.isLoading = true;
        RecyclerView.Adapter adapter2 = this.adapter;
        if (adapter2 != null) {
            adapter2.notifyDataSetChanged();
        }
        if (!z3) {
            this.clearCurrentResultsRunnable.run();
            this.emptyView.showProgress(true, !z2);
        }
        if (TextUtils.isEmpty(str)) {
            this.localTipDates.clear();
            this.localTipChats.clear();
            Delegate delegate = this.delegate;
            if (delegate != null) {
                delegate.updateFiltersView(false, null, null, false);
            }
        }
        final int i2 = this.requestIndex + 1;
        this.requestIndex = i2;
        final int i3 = UserConfig.selectedAccount;
        final boolean z5 = z3;
        Runnable runnable2 = new Runnable() {
            @Override
            public final void run() {
                FilteredSearchView.this.lambda$search$4(j, str, mediaFilterData, i3, j2, j3, z5, z, format, i2);
            }
        };
        this.searchRunnable = runnable2;
        AndroidUtilities.runOnUIThread(runnable2, (!z3 || this.messages.isEmpty()) ? 350L : 0L);
        if (mediaFilterData != null) {
            int i4 = mediaFilterData.filterType;
            if (i4 != 0) {
                if (i4 == 1) {
                    flickerLoadingView = this.loadingView;
                    i = 3;
                } else {
                    if (i4 != 3) {
                        i = 5;
                        if (i4 != 5) {
                            if (i4 != 2) {
                                return;
                            } else {
                                flickerLoadingView = this.loadingView;
                            }
                        }
                    }
                    flickerLoadingView = this.loadingView;
                    i = 4;
                }
                flickerLoadingView.setViewType(i);
            }
            if (TextUtils.isEmpty(this.currentSearchString)) {
                this.loadingView.setViewType(2);
                return;
            }
        }
        flickerLoadingView = this.loadingView;
        i = 1;
        flickerLoadingView.setViewType(i);
    }

    public void setChatPreviewDelegate(SearchViewPager.ChatPreviewDelegate chatPreviewDelegate) {
        this.chatPreviewDelegate = chatPreviewDelegate;
    }

    public void setDelegate(Delegate delegate, boolean z) {
        this.delegate = delegate;
        if (!z || delegate == null || this.localTipChats.isEmpty()) {
            return;
        }
        delegate.updateFiltersView(false, this.localTipChats, this.localTipDates, this.localTipArchive);
    }

    public void setKeyboardHeight(int i, boolean z) {
        this.emptyView.setKeyboardHeight(i, z);
    }

    public void setUiCallback(UiCallback uiCallback) {
        this.uiCallback = uiCallback;
    }

    public void setUseFromUserAsAvatar(boolean z) {
        this.useFromUserAsAvatar = z;
    }

    public void update() {
        RecyclerView.Adapter adapter = this.adapter;
        if (adapter != null) {
            adapter.notifyDataSetChanged();
        }
    }
}
