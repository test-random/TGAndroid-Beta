package org.telegram.ui;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Property;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import androidx.core.graphics.ColorUtils;
import androidx.recyclerview.widget.RecyclerView;
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
import org.telegram.ui.Cells.EditTextSettingsCell;
import org.telegram.ui.Cells.TextInfoPrivacyCell;
import org.telegram.ui.Cells.TextSettingsCell;
import org.telegram.ui.Components.AlertsCreator;
import org.telegram.ui.Components.CubicBezierInterpolator;
import org.telegram.ui.Components.EditTextBoldCursor;
import org.telegram.ui.Components.EmptyTextProgressView;
import org.telegram.ui.Components.OutlineTextContainerView;
import org.telegram.ui.Components.RLottieImageView;
import org.telegram.ui.Components.RadialProgressView;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.Components.TransformableLoginButtonView;

public class TwoStepVerificationActivity extends BaseFragment implements NotificationCenter.NotificationCenterDelegate {
    private SimpleTextView bottomButton;
    private TextView bottomTextView;
    private TextView cancelResetButton;
    private int changePasswordRow;
    private int changeRecoveryEmailRow;
    private TLRPC.account_Password currentPassword;
    private byte[] currentSecret;
    private long currentSecretId;
    private TwoStepVerificationActivityDelegate delegate;
    private int delegateType;
    private boolean destroyed;
    private EmptyTextProgressView emptyView;
    private FrameLayout floatingButtonContainer;
    private TransformableLoginButtonView floatingButtonIcon;
    private boolean forgotPasswordOnShow;
    private ListAdapter listAdapter;
    private RecyclerListView listView;
    private boolean loading;
    private RLottieImageView lockImageView;
    private EditTextBoldCursor passwordEditText;
    private int passwordEnabledDetailRow;
    private OutlineTextContainerView passwordOutlineView;
    private boolean paused;
    private boolean postedErrorColorTimeout;
    public boolean preloaded;
    private AlertDialog progressDialog;
    private RadialProgressView radialProgressView;
    private boolean resetPasswordOnShow;
    private TextView resetWaitView;
    private int rowCount;
    private ScrollView scrollView;
    private int setPasswordDetailRow;
    private int setPasswordRow;
    private int setRecoveryEmailRow;
    private TextView subtitleTextView;
    private TextView titleTextView;
    private int turnPasswordOffRow;
    private boolean passwordEntered = true;
    private byte[] currentPasswordHash = new byte[0];
    private Runnable errorColorTimeout = new Runnable() {
        @Override
        public final void run() {
            TwoStepVerificationActivity.this.lambda$new$0();
        }
    };
    int otherwiseReloginDays = -1;
    private Runnable updateTimeRunnable = new Runnable() {
        @Override
        public final void run() {
            TwoStepVerificationActivity.this.updateBottomButton();
        }
    };

    public class ListAdapter extends RecyclerListView.SelectionAdapter {
        private Context mContext;

        public ListAdapter(Context context) {
            this.mContext = context;
        }

        @Override
        public int getItemCount() {
            if (TwoStepVerificationActivity.this.loading || TwoStepVerificationActivity.this.currentPassword == null) {
                return 0;
            }
            return TwoStepVerificationActivity.this.rowCount;
        }

        @Override
        public int getItemViewType(int i) {
            return (i == TwoStepVerificationActivity.this.setPasswordDetailRow || i == TwoStepVerificationActivity.this.passwordEnabledDetailRow) ? 1 : 0;
        }

        @Override
        public boolean isEnabled(RecyclerView.ViewHolder viewHolder) {
            return viewHolder.getItemViewType() == 0;
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
            int i2;
            int i3;
            int itemViewType = viewHolder.getItemViewType();
            boolean z = true;
            if (itemViewType != 0) {
                if (itemViewType != 1) {
                    return;
                }
                TextInfoPrivacyCell textInfoPrivacyCell = (TextInfoPrivacyCell) viewHolder.itemView;
                if (i == TwoStepVerificationActivity.this.setPasswordDetailRow) {
                    i3 = R.string.SetAdditionalPasswordInfo;
                } else if (i != TwoStepVerificationActivity.this.passwordEnabledDetailRow) {
                    return;
                } else {
                    i3 = R.string.EnabledPasswordText;
                }
                textInfoPrivacyCell.setText(LocaleController.getString(i3));
                textInfoPrivacyCell.setBackgroundDrawable(Theme.getThemedDrawableByKey(this.mContext, R.drawable.greydivider_bottom, Theme.key_windowBackgroundGrayShadow));
                return;
            }
            TextSettingsCell textSettingsCell = (TextSettingsCell) viewHolder.itemView;
            int i4 = Theme.key_windowBackgroundWhiteBlackText;
            textSettingsCell.setTag(Integer.valueOf(i4));
            textSettingsCell.setTextColor(Theme.getColor(i4));
            if (i == TwoStepVerificationActivity.this.changePasswordRow) {
                i2 = R.string.ChangePassword;
            } else if (i == TwoStepVerificationActivity.this.setPasswordRow) {
                i2 = R.string.SetAdditionalPassword;
            } else if (i == TwoStepVerificationActivity.this.turnPasswordOffRow) {
                i2 = R.string.TurnPasswordOff;
            } else {
                z = false;
                if (i == TwoStepVerificationActivity.this.changeRecoveryEmailRow) {
                    i2 = R.string.ChangeRecoveryEmail;
                } else if (i != TwoStepVerificationActivity.this.setRecoveryEmailRow) {
                    return;
                } else {
                    i2 = R.string.SetRecoveryEmail;
                }
            }
            textSettingsCell.setText(LocaleController.getString(i2), z);
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View textSettingsCell;
            if (i != 0) {
                textSettingsCell = new TextInfoPrivacyCell(this.mContext);
            } else {
                textSettingsCell = new TextSettingsCell(this.mContext);
                textSettingsCell.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
            }
            return new RecyclerListView.Holder(textSettingsCell);
        }
    }

    public interface TwoStepVerificationActivityDelegate {
        void didEnterPassword(TLRPC.InputCheckPasswordSRP inputCheckPasswordSRP);
    }

    public static boolean canHandleCurrentPassword(TLRPC.account_Password account_password, boolean z) {
        return z ? !(account_password.current_algo instanceof TLRPC.TL_passwordKdfAlgoUnknown) : ((account_password.new_algo instanceof TLRPC.TL_passwordKdfAlgoUnknown) || (account_password.current_algo instanceof TLRPC.TL_passwordKdfAlgoUnknown) || (account_password.new_secure_algo instanceof TLRPC.TL_securePasswordKdfAlgoUnknown)) ? false : true;
    }

    private void cancelPasswordReset() {
        if (getParentActivity() == null) {
            return;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(getParentActivity());
        builder.setPositiveButton(LocaleController.getString(R.string.CancelPasswordResetYes), new DialogInterface.OnClickListener() {
            @Override
            public final void onClick(DialogInterface dialogInterface, int i) {
                TwoStepVerificationActivity.this.lambda$cancelPasswordReset$10(dialogInterface, i);
            }
        });
        builder.setNegativeButton(LocaleController.getString(R.string.CancelPasswordResetNo), null);
        builder.setTitle(LocaleController.getString(R.string.CancelReset));
        builder.setMessage(LocaleController.getString(R.string.CancelPasswordReset));
        showDialog(builder.create());
    }

    private boolean checkSecretValues(byte[] bArr, TLRPC.TL_account_passwordSettings tL_account_passwordSettings) {
        byte[] computeSHA512;
        TLRPC.TL_secureSecretSettings tL_secureSecretSettings = tL_account_passwordSettings.secure_settings;
        if (tL_secureSecretSettings != null) {
            this.currentSecret = tL_secureSecretSettings.secure_secret;
            TLRPC.SecurePasswordKdfAlgo securePasswordKdfAlgo = tL_secureSecretSettings.secure_algo;
            if (securePasswordKdfAlgo instanceof TLRPC.TL_securePasswordKdfAlgoPBKDF2HMACSHA512iter100000) {
                computeSHA512 = Utilities.computePBKDF2(bArr, ((TLRPC.TL_securePasswordKdfAlgoPBKDF2HMACSHA512iter100000) securePasswordKdfAlgo).salt);
            } else {
                if (!(securePasswordKdfAlgo instanceof TLRPC.TL_securePasswordKdfAlgoSHA512)) {
                    return false;
                }
                byte[] bArr2 = ((TLRPC.TL_securePasswordKdfAlgoSHA512) securePasswordKdfAlgo).salt;
                computeSHA512 = Utilities.computeSHA512(bArr2, bArr, bArr2);
            }
            this.currentSecretId = tL_account_passwordSettings.secure_settings.secure_secret_id;
            byte[] bArr3 = new byte[32];
            System.arraycopy(computeSHA512, 0, bArr3, 0, 32);
            byte[] bArr4 = new byte[16];
            System.arraycopy(computeSHA512, 32, bArr4, 0, 16);
            byte[] bArr5 = this.currentSecret;
            Utilities.aesCbcEncryptionByteArraySafe(bArr5, bArr3, bArr4, 0, bArr5.length, 0, 0);
            TLRPC.TL_secureSecretSettings tL_secureSecretSettings2 = tL_account_passwordSettings.secure_settings;
            if (PassportActivity.checkSecret(tL_secureSecretSettings2.secure_secret, Long.valueOf(tL_secureSecretSettings2.secure_secret_id))) {
                return true;
            }
            TLRPC.TL_account_updatePasswordSettings tL_account_updatePasswordSettings = new TLRPC.TL_account_updatePasswordSettings();
            tL_account_updatePasswordSettings.password = getNewSrpPassword();
            TLRPC.TL_account_passwordInputSettings tL_account_passwordInputSettings = new TLRPC.TL_account_passwordInputSettings();
            tL_account_updatePasswordSettings.new_settings = tL_account_passwordInputSettings;
            tL_account_passwordInputSettings.new_secure_settings = new TLRPC.TL_secureSecretSettings();
            TLRPC.TL_secureSecretSettings tL_secureSecretSettings3 = tL_account_updatePasswordSettings.new_settings.new_secure_settings;
            tL_secureSecretSettings3.secure_secret = new byte[0];
            tL_secureSecretSettings3.secure_algo = new TLRPC.TL_securePasswordKdfAlgoUnknown();
            TLRPC.TL_account_passwordInputSettings tL_account_passwordInputSettings2 = tL_account_updatePasswordSettings.new_settings;
            tL_account_passwordInputSettings2.new_secure_settings.secure_secret_id = 0L;
            tL_account_passwordInputSettings2.flags |= 4;
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(tL_account_updatePasswordSettings, new RequestDelegate() {
                @Override
                public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                    TwoStepVerificationActivity.lambda$checkSecretValues$28(tLObject, tL_error);
                }
            });
        }
        this.currentSecret = null;
        this.currentSecretId = 0L;
        return true;
    }

    private void clearPassword() {
        final TLRPC.TL_account_updatePasswordSettings tL_account_updatePasswordSettings = new TLRPC.TL_account_updatePasswordSettings();
        byte[] bArr = this.currentPasswordHash;
        if (bArr == null || bArr.length == 0) {
            tL_account_updatePasswordSettings.password = new TLRPC.TL_inputCheckPasswordEmpty();
        }
        tL_account_updatePasswordSettings.new_settings = new TLRPC.TL_account_passwordInputSettings();
        UserConfig.getInstance(this.currentAccount).resetSavedPassword();
        this.currentSecret = null;
        TLRPC.TL_account_passwordInputSettings tL_account_passwordInputSettings = tL_account_updatePasswordSettings.new_settings;
        tL_account_passwordInputSettings.flags = 3;
        tL_account_passwordInputSettings.hint = "";
        tL_account_passwordInputSettings.new_password_hash = new byte[0];
        tL_account_passwordInputSettings.new_algo = new TLRPC.TL_passwordKdfAlgoUnknown();
        tL_account_updatePasswordSettings.new_settings.email = "";
        needShowProgress();
        Utilities.globalQueue.postRunnable(new Runnable() {
            @Override
            public final void run() {
                TwoStepVerificationActivity.this.lambda$clearPassword$27(tL_account_updatePasswordSettings);
            }
        });
    }

    public static void initPasswordNewAlgo(TLRPC.account_Password account_password) {
        TLRPC.PasswordKdfAlgo passwordKdfAlgo = account_password.new_algo;
        if (passwordKdfAlgo instanceof TLRPC.TL_passwordKdfAlgoSHA256SHA256PBKDF2HMACSHA512iter100000SHA256ModPow) {
            TLRPC.TL_passwordKdfAlgoSHA256SHA256PBKDF2HMACSHA512iter100000SHA256ModPow tL_passwordKdfAlgoSHA256SHA256PBKDF2HMACSHA512iter100000SHA256ModPow = (TLRPC.TL_passwordKdfAlgoSHA256SHA256PBKDF2HMACSHA512iter100000SHA256ModPow) passwordKdfAlgo;
            byte[] bArr = new byte[tL_passwordKdfAlgoSHA256SHA256PBKDF2HMACSHA512iter100000SHA256ModPow.salt1.length + 32];
            Utilities.random.nextBytes(bArr);
            byte[] bArr2 = tL_passwordKdfAlgoSHA256SHA256PBKDF2HMACSHA512iter100000SHA256ModPow.salt1;
            System.arraycopy(bArr2, 0, bArr, 0, bArr2.length);
            tL_passwordKdfAlgoSHA256SHA256PBKDF2HMACSHA512iter100000SHA256ModPow.salt1 = bArr;
        }
        TLRPC.SecurePasswordKdfAlgo securePasswordKdfAlgo = account_password.new_secure_algo;
        if (securePasswordKdfAlgo instanceof TLRPC.TL_securePasswordKdfAlgoPBKDF2HMACSHA512iter100000) {
            TLRPC.TL_securePasswordKdfAlgoPBKDF2HMACSHA512iter100000 tL_securePasswordKdfAlgoPBKDF2HMACSHA512iter100000 = (TLRPC.TL_securePasswordKdfAlgoPBKDF2HMACSHA512iter100000) securePasswordKdfAlgo;
            byte[] bArr3 = new byte[tL_securePasswordKdfAlgoPBKDF2HMACSHA512iter100000.salt.length + 32];
            Utilities.random.nextBytes(bArr3);
            byte[] bArr4 = tL_securePasswordKdfAlgoPBKDF2HMACSHA512iter100000.salt;
            System.arraycopy(bArr4, 0, bArr3, 0, bArr4.length);
            tL_securePasswordKdfAlgoPBKDF2HMACSHA512iter100000.salt = bArr3;
        }
    }

    public void lambda$cancelPasswordReset$10(DialogInterface dialogInterface, int i) {
        getConnectionsManager().sendRequest(new TLRPC.TL_account_declinePasswordReset(), new RequestDelegate() {
            @Override
            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                TwoStepVerificationActivity.this.lambda$cancelPasswordReset$9(tLObject, tL_error);
            }
        });
    }

    public void lambda$cancelPasswordReset$8(TLObject tLObject) {
        if (tLObject instanceof TLRPC.TL_boolTrue) {
            this.currentPassword.pending_reset_date = 0;
            updateBottomButton();
        }
    }

    public void lambda$cancelPasswordReset$9(final TLObject tLObject, TLRPC.TL_error tL_error) {
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                TwoStepVerificationActivity.this.lambda$cancelPasswordReset$8(tLObject);
            }
        });
    }

    public static void lambda$checkSecretValues$28(TLObject tLObject, TLRPC.TL_error tL_error) {
    }

    public void lambda$clearPassword$21(TLRPC.TL_error tL_error, TLObject tLObject) {
        if (tL_error == null) {
            TLRPC.account_Password account_password = (TLRPC.account_Password) tLObject;
            this.currentPassword = account_password;
            initPasswordNewAlgo(account_password);
            NotificationCenter.getInstance(this.currentAccount).lambda$postNotificationNameOnUIThread$1(NotificationCenter.didSetOrRemoveTwoStepPassword, this.currentPassword);
            clearPassword();
        }
    }

    public void lambda$clearPassword$22(final TLObject tLObject, final TLRPC.TL_error tL_error) {
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                TwoStepVerificationActivity.this.lambda$clearPassword$21(tL_error, tLObject);
            }
        });
    }

    public void lambda$clearPassword$23(TLRPC.TL_error tL_error, TLObject tLObject) {
        if (tL_error == null) {
            TLRPC.account_Password account_password = (TLRPC.account_Password) tLObject;
            this.currentPassword = account_password;
            initPasswordNewAlgo(account_password);
            NotificationCenter.getInstance(this.currentAccount).lambda$postNotificationNameOnUIThread$1(NotificationCenter.didSetOrRemoveTwoStepPassword, this.currentPassword);
            clearPassword();
        }
    }

    public void lambda$clearPassword$24(final TLObject tLObject, final TLRPC.TL_error tL_error) {
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                TwoStepVerificationActivity.this.lambda$clearPassword$23(tL_error, tLObject);
            }
        });
    }

    public void lambda$clearPassword$25(TLRPC.TL_error tL_error, TLObject tLObject) {
        String string;
        String str;
        if (tL_error != null && "SRP_ID_INVALID".equals(tL_error.text)) {
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(new TLRPC.TL_account_getPassword(), new RequestDelegate() {
                @Override
                public final void run(TLObject tLObject2, TLRPC.TL_error tL_error2) {
                    TwoStepVerificationActivity.this.lambda$clearPassword$24(tLObject2, tL_error2);
                }
            }, 8);
            return;
        }
        needHideProgress();
        if (tL_error == null && (tLObject instanceof TLRPC.TL_boolTrue)) {
            this.currentPassword = null;
            this.currentPasswordHash = new byte[0];
            NotificationCenter.getInstance(this.currentAccount).lambda$postNotificationNameOnUIThread$1(NotificationCenter.didRemoveTwoStepPassword, new Object[0]);
            NotificationCenter.getInstance(this.currentAccount).lambda$postNotificationNameOnUIThread$1(NotificationCenter.didSetOrRemoveTwoStepPassword, new Object[0]);
            lambda$onBackPressed$321();
            return;
        }
        if (tL_error != null) {
            if (tL_error.text.startsWith("FLOOD_WAIT")) {
                int intValue = Utilities.parseInt((CharSequence) tL_error.text).intValue();
                String formatPluralString = intValue < 60 ? LocaleController.formatPluralString("Seconds", intValue, new Object[0]) : LocaleController.formatPluralString("Minutes", intValue / 60, new Object[0]);
                string = LocaleController.getString(R.string.AppName);
                str = LocaleController.formatString("FloodWaitTime", R.string.FloodWaitTime, formatPluralString);
            } else {
                string = LocaleController.getString(R.string.AppName);
                str = tL_error.text;
            }
            showAlertWithText(string, str);
        }
    }

    public void lambda$clearPassword$26(final TLObject tLObject, final TLRPC.TL_error tL_error) {
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                TwoStepVerificationActivity.this.lambda$clearPassword$25(tL_error, tLObject);
            }
        });
    }

    public void lambda$clearPassword$27(TLRPC.TL_account_updatePasswordSettings tL_account_updatePasswordSettings) {
        if (tL_account_updatePasswordSettings.password == null) {
            if (this.currentPassword.current_algo == null) {
                ConnectionsManager.getInstance(this.currentAccount).sendRequest(new TLRPC.TL_account_getPassword(), new RequestDelegate() {
                    @Override
                    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                        TwoStepVerificationActivity.this.lambda$clearPassword$22(tLObject, tL_error);
                    }
                }, 8);
                return;
            }
            tL_account_updatePasswordSettings.password = getNewSrpPassword();
        }
        ConnectionsManager.getInstance(this.currentAccount).sendRequest(tL_account_updatePasswordSettings, new RequestDelegate() {
            @Override
            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                TwoStepVerificationActivity.this.lambda$clearPassword$26(tLObject, tL_error);
            }
        }, 10);
    }

    public void lambda$createView$1(View view, boolean z) {
        this.passwordOutlineView.animateSelection(z ? 1.0f : 0.0f);
    }

    public boolean lambda$createView$2(TextView textView, int i, KeyEvent keyEvent) {
        if (i != 5 && i != 6) {
            return false;
        }
        processDone();
        return true;
    }

    public void lambda$createView$3(View view) {
        onPasswordForgot();
    }

    public void lambda$createView$4(View view) {
        cancelPasswordReset();
    }

    public void lambda$createView$5(View view) {
        processDone();
    }

    public void lambda$createView$6(DialogInterface dialogInterface, int i) {
        clearPassword();
    }

    public void lambda$createView$7(View view, int i) {
        TwoStepVerificationSetupActivity twoStepVerificationSetupActivity;
        if (i == this.setPasswordRow || i == this.changePasswordRow) {
            twoStepVerificationSetupActivity = new TwoStepVerificationSetupActivity(this.currentAccount, 0, this.currentPassword);
            twoStepVerificationSetupActivity.addFragmentToClose(this);
            twoStepVerificationSetupActivity.setCurrentPasswordParams(this.currentPasswordHash, this.currentSecretId, this.currentSecret, false);
        } else {
            if (i != this.setRecoveryEmailRow && i != this.changeRecoveryEmailRow) {
                if (i == this.turnPasswordOffRow) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getParentActivity());
                    String string = LocaleController.getString(R.string.TurnPasswordOffQuestion);
                    if (this.currentPassword.has_secure_values) {
                        string = string + "\n\n" + LocaleController.getString(R.string.TurnPasswordOffPassport);
                    }
                    String string2 = LocaleController.getString(R.string.TurnPasswordOffQuestionTitle);
                    String string3 = LocaleController.getString(R.string.Disable);
                    builder.setMessage(string);
                    builder.setTitle(string2);
                    builder.setPositiveButton(string3, new DialogInterface.OnClickListener() {
                        @Override
                        public final void onClick(DialogInterface dialogInterface, int i2) {
                            TwoStepVerificationActivity.this.lambda$createView$6(dialogInterface, i2);
                        }
                    });
                    builder.setNegativeButton(LocaleController.getString(R.string.Cancel), null);
                    AlertDialog create = builder.create();
                    showDialog(create);
                    TextView textView = (TextView) create.getButton(-1);
                    if (textView != null) {
                        textView.setTextColor(Theme.getColor(Theme.key_text_RedBold));
                        return;
                    }
                    return;
                }
                return;
            }
            twoStepVerificationSetupActivity = new TwoStepVerificationSetupActivity(this.currentAccount, 3, this.currentPassword);
            twoStepVerificationSetupActivity.addFragmentToClose(this);
            twoStepVerificationSetupActivity.setCurrentPasswordParams(this.currentPasswordHash, this.currentSecretId, this.currentSecret, true);
        }
        presentFragment(twoStepVerificationSetupActivity);
    }

    public void lambda$loadPasswordInfo$18(TLRPC.TL_error tL_error, TLObject tLObject, boolean z, boolean z2, Runnable runnable) {
        if (tL_error == null) {
            this.loading = false;
            TLRPC.account_Password account_password = (TLRPC.account_Password) tLObject;
            this.currentPassword = account_password;
            if (!canHandleCurrentPassword(account_password, false)) {
                AlertsCreator.showUpdateAppAlert(getParentActivity(), LocaleController.getString(R.string.UpdateAppAlert), true);
                return;
            }
            if (!z || z2) {
                byte[] bArr = this.currentPasswordHash;
                this.passwordEntered = (bArr != null && bArr.length > 0) || !this.currentPassword.has_password;
            }
            initPasswordNewAlgo(this.currentPassword);
            NotificationCenter.getInstance(this.currentAccount).lambda$postNotificationNameOnUIThread$1(NotificationCenter.didSetOrRemoveTwoStepPassword, this.currentPassword);
        }
        if (runnable != null) {
            runnable.run();
        }
        updateRows();
    }

    public void lambda$loadPasswordInfo$19(final boolean z, final boolean z2, final Runnable runnable, final TLObject tLObject, final TLRPC.TL_error tL_error) {
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                TwoStepVerificationActivity.this.lambda$loadPasswordInfo$18(tL_error, tLObject, z, z2, runnable);
            }
        });
    }

    public void lambda$new$0() {
        this.postedErrorColorTimeout = false;
        this.passwordOutlineView.animateError(0.0f);
    }

    public void lambda$onFieldError$36() {
        AndroidUtilities.cancelRunOnUIThread(this.errorColorTimeout);
        AndroidUtilities.runOnUIThread(this.errorColorTimeout, 1500L);
        this.postedErrorColorTimeout = true;
    }

    public void lambda$onPasswordForgot$14(TLRPC.TL_error tL_error, TLObject tLObject) {
        String string;
        String str;
        needHideProgress();
        if (tL_error == null) {
            TLRPC.account_Password account_password = this.currentPassword;
            account_password.email_unconfirmed_pattern = ((TLRPC.TL_auth_passwordRecovery) tLObject).email_pattern;
            TwoStepVerificationSetupActivity twoStepVerificationSetupActivity = new TwoStepVerificationSetupActivity(this.currentAccount, 4, account_password) {
                @Override
                protected void onReset() {
                    TwoStepVerificationActivity.this.resetPasswordOnShow = true;
                }
            };
            twoStepVerificationSetupActivity.addFragmentToClose(this);
            twoStepVerificationSetupActivity.setCurrentPasswordParams(this.currentPasswordHash, this.currentSecretId, this.currentSecret, false);
            presentFragment(twoStepVerificationSetupActivity);
            return;
        }
        if (tL_error.text.startsWith("FLOOD_WAIT")) {
            int intValue = Utilities.parseInt((CharSequence) tL_error.text).intValue();
            String formatPluralString = intValue < 60 ? LocaleController.formatPluralString("Seconds", intValue, new Object[0]) : LocaleController.formatPluralString("Minutes", intValue / 60, new Object[0]);
            string = LocaleController.getString(R.string.AppName);
            str = LocaleController.formatString("FloodWaitTime", R.string.FloodWaitTime, formatPluralString);
        } else {
            string = LocaleController.getString(R.string.AppName);
            str = tL_error.text;
        }
        showAlertWithText(string, str);
    }

    public void lambda$onPasswordForgot$15(final TLObject tLObject, final TLRPC.TL_error tL_error) {
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                TwoStepVerificationActivity.this.lambda$onPasswordForgot$14(tL_error, tLObject);
            }
        });
    }

    public void lambda$onPasswordForgot$16(DialogInterface dialogInterface, int i) {
        resetPassword();
    }

    public void lambda$onPasswordForgot$17(DialogInterface dialogInterface, int i) {
        resetPassword();
    }

    public void lambda$processDone$29(boolean z, byte[] bArr) {
        TwoStepVerificationActivity twoStepVerificationActivity;
        if (this.delegate == null || !z) {
            needHideProgress();
        }
        if (!z) {
            AlertsCreator.showUpdateAppAlert(getParentActivity(), LocaleController.getString(R.string.UpdateAppAlert), true);
            return;
        }
        this.currentPasswordHash = bArr;
        this.passwordEntered = true;
        if (this.delegate != null) {
            AndroidUtilities.hideKeyboard(this.passwordEditText);
            this.delegate.didEnterPassword(getNewSrpPassword());
            return;
        }
        if (TextUtils.isEmpty(this.currentPassword.email_unconfirmed_pattern)) {
            AndroidUtilities.hideKeyboard(this.passwordEditText);
            TwoStepVerificationActivity twoStepVerificationActivity2 = new TwoStepVerificationActivity();
            twoStepVerificationActivity2.passwordEntered = true;
            twoStepVerificationActivity2.currentPasswordHash = this.currentPasswordHash;
            twoStepVerificationActivity2.currentPassword = this.currentPassword;
            twoStepVerificationActivity2.currentSecret = this.currentSecret;
            twoStepVerificationActivity2.currentSecretId = this.currentSecretId;
            twoStepVerificationActivity = twoStepVerificationActivity2;
        } else {
            TwoStepVerificationSetupActivity twoStepVerificationSetupActivity = new TwoStepVerificationSetupActivity(this.currentAccount, 5, this.currentPassword);
            twoStepVerificationSetupActivity.setCurrentPasswordParams(this.currentPasswordHash, this.currentSecretId, this.currentSecret, true);
            twoStepVerificationActivity = twoStepVerificationSetupActivity;
        }
        presentFragment(twoStepVerificationActivity, true);
    }

    public void lambda$processDone$30(byte[] bArr, TLObject tLObject, final byte[] bArr2) {
        final boolean checkSecretValues = checkSecretValues(bArr, (TLRPC.TL_account_passwordSettings) tLObject);
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                TwoStepVerificationActivity.this.lambda$processDone$29(checkSecretValues, bArr2);
            }
        });
    }

    public void lambda$processDone$31(TLRPC.TL_error tL_error, TLObject tLObject) {
        if (tL_error == null) {
            TLRPC.account_Password account_password = (TLRPC.account_Password) tLObject;
            this.currentPassword = account_password;
            initPasswordNewAlgo(account_password);
            NotificationCenter.getInstance(this.currentAccount).lambda$postNotificationNameOnUIThread$1(NotificationCenter.didSetOrRemoveTwoStepPassword, this.currentPassword);
            processDone();
        }
    }

    public void lambda$processDone$32(final TLObject tLObject, final TLRPC.TL_error tL_error) {
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                TwoStepVerificationActivity.this.lambda$processDone$31(tL_error, tLObject);
            }
        });
    }

    public void lambda$processDone$33(TLRPC.TL_error tL_error) {
        if ("SRP_ID_INVALID".equals(tL_error.text)) {
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(new TLRPC.TL_account_getPassword(), new RequestDelegate() {
                @Override
                public final void run(TLObject tLObject, TLRPC.TL_error tL_error2) {
                    TwoStepVerificationActivity.this.lambda$processDone$32(tLObject, tL_error2);
                }
            }, 8);
            return;
        }
        needHideProgress();
        if ("PASSWORD_HASH_INVALID".equals(tL_error.text)) {
            onFieldError(this.passwordOutlineView, this.passwordEditText, true);
        } else if (!tL_error.text.startsWith("FLOOD_WAIT")) {
            showAlertWithText(LocaleController.getString(R.string.AppName), tL_error.text);
        } else {
            int intValue = Utilities.parseInt((CharSequence) tL_error.text).intValue();
            showAlertWithText(LocaleController.getString(R.string.AppName), LocaleController.formatString("FloodWaitTime", R.string.FloodWaitTime, intValue < 60 ? LocaleController.formatPluralString("Seconds", intValue, new Object[0]) : LocaleController.formatPluralString("Minutes", intValue / 60, new Object[0])));
        }
    }

    public void lambda$processDone$34(final byte[] bArr, final byte[] bArr2, final TLObject tLObject, final TLRPC.TL_error tL_error) {
        if (tL_error == null) {
            Utilities.globalQueue.postRunnable(new Runnable() {
                @Override
                public final void run() {
                    TwoStepVerificationActivity.this.lambda$processDone$30(bArr, tLObject, bArr2);
                }
            });
        } else {
            AndroidUtilities.runOnUIThread(new Runnable() {
                @Override
                public final void run() {
                    TwoStepVerificationActivity.this.lambda$processDone$33(tL_error);
                }
            });
        }
    }

    public void lambda$processDone$35(final byte[] bArr) {
        TLRPC.TL_account_getPasswordSettings tL_account_getPasswordSettings = new TLRPC.TL_account_getPasswordSettings();
        TLRPC.PasswordKdfAlgo passwordKdfAlgo = this.currentPassword.current_algo;
        final byte[] x = passwordKdfAlgo instanceof TLRPC.TL_passwordKdfAlgoSHA256SHA256PBKDF2HMACSHA512iter100000SHA256ModPow ? SRPHelper.getX(bArr, (TLRPC.TL_passwordKdfAlgoSHA256SHA256PBKDF2HMACSHA512iter100000SHA256ModPow) passwordKdfAlgo) : null;
        RequestDelegate requestDelegate = new RequestDelegate() {
            @Override
            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                TwoStepVerificationActivity.this.lambda$processDone$34(bArr, x, tLObject, tL_error);
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

    public void lambda$resetPassword$11(DialogInterface dialogInterface) {
        getNotificationCenter().lambda$postNotificationNameOnUIThread$1(NotificationCenter.didSetOrRemoveTwoStepPassword, new Object[0]);
        lambda$onBackPressed$321();
    }

    public void lambda$resetPassword$12(TLObject tLObject) {
        needHideProgress();
        if (tLObject instanceof TLRPC.TL_account_resetPasswordOk) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getParentActivity());
            builder.setNegativeButton(LocaleController.getString(R.string.OK), null);
            builder.setTitle(LocaleController.getString(R.string.ResetPassword));
            builder.setMessage(LocaleController.getString(R.string.RestorePasswordResetPasswordOk));
            showDialog(builder.create(), new DialogInterface.OnDismissListener() {
                @Override
                public final void onDismiss(DialogInterface dialogInterface) {
                    TwoStepVerificationActivity.this.lambda$resetPassword$11(dialogInterface);
                }
            });
            return;
        }
        if (tLObject instanceof TLRPC.TL_account_resetPasswordRequestedWait) {
            this.currentPassword.pending_reset_date = ((TLRPC.TL_account_resetPasswordRequestedWait) tLObject).until_date;
            updateBottomButton();
        } else if (tLObject instanceof TLRPC.TL_account_resetPasswordFailedWait) {
            int currentTime = ((TLRPC.TL_account_resetPasswordFailedWait) tLObject).retry_date - getConnectionsManager().getCurrentTime();
            showAlertWithText(LocaleController.getString(R.string.ResetPassword), LocaleController.formatString("ResetPasswordWait", R.string.ResetPasswordWait, currentTime > 86400 ? LocaleController.formatPluralString("Days", currentTime / 86400, new Object[0]) : currentTime > 3600 ? LocaleController.formatPluralString("Hours", currentTime / 86400, new Object[0]) : currentTime > 60 ? LocaleController.formatPluralString("Minutes", currentTime / 60, new Object[0]) : LocaleController.formatPluralString("Seconds", Math.max(1, currentTime), new Object[0])));
        }
    }

    public void lambda$resetPassword$13(final TLObject tLObject, TLRPC.TL_error tL_error) {
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                TwoStepVerificationActivity.this.lambda$resetPassword$12(tLObject);
            }
        });
    }

    public void lambda$showSetForcePasswordAlert$37(DialogInterface dialogInterface, int i) {
        lambda$onBackPressed$321();
    }

    public void lambda$updateRows$20() {
        EditTextBoldCursor editTextBoldCursor;
        if (isFinishing() || this.destroyed || (editTextBoldCursor = this.passwordEditText) == null) {
            return;
        }
        editTextBoldCursor.requestFocus();
        AndroidUtilities.showKeyboard(this.passwordEditText);
    }

    private void loadPasswordInfo(final boolean z, final boolean z2, final Runnable runnable) {
        if (!z2) {
            this.loading = true;
            ListAdapter listAdapter = this.listAdapter;
            if (listAdapter != null) {
                listAdapter.notifyDataSetChanged();
            }
        }
        ConnectionsManager.getInstance(this.currentAccount).sendRequest(new TLRPC.TL_account_getPassword(), new RequestDelegate() {
            @Override
            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                TwoStepVerificationActivity.this.lambda$loadPasswordInfo$19(z2, z, runnable, tLObject, tL_error);
            }
        }, 10);
    }

    private void needShowProgress() {
        needShowProgress(false);
    }

    private void needShowProgress(boolean z) {
        if (getParentActivity() == null || getParentActivity().isFinishing() || this.progressDialog != null) {
            return;
        }
        if (!this.passwordEntered) {
            AnimatorSet animatorSet = new AnimatorSet();
            animatorSet.playTogether(ObjectAnimator.ofFloat(this.radialProgressView, (Property<RadialProgressView, Float>) View.ALPHA, 1.0f), ObjectAnimator.ofFloat(this.radialProgressView, (Property<RadialProgressView, Float>) View.SCALE_X, 1.0f), ObjectAnimator.ofFloat(this.radialProgressView, (Property<RadialProgressView, Float>) View.SCALE_Y, 1.0f));
            animatorSet.setInterpolator(CubicBezierInterpolator.DEFAULT);
            animatorSet.start();
            return;
        }
        AlertDialog alertDialog = new AlertDialog(getParentActivity(), 3);
        this.progressDialog = alertDialog;
        alertDialog.setCanCancel(false);
        if (z) {
            this.progressDialog.showDelayed(300L);
        } else {
            this.progressDialog.show();
        }
    }

    private void onFieldError(OutlineTextContainerView outlineTextContainerView, TextView textView, boolean z) {
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
        outlineTextContainerView.animateError(1.0f);
        AndroidUtilities.shakeViewSpring(outlineTextContainerView, 5.0f, new Runnable() {
            @Override
            public final void run() {
                TwoStepVerificationActivity.this.lambda$onFieldError$36();
            }
        });
    }

    private void onPasswordForgot() {
        TLRPC.account_Password account_password = this.currentPassword;
        if (account_password.pending_reset_date == 0 && account_password.has_recovery) {
            needShowProgress(true);
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(new TLRPC.TL_auth_requestPasswordRecovery(), new RequestDelegate() {
                @Override
                public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                    TwoStepVerificationActivity.this.lambda$onPasswordForgot$15(tLObject, tL_error);
                }
            }, 10);
            return;
        }
        if (getParentActivity() == null) {
            return;
        }
        if (this.currentPassword.pending_reset_date == 0) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getParentActivity());
            builder.setPositiveButton(LocaleController.getString(R.string.Reset), new DialogInterface.OnClickListener() {
                @Override
                public final void onClick(DialogInterface dialogInterface, int i) {
                    TwoStepVerificationActivity.this.lambda$onPasswordForgot$17(dialogInterface, i);
                }
            });
            builder.setNegativeButton(LocaleController.getString(R.string.Cancel), null);
            builder.setTitle(LocaleController.getString(R.string.ResetPassword));
            builder.setMessage(LocaleController.getString(R.string.RestorePasswordNoEmailText2));
            showDialog(builder.create());
            return;
        }
        if (getConnectionsManager().getCurrentTime() <= this.currentPassword.pending_reset_date) {
            cancelPasswordReset();
            return;
        }
        AlertDialog.Builder builder2 = new AlertDialog.Builder(getParentActivity());
        builder2.setPositiveButton(LocaleController.getString(R.string.Reset), new DialogInterface.OnClickListener() {
            @Override
            public final void onClick(DialogInterface dialogInterface, int i) {
                TwoStepVerificationActivity.this.lambda$onPasswordForgot$16(dialogInterface, i);
            }
        });
        builder2.setNegativeButton(LocaleController.getString(R.string.Cancel), null);
        builder2.setTitle(LocaleController.getString(R.string.ResetPassword));
        builder2.setMessage(LocaleController.getString(R.string.RestorePasswordResetPasswordText));
        AlertDialog create = builder2.create();
        showDialog(create);
        TextView textView = (TextView) create.getButton(-1);
        if (textView != null) {
            textView.setTextColor(Theme.getColor(Theme.key_text_RedBold));
        }
    }

    private void processDone() {
        if (this.passwordEntered) {
            return;
        }
        String obj = this.passwordEditText.getText().toString();
        if (obj.length() == 0) {
            onFieldError(this.passwordOutlineView, this.passwordEditText, false);
            return;
        }
        final byte[] stringBytes = AndroidUtilities.getStringBytes(obj);
        needShowProgress();
        Utilities.globalQueue.postRunnable(new Runnable() {
            @Override
            public final void run() {
                TwoStepVerificationActivity.this.lambda$processDone$35(stringBytes);
            }
        });
    }

    private void resetPassword() {
        needShowProgress(true);
        getConnectionsManager().sendRequest(new TLRPC.TL_account_resetPassword(), new RequestDelegate() {
            @Override
            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                TwoStepVerificationActivity.this.lambda$resetPassword$13(tLObject, tL_error);
            }
        });
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

    public void showSetForcePasswordAlert() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getParentActivity());
        builder.setTitle(LocaleController.getString(R.string.Warning));
        builder.setMessage(LocaleController.formatPluralString("ForceSetPasswordAlertMessageShort", this.otherwiseReloginDays, new Object[0]));
        builder.setPositiveButton(LocaleController.getString(R.string.TwoStepVerificationSetPassword), null);
        builder.setNegativeButton(LocaleController.getString(R.string.ForceSetPasswordCancel), new DialogInterface.OnClickListener() {
            @Override
            public final void onClick(DialogInterface dialogInterface, int i) {
                TwoStepVerificationActivity.this.lambda$showSetForcePasswordAlert$37(dialogInterface, i);
            }
        });
        ((TextView) builder.show().getButton(-2)).setTextColor(Theme.getColor(Theme.key_text_RedBold));
    }

    public void updateBottomButton() {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.TwoStepVerificationActivity.updateBottomButton():void");
    }

    private void updateRows() {
        TLRPC.account_Password account_password;
        StringBuilder sb = new StringBuilder();
        sb.append(this.setPasswordRow);
        sb.append(this.setPasswordDetailRow);
        sb.append(this.changePasswordRow);
        sb.append(this.turnPasswordOffRow);
        sb.append(this.setRecoveryEmailRow);
        sb.append(this.changeRecoveryEmailRow);
        sb.append(this.passwordEnabledDetailRow);
        sb.append(this.rowCount);
        this.rowCount = 0;
        this.setPasswordRow = -1;
        this.setPasswordDetailRow = -1;
        this.changePasswordRow = -1;
        this.turnPasswordOffRow = -1;
        this.setRecoveryEmailRow = -1;
        this.changeRecoveryEmailRow = -1;
        this.passwordEnabledDetailRow = -1;
        if (!this.loading && (account_password = this.currentPassword) != null && this.passwordEntered) {
            if (account_password.has_password) {
                this.changePasswordRow = 0;
                this.rowCount = 2;
                this.turnPasswordOffRow = 1;
                boolean z = account_password.has_recovery;
                this.rowCount = 3;
                if (z) {
                    this.changeRecoveryEmailRow = 2;
                } else {
                    this.setRecoveryEmailRow = 2;
                }
                int i = this.rowCount;
                this.rowCount = i + 1;
                this.passwordEnabledDetailRow = i;
            } else {
                this.setPasswordRow = 0;
                this.rowCount = 2;
                this.setPasswordDetailRow = 1;
            }
        }
        StringBuilder sb2 = new StringBuilder();
        sb2.append(this.setPasswordRow);
        sb2.append(this.setPasswordDetailRow);
        sb2.append(this.changePasswordRow);
        sb2.append(this.turnPasswordOffRow);
        sb2.append(this.setRecoveryEmailRow);
        sb2.append(this.changeRecoveryEmailRow);
        sb2.append(this.passwordEnabledDetailRow);
        sb2.append(this.rowCount);
        if (this.listAdapter != null && !sb.toString().equals(sb2.toString())) {
            this.listAdapter.notifyDataSetChanged();
        }
        if (this.fragmentView != null) {
            if (this.loading || this.passwordEntered) {
                RecyclerListView recyclerListView = this.listView;
                if (recyclerListView != null) {
                    recyclerListView.setVisibility(0);
                    this.scrollView.setVisibility(4);
                    this.listView.setEmptyView(this.emptyView);
                }
                if (this.passwordEditText != null) {
                    this.floatingButtonContainer.setVisibility(8);
                    this.passwordEditText.setVisibility(4);
                    this.titleTextView.setVisibility(4);
                    this.bottomTextView.setVisibility(8);
                    this.bottomButton.setVisibility(4);
                    updateBottomButton();
                }
                View view = this.fragmentView;
                int i2 = Theme.key_windowBackgroundGray;
                view.setBackgroundColor(Theme.getColor(i2));
                this.fragmentView.setTag(Integer.valueOf(i2));
                return;
            }
            RecyclerListView recyclerListView2 = this.listView;
            if (recyclerListView2 != null) {
                recyclerListView2.setEmptyView(null);
                this.listView.setVisibility(4);
                this.scrollView.setVisibility(0);
                this.emptyView.setVisibility(4);
            }
            if (this.passwordEditText != null) {
                this.floatingButtonContainer.setVisibility(0);
                this.passwordEditText.setVisibility(0);
                View view2 = this.fragmentView;
                int i3 = Theme.key_windowBackgroundWhite;
                view2.setBackgroundColor(Theme.getColor(i3));
                this.fragmentView.setTag(Integer.valueOf(i3));
                this.titleTextView.setVisibility(0);
                this.bottomButton.setVisibility(0);
                updateBottomButton();
                this.bottomTextView.setVisibility(8);
                if (TextUtils.isEmpty(this.currentPassword.hint)) {
                    this.passwordEditText.setHint((CharSequence) null);
                } else {
                    this.passwordEditText.setHint(this.currentPassword.hint);
                }
                AndroidUtilities.runOnUIThread(new Runnable() {
                    @Override
                    public final void run() {
                        TwoStepVerificationActivity.this.lambda$updateRows$20();
                    }
                }, 200L);
            }
        }
    }

    @Override
    public android.view.View createView(android.content.Context r34) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.TwoStepVerificationActivity.createView(android.content.Context):android.view.View");
    }

    @Override
    public void didReceivedNotification(int i, int i2, Object... objArr) {
        Object obj;
        if (i == NotificationCenter.twoStepPasswordChanged) {
            if (objArr != null && objArr.length > 0 && (obj = objArr[0]) != null) {
                this.currentPasswordHash = (byte[]) obj;
            }
            loadPasswordInfo(false, false, null);
            updateRows();
        }
    }

    @Override
    public void lambda$onBackPressed$321() {
        if (this.otherwiseReloginDays < 0) {
            super.lambda$onBackPressed$321();
            return;
        }
        Bundle bundle = new Bundle();
        bundle.putBoolean("afterSignup", true);
        presentFragment(new DialogsActivity(bundle), true);
    }

    public TLRPC.TL_inputCheckPasswordSRP getNewSrpPassword() {
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
        int i = Theme.key_windowBackgroundWhite;
        arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_CELLBACKGROUNDCOLOR, new Class[]{TextSettingsCell.class, EditTextSettingsCell.class}, null, null, null, i));
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
        arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_SELECTOR, null, null, null, null, Theme.key_listSelector));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{View.class}, Theme.dividerPaint, null, null, Theme.key_divider));
        arrayList.add(new ThemeDescription(this.emptyView, ThemeDescription.FLAG_PROGRESSBAR, null, null, null, null, Theme.key_progressCircle));
        int i4 = Theme.key_windowBackgroundWhiteBlackText;
        arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_CHECKTAG, new Class[]{TextSettingsCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, i4));
        arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_CHECKTAG, new Class[]{TextSettingsCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_text_RedRegular));
        arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{EditTextSettingsCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, i4));
        int i5 = Theme.key_windowBackgroundWhiteHintText;
        arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_HINTTEXTCOLOR, new Class[]{EditTextSettingsCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, i5));
        arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{TextInfoPrivacyCell.class}, null, null, null, Theme.key_windowBackgroundGrayShadow));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{TextInfoPrivacyCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteGrayText4));
        TextView textView = this.titleTextView;
        int i6 = ThemeDescription.FLAG_TEXTCOLOR;
        int i7 = Theme.key_windowBackgroundWhiteGrayText6;
        arrayList.add(new ThemeDescription(textView, i6, null, null, null, null, i7));
        arrayList.add(new ThemeDescription(this.bottomTextView, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, i7));
        arrayList.add(new ThemeDescription(this.bottomButton, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, Theme.key_windowBackgroundWhiteBlueText4));
        arrayList.add(new ThemeDescription(this.passwordEditText, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, i4));
        arrayList.add(new ThemeDescription(this.passwordEditText, ThemeDescription.FLAG_HINTTEXTCOLOR, null, null, null, null, i5));
        arrayList.add(new ThemeDescription(this.passwordEditText, ThemeDescription.FLAG_BACKGROUNDFILTER, null, null, null, null, Theme.key_windowBackgroundWhiteInputField));
        arrayList.add(new ThemeDescription(this.passwordEditText, ThemeDescription.FLAG_BACKGROUNDFILTER | ThemeDescription.FLAG_DRAWABLESELECTEDSTATE, null, null, null, null, Theme.key_windowBackgroundWhiteInputFieldActivated));
        return arrayList;
    }

    @Override
    public boolean isLightStatusBar() {
        return ColorUtils.calculateLuminance(Theme.getColor(Theme.key_windowBackgroundWhite, null, true)) > 0.699999988079071d;
    }

    public void needHideProgress() {
        if (!this.passwordEntered) {
            AnimatorSet animatorSet = new AnimatorSet();
            animatorSet.playTogether(ObjectAnimator.ofFloat(this.radialProgressView, (Property<RadialProgressView, Float>) View.ALPHA, 0.0f), ObjectAnimator.ofFloat(this.radialProgressView, (Property<RadialProgressView, Float>) View.SCALE_X, 0.1f), ObjectAnimator.ofFloat(this.radialProgressView, (Property<RadialProgressView, Float>) View.SCALE_Y, 0.1f));
            animatorSet.setInterpolator(CubicBezierInterpolator.DEFAULT);
            animatorSet.start();
            return;
        }
        AlertDialog alertDialog = this.progressDialog;
        if (alertDialog == null) {
            return;
        }
        try {
            alertDialog.dismiss();
        } catch (Exception e) {
            FileLog.e(e);
        }
        this.progressDialog = null;
    }

    @Override
    public boolean onBackPressed() {
        if (this.otherwiseReloginDays < 0) {
            return super.onBackPressed();
        }
        showSetForcePasswordAlert();
        return false;
    }

    @Override
    public void onConfigurationChanged(Configuration configuration) {
        int i;
        super.onConfigurationChanged(configuration);
        RLottieImageView rLottieImageView = this.lockImageView;
        if (!AndroidUtilities.isSmallScreen()) {
            Point point = AndroidUtilities.displaySize;
            if (point.x <= point.y) {
                i = 0;
                rLottieImageView.setVisibility(i);
            }
        }
        i = 8;
        rLottieImageView.setVisibility(i);
    }

    @Override
    public boolean onFragmentCreate() {
        super.onFragmentCreate();
        if (!this.preloaded) {
            preload(null);
        }
        updateRows();
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.twoStepPasswordChanged);
        return true;
    }

    @Override
    public void onFragmentDestroy() {
        super.onFragmentDestroy();
        AndroidUtilities.cancelRunOnUIThread(this.updateTimeRunnable);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.twoStepPasswordChanged);
        this.destroyed = true;
        AlertDialog alertDialog = this.progressDialog;
        if (alertDialog != null) {
            try {
                alertDialog.dismiss();
            } catch (Exception e) {
                FileLog.e(e);
            }
            this.progressDialog = null;
        }
        AndroidUtilities.removeAdjustResize(getParentActivity(), this.classGuid);
    }

    @Override
    public void onPause() {
        super.onPause();
        this.paused = true;
    }

    @Override
    public void onResume() {
        super.onResume();
        this.paused = false;
        AndroidUtilities.requestAdjustResize(getParentActivity(), this.classGuid);
    }

    @Override
    public void onTransitionAnimationEnd(boolean z, boolean z2) {
        super.onTransitionAnimationEnd(z, z2);
        if (z) {
            if (this.forgotPasswordOnShow) {
                onPasswordForgot();
                this.forgotPasswordOnShow = false;
            } else if (this.resetPasswordOnShow) {
                resetPassword();
                this.resetPasswordOnShow = false;
            }
        }
    }

    public void preload(Runnable runnable) {
        byte[] bArr;
        this.preloaded = false;
        TLRPC.account_Password account_password = this.currentPassword;
        if (account_password == null || account_password.current_algo == null || (bArr = this.currentPasswordHash) == null || bArr.length <= 0) {
            loadPasswordInfo(true, account_password != null, runnable);
        } else if (runnable != null) {
            runnable.run();
        }
    }

    public void setBlockingAlert(int i) {
        this.otherwiseReloginDays = i;
    }

    public void setCurrentPasswordInfo(byte[] bArr, TLRPC.account_Password account_password) {
        if (bArr != null) {
            this.currentPasswordHash = bArr;
        }
        this.currentPassword = account_password;
    }

    public void setCurrentPasswordParams(TLRPC.account_Password account_password, byte[] bArr, long j, byte[] bArr2) {
        this.currentPassword = account_password;
        this.currentPasswordHash = bArr;
        this.currentSecret = bArr2;
        this.currentSecretId = j;
        this.passwordEntered = (bArr != null && bArr.length > 0) || !account_password.has_password;
    }

    public void setDelegate(int i, TwoStepVerificationActivityDelegate twoStepVerificationActivityDelegate) {
        this.delegateType = i;
        this.delegate = twoStepVerificationActivityDelegate;
    }

    public void setForgotPasswordOnShow() {
        this.forgotPasswordOnShow = true;
    }

    public void setPassword(TLRPC.account_Password account_password) {
        this.currentPassword = account_password;
        this.passwordEntered = false;
    }
}
