package org.telegram.ui.Business;

import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.FrameLayout;
import j$.time.DayOfWeek;
import j$.time.format.TextStyle;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.R;
import org.telegram.messenger.UserObject;
import org.telegram.messenger.Utilities;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.tgnet.tl.TL_account;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.ActionBarMenuItem;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Cells.NotificationsCheckCell;
import org.telegram.ui.Cells.TextCell;
import org.telegram.ui.Cells.TextCheckCell;
import org.telegram.ui.Components.BulletinFactory;
import org.telegram.ui.Components.CircularProgressDrawable;
import org.telegram.ui.Components.CrossfadeDrawable;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.UItem;
import org.telegram.ui.Components.UniversalAdapter;
import org.telegram.ui.Components.UniversalRecyclerView;

public class OpeningHoursActivity extends BaseFragment implements NotificationCenter.NotificationCenterDelegate {
    public String currentTimezoneId;
    private ActionBarMenuItem doneButton;
    private CrossfadeDrawable doneButtonDrawable;
    public boolean enabled;
    private UniversalRecyclerView listView;
    public String timezoneId;
    private boolean valueSet;
    public ArrayList[] currentValue = null;
    public ArrayList[] value = {new ArrayList(), new ArrayList(), new ArrayList(), new ArrayList(), new ArrayList(), new ArrayList(), new ArrayList()};

    public static class Period {
        public int end;
        public int start;

        public Period(int i, int i2) {
            this.start = i;
            this.end = i2;
        }

        public static String timeToString(int i) {
            return timeToString(i, true);
        }

        public static String timeToString(int i, boolean z) {
            int i2 = i % 60;
            Calendar calendar = Calendar.getInstance();
            calendar.set(0, 0, 0, ((i - i2) / 60) % 24, i2);
            String format = LocaleController.getInstance().getFormatterConstDay().format(calendar.getTime());
            return (i <= 1440 || !z) ? format : LocaleController.formatString(R.string.BusinessHoursNextDay, format);
        }

        public String toString() {
            return timeToString(this.start) + " - " + timeToString(this.end);
        }
    }

    private void adaptPrevDay(int i) {
        Period period;
        Period period2 = null;
        if (this.value[i].isEmpty()) {
            period = null;
        } else {
            ArrayList arrayList = this.value[i];
            period = (Period) arrayList.get(arrayList.size() - 1);
        }
        if (period == null) {
            return;
        }
        int i2 = (i + 6) % 7;
        if (!this.value[i2].isEmpty()) {
            ArrayList arrayList2 = this.value[i2];
            period2 = (Period) arrayList2.get(arrayList2.size() - 1);
        }
        if (period2 == null || period2.end <= 1439) {
            return;
        }
        period2.end = 1439;
        if (period2.start >= 1439) {
            this.value[i2].remove(period2);
        }
        View findViewByItemId = this.listView.findViewByItemId(i2);
        if (findViewByItemId instanceof NotificationsCheckCell) {
            ((NotificationsCheckCell) findViewByItemId).setValue(getPeriodsValue(this.value[i2]));
        } else {
            this.listView.adapter.update(true);
        }
    }

    public static ArrayList adaptWeeklyOpen(ArrayList arrayList, int i) {
        int i2;
        ArrayList arrayList2 = new ArrayList(arrayList);
        ArrayList arrayList3 = new ArrayList(arrayList2.size());
        for (int i3 = 0; i3 < arrayList2.size(); i3++) {
            TL_account.TL_businessWeeklyOpen tL_businessWeeklyOpen = (TL_account.TL_businessWeeklyOpen) arrayList2.get(i3);
            TL_account.TL_businessWeeklyOpen tL_businessWeeklyOpen2 = new TL_account.TL_businessWeeklyOpen();
            if (i != 0) {
                int i4 = tL_businessWeeklyOpen.start_minute;
                int i5 = i4 % 1440;
                int i6 = tL_businessWeeklyOpen.end_minute;
                int i7 = (i6 - i4) + i5;
                if (i5 == 0 && (i7 == 1440 || i7 == 1439)) {
                    tL_businessWeeklyOpen2.start_minute = i4;
                    tL_businessWeeklyOpen2.end_minute = i6;
                    arrayList3.add(tL_businessWeeklyOpen2);
                }
            }
            tL_businessWeeklyOpen2.start_minute = tL_businessWeeklyOpen.start_minute + i;
            tL_businessWeeklyOpen2.end_minute = tL_businessWeeklyOpen.end_minute + i;
            arrayList3.add(tL_businessWeeklyOpen2);
            int i8 = tL_businessWeeklyOpen2.start_minute;
            int i9 = tL_businessWeeklyOpen2.end_minute;
            if (i8 < 0) {
                if (i9 < 0) {
                    tL_businessWeeklyOpen2.start_minute = i8 + 10080;
                    i2 = i9 + 10080;
                    tL_businessWeeklyOpen2.end_minute = i2;
                } else {
                    tL_businessWeeklyOpen2.start_minute = 0;
                    tL_businessWeeklyOpen2 = new TL_account.TL_businessWeeklyOpen();
                    tL_businessWeeklyOpen2.start_minute = tL_businessWeeklyOpen.start_minute + 10080 + i;
                    tL_businessWeeklyOpen2.end_minute = 10079;
                    arrayList3.add(tL_businessWeeklyOpen2);
                }
            } else if (i9 > 10080) {
                if (i8 > 10080) {
                    tL_businessWeeklyOpen2.start_minute = i8 - 10080;
                    i2 = i9 - 10080;
                    tL_businessWeeklyOpen2.end_minute = i2;
                } else {
                    tL_businessWeeklyOpen2.end_minute = 10079;
                    tL_businessWeeklyOpen2 = new TL_account.TL_businessWeeklyOpen();
                    tL_businessWeeklyOpen2.start_minute = 0;
                    tL_businessWeeklyOpen2.end_minute = (tL_businessWeeklyOpen.end_minute + i) - 10079;
                    arrayList3.add(tL_businessWeeklyOpen2);
                }
            }
        }
        Collections.sort(arrayList3, new Comparator() {
            @Override
            public final int compare(Object obj, Object obj2) {
                int lambda$adaptWeeklyOpen$0;
                lambda$adaptWeeklyOpen$0 = OpeningHoursActivity.lambda$adaptWeeklyOpen$0((TL_account.TL_businessWeeklyOpen) obj, (TL_account.TL_businessWeeklyOpen) obj2);
                return lambda$adaptWeeklyOpen$0;
            }
        });
        return arrayList3;
    }

    private void checkDone(boolean z) {
        if (this.doneButton == null) {
            return;
        }
        boolean hasChanges = hasChanges();
        this.doneButton.setEnabled(hasChanges);
        if (z) {
            this.doneButton.animate().alpha(hasChanges ? 1.0f : 0.0f).scaleX(hasChanges ? 1.0f : 0.0f).scaleY(hasChanges ? 1.0f : 0.0f).setDuration(180L).start();
            return;
        }
        this.doneButton.setAlpha(hasChanges ? 1.0f : 0.0f);
        this.doneButton.setScaleX(hasChanges ? 1.0f : 0.0f);
        this.doneButton.setScaleY(hasChanges ? 1.0f : 0.0f);
    }

    public void fillItems(ArrayList arrayList, UniversalAdapter universalAdapter) {
        arrayList.add(UItem.asTopView(LocaleController.getString(R.string.BusinessHoursInfo), R.raw.biz_clock));
        arrayList.add(UItem.asCheck(-1, LocaleController.getString(R.string.BusinessHoursShow)).setChecked(this.enabled));
        arrayList.add(UItem.asShadow(-100, null));
        if (!this.enabled) {
            return;
        }
        arrayList.add(UItem.asHeader(LocaleController.getString(R.string.BusinessHours)));
        int i = 0;
        while (true) {
            ArrayList[] arrayListArr = this.value;
            if (i >= arrayListArr.length) {
                arrayList.add(UItem.asShadow(-101, null));
                arrayList.add(UItem.asButton(-2, LocaleController.getString(R.string.BusinessHoursTimezone), TimezonesController.getInstance(this.currentAccount).getTimezoneName(this.timezoneId, false)));
                arrayList.add(UItem.asShadow(-102, null));
                return;
            }
            if (arrayListArr[i] == null) {
                arrayListArr[i] = new ArrayList();
            }
            String displayName = DayOfWeek.values()[i].getDisplayName(TextStyle.FULL, LocaleController.getInstance().getCurrentLocale());
            arrayList.add(UItem.asButtonCheck(i, displayName.substring(0, 1).toUpperCase() + displayName.substring(1), getPeriodsValue(this.value[i])).setChecked(!this.value[i].isEmpty()));
            i++;
        }
    }

    public static ArrayList fromDaysHours(ArrayList[] arrayListArr) {
        ArrayList arrayList = new ArrayList();
        if (arrayListArr != null) {
            for (int i = 0; i < arrayListArr.length; i++) {
                if (arrayListArr[i] != null) {
                    for (int i2 = 0; i2 < arrayListArr[i].size(); i2++) {
                        Period period = (Period) arrayListArr[i].get(i2);
                        TL_account.TL_businessWeeklyOpen tL_businessWeeklyOpen = new TL_account.TL_businessWeeklyOpen();
                        int i3 = i * 1440;
                        tL_businessWeeklyOpen.start_minute = period.start + i3;
                        tL_businessWeeklyOpen.end_minute = i3 + period.end;
                        arrayList.add(tL_businessWeeklyOpen);
                    }
                }
            }
        }
        return arrayList;
    }

    public static ArrayList[] getDaysHours(ArrayList arrayList) {
        int i;
        ArrayList[] arrayListArr = new ArrayList[7];
        for (int i2 = 0; i2 < 7; i2++) {
            arrayListArr[i2] = new ArrayList();
        }
        for (int i3 = 0; i3 < arrayList.size(); i3++) {
            TL_account.TL_businessWeeklyOpen tL_businessWeeklyOpen = (TL_account.TL_businessWeeklyOpen) arrayList.get(i3);
            int i4 = tL_businessWeeklyOpen.start_minute;
            int i5 = i4 % 1440;
            arrayListArr[(i4 / 1440) % 7].add(new Period(i5, (tL_businessWeeklyOpen.end_minute - i4) + i5));
        }
        int i6 = 0;
        while (i6 < 7) {
            int i7 = i6 + 1;
            int i8 = i7 * 1440;
            int i9 = i6 * 1440;
            for (int i10 = 0; i10 < arrayList.size(); i10++) {
                TL_account.TL_businessWeeklyOpen tL_businessWeeklyOpen2 = (TL_account.TL_businessWeeklyOpen) arrayList.get(i10);
                if (tL_businessWeeklyOpen2.start_minute <= i9 && (i = tL_businessWeeklyOpen2.end_minute) >= i9) {
                    i9 = i + 1;
                }
            }
            if (i9 >= i8) {
                int i11 = (i6 + 6) % 7;
                if (!arrayListArr[i11].isEmpty()) {
                    if (((Period) arrayListArr[i11].get(r10.size() - 1)).end >= 1440) {
                        ((Period) arrayListArr[i11].get(r6.size() - 1)).end = 1439;
                    }
                }
                int min = Math.min((i9 - r4) - 1, 2879);
                ArrayList arrayList2 = arrayListArr[(i6 + 8) % 7];
                if (min >= 1440 && !arrayList2.isEmpty() && ((Period) arrayList2.get(0)).start < min - 1440) {
                    min = ((Period) arrayList2.get(0)).start + 1439;
                }
                arrayListArr[i6].clear();
                arrayListArr[i6].add(new Period(0, min));
            } else {
                int i12 = i7 % 7;
                if (!arrayListArr[i6].isEmpty() && !arrayListArr[i12].isEmpty()) {
                    Period period = (Period) arrayListArr[i6].get(r3.size() - 1);
                    Period period2 = (Period) arrayListArr[i12].get(0);
                    int i13 = period.end;
                    if (i13 > 1440 && i13 - 1439 == period2.start) {
                        period.end = 1439;
                        period2.start = 0;
                    }
                }
            }
            i6 = i7;
        }
        return arrayListArr;
    }

    private String getPeriodsValue(ArrayList arrayList) {
        int i;
        if (arrayList.isEmpty()) {
            i = R.string.BusinessHoursDayClosed;
        } else {
            if (!isFull(arrayList)) {
                String str = "";
                for (int i2 = 0; i2 < arrayList.size(); i2++) {
                    Period period = (Period) arrayList.get(i2);
                    if (i2 > 0) {
                        str = str + "\n";
                    }
                    str = str + Period.timeToString(period.start) + " - " + Period.timeToString(period.end);
                }
                return str;
            }
            i = R.string.BusinessHoursDayFullOpened;
        }
        return LocaleController.getString(i);
    }

    public static boolean is24x7(TL_account.TL_businessWorkHours tL_businessWorkHours) {
        if (tL_businessWorkHours == null || tL_businessWorkHours.weekly_open.isEmpty()) {
            return false;
        }
        int i = 0;
        for (int i2 = 0; i2 < tL_businessWorkHours.weekly_open.size(); i2++) {
            TL_account.TL_businessWeeklyOpen tL_businessWeeklyOpen = tL_businessWorkHours.weekly_open.get(i2);
            if (tL_businessWeeklyOpen.start_minute > i + 1) {
                return false;
            }
            i = tL_businessWeeklyOpen.end_minute;
        }
        return i >= 10079;
    }

    public static boolean isFull(ArrayList arrayList) {
        if (arrayList == null || arrayList.isEmpty()) {
            return false;
        }
        int i = 0;
        for (int i2 = 0; i2 < arrayList.size(); i2++) {
            Period period = (Period) arrayList.get(i2);
            if (i < period.start) {
                return false;
            }
            i = period.end;
        }
        return i == 1439 || i == 1440;
    }

    public static int lambda$adaptWeeklyOpen$0(TL_account.TL_businessWeeklyOpen tL_businessWeeklyOpen, TL_account.TL_businessWeeklyOpen tL_businessWeeklyOpen2) {
        return tL_businessWeeklyOpen.start_minute - tL_businessWeeklyOpen2.start_minute;
    }

    public void lambda$onClick$3(View view, String str) {
        TimezonesController timezonesController = TimezonesController.getInstance(this.currentAccount);
        this.timezoneId = str;
        ((TextCell) view).setValue(timezonesController.getTimezoneName(str, false), true);
        checkDone(true);
    }

    public void lambda$onClick$4() {
        this.listView.adapter.update(true);
        checkDone(true);
    }

    public void lambda$onClick$5(UItem uItem) {
        adaptPrevDay(uItem.id);
    }

    public void lambda$processDone$1(TLRPC.TL_error tL_error, TLObject tLObject) {
        if (tL_error != null) {
            this.doneButtonDrawable.animateToProgress(0.0f);
            BulletinFactory.showError(tL_error);
        } else {
            if (tLObject instanceof TLRPC.TL_boolFalse) {
                if (getContext() == null) {
                    return;
                }
                this.doneButtonDrawable.animateToProgress(0.0f);
                BulletinFactory.of(this).createErrorBulletin(LocaleController.getString(R.string.UnknownError)).show();
                return;
            }
            if (this.isFinished || this.finishing) {
                return;
            }
            lambda$onBackPressed$323();
        }
    }

    public void lambda$processDone$2(final TLObject tLObject, final TLRPC.TL_error tL_error) {
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                OpeningHoursActivity.this.lambda$processDone$1(tL_error, tLObject);
            }
        });
    }

    private int maxPeriodsFor(int i) {
        int i2 = 0;
        for (int i3 = 0; i3 < 7; i3++) {
            ArrayList arrayList = this.value[i3];
            if (arrayList != null) {
                i2 += Math.max(1, arrayList.size());
            }
        }
        return 28 - i2;
    }

    public void onClick(final UItem uItem, final View view, int i, float f, float f2) {
        BaseFragment onDone;
        int i2 = uItem.id;
        if (i2 != -1) {
            if (i2 == -2) {
                onDone = new TimezoneSelector().setValue(this.timezoneId).whenSelected(new Utilities.Callback() {
                    @Override
                    public final void run(Object obj) {
                        OpeningHoursActivity.this.lambda$onClick$3(view, (String) obj);
                    }
                });
            } else {
                if (uItem.viewType != 5 || i2 < 0 || i2 >= this.value.length) {
                    return;
                }
                if (!LocaleController.isRTL ? f < view.getMeasuredWidth() - AndroidUtilities.dp(76.0f) : f > AndroidUtilities.dp(76.0f)) {
                    int i3 = (uItem.id + 6) % 7;
                    int i4 = 0;
                    for (int i5 = 0; i5 < this.value[i3].size(); i5++) {
                        if (((Period) this.value[i3].get(i5)).end > i4) {
                            i4 = ((Period) this.value[i3].get(i5)).end;
                        }
                    }
                    int max = Math.max(0, i4 - 1439);
                    int i6 = (uItem.id + 1) % 7;
                    int i7 = 1440;
                    for (int i8 = 0; i8 < this.value[i6].size(); i8++) {
                        if (((Period) this.value[i6].get(i8)).start < i7) {
                            i7 = ((Period) this.value[i6].get(i8)).start;
                        }
                    }
                    int i9 = i7 + 1439;
                    CharSequence charSequence = uItem.text;
                    ArrayList[] arrayListArr = this.value;
                    int i10 = uItem.id;
                    onDone = new OpeningHoursDayActivity(charSequence, arrayListArr[i10], max, i9, maxPeriodsFor(i10)).onApplied(new Runnable() {
                        @Override
                        public final void run() {
                            OpeningHoursActivity.this.lambda$onClick$4();
                        }
                    }).onDone(new Runnable() {
                        @Override
                        public final void run() {
                            OpeningHoursActivity.this.lambda$onClick$5(uItem);
                        }
                    });
                } else {
                    if (this.value[uItem.id].isEmpty()) {
                        ((NotificationsCheckCell) view).setChecked(true);
                        this.value[uItem.id].add(new Period(0, 1439));
                        adaptPrevDay(uItem.id);
                    } else {
                        this.value[uItem.id].clear();
                        ((NotificationsCheckCell) view).setChecked(false);
                    }
                    ((NotificationsCheckCell) view).setValue(getPeriodsValue(this.value[uItem.id]));
                }
            }
            presentFragment(onDone);
            return;
        }
        boolean z = !this.enabled;
        this.enabled = z;
        ((TextCheckCell) view).setChecked(z);
        this.listView.adapter.update(true);
        checkDone(true);
    }

    public void processDone() {
        if (this.doneButtonDrawable.getProgress() > 0.0f) {
            return;
        }
        if (!hasChanges()) {
            lambda$onBackPressed$323();
            return;
        }
        this.doneButtonDrawable.animateToProgress(1.0f);
        TLRPC.UserFull userFull = getMessagesController().getUserFull(getUserConfig().getClientUserId());
        TL_account.updateBusinessWorkHours updatebusinessworkhours = new TL_account.updateBusinessWorkHours();
        ArrayList fromDaysHours = fromDaysHours(this.value);
        if (this.enabled && !fromDaysHours.isEmpty()) {
            TL_account.TL_businessWorkHours tL_businessWorkHours = new TL_account.TL_businessWorkHours();
            tL_businessWorkHours.timezone_id = this.timezoneId;
            tL_businessWorkHours.weekly_open.addAll(fromDaysHours);
            updatebusinessworkhours.flags |= 1;
            updatebusinessworkhours.business_work_hours = tL_businessWorkHours;
            if (userFull != null) {
                userFull.flags2 |= 1;
                userFull.business_work_hours = tL_businessWorkHours;
            }
        } else if (userFull != null) {
            userFull.flags2 &= -2;
            userFull.business_work_hours = null;
        }
        getConnectionsManager().sendRequest(updatebusinessworkhours, new RequestDelegate() {
            @Override
            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                OpeningHoursActivity.this.lambda$processDone$2(tLObject, tL_error);
            }
        });
        getMessagesStorage().updateUserInfo(userFull, false);
    }

    private void setValue() {
        UniversalAdapter universalAdapter;
        if (this.valueSet) {
            return;
        }
        TLRPC.UserFull userFull = getMessagesController().getUserFull(getUserConfig().getClientUserId());
        if (userFull == null) {
            getMessagesController().loadUserInfo(getUserConfig().getCurrentUser(), true, getClassGuid());
            return;
        }
        TL_account.TL_businessWorkHours tL_businessWorkHours = userFull.business_work_hours;
        boolean z = tL_businessWorkHours != null;
        this.enabled = z;
        if (!z) {
            String systemTimezoneId = TimezonesController.getInstance(this.currentAccount).getSystemTimezoneId();
            this.timezoneId = systemTimezoneId;
            this.currentTimezoneId = systemTimezoneId;
            this.currentValue = null;
            this.value = new ArrayList[7];
            int i = 0;
            while (true) {
                ArrayList[] arrayListArr = this.value;
                if (i >= arrayListArr.length) {
                    break;
                }
                arrayListArr[i] = new ArrayList();
                if (i >= 0 && i < 5) {
                    this.value[i].add(new Period(0, 1439));
                }
                i++;
            }
        } else {
            String str = tL_businessWorkHours.timezone_id;
            this.timezoneId = str;
            this.currentTimezoneId = str;
            this.currentValue = getDaysHours(tL_businessWorkHours.weekly_open);
            this.value = getDaysHours(userFull.business_work_hours.weekly_open);
        }
        UniversalRecyclerView universalRecyclerView = this.listView;
        if (universalRecyclerView != null && (universalAdapter = universalRecyclerView.adapter) != null) {
            universalAdapter.update(true);
        }
        checkDone(false);
        this.valueSet = true;
    }

    public static String toString(int i, TLRPC.User user, TL_account.TL_businessWorkHours tL_businessWorkHours) {
        int i2;
        if (tL_businessWorkHours == null) {
            return null;
        }
        ArrayList[] daysHours = getDaysHours(tL_businessWorkHours.weekly_open);
        StringBuilder sb = new StringBuilder();
        if (user != null) {
            sb.append(LocaleController.formatString(R.string.BusinessHoursCopyHeader, UserObject.getUserName(user)));
            sb.append("\n");
        }
        for (int i3 = 0; i3 < daysHours.length; i3++) {
            ArrayList arrayList = daysHours[i3];
            String displayName = DayOfWeek.values()[i3].getDisplayName(TextStyle.FULL, LocaleController.getInstance().getCurrentLocale());
            sb.append(displayName.substring(0, 1).toUpperCase() + displayName.substring(1));
            sb.append(": ");
            if (isFull(arrayList)) {
                i2 = R.string.BusinessHoursProfileOpen;
            } else if (arrayList.isEmpty()) {
                i2 = R.string.BusinessHoursProfileClose;
            } else {
                for (int i4 = 0; i4 < arrayList.size(); i4++) {
                    if (i4 > 0) {
                        sb.append(", ");
                    }
                    Period period = (Period) arrayList.get(i4);
                    sb.append(Period.timeToString(period.start));
                    sb.append(" - ");
                    sb.append(Period.timeToString(period.end));
                }
                sb.append("\n");
            }
            sb.append(LocaleController.getString(i2));
            sb.append("\n");
        }
        TLRPC.TL_timezone findTimezone = TimezonesController.getInstance(i).findTimezone(tL_businessWorkHours.timezone_id);
        if (((Calendar.getInstance().getTimeZone().getRawOffset() / 1000) - (findTimezone == null ? 0 : findTimezone.utc_offset)) / 60 != 0 && findTimezone != null) {
            sb.append(LocaleController.formatString(R.string.BusinessHoursCopyFooter, TimezonesController.getInstance(i).getTimezoneName(findTimezone, true)));
        }
        return sb.toString();
    }

    @Override
    public View createView(Context context) {
        this.actionBar.setBackButtonImage(R.drawable.ic_ab_back);
        this.actionBar.setAllowOverlayTitle(true);
        this.actionBar.setTitle(LocaleController.getString(R.string.BusinessHours));
        this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
            @Override
            public void onItemClick(int i) {
                if (i == -1) {
                    if (OpeningHoursActivity.this.onBackPressed()) {
                        OpeningHoursActivity.this.lambda$onBackPressed$323();
                    }
                } else if (i == 1) {
                    OpeningHoursActivity.this.processDone();
                }
            }
        });
        Drawable mutate = context.getResources().getDrawable(R.drawable.ic_ab_done).mutate();
        int i = Theme.key_actionBarDefaultIcon;
        mutate.setColorFilter(new PorterDuffColorFilter(Theme.getColor(i), PorterDuff.Mode.MULTIPLY));
        this.doneButtonDrawable = new CrossfadeDrawable(mutate, new CircularProgressDrawable(Theme.getColor(i)));
        this.doneButton = this.actionBar.createMenu().addItemWithWidth(1, this.doneButtonDrawable, AndroidUtilities.dp(56.0f), LocaleController.getString(R.string.Done));
        checkDone(false);
        FrameLayout frameLayout = new FrameLayout(context);
        frameLayout.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundGray));
        UniversalRecyclerView universalRecyclerView = new UniversalRecyclerView(this, new Utilities.Callback2() {
            @Override
            public final void run(Object obj, Object obj2) {
                OpeningHoursActivity.this.fillItems((ArrayList) obj, (UniversalAdapter) obj2);
            }
        }, new Utilities.Callback5() {
            @Override
            public final void run(Object obj, Object obj2, Object obj3, Object obj4, Object obj5) {
                OpeningHoursActivity.this.onClick((UItem) obj, (View) obj2, ((Integer) obj3).intValue(), ((Float) obj4).floatValue(), ((Float) obj5).floatValue());
            }
        }, null);
        this.listView = universalRecyclerView;
        frameLayout.addView(universalRecyclerView, LayoutHelper.createFrame(-1, -1.0f));
        setValue();
        this.fragmentView = frameLayout;
        return frameLayout;
    }

    @Override
    public void didReceivedNotification(int i, int i2, Object... objArr) {
        UniversalAdapter universalAdapter;
        if (i == NotificationCenter.userInfoDidLoad) {
            setValue();
            return;
        }
        if (i == NotificationCenter.timezonesUpdated) {
            if (this.currentValue == null) {
                this.timezoneId = TimezonesController.getInstance(this.currentAccount).getSystemTimezoneId();
            }
            UniversalRecyclerView universalRecyclerView = this.listView;
            if (universalRecyclerView == null || (universalAdapter = universalRecyclerView.adapter) == null) {
                return;
            }
            universalAdapter.update(true);
        }
    }

    public boolean hasChanges() {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Business.OpeningHoursActivity.hasChanges():boolean");
    }

    @Override
    public boolean onFragmentCreate() {
        TimezonesController.getInstance(this.currentAccount).load();
        this.timezoneId = TimezonesController.getInstance(this.currentAccount).getSystemTimezoneId();
        getNotificationCenter().addObserver(this, NotificationCenter.userInfoDidLoad);
        return super.onFragmentCreate();
    }

    @Override
    public void onFragmentDestroy() {
        getNotificationCenter().removeObserver(this, NotificationCenter.userInfoDidLoad);
        super.onFragmentDestroy();
        processDone();
    }
}
