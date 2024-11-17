package org.telegram.messenger;

import android.content.SharedPreferences;
import android.util.SparseArray;
import android.util.SparseIntArray;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;

public class FileUploadOperation {
    private static final int initialRequestsCount = 8;
    private static final int initialRequestsSlowNetworkCount = 1;
    private static final int maxUploadingKBytes = 2048;
    private static final int maxUploadingSlowNetworkKBytes = 32;
    private static final int minUploadChunkSize = 128;
    private static final int minUploadChunkSlowNetworkSize = 32;
    private long availableSize;
    public volatile boolean caughtPremiumFloodWait;
    private int currentAccount;
    private long currentFileId;
    private int currentPartNum;
    private int currentType;
    private int currentUploadRequetsCount;
    private FileUploadOperationDelegate delegate;
    private long estimatedSize;
    private String fileKey;
    private int fingerprint;
    private boolean forceSmallFile;
    private ArrayList<byte[]> freeRequestIvs;
    private boolean isBigFile;
    private boolean isEncrypted;
    private boolean isLastPart;
    private byte[] iv;
    private byte[] ivChange;
    private byte[] key;
    protected long lastProgressUpdateTime;
    private int lastSavedPartNum;
    private int maxRequestsCount;
    private boolean nextPartFirst;
    private int operationGuid;
    private SharedPreferences preferences;
    private byte[] readBuffer;
    private long readBytesCount;
    private int requestNum;
    private int saveInfoTimes;
    private boolean slowNetwork;
    private boolean started;
    private int state;
    private RandomAccessFile stream;
    private long totalFileSize;
    private int totalPartsCount;
    private boolean uploadFirstPartLater;
    private int uploadStartTime;
    private long uploadedBytesCount;
    private String uploadingFilePath;
    private int uploadChunkSize = 65536;
    public final SparseIntArray requestTokens = new SparseIntArray();
    public final ArrayList<Integer> uiRequestTokens = new ArrayList<>();
    private SparseArray<UploadCachedResult> cachedResults = new SparseArray<>();
    private boolean[] recalculatedEstimatedSize = {false, false};

    public interface FileUploadOperationDelegate {
        void didChangedUploadProgress(FileUploadOperation fileUploadOperation, long j, long j2);

        void didFailedUploadingFile(FileUploadOperation fileUploadOperation);

        void didFinishUploadingFile(FileUploadOperation fileUploadOperation, TLRPC.InputFile inputFile, TLRPC.InputEncryptedFile inputEncryptedFile, byte[] bArr, byte[] bArr2);
    }

    public static class UploadCachedResult {
        private long bytesOffset;
        private byte[] iv;

        private UploadCachedResult() {
        }
    }

    public FileUploadOperation(int i, String str, boolean z, long j, int i2) {
        boolean z2 = false;
        this.currentAccount = i;
        this.uploadingFilePath = str;
        this.isEncrypted = z;
        this.estimatedSize = j;
        this.currentType = i2;
        if (j != 0 && !z) {
            z2 = true;
        }
        this.uploadFirstPartLater = z2;
    }

    private void calcTotalPartsCount() {
        int i;
        long j;
        long j2;
        if (this.uploadFirstPartLater) {
            boolean z = this.isBigFile;
            long j3 = this.totalFileSize;
            if (z) {
                j2 = this.uploadChunkSize;
                j = j3 - j2;
            } else {
                j = j3 - 1024;
                j2 = this.uploadChunkSize;
            }
            i = ((int) (((j + j2) - 1) / j2)) + 1;
        } else {
            long j4 = this.totalFileSize;
            long j5 = this.uploadChunkSize;
            i = (int) (((j4 + j5) - 1) / j5);
        }
        this.totalPartsCount = i;
    }

    private void cleanup() {
        if (this.preferences == null) {
            this.preferences = ApplicationLoader.applicationContext.getSharedPreferences("uploadinfo", 0);
        }
        this.preferences.edit().remove(this.fileKey + "_time").remove(this.fileKey + "_size").remove(this.fileKey + "_uploaded").remove(this.fileKey + "_id").remove(this.fileKey + "_iv").remove(this.fileKey + "_key").remove(this.fileKey + "_ivc").commit();
        try {
            RandomAccessFile randomAccessFile = this.stream;
            if (randomAccessFile != null) {
                randomAccessFile.close();
                this.stream = null;
            }
        } catch (Exception e) {
            FileLog.e(e);
        }
        AutoDeleteMediaTask.unlockFile(this.uploadingFilePath);
    }

    public void lambda$cancel$3() {
        for (int i = 0; i < this.requestTokens.size(); i++) {
            ConnectionsManager.getInstance(this.currentAccount).cancelRequest(this.requestTokens.valueAt(i), true);
        }
    }

    public void lambda$checkNewDataAvailable$4(java.lang.Float r7, long r8, long r10) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.FileUploadOperation.lambda$checkNewDataAvailable$4(java.lang.Float, long, long):void");
    }

    public void lambda$onNetworkChanged$1(boolean z) {
        if (this.slowNetwork != z) {
            this.slowNetwork = z;
            if (BuildVars.LOGS_ENABLED) {
                FileLog.d("network changed to slow = " + this.slowNetwork);
            }
            int i = 0;
            while (true) {
                if (i >= this.requestTokens.size()) {
                    break;
                }
                ConnectionsManager.getInstance(this.currentAccount).cancelRequest(this.requestTokens.valueAt(i), true);
                i++;
            }
            this.requestTokens.clear();
            cleanup();
            this.isLastPart = false;
            this.nextPartFirst = false;
            this.requestNum = 0;
            this.currentPartNum = 0;
            this.readBytesCount = 0L;
            this.uploadedBytesCount = 0L;
            this.saveInfoTimes = 0;
            this.key = null;
            this.iv = null;
            this.ivChange = null;
            this.currentUploadRequetsCount = 0;
            this.lastSavedPartNum = 0;
            this.uploadFirstPartLater = false;
            this.cachedResults.clear();
            this.operationGuid++;
            int i2 = this.slowNetwork ? 1 : 8;
            for (int i3 = 0; i3 < i2; i3++) {
                startUploadRequest();
            }
        }
    }

    public void lambda$onNetworkChanged$2() {
        this.uiRequestTokens.clear();
    }

    public void lambda$start$0() {
        this.preferences = ApplicationLoader.applicationContext.getSharedPreferences("uploadinfo", 0);
        this.slowNetwork = ApplicationLoader.isConnectionSlow();
        if (BuildVars.LOGS_ENABLED) {
            FileLog.d("start upload on slow network = " + this.slowNetwork);
        }
        int i = this.slowNetwork ? 1 : 8;
        for (int i2 = 0; i2 < i; i2++) {
            startUploadRequest();
        }
    }

    public void lambda$startUploadRequest$5(int[] iArr) {
        this.uiRequestTokens.remove(Integer.valueOf(iArr[0]));
    }

    public void lambda$startUploadRequest$6(int i, final int[] iArr, int i2, byte[] bArr, int i3, int i4, int i5, long j, TLObject tLObject, TLRPC.TL_error tL_error) {
        StatsController statsController;
        long j2;
        int i6;
        TLRPC.InputEncryptedFile tL_inputEncryptedFileUploaded;
        byte[] bArr2;
        byte[] bArr3;
        FileUploadOperationDelegate fileUploadOperationDelegate;
        FileUploadOperation fileUploadOperation;
        TLRPC.InputFile inputFile;
        TLRPC.InputEncryptedFile inputEncryptedFile;
        StatsController statsController2;
        int currentNetworkType;
        int i7;
        TLRPC.InputFile tL_inputFile;
        byte[] bArr4 = bArr;
        if (i != this.operationGuid) {
            return;
        }
        if (BuildVars.LOGS_ENABLED) {
            FileLog.d("debug_uploading:  response reqId " + iArr[0] + " time" + this.uploadingFilePath);
        }
        int currentNetworkType2 = tLObject != null ? tLObject.networkType : ApplicationLoader.getCurrentNetworkType();
        int i8 = this.currentType;
        if (i8 == 50331648) {
            StatsController.getInstance(this.currentAccount).incrementSentBytesCount(currentNetworkType2, 3, i2);
        } else if (i8 == 33554432) {
            StatsController.getInstance(this.currentAccount).incrementSentBytesCount(currentNetworkType2, 2, i2);
        } else {
            if (i8 == 16777216) {
                statsController = StatsController.getInstance(this.currentAccount);
                j2 = i2;
                i6 = 4;
            } else if (i8 == 67108864) {
                String str = this.uploadingFilePath;
                if (str == null || !(str.toLowerCase().endsWith("mp3") || this.uploadingFilePath.toLowerCase().endsWith("m4a"))) {
                    statsController = StatsController.getInstance(this.currentAccount);
                    j2 = i2;
                    i6 = 5;
                } else {
                    statsController = StatsController.getInstance(this.currentAccount);
                    j2 = i2;
                    i6 = 7;
                }
            }
            statsController.incrementSentBytesCount(currentNetworkType2, i6, j2);
        }
        if (bArr4 != null) {
            this.freeRequestIvs.add(bArr4);
        }
        this.requestTokens.delete(i3);
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                FileUploadOperation.this.lambda$startUploadRequest$5(iArr);
            }
        });
        if (!(tLObject instanceof TLRPC.TL_boolTrue)) {
            this.state = 4;
            this.delegate.didFailedUploadingFile(this);
            cleanup();
            return;
        }
        if (this.state != 1) {
            return;
        }
        this.uploadedBytesCount += i4;
        long j3 = this.estimatedSize;
        this.delegate.didChangedUploadProgress(this, this.uploadedBytesCount, j3 != 0 ? Math.max(this.availableSize, j3) : this.totalFileSize);
        int i9 = this.currentUploadRequetsCount - 1;
        this.currentUploadRequetsCount = i9;
        if (!this.isLastPart || i9 != 0 || this.state != 1) {
            if (i9 < this.maxRequestsCount) {
                if (this.estimatedSize == 0 && !this.uploadFirstPartLater && !this.nextPartFirst) {
                    if (this.saveInfoTimes >= 4) {
                        this.saveInfoTimes = 0;
                    }
                    int i10 = this.lastSavedPartNum;
                    if (i5 == i10) {
                        this.lastSavedPartNum = i10 + 1;
                        long j4 = j;
                        while (true) {
                            UploadCachedResult uploadCachedResult = this.cachedResults.get(this.lastSavedPartNum);
                            if (uploadCachedResult == null) {
                                break;
                            }
                            j4 = uploadCachedResult.bytesOffset;
                            bArr4 = uploadCachedResult.iv;
                            this.cachedResults.remove(this.lastSavedPartNum);
                            this.lastSavedPartNum++;
                        }
                        boolean z = this.isBigFile;
                        if ((z && j4 % 1048576 == 0) || (!z && this.saveInfoTimes == 0)) {
                            SharedPreferences.Editor edit = this.preferences.edit();
                            edit.putLong(this.fileKey + "_uploaded", j4);
                            if (this.isEncrypted) {
                                edit.putString(this.fileKey + "_ivc", Utilities.bytesToHex(bArr4));
                            }
                            edit.commit();
                        }
                    } else {
                        UploadCachedResult uploadCachedResult2 = new UploadCachedResult();
                        uploadCachedResult2.bytesOffset = j;
                        if (bArr4 != null) {
                            uploadCachedResult2.iv = new byte[32];
                            System.arraycopy(bArr4, 0, uploadCachedResult2.iv, 0, 32);
                        }
                        this.cachedResults.put(i5, uploadCachedResult2);
                    }
                    this.saveInfoTimes++;
                }
                startUploadRequest();
                return;
            }
            return;
        }
        this.state = 3;
        if (this.key == null) {
            if (this.isBigFile) {
                tL_inputFile = new TLRPC.TL_inputFileBig();
            } else {
                tL_inputFile = new TLRPC.TL_inputFile();
                tL_inputFile.md5_checksum = "";
            }
            tL_inputFile.parts = this.currentPartNum;
            tL_inputFile.id = this.currentFileId;
            String str2 = this.uploadingFilePath;
            tL_inputFile.name = str2.substring(str2.lastIndexOf("/") + 1);
            bArr2 = null;
            bArr3 = null;
            fileUploadOperationDelegate = this.delegate;
            fileUploadOperation = this;
            inputFile = tL_inputFile;
            inputEncryptedFile = null;
        } else {
            if (this.isBigFile) {
                tL_inputEncryptedFileUploaded = new TLRPC.TL_inputEncryptedFileBigUploaded();
            } else {
                tL_inputEncryptedFileUploaded = new TLRPC.TL_inputEncryptedFileUploaded();
                tL_inputEncryptedFileUploaded.md5_checksum = "";
            }
            tL_inputEncryptedFileUploaded.parts = this.currentPartNum;
            tL_inputEncryptedFileUploaded.id = this.currentFileId;
            tL_inputEncryptedFileUploaded.key_fingerprint = this.fingerprint;
            FileUploadOperationDelegate fileUploadOperationDelegate2 = this.delegate;
            bArr2 = this.key;
            bArr3 = this.iv;
            fileUploadOperationDelegate = fileUploadOperationDelegate2;
            fileUploadOperation = this;
            inputFile = null;
            inputEncryptedFile = tL_inputEncryptedFileUploaded;
        }
        fileUploadOperationDelegate.didFinishUploadingFile(fileUploadOperation, inputFile, inputEncryptedFile, bArr2, bArr3);
        cleanup();
        int i11 = this.currentType;
        if (i11 == 50331648) {
            StatsController.getInstance(this.currentAccount).incrementSentItemsCount(ApplicationLoader.getCurrentNetworkType(), 3, 1);
            return;
        }
        if (i11 == 33554432) {
            StatsController.getInstance(this.currentAccount).incrementSentItemsCount(ApplicationLoader.getCurrentNetworkType(), 2, 1);
            return;
        }
        if (i11 == 16777216) {
            statsController2 = StatsController.getInstance(this.currentAccount);
            currentNetworkType = ApplicationLoader.getCurrentNetworkType();
            i7 = 4;
        } else {
            if (i11 != 67108864) {
                return;
            }
            String str3 = this.uploadingFilePath;
            if (str3 == null || !(str3.toLowerCase().endsWith("mp3") || this.uploadingFilePath.toLowerCase().endsWith("m4a"))) {
                statsController2 = StatsController.getInstance(this.currentAccount);
                currentNetworkType = ApplicationLoader.getCurrentNetworkType();
                i7 = 5;
            } else {
                statsController2 = StatsController.getInstance(this.currentAccount);
                currentNetworkType = ApplicationLoader.getCurrentNetworkType();
                i7 = 7;
            }
        }
        statsController2.incrementSentItemsCount(currentNetworkType, i7, 1);
    }

    public void lambda$startUploadRequest$7() {
        if (this.currentUploadRequetsCount < this.maxRequestsCount) {
            startUploadRequest();
        }
    }

    public void lambda$startUploadRequest$8() {
        Utilities.stageQueue.postRunnable(new Runnable() {
            @Override
            public final void run() {
                FileUploadOperation.this.lambda$startUploadRequest$7();
            }
        });
    }

    public void lambda$startUploadRequest$9(int[] iArr) {
        this.uiRequestTokens.add(Integer.valueOf(iArr[0]));
    }

    private void startUploadRequest() {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.FileUploadOperation.startUploadRequest():void");
    }

    private void storeFileUploadInfo() {
        SharedPreferences.Editor edit = this.preferences.edit();
        edit.putInt(this.fileKey + "_time", this.uploadStartTime);
        edit.putLong(this.fileKey + "_size", this.totalFileSize);
        edit.putLong(this.fileKey + "_id", this.currentFileId);
        edit.remove(this.fileKey + "_uploaded");
        if (this.isEncrypted) {
            edit.putString(this.fileKey + "_iv", Utilities.bytesToHex(this.iv));
            edit.putString(this.fileKey + "_ivc", Utilities.bytesToHex(this.ivChange));
            edit.putString(this.fileKey + "_key", Utilities.bytesToHex(this.key));
        }
        edit.commit();
    }

    public void cancel() {
        if (this.state == 3) {
            return;
        }
        this.state = 2;
        Utilities.stageQueue.postRunnable(new Runnable() {
            @Override
            public final void run() {
                FileUploadOperation.this.lambda$cancel$3();
            }
        });
        AutoDeleteMediaTask.unlockFile(this.uploadingFilePath);
        this.delegate.didFailedUploadingFile(this);
        cleanup();
    }

    public void checkNewDataAvailable(final long j, final long j2, final Float f) {
        Utilities.stageQueue.postRunnable(new Runnable() {
            @Override
            public final void run() {
                FileUploadOperation.this.lambda$checkNewDataAvailable$4(f, j2, j);
            }
        });
    }

    public long getTotalFileSize() {
        return this.totalFileSize;
    }

    public void onNetworkChanged(final boolean z) {
        if (this.state != 1) {
            return;
        }
        Utilities.stageQueue.postRunnable(new Runnable() {
            @Override
            public final void run() {
                FileUploadOperation.this.lambda$onNetworkChanged$1(z);
            }
        });
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                FileUploadOperation.this.lambda$onNetworkChanged$2();
            }
        });
    }

    public void setDelegate(FileUploadOperationDelegate fileUploadOperationDelegate) {
        this.delegate = fileUploadOperationDelegate;
    }

    public void setForceSmallFile() {
        this.forceSmallFile = true;
    }

    public void start() {
        if (this.state != 0) {
            return;
        }
        this.state = 1;
        AutoDeleteMediaTask.lockFile(this.uploadingFilePath);
        Utilities.stageQueue.postRunnable(new Runnable() {
            @Override
            public final void run() {
                FileUploadOperation.this.lambda$start$0();
            }
        });
    }
}
