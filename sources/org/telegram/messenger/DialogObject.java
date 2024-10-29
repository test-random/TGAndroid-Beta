package org.telegram.messenger;

import android.text.TextUtils;
import java.util.ArrayList;
import java.util.Iterator;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.Components.BackupImageView;

public class DialogObject {
    public static int editDistance(String str, String str2) {
        String lowerCase = str.toLowerCase();
        String lowerCase2 = str2.toLowerCase();
        int[] iArr = new int[lowerCase2.length() + 1];
        for (int i = 0; i <= lowerCase.length(); i++) {
            int i2 = i;
            for (int i3 = 0; i3 <= lowerCase2.length(); i3++) {
                if (i == 0) {
                    iArr[i3] = i3;
                } else if (i3 > 0) {
                    int i4 = i3 - 1;
                    int i5 = iArr[i4];
                    if (lowerCase.charAt(i - 1) != lowerCase2.charAt(i4)) {
                        i5 = Math.min(Math.min(i5, i2), iArr[i3]) + 1;
                    }
                    iArr[i4] = i2;
                    i2 = i5;
                }
            }
            if (i > 0) {
                iArr[lowerCase2.length()] = i2;
            }
        }
        return iArr[lowerCase2.length()];
    }

    public static boolean emojiStatusesEqual(TLRPC.EmojiStatus emojiStatus, TLRPC.EmojiStatus emojiStatus2) {
        return getEmojiStatusDocumentId(emojiStatus) == getEmojiStatusDocumentId(emojiStatus2) && getEmojiStatusUntil(emojiStatus) == getEmojiStatusUntil(emojiStatus2);
    }

    public static TLRPC.TL_username findUsername(String str, ArrayList<TLRPC.TL_username> arrayList) {
        if (arrayList == null) {
            return null;
        }
        Iterator<TLRPC.TL_username> it = arrayList.iterator();
        while (it.hasNext()) {
            TLRPC.TL_username next = it.next();
            if (next != null && TextUtils.equals(next.username, str)) {
                return next;
            }
        }
        return null;
    }

    public static TLRPC.TL_username findUsername(String str, TLRPC.Chat chat) {
        if (chat == null) {
            return null;
        }
        return findUsername(str, chat.usernames);
    }

    public static TLRPC.TL_username findUsername(String str, TLRPC.User user) {
        if (user == null) {
            return null;
        }
        return findUsername(str, user.usernames);
    }

    public static String getDialogTitle(TLObject tLObject) {
        return setDialogPhotoTitle(null, null, tLObject);
    }

    public static long getEmojiStatusDocumentId(TLRPC.EmojiStatus emojiStatus) {
        if (MessagesController.getInstance(UserConfig.selectedAccount).premiumFeaturesBlocked()) {
            return 0L;
        }
        if (emojiStatus instanceof TLRPC.TL_emojiStatus) {
            return ((TLRPC.TL_emojiStatus) emojiStatus).document_id;
        }
        if (emojiStatus instanceof TLRPC.TL_emojiStatusUntil) {
            TLRPC.TL_emojiStatusUntil tL_emojiStatusUntil = (TLRPC.TL_emojiStatusUntil) emojiStatus;
            if (tL_emojiStatusUntil.until > ((int) (System.currentTimeMillis() / 1000))) {
                return tL_emojiStatusUntil.document_id;
            }
        }
        return 0L;
    }

    public static int getEmojiStatusUntil(TLRPC.EmojiStatus emojiStatus) {
        if (!(emojiStatus instanceof TLRPC.TL_emojiStatusUntil)) {
            return 0;
        }
        TLRPC.TL_emojiStatusUntil tL_emojiStatusUntil = (TLRPC.TL_emojiStatusUntil) emojiStatus;
        if (tL_emojiStatusUntil.until > ((int) (System.currentTimeMillis() / 1000))) {
            return tL_emojiStatusUntil.until;
        }
        return 0;
    }

    public static int getEncryptedChatId(long j) {
        return (int) (j & 4294967295L);
    }

    public static int getFolderId(long j) {
        return (int) j;
    }

    public static long getLastMessageOrDraftDate(TLRPC.Dialog dialog, TLRPC.DraftMessage draftMessage) {
        int i;
        return (draftMessage == null || (i = draftMessage.date) < dialog.last_message_date) ? dialog.last_message_date : i;
    }

    public static long getPeerDialogId(TLRPC.InputPeer inputPeer) {
        if (inputPeer == null) {
            return 0L;
        }
        long j = inputPeer.user_id;
        if (j != 0) {
            return j;
        }
        long j2 = inputPeer.chat_id;
        return j2 != 0 ? -j2 : -inputPeer.channel_id;
    }

    public static long getPeerDialogId(TLRPC.Peer peer) {
        if (peer == null) {
            return 0L;
        }
        long j = peer.user_id;
        if (j != 0) {
            return j;
        }
        long j2 = peer.chat_id;
        return j2 != 0 ? -j2 : -peer.channel_id;
    }

    public static String getPublicUsername(String str, ArrayList<TLRPC.TL_username> arrayList, boolean z) {
        if (!TextUtils.isEmpty(str) && !z) {
            return str;
        }
        if (arrayList != null) {
            for (int i = 0; i < arrayList.size(); i++) {
                TLRPC.TL_username tL_username = arrayList.get(i);
                if (tL_username != null && (((tL_username.active && !z) || tL_username.editable) && !TextUtils.isEmpty(tL_username.username))) {
                    return tL_username.username;
                }
            }
        }
        if (TextUtils.isEmpty(str) || !z) {
            return null;
        }
        if (arrayList == null || arrayList.size() <= 0) {
            return str;
        }
        return null;
    }

    public static String getPublicUsername(TLObject tLObject) {
        String str;
        ArrayList<TLRPC.TL_username> arrayList;
        if (tLObject instanceof TLRPC.Chat) {
            TLRPC.Chat chat = (TLRPC.Chat) tLObject;
            str = chat.username;
            arrayList = chat.usernames;
        } else {
            if (!(tLObject instanceof TLRPC.User)) {
                return null;
            }
            TLRPC.User user = (TLRPC.User) tLObject;
            str = user.username;
            arrayList = user.usernames;
        }
        return getPublicUsername(str, arrayList, false);
    }

    public static String getPublicUsername(TLObject tLObject, String str) {
        if (tLObject instanceof TLRPC.Chat) {
            TLRPC.Chat chat = (TLRPC.Chat) tLObject;
            return str == null ? getPublicUsername(chat.username, chat.usernames, false) : getSimilarPublicUsername(chat.username, chat.usernames, str);
        }
        if (!(tLObject instanceof TLRPC.User)) {
            return null;
        }
        TLRPC.User user = (TLRPC.User) tLObject;
        return str == null ? getPublicUsername(user.username, user.usernames, false) : getSimilarPublicUsername(user.username, user.usernames, str);
    }

    public static String getSimilarPublicUsername(String str, ArrayList<TLRPC.TL_username> arrayList, String str2) {
        double d = -1.0d;
        String str3 = null;
        if (arrayList != null) {
            for (int i = 0; i < arrayList.size(); i++) {
                TLRPC.TL_username tL_username = arrayList.get(i);
                if (tL_username != null && tL_username.active && !TextUtils.isEmpty(tL_username.username)) {
                    double similarity = d < 0.0d ? 0.0d : similarity(tL_username.username, str2);
                    if (similarity > d) {
                        str3 = tL_username.username;
                        d = similarity;
                    }
                }
            }
        }
        if (!TextUtils.isEmpty(str)) {
            if ((d >= 0.0d ? similarity(str, str2) : 0.0d) > d) {
                return str;
            }
        }
        return str3;
    }

    public static void initDialog(TLRPC.Dialog dialog) {
        long makeFolderDialogId;
        if (dialog == null || dialog.id != 0) {
            return;
        }
        if (dialog instanceof TLRPC.TL_dialog) {
            TLRPC.Peer peer = dialog.peer;
            if (peer == null) {
                return;
            }
            long j = peer.user_id;
            if (j != 0) {
                dialog.id = j;
                return;
            } else {
                long j2 = peer.chat_id;
                makeFolderDialogId = j2 != 0 ? -j2 : -peer.channel_id;
            }
        } else if (!(dialog instanceof TLRPC.TL_dialogFolder)) {
            return;
        } else {
            makeFolderDialogId = makeFolderDialogId(((TLRPC.TL_dialogFolder) dialog).folder.id);
        }
        dialog.id = makeFolderDialogId;
    }

    public static boolean isChannel(TLRPC.Dialog dialog) {
        return (dialog == null || (dialog.flags & 1) == 0) ? false : true;
    }

    public static boolean isChatDialog(long j) {
        return (isEncryptedDialog(j) || isFolderDialogId(j) || j >= 0) ? false : true;
    }

    public static boolean isEncryptedDialog(long j) {
        return (4611686018427387904L & j) != 0 && (j & Long.MIN_VALUE) == 0;
    }

    public static boolean isFolderDialogId(long j) {
        return (2305843009213693952L & j) != 0 && (j & Long.MIN_VALUE) == 0;
    }

    public static boolean isUserDialog(long j) {
        return (isEncryptedDialog(j) || isFolderDialogId(j) || j <= 0) ? false : true;
    }

    public static long makeEncryptedDialogId(long j) {
        return (j & 4294967295L) | 4611686018427387904L;
    }

    public static long makeFolderDialogId(int i) {
        return i | 2305843009213693952L;
    }

    public static java.lang.String setDialogPhotoTitle(org.telegram.messenger.ImageReceiver r2, org.telegram.ui.Components.AvatarDrawable r3, org.telegram.tgnet.TLObject r4) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.DialogObject.setDialogPhotoTitle(org.telegram.messenger.ImageReceiver, org.telegram.ui.Components.AvatarDrawable, org.telegram.tgnet.TLObject):java.lang.String");
    }

    public static String setDialogPhotoTitle(BackupImageView backupImageView, TLObject tLObject) {
        return backupImageView != null ? setDialogPhotoTitle(backupImageView.getImageReceiver(), backupImageView.getAvatarDrawable(), tLObject) : setDialogPhotoTitle(null, null, tLObject);
    }

    public static double similarity(String str, String str2) {
        if (str.length() < str2.length()) {
            str2 = str;
            str = str2;
        }
        int length = str.length();
        if (length == 0) {
            return 1.0d;
        }
        double editDistance = length - editDistance(str, str2);
        double d = length;
        Double.isNaN(editDistance);
        Double.isNaN(d);
        return editDistance / d;
    }
}
