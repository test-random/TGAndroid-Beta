package org.telegram.ui;

import android.content.res.Configuration;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.ReplacementSpan;
import android.view.View;
import android.view.ViewGroup;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearSmoothScrollerCustom;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ContactsController;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MediaDataController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.R;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.UserObject;
import org.telegram.messenger.Utilities;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.BottomSheetWithRecyclerListView;
import org.telegram.ui.Components.BulletinFactory;
import org.telegram.ui.Components.CubicBezierInterpolator;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.Premium.boosts.BoostRepository;
import org.telegram.ui.Components.Premium.boosts.adapters.SelectorAdapter;
import org.telegram.ui.Components.Premium.boosts.cells.selector.SelectorBtnCell;
import org.telegram.ui.Components.Premium.boosts.cells.selector.SelectorHeaderCell;
import org.telegram.ui.Components.Premium.boosts.cells.selector.SelectorSearchCell;
import org.telegram.ui.Components.Premium.boosts.cells.selector.SelectorUserCell;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.Stories.recorder.ButtonWithCounterView;

public class MultiContactsSelectorBottomSheet extends BottomSheetWithRecyclerListView {
    private static MultiContactsSelectorBottomSheet instance;
    private final ButtonWithCounterView actionButton;
    private final HashMap allSelectedObjects;
    private final SelectorBtnCell buttonContainer;
    private final List contacts;
    private final List contactsLetters;
    private final Map contactsMap;
    private final List foundedUsers;
    private final SelectorHeaderCell headerView;
    private final List hints;
    private final ArrayList items;
    private int lastRequestId;
    private int listPaddingTop;
    private int maxCount;
    private final ArrayList oldItems;
    private String query;
    private float recipientsBtnExtraSpace;
    private ReplacementSpan recipientsBtnSpaceSpan;
    private final Runnable remoteSearchRunnable;
    private final SelectorSearchCell searchField;
    private final View sectionCell;
    private final HashSet selectedIds;
    private SelectorAdapter selectorAdapter;
    private SelectorListener selectorListener;

    public interface SelectorListener {
        void onUserSelected(List list);
    }

    public MultiContactsSelectorBottomSheet(BaseFragment baseFragment, boolean z, final int i, SelectorListener selectorListener) {
        super(baseFragment, z, false, false, baseFragment.getResourceProvider());
        this.oldItems = new ArrayList();
        ArrayList arrayList = new ArrayList();
        this.items = arrayList;
        HashSet hashSet = new HashSet();
        this.selectedIds = hashSet;
        ArrayList arrayList2 = new ArrayList();
        this.contacts = arrayList2;
        ArrayList arrayList3 = new ArrayList();
        this.hints = arrayList3;
        this.foundedUsers = new ArrayList();
        HashMap hashMap = new HashMap();
        this.contactsMap = hashMap;
        ArrayList arrayList4 = new ArrayList();
        this.contactsLetters = arrayList4;
        this.allSelectedObjects = new LinkedHashMap();
        this.listPaddingTop = AndroidUtilities.dp(120.0f);
        this.remoteSearchRunnable = new Runnable() {
            @Override
            public void run() {
                String str = MultiContactsSelectorBottomSheet.this.query;
                if (str != null) {
                    MultiContactsSelectorBottomSheet.this.loadData(str);
                }
            }
        };
        this.maxCount = i;
        this.selectorListener = selectorListener;
        SelectorHeaderCell selectorHeaderCell = new SelectorHeaderCell(getContext(), this.resourcesProvider) {
            @Override
            protected int getHeaderHeight() {
                return AndroidUtilities.dp(getResources().getConfiguration().orientation == 2 ? 48.0f : 54.0f);
            }
        };
        this.headerView = selectorHeaderCell;
        selectorHeaderCell.setOnCloseClickListener(new Runnable() {
            @Override
            public final void run() {
                MultiContactsSelectorBottomSheet.this.dismiss();
            }
        });
        selectorHeaderCell.setText(getTitle());
        selectorHeaderCell.setCloseImageVisible(false);
        selectorHeaderCell.backDrawable.setRotation(0.0f, false);
        createRecipientsBtnSpaceSpan();
        SelectorSearchCell selectorSearchCell = new SelectorSearchCell(getContext(), this.resourcesProvider, null) {
            private boolean isKeyboardVisible;

            @Override
            protected void onLayout(boolean z2, int i2, int i3, int i4, int i5) {
                super.onLayout(z2, i2, i3, i4, i5);
                MultiContactsSelectorBottomSheet.this.listPaddingTop = getMeasuredHeight() + AndroidUtilities.dp(64.0f);
                MultiContactsSelectorBottomSheet.this.selectorAdapter.notifyChangedLast();
                if (this.isKeyboardVisible != MultiContactsSelectorBottomSheet.this.isKeyboardVisible()) {
                    boolean isKeyboardVisible = MultiContactsSelectorBottomSheet.this.isKeyboardVisible();
                    this.isKeyboardVisible = isKeyboardVisible;
                    if (isKeyboardVisible) {
                        MultiContactsSelectorBottomSheet.this.scrollToTop(true);
                    }
                }
            }
        };
        this.searchField = selectorSearchCell;
        int i2 = Theme.key_dialogBackground;
        selectorSearchCell.setBackgroundColor(getThemedColor(i2));
        selectorSearchCell.setOnSearchTextChange(new Utilities.Callback() {
            @Override
            public final void run(Object obj) {
                MultiContactsSelectorBottomSheet.this.onSearch((String) obj);
            }
        });
        selectorSearchCell.setHintText(LocaleController.getString(R.string.Search), false);
        View view = new View(getContext()) {
            @Override
            protected void onDraw(Canvas canvas) {
                canvas.drawColor(MultiContactsSelectorBottomSheet.this.getThemedColor(Theme.key_graySection));
            }
        };
        this.sectionCell = view;
        ViewGroup viewGroup = this.containerView;
        int i3 = this.backgroundPaddingLeft;
        viewGroup.addView(selectorHeaderCell, 0, LayoutHelper.createFrameMarginPx(-1, -2.0f, 55, i3, 0, i3, 0));
        ViewGroup viewGroup2 = this.containerView;
        int i4 = this.backgroundPaddingLeft;
        viewGroup2.addView(selectorSearchCell, LayoutHelper.createFrameMarginPx(-1, -2.0f, 55, i4, 0, i4, 0));
        ViewGroup viewGroup3 = this.containerView;
        int i5 = this.backgroundPaddingLeft;
        viewGroup3.addView(view, LayoutHelper.createFrameMarginPx(-1, 1.0f, 55, i5, 0, i5, 0));
        SelectorBtnCell selectorBtnCell = new SelectorBtnCell(getContext(), this.resourcesProvider, null);
        this.buttonContainer = selectorBtnCell;
        selectorBtnCell.setClickable(true);
        selectorBtnCell.setOrientation(1);
        selectorBtnCell.setPadding(AndroidUtilities.dp(10.0f), AndroidUtilities.dp(10.0f), AndroidUtilities.dp(10.0f), AndroidUtilities.dp(10.0f));
        selectorBtnCell.setBackgroundColor(Theme.getColor(i2, this.resourcesProvider));
        ButtonWithCounterView buttonWithCounterView = new ButtonWithCounterView(getContext(), this.resourcesProvider) {
            @Override
            protected float calculateCounterWidth(float f, float f2) {
                boolean z2 = MultiContactsSelectorBottomSheet.this.recipientsBtnExtraSpace == 0.0f;
                MultiContactsSelectorBottomSheet.this.recipientsBtnExtraSpace = f;
                if (z2) {
                    MultiContactsSelectorBottomSheet.this.createRecipientsBtnSpaceSpan();
                    MultiContactsSelectorBottomSheet.this.updateActionButton(false);
                }
                return f;
            }
        };
        this.actionButton = buttonWithCounterView;
        buttonWithCounterView.setOnClickListener(new View.OnClickListener() {
            @Override
            public final void onClick(View view2) {
                MultiContactsSelectorBottomSheet.this.lambda$new$1(view2);
            }
        });
        selectorBtnCell.addView(buttonWithCounterView, LayoutHelper.createLinear(-1, 48, 87));
        ViewGroup viewGroup4 = this.containerView;
        int i6 = this.backgroundPaddingLeft;
        viewGroup4.addView(selectorBtnCell, LayoutHelper.createFrameMarginPx(-1, -2.0f, 87, i6, 0, i6, 0));
        this.selectorAdapter.setData(arrayList, this.recyclerListView);
        RecyclerListView recyclerListView = this.recyclerListView;
        int i7 = this.backgroundPaddingLeft;
        recyclerListView.setPadding(i7, 0, i7, AndroidUtilities.dp(60.0f));
        this.recyclerListView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int i8) {
                if (i8 == 1) {
                    AndroidUtilities.hideKeyboard(MultiContactsSelectorBottomSheet.this.searchField.getEditText());
                }
            }
        });
        this.recyclerListView.setOnItemClickListener(new RecyclerListView.OnItemClickListenerExtended() {
            @Override
            public boolean hasDoubleTap(View view2, int i8) {
                return RecyclerListView.OnItemClickListenerExtended.CC.$default$hasDoubleTap(this, view2, i8);
            }

            @Override
            public void onDoubleTap(View view2, int i8, float f, float f2) {
                RecyclerListView.OnItemClickListenerExtended.CC.$default$onDoubleTap(this, view2, i8, f, f2);
            }

            @Override
            public final void onItemClick(View view2, int i8, float f, float f2) {
                MultiContactsSelectorBottomSheet.this.lambda$new$3(i, view2, i8, f, f2);
            }
        });
        DefaultItemAnimator defaultItemAnimator = new DefaultItemAnimator();
        defaultItemAnimator.setDurations(350L);
        defaultItemAnimator.setInterpolator(CubicBezierInterpolator.EASE_OUT_QUINT);
        defaultItemAnimator.setDelayAnimations(false);
        defaultItemAnimator.setSupportsChangeAnimations(false);
        this.recyclerListView.setItemAnimator(defaultItemAnimator);
        this.recyclerListView.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(Rect rect, View view2, RecyclerView recyclerView, RecyclerView.State state) {
                super.getItemOffsets(rect, view2, recyclerView, state);
                if (recyclerView.getChildAdapterPosition(view2) == MultiContactsSelectorBottomSheet.this.items.size()) {
                    rect.bottom = MultiContactsSelectorBottomSheet.this.listPaddingTop;
                }
            }
        });
        selectorSearchCell.setText("");
        selectorSearchCell.spansContainer.removeAllSpans(false);
        selectorSearchCell.updateSpans(false, hashSet, new Runnable() {
            @Override
            public final void run() {
                MultiContactsSelectorBottomSheet.this.lambda$new$4();
            }
        }, null);
        selectorHeaderCell.setText(getTitle());
        updateActionButton(false);
        arrayList2.addAll(ContactsController.getInstance(this.currentAccount).contacts);
        hashMap.putAll(ContactsController.getInstance(this.currentAccount).usersSectionsDict);
        arrayList4.addAll(ContactsController.getInstance(this.currentAccount).sortedUsersSectionsArray);
        arrayList3.addAll(MediaDataController.getInstance(this.currentAccount).hints);
        updateList(false, true);
        fixNavigationBar();
    }

    private void clearSearchAfterSelect() {
        if (isSearching()) {
            this.query = null;
            this.searchField.setText("");
            AndroidUtilities.cancelRunOnUIThread(this.remoteSearchRunnable);
            updateItems(true, true);
        }
    }

    public void createRecipientsBtnSpaceSpan() {
        this.recipientsBtnSpaceSpan = new ReplacementSpan() {
            @Override
            public void draw(Canvas canvas, CharSequence charSequence, int i, int i2, float f, int i3, int i4, int i5, Paint paint) {
            }

            @Override
            public int getSize(Paint paint, CharSequence charSequence, int i, int i2, Paint.FontMetricsInt fontMetricsInt) {
                return (int) MultiContactsSelectorBottomSheet.this.recipientsBtnExtraSpace;
            }
        };
    }

    private boolean isSearching() {
        return !TextUtils.isEmpty(this.query);
    }

    public void lambda$loadData$0(List list) {
        this.foundedUsers.clear();
        this.foundedUsers.addAll(list);
        updateList(true, true);
    }

    public void lambda$new$1(View view) {
        next();
    }

    public void lambda$new$2() {
        updateList(true, false);
    }

    public void lambda$new$3(int i, View view, int i2, float f, float f2) {
        if (view instanceof SelectorUserCell) {
            TLRPC.User user = ((SelectorUserCell) view).getUser();
            long j = user.id;
            if (this.selectedIds.contains(Long.valueOf(j))) {
                this.selectedIds.remove(Long.valueOf(j));
            } else {
                this.selectedIds.add(Long.valueOf(j));
                this.allSelectedObjects.put(Long.valueOf(j), user);
            }
            if (this.selectedIds.size() == i + 1) {
                this.selectedIds.remove(Long.valueOf(j));
                showMaximumUsersToast();
            } else {
                this.searchField.updateSpans(true, this.selectedIds, new Runnable() {
                    @Override
                    public final void run() {
                        MultiContactsSelectorBottomSheet.this.lambda$new$2();
                    }
                }, null);
                updateList(true, false);
                clearSearchAfterSelect();
            }
        }
    }

    public void lambda$new$4() {
        updateList(true, false);
    }

    public void lambda$updateSectionCell$5(View view) {
        this.selectedIds.clear();
        this.searchField.spansContainer.removeAllSpans(true);
        updateList(true, false);
    }

    public void loadData(String str) {
        this.lastRequestId = BoostRepository.searchContacts(this.lastRequestId, str, new Utilities.Callback() {
            @Override
            public final void run(Object obj) {
                MultiContactsSelectorBottomSheet.this.lambda$loadData$0((List) obj);
            }
        });
    }

    private void next() {
        if (this.selectedIds.size() == 0 || this.selectorListener == null) {
            return;
        }
        ArrayList arrayList = new ArrayList();
        for (TLRPC.User user : this.allSelectedObjects.values()) {
            if (this.selectedIds.contains(Long.valueOf(user.id))) {
                arrayList.add(Long.valueOf(user.id));
            }
        }
        this.selectorListener.onUserSelected(arrayList);
        dismiss();
    }

    public void onSearch(String str) {
        this.query = str;
        AndroidUtilities.cancelRunOnUIThread(this.remoteSearchRunnable);
        AndroidUtilities.runOnUIThread(this.remoteSearchRunnable, 100L);
    }

    public static void open(int i, SelectorListener selectorListener) {
        BaseFragment lastFragment = LaunchActivity.getLastFragment();
        if (lastFragment != null && instance == null) {
            MultiContactsSelectorBottomSheet multiContactsSelectorBottomSheet = new MultiContactsSelectorBottomSheet(lastFragment, true, i, selectorListener);
            multiContactsSelectorBottomSheet.show();
            instance = multiContactsSelectorBottomSheet;
        }
    }

    private void showMaximumUsersToast() {
        BulletinFactory.of(this.container, this.resourcesProvider).createSimpleBulletin(R.raw.chats_infotip, LocaleController.formatPluralString("BotMultiContactsSelectorLimit", this.maxCount, new Object[0])).show(true);
        try {
            this.container.performHapticFeedback(3, 2);
        } catch (Exception unused) {
        }
    }

    public void updateActionButton(boolean z) {
        int i;
        this.actionButton.setShowZero(false);
        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder();
        if (this.selectedIds.size() == 0) {
            spannableStringBuilder.append((CharSequence) "d").setSpan(this.recipientsBtnSpaceSpan, 0, 1, 33);
            i = R.string.ChooseUsers;
        } else {
            i = R.string.GiftPremiumProceedBtn;
        }
        spannableStringBuilder.append((CharSequence) LocaleController.getString(i));
        this.actionButton.setCount(this.selectedIds.size(), true);
        this.actionButton.setText(spannableStringBuilder, z, false);
        this.actionButton.setEnabled(true);
    }

    private void updateCheckboxes(boolean z) {
        int childAdapterPosition;
        int i = -1;
        int i2 = 0;
        for (int i3 = 0; i3 < this.recyclerListView.getChildCount(); i3++) {
            View childAt = this.recyclerListView.getChildAt(i3);
            if ((childAt instanceof SelectorUserCell) && (childAdapterPosition = this.recyclerListView.getChildAdapterPosition(childAt)) > 0) {
                if (i == -1) {
                    i = childAdapterPosition;
                }
                int i4 = childAdapterPosition - 1;
                if (i4 >= 0 && i4 < this.items.size()) {
                    SelectorAdapter.Item item = (SelectorAdapter.Item) this.items.get(i4);
                    SelectorUserCell selectorUserCell = (SelectorUserCell) childAt;
                    selectorUserCell.setChecked(item.checked, z);
                    TLRPC.Chat chat = item.chat;
                    float f = 1.0f;
                    if (chat != null && this.selectorAdapter.getParticipantsCount(chat) > 200) {
                        f = 0.3f;
                    }
                    selectorUserCell.setCheckboxAlpha(f, z);
                }
                i2 = childAdapterPosition;
            }
        }
        if (z) {
            this.selectorAdapter.notifyItemRangeChanged(0, i);
            SelectorAdapter selectorAdapter = this.selectorAdapter;
            selectorAdapter.notifyItemRangeChanged(i2, selectorAdapter.getItemCount() - i2);
        }
    }

    private void updateList(boolean z, boolean z2) {
        updateItems(z, z2);
        updateCheckboxes(z);
        updateActionButton(z);
    }

    private void updateSectionCell(boolean z) {
        SelectorAdapter selectorAdapter;
        View.OnClickListener onClickListener;
        HashSet hashSet = this.selectedIds;
        if (hashSet == null) {
            return;
        }
        if (hashSet.size() > 0) {
            selectorAdapter = this.selectorAdapter;
            onClickListener = new View.OnClickListener() {
                @Override
                public final void onClick(View view) {
                    MultiContactsSelectorBottomSheet.this.lambda$updateSectionCell$5(view);
                }
            };
        } else {
            selectorAdapter = this.selectorAdapter;
            onClickListener = null;
        }
        selectorAdapter.setTopSectionClickListener(onClickListener);
    }

    @Override
    protected RecyclerListView.SelectionAdapter createAdapter(RecyclerListView recyclerListView) {
        SelectorAdapter selectorAdapter = new SelectorAdapter(getContext(), true, this.resourcesProvider);
        this.selectorAdapter = selectorAdapter;
        selectorAdapter.setGreenSelector(true);
        return this.selectorAdapter;
    }

    @Override
    public void dismiss() {
        AndroidUtilities.hideKeyboard(this.searchField.getEditText());
        super.dismiss();
    }

    @Override
    public void dismissInternal() {
        super.dismissInternal();
        instance = null;
        AndroidUtilities.cancelRunOnUIThread(this.remoteSearchRunnable);
    }

    @Override
    protected CharSequence getTitle() {
        return LocaleController.getString(R.string.ChooseUsers);
    }

    @Override
    public void onConfigurationChanged(Configuration configuration) {
        super.onConfigurationChanged(configuration);
        updateItems(false, true);
    }

    @Override
    protected void onPreDraw(Canvas canvas, int i, float f) {
        this.headerView.setTranslationY(Math.max(i, AndroidUtilities.statusBarHeight + (((this.headerView.getMeasuredHeight() - AndroidUtilities.statusBarHeight) - AndroidUtilities.dp(40.0f)) / 2.0f)) + AndroidUtilities.dp(8.0f));
        this.searchField.setTranslationY(this.headerView.getTranslationY() + this.headerView.getMeasuredHeight());
        this.sectionCell.setTranslationY(this.searchField.getTranslationY() + this.searchField.getMeasuredHeight());
        this.recyclerListView.setTranslationY(((this.headerView.getMeasuredHeight() + this.searchField.getMeasuredHeight()) + this.sectionCell.getMeasuredHeight()) - AndroidUtilities.dp(8.0f));
    }

    public void scrollToTop(boolean z) {
        if (!z) {
            this.recyclerListView.scrollToPosition(0);
            return;
        }
        LinearSmoothScrollerCustom linearSmoothScrollerCustom = new LinearSmoothScrollerCustom(getContext(), 2, 0.6f);
        linearSmoothScrollerCustom.setTargetPosition(1);
        linearSmoothScrollerCustom.setOffset(AndroidUtilities.dp(36.0f));
        this.recyclerListView.getLayoutManager().startSmoothScroll(linearSmoothScrollerCustom);
    }

    public void updateItems(boolean z, boolean z2) {
        int i;
        int i2;
        SelectorAdapter selectorAdapter;
        this.oldItems.clear();
        this.oldItems.addAll(this.items);
        this.items.clear();
        if (isSearching()) {
            i2 = 0;
            for (TLRPC.User user : this.foundedUsers) {
                i2 += AndroidUtilities.dp(56.0f);
                this.items.add(SelectorAdapter.Item.asUser(user, this.selectedIds.contains(Long.valueOf(user.id))));
            }
        } else {
            if (this.hints.isEmpty()) {
                i = 0;
            } else {
                ArrayList arrayList = new ArrayList();
                Iterator it = this.hints.iterator();
                i = 0;
                while (it.hasNext()) {
                    TLRPC.User user2 = MessagesController.getInstance(this.currentAccount).getUser(Long.valueOf(((TLRPC.TL_topPeer) it.next()).peer.user_id));
                    if (!user2.self && !user2.bot && !UserObject.isService(user2.id) && !UserObject.isDeleted(user2)) {
                        i += AndroidUtilities.dp(56.0f);
                        arrayList.add(SelectorAdapter.Item.asUser(user2, this.selectedIds.contains(Long.valueOf(user2.id))));
                    }
                }
                if (!arrayList.isEmpty()) {
                    i += AndroidUtilities.dp(32.0f);
                    this.items.add(SelectorAdapter.Item.asTopSection(LocaleController.getString(R.string.GiftPremiumFrequentContacts)));
                    this.items.addAll(arrayList);
                }
            }
            for (String str : this.contactsLetters) {
                ArrayList arrayList2 = new ArrayList();
                for (TLRPC.TL_contact tL_contact : (List) this.contactsMap.get(str)) {
                    if (tL_contact.user_id != UserConfig.getInstance(this.currentAccount).getClientUserId()) {
                        i += AndroidUtilities.dp(56.0f);
                        TLRPC.User user3 = MessagesController.getInstance(this.currentAccount).getUser(Long.valueOf(tL_contact.user_id));
                        arrayList2.add(SelectorAdapter.Item.asUser(user3, this.selectedIds.contains(Long.valueOf(user3.id))));
                    }
                }
                if (!arrayList2.isEmpty()) {
                    i += AndroidUtilities.dp(32.0f);
                    this.items.add(SelectorAdapter.Item.asLetter(str.toUpperCase()));
                    this.items.addAll(arrayList2);
                }
            }
            i2 = i;
        }
        if (this.items.isEmpty()) {
            this.items.add(SelectorAdapter.Item.asNoUsers());
            i2 += AndroidUtilities.dp(150.0f);
        }
        this.items.add(SelectorAdapter.Item.asPad(Math.max(0, ((int) (AndroidUtilities.displaySize.y * 0.6f)) - i2)));
        updateSectionCell(z);
        if (!z2 || (selectorAdapter = this.selectorAdapter) == null) {
            return;
        }
        if (z) {
            selectorAdapter.setItems(this.oldItems, this.items);
        } else {
            selectorAdapter.notifyDataSetChanged();
        }
    }
}
