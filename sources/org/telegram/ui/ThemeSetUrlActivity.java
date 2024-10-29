package org.telegram.ui;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.InputFilter;
import android.text.Selection;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import java.util.ArrayList;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.R;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.BottomSheet;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ActionBar.ThemeDescription;
import org.telegram.ui.Cells.HeaderCell;
import org.telegram.ui.Cells.TextInfoPrivacyCell;
import org.telegram.ui.Cells.TextSettingsCell;
import org.telegram.ui.Cells.ThemePreviewMessagesCell;
import org.telegram.ui.Cells.ThemesHorizontalListCell;
import org.telegram.ui.Components.AlertsCreator;
import org.telegram.ui.Components.BulletinFactory;
import org.telegram.ui.Components.EditTextBoldCursor;
import org.telegram.ui.Components.LayoutHelper;

public class ThemeSetUrlActivity extends BaseFragment implements NotificationCenter.NotificationCenterDelegate {
    private TextInfoPrivacyCell checkInfoCell;
    private int checkReqId;
    private Runnable checkRunnable;
    private TextSettingsCell createCell;
    private TextInfoPrivacyCell createInfoCell;
    private boolean creatingNewTheme;
    private View divider;
    private View doneButton;
    private EditTextBoldCursor editText;
    private HeaderCell headerCell;
    private TextInfoPrivacyCell helpInfoCell;
    private boolean ignoreCheck;
    private TLRPC.TL_theme info;
    private CharSequence infoText;
    private String lastCheckName;
    private boolean lastNameAvailable;
    private LinearLayout linearLayoutTypeContainer;
    private EditTextBoldCursor linkField;
    private ThemePreviewMessagesCell messagesCell;
    private EditTextBoldCursor nameField;
    private AlertDialog progressDialog;
    private Theme.ThemeAccent themeAccent;
    private Theme.ThemeInfo themeInfo;

    private static class LinkMovementMethodMy extends LinkMovementMethod {
        private LinkMovementMethodMy() {
        }

        @Override
        public boolean onTouchEvent(TextView textView, Spannable spannable, MotionEvent motionEvent) {
            try {
                boolean onTouchEvent = super.onTouchEvent(textView, spannable, motionEvent);
                if (motionEvent.getAction() != 1) {
                    if (motionEvent.getAction() == 3) {
                    }
                    return onTouchEvent;
                }
                Selection.removeSelection(spannable);
                return onTouchEvent;
            } catch (Exception e) {
                FileLog.e(e);
                return false;
            }
        }
    }

    public class LinkSpan extends ClickableSpan {
        private String url;

        public LinkSpan(String str) {
            this.url = str;
        }

        @Override
        public void onClick(View view) {
            try {
                ((ClipboardManager) ApplicationLoader.applicationContext.getSystemService("clipboard")).setPrimaryClip(ClipData.newPlainText("label", this.url));
                if (BulletinFactory.canShowBulletin(ThemeSetUrlActivity.this)) {
                    BulletinFactory.createCopyLinkBulletin(ThemeSetUrlActivity.this).show();
                }
            } catch (Exception e) {
                FileLog.e(e);
            }
        }

        @Override
        public void updateDrawState(TextPaint textPaint) {
            super.updateDrawState(textPaint);
            textPaint.setUnderlineText(false);
        }
    }

    public ThemeSetUrlActivity(Theme.ThemeInfo themeInfo, Theme.ThemeAccent themeAccent, boolean z) {
        this.themeInfo = themeInfo;
        this.themeAccent = themeAccent;
        this.info = themeAccent != null ? themeAccent.info : themeInfo.info;
        this.currentAccount = themeAccent != null ? themeAccent.account : themeInfo.account;
        this.creatingNewTheme = z;
    }

    public boolean checkUrl(final String str, boolean z) {
        String str2;
        Runnable runnable = this.checkRunnable;
        if (runnable != null) {
            AndroidUtilities.cancelRunOnUIThread(runnable);
            this.checkRunnable = null;
            this.lastCheckName = null;
            if (this.checkReqId != 0) {
                ConnectionsManager.getInstance(this.currentAccount).cancelRequest(this.checkReqId, true);
            }
        }
        this.lastNameAvailable = false;
        if (str != null) {
            if (str.startsWith("_") || str.endsWith("_")) {
                setCheckText(LocaleController.getString(R.string.SetUrlInvalid), Theme.key_text_RedRegular);
                return false;
            }
            for (int i = 0; i < str.length(); i++) {
                char charAt = str.charAt(i);
                if (i == 0 && charAt >= '0' && charAt <= '9') {
                    if (z) {
                        AlertsCreator.showSimpleAlert(this, LocaleController.getString(R.string.Theme), LocaleController.getString(R.string.SetUrlInvalidStartNumber));
                    } else {
                        setCheckText(LocaleController.getString(R.string.SetUrlInvalidStartNumber), Theme.key_text_RedRegular);
                    }
                    return false;
                }
                if ((charAt < '0' || charAt > '9') && ((charAt < 'a' || charAt > 'z') && ((charAt < 'A' || charAt > 'Z') && charAt != '_'))) {
                    if (z) {
                        AlertsCreator.showSimpleAlert(this, LocaleController.getString(R.string.Theme), LocaleController.getString(R.string.SetUrlInvalid));
                    } else {
                        setCheckText(LocaleController.getString(R.string.SetUrlInvalid), Theme.key_text_RedRegular);
                    }
                    return false;
                }
            }
        }
        if (str == null || str.length() < 5) {
            if (z) {
                AlertsCreator.showSimpleAlert(this, LocaleController.getString(R.string.Theme), LocaleController.getString(R.string.SetUrlInvalidShort));
            } else {
                setCheckText(LocaleController.getString(R.string.SetUrlInvalidShort), Theme.key_text_RedRegular);
            }
            return false;
        }
        if (str.length() > 64) {
            if (z) {
                AlertsCreator.showSimpleAlert(this, LocaleController.getString(R.string.Theme), LocaleController.getString(R.string.SetUrlInvalidLong));
            } else {
                setCheckText(LocaleController.getString(R.string.SetUrlInvalidLong), Theme.key_text_RedRegular);
            }
            return false;
        }
        if (!z) {
            TLRPC.TL_theme tL_theme = this.info;
            if (tL_theme == null || (str2 = tL_theme.slug) == null) {
                str2 = "";
            }
            if (str.equals(str2)) {
                setCheckText(LocaleController.formatString("SetUrlAvailable", R.string.SetUrlAvailable, str), Theme.key_windowBackgroundWhiteGreenText);
                return true;
            }
            setCheckText(LocaleController.getString(R.string.SetUrlChecking), Theme.key_windowBackgroundWhiteGrayText8);
            this.lastCheckName = str;
            Runnable runnable2 = new Runnable() {
                @Override
                public final void run() {
                    ThemeSetUrlActivity.this.lambda$checkUrl$8(str);
                }
            };
            this.checkRunnable = runnable2;
            AndroidUtilities.runOnUIThread(runnable2, 300L);
        }
        return true;
    }

    public void lambda$checkUrl$6(String str, TLRPC.TL_error tL_error) {
        this.checkReqId = 0;
        String str2 = this.lastCheckName;
        if (str2 == null || !str2.equals(str)) {
            return;
        }
        if (tL_error == null || !("THEME_SLUG_INVALID".equals(tL_error.text) || "THEME_SLUG_OCCUPIED".equals(tL_error.text))) {
            setCheckText(LocaleController.formatString("SetUrlAvailable", R.string.SetUrlAvailable, str), Theme.key_windowBackgroundWhiteGreenText);
            this.lastNameAvailable = true;
        } else {
            setCheckText(LocaleController.getString(R.string.SetUrlInUse), Theme.key_text_RedRegular);
            this.lastNameAvailable = false;
        }
    }

    public void lambda$checkUrl$7(final String str, TLObject tLObject, final TLRPC.TL_error tL_error) {
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                ThemeSetUrlActivity.this.lambda$checkUrl$6(str, tL_error);
            }
        });
    }

    public void lambda$checkUrl$8(final String str) {
        TLRPC.TL_account_createTheme tL_account_createTheme = new TLRPC.TL_account_createTheme();
        tL_account_createTheme.slug = str;
        tL_account_createTheme.title = "";
        tL_account_createTheme.document = new TLRPC.TL_inputDocumentEmpty();
        this.checkReqId = ConnectionsManager.getInstance(this.currentAccount).sendRequest(tL_account_createTheme, new RequestDelegate() {
            @Override
            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                ThemeSetUrlActivity.this.lambda$checkUrl$7(str, tLObject, tL_error);
            }
        }, 2);
    }

    public static boolean lambda$createView$0(View view, MotionEvent motionEvent) {
        return true;
    }

    public boolean lambda$createView$1(TextView textView, int i, KeyEvent keyEvent) {
        if (i != 6) {
            return false;
        }
        AndroidUtilities.hideKeyboard(this.nameField);
        return true;
    }

    public boolean lambda$createView$2(TextView textView, int i, KeyEvent keyEvent) {
        View view;
        if (i != 6 || (view = this.doneButton) == null) {
            return false;
        }
        view.performClick();
        return true;
    }

    public void lambda$createView$3(View view, boolean z) {
        this.helpInfoCell.setText(AndroidUtilities.replaceTags(LocaleController.getString(z ? R.string.ThemeCreateHelp2 : R.string.ThemeCreateHelp)));
    }

    public static boolean lambda$createView$4(View view, MotionEvent motionEvent) {
        return true;
    }

    public void lambda$createView$5(Context context, View view) {
        if (getParentActivity() == null) {
            return;
        }
        final BottomSheet.Builder builder = new BottomSheet.Builder(getParentActivity(), false);
        builder.setApplyBottomPadding(false);
        LinearLayout linearLayout = new LinearLayout(context);
        linearLayout.setOrientation(1);
        TextView textView = new TextView(context);
        textView.setText(LocaleController.getString(R.string.ChooseTheme));
        textView.setTextColor(Theme.getColor(Theme.key_dialogTextBlack));
        textView.setTextSize(1, 20.0f);
        textView.setTypeface(AndroidUtilities.bold());
        linearLayout.addView(textView, LayoutHelper.createLinear(-1, -2, 51, 22, 12, 22, 4));
        textView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public final boolean onTouch(View view2, MotionEvent motionEvent) {
                boolean lambda$createView$4;
                lambda$createView$4 = ThemeSetUrlActivity.lambda$createView$4(view2, motionEvent);
                return lambda$createView$4;
            }
        });
        builder.setCustomView(linearLayout);
        ArrayList arrayList = new ArrayList();
        int size = Theme.themes.size();
        for (int i = 0; i < size; i++) {
            Theme.ThemeInfo themeInfo = (Theme.ThemeInfo) Theme.themes.get(i);
            TLRPC.TL_theme tL_theme = themeInfo.info;
            if (tL_theme == null || tL_theme.document != null) {
                arrayList.add(themeInfo);
            }
        }
        ThemesHorizontalListCell themesHorizontalListCell = new ThemesHorizontalListCell(context, this, 2, arrayList, new ArrayList()) {
            @Override
            protected void updateRows() {
                builder.getDismissRunnable().run();
            }
        };
        linearLayout.addView(themesHorizontalListCell, LayoutHelper.createLinear(-1, 148, 0.0f, 7.0f, 0.0f, 1.0f));
        themesHorizontalListCell.scrollToCurrentTheme(this.fragmentView.getMeasuredWidth(), false);
        showDialog(builder.create());
    }

    public void lambda$saveTheme$10(TLRPC.TL_theme tL_theme) {
        try {
            this.progressDialog.dismiss();
            this.progressDialog = null;
        } catch (Exception e) {
            FileLog.e(e);
        }
        Theme.setThemeUploadInfo(this.themeInfo, this.themeAccent, tL_theme, this.currentAccount, false);
        lambda$onBackPressed$319();
    }

    public void lambda$saveTheme$11(TLRPC.TL_error tL_error, TLRPC.TL_account_updateTheme tL_account_updateTheme) {
        try {
            this.progressDialog.dismiss();
            this.progressDialog = null;
        } catch (Exception e) {
            FileLog.e(e);
        }
        AlertsCreator.processError(this.currentAccount, tL_error, this, tL_account_updateTheme, new Object[0]);
    }

    public void lambda$saveTheme$12(final TLRPC.TL_account_updateTheme tL_account_updateTheme, TLObject tLObject, final TLRPC.TL_error tL_error) {
        if (!(tLObject instanceof TLRPC.TL_theme)) {
            AndroidUtilities.runOnUIThread(new Runnable() {
                @Override
                public final void run() {
                    ThemeSetUrlActivity.this.lambda$saveTheme$11(tL_error, tL_account_updateTheme);
                }
            });
        } else {
            final TLRPC.TL_theme tL_theme = (TLRPC.TL_theme) tLObject;
            AndroidUtilities.runOnUIThread(new Runnable() {
                @Override
                public final void run() {
                    ThemeSetUrlActivity.this.lambda$saveTheme$10(tL_theme);
                }
            });
        }
    }

    public void lambda$saveTheme$13(int i, DialogInterface dialogInterface) {
        ConnectionsManager.getInstance(this.currentAccount).cancelRequest(i, true);
    }

    public static void lambda$saveTheme$9(DialogInterface dialogInterface) {
    }

    public void saveTheme() {
        if (checkUrl(this.linkField.getText().toString(), true) && getParentActivity() != null) {
            if (this.nameField.length() == 0) {
                AlertsCreator.showSimpleAlert(this, LocaleController.getString(R.string.Theme), LocaleController.getString(R.string.ThemeNameInvalid));
                return;
            }
            if (this.creatingNewTheme) {
                String str = this.info.title;
                AlertDialog alertDialog = new AlertDialog(getParentActivity(), 3);
                this.progressDialog = alertDialog;
                alertDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public final void onCancel(DialogInterface dialogInterface) {
                        ThemeSetUrlActivity.lambda$saveTheme$9(dialogInterface);
                    }
                });
                this.progressDialog.show();
                Theme.ThemeInfo themeInfo = this.themeInfo;
                TLRPC.TL_theme tL_theme = this.info;
                String obj = this.nameField.getText().toString();
                tL_theme.title = obj;
                themeInfo.name = obj;
                this.themeInfo.info.slug = this.linkField.getText().toString();
                Theme.saveCurrentTheme(this.themeInfo, true, true, true);
                return;
            }
            TLRPC.TL_theme tL_theme2 = this.info;
            String str2 = tL_theme2.slug;
            if (str2 == null) {
                str2 = "";
            }
            String str3 = tL_theme2.title;
            String str4 = str3 != null ? str3 : "";
            String obj2 = this.linkField.getText().toString();
            String obj3 = this.nameField.getText().toString();
            if (str2.equals(obj2) && str4.equals(obj3)) {
                lambda$onBackPressed$319();
                return;
            }
            this.progressDialog = new AlertDialog(getParentActivity(), 3);
            final TLRPC.TL_account_updateTheme tL_account_updateTheme = new TLRPC.TL_account_updateTheme();
            TLRPC.TL_inputTheme tL_inputTheme = new TLRPC.TL_inputTheme();
            TLRPC.TL_theme tL_theme3 = this.info;
            tL_inputTheme.id = tL_theme3.id;
            tL_inputTheme.access_hash = tL_theme3.access_hash;
            tL_account_updateTheme.theme = tL_inputTheme;
            tL_account_updateTheme.format = "android";
            tL_account_updateTheme.slug = obj2;
            int i = tL_account_updateTheme.flags;
            tL_account_updateTheme.title = obj3;
            tL_account_updateTheme.flags = i | 3;
            final int sendRequest = ConnectionsManager.getInstance(this.currentAccount).sendRequest(tL_account_updateTheme, new RequestDelegate() {
                @Override
                public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                    ThemeSetUrlActivity.this.lambda$saveTheme$12(tL_account_updateTheme, tLObject, tL_error);
                }
            }, 2);
            ConnectionsManager.getInstance(this.currentAccount).bindRequestToGuid(sendRequest, this.classGuid);
            this.progressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public final void onCancel(DialogInterface dialogInterface) {
                    ThemeSetUrlActivity.this.lambda$saveTheme$13(sendRequest, dialogInterface);
                }
            });
            this.progressDialog.show();
        }
    }

    private void setCheckText(String str, int i) {
        TextInfoPrivacyCell textInfoPrivacyCell;
        Drawable drawable;
        Activity parentActivity;
        int i2;
        if (TextUtils.isEmpty(str)) {
            this.checkInfoCell.setVisibility(8);
            if (this.creatingNewTheme) {
                textInfoPrivacyCell = this.helpInfoCell;
                parentActivity = getParentActivity();
                i2 = R.drawable.greydivider;
            } else {
                textInfoPrivacyCell = this.helpInfoCell;
                parentActivity = getParentActivity();
                i2 = R.drawable.greydivider_bottom;
            }
        } else {
            this.checkInfoCell.setVisibility(0);
            this.checkInfoCell.setText(str);
            this.checkInfoCell.setTag(Integer.valueOf(i));
            this.checkInfoCell.setTextColorByKey(i);
            if (!this.creatingNewTheme) {
                textInfoPrivacyCell = this.helpInfoCell;
                drawable = null;
                textInfoPrivacyCell.setBackgroundDrawable(drawable);
            } else {
                textInfoPrivacyCell = this.helpInfoCell;
                parentActivity = getParentActivity();
                i2 = R.drawable.greydivider_top;
            }
        }
        drawable = Theme.getThemedDrawableByKey(parentActivity, i2, Theme.key_windowBackgroundGrayShadow);
        textInfoPrivacyCell.setBackgroundDrawable(drawable);
    }

    @Override
    public View createView(final Context context) {
        ActionBar actionBar;
        int i;
        TextInfoPrivacyCell textInfoPrivacyCell;
        SpannableStringBuilder replaceTags;
        this.actionBar.setBackButtonImage(R.drawable.ic_ab_back);
        this.actionBar.setAllowOverlayTitle(true);
        if (this.creatingNewTheme) {
            actionBar = this.actionBar;
            i = R.string.NewThemeTitle;
        } else {
            actionBar = this.actionBar;
            i = R.string.EditThemeTitle;
        }
        actionBar.setTitle(LocaleController.getString(i));
        this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
            @Override
            public void onItemClick(int i2) {
                if (i2 == -1) {
                    ThemeSetUrlActivity.this.lambda$onBackPressed$319();
                } else if (i2 == 1) {
                    ThemeSetUrlActivity.this.saveTheme();
                }
            }
        });
        this.doneButton = this.actionBar.createMenu().addItem(1, LocaleController.getString(R.string.Done).toUpperCase());
        LinearLayout linearLayout = new LinearLayout(context);
        this.fragmentView = linearLayout;
        linearLayout.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundGray));
        LinearLayout linearLayout2 = (LinearLayout) this.fragmentView;
        linearLayout2.setOrientation(1);
        this.fragmentView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public final boolean onTouch(View view, MotionEvent motionEvent) {
                boolean lambda$createView$0;
                lambda$createView$0 = ThemeSetUrlActivity.lambda$createView$0(view, motionEvent);
                return lambda$createView$0;
            }
        });
        LinearLayout linearLayout3 = new LinearLayout(context);
        this.linearLayoutTypeContainer = linearLayout3;
        linearLayout3.setOrientation(1);
        this.linearLayoutTypeContainer.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
        linearLayout2.addView(this.linearLayoutTypeContainer, LayoutHelper.createLinear(-1, -2));
        HeaderCell headerCell = new HeaderCell(context, 23);
        this.headerCell = headerCell;
        headerCell.setText(LocaleController.getString(R.string.Info));
        this.linearLayoutTypeContainer.addView(this.headerCell);
        EditTextBoldCursor editTextBoldCursor = new EditTextBoldCursor(context);
        this.nameField = editTextBoldCursor;
        editTextBoldCursor.setTextSize(1, 18.0f);
        EditTextBoldCursor editTextBoldCursor2 = this.nameField;
        int i2 = Theme.key_windowBackgroundWhiteHintText;
        editTextBoldCursor2.setHintTextColor(Theme.getColor(i2));
        EditTextBoldCursor editTextBoldCursor3 = this.nameField;
        int i3 = Theme.key_windowBackgroundWhiteBlackText;
        editTextBoldCursor3.setTextColor(Theme.getColor(i3));
        this.nameField.setMaxLines(1);
        this.nameField.setLines(1);
        this.nameField.setGravity((LocaleController.isRTL ? 5 : 3) | 16);
        this.nameField.setBackgroundDrawable(null);
        this.nameField.setPadding(0, 0, 0, 0);
        this.nameField.setSingleLine(true);
        this.nameField.setFilters(new InputFilter[]{new InputFilter.LengthFilter(128)});
        this.nameField.setInputType(163872);
        this.nameField.setImeOptions(6);
        this.nameField.setHint(LocaleController.getString(R.string.ThemeNamePlaceholder));
        this.nameField.setCursorColor(Theme.getColor(i3));
        this.nameField.setCursorSize(AndroidUtilities.dp(20.0f));
        this.nameField.setCursorWidth(1.5f);
        this.linearLayoutTypeContainer.addView(this.nameField, LayoutHelper.createLinear(-1, 50, 23.0f, 0.0f, 23.0f, 0.0f));
        this.nameField.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public final boolean onEditorAction(TextView textView, int i4, KeyEvent keyEvent) {
                boolean lambda$createView$1;
                lambda$createView$1 = ThemeSetUrlActivity.this.lambda$createView$1(textView, i4, keyEvent);
                return lambda$createView$1;
            }
        });
        View view = new View(context) {
            @Override
            protected void onDraw(Canvas canvas) {
                canvas.drawLine(LocaleController.isRTL ? 0.0f : AndroidUtilities.dp(20.0f), getMeasuredHeight() - 1, getMeasuredWidth() - (LocaleController.isRTL ? AndroidUtilities.dp(20.0f) : 0), getMeasuredHeight() - 1, Theme.dividerPaint);
            }
        };
        this.divider = view;
        this.linearLayoutTypeContainer.addView(view, new LinearLayout.LayoutParams(-1, 1));
        LinearLayout linearLayout4 = new LinearLayout(context);
        linearLayout4.setOrientation(0);
        this.linearLayoutTypeContainer.addView(linearLayout4, LayoutHelper.createLinear(-1, 50, 23.0f, 0.0f, 23.0f, 0.0f));
        EditTextBoldCursor editTextBoldCursor4 = new EditTextBoldCursor(context);
        this.editText = editTextBoldCursor4;
        editTextBoldCursor4.setText(getMessagesController().linkPrefix + "/addtheme/");
        this.editText.setTextSize(1, 18.0f);
        this.editText.setHintTextColor(Theme.getColor(i2));
        this.editText.setTextColor(Theme.getColor(i3));
        this.editText.setMaxLines(1);
        this.editText.setLines(1);
        this.editText.setEnabled(false);
        this.editText.setBackgroundDrawable(null);
        this.editText.setPadding(0, 0, 0, 0);
        this.editText.setSingleLine(true);
        this.editText.setInputType(163840);
        this.editText.setImeOptions(6);
        linearLayout4.addView(this.editText, LayoutHelper.createLinear(-2, 50));
        EditTextBoldCursor editTextBoldCursor5 = new EditTextBoldCursor(context);
        this.linkField = editTextBoldCursor5;
        editTextBoldCursor5.setTextSize(1, 18.0f);
        this.linkField.setHintTextColor(Theme.getColor(i2));
        this.linkField.setTextColor(Theme.getColor(i3));
        this.linkField.setMaxLines(1);
        this.linkField.setLines(1);
        this.linkField.setBackgroundDrawable(null);
        this.linkField.setPadding(0, 0, 0, 0);
        this.linkField.setSingleLine(true);
        this.linkField.setInputType(163872);
        this.linkField.setImeOptions(6);
        this.linkField.setHint(LocaleController.getString(R.string.SetUrlPlaceholder));
        this.linkField.setCursorColor(Theme.getColor(i3));
        this.linkField.setCursorSize(AndroidUtilities.dp(20.0f));
        this.linkField.setCursorWidth(1.5f);
        linearLayout4.addView(this.linkField, LayoutHelper.createLinear(-1, 50));
        this.linkField.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public final boolean onEditorAction(TextView textView, int i4, KeyEvent keyEvent) {
                boolean lambda$createView$2;
                lambda$createView$2 = ThemeSetUrlActivity.this.lambda$createView$2(textView, i4, keyEvent);
                return lambda$createView$2;
            }
        });
        this.linkField.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable editable) {
                if (ThemeSetUrlActivity.this.creatingNewTheme) {
                    return;
                }
                if (ThemeSetUrlActivity.this.linkField.length() <= 0) {
                    ThemeSetUrlActivity.this.helpInfoCell.setText(ThemeSetUrlActivity.this.infoText);
                    return;
                }
                String str = "https://" + ThemeSetUrlActivity.this.getMessagesController().linkPrefix + "/addtheme/" + ((Object) ThemeSetUrlActivity.this.linkField.getText());
                String formatString = LocaleController.formatString("ThemeHelpLink", R.string.ThemeHelpLink, str);
                int indexOf = formatString.indexOf(str);
                SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(formatString);
                if (indexOf >= 0) {
                    spannableStringBuilder.setSpan(new LinkSpan(str), indexOf, str.length() + indexOf, 33);
                }
                ThemeSetUrlActivity.this.helpInfoCell.setText(TextUtils.concat(ThemeSetUrlActivity.this.infoText, "\n\n", spannableStringBuilder));
            }

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i4, int i5, int i6) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i4, int i5, int i6) {
                if (ThemeSetUrlActivity.this.ignoreCheck) {
                    return;
                }
                ThemeSetUrlActivity themeSetUrlActivity = ThemeSetUrlActivity.this;
                themeSetUrlActivity.checkUrl(themeSetUrlActivity.linkField.getText().toString(), false);
            }
        });
        if (this.creatingNewTheme) {
            this.linkField.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public final void onFocusChange(View view2, boolean z) {
                    ThemeSetUrlActivity.this.lambda$createView$3(view2, z);
                }
            });
        }
        TextInfoPrivacyCell textInfoPrivacyCell2 = new TextInfoPrivacyCell(context);
        this.checkInfoCell = textInfoPrivacyCell2;
        int i4 = R.drawable.greydivider_bottom;
        int i5 = Theme.key_windowBackgroundGrayShadow;
        textInfoPrivacyCell2.setBackgroundDrawable(Theme.getThemedDrawableByKey(context, i4, i5));
        this.checkInfoCell.setVisibility(8);
        this.checkInfoCell.setBottomPadding(0);
        linearLayout2.addView(this.checkInfoCell, LayoutHelper.createLinear(-1, -2));
        TextInfoPrivacyCell textInfoPrivacyCell3 = new TextInfoPrivacyCell(context);
        this.helpInfoCell = textInfoPrivacyCell3;
        textInfoPrivacyCell3.getTextView().setMovementMethod(new LinkMovementMethodMy());
        this.helpInfoCell.getTextView().setHighlightColor(Theme.getColor(Theme.key_windowBackgroundWhiteLinkSelection));
        if (this.creatingNewTheme) {
            textInfoPrivacyCell = this.helpInfoCell;
            replaceTags = AndroidUtilities.replaceTags(LocaleController.getString(R.string.ThemeCreateHelp));
        } else {
            textInfoPrivacyCell = this.helpInfoCell;
            replaceTags = AndroidUtilities.replaceTags(LocaleController.getString(R.string.ThemeSetUrlHelp));
            this.infoText = replaceTags;
        }
        textInfoPrivacyCell.setText(replaceTags);
        linearLayout2.addView(this.helpInfoCell, LayoutHelper.createLinear(-1, -2));
        if (this.creatingNewTheme) {
            this.helpInfoCell.setBackgroundDrawable(Theme.getThemedDrawableByKey(context, R.drawable.greydivider, i5));
            ThemePreviewMessagesCell themePreviewMessagesCell = new ThemePreviewMessagesCell(context, this.parentLayout, 1);
            this.messagesCell = themePreviewMessagesCell;
            linearLayout2.addView(themePreviewMessagesCell, LayoutHelper.createLinear(-1, -2));
            TextSettingsCell textSettingsCell = new TextSettingsCell(context);
            this.createCell = textSettingsCell;
            textSettingsCell.setBackgroundDrawable(Theme.getSelectorDrawable(true));
            this.createCell.setText(LocaleController.getString(R.string.UseDifferentTheme), false);
            linearLayout2.addView(this.createCell, LayoutHelper.createLinear(-1, -2));
            this.createCell.setOnClickListener(new View.OnClickListener() {
                @Override
                public final void onClick(View view2) {
                    ThemeSetUrlActivity.this.lambda$createView$5(context, view2);
                }
            });
            TextInfoPrivacyCell textInfoPrivacyCell4 = new TextInfoPrivacyCell(context);
            this.createInfoCell = textInfoPrivacyCell4;
            textInfoPrivacyCell4.setText(AndroidUtilities.replaceTags(LocaleController.getString(R.string.UseDifferentThemeInfo)));
            this.createInfoCell.setBackgroundDrawable(Theme.getThemedDrawableByKey(context, i4, i5));
            linearLayout2.addView(this.createInfoCell, LayoutHelper.createLinear(-1, -2));
        } else {
            this.helpInfoCell.setBackgroundDrawable(Theme.getThemedDrawableByKey(context, i4, i5));
        }
        TLRPC.TL_theme tL_theme = this.info;
        if (tL_theme != null) {
            this.ignoreCheck = true;
            this.nameField.setText(tL_theme.title);
            EditTextBoldCursor editTextBoldCursor6 = this.nameField;
            editTextBoldCursor6.setSelection(editTextBoldCursor6.length());
            this.linkField.setText(this.info.slug);
            EditTextBoldCursor editTextBoldCursor7 = this.linkField;
            editTextBoldCursor7.setSelection(editTextBoldCursor7.length());
            this.ignoreCheck = false;
        }
        return this.fragmentView;
    }

    @Override
    public void didReceivedNotification(int i, int i2, Object... objArr) {
        AlertDialog alertDialog;
        AlertDialog alertDialog2;
        if (i == NotificationCenter.themeUploadedToServer) {
            Theme.ThemeInfo themeInfo = (Theme.ThemeInfo) objArr[0];
            Theme.ThemeAccent themeAccent = (Theme.ThemeAccent) objArr[1];
            if (themeInfo == this.themeInfo && themeAccent == this.themeAccent && (alertDialog2 = this.progressDialog) != null) {
                try {
                    alertDialog2.dismiss();
                    this.progressDialog = null;
                } catch (Exception e) {
                    FileLog.e(e);
                }
                Theme.applyTheme(this.themeInfo, false);
                lambda$onBackPressed$319();
                return;
            }
            return;
        }
        if (i == NotificationCenter.themeUploadError) {
            Theme.ThemeInfo themeInfo2 = (Theme.ThemeInfo) objArr[0];
            Theme.ThemeAccent themeAccent2 = (Theme.ThemeAccent) objArr[1];
            if (themeInfo2 == this.themeInfo && themeAccent2 == this.themeAccent && (alertDialog = this.progressDialog) != null) {
                try {
                    alertDialog.dismiss();
                    this.progressDialog = null;
                } catch (Exception e2) {
                    FileLog.e(e2);
                }
            }
        }
    }

    @Override
    public ArrayList getThemeDescriptions() {
        ArrayList arrayList = new ArrayList();
        arrayList.add(new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, Theme.key_windowBackgroundGray));
        LinearLayout linearLayout = this.linearLayoutTypeContainer;
        int i = ThemeDescription.FLAG_BACKGROUND;
        int i2 = Theme.key_windowBackgroundWhite;
        arrayList.add(new ThemeDescription(linearLayout, i, null, null, null, null, i2));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, Theme.key_actionBarDefault));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, null, null, null, null, Theme.key_actionBarDefaultIcon));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, null, null, null, null, Theme.key_actionBarDefaultTitle));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, null, null, null, null, Theme.key_actionBarDefaultSelector));
        arrayList.add(new ThemeDescription(this.headerCell, 0, new Class[]{HeaderCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteBlueHeader));
        int i3 = Theme.key_windowBackgroundGrayShadow;
        arrayList.add(new ThemeDescription(this.createInfoCell, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{TextInfoPrivacyCell.class}, null, null, null, i3));
        int i4 = Theme.key_windowBackgroundWhiteGrayText4;
        arrayList.add(new ThemeDescription(this.createInfoCell, 0, new Class[]{TextInfoPrivacyCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, i4));
        arrayList.add(new ThemeDescription(this.helpInfoCell, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{TextInfoPrivacyCell.class}, null, null, null, i3));
        arrayList.add(new ThemeDescription(this.helpInfoCell, 0, new Class[]{TextInfoPrivacyCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, i4));
        arrayList.add(new ThemeDescription(this.checkInfoCell, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{TextInfoPrivacyCell.class}, null, null, null, i3));
        arrayList.add(new ThemeDescription(this.checkInfoCell, ThemeDescription.FLAG_CHECKTAG, new Class[]{TextInfoPrivacyCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_text_RedRegular));
        arrayList.add(new ThemeDescription(this.checkInfoCell, ThemeDescription.FLAG_CHECKTAG, new Class[]{TextInfoPrivacyCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteGrayText8));
        arrayList.add(new ThemeDescription(this.checkInfoCell, ThemeDescription.FLAG_CHECKTAG, new Class[]{TextInfoPrivacyCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteGreenText));
        int i5 = Theme.key_windowBackgroundWhiteBlackText;
        arrayList.add(new ThemeDescription(this.createCell, 0, new Class[]{TextSettingsCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, i5));
        arrayList.add(new ThemeDescription(this.createCell, ThemeDescription.FLAG_SELECTORWHITE, null, null, null, null, Theme.key_listSelector));
        arrayList.add(new ThemeDescription(this.createCell, ThemeDescription.FLAG_SELECTORWHITE, null, null, null, null, i2));
        arrayList.add(new ThemeDescription(this.linkField, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, i5));
        EditTextBoldCursor editTextBoldCursor = this.linkField;
        int i6 = ThemeDescription.FLAG_HINTTEXTCOLOR;
        int i7 = Theme.key_windowBackgroundWhiteHintText;
        arrayList.add(new ThemeDescription(editTextBoldCursor, i6, null, null, null, null, i7));
        arrayList.add(new ThemeDescription(this.linkField, ThemeDescription.FLAG_BACKGROUNDFILTER, null, null, null, null, Theme.key_windowBackgroundWhiteInputField));
        arrayList.add(new ThemeDescription(this.linkField, ThemeDescription.FLAG_BACKGROUNDFILTER | ThemeDescription.FLAG_DRAWABLESELECTEDSTATE, null, null, null, null, Theme.key_windowBackgroundWhiteInputFieldActivated));
        arrayList.add(new ThemeDescription(this.linkField, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, i5));
        arrayList.add(new ThemeDescription(this.linkField, ThemeDescription.FLAG_HINTTEXTCOLOR, null, null, null, null, i7));
        arrayList.add(new ThemeDescription(this.linkField, ThemeDescription.FLAG_CURSORCOLOR, null, null, null, null, i5));
        arrayList.add(new ThemeDescription(this.nameField, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, i5));
        arrayList.add(new ThemeDescription(this.nameField, ThemeDescription.FLAG_HINTTEXTCOLOR, null, null, null, null, i7));
        arrayList.add(new ThemeDescription(this.nameField, ThemeDescription.FLAG_CURSORCOLOR, null, null, null, null, i5));
        arrayList.add(new ThemeDescription(this.editText, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, i5));
        arrayList.add(new ThemeDescription(this.editText, ThemeDescription.FLAG_HINTTEXTCOLOR, null, null, null, null, i7));
        View view = this.divider;
        Paint paint = Theme.dividerPaint;
        int i8 = Theme.key_divider;
        arrayList.add(new ThemeDescription(view, 0, null, paint, null, null, i8));
        arrayList.add(new ThemeDescription(this.divider, ThemeDescription.FLAG_BACKGROUND, null, Theme.dividerPaint, null, null, i8));
        arrayList.add(new ThemeDescription(this.messagesCell, 0, null, null, new Drawable[]{Theme.chat_msgInDrawable, Theme.chat_msgInMediaDrawable}, null, Theme.key_chat_inBubble));
        arrayList.add(new ThemeDescription(this.messagesCell, 0, null, null, new Drawable[]{Theme.chat_msgInSelectedDrawable, Theme.chat_msgInMediaSelectedDrawable}, null, Theme.key_chat_inBubbleSelected));
        ThemePreviewMessagesCell themePreviewMessagesCell = this.messagesCell;
        Drawable[] shadowDrawables = Theme.chat_msgInDrawable.getShadowDrawables();
        int i9 = Theme.key_chat_inBubbleShadow;
        arrayList.add(new ThemeDescription(themePreviewMessagesCell, 0, null, null, shadowDrawables, null, i9));
        arrayList.add(new ThemeDescription(this.messagesCell, 0, null, null, Theme.chat_msgInMediaDrawable.getShadowDrawables(), null, i9));
        arrayList.add(new ThemeDescription(this.messagesCell, 0, null, null, new Drawable[]{Theme.chat_msgOutDrawable, Theme.chat_msgOutMediaDrawable}, null, Theme.key_chat_outBubble));
        arrayList.add(new ThemeDescription(this.messagesCell, 0, null, null, new Drawable[]{Theme.chat_msgOutDrawable, Theme.chat_msgOutMediaDrawable}, null, Theme.key_chat_outBubbleGradient1));
        arrayList.add(new ThemeDescription(this.messagesCell, 0, null, null, new Drawable[]{Theme.chat_msgOutDrawable, Theme.chat_msgOutMediaDrawable}, null, Theme.key_chat_outBubbleGradient2));
        arrayList.add(new ThemeDescription(this.messagesCell, 0, null, null, new Drawable[]{Theme.chat_msgOutDrawable, Theme.chat_msgOutMediaDrawable}, null, Theme.key_chat_outBubbleGradient3));
        arrayList.add(new ThemeDescription(this.messagesCell, 0, null, null, new Drawable[]{Theme.chat_msgOutSelectedDrawable, Theme.chat_msgOutMediaSelectedDrawable}, null, Theme.key_chat_outBubbleSelected));
        ThemePreviewMessagesCell themePreviewMessagesCell2 = this.messagesCell;
        Drawable[] shadowDrawables2 = Theme.chat_msgOutDrawable.getShadowDrawables();
        int i10 = Theme.key_chat_outBubbleShadow;
        arrayList.add(new ThemeDescription(themePreviewMessagesCell2, 0, null, null, shadowDrawables2, null, i10));
        arrayList.add(new ThemeDescription(this.messagesCell, 0, null, null, Theme.chat_msgOutMediaDrawable.getShadowDrawables(), null, i10));
        arrayList.add(new ThemeDescription(this.messagesCell, 0, null, null, null, null, Theme.key_chat_messageTextIn));
        arrayList.add(new ThemeDescription(this.messagesCell, 0, null, null, null, null, Theme.key_chat_messageTextOut));
        arrayList.add(new ThemeDescription(this.messagesCell, 0, null, null, new Drawable[]{Theme.chat_msgOutCheckDrawable}, null, Theme.key_chat_outSentCheck));
        arrayList.add(new ThemeDescription(this.messagesCell, 0, null, null, new Drawable[]{Theme.chat_msgOutCheckSelectedDrawable}, null, Theme.key_chat_outSentCheckSelected));
        arrayList.add(new ThemeDescription(this.messagesCell, 0, null, null, new Drawable[]{Theme.chat_msgOutCheckReadDrawable, Theme.chat_msgOutHalfCheckDrawable}, null, Theme.key_chat_outSentCheckRead));
        arrayList.add(new ThemeDescription(this.messagesCell, 0, null, null, new Drawable[]{Theme.chat_msgOutCheckReadSelectedDrawable, Theme.chat_msgOutHalfCheckSelectedDrawable}, null, Theme.key_chat_outSentCheckReadSelected));
        arrayList.add(new ThemeDescription(this.messagesCell, 0, null, null, new Drawable[]{Theme.chat_msgMediaCheckDrawable, Theme.chat_msgMediaHalfCheckDrawable}, null, Theme.key_chat_mediaSentCheck));
        arrayList.add(new ThemeDescription(this.messagesCell, 0, null, null, null, null, Theme.key_chat_inReplyLine));
        arrayList.add(new ThemeDescription(this.messagesCell, 0, null, null, null, null, Theme.key_chat_outReplyLine));
        arrayList.add(new ThemeDescription(this.messagesCell, 0, null, null, null, null, Theme.key_chat_inReplyNameText));
        arrayList.add(new ThemeDescription(this.messagesCell, 0, null, null, null, null, Theme.key_chat_outReplyNameText));
        arrayList.add(new ThemeDescription(this.messagesCell, 0, null, null, null, null, Theme.key_chat_inReplyMessageText));
        arrayList.add(new ThemeDescription(this.messagesCell, 0, null, null, null, null, Theme.key_chat_outReplyMessageText));
        arrayList.add(new ThemeDescription(this.messagesCell, 0, null, null, null, null, Theme.key_chat_inReplyMediaMessageSelectedText));
        arrayList.add(new ThemeDescription(this.messagesCell, 0, null, null, null, null, Theme.key_chat_outReplyMediaMessageSelectedText));
        arrayList.add(new ThemeDescription(this.messagesCell, 0, null, null, null, null, Theme.key_chat_inTimeText));
        arrayList.add(new ThemeDescription(this.messagesCell, 0, null, null, null, null, Theme.key_chat_outTimeText));
        arrayList.add(new ThemeDescription(this.messagesCell, 0, null, null, null, null, Theme.key_chat_inTimeSelectedText));
        arrayList.add(new ThemeDescription(this.messagesCell, 0, null, null, null, null, Theme.key_chat_outTimeSelectedText));
        return arrayList;
    }

    @Override
    public boolean onFragmentCreate() {
        getNotificationCenter().addObserver(this, NotificationCenter.themeUploadedToServer);
        getNotificationCenter().addObserver(this, NotificationCenter.themeUploadError);
        return super.onFragmentCreate();
    }

    @Override
    public void onFragmentDestroy() {
        super.onFragmentDestroy();
        getNotificationCenter().removeObserver(this, NotificationCenter.themeUploadedToServer);
        getNotificationCenter().removeObserver(this, NotificationCenter.themeUploadError);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!MessagesController.getGlobalMainSettings().getBoolean("view_animations", true) && this.creatingNewTheme) {
            this.linkField.requestFocus();
            AndroidUtilities.showKeyboard(this.linkField);
        }
        AndroidUtilities.requestAdjustResize(getParentActivity(), this.classGuid);
        AndroidUtilities.removeAdjustResize(getParentActivity(), this.classGuid);
    }

    @Override
    public void onTransitionAnimationEnd(boolean z, boolean z2) {
        if (!z || this.creatingNewTheme) {
            return;
        }
        this.linkField.requestFocus();
        AndroidUtilities.showKeyboard(this.linkField);
    }
}
