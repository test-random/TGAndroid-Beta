package org.telegram.messenger;

import java.util.ArrayList;
import org.telegram.messenger.DocumentObject;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.web.WebInstantView;

public class ImageLocation {
    public static final int TYPE_BIG = 0;
    public static final int TYPE_SMALL = 1;
    public static final int TYPE_STRIPPED = 2;
    public static final int TYPE_VIDEO_BIG = 4;
    public static final int TYPE_VIDEO_SMALL = 3;
    public long access_hash;
    public long currentSize;
    public int dc_id;
    public TLRPC.Document document;
    public long documentId;
    public byte[] file_reference;
    public int imageType;
    public WebInstantView.WebPhoto instantFile;
    public byte[] iv;
    public byte[] key;
    public TLRPC.TL_fileLocationToBeDeprecated location;
    public String path;
    public TLRPC.Photo photo;
    public long photoId;
    public TLRPC.InputPeer photoPeer;
    public int photoPeerType;
    public TLRPC.PhotoSize photoSize;
    public SecureDocument secureDocument;
    public TLRPC.InputStickerSet stickerSet;
    public String thumbSize;
    public int thumbVersion;
    public long videoSeekTo;
    public WebFile webFile;

    public static ImageLocation getForChat(TLRPC.Chat chat, int i) {
        TLRPC.ChatPhoto chatPhoto;
        TLRPC.InputPeer tL_inputPeerChat;
        if (chat == null || (chatPhoto = chat.photo) == null) {
            return null;
        }
        if (i == 2) {
            if (chatPhoto.stripped_thumb == null) {
                return null;
            }
            ImageLocation imageLocation = new ImageLocation();
            TLRPC.TL_photoStrippedSize tL_photoStrippedSize = new TLRPC.TL_photoStrippedSize();
            imageLocation.photoSize = tL_photoStrippedSize;
            tL_photoStrippedSize.type = "s";
            tL_photoStrippedSize.bytes = chat.photo.stripped_thumb;
            return imageLocation;
        }
        TLRPC.FileLocation fileLocation = i == 0 ? chatPhoto.photo_big : chatPhoto.photo_small;
        if (fileLocation == null) {
            return null;
        }
        if (!ChatObject.isChannel(chat)) {
            tL_inputPeerChat = new TLRPC.TL_inputPeerChat();
            tL_inputPeerChat.chat_id = chat.id;
        } else {
            if (chat.access_hash == 0) {
                return null;
            }
            tL_inputPeerChat = new TLRPC.TL_inputPeerChannel();
            tL_inputPeerChat.channel_id = chat.id;
            tL_inputPeerChat.access_hash = chat.access_hash;
        }
        TLRPC.InputPeer inputPeer = tL_inputPeerChat;
        int i2 = chat.photo.dc_id;
        if (i2 == 0) {
            i2 = fileLocation.dc_id;
        }
        ImageLocation forPhoto = getForPhoto(fileLocation, 0, null, null, inputPeer, i, i2, null, null);
        forPhoto.photoId = chat.photo.photo_id;
        return forPhoto;
    }

    public static ImageLocation getForDocument(TLRPC.Document document) {
        if (document == null) {
            return null;
        }
        ImageLocation imageLocation = new ImageLocation();
        imageLocation.document = document;
        imageLocation.key = document.key;
        imageLocation.iv = document.iv;
        imageLocation.currentSize = document.size;
        return imageLocation;
    }

    public static ImageLocation getForDocument(TLRPC.PhotoSize photoSize, TLRPC.Document document) {
        if ((photoSize instanceof TLRPC.TL_photoStrippedSize) || (photoSize instanceof TLRPC.TL_photoPathSize)) {
            ImageLocation imageLocation = new ImageLocation();
            imageLocation.photoSize = photoSize;
            return imageLocation;
        }
        if (photoSize == null || document == null) {
            return null;
        }
        return getForPhoto(photoSize.location, photoSize.size, null, document, null, 1, document.dc_id, null, photoSize.type);
    }

    public static ImageLocation getForDocument(TLRPC.VideoSize videoSize, TLRPC.Document document) {
        if (videoSize == null || document == null) {
            return null;
        }
        ImageLocation forPhoto = getForPhoto(videoSize.location, videoSize.size, null, document, null, 1, document.dc_id, null, videoSize.type);
        forPhoto.imageType = "f".equals(videoSize.type) ? 1 : 2;
        return forPhoto;
    }

    public static ImageLocation getForInstantFile(WebInstantView.WebPhoto webPhoto) {
        if (webPhoto == null) {
            return null;
        }
        ImageLocation imageLocation = new ImageLocation();
        imageLocation.instantFile = webPhoto;
        return imageLocation;
    }

    public static ImageLocation getForLocal(TLRPC.FileLocation fileLocation) {
        if (fileLocation == null) {
            return null;
        }
        ImageLocation imageLocation = new ImageLocation();
        TLRPC.TL_fileLocationToBeDeprecated tL_fileLocationToBeDeprecated = new TLRPC.TL_fileLocationToBeDeprecated();
        imageLocation.location = tL_fileLocationToBeDeprecated;
        tL_fileLocationToBeDeprecated.local_id = fileLocation.local_id;
        tL_fileLocationToBeDeprecated.volume_id = fileLocation.volume_id;
        tL_fileLocationToBeDeprecated.secret = fileLocation.secret;
        tL_fileLocationToBeDeprecated.dc_id = fileLocation.dc_id;
        return imageLocation;
    }

    public static ImageLocation getForMessage(TLRPC.PhotoSize photoSize, TLRPC.Message message) {
        if (!(photoSize instanceof TLRPC.TL_photoStrippedSize) && !(photoSize instanceof TLRPC.TL_photoPathSize)) {
            return null;
        }
        ImageLocation imageLocation = new ImageLocation();
        imageLocation.photoSize = photoSize;
        return imageLocation;
    }

    public static ImageLocation getForObject(TLRPC.PhotoSize photoSize, TLObject tLObject) {
        if (tLObject instanceof TLRPC.Photo) {
            return getForPhoto(photoSize, (TLRPC.Photo) tLObject);
        }
        if (tLObject instanceof TLRPC.Document) {
            return getForDocument(photoSize, (TLRPC.Document) tLObject);
        }
        if (tLObject instanceof TLRPC.Message) {
            return getForMessage(photoSize, (TLRPC.Message) tLObject);
        }
        return null;
    }

    public static ImageLocation getForPath(String str) {
        if (str == null) {
            return null;
        }
        ImageLocation imageLocation = new ImageLocation();
        imageLocation.path = str;
        return imageLocation;
    }

    private static ImageLocation getForPhoto(TLRPC.FileLocation fileLocation, int i, TLRPC.Photo photo, TLRPC.Document document, TLRPC.InputPeer inputPeer, int i2, int i3, TLRPC.InputStickerSet inputStickerSet, String str) {
        if (fileLocation == null) {
            return null;
        }
        if (photo == null && inputPeer == null && inputStickerSet == null && document == null) {
            return null;
        }
        ImageLocation imageLocation = new ImageLocation();
        imageLocation.dc_id = i3;
        imageLocation.photo = photo;
        imageLocation.currentSize = i;
        imageLocation.photoPeer = inputPeer;
        imageLocation.photoPeerType = i2;
        imageLocation.stickerSet = inputStickerSet;
        if (fileLocation instanceof TLRPC.TL_fileLocationToBeDeprecated) {
            imageLocation.location = (TLRPC.TL_fileLocationToBeDeprecated) fileLocation;
            if (photo != null) {
                imageLocation.file_reference = photo.file_reference;
                imageLocation.access_hash = photo.access_hash;
                imageLocation.photoId = photo.id;
            } else if (document != null) {
                imageLocation.file_reference = document.file_reference;
                imageLocation.access_hash = document.access_hash;
                imageLocation.documentId = document.id;
            }
            imageLocation.thumbSize = str;
        } else {
            TLRPC.TL_fileLocationToBeDeprecated tL_fileLocationToBeDeprecated = new TLRPC.TL_fileLocationToBeDeprecated();
            imageLocation.location = tL_fileLocationToBeDeprecated;
            tL_fileLocationToBeDeprecated.local_id = fileLocation.local_id;
            tL_fileLocationToBeDeprecated.volume_id = fileLocation.volume_id;
            tL_fileLocationToBeDeprecated.secret = fileLocation.secret;
            imageLocation.dc_id = fileLocation.dc_id;
            imageLocation.file_reference = fileLocation.file_reference;
            imageLocation.key = fileLocation.key;
            imageLocation.iv = fileLocation.iv;
            imageLocation.access_hash = fileLocation.secret;
        }
        return imageLocation;
    }

    public static ImageLocation getForPhoto(TLRPC.PhotoSize photoSize, TLRPC.Photo photo) {
        if ((photoSize instanceof TLRPC.TL_photoStrippedSize) || (photoSize instanceof TLRPC.TL_photoPathSize)) {
            ImageLocation imageLocation = new ImageLocation();
            imageLocation.photoSize = photoSize;
            return imageLocation;
        }
        if (photoSize == null || photo == null) {
            return null;
        }
        int i = photo.dc_id;
        if (i == 0) {
            i = photoSize.location.dc_id;
        }
        return getForPhoto(photoSize.location, photoSize.size, photo, null, null, 1, i, null, photoSize.type);
    }

    public static ImageLocation getForPhoto(TLRPC.VideoSize videoSize, TLRPC.Photo photo) {
        if (videoSize == null || photo == null) {
            return null;
        }
        ImageLocation forPhoto = getForPhoto(videoSize.location, videoSize.size, photo, null, null, 1, photo.dc_id, null, videoSize.type);
        forPhoto.imageType = 2;
        if ((videoSize.flags & 1) != 0) {
            forPhoto.videoSeekTo = (int) (videoSize.video_start_ts * 1000.0d);
        }
        return forPhoto;
    }

    public static ImageLocation getForSecureDocument(SecureDocument secureDocument) {
        if (secureDocument == null) {
            return null;
        }
        ImageLocation imageLocation = new ImageLocation();
        imageLocation.secureDocument = secureDocument;
        return imageLocation;
    }

    public static ImageLocation getForSticker(TLRPC.PhotoSize photoSize, TLRPC.Document document, int i) {
        TLRPC.InputStickerSet inputStickerSet;
        int i2;
        if ((photoSize instanceof TLRPC.TL_photoStrippedSize) || (photoSize instanceof TLRPC.TL_photoPathSize)) {
            ImageLocation imageLocation = new ImageLocation();
            imageLocation.photoSize = photoSize;
            return imageLocation;
        }
        if (photoSize == null || document == null || (inputStickerSet = MediaDataController.getInputStickerSet(document)) == null) {
            return null;
        }
        ImageLocation forPhoto = getForPhoto(photoSize.location, photoSize.size, null, null, null, 1, document.dc_id, inputStickerSet, photoSize.type);
        if (!photoSize.type.equalsIgnoreCase("a")) {
            i2 = photoSize.type.equalsIgnoreCase("v") ? 2 : 1;
            forPhoto.thumbVersion = i;
            return forPhoto;
        }
        forPhoto.imageType = i2;
        forPhoto.thumbVersion = i;
        return forPhoto;
    }

    public static ImageLocation getForStickerSet(TLRPC.StickerSet stickerSet) {
        TLRPC.PhotoSize closestPhotoSizeWithSize;
        TLRPC.InputStickerSet tL_inputStickerSetShortName;
        if (stickerSet == null || (closestPhotoSizeWithSize = FileLoader.getClosestPhotoSizeWithSize(stickerSet.thumbs, 90)) == null) {
            return null;
        }
        if (stickerSet.access_hash != 0) {
            tL_inputStickerSetShortName = new TLRPC.TL_inputStickerSetID();
            tL_inputStickerSetShortName.id = stickerSet.id;
            tL_inputStickerSetShortName.access_hash = stickerSet.access_hash;
        } else {
            tL_inputStickerSetShortName = new TLRPC.TL_inputStickerSetShortName();
            tL_inputStickerSetShortName.short_name = stickerSet.short_name;
        }
        TLRPC.InputStickerSet inputStickerSet = tL_inputStickerSetShortName;
        TLRPC.FileLocation fileLocation = closestPhotoSizeWithSize.location;
        return getForPhoto(fileLocation, closestPhotoSizeWithSize.size, null, null, null, 1, fileLocation.dc_id, inputStickerSet, closestPhotoSizeWithSize.type);
    }

    public static ImageLocation getForUser(TLRPC.User user, int i) {
        TLRPC.UserProfilePhoto userProfilePhoto;
        TLRPC.UserFull userFull;
        TLRPC.Photo photo;
        ArrayList<TLRPC.VideoSize> arrayList;
        TLRPC.VideoSize closestVideoSizeWithSize;
        if (user != null && user.access_hash != 0 && (userProfilePhoto = user.photo) != null) {
            if (i != 4 && i != 3) {
                if (i == 2) {
                    if (userProfilePhoto.stripped_thumb == null) {
                        return null;
                    }
                    ImageLocation imageLocation = new ImageLocation();
                    TLRPC.TL_photoStrippedSize tL_photoStrippedSize = new TLRPC.TL_photoStrippedSize();
                    imageLocation.photoSize = tL_photoStrippedSize;
                    tL_photoStrippedSize.type = "s";
                    tL_photoStrippedSize.bytes = user.photo.stripped_thumb;
                    return imageLocation;
                }
                TLRPC.FileLocation fileLocation = i == 0 ? userProfilePhoto.photo_big : userProfilePhoto.photo_small;
                if (fileLocation == null) {
                    return null;
                }
                TLRPC.TL_inputPeerUser tL_inputPeerUser = new TLRPC.TL_inputPeerUser();
                tL_inputPeerUser.user_id = user.id;
                tL_inputPeerUser.access_hash = user.access_hash;
                int i2 = user.photo.dc_id;
                if (i2 == 0) {
                    i2 = fileLocation.dc_id;
                }
                ImageLocation forPhoto = getForPhoto(fileLocation, 0, null, null, tL_inputPeerUser, i, i2, null, null);
                forPhoto.photoId = user.photo.photo_id;
                return forPhoto;
            }
            int i3 = UserConfig.selectedAccount;
            if (MessagesController.getInstance(i3).isPremiumUser(user) && user.photo.has_video && (userFull = MessagesController.getInstance(i3).getUserFull(user.id)) != null && (photo = userFull.profile_photo) != null && (arrayList = photo.video_sizes) != null && !arrayList.isEmpty()) {
                if (i == 4) {
                    closestVideoSizeWithSize = FileLoader.getClosestVideoSizeWithSize(userFull.profile_photo.video_sizes, 1000);
                } else {
                    closestVideoSizeWithSize = FileLoader.getClosestVideoSizeWithSize(userFull.profile_photo.video_sizes, 100);
                    int i4 = 0;
                    while (true) {
                        if (i4 >= userFull.profile_photo.video_sizes.size()) {
                            break;
                        }
                        if ("p".equals(userFull.profile_photo.video_sizes.get(i4).type)) {
                            closestVideoSizeWithSize = userFull.profile_photo.video_sizes.get(i4);
                            break;
                        }
                        i4++;
                    }
                }
                return getForPhoto(closestVideoSizeWithSize, userFull.profile_photo);
            }
        }
        return null;
    }

    public static ImageLocation getForUserOrChat(TLObject tLObject, int i) {
        if (tLObject instanceof TLRPC.User) {
            return getForUser((TLRPC.User) tLObject, i);
        }
        if (tLObject instanceof TLRPC.Chat) {
            return getForChat((TLRPC.Chat) tLObject, i);
        }
        return null;
    }

    public static ImageLocation getForVideoPath(String str) {
        if (str == null) {
            return null;
        }
        ImageLocation imageLocation = new ImageLocation();
        imageLocation.path = str;
        imageLocation.imageType = 2;
        return imageLocation;
    }

    public static ImageLocation getForWebFile(WebFile webFile) {
        if (webFile == null) {
            return null;
        }
        ImageLocation imageLocation = new ImageLocation();
        imageLocation.webFile = webFile;
        imageLocation.currentSize = webFile.size;
        return imageLocation;
    }

    public static String getStrippedKey(Object obj, Object obj2, Object obj3) {
        if ((obj instanceof TLRPC.WebPage) || ((obj instanceof MessageObject) && ((MessageObject) obj).type == 29)) {
            if (obj2 instanceof ImageLocation) {
                ImageLocation imageLocation = (ImageLocation) obj2;
                Object obj4 = imageLocation.document;
                if (obj4 == null && (obj4 = imageLocation.photoSize) == null) {
                    TLRPC.Photo photo = imageLocation.photo;
                    if (photo != null) {
                        obj2 = photo;
                    }
                } else {
                    obj2 = obj4;
                }
            }
            if (obj2 == null) {
                return "stripped" + FileRefController.getKeyForParentObject(obj) + "_" + obj3;
            }
            if (obj2 instanceof TLRPC.Document) {
                return "stripped" + FileRefController.getKeyForParentObject(obj) + "_" + ((TLRPC.Document) obj2).id;
            }
            if (obj2 instanceof TLRPC.Photo) {
                return "stripped" + FileRefController.getKeyForParentObject(obj) + "_" + ((TLRPC.Photo) obj2).id;
            }
            if (obj2 instanceof TLRPC.PhotoSize) {
                TLRPC.PhotoSize photoSize = (TLRPC.PhotoSize) obj2;
                if (photoSize.location == null) {
                    return "stripped" + FileRefController.getKeyForParentObject(obj);
                }
                return "stripped" + FileRefController.getKeyForParentObject(obj) + "_" + photoSize.location.local_id + "_" + photoSize.location.volume_id;
            }
            if (obj2 instanceof TLRPC.FileLocation) {
                TLRPC.FileLocation fileLocation = (TLRPC.FileLocation) obj2;
                return "stripped" + FileRefController.getKeyForParentObject(obj) + "_" + fileLocation.local_id + "_" + fileLocation.volume_id;
            }
        }
        return "stripped" + FileRefController.getKeyForParentObject(obj);
    }

    public String getKey(Object obj, Object obj2, boolean z) {
        if (this.secureDocument != null) {
            return this.secureDocument.secureFile.dc_id + "_" + this.secureDocument.secureFile.id;
        }
        TLRPC.PhotoSize photoSize = this.photoSize;
        if ((photoSize instanceof TLRPC.TL_photoStrippedSize) || (photoSize instanceof TLRPC.TL_photoPathSize)) {
            if (photoSize.bytes.length <= 0) {
                return null;
            }
            if (obj2 == null) {
                obj2 = this;
            }
            return getStrippedKey(obj, obj2, photoSize);
        }
        if (this.location != null) {
            return this.location.volume_id + "_" + this.location.local_id;
        }
        WebFile webFile = this.webFile;
        if (webFile != null) {
            return Utilities.MD5(webFile.url);
        }
        WebInstantView.WebPhoto webPhoto = this.instantFile;
        if (webPhoto != null) {
            return Utilities.MD5(webPhoto.url);
        }
        TLRPC.Document document = this.document;
        if (document == null) {
            String str = this.path;
            if (str != null) {
                return Utilities.MD5(str);
            }
            return null;
        }
        if (z || !(document instanceof DocumentObject.ThemeDocument)) {
            if (document.id == 0 || document.dc_id == 0) {
                return null;
            }
            return this.document.dc_id + "_" + this.document.id;
        }
        DocumentObject.ThemeDocument themeDocument = (DocumentObject.ThemeDocument) document;
        StringBuilder sb = new StringBuilder();
        sb.append(this.document.dc_id);
        sb.append("_");
        sb.append(this.document.id);
        sb.append("_");
        sb.append(Theme.getBaseThemeKey(themeDocument.themeSettings));
        sb.append("_");
        sb.append(themeDocument.themeSettings.accent_color);
        sb.append("_");
        sb.append(themeDocument.themeSettings.message_colors.size() > 1 ? themeDocument.themeSettings.message_colors.get(1).intValue() : 0);
        sb.append("_");
        sb.append(themeDocument.themeSettings.message_colors.size() > 0 ? themeDocument.themeSettings.message_colors.get(0).intValue() : 0);
        return sb.toString();
    }

    public long getSize() {
        int i;
        TLRPC.PhotoSize photoSize = this.photoSize;
        if (photoSize == null) {
            SecureDocument secureDocument = this.secureDocument;
            if (secureDocument != null) {
                TLRPC.TL_secureFile tL_secureFile = secureDocument.secureFile;
                if (tL_secureFile != null) {
                    return tL_secureFile.size;
                }
            } else {
                TLRPC.Document document = this.document;
                if (document != null) {
                    return document.size;
                }
                WebFile webFile = this.webFile;
                if (webFile != null) {
                    i = webFile.size;
                }
            }
            return this.currentSize;
        }
        i = photoSize.size;
        return i;
    }

    public boolean isEncrypted() {
        return this.key != null;
    }
}
