package org.telegram.messenger;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import androidx.core.util.Consumer;
import androidx.core.util.Pair;
import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingClientStateListener;
import com.android.billingclient.api.BillingFlowParams;
import com.android.billingclient.api.BillingResult;
import com.android.billingclient.api.ConsumeParams;
import com.android.billingclient.api.ConsumeResponseListener;
import com.android.billingclient.api.ProductDetails;
import com.android.billingclient.api.ProductDetailsResponseListener;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.PurchasesResponseListener;
import com.android.billingclient.api.PurchasesUpdatedListener;
import com.android.billingclient.api.QueryProductDetailsParams;
import com.android.billingclient.api.QueryPurchasesParams;
import j$.util.Map;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Currency;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import org.telegram.messenger.utils.BillingUtilities;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.PremiumPreviewFragment;

public class BillingController implements PurchasesUpdatedListener, BillingClientStateListener {
    public static final QueryProductDetailsParams.Product PREMIUM_PRODUCT = QueryProductDetailsParams.Product.newBuilder().setProductType("subs").setProductId("telegram_premium").build();
    public static ProductDetails PREMIUM_PRODUCT_DETAILS = null;
    public static final String PREMIUM_PRODUCT_ID = "telegram_premium";
    public static boolean billingClientEmpty;
    private static NumberFormat currencyInstance;
    private static BillingController instance;
    private final BillingClient billingClient;
    private boolean isDisconnected;
    private String lastPremiumToken;
    private String lastPremiumTransaction;
    private Runnable onCanceled;
    private final Map<String, Consumer> resultListeners = new HashMap();
    private final List<String> requestingTokens = Collections.synchronizedList(new ArrayList());
    private final Map<String, Integer> currencyExpMap = new HashMap();
    private ArrayList<Runnable> setupListeners = new ArrayList<>();
    private int triesLeft = 0;

    private BillingController(Context context) {
        this.billingClient = BillingClient.newBuilder(context).enablePendingPurchases().setListener(this).build();
    }

    private void consumeGiftPurchase(Purchase purchase, TLRPC.InputStorePaymentPurpose inputStorePaymentPurpose) {
        if ((inputStorePaymentPurpose instanceof TLRPC.TL_inputStorePaymentGiftPremium) || (inputStorePaymentPurpose instanceof TLRPC.TL_inputStorePaymentPremiumGiftCode) || (inputStorePaymentPurpose instanceof TLRPC.TL_inputStorePaymentStarsTopup) || (inputStorePaymentPurpose instanceof TLRPC.TL_inputStorePaymentStarsGift) || (inputStorePaymentPurpose instanceof TLRPC.TL_inputStorePaymentPremiumGiveaway) || (inputStorePaymentPurpose instanceof TLRPC.TL_inputStorePaymentStarsGiveaway)) {
            this.billingClient.consumeAsync(ConsumeParams.newBuilder().setPurchaseToken(purchase.getPurchaseToken()).build(), new ConsumeResponseListener() {
                @Override
                public final void onConsumeResponse(BillingResult billingResult, String str) {
                    BillingController.lambda$consumeGiftPurchase$5(billingResult, str);
                }
            });
        }
    }

    public static BillingController getInstance() {
        if (instance == null) {
            instance = new BillingController(ApplicationLoader.applicationContext);
        }
        return instance;
    }

    public static String getResponseCodeString(int i) {
        if (i == 12) {
            return "NETWORK_ERROR";
        }
        switch (i) {
            case -3:
                return "SERVICE_TIMEOUT";
            case -2:
                return "FEATURE_NOT_SUPPORTED";
            case -1:
                return "SERVICE_DISCONNECTED";
            case 0:
                return "OK";
            case 1:
                return "USER_CANCELED";
            case 2:
                return "SERVICE_UNAVAILABLE";
            case 3:
                return "BILLING_UNAVAILABLE";
            case 4:
                return "ITEM_UNAVAILABLE";
            case 5:
                return "DEVELOPER_ERROR";
            case 6:
                return "ERROR";
            case 7:
                return "ITEM_ALREADY_OWNED";
            case 8:
                return "ITEM_NOT_OWNED";
            default:
                return null;
        }
    }

    public static void lambda$consumeGiftPurchase$5(BillingResult billingResult, String str) {
    }

    public void lambda$launchBillingFlow$0(Activity activity, AccountInstance accountInstance, TLRPC.InputStorePaymentPurpose inputStorePaymentPurpose, List list, BillingFlowParams.SubscriptionUpdateParams subscriptionUpdateParams) {
        launchBillingFlow(activity, accountInstance, inputStorePaymentPurpose, list, subscriptionUpdateParams, true);
    }

    public static void lambda$launchBillingFlow$1(List list, String str, AtomicInteger atomicInteger, Runnable runnable, BillingResult billingResult, String str2) {
        if (billingResult.getResponseCode() == 0) {
            list.add(str);
            if (atomicInteger.get() == list.size()) {
                runnable.run();
            }
        }
    }

    public void lambda$launchBillingFlow$2(final Activity activity, final AccountInstance accountInstance, final TLRPC.InputStorePaymentPurpose inputStorePaymentPurpose, final List list, final BillingFlowParams.SubscriptionUpdateParams subscriptionUpdateParams, BillingResult billingResult, List list2) {
        if (billingResult.getResponseCode() == 0) {
            final Runnable runnable = new Runnable() {
                @Override
                public final void run() {
                    BillingController.this.lambda$launchBillingFlow$0(activity, accountInstance, inputStorePaymentPurpose, list, subscriptionUpdateParams);
                }
            };
            final AtomicInteger atomicInteger = new AtomicInteger(0);
            final ArrayList arrayList = new ArrayList();
            Iterator it = list2.iterator();
            while (it.hasNext()) {
                Purchase purchase = (Purchase) it.next();
                if (!purchase.isAcknowledged()) {
                    onPurchasesUpdated(BillingResult.newBuilder().setResponseCode(0).build(), Collections.singletonList(purchase));
                    return;
                }
                Iterator it2 = list.iterator();
                while (true) {
                    if (it2.hasNext()) {
                        final String productId = ((BillingFlowParams.ProductDetailsParams) it2.next()).zza().getProductId();
                        if (purchase.getProducts().contains(productId)) {
                            atomicInteger.incrementAndGet();
                            this.billingClient.consumeAsync(ConsumeParams.newBuilder().setPurchaseToken(purchase.getPurchaseToken()).build(), new ConsumeResponseListener() {
                                @Override
                                public final void onConsumeResponse(BillingResult billingResult2, String str) {
                                    BillingController.lambda$launchBillingFlow$1(arrayList, productId, atomicInteger, runnable, billingResult2, str);
                                }
                            });
                            break;
                        }
                    }
                }
            }
            if (atomicInteger.get() == 0) {
                runnable.run();
            }
        }
    }

    public void lambda$onPurchasesUpdated$4(AlertDialog alertDialog, Purchase purchase, AccountInstance accountInstance, BillingResult billingResult, TLRPC.TL_payments_assignPlayMarketTransaction tL_payments_assignPlayMarketTransaction, TLObject tLObject, TLRPC.TL_error tL_error) {
        Objects.requireNonNull(alertDialog);
        AndroidUtilities.runOnUIThread(new BillingController$$ExternalSyntheticLambda10(alertDialog));
        this.requestingTokens.remove(purchase.getPurchaseToken());
        if (!(tLObject instanceof TLRPC.Updates)) {
            if (tL_error != null) {
                Runnable runnable = this.onCanceled;
                if (runnable != null) {
                    runnable.run();
                    this.onCanceled = null;
                }
                NotificationCenter.getGlobalInstance().postNotificationNameOnUIThread(NotificationCenter.billingConfirmPurchaseError, tL_payments_assignPlayMarketTransaction, tL_error);
                return;
            }
            return;
        }
        accountInstance.getMessagesController().processUpdates((TLRPC.Updates) tLObject, false);
        Iterator it = purchase.getProducts().iterator();
        while (it.hasNext()) {
            Consumer remove = this.resultListeners.remove((String) it.next());
            if (remove != null) {
                remove.accept(billingResult);
            }
        }
        consumeGiftPurchase(purchase, tL_payments_assignPlayMarketTransaction.purpose);
        BillingUtilities.cleanupPurchase(purchase);
    }

    public void lambda$onQueriedPremiumProductDetails$7() {
        try {
            queryProductDetails(Collections.singletonList(PREMIUM_PRODUCT), new BillingController$$ExternalSyntheticLambda3(this));
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    public void onQueriedPremiumProductDetails(BillingResult billingResult, List<ProductDetails> list) {
        FileLog.d("Billing: Query product details finished " + billingResult + ", " + list);
        if (billingResult.getResponseCode() != 0) {
            switchToInvoice();
            int i = this.triesLeft - 1;
            this.triesLeft = i;
            if (i > 0) {
                AndroidUtilities.runOnUIThread(new Runnable() {
                    @Override
                    public final void run() {
                        BillingController.this.lambda$onQueriedPremiumProductDetails$7();
                    }
                }, i == 2 ? 1000L : 10000L);
                return;
            }
            return;
        }
        for (ProductDetails productDetails : list) {
            if (productDetails.getProductId().equals("telegram_premium")) {
                PREMIUM_PRODUCT_DETAILS = productDetails;
            }
        }
        if (PREMIUM_PRODUCT_DETAILS == null) {
            switchToInvoice();
        } else {
            switchBackFromInvoice();
            NotificationCenter.getGlobalInstance().postNotificationNameOnUIThread(NotificationCenter.billingProductDetailsUpdated, new Object[0]);
        }
    }

    private void switchBackFromInvoice() {
        if (billingClientEmpty) {
            billingClientEmpty = false;
            NotificationCenter.getGlobalInstance().lambda$postNotificationNameOnUIThread$1(NotificationCenter.billingProductDetailsUpdated, new Object[0]);
        }
    }

    private void switchToInvoice() {
        if (billingClientEmpty) {
            return;
        }
        billingClientEmpty = true;
        NotificationCenter.getGlobalInstance().lambda$postNotificationNameOnUIThread$1(NotificationCenter.billingProductDetailsUpdated, new Object[0]);
    }

    public void addResultListener(String str, Consumer consumer) {
        this.resultListeners.put(str, consumer);
    }

    public String formatCurrency(long j, String str) {
        return formatCurrency(j, str, getCurrencyExp(str));
    }

    public String formatCurrency(long j, String str, int i) {
        return formatCurrency(j, str, i, false);
    }

    public String formatCurrency(long j, String str, int i, boolean z) {
        if (str == null || str.isEmpty()) {
            return String.valueOf(j);
        }
        if ("TON".equalsIgnoreCase(str)) {
            StringBuilder sb = new StringBuilder();
            sb.append("TON ");
            double d = j;
            Double.isNaN(d);
            sb.append(d / 1.0E9d);
            return sb.toString();
        }
        Currency currency = Currency.getInstance(str);
        if (currency == null) {
            return j + " " + str;
        }
        if (currencyInstance == null) {
            currencyInstance = NumberFormat.getCurrencyInstance();
        }
        currencyInstance.setCurrency(currency);
        NumberFormat numberFormat = currencyInstance;
        double d2 = j;
        if (z) {
            double pow = Math.pow(10.0d, i);
            Double.isNaN(d2);
            return numberFormat.format(Math.round(d2 / pow));
        }
        double pow2 = Math.pow(10.0d, i);
        Double.isNaN(d2);
        return numberFormat.format(d2 / pow2);
    }

    public int getCurrencyExp(String str) {
        BillingUtilities.extractCurrencyExp(this.currencyExpMap);
        return ((Integer) Map.EL.getOrDefault(this.currencyExpMap, str, 0)).intValue();
    }

    public String getLastPremiumToken() {
        return this.lastPremiumToken;
    }

    public String getLastPremiumTransaction() {
        return this.lastPremiumTransaction;
    }

    public boolean isReady() {
        return this.billingClient.isReady();
    }

    public void launchBillingFlow(Activity activity, AccountInstance accountInstance, TLRPC.InputStorePaymentPurpose inputStorePaymentPurpose, List<BillingFlowParams.ProductDetailsParams> list) {
        launchBillingFlow(activity, accountInstance, inputStorePaymentPurpose, list, null, false);
    }

    public void launchBillingFlow(final Activity activity, final AccountInstance accountInstance, final TLRPC.InputStorePaymentPurpose inputStorePaymentPurpose, final List<BillingFlowParams.ProductDetailsParams> list, final BillingFlowParams.SubscriptionUpdateParams subscriptionUpdateParams, boolean z) {
        if (!isReady() || activity == null) {
            return;
        }
        if (((inputStorePaymentPurpose instanceof TLRPC.TL_inputStorePaymentGiftPremium) || (inputStorePaymentPurpose instanceof TLRPC.TL_inputStorePaymentStarsTopup) || (inputStorePaymentPurpose instanceof TLRPC.TL_inputStorePaymentStarsGift)) && !z) {
            queryPurchases("inapp", new PurchasesResponseListener() {
                @Override
                public final void onQueryPurchasesResponse(BillingResult billingResult, List list2) {
                    BillingController.this.lambda$launchBillingFlow$2(activity, accountInstance, inputStorePaymentPurpose, list, subscriptionUpdateParams, billingResult, list2);
                }
            });
            return;
        }
        Pair createDeveloperPayload = BillingUtilities.createDeveloperPayload(inputStorePaymentPurpose, accountInstance);
        String str = (String) createDeveloperPayload.first;
        String str2 = (String) createDeveloperPayload.second;
        BillingFlowParams.Builder productDetailsParamsList = BillingFlowParams.newBuilder().setObfuscatedAccountId(str).setObfuscatedProfileId(str2).setProductDetailsParamsList(list);
        if (subscriptionUpdateParams != null) {
            productDetailsParamsList.setSubscriptionUpdateParams(subscriptionUpdateParams);
        }
        int responseCode = this.billingClient.launchBillingFlow(activity, productDetailsParamsList.build()).getResponseCode();
        if (responseCode != 0) {
            FileLog.d("Billing: Launch Error: " + responseCode + ", " + str + ", " + str2);
        }
    }

    @Override
    public void onBillingServiceDisconnected() {
        FileLog.d("Billing: Service disconnected");
        int i = this.isDisconnected ? 15000 : 5000;
        this.isDisconnected = true;
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                BillingController.this.lambda$onBillingServiceDisconnected$6();
            }
        }, i);
    }

    @Override
    public void onBillingSetupFinished(BillingResult billingResult) {
        FileLog.d("Billing: Setup finished with result " + billingResult);
        if (billingResult.getResponseCode() != 0) {
            if (this.isDisconnected) {
                return;
            }
            switchToInvoice();
            return;
        }
        this.isDisconnected = false;
        this.triesLeft = 3;
        try {
            queryProductDetails(Collections.singletonList(PREMIUM_PRODUCT), new BillingController$$ExternalSyntheticLambda3(this));
        } catch (Exception e) {
            FileLog.e(e);
        }
        queryPurchases("inapp", new PurchasesResponseListener() {
            @Override
            public final void onQueryPurchasesResponse(BillingResult billingResult2, List list) {
                BillingController.this.onPurchasesUpdated(billingResult2, list);
            }
        });
        queryPurchases("subs", new PurchasesResponseListener() {
            @Override
            public final void onQueryPurchasesResponse(BillingResult billingResult2, List list) {
                BillingController.this.onPurchasesUpdated(billingResult2, list);
            }
        });
        if (this.setupListeners.isEmpty()) {
            return;
        }
        for (int i = 0; i < this.setupListeners.size(); i++) {
            AndroidUtilities.runOnUIThread(this.setupListeners.get(i));
        }
        this.setupListeners.clear();
    }

    @Override
    public void onPurchasesUpdated(final BillingResult billingResult, List<Purchase> list) {
        Pair extractDeveloperPayload;
        FileLog.d("Billing: Purchases updated: " + billingResult + ", " + list);
        if (billingResult.getResponseCode() != 0) {
            if (billingResult.getResponseCode() == 1) {
                PremiumPreviewFragment.sentPremiumBuyCanceled();
            }
            Runnable runnable = this.onCanceled;
            if (runnable != null) {
                runnable.run();
                this.onCanceled = null;
                return;
            }
            return;
        }
        if (list == null || list.isEmpty()) {
            return;
        }
        this.lastPremiumTransaction = null;
        for (final Purchase purchase : list) {
            if (purchase.getProducts().contains("telegram_premium")) {
                this.lastPremiumTransaction = purchase.getOrderId();
                this.lastPremiumToken = purchase.getPurchaseToken();
            }
            if (!this.requestingTokens.contains(purchase.getPurchaseToken()) && purchase.getPurchaseState() == 1 && (extractDeveloperPayload = BillingUtilities.extractDeveloperPayload(purchase)) != null && extractDeveloperPayload.first != null) {
                if (purchase.isAcknowledged()) {
                    consumeGiftPurchase(purchase, (TLRPC.InputStorePaymentPurpose) extractDeveloperPayload.second);
                } else {
                    this.requestingTokens.add(purchase.getPurchaseToken());
                    final TLRPC.TL_payments_assignPlayMarketTransaction tL_payments_assignPlayMarketTransaction = new TLRPC.TL_payments_assignPlayMarketTransaction();
                    TLRPC.TL_dataJSON tL_dataJSON = new TLRPC.TL_dataJSON();
                    tL_payments_assignPlayMarketTransaction.receipt = tL_dataJSON;
                    tL_dataJSON.data = purchase.getOriginalJson();
                    tL_payments_assignPlayMarketTransaction.purpose = (TLRPC.InputStorePaymentPurpose) extractDeveloperPayload.second;
                    final AlertDialog alertDialog = new AlertDialog(ApplicationLoader.applicationContext, 3);
                    AndroidUtilities.runOnUIThread(new Runnable() {
                        @Override
                        public final void run() {
                            AlertDialog.this.showDelayed(500L);
                        }
                    });
                    final AccountInstance accountInstance = (AccountInstance) extractDeveloperPayload.first;
                    accountInstance.getConnectionsManager().sendRequest(tL_payments_assignPlayMarketTransaction, new RequestDelegate() {
                        @Override
                        public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                            BillingController.this.lambda$onPurchasesUpdated$4(alertDialog, purchase, accountInstance, billingResult, tL_payments_assignPlayMarketTransaction, tLObject, tL_error);
                        }
                    }, 65602);
                }
            }
        }
    }

    public void queryProductDetails(List<QueryProductDetailsParams.Product> list, ProductDetailsResponseListener productDetailsResponseListener) {
        if (!isReady()) {
            throw new IllegalStateException("Billing: Controller should be ready for this call!");
        }
        this.billingClient.queryProductDetailsAsync(QueryProductDetailsParams.newBuilder().setProductList(list).build(), productDetailsResponseListener);
    }

    public void queryPurchases(String str, PurchasesResponseListener purchasesResponseListener) {
        this.billingClient.queryPurchasesAsync(QueryPurchasesParams.newBuilder().setProductType(str).build(), purchasesResponseListener);
    }

    public void setOnCanceled(Runnable runnable) {
        this.onCanceled = runnable;
    }

    public void lambda$onBillingServiceDisconnected$6() {
        if (isReady()) {
            return;
        }
        try {
            BillingUtilities.extractCurrencyExp(this.currencyExpMap);
            if (BuildVars.useInvoiceBilling()) {
                return;
            }
            this.billingClient.startConnection(this);
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    public boolean startManageSubscription(Context context, String str) {
        try {
            context.startActivity(new Intent("android.intent.action.VIEW", Uri.parse(String.format("https://play.google.com/store/account/subscriptions?sku=%s&package=%s", str, context.getPackageName()))));
            return true;
        } catch (ActivityNotFoundException unused) {
            return false;
        }
    }

    public void whenSetuped(Runnable runnable) {
        this.setupListeners.add(runnable);
    }
}
