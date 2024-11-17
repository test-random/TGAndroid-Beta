package org.telegram.messenger;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.SystemClock;
import android.text.TextUtils;
import androidx.core.content.FileProvider;
import com.microsoft.appcenter.AppCenter;
import com.microsoft.appcenter.CustomProperties;
import com.microsoft.appcenter.analytics.Analytics;
import com.microsoft.appcenter.analytics.EventProperties;
import com.microsoft.appcenter.crashes.Crashes;
import com.microsoft.appcenter.distribute.Distribute;
import com.microsoft.appcenter.utils.async.AppCenterConsumer;
import java.io.File;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.Components.AlertsCreator;

public class ApplicationLoaderImpl extends ApplicationLoader {
    private static long lastUpdateCheckTime;

    public static void lambda$startAppCenterInternal$0(String str) {
        if (str != null) {
            Utilities.setupNativeCrashesListener(str);
        }
    }

    @Override
    protected void appCenterLogInternal(Throwable th) {
        try {
            Crashes.trackError(th);
        } catch (Throwable unused) {
        }
    }

    @Override
    public boolean checkApkInstallPermissions(Context context) {
        boolean canRequestPackageInstalls;
        if (Build.VERSION.SDK_INT < 26) {
            return true;
        }
        canRequestPackageInstalls = ApplicationLoader.applicationContext.getPackageManager().canRequestPackageInstalls();
        if (canRequestPackageInstalls) {
            return true;
        }
        AlertsCreator.createApkRestrictedDialog(context, null).show();
        return false;
    }

    @Override
    protected void checkForUpdatesInternal() {
        try {
            if (!BuildVars.DEBUG_VERSION || SystemClock.elapsedRealtime() - lastUpdateCheckTime < 3600000) {
                return;
            }
            lastUpdateCheckTime = SystemClock.elapsedRealtime();
            Distribute.checkForUpdate();
        } catch (Throwable th) {
            FileLog.e(th);
        }
    }

    @Override
    protected void logDualCameraInternal(boolean z, boolean z2) {
        try {
            StringBuilder sb = new StringBuilder();
            sb.append("dual-camera[");
            sb.append((Build.MANUFACTURER + " " + Build.DEVICE).toUpperCase());
            sb.append("]");
            Analytics.trackEvent(sb.toString(), new EventProperties().set("success", z).set("vendor", z2).set("product", Build.PRODUCT + "").set("model", Build.MODEL));
        } catch (Throwable unused) {
        }
    }

    @Override
    protected String onGetApplicationId() {
        return "org.telegram.messenger.beta";
    }

    @Override
    public boolean openApkInstall(Activity activity, TLRPC.Document document) {
        Uri fromFile;
        boolean z = false;
        try {
            FileLoader.getAttachFileName(document);
            File pathToAttach = FileLoader.getInstance(UserConfig.selectedAccount).getPathToAttach(document, true);
            z = pathToAttach.exists();
            if (z) {
                Intent intent = new Intent("android.intent.action.VIEW");
                intent.setFlags(1);
                if (Build.VERSION.SDK_INT >= 24) {
                    fromFile = FileProvider.getUriForFile(activity, ApplicationLoader.getApplicationId() + ".provider", pathToAttach);
                } else {
                    fromFile = Uri.fromFile(pathToAttach);
                }
                intent.setDataAndType(fromFile, "application/vnd.android.package-archive");
                try {
                    activity.startActivityForResult(intent, 500);
                } catch (Exception e) {
                    FileLog.e(e);
                }
            }
        } catch (Exception e2) {
            FileLog.e(e2);
        }
        return z;
    }

    @Override
    protected void startAppCenterInternal(Activity activity) {
        String str;
        String str2;
        try {
            if (BuildVars.DEBUG_VERSION) {
                Distribute.setEnabledForDebuggableBuild(true);
                if (TextUtils.isEmpty("f9726602-67c9-48d2-b5d0-4761f1c1a8f3")) {
                    throw new RuntimeException("App Center hash is empty. add to local.properties field APP_CENTER_HASH_PRIVATE and APP_CENTER_HASH_PUBLIC");
                }
                AppCenter.start(activity.getApplication(), "f9726602-67c9-48d2-b5d0-4761f1c1a8f3", Distribute.class, Crashes.class, Analytics.class);
                Crashes.getMinidumpDirectory().thenAccept(new AppCenterConsumer() {
                    @Override
                    public final void accept(Object obj) {
                        ApplicationLoaderImpl.lambda$startAppCenterInternal$0((String) obj);
                    }
                });
                CustomProperties customProperties = new CustomProperties();
                customProperties.set("model", Build.MODEL);
                customProperties.set("manufacturer", Build.MANUFACTURER);
                if (Build.VERSION.SDK_INT >= 31) {
                    str = Build.SOC_MODEL;
                    customProperties.set("model", str);
                    str2 = Build.SOC_MANUFACTURER;
                    customProperties.set("manufacturer", str2);
                }
                customProperties.set("device", Build.DEVICE);
                customProperties.set("product", Build.PRODUCT);
                customProperties.set("hardware", Build.HARDWARE);
                customProperties.set("user", Build.USER);
                AppCenter.setCustomProperties(customProperties);
                String str3 = "uid=" + UserConfig.getInstance(UserConfig.selectedAccount).clientUserId;
                if (UserConfig.getInstance(UserConfig.selectedAccount).getCurrentUser() != null) {
                    String publicUsername = UserObject.getPublicUsername(UserConfig.getInstance(UserConfig.selectedAccount).getCurrentUser());
                    if (!TextUtils.isEmpty(publicUsername)) {
                        str3 = str3 + " @" + publicUsername;
                    }
                }
                AppCenter.setUserId(str3);
            }
        } catch (Throwable th) {
            FileLog.e(th);
        }
    }
}
