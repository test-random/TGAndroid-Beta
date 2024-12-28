package org.telegram.messenger;

import android.content.SharedPreferences;
import android.util.Pair;
import android.util.SparseArray;
import androidx.collection.LongSparseArray;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import org.telegram.SQLite.SQLiteCursor;
import org.telegram.SQLite.SQLitePreparedStatement;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.SendMessagesHelper;
import org.telegram.tgnet.NativeByteBuffer;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.tgnet.tl.TL_account;
import org.telegram.tgnet.tl.TL_stories;
import org.telegram.ui.LaunchActivity;

public class DownloadController extends BaseController implements NotificationCenter.NotificationCenterDelegate {
    public static final int AUTODOWNLOAD_TYPE_AUDIO = 2;
    public static final int AUTODOWNLOAD_TYPE_DOCUMENT = 8;
    public static final int AUTODOWNLOAD_TYPE_PHOTO = 1;
    public static final int AUTODOWNLOAD_TYPE_VIDEO = 4;
    private static volatile DownloadController[] Instance = new DownloadController[4];
    public static final int PRESET_NUM_CHANNEL = 3;
    public static final int PRESET_NUM_CONTACT = 0;
    public static final int PRESET_NUM_GROUP = 2;
    public static final int PRESET_NUM_PM = 1;
    public static final int PRESET_SIZE_NUM_AUDIO = 3;
    public static final int PRESET_SIZE_NUM_DOCUMENT = 2;
    public static final int PRESET_SIZE_NUM_PHOTO = 0;
    public static final int PRESET_SIZE_NUM_VIDEO = 1;
    private HashMap<String, FileDownloadProgressListener> addLaterArray;
    private ArrayList<DownloadObject> audioDownloadQueue;
    Runnable clearUnviewedDownloadsRunnale;
    public int currentMobilePreset;
    public int currentRoamingPreset;
    public int currentWifiPreset;
    private ArrayList<FileDownloadProgressListener> deleteLaterArray;
    private ArrayList<DownloadObject> documentDownloadQueue;
    private HashMap<String, DownloadObject> downloadQueueKeys;
    private HashMap<Pair<Long, Integer>, DownloadObject> downloadQueuePairs;
    public final ArrayList<MessageObject> downloadingFiles;
    public Preset highPreset;
    private int lastCheckMask;
    private int lastTag;
    private boolean listenerInProgress;
    private boolean loadingAutoDownloadConfig;
    private HashMap<String, ArrayList<MessageObject>> loadingFileMessagesObservers;
    private HashMap<String, ArrayList<WeakReference<FileDownloadProgressListener>>> loadingFileObservers;
    public Preset lowPreset;
    public Preset mediumPreset;
    public Preset mobilePreset;
    private SparseArray<String> observersByTag;
    private ArrayList<DownloadObject> photoDownloadQueue;
    public final ArrayList<MessageObject> recentDownloadingFiles;
    public Preset roamingPreset;
    private LongSparseArray typingTimes;
    public final SparseArray<MessageObject> unviewedDownloads;
    private ArrayList<DownloadObject> videoDownloadQueue;
    public Preset wifiPreset;

    public class DownloadingDocumentEntry {
        int hash;
        long id;

        private DownloadingDocumentEntry() {
        }
    }

    public interface FileDownloadProgressListener {
        int getObserverTag();

        void onFailedDownload(String str, boolean z);

        void onProgressDownload(String str, long j, long j2);

        void onProgressUpload(String str, long j, long j2, boolean z);

        void onSuccessDownload(String str);
    }

    public static class Preset {
        public boolean enabled;
        public boolean lessCallData;
        public int[] mask;
        public int maxVideoBitrate;
        public boolean preloadMusic;
        public boolean preloadStories;
        public boolean preloadVideo;
        public long[] sizes;

        public Preset(java.lang.String r11, java.lang.String r12) {
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.DownloadController.Preset.<init>(java.lang.String, java.lang.String):void");
        }

        public Preset(int[] iArr, long j, long j2, long j3, boolean z, boolean z2, boolean z3, boolean z4, int i, boolean z5) {
            int[] iArr2 = new int[4];
            this.mask = iArr2;
            this.sizes = new long[4];
            System.arraycopy(iArr, 0, iArr2, 0, Math.max(iArr.length, 4));
            long[] jArr = this.sizes;
            jArr[0] = j;
            jArr[1] = j2;
            jArr[2] = j3;
            jArr[3] = 524288;
            this.preloadVideo = z;
            this.preloadMusic = z2;
            this.lessCallData = z4;
            this.maxVideoBitrate = i;
            this.enabled = z3;
            this.preloadStories = z5;
        }

        public boolean equals(Preset preset) {
            int[] iArr = this.mask;
            int i = iArr[0];
            int[] iArr2 = preset.mask;
            if (i != iArr2[0] || iArr[1] != iArr2[1] || iArr[2] != iArr2[2] || iArr[3] != iArr2[3]) {
                return false;
            }
            long[] jArr = this.sizes;
            long j = jArr[0];
            long[] jArr2 = preset.sizes;
            return j == jArr2[0] && jArr[1] == jArr2[1] && jArr[2] == jArr2[2] && jArr[3] == jArr2[3] && this.preloadVideo == preset.preloadVideo && this.preloadMusic == preset.preloadMusic && this.maxVideoBitrate == preset.maxVideoBitrate && this.preloadStories == preset.preloadStories;
        }

        public boolean isEnabled() {
            int i = 0;
            while (true) {
                int[] iArr = this.mask;
                if (i >= iArr.length) {
                    return false;
                }
                if (iArr[i] != 0) {
                    return true;
                }
                i++;
            }
        }

        public void set(Preset preset) {
            int[] iArr = preset.mask;
            int[] iArr2 = this.mask;
            System.arraycopy(iArr, 0, iArr2, 0, iArr2.length);
            long[] jArr = preset.sizes;
            long[] jArr2 = this.sizes;
            System.arraycopy(jArr, 0, jArr2, 0, jArr2.length);
            this.preloadVideo = preset.preloadVideo;
            this.preloadMusic = preset.preloadMusic;
            this.lessCallData = preset.lessCallData;
            this.maxVideoBitrate = preset.maxVideoBitrate;
            this.preloadStories = preset.preloadStories;
        }

        public void set(TLRPC.TL_autoDownloadSettings tL_autoDownloadSettings) {
            this.preloadMusic = tL_autoDownloadSettings.audio_preload_next;
            this.preloadVideo = tL_autoDownloadSettings.video_preload_large;
            this.lessCallData = tL_autoDownloadSettings.phonecalls_less_data;
            this.maxVideoBitrate = tL_autoDownloadSettings.video_upload_maxbitrate;
            int i = 0;
            this.sizes[0] = Math.max(512000, tL_autoDownloadSettings.photo_size_max);
            this.sizes[1] = Math.max(512000L, tL_autoDownloadSettings.video_size_max);
            this.sizes[2] = Math.max(512000L, tL_autoDownloadSettings.file_size_max);
            while (true) {
                int[] iArr = this.mask;
                if (i >= iArr.length) {
                    this.preloadStories = true;
                    return;
                }
                if (tL_autoDownloadSettings.photo_size_max == 0 || tL_autoDownloadSettings.disabled) {
                    iArr[i] = iArr[i] & (-2);
                } else {
                    iArr[i] = iArr[i] | 1;
                }
                if (tL_autoDownloadSettings.video_size_max == 0 || tL_autoDownloadSettings.disabled) {
                    iArr[i] = iArr[i] & (-5);
                } else {
                    iArr[i] = iArr[i] | 4;
                }
                if (tL_autoDownloadSettings.file_size_max == 0 || tL_autoDownloadSettings.disabled) {
                    iArr[i] = iArr[i] & (-9);
                } else {
                    iArr[i] = iArr[i] | 8;
                }
                i++;
            }
        }

        public String toString() {
            return this.mask[0] + "_" + this.mask[1] + "_" + this.mask[2] + "_" + this.mask[3] + "_" + this.sizes[0] + "_" + this.sizes[1] + "_" + this.sizes[2] + "_" + this.sizes[3] + "_" + (this.preloadVideo ? 1 : 0) + "_" + (this.preloadMusic ? 1 : 0) + "_" + (this.enabled ? 1 : 0) + "_" + (this.lessCallData ? 1 : 0) + "_" + this.maxVideoBitrate + "_" + (this.preloadStories ? 1 : 0);
        }
    }

    public DownloadController(int r38) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.DownloadController.<init>(int):void");
    }

    private int canDownloadMediaInternal(org.telegram.messenger.MessageObject r16) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.DownloadController.canDownloadMediaInternal(org.telegram.messenger.MessageObject):int");
    }

    private int canDownloadMediaInternal(org.telegram.messenger.MessageObject r16, long r17) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.DownloadController.canDownloadMediaInternal(org.telegram.messenger.MessageObject, long):int");
    }

    private void checkDownloadFinished(String str, int i) {
        DownloadObject downloadObject = this.downloadQueueKeys.get(str);
        if (downloadObject != null) {
            this.downloadQueueKeys.remove(str);
            this.downloadQueuePairs.remove(new Pair(Long.valueOf(downloadObject.id), Integer.valueOf(downloadObject.type)));
            int i2 = 2;
            if (i == 0 || i == 2) {
                getMessagesStorage().removeFromDownloadQueue(downloadObject.id, downloadObject.type, false);
            }
            int i3 = downloadObject.type;
            if (i3 == 1) {
                this.photoDownloadQueue.remove(downloadObject);
                if (this.photoDownloadQueue.isEmpty()) {
                    newDownloadObjectsAvailable(1);
                    return;
                }
                return;
            }
            if (i3 == 2) {
                this.audioDownloadQueue.remove(downloadObject);
                if (!this.audioDownloadQueue.isEmpty()) {
                    return;
                }
            } else {
                i2 = 4;
                if (i3 == 4) {
                    this.videoDownloadQueue.remove(downloadObject);
                    if (!this.videoDownloadQueue.isEmpty()) {
                        return;
                    }
                } else {
                    i2 = 8;
                    if (i3 != 8) {
                        return;
                    }
                    this.documentDownloadQueue.remove(downloadObject);
                    if (!this.documentDownloadQueue.isEmpty()) {
                        return;
                    }
                }
            }
            newDownloadObjectsAvailable(i2);
        }
    }

    public static DownloadController getInstance(int i) {
        DownloadController downloadController = Instance[i];
        if (downloadController == null) {
            synchronized (DownloadController.class) {
                try {
                    downloadController = Instance[i];
                    if (downloadController == null) {
                        DownloadController[] downloadControllerArr = Instance;
                        DownloadController downloadController2 = new DownloadController(i);
                        downloadControllerArr[i] = downloadController2;
                        downloadController = downloadController2;
                    }
                } finally {
                }
            }
        }
        return downloadController;
    }

    public static float getProgress(long[] jArr) {
        if (jArr == null || jArr.length < 2) {
            return 0.0f;
        }
        long j = jArr[1];
        if (j == 0) {
            return 0.0f;
        }
        return Math.min(1.0f, ((float) jArr[0]) / ((float) j));
    }

    public void lambda$clearRecentDownloadedFiles$12() {
        try {
            getMessagesStorage().getDatabase().executeFast("DELETE FROM downloading_documents WHERE state = 1").stepThis().dispose();
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    public void lambda$deleteRecentFiles$13(ArrayList arrayList) {
        try {
            SQLitePreparedStatement executeFast = getMessagesStorage().getDatabase().executeFast("DELETE FROM downloading_documents WHERE hash = ? AND id = ?");
            for (int i = 0; i < arrayList.size(); i++) {
                executeFast.requery();
                executeFast.bindInteger(1, ((MessageObject) arrayList.get(i)).getDocument().dc_id);
                executeFast.bindLong(2, ((MessageObject) arrayList.get(i)).getDocument().id);
                executeFast.step();
                try {
                    FileLoader.getInstance(this.currentAccount).getPathToMessage(((MessageObject) arrayList.get(i)).messageOwner).delete();
                } catch (Exception e) {
                    FileLog.e(e);
                }
            }
            executeFast.dispose();
        } catch (Exception e2) {
            FileLog.e(e2);
        }
    }

    public void lambda$loadAutoDownloadConfig$1(TLObject tLObject) {
        TLRPC.TL_autoDownloadSettings tL_autoDownloadSettings;
        this.loadingAutoDownloadConfig = false;
        getUserConfig().autoDownloadConfigLoadTime = System.currentTimeMillis();
        getUserConfig().saveConfig(false);
        if (tLObject != null) {
            TL_account.autoDownloadSettings autodownloadsettings = (TL_account.autoDownloadSettings) tLObject;
            this.lowPreset.set(autodownloadsettings.low);
            this.lowPreset.preloadStories = false;
            this.mediumPreset.set(autodownloadsettings.medium);
            this.highPreset.set(autodownloadsettings.high);
            int i = 0;
            while (i < 3) {
                Preset preset = i == 0 ? this.mobilePreset : i == 1 ? this.wifiPreset : this.roamingPreset;
                if (preset.equals(this.lowPreset)) {
                    preset.set(autodownloadsettings.low);
                    preset.preloadStories = false;
                } else {
                    if (preset.equals(this.mediumPreset)) {
                        tL_autoDownloadSettings = autodownloadsettings.medium;
                    } else if (preset.equals(this.highPreset)) {
                        tL_autoDownloadSettings = autodownloadsettings.high;
                    }
                    preset.set(tL_autoDownloadSettings);
                }
                i++;
            }
            SharedPreferences.Editor edit = MessagesController.getMainSettings(this.currentAccount).edit();
            edit.putString("mobilePreset", this.mobilePreset.toString());
            edit.putString("wifiPreset", this.wifiPreset.toString());
            edit.putString("roamingPreset", this.roamingPreset.toString());
            edit.putString("preset0", this.lowPreset.toString());
            edit.putString("preset1", this.mediumPreset.toString());
            edit.putString("preset2", this.highPreset.toString());
            edit.commit();
            this.lowPreset.toString();
            this.mediumPreset.toString();
            this.highPreset.toString();
            checkAutodownloadSettings();
        }
    }

    public void lambda$loadAutoDownloadConfig$2(final TLObject tLObject, TLRPC.TL_error tL_error) {
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                DownloadController.this.lambda$loadAutoDownloadConfig$1(tLObject);
            }
        });
    }

    public void lambda$loadDownloadingFiles$10(ArrayList arrayList, ArrayList arrayList2) {
        this.downloadingFiles.clear();
        this.downloadingFiles.addAll(arrayList);
        this.recentDownloadingFiles.clear();
        this.recentDownloadingFiles.addAll(arrayList2);
    }

    public void lambda$loadDownloadingFiles$11() {
        final ArrayList<MessageObject> arrayList = new ArrayList<>();
        final ArrayList<MessageObject> arrayList2 = new ArrayList<>();
        ArrayList arrayList3 = new ArrayList();
        try {
            SQLiteCursor queryFinalized = getMessagesStorage().getDatabase().queryFinalized("SELECT data, state FROM downloading_documents ORDER BY date DESC", new Object[0]);
            while (queryFinalized.next()) {
                NativeByteBuffer byteBufferValue = queryFinalized.byteBufferValue(0);
                int intValue = queryFinalized.intValue(1);
                if (byteBufferValue != null) {
                    TLRPC.Message TLdeserialize = TLRPC.Message.TLdeserialize(byteBufferValue, byteBufferValue.readInt32(false), false);
                    if (TLdeserialize != null) {
                        TLdeserialize.readAttachPath(byteBufferValue, UserConfig.getInstance(this.currentAccount).clientUserId);
                        MessageObject messageObject = new MessageObject(this.currentAccount, TLdeserialize, false, false);
                        arrayList3.add(messageObject);
                        if (intValue == 0) {
                            arrayList.add(messageObject);
                        } else {
                            arrayList2.add(messageObject);
                        }
                    }
                    byteBufferValue.reuse();
                }
            }
            queryFinalized.dispose();
        } catch (Exception e) {
            FileLog.e(e);
        }
        getFileLoader().checkMediaExistance(arrayList);
        getFileLoader().checkMediaExistance(arrayList2);
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                DownloadController.this.lambda$loadDownloadingFiles$10(arrayList, arrayList2);
            }
        });
    }

    public void lambda$new$0() {
        getNotificationCenter().addObserver(this, NotificationCenter.fileLoadFailed);
        getNotificationCenter().addObserver(this, NotificationCenter.fileLoaded);
        getNotificationCenter().addObserver(this, NotificationCenter.fileLoadProgressChanged);
        getNotificationCenter().addObserver(this, NotificationCenter.fileUploadProgressChanged);
        getNotificationCenter().addObserver(this, NotificationCenter.httpFileDidLoad);
        getNotificationCenter().addObserver(this, NotificationCenter.httpFileDidFailedLoad);
        loadAutoDownloadConfig(false);
    }

    public void lambda$onDownloadComplete$6(MessageObject messageObject) {
        try {
            getMessagesStorage().getDatabase().executeFast(String.format(Locale.ENGLISH, "UPDATE downloading_documents SET state = 1, date = %d WHERE hash = %d AND id = %d", Long.valueOf(System.currentTimeMillis()), Integer.valueOf(messageObject.getDocument().dc_id), Long.valueOf(messageObject.getDocument().id))).stepThis().dispose();
            SQLiteCursor queryFinalized = getMessagesStorage().getDatabase().queryFinalized("SELECT COUNT(*) FROM downloading_documents WHERE state = 1", new Object[0]);
            int intValue = queryFinalized.next() ? queryFinalized.intValue(0) : 0;
            queryFinalized.dispose();
            SQLiteCursor queryFinalized2 = getMessagesStorage().getDatabase().queryFinalized("SELECT state FROM downloading_documents WHERE state = 1", new Object[0]);
            if (queryFinalized2.next()) {
                queryFinalized2.intValue(0);
            }
            queryFinalized2.dispose();
            if (intValue > 100) {
                SQLiteCursor queryFinalized3 = getMessagesStorage().getDatabase().queryFinalized("SELECT hash, id FROM downloading_documents WHERE state = 1 ORDER BY date ASC LIMIT " + (100 - intValue), new Object[0]);
                ArrayList arrayList = new ArrayList();
                while (queryFinalized3.next()) {
                    DownloadingDocumentEntry downloadingDocumentEntry = new DownloadingDocumentEntry();
                    downloadingDocumentEntry.hash = queryFinalized3.intValue(0);
                    downloadingDocumentEntry.id = queryFinalized3.longValue(1);
                    arrayList.add(downloadingDocumentEntry);
                }
                queryFinalized3.dispose();
                SQLitePreparedStatement executeFast = getMessagesStorage().getDatabase().executeFast("DELETE FROM downloading_documents WHERE hash = ? AND id = ?");
                for (int i = 0; i < arrayList.size(); i++) {
                    executeFast.requery();
                    executeFast.bindInteger(1, ((DownloadingDocumentEntry) arrayList.get(i)).hash);
                    executeFast.bindLong(2, ((DownloadingDocumentEntry) arrayList.get(i)).id);
                    executeFast.step();
                }
                executeFast.dispose();
            }
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    public void lambda$onDownloadComplete$7(TLRPC.Document document, final MessageObject messageObject) {
        for (int i = 0; i < this.downloadingFiles.size(); i++) {
            if (this.downloadingFiles.get(i).getDocument() != null && this.downloadingFiles.get(i).getDocument().id == document.id) {
                this.downloadingFiles.remove(i);
                int i2 = 0;
                while (true) {
                    if (i2 >= this.recentDownloadingFiles.size()) {
                        this.recentDownloadingFiles.add(0, messageObject);
                        putToUnviewedDownloads(messageObject);
                        break;
                    } else if (this.recentDownloadingFiles.get(i2).getDocument() != null && this.recentDownloadingFiles.get(i2).getDocument().id == document.id) {
                        break;
                    } else {
                        i2++;
                    }
                }
                getNotificationCenter().lambda$postNotificationNameOnUIThread$1(NotificationCenter.onDownloadingFilesChanged, new Object[0]);
                getMessagesStorage().getStorageQueue().postRunnable(new Runnable() {
                    @Override
                    public final void run() {
                        DownloadController.this.lambda$onDownloadComplete$6(messageObject);
                    }
                });
                return;
            }
        }
    }

    public void lambda$onDownloadFail$8(MessageObject messageObject, int i) {
        TLRPC.Document document = messageObject.getDocument();
        for (int i2 = 0; i2 < this.downloadingFiles.size(); i2++) {
            TLRPC.Document document2 = this.downloadingFiles.get(i2).getDocument();
            if (document2 == null || (document != null && document2.id == document.id)) {
                this.downloadingFiles.remove(i2);
                getNotificationCenter().lambda$postNotificationNameOnUIThread$1(NotificationCenter.onDownloadingFilesChanged, new Object[0]);
                if (i == 0) {
                    NotificationCenter.getGlobalInstance().lambda$postNotificationNameOnUIThread$1(NotificationCenter.showBulletin, 1, LocaleController.formatString("MessageNotFound", R.string.MessageNotFound, new Object[0]));
                    return;
                } else {
                    if (i == -1) {
                        LaunchActivity.checkFreeDiscSpaceStatic(2);
                        return;
                    }
                    return;
                }
            }
        }
    }

    public void lambda$onDownloadFail$9(MessageObject messageObject) {
        try {
            SQLitePreparedStatement executeFast = getMessagesStorage().getDatabase().executeFast("DELETE FROM downloading_documents WHERE hash = ? AND id = ?");
            executeFast.bindInteger(1, messageObject.getDocument().dc_id);
            executeFast.bindLong(2, messageObject.getDocument().id);
            executeFast.step();
            executeFast.dispose();
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    public static void lambda$savePresetToServer$3(TLObject tLObject, TLRPC.TL_error tL_error) {
    }

    public void lambda$startDownloadFile$4(MessageObject messageObject) {
        try {
            NativeByteBuffer nativeByteBuffer = new NativeByteBuffer(messageObject.messageOwner.getObjectSize());
            messageObject.messageOwner.serializeToStream(nativeByteBuffer);
            SQLitePreparedStatement executeFast = getMessagesStorage().getDatabase().executeFast("REPLACE INTO downloading_documents VALUES(?, ?, ?, ?, ?)");
            executeFast.bindByteBuffer(1, nativeByteBuffer);
            executeFast.bindInteger(2, messageObject.getDocument().dc_id);
            executeFast.bindLong(3, messageObject.getDocument().id);
            executeFast.bindLong(4, System.currentTimeMillis());
            executeFast.bindInteger(4, 0);
            executeFast.step();
            executeFast.dispose();
            nativeByteBuffer.reuse();
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    public void lambda$startDownloadFile$5(TLRPC.Document document, final MessageObject messageObject) {
        boolean z;
        boolean z2;
        TLRPC.Document document2;
        TLRPC.Document document3;
        if (document == null) {
            return;
        }
        int i = 0;
        while (true) {
            z = true;
            if (i >= this.recentDownloadingFiles.size()) {
                z2 = false;
                break;
            }
            MessageObject messageObject2 = this.recentDownloadingFiles.get(i);
            if (messageObject2 != null && (document3 = messageObject2.getDocument()) != null && document3.id == document.id) {
                z2 = true;
                break;
            }
            i++;
        }
        if (!z2) {
            for (int i2 = 0; i2 < this.downloadingFiles.size(); i2++) {
                MessageObject messageObject3 = this.downloadingFiles.get(i2);
                if (messageObject3 != null && (document2 = messageObject3.getDocument()) != null && document2.id == document.id) {
                    break;
                }
            }
        }
        z = z2;
        if (!z) {
            this.downloadingFiles.add(0, messageObject);
            getMessagesStorage().getStorageQueue().postRunnable(new Runnable() {
                @Override
                public final void run() {
                    DownloadController.this.lambda$startDownloadFile$4(messageObject);
                }
            });
        }
        getNotificationCenter().lambda$postNotificationNameOnUIThread$1(NotificationCenter.onDownloadingFilesChanged, new Object[0]);
    }

    private void processLaterArrays() {
        for (Map.Entry<String, FileDownloadProgressListener> entry : this.addLaterArray.entrySet()) {
            addLoadingFileObserver(entry.getKey(), entry.getValue());
        }
        this.addLaterArray.clear();
        Iterator<FileDownloadProgressListener> it = this.deleteLaterArray.iterator();
        while (it.hasNext()) {
            removeLoadingFileObserver(it.next());
        }
        this.deleteLaterArray.clear();
    }

    private void putToUnviewedDownloads(MessageObject messageObject) {
        this.unviewedDownloads.put(messageObject.getId(), messageObject);
        AndroidUtilities.cancelRunOnUIThread(this.clearUnviewedDownloadsRunnale);
        AndroidUtilities.runOnUIThread(this.clearUnviewedDownloadsRunnale, 60000L);
    }

    public static int typeToIndex(int i) {
        if (i == 1) {
            return 0;
        }
        if (i == 2) {
            return 2;
        }
        if (i == 4) {
            return 1;
        }
        return i == 8 ? 2 : 0;
    }

    public void addLoadingFileObserver(String str, FileDownloadProgressListener fileDownloadProgressListener) {
        addLoadingFileObserver(str, null, fileDownloadProgressListener);
    }

    public void addLoadingFileObserver(String str, MessageObject messageObject, FileDownloadProgressListener fileDownloadProgressListener) {
        if (this.listenerInProgress) {
            this.addLaterArray.put(str, fileDownloadProgressListener);
            return;
        }
        removeLoadingFileObserver(fileDownloadProgressListener);
        ArrayList<WeakReference<FileDownloadProgressListener>> arrayList = this.loadingFileObservers.get(str);
        if (arrayList == null) {
            arrayList = new ArrayList<>();
            this.loadingFileObservers.put(str, arrayList);
        }
        arrayList.add(new WeakReference<>(fileDownloadProgressListener));
        if (messageObject != null) {
            ArrayList<MessageObject> arrayList2 = this.loadingFileMessagesObservers.get(str);
            if (arrayList2 == null) {
                arrayList2 = new ArrayList<>();
                this.loadingFileMessagesObservers.put(str, arrayList2);
            }
            arrayList2.add(messageObject);
        }
        this.observersByTag.put(fileDownloadProgressListener.getObserverTag(), str);
    }

    public int canDownloadMedia(org.telegram.tgnet.TLRPC.Message r18) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.DownloadController.canDownloadMedia(org.telegram.tgnet.TLRPC$Message):int");
    }

    public int canDownloadMedia(org.telegram.tgnet.TLRPC.Message r14, org.telegram.tgnet.TLRPC.MessageMedia r15) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.DownloadController.canDownloadMedia(org.telegram.tgnet.TLRPC$Message, org.telegram.tgnet.TLRPC$MessageMedia):int");
    }

    public boolean canDownloadMedia(int i, long j) {
        Preset currentMobilePreset;
        int autodownloadNetworkType = ApplicationLoader.getAutodownloadNetworkType();
        if (autodownloadNetworkType == 1) {
            if (!this.wifiPreset.enabled) {
                return false;
            }
            currentMobilePreset = getCurrentWiFiPreset();
        } else if (autodownloadNetworkType == 2) {
            if (!this.roamingPreset.enabled) {
                return false;
            }
            currentMobilePreset = getCurrentRoamingPreset();
        } else {
            if (!this.mobilePreset.enabled) {
                return false;
            }
            currentMobilePreset = getCurrentMobilePreset();
        }
        int i2 = currentMobilePreset.mask[1];
        long j2 = currentMobilePreset.sizes[typeToIndex(i)];
        if (i == 1 || (j != 0 && j <= j2)) {
            return i == 2 || (i & i2) != 0;
        }
        return false;
    }

    public boolean canDownloadMedia(MessageObject messageObject) {
        TL_stories.StoryItem storyItem;
        TLRPC.MessageMedia messageMedia;
        if (messageObject.type == 23) {
            return (!SharedConfig.isAutoplayVideo() || (storyItem = ((TLRPC.TL_messageMediaStory) MessageObject.getMedia(messageObject)).storyItem) == null || (messageMedia = storyItem.media) == null || messageMedia.document == null || !storyItem.isPublic) ? false : true;
        }
        if (messageObject.sponsoredMedia != null) {
            return true;
        }
        return !messageObject.isHiddenSensitive() && canDownloadMediaInternal(messageObject) == 1;
    }

    public int canDownloadMediaType(MessageObject messageObject) {
        TL_stories.StoryItem storyItem;
        TLRPC.MessageMedia messageMedia;
        if (messageObject.type == 23) {
            return (!SharedConfig.isAutoplayVideo() || (storyItem = ((TLRPC.TL_messageMediaStory) MessageObject.getMedia(messageObject)).storyItem) == null || (messageMedia = storyItem.media) == null || messageMedia.document == null || !storyItem.isPublic) ? 0 : 2;
        }
        if (messageObject.sponsoredMedia != null) {
            return 2;
        }
        if (messageObject.isHiddenSensitive()) {
            return 0;
        }
        return canDownloadMediaInternal(messageObject);
    }

    public int canDownloadMediaType(MessageObject messageObject, long j) {
        TL_stories.StoryItem storyItem;
        TLRPC.MessageMedia messageMedia;
        if (messageObject.type == 23) {
            return (!SharedConfig.isAutoplayVideo() || (storyItem = ((TLRPC.TL_messageMediaStory) MessageObject.getMedia(messageObject)).storyItem) == null || (messageMedia = storyItem.media) == null || messageMedia.document == null || !storyItem.isPublic) ? 0 : 2;
        }
        if (messageObject.sponsoredMedia != null) {
            return 2;
        }
        if (messageObject.isHiddenSensitive()) {
            return 0;
        }
        return canDownloadMediaInternal(messageObject, j);
    }

    public boolean canDownloadNextTrack() {
        int autodownloadNetworkType = ApplicationLoader.getAutodownloadNetworkType();
        return autodownloadNetworkType == 1 ? this.wifiPreset.enabled && getCurrentWiFiPreset().preloadMusic : autodownloadNetworkType == 2 ? this.roamingPreset.enabled && getCurrentRoamingPreset().preloadMusic : this.mobilePreset.enabled && getCurrentMobilePreset().preloadMusic;
    }

    public boolean canPreloadStories() {
        Preset currentMobilePreset;
        int autodownloadNetworkType = ApplicationLoader.getAutodownloadNetworkType();
        if (autodownloadNetworkType == 1) {
            if (!this.wifiPreset.enabled) {
                return false;
            }
            currentMobilePreset = getCurrentWiFiPreset();
        } else if (autodownloadNetworkType == 2) {
            if (!this.roamingPreset.enabled) {
                return false;
            }
            currentMobilePreset = getCurrentRoamingPreset();
        } else {
            if (!this.mobilePreset.enabled) {
                return false;
            }
            currentMobilePreset = getCurrentMobilePreset();
        }
        return currentMobilePreset.preloadStories;
    }

    public void cancelDownloading(ArrayList<Pair<Long, Integer>> arrayList) {
        TLRPC.PhotoSize closestPhotoSizeWithSize;
        int size = arrayList.size();
        for (int i = 0; i < size; i++) {
            DownloadObject downloadObject = this.downloadQueuePairs.get(arrayList.get(i));
            if (downloadObject != null) {
                TLObject tLObject = downloadObject.object;
                if (tLObject instanceof TLRPC.Document) {
                    getFileLoader().cancelLoadFile((TLRPC.Document) tLObject, true);
                } else if ((tLObject instanceof TLRPC.Photo) && (closestPhotoSizeWithSize = FileLoader.getClosestPhotoSizeWithSize(((TLRPC.Photo) tLObject).sizes, AndroidUtilities.getPhotoSize())) != null) {
                    getFileLoader().cancelLoadFile(closestPhotoSizeWithSize, true);
                }
            }
        }
    }

    public void checkAutodownloadSettings() {
        int currentDownloadMask = getCurrentDownloadMask();
        if (currentDownloadMask == this.lastCheckMask) {
            return;
        }
        this.lastCheckMask = currentDownloadMask;
        if ((currentDownloadMask & 1) == 0) {
            for (int i = 0; i < this.photoDownloadQueue.size(); i++) {
                DownloadObject downloadObject = this.photoDownloadQueue.get(i);
                TLObject tLObject = downloadObject.object;
                if (tLObject instanceof TLRPC.Photo) {
                    getFileLoader().cancelLoadFile(FileLoader.getClosestPhotoSizeWithSize(((TLRPC.Photo) tLObject).sizes, AndroidUtilities.getPhotoSize()));
                } else if (tLObject instanceof TLRPC.Document) {
                    getFileLoader().cancelLoadFile((TLRPC.Document) downloadObject.object);
                }
            }
            this.photoDownloadQueue.clear();
        } else if (this.photoDownloadQueue.isEmpty()) {
            newDownloadObjectsAvailable(1);
        }
        if ((currentDownloadMask & 2) == 0) {
            for (int i2 = 0; i2 < this.audioDownloadQueue.size(); i2++) {
                getFileLoader().cancelLoadFile((TLRPC.Document) this.audioDownloadQueue.get(i2).object);
            }
            this.audioDownloadQueue.clear();
        } else if (this.audioDownloadQueue.isEmpty()) {
            newDownloadObjectsAvailable(2);
        }
        if ((currentDownloadMask & 8) == 0) {
            for (int i3 = 0; i3 < this.documentDownloadQueue.size(); i3++) {
                getFileLoader().cancelLoadFile((TLRPC.Document) this.documentDownloadQueue.get(i3).object);
            }
            this.documentDownloadQueue.clear();
        } else if (this.documentDownloadQueue.isEmpty()) {
            newDownloadObjectsAvailable(8);
        }
        if ((currentDownloadMask & 4) == 0) {
            for (int i4 = 0; i4 < this.videoDownloadQueue.size(); i4++) {
                getFileLoader().cancelLoadFile((TLRPC.Document) this.videoDownloadQueue.get(i4).object);
            }
            this.videoDownloadQueue.clear();
        } else if (this.videoDownloadQueue.isEmpty()) {
            newDownloadObjectsAvailable(4);
        }
        int autodownloadMaskAll = getAutodownloadMaskAll();
        if (autodownloadMaskAll == 0) {
            getMessagesStorage().clearDownloadQueue(0);
            return;
        }
        if ((autodownloadMaskAll & 1) == 0) {
            getMessagesStorage().clearDownloadQueue(1);
        }
        if ((autodownloadMaskAll & 2) == 0) {
            getMessagesStorage().clearDownloadQueue(2);
        }
        if ((autodownloadMaskAll & 4) == 0) {
            getMessagesStorage().clearDownloadQueue(4);
        }
        if ((autodownloadMaskAll & 8) == 0) {
            getMessagesStorage().clearDownloadQueue(8);
        }
    }

    public void checkUnviewedDownloads(int i, long j) {
        MessageObject messageObject = this.unviewedDownloads.get(i);
        if (messageObject == null || messageObject.getDialogId() != j) {
            return;
        }
        this.unviewedDownloads.remove(i);
        if (this.unviewedDownloads.size() == 0) {
            getNotificationCenter().lambda$postNotificationNameOnUIThread$1(NotificationCenter.onDownloadingFilesChanged, new Object[0]);
        }
    }

    public void cleanup() {
        this.photoDownloadQueue.clear();
        this.audioDownloadQueue.clear();
        this.documentDownloadQueue.clear();
        this.videoDownloadQueue.clear();
        this.downloadQueueKeys.clear();
        this.downloadQueuePairs.clear();
        this.typingTimes.clear();
    }

    public void clearRecentDownloadedFiles() {
        this.recentDownloadingFiles.clear();
        getNotificationCenter().lambda$postNotificationNameOnUIThread$1(NotificationCenter.onDownloadingFilesChanged, new Object[0]);
        getMessagesStorage().getStorageQueue().postRunnable(new Runnable() {
            @Override
            public final void run() {
                DownloadController.this.lambda$clearRecentDownloadedFiles$12();
            }
        });
    }

    public void clearUnviewedDownloads() {
        this.unviewedDownloads.clear();
    }

    public void deleteRecentFiles(final java.util.ArrayList<org.telegram.messenger.MessageObject> r9) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.DownloadController.deleteRecentFiles(java.util.ArrayList):void");
    }

    @Override
    public void didReceivedNotification(int i, int i2, Object... objArr) {
        LongSparseArray longSparseArray;
        long currentTimeMillis;
        if (i == NotificationCenter.fileLoadFailed || i == NotificationCenter.httpFileDidFailedLoad) {
            String str = (String) objArr[0];
            Integer num = (Integer) objArr[1];
            this.listenerInProgress = true;
            ArrayList<WeakReference<FileDownloadProgressListener>> arrayList = this.loadingFileObservers.get(str);
            if (arrayList != null) {
                int size = arrayList.size();
                for (int i3 = 0; i3 < size; i3++) {
                    WeakReference<FileDownloadProgressListener> weakReference = arrayList.get(i3);
                    if (weakReference.get() != null) {
                        weakReference.get().onFailedDownload(str, num.intValue() == 1);
                        if (num.intValue() != 1) {
                            this.observersByTag.remove(weakReference.get().getObserverTag());
                        }
                    }
                }
                if (num.intValue() != 1) {
                    this.loadingFileObservers.remove(str);
                }
            }
            this.listenerInProgress = false;
            processLaterArrays();
            checkDownloadFinished(str, num.intValue());
            return;
        }
        if (i == NotificationCenter.fileLoaded || i == NotificationCenter.httpFileDidLoad) {
            this.listenerInProgress = true;
            String str2 = (String) objArr[0];
            ArrayList<MessageObject> arrayList2 = this.loadingFileMessagesObservers.get(str2);
            if (arrayList2 != null) {
                int size2 = arrayList2.size();
                for (int i4 = 0; i4 < size2; i4++) {
                    arrayList2.get(i4).mediaExists = true;
                }
                this.loadingFileMessagesObservers.remove(str2);
            }
            ArrayList<WeakReference<FileDownloadProgressListener>> arrayList3 = this.loadingFileObservers.get(str2);
            if (arrayList3 != null) {
                int size3 = arrayList3.size();
                for (int i5 = 0; i5 < size3; i5++) {
                    WeakReference<FileDownloadProgressListener> weakReference2 = arrayList3.get(i5);
                    if (weakReference2.get() != null) {
                        weakReference2.get().onSuccessDownload(str2);
                        this.observersByTag.remove(weakReference2.get().getObserverTag());
                    }
                }
                this.loadingFileObservers.remove(str2);
            }
            this.listenerInProgress = false;
            processLaterArrays();
            checkDownloadFinished(str2, 0);
            return;
        }
        if (i == NotificationCenter.fileLoadProgressChanged) {
            this.listenerInProgress = true;
            String str3 = (String) objArr[0];
            ArrayList<WeakReference<FileDownloadProgressListener>> arrayList4 = this.loadingFileObservers.get(str3);
            if (arrayList4 != null) {
                Long l = (Long) objArr[1];
                Long l2 = (Long) objArr[2];
                int size4 = arrayList4.size();
                for (int i6 = 0; i6 < size4; i6++) {
                    WeakReference<FileDownloadProgressListener> weakReference3 = arrayList4.get(i6);
                    if (weakReference3.get() != null) {
                        weakReference3.get().onProgressDownload(str3, l.longValue(), l2.longValue());
                    }
                }
            }
            this.listenerInProgress = false;
            processLaterArrays();
            return;
        }
        if (i == NotificationCenter.fileUploadProgressChanged) {
            this.listenerInProgress = true;
            String str4 = (String) objArr[0];
            ArrayList<WeakReference<FileDownloadProgressListener>> arrayList5 = this.loadingFileObservers.get(str4);
            if (arrayList5 != null) {
                Long l3 = (Long) objArr[1];
                Long l4 = (Long) objArr[2];
                Boolean bool = (Boolean) objArr[3];
                int size5 = arrayList5.size();
                for (int i7 = 0; i7 < size5; i7++) {
                    WeakReference<FileDownloadProgressListener> weakReference4 = arrayList5.get(i7);
                    if (weakReference4.get() != null) {
                        weakReference4.get().onProgressUpload(str4, l3.longValue(), l4.longValue(), bool.booleanValue());
                    }
                }
            }
            this.listenerInProgress = false;
            processLaterArrays();
            try {
                ArrayList<SendMessagesHelper.DelayedMessage> delayedMessages = getSendMessagesHelper().getDelayedMessages(str4);
                if (delayedMessages != null) {
                    for (int i8 = 0; i8 < delayedMessages.size(); i8++) {
                        SendMessagesHelper.DelayedMessage delayedMessage = delayedMessages.get(i8);
                        if (delayedMessage.encryptedChat == null) {
                            long j = delayedMessage.peer;
                            int i9 = delayedMessage.topMessageId;
                            Long l5 = (Long) this.typingTimes.get(j);
                            if (delayedMessage.type == 4) {
                                if (l5 != null && l5.longValue() + 4000 >= System.currentTimeMillis()) {
                                }
                                MessageObject messageObject = (MessageObject) delayedMessage.extraHashMap.get(str4 + "_i");
                                if (messageObject != null && messageObject.isVideo()) {
                                    getMessagesController().sendTyping(j, i9, 5, 0);
                                } else if (messageObject == null || messageObject.getDocument() == null) {
                                    getMessagesController().sendTyping(j, i9, 4, 0);
                                } else {
                                    getMessagesController().sendTyping(j, i9, 3, 0);
                                }
                                longSparseArray = this.typingTimes;
                                currentTimeMillis = System.currentTimeMillis();
                                longSparseArray.put(j, Long.valueOf(currentTimeMillis));
                            } else {
                                delayedMessage.obj.getDocument();
                                if (l5 == null || l5.longValue() + 4000 < System.currentTimeMillis()) {
                                    if (delayedMessage.obj.isRoundVideo()) {
                                        getMessagesController().sendTyping(j, i9, 8, 0);
                                    } else if (delayedMessage.obj.isVideo()) {
                                        getMessagesController().sendTyping(j, i9, 5, 0);
                                    } else if (delayedMessage.obj.isVoice()) {
                                        getMessagesController().sendTyping(j, i9, 9, 0);
                                    } else if (delayedMessage.obj.getDocument() != null) {
                                        getMessagesController().sendTyping(j, i9, 3, 0);
                                    } else if (delayedMessage.photoSize != null) {
                                        getMessagesController().sendTyping(j, i9, 4, 0);
                                    }
                                    longSparseArray = this.typingTimes;
                                    currentTimeMillis = System.currentTimeMillis();
                                    longSparseArray.put(j, Long.valueOf(currentTimeMillis));
                                }
                            }
                        }
                    }
                }
            } catch (Exception e) {
                FileLog.e(e);
            }
        }
    }

    public int generateObserverTag() {
        int i = this.lastTag;
        this.lastTag = i + 1;
        return i;
    }

    public int getAutodownloadMask() {
        Preset currentMobilePreset;
        int autodownloadNetworkType = ApplicationLoader.getAutodownloadNetworkType();
        if (autodownloadNetworkType == 1) {
            if (!this.wifiPreset.enabled) {
                return 0;
            }
            currentMobilePreset = getCurrentWiFiPreset();
        } else if (autodownloadNetworkType == 2) {
            if (!this.roamingPreset.enabled) {
                return 0;
            }
            currentMobilePreset = getCurrentRoamingPreset();
        } else {
            if (!this.mobilePreset.enabled) {
                return 0;
            }
            currentMobilePreset = getCurrentMobilePreset();
        }
        int[] iArr = currentMobilePreset.mask;
        int i = 0;
        for (int i2 = 0; i2 < iArr.length; i2++) {
            int i3 = iArr[i2];
            int i4 = (i3 & 1) != 0 ? 1 : 0;
            if ((i3 & 2) != 0) {
                i4 |= 2;
            }
            if ((i3 & 4) != 0) {
                i4 |= 4;
            }
            if ((i3 & 8) != 0) {
                i4 |= 8;
            }
            i |= i4 << (i2 * 8);
        }
        return i;
    }

    protected int getAutodownloadMaskAll() {
        if (!this.mobilePreset.enabled && !this.roamingPreset.enabled && !this.wifiPreset.enabled) {
            return 0;
        }
        int i = 0;
        for (int i2 = 0; i2 < 4; i2++) {
            if ((getCurrentMobilePreset().mask[i2] & 1) != 0 || (getCurrentWiFiPreset().mask[i2] & 1) != 0 || (getCurrentRoamingPreset().mask[i2] & 1) != 0) {
                i |= 1;
            }
            if ((getCurrentMobilePreset().mask[i2] & 2) != 0 || (getCurrentWiFiPreset().mask[i2] & 2) != 0 || (getCurrentRoamingPreset().mask[i2] & 2) != 0) {
                i |= 2;
            }
            if ((getCurrentMobilePreset().mask[i2] & 4) != 0 || (getCurrentWiFiPreset().mask[i2] & 4) != 0 || (4 & getCurrentRoamingPreset().mask[i2]) != 0) {
                i |= 4;
            }
            if ((getCurrentMobilePreset().mask[i2] & 8) != 0 || (getCurrentWiFiPreset().mask[i2] & 8) != 0 || (getCurrentRoamingPreset().mask[i2] & 8) != 0) {
                i |= 8;
            }
        }
        return i;
    }

    public int getCurrentDownloadMask() {
        int autodownloadNetworkType = ApplicationLoader.getAutodownloadNetworkType();
        int i = 0;
        if (autodownloadNetworkType == 1) {
            if (!this.wifiPreset.enabled) {
                return 0;
            }
            int i2 = 0;
            while (i < 4) {
                i2 |= getCurrentWiFiPreset().mask[i];
                i++;
            }
            return i2;
        }
        if (autodownloadNetworkType == 2) {
            if (!this.roamingPreset.enabled) {
                return 0;
            }
            int i3 = 0;
            while (i < 4) {
                i3 |= getCurrentRoamingPreset().mask[i];
                i++;
            }
            return i3;
        }
        if (!this.mobilePreset.enabled) {
            return 0;
        }
        int i4 = 0;
        while (i < 4) {
            i4 |= getCurrentMobilePreset().mask[i];
            i++;
        }
        return i4;
    }

    public Preset getCurrentMobilePreset() {
        int i = this.currentMobilePreset;
        return i == 0 ? this.lowPreset : i == 1 ? this.mediumPreset : i == 2 ? this.highPreset : this.mobilePreset;
    }

    public Preset getCurrentRoamingPreset() {
        int i = this.currentRoamingPreset;
        return i == 0 ? this.lowPreset : i == 1 ? this.mediumPreset : i == 2 ? this.highPreset : this.roamingPreset;
    }

    public Preset getCurrentWiFiPreset() {
        int i = this.currentWifiPreset;
        return i == 0 ? this.lowPreset : i == 1 ? this.mediumPreset : i == 2 ? this.highPreset : this.wifiPreset;
    }

    public int getMaxVideoBitrate() {
        int autodownloadNetworkType = ApplicationLoader.getAutodownloadNetworkType();
        return (autodownloadNetworkType == 1 ? getCurrentWiFiPreset() : autodownloadNetworkType == 2 ? getCurrentRoamingPreset() : getCurrentMobilePreset()).maxVideoBitrate;
    }

    public boolean hasUnviewedDownloads() {
        return this.unviewedDownloads.size() > 0;
    }

    public boolean isDownloading(int i) {
        for (int i2 = 0; i2 < this.downloadingFiles.size(); i2++) {
            if (this.downloadingFiles.get(i2).messageOwner.id == i) {
                return true;
            }
        }
        return false;
    }

    public void loadAutoDownloadConfig(boolean z) {
        if (this.loadingAutoDownloadConfig) {
            return;
        }
        if (z || Math.abs(System.currentTimeMillis() - getUserConfig().autoDownloadConfigLoadTime) >= 86400000) {
            this.loadingAutoDownloadConfig = true;
            getConnectionsManager().sendRequest(new TL_account.getAutoDownloadSettings(), new RequestDelegate() {
                @Override
                public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                    DownloadController.this.lambda$loadAutoDownloadConfig$2(tLObject, tL_error);
                }
            });
        }
    }

    public void loadDownloadingFiles() {
        getMessagesStorage().getStorageQueue().postRunnable(new Runnable() {
            @Override
            public final void run() {
                DownloadController.this.lambda$loadDownloadingFiles$11();
            }
        });
    }

    public void newDownloadObjectsAvailable(int i) {
        int currentDownloadMask = getCurrentDownloadMask();
        if ((currentDownloadMask & 1) != 0 && (i & 1) != 0 && this.photoDownloadQueue.isEmpty()) {
            getMessagesStorage().getDownloadQueue(1);
        }
        if ((currentDownloadMask & 2) != 0 && (i & 2) != 0 && this.audioDownloadQueue.isEmpty()) {
            getMessagesStorage().getDownloadQueue(2);
        }
        if ((currentDownloadMask & 4) != 0 && (i & 4) != 0 && this.videoDownloadQueue.isEmpty()) {
            getMessagesStorage().getDownloadQueue(4);
        }
        if ((currentDownloadMask & 8) == 0 || (i & 8) == 0 || !this.documentDownloadQueue.isEmpty()) {
            return;
        }
        getMessagesStorage().getDownloadQueue(8);
    }

    public void onDownloadComplete(final MessageObject messageObject) {
        if (messageObject == null || messageObject.getDocument() == null) {
            return;
        }
        final TLRPC.Document document = messageObject.getDocument();
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                DownloadController.this.lambda$onDownloadComplete$7(document, messageObject);
            }
        });
    }

    public void onDownloadFail(final MessageObject messageObject, final int i) {
        if (messageObject == null) {
            return;
        }
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                DownloadController.this.lambda$onDownloadFail$8(messageObject, i);
            }
        });
        getMessagesStorage().getStorageQueue().postRunnable(new Runnable() {
            @Override
            public final void run() {
                DownloadController.this.lambda$onDownloadFail$9(messageObject);
            }
        });
    }

    public void processDownloadObjects(int r20, java.util.ArrayList<org.telegram.messenger.DownloadObject> r21) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.DownloadController.processDownloadObjects(int, java.util.ArrayList):void");
    }

    public void removeLoadingFileObserver(FileDownloadProgressListener fileDownloadProgressListener) {
        if (this.listenerInProgress) {
            this.deleteLaterArray.add(fileDownloadProgressListener);
            return;
        }
        String str = this.observersByTag.get(fileDownloadProgressListener.getObserverTag());
        if (str != null) {
            ArrayList<WeakReference<FileDownloadProgressListener>> arrayList = this.loadingFileObservers.get(str);
            if (arrayList != null) {
                int i = 0;
                while (i < arrayList.size()) {
                    WeakReference<FileDownloadProgressListener> weakReference = arrayList.get(i);
                    if (weakReference.get() == null || weakReference.get() == fileDownloadProgressListener) {
                        arrayList.remove(i);
                        i--;
                    }
                    i++;
                }
                if (arrayList.isEmpty()) {
                    this.loadingFileObservers.remove(str);
                }
            }
            this.observersByTag.remove(fileDownloadProgressListener.getObserverTag());
        }
    }

    public void savePresetToServer(int i) {
        Preset currentRoamingPreset;
        Preset preset;
        TL_account.saveAutoDownloadSettings saveautodownloadsettings = new TL_account.saveAutoDownloadSettings();
        if (i == 0) {
            currentRoamingPreset = getCurrentMobilePreset();
            preset = this.mobilePreset;
        } else if (i == 1) {
            currentRoamingPreset = getCurrentWiFiPreset();
            preset = this.wifiPreset;
        } else {
            currentRoamingPreset = getCurrentRoamingPreset();
            preset = this.roamingPreset;
        }
        boolean z = preset.enabled;
        TLRPC.TL_autoDownloadSettings tL_autoDownloadSettings = new TLRPC.TL_autoDownloadSettings();
        saveautodownloadsettings.settings = tL_autoDownloadSettings;
        tL_autoDownloadSettings.audio_preload_next = currentRoamingPreset.preloadMusic;
        tL_autoDownloadSettings.video_preload_large = currentRoamingPreset.preloadVideo;
        tL_autoDownloadSettings.phonecalls_less_data = currentRoamingPreset.lessCallData;
        tL_autoDownloadSettings.video_upload_maxbitrate = currentRoamingPreset.maxVideoBitrate;
        tL_autoDownloadSettings.disabled = !z;
        int i2 = 0;
        boolean z2 = false;
        boolean z3 = false;
        boolean z4 = false;
        while (true) {
            int[] iArr = currentRoamingPreset.mask;
            if (i2 >= iArr.length) {
                break;
            }
            int i3 = iArr[i2];
            if ((i3 & 1) != 0) {
                z2 = true;
            }
            if ((i3 & 4) != 0) {
                z3 = true;
            }
            if ((i3 & 8) != 0) {
                z4 = true;
            }
            if (z2 && z3 && z4) {
                break;
            } else {
                i2++;
            }
        }
        TLRPC.TL_autoDownloadSettings tL_autoDownloadSettings2 = saveautodownloadsettings.settings;
        tL_autoDownloadSettings2.photo_size_max = z2 ? (int) currentRoamingPreset.sizes[0] : 0;
        tL_autoDownloadSettings2.video_size_max = z3 ? currentRoamingPreset.sizes[1] : 0L;
        tL_autoDownloadSettings2.file_size_max = z4 ? currentRoamingPreset.sizes[2] : 0L;
        getConnectionsManager().sendRequest(saveautodownloadsettings, new RequestDelegate() {
            @Override
            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                DownloadController.lambda$savePresetToServer$3(tLObject, tL_error);
            }
        });
    }

    public void startDownloadFile(TLRPC.Document document, final MessageObject messageObject) {
        final TLRPC.Document document2;
        if (messageObject == null || (document2 = messageObject.getDocument()) == null) {
            return;
        }
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                DownloadController.this.lambda$startDownloadFile$5(document2, messageObject);
            }
        });
    }

    public void swapLoadingPriority(MessageObject messageObject, MessageObject messageObject2) {
        int indexOf = this.downloadingFiles.indexOf(messageObject);
        int indexOf2 = this.downloadingFiles.indexOf(messageObject2);
        if (indexOf >= 0 && indexOf2 >= 0) {
            this.downloadingFiles.set(indexOf, messageObject2);
            this.downloadingFiles.set(indexOf2, messageObject);
        }
        updateFilesLoadingPriority();
    }

    public void updateFilesLoadingPriority() {
        for (int size = this.downloadingFiles.size() - 1; size >= 0; size--) {
            if (getFileLoader().isLoadingFile(this.downloadingFiles.get(size).getFileName())) {
                getFileLoader().loadFile(this.downloadingFiles.get(size).getDocument(), this.downloadingFiles.get(size), 2, 0);
            }
        }
    }
}
