package org.telegram.messenger;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.SystemClock;
import android.text.TextUtils;

public class NotificationsDisabledReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        SharedPreferences.Editor putBoolean;
        if ("android.app.action.NOTIFICATION_CHANNEL_BLOCK_STATE_CHANGED".equals(intent.getAction())) {
            String stringExtra = intent.getStringExtra("android.app.extra.NOTIFICATION_CHANNEL_ID");
            boolean booleanExtra = intent.getBooleanExtra("android.app.extra.BLOCKED_STATE", false);
            if (TextUtils.isEmpty(stringExtra) || stringExtra.contains("_ia_")) {
                return;
            }
            String[] split = stringExtra.split("_");
            if (split.length < 3) {
                return;
            }
            ApplicationLoader.postInitApplication();
            int intValue = Utilities.parseInt((CharSequence) split[0]).intValue();
            if (intValue < 0 || intValue >= 4) {
                return;
            }
            if (BuildVars.LOGS_ENABLED) {
                FileLog.d("received disabled notification channel event for " + stringExtra + " state = " + booleanExtra);
            }
            if (SystemClock.elapsedRealtime() - AccountInstance.getInstance(intValue).getNotificationsController().lastNotificationChannelCreateTime <= 1000) {
                if (BuildVars.LOGS_ENABLED) {
                    FileLog.d("received disable notification event right after creating notification channel, ignoring");
                    return;
                }
                return;
            }
            SharedPreferences notificationsSettings = AccountInstance.getInstance(intValue).getNotificationsSettings();
            if (split[1].startsWith("channel")) {
                if (!stringExtra.equals(notificationsSettings.getString("channels", null))) {
                    return;
                }
                if (BuildVars.LOGS_ENABLED) {
                    FileLog.d("apply channel{channel} " + stringExtra + " state");
                }
                notificationsSettings.edit().putInt(NotificationsController.getGlobalNotificationsKey(2), booleanExtra ? Integer.MAX_VALUE : 0).commit();
                AccountInstance.getInstance(intValue).getNotificationsController().updateServerNotificationsSettings(2);
            } else if (!split[1].startsWith("groups")) {
                if (split[1].startsWith("private")) {
                    if (!stringExtra.equals(notificationsSettings.getString("private", null))) {
                        return;
                    }
                    if (BuildVars.LOGS_ENABLED) {
                        FileLog.d("apply channel{private} " + stringExtra + " state");
                    }
                    putBoolean = notificationsSettings.edit().putInt(NotificationsController.getGlobalNotificationsKey(1), booleanExtra ? Integer.MAX_VALUE : 0);
                } else if (!split[1].startsWith("stories")) {
                    long longValue = Utilities.parseLong(split[1]).longValue();
                    if (longValue == 0) {
                        return;
                    }
                    String sharedPrefKey = NotificationsController.getSharedPrefKey(longValue, 0);
                    if (!stringExtra.equals(notificationsSettings.getString("org.telegram.key" + longValue, null))) {
                        return;
                    }
                    if (BuildVars.LOGS_ENABLED) {
                        FileLog.d("apply channel{else} " + stringExtra + " state");
                    }
                    SharedPreferences.Editor edit = notificationsSettings.edit();
                    edit.putInt("notify2_" + sharedPrefKey, booleanExtra ? 2 : 0);
                    if (!booleanExtra) {
                        edit.remove("notifyuntil_" + sharedPrefKey);
                    }
                    edit.commit();
                    AccountInstance.getInstance(intValue).getNotificationsController().updateServerNotificationsSettings(longValue, 0L, true);
                } else {
                    if (!stringExtra.equals(notificationsSettings.getString("stories", null))) {
                        return;
                    }
                    if (BuildVars.LOGS_ENABLED) {
                        FileLog.d("apply channel{stories} " + stringExtra + " state");
                    }
                    putBoolean = notificationsSettings.edit().putBoolean(NotificationsController.getGlobalNotificationsKey(3), !booleanExtra);
                }
                putBoolean.commit();
                AccountInstance.getInstance(intValue).getNotificationsController().updateServerNotificationsSettings(1);
            } else {
                if (!stringExtra.equals(notificationsSettings.getString("groups", null))) {
                    return;
                }
                if (BuildVars.LOGS_ENABLED) {
                    FileLog.d("apply channel{groups} " + stringExtra + " state");
                }
                notificationsSettings.edit().putInt(NotificationsController.getGlobalNotificationsKey(0), booleanExtra ? Integer.MAX_VALUE : 0).commit();
                AccountInstance.getInstance(intValue).getNotificationsController().updateServerNotificationsSettings(0);
            }
            AccountInstance.getInstance(intValue).getConnectionsManager().resumeNetworkMaybe();
        }
    }
}
