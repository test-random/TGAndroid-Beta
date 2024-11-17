package org.telegram.messenger;

import android.content.SharedPreferences;
import android.os.SystemClock;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.SharedConfig;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.RequestTimeDelegate;

public class ProxyRotationController implements NotificationCenter.NotificationCenterDelegate {
    public static final int DEFAULT_TIMEOUT_INDEX = 1;
    private static final ProxyRotationController INSTANCE = new ProxyRotationController();
    public static final List<Integer> ROTATION_TIMEOUTS = Arrays.asList(5, 10, 15, 30, 60);
    private Runnable checkProxyAndSwitchRunnable = new Runnable() {
        @Override
        public final void run() {
            ProxyRotationController.this.lambda$new$2();
        }
    };
    private boolean isCurrentlyChecking;

    public static void init() {
        INSTANCE.initInternal();
    }

    private void initInternal() {
        for (int i = 0; i < 4; i++) {
            NotificationCenter.getInstance(i).addObserver(this, NotificationCenter.didUpdateConnectionState);
        }
        NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.proxyCheckDone);
        NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.proxySettingsChanged);
    }

    public static void lambda$new$0(SharedConfig.ProxyInfo proxyInfo, long j) {
        proxyInfo.availableCheckTime = SystemClock.elapsedRealtime();
        proxyInfo.checking = false;
        if (j == -1) {
            proxyInfo.available = false;
            proxyInfo.ping = 0L;
        } else {
            proxyInfo.ping = j;
            proxyInfo.available = true;
        }
        NotificationCenter.getGlobalInstance().lambda$postNotificationNameOnUIThread$1(NotificationCenter.proxyCheckDone, proxyInfo);
    }

    public static void lambda$new$1(final SharedConfig.ProxyInfo proxyInfo, final long j) {
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                ProxyRotationController.lambda$new$0(SharedConfig.ProxyInfo.this, j);
            }
        });
    }

    public void lambda$new$2() {
        this.isCurrentlyChecking = true;
        int i = UserConfig.selectedAccount;
        boolean z = false;
        for (int i2 = 0; i2 < SharedConfig.proxyList.size(); i2++) {
            final SharedConfig.ProxyInfo proxyInfo = SharedConfig.proxyList.get(i2);
            if (!proxyInfo.checking && SystemClock.elapsedRealtime() - proxyInfo.availableCheckTime >= 120000) {
                proxyInfo.checking = true;
                proxyInfo.proxyCheckPingId = ConnectionsManager.getInstance(i).checkProxy(proxyInfo.address, proxyInfo.port, proxyInfo.username, proxyInfo.password, proxyInfo.secret, new RequestTimeDelegate() {
                    @Override
                    public final void run(long j) {
                        ProxyRotationController.lambda$new$1(SharedConfig.ProxyInfo.this, j);
                    }
                });
                z = true;
            }
        }
        if (z) {
            return;
        }
        this.isCurrentlyChecking = false;
        switchToAvailable();
    }

    public static int lambda$switchToAvailable$3(SharedConfig.ProxyInfo proxyInfo, SharedConfig.ProxyInfo proxyInfo2) {
        return Long.compare(proxyInfo.ping, proxyInfo2.ping);
    }

    private void switchToAvailable() {
        this.isCurrentlyChecking = false;
        if (SharedConfig.proxyRotationEnabled) {
            ArrayList<SharedConfig.ProxyInfo> arrayList = new ArrayList(SharedConfig.proxyList);
            Collections.sort(arrayList, new Comparator() {
                @Override
                public final int compare(Object obj, Object obj2) {
                    int lambda$switchToAvailable$3;
                    lambda$switchToAvailable$3 = ProxyRotationController.lambda$switchToAvailable$3((SharedConfig.ProxyInfo) obj, (SharedConfig.ProxyInfo) obj2);
                    return lambda$switchToAvailable$3;
                }
            });
            for (SharedConfig.ProxyInfo proxyInfo : arrayList) {
                if (proxyInfo != SharedConfig.currentProxy && !proxyInfo.checking && proxyInfo.available) {
                    SharedPreferences.Editor edit = MessagesController.getGlobalMainSettings().edit();
                    edit.putString("proxy_ip", proxyInfo.address);
                    edit.putString("proxy_pass", proxyInfo.password);
                    edit.putString("proxy_user", proxyInfo.username);
                    edit.putInt("proxy_port", proxyInfo.port);
                    edit.putString("proxy_secret", proxyInfo.secret);
                    edit.putBoolean("proxy_enabled", true);
                    if (!proxyInfo.secret.isEmpty()) {
                        edit.putBoolean("proxy_enabled_calls", false);
                    }
                    edit.apply();
                    SharedConfig.currentProxy = proxyInfo;
                    NotificationCenter.getGlobalInstance().lambda$postNotificationNameOnUIThread$1(NotificationCenter.proxySettingsChanged, new Object[0]);
                    NotificationCenter.getGlobalInstance().lambda$postNotificationNameOnUIThread$1(NotificationCenter.proxyChangedByRotation, new Object[0]);
                    SharedConfig.ProxyInfo proxyInfo2 = SharedConfig.currentProxy;
                    ConnectionsManager.setProxySettings(true, proxyInfo2.address, proxyInfo2.port, proxyInfo2.username, proxyInfo2.password, proxyInfo2.secret);
                    return;
                }
            }
        }
    }

    @Override
    public void didReceivedNotification(int i, int i2, Object... objArr) {
        if (i == NotificationCenter.proxyCheckDone) {
            if (SharedConfig.isProxyEnabled() && SharedConfig.proxyRotationEnabled && SharedConfig.proxyList.size() > 1 && this.isCurrentlyChecking) {
                switchToAvailable();
                return;
            }
            return;
        }
        if (i != NotificationCenter.proxySettingsChanged) {
            if (i != NotificationCenter.didUpdateConnectionState || i2 != UserConfig.selectedAccount) {
                return;
            }
            if ((!SharedConfig.isProxyEnabled() && !SharedConfig.proxyRotationEnabled) || SharedConfig.proxyList.size() <= 1) {
                return;
            }
            if (ConnectionsManager.getInstance(i2).getConnectionState() == 4) {
                if (this.isCurrentlyChecking) {
                    return;
                }
                AndroidUtilities.runOnUIThread(this.checkProxyAndSwitchRunnable, ROTATION_TIMEOUTS.get(SharedConfig.proxyRotationTimeout).intValue() * 1000);
                return;
            }
        }
        AndroidUtilities.cancelRunOnUIThread(this.checkProxyAndSwitchRunnable);
    }
}
