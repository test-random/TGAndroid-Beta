package org.telegram.messenger;

import android.content.SharedPreferences;
import android.util.LongSparseArray;
import org.telegram.messenger.FilePathDatabase;

public class SaveToGallerySettingsHelper {
    public static String CHANNELS_PREF_NAME = "channels_save_gallery_exceptions";
    public static final long DEFAULT_VIDEO_LIMIT = 104857600;
    public static String GROUPS_PREF_NAME = "groups_save_gallery_exceptions";
    public static final long MAX_VIDEO_LIMIT = 4194304000L;
    public static String USERS_PREF_NAME = "users_save_gallery_exceptions";
    public static SharedSettings channels;
    public static SharedSettings groups;
    public static SharedSettings user;

    public static class DialogException extends Settings {
        public long dialogId;

        @Override
        public CharSequence createDescription(int i) {
            String string;
            StringBuilder sb = new StringBuilder();
            if (enabled()) {
                if (this.savePhoto) {
                    sb.append(LocaleController.getString(R.string.SaveToGalleryPhotos));
                }
                if (this.saveVideo) {
                    if (sb.length() != 0) {
                        sb.append(", ");
                    }
                    long j = this.limitVideo;
                    string = (j <= 0 || j >= 4194304000L) ? LocaleController.formatString("SaveToGalleryVideos", R.string.SaveToGalleryVideos, new Object[0]) : LocaleController.formatString("SaveToGalleryVideosUpTo", R.string.SaveToGalleryVideosUpTo, AndroidUtilities.formatFileSize(j, true, false));
                }
                return sb;
            }
            string = LocaleController.getString(R.string.SaveToGalleryOff);
            sb.append(string);
            return sb;
        }
    }

    public static abstract class Settings {
        public long limitVideo = 104857600;
        public boolean savePhoto;
        public boolean saveVideo;

        public abstract CharSequence createDescription(int i);

        public boolean enabled() {
            return this.savePhoto || this.saveVideo;
        }

        public void toggle() {
            if (enabled()) {
                this.saveVideo = false;
                this.savePhoto = false;
            } else {
                this.savePhoto = true;
                this.saveVideo = true;
            }
        }
    }

    public static class SharedSettings extends Settings {
        private int type;

        public boolean needSave(FilePathDatabase.FileMeta fileMeta, MessageObject messageObject, int i) {
            DialogException dialogException = UserConfig.getInstance(i).getSaveGalleryExceptions(this.type).get(fileMeta.dialogId);
            if (messageObject != null && (messageObject.isOutOwner() || messageObject.isSecretMedia())) {
                return false;
            }
            boolean z = (messageObject != null && messageObject.isVideo()) || fileMeta.messageType == 3;
            long size = messageObject != null ? messageObject.getSize() : fileMeta.messageSize;
            boolean z2 = this.saveVideo;
            boolean z3 = this.savePhoto;
            long j = this.limitVideo;
            if (dialogException != null) {
                z2 = dialogException.saveVideo;
                z3 = dialogException.savePhoto;
                j = dialogException.limitVideo;
            }
            if (z) {
                if (z2 && (j == -1 || size < j)) {
                    return true;
                }
            } else if (z3) {
                return true;
            }
            return false;
        }

        public static SharedSettings read(String str, SharedPreferences sharedPreferences) {
            SharedSettings sharedSettings = new SharedSettings();
            sharedSettings.savePhoto = sharedPreferences.getBoolean(str + "_save_gallery_photo", false);
            sharedSettings.saveVideo = sharedPreferences.getBoolean(str + "_save_gallery_video", false);
            sharedSettings.limitVideo = sharedPreferences.getLong(str + "_save_gallery_limitVideo", 104857600L);
            return sharedSettings;
        }

        public void save(String str, SharedPreferences sharedPreferences) {
            sharedPreferences.edit().putBoolean(str + "_save_gallery_photo", this.savePhoto).putBoolean(str + "_save_gallery_video", this.saveVideo).putLong(str + "_save_gallery_limitVideo", this.limitVideo).apply();
        }

        @Override
        public java.lang.CharSequence createDescription(int r10) {
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.SaveToGallerySettingsHelper.SharedSettings.createDescription(int):java.lang.CharSequence");
        }

        @Override
        public void toggle() {
            super.toggle();
            SaveToGallerySettingsHelper.saveSettings(this.type);
        }
    }

    public static Settings getSettings(int i) {
        if (i == 1) {
            return user;
        }
        if (i == 2) {
            return groups;
        }
        if (i == 4) {
            return channels;
        }
        return null;
    }

    public static void load(SharedPreferences sharedPreferences) {
        int i = (sharedPreferences.getBoolean("save_gallery", false) && BuildVars.NO_SCOPED_STORAGE) ? 7 : sharedPreferences.getInt("save_gallery_flags", -1);
        if (i != -1) {
            sharedPreferences.edit().remove("save_gallery").remove("save_gallery_flags").apply();
            SharedSettings sharedSettings = new SharedSettings();
            user = sharedSettings;
            boolean z = (i & 1) != 0;
            sharedSettings.saveVideo = z;
            sharedSettings.savePhoto = z;
            sharedSettings.limitVideo = 104857600L;
            sharedSettings.save("user", sharedPreferences);
            SharedSettings sharedSettings2 = new SharedSettings();
            groups = sharedSettings2;
            SharedSettings sharedSettings3 = user;
            boolean z2 = (i & 2) != 0;
            sharedSettings3.saveVideo = z2;
            sharedSettings2.savePhoto = z2;
            sharedSettings2.limitVideo = 104857600L;
            sharedSettings2.save("groups", sharedPreferences);
            SharedSettings sharedSettings4 = new SharedSettings();
            channels = sharedSettings4;
            boolean z3 = (i & 4) != 0;
            sharedSettings4.saveVideo = z3;
            sharedSettings4.savePhoto = z3;
            sharedSettings4.limitVideo = 104857600L;
            sharedSettings4.save("channels", sharedPreferences);
        } else {
            user = SharedSettings.read("user", sharedPreferences);
            groups = SharedSettings.read("groups", sharedPreferences);
            channels = SharedSettings.read("channels", sharedPreferences);
        }
        user.type = 1;
        groups.type = 2;
        channels.type = 4;
    }

    public static LongSparseArray<DialogException> loadExceptions(SharedPreferences sharedPreferences) {
        LongSparseArray<DialogException> longSparseArray = new LongSparseArray<>();
        int i = sharedPreferences.getInt("count", 0);
        for (int i2 = 0; i2 < i; i2++) {
            DialogException dialogException = new DialogException();
            dialogException.dialogId = sharedPreferences.getLong(i2 + "_dialog_id", 0L);
            dialogException.savePhoto = sharedPreferences.getBoolean(i2 + "_photo", false);
            dialogException.saveVideo = sharedPreferences.getBoolean(i2 + "_video", false);
            dialogException.limitVideo = sharedPreferences.getLong(i2 + "_limitVideo", 104857600L);
            long j = dialogException.dialogId;
            if (j != 0) {
                longSparseArray.put(j, dialogException);
            }
        }
        return longSparseArray;
    }

    public static boolean needSave(int i, FilePathDatabase.FileMeta fileMeta, MessageObject messageObject, int i2) {
        SharedSettings sharedSettings;
        if (i == 1) {
            sharedSettings = user;
        } else if (i == 4) {
            sharedSettings = channels;
        } else {
            if (i != 2) {
                return false;
            }
            sharedSettings = groups;
        }
        return sharedSettings.needSave(fileMeta, messageObject, i2);
    }

    public static void saveExceptions(SharedPreferences sharedPreferences, LongSparseArray<DialogException> longSparseArray) {
        sharedPreferences.edit().clear().apply();
        SharedPreferences.Editor edit = sharedPreferences.edit();
        edit.putInt("count", longSparseArray.size());
        for (int i = 0; i < longSparseArray.size(); i++) {
            DialogException valueAt = longSparseArray.valueAt(i);
            edit.putLong(i + "_dialog_id", valueAt.dialogId);
            edit.putBoolean(i + "_photo", valueAt.savePhoto);
            edit.putBoolean(i + "_video", valueAt.saveVideo);
            edit.putLong(i + "_limitVideo", valueAt.limitVideo);
        }
        edit.apply();
    }

    public static void saveSettings(int i) {
        SharedSettings sharedSettings;
        String str;
        SharedPreferences sharedPreferences = ApplicationLoader.applicationContext.getSharedPreferences("mainconfig", 0);
        if (i == 1) {
            sharedSettings = user;
            str = "user";
        } else if (i == 2) {
            sharedSettings = groups;
            str = "groups";
        } else {
            if (i != 4) {
                return;
            }
            sharedSettings = channels;
            str = "channels";
        }
        sharedSettings.save(str, sharedPreferences);
    }
}
