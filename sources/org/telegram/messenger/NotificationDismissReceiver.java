package org.telegram.messenger;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

public class NotificationDismissReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        SharedPreferences.Editor edit;
        if (intent == null) {
            return;
        }
        int intExtra = intent.getIntExtra("currentAccount", UserConfig.selectedAccount);
        if (UserConfig.isValidAccount(intExtra)) {
            long longExtra = intent.getLongExtra("dialogId", 0L);
            int intExtra2 = intent.getIntExtra("messageDate", 0);
            if (intent.hasExtra("story") && intent.getBooleanExtra("story", false)) {
                NotificationsController.getInstance(intExtra).processIgnoreStories();
                return;
            }
            if (intent.hasExtra("storyReaction") && intent.getBooleanExtra("storyReaction", false)) {
                NotificationsController.getInstance(intExtra).processIgnoreStoryReactions();
                return;
            }
            String str = "dismissDate";
            if (longExtra == 0) {
                FileLog.d("set dismissDate of global to " + intExtra2);
                edit = MessagesController.getNotificationsSettings(intExtra).edit();
            } else {
                FileLog.d("set dismissDate of " + longExtra + " to " + intExtra2);
                edit = MessagesController.getNotificationsSettings(intExtra).edit();
                StringBuilder sb = new StringBuilder();
                sb.append("dismissDate");
                sb.append(longExtra);
                str = sb.toString();
            }
            edit.putInt(str, intExtra2).commit();
        }
    }
}
