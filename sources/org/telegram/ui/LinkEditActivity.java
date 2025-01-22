package org.telegram.ui;

import android.content.Context;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import java.util.ArrayList;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.Emoji;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.R;
import org.telegram.messenger.browser.Browser;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ActionBar.ThemeDescription;
import org.telegram.ui.Cells.EditTextCell;
import org.telegram.ui.Cells.HeaderCell;
import org.telegram.ui.Cells.TextCheckCell;
import org.telegram.ui.Cells.TextInfoPrivacyCell;
import org.telegram.ui.Cells.TextSettingsCell;
import org.telegram.ui.Components.AlertsCreator;
import org.telegram.ui.Components.SlideChooseView;

public class LinkEditActivity extends BaseFragment {
    private TextCheckCell approveCell;
    private TextInfoPrivacyCell approveHintCell;
    private FrameLayout buttonLayout;
    private TextView buttonTextView;
    private Callback callback;
    private final long chatId;
    private TextView createTextView;
    int currentInviteDate;
    private TextInfoPrivacyCell divider;
    private TextInfoPrivacyCell dividerName;
    private TextInfoPrivacyCell dividerUses;
    private boolean finished;
    private boolean ignoreSet;
    TLRPC.TL_chatInviteExported inviteToEdit;
    boolean loading;
    private EditText nameEditText;
    AlertDialog progressDialog;
    private TextSettingsCell revokeLink;
    boolean scrollToEnd;
    boolean scrollToStart;
    private ScrollView scrollView;
    private TextCheckCell subCell;
    private EditTextCell subEditPriceCell;
    private TextInfoPrivacyCell subInfoCell;
    private TextView subPriceView;
    private SlideChooseView timeChooseView;
    private TextView timeEditText;
    private HeaderCell timeHeaderCell;
    private int type;
    private SlideChooseView usesChooseView;
    private EditText usesEditText;
    private HeaderCell usesHeaderCell;
    private int shakeDp = -3;
    private boolean firstLayout = true;
    private ArrayList dispalyedDates = new ArrayList();
    private final int[] defaultDates = {3600, 86400, 604800};
    private ArrayList dispalyedUses = new ArrayList();
    private final int[] defaultUses = {1, 10, 100};

    public interface Callback {
        void onLinkCreated(TLObject tLObject);

        void onLinkEdited(TLRPC.TL_chatInviteExported tL_chatInviteExported, TLObject tLObject);

        void onLinkRemoved(TLRPC.TL_chatInviteExported tL_chatInviteExported);

        void revokeLink(TLRPC.TL_chatInviteExported tL_chatInviteExported);
    }

    public LinkEditActivity(int i, long j) {
        this.type = i;
        this.chatId = j;
    }

    private void chooseDate(int i) {
        long j = i;
        this.timeEditText.setText(LocaleController.formatDateAudio(j, false));
        int currentTime = i - getConnectionsManager().getCurrentTime();
        this.dispalyedDates.clear();
        int i2 = 0;
        boolean z = false;
        int i3 = 0;
        while (true) {
            int[] iArr = this.defaultDates;
            if (i2 >= iArr.length) {
                break;
            }
            if (!z && currentTime < iArr[i2]) {
                this.dispalyedDates.add(Integer.valueOf(currentTime));
                i3 = i2;
                z = true;
            }
            this.dispalyedDates.add(Integer.valueOf(this.defaultDates[i2]));
            i2++;
        }
        if (!z) {
            this.dispalyedDates.add(Integer.valueOf(currentTime));
            i3 = this.defaultDates.length;
        }
        int size = this.dispalyedDates.size();
        int i4 = size + 1;
        String[] strArr = new String[i4];
        for (int i5 = 0; i5 < i4; i5++) {
            if (i5 == size) {
                strArr[i5] = LocaleController.getString(R.string.NoLimit);
            } else if (((Integer) this.dispalyedDates.get(i5)).intValue() == this.defaultDates[0]) {
                strArr[i5] = LocaleController.formatPluralString("Hours", 1, new Object[0]);
            } else if (((Integer) this.dispalyedDates.get(i5)).intValue() == this.defaultDates[1]) {
                strArr[i5] = LocaleController.formatPluralString("Days", 1, new Object[0]);
            } else if (((Integer) this.dispalyedDates.get(i5)).intValue() == this.defaultDates[2]) {
                strArr[i5] = LocaleController.formatPluralString("Weeks", 1, new Object[0]);
            } else {
                long j2 = currentTime;
                if (j2 < 86400) {
                    strArr[i5] = LocaleController.getString(R.string.MessageScheduleToday);
                } else {
                    LocaleController localeController = LocaleController.getInstance();
                    if (j2 < 31449600) {
                        strArr[i5] = localeController.getFormatterScheduleDay().format(j * 1000);
                    } else {
                        strArr[i5] = localeController.getFormatterYear().format(j * 1000);
                    }
                }
            }
        }
        this.timeChooseView.setOptions(i3, strArr);
    }

    public void chooseUses(int i) {
        int i2;
        this.dispalyedUses.clear();
        int i3 = 0;
        boolean z = false;
        int i4 = 0;
        while (true) {
            int[] iArr = this.defaultUses;
            if (i3 >= iArr.length) {
                break;
            }
            if (!z && i <= (i2 = iArr[i3])) {
                if (i != i2) {
                    this.dispalyedUses.add(Integer.valueOf(i));
                }
                z = true;
                i4 = i3;
            }
            this.dispalyedUses.add(Integer.valueOf(this.defaultUses[i3]));
            i3++;
        }
        if (!z) {
            this.dispalyedUses.add(Integer.valueOf(i));
            i4 = this.defaultUses.length;
        }
        int size = this.dispalyedUses.size();
        int i5 = size + 1;
        String[] strArr = new String[i5];
        for (int i6 = 0; i6 < i5; i6++) {
            if (i6 == size) {
                strArr[i6] = LocaleController.getString(R.string.NoLimit);
            } else {
                strArr[i6] = ((Integer) this.dispalyedUses.get(i6)).toString();
            }
        }
        this.usesChooseView.setOptions(i4, strArr);
    }

    public void lambda$createView$0(View view) {
        TextCheckCell textCheckCell = this.subCell;
        if (textCheckCell != null && textCheckCell.isChecked()) {
            TextCheckCell textCheckCell2 = this.subCell;
            int i = -this.shakeDp;
            this.shakeDp = i;
            AndroidUtilities.shakeViewSpring(textCheckCell2, i);
            return;
        }
        TextCheckCell textCheckCell3 = (TextCheckCell) view;
        boolean isChecked = textCheckCell3.isChecked();
        boolean z = !isChecked;
        textCheckCell3.setBackgroundColorAnimated(z, Theme.getColor(z ? Theme.key_windowBackgroundChecked : Theme.key_windowBackgroundUnchecked));
        textCheckCell3.setChecked(z);
        setUsesVisible(isChecked);
        this.firstLayout = true;
        if (this.subCell != null) {
            if (textCheckCell3.isChecked()) {
                this.subCell.setChecked(false);
                this.subCell.setCheckBoxIcon(R.drawable.permission_locked);
                this.subEditPriceCell.setVisibility(8);
            } else if (this.inviteToEdit == null) {
                this.subCell.setCheckBoxIcon(0);
            }
        }
    }

    public void lambda$createView$1() {
        this.subEditPriceCell.editText.requestFocus();
        AndroidUtilities.showKeyboard(this.subEditPriceCell.editText);
    }

    public void lambda$createView$10(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getParentActivity());
        builder.setMessage(LocaleController.getString(R.string.RevokeAlert));
        builder.setTitle(LocaleController.getString(R.string.RevokeLink));
        builder.setPositiveButton(LocaleController.getString(R.string.RevokeButton), new AlertDialog.OnButtonClickListener() {
            @Override
            public final void onClick(AlertDialog alertDialog, int i) {
                LinkEditActivity.this.lambda$createView$9(alertDialog, i);
            }
        });
        builder.setNegativeButton(LocaleController.getString(R.string.Cancel), null);
        showDialog(builder.create());
    }

    public static void lambda$createView$11(Integer num) {
    }

    public void lambda$createView$2() {
        this.subEditPriceCell.editText.clearFocus();
        AndroidUtilities.hideKeyboard(this.subEditPriceCell.editText);
    }

    public void lambda$createView$3(Runnable[] runnableArr, View view) {
        if (this.inviteToEdit != null) {
            return;
        }
        if (this.approveCell.isChecked()) {
            TextCheckCell textCheckCell = this.approveCell;
            int i = -this.shakeDp;
            this.shakeDp = i;
            AndroidUtilities.shakeViewSpring(textCheckCell, i);
            return;
        }
        TextCheckCell textCheckCell2 = (TextCheckCell) view;
        textCheckCell2.setChecked(!textCheckCell2.isChecked());
        this.subEditPriceCell.setVisibility(textCheckCell2.isChecked() ? 0 : 8);
        AndroidUtilities.cancelRunOnUIThread(runnableArr[0]);
        if (!textCheckCell2.isChecked()) {
            this.approveCell.setCheckBoxIcon(0);
            this.approveHintCell.setText(LocaleController.getString(R.string.ApproveNewMembersDescription));
            Runnable runnable = new Runnable() {
                @Override
                public final void run() {
                    LinkEditActivity.this.lambda$createView$2();
                }
            };
            runnableArr[0] = runnable;
            AndroidUtilities.runOnUIThread(runnable);
            return;
        }
        this.approveCell.setChecked(false);
        this.approveCell.setCheckBoxIcon(R.drawable.permission_locked);
        this.approveHintCell.setText(LocaleController.getString(R.string.ApproveNewMembersDescriptionFrozen));
        Runnable runnable2 = new Runnable() {
            @Override
            public final void run() {
                LinkEditActivity.this.lambda$createView$1();
            }
        };
        runnableArr[0] = runnable2;
        AndroidUtilities.runOnUIThread(runnable2, 60L);
    }

    public void lambda$createView$4() {
        Browser.openUrl(getContext(), LocaleController.getString(R.string.RequireMonthlyFeeInfoLink));
    }

    public void lambda$createView$5(boolean z, int i) {
        chooseDate(i);
    }

    public void lambda$createView$6(Context context, View view) {
        AlertsCreator.createDatePickerDialog(context, LocaleController.getString(R.string.ExpireAfter), LocaleController.getString(R.string.SetTimeLimit), -1L, new AlertsCreator.ScheduleDatePickerDelegate() {
            @Override
            public final void didSelectDate(boolean z, int i) {
                LinkEditActivity.this.lambda$createView$5(z, i);
            }
        });
    }

    public void lambda$createView$7(int i) {
        TextView textView;
        String str;
        if (i < this.dispalyedDates.size()) {
            long intValue = ((Integer) this.dispalyedDates.get(i)).intValue() + getConnectionsManager().getCurrentTime();
            textView = this.timeEditText;
            str = LocaleController.formatDateAudio(intValue, false);
        } else {
            textView = this.timeEditText;
            str = "";
        }
        textView.setText(str);
    }

    public void lambda$createView$8(int i) {
        this.usesEditText.clearFocus();
        this.ignoreSet = true;
        if (i < this.dispalyedUses.size()) {
            this.usesEditText.setText(((Integer) this.dispalyedUses.get(i)).toString());
        } else {
            this.usesEditText.setText("");
        }
        this.ignoreSet = false;
    }

    public void lambda$createView$9(AlertDialog alertDialog, int i) {
        this.callback.revokeLink(this.inviteToEdit);
        lambda$onBackPressed$323();
    }

    public void lambda$getThemeDescriptions$16() {
        TextInfoPrivacyCell textInfoPrivacyCell = this.dividerUses;
        if (textInfoPrivacyCell != null) {
            Context context = textInfoPrivacyCell.getContext();
            TextInfoPrivacyCell textInfoPrivacyCell2 = this.dividerUses;
            int i = R.drawable.greydivider_bottom;
            int i2 = Theme.key_windowBackgroundGrayShadow;
            textInfoPrivacyCell2.setBackgroundDrawable(Theme.getThemedDrawableByKey(context, i, i2));
            this.divider.setBackgroundDrawable(Theme.getThemedDrawableByKey(context, R.drawable.greydivider, i2));
            this.buttonTextView.setBackgroundDrawable(Theme.createSimpleSelectorRoundRectDrawable(AndroidUtilities.dp(6.0f), Theme.getColor(Theme.key_featuredStickers_addButton), Theme.getColor(Theme.key_featuredStickers_addButtonPressed)));
            EditText editText = this.usesEditText;
            int i3 = Theme.key_windowBackgroundWhiteBlackText;
            editText.setTextColor(Theme.getColor(i3));
            EditText editText2 = this.usesEditText;
            int i4 = Theme.key_windowBackgroundWhiteGrayText;
            editText2.setHintTextColor(Theme.getColor(i4));
            this.timeEditText.setTextColor(Theme.getColor(i3));
            this.timeEditText.setHintTextColor(Theme.getColor(i4));
            this.buttonTextView.setTextColor(Theme.getColor(Theme.key_featuredStickers_buttonText));
            TextSettingsCell textSettingsCell = this.revokeLink;
            if (textSettingsCell != null) {
                textSettingsCell.setTextColor(Theme.getColor(Theme.key_text_RedRegular));
            }
            this.createTextView.setTextColor(Theme.getColor(Theme.key_actionBarDefaultTitle));
            this.dividerName.setBackground(Theme.getThemedDrawableByKey(context, i, i2));
            this.nameEditText.setTextColor(Theme.getColor(i3));
            this.nameEditText.setHintTextColor(Theme.getColor(i4));
        }
    }

    public void lambda$onCreateClicked$12(TLRPC.TL_error tL_error, TLObject tLObject) {
        this.loading = false;
        AlertDialog alertDialog = this.progressDialog;
        if (alertDialog != null) {
            alertDialog.dismiss();
        }
        if (tL_error != null) {
            AlertsCreator.showSimpleAlert(this, tL_error.text);
            return;
        }
        Callback callback = this.callback;
        if (callback != null) {
            callback.onLinkCreated(tLObject);
        }
        lambda$onBackPressed$323();
    }

    public void lambda$onCreateClicked$13(final TLObject tLObject, final TLRPC.TL_error tL_error) {
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                LinkEditActivity.this.lambda$onCreateClicked$12(tL_error, tLObject);
            }
        });
    }

    public void lambda$onCreateClicked$14(TLRPC.TL_error tL_error, TLObject tLObject) {
        this.loading = false;
        AlertDialog alertDialog = this.progressDialog;
        if (alertDialog != null) {
            alertDialog.dismiss();
        }
        if (tL_error != null) {
            AlertsCreator.showSimpleAlert(this, tL_error.text);
            return;
        }
        if (tLObject instanceof TLRPC.TL_messages_exportedChatInvite) {
            this.inviteToEdit = (TLRPC.TL_chatInviteExported) ((TLRPC.TL_messages_exportedChatInvite) tLObject).invite;
        }
        Callback callback = this.callback;
        if (callback != null) {
            callback.onLinkEdited(this.inviteToEdit, tLObject);
        }
        lambda$onBackPressed$323();
    }

    public void lambda$onCreateClicked$15(final TLObject tLObject, final TLRPC.TL_error tL_error) {
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                LinkEditActivity.this.lambda$onCreateClicked$14(tL_error, tLObject);
            }
        });
    }

    public void onCreateClicked(android.view.View r11) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.LinkEditActivity.onCreateClicked(android.view.View):void");
    }

    private void resetDates() {
        this.dispalyedDates.clear();
        int i = 0;
        while (true) {
            int[] iArr = this.defaultDates;
            if (i >= iArr.length) {
                this.timeChooseView.setOptions(3, LocaleController.formatPluralString("Hours", 1, new Object[0]), LocaleController.formatPluralString("Days", 1, new Object[0]), LocaleController.formatPluralString("Weeks", 1, new Object[0]), LocaleController.getString(R.string.NoLimit));
                return;
            } else {
                this.dispalyedDates.add(Integer.valueOf(iArr[i]));
                i++;
            }
        }
    }

    public void resetUses() {
        this.dispalyedUses.clear();
        int i = 0;
        while (true) {
            int[] iArr = this.defaultUses;
            if (i >= iArr.length) {
                this.usesChooseView.setOptions(3, "1", "10", "100", LocaleController.getString(R.string.NoLimit));
                return;
            } else {
                this.dispalyedUses.add(Integer.valueOf(iArr[i]));
                i++;
            }
        }
    }

    private void setUsesVisible(boolean z) {
        this.usesHeaderCell.setVisibility(z ? 0 : 8);
        this.usesChooseView.setVisibility(z ? 0 : 8);
        this.usesEditText.setVisibility(z ? 0 : 8);
        this.dividerUses.setVisibility(z ? 0 : 8);
        this.divider.setBackground(Theme.getThemedDrawableByKey(getParentActivity(), z ? R.drawable.greydivider : R.drawable.greydivider_bottom, Theme.key_windowBackgroundGrayShadow));
    }

    @Override
    public android.view.View createView(final android.content.Context r30) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.LinkEditActivity.createView(android.content.Context):android.view.View");
    }

    @Override
    public void lambda$onBackPressed$323() {
        this.scrollView.getLayoutParams().height = this.scrollView.getHeight();
        this.finished = true;
        super.lambda$onBackPressed$323();
    }

    @Override
    public ArrayList getThemeDescriptions() {
        ThemeDescription.ThemeDescriptionDelegate themeDescriptionDelegate = new ThemeDescription.ThemeDescriptionDelegate() {
            @Override
            public final void didSetColor() {
                LinkEditActivity.this.lambda$getThemeDescriptions$16();
            }

            @Override
            public void onAnimationProgress(float f) {
                ThemeDescription.ThemeDescriptionDelegate.CC.$default$onAnimationProgress(this, f);
            }
        };
        ArrayList arrayList = new ArrayList();
        int i = Theme.key_windowBackgroundWhiteBlueHeader;
        arrayList.add(new ThemeDescription(this.timeHeaderCell, 0, new Class[]{HeaderCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, i));
        arrayList.add(new ThemeDescription(this.usesHeaderCell, 0, new Class[]{HeaderCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, i));
        HeaderCell headerCell = this.timeHeaderCell;
        int i2 = ThemeDescription.FLAG_BACKGROUND;
        int i3 = Theme.key_windowBackgroundWhite;
        arrayList.add(new ThemeDescription(headerCell, i2, null, null, null, null, i3));
        arrayList.add(new ThemeDescription(this.usesHeaderCell, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, i3));
        arrayList.add(new ThemeDescription(this.timeChooseView, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, i3));
        arrayList.add(new ThemeDescription(this.usesChooseView, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, i3));
        arrayList.add(new ThemeDescription(this.timeEditText, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, i3));
        arrayList.add(new ThemeDescription(this.usesEditText, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, i3));
        arrayList.add(new ThemeDescription(this.revokeLink, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, i3));
        int i4 = Theme.key_windowBackgroundWhiteGrayText4;
        arrayList.add(new ThemeDescription(this.divider, 0, new Class[]{TextInfoPrivacyCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, i4));
        arrayList.add(new ThemeDescription(this.dividerUses, 0, new Class[]{TextInfoPrivacyCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, i4));
        arrayList.add(new ThemeDescription(this.dividerName, 0, new Class[]{TextInfoPrivacyCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, i4));
        arrayList.add(new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, Theme.key_windowBackgroundGray));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, Theme.key_actionBarDefault));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, null, null, null, null, Theme.key_actionBarDefaultIcon));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, null, null, null, null, Theme.key_actionBarDefaultTitle));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, null, null, null, null, Theme.key_actionBarDefaultSelector));
        arrayList.add(new ThemeDescription(null, 0, null, null, null, themeDescriptionDelegate, Theme.key_windowBackgroundGrayShadow));
        arrayList.add(new ThemeDescription(null, 0, null, null, null, themeDescriptionDelegate, Theme.key_featuredStickers_addButton));
        arrayList.add(new ThemeDescription(null, 0, null, null, null, themeDescriptionDelegate, Theme.key_featuredStickers_addButtonPressed));
        arrayList.add(new ThemeDescription(null, 0, null, null, null, themeDescriptionDelegate, Theme.key_windowBackgroundWhiteBlackText));
        arrayList.add(new ThemeDescription(null, 0, null, null, null, themeDescriptionDelegate, Theme.key_windowBackgroundWhiteGrayText));
        arrayList.add(new ThemeDescription(null, 0, null, null, null, themeDescriptionDelegate, Theme.key_featuredStickers_buttonText));
        arrayList.add(new ThemeDescription(null, 0, null, null, null, themeDescriptionDelegate, Theme.key_text_RedRegular));
        return arrayList;
    }

    public void setCallback(Callback callback) {
        this.callback = callback;
    }

    public void setInviteToEdit(TLRPC.TL_chatInviteExported tL_chatInviteExported) {
        this.inviteToEdit = tL_chatInviteExported;
        if (this.fragmentView == null || tL_chatInviteExported == null) {
            return;
        }
        int i = tL_chatInviteExported.expire_date;
        if (i > 0) {
            chooseDate(i);
            this.currentInviteDate = ((Integer) this.dispalyedDates.get(this.timeChooseView.getSelectedIndex())).intValue();
        } else {
            this.currentInviteDate = 0;
        }
        int i2 = tL_chatInviteExported.usage_limit;
        if (i2 > 0) {
            chooseUses(i2);
            this.usesEditText.setText(Integer.toString(tL_chatInviteExported.usage_limit));
        }
        TextCheckCell textCheckCell = this.approveCell;
        if (textCheckCell != null) {
            textCheckCell.setBackgroundColor(Theme.getColor(tL_chatInviteExported.request_needed ? Theme.key_windowBackgroundChecked : Theme.key_windowBackgroundUnchecked));
            this.approveCell.setChecked(tL_chatInviteExported.request_needed);
        }
        setUsesVisible(!tL_chatInviteExported.request_needed);
        if (!TextUtils.isEmpty(tL_chatInviteExported.title)) {
            SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(tL_chatInviteExported.title);
            Emoji.replaceEmoji(spannableStringBuilder, this.nameEditText.getPaint().getFontMetricsInt(), false);
            this.nameEditText.setText(spannableStringBuilder);
        }
        TextCheckCell textCheckCell2 = this.subCell;
        if (textCheckCell2 != null) {
            textCheckCell2.setChecked(tL_chatInviteExported.subscription_pricing != null);
        }
        if (tL_chatInviteExported.subscription_pricing != null) {
            TextCheckCell textCheckCell3 = this.approveCell;
            if (textCheckCell3 != null) {
                textCheckCell3.setChecked(false);
                this.approveCell.setCheckBoxIcon(R.drawable.permission_locked);
            }
            TextInfoPrivacyCell textInfoPrivacyCell = this.approveHintCell;
            if (textInfoPrivacyCell != null) {
                textInfoPrivacyCell.setText(LocaleController.getString(R.string.ApproveNewMembersDescriptionFrozen));
            }
        }
        EditTextCell editTextCell = this.subEditPriceCell;
        if (editTextCell != null) {
            editTextCell.setVisibility(tL_chatInviteExported.subscription_pricing != null ? 0 : 8);
            this.subEditPriceCell.setText(Long.toString(tL_chatInviteExported.subscription_pricing.amount));
            this.subEditPriceCell.editText.setClickable(false);
            this.subEditPriceCell.editText.setFocusable(false);
            this.subEditPriceCell.editText.setFocusableInTouchMode(false);
            this.subEditPriceCell.editText.setLongClickable(false);
        }
    }
}
