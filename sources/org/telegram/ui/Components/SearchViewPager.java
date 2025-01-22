package org.telegram.ui.Components;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.AnimatedVectorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.OvershootInterpolator;
import android.widget.FrameLayout;
import android.widget.TextView;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.vectordrawable.graphics.drawable.AnimatedVectorDrawableCompat$$ExternalSyntheticApiModelOutline0;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import org.telegram.messenger.AccountInstance;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.DialogObject;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.MessagesStorage;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.R;
import org.telegram.messenger.SendMessagesHelper;
import org.telegram.messenger.UserConfig;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.ActionBarMenu;
import org.telegram.ui.ActionBar.ActionBarMenuItem;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.BackDrawable;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.MenuDrawable;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ActionBar.ThemeDescription;
import org.telegram.ui.Adapters.DialogsSearchAdapter;
import org.telegram.ui.Adapters.FiltersView;
import org.telegram.ui.Cells.ContextLinkCell;
import org.telegram.ui.Cells.DialogCell;
import org.telegram.ui.Cells.HashtagSearchCell;
import org.telegram.ui.Cells.ProfileSearchCell;
import org.telegram.ui.Cells.SharedAudioCell;
import org.telegram.ui.Cells.SharedDocumentCell;
import org.telegram.ui.Cells.SharedLinkCell;
import org.telegram.ui.Cells.SharedPhotoVideoCell;
import org.telegram.ui.ChatActivity;
import org.telegram.ui.Components.Premium.PremiumFeatureBottomSheet;
import org.telegram.ui.Components.ViewPagerFixed;
import org.telegram.ui.Components.spoilers.SpoilersTextView;
import org.telegram.ui.DialogsActivity;
import org.telegram.ui.FilteredSearchView;
import org.telegram.ui.TopicsFragment;

public abstract class SearchViewPager extends ViewPagerFixed implements FilteredSearchView.UiCallback, NotificationCenter.NotificationCenterDelegate {
    private ActionBarMenu actionMode;
    int animateFromCount;
    private boolean attached;
    public StickerEmptyView botsEmptyView;
    private DefaultItemAnimator botsItemAnimator;
    public DialogsBotsAdapter botsSearchAdapter;
    public FrameLayout botsSearchContainer;
    private LinearLayoutManager botsSearchLayoutManager;
    public RecyclerListView botsSearchListView;
    public StickerEmptyView channelsEmptyView;
    private DefaultItemAnimator channelsItemAnimator;
    public DialogsChannelsAdapter channelsSearchAdapter;
    public FrameLayout channelsSearchContainer;
    private LinearLayoutManager channelsSearchLayoutManager;
    public RecyclerListView channelsSearchListView;
    ChatPreviewDelegate chatPreviewDelegate;
    int currentAccount;
    private ArrayList currentSearchFilters;
    private ActionBarMenuItem deleteItem;
    public DialogsSearchAdapter dialogsSearchAdapter;
    private SearchDownloadsContainer downloadsContainer;
    public StickerEmptyView emptyView;
    public boolean expandedPublicPosts;
    private FilteredSearchView.Delegate filteredSearchViewDelegate;
    private final int folderId;
    private ActionBarMenuItem forwardItem;
    SizeNotifierFrameLayout fragmentView;
    private ActionBarMenuItem gotoItem;
    public StickerEmptyView hashtagEmptyView;
    private DefaultItemAnimator hashtagItemAnimator;
    public HashtagsSearchAdapter hashtagSearchAdapter;
    public FrameLayout hashtagSearchContainer;
    private LinearLayoutManager hashtagSearchLayoutManager;
    public RecyclerListView hashtagSearchListView;
    private boolean isActionModeShowed;
    private DefaultItemAnimator itemAnimator;
    private RecyclerItemsEnterAnimator itemsEnterAnimator;
    private int keyboardSize;
    private boolean lastSearchScrolledToTop;
    String lastSearchString;
    private FilteredSearchView noMediaFiltersSearchView;
    BaseFragment parent;
    public FrameLayout searchContainer;
    private LinearLayoutManager searchLayoutManager;
    public RecyclerListView searchListView;
    private HashMap selectedFiles;
    private NumberTextView selectedMessagesCountTextView;
    private boolean showOnlyDialogsAdapter;
    private ActionBarMenuItem speedItem;
    protected final ViewPagerAdapter viewPagerAdapter;

    public interface ChatPreviewDelegate {
        void finish();

        void move(float f);

        void startChatPreview(RecyclerListView recyclerListView, DialogCell dialogCell);
    }

    public class ViewPagerAdapter extends ViewPagerFixed.Adapter {
        ArrayList items = new ArrayList();

        public class Item {
            int filterIndex;
            private final int type;

            private Item(int i) {
                this.type = i;
            }
        }

        public ViewPagerAdapter() {
            updateItems();
        }

        @Override
        public void bindView(View view, int i, int i2) {
            SearchViewPager searchViewPager = SearchViewPager.this;
            searchViewPager.search(view, i, searchViewPager.lastSearchString, true);
        }

        @Override
        public View createView(int i) {
            if (i == 1) {
                return SearchViewPager.this.searchContainer;
            }
            if (i == 3) {
                return SearchViewPager.this.channelsSearchContainer;
            }
            if (i == 4) {
                return SearchViewPager.this.botsSearchContainer;
            }
            if (i == 5) {
                return SearchViewPager.this.hashtagSearchContainer;
            }
            if (i != 2) {
                FilteredSearchView filteredSearchView = new FilteredSearchView(SearchViewPager.this.parent);
                filteredSearchView.setChatPreviewDelegate(SearchViewPager.this.chatPreviewDelegate);
                filteredSearchView.setUiCallback(SearchViewPager.this);
                filteredSearchView.recyclerListView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                    @Override
                    public void onScrolled(RecyclerView recyclerView, int i2, int i3) {
                        super.onScrolled(recyclerView, i2, i3);
                        SearchViewPager.this.fragmentView.invalidateBlur();
                    }
                });
                return filteredSearchView;
            }
            SearchViewPager searchViewPager = SearchViewPager.this;
            SearchViewPager searchViewPager2 = SearchViewPager.this;
            searchViewPager.downloadsContainer = new SearchDownloadsContainer(searchViewPager2.parent, searchViewPager2.currentAccount);
            SearchViewPager.this.downloadsContainer.recyclerListView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrolled(RecyclerView recyclerView, int i2, int i3) {
                    super.onScrolled(recyclerView, i2, i3);
                    SearchViewPager.this.fragmentView.invalidateBlur();
                }
            });
            SearchViewPager.this.downloadsContainer.setUiCallback(SearchViewPager.this);
            return SearchViewPager.this.downloadsContainer;
        }

        @Override
        public int getItemCount() {
            return this.items.size();
        }

        @Override
        public String getItemTitle(int i) {
            return ((Item) this.items.get(i)).type == 0 ? LocaleController.getString(R.string.SearchAllChatsShort) : ((Item) this.items.get(i)).type == 1 ? LocaleController.getString(R.string.ChannelsTab) : ((Item) this.items.get(i)).type == 4 ? LocaleController.getString(R.string.AppsTab) : ((Item) this.items.get(i)).type == 2 ? LocaleController.getString(R.string.DownloadsTabs) : ((Item) this.items.get(i)).type == 5 ? LocaleController.getString(R.string.PublicPostsTabs) : FiltersView.filters[((Item) this.items.get(i)).filterIndex].getTitle();
        }

        @Override
        public int getItemViewType(int i) {
            if (((Item) this.items.get(i)).type == 0) {
                return 1;
            }
            if (((Item) this.items.get(i)).type == 1) {
                return 3;
            }
            if (((Item) this.items.get(i)).type == 4) {
                return 4;
            }
            if (((Item) this.items.get(i)).type == 2) {
                return 2;
            }
            if (((Item) this.items.get(i)).type == 5) {
                return 5;
            }
            return ((Item) this.items.get(i)).type + i;
        }

        public void updateItems() {
            this.items.clear();
            this.items.add(new Item(0));
            if (SearchViewPager.this.expandedPublicPosts) {
                this.items.add(new Item(5));
            }
            this.items.add(new Item(1));
            this.items.add(new Item(4));
            if (SearchViewPager.this.showOnlyDialogsAdapter) {
                return;
            }
            int i = 3;
            Item item = new Item(i);
            item.filterIndex = 0;
            this.items.add(item);
            int i2 = 2;
            if (SearchViewPager.this.includeDownloads()) {
                this.items.add(new Item(i2));
            }
            Item item2 = new Item(i);
            item2.filterIndex = 1;
            this.items.add(item2);
            Item item3 = new Item(i);
            item3.filterIndex = 2;
            this.items.add(item3);
            Item item4 = new Item(i);
            item4.filterIndex = 3;
            this.items.add(item4);
            Item item5 = new Item(i);
            item5.filterIndex = 4;
            this.items.add(item5);
        }
    }

    public SearchViewPager(Context context, final DialogsActivity dialogsActivity, int i, int i2, int i3, ChatPreviewDelegate chatPreviewDelegate) {
        super(context);
        this.expandedPublicPosts = false;
        this.selectedFiles = new HashMap();
        this.currentSearchFilters = new ArrayList();
        this.currentAccount = UserConfig.selectedAccount;
        this.animateFromCount = 0;
        this.folderId = i3;
        this.parent = dialogsActivity;
        this.chatPreviewDelegate = chatPreviewDelegate;
        DefaultItemAnimator defaultItemAnimator = new DefaultItemAnimator();
        this.itemAnimator = defaultItemAnimator;
        defaultItemAnimator.setAddDuration(150L);
        this.itemAnimator.setMoveDuration(350L);
        this.itemAnimator.setChangeDuration(0L);
        this.itemAnimator.setRemoveDuration(0L);
        this.itemAnimator.setMoveInterpolator(new OvershootInterpolator(1.1f));
        this.itemAnimator.setTranslationInterpolator(CubicBezierInterpolator.EASE_OUT_QUINT);
        this.dialogsSearchAdapter = new DialogsSearchAdapter(context, dialogsActivity, i, i2, this.itemAnimator, dialogsActivity.getAllowGlobalSearch(), null) {
            @Override
            public void notifyDataSetChanged() {
                RecyclerListView recyclerListView;
                int currentItemCount = getCurrentItemCount();
                super.notifyDataSetChanged();
                if (!SearchViewPager.this.lastSearchScrolledToTop && (recyclerListView = SearchViewPager.this.searchListView) != null) {
                    recyclerListView.scrollToPosition(0);
                    SearchViewPager.this.lastSearchScrolledToTop = true;
                }
                if (getItemCount() != 0 || currentItemCount == 0 || isSearching()) {
                    return;
                }
                SearchViewPager.this.emptyView.showProgress(false, false);
            }

            @Override
            protected void openBotApp(TLRPC.User user) {
                if (user == null) {
                    return;
                }
                BaseFragment baseFragment = SearchViewPager.this.parent;
                if (baseFragment instanceof DialogsActivity) {
                    ((DialogsActivity) baseFragment).closeSearching();
                }
                MessagesController.getInstance(SearchViewPager.this.currentAccount).openApp(user, 0);
                putRecentSearch(user.id, user);
            }

            @Override
            protected void openPublicPosts() {
                SearchViewPager searchViewPager = SearchViewPager.this;
                HashtagsSearchAdapter hashtagsSearchAdapter = searchViewPager.hashtagSearchAdapter;
                DialogsSearchAdapter dialogsSearchAdapter = searchViewPager.dialogsSearchAdapter;
                hashtagsSearchAdapter.setInitialData(dialogsSearchAdapter.publicPostsHashtag, dialogsSearchAdapter.publicPosts, dialogsSearchAdapter.publicPostsLastRate, dialogsSearchAdapter.publicPostsTotalCount);
                SearchViewPager searchViewPager2 = SearchViewPager.this;
                searchViewPager2.expandedPublicPosts = true;
                searchViewPager2.hashtagSearchLayoutManager.scrollToPositionWithOffset(0, 0);
                SearchViewPager.this.updateTabs();
                ViewPagerFixed.TabsView tabsView = SearchViewPager.this.tabsView;
                if (tabsView != null && tabsView.getCurrentTabId() != 1) {
                    SearchViewPager.this.tabsView.scrollToTab(1, 1);
                }
                SearchViewPager searchViewPager3 = SearchViewPager.this;
                searchViewPager3.hashtagSearchAdapter.search(searchViewPager3.lastSearchString);
            }
        };
        int i4 = 1;
        if (i2 == 15) {
            ArrayList dialogsArray = dialogsActivity.getDialogsArray(this.currentAccount, i2, i3, true);
            ArrayList arrayList = new ArrayList();
            for (int i5 = 0; i5 < dialogsArray.size(); i5++) {
                arrayList.add(Long.valueOf(((TLRPC.Dialog) dialogsArray.get(i5)).id));
            }
            this.dialogsSearchAdapter.setFilterDialogIds(arrayList);
        }
        this.fragmentView = (SizeNotifierFrameLayout) dialogsActivity.getFragmentView();
        BlurredRecyclerView blurredRecyclerView = new BlurredRecyclerView(context) {
            @Override
            public void dispatchDraw(Canvas canvas) {
                SearchViewPager searchViewPager = SearchViewPager.this;
                if (searchViewPager.dialogsSearchAdapter != null && searchViewPager.itemAnimator != null && SearchViewPager.this.searchLayoutManager != null && SearchViewPager.this.dialogsSearchAdapter.showMoreAnimation) {
                    canvas.save();
                    invalidate();
                    int itemCount = SearchViewPager.this.dialogsSearchAdapter.getItemCount() - 1;
                    int i6 = 0;
                    while (true) {
                        if (i6 >= getChildCount()) {
                            break;
                        }
                        View childAt = getChildAt(i6);
                        if (getChildAdapterPosition(childAt) == itemCount) {
                            canvas.clipRect(0.0f, 0.0f, getWidth(), childAt.getBottom() + childAt.getTranslationY());
                            break;
                        }
                        i6++;
                    }
                }
                super.dispatchDraw(canvas);
                SearchViewPager searchViewPager2 = SearchViewPager.this;
                if (searchViewPager2.dialogsSearchAdapter != null && searchViewPager2.itemAnimator != null && SearchViewPager.this.searchLayoutManager != null && SearchViewPager.this.dialogsSearchAdapter.showMoreAnimation) {
                    canvas.restore();
                }
                DialogsSearchAdapter dialogsSearchAdapter = SearchViewPager.this.dialogsSearchAdapter;
                if (dialogsSearchAdapter == null || dialogsSearchAdapter.showMoreHeader == null) {
                    return;
                }
                canvas.save();
                canvas.translate(SearchViewPager.this.dialogsSearchAdapter.showMoreHeader.getLeft(), SearchViewPager.this.dialogsSearchAdapter.showMoreHeader.getTop() + SearchViewPager.this.dialogsSearchAdapter.showMoreHeader.getTranslationY());
                SearchViewPager.this.dialogsSearchAdapter.showMoreHeader.draw(canvas);
                canvas.restore();
            }
        };
        this.searchListView = blurredRecyclerView;
        blurredRecyclerView.setItemAnimator(this.itemAnimator);
        this.searchListView.setPivotY(0.0f);
        this.searchListView.setAdapter(this.dialogsSearchAdapter);
        this.searchListView.setVerticalScrollBarEnabled(true);
        this.searchListView.setInstantClick(true);
        this.searchListView.setVerticalScrollbarPosition(LocaleController.isRTL ? 1 : 2);
        RecyclerListView recyclerListView = this.searchListView;
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context, 1, false);
        this.searchLayoutManager = linearLayoutManager;
        recyclerListView.setLayoutManager(linearLayoutManager);
        this.searchListView.setAnimateEmptyView(true, 0);
        this.searchListView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int i6) {
                if (i6 == 1) {
                    AndroidUtilities.hideKeyboard(dialogsActivity.getParentActivity().getCurrentFocus());
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int i6, int i7) {
                DialogsSearchAdapter.DialogsSearchAdapterDelegate dialogsSearchAdapterDelegate;
                int i8;
                int findFirstVisibleItemPosition = SearchViewPager.this.searchLayoutManager.findFirstVisibleItemPosition();
                int findLastVisibleItemPosition = SearchViewPager.this.searchLayoutManager.findLastVisibleItemPosition();
                int abs = Math.abs(SearchViewPager.this.searchLayoutManager.findLastVisibleItemPosition() - findFirstVisibleItemPosition) + 1;
                int itemCount = recyclerView.getAdapter().getItemCount();
                if (abs > 0 && !SearchViewPager.this.dialogsSearchAdapter.isMessagesSearchEndReached() && (findLastVisibleItemPosition == itemCount - 1 || ((dialogsSearchAdapterDelegate = SearchViewPager.this.dialogsSearchAdapter.delegate) != null && dialogsSearchAdapterDelegate.getSearchForumDialogId() != 0 && (i8 = SearchViewPager.this.dialogsSearchAdapter.localMessagesLoadingRow) >= 0 && findFirstVisibleItemPosition <= i8 && findLastVisibleItemPosition >= i8))) {
                    SearchViewPager.this.dialogsSearchAdapter.loadMoreSearchMessages();
                }
                SearchViewPager.this.fragmentView.invalidateBlur();
            }
        });
        FilteredSearchView filteredSearchView = new FilteredSearchView(this.parent);
        this.noMediaFiltersSearchView = filteredSearchView;
        filteredSearchView.setUiCallback(this);
        this.noMediaFiltersSearchView.setVisibility(8);
        this.noMediaFiltersSearchView.setChatPreviewDelegate(chatPreviewDelegate);
        FlickerLoadingView flickerLoadingView = new FlickerLoadingView(context);
        flickerLoadingView.setViewType(1);
        StickerEmptyView stickerEmptyView = new StickerEmptyView(context, flickerLoadingView, i4) {
            @Override
            public void setVisibility(int i6) {
                if (SearchViewPager.this.noMediaFiltersSearchView.getTag() != null) {
                    super.setVisibility(8);
                } else {
                    super.setVisibility(i6);
                }
            }
        };
        this.emptyView = stickerEmptyView;
        SpoilersTextView spoilersTextView = stickerEmptyView.title;
        int i6 = R.string.NoResult;
        spoilersTextView.setText(LocaleController.getString(i6));
        this.emptyView.subtitle.setVisibility(8);
        this.emptyView.setVisibility(8);
        this.emptyView.addView(flickerLoadingView, 0);
        this.emptyView.showProgress(true, false);
        FrameLayout frameLayout = new FrameLayout(context);
        this.searchContainer = frameLayout;
        frameLayout.addView(this.emptyView);
        this.searchContainer.addView(this.searchListView);
        this.searchContainer.addView(this.noMediaFiltersSearchView);
        this.searchListView.setEmptyView(this.emptyView);
        this.searchListView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int i7, int i8) {
                super.onScrolled(recyclerView, i7, i8);
                SearchViewPager.this.fragmentView.invalidateBlur();
            }
        });
        this.channelsSearchContainer = new FrameLayout(context);
        DefaultItemAnimator defaultItemAnimator2 = new DefaultItemAnimator() {
            @Override
            public void onMoveAnimationUpdate(RecyclerView.ViewHolder viewHolder) {
                super.onMoveAnimationUpdate(viewHolder);
                SearchViewPager.this.invalidate();
            }
        };
        this.channelsItemAnimator = defaultItemAnimator2;
        defaultItemAnimator2.setSupportsChangeAnimations(false);
        this.channelsItemAnimator.setDelayAnimations(false);
        DefaultItemAnimator defaultItemAnimator3 = this.channelsItemAnimator;
        CubicBezierInterpolator cubicBezierInterpolator = CubicBezierInterpolator.EASE_OUT_QUINT;
        defaultItemAnimator3.setInterpolator(cubicBezierInterpolator);
        this.channelsItemAnimator.setDurations(350L);
        BlurredRecyclerView blurredRecyclerView2 = new BlurredRecyclerView(context);
        this.channelsSearchListView = blurredRecyclerView2;
        blurredRecyclerView2.setItemAnimator(this.channelsItemAnimator);
        this.channelsSearchListView.setPivotY(0.0f);
        this.channelsSearchListView.setVerticalScrollBarEnabled(true);
        this.channelsSearchListView.setInstantClick(true);
        this.channelsSearchListView.setVerticalScrollbarPosition(LocaleController.isRTL ? 1 : 2);
        RecyclerListView recyclerListView2 = this.channelsSearchListView;
        LinearLayoutManager linearLayoutManager2 = new LinearLayoutManager(context, 1, false);
        this.channelsSearchLayoutManager = linearLayoutManager2;
        recyclerListView2.setLayoutManager(linearLayoutManager2);
        this.channelsSearchListView.setAnimateEmptyView(true, 0);
        FlickerLoadingView flickerLoadingView2 = new FlickerLoadingView(context);
        flickerLoadingView2.setViewType(1);
        StickerEmptyView stickerEmptyView2 = new StickerEmptyView(context, flickerLoadingView2, i4) {
            @Override
            public void setVisibility(int i7) {
                if (SearchViewPager.this.noMediaFiltersSearchView.getTag() != null) {
                    super.setVisibility(8);
                } else {
                    super.setVisibility(i7);
                }
            }
        };
        this.channelsEmptyView = stickerEmptyView2;
        stickerEmptyView2.title.setText(LocaleController.getString(i6));
        this.channelsEmptyView.subtitle.setVisibility(8);
        this.channelsEmptyView.setVisibility(8);
        this.channelsEmptyView.addView(flickerLoadingView2, 0);
        this.channelsEmptyView.showProgress(true, false);
        this.channelsSearchContainer.addView(this.channelsEmptyView);
        this.channelsSearchContainer.addView(this.channelsSearchListView);
        this.channelsSearchListView.setEmptyView(this.channelsEmptyView);
        RecyclerListView recyclerListView3 = this.channelsSearchListView;
        DialogsChannelsAdapter dialogsChannelsAdapter = new DialogsChannelsAdapter(recyclerListView3, context, this.currentAccount, i3, null) {
            @Override
            protected void hideKeyboard() {
                AndroidUtilities.hideKeyboard(dialogsActivity.getParentActivity().getCurrentFocus());
            }

            @Override
            public void update(boolean z) {
                ArrayList arrayList2;
                ArrayList arrayList3;
                ArrayList arrayList4;
                ArrayList arrayList5;
                super.update(z);
                SearchViewPager.this.channelsEmptyView.showProgress(this.loadingMessages || this.loadingChannels || (arrayList2 = this.messages) == null || !arrayList2.isEmpty() || (arrayList3 = this.searchMyChannels) == null || !arrayList3.isEmpty() || (arrayList4 = this.searchChannels) == null || !arrayList4.isEmpty() || (arrayList5 = this.searchRecommendedChannels) == null || !arrayList5.isEmpty(), z);
                if (!TextUtils.isEmpty(this.query)) {
                    SearchViewPager.this.channelsEmptyView.title.setText(LocaleController.getString(R.string.NoResult));
                    SearchViewPager.this.channelsEmptyView.subtitle.setVisibility(8);
                } else {
                    SearchViewPager.this.channelsEmptyView.title.setText(LocaleController.getString(R.string.NoChannelsTitle));
                    SearchViewPager.this.channelsEmptyView.subtitle.setVisibility(0);
                    SearchViewPager.this.channelsEmptyView.subtitle.setText(LocaleController.getString(R.string.NoChannelsMessage));
                }
            }
        };
        this.channelsSearchAdapter = dialogsChannelsAdapter;
        recyclerListView3.setAdapter(dialogsChannelsAdapter);
        this.channelsSearchListView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int i7) {
                if (i7 == 1) {
                    AndroidUtilities.hideKeyboard(dialogsActivity.getParentActivity().getCurrentFocus());
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int i7, int i8) {
                SearchViewPager.this.channelsSearchAdapter.checkBottom();
            }
        });
        this.botsSearchContainer = new FrameLayout(context);
        DefaultItemAnimator defaultItemAnimator4 = new DefaultItemAnimator() {
            @Override
            public void onMoveAnimationUpdate(RecyclerView.ViewHolder viewHolder) {
                super.onMoveAnimationUpdate(viewHolder);
                SearchViewPager.this.invalidate();
            }
        };
        this.botsItemAnimator = defaultItemAnimator4;
        defaultItemAnimator4.setSupportsChangeAnimations(false);
        this.botsItemAnimator.setDelayAnimations(false);
        this.botsItemAnimator.setInterpolator(cubicBezierInterpolator);
        this.botsItemAnimator.setDurations(350L);
        BlurredRecyclerView blurredRecyclerView3 = new BlurredRecyclerView(context);
        this.botsSearchListView = blurredRecyclerView3;
        blurredRecyclerView3.setItemAnimator(this.botsItemAnimator);
        this.botsSearchListView.setPivotY(0.0f);
        int i7 = 1;
        this.botsSearchListView.setVerticalScrollBarEnabled(true);
        this.botsSearchListView.setInstantClick(true);
        this.botsSearchListView.setVerticalScrollbarPosition(LocaleController.isRTL ? 1 : 2);
        RecyclerListView recyclerListView4 = this.botsSearchListView;
        LinearLayoutManager linearLayoutManager3 = new LinearLayoutManager(context, 1, false);
        this.botsSearchLayoutManager = linearLayoutManager3;
        recyclerListView4.setLayoutManager(linearLayoutManager3);
        this.botsSearchListView.setAnimateEmptyView(true, 0);
        FlickerLoadingView flickerLoadingView3 = new FlickerLoadingView(context);
        flickerLoadingView3.setViewType(1);
        StickerEmptyView stickerEmptyView3 = new StickerEmptyView(context, flickerLoadingView3, i7) {
            @Override
            public void setVisibility(int i8) {
                if (SearchViewPager.this.noMediaFiltersSearchView.getTag() != null) {
                    super.setVisibility(8);
                } else {
                    super.setVisibility(i8);
                }
            }
        };
        this.botsEmptyView = stickerEmptyView3;
        stickerEmptyView3.title.setText(LocaleController.getString(i6));
        this.botsEmptyView.subtitle.setVisibility(8);
        this.botsEmptyView.setVisibility(8);
        this.botsEmptyView.addView(flickerLoadingView3, 0);
        this.botsEmptyView.showProgress(true, false);
        this.botsSearchContainer.addView(this.botsEmptyView);
        this.botsSearchContainer.addView(this.botsSearchListView);
        this.botsSearchListView.setEmptyView(this.botsEmptyView);
        RecyclerListView recyclerListView5 = this.botsSearchListView;
        DialogsBotsAdapter dialogsBotsAdapter = new DialogsBotsAdapter(recyclerListView5, context, this.currentAccount, i3, false, null) {
            @Override
            public void update(boolean z) {
                ArrayList arrayList2;
                super.update(z);
                SearchViewPager.this.botsEmptyView.showProgress(this.loadingMessages || this.loadingBots || (arrayList2 = this.searchMessages) == null || !arrayList2.isEmpty(), z);
                SearchViewPager.this.botsEmptyView.title.setText(LocaleController.getString(R.string.NoResult));
                SearchViewPager.this.botsEmptyView.subtitle.setVisibility(8);
            }
        };
        this.botsSearchAdapter = dialogsBotsAdapter;
        recyclerListView5.setAdapter(dialogsBotsAdapter);
        this.botsSearchListView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int i8) {
                if (i8 == 1) {
                    AndroidUtilities.hideKeyboard(dialogsActivity.getParentActivity().getCurrentFocus());
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int i8, int i9) {
                SearchViewPager.this.botsSearchAdapter.checkBottom();
            }
        });
        this.hashtagSearchContainer = new FrameLayout(context);
        DefaultItemAnimator defaultItemAnimator5 = new DefaultItemAnimator() {
            @Override
            public void onMoveAnimationUpdate(RecyclerView.ViewHolder viewHolder) {
                super.onMoveAnimationUpdate(viewHolder);
                SearchViewPager.this.invalidate();
            }
        };
        this.hashtagItemAnimator = defaultItemAnimator5;
        defaultItemAnimator5.setSupportsChangeAnimations(false);
        this.hashtagItemAnimator.setDelayAnimations(false);
        this.hashtagItemAnimator.setInterpolator(cubicBezierInterpolator);
        this.hashtagItemAnimator.setDurations(350L);
        BlurredRecyclerView blurredRecyclerView4 = new BlurredRecyclerView(context);
        this.hashtagSearchListView = blurredRecyclerView4;
        blurredRecyclerView4.setItemAnimator(this.hashtagItemAnimator);
        this.hashtagSearchListView.setPivotY(0.0f);
        int i8 = 1;
        this.hashtagSearchListView.setVerticalScrollBarEnabled(true);
        this.hashtagSearchListView.setInstantClick(true);
        this.hashtagSearchListView.setVerticalScrollbarPosition(LocaleController.isRTL ? 1 : 2);
        RecyclerListView recyclerListView6 = this.hashtagSearchListView;
        LinearLayoutManager linearLayoutManager4 = new LinearLayoutManager(context, 1, false);
        this.hashtagSearchLayoutManager = linearLayoutManager4;
        recyclerListView6.setLayoutManager(linearLayoutManager4);
        this.hashtagSearchListView.setAnimateEmptyView(true, 0);
        FlickerLoadingView flickerLoadingView4 = new FlickerLoadingView(context);
        flickerLoadingView4.setViewType(1);
        StickerEmptyView stickerEmptyView4 = new StickerEmptyView(context, flickerLoadingView4, i8) {
            @Override
            public void setVisibility(int i9) {
                if (SearchViewPager.this.noMediaFiltersSearchView.getTag() != null) {
                    super.setVisibility(8);
                } else {
                    super.setVisibility(i9);
                }
            }
        };
        this.hashtagEmptyView = stickerEmptyView4;
        stickerEmptyView4.title.setText(LocaleController.getString(i6));
        this.hashtagEmptyView.subtitle.setVisibility(8);
        this.hashtagEmptyView.setVisibility(8);
        this.hashtagEmptyView.addView(flickerLoadingView4, 0);
        this.hashtagEmptyView.showProgress(true, false);
        this.hashtagSearchContainer.addView(this.hashtagEmptyView);
        this.hashtagSearchContainer.addView(this.hashtagSearchListView);
        this.hashtagSearchListView.setEmptyView(this.hashtagEmptyView);
        RecyclerListView recyclerListView7 = this.hashtagSearchListView;
        HashtagsSearchAdapter hashtagsSearchAdapter = new HashtagsSearchAdapter(recyclerListView7, context, this.currentAccount, i3, null) {
            @Override
            protected void scrollToTop(boolean z) {
                if (z && SearchViewPager.this.hashtagSearchListView.canScrollVertically(-1)) {
                    return;
                }
                SearchViewPager.this.hashtagSearchLayoutManager.scrollToPositionWithOffset(0, 0);
            }

            @Override
            public void update(boolean z) {
                super.update(z);
                SearchViewPager.this.hashtagEmptyView.showProgress(false, z);
                SearchViewPager.this.hashtagEmptyView.title.setText(LocaleController.getString(R.string.NoResult));
                SearchViewPager.this.hashtagEmptyView.subtitle.setVisibility(8);
            }
        };
        this.hashtagSearchAdapter = hashtagsSearchAdapter;
        recyclerListView7.setAdapter(hashtagsSearchAdapter);
        this.hashtagSearchListView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int i9) {
                if (i9 == 1) {
                    AndroidUtilities.hideKeyboard(dialogsActivity.getParentActivity().getCurrentFocus());
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int i9, int i10) {
                SearchViewPager.this.hashtagSearchAdapter.checkBottom();
            }
        });
        this.itemsEnterAnimator = new RecyclerItemsEnterAnimator(this.searchListView, true);
        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter();
        this.viewPagerAdapter = viewPagerAdapter;
        setAdapter(viewPagerAdapter);
    }

    private boolean isSpeedItemVisible() {
        if (!UserConfig.getInstance(this.currentAccount).isPremium() && !MessagesController.getInstance(this.currentAccount).premiumFeaturesBlocked()) {
            for (MessageObject messageObject : this.selectedFiles.values()) {
                if (messageObject.getDocument() != null && messageObject.getDocument().size >= 157286400) {
                    return true;
                }
            }
        }
        return false;
    }

    public void lambda$getThemeDescriptions$4() {
        NumberTextView numberTextView = this.selectedMessagesCountTextView;
        if (numberTextView != null) {
            numberTextView.setTextColor(Theme.getColor(Theme.key_actionBarActionModeDefaultIcon));
        }
    }

    public void lambda$onActionBarItemClick$2(ArrayList arrayList, AlertDialog alertDialog, int i) {
        alertDialog.dismiss();
        this.parent.getDownloadController().deleteRecentFiles(arrayList);
        hideActionMode();
    }

    public boolean lambda$onActionBarItemClick$3(DialogsActivity dialogsActivity, ArrayList arrayList, CharSequence charSequence, boolean z, boolean z2, int i, TopicsFragment topicsFragment) {
        String str;
        ArrayList<MessageObject> arrayList2 = new ArrayList<>();
        Iterator it = this.selectedFiles.keySet().iterator();
        while (it.hasNext()) {
            arrayList2.add((MessageObject) this.selectedFiles.get((FilteredSearchView.MessageHashId) it.next()));
        }
        this.selectedFiles.clear();
        showActionMode(false);
        if (arrayList.size() > 1 || ((MessagesStorage.TopicKey) arrayList.get(0)).dialogId == AccountInstance.getInstance(this.currentAccount).getUserConfig().getClientUserId() || charSequence != null) {
            for (int i2 = 0; i2 < arrayList.size(); i2++) {
                long j = ((MessagesStorage.TopicKey) arrayList.get(i2)).dialogId;
                if (charSequence != null) {
                    AccountInstance.getInstance(this.currentAccount).getSendMessagesHelper().sendMessage(SendMessagesHelper.SendMessageParams.of(charSequence.toString(), j, null, null, null, true, null, null, null, true, 0, null, false));
                }
                AccountInstance.getInstance(this.currentAccount).getSendMessagesHelper().sendMessage(arrayList2, j, false, false, true, 0);
            }
            dialogsActivity.lambda$onBackPressed$323();
        } else {
            long j2 = ((MessagesStorage.TopicKey) arrayList.get(0)).dialogId;
            Bundle bundle = new Bundle();
            bundle.putBoolean("scrollToTopOnResume", true);
            if (DialogObject.isEncryptedDialog(j2)) {
                bundle.putInt("enc_id", DialogObject.getEncryptedChatId(j2));
            } else {
                if (DialogObject.isUserDialog(j2)) {
                    str = "user_id";
                } else {
                    j2 = -j2;
                    str = "chat_id";
                }
                bundle.putLong(str, j2);
                if (!AccountInstance.getInstance(this.currentAccount).getMessagesController().checkCanOpenChat(bundle, dialogsActivity)) {
                    return true;
                }
            }
            ChatActivity chatActivity = new ChatActivity(bundle);
            dialogsActivity.presentFragment(chatActivity, true);
            chatActivity.showFieldPanelForForward(true, arrayList2);
        }
        return true;
    }

    public static boolean lambda$showActionMode$0(View view, MotionEvent motionEvent) {
        return true;
    }

    public void search(View view, int i, String str, boolean z) {
        boolean z2;
        boolean z3;
        if (TextUtils.isEmpty(str)) {
            this.emptyView.subtitle.setVisibility(8);
        } else {
            this.emptyView.subtitle.setVisibility(0);
            this.emptyView.subtitle.setText(LocaleController.formatString(R.string.NoResultFoundFor2, str));
        }
        DialogsSearchAdapter.DialogsSearchAdapterDelegate dialogsSearchAdapterDelegate = this.dialogsSearchAdapter.delegate;
        long searchForumDialogId = dialogsSearchAdapterDelegate != null ? dialogsSearchAdapterDelegate.getSearchForumDialogId() : 0L;
        long j = i == 0 ? 0L : searchForumDialogId;
        boolean z4 = false;
        long j2 = 0;
        long j3 = 0;
        for (int i2 = 0; i2 < this.currentSearchFilters.size(); i2++) {
            FiltersView.MediaFilterData mediaFilterData = (FiltersView.MediaFilterData) this.currentSearchFilters.get(i2);
            int i3 = mediaFilterData.filterType;
            if (i3 == 4) {
                TLObject tLObject = mediaFilterData.chat;
                if (tLObject instanceof TLRPC.User) {
                    j = ((TLRPC.User) tLObject).id;
                } else if (tLObject instanceof TLRPC.Chat) {
                    j = -((TLRPC.Chat) tLObject).id;
                }
            } else if (i3 == 6) {
                FiltersView.DateData dateData = mediaFilterData.dateData;
                long j4 = dateData.minDate;
                j3 = dateData.maxDate;
                j2 = j4;
            } else if (i3 == 7) {
                z4 = true;
            }
        }
        if (this.hashtagSearchAdapter.getHashtag(str) == null) {
            collapsePublicPosts();
        }
        if (view == this.channelsSearchContainer) {
            MessagesController.getInstance(this.currentAccount).getChannelRecommendations(0L);
            this.channelsSearchAdapter.search(str);
            this.channelsEmptyView.setKeyboardHeight(this.keyboardSize, false);
            return;
        }
        if (view == this.botsSearchContainer) {
            this.botsSearchAdapter.search(str);
            this.botsEmptyView.setKeyboardHeight(this.keyboardSize, false);
            if (TextUtils.isEmpty(str)) {
                this.botsSearchAdapter.checkBottom();
                return;
            }
            return;
        }
        if (view == this.hashtagSearchContainer) {
            if (this.hashtagSearchAdapter.getHashtag(str) == null) {
                return;
            }
            if (z) {
                z3 = false;
                this.hashtagSearchLayoutManager.scrollToPositionWithOffset(0, 0);
            } else {
                z3 = false;
            }
            this.hashtagSearchAdapter.search(str);
            this.hashtagEmptyView.setKeyboardHeight(this.keyboardSize, z3);
            return;
        }
        if (view != this.searchContainer) {
            if (view instanceof FilteredSearchView) {
                FilteredSearchView filteredSearchView = (FilteredSearchView) view;
                filteredSearchView.setUseFromUserAsAvatar(searchForumDialogId != 0);
                filteredSearchView.setKeyboardHeight(this.keyboardSize, false);
                filteredSearchView.search(j, j2, j3, FiltersView.filters[((ViewPagerAdapter.Item) this.viewPagerAdapter.items.get(i)).filterIndex], z4, str, z);
                return;
            }
            if (view instanceof SearchDownloadsContainer) {
                SearchDownloadsContainer searchDownloadsContainer = (SearchDownloadsContainer) view;
                searchDownloadsContainer.setKeyboardHeight(this.keyboardSize, false);
                searchDownloadsContainer.search(str);
                return;
            }
            return;
        }
        if (!(j == 0 && j2 == 0 && j3 == 0) && searchForumDialogId == 0) {
            this.noMediaFiltersSearchView.setTag(1);
            this.noMediaFiltersSearchView.setDelegate(this.filteredSearchViewDelegate, false);
            this.noMediaFiltersSearchView.animate().setListener(null).cancel();
            if (z) {
                this.noMediaFiltersSearchView.setVisibility(0);
                this.noMediaFiltersSearchView.setAlpha(1.0f);
                z2 = z;
            } else {
                if (this.noMediaFiltersSearchView.getVisibility() != 0) {
                    this.noMediaFiltersSearchView.setVisibility(0);
                    this.noMediaFiltersSearchView.setAlpha(0.0f);
                } else {
                    r3 = z;
                }
                this.noMediaFiltersSearchView.animate().alpha(1.0f).setDuration(150L).start();
                z2 = r3;
            }
            this.noMediaFiltersSearchView.search(j, j2, j3, null, z4, str, z2);
            this.emptyView.setVisibility(8);
        } else {
            this.lastSearchScrolledToTop = false;
            this.dialogsSearchAdapter.searchDialogs(str, z4 ? 1 : 0, true);
            this.dialogsSearchAdapter.setFiltersDelegate(this.filteredSearchViewDelegate, false);
            this.noMediaFiltersSearchView.animate().setListener(null).cancel();
            this.noMediaFiltersSearchView.setDelegate(null, false);
            if (z) {
                this.emptyView.showProgress(true ^ this.dialogsSearchAdapter.isSearching(), false);
                this.emptyView.showProgress(this.dialogsSearchAdapter.isSearching(), false);
            } else if (!this.dialogsSearchAdapter.hasRecentSearch()) {
                this.emptyView.showProgress(this.dialogsSearchAdapter.isSearching(), true);
            }
            if (z) {
                this.noMediaFiltersSearchView.setVisibility(8);
            } else if (this.noMediaFiltersSearchView.getVisibility() != 8) {
                this.noMediaFiltersSearchView.animate().alpha(0.0f).setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animator) {
                        SearchViewPager.this.noMediaFiltersSearchView.setVisibility(8);
                    }
                }).setDuration(150L).start();
            }
            this.noMediaFiltersSearchView.setTag(null);
        }
        this.emptyView.setKeyboardHeight(this.keyboardSize, false);
        this.noMediaFiltersSearchView.setKeyboardHeight(this.keyboardSize, false);
    }

    private void showActionMode(boolean z) {
        DialogsSearchAdapter.DialogsSearchAdapterDelegate dialogsSearchAdapterDelegate;
        if (this.isActionModeShowed == z) {
            return;
        }
        if (z && this.parent.getActionBar().isActionModeShowed()) {
            return;
        }
        if (z && !this.parent.getActionBar().actionModeIsExist("search_view_pager")) {
            ActionBarMenu createActionMode = this.parent.getActionBar().createActionMode(true, "search_view_pager");
            this.actionMode = createActionMode;
            NumberTextView numberTextView = new NumberTextView(createActionMode.getContext());
            this.selectedMessagesCountTextView = numberTextView;
            numberTextView.setTextSize(18);
            this.selectedMessagesCountTextView.setTypeface(AndroidUtilities.bold());
            NumberTextView numberTextView2 = this.selectedMessagesCountTextView;
            int i = Theme.key_actionBarActionModeDefaultIcon;
            numberTextView2.setTextColor(Theme.getColor(i));
            this.actionMode.addView(this.selectedMessagesCountTextView, LayoutHelper.createLinear(0, -1, 1.0f, 72, 0, 0, 0));
            this.selectedMessagesCountTextView.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public final boolean onTouch(View view, MotionEvent motionEvent) {
                    boolean lambda$showActionMode$0;
                    lambda$showActionMode$0 = SearchViewPager.lambda$showActionMode$0(view, motionEvent);
                    return lambda$showActionMode$0;
                }
            });
            ActionBarMenuItem addItemWithWidth = this.actionMode.addItemWithWidth(203, R.drawable.avd_speed, AndroidUtilities.dp(54.0f), LocaleController.getString(R.string.AccDescrPremiumSpeed));
            this.speedItem = addItemWithWidth;
            addItemWithWidth.getIconView().setColorFilter(new PorterDuffColorFilter(Theme.getColor(i), PorterDuff.Mode.SRC_IN));
            this.gotoItem = this.actionMode.addItemWithWidth(200, R.drawable.msg_message, AndroidUtilities.dp(54.0f), LocaleController.getString(R.string.AccDescrGoToMessage));
            this.forwardItem = this.actionMode.addItemWithWidth(201, R.drawable.msg_forward, AndroidUtilities.dp(54.0f), LocaleController.getString(R.string.Forward));
            this.deleteItem = this.actionMode.addItemWithWidth(202, R.drawable.msg_delete, AndroidUtilities.dp(54.0f), LocaleController.getString(R.string.Delete));
        }
        if (this.selectedMessagesCountTextView != null) {
            DialogsSearchAdapter dialogsSearchAdapter = this.dialogsSearchAdapter;
            ((ViewGroup.MarginLayoutParams) this.selectedMessagesCountTextView.getLayoutParams()).leftMargin = AndroidUtilities.dp((dialogsSearchAdapter != null && (dialogsSearchAdapterDelegate = dialogsSearchAdapter.delegate) != null && (dialogsSearchAdapterDelegate.getSearchForumDialogId() > 0L ? 1 : (dialogsSearchAdapterDelegate.getSearchForumDialogId() == 0L ? 0 : -1)) != 0 ? 56 : 0) + 72);
            NumberTextView numberTextView3 = this.selectedMessagesCountTextView;
            numberTextView3.setLayoutParams(numberTextView3.getLayoutParams());
        }
        if (this.parent.getActionBar().getBackButton().getDrawable() instanceof MenuDrawable) {
            BackDrawable backDrawable = new BackDrawable(false);
            this.parent.getActionBar().setBackButtonDrawable(backDrawable);
            backDrawable.setColorFilter(null);
        }
        this.isActionModeShowed = z;
        if (z) {
            AndroidUtilities.hideKeyboard(this.parent.getParentActivity().getCurrentFocus());
            this.parent.getActionBar().showActionMode();
            this.selectedMessagesCountTextView.setNumber(this.selectedFiles.size(), false);
            this.speedItem.setVisibility(isSpeedItemVisible() ? 0 : 8);
            this.gotoItem.setVisibility(0);
            this.forwardItem.setVisibility(0);
            this.deleteItem.setVisibility(0);
            return;
        }
        this.parent.getActionBar().hideActionMode();
        this.selectedFiles.clear();
        for (int i2 = 0; i2 < getChildCount(); i2++) {
            if (getChildAt(i2) instanceof FilteredSearchView) {
                ((FilteredSearchView) getChildAt(i2)).update();
            }
            if (getChildAt(i2) instanceof SearchDownloadsContainer) {
                ((SearchDownloadsContainer) getChildAt(i2)).update(true);
            }
        }
        FilteredSearchView filteredSearchView = this.noMediaFiltersSearchView;
        if (filteredSearchView != null) {
            filteredSearchView.update();
        }
        int size = this.viewsByType.size();
        for (int i3 = 0; i3 < size; i3++) {
            View view = (View) this.viewsByType.valueAt(i3);
            if (view instanceof FilteredSearchView) {
                ((FilteredSearchView) view).update();
            }
        }
    }

    @Override
    public boolean actionModeShowing() {
        return this.isActionModeShowed;
    }

    public void cancelEnterAnimation() {
        this.itemsEnterAnimator.cancel();
        this.searchListView.invalidate();
        this.animateFromCount = 0;
    }

    public void clear() {
        this.currentSearchFilters.clear();
        collapsePublicPosts();
    }

    public void collapsePublicPosts() {
        if (this.expandedPublicPosts) {
            this.expandedPublicPosts = false;
            updateTabs();
            ViewPagerFixed.TabsView tabsView = this.tabsView;
            if (tabsView != null && tabsView.getCurrentTabId() != 0) {
                this.tabsView.scrollToTab(0, 0);
            }
            DialogsSearchAdapter dialogsSearchAdapter = this.dialogsSearchAdapter;
            if (dialogsSearchAdapter != null) {
                dialogsSearchAdapter.searchDialogs(this.lastSearchString, includeFolder() ? 1 : 0, true);
            }
        }
    }

    @Override
    public void didReceivedNotification(int i, int i2, Object... objArr) {
        UniversalAdapter universalAdapter;
        if (i == NotificationCenter.channelRecommendationsLoaded) {
            this.channelsEmptyView.showProgress(MessagesController.getInstance(this.currentAccount).getChannelRecommendations(0L) != null, true);
        } else if (i != NotificationCenter.dialogDeleted && i != NotificationCenter.dialogsNeedReload) {
            if (i == NotificationCenter.reloadWebappsHints) {
                universalAdapter = this.botsSearchAdapter;
                universalAdapter.update(true);
            } else {
                if (i == NotificationCenter.storiesListUpdated) {
                    Object obj = objArr[0];
                    HashtagsSearchAdapter hashtagsSearchAdapter = this.hashtagSearchAdapter;
                    if (obj == hashtagsSearchAdapter.list) {
                        hashtagsSearchAdapter.update(true);
                        return;
                    }
                    return;
                }
                return;
            }
        }
        this.channelsSearchAdapter.updateMyChannels();
        universalAdapter = this.channelsSearchAdapter;
        universalAdapter.update(true);
    }

    public ActionBarMenu getActionMode() {
        return this.actionMode;
    }

    public ArrayList<FiltersView.MediaFilterData> getCurrentSearchFilters() {
        return this.currentSearchFilters;
    }

    public SearchDownloadsContainer getDownloadsContainer() {
        return this.downloadsContainer;
    }

    public int getFolderId() {
        return this.folderId;
    }

    public int getPositionForType(int i) {
        for (int i2 = 0; i2 < this.viewPagerAdapter.items.size(); i2++) {
            if (((ViewPagerAdapter.Item) this.viewPagerAdapter.items.get(i2)).type == 3 && ((ViewPagerAdapter.Item) this.viewPagerAdapter.items.get(i2)).filterIndex == i) {
                return i2;
            }
        }
        return -1;
    }

    public ActionBarMenuItem getSpeedItem() {
        return this.speedItem;
    }

    public ViewPagerFixed.TabsView getTabsView() {
        return this.tabsView;
    }

    public void getThemeDescriptions(ArrayList arrayList) {
        for (int i = 0; i < this.searchListView.getChildCount(); i++) {
            View childAt = this.searchListView.getChildAt(i);
            if ((childAt instanceof ProfileSearchCell) || (childAt instanceof DialogCell) || (childAt instanceof HashtagSearchCell)) {
                arrayList.add(new ThemeDescription(childAt, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, Theme.key_windowBackgroundWhite));
            }
        }
        for (int i2 = 0; i2 < getChildCount(); i2++) {
            if (getChildAt(i2) instanceof FilteredSearchView) {
                arrayList.addAll(((FilteredSearchView) getChildAt(i2)).getThemeDescriptions());
            }
        }
        int size = this.viewsByType.size();
        for (int i3 = 0; i3 < size; i3++) {
            View view = (View) this.viewsByType.valueAt(i3);
            if (view instanceof FilteredSearchView) {
                arrayList.addAll(((FilteredSearchView) view).getThemeDescriptions());
            }
        }
        FilteredSearchView filteredSearchView = this.noMediaFiltersSearchView;
        if (filteredSearchView != null) {
            arrayList.addAll(filteredSearchView.getThemeDescriptions());
        }
        arrayList.add(new ThemeDescription(this.emptyView.title, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, Theme.key_windowBackgroundWhiteBlackText));
        arrayList.add(new ThemeDescription(this.emptyView.subtitle, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, Theme.key_windowBackgroundWhiteGrayText));
        arrayList.addAll(SimpleThemeDescription.createThemeDescriptions(new ThemeDescription.ThemeDescriptionDelegate() {
            @Override
            public final void didSetColor() {
                SearchViewPager.this.lambda$getThemeDescriptions$4();
            }

            @Override
            public void onAnimationProgress(float f) {
                ThemeDescription.ThemeDescriptionDelegate.CC.$default$onAnimationProgress(this, f);
            }
        }, Theme.key_actionBarActionModeDefaultIcon));
    }

    @Override
    public void goToMessage(MessageObject messageObject) {
        String str;
        Bundle bundle = new Bundle();
        long dialogId = messageObject.getDialogId();
        if (DialogObject.isEncryptedDialog(dialogId)) {
            bundle.putInt("enc_id", DialogObject.getEncryptedChatId(dialogId));
        } else {
            if (DialogObject.isUserDialog(dialogId)) {
                str = "user_id";
            } else {
                TLRPC.Chat chat = AccountInstance.getInstance(this.currentAccount).getMessagesController().getChat(Long.valueOf(-dialogId));
                if (chat != null && chat.migrated_to != null) {
                    bundle.putLong("migrated_to", dialogId);
                    dialogId = -chat.migrated_to.channel_id;
                }
                dialogId = -dialogId;
                str = "chat_id";
            }
            bundle.putLong(str, dialogId);
        }
        bundle.putInt("message_id", messageObject.getId());
        this.parent.presentFragment(new ChatActivity(bundle));
        showActionMode(false);
    }

    public void hideActionMode() {
        showActionMode(false);
    }

    protected abstract boolean includeDownloads();

    public boolean includeFolder() {
        for (int i = 0; i < this.currentSearchFilters.size(); i++) {
            if (((FiltersView.MediaFilterData) this.currentSearchFilters.get(i)).filterType == 7) {
                return true;
            }
        }
        return false;
    }

    @Override
    protected void invalidateBlur() {
        this.fragmentView.invalidateBlur();
    }

    @Override
    public boolean isSelected(FilteredSearchView.MessageHashId messageHashId) {
        return this.selectedFiles.containsKey(messageHashId);
    }

    public void messagesDeleted(long r12, java.util.ArrayList r14) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.SearchViewPager.messagesDeleted(long, java.util.ArrayList):void");
    }

    public void onActionBarItemClick(int i) {
        if (i == 202) {
            BaseFragment baseFragment = this.parent;
            if (baseFragment == null || baseFragment.getParentActivity() == null) {
                return;
            }
            final ArrayList arrayList = new ArrayList(this.selectedFiles.values());
            AlertDialog.Builder builder = new AlertDialog.Builder(this.parent.getParentActivity());
            builder.setTitle(LocaleController.formatPluralString("RemoveDocumentsTitle", this.selectedFiles.size(), new Object[0]));
            SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder();
            spannableStringBuilder.append((CharSequence) AndroidUtilities.replaceTags(LocaleController.formatPluralString("RemoveDocumentsMessage", this.selectedFiles.size(), new Object[0]))).append((CharSequence) "\n\n").append((CharSequence) LocaleController.getString(R.string.RemoveDocumentsAlertMessage));
            builder.setMessage(spannableStringBuilder);
            builder.setNegativeButton(LocaleController.getString(R.string.Cancel), new AlertDialog.OnButtonClickListener() {
                @Override
                public final void onClick(AlertDialog alertDialog, int i2) {
                    alertDialog.dismiss();
                }
            });
            builder.setPositiveButton(LocaleController.getString(R.string.Delete), new AlertDialog.OnButtonClickListener() {
                @Override
                public final void onClick(AlertDialog alertDialog, int i2) {
                    SearchViewPager.this.lambda$onActionBarItemClick$2(arrayList, alertDialog, i2);
                }
            });
            TextView textView = (TextView) builder.show().getButton(-1);
            if (textView != null) {
                textView.setTextColor(Theme.getColor(Theme.key_text_RedBold));
                return;
            }
            return;
        }
        if (i == 203) {
            if (isSpeedItemVisible()) {
                this.parent.showDialog(new PremiumFeatureBottomSheet(this.parent, 2, true));
            }
        } else if (i == 200) {
            if (this.selectedFiles.size() != 1) {
                return;
            }
            goToMessage((MessageObject) this.selectedFiles.values().iterator().next());
        } else if (i == 201) {
            Bundle bundle = new Bundle();
            bundle.putBoolean("onlySelect", true);
            bundle.putInt("dialogsType", 3);
            DialogsActivity dialogsActivity = new DialogsActivity(bundle);
            dialogsActivity.setDelegate(new DialogsActivity.DialogsActivityDelegate() {
                @Override
                public final boolean didSelectDialogs(DialogsActivity dialogsActivity2, ArrayList arrayList2, CharSequence charSequence, boolean z, boolean z2, int i2, TopicsFragment topicsFragment) {
                    boolean lambda$onActionBarItemClick$3;
                    lambda$onActionBarItemClick$3 = SearchViewPager.this.lambda$onActionBarItemClick$3(dialogsActivity2, arrayList2, charSequence, z, z2, i2, topicsFragment);
                    return lambda$onActionBarItemClick$3;
                }
            });
            this.parent.presentFragment(dialogsActivity);
        }
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.channelRecommendationsLoaded);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.dialogDeleted);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.dialogsNeedReload);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.reloadWebappsHints);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.storiesListUpdated);
        this.attached = true;
        DialogsChannelsAdapter dialogsChannelsAdapter = this.channelsSearchAdapter;
        if (dialogsChannelsAdapter != null) {
            dialogsChannelsAdapter.update(false);
        }
        DialogsBotsAdapter dialogsBotsAdapter = this.botsSearchAdapter;
        if (dialogsBotsAdapter != null) {
            dialogsBotsAdapter.update(false);
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        this.attached = false;
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.channelRecommendationsLoaded);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.dialogDeleted);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.dialogsNeedReload);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.reloadWebappsHints);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.storiesListUpdated);
    }

    @Override
    protected void onItemSelected(View view, View view2, int i, int i2) {
        if (i == 0) {
            if (this.noMediaFiltersSearchView.getVisibility() == 0) {
                this.noMediaFiltersSearchView.setDelegate(this.filteredSearchViewDelegate, false);
                this.dialogsSearchAdapter.setFiltersDelegate(null, false);
            } else {
                this.noMediaFiltersSearchView.setDelegate(null, false);
                this.dialogsSearchAdapter.setFiltersDelegate(this.filteredSearchViewDelegate, true);
            }
        } else if (view instanceof FilteredSearchView) {
            ((FilteredSearchView) view).setDelegate(this.filteredSearchViewDelegate, i2 == 0 && this.noMediaFiltersSearchView.getVisibility() != 0);
        }
        if (view2 instanceof FilteredSearchView) {
            ((FilteredSearchView) view2).setDelegate(null, false);
        } else {
            this.dialogsSearchAdapter.setFiltersDelegate(null, false);
            this.noMediaFiltersSearchView.setDelegate(null, false);
        }
    }

    public void onResume() {
        DialogsSearchAdapter dialogsSearchAdapter = this.dialogsSearchAdapter;
        if (dialogsSearchAdapter != null) {
            dialogsSearchAdapter.notifyDataSetChanged();
        }
    }

    public void onShown() {
        DialogsSearchAdapter dialogsSearchAdapter = this.dialogsSearchAdapter;
        if (dialogsSearchAdapter != null) {
            dialogsSearchAdapter.resetFilter();
        }
    }

    public void onTextChanged(String str) {
        View currentView = getCurrentView();
        boolean z = TextUtils.isEmpty(this.lastSearchString) ? true : !this.attached;
        this.lastSearchString = str;
        search(currentView, getCurrentPosition(), str, z);
    }

    public void removeSearchFilter(FiltersView.MediaFilterData mediaFilterData) {
        this.currentSearchFilters.remove(mediaFilterData);
    }

    public void reset() {
        setPosition(0);
        if (this.dialogsSearchAdapter.getItemCount() > 0) {
            this.searchLayoutManager.scrollToPositionWithOffset(0, 0);
        }
        LinearLayoutManager linearLayoutManager = this.channelsSearchLayoutManager;
        if (linearLayoutManager != null) {
            linearLayoutManager.scrollToPositionWithOffset(0, 0);
        }
        LinearLayoutManager linearLayoutManager2 = this.botsSearchLayoutManager;
        if (linearLayoutManager2 != null) {
            linearLayoutManager2.scrollToPositionWithOffset(0, 0);
        }
        LinearLayoutManager linearLayoutManager3 = this.hashtagSearchLayoutManager;
        if (linearLayoutManager3 != null) {
            linearLayoutManager3.scrollToPositionWithOffset(0, 0);
        }
        this.viewsByType.clear();
    }

    public void runResultsEnterAnimation() {
        RecyclerItemsEnterAnimator recyclerItemsEnterAnimator = this.itemsEnterAnimator;
        int i = this.animateFromCount;
        recyclerItemsEnterAnimator.showItemsAnimated(i > 0 ? i + 1 : 0);
        this.animateFromCount = this.dialogsSearchAdapter.getItemCount();
    }

    public void setFilteredSearchViewDelegate(FilteredSearchView.Delegate delegate) {
        this.filteredSearchViewDelegate = delegate;
    }

    public void setKeyboardHeight(int i) {
        FilteredSearchView filteredSearchView;
        this.keyboardSize = i;
        boolean z = getVisibility() == 0 && getAlpha() > 0.0f;
        for (int i2 = 0; i2 < getChildCount(); i2++) {
            if (getChildAt(i2) instanceof FilteredSearchView) {
                filteredSearchView = (FilteredSearchView) getChildAt(i2);
            } else if (getChildAt(i2) == this.searchContainer) {
                this.emptyView.setKeyboardHeight(i, z);
                filteredSearchView = this.noMediaFiltersSearchView;
            } else {
                if (getChildAt(i2) instanceof SearchDownloadsContainer) {
                    ((SearchDownloadsContainer) getChildAt(i2)).setKeyboardHeight(i, z);
                } else if (getChildAt(i2) == this.channelsSearchContainer) {
                    this.channelsEmptyView.setKeyboardHeight(i, z);
                }
            }
            filteredSearchView.setKeyboardHeight(i, z);
        }
    }

    @Override
    public void setPosition(int i) {
        if (i < 0) {
            return;
        }
        super.setPosition(i);
        this.viewsByType.clear();
        ViewPagerFixed.TabsView tabsView = this.tabsView;
        if (tabsView != null) {
            tabsView.selectTabWithId(i, 1.0f);
        }
        invalidate();
    }

    @Override
    public void showActionMode() {
        showActionMode(true);
    }

    public void showDownloads() {
        setPosition((this.expandedPublicPosts ? 1 : 0) + 4);
    }

    public void showOnlyDialogsAdapter(boolean z) {
        this.showOnlyDialogsAdapter = z;
    }

    @Override
    public void toggleItemSelection(MessageObject messageObject, View view, int i) {
        boolean z;
        FilteredSearchView.MessageHashId messageHashId = new FilteredSearchView.MessageHashId(messageObject.getId(), messageObject.getDialogId());
        if (this.selectedFiles.containsKey(messageHashId)) {
            this.selectedFiles.remove(messageHashId);
        } else if (this.selectedFiles.size() >= 100) {
            return;
        } else {
            this.selectedFiles.put(messageHashId, messageObject);
        }
        if (this.selectedFiles.size() == 0) {
            showActionMode(false);
        } else {
            this.selectedMessagesCountTextView.setNumber(this.selectedFiles.size(), true);
            ActionBarMenuItem actionBarMenuItem = this.gotoItem;
            if (actionBarMenuItem != null) {
                actionBarMenuItem.setVisibility(this.selectedFiles.size() == 1 ? 0 : 8);
            }
            if (this.speedItem != null) {
                boolean isSpeedItemVisible = isSpeedItemVisible();
                int i2 = isSpeedItemVisible ? 0 : 8;
                if (this.speedItem.getVisibility() != i2) {
                    this.speedItem.setVisibility(i2);
                    int i3 = Build.VERSION.SDK_INT;
                    if (i3 >= 21) {
                        AnimatedVectorDrawable m = AnimatedVectorDrawableCompat$$ExternalSyntheticApiModelOutline0.m(this.speedItem.getIconView().getDrawable());
                        m.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_actionBarActionModeDefaultIcon), PorterDuff.Mode.SRC_IN));
                        if (isSpeedItemVisible) {
                            m.start();
                        } else if (i3 >= 23) {
                            m.reset();
                        } else {
                            m.setVisible(false, true);
                        }
                    }
                }
            }
            if (this.deleteItem != null) {
                Iterator it = this.selectedFiles.keySet().iterator();
                while (true) {
                    if (!it.hasNext()) {
                        z = true;
                        break;
                    } else {
                        if (!((MessageObject) this.selectedFiles.get((FilteredSearchView.MessageHashId) it.next())).isDownloadingFile) {
                            z = false;
                            break;
                        }
                    }
                }
                this.deleteItem.setVisibility(z ? 0 : 8);
            }
        }
        if (view instanceof SharedDocumentCell) {
            ((SharedDocumentCell) view).setChecked(this.selectedFiles.containsKey(messageHashId), true);
            return;
        }
        if (view instanceof SharedPhotoVideoCell) {
            ((SharedPhotoVideoCell) view).setChecked(i, this.selectedFiles.containsKey(messageHashId), true);
            return;
        }
        if (view instanceof SharedLinkCell) {
            ((SharedLinkCell) view).setChecked(this.selectedFiles.containsKey(messageHashId), true);
            return;
        }
        if (view instanceof SharedAudioCell) {
            ((SharedAudioCell) view).setChecked(this.selectedFiles.containsKey(messageHashId), true);
        } else if (view instanceof ContextLinkCell) {
            ((ContextLinkCell) view).setChecked(this.selectedFiles.containsKey(messageHashId), true);
        } else if (view instanceof DialogCell) {
            ((DialogCell) view).setChecked(this.selectedFiles.containsKey(messageHashId), true);
        }
    }

    public void updateColors() {
        for (int i = 0; i < getChildCount(); i++) {
            if (getChildAt(i) instanceof FilteredSearchView) {
                RecyclerListView recyclerListView = ((FilteredSearchView) getChildAt(i)).recyclerListView;
                int childCount = recyclerListView.getChildCount();
                for (int i2 = 0; i2 < childCount; i2++) {
                    View childAt = recyclerListView.getChildAt(i2);
                    if (childAt instanceof DialogCell) {
                        ((DialogCell) childAt).update(0);
                    }
                }
            }
        }
        int size = this.viewsByType.size();
        for (int i3 = 0; i3 < size; i3++) {
            View view = (View) this.viewsByType.valueAt(i3);
            if (view instanceof FilteredSearchView) {
                RecyclerListView recyclerListView2 = ((FilteredSearchView) view).recyclerListView;
                int childCount2 = recyclerListView2.getChildCount();
                for (int i4 = 0; i4 < childCount2; i4++) {
                    View childAt2 = recyclerListView2.getChildAt(i4);
                    if (childAt2 instanceof DialogCell) {
                        ((DialogCell) childAt2).update(0);
                    }
                }
            }
        }
        FilteredSearchView filteredSearchView = this.noMediaFiltersSearchView;
        if (filteredSearchView != null) {
            RecyclerListView recyclerListView3 = filteredSearchView.recyclerListView;
            int childCount3 = recyclerListView3.getChildCount();
            for (int i5 = 0; i5 < childCount3; i5++) {
                View childAt3 = recyclerListView3.getChildAt(i5);
                if (childAt3 instanceof DialogCell) {
                    ((DialogCell) childAt3).update(0);
                }
            }
        }
    }

    public void updateTabs() {
        updateTabs(false);
    }

    public void updateTabs(boolean z) {
        this.viewPagerAdapter.updateItems();
        fillTabs(z);
        ViewPagerFixed.TabsView tabsView = this.tabsView;
        if (tabsView != null) {
            tabsView.finishAddingTabs();
        }
    }
}
