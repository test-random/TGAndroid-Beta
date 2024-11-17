package org.telegram.ui.Stories;

import android.text.TextUtils;
import org.telegram.messenger.ChatObject;
import org.telegram.messenger.MessagesController;
import org.telegram.tgnet.TLRPC;

public abstract class ChannelBoostUtilities {
    public static String createLink(int i, long j) {
        long j2 = -j;
        TLRPC.Chat chat = MessagesController.getInstance(i).getChat(Long.valueOf(j2));
        if (TextUtils.isEmpty(ChatObject.getPublicUsername(chat))) {
            return "https://t.me/boost/?c=" + j2;
        }
        return "https://t.me/boost/" + ChatObject.getPublicUsername(chat);
    }
}
