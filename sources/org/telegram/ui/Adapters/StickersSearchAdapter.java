package org.telegram.ui.Adapters;

import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.text.TextUtils;
import android.util.LongSparseArray;
import android.util.SparseArray;
import android.util.SparseIntArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Space;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MediaDataController;
import org.telegram.messenger.R;
import org.telegram.messenger.UserConfig;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ActionBar.ThemeDescription;
import org.telegram.ui.Adapters.StickersSearchAdapter;
import org.telegram.ui.Cells.EmptyCell;
import org.telegram.ui.Cells.FeaturedStickerSetInfoCell;
import org.telegram.ui.Cells.StickerEmojiCell;
import org.telegram.ui.Cells.StickerSetNameCell;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.RecyclerListView;

public class StickersSearchAdapter extends RecyclerListView.SelectionAdapter {
    boolean cleared;
    private final Context context;
    private final Delegate delegate;
    private int emojiSearchId;
    private ImageView emptyImageView;
    private TextView emptyTextView;
    private final LongSparseArray installingStickerSets;
    private final TLRPC.StickerSetCovered[] primaryInstallingStickerSets;
    private final LongSparseArray removingStickerSets;
    private int reqId;
    private int reqId2;
    private final Theme.ResourcesProvider resourcesProvider;
    private String searchQuery;
    private int totalItems;
    private final int currentAccount = UserConfig.selectedAccount;
    private SparseArray rowStartPack = new SparseArray();
    private SparseArray cache = new SparseArray();
    private SparseArray cacheParent = new SparseArray();
    private SparseIntArray positionToRow = new SparseIntArray();
    private SparseArray positionToEmoji = new SparseArray();
    private ArrayList serverPacks = new ArrayList();
    private ArrayList localPacks = new ArrayList();
    private HashMap localPacksByShortName = new HashMap();
    private HashMap localPacksByName = new HashMap();
    private HashMap emojiStickers = new HashMap();
    private ArrayList emojiArrays = new ArrayList();
    private SparseArray positionsToSets = new SparseArray();
    private Runnable searchRunnable = new AnonymousClass1();

    public class AnonymousClass1 implements Runnable {
        AnonymousClass1() {
        }

        private void clear() {
            StickersSearchAdapter stickersSearchAdapter = StickersSearchAdapter.this;
            if (stickersSearchAdapter.cleared) {
                return;
            }
            stickersSearchAdapter.cleared = true;
            stickersSearchAdapter.emojiStickers.clear();
            StickersSearchAdapter.this.emojiArrays.clear();
            StickersSearchAdapter.this.localPacks.clear();
            StickersSearchAdapter.this.serverPacks.clear();
            StickersSearchAdapter.this.localPacksByShortName.clear();
            StickersSearchAdapter.this.localPacksByName.clear();
        }

        public void lambda$run$0(int i, HashMap hashMap, ArrayList arrayList, String str) {
            if (i != StickersSearchAdapter.this.emojiSearchId) {
                return;
            }
            int size = arrayList.size();
            boolean z = false;
            for (int i2 = 0; i2 < size; i2++) {
                String str2 = ((MediaDataController.KeywordResult) arrayList.get(i2)).emoji;
                ArrayList arrayList2 = hashMap != null ? (ArrayList) hashMap.get(str2) : null;
                if (arrayList2 != null && !arrayList2.isEmpty()) {
                    clear();
                    if (!StickersSearchAdapter.this.emojiStickers.containsKey(arrayList2)) {
                        StickersSearchAdapter.this.emojiStickers.put(arrayList2, str2);
                        StickersSearchAdapter.this.emojiArrays.add(arrayList2);
                        z = true;
                    }
                }
            }
            if (z) {
                StickersSearchAdapter.this.notifyDataSetChanged();
            }
        }

        public void lambda$run$1(TLRPC.TL_messages_searchStickerSets tL_messages_searchStickerSets, TLObject tLObject) {
            if (tL_messages_searchStickerSets.q.equals(StickersSearchAdapter.this.searchQuery)) {
                clear();
                StickersSearchAdapter.this.delegate.onSearchStop();
                StickersSearchAdapter.this.reqId = 0;
                StickersSearchAdapter.this.delegate.setAdapterVisible(true);
                StickersSearchAdapter.this.serverPacks.addAll(((TLRPC.TL_messages_foundStickerSets) tLObject).sets);
                StickersSearchAdapter.this.notifyDataSetChanged();
            }
        }

        public void lambda$run$2(final TLRPC.TL_messages_searchStickerSets tL_messages_searchStickerSets, final TLObject tLObject, TLRPC.TL_error tL_error) {
            if (tLObject instanceof TLRPC.TL_messages_foundStickerSets) {
                AndroidUtilities.runOnUIThread(new Runnable() {
                    @Override
                    public final void run() {
                        StickersSearchAdapter.AnonymousClass1.this.lambda$run$1(tL_messages_searchStickerSets, tLObject);
                    }
                });
            }
        }

        public void lambda$run$3(TLRPC.TL_messages_getStickers tL_messages_getStickers, TLObject tLObject, ArrayList arrayList, LongSparseArray longSparseArray) {
            if (tL_messages_getStickers.emoticon.equals(StickersSearchAdapter.this.searchQuery)) {
                StickersSearchAdapter.this.reqId2 = 0;
                if (tLObject instanceof TLRPC.TL_messages_stickers) {
                    TLRPC.TL_messages_stickers tL_messages_stickers = (TLRPC.TL_messages_stickers) tLObject;
                    int size = arrayList.size();
                    int size2 = tL_messages_stickers.stickers.size();
                    for (int i = 0; i < size2; i++) {
                        TLRPC.Document document = tL_messages_stickers.stickers.get(i);
                        if (longSparseArray.indexOfKey(document.id) < 0) {
                            arrayList.add(document);
                        }
                    }
                    if (size != arrayList.size()) {
                        StickersSearchAdapter.this.emojiStickers.put(arrayList, StickersSearchAdapter.this.searchQuery);
                        if (size == 0) {
                            StickersSearchAdapter.this.emojiArrays.add(arrayList);
                        }
                        StickersSearchAdapter.this.notifyDataSetChanged();
                    }
                }
            }
        }

        public void lambda$run$4(final TLRPC.TL_messages_getStickers tL_messages_getStickers, final ArrayList arrayList, final LongSparseArray longSparseArray, final TLObject tLObject, TLRPC.TL_error tL_error) {
            AndroidUtilities.runOnUIThread(new Runnable() {
                @Override
                public final void run() {
                    StickersSearchAdapter.AnonymousClass1.this.lambda$run$3(tL_messages_getStickers, tLObject, arrayList, longSparseArray);
                }
            });
        }

        @Override
        public void run() {
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Adapters.StickersSearchAdapter.AnonymousClass1.run():void");
        }
    }

    public interface Delegate {
        String[] getLastSearchKeyboardLanguage();

        int getStickersPerRow();

        void onSearchStart();

        void onSearchStop();

        void onStickerSetAdd(TLRPC.StickerSetCovered stickerSetCovered, boolean z);

        void onStickerSetRemove(TLRPC.StickerSetCovered stickerSetCovered);

        void setAdapterVisible(boolean z);

        void setLastSearchKeyboardLanguage(String[] strArr);
    }

    public StickersSearchAdapter(Context context, Delegate delegate, TLRPC.StickerSetCovered[] stickerSetCoveredArr, LongSparseArray longSparseArray, LongSparseArray longSparseArray2, Theme.ResourcesProvider resourcesProvider) {
        this.context = context;
        this.delegate = delegate;
        this.primaryInstallingStickerSets = stickerSetCoveredArr;
        this.installingStickerSets = longSparseArray;
        this.removingStickerSets = longSparseArray2;
        this.resourcesProvider = resourcesProvider;
    }

    static int access$804(StickersSearchAdapter stickersSearchAdapter) {
        int i = stickersSearchAdapter.emojiSearchId + 1;
        stickersSearchAdapter.emojiSearchId = i;
        return i;
    }

    private void bindFeaturedStickerSetInfoCell(FeaturedStickerSetInfoCell featuredStickerSetInfoCell, int i, boolean z) {
        boolean z2;
        boolean z3;
        boolean z4;
        MediaDataController mediaDataController = MediaDataController.getInstance(this.currentAccount);
        ArrayList<Long> unreadStickerSets = mediaDataController.getUnreadStickerSets();
        TLRPC.StickerSetCovered stickerSetCovered = (TLRPC.StickerSetCovered) this.cache.get(i);
        boolean z5 = unreadStickerSets != null && unreadStickerSets.contains(Long.valueOf(stickerSetCovered.set.id));
        int i2 = 0;
        while (true) {
            TLRPC.StickerSetCovered[] stickerSetCoveredArr = this.primaryInstallingStickerSets;
            if (i2 >= stickerSetCoveredArr.length) {
                z2 = false;
                break;
            }
            if (stickerSetCoveredArr[i2] != null) {
                TLRPC.TL_messages_stickerSet stickerSetById = MediaDataController.getInstance(this.currentAccount).getStickerSetById(this.primaryInstallingStickerSets[i2].set.id);
                if (stickerSetById != null && !stickerSetById.set.archived) {
                    this.primaryInstallingStickerSets[i2] = null;
                } else if (this.primaryInstallingStickerSets[i2].set.id == stickerSetCovered.set.id) {
                    z2 = true;
                    break;
                }
            }
            i2++;
        }
        int indexOfIgnoreCase = TextUtils.isEmpty(this.searchQuery) ? -1 : AndroidUtilities.indexOfIgnoreCase(stickerSetCovered.set.title, this.searchQuery);
        if (indexOfIgnoreCase >= 0) {
            featuredStickerSetInfoCell.setStickerSet(stickerSetCovered, z5, z, indexOfIgnoreCase, this.searchQuery.length(), z2);
        } else {
            featuredStickerSetInfoCell.setStickerSet(stickerSetCovered, z5, z, 0, 0, z2);
            if (!TextUtils.isEmpty(this.searchQuery) && AndroidUtilities.indexOfIgnoreCase(stickerSetCovered.set.short_name, this.searchQuery) == 0) {
                featuredStickerSetInfoCell.setUrl(stickerSetCovered.set.short_name, this.searchQuery.length());
            }
        }
        if (z5) {
            mediaDataController.markFeaturedStickersByIdAsRead(false, stickerSetCovered.set.id);
        }
        boolean z6 = this.installingStickerSets.indexOfKey(stickerSetCovered.set.id) >= 0;
        boolean z7 = this.removingStickerSets.indexOfKey(stickerSetCovered.set.id) >= 0;
        if (z6 || z7) {
            if (z6 && featuredStickerSetInfoCell.isInstalled()) {
                this.installingStickerSets.remove(stickerSetCovered.set.id);
                z6 = false;
            } else if (z7 && !featuredStickerSetInfoCell.isInstalled()) {
                this.removingStickerSets.remove(stickerSetCovered.set.id);
            }
        }
        if (z2 || !z6) {
            z3 = z;
            z4 = false;
        } else {
            z3 = z;
            z4 = true;
        }
        featuredStickerSetInfoCell.setAddDrawProgress(z4, z3);
        mediaDataController.preloadStickerSetThumb(stickerSetCovered);
        featuredStickerSetInfoCell.setNeedDivider(i > 0);
    }

    private int getThemedColor(int i) {
        return Theme.getColor(i, this.resourcesProvider);
    }

    public void lambda$onCreateViewHolder$0(View view) {
        FeaturedStickerSetInfoCell featuredStickerSetInfoCell = (FeaturedStickerSetInfoCell) view.getParent();
        TLRPC.StickerSetCovered stickerSet = featuredStickerSetInfoCell.getStickerSet();
        if (stickerSet == null || this.installingStickerSets.indexOfKey(stickerSet.set.id) >= 0 || this.removingStickerSets.indexOfKey(stickerSet.set.id) >= 0) {
            return;
        }
        if (!featuredStickerSetInfoCell.isInstalled()) {
            installStickerSet(stickerSet, featuredStickerSetInfoCell);
        } else {
            this.removingStickerSets.put(stickerSet.set.id, stickerSet);
            this.delegate.onStickerSetRemove(featuredStickerSetInfoCell.getStickerSet());
        }
    }

    @Override
    public int getItemCount() {
        return Math.max(1, this.totalItems + 1);
    }

    @Override
    public int getItemViewType(int i) {
        if (i == 0 && this.totalItems == 0) {
            return 5;
        }
        if (i == getItemCount() - 1) {
            return 4;
        }
        Object obj = this.cache.get(i);
        if (obj == null) {
            return 1;
        }
        if (obj instanceof TLRPC.Document) {
            return 0;
        }
        return obj instanceof TLRPC.StickerSetCovered ? 3 : 2;
    }

    public TLRPC.StickerSetCovered getSetForPosition(int i) {
        return (TLRPC.StickerSetCovered) this.positionsToSets.get(i);
    }

    public int getSpanSize(int i) {
        if (i == this.totalItems || !(this.cache.get(i) == null || (this.cache.get(i) instanceof TLRPC.Document))) {
            return this.delegate.getStickersPerRow();
        }
        return 1;
    }

    public void getThemeDescriptions(List list, RecyclerListView recyclerListView, ThemeDescription.ThemeDescriptionDelegate themeDescriptionDelegate) {
        FeaturedStickerSetInfoCell.createThemeDescriptions(list, recyclerListView, themeDescriptionDelegate);
        StickerSetNameCell.createThemeDescriptions(list, recyclerListView, themeDescriptionDelegate);
        ImageView imageView = this.emptyImageView;
        int i = ThemeDescription.FLAG_IMAGECOLOR;
        int i2 = Theme.key_chat_emojiPanelEmptyText;
        list.add(new ThemeDescription(imageView, i, null, null, null, null, i2));
        list.add(new ThemeDescription(this.emptyTextView, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, i2));
    }

    public void installStickerSet(TLRPC.InputStickerSet inputStickerSet) {
        for (int i = 0; i < this.serverPacks.size(); i++) {
            TLRPC.StickerSetCovered stickerSetCovered = (TLRPC.StickerSetCovered) this.serverPacks.get(i);
            if (stickerSetCovered.set.id == inputStickerSet.id) {
                installStickerSet(stickerSetCovered, null);
                return;
            }
        }
    }

    public void installStickerSet(org.telegram.tgnet.TLRPC.StickerSetCovered r8, org.telegram.ui.Cells.FeaturedStickerSetInfoCell r9) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Adapters.StickersSearchAdapter.installStickerSet(org.telegram.tgnet.TLRPC$StickerSetCovered, org.telegram.ui.Cells.FeaturedStickerSetInfoCell):void");
    }

    @Override
    public boolean isEnabled(RecyclerView.ViewHolder viewHolder) {
        return false;
    }

    @Override
    public void notifyDataSetChanged() {
        int i;
        ArrayList<TLRPC.Document> arrayList;
        TLRPC.StickerSetCovered stickerSetCovered;
        this.rowStartPack.clear();
        this.positionToRow.clear();
        this.cache.clear();
        this.positionsToSets.clear();
        this.positionToEmoji.clear();
        this.totalItems = 0;
        int size = this.serverPacks.size();
        int size2 = this.localPacks.size();
        int i2 = !this.emojiArrays.isEmpty() ? 1 : 0;
        int i3 = 0;
        int i4 = 0;
        while (i3 < size + size2 + i2) {
            if (i3 < size2) {
                ?? r7 = (TLRPC.TL_messages_stickerSet) this.localPacks.get(i3);
                arrayList = r7.documents;
                i = size;
                stickerSetCovered = r7;
            } else {
                int i5 = i3 - size2;
                if (i5 < i2) {
                    int size3 = this.emojiArrays.size();
                    String str = "";
                    int i6 = 0;
                    for (int i7 = 0; i7 < size3; i7++) {
                        ArrayList arrayList2 = (ArrayList) this.emojiArrays.get(i7);
                        String str2 = (String) this.emojiStickers.get(arrayList2);
                        if (str2 != null && !str.equals(str2)) {
                            this.positionToEmoji.put(this.totalItems + i6, str2);
                            str = str2;
                        }
                        int size4 = arrayList2.size();
                        int i8 = 0;
                        while (i8 < size4) {
                            int i9 = this.totalItems + i6;
                            int stickersPerRow = (i6 / this.delegate.getStickersPerRow()) + i4;
                            TLRPC.Document document = (TLRPC.Document) arrayList2.get(i8);
                            int i10 = size;
                            this.cache.put(i9, document);
                            int i11 = size3;
                            String str3 = str;
                            TLRPC.TL_messages_stickerSet stickerSetById = MediaDataController.getInstance(this.currentAccount).getStickerSetById(MediaDataController.getStickerSetId(document));
                            if (stickerSetById != null) {
                                this.cacheParent.put(i9, stickerSetById);
                            }
                            this.positionToRow.put(i9, stickersPerRow);
                            i6++;
                            i8++;
                            size = i10;
                            size3 = i11;
                            str = str3;
                        }
                    }
                    i = size;
                    int ceil = (int) Math.ceil(i6 / this.delegate.getStickersPerRow());
                    for (int i12 = 0; i12 < ceil; i12++) {
                        this.rowStartPack.put(i4 + i12, Integer.valueOf(i6));
                    }
                    this.totalItems += this.delegate.getStickersPerRow() * ceil;
                    i4 += ceil;
                    i3++;
                    size = i;
                } else {
                    i = size;
                    TLRPC.StickerSetCovered stickerSetCovered2 = (TLRPC.StickerSetCovered) this.serverPacks.get(i5 - i2);
                    arrayList = stickerSetCovered2.covers;
                    stickerSetCovered = stickerSetCovered2;
                }
            }
            if (!arrayList.isEmpty()) {
                int ceil2 = (int) Math.ceil(arrayList.size() / this.delegate.getStickersPerRow());
                this.cache.put(this.totalItems, stickerSetCovered);
                if (i3 >= size2 && (stickerSetCovered instanceof TLRPC.StickerSetCovered)) {
                    this.positionsToSets.put(this.totalItems, stickerSetCovered);
                }
                this.positionToRow.put(this.totalItems, i4);
                int size5 = arrayList.size();
                int i13 = 0;
                while (i13 < size5) {
                    int i14 = i13 + 1;
                    int i15 = this.totalItems + i14;
                    int stickersPerRow2 = i4 + 1 + (i13 / this.delegate.getStickersPerRow());
                    this.cache.put(i15, arrayList.get(i13));
                    this.cacheParent.put(i15, stickerSetCovered);
                    this.positionToRow.put(i15, stickersPerRow2);
                    if (i3 >= size2 && (stickerSetCovered instanceof TLRPC.StickerSetCovered)) {
                        this.positionsToSets.put(i15, stickerSetCovered);
                    }
                    i13 = i14;
                }
                int i16 = ceil2 + 1;
                for (int i17 = 0; i17 < i16; i17++) {
                    this.rowStartPack.put(i4 + i17, stickerSetCovered);
                }
                this.totalItems += (ceil2 * this.delegate.getStickersPerRow()) + 1;
                i4 += i16;
            }
            i3++;
            size = i;
        }
        super.notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
        int itemViewType = viewHolder.getItemViewType();
        if (itemViewType == 0) {
            ((StickerEmojiCell) viewHolder.itemView).setSticker((TLRPC.Document) this.cache.get(i), null, this.cacheParent.get(i), (String) this.positionToEmoji.get(i), false);
            return;
        }
        if (itemViewType == 1) {
            ((EmptyCell) viewHolder.itemView).setHeight(0);
            return;
        }
        if (itemViewType != 2) {
            if (itemViewType != 3) {
                return;
            }
            bindFeaturedStickerSetInfoCell((FeaturedStickerSetInfoCell) viewHolder.itemView, i, false);
            return;
        }
        StickerSetNameCell stickerSetNameCell = (StickerSetNameCell) viewHolder.itemView;
        Object obj = this.cache.get(i);
        if (obj instanceof TLRPC.TL_messages_stickerSet) {
            TLRPC.TL_messages_stickerSet tL_messages_stickerSet = (TLRPC.TL_messages_stickerSet) obj;
            if (!TextUtils.isEmpty(this.searchQuery) && this.localPacksByShortName.containsKey(tL_messages_stickerSet)) {
                TLRPC.StickerSet stickerSet = tL_messages_stickerSet.set;
                if (stickerSet != null) {
                    stickerSetNameCell.setText(stickerSet.title, 0);
                }
                stickerSetNameCell.setUrl(tL_messages_stickerSet.set.short_name, this.searchQuery.length());
                return;
            }
            Integer num = (Integer) this.localPacksByName.get(tL_messages_stickerSet);
            TLRPC.StickerSet stickerSet2 = tL_messages_stickerSet.set;
            if (stickerSet2 != null && num != null) {
                stickerSetNameCell.setText(stickerSet2.title, 0, num.intValue(), !TextUtils.isEmpty(this.searchQuery) ? this.searchQuery.length() : 0);
            }
            stickerSetNameCell.setUrl(null, 0);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i, List list) {
        if (list.contains(0) && viewHolder.getItemViewType() == 3) {
            bindFeaturedStickerSetInfoCell((FeaturedStickerSetInfoCell) viewHolder.itemView, i, true);
        } else {
            super.onBindViewHolder(viewHolder, i, list);
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        FrameLayout frameLayout;
        FrameLayout frameLayout2;
        boolean z = false;
        if (i != 0) {
            if (i == 1) {
                frameLayout2 = new EmptyCell(this.context);
            } else if (i == 2) {
                frameLayout = new StickerSetNameCell(this.context, false, true, this.resourcesProvider);
            } else if (i == 3) {
                FeaturedStickerSetInfoCell featuredStickerSetInfoCell = new FeaturedStickerSetInfoCell(this.context, 17, true, true, this.resourcesProvider);
                featuredStickerSetInfoCell.setAddOnClickListener(new View.OnClickListener() {
                    @Override
                    public final void onClick(View view) {
                        StickersSearchAdapter.this.lambda$onCreateViewHolder$0(view);
                    }
                });
                frameLayout2 = featuredStickerSetInfoCell;
            } else if (i == 4) {
                frameLayout2 = new View(this.context);
            } else if (i != 5) {
                frameLayout2 = null;
            } else {
                ?? linearLayout = new LinearLayout(this.context);
                linearLayout.setOrientation(1);
                linearLayout.setGravity(17);
                ImageView imageView = new ImageView(this.context);
                this.emptyImageView = imageView;
                imageView.setScaleType(ImageView.ScaleType.CENTER);
                this.emptyImageView.setImageResource(R.drawable.stickers_empty);
                ImageView imageView2 = this.emptyImageView;
                int i2 = Theme.key_chat_emojiPanelEmptyText;
                imageView2.setColorFilter(new PorterDuffColorFilter(getThemedColor(i2), PorterDuff.Mode.MULTIPLY));
                linearLayout.addView(this.emptyImageView, LayoutHelper.createLinear(-2, -2));
                linearLayout.addView(new Space(this.context), LayoutHelper.createLinear(-1, 15));
                TextView textView = new TextView(this.context);
                this.emptyTextView = textView;
                textView.setText(LocaleController.getString(R.string.NoStickersFound));
                this.emptyTextView.setTextSize(1, 16.0f);
                this.emptyTextView.setTextColor(getThemedColor(i2));
                linearLayout.addView(this.emptyTextView, LayoutHelper.createLinear(-2, -2));
                linearLayout.setMinimumHeight(AndroidUtilities.dp(112.0f));
                linearLayout.setLayoutParams(LayoutHelper.createFrame(-1, -1.0f));
                frameLayout2 = linearLayout;
            }
            return new RecyclerListView.Holder(frameLayout2);
        }
        StickerEmojiCell stickerEmojiCell = new StickerEmojiCell(this.context, z, this.resourcesProvider) {
            @Override
            public void onMeasure(int i3, int i4) {
                super.onMeasure(i3, View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(82.0f), 1073741824));
            }
        };
        stickerEmojiCell.getImageView().setLayerNum(3);
        frameLayout = stickerEmojiCell;
        frameLayout2 = frameLayout;
        return new RecyclerListView.Holder(frameLayout2);
    }

    public void search(String str) {
        if (this.reqId != 0) {
            ConnectionsManager.getInstance(this.currentAccount).cancelRequest(this.reqId, true);
            this.reqId = 0;
        }
        if (this.reqId2 != 0) {
            ConnectionsManager.getInstance(this.currentAccount).cancelRequest(this.reqId2, true);
            this.reqId2 = 0;
        }
        if (TextUtils.isEmpty(str)) {
            this.searchQuery = null;
            this.localPacks.clear();
            this.emojiStickers.clear();
            this.serverPacks.clear();
            this.delegate.setAdapterVisible(false);
            notifyDataSetChanged();
        } else {
            this.searchQuery = str.toLowerCase();
        }
        AndroidUtilities.cancelRunOnUIThread(this.searchRunnable);
        AndroidUtilities.runOnUIThread(this.searchRunnable, 300L);
    }

    public void updateColors(RecyclerListView recyclerListView) {
        int childCount = recyclerListView.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View childAt = recyclerListView.getChildAt(i);
            if (childAt instanceof FeaturedStickerSetInfoCell) {
                ((FeaturedStickerSetInfoCell) childAt).updateColors();
            } else if (childAt instanceof StickerSetNameCell) {
                ((StickerSetNameCell) childAt).updateColors();
            }
        }
    }
}
