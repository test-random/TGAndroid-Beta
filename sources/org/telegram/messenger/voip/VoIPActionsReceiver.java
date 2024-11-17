package org.telegram.messenger.voip;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class VoIPActionsReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        int i;
        if (VoIPService.getSharedInstance() != null) {
            VoIPService.getSharedInstance().handleNotificationAction(intent);
            return;
        }
        String packageName = context.getPackageName();
        if ((packageName + ".END_CALL").equals(intent.getAction())) {
            i = 1;
        } else {
            if (!(packageName + ".DECLINE_CALL").equals(intent.getAction())) {
                if ((packageName + ".ANSWER_CALL").equals(intent.getAction())) {
                    VoIPPreNotificationService.answer(context);
                    return;
                }
                if ((packageName + ".HIDE_CALL").equals(intent.getAction())) {
                    VoIPPreNotificationService.dismiss(context, false);
                    return;
                }
                return;
            }
            i = 4;
        }
        VoIPPreNotificationService.decline(context, i);
    }
}
