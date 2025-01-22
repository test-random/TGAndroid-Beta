package org.telegram.ui.Business;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MediaDataController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.R;
import org.telegram.messenger.Utilities;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.ActionBarMenuItem;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Cells.TextCheckCell;
import org.telegram.ui.Components.BackupImageView;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.UItem;
import org.telegram.ui.Components.UniversalAdapter;
import org.telegram.ui.Components.UniversalRecyclerView;

public class TimezoneSelector extends BaseFragment implements NotificationCenter.NotificationCenterDelegate {
    private String currentTimezone;
    private LinearLayout emptyView;
    private UniversalRecyclerView listView;
    private String query;
    private ActionBarMenuItem searchItem;
    private boolean searching;
    private String systemTimezone;
    private boolean useSystem;
    private Utilities.Callback whenTimezoneSelected;

    public void fillItems(ArrayList arrayList, UniversalAdapter universalAdapter) {
        int i;
        boolean z = this.searching && !TextUtils.isEmpty(this.query);
        TimezonesController timezonesController = TimezonesController.getInstance(this.currentAccount);
        if (!z) {
            arrayList.add(UItem.asRippleCheck(-1, LocaleController.getString(R.string.TimezoneDetectAutomatically)).setChecked(this.useSystem));
            arrayList.add(UItem.asShadow(LocaleController.formatString(R.string.TimezoneDetectAutomaticallyInfo, timezonesController.getTimezoneName(this.currentTimezone, true))));
            arrayList.add(UItem.asHeader(LocaleController.getString(R.string.TimezoneHeader)));
        }
        boolean z2 = true;
        while (i < timezonesController.getTimezones().size()) {
            TLRPC.TL_timezone tL_timezone = (TLRPC.TL_timezone) timezonesController.getTimezones().get(i);
            if (z) {
                String replace = AndroidUtilities.translitSafe(tL_timezone.name).toLowerCase().replace("/", " ");
                String lowerCase = AndroidUtilities.translitSafe(this.query).toLowerCase();
                StringBuilder sb = new StringBuilder();
                sb.append(" ");
                sb.append(lowerCase);
                i = (replace.contains(sb.toString()) || replace.startsWith(lowerCase)) ? 0 : i + 1;
            }
            arrayList.add(UItem.asRadio(i, timezonesController.getTimezoneName(tL_timezone, false), timezonesController.getTimezoneOffsetName(tL_timezone)).setChecked(TextUtils.equals(tL_timezone.id, this.currentTimezone)).setEnabled(!this.useSystem || z));
            z2 = false;
        }
        arrayList.add(z2 ? UItem.asCustom(this.emptyView) : UItem.asShadow(null));
    }

    public void onClick(UItem uItem, View view, int i, float f, float f2) {
        if (uItem.id == -1) {
            boolean z = !this.useSystem;
            this.useSystem = z;
            if (z) {
                String str = this.systemTimezone;
                this.currentTimezone = str;
                Utilities.Callback callback = this.whenTimezoneSelected;
                if (callback != null) {
                    callback.run(str);
                }
            }
            ((TextCheckCell) view).setChecked(this.useSystem);
        } else {
            if (!view.isEnabled()) {
                return;
            }
            TimezonesController timezonesController = TimezonesController.getInstance(this.currentAccount);
            int i2 = uItem.id;
            if (i2 < 0 || i2 >= timezonesController.getTimezones().size()) {
                return;
            }
            TLRPC.TL_timezone tL_timezone = (TLRPC.TL_timezone) timezonesController.getTimezones().get(uItem.id);
            this.useSystem = false;
            String str2 = tL_timezone.id;
            this.currentTimezone = str2;
            Utilities.Callback callback2 = this.whenTimezoneSelected;
            if (callback2 != null) {
                callback2.run(str2);
            }
            if (this.searching) {
                this.actionBar.closeSearchField(true);
            }
        }
        this.listView.adapter.update(true);
    }

    @Override
    public View createView(Context context) {
        this.actionBar.setBackButtonImage(R.drawable.ic_ab_back);
        this.actionBar.setAllowOverlayTitle(true);
        this.actionBar.setTitle(LocaleController.getString(R.string.TimezoneTitle));
        this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
            @Override
            public void onItemClick(int i) {
                if (i == -1) {
                    TimezoneSelector.this.lambda$onBackPressed$323();
                }
            }
        });
        ActionBarMenuItem actionBarMenuItemSearchListener = this.actionBar.createMenu().addItem(1, R.drawable.ic_ab_search).setIsSearchField(true).setActionBarMenuItemSearchListener(new ActionBarMenuItem.ActionBarMenuItemSearchListener() {
            @Override
            public void onSearchCollapse() {
                TimezoneSelector.this.searching = false;
                TimezoneSelector.this.query = null;
                TimezoneSelector.this.listView.adapter.update(true);
                TimezoneSelector.this.listView.scrollToPosition(0);
            }

            @Override
            public void onSearchExpand() {
                TimezoneSelector.this.searching = true;
                TimezoneSelector.this.listView.adapter.update(true);
                TimezoneSelector.this.listView.scrollToPosition(0);
            }

            @Override
            public void onTextChanged(EditText editText) {
                TimezoneSelector.this.query = editText.getText().toString();
                TimezoneSelector.this.listView.adapter.update(true);
                TimezoneSelector.this.listView.scrollToPosition(0);
            }
        });
        this.searchItem = actionBarMenuItemSearchListener;
        actionBarMenuItemSearchListener.setSearchFieldHint(LocaleController.getString(R.string.Search));
        FrameLayout frameLayout = new FrameLayout(context);
        frameLayout.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundGray));
        UniversalRecyclerView universalRecyclerView = new UniversalRecyclerView(this, new Utilities.Callback2() {
            @Override
            public final void run(Object obj, Object obj2) {
                TimezoneSelector.this.fillItems((ArrayList) obj, (UniversalAdapter) obj2);
            }
        }, new Utilities.Callback5() {
            @Override
            public final void run(Object obj, Object obj2, Object obj3, Object obj4, Object obj5) {
                TimezoneSelector.this.onClick((UItem) obj, (View) obj2, ((Integer) obj3).intValue(), ((Float) obj4).floatValue(), ((Float) obj5).floatValue());
            }
        }, null);
        this.listView = universalRecyclerView;
        frameLayout.addView(universalRecyclerView, LayoutHelper.createFrame(-1, -1.0f));
        this.listView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int i) {
                if (i == 1) {
                    AndroidUtilities.hideKeyboard(TimezoneSelector.this.getParentActivity().getCurrentFocus());
                }
            }
        });
        LinearLayout linearLayout = new LinearLayout(context);
        this.emptyView = linearLayout;
        linearLayout.setOrientation(1);
        this.emptyView.setMinimumHeight(AndroidUtilities.dp(500.0f));
        BackupImageView backupImageView = new BackupImageView(context);
        backupImageView.getImageReceiver().setAllowLoadingOnAttachedOnly(false);
        MediaDataController.getInstance(this.currentAccount).setPlaceholderImage(backupImageView, "RestrictedEmoji", "🌖", "130_130");
        this.emptyView.addView(backupImageView, LayoutHelper.createLinear(130, 130, 49, 0, 42, 0, 12));
        TextView textView = new TextView(context);
        textView.setText(LocaleController.getString(R.string.TimezoneNotFound));
        textView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteGrayText, this.resourceProvider));
        textView.setTextSize(1, 15.0f);
        this.emptyView.addView(textView, LayoutHelper.createLinear(-2, -2, 49, 0, 0, 0, 0));
        this.fragmentView = frameLayout;
        return frameLayout;
    }

    @Override
    public void didReceivedNotification(int i, int i2, Object... objArr) {
        UniversalRecyclerView universalRecyclerView;
        UniversalAdapter universalAdapter;
        if (i != NotificationCenter.timezonesUpdated || (universalRecyclerView = this.listView) == null || (universalAdapter = universalRecyclerView.adapter) == null) {
            return;
        }
        universalAdapter.update(true);
    }

    @Override
    public boolean onFragmentCreate() {
        String systemTimezoneId = TimezonesController.getInstance(this.currentAccount).getSystemTimezoneId();
        this.systemTimezone = systemTimezoneId;
        this.useSystem = TextUtils.equals(systemTimezoneId, this.currentTimezone);
        getNotificationCenter().addObserver(this, NotificationCenter.timezonesUpdated);
        return super.onFragmentCreate();
    }

    @Override
    public void onFragmentDestroy() {
        getNotificationCenter().removeObserver(this, NotificationCenter.timezonesUpdated);
        super.onFragmentDestroy();
    }

    public TimezoneSelector setValue(String str) {
        this.currentTimezone = str;
        return this;
    }

    public TimezoneSelector whenSelected(Utilities.Callback callback) {
        this.whenTimezoneSelected = callback;
        return this;
    }
}
