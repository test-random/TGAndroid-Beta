package org.telegram.ui.Stories.recorder;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.BuildVars;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.NotificationsController;
import org.telegram.messenger.R;
import org.telegram.messenger.UserConfig;

public class StoryUploadingService extends Service implements NotificationCenter.NotificationCenterDelegate {
    private NotificationCompat.Builder builder;
    private int currentAccount = -1;
    private float currentProgress;
    private String path;

    public StoryUploadingService() {
        NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.uploadStoryEnd);
    }

    @Override
    public void didReceivedNotification(int i, int i2, Object... objArr) {
        String str;
        if (i != NotificationCenter.uploadStoryProgress) {
            if (i == NotificationCenter.uploadStoryEnd && (str = this.path) != null && str.equals((String) objArr[0])) {
                stopSelf();
                return;
            }
            return;
        }
        String str2 = this.path;
        if (str2 == null || !str2.equals((String) objArr[0])) {
            return;
        }
        float floatValue = ((Float) objArr[1]).floatValue();
        this.currentProgress = floatValue;
        this.builder.setProgress(100, Math.round(floatValue * 100.0f), this.currentProgress <= 0.0f);
        try {
            NotificationManagerCompat.from(ApplicationLoader.applicationContext).notify(33, this.builder.build());
        } catch (Throwable th) {
            FileLog.e(th);
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        try {
            stopForeground(true);
        } catch (Exception unused) {
        }
        NotificationManagerCompat.from(ApplicationLoader.applicationContext).cancel(33);
        NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.uploadStoryEnd);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.uploadStoryProgress);
        if (BuildVars.LOGS_ENABLED) {
            FileLog.d("upload story destroy");
        }
    }

    @Override
    public int onStartCommand(Intent intent, int i, int i2) {
        this.path = intent.getStringExtra("path");
        int i3 = this.currentAccount;
        int intExtra = intent.getIntExtra("currentAccount", UserConfig.selectedAccount);
        this.currentAccount = intExtra;
        if (!UserConfig.isValidAccount(intExtra)) {
            stopSelf();
            return 2;
        }
        if (i3 != this.currentAccount) {
            if (i3 != -1) {
                NotificationCenter.getInstance(i3).removeObserver(this, NotificationCenter.uploadStoryProgress);
            }
            int i4 = this.currentAccount;
            if (i4 != -1) {
                NotificationCenter.getInstance(i4).addObserver(this, NotificationCenter.uploadStoryProgress);
            }
        }
        if (this.path == null) {
            stopSelf();
            return 2;
        }
        if (BuildVars.LOGS_ENABLED) {
            FileLog.d("start upload story");
        }
        if (this.builder == null) {
            NotificationsController.checkOtherNotificationsChannel();
            NotificationCompat.Builder builder = new NotificationCompat.Builder(ApplicationLoader.applicationContext);
            this.builder = builder;
            builder.setSmallIcon(17301640);
            this.builder.setWhen(System.currentTimeMillis());
            this.builder.setChannelId(NotificationsController.OTHER_NOTIFICATIONS_CHANNEL);
            this.builder.setContentTitle(LocaleController.getString(R.string.AppName));
            NotificationCompat.Builder builder2 = this.builder;
            int i5 = R.string.StoryUploading;
            builder2.setTicker(LocaleController.getString(i5));
            this.builder.setContentText(LocaleController.getString(i5));
        }
        this.currentProgress = 0.0f;
        this.builder.setProgress(100, Math.round(0.0f), false);
        startForeground(33, this.builder.build());
        try {
            NotificationManagerCompat.from(ApplicationLoader.applicationContext).notify(33, this.builder.build());
        } catch (Throwable th) {
            FileLog.e(th);
        }
        return 2;
    }
}
