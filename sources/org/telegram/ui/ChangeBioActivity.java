package org.telegram.ui;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Vibrator;
import android.text.Editable;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import java.util.ArrayList;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.R;
import org.telegram.messenger.UserConfig;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.tgnet.tl.TL_account;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.ActionBarMenuItem;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ActionBar.ThemeDescription;
import org.telegram.ui.Components.AlertsCreator;
import org.telegram.ui.Components.CodepointsLengthInputFilter;
import org.telegram.ui.Components.EditTextBoldCursor;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.NumberTextView;

public class ChangeBioActivity extends BaseFragment {
    private NumberTextView checkTextView;
    private View doneButton;
    private EditTextBoldCursor firstNameField;
    private TextView helpTextView;

    public static boolean lambda$createView$0(View view, MotionEvent motionEvent) {
        return true;
    }

    public boolean lambda$createView$1(TextView textView, int i, KeyEvent keyEvent) {
        View view;
        if (i != 6 || (view = this.doneButton) == null) {
            return false;
        }
        view.performClick();
        return true;
    }

    public void lambda$saveName$2(AlertDialog alertDialog, TLRPC.UserFull userFull, String str, TLRPC.User user) {
        try {
            alertDialog.dismiss();
        } catch (Exception e) {
            FileLog.e(e);
        }
        userFull.about = str;
        NotificationCenter.getInstance(this.currentAccount).lambda$postNotificationNameOnUIThread$1(NotificationCenter.userInfoDidLoad, Long.valueOf(user.id), userFull);
        lambda$onBackPressed$323();
    }

    public void lambda$saveName$3(AlertDialog alertDialog, TLRPC.TL_error tL_error, TL_account.updateProfile updateprofile) {
        try {
            alertDialog.dismiss();
        } catch (Exception e) {
            FileLog.e(e);
        }
        AlertsCreator.processError(this.currentAccount, tL_error, this, updateprofile, new Object[0]);
    }

    public void lambda$saveName$4(final AlertDialog alertDialog, final TLRPC.UserFull userFull, final String str, final TL_account.updateProfile updateprofile, TLObject tLObject, final TLRPC.TL_error tL_error) {
        if (tL_error != null) {
            AndroidUtilities.runOnUIThread(new Runnable() {
                @Override
                public final void run() {
                    ChangeBioActivity.this.lambda$saveName$3(alertDialog, tL_error, updateprofile);
                }
            });
        } else {
            final TLRPC.User user = (TLRPC.User) tLObject;
            AndroidUtilities.runOnUIThread(new Runnable() {
                @Override
                public final void run() {
                    ChangeBioActivity.this.lambda$saveName$2(alertDialog, userFull, str, user);
                }
            });
        }
    }

    public void lambda$saveName$5(int i, DialogInterface dialogInterface) {
        ConnectionsManager.getInstance(this.currentAccount).cancelRequest(i, true);
    }

    public void saveName() {
        final TLRPC.UserFull userFull = MessagesController.getInstance(this.currentAccount).getUserFull(UserConfig.getInstance(this.currentAccount).getClientUserId());
        if (getParentActivity() == null || userFull == null) {
            return;
        }
        String str = userFull.about;
        if (str == null) {
            str = "";
        }
        final String replace = this.firstNameField.getText().toString().replace("\n", "");
        if (str.equals(replace)) {
            lambda$onBackPressed$323();
            return;
        }
        final AlertDialog alertDialog = new AlertDialog(getParentActivity(), 3);
        final TL_account.updateProfile updateprofile = new TL_account.updateProfile();
        updateprofile.about = replace;
        updateprofile.flags |= 4;
        final int sendRequest = ConnectionsManager.getInstance(this.currentAccount).sendRequest(updateprofile, new RequestDelegate() {
            @Override
            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                ChangeBioActivity.this.lambda$saveName$4(alertDialog, userFull, replace, updateprofile, tLObject, tL_error);
            }
        }, 2);
        ConnectionsManager.getInstance(this.currentAccount).bindRequestToGuid(sendRequest, this.classGuid);
        alertDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public final void onCancel(DialogInterface dialogInterface) {
                ChangeBioActivity.this.lambda$saveName$5(sendRequest, dialogInterface);
            }
        });
        alertDialog.show();
    }

    @Override
    public View createView(Context context) {
        String str;
        this.actionBar.setBackButtonImage(R.drawable.ic_ab_back);
        this.actionBar.setAllowOverlayTitle(true);
        ActionBar actionBar = this.actionBar;
        int i = R.string.UserBio;
        actionBar.setTitle(LocaleController.getString(i));
        this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
            @Override
            public void onItemClick(int i2) {
                if (i2 == -1) {
                    ChangeBioActivity.this.lambda$onBackPressed$323();
                } else if (i2 == 1) {
                    ChangeBioActivity.this.saveName();
                }
            }
        });
        ActionBarMenuItem addItemWithWidth = this.actionBar.createMenu().addItemWithWidth(1, R.drawable.ic_ab_done, AndroidUtilities.dp(56.0f));
        this.doneButton = addItemWithWidth;
        addItemWithWidth.setContentDescription(LocaleController.getString(R.string.Done));
        LinearLayout linearLayout = new LinearLayout(context);
        this.fragmentView = linearLayout;
        linearLayout.setOrientation(1);
        this.fragmentView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public final boolean onTouch(View view, MotionEvent motionEvent) {
                boolean lambda$createView$0;
                lambda$createView$0 = ChangeBioActivity.lambda$createView$0(view, motionEvent);
                return lambda$createView$0;
            }
        });
        FrameLayout frameLayout = new FrameLayout(context);
        linearLayout.addView(frameLayout, LayoutHelper.createLinear(-1, -2, 24.0f, 24.0f, 20.0f, 0.0f));
        EditTextBoldCursor editTextBoldCursor = new EditTextBoldCursor(context) {
            @Override
            public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo accessibilityNodeInfo) {
                super.onInitializeAccessibilityNodeInfo(accessibilityNodeInfo);
                Editable editableText = getEditableText();
                accessibilityNodeInfo.setText(((Object) getText()) + ", " + LocaleController.formatPluralString("PeopleJoinedRemaining", ChangeBioActivity.this.getMessagesController().getAboutLimit() - Character.codePointCount(editableText, 0, editableText.length()), new Object[0]));
            }
        };
        this.firstNameField = editTextBoldCursor;
        editTextBoldCursor.setTextSize(1, 18.0f);
        this.firstNameField.setHintTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteHintText));
        EditTextBoldCursor editTextBoldCursor2 = this.firstNameField;
        int i2 = Theme.key_windowBackgroundWhiteBlackText;
        editTextBoldCursor2.setTextColor(Theme.getColor(i2));
        this.firstNameField.setBackgroundDrawable(null);
        this.firstNameField.setLineColors(getThemedColor(Theme.key_windowBackgroundWhiteInputField), getThemedColor(Theme.key_windowBackgroundWhiteInputFieldActivated), getThemedColor(Theme.key_text_RedRegular));
        this.firstNameField.setMaxLines(4);
        this.firstNameField.setPadding(AndroidUtilities.dp(LocaleController.isRTL ? 24.0f : 0.0f), 0, AndroidUtilities.dp(LocaleController.isRTL ? 0.0f : 24.0f), AndroidUtilities.dp(6.0f));
        this.firstNameField.setGravity(LocaleController.isRTL ? 5 : 3);
        this.firstNameField.setImeOptions(268435456);
        this.firstNameField.setInputType(147457);
        this.firstNameField.setImeOptions(6);
        this.firstNameField.setFilters(new InputFilter[]{new CodepointsLengthInputFilter(getMessagesController().getAboutLimit()) {
            @Override
            public CharSequence filter(CharSequence charSequence, int i3, int i4, Spanned spanned, int i5, int i6) {
                if (charSequence != null && charSequence.length() > 0 && TextUtils.indexOf(charSequence, '\n') == charSequence.length() - 1) {
                    ChangeBioActivity.this.doneButton.performClick();
                    return "";
                }
                CharSequence filter = super.filter(charSequence, i3, i4, spanned, i5, i6);
                if (filter != null && charSequence != null && filter.length() != charSequence.length()) {
                    Vibrator vibrator = (Vibrator) ChangeBioActivity.this.getParentActivity().getSystemService("vibrator");
                    if (vibrator != null) {
                        vibrator.vibrate(200L);
                    }
                    AndroidUtilities.shakeView(ChangeBioActivity.this.checkTextView);
                }
                return filter;
            }
        }});
        this.firstNameField.setMinHeight(AndroidUtilities.dp(36.0f));
        this.firstNameField.setHint(LocaleController.getString(i));
        this.firstNameField.setCursorColor(Theme.getColor(i2));
        this.firstNameField.setCursorSize(AndroidUtilities.dp(20.0f));
        this.firstNameField.setCursorWidth(1.5f);
        this.firstNameField.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public final boolean onEditorAction(TextView textView, int i3, KeyEvent keyEvent) {
                boolean lambda$createView$1;
                lambda$createView$1 = ChangeBioActivity.this.lambda$createView$1(textView, i3, keyEvent);
                return lambda$createView$1;
            }
        });
        this.firstNameField.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable editable) {
                ChangeBioActivity.this.checkTextView.setNumber(ChangeBioActivity.this.getMessagesController().getAboutLimit() - Character.codePointCount(editable, 0, editable.length()), true);
            }

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i3, int i4, int i5) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i3, int i4, int i5) {
            }
        });
        frameLayout.addView(this.firstNameField, LayoutHelper.createFrame(-1, -2.0f, 51, 0.0f, 0.0f, 4.0f, 0.0f));
        NumberTextView numberTextView = new NumberTextView(context);
        this.checkTextView = numberTextView;
        numberTextView.setCenterAlign(true);
        this.checkTextView.setTextSize(15);
        this.checkTextView.setNumber(getMessagesController().getAboutLimit(), false);
        this.checkTextView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteGrayText4));
        this.checkTextView.setImportantForAccessibility(2);
        frameLayout.addView(this.checkTextView, LayoutHelper.createFrame(26, 20.0f, LocaleController.isRTL ? 3 : 5, 0.0f, 4.0f, 4.0f, 0.0f));
        TextView textView = new TextView(context);
        this.helpTextView = textView;
        textView.setFocusable(true);
        this.helpTextView.setTextSize(1, 15.0f);
        this.helpTextView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteGrayText8));
        this.helpTextView.setGravity(LocaleController.isRTL ? 5 : 3);
        this.helpTextView.setText(AndroidUtilities.replaceTags(LocaleController.getString(R.string.UserBioInfo)));
        linearLayout.addView(this.helpTextView, LayoutHelper.createLinear(-2, -2, LocaleController.isRTL ? 5 : 3, 24, 10, 24, 0));
        TLRPC.UserFull userFull = MessagesController.getInstance(this.currentAccount).getUserFull(UserConfig.getInstance(this.currentAccount).getClientUserId());
        if (userFull != null && (str = userFull.about) != null) {
            this.firstNameField.setText(str);
            EditTextBoldCursor editTextBoldCursor3 = this.firstNameField;
            editTextBoldCursor3.setSelection(editTextBoldCursor3.length());
        }
        return this.fragmentView;
    }

    @Override
    public ArrayList getThemeDescriptions() {
        ArrayList arrayList = new ArrayList();
        arrayList.add(new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, Theme.key_windowBackgroundWhite));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, Theme.key_actionBarDefault));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, null, null, null, null, Theme.key_actionBarDefaultIcon));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, null, null, null, null, Theme.key_actionBarDefaultTitle));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, null, null, null, null, Theme.key_actionBarDefaultSelector));
        arrayList.add(new ThemeDescription(this.firstNameField, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, Theme.key_windowBackgroundWhiteBlackText));
        arrayList.add(new ThemeDescription(this.firstNameField, ThemeDescription.FLAG_HINTTEXTCOLOR, null, null, null, null, Theme.key_windowBackgroundWhiteHintText));
        arrayList.add(new ThemeDescription(this.firstNameField, ThemeDescription.FLAG_BACKGROUNDFILTER, null, null, null, null, Theme.key_windowBackgroundWhiteInputField));
        arrayList.add(new ThemeDescription(this.firstNameField, ThemeDescription.FLAG_DRAWABLESELECTEDSTATE | ThemeDescription.FLAG_BACKGROUNDFILTER, null, null, null, null, Theme.key_windowBackgroundWhiteInputFieldActivated));
        arrayList.add(new ThemeDescription(this.helpTextView, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, Theme.key_windowBackgroundWhiteGrayText8));
        arrayList.add(new ThemeDescription(this.checkTextView, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, Theme.key_windowBackgroundWhiteGrayText4));
        return arrayList;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (MessagesController.getGlobalMainSettings().getBoolean("view_animations", true)) {
            return;
        }
        this.firstNameField.requestFocus();
        AndroidUtilities.showKeyboard(this.firstNameField);
    }

    @Override
    public void onTransitionAnimationEnd(boolean z, boolean z2) {
        if (z) {
            this.firstNameField.requestFocus();
            AndroidUtilities.showKeyboard(this.firstNameField);
        }
    }
}
