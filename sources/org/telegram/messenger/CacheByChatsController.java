package org.telegram.messenger;

import android.text.TextUtils;
import android.util.LongSparseArray;
import java.io.File;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashSet;
import org.telegram.tgnet.TLRPC;

public class CacheByChatsController {
    public static int KEEP_MEDIA_DELETE = 4;
    public static int KEEP_MEDIA_FOREVER = 2;
    public static int KEEP_MEDIA_ONE_DAY = 3;
    public static int KEEP_MEDIA_ONE_MINUTE = 5;
    public static int KEEP_MEDIA_ONE_MONTH = 1;
    public static int KEEP_MEDIA_ONE_WEEK = 0;
    public static int KEEP_MEDIA_TWO_DAY = 6;
    public static final int KEEP_MEDIA_TYPE_CHANNEL = 2;
    public static final int KEEP_MEDIA_TYPE_GROUP = 1;
    public static final int KEEP_MEDIA_TYPE_STORIES = 3;
    public static final int KEEP_MEDIA_TYPE_USER = 0;
    private final int currentAccount;
    private boolean gotKeepMediaByTypes = false;
    private final int[] keepMediaByTypes = {-1, -1, -1, -1};

    public static class KeepMediaException {
        public final long dialogId;
        public int keepMedia;

        public KeepMediaException(long j, int i) {
            this.dialogId = j;
            this.keepMedia = i;
        }
    }

    public static class KeepMediaFile {
        final File file;
        boolean isStory;
        int keepMedia = -1;
        int dialogType = 2;

        public KeepMediaFile(File file) {
            this.file = file;
        }
    }

    public CacheByChatsController(int i) {
        this.currentAccount = i;
    }

    public static long getDaysInSeconds(int i) {
        if (i == KEEP_MEDIA_ONE_WEEK) {
            return 604800L;
        }
        if (i == KEEP_MEDIA_ONE_MONTH) {
            return 2592000L;
        }
        if (i == KEEP_MEDIA_ONE_DAY) {
            return 86400L;
        }
        if (i == KEEP_MEDIA_TWO_DAY) {
            return 172800L;
        }
        return (i == KEEP_MEDIA_ONE_MINUTE && BuildVars.DEBUG_PRIVATE_VERSION) ? 60L : Long.MAX_VALUE;
    }

    public static int getDefault(int i) {
        return i == 0 ? KEEP_MEDIA_FOREVER : i == 1 ? KEEP_MEDIA_ONE_MONTH : i == 2 ? KEEP_MEDIA_ONE_WEEK : i == 3 ? KEEP_MEDIA_TWO_DAY : SharedConfig.keepMedia;
    }

    public static String getKeepMediaString(int i) {
        return i == KEEP_MEDIA_ONE_MINUTE ? LocaleController.formatPluralString("Minutes", 1, new Object[0]) : i == KEEP_MEDIA_ONE_DAY ? LocaleController.formatPluralString("Days", 1, new Object[0]) : i == KEEP_MEDIA_TWO_DAY ? LocaleController.formatPluralString("Days", 2, new Object[0]) : i == KEEP_MEDIA_ONE_WEEK ? LocaleController.formatPluralString("Weeks", 1, new Object[0]) : i == KEEP_MEDIA_ONE_MONTH ? LocaleController.formatPluralString("Months", 1, new Object[0]) : LocaleController.getString(R.string.AutoDeleteMediaNever);
    }

    public int getKeepMedia(int i) {
        if (!this.gotKeepMediaByTypes) {
            this.gotKeepMediaByTypes = true;
            for (int i2 = 0; i2 < 4; i2++) {
                this.keepMediaByTypes[i2] = SharedConfig.getPreferences().getInt("keep_media_type_" + i2, getDefault(i2));
            }
        }
        int i3 = this.keepMediaByTypes[i];
        return i3 == -1 ? SharedConfig.keepMedia : i3;
    }

    public ArrayList<KeepMediaException> getKeepMediaExceptions(int i) {
        ArrayList<KeepMediaException> arrayList = new ArrayList<>();
        HashSet hashSet = new HashSet();
        String string = UserConfig.getInstance(this.currentAccount).getPreferences().getString("keep_media_exceptions_" + i, "");
        if (TextUtils.isEmpty(string)) {
            return arrayList;
        }
        ByteBuffer wrap = ByteBuffer.wrap(Utilities.hexToBytes(string));
        int i2 = wrap.getInt();
        for (int i3 = 0; i3 < i2; i3++) {
            KeepMediaException keepMediaException = new KeepMediaException(wrap.getLong(), wrap.getInt());
            if (!hashSet.contains(Long.valueOf(keepMediaException.dialogId))) {
                hashSet.add(Long.valueOf(keepMediaException.dialogId));
                arrayList.add(keepMediaException);
            }
        }
        wrap.clear();
        return arrayList;
    }

    public LongSparseArray<KeepMediaException> getKeepMediaExceptionsByDialogs() {
        LongSparseArray<KeepMediaException> longSparseArray = new LongSparseArray<>();
        for (int i = 0; i < 3; i++) {
            ArrayList<KeepMediaException> keepMediaExceptions = getKeepMediaExceptions(i);
            if (keepMediaExceptions != null) {
                for (int i2 = 0; i2 < keepMediaExceptions.size(); i2++) {
                    longSparseArray.put(keepMediaExceptions.get(i2).dialogId, keepMediaExceptions.get(i2));
                }
            }
        }
        return longSparseArray;
    }

    public void lookupFiles(ArrayList<? extends KeepMediaFile> arrayList) {
        int i;
        LongSparseArray<ArrayList<KeepMediaFile>> lookupFiles = FileLoader.getInstance(this.currentAccount).getFileDatabase().lookupFiles(arrayList);
        LongSparseArray<KeepMediaException> keepMediaExceptionsByDialogs = getKeepMediaExceptionsByDialogs();
        for (int i2 = 0; i2 < lookupFiles.size(); i2++) {
            long keyAt = lookupFiles.keyAt(i2);
            ArrayList<KeepMediaFile> valueAt = lookupFiles.valueAt(i2);
            if (keyAt >= 0) {
                i = 0;
            } else {
                long j = -keyAt;
                TLRPC.Chat chat = MessagesController.getInstance(this.currentAccount).getChat(Long.valueOf(j));
                if (chat == null) {
                    chat = MessagesStorage.getInstance(this.currentAccount).getChatSync(j);
                }
                i = chat == null ? -1 : ChatObject.isChannel(chat) ? 2 : 1;
            }
            KeepMediaException keepMediaException = keepMediaExceptionsByDialogs.get(keyAt);
            for (int i3 = 0; i3 < valueAt.size(); i3++) {
                KeepMediaFile keepMediaFile = valueAt.get(i3);
                if (i >= 0) {
                    keepMediaFile.dialogType = i;
                }
                if (keepMediaException != null) {
                    keepMediaFile.keepMedia = keepMediaException.keepMedia;
                }
            }
        }
    }

    public void saveKeepMediaExceptions(int i, ArrayList<KeepMediaException> arrayList) {
        String str = "keep_media_exceptions_" + i;
        if (arrayList.isEmpty()) {
            UserConfig.getInstance(this.currentAccount).getPreferences().edit().remove(str).apply();
            return;
        }
        int size = arrayList.size();
        ByteBuffer allocate = ByteBuffer.allocate((size * 12) + 4);
        allocate.putInt(size);
        for (int i2 = 0; i2 < size; i2++) {
            allocate.putLong(arrayList.get(i2).dialogId);
            allocate.putInt(arrayList.get(i2).keepMedia);
        }
        UserConfig.getInstance(this.currentAccount).getPreferences().edit().putString(str, Utilities.bytesToHex(allocate.array())).apply();
        allocate.clear();
    }

    public void setKeepMedia(int i, int i2) {
        if (!this.gotKeepMediaByTypes) {
            this.gotKeepMediaByTypes = true;
            for (int i3 = 0; i3 < 4; i3++) {
                this.keepMediaByTypes[i3] = SharedConfig.getPreferences().getInt("keep_media_type_" + i3, getDefault(i3));
            }
        }
        this.keepMediaByTypes[i] = i2;
        SharedConfig.getPreferences().edit().putInt("keep_media_type_" + i, i2).apply();
    }
}
