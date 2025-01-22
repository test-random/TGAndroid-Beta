package org.telegram.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.text.SpannableStringBuilder;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.style.ClickableSpan;
import android.util.Property;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.RenderProcessGoneDetail;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.dynamicanimation.animation.DynamicAnimation;
import androidx.dynamicanimation.animation.FloatValueHolder;
import androidx.dynamicanimation.animation.SpringAnimation;
import androidx.dynamicanimation.animation.SpringForce;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.wallet.AutoResolveHelper;
import com.google.android.gms.wallet.IsReadyToPayRequest;
import com.google.android.gms.wallet.PaymentData;
import com.google.android.gms.wallet.PaymentDataRequest;
import com.google.android.gms.wallet.PaymentsClient;
import com.google.android.gms.wallet.Wallet;
import com.stripe.android.Stripe;
import com.stripe.android.TokenCallback;
import com.stripe.android.exception.APIConnectionException;
import com.stripe.android.exception.APIException;
import com.stripe.android.model.Card;
import com.stripe.android.model.Token;
import com.stripe.android.net.TokenParser;
import j$.util.Optional;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Scanner;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.telegram.PhoneFormat.PhoneFormat;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.DialogObject;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.R;
import org.telegram.messenger.SRPHelper;
import org.telegram.messenger.SharedConfig;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.UserObject;
import org.telegram.messenger.Utilities;
import org.telegram.messenger.browser.Browser;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.tgnet.tl.TL_account;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.ActionBarMenuItem;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.INavigationLayout;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ActionBar.ThemeDescription;
import org.telegram.ui.Cells.EditTextSettingsCell;
import org.telegram.ui.Cells.HeaderCell;
import org.telegram.ui.Cells.PaymentInfoCell;
import org.telegram.ui.Cells.RadioCell;
import org.telegram.ui.Cells.RecurrentPaymentsAcceptCell;
import org.telegram.ui.Cells.ShadowSectionCell;
import org.telegram.ui.Cells.TextCheckCell;
import org.telegram.ui.Cells.TextDetailSettingsCell;
import org.telegram.ui.Cells.TextInfoPrivacyCell;
import org.telegram.ui.Cells.TextPriceCell;
import org.telegram.ui.Cells.TextSettingsCell;
import org.telegram.ui.Components.AlertsCreator;
import org.telegram.ui.Components.Bulletin;
import org.telegram.ui.Components.BulletinFactory;
import org.telegram.ui.Components.ContextProgressView;
import org.telegram.ui.Components.EditTextBoldCursor;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.CountrySelectActivity;
import org.telegram.ui.PaymentFormActivity;
import org.telegram.ui.Stars.StarsIntroActivity;

public class PaymentFormActivity extends BaseFragment implements NotificationCenter.NotificationCenterDelegate {
    private TLRPC.User botUser;
    private TextInfoPrivacyCell[] bottomCell;
    private BottomFrameLayout bottomLayout;
    private boolean canceled;
    private String cardName;
    private TextCheckCell checkCell1;
    private EditTextSettingsCell codeFieldCell;
    private HashMap codesMap;
    private ArrayList countriesArray;
    private HashMap countriesMap;
    private CountrySelectActivity.Country country;
    private String countryName;
    private String currentBotName;
    private String currentItemName;
    private TL_account.Password currentPassword;
    private int currentStep;
    private PaymentFormActivityDelegate delegate;
    private TextDetailSettingsCell[] detailSettingsCell;
    private ArrayList dividers;
    private ActionBarMenuItem doneItem;
    private AnimatorSet doneItemAnimation;
    private boolean donePressed;
    private int emailCodeLength;
    private FrameLayout googlePayButton;
    private FrameLayout googlePayContainer;
    private String googlePayCountryCode;
    private TLRPC.TL_inputPaymentCredentialsGooglePay googlePayCredentials;
    private JSONObject googlePayParameters;
    private String googlePayPublicKey;
    private HeaderCell[] headerCell;
    private boolean ignoreOnCardChange;
    private boolean ignoreOnPhoneChange;
    private boolean ignoreOnTextChange;
    private boolean initGooglePay;
    private EditTextBoldCursor[] inputFields;
    private TLRPC.InputInvoice invoiceInput;
    private String invoiceSlug;
    private InvoiceStatus invoiceStatus;
    private boolean isCheckoutPreview;
    private boolean isWebView;
    private LinearLayout linearLayout2;
    private boolean loadingPasswordInfo;
    private MessageObject messageObject;
    private boolean needPayAfterTransition;
    private boolean need_card_country;
    private boolean need_card_name;
    private boolean need_card_postcode;
    private BaseFragment parentFragment;
    private PaymentFormActivity passwordFragment;
    private boolean passwordOk;
    private TextView payTextView;
    private TLRPC.PaymentForm paymentForm;
    private PaymentFormCallback paymentFormCallback;
    private TLRPC.TL_paymentFormMethod paymentFormMethod;
    private PaymentInfoCell paymentInfoCell;
    private String paymentJson;
    private TLRPC.PaymentReceipt paymentReceipt;
    private boolean paymentStatusSent;
    private PaymentsClient paymentsClient;
    private HashMap phoneFormatMap;
    private ArrayList prices;
    private ContextProgressView progressView;
    private ContextProgressView progressViewButton;
    private String providerApiKey;
    private RadioCell[] radioCells;
    private RecurrentPaymentsAcceptCell recurrentAcceptCell;
    private boolean recurrentAccepted;
    private TLRPC.TL_payments_validatedRequestedInfo requestedInfo;
    private Theme.ResourcesProvider resourcesProvider;
    private boolean saveCardInfo;
    private boolean saveShippingInfo;
    private TLRPC.TL_paymentSavedCredentialsCard savedCredentialsCard;
    private ScrollView scrollView;
    private ShadowSectionCell[] sectionCell;
    private TextSettingsCell[] settingsCell;
    private float shiftDp;
    private TLRPC.TL_shippingOption shippingOption;
    private Runnable shortPollRunnable;
    private boolean shouldNavigateBack;
    private boolean swipeBackEnabled;
    private TextView textView;
    private Long tipAmount;
    private LinearLayout tipLayout;
    private TextPriceCell totalCell;
    private String[] totalPrice;
    private String totalPriceDecimal;
    private TLRPC.TL_payments_validateRequestedInfo validateRequest;
    private boolean waitingForEmail;
    private WebView webView;
    private String webViewUrl;
    private boolean webviewLoading;
    private static final List WEBVIEW_PROTOCOLS = Arrays.asList("http", "https");
    private static final List BLACKLISTED_PROTOCOLS = Collections.singletonList("tg");

    public class AnonymousClass18 extends WebViewClient {
        final Context val$context;

        AnonymousClass18(Context context) {
            this.val$context = context;
        }

        public void lambda$onRenderProcessGone$0() {
            Browser.openUrl(PaymentFormActivity.this.getContext(), "https://play.google.com/store/apps/details?id=com.google.android.webview");
        }

        @Override
        public void onPageFinished(WebView webView, String str) {
            super.onPageFinished(webView, str);
            PaymentFormActivity.this.webviewLoading = false;
            PaymentFormActivity.this.showEditDoneProgress(true, false);
            PaymentFormActivity.this.updateSavePaymentField();
        }

        @Override
        public boolean onRenderProcessGone(WebView webView, RenderProcessGoneDetail renderProcessGoneDetail) {
            try {
                if (!AndroidUtilities.isSafeToShow(PaymentFormActivity.this.getContext())) {
                    return true;
                }
                new AlertDialog.Builder(PaymentFormActivity.this.getContext(), PaymentFormActivity.this.resourcesProvider).setTitle(LocaleController.getString(R.string.ChromeCrashTitle)).setMessage(AndroidUtilities.replaceSingleTag(LocaleController.getString(R.string.ChromeCrashMessage), new Runnable() {
                    @Override
                    public final void run() {
                        PaymentFormActivity.AnonymousClass18.this.lambda$onRenderProcessGone$0();
                    }
                })).setPositiveButton(LocaleController.getString(R.string.OK), null).show();
                return true;
            } catch (Exception e) {
                FileLog.e(e);
                return false;
            }
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView webView, String str) {
            try {
                Uri parse = Uri.parse(str);
                if ("t.me".equals(parse.getHost())) {
                    PaymentFormActivity.this.goToNextStep();
                    return true;
                }
                if (PaymentFormActivity.BLACKLISTED_PROTOCOLS.contains(parse.getScheme())) {
                    return true;
                }
                if (PaymentFormActivity.WEBVIEW_PROTOCOLS.contains(parse.getScheme())) {
                    return false;
                }
                try {
                    if (PaymentFormActivity.this.getContext() instanceof Activity) {
                        ((Activity) PaymentFormActivity.this.getContext()).startActivityForResult(new Intent("android.intent.action.VIEW", parse), 210);
                    }
                } catch (ActivityNotFoundException unused) {
                    new AlertDialog.Builder(this.val$context).setTitle(PaymentFormActivity.this.currentBotName).setMessage(LocaleController.getString(R.string.PaymentAppNotFoundForDeeplink)).setPositiveButton(LocaleController.getString(R.string.OK), null).show();
                }
                return true;
            } catch (Exception unused2) {
                return false;
            }
        }
    }

    public class AnonymousClass25 implements TokenCallback {
        AnonymousClass25() {
        }

        public void lambda$onSuccess$0() {
            PaymentFormActivity.this.goToNextStep();
            PaymentFormActivity.this.showEditDoneProgress(true, false);
            PaymentFormActivity.this.setDonePressed(false);
        }

        @Override
        public void onError(Exception exc) {
            if (PaymentFormActivity.this.canceled) {
                return;
            }
            PaymentFormActivity.this.showEditDoneProgress(true, false);
            PaymentFormActivity.this.setDonePressed(false);
            if ((exc instanceof APIConnectionException) || (exc instanceof APIException)) {
                AlertsCreator.showSimpleToast(PaymentFormActivity.this, LocaleController.getString(R.string.PaymentConnectionFailed));
            } else {
                AlertsCreator.showSimpleToast(PaymentFormActivity.this, exc.getMessage());
            }
        }

        @Override
        public void onSuccess(Token token) {
            if (PaymentFormActivity.this.canceled) {
                return;
            }
            PaymentFormActivity.this.paymentJson = String.format(Locale.US, "{\"type\":\"%1$s\", \"id\":\"%2$s\"}", token.getType(), token.getId());
            AndroidUtilities.runOnUIThread(new Runnable() {
                @Override
                public final void run() {
                    PaymentFormActivity.AnonymousClass25.this.lambda$onSuccess$0();
                }
            });
        }
    }

    public class AnonymousClass6 extends WebViewClient {
        final Context val$context;

        AnonymousClass6(Context context) {
            this.val$context = context;
        }

        public void lambda$onRenderProcessGone$0() {
            Browser.openUrl(PaymentFormActivity.this.getContext(), "https://play.google.com/store/apps/details?id=com.google.android.webview");
        }

        @Override
        public void onLoadResource(WebView webView, String str) {
            super.onLoadResource(webView, str);
        }

        @Override
        public void onPageFinished(WebView webView, String str) {
            super.onPageFinished(webView, str);
            PaymentFormActivity.this.webviewLoading = false;
            PaymentFormActivity.this.showEditDoneProgress(true, false);
            PaymentFormActivity.this.updateSavePaymentField();
        }

        @Override
        public boolean onRenderProcessGone(WebView webView, RenderProcessGoneDetail renderProcessGoneDetail) {
            try {
                if (!AndroidUtilities.isSafeToShow(PaymentFormActivity.this.getContext())) {
                    return true;
                }
                new AlertDialog.Builder(PaymentFormActivity.this.getContext(), PaymentFormActivity.this.resourcesProvider).setTitle(LocaleController.getString(R.string.ChromeCrashTitle)).setMessage(AndroidUtilities.replaceSingleTag(LocaleController.getString(R.string.ChromeCrashMessage), new Runnable() {
                    @Override
                    public final void run() {
                        PaymentFormActivity.AnonymousClass6.this.lambda$onRenderProcessGone$0();
                    }
                })).setPositiveButton(LocaleController.getString(R.string.OK), null).show();
                return true;
            } catch (Exception e) {
                FileLog.e(e);
                return false;
            }
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView webView, String str) {
            Uri parse;
            PaymentFormActivity.this.shouldNavigateBack = !str.equals(r0.webViewUrl);
            try {
                parse = Uri.parse(str);
            } catch (Exception unused) {
            }
            if ("t.me".equals(parse.getHost())) {
                PaymentFormActivity.this.goToNextStep();
                return true;
            }
            if (PaymentFormActivity.BLACKLISTED_PROTOCOLS.contains(parse.getScheme())) {
                return true;
            }
            if (!PaymentFormActivity.WEBVIEW_PROTOCOLS.contains(parse.getScheme())) {
                try {
                    if (PaymentFormActivity.this.getContext() instanceof Activity) {
                        ((Activity) PaymentFormActivity.this.getContext()).startActivityForResult(new Intent("android.intent.action.VIEW", parse), 210);
                    }
                } catch (ActivityNotFoundException unused2) {
                    new AlertDialog.Builder(this.val$context).setTitle(PaymentFormActivity.this.currentBotName).setMessage(LocaleController.getString(R.string.PaymentAppNotFoundForDeeplink)).setPositiveButton(LocaleController.getString(R.string.OK), null).show();
                }
                return true;
            }
            return super.shouldOverrideUrlLoading(webView, str);
        }
    }

    public class BottomFrameLayout extends FrameLayout {
        Paint paint;
        float progress;
        SpringAnimation springAnimation;

        public BottomFrameLayout(Context context, TLRPC.PaymentForm paymentForm) {
            super(context);
            this.paint = new Paint(1);
            setWillNotDraw(false);
        }

        public void lambda$setChecked$0(DynamicAnimation dynamicAnimation, float f, float f2) {
            this.progress = f / 100.0f;
            if (PaymentFormActivity.this.payTextView != null) {
                PaymentFormActivity.this.payTextView.setAlpha((this.progress * 0.2f) + 0.8f);
            }
            invalidate();
        }

        public void lambda$setChecked$1(DynamicAnimation dynamicAnimation, boolean z, float f, float f2) {
            if (dynamicAnimation == this.springAnimation) {
                this.springAnimation = null;
            }
        }

        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);
            canvas.drawColor(PaymentFormActivity.this.getThemedColor(Theme.key_switchTrackBlue));
            this.paint.setColor(PaymentFormActivity.this.getThemedColor(Theme.key_contacts_inviteBackground));
            canvas.drawCircle(LocaleController.isRTL ? getWidth() - AndroidUtilities.dp(28.0f) : AndroidUtilities.dp(28.0f), -AndroidUtilities.dp(28.0f), Math.max(getWidth(), getHeight()) * this.progress, this.paint);
        }

        public void setChecked(boolean z, boolean z2) {
            SpringAnimation springAnimation = this.springAnimation;
            if (springAnimation != null) {
                springAnimation.cancel();
            }
            float f = z ? 1.0f : 0.0f;
            if (!z2) {
                this.progress = f;
                if (PaymentFormActivity.this.payTextView != null) {
                    PaymentFormActivity.this.payTextView.setAlpha((this.progress * 0.2f) + 0.8f);
                }
                invalidate();
                return;
            }
            if (this.progress == f) {
                return;
            }
            SpringAnimation spring = new SpringAnimation(new FloatValueHolder(this.progress * 100.0f)).setSpring(new SpringForce(f * 100.0f).setStiffness(z ? 500.0f : 650.0f).setDampingRatio(1.0f));
            this.springAnimation = spring;
            spring.addUpdateListener(new DynamicAnimation.OnAnimationUpdateListener() {
                @Override
                public final void onAnimationUpdate(DynamicAnimation dynamicAnimation, float f2, float f3) {
                    PaymentFormActivity.BottomFrameLayout.this.lambda$setChecked$0(dynamicAnimation, f2, f3);
                }
            });
            this.springAnimation.addEndListener(new DynamicAnimation.OnAnimationEndListener() {
                @Override
                public final void onAnimationEnd(DynamicAnimation dynamicAnimation, boolean z3, float f2, float f3) {
                    PaymentFormActivity.BottomFrameLayout.this.lambda$setChecked$1(dynamicAnimation, z3, f2, f3);
                }
            });
            this.springAnimation.start();
        }
    }

    public enum InvoiceStatus {
        PAID,
        CANCELLED,
        PENDING,
        FAILED
    }

    public class LinkSpan extends ClickableSpan {
        public LinkSpan() {
        }

        @Override
        public void onClick(View view) {
            PaymentFormActivity paymentFormActivity = PaymentFormActivity.this;
            paymentFormActivity.presentFragment(new TwoStepVerificationSetupActivity(6, paymentFormActivity.currentPassword));
        }

        @Override
        public void updateDrawState(TextPaint textPaint) {
            super.updateDrawState(textPaint);
            textPaint.setUnderlineText(false);
        }
    }

    public interface PaymentFormActivityDelegate {

        public abstract class CC {
            public static void $default$currentPasswordUpdated(PaymentFormActivityDelegate paymentFormActivityDelegate, TL_account.Password password) {
            }

            public static void $default$didSelectNewAddress(PaymentFormActivityDelegate paymentFormActivityDelegate, TLRPC.TL_payments_validateRequestedInfo tL_payments_validateRequestedInfo) {
            }

            public static boolean $default$didSelectNewCard(PaymentFormActivityDelegate paymentFormActivityDelegate, String str, String str2, boolean z, TLRPC.TL_inputPaymentCredentialsGooglePay tL_inputPaymentCredentialsGooglePay, TLRPC.TL_paymentSavedCredentialsCard tL_paymentSavedCredentialsCard) {
                return false;
            }

            public static void $default$onFragmentDestroyed(PaymentFormActivityDelegate paymentFormActivityDelegate) {
            }
        }

        void currentPasswordUpdated(TL_account.Password password);

        void didSelectNewAddress(TLRPC.TL_payments_validateRequestedInfo tL_payments_validateRequestedInfo);

        boolean didSelectNewCard(String str, String str2, boolean z, TLRPC.TL_inputPaymentCredentialsGooglePay tL_inputPaymentCredentialsGooglePay, TLRPC.TL_paymentSavedCredentialsCard tL_paymentSavedCredentialsCard);

        void onFragmentDestroyed();
    }

    public interface PaymentFormCallback {
        void onInvoiceStatusChanged(InvoiceStatus invoiceStatus);
    }

    public class TelegramWebviewProxy {
        private TelegramWebviewProxy() {
        }

        public void lambda$postEvent$0(String str, String str2) {
            if (PaymentFormActivity.this.getParentActivity() != null && str.equals("payment_form_submit")) {
                try {
                    JSONObject jSONObject = new JSONObject(str2);
                    JSONObject jSONObject2 = jSONObject.getJSONObject("credentials");
                    PaymentFormActivity.this.paymentJson = jSONObject2.toString();
                    PaymentFormActivity.this.cardName = jSONObject.getString("title");
                } catch (Throwable th) {
                    PaymentFormActivity.this.paymentJson = str2;
                    FileLog.e(th);
                }
                PaymentFormActivity.this.goToNextStep();
            }
        }

        @JavascriptInterface
        public void postEvent(final String str, final String str2) {
            AndroidUtilities.runOnUIThread(new Runnable() {
                @Override
                public final void run() {
                    PaymentFormActivity.TelegramWebviewProxy.this.lambda$postEvent$0(str, str2);
                }
            });
        }
    }

    private PaymentFormActivity(TLRPC.InputInvoice inputInvoice, TLRPC.PaymentForm paymentForm, MessageObject messageObject, String str, int i, TLRPC.TL_payments_validatedRequestedInfo tL_payments_validatedRequestedInfo, TLRPC.TL_shippingOption tL_shippingOption, Long l, String str2, String str3, TLRPC.TL_payments_validateRequestedInfo tL_payments_validateRequestedInfo, boolean z, TLRPC.TL_inputPaymentCredentialsGooglePay tL_inputPaymentCredentialsGooglePay, BaseFragment baseFragment) {
        this.countriesArray = new ArrayList();
        this.countriesMap = new HashMap();
        this.codesMap = new HashMap();
        this.phoneFormatMap = new HashMap();
        this.swipeBackEnabled = true;
        this.headerCell = new HeaderCell[3];
        this.dividers = new ArrayList();
        this.sectionCell = new ShadowSectionCell[3];
        this.bottomCell = new TextInfoPrivacyCell[3];
        this.settingsCell = new TextSettingsCell[2];
        this.detailSettingsCell = new TextDetailSettingsCell[7];
        this.shiftDp = -4.5f;
        this.emailCodeLength = 6;
        init(inputInvoice, paymentForm, messageObject, str, i, tL_payments_validatedRequestedInfo, tL_shippingOption, l, str2, str3, tL_payments_validateRequestedInfo, z, tL_inputPaymentCredentialsGooglePay, baseFragment);
    }

    public PaymentFormActivity(TLRPC.PaymentForm paymentForm, String str, BaseFragment baseFragment) {
        this(paymentForm, null, str, baseFragment);
    }

    public PaymentFormActivity(TLRPC.PaymentForm paymentForm, MessageObject messageObject, String str, BaseFragment baseFragment) {
        this.countriesArray = new ArrayList();
        this.countriesMap = new HashMap();
        this.codesMap = new HashMap();
        this.phoneFormatMap = new HashMap();
        this.swipeBackEnabled = true;
        this.headerCell = new HeaderCell[3];
        this.dividers = new ArrayList();
        this.sectionCell = new ShadowSectionCell[3];
        this.bottomCell = new TextInfoPrivacyCell[3];
        this.settingsCell = new TextSettingsCell[2];
        this.detailSettingsCell = new TextDetailSettingsCell[7];
        this.shiftDp = -4.5f;
        this.emailCodeLength = 6;
        this.isCheckoutPreview = true;
        init(null, paymentForm, messageObject, str, 4, null, null, null, null, null, null, false, null, baseFragment);
    }

    public PaymentFormActivity(TLRPC.PaymentForm paymentForm, MessageObject messageObject, BaseFragment baseFragment) {
        this(paymentForm, messageObject, null, baseFragment);
    }

    public PaymentFormActivity(TLRPC.PaymentForm paymentForm, TLRPC.InputInvoice inputInvoice, BaseFragment baseFragment) {
        this(inputInvoice, paymentForm, null, null, 4, null, null, null, null, null, null, false, null, baseFragment);
        this.isCheckoutPreview = true;
    }

    public PaymentFormActivity(TLRPC.PaymentReceipt paymentReceipt) {
        TLRPC.TL_inputInvoiceSlug tL_inputInvoiceSlug;
        this.countriesArray = new ArrayList();
        this.countriesMap = new HashMap();
        this.codesMap = new HashMap();
        this.phoneFormatMap = new HashMap();
        this.swipeBackEnabled = true;
        this.headerCell = new HeaderCell[3];
        this.dividers = new ArrayList();
        this.sectionCell = new ShadowSectionCell[3];
        this.bottomCell = new TextInfoPrivacyCell[3];
        this.settingsCell = new TextSettingsCell[2];
        this.detailSettingsCell = new TextDetailSettingsCell[7];
        this.shiftDp = -4.5f;
        this.emailCodeLength = 6;
        this.currentStep = 5;
        TLRPC.PaymentForm paymentForm = new TLRPC.PaymentForm();
        this.paymentForm = paymentForm;
        this.paymentReceipt = paymentReceipt;
        paymentForm.bot_id = paymentReceipt.bot_id;
        paymentForm.invoice = paymentReceipt.invoice;
        paymentForm.provider_id = paymentReceipt.provider_id;
        paymentForm.users = paymentReceipt.users;
        this.shippingOption = paymentReceipt.shipping;
        long j = paymentReceipt.tip_amount;
        if (j != 0) {
            this.tipAmount = Long.valueOf(j);
        }
        TLRPC.User user = getMessagesController().getUser(Long.valueOf(paymentReceipt.bot_id));
        this.botUser = user;
        this.currentBotName = user != null ? user.first_name : "";
        this.currentItemName = paymentReceipt.title;
        if (paymentReceipt.info != null) {
            this.validateRequest = new TLRPC.TL_payments_validateRequestedInfo();
            if (this.messageObject != null) {
                TLRPC.TL_inputInvoiceMessage tL_inputInvoiceMessage = new TLRPC.TL_inputInvoiceMessage();
                tL_inputInvoiceMessage.peer = getMessagesController().getInputPeer(paymentReceipt.bot_id);
                tL_inputInvoiceSlug = tL_inputInvoiceMessage;
            } else {
                TLRPC.TL_inputInvoiceSlug tL_inputInvoiceSlug2 = new TLRPC.TL_inputInvoiceSlug();
                tL_inputInvoiceSlug2.slug = this.invoiceSlug;
                tL_inputInvoiceSlug = tL_inputInvoiceSlug2;
            }
            this.validateRequest.invoice = tL_inputInvoiceSlug;
            this.validateRequest.info = paymentReceipt.info;
        }
        this.cardName = paymentReceipt.credentials_title;
    }

    public void checkPassword() {
        if (UserConfig.getInstance(this.currentAccount).tmpPassword != null && UserConfig.getInstance(this.currentAccount).tmpPassword.valid_until < ConnectionsManager.getInstance(this.currentAccount).getCurrentTime() + 60) {
            UserConfig.getInstance(this.currentAccount).tmpPassword = null;
            UserConfig.getInstance(this.currentAccount).saveConfig(false);
        }
        if (UserConfig.getInstance(this.currentAccount).tmpPassword != null) {
            sendData();
            return;
        }
        if (this.inputFields[1].length() == 0) {
            try {
                this.inputFields[1].performHapticFeedback(3, 2);
            } catch (Exception unused) {
            }
            AndroidUtilities.shakeViewSpring(this.inputFields[1], 2.5f);
            return;
        }
        final String obj = this.inputFields[1].getText().toString();
        showEditDoneProgress(true, true);
        setDonePressed(true);
        final TL_account.getPassword getpassword = new TL_account.getPassword();
        ConnectionsManager.getInstance(this.currentAccount).sendRequest(getpassword, new RequestDelegate() {
            @Override
            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                PaymentFormActivity.this.lambda$checkPassword$74(obj, getpassword, tLObject, tL_error);
            }
        }, 2);
    }

    private void createGooglePayButton(Context context) {
        FrameLayout frameLayout = new FrameLayout(context);
        this.googlePayContainer = frameLayout;
        frameLayout.setBackgroundDrawable(Theme.getSelectorDrawable(true));
        this.googlePayContainer.setVisibility(8);
        FrameLayout frameLayout2 = new FrameLayout(context);
        this.googlePayButton = frameLayout2;
        frameLayout2.setClickable(true);
        this.googlePayButton.setFocusable(true);
        this.googlePayButton.setBackgroundResource(R.drawable.googlepay_button_no_shadow_background);
        if (this.googlePayPublicKey == null) {
            this.googlePayButton.setPadding(AndroidUtilities.dp(10.0f), AndroidUtilities.dp(2.0f), AndroidUtilities.dp(10.0f), AndroidUtilities.dp(2.0f));
        } else {
            this.googlePayButton.setPadding(AndroidUtilities.dp(2.0f), AndroidUtilities.dp(2.0f), AndroidUtilities.dp(2.0f), AndroidUtilities.dp(2.0f));
        }
        this.googlePayContainer.addView(this.googlePayButton, LayoutHelper.createFrame(-1, 48.0f));
        this.googlePayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public final void onClick(View view) {
                PaymentFormActivity.this.lambda$createGooglePayButton$32(view);
            }
        });
        LinearLayout linearLayout = new LinearLayout(context);
        linearLayout.setWeightSum(2.0f);
        linearLayout.setGravity(16);
        linearLayout.setOrientation(1);
        linearLayout.setDuplicateParentStateEnabled(true);
        this.googlePayButton.addView(linearLayout, LayoutHelper.createFrame(-1, -1.0f));
        ImageView imageView = new ImageView(context);
        imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
        imageView.setDuplicateParentStateEnabled(true);
        imageView.setImageResource(R.drawable.buy_with_googlepay_button_content);
        linearLayout.addView(imageView, LayoutHelper.createLinear(-1, 0, 1.0f));
        ImageView imageView2 = new ImageView(context);
        imageView2.setScaleType(ImageView.ScaleType.FIT_XY);
        imageView2.setDuplicateParentStateEnabled(true);
        imageView2.setImageResource(R.drawable.googlepay_button_overlay);
        this.googlePayButton.addView(imageView2, LayoutHelper.createFrame(-1, -1.0f));
    }

    private JSONObject getBaseCardPaymentMethod() {
        List asList = Arrays.asList("AMEX", "DISCOVER", "JCB", "MASTERCARD", "VISA");
        List asList2 = Arrays.asList("PAN_ONLY", "CRYPTOGRAM_3DS");
        JSONObject jSONObject = new JSONObject();
        jSONObject.put("type", "CARD");
        JSONObject jSONObject2 = new JSONObject();
        jSONObject2.put("allowedAuthMethods", new JSONArray((Collection) asList2));
        jSONObject2.put("allowedCardNetworks", new JSONArray((Collection) asList));
        jSONObject.put("parameters", jSONObject2);
        return jSONObject;
    }

    private JSONObject getBaseRequest() {
        return new JSONObject().put("apiVersion", 2).put("apiVersionMinor", 0);
    }

    public static String getResponseBody(InputStream inputStream) {
        String next = new Scanner(inputStream, "UTF-8").useDelimiter("\\A").next();
        inputStream.close();
        return next;
    }

    private long getStars() {
        TLRPC.InputInvoice inputInvoice = this.invoiceInput;
        if (!(inputInvoice instanceof TLRPC.TL_inputInvoiceStars)) {
            return 0L;
        }
        TLRPC.InputStorePaymentPurpose inputStorePaymentPurpose = ((TLRPC.TL_inputInvoiceStars) inputInvoice).purpose;
        if (inputStorePaymentPurpose instanceof TLRPC.TL_inputStorePaymentStarsGift) {
            return ((TLRPC.TL_inputStorePaymentStarsGift) inputStorePaymentPurpose).stars;
        }
        if (inputStorePaymentPurpose instanceof TLRPC.TL_inputStorePaymentStarsTopup) {
            return ((TLRPC.TL_inputStorePaymentStarsTopup) inputStorePaymentPurpose).stars;
        }
        if (inputStorePaymentPurpose instanceof TLRPC.TL_inputStorePaymentStarsGiveaway) {
            return ((TLRPC.TL_inputStorePaymentStarsGiveaway) inputStorePaymentPurpose).stars;
        }
        return 0L;
    }

    private long getStarsGiftUserId() {
        TLRPC.InputPeer inputPeer;
        TLRPC.InputInvoice inputInvoice = this.invoiceInput;
        if (!(inputInvoice instanceof TLRPC.TL_inputInvoiceStars)) {
            return 0L;
        }
        TLRPC.InputStorePaymentPurpose inputStorePaymentPurpose = ((TLRPC.TL_inputInvoiceStars) inputInvoice).purpose;
        if (inputStorePaymentPurpose instanceof TLRPC.TL_inputStorePaymentStarsGift) {
            TLRPC.InputUser inputUser = ((TLRPC.TL_inputStorePaymentStarsGift) inputStorePaymentPurpose).user_id;
            if (inputUser != null) {
                return inputUser.user_id;
            }
            return 0L;
        }
        if (!(inputStorePaymentPurpose instanceof TLRPC.TL_inputStorePaymentStarsGiveaway) || (inputPeer = ((TLRPC.TL_inputStorePaymentStarsGiveaway) inputStorePaymentPurpose).boost_peer) == null) {
            return 0L;
        }
        return DialogObject.getPeerDialogId(inputPeer);
    }

    private String getTotalPriceDecimalString(ArrayList arrayList) {
        long j = 0;
        for (int i = 0; i < arrayList.size(); i++) {
            j += ((TLRPC.TL_labeledPrice) arrayList.get(i)).amount;
        }
        return LocaleController.getInstance().formatCurrencyDecimalString(j, this.paymentForm.invoice.currency, false);
    }

    private String getTotalPriceString(ArrayList arrayList) {
        long j = 0;
        for (int i = 0; i < arrayList.size(); i++) {
            j += ((TLRPC.TL_labeledPrice) arrayList.get(i)).amount;
        }
        Long l = this.tipAmount;
        if (l != null) {
            j += l.longValue();
        }
        return LocaleController.getInstance().formatCurrencyString(j, this.paymentForm.invoice.currency);
    }

    public void goToNextStep() {
        int i;
        PaymentFormActivity paymentFormActivity;
        Runnable runnable;
        int i2;
        boolean z;
        PaymentFormActivity paymentFormActivity2;
        int i3 = this.currentStep;
        boolean z2 = true;
        if (i3 == 0) {
            PaymentFormActivityDelegate paymentFormActivityDelegate = this.delegate;
            if (paymentFormActivityDelegate != null) {
                paymentFormActivityDelegate.didSelectNewAddress(this.validateRequest);
                lambda$onBackPressed$323();
                return;
            }
            if (this.paymentForm.invoice.flexible) {
                i = 1;
            } else if (this.savedCredentialsCard == null && this.paymentJson == null) {
                i = 2;
            } else {
                if (UserConfig.getInstance(this.currentAccount).tmpPassword != null && UserConfig.getInstance(this.currentAccount).tmpPassword.valid_until < ConnectionsManager.getInstance(this.currentAccount).getCurrentTime() + 60) {
                    UserConfig.getInstance(this.currentAccount).tmpPassword = null;
                    UserConfig.getInstance(this.currentAccount).saveConfig(false);
                }
                i = UserConfig.getInstance(this.currentAccount).tmpPassword != null ? 4 : 3;
            }
            if (i == 2 && this.savedCredentialsCard == null && this.paymentJson == null && !this.paymentForm.additional_methods.isEmpty()) {
                runnable = new Runnable() {
                    @Override
                    public final void run() {
                        PaymentFormActivity.this.goToNextStep();
                    }
                };
                showChoosePaymentMethod(runnable);
                return;
            } else {
                paymentFormActivity = new PaymentFormActivity(this.invoiceInput, this.paymentForm, this.messageObject, this.invoiceSlug, i, this.requestedInfo, null, null, this.paymentJson, this.cardName, this.validateRequest, this.saveCardInfo, this.googlePayCredentials, this.parentFragment);
                presentFragment(paymentFormActivity, this.isWebView);
            }
        }
        if (i3 != 1) {
            if (i3 != 2) {
                if (i3 == 3) {
                    paymentFormActivity2 = new PaymentFormActivity(this.invoiceInput, this.paymentForm, this.messageObject, this.invoiceSlug, this.passwordOk ? 4 : 2, this.requestedInfo, this.shippingOption, this.tipAmount, this.paymentJson, this.cardName, this.validateRequest, this.saveCardInfo, this.googlePayCredentials, this.parentFragment);
                    z2 = true;
                } else if (i3 == 4) {
                    if (this.isCheckoutPreview) {
                        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.paymentFinished);
                    }
                    NotificationCenter.getInstance(this.currentAccount).lambda$postNotificationNameOnUIThread$1(NotificationCenter.paymentFinished, new Object[0]);
                    if (getMessagesController().newMessageCallback != null) {
                        AndroidUtilities.runOnUIThread(new Runnable() {
                            @Override
                            public final void run() {
                                PaymentFormActivity.this.lambda$goToNextStep$40();
                            }
                        }, 500L);
                        return;
                    } else if (onCheckoutSuccess(getParentLayout(), getParentActivity()) || isFinishing()) {
                        return;
                    }
                } else {
                    if (i3 != 6) {
                        return;
                    }
                    if (!this.delegate.didSelectNewCard(this.paymentJson, this.cardName, this.saveCardInfo, this.googlePayCredentials, this.savedCredentialsCard)) {
                        paymentFormActivity2 = new PaymentFormActivity(this.invoiceInput, this.paymentForm, this.messageObject, this.invoiceSlug, 4, this.requestedInfo, this.shippingOption, this.tipAmount, this.paymentJson, this.cardName, this.validateRequest, this.saveCardInfo, this.googlePayCredentials, this.parentFragment);
                    }
                }
                presentFragment(paymentFormActivity2, z2);
                return;
            }
            TLRPC.PaymentForm paymentForm = this.paymentForm;
            if (paymentForm.password_missing && (z = this.saveCardInfo)) {
                PaymentFormActivity paymentFormActivity3 = new PaymentFormActivity(this.invoiceInput, paymentForm, this.messageObject, this.invoiceSlug, 6, this.requestedInfo, this.shippingOption, this.tipAmount, this.paymentJson, this.cardName, this.validateRequest, z, this.googlePayCredentials, this.parentFragment);
                this.passwordFragment = paymentFormActivity3;
                paymentFormActivity3.setCurrentPassword(this.currentPassword);
                this.passwordFragment.setDelegate(new PaymentFormActivityDelegate() {
                    @Override
                    public void currentPasswordUpdated(TL_account.Password password) {
                        PaymentFormActivity.this.currentPassword = password;
                    }

                    @Override
                    public void didSelectNewAddress(TLRPC.TL_payments_validateRequestedInfo tL_payments_validateRequestedInfo) {
                        PaymentFormActivityDelegate.CC.$default$didSelectNewAddress(this, tL_payments_validateRequestedInfo);
                    }

                    @Override
                    public boolean didSelectNewCard(String str, String str2, boolean z3, TLRPC.TL_inputPaymentCredentialsGooglePay tL_inputPaymentCredentialsGooglePay, TLRPC.TL_paymentSavedCredentialsCard tL_paymentSavedCredentialsCard) {
                        if (PaymentFormActivity.this.delegate != null) {
                            PaymentFormActivity.this.delegate.didSelectNewCard(str, str2, z3, tL_inputPaymentCredentialsGooglePay, tL_paymentSavedCredentialsCard);
                        }
                        if (PaymentFormActivity.this.isWebView) {
                            PaymentFormActivity.this.removeSelfFromStack();
                        }
                        return PaymentFormActivity.this.delegate != null;
                    }

                    @Override
                    public void onFragmentDestroyed() {
                        PaymentFormActivity.this.passwordFragment = null;
                    }
                });
                paymentFormActivity = this.passwordFragment;
            } else {
                PaymentFormActivityDelegate paymentFormActivityDelegate2 = this.delegate;
                if (paymentFormActivityDelegate2 != null) {
                    paymentFormActivityDelegate2.didSelectNewCard(this.paymentJson, this.cardName, this.saveCardInfo, this.googlePayCredentials, null);
                } else {
                    paymentFormActivity = new PaymentFormActivity(this.invoiceInput, paymentForm, this.messageObject, this.invoiceSlug, 4, this.requestedInfo, this.shippingOption, this.tipAmount, this.paymentJson, this.cardName, this.validateRequest, this.saveCardInfo, this.googlePayCredentials, this.parentFragment);
                }
            }
            lambda$onBackPressed$323();
            return;
        }
        if (this.paymentJson == null && this.cardName == null) {
            if (this.savedCredentialsCard != null) {
                if (UserConfig.getInstance(this.currentAccount).tmpPassword != null && UserConfig.getInstance(this.currentAccount).tmpPassword.valid_until < ConnectionsManager.getInstance(this.currentAccount).getCurrentTime() + 60) {
                    UserConfig.getInstance(this.currentAccount).tmpPassword = null;
                    UserConfig.getInstance(this.currentAccount).saveConfig(false);
                }
                if (UserConfig.getInstance(this.currentAccount).tmpPassword == null) {
                    i2 = 3;
                }
            } else {
                i2 = 2;
            }
            if (i2 != 2 && this.cardName == null && this.savedCredentialsCard == null && this.paymentJson == null && !this.paymentForm.additional_methods.isEmpty()) {
                runnable = new Runnable() {
                    @Override
                    public final void run() {
                        PaymentFormActivity.this.goToNextStep();
                    }
                };
                showChoosePaymentMethod(runnable);
                return;
            }
            paymentFormActivity = new PaymentFormActivity(this.invoiceInput, this.paymentForm, this.messageObject, this.invoiceSlug, i2, this.requestedInfo, this.shippingOption, this.tipAmount, this.paymentJson, this.cardName, this.validateRequest, this.saveCardInfo, this.googlePayCredentials, this.parentFragment);
        }
        i2 = 4;
        if (i2 != 2) {
        }
        paymentFormActivity = new PaymentFormActivity(this.invoiceInput, this.paymentForm, this.messageObject, this.invoiceSlug, i2, this.requestedInfo, this.shippingOption, this.tipAmount, this.paymentJson, this.cardName, this.validateRequest, this.saveCardInfo, this.googlePayCredentials, this.parentFragment);
        presentFragment(paymentFormActivity, this.isWebView);
    }

    private void init(TLRPC.InputInvoice inputInvoice, TLRPC.PaymentForm paymentForm, MessageObject messageObject, String str, int i, TLRPC.TL_payments_validatedRequestedInfo tL_payments_validatedRequestedInfo, TLRPC.TL_shippingOption tL_shippingOption, Long l, String str2, String str3, TLRPC.TL_payments_validateRequestedInfo tL_payments_validateRequestedInfo, boolean z, TLRPC.TL_inputPaymentCredentialsGooglePay tL_inputPaymentCredentialsGooglePay, BaseFragment baseFragment) {
        this.currentStep = i;
        this.parentFragment = baseFragment;
        this.paymentJson = str2;
        this.googlePayCredentials = tL_inputPaymentCredentialsGooglePay;
        this.requestedInfo = tL_payments_validatedRequestedInfo;
        this.paymentForm = paymentForm;
        this.shippingOption = tL_shippingOption;
        this.tipAmount = l;
        this.messageObject = messageObject;
        this.invoiceSlug = str;
        this.invoiceInput = inputInvoice;
        this.saveCardInfo = z;
        this.isWebView = ("stripe".equals(paymentForm.native_provider) || "smartglocal".equals(this.paymentForm.native_provider)) ? false : true;
        TLRPC.User user = getMessagesController().getUser(Long.valueOf(paymentForm.bot_id));
        this.botUser = user;
        this.currentBotName = user != null ? user.first_name : "";
        this.currentItemName = paymentForm.title;
        this.validateRequest = tL_payments_validateRequestedInfo;
        this.saveShippingInfo = true;
        if (z || this.currentStep == 4) {
            this.saveCardInfo = z;
        } else {
            this.saveCardInfo = !this.paymentForm.saved_credentials.isEmpty();
        }
        if (str3 != null) {
            this.cardName = str3;
        } else {
            if (this.paymentForm.saved_credentials.isEmpty()) {
                return;
            }
            TLRPC.TL_paymentSavedCredentialsCard tL_paymentSavedCredentialsCard = this.paymentForm.saved_credentials.get(0);
            this.savedCredentialsCard = tL_paymentSavedCredentialsCard;
            this.cardName = tL_paymentSavedCredentialsCard.title;
        }
    }

    private void initGooglePay(Context context) {
        IsReadyToPayRequest fromJson;
        if (getParentActivity() == null) {
            return;
        }
        this.paymentsClient = Wallet.getPaymentsClient(context, new Wallet.WalletOptions.Builder().setEnvironment(this.paymentForm.invoice.test ? 3 : 1).setTheme(1).build());
        Optional isReadyToPayRequest = getIsReadyToPayRequest();
        if (isReadyToPayRequest.isPresent() && (fromJson = IsReadyToPayRequest.fromJson(((JSONObject) isReadyToPayRequest.get()).toString())) != null) {
            this.paymentsClient.isReadyToPay(fromJson).addOnCompleteListener(getParentActivity(), new OnCompleteListener() {
                @Override
                public final void onComplete(Task task) {
                    PaymentFormActivity.this.lambda$initGooglePay$37(task);
                }
            });
        }
    }

    public void lambda$checkPassword$70(TLObject tLObject, TLRPC.TL_error tL_error, TL_account.getTmpPassword gettmppassword) {
        showEditDoneProgress(true, false);
        setDonePressed(false);
        if (tLObject != null) {
            this.passwordOk = true;
            UserConfig.getInstance(this.currentAccount).tmpPassword = (TL_account.tmpPassword) tLObject;
            UserConfig.getInstance(this.currentAccount).saveConfig(false);
            goToNextStep();
            return;
        }
        if (!tL_error.text.equals("PASSWORD_HASH_INVALID")) {
            AlertsCreator.processError(this.currentAccount, tL_error, this, gettmppassword, new Object[0]);
            return;
        }
        try {
            this.inputFields[1].performHapticFeedback(3, 2);
        } catch (Exception unused) {
        }
        AndroidUtilities.shakeViewSpring(this.inputFields[1], 3.25f);
        this.inputFields[1].setText("");
    }

    public void lambda$checkPassword$71(final TL_account.getTmpPassword gettmppassword, final TLObject tLObject, final TLRPC.TL_error tL_error) {
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                PaymentFormActivity.this.lambda$checkPassword$70(tLObject, tL_error, gettmppassword);
            }
        });
    }

    public void lambda$checkPassword$72(TL_account.Password password, byte[] bArr) {
        TLRPC.PasswordKdfAlgo passwordKdfAlgo = password.current_algo;
        byte[] x = passwordKdfAlgo instanceof TLRPC.TL_passwordKdfAlgoSHA256SHA256PBKDF2HMACSHA512iter100000SHA256ModPow ? SRPHelper.getX(bArr, (TLRPC.TL_passwordKdfAlgoSHA256SHA256PBKDF2HMACSHA512iter100000SHA256ModPow) passwordKdfAlgo) : null;
        final TL_account.getTmpPassword gettmppassword = new TL_account.getTmpPassword();
        gettmppassword.period = 1800;
        RequestDelegate requestDelegate = new RequestDelegate() {
            @Override
            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                PaymentFormActivity.this.lambda$checkPassword$71(gettmppassword, tLObject, tL_error);
            }
        };
        TLRPC.PasswordKdfAlgo passwordKdfAlgo2 = password.current_algo;
        if (!(passwordKdfAlgo2 instanceof TLRPC.TL_passwordKdfAlgoSHA256SHA256PBKDF2HMACSHA512iter100000SHA256ModPow)) {
            TLRPC.TL_error tL_error = new TLRPC.TL_error();
            tL_error.text = "PASSWORD_HASH_INVALID";
            requestDelegate.run(null, tL_error);
            return;
        }
        TLRPC.TL_inputCheckPasswordSRP startCheck = SRPHelper.startCheck(x, password.srp_id, password.srp_B, (TLRPC.TL_passwordKdfAlgoSHA256SHA256PBKDF2HMACSHA512iter100000SHA256ModPow) passwordKdfAlgo2);
        gettmppassword.password = startCheck;
        if (startCheck != null) {
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(gettmppassword, requestDelegate, 10);
            return;
        }
        TLRPC.TL_error tL_error2 = new TLRPC.TL_error();
        tL_error2.text = "ALGO_INVALID";
        requestDelegate.run(null, tL_error2);
    }

    public void lambda$checkPassword$73(TLRPC.TL_error tL_error, TLObject tLObject, String str, TL_account.getPassword getpassword) {
        if (tL_error != null) {
            AlertsCreator.processError(this.currentAccount, tL_error, this, getpassword, new Object[0]);
            showEditDoneProgress(true, false);
            setDonePressed(false);
            return;
        }
        final TL_account.Password password = (TL_account.Password) tLObject;
        if (!TwoStepVerificationActivity.canHandleCurrentPassword(password, false)) {
            AlertsCreator.showUpdateAppAlert(getParentActivity(), LocaleController.getString(R.string.UpdateAppAlert), true);
        } else if (password.has_password) {
            final byte[] stringBytes = AndroidUtilities.getStringBytes(str);
            Utilities.globalQueue.postRunnable(new Runnable() {
                @Override
                public final void run() {
                    PaymentFormActivity.this.lambda$checkPassword$72(password, stringBytes);
                }
            });
        } else {
            this.passwordOk = false;
            goToNextStep();
        }
    }

    public void lambda$checkPassword$74(final String str, final TL_account.getPassword getpassword, final TLObject tLObject, final TLRPC.TL_error tL_error) {
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                PaymentFormActivity.this.lambda$checkPassword$73(tL_error, tLObject, str, getpassword);
            }
        });
    }

    public void lambda$createGooglePayButton$32(View view) {
        this.googlePayButton.setClickable(false);
        try {
            JSONObject baseRequest = getBaseRequest();
            JSONObject baseCardPaymentMethod = getBaseCardPaymentMethod();
            baseCardPaymentMethod.put("tokenizationSpecification", (this.googlePayPublicKey == null || this.googlePayParameters != null) ? new JSONObject() {
                {
                    put("type", "PAYMENT_GATEWAY");
                    put("parameters", PaymentFormActivity.this.googlePayParameters != null ? PaymentFormActivity.this.googlePayParameters : new JSONObject() {
                        {
                            put("gateway", "stripe");
                            put("stripe:publishableKey", PaymentFormActivity.this.providerApiKey);
                            put("stripe:version", "3.5.0");
                        }
                    });
                }
            } : new JSONObject() {
                {
                    put("type", "DIRECT");
                    put("parameters", new JSONObject() {
                        {
                            put("protocolVersion", "ECv2");
                            put("publicKey", PaymentFormActivity.this.googlePayPublicKey);
                        }
                    });
                }
            });
            baseRequest.put("allowedPaymentMethods", new JSONArray().put(baseCardPaymentMethod));
            JSONObject jSONObject = new JSONObject();
            ArrayList arrayList = new ArrayList(this.paymentForm.invoice.prices);
            TLRPC.TL_shippingOption tL_shippingOption = this.shippingOption;
            if (tL_shippingOption != null) {
                arrayList.addAll(tL_shippingOption.prices);
            }
            String totalPriceDecimalString = getTotalPriceDecimalString(arrayList);
            this.totalPriceDecimal = totalPriceDecimalString;
            jSONObject.put("totalPrice", totalPriceDecimalString);
            jSONObject.put("totalPriceStatus", "FINAL");
            if (!TextUtils.isEmpty(this.googlePayCountryCode)) {
                jSONObject.put("countryCode", this.googlePayCountryCode);
            }
            jSONObject.put("currencyCode", this.paymentForm.invoice.currency);
            jSONObject.put("checkoutOption", "COMPLETE_IMMEDIATE_PURCHASE");
            baseRequest.put("transactionInfo", jSONObject);
            baseRequest.put("merchantInfo", new JSONObject().put("merchantName", this.currentBotName));
            PaymentDataRequest fromJson = PaymentDataRequest.fromJson(baseRequest.toString());
            if (fromJson != null) {
                AutoResolveHelper.resolveTask(this.paymentsClient.loadPaymentData(fromJson), getParentActivity(), 991);
            }
        } catch (JSONException e) {
            FileLog.e(e);
        }
    }

    public void lambda$createView$0(CountrySelectActivity.Country country) {
        this.country = country;
        this.inputFields[4].setText(country.name);
        this.countryName = country.shortname;
    }

    public boolean lambda$createView$1(View view, MotionEvent motionEvent) {
        if (getParentActivity() == null) {
            return false;
        }
        if (motionEvent.getAction() == 1) {
            CountrySelectActivity countrySelectActivity = new CountrySelectActivity(false);
            countrySelectActivity.setDisableAnonymousNumbers(true);
            countrySelectActivity.setCountrySelectActivityDelegate(new CountrySelectActivity.CountrySelectActivityDelegate() {
                @Override
                public final void didSelectCountry(CountrySelectActivity.Country country) {
                    PaymentFormActivity.this.lambda$createView$0(country);
                }
            });
            presentFragment(countrySelectActivity);
        }
        return true;
    }

    public static boolean lambda$createView$10(View view, MotionEvent motionEvent) {
        return true;
    }

    public boolean lambda$createView$11(TextView textView, int i, KeyEvent keyEvent) {
        if (i != 6) {
            return false;
        }
        this.doneItem.performClick();
        return true;
    }

    public void lambda$createView$12(View view) {
        this.passwordOk = false;
        goToNextStep();
    }

    public void lambda$createView$13(View view) {
        this.inputFields[0].requestFocus();
        AndroidUtilities.showKeyboard(this.inputFields[0]);
    }

    public static boolean lambda$createView$14(TextView textView, int i, KeyEvent keyEvent) {
        if (i != 6) {
            return false;
        }
        AndroidUtilities.hideKeyboard(textView);
        return true;
    }

    public void lambda$createView$15(TextView textView, long j, View view) {
        long longValue = ((Long) textView.getTag()).longValue();
        Long l = this.tipAmount;
        if (l == null || longValue != l.longValue()) {
            this.inputFields[0].setText(LocaleController.getInstance().formatCurrencyString(j, false, true, true, this.paymentForm.invoice.currency));
        } else {
            this.ignoreOnTextChange = true;
            this.inputFields[0].setText("");
            this.ignoreOnTextChange = false;
            this.tipAmount = 0L;
            updateTotalPrice();
        }
        EditTextBoldCursor editTextBoldCursor = this.inputFields[0];
        editTextBoldCursor.setSelection(editTextBoldCursor.length());
    }

    public void lambda$createView$16(View view) {
        if (getParentActivity() == null) {
            return;
        }
        showChoosePaymentMethod();
    }

    public void lambda$createView$17(View view) {
        PaymentFormActivity paymentFormActivity = new PaymentFormActivity(this.invoiceInput, this.paymentForm, this.messageObject, this.invoiceSlug, 0, this.requestedInfo, this.shippingOption, this.tipAmount, null, this.cardName, this.validateRequest, this.saveCardInfo, null, this.parentFragment);
        paymentFormActivity.setDelegate(new PaymentFormActivityDelegate() {
            @Override
            public void currentPasswordUpdated(TL_account.Password password) {
                PaymentFormActivityDelegate.CC.$default$currentPasswordUpdated(this, password);
            }

            @Override
            public void didSelectNewAddress(TLRPC.TL_payments_validateRequestedInfo tL_payments_validateRequestedInfo) {
                PaymentFormActivity.this.validateRequest = tL_payments_validateRequestedInfo;
                PaymentFormActivity paymentFormActivity2 = PaymentFormActivity.this;
                paymentFormActivity2.setAddressFields(paymentFormActivity2.validateRequest.info);
            }

            @Override
            public boolean didSelectNewCard(String str, String str2, boolean z, TLRPC.TL_inputPaymentCredentialsGooglePay tL_inputPaymentCredentialsGooglePay, TLRPC.TL_paymentSavedCredentialsCard tL_paymentSavedCredentialsCard) {
                return PaymentFormActivityDelegate.CC.$default$didSelectNewCard(this, str, str2, z, tL_inputPaymentCredentialsGooglePay, tL_paymentSavedCredentialsCard);
            }

            @Override
            public void onFragmentDestroyed() {
                PaymentFormActivityDelegate.CC.$default$onFragmentDestroyed(this);
            }
        });
        presentFragment(paymentFormActivity);
    }

    public void lambda$createView$18(View view) {
        PaymentFormActivity paymentFormActivity = new PaymentFormActivity(this.invoiceInput, this.paymentForm, this.messageObject, this.invoiceSlug, 0, this.requestedInfo, this.shippingOption, this.tipAmount, null, this.cardName, this.validateRequest, this.saveCardInfo, null, this.parentFragment);
        paymentFormActivity.setDelegate(new PaymentFormActivityDelegate() {
            @Override
            public void currentPasswordUpdated(TL_account.Password password) {
                PaymentFormActivityDelegate.CC.$default$currentPasswordUpdated(this, password);
            }

            @Override
            public void didSelectNewAddress(TLRPC.TL_payments_validateRequestedInfo tL_payments_validateRequestedInfo) {
                PaymentFormActivity.this.validateRequest = tL_payments_validateRequestedInfo;
                PaymentFormActivity paymentFormActivity2 = PaymentFormActivity.this;
                paymentFormActivity2.setAddressFields(paymentFormActivity2.validateRequest.info);
            }

            @Override
            public boolean didSelectNewCard(String str, String str2, boolean z, TLRPC.TL_inputPaymentCredentialsGooglePay tL_inputPaymentCredentialsGooglePay, TLRPC.TL_paymentSavedCredentialsCard tL_paymentSavedCredentialsCard) {
                return PaymentFormActivityDelegate.CC.$default$didSelectNewCard(this, str, str2, z, tL_inputPaymentCredentialsGooglePay, tL_paymentSavedCredentialsCard);
            }

            @Override
            public void onFragmentDestroyed() {
                PaymentFormActivityDelegate.CC.$default$onFragmentDestroyed(this);
            }
        });
        presentFragment(paymentFormActivity);
    }

    public void lambda$createView$19(View view) {
        PaymentFormActivity paymentFormActivity = new PaymentFormActivity(this.invoiceInput, this.paymentForm, this.messageObject, this.invoiceSlug, 0, this.requestedInfo, this.shippingOption, this.tipAmount, null, this.cardName, this.validateRequest, this.saveCardInfo, null, this.parentFragment);
        paymentFormActivity.setDelegate(new PaymentFormActivityDelegate() {
            @Override
            public void currentPasswordUpdated(TL_account.Password password) {
                PaymentFormActivityDelegate.CC.$default$currentPasswordUpdated(this, password);
            }

            @Override
            public void didSelectNewAddress(TLRPC.TL_payments_validateRequestedInfo tL_payments_validateRequestedInfo) {
                PaymentFormActivity.this.validateRequest = tL_payments_validateRequestedInfo;
                PaymentFormActivity paymentFormActivity2 = PaymentFormActivity.this;
                paymentFormActivity2.setAddressFields(paymentFormActivity2.validateRequest.info);
            }

            @Override
            public boolean didSelectNewCard(String str, String str2, boolean z, TLRPC.TL_inputPaymentCredentialsGooglePay tL_inputPaymentCredentialsGooglePay, TLRPC.TL_paymentSavedCredentialsCard tL_paymentSavedCredentialsCard) {
                return PaymentFormActivityDelegate.CC.$default$didSelectNewCard(this, str, str2, z, tL_inputPaymentCredentialsGooglePay, tL_paymentSavedCredentialsCard);
            }

            @Override
            public void onFragmentDestroyed() {
                PaymentFormActivityDelegate.CC.$default$onFragmentDestroyed(this);
            }
        });
        presentFragment(paymentFormActivity);
    }

    public boolean lambda$createView$2(TextView textView, int i, KeyEvent keyEvent) {
        if (i != 5) {
            if (i != 6) {
                return false;
            }
            this.doneItem.performClick();
            return true;
        }
        int intValue = ((Integer) textView.getTag()).intValue();
        while (true) {
            intValue++;
            EditTextBoldCursor[] editTextBoldCursorArr = this.inputFields;
            if (intValue < editTextBoldCursorArr.length) {
                if (intValue != 4 && ((View) editTextBoldCursorArr[intValue].getParent()).getVisibility() == 0) {
                    this.inputFields[intValue].requestFocus();
                    break;
                }
            } else {
                break;
            }
        }
        return true;
    }

    public void lambda$createView$20(View view) {
        PaymentFormActivity paymentFormActivity = new PaymentFormActivity(this.invoiceInput, this.paymentForm, this.messageObject, this.invoiceSlug, 0, this.requestedInfo, this.shippingOption, this.tipAmount, null, this.cardName, this.validateRequest, this.saveCardInfo, null, this.parentFragment);
        paymentFormActivity.setDelegate(new PaymentFormActivityDelegate() {
            @Override
            public void currentPasswordUpdated(TL_account.Password password) {
                PaymentFormActivityDelegate.CC.$default$currentPasswordUpdated(this, password);
            }

            @Override
            public void didSelectNewAddress(TLRPC.TL_payments_validateRequestedInfo tL_payments_validateRequestedInfo) {
                PaymentFormActivity.this.validateRequest = tL_payments_validateRequestedInfo;
                PaymentFormActivity paymentFormActivity2 = PaymentFormActivity.this;
                paymentFormActivity2.setAddressFields(paymentFormActivity2.validateRequest.info);
            }

            @Override
            public boolean didSelectNewCard(String str, String str2, boolean z, TLRPC.TL_inputPaymentCredentialsGooglePay tL_inputPaymentCredentialsGooglePay, TLRPC.TL_paymentSavedCredentialsCard tL_paymentSavedCredentialsCard) {
                return PaymentFormActivityDelegate.CC.$default$didSelectNewCard(this, str, str2, z, tL_inputPaymentCredentialsGooglePay, tL_paymentSavedCredentialsCard);
            }

            @Override
            public void onFragmentDestroyed() {
                PaymentFormActivityDelegate.CC.$default$onFragmentDestroyed(this);
            }
        });
        presentFragment(paymentFormActivity);
    }

    public void lambda$createView$21(View view) {
        setDonePressed(false);
        view.callOnClick();
    }

    public void lambda$createView$22(AlertDialog alertDialog, int i) {
        showPayAlert(this.totalPrice[0]);
    }

    public void lambda$createView$23(java.lang.String r22, final android.view.View r23) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.PaymentFormActivity.lambda$createView$23(java.lang.String, android.view.View):void");
    }

    public void lambda$createView$24(View view) {
        if (this.donePressed) {
            return;
        }
        boolean z = !this.recurrentAccepted;
        this.recurrentAccepted = z;
        this.recurrentAcceptCell.setChecked(z);
        this.bottomLayout.setChecked(this.recurrentAccepted, true);
    }

    public boolean lambda$createView$25(TextView textView, int i, KeyEvent keyEvent) {
        if (i != 6) {
            return false;
        }
        sendSavePassword(false);
        return true;
    }

    public static void lambda$createView$26(TLObject tLObject, TLRPC.TL_error tL_error) {
    }

    public void lambda$createView$27(View view) {
        ConnectionsManager.getInstance(this.currentAccount).sendRequest(new TL_account.resendPasswordEmail(), new RequestDelegate() {
            @Override
            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                PaymentFormActivity.lambda$createView$26(tLObject, tL_error);
            }
        });
        AlertDialog.Builder builder = new AlertDialog.Builder(getParentActivity());
        builder.setMessage(LocaleController.getString(R.string.ResendCodeInfo));
        builder.setTitle(LocaleController.getString(R.string.AppName));
        builder.setPositiveButton(LocaleController.getString(R.string.OK), null);
        showDialog(builder.create());
    }

    public void lambda$createView$28(AlertDialog alertDialog, int i) {
        sendSavePassword(true);
    }

    public void lambda$createView$29(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getParentActivity());
        String string = LocaleController.getString(R.string.TurnPasswordOffQuestion);
        if (this.currentPassword.has_secure_values) {
            string = string + "\n\n" + LocaleController.getString(R.string.TurnPasswordOffPassport);
        }
        builder.setMessage(string);
        builder.setTitle(LocaleController.getString(R.string.TurnPasswordOffQuestionTitle));
        builder.setPositiveButton(LocaleController.getString(R.string.Disable), new AlertDialog.OnButtonClickListener() {
            @Override
            public final void onClick(AlertDialog alertDialog, int i) {
                PaymentFormActivity.this.lambda$createView$28(alertDialog, i);
            }
        });
        builder.setNegativeButton(LocaleController.getString(R.string.Cancel), null);
        AlertDialog create = builder.create();
        showDialog(create);
        TextView textView = (TextView) create.getButton(-1);
        if (textView != null) {
            textView.setTextColor(getThemedColor(Theme.key_text_RedBold));
        }
    }

    public void lambda$createView$3(View view) {
        boolean z = !this.saveShippingInfo;
        this.saveShippingInfo = z;
        this.checkCell1.setChecked(z);
    }

    public boolean lambda$createView$30(TextView textView, int i, KeyEvent keyEvent) {
        EditTextBoldCursor editTextBoldCursor;
        if (i == 6) {
            this.doneItem.performClick();
            return true;
        }
        if (i != 5) {
            return false;
        }
        int intValue = ((Integer) textView.getTag()).intValue();
        if (intValue == 0) {
            editTextBoldCursor = this.inputFields[1];
        } else {
            if (intValue != 1) {
                return false;
            }
            editTextBoldCursor = this.inputFields[2];
        }
        editTextBoldCursor.requestFocus();
        return false;
    }

    public void lambda$createView$4(View view) {
        boolean z = !this.saveCardInfo;
        this.saveCardInfo = z;
        this.checkCell1.setChecked(z);
    }

    public void lambda$createView$5(CountrySelectActivity.Country country) {
        this.country = country;
        this.inputFields[4].setText(country.name);
    }

    public boolean lambda$createView$6(View view, MotionEvent motionEvent) {
        if (getParentActivity() == null) {
            return false;
        }
        if (motionEvent.getAction() == 1) {
            CountrySelectActivity countrySelectActivity = new CountrySelectActivity(false);
            countrySelectActivity.setDisableAnonymousNumbers(true);
            countrySelectActivity.setCountrySelectActivityDelegate(new CountrySelectActivity.CountrySelectActivityDelegate() {
                @Override
                public final void didSelectCountry(CountrySelectActivity.Country country) {
                    PaymentFormActivity.this.lambda$createView$5(country);
                }
            });
            presentFragment(countrySelectActivity);
        }
        return true;
    }

    public boolean lambda$createView$7(TextView textView, int i, KeyEvent keyEvent) {
        if (i != 5) {
            if (i != 6) {
                return false;
            }
            this.doneItem.performClick();
            return true;
        }
        int intValue = ((Integer) textView.getTag()).intValue();
        while (true) {
            int i2 = intValue + 1;
            EditTextBoldCursor[] editTextBoldCursorArr = this.inputFields;
            if (i2 >= editTextBoldCursorArr.length) {
                break;
            }
            intValue = i2 == 4 ? intValue + 2 : i2;
            if (((View) editTextBoldCursorArr[intValue].getParent()).getVisibility() == 0) {
                this.inputFields[intValue].requestFocus();
                break;
            }
        }
        return true;
    }

    public void lambda$createView$8(View view) {
        boolean z = !this.saveCardInfo;
        this.saveCardInfo = z;
        this.checkCell1.setChecked(z);
    }

    public void lambda$createView$9(View view) {
        int intValue = ((Integer) view.getTag()).intValue();
        int i = 0;
        while (true) {
            RadioCell[] radioCellArr = this.radioCells;
            if (i >= radioCellArr.length) {
                return;
            }
            radioCellArr[i].setChecked(intValue == i, true);
            i++;
        }
    }

    public void lambda$goToNextStep$40() {
        getMessagesController().newMessageCallback = null;
        if (this.invoiceStatus == InvoiceStatus.PENDING && !isFinishing()) {
            InvoiceStatus invoiceStatus = InvoiceStatus.FAILED;
            this.invoiceStatus = invoiceStatus;
            PaymentFormCallback paymentFormCallback = this.paymentFormCallback;
            if (paymentFormCallback != null) {
                paymentFormCallback.onInvoiceStatusChanged(invoiceStatus);
            }
        } else if (this.invoiceStatus != InvoiceStatus.PAID || isFinishing()) {
            return;
        }
        lambda$onBackPressed$323();
    }

    public void lambda$initGooglePay$37(Task task) {
        if (!task.isSuccessful()) {
            FileLog.e("isReadyToPay failed", task.getException());
            return;
        }
        FrameLayout frameLayout = this.googlePayContainer;
        if (frameLayout != null) {
            frameLayout.setVisibility(0);
        }
    }

    public void lambda$loadPasswordInfo$33() {
        if (this.shortPollRunnable == null) {
            return;
        }
        loadPasswordInfo();
        this.shortPollRunnable = null;
    }

    public void lambda$loadPasswordInfo$34(TLRPC.TL_error tL_error, TLObject tLObject) {
        this.loadingPasswordInfo = false;
        if (tL_error == null) {
            TL_account.Password password = (TL_account.Password) tLObject;
            this.currentPassword = password;
            if (!TwoStepVerificationActivity.canHandleCurrentPassword(password, false)) {
                AlertsCreator.showUpdateAppAlert(getParentActivity(), LocaleController.getString(R.string.UpdateAppAlert), true);
                return;
            }
            TLRPC.PaymentForm paymentForm = this.paymentForm;
            if (paymentForm != null && this.currentPassword.has_password) {
                paymentForm.password_missing = false;
                paymentForm.can_save_credentials = true;
                updateSavePaymentField();
            }
            TwoStepVerificationActivity.initPasswordNewAlgo(this.currentPassword);
            PaymentFormActivity paymentFormActivity = this.passwordFragment;
            if (paymentFormActivity != null) {
                paymentFormActivity.setCurrentPassword(this.currentPassword);
            }
            if (this.currentPassword.has_password || this.shortPollRunnable != null) {
                return;
            }
            Runnable runnable = new Runnable() {
                @Override
                public final void run() {
                    PaymentFormActivity.this.lambda$loadPasswordInfo$33();
                }
            };
            this.shortPollRunnable = runnable;
            AndroidUtilities.runOnUIThread(runnable, 5000L);
        }
    }

    public void lambda$loadPasswordInfo$35(final TLObject tLObject, final TLRPC.TL_error tL_error) {
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                PaymentFormActivity.this.lambda$loadPasswordInfo$34(tL_error, tLObject);
            }
        });
    }

    public void lambda$onActivityResultFragment$39(int i, Intent intent) {
        String json;
        String optString;
        if (i == -1) {
            PaymentData fromIntent = PaymentData.getFromIntent(intent);
            if (fromIntent == null || (json = fromIntent.toJson()) == null) {
                return;
            }
            try {
                JSONObject jSONObject = new JSONObject(json).getJSONObject("paymentMethodData");
                JSONObject jSONObject2 = jSONObject.getJSONObject("tokenizationData");
                jSONObject2.getString("type");
                String string = jSONObject2.getString("token");
                if (this.googlePayPublicKey == null && this.googlePayParameters == null) {
                    Token parseToken = TokenParser.parseToken(string);
                    this.paymentJson = String.format(Locale.US, "{\"type\":\"%1$s\", \"id\":\"%2$s\"}", parseToken.getType(), parseToken.getId());
                    Card card = parseToken.getCard();
                    optString = card.getBrand() + " *" + card.getLast4();
                    this.cardName = optString;
                    goToNextStep();
                }
                TLRPC.TL_inputPaymentCredentialsGooglePay tL_inputPaymentCredentialsGooglePay = new TLRPC.TL_inputPaymentCredentialsGooglePay();
                this.googlePayCredentials = tL_inputPaymentCredentialsGooglePay;
                tL_inputPaymentCredentialsGooglePay.payment_token = new TLRPC.TL_dataJSON();
                this.googlePayCredentials.payment_token.data = jSONObject2.toString();
                optString = jSONObject.optString("description");
                if (TextUtils.isEmpty(optString)) {
                    optString = "Android Pay";
                }
                this.cardName = optString;
                goToNextStep();
            } catch (JSONException e) {
                FileLog.e(e);
            }
        } else if (i == 1) {
            Status statusFromIntent = AutoResolveHelper.getStatusFromIntent(intent);
            StringBuilder sb = new StringBuilder();
            sb.append("android pay error ");
            sb.append(statusFromIntent != null ? statusFromIntent.getStatusMessage() : "");
            FileLog.e(sb.toString());
        }
        showEditDoneProgress(true, false);
        setDonePressed(false);
        FrameLayout frameLayout = this.googlePayButton;
        if (frameLayout != null) {
            frameLayout.setClickable(true);
        }
    }

    public void lambda$onTransitionAnimationEnd$38() {
        this.inputFields[0].requestFocus();
        AndroidUtilities.showKeyboard(this.inputFields[0]);
    }

    public static void lambda$sendData$56(long j) {
        BaseFragment safeLastFragment = LaunchActivity.getSafeLastFragment();
        if (safeLastFragment != null) {
            safeLastFragment.presentFragment(ChatActivity.of(j));
        }
    }

    public void lambda$sendData$57(TLObject tLObject) {
        BaseFragment lastFragment;
        if (tLObject instanceof TLRPC.TL_payments_paymentReceiptStars) {
            StarsIntroActivity.showTransactionSheet(getContext(), false, this.currentAccount, (TLRPC.TL_payments_paymentReceiptStars) tLObject, getResourceProvider());
            return;
        }
        if (!(tLObject instanceof TLRPC.PaymentReceipt) || (lastFragment = LaunchActivity.getLastFragment()) == null) {
            return;
        }
        BaseFragment.BottomSheetParams bottomSheetParams = new BaseFragment.BottomSheetParams();
        bottomSheetParams.transitionFromLeft = true;
        bottomSheetParams.allowNestedScroll = false;
        lastFragment.showAsSheet(new PaymentFormActivity((TLRPC.PaymentReceipt) tLObject), bottomSheetParams);
    }

    public void lambda$sendData$58(final TLObject tLObject, TLRPC.TL_error tL_error) {
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                PaymentFormActivity.this.lambda$sendData$57(tLObject);
            }
        });
    }

    public void lambda$sendData$59(Bulletin bulletin, boolean z, TLRPC.Message[] messageArr, View view) {
        bulletin.hide();
        if (z) {
            BaseFragment safeLastFragment = LaunchActivity.getSafeLastFragment();
            if (safeLastFragment != null) {
                safeLastFragment.presentFragment(ChatActivity.of(MessageObject.getDialogId(messageArr[0]), messageArr[0].id));
                return;
            }
            return;
        }
        TLRPC.TL_payments_getPaymentReceipt tL_payments_getPaymentReceipt = new TLRPC.TL_payments_getPaymentReceipt();
        tL_payments_getPaymentReceipt.msg_id = messageArr[0].id;
        tL_payments_getPaymentReceipt.peer = MessagesController.getInstance(this.currentAccount).getInputPeer(messageArr[0].peer_id);
        ConnectionsManager.getInstance(this.currentAccount).sendRequest(tL_payments_getPaymentReceipt, new RequestDelegate() {
            @Override
            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                PaymentFormActivity.this.lambda$sendData$58(tLObject, tL_error);
            }
        }, 2);
    }

    public void lambda$sendData$60(final TLRPC.Message[] messageArr) {
        String str;
        TLRPC.Chat chat;
        String string;
        String formatString;
        final Bulletin createSimpleBulletin;
        PaymentFormCallback paymentFormCallback;
        PaymentFormCallback paymentFormCallback2;
        Context context = getContext();
        if (context == null) {
            context = ApplicationLoader.applicationContext;
        }
        if (context == null) {
            context = LaunchActivity.instance;
        }
        if (context == null) {
            return;
        }
        this.paymentStatusSent = true;
        InvoiceStatus invoiceStatus = InvoiceStatus.PAID;
        this.invoiceStatus = invoiceStatus;
        TLRPC.InputInvoice inputInvoice = this.invoiceInput;
        boolean z = inputInvoice instanceof TLRPC.TL_inputInvoiceStars;
        final boolean z2 = z && (((TLRPC.TL_inputInvoiceStars) inputInvoice).purpose instanceof TLRPC.TL_inputStorePaymentStarsGift);
        boolean z3 = z && (((TLRPC.TL_inputInvoiceStars) inputInvoice).purpose instanceof TLRPC.TL_inputStorePaymentStarsGiveaway);
        if (!z && (paymentFormCallback2 = this.paymentFormCallback) != null) {
            paymentFormCallback2.onInvoiceStatusChanged(invoiceStatus);
        }
        goToNextStep();
        if (z && (paymentFormCallback = this.paymentFormCallback) != null) {
            paymentFormCallback.onInvoiceStatusChanged(this.invoiceStatus);
        }
        final long starsGiftUserId = getStarsGiftUserId();
        if (starsGiftUserId > 0) {
            str = UserObject.getForcedFirstName(getMessagesController().getUser(Long.valueOf(starsGiftUserId)));
        } else {
            str = "";
            if (starsGiftUserId < 0 && (chat = getMessagesController().getChat(Long.valueOf(-starsGiftUserId))) != null) {
                str = chat.title;
            }
        }
        long stars = getStars();
        int i = z ? (z2 || z3) ? R.raw.stars_send : R.raw.stars_topup : R.raw.payment_success;
        if (z) {
            string = LocaleController.getString(z3 ? R.string.StarsGiveawaySentPopup : z2 ? R.string.StarsGiftSentPopup : R.string.StarsAcquired);
        } else {
            string = null;
        }
        if (!z) {
            formatString = LocaleController.formatString(R.string.PaymentInfoHint, this.totalPrice[0], this.currentItemName);
        } else if (z3) {
            formatString = LocaleController.formatPluralStringComma("StarsGiveawaySentPopupInfo", (int) stars);
        } else {
            formatString = LocaleController.formatPluralStringComma(z2 ? "StarsGiftSentPopupInfo" : "StarsAcquiredInfo", (int) stars, str);
        }
        SpannableStringBuilder replaceTags = AndroidUtilities.replaceTags(formatString);
        BaseFragment safeLastFragment = LaunchActivity.getSafeLastFragment();
        if (safeLastFragment == null) {
            return;
        }
        BulletinFactory of = BulletinFactory.of(safeLastFragment);
        if (starsGiftUserId == 0 || string == null || z3) {
            String str2 = string;
            createSimpleBulletin = str2 != null ? of.createSimpleBulletin(i, str2, replaceTags) : of.createSimpleBulletin(i, replaceTags);
        } else {
            createSimpleBulletin = of.createSimpleBulletin(i, string, replaceTags, LocaleController.getString(R.string.ViewInChat), new Runnable() {
                @Override
                public final void run() {
                    PaymentFormActivity.lambda$sendData$56(starsGiftUserId);
                }
            });
        }
        createSimpleBulletin.hideAfterBottomSheet = false;
        createSimpleBulletin.setDuration(5000);
        if (messageArr[0] != null) {
            createSimpleBulletin.setOnClickListener(new View.OnClickListener() {
                @Override
                public final void onClick(View view) {
                    PaymentFormActivity.this.lambda$sendData$59(createSimpleBulletin, z2, messageArr, view);
                }
            });
        }
        createSimpleBulletin.show(z3);
    }

    public static void lambda$sendData$61(long j) {
        BaseFragment safeLastFragment = LaunchActivity.getSafeLastFragment();
        if (safeLastFragment != null) {
            safeLastFragment.presentFragment(ChatActivity.of(j));
        }
    }

    public void lambda$sendData$62(TLObject tLObject) {
        BaseFragment lastFragment;
        if (tLObject instanceof TLRPC.TL_payments_paymentReceiptStars) {
            StarsIntroActivity.showTransactionSheet(getContext(), false, this.currentAccount, (TLRPC.TL_payments_paymentReceiptStars) tLObject, getResourceProvider());
            return;
        }
        if (!(tLObject instanceof TLRPC.PaymentReceipt) || (lastFragment = LaunchActivity.getLastFragment()) == null) {
            return;
        }
        BaseFragment.BottomSheetParams bottomSheetParams = new BaseFragment.BottomSheetParams();
        bottomSheetParams.transitionFromLeft = true;
        bottomSheetParams.allowNestedScroll = false;
        lastFragment.showAsSheet(new PaymentFormActivity((TLRPC.PaymentReceipt) tLObject), bottomSheetParams);
    }

    public void lambda$sendData$63(final TLObject tLObject, TLRPC.TL_error tL_error) {
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                PaymentFormActivity.this.lambda$sendData$62(tLObject);
            }
        });
    }

    public void lambda$sendData$64(Bulletin bulletin, boolean z, TLRPC.Message message, View view) {
        bulletin.hide();
        if (z) {
            BaseFragment safeLastFragment = LaunchActivity.getSafeLastFragment();
            if (safeLastFragment != null) {
                safeLastFragment.presentFragment(ChatActivity.of(MessageObject.getDialogId(message), message.id));
                return;
            }
            return;
        }
        TLRPC.TL_payments_getPaymentReceipt tL_payments_getPaymentReceipt = new TLRPC.TL_payments_getPaymentReceipt();
        tL_payments_getPaymentReceipt.msg_id = message.id;
        tL_payments_getPaymentReceipt.peer = MessagesController.getInstance(this.currentAccount).getInputPeer(message.peer_id);
        ConnectionsManager.getInstance(this.currentAccount).sendRequest(tL_payments_getPaymentReceipt, new RequestDelegate() {
            @Override
            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                PaymentFormActivity.this.lambda$sendData$63(tLObject, tL_error);
            }
        }, 2);
    }

    public void lambda$sendData$65(INavigationLayout iNavigationLayout, Activity activity, final TLRPC.Message message) {
        String str;
        TLRPC.Chat chat;
        String string;
        String formatString;
        final Bulletin createSimpleBulletin;
        PaymentFormCallback paymentFormCallback;
        PaymentFormCallback paymentFormCallback2;
        this.paymentStatusSent = true;
        this.invoiceStatus = InvoiceStatus.PAID;
        onCheckoutSuccess(iNavigationLayout, activity);
        TLRPC.InputInvoice inputInvoice = this.invoiceInput;
        boolean z = inputInvoice instanceof TLRPC.TL_inputInvoiceStars;
        final boolean z2 = z && (((TLRPC.TL_inputInvoiceStars) inputInvoice).purpose instanceof TLRPC.TL_inputStorePaymentStarsGift);
        boolean z3 = z && (((TLRPC.TL_inputInvoiceStars) inputInvoice).purpose instanceof TLRPC.TL_inputStorePaymentStarsGiveaway);
        if (!z && (paymentFormCallback2 = this.paymentFormCallback) != null) {
            paymentFormCallback2.onInvoiceStatusChanged(this.invoiceStatus);
        }
        goToNextStep();
        if (z && (paymentFormCallback = this.paymentFormCallback) != null) {
            paymentFormCallback.onInvoiceStatusChanged(this.invoiceStatus);
        }
        final long starsGiftUserId = getStarsGiftUserId();
        if (starsGiftUserId > 0) {
            str = UserObject.getForcedFirstName(getMessagesController().getUser(Long.valueOf(starsGiftUserId)));
        } else {
            str = "";
            if (starsGiftUserId < 0 && (chat = getMessagesController().getChat(Long.valueOf(-starsGiftUserId))) != null) {
                str = chat.title;
            }
        }
        long stars = getStars();
        int i = z ? (z2 || z3) ? R.raw.stars_send : R.raw.stars_topup : R.raw.payment_success;
        if (z) {
            string = LocaleController.getString(z3 ? R.string.StarsGiveawaySentPopup : z2 ? R.string.StarsGiftSentPopup : R.string.StarsAcquired);
        } else {
            string = null;
        }
        if (!z) {
            formatString = LocaleController.formatString(R.string.PaymentInfoHint, this.totalPrice[0], this.currentItemName);
        } else if (z3) {
            formatString = LocaleController.formatPluralStringComma("StarsGiveawaySentPopupInfo", (int) stars);
        } else {
            formatString = LocaleController.formatPluralStringComma(z2 ? "StarsGiftSentPopupInfo" : "StarsAcquiredInfo", (int) stars, str);
        }
        SpannableStringBuilder replaceTags = AndroidUtilities.replaceTags(formatString);
        BaseFragment safeLastFragment = LaunchActivity.getSafeLastFragment();
        if (safeLastFragment == null) {
            return;
        }
        BulletinFactory of = BulletinFactory.of(safeLastFragment);
        if (starsGiftUserId == 0 || string == null || z3) {
            String str2 = string;
            createSimpleBulletin = str2 != null ? of.createSimpleBulletin(i, str2, replaceTags) : of.createSimpleBulletin(i, replaceTags);
        } else {
            createSimpleBulletin = of.createSimpleBulletin(i, string, replaceTags, LocaleController.getString(R.string.ViewInChat), new Runnable() {
                @Override
                public final void run() {
                    PaymentFormActivity.lambda$sendData$61(starsGiftUserId);
                }
            });
        }
        createSimpleBulletin.hideAfterBottomSheet = false;
        createSimpleBulletin.setDuration(5000);
        if (message != null) {
            createSimpleBulletin.setOnClickListener(new View.OnClickListener() {
                @Override
                public final void onClick(View view) {
                    PaymentFormActivity.this.lambda$sendData$64(createSimpleBulletin, z2, message, view);
                }
            });
        }
        createSimpleBulletin.show(z3);
    }

    public boolean lambda$sendData$66(final INavigationLayout iNavigationLayout, final Activity activity, final TLRPC.Message message) {
        if (MessageObject.getPeerId(message.peer_id) != this.botUser.id || !(message.action instanceof TLRPC.TL_messageActionPaymentSent)) {
            return false;
        }
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                PaymentFormActivity.this.lambda$sendData$65(iNavigationLayout, activity, message);
            }
        });
        return true;
    }

    public void lambda$sendData$67(TLObject tLObject) {
        setDonePressed(false);
        this.webviewLoading = true;
        showEditDoneProgress(true, true);
        ContextProgressView contextProgressView = this.progressView;
        if (contextProgressView != null) {
            contextProgressView.setVisibility(0);
        }
        ActionBarMenuItem actionBarMenuItem = this.doneItem;
        if (actionBarMenuItem != null) {
            actionBarMenuItem.setEnabled(false);
            this.doneItem.getContentView().setVisibility(4);
        }
        final INavigationLayout parentLayout = getParentLayout();
        final Activity parentActivity = getParentActivity();
        getMessagesController().newMessageCallback = new MessagesController.NewMessageCallback() {
            @Override
            public final boolean onMessageReceived(TLRPC.Message message) {
                boolean lambda$sendData$66;
                lambda$sendData$66 = PaymentFormActivity.this.lambda$sendData$66(parentLayout, parentActivity, message);
                return lambda$sendData$66;
            }
        };
        WebView webView = this.webView;
        if (webView != null) {
            webView.setVisibility(0);
            WebView webView2 = this.webView;
            String str = ((TLRPC.TL_payments_paymentVerificationNeeded) tLObject).url;
            this.webViewUrl = str;
            webView2.loadUrl(str);
        }
        this.paymentStatusSent = true;
        InvoiceStatus invoiceStatus = InvoiceStatus.PENDING;
        this.invoiceStatus = invoiceStatus;
        PaymentFormCallback paymentFormCallback = this.paymentFormCallback;
        if (paymentFormCallback != null) {
            paymentFormCallback.onInvoiceStatusChanged(invoiceStatus);
        }
    }

    public void lambda$sendData$68(TLRPC.TL_error tL_error, TLRPC.TL_payments_sendPaymentForm tL_payments_sendPaymentForm) {
        AlertsCreator.processError(this.currentAccount, tL_error, this, tL_payments_sendPaymentForm, new Object[0]);
        setDonePressed(false);
        showEditDoneProgress(false, false);
        this.paymentStatusSent = true;
        InvoiceStatus invoiceStatus = InvoiceStatus.FAILED;
        this.invoiceStatus = invoiceStatus;
        PaymentFormCallback paymentFormCallback = this.paymentFormCallback;
        if (paymentFormCallback != null) {
            paymentFormCallback.onInvoiceStatusChanged(invoiceStatus);
        }
    }

    public void lambda$sendData$69(final TLRPC.TL_payments_sendPaymentForm tL_payments_sendPaymentForm, final TLObject tLObject, final TLRPC.TL_error tL_error) {
        Runnable runnable;
        if (tLObject == null) {
            AndroidUtilities.runOnUIThread(new Runnable() {
                @Override
                public final void run() {
                    PaymentFormActivity.this.lambda$sendData$68(tL_error, tL_payments_sendPaymentForm);
                }
            });
            return;
        }
        if (tLObject instanceof TLRPC.TL_payments_paymentResult) {
            TLRPC.Updates updates = ((TLRPC.TL_payments_paymentResult) tLObject).updates;
            final TLRPC.Message[] messageArr = new TLRPC.Message[1];
            int size = updates.updates.size();
            int i = 0;
            while (true) {
                if (i >= size) {
                    break;
                }
                TLRPC.Update update = updates.updates.get(i);
                if (update instanceof TLRPC.TL_updateNewMessage) {
                    messageArr[0] = ((TLRPC.TL_updateNewMessage) update).message;
                    break;
                } else {
                    if (update instanceof TLRPC.TL_updateNewChannelMessage) {
                        messageArr[0] = ((TLRPC.TL_updateNewChannelMessage) update).message;
                        break;
                    }
                    i++;
                }
            }
            getMessagesController().processUpdates(updates, false);
            runnable = new Runnable() {
                @Override
                public final void run() {
                    PaymentFormActivity.this.lambda$sendData$60(messageArr);
                }
            };
        } else if (!(tLObject instanceof TLRPC.TL_payments_paymentVerificationNeeded)) {
            return;
        } else {
            runnable = new Runnable() {
                @Override
                public final void run() {
                    PaymentFormActivity.this.lambda$sendData$67(tLObject);
                }
            };
        }
        AndroidUtilities.runOnUIThread(runnable);
    }

    public static void lambda$sendForm$52(TLObject tLObject, TLRPC.TL_error tL_error) {
    }

    public void lambda$sendForm$53(TLObject tLObject) {
        this.requestedInfo = (TLRPC.TL_payments_validatedRequestedInfo) tLObject;
        if (this.paymentForm.saved_info != null && !this.saveShippingInfo) {
            TLRPC.TL_payments_clearSavedInfo tL_payments_clearSavedInfo = new TLRPC.TL_payments_clearSavedInfo();
            tL_payments_clearSavedInfo.info = true;
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(tL_payments_clearSavedInfo, new RequestDelegate() {
                @Override
                public final void run(TLObject tLObject2, TLRPC.TL_error tL_error) {
                    PaymentFormActivity.lambda$sendForm$52(tLObject2, tL_error);
                }
            });
        }
        goToNextStep();
        setDonePressed(false);
        showEditDoneProgress(true, false);
    }

    public void lambda$sendForm$54(TLRPC.TL_error tL_error, TLObject tLObject) {
        setDonePressed(false);
        showEditDoneProgress(true, false);
        if (tL_error != null) {
            String str = tL_error.text;
            str.hashCode();
            char c = 65535;
            switch (str.hashCode()) {
                case -2092780146:
                    if (str.equals("ADDRESS_CITY_INVALID")) {
                        c = 0;
                        break;
                    }
                    break;
                case -1623547228:
                    if (str.equals("ADDRESS_STREET_LINE1_INVALID")) {
                        c = 1;
                        break;
                    }
                    break;
                case -1224177757:
                    if (str.equals("ADDRESS_COUNTRY_INVALID")) {
                        c = 2;
                        break;
                    }
                    break;
                case -1031752045:
                    if (str.equals("REQ_INFO_NAME_INVALID")) {
                        c = 3;
                        break;
                    }
                    break;
                case -274035920:
                    if (str.equals("ADDRESS_POSTCODE_INVALID")) {
                        c = 4;
                        break;
                    }
                    break;
                case 417441502:
                    if (str.equals("ADDRESS_STATE_INVALID")) {
                        c = 5;
                        break;
                    }
                    break;
                case 708423542:
                    if (str.equals("REQ_INFO_PHONE_INVALID")) {
                        c = 6;
                        break;
                    }
                    break;
                case 863965605:
                    if (str.equals("ADDRESS_STREET_LINE2_INVALID")) {
                        c = 7;
                        break;
                    }
                    break;
                case 889106340:
                    if (str.equals("REQ_INFO_EMAIL_INVALID")) {
                        c = '\b';
                        break;
                    }
                    break;
            }
            switch (c) {
                case 0:
                    shakeField(2);
                    return;
                case 1:
                    shakeField(0);
                    return;
                case 2:
                    shakeField(4);
                    return;
                case 3:
                    shakeField(6);
                    return;
                case 4:
                    shakeField(5);
                    return;
                case 5:
                    shakeField(3);
                    return;
                case 6:
                    shakeField(9);
                    return;
                case 7:
                    shakeField(1);
                    return;
                case '\b':
                    shakeField(7);
                    return;
                default:
                    AlertsCreator.processError(this.currentAccount, tL_error, this, tLObject, new Object[0]);
                    return;
            }
        }
    }

    public void lambda$sendForm$55(final TLObject tLObject, final TLObject tLObject2, final TLRPC.TL_error tL_error) {
        if (tLObject2 instanceof TLRPC.TL_payments_validatedRequestedInfo) {
            AndroidUtilities.runOnUIThread(new Runnable() {
                @Override
                public final void run() {
                    PaymentFormActivity.this.lambda$sendForm$53(tLObject2);
                }
            });
        } else {
            AndroidUtilities.runOnUIThread(new Runnable() {
                @Override
                public final void run() {
                    PaymentFormActivity.this.lambda$sendForm$54(tL_error, tLObject);
                }
            });
        }
    }

    public void lambda$sendSavePassword$41(TLRPC.TL_error tL_error) {
        showEditDoneProgress(true, false);
        if (tL_error == null) {
            if (getParentActivity() == null) {
                return;
            }
            Runnable runnable = this.shortPollRunnable;
            if (runnable != null) {
                AndroidUtilities.cancelRunOnUIThread(runnable);
                this.shortPollRunnable = null;
            }
            goToNextStep();
            return;
        }
        if (tL_error.text.startsWith("CODE_INVALID")) {
            shakeView(this.codeFieldCell);
            this.codeFieldCell.setText("", false);
        } else if (!tL_error.text.startsWith("FLOOD_WAIT")) {
            showAlertWithText(LocaleController.getString(R.string.AppName), tL_error.text);
        } else {
            int intValue = Utilities.parseInt((CharSequence) tL_error.text).intValue();
            showAlertWithText(LocaleController.getString(R.string.AppName), LocaleController.formatString("FloodWaitTime", R.string.FloodWaitTime, intValue < 60 ? LocaleController.formatPluralString("Seconds", intValue, new Object[0]) : LocaleController.formatPluralString("Minutes", intValue / 60, new Object[0])));
        }
    }

    public void lambda$sendSavePassword$42(TLObject tLObject, final TLRPC.TL_error tL_error) {
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                PaymentFormActivity.this.lambda$sendSavePassword$41(tL_error);
            }
        });
    }

    public void lambda$sendSavePassword$43(TLRPC.TL_error tL_error, TLObject tLObject, boolean z) {
        if (tL_error == null) {
            TL_account.Password password = (TL_account.Password) tLObject;
            this.currentPassword = password;
            TwoStepVerificationActivity.initPasswordNewAlgo(password);
            sendSavePassword(z);
        }
    }

    public void lambda$sendSavePassword$44(final boolean z, final TLObject tLObject, final TLRPC.TL_error tL_error) {
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                PaymentFormActivity.this.lambda$sendSavePassword$43(tL_error, tLObject, z);
            }
        });
    }

    public void lambda$sendSavePassword$45(String str, AlertDialog alertDialog, int i) {
        this.waitingForEmail = true;
        this.currentPassword.email_unconfirmed_pattern = str;
        updatePasswordFields();
    }

    public void lambda$sendSavePassword$46(TLRPC.TL_error tL_error, final boolean z, TLObject tLObject, final String str) {
        String string;
        String str2;
        if (tL_error != null && "SRP_ID_INVALID".equals(tL_error.text)) {
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(new TL_account.getPassword(), new RequestDelegate() {
                @Override
                public final void run(TLObject tLObject2, TLRPC.TL_error tL_error2) {
                    PaymentFormActivity.this.lambda$sendSavePassword$44(z, tLObject2, tL_error2);
                }
            }, 8);
            return;
        }
        showEditDoneProgress(true, false);
        if (z) {
            TL_account.Password password = this.currentPassword;
            password.has_password = false;
            password.current_algo = null;
            this.delegate.currentPasswordUpdated(password);
            lambda$onBackPressed$323();
            return;
        }
        if (tL_error == null && (tLObject instanceof TLRPC.TL_boolTrue)) {
            if (getParentActivity() == null) {
                return;
            }
            goToNextStep();
            return;
        }
        if (tL_error != null) {
            if (tL_error.text.equals("EMAIL_UNCONFIRMED") || tL_error.text.startsWith("EMAIL_UNCONFIRMED_")) {
                this.emailCodeLength = Utilities.parseInt((CharSequence) tL_error.text).intValue();
                AlertDialog.Builder builder = new AlertDialog.Builder(getParentActivity());
                builder.setPositiveButton(LocaleController.getString(R.string.OK), new AlertDialog.OnButtonClickListener() {
                    @Override
                    public final void onClick(AlertDialog alertDialog, int i) {
                        PaymentFormActivity.this.lambda$sendSavePassword$45(str, alertDialog, i);
                    }
                });
                builder.setMessage(LocaleController.getString(R.string.YourEmailAlmostThereText));
                builder.setTitle(LocaleController.getString(R.string.YourEmailAlmostThere));
                Dialog showDialog = showDialog(builder.create());
                if (showDialog != null) {
                    showDialog.setCanceledOnTouchOutside(false);
                    showDialog.setCancelable(false);
                    return;
                }
                return;
            }
            if (tL_error.text.equals("EMAIL_INVALID")) {
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
        }
    }

    public void lambda$sendSavePassword$47(final boolean z, final String str, final TLObject tLObject, final TLRPC.TL_error tL_error) {
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                PaymentFormActivity.this.lambda$sendSavePassword$46(tL_error, z, tLObject, str);
            }
        });
    }

    public void lambda$sendSavePassword$48(final boolean z, final String str, String str2, TL_account.updatePasswordSettings updatepasswordsettings) {
        RequestDelegate requestDelegate = new RequestDelegate() {
            @Override
            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                PaymentFormActivity.this.lambda$sendSavePassword$47(z, str, tLObject, tL_error);
            }
        };
        if (!z) {
            byte[] stringBytes = AndroidUtilities.getStringBytes(str2);
            TLRPC.PasswordKdfAlgo passwordKdfAlgo = this.currentPassword.new_algo;
            if (!(passwordKdfAlgo instanceof TLRPC.TL_passwordKdfAlgoSHA256SHA256PBKDF2HMACSHA512iter100000SHA256ModPow)) {
                TLRPC.TL_error tL_error = new TLRPC.TL_error();
                tL_error.text = "PASSWORD_HASH_INVALID";
                requestDelegate.run(null, tL_error);
                return;
            } else {
                updatepasswordsettings.new_settings.new_password_hash = SRPHelper.getVBytes(stringBytes, (TLRPC.TL_passwordKdfAlgoSHA256SHA256PBKDF2HMACSHA512iter100000SHA256ModPow) passwordKdfAlgo);
                if (updatepasswordsettings.new_settings.new_password_hash == null) {
                    TLRPC.TL_error tL_error2 = new TLRPC.TL_error();
                    tL_error2.text = "ALGO_INVALID";
                    requestDelegate.run(null, tL_error2);
                }
            }
        }
        ConnectionsManager.getInstance(this.currentAccount).sendRequest(updatepasswordsettings, requestDelegate, 10);
    }

    public void lambda$sendSavedForm$49(TLObject tLObject, Runnable runnable) {
        this.requestedInfo = (TLRPC.TL_payments_validatedRequestedInfo) tLObject;
        runnable.run();
        setDonePressed(false);
        showEditDoneProgress(true, false);
    }

    public void lambda$sendSavedForm$50(TLRPC.TL_error tL_error, TLObject tLObject) {
        setDonePressed(false);
        showEditDoneProgress(true, false);
        if (tL_error != null) {
            AlertsCreator.processError(this.currentAccount, tL_error, this, tLObject, new Object[0]);
        }
    }

    public void lambda$sendSavedForm$51(final Runnable runnable, final TLObject tLObject, final TLObject tLObject2, final TLRPC.TL_error tL_error) {
        if (tLObject2 instanceof TLRPC.TL_payments_validatedRequestedInfo) {
            AndroidUtilities.runOnUIThread(new Runnable() {
                @Override
                public final void run() {
                    PaymentFormActivity.this.lambda$sendSavedForm$49(tLObject2, runnable);
                }
            });
        } else {
            AndroidUtilities.runOnUIThread(new Runnable() {
                @Override
                public final void run() {
                    PaymentFormActivity.this.lambda$sendSavedForm$50(tL_error, tLObject);
                }
            });
        }
    }

    public void lambda$showChoosePaymentMethod$31(final Runnable runnable, List list, List list2, DialogInterface dialogInterface, int i) {
        PaymentFormActivityDelegate paymentFormActivityDelegate = new PaymentFormActivityDelegate() {
            @Override
            public void currentPasswordUpdated(TL_account.Password password) {
                PaymentFormActivityDelegate.CC.$default$currentPasswordUpdated(this, password);
            }

            @Override
            public void didSelectNewAddress(TLRPC.TL_payments_validateRequestedInfo tL_payments_validateRequestedInfo) {
                PaymentFormActivityDelegate.CC.$default$didSelectNewAddress(this, tL_payments_validateRequestedInfo);
            }

            @Override
            public boolean didSelectNewCard(String str, String str2, boolean z, TLRPC.TL_inputPaymentCredentialsGooglePay tL_inputPaymentCredentialsGooglePay, TLRPC.TL_paymentSavedCredentialsCard tL_paymentSavedCredentialsCard) {
                String str3;
                PaymentFormActivity.this.savedCredentialsCard = tL_paymentSavedCredentialsCard;
                PaymentFormActivity.this.paymentJson = str;
                PaymentFormActivity.this.saveCardInfo = z;
                PaymentFormActivity.this.cardName = str2;
                PaymentFormActivity.this.googlePayCredentials = tL_inputPaymentCredentialsGooglePay;
                if (PaymentFormActivity.this.detailSettingsCell[0] != null) {
                    PaymentFormActivity.this.detailSettingsCell[0].setVisibility(0);
                    TextDetailSettingsCell textDetailSettingsCell = PaymentFormActivity.this.detailSettingsCell[0];
                    if (PaymentFormActivity.this.cardName == null || PaymentFormActivity.this.cardName.length() <= 1) {
                        str3 = PaymentFormActivity.this.cardName;
                    } else {
                        str3 = PaymentFormActivity.this.cardName.substring(0, 1).toUpperCase() + PaymentFormActivity.this.cardName.substring(1);
                    }
                    textDetailSettingsCell.setTextAndValueAndIcon(str3, LocaleController.getString(R.string.PaymentCheckoutMethod), R.drawable.msg_payment_card, true);
                    if (PaymentFormActivity.this.detailSettingsCell[1] != null) {
                        PaymentFormActivity.this.detailSettingsCell[1].setVisibility(0);
                    }
                }
                Runnable runnable2 = runnable;
                if (runnable2 != null) {
                    runnable2.run();
                }
                return false;
            }

            @Override
            public void onFragmentDestroyed() {
                PaymentFormActivityDelegate.CC.$default$onFragmentDestroyed(this);
            }
        };
        TLRPC.TL_paymentSavedCredentialsCard tL_paymentSavedCredentialsCard = this.savedCredentialsCard;
        int i2 = (tL_paymentSavedCredentialsCard == null && this.cardName == null) ? 0 : 1;
        if (!(tL_paymentSavedCredentialsCard == null && this.cardName == null) && i == 0) {
            return;
        }
        if (i >= i2 && i < list.size() + i2) {
            TLRPC.TL_paymentSavedCredentialsCard tL_paymentSavedCredentialsCard2 = (TLRPC.TL_paymentSavedCredentialsCard) list.get(i - i2);
            this.savedCredentialsCard = tL_paymentSavedCredentialsCard2;
            paymentFormActivityDelegate.didSelectNewCard(null, tL_paymentSavedCredentialsCard2.title, true, null, tL_paymentSavedCredentialsCard2);
        } else {
            if (i < list2.size() - 1) {
                TLRPC.TL_paymentFormMethod tL_paymentFormMethod = this.paymentForm.additional_methods.get((i - list.size()) - i2);
                PaymentFormActivity paymentFormActivity = new PaymentFormActivity(this.invoiceInput, this.paymentForm, this.messageObject, this.invoiceSlug, 2, this.requestedInfo, this.shippingOption, this.tipAmount, null, this.cardName, this.validateRequest, this.saveCardInfo, null, this.parentFragment);
                paymentFormActivity.setPaymentMethod(tL_paymentFormMethod);
                paymentFormActivity.setDelegate(paymentFormActivityDelegate);
                presentFragment(paymentFormActivity);
                return;
            }
            if (i == list2.size() - 1) {
                PaymentFormActivity paymentFormActivity2 = new PaymentFormActivity(this.invoiceInput, this.paymentForm, this.messageObject, this.invoiceSlug, 2, this.requestedInfo, this.shippingOption, this.tipAmount, null, this.cardName, this.validateRequest, this.saveCardInfo, null, this.parentFragment);
                paymentFormActivity2.setDelegate(paymentFormActivityDelegate);
                presentFragment(paymentFormActivity2);
            }
        }
    }

    public void lambda$showPayAlert$36(AlertDialog alertDialog, int i) {
        setDonePressed(true);
        sendData();
    }

    private void loadPasswordInfo() {
        if (this.loadingPasswordInfo) {
            return;
        }
        this.loadingPasswordInfo = true;
        ConnectionsManager.getInstance(this.currentAccount).sendRequest(new TL_account.getPassword(), new RequestDelegate() {
            @Override
            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                PaymentFormActivity.this.lambda$loadPasswordInfo$35(tLObject, tL_error);
            }
        }, 10);
    }

    private boolean onCheckoutSuccess(INavigationLayout iNavigationLayout, Activity activity) {
        if (this.invoiceInput != null) {
            if (iNavigationLayout == null) {
                return false;
            }
            Iterator it = new ArrayList(iNavigationLayout.getFragmentStack()).iterator();
            while (it.hasNext()) {
                BaseFragment baseFragment = (BaseFragment) it.next();
                if (baseFragment instanceof PaymentFormActivity) {
                    baseFragment.removeSelfFromStack();
                }
            }
            return true;
        }
        String str = this.botUser.username;
        if ((!(str != null && str.equalsIgnoreCase(getMessagesController().premiumBotUsername) && this.invoiceSlug == null) && (this.invoiceSlug == null || getMessagesController().premiumInvoiceSlug == null || !Objects.equals(this.invoiceSlug, getMessagesController().premiumInvoiceSlug))) || iNavigationLayout == null) {
            return false;
        }
        Iterator it2 = new ArrayList(iNavigationLayout.getFragmentStack()).iterator();
        while (it2.hasNext()) {
            BaseFragment baseFragment2 = (BaseFragment) it2.next();
            if ((baseFragment2 instanceof ChatActivity) || (baseFragment2 instanceof PremiumPreviewFragment)) {
                baseFragment2.removeSelfFromStack();
            }
        }
        iNavigationLayout.presentFragment(new PremiumPreviewFragment(null).setForcePremium(), !isFinishing());
        if (activity instanceof LaunchActivity) {
            try {
                this.fragmentView.performHapticFeedback(3, 2);
            } catch (Exception unused) {
            }
            ((LaunchActivity) activity).getFireworksOverlay().start();
        }
        return true;
    }

    private void onPresentFragment(BaseFragment baseFragment) {
        AndroidUtilities.hideKeyboard(this.fragmentView);
        if (baseFragment instanceof PaymentFormActivity) {
            PaymentFormActivity paymentFormActivity = (PaymentFormActivity) baseFragment;
            paymentFormActivity.paymentFormCallback = this.paymentFormCallback;
            paymentFormActivity.resourcesProvider = this.resourcesProvider;
            paymentFormActivity.needPayAfterTransition = this.needPayAfterTransition;
            paymentFormActivity.savedCredentialsCard = this.savedCredentialsCard;
        }
    }

    public boolean sendCardData() {
        Integer num;
        Integer num2;
        String[] split = this.inputFields[1].getText().toString().split("/");
        if (split.length == 2) {
            Integer parseInt = Utilities.parseInt((CharSequence) split[0]);
            num2 = Utilities.parseInt((CharSequence) split[1]);
            num = parseInt;
        } else {
            num = null;
            num2 = null;
        }
        final Card card = new Card(this.inputFields[0].getText().toString(), num, num2, this.inputFields[3].getText().toString(), this.inputFields[2].getText().toString(), null, null, null, null, this.inputFields[5].getText().toString(), this.inputFields[4].getText().toString(), null);
        this.cardName = card.getBrand() + " *" + card.getLast4();
        if (!card.validateNumber()) {
            shakeField(0);
            return false;
        }
        if (!card.validateExpMonth() || !card.validateExpYear() || !card.validateExpiryDate()) {
            shakeField(1);
            return false;
        }
        if (this.need_card_name && this.inputFields[2].length() == 0) {
            shakeField(2);
            return false;
        }
        if (!card.validateCVC()) {
            shakeField(3);
            return false;
        }
        if (this.need_card_country && this.inputFields[4].length() == 0) {
            shakeField(4);
            return false;
        }
        if (this.need_card_postcode && this.inputFields[5].length() == 0) {
            shakeField(5);
            return false;
        }
        showEditDoneProgress(true, true);
        try {
            if ("stripe".equals(this.paymentForm.native_provider)) {
                new Stripe(this.providerApiKey).createToken(card, new AnonymousClass25());
            } else if ("smartglocal".equals(this.paymentForm.native_provider)) {
                new AsyncTask() {
                    @Override
                    public java.lang.String doInBackground(java.lang.Object... r13) {
                        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.PaymentFormActivity.AnonymousClass26.doInBackground(java.lang.Object[]):java.lang.String");
                    }

                    @Override
                    public void onPostExecute(String str) {
                        if (PaymentFormActivity.this.canceled) {
                            return;
                        }
                        if (str == null) {
                            AlertsCreator.showSimpleToast(PaymentFormActivity.this, LocaleController.getString(R.string.PaymentConnectionFailed));
                        } else {
                            PaymentFormActivity.this.paymentJson = str;
                            PaymentFormActivity.this.goToNextStep();
                        }
                        PaymentFormActivity.this.showEditDoneProgress(true, false);
                        PaymentFormActivity.this.setDonePressed(false);
                    }
                }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, null, null, null);
            }
        } catch (Exception e) {
            FileLog.e(e);
        }
        return true;
    }

    private void sendData() {
        String str;
        if (this.canceled) {
            return;
        }
        showEditDoneProgress(false, true);
        final TLRPC.TL_payments_sendPaymentForm tL_payments_sendPaymentForm = new TLRPC.TL_payments_sendPaymentForm();
        TLRPC.InputInvoice inputInvoice = this.invoiceInput;
        TLRPC.InputInvoice inputInvoice2 = inputInvoice;
        if (inputInvoice == null) {
            if (this.messageObject != null) {
                TLRPC.TL_inputInvoiceMessage tL_inputInvoiceMessage = new TLRPC.TL_inputInvoiceMessage();
                tL_inputInvoiceMessage.peer = getMessagesController().getInputPeer(this.messageObject.messageOwner.peer_id);
                tL_inputInvoiceMessage.msg_id = this.messageObject.getId();
                inputInvoice2 = tL_inputInvoiceMessage;
            } else {
                TLRPC.TL_inputInvoiceSlug tL_inputInvoiceSlug = new TLRPC.TL_inputInvoiceSlug();
                tL_inputInvoiceSlug.slug = this.invoiceSlug;
                inputInvoice2 = tL_inputInvoiceSlug;
            }
        }
        tL_payments_sendPaymentForm.invoice = inputInvoice2;
        tL_payments_sendPaymentForm.form_id = this.paymentForm.form_id;
        if (UserConfig.getInstance(this.currentAccount).tmpPassword == null || this.savedCredentialsCard == null) {
            TLRPC.TL_inputPaymentCredentialsGooglePay tL_inputPaymentCredentialsGooglePay = this.googlePayCredentials;
            if (tL_inputPaymentCredentialsGooglePay != null) {
                tL_payments_sendPaymentForm.credentials = tL_inputPaymentCredentialsGooglePay;
            } else {
                TLRPC.TL_inputPaymentCredentials tL_inputPaymentCredentials = new TLRPC.TL_inputPaymentCredentials();
                tL_payments_sendPaymentForm.credentials = tL_inputPaymentCredentials;
                tL_inputPaymentCredentials.save = this.saveCardInfo;
                tL_inputPaymentCredentials.data = new TLRPC.TL_dataJSON();
                tL_payments_sendPaymentForm.credentials.data.data = this.paymentJson;
            }
        } else {
            TLRPC.TL_inputPaymentCredentialsSaved tL_inputPaymentCredentialsSaved = new TLRPC.TL_inputPaymentCredentialsSaved();
            tL_payments_sendPaymentForm.credentials = tL_inputPaymentCredentialsSaved;
            tL_inputPaymentCredentialsSaved.id = this.savedCredentialsCard.id;
            tL_inputPaymentCredentialsSaved.tmp_password = UserConfig.getInstance(this.currentAccount).tmpPassword.tmp_password;
        }
        TLRPC.TL_payments_validatedRequestedInfo tL_payments_validatedRequestedInfo = this.requestedInfo;
        if (tL_payments_validatedRequestedInfo != null && (str = tL_payments_validatedRequestedInfo.id) != null) {
            tL_payments_sendPaymentForm.requested_info_id = str;
            tL_payments_sendPaymentForm.flags = 1 | tL_payments_sendPaymentForm.flags;
        }
        TLRPC.TL_shippingOption tL_shippingOption = this.shippingOption;
        if (tL_shippingOption != null) {
            tL_payments_sendPaymentForm.shipping_option_id = tL_shippingOption.id;
            tL_payments_sendPaymentForm.flags |= 2;
        }
        if ((this.paymentForm.invoice.flags & 256) != 0) {
            Long l = this.tipAmount;
            tL_payments_sendPaymentForm.tip_amount = l != null ? l.longValue() : 0L;
            tL_payments_sendPaymentForm.flags |= 4;
        }
        ConnectionsManager.getInstance(this.currentAccount).sendRequest(tL_payments_sendPaymentForm, new RequestDelegate() {
            @Override
            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                PaymentFormActivity.this.lambda$sendData$69(tL_payments_sendPaymentForm, tLObject, tL_error);
            }
        }, 2);
    }

    public void sendForm() {
        TLRPC.TL_inputInvoiceSlug tL_inputInvoiceSlug;
        if (this.canceled) {
            return;
        }
        showEditDoneProgress(true, true);
        TLRPC.TL_payments_validateRequestedInfo tL_payments_validateRequestedInfo = new TLRPC.TL_payments_validateRequestedInfo();
        this.validateRequest = tL_payments_validateRequestedInfo;
        TLRPC.InputInvoice inputInvoice = this.invoiceInput;
        if (inputInvoice != null) {
            tL_payments_validateRequestedInfo.invoice = inputInvoice;
        } else {
            if (this.messageObject != null) {
                TLRPC.TL_inputInvoiceMessage tL_inputInvoiceMessage = new TLRPC.TL_inputInvoiceMessage();
                tL_inputInvoiceMessage.peer = getMessagesController().getInputPeer(this.messageObject.messageOwner.peer_id);
                tL_inputInvoiceMessage.msg_id = this.messageObject.getId();
                tL_inputInvoiceSlug = tL_inputInvoiceMessage;
            } else {
                TLRPC.TL_inputInvoiceSlug tL_inputInvoiceSlug2 = new TLRPC.TL_inputInvoiceSlug();
                tL_inputInvoiceSlug2.slug = this.invoiceSlug;
                tL_inputInvoiceSlug = tL_inputInvoiceSlug2;
            }
            this.validateRequest.invoice = tL_inputInvoiceSlug;
        }
        TLRPC.TL_payments_validateRequestedInfo tL_payments_validateRequestedInfo2 = this.validateRequest;
        tL_payments_validateRequestedInfo2.save = this.saveShippingInfo;
        tL_payments_validateRequestedInfo2.info = new TLRPC.TL_paymentRequestedInfo();
        if (this.paymentForm.invoice.name_requested) {
            this.validateRequest.info.name = this.inputFields[6].getText().toString();
            this.validateRequest.info.flags |= 1;
        }
        if (this.paymentForm.invoice.phone_requested) {
            this.validateRequest.info.phone = "+" + this.inputFields[8].getText().toString() + this.inputFields[9].getText().toString();
            TLRPC.TL_paymentRequestedInfo tL_paymentRequestedInfo = this.validateRequest.info;
            tL_paymentRequestedInfo.flags = tL_paymentRequestedInfo.flags | 2;
        }
        if (this.paymentForm.invoice.email_requested) {
            this.validateRequest.info.email = this.inputFields[7].getText().toString().trim();
            this.validateRequest.info.flags |= 4;
        }
        if (this.paymentForm.invoice.shipping_address_requested) {
            this.validateRequest.info.shipping_address = new TLRPC.TL_postAddress();
            this.validateRequest.info.shipping_address.street_line1 = this.inputFields[0].getText().toString();
            this.validateRequest.info.shipping_address.street_line2 = this.inputFields[1].getText().toString();
            this.validateRequest.info.shipping_address.city = this.inputFields[2].getText().toString();
            this.validateRequest.info.shipping_address.state = this.inputFields[3].getText().toString();
            TLRPC.TL_postAddress tL_postAddress = this.validateRequest.info.shipping_address;
            String str = this.countryName;
            if (str == null) {
                str = "";
            }
            tL_postAddress.country_iso2 = str;
            tL_postAddress.post_code = this.inputFields[5].getText().toString();
            this.validateRequest.info.flags |= 8;
        }
        final TLRPC.TL_payments_validateRequestedInfo tL_payments_validateRequestedInfo3 = this.validateRequest;
        ConnectionsManager.getInstance(this.currentAccount).sendRequest(this.validateRequest, new RequestDelegate() {
            @Override
            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                PaymentFormActivity.this.lambda$sendForm$55(tL_payments_validateRequestedInfo3, tLObject, tL_error);
            }
        }, 2);
    }

    public void sendSavePassword(final boolean z) {
        final String str;
        final String str2;
        if (!z && this.codeFieldCell.getVisibility() == 0) {
            String text = this.codeFieldCell.getText();
            if (text.length() == 0) {
                shakeView(this.codeFieldCell);
                return;
            }
            showEditDoneProgress(true, true);
            TL_account.confirmPasswordEmail confirmpasswordemail = new TL_account.confirmPasswordEmail();
            confirmpasswordemail.code = text;
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(confirmpasswordemail, new RequestDelegate() {
                @Override
                public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                    PaymentFormActivity.this.lambda$sendSavePassword$42(tLObject, tL_error);
                }
            }, 10);
            return;
        }
        final TL_account.updatePasswordSettings updatepasswordsettings = new TL_account.updatePasswordSettings();
        if (z) {
            this.doneItem.setVisibility(0);
            TL_account.passwordInputSettings passwordinputsettings = new TL_account.passwordInputSettings();
            updatepasswordsettings.new_settings = passwordinputsettings;
            passwordinputsettings.flags = 2;
            passwordinputsettings.email = "";
            updatepasswordsettings.password = new TLRPC.TL_inputCheckPasswordEmpty();
            str = null;
            str2 = null;
        } else {
            String obj = this.inputFields[0].getText().toString();
            if (TextUtils.isEmpty(obj)) {
                shakeField(0);
                return;
            }
            if (!obj.equals(this.inputFields[1].getText().toString())) {
                try {
                    Toast.makeText(getParentActivity(), LocaleController.getString(R.string.PasswordDoNotMatch), 0).show();
                } catch (Exception e) {
                    FileLog.e(e);
                }
                shakeField(1);
                return;
            }
            String obj2 = this.inputFields[2].getText().toString();
            if (obj2.length() < 3) {
                shakeField(2);
                return;
            }
            int lastIndexOf = obj2.lastIndexOf(46);
            int lastIndexOf2 = obj2.lastIndexOf(64);
            if (lastIndexOf2 < 0 || lastIndexOf < lastIndexOf2) {
                shakeField(2);
                return;
            }
            updatepasswordsettings.password = new TLRPC.TL_inputCheckPasswordEmpty();
            TL_account.passwordInputSettings passwordinputsettings2 = new TL_account.passwordInputSettings();
            updatepasswordsettings.new_settings = passwordinputsettings2;
            int i = passwordinputsettings2.flags;
            passwordinputsettings2.flags = i | 1;
            passwordinputsettings2.hint = "";
            passwordinputsettings2.new_algo = this.currentPassword.new_algo;
            passwordinputsettings2.flags = i | 3;
            passwordinputsettings2.email = obj2.trim();
            str = obj2;
            str2 = obj;
        }
        showEditDoneProgress(true, true);
        Utilities.globalQueue.postRunnable(new Runnable() {
            @Override
            public final void run() {
                PaymentFormActivity.this.lambda$sendSavePassword$48(z, str, str2, updatepasswordsettings);
            }
        });
    }

    private void sendSavedForm(final Runnable runnable) {
        TLRPC.TL_inputInvoiceSlug tL_inputInvoiceSlug;
        if (this.canceled) {
            return;
        }
        showEditDoneProgress(true, true);
        TLRPC.TL_payments_validateRequestedInfo tL_payments_validateRequestedInfo = new TLRPC.TL_payments_validateRequestedInfo();
        this.validateRequest = tL_payments_validateRequestedInfo;
        TLRPC.InputInvoice inputInvoice = this.invoiceInput;
        if (inputInvoice != null) {
            tL_payments_validateRequestedInfo.invoice = inputInvoice;
        } else {
            if (this.messageObject != null) {
                TLRPC.TL_inputInvoiceMessage tL_inputInvoiceMessage = new TLRPC.TL_inputInvoiceMessage();
                tL_inputInvoiceMessage.peer = getMessagesController().getInputPeer(this.messageObject.messageOwner.peer_id);
                tL_inputInvoiceMessage.msg_id = this.messageObject.getId();
                tL_inputInvoiceSlug = tL_inputInvoiceMessage;
            } else {
                TLRPC.TL_inputInvoiceSlug tL_inputInvoiceSlug2 = new TLRPC.TL_inputInvoiceSlug();
                tL_inputInvoiceSlug2.slug = this.invoiceSlug;
                tL_inputInvoiceSlug = tL_inputInvoiceSlug2;
            }
            this.validateRequest.invoice = tL_inputInvoiceSlug;
        }
        final TLRPC.TL_payments_validateRequestedInfo tL_payments_validateRequestedInfo2 = this.validateRequest;
        tL_payments_validateRequestedInfo2.save = true;
        tL_payments_validateRequestedInfo2.info = this.paymentForm.saved_info;
        ConnectionsManager.getInstance(this.currentAccount).sendRequest(tL_payments_validateRequestedInfo2, new RequestDelegate() {
            @Override
            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                PaymentFormActivity.this.lambda$sendSavedForm$51(runnable, tL_payments_validateRequestedInfo2, tLObject, tL_error);
            }
        }, 2);
    }

    public void setAddressFields(TLRPC.TL_paymentRequestedInfo tL_paymentRequestedInfo) {
        TLRPC.TL_postAddress tL_postAddress = tL_paymentRequestedInfo.shipping_address;
        if (tL_postAddress != null) {
            this.detailSettingsCell[2].setTextAndValueAndIcon(String.format("%s %s, %s, %s, %s, %s", tL_postAddress.street_line1, tL_postAddress.street_line2, tL_postAddress.city, tL_postAddress.state, tL_postAddress.country_iso2, tL_postAddress.post_code), LocaleController.getString(R.string.PaymentShippingAddress), R.drawable.msg_payment_address, true);
        }
        this.detailSettingsCell[2].setVisibility(tL_paymentRequestedInfo.shipping_address != null ? 0 : 8);
        String str = tL_paymentRequestedInfo.name;
        if (str != null) {
            this.detailSettingsCell[3].setTextAndValueAndIcon(str, LocaleController.getString(R.string.PaymentCheckoutName), R.drawable.msg_contacts, true);
        }
        this.detailSettingsCell[3].setVisibility(tL_paymentRequestedInfo.name != null ? 0 : 8);
        if (tL_paymentRequestedInfo.phone != null) {
            this.detailSettingsCell[4].setTextAndValueAndIcon(PhoneFormat.getInstance().format(tL_paymentRequestedInfo.phone), LocaleController.getString(R.string.PaymentCheckoutPhoneNumber), R.drawable.msg_calls, (tL_paymentRequestedInfo.email == null && this.shippingOption == null) ? false : true);
        }
        this.detailSettingsCell[4].setVisibility(tL_paymentRequestedInfo.phone != null ? 0 : 8);
        String str2 = tL_paymentRequestedInfo.email;
        if (str2 != null) {
            this.detailSettingsCell[5].setTextAndValueAndIcon(str2, LocaleController.getString(R.string.PaymentCheckoutEmail), R.drawable.msg_mention, this.shippingOption != null);
        }
        this.detailSettingsCell[5].setVisibility(tL_paymentRequestedInfo.email == null ? 8 : 0);
    }

    private void setCurrentPassword(TL_account.Password password) {
        if (password == null || !password.has_password) {
            this.currentPassword = password;
            this.waitingForEmail = (password == null || TextUtils.isEmpty(password.email_unconfirmed_pattern)) ? false : true;
            updatePasswordFields();
        } else {
            if (getParentActivity() == null) {
                return;
            }
            goToNextStep();
        }
    }

    private void setDelegate(PaymentFormActivityDelegate paymentFormActivityDelegate) {
        this.delegate = paymentFormActivityDelegate;
    }

    public void setDonePressed(boolean z) {
        this.donePressed = z;
        this.swipeBackEnabled = !z;
        ActionBar actionBar = this.actionBar;
        if (actionBar != null && actionBar.getBackButton() != null) {
            this.actionBar.getBackButton().setEnabled(!this.donePressed);
        }
        TextDetailSettingsCell textDetailSettingsCell = this.detailSettingsCell[0];
        if (textDetailSettingsCell != null) {
            textDetailSettingsCell.setEnabled(!this.donePressed);
        }
    }

    private void setPaymentMethod(TLRPC.TL_paymentFormMethod tL_paymentFormMethod) {
        this.paymentFormMethod = tL_paymentFormMethod;
    }

    private void shakeField(int i) {
        shakeView(this.inputFields[i]);
    }

    private void shakeView(View view) {
        try {
            view.performHapticFeedback(3, 2);
        } catch (Exception unused) {
        }
        AndroidUtilities.shakeViewSpring(view, 2.5f);
    }

    private void showAlertWithText(String str, String str2) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getParentActivity());
        builder.setPositiveButton(LocaleController.getString(R.string.OK), null);
        builder.setTitle(str);
        builder.setMessage(str2);
        showDialog(builder.create());
    }

    private void showChoosePaymentMethod() {
        showChoosePaymentMethod(null);
    }

    private void showChoosePaymentMethod(final java.lang.Runnable r9) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.PaymentFormActivity.showChoosePaymentMethod(java.lang.Runnable):void");
    }

    public void showEditDoneProgress(boolean z, final boolean z2) {
        AnimatorSet animatorSet;
        AnimatorListenerAdapter animatorListenerAdapter;
        AnimatorSet animatorSet2 = this.doneItemAnimation;
        if (animatorSet2 != null) {
            animatorSet2.cancel();
        }
        if (z && this.doneItem != null) {
            AnimatorSet animatorSet3 = new AnimatorSet();
            this.doneItemAnimation = animatorSet3;
            if (z2) {
                this.progressView.setVisibility(0);
                this.doneItem.setEnabled(false);
                AnimatorSet animatorSet4 = this.doneItemAnimation;
                View contentView = this.doneItem.getContentView();
                Property property = View.SCALE_X;
                ObjectAnimator ofFloat = ObjectAnimator.ofFloat(contentView, (Property<View, Float>) property, 0.1f);
                View contentView2 = this.doneItem.getContentView();
                Property property2 = View.SCALE_Y;
                ObjectAnimator ofFloat2 = ObjectAnimator.ofFloat(contentView2, (Property<View, Float>) property2, 0.1f);
                View contentView3 = this.doneItem.getContentView();
                Property property3 = View.ALPHA;
                animatorSet4.playTogether(ofFloat, ofFloat2, ObjectAnimator.ofFloat(contentView3, (Property<View, Float>) property3, 0.0f), ObjectAnimator.ofFloat(this.progressView, (Property<ContextProgressView, Float>) property, 1.0f), ObjectAnimator.ofFloat(this.progressView, (Property<ContextProgressView, Float>) property2, 1.0f), ObjectAnimator.ofFloat(this.progressView, (Property<ContextProgressView, Float>) property3, 1.0f));
            } else if (this.webView != null) {
                animatorSet3.playTogether(ObjectAnimator.ofFloat(this.progressView, (Property<ContextProgressView, Float>) View.SCALE_X, 0.1f), ObjectAnimator.ofFloat(this.progressView, (Property<ContextProgressView, Float>) View.SCALE_Y, 0.1f), ObjectAnimator.ofFloat(this.progressView, (Property<ContextProgressView, Float>) View.ALPHA, 0.0f));
            } else {
                this.doneItem.getContentView().setVisibility(0);
                this.doneItem.setEnabled(true);
                AnimatorSet animatorSet5 = this.doneItemAnimation;
                ContextProgressView contextProgressView = this.progressView;
                Property property4 = View.SCALE_X;
                ObjectAnimator ofFloat3 = ObjectAnimator.ofFloat(contextProgressView, (Property<ContextProgressView, Float>) property4, 0.1f);
                ContextProgressView contextProgressView2 = this.progressView;
                Property property5 = View.SCALE_Y;
                ObjectAnimator ofFloat4 = ObjectAnimator.ofFloat(contextProgressView2, (Property<ContextProgressView, Float>) property5, 0.1f);
                ContextProgressView contextProgressView3 = this.progressView;
                Property property6 = View.ALPHA;
                animatorSet5.playTogether(ofFloat3, ofFloat4, ObjectAnimator.ofFloat(contextProgressView3, (Property<ContextProgressView, Float>) property6, 0.0f));
                if (!isFinishing()) {
                    this.doneItemAnimation.playTogether(ObjectAnimator.ofFloat(this.doneItem.getContentView(), (Property<View, Float>) property4, 1.0f), ObjectAnimator.ofFloat(this.doneItem.getContentView(), (Property<View, Float>) property5, 1.0f), ObjectAnimator.ofFloat(this.doneItem.getContentView(), (Property<View, Float>) property6, 1.0f));
                }
            }
            animatorSet = this.doneItemAnimation;
            animatorListenerAdapter = new AnimatorListenerAdapter() {
                @Override
                public void onAnimationCancel(Animator animator) {
                    if (PaymentFormActivity.this.doneItemAnimation == null || !PaymentFormActivity.this.doneItemAnimation.equals(animator)) {
                        return;
                    }
                    PaymentFormActivity.this.doneItemAnimation = null;
                }

                @Override
                public void onAnimationEnd(Animator animator) {
                    if (PaymentFormActivity.this.doneItemAnimation == null || !PaymentFormActivity.this.doneItemAnimation.equals(animator)) {
                        return;
                    }
                    if (z2) {
                        PaymentFormActivity.this.doneItem.getContentView().setVisibility(4);
                    } else {
                        PaymentFormActivity.this.progressView.setVisibility(4);
                    }
                }
            };
        } else {
            if (this.payTextView == null) {
                return;
            }
            this.doneItemAnimation = new AnimatorSet();
            if (z2) {
                this.progressViewButton.setVisibility(0);
                this.bottomLayout.setEnabled(false);
                AnimatorSet animatorSet6 = this.doneItemAnimation;
                TextView textView = this.payTextView;
                Property property7 = View.SCALE_X;
                ObjectAnimator ofFloat5 = ObjectAnimator.ofFloat(textView, (Property<TextView, Float>) property7, 0.1f);
                TextView textView2 = this.payTextView;
                Property property8 = View.SCALE_Y;
                ObjectAnimator ofFloat6 = ObjectAnimator.ofFloat(textView2, (Property<TextView, Float>) property8, 0.1f);
                TextView textView3 = this.payTextView;
                Property property9 = View.ALPHA;
                animatorSet6.playTogether(ofFloat5, ofFloat6, ObjectAnimator.ofFloat(textView3, (Property<TextView, Float>) property9, 0.0f), ObjectAnimator.ofFloat(this.progressViewButton, (Property<ContextProgressView, Float>) property7, 1.0f), ObjectAnimator.ofFloat(this.progressViewButton, (Property<ContextProgressView, Float>) property8, 1.0f), ObjectAnimator.ofFloat(this.progressViewButton, (Property<ContextProgressView, Float>) property9, 1.0f));
            } else {
                this.payTextView.setVisibility(0);
                this.bottomLayout.setEnabled(true);
                AnimatorSet animatorSet7 = this.doneItemAnimation;
                ContextProgressView contextProgressView4 = this.progressViewButton;
                Property property10 = View.SCALE_X;
                ObjectAnimator ofFloat7 = ObjectAnimator.ofFloat(contextProgressView4, (Property<ContextProgressView, Float>) property10, 0.1f);
                ContextProgressView contextProgressView5 = this.progressViewButton;
                Property property11 = View.SCALE_Y;
                ObjectAnimator ofFloat8 = ObjectAnimator.ofFloat(contextProgressView5, (Property<ContextProgressView, Float>) property11, 0.1f);
                ContextProgressView contextProgressView6 = this.progressViewButton;
                Property property12 = View.ALPHA;
                animatorSet7.playTogether(ofFloat7, ofFloat8, ObjectAnimator.ofFloat(contextProgressView6, (Property<ContextProgressView, Float>) property12, 0.0f), ObjectAnimator.ofFloat(this.payTextView, (Property<TextView, Float>) property10, 1.0f), ObjectAnimator.ofFloat(this.payTextView, (Property<TextView, Float>) property11, 1.0f), ObjectAnimator.ofFloat(this.payTextView, (Property<TextView, Float>) property12, 1.0f));
            }
            animatorSet = this.doneItemAnimation;
            animatorListenerAdapter = new AnimatorListenerAdapter() {
                @Override
                public void onAnimationCancel(Animator animator) {
                    if (PaymentFormActivity.this.doneItemAnimation == null || !PaymentFormActivity.this.doneItemAnimation.equals(animator)) {
                        return;
                    }
                    PaymentFormActivity.this.doneItemAnimation = null;
                }

                @Override
                public void onAnimationEnd(Animator animator) {
                    if (PaymentFormActivity.this.doneItemAnimation == null || !PaymentFormActivity.this.doneItemAnimation.equals(animator)) {
                        return;
                    }
                    if (z2) {
                        PaymentFormActivity.this.payTextView.setVisibility(4);
                    } else {
                        PaymentFormActivity.this.progressViewButton.setVisibility(4);
                    }
                }
            };
        }
        animatorSet.addListener(animatorListenerAdapter);
        this.doneItemAnimation.setDuration(150L);
        this.doneItemAnimation.start();
    }

    private void showPayAlert(String str) {
        if (getParentActivity() == null) {
            return;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(getParentActivity());
        builder.setTitle(LocaleController.getString(R.string.PaymentTransactionReview));
        builder.setMessage(AndroidUtilities.replaceTags(LocaleController.formatString("PaymentTransactionMessage2", R.string.PaymentTransactionMessage2, str, this.currentBotName, this.currentItemName)));
        builder.setPositiveButton(LocaleController.getString(R.string.Continue), new AlertDialog.OnButtonClickListener() {
            @Override
            public final void onClick(AlertDialog alertDialog, int i) {
                PaymentFormActivity.this.lambda$showPayAlert$36(alertDialog, i);
            }
        });
        builder.setNegativeButton(LocaleController.getString(R.string.Cancel), null);
        showDialog(builder.create());
    }

    private void updatePasswordFields() {
        int i = 0;
        if (this.currentStep != 6 || this.bottomCell[2] == null) {
            return;
        }
        this.doneItem.setVisibility(0);
        if (this.currentPassword == null) {
            showEditDoneProgress(true, true);
            this.bottomCell[2].setVisibility(8);
            this.settingsCell[0].setVisibility(8);
            this.settingsCell[1].setVisibility(8);
            this.codeFieldCell.setVisibility(8);
            this.headerCell[0].setVisibility(8);
            this.headerCell[1].setVisibility(8);
            this.bottomCell[0].setVisibility(8);
            for (int i2 = 0; i2 < 3; i2++) {
                ((View) this.inputFields[i2].getParent()).setVisibility(8);
            }
            while (i < this.dividers.size()) {
                ((View) this.dividers.get(i)).setVisibility(8);
                i++;
            }
            return;
        }
        showEditDoneProgress(true, false);
        if (!this.waitingForEmail) {
            this.bottomCell[2].setVisibility(8);
            this.settingsCell[0].setVisibility(8);
            this.settingsCell[1].setVisibility(8);
            this.bottomCell[1].setText(LocaleController.getString(R.string.PaymentPasswordEmailInfo));
            this.codeFieldCell.setVisibility(8);
            this.headerCell[0].setVisibility(0);
            this.headerCell[1].setVisibility(0);
            this.bottomCell[0].setVisibility(0);
            for (int i3 = 0; i3 < 3; i3++) {
                ((View) this.inputFields[i3].getParent()).setVisibility(0);
            }
            for (int i4 = 0; i4 < this.dividers.size(); i4++) {
                ((View) this.dividers.get(i4)).setVisibility(0);
            }
            return;
        }
        TextInfoPrivacyCell textInfoPrivacyCell = this.bottomCell[2];
        int i5 = R.string.EmailPasswordConfirmText2;
        String str = this.currentPassword.email_unconfirmed_pattern;
        if (str == null) {
            str = "";
        }
        textInfoPrivacyCell.setText(LocaleController.formatString("EmailPasswordConfirmText2", i5, str));
        this.bottomCell[2].setVisibility(0);
        this.settingsCell[0].setVisibility(0);
        this.settingsCell[1].setVisibility(0);
        this.codeFieldCell.setVisibility(0);
        this.bottomCell[1].setText("");
        this.headerCell[0].setVisibility(8);
        this.headerCell[1].setVisibility(8);
        this.bottomCell[0].setVisibility(8);
        for (int i6 = 0; i6 < 3; i6++) {
            ((View) this.inputFields[i6].getParent()).setVisibility(8);
        }
        while (i < this.dividers.size()) {
            ((View) this.dividers.get(i)).setVisibility(8);
            i++;
        }
    }

    public void updateSavePaymentField() {
        ShadowSectionCell shadowSectionCell;
        Context context;
        int i;
        if (this.bottomCell[0] == null || this.sectionCell[2] == null) {
            return;
        }
        TLRPC.PaymentForm paymentForm = this.paymentForm;
        if ((paymentForm.password_missing || paymentForm.can_save_credentials) && (this.webView == null || !this.webviewLoading)) {
            SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(LocaleController.getString(R.string.PaymentCardSavePaymentInformationInfoLine1));
            if (this.paymentForm.password_missing) {
                loadPasswordInfo();
                spannableStringBuilder.append((CharSequence) "\n");
                int length = spannableStringBuilder.length();
                String string = LocaleController.getString(R.string.PaymentCardSavePaymentInformationInfoLine2);
                int indexOf = string.indexOf(42);
                int lastIndexOf = string.lastIndexOf(42);
                spannableStringBuilder.append((CharSequence) string);
                if (indexOf != -1 && lastIndexOf != -1) {
                    int i2 = indexOf + length;
                    int i3 = lastIndexOf + length;
                    this.bottomCell[0].getTextView().setMovementMethod(new AndroidUtilities.LinkMovementMethodMy());
                    spannableStringBuilder.replace(i3, i3 + 1, (CharSequence) "");
                    spannableStringBuilder.replace(i2, i2 + 1, (CharSequence) "");
                    spannableStringBuilder.setSpan(new LinkSpan(), i2, i3 - 1, 33);
                }
            }
            this.checkCell1.setEnabled(true);
            this.bottomCell[0].setText(spannableStringBuilder);
            this.checkCell1.setVisibility(0);
            this.bottomCell[0].setVisibility(0);
            shadowSectionCell = this.sectionCell[2];
            context = shadowSectionCell.getContext();
            i = R.drawable.greydivider;
        } else {
            this.checkCell1.setVisibility(8);
            this.bottomCell[0].setVisibility(8);
            shadowSectionCell = this.sectionCell[2];
            context = shadowSectionCell.getContext();
            i = R.drawable.greydivider_bottom;
        }
        shadowSectionCell.setBackgroundDrawable(Theme.getThemedDrawableByKey(context, i, Theme.key_windowBackgroundGrayShadow));
    }

    public void updateTotalPrice() {
        int i;
        this.totalPrice[0] = getTotalPriceString(this.prices);
        this.totalCell.setTextAndValue(LocaleController.getString(R.string.PaymentTransactionTotal), this.totalPrice[0], true);
        TextView textView = this.payTextView;
        if (textView != null) {
            textView.setText(LocaleController.formatString("PaymentCheckoutPay", R.string.PaymentCheckoutPay, this.totalPrice[0]));
        }
        if (this.tipLayout != null) {
            int themedColor = getThemedColor(Theme.key_contacts_inviteBackground);
            int childCount = this.tipLayout.getChildCount();
            for (int i2 = 0; i2 < childCount; i2++) {
                TextView textView2 = (TextView) this.tipLayout.getChildAt(i2);
                if (textView2.getTag().equals(this.tipAmount)) {
                    Theme.setDrawableColor(textView2.getBackground(), themedColor);
                    i = Theme.key_contacts_inviteText;
                } else {
                    Theme.setDrawableColor(textView2.getBackground(), 536870911 & themedColor);
                    i = Theme.key_chats_secretName;
                }
                textView2.setTextColor(getThemedColor(i));
                textView2.invalidate();
            }
        }
    }

    @Override
    public android.view.View createView(android.content.Context r42) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.PaymentFormActivity.createView(android.content.Context):android.view.View");
    }

    @Override
    public void didReceivedNotification(int i, int i2, Object... objArr) {
        if (i == NotificationCenter.twoStepPasswordChanged) {
            TLRPC.PaymentForm paymentForm = this.paymentForm;
            paymentForm.password_missing = false;
            paymentForm.can_save_credentials = true;
        } else {
            if (i != NotificationCenter.didRemoveTwoStepPassword) {
                if (i == NotificationCenter.paymentFinished) {
                    this.paymentStatusSent = true;
                    removeSelfFromStack();
                    return;
                }
                return;
            }
            TLRPC.PaymentForm paymentForm2 = this.paymentForm;
            paymentForm2.password_missing = true;
            paymentForm2.can_save_credentials = false;
        }
        updateSavePaymentField();
    }

    public void fillNumber(java.lang.String r8) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.PaymentFormActivity.fillNumber(java.lang.String):void");
    }

    public Optional getIsReadyToPayRequest() {
        try {
            JSONObject baseRequest = getBaseRequest();
            baseRequest.put("allowedPaymentMethods", new JSONArray().put(getBaseCardPaymentMethod()));
            return Optional.of(baseRequest);
        } catch (JSONException unused) {
            return Optional.empty();
        }
    }

    public int getOtherSameFragmentDiff() {
        INavigationLayout iNavigationLayout = this.parentLayout;
        int i = 0;
        if (iNavigationLayout == null || iNavigationLayout.getFragmentStack() == null) {
            return 0;
        }
        int indexOf = this.parentLayout.getFragmentStack().indexOf(this);
        if (indexOf == -1) {
            indexOf = this.parentLayout.getFragmentStack().size();
        }
        while (true) {
            if (i >= this.parentLayout.getFragmentStack().size()) {
                i = indexOf;
                break;
            }
            if (((BaseFragment) this.parentLayout.getFragmentStack().get(i)) instanceof PaymentFormActivity) {
                break;
            }
            i++;
        }
        return i - indexOf;
    }

    @Override
    public Theme.ResourcesProvider getResourceProvider() {
        return this.resourcesProvider;
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
        ContextProgressView contextProgressView = this.progressView;
        int i3 = Theme.key_contextProgressInner2;
        arrayList.add(new ThemeDescription(contextProgressView, 0, null, null, null, null, i3));
        ContextProgressView contextProgressView2 = this.progressView;
        int i4 = Theme.key_contextProgressOuter2;
        arrayList.add(new ThemeDescription(contextProgressView2, 0, null, null, null, null, i4));
        arrayList.add(new ThemeDescription(this.progressViewButton, 0, null, null, null, null, i3));
        arrayList.add(new ThemeDescription(this.progressViewButton, 0, null, null, null, null, i4));
        if (this.inputFields != null) {
            for (int i5 = 0; i5 < this.inputFields.length; i5++) {
                arrayList.add(new ThemeDescription((View) this.inputFields[i5].getParent(), ThemeDescription.FLAG_BACKGROUND, null, null, null, null, Theme.key_windowBackgroundWhite));
                arrayList.add(new ThemeDescription(this.inputFields[i5], ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, Theme.key_windowBackgroundWhiteBlackText));
                arrayList.add(new ThemeDescription(this.inputFields[i5], ThemeDescription.FLAG_HINTTEXTCOLOR, null, null, null, null, Theme.key_windowBackgroundWhiteHintText));
            }
        } else {
            arrayList.add(new ThemeDescription(null, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, Theme.key_windowBackgroundWhiteBlackText));
            arrayList.add(new ThemeDescription(null, ThemeDescription.FLAG_HINTTEXTCOLOR, null, null, null, null, Theme.key_windowBackgroundWhiteHintText));
        }
        if (this.radioCells != null) {
            for (int i6 = 0; i6 < this.radioCells.length; i6++) {
                arrayList.add(new ThemeDescription(this.radioCells[i6], ThemeDescription.FLAG_SELECTORWHITE, null, null, null, null, Theme.key_windowBackgroundWhite));
                arrayList.add(new ThemeDescription(this.radioCells[i6], ThemeDescription.FLAG_SELECTORWHITE, null, null, null, null, Theme.key_listSelector));
                arrayList.add(new ThemeDescription(this.radioCells[i6], 0, new Class[]{RadioCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteBlackText));
                arrayList.add(new ThemeDescription(this.radioCells[i6], ThemeDescription.FLAG_CHECKBOX, new Class[]{RadioCell.class}, new String[]{"radioButton"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_radioBackground));
                arrayList.add(new ThemeDescription(this.radioCells[i6], ThemeDescription.FLAG_CHECKBOXCHECK, new Class[]{RadioCell.class}, new String[]{"radioButton"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_radioBackgroundChecked));
            }
        } else {
            arrayList.add(new ThemeDescription((View) null, 0, new Class[]{RadioCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteBlackText));
            arrayList.add(new ThemeDescription((View) null, ThemeDescription.FLAG_CHECKBOX, new Class[]{RadioCell.class}, new String[]{"radioButton"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_radioBackground));
            arrayList.add(new ThemeDescription((View) null, ThemeDescription.FLAG_CHECKBOXCHECK, new Class[]{RadioCell.class}, new String[]{"radioButton"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_radioBackgroundChecked));
        }
        for (int i7 = 0; i7 < this.headerCell.length; i7++) {
            arrayList.add(new ThemeDescription(this.headerCell[i7], ThemeDescription.FLAG_BACKGROUND, null, null, null, null, Theme.key_windowBackgroundWhite));
            arrayList.add(new ThemeDescription(this.headerCell[i7], 0, new Class[]{HeaderCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteBlueHeader));
        }
        for (int i8 = 0; i8 < this.sectionCell.length; i8++) {
            arrayList.add(new ThemeDescription(this.sectionCell[i8], ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{ShadowSectionCell.class}, null, null, null, Theme.key_windowBackgroundGrayShadow));
        }
        for (int i9 = 0; i9 < this.bottomCell.length; i9++) {
            arrayList.add(new ThemeDescription(this.bottomCell[i9], ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{TextInfoPrivacyCell.class}, null, null, null, Theme.key_windowBackgroundGrayShadow));
            arrayList.add(new ThemeDescription(this.bottomCell[i9], 0, new Class[]{TextInfoPrivacyCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteGrayText4));
            arrayList.add(new ThemeDescription(this.bottomCell[i9], ThemeDescription.FLAG_LINKCOLOR, new Class[]{TextInfoPrivacyCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteLinkText));
        }
        for (int i10 = 0; i10 < this.dividers.size(); i10++) {
            arrayList.add(new ThemeDescription((View) this.dividers.get(i10), ThemeDescription.FLAG_BACKGROUND, null, null, null, null, Theme.key_windowBackgroundWhite));
        }
        EditTextSettingsCell editTextSettingsCell = this.codeFieldCell;
        int i11 = ThemeDescription.FLAG_BACKGROUND;
        int i12 = Theme.key_windowBackgroundWhite;
        arrayList.add(new ThemeDescription(editTextSettingsCell, i11, null, null, null, null, i12));
        int i13 = Theme.key_windowBackgroundWhiteBlackText;
        arrayList.add(new ThemeDescription(this.codeFieldCell, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{EditTextSettingsCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, i13));
        arrayList.add(new ThemeDescription(this.codeFieldCell, ThemeDescription.FLAG_HINTTEXTCOLOR, new Class[]{EditTextSettingsCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteHintText));
        arrayList.add(new ThemeDescription(this.textView, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, i13));
        arrayList.add(new ThemeDescription(this.checkCell1, 0, new Class[]{TextCheckCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, i13));
        arrayList.add(new ThemeDescription(this.checkCell1, 0, new Class[]{TextCheckCell.class}, new String[]{"checkBox"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_switchTrack));
        arrayList.add(new ThemeDescription(this.checkCell1, 0, new Class[]{TextCheckCell.class}, new String[]{"checkBox"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_switchTrackChecked));
        arrayList.add(new ThemeDescription(this.checkCell1, ThemeDescription.FLAG_SELECTORWHITE, null, null, null, null, i12));
        arrayList.add(new ThemeDescription(this.checkCell1, ThemeDescription.FLAG_SELECTORWHITE, null, null, null, null, Theme.key_listSelector));
        for (int i14 = 0; i14 < this.settingsCell.length; i14++) {
            arrayList.add(new ThemeDescription(this.settingsCell[i14], ThemeDescription.FLAG_SELECTORWHITE, null, null, null, null, Theme.key_windowBackgroundWhite));
            arrayList.add(new ThemeDescription(this.settingsCell[i14], ThemeDescription.FLAG_SELECTORWHITE, null, null, null, null, Theme.key_listSelector));
            arrayList.add(new ThemeDescription(this.settingsCell[i14], 0, new Class[]{TextSettingsCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteBlackText));
        }
        arrayList.add(new ThemeDescription(this.payTextView, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, Theme.key_windowBackgroundWhiteBlueText6));
        int i15 = Theme.key_windowBackgroundWhite;
        arrayList.add(new ThemeDescription(this.linearLayout2, ThemeDescription.FLAG_CELLBACKGROUNDCOLOR, new Class[]{TextPriceCell.class}, null, null, null, i15));
        int i16 = Theme.key_windowBackgroundWhiteBlackText;
        arrayList.add(new ThemeDescription(this.linearLayout2, ThemeDescription.FLAG_CHECKTAG, new Class[]{TextPriceCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, i16));
        arrayList.add(new ThemeDescription(this.linearLayout2, ThemeDescription.FLAG_CHECKTAG, new Class[]{TextPriceCell.class}, new String[]{"valueTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, i16));
        int i17 = Theme.key_windowBackgroundWhiteGrayText2;
        arrayList.add(new ThemeDescription(this.linearLayout2, ThemeDescription.FLAG_CHECKTAG, new Class[]{TextPriceCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, i17));
        arrayList.add(new ThemeDescription(this.linearLayout2, ThemeDescription.FLAG_CHECKTAG, new Class[]{TextPriceCell.class}, new String[]{"valueTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, i17));
        arrayList.add(new ThemeDescription(this.detailSettingsCell[0], ThemeDescription.FLAG_SELECTORWHITE, null, null, null, null, i15));
        arrayList.add(new ThemeDescription(this.detailSettingsCell[0], ThemeDescription.FLAG_SELECTORWHITE, null, null, null, null, Theme.key_listSelector));
        for (int i18 = 1; i18 < this.detailSettingsCell.length; i18++) {
            arrayList.add(new ThemeDescription(this.detailSettingsCell[i18], ThemeDescription.FLAG_BACKGROUND, null, null, null, null, Theme.key_windowBackgroundWhite));
            arrayList.add(new ThemeDescription(this.detailSettingsCell[i18], 0, new Class[]{TextDetailSettingsCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteBlackText));
            arrayList.add(new ThemeDescription(this.detailSettingsCell[i18], 0, new Class[]{TextDetailSettingsCell.class}, new String[]{"valueTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteGrayText2));
        }
        PaymentInfoCell paymentInfoCell = this.paymentInfoCell;
        int i19 = ThemeDescription.FLAG_BACKGROUND;
        int i20 = Theme.key_windowBackgroundWhite;
        arrayList.add(new ThemeDescription(paymentInfoCell, i19, null, null, null, null, i20));
        int i21 = Theme.key_windowBackgroundWhiteBlackText;
        arrayList.add(new ThemeDescription(this.paymentInfoCell, 0, new Class[]{PaymentInfoCell.class}, new String[]{"nameTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, i21));
        arrayList.add(new ThemeDescription(this.paymentInfoCell, 0, new Class[]{PaymentInfoCell.class}, new String[]{"detailTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, i21));
        arrayList.add(new ThemeDescription(this.paymentInfoCell, 0, new Class[]{PaymentInfoCell.class}, new String[]{"detailExTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteGrayText2));
        arrayList.add(new ThemeDescription(this.bottomLayout, ThemeDescription.FLAG_SELECTORWHITE, null, null, null, null, i20));
        arrayList.add(new ThemeDescription(this.bottomLayout, ThemeDescription.FLAG_SELECTORWHITE, null, null, null, null, Theme.key_listSelector));
        return arrayList;
    }

    @Override
    public boolean isSwipeBackEnabled(MotionEvent motionEvent) {
        return this.swipeBackEnabled;
    }

    @Override
    public void onActivityResultFragment(int i, final int i2, final Intent intent) {
        if (i == 991) {
            AndroidUtilities.runOnUIThread(new Runnable() {
                @Override
                public final void run() {
                    PaymentFormActivity.this.lambda$onActivityResultFragment$39(i2, intent);
                }
            });
        }
    }

    @Override
    public boolean onBackPressed() {
        if (!this.shouldNavigateBack) {
            return !this.donePressed;
        }
        this.webView.loadUrl(this.webViewUrl);
        this.shouldNavigateBack = false;
        return false;
    }

    @Override
    public void onBecomeFullyVisible() {
        super.onBecomeFullyVisible();
        if (this.currentStep == 4 && this.needPayAfterTransition) {
            this.needPayAfterTransition = false;
            this.bottomLayout.callOnClick();
        }
    }

    @Override
    public boolean onFragmentCreate() {
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.twoStepPasswordChanged);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.didRemoveTwoStepPassword);
        if (this.currentStep != 4 || this.isCheckoutPreview) {
            NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.paymentFinished);
        }
        return super.onFragmentCreate();
    }

    @Override
    public void onFragmentDestroy() {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.PaymentFormActivity.onFragmentDestroy():void");
    }

    @Override
    public void onResume() {
        super.onResume();
        AndroidUtilities.requestAdjustResize(getParentActivity(), this.classGuid);
        if (Build.VERSION.SDK_INT >= 23) {
            try {
                int i = this.currentStep;
                if ((i == 2 || i == 6) && !this.paymentForm.invoice.test) {
                    getParentActivity().getWindow().setFlags(8192, 8192);
                } else if (SharedConfig.passcodeHash.length() == 0 || SharedConfig.allowScreenCapture) {
                    getParentActivity().getWindow().clearFlags(8192);
                }
            } catch (Throwable th) {
                FileLog.e(th);
            }
        }
    }

    @Override
    public void onTransitionAnimationEnd(boolean z, boolean z2) {
        EditTextBoldCursor editTextBoldCursor;
        if (!z || z2) {
            return;
        }
        WebView webView = this.webView;
        if (webView != null) {
            if (this.currentStep != 4) {
                TLRPC.TL_paymentFormMethod tL_paymentFormMethod = this.paymentFormMethod;
                String str = tL_paymentFormMethod != null ? tL_paymentFormMethod.url : this.paymentForm.url;
                this.webViewUrl = str;
                webView.loadUrl(str);
                return;
            }
            return;
        }
        int i = this.currentStep;
        if (i == 2) {
            AndroidUtilities.runOnUIThread(new Runnable() {
                @Override
                public final void run() {
                    PaymentFormActivity.this.lambda$onTransitionAnimationEnd$38();
                }
            }, 100L);
            return;
        }
        if (i == 3) {
            this.inputFields[1].requestFocus();
            editTextBoldCursor = this.inputFields[1];
        } else {
            if (i == 4) {
                EditTextBoldCursor[] editTextBoldCursorArr = this.inputFields;
                if (editTextBoldCursorArr != null) {
                    editTextBoldCursorArr[0].requestFocus();
                    return;
                }
                return;
            }
            if (i != 6 || this.waitingForEmail) {
                return;
            }
            this.inputFields[0].requestFocus();
            editTextBoldCursor = this.inputFields[0];
        }
        AndroidUtilities.showKeyboard(editTextBoldCursor);
    }

    @Override
    public boolean presentFragment(BaseFragment baseFragment) {
        onPresentFragment(baseFragment);
        return super.presentFragment(baseFragment);
    }

    @Override
    public boolean presentFragment(BaseFragment baseFragment, boolean z) {
        onPresentFragment(baseFragment);
        return super.presentFragment(baseFragment, z);
    }

    public void setPaymentFormCallback(PaymentFormCallback paymentFormCallback) {
        this.paymentFormCallback = paymentFormCallback;
    }

    public void setResourcesProvider(Theme.ResourcesProvider resourcesProvider) {
        this.resourcesProvider = resourcesProvider;
    }
}
