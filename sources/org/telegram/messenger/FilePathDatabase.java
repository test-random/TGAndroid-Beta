package org.telegram.messenger;

import android.os.Looper;
import android.util.LongSparseArray;
import j$.util.concurrent.ConcurrentHashMap;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import org.telegram.SQLite.SQLiteCursor;
import org.telegram.SQLite.SQLiteDatabase;
import org.telegram.SQLite.SQLitePreparedStatement;
import org.telegram.messenger.CacheByChatsController;
import org.telegram.ui.Storage.CacheModel;

public class FilePathDatabase {
    private static final String DATABASE_BACKUP_NAME = "file_to_path_backup";
    private static final String DATABASE_NAME = "file_to_path";
    public static final int FLAG_LOCALLY_CREATED = 1;
    private static final int LAST_DB_VERSION = 7;
    public static final int MESSAGE_TYPE_VIDEO_MESSAGE = 0;
    private File cacheFile;
    private final int currentAccount;
    private SQLiteDatabase database;
    boolean databaseCreated;
    private DispatchQueue dispatchQueue;
    private File shmCacheFile;
    private final ConcurrentHashMap<String, String> cache = new ConcurrentHashMap<>();
    private final FileMeta metaTmp = new FileMeta();

    public static class FileMeta {
        public long dialogId;
        public int messageId;
        public long messageSize;
        public int messageType;
    }

    public static class PathData {
        public final int dc;
        public final long id;
        public final int type;

        public PathData(long j, int i, int i2) {
            this.id = j;
            this.dc = i;
            this.type = i2;
        }
    }

    public FilePathDatabase(int i) {
        this.currentAccount = i;
    }

    private void createBackup() {
        File filesDirFixed = ApplicationLoader.getFilesDirFixed();
        if (this.currentAccount != 0) {
            File file = new File(filesDirFixed, "account" + this.currentAccount + "/");
            file.mkdirs();
            filesDirFixed = file;
        }
        File file2 = new File(filesDirFixed, "file_to_path_backup.db");
        try {
            AndroidUtilities.copyFile(this.cacheFile, file2);
            FileLog.d("file db backup created " + file2.getAbsolutePath());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void ensureQueueExist() {
        if (this.dispatchQueue == null) {
            synchronized (this) {
                try {
                    if (this.dispatchQueue == null) {
                        DispatchQueue dispatchQueue = new DispatchQueue("files_database_queue_" + this.currentAccount);
                        this.dispatchQueue = dispatchQueue;
                        dispatchQueue.setPriority(10);
                    }
                } finally {
                }
            }
        }
    }

    public void lambda$checkMediaExistance$2(ArrayList arrayList, long[] jArr, CountDownLatch countDownLatch) {
        long currentTimeMillis = System.currentTimeMillis();
        ensureDatabaseCreated();
        for (int i = 0; i < arrayList.size(); i++) {
            try {
                ((MessageObject) arrayList.get(i)).checkMediaExistance(false);
            } finally {
                try {
                } finally {
                }
            }
        }
        jArr[0] = System.currentTimeMillis() - currentTimeMillis;
    }

    public void lambda$clear$3() {
        ensureDatabaseCreated();
        try {
            this.database.executeFast("DELETE FROM paths WHERE 1").stepThis().dispose();
            this.database.executeFast("DELETE FROM paths_by_dialog_id WHERE 1").stepThis().dispose();
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    public void lambda$getPath$0(long j, int i, int i2, String[] strArr, long j2, CountDownLatch countDownLatch) {
        ensureDatabaseCreated();
        SQLiteDatabase sQLiteDatabase = this.database;
        if (sQLiteDatabase != null) {
            SQLiteCursor sQLiteCursor = null;
            try {
                sQLiteCursor = sQLiteDatabase.queryFinalized("SELECT path FROM paths WHERE document_id = " + j + " AND dc_id = " + i + " AND type = " + i2, new Object[0]);
                if (sQLiteCursor.next()) {
                    strArr[0] = sQLiteCursor.stringValue(0);
                    if (BuildVars.DEBUG_VERSION) {
                        FileLog.d("get file path id=" + j + " dc=" + i + " type=" + i2 + " path=" + strArr[0] + " in " + (System.currentTimeMillis() - j2) + "ms");
                    }
                }
            } catch (Throwable th) {
                try {
                    FileLog.e(th);
                } finally {
                    if (sQLiteCursor != null) {
                        sQLiteCursor.dispose();
                    }
                }
            }
        }
        countDownLatch.countDown();
    }

    public void lambda$hasAnotherRefOnFile$4(String str, boolean[] zArr, CountDownLatch countDownLatch) {
        ensureDatabaseCreated();
        try {
            try {
                if (this.database.queryFinalized("SELECT document_id FROM paths WHERE path = '" + str + "'", new Object[0]).next()) {
                    zArr[0] = true;
                }
            } catch (Exception e) {
                FileLog.e(e);
            }
        } finally {
            countDownLatch.countDown();
        }
    }

    public void lambda$isLocallyCreated$8(String str, boolean[] zArr, CountDownLatch countDownLatch) {
        ensureDatabaseCreated();
        try {
            try {
                SQLiteCursor queryFinalized = this.database.queryFinalized("SELECT flags FROM paths WHERE path = '" + str + "'", new Object[0]);
                if (queryFinalized.next()) {
                    boolean z = true;
                    if ((queryFinalized.intValue(0) & 1) == 0) {
                        z = false;
                    }
                    zArr[0] = z;
                }
            } catch (Exception e) {
                FileLog.e(e);
            }
        } finally {
            countDownLatch.countDown();
        }
    }

    public void lambda$lookupFiles$7(ArrayList arrayList, LongSparseArray longSparseArray, CountDownLatch countDownLatch) {
        try {
            ensureDatabaseCreated();
            FileMeta fileMeta = new FileMeta();
            for (int i = 0; i < arrayList.size(); i++) {
                FileMeta fileDialogId = getFileDialogId(((CacheByChatsController.KeepMediaFile) arrayList.get(i)).file, fileMeta);
                if (fileDialogId != null) {
                    long j = fileDialogId.dialogId;
                    if (j != 0) {
                        ArrayList arrayList2 = (ArrayList) longSparseArray.get(j);
                        if (arrayList2 == null) {
                            arrayList2 = new ArrayList();
                            longSparseArray.put(fileDialogId.dialogId, arrayList2);
                        }
                        ((CacheByChatsController.KeepMediaFile) arrayList.get(i)).isStory = fileDialogId.messageType == 23;
                        arrayList2.add((CacheByChatsController.KeepMediaFile) arrayList.get(i));
                    }
                }
            }
        } finally {
            try {
            } finally {
            }
        }
    }

    public void lambda$putPath$1(long r7, int r9, int r10, java.lang.String r11, int r12) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.FilePathDatabase.lambda$putPath$1(long, int, int, java.lang.String, int):void");
    }

    public void lambda$removeFiles$6(List list) {
        try {
            ensureDatabaseCreated();
            this.database.beginTransaction();
            for (int i = 0; i < list.size(); i++) {
                this.database.executeFast("DELETE FROM paths_by_dialog_id WHERE path = '" + shield(((CacheModel.FileInfo) list.get(i)).file.getPath()) + "'").stepThis().dispose();
            }
        } finally {
            try {
            } finally {
            }
        }
    }

    public void lambda$saveFileDialogId$5(File file, FileMeta fileMeta) {
        ensureDatabaseCreated();
        SQLitePreparedStatement sQLitePreparedStatement = null;
        try {
            try {
                sQLitePreparedStatement = this.database.executeFast("REPLACE INTO paths_by_dialog_id VALUES(?, ?, ?, ?)");
                sQLitePreparedStatement.requery();
                sQLitePreparedStatement.bindString(1, shield(file.getPath()));
                sQLitePreparedStatement.bindLong(2, fileMeta.dialogId);
                sQLitePreparedStatement.bindInteger(3, fileMeta.messageId);
                sQLitePreparedStatement.bindInteger(4, fileMeta.messageType);
                sQLitePreparedStatement.step();
            } catch (Exception e) {
                FileLog.e(e);
                if (sQLitePreparedStatement == null) {
                    return;
                }
            }
            sQLitePreparedStatement.dispose();
        } catch (Throwable th) {
            if (sQLitePreparedStatement != null) {
                sQLitePreparedStatement.dispose();
            }
            throw th;
        }
    }

    private void migrateDatabase(int i) {
        if (i == 1) {
            this.database.executeFast("CREATE INDEX IF NOT EXISTS path_in_paths ON paths(path);").stepThis().dispose();
            this.database.executeFast("PRAGMA user_version = 2").stepThis().dispose();
            i = 2;
        }
        if (i == 2) {
            this.database.executeFast("CREATE TABLE paths_by_dialog_id(path TEXT PRIMARY KEY, dialog_id INTEGER);").stepThis().dispose();
            this.database.executeFast("PRAGMA user_version = 3").stepThis().dispose();
            i = 3;
        }
        if (i == 3) {
            this.database.executeFast("ALTER TABLE paths_by_dialog_id ADD COLUMN message_id INTEGER default 0").stepThis().dispose();
            this.database.executeFast("ALTER TABLE paths_by_dialog_id ADD COLUMN message_type INTEGER default 0").stepThis().dispose();
            this.database.executeFast("PRAGMA user_version = 4").stepThis().dispose();
            i = 4;
        }
        if (i == 4 || i == 5 || i == 6) {
            try {
                this.database.executeFast("ALTER TABLE paths ADD COLUMN flags INTEGER default 0").stepThis().dispose();
            } catch (Throwable th) {
                FileLog.e(th);
            }
            this.database.executeFast("PRAGMA user_version = 7").stepThis().dispose();
        }
    }

    private void postRunnable(Runnable runnable) {
        ensureQueueExist();
        this.dispatchQueue.postRunnable(runnable);
    }

    private void postToFrontRunnable(Runnable runnable) {
        ensureQueueExist();
        this.dispatchQueue.postToFrontRunnable(runnable);
    }

    private boolean restoreBackup() {
        File filesDirFixed = ApplicationLoader.getFilesDirFixed();
        if (this.currentAccount != 0) {
            File file = new File(filesDirFixed, "account" + this.currentAccount + "/");
            file.mkdirs();
            filesDirFixed = file;
        }
        File file2 = new File(filesDirFixed, "file_to_path_backup.db");
        if (!file2.exists()) {
            return false;
        }
        try {
            return AndroidUtilities.copyFile(file2, this.cacheFile);
        } catch (IOException e) {
            FileLog.e(e);
            return false;
        }
    }

    private String shield(String str) {
        return str.replace("'", "").replace("\"", "");
    }

    public void checkMediaExistance(ArrayList<MessageObject> arrayList) {
        if (arrayList.isEmpty()) {
            return;
        }
        final ArrayList arrayList2 = new ArrayList(arrayList);
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        long currentTimeMillis = System.currentTimeMillis();
        final long[] jArr = new long[1];
        postToFrontRunnable(new Runnable() {
            @Override
            public final void run() {
                FilePathDatabase.this.lambda$checkMediaExistance$2(arrayList2, jArr, countDownLatch);
            }
        });
        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            FileLog.e(e);
        }
        FileLog.d("checkMediaExistance size=" + arrayList.size() + " time=" + (System.currentTimeMillis() - currentTimeMillis) + " thread_time=" + jArr[0]);
        if (BuildVars.DEBUG_VERSION && Thread.currentThread() == Looper.getMainLooper().getThread()) {
            FileLog.e(new Exception("warning, not allowed in main thread"));
        }
    }

    public void clear() {
        this.cache.clear();
        postRunnable(new Runnable() {
            @Override
            public final void run() {
                FilePathDatabase.this.lambda$clear$3();
            }
        });
    }

    public void createDatabase(int i, boolean z) {
        File filesDirFixed = ApplicationLoader.getFilesDirFixed();
        if (this.currentAccount != 0) {
            File file = new File(filesDirFixed, "account" + this.currentAccount + "/");
            file.mkdirs();
            filesDirFixed = file;
        }
        this.cacheFile = new File(filesDirFixed, "file_to_path.db");
        this.shmCacheFile = new File(filesDirFixed, "file_to_path.db-shm");
        boolean z2 = !this.cacheFile.exists();
        try {
            SQLiteDatabase sQLiteDatabase = new SQLiteDatabase(this.cacheFile.getPath());
            this.database = sQLiteDatabase;
            sQLiteDatabase.executeFast("PRAGMA secure_delete = ON").stepThis().dispose();
            this.database.executeFast("PRAGMA temp_store = MEMORY").stepThis().dispose();
            if (z2) {
                this.database.executeFast("CREATE TABLE paths(document_id INTEGER, dc_id INTEGER, type INTEGER, path TEXT, flags INTEGER, PRIMARY KEY(document_id, dc_id, type));").stepThis().dispose();
                this.database.executeFast("CREATE INDEX IF NOT EXISTS path_in_paths ON paths(path);").stepThis().dispose();
                this.database.executeFast("CREATE TABLE paths_by_dialog_id(path TEXT PRIMARY KEY, dialog_id INTEGER, message_id INTEGER, message_type INTEGER);").stepThis().dispose();
                this.database.executeFast("PRAGMA user_version = 7").stepThis().dispose();
            } else {
                int intValue = this.database.executeInt("PRAGMA user_version", new Object[0]).intValue();
                if (BuildVars.LOGS_ENABLED) {
                    FileLog.d("current files db version = " + intValue);
                }
                if (intValue == 0) {
                    throw new Exception("malformed");
                }
                migrateDatabase(intValue);
            }
            if (!z) {
                createBackup();
            }
            FileLog.d("files db created from_backup= " + z);
        } catch (Exception e) {
            if (i < 4) {
                if (!z && restoreBackup()) {
                    createDatabase(i + 1, true);
                    return;
                } else {
                    this.cacheFile.delete();
                    this.shmCacheFile.delete();
                    createDatabase(i + 1, false);
                }
            }
            if (BuildVars.DEBUG_VERSION) {
                FileLog.e(e);
            }
        }
    }

    public void ensureDatabaseCreated() {
        if (this.databaseCreated) {
            return;
        }
        if (!NativeLoader.loaded()) {
            int i = 0;
            while (!NativeLoader.loaded()) {
                try {
                    Thread.sleep(1000L);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                i++;
                if (i > 5) {
                    break;
                }
            }
        }
        createDatabase(0, false);
        this.databaseCreated = true;
    }

    public FileMeta getFileDialogId(File file, FileMeta fileMeta) {
        int i;
        int i2;
        SQLiteCursor sQLiteCursor = null;
        if (file == null) {
            return null;
        }
        if (fileMeta == null) {
            fileMeta = this.metaTmp;
        }
        int i3 = 0;
        long j = 0;
        try {
            try {
                sQLiteCursor = this.database.queryFinalized("SELECT dialog_id, message_id, message_type FROM paths_by_dialog_id WHERE path = '" + shield(file.getPath()) + "'", new Object[0]);
            } catch (Exception e) {
                e = e;
                i = 0;
            }
            if (sQLiteCursor.next()) {
                j = sQLiteCursor.longValue(0);
                i = sQLiteCursor.intValue(1);
                try {
                    i3 = i;
                    i2 = sQLiteCursor.intValue(2);
                } catch (Exception e2) {
                    e = e2;
                    FileLog.e(e);
                    if (sQLiteCursor != null) {
                        i3 = i;
                        i2 = 0;
                        sQLiteCursor.dispose();
                        int i4 = i3;
                        i3 = i2;
                        i = i4;
                    }
                    fileMeta.dialogId = j;
                    fileMeta.messageId = i;
                    fileMeta.messageType = i3;
                    return fileMeta;
                }
                sQLiteCursor.dispose();
                int i42 = i3;
                i3 = i2;
                i = i42;
                fileMeta.dialogId = j;
                fileMeta.messageId = i;
                fileMeta.messageType = i3;
                return fileMeta;
            }
            i2 = 0;
            sQLiteCursor.dispose();
            int i422 = i3;
            i3 = i2;
            i = i422;
            fileMeta.dialogId = j;
            fileMeta.messageId = i;
            fileMeta.messageType = i3;
            return fileMeta;
        } catch (Throwable th) {
            if (sQLiteCursor != null) {
                sQLiteCursor.dispose();
            }
            throw th;
        }
    }

    public java.lang.String getPath(final long r19, final int r21, final int r22, boolean r23) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.FilePathDatabase.getPath(long, int, int, boolean):java.lang.String");
    }

    public DispatchQueue getQueue() {
        ensureQueueExist();
        return this.dispatchQueue;
    }

    public boolean hasAnotherRefOnFile(final String str) {
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        final boolean[] zArr = {false};
        postRunnable(new Runnable() {
            @Override
            public final void run() {
                FilePathDatabase.this.lambda$hasAnotherRefOnFile$4(str, zArr, countDownLatch);
            }
        });
        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            FileLog.e(e);
        }
        return zArr[0];
    }

    public boolean isLocallyCreated(final String str) {
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        final boolean[] zArr = {false};
        postRunnable(new Runnable() {
            @Override
            public final void run() {
                FilePathDatabase.this.lambda$isLocallyCreated$8(str, zArr, countDownLatch);
            }
        });
        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            FileLog.e(e);
        }
        return zArr[0];
    }

    public LongSparseArray<ArrayList<CacheByChatsController.KeepMediaFile>> lookupFiles(final ArrayList<? extends CacheByChatsController.KeepMediaFile> arrayList) {
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        final LongSparseArray<ArrayList<CacheByChatsController.KeepMediaFile>> longSparseArray = new LongSparseArray<>();
        postRunnable(new Runnable() {
            @Override
            public final void run() {
                FilePathDatabase.this.lambda$lookupFiles$7(arrayList, longSparseArray, countDownLatch);
            }
        });
        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            FileLog.e(e);
        }
        return longSparseArray;
    }

    public void putPath(final long j, final int i, final int i2, final int i3, final String str) {
        postRunnable(new Runnable() {
            @Override
            public final void run() {
                FilePathDatabase.this.lambda$putPath$1(j, i, i2, str, i3);
            }
        });
    }

    public void removeFiles(final List<CacheModel.FileInfo> list) {
        postRunnable(new Runnable() {
            @Override
            public final void run() {
                FilePathDatabase.this.lambda$removeFiles$6(list);
            }
        });
    }

    public void saveFileDialogId(final File file, final FileMeta fileMeta) {
        if (file == null || fileMeta == null) {
            return;
        }
        postRunnable(new Runnable() {
            @Override
            public final void run() {
                FilePathDatabase.this.lambda$saveFileDialogId$5(file, fileMeta);
            }
        });
    }
}
