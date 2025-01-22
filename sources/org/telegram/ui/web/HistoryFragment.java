package org.telegram.ui.web;

import android.content.Context;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.exoplayer2.util.Consumer;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.TimeZone;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.R;
import org.telegram.messenger.Utilities;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.ActionBarMenu;
import org.telegram.ui.ActionBar.ActionBarMenuItem;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.EditTextBoldCursor;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.NumberTextView;
import org.telegram.ui.Components.StickerEmptyView;
import org.telegram.ui.Components.UItem;
import org.telegram.ui.Components.UniversalAdapter;
import org.telegram.ui.Components.UniversalFragment;
import org.telegram.ui.Components.UniversalRecyclerView;
import org.telegram.ui.web.AddressBarList;
import org.telegram.ui.web.BrowserHistory;
import org.telegram.ui.web.HistoryFragment;
import org.telegram.ui.web.WebMetadataCache;

public class HistoryFragment extends UniversalFragment {
    private final Runnable closeToTabs;
    private StickerEmptyView emptyView;
    private String query;
    private ActionBarMenuItem searchItem;
    private boolean searchLoading;
    private NumberTextView selectedCount;
    private final Utilities.Callback whenClicked;
    private ArrayList history = BrowserHistory.getHistory(new Utilities.Callback() {
        @Override
        public final void run(Object obj) {
            HistoryFragment.this.lambda$new$0((ArrayList) obj);
        }
    });
    private final ArrayList searchResults = new ArrayList();
    public HashSet selected = new HashSet();

    class AnonymousClass1 extends ActionBar.ActionBarMenuOnItemClick {
        AnonymousClass1() {
        }

        public static void lambda$onItemClick$0(View view) {
            if (view instanceof AddressBarList.BookmarkView) {
                ((AddressBarList.BookmarkView) view).setChecked(false);
            }
        }

        @Override
        public void onItemClick(int i) {
            if (i == -1) {
                if (!((BaseFragment) HistoryFragment.this).actionBar.isActionModeShowed()) {
                    HistoryFragment.this.lambda$onBackPressed$323();
                    return;
                }
                ((BaseFragment) HistoryFragment.this).actionBar.hideActionMode();
                HistoryFragment.this.selected.clear();
                AndroidUtilities.forEachViews((RecyclerView) HistoryFragment.this.listView, new Consumer() {
                    @Override
                    public final void accept(Object obj) {
                        HistoryFragment.AnonymousClass1.lambda$onItemClick$0((View) obj);
                    }
                });
            }
        }
    }

    public class AnonymousClass2 extends ActionBarMenuItem.ActionBarMenuItemSearchListener {
        private Runnable applySearch = new Runnable() {
            @Override
            public final void run() {
                HistoryFragment.AnonymousClass2.this.lambda$$2();
            }
        };

        AnonymousClass2() {
        }

        public void lambda$$0(ArrayList arrayList) {
            HistoryFragment.this.searchResults.clear();
            HistoryFragment.this.searchResults.addAll(arrayList);
            HistoryFragment.this.searchLoading = false;
            UniversalRecyclerView universalRecyclerView = HistoryFragment.this.listView;
            if (universalRecyclerView != null) {
                universalRecyclerView.adapter.update(true);
            }
        }

        public void lambda$$1(ArrayList arrayList, String str) {
            WebMetadataCache.WebMetadata webMetadata;
            final ArrayList arrayList2 = new ArrayList();
            for (int i = 0; i < arrayList.size(); i++) {
                BrowserHistory.Entry entry = (BrowserHistory.Entry) arrayList.get(i);
                if (matches(entry.url, str) || ((webMetadata = entry.meta) != null && (matches(webMetadata.title, str) || matches(entry.meta.sitename, str)))) {
                    arrayList2.add(entry);
                }
            }
            AndroidUtilities.runOnUIThread(new Runnable() {
                @Override
                public final void run() {
                    HistoryFragment.AnonymousClass2.this.lambda$$0(arrayList2);
                }
            });
        }

        public void lambda$$2() {
            final ArrayList arrayList = new ArrayList(HistoryFragment.this.history);
            final String str = HistoryFragment.this.query;
            Utilities.searchQueue.postRunnable(new Runnable() {
                @Override
                public final void run() {
                    HistoryFragment.AnonymousClass2.this.lambda$$1(arrayList, str);
                }
            });
        }

        private void scheduleSearch() {
            HistoryFragment.this.searchLoading = true;
            AndroidUtilities.cancelRunOnUIThread(this.applySearch);
            AndroidUtilities.runOnUIThread(this.applySearch, 500L);
        }

        public boolean matches(String str, String str2) {
            if (str == null || str2 == null) {
                return false;
            }
            String lowerCase = str.toLowerCase();
            String lowerCase2 = str2.toLowerCase();
            if (!lowerCase.startsWith(lowerCase2)) {
                if (!lowerCase.contains(" " + lowerCase2)) {
                    if (!lowerCase.contains("." + lowerCase2)) {
                        String translitSafe = AndroidUtilities.translitSafe(lowerCase);
                        String translitSafe2 = AndroidUtilities.translitSafe(lowerCase2);
                        if (!translitSafe.startsWith(translitSafe2)) {
                            if (!translitSafe.contains(" " + translitSafe2)) {
                                if (!translitSafe.contains("." + translitSafe2)) {
                                    return false;
                                }
                            }
                        }
                        return true;
                    }
                }
            }
            return true;
        }

        @Override
        public void onSearchCollapse() {
            HistoryFragment.this.query = null;
            HistoryFragment.this.searchLoading = false;
            AndroidUtilities.cancelRunOnUIThread(this.applySearch);
            UniversalRecyclerView universalRecyclerView = HistoryFragment.this.listView;
            if (universalRecyclerView != null) {
                universalRecyclerView.adapter.update(true);
                HistoryFragment.this.listView.layoutManager.scrollToPositionWithOffset(0, 0);
            }
            HistoryFragment.this.emptyView.title.setText(LocaleController.getString(TextUtils.isEmpty(HistoryFragment.this.query) ? R.string.WebNoHistory : R.string.WebNoSearchedHistory));
        }

        @Override
        public void onSearchExpand() {
        }

        @Override
        public void onTextChanged(EditText editText) {
            boolean z = !TextUtils.isEmpty(HistoryFragment.this.query);
            String obj = editText.getText().toString();
            if (!TextUtils.equals(HistoryFragment.this.query, obj)) {
                HistoryFragment.this.query = obj;
                scheduleSearch();
                HistoryFragment.this.emptyView.title.setText(LocaleController.getString(TextUtils.isEmpty(obj) ? R.string.WebNoHistory : R.string.WebNoSearchedHistory));
            }
            UniversalRecyclerView universalRecyclerView = HistoryFragment.this.listView;
            if (universalRecyclerView != null) {
                universalRecyclerView.adapter.update(true);
                if (z != (!TextUtils.isEmpty(obj))) {
                    HistoryFragment.this.listView.layoutManager.scrollToPositionWithOffset(0, 0);
                }
            }
        }
    }

    public HistoryFragment(Runnable runnable, Utilities.Callback callback) {
        this.closeToTabs = runnable;
        this.whenClicked = callback;
    }

    public static boolean lambda$createView$1(View view, MotionEvent motionEvent) {
        return true;
    }

    public void lambda$new$0(ArrayList arrayList) {
        this.history = arrayList;
        if (this.listView.isAttachedToWindow()) {
            this.listView.adapter.update(true);
        }
    }

    public void clickSelect(UItem uItem, View view) {
    }

    @Override
    public View createView(Context context) {
        this.fragmentView = super.createView(context);
        ActionBar actionBar = this.actionBar;
        int i = Theme.key_windowBackgroundWhite;
        actionBar.setBackgroundColor(getThemedColor(i));
        this.actionBar.setActionModeColor(Theme.getColor(i));
        this.actionBar.setBackButtonImage(R.drawable.ic_ab_back);
        ActionBar actionBar2 = this.actionBar;
        int i2 = Theme.key_windowBackgroundWhiteBlackText;
        actionBar2.setTitleColor(getThemedColor(i2));
        this.actionBar.setItemsBackgroundColor(getThemedColor(Theme.key_actionBarActionModeDefaultSelector), false);
        this.actionBar.setItemsColor(getThemedColor(i2), false);
        this.actionBar.setItemsColor(getThemedColor(i2), true);
        this.actionBar.setCastShadows(true);
        this.actionBar.setActionBarMenuOnItemClick(new AnonymousClass1());
        ActionBarMenu createActionMode = this.actionBar.createActionMode();
        NumberTextView numberTextView = new NumberTextView(createActionMode.getContext());
        this.selectedCount = numberTextView;
        numberTextView.setTextSize(18);
        this.selectedCount.setTypeface(AndroidUtilities.bold());
        this.selectedCount.setTextColor(getThemedColor(Theme.key_actionBarActionModeDefaultIcon));
        this.selectedCount.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public final boolean onTouch(View view, MotionEvent motionEvent) {
                boolean lambda$createView$1;
                lambda$createView$1 = HistoryFragment.lambda$createView$1(view, motionEvent);
                return lambda$createView$1;
            }
        });
        createActionMode.addView(this.selectedCount, LayoutHelper.createLinear(0, -1, 1.0f, 65, 0, 0, 0));
        ActionBarMenuItem actionBarMenuItemSearchListener = this.actionBar.createMenu().addItem(0, R.drawable.ic_ab_search, getResourceProvider()).setIsSearchField(true).setActionBarMenuItemSearchListener(new AnonymousClass2());
        this.searchItem = actionBarMenuItemSearchListener;
        int i3 = R.string.Search;
        actionBarMenuItemSearchListener.setSearchFieldHint(LocaleController.getString(i3));
        this.searchItem.setContentDescription(LocaleController.getString(i3));
        EditTextBoldCursor searchField = this.searchItem.getSearchField();
        searchField.setTextColor(getThemedColor(i2));
        searchField.setHintTextColor(getThemedColor(Theme.key_player_time));
        searchField.setCursorColor(getThemedColor(i2));
        StickerEmptyView stickerEmptyView = new StickerEmptyView(context, null, 1);
        this.emptyView = stickerEmptyView;
        stickerEmptyView.title.setText(LocaleController.getString(TextUtils.isEmpty(this.query) ? R.string.WebNoHistory : R.string.WebNoSearchedHistory));
        this.emptyView.subtitle.setVisibility(8);
        this.emptyView.showProgress(false, false);
        this.emptyView.setAnimateLayoutChange(true);
        ((FrameLayout) this.fragmentView).addView(this.emptyView, LayoutHelper.createFrame(-1, -1.0f));
        this.listView.setEmptyView(this.emptyView);
        this.listView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int i4, int i5) {
                HistoryFragment historyFragment = HistoryFragment.this;
                if (historyFragment.listView.scrollingByUser) {
                    AndroidUtilities.hideKeyboard(historyFragment.fragmentView);
                }
            }
        });
        return this.fragmentView;
    }

    @Override
    public void fillItems(ArrayList arrayList, UniversalAdapter universalAdapter) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeZone(TimeZone.getDefault());
        int i = 0;
        if (TextUtils.isEmpty(this.query)) {
            ArrayList arrayList2 = this.history;
            if (arrayList2 != null) {
                for (int size = arrayList2.size() - 1; size >= 0; size--) {
                    BrowserHistory.Entry entry = (BrowserHistory.Entry) this.history.get(size);
                    calendar.setTimeInMillis(entry.time);
                    int i2 = (calendar.get(1) * 10000) + (calendar.get(2) * 100) + calendar.get(5);
                    if (i != i2) {
                        arrayList.add(UItem.asGraySection(LocaleController.formatDateChat(entry.time / 1000)));
                        i = i2;
                    }
                    arrayList.add(AddressBarList.BookmarkView.Factory.as(entry, this.query));
                }
            }
        } else {
            for (int size2 = this.searchResults.size() - 1; size2 >= 0; size2--) {
                BrowserHistory.Entry entry2 = (BrowserHistory.Entry) this.searchResults.get(size2);
                calendar.setTimeInMillis(entry2.time);
                int i3 = (calendar.get(1) * 10000) + (calendar.get(2) * 100) + calendar.get(5);
                if (i != i3) {
                    arrayList.add(UItem.asGraySection(LocaleController.formatDateChat(entry2.time / 1000)));
                    i = i3;
                }
                arrayList.add(AddressBarList.BookmarkView.Factory.as(entry2, this.query));
            }
            if (this.searchLoading) {
                arrayList.add(UItem.asFlicker(32));
                arrayList.add(UItem.asFlicker(32));
                arrayList.add(UItem.asFlicker(32));
            }
        }
        if (arrayList.isEmpty()) {
            return;
        }
        arrayList.add(UItem.asShadow(null));
    }

    @Override
    protected CharSequence getTitle() {
        return LocaleController.getString(R.string.WebHistory);
    }

    @Override
    public boolean isLightStatusBar() {
        return AndroidUtilities.computePerceivedBrightness(getThemedColor(Theme.key_windowBackgroundWhite)) > 0.721f;
    }

    @Override
    public void onClick(UItem uItem, View view, int i, float f, float f2) {
        if (uItem.instanceOf(AddressBarList.BookmarkView.Factory.class)) {
            if (this.actionBar.isActionModeShowed()) {
                clickSelect(uItem, view);
            } else {
                lambda$onBackPressed$323();
                this.whenClicked.run((BrowserHistory.Entry) uItem.object2);
            }
        }
    }

    @Override
    public boolean onLongClick(UItem uItem, View view, int i, float f, float f2) {
        return false;
    }
}
