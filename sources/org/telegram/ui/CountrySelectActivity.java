package org.telegram.ui;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.ReplacementSpan;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import androidx.core.graphics.ColorUtils;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.Emoji;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.R;
import org.telegram.messenger.Utilities;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.ActionBarMenuItem;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ActionBar.ThemeDescription;
import org.telegram.ui.Cells.DividerCell;
import org.telegram.ui.Cells.LetterSectionCell;
import org.telegram.ui.Cells.TextSettingsCell;
import org.telegram.ui.Components.EmptyTextProgressView;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.Premium.boosts.BoostRepository$$ExternalSyntheticLambda30;
import org.telegram.ui.Components.Premium.boosts.BoostRepository$$ExternalSyntheticLambda31;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.CountrySelectActivity;

public class CountrySelectActivity extends BaseFragment {
    private CountrySelectActivityDelegate delegate;
    private boolean disableAnonymousNumbers;
    private EmptyTextProgressView emptyView;
    private ArrayList existingCountries;
    private RecyclerListView listView;
    private CountryAdapter listViewAdapter;
    private boolean needPhoneCode;
    private CountrySearchAdapter searchListViewAdapter;
    private boolean searchWas;
    private boolean searching;

    public class AnonymousClass4 implements View.OnAttachStateChangeListener {
        private NotificationCenter.NotificationCenterDelegate listener;
        final TextSettingsCell val$view;

        AnonymousClass4(final TextSettingsCell textSettingsCell) {
            this.val$view = textSettingsCell;
            this.listener = new NotificationCenter.NotificationCenterDelegate() {
                @Override
                public final void didReceivedNotification(int i, int i2, Object[] objArr) {
                    CountrySelectActivity.AnonymousClass4.lambda$$0(TextSettingsCell.this, i, i2, objArr);
                }
            };
        }

        public static void lambda$$0(TextSettingsCell textSettingsCell, int i, int i2, Object[] objArr) {
            if (i == NotificationCenter.emojiLoaded) {
                textSettingsCell.getTextView().invalidate();
            }
        }

        @Override
        public void onViewAttachedToWindow(View view) {
            NotificationCenter.getGlobalInstance().addObserver(this.listener, NotificationCenter.emojiLoaded);
        }

        @Override
        public void onViewDetachedFromWindow(View view) {
            NotificationCenter.getGlobalInstance().removeObserver(this.listener, NotificationCenter.emojiLoaded);
        }
    }

    public static class Country {
        public String code;
        public String defaultName;
        public String name;
        public String shortname;

        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null || getClass() != obj.getClass()) {
                return false;
            }
            Country country = (Country) obj;
            return Objects.equals(this.name, country.name) && Objects.equals(this.code, country.code);
        }

        public int hashCode() {
            return Objects.hash(this.name, this.code);
        }
    }

    public class CountryAdapter extends RecyclerListView.SectionsAdapter {
        private Context mContext;
        private HashMap countries = new HashMap();
        private ArrayList sortedCountries = new ArrayList();

        public CountryAdapter(Context context, ArrayList arrayList, boolean z) {
            final Comparator boostRepository$$ExternalSyntheticLambda31;
            this.mContext = context;
            if (arrayList != null) {
                for (int i = 0; i < arrayList.size(); i++) {
                    Country country = (Country) arrayList.get(i);
                    String upperCase = country.name.substring(0, 1).toUpperCase();
                    ArrayList arrayList2 = (ArrayList) this.countries.get(upperCase);
                    if (arrayList2 == null) {
                        arrayList2 = new ArrayList();
                        this.countries.put(upperCase, arrayList2);
                        this.sortedCountries.add(upperCase);
                    }
                    arrayList2.add(country);
                }
            } else {
                try {
                    InputStream open = ApplicationLoader.applicationContext.getResources().getAssets().open("countries.txt");
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(open));
                    while (true) {
                        String readLine = bufferedReader.readLine();
                        if (readLine == null) {
                            break;
                        }
                        String[] split = readLine.split(";");
                        Country country2 = new Country();
                        country2.name = split[2];
                        country2.code = split[0];
                        String str = split[1];
                        country2.shortname = str;
                        if (!str.equals("FT") || !z) {
                            String upperCase2 = country2.name.substring(0, 1).toUpperCase();
                            ArrayList arrayList3 = (ArrayList) this.countries.get(upperCase2);
                            if (arrayList3 == null) {
                                arrayList3 = new ArrayList();
                                this.countries.put(upperCase2, arrayList3);
                                this.sortedCountries.add(upperCase2);
                            }
                            arrayList3.add(country2);
                        }
                    }
                    bufferedReader.close();
                    open.close();
                } catch (Exception e) {
                    FileLog.e(e);
                }
            }
            if (Build.VERSION.SDK_INT >= 24) {
                Collator collator = Collator.getInstance(LocaleController.getInstance().getCurrentLocale() != null ? LocaleController.getInstance().getCurrentLocale() : Locale.getDefault());
                Objects.requireNonNull(collator);
                boostRepository$$ExternalSyntheticLambda31 = new BoostRepository$$ExternalSyntheticLambda30(collator);
            } else {
                boostRepository$$ExternalSyntheticLambda31 = new BoostRepository$$ExternalSyntheticLambda31();
            }
            Collections.sort(this.sortedCountries, boostRepository$$ExternalSyntheticLambda31);
            Iterator it = this.countries.values().iterator();
            while (it.hasNext()) {
                Collections.sort((ArrayList) it.next(), new Comparator() {
                    @Override
                    public final int compare(Object obj, Object obj2) {
                        int lambda$new$0;
                        lambda$new$0 = CountrySelectActivity.CountryAdapter.lambda$new$0(boostRepository$$ExternalSyntheticLambda31, (CountrySelectActivity.Country) obj, (CountrySelectActivity.Country) obj2);
                        return lambda$new$0;
                    }
                });
            }
        }

        public static int lambda$new$0(Comparator comparator, Country country, Country country2) {
            return comparator.compare(country.name, country2.name);
        }

        @Override
        public int getCountForSection(int i) {
            int size = ((ArrayList) this.countries.get(this.sortedCountries.get(i))).size();
            return i != this.sortedCountries.size() + (-1) ? size + 1 : size;
        }

        public HashMap getCountries() {
            return this.countries;
        }

        @Override
        public Country getItem(int i, int i2) {
            if (i >= 0 && i < this.sortedCountries.size()) {
                ArrayList arrayList = (ArrayList) this.countries.get(this.sortedCountries.get(i));
                if (i2 >= 0 && i2 < arrayList.size()) {
                    return (Country) arrayList.get(i2);
                }
            }
            return null;
        }

        @Override
        public int getItemViewType(int i, int i2) {
            return i2 < ((ArrayList) this.countries.get(this.sortedCountries.get(i))).size() ? 0 : 1;
        }

        @Override
        public String getLetter(int i) {
            int sectionForPosition = getSectionForPosition(i);
            if (sectionForPosition == -1) {
                sectionForPosition = this.sortedCountries.size() - 1;
            }
            return (String) this.sortedCountries.get(sectionForPosition);
        }

        @Override
        public void getPositionForScrollProgress(RecyclerListView recyclerListView, float f, int[] iArr) {
            iArr[0] = (int) (getItemCount() * f);
            iArr[1] = 0;
        }

        @Override
        public int getSectionCount() {
            return this.sortedCountries.size();
        }

        @Override
        public View getSectionHeaderView(int i, View view) {
            return null;
        }

        @Override
        public boolean isEnabled(RecyclerView.ViewHolder viewHolder, int i, int i2) {
            return i2 < ((ArrayList) this.countries.get(this.sortedCountries.get(i))).size();
        }

        @Override
        public void onBindViewHolder(int i, int i2, RecyclerView.ViewHolder viewHolder) {
            String str;
            if (viewHolder.getItemViewType() == 0) {
                Country country = (Country) ((ArrayList) this.countries.get(this.sortedCountries.get(i))).get(i2);
                TextSettingsCell textSettingsCell = (TextSettingsCell) viewHolder.itemView;
                CharSequence replaceEmoji = Emoji.replaceEmoji(CountrySelectActivity.getCountryNameWithFlag(country), textSettingsCell.getTextView().getPaint().getFontMetricsInt(), AndroidUtilities.dp(20.0f), false);
                if (CountrySelectActivity.this.needPhoneCode) {
                    str = "+" + country.code;
                } else {
                    str = null;
                }
                textSettingsCell.setTextAndValue(replaceEmoji, str, false);
            }
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View createSettingsCell;
            if (i != 0) {
                createSettingsCell = new DividerCell(this.mContext);
                createSettingsCell.setPadding(AndroidUtilities.dp(24.0f), AndroidUtilities.dp(8.0f), AndroidUtilities.dp(24.0f), AndroidUtilities.dp(8.0f));
            } else {
                createSettingsCell = CountrySelectActivity.createSettingsCell(this.mContext);
            }
            return new RecyclerListView.Holder(createSettingsCell);
        }
    }

    public class CountrySearchAdapter extends RecyclerListView.SelectionAdapter {
        private List countryList = new ArrayList();
        private Map countrySearchMap = new HashMap();
        private Context mContext;
        private ArrayList searchResult;
        private Timer searchTimer;

        public CountrySearchAdapter(Context context, HashMap hashMap) {
            this.mContext = context;
            Iterator it = hashMap.values().iterator();
            while (it.hasNext()) {
                for (Country country : (List) it.next()) {
                    this.countryList.add(country);
                    ArrayList arrayList = new ArrayList(Arrays.asList(country.name.split(" ")));
                    String str = country.defaultName;
                    if (str != null) {
                        arrayList.addAll(Arrays.asList(str.split(" ")));
                    }
                    this.countrySearchMap.put(country, arrayList);
                }
            }
        }

        public void lambda$processSearch$0(String str) {
            String lowerCase = str.trim().toLowerCase();
            if (lowerCase.length() == 0) {
                updateSearchResults(new ArrayList());
                return;
            }
            ArrayList arrayList = new ArrayList();
            for (Country country : this.countryList) {
                Iterator it = ((List) this.countrySearchMap.get(country)).iterator();
                while (true) {
                    if (it.hasNext()) {
                        if (((String) it.next()).toLowerCase().startsWith(lowerCase)) {
                            arrayList.add(country);
                            break;
                        }
                    } else {
                        break;
                    }
                }
            }
            updateSearchResults(arrayList);
        }

        public void lambda$updateSearchResults$1(ArrayList arrayList) {
            if (CountrySelectActivity.this.searching) {
                this.searchResult = arrayList;
                if (CountrySelectActivity.this.searchWas && CountrySelectActivity.this.listView != null && CountrySelectActivity.this.listView.getAdapter() != CountrySelectActivity.this.searchListViewAdapter) {
                    CountrySelectActivity.this.listView.setAdapter(CountrySelectActivity.this.searchListViewAdapter);
                    CountrySelectActivity.this.listView.setFastScrollVisible(false);
                }
                notifyDataSetChanged();
            }
        }

        public void processSearch(final String str) {
            Utilities.searchQueue.postRunnable(new Runnable() {
                @Override
                public final void run() {
                    CountrySelectActivity.CountrySearchAdapter.this.lambda$processSearch$0(str);
                }
            });
        }

        private void updateSearchResults(final ArrayList arrayList) {
            AndroidUtilities.runOnUIThread(new Runnable() {
                @Override
                public final void run() {
                    CountrySelectActivity.CountrySearchAdapter.this.lambda$updateSearchResults$1(arrayList);
                }
            });
        }

        public Country getItem(int i) {
            ArrayList arrayList = this.searchResult;
            if (arrayList == null || i < 0 || i >= arrayList.size()) {
                return null;
            }
            return (Country) this.searchResult.get(i);
        }

        @Override
        public int getItemCount() {
            ArrayList arrayList = this.searchResult;
            if (arrayList == null) {
                return 0;
            }
            return arrayList.size();
        }

        @Override
        public int getItemViewType(int i) {
            return 0;
        }

        @Override
        public boolean isEnabled(RecyclerView.ViewHolder viewHolder) {
            return true;
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
            String str;
            Country country = (Country) this.searchResult.get(i);
            TextSettingsCell textSettingsCell = (TextSettingsCell) viewHolder.itemView;
            CharSequence replaceEmoji = Emoji.replaceEmoji(CountrySelectActivity.getCountryNameWithFlag(country), textSettingsCell.getTextView().getPaint().getFontMetricsInt(), AndroidUtilities.dp(20.0f), false);
            if (CountrySelectActivity.this.needPhoneCode) {
                str = "+" + country.code;
            } else {
                str = null;
            }
            textSettingsCell.setTextAndValue(replaceEmoji, str, false);
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            return new RecyclerListView.Holder(CountrySelectActivity.createSettingsCell(this.mContext));
        }

        public void search(final String str) {
            if (str == null) {
                this.searchResult = null;
                return;
            }
            try {
                Timer timer = this.searchTimer;
                if (timer != null) {
                    timer.cancel();
                }
            } catch (Exception e) {
                FileLog.e(e);
            }
            Timer timer2 = new Timer();
            this.searchTimer = timer2;
            timer2.schedule(new TimerTask() {
                @Override
                public void run() {
                    try {
                        CountrySearchAdapter.this.searchTimer.cancel();
                        CountrySearchAdapter.this.searchTimer = null;
                    } catch (Exception e2) {
                        FileLog.e(e2);
                    }
                    CountrySearchAdapter.this.processSearch(str);
                }
            }, 100L, 300L);
        }
    }

    public interface CountrySelectActivityDelegate {
        void didSelectCountry(Country country);
    }

    public CountrySelectActivity(boolean z) {
        this(z, null);
    }

    public CountrySelectActivity(boolean z, ArrayList arrayList) {
        if (arrayList != null && !arrayList.isEmpty()) {
            this.existingCountries = new ArrayList(arrayList);
        }
        this.needPhoneCode = z;
    }

    public static TextSettingsCell createSettingsCell(Context context) {
        TextSettingsCell textSettingsCell = new TextSettingsCell(context);
        textSettingsCell.setPadding(AndroidUtilities.dp(LocaleController.isRTL ? 16.0f : 12.0f), 0, AndroidUtilities.dp(LocaleController.isRTL ? 12.0f : 16.0f), 0);
        textSettingsCell.addOnAttachStateChangeListener(new AnonymousClass4(textSettingsCell));
        return textSettingsCell;
    }

    public static CharSequence getCountryNameWithFlag(Country country) {
        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder();
        String languageFlag = LocaleController.getLanguageFlag(country.shortname);
        if (languageFlag != null) {
            spannableStringBuilder.append((CharSequence) languageFlag).append((CharSequence) " ");
            spannableStringBuilder.setSpan(new ReplacementSpan() {
                @Override
                public void draw(Canvas canvas, CharSequence charSequence, int i, int i2, float f, int i3, int i4, int i5, Paint paint) {
                }

                @Override
                public int getSize(Paint paint, CharSequence charSequence, int i, int i2, Paint.FontMetricsInt fontMetricsInt) {
                    return AndroidUtilities.dp(16.0f);
                }
            }, languageFlag.length(), languageFlag.length() + 1, 0);
        }
        spannableStringBuilder.append((CharSequence) country.name);
        return spannableStringBuilder;
    }

    public void lambda$createView$0(View view, int i) {
        Country item;
        CountrySelectActivityDelegate countrySelectActivityDelegate;
        if (this.searching && this.searchWas) {
            item = this.searchListViewAdapter.getItem(i);
        } else {
            int sectionForPosition = this.listViewAdapter.getSectionForPosition(i);
            int positionInSectionForPosition = this.listViewAdapter.getPositionInSectionForPosition(i);
            if (positionInSectionForPosition < 0 || sectionForPosition < 0) {
                return;
            } else {
                item = this.listViewAdapter.getItem(sectionForPosition, positionInSectionForPosition);
            }
        }
        if (i < 0) {
            return;
        }
        lambda$onBackPressed$321();
        if (item == null || (countrySelectActivityDelegate = this.delegate) == null) {
            return;
        }
        countrySelectActivityDelegate.didSelectCountry(item);
    }

    @Override
    public View createView(Context context) {
        this.actionBar.setBackButtonImage(R.drawable.ic_ab_back);
        this.actionBar.setAllowOverlayTitle(false);
        this.actionBar.setTitle(LocaleController.getString(R.string.ChooseCountry));
        this.actionBar.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
        ActionBar actionBar = this.actionBar;
        int i = Theme.key_windowBackgroundWhiteBlackText;
        actionBar.setItemsColor(Theme.getColor(i), false);
        this.actionBar.setItemsBackgroundColor(Theme.getColor(Theme.key_actionBarWhiteSelector), false);
        this.actionBar.setTitleColor(Theme.getColor(i));
        this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
            @Override
            public void onItemClick(int i2) {
                if (i2 == -1) {
                    CountrySelectActivity.this.lambda$onBackPressed$321();
                }
            }
        });
        this.actionBar.createMenu().addItem(0, R.drawable.ic_ab_search).setIsSearchField(true).setActionBarMenuItemSearchListener(new ActionBarMenuItem.ActionBarMenuItemSearchListener() {
            @Override
            public void onSearchCollapse() {
                CountrySelectActivity.this.searchListViewAdapter.search(null);
                CountrySelectActivity.this.searching = false;
                CountrySelectActivity.this.searchWas = false;
                CountrySelectActivity.this.listView.setAdapter(CountrySelectActivity.this.listViewAdapter);
                CountrySelectActivity.this.listView.setFastScrollVisible(true);
            }

            @Override
            public void onSearchExpand() {
                CountrySelectActivity.this.searching = true;
            }

            @Override
            public void onTextChanged(EditText editText) {
                String obj = editText.getText().toString();
                if (TextUtils.isEmpty(obj)) {
                    CountrySelectActivity.this.searchListViewAdapter.search(null);
                    CountrySelectActivity.this.searchWas = false;
                    CountrySelectActivity.this.listView.setAdapter(CountrySelectActivity.this.listViewAdapter);
                    CountrySelectActivity.this.listView.setFastScrollVisible(true);
                    return;
                }
                CountrySelectActivity.this.searchListViewAdapter.search(obj);
                if (obj.length() != 0) {
                    CountrySelectActivity.this.searchWas = true;
                }
            }
        }).setSearchFieldHint(LocaleController.getString(R.string.Search));
        this.actionBar.setSearchTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteGrayText), true);
        this.actionBar.setSearchTextColor(Theme.getColor(i), false);
        this.actionBar.setSearchCursorColor(Theme.getColor(i));
        this.searching = false;
        this.searchWas = false;
        CountryAdapter countryAdapter = new CountryAdapter(context, this.existingCountries, this.disableAnonymousNumbers);
        this.listViewAdapter = countryAdapter;
        this.searchListViewAdapter = new CountrySearchAdapter(context, countryAdapter.getCountries());
        FrameLayout frameLayout = new FrameLayout(context);
        this.fragmentView = frameLayout;
        EmptyTextProgressView emptyTextProgressView = new EmptyTextProgressView(context);
        this.emptyView = emptyTextProgressView;
        emptyTextProgressView.showTextView();
        this.emptyView.setShowAtCenter(true);
        this.emptyView.setText(LocaleController.getString(R.string.NoResult));
        frameLayout.addView(this.emptyView, LayoutHelper.createFrame(-1, -1.0f));
        RecyclerListView recyclerListView = new RecyclerListView(context);
        this.listView = recyclerListView;
        recyclerListView.setSectionsType(3);
        this.listView.setEmptyView(this.emptyView);
        this.listView.setVerticalScrollBarEnabled(false);
        this.listView.setFastScrollEnabled(0);
        this.listView.setFastScrollVisible(true);
        this.listView.setLayoutManager(new LinearLayoutManager(context, 1, false));
        this.listView.setAdapter(this.listViewAdapter);
        this.listView.setVerticalScrollbarPosition(LocaleController.isRTL ? 1 : 2);
        frameLayout.addView(this.listView, LayoutHelper.createFrame(-1, -1.0f));
        this.listView.setOnItemClickListener(new RecyclerListView.OnItemClickListener() {
            @Override
            public final void onItemClick(View view, int i2) {
                CountrySelectActivity.this.lambda$createView$0(view, i2);
            }
        });
        this.listView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int i2) {
                if (i2 == 1) {
                    AndroidUtilities.hideKeyboard(CountrySelectActivity.this.getParentActivity().getCurrentFocus());
                }
            }
        });
        return this.fragmentView;
    }

    @Override
    public ArrayList getThemeDescriptions() {
        ArrayList arrayList = new ArrayList();
        arrayList.add(new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, Theme.key_windowBackgroundWhite));
        ActionBar actionBar = this.actionBar;
        int i = ThemeDescription.FLAG_BACKGROUND;
        int i2 = Theme.key_actionBarDefault;
        arrayList.add(new ThemeDescription(actionBar, i, null, null, null, null, i2));
        arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_LISTGLOWCOLOR, null, null, null, null, i2));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, null, null, null, null, Theme.key_actionBarDefaultIcon));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, null, null, null, null, Theme.key_actionBarDefaultTitle));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, null, null, null, null, Theme.key_actionBarDefaultSelector));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SEARCH, null, null, null, null, Theme.key_actionBarDefaultSearch));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SEARCHPLACEHOLDER, null, null, null, null, Theme.key_actionBarDefaultSearchPlaceholder));
        arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_SELECTOR, null, null, null, null, Theme.key_listSelector));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{View.class}, Theme.dividerPaint, null, null, Theme.key_divider));
        arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_FASTSCROLL, null, null, null, null, Theme.key_fastScrollActive));
        arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_FASTSCROLL, null, null, null, null, Theme.key_fastScrollInactive));
        arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_FASTSCROLL, null, null, null, null, Theme.key_fastScrollText));
        arrayList.add(new ThemeDescription(this.emptyView, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, Theme.key_emptyListPlaceholder));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{TextSettingsCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteBlackText));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{TextSettingsCell.class}, new String[]{"valueTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteValueText));
        arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_SECTIONS, new Class[]{LetterSectionCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteGrayText4));
        return arrayList;
    }

    @Override
    public boolean isLightStatusBar() {
        return ColorUtils.calculateLuminance(Theme.getColor(Theme.key_windowBackgroundWhite, null, true)) > 0.699999988079071d;
    }

    @Override
    public boolean onFragmentCreate() {
        return super.onFragmentCreate();
    }

    @Override
    public void onFragmentDestroy() {
        super.onFragmentDestroy();
    }

    @Override
    public void onResume() {
        super.onResume();
        CountryAdapter countryAdapter = this.listViewAdapter;
        if (countryAdapter != null) {
            countryAdapter.notifyDataSetChanged();
        }
    }

    public void setCountrySelectActivityDelegate(CountrySelectActivityDelegate countrySelectActivityDelegate) {
        this.delegate = countrySelectActivityDelegate;
    }

    public void setDisableAnonymousNumbers(boolean z) {
        this.disableAnonymousNumbers = z;
    }
}
