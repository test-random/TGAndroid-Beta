package org.telegram.messenger;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import java.util.Map;

public class GcmPushListenerService extends FirebaseMessagingService {
    public static void lambda$onNewToken$0(String str) {
        if (BuildVars.LOGS_ENABLED) {
            FileLog.d("Refreshed FCM token: " + str);
        }
        ApplicationLoader.postInitApplication();
        PushListenerController.sendRegistrationToServer(2, str);
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        String from = remoteMessage.getFrom();
        Map data = remoteMessage.getData();
        long sentTime = remoteMessage.getSentTime();
        if (BuildVars.LOGS_ENABLED) {
            FileLog.d("FCM received data: " + data + " from: " + from);
        }
        PushListenerController.processRemoteMessage(2, (String) data.get("p"), sentTime);
    }

    @Override
    public void onNewToken(final String str) {
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                GcmPushListenerService.lambda$onNewToken$0(str);
            }
        });
    }
}
