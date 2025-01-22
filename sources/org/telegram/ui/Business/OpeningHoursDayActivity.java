package org.telegram.ui.Business;

import android.content.Context;
import android.view.View;
import android.widget.FrameLayout;
import java.util.ArrayList;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.R;
import org.telegram.messenger.Utilities;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Business.OpeningHoursActivity;
import org.telegram.ui.Cells.TextCell;
import org.telegram.ui.Cells.TextCheckCell;
import org.telegram.ui.Components.AlertsCreator;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.UItem;
import org.telegram.ui.Components.UniversalAdapter;
import org.telegram.ui.Components.UniversalRecyclerView;

public class OpeningHoursDayActivity extends BaseFragment {
    public boolean enabled;
    private UniversalRecyclerView listView;
    private final int max;
    private final int maxPeriodsCount;
    private final int min;
    private final ArrayList periods;
    private final CharSequence title;
    private Runnable whenApplied;
    public Runnable whenDone;

    public OpeningHoursDayActivity(CharSequence charSequence, ArrayList arrayList, int i, int i2, int i3) {
        this.title = charSequence;
        this.periods = arrayList;
        this.min = i;
        this.max = i2;
        this.maxPeriodsCount = i3;
        this.enabled = !arrayList.isEmpty();
    }

    public void fillItems(ArrayList arrayList, UniversalAdapter universalAdapter) {
        arrayList.add(UItem.asRippleCheck(-1, LocaleController.getString(R.string.BusinessHoursDayOpen)).setChecked(this.enabled));
        arrayList.add(UItem.asShadow(null));
        if (this.enabled) {
            for (int i = 0; i < this.periods.size(); i++) {
                if (i > 0) {
                    arrayList.add(UItem.asShadow(null));
                }
                OpeningHoursActivity.Period period = (OpeningHoursActivity.Period) this.periods.get(i);
                if (!is24()) {
                    int i2 = i * 3;
                    arrayList.add(UItem.asButton(i2, LocaleController.getString(R.string.BusinessHoursDayOpenHour), OpeningHoursActivity.Period.timeToString(period.start)));
                    arrayList.add(UItem.asButton(i2 + 1, LocaleController.getString(R.string.BusinessHoursDayCloseHour), OpeningHoursActivity.Period.timeToString(period.end)));
                    arrayList.add(UItem.asButton(i2 + 2, LocaleController.getString(R.string.Remove)).red());
                }
            }
            if (showAddButton()) {
                arrayList.add(UItem.asShadow(null));
                arrayList.add(UItem.asButton(-2, R.drawable.menu_premium_clock_add, LocaleController.getString(R.string.BusinessHoursDayAdd)).accent());
            }
            arrayList.add(UItem.asShadow(LocaleController.getString(R.string.BusinessHoursDayInfo)));
        }
    }

    private boolean is24() {
        return this.periods.size() == 1 && ((OpeningHoursActivity.Period) this.periods.get(0)).start == 0 && ((OpeningHoursActivity.Period) this.periods.get(0)).end == 1439;
    }

    public void lambda$onClick$0(View view, OpeningHoursActivity.Period period, Integer num) {
        boolean showAddButton = showAddButton();
        int intValue = num.intValue();
        period.start = intValue;
        ((TextCell) view).setValue(OpeningHoursActivity.Period.timeToString(intValue), true);
        if (showAddButton != showAddButton()) {
            this.listView.adapter.update(true);
        }
        Runnable runnable = this.whenApplied;
        if (runnable != null) {
            runnable.run();
        }
    }

    public void lambda$onClick$1(View view, OpeningHoursActivity.Period period, Integer num) {
        boolean showAddButton = showAddButton();
        int intValue = num.intValue();
        period.end = intValue;
        ((TextCell) view).setValue(OpeningHoursActivity.Period.timeToString(intValue), true);
        if (showAddButton != showAddButton()) {
            this.listView.adapter.update(true);
        }
        Runnable runnable = this.whenApplied;
        if (runnable != null) {
            runnable.run();
        }
    }

    public void onClick(UItem uItem, final View view, int i, float f, float f2) {
        int i2;
        Runnable runnable;
        ArrayList arrayList;
        OpeningHoursActivity.Period period;
        int i3 = uItem.id;
        if (i3 == -1) {
            this.enabled = !this.enabled;
            this.periods.clear();
            if (this.enabled) {
                this.periods.add(new OpeningHoursActivity.Period(0, 1439));
            }
            TextCheckCell textCheckCell = (TextCheckCell) view;
            boolean z = this.enabled;
            uItem.checked = z;
            textCheckCell.setChecked(z);
            boolean z2 = this.enabled;
            textCheckCell.setBackgroundColorAnimated(z2, Theme.getColor(z2 ? Theme.key_windowBackgroundChecked : Theme.key_windowBackgroundUnchecked));
            this.listView.adapter.update(true);
            runnable = this.whenApplied;
            if (runnable == null) {
                return;
            }
        } else {
            if (i3 == -2) {
                if (this.periods.isEmpty() || is24()) {
                    if (is24()) {
                        this.periods.clear();
                    }
                    int clamp = Utilities.clamp(480, this.max - 1, this.min);
                    int clamp2 = Utilities.clamp(1200, this.max, clamp + 1);
                    arrayList = this.periods;
                    period = new OpeningHoursActivity.Period(clamp, clamp2);
                } else {
                    ArrayList arrayList2 = this.periods;
                    int i4 = ((OpeningHoursActivity.Period) arrayList2.get(arrayList2.size() - 1)).end;
                    int clamp3 = Utilities.clamp(i4 + 30, this.max - 1, this.min);
                    int clamp4 = Utilities.clamp((i4 + 1560) / 2, this.max, clamp3 + 1);
                    arrayList = this.periods;
                    period = new OpeningHoursActivity.Period(clamp3, clamp4);
                }
                arrayList.add(period);
                Runnable runnable2 = this.whenApplied;
                if (runnable2 != null) {
                    runnable2.run();
                }
                this.listView.adapter.update(true);
                return;
            }
            if (uItem.viewType != 3 || (i2 = i3 / 3) < 0 || i2 >= this.periods.size()) {
                return;
            }
            int i5 = i2 - 1;
            OpeningHoursActivity.Period period2 = i5 >= 0 ? (OpeningHoursActivity.Period) this.periods.get(i5) : null;
            final OpeningHoursActivity.Period period3 = (OpeningHoursActivity.Period) this.periods.get(i2);
            int i6 = i2 + 1;
            OpeningHoursActivity.Period period4 = i6 < this.periods.size() ? (OpeningHoursActivity.Period) this.periods.get(i6) : null;
            int i7 = uItem.id % 3;
            if (i7 == 0) {
                AlertsCreator.createTimePickerDialog(getContext(), LocaleController.getString(R.string.BusinessHoursDayOpenHourPicker), period3.start, period2 == null ? this.min : period2.end + 1, period3.end - 1, new Utilities.Callback() {
                    @Override
                    public final void run(Object obj) {
                        OpeningHoursDayActivity.this.lambda$onClick$0(view, period3, (Integer) obj);
                    }
                });
                return;
            }
            if (i7 == 1) {
                AlertsCreator.createTimePickerDialog(getContext(), LocaleController.getString(R.string.BusinessHoursDayCloseHourPicker), period3.end, period3.start + 1, period4 == null ? this.max : period4.start - 1, new Utilities.Callback() {
                    @Override
                    public final void run(Object obj) {
                        OpeningHoursDayActivity.this.lambda$onClick$1(view, period3, (Integer) obj);
                    }
                });
                return;
            }
            if (i7 != 2) {
                return;
            }
            this.periods.remove(i2);
            if (this.periods.isEmpty()) {
                this.periods.add(new OpeningHoursActivity.Period(0, 1439));
            }
            this.listView.adapter.update(true);
            runnable = this.whenApplied;
            if (runnable == null) {
                return;
            }
        }
        runnable.run();
    }

    private boolean showAddButton() {
        if (this.periods.size() >= this.maxPeriodsCount) {
            return false;
        }
        if (!this.periods.isEmpty() && !is24()) {
            ArrayList arrayList = this.periods;
            if (((OpeningHoursActivity.Period) arrayList.get(arrayList.size() - 1)).end >= Math.min(1438, this.max - 2)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public View createView(Context context) {
        this.actionBar.setBackButtonImage(R.drawable.ic_ab_back);
        this.actionBar.setAllowOverlayTitle(true);
        this.actionBar.setTitle(this.title);
        this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
            @Override
            public void onItemClick(int i) {
                if (i == -1) {
                    OpeningHoursDayActivity.this.lambda$onBackPressed$323();
                }
            }
        });
        FrameLayout frameLayout = new FrameLayout(context);
        frameLayout.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundGray));
        UniversalRecyclerView universalRecyclerView = new UniversalRecyclerView(this, new Utilities.Callback2() {
            @Override
            public final void run(Object obj, Object obj2) {
                OpeningHoursDayActivity.this.fillItems((ArrayList) obj, (UniversalAdapter) obj2);
            }
        }, new Utilities.Callback5() {
            @Override
            public final void run(Object obj, Object obj2, Object obj3, Object obj4, Object obj5) {
                OpeningHoursDayActivity.this.onClick((UItem) obj, (View) obj2, ((Integer) obj3).intValue(), ((Float) obj4).floatValue(), ((Float) obj5).floatValue());
            }
        }, null);
        this.listView = universalRecyclerView;
        frameLayout.addView(universalRecyclerView, LayoutHelper.createFrame(-1, -1.0f));
        this.fragmentView = frameLayout;
        return frameLayout;
    }

    public OpeningHoursDayActivity onApplied(Runnable runnable) {
        this.whenApplied = runnable;
        return this;
    }

    @Override
    public void onBecomeFullyHidden() {
        Runnable runnable = this.whenDone;
        if (runnable != null) {
            runnable.run();
        }
        super.onBecomeFullyHidden();
    }

    public OpeningHoursDayActivity onDone(Runnable runnable) {
        this.whenDone = runnable;
        return this;
    }

    @Override
    public void onFragmentDestroy() {
        super.onFragmentDestroy();
        if (this.enabled || this.periods.isEmpty()) {
            return;
        }
        this.periods.clear();
        Runnable runnable = this.whenApplied;
        if (runnable != null) {
            runnable.run();
        }
    }
}
