package org.telegram.messenger.voip;

import android.app.Activity;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import org.telegram.messenger.AccountInstance;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.UserConfig;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.Components.voip.VoIPHelper;

public final class VoIPPendingCall {
    private AccountInstance accountInstance;
    private final Activity activity;
    private Handler handler;
    private NotificationCenter notificationCenter;
    private final NotificationCenter.NotificationCenterDelegate observer;
    private final Runnable releaseRunnable;
    private boolean released;
    private final long userId;
    private final boolean video;

    private VoIPPendingCall(Activity activity, long j, boolean z, long j2, AccountInstance accountInstance) {
        NotificationCenter.NotificationCenterDelegate notificationCenterDelegate = new NotificationCenter.NotificationCenterDelegate() {
            @Override
            public final void didReceivedNotification(int i, int i2, Object[] objArr) {
                VoIPPendingCall.this.lambda$new$0(i, i2, objArr);
            }
        };
        this.observer = notificationCenterDelegate;
        Runnable runnable = new Runnable() {
            @Override
            public final void run() {
                VoIPPendingCall.this.lambda$new$1();
            }
        };
        this.releaseRunnable = runnable;
        this.activity = activity;
        this.userId = j;
        this.video = z;
        this.accountInstance = accountInstance;
        if (onConnectionStateUpdated(false)) {
            return;
        }
        NotificationCenter notificationCenter = NotificationCenter.getInstance(UserConfig.selectedAccount);
        this.notificationCenter = notificationCenter;
        notificationCenter.addObserver(notificationCenterDelegate, NotificationCenter.didUpdateConnectionState);
        Handler handler = new Handler(Looper.myLooper());
        this.handler = handler;
        handler.postDelayed(runnable, j2);
    }

    private boolean isAirplaneMode() {
        return Settings.System.getInt(this.activity.getContentResolver(), "airplane_mode_on", 0) != 0;
    }

    private boolean isConnected(AccountInstance accountInstance) {
        return accountInstance.getConnectionsManager().getConnectionState() == 3;
    }

    public void lambda$new$0(int i, int i2, Object[] objArr) {
        if (i == NotificationCenter.didUpdateConnectionState) {
            onConnectionStateUpdated(false);
        }
    }

    public void lambda$new$1() {
        onConnectionStateUpdated(true);
    }

    private boolean onConnectionStateUpdated(boolean z) {
        if (this.released || !(z || isConnected(this.accountInstance) || isAirplaneMode())) {
            return false;
        }
        MessagesController messagesController = this.accountInstance.getMessagesController();
        TLRPC.User user = messagesController.getUser(Long.valueOf(this.userId));
        if (user != null) {
            TLRPC.UserFull userFull = messagesController.getUserFull(user.id);
            VoIPHelper.startCall(user, this.video, userFull != null && userFull.video_calls_available, this.activity, userFull, this.accountInstance);
        } else if (isAirplaneMode()) {
            VoIPHelper.startCall(null, this.video, false, this.activity, null, this.accountInstance);
        }
        release();
        return true;
    }

    public static VoIPPendingCall startOrSchedule(Activity activity, long j, boolean z, AccountInstance accountInstance) {
        return new VoIPPendingCall(activity, j, z, 1000L, accountInstance);
    }

    public void release() {
        if (this.released) {
            return;
        }
        NotificationCenter notificationCenter = this.notificationCenter;
        if (notificationCenter != null) {
            notificationCenter.removeObserver(this.observer, NotificationCenter.didUpdateConnectionState);
        }
        Handler handler = this.handler;
        if (handler != null) {
            handler.removeCallbacks(this.releaseRunnable);
        }
        this.released = true;
    }
}
