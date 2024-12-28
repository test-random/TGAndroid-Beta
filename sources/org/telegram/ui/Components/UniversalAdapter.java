package org.telegram.ui.Components;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ChatObject;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.R;
import org.telegram.messenger.UserObject;
import org.telegram.messenger.Utilities;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.tgnet.tl.TL_stats;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Business.BusinessLinksActivity;
import org.telegram.ui.Business.QuickRepliesActivity;
import org.telegram.ui.Business.QuickRepliesController;
import org.telegram.ui.Cells.CheckBoxCell;
import org.telegram.ui.Cells.CollapseTextCell;
import org.telegram.ui.Cells.DialogCell;
import org.telegram.ui.Cells.DialogRadioCell;
import org.telegram.ui.Cells.GraySectionCell;
import org.telegram.ui.Cells.HeaderCell;
import org.telegram.ui.Cells.NotificationsCheckCell;
import org.telegram.ui.Cells.ProfileSearchCell;
import org.telegram.ui.Cells.SlideIntChooseView;
import org.telegram.ui.Cells.TextCell;
import org.telegram.ui.Cells.TextCheckCell;
import org.telegram.ui.Cells.TextCheckCell2;
import org.telegram.ui.Cells.TextInfoPrivacyCell;
import org.telegram.ui.Cells.TextRightIconCell;
import org.telegram.ui.Cells.UserCell;
import org.telegram.ui.ChannelMonetizationLayout;
import org.telegram.ui.Charts.BaseChartView;
import org.telegram.ui.Components.LinkSpanDrawable;
import org.telegram.ui.Components.ListView.AdapterWithDiffUtils;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.Components.SlideChooseView;
import org.telegram.ui.Components.UItem;
import org.telegram.ui.StatisticActivity;
import org.telegram.ui.Stories.recorder.HintView2;
import org.telegram.ui.Stories.recorder.StoryPrivacyBottomSheet;

public class UniversalAdapter extends AdapterWithDiffUtils {
    private boolean allowReorder;
    private boolean applyBackground;
    private BaseChartView.SharedUiComponents chartSharedUI;
    private final int classGuid;
    private final Context context;
    private final int currentAccount;
    private Section currentReorderSection;
    private Section currentWhiteSection;
    private final boolean dialog;
    protected Utilities.Callback2 fillItems;
    private final ArrayList items;
    protected final RecyclerListView listView;
    private final ArrayList oldItems;
    private Utilities.Callback2 onReordered;
    private boolean orderChanged;
    private int orderChangedId;
    private final ArrayList reorderSections;
    private final Theme.ResourcesProvider resourcesProvider;
    private final ArrayList whiteSections;

    public class FullscreenCustomFrameLayout extends FrameLayout {
        private int minusHeight;

        public FullscreenCustomFrameLayout(Context context) {
            super(context);
            this.minusHeight = 0;
        }

        @Override
        protected void onMeasure(int i, int i2) {
            int makeMeasureSpec;
            int makeMeasureSpec2;
            int size;
            if ((getParent() instanceof View) && ((View) getParent()).getMeasuredHeight() > 0) {
                View view = (View) getParent();
                makeMeasureSpec = View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(i), 1073741824);
                size = view.getMeasuredHeight();
            } else {
                if (View.MeasureSpec.getMode(i2) == 0) {
                    int size2 = View.MeasureSpec.getSize(i2);
                    makeMeasureSpec = View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(i), 1073741824);
                    measureChildren(makeMeasureSpec, i2);
                    int i3 = 0;
                    for (int i4 = 0; i4 < getChildCount(); i4++) {
                        i3 = Math.max(i3, getChildAt(i4).getMeasuredHeight());
                    }
                    if (size2 > 0) {
                        i3 = Math.min(i3, size2 - this.minusHeight);
                    }
                    makeMeasureSpec2 = View.MeasureSpec.makeMeasureSpec(i3, 1073741824);
                    super.onMeasure(makeMeasureSpec, makeMeasureSpec2);
                }
                makeMeasureSpec = View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(i), 1073741824);
                size = View.MeasureSpec.getSize(i2);
            }
            makeMeasureSpec2 = View.MeasureSpec.makeMeasureSpec(size - this.minusHeight, 1073741824);
            super.onMeasure(makeMeasureSpec, makeMeasureSpec2);
        }

        public void setMinusHeight(int i) {
            this.minusHeight = i;
        }
    }

    public static class Section {
        public int end;
        public int start;

        private Section() {
        }

        public boolean contains(int i) {
            return i >= this.start && i <= this.end;
        }
    }

    public UniversalAdapter(RecyclerListView recyclerListView, Context context, int i, int i2, Utilities.Callback2 callback2, Theme.ResourcesProvider resourcesProvider) {
        this(recyclerListView, context, i, i2, false, callback2, resourcesProvider);
    }

    public UniversalAdapter(RecyclerListView recyclerListView, Context context, int i, int i2, boolean z, Utilities.Callback2 callback2, Theme.ResourcesProvider resourcesProvider) {
        this.applyBackground = true;
        this.oldItems = new ArrayList();
        this.items = new ArrayList();
        this.whiteSections = new ArrayList();
        this.reorderSections = new ArrayList();
        this.listView = recyclerListView;
        this.context = context;
        this.currentAccount = i;
        this.classGuid = i2;
        this.dialog = z;
        this.fillItems = callback2;
        this.resourcesProvider = resourcesProvider;
        update(false);
    }

    private void callReorder(int i) {
        if (i < 0 || i >= this.reorderSections.size()) {
            return;
        }
        Section section = (Section) this.reorderSections.get(i);
        this.onReordered.run(Integer.valueOf(i), new ArrayList(this.items.subList(section.start, section.end + 1)));
        this.orderChanged = false;
    }

    private View findViewByItemObject(Object obj) {
        int i = 0;
        while (true) {
            if (i >= getItemCount()) {
                i = -1;
                break;
            }
            UItem item = getItem(i);
            if (item != null && item.object == obj) {
                break;
            }
            i++;
        }
        if (i == -1) {
            return null;
        }
        for (int i2 = 0; i2 < this.listView.getChildCount(); i2++) {
            View childAt = this.listView.getChildAt(i2);
            int childAdapterPosition = this.listView.getChildAdapterPosition(childAt);
            if (childAdapterPosition != -1 && childAdapterPosition == i) {
                return childAt;
            }
        }
        return null;
    }

    private boolean hasDivider(int i) {
        UItem item = getItem(i);
        UItem item2 = getItem(i + 1);
        return (item == null || item.hideDivider || item2 == null || isShadow(item2.viewType) != isShadow(item.viewType)) ? false : true;
    }

    public static void lambda$onBindViewHolder$1(UItem uItem, int i) {
        Utilities.Callback callback = uItem.intCallback;
        if (callback != null) {
            callback.run(Integer.valueOf(i));
        }
    }

    public StatisticActivity.BaseChartCell lambda$onBindViewHolder$2(UItem uItem) {
        View findViewByItemObject = findViewByItemObject(uItem.object);
        if (findViewByItemObject instanceof StatisticActivity.UniversalChartCell) {
            return (StatisticActivity.UniversalChartCell) findViewByItemObject;
        }
        return null;
    }

    public static void lambda$onBindViewHolder$3(UItem uItem, TextCheckCell2 textCheckCell2) {
        uItem.clickCallback.onClick(textCheckCell2);
    }

    public void lambda$update$0(boolean z) {
        if (this.listView.isComputingLayout()) {
            return;
        }
        if (z) {
            setItems(this.oldItems, this.items);
        } else {
            notifyDataSetChanged();
        }
    }

    private void updateColors(RecyclerView.ViewHolder viewHolder) {
        KeyEvent.Callback callback = viewHolder.itemView;
        if (callback instanceof Theme.Colorable) {
            ((Theme.Colorable) callback).updateColors();
            if (shouldApplyBackground(viewHolder.getItemViewType())) {
                viewHolder.itemView.setBackgroundColor(getThemedColor(this.dialog ? Theme.key_dialogBackground : Theme.key_windowBackgroundWhite));
            }
        }
    }

    public void drawWhiteSections(Canvas canvas, RecyclerListView recyclerListView) {
        for (int i = 0; i < this.whiteSections.size(); i++) {
            Section section = (Section) this.whiteSections.get(i);
            int i2 = section.end;
            if (i2 >= 0) {
                recyclerListView.drawSectionBackground(canvas, section.start, i2, getThemedColor(this.dialog ? Theme.key_dialogBackground : Theme.key_windowBackgroundWhite));
            }
        }
    }

    public UItem getItem(int i) {
        if (i < 0 || i >= this.items.size()) {
            return null;
        }
        return (UItem) this.items.get(i);
    }

    @Override
    public int getItemCount() {
        return this.items.size();
    }

    @Override
    public int getItemViewType(int i) {
        UItem item = getItem(i);
        if (item == null) {
            return 0;
        }
        return item.viewType;
    }

    public int getReorderSectionId(int i) {
        for (int i2 = 0; i2 < this.reorderSections.size(); i2++) {
            if (((Section) this.reorderSections.get(i2)).contains(i)) {
                return i2;
            }
        }
        return -1;
    }

    public int getThemedColor(int i) {
        return Theme.getColor(i, this.resourcesProvider);
    }

    @Override
    public boolean isEnabled(RecyclerView.ViewHolder viewHolder) {
        UItem.UItemFactory findFactory;
        int itemViewType = viewHolder.getItemViewType();
        UItem item = getItem(viewHolder.getAdapterPosition());
        if (itemViewType < UItem.factoryViewTypeStartsWith ? itemViewType == 3 || itemViewType == 5 || itemViewType == 6 || itemViewType == 30 || itemViewType == 4 || itemViewType == 10 || itemViewType == 11 || itemViewType == 12 || itemViewType == 17 || itemViewType == 16 || itemViewType == 29 || itemViewType == 25 || itemViewType == 27 || itemViewType == 32 || itemViewType == 33 || itemViewType == 35 || itemViewType == 36 || itemViewType == 37 || itemViewType == 41 || itemViewType == 39 || itemViewType == 40 || itemViewType == 38 : (findFactory = UItem.findFactory(itemViewType)) != null && findFactory.isClickable()) {
            if (item == null || item.enabled) {
                return true;
            }
        }
        return false;
    }

    public boolean isReorderItem(int i) {
        return getReorderSectionId(i) >= 0;
    }

    public boolean isShadow(int i) {
        if (i < UItem.factoryViewTypeStartsWith) {
            return i == 7 || i == 8 || i == 38 || i == 31 || i == 34;
        }
        UItem.UItemFactory findFactory = UItem.findFactory(i);
        return findFactory != null && findFactory.isShadow();
    }

    public void listenReorder(Utilities.Callback2 callback2) {
        this.onReordered = callback2;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
        int i2;
        int i3;
        int color;
        LinkSpanDrawable.LinksTextView textView;
        int dp;
        View view;
        CharSequence charSequence;
        int i4;
        String str;
        int i5;
        long dialogId;
        int i6;
        boolean z;
        boolean z2;
        final UItem item = getItem(i);
        UItem item2 = getItem(i + 1);
        UItem item3 = getItem(i - 1);
        if (item == null) {
            return;
        }
        int itemViewType = viewHolder.getItemViewType();
        boolean hasDivider = hasDivider(i);
        updateColors(viewHolder);
        if (itemViewType >= UItem.factoryViewTypeStartsWith) {
            UItem.UItemFactory findFactory = UItem.findFactory(itemViewType);
            if (findFactory != null) {
                findFactory.bindView(viewHolder.itemView, item, hasDivider);
                return;
            }
            return;
        }
        String str2 = "";
        TextInfoPrivacyCell textInfoPrivacyCell = null;
        String publicUsername = null;
        switch (itemViewType) {
            case -3:
                FullscreenCustomFrameLayout fullscreenCustomFrameLayout = (FullscreenCustomFrameLayout) viewHolder.itemView;
                fullscreenCustomFrameLayout.setMinusHeight(item.intValue);
                if (fullscreenCustomFrameLayout.getChildCount() == (item.view != null) && fullscreenCustomFrameLayout.getChildAt(0) == item.view) {
                    return;
                }
                fullscreenCustomFrameLayout.removeAllViews();
                View view2 = item.view;
                if (view2 != null) {
                    AndroidUtilities.removeFromParent(view2);
                    fullscreenCustomFrameLayout.addView(item.view, LayoutHelper.createFrame(-1, -1.0f));
                    return;
                }
                return;
            case -2:
            case -1:
                FrameLayout frameLayout = (FrameLayout) viewHolder.itemView;
                if (frameLayout.getChildCount() == (item.view != null) && frameLayout.getChildAt(0) == item.view) {
                    return;
                }
                frameLayout.removeAllViews();
                View view3 = item.view;
                if (view3 != null) {
                    AndroidUtilities.removeFromParent(view3);
                    frameLayout.addView(item.view, (itemViewType == -1 || itemViewType == -3) ? LayoutHelper.createFrame(-1, -1.0f) : LayoutHelper.createFrame(-2, -2.0f));
                    return;
                }
                return;
            case 0:
            case 1:
            case 26:
                ((HeaderCell) viewHolder.itemView).setText(item.text);
                return;
            case 2:
                TopViewCell topViewCell = (TopViewCell) viewHolder.itemView;
                int i7 = item.iconResId;
                if (i7 != 0) {
                    topViewCell.setEmoji(i7);
                } else {
                    topViewCell.setEmoji(item.subtext.toString(), item.textValue.toString());
                }
                topViewCell.setText(item.text);
                return;
            case 3:
                TextCell textCell = (TextCell) viewHolder.itemView;
                Object obj = item.object;
                if (obj instanceof TLRPC.Document) {
                    textCell.setTextAndSticker(item.text, (TLRPC.Document) obj, hasDivider);
                } else if (obj instanceof String) {
                    textCell.setTextAndSticker(item.text, (String) obj, hasDivider);
                } else if (TextUtils.isEmpty(item.textValue)) {
                    Object obj2 = item.object;
                    if (obj2 instanceof Drawable) {
                        textCell.setTextAndIcon(item.text, (Drawable) obj2, hasDivider);
                    } else {
                        int i8 = item.iconResId;
                        if (i8 == 0) {
                            textCell.setText(item.text, hasDivider);
                        } else {
                            textCell.setTextAndIcon(item.text, i8, hasDivider);
                        }
                    }
                } else {
                    Object obj3 = item.object;
                    if (obj3 instanceof Drawable) {
                        textCell.setTextAndValueAndIcon(item.text, item.textValue, (Drawable) obj3, hasDivider);
                    } else {
                        int i9 = item.iconResId;
                        if (i9 == 0) {
                            textCell.setTextAndValue(item.text, item.textValue, hasDivider);
                        } else {
                            textCell.setTextAndValueAndIcon(item.text, item.textValue, i9, hasDivider);
                        }
                    }
                }
                if (item.accent) {
                    int i10 = Theme.key_windowBackgroundWhiteBlueText4;
                    textCell.setColors(i10, i10);
                    return;
                }
                if (item.red) {
                    i2 = Theme.key_text_RedBold;
                    i3 = Theme.key_text_RedRegular;
                } else {
                    i2 = Theme.key_windowBackgroundWhiteGrayIcon;
                    i3 = Theme.key_windowBackgroundWhiteBlackText;
                }
                textCell.setColors(i2, i3);
                return;
            case 4:
            case 9:
                TextCheckCell textCheckCell = (TextCheckCell) viewHolder.itemView;
                if (textCheckCell.itemId == item.id) {
                    textCheckCell.setChecked(item.checked);
                }
                textCheckCell.setEnabled(item.enabled, null);
                textCheckCell.setTextAndCheck(item.text, item.checked, hasDivider);
                textCheckCell.itemId = item.id;
                if (itemViewType == 9) {
                    View view4 = viewHolder.itemView;
                    color = Theme.getColor(item.checked ? Theme.key_windowBackgroundChecked : Theme.key_windowBackgroundUnchecked);
                    view = view4;
                    break;
                } else {
                    return;
                }
            case 5:
                NotificationsCheckCell notificationsCheckCell = (NotificationsCheckCell) viewHolder.itemView;
                CharSequence charSequence2 = item.subtext;
                notificationsCheckCell.setTextAndValueAndCheck(item.text, item.subtext, item.checked, 0, charSequence2 != null && charSequence2.toString().contains("\n"), hasDivider);
                return;
            case 6:
                ((NotificationsCheckCell) viewHolder.itemView).setTextAndValueAndCheck(item.text, item.subtext, item.checked, hasDivider);
                return;
            case 7:
            case 8:
            case 38:
                if (itemViewType == 7 || itemViewType == 8) {
                    TextInfoPrivacyCell textInfoPrivacyCell2 = (TextInfoPrivacyCell) viewHolder.itemView;
                    if (TextUtils.isEmpty(item.text)) {
                        textInfoPrivacyCell2.setFixedSize(itemViewType == 8 ? 220 : 12);
                        textInfoPrivacyCell2.setText("");
                    } else {
                        textInfoPrivacyCell2.setFixedSize(0);
                        textInfoPrivacyCell2.setText(item.text);
                    }
                    if (item.accent) {
                        textInfoPrivacyCell2.setTextGravity(17);
                        textInfoPrivacyCell2.getTextView().setWidth(Math.min(HintView2.cutInFancyHalf(textInfoPrivacyCell2.getText(), textInfoPrivacyCell2.getTextView().getPaint()), AndroidUtilities.displaySize.x - AndroidUtilities.dp(60.0f)));
                        textView = textInfoPrivacyCell2.getTextView();
                        dp = AndroidUtilities.dp(17.0f);
                    } else {
                        textInfoPrivacyCell2.setTextGravity(8388611);
                        textInfoPrivacyCell2.getTextView().setMinWidth(0);
                        textInfoPrivacyCell2.getTextView().setMaxWidth(AndroidUtilities.displaySize.x);
                        textView = textInfoPrivacyCell2.getTextView();
                        dp = AndroidUtilities.dp(10.0f);
                    }
                    textView.setPadding(0, dp, 0, AndroidUtilities.dp(17.0f));
                    textInfoPrivacyCell = textInfoPrivacyCell2;
                } else if (itemViewType == 38) {
                    CollapseTextCell collapseTextCell = (CollapseTextCell) viewHolder.itemView;
                    collapseTextCell.set(item.animatedText, item.collapsed);
                    collapseTextCell.setColor(item.accent ? Theme.key_windowBackgroundWhiteBlueText4 : item.red ? Theme.key_text_RedRegular : Theme.key_windowBackgroundWhiteBlackText);
                    textInfoPrivacyCell = collapseTextCell;
                }
                boolean z3 = (item3 == null || isShadow(item3.viewType)) ? false : true;
                boolean z4 = (item2 == null || isShadow(item2.viewType)) ? false : true;
                Drawable themedDrawableByKey = Theme.getThemedDrawableByKey(this.context, (z3 && z4) ? R.drawable.greydivider : z3 ? R.drawable.greydivider_bottom : z4 ? R.drawable.greydivider_top : R.drawable.field_carret_empty, Theme.key_windowBackgroundGrayShadow, this.resourcesProvider);
                if (this.dialog) {
                    textInfoPrivacyCell.setBackground(new LayerDrawable(new Drawable[]{new ColorDrawable(getThemedColor(Theme.key_dialogBackgroundGray)), themedDrawableByKey}));
                    return;
                } else {
                    textInfoPrivacyCell.setBackground(themedDrawableByKey);
                    return;
                }
            case 10:
                DialogRadioCell dialogRadioCell = (DialogRadioCell) viewHolder.itemView;
                if (dialogRadioCell.itemId == item.id) {
                    dialogRadioCell.setChecked(item.checked, true);
                    dialogRadioCell.setEnabled(item.enabled, true);
                } else {
                    dialogRadioCell.setEnabled(item.enabled, false);
                }
                if (TextUtils.isEmpty(item.textValue)) {
                    dialogRadioCell.setText(item.text, item.checked, hasDivider);
                } else {
                    dialogRadioCell.setTextAndValue(item.text, item.textValue, item.checked, hasDivider);
                }
                dialogRadioCell.itemId = item.id;
                return;
            case 11:
            case 12:
                UserCell userCell = (UserCell) viewHolder.itemView;
                userCell.setFromUItem(this.currentAccount, item, hasDivider);
                if (itemViewType == 12) {
                    userCell.setChecked(item.checked, false);
                    return;
                }
                return;
            case 13:
                UserCell userCell2 = (UserCell) viewHolder.itemView;
                userCell2.setFromUItem(this.currentAccount, item, hasDivider);
                userCell2.setAddButtonVisible(!item.checked);
                userCell2.setCloseIcon(item.clickCallback);
                return;
            case 14:
                SlideChooseView slideChooseView = (SlideChooseView) viewHolder.itemView;
                slideChooseView.setOptions(item.intValue, item.texts);
                slideChooseView.setMinAllowedIndex((int) item.longValue);
                slideChooseView.setCallback(new SlideChooseView.Callback() {
                    @Override
                    public final void onOptionSelected(int i11) {
                        UniversalAdapter.lambda$onBindViewHolder$1(UItem.this, i11);
                    }

                    @Override
                    public void onTouchEnd() {
                        SlideChooseView.Callback.CC.$default$onTouchEnd(this);
                    }
                });
                return;
            case 15:
                SlideIntChooseView slideIntChooseView = (SlideIntChooseView) viewHolder.itemView;
                slideIntChooseView.set(item.intValue, (SlideIntChooseView.Options) item.object, item.intCallback);
                slideIntChooseView.setMinValueAllowed((int) item.longValue);
                return;
            case 16:
                QuickRepliesActivity.QuickReplyView quickReplyView = (QuickRepliesActivity.QuickReplyView) viewHolder.itemView;
                quickReplyView.setChecked(item.checked, false);
                quickReplyView.setReorder(this.allowReorder);
                Object obj4 = item.object;
                if (obj4 instanceof QuickRepliesController.QuickReply) {
                    quickReplyView.set((QuickRepliesController.QuickReply) obj4, null, hasDivider);
                    return;
                }
                return;
            case 17:
                QuickRepliesActivity.LargeQuickReplyView largeQuickReplyView = (QuickRepliesActivity.LargeQuickReplyView) viewHolder.itemView;
                largeQuickReplyView.setChecked(item.checked, false);
                Object obj5 = item.object;
                if (obj5 instanceof QuickRepliesController.QuickReply) {
                    largeQuickReplyView.set((QuickRepliesController.QuickReply) obj5, hasDivider);
                    return;
                }
                return;
            case 18:
            case 19:
            case 20:
            case 21:
            case 22:
            case 23:
                ((StatisticActivity.UniversalChartCell) viewHolder.itemView).set(item.intValue, (StatisticActivity.ChartViewData) item.object, new Utilities.Callback0Return() {
                    @Override
                    public final Object run() {
                        StatisticActivity.BaseChartCell lambda$onBindViewHolder$2;
                        lambda$onBindViewHolder$2 = UniversalAdapter.this.lambda$onBindViewHolder$2(item);
                        return lambda$onBindViewHolder$2;
                    }
                });
                return;
            case 24:
                ((ChannelMonetizationLayout.ProceedOverviewCell) viewHolder.itemView).set((ChannelMonetizationLayout.ProceedOverview) item.object);
                return;
            case 25:
                ((ChannelMonetizationLayout.TransactionCell) viewHolder.itemView).set((TL_stats.BroadcastRevenueTransaction) item.object, hasDivider);
                return;
            case 27:
                StoryPrivacyBottomSheet.UserCell userCell3 = (StoryPrivacyBottomSheet.UserCell) viewHolder.itemView;
                long j = userCell3.dialogId;
                Object obj6 = item.object;
                boolean z5 = j == (obj6 instanceof TLRPC.User ? ((TLRPC.User) obj6).id : obj6 instanceof TLRPC.Chat ? -((TLRPC.Chat) obj6).id : 0L);
                userCell3.setIsSendAs(false, true);
                userCell3.set(item.object);
                userCell3.checkBox.setVisibility(8);
                userCell3.radioButton.setVisibility(0);
                userCell3.setChecked(item.checked, z5);
                userCell3.setDivider(hasDivider);
                return;
            case 28:
                if (item.transparent) {
                    viewHolder.itemView.setBackgroundColor(0);
                }
                viewHolder.itemView.setLayoutParams(new RecyclerView.LayoutParams(-1, item.intValue));
                return;
            case 29:
                BusinessLinksActivity.BusinessLinkView businessLinkView = (BusinessLinksActivity.BusinessLinkView) viewHolder.itemView;
                Object obj7 = item.object;
                if (obj7 instanceof BusinessLinksActivity.BusinessLinkWrapper) {
                    businessLinkView.set((BusinessLinksActivity.BusinessLinkWrapper) obj7, hasDivider);
                    return;
                }
                return;
            case 30:
                TextRightIconCell textRightIconCell = (TextRightIconCell) viewHolder.itemView;
                textRightIconCell.setTextAndIcon(item.text, item.iconResId);
                textRightIconCell.setDivider(hasDivider);
                color = getThemedColor(Theme.key_dialogBackground);
                view = textRightIconCell;
                break;
            case 31:
                GraySectionCell graySectionCell = (GraySectionCell) viewHolder.itemView;
                if (TextUtils.equals(graySectionCell.getText(), item.text)) {
                    graySectionCell.setRightText(item.subtext, true, item.clickCallback);
                    return;
                } else {
                    graySectionCell.setText(item.text, item.subtext, item.clickCallback);
                    return;
                }
            case 32:
                ProfileSearchCell profileSearchCell = (ProfileSearchCell) viewHolder.itemView;
                Object obj8 = item.object;
                if (item.accent && (obj8 instanceof TLRPC.User) && (i5 = ((TLRPC.User) obj8).bot_active_users) != 0) {
                    if (i5 != 0) {
                        charSequence = LocaleController.formatPluralStringSpaced("BotUsers", i5);
                    }
                    charSequence = "";
                } else {
                    if (item.withUsername) {
                        if (obj8 instanceof TLRPC.User) {
                            publicUsername = UserObject.getPublicUsername((TLRPC.User) obj8);
                        } else if (obj8 instanceof TLRPC.Chat) {
                            publicUsername = ChatObject.getPublicUsername((TLRPC.Chat) obj8);
                        }
                        if (publicUsername != null) {
                            charSequence = ((Object) "") + "@" + publicUsername;
                        }
                    }
                    charSequence = "";
                }
                if (obj8 instanceof TLRPC.Chat) {
                    TLRPC.Chat chat = (TLRPC.Chat) obj8;
                    if (chat.participants_count != 0) {
                        if (!ChatObject.isChannel(chat) || chat.megagroup) {
                            i4 = chat.participants_count;
                            str = "Members";
                        } else {
                            i4 = chat.participants_count;
                            str = "Subscribers";
                        }
                        String formatPluralStringSpaced = LocaleController.formatPluralStringSpaced(str, i4);
                        if (charSequence instanceof SpannableStringBuilder) {
                            ((SpannableStringBuilder) charSequence).append((CharSequence) ", ").append((CharSequence) formatPluralStringSpaced);
                        } else {
                            charSequence = !TextUtils.isEmpty(charSequence) ? TextUtils.concat(charSequence, ", ", formatPluralStringSpaced) : formatPluralStringSpaced;
                        }
                    }
                    str2 = chat.title;
                } else if (obj8 instanceof TLRPC.User) {
                    str2 = UserObject.getUserName((TLRPC.User) obj8);
                }
                CharSequence charSequence3 = charSequence;
                String str3 = str2;
                profileSearchCell.setRectangularAvatar(item.red);
                profileSearchCell.setData(obj8, null, str3, charSequence3, false, false);
                profileSearchCell.useSeparator = hasDivider;
                return;
            case 33:
                DialogCell dialogCell = (DialogCell) viewHolder.itemView;
                Object obj9 = item.object;
                MessageObject messageObject = obj9 instanceof MessageObject ? (MessageObject) obj9 : null;
                dialogCell.useSeparator = hasDivider;
                if (messageObject == null) {
                    z = false;
                    z2 = false;
                    dialogId = 0;
                    messageObject = null;
                    i6 = 0;
                } else {
                    dialogId = messageObject.getDialogId();
                    i6 = messageObject.messageOwner.date;
                    z = false;
                    z2 = false;
                }
                dialogCell.setDialog(dialogId, messageObject, i6, z, z2);
                return;
            case 34:
                ((FlickerLoadingView) viewHolder.itemView).setViewType(item.intValue);
                return;
            case 35:
            case 36:
            case 41:
                CheckBoxCell checkBoxCell = (CheckBoxCell) viewHolder.itemView;
                checkBoxCell.setPad(item.pad);
                checkBoxCell.setText(item.text, "", item.checked, hasDivider, checkBoxCell.itemId == item.id);
                checkBoxCell.itemId = item.id;
                checkBoxCell.setIcon(item.locked ? R.drawable.permission_locked : 0);
                if (itemViewType == 36 || itemViewType == 41) {
                    checkBoxCell.setCollapseButton(item.collapsed, item.animatedText, item.clickCallback);
                    return;
                }
                return;
            case 37:
                CheckBoxCell checkBoxCell2 = (CheckBoxCell) viewHolder.itemView;
                checkBoxCell2.setPad(item.pad);
                checkBoxCell2.setUserOrChat((TLObject) item.object);
                checkBoxCell2.setChecked(item.checked, checkBoxCell2.itemId == item.id);
                checkBoxCell2.itemId = item.id;
                checkBoxCell2.setNeedDivider(hasDivider);
                return;
            case 39:
            case 40:
                final TextCheckCell2 textCheckCell2 = (TextCheckCell2) viewHolder.itemView;
                textCheckCell2.setTextAndCheck(item.text.toString(), item.checked, hasDivider, textCheckCell2.id == item.id);
                textCheckCell2.id = item.id;
                textCheckCell2.setIcon(item.locked ? R.drawable.permission_locked : 0);
                if (itemViewType == 40) {
                    textCheckCell2.setCollapseArrow(item.animatedText.toString(), item.collapsed, new Runnable() {
                        @Override
                        public final void run() {
                            UniversalAdapter.lambda$onBindViewHolder$3(UItem.this, textCheckCell2);
                        }
                    });
                    return;
                }
                return;
            case 42:
                HeaderCell headerCell = (HeaderCell) viewHolder.itemView;
                headerCell.setText(item.animatedText, headerCell.id == item.id);
                headerCell.id = item.id;
                return;
            default:
                return;
        }
        view.setBackgroundColor(color);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view;
        boolean z = this.dialog;
        int i2 = z ? Theme.key_dialogBackground : Theme.key_windowBackgroundWhite;
        if (i < UItem.factoryViewTypeStartsWith) {
            switch (i) {
                case -3:
                    view = new FullscreenCustomFrameLayout(this.context);
                    break;
                case -2:
                    view = new FrameLayout(this.context) {
                        @Override
                        protected void onMeasure(int i3, int i4) {
                            int makeMeasureSpec = View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(i3), 1073741824);
                            measureChildren(makeMeasureSpec, i4);
                            int i5 = 0;
                            for (int i6 = 0; i6 < getChildCount(); i6++) {
                                i5 = Math.max(i5, getChildAt(i6).getMeasuredHeight());
                            }
                            super.onMeasure(makeMeasureSpec, View.MeasureSpec.makeMeasureSpec(i5, 1073741824));
                        }
                    };
                    break;
                case -1:
                    view = new FrameLayout(this.context) {
                        @Override
                        protected void onMeasure(int i3, int i4) {
                            super.onMeasure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(i3), 1073741824), i4);
                        }
                    };
                    break;
                case 0:
                    if (!z) {
                        view = new HeaderCell(this.context, this.resourcesProvider);
                        break;
                    } else {
                        view = new HeaderCell(this.context, Theme.key_windowBackgroundWhiteBlueHeader, 21, 15, 0, false, this.resourcesProvider);
                        break;
                    }
                case 1:
                    view = new HeaderCell(this.context, Theme.key_windowBackgroundWhiteBlackText, 17, 15, false, this.resourcesProvider);
                    break;
                case 2:
                    view = new TopViewCell(this.context, this.resourcesProvider);
                    break;
                case 3:
                    view = new TextCell(this.context, this.resourcesProvider);
                    break;
                case 4:
                case 9:
                    TextCheckCell textCheckCell = new TextCheckCell(this.context, this.resourcesProvider);
                    view = textCheckCell;
                    if (i == 9) {
                        textCheckCell.setDrawCheckRipple(true);
                        textCheckCell.setColors(Theme.key_windowBackgroundCheckText, Theme.key_switchTrackBlue, Theme.key_switchTrackBlueChecked, Theme.key_switchTrackBlueThumb, Theme.key_switchTrackBlueThumbChecked);
                        textCheckCell.setTypeface(AndroidUtilities.bold());
                        textCheckCell.setHeight(56);
                        view = textCheckCell;
                        break;
                    }
                    break;
                case 5:
                case 6:
                    view = new NotificationsCheckCell(this.context, 21, 60, i == 6, this.resourcesProvider);
                    break;
                case 7:
                case 8:
                default:
                    view = new TextInfoPrivacyCell(this.context, this.resourcesProvider);
                    break;
                case 10:
                    view = new DialogRadioCell(this.context);
                    break;
                case 11:
                case 12:
                    UserCell userCell = new UserCell(this.context, 6, i == 12 ? 3 : 0, false);
                    userCell.setSelfAsSavedMessages(true);
                    view = userCell;
                    break;
                case 13:
                    view = new UserCell(this.context, 6, 0, false, true);
                    break;
                case 14:
                    view = new SlideChooseView(this.context, this.resourcesProvider);
                    break;
                case 15:
                    view = new SlideIntChooseView(this.context, this.resourcesProvider);
                    break;
                case 16:
                    view = new QuickRepliesActivity.QuickReplyView(this.context, this.onReordered != null, this.resourcesProvider);
                    break;
                case 17:
                    view = new QuickRepliesActivity.LargeQuickReplyView(this.context, this.resourcesProvider);
                    break;
                case 18:
                case 19:
                case 20:
                case 21:
                case 22:
                case 23:
                    if (this.chartSharedUI == null) {
                        this.chartSharedUI = new BaseChartView.SharedUiComponents();
                    }
                    view = new StatisticActivity.UniversalChartCell(this.context, this.currentAccount, i - 18, this.chartSharedUI, this.classGuid);
                    break;
                case 24:
                    view = new ChannelMonetizationLayout.ProceedOverviewCell(this.context, this.resourcesProvider);
                    break;
                case 25:
                    view = new ChannelMonetizationLayout.TransactionCell(this.context, this.resourcesProvider);
                    break;
                case 26:
                    HeaderCell headerCell = new HeaderCell(this.context, Theme.key_windowBackgroundWhiteBlackText, 23, 20, 0, false, this.resourcesProvider);
                    headerCell.setTextSize(20.0f);
                    view = headerCell;
                    break;
                case 27:
                    StoryPrivacyBottomSheet.UserCell userCell2 = new StoryPrivacyBottomSheet.UserCell(this.context, this.resourcesProvider);
                    userCell2.setIsSendAs(false, false);
                    view = userCell2;
                    break;
                case 28:
                    view = new View(this.context);
                    break;
                case 29:
                    view = new BusinessLinksActivity.BusinessLinkView(this.context, this.resourcesProvider);
                    break;
                case 30:
                    view = new TextRightIconCell(this.context, this.resourcesProvider);
                    break;
                case 31:
                    view = new GraySectionCell(this.context, this.resourcesProvider);
                    break;
                case 32:
                    view = new ProfileSearchCell(this.context);
                    break;
                case 33:
                    view = new DialogCell(null, this.context, false, true);
                    break;
                case 34:
                    FlickerLoadingView flickerLoadingView = new FlickerLoadingView(this.context, this.resourcesProvider);
                    flickerLoadingView.setIsSingleCell(true);
                    view = flickerLoadingView;
                    break;
                case 35:
                case 36:
                case 37:
                case 41:
                    CheckBoxCell checkBoxCell = new CheckBoxCell(this.context, i == 35 ? 4 : i == 36 ? 6 : i == 37 ? 7 : i == 41 ? 8 : 0, 21, true, this.resourcesProvider);
                    checkBoxCell.getCheckBoxRound().setColor(Theme.key_switch2TrackChecked, Theme.key_radioBackground, Theme.key_checkboxCheck);
                    view = checkBoxCell;
                    break;
                case 38:
                    view = new CollapseTextCell(this.context, this.resourcesProvider);
                    break;
                case 39:
                case 40:
                    view = new TextCheckCell2(this.context);
                    break;
                case 42:
                    view = new HeaderCell(this.context, Theme.key_windowBackgroundWhiteBlueHeader, 21, 15, 0, false, true, this.resourcesProvider);
                    break;
            }
        } else {
            UItem.UItemFactory findFactory = UItem.findFactory(i);
            view = findFactory != null ? findFactory.createView(this.context, this.currentAccount, this.classGuid, this.resourcesProvider) : new View(this.context);
        }
        if (shouldApplyBackground(i)) {
            view.setBackgroundColor(getThemedColor(i2));
        }
        return new RecyclerListView.Holder(view);
    }

    @Override
    public void onViewAttachedToWindow(RecyclerView.ViewHolder viewHolder) {
        updateReorder(viewHolder, this.allowReorder);
        updateColors(viewHolder);
    }

    public void reorderDone() {
        if (this.orderChanged) {
            callReorder(this.orderChangedId);
        }
    }

    public void reorderSectionEnd() {
        Section section = this.currentReorderSection;
        if (section != null) {
            section.end = Math.max(0, this.items.size() - 1);
        }
    }

    public int reorderSectionStart() {
        Section section = new Section();
        this.currentReorderSection = section;
        section.start = this.items.size();
        Section section2 = this.currentReorderSection;
        section2.end = -1;
        this.reorderSections.add(section2);
        return this.reorderSections.size() - 1;
    }

    public void setApplyBackground(boolean z) {
        this.applyBackground = z;
    }

    public boolean shouldApplyBackground(int i) {
        if (!this.applyBackground) {
            return false;
        }
        if (i >= UItem.factoryViewTypeStartsWith) {
            return true;
        }
        switch (i) {
            case -3:
            case 0:
            case 1:
            case 3:
            case 4:
            case 5:
            case 6:
            case 9:
            case 10:
            case 11:
            case 12:
            case 13:
            case 14:
            case 15:
            case 16:
            case 17:
            case 18:
            case 19:
            case 20:
            case 21:
            case 22:
            case 23:
            case 24:
            case 25:
            case 27:
            case 28:
            case 29:
            case 30:
            case 32:
            case 33:
            case 34:
            case 35:
            case 36:
            case 37:
            case 39:
            case 40:
            case 41:
            case 42:
                return true;
            case -2:
            case -1:
            case 2:
            case 7:
            case 8:
            case 26:
            case 31:
            case 38:
            default:
                return false;
        }
    }

    public void swapElements(int i, int i2) {
        int i3;
        if (this.onReordered == null) {
            return;
        }
        int reorderSectionId = getReorderSectionId(i);
        int reorderSectionId2 = getReorderSectionId(i2);
        if (reorderSectionId < 0 || reorderSectionId != reorderSectionId2) {
            return;
        }
        UItem uItem = (UItem) this.items.get(i);
        UItem uItem2 = (UItem) this.items.get(i2);
        boolean hasDivider = hasDivider(i);
        boolean hasDivider2 = hasDivider(i2);
        this.items.set(i, uItem2);
        this.items.set(i2, uItem);
        notifyItemMoved(i, i2);
        if (hasDivider(i2) != hasDivider) {
            notifyItemChanged(i2, 3);
        }
        if (hasDivider(i) != hasDivider2) {
            notifyItemChanged(i, 3);
        }
        if (this.orderChanged && (i3 = this.orderChangedId) != reorderSectionId) {
            callReorder(i3);
        }
        this.orderChanged = true;
        this.orderChangedId = reorderSectionId;
    }

    public void update(final boolean z) {
        this.oldItems.clear();
        this.oldItems.addAll(this.items);
        this.items.clear();
        this.whiteSections.clear();
        this.reorderSections.clear();
        Utilities.Callback2 callback2 = this.fillItems;
        if (callback2 != null) {
            callback2.run(this.items, this);
            RecyclerListView recyclerListView = this.listView;
            if (recyclerListView != null && recyclerListView.isComputingLayout()) {
                this.listView.post(new Runnable() {
                    @Override
                    public final void run() {
                        UniversalAdapter.this.lambda$update$0(z);
                    }
                });
            } else if (z) {
                setItems(this.oldItems, this.items);
            } else {
                notifyDataSetChanged();
            }
        }
    }

    public void updateReorder(RecyclerView.ViewHolder viewHolder, boolean z) {
        if (viewHolder != null && viewHolder.getItemViewType() == 16) {
            ((QuickRepliesActivity.QuickReplyView) viewHolder.itemView).setReorder(z);
        }
    }

    public void updateReorder(boolean z) {
        this.allowReorder = z;
    }

    public void whiteSectionEnd() {
        Section section = this.currentWhiteSection;
        if (section != null) {
            section.end = Math.max(0, this.items.size() - 1);
        }
    }

    public void whiteSectionStart() {
        Section section = new Section();
        this.currentWhiteSection = section;
        section.start = this.items.size();
        Section section2 = this.currentWhiteSection;
        section2.end = -1;
        this.whiteSections.add(section2);
    }
}
