package org.telegram.messenger;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import androidx.core.app.RemoteInput;
import org.telegram.messenger.SendMessagesHelper;

public class AutoMessageReplyReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        ApplicationLoader.postInitApplication();
        Bundle resultsFromIntent = RemoteInput.getResultsFromIntent(intent);
        if (resultsFromIntent == null) {
            return;
        }
        CharSequence charSequence = resultsFromIntent.getCharSequence("extra_voice_reply");
        if (TextUtils.isEmpty(charSequence)) {
            return;
        }
        long longExtra = intent.getLongExtra("dialog_id", 0L);
        int intExtra = intent.getIntExtra("max_id", 0);
        int intExtra2 = intent.getIntExtra("currentAccount", 0);
        if (longExtra == 0 || intExtra == 0 || !UserConfig.isValidAccount(intExtra2)) {
            return;
        }
        SendMessagesHelper.getInstance(intExtra2).sendMessage(SendMessagesHelper.SendMessageParams.of(charSequence.toString(), longExtra, null, null, null, true, null, null, null, true, 0, null, false));
        MessagesController.getInstance(intExtra2).markDialogAsRead(longExtra, intExtra, intExtra, 0, false, 0L, 0, true, 0);
    }
}
