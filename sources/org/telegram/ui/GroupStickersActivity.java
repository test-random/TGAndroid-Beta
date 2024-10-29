package org.telegram.ui;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MediaDataController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.MessagesStorage;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.R;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.ActionBarMenuItem;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ActionBar.ThemeDescription;
import org.telegram.ui.Cells.HeaderCell;
import org.telegram.ui.Cells.ShadowSectionCell;
import org.telegram.ui.Cells.StickerSetCell;
import org.telegram.ui.Cells.TextInfoPrivacyCell;
import org.telegram.ui.Cells.TextSettingsCell;
import org.telegram.ui.Components.BulletinFactory;
import org.telegram.ui.Components.CombinedDrawable;
import org.telegram.ui.Components.EditTextCaption;
import org.telegram.ui.Components.FlickerLoadingView;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.Components.StickerEmptyView;
import org.telegram.ui.Components.StickersAlert;
import org.telegram.ui.Components.VerticalPositionAutoAnimator;
import org.telegram.ui.GroupStickersActivity;

public class GroupStickersActivity extends BaseFragment implements NotificationCenter.NotificationCenterDelegate {
    private AddEmojiCell addEmojiCell;
    private int addEmojiPackHintRow;
    private int addEmojiPackRow;
    private int addEmojiPackTitleRow;
    private final long chatId;
    private int currentEmojiPackRow;
    private FrameLayout emptyFrameView;
    private StickerEmptyView emptyView;
    private int headerRow;
    private TLRPC.ChatFull info;
    private int infoRow;
    private boolean isEmoji;
    private LinearLayoutManager layoutManager;
    private ListAdapter listAdapter;
    private RecyclerListView listView;
    private FlickerLoadingView loadingView;
    private boolean removeStickerSet;
    private int rowCount;
    private SearchAdapter searchAdapter;
    private ActionBarMenuItem searchItem;
    private boolean searching;
    private TLRPC.TL_messages_stickerSet selectedStickerSet;
    private int selectedStickerSetIndex = -1;
    private int stickersEndRow;
    private int stickersStartRow;

    public class AnonymousClass4 implements StickersAlert.StickersAlertCustomButtonDelegate {
        final boolean val$isSelected;
        final TLRPC.TL_messages_stickerSet val$stickerSet;

        AnonymousClass4(boolean z, TLRPC.TL_messages_stickerSet tL_messages_stickerSet) {
            this.val$isSelected = z;
            this.val$stickerSet = tL_messages_stickerSet;
        }

        public void lambda$onCustomButtonPressed$0() {
            BulletinFactory.of(GroupStickersActivity.this).createSimpleBulletin(R.raw.done, LocaleController.getString(R.string.GroupsEmojiPackUpdated)).show();
        }

        @Override
        public int getCustomButtonColorKey() {
            if (this.val$isSelected) {
                return -1;
            }
            return Theme.key_featuredStickers_addButton;
        }

        @Override
        public int getCustomButtonRippleColorKey() {
            if (this.val$isSelected) {
                return -1;
            }
            return Theme.key_featuredStickers_addButtonPressed;
        }

        @Override
        public String getCustomButtonText() {
            if (GroupStickersActivity.this.isEmoji) {
                return LocaleController.getString(this.val$isSelected ? R.string.RemoveGroupEmojiPackSet : R.string.SetAsGroupEmojiPackSet);
            }
            return LocaleController.getString(this.val$isSelected ? R.string.RemoveGroupStickerSet : R.string.SetAsGroupStickerSet);
        }

        @Override
        public int getCustomButtonTextColorKey() {
            return this.val$isSelected ? Theme.key_text_RedBold : Theme.key_featuredStickers_buttonText;
        }

        @Override
        public boolean onCustomButtonPressed() {
            int findFirstVisibleItemPosition = GroupStickersActivity.this.layoutManager.findFirstVisibleItemPosition();
            RecyclerListView.Holder holder = (RecyclerListView.Holder) GroupStickersActivity.this.listView.findViewHolderForAdapterPosition(findFirstVisibleItemPosition);
            int top = holder != null ? holder.itemView.getTop() : Integer.MAX_VALUE;
            int i = GroupStickersActivity.this.selectedStickerSetIndex;
            if (this.val$isSelected) {
                GroupStickersActivity.this.selectedStickerSet = null;
                GroupStickersActivity.this.removeStickerSet = true;
            } else {
                GroupStickersActivity.this.selectedStickerSet = this.val$stickerSet;
                GroupStickersActivity.this.removeStickerSet = false;
            }
            if (GroupStickersActivity.this.isEmoji) {
                AndroidUtilities.runOnUIThread(new Runnable() {
                    @Override
                    public final void run() {
                        GroupStickersActivity.AnonymousClass4.this.lambda$onCustomButtonPressed$0();
                    }
                }, 350L);
            }
            GroupStickersActivity.this.updateSelectedStickerSetIndex();
            GroupStickersActivity groupStickersActivity = GroupStickersActivity.this;
            groupStickersActivity.updateCurrentPackVisibility(groupStickersActivity.selectedStickerSet, true);
            if (i != -1) {
                if (!GroupStickersActivity.this.searching) {
                    for (int i2 = 0; i2 < GroupStickersActivity.this.listView.getChildCount(); i2++) {
                        View childAt = GroupStickersActivity.this.listView.getChildAt(i2);
                        if (GroupStickersActivity.this.listView.getChildViewHolder(childAt).getAdapterPosition() == GroupStickersActivity.this.stickersStartRow + i) {
                            ((StickerSetCell) childAt).setChecked(false, true);
                            break;
                        }
                    }
                }
                GroupStickersActivity.this.listAdapter.notifyItemChanged(i);
            }
            if (GroupStickersActivity.this.selectedStickerSetIndex != -1) {
                if (!GroupStickersActivity.this.searching) {
                    for (int i3 = 0; i3 < GroupStickersActivity.this.listView.getChildCount(); i3++) {
                        View childAt2 = GroupStickersActivity.this.listView.getChildAt(i3);
                        if (GroupStickersActivity.this.listView.getChildViewHolder(childAt2).getAdapterPosition() == GroupStickersActivity.this.stickersStartRow + GroupStickersActivity.this.selectedStickerSetIndex) {
                            ((StickerSetCell) childAt2).setChecked(true, true);
                            break;
                        }
                    }
                }
                GroupStickersActivity.this.listAdapter.notifyItemChanged(GroupStickersActivity.this.selectedStickerSetIndex);
            }
            if (top != Integer.MAX_VALUE && !GroupStickersActivity.this.isEmoji) {
                GroupStickersActivity.this.layoutManager.scrollToPositionWithOffset(findFirstVisibleItemPosition + 1, top);
            }
            if (GroupStickersActivity.this.searching) {
                GroupStickersActivity.this.searchItem.setSearchFieldText("", false);
                ((BaseFragment) GroupStickersActivity.this).actionBar.closeSearchField(true);
            }
            return true;
        }
    }

    public class AddEmojiCell extends LinearLayout {
        private final EditTextCaption editText;
        private Runnable lastCallback;
        private String lastQuery;
        private boolean needDivider;
        private int reqId;
        private final TextWatcher textWatcher;

        public class AnonymousClass1 implements TextWatcher {
            AnonymousClass1() {
            }

            public void lambda$afterTextChanged$0(TLObject tLObject) {
                if (tLObject != null) {
                    GroupStickersActivity.this.selectSetAfterSearch((TLRPC.TL_messages_stickerSet) tLObject);
                } else {
                    GroupStickersActivity.this.selectSetAfterSearch(null);
                }
            }

            public void lambda$afterTextChanged$1(String str, final TLObject tLObject, TLRPC.TL_error tL_error) {
                if (Objects.equals(AddEmojiCell.this.lastQuery, str)) {
                    AndroidUtilities.runOnUIThread(new Runnable() {
                        @Override
                        public final void run() {
                            GroupStickersActivity.AddEmojiCell.AnonymousClass1.this.lambda$afterTextChanged$0(tLObject);
                        }
                    });
                }
            }

            public void lambda$afterTextChanged$2(final String str) {
                AddEmojiCell.this.lastQuery = str;
                TLRPC.TL_messages_getStickerSet tL_messages_getStickerSet = new TLRPC.TL_messages_getStickerSet();
                TLRPC.TL_inputStickerSetShortName tL_inputStickerSetShortName = new TLRPC.TL_inputStickerSetShortName();
                tL_messages_getStickerSet.stickerset = tL_inputStickerSetShortName;
                tL_inputStickerSetShortName.short_name = str;
                AddEmojiCell addEmojiCell = AddEmojiCell.this;
                addEmojiCell.reqId = GroupStickersActivity.this.getConnectionsManager().sendRequest(tL_messages_getStickerSet, new RequestDelegate() {
                    @Override
                    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                        GroupStickersActivity.AddEmojiCell.AnonymousClass1.this.lambda$afterTextChanged$1(str, tLObject, tL_error);
                    }
                }, 66);
            }

            @Override
            public void afterTextChanged(Editable editable) {
                final String trim = editable.toString().trim();
                if (AddEmojiCell.this.reqId != 0) {
                    GroupStickersActivity.this.getConnectionsManager().cancelRequest(AddEmojiCell.this.reqId, true);
                    AddEmojiCell.this.reqId = 0;
                }
                if (AddEmojiCell.this.lastCallback != null) {
                    AndroidUtilities.cancelRunOnUIThread(AddEmojiCell.this.lastCallback);
                }
                AddEmojiCell.this.lastQuery = null;
                if (trim.isEmpty()) {
                    GroupStickersActivity.this.selectSetAfterSearch(null);
                } else {
                    AndroidUtilities.runOnUIThread(AddEmojiCell.this.lastCallback = new Runnable() {
                        @Override
                        public final void run() {
                            GroupStickersActivity.AddEmojiCell.AnonymousClass1.this.lambda$afterTextChanged$2(trim);
                        }
                    }, 300L);
                }
            }

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            }
        }

        public AddEmojiCell(Context context) {
            super(context);
            this.textWatcher = new AnonymousClass1();
            TextView textView = new TextView(context);
            textView.setTextSize(1, 16.0f);
            textView.setTextColor(Theme.getColor(Theme.key_dialogTextBlack));
            textView.setText("t.me/addemoji/");
            EditTextCaption editTextCaption = new EditTextCaption(context, null);
            this.editText = editTextCaption;
            editTextCaption.setLines(1);
            editTextCaption.setSingleLine(true);
            editTextCaption.setInputType(16384);
            editTextCaption.setTextSize(1, 16.0f);
            editTextCaption.setTextColor(Theme.getColor(Theme.key_chat_messagePanelText));
            editTextCaption.setLinkTextColor(Theme.getColor(Theme.key_chat_messageLinkOut));
            editTextCaption.setHighlightColor(Theme.getColor(Theme.key_chat_inTextSelectionHighlight));
            int i = Theme.key_chat_messagePanelHint;
            editTextCaption.setHintColor(Theme.getColor(i));
            editTextCaption.setHintTextColor(Theme.getColor(i));
            editTextCaption.setCursorColor(Theme.getColor(Theme.key_chat_messagePanelCursor));
            editTextCaption.setHandlesColor(Theme.getColor(Theme.key_chat_TextSelectionCursor));
            editTextCaption.setBackground(null);
            editTextCaption.setHint(LocaleController.getString(R.string.AddEmojiPackLinkHint));
            addView(textView, LayoutHelper.createLinear(-2, -2, 16, 20, 0, 0, 0));
            addView(editTextCaption, LayoutHelper.createLinear(-1, -2, 16, -4, 0, 0, 0));
            setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
            setPadding(0, AndroidUtilities.dp(5.0f), 0, AndroidUtilities.dp(5.0f));
            setWillNotDraw(false);
        }

        public void bind(boolean z, TLRPC.TL_messages_stickerSet tL_messages_stickerSet) {
            this.needDivider = z;
            this.editText.removeTextChangedListener(this.textWatcher);
            if (tL_messages_stickerSet == null) {
                this.editText.setText("");
            } else {
                String str = tL_messages_stickerSet.set.short_name;
                this.editText.setText(str);
                this.editText.setSelection(str.length());
            }
            this.editText.addTextChangedListener(this.textWatcher);
        }

        @Override
        protected void onDraw(Canvas canvas) {
            if (this.needDivider) {
                canvas.drawLine(AndroidUtilities.dp(20.0f), getHeight() - 1, getWidth() - getPaddingRight(), getHeight() - 1, Theme.dividerPaint);
            }
        }

        public void setNeedDivider(boolean z) {
            this.needDivider = z;
            invalidate();
        }
    }

    public class ListAdapter extends RecyclerListView.SelectionAdapter {
        private final Context mContext;

        public ListAdapter(Context context) {
            this.mContext = context;
        }

        public void lambda$onBindViewHolder$0(View view) {
            GroupStickersActivity.this.selectSetAfterSearch(null);
        }

        @Override
        public int getItemCount() {
            return GroupStickersActivity.this.rowCount;
        }

        @Override
        public int getItemViewType(int i) {
            if ((i >= GroupStickersActivity.this.stickersStartRow && i < GroupStickersActivity.this.stickersEndRow) || i == GroupStickersActivity.this.currentEmojiPackRow) {
                return 0;
            }
            if (i == GroupStickersActivity.this.headerRow || i == GroupStickersActivity.this.addEmojiPackTitleRow) {
                return 4;
            }
            if (i == GroupStickersActivity.this.infoRow || i == GroupStickersActivity.this.addEmojiPackHintRow) {
                return 1;
            }
            return i == GroupStickersActivity.this.addEmojiPackRow ? 5 : 0;
        }

        @Override
        public boolean isEnabled(RecyclerView.ViewHolder viewHolder) {
            return viewHolder.getItemViewType() == 0;
        }

        @Override
        public void onBindViewHolder(androidx.recyclerview.widget.RecyclerView.ViewHolder r9, int r10) {
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.GroupStickersActivity.ListAdapter.onBindViewHolder(androidx.recyclerview.widget.RecyclerView$ViewHolder, int):void");
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View stickerSetCell;
            if (i != 0) {
                if (i == 1) {
                    stickerSetCell = new TextInfoPrivacyCell(this.mContext);
                    stickerSetCell.setBackground(Theme.getThemedDrawableByKey(this.mContext, R.drawable.greydivider_bottom, Theme.key_windowBackgroundGrayShadow));
                } else if (i != 5) {
                    stickerSetCell = new HeaderCell(this.mContext);
                } else {
                    GroupStickersActivity.this.addEmojiCell = new AddEmojiCell(this.mContext);
                    stickerSetCell = GroupStickersActivity.this.addEmojiCell;
                }
                stickerSetCell.setLayoutParams(new RecyclerView.LayoutParams(-1, -2));
                return new RecyclerListView.Holder(stickerSetCell);
            }
            stickerSetCell = new StickerSetCell(this.mContext, 3);
            stickerSetCell.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
            stickerSetCell.setLayoutParams(new RecyclerView.LayoutParams(-1, -2));
            return new RecyclerListView.Holder(stickerSetCell);
        }
    }

    public class SearchAdapter extends RecyclerListView.SelectionAdapter {
        private Runnable lastCallback;
        private String lastQuery;
        private Context mContext;
        private int reqId;
        private List searchEntries = new ArrayList();
        private List localSearchEntries = new ArrayList();

        public SearchAdapter(Context context) {
            this.mContext = context;
            setHasStableIds(true);
        }

        private void changeBackgroundColor(String str) {
            if (GroupStickersActivity.this.isEmoji) {
                if (TextUtils.isEmpty(str)) {
                    GroupStickersActivity.this.listView.setBackground(null);
                } else {
                    GroupStickersActivity.this.listView.setBackgroundColor(GroupStickersActivity.this.getThemedColor(Theme.key_windowBackgroundWhite));
                }
            }
        }

        public void lambda$onSearchStickers$0(List list, List list2, String str) {
            this.searchEntries = list;
            this.localSearchEntries = list2;
            notifyDataSetChanged();
            GroupStickersActivity.this.emptyView.title.setVisibility(8);
            GroupStickersActivity.this.emptyView.subtitle.setText(LocaleController.formatString(R.string.ChooseStickerNoResultsFound, str));
            GroupStickersActivity.this.emptyView.showProgress(false, true);
        }

        public void lambda$onSearchStickers$1(String str, final String str2, TLObject tLObject, TLRPC.TL_error tL_error) {
            if (Objects.equals(this.lastQuery, str) && (tLObject instanceof TLRPC.TL_messages_foundStickerSets)) {
                final ArrayList arrayList = new ArrayList();
                Iterator<TLRPC.StickerSetCovered> it = ((TLRPC.TL_messages_foundStickerSets) tLObject).sets.iterator();
                while (it.hasNext()) {
                    TLRPC.StickerSetCovered next = it.next();
                    TLRPC.TL_messages_stickerSet tL_messages_stickerSet = new TLRPC.TL_messages_stickerSet();
                    tL_messages_stickerSet.set = next.set;
                    tL_messages_stickerSet.documents = next.covers;
                    if (!GroupStickersActivity.this.isEmoji || tL_messages_stickerSet.set.emojis) {
                        arrayList.add(tL_messages_stickerSet);
                    }
                }
                String trim = str2.toLowerCase(Locale.ROOT).trim();
                final ArrayList arrayList2 = new ArrayList();
                Iterator<TLRPC.TL_messages_stickerSet> it2 = MediaDataController.getInstance(((BaseFragment) GroupStickersActivity.this).currentAccount).getStickerSets(GroupStickersActivity.this.getStickerSetType()).iterator();
                while (it2.hasNext()) {
                    TLRPC.TL_messages_stickerSet next2 = it2.next();
                    String str3 = next2.set.short_name;
                    Locale locale = Locale.ROOT;
                    if (str3.toLowerCase(locale).contains(trim) || next2.set.title.toLowerCase(locale).contains(trim)) {
                        arrayList2.add(next2);
                    }
                }
                AndroidUtilities.runOnUIThread(new Runnable() {
                    @Override
                    public final void run() {
                        GroupStickersActivity.SearchAdapter.this.lambda$onSearchStickers$0(arrayList, arrayList2, str2);
                    }
                });
            }
        }

        public void lambda$onSearchStickers$2(final String str) {
            TLRPC.TL_messages_searchStickerSets tL_messages_searchStickerSets;
            this.lastQuery = str;
            if (GroupStickersActivity.this.isEmoji) {
                TLRPC.TL_messages_searchEmojiStickerSets tL_messages_searchEmojiStickerSets = new TLRPC.TL_messages_searchEmojiStickerSets();
                tL_messages_searchEmojiStickerSets.q = str;
                tL_messages_searchStickerSets = tL_messages_searchEmojiStickerSets;
            } else {
                TLRPC.TL_messages_searchStickerSets tL_messages_searchStickerSets2 = new TLRPC.TL_messages_searchStickerSets();
                tL_messages_searchStickerSets2.q = str;
                tL_messages_searchStickerSets = tL_messages_searchStickerSets2;
            }
            this.reqId = GroupStickersActivity.this.getConnectionsManager().sendRequest(tL_messages_searchStickerSets, new RequestDelegate() {
                @Override
                public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                    GroupStickersActivity.SearchAdapter.this.lambda$onSearchStickers$1(str, str, tLObject, tL_error);
                }
            }, 66);
        }

        public void onSearchStickers(final String str) {
            changeBackgroundColor(str);
            if (this.reqId != 0) {
                GroupStickersActivity.this.getConnectionsManager().cancelRequest(this.reqId, true);
                this.reqId = 0;
            }
            Runnable runnable = this.lastCallback;
            if (runnable != null) {
                AndroidUtilities.cancelRunOnUIThread(runnable);
                this.lastCallback = null;
            }
            this.lastQuery = null;
            int itemCount = getItemCount();
            if (itemCount > 0) {
                this.searchEntries.clear();
                this.localSearchEntries.clear();
                notifyItemRangeRemoved(0, itemCount);
            }
            if (TextUtils.isEmpty(str)) {
                GroupStickersActivity.this.emptyView.setVisibility(8);
                GroupStickersActivity.this.emptyView.showProgress(false, true);
                return;
            }
            if (GroupStickersActivity.this.emptyView.getVisibility() != 0) {
                GroupStickersActivity.this.emptyView.setVisibility(0);
                GroupStickersActivity.this.emptyView.showProgress(true, false);
            } else {
                GroupStickersActivity.this.emptyView.showProgress(true, true);
            }
            Runnable runnable2 = new Runnable() {
                @Override
                public final void run() {
                    GroupStickersActivity.SearchAdapter.this.lambda$onSearchStickers$2(str);
                }
            };
            this.lastCallback = runnable2;
            AndroidUtilities.runOnUIThread(runnable2, 300L);
        }

        @Override
        public int getItemCount() {
            return this.searchEntries.size() + this.localSearchEntries.size() + (!this.localSearchEntries.isEmpty() ? 1 : 0);
        }

        @Override
        public long getItemId(int i) {
            if (getItemViewType(i) != 0) {
                return -1L;
            }
            List list = i > this.searchEntries.size() ? this.localSearchEntries : this.searchEntries;
            if (i > this.searchEntries.size()) {
                i = (i - this.searchEntries.size()) - 1;
            }
            return ((TLRPC.TL_messages_stickerSet) list.get(i)).set.id;
        }

        @Override
        public int getItemViewType(int i) {
            return this.searchEntries.size() == i ? 1 : 0;
        }

        @Override
        public boolean isEnabled(RecyclerView.ViewHolder viewHolder) {
            return getItemViewType(viewHolder.getAdapterPosition()) == 0;
        }

        @Override
        public void onBindViewHolder(androidx.recyclerview.widget.RecyclerView.ViewHolder r8, int r9) {
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.GroupStickersActivity.SearchAdapter.onBindViewHolder(androidx.recyclerview.widget.RecyclerView$ViewHolder, int):void");
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            StickerSetCell stickerSetCell;
            if (i != 0) {
                HeaderCell headerCell = new HeaderCell(this.mContext, Theme.key_windowBackgroundWhiteGrayText4, 21, 0, 0, false, GroupStickersActivity.this.getResourceProvider());
                CombinedDrawable combinedDrawable = new CombinedDrawable(new ColorDrawable(GroupStickersActivity.this.getThemedColor(Theme.key_windowBackgroundGray)), Theme.getThemedDrawableByKey(this.mContext, R.drawable.greydivider_bottom, Theme.key_windowBackgroundGrayShadow));
                combinedDrawable.setFullsize(true);
                headerCell.setBackground(combinedDrawable);
                headerCell.setText(LocaleController.getString(GroupStickersActivity.this.isEmoji ? R.string.ChooseStickerMyEmojiPacks : R.string.ChooseStickerMyStickerSets));
                stickerSetCell = headerCell;
            } else {
                StickerSetCell stickerSetCell2 = new StickerSetCell(this.mContext, 3);
                stickerSetCell2.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                stickerSetCell = stickerSetCell2;
            }
            stickerSetCell.setLayoutParams(new RecyclerView.LayoutParams(-1, -2));
            return new RecyclerListView.Holder(stickerSetCell);
        }
    }

    public GroupStickersActivity(long j) {
        this.chatId = j;
    }

    public GroupStickersActivity(long j, boolean z) {
        this.chatId = j;
        this.isEmoji = z;
    }

    public TLRPC.StickerSet getStickerSet(TLRPC.ChatFull chatFull) {
        if (chatFull == null) {
            return null;
        }
        return this.isEmoji ? chatFull.emojiset : chatFull.stickerset;
    }

    public int getStickerSetType() {
        return this.isEmoji ? 5 : 0;
    }

    public void lambda$createView$0(View view, int i) {
        if (getParentActivity() == null) {
            return;
        }
        if (this.searching) {
            if (i > this.searchAdapter.searchEntries.size()) {
                onStickerSetClicked(((StickerSetCell) view).isChecked(), (TLRPC.TL_messages_stickerSet) this.searchAdapter.localSearchEntries.get((i - this.searchAdapter.searchEntries.size()) - 1), false);
                return;
            } else {
                if (i != this.searchAdapter.searchEntries.size()) {
                    onStickerSetClicked(((StickerSetCell) view).isChecked(), (TLRPC.TL_messages_stickerSet) this.searchAdapter.searchEntries.get(i), true);
                    return;
                }
                return;
            }
        }
        if (i >= this.stickersStartRow && i < this.stickersEndRow) {
            onStickerSetClicked(((StickerSetCell) view).isChecked(), MediaDataController.getInstance(this.currentAccount).getStickerSets(getStickerSetType()).get(i - this.stickersStartRow), false);
        }
        if (i == this.currentEmojiPackRow) {
            onStickerSetClicked(true, this.selectedStickerSet, false);
        }
    }

    public void lambda$saveStickerSet$1(TLRPC.TL_error tL_error) {
        if (tL_error != null) {
            if (getParentActivity() != null) {
                Toast.makeText(getParentActivity(), LocaleController.getString(R.string.ErrorOccurred) + "\n" + tL_error.text, 0).show();
                return;
            }
            return;
        }
        TLRPC.TL_messages_stickerSet tL_messages_stickerSet = this.selectedStickerSet;
        if (tL_messages_stickerSet == null) {
            setStickerSet(null);
        } else {
            setStickerSet(tL_messages_stickerSet.set);
            MediaDataController.getInstance(this.currentAccount).putGroupStickerSet(this.selectedStickerSet);
        }
        updateSelectedStickerSetIndex();
        if (this.isEmoji) {
            TLRPC.ChatFull chatFull = this.info;
            chatFull.flags2 = chatFull.emojiset != null ? chatFull.flags2 | 1024 : chatFull.flags2 & (-1025);
        } else {
            TLRPC.ChatFull chatFull2 = this.info;
            chatFull2.flags = chatFull2.stickerset == null ? chatFull2.flags | 256 : chatFull2.flags & (-257);
        }
        MessagesStorage.getInstance(this.currentAccount).updateChatInfo(this.info, false);
        NotificationCenter.getInstance(this.currentAccount).lambda$postNotificationNameOnUIThread$1(NotificationCenter.chatInfoDidLoad, this.info, 0, Boolean.TRUE, Boolean.FALSE);
        NotificationCenter.getInstance(this.currentAccount).lambda$postNotificationNameOnUIThread$1(NotificationCenter.groupPackUpdated, Long.valueOf(this.info.id), Boolean.valueOf(this.isEmoji));
        lambda$onBackPressed$319();
    }

    public void lambda$saveStickerSet$2(TLObject tLObject, final TLRPC.TL_error tL_error) {
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                GroupStickersActivity.this.lambda$saveStickerSet$1(tL_error);
            }
        });
    }

    private void onStickerSetClicked(boolean z, TLRPC.TL_messages_stickerSet tL_messages_stickerSet, boolean z2) {
        TLRPC.TL_inputStickerSetShortName tL_inputStickerSetShortName;
        if (z2) {
            TLRPC.TL_inputStickerSetShortName tL_inputStickerSetShortName2 = new TLRPC.TL_inputStickerSetShortName();
            tL_inputStickerSetShortName2.short_name = tL_messages_stickerSet.set.short_name;
            tL_inputStickerSetShortName = tL_inputStickerSetShortName2;
        } else {
            tL_inputStickerSetShortName = null;
        }
        StickersAlert stickersAlert = new StickersAlert(getParentActivity(), this, tL_inputStickerSetShortName, !z2 ? tL_messages_stickerSet : null, null, false);
        stickersAlert.setCustomButtonDelegate(new AnonymousClass4(z, tL_messages_stickerSet));
        AndroidUtilities.hideKeyboard(getParentActivity().getCurrentFocus());
        stickersAlert.show();
    }

    private void saveStickerSet() {
        TLRPC.TL_inputStickerSetID tL_inputStickerSetID;
        TLRPC.TL_channels_setStickers tL_channels_setStickers;
        TLRPC.TL_channels_setStickers tL_channels_setStickers2;
        TLRPC.TL_messages_stickerSet tL_messages_stickerSet;
        TLRPC.ChatFull chatFull = this.info;
        if (chatFull != null) {
            if (getStickerSet(chatFull) == null || (tL_messages_stickerSet = this.selectedStickerSet) == null || tL_messages_stickerSet.set.id != getStickerSet(this.info).id) {
                if (getStickerSet(this.info) == null && this.selectedStickerSet == null) {
                    return;
                }
                if (this.isEmoji) {
                    TLRPC.TL_channels_setEmojiStickers tL_channels_setEmojiStickers = new TLRPC.TL_channels_setEmojiStickers();
                    tL_channels_setEmojiStickers.channel = MessagesController.getInstance(this.currentAccount).getInputChannel(this.chatId);
                    if (this.removeStickerSet) {
                        tL_channels_setEmojiStickers.stickerset = new TLRPC.TL_inputStickerSetEmpty();
                        tL_channels_setStickers2 = tL_channels_setEmojiStickers;
                    } else {
                        tL_inputStickerSetID = new TLRPC.TL_inputStickerSetID();
                        tL_channels_setEmojiStickers.stickerset = tL_inputStickerSetID;
                        tL_channels_setStickers = tL_channels_setEmojiStickers;
                        TLRPC.StickerSet stickerSet = this.selectedStickerSet.set;
                        tL_inputStickerSetID.id = stickerSet.id;
                        tL_inputStickerSetID.access_hash = stickerSet.access_hash;
                        tL_channels_setStickers2 = tL_channels_setStickers;
                    }
                } else {
                    TLRPC.TL_channels_setStickers tL_channels_setStickers3 = new TLRPC.TL_channels_setStickers();
                    tL_channels_setStickers3.channel = MessagesController.getInstance(this.currentAccount).getInputChannel(this.chatId);
                    if (this.removeStickerSet) {
                        tL_channels_setStickers3.stickerset = new TLRPC.TL_inputStickerSetEmpty();
                        tL_channels_setStickers2 = tL_channels_setStickers3;
                    } else {
                        MessagesController.getEmojiSettings(this.currentAccount).edit().remove("group_hide_stickers_" + this.info.id).apply();
                        tL_inputStickerSetID = new TLRPC.TL_inputStickerSetID();
                        tL_channels_setStickers3.stickerset = tL_inputStickerSetID;
                        tL_channels_setStickers = tL_channels_setStickers3;
                        TLRPC.StickerSet stickerSet2 = this.selectedStickerSet.set;
                        tL_inputStickerSetID.id = stickerSet2.id;
                        tL_inputStickerSetID.access_hash = stickerSet2.access_hash;
                        tL_channels_setStickers2 = tL_channels_setStickers;
                    }
                }
                ConnectionsManager.getInstance(this.currentAccount).sendRequest(tL_channels_setStickers2, new RequestDelegate() {
                    @Override
                    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                        GroupStickersActivity.this.lambda$saveStickerSet$2(tLObject, tL_error);
                    }
                });
            }
        }
    }

    public void selectSetAfterSearch(TLRPC.TL_messages_stickerSet tL_messages_stickerSet) {
        int i = this.selectedStickerSetIndex;
        if (tL_messages_stickerSet == null) {
            if (this.selectedStickerSet != null) {
                BulletinFactory.of(this).createSimpleBulletin(R.raw.done, LocaleController.getString(R.string.GroupsEmojiPackUpdated)).show();
            }
            this.selectedStickerSet = null;
            this.removeStickerSet = true;
        } else {
            this.selectedStickerSet = tL_messages_stickerSet;
            this.removeStickerSet = false;
            BulletinFactory.of(this).createSimpleBulletin(R.raw.done, LocaleController.getString(R.string.GroupsEmojiPackUpdated)).show();
        }
        updateSelectedStickerSetIndex();
        updateCurrentPackVisibility(this.selectedStickerSet, false);
        if (i != -1) {
            if (!this.searching) {
                for (int i2 = 0; i2 < this.listView.getChildCount(); i2++) {
                    View childAt = this.listView.getChildAt(i2);
                    if (this.listView.getChildViewHolder(childAt).getAdapterPosition() == this.stickersStartRow + i) {
                        ((StickerSetCell) childAt).setChecked(false, true);
                        break;
                    }
                }
            }
            this.listAdapter.notifyItemChanged(this.stickersStartRow + i);
        }
        if (this.selectedStickerSetIndex != -1) {
            if (!this.searching) {
                for (int i3 = 0; i3 < this.listView.getChildCount(); i3++) {
                    View childAt2 = this.listView.getChildAt(i3);
                    if (this.listView.getChildViewHolder(childAt2).getAdapterPosition() == this.stickersStartRow + this.selectedStickerSetIndex) {
                        ((StickerSetCell) childAt2).setChecked(true, true);
                        return;
                    }
                }
            }
            this.listAdapter.notifyItemChanged(this.stickersStartRow + this.selectedStickerSetIndex);
        }
    }

    private void setStickerSet(TLRPC.StickerSet stickerSet) {
        if (this.isEmoji) {
            this.info.emojiset = stickerSet;
        } else {
            this.info.stickerset = stickerSet;
        }
    }

    public void updateCurrentPackVisibility(TLRPC.TL_messages_stickerSet tL_messages_stickerSet, boolean z) {
        if (this.isEmoji) {
            if (tL_messages_stickerSet == null) {
                int i = this.currentEmojiPackRow;
                boolean z2 = i > 0;
                this.selectedStickerSet = null;
                if (z2) {
                    this.listAdapter.notifyItemRemoved(i);
                    if (z) {
                        this.listAdapter.notifyItemChanged(this.addEmojiPackRow);
                    }
                }
                updateRows(false);
                this.addEmojiCell.setNeedDivider(false);
                return;
            }
            boolean z3 = this.currentEmojiPackRow == -1;
            this.selectedStickerSet = tL_messages_stickerSet;
            updateRows(false);
            ListAdapter listAdapter = this.listAdapter;
            int i2 = this.currentEmojiPackRow;
            if (z3) {
                listAdapter.notifyItemInserted(i2);
            } else {
                listAdapter.notifyItemChanged(i2);
            }
            if (z) {
                this.listAdapter.notifyItemChanged(this.addEmojiPackRow);
            }
            this.addEmojiCell.setNeedDivider(true);
        }
    }

    private void updateRows() {
        updateRows(true);
    }

    private void updateRows(boolean z) {
        ListAdapter listAdapter;
        this.addEmojiPackTitleRow = -1;
        this.addEmojiPackRow = -1;
        this.currentEmojiPackRow = -1;
        this.addEmojiPackHintRow = -1;
        this.rowCount = 0;
        if (this.isEmoji) {
            this.addEmojiPackTitleRow = 0;
            this.rowCount = 2;
            this.addEmojiPackRow = 1;
            if (this.selectedStickerSet != null) {
                this.rowCount = 3;
                this.currentEmojiPackRow = 2;
            }
            int i = this.rowCount;
            this.rowCount = i + 1;
            this.addEmojiPackHintRow = i;
        }
        ArrayList<TLRPC.TL_messages_stickerSet> stickerSets = MediaDataController.getInstance(this.currentAccount).getStickerSets(getStickerSetType());
        if (stickerSets.isEmpty()) {
            this.headerRow = -1;
            this.stickersStartRow = -1;
            this.stickersEndRow = -1;
        } else {
            int i2 = this.rowCount;
            int i3 = i2 + 1;
            this.rowCount = i3;
            this.headerRow = i2;
            this.stickersStartRow = i3;
            this.stickersEndRow = i3 + stickerSets.size();
            this.rowCount += stickerSets.size();
        }
        int i4 = this.rowCount;
        this.rowCount = i4 + 1;
        this.infoRow = i4;
        updateSelectedStickerSetIndex();
        if (!z || (listAdapter = this.listAdapter) == null) {
            return;
        }
        listAdapter.notifyDataSetChanged();
    }

    public void updateSelectedStickerSetIndex() {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.GroupStickersActivity.updateSelectedStickerSetIndex():void");
    }

    @Override
    public View createView(Context context) {
        this.actionBar.setBackButtonImage(R.drawable.ic_ab_back);
        this.actionBar.setAllowOverlayTitle(true);
        this.actionBar.setTitle(LocaleController.getString(this.isEmoji ? R.string.GroupEmojiPack : R.string.GroupStickers));
        this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
            @Override
            public void onItemClick(int i) {
                if (i == -1) {
                    GroupStickersActivity.this.lambda$onBackPressed$319();
                }
            }
        });
        ActionBarMenuItem addItem = this.actionBar.createMenu().addItem(0, R.drawable.ic_ab_search);
        this.searchItem = addItem;
        addItem.setIsSearchField(true).setActionBarMenuItemSearchListener(new ActionBarMenuItem.ActionBarMenuItemSearchListener() {
            @Override
            public void onSearchCollapse() {
                if (GroupStickersActivity.this.searching) {
                    GroupStickersActivity.this.searchAdapter.onSearchStickers(null);
                    GroupStickersActivity.this.searching = false;
                    GroupStickersActivity.this.listView.setAdapter(GroupStickersActivity.this.listAdapter);
                }
            }

            @Override
            public void onSearchExpand() {
            }

            @Override
            public void onTextChanged(EditText editText) {
                String obj = editText.getText().toString();
                GroupStickersActivity.this.searchAdapter.onSearchStickers(obj);
                boolean z = !TextUtils.isEmpty(obj);
                if (z != GroupStickersActivity.this.searching) {
                    GroupStickersActivity.this.searching = z;
                    if (GroupStickersActivity.this.listView != null) {
                        GroupStickersActivity.this.listView.setAdapter(GroupStickersActivity.this.searching ? GroupStickersActivity.this.searchAdapter : GroupStickersActivity.this.listAdapter);
                    }
                }
            }
        });
        this.searchItem.setSearchFieldHint(LocaleController.getString(R.string.Search));
        this.listAdapter = new ListAdapter(context);
        this.searchAdapter = new SearchAdapter(context);
        FrameLayout frameLayout = new FrameLayout(context);
        this.fragmentView = frameLayout;
        frameLayout.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundGray));
        this.listView = new RecyclerListView(context);
        DefaultItemAnimator defaultItemAnimator = new DefaultItemAnimator();
        defaultItemAnimator.setDurations(200L);
        defaultItemAnimator.setSupportsChangeAnimations(true);
        this.listView.setItemAnimator(defaultItemAnimator);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
        this.layoutManager = linearLayoutManager;
        linearLayoutManager.setOrientation(1);
        this.listView.setLayoutManager(this.layoutManager);
        FrameLayout frameLayout2 = new FrameLayout(context);
        this.emptyFrameView = frameLayout2;
        frameLayout2.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
        FlickerLoadingView flickerLoadingView = new FlickerLoadingView(context, getResourceProvider());
        this.loadingView = flickerLoadingView;
        flickerLoadingView.setViewType(19);
        this.loadingView.setIsSingleCell(true);
        this.loadingView.setItemsCount((int) Math.ceil(AndroidUtilities.displaySize.y / AndroidUtilities.dpf2(58.0f)));
        this.emptyFrameView.addView(this.loadingView, LayoutHelper.createFrame(-1, -1.0f));
        StickerEmptyView stickerEmptyView = new StickerEmptyView(context, this.loadingView, 1);
        this.emptyView = stickerEmptyView;
        VerticalPositionAutoAnimator.attach(stickerEmptyView);
        this.emptyFrameView.addView(this.emptyView);
        frameLayout.addView(this.emptyFrameView);
        this.emptyFrameView.setVisibility(8);
        this.listView.setEmptyView(this.emptyFrameView);
        frameLayout.addView(this.listView, LayoutHelper.createFrame(-1, -1.0f));
        this.listView.setAdapter(this.listAdapter);
        this.listView.setOnItemClickListener(new RecyclerListView.OnItemClickListener() {
            @Override
            public final void onItemClick(View view, int i) {
                GroupStickersActivity.this.lambda$createView$0(view, i);
            }
        });
        this.listView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int i) {
                if (i == 1) {
                    AndroidUtilities.hideKeyboard(GroupStickersActivity.this.getParentActivity().getCurrentFocus());
                }
            }
        });
        return this.fragmentView;
    }

    @Override
    public void didReceivedNotification(int i, int i2, Object... objArr) {
        if (i == NotificationCenter.stickersDidLoad) {
            if (((Integer) objArr[0]).intValue() != getStickerSetType()) {
                return;
            }
        } else if (i == NotificationCenter.chatInfoDidLoad) {
            TLRPC.ChatFull chatFull = (TLRPC.ChatFull) objArr[0];
            if (chatFull.id != this.chatId) {
                return;
            }
            if (this.info == null && getStickerSet(chatFull) != null) {
                this.selectedStickerSet = MediaDataController.getInstance(this.currentAccount).getGroupStickerSetById(getStickerSet(chatFull));
            }
            this.info = chatFull;
        } else {
            if (i != NotificationCenter.groupStickersDidLoad) {
                return;
            }
            long longValue = ((Long) objArr[0]).longValue();
            if (getStickerSet(this.info) == null || getStickerSet(this.info).id != longValue) {
                return;
            }
        }
        updateRows();
    }

    @Override
    public ArrayList getThemeDescriptions() {
        ArrayList arrayList = new ArrayList();
        arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_CELLBACKGROUNDCOLOR, new Class[]{StickerSetCell.class, TextSettingsCell.class}, null, null, null, Theme.key_windowBackgroundWhite));
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
        arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_LINKCOLOR, new Class[]{TextInfoPrivacyCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteLinkText));
        int i4 = Theme.key_windowBackgroundWhiteBlackText;
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{TextSettingsCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, i4));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{TextSettingsCell.class}, new String[]{"valueTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteValueText));
        arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{ShadowSectionCell.class}, null, null, null, i3));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{StickerSetCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, i4));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{StickerSetCell.class}, new String[]{"valueTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteGrayText2));
        arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_USEBACKGROUNDDRAWABLE | ThemeDescription.FLAG_DRAWABLESELECTEDSTATE, new Class[]{StickerSetCell.class}, new String[]{"optionsButton"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_stickers_menuSelector));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{StickerSetCell.class}, new String[]{"optionsButton"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_stickers_menu));
        return arrayList;
    }

    @Override
    public boolean onFragmentCreate() {
        super.onFragmentCreate();
        MediaDataController.getInstance(this.currentAccount).checkStickers(getStickerSetType());
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.stickersDidLoad);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.chatInfoDidLoad);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.groupStickersDidLoad);
        updateRows();
        return true;
    }

    @Override
    public void onFragmentDestroy() {
        super.onFragmentDestroy();
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.stickersDidLoad);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.chatInfoDidLoad);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.groupStickersDidLoad);
        if (this.selectedStickerSet != null || this.removeStickerSet) {
            saveStickerSet();
        }
    }

    public void setInfo(TLRPC.ChatFull chatFull) {
        this.info = chatFull;
        if (getStickerSet(chatFull) != null) {
            this.selectedStickerSet = MediaDataController.getInstance(this.currentAccount).getGroupStickerSetById(getStickerSet(this.info));
        }
    }
}
