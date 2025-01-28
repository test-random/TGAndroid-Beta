package org.telegram.ui.Components;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Property;
import android.util.SparseArray;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.webkit.MimeTypeMap;
import android.widget.EditText;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSmoothScroller;
import androidx.recyclerview.widget.RecyclerView;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import org.telegram.messenger.AccountInstance;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.AnimationNotificationsLocker;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.BuildVars;
import org.telegram.messenger.FileLoader;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MediaController;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.R;
import org.telegram.messenger.SendMessagesHelper;
import org.telegram.messenger.SharedConfig;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.Utilities;
import org.telegram.messenger.ringtone.RingtoneDataStore;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.ActionBarMenu;
import org.telegram.ui.ActionBar.ActionBarMenuItem;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ActionBar.ThemeDescription;
import org.telegram.ui.Adapters.FiltersView;
import org.telegram.ui.Cells.GraySectionCell;
import org.telegram.ui.Cells.HeaderCell;
import org.telegram.ui.Cells.ShadowSectionCell;
import org.telegram.ui.Cells.SharedDocumentCell;
import org.telegram.ui.ChatActivity;
import org.telegram.ui.Components.ChatAttachAlert;
import org.telegram.ui.Components.ChatAttachAlertDocumentLayout;
import org.telegram.ui.Components.LinkSpanDrawable;
import org.telegram.ui.Components.Premium.LimitReachedBottomSheet;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.FilteredSearchView;
import org.telegram.ui.PhotoPickerActivity;

public class ChatAttachAlertDocumentLayout extends ChatAttachAlert.AttachAlertLayout {
    private float additionalTranslationY;
    private boolean allowMusic;
    private LinearLayoutManager backgroundLayoutManager;
    private ListAdapter backgroundListAdapter;
    private RecyclerListView backgroundListView;
    private boolean canSelectOnlyImageFiles;
    private int currentAnimationType;
    private File currentDir;
    private DocumentSelectActivityDelegate delegate;
    private StickerEmptyView emptyView;
    private FiltersView filtersView;
    private AnimatorSet filtersViewAnimator;
    private boolean hasFiles;
    private boolean ignoreLayout;
    public boolean isSoundPicker;
    private LinearLayoutManager layoutManager;
    private ListAdapter listAdapter;
    ValueAnimator listAnimation;
    private RecyclerListView listView;
    private FlickerLoadingView loadingView;
    private int maxSelectedFiles;
    private BroadcastReceiver receiver;
    private boolean receiverRegistered;
    private boolean scrolling;
    private SearchAdapter searchAdapter;
    private ActionBarMenuItem searchItem;
    private boolean searching;
    private HashMap selectedFiles;
    public ArrayList selectedFilesOrder;
    private HashMap selectedMessages;
    private boolean sendPressed;
    private boolean sortByName;
    private ActionBarMenuItem sortItem;

    public class AnonymousClass1 extends BroadcastReceiver {
        AnonymousClass1() {
        }

        public void lambda$onReceive$0() {
            try {
                if (ChatAttachAlertDocumentLayout.this.currentDir == null) {
                    ChatAttachAlertDocumentLayout.this.listRoots();
                } else {
                    ChatAttachAlertDocumentLayout chatAttachAlertDocumentLayout = ChatAttachAlertDocumentLayout.this;
                    chatAttachAlertDocumentLayout.listFiles(chatAttachAlertDocumentLayout.currentDir);
                }
                ChatAttachAlertDocumentLayout.this.updateSearchButton();
            } catch (Exception e) {
                FileLog.e(e);
            }
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            Runnable runnable = new Runnable() {
                @Override
                public final void run() {
                    ChatAttachAlertDocumentLayout.AnonymousClass1.this.lambda$onReceive$0();
                }
            };
            if ("android.intent.action.MEDIA_UNMOUNTED".equals(intent.getAction())) {
                ChatAttachAlertDocumentLayout.this.listView.postDelayed(runnable, 1000L);
            } else {
                runnable.run();
            }
        }
    }

    public class AnonymousClass2 extends ActionBarMenuItem.ActionBarMenuItemSearchListener {
        AnonymousClass2() {
        }

        @Override
        public void onSearchCollapse() {
            ChatAttachAlertDocumentLayout.this.searching = false;
            ChatAttachAlertDocumentLayout.this.sortItem.setVisibility(0);
            if (ChatAttachAlertDocumentLayout.this.listView.getAdapter() != ChatAttachAlertDocumentLayout.this.listAdapter) {
                ChatAttachAlertDocumentLayout.this.listView.setAdapter(ChatAttachAlertDocumentLayout.this.listAdapter);
            }
            ChatAttachAlertDocumentLayout.this.listAdapter.notifyDataSetChanged();
            ChatAttachAlertDocumentLayout.this.searchAdapter.search(null, true);
        }

        @Override
        public void onSearchExpand() {
            ChatAttachAlertDocumentLayout.this.searching = true;
            ChatAttachAlertDocumentLayout.this.sortItem.setVisibility(8);
            ChatAttachAlertDocumentLayout chatAttachAlertDocumentLayout = ChatAttachAlertDocumentLayout.this;
            chatAttachAlertDocumentLayout.parentAlert.makeFocusable(chatAttachAlertDocumentLayout.searchItem.getSearchField(), true);
        }

        @Override
        public void onSearchFilterCleared(FiltersView.MediaFilterData mediaFilterData) {
            ChatAttachAlertDocumentLayout.this.searchAdapter.removeSearchFilter(mediaFilterData);
            ChatAttachAlertDocumentLayout.this.searchAdapter.search(ChatAttachAlertDocumentLayout.this.searchItem.getSearchField().getText().toString(), false);
            ChatAttachAlertDocumentLayout.this.searchAdapter.updateFiltersView(true, null, null, true);
        }

        @Override
        public void onTextChanged(EditText editText) {
            ChatAttachAlertDocumentLayout.this.searchAdapter.search(editText.getText().toString(), false);
        }
    }

    public class AnonymousClass3 extends StickerEmptyView {
        AnonymousClass3(Context context, View view, int i, Theme.ResourcesProvider resourcesProvider) {
            super(context, view, i, resourcesProvider);
        }

        @Override
        public float getTranslationY() {
            return super.getTranslationY() - ChatAttachAlertDocumentLayout.this.additionalTranslationY;
        }

        @Override
        public void setTranslationY(float f) {
            super.setTranslationY(f + ChatAttachAlertDocumentLayout.this.additionalTranslationY);
        }
    }

    public class AnonymousClass4 extends RecyclerListView {
        Paint paint = new Paint();

        AnonymousClass4(Context context, Theme.ResourcesProvider resourcesProvider) {
            super(context, resourcesProvider);
            this.paint = new Paint();
        }

        @Override
        public void dispatchDraw(Canvas canvas) {
            if (ChatAttachAlertDocumentLayout.this.currentAnimationType == 2 && getChildCount() > 0) {
                float f = 2.1474836E9f;
                for (int i = 0; i < getChildCount(); i++) {
                    if (getChildAt(i).getY() < f) {
                        f = getChildAt(i).getY();
                    }
                }
                this.paint.setColor(Theme.getColor(Theme.key_dialogBackground));
            }
            super.dispatchDraw(canvas);
        }

        @Override
        public boolean onTouchEvent(MotionEvent motionEvent) {
            if (ChatAttachAlertDocumentLayout.this.currentAnimationType != 0) {
                return false;
            }
            return super.onTouchEvent(motionEvent);
        }
    }

    public class AnonymousClass5 extends RecyclerListView {
        Paint paint = new Paint();

        AnonymousClass5(Context context, Theme.ResourcesProvider resourcesProvider) {
            super(context, resourcesProvider);
            this.paint = new Paint();
        }

        @Override
        public void dispatchDraw(Canvas canvas) {
            if (ChatAttachAlertDocumentLayout.this.currentAnimationType == 1 && getChildCount() > 0) {
                float f = 2.1474836E9f;
                for (int i = 0; i < getChildCount(); i++) {
                    if (getChildAt(i).getY() < f) {
                        f = getChildAt(i).getY();
                    }
                }
                this.paint.setColor(Theme.getColor(Theme.key_dialogBackground));
            }
            super.dispatchDraw(canvas);
        }
    }

    public class AnonymousClass6 extends FillLastLinearLayoutManager {

        class AnonymousClass1 extends LinearSmoothScroller {
            AnonymousClass1(Context context) {
                super(context);
            }

            @Override
            public int calculateDyToMakeVisible(View view, int i) {
                return super.calculateDyToMakeVisible(view, i) - (ChatAttachAlertDocumentLayout.this.listView.getPaddingTop() - AndroidUtilities.dp(56.0f));
            }

            @Override
            public int calculateTimeForDeceleration(int i) {
                return super.calculateTimeForDeceleration(i) * 2;
            }
        }

        AnonymousClass6(Context context, int i, boolean z, int i2, RecyclerView recyclerView) {
            super(context, i, z, i2, recyclerView);
        }

        @Override
        public void smoothScrollToPosition(RecyclerView recyclerView, RecyclerView.State state, int i) {
            AnonymousClass1 anonymousClass1 = new LinearSmoothScroller(recyclerView.getContext()) {
                AnonymousClass1(Context context) {
                    super(context);
                }

                @Override
                public int calculateDyToMakeVisible(View view, int i2) {
                    return super.calculateDyToMakeVisible(view, i2) - (ChatAttachAlertDocumentLayout.this.listView.getPaddingTop() - AndroidUtilities.dp(56.0f));
                }

                @Override
                public int calculateTimeForDeceleration(int i2) {
                    return super.calculateTimeForDeceleration(i2) * 2;
                }
            };
            anonymousClass1.setTargetPosition(i);
            startSmoothScroll(anonymousClass1);
        }
    }

    public class AnonymousClass7 extends RecyclerView.OnScrollListener {
        AnonymousClass7() {
        }

        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int i) {
            RecyclerListView.Holder holder;
            if (i == 0) {
                int dp = AndroidUtilities.dp(13.0f);
                int backgroundPaddingTop = ChatAttachAlertDocumentLayout.this.parentAlert.getBackgroundPaddingTop();
                if (((ChatAttachAlertDocumentLayout.this.parentAlert.scrollOffsetY[0] - backgroundPaddingTop) - dp) + backgroundPaddingTop < ActionBar.getCurrentActionBarHeight() && (holder = (RecyclerListView.Holder) ChatAttachAlertDocumentLayout.this.listView.findViewHolderForAdapterPosition(0)) != null && holder.itemView.getTop() > AndroidUtilities.dp(56.0f)) {
                    ChatAttachAlertDocumentLayout.this.listView.smoothScrollBy(0, holder.itemView.getTop() - AndroidUtilities.dp(56.0f));
                }
            }
            if (i == 1 && ChatAttachAlertDocumentLayout.this.searching && ChatAttachAlertDocumentLayout.this.listView.getAdapter() == ChatAttachAlertDocumentLayout.this.searchAdapter) {
                AndroidUtilities.hideKeyboard(ChatAttachAlertDocumentLayout.this.parentAlert.getCurrentFocus());
            }
            ChatAttachAlertDocumentLayout.this.scrolling = i != 0;
        }

        @Override
        public void onScrolled(RecyclerView recyclerView, int i, int i2) {
            ChatAttachAlertDocumentLayout chatAttachAlertDocumentLayout = ChatAttachAlertDocumentLayout.this;
            chatAttachAlertDocumentLayout.parentAlert.updateLayout(chatAttachAlertDocumentLayout, true, i2);
            ChatAttachAlertDocumentLayout.this.updateEmptyViewPosition();
            if (ChatAttachAlertDocumentLayout.this.listView.getAdapter() == ChatAttachAlertDocumentLayout.this.searchAdapter) {
                int findFirstVisibleItemPosition = ChatAttachAlertDocumentLayout.this.layoutManager.findFirstVisibleItemPosition();
                int findLastVisibleItemPosition = ChatAttachAlertDocumentLayout.this.layoutManager.findLastVisibleItemPosition();
                int abs = Math.abs(findLastVisibleItemPosition - findFirstVisibleItemPosition) + 1;
                int itemCount = recyclerView.getAdapter().getItemCount();
                if (abs <= 0 || findLastVisibleItemPosition < itemCount - 10) {
                    return;
                }
                ChatAttachAlertDocumentLayout.this.searchAdapter.loadMore();
            }
        }
    }

    public class AnonymousClass8 implements PhotoPickerActivity.PhotoPickerActivityDelegate {
        final HashMap val$selectedPhotos;
        final ArrayList val$selectedPhotosOrder;

        AnonymousClass8(HashMap hashMap, ArrayList arrayList) {
            r2 = hashMap;
            r3 = arrayList;
        }

        @Override
        public void actionButtonPressed(boolean z, boolean z2, int i) {
            if (z) {
                return;
            }
            ChatAttachAlertDocumentLayout.this.sendSelectedPhotos(r2, r3, z2, i);
        }

        @Override
        public boolean canFinishFragment() {
            return PhotoPickerActivity.PhotoPickerActivityDelegate.CC.$default$canFinishFragment(this);
        }

        @Override
        public void onCaptionChanged(CharSequence charSequence) {
        }

        @Override
        public void onOpenInPressed() {
            ChatAttachAlertDocumentLayout.this.delegate.startDocumentSelectActivity();
        }

        @Override
        public void selectedPhotosChanged() {
        }
    }

    public class AnonymousClass9 extends AnimatorListenerAdapter {
        AnonymousClass9() {
        }

        @Override
        public void onAnimationEnd(Animator animator) {
            super.onAnimationEnd(animator);
            ChatAttachAlertDocumentLayout.this.backgroundListView.setVisibility(8);
            ChatAttachAlertDocumentLayout.this.currentAnimationType = 0;
            ChatAttachAlertDocumentLayout.this.listView.setAlpha(1.0f);
            ChatAttachAlertDocumentLayout.this.listView.setScaleX(1.0f);
            ChatAttachAlertDocumentLayout.this.listView.setScaleY(1.0f);
            ChatAttachAlertDocumentLayout.this.listView.setTranslationX(0.0f);
            ChatAttachAlertDocumentLayout.this.listView.invalidate();
        }
    }

    public interface DocumentSelectActivityDelegate {

        public abstract class CC {
            public static void $default$didSelectPhotos(DocumentSelectActivityDelegate documentSelectActivityDelegate, ArrayList arrayList, boolean z, int i) {
            }

            public static void $default$startMusicSelectActivity(DocumentSelectActivityDelegate documentSelectActivityDelegate) {
            }
        }

        void didSelectFiles(ArrayList arrayList, String str, ArrayList arrayList2, boolean z, int i, long j, boolean z2);

        void didSelectPhotos(ArrayList arrayList, boolean z, int i);

        void startDocumentSelectActivity();

        void startMusicSelectActivity();
    }

    public static class HistoryEntry {
        File dir;
        int scrollItem;
        int scrollOffset;
        String title;

        private HistoryEntry() {
        }

        HistoryEntry(AnonymousClass1 anonymousClass1) {
            this();
        }
    }

    public class ListAdapter extends RecyclerListView.SelectionAdapter {
        private Context mContext;
        private ArrayList items = new ArrayList();
        private ArrayList history = new ArrayList();
        private ArrayList recentItems = new ArrayList();

        public ListAdapter(Context context) {
            this.mContext = context;
        }

        public ListItem getItem(int i) {
            ArrayList arrayList;
            int size = this.items.size();
            if (i < size) {
                arrayList = this.items;
            } else {
                if (!this.history.isEmpty() || this.recentItems.isEmpty() || i == size || i == size + 1 || (i = i - (this.items.size() + 2)) >= this.recentItems.size()) {
                    return null;
                }
                arrayList = this.recentItems;
            }
            return (ListItem) arrayList.get(i);
        }

        @Override
        public int getItemCount() {
            int size = this.items.size();
            if (this.history.isEmpty() && !this.recentItems.isEmpty()) {
                size += this.recentItems.size() + 2;
            }
            return size + 1;
        }

        @Override
        public int getItemViewType(int i) {
            if (i == getItemCount() - 1) {
                return 3;
            }
            int size = this.items.size();
            if (i == size) {
                return 2;
            }
            return i == size + 1 ? 0 : 1;
        }

        @Override
        public boolean isEnabled(RecyclerView.ViewHolder viewHolder) {
            return viewHolder.getItemViewType() == 1;
        }

        @Override
        public void notifyDataSetChanged() {
            super.notifyDataSetChanged();
            ChatAttachAlertDocumentLayout.this.updateEmptyView();
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
            String substring;
            String str;
            String str2;
            String str3;
            boolean z;
            int itemViewType = viewHolder.getItemViewType();
            if (itemViewType == 0) {
                ((HeaderCell) viewHolder.itemView).setText(LocaleController.getString(ChatAttachAlertDocumentLayout.this.sortByName ? R.string.RecentFilesAZ : R.string.RecentFiles));
                return;
            }
            if (itemViewType != 1) {
                return;
            }
            ListItem item = getItem(i);
            SharedDocumentCell sharedDocumentCell = (SharedDocumentCell) viewHolder.itemView;
            int i2 = item.icon;
            if (i2 != 0) {
                str = item.title;
                str2 = item.subtitle;
                z = i != this.items.size() - 1;
                substring = null;
                str3 = null;
            } else {
                substring = item.ext.toUpperCase().substring(0, Math.min(item.ext.length(), 4));
                str = item.title;
                str2 = item.subtitle;
                str3 = item.thumb;
                i2 = 0;
                z = false;
            }
            sharedDocumentCell.setTextAndValueAndTypeAndThumb(str, str2, substring, str3, i2, z);
            sharedDocumentCell.setChecked(item.file != null ? ChatAttachAlertDocumentLayout.this.selectedFiles.containsKey(item.file.toString()) : false, !ChatAttachAlertDocumentLayout.this.scrolling);
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View headerCell;
            View sharedDocumentCell;
            if (i != 0) {
                if (i == 1) {
                    sharedDocumentCell = new SharedDocumentCell(this.mContext, 1, ChatAttachAlertDocumentLayout.this.resourcesProvider);
                } else if (i != 2) {
                    headerCell = new View(this.mContext);
                } else {
                    sharedDocumentCell = new ShadowSectionCell(this.mContext);
                    CombinedDrawable combinedDrawable = new CombinedDrawable(new ColorDrawable(ChatAttachAlertDocumentLayout.this.getThemedColor(Theme.key_windowBackgroundGray)), Theme.getThemedDrawableByKey(this.mContext, R.drawable.greydivider, Theme.key_windowBackgroundGrayShadow));
                    combinedDrawable.setFullsize(true);
                    sharedDocumentCell.setBackgroundDrawable(combinedDrawable);
                }
                headerCell = sharedDocumentCell;
            } else {
                headerCell = new HeaderCell(this.mContext, ChatAttachAlertDocumentLayout.this.resourcesProvider);
            }
            return new RecyclerListView.Holder(headerCell);
        }
    }

    public static class ListItem {
        public String ext;
        public File file;
        public int icon;
        public String subtitle;
        public String thumb;
        public String title;

        private ListItem() {
            this.subtitle = "";
            this.ext = "";
        }

        ListItem(AnonymousClass1 anonymousClass1) {
            this();
        }
    }

    public class SearchAdapter extends RecyclerListView.SectionsAdapter {
        private String currentDataQuery;
        private long currentSearchDialogId;
        private FiltersView.MediaFilterData currentSearchFilter;
        private long currentSearchMaxDate;
        private long currentSearchMinDate;
        private boolean endReached;
        private boolean isLoading;
        private String lastMessagesSearchString;
        private String lastSearchFilterQueryString;
        private Runnable localSearchRunnable;
        private Context mContext;
        private int nextSearchRate;
        private int requestIndex;
        private Runnable searchRunnable;
        private ArrayList searchResult = new ArrayList();
        private final FilteredSearchView.MessageHashId messageHashIdTmp = new FilteredSearchView.MessageHashId(0, 0);
        private ArrayList localTipChats = new ArrayList();
        private ArrayList localTipDates = new ArrayList();
        public ArrayList messages = new ArrayList();
        public SparseArray messagesById = new SparseArray();
        public ArrayList sections = new ArrayList();
        public HashMap sectionArrays = new HashMap();
        private ArrayList currentSearchFilters = new ArrayList();
        private boolean firstLoading = true;
        private AnimationNotificationsLocker notificationsLocker = new AnimationNotificationsLocker();
        private Runnable clearCurrentResultsRunnable = new Runnable() {
            AnonymousClass1() {
            }

            @Override
            public void run() {
                if (SearchAdapter.this.isLoading) {
                    SearchAdapter.this.messages.clear();
                    SearchAdapter.this.sections.clear();
                    SearchAdapter.this.sectionArrays.clear();
                    SearchAdapter.this.notifyDataSetChanged();
                }
            }
        };

        public class AnonymousClass1 implements Runnable {
            AnonymousClass1() {
            }

            @Override
            public void run() {
                if (SearchAdapter.this.isLoading) {
                    SearchAdapter.this.messages.clear();
                    SearchAdapter.this.sections.clear();
                    SearchAdapter.this.sectionArrays.clear();
                    SearchAdapter.this.notifyDataSetChanged();
                }
            }
        }

        public class AnonymousClass2 extends AnimatorListenerAdapter {
            AnonymousClass2() {
            }

            @Override
            public void onAnimationEnd(Animator animator) {
                if (ChatAttachAlertDocumentLayout.this.filtersView.getTag() == null) {
                    ChatAttachAlertDocumentLayout.this.filtersView.setVisibility(4);
                }
                ChatAttachAlertDocumentLayout.this.filtersViewAnimator = null;
            }
        }

        public class AnonymousClass3 implements ViewTreeObserver.OnPreDrawListener {
            final View val$finalProgressView;
            final int val$finalProgressViewPosition;

            class AnonymousClass1 extends AnimatorListenerAdapter {
                AnonymousClass1() {
                }

                @Override
                public void onAnimationEnd(Animator animator) {
                    SearchAdapter.this.notificationsLocker.unlock();
                }
            }

            class AnonymousClass2 extends AnimatorListenerAdapter {
                final RecyclerView.LayoutManager val$layoutManager;

                AnonymousClass2(RecyclerView.LayoutManager layoutManager) {
                    r2 = layoutManager;
                }

                @Override
                public void onAnimationEnd(Animator animator) {
                    r2.setAlpha(1.0f);
                    r2.stopIgnoringView(r2);
                    ChatAttachAlertDocumentLayout.this.listView.removeView(r2);
                }
            }

            AnonymousClass3(View view, int i) {
                r2 = view;
                r3 = i;
            }

            @Override
            public boolean onPreDraw() {
                ChatAttachAlertDocumentLayout.this.getViewTreeObserver().removeOnPreDrawListener(this);
                int childCount = ChatAttachAlertDocumentLayout.this.listView.getChildCount();
                AnimatorSet animatorSet = new AnimatorSet();
                for (int i = 0; i < childCount; i++) {
                    View childAt = ChatAttachAlertDocumentLayout.this.listView.getChildAt(i);
                    if (r2 == null || ChatAttachAlertDocumentLayout.this.listView.getChildAdapterPosition(childAt) >= r3) {
                        childAt.setAlpha(0.0f);
                        int min = (int) ((Math.min(ChatAttachAlertDocumentLayout.this.listView.getMeasuredHeight(), Math.max(0, childAt.getTop())) / ChatAttachAlertDocumentLayout.this.listView.getMeasuredHeight()) * 100.0f);
                        ObjectAnimator ofFloat = ObjectAnimator.ofFloat(childAt, (Property<View, Float>) View.ALPHA, 0.0f, 1.0f);
                        ofFloat.setStartDelay(min);
                        ofFloat.setDuration(200L);
                        animatorSet.playTogether(ofFloat);
                    }
                }
                animatorSet.addListener(new AnimatorListenerAdapter() {
                    AnonymousClass1() {
                    }

                    @Override
                    public void onAnimationEnd(Animator animator) {
                        SearchAdapter.this.notificationsLocker.unlock();
                    }
                });
                SearchAdapter.this.notificationsLocker.lock();
                animatorSet.start();
                View view = r2;
                if (view != null && view.getParent() == null) {
                    ChatAttachAlertDocumentLayout.this.listView.addView(r2);
                    RecyclerView.LayoutManager layoutManager = ChatAttachAlertDocumentLayout.this.listView.getLayoutManager();
                    if (layoutManager != null) {
                        layoutManager.ignoreView(r2);
                        View view2 = r2;
                        ObjectAnimator ofFloat2 = ObjectAnimator.ofFloat(view2, (Property<View, Float>) View.ALPHA, view2.getAlpha(), 0.0f);
                        ofFloat2.addListener(new AnimatorListenerAdapter() {
                            final RecyclerView.LayoutManager val$layoutManager;

                            AnonymousClass2(RecyclerView.LayoutManager layoutManager2) {
                                r2 = layoutManager2;
                            }

                            @Override
                            public void onAnimationEnd(Animator animator) {
                                r2.setAlpha(1.0f);
                                r2.stopIgnoringView(r2);
                                ChatAttachAlertDocumentLayout.this.listView.removeView(r2);
                            }
                        });
                        ofFloat2.start();
                    }
                }
                return true;
            }
        }

        class AnonymousClass4 implements ViewTreeObserver.OnPreDrawListener {
            final boolean val$animated;
            final MessageObject val$messageObject;
            final SharedDocumentCell val$sharedDocumentCell;

            AnonymousClass4(SharedDocumentCell sharedDocumentCell, MessageObject messageObject, boolean z) {
                r2 = sharedDocumentCell;
                r3 = messageObject;
                r4 = z;
            }

            @Override
            public boolean onPreDraw() {
                r2.getViewTreeObserver().removeOnPreDrawListener(this);
                if (!ChatAttachAlertDocumentLayout.this.parentAlert.actionBar.isActionModeShowed()) {
                    r2.setChecked(false, r4);
                    return true;
                }
                SearchAdapter.this.messageHashIdTmp.set(r3.getId(), r3.getDialogId());
                r2.setChecked(ChatAttachAlertDocumentLayout.this.selectedMessages.containsKey(SearchAdapter.this.messageHashIdTmp), r4);
                return true;
            }
        }

        public SearchAdapter(Context context) {
            this.mContext = context;
        }

        public void addSearchFilter(FiltersView.MediaFilterData mediaFilterData) {
            if (!this.currentSearchFilters.isEmpty()) {
                for (int i = 0; i < this.currentSearchFilters.size(); i++) {
                    if (mediaFilterData.isSameType((FiltersView.MediaFilterData) this.currentSearchFilters.get(i))) {
                        return;
                    }
                }
            }
            this.currentSearchFilters.add(mediaFilterData);
            ChatAttachAlertDocumentLayout.this.parentAlert.actionBar.setSearchFilter(mediaFilterData);
            ChatAttachAlertDocumentLayout.this.parentAlert.actionBar.setSearchFieldText("");
            updateFiltersView(true, null, null, true);
        }

        public void lambda$search$0(String str, boolean z, ArrayList arrayList) {
            String lowerCase = str.trim().toLowerCase();
            if (lowerCase.length() == 0) {
                updateSearchResults(new ArrayList(), str);
                return;
            }
            String translitString = LocaleController.getInstance().getTranslitString(lowerCase);
            if (lowerCase.equals(translitString) || translitString.length() == 0) {
                translitString = null;
            }
            int i = (translitString != null ? 1 : 0) + 1;
            String[] strArr = new String[i];
            strArr[0] = lowerCase;
            if (translitString != null) {
                strArr[1] = translitString;
            }
            ArrayList arrayList2 = new ArrayList();
            if (!z) {
                for (int i2 = 0; i2 < arrayList.size(); i2++) {
                    ListItem listItem = (ListItem) arrayList.get(i2);
                    File file = listItem.file;
                    if (file != null && !file.isDirectory()) {
                        int i3 = 0;
                        while (true) {
                            if (i3 < i) {
                                String str2 = strArr[i3];
                                String str3 = listItem.title;
                                if (str3 != null ? str3.toLowerCase().contains(str2) : false) {
                                    arrayList2.add(listItem);
                                    break;
                                }
                                i3++;
                            }
                        }
                    }
                }
            }
            updateSearchResults(arrayList2, str);
        }

        public void lambda$search$1(final String str) {
            final ArrayList arrayList = new ArrayList(ChatAttachAlertDocumentLayout.this.listAdapter.items);
            if (ChatAttachAlertDocumentLayout.this.listAdapter.history.isEmpty()) {
                arrayList.addAll(0, ChatAttachAlertDocumentLayout.this.listAdapter.recentItems);
            }
            final boolean z = !this.currentSearchFilters.isEmpty();
            Utilities.searchQueue.postRunnable(new Runnable() {
                @Override
                public final void run() {
                    ChatAttachAlertDocumentLayout.SearchAdapter.this.lambda$search$0(str, z, arrayList);
                }
            });
        }

        public void lambda$searchGlobal$2(int i, TLRPC.TL_error tL_error, TLObject tLObject, AccountInstance accountInstance, boolean z, String str, ArrayList arrayList, long j, long j2, ArrayList arrayList2, ArrayList arrayList3) {
            LinkSpanDrawable.LinksTextView linksTextView;
            int i2;
            if (i != this.requestIndex) {
                return;
            }
            this.isLoading = false;
            if (tL_error != null) {
                ChatAttachAlertDocumentLayout.this.emptyView.title.setText(LocaleController.getString(R.string.SearchEmptyViewTitle2));
                ChatAttachAlertDocumentLayout.this.emptyView.subtitle.setVisibility(0);
                ChatAttachAlertDocumentLayout.this.emptyView.subtitle.setText(LocaleController.getString(R.string.SearchEmptyViewFilteredSubtitle2));
                ChatAttachAlertDocumentLayout.this.emptyView.showProgress(false, true);
                return;
            }
            ChatAttachAlertDocumentLayout.this.emptyView.showProgress(false);
            TLRPC.messages_Messages messages_messages = (TLRPC.messages_Messages) tLObject;
            this.nextSearchRate = messages_messages.next_rate;
            accountInstance.getMessagesStorage().putUsersAndChats(messages_messages.users, messages_messages.chats, true, true);
            accountInstance.getMessagesController().putUsers(messages_messages.users, false);
            accountInstance.getMessagesController().putChats(messages_messages.chats, false);
            if (!z) {
                this.messages.clear();
                this.messagesById.clear();
                this.sections.clear();
                this.sectionArrays.clear();
            }
            int i3 = messages_messages.count;
            this.currentDataQuery = str;
            int size = arrayList.size();
            for (int i4 = 0; i4 < size; i4++) {
                MessageObject messageObject = (MessageObject) arrayList.get(i4);
                ArrayList arrayList4 = (ArrayList) this.sectionArrays.get(messageObject.monthKey);
                if (arrayList4 == null) {
                    arrayList4 = new ArrayList();
                    this.sectionArrays.put(messageObject.monthKey, arrayList4);
                    this.sections.add(messageObject.monthKey);
                }
                arrayList4.add(messageObject);
                this.messages.add(messageObject);
                this.messagesById.put(messageObject.getId(), messageObject);
            }
            if (this.messages.size() > i3) {
                i3 = this.messages.size();
            }
            this.endReached = this.messages.size() >= i3;
            if (this.messages.isEmpty()) {
                if (TextUtils.isEmpty(this.currentDataQuery) && j == 0 && j2 == 0) {
                    ChatAttachAlertDocumentLayout.this.emptyView.title.setText(LocaleController.getString(R.string.SearchEmptyViewTitle));
                    ChatAttachAlertDocumentLayout.this.emptyView.subtitle.setVisibility(0);
                    linksTextView = ChatAttachAlertDocumentLayout.this.emptyView.subtitle;
                    i2 = R.string.SearchEmptyViewFilteredSubtitleFiles;
                } else {
                    ChatAttachAlertDocumentLayout.this.emptyView.title.setText(LocaleController.getString(R.string.SearchEmptyViewTitle2));
                    ChatAttachAlertDocumentLayout.this.emptyView.subtitle.setVisibility(0);
                    linksTextView = ChatAttachAlertDocumentLayout.this.emptyView.subtitle;
                    i2 = R.string.SearchEmptyViewFilteredSubtitle2;
                }
                linksTextView.setText(LocaleController.getString(i2));
            }
            if (!z) {
                this.localTipChats.clear();
                if (arrayList2 != null) {
                    this.localTipChats.addAll(arrayList2);
                }
                if (str.length() >= 3 && (LocaleController.getString(R.string.SavedMessages).toLowerCase().startsWith(str) || "saved messages".startsWith(str))) {
                    int i5 = 0;
                    while (true) {
                        if (i5 >= this.localTipChats.size()) {
                            this.localTipChats.add(0, UserConfig.getInstance(UserConfig.selectedAccount).getCurrentUser());
                            break;
                        } else if ((this.localTipChats.get(i5) instanceof TLRPC.User) && UserConfig.getInstance(UserConfig.selectedAccount).getCurrentUser().id == ((TLRPC.User) this.localTipChats.get(i5)).id) {
                            break;
                        } else {
                            i5++;
                        }
                    }
                }
                this.localTipDates.clear();
                this.localTipDates.addAll(arrayList3);
                updateFiltersView(TextUtils.isEmpty(this.currentDataQuery), this.localTipChats, this.localTipDates, true);
            }
            this.firstLoading = false;
            View view = null;
            int i6 = -1;
            for (int i7 = 0; i7 < size; i7++) {
                View childAt = ChatAttachAlertDocumentLayout.this.listView.getChildAt(i7);
                if (childAt instanceof FlickerLoadingView) {
                    i6 = ChatAttachAlertDocumentLayout.this.listView.getChildAdapterPosition(childAt);
                    view = childAt;
                }
            }
            if (view != null) {
                ChatAttachAlertDocumentLayout.this.listView.removeView(view);
            }
            if ((ChatAttachAlertDocumentLayout.this.loadingView.getVisibility() == 0 && ChatAttachAlertDocumentLayout.this.listView.getChildCount() <= 1) || view != null) {
                ChatAttachAlertDocumentLayout.this.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                    final View val$finalProgressView;
                    final int val$finalProgressViewPosition;

                    class AnonymousClass1 extends AnimatorListenerAdapter {
                        AnonymousClass1() {
                        }

                        @Override
                        public void onAnimationEnd(Animator animator) {
                            SearchAdapter.this.notificationsLocker.unlock();
                        }
                    }

                    class AnonymousClass2 extends AnimatorListenerAdapter {
                        final RecyclerView.LayoutManager val$layoutManager;

                        AnonymousClass2(RecyclerView.LayoutManager layoutManager2) {
                            r2 = layoutManager2;
                        }

                        @Override
                        public void onAnimationEnd(Animator animator) {
                            r2.setAlpha(1.0f);
                            r2.stopIgnoringView(r2);
                            ChatAttachAlertDocumentLayout.this.listView.removeView(r2);
                        }
                    }

                    AnonymousClass3(View view2, int i62) {
                        r2 = view2;
                        r3 = i62;
                    }

                    @Override
                    public boolean onPreDraw() {
                        ChatAttachAlertDocumentLayout.this.getViewTreeObserver().removeOnPreDrawListener(this);
                        int childCount = ChatAttachAlertDocumentLayout.this.listView.getChildCount();
                        AnimatorSet animatorSet = new AnimatorSet();
                        for (int i8 = 0; i8 < childCount; i8++) {
                            View childAt2 = ChatAttachAlertDocumentLayout.this.listView.getChildAt(i8);
                            if (r2 == null || ChatAttachAlertDocumentLayout.this.listView.getChildAdapterPosition(childAt2) >= r3) {
                                childAt2.setAlpha(0.0f);
                                int min = (int) ((Math.min(ChatAttachAlertDocumentLayout.this.listView.getMeasuredHeight(), Math.max(0, childAt2.getTop())) / ChatAttachAlertDocumentLayout.this.listView.getMeasuredHeight()) * 100.0f);
                                ObjectAnimator ofFloat = ObjectAnimator.ofFloat(childAt2, (Property<View, Float>) View.ALPHA, 0.0f, 1.0f);
                                ofFloat.setStartDelay(min);
                                ofFloat.setDuration(200L);
                                animatorSet.playTogether(ofFloat);
                            }
                        }
                        animatorSet.addListener(new AnimatorListenerAdapter() {
                            AnonymousClass1() {
                            }

                            @Override
                            public void onAnimationEnd(Animator animator) {
                                SearchAdapter.this.notificationsLocker.unlock();
                            }
                        });
                        SearchAdapter.this.notificationsLocker.lock();
                        animatorSet.start();
                        View view2 = r2;
                        if (view2 != null && view2.getParent() == null) {
                            ChatAttachAlertDocumentLayout.this.listView.addView(r2);
                            RecyclerView.LayoutManager layoutManager2 = ChatAttachAlertDocumentLayout.this.listView.getLayoutManager();
                            if (layoutManager2 != null) {
                                layoutManager2.ignoreView(r2);
                                View view22 = r2;
                                ObjectAnimator ofFloat2 = ObjectAnimator.ofFloat(view22, (Property<View, Float>) View.ALPHA, view22.getAlpha(), 0.0f);
                                ofFloat2.addListener(new AnimatorListenerAdapter() {
                                    final RecyclerView.LayoutManager val$layoutManager;

                                    AnonymousClass2(RecyclerView.LayoutManager layoutManager22) {
                                        r2 = layoutManager22;
                                    }

                                    @Override
                                    public void onAnimationEnd(Animator animator) {
                                        r2.setAlpha(1.0f);
                                        r2.stopIgnoringView(r2);
                                        ChatAttachAlertDocumentLayout.this.listView.removeView(r2);
                                    }
                                });
                                ofFloat2.start();
                            }
                        }
                        return true;
                    }
                });
            }
            notifyDataSetChanged();
        }

        public void lambda$searchGlobal$3(final AccountInstance accountInstance, final String str, final int i, final boolean z, final long j, final long j2, final ArrayList arrayList, final ArrayList arrayList2, final TLObject tLObject, final TLRPC.TL_error tL_error) {
            final ArrayList arrayList3 = new ArrayList();
            if (tL_error == null) {
                TLRPC.messages_Messages messages_messages = (TLRPC.messages_Messages) tLObject;
                int size = messages_messages.messages.size();
                for (int i2 = 0; i2 < size; i2++) {
                    MessageObject messageObject = new MessageObject(accountInstance.getCurrentAccount(), messages_messages.messages.get(i2), false, true);
                    messageObject.setQuery(str);
                    arrayList3.add(messageObject);
                }
            }
            AndroidUtilities.runOnUIThread(new Runnable() {
                @Override
                public final void run() {
                    ChatAttachAlertDocumentLayout.SearchAdapter.this.lambda$searchGlobal$2(i, tL_error, tLObject, accountInstance, z, str, arrayList3, j, j2, arrayList, arrayList2);
                }
            });
        }

        public void lambda$searchGlobal$4(final long j, final String str, final AccountInstance accountInstance, final long j2, long j3, final boolean z, String str2, final int i) {
            TLRPC.InputPeer tL_inputPeerEmpty;
            long j4;
            TLRPC.TL_messages_searchGlobal tL_messages_searchGlobal;
            ArrayList<Object> arrayList = null;
            if (j != 0) {
                TLRPC.TL_messages_search tL_messages_search = new TLRPC.TL_messages_search();
                tL_messages_search.q = str;
                tL_messages_search.limit = 20;
                tL_messages_search.filter = this.currentSearchFilter.filter;
                tL_messages_search.peer = accountInstance.getMessagesController().getInputPeer(j);
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
                    accountInstance.getMessagesStorage().localSearch(0, str, arrayList, new ArrayList<>(), new ArrayList<>(), null, -1);
                }
                TLRPC.TL_messages_searchGlobal tL_messages_searchGlobal2 = new TLRPC.TL_messages_searchGlobal();
                tL_messages_searchGlobal2.limit = 20;
                tL_messages_searchGlobal2.q = str;
                tL_messages_searchGlobal2.filter = this.currentSearchFilter.filter;
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
                    TLRPC.Peer peer = messageObject.messageOwner.peer_id;
                    long j5 = peer.channel_id;
                    if (j5 == 0) {
                        j5 = peer.chat_id;
                        if (j5 == 0) {
                            j4 = peer.user_id;
                            tL_inputPeerEmpty = accountInstance.getMessagesController().getInputPeer(j4);
                        }
                    }
                    j4 = -j5;
                    tL_inputPeerEmpty = accountInstance.getMessagesController().getInputPeer(j4);
                } else {
                    tL_messages_searchGlobal2.offset_rate = 0;
                    tL_messages_searchGlobal2.offset_id = 0;
                    tL_inputPeerEmpty = new TLRPC.TL_inputPeerEmpty();
                }
                tL_messages_searchGlobal2.offset_peer = tL_inputPeerEmpty;
                tL_messages_searchGlobal = tL_messages_searchGlobal2;
            }
            TLRPC.TL_messages_searchGlobal tL_messages_searchGlobal3 = tL_messages_searchGlobal;
            final ArrayList<Object> arrayList2 = arrayList;
            this.lastMessagesSearchString = str;
            this.lastSearchFilterQueryString = str2;
            final ArrayList arrayList3 = new ArrayList();
            FiltersView.fillTipDates(this.lastMessagesSearchString, arrayList3);
            accountInstance.getConnectionsManager().sendRequest(tL_messages_searchGlobal3, new RequestDelegate() {
                @Override
                public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                    ChatAttachAlertDocumentLayout.SearchAdapter.this.lambda$searchGlobal$3(accountInstance, str, i, z, j, j2, arrayList2, arrayList3, tLObject, tL_error);
                }
            });
        }

        public void lambda$updateSearchResults$5(ArrayList arrayList) {
            if (ChatAttachAlertDocumentLayout.this.searching && ChatAttachAlertDocumentLayout.this.listView.getAdapter() != ChatAttachAlertDocumentLayout.this.searchAdapter) {
                ChatAttachAlertDocumentLayout.this.listView.setAdapter(ChatAttachAlertDocumentLayout.this.searchAdapter);
            }
            this.searchResult = arrayList;
            notifyDataSetChanged();
        }

        private void searchGlobal(final long j, final long j2, final long j3, FiltersView.MediaFilterData mediaFilterData, final String str, boolean z) {
            final String format = String.format(Locale.ENGLISH, "%d%d%d%d%s", Long.valueOf(j), Long.valueOf(j2), Long.valueOf(j3), Integer.valueOf(mediaFilterData.filterType), str);
            String str2 = this.lastSearchFilterQueryString;
            boolean z2 = str2 != null && str2.equals(format);
            boolean z3 = !z2 && z;
            this.currentSearchFilter = mediaFilterData;
            this.currentSearchDialogId = j;
            this.currentSearchMinDate = j2;
            this.currentSearchMaxDate = j3;
            Runnable runnable = this.searchRunnable;
            if (runnable != null) {
                AndroidUtilities.cancelRunOnUIThread(runnable);
            }
            AndroidUtilities.cancelRunOnUIThread(this.clearCurrentResultsRunnable);
            if (z2 && z) {
                return;
            }
            if (z3) {
                this.messages.clear();
                this.sections.clear();
                this.sectionArrays.clear();
                this.isLoading = true;
                ChatAttachAlertDocumentLayout.this.emptyView.setVisibility(0);
                notifyDataSetChanged();
                this.requestIndex++;
                this.firstLoading = true;
                if (ChatAttachAlertDocumentLayout.this.listView.getPinnedHeader() != null) {
                    ChatAttachAlertDocumentLayout.this.listView.getPinnedHeader().setAlpha(0.0f);
                }
                this.localTipChats.clear();
                this.localTipDates.clear();
            }
            this.isLoading = true;
            notifyDataSetChanged();
            if (!z2) {
                this.clearCurrentResultsRunnable.run();
                ChatAttachAlertDocumentLayout.this.emptyView.showProgress(true, !z);
            }
            if (TextUtils.isEmpty(str)) {
                this.localTipDates.clear();
                this.localTipChats.clear();
                updateFiltersView(false, null, null, true);
                return;
            }
            final int i = this.requestIndex + 1;
            this.requestIndex = i;
            final AccountInstance accountInstance = AccountInstance.getInstance(UserConfig.selectedAccount);
            final boolean z4 = z2;
            Runnable runnable2 = new Runnable() {
                @Override
                public final void run() {
                    ChatAttachAlertDocumentLayout.SearchAdapter.this.lambda$searchGlobal$4(j, str, accountInstance, j2, j3, z4, format, i);
                }
            };
            this.searchRunnable = runnable2;
            AndroidUtilities.runOnUIThread(runnable2, (!z2 || this.messages.isEmpty()) ? 350L : 0L);
            ChatAttachAlertDocumentLayout.this.loadingView.setViewType(3);
        }

        public void updateFiltersView(boolean r10, java.util.ArrayList r11, java.util.ArrayList r12, boolean r13) {
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.ChatAttachAlertDocumentLayout.SearchAdapter.updateFiltersView(boolean, java.util.ArrayList, java.util.ArrayList, boolean):void");
        }

        private void updateSearchResults(final ArrayList arrayList, String str) {
            AndroidUtilities.runOnUIThread(new Runnable() {
                @Override
                public final void run() {
                    ChatAttachAlertDocumentLayout.SearchAdapter.this.lambda$updateSearchResults$5(arrayList);
                }
            });
        }

        @Override
        public int getCountForSection(int i) {
            if (i == 0) {
                return this.searchResult.size();
            }
            int i2 = i - 1;
            int i3 = 1;
            if (i2 >= this.sections.size()) {
                return 1;
            }
            ArrayList arrayList = (ArrayList) this.sectionArrays.get(this.sections.get(i2));
            if (arrayList == null) {
                return 0;
            }
            int size = arrayList.size();
            if (i2 == 0 && this.searchResult.isEmpty()) {
                i3 = 0;
            }
            return size + i3;
        }

        @Override
        public Object getItem(int i, int i2) {
            ArrayList arrayList;
            if (i == 0) {
                if (i2 < this.searchResult.size()) {
                    return this.searchResult.get(i2);
                }
                return null;
            }
            int i3 = i - 1;
            if (i3 >= this.sections.size() || (arrayList = (ArrayList) this.sectionArrays.get(this.sections.get(i3))) == null) {
                return null;
            }
            int i4 = i2 - ((i3 == 0 && this.searchResult.isEmpty()) ? 0 : 1);
            if (i4 < 0 || i4 >= arrayList.size()) {
                return null;
            }
            return arrayList.get(i4);
        }

        @Override
        public int getItemViewType(int i, int i2) {
            if (i == 0) {
                return 1;
            }
            if (i == getSectionCount() - 1) {
                return 3;
            }
            int i3 = i - 1;
            if (i3 < this.sections.size()) {
                return (!(i3 == 0 && this.searchResult.isEmpty()) && i2 == 0) ? 0 : 4;
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
            if (this.sections.isEmpty()) {
                return 2;
            }
            return 2 + this.sections.size() + (!this.endReached ? 1 : 0);
        }

        @Override
        public View getSectionHeaderView(int i, View view) {
            GraySectionCell graySectionCell = (GraySectionCell) view;
            if (graySectionCell == null) {
                graySectionCell = new GraySectionCell(this.mContext, ChatAttachAlertDocumentLayout.this.resourcesProvider);
                graySectionCell.setBackgroundColor(ChatAttachAlertDocumentLayout.this.getThemedColor(Theme.key_graySection) & (-218103809));
            }
            if (i == 0 || (i == 1 && this.searchResult.isEmpty())) {
                graySectionCell.setAlpha(0.0f);
                return graySectionCell;
            }
            int i2 = i - 1;
            if (i2 < this.sections.size()) {
                graySectionCell.setAlpha(1.0f);
                if (((ArrayList) this.sectionArrays.get((String) this.sections.get(i2))) != null) {
                    graySectionCell.setText((i2 != 0 || this.searchResult.isEmpty()) ? LocaleController.formatSectionDate(((MessageObject) r1.get(0)).messageOwner.date) : LocaleController.getString(R.string.GlobalSearch));
                }
            }
            return view;
        }

        @Override
        public boolean isEnabled(RecyclerView.ViewHolder viewHolder, int i, int i2) {
            int itemViewType = viewHolder.getItemViewType();
            return itemViewType == 1 || itemViewType == 4;
        }

        public void loadMore() {
            FiltersView.MediaFilterData mediaFilterData;
            if (ChatAttachAlertDocumentLayout.this.searchAdapter.isLoading || ChatAttachAlertDocumentLayout.this.searchAdapter.endReached || (mediaFilterData = this.currentSearchFilter) == null) {
                return;
            }
            searchGlobal(this.currentSearchDialogId, this.currentSearchMinDate, this.currentSearchMaxDate, mediaFilterData, this.lastMessagesSearchString, false);
        }

        @Override
        public void notifyDataSetChanged() {
            super.notifyDataSetChanged();
            ChatAttachAlertDocumentLayout.this.updateEmptyView();
        }

        @Override
        public void onBindViewHolder(int i, int i2, RecyclerView.ViewHolder viewHolder) {
            String substring;
            String str;
            String str2;
            String str3;
            boolean z;
            int itemViewType = viewHolder.getItemViewType();
            if (itemViewType == 2 || itemViewType == 3) {
                return;
            }
            if (itemViewType == 0) {
                int i3 = i - 1;
                if (((ArrayList) this.sectionArrays.get((String) this.sections.get(i3))) == null) {
                    return;
                }
                ((GraySectionCell) viewHolder.itemView).setText((i3 != 0 || this.searchResult.isEmpty()) ? LocaleController.formatSectionDate(((MessageObject) r13.get(0)).messageOwner.date) : LocaleController.getString(R.string.GlobalSearch));
                return;
            }
            if (itemViewType == 1 || itemViewType == 4) {
                SharedDocumentCell sharedDocumentCell = (SharedDocumentCell) viewHolder.itemView;
                if (i != 0) {
                    int i4 = i - 1;
                    if (i4 != 0 || !this.searchResult.isEmpty()) {
                        i2--;
                    }
                    ArrayList arrayList = (ArrayList) this.sectionArrays.get((String) this.sections.get(i4));
                    if (arrayList == null) {
                        return;
                    }
                    MessageObject messageObject = (MessageObject) arrayList.get(i2);
                    boolean z2 = sharedDocumentCell.getMessage() != null && sharedDocumentCell.getMessage().getId() == messageObject.getId();
                    if (i2 != arrayList.size() - 1 || (i4 == this.sections.size() - 1 && this.isLoading)) {
                        r1 = true;
                    }
                    sharedDocumentCell.setDocument(messageObject, r1);
                    sharedDocumentCell.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                        final boolean val$animated;
                        final MessageObject val$messageObject;
                        final SharedDocumentCell val$sharedDocumentCell;

                        AnonymousClass4(SharedDocumentCell sharedDocumentCell2, MessageObject messageObject2, boolean z22) {
                            r2 = sharedDocumentCell2;
                            r3 = messageObject2;
                            r4 = z22;
                        }

                        @Override
                        public boolean onPreDraw() {
                            r2.getViewTreeObserver().removeOnPreDrawListener(this);
                            if (!ChatAttachAlertDocumentLayout.this.parentAlert.actionBar.isActionModeShowed()) {
                                r2.setChecked(false, r4);
                                return true;
                            }
                            SearchAdapter.this.messageHashIdTmp.set(r3.getId(), r3.getDialogId());
                            r2.setChecked(ChatAttachAlertDocumentLayout.this.selectedMessages.containsKey(SearchAdapter.this.messageHashIdTmp), r4);
                            return true;
                        }
                    });
                    return;
                }
                ListItem listItem = (ListItem) getItem(i2);
                SharedDocumentCell sharedDocumentCell2 = (SharedDocumentCell) viewHolder.itemView;
                int i5 = listItem.icon;
                if (i5 != 0) {
                    str = listItem.title;
                    str2 = listItem.subtitle;
                    str3 = null;
                    z = false;
                    substring = null;
                } else {
                    substring = listItem.ext.toUpperCase().substring(0, Math.min(listItem.ext.length(), 4));
                    str = listItem.title;
                    str2 = listItem.subtitle;
                    str3 = listItem.thumb;
                    i5 = 0;
                    z = false;
                }
                sharedDocumentCell2.setTextAndValueAndTypeAndThumb(str, str2, substring, str3, i5, z);
                sharedDocumentCell2.setChecked(listItem.file != null ? ChatAttachAlertDocumentLayout.this.selectedFiles.containsKey(listItem.file.toString()) : false, !ChatAttachAlertDocumentLayout.this.scrolling);
            }
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            GraySectionCell graySectionCell;
            if (i != 0) {
                if (i != 1) {
                    if (i == 2) {
                        ?? flickerLoadingView = new FlickerLoadingView(this.mContext, ChatAttachAlertDocumentLayout.this.resourcesProvider);
                        flickerLoadingView.setViewType(3);
                        flickerLoadingView.setIsSingleCell(true);
                        graySectionCell = flickerLoadingView;
                    } else if (i != 4) {
                        graySectionCell = new View(this.mContext);
                    }
                }
                ?? sharedDocumentCell = new SharedDocumentCell(this.mContext, i == 1 ? 1 : 2, ChatAttachAlertDocumentLayout.this.resourcesProvider);
                sharedDocumentCell.setDrawDownloadIcon(false);
                graySectionCell = sharedDocumentCell;
            } else {
                graySectionCell = new GraySectionCell(this.mContext, ChatAttachAlertDocumentLayout.this.resourcesProvider);
            }
            graySectionCell.setLayoutParams(new RecyclerView.LayoutParams(-1, -2));
            return new RecyclerListView.Holder(graySectionCell);
        }

        public void removeSearchFilter(FiltersView.MediaFilterData mediaFilterData) {
            this.currentSearchFilters.remove(mediaFilterData);
        }

        public void search(final String str, boolean z) {
            long j;
            Runnable runnable = this.localSearchRunnable;
            if (runnable != null) {
                AndroidUtilities.cancelRunOnUIThread(runnable);
                this.localSearchRunnable = null;
            }
            if (TextUtils.isEmpty(str)) {
                if (!this.searchResult.isEmpty()) {
                    this.searchResult.clear();
                }
                if (ChatAttachAlertDocumentLayout.this.listView.getAdapter() != ChatAttachAlertDocumentLayout.this.listAdapter) {
                    ChatAttachAlertDocumentLayout.this.listView.setAdapter(ChatAttachAlertDocumentLayout.this.listAdapter);
                }
                notifyDataSetChanged();
            } else {
                Runnable runnable2 = new Runnable() {
                    @Override
                    public final void run() {
                        ChatAttachAlertDocumentLayout.SearchAdapter.this.lambda$search$1(str);
                    }
                };
                this.localSearchRunnable = runnable2;
                AndroidUtilities.runOnUIThread(runnable2, 300L);
            }
            if (ChatAttachAlertDocumentLayout.this.canSelectOnlyImageFiles || !ChatAttachAlertDocumentLayout.this.listAdapter.history.isEmpty()) {
                return;
            }
            long j2 = 0;
            long j3 = 0;
            long j4 = 0;
            for (int i = 0; i < this.currentSearchFilters.size(); i++) {
                FiltersView.MediaFilterData mediaFilterData = (FiltersView.MediaFilterData) this.currentSearchFilters.get(i);
                int i2 = mediaFilterData.filterType;
                if (i2 == 4) {
                    TLObject tLObject = mediaFilterData.chat;
                    if (tLObject instanceof TLRPC.User) {
                        j = ((TLRPC.User) tLObject).id;
                    } else if (tLObject instanceof TLRPC.Chat) {
                        j = -((TLRPC.Chat) tLObject).id;
                    }
                    j2 = j;
                } else if (i2 == 6) {
                    FiltersView.DateData dateData = mediaFilterData.dateData;
                    j3 = dateData.minDate;
                    j4 = dateData.maxDate;
                }
            }
            searchGlobal(j2, j3, j4, FiltersView.filters[2], str, z);
        }
    }

    public ChatAttachAlertDocumentLayout(ChatAttachAlert chatAttachAlert, Context context, int i, Theme.ResourcesProvider resourcesProvider) {
        super(chatAttachAlert, context, resourcesProvider);
        this.receiverRegistered = false;
        this.selectedFiles = new HashMap();
        this.selectedFilesOrder = new ArrayList();
        this.selectedMessages = new HashMap();
        this.maxSelectedFiles = -1;
        this.receiver = new AnonymousClass1();
        this.listAdapter = new ListAdapter(context);
        this.allowMusic = i == 1;
        this.isSoundPicker = i == 2;
        this.sortByName = SharedConfig.sortFilesByName;
        loadRecentFiles();
        this.searching = false;
        if (!this.receiverRegistered) {
            this.receiverRegistered = true;
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction("android.intent.action.MEDIA_BAD_REMOVAL");
            intentFilter.addAction("android.intent.action.MEDIA_CHECKING");
            intentFilter.addAction("android.intent.action.MEDIA_EJECT");
            intentFilter.addAction("android.intent.action.MEDIA_MOUNTED");
            intentFilter.addAction("android.intent.action.MEDIA_NOFS");
            intentFilter.addAction("android.intent.action.MEDIA_REMOVED");
            intentFilter.addAction("android.intent.action.MEDIA_SHARED");
            intentFilter.addAction("android.intent.action.MEDIA_UNMOUNTABLE");
            intentFilter.addAction("android.intent.action.MEDIA_UNMOUNTED");
            intentFilter.addDataScheme("file");
            if (Build.VERSION.SDK_INT >= 33) {
                ApplicationLoader.applicationContext.registerReceiver(this.receiver, intentFilter, 4);
            } else {
                ApplicationLoader.applicationContext.registerReceiver(this.receiver, intentFilter);
            }
        }
        ActionBarMenu createMenu = this.parentAlert.actionBar.createMenu();
        ActionBarMenuItem actionBarMenuItemSearchListener = createMenu.addItem(0, R.drawable.ic_ab_search).setIsSearchField(true).setActionBarMenuItemSearchListener(new ActionBarMenuItem.ActionBarMenuItemSearchListener() {
            AnonymousClass2() {
            }

            @Override
            public void onSearchCollapse() {
                ChatAttachAlertDocumentLayout.this.searching = false;
                ChatAttachAlertDocumentLayout.this.sortItem.setVisibility(0);
                if (ChatAttachAlertDocumentLayout.this.listView.getAdapter() != ChatAttachAlertDocumentLayout.this.listAdapter) {
                    ChatAttachAlertDocumentLayout.this.listView.setAdapter(ChatAttachAlertDocumentLayout.this.listAdapter);
                }
                ChatAttachAlertDocumentLayout.this.listAdapter.notifyDataSetChanged();
                ChatAttachAlertDocumentLayout.this.searchAdapter.search(null, true);
            }

            @Override
            public void onSearchExpand() {
                ChatAttachAlertDocumentLayout.this.searching = true;
                ChatAttachAlertDocumentLayout.this.sortItem.setVisibility(8);
                ChatAttachAlertDocumentLayout chatAttachAlertDocumentLayout = ChatAttachAlertDocumentLayout.this;
                chatAttachAlertDocumentLayout.parentAlert.makeFocusable(chatAttachAlertDocumentLayout.searchItem.getSearchField(), true);
            }

            @Override
            public void onSearchFilterCleared(FiltersView.MediaFilterData mediaFilterData) {
                ChatAttachAlertDocumentLayout.this.searchAdapter.removeSearchFilter(mediaFilterData);
                ChatAttachAlertDocumentLayout.this.searchAdapter.search(ChatAttachAlertDocumentLayout.this.searchItem.getSearchField().getText().toString(), false);
                ChatAttachAlertDocumentLayout.this.searchAdapter.updateFiltersView(true, null, null, true);
            }

            @Override
            public void onTextChanged(EditText editText) {
                ChatAttachAlertDocumentLayout.this.searchAdapter.search(editText.getText().toString(), false);
            }
        });
        this.searchItem = actionBarMenuItemSearchListener;
        int i2 = R.string.Search;
        actionBarMenuItemSearchListener.setSearchFieldHint(LocaleController.getString(i2));
        this.searchItem.setContentDescription(LocaleController.getString(i2));
        EditTextBoldCursor searchField = this.searchItem.getSearchField();
        int i3 = Theme.key_dialogTextBlack;
        searchField.setTextColor(getThemedColor(i3));
        searchField.setCursorColor(getThemedColor(i3));
        searchField.setHintTextColor(getThemedColor(Theme.key_chat_messagePanelHint));
        ActionBarMenuItem addItem = createMenu.addItem(6, this.sortByName ? R.drawable.msg_contacts_time : R.drawable.msg_contacts_name);
        this.sortItem = addItem;
        addItem.setContentDescription(LocaleController.getString(R.string.AccDescrContactSorting));
        FlickerLoadingView flickerLoadingView = new FlickerLoadingView(context, resourcesProvider);
        this.loadingView = flickerLoadingView;
        addView(flickerLoadingView);
        AnonymousClass3 anonymousClass3 = new StickerEmptyView(context, this.loadingView, 1, resourcesProvider) {
            AnonymousClass3(Context context2, View view, int i4, Theme.ResourcesProvider resourcesProvider2) {
                super(context2, view, i4, resourcesProvider2);
            }

            @Override
            public float getTranslationY() {
                return super.getTranslationY() - ChatAttachAlertDocumentLayout.this.additionalTranslationY;
            }

            @Override
            public void setTranslationY(float f) {
                super.setTranslationY(f + ChatAttachAlertDocumentLayout.this.additionalTranslationY);
            }
        };
        this.emptyView = anonymousClass3;
        addView(anonymousClass3, LayoutHelper.createFrame(-1, -1.0f));
        this.emptyView.setVisibility(8);
        this.emptyView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public final boolean onTouch(View view, MotionEvent motionEvent) {
                boolean lambda$new$0;
                lambda$new$0 = ChatAttachAlertDocumentLayout.lambda$new$0(view, motionEvent);
                return lambda$new$0;
            }
        });
        AnonymousClass4 anonymousClass4 = new RecyclerListView(context2, resourcesProvider2) {
            Paint paint = new Paint();

            AnonymousClass4(Context context2, Theme.ResourcesProvider resourcesProvider2) {
                super(context2, resourcesProvider2);
                this.paint = new Paint();
            }

            @Override
            public void dispatchDraw(Canvas canvas) {
                if (ChatAttachAlertDocumentLayout.this.currentAnimationType == 2 && getChildCount() > 0) {
                    float f = 2.1474836E9f;
                    for (int i4 = 0; i4 < getChildCount(); i4++) {
                        if (getChildAt(i4).getY() < f) {
                            f = getChildAt(i4).getY();
                        }
                    }
                    this.paint.setColor(Theme.getColor(Theme.key_dialogBackground));
                }
                super.dispatchDraw(canvas);
            }

            @Override
            public boolean onTouchEvent(MotionEvent motionEvent) {
                if (ChatAttachAlertDocumentLayout.this.currentAnimationType != 0) {
                    return false;
                }
                return super.onTouchEvent(motionEvent);
            }
        };
        this.backgroundListView = anonymousClass4;
        anonymousClass4.setSectionsType(2);
        this.backgroundListView.setVerticalScrollBarEnabled(false);
        RecyclerListView recyclerListView = this.backgroundListView;
        FillLastLinearLayoutManager fillLastLinearLayoutManager = new FillLastLinearLayoutManager(context2, 1, false, AndroidUtilities.dp(56.0f), this.backgroundListView);
        this.backgroundLayoutManager = fillLastLinearLayoutManager;
        recyclerListView.setLayoutManager(fillLastLinearLayoutManager);
        this.backgroundListView.setClipToPadding(false);
        RecyclerListView recyclerListView2 = this.backgroundListView;
        ListAdapter listAdapter = new ListAdapter(context2);
        this.backgroundListAdapter = listAdapter;
        recyclerListView2.setAdapter(listAdapter);
        this.backgroundListView.setPadding(0, 0, 0, AndroidUtilities.dp(48.0f));
        addView(this.backgroundListView, LayoutHelper.createFrame(-1, -1.0f));
        this.backgroundListView.setVisibility(8);
        AnonymousClass5 anonymousClass5 = new RecyclerListView(context2, resourcesProvider2) {
            Paint paint = new Paint();

            AnonymousClass5(Context context2, Theme.ResourcesProvider resourcesProvider2) {
                super(context2, resourcesProvider2);
                this.paint = new Paint();
            }

            @Override
            public void dispatchDraw(Canvas canvas) {
                if (ChatAttachAlertDocumentLayout.this.currentAnimationType == 1 && getChildCount() > 0) {
                    float f = 2.1474836E9f;
                    for (int i4 = 0; i4 < getChildCount(); i4++) {
                        if (getChildAt(i4).getY() < f) {
                            f = getChildAt(i4).getY();
                        }
                    }
                    this.paint.setColor(Theme.getColor(Theme.key_dialogBackground));
                }
                super.dispatchDraw(canvas);
            }
        };
        this.listView = anonymousClass5;
        anonymousClass5.setSectionsType(2);
        this.listView.setVerticalScrollBarEnabled(false);
        RecyclerListView recyclerListView3 = this.listView;
        AnonymousClass6 anonymousClass6 = new FillLastLinearLayoutManager(context2, 1, false, AndroidUtilities.dp(56.0f), this.listView) {

            class AnonymousClass1 extends LinearSmoothScroller {
                AnonymousClass1(Context context) {
                    super(context);
                }

                @Override
                public int calculateDyToMakeVisible(View view, int i2) {
                    return super.calculateDyToMakeVisible(view, i2) - (ChatAttachAlertDocumentLayout.this.listView.getPaddingTop() - AndroidUtilities.dp(56.0f));
                }

                @Override
                public int calculateTimeForDeceleration(int i2) {
                    return super.calculateTimeForDeceleration(i2) * 2;
                }
            }

            AnonymousClass6(Context context2, int i4, boolean z, int i22, RecyclerView recyclerView) {
                super(context2, i4, z, i22, recyclerView);
            }

            @Override
            public void smoothScrollToPosition(RecyclerView recyclerView, RecyclerView.State state, int i4) {
                AnonymousClass1 anonymousClass1 = new LinearSmoothScroller(recyclerView.getContext()) {
                    AnonymousClass1(Context context2) {
                        super(context2);
                    }

                    @Override
                    public int calculateDyToMakeVisible(View view, int i22) {
                        return super.calculateDyToMakeVisible(view, i22) - (ChatAttachAlertDocumentLayout.this.listView.getPaddingTop() - AndroidUtilities.dp(56.0f));
                    }

                    @Override
                    public int calculateTimeForDeceleration(int i22) {
                        return super.calculateTimeForDeceleration(i22) * 2;
                    }
                };
                anonymousClass1.setTargetPosition(i4);
                startSmoothScroll(anonymousClass1);
            }
        };
        this.layoutManager = anonymousClass6;
        recyclerListView3.setLayoutManager(anonymousClass6);
        this.listView.setClipToPadding(false);
        this.listView.setAdapter(this.listAdapter);
        this.listView.setPadding(0, 0, 0, AndroidUtilities.dp(48.0f));
        addView(this.listView, LayoutHelper.createFrame(-1, -1.0f));
        this.searchAdapter = new SearchAdapter(context2);
        this.listView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            AnonymousClass7() {
            }

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int i4) {
                RecyclerListView.Holder holder;
                if (i4 == 0) {
                    int dp = AndroidUtilities.dp(13.0f);
                    int backgroundPaddingTop = ChatAttachAlertDocumentLayout.this.parentAlert.getBackgroundPaddingTop();
                    if (((ChatAttachAlertDocumentLayout.this.parentAlert.scrollOffsetY[0] - backgroundPaddingTop) - dp) + backgroundPaddingTop < ActionBar.getCurrentActionBarHeight() && (holder = (RecyclerListView.Holder) ChatAttachAlertDocumentLayout.this.listView.findViewHolderForAdapterPosition(0)) != null && holder.itemView.getTop() > AndroidUtilities.dp(56.0f)) {
                        ChatAttachAlertDocumentLayout.this.listView.smoothScrollBy(0, holder.itemView.getTop() - AndroidUtilities.dp(56.0f));
                    }
                }
                if (i4 == 1 && ChatAttachAlertDocumentLayout.this.searching && ChatAttachAlertDocumentLayout.this.listView.getAdapter() == ChatAttachAlertDocumentLayout.this.searchAdapter) {
                    AndroidUtilities.hideKeyboard(ChatAttachAlertDocumentLayout.this.parentAlert.getCurrentFocus());
                }
                ChatAttachAlertDocumentLayout.this.scrolling = i4 != 0;
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int i4, int i22) {
                ChatAttachAlertDocumentLayout chatAttachAlertDocumentLayout = ChatAttachAlertDocumentLayout.this;
                chatAttachAlertDocumentLayout.parentAlert.updateLayout(chatAttachAlertDocumentLayout, true, i22);
                ChatAttachAlertDocumentLayout.this.updateEmptyViewPosition();
                if (ChatAttachAlertDocumentLayout.this.listView.getAdapter() == ChatAttachAlertDocumentLayout.this.searchAdapter) {
                    int findFirstVisibleItemPosition = ChatAttachAlertDocumentLayout.this.layoutManager.findFirstVisibleItemPosition();
                    int findLastVisibleItemPosition = ChatAttachAlertDocumentLayout.this.layoutManager.findLastVisibleItemPosition();
                    int abs = Math.abs(findLastVisibleItemPosition - findFirstVisibleItemPosition) + 1;
                    int itemCount = recyclerView.getAdapter().getItemCount();
                    if (abs <= 0 || findLastVisibleItemPosition < itemCount - 10) {
                        return;
                    }
                    ChatAttachAlertDocumentLayout.this.searchAdapter.loadMore();
                }
            }
        });
        this.listView.setOnItemClickListener(new RecyclerListView.OnItemClickListener() {
            @Override
            public final void onItemClick(View view, int i4) {
                ChatAttachAlertDocumentLayout.this.lambda$new$1(view, i4);
            }
        });
        this.listView.setOnItemLongClickListener(new RecyclerListView.OnItemLongClickListener() {
            @Override
            public final boolean onItemClick(View view, int i4) {
                boolean lambda$new$2;
                lambda$new$2 = ChatAttachAlertDocumentLayout.this.lambda$new$2(view, i4);
                return lambda$new$2;
            }
        });
        FiltersView filtersView = new FiltersView(context2, resourcesProvider2);
        this.filtersView = filtersView;
        filtersView.setOnItemClickListener(new RecyclerListView.OnItemClickListener() {
            @Override
            public final void onItemClick(View view, int i4) {
                ChatAttachAlertDocumentLayout.this.lambda$new$3(view, i4);
            }
        });
        this.filtersView.setBackgroundColor(getThemedColor(Theme.key_dialogBackground));
        addView(this.filtersView, LayoutHelper.createFrame(-1, -2, 48));
        this.filtersView.setTranslationY(-AndroidUtilities.dp(44.0f));
        this.filtersView.setVisibility(4);
        listRoots();
        updateSearchButton();
        updateEmptyView();
    }

    private boolean canClosePicker() {
        if (this.listAdapter.history.size() <= 0) {
            return true;
        }
        prepareAnimation();
        HistoryEntry historyEntry = (HistoryEntry) this.listAdapter.history.remove(this.listAdapter.history.size() - 1);
        this.parentAlert.actionBar.setTitle(historyEntry.title);
        int topForScroll = getTopForScroll();
        File file = historyEntry.dir;
        if (file != null) {
            listFiles(file);
        } else {
            listRoots();
        }
        updateSearchButton();
        this.layoutManager.scrollToPositionWithOffset(0, topForScroll);
        runAnimation(2);
        return false;
    }

    private void checkDirectory(File file) {
        File[] listFiles = file.listFiles();
        File checkDirectory = FileLoader.checkDirectory(6);
        if (listFiles != null) {
            for (File file2 : listFiles) {
                if (file2.isDirectory() && file2.getName().equals("Telegram")) {
                    checkDirectory(file2);
                } else if (!file2.equals(checkDirectory)) {
                    ListItem listItem = new ListItem(null);
                    listItem.title = file2.getName();
                    listItem.file = file2;
                    String name = file2.getName();
                    String[] split = name.split("\\.");
                    listItem.ext = split.length > 1 ? split[split.length - 1] : "?";
                    listItem.subtitle = AndroidUtilities.formatFileSize(file2.length());
                    String lowerCase = name.toLowerCase();
                    if (lowerCase.endsWith(".jpg") || lowerCase.endsWith(".png") || lowerCase.endsWith(".gif") || lowerCase.endsWith(".jpeg")) {
                        listItem.thumb = file2.getAbsolutePath();
                    }
                    this.listAdapter.recentItems.add(listItem);
                }
            }
        }
    }

    private int getTopForScroll() {
        View childAt = this.listView.getChildAt(0);
        RecyclerView.ViewHolder findContainingViewHolder = this.listView.findContainingViewHolder(childAt);
        int i = -this.listView.getPaddingTop();
        return (findContainingViewHolder == null || findContainingViewHolder.getAdapterPosition() != 0) ? i : i + childAt.getTop();
    }

    public static boolean lambda$new$0(View view, MotionEvent motionEvent) {
        return true;
    }

    public void lambda$new$1(View view, int i) {
        int i2;
        RecyclerView.Adapter adapter = this.listView.getAdapter();
        ListAdapter listAdapter = this.listAdapter;
        Object item = adapter == listAdapter ? listAdapter.getItem(i) : this.searchAdapter.getItem(i);
        boolean z = item instanceof ListItem;
        Object obj = item;
        if (z) {
            ListItem listItem = (ListItem) item;
            File file = listItem.file;
            boolean isExternalStorageManager = Build.VERSION.SDK_INT >= 30 ? Environment.isExternalStorageManager() : false;
            if (!BuildVars.NO_SCOPED_STORAGE && (((i2 = listItem.icon) == R.drawable.files_storage || i2 == R.drawable.files_internal) && !isExternalStorageManager)) {
                this.delegate.startDocumentSelectActivity();
                return;
            }
            if (file == null) {
                int i3 = listItem.icon;
                if (i3 == R.drawable.files_gallery) {
                    HashMap hashMap = new HashMap();
                    ArrayList arrayList = new ArrayList();
                    BaseFragment baseFragment = this.parentAlert.baseFragment;
                    ChatActivity chatActivity = baseFragment instanceof ChatActivity ? (ChatActivity) baseFragment : null;
                    PhotoPickerActivity photoPickerActivity = new PhotoPickerActivity(0, MediaController.allMediaAlbumEntry, hashMap, arrayList, 0, chatActivity != null, chatActivity, false);
                    photoPickerActivity.setDocumentsPicker(true);
                    photoPickerActivity.setDelegate(new PhotoPickerActivity.PhotoPickerActivityDelegate() {
                        final HashMap val$selectedPhotos;
                        final ArrayList val$selectedPhotosOrder;

                        AnonymousClass8(HashMap hashMap2, ArrayList arrayList2) {
                            r2 = hashMap2;
                            r3 = arrayList2;
                        }

                        @Override
                        public void actionButtonPressed(boolean z2, boolean z22, int i4) {
                            if (z2) {
                                return;
                            }
                            ChatAttachAlertDocumentLayout.this.sendSelectedPhotos(r2, r3, z22, i4);
                        }

                        @Override
                        public boolean canFinishFragment() {
                            return PhotoPickerActivity.PhotoPickerActivityDelegate.CC.$default$canFinishFragment(this);
                        }

                        @Override
                        public void onCaptionChanged(CharSequence charSequence) {
                        }

                        @Override
                        public void onOpenInPressed() {
                            ChatAttachAlertDocumentLayout.this.delegate.startDocumentSelectActivity();
                        }

                        @Override
                        public void selectedPhotosChanged() {
                        }
                    });
                    photoPickerActivity.setMaxSelectedPhotos(this.maxSelectedFiles, false);
                    this.parentAlert.presentFragment(photoPickerActivity);
                    this.parentAlert.dismiss(true);
                    return;
                }
                if (i3 == R.drawable.files_music) {
                    DocumentSelectActivityDelegate documentSelectActivityDelegate = this.delegate;
                    if (documentSelectActivityDelegate != null) {
                        documentSelectActivityDelegate.startMusicSelectActivity();
                        return;
                    }
                    return;
                }
                int topForScroll = getTopForScroll();
                prepareAnimation();
                HistoryEntry historyEntry = (HistoryEntry) this.listAdapter.history.remove(this.listAdapter.history.size() - 1);
                this.parentAlert.actionBar.setTitle(historyEntry.title);
                File file2 = historyEntry.dir;
                if (file2 != null) {
                    listFiles(file2);
                } else {
                    listRoots();
                }
                updateSearchButton();
                this.layoutManager.scrollToPositionWithOffset(0, topForScroll);
                runAnimation(2);
                return;
            }
            obj = listItem;
            if (file.isDirectory()) {
                HistoryEntry historyEntry2 = new HistoryEntry(null);
                View childAt = this.listView.getChildAt(0);
                RecyclerView.ViewHolder findContainingViewHolder = this.listView.findContainingViewHolder(childAt);
                if (findContainingViewHolder != null) {
                    historyEntry2.scrollItem = findContainingViewHolder.getAdapterPosition();
                    historyEntry2.scrollOffset = childAt.getTop();
                    historyEntry2.dir = this.currentDir;
                    historyEntry2.title = this.parentAlert.actionBar.getTitle();
                    prepareAnimation();
                    this.listAdapter.history.add(historyEntry2);
                    if (!listFiles(file)) {
                        this.listAdapter.history.remove(historyEntry2);
                        return;
                    } else {
                        runAnimation(1);
                        this.parentAlert.actionBar.setTitle(listItem.title);
                        return;
                    }
                }
                return;
            }
        }
        onItemClick(view, obj);
    }

    public boolean lambda$new$2(View view, int i) {
        RecyclerView.Adapter adapter = this.listView.getAdapter();
        ListAdapter listAdapter = this.listAdapter;
        return onItemClick(view, adapter == listAdapter ? listAdapter.getItem(i) : this.searchAdapter.getItem(i));
    }

    public void lambda$new$3(View view, int i) {
        this.filtersView.cancelClickRunnables(true);
        this.searchAdapter.addSearchFilter(this.filtersView.getFilterAt(i));
    }

    public void lambda$runAnimation$4(int i, float f, ValueAnimator valueAnimator) {
        float floatValue = ((Float) valueAnimator.getAnimatedValue()).floatValue();
        if (i == 1) {
            this.listView.setTranslationX(f * floatValue);
            this.listView.setAlpha(1.0f - floatValue);
            this.listView.invalidate();
            this.backgroundListView.setAlpha(floatValue);
            float f2 = (floatValue * 0.05f) + 0.95f;
            this.backgroundListView.setScaleX(f2);
            this.backgroundListView.setScaleY(f2);
            return;
        }
        this.backgroundListView.setTranslationX(f * floatValue);
        this.backgroundListView.setAlpha(Math.max(0.0f, 1.0f - floatValue));
        this.backgroundListView.invalidate();
        this.listView.setAlpha(floatValue);
        float f3 = (floatValue * 0.05f) + 0.95f;
        this.listView.setScaleX(f3);
        this.listView.setScaleY(f3);
        this.backgroundListView.invalidate();
    }

    public int lambda$sortFileItems$6(ListItem listItem, ListItem listItem2) {
        File file = listItem.file;
        if (file == null) {
            return -1;
        }
        if (listItem2.file == null) {
            return 1;
        }
        boolean isDirectory = file.isDirectory();
        if (isDirectory != listItem2.file.isDirectory()) {
            return isDirectory ? -1 : 1;
        }
        if (isDirectory || this.sortByName) {
            return listItem.file.getName().compareToIgnoreCase(listItem2.file.getName());
        }
        long lastModified = listItem.file.lastModified();
        long lastModified2 = listItem2.file.lastModified();
        if (lastModified == lastModified2) {
            return 0;
        }
        return lastModified > lastModified2 ? -1 : 1;
    }

    public int lambda$sortRecentItems$5(ListItem listItem, ListItem listItem2) {
        boolean z = this.sortByName;
        File file = listItem.file;
        if (z) {
            return file.getName().compareToIgnoreCase(listItem2.file.getName());
        }
        long lastModified = file.lastModified();
        long lastModified2 = listItem2.file.lastModified();
        if (lastModified == lastModified2) {
            return 0;
        }
        return lastModified > lastModified2 ? -1 : 1;
    }

    public boolean listFiles(File file) {
        String localizedMessage;
        File file2;
        int i;
        this.hasFiles = false;
        if (file.canRead()) {
            try {
                File[] listFiles = file.listFiles();
                if (listFiles != null) {
                    this.currentDir = file;
                    this.listAdapter.items.clear();
                    File checkDirectory = FileLoader.checkDirectory(6);
                    for (File file3 : listFiles) {
                        if (file3.getName().indexOf(46) != 0 && !file3.equals(checkDirectory)) {
                            ListItem listItem = new ListItem(null);
                            listItem.title = file3.getName();
                            listItem.file = file3;
                            if (file3.isDirectory()) {
                                listItem.icon = R.drawable.files_folder;
                                listItem.subtitle = LocaleController.getString(R.string.Folder);
                            } else {
                                this.hasFiles = true;
                                String name = file3.getName();
                                String[] split = name.split("\\.");
                                listItem.ext = split.length > 1 ? split[split.length - 1] : "?";
                                listItem.subtitle = AndroidUtilities.formatFileSize(file3.length());
                                String lowerCase = name.toLowerCase();
                                if (lowerCase.endsWith(".jpg") || lowerCase.endsWith(".png") || lowerCase.endsWith(".gif") || lowerCase.endsWith(".jpeg")) {
                                    listItem.thumb = file3.getAbsolutePath();
                                }
                            }
                            this.listAdapter.items.add(listItem);
                        }
                    }
                    ListItem listItem2 = new ListItem(null);
                    listItem2.title = "..";
                    listItem2.subtitle = (this.listAdapter.history.size() <= 0 || (file2 = ((HistoryEntry) this.listAdapter.history.get(this.listAdapter.history.size() - 1)).dir) == null) ? LocaleController.getString(R.string.Folder) : file2.toString();
                    listItem2.icon = R.drawable.files_folder;
                    listItem2.file = null;
                    this.listAdapter.items.add(0, listItem2);
                    sortFileItems();
                    updateSearchButton();
                    AndroidUtilities.clearDrawableAnimation(this.listView);
                    this.scrolling = true;
                    int topForScroll = getTopForScroll();
                    this.listAdapter.notifyDataSetChanged();
                    this.layoutManager.scrollToPositionWithOffset(0, topForScroll);
                    return true;
                }
                i = R.string.UnknownError;
            } catch (Exception e) {
                localizedMessage = e.getLocalizedMessage();
            }
        } else {
            if ((file.getAbsolutePath().startsWith(Environment.getExternalStorageDirectory().toString()) || file.getAbsolutePath().startsWith("/sdcard") || file.getAbsolutePath().startsWith("/mnt/sdcard")) && !Environment.getExternalStorageState().equals("mounted") && !Environment.getExternalStorageState().equals("mounted_ro")) {
                this.currentDir = file;
                this.listAdapter.items.clear();
                Environment.getExternalStorageState();
                AndroidUtilities.clearDrawableAnimation(this.listView);
                this.scrolling = true;
                this.listAdapter.notifyDataSetChanged();
                return true;
            }
            i = R.string.AccessError;
        }
        localizedMessage = LocaleController.getString(i);
        showErrorBox(localizedMessage);
        return false;
    }

    public void listRoots() {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.ChatAttachAlertDocumentLayout.listRoots():void");
    }

    private boolean onItemClick(View view, Object obj) {
        boolean z;
        boolean z2 = false;
        if (obj instanceof ListItem) {
            ListItem listItem = (ListItem) obj;
            File file = listItem.file;
            if (file == null || file.isDirectory()) {
                return false;
            }
            String absolutePath = listItem.file.getAbsolutePath();
            if (this.selectedFiles.containsKey(absolutePath)) {
                this.selectedFiles.remove(absolutePath);
                this.selectedFilesOrder.remove(absolutePath);
                z = false;
            } else {
                if (!listItem.file.canRead()) {
                    showErrorBox(LocaleController.getString(R.string.AccessError));
                    return false;
                }
                if (this.canSelectOnlyImageFiles && listItem.thumb == null) {
                    showErrorBox(LocaleController.formatString("PassportUploadNotImage", R.string.PassportUploadNotImage, new Object[0]));
                    return false;
                }
                if ((listItem.file.length() > 2097152000 && !UserConfig.getInstance(UserConfig.selectedAccount).isPremium()) || listItem.file.length() > 4194304000L) {
                    ChatAttachAlert chatAttachAlert = this.parentAlert;
                    LimitReachedBottomSheet limitReachedBottomSheet = new LimitReachedBottomSheet(chatAttachAlert.baseFragment, chatAttachAlert.getContainer().getContext(), 6, UserConfig.selectedAccount, null);
                    limitReachedBottomSheet.setVeryLargeFile(true);
                    limitReachedBottomSheet.show();
                    return false;
                }
                if (this.maxSelectedFiles >= 0) {
                    int size = this.selectedFiles.size();
                    int i = this.maxSelectedFiles;
                    if (size >= i) {
                        showErrorBox(LocaleController.formatString("PassportUploadMaxReached", R.string.PassportUploadMaxReached, LocaleController.formatPluralString("Files", i, new Object[0])));
                        return false;
                    }
                }
                if ((this.isSoundPicker && !isRingtone(listItem.file)) || listItem.file.length() == 0) {
                    return false;
                }
                boolean z3 = this.parentAlert.storyMediaPicker;
                this.selectedFiles.put(absolutePath, listItem);
                this.selectedFilesOrder.add(absolutePath);
                z = true;
            }
            this.scrolling = false;
            z2 = z;
        } else {
            if (!(obj instanceof MessageObject)) {
                return false;
            }
            MessageObject messageObject = (MessageObject) obj;
            FilteredSearchView.MessageHashId messageHashId = new FilteredSearchView.MessageHashId(messageObject.getId(), messageObject.getDialogId());
            if (this.selectedMessages.containsKey(messageHashId)) {
                this.selectedMessages.remove(messageHashId);
            } else {
                if (this.selectedMessages.size() >= 100) {
                    return false;
                }
                this.selectedMessages.put(messageHashId, messageObject);
                z2 = true;
            }
        }
        if (view instanceof SharedDocumentCell) {
            ((SharedDocumentCell) view).setChecked(z2, true);
        }
        this.parentAlert.updateCountButton(z2 ? 1 : 2);
        return true;
    }

    private void prepareAnimation() {
        View findViewByPosition;
        this.backgroundListAdapter.history.clear();
        this.backgroundListAdapter.history.addAll(this.listAdapter.history);
        this.backgroundListAdapter.items.clear();
        this.backgroundListAdapter.items.addAll(this.listAdapter.items);
        this.backgroundListAdapter.recentItems.clear();
        this.backgroundListAdapter.recentItems.addAll(this.listAdapter.recentItems);
        this.backgroundListAdapter.notifyDataSetChanged();
        this.backgroundListView.setVisibility(0);
        this.backgroundListView.setPadding(this.listView.getPaddingLeft(), this.listView.getPaddingTop(), this.listView.getPaddingRight(), this.listView.getPaddingBottom());
        int findFirstVisibleItemPosition = this.layoutManager.findFirstVisibleItemPosition();
        if (findFirstVisibleItemPosition < 0 || (findViewByPosition = this.layoutManager.findViewByPosition(findFirstVisibleItemPosition)) == null) {
            return;
        }
        this.backgroundLayoutManager.scrollToPositionWithOffset(findFirstVisibleItemPosition, findViewByPosition.getTop() - this.backgroundListView.getPaddingTop());
    }

    private void runAnimation(final int i) {
        ValueAnimator ofFloat;
        ValueAnimator valueAnimator;
        long j;
        ValueAnimator valueAnimator2 = this.listAnimation;
        if (valueAnimator2 != null) {
            valueAnimator2.cancel();
        }
        this.currentAnimationType = i;
        int i2 = 0;
        while (true) {
            if (i2 >= getChildCount()) {
                i2 = 0;
                break;
            } else if (getChildAt(i2) == this.listView) {
                break;
            } else {
                i2++;
            }
        }
        final float dp = AndroidUtilities.dp(150.0f);
        if (i == 1) {
            this.backgroundListView.setAlpha(1.0f);
            this.backgroundListView.setScaleX(1.0f);
            this.backgroundListView.setScaleY(1.0f);
            this.backgroundListView.setTranslationX(0.0f);
            removeView(this.backgroundListView);
            addView(this.backgroundListView, i2);
            this.backgroundListView.setVisibility(0);
            this.listView.setTranslationX(dp);
            this.listView.setAlpha(0.0f);
            ofFloat = ValueAnimator.ofFloat(1.0f, 0.0f);
        } else {
            this.listView.setAlpha(0.0f);
            this.listView.setScaleX(0.95f);
            this.listView.setScaleY(0.95f);
            this.backgroundListView.setScaleX(1.0f);
            this.backgroundListView.setScaleY(1.0f);
            this.backgroundListView.setTranslationX(0.0f);
            this.backgroundListView.setAlpha(1.0f);
            removeView(this.backgroundListView);
            addView(this.backgroundListView, i2 + 1);
            this.backgroundListView.setVisibility(0);
            ofFloat = ValueAnimator.ofFloat(0.0f, 1.0f);
        }
        this.listAnimation = ofFloat;
        this.listAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public final void onAnimationUpdate(ValueAnimator valueAnimator3) {
                ChatAttachAlertDocumentLayout.this.lambda$runAnimation$4(i, dp, valueAnimator3);
            }
        });
        this.listAnimation.addListener(new AnimatorListenerAdapter() {
            AnonymousClass9() {
            }

            @Override
            public void onAnimationEnd(Animator animator) {
                super.onAnimationEnd(animator);
                ChatAttachAlertDocumentLayout.this.backgroundListView.setVisibility(8);
                ChatAttachAlertDocumentLayout.this.currentAnimationType = 0;
                ChatAttachAlertDocumentLayout.this.listView.setAlpha(1.0f);
                ChatAttachAlertDocumentLayout.this.listView.setScaleX(1.0f);
                ChatAttachAlertDocumentLayout.this.listView.setScaleY(1.0f);
                ChatAttachAlertDocumentLayout.this.listView.setTranslationX(0.0f);
                ChatAttachAlertDocumentLayout.this.listView.invalidate();
            }
        });
        if (i == 1) {
            valueAnimator = this.listAnimation;
            j = 220;
        } else {
            valueAnimator = this.listAnimation;
            j = 200;
        }
        valueAnimator.setDuration(j);
        this.listAnimation.setInterpolator(CubicBezierInterpolator.DEFAULT);
        this.listAnimation.start();
    }

    public void sendSelectedPhotos(HashMap hashMap, ArrayList arrayList, boolean z, int i) {
        if (hashMap.isEmpty() || this.delegate == null || this.sendPressed) {
            return;
        }
        this.sendPressed = true;
        ArrayList arrayList2 = new ArrayList();
        for (int i2 = 0; i2 < arrayList.size(); i2++) {
            Object obj = hashMap.get(arrayList.get(i2));
            SendMessagesHelper.SendingMediaInfo sendingMediaInfo = new SendMessagesHelper.SendingMediaInfo();
            arrayList2.add(sendingMediaInfo);
            if (obj instanceof MediaController.PhotoEntry) {
                MediaController.PhotoEntry photoEntry = (MediaController.PhotoEntry) obj;
                String str = photoEntry.imagePath;
                if (str == null) {
                    str = photoEntry.path;
                }
                sendingMediaInfo.path = str;
                sendingMediaInfo.thumbPath = photoEntry.thumbPath;
                sendingMediaInfo.coverPath = photoEntry.coverPath;
                sendingMediaInfo.videoEditedInfo = photoEntry.editedInfo;
                sendingMediaInfo.isVideo = photoEntry.isVideo;
                CharSequence charSequence = photoEntry.caption;
                sendingMediaInfo.caption = charSequence != null ? charSequence.toString() : null;
                sendingMediaInfo.entities = photoEntry.entities;
                sendingMediaInfo.masks = photoEntry.stickers;
                sendingMediaInfo.ttl = photoEntry.ttl;
            }
        }
        this.delegate.didSelectPhotos(arrayList2, z, i);
    }

    private void showErrorBox(String str) {
        new AlertDialog.Builder(getContext(), this.resourcesProvider).setTitle(LocaleController.getString(R.string.AppName)).setMessage(str).setPositiveButton(LocaleController.getString(R.string.OK), null).show();
    }

    private void sortFileItems() {
        if (this.currentDir == null) {
            return;
        }
        Collections.sort(this.listAdapter.items, new Comparator() {
            @Override
            public final int compare(Object obj, Object obj2) {
                int lambda$sortFileItems$6;
                lambda$sortFileItems$6 = ChatAttachAlertDocumentLayout.this.lambda$sortFileItems$6((ChatAttachAlertDocumentLayout.ListItem) obj, (ChatAttachAlertDocumentLayout.ListItem) obj2);
                return lambda$sortFileItems$6;
            }
        });
    }

    private void sortRecentItems() {
        Collections.sort(this.listAdapter.recentItems, new Comparator() {
            @Override
            public final int compare(Object obj, Object obj2) {
                int lambda$sortRecentItems$5;
                lambda$sortRecentItems$5 = ChatAttachAlertDocumentLayout.this.lambda$sortRecentItems$5((ChatAttachAlertDocumentLayout.ListItem) obj, (ChatAttachAlertDocumentLayout.ListItem) obj2);
                return lambda$sortRecentItems$5;
            }
        });
    }

    public void updateEmptyView() {
        RecyclerView.Adapter adapter = this.listView.getAdapter();
        SearchAdapter searchAdapter = this.searchAdapter;
        boolean z = true;
        if (adapter != searchAdapter ? this.listAdapter.getItemCount() != 1 : !searchAdapter.searchResult.isEmpty() || !this.searchAdapter.sections.isEmpty()) {
            z = false;
        }
        this.emptyView.setVisibility(z ? 0 : 8);
        updateEmptyViewPosition();
    }

    public void updateEmptyViewPosition() {
        View childAt;
        if (this.emptyView.getVisibility() == 0 && (childAt = this.listView.getChildAt(0)) != null) {
            float translationY = this.emptyView.getTranslationY();
            this.additionalTranslationY = ((this.emptyView.getMeasuredHeight() - getMeasuredHeight()) + childAt.getTop()) / 2;
            this.emptyView.setTranslationY(translationY);
        }
    }

    public void updateSearchButton() {
        ActionBarMenuItem actionBarMenuItem = this.searchItem;
        if (actionBarMenuItem == null || actionBarMenuItem.isSearchFieldVisible()) {
            return;
        }
        this.searchItem.setVisibility((this.hasFiles || this.listAdapter.history.isEmpty()) ? 0 : 8);
    }

    @Override
    public int getButtonsHideOffset() {
        return AndroidUtilities.dp(62.0f);
    }

    @Override
    public int getCurrentItemTop() {
        if (this.listView.getChildCount() <= 0) {
            return Integer.MAX_VALUE;
        }
        int i = 0;
        View childAt = this.listView.getChildAt(0);
        RecyclerListView.Holder holder = (RecyclerListView.Holder) this.listView.findContainingViewHolder(childAt);
        int y = ((int) childAt.getY()) - AndroidUtilities.dp(8.0f);
        if (y > 0 && holder != null && holder.getAdapterPosition() == 0) {
            i = y;
        }
        if (y < 0 || holder == null || holder.getAdapterPosition() != 0) {
            y = i;
        }
        return y + AndroidUtilities.dp(13.0f);
    }

    @Override
    public int getFirstOffset() {
        return getListTopPadding() + AndroidUtilities.dp(5.0f);
    }

    @Override
    public int getListTopPadding() {
        return this.listView.getPaddingTop();
    }

    @Override
    public int getSelectedItemsCount() {
        return this.selectedFiles.size() + this.selectedMessages.size();
    }

    @Override
    public ArrayList<ThemeDescription> getThemeDescriptions() {
        ArrayList<ThemeDescription> arrayList = new ArrayList<>();
        arrayList.add(new ThemeDescription(this.searchItem.getSearchField(), ThemeDescription.FLAG_CURSORCOLOR, null, null, null, null, Theme.key_dialogTextBlack));
        arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_LISTGLOWCOLOR, null, null, null, null, Theme.key_dialogScrollGlow));
        arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{ShadowSectionCell.class}, null, null, null, Theme.key_windowBackgroundGrayShadow));
        arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER | ThemeDescription.FLAG_CELLBACKGROUNDCOLOR, new Class[]{ShadowSectionCell.class}, null, null, null, Theme.key_windowBackgroundGray));
        arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_SELECTOR, null, null, null, null, Theme.key_listSelector));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{View.class}, Theme.dividerPaint, null, null, Theme.key_divider));
        arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{SharedDocumentCell.class}, new String[]{"nameTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteBlackText));
        arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{SharedDocumentCell.class}, new String[]{"dateTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteGrayText3));
        arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_CHECKBOX, new Class[]{SharedDocumentCell.class}, new String[]{"checkBox"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_checkbox));
        arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_CHECKBOXCHECK, new Class[]{SharedDocumentCell.class}, new String[]{"checkBox"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_checkboxCheck));
        arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_IMAGECOLOR, new Class[]{SharedDocumentCell.class}, new String[]{"thumbImageView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_files_folderIcon));
        arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_IMAGECOLOR | ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{SharedDocumentCell.class}, new String[]{"thumbImageView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_files_folderIconBackground));
        arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{SharedDocumentCell.class}, new String[]{"extTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_files_iconText));
        return arrayList;
    }

    public boolean isRingtone(File file) {
        Bulletin createErrorBulletinSubtitle;
        int i;
        BulletinFactory of;
        String formatString;
        String formatString2;
        String fileExtension = FileLoader.getFileExtension(file);
        String mimeTypeFromExtension = fileExtension != null ? MimeTypeMap.getSingleton().getMimeTypeFromExtension(fileExtension) : null;
        if (file.length() == 0 || mimeTypeFromExtension == null || !RingtoneDataStore.ringtoneSupportedMimeType.contains(mimeTypeFromExtension)) {
            createErrorBulletinSubtitle = BulletinFactory.of(this.parentAlert.getContainer(), null).createErrorBulletinSubtitle(LocaleController.formatString("InvalidFormatError", R.string.InvalidFormatError, new Object[0]), LocaleController.getString(R.string.ErrorRingtoneInvalidFormat), null);
        } else {
            if (file.length() > MessagesController.getInstance(UserConfig.selectedAccount).ringtoneSizeMax) {
                of = BulletinFactory.of(this.parentAlert.getContainer(), null);
                formatString = LocaleController.formatString("TooLargeError", R.string.TooLargeError, new Object[0]);
                formatString2 = LocaleController.formatString("ErrorRingtoneSizeTooBig", R.string.ErrorRingtoneSizeTooBig, Integer.valueOf(MessagesController.getInstance(UserConfig.selectedAccount).ringtoneSizeMax / 1024));
            } else {
                try {
                    MediaMetadataRetriever mediaMetadataRetriever = new MediaMetadataRetriever();
                    mediaMetadataRetriever.setDataSource(ApplicationLoader.applicationContext, Uri.fromFile(file));
                    i = Integer.parseInt(mediaMetadataRetriever.extractMetadata(9));
                } catch (Exception unused) {
                    i = Integer.MAX_VALUE;
                }
                if (i <= MessagesController.getInstance(UserConfig.selectedAccount).ringtoneDurationMax * 1000) {
                    return true;
                }
                of = BulletinFactory.of(this.parentAlert.getContainer(), null);
                formatString = LocaleController.formatString("TooLongError", R.string.TooLongError, new Object[0]);
                formatString2 = LocaleController.formatString("ErrorRingtoneDurationTooLong", R.string.ErrorRingtoneDurationTooLong, Integer.valueOf(MessagesController.getInstance(UserConfig.selectedAccount).ringtoneDurationMax));
            }
            createErrorBulletinSubtitle = of.createErrorBulletinSubtitle(formatString, formatString2, null);
        }
        createErrorBulletinSubtitle.show();
        return false;
    }

    public void loadRecentFiles() {
        try {
            if (!this.isSoundPicker) {
                checkDirectory(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS));
                sortRecentItems();
                return;
            }
            try {
                Cursor query = ApplicationLoader.applicationContext.getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, new String[]{"_id", "_data", "duration", "_size", "mime_type"}, "is_music != 0", null, "date_added DESC");
                while (query.moveToNext()) {
                    try {
                        File file = new File(query.getString(1));
                        long j = query.getLong(2);
                        long j2 = query.getLong(3);
                        String string = query.getString(4);
                        if (j <= MessagesController.getInstance(UserConfig.selectedAccount).ringtoneDurationMax * 1000 && j2 <= MessagesController.getInstance(UserConfig.selectedAccount).ringtoneSizeMax && (TextUtils.isEmpty(string) || "audio/mpeg".equals(string) || !"audio/mpeg4".equals(string))) {
                            ListItem listItem = new ListItem(null);
                            listItem.title = file.getName();
                            listItem.file = file;
                            String name = file.getName();
                            String[] split = name.split("\\.");
                            listItem.ext = split.length > 1 ? split[split.length - 1] : "?";
                            listItem.subtitle = AndroidUtilities.formatFileSize(file.length());
                            String lowerCase = name.toLowerCase();
                            if (lowerCase.endsWith(".jpg") || lowerCase.endsWith(".png") || lowerCase.endsWith(".gif") || lowerCase.endsWith(".jpeg")) {
                                listItem.thumb = file.getAbsolutePath();
                            }
                            this.listAdapter.recentItems.add(listItem);
                        }
                    } finally {
                    }
                }
                query.close();
            } catch (Exception e) {
                FileLog.e(e);
            }
        } catch (Exception e2) {
            FileLog.e(e2);
        }
    }

    @Override
    public int needsActionBar() {
        return 1;
    }

    @Override
    public boolean onBackPressed() {
        if (canClosePicker()) {
            return super.onBackPressed();
        }
        return true;
    }

    @Override
    public void onDestroy() {
        try {
            if (this.receiverRegistered) {
                ApplicationLoader.applicationContext.unregisterReceiver(this.receiver);
                this.receiverRegistered = false;
            }
        } catch (Exception e) {
            FileLog.e(e);
        }
        this.parentAlert.actionBar.closeSearchField();
        ActionBarMenu createMenu = this.parentAlert.actionBar.createMenu();
        createMenu.removeView(this.sortItem);
        createMenu.removeView(this.searchItem);
    }

    @Override
    public void onHide() {
        this.sortItem.setVisibility(8);
        this.searchItem.setVisibility(8);
    }

    @Override
    protected void onLayout(boolean z, int i, int i2, int i3, int i4) {
        super.onLayout(z, i, i2, i3, i4);
        updateEmptyViewPosition();
    }

    @Override
    public void onMenuItemClick(int i) {
        if (i == 6) {
            SharedConfig.toggleSortFilesByName();
            this.sortByName = SharedConfig.sortFilesByName;
            sortRecentItems();
            sortFileItems();
            this.listAdapter.notifyDataSetChanged();
            this.sortItem.setIcon(this.sortByName ? R.drawable.msg_contacts_time : R.drawable.msg_contacts_name);
        }
    }

    @Override
    public void onPreMeasure(int r4, int r5) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.ChatAttachAlertDocumentLayout.onPreMeasure(int, int):void");
    }

    @Override
    public void onResume() {
        super.onResume();
        ListAdapter listAdapter = this.listAdapter;
        if (listAdapter != null) {
            listAdapter.notifyDataSetChanged();
        }
        SearchAdapter searchAdapter = this.searchAdapter;
        if (searchAdapter != null) {
            searchAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onShow(ChatAttachAlert.AttachAlertLayout attachAlertLayout) {
        this.selectedFiles.clear();
        this.selectedMessages.clear();
        this.searchAdapter.currentSearchFilters.clear();
        this.selectedFilesOrder.clear();
        this.listAdapter.history.clear();
        listRoots();
        updateSearchButton();
        updateEmptyView();
        this.parentAlert.actionBar.setTitle(LocaleController.getString(R.string.SelectFile));
        this.sortItem.setVisibility(0);
        this.layoutManager.scrollToPositionWithOffset(0, 0);
    }

    @Override
    public void requestLayout() {
        if (this.ignoreLayout) {
            return;
        }
        super.requestLayout();
    }

    @Override
    public void scrollToTop() {
        this.listView.smoothScrollToPosition(0);
    }

    @Override
    public void sendSelectedItems(boolean z, int i, long j, boolean z2) {
        if ((this.selectedFiles.size() == 0 && this.selectedMessages.size() == 0) || this.delegate == null || this.sendPressed) {
            return;
        }
        this.sendPressed = true;
        ArrayList arrayList = new ArrayList();
        Iterator it = this.selectedMessages.keySet().iterator();
        while (it.hasNext()) {
            arrayList.add((MessageObject) this.selectedMessages.get((FilteredSearchView.MessageHashId) it.next()));
        }
        this.delegate.didSelectFiles(new ArrayList(this.selectedFilesOrder), this.parentAlert.getCommentView().getText().toString(), arrayList, z, i, j, z2);
        this.parentAlert.dismiss(true);
    }

    public void setCanSelectOnlyImageFiles(boolean z) {
        this.canSelectOnlyImageFiles = z;
    }

    public void setDelegate(DocumentSelectActivityDelegate documentSelectActivityDelegate) {
        this.delegate = documentSelectActivityDelegate;
    }

    public void setMaxSelectedFiles(int i) {
        this.maxSelectedFiles = i;
    }

    @Override
    public void setTranslationY(float f) {
        super.setTranslationY(f);
        this.parentAlert.getSheetContainer().invalidate();
    }
}
