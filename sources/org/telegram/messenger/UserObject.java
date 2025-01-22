package org.telegram.messenger;

import android.text.TextUtils;
import org.telegram.PhoneFormat.PhoneFormat;
import org.telegram.messenger.MessagesController;
import org.telegram.tgnet.TLRPC;

public class UserObject {
    public static final long ANONYMOUS = 2666000;
    public static final long REPLY_BOT = 1271266957;
    public static final long VERIFY = 489000;

    public static int getColorId(TLRPC.User user) {
        if (user == null) {
            return 0;
        }
        TLRPC.TL_peerColor tL_peerColor = user.color;
        return (tL_peerColor == null || (tL_peerColor.flags & 1) == 0) ? (int) (user.id % 7) : tL_peerColor.color;
    }

    public static long getEmojiId(TLRPC.User user) {
        TLRPC.TL_peerColor tL_peerColor;
        if (user == null || (tL_peerColor = user.color) == null || (tL_peerColor.flags & 2) == 0) {
            return 0L;
        }
        return tL_peerColor.background_emoji_id;
    }

    public static Long getEmojiStatusDocumentId(TLRPC.EmojiStatus emojiStatus) {
        long j;
        if (emojiStatus == null || MessagesController.getInstance(UserConfig.selectedAccount).premiumFeaturesBlocked()) {
            return null;
        }
        if (emojiStatus instanceof TLRPC.TL_emojiStatus) {
            TLRPC.TL_emojiStatus tL_emojiStatus = (TLRPC.TL_emojiStatus) emojiStatus;
            if ((tL_emojiStatus.flags & 1) != 0 && tL_emojiStatus.until <= ((int) (System.currentTimeMillis() / 1000))) {
                return null;
            }
            j = tL_emojiStatus.document_id;
        } else {
            if (!(emojiStatus instanceof TLRPC.TL_emojiStatusCollectible)) {
                return null;
            }
            TLRPC.TL_emojiStatusCollectible tL_emojiStatusCollectible = (TLRPC.TL_emojiStatusCollectible) emojiStatus;
            if ((tL_emojiStatusCollectible.flags & 1) != 0 && tL_emojiStatusCollectible.until <= ((int) (System.currentTimeMillis() / 1000))) {
                return null;
            }
            j = tL_emojiStatusCollectible.document_id;
        }
        return Long.valueOf(j);
    }

    public static Long getEmojiStatusDocumentId(TLRPC.User user) {
        if (user == null) {
            return null;
        }
        return getEmojiStatusDocumentId(user.emoji_status);
    }

    public static String getFirstName(TLRPC.User user) {
        return getFirstName(user, true);
    }

    public static String getFirstName(TLRPC.User user, boolean z) {
        if (user == null || isDeleted(user)) {
            return "DELETED";
        }
        String str = user.first_name;
        if (TextUtils.isEmpty(str)) {
            str = user.last_name;
        } else if (!z && str.length() <= 2) {
            return ContactsController.formatName(user.first_name, user.last_name);
        }
        return !TextUtils.isEmpty(str) ? str : LocaleController.getString(R.string.HiddenName);
    }

    public static String getForcedFirstName(TLRPC.User user) {
        if (user != null && !isDeleted(user)) {
            String str = user.first_name;
            if (TextUtils.isEmpty(str)) {
                str = user.last_name;
            }
            if (str != null) {
                int indexOf = str.indexOf(" ", 2);
                return indexOf >= 0 ? str.substring(0, indexOf) : str;
            }
        }
        return LocaleController.getString(R.string.HiddenName);
    }

    public static MessagesController.PeerColor getPeerColorForAvatar(int i, TLRPC.User user) {
        return null;
    }

    public static TLRPC.UserProfilePhoto getPhoto(TLRPC.User user) {
        if (hasPhoto(user)) {
            return user.photo;
        }
        return null;
    }

    public static long getProfileCollectibleId(TLRPC.User user) {
        if (user == null) {
            return 0L;
        }
        TLRPC.EmojiStatus emojiStatus = user.emoji_status;
        if (emojiStatus instanceof TLRPC.TL_emojiStatusCollectible) {
            return ((TLRPC.TL_emojiStatusCollectible) emojiStatus).collectible_id;
        }
        return 0L;
    }

    public static int getProfileColorId(TLRPC.User user) {
        if (user == null) {
            return 0;
        }
        TLRPC.TL_peerColor tL_peerColor = user.profile_color;
        if (tL_peerColor == null || (tL_peerColor.flags & 1) == 0) {
            return -1;
        }
        return tL_peerColor.color;
    }

    public static long getProfileEmojiId(TLRPC.User user) {
        TLRPC.TL_peerColor tL_peerColor;
        if (user != null) {
            TLRPC.EmojiStatus emojiStatus = user.emoji_status;
            if (emojiStatus instanceof TLRPC.TL_emojiStatusCollectible) {
                return ((TLRPC.TL_emojiStatusCollectible) emojiStatus).pattern_document_id;
            }
        }
        if (user == null || (tL_peerColor = user.profile_color) == null || (tL_peerColor.flags & 2) == 0) {
            return 0L;
        }
        return tL_peerColor.background_emoji_id;
    }

    public static String getPublicUsername(TLRPC.User user) {
        return getPublicUsername(user, false);
    }

    public static String getPublicUsername(TLRPC.User user, boolean z) {
        if (user == null) {
            return null;
        }
        if (!TextUtils.isEmpty(user.username)) {
            return user.username;
        }
        if (user.usernames != null) {
            for (int i = 0; i < user.usernames.size(); i++) {
                TLRPC.TL_username tL_username = user.usernames.get(i);
                if (tL_username != null && (((tL_username.active && !z) || tL_username.editable) && !TextUtils.isEmpty(tL_username.username))) {
                    return tL_username.username;
                }
            }
        }
        return null;
    }

    public static String getUserName(TLRPC.User user) {
        if (user == null || isDeleted(user)) {
            return LocaleController.getString(R.string.HiddenName);
        }
        String formatName = ContactsController.formatName(user.first_name, user.last_name);
        if (formatName.length() != 0 || TextUtils.isEmpty(user.phone)) {
            return formatName;
        }
        return PhoneFormat.getInstance().format("+" + user.phone);
    }

    public static boolean hasFallbackPhoto(TLRPC.UserFull userFull) {
        TLRPC.Photo photo;
        return (userFull == null || (photo = userFull.fallback_photo) == null || (photo instanceof TLRPC.TL_photoEmpty)) ? false : true;
    }

    public static boolean hasPhoto(TLRPC.User user) {
        TLRPC.UserProfilePhoto userProfilePhoto;
        return (user == null || (userProfilePhoto = user.photo) == null || (userProfilePhoto instanceof TLRPC.TL_userProfilePhotoEmpty)) ? false : true;
    }

    public static boolean hasPublicUsername(TLRPC.User user, String str) {
        if (user != null && str != null) {
            if (str.equalsIgnoreCase(user.username)) {
                return true;
            }
            if (user.usernames != null) {
                for (int i = 0; i < user.usernames.size(); i++) {
                    TLRPC.TL_username tL_username = user.usernames.get(i);
                    if (tL_username != null && tL_username.active && str.equalsIgnoreCase(tL_username.username)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public static boolean isAnonymous(TLRPC.User user) {
        return user != null && user.id == 2666000;
    }

    public static boolean isBot(TLRPC.User user) {
        return user != null && user.bot;
    }

    public static boolean isContact(TLRPC.User user) {
        return user != null && ((user instanceof TLRPC.TL_userContact_old2) || user.contact || user.mutual_contact);
    }

    public static boolean isDeleted(TLRPC.User user) {
        return user == null || (user instanceof TLRPC.TL_userDeleted_old2) || (user instanceof TLRPC.TL_userEmpty) || user.deleted;
    }

    public static boolean isReplyUser(long j) {
        return j == 708513 || j == 1271266957;
    }

    public static boolean isReplyUser(TLRPC.User user) {
        if (user != null) {
            long j = user.id;
            if (j == 708513 || j == 1271266957) {
                return true;
            }
        }
        return false;
    }

    public static boolean isService(long j) {
        return j == 333000 || j == 777000 || j == 42777;
    }

    public static boolean isUserSelf(TLRPC.User user) {
        return user != null && ((user instanceof TLRPC.TL_userSelf_old3) || user.self);
    }
}
