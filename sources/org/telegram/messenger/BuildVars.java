package org.telegram.messenger;

import android.content.SharedPreferences;
import android.os.Build;
import com.android.billingclient.api.ProductDetails;
import java.lang.Thread;
import java.util.Iterator;
import java.util.Objects;

public class BuildVars {
    public static String APP_HASH = null;
    public static int APP_ID = 0;
    public static String BUILD_VERSION_STRING = null;
    public static boolean CHECK_UPDATES = true;
    public static boolean DEBUG_PRIVATE_VERSION = false;
    public static boolean DEBUG_VERSION = true;
    public static String GOOGLE_AUTH_CLIENT_ID = null;
    public static String HUAWEI_APP_ID = null;
    public static String HUAWEI_STORE_URL = null;
    public static boolean IS_BILLING_UNAVAILABLE = false;
    public static boolean LOGS_ENABLED = true;
    public static boolean NO_SCOPED_STORAGE = false;
    public static String PLAYSTORE_APP_URL = null;
    public static String SAFETYNET_KEY = null;
    public static boolean USE_CLOUD_STRINGS = true;
    private static Boolean betaApp;

    static {
        boolean z = true;
        NO_SCOPED_STORAGE = Build.VERSION.SDK_INT <= 29;
        BUILD_VERSION_STRING = "11.3.5";
        APP_ID = 4;
        APP_HASH = "014b35b6184100b085b0d0572f9b5103";
        SAFETYNET_KEY = "AIzaSyDqt8P-7F7CPCseMkOiVRgb1LY8RN1bvH8";
        PLAYSTORE_APP_URL = "https://play.google.com/store/apps/details?id=org.telegram.messenger";
        HUAWEI_STORE_URL = "https://appgallery.huawei.com/app/C101184875";
        GOOGLE_AUTH_CLIENT_ID = "760348033671-81kmi3pi84p11ub8hp9a1funsv0rn2p9.apps.googleusercontent.com";
        HUAWEI_APP_ID = "101184875";
        IS_BILLING_UNAVAILABLE = false;
        if (ApplicationLoader.applicationContext != null) {
            SharedPreferences sharedPreferences = ApplicationLoader.applicationContext.getSharedPreferences("systemConfig", 0);
            boolean z2 = DEBUG_VERSION;
            if (!z2 && !sharedPreferences.getBoolean("logsEnabled", z2)) {
                z = false;
            }
            LOGS_ENABLED = z;
            if (z) {
                Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
                    @Override
                    public final void uncaughtException(Thread thread, Throwable th) {
                        FileLog.fatal(th, true);
                    }
                });
            }
        }
    }

    public static String getSmsHash() {
        return ApplicationLoader.isStandaloneBuild() ? "w0lkcmTZkKh" : DEBUG_VERSION ? "O2P2z+/jBpJ" : "oLeq9AcOZkT";
    }

    private static boolean hasDirectCurrency() {
        ProductDetails productDetails;
        if (BillingController.getInstance().isReady() && (productDetails = BillingController.PREMIUM_PRODUCT_DETAILS) != null) {
            Iterator it = productDetails.getSubscriptionOfferDetails().iterator();
            while (it.hasNext()) {
                for (ProductDetails.PricingPhase pricingPhase : ((ProductDetails.SubscriptionOfferDetails) it.next()).getPricingPhases().getPricingPhaseList()) {
                    Iterator<String> it2 = MessagesController.getInstance(UserConfig.selectedAccount).directPaymentsCurrency.iterator();
                    while (it2.hasNext()) {
                        if (Objects.equals(pricingPhase.getPriceCurrencyCode(), it2.next())) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    public static boolean isBetaApp() {
        if (betaApp == null) {
            betaApp = Boolean.valueOf(ApplicationLoader.applicationContext != null && "org.telegram.messenger.beta".equals(ApplicationLoader.applicationContext.getPackageName()));
        }
        return betaApp.booleanValue();
    }

    public static boolean isHuaweiStoreApp() {
        return ApplicationLoader.isHuaweiStoreBuild();
    }

    public static boolean useInvoiceBilling() {
        return BillingController.billingClientEmpty || DEBUG_VERSION || ApplicationLoader.isStandaloneBuild() || isBetaApp() || isHuaweiStoreApp() || hasDirectCurrency();
    }
}
