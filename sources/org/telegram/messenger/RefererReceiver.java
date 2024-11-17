package org.telegram.messenger;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class RefererReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        try {
            MessagesController.getInstance(UserConfig.selectedAccount).setReferer(intent.getExtras().getString("referrer"));
        } catch (Exception unused) {
        }
    }
}
