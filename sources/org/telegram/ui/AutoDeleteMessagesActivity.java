package org.telegram.ui;

import android.animation.TimeInterpolator;
import android.content.Context;
import android.transition.ChangeBounds;
import android.transition.Fade;
import android.transition.TransitionManager;
import android.transition.TransitionSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import java.util.ArrayList;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.R;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.AutoDeleteMessagesActivity;
import org.telegram.ui.Cells.HeaderCell;
import org.telegram.ui.Cells.RadioCell;
import org.telegram.ui.Cells.TextInfoPrivacyCell;
import org.telegram.ui.Components.AlertsCreator;
import org.telegram.ui.Components.BulletinFactory;
import org.telegram.ui.Components.CubicBezierInterpolator;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.StickerImageView;
import org.telegram.ui.UsersSelectActivity;

public class AutoDeleteMessagesActivity extends BaseFragment implements NotificationCenter.NotificationCenterDelegate {
    RadioCellInternal afterOneDay;
    RadioCellInternal afterOneMonth;
    RadioCellInternal afterOneWeek;
    LinearLayout checkBoxContainer;
    RadioCellInternal customTimeButton;
    RadioCellInternal offCell;
    ArrayList arrayList = new ArrayList();
    public int startFromTtl = 0;

    public class AnonymousClass2 implements Runnable {
        AnonymousClass2() {
        }

        public void lambda$run$0(ArrayList arrayList) {
            BulletinFactory of;
            int i;
            CharSequence formatString;
            if (arrayList.isEmpty()) {
                return;
            }
            for (int i2 = 0; i2 < arrayList.size(); i2++) {
                AutoDeleteMessagesActivity.this.getMessagesController().setDialogHistoryTTL(((Long) arrayList.get(i2)).longValue(), AutoDeleteMessagesActivity.this.getSelectedTime() * 60);
            }
            if (AutoDeleteMessagesActivity.this.getSelectedTime() > 0) {
                of = BulletinFactory.of(AutoDeleteMessagesActivity.this);
                i = R.raw.fire_on;
                formatString = AndroidUtilities.replaceTags(LocaleController.formatString("AutodeleteTimerEnabledForChats", R.string.AutodeleteTimerEnabledForChats, LocaleController.formatTTLString(AutoDeleteMessagesActivity.this.getSelectedTime() * 60), LocaleController.formatPluralString("Chats", arrayList.size(), Integer.valueOf(arrayList.size()))));
            } else {
                of = BulletinFactory.of(AutoDeleteMessagesActivity.this);
                i = R.raw.fire_off;
                formatString = LocaleController.formatString("AutodeleteTimerDisabledForChats", R.string.AutodeleteTimerDisabledForChats, LocaleController.formatPluralString("Chats", arrayList.size(), Integer.valueOf(arrayList.size())));
            }
            of.createSimpleBulletin(i, formatString).show();
        }

        public void lambda$run$1(final ArrayList arrayList, int i) {
            AndroidUtilities.runOnUIThread(new Runnable() {
                @Override
                public final void run() {
                    AutoDeleteMessagesActivity.AnonymousClass2.this.lambda$run$0(arrayList);
                }
            }, 100L);
        }

        @Override
        public void run() {
            UsersSelectActivity usersSelectActivity = new UsersSelectActivity(1);
            usersSelectActivity.setTtlPeriod(AutoDeleteMessagesActivity.this.getSelectedTime());
            usersSelectActivity.setDelegate(new UsersSelectActivity.FilterUsersActivityDelegate() {
                @Override
                public final void didSelectChats(ArrayList arrayList, int i) {
                    AutoDeleteMessagesActivity.AnonymousClass2.this.lambda$run$1(arrayList, i);
                }
            });
            AutoDeleteMessagesActivity.this.presentFragment(usersSelectActivity);
        }
    }

    public class AnonymousClass3 implements AlertsCreator.ScheduleDatePickerDelegate {
        AnonymousClass3() {
        }

        public void lambda$didSelectDate$0(int i) {
            AutoDeleteMessagesActivity.this.selectDate(i, true);
        }

        @Override
        public void didSelectDate(boolean z, final int i) {
            AndroidUtilities.runOnUIThread(new Runnable() {
                @Override
                public final void run() {
                    AutoDeleteMessagesActivity.AnonymousClass3.this.lambda$didSelectDate$0(i);
                }
            }, 50L);
        }
    }

    public class RadioCellInternal extends RadioCell {
        boolean custom;
        int time;

        public RadioCellInternal(Context context) {
            super(context);
        }
    }

    public int getSelectedTime() {
        for (int i = 0; i < this.arrayList.size(); i++) {
            if (((RadioCellInternal) this.arrayList.get(i)).isChecked()) {
                return ((RadioCellInternal) this.arrayList.get(i)).time;
            }
        }
        return this.startFromTtl;
    }

    public void lambda$updateItems$1(View view, AlertDialog alertDialog, int i) {
        alertDialog.dismiss();
        selectRadioButton(view, true);
    }

    public void lambda$updateItems$2(final View view) {
        if (view == this.customTimeButton) {
            AlertsCreator.createAutoDeleteDatePickerDialog(getContext(), 1, null, new AnonymousClass3());
            return;
        }
        int i = ((RadioCellInternal) view).time;
        if (getSelectedTime() != 0 || i <= 0) {
            selectRadioButton(view, true);
            return;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle(LocaleController.getString(R.string.MessageLifetime));
        builder.setMessage(LocaleController.formatString("AutoDeleteConfirmMessage", R.string.AutoDeleteConfirmMessage, LocaleController.formatTTLString(i * 60)));
        builder.setNegativeButton(LocaleController.getString(R.string.Cancel), new AlertDialog.OnButtonClickListener() {
            @Override
            public final void onClick(AlertDialog alertDialog, int i2) {
                alertDialog.dismiss();
            }
        });
        builder.setPositiveButton(LocaleController.getString(R.string.Enable), new AlertDialog.OnButtonClickListener() {
            @Override
            public final void onClick(AlertDialog alertDialog, int i2) {
                AutoDeleteMessagesActivity.this.lambda$updateItems$1(view, alertDialog, i2);
            }
        });
        builder.show();
    }

    public void selectDate(int i, boolean z) {
        TransitionSet transitionSet = new TransitionSet();
        ChangeBounds changeBounds = new ChangeBounds();
        changeBounds.setDuration(150L);
        Fade fade = new Fade(1);
        fade.setDuration(150L);
        transitionSet.addTransition(new Fade(2).setDuration(150L)).addTransition(changeBounds).addTransition(fade);
        transitionSet.setOrdering(0);
        transitionSet.setInterpolator((TimeInterpolator) CubicBezierInterpolator.DEFAULT);
        TransitionManager.beginDelayedTransition(this.checkBoxContainer, transitionSet);
        for (int i2 = 0; i2 < this.arrayList.size(); i2++) {
            if (((RadioCellInternal) this.arrayList.get(i2)).time == i) {
                selectRadioButton((View) this.arrayList.get(i2), z);
                return;
            }
        }
        int i3 = 0;
        while (i3 < this.arrayList.size()) {
            if (((RadioCellInternal) this.arrayList.get(i3)).custom) {
                this.checkBoxContainer.removeView((View) this.arrayList.get(i3));
                this.arrayList.remove(i3);
                i3--;
            }
            i3++;
        }
        int size = this.arrayList.size();
        int i4 = 0;
        while (true) {
            if (i4 >= this.arrayList.size()) {
                break;
            }
            if (i < ((RadioCellInternal) this.arrayList.get(i4)).time) {
                size = i4 + 1;
                break;
            }
            i4++;
        }
        RadioCellInternal radioCellInternal = new RadioCellInternal(getContext());
        radioCellInternal.custom = true;
        radioCellInternal.time = i;
        radioCellInternal.setText(LocaleController.formatString("AutoDeleteAfterShort", R.string.AutoDeleteAfterShort, LocaleController.formatTTLString(i * 60)), false, true);
        this.arrayList.add(size, radioCellInternal);
        this.checkBoxContainer.addView(radioCellInternal, size);
        updateItems();
        selectRadioButton(radioCellInternal, z);
    }

    private void selectRadioButton(View view, boolean z) {
        int i;
        for (int i2 = 0; i2 < this.arrayList.size(); i2++) {
            if (this.arrayList.get(i2) == view) {
                ((RadioCellInternal) this.arrayList.get(i2)).setChecked(true, this.fragmentBeginToShow);
            } else {
                ((RadioCellInternal) this.arrayList.get(i2)).setChecked(false, this.fragmentBeginToShow);
            }
        }
        if (!z || (i = ((RadioCellInternal) view).time) <= 0) {
            return;
        }
        BulletinFactory.of(this).createSimpleBulletin(R.raw.fire_on, AndroidUtilities.replaceTags(LocaleController.formatString("AutoDeleteGlobalTimerEnabled", R.string.AutoDeleteGlobalTimerEnabled, LocaleController.formatTTLString(i * 60)))).show();
    }

    private void updateItems() {
        for (int i = 0; i < this.arrayList.size(); i++) {
            ((RadioCellInternal) this.arrayList.get(i)).setBackground(Theme.createSelectorWithBackgroundDrawable(Theme.getColor(Theme.key_windowBackgroundWhite), Theme.getColor(Theme.key_listSelector)));
            ((RadioCellInternal) this.arrayList.get(i)).setOnClickListener(new View.OnClickListener() {
                @Override
                public final void onClick(View view) {
                    AutoDeleteMessagesActivity.this.lambda$updateItems$2(view);
                }
            });
        }
    }

    @Override
    public View createView(Context context) {
        this.actionBar.setBackButtonImage(R.drawable.ic_ab_back);
        this.actionBar.setAllowOverlayTitle(true);
        this.actionBar.setTitle(LocaleController.getString(R.string.AutoDeleteMessages));
        this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
            @Override
            public void onItemClick(int i) {
                if (i == -1) {
                    AutoDeleteMessagesActivity.this.lambda$onBackPressed$323();
                }
            }
        });
        FrameLayout frameLayout = new FrameLayout(context);
        this.fragmentView = frameLayout;
        frameLayout.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundGray));
        ScrollView scrollView = new ScrollView(getContext());
        LinearLayout linearLayout = new LinearLayout(getContext());
        linearLayout.setOrientation(1);
        scrollView.addView(linearLayout);
        frameLayout.addView(scrollView);
        FrameLayout frameLayout2 = new FrameLayout(context);
        StickerImageView stickerImageView = new StickerImageView(context, this.currentAccount);
        stickerImageView.setStickerNum(10);
        frameLayout2.addView(stickerImageView, LayoutHelper.createFrame(130, 130, 17));
        linearLayout.addView(frameLayout2, LayoutHelper.createLinear(-1, 170));
        LinearLayout linearLayout2 = new LinearLayout(getContext());
        this.checkBoxContainer = linearLayout2;
        linearLayout2.setOrientation(1);
        this.checkBoxContainer.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
        linearLayout.addView(this.checkBoxContainer, LayoutHelper.createLinear(-1, -2));
        HeaderCell headerCell = new HeaderCell(getContext());
        headerCell.setText(LocaleController.getString(R.string.MessageLifetime));
        this.checkBoxContainer.addView(headerCell);
        RadioCellInternal radioCellInternal = new RadioCellInternal(getContext());
        this.offCell = radioCellInternal;
        radioCellInternal.setText(LocaleController.getString(R.string.ShortMessageLifetimeForever), false, true);
        RadioCellInternal radioCellInternal2 = this.offCell;
        radioCellInternal2.time = 0;
        this.checkBoxContainer.addView(radioCellInternal2);
        RadioCellInternal radioCellInternal3 = new RadioCellInternal(getContext());
        this.afterOneDay = radioCellInternal3;
        radioCellInternal3.setText(LocaleController.getString(R.string.AutoDeleteAfter1Day), false, true);
        RadioCellInternal radioCellInternal4 = this.afterOneDay;
        radioCellInternal4.time = 1440;
        this.checkBoxContainer.addView(radioCellInternal4);
        RadioCellInternal radioCellInternal5 = new RadioCellInternal(getContext());
        this.afterOneWeek = radioCellInternal5;
        radioCellInternal5.setText(LocaleController.getString(R.string.AutoDeleteAfter1Week), false, true);
        RadioCellInternal radioCellInternal6 = this.afterOneWeek;
        radioCellInternal6.time = 10080;
        this.checkBoxContainer.addView(radioCellInternal6);
        RadioCellInternal radioCellInternal7 = new RadioCellInternal(getContext());
        this.afterOneMonth = radioCellInternal7;
        radioCellInternal7.setText(LocaleController.getString(R.string.AutoDeleteAfter1Month), false, true);
        RadioCellInternal radioCellInternal8 = this.afterOneMonth;
        radioCellInternal8.time = 44640;
        this.checkBoxContainer.addView(radioCellInternal8);
        RadioCellInternal radioCellInternal9 = new RadioCellInternal(getContext());
        this.customTimeButton = radioCellInternal9;
        radioCellInternal9.setText(LocaleController.getString(R.string.SetCustomTime), false, false);
        this.customTimeButton.hideRadioButton();
        this.checkBoxContainer.addView(this.customTimeButton);
        this.arrayList.add(this.offCell);
        this.arrayList.add(this.afterOneDay);
        this.arrayList.add(this.afterOneWeek);
        this.arrayList.add(this.afterOneMonth);
        this.arrayList.add(this.customTimeButton);
        updateItems();
        TextInfoPrivacyCell textInfoPrivacyCell = new TextInfoPrivacyCell(context);
        textInfoPrivacyCell.setText(AndroidUtilities.replaceSingleTag(LocaleController.getString(R.string.GlobalAutoDeleteInfo), new AnonymousClass2()));
        linearLayout.addView(textInfoPrivacyCell, LayoutHelper.createLinear(-1, -2));
        selectDate(this.startFromTtl, false);
        return this.fragmentView;
    }

    @Override
    public void didReceivedNotification(int i, int i2, Object... objArr) {
    }

    @Override
    public boolean onFragmentCreate() {
        int globalTTl = getUserConfig().getGlobalTTl();
        this.startFromTtl = globalTTl;
        if (globalTTl < 0) {
            this.startFromTtl = 0;
        }
        getUserConfig().loadGlobalTTl();
        getNotificationCenter().addObserver(this, NotificationCenter.didUpdateGlobalAutoDeleteTimer);
        return super.onFragmentCreate();
    }

    @Override
    public void onFragmentDestroy() {
        super.onFragmentDestroy();
        getNotificationCenter().removeObserver(this, NotificationCenter.didUpdateGlobalAutoDeleteTimer);
    }

    @Override
    public void onPause() {
        super.onPause();
        for (int i = 0; i < this.arrayList.size(); i++) {
            if (((RadioCellInternal) this.arrayList.get(i)).isChecked()) {
                if (((RadioCellInternal) this.arrayList.get(i)).time != this.startFromTtl) {
                    this.startFromTtl = ((RadioCellInternal) this.arrayList.get(i)).time;
                    TLRPC.TL_messages_setDefaultHistoryTTL tL_messages_setDefaultHistoryTTL = new TLRPC.TL_messages_setDefaultHistoryTTL();
                    tL_messages_setDefaultHistoryTTL.period = ((RadioCellInternal) this.arrayList.get(i)).time * 60;
                    getConnectionsManager().sendRequest(tL_messages_setDefaultHistoryTTL, new RequestDelegate() {
                        @Override
                        public void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                        }
                    });
                    getUserConfig().setGlobalTtl(this.startFromTtl);
                    NotificationCenter.getInstance(this.currentAccount).lambda$postNotificationNameOnUIThread$1(NotificationCenter.didUpdateGlobalAutoDeleteTimer, new Object[0]);
                    return;
                }
                return;
            }
        }
    }
}
