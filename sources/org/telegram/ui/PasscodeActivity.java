package org.telegram.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.biometric.BiometricManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicBoolean;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.R;
import org.telegram.messenger.SharedConfig;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.Utilities;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.ActionBarMenuItem;
import org.telegram.ui.ActionBar.ActionBarMenuSubItem;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ActionBar.ThemeDescription;
import org.telegram.ui.Cells.HeaderCell;
import org.telegram.ui.Cells.TextCheckCell;
import org.telegram.ui.Cells.TextInfoPrivacyCell;
import org.telegram.ui.Cells.TextSettingsCell;
import org.telegram.ui.Components.AlertsCreator;
import org.telegram.ui.Components.CubicBezierInterpolator;
import org.telegram.ui.Components.CustomPhoneKeyboardView;
import org.telegram.ui.Components.Easings;
import org.telegram.ui.Components.EditTextBoldCursor;
import org.telegram.ui.Components.NumberPicker;
import org.telegram.ui.Components.OutlineTextContainerView;
import org.telegram.ui.Components.RLottieImageView;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.Components.TextViewSwitcher;
import org.telegram.ui.Components.TransformableLoginButtonView;
import org.telegram.ui.Components.VerticalPositionAutoAnimator;
import org.telegram.ui.PasscodeActivity;

public class PasscodeActivity extends BaseFragment implements NotificationCenter.NotificationCenterDelegate {
    private int autoLockDetailRow;
    private int autoLockRow;
    private int captureDetailRow;
    private int captureHeaderRow;
    private int captureRow;
    private int changePasscodeRow;
    private CodeFieldContainer codeFieldContainer;
    private TextViewSwitcher descriptionTextSwitcher;
    private int disablePasscodeRow;
    private int fingerprintRow;
    private String firstPassword;
    private VerticalPositionAutoAnimator floatingAutoAnimator;
    private Animator floatingButtonAnimator;
    private FrameLayout floatingButtonContainer;
    private TransformableLoginButtonView floatingButtonIcon;
    private int hintRow;
    private CustomPhoneKeyboardView keyboardView;
    private ListAdapter listAdapter;
    private RecyclerListView listView;
    private RLottieImageView lockImageView;
    private Runnable onShowKeyboardCallback;
    private ActionBarMenuItem otherItem;
    private OutlineTextContainerView outlinePasswordView;
    private TextView passcodesDoNotMatchTextView;
    private ImageView passwordButton;
    private EditTextBoldCursor passwordEditText;
    private boolean postedHidePasscodesDoNotMatch;
    private int rowCount;
    private TextView titleTextView;
    private int type;
    private int utyanRow;
    private int currentPasswordType = 0;
    private int passcodeSetStep = 0;
    private Runnable hidePasscodesDoNotMatch = new Runnable() {
        @Override
        public final void run() {
            PasscodeActivity.this.lambda$new$0();
        }
    };

    public class AnonymousClass4 extends ActionBar.ActionBarMenuOnItemClick {
        final ActionBarMenuSubItem val$switchItem;

        AnonymousClass4(ActionBarMenuSubItem actionBarMenuSubItem) {
            this.val$switchItem = actionBarMenuSubItem;
        }

        public void lambda$onItemClick$0(ActionBarMenuSubItem actionBarMenuSubItem) {
            actionBarMenuSubItem.setText(LocaleController.getString(PasscodeActivity.this.currentPasswordType == 0 ? R.string.PasscodeSwitchToPassword : R.string.PasscodeSwitchToPIN));
            actionBarMenuSubItem.setIcon(PasscodeActivity.this.currentPasswordType == 0 ? R.drawable.msg_permissions : R.drawable.msg_pin_code);
            PasscodeActivity.this.showKeyboard();
            if (PasscodeActivity.this.isPinCode()) {
                PasscodeActivity.this.passwordEditText.setInputType(524417);
                AndroidUtilities.updateViewVisibilityAnimated(PasscodeActivity.this.passwordButton, true, 0.1f, false);
            }
        }

        @Override
        public void onItemClick(int i) {
            if (i == -1) {
                PasscodeActivity.this.lambda$onBackPressed$323();
                return;
            }
            if (i == 1) {
                PasscodeActivity passcodeActivity = PasscodeActivity.this;
                passcodeActivity.currentPasswordType = passcodeActivity.currentPasswordType != 0 ? 0 : 1;
                final ActionBarMenuSubItem actionBarMenuSubItem = this.val$switchItem;
                AndroidUtilities.runOnUIThread(new Runnable() {
                    @Override
                    public final void run() {
                        PasscodeActivity.AnonymousClass4.this.lambda$onItemClick$0(actionBarMenuSubItem);
                    }
                }, 150L);
                PasscodeActivity.this.passwordEditText.setText("");
                for (CodeNumberField codeNumberField : PasscodeActivity.this.codeFieldContainer.codeField) {
                    codeNumberField.setText("");
                }
                PasscodeActivity.this.updateFields();
            }
        }
    }

    public class AnonymousClass8 extends CodeFieldContainer {
        AnonymousClass8(Context context) {
            super(context);
        }

        public void lambda$processNextPressed$0() {
            PasscodeActivity.this.processNext();
        }

        @Override
        protected void processNextPressed() {
            if (PasscodeActivity.this.passcodeSetStep == 0) {
                postDelayed(new Runnable() {
                    @Override
                    public final void run() {
                        PasscodeActivity.AnonymousClass8.this.lambda$processNextPressed$0();
                    }
                }, 260L);
            } else {
                PasscodeActivity.this.processDone();
            }
        }
    }

    public class ListAdapter extends RecyclerListView.SelectionAdapter {
        private Context mContext;

        public ListAdapter(Context context) {
            this.mContext = context;
        }

        @Override
        public int getItemCount() {
            return PasscodeActivity.this.rowCount;
        }

        @Override
        public int getItemViewType(int i) {
            if (i == PasscodeActivity.this.fingerprintRow || i == PasscodeActivity.this.captureRow) {
                return 0;
            }
            if (i == PasscodeActivity.this.changePasscodeRow || i == PasscodeActivity.this.autoLockRow || i == PasscodeActivity.this.disablePasscodeRow) {
                return 1;
            }
            if (i == PasscodeActivity.this.autoLockDetailRow || i == PasscodeActivity.this.captureDetailRow || i == PasscodeActivity.this.hintRow) {
                return 2;
            }
            if (i == PasscodeActivity.this.captureHeaderRow) {
                return 3;
            }
            return i == PasscodeActivity.this.utyanRow ? 4 : 0;
        }

        @Override
        public boolean isEnabled(RecyclerView.ViewHolder viewHolder) {
            int adapterPosition = viewHolder.getAdapterPosition();
            return adapterPosition == PasscodeActivity.this.fingerprintRow || adapterPosition == PasscodeActivity.this.autoLockRow || adapterPosition == PasscodeActivity.this.captureRow || adapterPosition == PasscodeActivity.this.changePasscodeRow || adapterPosition == PasscodeActivity.this.disablePasscodeRow;
        }

        @Override
        public void onBindViewHolder(androidx.recyclerview.widget.RecyclerView.ViewHolder r7, int r8) {
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.PasscodeActivity.ListAdapter.onBindViewHolder(androidx.recyclerview.widget.RecyclerView$ViewHolder, int):void");
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View textCheckCell;
            if (i == 0) {
                textCheckCell = new TextCheckCell(this.mContext);
            } else if (i == 1) {
                textCheckCell = new TextSettingsCell(this.mContext);
            } else {
                if (i != 3) {
                    textCheckCell = i != 4 ? new TextInfoPrivacyCell(this.mContext) : new RLottieImageHolderView(this.mContext);
                    return new RecyclerListView.Holder(textCheckCell);
                }
                textCheckCell = new HeaderCell(this.mContext);
            }
            textCheckCell.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
            return new RecyclerListView.Holder(textCheckCell);
        }
    }

    public static final class RLottieImageHolderView extends FrameLayout {
        private RLottieImageView imageView;

        private RLottieImageHolderView(Context context) {
            super(context);
            RLottieImageView rLottieImageView = new RLottieImageView(context);
            this.imageView = rLottieImageView;
            rLottieImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public final void onClick(View view) {
                    PasscodeActivity.RLottieImageHolderView.this.lambda$new$0(view);
                }
            });
            int dp = AndroidUtilities.dp(120.0f);
            FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(dp, dp);
            layoutParams.gravity = 1;
            addView(this.imageView, layoutParams);
            setPadding(0, AndroidUtilities.dp(32.0f), 0, 0);
            setLayoutParams(new RecyclerView.LayoutParams(-1, -2));
        }

        public void lambda$new$0(View view) {
            if (this.imageView.getAnimatedDrawable().isRunning()) {
                return;
            }
            this.imageView.getAnimatedDrawable().setCurrentFrame(0, false);
            this.imageView.playAnimation();
        }
    }

    public PasscodeActivity(int i) {
        this.type = i;
    }

    private void animateSuccessAnimation(final Runnable runnable) {
        if (!isPinCode()) {
            runnable.run();
            return;
        }
        int i = 0;
        while (true) {
            CodeFieldContainer codeFieldContainer = this.codeFieldContainer;
            CodeNumberField[] codeNumberFieldArr = codeFieldContainer.codeField;
            if (i >= codeNumberFieldArr.length) {
                codeFieldContainer.postDelayed(new Runnable() {
                    @Override
                    public final void run() {
                        PasscodeActivity.this.lambda$animateSuccessAnimation$16(runnable);
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

    public static BaseFragment determineOpenFragment() {
        return SharedConfig.passcodeHash.length() != 0 ? new PasscodeActivity(2) : new ActionIntroActivity(6);
    }

    public boolean isCustomKeyboardVisible() {
        if (isPinCode() && this.type != 0 && !AndroidUtilities.isTablet()) {
            Point point = AndroidUtilities.displaySize;
            if (point.x < point.y && !AndroidUtilities.isAccessibilityTouchExplorationEnabled()) {
                return true;
            }
        }
        return false;
    }

    private boolean isPassword() {
        int i = this.type;
        if (i == 1 && this.currentPasswordType == 1) {
            return true;
        }
        return i == 2 && SharedConfig.passcodeType == 1;
    }

    public boolean isPinCode() {
        int i = this.type;
        if (i == 1 && this.currentPasswordType == 0) {
            return true;
        }
        return i == 2 && SharedConfig.passcodeType == 0;
    }

    public void lambda$animateSuccessAnimation$16(Runnable runnable) {
        for (CodeNumberField codeNumberField : this.codeFieldContainer.codeField) {
            codeNumberField.animateSuccessProgress(0.0f);
        }
        runnable.run();
    }

    public void lambda$createView$1(int i, boolean z) {
        Runnable runnable;
        if (i < AndroidUtilities.dp(20.0f) || (runnable = this.onShowKeyboardCallback) == null) {
            return;
        }
        runnable.run();
        this.onShowKeyboardCallback = null;
    }

    public boolean lambda$createView$10(TextView textView, int i, KeyEvent keyEvent) {
        int i2 = this.passcodeSetStep;
        if (i2 == 0) {
            processNext();
            return true;
        }
        if (i2 != 1) {
            return false;
        }
        processDone();
        return true;
    }

    public void lambda$createView$11(CodeNumberField codeNumberField, View view, boolean z) {
        this.keyboardView.setEditText(codeNumberField);
        this.keyboardView.setDispatchBackWhenEmpty(true);
    }

    public void lambda$createView$12(View view) {
        int i = this.type;
        if (i == 1) {
            if (this.passcodeSetStep == 0) {
                processNext();
                return;
            }
        } else if (i != 2) {
            return;
        }
        processDone();
    }

    public void lambda$createView$2(AlertDialog alertDialog, int i) {
        SharedConfig.passcodeHash = "";
        SharedConfig.appLocked = false;
        SharedConfig.saveConfig();
        getMediaDataController().buildShortcuts();
        int childCount = this.listView.getChildCount();
        int i2 = 0;
        while (true) {
            if (i2 >= childCount) {
                break;
            }
            View childAt = this.listView.getChildAt(i2);
            if (childAt instanceof TextSettingsCell) {
                ((TextSettingsCell) childAt).setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteGrayText7));
                break;
            }
            i2++;
        }
        NotificationCenter.getGlobalInstance().lambda$postNotificationNameOnUIThread$1(NotificationCenter.didSetPasscode, new Object[0]);
        lambda$onBackPressed$323();
    }

    public static String lambda$createView$3(int i) {
        return i == 0 ? LocaleController.getString(R.string.AutoLockDisabled) : i == 1 ? LocaleController.formatString("AutoLockInTime", R.string.AutoLockInTime, LocaleController.formatPluralString("Minutes", 1, new Object[0])) : i == 2 ? LocaleController.formatString("AutoLockInTime", R.string.AutoLockInTime, LocaleController.formatPluralString("Minutes", 5, new Object[0])) : i == 3 ? LocaleController.formatString("AutoLockInTime", R.string.AutoLockInTime, LocaleController.formatPluralString("Hours", 1, new Object[0])) : i == 4 ? LocaleController.formatString("AutoLockInTime", R.string.AutoLockInTime, LocaleController.formatPluralString("Hours", 5, new Object[0])) : "";
    }

    public void lambda$createView$4(NumberPicker numberPicker, int i, AlertDialog alertDialog, int i2) {
        int i3;
        int value = numberPicker.getValue();
        if (value == 0) {
            SharedConfig.autoLockIn = 0;
        } else {
            if (value == 1) {
                i3 = 60;
            } else if (value == 2) {
                i3 = 300;
            } else if (value == 3) {
                i3 = 3600;
            } else if (value == 4) {
                i3 = 18000;
            }
            SharedConfig.autoLockIn = i3;
        }
        this.listAdapter.notifyItemChanged(i);
        UserConfig.getInstance(this.currentAccount).saveConfig(false);
    }

    public void lambda$createView$5(View view, final int i) {
        int i2 = 0;
        if (view.isEnabled()) {
            if (i == this.disablePasscodeRow) {
                AlertDialog create = new AlertDialog.Builder(getParentActivity()).setTitle(LocaleController.getString(R.string.DisablePasscode)).setMessage(LocaleController.getString(R.string.DisablePasscodeConfirmMessage)).setNegativeButton(LocaleController.getString(R.string.Cancel), null).setPositiveButton(LocaleController.getString(R.string.DisablePasscodeTurnOff), new AlertDialog.OnButtonClickListener() {
                    @Override
                    public final void onClick(AlertDialog alertDialog, int i3) {
                        PasscodeActivity.this.lambda$createView$2(alertDialog, i3);
                    }
                }).create();
                create.show();
                ((TextView) create.getButton(-1)).setTextColor(Theme.getColor(Theme.key_text_RedBold));
                return;
            }
            if (i == this.changePasscodeRow) {
                presentFragment(new PasscodeActivity(1));
                return;
            }
            if (i != this.autoLockRow) {
                if (i == this.fingerprintRow) {
                    SharedConfig.useFingerprintLock = !SharedConfig.useFingerprintLock;
                    UserConfig.getInstance(this.currentAccount).saveConfig(false);
                    ((TextCheckCell) view).setChecked(SharedConfig.useFingerprintLock);
                    return;
                } else {
                    if (i == this.captureRow) {
                        SharedConfig.allowScreenCapture = !SharedConfig.allowScreenCapture;
                        UserConfig.getInstance(this.currentAccount).saveConfig(false);
                        ((TextCheckCell) view).setChecked(SharedConfig.allowScreenCapture);
                        NotificationCenter.getGlobalInstance().lambda$postNotificationNameOnUIThread$1(NotificationCenter.didSetPasscode, Boolean.FALSE);
                        if (SharedConfig.allowScreenCapture) {
                            return;
                        }
                        AlertsCreator.showSimpleAlert(this, LocaleController.getString(R.string.ScreenCaptureAlert));
                        return;
                    }
                    return;
                }
            }
            if (getParentActivity() == null) {
                return;
            }
            AlertDialog.Builder builder = new AlertDialog.Builder(getParentActivity());
            builder.setTitle(LocaleController.getString(R.string.AutoLock));
            final NumberPicker numberPicker = new NumberPicker(getParentActivity());
            numberPicker.setMinValue(0);
            numberPicker.setMaxValue(4);
            int i3 = SharedConfig.autoLockIn;
            if (i3 != 0) {
                if (i3 == 60) {
                    numberPicker.setValue(1);
                } else if (i3 == 300) {
                    i2 = 2;
                } else if (i3 == 3600) {
                    i2 = 3;
                } else if (i3 == 18000) {
                    numberPicker.setValue(4);
                }
                numberPicker.setFormatter(new NumberPicker.Formatter() {
                    @Override
                    public final String format(int i4) {
                        String lambda$createView$3;
                        lambda$createView$3 = PasscodeActivity.lambda$createView$3(i4);
                        return lambda$createView$3;
                    }
                });
                builder.setView(numberPicker);
                builder.setNegativeButton(LocaleController.getString(R.string.Done), new AlertDialog.OnButtonClickListener() {
                    @Override
                    public final void onClick(AlertDialog alertDialog, int i4) {
                        PasscodeActivity.this.lambda$createView$4(numberPicker, i, alertDialog, i4);
                    }
                });
                showDialog(builder.create());
            }
            numberPicker.setValue(i2);
            numberPicker.setFormatter(new NumberPicker.Formatter() {
                @Override
                public final String format(int i4) {
                    String lambda$createView$3;
                    lambda$createView$3 = PasscodeActivity.lambda$createView$3(i4);
                    return lambda$createView$3;
                }
            });
            builder.setView(numberPicker);
            builder.setNegativeButton(LocaleController.getString(R.string.Done), new AlertDialog.OnButtonClickListener() {
                @Override
                public final void onClick(AlertDialog alertDialog, int i4) {
                    PasscodeActivity.this.lambda$createView$4(numberPicker, i, alertDialog, i4);
                }
            });
            showDialog(builder.create());
        }
    }

    public static View lambda$createView$6(Context context) {
        TextView textView = new TextView(context);
        textView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteGrayText6));
        textView.setGravity(1);
        textView.setLineSpacing(AndroidUtilities.dp(2.0f), 1.0f);
        textView.setTextSize(1, 15.0f);
        return textView;
    }

    public static void lambda$createView$7(Context context, View view) {
        AlertsCreator.createForgotPasscodeDialog(context).show();
    }

    public void lambda$createView$8(View view, boolean z) {
        this.outlinePasswordView.animateSelection(z ? 1.0f : 0.0f);
    }

    public void lambda$createView$9(AtomicBoolean atomicBoolean, View view) {
        atomicBoolean.set(!atomicBoolean.get());
        int selectionStart = this.passwordEditText.getSelectionStart();
        int selectionEnd = this.passwordEditText.getSelectionEnd();
        this.passwordEditText.setInputType((atomicBoolean.get() ? 144 : 128) | 1);
        this.passwordEditText.setSelection(selectionStart, selectionEnd);
        this.passwordButton.setColorFilter(Theme.getColor(atomicBoolean.get() ? Theme.key_windowBackgroundWhiteInputFieldActivated : Theme.key_windowBackgroundWhiteHintText));
    }

    public void lambda$new$0() {
        this.postedHidePasscodesDoNotMatch = false;
        AndroidUtilities.updateViewVisibilityAnimated(this.passcodesDoNotMatchTextView, false);
    }

    public void lambda$onPasscodeError$21() {
        if (!isPinCode()) {
            this.outlinePasswordView.animateError(0.0f);
            return;
        }
        for (CodeNumberField codeNumberField : this.codeFieldContainer.codeField) {
            codeNumberField.animateErrorProgress(0.0f);
        }
    }

    public void lambda$onPasscodeError$22() {
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                PasscodeActivity.this.lambda$onPasscodeError$21();
            }
        }, isPinCode() ? 150L : 1000L);
    }

    public void lambda$processDone$18() {
        this.codeFieldContainer.postDelayed(this.hidePasscodesDoNotMatch, 3000L);
        this.postedHidePasscodesDoNotMatch = true;
    }

    public void lambda$processDone$19(boolean z) {
        getMediaDataController().buildShortcuts();
        if (z) {
            presentFragment(new PasscodeActivity(0), true);
        } else {
            lambda$onBackPressed$323();
        }
        NotificationCenter.getGlobalInstance().lambda$postNotificationNameOnUIThread$1(NotificationCenter.didSetPasscode, new Object[0]);
    }

    public void lambda$processDone$20() {
        presentFragment(new PasscodeActivity(0), true);
    }

    public void lambda$setCustomKeyboardVisible$13(ValueAnimator valueAnimator) {
        float floatValue = ((Float) valueAnimator.getAnimatedValue()).floatValue();
        this.keyboardView.setAlpha(floatValue);
        this.keyboardView.setTranslationY((1.0f - floatValue) * AndroidUtilities.dp(230.0f) * 0.75f);
        this.fragmentView.requestLayout();
    }

    public void lambda$setFloatingButtonVisible$14(ValueAnimator valueAnimator) {
        float floatValue = ((Float) valueAnimator.getAnimatedValue()).floatValue();
        this.floatingAutoAnimator.setOffsetY(AndroidUtilities.dp(70.0f) * (1.0f - floatValue));
        this.floatingButtonContainer.setAlpha(floatValue);
    }

    public void lambda$updateFields$17(boolean z, boolean z2) {
        setFloatingButtonVisible(z, z2);
        AndroidUtilities.cancelRunOnUIThread(this.onShowKeyboardCallback);
    }

    private void onPasscodeError() {
        if (getParentActivity() == null) {
            return;
        }
        try {
            this.fragmentView.performHapticFeedback(3, 2);
        } catch (Exception unused) {
        }
        if (isPinCode()) {
            for (CodeNumberField codeNumberField : this.codeFieldContainer.codeField) {
                codeNumberField.animateErrorProgress(1.0f);
            }
        } else {
            this.outlinePasswordView.animateError(1.0f);
        }
        AndroidUtilities.shakeViewSpring(isPinCode() ? this.codeFieldContainer : this.outlinePasswordView, isPinCode() ? 10.0f : 4.0f, new Runnable() {
            @Override
            public final void run() {
                PasscodeActivity.this.lambda$onPasscodeError$22();
            }
        });
    }

    public void processDone() {
        Runnable runnable;
        int i = 0;
        if (isPassword() && this.passwordEditText.getText().length() == 0) {
            onPasscodeError();
            return;
        }
        String code = isPinCode() ? this.codeFieldContainer.getCode() : this.passwordEditText.getText().toString();
        int i2 = this.type;
        if (i2 == 1) {
            if (!this.firstPassword.equals(code)) {
                AndroidUtilities.updateViewVisibilityAnimated(this.passcodesDoNotMatchTextView, true);
                for (CodeNumberField codeNumberField : this.codeFieldContainer.codeField) {
                    codeNumberField.setText("");
                }
                if (isPinCode()) {
                    this.codeFieldContainer.codeField[0].requestFocus();
                }
                this.passwordEditText.setText("");
                onPasscodeError();
                this.codeFieldContainer.removeCallbacks(this.hidePasscodesDoNotMatch);
                this.codeFieldContainer.post(new Runnable() {
                    @Override
                    public final void run() {
                        PasscodeActivity.this.lambda$processDone$18();
                    }
                });
                return;
            }
            final boolean z = SharedConfig.passcodeHash.length() == 0;
            try {
                SharedConfig.passcodeSalt = new byte[16];
                Utilities.random.nextBytes(SharedConfig.passcodeSalt);
                byte[] bytes = this.firstPassword.getBytes("UTF-8");
                int length = bytes.length + 32;
                byte[] bArr = new byte[length];
                System.arraycopy(SharedConfig.passcodeSalt, 0, bArr, 0, 16);
                System.arraycopy(bytes, 0, bArr, 16, bytes.length);
                System.arraycopy(SharedConfig.passcodeSalt, 0, bArr, bytes.length + 16, 16);
                SharedConfig.passcodeHash = Utilities.bytesToHex(Utilities.computeSHA256(bArr, 0, length));
            } catch (Exception e) {
                FileLog.e(e);
            }
            SharedConfig.allowScreenCapture = true;
            SharedConfig.passcodeType = this.currentPasswordType;
            SharedConfig.saveConfig();
            this.passwordEditText.clearFocus();
            AndroidUtilities.hideKeyboard(this.passwordEditText);
            CodeNumberField[] codeNumberFieldArr = this.codeFieldContainer.codeField;
            int length2 = codeNumberFieldArr.length;
            while (i < length2) {
                CodeNumberField codeNumberField2 = codeNumberFieldArr[i];
                codeNumberField2.clearFocus();
                AndroidUtilities.hideKeyboard(codeNumberField2);
                i++;
            }
            this.keyboardView.setEditText(null);
            runnable = new Runnable() {
                @Override
                public final void run() {
                    PasscodeActivity.this.lambda$processDone$19(z);
                }
            };
        } else {
            if (i2 != 2) {
                return;
            }
            long j = SharedConfig.passcodeRetryInMs;
            if (j > 0) {
                double d = j;
                Double.isNaN(d);
                Toast.makeText(getParentActivity(), LocaleController.formatString("TooManyTries", R.string.TooManyTries, LocaleController.formatPluralString("Seconds", Math.max(1, (int) Math.ceil(d / 1000.0d)), new Object[0])), 0).show();
                for (CodeNumberField codeNumberField3 : this.codeFieldContainer.codeField) {
                    codeNumberField3.setText("");
                }
                this.passwordEditText.setText("");
                if (isPinCode()) {
                    this.codeFieldContainer.codeField[0].requestFocus();
                }
                onPasscodeError();
                return;
            }
            if (!SharedConfig.checkPasscode(code)) {
                SharedConfig.increaseBadPasscodeTries();
                this.passwordEditText.setText("");
                for (CodeNumberField codeNumberField4 : this.codeFieldContainer.codeField) {
                    codeNumberField4.setText("");
                }
                if (isPinCode()) {
                    this.codeFieldContainer.codeField[0].requestFocus();
                }
                onPasscodeError();
                return;
            }
            SharedConfig.badPasscodeTries = 0;
            SharedConfig.saveConfig();
            this.passwordEditText.clearFocus();
            AndroidUtilities.hideKeyboard(this.passwordEditText);
            CodeNumberField[] codeNumberFieldArr2 = this.codeFieldContainer.codeField;
            int length3 = codeNumberFieldArr2.length;
            while (i < length3) {
                CodeNumberField codeNumberField5 = codeNumberFieldArr2[i];
                codeNumberField5.clearFocus();
                AndroidUtilities.hideKeyboard(codeNumberField5);
                i++;
            }
            this.keyboardView.setEditText(null);
            runnable = new Runnable() {
                @Override
                public final void run() {
                    PasscodeActivity.this.lambda$processDone$20();
                }
            };
        }
        animateSuccessAnimation(runnable);
    }

    public void processNext() {
        if ((this.currentPasswordType == 1 && this.passwordEditText.getText().length() == 0) || (this.currentPasswordType == 0 && this.codeFieldContainer.getCode().length() != 4)) {
            onPasscodeError();
            return;
        }
        ActionBarMenuItem actionBarMenuItem = this.otherItem;
        if (actionBarMenuItem != null) {
            actionBarMenuItem.setVisibility(8);
        }
        this.titleTextView.setText(LocaleController.getString(R.string.ConfirmCreatePasscode));
        this.descriptionTextSwitcher.setText(AndroidUtilities.replaceTags(LocaleController.getString(R.string.PasscodeReinstallNotice)));
        this.firstPassword = isPinCode() ? this.codeFieldContainer.getCode() : this.passwordEditText.getText().toString();
        this.passwordEditText.setText("");
        this.passwordEditText.setInputType(524417);
        for (CodeNumberField codeNumberField : this.codeFieldContainer.codeField) {
            codeNumberField.setText("");
        }
        showKeyboard();
        this.passcodeSetStep = 1;
    }

    private void setCustomKeyboardVisible(final boolean z, boolean z2) {
        if (z) {
            AndroidUtilities.hideKeyboard(this.fragmentView);
            AndroidUtilities.requestAltFocusable(getParentActivity(), this.classGuid);
        } else {
            AndroidUtilities.removeAltFocusable(getParentActivity(), this.classGuid);
        }
        if (!z2) {
            this.keyboardView.setVisibility(z ? 0 : 8);
            this.keyboardView.setAlpha(z ? 1.0f : 0.0f);
            this.keyboardView.setTranslationY(z ? 0.0f : AndroidUtilities.dp(230.0f));
            this.fragmentView.requestLayout();
            return;
        }
        ValueAnimator duration = ValueAnimator.ofFloat(z ? 0.0f : 1.0f, z ? 1.0f : 0.0f).setDuration(150L);
        duration.setInterpolator(z ? CubicBezierInterpolator.DEFAULT : Easings.easeInOutQuad);
        duration.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                PasscodeActivity.this.lambda$setCustomKeyboardVisible$13(valueAnimator);
            }
        });
        duration.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animator) {
                if (z) {
                    return;
                }
                PasscodeActivity.this.keyboardView.setVisibility(8);
            }

            @Override
            public void onAnimationStart(Animator animator) {
                if (z) {
                    PasscodeActivity.this.keyboardView.setVisibility(0);
                }
            }
        });
        duration.start();
    }

    private void setFloatingButtonVisible(final boolean z, boolean z2) {
        Animator animator = this.floatingButtonAnimator;
        if (animator != null) {
            animator.cancel();
            this.floatingButtonAnimator = null;
        }
        if (!z2) {
            this.floatingAutoAnimator.setOffsetY(z ? 0.0f : AndroidUtilities.dp(70.0f));
            this.floatingButtonContainer.setAlpha(z ? 1.0f : 0.0f);
            this.floatingButtonContainer.setVisibility(z ? 0 : 8);
        } else {
            ValueAnimator duration = ValueAnimator.ofFloat(z ? 0.0f : 1.0f, z ? 1.0f : 0.0f).setDuration(150L);
            duration.setInterpolator(z ? AndroidUtilities.decelerateInterpolator : AndroidUtilities.accelerateInterpolator);
            duration.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                    PasscodeActivity.this.lambda$setFloatingButtonVisible$14(valueAnimator);
                }
            });
            duration.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animator2) {
                    if (!z) {
                        PasscodeActivity.this.floatingButtonContainer.setVisibility(8);
                    }
                    if (PasscodeActivity.this.floatingButtonAnimator == animator2) {
                        PasscodeActivity.this.floatingButtonAnimator = null;
                    }
                }

                @Override
                public void onAnimationStart(Animator animator2) {
                    if (z) {
                        PasscodeActivity.this.floatingButtonContainer.setVisibility(0);
                    }
                }
            });
            duration.start();
            this.floatingButtonAnimator = duration;
        }
    }

    public void showKeyboard() {
        EditTextBoldCursor editTextBoldCursor;
        if (isPinCode()) {
            this.codeFieldContainer.codeField[0].requestFocus();
            if (isCustomKeyboardVisible()) {
                return;
            } else {
                editTextBoldCursor = this.codeFieldContainer.codeField[0];
            }
        } else {
            if (!isPassword()) {
                return;
            }
            this.passwordEditText.requestFocus();
            editTextBoldCursor = this.passwordEditText;
        }
        AndroidUtilities.showKeyboard(editTextBoldCursor);
    }

    public void updateFields() {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.PasscodeActivity.updateFields():void");
    }

    private void updateRows() {
        this.fingerprintRow = -1;
        this.utyanRow = 0;
        this.hintRow = 1;
        this.rowCount = 3;
        this.changePasscodeRow = 2;
        try {
            if (Build.VERSION.SDK_INT >= 23 && BiometricManager.from(ApplicationLoader.applicationContext).canAuthenticate(15) == 0 && AndroidUtilities.isKeyguardSecure()) {
                int i = this.rowCount;
                this.rowCount = i + 1;
                this.fingerprintRow = i;
            }
        } catch (Throwable th) {
            FileLog.e(th);
        }
        int i2 = this.rowCount;
        this.autoLockRow = i2;
        this.autoLockDetailRow = i2 + 1;
        this.captureHeaderRow = i2 + 2;
        this.captureRow = i2 + 3;
        this.captureDetailRow = i2 + 4;
        this.rowCount = i2 + 6;
        this.disablePasscodeRow = i2 + 5;
    }

    @Override
    public android.view.View createView(final android.content.Context r31) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.PasscodeActivity.createView(android.content.Context):android.view.View");
    }

    @Override
    public void didReceivedNotification(int i, int i2, Object... objArr) {
        if (i == NotificationCenter.didSetPasscode) {
            if ((objArr.length == 0 || ((Boolean) objArr[0]).booleanValue()) && this.type == 0) {
                updateRows();
                ListAdapter listAdapter = this.listAdapter;
                if (listAdapter != null) {
                    listAdapter.notifyDataSetChanged();
                }
            }
        }
    }

    @Override
    public ArrayList getThemeDescriptions() {
        ArrayList arrayList = new ArrayList();
        int i = Theme.key_windowBackgroundWhite;
        arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_CELLBACKGROUNDCOLOR, new Class[]{TextCheckCell.class, TextSettingsCell.class}, null, null, null, i));
        arrayList.add(new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_BACKGROUND | ThemeDescription.FLAG_CHECKTAG, null, null, null, null, i));
        arrayList.add(new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_BACKGROUND | ThemeDescription.FLAG_CHECKTAG, null, null, null, null, Theme.key_windowBackgroundGray));
        ActionBar actionBar = this.actionBar;
        int i2 = ThemeDescription.FLAG_BACKGROUND;
        int i3 = Theme.key_actionBarDefault;
        arrayList.add(new ThemeDescription(actionBar, i2, null, null, null, null, i3));
        arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_LISTGLOWCOLOR, null, null, null, null, i3));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, null, null, null, null, Theme.key_actionBarDefaultIcon));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, null, null, null, null, Theme.key_actionBarDefaultTitle));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, null, null, null, null, Theme.key_actionBarDefaultSelector));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SUBMENUBACKGROUND, null, null, null, null, Theme.key_actionBarDefaultSubmenuBackground));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SUBMENUITEM, null, null, null, null, Theme.key_actionBarDefaultSubmenuItem));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SUBMENUITEM | ThemeDescription.FLAG_IMAGECOLOR, null, null, null, null, Theme.key_actionBarDefaultSubmenuItemIcon));
        arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_SELECTOR, null, null, null, null, Theme.key_listSelector));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{View.class}, Theme.dividerPaint, null, null, Theme.key_divider));
        arrayList.add(new ThemeDescription(this.titleTextView, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, Theme.key_windowBackgroundWhiteGrayText6));
        EditTextBoldCursor editTextBoldCursor = this.passwordEditText;
        int i4 = ThemeDescription.FLAG_TEXTCOLOR;
        int i5 = Theme.key_windowBackgroundWhiteBlackText;
        arrayList.add(new ThemeDescription(editTextBoldCursor, i4, null, null, null, null, i5));
        arrayList.add(new ThemeDescription(this.passwordEditText, ThemeDescription.FLAG_BACKGROUNDFILTER, null, null, null, null, Theme.key_windowBackgroundWhiteInputField));
        arrayList.add(new ThemeDescription(this.passwordEditText, ThemeDescription.FLAG_BACKGROUNDFILTER | ThemeDescription.FLAG_DRAWABLESELECTEDSTATE, null, null, null, null, Theme.key_windowBackgroundWhiteInputFieldActivated));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{TextCheckCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, i5));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{TextCheckCell.class}, new String[]{"checkBox"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_switchTrack));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{TextCheckCell.class}, new String[]{"checkBox"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_switchTrackChecked));
        arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_CHECKTAG, new Class[]{TextSettingsCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, i5));
        arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_CHECKTAG, new Class[]{TextSettingsCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteGrayText7));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{TextSettingsCell.class}, new String[]{"valueTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteValueText));
        arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{TextInfoPrivacyCell.class}, null, null, null, Theme.key_windowBackgroundGrayShadow));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{TextInfoPrivacyCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteGrayText4));
        return arrayList;
    }

    @Override
    public boolean hasForceLightStatusBar() {
        return this.type != 0;
    }

    @Override
    public void onConfigurationChanged(Configuration configuration) {
        CodeNumberField[] codeNumberFieldArr;
        int i;
        super.onConfigurationChanged(configuration);
        setCustomKeyboardVisible(isCustomKeyboardVisible(), false);
        RLottieImageView rLottieImageView = this.lockImageView;
        if (rLottieImageView != null) {
            if (!AndroidUtilities.isSmallScreen()) {
                Point point = AndroidUtilities.displaySize;
                if (point.x < point.y) {
                    i = 0;
                    rLottieImageView.setVisibility(i);
                }
            }
            i = 8;
            rLottieImageView.setVisibility(i);
        }
        CodeFieldContainer codeFieldContainer = this.codeFieldContainer;
        if (codeFieldContainer == null || (codeNumberFieldArr = codeFieldContainer.codeField) == null) {
            return;
        }
        for (CodeNumberField codeNumberField : codeNumberFieldArr) {
            codeNumberField.setShowSoftInputOnFocusCompat(!isCustomKeyboardVisible());
        }
    }

    @Override
    public boolean onFragmentCreate() {
        super.onFragmentCreate();
        updateRows();
        if (this.type != 0) {
            return true;
        }
        NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.didSetPasscode);
        return true;
    }

    @Override
    public void onFragmentDestroy() {
        super.onFragmentDestroy();
        if (this.type == 0) {
            NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.didSetPasscode);
        }
        AndroidUtilities.removeAdjustResize(getParentActivity(), this.classGuid);
    }

    @Override
    public void onPause() {
        super.onPause();
        AndroidUtilities.removeAltFocusable(getParentActivity(), this.classGuid);
    }

    @Override
    public void onResume() {
        super.onResume();
        ListAdapter listAdapter = this.listAdapter;
        if (listAdapter != null) {
            listAdapter.notifyDataSetChanged();
        }
        if (this.type != 0 && !isCustomKeyboardVisible()) {
            AndroidUtilities.runOnUIThread(new Runnable() {
                @Override
                public final void run() {
                    PasscodeActivity.this.showKeyboard();
                }
            }, 200L);
        }
        AndroidUtilities.requestAdjustResize(getParentActivity(), this.classGuid);
        if (isCustomKeyboardVisible()) {
            AndroidUtilities.hideKeyboard(this.fragmentView);
            AndroidUtilities.requestAltFocusable(getParentActivity(), this.classGuid);
        }
    }

    @Override
    public void onTransitionAnimationEnd(boolean z, boolean z2) {
        if (!z || this.type == 0) {
            return;
        }
        showKeyboard();
    }
}
