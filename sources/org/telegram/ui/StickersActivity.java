package org.telegram.ui;

import android.app.Activity;
import android.app.Dialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.text.SpannableStringBuilder;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.collection.LongSparseArray;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MediaDataController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.R;
import org.telegram.messenger.SharedConfig;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.ActionBarMenu;
import org.telegram.ui.ActionBar.ActionBarMenuItem;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.BackDrawable;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ActionBar.ThemeDescription;
import org.telegram.ui.Cells.FeaturedStickerSetCell2;
import org.telegram.ui.Cells.HeaderCell;
import org.telegram.ui.Cells.RadioColorCell;
import org.telegram.ui.Cells.ShadowSectionCell;
import org.telegram.ui.Cells.StickerSetCell;
import org.telegram.ui.Cells.TextCell;
import org.telegram.ui.Cells.TextCheckCell;
import org.telegram.ui.Cells.TextInfoPrivacyCell;
import org.telegram.ui.Cells.TextSettingsCell;
import org.telegram.ui.Components.Bulletin;
import org.telegram.ui.Components.BulletinFactory;
import org.telegram.ui.Components.CubicBezierInterpolator;
import org.telegram.ui.Components.EmojiPacksAlert;
import org.telegram.ui.Components.ItemOptions;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.NumberTextView;
import org.telegram.ui.Components.Premium.PremiumFeatureBottomSheet;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.Components.ReorderingBulletinLayout;
import org.telegram.ui.Components.ShareAlert;
import org.telegram.ui.Components.StickersAlert;
import org.telegram.ui.Components.TrendingStickersAlert;
import org.telegram.ui.Components.TrendingStickersLayout;
import org.telegram.ui.Components.URLSpanNoUnderline;
import org.telegram.ui.StickersActivity;

public class StickersActivity extends BaseFragment implements NotificationCenter.NotificationCenterDelegate {
    private int activeReorderingRequests;
    private ActionBarMenuItem archiveMenuItem;
    private int archivedInfoRow;
    private int archivedRow;
    private int currentType;
    private ActionBarMenuItem deleteMenuItem;
    private int dynamicPackOrder;
    private int dynamicPackOrderInfo;
    private int emojiPacksRow;
    private int featuredRow;
    private int featuredStickersEndRow;
    private int featuredStickersHeaderRow;
    private int featuredStickersShadowRow;
    private int featuredStickersShowMoreRow;
    private int featuredStickersStartRow;
    ArrayList frozenEmojiPacks;
    private boolean isListeningForFeaturedUpdate;
    private ItemTouchHelper itemTouchHelper;
    private int largeEmojiRow;
    private LinearLayoutManager layoutManager;
    private ListAdapter listAdapter;
    private RecyclerListView listView;
    private int loopInfoRow;
    private int loopRow;
    private int masksInfoRow;
    private int masksRow;
    private boolean needReorder;
    private int reactionsDoubleTapRow;
    private int rowCount;
    private NumberTextView selectedCountTextView;
    private ActionBarMenuItem shareMenuItem;
    private int stickersBotInfo;
    private int stickersEndRow;
    private int stickersHeaderRow;
    private int stickersSettingsRow;
    private int stickersShadowRow;
    private int stickersStartRow;
    private int suggestAnimatedEmojiInfoRow;
    private int suggestAnimatedEmojiRow;
    private int suggestRow;
    private TrendingStickersAlert trendingStickersAlert;
    private boolean updateSuggestStickers;

    public class ListAdapter extends RecyclerListView.SelectionAdapter {
        private Context mContext;
        private final LongSparseArray selectedItems = new LongSparseArray();
        private final List stickerSets = new ArrayList();
        private final List featuredStickerSets = new ArrayList();
        private final List loadingFeaturedStickerSets = new ArrayList();

        public ListAdapter(Context context, List list, List list2) {
            this.mContext = context;
            setStickerSets(list);
            if (list2.size() > 3) {
                setFeaturedStickerSets(list2.subList(0, 3));
            } else {
                setFeaturedStickerSets(list2);
            }
        }

        private CharSequence addStickersBotSpan(String str) {
            String str2 = "@stickers";
            int indexOf = str.indexOf("@stickers");
            if (indexOf != -1) {
                try {
                    SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(str);
                    spannableStringBuilder.setSpan(new URLSpanNoUnderline(str2) {
                        @Override
                        public void onClick(View view) {
                            MessagesController.getInstance(((BaseFragment) StickersActivity.this).currentAccount).openByUserName("stickers", StickersActivity.this, 3);
                        }
                    }, indexOf, indexOf + 9, 18);
                    return spannableStringBuilder;
                } catch (Exception e) {
                    FileLog.e(e);
                }
            }
            return str;
        }

        private void checkActionMode() {
            int selectedCount = StickersActivity.this.listAdapter.getSelectedCount();
            boolean isActionModeShowed = ((BaseFragment) StickersActivity.this).actionBar.isActionModeShowed();
            if (selectedCount <= 0) {
                if (isActionModeShowed) {
                    ((BaseFragment) StickersActivity.this).actionBar.hideActionMode();
                    notifyStickersItemsChanged(2);
                    return;
                }
                return;
            }
            checkActionModeIcons();
            StickersActivity.this.selectedCountTextView.setNumber(selectedCount, isActionModeShowed);
            if (isActionModeShowed) {
                return;
            }
            ((BaseFragment) StickersActivity.this).actionBar.showActionMode();
            notifyStickersItemsChanged(2);
            if (SharedConfig.stickersReorderingHintUsed || StickersActivity.this.currentType == 5) {
                return;
            }
            SharedConfig.setStickersReorderingHintUsed(true);
            Bulletin.make(((BaseFragment) StickersActivity.this).parentLayout.getLastFragment(), new ReorderingBulletinLayout(this.mContext, LocaleController.getString(R.string.StickersReorderHint), null), 3250).show();
        }

        private void checkActionModeIcons() {
            if (hasSelected()) {
                int size = this.stickerSets.size();
                int i = 0;
                int i2 = 0;
                while (true) {
                    if (i2 < size) {
                        if (((Boolean) this.selectedItems.get(((TLRPC.TL_messages_stickerSet) this.stickerSets.get(i2)).set.id, Boolean.FALSE)).booleanValue() && ((TLRPC.TL_messages_stickerSet) this.stickerSets.get(i2)).set.official && !((TLRPC.TL_messages_stickerSet) this.stickerSets.get(i2)).set.emojis) {
                            i = 8;
                            break;
                        }
                        i2++;
                    } else {
                        break;
                    }
                }
                if (StickersActivity.this.deleteMenuItem.getVisibility() != i) {
                    StickersActivity.this.deleteMenuItem.setVisibility(i);
                }
            }
        }

        public void lambda$onBindViewHolder$1(View view) {
            MediaDataController mediaDataController;
            Activity parentActivity;
            StickersActivity stickersActivity;
            boolean z;
            boolean z2;
            int i;
            FeaturedStickerSetCell2 featuredStickerSetCell2 = (FeaturedStickerSetCell2) view.getParent();
            TLRPC.StickerSetCovered stickerSet = featuredStickerSetCell2.getStickerSet();
            if (this.loadingFeaturedStickerSets.contains(Long.valueOf(stickerSet.set.id))) {
                return;
            }
            StickersActivity.this.isListeningForFeaturedUpdate = true;
            this.loadingFeaturedStickerSets.add(Long.valueOf(stickerSet.set.id));
            featuredStickerSetCell2.setDrawProgress(true, true);
            if (featuredStickerSetCell2.isInstalled()) {
                mediaDataController = MediaDataController.getInstance(((BaseFragment) StickersActivity.this).currentAccount);
                parentActivity = StickersActivity.this.getParentActivity();
                stickersActivity = StickersActivity.this;
                z = false;
                z2 = false;
                i = 0;
            } else {
                mediaDataController = MediaDataController.getInstance(((BaseFragment) StickersActivity.this).currentAccount);
                parentActivity = StickersActivity.this.getParentActivity();
                stickersActivity = StickersActivity.this;
                z = false;
                z2 = false;
                i = 2;
            }
            mediaDataController.toggleStickerSet(parentActivity, stickerSet, i, stickersActivity, z, z2);
        }

        public boolean lambda$onCreateViewHolder$2(StickerSetCell stickerSetCell, View view, MotionEvent motionEvent) {
            if (motionEvent.getAction() != 0) {
                return false;
            }
            StickersActivity.this.itemTouchHelper.startDrag(StickersActivity.this.listView.getChildViewHolder(stickerSetCell));
            return false;
        }

        public void lambda$onCreateViewHolder$3(TLRPC.TL_messages_stickerSet tL_messages_stickerSet) {
            processSelectionOption(0, tL_messages_stickerSet);
        }

        public void lambda$onCreateViewHolder$4(TLRPC.TL_messages_stickerSet tL_messages_stickerSet) {
            processSelectionOption(4, tL_messages_stickerSet);
        }

        public void lambda$onCreateViewHolder$5(TLRPC.TL_messages_stickerSet tL_messages_stickerSet) {
            processSelectionOption(3, tL_messages_stickerSet);
        }

        public void lambda$onCreateViewHolder$6(TLRPC.TL_messages_stickerSet tL_messages_stickerSet) {
            processSelectionOption(4, tL_messages_stickerSet);
        }

        public void lambda$onCreateViewHolder$7(TLRPC.TL_messages_stickerSet tL_messages_stickerSet) {
            processSelectionOption(2, tL_messages_stickerSet);
        }

        public void lambda$onCreateViewHolder$8(TLRPC.TL_messages_stickerSet tL_messages_stickerSet) {
            processSelectionOption(1, tL_messages_stickerSet);
        }

        public void lambda$onCreateViewHolder$9(View view) {
            StickerSetCell stickerSetCell = (StickerSetCell) view.getParent();
            final TLRPC.TL_messages_stickerSet stickersSet = stickerSetCell.getStickersSet();
            ItemOptions makeOptions = ItemOptions.makeOptions(StickersActivity.this, stickerSetCell);
            makeOptions.add(R.drawable.msg_archive, LocaleController.getString(R.string.StickersHide), new Runnable() {
                @Override
                public final void run() {
                    StickersActivity.ListAdapter.this.lambda$onCreateViewHolder$3(stickersSet);
                }
            });
            if (stickersSet.set.official) {
                makeOptions.add(R.drawable.msg_reorder, LocaleController.getString(R.string.StickersReorder), new Runnable() {
                    @Override
                    public final void run() {
                        StickersActivity.ListAdapter.this.lambda$onCreateViewHolder$4(stickersSet);
                    }
                });
            } else {
                makeOptions.add(R.drawable.msg_link, LocaleController.getString(R.string.StickersCopy), new Runnable() {
                    @Override
                    public final void run() {
                        StickersActivity.ListAdapter.this.lambda$onCreateViewHolder$5(stickersSet);
                    }
                });
                makeOptions.add(R.drawable.msg_reorder, LocaleController.getString(R.string.StickersReorder), new Runnable() {
                    @Override
                    public final void run() {
                        StickersActivity.ListAdapter.this.lambda$onCreateViewHolder$6(stickersSet);
                    }
                });
                makeOptions.add(R.drawable.msg_share, LocaleController.getString(R.string.StickersShare), new Runnable() {
                    @Override
                    public final void run() {
                        StickersActivity.ListAdapter.this.lambda$onCreateViewHolder$7(stickersSet);
                    }
                });
                makeOptions.add(R.drawable.msg_delete, (CharSequence) LocaleController.getString(R.string.StickersRemove), true, new Runnable() {
                    @Override
                    public final void run() {
                        StickersActivity.ListAdapter.this.lambda$onCreateViewHolder$8(stickersSet);
                    }
                });
            }
            makeOptions.setMinWidth(190);
            makeOptions.show();
        }

        public void lambda$processSelectionMenu$0(ArrayList arrayList, int i, AlertDialog alertDialog, int i2) {
            StickersActivity.this.listAdapter.clearSelected();
            MediaDataController.getInstance(((BaseFragment) StickersActivity.this).currentAccount).toggleStickerSets(arrayList, StickersActivity.this.currentType, i == 1 ? 0 : 1, StickersActivity.this, true);
        }

        public int lambda$swapElements$10(TLRPC.TL_messages_stickerSet tL_messages_stickerSet, TLRPC.TL_messages_stickerSet tL_messages_stickerSet2) {
            int indexOf = this.stickerSets.indexOf(tL_messages_stickerSet);
            int indexOf2 = this.stickerSets.indexOf(tL_messages_stickerSet2);
            if (indexOf < 0 || indexOf2 < 0) {
                return 0;
            }
            return indexOf - indexOf2;
        }

        private void notifyStickersItemsChanged(Object obj) {
            notifyItemRangeChanged(StickersActivity.this.stickersStartRow, StickersActivity.this.stickersEndRow - StickersActivity.this.stickersStartRow, obj);
        }

        public void processSelectionMenu(final int i) {
            int i2;
            TextView textView;
            int i3 = 0;
            if (i == 2) {
                StringBuilder sb = new StringBuilder();
                int size = this.stickerSets.size();
                while (i3 < size) {
                    TLRPC.TL_messages_stickerSet tL_messages_stickerSet = (TLRPC.TL_messages_stickerSet) this.stickerSets.get(i3);
                    if (((Boolean) this.selectedItems.get(tL_messages_stickerSet.set.id, Boolean.FALSE)).booleanValue()) {
                        if (sb.length() != 0) {
                            sb.append("\n");
                        }
                        sb.append(StickersActivity.this.getLinkForSet(tL_messages_stickerSet));
                    }
                    i3++;
                }
                String sb2 = sb.toString();
                ShareAlert createShareAlert = ShareAlert.createShareAlert(StickersActivity.this.fragmentView.getContext(), null, sb2, false, sb2, false);
                createShareAlert.setDelegate(new ShareAlert.ShareAlertDelegate() {
                    @Override
                    public boolean didCopy() {
                        ListAdapter.this.clearSelected();
                        return true;
                    }

                    @Override
                    public void didShare() {
                        ListAdapter.this.clearSelected();
                    }
                });
                createShareAlert.show();
                return;
            }
            if (i == 0 || i == 1) {
                final ArrayList arrayList = new ArrayList(this.selectedItems.size());
                int size2 = this.stickerSets.size();
                for (int i4 = 0; i4 < size2; i4++) {
                    TLRPC.StickerSet stickerSet = ((TLRPC.TL_messages_stickerSet) this.stickerSets.get(i4)).set;
                    if (((Boolean) this.selectedItems.get(stickerSet.id, Boolean.FALSE)).booleanValue()) {
                        arrayList.add(stickerSet);
                    }
                }
                int size3 = arrayList.size();
                if (size3 != 0) {
                    if (size3 == 1) {
                        int size4 = this.stickerSets.size();
                        while (true) {
                            if (i3 >= size4) {
                                break;
                            }
                            TLRPC.TL_messages_stickerSet tL_messages_stickerSet2 = (TLRPC.TL_messages_stickerSet) this.stickerSets.get(i3);
                            if (((Boolean) this.selectedItems.get(tL_messages_stickerSet2.set.id, Boolean.FALSE)).booleanValue()) {
                                processSelectionOption(i, tL_messages_stickerSet2);
                                break;
                            }
                            i3++;
                        }
                        StickersActivity.this.listAdapter.clearSelected();
                        return;
                    }
                    AlertDialog.Builder builder = new AlertDialog.Builder(StickersActivity.this.getParentActivity());
                    if (i == 1) {
                        builder.setTitle(LocaleController.formatString("DeleteStickerSetsAlertTitle", R.string.DeleteStickerSetsAlertTitle, LocaleController.formatPluralString("StickerSets", size3, new Object[0])));
                        builder.setMessage(LocaleController.formatString("DeleteStickersAlertMessage", R.string.DeleteStickersAlertMessage, Integer.valueOf(size3)));
                        i2 = R.string.Delete;
                    } else {
                        builder.setTitle(LocaleController.formatString("ArchiveStickerSetsAlertTitle", R.string.ArchiveStickerSetsAlertTitle, LocaleController.formatPluralString("StickerSets", size3, new Object[0])));
                        builder.setMessage(LocaleController.formatString("ArchiveStickersAlertMessage", R.string.ArchiveStickersAlertMessage, Integer.valueOf(size3)));
                        i2 = R.string.Archive;
                    }
                    builder.setPositiveButton(LocaleController.getString(i2), new AlertDialog.OnButtonClickListener() {
                        @Override
                        public final void onClick(AlertDialog alertDialog, int i5) {
                            StickersActivity.ListAdapter.this.lambda$processSelectionMenu$0(arrayList, i, alertDialog, i5);
                        }
                    });
                    builder.setNegativeButton(LocaleController.getString(R.string.Cancel), null);
                    AlertDialog create = builder.create();
                    StickersActivity.this.showDialog(create);
                    if (i != 1 || (textView = (TextView) create.getButton(-1)) == null) {
                        return;
                    }
                    textView.setTextColor(Theme.getColor(Theme.key_text_RedBold));
                }
            }
        }

        private void processSelectionOption(int i, TLRPC.TL_messages_stickerSet tL_messages_stickerSet) {
            int indexOf;
            if (i == 0) {
                MediaDataController.getInstance(((BaseFragment) StickersActivity.this).currentAccount).toggleStickerSet(StickersActivity.this.getParentActivity(), tL_messages_stickerSet, !tL_messages_stickerSet.set.archived ? 1 : 2, StickersActivity.this, true, true);
                return;
            }
            if (i == 1) {
                MediaDataController.getInstance(((BaseFragment) StickersActivity.this).currentAccount).toggleStickerSet(StickersActivity.this.getParentActivity(), tL_messages_stickerSet, 0, StickersActivity.this, true, true);
                return;
            }
            try {
                if (i == 2) {
                    Intent intent = new Intent("android.intent.action.SEND");
                    intent.setType("text/plain");
                    intent.putExtra("android.intent.extra.TEXT", StickersActivity.this.getLinkForSet(tL_messages_stickerSet));
                    StickersActivity.this.getParentActivity().startActivityForResult(Intent.createChooser(intent, LocaleController.getString(R.string.StickersShare)), 500);
                } else {
                    if (i != 3) {
                        if (i != 4 || (indexOf = this.stickerSets.indexOf(tL_messages_stickerSet)) < 0) {
                            return;
                        }
                        StickersActivity.this.listAdapter.toggleSelected(StickersActivity.this.stickersStartRow + indexOf);
                        return;
                    }
                    Locale locale = Locale.US;
                    StringBuilder sb = new StringBuilder();
                    sb.append("https://");
                    sb.append(MessagesController.getInstance(((BaseFragment) StickersActivity.this).currentAccount).linkPrefix);
                    sb.append("/");
                    sb.append(tL_messages_stickerSet.set.emojis ? "addemoji" : "addstickers");
                    sb.append("/%s");
                    ((ClipboardManager) ApplicationLoader.applicationContext.getSystemService("clipboard")).setPrimaryClip(ClipData.newPlainText("label", String.format(locale, sb.toString(), tL_messages_stickerSet.set.short_name)));
                    BulletinFactory.createCopyLinkBulletin(StickersActivity.this).show();
                }
            } catch (Exception e) {
                FileLog.e(e);
            }
        }

        private void swapListElements(List list, int i, int i2) {
            TLRPC.TL_messages_stickerSet tL_messages_stickerSet = (TLRPC.TL_messages_stickerSet) list.get(i);
            list.set(i, (TLRPC.TL_messages_stickerSet) list.get(i2));
            list.set(i2, tL_messages_stickerSet);
        }

        public void clearSelected() {
            this.selectedItems.clear();
            notifyStickersItemsChanged(1);
            checkActionMode();
        }

        @Override
        public int getItemCount() {
            return StickersActivity.this.rowCount;
        }

        @Override
        public long getItemId(int i) {
            TLRPC.StickerSet stickerSet;
            if (i >= StickersActivity.this.featuredStickersStartRow && i < StickersActivity.this.featuredStickersEndRow) {
                stickerSet = ((TLRPC.StickerSetCovered) this.featuredStickerSets.get(i - StickersActivity.this.featuredStickersStartRow)).set;
            } else {
                if (i < StickersActivity.this.stickersStartRow || i >= StickersActivity.this.stickersEndRow) {
                    return i;
                }
                stickerSet = ((TLRPC.TL_messages_stickerSet) this.stickerSets.get(i - StickersActivity.this.stickersStartRow)).set;
            }
            return stickerSet.id;
        }

        @Override
        public int getItemViewType(int i) {
            if (i >= StickersActivity.this.featuredStickersStartRow && i < StickersActivity.this.featuredStickersEndRow) {
                return 7;
            }
            if (i >= StickersActivity.this.stickersStartRow && i < StickersActivity.this.stickersEndRow) {
                return 0;
            }
            if (i == StickersActivity.this.stickersBotInfo || i == StickersActivity.this.archivedInfoRow || i == StickersActivity.this.loopInfoRow || i == StickersActivity.this.suggestAnimatedEmojiInfoRow || i == StickersActivity.this.masksInfoRow || i == StickersActivity.this.dynamicPackOrderInfo) {
                return 1;
            }
            if (i == StickersActivity.this.archivedRow || i == StickersActivity.this.masksRow || i == StickersActivity.this.featuredRow || i == StickersActivity.this.emojiPacksRow || i == StickersActivity.this.suggestRow || i == StickersActivity.this.featuredStickersShowMoreRow) {
                return 2;
            }
            if (i == StickersActivity.this.stickersShadowRow || i == StickersActivity.this.featuredStickersShadowRow) {
                return 3;
            }
            if (i == StickersActivity.this.loopRow || i == StickersActivity.this.largeEmojiRow || i == StickersActivity.this.suggestAnimatedEmojiRow || i == StickersActivity.this.dynamicPackOrder) {
                return 4;
            }
            if (i == StickersActivity.this.reactionsDoubleTapRow) {
                return 5;
            }
            return (i == StickersActivity.this.featuredStickersHeaderRow || i == StickersActivity.this.stickersHeaderRow || i == StickersActivity.this.stickersSettingsRow) ? 6 : 0;
        }

        public int getSelectedCount() {
            int size = this.selectedItems.size();
            int i = 0;
            for (int i2 = 0; i2 < size; i2++) {
                if (((Boolean) this.selectedItems.valueAt(i2)).booleanValue()) {
                    i++;
                }
            }
            return i;
        }

        public boolean hasSelected() {
            return this.selectedItems.indexOfValue(Boolean.TRUE) != -1;
        }

        @Override
        public boolean isEnabled(RecyclerView.ViewHolder viewHolder) {
            int itemViewType = viewHolder.getItemViewType();
            return itemViewType == 0 || itemViewType == 7 || itemViewType == 2 || itemViewType == 4 || itemViewType == 5;
        }

        @Override
        public void notifyDataSetChanged() {
            super.notifyDataSetChanged();
            if (StickersActivity.this.isListeningForFeaturedUpdate) {
                StickersActivity.this.isListeningForFeaturedUpdate = false;
            }
        }

        @Override
        public void onBindViewHolder(androidx.recyclerview.widget.RecyclerView.ViewHolder r12, int r13) {
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.StickersActivity.ListAdapter.onBindViewHolder(androidx.recyclerview.widget.RecyclerView$ViewHolder, int):void");
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i, List list) {
            if (list.isEmpty()) {
                onBindViewHolder(viewHolder, i);
                return;
            }
            int itemViewType = viewHolder.getItemViewType();
            if (itemViewType == 0) {
                if (i < StickersActivity.this.stickersStartRow || i >= StickersActivity.this.stickersEndRow) {
                    return;
                }
                StickerSetCell stickerSetCell = (StickerSetCell) viewHolder.itemView;
                if (list.contains(1)) {
                    stickerSetCell.setChecked(((Boolean) this.selectedItems.get(getItemId(i), Boolean.FALSE)).booleanValue());
                }
                if (list.contains(2)) {
                    stickerSetCell.setReorderable(hasSelected());
                }
                if (list.contains(3)) {
                    stickerSetCell.setNeedDivider(i - StickersActivity.this.stickersStartRow != this.stickerSets.size() - 1);
                    return;
                }
                return;
            }
            if (itemViewType == 4) {
                if (list.contains(0) && i == StickersActivity.this.loopRow) {
                    ((TextCheckCell) viewHolder.itemView).setChecked(SharedConfig.loopStickers());
                    return;
                }
                return;
            }
            if (itemViewType == 7 && list.contains(4) && i >= StickersActivity.this.featuredStickersStartRow && i <= StickersActivity.this.featuredStickersEndRow) {
                ((FeaturedStickerSetCell2) viewHolder.itemView).setStickersSet((TLRPC.StickerSetCovered) this.featuredStickerSets.get(i - StickersActivity.this.featuredStickersStartRow), true, false, false, true);
            }
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View view;
            View view2;
            int i2 = 1;
            if (i == 0) {
                final StickerSetCell stickerSetCell = new StickerSetCell(this.mContext, i2) {
                    @Override
                    protected void onAddButtonClick() {
                        ?? r1;
                        TLRPC.TL_messages_stickerSet stickersSet = getStickersSet();
                        if (stickersSet == null || stickersSet.set == null) {
                            return;
                        }
                        ArrayList<TLRPC.StickerSetCovered> featuredEmojiSets = StickersActivity.this.getMediaDataController().getFeaturedEmojiSets();
                        int i3 = 0;
                        while (true) {
                            if (i3 >= featuredEmojiSets.size()) {
                                r1 = 0;
                                break;
                            } else {
                                if (stickersSet.set.id == featuredEmojiSets.get(i3).set.id) {
                                    r1 = featuredEmojiSets.get(i3);
                                    break;
                                }
                                i3++;
                            }
                        }
                        if (r1 != 0) {
                            if (ListAdapter.this.loadingFeaturedStickerSets.contains(Long.valueOf(r1.set.id))) {
                                return;
                            } else {
                                ListAdapter.this.loadingFeaturedStickerSets.add(Long.valueOf(r1.set.id));
                            }
                        }
                        MediaDataController.getInstance(((BaseFragment) StickersActivity.this).currentAccount).toggleStickerSet(StickersActivity.this.getParentActivity(), r1 == 0 ? stickersSet : r1, 2, StickersActivity.this, false, false);
                    }

                    @Override
                    protected void onPremiumButtonClick() {
                        StickersActivity.this.showDialog(new PremiumFeatureBottomSheet(StickersActivity.this, 11, false));
                    }

                    @Override
                    protected void onRemoveButtonClick() {
                        MediaDataController.getInstance(((BaseFragment) StickersActivity.this).currentAccount).toggleStickerSet(StickersActivity.this.getParentActivity(), getStickersSet(), 0, StickersActivity.this, false, true);
                    }
                };
                stickerSetCell.setBackgroundColor(StickersActivity.this.getThemedColor(Theme.key_windowBackgroundWhite));
                stickerSetCell.setOnReorderButtonTouchListener(new View.OnTouchListener() {
                    @Override
                    public final boolean onTouch(View view3, MotionEvent motionEvent) {
                        boolean lambda$onCreateViewHolder$2;
                        lambda$onCreateViewHolder$2 = StickersActivity.ListAdapter.this.lambda$onCreateViewHolder$2(stickerSetCell, view3, motionEvent);
                        return lambda$onCreateViewHolder$2;
                    }
                });
                stickerSetCell.setOnOptionsClick(new View.OnClickListener() {
                    @Override
                    public final void onClick(View view3) {
                        StickersActivity.ListAdapter.this.lambda$onCreateViewHolder$9(view3);
                    }
                });
                view = stickerSetCell;
            } else if (i != 1) {
                if (i == 2) {
                    view2 = new TextCell(this.mContext);
                } else if (i == 3) {
                    view = new ShadowSectionCell(this.mContext);
                } else if (i == 5) {
                    view2 = new TextSettingsCell(this.mContext);
                } else if (i == 6) {
                    view2 = new HeaderCell(this.mContext);
                } else if (i != 7) {
                    view2 = new TextCheckCell(this.mContext);
                } else {
                    FeaturedStickerSetCell2 featuredStickerSetCell2 = new FeaturedStickerSetCell2(this.mContext, StickersActivity.this.getResourceProvider()) {
                        @Override
                        protected void onPremiumButtonClick() {
                            StickersActivity.this.showDialog(new PremiumFeatureBottomSheet(StickersActivity.this, 11, false));
                        }
                    };
                    featuredStickerSetCell2.setBackgroundColor(StickersActivity.this.getThemedColor(Theme.key_windowBackgroundWhite));
                    featuredStickerSetCell2.getTextView().setTypeface(AndroidUtilities.bold());
                    view = featuredStickerSetCell2;
                }
                view2.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                view = view2;
            } else {
                View textInfoPrivacyCell = new TextInfoPrivacyCell(this.mContext);
                textInfoPrivacyCell.setBackground(Theme.getThemedDrawableByKey(this.mContext, R.drawable.greydivider_bottom, Theme.key_windowBackgroundGrayShadow));
                view = textInfoPrivacyCell;
            }
            view.setLayoutParams(new RecyclerView.LayoutParams(-1, -2));
            return new RecyclerListView.Holder(view);
        }

        public void setFeaturedStickerSets(List list) {
            this.featuredStickerSets.clear();
            this.featuredStickerSets.addAll(list);
        }

        public void setStickerSets(List list) {
            this.stickerSets.clear();
            this.stickerSets.addAll(list);
        }

        public void swapElements(int i, int i2) {
            if (i != i2) {
                StickersActivity.this.needReorder = true;
            }
            MediaDataController mediaDataController = MediaDataController.getInstance(((BaseFragment) StickersActivity.this).currentAccount);
            swapListElements(this.stickerSets, i - StickersActivity.this.stickersStartRow, i2 - StickersActivity.this.stickersStartRow);
            Collections.sort(mediaDataController.getStickerSets(StickersActivity.this.currentType), new Comparator() {
                @Override
                public final int compare(Object obj, Object obj2) {
                    int lambda$swapElements$10;
                    lambda$swapElements$10 = StickersActivity.ListAdapter.this.lambda$swapElements$10((TLRPC.TL_messages_stickerSet) obj, (TLRPC.TL_messages_stickerSet) obj2);
                    return lambda$swapElements$10;
                }
            });
            notifyItemMoved(i, i2);
            if (i == StickersActivity.this.stickersEndRow - 1 || i2 == StickersActivity.this.stickersEndRow - 1) {
                notifyItemRangeChanged(i, 3);
                notifyItemRangeChanged(i2, 3);
            }
        }

        public void toggleSelected(int i) {
            long itemId = getItemId(i);
            this.selectedItems.put(itemId, Boolean.valueOf(!((Boolean) r2.get(itemId, Boolean.FALSE)).booleanValue()));
            notifyItemChanged(i, 1);
            checkActionMode();
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
            return viewHolder.getItemViewType() != 0 ? ItemTouchHelper.Callback.makeMovementFlags(0, 0) : ItemTouchHelper.Callback.makeMovementFlags(3, 0);
        }

        @Override
        public boolean isLongPressDragEnabled() {
            return StickersActivity.this.listAdapter.hasSelected();
        }

        @Override
        public void onChildDraw(Canvas canvas, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float f, float f2, int i, boolean z) {
            super.onChildDraw(canvas, recyclerView, viewHolder, f, f2, i, z);
        }

        @Override
        public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder viewHolder2) {
            if (viewHolder.getItemViewType() != viewHolder2.getItemViewType()) {
                return false;
            }
            StickersActivity.this.listAdapter.swapElements(viewHolder.getAdapterPosition(), viewHolder2.getAdapterPosition());
            return true;
        }

        @Override
        public void onSelectedChanged(RecyclerView.ViewHolder viewHolder, int i) {
            StickersActivity stickersActivity = StickersActivity.this;
            if (i == 0) {
                stickersActivity.sendReorder();
            } else {
                stickersActivity.listView.cancelClickRunnables(false);
                viewHolder.itemView.setPressed(true);
            }
            super.onSelectedChanged(viewHolder, i);
        }

        @Override
        public void onSwiped(RecyclerView.ViewHolder viewHolder, int i) {
        }
    }

    public StickersActivity(int i, ArrayList arrayList) {
        this.currentType = i;
        this.frozenEmojiPacks = arrayList;
    }

    public List getFeaturedSets() {
        MediaDataController mediaDataController = MediaDataController.getInstance(this.currentAccount);
        if (this.currentType != 5) {
            return mediaDataController.getFeaturedStickerSets();
        }
        ArrayList arrayList = new ArrayList(mediaDataController.getFeaturedEmojiSets());
        int i = 0;
        while (i < arrayList.size()) {
            if (arrayList.get(i) == null || mediaDataController.isStickerPackInstalled(((TLRPC.StickerSetCovered) arrayList.get(i)).set.id, false)) {
                arrayList.remove(i);
                i--;
            }
            i++;
        }
        return arrayList;
    }

    public String getLinkForSet(TLRPC.TL_messages_stickerSet tL_messages_stickerSet) {
        Locale locale = Locale.US;
        StringBuilder sb = new StringBuilder();
        sb.append("https://");
        sb.append(MessagesController.getInstance(this.currentAccount).linkPrefix);
        sb.append("/");
        sb.append(tL_messages_stickerSet.set.emojis ? "addemoji" : "addstickers");
        sb.append("/%s");
        return String.format(locale, sb.toString(), tL_messages_stickerSet.set.short_name);
    }

    public static boolean lambda$createView$0(View view, MotionEvent motionEvent) {
        return true;
    }

    public void lambda$createView$1(AlertDialog.Builder builder, View view) {
        SharedConfig.setSuggestStickers(((Integer) view.getTag()).intValue());
        this.updateSuggestStickers = true;
        this.listAdapter.notifyItemChanged(this.suggestRow);
        builder.getDismissRunnable().run();
    }

    public void lambda$createView$2(Context context, View view, int i) {
        EmojiPacksAlert emojiPacksAlert;
        TextCheckCell textCheckCell;
        boolean z;
        BaseFragment reactionsDoubleTapManageActivity;
        Dialog create;
        if (i >= this.featuredStickersStartRow && i < this.featuredStickersEndRow && getParentActivity() != null) {
            TLRPC.StickerSetCovered stickerSetCovered = (TLRPC.StickerSetCovered) this.listAdapter.featuredStickerSets.get(i - this.featuredStickersStartRow);
            TLRPC.TL_inputStickerSetID tL_inputStickerSetID = new TLRPC.TL_inputStickerSetID();
            TLRPC.StickerSet stickerSet = stickerSetCovered.set;
            tL_inputStickerSetID.id = stickerSet.id;
            tL_inputStickerSetID.access_hash = stickerSet.access_hash;
            if (this.currentType != 5) {
                create = new StickersAlert(getParentActivity(), this, tL_inputStickerSetID, null, null, false);
                showDialog(create);
                return;
            } else {
                ArrayList arrayList = new ArrayList(1);
                arrayList.add(tL_inputStickerSetID);
                emojiPacksAlert = new EmojiPacksAlert(this, getParentActivity(), getResourceProvider(), arrayList);
                showDialog(emojiPacksAlert);
            }
        }
        if (i != this.featuredStickersShowMoreRow && i != this.featuredRow) {
            if (i < this.stickersStartRow || i >= this.stickersEndRow || getParentActivity() == null) {
                if (i == this.archivedRow) {
                    reactionsDoubleTapManageActivity = new ArchivedStickersActivity(this.currentType);
                } else if (i == this.masksRow) {
                    reactionsDoubleTapManageActivity = new StickersActivity(1, null);
                } else if (i == this.emojiPacksRow) {
                    reactionsDoubleTapManageActivity = new StickersActivity(5, null);
                } else {
                    if (i != this.suggestRow) {
                        if (i == this.loopRow) {
                            SharedConfig.toggleLoopStickers();
                            this.listAdapter.notifyItemChanged(this.loopRow, 0);
                            return;
                        }
                        if (i == this.largeEmojiRow) {
                            SharedConfig.toggleBigEmoji();
                            textCheckCell = (TextCheckCell) view;
                            z = SharedConfig.allowBigEmoji;
                        } else if (i == this.suggestAnimatedEmojiRow) {
                            SharedConfig.toggleSuggestAnimatedEmoji();
                            textCheckCell = (TextCheckCell) view;
                            z = SharedConfig.suggestAnimatedEmoji;
                        } else if (i == this.reactionsDoubleTapRow) {
                            reactionsDoubleTapManageActivity = new ReactionsDoubleTapManageActivity();
                        } else {
                            if (i != this.dynamicPackOrder) {
                                return;
                            }
                            SharedConfig.toggleUpdateStickersOrderOnSend();
                            textCheckCell = (TextCheckCell) view;
                            z = SharedConfig.updateStickersOrderOnSend;
                        }
                        textCheckCell.setChecked(z);
                        return;
                    }
                    if (getParentActivity() == null) {
                        return;
                    }
                    final AlertDialog.Builder builder = new AlertDialog.Builder(getParentActivity());
                    builder.setTitle(LocaleController.getString(R.string.SuggestStickers));
                    String[] strArr = {LocaleController.getString(R.string.SuggestStickersAll), LocaleController.getString(R.string.SuggestStickersInstalled), LocaleController.getString(R.string.SuggestStickersNone)};
                    LinearLayout linearLayout = new LinearLayout(getParentActivity());
                    linearLayout.setOrientation(1);
                    builder.setView(linearLayout);
                    int i2 = 0;
                    while (i2 < 3) {
                        RadioColorCell radioColorCell = new RadioColorCell(getParentActivity());
                        radioColorCell.setPadding(AndroidUtilities.dp(4.0f), 0, AndroidUtilities.dp(4.0f), 0);
                        radioColorCell.setTag(Integer.valueOf(i2));
                        radioColorCell.setCheckColor(Theme.getColor(Theme.key_radioBackground), Theme.getColor(Theme.key_dialogRadioBackgroundChecked));
                        radioColorCell.setTextAndValue(strArr[i2], SharedConfig.suggestStickers == i2);
                        radioColorCell.setBackground(Theme.createSelectorDrawable(Theme.getColor(Theme.key_listSelector), 2));
                        linearLayout.addView(radioColorCell);
                        radioColorCell.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public final void onClick(View view2) {
                                StickersActivity.this.lambda$createView$1(builder, view2);
                            }
                        });
                        i2++;
                    }
                    create = builder.create();
                }
                presentFragment(reactionsDoubleTapManageActivity);
                return;
            }
            if (this.listAdapter.hasSelected()) {
                this.listAdapter.toggleSelected(i);
                return;
            }
            TLRPC.TL_messages_stickerSet tL_messages_stickerSet = (TLRPC.TL_messages_stickerSet) this.listAdapter.stickerSets.get(i - this.stickersStartRow);
            ArrayList<TLRPC.Document> arrayList2 = tL_messages_stickerSet.documents;
            if (arrayList2 == null || arrayList2.isEmpty()) {
                return;
            }
            TLRPC.StickerSet stickerSet2 = tL_messages_stickerSet.set;
            if (stickerSet2 == null || !stickerSet2.emojis) {
                create = new StickersAlert(getParentActivity(), this, null, tL_messages_stickerSet, null, false);
            } else {
                ArrayList arrayList3 = new ArrayList();
                TLRPC.TL_inputStickerSetID tL_inputStickerSetID2 = new TLRPC.TL_inputStickerSetID();
                TLRPC.StickerSet stickerSet3 = tL_messages_stickerSet.set;
                tL_inputStickerSetID2.id = stickerSet3.id;
                tL_inputStickerSetID2.access_hash = stickerSet3.access_hash;
                arrayList3.add(tL_inputStickerSetID2);
                emojiPacksAlert = new EmojiPacksAlert(this, getParentActivity(), getResourceProvider(), arrayList3);
            }
            showDialog(create);
            return;
        }
        if (this.currentType != 5) {
            TrendingStickersAlert trendingStickersAlert = new TrendingStickersAlert(context, this, new TrendingStickersLayout(context, new TrendingStickersLayout.Delegate() {
                @Override
                public void onStickerSetAdd(TLRPC.StickerSetCovered stickerSetCovered2, boolean z2) {
                    MediaDataController.getInstance(((BaseFragment) StickersActivity.this).currentAccount).toggleStickerSet(StickersActivity.this.getParentActivity(), stickerSetCovered2, 2, StickersActivity.this, false, false);
                }

                @Override
                public void onStickerSetRemove(TLRPC.StickerSetCovered stickerSetCovered2) {
                    MediaDataController.getInstance(((BaseFragment) StickersActivity.this).currentAccount).toggleStickerSet(StickersActivity.this.getParentActivity(), stickerSetCovered2, 0, StickersActivity.this, false, false);
                }
            }), null);
            this.trendingStickersAlert = trendingStickersAlert;
            trendingStickersAlert.show();
            return;
        }
        ArrayList arrayList4 = new ArrayList();
        List featuredSets = getFeaturedSets();
        if (featuredSets != null) {
            for (int i3 = 0; i3 < featuredSets.size(); i3++) {
                TLRPC.StickerSetCovered stickerSetCovered2 = (TLRPC.StickerSetCovered) featuredSets.get(i3);
                if (stickerSetCovered2 != null && stickerSetCovered2.set != null) {
                    TLRPC.TL_inputStickerSetID tL_inputStickerSetID3 = new TLRPC.TL_inputStickerSetID();
                    TLRPC.StickerSet stickerSet4 = stickerSetCovered2.set;
                    tL_inputStickerSetID3.id = stickerSet4.id;
                    tL_inputStickerSetID3.access_hash = stickerSet4.access_hash;
                    arrayList4.add(tL_inputStickerSetID3);
                }
            }
        }
        MediaDataController.getInstance(this.currentAccount).markFeaturedStickersAsRead(true, true);
        emojiPacksAlert = new EmojiPacksAlert(this, getParentActivity(), getResourceProvider(), arrayList4);
        showDialog(emojiPacksAlert);
    }

    public boolean lambda$createView$3(View view, int i) {
        if (this.listAdapter.hasSelected() || i < this.stickersStartRow || i >= this.stickersEndRow) {
            return false;
        }
        this.listAdapter.toggleSelected(i);
        return true;
    }

    public void lambda$sendReorder$4() {
        this.activeReorderingRequests--;
    }

    public void lambda$sendReorder$5(TLObject tLObject, TLRPC.TL_error tL_error) {
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                StickersActivity.this.lambda$sendReorder$4();
            }
        });
    }

    public void sendReorder() {
        if (this.needReorder) {
            MediaDataController.getInstance(this.currentAccount).calcNewHash(this.currentType);
            this.needReorder = false;
            this.activeReorderingRequests++;
            TLRPC.TL_messages_reorderStickerSets tL_messages_reorderStickerSets = new TLRPC.TL_messages_reorderStickerSets();
            int i = this.currentType;
            tL_messages_reorderStickerSets.masks = i == 1;
            tL_messages_reorderStickerSets.emojis = i == 5;
            for (int i2 = 0; i2 < this.listAdapter.stickerSets.size(); i2++) {
                tL_messages_reorderStickerSets.order.add(Long.valueOf(((TLRPC.TL_messages_stickerSet) this.listAdapter.stickerSets.get(i2)).set.id));
            }
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(tL_messages_reorderStickerSets, new RequestDelegate() {
                @Override
                public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                    StickersActivity.this.lambda$sendReorder$5(tLObject, tL_error);
                }
            });
            NotificationCenter.getInstance(this.currentAccount).lambda$postNotificationNameOnUIThread$1(NotificationCenter.stickersDidLoad, Integer.valueOf(this.currentType), Boolean.TRUE);
            if (!SharedConfig.updateStickersOrderOnSend || this.dynamicPackOrder == -1) {
                return;
            }
            SharedConfig.toggleUpdateStickersOrderOnSend();
            BulletinFactory.of(this).createSimpleBulletin(R.raw.filter_reorder, LocaleController.getString(R.string.DynamicPackOrderOff), LocaleController.getString(R.string.DynamicPackOrderOffInfo)).show();
            for (int i3 = 0; i3 < this.listView.getChildCount(); i3++) {
                View childAt = this.listView.getChildAt(i3);
                if (this.listView.getChildAdapterPosition(childAt) == this.dynamicPackOrder && (childAt instanceof TextCheckCell)) {
                    ((TextCheckCell) childAt).setChecked(SharedConfig.updateStickersOrderOnSend);
                    return;
                }
            }
        }
    }

    private void updateRows(boolean r14) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.StickersActivity.updateRows(boolean):void");
    }

    @Override
    public View createView(final Context context) {
        ActionBar actionBar;
        int i;
        this.actionBar.setBackButtonDrawable(new BackDrawable(false));
        this.actionBar.setAllowOverlayTitle(true);
        int i2 = this.currentType;
        if (i2 == 0) {
            actionBar = this.actionBar;
            i = R.string.StickersName;
        } else {
            if (i2 != 1) {
                if (i2 == 5) {
                    actionBar = this.actionBar;
                    i = R.string.Emoji;
                }
                this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
                    @Override
                    public void onItemClick(int i3) {
                        if (i3 == -1) {
                            if (StickersActivity.this.onBackPressed()) {
                                StickersActivity.this.lambda$onBackPressed$323();
                            }
                        } else if (i3 == 0 || i3 == 1 || i3 == 2) {
                            if (StickersActivity.this.needReorder) {
                                StickersActivity.this.sendReorder();
                            } else if (StickersActivity.this.activeReorderingRequests == 0) {
                                StickersActivity.this.listAdapter.processSelectionMenu(i3);
                            }
                        }
                    }
                });
                ActionBarMenu createActionMode = this.actionBar.createActionMode();
                NumberTextView numberTextView = new NumberTextView(createActionMode.getContext());
                this.selectedCountTextView = numberTextView;
                numberTextView.setTextSize(18);
                this.selectedCountTextView.setTypeface(AndroidUtilities.bold());
                this.selectedCountTextView.setTextColor(Theme.getColor(Theme.key_actionBarActionModeDefaultIcon));
                createActionMode.addView(this.selectedCountTextView, LayoutHelper.createLinear(0, -1, 1.0f, 72, 0, 0, 0));
                this.selectedCountTextView.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public final boolean onTouch(View view, MotionEvent motionEvent) {
                        boolean lambda$createView$0;
                        lambda$createView$0 = StickersActivity.lambda$createView$0(view, motionEvent);
                        return lambda$createView$0;
                    }
                });
                this.shareMenuItem = createActionMode.addItemWithWidth(2, R.drawable.msg_share, AndroidUtilities.dp(54.0f));
                this.archiveMenuItem = createActionMode.addItemWithWidth(0, R.drawable.msg_archive, AndroidUtilities.dp(54.0f));
                this.deleteMenuItem = createActionMode.addItemWithWidth(1, R.drawable.msg_delete, AndroidUtilities.dp(54.0f));
                if (this.currentType == 5 || (r0 = this.frozenEmojiPacks) == null) {
                    ArrayList arrayList = new ArrayList(MessagesController.getInstance(this.currentAccount).filterPremiumStickers(MediaDataController.getInstance(this.currentAccount).getStickerSets(this.currentType)));
                }
                this.listAdapter = new ListAdapter(context, arrayList, getFeaturedSets());
                FrameLayout frameLayout = new FrameLayout(context);
                this.fragmentView = frameLayout;
                frameLayout.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundGray));
                RecyclerListView recyclerListView = new RecyclerListView(context) {
                    @Override
                    public void dispatchDraw(Canvas canvas) {
                        if (((BaseFragment) StickersActivity.this).actionBar.isActionModeShowed()) {
                            drawSectionBackground(canvas, StickersActivity.this.stickersHeaderRow, StickersActivity.this.stickersEndRow - 1, getThemedColor(Theme.key_windowBackgroundWhite));
                        }
                        super.dispatchDraw(canvas);
                    }
                };
                this.listView = recyclerListView;
                recyclerListView.setFocusable(true);
                this.listView.setTag(7);
                DefaultItemAnimator defaultItemAnimator = new DefaultItemAnimator() {
                    @Override
                    public void onMoveAnimationUpdate(RecyclerView.ViewHolder viewHolder) {
                        super.onMoveAnimationUpdate(viewHolder);
                        StickersActivity.this.listView.invalidate();
                    }
                };
                defaultItemAnimator.setMoveDuration(350L);
                defaultItemAnimator.setSupportsChangeAnimations(false);
                defaultItemAnimator.setMoveInterpolator(CubicBezierInterpolator.EASE_OUT_QUINT);
                this.listView.setItemAnimator(defaultItemAnimator);
                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context) {
                    @Override
                    public void calculateExtraLayoutSpace(RecyclerView.State state, int[] iArr) {
                        iArr[1] = StickersActivity.this.listView.getHeight();
                    }

                    @Override
                    public boolean supportsPredictiveItemAnimations() {
                        return false;
                    }
                };
                this.layoutManager = linearLayoutManager;
                linearLayoutManager.setOrientation(1);
                this.listView.setLayoutManager(this.layoutManager);
                ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new TouchHelperCallback());
                this.itemTouchHelper = itemTouchHelper;
                itemTouchHelper.attachToRecyclerView(this.listView);
                frameLayout.addView(this.listView, LayoutHelper.createFrame(-1, -1.0f));
                this.listView.setAdapter(this.listAdapter);
                this.listView.setOnItemClickListener(new RecyclerListView.OnItemClickListener() {
                    @Override
                    public final void onItemClick(View view, int i3) {
                        StickersActivity.this.lambda$createView$2(context, view, i3);
                    }
                });
                this.listView.setOnItemLongClickListener(new RecyclerListView.OnItemLongClickListener() {
                    @Override
                    public final boolean onItemClick(View view, int i3) {
                        boolean lambda$createView$3;
                        lambda$createView$3 = StickersActivity.this.lambda$createView$3(view, i3);
                        return lambda$createView$3;
                    }
                });
                return this.fragmentView;
            }
            actionBar = this.actionBar;
            i = R.string.Masks;
        }
        actionBar.setTitle(LocaleController.getString(i));
        this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
            @Override
            public void onItemClick(int i3) {
                if (i3 == -1) {
                    if (StickersActivity.this.onBackPressed()) {
                        StickersActivity.this.lambda$onBackPressed$323();
                    }
                } else if (i3 == 0 || i3 == 1 || i3 == 2) {
                    if (StickersActivity.this.needReorder) {
                        StickersActivity.this.sendReorder();
                    } else if (StickersActivity.this.activeReorderingRequests == 0) {
                        StickersActivity.this.listAdapter.processSelectionMenu(i3);
                    }
                }
            }
        });
        ActionBarMenu createActionMode2 = this.actionBar.createActionMode();
        NumberTextView numberTextView2 = new NumberTextView(createActionMode2.getContext());
        this.selectedCountTextView = numberTextView2;
        numberTextView2.setTextSize(18);
        this.selectedCountTextView.setTypeface(AndroidUtilities.bold());
        this.selectedCountTextView.setTextColor(Theme.getColor(Theme.key_actionBarActionModeDefaultIcon));
        createActionMode2.addView(this.selectedCountTextView, LayoutHelper.createLinear(0, -1, 1.0f, 72, 0, 0, 0));
        this.selectedCountTextView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public final boolean onTouch(View view, MotionEvent motionEvent) {
                boolean lambda$createView$0;
                lambda$createView$0 = StickersActivity.lambda$createView$0(view, motionEvent);
                return lambda$createView$0;
            }
        });
        this.shareMenuItem = createActionMode2.addItemWithWidth(2, R.drawable.msg_share, AndroidUtilities.dp(54.0f));
        this.archiveMenuItem = createActionMode2.addItemWithWidth(0, R.drawable.msg_archive, AndroidUtilities.dp(54.0f));
        this.deleteMenuItem = createActionMode2.addItemWithWidth(1, R.drawable.msg_delete, AndroidUtilities.dp(54.0f));
        if (this.currentType == 5) {
        }
        ArrayList arrayList2 = new ArrayList(MessagesController.getInstance(this.currentAccount).filterPremiumStickers(MediaDataController.getInstance(this.currentAccount).getStickerSets(this.currentType)));
        this.listAdapter = new ListAdapter(context, arrayList2, getFeaturedSets());
        FrameLayout frameLayout2 = new FrameLayout(context);
        this.fragmentView = frameLayout2;
        frameLayout2.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundGray));
        RecyclerListView recyclerListView2 = new RecyclerListView(context) {
            @Override
            public void dispatchDraw(Canvas canvas) {
                if (((BaseFragment) StickersActivity.this).actionBar.isActionModeShowed()) {
                    drawSectionBackground(canvas, StickersActivity.this.stickersHeaderRow, StickersActivity.this.stickersEndRow - 1, getThemedColor(Theme.key_windowBackgroundWhite));
                }
                super.dispatchDraw(canvas);
            }
        };
        this.listView = recyclerListView2;
        recyclerListView2.setFocusable(true);
        this.listView.setTag(7);
        DefaultItemAnimator defaultItemAnimator2 = new DefaultItemAnimator() {
            @Override
            public void onMoveAnimationUpdate(RecyclerView.ViewHolder viewHolder) {
                super.onMoveAnimationUpdate(viewHolder);
                StickersActivity.this.listView.invalidate();
            }
        };
        defaultItemAnimator2.setMoveDuration(350L);
        defaultItemAnimator2.setSupportsChangeAnimations(false);
        defaultItemAnimator2.setMoveInterpolator(CubicBezierInterpolator.EASE_OUT_QUINT);
        this.listView.setItemAnimator(defaultItemAnimator2);
        LinearLayoutManager linearLayoutManager2 = new LinearLayoutManager(context) {
            @Override
            public void calculateExtraLayoutSpace(RecyclerView.State state, int[] iArr) {
                iArr[1] = StickersActivity.this.listView.getHeight();
            }

            @Override
            public boolean supportsPredictiveItemAnimations() {
                return false;
            }
        };
        this.layoutManager = linearLayoutManager2;
        linearLayoutManager2.setOrientation(1);
        this.listView.setLayoutManager(this.layoutManager);
        ItemTouchHelper itemTouchHelper2 = new ItemTouchHelper(new TouchHelperCallback());
        this.itemTouchHelper = itemTouchHelper2;
        itemTouchHelper2.attachToRecyclerView(this.listView);
        frameLayout2.addView(this.listView, LayoutHelper.createFrame(-1, -1.0f));
        this.listView.setAdapter(this.listAdapter);
        this.listView.setOnItemClickListener(new RecyclerListView.OnItemClickListener() {
            @Override
            public final void onItemClick(View view, int i3) {
                StickersActivity.this.lambda$createView$2(context, view, i3);
            }
        });
        this.listView.setOnItemLongClickListener(new RecyclerListView.OnItemLongClickListener() {
            @Override
            public final boolean onItemClick(View view, int i3) {
                boolean lambda$createView$3;
                lambda$createView$3 = StickersActivity.this.lambda$createView$3(view, i3);
                return lambda$createView$3;
            }
        });
        return this.fragmentView;
    }

    @Override
    public void didReceivedNotification(int i, int i2, Object... objArr) {
        if (i != NotificationCenter.stickersDidLoad) {
            if (i == NotificationCenter.featuredStickersDidLoad || i == NotificationCenter.featuredEmojiDidLoad || (i == NotificationCenter.archivedStickersCountDidLoad && ((Integer) objArr[0]).intValue() == this.currentType)) {
                updateRows(false);
                return;
            }
            return;
        }
        int intValue = ((Integer) objArr[0]).intValue();
        int i3 = this.currentType;
        if (intValue == i3) {
            this.listAdapter.loadingFeaturedStickerSets.clear();
            updateRows(((Boolean) objArr[1]).booleanValue());
        } else if (i3 == 0 && intValue == 1) {
            this.listAdapter.notifyItemChanged(this.masksRow);
        }
    }

    @Override
    public ArrayList getThemeDescriptions() {
        ArrayList arrayList = new ArrayList();
        int i = Theme.key_windowBackgroundWhite;
        arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_CELLBACKGROUNDCOLOR, new Class[]{StickerSetCell.class, TextSettingsCell.class, TextCheckCell.class}, null, null, null, i));
        arrayList.add(new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, Theme.key_windowBackgroundGray));
        ActionBar actionBar = this.actionBar;
        int i2 = ThemeDescription.FLAG_BACKGROUND;
        int i3 = Theme.key_actionBarDefault;
        arrayList.add(new ThemeDescription(actionBar, i2, null, null, null, null, i3));
        arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_LISTGLOWCOLOR, null, null, null, null, i3));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, null, null, null, null, Theme.key_actionBarDefaultIcon));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, null, null, null, null, Theme.key_actionBarDefaultTitle));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, null, null, null, null, Theme.key_actionBarDefaultSelector));
        ActionBar actionBar2 = this.actionBar;
        int i4 = ThemeDescription.FLAG_AB_AM_ITEMSCOLOR;
        int i5 = Theme.key_actionBarActionModeDefaultIcon;
        arrayList.add(new ThemeDescription(actionBar2, i4, null, null, null, null, i5));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_AM_BACKGROUND, null, null, null, null, Theme.key_actionBarActionModeDefault));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_AM_TOPBACKGROUND, null, null, null, null, Theme.key_actionBarActionModeDefaultTop));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_AM_SELECTORCOLOR, null, null, null, null, Theme.key_actionBarActionModeDefaultSelector));
        arrayList.add(new ThemeDescription(this.selectedCountTextView, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, i5));
        arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_SELECTOR, null, null, null, null, Theme.key_listSelector));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{View.class}, Theme.dividerPaint, null, null, Theme.key_divider));
        int i6 = Theme.key_windowBackgroundWhiteBlackText;
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{TextCheckCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, i6));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{TextCheckCell.class}, new String[]{"checkBox"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_switchTrack));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{TextCheckCell.class}, new String[]{"checkBox"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_switchTrackChecked));
        int i7 = Theme.key_windowBackgroundGrayShadow;
        arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{TextInfoPrivacyCell.class}, null, null, null, i7));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{TextInfoPrivacyCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteGrayText4));
        arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_LINKCOLOR, new Class[]{TextInfoPrivacyCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteLinkText));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{TextSettingsCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, i6));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{TextSettingsCell.class}, new String[]{"valueTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteValueText));
        arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{ShadowSectionCell.class}, null, null, null, i7));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{StickerSetCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, i6));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{StickerSetCell.class}, new String[]{"valueTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteGrayText2));
        arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_USEBACKGROUNDDRAWABLE | ThemeDescription.FLAG_DRAWABLESELECTEDSTATE, new Class[]{StickerSetCell.class}, new String[]{"optionsButton"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_stickers_menuSelector));
        int i8 = Theme.key_stickers_menu;
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{StickerSetCell.class}, new String[]{"optionsButton"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, i8));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{StickerSetCell.class}, new String[]{"reorderButton"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, i8));
        arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_CHECKBOX, new Class[]{StickerSetCell.class}, new String[]{"checkBox"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, i));
        arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_CHECKBOXCHECK, new Class[]{StickerSetCell.class}, new String[]{"checkBox"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_checkboxCheck));
        TrendingStickersAlert trendingStickersAlert = this.trendingStickersAlert;
        if (trendingStickersAlert != null) {
            arrayList.addAll(trendingStickersAlert.getThemeDescriptions());
        }
        return arrayList;
    }

    @Override
    public boolean onBackPressed() {
        if (!this.listAdapter.hasSelected()) {
            return super.onBackPressed();
        }
        this.listAdapter.clearSelected();
        return false;
    }

    @Override
    public boolean onFragmentCreate() {
        super.onFragmentCreate();
        MediaDataController.getInstance(this.currentAccount).checkStickers(this.currentType);
        int i = this.currentType;
        if (i == 0) {
            MediaDataController.getInstance(this.currentAccount).checkFeaturedStickers();
            MediaDataController.getInstance(this.currentAccount).checkStickers(1);
            MediaDataController.getInstance(this.currentAccount).checkStickers(5);
        } else if (i == 6) {
            MediaDataController.getInstance(this.currentAccount).checkFeaturedEmoji();
            NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.featuredEmojiDidLoad);
        }
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.stickersDidLoad);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.archivedStickersCountDidLoad);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.featuredStickersDidLoad);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.currentUserPremiumStatusChanged);
        updateRows(false);
        return true;
    }

    @Override
    public void onFragmentDestroy() {
        super.onFragmentDestroy();
        if (this.currentType == 6) {
            NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.featuredEmojiDidLoad);
        }
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.stickersDidLoad);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.archivedStickersCountDidLoad);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.featuredStickersDidLoad);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.currentUserPremiumStatusChanged);
    }

    @Override
    public void onResume() {
        super.onResume();
        ListAdapter listAdapter = this.listAdapter;
        if (listAdapter != null) {
            listAdapter.notifyDataSetChanged();
        }
    }
}
