package org.telegram.ui.Components;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Outline;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Vibrator;
import android.text.Editable;
import android.text.Html;
import android.text.InputFilter;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.style.URLSpan;
import android.util.Base64;
import android.util.SparseArray;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewOutlineProvider;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.core.util.Consumer;
import j$.time.YearMonth;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;
import org.telegram.messenger.AccountInstance;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.BuildVars;
import org.telegram.messenger.ChatObject;
import org.telegram.messenger.ContactsController;
import org.telegram.messenger.DialogObject;
import org.telegram.messenger.Emoji;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.ImageLocation;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.MessagesStorage;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.NotificationsController;
import org.telegram.messenger.OneUIUtilities;
import org.telegram.messenger.R;
import org.telegram.messenger.SharedConfig;
import org.telegram.messenger.SvgHelper;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.UserObject;
import org.telegram.messenger.Utilities;
import org.telegram.messenger.browser.Browser;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.SerializedData;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.tgnet.tl.TL_account;
import org.telegram.ui.ActionBar.ActionBarMenuItem;
import org.telegram.ui.ActionBar.ActionBarPopupWindow;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.BottomSheet;
import org.telegram.ui.ActionBar.SimpleTextView;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.CacheControlActivity;
import org.telegram.ui.Cells.AccountSelectCell;
import org.telegram.ui.Cells.CheckBoxCell;
import org.telegram.ui.Cells.RadioColorCell;
import org.telegram.ui.Cells.TextColorCell;
import org.telegram.ui.ChatActivity;
import org.telegram.ui.Components.AlertsCreator;
import org.telegram.ui.Components.Bulletin;
import org.telegram.ui.Components.LinkSpanDrawable;
import org.telegram.ui.Components.NumberPicker;
import org.telegram.ui.Components.Premium.LimitReachedBottomSheet;
import org.telegram.ui.Components.voip.VoIPHelper;
import org.telegram.ui.LanguageSelectActivity;
import org.telegram.ui.LaunchActivity;
import org.telegram.ui.NotificationsCustomSettingsActivity;
import org.telegram.ui.PrivacyControlActivity;
import org.telegram.ui.ProfileActivity;
import org.telegram.ui.Stories.recorder.ButtonWithCounterView;
import org.telegram.ui.ThemePreviewActivity;

public abstract class AlertsCreator {

    public interface AccountSelectDelegate {
        void didSelectAccount(int i);
    }

    public interface BlockDialogCallback {
        void run(boolean z, boolean z2);
    }

    public interface DatePickerDelegate {
        void didSelectDate(int i, int i2, int i3);
    }

    public static class ScheduleDatePickerColors {
        public final int backgroundColor;
        public final int buttonBackgroundColor;
        public final int buttonBackgroundPressedColor;
        public final int buttonTextColor;
        public final int iconColor;
        public final int iconSelectorColor;
        public final int subMenuBackgroundColor;
        public final int subMenuSelectorColor;
        public final int subMenuTextColor;
        public final int textColor;

        private ScheduleDatePickerColors() {
            this((Theme.ResourcesProvider) null);
        }

        public ScheduleDatePickerColors(int i, int i2, int i3, int i4, int i5, int i6, int i7) {
            this(i, i2, i3, i4, i5, i6, i7, Theme.getColor(Theme.key_featuredStickers_buttonText), Theme.getColor(Theme.key_featuredStickers_addButton), Theme.getColor(Theme.key_featuredStickers_addButtonPressed));
        }

        public ScheduleDatePickerColors(int i, int i2, int i3, int i4, int i5, int i6, int i7, int i8, int i9, int i10) {
            this.textColor = i;
            this.backgroundColor = i2;
            this.iconColor = i3;
            this.iconSelectorColor = i4;
            this.subMenuTextColor = i5;
            this.subMenuBackgroundColor = i6;
            this.subMenuSelectorColor = i7;
            this.buttonTextColor = i8;
            this.buttonBackgroundColor = i9;
            this.buttonBackgroundPressedColor = i10;
        }

        public ScheduleDatePickerColors(org.telegram.ui.ActionBar.Theme.ResourcesProvider r13) {
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.AlertsCreator.ScheduleDatePickerColors.<init>(org.telegram.ui.ActionBar.Theme$ResourcesProvider):void");
        }
    }

    public interface ScheduleDatePickerDelegate {
        void didSelectDate(boolean z, int i);
    }

    public interface SoundFrequencyDelegate {
        void didSelectValues(int i, int i2);
    }

    public interface StatusUntilDatePickerDelegate {
        void didSelectDate(int i);
    }

    public static void checkCalendarDate(long j, NumberPicker numberPicker, NumberPicker numberPicker2, NumberPicker numberPicker3) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(j);
        int i = 1;
        int i2 = calendar.get(1);
        int i3 = calendar.get(2);
        int i4 = calendar.get(5);
        calendar.setTimeInMillis(System.currentTimeMillis());
        int i5 = calendar.get(1);
        int i6 = calendar.get(2);
        int i7 = calendar.get(5);
        numberPicker3.setMaxValue(i5);
        numberPicker3.setMinValue(i2);
        int value = numberPicker3.getValue();
        numberPicker2.setMaxValue(value == i5 ? i6 : 11);
        numberPicker2.setMinValue(value == i2 ? i3 : 0);
        int value2 = numberPicker2.getValue();
        calendar.set(1, value);
        calendar.set(2, value2);
        int actualMaximum = calendar.getActualMaximum(5);
        if (value == i5 && value2 == i6) {
            actualMaximum = Math.min(i7, actualMaximum);
        }
        numberPicker.setMaxValue(actualMaximum);
        if (value == i2 && value2 == i3) {
            i = i4;
        }
        numberPicker.setMinValue(i);
    }

    private static boolean checkInternalBotApp(String str) {
        return Uri.parse(str).getPath().matches("^/\\w*/[^\\d]*(?:\\?startapp=.*?|)$");
    }

    private static void checkPickerDate(NumberPicker numberPicker, NumberPicker numberPicker2, NumberPicker numberPicker3) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        int i = 1;
        int i2 = calendar.get(1);
        int i3 = calendar.get(2);
        int i4 = calendar.get(5);
        numberPicker3.setMinValue(i2);
        int value = numberPicker3.getValue();
        numberPicker2.setMinValue(value == i2 ? i3 : 0);
        int value2 = numberPicker2.getValue();
        if (value == i2 && value2 == i3) {
            i = i4;
        }
        numberPicker.setMinValue(i);
    }

    public static void checkRestrictedInviteUsers(final int i, final TLRPC.Chat chat, TLRPC.TL_messages_invitedUsers tL_messages_invitedUsers) {
        TLRPC.User user;
        if (tL_messages_invitedUsers == null || tL_messages_invitedUsers.missing_invitees.isEmpty() || chat == null) {
            return;
        }
        final ArrayList arrayList = new ArrayList();
        final ArrayList arrayList2 = new ArrayList();
        final ArrayList arrayList3 = new ArrayList();
        Iterator<TLRPC.TL_missingInvitee> it = tL_messages_invitedUsers.missing_invitees.iterator();
        while (it.hasNext()) {
            TLRPC.TL_missingInvitee next = it.next();
            if (tL_messages_invitedUsers.updates != null) {
                for (int i2 = 0; i2 < tL_messages_invitedUsers.updates.users.size(); i2++) {
                    user = tL_messages_invitedUsers.updates.users.get(i2);
                    if (user.id == next.user_id) {
                        break;
                    }
                }
            }
            user = null;
            if (user == null) {
                user = MessagesController.getInstance(i).getUser(Long.valueOf(next.user_id));
            }
            if (user != null) {
                arrayList.add(user);
                if (next.premium_required_for_pm) {
                    arrayList2.add(Long.valueOf(user.id));
                }
                if (next.premium_would_allow_invite) {
                    arrayList3.add(Long.valueOf(user.id));
                }
            }
        }
        if (arrayList.isEmpty()) {
            return;
        }
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                AlertsCreator.lambda$checkRestrictedInviteUsers$55(i, chat, arrayList, arrayList2, arrayList3);
            }
        }, 200L);
    }

    public static boolean checkScheduleDate(TextView textView, TextView textView2, int i, NumberPicker numberPicker, NumberPicker numberPicker2, NumberPicker numberPicker3) {
        return checkScheduleDate(textView, textView2, 0L, i, numberPicker, numberPicker2, numberPicker3);
    }

    public static boolean checkScheduleDate(TextView textView, TextView textView2, long j, int i, NumberPicker numberPicker, NumberPicker numberPicker2, NumberPicker numberPicker3) {
        int i2;
        long j2;
        int i3;
        int i4;
        int i5;
        int i6;
        char c;
        String formatPluralString;
        String formatString;
        int i7;
        int i8;
        int value = numberPicker.getValue();
        int value2 = numberPicker2.getValue();
        int value3 = numberPicker3.getValue();
        Calendar calendar = Calendar.getInstance();
        long currentTimeMillis = System.currentTimeMillis();
        calendar.setTimeInMillis(currentTimeMillis);
        int i9 = calendar.get(1);
        calendar.get(6);
        if (j > 0) {
            i2 = i9;
            calendar.setTimeInMillis(currentTimeMillis + (j * 1000));
            calendar.set(11, 23);
            calendar.set(12, 59);
            calendar.set(13, 59);
            i4 = 7;
            j2 = calendar.getTimeInMillis();
            i3 = 23;
            i5 = 59;
        } else {
            i2 = i9;
            j2 = j;
            i3 = 0;
            i4 = 0;
            i5 = 0;
        }
        int i10 = i4;
        long j3 = currentTimeMillis + 60000;
        calendar.setTimeInMillis(j3);
        int i11 = calendar.get(11);
        int i12 = calendar.get(12);
        int i13 = i3;
        calendar.setTimeInMillis(System.currentTimeMillis() + (value * 86400000));
        calendar.set(11, value2);
        calendar.set(12, value3);
        long timeInMillis = calendar.getTimeInMillis();
        calendar.setTimeInMillis(timeInMillis);
        numberPicker.setMinValue(0);
        long j4 = j2;
        if (j4 > 0) {
            i6 = i10;
            numberPicker.setMaxValue(i6);
        } else {
            i6 = i10;
        }
        int value4 = numberPicker.getValue();
        numberPicker2.setMinValue(value4 == 0 ? i11 : 0);
        if (j4 > 0) {
            numberPicker2.setMaxValue(value4 == i6 ? i13 : 23);
        }
        int value5 = numberPicker2.getValue();
        numberPicker3.setMinValue((value4 == 0 && value5 == i11) ? i12 : 0);
        if (j4 > 0) {
            numberPicker3.setMaxValue((value4 == i6 && value5 == i13) ? i5 : 59);
        }
        int value6 = numberPicker3.getValue();
        if (timeInMillis <= j3) {
            calendar.setTimeInMillis(j3);
        } else if (j4 > 0 && timeInMillis > j4) {
            calendar.setTimeInMillis(j4);
        }
        int i14 = calendar.get(1);
        calendar.setTimeInMillis(System.currentTimeMillis() + (value4 * 86400000));
        calendar.set(11, value5);
        calendar.set(12, value6);
        long timeInMillis2 = calendar.getTimeInMillis();
        if (textView != null) {
            if (value4 == 0) {
                i7 = 1;
                i8 = 0;
            } else if (i2 == i14) {
                i7 = 1;
                i8 = 1;
            } else {
                i7 = 1;
                i8 = 2;
            }
            if (i == i7) {
                i8 += 3;
            } else if (i == 2) {
                i8 += 6;
            } else if (i == 3) {
                i8 += 9;
            }
            textView.setText(LocaleController.getInstance().getFormatterScheduleSend(i8).format(timeInMillis2));
        }
        if (textView2 != null) {
            int i15 = (int) ((timeInMillis2 - currentTimeMillis) / 1000);
            if (i15 > 86400) {
                c = 0;
                formatPluralString = LocaleController.formatPluralString("DaysSchedule", Math.round(i15 / 86400.0f), new Object[0]);
            } else {
                c = 0;
                formatPluralString = i15 >= 3600 ? LocaleController.formatPluralString("HoursSchedule", Math.round(i15 / 3600.0f), new Object[0]) : i15 >= 60 ? LocaleController.formatPluralString("MinutesSchedule", Math.round(i15 / 60.0f), new Object[0]) : LocaleController.formatPluralString("SecondsSchedule", i15, new Object[0]);
            }
            if (textView2.getTag() != null) {
                int i16 = R.string.VoipChannelScheduleInfo;
                Object[] objArr = new Object[1];
                objArr[c] = formatPluralString;
                formatString = LocaleController.formatString("VoipChannelScheduleInfo", i16, objArr);
            } else {
                int i17 = R.string.VoipGroupScheduleInfo;
                Object[] objArr2 = new Object[1];
                objArr2[c] = formatPluralString;
                formatString = LocaleController.formatString("VoipGroupScheduleInfo", i17, objArr2);
            }
            textView2.setText(formatString);
        }
        return timeInMillis - currentTimeMillis > 60000;
    }

    public static boolean checkSlowMode(Context context, int i, long j, boolean z) {
        TLRPC.Chat chat;
        if (!DialogObject.isChatDialog(j) || (chat = MessagesController.getInstance(i).getChat(Long.valueOf(-j))) == null || !chat.slowmode_enabled || ChatObject.hasAdminRights(chat)) {
            return false;
        }
        if (!z) {
            TLRPC.ChatFull chatFull = MessagesController.getInstance(i).getChatFull(chat.id);
            if (chatFull == null) {
                chatFull = MessagesStorage.getInstance(i).loadChatInfo(chat.id, ChatObject.isChannel(chat), new CountDownLatch(1), false, false);
            }
            if (chatFull != null && chatFull.slowmode_next_send_date >= ConnectionsManager.getInstance(i).getCurrentTime()) {
                z = true;
            }
        }
        if (!z) {
            return false;
        }
        createSimpleAlert(context, chat.title, LocaleController.getString(R.string.SlowmodeSendError)).show();
        return true;
    }

    public static AlertDialog createAccountSelectDialog(Activity activity, final AccountSelectDelegate accountSelectDelegate) {
        if (UserConfig.getActivatedAccountsCount() < 2) {
            return null;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        final Runnable dismissRunnable = builder.getDismissRunnable();
        final AlertDialog[] alertDialogArr = new AlertDialog[1];
        LinearLayout linearLayout = new LinearLayout(activity);
        linearLayout.setOrientation(1);
        for (int i = 0; i < 4; i++) {
            if (UserConfig.getInstance(i).getCurrentUser() != null) {
                AccountSelectCell accountSelectCell = new AccountSelectCell(activity, false);
                accountSelectCell.setAccount(i, false);
                accountSelectCell.setPadding(AndroidUtilities.dp(14.0f), 0, AndroidUtilities.dp(14.0f), 0);
                accountSelectCell.setBackgroundDrawable(Theme.getSelectorDrawable(false));
                linearLayout.addView(accountSelectCell, LayoutHelper.createLinear(-1, 50));
                accountSelectCell.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public final void onClick(View view) {
                        AlertsCreator.lambda$createAccountSelectDialog$153(alertDialogArr, dismissRunnable, accountSelectDelegate, view);
                    }
                });
            }
        }
        builder.setTitle(LocaleController.getString(R.string.SelectAccount));
        builder.setView(linearLayout);
        builder.setPositiveButton(LocaleController.getString(R.string.Cancel), null);
        AlertDialog create = builder.create();
        alertDialogArr[0] = create;
        return create;
    }

    public static Dialog createApkRestrictedDialog(final Context context, Theme.ResourcesProvider resourcesProvider) {
        return new AlertDialog.Builder(context, resourcesProvider).setMessage(LocaleController.getString(R.string.ApkRestricted)).setTopAnimation(R.raw.permission_request_apk, 72, false, Theme.getColor(Theme.key_dialogTopBackground)).setPositiveButton(LocaleController.getString(R.string.PermissionOpenSettings), new AlertDialog.OnButtonClickListener() {
            @Override
            public final void onClick(AlertDialog alertDialog, int i) {
                AlertsCreator.lambda$createApkRestrictedDialog$6(context, alertDialog, i);
            }
        }).setNegativeButton(LocaleController.getString(R.string.ContactsPermissionAlertNotNow), null).create();
    }

    public static BottomSheet.Builder createAutoDeleteDatePickerDialog(Context context, int i, Theme.ResourcesProvider resourcesProvider, final ScheduleDatePickerDelegate scheduleDatePickerDelegate) {
        boolean z = false;
        if (context == null) {
            return null;
        }
        ScheduleDatePickerColors scheduleDatePickerColors = new ScheduleDatePickerColors(resourcesProvider);
        final BottomSheet.Builder builder = new BottomSheet.Builder(context, false, resourcesProvider);
        builder.setApplyBottomPadding(false);
        final int[] iArr = {0, 1440, 2880, 4320, 5760, 7200, 8640, 10080, 20160, 30240, 44640, 89280, 133920, 178560, 223200, 267840, 525600};
        final NumberPicker numberPicker = new NumberPicker(context, resourcesProvider) {
            @Override
            protected CharSequence getContentDescription(int i2) {
                int i3 = iArr[i2];
                return i3 == 0 ? LocaleController.getString(R.string.AutoDeleteNever) : i3 < 10080 ? LocaleController.formatPluralString("Days", i3 / 1440, new Object[0]) : i3 < 44640 ? LocaleController.formatPluralString("Weeks", i3 / 1440, new Object[0]) : i3 < 525600 ? LocaleController.formatPluralString("Months", i3 / 10080, new Object[0]) : LocaleController.formatPluralString("Years", ((i3 * 5) / 31) * 1440, new Object[0]);
            }
        };
        numberPicker.setMinValue(0);
        numberPicker.setMaxValue(16);
        numberPicker.setTextColor(scheduleDatePickerColors.textColor);
        numberPicker.setValue(0);
        numberPicker.setFormatter(new NumberPicker.Formatter() {
            @Override
            public final String format(int i2) {
                String lambda$createAutoDeleteDatePickerDialog$109;
                lambda$createAutoDeleteDatePickerDialog$109 = AlertsCreator.lambda$createAutoDeleteDatePickerDialog$109(iArr, i2);
                return lambda$createAutoDeleteDatePickerDialog$109;
            }
        });
        LinearLayout linearLayout = new LinearLayout(context) {
            boolean ignoreLayout = false;

            @Override
            protected void onMeasure(int i2, int i3) {
                this.ignoreLayout = true;
                android.graphics.Point point = AndroidUtilities.displaySize;
                int i4 = point.x > point.y ? 3 : 5;
                numberPicker.setItemCount(i4);
                numberPicker.getLayoutParams().height = AndroidUtilities.dp(42.0f) * i4;
                this.ignoreLayout = false;
                super.onMeasure(i2, i3);
            }

            @Override
            public void requestLayout() {
                if (this.ignoreLayout) {
                    return;
                }
                super.requestLayout();
            }
        };
        boolean z2 = true;
        linearLayout.setOrientation(1);
        FrameLayout frameLayout = new FrameLayout(context);
        linearLayout.addView(frameLayout, LayoutHelper.createLinear(-1, -2, 51, 22, 0, 0, 4));
        TextView textView = new TextView(context);
        textView.setText(LocaleController.getString(R.string.AutoDeleteAfteTitle));
        textView.setTextColor(scheduleDatePickerColors.textColor);
        textView.setTextSize(1, 20.0f);
        textView.setTypeface(AndroidUtilities.bold());
        frameLayout.addView(textView, LayoutHelper.createFrame(-2, -2.0f, 51, 0.0f, 12.0f, 0.0f, 0.0f));
        textView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public final boolean onTouch(View view, MotionEvent motionEvent) {
                boolean lambda$createAutoDeleteDatePickerDialog$110;
                lambda$createAutoDeleteDatePickerDialog$110 = AlertsCreator.lambda$createAutoDeleteDatePickerDialog$110(view, motionEvent);
                return lambda$createAutoDeleteDatePickerDialog$110;
            }
        });
        LinearLayout linearLayout2 = new LinearLayout(context);
        linearLayout2.setOrientation(0);
        linearLayout2.setWeightSum(1.0f);
        linearLayout.addView(linearLayout2, LayoutHelper.createLinear(-1, -2, 1.0f, 0, 0, 12, 0, 12));
        final AnimatedTextView animatedTextView = new AnimatedTextView(context, z2, z2, z) {
            @Override
            public CharSequence getAccessibilityClassName() {
                return Button.class.getName();
            }
        };
        linearLayout2.addView(numberPicker, LayoutHelper.createLinear(0, 270, 1.0f));
        animatedTextView.setPadding(0, 0, 0, 0);
        animatedTextView.setGravity(17);
        animatedTextView.setTextColor(scheduleDatePickerColors.buttonTextColor);
        animatedTextView.setTextSize(AndroidUtilities.dp(14.0f));
        animatedTextView.setTypeface(AndroidUtilities.bold());
        animatedTextView.setBackgroundDrawable(Theme.createSimpleSelectorRoundRectDrawable(AndroidUtilities.dp(8.0f), scheduleDatePickerColors.buttonBackgroundColor, scheduleDatePickerColors.buttonBackgroundPressedColor));
        linearLayout.addView(animatedTextView, LayoutHelper.createLinear(-1, 48, 83, 16, 15, 16, 16));
        animatedTextView.setText(LocaleController.getString(R.string.DisableAutoDeleteTimer));
        numberPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public final void onValueChange(NumberPicker numberPicker2, int i2, int i3) {
                AlertsCreator.lambda$createAutoDeleteDatePickerDialog$111(AnimatedTextView.this, numberPicker2, i2, i3);
            }
        });
        animatedTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public final void onClick(View view) {
                AlertsCreator.lambda$createAutoDeleteDatePickerDialog$112(iArr, numberPicker, scheduleDatePickerDelegate, builder, view);
            }
        });
        builder.setCustomView(linearLayout);
        BottomSheet show = builder.show();
        show.setBackgroundColor(scheduleDatePickerColors.backgroundColor);
        show.fixNavigationBar(scheduleDatePickerColors.backgroundColor);
        return builder;
    }

    public static Dialog createBackgroundActivityDialog(final Context context) {
        return new AlertDialog.Builder(context).setTitle(LocaleController.getString(R.string.AllowBackgroundActivity)).setMessage(AndroidUtilities.replaceTags(LocaleController.getString(OneUIUtilities.isOneUI() ? Build.VERSION.SDK_INT >= 31 ? R.string.AllowBackgroundActivityInfoOneUIAboveS : R.string.AllowBackgroundActivityInfoOneUIBelowS : R.string.AllowBackgroundActivityInfo))).setTopAnimation(R.raw.permission_request_apk, 72, false, Theme.getColor(Theme.key_dialogTopBackground)).setPositiveButton(LocaleController.getString(R.string.PermissionOpenSettings), new AlertDialog.OnButtonClickListener() {
            @Override
            public final void onClick(AlertDialog alertDialog, int i) {
                AlertsCreator.lambda$createBackgroundActivityDialog$1(context, alertDialog, i);
            }
        }).setNegativeButton(LocaleController.getString(R.string.ContactsPermissionAlertNotNow), null).setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public final void onDismiss(DialogInterface dialogInterface) {
                SharedConfig.BackgroundActivityPrefs.increaseDismissedCount();
            }
        }).create();
    }

    public static AlertDialog.Builder createBackgroundLocationPermissionDialog(final Activity activity, TLRPC.User user, final Runnable runnable, Theme.ResourcesProvider resourcesProvider) {
        if (activity == null || Build.VERSION.SDK_INT < 29) {
            return null;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(activity, resourcesProvider);
        String readRes = AndroidUtilities.readRes(Theme.getCurrentTheme().isDark() ? R.raw.permission_map_dark : R.raw.permission_map);
        String readRes2 = AndroidUtilities.readRes(Theme.getCurrentTheme().isDark() ? R.raw.permission_pin_dark : R.raw.permission_pin);
        FrameLayout frameLayout = new FrameLayout(activity);
        frameLayout.setClipToOutline(true);
        frameLayout.setOutlineProvider(new ViewOutlineProvider() {
            @Override
            public void getOutline(View view, Outline outline) {
                outline.setRoundRect(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight() + AndroidUtilities.dp(6.0f), AndroidUtilities.dp(6.0f));
            }
        });
        View view = new View(activity);
        view.setBackground(SvgHelper.getDrawable(readRes));
        frameLayout.addView(view, LayoutHelper.createFrame(-1, -1.0f, 51, 0.0f, 0.0f, 0.0f, 0.0f));
        View view2 = new View(activity);
        view2.setBackground(SvgHelper.getDrawable(readRes2));
        frameLayout.addView(view2, LayoutHelper.createFrame(60, 82.0f, 17, 0.0f, 0.0f, 0.0f, 0.0f));
        BackupImageView backupImageView = new BackupImageView(activity);
        backupImageView.setRoundRadius(AndroidUtilities.dp(26.0f));
        backupImageView.setForUserOrChat(user, new AvatarDrawable(user));
        frameLayout.addView(backupImageView, LayoutHelper.createFrame(52, 52.0f, 17, 0.0f, 0.0f, 0.0f, 11.0f));
        builder.setTopView(frameLayout);
        builder.setTopViewAspectRatio(0.37820512f);
        builder.setMessage(AndroidUtilities.replaceTags(LocaleController.getString(R.string.PermissionBackgroundLocation)));
        builder.setPositiveButton(LocaleController.getString(R.string.Continue), new AlertDialog.OnButtonClickListener() {
            @Override
            public final void onClick(AlertDialog alertDialog, int i) {
                AlertsCreator.lambda$createBackgroundLocationPermissionDialog$141(activity, alertDialog, i);
            }
        });
        builder.setNegativeButton(LocaleController.getString(R.string.Cancel), new AlertDialog.OnButtonClickListener() {
            @Override
            public final void onClick(AlertDialog alertDialog, int i) {
                runnable.run();
            }
        });
        return builder;
    }

    public static org.telegram.ui.ActionBar.BottomSheet.Builder createBirthdayPickerDialog(android.content.Context r28, java.lang.String r29, java.lang.String r30, org.telegram.tgnet.tl.TL_account.TL_birthday r31, final org.telegram.messenger.Utilities.Callback r32, java.lang.Runnable r33, org.telegram.ui.ActionBar.Theme.ResourcesProvider r34) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.AlertsCreator.createBirthdayPickerDialog(android.content.Context, java.lang.String, java.lang.String, org.telegram.tgnet.tl.TL_account$TL_birthday, org.telegram.messenger.Utilities$Callback, java.lang.Runnable, org.telegram.ui.ActionBar.Theme$ResourcesProvider):org.telegram.ui.ActionBar.BottomSheet$Builder");
    }

    public static void createBlockDialogAlert(BaseFragment baseFragment, int i, boolean z, TLRPC.User user, final BlockDialogCallback blockDialogCallback) {
        String string;
        String formatString;
        CheckBoxCell checkBoxCell;
        int i2;
        if (baseFragment == null || baseFragment.getParentActivity() == null) {
            return;
        }
        if (i == 1 && user == null) {
            return;
        }
        Activity parentActivity = baseFragment.getParentActivity();
        AlertDialog.Builder builder = new AlertDialog.Builder(parentActivity);
        CheckBoxCell[] checkBoxCellArr = new CheckBoxCell[2];
        LinearLayout linearLayout = new LinearLayout(parentActivity);
        linearLayout.setOrientation(1);
        builder.setView(linearLayout);
        if (i == 1) {
            String formatName = ContactsController.formatName(user.first_name, user.last_name);
            builder.setTitle(LocaleController.formatString("BlockUserTitle", R.string.BlockUserTitle, formatName));
            string = LocaleController.getString(R.string.BlockUser);
            formatString = LocaleController.formatString("BlockUserMessage", R.string.BlockUserMessage, formatName);
        } else {
            builder.setTitle(LocaleController.formatString("BlockUserTitle", R.string.BlockUserTitle, LocaleController.formatPluralString("UsersCountTitle", i, new Object[0])));
            string = LocaleController.getString(R.string.BlockUsers);
            formatString = LocaleController.formatString("BlockUsersMessage", R.string.BlockUsersMessage, LocaleController.formatPluralString("UsersCount", i, new Object[0]));
        }
        builder.setMessage(AndroidUtilities.replaceTags(formatString));
        final boolean[] zArr = {true, true};
        for (final int i3 = 0; i3 < 2; i3++) {
            if (i3 != 0 || z) {
                CheckBoxCell checkBoxCell2 = new CheckBoxCell(parentActivity, 1);
                checkBoxCellArr[i3] = checkBoxCell2;
                checkBoxCell2.setBackgroundDrawable(Theme.getSelectorDrawable(false));
                if (i3 == 0) {
                    checkBoxCell = checkBoxCellArr[i3];
                    i2 = R.string.ReportSpamTitle;
                } else {
                    checkBoxCell = checkBoxCellArr[i3];
                    i2 = i == 1 ? R.string.DeleteThisChatBothSides : R.string.DeleteTheseChatsBothSides;
                }
                checkBoxCell.setText(LocaleController.getString(i2), "", true, false);
                checkBoxCellArr[i3].setPadding(LocaleController.isRTL ? AndroidUtilities.dp(16.0f) : AndroidUtilities.dp(8.0f), 0, LocaleController.isRTL ? AndroidUtilities.dp(8.0f) : AndroidUtilities.dp(16.0f), 0);
                linearLayout.addView(checkBoxCellArr[i3], LayoutHelper.createLinear(-1, 48));
                checkBoxCellArr[i3].setOnClickListener(new View.OnClickListener() {
                    @Override
                    public final void onClick(View view) {
                        AlertsCreator.lambda$createBlockDialogAlert$56(zArr, i3, view);
                    }
                });
            }
        }
        builder.setPositiveButton(string, new AlertDialog.OnButtonClickListener() {
            @Override
            public final void onClick(AlertDialog alertDialog, int i4) {
                AlertsCreator.lambda$createBlockDialogAlert$57(AlertsCreator.BlockDialogCallback.this, zArr, alertDialog, i4);
            }
        });
        builder.setNegativeButton(LocaleController.getString(R.string.Cancel), null);
        AlertDialog create = builder.create();
        baseFragment.showDialog(create);
        TextView textView = (TextView) create.getButton(-1);
        if (textView != null) {
            textView.setTextColor(Theme.getColor(Theme.key_text_RedBold));
        }
    }

    public static void createBotLaunchAlert(final BaseFragment baseFragment, final AtomicBoolean atomicBoolean, final TLRPC.User user, final Runnable runnable) {
        float f;
        int dp;
        if (baseFragment == null) {
            return;
        }
        final Context context = baseFragment.getContext();
        final CheckBoxCell[] checkBoxCellArr = new CheckBoxCell[1];
        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
        LinkSpanDrawable.LinksTextView linksTextView = new LinkSpanDrawable.LinksTextView(context) {
            @Override
            public void setText(CharSequence charSequence, TextView.BufferType bufferType) {
                super.setText(Emoji.replaceEmoji(charSequence, getPaint().getFontMetricsInt(), false), bufferType);
            }
        };
        NotificationCenter.listenEmojiLoading(linksTextView);
        linksTextView.setTextColor(Theme.getColor(Theme.key_dialogTextBlack));
        linksTextView.setLinkTextColor(Theme.getColor(Theme.key_chat_messageLinkIn));
        linksTextView.setTextSize(1, 16.0f);
        linksTextView.setGravity((LocaleController.isRTL ? 5 : 3) | 48);
        FrameLayout frameLayout = new FrameLayout(context) {
            @Override
            protected void onMeasure(int i, int i2) {
                super.onMeasure(i, i2);
                if (checkBoxCellArr[0] != null) {
                    setMeasuredDimension(getMeasuredWidth(), getMeasuredHeight() + checkBoxCellArr[0].getMeasuredHeight() + AndroidUtilities.dp(7.0f));
                }
            }
        };
        builder.setCustomViewOffset(6);
        builder.setView(frameLayout);
        AvatarDrawable avatarDrawable = new AvatarDrawable();
        avatarDrawable.setTextSize(AndroidUtilities.dp(18.0f));
        BackupImageView backupImageView = new BackupImageView(context);
        backupImageView.setRoundRadius(AndroidUtilities.dp(20.0f));
        frameLayout.addView(backupImageView, LayoutHelper.createFrame(40, 40.0f, (LocaleController.isRTL ? 5 : 3) | 48, 22.0f, 5.0f, 22.0f, 0.0f));
        SimpleTextView simpleTextView = new SimpleTextView(context);
        simpleTextView.setTextColor(Theme.getColor(Theme.key_actionBarDefaultSubmenuItem));
        simpleTextView.setTextSize(20);
        simpleTextView.setTypeface(AndroidUtilities.bold());
        simpleTextView.setGravity((LocaleController.isRTL ? 5 : 3) | 16);
        simpleTextView.setEllipsizeByGradient(true);
        simpleTextView.setText(user.first_name);
        if (user.verified) {
            Drawable mutate = context.getResources().getDrawable(R.drawable.verified_area).mutate();
            int color = Theme.getColor(Theme.key_chats_verifiedBackground);
            PorterDuff.Mode mode = PorterDuff.Mode.MULTIPLY;
            mutate.setColorFilter(new PorterDuffColorFilter(color, mode));
            Drawable mutate2 = context.getResources().getDrawable(R.drawable.verified_check).mutate();
            mutate2.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_chats_verifiedCheck), mode));
            simpleTextView.setRightDrawable(new CombinedDrawable(mutate, mutate2));
        }
        TextView textView = new TextView(context);
        textView.setTextColor(Theme.getColor(Theme.key_dialogTextBlue));
        textView.setTextSize(1, 14.0f);
        textView.setLines(1);
        textView.setMaxLines(1);
        textView.setSingleLine(true);
        textView.setGravity((LocaleController.isRTL ? 5 : 3) | 16);
        textView.setEllipsize(TextUtils.TruncateAt.END);
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public final void onClick(View view) {
                AlertsCreator.lambda$createBotLaunchAlert$30(TLRPC.User.this, baseFragment, builder, view);
            }
        });
        SpannableString valueOf = SpannableString.valueOf(LocaleController.getString(R.string.MoreAboutThisBot) + "  ");
        ColoredImageSpan coloredImageSpan = new ColoredImageSpan(R.drawable.attach_arrow_right);
        coloredImageSpan.setTopOffset(1);
        coloredImageSpan.setSize(AndroidUtilities.dp(10.0f));
        valueOf.setSpan(coloredImageSpan, valueOf.length() - 1, valueOf.length(), 33);
        textView.setText(valueOf);
        boolean z = LocaleController.isRTL;
        frameLayout.addView(simpleTextView, LayoutHelper.createFrame(-1, -2.0f, (z ? 5 : 3) | 48, z ? 21 : 76, 0.0f, z ? 76 : 21, 0.0f));
        boolean z2 = LocaleController.isRTL;
        frameLayout.addView(textView, LayoutHelper.createFrame(-1, -2.0f, (z2 ? 5 : 3) | 48, z2 ? 21 : 76, 24.0f, z2 ? 76 : 21, 0.0f));
        frameLayout.addView(linksTextView, LayoutHelper.createFrame(-2, -2.0f, (LocaleController.isRTL ? 5 : 3) | 48, 24.0f, 57.0f, 24.0f, 1.0f));
        if (atomicBoolean != null) {
            atomicBoolean.set(true);
            CheckBoxCell checkBoxCell = new CheckBoxCell(context, 1, baseFragment.getResourceProvider());
            checkBoxCellArr[0] = checkBoxCell;
            checkBoxCell.allowMultiline();
            checkBoxCellArr[0].setBackgroundDrawable(Theme.getSelectorDrawable(false));
            checkBoxCellArr[0].setText(AndroidUtilities.replaceTags(LocaleController.formatString(R.string.OpenUrlOption2, UserObject.getUserName(user))), "", true, false);
            CheckBoxCell checkBoxCell2 = checkBoxCellArr[0];
            if (LocaleController.isRTL) {
                f = 16.0f;
                dp = AndroidUtilities.dp(16.0f);
            } else {
                f = 16.0f;
                dp = AndroidUtilities.dp(8.0f);
            }
            checkBoxCell2.setPadding(dp, 0, LocaleController.isRTL ? AndroidUtilities.dp(8.0f) : AndroidUtilities.dp(f), 0);
            checkBoxCellArr[0].setChecked(true, false);
            frameLayout.addView(checkBoxCellArr[0], LayoutHelper.createFrame(-1, 48.0f, 83, 0.0f, 0.0f, 0.0f, 0.0f));
            checkBoxCellArr[0].setOnClickListener(new View.OnClickListener() {
                @Override
                public final void onClick(View view) {
                    AlertsCreator.lambda$createBotLaunchAlert$31(atomicBoolean, view);
                }
            });
        }
        if (UserObject.isReplyUser(user)) {
            avatarDrawable.setScaleSize(0.8f);
            avatarDrawable.setAvatarType(12);
            backupImageView.setImage((ImageLocation) null, (String) null, avatarDrawable, user);
        } else {
            avatarDrawable.setScaleSize(1.0f);
            avatarDrawable.setInfo(baseFragment.getCurrentAccount(), user);
            backupImageView.setForUserOrChat(user, avatarDrawable);
        }
        builder.setPositiveButton(LocaleController.getString(R.string.Start), new AlertDialog.OnButtonClickListener() {
            @Override
            public final void onClick(AlertDialog alertDialog, int i) {
                runnable.run();
            }
        });
        builder.setNegativeButton(LocaleController.getString(R.string.Cancel), null);
        final AlertDialog create = builder.create();
        baseFragment.showDialog(create);
        linksTextView.setText(AndroidUtilities.replaceSingleTag(LocaleController.getString(R.string.BotWebViewStartPermission2), new Runnable() {
            @Override
            public final void run() {
                AlertsCreator.lambda$createBotLaunchAlert$33(AlertDialog.this, context);
            }
        }));
    }

    public static void createBotLaunchAlert(final BaseFragment baseFragment, final TLRPC.User user, final Runnable runnable, final Runnable runnable2) {
        final Context context = baseFragment.getContext();
        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
        LinkSpanDrawable.LinksTextView linksTextView = new LinkSpanDrawable.LinksTextView(context) {
            @Override
            public void setText(CharSequence charSequence, TextView.BufferType bufferType) {
                super.setText(Emoji.replaceEmoji(charSequence, getPaint().getFontMetricsInt(), false), bufferType);
            }
        };
        NotificationCenter.listenEmojiLoading(linksTextView);
        linksTextView.setTextColor(Theme.getColor(Theme.key_dialogTextBlack));
        linksTextView.setLinkTextColor(Theme.getColor(Theme.key_chat_messageLinkIn));
        linksTextView.setTextSize(1, 16.0f);
        linksTextView.setGravity((LocaleController.isRTL ? 5 : 3) | 48);
        FrameLayout frameLayout = new FrameLayout(context);
        builder.setCustomViewOffset(6);
        builder.setView(frameLayout);
        AvatarDrawable avatarDrawable = new AvatarDrawable();
        avatarDrawable.setTextSize(AndroidUtilities.dp(18.0f));
        BackupImageView backupImageView = new BackupImageView(context);
        backupImageView.setRoundRadius(AndroidUtilities.dp(20.0f));
        frameLayout.addView(backupImageView, LayoutHelper.createFrame(40, 40.0f, (LocaleController.isRTL ? 5 : 3) | 48, 22.0f, 5.0f, 22.0f, 0.0f));
        SimpleTextView simpleTextView = new SimpleTextView(context);
        simpleTextView.setTextColor(Theme.getColor(Theme.key_actionBarDefaultSubmenuItem));
        simpleTextView.setTextSize(20);
        simpleTextView.setTypeface(AndroidUtilities.bold());
        simpleTextView.setGravity((LocaleController.isRTL ? 5 : 3) | 16);
        simpleTextView.setEllipsizeByGradient(true);
        simpleTextView.setText(user.first_name);
        if (user.verified) {
            Drawable mutate = context.getResources().getDrawable(R.drawable.verified_area).mutate();
            int color = Theme.getColor(Theme.key_chats_verifiedBackground);
            PorterDuff.Mode mode = PorterDuff.Mode.MULTIPLY;
            mutate.setColorFilter(new PorterDuffColorFilter(color, mode));
            Drawable mutate2 = context.getResources().getDrawable(R.drawable.verified_check).mutate();
            mutate2.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_chats_verifiedCheck), mode));
            simpleTextView.setRightDrawable(new CombinedDrawable(mutate, mutate2));
        }
        TextView textView = new TextView(context);
        textView.setTextColor(Theme.getColor(Theme.key_dialogTextBlue));
        textView.setTextSize(1, 14.0f);
        textView.setLines(1);
        textView.setMaxLines(1);
        textView.setSingleLine(true);
        textView.setGravity((LocaleController.isRTL ? 5 : 3) | 16);
        textView.setEllipsize(TextUtils.TruncateAt.END);
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public final void onClick(View view) {
                AlertsCreator.lambda$createBotLaunchAlert$26(TLRPC.User.this, baseFragment, builder, view);
            }
        });
        SpannableString valueOf = SpannableString.valueOf(LocaleController.getString(R.string.MoreAboutThisBot) + "  ");
        ColoredImageSpan coloredImageSpan = new ColoredImageSpan(R.drawable.attach_arrow_right);
        coloredImageSpan.setTopOffset(1);
        coloredImageSpan.setSize(AndroidUtilities.dp(10.0f));
        valueOf.setSpan(coloredImageSpan, valueOf.length() - 1, valueOf.length(), 33);
        textView.setText(valueOf);
        boolean z = LocaleController.isRTL;
        frameLayout.addView(simpleTextView, LayoutHelper.createFrame(-1, -2.0f, (z ? 5 : 3) | 48, z ? 21 : 76, 0.0f, z ? 76 : 21, 0.0f));
        boolean z2 = LocaleController.isRTL;
        frameLayout.addView(textView, LayoutHelper.createFrame(-1, -2.0f, (z2 ? 5 : 3) | 48, z2 ? 21 : 76, 24.0f, z2 ? 76 : 21, 0.0f));
        frameLayout.addView(linksTextView, LayoutHelper.createFrame(-2, -2.0f, (LocaleController.isRTL ? 5 : 3) | 48, 24.0f, 57.0f, 24.0f, 1.0f));
        if (UserObject.isReplyUser(user)) {
            avatarDrawable.setScaleSize(0.8f);
            avatarDrawable.setAvatarType(12);
            backupImageView.setImage((ImageLocation) null, (String) null, avatarDrawable, user);
        } else {
            avatarDrawable.setScaleSize(1.0f);
            avatarDrawable.setInfo(baseFragment.getCurrentAccount(), user);
            backupImageView.setForUserOrChat(user, avatarDrawable);
        }
        builder.setPositiveButton(LocaleController.getString(R.string.Start), new AlertDialog.OnButtonClickListener() {
            @Override
            public final void onClick(AlertDialog alertDialog, int i) {
                AlertsCreator.lambda$createBotLaunchAlert$27(runnable, alertDialog, i);
            }
        });
        builder.setNegativeButton(LocaleController.getString(R.string.Cancel), null);
        final AlertDialog create = builder.create();
        baseFragment.showDialog(create, false, new DialogInterface.OnDismissListener() {
            @Override
            public final void onDismiss(DialogInterface dialogInterface) {
                AlertsCreator.lambda$createBotLaunchAlert$28(runnable2, dialogInterface);
            }
        });
        linksTextView.setText(AndroidUtilities.replaceSingleTag(LocaleController.getString(R.string.BotWebViewStartPermission2), new Runnable() {
            @Override
            public final void run() {
                AlertsCreator.lambda$createBotLaunchAlert$29(AlertDialog.this, context);
            }
        }));
    }

    public static BottomSheet.Builder createCalendarPickerDialog(Context context, final long j, final MessagesStorage.IntCallback intCallback, Theme.ResourcesProvider resourcesProvider) {
        if (context == null) {
            return null;
        }
        final BottomSheet.Builder builder = new BottomSheet.Builder(context, false, resourcesProvider);
        builder.setApplyBottomPadding(false);
        final NumberPicker numberPicker = new NumberPicker(context, resourcesProvider);
        numberPicker.setTextOffset(AndroidUtilities.dp(10.0f));
        numberPicker.setItemCount(5);
        final NumberPicker numberPicker2 = new NumberPicker(context, resourcesProvider);
        numberPicker2.setItemCount(5);
        numberPicker2.setTextOffset(-AndroidUtilities.dp(10.0f));
        final NumberPicker numberPicker3 = new NumberPicker(context, resourcesProvider);
        numberPicker3.setItemCount(5);
        numberPicker3.setTextOffset(-AndroidUtilities.dp(24.0f));
        LinearLayout linearLayout = new LinearLayout(context) {
            boolean ignoreLayout = false;

            @Override
            protected void onMeasure(int i, int i2) {
                this.ignoreLayout = true;
                android.graphics.Point point = AndroidUtilities.displaySize;
                int i3 = point.x > point.y ? 3 : 5;
                numberPicker.setItemCount(i3);
                numberPicker2.setItemCount(i3);
                numberPicker3.setItemCount(i3);
                numberPicker.getLayoutParams().height = AndroidUtilities.dp(42.0f) * i3;
                numberPicker2.getLayoutParams().height = AndroidUtilities.dp(42.0f) * i3;
                numberPicker3.getLayoutParams().height = AndroidUtilities.dp(42.0f) * i3;
                this.ignoreLayout = false;
                super.onMeasure(i, i2);
            }

            @Override
            public void requestLayout() {
                if (this.ignoreLayout) {
                    return;
                }
                super.requestLayout();
            }
        };
        linearLayout.setOrientation(1);
        FrameLayout frameLayout = new FrameLayout(context);
        linearLayout.addView(frameLayout, LayoutHelper.createLinear(-1, -2, 51, 22, 0, 0, 4));
        TextView textView = new TextView(context);
        textView.setText(LocaleController.getString(R.string.ChooseDate));
        textView.setTextColor(Theme.getColor(Theme.key_dialogTextBlack, resourcesProvider));
        textView.setTextSize(1, 20.0f);
        textView.setTypeface(AndroidUtilities.bold());
        frameLayout.addView(textView, LayoutHelper.createFrame(-2, -2.0f, 51, 0.0f, 12.0f, 0.0f, 0.0f));
        textView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public final boolean onTouch(View view, MotionEvent motionEvent) {
                boolean lambda$createCalendarPickerDialog$123;
                lambda$createCalendarPickerDialog$123 = AlertsCreator.lambda$createCalendarPickerDialog$123(view, motionEvent);
                return lambda$createCalendarPickerDialog$123;
            }
        });
        LinearLayout linearLayout2 = new LinearLayout(context);
        linearLayout2.setOrientation(0);
        linearLayout2.setWeightSum(1.0f);
        linearLayout.addView(linearLayout2, LayoutHelper.createLinear(-1, -2, 1.0f, 0, 0, 12, 0, 12));
        System.currentTimeMillis();
        TextView textView2 = new TextView(context) {
            @Override
            public CharSequence getAccessibilityClassName() {
                return Button.class.getName();
            }
        };
        linearLayout2.addView(numberPicker, LayoutHelper.createLinear(0, 270, 0.25f));
        numberPicker.setMinValue(1);
        numberPicker.setMaxValue(31);
        numberPicker.setWrapSelectorWheel(false);
        numberPicker.setFormatter(new NumberPicker.Formatter() {
            @Override
            public final String format(int i) {
                String lambda$createCalendarPickerDialog$124;
                lambda$createCalendarPickerDialog$124 = AlertsCreator.lambda$createCalendarPickerDialog$124(i);
                return lambda$createCalendarPickerDialog$124;
            }
        });
        NumberPicker.OnValueChangeListener onValueChangeListener = new NumberPicker.OnValueChangeListener() {
            @Override
            public final void onValueChange(NumberPicker numberPicker4, int i, int i2) {
                AlertsCreator.checkCalendarDate(j, numberPicker, numberPicker2, numberPicker3);
            }
        };
        numberPicker.setOnValueChangedListener(onValueChangeListener);
        numberPicker2.setMinValue(0);
        numberPicker2.setMaxValue(11);
        numberPicker2.setWrapSelectorWheel(false);
        linearLayout2.addView(numberPicker2, LayoutHelper.createLinear(0, 270, 0.5f));
        numberPicker2.setFormatter(new NumberPicker.Formatter() {
            @Override
            public final String format(int i) {
                String lambda$createCalendarPickerDialog$126;
                lambda$createCalendarPickerDialog$126 = AlertsCreator.lambda$createCalendarPickerDialog$126(i);
                return lambda$createCalendarPickerDialog$126;
            }
        });
        numberPicker2.setOnValueChangedListener(onValueChangeListener);
        final Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(j);
        int i = calendar.get(1);
        calendar.setTimeInMillis(System.currentTimeMillis());
        int i2 = calendar.get(1);
        numberPicker3.setMinValue(i);
        numberPicker3.setMaxValue(i2);
        numberPicker3.setWrapSelectorWheel(false);
        numberPicker3.setFormatter(new NumberPicker.Formatter() {
            @Override
            public final String format(int i3) {
                String lambda$createCalendarPickerDialog$127;
                lambda$createCalendarPickerDialog$127 = AlertsCreator.lambda$createCalendarPickerDialog$127(i3);
                return lambda$createCalendarPickerDialog$127;
            }
        });
        linearLayout2.addView(numberPicker3, LayoutHelper.createLinear(0, 270, 0.25f));
        numberPicker3.setOnValueChangedListener(onValueChangeListener);
        numberPicker.setValue(31);
        numberPicker2.setValue(12);
        numberPicker3.setValue(i2);
        checkCalendarDate(j, numberPicker, numberPicker2, numberPicker3);
        textView2.setPadding(AndroidUtilities.dp(34.0f), 0, AndroidUtilities.dp(34.0f), 0);
        textView2.setGravity(17);
        textView2.setTextColor(Theme.getColor(Theme.key_featuredStickers_buttonText, resourcesProvider));
        textView2.setTextSize(1, 14.0f);
        textView2.setTypeface(AndroidUtilities.bold());
        textView2.setText(LocaleController.getString(R.string.JumpToDate));
        textView2.setBackground(Theme.createSimpleSelectorRoundRectDrawable(AndroidUtilities.dp(8.0f), Theme.getColor(Theme.key_featuredStickers_addButton, resourcesProvider), Theme.getColor(Theme.key_featuredStickers_addButtonPressed, resourcesProvider)));
        linearLayout.addView(textView2, LayoutHelper.createLinear(-1, 48, 83, 16, 15, 16, 16));
        textView2.setOnClickListener(new View.OnClickListener() {
            @Override
            public final void onClick(View view) {
                AlertsCreator.lambda$createCalendarPickerDialog$128(j, numberPicker, numberPicker2, numberPicker3, calendar, intCallback, builder, view);
            }
        });
        builder.setCustomView(linearLayout);
        return builder;
    }

    public static void createCallDialogAlert(final BaseFragment baseFragment, final TLRPC.User user, final boolean z) {
        String string;
        String formatString;
        if (baseFragment == null || baseFragment.getParentActivity() == null || user == null || UserObject.isDeleted(user) || UserConfig.getInstance(baseFragment.getCurrentAccount()).getClientUserId() == user.id) {
            return;
        }
        baseFragment.getCurrentAccount();
        Activity parentActivity = baseFragment.getParentActivity();
        FrameLayout frameLayout = new FrameLayout(parentActivity);
        if (z) {
            string = LocaleController.getString(R.string.VideoCallAlertTitle);
            formatString = LocaleController.formatString("VideoCallAlert", R.string.VideoCallAlert, UserObject.getUserName(user));
        } else {
            string = LocaleController.getString(R.string.CallAlertTitle);
            formatString = LocaleController.formatString("CallAlert", R.string.CallAlert, UserObject.getUserName(user));
        }
        TextView textView = new TextView(parentActivity) {
            @Override
            public void setText(CharSequence charSequence, TextView.BufferType bufferType) {
                super.setText(Emoji.replaceEmoji(charSequence, getPaint().getFontMetricsInt(), false), bufferType);
            }
        };
        NotificationCenter.listenEmojiLoading(textView);
        textView.setTextColor(Theme.getColor(Theme.key_dialogTextBlack));
        textView.setTextSize(1, 16.0f);
        textView.setGravity((LocaleController.isRTL ? 5 : 3) | 48);
        textView.setText(AndroidUtilities.replaceTags(formatString));
        AvatarDrawable avatarDrawable = new AvatarDrawable();
        avatarDrawable.setTextSize(AndroidUtilities.dp(12.0f));
        avatarDrawable.setScaleSize(1.0f);
        avatarDrawable.setInfo(baseFragment.getCurrentAccount(), user);
        BackupImageView backupImageView = new BackupImageView(parentActivity);
        backupImageView.setRoundRadius(AndroidUtilities.dp(20.0f));
        backupImageView.setForUserOrChat(user, avatarDrawable);
        frameLayout.addView(backupImageView, LayoutHelper.createFrame(40, 40.0f, (LocaleController.isRTL ? 5 : 3) | 48, 22.0f, 5.0f, 22.0f, 0.0f));
        TextView textView2 = new TextView(parentActivity);
        textView2.setTextColor(Theme.getColor(Theme.key_actionBarDefaultSubmenuItem));
        textView2.setTextSize(1, 20.0f);
        textView2.setTypeface(AndroidUtilities.bold());
        textView2.setLines(1);
        textView2.setMaxLines(1);
        textView2.setSingleLine(true);
        textView2.setGravity((LocaleController.isRTL ? 5 : 3) | 16);
        textView2.setEllipsize(TextUtils.TruncateAt.END);
        textView2.setText(string);
        boolean z2 = LocaleController.isRTL;
        frameLayout.addView(textView2, LayoutHelper.createFrame(-1, -2.0f, (z2 ? 5 : 3) | 48, z2 ? 21 : 76, 11.0f, z2 ? 76 : 21, 0.0f));
        frameLayout.addView(textView, LayoutHelper.createFrame(-2, -2.0f, (LocaleController.isRTL ? 5 : 3) | 48, 24.0f, 57.0f, 24.0f, 9.0f));
        baseFragment.showDialog(new AlertDialog.Builder(parentActivity).setView(frameLayout).setPositiveButton(LocaleController.getString(R.string.Call), new AlertDialog.OnButtonClickListener() {
            @Override
            public final void onClick(AlertDialog alertDialog, int i) {
                AlertsCreator.lambda$createCallDialogAlert$42(BaseFragment.this, user, z, alertDialog, i);
            }
        }).setNegativeButton(LocaleController.getString(R.string.Cancel), null).create());
    }

    public static void createChangeBioAlert(String str, final long j, final Context context, final int i) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(LocaleController.getString(j > 0 ? R.string.UserBio : R.string.DescriptionPlaceholder));
        builder.setMessage(LocaleController.getString(j > 0 ? R.string.VoipGroupBioEditAlertText : R.string.DescriptionInfo));
        FrameLayout frameLayout = new FrameLayout(context);
        frameLayout.setClipChildren(false);
        if (j < 0) {
            long j2 = -j;
            if (MessagesController.getInstance(i).getChatFull(j2) == null) {
                MessagesController.getInstance(i).loadFullChat(j2, ConnectionsManager.generateClassGuid(), true);
            }
        }
        final NumberTextView numberTextView = new NumberTextView(context);
        final EditText editText = new EditText(context);
        int i2 = Theme.key_voipgroup_actionBarItems;
        editText.setTextColor(Theme.getColor(i2));
        editText.setHint(LocaleController.getString(j > 0 ? R.string.UserBio : R.string.DescriptionPlaceholder));
        editText.setTextSize(1, 16.0f);
        editText.setBackground(Theme.createEditTextDrawable(context, true));
        editText.setMaxLines(4);
        editText.setRawInputType(147457);
        editText.setImeOptions(6);
        final int i3 = j > 0 ? 70 : 255;
        editText.setFilters(new InputFilter[]{new CodepointsLengthInputFilter(i3) {
            @Override
            public CharSequence filter(CharSequence charSequence, int i4, int i5, Spanned spanned, int i6, int i7) {
                CharSequence filter = super.filter(charSequence, i4, i5, spanned, i6, i7);
                if (filter != null && charSequence != null && filter.length() != charSequence.length()) {
                    Vibrator vibrator = (Vibrator) context.getSystemService("vibrator");
                    if (vibrator != null) {
                        vibrator.vibrate(200L);
                    }
                    AndroidUtilities.shakeView(numberTextView);
                }
                return filter;
            }
        }});
        numberTextView.setCenterAlign(true);
        numberTextView.setTextSize(15);
        numberTextView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteGrayText4));
        numberTextView.setImportantForAccessibility(2);
        frameLayout.addView(numberTextView, LayoutHelper.createFrame(20, 20.0f, LocaleController.isRTL ? 3 : 5, 0.0f, 14.0f, 21.0f, 0.0f));
        editText.setPadding(LocaleController.isRTL ? AndroidUtilities.dp(24.0f) : 0, AndroidUtilities.dp(8.0f), LocaleController.isRTL ? 0 : AndroidUtilities.dp(24.0f), AndroidUtilities.dp(8.0f));
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable editable) {
                int codePointCount = i3 - Character.codePointCount(editable, 0, editable.length());
                if (codePointCount >= 30) {
                    AndroidUtilities.updateViewVisibilityAnimated(numberTextView, false);
                    return;
                }
                NumberTextView numberTextView2 = numberTextView;
                numberTextView2.setNumber(codePointCount, numberTextView2.getVisibility() == 0);
                AndroidUtilities.updateViewVisibilityAnimated(numberTextView, true);
            }

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i4, int i5, int i6) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i4, int i5, int i6) {
            }
        });
        AndroidUtilities.updateViewVisibilityAnimated(numberTextView, false, 0.0f, false);
        editText.setText(str);
        editText.setSelection(editText.getText().toString().length());
        builder.setView(frameLayout);
        final AlertDialog.OnButtonClickListener onButtonClickListener = new AlertDialog.OnButtonClickListener() {
            @Override
            public final void onClick(AlertDialog alertDialog, int i4) {
                AlertsCreator.lambda$createChangeBioAlert$44(j, i, editText, alertDialog, i4);
            }
        };
        builder.setPositiveButton(LocaleController.getString(R.string.Save), onButtonClickListener);
        builder.setNegativeButton(LocaleController.getString(R.string.Cancel), null);
        builder.setOnPreDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public final void onDismiss(DialogInterface dialogInterface) {
                AndroidUtilities.hideKeyboard(editText);
            }
        });
        frameLayout.addView(editText, LayoutHelper.createFrame(-1, -2.0f, 0, 23.0f, 12.0f, 23.0f, 21.0f));
        editText.requestFocus();
        AndroidUtilities.showKeyboard(editText);
        final AlertDialog create = builder.create();
        editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public final boolean onEditorAction(TextView textView, int i4, KeyEvent keyEvent) {
                boolean lambda$createChangeBioAlert$46;
                lambda$createChangeBioAlert$46 = AlertsCreator.lambda$createChangeBioAlert$46(j, create, onButtonClickListener, textView, i4, keyEvent);
                return lambda$createChangeBioAlert$46;
            }
        });
        create.setBackgroundColor(Theme.getColor(Theme.key_voipgroup_dialogBackground));
        create.show();
        create.setTextColor(Theme.getColor(i2));
    }

    public static void createChangeNameAlert(final long j, Context context, final int i) {
        String str;
        String str2;
        final EditText editText;
        if (DialogObject.isUserDialog(j)) {
            TLRPC.User user = MessagesController.getInstance(i).getUser(Long.valueOf(j));
            str = user.first_name;
            str2 = user.last_name;
        } else {
            str = MessagesController.getInstance(i).getChat(Long.valueOf(-j)).title;
            str2 = null;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(LocaleController.getString(j > 0 ? R.string.VoipEditName : R.string.VoipEditTitle));
        LinearLayout linearLayout = new LinearLayout(context);
        linearLayout.setOrientation(1);
        final EditText editText2 = new EditText(context);
        int i2 = Theme.key_voipgroup_actionBarItems;
        editText2.setTextColor(Theme.getColor(i2));
        editText2.setTextSize(1, 16.0f);
        editText2.setMaxLines(1);
        editText2.setLines(1);
        editText2.setSingleLine(true);
        editText2.setGravity(LocaleController.isRTL ? 5 : 3);
        editText2.setInputType(49152);
        editText2.setImeOptions(j > 0 ? 5 : 6);
        editText2.setHint(LocaleController.getString(j > 0 ? R.string.FirstName : R.string.VoipEditTitleHint));
        editText2.setBackground(Theme.createEditTextDrawable(context, true));
        editText2.setPadding(0, AndroidUtilities.dp(8.0f), 0, AndroidUtilities.dp(8.0f));
        editText2.requestFocus();
        if (j > 0) {
            editText = new EditText(context);
            editText.setTextColor(Theme.getColor(i2));
            editText.setTextSize(1, 16.0f);
            editText.setMaxLines(1);
            editText.setLines(1);
            editText.setSingleLine(true);
            editText.setGravity(LocaleController.isRTL ? 5 : 3);
            editText.setInputType(49152);
            editText.setImeOptions(6);
            editText.setHint(LocaleController.getString(R.string.LastName));
            editText.setBackground(Theme.createEditTextDrawable(context, true));
            editText.setPadding(0, AndroidUtilities.dp(8.0f), 0, AndroidUtilities.dp(8.0f));
        } else {
            editText = null;
        }
        AndroidUtilities.showKeyboard(editText2);
        linearLayout.addView(editText2, LayoutHelper.createLinear(-1, -2, 0, 23, 12, 23, 21));
        if (editText != null) {
            linearLayout.addView(editText, LayoutHelper.createLinear(-1, -2, 0, 23, 12, 23, 21));
        }
        editText2.setText(str);
        editText2.setSelection(editText2.getText().toString().length());
        if (editText != null) {
            editText.setText(str2);
            editText.setSelection(editText.getText().toString().length());
        }
        builder.setView(linearLayout);
        final EditText editText3 = editText;
        final AlertDialog.OnButtonClickListener onButtonClickListener = new AlertDialog.OnButtonClickListener() {
            @Override
            public final void onClick(AlertDialog alertDialog, int i3) {
                AlertsCreator.lambda$createChangeNameAlert$48(editText2, j, i, editText3, alertDialog, i3);
            }
        };
        builder.setPositiveButton(LocaleController.getString(R.string.Save), onButtonClickListener);
        builder.setNegativeButton(LocaleController.getString(R.string.Cancel), null);
        builder.setOnPreDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public final void onDismiss(DialogInterface dialogInterface) {
                AlertsCreator.lambda$createChangeNameAlert$49(editText2, editText, dialogInterface);
            }
        });
        final AlertDialog create = builder.create();
        create.setBackgroundColor(Theme.getColor(Theme.key_voipgroup_dialogBackground));
        create.show();
        create.setTextColor(Theme.getColor(i2));
        TextView.OnEditorActionListener onEditorActionListener = new TextView.OnEditorActionListener() {
            @Override
            public final boolean onEditorAction(TextView textView, int i3, KeyEvent keyEvent) {
                boolean lambda$createChangeNameAlert$50;
                lambda$createChangeNameAlert$50 = AlertsCreator.lambda$createChangeNameAlert$50(AlertDialog.this, onButtonClickListener, textView, i3, keyEvent);
                return lambda$createChangeNameAlert$50;
            }
        };
        if (editText != null) {
            editText.setOnEditorActionListener(onEditorActionListener);
        } else {
            editText2.setOnEditorActionListener(onEditorActionListener);
        }
    }

    public static void createClearDaysDialogAlert(org.telegram.ui.ActionBar.BaseFragment r26, int r27, org.telegram.tgnet.TLRPC.User r28, org.telegram.tgnet.TLRPC.Chat r29, boolean r30, final org.telegram.messenger.MessagesStorage.BooleanCallback r31, org.telegram.ui.ActionBar.Theme.ResourcesProvider r32) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.AlertsCreator.createClearDaysDialogAlert(org.telegram.ui.ActionBar.BaseFragment, int, org.telegram.tgnet.TLRPC$User, org.telegram.tgnet.TLRPC$Chat, boolean, org.telegram.messenger.MessagesStorage$BooleanCallback, org.telegram.ui.ActionBar.Theme$ResourcesProvider):void");
    }

    public static void createClearOrDeleteDialogAlert(BaseFragment baseFragment, boolean z, TLRPC.Chat chat, TLRPC.User user, boolean z2, boolean z3, MessagesStorage.BooleanCallback booleanCallback) {
        createClearOrDeleteDialogAlert(baseFragment, z, false, false, chat, user, z2, false, z3, booleanCallback, null);
    }

    public static void createClearOrDeleteDialogAlert(BaseFragment baseFragment, boolean z, TLRPC.Chat chat, TLRPC.User user, boolean z2, boolean z3, boolean z4, MessagesStorage.BooleanCallback booleanCallback, Theme.ResourcesProvider resourcesProvider) {
        createClearOrDeleteDialogAlert(baseFragment, z, chat != null && chat.creator, false, chat, user, z2, z3, z4, booleanCallback, resourcesProvider);
    }

    public static void createClearOrDeleteDialogAlert(final org.telegram.ui.ActionBar.BaseFragment r30, final boolean r31, final boolean r32, final boolean r33, final org.telegram.tgnet.TLRPC.Chat r34, final org.telegram.tgnet.TLRPC.User r35, final boolean r36, final boolean r37, final boolean r38, final org.telegram.messenger.MessagesStorage.BooleanCallback r39, final org.telegram.ui.ActionBar.Theme.ResourcesProvider r40) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.AlertsCreator.createClearOrDeleteDialogAlert(org.telegram.ui.ActionBar.BaseFragment, boolean, boolean, boolean, org.telegram.tgnet.TLRPC$Chat, org.telegram.tgnet.TLRPC$User, boolean, boolean, boolean, org.telegram.messenger.MessagesStorage$BooleanCallback, org.telegram.ui.ActionBar.Theme$ResourcesProvider):void");
    }

    public static void createClearOrDeleteDialogsAlert(BaseFragment baseFragment, boolean z, boolean z2, int i, int i2, boolean z3, final MessagesStorage.BooleanCallback booleanCallback, Theme.ResourcesProvider resourcesProvider) {
        int i3;
        String str;
        int i4;
        String str2;
        int currentAccount = baseFragment.getCurrentAccount();
        Activity parentActivity = baseFragment.getParentActivity();
        AlertDialog.Builder builder = new AlertDialog.Builder(parentActivity, resourcesProvider);
        UserConfig.getInstance(currentAccount).getClientUserId();
        final CheckBoxCell[] checkBoxCellArr = new CheckBoxCell[1];
        final boolean[] zArr = new boolean[1];
        TextView textView = new TextView(parentActivity);
        textView.setTextColor(Theme.getColor(Theme.key_dialogTextBlack));
        textView.setTextSize(1, 16.0f);
        textView.setGravity((LocaleController.isRTL ? 5 : 3) | 48);
        FrameLayout frameLayout = new FrameLayout(parentActivity) {
            @Override
            protected void onMeasure(int i5, int i6) {
                super.onMeasure(i5, i6);
                if (checkBoxCellArr[0] != null) {
                    setMeasuredDimension(getMeasuredWidth(), getMeasuredHeight() + checkBoxCellArr[0].getMeasuredHeight() + AndroidUtilities.dp(7.0f));
                }
            }
        };
        builder.setCustomViewOffset(6);
        builder.setView(frameLayout);
        TextView textView2 = new TextView(parentActivity);
        textView2.setTextColor(Theme.getColor(Theme.key_actionBarDefaultSubmenuItem));
        textView2.setTextSize(1, 20.0f);
        textView2.setTypeface(AndroidUtilities.bold());
        textView2.setLines(1);
        textView2.setMaxLines(1);
        textView2.setSingleLine(true);
        textView2.setGravity((LocaleController.isRTL ? 5 : 3) | 16);
        textView2.setEllipsize(TextUtils.TruncateAt.END);
        if (z2) {
            if (z3) {
                CheckBoxCell checkBoxCell = new CheckBoxCell(parentActivity, 1, resourcesProvider);
                checkBoxCellArr[0] = checkBoxCell;
                checkBoxCell.setBackgroundDrawable(Theme.getSelectorDrawable(false));
                checkBoxCellArr[0].setText(LocaleController.getString(R.string.DeleteMessagesForBothSidesWherePossible), "", false, false);
                checkBoxCellArr[0].setPadding(LocaleController.isRTL ? AndroidUtilities.dp(16.0f) : AndroidUtilities.dp(8.0f), 0, LocaleController.isRTL ? AndroidUtilities.dp(8.0f) : AndroidUtilities.dp(16.0f), 0);
                frameLayout.addView(checkBoxCellArr[0], LayoutHelper.createFrame(-1, 48.0f, 83, 0.0f, 0.0f, 0.0f, 0.0f));
                checkBoxCellArr[0].setOnClickListener(new View.OnClickListener() {
                    @Override
                    public final void onClick(View view) {
                        AlertsCreator.lambda$createClearOrDeleteDialogsAlert$38(zArr, view);
                    }
                });
            }
            textView2.setText(LocaleController.formatString("DeleteFewChatsTitle", R.string.DeleteFewChatsTitle, LocaleController.formatPluralString("ChatsSelected", i2, new Object[0])));
            i3 = R.string.AreYouSureDeleteFewChats;
            str = "AreYouSureDeleteFewChats";
        } else if (i != 0) {
            textView2.setText(LocaleController.formatString("ClearCacheFewChatsTitle", R.string.ClearCacheFewChatsTitle, LocaleController.formatPluralString("ChatsSelectedClearCache", i2, new Object[0])));
            i3 = R.string.AreYouSureClearHistoryCacheFewChats;
            str = "AreYouSureClearHistoryCacheFewChats";
        } else {
            textView2.setText(LocaleController.formatString("ClearFewChatsTitle", R.string.ClearFewChatsTitle, LocaleController.formatPluralString("ChatsSelectedClear", i2, new Object[0])));
            i3 = R.string.AreYouSureClearHistoryFewChats;
            str = "AreYouSureClearHistoryFewChats";
        }
        textView.setText(LocaleController.getString(str, i3));
        frameLayout.addView(textView2, LayoutHelper.createFrame(-1, -2.0f, (LocaleController.isRTL ? 5 : 3) | 48, 24.0f, 11.0f, 24.0f, 0.0f));
        frameLayout.addView(textView, LayoutHelper.createFrame(-2, -2.0f, (LocaleController.isRTL ? 5 : 3) | 48, 24.0f, 57.0f, 24.0f, 1.0f));
        if (z2) {
            i4 = R.string.Delete;
            str2 = "Delete";
        } else if (i != 0) {
            i4 = R.string.ClearHistoryCache;
            str2 = "ClearHistoryCache";
        } else {
            i4 = R.string.ClearHistory;
            str2 = "ClearHistory";
        }
        builder.setPositiveButton(LocaleController.getString(str2, i4), new AlertDialog.OnButtonClickListener() {
            @Override
            public final void onClick(AlertDialog alertDialog, int i5) {
                AlertsCreator.lambda$createClearOrDeleteDialogsAlert$39(MessagesStorage.BooleanCallback.this, zArr, alertDialog, i5);
            }
        });
        builder.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), null);
        AlertDialog create = builder.create();
        baseFragment.showDialog(create);
        TextView textView3 = (TextView) create.getButton(-1);
        if (textView3 != null) {
            textView3.setTextColor(Theme.getColor(Theme.key_text_RedBold));
        }
    }

    public static Dialog createColorSelectDialog(Activity activity, long j, int i, int i2, Runnable runnable) {
        return createColorSelectDialog(activity, j, i, i2, runnable, null);
    }

    public static android.app.Dialog createColorSelectDialog(android.app.Activity r25, final long r26, final long r28, final int r30, final java.lang.Runnable r31, org.telegram.ui.ActionBar.Theme.ResourcesProvider r32) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.AlertsCreator.createColorSelectDialog(android.app.Activity, long, long, int, java.lang.Runnable, org.telegram.ui.ActionBar.Theme$ResourcesProvider):android.app.Dialog");
    }

    public static void createContactInviteDialog(final BaseFragment baseFragment, String str, String str2, final String str3) {
        AlertDialog.Builder builder = new AlertDialog.Builder(baseFragment.getParentActivity());
        builder.setTitle(LocaleController.getString(R.string.ContactNotRegisteredTitle));
        builder.setMessage(LocaleController.formatString("ContactNotRegistered", R.string.ContactNotRegistered, ContactsController.formatName(str, str2)));
        builder.setNegativeButton(LocaleController.getString(R.string.Cancel), null);
        builder.setPositiveButton(LocaleController.getString(R.string.Invite), new AlertDialog.OnButtonClickListener() {
            @Override
            public final void onClick(AlertDialog alertDialog, int i) {
                AlertsCreator.lambda$createContactInviteDialog$54(str3, baseFragment, alertDialog, i);
            }
        });
        baseFragment.showDialog(builder.create());
    }

    public static AlertDialog.Builder createContactsPermissionDialog(Activity activity, final MessagesStorage.IntCallback intCallback) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTopAnimation(R.raw.permission_request_contacts, 72, false, Theme.getColor(Theme.key_dialogTopBackground));
        builder.setMessage(AndroidUtilities.replaceTags(LocaleController.getString(R.string.ContactsPermissionAlert)));
        builder.setPositiveButton(LocaleController.getString(R.string.ContactsPermissionAlertContinue), new AlertDialog.OnButtonClickListener() {
            @Override
            public final void onClick(AlertDialog alertDialog, int i) {
                MessagesStorage.IntCallback.this.run(1);
            }
        });
        builder.setNegativeButton(LocaleController.getString(R.string.ContactsPermissionAlertNotNow), new AlertDialog.OnButtonClickListener() {
            @Override
            public final void onClick(AlertDialog alertDialog, int i) {
                MessagesStorage.IntCallback.this.run(0);
            }
        });
        return builder;
    }

    public static AlertDialog.Builder createDatePickerDialog(Context context, int i, int i2, int i3, int i4, int i5, int i6, String str, final boolean z, final DatePickerDelegate datePickerDelegate) {
        if (context == null) {
            return null;
        }
        LinearLayout linearLayout = new LinearLayout(context);
        linearLayout.setOrientation(0);
        linearLayout.setWeightSum(1.0f);
        final NumberPicker numberPicker = new NumberPicker(context);
        final NumberPicker numberPicker2 = new NumberPicker(context);
        final NumberPicker numberPicker3 = new NumberPicker(context);
        linearLayout.addView(numberPicker2, LayoutHelper.createLinear(0, -2, 0.3f));
        numberPicker2.setOnScrollListener(new NumberPicker.OnScrollListener() {
            @Override
            public final void onScrollStateChange(NumberPicker numberPicker4, int i7) {
                AlertsCreator.lambda$createDatePickerDialog$71(z, numberPicker2, numberPicker, numberPicker3, numberPicker4, i7);
            }
        });
        numberPicker.setMinValue(0);
        numberPicker.setMaxValue(11);
        linearLayout.addView(numberPicker, LayoutHelper.createLinear(0, -2, 0.3f));
        numberPicker.setFormatter(new NumberPicker.Formatter() {
            @Override
            public final String format(int i7) {
                String lambda$createDatePickerDialog$72;
                lambda$createDatePickerDialog$72 = AlertsCreator.lambda$createDatePickerDialog$72(i7);
                return lambda$createDatePickerDialog$72;
            }
        });
        numberPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public final void onValueChange(NumberPicker numberPicker4, int i7, int i8) {
                AlertsCreator.updateDayPicker(NumberPicker.this, numberPicker, numberPicker3);
            }
        });
        numberPicker.setOnScrollListener(new NumberPicker.OnScrollListener() {
            @Override
            public final void onScrollStateChange(NumberPicker numberPicker4, int i7) {
                AlertsCreator.lambda$createDatePickerDialog$74(z, numberPicker2, numberPicker, numberPicker3, numberPicker4, i7);
            }
        });
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        int i7 = calendar.get(1);
        numberPicker3.setMinValue(i7 + i);
        numberPicker3.setMaxValue(i7 + i2);
        numberPicker3.setValue(i7 + i3);
        linearLayout.addView(numberPicker3, LayoutHelper.createLinear(0, -2, 0.4f));
        numberPicker3.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public final void onValueChange(NumberPicker numberPicker4, int i8, int i9) {
                AlertsCreator.updateDayPicker(NumberPicker.this, numberPicker, numberPicker3);
            }
        });
        numberPicker3.setOnScrollListener(new NumberPicker.OnScrollListener() {
            @Override
            public final void onScrollStateChange(NumberPicker numberPicker4, int i8) {
                AlertsCreator.lambda$createDatePickerDialog$76(z, numberPicker2, numberPicker, numberPicker3, numberPicker4, i8);
            }
        });
        updateDayPicker(numberPicker2, numberPicker, numberPicker3);
        if (z) {
            checkPickerDate(numberPicker2, numberPicker, numberPicker3);
        }
        if (i4 != -1) {
            numberPicker2.setValue(i4);
            numberPicker.setValue(i5);
            numberPicker3.setValue(i6);
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(str);
        builder.setView(linearLayout);
        builder.setPositiveButton(LocaleController.getString(R.string.Set), new AlertDialog.OnButtonClickListener() {
            @Override
            public final void onClick(AlertDialog alertDialog, int i8) {
                AlertsCreator.lambda$createDatePickerDialog$77(z, numberPicker2, numberPicker, numberPicker3, datePickerDelegate, alertDialog, i8);
            }
        });
        builder.setNegativeButton(LocaleController.getString(R.string.Cancel), null);
        return builder;
    }

    public static BottomSheet.Builder createDatePickerDialog(Context context, String str, String str2, long j, final ScheduleDatePickerDelegate scheduleDatePickerDelegate) {
        LinearLayout linearLayout;
        if (context == null) {
            return null;
        }
        ScheduleDatePickerColors scheduleDatePickerColors = new ScheduleDatePickerColors();
        final BottomSheet.Builder builder = new BottomSheet.Builder(context, false);
        builder.setApplyBottomPadding(false);
        final NumberPicker numberPicker = new NumberPicker(context);
        numberPicker.setTextColor(scheduleDatePickerColors.textColor);
        numberPicker.setTextOffset(AndroidUtilities.dp(10.0f));
        numberPicker.setItemCount(5);
        final NumberPicker numberPicker2 = new NumberPicker(context) {
            @Override
            protected CharSequence getContentDescription(int i) {
                return LocaleController.formatPluralString("Hours", i, new Object[0]);
            }
        };
        numberPicker2.setItemCount(5);
        numberPicker2.setTextColor(scheduleDatePickerColors.textColor);
        numberPicker2.setTextOffset(-AndroidUtilities.dp(10.0f));
        final NumberPicker numberPicker3 = new NumberPicker(context) {
            @Override
            protected CharSequence getContentDescription(int i) {
                return LocaleController.formatPluralString("Minutes", i, new Object[0]);
            }
        };
        numberPicker3.setItemCount(5);
        numberPicker3.setTextColor(scheduleDatePickerColors.textColor);
        numberPicker3.setTextOffset(-AndroidUtilities.dp(34.0f));
        LinearLayout linearLayout2 = new LinearLayout(context) {
            boolean ignoreLayout = false;

            @Override
            protected void onMeasure(int i, int i2) {
                this.ignoreLayout = true;
                android.graphics.Point point = AndroidUtilities.displaySize;
                int i3 = point.x > point.y ? 3 : 5;
                numberPicker.setItemCount(i3);
                numberPicker2.setItemCount(i3);
                numberPicker3.setItemCount(i3);
                numberPicker.getLayoutParams().height = AndroidUtilities.dp(42.0f) * i3;
                numberPicker2.getLayoutParams().height = AndroidUtilities.dp(42.0f) * i3;
                numberPicker3.getLayoutParams().height = AndroidUtilities.dp(42.0f) * i3;
                this.ignoreLayout = false;
                super.onMeasure(i, i2);
            }

            @Override
            public void requestLayout() {
                if (this.ignoreLayout) {
                    return;
                }
                super.requestLayout();
            }
        };
        linearLayout2.setOrientation(1);
        FrameLayout frameLayout = new FrameLayout(context);
        linearLayout2.addView(frameLayout, LayoutHelper.createLinear(-1, -2, 51, 22, 0, 0, 4));
        TextView textView = new TextView(context);
        textView.setText(str);
        textView.setTextColor(scheduleDatePickerColors.textColor);
        textView.setTextSize(1, 20.0f);
        textView.setTypeface(AndroidUtilities.bold());
        frameLayout.addView(textView, LayoutHelper.createFrame(-2, -2.0f, 51, 0.0f, 12.0f, 0.0f, 0.0f));
        textView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public final boolean onTouch(View view, MotionEvent motionEvent) {
                boolean lambda$createDatePickerDialog$87;
                lambda$createDatePickerDialog$87 = AlertsCreator.lambda$createDatePickerDialog$87(view, motionEvent);
                return lambda$createDatePickerDialog$87;
            }
        });
        LinearLayout linearLayout3 = new LinearLayout(context);
        linearLayout3.setOrientation(0);
        linearLayout3.setWeightSum(1.0f);
        linearLayout2.addView(linearLayout3, LayoutHelper.createLinear(-1, -2, 1.0f, 0, 0, 12, 0, 12));
        final long currentTimeMillis = System.currentTimeMillis();
        final Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(currentTimeMillis);
        final int i = calendar.get(1);
        TextView textView2 = new TextView(context) {
            @Override
            public CharSequence getAccessibilityClassName() {
                return Button.class.getName();
            }
        };
        linearLayout3.addView(numberPicker, LayoutHelper.createLinear(0, 270, 0.5f));
        numberPicker.setMinValue(0);
        numberPicker.setMaxValue(365);
        numberPicker.setWrapSelectorWheel(false);
        numberPicker.setFormatter(new NumberPicker.Formatter() {
            @Override
            public final String format(int i2) {
                String lambda$createDatePickerDialog$88;
                lambda$createDatePickerDialog$88 = AlertsCreator.lambda$createDatePickerDialog$88(currentTimeMillis, calendar, i, i2);
                return lambda$createDatePickerDialog$88;
            }
        });
        NumberPicker.OnValueChangeListener onValueChangeListener = new NumberPicker.OnValueChangeListener() {
            @Override
            public final void onValueChange(NumberPicker numberPicker4, int i2, int i3) {
                AlertsCreator.checkScheduleDate(null, null, 0, NumberPicker.this, numberPicker2, numberPicker3);
            }
        };
        numberPicker.setOnValueChangedListener(onValueChangeListener);
        numberPicker2.setMinValue(0);
        numberPicker2.setMaxValue(23);
        linearLayout3.addView(numberPicker2, LayoutHelper.createLinear(0, 270, 0.2f));
        numberPicker2.setFormatter(new NumberPicker.Formatter() {
            @Override
            public final String format(int i2) {
                String lambda$createDatePickerDialog$90;
                lambda$createDatePickerDialog$90 = AlertsCreator.lambda$createDatePickerDialog$90(i2);
                return lambda$createDatePickerDialog$90;
            }
        });
        numberPicker2.setOnValueChangedListener(onValueChangeListener);
        numberPicker3.setMinValue(0);
        numberPicker3.setMaxValue(59);
        numberPicker3.setValue(0);
        numberPicker3.setFormatter(new NumberPicker.Formatter() {
            @Override
            public final String format(int i2) {
                String lambda$createDatePickerDialog$91;
                lambda$createDatePickerDialog$91 = AlertsCreator.lambda$createDatePickerDialog$91(i2);
                return lambda$createDatePickerDialog$91;
            }
        });
        linearLayout3.addView(numberPicker3, LayoutHelper.createLinear(0, 270, 0.3f));
        numberPicker3.setOnValueChangedListener(onValueChangeListener);
        if (j <= 0 || j == 2147483646) {
            linearLayout = linearLayout2;
        } else {
            long j2 = 1000 * j;
            calendar.setTimeInMillis(System.currentTimeMillis());
            calendar.set(12, 0);
            calendar.set(13, 0);
            calendar.set(14, 0);
            calendar.set(11, 0);
            linearLayout = linearLayout2;
            int timeInMillis = (int) ((j2 - calendar.getTimeInMillis()) / 86400000);
            calendar.setTimeInMillis(j2);
            if (timeInMillis >= 0) {
                numberPicker3.setValue(calendar.get(12));
                numberPicker2.setValue(calendar.get(11));
                numberPicker.setValue(timeInMillis);
            }
        }
        checkScheduleDate(null, null, 0, numberPicker, numberPicker2, numberPicker3);
        textView2.setPadding(AndroidUtilities.dp(34.0f), 0, AndroidUtilities.dp(34.0f), 0);
        textView2.setGravity(17);
        textView2.setTextColor(scheduleDatePickerColors.buttonTextColor);
        textView2.setTextSize(1, 14.0f);
        textView2.setTypeface(AndroidUtilities.bold());
        textView2.setBackgroundDrawable(Theme.createSimpleSelectorRoundRectDrawable(AndroidUtilities.dp(8.0f), scheduleDatePickerColors.buttonBackgroundColor, scheduleDatePickerColors.buttonBackgroundPressedColor));
        textView2.setText(str2);
        LinearLayout linearLayout4 = linearLayout;
        linearLayout4.addView(textView2, LayoutHelper.createLinear(-1, 48, 83, 16, 15, 16, 16));
        textView2.setOnClickListener(new View.OnClickListener() {
            @Override
            public final void onClick(View view) {
                AlertsCreator.lambda$createDatePickerDialog$92(NumberPicker.this, numberPicker2, numberPicker3, calendar, scheduleDatePickerDelegate, builder, view);
            }
        });
        builder.setCustomView(linearLayout4);
        BottomSheet show = builder.show();
        show.setBackgroundColor(scheduleDatePickerColors.backgroundColor);
        show.fixNavigationBar(scheduleDatePickerColors.backgroundColor);
        return builder;
    }

    public static void createDeleteMessagesAlert(final org.telegram.ui.ActionBar.BaseFragment r46, final org.telegram.tgnet.TLRPC.User r47, final org.telegram.tgnet.TLRPC.Chat r48, final org.telegram.tgnet.TLRPC.EncryptedChat r49, final org.telegram.tgnet.TLRPC.ChatFull r50, final long r51, final org.telegram.messenger.MessageObject r53, final android.util.SparseArray[] r54, final org.telegram.messenger.MessageObject.GroupedMessages r55, final int r56, final int r57, org.telegram.tgnet.TLRPC.ChannelParticipant[] r58, final java.lang.Runnable r59, final java.lang.Runnable r60, final org.telegram.ui.ActionBar.Theme.ResourcesProvider r61) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.AlertsCreator.createDeleteMessagesAlert(org.telegram.ui.ActionBar.BaseFragment, org.telegram.tgnet.TLRPC$User, org.telegram.tgnet.TLRPC$Chat, org.telegram.tgnet.TLRPC$EncryptedChat, org.telegram.tgnet.TLRPC$ChatFull, long, org.telegram.messenger.MessageObject, android.util.SparseArray[], org.telegram.messenger.MessageObject$GroupedMessages, int, int, org.telegram.tgnet.TLRPC$ChannelParticipant[], java.lang.Runnable, java.lang.Runnable, org.telegram.ui.ActionBar.Theme$ResourcesProvider):void");
    }

    public static AlertDialog.Builder createDrawOverlayGroupCallPermissionDialog(final Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        String readRes = AndroidUtilities.readRes(R.raw.pip_voice_request);
        final GroupCallPipButton groupCallPipButton = new GroupCallPipButton(context, 0, true);
        groupCallPipButton.setImportantForAccessibility(2);
        FrameLayout frameLayout = new FrameLayout(context) {
            @Override
            protected void onLayout(boolean z, int i, int i2, int i3, int i4) {
                super.onLayout(z, i, i2, i3, i4);
                groupCallPipButton.setTranslationY((getMeasuredHeight() * 0.28f) - (groupCallPipButton.getMeasuredWidth() / 2.0f));
                groupCallPipButton.setTranslationX((getMeasuredWidth() * 0.82f) - (groupCallPipButton.getMeasuredWidth() / 2.0f));
            }
        };
        frameLayout.setBackground(new GradientDrawable(GradientDrawable.Orientation.BL_TR, new int[]{-15128003, -15118002}));
        frameLayout.setClipToOutline(true);
        frameLayout.setOutlineProvider(new ViewOutlineProvider() {
            @Override
            public void getOutline(View view, Outline outline) {
                outline.setRoundRect(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight() + AndroidUtilities.dp(6.0f), AndroidUtilities.dpf2(6.0f));
            }
        });
        View view = new View(context);
        view.setBackground(new BitmapDrawable(SvgHelper.getBitmap(readRes, AndroidUtilities.dp(320.0f), AndroidUtilities.dp(184.61539f), false)));
        frameLayout.addView(view, LayoutHelper.createFrame(-1, -1.0f, 0, -1.0f, -1.0f, -1.0f, -1.0f));
        frameLayout.addView(groupCallPipButton, LayoutHelper.createFrame(117, 117.0f));
        builder.setTopView(frameLayout);
        builder.setTitle(LocaleController.getString(R.string.PermissionDrawAboveOtherAppsGroupCallTitle));
        builder.setMessage(LocaleController.getString(R.string.PermissionDrawAboveOtherAppsGroupCall));
        builder.setPositiveButton(LocaleController.getString(R.string.Enable), new AlertDialog.OnButtonClickListener() {
            @Override
            public final void onClick(AlertDialog alertDialog, int i) {
                AlertsCreator.lambda$createDrawOverlayGroupCallPermissionDialog$144(context, alertDialog, i);
            }
        });
        builder.notDrawBackgroundOnTopView(true);
        builder.setNegativeButton(LocaleController.getString(R.string.Cancel), null);
        builder.setTopViewAspectRatio(0.5769231f);
        return builder;
    }

    public static AlertDialog.Builder createDrawOverlayPermissionDialog(final Activity activity, AlertDialog.OnButtonClickListener onButtonClickListener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        String readRes = AndroidUtilities.readRes(R.raw.pip_video_request);
        FrameLayout frameLayout = new FrameLayout(activity);
        frameLayout.setBackground(new GradientDrawable(GradientDrawable.Orientation.BL_TR, new int[]{-14535089, -14527894}));
        frameLayout.setClipToOutline(true);
        frameLayout.setOutlineProvider(new ViewOutlineProvider() {
            @Override
            public void getOutline(View view, Outline outline) {
                outline.setRoundRect(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight() + AndroidUtilities.dp(6.0f), AndroidUtilities.dpf2(6.0f));
            }
        });
        View view = new View(activity);
        view.setBackground(new BitmapDrawable(SvgHelper.getBitmap(readRes, AndroidUtilities.dp(320.0f), AndroidUtilities.dp(161.36752f), false)));
        frameLayout.addView(view, LayoutHelper.createFrame(-1, -1.0f, 0, -1.0f, -1.0f, -1.0f, -1.0f));
        builder.setTopView(frameLayout);
        builder.setTitle(LocaleController.getString(R.string.PermissionDrawAboveOtherAppsTitle));
        builder.setMessage(LocaleController.getString(R.string.PermissionDrawAboveOtherApps));
        builder.setPositiveButton(LocaleController.getString(R.string.Enable), new AlertDialog.OnButtonClickListener() {
            @Override
            public final void onClick(AlertDialog alertDialog, int i) {
                AlertsCreator.lambda$createDrawOverlayPermissionDialog$143(activity, alertDialog, i);
            }
        });
        builder.notDrawBackgroundOnTopView(true);
        builder.setNegativeButton(LocaleController.getString(R.string.Cancel), onButtonClickListener);
        builder.setTopViewAspectRatio(0.50427353f);
        return builder;
    }

    public static Dialog createForgotPasscodeDialog(Context context) {
        return new AlertDialog.Builder(context).setTitle(LocaleController.getString(R.string.ForgotPasscode)).setMessage(LocaleController.getString(R.string.ForgotPasscodeInfo)).setPositiveButton(LocaleController.getString(R.string.Close), null).create();
    }

    public static Dialog createFreeSpaceDialog(final LaunchActivity launchActivity) {
        AlertDialog.Builder builder = new AlertDialog.Builder(launchActivity);
        builder.setTitle(LocaleController.getString(R.string.LowDiskSpaceTitle));
        builder.setMessage(LocaleController.getString(R.string.LowDiskSpaceMessage2));
        builder.setNegativeButton(LocaleController.getString(R.string.Cancel), null);
        builder.setPositiveButton(LocaleController.getString(R.string.LowDiskSpaceButton), new AlertDialog.OnButtonClickListener() {
            @Override
            public final void onClick(AlertDialog alertDialog, int i) {
                AlertsCreator.lambda$createFreeSpaceDialog$147(LaunchActivity.this, alertDialog, i);
            }
        });
        return builder.create();
    }

    public static AlertDialog.Builder createGigagroupConvertAlert(Activity activity, AlertDialog.OnButtonClickListener onButtonClickListener, AlertDialog.OnButtonClickListener onButtonClickListener2) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        String readRes = AndroidUtilities.readRes(R.raw.gigagroup);
        FrameLayout frameLayout = new FrameLayout(activity);
        if (Build.VERSION.SDK_INT >= 21) {
            frameLayout.setClipToOutline(true);
            frameLayout.setOutlineProvider(new ViewOutlineProvider() {
                @Override
                public void getOutline(View view, Outline outline) {
                    outline.setRoundRect(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight() + AndroidUtilities.dp(6.0f), AndroidUtilities.dp(6.0f));
                }
            });
        }
        View view = new View(activity);
        view.setBackground(new BitmapDrawable(SvgHelper.getBitmap(readRes, AndroidUtilities.dp(320.0f), AndroidUtilities.dp(127.17949f), false)));
        frameLayout.addView(view, LayoutHelper.createFrame(-1, -1.0f, 0, -1.0f, -1.0f, -1.0f, -1.0f));
        builder.setTopView(frameLayout);
        builder.setTopViewAspectRatio(0.3974359f);
        builder.setTitle(LocaleController.getString(R.string.GigagroupAlertTitle));
        builder.setMessage(AndroidUtilities.replaceTags(LocaleController.getString(R.string.GigagroupAlertText)));
        builder.setPositiveButton(LocaleController.getString(R.string.GigagroupAlertLearnMore), onButtonClickListener);
        builder.setNegativeButton(LocaleController.getString(R.string.Cancel), onButtonClickListener2);
        return builder;
    }

    public static void createImportDialogAlert(BaseFragment baseFragment, String str, String str2, TLRPC.User user, TLRPC.Chat chat, final Runnable runnable) {
        if (baseFragment == null || baseFragment.getParentActivity() == null) {
            return;
        }
        if (chat == null && user == null) {
            return;
        }
        int currentAccount = baseFragment.getCurrentAccount();
        Activity parentActivity = baseFragment.getParentActivity();
        AlertDialog.Builder builder = new AlertDialog.Builder(parentActivity);
        long clientUserId = UserConfig.getInstance(currentAccount).getClientUserId();
        TextView textView = new TextView(parentActivity);
        textView.setTextColor(Theme.getColor(Theme.key_dialogTextBlack));
        textView.setTextSize(1, 16.0f);
        textView.setGravity((LocaleController.isRTL ? 5 : 3) | 48);
        FrameLayout frameLayout = new FrameLayout(parentActivity);
        builder.setView(frameLayout);
        AvatarDrawable avatarDrawable = new AvatarDrawable();
        avatarDrawable.setTextSize(AndroidUtilities.dp(12.0f));
        BackupImageView backupImageView = new BackupImageView(parentActivity);
        backupImageView.setRoundRadius(AndroidUtilities.dp(20.0f));
        frameLayout.addView(backupImageView, LayoutHelper.createFrame(40, 40.0f, (LocaleController.isRTL ? 5 : 3) | 48, 22.0f, 5.0f, 22.0f, 0.0f));
        TextView textView2 = new TextView(parentActivity);
        textView2.setTextColor(Theme.getColor(Theme.key_actionBarDefaultSubmenuItem));
        textView2.setTextSize(1, 20.0f);
        textView2.setTypeface(AndroidUtilities.bold());
        textView2.setLines(1);
        textView2.setMaxLines(1);
        textView2.setSingleLine(true);
        textView2.setGravity((LocaleController.isRTL ? 5 : 3) | 16);
        textView2.setEllipsize(TextUtils.TruncateAt.END);
        textView2.setText(LocaleController.getString(R.string.ImportMessages));
        boolean z = LocaleController.isRTL;
        frameLayout.addView(textView2, LayoutHelper.createFrame(-1, -2.0f, (z ? 5 : 3) | 48, z ? 21 : 76, 11.0f, z ? 76 : 21, 0.0f));
        frameLayout.addView(textView, LayoutHelper.createFrame(-2, -2.0f, (LocaleController.isRTL ? 5 : 3) | 48, 24.0f, 57.0f, 24.0f, 9.0f));
        if (user != null) {
            if (UserObject.isReplyUser(user)) {
                avatarDrawable.setScaleSize(0.8f);
                avatarDrawable.setAvatarType(12);
            } else if (user.id == clientUserId) {
                avatarDrawable.setScaleSize(0.8f);
                avatarDrawable.setAvatarType(1);
            } else {
                avatarDrawable.setScaleSize(1.0f);
                avatarDrawable.setInfo(currentAccount, user);
                backupImageView.setForUserOrChat(user, avatarDrawable);
            }
            backupImageView.setImage((ImageLocation) null, (String) null, avatarDrawable, user);
        } else {
            avatarDrawable.setInfo(currentAccount, chat);
            backupImageView.setForUserOrChat(chat, avatarDrawable);
        }
        textView.setText(AndroidUtilities.replaceTags(str2));
        builder.setPositiveButton(LocaleController.getString(R.string.Import), new AlertDialog.OnButtonClickListener() {
            @Override
            public final void onClick(AlertDialog alertDialog, int i) {
                AlertsCreator.lambda$createImportDialogAlert$25(runnable, alertDialog, i);
            }
        });
        builder.setNegativeButton(LocaleController.getString(R.string.Cancel), null);
        baseFragment.showDialog(builder.create());
    }

    public static AlertDialog.Builder createLanguageAlert(final LaunchActivity launchActivity, final TLRPC.TL_langPackLanguage tL_langPackLanguage) {
        String formatString;
        int i;
        int i2;
        if (tL_langPackLanguage == null) {
            return null;
        }
        tL_langPackLanguage.lang_code = tL_langPackLanguage.lang_code.replace('-', '_').toLowerCase();
        tL_langPackLanguage.plural_code = tL_langPackLanguage.plural_code.replace('-', '_').toLowerCase();
        String str = tL_langPackLanguage.base_lang_code;
        if (str != null) {
            tL_langPackLanguage.base_lang_code = str.replace('-', '_').toLowerCase();
        }
        final AlertDialog.Builder builder = new AlertDialog.Builder(launchActivity);
        if (LocaleController.getInstance().getCurrentLocaleInfo().shortName.equals(tL_langPackLanguage.lang_code)) {
            builder.setTitle(LocaleController.getString(R.string.Language));
            formatString = LocaleController.formatString("LanguageSame", R.string.LanguageSame, tL_langPackLanguage.name);
            builder.setNegativeButton(LocaleController.getString(R.string.OK), null);
            builder.setNeutralButton(LocaleController.getString(R.string.SETTINGS), new AlertDialog.OnButtonClickListener() {
                @Override
                public final void onClick(AlertDialog alertDialog, int i3) {
                    AlertsCreator.lambda$createLanguageAlert$8(LaunchActivity.this, alertDialog, i3);
                }
            });
        } else {
            if (tL_langPackLanguage.strings_count == 0) {
                builder.setTitle(LocaleController.getString(R.string.LanguageUnknownTitle));
                formatString = LocaleController.formatString("LanguageUnknownCustomAlert", R.string.LanguageUnknownCustomAlert, tL_langPackLanguage.name);
                i = R.string.OK;
            } else {
                builder.setTitle(LocaleController.getString(R.string.LanguageTitle));
                formatString = tL_langPackLanguage.official ? LocaleController.formatString("LanguageAlert", R.string.LanguageAlert, tL_langPackLanguage.name, Integer.valueOf((int) Math.ceil((tL_langPackLanguage.translated_count / tL_langPackLanguage.strings_count) * 100.0f))) : LocaleController.formatString("LanguageCustomAlert", R.string.LanguageCustomAlert, tL_langPackLanguage.name, Integer.valueOf((int) Math.ceil((tL_langPackLanguage.translated_count / tL_langPackLanguage.strings_count) * 100.0f)));
                builder.setPositiveButton(LocaleController.getString(R.string.Change), new AlertDialog.OnButtonClickListener() {
                    @Override
                    public final void onClick(AlertDialog alertDialog, int i3) {
                        AlertsCreator.lambda$createLanguageAlert$9(TLRPC.TL_langPackLanguage.this, launchActivity, alertDialog, i3);
                    }
                });
                i = R.string.Cancel;
            }
            builder.setNegativeButton(LocaleController.getString(i), null);
        }
        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(AndroidUtilities.replaceTags(formatString));
        int indexOf = TextUtils.indexOf((CharSequence) spannableStringBuilder, '[');
        if (indexOf != -1) {
            int i3 = indexOf + 1;
            i2 = TextUtils.indexOf((CharSequence) spannableStringBuilder, ']', i3);
            if (i2 != -1) {
                spannableStringBuilder.delete(i2, i2 + 1);
                spannableStringBuilder.delete(indexOf, i3);
            }
        } else {
            i2 = -1;
        }
        if (indexOf != -1 && i2 != -1) {
            spannableStringBuilder.setSpan(new URLSpanNoUnderline(tL_langPackLanguage.translations_url) {
                @Override
                public void onClick(View view) {
                    builder.getDismissRunnable().run();
                    super.onClick(view);
                }
            }, indexOf, i2 - 1, 33);
        }
        TextView textView = new TextView(launchActivity);
        textView.setText(spannableStringBuilder);
        textView.setTextSize(1, 16.0f);
        textView.setLinkTextColor(Theme.getColor(Theme.key_dialogTextLink));
        textView.setHighlightColor(Theme.getColor(Theme.key_dialogLinkSelection));
        textView.setPadding(AndroidUtilities.dp(23.0f), 0, AndroidUtilities.dp(23.0f), 0);
        textView.setMovementMethod(new AndroidUtilities.LinkMovementMethodMy());
        textView.setTextColor(Theme.getColor(Theme.key_dialogTextBlack));
        builder.setView(textView);
        return builder;
    }

    public static Dialog createLocationRequiredDialog(final Context context, boolean z) {
        return new AlertDialog.Builder(context).setMessage(AndroidUtilities.replaceTags(LocaleController.getString(R.string.PermissionNoLocationFriends))).setTopAnimation(R.raw.permission_request_location, 72, false, Theme.getColor(Theme.key_dialogTopBackground)).setPositiveButton(LocaleController.getString(R.string.PermissionOpenSettings), new AlertDialog.OnButtonClickListener() {
            @Override
            public final void onClick(AlertDialog alertDialog, int i) {
                AlertsCreator.lambda$createLocationRequiredDialog$0(context, alertDialog, i);
            }
        }).setNegativeButton(LocaleController.getString(R.string.ContactsPermissionAlertNotNow), null).create();
    }

    public static android.app.Dialog createLocationUpdateDialog(android.app.Activity r18, boolean r19, org.telegram.tgnet.TLRPC.User r20, final org.telegram.messenger.MessagesStorage.IntCallback r21, org.telegram.ui.ActionBar.Theme.ResourcesProvider r22) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.AlertsCreator.createLocationUpdateDialog(android.app.Activity, boolean, org.telegram.tgnet.TLRPC$User, org.telegram.messenger.MessagesStorage$IntCallback, org.telegram.ui.ActionBar.Theme$ResourcesProvider):android.app.Dialog");
    }

    public static BottomSheet createMuteAlert(final BaseFragment baseFragment, final long j, final long j2, final Theme.ResourcesProvider resourcesProvider) {
        if (baseFragment == null || baseFragment.getParentActivity() == null) {
            return null;
        }
        BottomSheet.Builder builder = new BottomSheet.Builder(baseFragment.getParentActivity(), false, resourcesProvider);
        builder.setTitle(LocaleController.getString(R.string.Notifications), true);
        int i = R.string.MuteFor;
        builder.setItems(new CharSequence[]{LocaleController.formatString("MuteFor", i, LocaleController.formatPluralString("Hours", 1, new Object[0])), LocaleController.formatString("MuteFor", i, LocaleController.formatPluralString("Hours", 8, new Object[0])), LocaleController.formatString("MuteFor", i, LocaleController.formatPluralString("Days", 2, new Object[0])), LocaleController.getString(R.string.MuteDisable)}, new DialogInterface.OnClickListener() {
            @Override
            public final void onClick(DialogInterface dialogInterface, int i2) {
                AlertsCreator.lambda$createMuteAlert$129(j, j2, baseFragment, resourcesProvider, dialogInterface, i2);
            }
        });
        return builder.create();
    }

    public static BottomSheet createMuteAlert(final BaseFragment baseFragment, final ArrayList arrayList, final int i, final Theme.ResourcesProvider resourcesProvider) {
        if (baseFragment == null || baseFragment.getParentActivity() == null) {
            return null;
        }
        BottomSheet.Builder builder = new BottomSheet.Builder(baseFragment.getParentActivity(), false, resourcesProvider);
        builder.setTitle(LocaleController.getString(R.string.Notifications), true);
        int i2 = R.string.MuteFor;
        builder.setItems(new CharSequence[]{LocaleController.formatString("MuteFor", i2, LocaleController.formatPluralString("Hours", 1, new Object[0])), LocaleController.formatString("MuteFor", i2, LocaleController.formatPluralString("Hours", 8, new Object[0])), LocaleController.formatString("MuteFor", i2, LocaleController.formatPluralString("Days", 2, new Object[0])), LocaleController.getString(R.string.MuteDisable)}, new DialogInterface.OnClickListener() {
            @Override
            public final void onClick(DialogInterface dialogInterface, int i3) {
                AlertsCreator.lambda$createMuteAlert$130(arrayList, i, baseFragment, resourcesProvider, dialogInterface, i3);
            }
        });
        return builder.create();
    }

    public static BottomSheet.Builder createMuteForPickerDialog(Context context, Theme.ResourcesProvider resourcesProvider, final ScheduleDatePickerDelegate scheduleDatePickerDelegate) {
        if (context == null) {
            return null;
        }
        ScheduleDatePickerColors scheduleDatePickerColors = new ScheduleDatePickerColors(resourcesProvider);
        final BottomSheet.Builder builder = new BottomSheet.Builder(context, false, resourcesProvider);
        builder.setApplyBottomPadding(false);
        final int[] iArr = {30, 60, 120, 180, 480, 1440, 2880, 4320, 5760, 7200, 8640, 10080, 20160, 30240, 44640, 89280, 133920, 178560, 223200, 267840, 525600};
        final NumberPicker numberPicker = new NumberPicker(context, resourcesProvider) {
            @Override
            protected CharSequence getContentDescription(int i) {
                int i2 = iArr[i];
                return i2 == 0 ? LocaleController.getString(R.string.MuteNever) : i2 < 60 ? LocaleController.formatPluralString("Minutes", i2, new Object[0]) : i2 < 1440 ? LocaleController.formatPluralString("Hours", i2 / 60, new Object[0]) : i2 < 10080 ? LocaleController.formatPluralString("Days", i2 / 1440, new Object[0]) : i2 < 44640 ? LocaleController.formatPluralString("Weeks", i2 / 10080, new Object[0]) : i2 < 525600 ? LocaleController.formatPluralString("Months", i2 / 44640, new Object[0]) : LocaleController.formatPluralString("Years", i2 / 525600, new Object[0]);
            }
        };
        numberPicker.setMinValue(0);
        numberPicker.setMaxValue(20);
        numberPicker.setTextColor(scheduleDatePickerColors.textColor);
        numberPicker.setValue(0);
        numberPicker.setFormatter(new NumberPicker.Formatter() {
            @Override
            public final String format(int i) {
                String lambda$createMuteForPickerDialog$119;
                lambda$createMuteForPickerDialog$119 = AlertsCreator.lambda$createMuteForPickerDialog$119(iArr, i);
                return lambda$createMuteForPickerDialog$119;
            }
        });
        LinearLayout linearLayout = new LinearLayout(context) {
            boolean ignoreLayout = false;

            @Override
            protected void onMeasure(int i, int i2) {
                this.ignoreLayout = true;
                android.graphics.Point point = AndroidUtilities.displaySize;
                int i3 = point.x > point.y ? 3 : 5;
                numberPicker.setItemCount(i3);
                numberPicker.getLayoutParams().height = AndroidUtilities.dp(42.0f) * i3;
                this.ignoreLayout = false;
                super.onMeasure(i, i2);
            }

            @Override
            public void requestLayout() {
                if (this.ignoreLayout) {
                    return;
                }
                super.requestLayout();
            }
        };
        linearLayout.setOrientation(1);
        FrameLayout frameLayout = new FrameLayout(context);
        linearLayout.addView(frameLayout, LayoutHelper.createLinear(-1, -2, 51, 22, 0, 0, 4));
        TextView textView = new TextView(context);
        textView.setText(LocaleController.getString(R.string.MuteForAlert));
        textView.setTextColor(scheduleDatePickerColors.textColor);
        textView.setTextSize(1, 20.0f);
        textView.setTypeface(AndroidUtilities.bold());
        frameLayout.addView(textView, LayoutHelper.createFrame(-2, -2.0f, 51, 0.0f, 12.0f, 0.0f, 0.0f));
        textView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public final boolean onTouch(View view, MotionEvent motionEvent) {
                boolean lambda$createMuteForPickerDialog$120;
                lambda$createMuteForPickerDialog$120 = AlertsCreator.lambda$createMuteForPickerDialog$120(view, motionEvent);
                return lambda$createMuteForPickerDialog$120;
            }
        });
        LinearLayout linearLayout2 = new LinearLayout(context);
        linearLayout2.setOrientation(0);
        linearLayout2.setWeightSum(1.0f);
        linearLayout.addView(linearLayout2, LayoutHelper.createLinear(-1, -2, 1.0f, 0, 0, 12, 0, 12));
        TextView textView2 = new TextView(context) {
            @Override
            public CharSequence getAccessibilityClassName() {
                return Button.class.getName();
            }
        };
        linearLayout2.addView(numberPicker, LayoutHelper.createLinear(0, 270, 1.0f));
        numberPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public final void onValueChange(NumberPicker numberPicker2, int i, int i2) {
                AlertsCreator.lambda$createMuteForPickerDialog$121(numberPicker2, i, i2);
            }
        });
        textView2.setPadding(AndroidUtilities.dp(34.0f), 0, AndroidUtilities.dp(34.0f), 0);
        textView2.setGravity(17);
        textView2.setTextColor(scheduleDatePickerColors.buttonTextColor);
        textView2.setTextSize(1, 14.0f);
        textView2.setTypeface(AndroidUtilities.bold());
        textView2.setBackgroundDrawable(Theme.createSimpleSelectorRoundRectDrawable(AndroidUtilities.dp(8.0f), scheduleDatePickerColors.buttonBackgroundColor, scheduleDatePickerColors.buttonBackgroundPressedColor));
        textView2.setText(LocaleController.getString(R.string.AutoDeleteConfirm));
        linearLayout.addView(textView2, LayoutHelper.createLinear(-1, 48, 83, 16, 15, 16, 16));
        textView2.setOnClickListener(new View.OnClickListener() {
            @Override
            public final void onClick(View view) {
                AlertsCreator.lambda$createMuteForPickerDialog$122(iArr, numberPicker, scheduleDatePickerDelegate, builder, view);
            }
        });
        builder.setCustomView(linearLayout);
        BottomSheet show = builder.show();
        show.setBackgroundColor(scheduleDatePickerColors.backgroundColor);
        show.fixNavigationBar(scheduleDatePickerColors.backgroundColor);
        return builder;
    }

    public static AlertDialog.Builder createNoAccessAlert(Context context, String str, String str2, Theme.ResourcesProvider resourcesProvider) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(str);
        HashMap hashMap = new HashMap();
        int i = Theme.key_dialogTopBackground;
        hashMap.put("info1.**", Integer.valueOf(Theme.getColor(i, resourcesProvider)));
        hashMap.put("info2.**", Integer.valueOf(Theme.getColor(i, resourcesProvider)));
        builder.setTopAnimation(R.raw.not_available, 52, false, Theme.getColor(i, resourcesProvider), hashMap);
        builder.setTopAnimationIsNew(true);
        builder.setPositiveButton(LocaleController.getString(R.string.Close), null);
        builder.setMessage(str2);
        return builder;
    }

    public static Dialog createPopupSelectDialog(Activity activity, final int i, final Runnable runnable) {
        SharedPreferences notificationsSettings = MessagesController.getNotificationsSettings(UserConfig.selectedAccount);
        final int[] iArr = new int[1];
        if (i == 1) {
            iArr[0] = notificationsSettings.getInt("popupAll", 0);
        } else if (i == 0) {
            iArr[0] = notificationsSettings.getInt("popupGroup", 0);
        } else {
            iArr[0] = notificationsSettings.getInt("popupChannel", 0);
        }
        String[] strArr = {LocaleController.getString(R.string.NoPopup), LocaleController.getString(R.string.OnlyWhenScreenOn), LocaleController.getString(R.string.OnlyWhenScreenOff), LocaleController.getString(R.string.AlwaysShowPopup)};
        LinearLayout linearLayout = new LinearLayout(activity);
        linearLayout.setOrientation(1);
        final AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        int i2 = 0;
        while (i2 < 4) {
            RadioColorCell radioColorCell = new RadioColorCell(activity);
            radioColorCell.setTag(Integer.valueOf(i2));
            radioColorCell.setPadding(AndroidUtilities.dp(4.0f), 0, AndroidUtilities.dp(4.0f), 0);
            radioColorCell.setCheckColor(Theme.getColor(Theme.key_radioBackground), Theme.getColor(Theme.key_dialogRadioBackgroundChecked));
            radioColorCell.setTextAndValue(strArr[i2], iArr[0] == i2);
            linearLayout.addView(radioColorCell);
            radioColorCell.setOnClickListener(new View.OnClickListener() {
                @Override
                public final void onClick(View view) {
                    AlertsCreator.lambda$createPopupSelectDialog$149(iArr, i, builder, runnable, view);
                }
            });
            i2++;
        }
        builder.setTitle(LocaleController.getString(R.string.PopupNotification));
        builder.setView(linearLayout);
        builder.setPositiveButton(LocaleController.getString(R.string.Cancel), null);
        return builder.create();
    }

    public static Dialog createPrioritySelectDialog(Activity activity, long j, int i, int i2, Runnable runnable) {
        return createPrioritySelectDialog(activity, j, i, i2, runnable, null);
    }

    public static Dialog createPrioritySelectDialog(Activity activity, final long j, final long j2, final int i, final Runnable runnable, Theme.ResourcesProvider resourcesProvider) {
        String[] strArr;
        Activity activity2 = activity;
        final SharedPreferences notificationsSettings = MessagesController.getNotificationsSettings(UserConfig.selectedAccount);
        int[] iArr = new int[1];
        int i2 = 0;
        if (j != 0) {
            int i3 = notificationsSettings.getInt("priority_" + j, 3);
            iArr[0] = i3;
            if (i3 == 3) {
                iArr[0] = 0;
            } else if (i3 == 4) {
                iArr[0] = 1;
            } else if (i3 == 5) {
                iArr[0] = 2;
            } else if (i3 == 0) {
                iArr[0] = 3;
            } else {
                iArr[0] = 4;
            }
            strArr = new String[]{LocaleController.getString(R.string.NotificationsPrioritySettings), LocaleController.getString(R.string.NotificationsPriorityLow), LocaleController.getString(R.string.NotificationsPriorityMedium), LocaleController.getString(R.string.NotificationsPriorityHigh), LocaleController.getString(R.string.NotificationsPriorityUrgent)};
        } else {
            if (i == 1) {
                iArr[0] = notificationsSettings.getInt("priority_messages", 1);
            } else if (i == 0) {
                iArr[0] = notificationsSettings.getInt("priority_group", 1);
            } else if (i == 2) {
                iArr[0] = notificationsSettings.getInt("priority_channel", 1);
            } else if (i == 3) {
                iArr[0] = notificationsSettings.getInt("priority_stories", 1);
            } else if (i == 4 || i == 5) {
                iArr[0] = notificationsSettings.getInt("priority_react", 1);
            }
            int i4 = iArr[0];
            if (i4 == 4) {
                iArr[0] = 0;
            } else if (i4 == 5) {
                iArr[0] = 1;
            } else if (i4 == 0) {
                iArr[0] = 2;
            } else {
                iArr[0] = 3;
            }
            strArr = new String[]{LocaleController.getString(R.string.NotificationsPriorityLow), LocaleController.getString(R.string.NotificationsPriorityMedium), LocaleController.getString(R.string.NotificationsPriorityHigh), LocaleController.getString(R.string.NotificationsPriorityUrgent)};
        }
        String[] strArr2 = strArr;
        LinearLayout linearLayout = new LinearLayout(activity2);
        linearLayout.setOrientation(1);
        AlertDialog.Builder builder = new AlertDialog.Builder(activity2, resourcesProvider);
        int i5 = 0;
        while (i5 < strArr2.length) {
            RadioColorCell radioColorCell = new RadioColorCell(activity2, resourcesProvider);
            radioColorCell.setPadding(AndroidUtilities.dp(4.0f), i2, AndroidUtilities.dp(4.0f), i2);
            radioColorCell.setTag(Integer.valueOf(i5));
            radioColorCell.setCheckColor(Theme.getColor(Theme.key_radioBackground, resourcesProvider), Theme.getColor(Theme.key_dialogRadioBackgroundChecked, resourcesProvider));
            radioColorCell.setTextAndValue(strArr2[i5], iArr[i2] == i5);
            linearLayout.addView(radioColorCell);
            final int[] iArr2 = iArr;
            final AlertDialog.Builder builder2 = builder;
            radioColorCell.setOnClickListener(new View.OnClickListener() {
                @Override
                public final void onClick(View view) {
                    AlertsCreator.lambda$createPrioritySelectDialog$148(iArr2, j, j2, i, notificationsSettings, builder2, runnable, view);
                }
            });
            i5++;
            activity2 = activity;
            linearLayout = linearLayout;
            builder = builder2;
            strArr2 = strArr2;
            iArr = iArr;
            i2 = 0;
        }
        AlertDialog.Builder builder3 = builder;
        builder3.setTitle(LocaleController.getString(R.string.NotificationsImportance));
        builder3.setView(linearLayout);
        builder3.setPositiveButton(LocaleController.getString(R.string.Cancel), null);
        return builder3.create();
    }

    public static void createReportPhotoAlert(final int i, final Context context, final long j, final TLRPC.Photo photo, final Theme.ResourcesProvider resourcesProvider) {
        if (context == null || photo == null) {
            return;
        }
        final Utilities.Callback2 callback2 = new Utilities.Callback2() {
            @Override
            public final void run(Object obj, Object obj2) {
                AlertsCreator.lambda$createReportPhotoAlert$131(i, j, photo, context, resourcesProvider, (Integer) obj, (String) obj2);
            }
        };
        BottomSheet.Builder builder = new BottomSheet.Builder(context, true, resourcesProvider);
        builder.setTitle(LocaleController.getString(R.string.ReportProfilePhoto), true);
        final int[] iArr = {0, 6, 1, 2, 3, 4, 5, 100};
        builder.setItems(new CharSequence[]{LocaleController.getString(R.string.ReportChatSpam), LocaleController.getString(R.string.ReportChatFakeAccount), LocaleController.getString(R.string.ReportChatViolence), LocaleController.getString(R.string.ReportChatChild), LocaleController.getString(R.string.ReportChatIllegalDrugs), LocaleController.getString(R.string.ReportChatPersonalDetails), LocaleController.getString(R.string.ReportChatPornography), LocaleController.getString(R.string.ReportChatOther)}, new int[]{R.drawable.msg_clearcache, R.drawable.msg_report_fake, R.drawable.msg_report_violence, R.drawable.msg_block2, R.drawable.msg_report_drugs, R.drawable.msg_report_personal, R.drawable.msg_report_xxx, R.drawable.msg_report_other}, new DialogInterface.OnClickListener() {
            @Override
            public final void onClick(DialogInterface dialogInterface, int i2) {
                AlertsCreator.lambda$createReportPhotoAlert$132(iArr, context, resourcesProvider, callback2, dialogInterface, i2);
            }
        });
        builder.show();
    }

    public static BottomSheet.Builder createScheduleDatePickerDialog(Context context, long j, long j2, ScheduleDatePickerDelegate scheduleDatePickerDelegate, Runnable runnable) {
        return createScheduleDatePickerDialog(context, j, j2, scheduleDatePickerDelegate, runnable, new ScheduleDatePickerColors(), null);
    }

    public static BottomSheet.Builder createScheduleDatePickerDialog(Context context, long j, long j2, ScheduleDatePickerDelegate scheduleDatePickerDelegate, Runnable runnable, Theme.ResourcesProvider resourcesProvider) {
        return createScheduleDatePickerDialog(context, j, j2, scheduleDatePickerDelegate, runnable, new ScheduleDatePickerColors(resourcesProvider), resourcesProvider);
    }

    public static BottomSheet.Builder createScheduleDatePickerDialog(Context context, final long j, long j2, final ScheduleDatePickerDelegate scheduleDatePickerDelegate, final Runnable runnable, final ScheduleDatePickerColors scheduleDatePickerColors, Theme.ResourcesProvider resourcesProvider) {
        LinearLayout linearLayout;
        NumberPicker numberPicker;
        final Calendar calendar;
        TLRPC.User user;
        TLRPC.UserStatus userStatus;
        if (context == null) {
            return null;
        }
        final long clientUserId = UserConfig.getInstance(UserConfig.selectedAccount).getClientUserId();
        final BottomSheet.Builder builder = new BottomSheet.Builder(context, false, resourcesProvider);
        builder.setApplyBottomPadding(false);
        final NumberPicker numberPicker2 = new NumberPicker(context, resourcesProvider);
        numberPicker2.setTextColor(scheduleDatePickerColors.textColor);
        numberPicker2.setTextOffset(AndroidUtilities.dp(10.0f));
        numberPicker2.setItemCount(5);
        final NumberPicker numberPicker3 = new NumberPicker(context, resourcesProvider) {
            @Override
            protected CharSequence getContentDescription(int i) {
                return LocaleController.formatPluralString("Hours", i, new Object[0]);
            }
        };
        numberPicker3.setWrapSelectorWheel(true);
        numberPicker3.setAllItemsCount(24);
        numberPicker3.setItemCount(5);
        numberPicker3.setTextColor(scheduleDatePickerColors.textColor);
        numberPicker3.setTextOffset(-AndroidUtilities.dp(10.0f));
        final NumberPicker numberPicker4 = new NumberPicker(context, resourcesProvider) {
            @Override
            protected CharSequence getContentDescription(int i) {
                return LocaleController.formatPluralString("Minutes", i, new Object[0]);
            }
        };
        numberPicker4.setWrapSelectorWheel(true);
        numberPicker4.setAllItemsCount(60);
        numberPicker4.setItemCount(5);
        numberPicker4.setTextColor(scheduleDatePickerColors.textColor);
        numberPicker4.setTextOffset(-AndroidUtilities.dp(34.0f));
        LinearLayout linearLayout2 = new LinearLayout(context) {
            boolean ignoreLayout = false;

            @Override
            protected void onMeasure(int i, int i2) {
                this.ignoreLayout = true;
                android.graphics.Point point = AndroidUtilities.displaySize;
                int i3 = point.x > point.y ? 3 : 5;
                numberPicker2.setItemCount(i3);
                numberPicker3.setItemCount(i3);
                numberPicker4.setItemCount(i3);
                numberPicker2.getLayoutParams().height = AndroidUtilities.dp(42.0f) * i3;
                numberPicker3.getLayoutParams().height = AndroidUtilities.dp(42.0f) * i3;
                numberPicker4.getLayoutParams().height = AndroidUtilities.dp(42.0f) * i3;
                this.ignoreLayout = false;
                super.onMeasure(i, i2);
            }

            @Override
            public void requestLayout() {
                if (this.ignoreLayout) {
                    return;
                }
                super.requestLayout();
            }
        };
        linearLayout2.setOrientation(1);
        FrameLayout frameLayout = new FrameLayout(context);
        linearLayout2.addView(frameLayout, LayoutHelper.createLinear(-1, -2, 51, 22, 0, 0, 4));
        TextView textView = new TextView(context);
        textView.setText(LocaleController.getString(j == clientUserId ? R.string.SetReminder : R.string.ScheduleMessage));
        textView.setTextColor(scheduleDatePickerColors.textColor);
        textView.setTextSize(1, 20.0f);
        textView.setTypeface(AndroidUtilities.bold());
        frameLayout.addView(textView, LayoutHelper.createFrame(-2, -2.0f, 51, 0.0f, 12.0f, 0.0f, 0.0f));
        textView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public final boolean onTouch(View view, MotionEvent motionEvent) {
                boolean lambda$createScheduleDatePickerDialog$78;
                lambda$createScheduleDatePickerDialog$78 = AlertsCreator.lambda$createScheduleDatePickerDialog$78(view, motionEvent);
                return lambda$createScheduleDatePickerDialog$78;
            }
        });
        if (!DialogObject.isUserDialog(j) || j == clientUserId || (user = MessagesController.getInstance(UserConfig.selectedAccount).getUser(Long.valueOf(j))) == null || user.bot || (userStatus = user.status) == null || userStatus.expires <= 0) {
            linearLayout = linearLayout2;
            numberPicker = numberPicker4;
        } else {
            String firstName = UserObject.getFirstName(user);
            if (firstName.length() > 10) {
                firstName = firstName.substring(0, 10) + "…";
            }
            linearLayout = linearLayout2;
            numberPicker = numberPicker4;
            final ActionBarMenuItem actionBarMenuItem = new ActionBarMenuItem(context, null, 0, scheduleDatePickerColors.iconColor, false, resourcesProvider);
            actionBarMenuItem.setLongClickEnabled(false);
            actionBarMenuItem.setSubMenuOpenSide(2);
            actionBarMenuItem.setIcon(R.drawable.ic_ab_other);
            actionBarMenuItem.setBackgroundDrawable(Theme.createSelectorDrawable(scheduleDatePickerColors.iconSelectorColor, 1));
            frameLayout.addView(actionBarMenuItem, LayoutHelper.createFrame(40, 40.0f, 53, 0.0f, 8.0f, 5.0f, 0.0f));
            actionBarMenuItem.addSubItem(1, LocaleController.formatString("ScheduleWhenOnline", R.string.ScheduleWhenOnline, firstName));
            actionBarMenuItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public final void onClick(View view) {
                    AlertsCreator.lambda$createScheduleDatePickerDialog$79(ActionBarMenuItem.this, scheduleDatePickerColors, view);
                }
            });
            actionBarMenuItem.setDelegate(new ActionBarMenuItem.ActionBarMenuItemDelegate() {
                @Override
                public final void onItemClick(int i) {
                    AlertsCreator.lambda$createScheduleDatePickerDialog$80(AlertsCreator.ScheduleDatePickerDelegate.this, builder, i);
                }
            });
            actionBarMenuItem.setContentDescription(LocaleController.getString(R.string.AccDescrMoreOptions));
        }
        LinearLayout linearLayout3 = new LinearLayout(context);
        linearLayout3.setOrientation(0);
        linearLayout3.setWeightSum(1.0f);
        LinearLayout linearLayout4 = linearLayout;
        linearLayout4.addView(linearLayout3, LayoutHelper.createLinear(-1, -2, 1.0f, 0, 0, 12, 0, 12));
        final long currentTimeMillis = System.currentTimeMillis();
        final Calendar calendar2 = Calendar.getInstance();
        calendar2.setTimeInMillis(currentTimeMillis);
        final int i = calendar2.get(1);
        final TextView textView2 = new TextView(context) {
            @Override
            public CharSequence getAccessibilityClassName() {
                return Button.class.getName();
            }
        };
        linearLayout3.addView(numberPicker2, LayoutHelper.createLinear(0, 270, 0.5f));
        numberPicker2.setMinValue(0);
        numberPicker2.setMaxValue(365);
        numberPicker2.setWrapSelectorWheel(false);
        numberPicker2.setFormatter(new NumberPicker.Formatter() {
            @Override
            public final String format(int i2) {
                String lambda$createScheduleDatePickerDialog$81;
                lambda$createScheduleDatePickerDialog$81 = AlertsCreator.lambda$createScheduleDatePickerDialog$81(currentTimeMillis, calendar2, i, i2);
                return lambda$createScheduleDatePickerDialog$81;
            }
        });
        final NumberPicker numberPicker5 = numberPicker;
        NumberPicker.OnValueChangeListener onValueChangeListener = new NumberPicker.OnValueChangeListener() {
            @Override
            public final void onValueChange(NumberPicker numberPicker6, int i2, int i3) {
                AlertsCreator.lambda$createScheduleDatePickerDialog$82(textView2, clientUserId, j, numberPicker2, numberPicker3, numberPicker5, numberPicker6, i2, i3);
            }
        };
        numberPicker2.setOnValueChangedListener(onValueChangeListener);
        numberPicker3.setMinValue(0);
        numberPicker3.setMaxValue(23);
        linearLayout3.addView(numberPicker3, LayoutHelper.createLinear(0, 270, 0.2f));
        numberPicker3.setFormatter(new NumberPicker.Formatter() {
            @Override
            public final String format(int i2) {
                String lambda$createScheduleDatePickerDialog$83;
                lambda$createScheduleDatePickerDialog$83 = AlertsCreator.lambda$createScheduleDatePickerDialog$83(i2);
                return lambda$createScheduleDatePickerDialog$83;
            }
        });
        numberPicker3.setOnValueChangedListener(onValueChangeListener);
        numberPicker5.setMinValue(0);
        numberPicker5.setMaxValue(59);
        numberPicker5.setValue(0);
        numberPicker5.setFormatter(new NumberPicker.Formatter() {
            @Override
            public final String format(int i2) {
                String lambda$createScheduleDatePickerDialog$84;
                lambda$createScheduleDatePickerDialog$84 = AlertsCreator.lambda$createScheduleDatePickerDialog$84(i2);
                return lambda$createScheduleDatePickerDialog$84;
            }
        });
        linearLayout3.addView(numberPicker5, LayoutHelper.createLinear(0, 270, 0.3f));
        numberPicker5.setOnValueChangedListener(onValueChangeListener);
        if (j2 <= 0 || j2 == 2147483646) {
            calendar = calendar2;
        } else {
            long j3 = 1000 * j2;
            calendar = calendar2;
            calendar.setTimeInMillis(System.currentTimeMillis());
            calendar.set(12, 0);
            calendar.set(13, 0);
            calendar.set(14, 0);
            calendar.set(11, 0);
            int timeInMillis = (int) ((j3 - calendar.getTimeInMillis()) / 86400000);
            calendar.setTimeInMillis(j3);
            if (timeInMillis >= 0) {
                numberPicker5.setValue(calendar.get(12));
                numberPicker3.setValue(calendar.get(11));
                numberPicker2.setValue(timeInMillis);
            }
        }
        final boolean[] zArr = {true};
        checkScheduleDate(textView2, null, clientUserId == j ? 1 : 0, numberPicker2, numberPicker3, numberPicker5);
        textView2.setPadding(AndroidUtilities.dp(34.0f), 0, AndroidUtilities.dp(34.0f), 0);
        textView2.setGravity(17);
        textView2.setTextColor(scheduleDatePickerColors.buttonTextColor);
        textView2.setTextSize(1, 14.0f);
        textView2.setTypeface(AndroidUtilities.bold());
        textView2.setBackground(Theme.AdaptiveRipple.filledRect(scheduleDatePickerColors.buttonBackgroundColor, 8.0f));
        linearLayout4.addView(textView2, LayoutHelper.createLinear(-1, 48, 83, 16, 15, 16, 16));
        textView2.setOnClickListener(new View.OnClickListener() {
            @Override
            public final void onClick(View view) {
                AlertsCreator.lambda$createScheduleDatePickerDialog$85(zArr, clientUserId, j, numberPicker2, numberPicker3, numberPicker5, calendar, scheduleDatePickerDelegate, builder, view);
            }
        });
        builder.setCustomView(linearLayout4);
        BottomSheet show = builder.show();
        show.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public final void onDismiss(DialogInterface dialogInterface) {
                AlertsCreator.lambda$createScheduleDatePickerDialog$86(runnable, zArr, dialogInterface);
            }
        });
        show.setBackgroundColor(scheduleDatePickerColors.backgroundColor);
        show.fixNavigationBar(scheduleDatePickerColors.backgroundColor);
        return builder;
    }

    public static BottomSheet.Builder createScheduleDatePickerDialog(Context context, long j, ScheduleDatePickerDelegate scheduleDatePickerDelegate) {
        return createScheduleDatePickerDialog(context, j, -1L, scheduleDatePickerDelegate, (Runnable) null);
    }

    public static BottomSheet.Builder createScheduleDatePickerDialog(Context context, long j, ScheduleDatePickerDelegate scheduleDatePickerDelegate, Runnable runnable, Theme.ResourcesProvider resourcesProvider) {
        return createScheduleDatePickerDialog(context, j, -1L, scheduleDatePickerDelegate, runnable, resourcesProvider);
    }

    public static BottomSheet.Builder createScheduleDatePickerDialog(Context context, long j, ScheduleDatePickerDelegate scheduleDatePickerDelegate, Theme.ResourcesProvider resourcesProvider) {
        return createScheduleDatePickerDialog(context, j, -1L, scheduleDatePickerDelegate, null, resourcesProvider);
    }

    public static BottomSheet.Builder createScheduleDatePickerDialog(Context context, long j, ScheduleDatePickerDelegate scheduleDatePickerDelegate, ScheduleDatePickerColors scheduleDatePickerColors) {
        return createScheduleDatePickerDialog(context, j, -1L, scheduleDatePickerDelegate, null, scheduleDatePickerColors, null);
    }

    public static AlertDialog.Builder createSimpleAlert(Context context, String str) {
        return createSimpleAlert(context, null, str);
    }

    public static AlertDialog.Builder createSimpleAlert(Context context, String str, String str2) {
        return createSimpleAlert(context, str, str2, null);
    }

    public static AlertDialog.Builder createSimpleAlert(Context context, String str, String str2, String str3, final Runnable runnable, Theme.ResourcesProvider resourcesProvider) {
        if (context == null || str2 == null) {
            return null;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        if (str == null) {
            str = LocaleController.getString(R.string.AppName);
        }
        builder.setTitle(str);
        builder.setMessage(str2);
        if (str3 == null) {
            builder.setPositiveButton(LocaleController.getString(R.string.OK), null);
        } else {
            builder.setNegativeButton(LocaleController.getString(R.string.Cancel), null);
            builder.setPositiveButton(str3, new AlertDialog.OnButtonClickListener() {
                @Override
                public final void onClick(AlertDialog alertDialog, int i) {
                    AlertsCreator.lambda$createSimpleAlert$10(runnable, alertDialog, i);
                }
            });
        }
        return builder;
    }

    public static AlertDialog.Builder createSimpleAlert(Context context, String str, String str2, Theme.ResourcesProvider resourcesProvider) {
        return createSimpleAlert(context, str, str2, null, null, resourcesProvider);
    }

    public static ActionBarPopupWindow createSimplePopup(BaseFragment baseFragment, View view, View view2, float f, float f2) {
        if (baseFragment == null || view2 == null || view == null) {
            return null;
        }
        ActionBarPopupWindow actionBarPopupWindow = new ActionBarPopupWindow(view, -2, -2);
        actionBarPopupWindow.setPauseNotifications(true);
        actionBarPopupWindow.setDismissAnimationDuration(220);
        actionBarPopupWindow.setOutsideTouchable(true);
        actionBarPopupWindow.setClippingEnabled(true);
        actionBarPopupWindow.setAnimationStyle(R.style.PopupContextAnimation);
        actionBarPopupWindow.setFocusable(true);
        view.measure(View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(1000.0f), Integer.MIN_VALUE), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(1000.0f), Integer.MIN_VALUE));
        actionBarPopupWindow.setInputMethodMode(2);
        actionBarPopupWindow.getContentView().setFocusableInTouchMode(true);
        float f3 = 0.0f;
        View view3 = view2;
        float f4 = 0.0f;
        while (view3 != view2.getRootView()) {
            f4 += view3.getX();
            f3 += view3.getY();
            view3 = (View) view3.getParent();
            if (view3 == null) {
                break;
            }
        }
        actionBarPopupWindow.showAtLocation(view2.getRootView(), 0, (int) ((f4 + f) - (view.getMeasuredWidth() / 2.0f)), (int) ((f3 + f2) - (view.getMeasuredHeight() / 2.0f)));
        actionBarPopupWindow.dimBehind();
        return actionBarPopupWindow;
    }

    public static Dialog createSingleChoiceDialog(Activity activity, String[] strArr, String str, int i, final DialogInterface.OnClickListener onClickListener) {
        LinearLayout linearLayout = new LinearLayout(activity);
        linearLayout.setOrientation(1);
        final AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        int i2 = 0;
        while (i2 < strArr.length) {
            RadioColorCell radioColorCell = new RadioColorCell(activity);
            radioColorCell.setPadding(AndroidUtilities.dp(4.0f), 0, AndroidUtilities.dp(4.0f), 0);
            radioColorCell.setTag(Integer.valueOf(i2));
            radioColorCell.setCheckColor(Theme.getColor(Theme.key_radioBackground), Theme.getColor(Theme.key_dialogRadioBackgroundChecked));
            radioColorCell.setTextAndValue(strArr[i2], i == i2);
            linearLayout.addView(radioColorCell);
            radioColorCell.setOnClickListener(new View.OnClickListener() {
                @Override
                public final void onClick(View view) {
                    AlertsCreator.lambda$createSingleChoiceDialog$150(AlertDialog.Builder.this, onClickListener, view);
                }
            });
            i2++;
        }
        builder.setTitle(str);
        builder.setView(linearLayout);
        builder.setPositiveButton(LocaleController.getString(R.string.Cancel), null);
        return builder.create();
    }

    public static BottomSheet.Builder createSoundFrequencyPickerDialog(Context context, int i, int i2, final SoundFrequencyDelegate soundFrequencyDelegate, Theme.ResourcesProvider resourcesProvider) {
        if (context == null) {
            return null;
        }
        ScheduleDatePickerColors scheduleDatePickerColors = new ScheduleDatePickerColors(resourcesProvider);
        final BottomSheet.Builder builder = new BottomSheet.Builder(context, false, resourcesProvider);
        builder.setApplyBottomPadding(false);
        final NumberPicker numberPicker = new NumberPicker(context, resourcesProvider) {
            @Override
            protected CharSequence getContentDescription(int i3) {
                return LocaleController.formatPluralString("Times", i3 + 1, new Object[0]);
            }
        };
        numberPicker.setMinValue(0);
        numberPicker.setMaxValue(10);
        numberPicker.setTextColor(scheduleDatePickerColors.textColor);
        numberPicker.setValue(i - 1);
        numberPicker.setWrapSelectorWheel(false);
        numberPicker.setFormatter(new NumberPicker.Formatter() {
            @Override
            public final String format(int i3) {
                String lambda$createSoundFrequencyPickerDialog$113;
                lambda$createSoundFrequencyPickerDialog$113 = AlertsCreator.lambda$createSoundFrequencyPickerDialog$113(i3);
                return lambda$createSoundFrequencyPickerDialog$113;
            }
        });
        final NumberPicker numberPicker2 = new NumberPicker(context, resourcesProvider) {
            @Override
            protected CharSequence getContentDescription(int i3) {
                return LocaleController.formatPluralString("Times", i3 + 1, new Object[0]);
            }
        };
        numberPicker2.setMinValue(0);
        numberPicker2.setMaxValue(10);
        numberPicker2.setTextColor(scheduleDatePickerColors.textColor);
        numberPicker2.setValue((i2 / 60) - 1);
        numberPicker2.setWrapSelectorWheel(false);
        numberPicker2.setFormatter(new NumberPicker.Formatter() {
            @Override
            public final String format(int i3) {
                String lambda$createSoundFrequencyPickerDialog$114;
                lambda$createSoundFrequencyPickerDialog$114 = AlertsCreator.lambda$createSoundFrequencyPickerDialog$114(i3);
                return lambda$createSoundFrequencyPickerDialog$114;
            }
        });
        final NumberPicker numberPicker3 = new NumberPicker(context, resourcesProvider);
        numberPicker3.setMinValue(0);
        numberPicker3.setMaxValue(0);
        numberPicker3.setTextColor(scheduleDatePickerColors.textColor);
        numberPicker3.setValue(0);
        numberPicker3.setWrapSelectorWheel(false);
        numberPicker3.setFormatter(new NumberPicker.Formatter() {
            @Override
            public final String format(int i3) {
                String lambda$createSoundFrequencyPickerDialog$115;
                lambda$createSoundFrequencyPickerDialog$115 = AlertsCreator.lambda$createSoundFrequencyPickerDialog$115(i3);
                return lambda$createSoundFrequencyPickerDialog$115;
            }
        });
        LinearLayout linearLayout = new LinearLayout(context) {
            boolean ignoreLayout = false;

            @Override
            protected void onMeasure(int i3, int i4) {
                this.ignoreLayout = true;
                android.graphics.Point point = AndroidUtilities.displaySize;
                int i5 = point.x > point.y ? 3 : 5;
                numberPicker.setItemCount(i5);
                numberPicker.getLayoutParams().height = AndroidUtilities.dp(42.0f) * i5;
                numberPicker2.setItemCount(i5);
                numberPicker2.getLayoutParams().height = AndroidUtilities.dp(42.0f) * i5;
                numberPicker3.setItemCount(i5);
                numberPicker3.getLayoutParams().height = AndroidUtilities.dp(42.0f) * i5;
                this.ignoreLayout = false;
                super.onMeasure(i3, i4);
            }

            @Override
            public void requestLayout() {
                if (this.ignoreLayout) {
                    return;
                }
                super.requestLayout();
            }
        };
        linearLayout.setOrientation(1);
        FrameLayout frameLayout = new FrameLayout(context);
        linearLayout.addView(frameLayout, LayoutHelper.createLinear(-1, -2, 51, 22, 0, 0, 4));
        TextView textView = new TextView(context);
        textView.setText(LocaleController.getString(R.string.NotfificationsFrequencyTitle));
        textView.setTextColor(scheduleDatePickerColors.textColor);
        textView.setTextSize(1, 20.0f);
        textView.setTypeface(AndroidUtilities.bold());
        frameLayout.addView(textView, LayoutHelper.createFrame(-2, -2.0f, 51, 0.0f, 12.0f, 0.0f, 0.0f));
        textView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public final boolean onTouch(View view, MotionEvent motionEvent) {
                boolean lambda$createSoundFrequencyPickerDialog$116;
                lambda$createSoundFrequencyPickerDialog$116 = AlertsCreator.lambda$createSoundFrequencyPickerDialog$116(view, motionEvent);
                return lambda$createSoundFrequencyPickerDialog$116;
            }
        });
        LinearLayout linearLayout2 = new LinearLayout(context);
        linearLayout2.setOrientation(0);
        linearLayout2.setWeightSum(1.0f);
        linearLayout.addView(linearLayout2, LayoutHelper.createLinear(-1, -2, 1.0f, 0, 0, 12, 0, 12));
        TextView textView2 = new TextView(context) {
            @Override
            public CharSequence getAccessibilityClassName() {
                return Button.class.getName();
            }
        };
        linearLayout2.addView(numberPicker, LayoutHelper.createLinear(0, 270, 0.4f));
        linearLayout2.addView(numberPicker3, LayoutHelper.createLinear(0, -2, 0.2f, 16));
        linearLayout2.addView(numberPicker2, LayoutHelper.createLinear(0, 270, 0.4f));
        textView2.setPadding(AndroidUtilities.dp(34.0f), 0, AndroidUtilities.dp(34.0f), 0);
        textView2.setGravity(17);
        textView2.setTextColor(scheduleDatePickerColors.buttonTextColor);
        textView2.setTextSize(1, 14.0f);
        textView2.setTypeface(AndroidUtilities.bold());
        textView2.setBackgroundDrawable(Theme.createSimpleSelectorRoundRectDrawable(AndroidUtilities.dp(8.0f), scheduleDatePickerColors.buttonBackgroundColor, scheduleDatePickerColors.buttonBackgroundPressedColor));
        textView2.setText(LocaleController.getString(R.string.AutoDeleteConfirm));
        linearLayout.addView(textView2, LayoutHelper.createLinear(-1, 48, 83, 16, 15, 16, 16));
        NumberPicker.OnValueChangeListener onValueChangeListener = new NumberPicker.OnValueChangeListener() {
            @Override
            public final void onValueChange(NumberPicker numberPicker4, int i3, int i4) {
                AlertsCreator.lambda$createSoundFrequencyPickerDialog$117(numberPicker4, i3, i4);
            }
        };
        numberPicker.setOnValueChangedListener(onValueChangeListener);
        numberPicker2.setOnValueChangedListener(onValueChangeListener);
        textView2.setOnClickListener(new View.OnClickListener() {
            @Override
            public final void onClick(View view) {
                AlertsCreator.lambda$createSoundFrequencyPickerDialog$118(NumberPicker.this, numberPicker2, soundFrequencyDelegate, builder, view);
            }
        });
        builder.setCustomView(linearLayout);
        BottomSheet show = builder.show();
        show.setBackgroundColor(scheduleDatePickerColors.backgroundColor);
        show.fixNavigationBar(scheduleDatePickerColors.backgroundColor);
        return builder;
    }

    public static BottomSheet.Builder createStatusUntilDatePickerDialog(Context context, long j, final StatusUntilDatePickerDelegate statusUntilDatePickerDelegate) {
        LinearLayout linearLayout;
        if (context == null) {
            return null;
        }
        ScheduleDatePickerColors scheduleDatePickerColors = new ScheduleDatePickerColors();
        final BottomSheet.Builder builder = new BottomSheet.Builder(context, false);
        builder.setApplyBottomPadding(false);
        final NumberPicker numberPicker = new NumberPicker(context);
        numberPicker.setTextColor(scheduleDatePickerColors.textColor);
        numberPicker.setTextOffset(AndroidUtilities.dp(10.0f));
        numberPicker.setItemCount(5);
        final NumberPicker numberPicker2 = new NumberPicker(context) {
            @Override
            protected CharSequence getContentDescription(int i) {
                return LocaleController.formatPluralString("Hours", i, new Object[0]);
            }
        };
        numberPicker2.setItemCount(5);
        numberPicker2.setTextColor(scheduleDatePickerColors.textColor);
        numberPicker2.setTextOffset(-AndroidUtilities.dp(10.0f));
        final NumberPicker numberPicker3 = new NumberPicker(context) {
            @Override
            protected CharSequence getContentDescription(int i) {
                return LocaleController.formatPluralString("Minutes", i, new Object[0]);
            }
        };
        numberPicker3.setItemCount(5);
        numberPicker3.setTextColor(scheduleDatePickerColors.textColor);
        numberPicker3.setTextOffset(-AndroidUtilities.dp(34.0f));
        LinearLayout linearLayout2 = new LinearLayout(context) {
            boolean ignoreLayout = false;

            @Override
            protected void onMeasure(int i, int i2) {
                this.ignoreLayout = true;
                android.graphics.Point point = AndroidUtilities.displaySize;
                int i3 = point.x > point.y ? 3 : 5;
                numberPicker.setItemCount(i3);
                numberPicker2.setItemCount(i3);
                numberPicker3.setItemCount(i3);
                numberPicker.getLayoutParams().height = AndroidUtilities.dp(42.0f) * i3;
                numberPicker2.getLayoutParams().height = AndroidUtilities.dp(42.0f) * i3;
                numberPicker3.getLayoutParams().height = AndroidUtilities.dp(42.0f) * i3;
                this.ignoreLayout = false;
                super.onMeasure(i, i2);
            }

            @Override
            public void requestLayout() {
                if (this.ignoreLayout) {
                    return;
                }
                super.requestLayout();
            }
        };
        linearLayout2.setOrientation(1);
        FrameLayout frameLayout = new FrameLayout(context);
        linearLayout2.addView(frameLayout, LayoutHelper.createLinear(-1, -2, 51, 22, 0, 0, 4));
        TextView textView = new TextView(context);
        textView.setText(LocaleController.getString(R.string.SetEmojiStatusUntilTitle));
        textView.setTextColor(scheduleDatePickerColors.textColor);
        textView.setTextSize(1, 20.0f);
        textView.setTypeface(AndroidUtilities.bold());
        frameLayout.addView(textView, LayoutHelper.createFrame(-2, -2.0f, 51, 0.0f, 12.0f, 0.0f, 0.0f));
        textView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public final boolean onTouch(View view, MotionEvent motionEvent) {
                boolean lambda$createStatusUntilDatePickerDialog$103;
                lambda$createStatusUntilDatePickerDialog$103 = AlertsCreator.lambda$createStatusUntilDatePickerDialog$103(view, motionEvent);
                return lambda$createStatusUntilDatePickerDialog$103;
            }
        });
        LinearLayout linearLayout3 = new LinearLayout(context);
        linearLayout3.setOrientation(0);
        linearLayout3.setWeightSum(1.0f);
        linearLayout2.addView(linearLayout3, LayoutHelper.createLinear(-1, -2, 1.0f, 0, 0, 12, 0, 12));
        final long currentTimeMillis = System.currentTimeMillis();
        final Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(currentTimeMillis);
        final int i = calendar.get(1);
        final int i2 = calendar.get(6);
        TextView textView2 = new TextView(context) {
            @Override
            public CharSequence getAccessibilityClassName() {
                return Button.class.getName();
            }
        };
        linearLayout3.addView(numberPicker, LayoutHelper.createLinear(0, 270, 0.5f));
        numberPicker.setMinValue(0);
        numberPicker.setMaxValue(365);
        numberPicker.setWrapSelectorWheel(false);
        numberPicker.setFormatter(new NumberPicker.Formatter() {
            @Override
            public final String format(int i3) {
                String lambda$createStatusUntilDatePickerDialog$104;
                lambda$createStatusUntilDatePickerDialog$104 = AlertsCreator.lambda$createStatusUntilDatePickerDialog$104(currentTimeMillis, calendar, i, i2, i3);
                return lambda$createStatusUntilDatePickerDialog$104;
            }
        });
        NumberPicker.OnValueChangeListener onValueChangeListener = new NumberPicker.OnValueChangeListener() {
            @Override
            public final void onValueChange(NumberPicker numberPicker4, int i3, int i4) {
                AlertsCreator.checkScheduleDate(null, null, 0, NumberPicker.this, numberPicker2, numberPicker3);
            }
        };
        numberPicker.setOnValueChangedListener(onValueChangeListener);
        numberPicker2.setMinValue(0);
        numberPicker2.setMaxValue(23);
        linearLayout3.addView(numberPicker2, LayoutHelper.createLinear(0, 270, 0.2f));
        numberPicker2.setFormatter(new NumberPicker.Formatter() {
            @Override
            public final String format(int i3) {
                String lambda$createStatusUntilDatePickerDialog$106;
                lambda$createStatusUntilDatePickerDialog$106 = AlertsCreator.lambda$createStatusUntilDatePickerDialog$106(i3);
                return lambda$createStatusUntilDatePickerDialog$106;
            }
        });
        numberPicker2.setOnValueChangedListener(onValueChangeListener);
        numberPicker3.setMinValue(0);
        numberPicker3.setMaxValue(59);
        numberPicker3.setValue(0);
        numberPicker3.setFormatter(new NumberPicker.Formatter() {
            @Override
            public final String format(int i3) {
                String lambda$createStatusUntilDatePickerDialog$107;
                lambda$createStatusUntilDatePickerDialog$107 = AlertsCreator.lambda$createStatusUntilDatePickerDialog$107(i3);
                return lambda$createStatusUntilDatePickerDialog$107;
            }
        });
        linearLayout3.addView(numberPicker3, LayoutHelper.createLinear(0, 270, 0.3f));
        numberPicker3.setOnValueChangedListener(onValueChangeListener);
        if (j <= 0 || j == 2147483646) {
            linearLayout = linearLayout2;
        } else {
            long j2 = 1000 * j;
            calendar.setTimeInMillis(System.currentTimeMillis());
            calendar.set(12, 0);
            calendar.set(13, 0);
            calendar.set(14, 0);
            calendar.set(11, 0);
            linearLayout = linearLayout2;
            int timeInMillis = (int) ((j2 - calendar.getTimeInMillis()) / 86400000);
            calendar.setTimeInMillis(j2);
            if (timeInMillis >= 0) {
                numberPicker3.setValue(calendar.get(12));
                numberPicker2.setValue(calendar.get(11));
                numberPicker.setValue(timeInMillis);
            }
        }
        checkScheduleDate(null, null, 0, numberPicker, numberPicker2, numberPicker3);
        textView2.setPadding(AndroidUtilities.dp(34.0f), 0, AndroidUtilities.dp(34.0f), 0);
        textView2.setGravity(17);
        textView2.setTextColor(scheduleDatePickerColors.buttonTextColor);
        textView2.setTextSize(1, 14.0f);
        textView2.setTypeface(AndroidUtilities.bold());
        textView2.setBackgroundDrawable(Theme.createSimpleSelectorRoundRectDrawable(AndroidUtilities.dp(8.0f), scheduleDatePickerColors.buttonBackgroundColor, scheduleDatePickerColors.buttonBackgroundPressedColor));
        textView2.setText(LocaleController.getString(R.string.SetEmojiStatusUntilButton));
        LinearLayout linearLayout4 = linearLayout;
        linearLayout4.addView(textView2, LayoutHelper.createLinear(-1, 48, 83, 16, 15, 16, 16));
        textView2.setOnClickListener(new View.OnClickListener() {
            @Override
            public final void onClick(View view) {
                AlertsCreator.lambda$createStatusUntilDatePickerDialog$108(NumberPicker.this, numberPicker2, numberPicker3, calendar, statusUntilDatePickerDelegate, builder, view);
            }
        });
        builder.setCustomView(linearLayout4);
        BottomSheet show = builder.show();
        show.setBackgroundColor(scheduleDatePickerColors.backgroundColor);
        show.fixNavigationBar(scheduleDatePickerColors.backgroundColor);
        return builder;
    }

    public static AlertDialog createSupportAlert(final BaseFragment baseFragment, Theme.ResourcesProvider resourcesProvider) {
        if (baseFragment == null || baseFragment.getParentActivity() == null) {
            return null;
        }
        LinkSpanDrawable.LinksTextView linksTextView = new LinkSpanDrawable.LinksTextView(baseFragment.getParentActivity(), baseFragment.getResourceProvider());
        SpannableString spannableString = new SpannableString(Html.fromHtml(LocaleController.getString(R.string.AskAQuestionInfo).replace("\n", "<br>")));
        for (URLSpan uRLSpan : (URLSpan[]) spannableString.getSpans(0, spannableString.length(), URLSpan.class)) {
            int spanStart = spannableString.getSpanStart(uRLSpan);
            int spanEnd = spannableString.getSpanEnd(uRLSpan);
            spannableString.removeSpan(uRLSpan);
            spannableString.setSpan(new URLSpanNoUnderline(uRLSpan.getURL()) {
                @Override
                public void onClick(View view) {
                    baseFragment.dismissCurrentDialog();
                    super.onClick(view);
                }
            }, spanStart, spanEnd, 0);
        }
        linksTextView.setText(spannableString);
        linksTextView.setTextSize(1, 16.0f);
        linksTextView.setLinkTextColor(Theme.getColor(Theme.key_dialogTextLink, resourcesProvider));
        linksTextView.setHighlightColor(Theme.getColor(Theme.key_dialogLinkSelection, resourcesProvider));
        linksTextView.setPadding(AndroidUtilities.dp(23.0f), 0, AndroidUtilities.dp(23.0f), 0);
        linksTextView.setMovementMethod(new AndroidUtilities.LinkMovementMethodMy());
        linksTextView.setTextColor(Theme.getColor(Theme.key_dialogTextBlack, resourcesProvider));
        AlertDialog.Builder builder = new AlertDialog.Builder(baseFragment.getParentActivity(), resourcesProvider);
        builder.setView(linksTextView);
        builder.setTitle(LocaleController.getString(R.string.AskAQuestion));
        builder.setPositiveButton(LocaleController.getString(R.string.AskButton), new AlertDialog.OnButtonClickListener() {
            @Override
            public final void onClick(AlertDialog alertDialog, int i) {
                AlertsCreator.performAskAQuestion(BaseFragment.this);
            }
        });
        builder.setNegativeButton(LocaleController.getString(R.string.Cancel), null);
        return builder.create();
    }

    public static org.telegram.ui.ActionBar.AlertDialog.Builder createTTLAlert(android.content.Context r5, final org.telegram.tgnet.TLRPC.EncryptedChat r6, org.telegram.ui.ActionBar.Theme.ResourcesProvider r7) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.AlertsCreator.createTTLAlert(android.content.Context, org.telegram.tgnet.TLRPC$EncryptedChat, org.telegram.ui.ActionBar.Theme$ResourcesProvider):org.telegram.ui.ActionBar.AlertDialog$Builder");
    }

    public static void createThemeCreateDialog(final BaseFragment baseFragment, int i, final Theme.ThemeInfo themeInfo, final Theme.ThemeAccent themeAccent) {
        if (baseFragment == null || baseFragment.getParentActivity() == null) {
            return;
        }
        Activity parentActivity = baseFragment.getParentActivity();
        final EditTextBoldCursor editTextBoldCursor = new EditTextBoldCursor(parentActivity);
        editTextBoldCursor.setBackground(null);
        editTextBoldCursor.setLineColors(Theme.getColor(Theme.key_dialogInputField), Theme.getColor(Theme.key_dialogInputFieldActivated), Theme.getColor(Theme.key_text_RedBold));
        AlertDialog.Builder builder = new AlertDialog.Builder(parentActivity);
        builder.setTitle(LocaleController.getString(R.string.NewTheme));
        builder.setNegativeButton(LocaleController.getString(R.string.Cancel), null);
        builder.setPositiveButton(LocaleController.getString(R.string.Create), new AlertDialog.OnButtonClickListener() {
            @Override
            public final void onClick(AlertDialog alertDialog, int i2) {
                AlertsCreator.lambda$createThemeCreateDialog$165(alertDialog, i2);
            }
        });
        LinearLayout linearLayout = new LinearLayout(parentActivity);
        linearLayout.setOrientation(1);
        builder.setView(linearLayout);
        TextView textView = new TextView(parentActivity);
        textView.setText(i != 0 ? AndroidUtilities.replaceTags(LocaleController.getString(R.string.EnterThemeNameEdit)) : LocaleController.getString(R.string.EnterThemeName));
        textView.setTextSize(1, 16.0f);
        textView.setPadding(AndroidUtilities.dp(23.0f), AndroidUtilities.dp(12.0f), AndroidUtilities.dp(23.0f), AndroidUtilities.dp(6.0f));
        int i2 = Theme.key_dialogTextBlack;
        textView.setTextColor(Theme.getColor(i2));
        linearLayout.addView(textView, LayoutHelper.createLinear(-1, -2));
        editTextBoldCursor.setTextSize(1, 16.0f);
        editTextBoldCursor.setTextColor(Theme.getColor(i2));
        editTextBoldCursor.setMaxLines(1);
        editTextBoldCursor.setLines(1);
        editTextBoldCursor.setInputType(16385);
        editTextBoldCursor.setGravity(51);
        editTextBoldCursor.setSingleLine(true);
        editTextBoldCursor.setImeOptions(6);
        editTextBoldCursor.setCursorColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
        editTextBoldCursor.setCursorSize(AndroidUtilities.dp(20.0f));
        editTextBoldCursor.setCursorWidth(1.5f);
        editTextBoldCursor.setPadding(0, AndroidUtilities.dp(4.0f), 0, 0);
        linearLayout.addView(editTextBoldCursor, LayoutHelper.createLinear(-1, 36, 51, 24, 6, 24, 0));
        editTextBoldCursor.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public final boolean onEditorAction(TextView textView2, int i3, KeyEvent keyEvent) {
                boolean lambda$createThemeCreateDialog$166;
                lambda$createThemeCreateDialog$166 = AlertsCreator.lambda$createThemeCreateDialog$166(textView2, i3, keyEvent);
                return lambda$createThemeCreateDialog$166;
            }
        });
        editTextBoldCursor.setText(generateThemeName(themeAccent));
        editTextBoldCursor.setSelection(editTextBoldCursor.length());
        final AlertDialog create = builder.create();
        create.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public final void onShow(DialogInterface dialogInterface) {
                AlertsCreator.lambda$createThemeCreateDialog$168(EditTextBoldCursor.this, dialogInterface);
            }
        });
        baseFragment.showDialog(create);
        editTextBoldCursor.requestFocus();
        create.getButton(-1).setOnClickListener(new View.OnClickListener() {
            @Override
            public final void onClick(View view) {
                AlertsCreator.lambda$createThemeCreateDialog$171(BaseFragment.this, editTextBoldCursor, themeAccent, themeInfo, create, view);
            }
        });
    }

    public static BottomSheet createTimePickerDialog(Context context, String str, final int i, final int i2, final int i3, final Utilities.Callback callback) {
        if (context == null) {
            return null;
        }
        ScheduleDatePickerColors scheduleDatePickerColors = new ScheduleDatePickerColors();
        BottomSheet.Builder builder = new BottomSheet.Builder(context, false, null);
        builder.setApplyBottomPadding(false);
        final NumberPicker numberPicker = new NumberPicker(context) {
            @Override
            protected CharSequence getContentDescription(int i4) {
                return LocaleController.formatPluralString("Hours", i4, new Object[0]);
            }
        };
        final LinearLayout linearLayout = new LinearLayout(context) {
            private Text ampmText;
            private boolean isAM;
            private final Text separatorText = new Text(":", 18.0f);

            @Override
            protected void dispatchDraw(Canvas canvas) {
                int i4 = Theme.key_windowBackgroundWhiteBlackText;
                this.separatorText.draw(canvas, (getWidth() - this.separatorText.getCurrentWidth()) / 2.0f, getHeight() / 2.0f, Theme.getColor(i4), 1.0f);
                if (!LocaleController.is24HourFormat) {
                    boolean z = numberPicker.getValue() % 24 < 12;
                    if (this.isAM != z || this.ampmText == null) {
                        this.isAM = z;
                        this.ampmText = new Text(z ? "AM" : "PM", 18.0f);
                    }
                    this.ampmText.draw(canvas, (getWidth() / 2.0f) + AndroidUtilities.dp(43.0f), (getHeight() / 2.0f) + AndroidUtilities.dp(1.0f), Theme.getColor(i4), 1.0f);
                }
                super.dispatchDraw(canvas);
            }
        };
        linearLayout.setOrientation(0);
        linearLayout.setWeightSum(1.0f);
        numberPicker.setAllItemsCount(24);
        numberPicker.setItemCount(5);
        numberPicker.setTextColor(scheduleDatePickerColors.textColor);
        numberPicker.setGravity(5);
        numberPicker.setTextOffset(-AndroidUtilities.dp(12.0f));
        final NumberPicker numberPicker2 = new NumberPicker(context) {
            @Override
            protected CharSequence getContentDescription(int i4) {
                return LocaleController.formatPluralString("Minutes", i4, new Object[0]);
            }
        };
        numberPicker2.setWrapSelectorWheel(true);
        numberPicker2.setAllItemsCount(60);
        numberPicker2.setItemCount(5);
        numberPicker2.setTextColor(scheduleDatePickerColors.textColor);
        numberPicker2.setGravity(3);
        numberPicker2.setTextOffset(AndroidUtilities.dp(12.0f));
        final Utilities.Callback callback2 = new Utilities.Callback() {
            @Override
            public final void run(Object obj) {
                AlertsCreator.lambda$createTimePickerDialog$63(i2, i3, numberPicker, numberPicker2, i, linearLayout, (Boolean) obj);
            }
        };
        linearLayout.addView(numberPicker, LayoutHelper.createLinear(0, 270, 0.5f));
        numberPicker.setFormatter(new NumberPicker.Formatter() {
            @Override
            public final String format(int i4) {
                String lambda$createTimePickerDialog$64;
                lambda$createTimePickerDialog$64 = AlertsCreator.lambda$createTimePickerDialog$64(i4);
                return lambda$createTimePickerDialog$64;
            }
        });
        numberPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public final void onValueChange(NumberPicker numberPicker3, int i4, int i5) {
                AlertsCreator.lambda$createTimePickerDialog$65(Utilities.Callback.this, numberPicker3, i4, i5);
            }
        });
        linearLayout.addView(numberPicker2, LayoutHelper.createLinear(0, 270, 0.5f));
        numberPicker2.setFormatter(new NumberPicker.Formatter() {
            @Override
            public final String format(int i4) {
                String lambda$createTimePickerDialog$66;
                lambda$createTimePickerDialog$66 = AlertsCreator.lambda$createTimePickerDialog$66(i4);
                return lambda$createTimePickerDialog$66;
            }
        });
        numberPicker2.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public final void onValueChange(NumberPicker numberPicker3, int i4, int i5) {
                AlertsCreator.lambda$createTimePickerDialog$67(Utilities.Callback.this, numberPicker3, i4, i5);
            }
        });
        callback2.run(Boolean.FALSE);
        LinearLayout linearLayout2 = new LinearLayout(context) {
            boolean ignoreLayout = false;

            @Override
            protected void onMeasure(int i4, int i5) {
                this.ignoreLayout = true;
                android.graphics.Point point = AndroidUtilities.displaySize;
                int i6 = point.x > point.y ? 3 : 5;
                numberPicker.setItemCount(i6);
                numberPicker2.setItemCount(i6);
                numberPicker.getLayoutParams().height = AndroidUtilities.dp(42.0f) * i6;
                numberPicker2.getLayoutParams().height = AndroidUtilities.dp(42.0f) * i6;
                this.ignoreLayout = false;
                super.onMeasure(i4, i5);
            }

            @Override
            public void requestLayout() {
                if (this.ignoreLayout) {
                    return;
                }
                super.requestLayout();
            }
        };
        linearLayout2.setOrientation(1);
        FrameLayout frameLayout = new FrameLayout(context);
        TextView textView = new TextView(context);
        textView.setText(str);
        textView.setTextColor(scheduleDatePickerColors.textColor);
        textView.setTextSize(1, 20.0f);
        textView.setTypeface(AndroidUtilities.bold());
        frameLayout.addView(textView, LayoutHelper.createFrame(-2, -2.0f, 51, 0.0f, 12.0f, 0.0f, 0.0f));
        textView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public final boolean onTouch(View view, MotionEvent motionEvent) {
                boolean lambda$createTimePickerDialog$68;
                lambda$createTimePickerDialog$68 = AlertsCreator.lambda$createTimePickerDialog$68(view, motionEvent);
                return lambda$createTimePickerDialog$68;
            }
        });
        linearLayout2.addView(frameLayout, LayoutHelper.createLinear(-1, -2, 51, 22, 0, 0, 4));
        linearLayout2.addView(linearLayout, LayoutHelper.createLinear(-1, -2, 1.0f, 0, 0, 12, 0, 12));
        ButtonWithCounterView buttonWithCounterView = new ButtonWithCounterView(context, null);
        buttonWithCounterView.setText(LocaleController.getString(R.string.Select), false);
        buttonWithCounterView.setOnClickListener(new View.OnClickListener() {
            @Override
            public final void onClick(View view) {
                AlertsCreator.lambda$createTimePickerDialog$69(r1, view);
            }
        });
        linearLayout2.addView(buttonWithCounterView, LayoutHelper.createLinear(-1, 48, 0, 16, 12, 16, 12));
        builder.setCustomView(linearLayout2);
        BottomSheet show = builder.show();
        show.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public final void onDismiss(DialogInterface dialogInterface) {
                AlertsCreator.lambda$createTimePickerDialog$70(Utilities.Callback.this, numberPicker, numberPicker2, dialogInterface);
            }
        });
        show.setBackgroundColor(scheduleDatePickerColors.backgroundColor);
        show.fixNavigationBar(scheduleDatePickerColors.backgroundColor);
        BottomSheet create = builder.create();
        final BottomSheet[] bottomSheetArr = {create};
        return create;
    }

    public static Dialog createVibrationSelectDialog(Activity activity, long j, long j2, String str, Runnable runnable) {
        return createVibrationSelectDialog(activity, j, j2, str, runnable, null);
    }

    public static Dialog createVibrationSelectDialog(Activity activity, final long j, final long j2, final String str, final Runnable runnable, Theme.ResourcesProvider resourcesProvider) {
        String[] strArr;
        Activity activity2 = activity;
        final int[] iArr = new int[1];
        int i = 0;
        int i2 = MessagesController.getNotificationsSettings(UserConfig.selectedAccount).getInt(str, 0);
        if (j != 0) {
            iArr[0] = i2;
            if (i2 == 3) {
                iArr[0] = 2;
            } else if (i2 == 2) {
                iArr[0] = 3;
            }
            strArr = new String[]{LocaleController.getString(R.string.VibrationDefault), LocaleController.getString(R.string.Short), LocaleController.getString(R.string.Long), LocaleController.getString(R.string.VibrationDisabled)};
        } else {
            iArr[0] = i2;
            if (i2 == 0) {
                iArr[0] = 1;
            } else if (i2 == 1) {
                iArr[0] = 2;
            } else if (i2 == 2) {
                iArr[0] = 0;
            }
            strArr = new String[]{LocaleController.getString(R.string.VibrationDisabled), LocaleController.getString(R.string.VibrationDefault), LocaleController.getString(R.string.Short), LocaleController.getString(R.string.Long), LocaleController.getString(R.string.OnlyIfSilent)};
        }
        String[] strArr2 = strArr;
        LinearLayout linearLayout = new LinearLayout(activity2);
        linearLayout.setOrientation(1);
        final AlertDialog.Builder builder = new AlertDialog.Builder(activity2, resourcesProvider);
        int i3 = 0;
        while (i3 < strArr2.length) {
            RadioColorCell radioColorCell = new RadioColorCell(activity2, resourcesProvider);
            radioColorCell.setPadding(AndroidUtilities.dp(4.0f), i, AndroidUtilities.dp(4.0f), i);
            radioColorCell.setTag(Integer.valueOf(i3));
            radioColorCell.setCheckColor(Theme.getColor(Theme.key_radioBackground, resourcesProvider), Theme.getColor(Theme.key_dialogRadioBackgroundChecked, resourcesProvider));
            radioColorCell.setTextAndValue(strArr2[i3], iArr[i] == i3);
            linearLayout.addView(radioColorCell);
            radioColorCell.setOnClickListener(new View.OnClickListener() {
                @Override
                public final void onClick(View view) {
                    AlertsCreator.lambda$createVibrationSelectDialog$138(iArr, j, str, j2, builder, runnable, view);
                }
            });
            i3++;
            linearLayout = linearLayout;
            i = 0;
            activity2 = activity;
        }
        builder.setTitle(LocaleController.getString(R.string.Vibrate));
        builder.setView(linearLayout);
        builder.setPositiveButton(LocaleController.getString(R.string.Cancel), null);
        return builder.create();
    }

    public static Dialog createVibrationSelectDialog(Activity activity, long j, long j2, boolean z, boolean z2, Runnable runnable, Theme.ResourcesProvider resourcesProvider) {
        String str;
        if (j != 0) {
            str = "vibrate_" + j;
        } else {
            str = z ? "vibrate_group" : "vibrate_messages";
        }
        return createVibrationSelectDialog(activity, j, j2, str, runnable, resourcesProvider);
    }

    public static Dialog createWebViewPermissionsRequestDialog(final Context context, Theme.ResourcesProvider resourcesProvider, String[] strArr, int i, String str, String str2, final Consumer consumer) {
        final boolean z;
        int checkSelfPermission;
        boolean shouldShowRequestPermissionRationale;
        if (strArr != null && (context instanceof Activity) && Build.VERSION.SDK_INT >= 23) {
            Activity activity = (Activity) context;
            for (String str3 : strArr) {
                checkSelfPermission = activity.checkSelfPermission(str3);
                if (checkSelfPermission != 0) {
                    shouldShowRequestPermissionRationale = activity.shouldShowRequestPermissionRationale(str3);
                    if (shouldShowRequestPermissionRationale) {
                        z = true;
                        break;
                    }
                }
            }
        }
        z = false;
        final AtomicBoolean atomicBoolean = new AtomicBoolean();
        AlertDialog.Builder topAnimation = new AlertDialog.Builder(context, resourcesProvider).setTopAnimation(i, 72, false, Theme.getColor(Theme.key_dialogTopBackground));
        if (z) {
            str = str2;
        }
        return topAnimation.setMessage(AndroidUtilities.replaceTags(str)).setPositiveButton(LocaleController.getString(z ? R.string.PermissionOpenSettings : R.string.BotWebViewRequestAllow), new AlertDialog.OnButtonClickListener() {
            @Override
            public final void onClick(AlertDialog alertDialog, int i2) {
                AlertsCreator.lambda$createWebViewPermissionsRequestDialog$3(z, context, atomicBoolean, consumer, alertDialog, i2);
            }
        }).setNegativeButton(LocaleController.getString(R.string.BotWebViewRequestDontAllow), new AlertDialog.OnButtonClickListener() {
            @Override
            public final void onClick(AlertDialog alertDialog, int i2) {
                AlertsCreator.lambda$createWebViewPermissionsRequestDialog$4(atomicBoolean, consumer, alertDialog, i2);
            }
        }).setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public final void onDismiss(DialogInterface dialogInterface) {
                AlertsCreator.lambda$createWebViewPermissionsRequestDialog$5(atomicBoolean, consumer, dialogInterface);
            }
        }).create();
    }

    private static String generateThemeName(Theme.ThemeAccent themeAccent) {
        int i;
        List asList = Arrays.asList("Ancient", "Antique", "Autumn", "Baby", "Barely", "Baroque", "Blazing", "Blushing", "Bohemian", "Bubbly", "Burning", "Buttered", "Classic", "Clear", "Cool", "Cosmic", "Cotton", "Cozy", "Crystal", "Dark", "Daring", "Darling", "Dawn", "Dazzling", "Deep", "Deepest", "Delicate", "Delightful", "Divine", "Double", "Downtown", "Dreamy", "Dusky", "Dusty", "Electric", "Enchanted", "Endless", "Evening", "Fantastic", "Flirty", "Forever", "Frigid", "Frosty", "Frozen", "Gentle", "Heavenly", "Hyper", "Icy", "Infinite", "Innocent", "Instant", "Luscious", "Lunar", "Lustrous", "Magic", "Majestic", "Mambo", "Midnight", "Millenium", "Morning", "Mystic", "Natural", "Neon", "Night", "Opaque", "Paradise", "Perfect", "Perky", "Polished", "Powerful", "Rich", "Royal", "Sheer", "Simply", "Sizzling", "Solar", "Sparkling", "Splendid", "Spicy", "Spring", "Stellar", "Sugared", "Summer", "Sunny", "Super", "Sweet", "Tender", "Tenacious", "Tidal", "Toasted", "Totally", "Tranquil", "Tropical", "True", "Twilight", "Twinkling", "Ultimate", "Ultra", "Velvety", "Vibrant", "Vintage", "Virtual", "Warm", "Warmest", "Whipped", "Wild", "Winsome");
        List asList2 = Arrays.asList("Ambrosia", "Attack", "Avalanche", "Blast", "Bliss", "Blossom", "Blush", "Burst", "Butter", "Candy", "Carnival", "Charm", "Chiffon", "Cloud", "Comet", "Delight", "Dream", "Dust", "Fantasy", "Flame", "Flash", "Fire", "Freeze", "Frost", "Glade", "Glaze", "Gleam", "Glimmer", "Glitter", "Glow", "Grande", "Haze", "Highlight", "Ice", "Illusion", "Intrigue", "Jewel", "Jubilee", "Kiss", "Lights", "Lollypop", "Love", "Luster", "Madness", "Matte", "Mirage", "Mist", "Moon", "Muse", "Myth", "Nectar", "Nova", "Parfait", "Passion", "Pop", "Rain", "Reflection", "Rhapsody", "Romance", "Satin", "Sensation", "Silk", "Shine", "Shadow", "Shimmer", "Sky", "Spice", "Star", "Sugar", "Sunrise", "Sunset", "Sun", "Twist", "Unbound", "Velvet", "Vibrant", "Waters", "Wine", "Wink", "Wonder", "Zone");
        HashMap hashMap = new HashMap();
        hashMap.put(9306112, "Berry");
        hashMap.put(14598550, "Brandy");
        hashMap.put(8391495, "Cherry");
        hashMap.put(16744272, "Coral");
        hashMap.put(14372985, "Cranberry");
        hashMap.put(14423100, "Crimson");
        hashMap.put(14725375, "Mauve");
        hashMap.put(16761035, "Pink");
        hashMap.put(16711680, "Red");
        hashMap.put(16711807, "Rose");
        hashMap.put(8406555, "Russet");
        hashMap.put(16720896, "Scarlet");
        hashMap.put(15856113, "Seashell");
        hashMap.put(16724889, "Strawberry");
        hashMap.put(16760576, "Amber");
        hashMap.put(15438707, "Apricot");
        hashMap.put(16508850, "Banana");
        hashMap.put(10601738, "Citrus");
        hashMap.put(11560192, "Ginger");
        hashMap.put(16766720, "Gold");
        hashMap.put(16640272, "Lemon");
        hashMap.put(16753920, "Orange");
        hashMap.put(16770484, "Peach");
        hashMap.put(16739155, "Persimmon");
        hashMap.put(14996514, "Sunflower");
        hashMap.put(15893760, "Tangerine");
        hashMap.put(16763004, "Topaz");
        hashMap.put(16776960, "Yellow");
        hashMap.put(3688720, "Clover");
        hashMap.put(8628829, "Cucumber");
        hashMap.put(5294200, "Emerald");
        hashMap.put(11907932, "Olive");
        hashMap.put(65280, "Green");
        hashMap.put(43115, "Jade");
        hashMap.put(2730887, "Jungle");
        hashMap.put(12582656, "Lime");
        hashMap.put(776785, "Malachite");
        hashMap.put(10026904, "Mint");
        hashMap.put(11394989, "Moss");
        hashMap.put(3234721, "Azure");
        hashMap.put(255, "Blue");
        hashMap.put(18347, "Cobalt");
        hashMap.put(5204422, "Indigo");
        hashMap.put(96647, "Lagoon");
        hashMap.put(7461346, "Aquamarine");
        hashMap.put(1182351, "Ultramarine");
        hashMap.put(128, "Navy");
        hashMap.put(3101086, "Sapphire");
        hashMap.put(7788522, "Sky");
        hashMap.put(32896, "Teal");
        hashMap.put(4251856, "Turquoise");
        hashMap.put(10053324, "Amethyst");
        hashMap.put(5046581, "Blackberry");
        hashMap.put(6373457, "Eggplant");
        hashMap.put(13148872, "Lilac");
        hashMap.put(11894492, "Lavender");
        hashMap.put(13421823, "Periwinkle");
        hashMap.put(8663417, "Plum");
        hashMap.put(6684825, "Purple");
        hashMap.put(14204888, "Thistle");
        hashMap.put(14315734, "Orchid");
        hashMap.put(2361920, "Violet");
        hashMap.put(4137225, "Bronze");
        hashMap.put(3604994, "Chocolate");
        hashMap.put(8077056, "Cinnamon");
        hashMap.put(3153694, "Cocoa");
        hashMap.put(7365973, "Coffee");
        hashMap.put(7956873, "Rum");
        hashMap.put(5113350, "Mahogany");
        hashMap.put(7875865, "Mocha");
        hashMap.put(12759680, "Sand");
        hashMap.put(8924439, "Sienna");
        hashMap.put(7864585, "Maple");
        hashMap.put(15787660, "Khaki");
        hashMap.put(12088115, "Copper");
        hashMap.put(12144200, "Chestnut");
        hashMap.put(15653316, "Almond");
        hashMap.put(16776656, "Cream");
        hashMap.put(12186367, "Diamond");
        hashMap.put(11109127, "Honey");
        hashMap.put(16777200, "Ivory");
        hashMap.put(15392968, "Pearl");
        hashMap.put(15725299, "Porcelain");
        hashMap.put(13745832, "Vanilla");
        hashMap.put(16777215, "White");
        hashMap.put(8421504, "Gray");
        hashMap.put(0, "Black");
        hashMap.put(15266260, "Chrome");
        hashMap.put(3556687, "Charcoal");
        hashMap.put(789277, "Ebony");
        hashMap.put(12632256, "Silver");
        hashMap.put(16119285, "Smoke");
        hashMap.put(2499381, "Steel");
        hashMap.put(5220413, "Apple");
        hashMap.put(8434628, "Glacier");
        hashMap.put(16693933, "Melon");
        hashMap.put(12929932, "Mulberry");
        hashMap.put(11126466, "Opal");
        hashMap.put(5547512, "Blue");
        Theme.ThemeAccent accent = themeAccent == null ? Theme.getCurrentTheme().getAccent(false) : themeAccent;
        if (accent == null || (i = accent.accentColor) == 0) {
            i = AndroidUtilities.calcDrawableColor(Theme.getCachedWallpaper())[0];
        }
        int red = Color.red(i);
        int green = Color.green(i);
        int blue = Color.blue(i);
        String str = null;
        int i2 = Integer.MAX_VALUE;
        for (Map.Entry entry : hashMap.entrySet()) {
            Integer num = (Integer) entry.getKey();
            int red2 = Color.red(num.intValue());
            int i3 = (red + red2) / 2;
            int i4 = red - red2;
            int green2 = green - Color.green(num.intValue());
            int blue2 = blue - Color.blue(num.intValue());
            int i5 = ((((767 - i3) * blue2) * blue2) >> 8) + ((((i3 + 512) * i4) * i4) >> 8) + (green2 * 4 * green2);
            if (i5 < i2) {
                str = (String) entry.getValue();
                i2 = i5;
            }
        }
        if (Utilities.random.nextInt() % 2 == 0) {
            return ((String) asList.get(Utilities.random.nextInt(asList.size()))) + " " + str;
        }
        return str + " " + ((String) asList2.get(Utilities.random.nextInt(asList2.size())));
    }

    private static String getFloodWaitString(String str) {
        int intValue = Utilities.parseInt((CharSequence) str).intValue();
        return LocaleController.formatString("FloodWaitTime", R.string.FloodWaitTime, intValue < 60 ? LocaleController.formatPluralString("Seconds", intValue, new Object[0]) : LocaleController.formatPluralString("Minutes", intValue / 60, new Object[0]));
    }

    public static void lambda$checkRestrictedInviteUsers$55(int i, TLRPC.Chat chat, ArrayList arrayList, ArrayList arrayList2, ArrayList arrayList3) {
        BaseFragment lastFragment;
        if (!LaunchActivity.isActive || (lastFragment = LaunchActivity.getLastFragment()) == null || lastFragment.getParentActivity() == null) {
            return;
        }
        LimitReachedBottomSheet limitReachedBottomSheet = new LimitReachedBottomSheet(lastFragment, lastFragment.getParentActivity(), 11, i, null);
        limitReachedBottomSheet.setRestrictedUsers(chat, arrayList, arrayList2, arrayList3);
        limitReachedBottomSheet.show();
    }

    public static void lambda$createAccountSelectDialog$153(AlertDialog[] alertDialogArr, Runnable runnable, AccountSelectDelegate accountSelectDelegate, View view) {
        AlertDialog alertDialog = alertDialogArr[0];
        if (alertDialog != null) {
            alertDialog.setOnDismissListener(null);
        }
        runnable.run();
        accountSelectDelegate.didSelectAccount(((AccountSelectCell) view).getAccountNumber());
    }

    public static void lambda$createApkRestrictedDialog$6(Context context, AlertDialog alertDialog, int i) {
        try {
            context.startActivity(new Intent("android.settings.MANAGE_UNKNOWN_APP_SOURCES", Uri.parse("package:" + context.getPackageName())));
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    public static String lambda$createAutoDeleteDatePickerDialog$109(int[] iArr, int i) {
        int i2 = iArr[i];
        return i2 == 0 ? LocaleController.getString(R.string.AutoDeleteNever) : i2 < 10080 ? LocaleController.formatPluralString("Days", i2 / 1440, new Object[0]) : i2 < 44640 ? LocaleController.formatPluralString("Weeks", i2 / 10080, new Object[0]) : i2 < 525600 ? LocaleController.formatPluralString("Months", i2 / 44640, new Object[0]) : LocaleController.formatPluralString("Years", i2 / 525600, new Object[0]);
    }

    public static boolean lambda$createAutoDeleteDatePickerDialog$110(View view, MotionEvent motionEvent) {
        return true;
    }

    public static void lambda$createAutoDeleteDatePickerDialog$111(AnimatedTextView animatedTextView, NumberPicker numberPicker, int i, int i2) {
        try {
            animatedTextView.setText(LocaleController.getString(i2 == 0 ? R.string.DisableAutoDeleteTimer : R.string.SetAutoDeleteTimer));
        } catch (Exception unused) {
        }
    }

    public static void lambda$createAutoDeleteDatePickerDialog$112(int[] iArr, NumberPicker numberPicker, ScheduleDatePickerDelegate scheduleDatePickerDelegate, BottomSheet.Builder builder, View view) {
        scheduleDatePickerDelegate.didSelectDate(true, iArr[numberPicker.getValue()]);
        builder.getDismissRunnable().run();
    }

    public static void lambda$createBackgroundActivityDialog$1(Context context, AlertDialog alertDialog, int i) {
        try {
            Intent intent = new Intent("android.settings.APPLICATION_DETAILS_SETTINGS");
            intent.setData(Uri.parse("package:" + ApplicationLoader.applicationContext.getPackageName()));
            context.startActivity(intent);
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    public static void lambda$createBackgroundLocationPermissionDialog$141(Activity activity, AlertDialog alertDialog, int i) {
        int checkSelfPermission;
        checkSelfPermission = activity.checkSelfPermission("android.permission.ACCESS_BACKGROUND_LOCATION");
        if (checkSelfPermission != 0) {
            activity.requestPermissions(new String[]{"android.permission.ACCESS_BACKGROUND_LOCATION"}, 30);
        }
    }

    public static void lambda$createBirthdayPickerDialog$100(int i, LinkSpanDrawable.LinksTextView linksTextView) {
        final ArrayList<TLRPC.PrivacyRule> privacyRules = ContactsController.getInstance(i).getPrivacyRules(11);
        String string = LocaleController.getString(R.string.EditProfileBirthdayInfoContacts);
        if (privacyRules != null && !privacyRules.isEmpty()) {
            int i2 = 0;
            while (true) {
                if (i2 >= privacyRules.size()) {
                    break;
                }
                if (privacyRules.get(i2) instanceof TLRPC.TL_privacyValueAllowContacts) {
                    string = LocaleController.getString(R.string.EditProfileBirthdayInfoContacts);
                    break;
                }
                if ((privacyRules.get(i2) instanceof TLRPC.TL_privacyValueAllowAll) || (privacyRules.get(i2) instanceof TLRPC.TL_privacyValueDisallowAll)) {
                    string = LocaleController.getString(R.string.EditProfileBirthdayInfo);
                }
                i2++;
            }
        }
        linksTextView.setText(AndroidUtilities.replaceArrows(AndroidUtilities.replaceSingleTag(string, new Runnable() {
            @Override
            public final void run() {
                AlertsCreator.lambda$createBirthdayPickerDialog$99(privacyRules);
            }
        }), true, AndroidUtilities.dp(2.6666667f), AndroidUtilities.dp(0.66f)));
    }

    public static void lambda$createBirthdayPickerDialog$102(NumberPicker numberPicker, NumberPicker numberPicker2, NumberPicker numberPicker3, int i, BottomSheet.Builder builder, Utilities.Callback callback, View view) {
        TL_account.TL_birthday tL_birthday = new TL_account.TL_birthday();
        tL_birthday.day = numberPicker.getValue();
        tL_birthday.month = numberPicker2.getValue() + 1;
        if (numberPicker3.getValue() != i) {
            tL_birthday.flags |= 1;
            tL_birthday.year = numberPicker3.getValue();
        }
        builder.getDismissRunnable().run();
        callback.run(tL_birthday);
    }

    public static boolean lambda$createBirthdayPickerDialog$93(View view, MotionEvent motionEvent) {
        return true;
    }

    public static void lambda$createBirthdayPickerDialog$94(NumberPicker numberPicker, int i, NumberPicker numberPicker2, NumberPicker numberPicker3, int i2, int i3, int i4) {
        try {
            if (numberPicker.getValue() == i) {
                numberPicker2.setMinValue(1);
                numberPicker2.setMaxValue(YearMonth.of(2024, numberPicker3.getValue() + 1).lengthOfMonth());
            } else {
                if (numberPicker.getValue() == i2) {
                    numberPicker3.setMinValue(0);
                    numberPicker3.setMaxValue(i3);
                    int value = numberPicker3.getValue();
                    numberPicker2.setMinValue(1);
                    if (value == i3) {
                        numberPicker2.setMaxValue(i4);
                        return;
                    }
                    try {
                        numberPicker2.setMaxValue(YearMonth.of(numberPicker.getValue(), numberPicker3.getValue() + 1).lengthOfMonth());
                        return;
                    } catch (Exception e) {
                        FileLog.e(e);
                        numberPicker2.setMaxValue(31);
                        return;
                    }
                }
                numberPicker2.setMinValue(1);
                numberPicker2.setMaxValue(YearMonth.of(numberPicker.getValue(), numberPicker3.getValue() + 1).lengthOfMonth());
            }
        } catch (Exception e2) {
            FileLog.e(e2);
            numberPicker2.setMaxValue(31);
        }
        numberPicker3.setMinValue(0);
        numberPicker3.setMaxValue(11);
    }

    public static String lambda$createBirthdayPickerDialog$95(int i) {
        return "" + i;
    }

    public static void lambda$createBirthdayPickerDialog$96(Runnable runnable, NumberPicker numberPicker, int i) {
        if (i == 0) {
            runnable.run();
        }
    }

    public static String lambda$createBirthdayPickerDialog$97(int i) {
        int i2;
        switch (i) {
            case 0:
                i2 = R.string.January;
                break;
            case 1:
                i2 = R.string.February;
                break;
            case 2:
                i2 = R.string.March;
                break;
            case 3:
                i2 = R.string.April;
                break;
            case 4:
                i2 = R.string.May;
                break;
            case 5:
                i2 = R.string.June;
                break;
            case 6:
                i2 = R.string.July;
                break;
            case 7:
                i2 = R.string.August;
                break;
            case 8:
                i2 = R.string.September;
                break;
            case 9:
                i2 = R.string.October;
                break;
            case 10:
                i2 = R.string.November;
                break;
            default:
                i2 = R.string.December;
                break;
        }
        return LocaleController.getString(i2);
    }

    public static String lambda$createBirthdayPickerDialog$98(int i, int i2) {
        return i2 == i ? "—" : String.format("%02d", Integer.valueOf(i2));
    }

    public static void lambda$createBirthdayPickerDialog$99(ArrayList arrayList) {
        BaseFragment lastFragment;
        if (arrayList == null || (lastFragment = LaunchActivity.getLastFragment()) == null) {
            return;
        }
        BaseFragment.BottomSheetParams bottomSheetParams = new BaseFragment.BottomSheetParams();
        bottomSheetParams.transitionFromLeft = true;
        bottomSheetParams.allowNestedScroll = false;
        lastFragment.showAsSheet(new PrivacyControlActivity(11), bottomSheetParams);
    }

    public static void lambda$createBlockDialogAlert$56(boolean[] zArr, int i, View view) {
        boolean z = !zArr[i];
        zArr[i] = z;
        ((CheckBoxCell) view).setChecked(z, true);
    }

    public static void lambda$createBlockDialogAlert$57(BlockDialogCallback blockDialogCallback, boolean[] zArr, AlertDialog alertDialog, int i) {
        blockDialogCallback.run(zArr[0], zArr[1]);
    }

    public static void lambda$createBotLaunchAlert$26(TLRPC.User user, BaseFragment baseFragment, AlertDialog.Builder builder, View view) {
        Bundle bundle = new Bundle();
        bundle.putLong("user_id", user.id);
        if (baseFragment.getMessagesController().checkCanOpenChat(bundle, baseFragment)) {
            baseFragment.presentFragment(new ProfileActivity(bundle));
        }
        builder.getDismissRunnable().run();
    }

    public static void lambda$createBotLaunchAlert$27(Runnable runnable, AlertDialog alertDialog, int i) {
        if (runnable != null) {
            runnable.run();
        }
    }

    public static void lambda$createBotLaunchAlert$28(Runnable runnable, DialogInterface dialogInterface) {
        if (runnable != null) {
            runnable.run();
        }
    }

    public static void lambda$createBotLaunchAlert$29(AlertDialog alertDialog, Context context) {
        if (alertDialog != null) {
            alertDialog.dismiss();
        }
        Browser.openUrl(context, LocaleController.getString(R.string.BotWebViewStartPermissionLink));
    }

    public static void lambda$createBotLaunchAlert$30(TLRPC.User user, BaseFragment baseFragment, AlertDialog.Builder builder, View view) {
        Bundle bundle = new Bundle();
        bundle.putLong("user_id", user.id);
        if (baseFragment.getMessagesController().checkCanOpenChat(bundle, baseFragment)) {
            baseFragment.presentFragment(new ProfileActivity(bundle));
        }
        builder.getDismissRunnable().run();
    }

    public static void lambda$createBotLaunchAlert$31(AtomicBoolean atomicBoolean, View view) {
        atomicBoolean.set(!atomicBoolean.get());
        ((CheckBoxCell) view).setChecked(atomicBoolean.get(), true);
    }

    public static void lambda$createBotLaunchAlert$33(AlertDialog alertDialog, Context context) {
        if (alertDialog != null) {
            alertDialog.dismiss();
        }
        Browser.openUrl(context, LocaleController.getString(R.string.BotWebViewStartPermissionLink));
    }

    public static boolean lambda$createCalendarPickerDialog$123(View view, MotionEvent motionEvent) {
        return true;
    }

    public static String lambda$createCalendarPickerDialog$124(int i) {
        return "" + i;
    }

    public static String lambda$createCalendarPickerDialog$126(int i) {
        int i2;
        switch (i) {
            case 0:
                i2 = R.string.January;
                break;
            case 1:
                i2 = R.string.February;
                break;
            case 2:
                i2 = R.string.March;
                break;
            case 3:
                i2 = R.string.April;
                break;
            case 4:
                i2 = R.string.May;
                break;
            case 5:
                i2 = R.string.June;
                break;
            case 6:
                i2 = R.string.July;
                break;
            case 7:
                i2 = R.string.August;
                break;
            case 8:
                i2 = R.string.September;
                break;
            case 9:
                i2 = R.string.October;
                break;
            case 10:
                i2 = R.string.November;
                break;
            default:
                i2 = R.string.December;
                break;
        }
        return LocaleController.getString(i2);
    }

    public static String lambda$createCalendarPickerDialog$127(int i) {
        return String.format("%02d", Integer.valueOf(i));
    }

    public static void lambda$createCalendarPickerDialog$128(long j, NumberPicker numberPicker, NumberPicker numberPicker2, NumberPicker numberPicker3, Calendar calendar, MessagesStorage.IntCallback intCallback, BottomSheet.Builder builder, View view) {
        checkCalendarDate(j, numberPicker, numberPicker2, numberPicker3);
        calendar.set(1, numberPicker3.getValue());
        calendar.set(2, numberPicker2.getValue());
        calendar.set(5, numberPicker.getValue());
        calendar.set(12, 0);
        calendar.set(11, 0);
        calendar.set(13, 0);
        intCallback.run((int) (calendar.getTimeInMillis() / 1000));
        builder.getDismissRunnable().run();
    }

    public static void lambda$createCallDialogAlert$42(BaseFragment baseFragment, TLRPC.User user, boolean z, AlertDialog alertDialog, int i) {
        TLRPC.UserFull userFull = baseFragment.getMessagesController().getUserFull(user.id);
        VoIPHelper.startCall(user, z, userFull != null && userFull.video_calls_available, baseFragment.getParentActivity(), userFull, baseFragment.getAccountInstance());
    }

    public static void lambda$createChangeBioAlert$43(TLObject tLObject, TLRPC.TL_error tL_error) {
    }

    public static void lambda$createChangeBioAlert$44(long j, int i, EditText editText, AlertDialog alertDialog, int i2) {
        MessagesController messagesController = MessagesController.getInstance(i);
        if (j > 0) {
            TLRPC.UserFull userFull = messagesController.getUserFull(UserConfig.getInstance(i).getClientUserId());
            String trim = editText.getText().toString().replace("\n", " ").replaceAll(" +", " ").trim();
            if (userFull != null) {
                String str = userFull.about;
                if (!(str != null ? str : "").equals(trim)) {
                    userFull.about = trim;
                    NotificationCenter.getInstance(i).lambda$postNotificationNameOnUIThread$1(NotificationCenter.userInfoDidLoad, Long.valueOf(j), userFull);
                }
                AndroidUtilities.hideKeyboard(editText);
                alertDialog.dismiss();
                return;
            }
            TL_account.updateProfile updateprofile = new TL_account.updateProfile();
            updateprofile.about = trim;
            updateprofile.flags = 4 | updateprofile.flags;
            NotificationCenter.getGlobalInstance().lambda$postNotificationNameOnUIThread$1(NotificationCenter.showBulletin, 2, Long.valueOf(j));
            ConnectionsManager.getInstance(i).sendRequest(updateprofile, new RequestDelegate() {
                @Override
                public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                    AlertsCreator.lambda$createChangeBioAlert$43(tLObject, tL_error);
                }
            }, 2);
            alertDialog.dismiss();
        }
        long j2 = -j;
        TLRPC.ChatFull chatFull = messagesController.getChatFull(j2);
        String obj = editText.getText().toString();
        if (chatFull != null) {
            String str2 = chatFull.about;
            if (!(str2 != null ? str2 : "").equals(obj)) {
                chatFull.about = obj;
                NotificationCenter notificationCenter = NotificationCenter.getInstance(i);
                int i3 = NotificationCenter.chatInfoDidLoad;
                Boolean bool = Boolean.FALSE;
                notificationCenter.lambda$postNotificationNameOnUIThread$1(i3, chatFull, 0, bool, bool);
            }
            AndroidUtilities.hideKeyboard(editText);
            alertDialog.dismiss();
            return;
        }
        NotificationCenter.getGlobalInstance().lambda$postNotificationNameOnUIThread$1(NotificationCenter.showBulletin, 2, Long.valueOf(j));
        MessagesController.getInstance(i).updateChatAbout(j2, obj, chatFull);
        alertDialog.dismiss();
    }

    public static boolean lambda$createChangeBioAlert$46(long j, AlertDialog alertDialog, AlertDialog.OnButtonClickListener onButtonClickListener, TextView textView, int i, KeyEvent keyEvent) {
        if ((i != 6 && (j <= 0 || keyEvent.getKeyCode() != 66)) || !alertDialog.isShowing()) {
            return false;
        }
        onButtonClickListener.onClick(alertDialog, 0);
        return true;
    }

    public static void lambda$createChangeNameAlert$47(TLObject tLObject, TLRPC.TL_error tL_error) {
    }

    public static void lambda$createChangeNameAlert$48(EditText editText, long j, int i, EditText editText2, AlertDialog alertDialog, int i2) {
        if (editText.getText() == null) {
            return;
        }
        if (j > 0) {
            TLRPC.User user = MessagesController.getInstance(i).getUser(Long.valueOf(j));
            String obj = editText.getText().toString();
            String obj2 = editText2.getText().toString();
            String str = user.first_name;
            String str2 = user.last_name;
            if (str == null) {
                str = "";
            }
            if (str2 == null) {
                str2 = "";
            }
            if (str.equals(obj) && str2.equals(obj2)) {
                alertDialog.dismiss();
                return;
            }
            TL_account.updateProfile updateprofile = new TL_account.updateProfile();
            updateprofile.flags = 3;
            updateprofile.first_name = obj;
            user.first_name = obj;
            updateprofile.last_name = obj2;
            user.last_name = obj2;
            TLRPC.User user2 = MessagesController.getInstance(i).getUser(Long.valueOf(UserConfig.getInstance(i).getClientUserId()));
            if (user2 != null) {
                user2.first_name = updateprofile.first_name;
                user2.last_name = updateprofile.last_name;
            }
            UserConfig.getInstance(i).saveConfig(true);
            NotificationCenter.getInstance(i).lambda$postNotificationNameOnUIThread$1(NotificationCenter.mainUserInfoChanged, new Object[0]);
            NotificationCenter.getInstance(i).lambda$postNotificationNameOnUIThread$1(NotificationCenter.updateInterfaces, Integer.valueOf(MessagesController.UPDATE_MASK_NAME));
            ConnectionsManager.getInstance(i).sendRequest(updateprofile, new RequestDelegate() {
                @Override
                public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                    AlertsCreator.lambda$createChangeNameAlert$47(tLObject, tL_error);
                }
            });
            NotificationCenter.getGlobalInstance().lambda$postNotificationNameOnUIThread$1(NotificationCenter.showBulletin, 3, Long.valueOf(j));
        } else {
            long j2 = -j;
            TLRPC.Chat chat = MessagesController.getInstance(i).getChat(Long.valueOf(j2));
            String obj3 = editText.getText().toString();
            String str3 = chat.title;
            if (str3 != null && str3.equals(obj3)) {
                alertDialog.dismiss();
                return;
            }
            chat.title = obj3;
            NotificationCenter.getInstance(i).lambda$postNotificationNameOnUIThread$1(NotificationCenter.updateInterfaces, Integer.valueOf(MessagesController.UPDATE_MASK_CHAT_NAME));
            MessagesController.getInstance(i).changeChatTitle(j2, obj3);
            NotificationCenter.getGlobalInstance().lambda$postNotificationNameOnUIThread$1(NotificationCenter.showBulletin, 3, Long.valueOf(j));
        }
        alertDialog.dismiss();
    }

    public static void lambda$createChangeNameAlert$49(EditText editText, EditText editText2, DialogInterface dialogInterface) {
        AndroidUtilities.hideKeyboard(editText);
        AndroidUtilities.hideKeyboard(editText2);
    }

    public static boolean lambda$createChangeNameAlert$50(AlertDialog alertDialog, AlertDialog.OnButtonClickListener onButtonClickListener, TextView textView, int i, KeyEvent keyEvent) {
        if ((i != 6 && keyEvent.getKeyCode() != 66) || !alertDialog.isShowing()) {
            return false;
        }
        onButtonClickListener.onClick(alertDialog, 0);
        return true;
    }

    public static void lambda$createClearDaysDialogAlert$40(boolean[] zArr, View view) {
        boolean z = !zArr[0];
        zArr[0] = z;
        ((CheckBoxCell) view).setChecked(z, true);
    }

    public static void lambda$createClearDaysDialogAlert$41(MessagesStorage.BooleanCallback booleanCallback, boolean[] zArr, AlertDialog alertDialog, int i) {
        booleanCallback.run(zArr[0]);
    }

    public static void lambda$createClearOrDeleteDialogAlert$34(boolean[] zArr, View view) {
        boolean z = !zArr[0];
        zArr[0] = z;
        ((CheckBoxCell) view).setChecked(z, true);
    }

    public static void lambda$createClearOrDeleteDialogAlert$35(boolean[] zArr, View view) {
        boolean z = !zArr[0];
        zArr[0] = z;
        ((CheckBoxCell) view).setChecked(z, true);
    }

    public static void lambda$createClearOrDeleteDialogAlert$36(BaseFragment baseFragment, boolean z, boolean z2, TLRPC.Chat chat, TLRPC.User user, boolean z3, boolean z4, MessagesStorage.BooleanCallback booleanCallback, Theme.ResourcesProvider resourcesProvider, boolean[] zArr, int i) {
        if (i >= 50) {
            createClearOrDeleteDialogAlert(baseFragment, z, z2, true, chat, user, false, z3, z4, booleanCallback, resourcesProvider);
        } else if (booleanCallback != null) {
            booleanCallback.run(zArr[0]);
        }
    }

    public static void lambda$createClearOrDeleteDialogAlert$37(boolean z, boolean z2, boolean z3, final TLRPC.User user, final BaseFragment baseFragment, final boolean z4, final boolean z5, final TLRPC.Chat chat, final boolean z6, final boolean z7, final MessagesStorage.BooleanCallback booleanCallback, final Theme.ResourcesProvider resourcesProvider, final boolean[] zArr, AlertDialog alertDialog, int i) {
        if (!z && !z2 && !z3) {
            if (UserObject.isUserSelf(user)) {
                createClearOrDeleteDialogAlert(baseFragment, z4, z5, true, chat, user, false, z6, z7, booleanCallback, resourcesProvider);
                return;
            } else if (user != null && zArr[0]) {
                MessagesStorage.getInstance(baseFragment.getCurrentAccount()).getMessagesCount(user.id, new MessagesStorage.IntCallback() {
                    @Override
                    public final void run(int i2) {
                        AlertsCreator.lambda$createClearOrDeleteDialogAlert$36(BaseFragment.this, z4, z5, chat, user, z6, z7, booleanCallback, resourcesProvider, zArr, i2);
                    }
                });
                return;
            }
        }
        if (booleanCallback != null) {
            booleanCallback.run(z2 || zArr[0]);
        }
    }

    public static void lambda$createClearOrDeleteDialogsAlert$38(boolean[] zArr, View view) {
        boolean z = !zArr[0];
        zArr[0] = z;
        ((CheckBoxCell) view).setChecked(z, true);
    }

    public static void lambda$createClearOrDeleteDialogsAlert$39(MessagesStorage.BooleanCallback booleanCallback, boolean[] zArr, AlertDialog alertDialog, int i) {
        if (booleanCallback != null) {
            booleanCallback.run(zArr[0]);
        }
    }

    public static void lambda$createColorSelectDialog$134(LinearLayout linearLayout, int[] iArr, View view) {
        int childCount = linearLayout.getChildCount();
        for (int i = 0; i < childCount; i++) {
            RadioColorCell radioColorCell = (RadioColorCell) linearLayout.getChildAt(i);
            radioColorCell.setChecked(radioColorCell == view, true);
        }
        iArr[0] = TextColorCell.colorsToSave[((Integer) view.getTag()).intValue()];
    }

    public static void lambda$createColorSelectDialog$135(long j, String str, int[] iArr, long j2, int i, Runnable runnable, AlertDialog alertDialog, int i2) {
        int i3;
        String str2;
        SharedPreferences.Editor edit = MessagesController.getNotificationsSettings(UserConfig.selectedAccount).edit();
        if (j != 0) {
            edit.putInt("color_" + str, iArr[0]);
            NotificationsController.getInstance(UserConfig.selectedAccount).deleteNotificationChannel(j, j2);
        } else {
            if (i == 1) {
                i3 = iArr[0];
                str2 = "MessagesLed";
            } else if (i == 0) {
                i3 = iArr[0];
                str2 = "GroupLed";
            } else if (i == 3) {
                i3 = iArr[0];
                str2 = "StoriesLed";
            } else if (i == 5 || i == 4) {
                i3 = iArr[0];
                str2 = "ReactionLed";
            } else {
                i3 = iArr[0];
                str2 = "ChannelLed";
            }
            edit.putInt(str2, i3);
            NotificationsController.getInstance(UserConfig.selectedAccount).deleteNotificationChannelGlobal(i);
        }
        edit.commit();
        if (runnable != null) {
            runnable.run();
        }
    }

    public static void lambda$createColorSelectDialog$136(long j, int i, Runnable runnable, AlertDialog alertDialog, int i2) {
        String str;
        SharedPreferences.Editor edit = MessagesController.getNotificationsSettings(UserConfig.selectedAccount).edit();
        if (j != 0) {
            str = "color_" + j;
        } else {
            str = i == 1 ? "MessagesLed" : i == 0 ? "GroupLed" : i == 3 ? "StoriesLed" : (i == 5 || i == 4) ? "ReactionsLed" : "ChannelLed";
        }
        edit.putInt(str, 0);
        edit.commit();
        if (runnable != null) {
            runnable.run();
        }
    }

    public static void lambda$createColorSelectDialog$137(String str, Runnable runnable, AlertDialog alertDialog, int i) {
        SharedPreferences.Editor edit = MessagesController.getNotificationsSettings(UserConfig.selectedAccount).edit();
        edit.remove("color_" + str);
        edit.commit();
        if (runnable != null) {
            runnable.run();
        }
    }

    public static void lambda$createContactInviteDialog$54(String str, BaseFragment baseFragment, AlertDialog alertDialog, int i) {
        try {
            Intent intent = new Intent("android.intent.action.VIEW", Uri.fromParts("sms", str, null));
            intent.putExtra("sms_body", ContactsController.getInstance(baseFragment.getCurrentAccount()).getInviteText(1));
            baseFragment.getParentActivity().startActivityForResult(intent, 500);
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    public static void lambda$createDatePickerDialog$71(boolean z, NumberPicker numberPicker, NumberPicker numberPicker2, NumberPicker numberPicker3, NumberPicker numberPicker4, int i) {
        if (z && i == 0) {
            checkPickerDate(numberPicker, numberPicker2, numberPicker3);
        }
    }

    public static String lambda$createDatePickerDialog$72(int i) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(5, 1);
        calendar.set(2, i);
        return calendar.getDisplayName(2, 1, Locale.getDefault());
    }

    public static void lambda$createDatePickerDialog$74(boolean z, NumberPicker numberPicker, NumberPicker numberPicker2, NumberPicker numberPicker3, NumberPicker numberPicker4, int i) {
        if (z && i == 0) {
            checkPickerDate(numberPicker, numberPicker2, numberPicker3);
        }
    }

    public static void lambda$createDatePickerDialog$76(boolean z, NumberPicker numberPicker, NumberPicker numberPicker2, NumberPicker numberPicker3, NumberPicker numberPicker4, int i) {
        if (z && i == 0) {
            checkPickerDate(numberPicker, numberPicker2, numberPicker3);
        }
    }

    public static void lambda$createDatePickerDialog$77(boolean z, NumberPicker numberPicker, NumberPicker numberPicker2, NumberPicker numberPicker3, DatePickerDelegate datePickerDelegate, AlertDialog alertDialog, int i) {
        if (z) {
            checkPickerDate(numberPicker, numberPicker2, numberPicker3);
        }
        datePickerDelegate.didSelectDate(numberPicker3.getValue(), numberPicker2.getValue(), numberPicker.getValue());
    }

    public static boolean lambda$createDatePickerDialog$87(View view, MotionEvent motionEvent) {
        return true;
    }

    public static String lambda$createDatePickerDialog$88(long j, Calendar calendar, int i, int i2) {
        if (i2 == 0) {
            return LocaleController.getString(R.string.MessageScheduleToday);
        }
        long j2 = j + (i2 * 86400000);
        calendar.setTimeInMillis(j2);
        return calendar.get(1) == i ? LocaleController.getInstance().getFormatterScheduleDay().format(j2) : LocaleController.getInstance().getFormatterScheduleYear().format(j2);
    }

    public static String lambda$createDatePickerDialog$90(int i) {
        return String.format("%02d", Integer.valueOf(i));
    }

    public static String lambda$createDatePickerDialog$91(int i) {
        return String.format("%02d", Integer.valueOf(i));
    }

    public static void lambda$createDatePickerDialog$92(NumberPicker numberPicker, NumberPicker numberPicker2, NumberPicker numberPicker3, Calendar calendar, ScheduleDatePickerDelegate scheduleDatePickerDelegate, BottomSheet.Builder builder, View view) {
        boolean checkScheduleDate = checkScheduleDate(null, null, 0, numberPicker, numberPicker2, numberPicker3);
        calendar.setTimeInMillis(System.currentTimeMillis() + (numberPicker.getValue() * 86400000));
        calendar.set(11, numberPicker2.getValue());
        calendar.set(12, numberPicker3.getValue());
        if (checkScheduleDate) {
            calendar.set(13, 0);
        }
        scheduleDatePickerDelegate.didSelectDate(true, (int) (calendar.getTimeInMillis() / 1000));
        builder.getDismissRunnable().run();
    }

    public static TLObject lambda$createDeleteMessagesAlert$154(int i, long j) {
        MessagesController messagesController = MessagesController.getInstance(i);
        return j > 0 ? messagesController.getUser(Long.valueOf(j)) : messagesController.getChat(Long.valueOf(-j));
    }

    public static boolean lambda$createDeleteMessagesAlert$155(long j, TLObject tLObject) {
        if (tLObject instanceof TLRPC.User) {
            return ((TLRPC.User) tLObject).id != j;
        }
        if (tLObject instanceof TLRPC.Chat) {
            return !ChatObject.hasAdminRights((TLRPC.Chat) tLObject);
        }
        return false;
    }

    public static void lambda$createDeleteMessagesAlert$156(int[] iArr, int[] iArr2, int i, TLObject tLObject, TLRPC.ChannelParticipant[] channelParticipantArr, int i2, AlertDialog[] alertDialogArr, BaseFragment baseFragment, TLRPC.User user, TLRPC.Chat chat, TLRPC.EncryptedChat encryptedChat, TLRPC.ChatFull chatFull, long j, MessageObject messageObject, SparseArray[] sparseArrayArr, MessageObject.GroupedMessages groupedMessages, int i3, int i4, Runnable runnable, Runnable runnable2, Theme.ResourcesProvider resourcesProvider) {
        iArr[0] = iArr[0] + 1;
        iArr2[i] = 0;
        if (tLObject != null) {
            channelParticipantArr[i] = ((TLRPC.TL_channels_channelParticipant) tLObject).participant;
        }
        if (iArr[0] == i2) {
            try {
                alertDialogArr[0].dismiss();
            } catch (Throwable unused) {
            }
            alertDialogArr[0] = null;
            createDeleteMessagesAlert(baseFragment, user, chat, encryptedChat, chatFull, j, messageObject, sparseArrayArr, groupedMessages, i3, i4, channelParticipantArr, runnable, runnable2, resourcesProvider);
        }
    }

    public static void lambda$createDeleteMessagesAlert$157(final int[] iArr, final int[] iArr2, final int i, final TLRPC.ChannelParticipant[] channelParticipantArr, final int i2, final AlertDialog[] alertDialogArr, final BaseFragment baseFragment, final TLRPC.User user, final TLRPC.Chat chat, final TLRPC.EncryptedChat encryptedChat, final TLRPC.ChatFull chatFull, final long j, final MessageObject messageObject, final SparseArray[] sparseArrayArr, final MessageObject.GroupedMessages groupedMessages, final int i3, final int i4, final Runnable runnable, final Runnable runnable2, final Theme.ResourcesProvider resourcesProvider, final TLObject tLObject, TLRPC.TL_error tL_error) {
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                AlertsCreator.lambda$createDeleteMessagesAlert$156(iArr, iArr2, i, tLObject, channelParticipantArr, i2, alertDialogArr, baseFragment, user, chat, encryptedChat, chatFull, j, messageObject, sparseArrayArr, groupedMessages, i3, i4, runnable, runnable2, resourcesProvider);
            }
        });
    }

    public static void lambda$createDeleteMessagesAlert$158(int[] iArr, int i, Runnable runnable, DialogInterface dialogInterface) {
        for (int i2 : iArr) {
            if (i2 != 0) {
                ConnectionsManager.getInstance(i).cancelRequest(i2, true);
            }
        }
        if (runnable != null) {
            runnable.run();
        }
    }

    public static void lambda$createDeleteMessagesAlert$159(AlertDialog[] alertDialogArr, final int[] iArr, final int i, final Runnable runnable, BaseFragment baseFragment) {
        AlertDialog alertDialog = alertDialogArr[0];
        if (alertDialog == null) {
            return;
        }
        alertDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public final void onCancel(DialogInterface dialogInterface) {
                AlertsCreator.lambda$createDeleteMessagesAlert$158(iArr, i, runnable, dialogInterface);
            }
        });
        baseFragment.showDialog(alertDialogArr[0]);
    }

    public static void lambda$createDeleteMessagesAlert$161(boolean[] zArr, View view) {
        boolean z = !zArr[0];
        zArr[0] = z;
        ((CheckBoxCell) view).setChecked(z, true);
    }

    public static void lambda$createDeleteMessagesAlert$162(boolean[] zArr, View view) {
        boolean z = !zArr[0];
        zArr[0] = z;
        ((CheckBoxCell) view).setChecked(z, true);
    }

    public static void lambda$createDeleteMessagesAlert$163(long j, boolean z, int i, MessageObject messageObject, MessageObject.GroupedMessages groupedMessages, TLRPC.EncryptedChat encryptedChat, long j2, int i2, boolean[] zArr, int i3, SparseArray[] sparseArrayArr, Runnable runnable, AlertDialog alertDialog, int i4) {
        ArrayList<Long> arrayList;
        TLRPC.Peer peer;
        long clientUserId = z ? UserConfig.getInstance(i).getClientUserId() : j;
        ArrayList<Long> arrayList2 = null;
        if (messageObject != null) {
            ArrayList<Integer> arrayList3 = new ArrayList<>();
            if (groupedMessages != null) {
                for (int i5 = 0; i5 < groupedMessages.messages.size(); i5++) {
                    MessageObject messageObject2 = groupedMessages.messages.get(i5);
                    arrayList3.add(Integer.valueOf(messageObject2.getId()));
                    if (encryptedChat != null && messageObject2.messageOwner.random_id != 0 && messageObject2.type != 10) {
                        if (arrayList2 == null) {
                            arrayList2 = new ArrayList<>();
                        }
                        arrayList2.add(Long.valueOf(messageObject2.messageOwner.random_id));
                    }
                }
            } else {
                arrayList3.add(Integer.valueOf(messageObject.getId()));
                if (encryptedChat != null && messageObject.messageOwner.random_id != 0 && messageObject.type != 10) {
                    ArrayList<Long> arrayList4 = new ArrayList<>();
                    arrayList4.add(Long.valueOf(messageObject.messageOwner.random_id));
                    arrayList2 = arrayList4;
                }
            }
            MessagesController.getInstance(i).deleteMessages(arrayList3, arrayList2, encryptedChat, (j2 == 0 || (peer = messageObject.messageOwner.peer_id) == null || peer.chat_id != (-j2)) ? clientUserId : j2, i2, zArr[0], i3);
        } else {
            int i6 = 1;
            int i7 = 1;
            while (i7 >= 0) {
                ArrayList<Integer> arrayList5 = new ArrayList<>();
                for (int i8 = 0; i8 < sparseArrayArr[i7].size(); i8++) {
                    arrayList5.add(Integer.valueOf(sparseArrayArr[i7].keyAt(i8)));
                }
                if (encryptedChat != null) {
                    ArrayList<Long> arrayList6 = new ArrayList<>();
                    for (int i9 = 0; i9 < sparseArrayArr[i7].size(); i9++) {
                        MessageObject messageObject3 = (MessageObject) sparseArrayArr[i7].valueAt(i9);
                        long j3 = messageObject3.messageOwner.random_id;
                        if (j3 != 0 && messageObject3.type != 10) {
                            arrayList6.add(Long.valueOf(j3));
                        }
                    }
                    arrayList = arrayList6;
                } else {
                    arrayList = null;
                }
                int i10 = i7;
                MessagesController.getInstance(i).deleteMessages(arrayList5, arrayList, encryptedChat, (i7 != i6 || j2 == 0) ? clientUserId : j2, i2, zArr[0], i3);
                sparseArrayArr[i10].clear();
                i7 = i10 - 1;
                i6 = 1;
            }
        }
        if (runnable != null) {
            runnable.run();
        }
    }

    public static void lambda$createDeleteMessagesAlert$164(Runnable runnable, DialogInterface dialogInterface) {
        if (runnable != null) {
            runnable.run();
        }
    }

    public static void lambda$createDrawOverlayGroupCallPermissionDialog$144(Context context, AlertDialog alertDialog, int i) {
        if (context != null) {
            try {
                if (Build.VERSION.SDK_INT >= 23) {
                    Intent intent = new Intent("android.settings.action.MANAGE_OVERLAY_PERMISSION", Uri.parse("package:" + context.getPackageName()));
                    Activity findActivity = AndroidUtilities.findActivity(context);
                    if (findActivity instanceof LaunchActivity) {
                        findActivity.startActivityForResult(intent, 105);
                    } else {
                        context.startActivity(intent);
                    }
                }
            } catch (Exception e) {
                FileLog.e(e);
            }
        }
    }

    public static void lambda$createDrawOverlayPermissionDialog$143(Activity activity, AlertDialog alertDialog, int i) {
        if (activity == null || Build.VERSION.SDK_INT < 23) {
            return;
        }
        try {
            activity.startActivity(new Intent("android.settings.action.MANAGE_OVERLAY_PERMISSION", Uri.parse("package:" + activity.getPackageName())));
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    public static void lambda$createFreeSpaceDialog$147(LaunchActivity launchActivity, AlertDialog alertDialog, int i) {
        launchActivity.lambda$runLinkRequest$95(new CacheControlActivity());
    }

    public static void lambda$createImportDialogAlert$25(Runnable runnable, AlertDialog alertDialog, int i) {
        if (runnable != null) {
            runnable.run();
        }
    }

    public static void lambda$createLanguageAlert$8(LaunchActivity launchActivity, AlertDialog alertDialog, int i) {
        launchActivity.lambda$runLinkRequest$95(new LanguageSelectActivity());
    }

    public static void lambda$createLanguageAlert$9(TLRPC.TL_langPackLanguage tL_langPackLanguage, LaunchActivity launchActivity, AlertDialog alertDialog, int i) {
        StringBuilder sb;
        String str;
        if (tL_langPackLanguage.official) {
            sb = new StringBuilder();
            str = "remote_";
        } else {
            sb = new StringBuilder();
            str = "unofficial_";
        }
        sb.append(str);
        sb.append(tL_langPackLanguage.lang_code);
        LocaleController.LocaleInfo languageFromDict = LocaleController.getInstance().getLanguageFromDict(sb.toString());
        if (languageFromDict == null) {
            languageFromDict = new LocaleController.LocaleInfo();
            languageFromDict.name = tL_langPackLanguage.native_name;
            languageFromDict.nameEnglish = tL_langPackLanguage.name;
            languageFromDict.shortName = tL_langPackLanguage.lang_code;
            languageFromDict.baseLangCode = tL_langPackLanguage.base_lang_code;
            languageFromDict.pluralLangCode = tL_langPackLanguage.plural_code;
            languageFromDict.isRtl = tL_langPackLanguage.rtl;
            languageFromDict.pathToFile = tL_langPackLanguage.official ? "remote" : "unofficial";
        }
        LocaleController.getInstance().applyLanguage(languageFromDict, true, false, false, true, UserConfig.selectedAccount, null);
        launchActivity.rebuildAllFragments(true);
    }

    public static void lambda$createLocationRequiredDialog$0(Context context, AlertDialog alertDialog, int i) {
        try {
            Intent intent = new Intent("android.settings.APPLICATION_DETAILS_SETTINGS");
            intent.setData(Uri.parse("package:" + ApplicationLoader.applicationContext.getPackageName()));
            context.startActivity(intent);
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    public static void lambda$createLocationUpdateDialog$139(int[] iArr, LinearLayout linearLayout, View view) {
        iArr[0] = ((Integer) view.getTag()).intValue();
        int childCount = linearLayout.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View childAt = linearLayout.getChildAt(i);
            if (childAt instanceof RadioColorCell) {
                ((RadioColorCell) childAt).setChecked(childAt == view, true);
            }
        }
    }

    public static void lambda$createLocationUpdateDialog$140(int[] iArr, MessagesStorage.IntCallback intCallback, AlertDialog alertDialog, int i) {
        int i2 = iArr[0];
        intCallback.run(i2 == 0 ? 900 : i2 == 1 ? 3600 : i2 == 2 ? 28800 : Integer.MAX_VALUE);
    }

    public static void lambda$createMuteAlert$129(long j, long j2, BaseFragment baseFragment, Theme.ResourcesProvider resourcesProvider, DialogInterface dialogInterface, int i) {
        int i2;
        if (i == 0) {
            i2 = 0;
        } else {
            i2 = 1;
            if (i != 1) {
                i2 = 2;
                if (i != 2) {
                    i2 = 3;
                }
            }
        }
        NotificationsController.getInstance(UserConfig.selectedAccount).setDialogNotificationsSettings(j, j2, i2);
        if (BulletinFactory.canShowBulletin(baseFragment)) {
            BulletinFactory.createMuteBulletin(baseFragment, i2, 0, resourcesProvider).show();
        }
    }

    public static void lambda$createMuteAlert$130(ArrayList arrayList, int i, BaseFragment baseFragment, Theme.ResourcesProvider resourcesProvider, DialogInterface dialogInterface, int i2) {
        int i3;
        if (i2 == 0) {
            i3 = 0;
        } else {
            i3 = 1;
            if (i2 != 1) {
                i3 = 2;
                if (i2 != 2) {
                    i3 = 3;
                }
            }
        }
        if (arrayList != null) {
            for (int i4 = 0; i4 < arrayList.size(); i4++) {
                NotificationsController.getInstance(UserConfig.selectedAccount).setDialogNotificationsSettings(((Long) arrayList.get(i4)).longValue(), i, i3);
            }
        }
        if (BulletinFactory.canShowBulletin(baseFragment)) {
            BulletinFactory.createMuteBulletin(baseFragment, i3, 0, resourcesProvider).show();
        }
    }

    public static String lambda$createMuteForPickerDialog$119(int[] iArr, int i) {
        int i2 = iArr[i];
        return i2 == 0 ? LocaleController.getString(R.string.MuteNever) : i2 < 60 ? LocaleController.formatPluralString("Minutes", i2, new Object[0]) : i2 < 1440 ? LocaleController.formatPluralString("Hours", i2 / 60, new Object[0]) : i2 < 10080 ? LocaleController.formatPluralString("Days", i2 / 1440, new Object[0]) : i2 < 44640 ? LocaleController.formatPluralString("Weeks", i2 / 10080, new Object[0]) : i2 < 525600 ? LocaleController.formatPluralString("Months", i2 / 44640, new Object[0]) : LocaleController.formatPluralString("Years", i2 / 525600, new Object[0]);
    }

    public static boolean lambda$createMuteForPickerDialog$120(View view, MotionEvent motionEvent) {
        return true;
    }

    public static void lambda$createMuteForPickerDialog$121(NumberPicker numberPicker, int i, int i2) {
    }

    public static void lambda$createMuteForPickerDialog$122(int[] iArr, NumberPicker numberPicker, ScheduleDatePickerDelegate scheduleDatePickerDelegate, BottomSheet.Builder builder, View view) {
        scheduleDatePickerDelegate.didSelectDate(true, iArr[numberPicker.getValue()] * 60);
        builder.getDismissRunnable().run();
    }

    public static void lambda$createPopupSelectDialog$149(int[] iArr, int i, AlertDialog.Builder builder, Runnable runnable, View view) {
        int i2;
        String str;
        iArr[0] = ((Integer) view.getTag()).intValue();
        SharedPreferences.Editor edit = MessagesController.getNotificationsSettings(UserConfig.selectedAccount).edit();
        if (i == 1) {
            i2 = iArr[0];
            str = "popupAll";
        } else if (i == 0) {
            i2 = iArr[0];
            str = "popupGroup";
        } else {
            i2 = iArr[0];
            str = "popupChannel";
        }
        edit.putInt(str, i2);
        edit.commit();
        builder.getDismissRunnable().run();
        if (runnable != null) {
            runnable.run();
        }
    }

    public static void lambda$createPrioritySelectDialog$148(int[] iArr, long j, long j2, int i, SharedPreferences sharedPreferences, AlertDialog.Builder builder, Runnable runnable, View view) {
        int i2 = 0;
        iArr[0] = ((Integer) view.getTag()).intValue();
        SharedPreferences.Editor edit = MessagesController.getNotificationsSettings(UserConfig.selectedAccount).edit();
        if (j != 0) {
            int i3 = iArr[0];
            if (i3 == 0) {
                i2 = 3;
            } else if (i3 == 1) {
                i2 = 4;
            } else if (i3 == 2) {
                i2 = 5;
            } else if (i3 != 3) {
                i2 = 1;
            }
            edit.putInt("priority_" + j, i2);
            NotificationsController.getInstance(UserConfig.selectedAccount).deleteNotificationChannel(j, j2);
        } else {
            int i4 = iArr[0];
            int i5 = i4 == 0 ? 4 : i4 == 1 ? 5 : i4 == 2 ? 0 : 1;
            if (i == 1) {
                edit.putInt("priority_messages", i5);
                iArr[0] = sharedPreferences.getInt("priority_messages", 1);
            } else if (i == 0) {
                edit.putInt("priority_group", i5);
                iArr[0] = sharedPreferences.getInt("priority_group", 1);
            } else if (i == 2) {
                edit.putInt("priority_channel", i5);
                iArr[0] = sharedPreferences.getInt("priority_channel", 1);
            } else if (i == 3) {
                edit.putInt("priority_stories", i5);
                iArr[0] = sharedPreferences.getInt("priority_stories", 1);
            } else if (i == 4 || i == 5) {
                edit.putInt("priority_react", i5);
                iArr[0] = sharedPreferences.getInt("priority_react", 1);
            }
            NotificationsController.getInstance(UserConfig.selectedAccount).deleteNotificationChannelGlobal(i);
        }
        edit.commit();
        builder.getDismissRunnable().run();
        if (runnable != null) {
            runnable.run();
        }
    }

    public static void lambda$createReportPhotoAlert$131(int i, long j, TLRPC.Photo photo, Context context, Theme.ResourcesProvider resourcesProvider, Integer num, String str) {
        TLRPC.ReportReason tL_inputReportReasonPersonalDetails;
        TL_account.reportProfilePhoto reportprofilephoto = new TL_account.reportProfilePhoto();
        reportprofilephoto.peer = MessagesController.getInstance(i).getInputPeer(j);
        TLRPC.TL_inputPhoto tL_inputPhoto = new TLRPC.TL_inputPhoto();
        tL_inputPhoto.id = photo.id;
        tL_inputPhoto.file_reference = photo.file_reference;
        tL_inputPhoto.access_hash = photo.access_hash;
        reportprofilephoto.photo_id = tL_inputPhoto;
        reportprofilephoto.message = "";
        if (num.intValue() == 0) {
            tL_inputReportReasonPersonalDetails = new TLRPC.TL_inputReportReasonSpam();
        } else if (num.intValue() == 1) {
            tL_inputReportReasonPersonalDetails = new TLRPC.TL_inputReportReasonViolence();
        } else if (num.intValue() == 2) {
            tL_inputReportReasonPersonalDetails = new TLRPC.TL_inputReportReasonChildAbuse();
        } else if (num.intValue() == 5) {
            tL_inputReportReasonPersonalDetails = new TLRPC.TL_inputReportReasonPornography();
        } else {
            if (num.intValue() != 3) {
                if (num.intValue() == 4) {
                    tL_inputReportReasonPersonalDetails = new TLRPC.TL_inputReportReasonPersonalDetails();
                }
                ConnectionsManager.getInstance(i).sendRequest(reportprofilephoto, null);
                BulletinFactory.of(Bulletin.BulletinWindow.make(context), resourcesProvider).createReportSent(resourcesProvider).show();
            }
            tL_inputReportReasonPersonalDetails = new TLRPC.TL_inputReportReasonIllegalDrugs();
        }
        reportprofilephoto.reason = tL_inputReportReasonPersonalDetails;
        ConnectionsManager.getInstance(i).sendRequest(reportprofilephoto, null);
        BulletinFactory.of(Bulletin.BulletinWindow.make(context), resourcesProvider).createReportSent(resourcesProvider).show();
    }

    public static void lambda$createReportPhotoAlert$132(int[] iArr, Context context, Theme.ResourcesProvider resourcesProvider, final Utilities.Callback2 callback2, DialogInterface dialogInterface, int i) {
        int i2 = iArr[i];
        if (i2 == 100) {
            new ReportAlert(context, i2, resourcesProvider) {
                @Override
                protected void onSend(int i3, String str) {
                    callback2.run(Integer.valueOf(i3), str);
                }
            }.show();
        } else {
            callback2.run(Integer.valueOf(i2), "");
        }
    }

    public static boolean lambda$createScheduleDatePickerDialog$78(View view, MotionEvent motionEvent) {
        return true;
    }

    public static void lambda$createScheduleDatePickerDialog$79(ActionBarMenuItem actionBarMenuItem, ScheduleDatePickerColors scheduleDatePickerColors, View view) {
        actionBarMenuItem.toggleSubMenu();
        actionBarMenuItem.setPopupItemsColor(scheduleDatePickerColors.subMenuTextColor, false);
        actionBarMenuItem.setupPopupRadialSelectors(scheduleDatePickerColors.subMenuSelectorColor);
        actionBarMenuItem.redrawPopup(scheduleDatePickerColors.subMenuBackgroundColor);
    }

    public static void lambda$createScheduleDatePickerDialog$80(ScheduleDatePickerDelegate scheduleDatePickerDelegate, BottomSheet.Builder builder, int i) {
        if (i == 1) {
            scheduleDatePickerDelegate.didSelectDate(true, 2147483646);
            builder.getDismissRunnable().run();
        }
    }

    public static String lambda$createScheduleDatePickerDialog$81(long j, Calendar calendar, int i, int i2) {
        if (i2 == 0) {
            return LocaleController.getString(R.string.MessageScheduleToday);
        }
        long j2 = j + (i2 * 86400000);
        calendar.setTimeInMillis(j2);
        if (calendar.get(1) != i) {
            return LocaleController.getInstance().getFormatterScheduleYear().format(j2);
        }
        return LocaleController.getInstance().getFormatterWeek().format(j2) + ", " + LocaleController.getInstance().getFormatterScheduleDay().format(j2);
    }

    public static void lambda$createScheduleDatePickerDialog$82(TextView textView, long j, long j2, NumberPicker numberPicker, NumberPicker numberPicker2, NumberPicker numberPicker3, NumberPicker numberPicker4, int i, int i2) {
        checkScheduleDate(textView, null, j == j2 ? 1 : 0, numberPicker, numberPicker2, numberPicker3);
    }

    public static String lambda$createScheduleDatePickerDialog$83(int i) {
        return String.format("%02d", Integer.valueOf(i));
    }

    public static String lambda$createScheduleDatePickerDialog$84(int i) {
        return String.format("%02d", Integer.valueOf(i));
    }

    public static void lambda$createScheduleDatePickerDialog$85(boolean[] zArr, long j, long j2, NumberPicker numberPicker, NumberPicker numberPicker2, NumberPicker numberPicker3, Calendar calendar, ScheduleDatePickerDelegate scheduleDatePickerDelegate, BottomSheet.Builder builder, View view) {
        zArr[0] = false;
        boolean checkScheduleDate = checkScheduleDate(null, null, j == j2 ? 1 : 0, numberPicker, numberPicker2, numberPicker3);
        calendar.setTimeInMillis(System.currentTimeMillis() + (numberPicker.getValue() * 86400000));
        calendar.set(11, numberPicker2.getValue());
        calendar.set(12, numberPicker3.getValue());
        if (checkScheduleDate) {
            calendar.set(13, 0);
        }
        scheduleDatePickerDelegate.didSelectDate(true, (int) (calendar.getTimeInMillis() / 1000));
        builder.getDismissRunnable().run();
    }

    public static void lambda$createScheduleDatePickerDialog$86(Runnable runnable, boolean[] zArr, DialogInterface dialogInterface) {
        if (runnable == null || !zArr[0]) {
            return;
        }
        runnable.run();
    }

    public static void lambda$createSimpleAlert$10(Runnable runnable, AlertDialog alertDialog, int i) {
        alertDialog.dismiss();
        if (runnable != null) {
            runnable.run();
        }
    }

    public static void lambda$createSingleChoiceDialog$150(AlertDialog.Builder builder, DialogInterface.OnClickListener onClickListener, View view) {
        int intValue = ((Integer) view.getTag()).intValue();
        builder.getDismissRunnable().run();
        onClickListener.onClick(null, intValue);
    }

    public static String lambda$createSoundFrequencyPickerDialog$113(int i) {
        return LocaleController.formatPluralString("Times", i + 1, new Object[0]);
    }

    public static String lambda$createSoundFrequencyPickerDialog$114(int i) {
        return LocaleController.formatPluralString("Minutes", i + 1, new Object[0]);
    }

    public static String lambda$createSoundFrequencyPickerDialog$115(int i) {
        return LocaleController.getString(R.string.NotificationsFrequencyDivider);
    }

    public static boolean lambda$createSoundFrequencyPickerDialog$116(View view, MotionEvent motionEvent) {
        return true;
    }

    public static void lambda$createSoundFrequencyPickerDialog$117(NumberPicker numberPicker, int i, int i2) {
    }

    public static void lambda$createSoundFrequencyPickerDialog$118(NumberPicker numberPicker, NumberPicker numberPicker2, SoundFrequencyDelegate soundFrequencyDelegate, BottomSheet.Builder builder, View view) {
        soundFrequencyDelegate.didSelectValues(numberPicker.getValue() + 1, (numberPicker2.getValue() + 1) * 60);
        builder.getDismissRunnable().run();
    }

    public static boolean lambda$createStatusUntilDatePickerDialog$103(View view, MotionEvent motionEvent) {
        return true;
    }

    public static String lambda$createStatusUntilDatePickerDialog$104(long j, Calendar calendar, int i, int i2, int i3) {
        if (i3 == 0) {
            return LocaleController.getString(R.string.MessageScheduleToday);
        }
        long j2 = j + (i3 * 86400000);
        calendar.setTimeInMillis(j2);
        int i4 = calendar.get(1);
        int i5 = calendar.get(6);
        if (i4 != i || i5 >= i2 + 7) {
            LocaleController localeController = LocaleController.getInstance();
            return i4 == i ? localeController.getFormatterScheduleDay().format(j2) : localeController.getFormatterScheduleYear().format(j2);
        }
        return LocaleController.getInstance().getFormatterWeek().format(j2) + ", " + LocaleController.getInstance().getFormatterScheduleDay().format(j2);
    }

    public static String lambda$createStatusUntilDatePickerDialog$106(int i) {
        return String.format("%02d", Integer.valueOf(i));
    }

    public static String lambda$createStatusUntilDatePickerDialog$107(int i) {
        return String.format("%02d", Integer.valueOf(i));
    }

    public static void lambda$createStatusUntilDatePickerDialog$108(NumberPicker numberPicker, NumberPicker numberPicker2, NumberPicker numberPicker3, Calendar calendar, StatusUntilDatePickerDelegate statusUntilDatePickerDelegate, BottomSheet.Builder builder, View view) {
        boolean checkScheduleDate = checkScheduleDate(null, null, 0, numberPicker, numberPicker2, numberPicker3);
        calendar.setTimeInMillis(System.currentTimeMillis() + (numberPicker.getValue() * 86400000));
        calendar.set(11, numberPicker2.getValue());
        calendar.set(12, numberPicker3.getValue());
        if (checkScheduleDate) {
            calendar.set(13, 0);
        }
        statusUntilDatePickerDelegate.didSelectDate((int) (calendar.getTimeInMillis() / 1000));
        builder.getDismissRunnable().run();
    }

    public static String lambda$createTTLAlert$151(int i) {
        return i == 0 ? LocaleController.getString(R.string.ShortMessageLifetimeForever) : (i < 1 || i >= 16) ? i == 16 ? LocaleController.formatTTLString(30) : i == 17 ? LocaleController.formatTTLString(60) : i == 18 ? LocaleController.formatTTLString(3600) : i == 19 ? LocaleController.formatTTLString(86400) : i == 20 ? LocaleController.formatTTLString(604800) : "" : LocaleController.formatTTLString(i);
    }

    public static void lambda$createTTLAlert$152(org.telegram.tgnet.TLRPC.EncryptedChat r0, org.telegram.ui.Components.NumberPicker r1, org.telegram.ui.ActionBar.AlertDialog r2, int r3) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.AlertsCreator.lambda$createTTLAlert$152(org.telegram.tgnet.TLRPC$EncryptedChat, org.telegram.ui.Components.NumberPicker, org.telegram.ui.ActionBar.AlertDialog, int):void");
    }

    public static void lambda$createThemeCreateDialog$165(AlertDialog alertDialog, int i) {
    }

    public static boolean lambda$createThemeCreateDialog$166(TextView textView, int i, KeyEvent keyEvent) {
        AndroidUtilities.hideKeyboard(textView);
        return false;
    }

    public static void lambda$createThemeCreateDialog$167(EditTextBoldCursor editTextBoldCursor) {
        editTextBoldCursor.requestFocus();
        AndroidUtilities.showKeyboard(editTextBoldCursor);
    }

    public static void lambda$createThemeCreateDialog$168(final EditTextBoldCursor editTextBoldCursor, DialogInterface dialogInterface) {
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                AlertsCreator.lambda$createThemeCreateDialog$167(EditTextBoldCursor.this);
            }
        });
    }

    public static void lambda$createThemeCreateDialog$170(final EditTextBoldCursor editTextBoldCursor, final AlertDialog alertDialog, final BaseFragment baseFragment) {
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                AlertsCreator.processCreate(EditTextBoldCursor.this, alertDialog, baseFragment);
            }
        });
    }

    public static void lambda$createThemeCreateDialog$171(final BaseFragment baseFragment, final EditTextBoldCursor editTextBoldCursor, Theme.ThemeAccent themeAccent, Theme.ThemeInfo themeInfo, final AlertDialog alertDialog, View view) {
        if (baseFragment.getParentActivity() == null) {
            return;
        }
        if (editTextBoldCursor.length() == 0) {
            Vibrator vibrator = (Vibrator) ApplicationLoader.applicationContext.getSystemService("vibrator");
            if (vibrator != null) {
                vibrator.vibrate(200L);
            }
            AndroidUtilities.shakeView(editTextBoldCursor);
            return;
        }
        if (baseFragment instanceof ThemePreviewActivity) {
            Theme.applyPreviousTheme();
            baseFragment.lambda$onBackPressed$323();
        }
        if (themeAccent == null) {
            processCreate(editTextBoldCursor, alertDialog, baseFragment);
            return;
        }
        themeInfo.setCurrentAccentId(themeAccent.id);
        Theme.refreshThemeColors();
        Utilities.searchQueue.postRunnable(new Runnable() {
            @Override
            public final void run() {
                AlertsCreator.lambda$createThemeCreateDialog$170(EditTextBoldCursor.this, alertDialog, baseFragment);
            }
        });
    }

    public static void lambda$createTimePickerDialog$63(int r5, int r6, org.telegram.ui.Components.NumberPicker r7, org.telegram.ui.Components.NumberPicker r8, int r9, android.widget.LinearLayout r10, java.lang.Boolean r11) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.AlertsCreator.lambda$createTimePickerDialog$63(int, int, org.telegram.ui.Components.NumberPicker, org.telegram.ui.Components.NumberPicker, int, android.widget.LinearLayout, java.lang.Boolean):void");
    }

    public static String lambda$createTimePickerDialog$64(int i) {
        boolean z = LocaleController.is24HourFormat;
        String format = String.format("%02d", Integer.valueOf((i % 12 != 0 || z) ? i % (z ? 24 : 12) : 12));
        return i >= 24 ? LocaleController.formatString(R.string.BusinessHoursNextDayPicker, format) : format;
    }

    public static void lambda$createTimePickerDialog$65(Utilities.Callback callback, NumberPicker numberPicker, int i, int i2) {
        callback.run(Boolean.TRUE);
    }

    public static String lambda$createTimePickerDialog$66(int i) {
        return String.format("%02d", Integer.valueOf(i));
    }

    public static void lambda$createTimePickerDialog$67(Utilities.Callback callback, NumberPicker numberPicker, int i, int i2) {
        callback.run(Boolean.TRUE);
    }

    public static boolean lambda$createTimePickerDialog$68(View view, MotionEvent motionEvent) {
        return true;
    }

    public static void lambda$createTimePickerDialog$69(BottomSheet[] bottomSheetArr, View view) {
        bottomSheetArr[0].dismiss();
    }

    public static void lambda$createTimePickerDialog$70(Utilities.Callback callback, NumberPicker numberPicker, NumberPicker numberPicker2, DialogInterface dialogInterface) {
        callback.run(Integer.valueOf((numberPicker.getValue() * 60) + numberPicker2.getValue()));
    }

    public static void lambda$createVibrationSelectDialog$138(int[] iArr, long j, String str, long j2, AlertDialog.Builder builder, Runnable runnable, View view) {
        iArr[0] = ((Integer) view.getTag()).intValue();
        SharedPreferences.Editor edit = MessagesController.getNotificationsSettings(UserConfig.selectedAccount).edit();
        if (j != 0) {
            int i = iArr[0];
            if (i == 0) {
                edit.putInt(str, 0);
            } else if (i == 1) {
                edit.putInt(str, 1);
            } else if (i == 2) {
                edit.putInt(str, 3);
            } else if (i == 3) {
                edit.putInt(str, 2);
            }
            NotificationsController.getInstance(UserConfig.selectedAccount).deleteNotificationChannel(j, j2);
        } else {
            int i2 = iArr[0];
            if (i2 == 0) {
                edit.putInt(str, 2);
            } else if (i2 == 1) {
                edit.putInt(str, 0);
            } else if (i2 == 2) {
                edit.putInt(str, 1);
            } else if (i2 == 3) {
                edit.putInt(str, 3);
            } else if (i2 == 4) {
                edit.putInt(str, 4);
            }
            if (str.equals("vibrate_channel")) {
                NotificationsController.getInstance(UserConfig.selectedAccount).deleteNotificationChannelGlobal(2);
            } else if (str.equals("vibrate_group")) {
                NotificationsController.getInstance(UserConfig.selectedAccount).deleteNotificationChannelGlobal(0);
            } else if (str.equals("vibrate_react")) {
                NotificationsController.getInstance(UserConfig.selectedAccount).deleteNotificationChannelGlobal(4);
            } else {
                NotificationsController.getInstance(UserConfig.selectedAccount).deleteNotificationChannelGlobal(1);
            }
        }
        edit.commit();
        builder.getDismissRunnable().run();
        if (runnable != null) {
            runnable.run();
        }
    }

    public static void lambda$createWebViewPermissionsRequestDialog$3(boolean z, Context context, AtomicBoolean atomicBoolean, Consumer consumer, AlertDialog alertDialog, int i) {
        if (!z) {
            atomicBoolean.set(true);
            consumer.accept(Boolean.TRUE);
            return;
        }
        try {
            Intent intent = new Intent("android.settings.APPLICATION_DETAILS_SETTINGS");
            intent.setData(Uri.parse("package:" + ApplicationLoader.applicationContext.getPackageName()));
            context.startActivity(intent);
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    public static void lambda$createWebViewPermissionsRequestDialog$4(AtomicBoolean atomicBoolean, Consumer consumer, AlertDialog alertDialog, int i) {
        atomicBoolean.set(true);
        consumer.accept(Boolean.FALSE);
    }

    public static void lambda$createWebViewPermissionsRequestDialog$5(AtomicBoolean atomicBoolean, Consumer consumer, DialogInterface dialogInterface) {
        if (atomicBoolean.get()) {
            return;
        }
        consumer.accept(Boolean.FALSE);
    }

    public static void lambda$performAskAQuestion$22(SharedPreferences sharedPreferences, TLRPC.TL_help_support tL_help_support, AlertDialog alertDialog, int i, BaseFragment baseFragment) {
        SharedPreferences.Editor edit = sharedPreferences.edit();
        edit.putLong("support_id2", tL_help_support.user.id);
        SerializedData serializedData = new SerializedData();
        tL_help_support.user.serializeToStream(serializedData);
        edit.putString("support_user", Base64.encodeToString(serializedData.toByteArray(), 0));
        edit.commit();
        serializedData.cleanup();
        try {
            alertDialog.dismiss();
        } catch (Exception e) {
            FileLog.e(e);
        }
        ArrayList arrayList = new ArrayList();
        arrayList.add(tL_help_support.user);
        MessagesStorage.getInstance(i).putUsersAndChats(arrayList, null, true, true);
        MessagesController.getInstance(i).putUser(tL_help_support.user, false);
        Bundle bundle = new Bundle();
        bundle.putLong("user_id", tL_help_support.user.id);
        baseFragment.presentFragment(new ChatActivity(bundle));
    }

    public static void lambda$performAskAQuestion$23(AlertDialog alertDialog) {
        try {
            alertDialog.dismiss();
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    public static void lambda$performAskAQuestion$24(final SharedPreferences sharedPreferences, final AlertDialog alertDialog, final int i, final BaseFragment baseFragment, TLObject tLObject, TLRPC.TL_error tL_error) {
        if (tL_error != null) {
            AndroidUtilities.runOnUIThread(new Runnable() {
                @Override
                public final void run() {
                    AlertsCreator.lambda$performAskAQuestion$23(AlertDialog.this);
                }
            });
        } else {
            final TLRPC.TL_help_support tL_help_support = (TLRPC.TL_help_support) tLObject;
            AndroidUtilities.runOnUIThread(new Runnable() {
                @Override
                public final void run() {
                    AlertsCreator.lambda$performAskAQuestion$22(sharedPreferences, tL_help_support, alertDialog, i, baseFragment);
                }
            });
        }
    }

    public static void lambda$showAddUserAlert$133(BaseFragment baseFragment, AlertDialog alertDialog, int i) {
        MessagesController.getInstance(baseFragment.getCurrentAccount()).openByUserName("spambot", baseFragment, 1);
    }

    public static void lambda$showBlockReportSpamAlert$15(CheckBoxCell[] checkBoxCellArr, View view) {
        checkBoxCellArr[((Integer) view.getTag()).intValue()].setChecked(!checkBoxCellArr[r2.intValue()].isChecked(), true);
    }

    public static void lambda$showBlockReportSpamAlert$16(TLRPC.User user, AccountInstance accountInstance, CheckBoxCell[] checkBoxCellArr, long j, TLRPC.Chat chat, TLRPC.EncryptedChat encryptedChat, boolean z, MessagesStorage.IntCallback intCallback, AlertDialog alertDialog, int i) {
        CheckBoxCell checkBoxCell;
        if (user != null) {
            accountInstance.getMessagesController().blockPeer(user.id);
        }
        if (checkBoxCellArr == null || ((checkBoxCell = checkBoxCellArr[0]) != null && checkBoxCell.isChecked())) {
            accountInstance.getMessagesController().reportSpam(j, user, chat, encryptedChat, chat != null && z);
        }
        if (checkBoxCellArr != null && !checkBoxCellArr[1].isChecked()) {
            intCallback.run(0);
            return;
        }
        if (chat == null || ChatObject.isNotInChat(chat)) {
            accountInstance.getMessagesController().deleteDialog(j, 0);
        } else {
            accountInstance.getMessagesController().deleteParticipantFromChat(-j, accountInstance.getMessagesController().getUser(Long.valueOf(accountInstance.getUserConfig().getClientUserId())));
        }
        intCallback.run(1);
    }

    public static void lambda$showBlockReportSpamReplyAlert$11(Runnable runnable, DialogInterface dialogInterface) {
        if (runnable != null) {
            runnable.run();
        }
    }

    public static void lambda$showBlockReportSpamReplyAlert$12(CheckBoxCell[] checkBoxCellArr, View view) {
        checkBoxCellArr[((Integer) view.getTag()).intValue()].setChecked(!checkBoxCellArr[r2.intValue()].isChecked(), true);
    }

    public static void lambda$showBlockReportSpamReplyAlert$13(AccountInstance accountInstance, TLObject tLObject, TLRPC.TL_error tL_error) {
        if (tLObject instanceof TLRPC.Updates) {
            accountInstance.getMessagesController().processUpdates((TLRPC.Updates) tLObject, false);
        }
    }

    public static void lambda$showBlockReportSpamReplyAlert$14(TLRPC.User user, final AccountInstance accountInstance, ChatActivity chatActivity, TLRPC.Chat chat, MessageObject messageObject, CheckBoxCell[] checkBoxCellArr, Theme.ResourcesProvider resourcesProvider, AlertDialog alertDialog, int i) {
        UndoView undoView;
        if (user != null) {
            accountInstance.getMessagesStorage().deleteUserChatHistory(chatActivity.getDialogId(), user.id);
        } else {
            accountInstance.getMessagesStorage().deleteUserChatHistory(chatActivity.getDialogId(), -chat.id);
        }
        TLRPC.TL_contacts_blockFromReplies tL_contacts_blockFromReplies = new TLRPC.TL_contacts_blockFromReplies();
        tL_contacts_blockFromReplies.msg_id = messageObject.getId();
        tL_contacts_blockFromReplies.delete_message = true;
        tL_contacts_blockFromReplies.delete_history = true;
        if (checkBoxCellArr[0].isChecked()) {
            tL_contacts_blockFromReplies.report_spam = true;
            if (chatActivity.getParentActivity() != null && (undoView = chatActivity.getUndoView()) != null) {
                undoView.showWithAction(0L, 74, (Runnable) null);
            }
        }
        accountInstance.getConnectionsManager().sendRequest(tL_contacts_blockFromReplies, new RequestDelegate() {
            @Override
            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                AlertsCreator.lambda$showBlockReportSpamReplyAlert$13(AccountInstance.this, tLObject, tL_error);
            }
        });
    }

    public static void lambda$showCustomNotificationsDialog$17(long r18, int r20, boolean r21, int r22, org.telegram.messenger.MessagesStorage.IntCallback r23, int r24, org.telegram.ui.ActionBar.BaseFragment r25, java.util.ArrayList r26, java.util.ArrayList r27, org.telegram.messenger.MessagesStorage.IntCallback r28, org.telegram.ui.ActionBar.AlertDialog.Builder r29, android.view.View r30) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.AlertsCreator.lambda$showCustomNotificationsDialog$17(long, int, boolean, int, org.telegram.messenger.MessagesStorage$IntCallback, int, org.telegram.ui.ActionBar.BaseFragment, java.util.ArrayList, java.util.ArrayList, org.telegram.messenger.MessagesStorage$IntCallback, org.telegram.ui.ActionBar.AlertDialog$Builder, android.view.View):void");
    }

    public static void lambda$showOpenUrlAlert$19(BaseFragment baseFragment, String str, long j, boolean z, Browser.Progress progress) {
        Browser.openUrl(baseFragment.getParentActivity(), Uri.parse(str), j == 0, z, progress);
    }

    public static boolean lambda$showPopupMenu$172(ActionBarPopupWindow actionBarPopupWindow, View view, int i, KeyEvent keyEvent) {
        if (i != 82 || keyEvent.getRepeatCount() != 0 || keyEvent.getAction() != 1 || !actionBarPopupWindow.isShowing()) {
            return false;
        }
        actionBarPopupWindow.dismiss();
        return true;
    }

    public static boolean lambda$showPopupMenu$173(ActionBarPopupWindow actionBarPopupWindow, android.graphics.Rect rect, View view, MotionEvent motionEvent) {
        if (motionEvent.getActionMasked() != 0 || actionBarPopupWindow == null || !actionBarPopupWindow.isShowing()) {
            return false;
        }
        view.getHitRect(rect);
        if (rect.contains((int) motionEvent.getX(), (int) motionEvent.getY())) {
            return false;
        }
        actionBarPopupWindow.dismiss();
        return false;
    }

    public static void lambda$showSecretLocationAlert$18(ArrayList arrayList, Runnable runnable, AlertDialog.Builder builder, View view) {
        SharedConfig.setSecretMapPreviewType(((Integer) arrayList.get(((Integer) view.getTag()).intValue())).intValue());
        if (runnable != null) {
            runnable.run();
        }
        builder.getDismissRunnable().run();
    }

    public static void lambda$showUpdateAppAlert$7(Context context, AlertDialog alertDialog, int i) {
        Browser.openUrl(context, BuildVars.PLAYSTORE_APP_URL);
    }

    public static void performAskAQuestion(final BaseFragment baseFragment) {
        String string;
        final int currentAccount = baseFragment.getCurrentAccount();
        final SharedPreferences mainSettings = MessagesController.getMainSettings(currentAccount);
        long prefIntOrLong = AndroidUtilities.getPrefIntOrLong(mainSettings, "support_id2", 0L);
        TLRPC.User user = null;
        if (prefIntOrLong != 0) {
            TLRPC.User user2 = MessagesController.getInstance(currentAccount).getUser(Long.valueOf(prefIntOrLong));
            if (user2 == null && (string = mainSettings.getString("support_user", null)) != null) {
                try {
                    byte[] decode = Base64.decode(string, 0);
                    if (decode != null) {
                        SerializedData serializedData = new SerializedData(decode);
                        TLRPC.User TLdeserialize = TLRPC.User.TLdeserialize(serializedData, serializedData.readInt32(false), false);
                        if (TLdeserialize != null && TLdeserialize.id == 333000) {
                            TLdeserialize = null;
                        }
                        serializedData.cleanup();
                        user = TLdeserialize;
                    }
                } catch (Exception e) {
                    FileLog.e(e);
                }
            }
            user = user2;
        }
        if (user == null) {
            final AlertDialog alertDialog = new AlertDialog(baseFragment.getParentActivity(), 3);
            alertDialog.setCanCancel(false);
            alertDialog.show();
            ConnectionsManager.getInstance(currentAccount).sendRequest(new TLRPC.TL_help_getSupport(), new RequestDelegate() {
                @Override
                public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                    AlertsCreator.lambda$performAskAQuestion$24(mainSettings, alertDialog, currentAccount, baseFragment, tLObject, tL_error);
                }
            });
            return;
        }
        MessagesController.getInstance(currentAccount).putUser(user, true);
        Bundle bundle = new Bundle();
        bundle.putLong("user_id", user.id);
        baseFragment.presentFragment(new ChatActivity(bundle));
    }

    public static void processCreate(EditTextBoldCursor editTextBoldCursor, AlertDialog alertDialog, BaseFragment baseFragment) {
        if (baseFragment == null || baseFragment.getParentActivity() == null) {
            return;
        }
        AndroidUtilities.hideKeyboard(editTextBoldCursor);
        Theme.ThemeInfo createNewTheme = Theme.createNewTheme(editTextBoldCursor.getText().toString());
        NotificationCenter.getGlobalInstance().lambda$postNotificationNameOnUIThread$1(NotificationCenter.themeListUpdated, new Object[0]);
        new ThemeEditorView().show(baseFragment.getParentActivity(), createNewTheme);
        alertDialog.dismiss();
        SharedPreferences globalMainSettings = MessagesController.getGlobalMainSettings();
        if (globalMainSettings.getBoolean("themehint", false)) {
            return;
        }
        globalMainSettings.edit().putBoolean("themehint", true).commit();
        try {
            Toast.makeText(baseFragment.getParentActivity(), LocaleController.getString(R.string.CreateNewThemeHelp), 1).show();
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    public static android.app.Dialog processError(int r20, org.telegram.tgnet.TLRPC.TL_error r21, org.telegram.ui.ActionBar.BaseFragment r22, org.telegram.tgnet.TLObject r23, java.lang.Object... r24) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.AlertsCreator.processError(int, org.telegram.tgnet.TLRPC$TL_error, org.telegram.ui.ActionBar.BaseFragment, org.telegram.tgnet.TLObject, java.lang.Object[]):android.app.Dialog");
    }

    public static void showAddUserAlert(String str, final BaseFragment baseFragment, boolean z, TLObject tLObject) {
        int i;
        String string;
        if (str == null || baseFragment == null || baseFragment.getParentActivity() == null) {
            return;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(baseFragment.getParentActivity());
        builder.setTitle(LocaleController.getString(R.string.AppName));
        char c = 65535;
        switch (str.hashCode()) {
            case -2120721660:
                if (str.equals("CHANNELS_ADMIN_LOCATED_TOO_MUCH")) {
                    c = 0;
                    break;
                }
                break;
            case -2012133105:
                if (str.equals("CHANNELS_ADMIN_PUBLIC_TOO_MUCH")) {
                    c = 1;
                    break;
                }
                break;
            case -1763467626:
                if (str.equals("USERS_TOO_FEW")) {
                    c = 2;
                    break;
                }
                break;
            case -538116776:
                if (str.equals("USER_BLOCKED")) {
                    c = 3;
                    break;
                }
                break;
            case -512775857:
                if (str.equals("USER_RESTRICTED")) {
                    c = 4;
                    break;
                }
                break;
            case -454039871:
                if (str.equals("PEER_FLOOD")) {
                    c = 5;
                    break;
                }
                break;
            case -420079733:
                if (str.equals("BOTS_TOO_MUCH")) {
                    c = 6;
                    break;
                }
                break;
            case 98635865:
                if (str.equals("USER_KICKED")) {
                    c = 7;
                    break;
                }
                break;
            case 517420851:
                if (str.equals("USER_BOT")) {
                    c = '\b';
                    break;
                }
                break;
            case 845559454:
                if (str.equals("YOU_BLOCKED_USER")) {
                    c = '\t';
                    break;
                }
                break;
            case 916342611:
                if (str.equals("USER_ADMIN_INVALID")) {
                    c = '\n';
                    break;
                }
                break;
            case 1047173446:
                if (str.equals("CHAT_ADMIN_BAN_REQUIRED")) {
                    c = 11;
                    break;
                }
                break;
            case 1167301807:
                if (str.equals("USERS_TOO_MUCH")) {
                    c = '\f';
                    break;
                }
                break;
            case 1227003815:
                if (str.equals("USER_ID_INVALID")) {
                    c = '\r';
                    break;
                }
                break;
            case 1253103379:
                if (str.equals("ADMINS_TOO_MUCH")) {
                    c = 14;
                    break;
                }
                break;
            case 1355367367:
                if (str.equals("CHANNELS_TOO_MUCH")) {
                    c = 15;
                    break;
                }
                break;
            case 1377621075:
                if (str.equals("USER_CHANNELS_TOO_MUCH")) {
                    c = 16;
                    break;
                }
                break;
            case 1623167701:
                if (str.equals("USER_NOT_MUTUAL_CONTACT")) {
                    c = 17;
                    break;
                }
                break;
            case 1754587486:
                if (str.equals("CHAT_ADMIN_INVITE_REQUIRED")) {
                    c = 18;
                    break;
                }
                break;
            case 1916725894:
                if (str.equals("USER_PRIVACY_RESTRICTED")) {
                    c = 19;
                    break;
                }
                break;
            case 1965565720:
                if (str.equals("USER_ALREADY_PARTICIPANT")) {
                    c = 20;
                    break;
                }
                break;
        }
        switch (c) {
            case 0:
                i = R.string.LocatedChannelsTooMuch;
                string = LocaleController.getString(i);
                builder.setMessage(string);
                break;
            case 1:
                i = R.string.PublicChannelsTooMuch;
                string = LocaleController.getString(i);
                builder.setMessage(string);
                break;
            case 2:
                i = R.string.CreateGroupError;
                string = LocaleController.getString(i);
                builder.setMessage(string);
                break;
            case 3:
            case '\b':
            case '\r':
                i = z ? R.string.ChannelUserCantAdd : R.string.GroupUserCantAdd;
                string = LocaleController.getString(i);
                builder.setMessage(string);
                break;
            case 4:
                i = R.string.UserRestricted;
                string = LocaleController.getString(i);
                builder.setMessage(string);
                break;
            case 5:
                builder.setMessage(LocaleController.getString(R.string.NobodyLikesSpam2));
                builder.setNegativeButton(LocaleController.getString(R.string.MoreInfo), new AlertDialog.OnButtonClickListener() {
                    @Override
                    public final void onClick(AlertDialog alertDialog, int i2) {
                        AlertsCreator.lambda$showAddUserAlert$133(BaseFragment.this, alertDialog, i2);
                    }
                });
                break;
            case 6:
                i = z ? R.string.ChannelUserCantBot : R.string.GroupUserCantBot;
                string = LocaleController.getString(i);
                builder.setMessage(string);
                break;
            case 7:
            case 11:
                i = tLObject instanceof TLRPC.TL_channels_inviteToChannel ? R.string.AddUserErrorBlacklisted : R.string.AddAdminErrorBlacklisted;
                string = LocaleController.getString(i);
                builder.setMessage(string);
                break;
            case '\t':
                i = R.string.YouBlockedUser;
                string = LocaleController.getString(i);
                builder.setMessage(string);
                break;
            case '\n':
                i = R.string.AddBannedErrorAdmin;
                string = LocaleController.getString(i);
                builder.setMessage(string);
                break;
            case '\f':
                i = z ? R.string.ChannelUserAddLimit : R.string.GroupUserAddLimit;
                string = LocaleController.getString(i);
                builder.setMessage(string);
                break;
            case 14:
                i = z ? R.string.ChannelUserCantAdmin : R.string.GroupUserCantAdmin;
                string = LocaleController.getString(i);
                builder.setMessage(string);
                break;
            case 15:
                builder.setTitle(LocaleController.getString(R.string.ChannelTooMuchTitle));
                i = tLObject instanceof TLRPC.TL_channels_createChannel ? R.string.ChannelTooMuch : R.string.ChannelTooMuchJoin;
                string = LocaleController.getString(i);
                builder.setMessage(string);
                break;
            case 16:
                builder.setTitle(LocaleController.getString(R.string.ChannelTooMuchTitle));
                i = R.string.UserChannelTooMuchJoin;
                string = LocaleController.getString(i);
                builder.setMessage(string);
                break;
            case 17:
                i = z ? R.string.ChannelUserLeftError : R.string.GroupUserLeftError;
                string = LocaleController.getString(i);
                builder.setMessage(string);
                break;
            case 18:
                i = R.string.AddAdminErrorNotAMember;
                string = LocaleController.getString(i);
                builder.setMessage(string);
                break;
            case 19:
                i = z ? R.string.InviteToChannelError : R.string.InviteToGroupError;
                string = LocaleController.getString(i);
                builder.setMessage(string);
                break;
            case 20:
                builder.setTitle(LocaleController.getString(R.string.VoipGroupVoiceChat));
                i = R.string.VoipGroupInviteAlreadyParticipant;
                string = LocaleController.getString(i);
                builder.setMessage(string);
                break;
            default:
                string = LocaleController.getString(R.string.ErrorOccurred) + "\n" + str;
                builder.setMessage(string);
                break;
        }
        builder.setPositiveButton(LocaleController.getString(R.string.OK), null);
        baseFragment.showDialog(builder.create(), true, null);
    }

    public static void showBlockReportSpamAlert(org.telegram.ui.ActionBar.BaseFragment r18, final long r19, final org.telegram.tgnet.TLRPC.User r21, final org.telegram.tgnet.TLRPC.Chat r22, final org.telegram.tgnet.TLRPC.EncryptedChat r23, final boolean r24, org.telegram.tgnet.TLRPC.ChatFull r25, final org.telegram.messenger.MessagesStorage.IntCallback r26, org.telegram.ui.ActionBar.Theme.ResourcesProvider r27) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.AlertsCreator.showBlockReportSpamAlert(org.telegram.ui.ActionBar.BaseFragment, long, org.telegram.tgnet.TLRPC$User, org.telegram.tgnet.TLRPC$Chat, org.telegram.tgnet.TLRPC$EncryptedChat, boolean, org.telegram.tgnet.TLRPC$ChatFull, org.telegram.messenger.MessagesStorage$IntCallback, org.telegram.ui.ActionBar.Theme$ResourcesProvider):void");
    }

    public static void showBlockReportSpamReplyAlert(final ChatActivity chatActivity, final MessageObject messageObject, long j, final Theme.ResourcesProvider resourcesProvider, final Runnable runnable) {
        if (chatActivity == null || chatActivity.getParentActivity() == null || messageObject == null) {
            return;
        }
        final AccountInstance accountInstance = chatActivity.getAccountInstance();
        TLRPC.User user = j > 0 ? accountInstance.getMessagesController().getUser(Long.valueOf(j)) : null;
        TLRPC.Chat chat = j < 0 ? accountInstance.getMessagesController().getChat(Long.valueOf(-j)) : null;
        if (user == null && chat == null) {
            return;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(chatActivity.getParentActivity(), resourcesProvider);
        builder.setDimEnabled(runnable == null);
        builder.setOnPreDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public final void onDismiss(DialogInterface dialogInterface) {
                AlertsCreator.lambda$showBlockReportSpamReplyAlert$11(runnable, dialogInterface);
            }
        });
        builder.setTitle(LocaleController.getString(R.string.BlockUser));
        int i = R.string.BlockUserReplyAlert;
        builder.setMessage(AndroidUtilities.replaceTags(user != null ? LocaleController.formatString("BlockUserReplyAlert", i, UserObject.getFirstName(user)) : LocaleController.formatString("BlockUserReplyAlert", i, chat.title)));
        LinearLayout linearLayout = new LinearLayout(chatActivity.getParentActivity());
        linearLayout.setOrientation(1);
        final CheckBoxCell[] checkBoxCellArr = {new CheckBoxCell(chatActivity.getParentActivity(), 1, resourcesProvider)};
        checkBoxCellArr[0].setBackgroundDrawable(Theme.getSelectorDrawable(false));
        checkBoxCellArr[0].setTag(0);
        checkBoxCellArr[0].setText(LocaleController.getString(R.string.DeleteReportSpam), "", true, false);
        checkBoxCellArr[0].setPadding(LocaleController.isRTL ? AndroidUtilities.dp(16.0f) : AndroidUtilities.dp(8.0f), 0, LocaleController.isRTL ? AndroidUtilities.dp(8.0f) : AndroidUtilities.dp(16.0f), 0);
        linearLayout.addView(checkBoxCellArr[0], LayoutHelper.createLinear(-1, -2));
        checkBoxCellArr[0].setOnClickListener(new View.OnClickListener() {
            @Override
            public final void onClick(View view) {
                AlertsCreator.lambda$showBlockReportSpamReplyAlert$12(checkBoxCellArr, view);
            }
        });
        builder.setView(linearLayout);
        final TLRPC.User user2 = user;
        final TLRPC.Chat chat2 = chat;
        builder.setPositiveButton(LocaleController.getString(R.string.BlockAndDeleteReplies), new AlertDialog.OnButtonClickListener() {
            @Override
            public final void onClick(AlertDialog alertDialog, int i2) {
                AlertsCreator.lambda$showBlockReportSpamReplyAlert$14(TLRPC.User.this, accountInstance, chatActivity, chat2, messageObject, checkBoxCellArr, resourcesProvider, alertDialog, i2);
            }
        });
        builder.setNegativeButton(LocaleController.getString(R.string.Cancel), null);
        AlertDialog create = builder.create();
        chatActivity.showDialog(create);
        TextView textView = (TextView) create.getButton(-1);
        if (textView != null) {
            textView.setTextColor(Theme.getColor(Theme.key_text_RedBold));
        }
    }

    public static void showChatWithAdmin(BaseFragment baseFragment, TLRPC.User user, String str, boolean z, int i) {
        if (baseFragment.getParentActivity() == null) {
            return;
        }
        BottomSheet.Builder builder = new BottomSheet.Builder(baseFragment.getParentActivity());
        builder.setTitle(LocaleController.getString(z ? R.string.ChatWithAdminChannelTitle : R.string.ChatWithAdminGroupTitle), true);
        LinearLayout linearLayout = new LinearLayout(baseFragment.getParentActivity());
        linearLayout.setOrientation(1);
        TextView textView = new TextView(baseFragment.getParentActivity());
        linearLayout.addView(textView, LayoutHelper.createLinear(-1, -1, 0, 21, 0, 21, 8));
        textView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
        textView.setTextSize(1, 16.0f);
        textView.setText(AndroidUtilities.replaceTags(LocaleController.formatString("ChatWithAdminMessage", R.string.ChatWithAdminMessage, str, LocaleController.formatDateAudio(i, false))));
        TextView textView2 = new TextView(baseFragment.getParentActivity());
        textView2.setPadding(AndroidUtilities.dp(34.0f), 0, AndroidUtilities.dp(34.0f), 0);
        textView2.setGravity(17);
        textView2.setTextSize(1, 14.0f);
        textView2.setTypeface(AndroidUtilities.bold());
        textView2.setText(LocaleController.getString(R.string.IUnderstand));
        textView2.setTextColor(Theme.getColor(Theme.key_featuredStickers_buttonText));
        textView2.setBackground(Theme.createSimpleSelectorRoundRectDrawable(AndroidUtilities.dp(8.0f), Theme.getColor(Theme.key_featuredStickers_addButton), Theme.getColor(Theme.key_featuredStickers_addButtonPressed)));
        linearLayout.addView(textView2, LayoutHelper.createLinear(-1, 48, 0, 16, 12, 16, 8));
        builder.setCustomView(linearLayout);
        final BottomSheet show = builder.show();
        textView2.setOnClickListener(new View.OnClickListener() {
            @Override
            public final void onClick(View view) {
                BottomSheet.this.dismiss();
            }
        });
    }

    public static void showCustomNotificationsDialog(BaseFragment baseFragment, long j, int i, int i2, ArrayList arrayList, ArrayList arrayList2, int i3, MessagesStorage.IntCallback intCallback) {
        showCustomNotificationsDialog(baseFragment, j, i, i2, arrayList, arrayList2, i3, intCallback, null);
    }

    public static void showCustomNotificationsDialog(final BaseFragment baseFragment, final long j, final int i, final int i2, final ArrayList arrayList, final ArrayList arrayList2, final int i3, final MessagesStorage.IntCallback intCallback, final MessagesStorage.IntCallback intCallback2) {
        PorterDuffColorFilter porterDuffColorFilter;
        int i4;
        final AlertDialog.Builder builder;
        Object obj;
        Drawable drawable;
        ?? r12 = 0;
        ?? r11 = 1;
        if (baseFragment == null || baseFragment.getParentActivity() == null) {
            return;
        }
        final boolean isGlobalNotificationsEnabled = NotificationsController.getInstance(i3).isGlobalNotificationsEnabled(j, false, false);
        String string = LocaleController.getString(R.string.NotificationsTurnOn);
        int i5 = R.string.MuteFor;
        Drawable drawable2 = null;
        String[] strArr = {string, LocaleController.formatString("MuteFor", i5, LocaleController.formatPluralString("Hours", 1, new Object[0])), LocaleController.formatString("MuteFor", i5, LocaleController.formatPluralString("Days", 2, new Object[0])), (j == 0 && (baseFragment instanceof NotificationsCustomSettingsActivity)) ? null : LocaleController.getString(R.string.NotificationsCustomize), LocaleController.getString(R.string.NotificationsTurnOff)};
        int[] iArr = {R.drawable.notifications_on, R.drawable.notifications_mute1h, R.drawable.notifications_mute2d, R.drawable.notifications_settings, R.drawable.notifications_off};
        LinearLayout linearLayout = new LinearLayout(baseFragment.getParentActivity());
        linearLayout.setOrientation(1);
        AlertDialog.Builder builder2 = new AlertDialog.Builder(baseFragment.getParentActivity());
        int i6 = 0;
        View view = linearLayout;
        while (i6 < 5) {
            if (strArr[i6] == null) {
                i4 = i6;
                builder = builder2;
                obj = view;
                drawable = drawable2;
            } else {
                ?? textView = new TextView(baseFragment.getParentActivity());
                Drawable drawable3 = baseFragment.getParentActivity().getResources().getDrawable(iArr[i6]);
                if (i6 == 4) {
                    textView.setTextColor(Theme.getColor(Theme.key_text_RedBold));
                    porterDuffColorFilter = new PorterDuffColorFilter(Theme.getColor(Theme.key_text_RedRegular), PorterDuff.Mode.MULTIPLY);
                } else {
                    textView.setTextColor(Theme.getColor(Theme.key_dialogTextBlack));
                    porterDuffColorFilter = new PorterDuffColorFilter(Theme.getColor(Theme.key_dialogIcon), PorterDuff.Mode.MULTIPLY);
                }
                drawable3.setColorFilter(porterDuffColorFilter);
                textView.setTextSize(r11, 16.0f);
                textView.setLines(r11);
                textView.setMaxLines(r11);
                textView.setCompoundDrawablesWithIntrinsicBounds(drawable3, drawable2, drawable2, drawable2);
                textView.setTag(Integer.valueOf(i6));
                textView.setBackgroundDrawable(Theme.getSelectorDrawable(r12));
                textView.setPadding(AndroidUtilities.dp(24.0f), r12, AndroidUtilities.dp(24.0f), r12);
                textView.setSingleLine(r11);
                textView.setGravity(19);
                textView.setCompoundDrawablePadding(AndroidUtilities.dp(26.0f));
                textView.setText(strArr[i6]);
                view.addView(textView, LayoutHelper.createLinear(-1, 48, 51));
                i4 = i6;
                builder = builder2;
                obj = view;
                drawable = drawable2;
                textView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public final void onClick(View view2) {
                        AlertsCreator.lambda$showCustomNotificationsDialog$17(j, i3, isGlobalNotificationsEnabled, i, intCallback2, i2, baseFragment, arrayList, arrayList2, intCallback, builder, view2);
                    }
                });
            }
            i6 = i4 + 1;
            view = obj;
            builder2 = builder;
            drawable2 = drawable;
            r11 = 1;
            r12 = 0;
        }
        AlertDialog.Builder builder3 = builder2;
        builder3.setTitle(LocaleController.getString(R.string.Notifications));
        builder3.setView(view);
        baseFragment.showDialog(builder3.create());
    }

    public static void showFloodWaitAlert(String str, BaseFragment baseFragment) {
        if (str == null || !str.startsWith("FLOOD_WAIT") || baseFragment == null || baseFragment.getParentActivity() == null) {
            return;
        }
        int intValue = Utilities.parseInt((CharSequence) str).intValue();
        String formatPluralString = intValue < 60 ? LocaleController.formatPluralString("Seconds", intValue, new Object[0]) : LocaleController.formatPluralString("Minutes", intValue / 60, new Object[0]);
        AlertDialog.Builder builder = new AlertDialog.Builder(baseFragment.getParentActivity());
        builder.setTitle(LocaleController.getString(R.string.AppName));
        builder.setMessage(LocaleController.formatString("FloodWaitTime", R.string.FloodWaitTime, formatPluralString));
        builder.setPositiveButton(LocaleController.getString(R.string.OK), null);
        baseFragment.showDialog(builder.create(), true, null);
    }

    public static void showOpenUrlAlert(BaseFragment baseFragment, String str, boolean z, boolean z2) {
        showOpenUrlAlert(baseFragment, str, z, true, z2, false, null, null);
    }

    public static void showOpenUrlAlert(BaseFragment baseFragment, String str, boolean z, boolean z2, Theme.ResourcesProvider resourcesProvider) {
        showOpenUrlAlert(baseFragment, str, z, true, z2, false, null, resourcesProvider);
    }

    public static void showOpenUrlAlert(BaseFragment baseFragment, String str, boolean z, boolean z2, boolean z3, Browser.Progress progress, Theme.ResourcesProvider resourcesProvider) {
        showOpenUrlAlert(baseFragment, str, z, z2, z3, false, progress, resourcesProvider);
    }

    public static void showOpenUrlAlert(final org.telegram.ui.ActionBar.BaseFragment r22, final java.lang.String r23, boolean r24, final boolean r25, boolean r26, boolean r27, final org.telegram.messenger.browser.Browser.Progress r28, org.telegram.ui.ActionBar.Theme.ResourcesProvider r29) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.AlertsCreator.showOpenUrlAlert(org.telegram.ui.ActionBar.BaseFragment, java.lang.String, boolean, boolean, boolean, boolean, org.telegram.messenger.browser.Browser$Progress, org.telegram.ui.ActionBar.Theme$ResourcesProvider):void");
    }

    public static ActionBarPopupWindow showPopupMenu(ActionBarPopupWindow.ActionBarPopupWindowLayout actionBarPopupWindowLayout, View view, int i, int i2) {
        final android.graphics.Rect rect = new android.graphics.Rect();
        final ActionBarPopupWindow actionBarPopupWindow = new ActionBarPopupWindow(actionBarPopupWindowLayout, -2, -2);
        actionBarPopupWindow.setAnimationStyle(R.style.PopupAnimation);
        actionBarPopupWindow.setAnimationEnabled(true);
        actionBarPopupWindow.setOutsideTouchable(true);
        actionBarPopupWindow.setClippingEnabled(true);
        actionBarPopupWindow.setInputMethodMode(2);
        actionBarPopupWindow.setSoftInputMode(0);
        actionBarPopupWindow.setFocusable(true);
        actionBarPopupWindowLayout.setFocusableInTouchMode(true);
        actionBarPopupWindowLayout.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public final boolean onKey(View view2, int i3, KeyEvent keyEvent) {
                boolean lambda$showPopupMenu$172;
                lambda$showPopupMenu$172 = AlertsCreator.lambda$showPopupMenu$172(ActionBarPopupWindow.this, view2, i3, keyEvent);
                return lambda$showPopupMenu$172;
            }
        });
        actionBarPopupWindowLayout.measure(View.MeasureSpec.makeMeasureSpec(AndroidUtilities.displaySize.x - AndroidUtilities.dp(40.0f), Integer.MIN_VALUE), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.displaySize.y, Integer.MIN_VALUE));
        actionBarPopupWindow.showAsDropDown(view, i, i2);
        actionBarPopupWindowLayout.updateRadialSelectors();
        ActionBarPopupWindow.startAnimation(actionBarPopupWindowLayout);
        actionBarPopupWindowLayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public final boolean onTouch(View view2, MotionEvent motionEvent) {
                boolean lambda$showPopupMenu$173;
                lambda$showPopupMenu$173 = AlertsCreator.lambda$showPopupMenu$173(ActionBarPopupWindow.this, rect, view2, motionEvent);
                return lambda$showPopupMenu$173;
            }
        });
        return actionBarPopupWindow;
    }

    public static AlertDialog showSecretLocationAlert(Context context, int i, final Runnable runnable, boolean z, Theme.ResourcesProvider resourcesProvider) {
        ArrayList arrayList = new ArrayList();
        final ArrayList arrayList2 = new ArrayList();
        int i2 = MessagesController.getInstance(i).availableMapProviders;
        if ((i2 & 1) != 0) {
            arrayList.add(LocaleController.getString(R.string.MapPreviewProviderTelegram));
            arrayList2.add(0);
        }
        if ((i2 & 2) != 0) {
            arrayList.add(LocaleController.getString(R.string.MapPreviewProviderGoogle));
            arrayList2.add(1);
        }
        if ((i2 & 4) != 0) {
            arrayList.add(LocaleController.getString(R.string.MapPreviewProviderYandex));
            arrayList2.add(3);
        }
        arrayList.add(LocaleController.getString(R.string.MapPreviewProviderNobody));
        arrayList2.add(2);
        final AlertDialog.Builder builder = new AlertDialog.Builder(context, resourcesProvider);
        builder.setTitle(LocaleController.getString(R.string.MapPreviewProviderTitle));
        LinearLayout linearLayout = new LinearLayout(context);
        linearLayout.setOrientation(1);
        builder.setView(linearLayout);
        for (int i3 = 0; i3 < arrayList.size(); i3++) {
            RadioColorCell radioColorCell = new RadioColorCell(context, resourcesProvider);
            radioColorCell.setPadding(AndroidUtilities.dp(4.0f), 0, AndroidUtilities.dp(4.0f), 0);
            radioColorCell.setTag(Integer.valueOf(i3));
            radioColorCell.setCheckColor(Theme.getColor(Theme.key_radioBackground), Theme.getColor(Theme.key_dialogRadioBackgroundChecked));
            radioColorCell.setTextAndValue((CharSequence) arrayList.get(i3), SharedConfig.mapPreviewType == ((Integer) arrayList2.get(i3)).intValue());
            radioColorCell.setBackground(Theme.createSelectorDrawable(Theme.getColor(Theme.key_listSelector), 2));
            linearLayout.addView(radioColorCell);
            radioColorCell.setOnClickListener(new View.OnClickListener() {
                @Override
                public final void onClick(View view) {
                    AlertsCreator.lambda$showSecretLocationAlert$18(arrayList2, runnable, builder, view);
                }
            });
        }
        if (!z) {
            builder.setNegativeButton(LocaleController.getString(R.string.Cancel), null);
        }
        AlertDialog show = builder.show();
        if (z) {
            show.setCanceledOnTouchOutside(false);
        }
        return show;
    }

    public static void showSendMediaAlert(int i, BaseFragment baseFragment, Theme.ResourcesProvider resourcesProvider) {
        int i2;
        if (i == 0 || baseFragment == null || baseFragment.getParentActivity() == null) {
            return;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(baseFragment.getParentActivity(), resourcesProvider);
        builder.setTitle(LocaleController.getString(R.string.UnableForward));
        if (i == 1) {
            i2 = R.string.ErrorSendRestrictedStickers;
        } else if (i == 2) {
            i2 = R.string.ErrorSendRestrictedMedia;
        } else if (i == 3) {
            i2 = R.string.ErrorSendRestrictedPolls;
        } else if (i == 4) {
            i2 = R.string.ErrorSendRestrictedStickersAll;
        } else if (i == 5) {
            i2 = R.string.ErrorSendRestrictedMediaAll;
        } else if (i == 6) {
            i2 = R.string.ErrorSendRestrictedPollsAll;
        } else if (i == 7) {
            i2 = R.string.ErrorSendRestrictedPrivacyVoiceMessages;
        } else if (i == 8) {
            i2 = R.string.ErrorSendRestrictedPrivacyVideoMessages;
        } else if (i == 9) {
            i2 = R.string.ErrorSendRestrictedVideoAll;
        } else if (i == 10) {
            i2 = R.string.ErrorSendRestrictedPhotoAll;
        } else if (i == 11) {
            i2 = R.string.ErrorSendRestrictedVideo;
        } else if (i == 12) {
            i2 = R.string.ErrorSendRestrictedPhoto;
        } else if (i == 13) {
            i2 = R.string.ErrorSendRestrictedVoiceAll;
        } else if (i == 14) {
            i2 = R.string.ErrorSendRestrictedVoice;
        } else if (i == 15) {
            i2 = R.string.ErrorSendRestrictedRoundAll;
        } else if (i == 16) {
            i2 = R.string.ErrorSendRestrictedRound;
        } else if (i == 17) {
            i2 = R.string.ErrorSendRestrictedDocumentsAll;
        } else if (i == 18) {
            i2 = R.string.ErrorSendRestrictedDocuments;
        } else {
            if (i != 19) {
                if (i == 20) {
                    i2 = R.string.ErrorSendRestrictedMusic;
                }
                builder.setPositiveButton(LocaleController.getString(R.string.OK), null);
                baseFragment.showDialog(builder.create(), true, null);
            }
            i2 = R.string.ErrorSendRestrictedMusicAll;
        }
        builder.setMessage(LocaleController.getString(i2));
        builder.setPositiveButton(LocaleController.getString(R.string.OK), null);
        baseFragment.showDialog(builder.create(), true, null);
    }

    public static Dialog showSimpleAlert(BaseFragment baseFragment, String str) {
        return showSimpleAlert(baseFragment, null, str);
    }

    public static Dialog showSimpleAlert(BaseFragment baseFragment, String str, String str2) {
        return showSimpleAlert(baseFragment, str, str2, null);
    }

    public static Dialog showSimpleAlert(BaseFragment baseFragment, String str, String str2, Theme.ResourcesProvider resourcesProvider) {
        if (str2 == null || baseFragment == null || baseFragment.getParentActivity() == null) {
            return null;
        }
        AlertDialog create = createSimpleAlert(baseFragment.getParentActivity(), str, str2, resourcesProvider).create();
        baseFragment.showDialog(create);
        return create;
    }

    public static Toast showSimpleToast(BaseFragment baseFragment, String str) {
        if (str == null) {
            return null;
        }
        Toast makeText = Toast.makeText((baseFragment == null || baseFragment.getParentActivity() == null) ? ApplicationLoader.applicationContext : baseFragment.getParentActivity(), str, 1);
        makeText.show();
        return makeText;
    }

    public static AlertDialog showUpdateAppAlert(final Context context, String str, boolean z) {
        if (context == null || str == null) {
            return null;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(LocaleController.getString(R.string.AppName));
        builder.setMessage(str);
        builder.setPositiveButton(LocaleController.getString(R.string.OK), null);
        if (z) {
            builder.setNegativeButton(LocaleController.getString(R.string.UpdateApp), new AlertDialog.OnButtonClickListener() {
                @Override
                public final void onClick(AlertDialog alertDialog, int i) {
                    AlertsCreator.lambda$showUpdateAppAlert$7(context, alertDialog, i);
                }
            });
        }
        return builder.show();
    }

    public static void updateDayPicker(NumberPicker numberPicker, NumberPicker numberPicker2, NumberPicker numberPicker3) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(2, numberPicker2.getValue());
        calendar.set(1, numberPicker3.getValue());
        numberPicker.setMinValue(1);
        numberPicker.setMaxValue(calendar.getActualMaximum(5));
    }
}
