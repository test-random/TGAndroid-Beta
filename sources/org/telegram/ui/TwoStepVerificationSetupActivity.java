package org.telegram.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.StateListAnimator;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.graphics.Canvas;
import android.graphics.Outline;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.PasswordTransformationMethod;
import android.util.Property;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewOutlineProvider;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.ColorUtils;
import java.util.ArrayList;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.R;
import org.telegram.messenger.SRPHelper;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.Utilities;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.SimpleTextView;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ActionBar.ThemeDescription;
import org.telegram.ui.Components.AlertsCreator;
import org.telegram.ui.Components.CombinedDrawable;
import org.telegram.ui.Components.CubicBezierInterpolator;
import org.telegram.ui.Components.CustomPhoneKeyboardView;
import org.telegram.ui.Components.EditTextBoldCursor;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.OutlineTextContainerView;
import org.telegram.ui.Components.RLottieDrawable;
import org.telegram.ui.Components.RLottieImageView;
import org.telegram.ui.Components.RadialProgressView;
import org.telegram.ui.Components.SizeNotifierFrameLayout;
import org.telegram.ui.Components.TextStyleSpan;
import org.telegram.ui.Components.TransformableLoginButtonView;
import org.telegram.ui.Components.VerticalPositionAutoAnimator;
import org.telegram.ui.Components.spoilers.SpoilersTextView;
import org.telegram.ui.TwoStepVerificationSetupActivity;

public class TwoStepVerificationSetupActivity extends BaseFragment {
    private AnimatorSet actionBarAnimator;
    private View actionBarBackground;
    private RLottieDrawable[] animationDrawables;
    private TextView bottomSkipButton;
    private AnimatorSet buttonAnimation;
    private TextView buttonTextView;
    private boolean closeAfterSet;
    private CodeFieldContainer codeFieldContainer;
    private TLRPC.account_Password currentPassword;
    private byte[] currentPasswordHash;
    private byte[] currentSecret;
    private long currentSecretId;
    private int currentType;
    private TextView descriptionText;
    private TextView descriptionText2;
    private TextView descriptionText3;
    private boolean doneAfterPasswordLoad;
    private EditTextBoldCursor editTextFirstRow;
    private EditTextBoldCursor editTextSecondRow;
    private String email;
    private String emailCode;
    private int emailCodeLength;
    private boolean emailOnly;
    private Runnable errorColorTimeout;
    private Runnable finishCallback;
    private String firstPassword;
    private VerticalPositionAutoAnimator floatingAutoAnimator;
    private FrameLayout floatingButtonContainer;
    private TransformableLoginButtonView floatingButtonIcon;
    private RadialProgressView floatingProgressView;
    private ArrayList fragmentsToClose;
    private boolean fromRegistration;
    private String hint;
    private boolean ignoreTextChange;
    private RLottieImageView imageView;
    private boolean isPasswordVisible;
    private CustomPhoneKeyboardView keyboardView;
    private Runnable monkeyEndCallback;
    private boolean needPasswordButton;
    private int otherwiseReloginDays;
    private OutlineTextContainerView outlineTextFirstRow;
    private OutlineTextContainerView outlineTextSecondRow;
    private boolean paused;
    private boolean postedErrorColorTimeout;
    private RadialProgressView radialProgressView;
    private ScrollView scrollView;
    private Runnable setAnimationRunnable;
    private ImageView showPasswordButton;
    private TextView titleTextView;
    private boolean waitingForEmail;

    public class AnonymousClass1 extends ActionBar.ActionBarMenuOnItemClick {
        AnonymousClass1() {
        }

        public void lambda$onItemClick$0(DialogInterface dialogInterface, int i) {
            TwoStepVerificationSetupActivity.this.setNewPassword(true);
        }

        @Override
        public void onItemClick(int i) {
            if (i == -1) {
                if (TwoStepVerificationSetupActivity.this.otherwiseReloginDays < 0 || ((BaseFragment) TwoStepVerificationSetupActivity.this).parentLayout.getFragmentStack().size() != 1) {
                    TwoStepVerificationSetupActivity.this.lambda$onBackPressed$321();
                    return;
                } else {
                    TwoStepVerificationSetupActivity.this.showSetForcePasswordAlert();
                    return;
                }
            }
            if (i == 1) {
                AlertDialog.Builder builder = new AlertDialog.Builder(TwoStepVerificationSetupActivity.this.getParentActivity());
                String string = LocaleController.getString((TwoStepVerificationSetupActivity.this.currentPassword == null || !TwoStepVerificationSetupActivity.this.currentPassword.has_password) ? R.string.CancelPasswordQuestion : R.string.CancelEmailQuestion);
                String string2 = LocaleController.getString(R.string.CancelEmailQuestionTitle);
                String string3 = LocaleController.getString(R.string.Abort);
                builder.setMessage(string);
                builder.setTitle(string2);
                builder.setPositiveButton(string3, new DialogInterface.OnClickListener() {
                    @Override
                    public final void onClick(DialogInterface dialogInterface, int i2) {
                        TwoStepVerificationSetupActivity.AnonymousClass1.this.lambda$onItemClick$0(dialogInterface, i2);
                    }
                });
                builder.setNegativeButton(LocaleController.getString(R.string.Cancel), null);
                AlertDialog create = builder.create();
                TwoStepVerificationSetupActivity.this.showDialog(create);
                TextView textView = (TextView) create.getButton(-1);
                if (textView != null) {
                    textView.setTextColor(Theme.getColor(Theme.key_text_RedBold));
                }
            }
        }
    }

    public TwoStepVerificationSetupActivity(int i, int i2, TLRPC.account_Password account_password) {
        this.needPasswordButton = false;
        this.otherwiseReloginDays = -1;
        this.fragmentsToClose = new ArrayList();
        this.emailCodeLength = 6;
        this.currentPasswordHash = new byte[0];
        this.errorColorTimeout = new Runnable() {
            @Override
            public final void run() {
                TwoStepVerificationSetupActivity.this.lambda$new$0();
            }
        };
        this.finishCallback = new Runnable() {
            @Override
            public final void run() {
                TwoStepVerificationSetupActivity.this.lambda$new$1();
            }
        };
        this.currentAccount = i;
        this.currentType = i2;
        this.currentPassword = account_password;
        this.waitingForEmail = !TextUtils.isEmpty(account_password.email_unconfirmed_pattern);
        if (this.currentPassword == null) {
            int i3 = this.currentType;
            if (i3 == 6 || i3 == 8) {
                loadPasswordInfo();
            }
        }
    }

    public TwoStepVerificationSetupActivity(int i, TLRPC.account_Password account_password) {
        this.needPasswordButton = false;
        this.otherwiseReloginDays = -1;
        this.fragmentsToClose = new ArrayList();
        this.emailCodeLength = 6;
        this.currentPasswordHash = new byte[0];
        this.errorColorTimeout = new Runnable() {
            @Override
            public final void run() {
                TwoStepVerificationSetupActivity.this.lambda$new$0();
            }
        };
        this.finishCallback = new Runnable() {
            @Override
            public final void run() {
                TwoStepVerificationSetupActivity.this.lambda$new$1();
            }
        };
        this.currentType = i;
        this.currentPassword = account_password;
        if (account_password == null && (i == 6 || i == 8)) {
            loadPasswordInfo();
        } else {
            this.waitingForEmail = !TextUtils.isEmpty(account_password.email_unconfirmed_pattern);
        }
    }

    private void animateSuccess(final Runnable runnable) {
        int i = 0;
        while (true) {
            CodeFieldContainer codeFieldContainer = this.codeFieldContainer;
            CodeNumberField[] codeNumberFieldArr = codeFieldContainer.codeField;
            if (i >= codeNumberFieldArr.length) {
                codeFieldContainer.postDelayed(new Runnable() {
                    @Override
                    public final void run() {
                        TwoStepVerificationSetupActivity.this.lambda$animateSuccess$22(runnable);
                    }
                }, (this.codeFieldContainer.codeField.length * 75) + 350);
                return;
            } else {
                final CodeNumberField codeNumberField = codeNumberFieldArr[i];
                codeNumberField.postDelayed(new Runnable() {
                    @Override
                    public final void run() {
                        CodeNumberField.this.animateSuccessProgress(1.0f);
                    }
                }, i * 75);
                i++;
            }
        }
    }

    public boolean isCustomKeyboardVisible() {
        int i = this.currentType;
        if ((i == 5 || i == 4) && !AndroidUtilities.isTablet()) {
            Point point = AndroidUtilities.displaySize;
            if (point.x < point.y && !AndroidUtilities.isAccessibilityTouchExplorationEnabled()) {
                return true;
            }
        }
        return false;
    }

    private boolean isIntro() {
        int i = this.currentType;
        return i == 6 || i == 9 || i == 7;
    }

    public boolean isLandscape() {
        Point point = AndroidUtilities.displaySize;
        return point.x > point.y;
    }

    private boolean isValidEmail(String str) {
        if (str == null || str.length() < 3) {
            return false;
        }
        int lastIndexOf = str.lastIndexOf(46);
        int lastIndexOf2 = str.lastIndexOf(64);
        return lastIndexOf2 >= 0 && lastIndexOf >= lastIndexOf2;
    }

    public void lambda$animateSuccess$22(Runnable runnable) {
        for (CodeNumberField codeNumberField : this.codeFieldContainer.codeField) {
            codeNumberField.animateSuccessProgress(0.0f);
        }
        runnable.run();
    }

    public static boolean lambda$createView$10(View view, MotionEvent motionEvent) {
        return true;
    }

    public boolean lambda$createView$11(TextView textView, int i, KeyEvent keyEvent) {
        if (i != 5 && i != 6) {
            return false;
        }
        if (this.outlineTextSecondRow.getVisibility() == 0) {
            this.editTextSecondRow.requestFocus();
            return true;
        }
        processNext();
        return true;
    }

    public void lambda$createView$12(View view, boolean z) {
        this.outlineTextFirstRow.animateSelection(z ? 1.0f : 0.0f);
    }

    public void lambda$createView$13(View view) {
        this.ignoreTextChange = true;
        if (this.editTextFirstRow.getTransformationMethod() == null) {
            this.isPasswordVisible = false;
            this.editTextFirstRow.setTransformationMethod(PasswordTransformationMethod.getInstance());
            this.showPasswordButton.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_chat_messagePanelIcons), PorterDuff.Mode.MULTIPLY));
            if (this.currentType == 0 && this.editTextFirstRow.length() > 0 && this.editTextFirstRow.hasFocus() && this.monkeyEndCallback == null) {
                this.animationDrawables[3].setCustomEndFrame(-1);
                RLottieDrawable animatedDrawable = this.imageView.getAnimatedDrawable();
                RLottieDrawable rLottieDrawable = this.animationDrawables[3];
                if (animatedDrawable != rLottieDrawable) {
                    this.imageView.setAnimation(rLottieDrawable);
                    this.animationDrawables[3].setCurrentFrame(18, false);
                }
                this.imageView.playAnimation();
            }
        } else {
            this.isPasswordVisible = true;
            this.editTextFirstRow.setTransformationMethod(null);
            this.showPasswordButton.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_chat_messagePanelSend), PorterDuff.Mode.MULTIPLY));
            if (this.currentType == 0 && this.editTextFirstRow.length() > 0 && this.editTextFirstRow.hasFocus() && this.monkeyEndCallback == null) {
                this.animationDrawables[3].setCustomEndFrame(18);
                RLottieDrawable animatedDrawable2 = this.imageView.getAnimatedDrawable();
                RLottieDrawable rLottieDrawable2 = this.animationDrawables[3];
                if (animatedDrawable2 != rLottieDrawable2) {
                    this.imageView.setAnimation(rLottieDrawable2);
                }
                this.animationDrawables[3].setProgress(0.0f, false);
                this.imageView.playAnimation();
            }
        }
        EditTextBoldCursor editTextBoldCursor = this.editTextFirstRow;
        editTextBoldCursor.setSelection(editTextBoldCursor.length());
        this.ignoreTextChange = false;
    }

    public boolean lambda$createView$14(TextView textView, int i, KeyEvent keyEvent) {
        if (i != 5 && i != 6) {
            return false;
        }
        processNext();
        return true;
    }

    public void lambda$createView$15(View view, boolean z) {
        this.outlineTextSecondRow.animateSelection(z ? 1.0f : 0.0f);
    }

    public void lambda$createView$16(View view, boolean z) {
        if (z) {
            this.keyboardView.setEditText((EditText) view);
            this.keyboardView.setDispatchBackWhenEmpty(true);
        }
    }

    public void lambda$createView$17(DialogInterface dialogInterface, int i) {
        onReset();
        lambda$onBackPressed$321();
    }

    public void lambda$createView$18(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getParentActivity());
        builder.setNegativeButton(LocaleController.getString(R.string.Cancel), null);
        builder.setPositiveButton(LocaleController.getString(R.string.Reset), new DialogInterface.OnClickListener() {
            @Override
            public final void onClick(DialogInterface dialogInterface, int i) {
                TwoStepVerificationSetupActivity.this.lambda$createView$17(dialogInterface, i);
            }
        });
        builder.setTitle(LocaleController.getString(R.string.ResetPassword));
        builder.setMessage(LocaleController.getString(R.string.RestoreEmailTroubleText2));
        showDialog(builder.create());
    }

    public static void lambda$createView$19(TLObject tLObject, TLRPC.TL_error tL_error) {
    }

    public void lambda$createView$2(View view) {
        processNext();
    }

    public void lambda$createView$20(View view) {
        ConnectionsManager.getInstance(this.currentAccount).sendRequest(new TLRPC.TL_account_resendPasswordEmail(), new RequestDelegate() {
            @Override
            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                TwoStepVerificationSetupActivity.lambda$createView$19(tLObject, tL_error);
            }
        });
        showDialog(new AlertDialog.Builder(getParentActivity()).setMessage(LocaleController.getString(R.string.ResendCodeInfo)).setTitle(LocaleController.getString(R.string.TwoStepVerificationTitle)).setPositiveButton(LocaleController.getString(R.string.OK), null).create());
    }

    public void lambda$createView$3(DialogInterface dialogInterface, int i) {
        int size = this.fragmentsToClose.size();
        for (int i2 = 0; i2 < size; i2++) {
            ((BaseFragment) this.fragmentsToClose.get(i2)).removeSelfFromStack();
        }
        NotificationCenter.getInstance(this.currentAccount).lambda$postNotificationNameOnUIThread$1(NotificationCenter.twoStepPasswordChanged, new Object[0]);
        lambda$onBackPressed$321();
    }

    public void lambda$createView$4(TLRPC.TL_error tL_error) {
        needHideProgress();
        if (tL_error != null) {
            if (!tL_error.text.startsWith("FLOOD_WAIT")) {
                showAlertWithText(LocaleController.getString(R.string.TwoStepVerificationTitle), tL_error.text);
                return;
            } else {
                int intValue = Utilities.parseInt((CharSequence) tL_error.text).intValue();
                showAlertWithText(LocaleController.getString(R.string.TwoStepVerificationTitle), LocaleController.formatString("FloodWaitTime", R.string.FloodWaitTime, intValue < 60 ? LocaleController.formatPluralString("Seconds", intValue, new Object[0]) : LocaleController.formatPluralString("Minutes", intValue / 60, new Object[0])));
                return;
            }
        }
        getMessagesController().removeSuggestion(0L, "VALIDATE_PASSWORD");
        AlertDialog.Builder builder = new AlertDialog.Builder(getParentActivity());
        builder.setPositiveButton(LocaleController.getString(R.string.OK), new DialogInterface.OnClickListener() {
            @Override
            public final void onClick(DialogInterface dialogInterface, int i) {
                TwoStepVerificationSetupActivity.this.lambda$createView$3(dialogInterface, i);
            }
        });
        builder.setMessage(LocaleController.getString(R.string.PasswordReset));
        builder.setTitle(LocaleController.getString(R.string.TwoStepVerificationTitle));
        Dialog showDialog = showDialog(builder.create());
        if (showDialog != null) {
            showDialog.setCanceledOnTouchOutside(false);
            showDialog.setCancelable(false);
        }
    }

    public void lambda$createView$5(TLObject tLObject, final TLRPC.TL_error tL_error) {
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                TwoStepVerificationSetupActivity.this.lambda$createView$4(tL_error);
            }
        });
    }

    public void lambda$createView$6(DialogInterface dialogInterface, int i) {
        this.email = "";
        setNewPassword(false);
    }

    public void lambda$createView$7(View view) {
        if (this.bottomSkipButton.getAlpha() < 0.5f) {
            return;
        }
        int i = this.currentType;
        if (i == 0) {
            needShowProgress();
            TLRPC.TL_auth_recoverPassword tL_auth_recoverPassword = new TLRPC.TL_auth_recoverPassword();
            tL_auth_recoverPassword.code = this.emailCode;
            getConnectionsManager().sendRequest(tL_auth_recoverPassword, new RequestDelegate() {
                @Override
                public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                    TwoStepVerificationSetupActivity.this.lambda$createView$5(tLObject, tL_error);
                }
            });
            return;
        }
        if (i != 3) {
            if (i == 2) {
                onHintDone();
                return;
            }
            return;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(getParentActivity());
        builder.setMessage(LocaleController.getString(R.string.YourEmailSkipWarningText));
        builder.setTitle(LocaleController.getString(R.string.YourEmailSkipWarning));
        builder.setPositiveButton(LocaleController.getString(R.string.YourEmailSkip), new DialogInterface.OnClickListener() {
            @Override
            public final void onClick(DialogInterface dialogInterface, int i2) {
                TwoStepVerificationSetupActivity.this.lambda$createView$6(dialogInterface, i2);
            }
        });
        builder.setNegativeButton(LocaleController.getString(R.string.Cancel), null);
        AlertDialog create = builder.create();
        showDialog(create);
        TextView textView = (TextView) create.getButton(-1);
        if (textView != null) {
            textView.setTextColor(Theme.getColor(Theme.key_text_RedBold));
        }
    }

    public void lambda$createView$8(View view) {
        if (this.currentType == 8) {
            TwoStepVerificationActivity twoStepVerificationActivity = new TwoStepVerificationActivity();
            twoStepVerificationActivity.setForgotPasswordOnShow();
            twoStepVerificationActivity.setPassword(this.currentPassword);
            twoStepVerificationActivity.setBlockingAlert(this.otherwiseReloginDays);
            presentFragment(twoStepVerificationActivity, true);
        }
    }

    public void lambda$createView$9(View view) {
        processNext();
    }

    public void lambda$loadPasswordInfo$41(TLRPC.TL_error tL_error, TLObject tLObject) {
        if (tL_error == null) {
            TLRPC.account_Password account_password = (TLRPC.account_Password) tLObject;
            this.currentPassword = account_password;
            if (!TwoStepVerificationActivity.canHandleCurrentPassword(account_password, false)) {
                AlertsCreator.showUpdateAppAlert(getParentActivity(), LocaleController.getString(R.string.UpdateAppAlert), true);
                return;
            }
            this.waitingForEmail = !TextUtils.isEmpty(this.currentPassword.email_unconfirmed_pattern);
            TwoStepVerificationActivity.initPasswordNewAlgo(this.currentPassword);
            if (!this.paused && this.closeAfterSet) {
                TLRPC.account_Password account_password2 = this.currentPassword;
                if (account_password2.has_password) {
                    TLRPC.PasswordKdfAlgo passwordKdfAlgo = account_password2.current_algo;
                    TLRPC.SecurePasswordKdfAlgo securePasswordKdfAlgo = account_password2.new_secure_algo;
                    byte[] bArr = account_password2.secure_random;
                    String str = account_password2.has_recovery ? "1" : null;
                    String str2 = account_password2.hint;
                    if (str2 == null) {
                        str2 = "";
                    }
                    if (!this.waitingForEmail && passwordKdfAlgo != null) {
                        NotificationCenter.getInstance(this.currentAccount).lambda$postNotificationNameOnUIThread$1(NotificationCenter.twoStepPasswordChanged, null, passwordKdfAlgo, securePasswordKdfAlgo, bArr, str, str2, null, null);
                        lambda$onBackPressed$321();
                    }
                }
            }
            if (this.doneAfterPasswordLoad) {
                needHideProgress();
                processNext();
            }
            NotificationCenter.getInstance(this.currentAccount).lambda$postNotificationNameOnUIThread$1(NotificationCenter.didSetOrRemoveTwoStepPassword, this.currentPassword);
        }
    }

    public void lambda$loadPasswordInfo$42(final TLObject tLObject, final TLRPC.TL_error tL_error) {
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                TwoStepVerificationSetupActivity.this.lambda$loadPasswordInfo$41(tL_error, tLObject);
            }
        });
    }

    public void lambda$new$0() {
        int i = 0;
        this.postedErrorColorTimeout = false;
        while (true) {
            CodeNumberField[] codeNumberFieldArr = this.codeFieldContainer.codeField;
            if (i >= codeNumberFieldArr.length) {
                return;
            }
            codeNumberFieldArr[i].animateErrorProgress(0.0f);
            i++;
        }
    }

    public void lambda$new$1() {
        EditTextBoldCursor editTextBoldCursor = this.editTextFirstRow;
        if (editTextBoldCursor == null) {
            return;
        }
        if (editTextBoldCursor.length() == 0) {
            setRandomMonkeyIdleAnimation(true);
            return;
        }
        this.animationDrawables[2].setCustomEndFrame(49);
        this.animationDrawables[2].setProgress(0.0f, false);
        this.imageView.playAnimation();
    }

    public void lambda$onCodeFieldError$36() {
        for (CodeNumberField codeNumberField : this.codeFieldContainer.codeField) {
            codeNumberField.animateErrorProgress(0.0f);
        }
    }

    public void lambda$onCodeFieldError$37() {
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                TwoStepVerificationSetupActivity.this.lambda$onCodeFieldError$36();
            }
        }, 150L);
    }

    public void lambda$onTransitionAnimationEnd$39() {
        EditTextBoldCursor editTextBoldCursor = this.editTextFirstRow;
        if (editTextBoldCursor == null || editTextBoldCursor.getVisibility() != 0) {
            return;
        }
        this.editTextFirstRow.requestFocus();
        AndroidUtilities.showKeyboard(this.editTextFirstRow);
    }

    public void lambda$onTransitionAnimationEnd$40() {
        CodeFieldContainer codeFieldContainer = this.codeFieldContainer;
        if (codeFieldContainer == null || codeFieldContainer.getVisibility() != 0) {
            return;
        }
        this.codeFieldContainer.codeField[0].requestFocus();
    }

    public void lambda$processNext$23(byte[] bArr) {
        needHideProgress();
        this.currentPasswordHash = bArr;
        getMessagesController().removeSuggestion(0L, "VALIDATE_PASSWORD");
        TwoStepVerificationSetupActivity twoStepVerificationSetupActivity = new TwoStepVerificationSetupActivity(9, this.currentPassword);
        twoStepVerificationSetupActivity.fromRegistration = this.fromRegistration;
        twoStepVerificationSetupActivity.setBlockingAlert(this.otherwiseReloginDays);
        presentFragment(twoStepVerificationSetupActivity, true);
    }

    public void lambda$processNext$24(TLRPC.TL_error tL_error, TLObject tLObject) {
        if (tL_error == null) {
            TLRPC.account_Password account_password = (TLRPC.account_Password) tLObject;
            this.currentPassword = account_password;
            TwoStepVerificationActivity.initPasswordNewAlgo(account_password);
            NotificationCenter.getInstance(this.currentAccount).lambda$postNotificationNameOnUIThread$1(NotificationCenter.didSetOrRemoveTwoStepPassword, this.currentPassword);
            processNext();
        }
    }

    public void lambda$processNext$25(final TLObject tLObject, final TLRPC.TL_error tL_error) {
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                TwoStepVerificationSetupActivity.this.lambda$processNext$24(tL_error, tLObject);
            }
        });
    }

    public void lambda$processNext$26(TLRPC.TL_error tL_error) {
        if ("SRP_ID_INVALID".equals(tL_error.text)) {
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(new TLRPC.TL_account_getPassword(), new RequestDelegate() {
                @Override
                public final void run(TLObject tLObject, TLRPC.TL_error tL_error2) {
                    TwoStepVerificationSetupActivity.this.lambda$processNext$25(tLObject, tL_error2);
                }
            }, 8);
            return;
        }
        needHideProgress();
        if ("PASSWORD_HASH_INVALID".equals(tL_error.text)) {
            this.descriptionText.setText(LocaleController.getString(R.string.CheckPasswordWrong));
            this.descriptionText.setTextColor(Theme.getColor(Theme.key_text_RedRegular));
            onFieldError(this.outlineTextFirstRow, this.editTextFirstRow, true);
            showDoneButton(false);
            return;
        }
        if (!tL_error.text.startsWith("FLOOD_WAIT")) {
            showAlertWithText(LocaleController.getString(R.string.AppName), tL_error.text);
        } else {
            int intValue = Utilities.parseInt((CharSequence) tL_error.text).intValue();
            showAlertWithText(LocaleController.getString(R.string.AppName), LocaleController.formatString("FloodWaitTime", R.string.FloodWaitTime, intValue < 60 ? LocaleController.formatPluralString("Seconds", intValue, new Object[0]) : LocaleController.formatPluralString("Minutes", intValue / 60, new Object[0])));
        }
    }

    public void lambda$processNext$27(final byte[] bArr, TLObject tLObject, final TLRPC.TL_error tL_error) {
        if (tL_error == null) {
            AndroidUtilities.runOnUIThread(new Runnable() {
                @Override
                public final void run() {
                    TwoStepVerificationSetupActivity.this.lambda$processNext$23(bArr);
                }
            });
        } else {
            AndroidUtilities.runOnUIThread(new Runnable() {
                @Override
                public final void run() {
                    TwoStepVerificationSetupActivity.this.lambda$processNext$26(tL_error);
                }
            });
        }
    }

    public void lambda$processNext$28(byte[] bArr) {
        TLRPC.TL_account_getPasswordSettings tL_account_getPasswordSettings = new TLRPC.TL_account_getPasswordSettings();
        TLRPC.PasswordKdfAlgo passwordKdfAlgo = this.currentPassword.current_algo;
        final byte[] x = passwordKdfAlgo instanceof TLRPC.TL_passwordKdfAlgoSHA256SHA256PBKDF2HMACSHA512iter100000SHA256ModPow ? SRPHelper.getX(bArr, (TLRPC.TL_passwordKdfAlgoSHA256SHA256PBKDF2HMACSHA512iter100000SHA256ModPow) passwordKdfAlgo) : null;
        RequestDelegate requestDelegate = new RequestDelegate() {
            @Override
            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                TwoStepVerificationSetupActivity.this.lambda$processNext$27(x, tLObject, tL_error);
            }
        };
        TLRPC.account_Password account_password = this.currentPassword;
        TLRPC.PasswordKdfAlgo passwordKdfAlgo2 = account_password.current_algo;
        if (!(passwordKdfAlgo2 instanceof TLRPC.TL_passwordKdfAlgoSHA256SHA256PBKDF2HMACSHA512iter100000SHA256ModPow)) {
            TLRPC.TL_error tL_error = new TLRPC.TL_error();
            tL_error.text = "PASSWORD_HASH_INVALID";
            requestDelegate.run(null, tL_error);
            return;
        }
        TLRPC.TL_inputCheckPasswordSRP startCheck = SRPHelper.startCheck(x, account_password.srp_id, account_password.srp_B, (TLRPC.TL_passwordKdfAlgoSHA256SHA256PBKDF2HMACSHA512iter100000SHA256ModPow) passwordKdfAlgo2);
        tL_account_getPasswordSettings.password = startCheck;
        if (startCheck != null) {
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(tL_account_getPasswordSettings, requestDelegate, 10);
            return;
        }
        TLRPC.TL_error tL_error2 = new TLRPC.TL_error();
        tL_error2.text = "ALGO_INVALID";
        requestDelegate.run(null, tL_error2);
    }

    public void lambda$processNext$29(String str) {
        TwoStepVerificationSetupActivity twoStepVerificationSetupActivity = new TwoStepVerificationSetupActivity(this.currentAccount, 0, this.currentPassword);
        twoStepVerificationSetupActivity.fromRegistration = this.fromRegistration;
        twoStepVerificationSetupActivity.fragmentsToClose.addAll(this.fragmentsToClose);
        twoStepVerificationSetupActivity.addFragmentToClose(this);
        twoStepVerificationSetupActivity.setCurrentEmailCode(str);
        twoStepVerificationSetupActivity.setBlockingAlert(this.otherwiseReloginDays);
        presentFragment(twoStepVerificationSetupActivity, true);
    }

    public void lambda$processNext$30(TLObject tLObject, final String str, TLRPC.TL_error tL_error) {
        if (tLObject instanceof TLRPC.TL_boolTrue) {
            animateSuccess(new Runnable() {
                @Override
                public final void run() {
                    TwoStepVerificationSetupActivity.this.lambda$processNext$29(str);
                }
            });
            return;
        }
        if (tL_error == null || tL_error.text.startsWith("CODE_INVALID")) {
            onCodeFieldError(true);
        } else if (!tL_error.text.startsWith("FLOOD_WAIT")) {
            showAlertWithText(LocaleController.getString(R.string.TwoStepVerificationTitle), tL_error.text);
        } else {
            int intValue = Utilities.parseInt((CharSequence) tL_error.text).intValue();
            showAlertWithText(LocaleController.getString(R.string.TwoStepVerificationTitle), LocaleController.formatString("FloodWaitTime", R.string.FloodWaitTime, intValue < 60 ? LocaleController.formatPluralString("Seconds", intValue, new Object[0]) : LocaleController.formatPluralString("Minutes", intValue / 60, new Object[0])));
        }
    }

    public void lambda$processNext$31(final String str, final TLObject tLObject, final TLRPC.TL_error tL_error) {
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                TwoStepVerificationSetupActivity.this.lambda$processNext$30(tLObject, str, tL_error);
            }
        });
    }

    public void lambda$processNext$32(DialogInterface dialogInterface, int i) {
        int size = this.fragmentsToClose.size();
        for (int i2 = 0; i2 < size; i2++) {
            ((BaseFragment) this.fragmentsToClose.get(i2)).removeSelfFromStack();
        }
        NotificationCenter notificationCenter = NotificationCenter.getInstance(this.currentAccount);
        int i3 = NotificationCenter.twoStepPasswordChanged;
        byte[] bArr = this.currentPasswordHash;
        TLRPC.account_Password account_password = this.currentPassword;
        notificationCenter.lambda$postNotificationNameOnUIThread$1(i3, bArr, account_password.new_algo, account_password.new_secure_algo, account_password.secure_random, this.email, this.hint, null, this.firstPassword);
        TwoStepVerificationActivity twoStepVerificationActivity = new TwoStepVerificationActivity();
        TLRPC.account_Password account_password2 = this.currentPassword;
        account_password2.has_password = true;
        account_password2.has_recovery = true;
        account_password2.email_unconfirmed_pattern = "";
        twoStepVerificationActivity.setCurrentPasswordParams(account_password2, this.currentPasswordHash, this.currentSecretId, this.currentSecret);
        twoStepVerificationActivity.setBlockingAlert(this.otherwiseReloginDays);
        presentFragment(twoStepVerificationActivity, true);
        NotificationCenter.getInstance(this.currentAccount).lambda$postNotificationNameOnUIThread$1(NotificationCenter.didSetOrRemoveTwoStepPassword, this.currentPassword);
    }

    public void lambda$processNext$33() {
        if (this.currentPassword.has_password) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getParentActivity());
            builder.setPositiveButton(LocaleController.getString(R.string.OK), new DialogInterface.OnClickListener() {
                @Override
                public final void onClick(DialogInterface dialogInterface, int i) {
                    TwoStepVerificationSetupActivity.this.lambda$processNext$32(dialogInterface, i);
                }
            });
            builder.setMessage(LocaleController.getString(this.currentPassword.has_recovery ? R.string.YourEmailSuccessChangedText : R.string.YourEmailSuccessText));
            builder.setTitle(LocaleController.getString(R.string.YourPasswordSuccess));
            Dialog showDialog = showDialog(builder.create());
            if (showDialog != null) {
                showDialog.setCanceledOnTouchOutside(false);
                showDialog.setCancelable(false);
                return;
            }
            return;
        }
        int size = this.fragmentsToClose.size();
        for (int i = 0; i < size; i++) {
            ((BaseFragment) this.fragmentsToClose.get(i)).removeSelfFromStack();
        }
        TLRPC.account_Password account_password = this.currentPassword;
        account_password.has_password = true;
        account_password.has_recovery = true;
        account_password.email_unconfirmed_pattern = "";
        TwoStepVerificationSetupActivity twoStepVerificationSetupActivity = new TwoStepVerificationSetupActivity(7, account_password);
        twoStepVerificationSetupActivity.fromRegistration = this.fromRegistration;
        twoStepVerificationSetupActivity.setCurrentPasswordParams(this.currentPasswordHash, this.currentSecretId, this.currentSecret, this.emailOnly);
        twoStepVerificationSetupActivity.fragmentsToClose.addAll(this.fragmentsToClose);
        twoStepVerificationSetupActivity.closeAfterSet = this.closeAfterSet;
        twoStepVerificationSetupActivity.setBlockingAlert(this.otherwiseReloginDays);
        presentFragment(twoStepVerificationSetupActivity, true);
        NotificationCenter notificationCenter = NotificationCenter.getInstance(this.currentAccount);
        int i2 = NotificationCenter.twoStepPasswordChanged;
        byte[] bArr = this.currentPasswordHash;
        TLRPC.account_Password account_password2 = this.currentPassword;
        notificationCenter.lambda$postNotificationNameOnUIThread$1(i2, bArr, account_password2.new_algo, account_password2.new_secure_algo, account_password2.secure_random, this.email, this.hint, null, this.firstPassword);
        NotificationCenter.getInstance(this.currentAccount).lambda$postNotificationNameOnUIThread$1(NotificationCenter.didSetOrRemoveTwoStepPassword, this.currentPassword);
    }

    public void lambda$processNext$34(TLRPC.TL_error tL_error) {
        needHideProgress();
        if (tL_error == null) {
            if (getParentActivity() == null) {
                return;
            }
            animateSuccess(new Runnable() {
                @Override
                public final void run() {
                    TwoStepVerificationSetupActivity.this.lambda$processNext$33();
                }
            });
        } else if (tL_error.text.startsWith("CODE_INVALID")) {
            onCodeFieldError(true);
        } else if (!tL_error.text.startsWith("FLOOD_WAIT")) {
            showAlertWithText(LocaleController.getString(R.string.AppName), tL_error.text);
        } else {
            int intValue = Utilities.parseInt((CharSequence) tL_error.text).intValue();
            showAlertWithText(LocaleController.getString(R.string.AppName), LocaleController.formatString("FloodWaitTime", R.string.FloodWaitTime, intValue < 60 ? LocaleController.formatPluralString("Seconds", intValue, new Object[0]) : LocaleController.formatPluralString("Minutes", intValue / 60, new Object[0])));
        }
    }

    public void lambda$processNext$35(TLObject tLObject, final TLRPC.TL_error tL_error) {
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                TwoStepVerificationSetupActivity.this.lambda$processNext$34(tL_error);
            }
        });
    }

    public void lambda$setNewPassword$43(TLRPC.TL_error tL_error) {
        needHideProgress();
        if (tL_error == null) {
            TwoStepVerificationActivity twoStepVerificationActivity = new TwoStepVerificationActivity();
            TLRPC.account_Password account_password = this.currentPassword;
            account_password.has_recovery = false;
            account_password.email_unconfirmed_pattern = "";
            twoStepVerificationActivity.setCurrentPasswordParams(account_password, this.currentPasswordHash, this.currentSecretId, this.currentSecret);
            twoStepVerificationActivity.setBlockingAlert(this.otherwiseReloginDays);
            presentFragment(twoStepVerificationActivity, true);
            NotificationCenter.getInstance(this.currentAccount).lambda$postNotificationNameOnUIThread$1(NotificationCenter.didRemoveTwoStepPassword, new Object[0]);
        }
    }

    public void lambda$setNewPassword$44(TLObject tLObject, final TLRPC.TL_error tL_error) {
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                TwoStepVerificationSetupActivity.this.lambda$setNewPassword$43(tL_error);
            }
        });
    }

    public void lambda$setNewPassword$45(TLRPC.TL_error tL_error, TLObject tLObject, boolean z) {
        if (tL_error == null) {
            TLRPC.account_Password account_password = (TLRPC.account_Password) tLObject;
            this.currentPassword = account_password;
            TwoStepVerificationActivity.initPasswordNewAlgo(account_password);
            setNewPassword(z);
            NotificationCenter.getInstance(this.currentAccount).lambda$postNotificationNameOnUIThread$1(NotificationCenter.didSetOrRemoveTwoStepPassword, this.currentPassword);
        }
    }

    public void lambda$setNewPassword$46(final boolean z, final TLObject tLObject, final TLRPC.TL_error tL_error) {
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                TwoStepVerificationSetupActivity.this.lambda$setNewPassword$45(tL_error, tLObject, z);
            }
        });
    }

    public void lambda$setNewPassword$47(byte[] bArr, DialogInterface dialogInterface, int i) {
        int size = this.fragmentsToClose.size();
        for (int i2 = 0; i2 < size; i2++) {
            ((BaseFragment) this.fragmentsToClose.get(i2)).removeSelfFromStack();
        }
        TwoStepVerificationActivity twoStepVerificationActivity = new TwoStepVerificationActivity();
        TLRPC.account_Password account_password = this.currentPassword;
        account_password.has_password = true;
        if (!account_password.has_recovery) {
            account_password.has_recovery = !TextUtils.isEmpty(account_password.email_unconfirmed_pattern);
        }
        TLRPC.account_Password account_password2 = this.currentPassword;
        if (bArr == null) {
            bArr = this.currentPasswordHash;
        }
        twoStepVerificationActivity.setCurrentPasswordParams(account_password2, bArr, this.currentSecretId, this.currentSecret);
        twoStepVerificationActivity.setBlockingAlert(this.otherwiseReloginDays);
        presentFragment(twoStepVerificationActivity, true);
        NotificationCenter.getInstance(this.currentAccount).lambda$postNotificationNameOnUIThread$1(NotificationCenter.didSetOrRemoveTwoStepPassword, this.currentPassword);
    }

    public void lambda$setNewPassword$48(TLRPC.TL_error tL_error, final boolean z, TLObject tLObject, final byte[] bArr, String str, TLRPC.TL_account_passwordInputSettings tL_account_passwordInputSettings) {
        String string;
        String str2;
        TLRPC.account_Password account_password;
        if (tL_error != null && "SRP_ID_INVALID".equals(tL_error.text)) {
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(new TLRPC.TL_account_getPassword(), new RequestDelegate() {
                @Override
                public final void run(TLObject tLObject2, TLRPC.TL_error tL_error2) {
                    TwoStepVerificationSetupActivity.this.lambda$setNewPassword$46(z, tLObject2, tL_error2);
                }
            }, 8);
            return;
        }
        needHideProgress();
        if (tL_error != null || (!(tLObject instanceof TLRPC.TL_boolTrue) && !(tLObject instanceof TLRPC.auth_Authorization))) {
            if (tL_error != null) {
                if (!"EMAIL_UNCONFIRMED".equals(tL_error.text) && !tL_error.text.startsWith("EMAIL_UNCONFIRMED_")) {
                    if ("EMAIL_INVALID".equals(tL_error.text)) {
                        showAlertWithText(LocaleController.getString(R.string.AppName), LocaleController.getString(R.string.PasswordEmailInvalid));
                        return;
                    }
                    if (tL_error.text.startsWith("FLOOD_WAIT")) {
                        int intValue = Utilities.parseInt((CharSequence) tL_error.text).intValue();
                        String formatPluralString = intValue < 60 ? LocaleController.formatPluralString("Seconds", intValue, new Object[0]) : LocaleController.formatPluralString("Minutes", intValue / 60, new Object[0]);
                        string = LocaleController.getString(R.string.AppName);
                        str2 = LocaleController.formatString("FloodWaitTime", R.string.FloodWaitTime, formatPluralString);
                    } else {
                        string = LocaleController.getString(R.string.AppName);
                        str2 = tL_error.text;
                    }
                    showAlertWithText(string, str2);
                    return;
                }
                NotificationCenter.getInstance(this.currentAccount).lambda$postNotificationNameOnUIThread$1(NotificationCenter.twoStepPasswordChanged, new Object[0]);
                int size = this.fragmentsToClose.size();
                for (int i = 0; i < size; i++) {
                    ((BaseFragment) this.fragmentsToClose.get(i)).removeSelfFromStack();
                }
                NotificationCenter notificationCenter = NotificationCenter.getInstance(this.currentAccount);
                int i2 = NotificationCenter.twoStepPasswordChanged;
                TLRPC.PasswordKdfAlgo passwordKdfAlgo = tL_account_passwordInputSettings.new_algo;
                TLRPC.account_Password account_password2 = this.currentPassword;
                TLRPC.SecurePasswordKdfAlgo securePasswordKdfAlgo = account_password2.new_secure_algo;
                byte[] bArr2 = account_password2.secure_random;
                String str3 = this.email;
                notificationCenter.lambda$postNotificationNameOnUIThread$1(i2, bArr, passwordKdfAlgo, securePasswordKdfAlgo, bArr2, str3, this.hint, str3, this.firstPassword);
                TLRPC.account_Password account_password3 = this.currentPassword;
                account_password3.email_unconfirmed_pattern = this.email;
                TwoStepVerificationSetupActivity twoStepVerificationSetupActivity = new TwoStepVerificationSetupActivity(5, account_password3);
                twoStepVerificationSetupActivity.fromRegistration = this.fromRegistration;
                twoStepVerificationSetupActivity.setCurrentPasswordParams(bArr != null ? bArr : this.currentPasswordHash, this.currentSecretId, this.currentSecret, this.emailOnly);
                twoStepVerificationSetupActivity.closeAfterSet = this.closeAfterSet;
                twoStepVerificationSetupActivity.setBlockingAlert(this.otherwiseReloginDays);
                presentFragment(twoStepVerificationSetupActivity, true);
                return;
            }
            return;
        }
        getMessagesController().removeSuggestion(0L, "VALIDATE_PASSWORD");
        if (z) {
            int size2 = this.fragmentsToClose.size();
            for (int i3 = 0; i3 < size2; i3++) {
                ((BaseFragment) this.fragmentsToClose.get(i3)).removeSelfFromStack();
            }
            NotificationCenter.getInstance(this.currentAccount).lambda$postNotificationNameOnUIThread$1(NotificationCenter.didRemoveTwoStepPassword, new Object[0]);
            NotificationCenter.getInstance(this.currentAccount).lambda$postNotificationNameOnUIThread$1(NotificationCenter.didSetOrRemoveTwoStepPassword, new Object[0]);
            lambda$onBackPressed$321();
            return;
        }
        if (getParentActivity() == null) {
            return;
        }
        if (this.currentPassword.has_password) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getParentActivity());
            builder.setPositiveButton(LocaleController.getString(R.string.OK), new DialogInterface.OnClickListener() {
                @Override
                public final void onClick(DialogInterface dialogInterface, int i4) {
                    TwoStepVerificationSetupActivity.this.lambda$setNewPassword$47(bArr, dialogInterface, i4);
                }
            });
            builder.setMessage(LocaleController.getString((str == null && (account_password = this.currentPassword) != null && account_password.has_password) ? R.string.YourEmailSuccessText : R.string.YourPasswordChangedSuccessText));
            builder.setTitle(LocaleController.getString(R.string.YourPasswordSuccess));
            Dialog showDialog = showDialog(builder.create());
            if (showDialog != null) {
                showDialog.setCanceledOnTouchOutside(false);
                showDialog.setCancelable(false);
                return;
            }
            return;
        }
        int size3 = this.fragmentsToClose.size();
        for (int i4 = 0; i4 < size3; i4++) {
            ((BaseFragment) this.fragmentsToClose.get(i4)).removeSelfFromStack();
        }
        TLRPC.account_Password account_password4 = this.currentPassword;
        account_password4.has_password = true;
        if (!account_password4.has_recovery) {
            account_password4.has_recovery = !TextUtils.isEmpty(account_password4.email_unconfirmed_pattern);
        }
        if (this.closeAfterSet) {
            NotificationCenter.getInstance(this.currentAccount).lambda$postNotificationNameOnUIThread$1(NotificationCenter.twoStepPasswordChanged, new Object[0]);
        }
        TwoStepVerificationSetupActivity twoStepVerificationSetupActivity2 = new TwoStepVerificationSetupActivity(7, this.currentPassword);
        twoStepVerificationSetupActivity2.fromRegistration = this.fromRegistration;
        twoStepVerificationSetupActivity2.setCurrentPasswordParams(bArr != null ? bArr : this.currentPasswordHash, this.currentSecretId, this.currentSecret, this.emailOnly);
        twoStepVerificationSetupActivity2.closeAfterSet = this.closeAfterSet;
        twoStepVerificationSetupActivity2.setBlockingAlert(this.otherwiseReloginDays);
        presentFragment(twoStepVerificationSetupActivity2, true);
        NotificationCenter.getInstance(this.currentAccount).lambda$postNotificationNameOnUIThread$1(NotificationCenter.didSetOrRemoveTwoStepPassword, this.currentPassword);
    }

    public void lambda$setNewPassword$49(final boolean z, final byte[] bArr, final String str, final TLRPC.TL_account_passwordInputSettings tL_account_passwordInputSettings, final TLObject tLObject, final TLRPC.TL_error tL_error) {
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                TwoStepVerificationSetupActivity.this.lambda$setNewPassword$48(tL_error, z, tLObject, bArr, str, tL_account_passwordInputSettings);
            }
        });
    }

    public void lambda$setNewPassword$50(TLObject tLObject, final boolean z, final String str, final TLRPC.TL_account_passwordInputSettings tL_account_passwordInputSettings) {
        final byte[] bArr;
        byte[] bArr2;
        byte[] bArr3;
        if (tLObject instanceof TLRPC.TL_account_updatePasswordSettings) {
            TLRPC.TL_account_updatePasswordSettings tL_account_updatePasswordSettings = (TLRPC.TL_account_updatePasswordSettings) tLObject;
            if (tL_account_updatePasswordSettings.password == null) {
                tL_account_updatePasswordSettings.password = getNewSrpPassword();
            }
        }
        if (z || str == null) {
            bArr = null;
            bArr2 = null;
        } else {
            byte[] stringBytes = AndroidUtilities.getStringBytes(str);
            TLRPC.PasswordKdfAlgo passwordKdfAlgo = this.currentPassword.new_algo;
            if (passwordKdfAlgo instanceof TLRPC.TL_passwordKdfAlgoSHA256SHA256PBKDF2HMACSHA512iter100000SHA256ModPow) {
                bArr2 = stringBytes;
                bArr = SRPHelper.getX(stringBytes, (TLRPC.TL_passwordKdfAlgoSHA256SHA256PBKDF2HMACSHA512iter100000SHA256ModPow) passwordKdfAlgo);
            } else {
                bArr2 = stringBytes;
                bArr = null;
            }
        }
        RequestDelegate requestDelegate = new RequestDelegate() {
            @Override
            public final void run(TLObject tLObject2, TLRPC.TL_error tL_error) {
                TwoStepVerificationSetupActivity.this.lambda$setNewPassword$49(z, bArr, str, tL_account_passwordInputSettings, tLObject2, tL_error);
            }
        };
        if (!z) {
            if (str != null && (bArr3 = this.currentSecret) != null && bArr3.length == 32) {
                TLRPC.SecurePasswordKdfAlgo securePasswordKdfAlgo = this.currentPassword.new_secure_algo;
                if (securePasswordKdfAlgo instanceof TLRPC.TL_securePasswordKdfAlgoPBKDF2HMACSHA512iter100000) {
                    TLRPC.TL_securePasswordKdfAlgoPBKDF2HMACSHA512iter100000 tL_securePasswordKdfAlgoPBKDF2HMACSHA512iter100000 = (TLRPC.TL_securePasswordKdfAlgoPBKDF2HMACSHA512iter100000) securePasswordKdfAlgo;
                    byte[] computePBKDF2 = Utilities.computePBKDF2(bArr2, tL_securePasswordKdfAlgoPBKDF2HMACSHA512iter100000.salt);
                    byte[] bArr4 = new byte[32];
                    System.arraycopy(computePBKDF2, 0, bArr4, 0, 32);
                    byte[] bArr5 = new byte[16];
                    System.arraycopy(computePBKDF2, 32, bArr5, 0, 16);
                    byte[] bArr6 = new byte[32];
                    System.arraycopy(this.currentSecret, 0, bArr6, 0, 32);
                    Utilities.aesCbcEncryptionByteArraySafe(bArr6, bArr4, bArr5, 0, 32, 0, 1);
                    TLRPC.TL_secureSecretSettings tL_secureSecretSettings = new TLRPC.TL_secureSecretSettings();
                    tL_account_passwordInputSettings.new_secure_settings = tL_secureSecretSettings;
                    tL_secureSecretSettings.secure_algo = tL_securePasswordKdfAlgoPBKDF2HMACSHA512iter100000;
                    tL_secureSecretSettings.secure_secret = bArr6;
                    tL_secureSecretSettings.secure_secret_id = this.currentSecretId;
                    tL_account_passwordInputSettings.flags |= 4;
                }
            }
            TLRPC.PasswordKdfAlgo passwordKdfAlgo2 = this.currentPassword.new_algo;
            if (!(passwordKdfAlgo2 instanceof TLRPC.TL_passwordKdfAlgoSHA256SHA256PBKDF2HMACSHA512iter100000SHA256ModPow)) {
                TLRPC.TL_error tL_error = new TLRPC.TL_error();
                tL_error.text = "PASSWORD_HASH_INVALID";
                requestDelegate.run(null, tL_error);
                return;
            } else if (str != null) {
                byte[] vBytes = SRPHelper.getVBytes(bArr2, (TLRPC.TL_passwordKdfAlgoSHA256SHA256PBKDF2HMACSHA512iter100000SHA256ModPow) passwordKdfAlgo2);
                tL_account_passwordInputSettings.new_password_hash = vBytes;
                if (vBytes == null) {
                    TLRPC.TL_error tL_error2 = new TLRPC.TL_error();
                    tL_error2.text = "ALGO_INVALID";
                    requestDelegate.run(null, tL_error2);
                }
            }
        }
        ConnectionsManager.getInstance(this.currentAccount).sendRequest(tLObject, requestDelegate, 10);
    }

    public void lambda$setRandomMonkeyIdleAnimation$38() {
        if (this.setAnimationRunnable == null) {
            return;
        }
        setRandomMonkeyIdleAnimation(false);
    }

    public void lambda$showSetForcePasswordAlert$51(DialogInterface dialogInterface, int i) {
        lambda$onBackPressed$321();
    }

    private void loadPasswordInfo() {
        ConnectionsManager.getInstance(this.currentAccount).sendRequest(new TLRPC.TL_account_getPassword(), new RequestDelegate() {
            @Override
            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                TwoStepVerificationSetupActivity.this.lambda$loadPasswordInfo$42(tLObject, tL_error);
            }
        }, 10);
    }

    private void needShowProgress() {
        if (getParentActivity() == null || getParentActivity().isFinishing()) {
            return;
        }
        AnimatorSet animatorSet = new AnimatorSet();
        if (this.floatingButtonContainer.getVisibility() == 0) {
            RadialProgressView radialProgressView = this.floatingProgressView;
            Property property = View.ALPHA;
            ObjectAnimator ofFloat = ObjectAnimator.ofFloat(radialProgressView, (Property<RadialProgressView, Float>) property, 1.0f);
            RadialProgressView radialProgressView2 = this.floatingProgressView;
            Property property2 = View.SCALE_X;
            ObjectAnimator ofFloat2 = ObjectAnimator.ofFloat(radialProgressView2, (Property<RadialProgressView, Float>) property2, 1.0f);
            RadialProgressView radialProgressView3 = this.floatingProgressView;
            Property property3 = View.SCALE_Y;
            animatorSet.playTogether(ofFloat, ofFloat2, ObjectAnimator.ofFloat(radialProgressView3, (Property<RadialProgressView, Float>) property3, 1.0f), ObjectAnimator.ofFloat(this.floatingButtonIcon, (Property<TransformableLoginButtonView, Float>) property, 0.0f), ObjectAnimator.ofFloat(this.floatingButtonIcon, (Property<TransformableLoginButtonView, Float>) property2, 0.1f), ObjectAnimator.ofFloat(this.floatingButtonIcon, (Property<TransformableLoginButtonView, Float>) property3, 0.1f));
        } else {
            animatorSet.playTogether(ObjectAnimator.ofFloat(this.radialProgressView, (Property<RadialProgressView, Float>) View.ALPHA, 1.0f), ObjectAnimator.ofFloat(this.radialProgressView, (Property<RadialProgressView, Float>) View.SCALE_X, 1.0f), ObjectAnimator.ofFloat(this.radialProgressView, (Property<RadialProgressView, Float>) View.SCALE_Y, 1.0f));
        }
        animatorSet.setInterpolator(CubicBezierInterpolator.DEFAULT);
        animatorSet.start();
    }

    private void onCodeFieldError(boolean z) {
        for (CodeNumberField codeNumberField : this.codeFieldContainer.codeField) {
            if (z) {
                codeNumberField.setText("");
            }
            codeNumberField.animateErrorProgress(1.0f);
        }
        if (z) {
            this.codeFieldContainer.codeField[0].requestFocus();
        }
        AndroidUtilities.shakeViewSpring(this.codeFieldContainer, 8.0f, new Runnable() {
            @Override
            public final void run() {
                TwoStepVerificationSetupActivity.this.lambda$onCodeFieldError$37();
            }
        });
    }

    private void onFieldError(View view, TextView textView, boolean z) {
        if (getParentActivity() == null) {
            return;
        }
        try {
            textView.performHapticFeedback(3, 2);
        } catch (Exception unused) {
        }
        if (z) {
            textView.setText("");
        }
        AndroidUtilities.shakeViewSpring(view, 5.0f);
    }

    private void onHintDone() {
        TLRPC.account_Password account_password = this.currentPassword;
        if (account_password.has_recovery) {
            this.email = "";
            setNewPassword(false);
            return;
        }
        TwoStepVerificationSetupActivity twoStepVerificationSetupActivity = new TwoStepVerificationSetupActivity(this.currentAccount, 3, account_password);
        twoStepVerificationSetupActivity.fromRegistration = this.fromRegistration;
        twoStepVerificationSetupActivity.setCurrentPasswordParams(this.currentPasswordHash, this.currentSecretId, this.currentSecret, this.emailOnly);
        twoStepVerificationSetupActivity.firstPassword = this.firstPassword;
        twoStepVerificationSetupActivity.hint = this.hint;
        twoStepVerificationSetupActivity.fragmentsToClose.addAll(this.fragmentsToClose);
        twoStepVerificationSetupActivity.fragmentsToClose.add(this);
        twoStepVerificationSetupActivity.closeAfterSet = this.closeAfterSet;
        twoStepVerificationSetupActivity.setBlockingAlert(this.otherwiseReloginDays);
        presentFragment(twoStepVerificationSetupActivity);
    }

    public void processNext() {
        BaseFragment baseFragment;
        if (getParentActivity() == null) {
            return;
        }
        switch (this.currentType) {
            case 0:
            case 1:
                if (this.editTextFirstRow.length() == 0) {
                    onFieldError(this.outlineTextFirstRow, this.editTextFirstRow, false);
                    return;
                }
                if (!this.editTextFirstRow.getText().toString().equals(this.firstPassword) && this.currentType == 1) {
                    AndroidUtilities.shakeViewSpring(this.outlineTextFirstRow, 5.0f);
                    try {
                        this.outlineTextFirstRow.performHapticFeedback(3, 2);
                    } catch (Exception unused) {
                    }
                    try {
                        Toast.makeText(getParentActivity(), LocaleController.getString(R.string.PasswordDoNotMatch), 0).show();
                        return;
                    } catch (Exception e) {
                        FileLog.e(e);
                        return;
                    }
                }
                TwoStepVerificationSetupActivity twoStepVerificationSetupActivity = new TwoStepVerificationSetupActivity(this.currentAccount, this.currentType != 0 ? 2 : 1, this.currentPassword);
                twoStepVerificationSetupActivity.fromRegistration = this.fromRegistration;
                twoStepVerificationSetupActivity.firstPassword = this.editTextFirstRow.getText().toString();
                twoStepVerificationSetupActivity.setCurrentPasswordParams(this.currentPasswordHash, this.currentSecretId, this.currentSecret, this.emailOnly);
                twoStepVerificationSetupActivity.setCurrentEmailCode(this.emailCode);
                twoStepVerificationSetupActivity.fragmentsToClose.addAll(this.fragmentsToClose);
                twoStepVerificationSetupActivity.fragmentsToClose.add(this);
                twoStepVerificationSetupActivity.closeAfterSet = this.closeAfterSet;
                twoStepVerificationSetupActivity.setBlockingAlert(this.otherwiseReloginDays);
                presentFragment(twoStepVerificationSetupActivity);
                return;
            case 2:
                String obj = this.editTextFirstRow.getText().toString();
                this.hint = obj;
                if (!obj.equalsIgnoreCase(this.firstPassword)) {
                    onHintDone();
                    return;
                }
                try {
                    Toast.makeText(getParentActivity(), LocaleController.getString(R.string.PasswordAsHintError), 0).show();
                } catch (Exception e2) {
                    FileLog.e(e2);
                }
                onFieldError(this.outlineTextFirstRow, this.editTextFirstRow, false);
                return;
            case 3:
                if (!this.emailOnly && this.bottomSkipButton.getAlpha() < 1.0f) {
                    this.bottomSkipButton.animate().cancel();
                    this.bottomSkipButton.animate().alpha(1.0f).start();
                }
                String obj2 = this.editTextFirstRow.getText().toString();
                this.email = obj2;
                if (isValidEmail(obj2)) {
                    setNewPassword(false);
                    return;
                } else {
                    onFieldError(this.outlineTextFirstRow, this.editTextFirstRow, false);
                    return;
                }
            case 4:
                final String code = this.codeFieldContainer.getCode();
                TLRPC.TL_auth_checkRecoveryPassword tL_auth_checkRecoveryPassword = new TLRPC.TL_auth_checkRecoveryPassword();
                tL_auth_checkRecoveryPassword.code = code;
                ConnectionsManager.getInstance(this.currentAccount).sendRequest(tL_auth_checkRecoveryPassword, new RequestDelegate() {
                    @Override
                    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                        TwoStepVerificationSetupActivity.this.lambda$processNext$31(code, tLObject, tL_error);
                    }
                }, 10);
                return;
            case 5:
                TLRPC.TL_account_confirmPasswordEmail tL_account_confirmPasswordEmail = new TLRPC.TL_account_confirmPasswordEmail();
                tL_account_confirmPasswordEmail.code = this.codeFieldContainer.getCode();
                ConnectionsManager.getInstance(this.currentAccount).sendRequest(tL_account_confirmPasswordEmail, new RequestDelegate() {
                    @Override
                    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                        TwoStepVerificationSetupActivity.this.lambda$processNext$35(tLObject, tL_error);
                    }
                }, 10);
                needShowProgress();
                return;
            case 6:
                TLRPC.account_Password account_password = this.currentPassword;
                if (account_password == null) {
                    needShowProgress();
                    this.doneAfterPasswordLoad = true;
                    return;
                }
                TwoStepVerificationSetupActivity twoStepVerificationSetupActivity2 = new TwoStepVerificationSetupActivity(this.currentAccount, 0, account_password);
                twoStepVerificationSetupActivity2.fromRegistration = this.fromRegistration;
                twoStepVerificationSetupActivity2.closeAfterSet = this.closeAfterSet;
                twoStepVerificationSetupActivity2.setBlockingAlert(this.otherwiseReloginDays);
                baseFragment = twoStepVerificationSetupActivity2;
                presentFragment(baseFragment, true);
                return;
            case 7:
                if (!this.closeAfterSet) {
                    if (this.fromRegistration) {
                        Bundle bundle = new Bundle();
                        bundle.putBoolean("afterSignup", true);
                        baseFragment = new DialogsActivity(bundle);
                        presentFragment(baseFragment, true);
                        return;
                    }
                    TwoStepVerificationActivity twoStepVerificationActivity = new TwoStepVerificationActivity();
                    twoStepVerificationActivity.setCurrentPasswordParams(this.currentPassword, this.currentPasswordHash, this.currentSecretId, this.currentSecret);
                    twoStepVerificationActivity.setBlockingAlert(this.otherwiseReloginDays);
                    presentFragment(twoStepVerificationActivity, true);
                    return;
                }
                lambda$onBackPressed$321();
                return;
            case 8:
                if (this.currentPassword == null) {
                    needShowProgress();
                    this.doneAfterPasswordLoad = true;
                    return;
                }
                String obj3 = this.editTextFirstRow.getText().toString();
                if (obj3.length() == 0) {
                    onFieldError(this.outlineTextFirstRow, this.editTextFirstRow, false);
                    return;
                }
                final byte[] stringBytes = AndroidUtilities.getStringBytes(obj3);
                needShowProgress();
                Utilities.globalQueue.postRunnable(new Runnable() {
                    @Override
                    public final void run() {
                        TwoStepVerificationSetupActivity.this.lambda$processNext$28(stringBytes);
                    }
                });
                return;
            case 9:
                lambda$onBackPressed$321();
                return;
            default:
                return;
        }
    }

    public void setNewPassword(final boolean z) {
        TLRPC.account_Password account_password;
        TLRPC.TL_account_updatePasswordSettings tL_account_updatePasswordSettings;
        if (z && this.waitingForEmail && this.currentPassword.has_password) {
            needShowProgress();
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(new TLRPC.TL_account_cancelPasswordEmail(), new RequestDelegate() {
                @Override
                public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                    TwoStepVerificationSetupActivity.this.lambda$setNewPassword$44(tLObject, tL_error);
                }
            });
            return;
        }
        final String str = this.firstPassword;
        final TLRPC.TL_account_passwordInputSettings tL_account_passwordInputSettings = new TLRPC.TL_account_passwordInputSettings();
        if (z) {
            UserConfig.getInstance(this.currentAccount).resetSavedPassword();
            this.currentSecret = null;
            if (this.waitingForEmail) {
                tL_account_passwordInputSettings.flags = 2;
            } else {
                tL_account_passwordInputSettings.flags = 3;
                tL_account_passwordInputSettings.hint = "";
                tL_account_passwordInputSettings.new_password_hash = new byte[0];
                tL_account_passwordInputSettings.new_algo = new TLRPC.TL_passwordKdfAlgoUnknown();
            }
            tL_account_passwordInputSettings.email = "";
        } else {
            if (this.hint == null && (account_password = this.currentPassword) != null) {
                this.hint = account_password.hint;
            }
            if (this.hint == null) {
                this.hint = "";
            }
            if (str != null) {
                tL_account_passwordInputSettings.flags |= 1;
                tL_account_passwordInputSettings.hint = this.hint;
                tL_account_passwordInputSettings.new_algo = this.currentPassword.new_algo;
            }
            if (this.email.length() > 0) {
                tL_account_passwordInputSettings.flags = 2 | tL_account_passwordInputSettings.flags;
                tL_account_passwordInputSettings.email = this.email.trim();
            }
        }
        if (this.emailCode != null) {
            TLRPC.TL_auth_recoverPassword tL_auth_recoverPassword = new TLRPC.TL_auth_recoverPassword();
            tL_auth_recoverPassword.code = this.emailCode;
            tL_auth_recoverPassword.new_settings = tL_account_passwordInputSettings;
            tL_auth_recoverPassword.flags |= 1;
            tL_account_updatePasswordSettings = tL_auth_recoverPassword;
        } else {
            TLRPC.TL_account_updatePasswordSettings tL_account_updatePasswordSettings2 = new TLRPC.TL_account_updatePasswordSettings();
            byte[] bArr = this.currentPasswordHash;
            if (bArr == null || bArr.length == 0 || (z && this.waitingForEmail)) {
                tL_account_updatePasswordSettings2.password = new TLRPC.TL_inputCheckPasswordEmpty();
            }
            tL_account_updatePasswordSettings2.new_settings = tL_account_passwordInputSettings;
            tL_account_updatePasswordSettings = tL_account_updatePasswordSettings2;
        }
        final TLRPC.TL_account_updatePasswordSettings tL_account_updatePasswordSettings3 = tL_account_updatePasswordSettings;
        needShowProgress();
        Utilities.globalQueue.postRunnable(new Runnable() {
            @Override
            public final void run() {
                TwoStepVerificationSetupActivity.this.lambda$setNewPassword$50(tL_account_updatePasswordSettings3, z, str, tL_account_passwordInputSettings);
            }
        });
    }

    private void setRandomMonkeyIdleAnimation(boolean r6) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.TwoStepVerificationSetupActivity.setRandomMonkeyIdleAnimation(boolean):void");
    }

    private void showAlertWithText(String str, String str2) {
        if (getParentActivity() == null) {
            return;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(getParentActivity());
        builder.setPositiveButton(LocaleController.getString(R.string.OK), null);
        builder.setTitle(str);
        builder.setMessage(str2);
        showDialog(builder.create());
    }

    public void showDoneButton(final boolean z) {
        if (z == (this.buttonTextView.getTag() != null)) {
            return;
        }
        AnimatorSet animatorSet = this.buttonAnimation;
        if (animatorSet != null) {
            animatorSet.cancel();
        }
        this.buttonTextView.setTag(z ? 1 : null);
        this.buttonAnimation = new AnimatorSet();
        if (z) {
            this.buttonTextView.setVisibility(0);
            AnimatorSet animatorSet2 = this.buttonAnimation;
            TextView textView = this.descriptionText2;
            Property property = View.SCALE_X;
            ObjectAnimator ofFloat = ObjectAnimator.ofFloat(textView, (Property<TextView, Float>) property, 0.9f);
            TextView textView2 = this.descriptionText2;
            Property property2 = View.SCALE_Y;
            ObjectAnimator ofFloat2 = ObjectAnimator.ofFloat(textView2, (Property<TextView, Float>) property2, 0.9f);
            TextView textView3 = this.descriptionText2;
            Property property3 = View.ALPHA;
            animatorSet2.playTogether(ofFloat, ofFloat2, ObjectAnimator.ofFloat(textView3, (Property<TextView, Float>) property3, 0.0f), ObjectAnimator.ofFloat(this.buttonTextView, (Property<TextView, Float>) property, 1.0f), ObjectAnimator.ofFloat(this.buttonTextView, (Property<TextView, Float>) property2, 1.0f), ObjectAnimator.ofFloat(this.buttonTextView, (Property<TextView, Float>) property3, 1.0f));
        } else {
            this.descriptionText2.setVisibility(0);
            AnimatorSet animatorSet3 = this.buttonAnimation;
            TextView textView4 = this.buttonTextView;
            Property property4 = View.SCALE_X;
            ObjectAnimator ofFloat3 = ObjectAnimator.ofFloat(textView4, (Property<TextView, Float>) property4, 0.9f);
            TextView textView5 = this.buttonTextView;
            Property property5 = View.SCALE_Y;
            ObjectAnimator ofFloat4 = ObjectAnimator.ofFloat(textView5, (Property<TextView, Float>) property5, 0.9f);
            TextView textView6 = this.buttonTextView;
            Property property6 = View.ALPHA;
            animatorSet3.playTogether(ofFloat3, ofFloat4, ObjectAnimator.ofFloat(textView6, (Property<TextView, Float>) property6, 0.0f), ObjectAnimator.ofFloat(this.descriptionText2, (Property<TextView, Float>) property4, 1.0f), ObjectAnimator.ofFloat(this.descriptionText2, (Property<TextView, Float>) property5, 1.0f), ObjectAnimator.ofFloat(this.descriptionText2, (Property<TextView, Float>) property6, 1.0f));
        }
        this.buttonAnimation.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationCancel(Animator animator) {
                if (TwoStepVerificationSetupActivity.this.buttonAnimation == null || !TwoStepVerificationSetupActivity.this.buttonAnimation.equals(animator)) {
                    return;
                }
                TwoStepVerificationSetupActivity.this.buttonAnimation = null;
            }

            @Override
            public void onAnimationEnd(Animator animator) {
                if (TwoStepVerificationSetupActivity.this.buttonAnimation == null || !TwoStepVerificationSetupActivity.this.buttonAnimation.equals(animator)) {
                    return;
                }
                (z ? TwoStepVerificationSetupActivity.this.descriptionText2 : TwoStepVerificationSetupActivity.this.buttonTextView).setVisibility(4);
            }
        });
        this.buttonAnimation.setDuration(150L);
        this.buttonAnimation.start();
    }

    public void showSetForcePasswordAlert() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getParentActivity());
        builder.setTitle(LocaleController.getString(R.string.Warning));
        builder.setMessage(LocaleController.formatPluralString("ForceSetPasswordAlertMessageShort", this.otherwiseReloginDays, new Object[0]));
        builder.setPositiveButton(LocaleController.getString(R.string.TwoStepVerificationSetPassword), null);
        builder.setNegativeButton(LocaleController.getString(R.string.ForceSetPasswordCancel), new DialogInterface.OnClickListener() {
            @Override
            public final void onClick(DialogInterface dialogInterface, int i) {
                TwoStepVerificationSetupActivity.this.lambda$showSetForcePasswordAlert$51(dialogInterface, i);
            }
        });
        ((TextView) builder.show().getButton(-2)).setTextColor(Theme.getColor(Theme.key_text_RedBold));
    }

    private void switchMonkeyAnimation(boolean z) {
        if (!z) {
            this.editTextFirstRow.dispatchTextWatchersTextChanged();
            setRandomMonkeyIdleAnimation(true);
            return;
        }
        Runnable runnable = this.setAnimationRunnable;
        if (runnable != null) {
            AndroidUtilities.cancelRunOnUIThread(runnable);
        }
        this.imageView.setAnimation(this.animationDrawables[6]);
        this.imageView.playAnimation();
    }

    public void addFragmentToClose(BaseFragment baseFragment) {
        this.fragmentsToClose.add(baseFragment);
    }

    @Override
    public View createView(Context context) {
        RLottieImageView rLottieImageView;
        int i;
        TextView textView;
        int i2;
        this.actionBar.setBackgroundDrawable(null);
        this.actionBar.setBackButtonImage(R.drawable.ic_ab_back);
        this.actionBar.setAllowOverlayTitle(false);
        ActionBar actionBar = this.actionBar;
        int i3 = Theme.key_windowBackgroundWhiteBlackText;
        actionBar.setTitleColor(Theme.getColor(i3));
        this.actionBar.setItemsColor(Theme.getColor(i3), false);
        this.actionBar.setItemsBackgroundColor(Theme.getColor(Theme.key_actionBarWhiteSelector), false);
        this.actionBar.setCastShadows(false);
        this.actionBar.setAddToContainer(false);
        this.actionBar.setActionBarMenuOnItemClick(new AnonymousClass1());
        if (this.currentType == 5) {
            this.actionBar.createMenu().addItem(0, R.drawable.ic_ab_other).addSubItem(1, LocaleController.getString(R.string.AbortPasswordMenu));
        }
        this.floatingButtonContainer = new FrameLayout(context);
        int i4 = Build.VERSION.SDK_INT;
        if (i4 >= 21) {
            StateListAnimator stateListAnimator = new StateListAnimator();
            stateListAnimator.addState(new int[]{16842919}, ObjectAnimator.ofFloat(this.floatingButtonIcon, "translationZ", AndroidUtilities.dp(2.0f), AndroidUtilities.dp(4.0f)).setDuration(200L));
            stateListAnimator.addState(new int[0], ObjectAnimator.ofFloat(this.floatingButtonIcon, "translationZ", AndroidUtilities.dp(4.0f), AndroidUtilities.dp(2.0f)).setDuration(200L));
            this.floatingButtonContainer.setStateListAnimator(stateListAnimator);
            this.floatingButtonContainer.setOutlineProvider(new ViewOutlineProvider() {
                @Override
                public void getOutline(View view, Outline outline) {
                    outline.setOval(0, 0, AndroidUtilities.dp(56.0f), AndroidUtilities.dp(56.0f));
                }
            });
        }
        this.floatingAutoAnimator = VerticalPositionAutoAnimator.attach(this.floatingButtonContainer);
        this.floatingButtonContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public final void onClick(View view) {
                TwoStepVerificationSetupActivity.this.lambda$createView$2(view);
            }
        });
        TransformableLoginButtonView transformableLoginButtonView = new TransformableLoginButtonView(context);
        this.floatingButtonIcon = transformableLoginButtonView;
        transformableLoginButtonView.setTransformType(1);
        this.floatingButtonIcon.setProgress(0.0f);
        this.floatingButtonIcon.setColor(Theme.getColor(Theme.key_chats_actionIcon));
        this.floatingButtonIcon.setDrawBackground(false);
        this.floatingButtonContainer.setContentDescription(LocaleController.getString(R.string.Next));
        this.floatingButtonContainer.addView(this.floatingButtonIcon, LayoutHelper.createFrame(i4 >= 21 ? 56 : 60, i4 >= 21 ? 56.0f : 60.0f));
        RadialProgressView radialProgressView = new RadialProgressView(context);
        this.floatingProgressView = radialProgressView;
        radialProgressView.setSize(AndroidUtilities.dp(22.0f));
        this.floatingProgressView.setAlpha(0.0f);
        this.floatingProgressView.setScaleX(0.1f);
        this.floatingProgressView.setScaleY(0.1f);
        this.floatingButtonContainer.addView(this.floatingProgressView, LayoutHelper.createFrame(-1, -1.0f));
        Drawable createSimpleSelectorCircleDrawable = Theme.createSimpleSelectorCircleDrawable(AndroidUtilities.dp(56.0f), Theme.getColor(Theme.key_chats_actionBackground), Theme.getColor(Theme.key_chats_actionPressedBackground));
        if (i4 < 21) {
            Drawable mutate = ContextCompat.getDrawable(context, R.drawable.floating_shadow).mutate();
            mutate.setColorFilter(new PorterDuffColorFilter(-16777216, PorterDuff.Mode.MULTIPLY));
            CombinedDrawable combinedDrawable = new CombinedDrawable(mutate, createSimpleSelectorCircleDrawable, 0, 0);
            combinedDrawable.setIconSize(AndroidUtilities.dp(56.0f), AndroidUtilities.dp(56.0f));
            createSimpleSelectorCircleDrawable = combinedDrawable;
        }
        this.floatingButtonContainer.setBackground(createSimpleSelectorCircleDrawable);
        TextView textView2 = new TextView(context);
        this.bottomSkipButton = textView2;
        textView2.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlueText2));
        this.bottomSkipButton.setTextSize(1, 14.0f);
        this.bottomSkipButton.setGravity(19);
        this.bottomSkipButton.setVisibility(8);
        VerticalPositionAutoAnimator.attach(this.bottomSkipButton);
        this.bottomSkipButton.setPadding(AndroidUtilities.dp(32.0f), 0, AndroidUtilities.dp(32.0f), 0);
        this.bottomSkipButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public final void onClick(View view) {
                TwoStepVerificationSetupActivity.this.lambda$createView$7(view);
            }
        });
        RLottieImageView rLottieImageView2 = new RLottieImageView(context);
        this.imageView = rLottieImageView2;
        ImageView.ScaleType scaleType = ImageView.ScaleType.CENTER;
        rLottieImageView2.setScaleType(scaleType);
        if (this.currentType == 2 && AndroidUtilities.isSmallScreen()) {
            this.imageView.setVisibility(8);
        } else if (!isIntro()) {
            this.imageView.setVisibility(isLandscape() ? 8 : 0);
        }
        TextView textView3 = new TextView(context);
        this.titleTextView = textView3;
        textView3.setTextColor(Theme.getColor(i3));
        this.titleTextView.setGravity(1);
        this.titleTextView.setPadding(AndroidUtilities.dp(32.0f), 0, AndroidUtilities.dp(32.0f), 0);
        this.titleTextView.setTextSize(1, 24.0f);
        SpoilersTextView spoilersTextView = new SpoilersTextView(context);
        this.descriptionText = spoilersTextView;
        int i5 = Theme.key_windowBackgroundWhiteGrayText6;
        spoilersTextView.setTextColor(Theme.getColor(i5));
        this.descriptionText.setGravity(1);
        this.descriptionText.setLineSpacing(AndroidUtilities.dp(2.0f), 1.0f);
        this.descriptionText.setTextSize(1, 15.0f);
        this.descriptionText.setVisibility(8);
        this.descriptionText.setPadding(AndroidUtilities.dp(32.0f), 0, AndroidUtilities.dp(32.0f), 0);
        TextView textView4 = new TextView(context);
        this.descriptionText2 = textView4;
        textView4.setTextColor(Theme.getColor(i5));
        this.descriptionText2.setGravity(1);
        this.descriptionText2.setTextSize(1, 14.0f);
        this.descriptionText2.setLineSpacing(AndroidUtilities.dp(2.0f), 1.0f);
        this.descriptionText2.setPadding(AndroidUtilities.dp(32.0f), 0, AndroidUtilities.dp(32.0f), 0);
        this.descriptionText2.setVisibility(8);
        this.descriptionText2.setOnClickListener(new View.OnClickListener() {
            @Override
            public final void onClick(View view) {
                TwoStepVerificationSetupActivity.this.lambda$createView$8(view);
            }
        });
        TextView textView5 = new TextView(context);
        this.buttonTextView = textView5;
        textView5.setMinWidth(AndroidUtilities.dp(220.0f));
        this.buttonTextView.setPadding(AndroidUtilities.dp(34.0f), 0, AndroidUtilities.dp(34.0f), 0);
        this.buttonTextView.setGravity(17);
        this.buttonTextView.setTextColor(Theme.getColor(Theme.key_featuredStickers_buttonText));
        this.buttonTextView.setTextSize(1, 15.0f);
        this.buttonTextView.setTypeface(AndroidUtilities.bold());
        this.buttonTextView.setBackground(Theme.AdaptiveRipple.filledRectByKey(Theme.key_featuredStickers_addButton, 6.0f));
        this.buttonTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public final void onClick(View view) {
                TwoStepVerificationSetupActivity.this.lambda$createView$9(view);
            }
        });
        int i6 = this.currentType;
        if (i6 == 6 || i6 == 7 || i6 == 9) {
            this.titleTextView.setTypeface(Typeface.DEFAULT);
            this.titleTextView.setTextSize(1, 24.0f);
        } else {
            this.titleTextView.setTypeface(AndroidUtilities.bold());
            this.titleTextView.setTextSize(1, 18.0f);
        }
        switch (this.currentType) {
            case 0:
            case 1:
            case 2:
            case 3:
            case 4:
            case 5:
            case 8:
                final FrameLayout frameLayout = new FrameLayout(context) {
                    @Override
                    protected void onMeasure(int i7, int i8) {
                        super.onMeasure(i7, i8);
                        ((ViewGroup.MarginLayoutParams) TwoStepVerificationSetupActivity.this.radialProgressView.getLayoutParams()).topMargin = AndroidUtilities.statusBarHeight + AndroidUtilities.dp(16.0f);
                    }
                };
                final SizeNotifierFrameLayout sizeNotifierFrameLayout = new SizeNotifierFrameLayout(context) {
                    @Override
                    public void onLayout(boolean z, int i7, int i8, int i9, int i10) {
                        FrameLayout frameLayout2;
                        int measuredWidth;
                        int measuredHeight;
                        if (TwoStepVerificationSetupActivity.this.keyboardView.getVisibility() == 8 || measureKeyboardHeight() < AndroidUtilities.dp(20.0f)) {
                            if (TwoStepVerificationSetupActivity.this.keyboardView.getVisibility() != 8) {
                                frameLayout2 = frameLayout;
                                measuredWidth = getMeasuredWidth();
                                measuredHeight = getMeasuredHeight() - AndroidUtilities.dp(230.0f);
                            }
                            frameLayout2 = frameLayout;
                            measuredWidth = getMeasuredWidth();
                            measuredHeight = getMeasuredHeight();
                        } else {
                            if (TwoStepVerificationSetupActivity.this.isCustomKeyboardVisible()) {
                                frameLayout2 = frameLayout;
                                measuredWidth = getMeasuredWidth();
                                measuredHeight = (getMeasuredHeight() - AndroidUtilities.dp(230.0f)) + measureKeyboardHeight();
                            }
                            frameLayout2 = frameLayout;
                            measuredWidth = getMeasuredWidth();
                            measuredHeight = getMeasuredHeight();
                        }
                        frameLayout2.layout(0, 0, measuredWidth, measuredHeight);
                        TwoStepVerificationSetupActivity.this.keyboardView.layout(0, measuredHeight, getMeasuredWidth(), AndroidUtilities.dp(230.0f) + measuredHeight);
                    }

                    @Override
                    protected void onMeasure(int i7, int i8) {
                        int size = View.MeasureSpec.getSize(i7);
                        int size2 = View.MeasureSpec.getSize(i8);
                        setMeasuredDimension(size, size2);
                        if (TwoStepVerificationSetupActivity.this.keyboardView.getVisibility() != 8 && measureKeyboardHeight() < AndroidUtilities.dp(20.0f)) {
                            size2 -= AndroidUtilities.dp(230.0f);
                        }
                        frameLayout.measure(View.MeasureSpec.makeMeasureSpec(size, 1073741824), View.MeasureSpec.makeMeasureSpec(size2, 1073741824));
                        TwoStepVerificationSetupActivity.this.keyboardView.measure(View.MeasureSpec.makeMeasureSpec(size, 1073741824), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(230.0f), 1073741824));
                    }
                };
                sizeNotifierFrameLayout.addView(frameLayout);
                ViewGroup viewGroup = new ViewGroup(context) {
                    @Override
                    protected void onLayout(boolean z, int i7, int i8, int i9, int i10) {
                        ((BaseFragment) TwoStepVerificationSetupActivity.this).actionBar.layout(0, 0, ((BaseFragment) TwoStepVerificationSetupActivity.this).actionBar.getMeasuredWidth(), ((BaseFragment) TwoStepVerificationSetupActivity.this).actionBar.getMeasuredHeight());
                        TwoStepVerificationSetupActivity.this.actionBarBackground.layout(0, 0, TwoStepVerificationSetupActivity.this.actionBarBackground.getMeasuredWidth(), TwoStepVerificationSetupActivity.this.actionBarBackground.getMeasuredHeight());
                        SizeNotifierFrameLayout sizeNotifierFrameLayout2 = sizeNotifierFrameLayout;
                        sizeNotifierFrameLayout2.layout(0, 0, sizeNotifierFrameLayout2.getMeasuredWidth(), sizeNotifierFrameLayout.getMeasuredHeight());
                    }

                    @Override
                    protected void onMeasure(int i7, int i8) {
                        int size = View.MeasureSpec.getSize(i7);
                        int size2 = View.MeasureSpec.getSize(i8);
                        ((BaseFragment) TwoStepVerificationSetupActivity.this).actionBar.measure(View.MeasureSpec.makeMeasureSpec(size, 1073741824), i8);
                        TwoStepVerificationSetupActivity.this.actionBarBackground.measure(View.MeasureSpec.makeMeasureSpec(size, 1073741824), View.MeasureSpec.makeMeasureSpec(((BaseFragment) TwoStepVerificationSetupActivity.this).actionBar.getMeasuredHeight() + AndroidUtilities.dp(3.0f), 1073741824));
                        sizeNotifierFrameLayout.measure(View.MeasureSpec.makeMeasureSpec(size, 1073741824), i8);
                        setMeasuredDimension(size, size2);
                    }
                };
                ScrollView scrollView = new ScrollView(context) {
                    private int scrollingUp;
                    private int[] location = new int[2];
                    private Rect tempRect = new Rect();
                    private boolean isLayoutDirty = true;

                    @Override
                    protected void onLayout(boolean z, int i7, int i8, int i9, int i10) {
                        this.isLayoutDirty = false;
                        super.onLayout(z, i7, i8, i9, i10);
                    }

                    @Override
                    protected void onScrollChanged(int i7, int i8, int i9, int i10) {
                        super.onScrollChanged(i7, i8, i9, i10);
                        if (TwoStepVerificationSetupActivity.this.titleTextView == null) {
                            return;
                        }
                        TwoStepVerificationSetupActivity.this.titleTextView.getLocationOnScreen(this.location);
                        boolean z = this.location[1] + TwoStepVerificationSetupActivity.this.titleTextView.getMeasuredHeight() < ((BaseFragment) TwoStepVerificationSetupActivity.this).actionBar.getBottom();
                        if (z != (TwoStepVerificationSetupActivity.this.titleTextView.getTag() == null)) {
                            TwoStepVerificationSetupActivity.this.titleTextView.setTag(z ? null : 1);
                            if (TwoStepVerificationSetupActivity.this.actionBarAnimator != null) {
                                TwoStepVerificationSetupActivity.this.actionBarAnimator.cancel();
                                TwoStepVerificationSetupActivity.this.actionBarAnimator = null;
                            }
                            TwoStepVerificationSetupActivity.this.actionBarAnimator = new AnimatorSet();
                            AnimatorSet animatorSet = TwoStepVerificationSetupActivity.this.actionBarAnimator;
                            View view = TwoStepVerificationSetupActivity.this.actionBarBackground;
                            Property property = View.ALPHA;
                            animatorSet.playTogether(ObjectAnimator.ofFloat(view, (Property<View, Float>) property, z ? 1.0f : 0.0f), ObjectAnimator.ofFloat(((BaseFragment) TwoStepVerificationSetupActivity.this).actionBar.getTitleTextView(), (Property<SimpleTextView, Float>) property, z ? 1.0f : 0.0f));
                            TwoStepVerificationSetupActivity.this.actionBarAnimator.setDuration(150L);
                            TwoStepVerificationSetupActivity.this.actionBarAnimator.addListener(new AnimatorListenerAdapter() {
                                @Override
                                public void onAnimationEnd(Animator animator) {
                                    if (animator.equals(TwoStepVerificationSetupActivity.this.actionBarAnimator)) {
                                        TwoStepVerificationSetupActivity.this.actionBarAnimator = null;
                                    }
                                }
                            });
                            TwoStepVerificationSetupActivity.this.actionBarAnimator.start();
                        }
                    }

                    @Override
                    public void requestChildFocus(View view, View view2) {
                        if (Build.VERSION.SDK_INT < 29 && view2 != null && !this.isLayoutDirty) {
                            scrollToDescendant(view2);
                        }
                        super.requestChildFocus(view, view2);
                    }

                    @Override
                    public boolean requestChildRectangleOnScreen(View view, Rect rect, boolean z) {
                        if (Build.VERSION.SDK_INT < 23) {
                            int dp = rect.bottom + AndroidUtilities.dp(120.0f);
                            rect.bottom = dp;
                            int i7 = this.scrollingUp;
                            if (i7 != 0) {
                                rect.top -= i7;
                                rect.bottom = dp - i7;
                                this.scrollingUp = 0;
                            }
                        }
                        return super.requestChildRectangleOnScreen(view, rect, z);
                    }

                    @Override
                    public void requestLayout() {
                        this.isLayoutDirty = true;
                        super.requestLayout();
                    }

                    @Override
                    public void scrollToDescendant(View view) {
                        view.getDrawingRect(this.tempRect);
                        offsetDescendantRectToMyCoords(view, this.tempRect);
                        this.tempRect.bottom += AndroidUtilities.dp(120.0f);
                        int computeScrollDeltaToGetChildRectOnScreen = computeScrollDeltaToGetChildRectOnScreen(this.tempRect);
                        if (computeScrollDeltaToGetChildRectOnScreen < 0) {
                            int measuredHeight = (getMeasuredHeight() - view.getMeasuredHeight()) / 2;
                            this.scrollingUp = measuredHeight;
                            computeScrollDeltaToGetChildRectOnScreen -= measuredHeight;
                        } else {
                            this.scrollingUp = 0;
                        }
                        if (computeScrollDeltaToGetChildRectOnScreen != 0) {
                            smoothScrollBy(0, computeScrollDeltaToGetChildRectOnScreen);
                        }
                    }
                };
                this.scrollView = scrollView;
                scrollView.setVerticalScrollBarEnabled(false);
                frameLayout.addView(this.scrollView, LayoutHelper.createFrame(-1, -1.0f));
                frameLayout.addView(this.bottomSkipButton, LayoutHelper.createFrame(-1, i4 >= 21 ? 56.0f : 60.0f, 80, 0.0f, 0.0f, 0.0f, 16.0f));
                frameLayout.addView(this.floatingButtonContainer, LayoutHelper.createFrame(i4 >= 21 ? 56 : 60, i4 >= 21 ? 56.0f : 60.0f, 85, 0.0f, 0.0f, 24.0f, 16.0f));
                viewGroup.addView(sizeNotifierFrameLayout, LayoutHelper.createFrame(-1, -1.0f));
                LinearLayout linearLayout = new LinearLayout(context) {
                    @Override
                    protected void onMeasure(int i7, int i8) {
                        super.onMeasure(i7, i8);
                        ViewGroup.MarginLayoutParams marginLayoutParams = (ViewGroup.MarginLayoutParams) TwoStepVerificationSetupActivity.this.titleTextView.getLayoutParams();
                        int i9 = 0;
                        int dp = ((TwoStepVerificationSetupActivity.this.imageView.getVisibility() != 8 || Build.VERSION.SDK_INT < 21) ? 0 : AndroidUtilities.statusBarHeight) + AndroidUtilities.dp(8.0f);
                        if (TwoStepVerificationSetupActivity.this.currentType == 2 && AndroidUtilities.isSmallScreen() && !TwoStepVerificationSetupActivity.this.isLandscape()) {
                            i9 = AndroidUtilities.dp(32.0f);
                        }
                        marginLayoutParams.topMargin = dp + i9;
                    }
                };
                linearLayout.setOrientation(1);
                this.scrollView.addView(linearLayout, LayoutHelper.createScroll(-1, -1, 51));
                linearLayout.addView(this.imageView, LayoutHelper.createLinear(-2, -2, 49, 0, 69, 0, 0));
                linearLayout.addView(this.titleTextView, LayoutHelper.createLinear(-2, -2, 49, 0, 8, 0, 0));
                linearLayout.addView(this.descriptionText, LayoutHelper.createLinear(-2, -2, 49, 0, 9, 0, 0));
                OutlineTextContainerView outlineTextContainerView = new OutlineTextContainerView(context);
                this.outlineTextFirstRow = outlineTextContainerView;
                outlineTextContainerView.animateSelection(1.0f, false);
                EditTextBoldCursor editTextBoldCursor = new EditTextBoldCursor(context);
                this.editTextFirstRow = editTextBoldCursor;
                editTextBoldCursor.setTextSize(1, 18.0f);
                int dp = AndroidUtilities.dp(16.0f);
                this.editTextFirstRow.setPadding(dp, dp, dp, dp);
                EditTextBoldCursor editTextBoldCursor2 = this.editTextFirstRow;
                int i7 = Theme.key_windowBackgroundWhiteInputFieldActivated;
                editTextBoldCursor2.setCursorColor(Theme.getColor(i7));
                this.editTextFirstRow.setTextColor(Theme.getColor(i3));
                this.editTextFirstRow.setBackground(null);
                this.editTextFirstRow.setMaxLines(1);
                this.editTextFirstRow.setLines(1);
                this.editTextFirstRow.setGravity(3);
                this.editTextFirstRow.setCursorSize(AndroidUtilities.dp(20.0f));
                this.editTextFirstRow.setSingleLine(true);
                this.editTextFirstRow.setCursorWidth(1.5f);
                this.editTextFirstRow.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                    @Override
                    public final boolean onEditorAction(TextView textView6, int i8, KeyEvent keyEvent) {
                        boolean lambda$createView$11;
                        lambda$createView$11 = TwoStepVerificationSetupActivity.this.lambda$createView$11(textView6, i8, keyEvent);
                        return lambda$createView$11;
                    }
                });
                this.outlineTextFirstRow.attachEditText(this.editTextFirstRow);
                this.editTextFirstRow.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                    @Override
                    public final void onFocusChange(View view, boolean z) {
                        TwoStepVerificationSetupActivity.this.lambda$createView$12(view, z);
                    }
                });
                LinearLayout linearLayout2 = new LinearLayout(context);
                linearLayout2.setOrientation(0);
                linearLayout2.addView(this.editTextFirstRow, LayoutHelper.createLinear(0, -2, 1.0f));
                ImageView imageView = new ImageView(context) {
                    @Override
                    public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo accessibilityNodeInfo) {
                        super.onInitializeAccessibilityNodeInfo(accessibilityNodeInfo);
                        accessibilityNodeInfo.setCheckable(true);
                        accessibilityNodeInfo.setChecked(TwoStepVerificationSetupActivity.this.editTextFirstRow.getTransformationMethod() == null);
                    }
                };
                this.showPasswordButton = imageView;
                imageView.setImageResource(R.drawable.msg_message);
                this.showPasswordButton.setScaleType(scaleType);
                this.showPasswordButton.setContentDescription(LocaleController.getString(R.string.TwoStepVerificationShowPassword));
                if (i4 >= 21) {
                    this.showPasswordButton.setBackground(Theme.createSelectorDrawable(Theme.getColor(Theme.key_listSelector)));
                }
                this.showPasswordButton.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_chat_messagePanelIcons), PorterDuff.Mode.MULTIPLY));
                AndroidUtilities.updateViewVisibilityAnimated(this.showPasswordButton, false, 0.1f, false);
                this.showPasswordButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public final void onClick(View view) {
                        TwoStepVerificationSetupActivity.this.lambda$createView$13(view);
                    }
                });
                linearLayout2.addView(this.showPasswordButton, LayoutHelper.createLinear(24, 24, 16, 0, 0, 16, 0));
                this.editTextFirstRow.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void afterTextChanged(Editable editable) {
                        if (TwoStepVerificationSetupActivity.this.needPasswordButton) {
                            if (TwoStepVerificationSetupActivity.this.showPasswordButton.getVisibility() != 0 && !TextUtils.isEmpty(editable)) {
                                AndroidUtilities.updateViewVisibilityAnimated(TwoStepVerificationSetupActivity.this.showPasswordButton, true, 0.1f, true);
                            } else {
                                if (TwoStepVerificationSetupActivity.this.showPasswordButton.getVisibility() == 8 || !TextUtils.isEmpty(editable)) {
                                    return;
                                }
                                AndroidUtilities.updateViewVisibilityAnimated(TwoStepVerificationSetupActivity.this.showPasswordButton, false, 0.1f, true);
                            }
                        }
                    }

                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i8, int i9, int i10) {
                    }

                    @Override
                    public void onTextChanged(CharSequence charSequence, int i8, int i9, int i10) {
                    }
                });
                this.outlineTextFirstRow.addView(linearLayout2, LayoutHelper.createFrame(-1, -2.0f));
                linearLayout.addView(this.outlineTextFirstRow, LayoutHelper.createFrame(-1, -2.0f, 49, 24.0f, 32.0f, 24.0f, 32.0f));
                this.outlineTextSecondRow = new OutlineTextContainerView(context);
                EditTextBoldCursor editTextBoldCursor3 = new EditTextBoldCursor(context);
                this.editTextSecondRow = editTextBoldCursor3;
                editTextBoldCursor3.setTextSize(1, 18.0f);
                int dp2 = AndroidUtilities.dp(16.0f);
                this.editTextSecondRow.setPadding(dp2, dp2, dp2, dp2);
                this.editTextSecondRow.setCursorColor(Theme.getColor(i7));
                this.editTextSecondRow.setTextColor(Theme.getColor(i3));
                this.editTextSecondRow.setBackground(null);
                this.editTextSecondRow.setMaxLines(1);
                this.editTextSecondRow.setLines(1);
                this.editTextSecondRow.setGravity(3);
                this.editTextSecondRow.setCursorSize(AndroidUtilities.dp(20.0f));
                this.editTextSecondRow.setSingleLine(true);
                this.editTextSecondRow.setCursorWidth(1.5f);
                this.editTextSecondRow.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                    @Override
                    public final boolean onEditorAction(TextView textView6, int i8, KeyEvent keyEvent) {
                        boolean lambda$createView$14;
                        lambda$createView$14 = TwoStepVerificationSetupActivity.this.lambda$createView$14(textView6, i8, keyEvent);
                        return lambda$createView$14;
                    }
                });
                this.outlineTextSecondRow.attachEditText(this.editTextSecondRow);
                this.editTextSecondRow.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                    @Override
                    public final void onFocusChange(View view, boolean z) {
                        TwoStepVerificationSetupActivity.this.lambda$createView$15(view, z);
                    }
                });
                this.outlineTextSecondRow.addView(this.editTextSecondRow, LayoutHelper.createFrame(-1, -2.0f));
                linearLayout.addView(this.outlineTextSecondRow, LayoutHelper.createFrame(-1, -2.0f, 49, 24.0f, 16.0f, 24.0f, 0.0f));
                this.outlineTextSecondRow.setVisibility(8);
                CustomPhoneKeyboardView customPhoneKeyboardView = new CustomPhoneKeyboardView(context);
                this.keyboardView = customPhoneKeyboardView;
                customPhoneKeyboardView.setVisibility(8);
                sizeNotifierFrameLayout.addView(this.keyboardView);
                CodeFieldContainer codeFieldContainer = new CodeFieldContainer(context) {
                    @Override
                    protected void processNextPressed() {
                        TwoStepVerificationSetupActivity.this.processNext();
                    }
                };
                this.codeFieldContainer = codeFieldContainer;
                codeFieldContainer.setNumbersCount(6, 1);
                for (CodeNumberField codeNumberField : this.codeFieldContainer.codeField) {
                    codeNumberField.setShowSoftInputOnFocusCompat(!isCustomKeyboardVisible());
                    codeNumberField.addTextChangedListener(new TextWatcher() {
                        @Override
                        public void afterTextChanged(Editable editable) {
                            if (TwoStepVerificationSetupActivity.this.postedErrorColorTimeout) {
                                AndroidUtilities.cancelRunOnUIThread(TwoStepVerificationSetupActivity.this.errorColorTimeout);
                                TwoStepVerificationSetupActivity.this.errorColorTimeout.run();
                            }
                        }

                        @Override
                        public void beforeTextChanged(CharSequence charSequence, int i8, int i9, int i10) {
                        }

                        @Override
                        public void onTextChanged(CharSequence charSequence, int i8, int i9, int i10) {
                        }
                    });
                    codeNumberField.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                        @Override
                        public final void onFocusChange(View view, boolean z) {
                            TwoStepVerificationSetupActivity.this.lambda$createView$16(view, z);
                        }
                    });
                }
                this.codeFieldContainer.setVisibility(8);
                linearLayout.addView(this.codeFieldContainer, LayoutHelper.createLinear(-2, -2, 1, 0, 32, 0, 0));
                FrameLayout frameLayout2 = new FrameLayout(context);
                linearLayout.addView(frameLayout2, LayoutHelper.createLinear(-1, -2, 51, 0, 36, 0, 22));
                frameLayout2.addView(this.descriptionText2, LayoutHelper.createFrame(-2, -2, 49));
                if (this.currentType == 4) {
                    TextView textView6 = new TextView(context);
                    this.descriptionText3 = textView6;
                    textView6.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteLinkText));
                    this.descriptionText3.setGravity(1);
                    this.descriptionText3.setTextSize(1, 14.0f);
                    this.descriptionText3.setLineSpacing(AndroidUtilities.dp(2.0f), 1.0f);
                    this.descriptionText3.setPadding(AndroidUtilities.dp(32.0f), 0, AndroidUtilities.dp(32.0f), 0);
                    this.descriptionText3.setText(LocaleController.getString(R.string.RestoreEmailTroubleNoEmail));
                    linearLayout.addView(this.descriptionText3, LayoutHelper.createLinear(-2, -2, 49, 0, 0, 0, 25));
                    this.descriptionText3.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public final void onClick(View view) {
                            TwoStepVerificationSetupActivity.this.lambda$createView$18(view);
                        }
                    });
                }
                this.fragmentView = viewGroup;
                View view = new View(context) {
                    private Paint paint = new Paint();

                    @Override
                    protected void onDraw(Canvas canvas) {
                        this.paint.setColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                        int measuredHeight = getMeasuredHeight() - AndroidUtilities.dp(3.0f);
                        canvas.drawRect(0.0f, 0.0f, getMeasuredWidth(), measuredHeight, this.paint);
                        ((BaseFragment) TwoStepVerificationSetupActivity.this).parentLayout.drawHeaderShadow(canvas, measuredHeight);
                    }
                };
                this.actionBarBackground = view;
                view.setAlpha(0.0f);
                viewGroup.addView(this.actionBarBackground);
                viewGroup.addView(this.actionBar);
                RadialProgressView radialProgressView2 = new RadialProgressView(context);
                this.radialProgressView = radialProgressView2;
                radialProgressView2.setSize(AndroidUtilities.dp(20.0f));
                this.radialProgressView.setAlpha(0.0f);
                this.radialProgressView.setScaleX(0.1f);
                this.radialProgressView.setScaleY(0.1f);
                this.radialProgressView.setProgressColor(Theme.getColor(Theme.key_windowBackgroundWhiteInputFieldActivated));
                frameLayout.addView(this.radialProgressView, LayoutHelper.createFrame(32, 32.0f, 53, 0.0f, 16.0f, 16.0f, 0.0f));
                break;
            case 6:
            case 7:
            case 9:
                ViewGroup viewGroup2 = new ViewGroup(context) {
                    @Override
                    protected void onLayout(boolean z, int i8, int i9, int i10, int i11) {
                        int measuredWidth;
                        int measuredHeight;
                        ((BaseFragment) TwoStepVerificationSetupActivity.this).actionBar.layout(0, 0, i10, ((BaseFragment) TwoStepVerificationSetupActivity.this).actionBar.getMeasuredHeight());
                        int i12 = i10 - i8;
                        int i13 = i11 - i9;
                        if (i10 > i11) {
                            int measuredHeight2 = (i13 - TwoStepVerificationSetupActivity.this.imageView.getMeasuredHeight()) / 2;
                            TwoStepVerificationSetupActivity.this.imageView.layout(0, measuredHeight2, TwoStepVerificationSetupActivity.this.imageView.getMeasuredWidth(), TwoStepVerificationSetupActivity.this.imageView.getMeasuredHeight() + measuredHeight2);
                            float f = i12;
                            float f2 = 0.4f * f;
                            int i14 = (int) f2;
                            float f3 = i13;
                            int i15 = (int) (0.22f * f3);
                            TwoStepVerificationSetupActivity.this.titleTextView.layout(i14, i15, TwoStepVerificationSetupActivity.this.titleTextView.getMeasuredWidth() + i14, TwoStepVerificationSetupActivity.this.titleTextView.getMeasuredHeight() + i15);
                            int i16 = (int) (0.39f * f3);
                            TwoStepVerificationSetupActivity.this.descriptionText.layout(i14, i16, TwoStepVerificationSetupActivity.this.descriptionText.getMeasuredWidth() + i14, TwoStepVerificationSetupActivity.this.descriptionText.getMeasuredHeight() + i16);
                            measuredWidth = (int) (f2 + (((f * 0.6f) - TwoStepVerificationSetupActivity.this.buttonTextView.getMeasuredWidth()) / 2.0f));
                            measuredHeight = (int) (f3 * 0.64f);
                        } else {
                            int i17 = (int) (i13 * 0.3f);
                            int measuredWidth2 = (i12 - TwoStepVerificationSetupActivity.this.imageView.getMeasuredWidth()) / 2;
                            TwoStepVerificationSetupActivity.this.imageView.layout(measuredWidth2, i17, TwoStepVerificationSetupActivity.this.imageView.getMeasuredWidth() + measuredWidth2, TwoStepVerificationSetupActivity.this.imageView.getMeasuredHeight() + i17);
                            int measuredHeight3 = i17 + TwoStepVerificationSetupActivity.this.imageView.getMeasuredHeight() + AndroidUtilities.dp(16.0f);
                            TwoStepVerificationSetupActivity.this.titleTextView.layout(0, measuredHeight3, TwoStepVerificationSetupActivity.this.titleTextView.getMeasuredWidth(), TwoStepVerificationSetupActivity.this.titleTextView.getMeasuredHeight() + measuredHeight3);
                            int measuredHeight4 = measuredHeight3 + TwoStepVerificationSetupActivity.this.titleTextView.getMeasuredHeight() + AndroidUtilities.dp(12.0f);
                            TwoStepVerificationSetupActivity.this.descriptionText.layout(0, measuredHeight4, TwoStepVerificationSetupActivity.this.descriptionText.getMeasuredWidth(), TwoStepVerificationSetupActivity.this.descriptionText.getMeasuredHeight() + measuredHeight4);
                            measuredWidth = (i12 - TwoStepVerificationSetupActivity.this.buttonTextView.getMeasuredWidth()) / 2;
                            measuredHeight = (i13 - TwoStepVerificationSetupActivity.this.buttonTextView.getMeasuredHeight()) - AndroidUtilities.dp(48.0f);
                        }
                        TwoStepVerificationSetupActivity.this.buttonTextView.layout(measuredWidth, measuredHeight, TwoStepVerificationSetupActivity.this.buttonTextView.getMeasuredWidth() + measuredWidth, TwoStepVerificationSetupActivity.this.buttonTextView.getMeasuredHeight() + measuredHeight);
                    }

                    @Override
                    protected void onMeasure(int i8, int i9) {
                        TextView textView7;
                        int makeMeasureSpec;
                        float f;
                        int size = View.MeasureSpec.getSize(i8);
                        int size2 = View.MeasureSpec.getSize(i9);
                        ((BaseFragment) TwoStepVerificationSetupActivity.this).actionBar.measure(View.MeasureSpec.makeMeasureSpec(size, 1073741824), i9);
                        TwoStepVerificationSetupActivity twoStepVerificationSetupActivity = TwoStepVerificationSetupActivity.this;
                        if (size > size2) {
                            float f2 = size;
                            twoStepVerificationSetupActivity.imageView.measure(View.MeasureSpec.makeMeasureSpec((int) (0.45f * f2), 1073741824), View.MeasureSpec.makeMeasureSpec((int) (size2 * 0.68f), 1073741824));
                            int i10 = (int) (f2 * 0.6f);
                            TwoStepVerificationSetupActivity.this.titleTextView.measure(View.MeasureSpec.makeMeasureSpec(i10, 1073741824), View.MeasureSpec.makeMeasureSpec(size2, 0));
                            TwoStepVerificationSetupActivity.this.descriptionText.measure(View.MeasureSpec.makeMeasureSpec(i10, 1073741824), View.MeasureSpec.makeMeasureSpec(size2, 0));
                            TwoStepVerificationSetupActivity.this.descriptionText2.measure(View.MeasureSpec.makeMeasureSpec(i10, 1073741824), View.MeasureSpec.makeMeasureSpec(size2, 0));
                            textView7 = TwoStepVerificationSetupActivity.this.buttonTextView;
                            makeMeasureSpec = View.MeasureSpec.makeMeasureSpec(i10, Integer.MIN_VALUE);
                            f = 42.0f;
                        } else {
                            float f3 = twoStepVerificationSetupActivity.currentType == 7 ? 160 : 140;
                            TwoStepVerificationSetupActivity.this.imageView.measure(View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(f3), 1073741824), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(f3), 1073741824));
                            TwoStepVerificationSetupActivity.this.titleTextView.measure(View.MeasureSpec.makeMeasureSpec(size, 1073741824), View.MeasureSpec.makeMeasureSpec(size2, 0));
                            TwoStepVerificationSetupActivity.this.descriptionText.measure(View.MeasureSpec.makeMeasureSpec(size, 1073741824), View.MeasureSpec.makeMeasureSpec(size2, 0));
                            TwoStepVerificationSetupActivity.this.descriptionText2.measure(View.MeasureSpec.makeMeasureSpec(size, 1073741824), View.MeasureSpec.makeMeasureSpec(size2, 0));
                            textView7 = TwoStepVerificationSetupActivity.this.buttonTextView;
                            makeMeasureSpec = View.MeasureSpec.makeMeasureSpec(size - AndroidUtilities.dp(48.0f), 1073741824);
                            f = 50.0f;
                        }
                        textView7.measure(makeMeasureSpec, View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(f), 1073741824));
                        setMeasuredDimension(size, size2);
                    }
                };
                viewGroup2.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public final boolean onTouch(View view2, MotionEvent motionEvent) {
                        boolean lambda$createView$10;
                        lambda$createView$10 = TwoStepVerificationSetupActivity.lambda$createView$10(view2, motionEvent);
                        return lambda$createView$10;
                    }
                });
                viewGroup2.addView(this.actionBar);
                viewGroup2.addView(this.imageView);
                viewGroup2.addView(this.titleTextView);
                viewGroup2.addView(this.descriptionText);
                viewGroup2.addView(this.buttonTextView);
                this.fragmentView = viewGroup2;
                break;
        }
        this.fragmentView.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
        int i8 = this.currentType;
        int i9 = 140;
        int i10 = 120;
        switch (i8) {
            case 0:
            case 1:
                if (this.currentPassword.has_password) {
                    ActionBar actionBar2 = this.actionBar;
                    int i11 = R.string.PleaseEnterNewFirstPassword;
                    actionBar2.setTitle(LocaleController.getString(i11));
                    this.titleTextView.setText(LocaleController.getString(i11));
                } else {
                    String string = LocaleController.getString(i8 == 0 ? R.string.CreatePassword : R.string.ReEnterPassword);
                    this.actionBar.setTitle(string);
                    this.titleTextView.setText(string);
                }
                if (!TextUtils.isEmpty(this.emailCode)) {
                    this.bottomSkipButton.setVisibility(0);
                    this.bottomSkipButton.setText(LocaleController.getString(R.string.YourEmailSkip));
                }
                this.actionBar.getTitleTextView().setAlpha(0.0f);
                this.outlineTextFirstRow.setText(LocaleController.getString(this.currentType == 0 ? R.string.EnterPassword : R.string.ReEnterPassword));
                this.editTextFirstRow.setContentDescription(LocaleController.getString(this.currentType == 0 ? R.string.EnterPassword : R.string.ReEnterPassword));
                this.editTextFirstRow.setImeOptions(268435461);
                this.editTextFirstRow.setInputType(129);
                this.editTextFirstRow.setTransformationMethod(PasswordTransformationMethod.getInstance());
                this.editTextFirstRow.setTypeface(Typeface.DEFAULT);
                this.needPasswordButton = this.currentType == 0;
                AndroidUtilities.updateViewVisibilityAnimated(this.showPasswordButton, false, 0.1f, false);
                RLottieDrawable[] rLottieDrawableArr = new RLottieDrawable[7];
                this.animationDrawables = rLottieDrawableArr;
                int i12 = R.raw.tsv_setup_monkey_idle1;
                rLottieDrawableArr[0] = new RLottieDrawable(i12, "" + i12, AndroidUtilities.dp(120.0f), AndroidUtilities.dp(120.0f), true, null);
                RLottieDrawable[] rLottieDrawableArr2 = this.animationDrawables;
                int i13 = R.raw.tsv_setup_monkey_idle2;
                rLottieDrawableArr2[1] = new RLottieDrawable(i13, "" + i13, AndroidUtilities.dp(120.0f), AndroidUtilities.dp(120.0f), true, null);
                RLottieDrawable[] rLottieDrawableArr3 = this.animationDrawables;
                int i14 = R.raw.tsv_monkey_close;
                rLottieDrawableArr3[2] = new RLottieDrawable(i14, "" + i14, AndroidUtilities.dp(120.0f), AndroidUtilities.dp(120.0f), true, null);
                RLottieDrawable[] rLottieDrawableArr4 = this.animationDrawables;
                int i15 = R.raw.tsv_setup_monkey_peek;
                rLottieDrawableArr4[3] = new RLottieDrawable(i15, "" + i15, AndroidUtilities.dp(120.0f), AndroidUtilities.dp(120.0f), true, null);
                RLottieDrawable[] rLottieDrawableArr5 = this.animationDrawables;
                int i16 = R.raw.tsv_setup_monkey_close_and_peek_to_idle;
                rLottieDrawableArr5[4] = new RLottieDrawable(i16, "" + i16, AndroidUtilities.dp(120.0f), AndroidUtilities.dp(120.0f), true, null);
                RLottieDrawable[] rLottieDrawableArr6 = this.animationDrawables;
                int i17 = R.raw.tsv_setup_monkey_close_and_peek;
                rLottieDrawableArr6[5] = new RLottieDrawable(i17, "" + i17, AndroidUtilities.dp(120.0f), AndroidUtilities.dp(120.0f), true, null);
                RLottieDrawable[] rLottieDrawableArr7 = this.animationDrawables;
                int i18 = R.raw.tsv_setup_monkey_tracking;
                rLottieDrawableArr7[6] = new RLottieDrawable(i18, "" + i18, AndroidUtilities.dp(120.0f), AndroidUtilities.dp(120.0f), true, null);
                this.animationDrawables[6].setPlayInDirectionOfCustomEndFrame(true);
                this.animationDrawables[6].setCustomEndFrame(19);
                this.animationDrawables[2].setOnFinishCallback(this.finishCallback, 97);
                setRandomMonkeyIdleAnimation(true);
                switchMonkeyAnimation(this.currentType == 1);
                break;
            case 2:
                ActionBar actionBar3 = this.actionBar;
                int i19 = R.string.PasswordHint;
                actionBar3.setTitle(LocaleController.getString(i19));
                this.actionBar.getTitleTextView().setAlpha(0.0f);
                this.bottomSkipButton.setVisibility(0);
                this.bottomSkipButton.setText(LocaleController.getString(R.string.YourEmailSkip));
                this.titleTextView.setText(LocaleController.getString(i19));
                this.descriptionText.setText(LocaleController.getString(R.string.PasswordHintDescription));
                this.descriptionText.setVisibility(0);
                OutlineTextContainerView outlineTextContainerView2 = this.outlineTextFirstRow;
                int i20 = R.string.PasswordHintPlaceholder;
                outlineTextContainerView2.setText(LocaleController.getString(i20));
                this.editTextFirstRow.setContentDescription(LocaleController.getString(i20));
                this.editTextFirstRow.setImeOptions(268435461);
                this.outlineTextSecondRow.setVisibility(8);
                rLottieImageView = this.imageView;
                i = R.raw.tsv_setup_hint;
                i9 = 120;
                rLottieImageView.setAnimation(i, i9, i10);
                this.imageView.playAnimation();
                break;
            case 3:
                ActionBar actionBar4 = this.actionBar;
                int i21 = R.string.RecoveryEmailTitle;
                actionBar4.setTitle(LocaleController.getString(i21));
                this.actionBar.getTitleTextView().setAlpha(0.0f);
                if (!this.emailOnly) {
                    this.bottomSkipButton.setVisibility(0);
                    this.bottomSkipButton.setAlpha(0.0f);
                    this.bottomSkipButton.setText(LocaleController.getString(R.string.YourEmailSkip));
                }
                this.titleTextView.setText(LocaleController.getString(i21));
                this.descriptionText.setText(LocaleController.getString(R.string.RecoveryEmailSubtitle));
                this.descriptionText.setVisibility(0);
                OutlineTextContainerView outlineTextContainerView3 = this.outlineTextFirstRow;
                int i22 = R.string.PaymentShippingEmailPlaceholder;
                outlineTextContainerView3.setText(LocaleController.getString(i22));
                this.editTextFirstRow.setContentDescription(LocaleController.getString(i22));
                this.editTextFirstRow.setImeOptions(268435461);
                this.editTextFirstRow.setInputType(33);
                this.outlineTextSecondRow.setVisibility(8);
                rLottieImageView = this.imageView;
                i = R.raw.tsv_setup_email_sent;
                i9 = 120;
                rLottieImageView.setAnimation(i, i9, i10);
                this.imageView.playAnimation();
                break;
            case 4:
                ActionBar actionBar5 = this.actionBar;
                int i23 = R.string.PasswordRecovery;
                actionBar5.setTitle(LocaleController.getString(i23));
                this.actionBar.getTitleTextView().setAlpha(0.0f);
                this.titleTextView.setText(LocaleController.getString(i23));
                this.keyboardView.setVisibility(0);
                this.outlineTextFirstRow.setVisibility(8);
                String str = this.currentPassword.email_unconfirmed_pattern;
                String str2 = str != null ? str : "";
                SpannableStringBuilder valueOf = SpannableStringBuilder.valueOf(str2);
                int indexOf = str2.indexOf(42);
                int lastIndexOf = str2.lastIndexOf(42);
                if (indexOf != lastIndexOf && indexOf != -1 && lastIndexOf != -1) {
                    TextStyleSpan.TextStyleRun textStyleRun = new TextStyleSpan.TextStyleRun();
                    textStyleRun.flags |= 256;
                    textStyleRun.start = indexOf;
                    int i24 = lastIndexOf + 1;
                    textStyleRun.end = i24;
                    valueOf.setSpan(new TextStyleSpan(textStyleRun), indexOf, i24, 0);
                }
                this.descriptionText.setText(AndroidUtilities.formatSpannable(LocaleController.getString(R.string.RestoreEmailSent), valueOf));
                this.descriptionText.setVisibility(0);
                this.floatingButtonContainer.setVisibility(8);
                this.codeFieldContainer.setVisibility(0);
                rLottieImageView = this.imageView;
                i = R.raw.tsv_setup_mail;
                i9 = 120;
                rLottieImageView.setAnimation(i, i9, i10);
                this.imageView.playAnimation();
                break;
            case 5:
                ActionBar actionBar6 = this.actionBar;
                int i25 = R.string.VerificationCode;
                actionBar6.setTitle(LocaleController.getString(i25));
                this.actionBar.getTitleTextView().setAlpha(0.0f);
                this.titleTextView.setText(LocaleController.getString(i25));
                this.outlineTextFirstRow.setVisibility(8);
                this.keyboardView.setVisibility(0);
                TextView textView7 = this.descriptionText;
                int i26 = R.string.EmailPasswordConfirmText2;
                String str3 = this.currentPassword.email_unconfirmed_pattern;
                textView7.setText(LocaleController.formatString("EmailPasswordConfirmText2", i26, str3 != null ? str3 : ""));
                this.descriptionText.setVisibility(0);
                this.floatingButtonContainer.setVisibility(8);
                this.bottomSkipButton.setVisibility(0);
                this.bottomSkipButton.setGravity(17);
                ((ViewGroup.MarginLayoutParams) this.bottomSkipButton.getLayoutParams()).bottomMargin = 0;
                this.bottomSkipButton.setText(LocaleController.getString(R.string.ResendCode));
                this.bottomSkipButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public final void onClick(View view2) {
                        TwoStepVerificationSetupActivity.this.lambda$createView$20(view2);
                    }
                });
                this.codeFieldContainer.setVisibility(0);
                rLottieImageView = this.imageView;
                i = R.raw.tsv_setup_mail;
                i9 = 120;
                rLottieImageView.setAnimation(i, i9, i10);
                this.imageView.playAnimation();
                break;
            case 6:
                this.titleTextView.setText(LocaleController.getString(R.string.TwoStepVerificationTitle));
                this.descriptionText.setText(LocaleController.getString(R.string.SetAdditionalPasswordInfo));
                this.buttonTextView.setText(LocaleController.getString(R.string.TwoStepVerificationSetPassword));
                this.descriptionText.setVisibility(0);
                rLottieImageView = this.imageView;
                i = R.raw.tsv_setup_intro;
                i10 = 140;
                rLottieImageView.setAnimation(i, i9, i10);
                this.imageView.playAnimation();
                break;
            case 7:
                this.titleTextView.setText(LocaleController.getString(R.string.TwoStepVerificationPasswordSet));
                this.descriptionText.setText(LocaleController.getString(R.string.TwoStepVerificationPasswordSetInfo));
                if (this.closeAfterSet) {
                    textView = this.buttonTextView;
                    i2 = R.string.TwoStepVerificationPasswordReturnPassport;
                } else if (this.fromRegistration) {
                    textView = this.buttonTextView;
                    i2 = R.string.Continue;
                } else {
                    textView = this.buttonTextView;
                    i2 = R.string.TwoStepVerificationPasswordReturnSettings;
                }
                textView.setText(LocaleController.getString(i2));
                this.descriptionText.setVisibility(0);
                rLottieImageView = this.imageView;
                i = R.raw.wallet_allset;
                i9 = 160;
                i10 = 160;
                rLottieImageView.setAnimation(i, i9, i10);
                this.imageView.playAnimation();
                break;
            case 8:
                ActionBar actionBar7 = this.actionBar;
                int i27 = R.string.PleaseEnterCurrentPassword;
                actionBar7.setTitle(LocaleController.getString(i27));
                this.titleTextView.setText(LocaleController.getString(i27));
                this.descriptionText.setText(LocaleController.getString(R.string.CheckPasswordInfo));
                this.descriptionText.setVisibility(0);
                this.actionBar.getTitleTextView().setAlpha(0.0f);
                this.descriptionText2.setText(LocaleController.getString(R.string.ForgotPassword));
                this.descriptionText2.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlueText2));
                OutlineTextContainerView outlineTextContainerView4 = this.outlineTextFirstRow;
                int i28 = R.string.LoginPassword;
                outlineTextContainerView4.setText(LocaleController.getString(i28));
                this.editTextFirstRow.setContentDescription(LocaleController.getString(i28));
                this.editTextFirstRow.setImeOptions(268435462);
                this.editTextFirstRow.setInputType(129);
                this.editTextFirstRow.setTransformationMethod(PasswordTransformationMethod.getInstance());
                this.editTextFirstRow.setTypeface(Typeface.DEFAULT);
                rLottieImageView = this.imageView;
                i = R.raw.wallet_science;
                i9 = 120;
                rLottieImageView.setAnimation(i, i9, i10);
                this.imageView.playAnimation();
                break;
            case 9:
                this.titleTextView.setText(LocaleController.getString(R.string.CheckPasswordPerfect));
                this.descriptionText.setText(LocaleController.getString(R.string.CheckPasswordPerfectInfo));
                this.buttonTextView.setText(LocaleController.getString(R.string.CheckPasswordBackToSettings));
                this.descriptionText.setVisibility(0);
                rLottieImageView = this.imageView;
                i = R.raw.wallet_perfect;
                i10 = 140;
                rLottieImageView.setAnimation(i, i9, i10);
                this.imageView.playAnimation();
                break;
        }
        EditTextBoldCursor editTextBoldCursor4 = this.editTextFirstRow;
        if (editTextBoldCursor4 != null) {
            editTextBoldCursor4.addTextChangedListener(new TextWatcher() {
                @Override
                public void afterTextChanged(Editable editable) {
                    RLottieDrawable rLottieDrawable;
                    if (TwoStepVerificationSetupActivity.this.ignoreTextChange) {
                        return;
                    }
                    if (TwoStepVerificationSetupActivity.this.currentType != 0) {
                        if (TwoStepVerificationSetupActivity.this.currentType != 1) {
                            if (TwoStepVerificationSetupActivity.this.currentType != 8 || editable.length() <= 0) {
                                return;
                            }
                            TwoStepVerificationSetupActivity.this.showDoneButton(true);
                            return;
                        }
                        try {
                            TwoStepVerificationSetupActivity.this.animationDrawables[6].setCustomEndFrame((int) ((Math.min(1.0f, TwoStepVerificationSetupActivity.this.editTextFirstRow.getLayout().getLineWidth(0) / TwoStepVerificationSetupActivity.this.editTextFirstRow.getWidth()) * 142.0f) + 18.0f));
                            TwoStepVerificationSetupActivity.this.imageView.playAnimation();
                            return;
                        } catch (Exception e) {
                            FileLog.e(e);
                            return;
                        }
                    }
                    RLottieDrawable animatedDrawable = TwoStepVerificationSetupActivity.this.imageView.getAnimatedDrawable();
                    if (TwoStepVerificationSetupActivity.this.editTextFirstRow.length() > 0) {
                        if (TwoStepVerificationSetupActivity.this.editTextFirstRow.getTransformationMethod() == null) {
                            if (animatedDrawable == TwoStepVerificationSetupActivity.this.animationDrawables[3] || animatedDrawable == TwoStepVerificationSetupActivity.this.animationDrawables[5]) {
                                return;
                            }
                            TwoStepVerificationSetupActivity.this.imageView.setAnimation(TwoStepVerificationSetupActivity.this.animationDrawables[5]);
                            rLottieDrawable = TwoStepVerificationSetupActivity.this.animationDrawables[5];
                        } else {
                            if (animatedDrawable == TwoStepVerificationSetupActivity.this.animationDrawables[3]) {
                                return;
                            }
                            if (animatedDrawable == TwoStepVerificationSetupActivity.this.animationDrawables[2]) {
                                if (TwoStepVerificationSetupActivity.this.animationDrawables[2].getCurrentFrame() < 49) {
                                    TwoStepVerificationSetupActivity.this.animationDrawables[2].setCustomEndFrame(49);
                                    return;
                                }
                                return;
                            } else {
                                TwoStepVerificationSetupActivity.this.imageView.setAnimation(TwoStepVerificationSetupActivity.this.animationDrawables[2]);
                                TwoStepVerificationSetupActivity.this.animationDrawables[2].setCustomEndFrame(49);
                                rLottieDrawable = TwoStepVerificationSetupActivity.this.animationDrawables[2];
                            }
                        }
                    } else {
                        if ((animatedDrawable != TwoStepVerificationSetupActivity.this.animationDrawables[3] || TwoStepVerificationSetupActivity.this.editTextFirstRow.getTransformationMethod() != null) && animatedDrawable != TwoStepVerificationSetupActivity.this.animationDrawables[5]) {
                            TwoStepVerificationSetupActivity.this.animationDrawables[2].setCustomEndFrame(-1);
                            if (animatedDrawable != TwoStepVerificationSetupActivity.this.animationDrawables[2]) {
                                TwoStepVerificationSetupActivity.this.imageView.setAnimation(TwoStepVerificationSetupActivity.this.animationDrawables[2]);
                                TwoStepVerificationSetupActivity.this.animationDrawables[2].setCurrentFrame(49, false);
                            }
                            TwoStepVerificationSetupActivity.this.imageView.playAnimation();
                        }
                        TwoStepVerificationSetupActivity.this.imageView.setAnimation(TwoStepVerificationSetupActivity.this.animationDrawables[4]);
                        rLottieDrawable = TwoStepVerificationSetupActivity.this.animationDrawables[4];
                    }
                    rLottieDrawable.setProgress(0.0f, false);
                    TwoStepVerificationSetupActivity.this.imageView.playAnimation();
                }

                @Override
                public void beforeTextChanged(CharSequence charSequence, int i29, int i30, int i31) {
                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i29, int i30, int i31) {
                }
            });
        }
        return this.fragmentView;
    }

    @Override
    public void lambda$onBackPressed$321() {
        if (this.otherwiseReloginDays < 0 || this.parentLayout.getFragmentStack().size() != 1) {
            super.lambda$onBackPressed$321();
            return;
        }
        Bundle bundle = new Bundle();
        bundle.putBoolean("afterSignup", true);
        presentFragment(new DialogsActivity(bundle), true);
    }

    @Override
    public boolean finishFragment(boolean z) {
        for (BaseFragment baseFragment : getParentLayout().getFragmentStack()) {
            if (baseFragment != this && (baseFragment instanceof TwoStepVerificationSetupActivity)) {
                ((TwoStepVerificationSetupActivity) baseFragment).floatingAutoAnimator.ignoreNextLayout();
            }
        }
        return super.finishFragment(z);
    }

    protected TLRPC.TL_inputCheckPasswordSRP getNewSrpPassword() {
        TLRPC.account_Password account_password = this.currentPassword;
        TLRPC.PasswordKdfAlgo passwordKdfAlgo = account_password.current_algo;
        if (!(passwordKdfAlgo instanceof TLRPC.TL_passwordKdfAlgoSHA256SHA256PBKDF2HMACSHA512iter100000SHA256ModPow)) {
            return null;
        }
        return SRPHelper.startCheck(this.currentPasswordHash, account_password.srp_id, account_password.srp_B, (TLRPC.TL_passwordKdfAlgoSHA256SHA256PBKDF2HMACSHA512iter100000SHA256ModPow) passwordKdfAlgo);
    }

    @Override
    public ArrayList getThemeDescriptions() {
        ArrayList arrayList = new ArrayList();
        arrayList.add(new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_CHECKTAG | ThemeDescription.FLAG_BACKGROUND, null, null, null, null, Theme.key_windowBackgroundWhite));
        arrayList.add(new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_BACKGROUND | ThemeDescription.FLAG_CHECKTAG, null, null, null, null, Theme.key_windowBackgroundGray));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, Theme.key_actionBarDefault));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, null, null, null, null, Theme.key_actionBarDefaultIcon));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, null, null, null, null, Theme.key_actionBarDefaultTitle));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, null, null, null, null, Theme.key_actionBarDefaultSelector));
        arrayList.add(new ThemeDescription(this.titleTextView, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, Theme.key_windowBackgroundWhiteGrayText6));
        arrayList.add(new ThemeDescription(this.editTextFirstRow, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, Theme.key_windowBackgroundWhiteBlackText));
        arrayList.add(new ThemeDescription(this.editTextFirstRow, ThemeDescription.FLAG_HINTTEXTCOLOR, null, null, null, null, Theme.key_windowBackgroundWhiteHintText));
        arrayList.add(new ThemeDescription(this.editTextFirstRow, ThemeDescription.FLAG_BACKGROUNDFILTER, null, null, null, null, Theme.key_windowBackgroundWhiteInputField));
        arrayList.add(new ThemeDescription(this.editTextFirstRow, ThemeDescription.FLAG_DRAWABLESELECTEDSTATE | ThemeDescription.FLAG_BACKGROUNDFILTER, null, null, null, null, Theme.key_windowBackgroundWhiteInputFieldActivated));
        return arrayList;
    }

    @Override
    public boolean hasForceLightStatusBar() {
        return true;
    }

    @Override
    public boolean hideKeyboardOnShow() {
        int i = this.currentType;
        return i == 7 || i == 9;
    }

    @Override
    public boolean isLightStatusBar() {
        return ColorUtils.calculateLuminance(Theme.getColor(Theme.key_windowBackgroundWhite, null, true)) > 0.699999988079071d;
    }

    @Override
    public boolean isSwipeBackEnabled(MotionEvent motionEvent) {
        if (this.otherwiseReloginDays < 0 || this.parentLayout.getFragmentStack().size() != 1) {
            return super.isSwipeBackEnabled(motionEvent);
        }
        return false;
    }

    protected void needHideProgress() {
        AnimatorSet animatorSet = new AnimatorSet();
        if (this.floatingButtonContainer.getVisibility() == 0) {
            RadialProgressView radialProgressView = this.floatingProgressView;
            Property property = View.ALPHA;
            ObjectAnimator ofFloat = ObjectAnimator.ofFloat(radialProgressView, (Property<RadialProgressView, Float>) property, 0.0f);
            RadialProgressView radialProgressView2 = this.floatingProgressView;
            Property property2 = View.SCALE_X;
            ObjectAnimator ofFloat2 = ObjectAnimator.ofFloat(radialProgressView2, (Property<RadialProgressView, Float>) property2, 0.1f);
            RadialProgressView radialProgressView3 = this.floatingProgressView;
            Property property3 = View.SCALE_Y;
            animatorSet.playTogether(ofFloat, ofFloat2, ObjectAnimator.ofFloat(radialProgressView3, (Property<RadialProgressView, Float>) property3, 0.1f), ObjectAnimator.ofFloat(this.floatingButtonIcon, (Property<TransformableLoginButtonView, Float>) property, 1.0f), ObjectAnimator.ofFloat(this.floatingButtonIcon, (Property<TransformableLoginButtonView, Float>) property2, 1.0f), ObjectAnimator.ofFloat(this.floatingButtonIcon, (Property<TransformableLoginButtonView, Float>) property3, 1.0f));
        } else {
            animatorSet.playTogether(ObjectAnimator.ofFloat(this.radialProgressView, (Property<RadialProgressView, Float>) View.ALPHA, 0.0f), ObjectAnimator.ofFloat(this.radialProgressView, (Property<RadialProgressView, Float>) View.SCALE_X, 0.1f), ObjectAnimator.ofFloat(this.radialProgressView, (Property<RadialProgressView, Float>) View.SCALE_Y, 0.1f));
        }
        animatorSet.setInterpolator(CubicBezierInterpolator.DEFAULT);
        animatorSet.start();
    }

    @Override
    public boolean onBackPressed() {
        if (this.otherwiseReloginDays < 0 || this.parentLayout.getFragmentStack().size() != 1) {
            lambda$onBackPressed$321();
            return true;
        }
        showSetForcePasswordAlert();
        return false;
    }

    @Override
    public void onConfigurationChanged(Configuration configuration) {
        super.onConfigurationChanged(configuration);
        if (this.imageView != null) {
            if (this.currentType == 2 && AndroidUtilities.isSmallScreen()) {
                this.imageView.setVisibility(8);
            } else if (!isIntro()) {
                this.imageView.setVisibility(isLandscape() ? 8 : 0);
            }
        }
        CustomPhoneKeyboardView customPhoneKeyboardView = this.keyboardView;
        if (customPhoneKeyboardView != null) {
            customPhoneKeyboardView.setVisibility(isCustomKeyboardVisible() ? 0 : 8);
        }
    }

    @Override
    public void onFragmentDestroy() {
        super.onFragmentDestroy();
        this.doneAfterPasswordLoad = false;
        Runnable runnable = this.setAnimationRunnable;
        if (runnable != null) {
            AndroidUtilities.cancelRunOnUIThread(runnable);
            this.setAnimationRunnable = null;
        }
        if (this.animationDrawables != null) {
            int i = 0;
            while (true) {
                RLottieDrawable[] rLottieDrawableArr = this.animationDrawables;
                if (i >= rLottieDrawableArr.length) {
                    break;
                }
                rLottieDrawableArr[i].recycle(false);
                i++;
            }
            this.animationDrawables = null;
        }
        AndroidUtilities.removeAdjustResize(getParentActivity(), this.classGuid);
        if (isCustomKeyboardVisible()) {
            AndroidUtilities.removeAltFocusable(getParentActivity(), this.classGuid);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        this.paused = true;
    }

    protected void onReset() {
    }

    @Override
    public void onResume() {
        super.onResume();
        this.paused = false;
        AndroidUtilities.requestAdjustResize(getParentActivity(), this.classGuid);
        if (isCustomKeyboardVisible()) {
            AndroidUtilities.requestAltFocusable(getParentActivity(), this.classGuid);
            AndroidUtilities.hideKeyboard(this.fragmentView);
        }
    }

    @Override
    public void onTransitionAnimationEnd(boolean z, boolean z2) {
        if (z) {
            if (this.editTextFirstRow != null && !isCustomKeyboardVisible()) {
                AndroidUtilities.runOnUIThread(new Runnable() {
                    @Override
                    public final void run() {
                        TwoStepVerificationSetupActivity.this.lambda$onTransitionAnimationEnd$39();
                    }
                }, 200L);
            }
            CodeFieldContainer codeFieldContainer = this.codeFieldContainer;
            if (codeFieldContainer == null || codeFieldContainer.getVisibility() != 0) {
                return;
            }
            AndroidUtilities.runOnUIThread(new Runnable() {
                @Override
                public final void run() {
                    TwoStepVerificationSetupActivity.this.lambda$onTransitionAnimationEnd$40();
                }
            }, 200L);
        }
    }

    public void setBlockingAlert(int i) {
        this.otherwiseReloginDays = i;
    }

    public void setCloseAfterSet(boolean z) {
        this.closeAfterSet = z;
    }

    public void setCurrentEmailCode(String str) {
        this.emailCode = str;
    }

    public void setCurrentPasswordParams(byte[] bArr, long j, byte[] bArr2, boolean z) {
        this.currentPasswordHash = bArr;
        this.currentSecret = bArr2;
        this.currentSecretId = j;
        this.emailOnly = z;
    }

    public void setFromRegistration(boolean z) {
        this.fromRegistration = z;
    }
}
