package org.telegram.messenger;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class NotificationsService extends Service {
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        ApplicationLoader.postInitApplication();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (MessagesController.getGlobalNotificationsSettings().getBoolean("pushService", true)) {
            Intent intent = new Intent("org.telegram.start");
            intent.setPackage(getPackageName());
            sendBroadcast(intent);
        }
    }

    @Override
    public int onStartCommand(Intent intent, int i, int i2) {
        return 1;
    }
}
