package org.telegram.ui.Components;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.LongSparseArray;
import android.util.SparseArray;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MediaDataController;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.R;
import org.telegram.messenger.UserConfig;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.AdjustPanLayoutHelper;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ActionBar.ThemeDescription;
import org.telegram.ui.Adapters.StickersSearchAdapter;
import org.telegram.ui.Cells.EmptyCell;
import org.telegram.ui.Cells.FeaturedStickerSetCell2;
import org.telegram.ui.Cells.FeaturedStickerSetInfoCell;
import org.telegram.ui.Cells.GraySectionCell;
import org.telegram.ui.Cells.StickerEmojiCell;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.Components.StickersAlert;
import org.telegram.ui.Components.TrendingStickersLayout;

public class TrendingStickersLayout extends FrameLayout implements NotificationCenter.NotificationCenterDelegate {
    private final TrendingStickersAdapter adapter;
    private final int currentAccount;
    private final Delegate delegate;
    ValueAnimator glueToTopAnimator;
    private boolean gluedToTop;
    private long hash;
    private float highlightProgress;
    private boolean ignoreLayout;
    private final LongSparseArray installingStickerSets;
    private final GridLayoutManager layoutManager;
    private final RecyclerListView listView;
    private boolean loaded;
    private boolean motionEventCatchedByListView;
    private RecyclerView.OnScrollListener onScrollListener;
    Paint paint;
    private BaseFragment parentFragment;
    private final TLRPC.StickerSetCovered[] primaryInstallingStickerSets;
    private final LongSparseArray removingStickerSets;
    private final Theme.ResourcesProvider resourcesProvider;
    private boolean scrollFromAnimator;
    private TLRPC.StickerSetCovered scrollToSet;
    private final StickersSearchAdapter searchAdapter;
    private final FrameLayout searchLayout;
    private final SearchField searchView;
    private final View shadowView;
    private boolean shadowVisible;
    private int topOffset;
    private boolean wasLayout;

    public static abstract class Delegate {
        private String[] lastSearchKeyboardLanguage = new String[0];

        public boolean canSchedule() {
            return false;
        }

        public boolean canSendSticker() {
            return false;
        }

        public String[] getLastSearchKeyboardLanguage() {
            return this.lastSearchKeyboardLanguage;
        }

        public boolean isInScheduleMode() {
            return false;
        }

        public boolean onListViewInterceptTouchEvent(RecyclerListView recyclerListView, MotionEvent motionEvent) {
            return false;
        }

        public boolean onListViewTouchEvent(RecyclerListView recyclerListView, RecyclerListView.OnItemClickListener onItemClickListener, MotionEvent motionEvent) {
            return false;
        }

        public void onStickerSelected(TLRPC.Document document, Object obj, boolean z, boolean z2, int i) {
        }

        public abstract void onStickerSetAdd(TLRPC.StickerSetCovered stickerSetCovered, boolean z);

        public abstract void onStickerSetRemove(TLRPC.StickerSetCovered stickerSetCovered);

        public void setLastSearchKeyboardLanguage(String[] strArr) {
            this.lastSearchKeyboardLanguage = strArr;
        }
    }

    public class TrendingStickersAdapter extends RecyclerListView.SelectionAdapter {
        private final Context context;
        private boolean endReached;
        private boolean loadingMore;
        private int totalItems;
        private final SparseArray cache = new SparseArray();
        private final ArrayList sets = new ArrayList();
        private final SparseArray positionsToSets = new SparseArray();
        private final HashMap setsToPosition = new HashMap();
        private final ArrayList otherPacks = new ArrayList();
        private int stickersPerRow = 5;

        public TrendingStickersAdapter(Context context) {
            this.context = context;
        }

        private void bindStickerSetCell(android.view.View r12, int r13, boolean r14) {
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.TrendingStickersLayout.TrendingStickersAdapter.bindStickerSetCell(android.view.View, int, boolean):void");
        }

        public void installStickerSet(TLRPC.StickerSetCovered stickerSetCovered, View view) {
            boolean z;
            int i = 0;
            while (true) {
                if (i >= TrendingStickersLayout.this.primaryInstallingStickerSets.length) {
                    break;
                }
                if (TrendingStickersLayout.this.primaryInstallingStickerSets[i] != null) {
                    TLRPC.TL_messages_stickerSet stickerSetById = MediaDataController.getInstance(TrendingStickersLayout.this.currentAccount).getStickerSetById(TrendingStickersLayout.this.primaryInstallingStickerSets[i].set.id);
                    if (stickerSetById != null && !stickerSetById.set.archived) {
                        TrendingStickersLayout.this.primaryInstallingStickerSets[i] = null;
                        break;
                    } else if (TrendingStickersLayout.this.primaryInstallingStickerSets[i].set.id == stickerSetCovered.set.id) {
                        return;
                    }
                }
                i++;
            }
            int i2 = 0;
            while (true) {
                if (i2 >= TrendingStickersLayout.this.primaryInstallingStickerSets.length) {
                    z = false;
                    break;
                } else {
                    if (TrendingStickersLayout.this.primaryInstallingStickerSets[i2] == null) {
                        TrendingStickersLayout.this.primaryInstallingStickerSets[i2] = stickerSetCovered;
                        z = true;
                        break;
                    }
                    i2++;
                }
            }
            if (!z && view != null) {
                if (view instanceof FeaturedStickerSetCell2) {
                    ((FeaturedStickerSetCell2) view).setDrawProgress(true, true);
                } else if (view instanceof FeaturedStickerSetInfoCell) {
                    ((FeaturedStickerSetInfoCell) view).setAddDrawProgress(true, true);
                }
            }
            TrendingStickersLayout.this.installingStickerSets.put(stickerSetCovered.set.id, stickerSetCovered);
            if (view != null) {
                TrendingStickersLayout.this.delegate.onStickerSetAdd(stickerSetCovered, z);
                return;
            }
            int size = this.positionsToSets.size();
            for (int i3 = 0; i3 < size; i3++) {
                TLRPC.StickerSetCovered stickerSetCovered2 = (TLRPC.StickerSetCovered) this.positionsToSets.get(i3);
                if (stickerSetCovered2 != null && stickerSetCovered2.set.id == stickerSetCovered.set.id) {
                    notifyItemChanged(i3, 0);
                    return;
                }
            }
        }

        public void lambda$loadMoreStickerSets$2(TLRPC.TL_error tL_error, TLObject tLObject) {
            int i;
            int i2;
            this.loadingMore = false;
            if (tL_error != null || !(tLObject instanceof TLRPC.TL_messages_featuredStickers)) {
                this.endReached = true;
                return;
            }
            ArrayList<TLRPC.StickerSetCovered> arrayList = ((TLRPC.TL_messages_featuredStickers) tLObject).sets;
            if (arrayList.size() < 40) {
                this.endReached = true;
            }
            if (arrayList.isEmpty()) {
                return;
            }
            if (this.otherPacks.isEmpty()) {
                SparseArray sparseArray = this.cache;
                int i3 = this.totalItems;
                this.totalItems = i3 + 1;
                sparseArray.put(i3, -1);
            }
            this.otherPacks.addAll(arrayList);
            int size = this.sets.size();
            for (int i4 = 0; i4 < arrayList.size(); i4++) {
                TLRPC.StickerSetCovered stickerSetCovered = arrayList.get(i4);
                if (!stickerSetCovered.covers.isEmpty() || stickerSetCovered.cover != null) {
                    this.sets.add(stickerSetCovered);
                    this.positionsToSets.put(this.totalItems, stickerSetCovered);
                    SparseArray sparseArray2 = this.cache;
                    int i5 = this.totalItems;
                    this.totalItems = i5 + 1;
                    int i6 = size + 1;
                    sparseArray2.put(i5, Integer.valueOf(size));
                    if (stickerSetCovered.covers.isEmpty()) {
                        this.cache.put(this.totalItems, stickerSetCovered.cover);
                        i = 1;
                    } else {
                        i = (int) Math.ceil(stickerSetCovered.covers.size() / this.stickersPerRow);
                        for (int i7 = 0; i7 < stickerSetCovered.covers.size(); i7++) {
                            this.cache.put(this.totalItems + i7, stickerSetCovered.covers.get(i7));
                        }
                    }
                    int i8 = 0;
                    while (true) {
                        i2 = this.stickersPerRow * i;
                        if (i8 >= i2) {
                            break;
                        }
                        this.positionsToSets.put(this.totalItems + i8, stickerSetCovered);
                        i8++;
                    }
                    this.totalItems += i2;
                    size = i6;
                }
            }
            notifyDataSetChanged();
        }

        public void lambda$loadMoreStickerSets$3(final TLObject tLObject, final TLRPC.TL_error tL_error) {
            AndroidUtilities.runOnUIThread(new Runnable() {
                @Override
                public final void run() {
                    TrendingStickersLayout.TrendingStickersAdapter.this.lambda$loadMoreStickerSets$2(tL_error, tLObject);
                }
            });
        }

        public void lambda$onCreateViewHolder$0(View view) {
            FeaturedStickerSetInfoCell featuredStickerSetInfoCell = (FeaturedStickerSetInfoCell) view.getParent();
            TLRPC.StickerSetCovered stickerSet = featuredStickerSetInfoCell.getStickerSet();
            if (TrendingStickersLayout.this.installingStickerSets.indexOfKey(stickerSet.set.id) >= 0 || TrendingStickersLayout.this.removingStickerSets.indexOfKey(stickerSet.set.id) >= 0) {
                return;
            }
            if (!featuredStickerSetInfoCell.isInstalled()) {
                installStickerSet(stickerSet, featuredStickerSetInfoCell);
            } else {
                TrendingStickersLayout.this.removingStickerSets.put(stickerSet.set.id, stickerSet);
                TrendingStickersLayout.this.delegate.onStickerSetRemove(stickerSet);
            }
        }

        public void lambda$onCreateViewHolder$1(View view) {
            FeaturedStickerSetCell2 featuredStickerSetCell2 = (FeaturedStickerSetCell2) view.getParent();
            TLRPC.StickerSetCovered stickerSet = featuredStickerSetCell2.getStickerSet();
            if (TrendingStickersLayout.this.installingStickerSets.indexOfKey(stickerSet.set.id) >= 0 || TrendingStickersLayout.this.removingStickerSets.indexOfKey(stickerSet.set.id) >= 0) {
                return;
            }
            if (!featuredStickerSetCell2.isInstalled()) {
                installStickerSet(stickerSet, featuredStickerSetCell2);
            } else {
                TrendingStickersLayout.this.removingStickerSets.put(stickerSet.set.id, stickerSet);
                TrendingStickersLayout.this.delegate.onStickerSetRemove(stickerSet);
            }
        }

        @Override
        public int getItemCount() {
            return this.totalItems + 1;
        }

        @Override
        public int getItemViewType(int i) {
            if (i == getItemCount() - 1) {
                return 3;
            }
            Object obj = this.cache.get(i);
            if (obj == null) {
                return 1;
            }
            if (obj instanceof TLRPC.Document) {
                return 0;
            }
            return obj.equals(-1) ? 4 : 2;
        }

        public void getThemeDescriptions(List list, RecyclerListView recyclerListView, ThemeDescription.ThemeDescriptionDelegate themeDescriptionDelegate) {
            FeaturedStickerSetInfoCell.createThemeDescriptions(list, recyclerListView, themeDescriptionDelegate);
            FeaturedStickerSetCell2.createThemeDescriptions(list, recyclerListView, themeDescriptionDelegate);
            GraySectionCell.createThemeDescriptions(list, recyclerListView);
        }

        @Override
        public boolean isEnabled(RecyclerView.ViewHolder viewHolder) {
            return viewHolder.getItemViewType() == 5;
        }

        public void loadMoreStickerSets() {
            if (!TrendingStickersLayout.this.loaded || this.loadingMore || this.endReached) {
                return;
            }
            this.loadingMore = true;
            TLRPC.TL_messages_getOldFeaturedStickers tL_messages_getOldFeaturedStickers = new TLRPC.TL_messages_getOldFeaturedStickers();
            tL_messages_getOldFeaturedStickers.offset = this.otherPacks.size();
            tL_messages_getOldFeaturedStickers.limit = 40;
            ConnectionsManager.getInstance(TrendingStickersLayout.this.currentAccount).sendRequest(tL_messages_getOldFeaturedStickers, new RequestDelegate() {
                @Override
                public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                    TrendingStickersLayout.TrendingStickersAdapter.this.lambda$loadMoreStickerSets$3(tLObject, tL_error);
                }
            });
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
            int itemViewType = viewHolder.getItemViewType();
            if (itemViewType == 0) {
                ((StickerEmojiCell) viewHolder.itemView).setSticker((TLRPC.Document) this.cache.get(i), this.positionsToSets.get(i), false);
            } else {
                if (itemViewType == 1) {
                    ((EmptyCell) viewHolder.itemView).setHeight(AndroidUtilities.dp(82.0f));
                    return;
                }
                if (itemViewType != 2) {
                    if (itemViewType == 4) {
                        ((GraySectionCell) viewHolder.itemView).setText(LocaleController.getString(R.string.OtherStickers));
                        return;
                    } else if (itemViewType != 5) {
                        return;
                    }
                }
                bindStickerSetCell(viewHolder.itemView, i, false);
            }
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i, List list) {
            if (!list.contains(0)) {
                super.onBindViewHolder(viewHolder, i, list);
                return;
            }
            int itemViewType = viewHolder.getItemViewType();
            if (itemViewType == 2 || itemViewType == 5) {
                bindStickerSetCell(viewHolder.itemView, i, true);
            }
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            FrameLayout frameLayout;
            FrameLayout frameLayout2;
            if (i != 0) {
                if (i == 1) {
                    frameLayout2 = new EmptyCell(this.context);
                } else if (i == 2) {
                    FeaturedStickerSetInfoCell featuredStickerSetInfoCell = new FeaturedStickerSetInfoCell(this.context, 17, true, true, TrendingStickersLayout.this.resourcesProvider);
                    featuredStickerSetInfoCell.setAddOnClickListener(new View.OnClickListener() {
                        @Override
                        public final void onClick(View view) {
                            TrendingStickersLayout.TrendingStickersAdapter.this.lambda$onCreateViewHolder$0(view);
                        }
                    });
                    frameLayout2 = featuredStickerSetInfoCell;
                } else if (i == 3) {
                    frameLayout2 = new View(this.context);
                } else if (i == 4) {
                    frameLayout2 = new GraySectionCell(this.context, TrendingStickersLayout.this.resourcesProvider);
                } else if (i != 5) {
                    frameLayout2 = null;
                } else {
                    FeaturedStickerSetCell2 featuredStickerSetCell2 = new FeaturedStickerSetCell2(this.context, TrendingStickersLayout.this.resourcesProvider);
                    featuredStickerSetCell2.setAddOnClickListener(new View.OnClickListener() {
                        @Override
                        public final void onClick(View view) {
                            TrendingStickersLayout.TrendingStickersAdapter.this.lambda$onCreateViewHolder$1(view);
                        }
                    });
                    featuredStickerSetCell2.getImageView().setLayerNum(3);
                    frameLayout = featuredStickerSetCell2;
                }
                return new RecyclerListView.Holder(frameLayout2);
            }
            StickerEmojiCell stickerEmojiCell = new StickerEmojiCell(this.context, false, TrendingStickersLayout.this.resourcesProvider) {
                @Override
                public void onMeasure(int i2, int i3) {
                    super.onMeasure(i2, View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(82.0f), 1073741824));
                }
            };
            stickerEmojiCell.getImageView().setLayerNum(3);
            frameLayout = stickerEmojiCell;
            frameLayout2 = frameLayout;
            return new RecyclerListView.Holder(frameLayout2);
        }

        public void refreshStickerSets() {
            int i;
            int measuredWidth = TrendingStickersLayout.this.getMeasuredWidth();
            if (measuredWidth != 0) {
                this.stickersPerRow = Math.max(5, measuredWidth / AndroidUtilities.dp(72.0f));
                if (TrendingStickersLayout.this.layoutManager.getSpanCount() != this.stickersPerRow) {
                    TrendingStickersLayout.this.layoutManager.setSpanCount(this.stickersPerRow);
                    TrendingStickersLayout.this.loaded = false;
                }
            }
            if (TrendingStickersLayout.this.loaded) {
                return;
            }
            this.cache.clear();
            this.positionsToSets.clear();
            this.setsToPosition.clear();
            this.sets.clear();
            this.totalItems = 0;
            MediaDataController mediaDataController = MediaDataController.getInstance(TrendingStickersLayout.this.currentAccount);
            ArrayList arrayList = new ArrayList(mediaDataController.getFeaturedStickerSets());
            int size = arrayList.size();
            arrayList.addAll(this.otherPacks);
            int i2 = 0;
            int i3 = 0;
            while (true) {
                int i4 = 1;
                if (i2 >= arrayList.size()) {
                    break;
                }
                TLRPC.StickerSetCovered stickerSetCovered = (TLRPC.StickerSetCovered) arrayList.get(i2);
                if (!stickerSetCovered.covers.isEmpty() || stickerSetCovered.cover != null) {
                    if (i2 == size) {
                        SparseArray sparseArray = this.cache;
                        int i5 = this.totalItems;
                        this.totalItems = i5 + 1;
                        sparseArray.put(i5, -1);
                    }
                    this.sets.add(stickerSetCovered);
                    this.positionsToSets.put(this.totalItems, stickerSetCovered);
                    this.setsToPosition.put(stickerSetCovered, Integer.valueOf(this.totalItems));
                    SparseArray sparseArray2 = this.cache;
                    int i6 = this.totalItems;
                    this.totalItems = i6 + 1;
                    int i7 = i3 + 1;
                    sparseArray2.put(i6, Integer.valueOf(i3));
                    if (stickerSetCovered.covers.isEmpty()) {
                        this.cache.put(this.totalItems, stickerSetCovered.cover);
                    } else {
                        i4 = (int) Math.ceil(stickerSetCovered.covers.size() / this.stickersPerRow);
                        for (int i8 = 0; i8 < stickerSetCovered.covers.size(); i8++) {
                            this.cache.put(this.totalItems + i8, stickerSetCovered.covers.get(i8));
                        }
                    }
                    int i9 = 0;
                    while (true) {
                        i = this.stickersPerRow * i4;
                        if (i9 >= i) {
                            break;
                        }
                        this.positionsToSets.put(this.totalItems + i9, stickerSetCovered);
                        i9++;
                    }
                    this.totalItems += i;
                    i3 = i7;
                }
                i2++;
            }
            if (this.totalItems != 0) {
                TrendingStickersLayout.this.loaded = true;
                TrendingStickersLayout.this.hash = mediaDataController.getFeaturedStickersHashWithoutUnread(false);
            }
            notifyDataSetChanged();
        }

        public void updateColors(RecyclerListView recyclerListView) {
            int childCount = recyclerListView.getChildCount();
            for (int i = 0; i < childCount; i++) {
                View childAt = recyclerListView.getChildAt(i);
                if (childAt instanceof FeaturedStickerSetInfoCell) {
                    ((FeaturedStickerSetInfoCell) childAt).updateColors();
                } else if (childAt instanceof FeaturedStickerSetCell2) {
                    ((FeaturedStickerSetCell2) childAt).updateColors();
                }
            }
        }
    }

    public TrendingStickersLayout(Context context, Delegate delegate) {
        this(context, delegate, new TLRPC.StickerSetCovered[10], new LongSparseArray(), new LongSparseArray(), null, null);
    }

    public TrendingStickersLayout(Context context, final Delegate delegate, TLRPC.StickerSetCovered[] stickerSetCoveredArr, LongSparseArray longSparseArray, LongSparseArray longSparseArray2, TLRPC.StickerSetCovered stickerSetCovered, Theme.ResourcesProvider resourcesProvider) {
        super(context);
        int i = UserConfig.selectedAccount;
        this.currentAccount = i;
        this.highlightProgress = 1.0f;
        this.paint = new Paint();
        this.delegate = delegate;
        this.primaryInstallingStickerSets = stickerSetCoveredArr;
        this.installingStickerSets = longSparseArray;
        this.removingStickerSets = longSparseArray2;
        this.scrollToSet = stickerSetCovered;
        this.resourcesProvider = resourcesProvider;
        TrendingStickersAdapter trendingStickersAdapter = new TrendingStickersAdapter(context);
        this.adapter = trendingStickersAdapter;
        this.searchAdapter = new StickersSearchAdapter(context, new StickersSearchAdapter.Delegate() {
            @Override
            public String[] getLastSearchKeyboardLanguage() {
                return delegate.getLastSearchKeyboardLanguage();
            }

            @Override
            public int getStickersPerRow() {
                return TrendingStickersLayout.this.adapter.stickersPerRow;
            }

            @Override
            public void onSearchStart() {
                TrendingStickersLayout.this.searchView.getProgressDrawable().startAnimation();
            }

            @Override
            public void onSearchStop() {
                TrendingStickersLayout.this.searchView.getProgressDrawable().stopAnimation();
            }

            @Override
            public void onStickerSetAdd(TLRPC.StickerSetCovered stickerSetCovered2, boolean z) {
                delegate.onStickerSetAdd(stickerSetCovered2, z);
            }

            @Override
            public void onStickerSetRemove(TLRPC.StickerSetCovered stickerSetCovered2) {
                delegate.onStickerSetRemove(stickerSetCovered2);
            }

            @Override
            public void setAdapterVisible(boolean z) {
                RecyclerListView recyclerListView;
                RecyclerView.Adapter adapter;
                if (z && TrendingStickersLayout.this.listView.getAdapter() != TrendingStickersLayout.this.searchAdapter) {
                    recyclerListView = TrendingStickersLayout.this.listView;
                    adapter = TrendingStickersLayout.this.searchAdapter;
                } else {
                    if (z || TrendingStickersLayout.this.listView.getAdapter() == TrendingStickersLayout.this.adapter) {
                        return;
                    }
                    recyclerListView = TrendingStickersLayout.this.listView;
                    adapter = TrendingStickersLayout.this.adapter;
                }
                recyclerListView.setAdapter(adapter);
                if (TrendingStickersLayout.this.listView.getAdapter().getItemCount() > 0) {
                    TrendingStickersLayout.this.layoutManager.scrollToPositionWithOffset(0, (-TrendingStickersLayout.this.listView.getPaddingTop()) + AndroidUtilities.dp(58.0f) + TrendingStickersLayout.this.topOffset, false);
                }
            }

            @Override
            public void setLastSearchKeyboardLanguage(String[] strArr) {
                delegate.setLastSearchKeyboardLanguage(strArr);
            }
        }, stickerSetCoveredArr, longSparseArray, longSparseArray2, resourcesProvider);
        FrameLayout frameLayout = new FrameLayout(context);
        this.searchLayout = frameLayout;
        frameLayout.setBackgroundColor(getThemedColor(Theme.key_dialogBackground));
        SearchField searchField = new SearchField(context, true, resourcesProvider) {
            @Override
            public void onTextChange(String str) {
                TrendingStickersLayout.this.searchAdapter.search(str);
            }
        };
        this.searchView = searchField;
        searchField.setHint(LocaleController.getString(R.string.SearchTrendingStickersHint));
        frameLayout.addView(searchField, LayoutHelper.createFrame(-1, -1, 48));
        RecyclerListView recyclerListView = new RecyclerListView(context) {
            @Override
            protected boolean allowSelectChildAtPosition(float f, float f2) {
                return f2 >= ((float) (TrendingStickersLayout.this.topOffset + AndroidUtilities.dp(58.0f)));
            }

            @Override
            public boolean dispatchTouchEvent(MotionEvent motionEvent) {
                TrendingStickersLayout.this.motionEventCatchedByListView = true;
                return super.dispatchTouchEvent(motionEvent);
            }

            @Override
            public boolean onInterceptTouchEvent(MotionEvent motionEvent) {
                return super.onInterceptTouchEvent(motionEvent) || delegate.onListViewInterceptTouchEvent(this, motionEvent);
            }

            @Override
            public boolean onTouchEvent(MotionEvent motionEvent) {
                if (TrendingStickersLayout.this.glueToTopAnimator != null) {
                    return false;
                }
                return super.onTouchEvent(motionEvent);
            }

            @Override
            public void requestLayout() {
                if (TrendingStickersLayout.this.ignoreLayout) {
                    return;
                }
                super.requestLayout();
            }
        };
        this.listView = recyclerListView;
        final RecyclerListView.OnItemClickListener onItemClickListener = new RecyclerListView.OnItemClickListener() {
            @Override
            public final void onItemClick(View view, int i2) {
                TrendingStickersLayout.this.lambda$new$0(view, i2);
            }
        };
        recyclerListView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public final boolean onTouch(View view, MotionEvent motionEvent) {
                boolean lambda$new$1;
                lambda$new$1 = TrendingStickersLayout.this.lambda$new$1(delegate, onItemClickListener, view, motionEvent);
                return lambda$new$1;
            }
        });
        recyclerListView.setOverScrollMode(2);
        recyclerListView.setClipToPadding(false);
        recyclerListView.setItemAnimator(null);
        recyclerListView.setLayoutAnimation(null);
        FillLastGridLayoutManager fillLastGridLayoutManager = new FillLastGridLayoutManager(context, 5, AndroidUtilities.dp(58.0f), recyclerListView) {
            @Override
            public boolean isLayoutRTL() {
                return LocaleController.isRTL;
            }

            @Override
            public int scrollVerticallyBy(int i2, RecyclerView.Recycler recycler, RecyclerView.State state) {
                int i3;
                View findViewByPosition;
                if (TrendingStickersLayout.this.scrollFromAnimator) {
                    return super.scrollVerticallyBy(i2, recycler, state);
                }
                TrendingStickersLayout trendingStickersLayout = TrendingStickersLayout.this;
                int i4 = 0;
                if (trendingStickersLayout.glueToTopAnimator != null) {
                    return 0;
                }
                if (trendingStickersLayout.gluedToTop) {
                    while (true) {
                        i3 = 1;
                        if (i4 >= getChildCount()) {
                            break;
                        }
                        int childAdapterPosition = TrendingStickersLayout.this.listView.getChildAdapterPosition(getChildAt(i4));
                        if (childAdapterPosition < 1) {
                            i3 = childAdapterPosition;
                            break;
                        }
                        i4++;
                    }
                    if (i3 == 0 && (findViewByPosition = TrendingStickersLayout.this.layoutManager.findViewByPosition(i3)) != null && findViewByPosition.getTop() - i2 > AndroidUtilities.dp(58.0f)) {
                        i2 = findViewByPosition.getTop() - AndroidUtilities.dp(58.0f);
                    }
                }
                return super.scrollVerticallyBy(i2, recycler, state);
            }

            @Override
            protected boolean shouldCalcLastItemHeight() {
                return TrendingStickersLayout.this.listView.getAdapter() == TrendingStickersLayout.this.searchAdapter;
            }

            @Override
            public boolean supportsPredictiveItemAnimations() {
                return false;
            }
        };
        this.layoutManager = fillLastGridLayoutManager;
        recyclerListView.setLayoutManager(fillLastGridLayoutManager);
        fillLastGridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int i2) {
                if (TrendingStickersLayout.this.listView.getAdapter() != TrendingStickersLayout.this.adapter) {
                    return TrendingStickersLayout.this.searchAdapter.getSpanSize(i2);
                }
                if ((TrendingStickersLayout.this.adapter.cache.get(i2) instanceof Integer) || i2 >= TrendingStickersLayout.this.adapter.totalItems) {
                    return TrendingStickersLayout.this.adapter.stickersPerRow;
                }
                return 1;
            }
        });
        recyclerListView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int i2) {
                if (TrendingStickersLayout.this.onScrollListener != null) {
                    TrendingStickersLayout.this.onScrollListener.onScrollStateChanged(recyclerView, i2);
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int i2, int i3) {
                if (TrendingStickersLayout.this.onScrollListener != null) {
                    TrendingStickersLayout.this.onScrollListener.onScrolled(TrendingStickersLayout.this.listView, i2, i3);
                }
                if (i3 <= 0 || TrendingStickersLayout.this.listView.getAdapter() != TrendingStickersLayout.this.adapter || !TrendingStickersLayout.this.loaded || TrendingStickersLayout.this.adapter.loadingMore || TrendingStickersLayout.this.adapter.endReached) {
                    return;
                }
                if (TrendingStickersLayout.this.layoutManager.findLastVisibleItemPosition() >= (TrendingStickersLayout.this.adapter.getItemCount() - ((TrendingStickersLayout.this.adapter.stickersPerRow + 1) * 10)) - 1) {
                    TrendingStickersLayout.this.adapter.loadMoreStickerSets();
                }
            }
        });
        recyclerListView.setAdapter(trendingStickersAdapter);
        recyclerListView.setOnItemClickListener(onItemClickListener);
        addView(recyclerListView, LayoutHelper.createFrame(-1, -1.0f, 51, 0.0f, 0.0f, 0.0f, 0.0f));
        View view = new View(context);
        this.shadowView = view;
        view.setBackgroundColor(getThemedColor(Theme.key_dialogShadowLine));
        view.setAlpha(0.0f);
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(-1, AndroidUtilities.getShadowHeight());
        layoutParams.topMargin = AndroidUtilities.dp(58.0f);
        addView(view, layoutParams);
        addView(frameLayout, LayoutHelper.createFrame(-1, 58, 51));
        updateColors();
        NotificationCenter notificationCenter = NotificationCenter.getInstance(i);
        notificationCenter.addObserver(this, NotificationCenter.stickersDidLoad);
        notificationCenter.addObserver(this, NotificationCenter.featuredStickersDidLoad);
    }

    private int getThemedColor(int i) {
        return Theme.getColor(i, this.resourcesProvider);
    }

    public void lambda$new$0(View view, int i) {
        RecyclerView.Adapter adapter = this.listView.getAdapter();
        StickersSearchAdapter stickersSearchAdapter = this.searchAdapter;
        TLRPC.StickerSetCovered setForPosition = adapter == stickersSearchAdapter ? stickersSearchAdapter.getSetForPosition(i) : i < this.adapter.totalItems ? (TLRPC.StickerSetCovered) this.adapter.positionsToSets.get(i) : null;
        if (setForPosition != null) {
            showStickerSet(setForPosition.set);
        }
    }

    public boolean lambda$new$1(Delegate delegate, RecyclerListView.OnItemClickListener onItemClickListener, View view, MotionEvent motionEvent) {
        return delegate.onListViewTouchEvent(this.listView, onItemClickListener, motionEvent);
    }

    private void setShadowVisible(boolean z) {
        if (this.shadowVisible != z) {
            this.shadowVisible = z;
            this.shadowView.animate().alpha(z ? 1.0f : 0.0f).setDuration(200L).start();
        }
    }

    private void showStickerSet(final TLRPC.InputStickerSet inputStickerSet) {
        StickersAlert stickersAlert = new StickersAlert(getContext(), this.parentFragment, inputStickerSet, null, this.delegate.canSendSticker() ? new StickersAlert.StickersAlertDelegate() {
            @Override
            public boolean canSchedule() {
                return TrendingStickersLayout.this.delegate.canSchedule();
            }

            @Override
            public boolean isInScheduleMode() {
                return TrendingStickersLayout.this.delegate.isInScheduleMode();
            }

            @Override
            public void lambda$onStickerSelected$68(TLRPC.Document document, String str, Object obj, MessageObject.SendAnimationData sendAnimationData, boolean z, boolean z2, int i) {
                TrendingStickersLayout.this.delegate.onStickerSelected(document, obj, z, z2, i);
            }
        } : null, this.resourcesProvider, false);
        stickersAlert.setShowTooltipWhenToggle(false);
        stickersAlert.setInstallDelegate(new StickersAlert.StickersAlertInstallDelegate() {
            @Override
            public void onStickerSetInstalled() {
                if (TrendingStickersLayout.this.listView.getAdapter() != TrendingStickersLayout.this.adapter) {
                    TrendingStickersLayout.this.searchAdapter.installStickerSet(inputStickerSet);
                    return;
                }
                for (int i = 0; i < TrendingStickersLayout.this.adapter.sets.size(); i++) {
                    TLRPC.StickerSetCovered stickerSetCovered = (TLRPC.StickerSetCovered) TrendingStickersLayout.this.adapter.sets.get(i);
                    if (stickerSetCovered.set.id == inputStickerSet.id) {
                        TrendingStickersLayout.this.adapter.installStickerSet(stickerSetCovered, null);
                        return;
                    }
                }
            }

            @Override
            public void onStickerSetUninstalled() {
            }
        });
        this.parentFragment.showDialog(stickersAlert);
    }

    private void showStickerSet(TLRPC.StickerSet stickerSet) {
        showStickerSet(stickerSet, null);
    }

    private void updateLastItemInAdapter() {
        this.listView.getAdapter().notifyItemChanged(r0.getItemCount() - 1);
    }

    private void updateVisibleTrendingSets() {
        RecyclerView.Adapter adapter = this.listView.getAdapter();
        if (adapter != null) {
            adapter.notifyItemRangeChanged(0, adapter.getItemCount(), 0);
        }
    }

    @Override
    public void didReceivedNotification(int r4, int r5, java.lang.Object... r6) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.TrendingStickersLayout.didReceivedNotification(int, int, java.lang.Object[]):void");
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        int i;
        int i2;
        float f = this.highlightProgress;
        if (f != 0.0f && this.scrollToSet != null) {
            float f2 = f - 0.0053333333f;
            this.highlightProgress = f2;
            if (f2 < 0.0f) {
                this.highlightProgress = 0.0f;
            } else {
                invalidate();
            }
            Integer num = (Integer) this.adapter.setsToPosition.get(this.scrollToSet);
            if (num != null) {
                View findViewByPosition = this.layoutManager.findViewByPosition(num.intValue());
                if (findViewByPosition != null) {
                    i = (int) findViewByPosition.getY();
                    i2 = ((int) findViewByPosition.getY()) + findViewByPosition.getMeasuredHeight();
                } else {
                    i = -1;
                    i2 = -1;
                }
                View findViewByPosition2 = this.layoutManager.findViewByPosition(num.intValue() + 1);
                if (findViewByPosition2 != null) {
                    if (findViewByPosition == null) {
                        i = (int) findViewByPosition2.getY();
                    }
                    i2 = ((int) findViewByPosition2.getY()) + findViewByPosition2.getMeasuredHeight();
                }
                if (findViewByPosition != null || findViewByPosition2 != null) {
                    this.paint.setColor(Theme.getColor(Theme.key_featuredStickers_addButton));
                    float f3 = this.highlightProgress;
                    this.paint.setAlpha((int) ((f3 < 0.06f ? f3 / 0.06f : 1.0f) * 25.5f));
                    canvas.drawRect(0.0f, i, getMeasuredWidth(), i2, this.paint);
                }
            }
        }
        super.dispatchDraw(canvas);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent motionEvent) {
        this.motionEventCatchedByListView = false;
        boolean dispatchTouchEvent = super.dispatchTouchEvent(motionEvent);
        if (!this.motionEventCatchedByListView) {
            MotionEvent obtain = MotionEvent.obtain(motionEvent);
            this.listView.dispatchTouchEvent(obtain);
            obtain.recycle();
        }
        return dispatchTouchEvent;
    }

    public int getContentTopOffset() {
        return this.topOffset;
    }

    public void getThemeDescriptions(List list, ThemeDescription.ThemeDescriptionDelegate themeDescriptionDelegate) {
        this.searchView.getThemeDescriptions(list);
        this.adapter.getThemeDescriptions(list, this.listView, themeDescriptionDelegate);
        this.searchAdapter.getThemeDescriptions(list, this.listView, themeDescriptionDelegate);
        list.add(new ThemeDescription(this.shadowView, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, Theme.key_dialogShadowLine));
        list.add(new ThemeDescription(this.searchLayout, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, Theme.key_dialogBackground));
    }

    public void glueToTop(boolean z) {
        this.gluedToTop = z;
        if (!z) {
            ValueAnimator valueAnimator = this.glueToTopAnimator;
            if (valueAnimator != null) {
                valueAnimator.removeAllListeners();
                this.glueToTopAnimator.cancel();
                this.glueToTopAnimator = null;
                return;
            }
            return;
        }
        if (getContentTopOffset() <= 0 || this.glueToTopAnimator != null) {
            return;
        }
        final int contentTopOffset = getContentTopOffset();
        ValueAnimator ofFloat = ValueAnimator.ofFloat(0.0f, 1.0f);
        this.glueToTopAnimator = ofFloat;
        ofFloat.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            int dy = 0;

            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator2) {
                int floatValue = (int) (contentTopOffset * ((Float) valueAnimator2.getAnimatedValue()).floatValue());
                TrendingStickersLayout.this.scrollFromAnimator = true;
                TrendingStickersLayout.this.listView.scrollBy(0, floatValue - this.dy);
                TrendingStickersLayout.this.scrollFromAnimator = false;
                this.dy = floatValue;
            }
        });
        this.glueToTopAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animator) {
                TrendingStickersLayout.this.glueToTopAnimator = null;
            }
        });
        this.glueToTopAnimator.setDuration(250L);
        this.glueToTopAnimator.setInterpolator(AdjustPanLayoutHelper.keyboardInterpolator);
        this.glueToTopAnimator.start();
    }

    @Override
    protected void onConfigurationChanged(Configuration configuration) {
        super.onConfigurationChanged(configuration);
        updateLastItemInAdapter();
        this.wasLayout = false;
    }

    @Override
    protected void onLayout(boolean z, int i, int i2, int i3, int i4) {
        Integer num;
        super.onLayout(z, i, i2, i3, i4);
        if (this.wasLayout) {
            return;
        }
        this.wasLayout = true;
        this.adapter.refreshStickerSets();
        if (this.scrollToSet == null || (num = (Integer) this.adapter.setsToPosition.get(this.scrollToSet)) == null) {
            return;
        }
        this.layoutManager.scrollToPositionWithOffset(num.intValue(), (-this.listView.getPaddingTop()) + AndroidUtilities.dp(58.0f));
    }

    public void recycle() {
        NotificationCenter notificationCenter = NotificationCenter.getInstance(this.currentAccount);
        notificationCenter.removeObserver(this, NotificationCenter.stickersDidLoad);
        notificationCenter.removeObserver(this, NotificationCenter.featuredStickersDidLoad);
    }

    public void setContentViewPaddingTop(int i) {
        int dp = i + AndroidUtilities.dp(58.0f);
        if (this.listView.getPaddingTop() != dp) {
            this.ignoreLayout = true;
            this.listView.setPadding(0, dp, 0, 0);
            this.ignoreLayout = false;
        }
    }

    public void setOnScrollListener(RecyclerView.OnScrollListener onScrollListener) {
        this.onScrollListener = onScrollListener;
    }

    public void setParentFragment(BaseFragment baseFragment) {
        this.parentFragment = baseFragment;
    }

    public void showStickerSet(TLRPC.StickerSet stickerSet, TLRPC.InputStickerSet inputStickerSet) {
        if (stickerSet != null) {
            inputStickerSet = new TLRPC.TL_inputStickerSetID();
            inputStickerSet.access_hash = stickerSet.access_hash;
            inputStickerSet.id = stickerSet.id;
        }
        if (inputStickerSet != null) {
            showStickerSet(inputStickerSet);
        }
    }

    public boolean update() {
        if (this.listView.getChildCount() <= 0) {
            int paddingTop = this.listView.getPaddingTop();
            this.topOffset = paddingTop;
            this.listView.setTopGlowOffset(paddingTop);
            this.searchLayout.setTranslationY(this.topOffset);
            this.shadowView.setTranslationY(this.topOffset);
            setShadowVisible(false);
            return true;
        }
        View childAt = this.listView.getChildAt(0);
        for (int i = 1; i < this.listView.getChildCount(); i++) {
            View childAt2 = this.listView.getChildAt(i);
            if (childAt2.getTop() < childAt.getTop()) {
                childAt = childAt2;
            }
        }
        RecyclerListView.Holder holder = (RecyclerListView.Holder) this.listView.findContainingViewHolder(childAt);
        int top = childAt.getTop() - AndroidUtilities.dp(58.0f);
        int i2 = (top <= 0 || holder == null || holder.getAdapterPosition() != 0) ? 0 : top;
        setShadowVisible(top < 0);
        if (this.topOffset == i2) {
            return false;
        }
        this.topOffset = i2;
        this.listView.setTopGlowOffset(i2 + AndroidUtilities.dp(58.0f));
        this.searchLayout.setTranslationY(this.topOffset);
        this.shadowView.setTranslationY(this.topOffset);
        return true;
    }

    public void updateColors() {
        RecyclerView.Adapter adapter = this.listView.getAdapter();
        TrendingStickersAdapter trendingStickersAdapter = this.adapter;
        if (adapter == trendingStickersAdapter) {
            trendingStickersAdapter.updateColors(this.listView);
        } else {
            this.searchAdapter.updateColors(this.listView);
        }
    }
}
