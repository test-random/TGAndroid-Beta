package org.telegram.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Vibrator;
import android.telephony.PhoneNumberUtils;
import android.telephony.TelephonyManager;
import android.text.Editable;
import android.text.SpannableStringBuilder;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.PasswordTransformationMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.util.Base64;
import android.util.Property;
import android.view.ActionMode;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.core.content.FileProvider;
import java.io.File;
import java.io.RandomAccessFile;
import java.security.KeyFactory;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import javax.crypto.Cipher;
import org.json.JSONArray;
import org.json.JSONObject;
import org.telegram.PhoneFormat.PhoneFormat;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.BuildVars;
import org.telegram.messenger.DispatchQueue;
import org.telegram.messenger.DownloadController;
import org.telegram.messenger.FileLoader;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.ImageLoader;
import org.telegram.messenger.ImageReceiver;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MediaController;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.MrzRecognizer;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.PushListenerController;
import org.telegram.messenger.R;
import org.telegram.messenger.SRPHelper;
import org.telegram.messenger.SecureDocument;
import org.telegram.messenger.SecureDocumentKey;
import org.telegram.messenger.SendMessagesHelper;
import org.telegram.messenger.SharedConfig;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.UserObject;
import org.telegram.messenger.Utilities;
import org.telegram.messenger.browser.Browser;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.ActionBarMenuItem;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ActionBar.ThemeDescription;
import org.telegram.ui.CameraScanActivity;
import org.telegram.ui.Cells.CheckBoxCell;
import org.telegram.ui.Cells.HeaderCell;
import org.telegram.ui.Cells.ShadowSectionCell;
import org.telegram.ui.Cells.TextDetailSettingsCell;
import org.telegram.ui.Cells.TextInfoPrivacyCell;
import org.telegram.ui.Cells.TextSettingsCell;
import org.telegram.ui.Components.AlertsCreator;
import org.telegram.ui.Components.AvatarDrawable;
import org.telegram.ui.Components.BackupImageView;
import org.telegram.ui.Components.ChatAttachAlert;
import org.telegram.ui.Components.ContextProgressView;
import org.telegram.ui.Components.EditTextBoldCursor;
import org.telegram.ui.Components.EmptyTextProgressView;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.RadialProgress;
import org.telegram.ui.Components.SlideView;
import org.telegram.ui.CountrySelectActivity;
import org.telegram.ui.PassportActivity;
import org.telegram.ui.PhotoViewer;

public class PassportActivity extends BaseFragment implements NotificationCenter.NotificationCenterDelegate {
    private TextView acceptTextView;
    private TextSettingsCell addDocumentCell;
    private ShadowSectionCell addDocumentSectionCell;
    private boolean allowNonLatinName;
    private ArrayList availableDocumentTypes;
    private TextInfoPrivacyCell bottomCell;
    private TextInfoPrivacyCell bottomCellTranslation;
    private FrameLayout bottomLayout;
    private boolean callbackCalled;
    private ChatAttachAlert chatAttachAlert;
    private HashMap codesMap;
    private ArrayList countriesArray;
    private HashMap countriesMap;
    private int currentActivityType;
    private long currentBotId;
    private String currentCallbackUrl;
    private String currentCitizeship;
    private HashMap currentDocumentValues;
    private TLRPC.TL_secureRequiredType currentDocumentsType;
    private TLRPC.TL_secureValue currentDocumentsTypeValue;
    private String currentEmail;
    private int[] currentExpireDate;
    private TLRPC.TL_account_authorizationForm currentForm;
    private String currentGender;
    private String currentNonce;
    private TLRPC.account_Password currentPassword;
    private String currentPayload;
    private TLRPC.TL_auth_sentCode currentPhoneVerification;
    private LinearLayout currentPhotoViewerLayout;
    private String currentPicturePath;
    private String currentPublicKey;
    private String currentResidence;
    private String currentScope;
    private TLRPC.TL_secureRequiredType currentType;
    private TLRPC.TL_secureValue currentTypeValue;
    private HashMap currentValues;
    private int currentViewNum;
    private PassportActivityDelegate delegate;
    private TextSettingsCell deletePassportCell;
    private ArrayList dividers;
    private boolean documentOnly;
    private ArrayList documents;
    private HashMap documentsCells;
    private HashMap documentsErrors;
    private LinearLayout documentsLayout;
    private HashMap documentsToTypesLink;
    private ActionBarMenuItem doneItem;
    private AnimatorSet doneItemAnimation;
    private int emailCodeLength;
    private ImageView emptyImageView;
    private LinearLayout emptyLayout;
    private TextView emptyTextView1;
    private TextView emptyTextView2;
    private TextView emptyTextView3;
    private EmptyTextProgressView emptyView;
    private HashMap errorsMap;
    private HashMap errorsValues;
    private View extraBackgroundView;
    private View extraBackgroundView2;
    private HashMap fieldsErrors;
    private SecureDocument frontDocument;
    private LinearLayout frontLayout;
    private HeaderCell headerCell;
    private boolean ignoreOnFailure;
    private boolean ignoreOnPhoneChange;
    private boolean ignoreOnTextChange;
    private String initialValues;
    private EditTextBoldCursor[] inputExtraFields;
    private ViewGroup[] inputFieldContainers;
    private EditTextBoldCursor[] inputFields;
    private HashMap languageMap;
    private LinearLayout linearLayout2;
    private HashMap mainErrorsMap;
    private TextInfoPrivacyCell nativeInfoCell;
    private boolean needActivityResult;
    private CharSequence noAllDocumentsErrorText;
    private CharSequence noAllTranslationErrorText;
    private ImageView noPasswordImageView;
    private TextView noPasswordSetTextView;
    private TextView noPasswordTextView;
    private boolean[] nonLatinNames;
    private FrameLayout passwordAvatarContainer;
    private TextView passwordForgotButton;
    private TextInfoPrivacyCell passwordInfoRequestTextView;
    private TextInfoPrivacyCell passwordRequestTextView;
    private PassportActivityDelegate pendingDelegate;
    private ErrorRunnable pendingErrorRunnable;
    private Runnable pendingFinishRunnable;
    private String pendingPhone;
    private Dialog permissionsDialog;
    private ArrayList permissionsItems;
    private HashMap phoneFormatMap;
    private TextView plusTextView;
    private PassportActivity presentAfterAnimation;
    private AlertDialog progressDialog;
    private ContextProgressView progressView;
    private ContextProgressView progressViewButton;
    private PhotoViewer.PhotoViewerProvider provider;
    private SecureDocument reverseDocument;
    private LinearLayout reverseLayout;
    private byte[] saltedPassword;
    private byte[] savedPasswordHash;
    private byte[] savedSaltedPassword;
    private TextSettingsCell scanDocumentCell;
    private int scrollHeight;
    private ScrollView scrollView;
    private ShadowSectionCell sectionCell;
    private ShadowSectionCell sectionCell2;
    private byte[] secureSecret;
    private long secureSecretId;
    private SecureDocument selfieDocument;
    private LinearLayout selfieLayout;
    private TextInfoPrivacyCell topErrorCell;
    private ArrayList translationDocuments;
    private LinearLayout translationLayout;
    private HashMap typesValues;
    private HashMap typesViews;
    private TextSettingsCell uploadDocumentCell;
    private TextDetailSettingsCell uploadFrontCell;
    private TextDetailSettingsCell uploadReverseCell;
    private TextDetailSettingsCell uploadSelfieCell;
    private TextSettingsCell uploadTranslationCell;
    private HashMap uploadingDocuments;
    private int uploadingFileType;
    private boolean useCurrentValue;
    private int usingSavedPassword;
    private SlideView[] views;

    public class C1ValueToSend {
        boolean selfie_required;
        boolean translation_required;
        TLRPC.TL_secureValue value;

        public C1ValueToSend(TLRPC.TL_secureValue tL_secureValue, boolean z, boolean z2) {
            this.value = tL_secureValue;
            this.selfie_required = z;
            this.translation_required = z2;
        }
    }

    public class AnonymousClass3 extends ActionBar.ActionBarMenuOnItemClick {
        AnonymousClass3() {
        }

        public void lambda$onIdentityDone$0(String str, String str2, String str3, Runnable runnable, ErrorRunnable errorRunnable, DialogInterface dialogInterface, int i) {
            PassportActivity.this.inputFields[0].setText(str);
            PassportActivity.this.inputFields[1].setText(str2);
            PassportActivity.this.inputFields[2].setText(str3);
            PassportActivity.this.showEditDoneProgress(true, true);
            onIdentityDone(runnable, errorRunnable);
        }

        public void lambda$onIdentityDone$1(int i, DialogInterface dialogInterface, int i2) {
            PassportActivity passportActivity = PassportActivity.this;
            passportActivity.onFieldError(passportActivity.inputFields[i]);
        }

        public int lambda$onIdentityDone$2(String str, String str2) {
            int fieldCost = PassportActivity.this.getFieldCost(str);
            int fieldCost2 = PassportActivity.this.getFieldCost(str2);
            if (fieldCost < fieldCost2) {
                return -1;
            }
            return fieldCost > fieldCost2 ? 1 : 0;
        }

        public int lambda$onIdentityDone$3(String str, String str2) {
            int fieldCost = PassportActivity.this.getFieldCost(str);
            int fieldCost2 = PassportActivity.this.getFieldCost(str2);
            if (fieldCost < fieldCost2) {
                return -1;
            }
            return fieldCost > fieldCost2 ? 1 : 0;
        }

        public void lambda$onItemClick$4() {
            PassportActivity.this.lambda$onBackPressed$321();
        }

        public void lambda$onItemClick$5(TLRPC.TL_error tL_error, Runnable runnable, ErrorRunnable errorRunnable, TLRPC.TL_account_verifyEmail tL_account_verifyEmail) {
            if (tL_error == null) {
                PassportActivity.this.delegate.saveValue(PassportActivity.this.currentType, (String) PassportActivity.this.currentValues.get("email"), null, null, null, null, null, null, null, null, runnable, errorRunnable);
            } else {
                AlertsCreator.processError(((BaseFragment) PassportActivity.this).currentAccount, tL_error, PassportActivity.this, tL_account_verifyEmail, new Object[0]);
                errorRunnable.onError(null, null);
            }
        }

        public void lambda$onItemClick$6(final Runnable runnable, final ErrorRunnable errorRunnable, final TLRPC.TL_account_verifyEmail tL_account_verifyEmail, TLObject tLObject, final TLRPC.TL_error tL_error) {
            AndroidUtilities.runOnUIThread(new Runnable() {
                @Override
                public final void run() {
                    PassportActivity.AnonymousClass3.this.lambda$onItemClick$5(tL_error, runnable, errorRunnable, tL_account_verifyEmail);
                }
            });
        }

        private boolean onIdentityDone(final java.lang.Runnable r25, final org.telegram.ui.PassportActivity.ErrorRunnable r26) {
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.PassportActivity.AnonymousClass3.onIdentityDone(java.lang.Runnable, org.telegram.ui.PassportActivity$ErrorRunnable):boolean");
        }

        @Override
        public void onItemClick(int r21) {
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.PassportActivity.AnonymousClass3.onItemClick(int):void");
        }
    }

    public class AnonymousClass8 implements RequestDelegate {
        final TLRPC.TL_account_getPasswordSettings val$req;
        final boolean val$saved;
        final String val$textPassword;
        final byte[] val$x_bytes;

        AnonymousClass8(boolean z, byte[] bArr, TLRPC.TL_account_getPasswordSettings tL_account_getPasswordSettings, String str) {
            this.val$saved = z;
            this.val$x_bytes = bArr;
            this.val$req = tL_account_getPasswordSettings;
            this.val$textPassword = str;
        }

        private void generateNewSecret() {
            DispatchQueue dispatchQueue = Utilities.globalQueue;
            final byte[] bArr = this.val$x_bytes;
            final String str = this.val$textPassword;
            dispatchQueue.postRunnable(new Runnable() {
                @Override
                public final void run() {
                    PassportActivity.AnonymousClass8.this.lambda$generateNewSecret$8(bArr, str);
                }
            });
        }

        public void lambda$generateNewSecret$4(TLRPC.TL_error tL_error, TLObject tLObject) {
            if (tL_error == null) {
                PassportActivity.this.currentPassword = (TLRPC.account_Password) tLObject;
                TwoStepVerificationActivity.initPasswordNewAlgo(PassportActivity.this.currentPassword);
                generateNewSecret();
            }
        }

        public void lambda$generateNewSecret$5(final TLObject tLObject, final TLRPC.TL_error tL_error) {
            AndroidUtilities.runOnUIThread(new Runnable() {
                @Override
                public final void run() {
                    PassportActivity.AnonymousClass8.this.lambda$generateNewSecret$4(tL_error, tLObject);
                }
            });
        }

        public void lambda$generateNewSecret$6(TLRPC.TL_error tL_error) {
            if (tL_error != null && "SRP_ID_INVALID".equals(tL_error.text)) {
                ConnectionsManager.getInstance(((BaseFragment) PassportActivity.this).currentAccount).sendRequest(new TLRPC.TL_account_getPassword(), new RequestDelegate() {
                    @Override
                    public final void run(TLObject tLObject, TLRPC.TL_error tL_error2) {
                        PassportActivity.AnonymousClass8.this.lambda$generateNewSecret$5(tLObject, tL_error2);
                    }
                }, 8);
            } else {
                if (PassportActivity.this.currentForm == null) {
                    PassportActivity.this.currentForm = new TLRPC.TL_account_authorizationForm();
                }
                openRequestInterface();
            }
        }

        public void lambda$generateNewSecret$7(TLObject tLObject, final TLRPC.TL_error tL_error) {
            AndroidUtilities.runOnUIThread(new Runnable() {
                @Override
                public final void run() {
                    PassportActivity.AnonymousClass8.this.lambda$generateNewSecret$6(tL_error);
                }
            });
        }

        public void lambda$generateNewSecret$8(byte[] bArr, String str) {
            Utilities.random.setSeed(PassportActivity.this.currentPassword.secure_random);
            TLRPC.TL_account_updatePasswordSettings tL_account_updatePasswordSettings = new TLRPC.TL_account_updatePasswordSettings();
            if (PassportActivity.this.currentPassword.current_algo instanceof TLRPC.TL_passwordKdfAlgoSHA256SHA256PBKDF2HMACSHA512iter100000SHA256ModPow) {
                tL_account_updatePasswordSettings.password = SRPHelper.startCheck(bArr, PassportActivity.this.currentPassword.srp_id, PassportActivity.this.currentPassword.srp_B, (TLRPC.TL_passwordKdfAlgoSHA256SHA256PBKDF2HMACSHA512iter100000SHA256ModPow) PassportActivity.this.currentPassword.current_algo);
            }
            tL_account_updatePasswordSettings.new_settings = new TLRPC.TL_account_passwordInputSettings();
            PassportActivity passportActivity = PassportActivity.this;
            passportActivity.secureSecret = passportActivity.getRandomSecret();
            PassportActivity passportActivity2 = PassportActivity.this;
            passportActivity2.secureSecretId = Utilities.bytesToLong(Utilities.computeSHA256(passportActivity2.secureSecret));
            if (PassportActivity.this.currentPassword.new_secure_algo instanceof TLRPC.TL_securePasswordKdfAlgoPBKDF2HMACSHA512iter100000) {
                TLRPC.TL_securePasswordKdfAlgoPBKDF2HMACSHA512iter100000 tL_securePasswordKdfAlgoPBKDF2HMACSHA512iter100000 = (TLRPC.TL_securePasswordKdfAlgoPBKDF2HMACSHA512iter100000) PassportActivity.this.currentPassword.new_secure_algo;
                PassportActivity.this.saltedPassword = Utilities.computePBKDF2(AndroidUtilities.getStringBytes(str), tL_securePasswordKdfAlgoPBKDF2HMACSHA512iter100000.salt);
                byte[] bArr2 = new byte[32];
                System.arraycopy(PassportActivity.this.saltedPassword, 0, bArr2, 0, 32);
                byte[] bArr3 = new byte[16];
                System.arraycopy(PassportActivity.this.saltedPassword, 32, bArr3, 0, 16);
                Utilities.aesCbcEncryptionByteArraySafe(PassportActivity.this.secureSecret, bArr2, bArr3, 0, PassportActivity.this.secureSecret.length, 0, 1);
                tL_account_updatePasswordSettings.new_settings.new_secure_settings = new TLRPC.TL_secureSecretSettings();
                TLRPC.TL_secureSecretSettings tL_secureSecretSettings = tL_account_updatePasswordSettings.new_settings.new_secure_settings;
                tL_secureSecretSettings.secure_algo = tL_securePasswordKdfAlgoPBKDF2HMACSHA512iter100000;
                tL_secureSecretSettings.secure_secret = PassportActivity.this.secureSecret;
                tL_account_updatePasswordSettings.new_settings.new_secure_settings.secure_secret_id = PassportActivity.this.secureSecretId;
                tL_account_updatePasswordSettings.new_settings.flags |= 4;
            }
            ConnectionsManager.getInstance(((BaseFragment) PassportActivity.this).currentAccount).sendRequest(tL_account_updatePasswordSettings, new RequestDelegate() {
                @Override
                public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                    PassportActivity.AnonymousClass8.this.lambda$generateNewSecret$7(tLObject, tL_error);
                }
            });
        }

        public void lambda$resetSecret$0(TLRPC.TL_error tL_error, TLObject tLObject) {
            if (tL_error == null) {
                PassportActivity.this.currentPassword = (TLRPC.account_Password) tLObject;
                TwoStepVerificationActivity.initPasswordNewAlgo(PassportActivity.this.currentPassword);
                resetSecret();
            }
        }

        public void lambda$resetSecret$1(final TLObject tLObject, final TLRPC.TL_error tL_error) {
            AndroidUtilities.runOnUIThread(new Runnable() {
                @Override
                public final void run() {
                    PassportActivity.AnonymousClass8.this.lambda$resetSecret$0(tL_error, tLObject);
                }
            });
        }

        public void lambda$resetSecret$2(TLRPC.TL_error tL_error) {
            if (tL_error == null || !"SRP_ID_INVALID".equals(tL_error.text)) {
                generateNewSecret();
            } else {
                ConnectionsManager.getInstance(((BaseFragment) PassportActivity.this).currentAccount).sendRequest(new TLRPC.TL_account_getPassword(), new RequestDelegate() {
                    @Override
                    public final void run(TLObject tLObject, TLRPC.TL_error tL_error2) {
                        PassportActivity.AnonymousClass8.this.lambda$resetSecret$1(tLObject, tL_error2);
                    }
                }, 8);
            }
        }

        public void lambda$resetSecret$3(TLObject tLObject, final TLRPC.TL_error tL_error) {
            AndroidUtilities.runOnUIThread(new Runnable() {
                @Override
                public final void run() {
                    PassportActivity.AnonymousClass8.this.lambda$resetSecret$2(tL_error);
                }
            });
        }

        public void lambda$run$10(final boolean z, final TLObject tLObject, final TLRPC.TL_error tL_error) {
            AndroidUtilities.runOnUIThread(new Runnable() {
                @Override
                public final void run() {
                    PassportActivity.AnonymousClass8.this.lambda$run$9(tL_error, tLObject, z);
                }
            });
        }

        public void lambda$run$11() {
            AlertsCreator.showUpdateAppAlert(PassportActivity.this.getParentActivity(), LocaleController.getString(R.string.UpdateAppAlert), true);
        }

        public void lambda$run$12(TLObject tLObject, TLRPC.TL_error tL_error) {
            if (tLObject == null) {
                if ("APP_VERSION_OUTDATED".equals(tL_error.text)) {
                    AlertsCreator.showUpdateAppAlert(PassportActivity.this.getParentActivity(), LocaleController.getString(R.string.UpdateAppAlert), true);
                } else {
                    PassportActivity.this.showAlertWithText(LocaleController.getString(R.string.AppName), tL_error.text);
                }
                PassportActivity.this.showEditDoneProgress(true, false);
                return;
            }
            PassportActivity.this.currentForm = new TLRPC.TL_account_authorizationForm();
            TLRPC.Vector vector = (TLRPC.Vector) tLObject;
            int size = vector.objects.size();
            for (int i = 0; i < size; i++) {
                PassportActivity.this.currentForm.values.add((TLRPC.TL_secureValue) vector.objects.get(i));
            }
            openRequestInterface();
        }

        public void lambda$run$13(final TLObject tLObject, final TLRPC.TL_error tL_error) {
            AndroidUtilities.runOnUIThread(new Runnable() {
                @Override
                public final void run() {
                    PassportActivity.AnonymousClass8.this.lambda$run$12(tLObject, tL_error);
                }
            });
        }

        public void lambda$run$14(TLRPC.TL_account_passwordSettings tL_account_passwordSettings, boolean z, byte[] bArr) {
            PassportActivity.this.currentEmail = tL_account_passwordSettings.email;
            if (z) {
                PassportActivity passportActivity = PassportActivity.this;
                passportActivity.saltedPassword = passportActivity.savedSaltedPassword;
            }
            PassportActivity passportActivity2 = PassportActivity.this;
            if (PassportActivity.checkSecret(passportActivity2.decryptSecret(passportActivity2.secureSecret, PassportActivity.this.saltedPassword), Long.valueOf(PassportActivity.this.secureSecretId)) && bArr.length != 0 && PassportActivity.this.secureSecretId != 0) {
                if (PassportActivity.this.currentBotId != 0) {
                    openRequestInterface();
                    return;
                } else {
                    ConnectionsManager.getInstance(((BaseFragment) PassportActivity.this).currentAccount).sendRequest(new TLRPC.TL_account_getAllSecureValues(), new RequestDelegate() {
                        @Override
                        public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                            PassportActivity.AnonymousClass8.this.lambda$run$13(tLObject, tL_error);
                        }
                    });
                    return;
                }
            }
            PassportActivity passportActivity3 = PassportActivity.this;
            if (z) {
                UserConfig.getInstance(((BaseFragment) passportActivity3).currentAccount).resetSavedPassword();
                PassportActivity.this.usingSavedPassword = 0;
                PassportActivity.this.updatePasswordInterface();
                return;
            }
            if (passportActivity3.currentForm != null) {
                PassportActivity.this.currentForm.values.clear();
                PassportActivity.this.currentForm.errors.clear();
            }
            if (PassportActivity.this.secureSecret == null || PassportActivity.this.secureSecret.length == 0) {
                generateNewSecret();
            } else {
                resetSecret();
            }
        }

        public void lambda$run$15(TLObject tLObject, String str, final boolean z) {
            final byte[] bArr;
            final TLRPC.TL_account_passwordSettings tL_account_passwordSettings = (TLRPC.TL_account_passwordSettings) tLObject;
            TLRPC.TL_secureSecretSettings tL_secureSecretSettings = tL_account_passwordSettings.secure_settings;
            if (tL_secureSecretSettings != null) {
                PassportActivity.this.secureSecret = tL_secureSecretSettings.secure_secret;
                PassportActivity.this.secureSecretId = tL_account_passwordSettings.secure_settings.secure_secret_id;
                TLRPC.SecurePasswordKdfAlgo securePasswordKdfAlgo = tL_account_passwordSettings.secure_settings.secure_algo;
                if (securePasswordKdfAlgo instanceof TLRPC.TL_securePasswordKdfAlgoSHA512) {
                    bArr = ((TLRPC.TL_securePasswordKdfAlgoSHA512) securePasswordKdfAlgo).salt;
                    PassportActivity.this.saltedPassword = Utilities.computeSHA512(bArr, AndroidUtilities.getStringBytes(str), bArr);
                } else if (securePasswordKdfAlgo instanceof TLRPC.TL_securePasswordKdfAlgoPBKDF2HMACSHA512iter100000) {
                    TLRPC.TL_securePasswordKdfAlgoPBKDF2HMACSHA512iter100000 tL_securePasswordKdfAlgoPBKDF2HMACSHA512iter100000 = (TLRPC.TL_securePasswordKdfAlgoPBKDF2HMACSHA512iter100000) securePasswordKdfAlgo;
                    byte[] bArr2 = tL_securePasswordKdfAlgoPBKDF2HMACSHA512iter100000.salt;
                    PassportActivity.this.saltedPassword = Utilities.computePBKDF2(AndroidUtilities.getStringBytes(str), tL_securePasswordKdfAlgoPBKDF2HMACSHA512iter100000.salt);
                    bArr = bArr2;
                } else {
                    if (securePasswordKdfAlgo instanceof TLRPC.TL_securePasswordKdfAlgoUnknown) {
                        AndroidUtilities.runOnUIThread(new Runnable() {
                            @Override
                            public final void run() {
                                PassportActivity.AnonymousClass8.this.lambda$run$11();
                            }
                        });
                        return;
                    }
                    bArr = new byte[0];
                }
            } else {
                if (PassportActivity.this.currentPassword.new_secure_algo instanceof TLRPC.TL_securePasswordKdfAlgoPBKDF2HMACSHA512iter100000) {
                    TLRPC.TL_securePasswordKdfAlgoPBKDF2HMACSHA512iter100000 tL_securePasswordKdfAlgoPBKDF2HMACSHA512iter1000002 = (TLRPC.TL_securePasswordKdfAlgoPBKDF2HMACSHA512iter100000) PassportActivity.this.currentPassword.new_secure_algo;
                    byte[] bArr3 = tL_securePasswordKdfAlgoPBKDF2HMACSHA512iter1000002.salt;
                    PassportActivity.this.saltedPassword = Utilities.computePBKDF2(AndroidUtilities.getStringBytes(str), tL_securePasswordKdfAlgoPBKDF2HMACSHA512iter1000002.salt);
                    bArr = bArr3;
                } else {
                    bArr = new byte[0];
                }
                PassportActivity.this.secureSecret = null;
                PassportActivity.this.secureSecretId = 0L;
            }
            AndroidUtilities.runOnUIThread(new Runnable() {
                @Override
                public final void run() {
                    PassportActivity.AnonymousClass8.this.lambda$run$14(tL_account_passwordSettings, z, bArr);
                }
            });
        }

        public void lambda$run$16(boolean z, TLRPC.TL_error tL_error) {
            if (z) {
                UserConfig.getInstance(((BaseFragment) PassportActivity.this).currentAccount).resetSavedPassword();
                PassportActivity.this.usingSavedPassword = 0;
                PassportActivity.this.updatePasswordInterface();
                if (PassportActivity.this.inputFieldContainers == null || PassportActivity.this.inputFieldContainers[0].getVisibility() != 0) {
                    return;
                }
                PassportActivity.this.inputFields[0].requestFocus();
                AndroidUtilities.showKeyboard(PassportActivity.this.inputFields[0]);
                return;
            }
            PassportActivity.this.showEditDoneProgress(true, false);
            if (tL_error.text.equals("PASSWORD_HASH_INVALID")) {
                PassportActivity.this.onPasscodeError(true);
            } else if (!tL_error.text.startsWith("FLOOD_WAIT")) {
                PassportActivity.this.showAlertWithText(LocaleController.getString(R.string.AppName), tL_error.text);
            } else {
                int intValue = Utilities.parseInt((CharSequence) tL_error.text).intValue();
                PassportActivity.this.showAlertWithText(LocaleController.getString(R.string.AppName), LocaleController.formatString("FloodWaitTime", R.string.FloodWaitTime, intValue < 60 ? LocaleController.formatPluralString("Seconds", intValue, new Object[0]) : LocaleController.formatPluralString("Minutes", intValue / 60, new Object[0])));
            }
        }

        public void lambda$run$9(TLRPC.TL_error tL_error, TLObject tLObject, boolean z) {
            if (tL_error == null) {
                PassportActivity.this.currentPassword = (TLRPC.account_Password) tLObject;
                TwoStepVerificationActivity.initPasswordNewAlgo(PassportActivity.this.currentPassword);
                PassportActivity.this.onPasswordDone(z);
            }
        }

        private void openRequestInterface() {
            if (PassportActivity.this.inputFields == null) {
                return;
            }
            if (!this.val$saved) {
                UserConfig.getInstance(((BaseFragment) PassportActivity.this).currentAccount).savePassword(this.val$x_bytes, PassportActivity.this.saltedPassword);
            }
            AndroidUtilities.hideKeyboard(PassportActivity.this.inputFields[0]);
            PassportActivity.this.ignoreOnFailure = true;
            PassportActivity passportActivity = new PassportActivity(PassportActivity.this.currentBotId == 0 ? 8 : 0, PassportActivity.this.currentBotId, PassportActivity.this.currentScope, PassportActivity.this.currentPublicKey, PassportActivity.this.currentPayload, PassportActivity.this.currentNonce, PassportActivity.this.currentCallbackUrl, PassportActivity.this.currentForm, PassportActivity.this.currentPassword);
            passportActivity.currentEmail = PassportActivity.this.currentEmail;
            ((BaseFragment) passportActivity).currentAccount = ((BaseFragment) PassportActivity.this).currentAccount;
            passportActivity.saltedPassword = PassportActivity.this.saltedPassword;
            passportActivity.secureSecret = PassportActivity.this.secureSecret;
            passportActivity.secureSecretId = PassportActivity.this.secureSecretId;
            passportActivity.needActivityResult = PassportActivity.this.needActivityResult;
            if (((BaseFragment) PassportActivity.this).parentLayout == null || !((BaseFragment) PassportActivity.this).parentLayout.checkTransitionAnimation()) {
                PassportActivity.this.presentFragment(passportActivity, true);
            } else {
                PassportActivity.this.presentAfterAnimation = passportActivity;
            }
        }

        private void resetSecret() {
            TLRPC.TL_account_updatePasswordSettings tL_account_updatePasswordSettings = new TLRPC.TL_account_updatePasswordSettings();
            if (PassportActivity.this.currentPassword.current_algo instanceof TLRPC.TL_passwordKdfAlgoSHA256SHA256PBKDF2HMACSHA512iter100000SHA256ModPow) {
                tL_account_updatePasswordSettings.password = SRPHelper.startCheck(this.val$x_bytes, PassportActivity.this.currentPassword.srp_id, PassportActivity.this.currentPassword.srp_B, (TLRPC.TL_passwordKdfAlgoSHA256SHA256PBKDF2HMACSHA512iter100000SHA256ModPow) PassportActivity.this.currentPassword.current_algo);
            }
            TLRPC.TL_account_passwordInputSettings tL_account_passwordInputSettings = new TLRPC.TL_account_passwordInputSettings();
            tL_account_updatePasswordSettings.new_settings = tL_account_passwordInputSettings;
            tL_account_passwordInputSettings.new_secure_settings = new TLRPC.TL_secureSecretSettings();
            TLRPC.TL_secureSecretSettings tL_secureSecretSettings = tL_account_updatePasswordSettings.new_settings.new_secure_settings;
            tL_secureSecretSettings.secure_secret = new byte[0];
            tL_secureSecretSettings.secure_algo = new TLRPC.TL_securePasswordKdfAlgoUnknown();
            TLRPC.TL_account_passwordInputSettings tL_account_passwordInputSettings2 = tL_account_updatePasswordSettings.new_settings;
            tL_account_passwordInputSettings2.new_secure_settings.secure_secret_id = 0L;
            tL_account_passwordInputSettings2.flags |= 4;
            ConnectionsManager.getInstance(((BaseFragment) PassportActivity.this).currentAccount).sendRequest(this.val$req, new RequestDelegate() {
                @Override
                public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                    PassportActivity.AnonymousClass8.this.lambda$resetSecret$3(tLObject, tL_error);
                }
            });
        }

        @Override
        public void run(final TLObject tLObject, final TLRPC.TL_error tL_error) {
            if (tL_error != null && "SRP_ID_INVALID".equals(tL_error.text)) {
                TLRPC.TL_account_getPassword tL_account_getPassword = new TLRPC.TL_account_getPassword();
                ConnectionsManager connectionsManager = ConnectionsManager.getInstance(((BaseFragment) PassportActivity.this).currentAccount);
                final boolean z = this.val$saved;
                connectionsManager.sendRequest(tL_account_getPassword, new RequestDelegate() {
                    @Override
                    public final void run(TLObject tLObject2, TLRPC.TL_error tL_error2) {
                        PassportActivity.AnonymousClass8.this.lambda$run$10(z, tLObject2, tL_error2);
                    }
                }, 8);
                return;
            }
            if (tL_error != null) {
                final boolean z2 = this.val$saved;
                AndroidUtilities.runOnUIThread(new Runnable() {
                    @Override
                    public final void run() {
                        PassportActivity.AnonymousClass8.this.lambda$run$16(z2, tL_error);
                    }
                });
            } else {
                DispatchQueue dispatchQueue = Utilities.globalQueue;
                final String str = this.val$textPassword;
                final boolean z3 = this.val$saved;
                dispatchQueue.postRunnable(new Runnable() {
                    @Override
                    public final void run() {
                        PassportActivity.AnonymousClass8.this.lambda$run$15(tLObject, str, z3);
                    }
                });
            }
        }
    }

    public static class EncryptionResult {
        byte[] decrypyedFileSecret;
        byte[] encryptedData;
        byte[] fileHash;
        byte[] fileSecret;
        SecureDocumentKey secureDocumentKey;

        public EncryptionResult(byte[] bArr, byte[] bArr2, byte[] bArr3, byte[] bArr4, byte[] bArr5, byte[] bArr6) {
            this.encryptedData = bArr;
            this.fileSecret = bArr2;
            this.fileHash = bArr4;
            this.decrypyedFileSecret = bArr3;
            this.secureDocumentKey = new SecureDocumentKey(bArr5, bArr6);
        }
    }

    public interface ErrorRunnable {
        void onError(String str, String str2);
    }

    public class LinkSpan extends ClickableSpan {
        public LinkSpan() {
        }

        @Override
        public void onClick(View view) {
            Browser.openUrl(PassportActivity.this.getParentActivity(), PassportActivity.this.currentForm.privacy_policy_url);
        }

        @Override
        public void updateDrawState(TextPaint textPaint) {
            super.updateDrawState(textPaint);
            textPaint.setUnderlineText(true);
            textPaint.setTypeface(AndroidUtilities.bold());
        }
    }

    public interface PassportActivityDelegate {
        void deleteValue(TLRPC.TL_secureRequiredType tL_secureRequiredType, TLRPC.TL_secureRequiredType tL_secureRequiredType2, ArrayList arrayList, boolean z, Runnable runnable, ErrorRunnable errorRunnable);

        SecureDocument saveFile(TLRPC.TL_secureFile tL_secureFile);

        void saveValue(TLRPC.TL_secureRequiredType tL_secureRequiredType, String str, String str2, TLRPC.TL_secureRequiredType tL_secureRequiredType2, String str3, ArrayList arrayList, SecureDocument secureDocument, ArrayList arrayList2, SecureDocument secureDocument2, SecureDocument secureDocument3, Runnable runnable, ErrorRunnable errorRunnable);
    }

    public class PhoneConfirmationView extends SlideView implements NotificationCenter.NotificationCenterDelegate {
        private ImageView blackImageView;
        private ImageView blueImageView;
        private EditTextBoldCursor[] codeField;
        private LinearLayout codeFieldContainer;
        private int codeTime;
        private Timer codeTimer;
        private TextView confirmTextView;
        private Bundle currentParams;
        private boolean ignoreOnTextChange;
        private double lastCodeTime;
        private double lastCurrentTime;
        private String lastError;
        private int length;
        private boolean nextPressed;
        private int nextType;
        private String pattern;
        private String phone;
        private String phoneHash;
        private TextView problemText;
        private ProgressView progressView;
        private int time;
        private TextView timeText;
        private Timer timeTimer;
        private int timeout;
        private final Object timerSync;
        private TextView titleTextView;
        private int verificationType;
        private boolean waitingForEvent;

        public class AnonymousClass4 extends TimerTask {
            AnonymousClass4() {
            }

            public void lambda$run$0() {
                double currentTimeMillis = System.currentTimeMillis();
                double d = PhoneConfirmationView.this.lastCodeTime;
                Double.isNaN(currentTimeMillis);
                PhoneConfirmationView.this.lastCodeTime = currentTimeMillis;
                PhoneConfirmationView.access$9926(PhoneConfirmationView.this, currentTimeMillis - d);
                if (PhoneConfirmationView.this.codeTime <= 1000) {
                    PhoneConfirmationView.this.problemText.setVisibility(0);
                    PhoneConfirmationView.this.timeText.setVisibility(8);
                    PhoneConfirmationView.this.destroyCodeTimer();
                }
            }

            @Override
            public void run() {
                AndroidUtilities.runOnUIThread(new Runnable() {
                    @Override
                    public final void run() {
                        PassportActivity.PhoneConfirmationView.AnonymousClass4.this.lambda$run$0();
                    }
                });
            }
        }

        public class AnonymousClass5 extends TimerTask {
            AnonymousClass5() {
            }

            public void lambda$run$0(TLRPC.TL_error tL_error) {
                PhoneConfirmationView.this.lastError = tL_error.text;
            }

            public void lambda$run$1(TLObject tLObject, final TLRPC.TL_error tL_error) {
                if (tL_error == null || tL_error.text == null) {
                    return;
                }
                AndroidUtilities.runOnUIThread(new Runnable() {
                    @Override
                    public final void run() {
                        PassportActivity.PhoneConfirmationView.AnonymousClass5.this.lambda$run$0(tL_error);
                    }
                });
            }

            public void lambda$run$2() {
                throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.PassportActivity.PhoneConfirmationView.AnonymousClass5.lambda$run$2():void");
            }

            @Override
            public void run() {
                if (PhoneConfirmationView.this.timeTimer == null) {
                    return;
                }
                double currentTimeMillis = System.currentTimeMillis();
                double d = PhoneConfirmationView.this.lastCurrentTime;
                Double.isNaN(currentTimeMillis);
                PhoneConfirmationView.access$10526(PhoneConfirmationView.this, currentTimeMillis - d);
                PhoneConfirmationView.this.lastCurrentTime = currentTimeMillis;
                AndroidUtilities.runOnUIThread(new Runnable() {
                    @Override
                    public final void run() {
                        PassportActivity.PhoneConfirmationView.AnonymousClass5.this.lambda$run$2();
                    }
                });
            }
        }

        public PhoneConfirmationView(Context context, int i) {
            super(context);
            TextView textView;
            int i2;
            View view;
            ViewGroup.LayoutParams createLinear;
            TextView textView2;
            int i3;
            FrameLayout.LayoutParams createFrame;
            View view2;
            this.timerSync = new Object();
            this.time = 60000;
            this.codeTime = 15000;
            this.lastError = "";
            this.pattern = "*";
            this.verificationType = i;
            setOrientation(1);
            TextView textView3 = new TextView(context);
            this.confirmTextView = textView3;
            int i4 = Theme.key_windowBackgroundWhiteGrayText6;
            textView3.setTextColor(Theme.getColor(i4));
            this.confirmTextView.setTextSize(1, 14.0f);
            this.confirmTextView.setLineSpacing(AndroidUtilities.dp(2.0f), 1.0f);
            TextView textView4 = new TextView(context);
            this.titleTextView = textView4;
            int i5 = Theme.key_windowBackgroundWhiteBlackText;
            textView4.setTextColor(Theme.getColor(i5));
            this.titleTextView.setTextSize(1, 18.0f);
            this.titleTextView.setTypeface(AndroidUtilities.bold());
            this.titleTextView.setGravity(LocaleController.isRTL ? 5 : 3);
            this.titleTextView.setLineSpacing(AndroidUtilities.dp(2.0f), 1.0f);
            this.titleTextView.setGravity(49);
            if (this.verificationType == 3) {
                this.confirmTextView.setGravity((LocaleController.isRTL ? 5 : 3) | 48);
                FrameLayout frameLayout = new FrameLayout(context);
                addView(frameLayout, LayoutHelper.createLinear(-2, -2, LocaleController.isRTL ? 5 : 3));
                ImageView imageView = new ImageView(context);
                imageView.setImageResource(R.drawable.phone_activate);
                boolean z = LocaleController.isRTL;
                if (z) {
                    frameLayout.addView(imageView, LayoutHelper.createFrame(64, 76.0f, 19, 2.0f, 2.0f, 0.0f, 0.0f));
                    View view3 = this.confirmTextView;
                    createFrame = LayoutHelper.createFrame(-1, -2.0f, LocaleController.isRTL ? 5 : 3, 82.0f, 0.0f, 0.0f, 0.0f);
                    view2 = view3;
                } else {
                    frameLayout.addView(this.confirmTextView, LayoutHelper.createFrame(-1, -2.0f, z ? 5 : 3, 0.0f, 0.0f, 82.0f, 0.0f));
                    createFrame = LayoutHelper.createFrame(64, 76.0f, 21, 0.0f, 2.0f, 0.0f, 2.0f);
                    view2 = imageView;
                }
                frameLayout.addView(view2, createFrame);
            } else {
                this.confirmTextView.setGravity(49);
                FrameLayout frameLayout2 = new FrameLayout(context);
                addView(frameLayout2, LayoutHelper.createLinear(-2, -2, 49));
                if (this.verificationType == 1) {
                    ImageView imageView2 = new ImageView(context);
                    this.blackImageView = imageView2;
                    imageView2.setImageResource(R.drawable.sms_devices);
                    ImageView imageView3 = this.blackImageView;
                    int color = Theme.getColor(i5);
                    PorterDuff.Mode mode = PorterDuff.Mode.MULTIPLY;
                    imageView3.setColorFilter(new PorterDuffColorFilter(color, mode));
                    frameLayout2.addView(this.blackImageView, LayoutHelper.createFrame(-2, -2.0f, 51, 0.0f, 0.0f, 0.0f, 0.0f));
                    ImageView imageView4 = new ImageView(context);
                    this.blueImageView = imageView4;
                    imageView4.setImageResource(R.drawable.sms_bubble);
                    this.blueImageView.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_chats_actionBackground), mode));
                    frameLayout2.addView(this.blueImageView, LayoutHelper.createFrame(-2, -2.0f, 51, 0.0f, 0.0f, 0.0f, 0.0f));
                    textView = this.titleTextView;
                    i2 = R.string.SentAppCodeTitle;
                } else {
                    ImageView imageView5 = new ImageView(context);
                    this.blueImageView = imageView5;
                    imageView5.setImageResource(R.drawable.sms_code);
                    this.blueImageView.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_chats_actionBackground), PorterDuff.Mode.MULTIPLY));
                    frameLayout2.addView(this.blueImageView, LayoutHelper.createFrame(-2, -2.0f, 51, 0.0f, 0.0f, 0.0f, 0.0f));
                    textView = this.titleTextView;
                    i2 = R.string.SentSmsCodeTitle;
                }
                textView.setText(LocaleController.getString(i2));
                addView(this.titleTextView, LayoutHelper.createLinear(-2, -2, 49, 0, 18, 0, 0));
                addView(this.confirmTextView, LayoutHelper.createLinear(-2, -2, 49, 0, 17, 0, 0));
            }
            LinearLayout linearLayout = new LinearLayout(context);
            this.codeFieldContainer = linearLayout;
            linearLayout.setOrientation(0);
            addView(this.codeFieldContainer, LayoutHelper.createLinear(-2, 36, 1));
            if (this.verificationType == 3) {
                this.codeFieldContainer.setVisibility(8);
            }
            TextView textView5 = new TextView(context) {
                @Override
                protected void onMeasure(int i6, int i7) {
                    super.onMeasure(i6, View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(100.0f), Integer.MIN_VALUE));
                }
            };
            this.timeText = textView5;
            textView5.setTextColor(Theme.getColor(i4));
            this.timeText.setLineSpacing(AndroidUtilities.dp(2.0f), 1.0f);
            if (this.verificationType == 3) {
                this.timeText.setTextSize(1, 14.0f);
                addView(this.timeText, LayoutHelper.createLinear(-2, -2, LocaleController.isRTL ? 5 : 3));
                this.progressView = new ProgressView(context);
                this.timeText.setGravity(LocaleController.isRTL ? 5 : 3);
                view = this.progressView;
                createLinear = LayoutHelper.createLinear(-1, 3, 0.0f, 12.0f, 0.0f, 0.0f);
            } else {
                this.timeText.setPadding(0, AndroidUtilities.dp(2.0f), 0, AndroidUtilities.dp(10.0f));
                this.timeText.setTextSize(1, 15.0f);
                this.timeText.setGravity(49);
                view = this.timeText;
                createLinear = LayoutHelper.createLinear(-2, -2, 49);
            }
            addView(view, createLinear);
            TextView textView6 = new TextView(context) {
                @Override
                protected void onMeasure(int i6, int i7) {
                    super.onMeasure(i6, View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(100.0f), Integer.MIN_VALUE));
                }
            };
            this.problemText = textView6;
            textView6.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlueText4));
            this.problemText.setLineSpacing(AndroidUtilities.dp(2.0f), 1.0f);
            this.problemText.setPadding(0, AndroidUtilities.dp(2.0f), 0, AndroidUtilities.dp(10.0f));
            this.problemText.setTextSize(1, 15.0f);
            this.problemText.setGravity(49);
            if (this.verificationType == 1) {
                textView2 = this.problemText;
                i3 = R.string.DidNotGetTheCodeSms;
            } else {
                textView2 = this.problemText;
                i3 = R.string.DidNotGetTheCode;
            }
            textView2.setText(LocaleController.getString(i3));
            addView(this.problemText, LayoutHelper.createLinear(-2, -2, 49));
            this.problemText.setOnClickListener(new View.OnClickListener() {
                @Override
                public final void onClick(View view4) {
                    PassportActivity.PhoneConfirmationView.this.lambda$new$0(view4);
                }
            });
        }

        static int access$10526(PhoneConfirmationView phoneConfirmationView, double d) {
            double d2 = phoneConfirmationView.time;
            Double.isNaN(d2);
            int i = (int) (d2 - d);
            phoneConfirmationView.time = i;
            return i;
        }

        static int access$9926(PhoneConfirmationView phoneConfirmationView, double d) {
            double d2 = phoneConfirmationView.codeTime;
            Double.isNaN(d2);
            int i = (int) (d2 - d);
            phoneConfirmationView.codeTime = i;
            return i;
        }

        public void createCodeTimer() {
            if (this.codeTimer != null) {
                return;
            }
            this.codeTime = 15000;
            this.codeTimer = new Timer();
            this.lastCodeTime = System.currentTimeMillis();
            this.codeTimer.schedule(new AnonymousClass4(), 0L, 1000L);
        }

        private void createTimer() {
            if (this.timeTimer != null) {
                return;
            }
            Timer timer = new Timer();
            this.timeTimer = timer;
            timer.schedule(new AnonymousClass5(), 0L, 1000L);
        }

        public void destroyCodeTimer() {
            try {
                synchronized (this.timerSync) {
                    try {
                        Timer timer = this.codeTimer;
                        if (timer != null) {
                            timer.cancel();
                            this.codeTimer = null;
                        }
                    } finally {
                    }
                }
            } catch (Exception e) {
                FileLog.e(e);
            }
        }

        public void destroyTimer() {
            try {
                synchronized (this.timerSync) {
                    try {
                        Timer timer = this.timeTimer;
                        if (timer != null) {
                            timer.cancel();
                            this.timeTimer = null;
                        }
                    } finally {
                    }
                }
            } catch (Exception e) {
                FileLog.e(e);
            }
        }

        public String getCode() {
            if (this.codeField == null) {
                return "";
            }
            StringBuilder sb = new StringBuilder();
            int i = 0;
            while (true) {
                EditTextBoldCursor[] editTextBoldCursorArr = this.codeField;
                if (i >= editTextBoldCursorArr.length) {
                    return sb.toString();
                }
                sb.append(PhoneFormat.stripExceptNumbers(editTextBoldCursorArr[i].getText().toString()));
                i++;
            }
        }

        public void lambda$new$0(View view) {
            if (this.nextPressed) {
                return;
            }
            int i = this.nextType;
            if ((i != 4 || this.verificationType != 2) && i != 0) {
                resendCode();
                return;
            }
            try {
                PackageInfo packageInfo = ApplicationLoader.applicationContext.getPackageManager().getPackageInfo(ApplicationLoader.applicationContext.getPackageName(), 0);
                String format = String.format(Locale.US, "%s (%d)", packageInfo.versionName, Integer.valueOf(packageInfo.versionCode));
                Intent intent = new Intent("android.intent.action.SENDTO");
                intent.setData(Uri.parse("mailto:"));
                intent.putExtra("android.intent.extra.EMAIL", new String[]{"sms@telegram.org"});
                intent.putExtra("android.intent.extra.SUBJECT", "Android registration/login issue " + format + " " + this.phone);
                intent.putExtra("android.intent.extra.TEXT", "Phone: " + this.phone + "\nApp version: " + format + "\nOS version: SDK " + Build.VERSION.SDK_INT + "\nDevice Name: " + Build.MANUFACTURER + Build.MODEL + "\nLocale: " + Locale.getDefault() + "\nError: " + this.lastError);
                getContext().startActivity(Intent.createChooser(intent, "Send email..."));
            } catch (Exception unused) {
                AlertsCreator.showSimpleAlert(PassportActivity.this, LocaleController.getString(R.string.NoMailInstalled));
            }
        }

        public void lambda$onBackPressed$8(DialogInterface dialogInterface, int i) {
            onBackPressed(true);
            PassportActivity.this.setPage(0, true, null);
        }

        public static void lambda$onBackPressed$9(TLObject tLObject, TLRPC.TL_error tL_error) {
        }

        public void lambda$onNextPressed$6(org.telegram.tgnet.TLRPC.TL_error r14, org.telegram.tgnet.TLRPC.TL_account_verifyPhone r15) {
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.PassportActivity.PhoneConfirmationView.lambda$onNextPressed$6(org.telegram.tgnet.TLRPC$TL_error, org.telegram.tgnet.TLRPC$TL_account_verifyPhone):void");
        }

        public void lambda$onNextPressed$7(final TLRPC.TL_account_verifyPhone tL_account_verifyPhone, TLObject tLObject, final TLRPC.TL_error tL_error) {
            AndroidUtilities.runOnUIThread(new Runnable() {
                @Override
                public final void run() {
                    PassportActivity.PhoneConfirmationView.this.lambda$onNextPressed$6(tL_error, tL_account_verifyPhone);
                }
            });
        }

        public void lambda$resendCode$1(DialogInterface dialogInterface, int i) {
            onBackPressed(true);
            PassportActivity.this.lambda$onBackPressed$321();
        }

        public void lambda$resendCode$2(TLRPC.TL_error tL_error, Bundle bundle, TLObject tLObject, TLRPC.TL_auth_resendCode tL_auth_resendCode) {
            this.nextPressed = false;
            if (tL_error == null) {
                PassportActivity.this.fillNextCodeParams(bundle, (TLRPC.TL_auth_sentCode) tLObject, true);
            } else {
                AlertDialog alertDialog = (AlertDialog) AlertsCreator.processError(((BaseFragment) PassportActivity.this).currentAccount, tL_error, PassportActivity.this, tL_auth_resendCode, new Object[0]);
                if (alertDialog != null && tL_error.text.contains("PHONE_CODE_EXPIRED")) {
                    alertDialog.setPositiveButtonListener(new DialogInterface.OnClickListener() {
                        @Override
                        public final void onClick(DialogInterface dialogInterface, int i) {
                            PassportActivity.PhoneConfirmationView.this.lambda$resendCode$1(dialogInterface, i);
                        }
                    });
                }
            }
            PassportActivity.this.needHideProgress();
        }

        public void lambda$resendCode$3(final Bundle bundle, final TLRPC.TL_auth_resendCode tL_auth_resendCode, final TLObject tLObject, final TLRPC.TL_error tL_error) {
            AndroidUtilities.runOnUIThread(new Runnable() {
                @Override
                public final void run() {
                    PassportActivity.PhoneConfirmationView.this.lambda$resendCode$2(tL_error, bundle, tLObject, tL_auth_resendCode);
                }
            });
        }

        public boolean lambda$setParams$4(int i, View view, int i2, KeyEvent keyEvent) {
            if (i2 != 67 || this.codeField[i].length() != 0 || i <= 0) {
                return false;
            }
            int i3 = i - 1;
            EditTextBoldCursor editTextBoldCursor = this.codeField[i3];
            editTextBoldCursor.setSelection(editTextBoldCursor.length());
            this.codeField[i3].requestFocus();
            this.codeField[i3].dispatchKeyEvent(keyEvent);
            return true;
        }

        public boolean lambda$setParams$5(TextView textView, int i, KeyEvent keyEvent) {
            if (i != 5) {
                return false;
            }
            lambda$onNextPressed$14(null);
            return true;
        }

        public void resendCode() {
            final Bundle bundle = new Bundle();
            bundle.putString("phone", this.phone);
            this.nextPressed = true;
            PassportActivity.this.needShowProgress();
            final TLRPC.TL_auth_resendCode tL_auth_resendCode = new TLRPC.TL_auth_resendCode();
            tL_auth_resendCode.phone_number = this.phone;
            tL_auth_resendCode.phone_code_hash = this.phoneHash;
            ConnectionsManager.getInstance(((BaseFragment) PassportActivity.this).currentAccount).sendRequest(tL_auth_resendCode, new RequestDelegate() {
                @Override
                public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                    PassportActivity.PhoneConfirmationView.this.lambda$resendCode$3(bundle, tL_auth_resendCode, tLObject, tL_error);
                }
            }, 2);
        }

        @Override
        public void didReceivedNotification(int i, int i2, Object... objArr) {
            EditTextBoldCursor[] editTextBoldCursorArr;
            if (!this.waitingForEvent || (editTextBoldCursorArr = this.codeField) == null) {
                return;
            }
            if (i == NotificationCenter.didReceiveSmsCode) {
                editTextBoldCursorArr[0].setText("" + objArr[0]);
            } else {
                if (i != NotificationCenter.didReceiveCall) {
                    return;
                }
                String str = "" + objArr[0];
                if (!AndroidUtilities.checkPhonePattern(this.pattern, str)) {
                    return;
                }
                this.ignoreOnTextChange = true;
                this.codeField[0].setText(str);
                this.ignoreOnTextChange = false;
            }
            lambda$onNextPressed$14(null);
        }

        @Override
        public boolean needBackButton() {
            return true;
        }

        @Override
        public boolean onBackPressed(boolean z) {
            NotificationCenter globalInstance;
            int i;
            if (!z) {
                AlertDialog.Builder builder = new AlertDialog.Builder(PassportActivity.this.getParentActivity());
                builder.setTitle(LocaleController.getString(R.string.AppName));
                builder.setMessage(LocaleController.getString(R.string.StopVerification));
                builder.setPositiveButton(LocaleController.getString(R.string.Continue), null);
                builder.setNegativeButton(LocaleController.getString(R.string.Stop), new DialogInterface.OnClickListener() {
                    @Override
                    public final void onClick(DialogInterface dialogInterface, int i2) {
                        PassportActivity.PhoneConfirmationView.this.lambda$onBackPressed$8(dialogInterface, i2);
                    }
                });
                PassportActivity.this.showDialog(builder.create());
                return false;
            }
            TLRPC.TL_auth_cancelCode tL_auth_cancelCode = new TLRPC.TL_auth_cancelCode();
            tL_auth_cancelCode.phone_number = this.phone;
            tL_auth_cancelCode.phone_code_hash = this.phoneHash;
            ConnectionsManager.getInstance(((BaseFragment) PassportActivity.this).currentAccount).sendRequest(tL_auth_cancelCode, new RequestDelegate() {
                @Override
                public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                    PassportActivity.PhoneConfirmationView.lambda$onBackPressed$9(tLObject, tL_error);
                }
            }, 2);
            destroyTimer();
            destroyCodeTimer();
            this.currentParams = null;
            int i2 = this.verificationType;
            if (i2 != 2) {
                if (i2 == 3) {
                    AndroidUtilities.setWaitingForCall(false);
                    globalInstance = NotificationCenter.getGlobalInstance();
                    i = NotificationCenter.didReceiveCall;
                }
                this.waitingForEvent = false;
                return true;
            }
            AndroidUtilities.setWaitingForSms(false);
            globalInstance = NotificationCenter.getGlobalInstance();
            i = NotificationCenter.didReceiveSmsCode;
            globalInstance.removeObserver(this, i);
            this.waitingForEvent = false;
            return true;
        }

        @Override
        public void onCancelPressed() {
            this.nextPressed = false;
        }

        @Override
        public void onDestroyActivity() {
            NotificationCenter globalInstance;
            int i;
            super.onDestroyActivity();
            int i2 = this.verificationType;
            if (i2 != 2) {
                if (i2 == 3) {
                    AndroidUtilities.setWaitingForCall(false);
                    globalInstance = NotificationCenter.getGlobalInstance();
                    i = NotificationCenter.didReceiveCall;
                }
                this.waitingForEvent = false;
                destroyTimer();
                destroyCodeTimer();
            }
            AndroidUtilities.setWaitingForSms(false);
            globalInstance = NotificationCenter.getGlobalInstance();
            i = NotificationCenter.didReceiveSmsCode;
            globalInstance.removeObserver(this, i);
            this.waitingForEvent = false;
            destroyTimer();
            destroyCodeTimer();
        }

        @Override
        protected void onLayout(boolean z, int i, int i2, int i3, int i4) {
            int i5;
            int measuredHeight;
            TextView textView;
            int left;
            TextView textView2;
            super.onLayout(z, i, i2, i3, i4);
            if (this.verificationType == 3 || this.blueImageView == null) {
                return;
            }
            int bottom = this.confirmTextView.getBottom();
            int measuredHeight2 = getMeasuredHeight() - bottom;
            if (this.problemText.getVisibility() == 0) {
                measuredHeight = this.problemText.getMeasuredHeight();
                i5 = (measuredHeight2 + bottom) - measuredHeight;
                textView = this.problemText;
                left = textView.getLeft();
                textView2 = this.problemText;
            } else {
                if (this.timeText.getVisibility() != 0) {
                    i5 = measuredHeight2 + bottom;
                    int measuredHeight3 = this.codeFieldContainer.getMeasuredHeight();
                    int i6 = (((i5 - bottom) - measuredHeight3) / 2) + bottom;
                    LinearLayout linearLayout = this.codeFieldContainer;
                    linearLayout.layout(linearLayout.getLeft(), i6, this.codeFieldContainer.getRight(), measuredHeight3 + i6);
                }
                measuredHeight = this.timeText.getMeasuredHeight();
                i5 = (measuredHeight2 + bottom) - measuredHeight;
                textView = this.timeText;
                left = textView.getLeft();
                textView2 = this.timeText;
            }
            textView.layout(left, i5, textView2.getRight(), measuredHeight + i5);
            int measuredHeight32 = this.codeFieldContainer.getMeasuredHeight();
            int i62 = (((i5 - bottom) - measuredHeight32) / 2) + bottom;
            LinearLayout linearLayout2 = this.codeFieldContainer;
            linearLayout2.layout(linearLayout2.getLeft(), i62, this.codeFieldContainer.getRight(), measuredHeight32 + i62);
        }

        @Override
        protected void onMeasure(int i, int i2) {
            ImageView imageView;
            super.onMeasure(i, i2);
            if (this.verificationType == 3 || (imageView = this.blueImageView) == null) {
                return;
            }
            int measuredHeight = imageView.getMeasuredHeight() + this.titleTextView.getMeasuredHeight() + this.confirmTextView.getMeasuredHeight() + AndroidUtilities.dp(35.0f);
            int dp = AndroidUtilities.dp(80.0f);
            int dp2 = AndroidUtilities.dp(291.0f);
            if (PassportActivity.this.scrollHeight - measuredHeight < dp) {
                setMeasuredDimension(getMeasuredWidth(), measuredHeight + dp);
            } else {
                setMeasuredDimension(getMeasuredWidth(), Math.min(PassportActivity.this.scrollHeight, dp2));
            }
        }

        @Override
        public void lambda$onNextPressed$14(String str) {
            NotificationCenter globalInstance;
            int i;
            if (this.nextPressed) {
                return;
            }
            if (str == null) {
                str = getCode();
            }
            if (TextUtils.isEmpty(str)) {
                AndroidUtilities.shakeView(this.codeFieldContainer);
                return;
            }
            this.nextPressed = true;
            int i2 = this.verificationType;
            if (i2 != 2) {
                if (i2 == 3) {
                    AndroidUtilities.setWaitingForCall(false);
                    globalInstance = NotificationCenter.getGlobalInstance();
                    i = NotificationCenter.didReceiveCall;
                }
                this.waitingForEvent = false;
                PassportActivity.this.showEditDoneProgress(true, true);
                final TLRPC.TL_account_verifyPhone tL_account_verifyPhone = new TLRPC.TL_account_verifyPhone();
                tL_account_verifyPhone.phone_number = this.phone;
                tL_account_verifyPhone.phone_code = str;
                tL_account_verifyPhone.phone_code_hash = this.phoneHash;
                destroyTimer();
                PassportActivity.this.needShowProgress();
                ConnectionsManager.getInstance(((BaseFragment) PassportActivity.this).currentAccount).sendRequest(tL_account_verifyPhone, new RequestDelegate() {
                    @Override
                    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                        PassportActivity.PhoneConfirmationView.this.lambda$onNextPressed$7(tL_account_verifyPhone, tLObject, tL_error);
                    }
                }, 2);
            }
            AndroidUtilities.setWaitingForSms(false);
            globalInstance = NotificationCenter.getGlobalInstance();
            i = NotificationCenter.didReceiveSmsCode;
            globalInstance.removeObserver(this, i);
            this.waitingForEvent = false;
            PassportActivity.this.showEditDoneProgress(true, true);
            final TLRPC.TL_account_verifyPhone tL_account_verifyPhone2 = new TLRPC.TL_account_verifyPhone();
            tL_account_verifyPhone2.phone_number = this.phone;
            tL_account_verifyPhone2.phone_code = str;
            tL_account_verifyPhone2.phone_code_hash = this.phoneHash;
            destroyTimer();
            PassportActivity.this.needShowProgress();
            ConnectionsManager.getInstance(((BaseFragment) PassportActivity.this).currentAccount).sendRequest(tL_account_verifyPhone2, new RequestDelegate() {
                @Override
                public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                    PassportActivity.PhoneConfirmationView.this.lambda$onNextPressed$7(tL_account_verifyPhone2, tLObject, tL_error);
                }
            }, 2);
        }

        @Override
        public void onShow() {
            super.onShow();
            LinearLayout linearLayout = this.codeFieldContainer;
            if (linearLayout == null || linearLayout.getVisibility() != 0) {
                return;
            }
            for (int length = this.codeField.length - 1; length >= 0; length--) {
                if (length == 0 || this.codeField[length].length() != 0) {
                    this.codeField[length].requestFocus();
                    EditTextBoldCursor editTextBoldCursor = this.codeField[length];
                    editTextBoldCursor.setSelection(editTextBoldCursor.length());
                    AndroidUtilities.showKeyboard(this.codeField[length]);
                    return;
                }
            }
        }

        @Override
        public void setParams(android.os.Bundle r19, boolean r20) {
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.PassportActivity.PhoneConfirmationView.setParams(android.os.Bundle, boolean):void");
        }
    }

    public static class ProgressView extends View {
        private Paint paint;
        private Paint paint2;
        private float progress;

        public ProgressView(Context context) {
            super(context);
            this.paint = new Paint();
            this.paint2 = new Paint();
            this.paint.setColor(Theme.getColor(Theme.key_login_progressInner));
            this.paint2.setColor(Theme.getColor(Theme.key_login_progressOuter));
        }

        @Override
        protected void onDraw(Canvas canvas) {
            float measuredWidth = (int) (getMeasuredWidth() * this.progress);
            canvas.drawRect(0.0f, 0.0f, measuredWidth, getMeasuredHeight(), this.paint2);
            canvas.drawRect(measuredWidth, 0.0f, getMeasuredWidth(), getMeasuredHeight(), this.paint);
        }

        public void setProgress(float f) {
            this.progress = f;
            invalidate();
        }
    }

    public class SecureDocumentCell extends FrameLayout implements DownloadController.FileDownloadProgressListener {
        private int TAG;
        private int buttonState;
        private SecureDocument currentSecureDocument;
        private BackupImageView imageView;
        private RadialProgress radialProgress;
        private TextView textView;
        private TextView valueTextView;

        public SecureDocumentCell(Context context) {
            super(context);
            this.TAG = DownloadController.getInstance(((BaseFragment) PassportActivity.this).currentAccount).generateObserverTag();
            this.radialProgress = new RadialProgress(this);
            BackupImageView backupImageView = new BackupImageView(context);
            this.imageView = backupImageView;
            addView(backupImageView, LayoutHelper.createFrame(48, 48.0f, (LocaleController.isRTL ? 5 : 3) | 48, 21.0f, 8.0f, 21.0f, 0.0f));
            TextView textView = new TextView(context);
            this.textView = textView;
            textView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
            this.textView.setTextSize(1, 16.0f);
            this.textView.setLines(1);
            this.textView.setMaxLines(1);
            this.textView.setSingleLine(true);
            this.textView.setEllipsize(TextUtils.TruncateAt.END);
            this.textView.setGravity((LocaleController.isRTL ? 5 : 3) | 16);
            View view = this.textView;
            boolean z = LocaleController.isRTL;
            addView(view, LayoutHelper.createFrame(-2, -2.0f, (z ? 5 : 3) | 48, z ? 21 : 81, 10.0f, z ? 81 : 21, 0.0f));
            TextView textView2 = new TextView(context);
            this.valueTextView = textView2;
            textView2.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteGrayText2));
            this.valueTextView.setTextSize(1, 13.0f);
            this.valueTextView.setGravity(LocaleController.isRTL ? 5 : 3);
            this.valueTextView.setLines(1);
            this.valueTextView.setMaxLines(1);
            this.valueTextView.setSingleLine(true);
            this.valueTextView.setPadding(0, 0, 0, 0);
            View view2 = this.valueTextView;
            boolean z2 = LocaleController.isRTL;
            addView(view2, LayoutHelper.createFrame(-2, -2.0f, (z2 ? 5 : 3) | 48, z2 ? 21 : 81, 35.0f, z2 ? 81 : 21, 0.0f));
            setWillNotDraw(false);
        }

        @Override
        protected boolean drawChild(Canvas canvas, View view, long j) {
            boolean drawChild = super.drawChild(canvas, view, j);
            if (view == this.imageView) {
                this.radialProgress.draw(canvas);
            }
            return drawChild;
        }

        @Override
        public int getObserverTag() {
            return this.TAG;
        }

        @Override
        public void invalidate() {
            super.invalidate();
            this.textView.invalidate();
        }

        @Override
        protected void onDraw(Canvas canvas) {
            canvas.drawLine(LocaleController.isRTL ? 0.0f : AndroidUtilities.dp(20.0f), getMeasuredHeight() - 1, getMeasuredWidth() - (LocaleController.isRTL ? AndroidUtilities.dp(20.0f) : 0), getMeasuredHeight() - 1, Theme.dividerPaint);
        }

        @Override
        public void onFailedDownload(String str, boolean z) {
            updateButtonState(false);
        }

        @Override
        protected void onLayout(boolean z, int i, int i2, int i3, int i4) {
            super.onLayout(z, i, i2, i3, i4);
            int left = this.imageView.getLeft() + ((this.imageView.getMeasuredWidth() - AndroidUtilities.dp(24.0f)) / 2);
            int top = this.imageView.getTop() + ((this.imageView.getMeasuredHeight() - AndroidUtilities.dp(24.0f)) / 2);
            this.radialProgress.setProgressRect(left, top, AndroidUtilities.dp(24.0f) + left, AndroidUtilities.dp(24.0f) + top);
        }

        @Override
        protected void onMeasure(int i, int i2) {
            super.onMeasure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(i), 1073741824), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(64.0f) + 1, 1073741824));
        }

        @Override
        public void onProgressDownload(String str, long j, long j2) {
            this.radialProgress.setProgress(Math.min(1.0f, ((float) j) / ((float) j2)), true);
            if (this.buttonState != 1) {
                updateButtonState(false);
            }
        }

        @Override
        public void onProgressUpload(String str, long j, long j2, boolean z) {
            this.radialProgress.setProgress(Math.min(1.0f, ((float) j) / ((float) j2)), true);
        }

        @Override
        public void onSuccessDownload(String str) {
            this.radialProgress.setProgress(1.0f, true);
            updateButtonState(true);
        }

        public void setTextAndValueAndImage(String str, CharSequence charSequence, SecureDocument secureDocument) {
            this.textView.setText(str);
            this.valueTextView.setText(charSequence);
            this.imageView.setImage(secureDocument, "48_48");
            this.currentSecureDocument = secureDocument;
            updateButtonState(false);
        }

        public void setValue(CharSequence charSequence) {
            this.valueTextView.setText(charSequence);
        }

        public void updateButtonState(boolean z) {
            String attachFileName = FileLoader.getAttachFileName(this.currentSecureDocument);
            boolean exists = FileLoader.getInstance(UserConfig.selectedAccount).getPathToAttach(this.currentSecureDocument).exists();
            if (TextUtils.isEmpty(attachFileName)) {
                this.radialProgress.setBackground(null, false, false);
                return;
            }
            SecureDocument secureDocument = this.currentSecureDocument;
            if (secureDocument.path != null) {
                if (secureDocument.inputFile != null) {
                    DownloadController.getInstance(((BaseFragment) PassportActivity.this).currentAccount).removeLoadingFileObserver(this);
                    this.radialProgress.setBackground(null, false, z);
                    this.buttonState = -1;
                    return;
                } else {
                    DownloadController.getInstance(((BaseFragment) PassportActivity.this).currentAccount).addLoadingFileObserver(this.currentSecureDocument.path, this);
                    this.buttonState = 1;
                    Float fileProgress = ImageLoader.getInstance().getFileProgress(this.currentSecureDocument.path);
                    this.radialProgress.setBackground(getResources().getDrawable(R.drawable.circle), true, z);
                    this.radialProgress.setProgress(fileProgress != null ? fileProgress.floatValue() : 0.0f, false);
                }
            } else if (exists) {
                DownloadController.getInstance(((BaseFragment) PassportActivity.this).currentAccount).removeLoadingFileObserver(this);
                this.buttonState = -1;
                this.radialProgress.setBackground(null, false, z);
            } else {
                DownloadController.getInstance(((BaseFragment) PassportActivity.this).currentAccount).addLoadingFileObserver(attachFileName, this);
                this.buttonState = 1;
                Float fileProgress2 = ImageLoader.getInstance().getFileProgress(attachFileName);
                this.radialProgress.setBackground(getResources().getDrawable(R.drawable.circle), true, z);
                this.radialProgress.setProgress(fileProgress2 != null ? fileProgress2.floatValue() : 0.0f, z);
            }
            invalidate();
        }
    }

    public class TextDetailSecureCell extends FrameLayout {
        private ImageView checkImageView;
        private boolean needDivider;
        private TextView textView;
        private TextView valueTextView;

        public TextDetailSecureCell(Context context) {
            super(context);
            int i = PassportActivity.this.currentActivityType == 8 ? 21 : 51;
            TextView textView = new TextView(context);
            this.textView = textView;
            textView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
            this.textView.setTextSize(1, 16.0f);
            this.textView.setLines(1);
            this.textView.setMaxLines(1);
            this.textView.setSingleLine(true);
            TextView textView2 = this.textView;
            TextUtils.TruncateAt truncateAt = TextUtils.TruncateAt.END;
            textView2.setEllipsize(truncateAt);
            this.textView.setGravity((LocaleController.isRTL ? 5 : 3) | 16);
            TextView textView3 = this.textView;
            boolean z = LocaleController.isRTL;
            addView(textView3, LayoutHelper.createFrame(-2, -2.0f, (z ? 5 : 3) | 48, z ? i : 21, 10.0f, z ? 21 : i, 0.0f));
            TextView textView4 = new TextView(context);
            this.valueTextView = textView4;
            textView4.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteGrayText2));
            this.valueTextView.setTextSize(1, 13.0f);
            this.valueTextView.setGravity(LocaleController.isRTL ? 5 : 3);
            this.valueTextView.setLines(1);
            this.valueTextView.setMaxLines(1);
            this.valueTextView.setSingleLine(true);
            this.valueTextView.setEllipsize(truncateAt);
            this.valueTextView.setPadding(0, 0, 0, 0);
            TextView textView5 = this.valueTextView;
            boolean z2 = LocaleController.isRTL;
            addView(textView5, LayoutHelper.createFrame(-2, -2.0f, (z2 ? 5 : 3) | 48, z2 ? i : 21, 35.0f, z2 ? 21 : i, 0.0f));
            ImageView imageView = new ImageView(context);
            this.checkImageView = imageView;
            imageView.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_featuredStickers_addedIcon), PorterDuff.Mode.MULTIPLY));
            this.checkImageView.setImageResource(R.drawable.sticker_added);
            addView(this.checkImageView, LayoutHelper.createFrame(-2, -2.0f, (LocaleController.isRTL ? 3 : 5) | 48, 21.0f, 25.0f, 21.0f, 0.0f));
        }

        @Override
        protected void onDraw(Canvas canvas) {
            if (this.needDivider) {
                canvas.drawLine(LocaleController.isRTL ? 0.0f : AndroidUtilities.dp(20.0f), getMeasuredHeight() - 1, getMeasuredWidth() - (LocaleController.isRTL ? AndroidUtilities.dp(20.0f) : 0), getMeasuredHeight() - 1, Theme.dividerPaint);
            }
        }

        @Override
        protected void onMeasure(int i, int i2) {
            super.onMeasure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(i), 1073741824), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(64.0f) + (this.needDivider ? 1 : 0), 1073741824));
        }

        public void setChecked(boolean z) {
            this.checkImageView.setVisibility(z ? 0 : 4);
        }

        public void setNeedDivider(boolean z) {
            this.needDivider = z;
            setWillNotDraw(!z);
            invalidate();
        }

        public void setTextAndValue(String str, CharSequence charSequence, boolean z) {
            this.textView.setText(str);
            this.valueTextView.setText(charSequence);
            this.needDivider = z;
            setWillNotDraw(!z);
        }

        public void setValue(CharSequence charSequence) {
            this.valueTextView.setText(charSequence);
        }
    }

    public PassportActivity(int r19, long r20, java.lang.String r22, java.lang.String r23, java.lang.String r24, java.lang.String r25, java.lang.String r26, org.telegram.tgnet.TLRPC.TL_account_authorizationForm r27, org.telegram.tgnet.TLRPC.account_Password r28) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.PassportActivity.<init>(int, long, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, org.telegram.tgnet.TLRPC$TL_account_authorizationForm, org.telegram.tgnet.TLRPC$account_Password):void");
    }

    public PassportActivity(int i, TLRPC.TL_account_authorizationForm tL_account_authorizationForm, TLRPC.account_Password account_password, TLRPC.TL_secureRequiredType tL_secureRequiredType, TLRPC.TL_secureValue tL_secureValue, TLRPC.TL_secureRequiredType tL_secureRequiredType2, TLRPC.TL_secureValue tL_secureValue2, HashMap hashMap, HashMap hashMap2) {
        this.currentCitizeship = "";
        this.currentResidence = "";
        this.currentExpireDate = new int[3];
        this.dividers = new ArrayList();
        this.nonLatinNames = new boolean[3];
        this.allowNonLatinName = true;
        this.countriesArray = new ArrayList();
        this.countriesMap = new HashMap();
        this.codesMap = new HashMap();
        this.phoneFormatMap = new HashMap();
        this.documents = new ArrayList();
        this.translationDocuments = new ArrayList();
        this.documentsCells = new HashMap();
        this.uploadingDocuments = new HashMap();
        this.typesValues = new HashMap();
        this.typesViews = new HashMap();
        this.documentsToTypesLink = new HashMap();
        this.errorsMap = new HashMap();
        this.mainErrorsMap = new HashMap();
        this.errorsValues = new HashMap();
        this.provider = new PhotoViewer.EmptyPhotoViewerProvider() {
            @Override
            public void deleteImageAtIndex(int r8) {
                throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.PassportActivity.AnonymousClass1.deleteImageAtIndex(int):void");
            }

            @Override
            public String getDeleteMessageString() {
                return PassportActivity.this.uploadingFileType == 1 ? LocaleController.formatString("PassportDeleteSelfieAlert", R.string.PassportDeleteSelfieAlert, new Object[0]) : LocaleController.formatString("PassportDeleteScanAlert", R.string.PassportDeleteScanAlert, new Object[0]);
            }

            @Override
            public PhotoViewer.PlaceProviderObject getPlaceForPhoto(MessageObject messageObject, TLRPC.FileLocation fileLocation, int i2, boolean z) {
                if (i2 < 0 || i2 >= PassportActivity.this.currentPhotoViewerLayout.getChildCount()) {
                    return null;
                }
                SecureDocumentCell secureDocumentCell = (SecureDocumentCell) PassportActivity.this.currentPhotoViewerLayout.getChildAt(i2);
                int[] iArr = new int[2];
                secureDocumentCell.imageView.getLocationInWindow(iArr);
                PhotoViewer.PlaceProviderObject placeProviderObject = new PhotoViewer.PlaceProviderObject();
                placeProviderObject.viewX = iArr[0];
                placeProviderObject.viewY = iArr[1] - (Build.VERSION.SDK_INT < 21 ? AndroidUtilities.statusBarHeight : 0);
                placeProviderObject.parentView = PassportActivity.this.currentPhotoViewerLayout;
                ImageReceiver imageReceiver = secureDocumentCell.imageView.getImageReceiver();
                placeProviderObject.imageReceiver = imageReceiver;
                placeProviderObject.thumb = imageReceiver.getBitmapSafe();
                return placeProviderObject;
            }
        };
        this.currentActivityType = i;
        this.currentForm = tL_account_authorizationForm;
        this.currentType = tL_secureRequiredType;
        if (tL_secureRequiredType != null) {
            this.allowNonLatinName = tL_secureRequiredType.native_names;
        }
        this.currentTypeValue = tL_secureValue;
        this.currentDocumentsType = tL_secureRequiredType2;
        this.currentDocumentsTypeValue = tL_secureValue2;
        this.currentPassword = account_password;
        this.currentValues = hashMap;
        this.currentDocumentValues = hashMap2;
        if (i == 3) {
            this.permissionsItems = new ArrayList();
        } else if (i == 7) {
            this.views = new SlideView[3];
        }
        if (this.currentValues == null) {
            this.currentValues = new HashMap();
        }
        if (this.currentDocumentValues == null) {
            this.currentDocumentValues = new HashMap();
        }
        if (i == 5) {
            if (UserConfig.getInstance(this.currentAccount).savedPasswordHash != null && UserConfig.getInstance(this.currentAccount).savedSaltedPassword != null) {
                this.usingSavedPassword = 1;
                this.savedPasswordHash = UserConfig.getInstance(this.currentAccount).savedPasswordHash;
                this.savedSaltedPassword = UserConfig.getInstance(this.currentAccount).savedSaltedPassword;
            }
            TLRPC.account_Password account_password2 = this.currentPassword;
            if (account_password2 == null) {
                loadPasswordInfo();
            } else {
                TwoStepVerificationActivity.initPasswordNewAlgo(account_password2);
                if (this.usingSavedPassword == 1) {
                    onPasswordDone(true);
                }
            }
            if (SharedConfig.isPassportConfigLoaded()) {
                return;
            }
            TLRPC.TL_help_getPassportConfig tL_help_getPassportConfig = new TLRPC.TL_help_getPassportConfig();
            tL_help_getPassportConfig.hash = SharedConfig.passportConfigHash;
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(tL_help_getPassportConfig, new RequestDelegate() {
                @Override
                public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                    PassportActivity.lambda$new$1(tLObject, tL_error);
                }
            });
        }
    }

    private void addDocumentView(final SecureDocument secureDocument, final int i) {
        String string;
        StringBuilder sb;
        String str;
        String formatDateForBan;
        HashMap hashMap;
        if (i == 1) {
            this.selfieDocument = secureDocument;
            if (this.selfieLayout == null) {
                return;
            }
        } else if (i == 4) {
            this.translationDocuments.add(secureDocument);
            if (this.translationLayout == null) {
                return;
            }
        } else if (i == 2) {
            this.frontDocument = secureDocument;
            if (this.frontLayout == null) {
                return;
            }
        } else if (i == 3) {
            this.reverseDocument = secureDocument;
            if (this.reverseLayout == null) {
                return;
            }
        } else {
            this.documents.add(secureDocument);
            if (this.documentsLayout == null) {
                return;
            }
        }
        if (getParentActivity() == null) {
            return;
        }
        final SecureDocumentCell secureDocumentCell = new SecureDocumentCell(getParentActivity());
        secureDocumentCell.setTag(secureDocument);
        secureDocumentCell.setBackgroundDrawable(Theme.getSelectorDrawable(true));
        this.documentsCells.put(secureDocument, secureDocumentCell);
        String documentHash = getDocumentHash(secureDocument);
        if (i == 1) {
            string = LocaleController.getString(R.string.PassportSelfie);
            this.selfieLayout.addView(secureDocumentCell, LayoutHelper.createLinear(-1, -2));
            sb = new StringBuilder();
            str = "selfie";
        } else if (i == 4) {
            string = LocaleController.getString(R.string.AttachPhoto);
            this.translationLayout.addView(secureDocumentCell, LayoutHelper.createLinear(-1, -2));
            sb = new StringBuilder();
            str = "translation";
        } else if (i == 2) {
            TLRPC.SecureValueType secureValueType = this.currentDocumentsType.type;
            string = LocaleController.getString(((secureValueType instanceof TLRPC.TL_secureValueTypePassport) || (secureValueType instanceof TLRPC.TL_secureValueTypeInternalPassport)) ? R.string.PassportMainPage : R.string.PassportFrontSide);
            this.frontLayout.addView(secureDocumentCell, LayoutHelper.createLinear(-1, -2));
            sb = new StringBuilder();
            str = "front";
        } else if (i == 3) {
            string = LocaleController.getString(R.string.PassportReverseSide);
            this.reverseLayout.addView(secureDocumentCell, LayoutHelper.createLinear(-1, -2));
            sb = new StringBuilder();
            str = "reverse";
        } else {
            string = LocaleController.getString(R.string.AttachPhoto);
            this.documentsLayout.addView(secureDocumentCell, LayoutHelper.createLinear(-1, -2));
            sb = new StringBuilder();
            str = "files";
        }
        sb.append(str);
        sb.append(documentHash);
        final String sb2 = sb.toString();
        if (sb2 == null || (hashMap = this.documentsErrors) == null || (formatDateForBan = (String) hashMap.get(sb2)) == null) {
            formatDateForBan = LocaleController.formatDateForBan(secureDocument.secureFile.date);
        } else {
            secureDocumentCell.valueTextView.setTextColor(Theme.getColor(Theme.key_text_RedRegular));
            this.errorsValues.put(sb2, "");
        }
        secureDocumentCell.setTextAndValueAndImage(string, formatDateForBan, secureDocument);
        secureDocumentCell.setOnClickListener(new View.OnClickListener() {
            @Override
            public final void onClick(View view) {
                PassportActivity.this.lambda$addDocumentView$55(i, view);
            }
        });
        secureDocumentCell.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public final boolean onLongClick(View view) {
                boolean lambda$addDocumentView$57;
                lambda$addDocumentView$57 = PassportActivity.this.lambda$addDocumentView$57(i, secureDocument, secureDocumentCell, sb2, view);
                return lambda$addDocumentView$57;
            }
        });
    }

    private void addDocumentViewInternal(TLRPC.TL_secureFile tL_secureFile, int i) {
        addDocumentView(new SecureDocument(getSecureDocumentKey(tL_secureFile.secret, tL_secureFile.file_hash), tL_secureFile, null, null, null), i);
    }

    private void addDocumentViews(ArrayList arrayList) {
        this.documents.clear();
        int size = arrayList.size();
        for (int i = 0; i < size; i++) {
            TLRPC.SecureFile secureFile = (TLRPC.SecureFile) arrayList.get(i);
            if (secureFile instanceof TLRPC.TL_secureFile) {
                addDocumentViewInternal((TLRPC.TL_secureFile) secureFile, 0);
            }
        }
    }

    private org.telegram.ui.PassportActivity.TextDetailSecureCell addField(android.content.Context r17, final org.telegram.tgnet.TLRPC.TL_secureRequiredType r18, final java.util.ArrayList r19, final boolean r20, boolean r21) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.PassportActivity.addField(android.content.Context, org.telegram.tgnet.TLRPC$TL_secureRequiredType, java.util.ArrayList, boolean, boolean):org.telegram.ui.PassportActivity$TextDetailSecureCell");
    }

    private void addTranslationDocumentViews(ArrayList arrayList) {
        this.translationDocuments.clear();
        int size = arrayList.size();
        for (int i = 0; i < size; i++) {
            TLRPC.SecureFile secureFile = (TLRPC.SecureFile) arrayList.get(i);
            if (secureFile instanceof TLRPC.TL_secureFile) {
                addDocumentViewInternal((TLRPC.TL_secureFile) secureFile, 4);
            }
        }
    }

    public void callCallback(boolean z) {
        int i;
        int i2;
        Activity parentActivity;
        StringBuilder sb;
        String str;
        if (this.callbackCalled) {
            return;
        }
        if (!TextUtils.isEmpty(this.currentCallbackUrl)) {
            if (z) {
                parentActivity = getParentActivity();
                sb = new StringBuilder();
                sb.append(this.currentCallbackUrl);
                str = "&tg_passport=success";
            } else if (!this.ignoreOnFailure && ((i2 = this.currentActivityType) == 5 || i2 == 0)) {
                parentActivity = getParentActivity();
                sb = new StringBuilder();
                sb.append(this.currentCallbackUrl);
                str = "&tg_passport=cancel";
            }
            sb.append(str);
            Browser.openUrl(parentActivity, Uri.parse(sb.toString()));
        } else {
            if (!this.needActivityResult) {
                return;
            }
            if (z || (!this.ignoreOnFailure && ((i = this.currentActivityType) == 5 || i == 0))) {
                getParentActivity().setResult(z ? -1 : 0);
            }
        }
        this.callbackCalled = true;
    }

    public boolean checkDiscard() {
        if (isHasNotAnyChanges()) {
            return false;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(getParentActivity());
        builder.setPositiveButton(LocaleController.getString(R.string.PassportDiscard), new DialogInterface.OnClickListener() {
            @Override
            public final void onClick(DialogInterface dialogInterface, int i) {
                PassportActivity.this.lambda$checkDiscard$69(dialogInterface, i);
            }
        });
        builder.setNegativeButton(LocaleController.getString(R.string.Cancel), null);
        builder.setTitle(LocaleController.getString(R.string.DiscardChanges));
        builder.setMessage(LocaleController.getString(R.string.PassportDiscardChanges));
        showDialog(builder.create());
        return true;
    }

    public void checkFieldForError(org.telegram.ui.Components.EditTextBoldCursor r2, java.lang.String r3, android.text.Editable r4, boolean r5) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.PassportActivity.checkFieldForError(org.telegram.ui.Components.EditTextBoldCursor, java.lang.String, android.text.Editable, boolean):void");
    }

    public boolean checkFieldsForError() {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.PassportActivity.checkFieldsForError():boolean");
    }

    public void checkNativeFields(boolean z) {
        ShadowSectionCell shadowSectionCell;
        int i;
        Activity activity;
        EditTextBoldCursor editTextBoldCursor;
        String formatString;
        int i2;
        EditTextBoldCursor[] editTextBoldCursorArr;
        int i3 = 0;
        if (this.inputExtraFields == null) {
            return;
        }
        String str = (String) this.languageMap.get(this.currentResidence);
        String str2 = SharedConfig.getCountryLangs().get(this.currentResidence);
        if (!this.currentType.native_names || TextUtils.isEmpty(this.currentResidence) || "EN".equals(str2)) {
            if (this.nativeInfoCell.getVisibility() != 8) {
                this.nativeInfoCell.setVisibility(8);
                this.headerCell.setVisibility(8);
                this.extraBackgroundView2.setVisibility(8);
                while (true) {
                    EditTextBoldCursor[] editTextBoldCursorArr2 = this.inputExtraFields;
                    if (i3 >= editTextBoldCursorArr2.length) {
                        break;
                    }
                    ((View) editTextBoldCursorArr2[i3].getParent()).setVisibility(8);
                    i3++;
                }
                if (((this.currentBotId == 0 && this.currentDocumentsType != null) || this.currentTypeValue == null || this.documentOnly) && this.currentDocumentsTypeValue == null) {
                    shadowSectionCell = this.sectionCell2;
                    activity = getParentActivity();
                    i = R.drawable.greydivider_bottom;
                } else {
                    shadowSectionCell = this.sectionCell2;
                    activity = getParentActivity();
                    i = R.drawable.greydivider;
                }
                shadowSectionCell.setBackgroundDrawable(Theme.getThemedDrawableByKey(activity, i, Theme.key_windowBackgroundGrayShadow));
                return;
            }
            return;
        }
        if (this.nativeInfoCell.getVisibility() != 0) {
            this.nativeInfoCell.setVisibility(0);
            this.headerCell.setVisibility(0);
            this.extraBackgroundView2.setVisibility(0);
            int i4 = 0;
            while (true) {
                editTextBoldCursorArr = this.inputExtraFields;
                if (i4 >= editTextBoldCursorArr.length) {
                    break;
                }
                ((View) editTextBoldCursorArr[i4].getParent()).setVisibility(0);
                i4++;
            }
            if (editTextBoldCursorArr[0].length() == 0 && this.inputExtraFields[1].length() == 0 && this.inputExtraFields[2].length() == 0) {
                int i5 = 0;
                while (true) {
                    boolean[] zArr = this.nonLatinNames;
                    if (i5 >= zArr.length) {
                        break;
                    }
                    if (zArr[i5]) {
                        this.inputExtraFields[0].setText(this.inputFields[0].getText());
                        this.inputExtraFields[1].setText(this.inputFields[1].getText());
                        this.inputExtraFields[2].setText(this.inputFields[2].getText());
                        break;
                    }
                    i5++;
                }
            }
            this.sectionCell2.setBackgroundDrawable(Theme.getThemedDrawableByKey(getParentActivity(), R.drawable.greydivider, Theme.key_windowBackgroundGrayShadow));
        }
        this.nativeInfoCell.setText(LocaleController.formatString("PassportNativeInfo", R.string.PassportNativeInfo, str));
        String serverString = str2 != null ? LocaleController.getServerString("PassportLanguage_" + str2) : null;
        this.headerCell.setText(serverString != null ? LocaleController.formatString("PassportNativeHeaderLang", R.string.PassportNativeHeaderLang, serverString) : LocaleController.getString(R.string.PassportNativeHeader));
        for (int i6 = 0; i6 < 3; i6++) {
            if (i6 == 0) {
                EditTextBoldCursor[] editTextBoldCursorArr3 = this.inputExtraFields;
                if (serverString != null) {
                    editTextBoldCursor = editTextBoldCursorArr3[i6];
                    i2 = R.string.PassportName;
                    formatString = LocaleController.getString(i2);
                    editTextBoldCursor.setHintText(formatString);
                } else {
                    editTextBoldCursor = editTextBoldCursorArr3[i6];
                    formatString = LocaleController.formatString("PassportNameCountry", R.string.PassportNameCountry, str);
                    editTextBoldCursor.setHintText(formatString);
                }
            } else if (i6 == 1) {
                EditTextBoldCursor[] editTextBoldCursorArr4 = this.inputExtraFields;
                if (serverString != null) {
                    editTextBoldCursor = editTextBoldCursorArr4[i6];
                    i2 = R.string.PassportMidname;
                    formatString = LocaleController.getString(i2);
                    editTextBoldCursor.setHintText(formatString);
                } else {
                    editTextBoldCursor = editTextBoldCursorArr4[i6];
                    formatString = LocaleController.formatString("PassportMidnameCountry", R.string.PassportMidnameCountry, str);
                    editTextBoldCursor.setHintText(formatString);
                }
            } else if (i6 == 2) {
                EditTextBoldCursor[] editTextBoldCursorArr5 = this.inputExtraFields;
                if (serverString != null) {
                    editTextBoldCursor = editTextBoldCursorArr5[i6];
                    i2 = R.string.PassportSurname;
                    formatString = LocaleController.getString(i2);
                    editTextBoldCursor.setHintText(formatString);
                } else {
                    editTextBoldCursor = editTextBoldCursorArr5[i6];
                    formatString = LocaleController.formatString("PassportSurnameCountry", R.string.PassportSurnameCountry, str);
                    editTextBoldCursor.setHintText(formatString);
                }
            }
        }
        if (z) {
            AndroidUtilities.runOnUIThread(new Runnable() {
                @Override
                public final void run() {
                    PassportActivity.this.lambda$checkNativeFields$58();
                }
            });
        }
    }

    public static boolean checkSecret(byte[] bArr, Long l) {
        if (bArr == null || bArr.length != 32) {
            return false;
        }
        int i = 0;
        for (byte b : bArr) {
            i += b & 255;
        }
        if (i % 255 != 239) {
            return false;
        }
        return l == null || Utilities.bytesToLong(Utilities.computeSHA256(bArr)) == l.longValue();
    }

    private void checkTopErrorCell(boolean z) {
        SpannableStringBuilder spannableStringBuilder;
        String str;
        String str2;
        if (this.topErrorCell == null) {
            return;
        }
        if (this.fieldsErrors == null || (!(z || this.errorsValues.containsKey("error_all")) || (str2 = (String) this.fieldsErrors.get("error_all")) == null)) {
            spannableStringBuilder = null;
        } else {
            spannableStringBuilder = new SpannableStringBuilder(str2);
            if (z) {
                this.errorsValues.put("error_all", "");
            }
        }
        if (this.documentsErrors != null && ((z || this.errorsValues.containsKey("error_document_all")) && (str = (String) this.documentsErrors.get("error_all")) != null)) {
            if (spannableStringBuilder == null) {
                spannableStringBuilder = new SpannableStringBuilder(str);
            } else {
                spannableStringBuilder.append((CharSequence) "\n\n").append((CharSequence) str);
            }
            if (z) {
                this.errorsValues.put("error_document_all", "");
            }
        }
        if (spannableStringBuilder != null) {
            spannableStringBuilder.setSpan(new ForegroundColorSpan(Theme.getColor(Theme.key_text_RedRegular)), 0, spannableStringBuilder.length(), 33);
            this.topErrorCell.setText(spannableStringBuilder);
            this.topErrorCell.setVisibility(0);
        } else if (this.topErrorCell.getVisibility() != 8) {
            this.topErrorCell.setVisibility(8);
        }
    }

    private void createAddressInterface(android.content.Context r25) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.PassportActivity.createAddressInterface(android.content.Context):void");
    }

    private void createChatAttachView() {
        if (getParentActivity() != null && this.chatAttachAlert == null) {
            ChatAttachAlert chatAttachAlert = new ChatAttachAlert(getParentActivity(), this, false, false);
            this.chatAttachAlert = chatAttachAlert;
            chatAttachAlert.setDelegate(new ChatAttachAlert.ChatAttachViewDelegate() {
                @Override
                public void didPressedButton(int i, boolean z, boolean z2, int i2, long j, boolean z3, boolean z4) {
                    if (PassportActivity.this.getParentActivity() == null || PassportActivity.this.chatAttachAlert == null) {
                        return;
                    }
                    if (i != 8 && i != 7) {
                        if (PassportActivity.this.chatAttachAlert != null) {
                            PassportActivity.this.chatAttachAlert.dismissWithButtonClick(i);
                        }
                        PassportActivity.this.processSelectedAttach(i);
                        return;
                    }
                    if (i != 8) {
                        PassportActivity.this.chatAttachAlert.dismiss(true);
                    }
                    HashMap<Object, Object> selectedPhotos = PassportActivity.this.chatAttachAlert.getPhotoLayout().getSelectedPhotos();
                    ArrayList<Object> selectedPhotosOrder = PassportActivity.this.chatAttachAlert.getPhotoLayout().getSelectedPhotosOrder();
                    if (selectedPhotos.isEmpty()) {
                        return;
                    }
                    ArrayList arrayList = new ArrayList();
                    for (int i3 = 0; i3 < selectedPhotosOrder.size(); i3++) {
                        MediaController.PhotoEntry photoEntry = (MediaController.PhotoEntry) selectedPhotos.get(selectedPhotosOrder.get(i3));
                        SendMessagesHelper.SendingMediaInfo sendingMediaInfo = new SendMessagesHelper.SendingMediaInfo();
                        String str = photoEntry.imagePath;
                        if (str == null) {
                            str = photoEntry.path;
                        }
                        sendingMediaInfo.path = str;
                        arrayList.add(sendingMediaInfo);
                        photoEntry.reset();
                    }
                    PassportActivity.this.processSelectedFiles(arrayList);
                }

                @Override
                public void didSelectBot(TLRPC.User user) {
                    ChatAttachAlert.ChatAttachViewDelegate.CC.$default$didSelectBot(this, user);
                }

                @Override
                public void doOnIdle(Runnable runnable) {
                    ChatAttachAlert.ChatAttachViewDelegate.CC.$default$doOnIdle(this, runnable);
                }

                @Override
                public boolean needEnterComment() {
                    return ChatAttachAlert.ChatAttachViewDelegate.CC.$default$needEnterComment(this);
                }

                @Override
                public void onCameraOpened() {
                    AndroidUtilities.hideKeyboard(PassportActivity.this.fragmentView.findFocus());
                }

                @Override
                public void onWallpaperSelected(Object obj) {
                    ChatAttachAlert.ChatAttachViewDelegate.CC.$default$onWallpaperSelected(this, obj);
                }

                @Override
                public void openAvatarsSearch() {
                    ChatAttachAlert.ChatAttachViewDelegate.CC.$default$openAvatarsSearch(this);
                }

                @Override
                public boolean selectItemOnClicking() {
                    return ChatAttachAlert.ChatAttachViewDelegate.CC.$default$selectItemOnClicking(this);
                }

                @Override
                public void sendAudio(ArrayList arrayList, CharSequence charSequence, boolean z, int i, long j, boolean z2) {
                    ChatAttachAlert.ChatAttachViewDelegate.CC.$default$sendAudio(this, arrayList, charSequence, z, i, j, z2);
                }
            });
        }
    }

    private void createDocumentDeleteAlert() {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.PassportActivity.createDocumentDeleteAlert():void");
    }

    private void createEmailInterface(Context context) {
        this.actionBar.setTitle(LocaleController.getString(R.string.PassportEmail));
        if (!TextUtils.isEmpty(this.currentEmail)) {
            TextSettingsCell textSettingsCell = new TextSettingsCell(context);
            textSettingsCell.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlueText4));
            textSettingsCell.setBackgroundDrawable(Theme.getSelectorDrawable(true));
            textSettingsCell.setText(LocaleController.formatString("PassportPhoneUseSame", R.string.PassportPhoneUseSame, this.currentEmail), false);
            this.linearLayout2.addView(textSettingsCell, LayoutHelper.createLinear(-1, -2));
            textSettingsCell.setOnClickListener(new View.OnClickListener() {
                @Override
                public final void onClick(View view) {
                    PassportActivity.this.lambda$createEmailInterface$24(view);
                }
            });
            TextInfoPrivacyCell textInfoPrivacyCell = new TextInfoPrivacyCell(context);
            this.bottomCell = textInfoPrivacyCell;
            textInfoPrivacyCell.setBackgroundDrawable(Theme.getThemedDrawableByKey(context, R.drawable.greydivider_bottom, Theme.key_windowBackgroundGrayShadow));
            this.bottomCell.setText(LocaleController.getString(R.string.PassportPhoneUseSameEmailInfo));
            this.linearLayout2.addView(this.bottomCell, LayoutHelper.createLinear(-1, -2));
        }
        this.inputFields = new EditTextBoldCursor[1];
        FrameLayout frameLayout = new FrameLayout(context);
        this.linearLayout2.addView(frameLayout, LayoutHelper.createLinear(-1, 50));
        frameLayout.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
        this.inputFields[0] = new EditTextBoldCursor(context);
        this.inputFields[0].setTag(0);
        this.inputFields[0].setTextSize(1, 16.0f);
        this.inputFields[0].setHintTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteHintText));
        EditTextBoldCursor editTextBoldCursor = this.inputFields[0];
        int i = Theme.key_windowBackgroundWhiteBlackText;
        editTextBoldCursor.setTextColor(Theme.getColor(i));
        this.inputFields[0].setBackgroundDrawable(null);
        this.inputFields[0].setCursorColor(Theme.getColor(i));
        this.inputFields[0].setCursorSize(AndroidUtilities.dp(20.0f));
        this.inputFields[0].setCursorWidth(1.5f);
        this.inputFields[0].setInputType(33);
        this.inputFields[0].setImeOptions(268435462);
        this.inputFields[0].setHint(LocaleController.getString(R.string.PaymentShippingEmailPlaceholder));
        TLRPC.TL_secureValue tL_secureValue = this.currentTypeValue;
        if (tL_secureValue != null) {
            TLRPC.SecurePlainData securePlainData = tL_secureValue.plain_data;
            if (securePlainData instanceof TLRPC.TL_securePlainEmail) {
                TLRPC.TL_securePlainEmail tL_securePlainEmail = (TLRPC.TL_securePlainEmail) securePlainData;
                if (!TextUtils.isEmpty(tL_securePlainEmail.email)) {
                    this.inputFields[0].setText(tL_securePlainEmail.email);
                }
            }
        }
        EditTextBoldCursor editTextBoldCursor2 = this.inputFields[0];
        editTextBoldCursor2.setSelection(editTextBoldCursor2.length());
        this.inputFields[0].setPadding(0, 0, 0, AndroidUtilities.dp(6.0f));
        this.inputFields[0].setGravity(LocaleController.isRTL ? 5 : 3);
        frameLayout.addView(this.inputFields[0], LayoutHelper.createFrame(-1, -2.0f, 51, 21.0f, 12.0f, 21.0f, 6.0f));
        this.inputFields[0].setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public final boolean onEditorAction(TextView textView, int i2, KeyEvent keyEvent) {
                boolean lambda$createEmailInterface$25;
                lambda$createEmailInterface$25 = PassportActivity.this.lambda$createEmailInterface$25(textView, i2, keyEvent);
                return lambda$createEmailInterface$25;
            }
        });
        TextInfoPrivacyCell textInfoPrivacyCell2 = new TextInfoPrivacyCell(context);
        this.bottomCell = textInfoPrivacyCell2;
        textInfoPrivacyCell2.setBackgroundDrawable(Theme.getThemedDrawableByKey(context, R.drawable.greydivider_bottom, Theme.key_windowBackgroundGrayShadow));
        this.bottomCell.setText(LocaleController.getString(R.string.PassportEmailUploadInfo));
        this.linearLayout2.addView(this.bottomCell, LayoutHelper.createLinear(-1, -2));
    }

    private void createEmailVerificationInterface(Context context) {
        this.actionBar.setTitle(LocaleController.getString(R.string.PassportEmail));
        this.inputFields = new EditTextBoldCursor[1];
        FrameLayout frameLayout = new FrameLayout(context);
        this.linearLayout2.addView(frameLayout, LayoutHelper.createLinear(-1, 50));
        frameLayout.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
        this.inputFields[0] = new EditTextBoldCursor(context);
        this.inputFields[0].setTag(0);
        this.inputFields[0].setTextSize(1, 16.0f);
        this.inputFields[0].setHintTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteHintText));
        EditTextBoldCursor editTextBoldCursor = this.inputFields[0];
        int i = Theme.key_windowBackgroundWhiteBlackText;
        editTextBoldCursor.setTextColor(Theme.getColor(i));
        this.inputFields[0].setBackgroundDrawable(null);
        this.inputFields[0].setCursorColor(Theme.getColor(i));
        this.inputFields[0].setCursorSize(AndroidUtilities.dp(20.0f));
        this.inputFields[0].setCursorWidth(1.5f);
        this.inputFields[0].setInputType(3);
        this.inputFields[0].setImeOptions(268435462);
        this.inputFields[0].setHint(LocaleController.getString(R.string.PassportEmailCode));
        EditTextBoldCursor editTextBoldCursor2 = this.inputFields[0];
        editTextBoldCursor2.setSelection(editTextBoldCursor2.length());
        this.inputFields[0].setPadding(0, 0, 0, AndroidUtilities.dp(6.0f));
        this.inputFields[0].setGravity(LocaleController.isRTL ? 5 : 3);
        frameLayout.addView(this.inputFields[0], LayoutHelper.createFrame(-1, -2.0f, 51, 21.0f, 12.0f, 21.0f, 6.0f));
        this.inputFields[0].setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public final boolean onEditorAction(TextView textView, int i2, KeyEvent keyEvent) {
                boolean lambda$createEmailVerificationInterface$5;
                lambda$createEmailVerificationInterface$5 = PassportActivity.this.lambda$createEmailVerificationInterface$5(textView, i2, keyEvent);
                return lambda$createEmailVerificationInterface$5;
            }
        });
        this.inputFields[0].addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable editable) {
                if (PassportActivity.this.ignoreOnTextChange || PassportActivity.this.emailCodeLength == 0 || PassportActivity.this.inputFields[0].length() != PassportActivity.this.emailCodeLength) {
                    return;
                }
                PassportActivity.this.doneItem.callOnClick();
            }

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i2, int i3, int i4) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i2, int i3, int i4) {
            }
        });
        TextInfoPrivacyCell textInfoPrivacyCell = new TextInfoPrivacyCell(context);
        this.bottomCell = textInfoPrivacyCell;
        textInfoPrivacyCell.setBackgroundDrawable(Theme.getThemedDrawableByKey(context, R.drawable.greydivider_bottom, Theme.key_windowBackgroundGrayShadow));
        this.bottomCell.setText(LocaleController.formatString("PassportEmailVerifyInfo", R.string.PassportEmailVerifyInfo, this.currentValues.get("email")));
        this.linearLayout2.addView(this.bottomCell, LayoutHelper.createLinear(-1, -2));
    }

    private void createIdentityInterface(final android.content.Context r26) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.PassportActivity.createIdentityInterface(android.content.Context):void");
    }

    private void createManageInterface(android.content.Context r19) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.PassportActivity.createManageInterface(android.content.Context):void");
    }

    private void createPasswordInterface(Context context) {
        TLRPC.User currentUser;
        TextInfoPrivacyCell textInfoPrivacyCell;
        CharSequence replaceTags;
        if (this.currentForm != null) {
            int i = 0;
            while (true) {
                if (i >= this.currentForm.users.size()) {
                    currentUser = null;
                    break;
                }
                currentUser = this.currentForm.users.get(i);
                if (currentUser.id == this.currentBotId) {
                    break;
                } else {
                    i++;
                }
            }
        } else {
            currentUser = UserConfig.getInstance(this.currentAccount).getCurrentUser();
        }
        FrameLayout frameLayout = (FrameLayout) this.fragmentView;
        this.actionBar.setTitle(LocaleController.getString(R.string.TelegramPassport));
        EmptyTextProgressView emptyTextProgressView = new EmptyTextProgressView(context);
        this.emptyView = emptyTextProgressView;
        emptyTextProgressView.showProgress();
        frameLayout.addView(this.emptyView, LayoutHelper.createFrame(-1, -1.0f));
        FrameLayout frameLayout2 = new FrameLayout(context);
        this.passwordAvatarContainer = frameLayout2;
        this.linearLayout2.addView(frameLayout2, LayoutHelper.createLinear(-1, 100));
        BackupImageView backupImageView = new BackupImageView(context);
        backupImageView.setRoundRadius(AndroidUtilities.dp(32.0f));
        this.passwordAvatarContainer.addView(backupImageView, LayoutHelper.createFrame(64, 64.0f, 17, 0.0f, 8.0f, 0.0f, 0.0f));
        backupImageView.setForUserOrChat(currentUser, new AvatarDrawable(currentUser));
        TextInfoPrivacyCell textInfoPrivacyCell2 = new TextInfoPrivacyCell(context);
        this.passwordRequestTextView = textInfoPrivacyCell2;
        textInfoPrivacyCell2.getTextView().setGravity(1);
        if (this.currentBotId == 0) {
            textInfoPrivacyCell = this.passwordRequestTextView;
            replaceTags = LocaleController.getString(R.string.PassportSelfRequest);
        } else {
            textInfoPrivacyCell = this.passwordRequestTextView;
            replaceTags = AndroidUtilities.replaceTags(LocaleController.formatString("PassportRequest", R.string.PassportRequest, UserObject.getFirstName(currentUser)));
        }
        textInfoPrivacyCell.setText(replaceTags);
        ((FrameLayout.LayoutParams) this.passwordRequestTextView.getTextView().getLayoutParams()).gravity = 1;
        this.linearLayout2.addView(this.passwordRequestTextView, LayoutHelper.createFrame(-2, -2.0f, (LocaleController.isRTL ? 5 : 3) | 48, 21.0f, 0.0f, 21.0f, 0.0f));
        ImageView imageView = new ImageView(context);
        this.noPasswordImageView = imageView;
        imageView.setImageResource(R.drawable.no_password);
        this.noPasswordImageView.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_chat_messagePanelIcons), PorterDuff.Mode.MULTIPLY));
        this.linearLayout2.addView(this.noPasswordImageView, LayoutHelper.createLinear(-2, -2, 49, 0, 13, 0, 0));
        TextView textView = new TextView(context);
        this.noPasswordTextView = textView;
        textView.setTextSize(1, 14.0f);
        this.noPasswordTextView.setGravity(1);
        this.noPasswordTextView.setPadding(AndroidUtilities.dp(21.0f), AndroidUtilities.dp(10.0f), AndroidUtilities.dp(21.0f), AndroidUtilities.dp(17.0f));
        this.noPasswordTextView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteGrayText4));
        this.noPasswordTextView.setText(LocaleController.getString(R.string.TelegramPassportCreatePasswordInfo));
        this.linearLayout2.addView(this.noPasswordTextView, LayoutHelper.createFrame(-2, -2.0f, (LocaleController.isRTL ? 5 : 3) | 48, 21.0f, 10.0f, 21.0f, 0.0f));
        TextView textView2 = new TextView(context);
        this.noPasswordSetTextView = textView2;
        textView2.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlueText5));
        this.noPasswordSetTextView.setGravity(17);
        this.noPasswordSetTextView.setTextSize(1, 16.0f);
        this.noPasswordSetTextView.setTypeface(AndroidUtilities.bold());
        this.noPasswordSetTextView.setText(LocaleController.getString(R.string.TelegramPassportCreatePassword));
        this.linearLayout2.addView(this.noPasswordSetTextView, LayoutHelper.createFrame(-1, 24.0f, (LocaleController.isRTL ? 5 : 3) | 48, 21.0f, 9.0f, 21.0f, 0.0f));
        this.noPasswordSetTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public final void onClick(View view) {
                PassportActivity.this.lambda$createPasswordInterface$6(view);
            }
        });
        this.inputFields = new EditTextBoldCursor[1];
        this.inputFieldContainers = r3;
        ViewGroup[] viewGroupArr = {new FrameLayout(context)};
        this.linearLayout2.addView(this.inputFieldContainers[0], LayoutHelper.createLinear(-1, 50));
        this.inputFieldContainers[0].setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
        this.inputFields[0] = new EditTextBoldCursor(context);
        this.inputFields[0].setTag(0);
        this.inputFields[0].setTextSize(1, 16.0f);
        this.inputFields[0].setHintTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteHintText));
        EditTextBoldCursor editTextBoldCursor = this.inputFields[0];
        int i2 = Theme.key_windowBackgroundWhiteBlackText;
        editTextBoldCursor.setTextColor(Theme.getColor(i2));
        this.inputFields[0].setBackgroundDrawable(null);
        this.inputFields[0].setCursorColor(Theme.getColor(i2));
        this.inputFields[0].setCursorSize(AndroidUtilities.dp(20.0f));
        this.inputFields[0].setCursorWidth(1.5f);
        this.inputFields[0].setInputType(129);
        this.inputFields[0].setMaxLines(1);
        this.inputFields[0].setLines(1);
        this.inputFields[0].setSingleLine(true);
        this.inputFields[0].setTransformationMethod(PasswordTransformationMethod.getInstance());
        this.inputFields[0].setTypeface(Typeface.DEFAULT);
        this.inputFields[0].setImeOptions(268435462);
        this.inputFields[0].setPadding(0, 0, 0, AndroidUtilities.dp(6.0f));
        this.inputFields[0].setGravity(LocaleController.isRTL ? 5 : 3);
        this.inputFieldContainers[0].addView(this.inputFields[0], LayoutHelper.createFrame(-1, -2.0f, 51, 21.0f, 12.0f, 21.0f, 6.0f));
        this.inputFields[0].setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public final boolean onEditorAction(TextView textView3, int i3, KeyEvent keyEvent) {
                boolean lambda$createPasswordInterface$7;
                lambda$createPasswordInterface$7 = PassportActivity.this.lambda$createPasswordInterface$7(textView3, i3, keyEvent);
                return lambda$createPasswordInterface$7;
            }
        });
        this.inputFields[0].setCustomSelectionActionModeCallback(new ActionMode.Callback() {
            @Override
            public boolean onActionItemClicked(ActionMode actionMode, MenuItem menuItem) {
                return false;
            }

            @Override
            public boolean onCreateActionMode(ActionMode actionMode, Menu menu) {
                return false;
            }

            @Override
            public void onDestroyActionMode(ActionMode actionMode) {
            }

            @Override
            public boolean onPrepareActionMode(ActionMode actionMode, Menu menu) {
                return false;
            }
        });
        TextInfoPrivacyCell textInfoPrivacyCell3 = new TextInfoPrivacyCell(context);
        this.passwordInfoRequestTextView = textInfoPrivacyCell3;
        textInfoPrivacyCell3.setBackgroundDrawable(Theme.getThemedDrawableByKey(context, R.drawable.greydivider_bottom, Theme.key_windowBackgroundGrayShadow));
        this.passwordInfoRequestTextView.setText(LocaleController.formatString("PassportRequestPasswordInfo", R.string.PassportRequestPasswordInfo, new Object[0]));
        this.linearLayout2.addView(this.passwordInfoRequestTextView, LayoutHelper.createLinear(-1, -2));
        TextView textView3 = new TextView(context);
        this.passwordForgotButton = textView3;
        textView3.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlueText4));
        this.passwordForgotButton.setTextSize(1, 14.0f);
        this.passwordForgotButton.setText(LocaleController.getString(R.string.ForgotPassword));
        this.passwordForgotButton.setPadding(0, 0, 0, 0);
        this.linearLayout2.addView(this.passwordForgotButton, LayoutHelper.createLinear(-2, 30, (LocaleController.isRTL ? 5 : 3) | 48, 21, 0, 21, 0));
        this.passwordForgotButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public final void onClick(View view) {
                PassportActivity.this.lambda$createPasswordInterface$12(view);
            }
        });
        updatePasswordInterface();
    }

    private void createPhoneInterface(android.content.Context r23) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.PassportActivity.createPhoneInterface(android.content.Context):void");
    }

    private void createPhoneVerificationInterface(Context context) {
        this.actionBar.setTitle(LocaleController.getString(R.string.PassportPhone));
        FrameLayout frameLayout = new FrameLayout(context);
        this.scrollView.addView(frameLayout, LayoutHelper.createScroll(-1, -2, 51));
        for (int i = 0; i < 3; i++) {
            this.views[i] = new PhoneConfirmationView(context, i + 2);
            this.views[i].setVisibility(8);
            frameLayout.addView(this.views[i], LayoutHelper.createFrame(-1, -1.0f, 51, AndroidUtilities.isTablet() ? 26.0f : 18.0f, 30.0f, AndroidUtilities.isTablet() ? 26.0f : 18.0f, 0.0f));
        }
        Bundle bundle = new Bundle();
        bundle.putString("phone", (String) this.currentValues.get("phone"));
        fillNextCodeParams(bundle, this.currentPhoneVerification, false);
    }

    private void createRequestInterface(android.content.Context r24) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.PassportActivity.createRequestInterface(android.content.Context):void");
    }

    public EncryptionResult createSecureDocument(String str) {
        RandomAccessFile randomAccessFile;
        byte[] bArr = new byte[(int) new File(str).length()];
        try {
            randomAccessFile = new RandomAccessFile(str, "rws");
            try {
                randomAccessFile.readFully(bArr);
            } catch (Exception unused) {
            }
        } catch (Exception unused2) {
            randomAccessFile = null;
        }
        EncryptionResult encryptData = encryptData(bArr);
        try {
            randomAccessFile.seek(0L);
            randomAccessFile.write(encryptData.encryptedData);
            randomAccessFile.close();
        } catch (Exception unused3) {
        }
        return encryptData;
    }

    private String decryptData(byte[] bArr, byte[] bArr2, byte[] bArr3) {
        if (bArr == null || bArr2 == null || bArr2.length != 32 || bArr3 == null || bArr3.length != 32) {
            return null;
        }
        byte[] computeSHA512 = Utilities.computeSHA512(bArr2, bArr3);
        byte[] bArr4 = new byte[32];
        System.arraycopy(computeSHA512, 0, bArr4, 0, 32);
        byte[] bArr5 = new byte[16];
        System.arraycopy(computeSHA512, 32, bArr5, 0, 16);
        int length = bArr.length;
        byte[] bArr6 = new byte[length];
        System.arraycopy(bArr, 0, bArr6, 0, bArr.length);
        Utilities.aesCbcEncryptionByteArraySafe(bArr6, bArr4, bArr5, 0, length, 0, 0);
        if (!Arrays.equals(Utilities.computeSHA256(bArr6), bArr3)) {
            return null;
        }
        int i = bArr6[0] & 255;
        return new String(bArr6, i, length - i);
    }

    public byte[] decryptSecret(byte[] bArr, byte[] bArr2) {
        if (bArr == null || bArr.length != 32) {
            return null;
        }
        byte[] bArr3 = new byte[32];
        System.arraycopy(bArr2, 0, bArr3, 0, 32);
        byte[] bArr4 = new byte[16];
        System.arraycopy(bArr2, 32, bArr4, 0, 16);
        byte[] bArr5 = new byte[32];
        System.arraycopy(bArr, 0, bArr5, 0, 32);
        Utilities.aesCbcEncryptionByteArraySafe(bArr5, bArr3, bArr4, 0, 32, 0, 0);
        return bArr5;
    }

    private byte[] decryptValueSecret(byte[] bArr, byte[] bArr2) {
        if (bArr == null || bArr.length != 32 || bArr2 == null || bArr2.length != 32) {
            return null;
        }
        byte[] bArr3 = new byte[32];
        System.arraycopy(this.saltedPassword, 0, bArr3, 0, 32);
        byte[] bArr4 = new byte[16];
        System.arraycopy(this.saltedPassword, 32, bArr4, 0, 16);
        byte[] bArr5 = new byte[32];
        System.arraycopy(this.secureSecret, 0, bArr5, 0, 32);
        Utilities.aesCbcEncryptionByteArraySafe(bArr5, bArr3, bArr4, 0, 32, 0, 0);
        if (!checkSecret(bArr5, null)) {
            return null;
        }
        byte[] computeSHA512 = Utilities.computeSHA512(bArr5, bArr2);
        byte[] bArr6 = new byte[32];
        System.arraycopy(computeSHA512, 0, bArr6, 0, 32);
        byte[] bArr7 = new byte[16];
        System.arraycopy(computeSHA512, 32, bArr7, 0, 16);
        byte[] bArr8 = new byte[32];
        System.arraycopy(bArr, 0, bArr8, 0, 32);
        Utilities.aesCbcEncryptionByteArraySafe(bArr8, bArr6, bArr7, 0, 32, 0, 0);
        return bArr8;
    }

    public void deleteValueInternal(final org.telegram.tgnet.TLRPC.TL_secureRequiredType r14, final org.telegram.tgnet.TLRPC.TL_secureRequiredType r15, final java.util.ArrayList r16, final boolean r17, final java.lang.Runnable r18, final org.telegram.ui.PassportActivity.ErrorRunnable r19, final boolean r20) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.PassportActivity.deleteValueInternal(org.telegram.tgnet.TLRPC$TL_secureRequiredType, org.telegram.tgnet.TLRPC$TL_secureRequiredType, java.util.ArrayList, boolean, java.lang.Runnable, org.telegram.ui.PassportActivity$ErrorRunnable, boolean):void");
    }

    public EncryptionResult encryptData(byte[] bArr) {
        byte[] randomSecret = getRandomSecret();
        int nextInt = Utilities.random.nextInt(208) + 32;
        while ((bArr.length + nextInt) % 16 != 0) {
            nextInt++;
        }
        byte[] bArr2 = new byte[nextInt];
        Utilities.random.nextBytes(bArr2);
        bArr2[0] = (byte) nextInt;
        int length = nextInt + bArr.length;
        byte[] bArr3 = new byte[length];
        System.arraycopy(bArr2, 0, bArr3, 0, nextInt);
        System.arraycopy(bArr, 0, bArr3, nextInt, bArr.length);
        byte[] computeSHA256 = Utilities.computeSHA256(bArr3);
        byte[] computeSHA512 = Utilities.computeSHA512(randomSecret, computeSHA256);
        byte[] bArr4 = new byte[32];
        System.arraycopy(computeSHA512, 0, bArr4, 0, 32);
        byte[] bArr5 = new byte[16];
        System.arraycopy(computeSHA512, 32, bArr5, 0, 16);
        Utilities.aesCbcEncryptionByteArraySafe(bArr3, bArr4, bArr5, 0, length, 0, 1);
        byte[] bArr6 = new byte[32];
        System.arraycopy(this.saltedPassword, 0, bArr6, 0, 32);
        byte[] bArr7 = new byte[16];
        System.arraycopy(this.saltedPassword, 32, bArr7, 0, 16);
        byte[] bArr8 = new byte[32];
        System.arraycopy(this.secureSecret, 0, bArr8, 0, 32);
        Utilities.aesCbcEncryptionByteArraySafe(bArr8, bArr6, bArr7, 0, 32, 0, 0);
        byte[] computeSHA5122 = Utilities.computeSHA512(bArr8, computeSHA256);
        byte[] bArr9 = new byte[32];
        System.arraycopy(computeSHA5122, 0, bArr9, 0, 32);
        byte[] bArr10 = new byte[16];
        System.arraycopy(computeSHA5122, 32, bArr10, 0, 16);
        byte[] bArr11 = new byte[32];
        System.arraycopy(randomSecret, 0, bArr11, 0, 32);
        Utilities.aesCbcEncryptionByteArraySafe(bArr11, bArr9, bArr10, 0, 32, 0, 1);
        return new EncryptionResult(bArr3, bArr11, randomSecret, computeSHA256, bArr4, bArr5);
    }

    private void fillInitialValues() {
        if (this.initialValues != null) {
            return;
        }
        this.initialValues = getCurrentValues();
    }

    public void fillNextCodeParams(Bundle bundle, TLRPC.TL_auth_sentCode tL_auth_sentCode, boolean z) {
        int i;
        bundle.putString("phoneHash", tL_auth_sentCode.phone_code_hash);
        TLRPC.auth_CodeType auth_codetype = tL_auth_sentCode.next_type;
        if (auth_codetype instanceof TLRPC.TL_auth_codeTypeCall) {
            bundle.putInt("nextType", 4);
        } else if (auth_codetype instanceof TLRPC.TL_auth_codeTypeFlashCall) {
            bundle.putInt("nextType", 3);
        } else if (auth_codetype instanceof TLRPC.TL_auth_codeTypeSms) {
            bundle.putInt("nextType", 2);
        }
        if (tL_auth_sentCode.timeout == 0) {
            tL_auth_sentCode.timeout = 60;
        }
        bundle.putInt("timeout", tL_auth_sentCode.timeout * 1000);
        TLRPC.auth_SentCodeType auth_sentcodetype = tL_auth_sentCode.type;
        if (auth_sentcodetype instanceof TLRPC.TL_auth_sentCodeTypeCall) {
            bundle.putInt("type", 4);
            bundle.putInt("length", tL_auth_sentCode.type.length);
            setPage(2, z, bundle);
            return;
        }
        if (auth_sentcodetype instanceof TLRPC.TL_auth_sentCodeTypeFlashCall) {
            bundle.putInt("type", 3);
            bundle.putString("pattern", tL_auth_sentCode.type.pattern);
            i = 1;
        } else {
            if (!(auth_sentcodetype instanceof TLRPC.TL_auth_sentCodeTypeSms)) {
                return;
            }
            bundle.putInt("type", 2);
            bundle.putInt("length", tL_auth_sentCode.type.length);
            i = 0;
        }
        setPage(i, z, bundle);
    }

    private String getCurrentValues() {
        StringBuilder sb = new StringBuilder();
        int i = 0;
        while (true) {
            EditTextBoldCursor[] editTextBoldCursorArr = this.inputFields;
            if (i >= editTextBoldCursorArr.length) {
                break;
            }
            sb.append((CharSequence) editTextBoldCursorArr[i].getText());
            sb.append(",");
            i++;
        }
        if (this.inputExtraFields != null) {
            int i2 = 0;
            while (true) {
                EditTextBoldCursor[] editTextBoldCursorArr2 = this.inputExtraFields;
                if (i2 >= editTextBoldCursorArr2.length) {
                    break;
                }
                sb.append((CharSequence) editTextBoldCursorArr2[i2].getText());
                sb.append(",");
                i2++;
            }
        }
        int size = this.documents.size();
        for (int i3 = 0; i3 < size; i3++) {
            sb.append(((SecureDocument) this.documents.get(i3)).secureFile.id);
        }
        SecureDocument secureDocument = this.frontDocument;
        if (secureDocument != null) {
            sb.append(secureDocument.secureFile.id);
        }
        SecureDocument secureDocument2 = this.reverseDocument;
        if (secureDocument2 != null) {
            sb.append(secureDocument2.secureFile.id);
        }
        SecureDocument secureDocument3 = this.selfieDocument;
        if (secureDocument3 != null) {
            sb.append(secureDocument3.secureFile.id);
        }
        int size2 = this.translationDocuments.size();
        for (int i4 = 0; i4 < size2; i4++) {
            sb.append(((SecureDocument) this.translationDocuments.get(i4)).secureFile.id);
        }
        return sb.toString();
    }

    public String getDocumentHash(SecureDocument secureDocument) {
        byte[] bArr;
        if (secureDocument == null) {
            return "";
        }
        TLRPC.TL_secureFile tL_secureFile = secureDocument.secureFile;
        if (tL_secureFile != null && (bArr = tL_secureFile.file_hash) != null) {
            return Base64.encodeToString(bArr, 2);
        }
        byte[] bArr2 = secureDocument.fileHash;
        return bArr2 != null ? Base64.encodeToString(bArr2, 2) : "";
    }

    public int getFieldCost(String str) {
        str.hashCode();
        char c = 65535;
        switch (str.hashCode()) {
            case -2006252145:
                if (str.equals("residence_country_code")) {
                    c = 0;
                    break;
                }
                break;
            case -1537298398:
                if (str.equals("last_name_native")) {
                    c = 1;
                    break;
                }
                break;
            case -1249512767:
                if (str.equals("gender")) {
                    c = 2;
                    break;
                }
                break;
            case -796150911:
                if (str.equals("street_line1")) {
                    c = 3;
                    break;
                }
                break;
            case -796150910:
                if (str.equals("street_line2")) {
                    c = 4;
                    break;
                }
                break;
            case -160985414:
                if (str.equals("first_name")) {
                    c = 5;
                    break;
                }
                break;
            case 3053931:
                if (str.equals("city")) {
                    c = 6;
                    break;
                }
                break;
            case 109757585:
                if (str.equals("state")) {
                    c = 7;
                    break;
                }
                break;
            case 421072629:
                if (str.equals("middle_name")) {
                    c = '\b';
                    break;
                }
                break;
            case 451516732:
                if (str.equals("first_name_native")) {
                    c = '\t';
                    break;
                }
                break;
            case 475919162:
                if (str.equals("expiry_date")) {
                    c = '\n';
                    break;
                }
                break;
            case 506677093:
                if (str.equals("document_no")) {
                    c = 11;
                    break;
                }
                break;
            case 1168724782:
                if (str.equals("birth_date")) {
                    c = '\f';
                    break;
                }
                break;
            case 1181577377:
                if (str.equals("middle_name_native")) {
                    c = '\r';
                    break;
                }
                break;
            case 1481071862:
                if (str.equals("country_code")) {
                    c = 14;
                    break;
                }
                break;
            case 2002465324:
                if (str.equals("post_code")) {
                    c = 15;
                    break;
                }
                break;
            case 2013122196:
                if (str.equals("last_name")) {
                    c = 16;
                    break;
                }
                break;
        }
        switch (c) {
            case 0:
                return 26;
            case 1:
            case 16:
                return 22;
            case 2:
                return 24;
            case 3:
                return 29;
            case 4:
                return 30;
            case 5:
            case '\t':
                return 20;
            case 6:
                return 32;
            case 7:
                return 33;
            case '\b':
            case '\r':
                return 21;
            case '\n':
                return 28;
            case 11:
                return 27;
            case '\f':
                return 23;
            case 14:
                return 25;
            case 15:
                return 31;
            default:
                return 100;
        }
    }

    private int getMaxSelectedDocuments() {
        ArrayList arrayList;
        int i = this.uploadingFileType;
        if (i == 0) {
            arrayList = this.documents;
        } else {
            if (i != 4) {
                return 1;
            }
            arrayList = this.translationDocuments;
        }
        return 20 - arrayList.size();
    }

    private String getNameForType(TLRPC.SecureValueType secureValueType) {
        return secureValueType instanceof TLRPC.TL_secureValueTypePersonalDetails ? "personal_details" : secureValueType instanceof TLRPC.TL_secureValueTypePassport ? "passport" : secureValueType instanceof TLRPC.TL_secureValueTypeInternalPassport ? "internal_passport" : secureValueType instanceof TLRPC.TL_secureValueTypeDriverLicense ? "driver_license" : secureValueType instanceof TLRPC.TL_secureValueTypeIdentityCard ? "identity_card" : secureValueType instanceof TLRPC.TL_secureValueTypeUtilityBill ? "utility_bill" : secureValueType instanceof TLRPC.TL_secureValueTypeAddress ? "address" : secureValueType instanceof TLRPC.TL_secureValueTypeBankStatement ? "bank_statement" : secureValueType instanceof TLRPC.TL_secureValueTypeRentalAgreement ? "rental_agreement" : secureValueType instanceof TLRPC.TL_secureValueTypeTemporaryRegistration ? "temporary_registration" : secureValueType instanceof TLRPC.TL_secureValueTypePassportRegistration ? "passport_registration" : secureValueType instanceof TLRPC.TL_secureValueTypeEmail ? "email" : secureValueType instanceof TLRPC.TL_secureValueTypePhone ? "phone" : "";
    }

    public byte[] getRandomSecret() {
        byte[] bArr = new byte[32];
        Utilities.random.nextBytes(bArr);
        int i = 0;
        for (int i2 = 0; i2 < 32; i2++) {
            i += 255 & bArr[i2];
        }
        int i3 = i % 255;
        if (i3 != 239) {
            int nextInt = Utilities.random.nextInt(32);
            int i4 = (bArr[nextInt] & 255) + (239 - i3);
            if (i4 < 255) {
                i4 += 255;
            }
            bArr[nextInt] = (byte) (i4 % 255);
        }
        return bArr;
    }

    private SecureDocumentKey getSecureDocumentKey(byte[] bArr, byte[] bArr2) {
        byte[] computeSHA512 = Utilities.computeSHA512(decryptValueSecret(bArr, bArr2), bArr2);
        byte[] bArr3 = new byte[32];
        System.arraycopy(computeSHA512, 0, bArr3, 0, 32);
        byte[] bArr4 = new byte[16];
        System.arraycopy(computeSHA512, 32, bArr4, 0, 16);
        return new SecureDocumentKey(bArr3, bArr4);
    }

    private String getTextForType(TLRPC.SecureValueType secureValueType) {
        int i;
        if (secureValueType instanceof TLRPC.TL_secureValueTypePassport) {
            i = R.string.ActionBotDocumentPassport;
        } else if (secureValueType instanceof TLRPC.TL_secureValueTypeDriverLicense) {
            i = R.string.ActionBotDocumentDriverLicence;
        } else if (secureValueType instanceof TLRPC.TL_secureValueTypeIdentityCard) {
            i = R.string.ActionBotDocumentIdentityCard;
        } else if (secureValueType instanceof TLRPC.TL_secureValueTypeUtilityBill) {
            i = R.string.ActionBotDocumentUtilityBill;
        } else if (secureValueType instanceof TLRPC.TL_secureValueTypeBankStatement) {
            i = R.string.ActionBotDocumentBankStatement;
        } else if (secureValueType instanceof TLRPC.TL_secureValueTypeRentalAgreement) {
            i = R.string.ActionBotDocumentRentalAgreement;
        } else if (secureValueType instanceof TLRPC.TL_secureValueTypeInternalPassport) {
            i = R.string.ActionBotDocumentInternalPassport;
        } else if (secureValueType instanceof TLRPC.TL_secureValueTypePassportRegistration) {
            i = R.string.ActionBotDocumentPassportRegistration;
        } else if (secureValueType instanceof TLRPC.TL_secureValueTypeTemporaryRegistration) {
            i = R.string.ActionBotDocumentTemporaryRegistration;
        } else if (secureValueType instanceof TLRPC.TL_secureValueTypePhone) {
            i = R.string.ActionBotDocumentPhone;
        } else {
            if (!(secureValueType instanceof TLRPC.TL_secureValueTypeEmail)) {
                return "";
            }
            i = R.string.ActionBotDocumentEmail;
        }
        return LocaleController.getString(i);
    }

    public String getTranslitString(String str) {
        return LocaleController.getInstance().getTranslitString(str, true);
    }

    private TLRPC.TL_secureValue getValueByType(TLRPC.TL_secureRequiredType tL_secureRequiredType, boolean z) {
        JSONObject jSONObject;
        int i;
        if (tL_secureRequiredType == null) {
            return null;
        }
        int size = this.currentForm.values.size();
        for (int i2 = 0; i2 < size; i2++) {
            TLRPC.TL_secureValue tL_secureValue = this.currentForm.values.get(i2);
            if (tL_secureRequiredType.type.getClass() == tL_secureValue.type.getClass()) {
                if (z) {
                    if (tL_secureRequiredType.selfie_required && !(tL_secureValue.selfie instanceof TLRPC.TL_secureFile)) {
                        return null;
                    }
                    if (tL_secureRequiredType.translation_required && tL_secureValue.translation.isEmpty()) {
                        return null;
                    }
                    if (isAddressDocument(tL_secureRequiredType.type) && tL_secureValue.files.isEmpty()) {
                        return null;
                    }
                    if (isPersonalDocument(tL_secureRequiredType.type) && !(tL_secureValue.front_side instanceof TLRPC.TL_secureFile)) {
                        return null;
                    }
                    TLRPC.SecureValueType secureValueType = tL_secureRequiredType.type;
                    if (((secureValueType instanceof TLRPC.TL_secureValueTypeDriverLicense) || (secureValueType instanceof TLRPC.TL_secureValueTypeIdentityCard)) && !(tL_secureValue.reverse_side instanceof TLRPC.TL_secureFile)) {
                        return null;
                    }
                    boolean z2 = secureValueType instanceof TLRPC.TL_secureValueTypePersonalDetails;
                    if (z2 || (secureValueType instanceof TLRPC.TL_secureValueTypeAddress)) {
                        String[] strArr = z2 ? tL_secureRequiredType.native_names ? new String[]{"first_name_native", "last_name_native", "birth_date", "gender", "country_code", "residence_country_code"} : new String[]{"first_name", "last_name", "birth_date", "gender", "country_code", "residence_country_code"} : new String[]{"street_line1", "street_line2", "post_code", "city", "state", "country_code"};
                        try {
                            TLRPC.TL_secureData tL_secureData = tL_secureValue.data;
                            jSONObject = new JSONObject(decryptData(tL_secureData.data, decryptValueSecret(tL_secureData.secret, tL_secureData.data_hash), tL_secureValue.data.data_hash));
                        } catch (Throwable unused) {
                        }
                        while (i < strArr.length) {
                            i = (jSONObject.has(strArr[i]) && !TextUtils.isEmpty(jSONObject.getString(strArr[i]))) ? i + 1 : 0;
                            return null;
                        }
                    }
                }
                return tL_secureValue;
            }
        }
        return null;
    }

    private TextDetailSecureCell getViewByType(TLRPC.TL_secureRequiredType tL_secureRequiredType) {
        TLRPC.TL_secureRequiredType tL_secureRequiredType2;
        TextDetailSecureCell textDetailSecureCell = (TextDetailSecureCell) this.typesViews.get(tL_secureRequiredType);
        return (textDetailSecureCell != null || (tL_secureRequiredType2 = (TLRPC.TL_secureRequiredType) this.documentsToTypesLink.get(tL_secureRequiredType)) == null) ? textDetailSecureCell : (TextDetailSecureCell) this.typesViews.get(tL_secureRequiredType2);
    }

    private boolean hasNotValueForType(Class cls) {
        int size = this.currentForm.values.size();
        for (int i = 0; i < size; i++) {
            if (this.currentForm.values.get(i).type.getClass() == cls) {
                return false;
            }
        }
        return true;
    }

    private boolean hasUnfilledValues() {
        return hasNotValueForType(TLRPC.TL_secureValueTypePhone.class) || hasNotValueForType(TLRPC.TL_secureValueTypeEmail.class) || hasNotValueForType(TLRPC.TL_secureValueTypePersonalDetails.class) || hasNotValueForType(TLRPC.TL_secureValueTypePassport.class) || hasNotValueForType(TLRPC.TL_secureValueTypeInternalPassport.class) || hasNotValueForType(TLRPC.TL_secureValueTypeIdentityCard.class) || hasNotValueForType(TLRPC.TL_secureValueTypeDriverLicense.class) || hasNotValueForType(TLRPC.TL_secureValueTypeAddress.class) || hasNotValueForType(TLRPC.TL_secureValueTypeUtilityBill.class) || hasNotValueForType(TLRPC.TL_secureValueTypePassportRegistration.class) || hasNotValueForType(TLRPC.TL_secureValueTypeTemporaryRegistration.class) || hasNotValueForType(TLRPC.TL_secureValueTypeBankStatement.class) || hasNotValueForType(TLRPC.TL_secureValueTypeRentalAgreement.class);
    }

    private boolean isAddressDocument(TLRPC.SecureValueType secureValueType) {
        return (secureValueType instanceof TLRPC.TL_secureValueTypeUtilityBill) || (secureValueType instanceof TLRPC.TL_secureValueTypeBankStatement) || (secureValueType instanceof TLRPC.TL_secureValueTypePassportRegistration) || (secureValueType instanceof TLRPC.TL_secureValueTypeTemporaryRegistration) || (secureValueType instanceof TLRPC.TL_secureValueTypeRentalAgreement);
    }

    public boolean isHasNotAnyChanges() {
        String str = this.initialValues;
        return str == null || str.equals(getCurrentValues());
    }

    private boolean isPersonalDocument(TLRPC.SecureValueType secureValueType) {
        return (secureValueType instanceof TLRPC.TL_secureValueTypeDriverLicense) || (secureValueType instanceof TLRPC.TL_secureValueTypePassport) || (secureValueType instanceof TLRPC.TL_secureValueTypeInternalPassport) || (secureValueType instanceof TLRPC.TL_secureValueTypeIdentityCard);
    }

    public void lambda$addDocumentView$55(int i, View view) {
        PhotoViewer photoViewer;
        ArrayList arrayList;
        ArrayList arrayList2;
        SecureDocument secureDocument;
        this.uploadingFileType = i;
        this.currentPhotoViewerLayout = i == 1 ? this.selfieLayout : i == 4 ? this.translationLayout : i == 2 ? this.frontLayout : i == 3 ? this.reverseLayout : this.documentsLayout;
        SecureDocument secureDocument2 = (SecureDocument) view.getTag();
        PhotoViewer.getInstance().setParentActivity(this);
        if (i == 1) {
            arrayList2 = new ArrayList();
            secureDocument = this.selfieDocument;
        } else if (i == 2) {
            arrayList2 = new ArrayList();
            secureDocument = this.frontDocument;
        } else {
            if (i != 3) {
                if (i == 0) {
                    photoViewer = PhotoViewer.getInstance();
                    arrayList = this.documents;
                } else {
                    photoViewer = PhotoViewer.getInstance();
                    arrayList = this.translationDocuments;
                }
                photoViewer.openPhoto(arrayList, arrayList.indexOf(secureDocument2), this.provider);
                return;
            }
            arrayList2 = new ArrayList();
            secureDocument = this.reverseDocument;
        }
        arrayList2.add(secureDocument);
        PhotoViewer.getInstance().openPhoto(arrayList2, 0, this.provider);
    }

    public void lambda$addDocumentView$56(SecureDocument secureDocument, int i, SecureDocumentCell secureDocumentCell, String str, DialogInterface dialogInterface, int i2) {
        LinearLayout linearLayout;
        this.documentsCells.remove(secureDocument);
        if (i == 1) {
            this.selfieDocument = null;
            linearLayout = this.selfieLayout;
        } else if (i == 4) {
            this.translationDocuments.remove(secureDocument);
            linearLayout = this.translationLayout;
        } else if (i == 2) {
            this.frontDocument = null;
            linearLayout = this.frontLayout;
        } else if (i == 3) {
            this.reverseDocument = null;
            linearLayout = this.reverseLayout;
        } else {
            this.documents.remove(secureDocument);
            linearLayout = this.documentsLayout;
        }
        linearLayout.removeView(secureDocumentCell);
        if (str != null) {
            HashMap hashMap = this.documentsErrors;
            if (hashMap != null) {
                hashMap.remove(str);
            }
            HashMap hashMap2 = this.errorsValues;
            if (hashMap2 != null) {
                hashMap2.remove(str);
            }
        }
        updateUploadText(i);
        String str2 = secureDocument.path;
        if (str2 == null || this.uploadingDocuments.remove(str2) == null) {
            return;
        }
        if (this.uploadingDocuments.isEmpty()) {
            this.doneItem.setEnabled(true);
            this.doneItem.setAlpha(1.0f);
        }
        FileLoader.getInstance(this.currentAccount).cancelFileUpload(secureDocument.path, false);
    }

    public boolean lambda$addDocumentView$57(final int i, final SecureDocument secureDocument, final SecureDocumentCell secureDocumentCell, final String str, View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getParentActivity());
        builder.setMessage(LocaleController.getString(i == 1 ? R.string.PassportDeleteSelfie : R.string.PassportDeleteScan));
        builder.setNegativeButton(LocaleController.getString(R.string.Cancel), null);
        builder.setTitle(LocaleController.getString(R.string.AppName));
        builder.setPositiveButton(LocaleController.getString(R.string.OK), new DialogInterface.OnClickListener() {
            @Override
            public final void onClick(DialogInterface dialogInterface, int i2) {
                PassportActivity.this.lambda$addDocumentView$56(secureDocument, i, secureDocumentCell, str, dialogInterface, i2);
            }
        });
        showDialog(builder.create());
        return true;
    }

    public void lambda$addField$61(TLRPC.TL_secureRequiredType tL_secureRequiredType, ArrayList arrayList, boolean z, DialogInterface dialogInterface, int i) {
        openTypeActivity(tL_secureRequiredType, (TLRPC.TL_secureRequiredType) arrayList.get(i), arrayList, z);
    }

    public void lambda$addField$62(String str, String str2) {
        needHideProgress();
    }

    public void lambda$addField$63(TLRPC.TL_secureRequiredType tL_secureRequiredType, boolean z, DialogInterface dialogInterface, int i) {
        needShowProgress();
        deleteValueInternal(tL_secureRequiredType, null, null, true, new Runnable() {
            @Override
            public final void run() {
                PassportActivity.this.needHideProgress();
            }
        }, new ErrorRunnable() {
            @Override
            public final void onError(String str, String str2) {
                PassportActivity.this.lambda$addField$62(str, str2);
            }
        }, z);
    }

    public void lambda$addField$64(final java.util.ArrayList r7, final org.telegram.tgnet.TLRPC.TL_secureRequiredType r8, final boolean r9, android.view.View r10) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.PassportActivity.lambda$addField$64(java.util.ArrayList, org.telegram.tgnet.TLRPC$TL_secureRequiredType, boolean, android.view.View):void");
    }

    public void lambda$checkDiscard$69(DialogInterface dialogInterface, int i) {
        lambda$onBackPressed$321();
    }

    public void lambda$checkNativeFields$58() {
        EditTextBoldCursor[] editTextBoldCursorArr = this.inputExtraFields;
        if (editTextBoldCursorArr != null) {
            scrollToField(editTextBoldCursorArr[0]);
        }
    }

    public void lambda$createAddressInterface$32(View view) {
        this.uploadingFileType = 0;
        openAttachMenu();
    }

    public void lambda$createAddressInterface$33(View view) {
        this.uploadingFileType = 4;
        openAttachMenu();
    }

    public void lambda$createAddressInterface$34(CountrySelectActivity.Country country) {
        this.inputFields[5].setText(country.name);
        this.currentCitizeship = country.shortname;
    }

    public boolean lambda$createAddressInterface$35(View view, MotionEvent motionEvent) {
        if (getParentActivity() == null) {
            return false;
        }
        if (motionEvent.getAction() == 1) {
            CountrySelectActivity countrySelectActivity = new CountrySelectActivity(false);
            countrySelectActivity.setCountrySelectActivityDelegate(new CountrySelectActivity.CountrySelectActivityDelegate() {
                @Override
                public final void didSelectCountry(CountrySelectActivity.Country country) {
                    PassportActivity.this.lambda$createAddressInterface$34(country);
                }
            });
            presentFragment(countrySelectActivity);
        }
        return true;
    }

    public boolean lambda$createAddressInterface$36(TextView textView, int i, KeyEvent keyEvent) {
        if (i != 5) {
            return false;
        }
        int intValue = ((Integer) textView.getTag()).intValue() + 1;
        EditTextBoldCursor[] editTextBoldCursorArr = this.inputFields;
        if (intValue < editTextBoldCursorArr.length) {
            if (editTextBoldCursorArr[intValue].isFocusable()) {
                this.inputFields[intValue].requestFocus();
            } else {
                this.inputFields[intValue].dispatchTouchEvent(MotionEvent.obtain(0L, 0L, 1, 0.0f, 0.0f, 0));
                textView.clearFocus();
                AndroidUtilities.hideKeyboard(textView);
            }
        }
        return true;
    }

    public void lambda$createAddressInterface$37(View view) {
        createDocumentDeleteAlert();
    }

    public void lambda$createDocumentDeleteAlert$38(boolean[] zArr, DialogInterface dialogInterface, int i) {
        if (!this.documentOnly) {
            this.currentValues.clear();
        }
        this.currentDocumentValues.clear();
        this.delegate.deleteValue(this.currentType, this.currentDocumentsType, this.availableDocumentTypes, zArr[0], null, null);
        lambda$onBackPressed$321();
    }

    public static void lambda$createDocumentDeleteAlert$39(boolean[] zArr, View view) {
        if (view.isEnabled()) {
            boolean z = !zArr[0];
            zArr[0] = z;
            ((CheckBoxCell) view).setChecked(z, true);
        }
    }

    public void lambda$createEmailInterface$24(View view) {
        this.useCurrentValue = true;
        this.doneItem.callOnClick();
        this.useCurrentValue = false;
    }

    public boolean lambda$createEmailInterface$25(TextView textView, int i, KeyEvent keyEvent) {
        if (i != 6 && i != 5) {
            return false;
        }
        this.doneItem.callOnClick();
        return true;
    }

    public boolean lambda$createEmailVerificationInterface$5(TextView textView, int i, KeyEvent keyEvent) {
        if (i != 6 && i != 5) {
            return false;
        }
        this.doneItem.callOnClick();
        return true;
    }

    public void lambda$createIdentityInterface$40(View view) {
        this.uploadingFileType = 2;
        openAttachMenu();
    }

    public void lambda$createIdentityInterface$41(View view) {
        this.uploadingFileType = 3;
        openAttachMenu();
    }

    public void lambda$createIdentityInterface$42(View view) {
        this.uploadingFileType = 1;
        openAttachMenu();
    }

    public void lambda$createIdentityInterface$43(View view) {
        this.uploadingFileType = 4;
        openAttachMenu();
    }

    public void lambda$createIdentityInterface$44(View view) {
        int checkSelfPermission;
        if (Build.VERSION.SDK_INT >= 23) {
            checkSelfPermission = getParentActivity().checkSelfPermission("android.permission.CAMERA");
            if (checkSelfPermission != 0) {
                getParentActivity().requestPermissions(new String[]{"android.permission.CAMERA"}, 22);
                return;
            }
        }
        CameraScanActivity cameraScanActivity = new CameraScanActivity(0);
        cameraScanActivity.setDelegate(new CameraScanActivity.CameraScanActivityDelegate() {
            @Override
            public void didFindMrzInfo(MrzRecognizer.Result result) {
                EditTextBoldCursor editTextBoldCursor;
                int i;
                if (!TextUtils.isEmpty(result.firstName)) {
                    PassportActivity.this.inputFields[0].setText(result.firstName);
                }
                if (!TextUtils.isEmpty(result.middleName)) {
                    PassportActivity.this.inputFields[1].setText(result.middleName);
                }
                if (!TextUtils.isEmpty(result.lastName)) {
                    PassportActivity.this.inputFields[2].setText(result.lastName);
                }
                int i2 = result.gender;
                if (i2 != 0) {
                    if (i2 == 1) {
                        PassportActivity.this.currentGender = "male";
                        editTextBoldCursor = PassportActivity.this.inputFields[4];
                        i = R.string.PassportMale;
                    } else if (i2 == 2) {
                        PassportActivity.this.currentGender = "female";
                        editTextBoldCursor = PassportActivity.this.inputFields[4];
                        i = R.string.PassportFemale;
                    }
                    editTextBoldCursor.setText(LocaleController.getString(i));
                }
                if (!TextUtils.isEmpty(result.nationality)) {
                    PassportActivity.this.currentCitizeship = result.nationality;
                    String str = (String) PassportActivity.this.languageMap.get(PassportActivity.this.currentCitizeship);
                    if (str != null) {
                        PassportActivity.this.inputFields[5].setText(str);
                    }
                }
                if (!TextUtils.isEmpty(result.issuingCountry)) {
                    PassportActivity.this.currentResidence = result.issuingCountry;
                    String str2 = (String) PassportActivity.this.languageMap.get(PassportActivity.this.currentResidence);
                    if (str2 != null) {
                        PassportActivity.this.inputFields[6].setText(str2);
                    }
                }
                if (result.birthDay <= 0 || result.birthMonth <= 0 || result.birthYear <= 0) {
                    return;
                }
                PassportActivity.this.inputFields[3].setText(String.format(Locale.US, "%02d.%02d.%d", Integer.valueOf(result.birthDay), Integer.valueOf(result.birthMonth), Integer.valueOf(result.birthYear)));
            }

            @Override
            public void didFindQr(String str) {
                CameraScanActivity.CameraScanActivityDelegate.CC.$default$didFindQr(this, str);
            }

            @Override
            public String getSubtitleText() {
                return CameraScanActivity.CameraScanActivityDelegate.CC.$default$getSubtitleText(this);
            }

            @Override
            public void onDismiss() {
                CameraScanActivity.CameraScanActivityDelegate.CC.$default$onDismiss(this);
            }

            @Override
            public boolean processQr(String str, Runnable runnable) {
                return CameraScanActivity.CameraScanActivityDelegate.CC.$default$processQr(this, str, runnable);
            }
        });
        presentFragment(cameraScanActivity);
    }

    public void lambda$createIdentityInterface$45(View view, CountrySelectActivity.Country country) {
        int intValue = ((Integer) view.getTag()).intValue();
        EditTextBoldCursor editTextBoldCursor = this.inputFields[intValue];
        if (intValue == 5) {
            this.currentCitizeship = country.shortname;
        } else {
            this.currentResidence = country.shortname;
        }
        editTextBoldCursor.setText(country.name);
    }

    public boolean lambda$createIdentityInterface$46(final View view, MotionEvent motionEvent) {
        if (getParentActivity() == null) {
            return false;
        }
        if (motionEvent.getAction() == 1) {
            CountrySelectActivity countrySelectActivity = new CountrySelectActivity(false);
            countrySelectActivity.setCountrySelectActivityDelegate(new CountrySelectActivity.CountrySelectActivityDelegate() {
                @Override
                public final void didSelectCountry(CountrySelectActivity.Country country) {
                    PassportActivity.this.lambda$createIdentityInterface$45(view, country);
                }
            });
            presentFragment(countrySelectActivity);
        }
        return true;
    }

    public void lambda$createIdentityInterface$47(int i, EditTextBoldCursor editTextBoldCursor, int i2, int i3, int i4) {
        if (i == 8) {
            int[] iArr = this.currentExpireDate;
            iArr[0] = i2;
            iArr[1] = i3 + 1;
            iArr[2] = i4;
        }
        editTextBoldCursor.setText(String.format(Locale.US, "%02d.%02d.%d", Integer.valueOf(i4), Integer.valueOf(i3 + 1), Integer.valueOf(i2)));
    }

    public void lambda$createIdentityInterface$48(EditTextBoldCursor editTextBoldCursor, DialogInterface dialogInterface, int i) {
        int[] iArr = this.currentExpireDate;
        iArr[2] = 0;
        iArr[1] = 0;
        iArr[0] = 0;
        editTextBoldCursor.setText(LocaleController.getString(R.string.PassportNoExpireDate));
    }

    public boolean lambda$createIdentityInterface$49(Context context, View view, MotionEvent motionEvent) {
        String string;
        int i;
        int i2;
        int i3;
        int i4;
        int i5;
        int i6;
        if (getParentActivity() == null) {
            return false;
        }
        if (motionEvent.getAction() == 1) {
            Calendar calendar = Calendar.getInstance();
            calendar.get(1);
            calendar.get(2);
            calendar.get(5);
            try {
                final EditTextBoldCursor editTextBoldCursor = (EditTextBoldCursor) view;
                final int intValue = ((Integer) editTextBoldCursor.getTag()).intValue();
                if (intValue == 8) {
                    string = LocaleController.getString(R.string.PassportSelectExpiredDate);
                    i = 0;
                    i2 = 20;
                    i3 = 0;
                } else {
                    string = LocaleController.getString(R.string.PassportSelectBithdayDate);
                    i = -120;
                    i2 = 0;
                    i3 = -18;
                }
                String[] split = editTextBoldCursor.getText().toString().split("\\.");
                if (split.length == 3) {
                    int intValue2 = Utilities.parseInt((CharSequence) split[0]).intValue();
                    int intValue3 = Utilities.parseInt((CharSequence) split[1]).intValue();
                    i6 = Utilities.parseInt((CharSequence) split[2]).intValue();
                    i4 = intValue2;
                    i5 = intValue3;
                } else {
                    i4 = -1;
                    i5 = -1;
                    i6 = -1;
                }
                AlertDialog.Builder createDatePickerDialog = AlertsCreator.createDatePickerDialog(context, i, i2, i3, i4, i5, i6, string, intValue == 8, new AlertsCreator.DatePickerDelegate() {
                    @Override
                    public final void didSelectDate(int i7, int i8, int i9) {
                        PassportActivity.this.lambda$createIdentityInterface$47(intValue, editTextBoldCursor, i7, i8, i9);
                    }
                });
                if (intValue == 8) {
                    createDatePickerDialog.setNegativeButton(LocaleController.getString(R.string.PassportSelectNotExpire), new DialogInterface.OnClickListener() {
                        @Override
                        public final void onClick(DialogInterface dialogInterface, int i7) {
                            PassportActivity.this.lambda$createIdentityInterface$48(editTextBoldCursor, dialogInterface, i7);
                        }
                    });
                }
                showDialog(createDatePickerDialog.create());
            } catch (Exception e) {
                FileLog.e(e);
            }
        }
        return true;
    }

    public void lambda$createIdentityInterface$50(DialogInterface dialogInterface, int i) {
        EditTextBoldCursor editTextBoldCursor;
        int i2;
        if (i == 0) {
            this.currentGender = "male";
            editTextBoldCursor = this.inputFields[4];
            i2 = R.string.PassportMale;
        } else {
            if (i != 1) {
                return;
            }
            this.currentGender = "female";
            editTextBoldCursor = this.inputFields[4];
            i2 = R.string.PassportFemale;
        }
        editTextBoldCursor.setText(LocaleController.getString(i2));
    }

    public boolean lambda$createIdentityInterface$51(View view, MotionEvent motionEvent) {
        if (getParentActivity() == null) {
            return false;
        }
        if (motionEvent.getAction() == 1) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getParentActivity());
            builder.setTitle(LocaleController.getString(R.string.PassportSelectGender));
            builder.setItems(new CharSequence[]{LocaleController.getString(R.string.PassportMale), LocaleController.getString(R.string.PassportFemale)}, new DialogInterface.OnClickListener() {
                @Override
                public final void onClick(DialogInterface dialogInterface, int i) {
                    PassportActivity.this.lambda$createIdentityInterface$50(dialogInterface, i);
                }
            });
            builder.setPositiveButton(LocaleController.getString(R.string.Cancel), null);
            showDialog(builder.create());
        }
        return true;
    }

    public boolean lambda$createIdentityInterface$52(TextView textView, int i, KeyEvent keyEvent) {
        if (i != 5) {
            return false;
        }
        int intValue = ((Integer) textView.getTag()).intValue() + 1;
        EditTextBoldCursor[] editTextBoldCursorArr = this.inputFields;
        if (intValue < editTextBoldCursorArr.length) {
            if (editTextBoldCursorArr[intValue].isFocusable()) {
                this.inputFields[intValue].requestFocus();
            } else {
                this.inputFields[intValue].dispatchTouchEvent(MotionEvent.obtain(0L, 0L, 1, 0.0f, 0.0f, 0));
                textView.clearFocus();
                AndroidUtilities.hideKeyboard(textView);
            }
        }
        return true;
    }

    public boolean lambda$createIdentityInterface$53(TextView textView, int i, KeyEvent keyEvent) {
        if (i != 5) {
            return false;
        }
        int intValue = ((Integer) textView.getTag()).intValue() + 1;
        EditTextBoldCursor[] editTextBoldCursorArr = this.inputExtraFields;
        if (intValue < editTextBoldCursorArr.length) {
            if (editTextBoldCursorArr[intValue].isFocusable()) {
                this.inputExtraFields[intValue].requestFocus();
            } else {
                this.inputExtraFields[intValue].dispatchTouchEvent(MotionEvent.obtain(0L, 0L, 1, 0.0f, 0.0f, 0));
                textView.clearFocus();
                AndroidUtilities.hideKeyboard(textView);
            }
        }
        return true;
    }

    public void lambda$createIdentityInterface$54(View view) {
        createDocumentDeleteAlert();
    }

    public void lambda$createManageInterface$17(View view) {
        openAddDocumentAlert();
    }

    public void lambda$createManageInterface$18() {
        int i = 0;
        while (i < this.linearLayout2.getChildCount()) {
            View childAt = this.linearLayout2.getChildAt(i);
            if (childAt instanceof TextDetailSecureCell) {
                this.linearLayout2.removeView(childAt);
                i--;
            }
            i++;
        }
        needHideProgress();
        this.typesViews.clear();
        this.typesValues.clear();
        this.currentForm.values.clear();
        updateManageVisibility();
    }

    public void lambda$createManageInterface$19(TLObject tLObject, TLRPC.TL_error tL_error) {
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                PassportActivity.this.lambda$createManageInterface$18();
            }
        });
    }

    public void lambda$createManageInterface$20(DialogInterface dialogInterface, int i) {
        TLRPC.TL_account_deleteSecureValue tL_account_deleteSecureValue = new TLRPC.TL_account_deleteSecureValue();
        for (int i2 = 0; i2 < this.currentForm.values.size(); i2++) {
            tL_account_deleteSecureValue.types.add(this.currentForm.values.get(i2).type);
        }
        needShowProgress();
        ConnectionsManager.getInstance(this.currentAccount).sendRequest(tL_account_deleteSecureValue, new RequestDelegate() {
            @Override
            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                PassportActivity.this.lambda$createManageInterface$19(tLObject, tL_error);
            }
        });
    }

    public void lambda$createManageInterface$21(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getParentActivity());
        builder.setTitle(LocaleController.getString(R.string.TelegramPassportDeleteTitle));
        builder.setMessage(LocaleController.getString(R.string.TelegramPassportDeleteAlert));
        builder.setPositiveButton(LocaleController.getString(R.string.Delete), new DialogInterface.OnClickListener() {
            @Override
            public final void onClick(DialogInterface dialogInterface, int i) {
                PassportActivity.this.lambda$createManageInterface$20(dialogInterface, i);
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

    public void lambda$createManageInterface$22(View view) {
        openAddDocumentAlert();
    }

    public void lambda$createPasswordInterface$10(final TLObject tLObject, final TLRPC.TL_error tL_error) {
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                PassportActivity.this.lambda$createPasswordInterface$9(tL_error, tLObject);
            }
        });
    }

    public void lambda$createPasswordInterface$11(DialogInterface dialogInterface, int i) {
        Browser.openUrl(getParentActivity(), "https://telegram.org/deactivate?phone=" + UserConfig.getInstance(this.currentAccount).getClientPhone());
    }

    public void lambda$createPasswordInterface$12(View view) {
        if (this.currentPassword.has_recovery) {
            needShowProgress();
            ConnectionsManager.getInstance(this.currentAccount).bindRequestToGuid(ConnectionsManager.getInstance(this.currentAccount).sendRequest(new TLRPC.TL_auth_requestPasswordRecovery(), new RequestDelegate() {
                @Override
                public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                    PassportActivity.this.lambda$createPasswordInterface$10(tLObject, tL_error);
                }
            }, 10), this.classGuid);
            return;
        }
        if (getParentActivity() == null) {
            return;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(getParentActivity());
        builder.setPositiveButton(LocaleController.getString(R.string.OK), null);
        builder.setNegativeButton(LocaleController.getString(R.string.RestorePasswordResetAccount), new DialogInterface.OnClickListener() {
            @Override
            public final void onClick(DialogInterface dialogInterface, int i) {
                PassportActivity.this.lambda$createPasswordInterface$11(dialogInterface, i);
            }
        });
        builder.setTitle(LocaleController.getString(R.string.RestorePasswordNoEmailTitle));
        builder.setMessage(LocaleController.getString(R.string.RestorePasswordNoEmailText));
        showDialog(builder.create());
    }

    public void lambda$createPasswordInterface$6(View view) {
        TwoStepVerificationSetupActivity twoStepVerificationSetupActivity = new TwoStepVerificationSetupActivity(this.currentAccount, 0, this.currentPassword);
        twoStepVerificationSetupActivity.setCloseAfterSet(true);
        presentFragment(twoStepVerificationSetupActivity);
    }

    public boolean lambda$createPasswordInterface$7(TextView textView, int i, KeyEvent keyEvent) {
        if (i != 5 && i != 6) {
            return false;
        }
        this.doneItem.callOnClick();
        return true;
    }

    public void lambda$createPasswordInterface$8(TLRPC.TL_auth_passwordRecovery tL_auth_passwordRecovery, DialogInterface dialogInterface, int i) {
        TLRPC.account_Password account_password = this.currentPassword;
        account_password.email_unconfirmed_pattern = tL_auth_passwordRecovery.email_pattern;
        presentFragment(new TwoStepVerificationSetupActivity(this.currentAccount, 4, account_password));
    }

    public void lambda$createPasswordInterface$9(TLRPC.TL_error tL_error, TLObject tLObject) {
        String string;
        String str;
        needHideProgress();
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
            return;
        }
        final TLRPC.TL_auth_passwordRecovery tL_auth_passwordRecovery = (TLRPC.TL_auth_passwordRecovery) tLObject;
        AlertDialog.Builder builder = new AlertDialog.Builder(getParentActivity());
        builder.setMessage(LocaleController.formatString("RestoreEmailSent", R.string.RestoreEmailSent, tL_auth_passwordRecovery.email_pattern));
        builder.setTitle(LocaleController.getString(R.string.RestoreEmailSentTitle));
        builder.setPositiveButton(LocaleController.getString(R.string.OK), new DialogInterface.OnClickListener() {
            @Override
            public final void onClick(DialogInterface dialogInterface, int i) {
                PassportActivity.this.lambda$createPasswordInterface$8(tL_auth_passwordRecovery, dialogInterface, i);
            }
        });
        Dialog showDialog = showDialog(builder.create());
        if (showDialog != null) {
            showDialog.setCanceledOnTouchOutside(false);
            showDialog.setCancelable(false);
        }
    }

    public void lambda$createPhoneInterface$26(View view) {
        this.useCurrentValue = true;
        this.doneItem.callOnClick();
        this.useCurrentValue = false;
    }

    public void lambda$createPhoneInterface$27() {
        AndroidUtilities.showKeyboard(this.inputFields[2]);
    }

    public void lambda$createPhoneInterface$28(CountrySelectActivity.Country country) {
        this.inputFields[0].setText(country.name);
        if (this.countriesArray.indexOf(country.name) != -1) {
            this.ignoreOnTextChange = true;
            String str = (String) this.countriesMap.get(country.name);
            this.inputFields[1].setText(str);
            String str2 = (String) this.phoneFormatMap.get(str);
            this.inputFields[2].setHintText(str2 != null ? str2.replace('X', (char) 8211) : null);
            this.ignoreOnTextChange = false;
        }
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                PassportActivity.this.lambda$createPhoneInterface$27();
            }
        }, 300L);
        this.inputFields[2].requestFocus();
        EditTextBoldCursor editTextBoldCursor = this.inputFields[2];
        editTextBoldCursor.setSelection(editTextBoldCursor.length());
    }

    public boolean lambda$createPhoneInterface$29(View view, MotionEvent motionEvent) {
        if (getParentActivity() == null) {
            return false;
        }
        if (motionEvent.getAction() == 1) {
            CountrySelectActivity countrySelectActivity = new CountrySelectActivity(false);
            countrySelectActivity.setCountrySelectActivityDelegate(new CountrySelectActivity.CountrySelectActivityDelegate() {
                @Override
                public final void didSelectCountry(CountrySelectActivity.Country country) {
                    PassportActivity.this.lambda$createPhoneInterface$28(country);
                }
            });
            presentFragment(countrySelectActivity);
        }
        return true;
    }

    public boolean lambda$createPhoneInterface$30(TextView textView, int i, KeyEvent keyEvent) {
        if (i == 5) {
            this.inputFields[2].requestFocus();
            return true;
        }
        if (i != 6) {
            return false;
        }
        this.doneItem.callOnClick();
        return true;
    }

    public boolean lambda$createPhoneInterface$31(View view, int i, KeyEvent keyEvent) {
        if (i != 67 || this.inputFields[2].length() != 0) {
            return false;
        }
        this.inputFields[1].requestFocus();
        EditTextBoldCursor editTextBoldCursor = this.inputFields[1];
        editTextBoldCursor.setSelection(editTextBoldCursor.length());
        this.inputFields[1].dispatchKeyEvent(keyEvent);
        return true;
    }

    public void lambda$createRequestInterface$14(TLRPC.TL_error tL_error) {
        if (tL_error == null) {
            this.ignoreOnFailure = true;
            callCallback(true);
            lambda$onBackPressed$321();
        } else {
            showEditDoneProgress(false, false);
            if ("APP_VERSION_OUTDATED".equals(tL_error.text)) {
                AlertsCreator.showUpdateAppAlert(getParentActivity(), LocaleController.getString(R.string.UpdateAppAlert), true);
            } else {
                showAlertWithText(LocaleController.getString(R.string.AppName), tL_error.text);
            }
        }
    }

    public void lambda$createRequestInterface$15(TLObject tLObject, final TLRPC.TL_error tL_error) {
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                PassportActivity.this.lambda$createRequestInterface$14(tL_error);
            }
        });
    }

    public void lambda$createRequestInterface$16(View view) {
        ArrayList arrayList;
        int i;
        TLRPC.TL_secureRequiredType tL_secureRequiredType;
        ArrayList arrayList2 = new ArrayList();
        int size = this.currentForm.required_types.size();
        for (int i2 = 0; i2 < size; i2++) {
            TLRPC.SecureRequiredType secureRequiredType = this.currentForm.required_types.get(i2);
            if (secureRequiredType instanceof TLRPC.TL_secureRequiredType) {
                tL_secureRequiredType = (TLRPC.TL_secureRequiredType) secureRequiredType;
            } else {
                if (secureRequiredType instanceof TLRPC.TL_secureRequiredTypeOneOf) {
                    TLRPC.TL_secureRequiredTypeOneOf tL_secureRequiredTypeOneOf = (TLRPC.TL_secureRequiredTypeOneOf) secureRequiredType;
                    if (!tL_secureRequiredTypeOneOf.types.isEmpty()) {
                        TLRPC.SecureRequiredType secureRequiredType2 = tL_secureRequiredTypeOneOf.types.get(0);
                        if (secureRequiredType2 instanceof TLRPC.TL_secureRequiredType) {
                            TLRPC.TL_secureRequiredType tL_secureRequiredType2 = (TLRPC.TL_secureRequiredType) secureRequiredType2;
                            int size2 = tL_secureRequiredTypeOneOf.types.size();
                            int i3 = 0;
                            while (true) {
                                if (i3 >= size2) {
                                    tL_secureRequiredType = tL_secureRequiredType2;
                                    break;
                                }
                                TLRPC.SecureRequiredType secureRequiredType3 = tL_secureRequiredTypeOneOf.types.get(i3);
                                if (secureRequiredType3 instanceof TLRPC.TL_secureRequiredType) {
                                    TLRPC.TL_secureRequiredType tL_secureRequiredType3 = (TLRPC.TL_secureRequiredType) secureRequiredType3;
                                    if (getValueByType(tL_secureRequiredType3, true) != null) {
                                        tL_secureRequiredType = tL_secureRequiredType3;
                                        break;
                                    }
                                }
                                i3++;
                            }
                        } else {
                            continue;
                        }
                    } else {
                        continue;
                    }
                } else {
                    continue;
                }
            }
            TLRPC.TL_secureValue valueByType = getValueByType(tL_secureRequiredType, true);
            if (valueByType == null) {
                Vibrator vibrator = (Vibrator) getParentActivity().getSystemService("vibrator");
                if (vibrator != null) {
                    vibrator.vibrate(200L);
                }
                AndroidUtilities.shakeView(getViewByType(tL_secureRequiredType));
                return;
            }
            HashMap hashMap = (HashMap) this.errorsMap.get(getNameForType(tL_secureRequiredType.type));
            if (hashMap != null && !hashMap.isEmpty()) {
                Vibrator vibrator2 = (Vibrator) getParentActivity().getSystemService("vibrator");
                if (vibrator2 != null) {
                    vibrator2.vibrate(200L);
                }
                AndroidUtilities.shakeView(getViewByType(tL_secureRequiredType));
                return;
            }
            arrayList2.add(new C1ValueToSend(valueByType, tL_secureRequiredType.selfie_required, tL_secureRequiredType.translation_required));
        }
        showEditDoneProgress(false, true);
        TLRPC.TL_account_acceptAuthorization tL_account_acceptAuthorization = new TLRPC.TL_account_acceptAuthorization();
        tL_account_acceptAuthorization.bot_id = this.currentBotId;
        tL_account_acceptAuthorization.scope = this.currentScope;
        tL_account_acceptAuthorization.public_key = this.currentPublicKey;
        JSONObject jSONObject = new JSONObject();
        int size3 = arrayList2.size();
        int i4 = 0;
        while (i4 < size3) {
            C1ValueToSend c1ValueToSend = (C1ValueToSend) arrayList2.get(i4);
            TLRPC.TL_secureValue tL_secureValue = c1ValueToSend.value;
            JSONObject jSONObject2 = new JSONObject();
            TLRPC.SecurePlainData securePlainData = tL_secureValue.plain_data;
            if (securePlainData == null) {
                try {
                    JSONObject jSONObject3 = new JSONObject();
                    TLRPC.TL_secureData tL_secureData = tL_secureValue.data;
                    if (tL_secureData != null) {
                        byte[] decryptValueSecret = decryptValueSecret(tL_secureData.secret, tL_secureData.data_hash);
                        jSONObject2.put("data_hash", Base64.encodeToString(tL_secureValue.data.data_hash, 2));
                        jSONObject2.put("secret", Base64.encodeToString(decryptValueSecret, 2));
                        jSONObject3.put("data", jSONObject2);
                    }
                    if (tL_secureValue.files.isEmpty()) {
                        arrayList = arrayList2;
                        i = size3;
                    } else {
                        JSONArray jSONArray = new JSONArray();
                        int i5 = 0;
                        for (int size4 = tL_secureValue.files.size(); i5 < size4; size4 = size4) {
                            TLRPC.TL_secureFile tL_secureFile = (TLRPC.TL_secureFile) tL_secureValue.files.get(i5);
                            arrayList = arrayList2;
                            try {
                                i = size3;
                                try {
                                    byte[] decryptValueSecret2 = decryptValueSecret(tL_secureFile.secret, tL_secureFile.file_hash);
                                    JSONObject jSONObject4 = new JSONObject();
                                    jSONObject4.put("file_hash", Base64.encodeToString(tL_secureFile.file_hash, 2));
                                    jSONObject4.put("secret", Base64.encodeToString(decryptValueSecret2, 2));
                                    jSONArray.put(jSONObject4);
                                    i5++;
                                    arrayList2 = arrayList;
                                    size3 = i;
                                } catch (Exception unused) {
                                }
                            } catch (Exception unused2) {
                            }
                        }
                        arrayList = arrayList2;
                        i = size3;
                        jSONObject3.put("files", jSONArray);
                    }
                    TLRPC.SecureFile secureFile = tL_secureValue.front_side;
                    if (secureFile instanceof TLRPC.TL_secureFile) {
                        TLRPC.TL_secureFile tL_secureFile2 = (TLRPC.TL_secureFile) secureFile;
                        byte[] decryptValueSecret3 = decryptValueSecret(tL_secureFile2.secret, tL_secureFile2.file_hash);
                        JSONObject jSONObject5 = new JSONObject();
                        jSONObject5.put("file_hash", Base64.encodeToString(tL_secureFile2.file_hash, 2));
                        jSONObject5.put("secret", Base64.encodeToString(decryptValueSecret3, 2));
                        jSONObject3.put("front_side", jSONObject5);
                    }
                    TLRPC.SecureFile secureFile2 = tL_secureValue.reverse_side;
                    if (secureFile2 instanceof TLRPC.TL_secureFile) {
                        TLRPC.TL_secureFile tL_secureFile3 = (TLRPC.TL_secureFile) secureFile2;
                        byte[] decryptValueSecret4 = decryptValueSecret(tL_secureFile3.secret, tL_secureFile3.file_hash);
                        JSONObject jSONObject6 = new JSONObject();
                        jSONObject6.put("file_hash", Base64.encodeToString(tL_secureFile3.file_hash, 2));
                        jSONObject6.put("secret", Base64.encodeToString(decryptValueSecret4, 2));
                        jSONObject3.put("reverse_side", jSONObject6);
                    }
                    if (c1ValueToSend.selfie_required) {
                        TLRPC.SecureFile secureFile3 = tL_secureValue.selfie;
                        if (secureFile3 instanceof TLRPC.TL_secureFile) {
                            TLRPC.TL_secureFile tL_secureFile4 = (TLRPC.TL_secureFile) secureFile3;
                            byte[] decryptValueSecret5 = decryptValueSecret(tL_secureFile4.secret, tL_secureFile4.file_hash);
                            JSONObject jSONObject7 = new JSONObject();
                            jSONObject7.put("file_hash", Base64.encodeToString(tL_secureFile4.file_hash, 2));
                            jSONObject7.put("secret", Base64.encodeToString(decryptValueSecret5, 2));
                            jSONObject3.put("selfie", jSONObject7);
                        }
                    }
                    if (c1ValueToSend.translation_required && !tL_secureValue.translation.isEmpty()) {
                        JSONArray jSONArray2 = new JSONArray();
                        int size5 = tL_secureValue.translation.size();
                        for (int i6 = 0; i6 < size5; i6++) {
                            TLRPC.TL_secureFile tL_secureFile5 = (TLRPC.TL_secureFile) tL_secureValue.translation.get(i6);
                            byte[] decryptValueSecret6 = decryptValueSecret(tL_secureFile5.secret, tL_secureFile5.file_hash);
                            JSONObject jSONObject8 = new JSONObject();
                            jSONObject8.put("file_hash", Base64.encodeToString(tL_secureFile5.file_hash, 2));
                            jSONObject8.put("secret", Base64.encodeToString(decryptValueSecret6, 2));
                            jSONArray2.put(jSONObject8);
                        }
                        jSONObject3.put("translation", jSONArray2);
                    }
                    jSONObject.put(getNameForType(tL_secureValue.type), jSONObject3);
                } catch (Exception unused3) {
                }
                TLRPC.TL_secureValueHash tL_secureValueHash = new TLRPC.TL_secureValueHash();
                tL_secureValueHash.type = tL_secureValue.type;
                tL_secureValueHash.hash = tL_secureValue.hash;
                tL_account_acceptAuthorization.value_hashes.add(tL_secureValueHash);
                i4++;
                arrayList2 = arrayList;
                size3 = i;
            } else if (securePlainData instanceof TLRPC.TL_securePlainEmail) {
            } else if (securePlainData instanceof TLRPC.TL_securePlainPhone) {
            }
            arrayList = arrayList2;
            i = size3;
            TLRPC.TL_secureValueHash tL_secureValueHash2 = new TLRPC.TL_secureValueHash();
            tL_secureValueHash2.type = tL_secureValue.type;
            tL_secureValueHash2.hash = tL_secureValue.hash;
            tL_account_acceptAuthorization.value_hashes.add(tL_secureValueHash2);
            i4++;
            arrayList2 = arrayList;
            size3 = i;
        }
        JSONObject jSONObject9 = new JSONObject();
        try {
            jSONObject9.put("secure_data", jSONObject);
        } catch (Exception unused4) {
        }
        Object obj = this.currentPayload;
        if (obj != null) {
            try {
                jSONObject9.put("payload", obj);
            } catch (Exception unused5) {
            }
        }
        Object obj2 = this.currentNonce;
        if (obj2 != null) {
            try {
                jSONObject9.put("nonce", obj2);
            } catch (Exception unused6) {
            }
        }
        EncryptionResult encryptData = encryptData(AndroidUtilities.getStringBytes(jSONObject9.toString()));
        TLRPC.TL_secureCredentialsEncrypted tL_secureCredentialsEncrypted = new TLRPC.TL_secureCredentialsEncrypted();
        tL_account_acceptAuthorization.credentials = tL_secureCredentialsEncrypted;
        tL_secureCredentialsEncrypted.hash = encryptData.fileHash;
        tL_secureCredentialsEncrypted.data = encryptData.encryptedData;
        try {
            RSAPublicKey rSAPublicKey = (RSAPublicKey) KeyFactory.getInstance("RSA").generatePublic(new X509EncodedKeySpec(Base64.decode(this.currentPublicKey.replaceAll("\\n", "").replace("-----BEGIN PUBLIC KEY-----", "").replace("-----END PUBLIC KEY-----", ""), 0)));
            Cipher cipher = Cipher.getInstance("RSA/NONE/OAEPWithSHA1AndMGF1Padding");
            cipher.init(1, rSAPublicKey);
            tL_account_acceptAuthorization.credentials.secret = cipher.doFinal(encryptData.decrypyedFileSecret);
        } catch (Exception e) {
            FileLog.e(e);
        }
        ConnectionsManager.getInstance(this.currentAccount).bindRequestToGuid(ConnectionsManager.getInstance(this.currentAccount).sendRequest(tL_account_acceptAuthorization, new RequestDelegate() {
            @Override
            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                PassportActivity.this.lambda$createRequestInterface$15(tLObject, tL_error);
            }
        }), this.classGuid);
    }

    public void lambda$deleteValueInternal$59(org.telegram.tgnet.TLRPC.TL_error r12, org.telegram.ui.PassportActivity.ErrorRunnable r13, boolean r14, org.telegram.tgnet.TLRPC.TL_secureRequiredType r15, org.telegram.tgnet.TLRPC.TL_secureRequiredType r16, boolean r17, java.util.ArrayList r18, java.lang.Runnable r19) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.PassportActivity.lambda$deleteValueInternal$59(org.telegram.tgnet.TLRPC$TL_error, org.telegram.ui.PassportActivity$ErrorRunnable, boolean, org.telegram.tgnet.TLRPC$TL_secureRequiredType, org.telegram.tgnet.TLRPC$TL_secureRequiredType, boolean, java.util.ArrayList, java.lang.Runnable):void");
    }

    public void lambda$deleteValueInternal$60(final ErrorRunnable errorRunnable, final boolean z, final TLRPC.TL_secureRequiredType tL_secureRequiredType, final TLRPC.TL_secureRequiredType tL_secureRequiredType2, final boolean z2, final ArrayList arrayList, final Runnable runnable, TLObject tLObject, final TLRPC.TL_error tL_error) {
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                PassportActivity.this.lambda$deleteValueInternal$59(tL_error, errorRunnable, z, tL_secureRequiredType, tL_secureRequiredType2, z2, arrayList, runnable);
            }
        });
    }

    public void lambda$loadPasswordInfo$3(TLObject tLObject) {
        if (tLObject != null) {
            TLRPC.account_Password account_password = (TLRPC.account_Password) tLObject;
            this.currentPassword = account_password;
            if (!TwoStepVerificationActivity.canHandleCurrentPassword(account_password, false)) {
                AlertsCreator.showUpdateAppAlert(getParentActivity(), LocaleController.getString(R.string.UpdateAppAlert), true);
                return;
            }
            TwoStepVerificationActivity.initPasswordNewAlgo(this.currentPassword);
            updatePasswordInterface();
            if (this.inputFieldContainers[0].getVisibility() == 0) {
                this.inputFields[0].requestFocus();
                AndroidUtilities.showKeyboard(this.inputFields[0]);
            }
            if (this.usingSavedPassword == 1) {
                onPasswordDone(true);
            }
        }
    }

    public void lambda$loadPasswordInfo$4(final TLObject tLObject, TLRPC.TL_error tL_error) {
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                PassportActivity.this.lambda$loadPasswordInfo$3(tLObject);
            }
        });
    }

    public static void lambda$new$0(TLObject tLObject) {
        if (!(tLObject instanceof TLRPC.TL_help_passportConfig)) {
            SharedConfig.getCountryLangs();
        } else {
            TLRPC.TL_help_passportConfig tL_help_passportConfig = (TLRPC.TL_help_passportConfig) tLObject;
            SharedConfig.setPassportConfig(tL_help_passportConfig.countries_langs.data, tL_help_passportConfig.hash);
        }
    }

    public static void lambda$new$1(final TLObject tLObject, TLRPC.TL_error tL_error) {
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                PassportActivity.lambda$new$0(TLObject.this);
            }
        });
    }

    public void lambda$onPasswordDone$13(boolean r11, java.lang.String r12) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.PassportActivity.lambda$onPasswordDone$13(boolean, java.lang.String):void");
    }

    public void lambda$onRequestPermissionsResultFragment$68(DialogInterface dialogInterface, int i) {
        try {
            Intent intent = new Intent("android.settings.APPLICATION_DETAILS_SETTINGS");
            intent.setData(Uri.parse("package:" + ApplicationLoader.applicationContext.getPackageName()));
            getParentActivity().startActivity(intent);
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    public void lambda$onResume$2() {
        ViewGroup viewGroup;
        ViewGroup[] viewGroupArr = this.inputFieldContainers;
        if (viewGroupArr == null || (viewGroup = viewGroupArr[0]) == null || viewGroup.getVisibility() != 0) {
            return;
        }
        this.inputFields[0].requestFocus();
        AndroidUtilities.showKeyboard(this.inputFields[0]);
    }

    public void lambda$onTransitionAnimationEnd$67() {
        presentFragment(this.presentAfterAnimation, true);
        this.presentAfterAnimation = null;
    }

    public void lambda$openAddDocumentAlert$23(java.util.ArrayList r2, android.content.DialogInterface r3, int r4) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.PassportActivity.lambda$openAddDocumentAlert$23(java.util.ArrayList, android.content.DialogInterface, int):void");
    }

    public void lambda$processSelectedFiles$70(SecureDocument secureDocument, int i) {
        int i2 = this.uploadingFileType;
        if (i2 == 1) {
            SecureDocument secureDocument2 = this.selfieDocument;
            if (secureDocument2 != null) {
                SecureDocumentCell secureDocumentCell = (SecureDocumentCell) this.documentsCells.remove(secureDocument2);
                if (secureDocumentCell != null) {
                    this.selfieLayout.removeView(secureDocumentCell);
                }
                this.selfieDocument = null;
            }
        } else if (i2 == 4) {
            if (this.translationDocuments.size() >= 20) {
                return;
            }
        } else if (i2 == 2) {
            SecureDocument secureDocument3 = this.frontDocument;
            if (secureDocument3 != null) {
                SecureDocumentCell secureDocumentCell2 = (SecureDocumentCell) this.documentsCells.remove(secureDocument3);
                if (secureDocumentCell2 != null) {
                    this.frontLayout.removeView(secureDocumentCell2);
                }
                this.frontDocument = null;
            }
        } else if (i2 == 3) {
            SecureDocument secureDocument4 = this.reverseDocument;
            if (secureDocument4 != null) {
                SecureDocumentCell secureDocumentCell3 = (SecureDocumentCell) this.documentsCells.remove(secureDocument4);
                if (secureDocumentCell3 != null) {
                    this.reverseLayout.removeView(secureDocumentCell3);
                }
                this.reverseDocument = null;
            }
        } else if (i2 == 0 && this.documents.size() >= 20) {
            return;
        }
        this.uploadingDocuments.put(secureDocument.path, secureDocument);
        this.doneItem.setEnabled(false);
        this.doneItem.setAlpha(0.5f);
        FileLoader.getInstance(this.currentAccount).uploadFile(secureDocument.path, false, true, 16777216);
        addDocumentView(secureDocument, i);
        updateUploadText(i);
    }

    public void lambda$processSelectedFiles$71(MrzRecognizer.Result result) {
        TLRPC.TL_secureRequiredType tL_secureRequiredType;
        int i;
        int i2;
        EditTextBoldCursor editTextBoldCursor;
        int i3;
        int i4 = result.type;
        if (i4 == 2) {
            if (!(this.currentDocumentsType.type instanceof TLRPC.TL_secureValueTypeIdentityCard)) {
                int size = this.availableDocumentTypes.size();
                for (int i5 = 0; i5 < size; i5++) {
                    tL_secureRequiredType = (TLRPC.TL_secureRequiredType) this.availableDocumentTypes.get(i5);
                    if (tL_secureRequiredType.type instanceof TLRPC.TL_secureValueTypeIdentityCard) {
                        this.currentDocumentsType = tL_secureRequiredType;
                        updateInterfaceStringsForDocumentType();
                        break;
                    }
                }
            }
        } else if (i4 == 1) {
            if (!(this.currentDocumentsType.type instanceof TLRPC.TL_secureValueTypePassport)) {
                int size2 = this.availableDocumentTypes.size();
                for (int i6 = 0; i6 < size2; i6++) {
                    tL_secureRequiredType = (TLRPC.TL_secureRequiredType) this.availableDocumentTypes.get(i6);
                    if (tL_secureRequiredType.type instanceof TLRPC.TL_secureValueTypePassport) {
                        this.currentDocumentsType = tL_secureRequiredType;
                        updateInterfaceStringsForDocumentType();
                        break;
                    }
                }
            }
        } else if (i4 == 3) {
            if (!(this.currentDocumentsType.type instanceof TLRPC.TL_secureValueTypeInternalPassport)) {
                int size3 = this.availableDocumentTypes.size();
                for (int i7 = 0; i7 < size3; i7++) {
                    tL_secureRequiredType = (TLRPC.TL_secureRequiredType) this.availableDocumentTypes.get(i7);
                    if (tL_secureRequiredType.type instanceof TLRPC.TL_secureValueTypeInternalPassport) {
                        this.currentDocumentsType = tL_secureRequiredType;
                        updateInterfaceStringsForDocumentType();
                        break;
                    }
                }
            }
        } else if (i4 == 4 && !(this.currentDocumentsType.type instanceof TLRPC.TL_secureValueTypeDriverLicense)) {
            int size4 = this.availableDocumentTypes.size();
            for (int i8 = 0; i8 < size4; i8++) {
                tL_secureRequiredType = (TLRPC.TL_secureRequiredType) this.availableDocumentTypes.get(i8);
                if (tL_secureRequiredType.type instanceof TLRPC.TL_secureValueTypeDriverLicense) {
                    this.currentDocumentsType = tL_secureRequiredType;
                    updateInterfaceStringsForDocumentType();
                    break;
                }
            }
        }
        if (!TextUtils.isEmpty(result.firstName)) {
            this.inputFields[0].setText(result.firstName);
        }
        if (!TextUtils.isEmpty(result.middleName)) {
            this.inputFields[1].setText(result.middleName);
        }
        if (!TextUtils.isEmpty(result.lastName)) {
            this.inputFields[2].setText(result.lastName);
        }
        if (!TextUtils.isEmpty(result.number)) {
            this.inputFields[7].setText(result.number);
        }
        int i9 = result.gender;
        if (i9 != 0) {
            if (i9 == 1) {
                this.currentGender = "male";
                editTextBoldCursor = this.inputFields[4];
                i3 = R.string.PassportMale;
            } else if (i9 == 2) {
                this.currentGender = "female";
                editTextBoldCursor = this.inputFields[4];
                i3 = R.string.PassportFemale;
            }
            editTextBoldCursor.setText(LocaleController.getString(i3));
        }
        if (!TextUtils.isEmpty(result.nationality)) {
            String str = result.nationality;
            this.currentCitizeship = str;
            String str2 = (String) this.languageMap.get(str);
            if (str2 != null) {
                this.inputFields[5].setText(str2);
            }
        }
        if (!TextUtils.isEmpty(result.issuingCountry)) {
            String str3 = result.issuingCountry;
            this.currentResidence = str3;
            String str4 = (String) this.languageMap.get(str3);
            if (str4 != null) {
                this.inputFields[6].setText(str4);
            }
        }
        int i10 = result.birthDay;
        if (i10 > 0 && result.birthMonth > 0 && result.birthYear > 0) {
            this.inputFields[3].setText(String.format(Locale.US, "%02d.%02d.%d", Integer.valueOf(i10), Integer.valueOf(result.birthMonth), Integer.valueOf(result.birthYear)));
        }
        int i11 = result.expiryDay;
        if (i11 <= 0 || (i = result.expiryMonth) <= 0 || (i2 = result.expiryYear) <= 0) {
            int[] iArr = this.currentExpireDate;
            iArr[2] = 0;
            iArr[1] = 0;
            iArr[0] = 0;
            this.inputFields[8].setText(LocaleController.getString(R.string.PassportNoExpireDate));
            return;
        }
        int[] iArr2 = this.currentExpireDate;
        iArr2[0] = i2;
        iArr2[1] = i;
        iArr2[2] = i11;
        this.inputFields[8].setText(String.format(Locale.US, "%02d.%02d.%d", Integer.valueOf(i11), Integer.valueOf(result.expiryMonth), Integer.valueOf(result.expiryYear)));
    }

    public void lambda$processSelectedFiles$72(ArrayList arrayList, final int i, boolean z) {
        TLRPC.PhotoSize scaleAndSaveImage;
        int i2 = this.uploadingFileType;
        int min = Math.min((i2 == 0 || i2 == 4) ? 20 : 1, arrayList.size());
        boolean z2 = false;
        for (int i3 = 0; i3 < min; i3++) {
            SendMessagesHelper.SendingMediaInfo sendingMediaInfo = (SendMessagesHelper.SendingMediaInfo) arrayList.get(i3);
            Bitmap loadBitmap = ImageLoader.loadBitmap(sendingMediaInfo.path, sendingMediaInfo.uri, 2048.0f, 2048.0f, false);
            if (loadBitmap != null && (scaleAndSaveImage = ImageLoader.scaleAndSaveImage(loadBitmap, 2048.0f, 2048.0f, 89, false, 320, 320)) != null) {
                TLRPC.TL_secureFile tL_secureFile = new TLRPC.TL_secureFile();
                tL_secureFile.dc_id = (int) scaleAndSaveImage.location.volume_id;
                tL_secureFile.id = r9.local_id;
                tL_secureFile.date = (int) (System.currentTimeMillis() / 1000);
                final SecureDocument saveFile = this.delegate.saveFile(tL_secureFile);
                saveFile.type = i;
                AndroidUtilities.runOnUIThread(new Runnable() {
                    @Override
                    public final void run() {
                        PassportActivity.this.lambda$processSelectedFiles$70(saveFile, i);
                    }
                });
                if (z && !z2) {
                    try {
                        final MrzRecognizer.Result recognize = MrzRecognizer.recognize(loadBitmap, this.currentDocumentsType.type instanceof TLRPC.TL_secureValueTypeDriverLicense);
                        if (recognize != null) {
                            try {
                                AndroidUtilities.runOnUIThread(new Runnable() {
                                    @Override
                                    public final void run() {
                                        PassportActivity.this.lambda$processSelectedFiles$71(recognize);
                                    }
                                });
                                z2 = true;
                            } catch (Throwable th) {
                                th = th;
                                z2 = true;
                                FileLog.e(th);
                            }
                        }
                    } catch (Throwable th2) {
                        th = th2;
                    }
                }
            }
        }
        SharedConfig.saveConfig();
    }

    public void lambda$startPhoneVerification$65(TLRPC.TL_error tL_error, String str, PassportActivityDelegate passportActivityDelegate, TLObject tLObject, TLRPC.TL_account_sendVerifyPhoneCode tL_account_sendVerifyPhoneCode) {
        if (tL_error != null) {
            AlertsCreator.processError(this.currentAccount, tL_error, this, tL_account_sendVerifyPhoneCode, str);
            return;
        }
        HashMap hashMap = new HashMap();
        hashMap.put("phone", str);
        PassportActivity passportActivity = new PassportActivity(7, this.currentForm, this.currentPassword, this.currentType, (TLRPC.TL_secureValue) null, (TLRPC.TL_secureRequiredType) null, (TLRPC.TL_secureValue) null, hashMap, (HashMap) null);
        passportActivity.currentAccount = this.currentAccount;
        passportActivity.saltedPassword = this.saltedPassword;
        passportActivity.secureSecret = this.secureSecret;
        passportActivity.delegate = passportActivityDelegate;
        passportActivity.currentPhoneVerification = (TLRPC.TL_auth_sentCode) tLObject;
        presentFragment(passportActivity, true);
    }

    public void lambda$startPhoneVerification$66(final String str, final PassportActivityDelegate passportActivityDelegate, final TLRPC.TL_account_sendVerifyPhoneCode tL_account_sendVerifyPhoneCode, final TLObject tLObject, final TLRPC.TL_error tL_error) {
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                PassportActivity.this.lambda$startPhoneVerification$65(tL_error, str, passportActivityDelegate, tLObject, tL_account_sendVerifyPhoneCode);
            }
        });
    }

    private void loadPasswordInfo() {
        ConnectionsManager.getInstance(this.currentAccount).bindRequestToGuid(ConnectionsManager.getInstance(this.currentAccount).sendRequest(new TLRPC.TL_account_getPassword(), new RequestDelegate() {
            @Override
            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                PassportActivity.this.lambda$loadPasswordInfo$4(tLObject, tL_error);
            }
        }), this.classGuid);
    }

    public void onFieldError(View view) {
        if (view == null) {
            return;
        }
        Vibrator vibrator = (Vibrator) getParentActivity().getSystemService("vibrator");
        if (vibrator != null) {
            vibrator.vibrate(200L);
        }
        AndroidUtilities.shakeView(view);
        scrollToField(view);
    }

    public void onPasscodeError(boolean z) {
        if (getParentActivity() == null) {
            return;
        }
        Vibrator vibrator = (Vibrator) getParentActivity().getSystemService("vibrator");
        if (vibrator != null) {
            vibrator.vibrate(200L);
        }
        if (z) {
            this.inputFields[0].setText("");
        }
        AndroidUtilities.shakeView(this.inputFields[0]);
    }

    public void onPasswordDone(final boolean z) {
        final String obj;
        if (z) {
            obj = null;
        } else {
            obj = this.inputFields[0].getText().toString();
            if (TextUtils.isEmpty(obj)) {
                onPasscodeError(false);
                return;
            }
            showEditDoneProgress(true, true);
        }
        Utilities.globalQueue.postRunnable(new Runnable() {
            @Override
            public final void run() {
                PassportActivity.this.lambda$onPasswordDone$13(z, obj);
            }
        });
    }

    private void openAddDocumentAlert() {
        ArrayList arrayList = new ArrayList();
        final ArrayList arrayList2 = new ArrayList();
        if (hasNotValueForType(TLRPC.TL_secureValueTypePhone.class)) {
            arrayList.add(LocaleController.getString(R.string.ActionBotDocumentPhone));
            arrayList2.add(TLRPC.TL_secureValueTypePhone.class);
        }
        if (hasNotValueForType(TLRPC.TL_secureValueTypeEmail.class)) {
            arrayList.add(LocaleController.getString(R.string.ActionBotDocumentEmail));
            arrayList2.add(TLRPC.TL_secureValueTypeEmail.class);
        }
        if (hasNotValueForType(TLRPC.TL_secureValueTypePersonalDetails.class)) {
            arrayList.add(LocaleController.getString(R.string.ActionBotDocumentIdentity));
            arrayList2.add(TLRPC.TL_secureValueTypePersonalDetails.class);
        }
        if (hasNotValueForType(TLRPC.TL_secureValueTypePassport.class)) {
            arrayList.add(LocaleController.getString(R.string.ActionBotDocumentPassport));
            arrayList2.add(TLRPC.TL_secureValueTypePassport.class);
        }
        if (hasNotValueForType(TLRPC.TL_secureValueTypeInternalPassport.class)) {
            arrayList.add(LocaleController.getString(R.string.ActionBotDocumentInternalPassport));
            arrayList2.add(TLRPC.TL_secureValueTypeInternalPassport.class);
        }
        if (hasNotValueForType(TLRPC.TL_secureValueTypePassportRegistration.class)) {
            arrayList.add(LocaleController.getString(R.string.ActionBotDocumentPassportRegistration));
            arrayList2.add(TLRPC.TL_secureValueTypePassportRegistration.class);
        }
        if (hasNotValueForType(TLRPC.TL_secureValueTypeTemporaryRegistration.class)) {
            arrayList.add(LocaleController.getString(R.string.ActionBotDocumentTemporaryRegistration));
            arrayList2.add(TLRPC.TL_secureValueTypeTemporaryRegistration.class);
        }
        if (hasNotValueForType(TLRPC.TL_secureValueTypeIdentityCard.class)) {
            arrayList.add(LocaleController.getString(R.string.ActionBotDocumentIdentityCard));
            arrayList2.add(TLRPC.TL_secureValueTypeIdentityCard.class);
        }
        if (hasNotValueForType(TLRPC.TL_secureValueTypeDriverLicense.class)) {
            arrayList.add(LocaleController.getString(R.string.ActionBotDocumentDriverLicence));
            arrayList2.add(TLRPC.TL_secureValueTypeDriverLicense.class);
        }
        if (hasNotValueForType(TLRPC.TL_secureValueTypeAddress.class)) {
            arrayList.add(LocaleController.getString(R.string.ActionBotDocumentAddress));
            arrayList2.add(TLRPC.TL_secureValueTypeAddress.class);
        }
        if (hasNotValueForType(TLRPC.TL_secureValueTypeUtilityBill.class)) {
            arrayList.add(LocaleController.getString(R.string.ActionBotDocumentUtilityBill));
            arrayList2.add(TLRPC.TL_secureValueTypeUtilityBill.class);
        }
        if (hasNotValueForType(TLRPC.TL_secureValueTypeBankStatement.class)) {
            arrayList.add(LocaleController.getString(R.string.ActionBotDocumentBankStatement));
            arrayList2.add(TLRPC.TL_secureValueTypeBankStatement.class);
        }
        if (hasNotValueForType(TLRPC.TL_secureValueTypeRentalAgreement.class)) {
            arrayList.add(LocaleController.getString(R.string.ActionBotDocumentRentalAgreement));
            arrayList2.add(TLRPC.TL_secureValueTypeRentalAgreement.class);
        }
        if (getParentActivity() == null || arrayList.isEmpty()) {
            return;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(getParentActivity());
        builder.setTitle(LocaleController.getString(R.string.PassportNoDocumentsAdd));
        builder.setItems((CharSequence[]) arrayList.toArray(new CharSequence[0]), new DialogInterface.OnClickListener() {
            @Override
            public final void onClick(DialogInterface dialogInterface, int i) {
                PassportActivity.this.lambda$openAddDocumentAlert$23(arrayList2, dialogInterface, i);
            }
        });
        showDialog(builder.create());
    }

    private void openAttachMenu() {
        if (getParentActivity() == null) {
            return;
        }
        if (this.uploadingFileType == 0 && this.documents.size() >= 20) {
            showAlertWithText(LocaleController.getString(R.string.AppName), LocaleController.formatString("PassportUploadMaxReached", R.string.PassportUploadMaxReached, LocaleController.formatPluralString("Files", 20, new Object[0])));
            return;
        }
        createChatAttachView();
        this.chatAttachAlert.setOpenWithFrontFaceCamera(this.uploadingFileType == 1);
        this.chatAttachAlert.setMaxSelectedPhotos(getMaxSelectedDocuments(), false);
        this.chatAttachAlert.getPhotoLayout().loadGalleryPhotos();
        int i = Build.VERSION.SDK_INT;
        if (i == 21 || i == 22) {
            AndroidUtilities.hideKeyboard(this.fragmentView.findFocus());
        }
        this.chatAttachAlert.init();
        showDialog(this.chatAttachAlert);
    }

    private void openTypeActivity(TLRPC.TL_secureRequiredType tL_secureRequiredType, TLRPC.TL_secureRequiredType tL_secureRequiredType2, ArrayList arrayList, final boolean z) {
        TLRPC.account_Password account_password;
        HashMap hashMap;
        final int size = arrayList != null ? arrayList.size() : 0;
        final TLRPC.SecureValueType secureValueType = tL_secureRequiredType.type;
        TLRPC.SecureValueType secureValueType2 = tL_secureRequiredType2 != null ? tL_secureRequiredType2.type : null;
        int i = secureValueType instanceof TLRPC.TL_secureValueTypePersonalDetails ? 1 : secureValueType instanceof TLRPC.TL_secureValueTypeAddress ? 2 : secureValueType instanceof TLRPC.TL_secureValueTypePhone ? 3 : secureValueType instanceof TLRPC.TL_secureValueTypeEmail ? 4 : -1;
        if (i != -1) {
            HashMap hashMap2 = !z ? (HashMap) this.errorsMap.get(getNameForType(secureValueType)) : null;
            HashMap hashMap3 = (HashMap) this.errorsMap.get(getNameForType(secureValueType2));
            TLRPC.TL_secureValue valueByType = getValueByType(tL_secureRequiredType, false);
            TLRPC.TL_secureValue valueByType2 = getValueByType(tL_secureRequiredType2, false);
            TLRPC.TL_account_authorizationForm tL_account_authorizationForm = this.currentForm;
            TLRPC.account_Password account_password2 = this.currentPassword;
            HashMap hashMap4 = (HashMap) this.typesValues.get(tL_secureRequiredType);
            if (tL_secureRequiredType2 != null) {
                account_password = account_password2;
                hashMap = (HashMap) this.typesValues.get(tL_secureRequiredType2);
            } else {
                account_password = account_password2;
                hashMap = null;
            }
            int i2 = i;
            PassportActivity passportActivity = new PassportActivity(i, tL_account_authorizationForm, account_password, tL_secureRequiredType, valueByType, tL_secureRequiredType2, valueByType2, hashMap4, hashMap);
            passportActivity.delegate = new PassportActivityDelegate() {

                public class AnonymousClass1 implements RequestDelegate {
                    final PassportActivityDelegate val$currentDelegate;
                    final TLRPC.TL_secureRequiredType val$documentRequiredType;
                    final ArrayList val$documents;
                    final String val$documentsJson;
                    final ErrorRunnable val$errorRunnable;
                    final TLRPC.TL_inputSecureValue val$finalFileInputSecureValue;
                    final Runnable val$finishRunnable;
                    final SecureDocument val$front;
                    final String val$json;
                    final TLRPC.TL_account_saveSecureValue val$req;
                    final TLRPC.TL_secureRequiredType val$requiredType;
                    final SecureDocument val$reverse;
                    final SecureDocument val$selfie;
                    final String val$text;
                    final ArrayList val$translationDocuments;

                    AnonymousClass1(ErrorRunnable errorRunnable, String str, TLRPC.TL_account_saveSecureValue tL_account_saveSecureValue, TLRPC.TL_secureRequiredType tL_secureRequiredType, TLRPC.TL_secureRequiredType tL_secureRequiredType2, ArrayList arrayList, SecureDocument secureDocument, SecureDocument secureDocument2, SecureDocument secureDocument3, ArrayList arrayList2, String str2, String str3, Runnable runnable, PassportActivityDelegate passportActivityDelegate, TLRPC.TL_inputSecureValue tL_inputSecureValue) {
                        this.val$errorRunnable = errorRunnable;
                        this.val$text = str;
                        this.val$req = tL_account_saveSecureValue;
                        this.val$documentRequiredType = tL_secureRequiredType;
                        this.val$requiredType = tL_secureRequiredType2;
                        this.val$documents = arrayList;
                        this.val$selfie = secureDocument;
                        this.val$front = secureDocument2;
                        this.val$reverse = secureDocument3;
                        this.val$translationDocuments = arrayList2;
                        this.val$json = str2;
                        this.val$documentsJson = str3;
                        this.val$finishRunnable = runnable;
                        this.val$currentDelegate = passportActivityDelegate;
                        this.val$finalFileInputSecureValue = tL_inputSecureValue;
                    }

                    public void lambda$onResult$0(org.telegram.tgnet.TLRPC.TL_error r17, org.telegram.ui.PassportActivity.ErrorRunnable r18, java.lang.String r19, org.telegram.tgnet.TLRPC.TL_account_saveSecureValue r20, boolean r21, org.telegram.tgnet.TLRPC.TL_secureRequiredType r22, org.telegram.tgnet.TLRPC.TL_secureRequiredType r23, org.telegram.tgnet.TLRPC.TL_secureValue r24, org.telegram.tgnet.TLRPC.TL_secureValue r25, java.util.ArrayList r26, org.telegram.messenger.SecureDocument r27, org.telegram.messenger.SecureDocument r28, org.telegram.messenger.SecureDocument r29, java.util.ArrayList r30, java.lang.String r31, java.lang.String r32, int r33, java.lang.Runnable r34) {
                        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.PassportActivity.AnonymousClass20.AnonymousClass1.lambda$onResult$0(org.telegram.tgnet.TLRPC$TL_error, org.telegram.ui.PassportActivity$ErrorRunnable, java.lang.String, org.telegram.tgnet.TLRPC$TL_account_saveSecureValue, boolean, org.telegram.tgnet.TLRPC$TL_secureRequiredType, org.telegram.tgnet.TLRPC$TL_secureRequiredType, org.telegram.tgnet.TLRPC$TL_secureValue, org.telegram.tgnet.TLRPC$TL_secureValue, java.util.ArrayList, org.telegram.messenger.SecureDocument, org.telegram.messenger.SecureDocument, org.telegram.messenger.SecureDocument, java.util.ArrayList, java.lang.String, java.lang.String, int, java.lang.Runnable):void");
                    }

                    public void lambda$run$1(TLObject tLObject, String str, TLRPC.TL_secureRequiredType tL_secureRequiredType, PassportActivityDelegate passportActivityDelegate, TLRPC.TL_error tL_error, ErrorRunnable errorRunnable) {
                        if (tLObject == null) {
                            PassportActivity.this.showAlertWithText(LocaleController.getString(R.string.PassportEmail), tL_error.text);
                            if (errorRunnable != null) {
                                errorRunnable.onError(tL_error.text, str);
                                return;
                            }
                            return;
                        }
                        TLRPC.TL_account_sentEmailCode tL_account_sentEmailCode = (TLRPC.TL_account_sentEmailCode) tLObject;
                        HashMap hashMap = new HashMap();
                        hashMap.put("email", str);
                        hashMap.put("pattern", tL_account_sentEmailCode.email_pattern);
                        PassportActivity passportActivity = new PassportActivity(6, PassportActivity.this.currentForm, PassportActivity.this.currentPassword, tL_secureRequiredType, (TLRPC.TL_secureValue) null, (TLRPC.TL_secureRequiredType) null, (TLRPC.TL_secureValue) null, hashMap, (HashMap) null);
                        ((BaseFragment) passportActivity).currentAccount = ((BaseFragment) PassportActivity.this).currentAccount;
                        passportActivity.emailCodeLength = tL_account_sentEmailCode.length;
                        passportActivity.saltedPassword = PassportActivity.this.saltedPassword;
                        passportActivity.secureSecret = PassportActivity.this.secureSecret;
                        passportActivity.delegate = passportActivityDelegate;
                        PassportActivity.this.presentFragment(passportActivity, true);
                    }

                    public void lambda$run$2(final String str, final TLRPC.TL_secureRequiredType tL_secureRequiredType, final PassportActivityDelegate passportActivityDelegate, final ErrorRunnable errorRunnable, final TLObject tLObject, final TLRPC.TL_error tL_error) {
                        AndroidUtilities.runOnUIThread(new Runnable() {
                            @Override
                            public final void run() {
                                PassportActivity.AnonymousClass20.AnonymousClass1.this.lambda$run$1(tLObject, str, tL_secureRequiredType, passportActivityDelegate, tL_error, errorRunnable);
                            }
                        });
                    }

                    public static void lambda$run$3(ErrorRunnable errorRunnable, TLRPC.TL_error tL_error, String str) {
                        errorRunnable.onError(tL_error.text, str);
                    }

                    public void lambda$run$4(final TLRPC.TL_error tL_error, final TLRPC.TL_secureValue tL_secureValue, final TLRPC.TL_secureValue tL_secureValue2) {
                        final ErrorRunnable errorRunnable = this.val$errorRunnable;
                        final String str = this.val$text;
                        final TLRPC.TL_account_saveSecureValue tL_account_saveSecureValue = this.val$req;
                        AnonymousClass20 anonymousClass20 = AnonymousClass20.this;
                        final boolean z = z;
                        final TLRPC.TL_secureRequiredType tL_secureRequiredType = this.val$documentRequiredType;
                        final TLRPC.TL_secureRequiredType tL_secureRequiredType2 = this.val$requiredType;
                        final ArrayList arrayList = this.val$documents;
                        final SecureDocument secureDocument = this.val$selfie;
                        final SecureDocument secureDocument2 = this.val$front;
                        final SecureDocument secureDocument3 = this.val$reverse;
                        final ArrayList arrayList2 = this.val$translationDocuments;
                        final String str2 = this.val$json;
                        final String str3 = this.val$documentsJson;
                        final int i = size;
                        final Runnable runnable = this.val$finishRunnable;
                        AndroidUtilities.runOnUIThread(new Runnable() {
                            @Override
                            public final void run() {
                                PassportActivity.AnonymousClass20.AnonymousClass1.this.lambda$onResult$0(tL_error, errorRunnable, str, tL_account_saveSecureValue, z, tL_secureRequiredType, tL_secureRequiredType2, tL_secureValue, tL_secureValue2, arrayList, secureDocument, secureDocument2, secureDocument3, arrayList2, str2, str3, i, runnable);
                            }
                        });
                    }

                    @Override
                    public void run(TLObject tLObject, final TLRPC.TL_error tL_error) {
                        if (tL_error != null) {
                            if (tL_error.text.equals("EMAIL_VERIFICATION_NEEDED")) {
                                TLRPC.TL_account_sendVerifyEmailCode tL_account_sendVerifyEmailCode = new TLRPC.TL_account_sendVerifyEmailCode();
                                tL_account_sendVerifyEmailCode.purpose = new TLRPC.TL_emailVerifyPurposePassport();
                                tL_account_sendVerifyEmailCode.email = this.val$text;
                                ConnectionsManager connectionsManager = ConnectionsManager.getInstance(((BaseFragment) PassportActivity.this).currentAccount);
                                final String str = this.val$text;
                                final TLRPC.TL_secureRequiredType tL_secureRequiredType = this.val$requiredType;
                                final PassportActivityDelegate passportActivityDelegate = this.val$currentDelegate;
                                final ErrorRunnable errorRunnable = this.val$errorRunnable;
                                connectionsManager.sendRequest(tL_account_sendVerifyEmailCode, new RequestDelegate() {
                                    @Override
                                    public final void run(TLObject tLObject2, TLRPC.TL_error tL_error2) {
                                        PassportActivity.AnonymousClass20.AnonymousClass1.this.lambda$run$2(str, tL_secureRequiredType, passportActivityDelegate, errorRunnable, tLObject2, tL_error2);
                                    }
                                });
                                return;
                            }
                            if (tL_error.text.equals("PHONE_VERIFICATION_NEEDED")) {
                                final ErrorRunnable errorRunnable2 = this.val$errorRunnable;
                                final String str2 = this.val$text;
                                AndroidUtilities.runOnUIThread(new Runnable() {
                                    @Override
                                    public final void run() {
                                        PassportActivity.AnonymousClass20.AnonymousClass1.lambda$run$3(PassportActivity.ErrorRunnable.this, tL_error, str2);
                                    }
                                });
                                return;
                            }
                        }
                        if (tL_error != null || this.val$finalFileInputSecureValue == null) {
                            lambda$run$4(tL_error, (TLRPC.TL_secureValue) tLObject, null);
                            return;
                        }
                        final TLRPC.TL_secureValue tL_secureValue = (TLRPC.TL_secureValue) tLObject;
                        TLRPC.TL_account_saveSecureValue tL_account_saveSecureValue = new TLRPC.TL_account_saveSecureValue();
                        tL_account_saveSecureValue.value = this.val$finalFileInputSecureValue;
                        tL_account_saveSecureValue.secure_secret_id = PassportActivity.this.secureSecretId;
                        ConnectionsManager.getInstance(((BaseFragment) PassportActivity.this).currentAccount).sendRequest(tL_account_saveSecureValue, new RequestDelegate() {
                            @Override
                            public final void run(TLObject tLObject2, TLRPC.TL_error tL_error2) {
                                PassportActivity.AnonymousClass20.AnonymousClass1.this.lambda$run$4(tL_secureValue, tLObject2, tL_error2);
                            }
                        });
                    }
                }

                private TLRPC.InputSecureFile getInputSecureFile(SecureDocument secureDocument) {
                    if (secureDocument.inputFile == null) {
                        TLRPC.TL_inputSecureFile tL_inputSecureFile = new TLRPC.TL_inputSecureFile();
                        TLRPC.TL_secureFile tL_secureFile = secureDocument.secureFile;
                        tL_inputSecureFile.id = tL_secureFile.id;
                        tL_inputSecureFile.access_hash = tL_secureFile.access_hash;
                        return tL_inputSecureFile;
                    }
                    TLRPC.TL_inputSecureFileUploaded tL_inputSecureFileUploaded = new TLRPC.TL_inputSecureFileUploaded();
                    TLRPC.TL_inputFile tL_inputFile = secureDocument.inputFile;
                    tL_inputSecureFileUploaded.id = tL_inputFile.id;
                    tL_inputSecureFileUploaded.parts = tL_inputFile.parts;
                    tL_inputSecureFileUploaded.md5_checksum = tL_inputFile.md5_checksum;
                    tL_inputSecureFileUploaded.file_hash = secureDocument.fileHash;
                    tL_inputSecureFileUploaded.secret = secureDocument.fileSecret;
                    return tL_inputSecureFileUploaded;
                }

                public void renameFile(SecureDocument secureDocument, TLRPC.TL_secureFile tL_secureFile) {
                    File pathToAttach = FileLoader.getInstance(UserConfig.selectedAccount).getPathToAttach(secureDocument);
                    String str = secureDocument.secureFile.dc_id + "_" + secureDocument.secureFile.id;
                    File pathToAttach2 = FileLoader.getInstance(UserConfig.selectedAccount).getPathToAttach(tL_secureFile);
                    String str2 = tL_secureFile.dc_id + "_" + tL_secureFile.id;
                    pathToAttach.renameTo(pathToAttach2);
                    ImageLoader.getInstance().replaceImageInCache(str, str2, null, false);
                }

                @Override
                public void deleteValue(TLRPC.TL_secureRequiredType tL_secureRequiredType3, TLRPC.TL_secureRequiredType tL_secureRequiredType4, ArrayList arrayList2, boolean z2, Runnable runnable, ErrorRunnable errorRunnable) {
                    PassportActivity.this.deleteValueInternal(tL_secureRequiredType3, tL_secureRequiredType4, arrayList2, z2, runnable, errorRunnable, z);
                }

                @Override
                public SecureDocument saveFile(TLRPC.TL_secureFile tL_secureFile) {
                    String str = FileLoader.getDirectory(4) + "/" + tL_secureFile.dc_id + "_" + tL_secureFile.id + ".jpg";
                    EncryptionResult createSecureDocument = PassportActivity.this.createSecureDocument(str);
                    return new SecureDocument(createSecureDocument.secureDocumentKey, tL_secureFile, str, createSecureDocument.fileHash, createSecureDocument.fileSecret);
                }

                @Override
                public void saveValue(TLRPC.TL_secureRequiredType tL_secureRequiredType3, String str, String str2, TLRPC.TL_secureRequiredType tL_secureRequiredType4, String str3, ArrayList arrayList2, SecureDocument secureDocument, ArrayList arrayList3, SecureDocument secureDocument2, SecureDocument secureDocument3, Runnable runnable, ErrorRunnable errorRunnable) {
                    TLRPC.TL_inputSecureValue tL_inputSecureValue;
                    TLRPC.TL_securePlainPhone tL_securePlainPhone;
                    TLRPC.TL_inputSecureValue tL_inputSecureValue2;
                    if (!TextUtils.isEmpty(str2)) {
                        tL_inputSecureValue = new TLRPC.TL_inputSecureValue();
                        tL_inputSecureValue.type = tL_secureRequiredType3.type;
                        tL_inputSecureValue.flags |= 1;
                        EncryptionResult encryptData = PassportActivity.this.encryptData(AndroidUtilities.getStringBytes(str2));
                        TLRPC.TL_secureData tL_secureData = new TLRPC.TL_secureData();
                        tL_inputSecureValue.data = tL_secureData;
                        tL_secureData.data = encryptData.encryptedData;
                        tL_secureData.data_hash = encryptData.fileHash;
                        tL_secureData.secret = encryptData.fileSecret;
                    } else if (TextUtils.isEmpty(str)) {
                        tL_inputSecureValue = null;
                    } else {
                        TLRPC.SecureValueType secureValueType3 = secureValueType;
                        if (secureValueType3 instanceof TLRPC.TL_secureValueTypeEmail) {
                            TLRPC.TL_securePlainEmail tL_securePlainEmail = new TLRPC.TL_securePlainEmail();
                            tL_securePlainEmail.email = str;
                            tL_securePlainPhone = tL_securePlainEmail;
                        } else {
                            if (!(secureValueType3 instanceof TLRPC.TL_secureValueTypePhone)) {
                                return;
                            }
                            TLRPC.TL_securePlainPhone tL_securePlainPhone2 = new TLRPC.TL_securePlainPhone();
                            tL_securePlainPhone2.phone = str;
                            tL_securePlainPhone = tL_securePlainPhone2;
                        }
                        TLRPC.TL_inputSecureValue tL_inputSecureValue3 = new TLRPC.TL_inputSecureValue();
                        tL_inputSecureValue3.type = tL_secureRequiredType3.type;
                        tL_inputSecureValue3.flags |= 32;
                        tL_inputSecureValue3.plain_data = tL_securePlainPhone;
                        tL_inputSecureValue = tL_inputSecureValue3;
                    }
                    if (!z && tL_inputSecureValue == null) {
                        if (errorRunnable != null) {
                            errorRunnable.onError(null, null);
                            return;
                        }
                        return;
                    }
                    if (tL_secureRequiredType4 != null) {
                        TLRPC.TL_inputSecureValue tL_inputSecureValue4 = new TLRPC.TL_inputSecureValue();
                        tL_inputSecureValue4.type = tL_secureRequiredType4.type;
                        if (!TextUtils.isEmpty(str3)) {
                            tL_inputSecureValue4.flags |= 1;
                            EncryptionResult encryptData2 = PassportActivity.this.encryptData(AndroidUtilities.getStringBytes(str3));
                            TLRPC.TL_secureData tL_secureData2 = new TLRPC.TL_secureData();
                            tL_inputSecureValue4.data = tL_secureData2;
                            tL_secureData2.data = encryptData2.encryptedData;
                            tL_secureData2.data_hash = encryptData2.fileHash;
                            tL_secureData2.secret = encryptData2.fileSecret;
                        }
                        if (secureDocument2 != null) {
                            tL_inputSecureValue4.front_side = getInputSecureFile(secureDocument2);
                            tL_inputSecureValue4.flags |= 2;
                        }
                        if (secureDocument3 != null) {
                            tL_inputSecureValue4.reverse_side = getInputSecureFile(secureDocument3);
                            tL_inputSecureValue4.flags |= 4;
                        }
                        if (secureDocument != null) {
                            tL_inputSecureValue4.selfie = getInputSecureFile(secureDocument);
                            tL_inputSecureValue4.flags |= 8;
                        }
                        if (arrayList3 != null && !arrayList3.isEmpty()) {
                            tL_inputSecureValue4.flags |= 64;
                            int size2 = arrayList3.size();
                            for (int i3 = 0; i3 < size2; i3++) {
                                tL_inputSecureValue4.translation.add(getInputSecureFile((SecureDocument) arrayList3.get(i3)));
                            }
                        }
                        if (arrayList2 != null && !arrayList2.isEmpty()) {
                            tL_inputSecureValue4.flags |= 16;
                            int size3 = arrayList2.size();
                            for (int i4 = 0; i4 < size3; i4++) {
                                tL_inputSecureValue4.files.add(getInputSecureFile((SecureDocument) arrayList2.get(i4)));
                            }
                        }
                        if (!z) {
                            tL_inputSecureValue2 = tL_inputSecureValue4;
                            TLRPC.TL_account_saveSecureValue tL_account_saveSecureValue = new TLRPC.TL_account_saveSecureValue();
                            tL_account_saveSecureValue.value = tL_inputSecureValue;
                            tL_account_saveSecureValue.secure_secret_id = PassportActivity.this.secureSecretId;
                            ConnectionsManager.getInstance(((BaseFragment) PassportActivity.this).currentAccount).sendRequest(tL_account_saveSecureValue, new AnonymousClass1(errorRunnable, str, tL_account_saveSecureValue, tL_secureRequiredType4, tL_secureRequiredType3, arrayList2, secureDocument, secureDocument2, secureDocument3, arrayList3, str2, str3, runnable, this, tL_inputSecureValue2));
                        }
                        tL_inputSecureValue = tL_inputSecureValue4;
                    }
                    tL_inputSecureValue2 = null;
                    TLRPC.TL_account_saveSecureValue tL_account_saveSecureValue2 = new TLRPC.TL_account_saveSecureValue();
                    tL_account_saveSecureValue2.value = tL_inputSecureValue;
                    tL_account_saveSecureValue2.secure_secret_id = PassportActivity.this.secureSecretId;
                    ConnectionsManager.getInstance(((BaseFragment) PassportActivity.this).currentAccount).sendRequest(tL_account_saveSecureValue2, new AnonymousClass1(errorRunnable, str, tL_account_saveSecureValue2, tL_secureRequiredType4, tL_secureRequiredType3, arrayList2, secureDocument, secureDocument2, secureDocument3, arrayList3, str2, str3, runnable, this, tL_inputSecureValue2));
                }
            };
            passportActivity.currentAccount = this.currentAccount;
            passportActivity.saltedPassword = this.saltedPassword;
            passportActivity.secureSecret = this.secureSecret;
            passportActivity.currentBotId = this.currentBotId;
            passportActivity.fieldsErrors = hashMap2;
            passportActivity.documentOnly = z;
            passportActivity.documentsErrors = hashMap3;
            passportActivity.availableDocumentTypes = arrayList;
            if (i2 == 4) {
                passportActivity.currentEmail = this.currentEmail;
            }
            presentFragment(passportActivity);
        }
    }

    public void processSelectedAttach(int i) {
        int checkSelfPermission;
        if (i == 0) {
            int i2 = Build.VERSION.SDK_INT;
            if (i2 >= 23) {
                checkSelfPermission = getParentActivity().checkSelfPermission("android.permission.CAMERA");
                if (checkSelfPermission != 0) {
                    getParentActivity().requestPermissions(new String[]{"android.permission.CAMERA"}, 19);
                    return;
                }
            }
            try {
                Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
                File generatePicturePath = AndroidUtilities.generatePicturePath();
                if (generatePicturePath != null) {
                    if (i2 >= 24) {
                        intent.putExtra("output", FileProvider.getUriForFile(getParentActivity(), ApplicationLoader.getApplicationId() + ".provider", generatePicturePath));
                        intent.addFlags(2);
                        intent.addFlags(1);
                    } else {
                        intent.putExtra("output", Uri.fromFile(generatePicturePath));
                    }
                    this.currentPicturePath = generatePicturePath.getAbsolutePath();
                }
                startActivityForResult(intent, 0);
            } catch (Exception e) {
                FileLog.e(e);
            }
        }
    }

    public void processSelectedFiles(final ArrayList arrayList) {
        if (arrayList.isEmpty()) {
            return;
        }
        int i = this.uploadingFileType;
        final boolean z = false;
        if (i != 1 && i != 4 && (this.currentType.type instanceof TLRPC.TL_secureValueTypePersonalDetails)) {
            int i2 = 0;
            while (true) {
                EditTextBoldCursor[] editTextBoldCursorArr = this.inputFields;
                if (i2 >= editTextBoldCursorArr.length) {
                    z = true;
                    break;
                } else if (i2 != 5 && i2 != 8 && i2 != 4 && i2 != 6 && editTextBoldCursorArr[i2].length() > 0) {
                    break;
                } else {
                    i2++;
                }
            }
        }
        final int i3 = this.uploadingFileType;
        Utilities.globalQueue.postRunnable(new Runnable() {
            @Override
            public final void run() {
                PassportActivity.this.lambda$processSelectedFiles$72(arrayList, i3, z);
            }
        });
    }

    public TLRPC.TL_secureValue removeValue(TLRPC.TL_secureRequiredType tL_secureRequiredType) {
        if (tL_secureRequiredType == null) {
            return null;
        }
        int size = this.currentForm.values.size();
        for (int i = 0; i < size; i++) {
            if (tL_secureRequiredType.type.getClass() == this.currentForm.values.get(i).type.getClass()) {
                return this.currentForm.values.remove(i);
            }
        }
        return null;
    }

    private void scrollToField(View view) {
        while (view != null && this.linearLayout2.indexOfChild(view) < 0) {
            view = (View) view.getParent();
        }
        if (view != null) {
            this.scrollView.smoothScrollTo(0, view.getTop() - ((this.scrollView.getMeasuredHeight() - view.getMeasuredHeight()) / 2));
        }
    }

    private void setFieldValues(java.util.HashMap r7, org.telegram.ui.Components.EditTextBoldCursor r8, java.lang.String r9) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.PassportActivity.setFieldValues(java.util.HashMap, org.telegram.ui.Components.EditTextBoldCursor, java.lang.String):void");
    }

    public void setTypeValue(org.telegram.tgnet.TLRPC.TL_secureRequiredType r31, java.lang.String r32, java.lang.String r33, org.telegram.tgnet.TLRPC.TL_secureRequiredType r34, java.lang.String r35, boolean r36, int r37) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.PassportActivity.setTypeValue(org.telegram.tgnet.TLRPC$TL_secureRequiredType, java.lang.String, java.lang.String, org.telegram.tgnet.TLRPC$TL_secureRequiredType, java.lang.String, boolean, int):void");
    }

    public void showAlertWithText(String str, String str2) {
        if (getParentActivity() == null) {
            return;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(getParentActivity());
        builder.setPositiveButton(LocaleController.getString(R.string.OK), null);
        builder.setTitle(str);
        builder.setMessage(str2);
        showDialog(builder.create());
    }

    private void showAttachmentError() {
        if (getParentActivity() == null) {
            return;
        }
        Toast.makeText(getParentActivity(), LocaleController.getString(R.string.UnsupportedAttachment), 0).show();
    }

    public void showEditDoneProgress(boolean z, final boolean z2) {
        AnimatorSet animatorSet;
        AnimatorListenerAdapter animatorListenerAdapter;
        AnimatorSet animatorSet2 = this.doneItemAnimation;
        if (animatorSet2 != null) {
            animatorSet2.cancel();
        }
        if (z && this.doneItem != null) {
            this.doneItemAnimation = new AnimatorSet();
            if (z2) {
                this.progressView.setVisibility(0);
                this.doneItem.setEnabled(false);
                AnimatorSet animatorSet3 = this.doneItemAnimation;
                View contentView = this.doneItem.getContentView();
                Property property = View.SCALE_X;
                ObjectAnimator ofFloat = ObjectAnimator.ofFloat(contentView, (Property<View, Float>) property, 0.1f);
                View contentView2 = this.doneItem.getContentView();
                Property property2 = View.SCALE_Y;
                ObjectAnimator ofFloat2 = ObjectAnimator.ofFloat(contentView2, (Property<View, Float>) property2, 0.1f);
                View contentView3 = this.doneItem.getContentView();
                Property property3 = View.ALPHA;
                animatorSet3.playTogether(ofFloat, ofFloat2, ObjectAnimator.ofFloat(contentView3, (Property<View, Float>) property3, 0.0f), ObjectAnimator.ofFloat(this.progressView, (Property<ContextProgressView, Float>) property, 1.0f), ObjectAnimator.ofFloat(this.progressView, (Property<ContextProgressView, Float>) property2, 1.0f), ObjectAnimator.ofFloat(this.progressView, (Property<ContextProgressView, Float>) property3, 1.0f));
            } else {
                this.doneItem.getContentView().setVisibility(0);
                this.doneItem.setEnabled(true);
                AnimatorSet animatorSet4 = this.doneItemAnimation;
                ContextProgressView contextProgressView = this.progressView;
                Property property4 = View.SCALE_X;
                ObjectAnimator ofFloat3 = ObjectAnimator.ofFloat(contextProgressView, (Property<ContextProgressView, Float>) property4, 0.1f);
                ContextProgressView contextProgressView2 = this.progressView;
                Property property5 = View.SCALE_Y;
                ObjectAnimator ofFloat4 = ObjectAnimator.ofFloat(contextProgressView2, (Property<ContextProgressView, Float>) property5, 0.1f);
                ContextProgressView contextProgressView3 = this.progressView;
                Property property6 = View.ALPHA;
                animatorSet4.playTogether(ofFloat3, ofFloat4, ObjectAnimator.ofFloat(contextProgressView3, (Property<ContextProgressView, Float>) property6, 0.0f), ObjectAnimator.ofFloat(this.doneItem.getContentView(), (Property<View, Float>) property4, 1.0f), ObjectAnimator.ofFloat(this.doneItem.getContentView(), (Property<View, Float>) property5, 1.0f), ObjectAnimator.ofFloat(this.doneItem.getContentView(), (Property<View, Float>) property6, 1.0f));
            }
            animatorSet = this.doneItemAnimation;
            animatorListenerAdapter = new AnimatorListenerAdapter() {
                @Override
                public void onAnimationCancel(Animator animator) {
                    if (PassportActivity.this.doneItemAnimation == null || !PassportActivity.this.doneItemAnimation.equals(animator)) {
                        return;
                    }
                    PassportActivity.this.doneItemAnimation = null;
                }

                @Override
                public void onAnimationEnd(Animator animator) {
                    if (PassportActivity.this.doneItemAnimation == null || !PassportActivity.this.doneItemAnimation.equals(animator)) {
                        return;
                    }
                    if (z2) {
                        PassportActivity.this.doneItem.getContentView().setVisibility(4);
                    } else {
                        PassportActivity.this.progressView.setVisibility(4);
                    }
                }
            };
        } else {
            if (this.acceptTextView == null) {
                return;
            }
            this.doneItemAnimation = new AnimatorSet();
            if (z2) {
                this.progressViewButton.setVisibility(0);
                this.bottomLayout.setEnabled(false);
                AnimatorSet animatorSet5 = this.doneItemAnimation;
                TextView textView = this.acceptTextView;
                Property property7 = View.SCALE_X;
                ObjectAnimator ofFloat5 = ObjectAnimator.ofFloat(textView, (Property<TextView, Float>) property7, 0.1f);
                TextView textView2 = this.acceptTextView;
                Property property8 = View.SCALE_Y;
                ObjectAnimator ofFloat6 = ObjectAnimator.ofFloat(textView2, (Property<TextView, Float>) property8, 0.1f);
                TextView textView3 = this.acceptTextView;
                Property property9 = View.ALPHA;
                animatorSet5.playTogether(ofFloat5, ofFloat6, ObjectAnimator.ofFloat(textView3, (Property<TextView, Float>) property9, 0.0f), ObjectAnimator.ofFloat(this.progressViewButton, (Property<ContextProgressView, Float>) property7, 1.0f), ObjectAnimator.ofFloat(this.progressViewButton, (Property<ContextProgressView, Float>) property8, 1.0f), ObjectAnimator.ofFloat(this.progressViewButton, (Property<ContextProgressView, Float>) property9, 1.0f));
            } else {
                this.acceptTextView.setVisibility(0);
                this.bottomLayout.setEnabled(true);
                AnimatorSet animatorSet6 = this.doneItemAnimation;
                ContextProgressView contextProgressView4 = this.progressViewButton;
                Property property10 = View.SCALE_X;
                ObjectAnimator ofFloat7 = ObjectAnimator.ofFloat(contextProgressView4, (Property<ContextProgressView, Float>) property10, 0.1f);
                ContextProgressView contextProgressView5 = this.progressViewButton;
                Property property11 = View.SCALE_Y;
                ObjectAnimator ofFloat8 = ObjectAnimator.ofFloat(contextProgressView5, (Property<ContextProgressView, Float>) property11, 0.1f);
                ContextProgressView contextProgressView6 = this.progressViewButton;
                Property property12 = View.ALPHA;
                animatorSet6.playTogether(ofFloat7, ofFloat8, ObjectAnimator.ofFloat(contextProgressView6, (Property<ContextProgressView, Float>) property12, 0.0f), ObjectAnimator.ofFloat(this.acceptTextView, (Property<TextView, Float>) property10, 1.0f), ObjectAnimator.ofFloat(this.acceptTextView, (Property<TextView, Float>) property11, 1.0f), ObjectAnimator.ofFloat(this.acceptTextView, (Property<TextView, Float>) property12, 1.0f));
            }
            animatorSet = this.doneItemAnimation;
            animatorListenerAdapter = new AnimatorListenerAdapter() {
                @Override
                public void onAnimationCancel(Animator animator) {
                    if (PassportActivity.this.doneItemAnimation == null || !PassportActivity.this.doneItemAnimation.equals(animator)) {
                        return;
                    }
                    PassportActivity.this.doneItemAnimation = null;
                }

                @Override
                public void onAnimationEnd(Animator animator) {
                    if (PassportActivity.this.doneItemAnimation == null || !PassportActivity.this.doneItemAnimation.equals(animator)) {
                        return;
                    }
                    if (z2) {
                        PassportActivity.this.acceptTextView.setVisibility(4);
                    } else {
                        PassportActivity.this.progressViewButton.setVisibility(4);
                    }
                }
            };
        }
        animatorSet.addListener(animatorListenerAdapter);
        this.doneItemAnimation.setDuration(150L);
        this.doneItemAnimation.start();
    }

    public void startPhoneVerification(boolean z, final String str, Runnable runnable, ErrorRunnable errorRunnable, final PassportActivityDelegate passportActivityDelegate) {
        boolean z2;
        int checkSelfPermission;
        boolean shouldShowRequestPermissionRationale;
        TelephonyManager telephonyManager = (TelephonyManager) ApplicationLoader.applicationContext.getSystemService("phone");
        boolean z3 = (telephonyManager.getSimState() == 1 || telephonyManager.getPhoneType() == 0) ? false : true;
        if (getParentActivity() == null || Build.VERSION.SDK_INT < 23 || !z3) {
            z2 = true;
        } else {
            checkSelfPermission = getParentActivity().checkSelfPermission("android.permission.READ_PHONE_STATE");
            z2 = checkSelfPermission == 0;
            if (z) {
                this.permissionsItems.clear();
                if (!z2) {
                    this.permissionsItems.add("android.permission.READ_PHONE_STATE");
                }
                if (!this.permissionsItems.isEmpty()) {
                    shouldShowRequestPermissionRationale = getParentActivity().shouldShowRequestPermissionRationale("android.permission.READ_PHONE_STATE");
                    if (shouldShowRequestPermissionRationale) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(getParentActivity());
                        builder.setTitle(LocaleController.getString(R.string.AppName));
                        builder.setPositiveButton(LocaleController.getString(R.string.OK), null);
                        builder.setMessage(LocaleController.getString(R.string.AllowReadCall));
                        this.permissionsDialog = showDialog(builder.create());
                    } else {
                        getParentActivity().requestPermissions((String[]) this.permissionsItems.toArray(new String[0]), 6);
                    }
                    this.pendingPhone = str;
                    this.pendingErrorRunnable = errorRunnable;
                    this.pendingFinishRunnable = runnable;
                    this.pendingDelegate = passportActivityDelegate;
                    return;
                }
            }
        }
        final TLRPC.TL_account_sendVerifyPhoneCode tL_account_sendVerifyPhoneCode = new TLRPC.TL_account_sendVerifyPhoneCode();
        tL_account_sendVerifyPhoneCode.phone_number = str;
        TLRPC.TL_codeSettings tL_codeSettings = new TLRPC.TL_codeSettings();
        tL_account_sendVerifyPhoneCode.settings = tL_codeSettings;
        tL_codeSettings.allow_flashcall = z3 && z2;
        tL_codeSettings.allow_app_hash = PushListenerController.GooglePushListenerServiceProvider.INSTANCE.hasServices();
        SharedPreferences sharedPreferences = ApplicationLoader.applicationContext.getSharedPreferences("mainconfig", 0);
        boolean z4 = tL_account_sendVerifyPhoneCode.settings.allow_app_hash;
        SharedPreferences.Editor edit = sharedPreferences.edit();
        (z4 ? edit.putString("sms_hash", BuildVars.getSmsHash()) : edit.remove("sms_hash")).commit();
        if (tL_account_sendVerifyPhoneCode.settings.allow_flashcall) {
            try {
                String line1Number = telephonyManager.getLine1Number();
                if (TextUtils.isEmpty(line1Number)) {
                    TLRPC.TL_codeSettings tL_codeSettings2 = tL_account_sendVerifyPhoneCode.settings;
                    tL_codeSettings2.unknown_number = true;
                    tL_codeSettings2.current_number = false;
                } else {
                    tL_account_sendVerifyPhoneCode.settings.current_number = PhoneNumberUtils.compare(str, line1Number);
                    TLRPC.TL_codeSettings tL_codeSettings3 = tL_account_sendVerifyPhoneCode.settings;
                    if (!tL_codeSettings3.current_number) {
                        tL_codeSettings3.allow_flashcall = false;
                    }
                }
            } catch (Exception e) {
                tL_account_sendVerifyPhoneCode.settings.allow_flashcall = false;
                FileLog.e(e);
            }
        }
        ConnectionsManager.getInstance(this.currentAccount).sendRequest(tL_account_sendVerifyPhoneCode, new RequestDelegate() {
            @Override
            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                PassportActivity.this.lambda$startPhoneVerification$66(str, passportActivityDelegate, tL_account_sendVerifyPhoneCode, tLObject, tL_error);
            }
        }, 2);
    }

    private void updateInterfaceStringsForDocumentType() {
        TLRPC.TL_secureRequiredType tL_secureRequiredType = this.currentDocumentsType;
        if (tL_secureRequiredType != null) {
            this.actionBar.setTitle(getTextForType(tL_secureRequiredType.type));
        } else {
            this.actionBar.setTitle(LocaleController.getString(R.string.PassportPersonal));
        }
        updateUploadText(2);
        updateUploadText(3);
        updateUploadText(1);
        updateUploadText(4);
    }

    private void updateManageVisibility() {
        View view;
        if (this.currentForm.values.isEmpty()) {
            this.emptyLayout.setVisibility(0);
            this.sectionCell.setVisibility(8);
            this.headerCell.setVisibility(8);
            this.addDocumentCell.setVisibility(8);
            this.deletePassportCell.setVisibility(8);
            view = this.addDocumentSectionCell;
        } else {
            this.emptyLayout.setVisibility(8);
            this.sectionCell.setVisibility(0);
            this.headerCell.setVisibility(0);
            this.deletePassportCell.setVisibility(0);
            this.addDocumentSectionCell.setVisibility(0);
            if (hasUnfilledValues()) {
                this.addDocumentCell.setVisibility(0);
                return;
            }
            view = this.addDocumentCell;
        }
        view.setVisibility(8);
    }

    public void updatePasswordInterface() {
        EditTextBoldCursor editTextBoldCursor;
        String string;
        ImageView imageView = this.noPasswordImageView;
        if (imageView == null) {
            return;
        }
        TLRPC.account_Password account_password = this.currentPassword;
        if (account_password == null || this.usingSavedPassword != 0) {
            imageView.setVisibility(8);
            this.noPasswordTextView.setVisibility(8);
            this.noPasswordSetTextView.setVisibility(8);
            this.passwordAvatarContainer.setVisibility(8);
            this.inputFieldContainers[0].setVisibility(8);
            this.doneItem.setVisibility(8);
            this.passwordForgotButton.setVisibility(8);
            this.passwordInfoRequestTextView.setVisibility(8);
            this.passwordRequestTextView.setVisibility(8);
            this.emptyView.setVisibility(0);
            return;
        }
        if (!account_password.has_password) {
            this.passwordRequestTextView.setVisibility(0);
            this.noPasswordImageView.setVisibility(0);
            this.noPasswordTextView.setVisibility(0);
            this.noPasswordSetTextView.setVisibility(0);
            this.passwordAvatarContainer.setVisibility(8);
            this.inputFieldContainers[0].setVisibility(8);
            this.doneItem.setVisibility(8);
            this.passwordForgotButton.setVisibility(8);
            this.passwordInfoRequestTextView.setVisibility(8);
            this.passwordRequestTextView.setLayoutParams(LayoutHelper.createLinear(-1, -2, 0.0f, 25.0f, 0.0f, 0.0f));
            this.emptyView.setVisibility(8);
            return;
        }
        this.passwordRequestTextView.setVisibility(0);
        this.noPasswordImageView.setVisibility(8);
        this.noPasswordTextView.setVisibility(8);
        this.noPasswordSetTextView.setVisibility(8);
        this.emptyView.setVisibility(8);
        this.passwordAvatarContainer.setVisibility(0);
        this.inputFieldContainers[0].setVisibility(0);
        this.doneItem.setVisibility(0);
        this.passwordForgotButton.setVisibility(0);
        this.passwordInfoRequestTextView.setVisibility(0);
        this.passwordRequestTextView.setLayoutParams(LayoutHelper.createLinear(-1, -2, 0.0f, 0.0f, 0.0f, 0.0f));
        if (this.inputFields != null) {
            TLRPC.account_Password account_password2 = this.currentPassword;
            if (account_password2 == null || TextUtils.isEmpty(account_password2.hint)) {
                editTextBoldCursor = this.inputFields[0];
                string = LocaleController.getString(R.string.LoginPassword);
            } else {
                editTextBoldCursor = this.inputFields[0];
                string = this.currentPassword.hint;
            }
            editTextBoldCursor.setHint(string);
        }
    }

    public void updateUploadText(int r7) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.PassportActivity.updateUploadText(int):void");
    }

    @Override
    public View createView(Context context) {
        ChatAttachAlert chatAttachAlert;
        this.actionBar.setBackButtonImage(R.drawable.ic_ab_back);
        this.actionBar.setAllowOverlayTitle(true);
        this.actionBar.setActionBarMenuOnItemClick(new AnonymousClass3());
        if (this.currentActivityType == 7) {
            ScrollView scrollView = new ScrollView(context) {
                @Override
                protected void onMeasure(int i, int i2) {
                    PassportActivity.this.scrollHeight = View.MeasureSpec.getSize(i2) - AndroidUtilities.dp(30.0f);
                    super.onMeasure(i, i2);
                }

                @Override
                protected boolean onRequestFocusInDescendants(int i, Rect rect) {
                    return false;
                }

                @Override
                public boolean requestChildRectangleOnScreen(View view, Rect rect, boolean z) {
                    if (PassportActivity.this.currentViewNum == 1 || PassportActivity.this.currentViewNum == 2 || PassportActivity.this.currentViewNum == 4) {
                        rect.bottom += AndroidUtilities.dp(40.0f);
                    }
                    return super.requestChildRectangleOnScreen(view, rect, z);
                }
            };
            this.scrollView = scrollView;
            this.fragmentView = scrollView;
            scrollView.setFillViewport(true);
            AndroidUtilities.setScrollViewEdgeEffectColor(this.scrollView, Theme.getColor(Theme.key_actionBarDefault));
        } else {
            FrameLayout frameLayout = new FrameLayout(context);
            this.fragmentView = frameLayout;
            frameLayout.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundGray));
            ScrollView scrollView2 = new ScrollView(context) {
                @Override
                protected boolean onRequestFocusInDescendants(int i, Rect rect) {
                    return false;
                }

                @Override
                public boolean requestChildRectangleOnScreen(View view, Rect rect, boolean z) {
                    rect.offset(view.getLeft() - view.getScrollX(), view.getTop() - view.getScrollY());
                    rect.top += AndroidUtilities.dp(20.0f);
                    rect.bottom += AndroidUtilities.dp(50.0f);
                    return super.requestChildRectangleOnScreen(view, rect, z);
                }
            };
            this.scrollView = scrollView2;
            scrollView2.setFillViewport(true);
            AndroidUtilities.setScrollViewEdgeEffectColor(this.scrollView, Theme.getColor(Theme.key_actionBarDefault));
            frameLayout.addView(this.scrollView, LayoutHelper.createFrame(-1, -1.0f, 51, 0.0f, 0.0f, 0.0f, this.currentActivityType == 0 ? 48.0f : 0.0f));
            LinearLayout linearLayout = new LinearLayout(context);
            this.linearLayout2 = linearLayout;
            linearLayout.setOrientation(1);
            this.scrollView.addView(this.linearLayout2, new FrameLayout.LayoutParams(-1, -2));
        }
        int i = this.currentActivityType;
        if (i != 0 && i != 8) {
            this.doneItem = this.actionBar.createMenu().addItemWithWidth(2, R.drawable.ic_ab_done, AndroidUtilities.dp(56.0f), LocaleController.getString(R.string.Done));
            ContextProgressView contextProgressView = new ContextProgressView(context, 1);
            this.progressView = contextProgressView;
            contextProgressView.setAlpha(0.0f);
            this.progressView.setScaleX(0.1f);
            this.progressView.setScaleY(0.1f);
            this.progressView.setVisibility(4);
            this.doneItem.addView(this.progressView, LayoutHelper.createFrame(-1, -1.0f));
            int i2 = this.currentActivityType;
            if ((i2 == 1 || i2 == 2) && (chatAttachAlert = this.chatAttachAlert) != null) {
                try {
                    if (chatAttachAlert.isShowing()) {
                        this.chatAttachAlert.dismiss();
                    }
                } catch (Exception unused) {
                }
                this.chatAttachAlert.onDestroy();
                this.chatAttachAlert = null;
            }
        }
        int i3 = this.currentActivityType;
        if (i3 == 5) {
            createPasswordInterface(context);
        } else if (i3 == 0) {
            createRequestInterface(context);
        } else {
            if (i3 == 1) {
                createIdentityInterface(context);
            } else if (i3 == 2) {
                createAddressInterface(context);
            } else if (i3 == 3) {
                createPhoneInterface(context);
            } else if (i3 == 4) {
                createEmailInterface(context);
            } else if (i3 == 6) {
                createEmailVerificationInterface(context);
            } else if (i3 == 7) {
                createPhoneVerificationInterface(context);
            } else if (i3 == 8) {
                createManageInterface(context);
            }
            fillInitialValues();
        }
        return this.fragmentView;
    }

    @Override
    public void didReceivedNotification(int i, int i2, Object... objArr) {
        EditTextBoldCursor editTextBoldCursor;
        HashMap hashMap;
        String str;
        SecureDocumentCell secureDocumentCell;
        ActionBarMenuItem actionBarMenuItem;
        if (i != NotificationCenter.fileUploaded) {
            if (i != NotificationCenter.fileUploadFailed && i == NotificationCenter.twoStepPasswordChanged) {
                if (objArr == null || objArr.length <= 0) {
                    this.currentPassword = null;
                    loadPasswordInfo();
                } else {
                    Object obj = objArr[7];
                    if (obj != null && (editTextBoldCursor = this.inputFields[0]) != null) {
                        editTextBoldCursor.setText((String) obj);
                    }
                    if (objArr[6] == null) {
                        TLRPC.TL_account_password tL_account_password = new TLRPC.TL_account_password();
                        this.currentPassword = tL_account_password;
                        tL_account_password.current_algo = (TLRPC.PasswordKdfAlgo) objArr[1];
                        tL_account_password.new_secure_algo = (TLRPC.SecurePasswordKdfAlgo) objArr[2];
                        tL_account_password.secure_random = (byte[]) objArr[3];
                        tL_account_password.has_recovery = !TextUtils.isEmpty((String) objArr[4]);
                        TLRPC.account_Password account_password = this.currentPassword;
                        account_password.hint = (String) objArr[5];
                        account_password.srp_id = -1L;
                        byte[] bArr = new byte[256];
                        account_password.srp_B = bArr;
                        Utilities.random.nextBytes(bArr);
                        EditTextBoldCursor editTextBoldCursor2 = this.inputFields[0];
                        if (editTextBoldCursor2 != null && editTextBoldCursor2.length() > 0) {
                            this.usingSavedPassword = 2;
                        }
                    }
                }
                updatePasswordInterface();
                return;
            }
            return;
        }
        String str2 = (String) objArr[0];
        SecureDocument secureDocument = (SecureDocument) this.uploadingDocuments.get(str2);
        if (secureDocument != null) {
            secureDocument.inputFile = (TLRPC.TL_inputFile) objArr[1];
            this.uploadingDocuments.remove(str2);
            if (this.uploadingDocuments.isEmpty() && (actionBarMenuItem = this.doneItem) != null) {
                actionBarMenuItem.setEnabled(true);
                this.doneItem.setAlpha(1.0f);
            }
            HashMap hashMap2 = this.documentsCells;
            if (hashMap2 != null && (secureDocumentCell = (SecureDocumentCell) hashMap2.get(secureDocument)) != null) {
                secureDocumentCell.updateButtonState(true);
            }
            HashMap hashMap3 = this.errorsValues;
            if (hashMap3 != null && hashMap3.containsKey("error_document_all")) {
                this.errorsValues.remove("error_document_all");
                checkTopErrorCell(false);
            }
            int i3 = secureDocument.type;
            if (i3 == 0) {
                if (this.bottomCell != null && !TextUtils.isEmpty(this.noAllDocumentsErrorText)) {
                    this.bottomCell.setText(this.noAllDocumentsErrorText);
                }
                hashMap = this.errorsValues;
                str = "files_all";
            } else {
                if (i3 != 4) {
                    return;
                }
                if (this.bottomCellTranslation != null && !TextUtils.isEmpty(this.noAllTranslationErrorText)) {
                    this.bottomCellTranslation.setText(this.noAllTranslationErrorText);
                }
                hashMap = this.errorsValues;
                str = "translation_all";
            }
            hashMap.remove(str);
        }
    }

    public void didSelectFiles(ArrayList arrayList, String str, boolean z, int i, long j, boolean z2) {
        ArrayList arrayList2 = new ArrayList();
        int size = arrayList.size();
        for (int i2 = 0; i2 < size; i2++) {
            SendMessagesHelper.SendingMediaInfo sendingMediaInfo = new SendMessagesHelper.SendingMediaInfo();
            sendingMediaInfo.path = (String) arrayList.get(i2);
            arrayList2.add(sendingMediaInfo);
        }
        processSelectedFiles(arrayList2);
    }

    public void didSelectPhotos(ArrayList arrayList, boolean z, int i) {
        processSelectedFiles(arrayList);
    }

    @Override
    public void dismissCurrentDialog() {
        ChatAttachAlert chatAttachAlert = this.chatAttachAlert;
        if (chatAttachAlert == null || this.visibleDialog != chatAttachAlert) {
            super.dismissCurrentDialog();
            return;
        }
        chatAttachAlert.getPhotoLayout().closeCamera(false);
        this.chatAttachAlert.dismissInternal();
        this.chatAttachAlert.getPhotoLayout().hideCamera(true);
    }

    @Override
    public boolean dismissDialogOnPause(Dialog dialog) {
        return dialog != this.chatAttachAlert && super.dismissDialogOnPause(dialog);
    }

    @Override
    public ArrayList getThemeDescriptions() {
        ArrayList arrayList = new ArrayList();
        arrayList.add(new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, Theme.key_windowBackgroundGray));
        ActionBar actionBar = this.actionBar;
        int i = ThemeDescription.FLAG_BACKGROUND;
        int i2 = Theme.key_actionBarDefault;
        arrayList.add(new ThemeDescription(actionBar, i, null, null, null, null, i2));
        arrayList.add(new ThemeDescription(this.scrollView, ThemeDescription.FLAG_LISTGLOWCOLOR, null, null, null, null, i2));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, null, null, null, null, Theme.key_actionBarDefaultIcon));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, null, null, null, null, Theme.key_actionBarDefaultTitle));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, null, null, null, null, Theme.key_actionBarDefaultSelector));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SEARCH, null, null, null, null, Theme.key_actionBarDefaultSearch));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SEARCHPLACEHOLDER, null, null, null, null, Theme.key_actionBarDefaultSearchPlaceholder));
        arrayList.add(new ThemeDescription(this.linearLayout2, 0, new Class[]{View.class}, Theme.dividerPaint, null, null, Theme.key_divider));
        View view = this.extraBackgroundView;
        int i3 = ThemeDescription.FLAG_BACKGROUND;
        int i4 = Theme.key_windowBackgroundWhite;
        arrayList.add(new ThemeDescription(view, i3, null, null, null, null, i4));
        if (this.extraBackgroundView2 != null) {
            arrayList.add(new ThemeDescription(this.extraBackgroundView2, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, i4));
        }
        for (int i5 = 0; i5 < this.dividers.size(); i5++) {
            arrayList.add(new ThemeDescription((View) this.dividers.get(i5), ThemeDescription.FLAG_BACKGROUND, null, null, null, null, Theme.key_divider));
        }
        Iterator it = this.documentsCells.entrySet().iterator();
        while (it.hasNext()) {
            SecureDocumentCell secureDocumentCell = (SecureDocumentCell) ((Map.Entry) it.next()).getValue();
            arrayList.add(new ThemeDescription(secureDocumentCell, ThemeDescription.FLAG_SELECTORWHITE, new Class[]{SecureDocumentCell.class}, null, null, null, Theme.key_windowBackgroundWhite));
            arrayList.add(new ThemeDescription(secureDocumentCell, 0, new Class[]{SecureDocumentCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteBlackText));
            arrayList.add(new ThemeDescription(secureDocumentCell, 0, new Class[]{SecureDocumentCell.class}, new String[]{"valueTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteGrayText2));
        }
        int i6 = Theme.key_windowBackgroundWhite;
        arrayList.add(new ThemeDescription(this.linearLayout2, ThemeDescription.FLAG_SELECTORWHITE, new Class[]{TextDetailSettingsCell.class}, null, null, null, i6));
        int i7 = Theme.key_windowBackgroundWhiteBlackText;
        arrayList.add(new ThemeDescription(this.linearLayout2, 0, new Class[]{TextDetailSettingsCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, i7));
        int i8 = Theme.key_windowBackgroundWhiteGrayText2;
        arrayList.add(new ThemeDescription(this.linearLayout2, 0, new Class[]{TextDetailSettingsCell.class}, new String[]{"valueTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, i8));
        arrayList.add(new ThemeDescription(this.linearLayout2, ThemeDescription.FLAG_SELECTORWHITE, new Class[]{TextSettingsCell.class}, null, null, null, i6));
        arrayList.add(new ThemeDescription(this.linearLayout2, 0, new Class[]{TextSettingsCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, i7));
        arrayList.add(new ThemeDescription(this.linearLayout2, 0, new Class[]{TextSettingsCell.class}, new String[]{"valueTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteValueText));
        int i9 = Theme.key_windowBackgroundGrayShadow;
        arrayList.add(new ThemeDescription(this.linearLayout2, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{ShadowSectionCell.class}, null, null, null, i9));
        arrayList.add(new ThemeDescription(this.linearLayout2, ThemeDescription.FLAG_SELECTORWHITE, new Class[]{TextDetailSecureCell.class}, null, null, null, i6));
        arrayList.add(new ThemeDescription(this.linearLayout2, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{TextDetailSecureCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, i7));
        arrayList.add(new ThemeDescription(this.linearLayout2, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{TextDetailSecureCell.class}, null, null, null, Theme.key_divider));
        arrayList.add(new ThemeDescription(this.linearLayout2, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{TextDetailSecureCell.class}, new String[]{"valueTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, i8));
        arrayList.add(new ThemeDescription(this.linearLayout2, ThemeDescription.FLAG_IMAGECOLOR, new Class[]{TextDetailSecureCell.class}, new String[]{"checkImageView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_featuredStickers_addedIcon));
        arrayList.add(new ThemeDescription(this.linearLayout2, ThemeDescription.FLAG_CELLBACKGROUNDCOLOR, new Class[]{HeaderCell.class}, null, null, null, i6));
        int i10 = Theme.key_windowBackgroundWhiteBlueHeader;
        arrayList.add(new ThemeDescription(this.linearLayout2, 0, new Class[]{HeaderCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, i10));
        arrayList.add(new ThemeDescription(this.linearLayout2, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{TextInfoPrivacyCell.class}, null, null, null, i9));
        arrayList.add(new ThemeDescription(this.linearLayout2, 0, new Class[]{TextInfoPrivacyCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteGrayText4));
        if (this.inputFields != null) {
            for (int i11 = 0; i11 < this.inputFields.length; i11++) {
                arrayList.add(new ThemeDescription((View) this.inputFields[i11].getParent(), ThemeDescription.FLAG_BACKGROUND, null, null, null, null, Theme.key_windowBackgroundWhite));
                arrayList.add(new ThemeDescription(this.inputFields[i11], ThemeDescription.FLAG_TEXTCOLOR | ThemeDescription.FLAG_CURSORCOLOR, null, null, null, null, Theme.key_windowBackgroundWhiteBlackText));
                arrayList.add(new ThemeDescription(this.inputFields[i11], ThemeDescription.FLAG_HINTTEXTCOLOR, null, null, null, null, Theme.key_windowBackgroundWhiteHintText));
                arrayList.add(new ThemeDescription(this.inputFields[i11], ThemeDescription.FLAG_HINTTEXTCOLOR | ThemeDescription.FLAG_PROGRESSBAR, null, null, null, null, Theme.key_windowBackgroundWhiteBlueHeader));
                arrayList.add(new ThemeDescription(this.inputFields[i11], ThemeDescription.FLAG_BACKGROUNDFILTER, null, null, null, null, Theme.key_windowBackgroundWhiteInputField));
                arrayList.add(new ThemeDescription(this.inputFields[i11], ThemeDescription.FLAG_BACKGROUNDFILTER | ThemeDescription.FLAG_DRAWABLESELECTEDSTATE, null, null, null, null, Theme.key_windowBackgroundWhiteInputFieldActivated));
                arrayList.add(new ThemeDescription(this.inputFields[i11], ThemeDescription.FLAG_BACKGROUNDFILTER | ThemeDescription.FLAG_PROGRESSBAR, null, null, null, null, Theme.key_text_RedRegular));
            }
        } else {
            arrayList.add(new ThemeDescription(null, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, i7));
            arrayList.add(new ThemeDescription(null, ThemeDescription.FLAG_HINTTEXTCOLOR, null, null, null, null, Theme.key_windowBackgroundWhiteHintText));
            arrayList.add(new ThemeDescription(null, ThemeDescription.FLAG_HINTTEXTCOLOR | ThemeDescription.FLAG_PROGRESSBAR, null, null, null, null, i10));
            arrayList.add(new ThemeDescription(null, ThemeDescription.FLAG_BACKGROUNDFILTER, null, null, null, null, Theme.key_windowBackgroundWhiteInputField));
            arrayList.add(new ThemeDescription(null, ThemeDescription.FLAG_BACKGROUNDFILTER | ThemeDescription.FLAG_DRAWABLESELECTEDSTATE, null, null, null, null, Theme.key_windowBackgroundWhiteInputFieldActivated));
            arrayList.add(new ThemeDescription(null, ThemeDescription.FLAG_BACKGROUNDFILTER | ThemeDescription.FLAG_PROGRESSBAR, null, null, null, null, Theme.key_text_RedRegular));
        }
        if (this.inputExtraFields != null) {
            for (int i12 = 0; i12 < this.inputExtraFields.length; i12++) {
                arrayList.add(new ThemeDescription((View) this.inputExtraFields[i12].getParent(), ThemeDescription.FLAG_BACKGROUND, null, null, null, null, Theme.key_windowBackgroundWhite));
                arrayList.add(new ThemeDescription(this.inputExtraFields[i12], ThemeDescription.FLAG_TEXTCOLOR | ThemeDescription.FLAG_CURSORCOLOR, null, null, null, null, Theme.key_windowBackgroundWhiteBlackText));
                arrayList.add(new ThemeDescription(this.inputExtraFields[i12], ThemeDescription.FLAG_HINTTEXTCOLOR, null, null, null, null, Theme.key_windowBackgroundWhiteHintText));
                arrayList.add(new ThemeDescription(this.inputExtraFields[i12], ThemeDescription.FLAG_HINTTEXTCOLOR | ThemeDescription.FLAG_PROGRESSBAR, null, null, null, null, Theme.key_windowBackgroundWhiteBlueHeader));
                arrayList.add(new ThemeDescription(this.inputExtraFields[i12], ThemeDescription.FLAG_BACKGROUNDFILTER, null, null, null, null, Theme.key_windowBackgroundWhiteInputField));
                arrayList.add(new ThemeDescription(this.inputExtraFields[i12], ThemeDescription.FLAG_BACKGROUNDFILTER | ThemeDescription.FLAG_DRAWABLESELECTEDSTATE, null, null, null, null, Theme.key_windowBackgroundWhiteInputFieldActivated));
                arrayList.add(new ThemeDescription(this.inputExtraFields[i12], ThemeDescription.FLAG_PROGRESSBAR | ThemeDescription.FLAG_BACKGROUNDFILTER, null, null, null, null, Theme.key_text_RedRegular));
            }
        }
        arrayList.add(new ThemeDescription(this.emptyView, ThemeDescription.FLAG_PROGRESSBAR, null, null, null, null, Theme.key_progressCircle));
        arrayList.add(new ThemeDescription(this.noPasswordImageView, ThemeDescription.FLAG_IMAGECOLOR, null, null, null, null, Theme.key_chat_messagePanelIcons));
        arrayList.add(new ThemeDescription(this.noPasswordTextView, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, Theme.key_windowBackgroundWhiteGrayText4));
        arrayList.add(new ThemeDescription(this.noPasswordSetTextView, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, Theme.key_windowBackgroundWhiteBlueText5));
        TextView textView = this.passwordForgotButton;
        int i13 = ThemeDescription.FLAG_TEXTCOLOR;
        int i14 = Theme.key_windowBackgroundWhiteBlueText4;
        arrayList.add(new ThemeDescription(textView, i13, null, null, null, null, i14));
        arrayList.add(new ThemeDescription(this.plusTextView, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, Theme.key_windowBackgroundWhiteBlackText));
        arrayList.add(new ThemeDescription(this.acceptTextView, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, Theme.key_passport_authorizeText));
        arrayList.add(new ThemeDescription(this.bottomLayout, ThemeDescription.FLAG_BACKGROUNDFILTER, null, null, null, null, Theme.key_passport_authorizeBackground));
        arrayList.add(new ThemeDescription(this.bottomLayout, ThemeDescription.FLAG_DRAWABLESELECTEDSTATE | ThemeDescription.FLAG_BACKGROUNDFILTER, null, null, null, null, Theme.key_passport_authorizeBackgroundSelected));
        ContextProgressView contextProgressView = this.progressView;
        int i15 = Theme.key_contextProgressInner2;
        arrayList.add(new ThemeDescription(contextProgressView, 0, null, null, null, null, i15));
        ContextProgressView contextProgressView2 = this.progressView;
        int i16 = Theme.key_contextProgressOuter2;
        arrayList.add(new ThemeDescription(contextProgressView2, 0, null, null, null, null, i16));
        arrayList.add(new ThemeDescription(this.progressViewButton, 0, null, null, null, null, i15));
        arrayList.add(new ThemeDescription(this.progressViewButton, 0, null, null, null, null, i16));
        arrayList.add(new ThemeDescription(this.emptyImageView, ThemeDescription.FLAG_IMAGECOLOR, null, null, null, null, Theme.key_sessions_devicesImage));
        TextView textView2 = this.emptyTextView1;
        int i17 = ThemeDescription.FLAG_TEXTCOLOR;
        int i18 = Theme.key_windowBackgroundWhiteGrayText2;
        arrayList.add(new ThemeDescription(textView2, i17, null, null, null, null, i18));
        arrayList.add(new ThemeDescription(this.emptyTextView2, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, i18));
        arrayList.add(new ThemeDescription(this.emptyTextView3, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, i14));
        return arrayList;
    }

    public void needHideProgress() {
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

    public void needShowProgress() {
        if (getParentActivity() == null || getParentActivity().isFinishing() || this.progressDialog != null) {
            return;
        }
        AlertDialog alertDialog = new AlertDialog(getParentActivity(), 3);
        this.progressDialog = alertDialog;
        alertDialog.setCanCancel(false);
        this.progressDialog.show();
    }

    @Override
    public void onActivityResultFragment(int i, int i2, Intent intent) {
        if (i2 == -1) {
            if (i == 0 || i == 2) {
                createChatAttachView();
                ChatAttachAlert chatAttachAlert = this.chatAttachAlert;
                if (chatAttachAlert != null) {
                    chatAttachAlert.onActivityResultFragment(i, intent, this.currentPicturePath);
                }
                this.currentPicturePath = null;
                return;
            }
            if (i == 1) {
                if (intent == null || intent.getData() == null) {
                    showAttachmentError();
                    return;
                }
                ArrayList arrayList = new ArrayList();
                SendMessagesHelper.SendingMediaInfo sendingMediaInfo = new SendMessagesHelper.SendingMediaInfo();
                sendingMediaInfo.uri = intent.getData();
                arrayList.add(sendingMediaInfo);
                processSelectedFiles(arrayList);
            }
        }
    }

    @Override
    public boolean onBackPressed() {
        int i = this.currentActivityType;
        int i2 = 0;
        if (i == 7) {
            this.views[this.currentViewNum].onBackPressed(true);
            while (true) {
                SlideView[] slideViewArr = this.views;
                if (i2 >= slideViewArr.length) {
                    break;
                }
                SlideView slideView = slideViewArr[i2];
                if (slideView != null) {
                    slideView.onDestroyActivity();
                }
                i2++;
            }
        } else if (i == 0 || i == 5) {
            callCallback(false);
        } else if (i == 1 || i == 2) {
            return !checkDiscard();
        }
        return true;
    }

    @Override
    public void onDialogDismiss(Dialog dialog) {
        if (this.currentActivityType != 3 || Build.VERSION.SDK_INT < 23 || dialog != this.permissionsDialog || this.permissionsItems.isEmpty()) {
            return;
        }
        getParentActivity().requestPermissions((String[]) this.permissionsItems.toArray(new String[0]), 6);
    }

    @Override
    public boolean onFragmentCreate() {
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.fileUploaded);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.fileUploadFailed);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.twoStepPasswordChanged);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.didRemoveTwoStepPassword);
        return super.onFragmentCreate();
    }

    @Override
    public void onFragmentDestroy() {
        super.onFragmentDestroy();
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.fileUploaded);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.fileUploadFailed);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.twoStepPasswordChanged);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.didRemoveTwoStepPassword);
        int i = 0;
        callCallback(false);
        ChatAttachAlert chatAttachAlert = this.chatAttachAlert;
        if (chatAttachAlert != null) {
            chatAttachAlert.dismissInternal();
            this.chatAttachAlert.onDestroy();
        }
        if (this.currentActivityType == 7) {
            while (true) {
                SlideView[] slideViewArr = this.views;
                if (i >= slideViewArr.length) {
                    break;
                }
                SlideView slideView = slideViewArr[i];
                if (slideView != null) {
                    slideView.onDestroyActivity();
                }
                i++;
            }
            AlertDialog alertDialog = this.progressDialog;
            if (alertDialog != null) {
                try {
                    alertDialog.dismiss();
                } catch (Exception e) {
                    FileLog.e(e);
                }
                this.progressDialog = null;
            }
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        ChatAttachAlert chatAttachAlert = this.chatAttachAlert;
        if (chatAttachAlert != null) {
            chatAttachAlert.onPause();
        }
    }

    @Override
    public void onRequestPermissionsResultFragment(int i, String[] strArr, int[] iArr) {
        ChatAttachAlert chatAttachAlert;
        TextSettingsCell textSettingsCell;
        int i2 = this.currentActivityType;
        if ((i2 != 1 && i2 != 2) || (chatAttachAlert = this.chatAttachAlert) == null) {
            if (i2 == 3 && i == 6) {
                startPhoneVerification(false, this.pendingPhone, this.pendingFinishRunnable, this.pendingErrorRunnable, this.pendingDelegate);
                return;
            }
            return;
        }
        if (i == 17) {
            chatAttachAlert.getPhotoLayout().checkCamera(false);
            return;
        }
        if (i == 21) {
            if (getParentActivity() == null || iArr == null || iArr.length == 0 || iArr[0] == 0) {
                return;
            }
            AlertDialog.Builder builder = new AlertDialog.Builder(getParentActivity());
            builder.setTitle(LocaleController.getString(R.string.AppName));
            builder.setMessage(LocaleController.getString(R.string.PermissionNoAudioVideoWithHint));
            builder.setNegativeButton(LocaleController.getString(R.string.PermissionOpenSettings), new DialogInterface.OnClickListener() {
                @Override
                public final void onClick(DialogInterface dialogInterface, int i3) {
                    PassportActivity.this.lambda$onRequestPermissionsResultFragment$68(dialogInterface, i3);
                }
            });
            builder.setPositiveButton(LocaleController.getString(R.string.OK), null);
            builder.show();
            return;
        }
        if (i == 19 && iArr != null && iArr.length > 0 && iArr[0] == 0) {
            processSelectedAttach(0);
            return;
        }
        if (i != 22 || iArr == null || iArr.length <= 0 || iArr[0] != 0 || (textSettingsCell = this.scanDocumentCell) == null) {
            return;
        }
        textSettingsCell.callOnClick();
    }

    @Override
    public void onResume() {
        ViewGroup[] viewGroupArr;
        ViewGroup viewGroup;
        super.onResume();
        ChatAttachAlert chatAttachAlert = this.chatAttachAlert;
        if (chatAttachAlert != null) {
            chatAttachAlert.onResume();
        }
        if (this.currentActivityType == 5 && (viewGroupArr = this.inputFieldContainers) != null && (viewGroup = viewGroupArr[0]) != null && viewGroup.getVisibility() == 0) {
            this.inputFields[0].requestFocus();
            AndroidUtilities.showKeyboard(this.inputFields[0]);
            AndroidUtilities.runOnUIThread(new Runnable() {
                @Override
                public final void run() {
                    PassportActivity.this.lambda$onResume$2();
                }
            }, 200L);
        }
        AndroidUtilities.requestAdjustResize(getParentActivity(), this.classGuid);
    }

    @Override
    public void onTransitionAnimationEnd(boolean z, boolean z2) {
        EditTextBoldCursor editTextBoldCursor;
        if (this.presentAfterAnimation != null) {
            AndroidUtilities.runOnUIThread(new Runnable() {
                @Override
                public final void run() {
                    PassportActivity.this.lambda$onTransitionAnimationEnd$67();
                }
            });
        }
        int i = this.currentActivityType;
        if (i == 5) {
            if (z) {
                if (this.inputFieldContainers[0].getVisibility() == 0) {
                    this.inputFields[0].requestFocus();
                    AndroidUtilities.showKeyboard(this.inputFields[0]);
                }
                if (this.usingSavedPassword == 2) {
                    onPasswordDone(false);
                    return;
                }
                return;
            }
            return;
        }
        if (i == 7) {
            if (z) {
                this.views[this.currentViewNum].onShow();
                return;
            }
            return;
        }
        if (i == 4) {
            if (!z) {
                return;
            }
            this.inputFields[0].requestFocus();
            editTextBoldCursor = this.inputFields[0];
        } else {
            if (i != 6) {
                if ((i == 2 || i == 1) && Build.VERSION.SDK_INT >= 21) {
                    createChatAttachView();
                    return;
                }
                return;
            }
            if (!z) {
                return;
            }
            this.inputFields[0].requestFocus();
            editTextBoldCursor = this.inputFields[0];
        }
        AndroidUtilities.showKeyboard(editTextBoldCursor);
    }

    @Override
    public void saveSelfArgs(Bundle bundle) {
        String str = this.currentPicturePath;
        if (str != null) {
            bundle.putString("path", str);
        }
    }

    public void setNeedActivityResult(boolean z) {
        this.needActivityResult = z;
    }

    public void setPage(int i, boolean z, Bundle bundle) {
        if (i == 3) {
            this.doneItem.setVisibility(8);
        }
        SlideView[] slideViewArr = this.views;
        final SlideView slideView = slideViewArr[this.currentViewNum];
        final SlideView slideView2 = slideViewArr[i];
        this.currentViewNum = i;
        slideView2.setParams(bundle, false);
        slideView2.onShow();
        if (!z) {
            slideView2.setTranslationX(0.0f);
            slideView2.setVisibility(0);
            if (slideView != slideView2) {
                slideView.setVisibility(8);
                return;
            }
            return;
        }
        slideView2.setTranslationX(AndroidUtilities.displaySize.x);
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.setInterpolator(new AccelerateDecelerateInterpolator());
        animatorSet.setDuration(300L);
        animatorSet.playTogether(ObjectAnimator.ofFloat(slideView, "translationX", -AndroidUtilities.displaySize.x), ObjectAnimator.ofFloat(slideView2, "translationX", 0.0f));
        animatorSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animator) {
                slideView.setVisibility(8);
                slideView.setX(0.0f);
            }

            @Override
            public void onAnimationStart(Animator animator) {
                slideView2.setVisibility(0);
            }
        });
        animatorSet.start();
    }

    public void startDocumentSelectActivity() {
        try {
            Intent intent = new Intent("android.intent.action.GET_CONTENT");
            intent.putExtra("android.intent.extra.ALLOW_MULTIPLE", true);
            intent.setType("*/*");
            startActivityForResult(intent, 21);
        } catch (Exception e) {
            FileLog.e(e);
        }
    }
}
