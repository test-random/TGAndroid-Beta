package org.telegram.messenger;

import java.io.File;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.concurrent.CountDownLatch;
import org.telegram.messenger.FileLoadOperation;
import org.telegram.messenger.FilePathDatabase;
import org.telegram.messenger.utils.ImmutableByteArrayOutputStream;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.NativeByteBuffer;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.tgnet.Vector;
import org.telegram.tgnet.tl.TL_stories;
import org.telegram.ui.LaunchActivity;
import org.telegram.ui.Storage.CacheModel;

public class FileLoadOperation {
    private static final int FINISH_CODE_DEFAULT = 0;
    private static final int FINISH_CODE_FILE_ALREADY_EXIST = 1;
    public static ImmutableByteArrayOutputStream filesQueueByteBuffer = null;
    private static int globalRequestPointer = 0;
    private static final int preloadMaxBytes = 2097152;
    private static final int stateCanceled = 4;
    private static final int stateCancelling = 5;
    private static final int stateDownloading = 1;
    private static final int stateFailed = 2;
    private static final int stateFinished = 3;
    private static final int stateIdle = 0;
    private final boolean FULL_LOGS;
    private boolean allowDisordererFileSave;
    private int bigFileSizeFrom;
    private long bytesCountPadding;
    private File cacheFileFinal;
    private boolean cacheFileFinalReady;
    private File cacheFileGzipTemp;
    private File cacheFileParts;
    private File cacheFilePreload;
    private File cacheFileTemp;
    private File cacheIvTemp;
    private final Runnable cancelAfterNoStreamListeners;
    private ArrayList<RequestInfo> cancelledRequestInfos;
    public volatile boolean caughtPremiumFloodWait;
    private byte[] cdnCheckBytes;
    private int cdnChunkCheckSize;
    private int cdnDatacenterId;
    private HashMap<Long, TLRPC.TL_fileHash> cdnHashes;
    private byte[] cdnIv;
    private byte[] cdnKey;
    private byte[] cdnToken;
    private volatile boolean closeFilePartsStreamOnWriteEnd;
    public int currentAccount;
    private int currentDownloadChunkSize;
    private int currentMaxDownloadRequests;
    private int currentType;
    private int datacenterId;
    private ArrayList<RequestInfo> delayedRequestInfos;
    private FileLoadOperationDelegate delegate;
    private long documentId;
    private int downloadChunkSize;
    private int downloadChunkSizeAnimation;
    private int downloadChunkSizeBig;
    private long downloadedBytes;
    private boolean encryptFile;
    private byte[] encryptIv;
    private byte[] encryptKey;
    private String ext;
    private FilePathDatabase.FileMeta fileMetadata;
    private String fileName;
    private RandomAccessFile fileOutputStream;
    private RandomAccessFile filePartsStream;
    private RandomAccessFile fileReadStream;
    private Runnable fileWriteRunnable;
    private RandomAccessFile fiv;
    private boolean forceSmallChunk;
    private long foundMoovSize;
    private int initialDatacenterId;
    private boolean isCdn;
    private boolean isForceRequest;
    private boolean isPreloadVideoOperation;
    public boolean isStory;
    private boolean isStream;
    private byte[] iv;
    private byte[] key;
    protected long lastProgressUpdateTime;
    protected TLRPC.InputFileLocation location;
    private int maxCdnParts;
    private int maxDownloadRequests;
    private int maxDownloadRequestsAnimation;
    private int maxDownloadRequestsBig;
    private int moovFound;
    private long nextAtomOffset;
    private boolean nextPartWasPreloaded;
    private long nextPreloadDownloadOffset;
    private ArrayList<Range> notCheckedCdnRanges;
    private ArrayList<Range> notLoadedBytesRanges;
    private volatile ArrayList<Range> notLoadedBytesRangesCopy;
    private ArrayList<Range> notRequestedBytesRanges;
    public Object parentObject;
    public FilePathDatabase.PathData pathSaveData;
    private volatile boolean paused;
    public boolean preFinished;
    private boolean preloadFinished;
    private long preloadNotRequestedBytesCount;
    private int preloadPrefixSize;
    private RandomAccessFile preloadStream;
    private int preloadStreamFileOffset;
    private byte[] preloadTempBuffer;
    private int preloadTempBufferCount;
    private HashMap<Long, PreloadRange> preloadedBytesRanges;
    private int priority;
    private FileLoaderPriorityQueue priorityQueue;
    private RequestInfo priorityRequestInfo;
    private int renameRetryCount;
    public ArrayList<RequestInfo> requestInfos;
    private long requestedBytesCount;
    private HashMap<Long, Integer> requestedPreloadedBytesRanges;
    private boolean requestingCdnOffsets;
    protected boolean requestingReference;
    private int requestsCount;
    private boolean reuploadingCdn;
    private long startTime;
    private boolean started;
    private volatile int state;
    private String storeFileName;
    private File storePath;
    FileLoadOperationStream stream;
    private ArrayList<FileLoadOperationStream> streamListeners;
    long streamOffset;
    boolean streamPriority;
    private long streamPriorityStartOffset;
    private long streamStartOffset;
    private boolean supportsPreloading;
    private File tempPath;
    public long totalBytesCount;
    private int totalPreloadedBytes;
    long totalTime;
    public final ArrayList<Integer> uiRequestTokens;
    private boolean ungzip;
    private WebFile webFile;
    private TLRPC.InputWebFileLocation webLocation;
    private volatile boolean writingToFilePartsStream;
    public static volatile DispatchQueue filesQueue = new DispatchQueue("writeFileQueue");
    private static final Object lockObject = new Object();

    public interface FileLoadOperationDelegate {
        void didChangedLoadProgress(FileLoadOperation fileLoadOperation, long j, long j2);

        void didFailedLoadingFile(FileLoadOperation fileLoadOperation, int i);

        void didFinishLoadingFile(FileLoadOperation fileLoadOperation, File file);

        void didPreFinishLoading(FileLoadOperation fileLoadOperation, File file);

        boolean hasAnotherRefOnFile(String str);

        boolean isLocallyCreatedFile(String str);

        void saveFilePath(FilePathDatabase.PathData pathData, File file);
    }

    public static class PreloadRange {
        private long fileOffset;
        private long length;

        private PreloadRange(long j, long j2) {
            this.fileOffset = j;
            this.length = j2;
        }
    }

    public static class Range {
        private long end;
        private long start;

        private Range(long j, long j2) {
            this.start = j;
            this.end = j2;
        }

        public String toString() {
            return "Range{start=" + this.start + ", end=" + this.end + '}';
        }
    }

    public static class RequestInfo {
        public boolean cancelled;
        public boolean cancelling;
        public int chunkSize;
        public int connectionType;
        private boolean forceSmallChunk;
        private long offset;
        public long requestStartTime;
        public int requestToken;
        private TLRPC.TL_upload_file response;
        private TLRPC.TL_upload_cdnFile responseCdn;
        private TLRPC.TL_upload_webFile responseWeb;
        public Runnable whenCancelled;

        protected RequestInfo() {
        }
    }

    public FileLoadOperation(int i, WebFile webFile) {
        this.FULL_LOGS = false;
        this.downloadChunkSize = 32768;
        this.downloadChunkSizeBig = 131072;
        this.cdnChunkCheckSize = 131072;
        this.maxDownloadRequests = 4;
        this.maxDownloadRequestsBig = 4;
        this.bigFileSizeFrom = 10485760;
        this.maxCdnParts = (int) (2097152000 / 131072);
        this.downloadChunkSizeAnimation = 131072;
        this.maxDownloadRequestsAnimation = 4;
        this.preloadTempBuffer = new byte[24];
        this.state = 0;
        this.uiRequestTokens = new ArrayList<>();
        this.cancelAfterNoStreamListeners = new Runnable() {
            @Override
            public final void run() {
                FileLoadOperation.this.lambda$new$6();
            }
        };
        updateParams();
        this.currentAccount = i;
        this.webFile = webFile;
        this.webLocation = webFile.location;
        this.totalBytesCount = webFile.size;
        int i2 = MessagesController.getInstance(i).webFileDatacenterId;
        this.datacenterId = i2;
        this.initialDatacenterId = i2;
        String mimeTypePart = FileLoader.getMimeTypePart(webFile.mime_type);
        this.currentType = webFile.mime_type.startsWith("image/") ? 16777216 : webFile.mime_type.equals("audio/ogg") ? 50331648 : webFile.mime_type.startsWith("video/") ? 33554432 : 67108864;
        this.allowDisordererFileSave = true;
        this.ext = ImageLoader.getHttpUrlExtension(webFile.url, mimeTypePart);
    }

    public FileLoadOperation(ImageLocation imageLocation, Object obj, String str, long j) {
        TLRPC.TL_inputStickerSetThumb tL_inputStickerSetThumb;
        this.FULL_LOGS = false;
        this.downloadChunkSize = 32768;
        this.downloadChunkSizeBig = 131072;
        this.cdnChunkCheckSize = 131072;
        this.maxDownloadRequests = 4;
        this.maxDownloadRequestsBig = 4;
        this.bigFileSizeFrom = 10485760;
        this.maxCdnParts = (int) (2097152000 / 131072);
        this.downloadChunkSizeAnimation = 131072;
        this.maxDownloadRequestsAnimation = 4;
        this.preloadTempBuffer = new byte[24];
        this.state = 0;
        this.uiRequestTokens = new ArrayList<>();
        this.cancelAfterNoStreamListeners = new Runnable() {
            @Override
            public final void run() {
                FileLoadOperation.this.lambda$new$6();
            }
        };
        updateParams();
        this.parentObject = obj;
        this.isStory = obj instanceof TL_stories.TL_storyItem;
        this.fileMetadata = FileLoader.getFileMetadataFromParent(this.currentAccount, obj);
        this.isStream = imageLocation.imageType == 2;
        if (imageLocation.isEncrypted()) {
            TLRPC.TL_inputEncryptedFileLocation tL_inputEncryptedFileLocation = new TLRPC.TL_inputEncryptedFileLocation();
            this.location = tL_inputEncryptedFileLocation;
            TLRPC.TL_fileLocationToBeDeprecated tL_fileLocationToBeDeprecated = imageLocation.location;
            long j2 = tL_fileLocationToBeDeprecated.volume_id;
            tL_inputEncryptedFileLocation.id = j2;
            tL_inputEncryptedFileLocation.volume_id = j2;
            tL_inputEncryptedFileLocation.local_id = tL_fileLocationToBeDeprecated.local_id;
            tL_inputEncryptedFileLocation.access_hash = imageLocation.access_hash;
            byte[] bArr = new byte[32];
            this.iv = bArr;
            System.arraycopy(imageLocation.iv, 0, bArr, 0, 32);
            this.key = imageLocation.key;
        } else {
            if (imageLocation.photoPeer != null) {
                TLRPC.TL_inputPeerPhotoFileLocation tL_inputPeerPhotoFileLocation = new TLRPC.TL_inputPeerPhotoFileLocation();
                TLRPC.TL_fileLocationToBeDeprecated tL_fileLocationToBeDeprecated2 = imageLocation.location;
                long j3 = tL_fileLocationToBeDeprecated2.volume_id;
                tL_inputPeerPhotoFileLocation.id = j3;
                tL_inputPeerPhotoFileLocation.volume_id = j3;
                tL_inputPeerPhotoFileLocation.local_id = tL_fileLocationToBeDeprecated2.local_id;
                tL_inputPeerPhotoFileLocation.photo_id = imageLocation.photoId;
                tL_inputPeerPhotoFileLocation.big = imageLocation.photoPeerType == 0;
                tL_inputPeerPhotoFileLocation.peer = imageLocation.photoPeer;
                tL_inputStickerSetThumb = tL_inputPeerPhotoFileLocation;
            } else if (imageLocation.stickerSet != null) {
                TLRPC.TL_inputStickerSetThumb tL_inputStickerSetThumb2 = new TLRPC.TL_inputStickerSetThumb();
                TLRPC.TL_fileLocationToBeDeprecated tL_fileLocationToBeDeprecated3 = imageLocation.location;
                long j4 = tL_fileLocationToBeDeprecated3.volume_id;
                tL_inputStickerSetThumb2.id = j4;
                tL_inputStickerSetThumb2.volume_id = j4;
                tL_inputStickerSetThumb2.local_id = tL_fileLocationToBeDeprecated3.local_id;
                tL_inputStickerSetThumb2.thumb_version = imageLocation.thumbVersion;
                tL_inputStickerSetThumb2.stickerset = imageLocation.stickerSet;
                tL_inputStickerSetThumb = tL_inputStickerSetThumb2;
            } else if (imageLocation.thumbSize != null) {
                if (imageLocation.photoId != 0) {
                    TLRPC.TL_inputPhotoFileLocation tL_inputPhotoFileLocation = new TLRPC.TL_inputPhotoFileLocation();
                    this.location = tL_inputPhotoFileLocation;
                    tL_inputPhotoFileLocation.id = imageLocation.photoId;
                    TLRPC.TL_fileLocationToBeDeprecated tL_fileLocationToBeDeprecated4 = imageLocation.location;
                    tL_inputPhotoFileLocation.volume_id = tL_fileLocationToBeDeprecated4.volume_id;
                    tL_inputPhotoFileLocation.local_id = tL_fileLocationToBeDeprecated4.local_id;
                    tL_inputPhotoFileLocation.access_hash = imageLocation.access_hash;
                    tL_inputPhotoFileLocation.file_reference = imageLocation.file_reference;
                    tL_inputPhotoFileLocation.thumb_size = imageLocation.thumbSize;
                    if (imageLocation.imageType == 2) {
                        this.allowDisordererFileSave = true;
                    }
                } else {
                    TLRPC.TL_inputDocumentFileLocation tL_inputDocumentFileLocation = new TLRPC.TL_inputDocumentFileLocation();
                    this.location = tL_inputDocumentFileLocation;
                    long j5 = imageLocation.documentId;
                    tL_inputDocumentFileLocation.id = j5;
                    this.documentId = j5;
                    TLRPC.TL_fileLocationToBeDeprecated tL_fileLocationToBeDeprecated5 = imageLocation.location;
                    tL_inputDocumentFileLocation.volume_id = tL_fileLocationToBeDeprecated5.volume_id;
                    tL_inputDocumentFileLocation.local_id = tL_fileLocationToBeDeprecated5.local_id;
                    tL_inputDocumentFileLocation.access_hash = imageLocation.access_hash;
                    tL_inputDocumentFileLocation.file_reference = imageLocation.file_reference;
                    tL_inputDocumentFileLocation.thumb_size = imageLocation.thumbSize;
                }
                TLRPC.InputFileLocation inputFileLocation = this.location;
                if (inputFileLocation.file_reference == null) {
                    inputFileLocation.file_reference = new byte[0];
                }
            } else {
                TLRPC.TL_inputFileLocation tL_inputFileLocation = new TLRPC.TL_inputFileLocation();
                this.location = tL_inputFileLocation;
                TLRPC.TL_fileLocationToBeDeprecated tL_fileLocationToBeDeprecated6 = imageLocation.location;
                tL_inputFileLocation.volume_id = tL_fileLocationToBeDeprecated6.volume_id;
                tL_inputFileLocation.local_id = tL_fileLocationToBeDeprecated6.local_id;
                tL_inputFileLocation.secret = imageLocation.access_hash;
                byte[] bArr2 = imageLocation.file_reference;
                tL_inputFileLocation.file_reference = bArr2;
                if (bArr2 == null) {
                    tL_inputFileLocation.file_reference = new byte[0];
                }
                this.allowDisordererFileSave = true;
            }
            this.location = tL_inputStickerSetThumb;
        }
        int i = imageLocation.imageType;
        this.ungzip = i == 1 || i == 3;
        int i2 = imageLocation.dc_id;
        this.datacenterId = i2;
        this.initialDatacenterId = i2;
        this.currentType = 16777216;
        this.totalBytesCount = j;
        this.ext = str == null ? "jpg" : str;
    }

    public FileLoadOperation(SecureDocument secureDocument) {
        this.FULL_LOGS = false;
        this.downloadChunkSize = 32768;
        this.downloadChunkSizeBig = 131072;
        this.cdnChunkCheckSize = 131072;
        this.maxDownloadRequests = 4;
        this.maxDownloadRequestsBig = 4;
        this.bigFileSizeFrom = 10485760;
        this.maxCdnParts = (int) (2097152000 / 131072);
        this.downloadChunkSizeAnimation = 131072;
        this.maxDownloadRequestsAnimation = 4;
        this.preloadTempBuffer = new byte[24];
        this.state = 0;
        this.uiRequestTokens = new ArrayList<>();
        this.cancelAfterNoStreamListeners = new Runnable() {
            @Override
            public final void run() {
                FileLoadOperation.this.lambda$new$6();
            }
        };
        updateParams();
        TLRPC.TL_inputSecureFileLocation tL_inputSecureFileLocation = new TLRPC.TL_inputSecureFileLocation();
        this.location = tL_inputSecureFileLocation;
        TLRPC.TL_secureFile tL_secureFile = secureDocument.secureFile;
        tL_inputSecureFileLocation.id = tL_secureFile.id;
        tL_inputSecureFileLocation.access_hash = tL_secureFile.access_hash;
        this.datacenterId = tL_secureFile.dc_id;
        this.totalBytesCount = tL_secureFile.size;
        this.allowDisordererFileSave = true;
        this.currentType = 67108864;
        this.ext = ".jpg";
    }

    public FileLoadOperation(org.telegram.tgnet.TLRPC.Document r12, java.lang.Object r13) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.FileLoadOperation.<init>(org.telegram.tgnet.TLRPC$Document, java.lang.Object):void");
    }

    private void addPart(ArrayList<Range> arrayList, long j, long j2, boolean z) {
        if (arrayList == null || j2 < j) {
            return;
        }
        int size = arrayList.size();
        boolean z2 = false;
        for (int i = 0; i < size; i++) {
            Range range = arrayList.get(i);
            long j3 = range.start;
            long j4 = range.end;
            if (j <= j3) {
                if (j2 >= j4) {
                    arrayList.remove(i);
                    z2 = true;
                    break;
                } else {
                    if (j2 > range.start) {
                        range.start = j2;
                        z2 = true;
                        break;
                    }
                }
            } else if (j2 < j4) {
                arrayList.add(0, new Range(range.start, j));
                range.start = j2;
                z2 = true;
                break;
            } else {
                if (j < range.end) {
                    range.end = j;
                    z2 = true;
                    break;
                }
            }
        }
        if (z) {
            if (!z2) {
                if (BuildVars.LOGS_ENABLED) {
                    FileLog.e(this.cacheFileFinal + " downloaded duplicate file part " + j + " - " + j2);
                    return;
                }
                return;
            }
            final ArrayList arrayList2 = new ArrayList(arrayList);
            if (this.fileWriteRunnable != null) {
                filesQueue.cancelRunnable(this.fileWriteRunnable);
            }
            synchronized (this) {
                this.writingToFilePartsStream = true;
            }
            DispatchQueue dispatchQueue = filesQueue;
            Runnable runnable = new Runnable() {
                @Override
                public final void run() {
                    FileLoadOperation.this.lambda$addPart$2(arrayList2);
                }
            };
            this.fileWriteRunnable = runnable;
            dispatchQueue.postRunnable(runnable);
            notifyStreamListeners();
        }
    }

    private boolean canFinishPreload() {
        return this.isStory && this.priority < 3;
    }

    private void cancel(final boolean z) {
        Utilities.stageQueue.postRunnable(new Runnable() {
            @Override
            public final void run() {
                FileLoadOperation.this.lambda$cancel$13(z);
            }
        });
    }

    public void lambda$cancel$13(boolean z) {
        if (this.state != 3 && this.state != 2) {
            this.state = 5;
            cancelRequests(new Runnable() {
                @Override
                public final void run() {
                    FileLoadOperation.this.lambda$cancelOnStage$14();
                }
            });
        }
        if (z) {
            File file = this.cacheFileFinal;
            if (file != null) {
                try {
                    if (!file.delete()) {
                        this.cacheFileFinal.deleteOnExit();
                    }
                } catch (Exception e) {
                    FileLog.e(e);
                }
            }
            File file2 = this.cacheFileTemp;
            if (file2 != null) {
                try {
                    if (!file2.delete()) {
                        this.cacheFileTemp.deleteOnExit();
                    }
                } catch (Exception e2) {
                    FileLog.e(e2);
                }
            }
            File file3 = this.cacheFileParts;
            if (file3 != null) {
                try {
                    if (!file3.delete()) {
                        this.cacheFileParts.deleteOnExit();
                    }
                } catch (Exception e3) {
                    FileLog.e(e3);
                }
            }
            File file4 = this.cacheIvTemp;
            if (file4 != null) {
                try {
                    if (!file4.delete()) {
                        this.cacheIvTemp.deleteOnExit();
                    }
                } catch (Exception e4) {
                    FileLog.e(e4);
                }
            }
            File file5 = this.cacheFilePreload;
            if (file5 != null) {
                try {
                    if (file5.delete()) {
                        return;
                    }
                    this.cacheFilePreload.deleteOnExit();
                } catch (Exception e5) {
                    FileLog.e(e5);
                }
            }
        }
    }

    private void cancelRequests(final Runnable runnable) {
        StringBuilder sb = new StringBuilder();
        sb.append("cancelRequests");
        sb.append(runnable != null ? " with callback" : "");
        FileLog.d(sb.toString());
        if (this.requestInfos != null) {
            final int[] iArr = new int[1];
            int[] iArr2 = new int[2];
            int i = 0;
            for (int i2 = 0; i2 < this.requestInfos.size(); i2++) {
                final RequestInfo requestInfo = this.requestInfos.get(i2);
                if (requestInfo.requestToken != 0) {
                    requestInfo.cancelling = true;
                    if (runnable == null) {
                        requestInfo.cancelled = true;
                        FileLog.d("cancelRequests cancel " + requestInfo.requestToken);
                        ConnectionsManager.getInstance(this.currentAccount).cancelRequest(requestInfo.requestToken, true);
                    } else {
                        requestInfo.whenCancelled = new Runnable() {
                            @Override
                            public final void run() {
                                FileLoadOperation.lambda$cancelRequests$15(FileLoadOperation.RequestInfo.this, iArr, runnable);
                            }
                        };
                        iArr[0] = iArr[0] + 1;
                        FileLog.d("cancelRequests cancel " + requestInfo.requestToken + " with callback");
                        ConnectionsManager.getInstance(this.currentAccount).cancelRequest(requestInfo.requestToken, true, new Runnable() {
                            @Override
                            public final void run() {
                                FileLoadOperation.lambda$cancelRequests$16(FileLoadOperation.RequestInfo.this);
                            }
                        });
                    }
                    char c = requestInfo.connectionType == 2 ? (char) 0 : (char) 1;
                    iArr2[c] = iArr2[c] + requestInfo.chunkSize;
                }
            }
            while (i < 2) {
                int i3 = i == 0 ? 2 : 65538;
                if (iArr2[i] > 1048576) {
                    ConnectionsManager.getInstance(this.currentAccount).discardConnection(this.isCdn ? this.cdnDatacenterId : this.datacenterId, i3);
                }
                i++;
            }
        }
    }

    private void cleanup() {
        try {
            RandomAccessFile randomAccessFile = this.fileOutputStream;
            if (randomAccessFile != null) {
                try {
                    randomAccessFile.getChannel().close();
                } catch (Exception e) {
                    FileLog.e(e);
                }
                this.fileOutputStream.close();
                this.fileOutputStream = null;
            }
        } catch (Exception e2) {
            FileLog.e(e2);
        }
        try {
            RandomAccessFile randomAccessFile2 = this.preloadStream;
            if (randomAccessFile2 != null) {
                try {
                    randomAccessFile2.getChannel().close();
                } catch (Exception e3) {
                    FileLog.e(e3);
                }
                this.preloadStream.close();
                this.preloadStream = null;
            }
        } catch (Exception e4) {
            FileLog.e(e4);
        }
        try {
            RandomAccessFile randomAccessFile3 = this.fileReadStream;
            if (randomAccessFile3 != null) {
                try {
                    randomAccessFile3.getChannel().close();
                } catch (Exception e5) {
                    FileLog.e(e5);
                }
                this.fileReadStream.close();
                this.fileReadStream = null;
            }
        } catch (Exception e6) {
            FileLog.e(e6);
        }
        try {
            if (this.filePartsStream != null) {
                synchronized (this) {
                    if (this.writingToFilePartsStream) {
                        this.closeFilePartsStreamOnWriteEnd = true;
                    } else {
                        try {
                            this.filePartsStream.getChannel().close();
                        } catch (Exception e7) {
                            FileLog.e(e7);
                        }
                        this.filePartsStream.close();
                        this.filePartsStream = null;
                    }
                }
            }
        } catch (Exception e8) {
            FileLog.e(e8);
        }
        try {
            RandomAccessFile randomAccessFile4 = this.fiv;
            if (randomAccessFile4 != null) {
                randomAccessFile4.close();
                this.fiv = null;
            }
        } catch (Exception e9) {
            FileLog.e(e9);
        }
        if (this.delayedRequestInfos != null) {
            for (int i = 0; i < this.delayedRequestInfos.size(); i++) {
                RequestInfo requestInfo = this.delayedRequestInfos.get(i);
                if (requestInfo.response != null) {
                    requestInfo.response.disableFree = false;
                    requestInfo.response.freeResources();
                } else if (requestInfo.responseWeb != null) {
                    requestInfo.responseWeb.disableFree = false;
                    requestInfo.responseWeb.freeResources();
                } else if (requestInfo.responseCdn != null) {
                    requestInfo.responseCdn.disableFree = false;
                    requestInfo.responseCdn.freeResources();
                }
            }
            this.delayedRequestInfos.clear();
        }
    }

    private void clearOperation(RequestInfo requestInfo, boolean z, boolean z2) {
        int[] iArr = new int[2];
        long j = Long.MAX_VALUE;
        int i = 0;
        while (i < this.requestInfos.size()) {
            final RequestInfo requestInfo2 = this.requestInfos.get(i);
            long min = Math.min(requestInfo2.offset, j);
            if (this.isPreloadVideoOperation) {
                this.requestedPreloadedBytesRanges.remove(Long.valueOf(requestInfo2.offset));
            } else {
                removePart(this.notRequestedBytesRanges, requestInfo2.offset, requestInfo2.offset + requestInfo2.chunkSize);
            }
            if (requestInfo != requestInfo2 && requestInfo2.requestToken != 0) {
                requestInfo2.cancelling = true;
                if (z2) {
                    this.cancelledRequestInfos.add(requestInfo2);
                    requestInfo2.whenCancelled = new Runnable() {
                        @Override
                        public final void run() {
                            FileLoadOperation.this.lambda$clearOperation$24(requestInfo2);
                        }
                    };
                    ConnectionsManager.getInstance(this.currentAccount).cancelRequest(requestInfo2.requestToken, true, new Runnable() {
                        @Override
                        public final void run() {
                            FileLoadOperation.lambda$clearOperation$25(FileLoadOperation.RequestInfo.this);
                        }
                    });
                } else {
                    ConnectionsManager.getInstance(this.currentAccount).cancelRequest(requestInfo2.requestToken, true);
                    requestInfo2.cancelled = true;
                }
            }
            i++;
            j = min;
        }
        int i2 = 0;
        while (i2 < 2) {
            int i3 = i2 == 0 ? 2 : 65538;
            if (iArr[i2] > 1048576) {
                ConnectionsManager.getInstance(this.currentAccount).discardConnection(this.isCdn ? this.cdnDatacenterId : this.datacenterId, i3);
            }
            i2++;
        }
        this.requestInfos.clear();
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                FileLoadOperation.this.lambda$clearOperation$26();
            }
        });
        long j2 = j;
        for (int i4 = 0; i4 < this.delayedRequestInfos.size(); i4++) {
            RequestInfo requestInfo3 = this.delayedRequestInfos.get(i4);
            if (this.isPreloadVideoOperation) {
                this.requestedPreloadedBytesRanges.remove(Long.valueOf(requestInfo3.offset));
            } else {
                removePart(this.notRequestedBytesRanges, requestInfo3.offset, requestInfo3.offset + requestInfo3.chunkSize);
            }
            if (requestInfo3.response != null) {
                requestInfo3.response.disableFree = false;
                requestInfo3.response.freeResources();
            } else if (requestInfo3.responseWeb != null) {
                requestInfo3.responseWeb.disableFree = false;
                requestInfo3.responseWeb.freeResources();
            } else if (requestInfo3.responseCdn != null) {
                requestInfo3.responseCdn.disableFree = false;
                requestInfo3.responseCdn.freeResources();
            }
            j2 = Math.min(requestInfo3.offset, j2);
        }
        this.delayedRequestInfos.clear();
        this.requestsCount = 0;
        if (!z && this.isPreloadVideoOperation) {
            this.requestedBytesCount = this.totalPreloadedBytes;
        } else if (this.notLoadedBytesRanges == null) {
            this.downloadedBytes = j2;
            this.requestedBytesCount = j2;
        }
    }

    private void copyNotLoadedRanges() {
        if (this.notLoadedBytesRanges == null) {
            return;
        }
        this.notLoadedBytesRangesCopy = new ArrayList<>(this.notLoadedBytesRanges);
    }

    private void delayRequestInfo(RequestInfo requestInfo) {
        TLObject tLObject;
        this.delayedRequestInfos.add(requestInfo);
        if (requestInfo.response != null) {
            tLObject = requestInfo.response;
        } else if (requestInfo.responseWeb != null) {
            tLObject = requestInfo.responseWeb;
        } else if (requestInfo.responseCdn == null) {
            return;
        } else {
            tLObject = requestInfo.responseCdn;
        }
        tLObject.disableFree = true;
    }

    private long findNextPreloadDownloadOffset(long j, long j2, NativeByteBuffer nativeByteBuffer) {
        long j3;
        int limit = nativeByteBuffer.limit();
        long j4 = j;
        do {
            if (j4 >= j2 - (this.preloadTempBuffer != null ? 16 : 0)) {
                j3 = j2 + limit;
                if (j4 < j3) {
                    if (j4 >= j3 - 16) {
                        long j5 = j3 - j4;
                        if (j5 > 2147483647L) {
                            throw new RuntimeException("!!!");
                        }
                        this.preloadTempBufferCount = (int) j5;
                        nativeByteBuffer.position(nativeByteBuffer.limit() - this.preloadTempBufferCount);
                        nativeByteBuffer.readBytes(this.preloadTempBuffer, 0, this.preloadTempBufferCount, false);
                        return j3;
                    }
                    if (this.preloadTempBufferCount != 0) {
                        nativeByteBuffer.position(0);
                        byte[] bArr = this.preloadTempBuffer;
                        int i = this.preloadTempBufferCount;
                        nativeByteBuffer.readBytes(bArr, i, 16 - i, false);
                        this.preloadTempBufferCount = 0;
                    } else {
                        long j6 = j4 - j2;
                        if (j6 > 2147483647L) {
                            throw new RuntimeException("!!!");
                        }
                        nativeByteBuffer.position((int) j6);
                        nativeByteBuffer.readBytes(this.preloadTempBuffer, 0, 16, false);
                    }
                    byte[] bArr2 = this.preloadTempBuffer;
                    int i2 = ((bArr2[0] & 255) << 24) + ((bArr2[1] & 255) << 16) + ((bArr2[2] & 255) << 8) + (bArr2[3] & 255);
                    if (i2 == 0) {
                        return 0L;
                    }
                    if (i2 == 1) {
                        i2 = ((bArr2[12] & 255) << 24) + ((bArr2[13] & 255) << 16) + ((bArr2[14] & 255) << 8) + (bArr2[15] & 255);
                    }
                    if (bArr2[4] == 109 && bArr2[5] == 111 && bArr2[6] == 111 && bArr2[7] == 118) {
                        return -i2;
                    }
                    j4 += i2;
                }
            }
            return 0L;
        } while (j4 < j3);
        return j4;
    }

    public static long floorDiv(long j, long j2) {
        long j3 = j / j2;
        return ((j ^ j2) >= 0 || j2 * j3 == j) ? j3 : j3 - 1;
    }

    private long getDownloadedLengthFromOffsetInternal(ArrayList<Range> arrayList, long j, long j2) {
        long j3;
        long max;
        long j4;
        if (arrayList != null && this.state != 3 && !arrayList.isEmpty()) {
            int size = arrayList.size();
            Range range = null;
            int i = 0;
            while (true) {
                if (i >= size) {
                    j4 = j2;
                    break;
                }
                Range range2 = arrayList.get(i);
                if (j <= range2.start && (range == null || range2.start < range.start)) {
                    range = range2;
                }
                if (range2.start <= j && range2.end > j) {
                    j4 = 0;
                    break;
                }
                i++;
            }
            if (j4 == 0) {
                return 0L;
            }
            if (range != null) {
                max = range.start - j;
                return Math.min(j2, max);
            }
            j3 = this.totalBytesCount;
        } else {
            if (this.state == 3) {
                return j2;
            }
            j3 = this.downloadedBytes;
            if (j3 == 0) {
                return 0L;
            }
        }
        max = Math.max(j3 - j, 0L);
        return Math.min(j2, max);
    }

    public void lambda$addPart$2(ArrayList arrayList) {
        long currentTimeMillis = System.currentTimeMillis();
        try {
        } catch (Exception e) {
            FileLog.e((Throwable) e, false);
            if (AndroidUtilities.isENOSPC(e)) {
                LaunchActivity.checkFreeDiscSpaceStatic(1);
            } else if (AndroidUtilities.isEROFS(e)) {
                SharedConfig.checkSdCard(this.cacheFileFinal);
            }
        }
        if (this.filePartsStream == null) {
            return;
        }
        int size = arrayList.size();
        int i = (size * 16) + 4;
        ImmutableByteArrayOutputStream immutableByteArrayOutputStream = filesQueueByteBuffer;
        if (immutableByteArrayOutputStream == null) {
            filesQueueByteBuffer = new ImmutableByteArrayOutputStream(i);
        } else {
            immutableByteArrayOutputStream.reset();
        }
        filesQueueByteBuffer.writeInt(size);
        for (int i2 = 0; i2 < size; i2++) {
            Range range = (Range) arrayList.get(i2);
            filesQueueByteBuffer.writeLong(range.start);
            filesQueueByteBuffer.writeLong(range.end);
        }
        synchronized (this) {
            try {
                RandomAccessFile randomAccessFile = this.filePartsStream;
                if (randomAccessFile == null) {
                    return;
                }
                randomAccessFile.seek(0L);
                this.filePartsStream.write(filesQueueByteBuffer.buf, 0, i);
                this.writingToFilePartsStream = false;
                if (this.closeFilePartsStreamOnWriteEnd) {
                    try {
                        this.filePartsStream.getChannel().close();
                    } catch (Exception e2) {
                        FileLog.e(e2);
                    }
                    this.filePartsStream.close();
                    this.filePartsStream = null;
                }
                this.totalTime += System.currentTimeMillis() - currentTimeMillis;
            } finally {
            }
        }
    }

    public void lambda$cancelOnStage$14() {
        if (this.state == 5) {
            onFail(false, 1);
        }
    }

    public static void lambda$cancelRequests$15(RequestInfo requestInfo, int[] iArr, Runnable runnable) {
        requestInfo.whenCancelled = null;
        requestInfo.cancelled = true;
        int i = iArr[0] - 1;
        iArr[0] = i;
        if (i == 0) {
            runnable.run();
        }
    }

    public static void lambda$cancelRequests$16(RequestInfo requestInfo) {
        Runnable runnable = requestInfo.whenCancelled;
        if (runnable != null) {
            runnable.run();
        }
    }

    public void lambda$clearOperation$24(RequestInfo requestInfo) {
        requestInfo.whenCancelled = null;
        this.cancelledRequestInfos.remove(requestInfo);
        requestInfo.cancelled = true;
    }

    public static void lambda$clearOperation$25(RequestInfo requestInfo) {
        Runnable runnable = requestInfo.whenCancelled;
        if (runnable != null) {
            runnable.run();
        }
    }

    public void lambda$clearOperation$26() {
        this.uiRequestTokens.clear();
    }

    public void lambda$getCurrentFile$3(File[] fileArr, CountDownLatch countDownLatch) {
        if (this.state != 3 || this.preloadFinished) {
            fileArr[0] = this.cacheFileTemp;
        } else {
            fileArr[0] = this.cacheFileFinal;
        }
        countDownLatch.countDown();
    }

    public void lambda$getDownloadedLengthFromOffset$4(long[] jArr, long j, long j2, CountDownLatch countDownLatch) {
        try {
            jArr[0] = getDownloadedLengthFromOffsetInternal(this.notLoadedBytesRanges, j, j2);
        } catch (Throwable th) {
            FileLog.e(th);
            jArr[0] = 0;
        }
        if (this.state == 3) {
            jArr[1] = 1;
        }
        countDownLatch.countDown();
    }

    public void lambda$new$6() {
        pause();
        FileLoader.getInstance(this.currentAccount).cancelLoadFile(getFileName());
    }

    public void lambda$onFail$23(int i) {
        FileLoadOperationDelegate fileLoadOperationDelegate = this.delegate;
        if (fileLoadOperationDelegate != null) {
            fileLoadOperationDelegate.didFailedLoadingFile(this, i);
        }
        notifyStreamListeners();
    }

    public void lambda$onFinishLoadingFile$17(boolean z) {
        try {
            onFinishLoadingFile(z, 0, false);
        } catch (Exception unused) {
            onFail(false, 0);
        }
    }

    public void lambda$onFinishLoadingFile$18() {
        onFail(false, 0);
    }

    public void lambda$onFinishLoadingFile$19(boolean z) {
        StatsController statsController;
        int currentNetworkType;
        int i;
        if (BuildVars.LOGS_ENABLED) {
            FileLog.d("finished downloading file to " + this.cacheFileFinal + " time = " + (System.currentTimeMillis() - this.startTime) + " dc = " + this.datacenterId + " size = " + AndroidUtilities.formatFileSize(this.totalBytesCount));
        }
        if (z) {
            int i2 = this.currentType;
            if (i2 == 50331648) {
                statsController = StatsController.getInstance(this.currentAccount);
                currentNetworkType = ApplicationLoader.getCurrentNetworkType();
                i = 3;
            } else if (i2 == 33554432) {
                statsController = StatsController.getInstance(this.currentAccount);
                currentNetworkType = ApplicationLoader.getCurrentNetworkType();
                i = 2;
            } else if (i2 == 16777216) {
                statsController = StatsController.getInstance(this.currentAccount);
                currentNetworkType = ApplicationLoader.getCurrentNetworkType();
                i = 4;
            } else if (i2 == 67108864) {
                String str = this.ext;
                if (str == null || !(str.toLowerCase().endsWith("mp3") || this.ext.toLowerCase().endsWith("m4a"))) {
                    statsController = StatsController.getInstance(this.currentAccount);
                    currentNetworkType = ApplicationLoader.getCurrentNetworkType();
                    i = 5;
                } else {
                    statsController = StatsController.getInstance(this.currentAccount);
                    currentNetworkType = ApplicationLoader.getCurrentNetworkType();
                    i = 7;
                }
            }
            statsController.incrementReceivedItemsCount(currentNetworkType, i, 1);
        }
        this.delegate.didFinishLoadingFile(this, this.cacheFileFinal);
    }

    public void lambda$onFinishLoadingFile$20(java.io.File r5, java.io.File r6, java.io.File r7, java.io.File r8, final boolean r9) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.FileLoadOperation.lambda$onFinishLoadingFile$20(java.io.File, java.io.File, java.io.File, java.io.File, boolean):void");
    }

    public void lambda$pause$7() {
        if (!this.isStory) {
            for (int i = 0; i < this.requestInfos.size(); i++) {
                ConnectionsManager.getInstance(this.currentAccount).failNotRunningRequest(this.requestInfos.get(i).requestToken);
            }
            return;
        }
        if (BuildVars.LOGS_ENABLED) {
            FileLog.d("debug_loading: " + this.cacheFileFinal.getName() + " pause operation, clear requests");
        }
        clearOperation(null, false, true);
    }

    public void lambda$processRequestResult$22(int i) {
        this.uiRequestTokens.remove(Integer.valueOf(i));
    }

    public static int lambda$removePart$1(Range range, Range range2) {
        if (range.start > range2.start) {
            return 1;
        }
        return range.start < range2.start ? -1 : 0;
    }

    public void lambda$removeStreamListener$5(FileLoadOperationStream fileLoadOperationStream) {
        if (this.streamListeners == null) {
            return;
        }
        FileLog.e("FileLoadOperation " + getFileName() + " removing stream listener " + fileLoadOperationStream);
        this.streamListeners.remove(fileLoadOperationStream);
    }

    public void lambda$requestFileOffsets$21(TLObject tLObject, TLRPC.TL_error tL_error) {
        if (tL_error != null) {
            onFail(false, 0);
            return;
        }
        if (tLObject instanceof Vector) {
            this.requestingCdnOffsets = false;
            Vector vector = (Vector) tLObject;
            if (!vector.objects.isEmpty()) {
                if (this.cdnHashes == null) {
                    this.cdnHashes = new HashMap<>();
                }
                for (int i = 0; i < vector.objects.size(); i++) {
                    TLRPC.TL_fileHash tL_fileHash = (TLRPC.TL_fileHash) vector.objects.get(i);
                    this.cdnHashes.put(Long.valueOf(tL_fileHash.offset), tL_fileHash);
                }
            }
            for (int i2 = 0; i2 < this.delayedRequestInfos.size(); i2++) {
                RequestInfo requestInfo = this.delayedRequestInfos.get(i2);
                if (this.notLoadedBytesRanges != null || this.downloadedBytes == requestInfo.offset) {
                    this.delayedRequestInfos.remove(i2);
                    if (processRequestResult(requestInfo, null)) {
                        return;
                    }
                    if (requestInfo.response != null) {
                        requestInfo.response.disableFree = false;
                        requestInfo.response.freeResources();
                        return;
                    } else if (requestInfo.responseWeb != null) {
                        requestInfo.responseWeb.disableFree = false;
                        requestInfo.responseWeb.freeResources();
                        return;
                    } else {
                        if (requestInfo.responseCdn != null) {
                            requestInfo.responseCdn.disableFree = false;
                            requestInfo.responseCdn.freeResources();
                            return;
                        }
                        return;
                    }
                }
            }
        }
    }

    public void lambda$setIsPreloadVideoOperation$12(boolean z) {
        this.requestedBytesCount = 0L;
        clearOperation(null, true, true);
        this.isPreloadVideoOperation = z;
        startDownloadRequest(-1);
    }

    public void lambda$setStream$0(FileLoadOperationStream fileLoadOperationStream) {
        if (this.streamListeners == null) {
            this.streamListeners = new ArrayList<>();
        }
        if (fileLoadOperationStream != null && !this.streamListeners.contains(fileLoadOperationStream)) {
            this.streamListeners.add(fileLoadOperationStream);
        }
        if (!this.streamListeners.isEmpty()) {
            Utilities.stageQueue.cancelRunnable(this.cancelAfterNoStreamListeners);
        }
        if (fileLoadOperationStream == null || this.state == 1 || this.state == 0) {
            return;
        }
        fileLoadOperationStream.newDataAvailable();
    }

    public void lambda$start$10() {
        startDownloadRequest(-1);
    }

    public void lambda$start$11(boolean[] zArr) {
        boolean z = this.isPreloadVideoOperation && zArr[0];
        int i = this.preloadPrefixSize;
        boolean z2 = i > 0 && this.downloadedBytes >= ((long) i) && canFinishPreload();
        long j = this.totalBytesCount;
        if (j == 0 || !(z || this.downloadedBytes == j || z2)) {
            startDownloadRequest(-1);
            return;
        }
        try {
            onFinishLoadingFile(false, 1, true);
        } catch (Exception unused) {
            onFail(true, 0);
        }
    }

    public void lambda$start$8(int i) {
        this.uiRequestTokens.remove(Integer.valueOf(i));
    }

    public void lambda$start$9(boolean z, long j, FileLoadOperationStream fileLoadOperationStream, boolean z2) {
        if (this.streamListeners == null) {
            this.streamListeners = new ArrayList<>();
        }
        if (z) {
            long j2 = this.currentDownloadChunkSize;
            long j3 = (j / j2) * j2;
            RequestInfo requestInfo = this.priorityRequestInfo;
            if (requestInfo != null && requestInfo.offset != j3) {
                RequestInfo requestInfo2 = this.priorityRequestInfo;
                final int i = requestInfo2.requestToken;
                this.requestInfos.remove(requestInfo2);
                AndroidUtilities.runOnUIThread(new Runnable() {
                    @Override
                    public final void run() {
                        FileLoadOperation.this.lambda$start$8(i);
                    }
                });
                this.requestedBytesCount -= this.currentDownloadChunkSize;
                removePart(this.notRequestedBytesRanges, this.priorityRequestInfo.offset, this.currentDownloadChunkSize + this.priorityRequestInfo.offset);
                if (this.priorityRequestInfo.requestToken != 0) {
                    ConnectionsManager.getInstance(this.currentAccount).cancelRequest(this.priorityRequestInfo.requestToken, true);
                    this.requestsCount--;
                }
                if (BuildVars.DEBUG_VERSION) {
                    FileLog.d("frame get cancel request at offset " + this.priorityRequestInfo.offset);
                }
                this.priorityRequestInfo = null;
            }
            if (this.priorityRequestInfo == null) {
                this.streamPriorityStartOffset = j3;
            }
        } else {
            long j4 = this.currentDownloadChunkSize;
            this.streamStartOffset = (j / j4) * j4;
        }
        if (!this.streamListeners.contains(fileLoadOperationStream)) {
            this.streamListeners.add(fileLoadOperationStream);
            FileLog.e("FileLoadOperation " + getFileName() + " start, adding stream " + fileLoadOperationStream);
        }
        if (!this.streamListeners.isEmpty()) {
            Utilities.stageQueue.cancelRunnable(this.cancelAfterNoStreamListeners);
        }
        if (z2) {
            if (this.preloadedBytesRanges != null && getDownloadedLengthFromOffsetInternal(this.notLoadedBytesRanges, this.streamStartOffset, 1L) == 0 && this.preloadedBytesRanges.get(Long.valueOf(this.streamStartOffset)) != null) {
                this.nextPartWasPreloaded = true;
            }
            startDownloadRequest(-1);
            this.nextPartWasPreloaded = false;
        }
        if (this.notLoadedBytesRanges != null) {
            notifyStreamListeners();
        }
    }

    public void lambda$startDownloadRequest$27(RequestInfo requestInfo) {
        processRequestResult(requestInfo, null);
        requestInfo.response.freeResources();
    }

    public void lambda$startDownloadRequest$28(int i, RequestInfo requestInfo, TLObject tLObject, TLRPC.TL_error tL_error) {
        this.reuploadingCdn = false;
        if (tLObject instanceof Vector) {
            Vector vector = (Vector) tLObject;
            if (!vector.objects.isEmpty()) {
                if (this.cdnHashes == null) {
                    this.cdnHashes = new HashMap<>();
                }
                for (int i2 = 0; i2 < vector.objects.size(); i2++) {
                    TLRPC.TL_fileHash tL_fileHash = (TLRPC.TL_fileHash) vector.objects.get(i2);
                    this.cdnHashes.put(Long.valueOf(tL_fileHash.offset), tL_fileHash);
                }
            }
        } else {
            if (tL_error == null) {
                return;
            }
            if (!tL_error.text.equals("FILE_TOKEN_INVALID") && !tL_error.text.equals("REQUEST_TOKEN_INVALID")) {
                onFail(false, 0);
                return;
            } else {
                this.isCdn = false;
                clearOperation(requestInfo, false, false);
            }
        }
        startDownloadRequest(i);
    }

    public void lambda$startDownloadRequest$29(final RequestInfo requestInfo, int i, final int i2, TLObject tLObject, TLObject tLObject2, TLRPC.TL_error tL_error) {
        StatsController statsController;
        int i3;
        long objectSize;
        int i4;
        byte[] bArr;
        if (requestInfo.cancelled) {
            FileLog.e("received chunk but definitely cancelled offset=" + requestInfo.offset + " size=" + requestInfo.chunkSize + " token=" + requestInfo.requestToken);
            return;
        }
        if (requestInfo.cancelling) {
            FileLog.e("received cancelled chunk after cancelRequests! offset=" + requestInfo.offset + " size=" + requestInfo.chunkSize + " token=" + requestInfo.requestToken);
        }
        if (!this.requestInfos.contains(requestInfo)) {
            if (!this.cancelledRequestInfos.contains(requestInfo)) {
                return;
            }
            int i5 = 0;
            boolean z = false;
            while (i5 < this.requestInfos.size()) {
                RequestInfo requestInfo2 = this.requestInfos.get(i5);
                if (requestInfo2 != null && requestInfo2 != requestInfo && requestInfo2.offset == requestInfo.offset && requestInfo2.chunkSize == requestInfo.chunkSize) {
                    FileLog.e("received cancelled chunk faster than new one! received=" + requestInfo.requestToken + " new=" + requestInfo2.requestToken);
                    if (z) {
                        this.requestInfos.remove(i5);
                        i5--;
                    } else {
                        this.requestInfos.set(i5, requestInfo);
                        z = true;
                    }
                }
                i5++;
            }
        }
        int i6 = 0;
        while (i6 < this.cancelledRequestInfos.size()) {
            RequestInfo requestInfo3 = this.cancelledRequestInfos.get(i6);
            if (requestInfo3 != null && requestInfo3 != requestInfo && requestInfo3.offset == requestInfo.offset && requestInfo3.chunkSize == requestInfo.chunkSize) {
                FileLog.e("received new chunk faster than cancelled one! received=" + requestInfo.requestToken + " cancelled=" + requestInfo3.requestToken);
                this.cancelledRequestInfos.remove(i6);
                i6 += -1;
            }
            i6++;
        }
        if (BuildVars.LOGS_ENABLED) {
            FileLog.d("debug_loading: " + this.cacheFileFinal.getName() + " time=" + (System.currentTimeMillis() - requestInfo.requestStartTime) + " dcId=" + i + " cdn=" + this.isCdn + " conType=" + i2 + " reqId" + requestInfo.requestToken);
        }
        if (requestInfo == this.priorityRequestInfo) {
            if (BuildVars.DEBUG_VERSION) {
                FileLog.d("frame get request completed " + this.priorityRequestInfo.offset);
            }
            this.priorityRequestInfo = null;
        }
        if (tL_error != null) {
            Runnable runnable = requestInfo.whenCancelled;
            if (runnable != null) {
                runnable.run();
            }
            if (tL_error.code == -2000) {
                this.requestInfos.remove(requestInfo);
                this.requestedBytesCount -= requestInfo.chunkSize;
                removePart(this.notRequestedBytesRanges, requestInfo.offset, requestInfo.offset + requestInfo.chunkSize);
                return;
            } else if (FileRefController.isFileRefError(tL_error.text)) {
                requestReference(requestInfo);
                return;
            } else if ((tLObject instanceof TLRPC.TL_upload_getCdnFile) && tL_error.text.equals("FILE_TOKEN_INVALID")) {
                this.isCdn = false;
                clearOperation(requestInfo, false, false);
                startDownloadRequest(i2);
                return;
            }
        }
        if (tLObject2 instanceof TLRPC.TL_upload_fileCdnRedirect) {
            TLRPC.TL_upload_fileCdnRedirect tL_upload_fileCdnRedirect = (TLRPC.TL_upload_fileCdnRedirect) tLObject2;
            if (!tL_upload_fileCdnRedirect.file_hashes.isEmpty()) {
                if (this.cdnHashes == null) {
                    this.cdnHashes = new HashMap<>();
                }
                for (int i7 = 0; i7 < tL_upload_fileCdnRedirect.file_hashes.size(); i7++) {
                    TLRPC.TL_fileHash tL_fileHash = tL_upload_fileCdnRedirect.file_hashes.get(i7);
                    this.cdnHashes.put(Long.valueOf(tL_fileHash.offset), tL_fileHash);
                }
            }
            byte[] bArr2 = tL_upload_fileCdnRedirect.encryption_iv;
            if (bArr2 == null || (bArr = tL_upload_fileCdnRedirect.encryption_key) == null || bArr2.length != 16 || bArr.length != 32) {
                Runnable runnable2 = requestInfo.whenCancelled;
                if (runnable2 != null) {
                    runnable2.run();
                }
                TLRPC.TL_error tL_error2 = new TLRPC.TL_error();
                tL_error2.text = "bad redirect response";
                tL_error2.code = 400;
                processRequestResult(requestInfo, tL_error2);
                return;
            }
            this.isCdn = true;
            if (this.notCheckedCdnRanges == null) {
                ArrayList<Range> arrayList = new ArrayList<>();
                this.notCheckedCdnRanges = arrayList;
                arrayList.add(new Range(0L, this.maxCdnParts));
            }
            this.cdnDatacenterId = tL_upload_fileCdnRedirect.dc_id;
            this.cdnIv = tL_upload_fileCdnRedirect.encryption_iv;
            this.cdnKey = tL_upload_fileCdnRedirect.encryption_key;
            this.cdnToken = tL_upload_fileCdnRedirect.file_token;
            clearOperation(requestInfo, false, false);
            startDownloadRequest(i2);
            return;
        }
        if (tLObject2 instanceof TLRPC.TL_upload_cdnFileReuploadNeeded) {
            if (this.reuploadingCdn) {
                return;
            }
            clearOperation(requestInfo, false, false);
            this.reuploadingCdn = true;
            TLRPC.TL_upload_reuploadCdnFile tL_upload_reuploadCdnFile = new TLRPC.TL_upload_reuploadCdnFile();
            tL_upload_reuploadCdnFile.file_token = this.cdnToken;
            tL_upload_reuploadCdnFile.request_token = ((TLRPC.TL_upload_cdnFileReuploadNeeded) tLObject2).request_token;
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(tL_upload_reuploadCdnFile, new RequestDelegate() {
                @Override
                public final void run(TLObject tLObject3, TLRPC.TL_error tL_error3) {
                    FileLoadOperation.this.lambda$startDownloadRequest$28(i2, requestInfo, tLObject3, tL_error3);
                }
            }, null, null, 0, this.datacenterId, 1, true);
            return;
        }
        if (tLObject2 instanceof TLRPC.TL_upload_file) {
            requestInfo.response = (TLRPC.TL_upload_file) tLObject2;
        } else if (tLObject2 instanceof TLRPC.TL_upload_webFile) {
            requestInfo.responseWeb = (TLRPC.TL_upload_webFile) tLObject2;
            if (this.totalBytesCount == 0 && requestInfo.responseWeb.size != 0) {
                this.totalBytesCount = requestInfo.responseWeb.size;
            }
        } else {
            requestInfo.responseCdn = (TLRPC.TL_upload_cdnFile) tLObject2;
        }
        if (tLObject2 != null) {
            int i8 = this.currentType;
            if (i8 == 50331648) {
                statsController = StatsController.getInstance(this.currentAccount);
                i3 = tLObject2.networkType;
                objectSize = tLObject2.getObjectSize() + 4;
                i4 = 3;
            } else if (i8 == 33554432) {
                statsController = StatsController.getInstance(this.currentAccount);
                i3 = tLObject2.networkType;
                objectSize = tLObject2.getObjectSize() + 4;
                i4 = 2;
            } else if (i8 == 16777216) {
                StatsController.getInstance(this.currentAccount).incrementReceivedBytesCount(tLObject2.networkType, 4, tLObject2.getObjectSize() + 4);
            } else if (i8 == 67108864) {
                String str = this.ext;
                if (str == null || !(str.toLowerCase().endsWith("mp3") || this.ext.toLowerCase().endsWith("m4a"))) {
                    statsController = StatsController.getInstance(this.currentAccount);
                    i3 = tLObject2.networkType;
                    objectSize = tLObject2.getObjectSize() + 4;
                    i4 = 5;
                } else {
                    statsController = StatsController.getInstance(this.currentAccount);
                    i3 = tLObject2.networkType;
                    objectSize = tLObject2.getObjectSize() + 4;
                    i4 = 7;
                }
            }
            statsController.incrementReceivedBytesCount(i3, i4, objectSize);
        }
        processRequestResult(requestInfo, tL_error);
        Runnable runnable3 = requestInfo.whenCancelled;
        if (runnable3 != null) {
            runnable3.run();
        }
    }

    public void lambda$startDownloadRequest$30(int i) {
        this.uiRequestTokens.add(Integer.valueOf(i));
    }

    private void notifyStreamListeners() {
        ArrayList<FileLoadOperationStream> arrayList = this.streamListeners;
        if (arrayList != null) {
            int size = arrayList.size();
            for (int i = 0; i < size; i++) {
                this.streamListeners.get(i).newDataAvailable();
            }
        }
    }

    private void onFinishLoadingFile(final boolean z, int i, boolean z2) {
        StringBuilder sb;
        if (this.state == 1 || this.state == 5) {
            this.state = 3;
            notifyStreamListeners();
            cleanup();
            if (!this.isPreloadVideoOperation && !z2) {
                final File file = this.cacheIvTemp;
                final File file2 = this.cacheFileParts;
                final File file3 = this.cacheFilePreload;
                final File file4 = this.cacheFileTemp;
                filesQueue.postRunnable(new Runnable() {
                    @Override
                    public final void run() {
                        FileLoadOperation.this.lambda$onFinishLoadingFile$20(file, file2, file3, file4, z);
                    }
                });
                this.cacheIvTemp = null;
                this.cacheFileParts = null;
                this.cacheFilePreload = null;
                this.delegate.didPreFinishLoading(this, this.cacheFileFinal);
                return;
            }
            this.preloadFinished = true;
            if (BuildVars.DEBUG_VERSION) {
                if (i == 1) {
                    sb = new StringBuilder();
                    sb.append("file already exist ");
                    sb.append(this.cacheFileTemp);
                } else {
                    sb = new StringBuilder();
                    sb.append("finished preloading file to ");
                    sb.append(this.cacheFileTemp);
                    sb.append(" loaded ");
                    sb.append(this.downloadedBytes);
                    sb.append(" of ");
                    sb.append(this.totalBytesCount);
                    sb.append(" prefSize=");
                    sb.append(this.preloadPrefixSize);
                }
                FileLog.d(sb.toString());
            }
            if (this.fileMetadata != null) {
                if (this.cacheFileTemp != null) {
                    FileLoader.getInstance(this.currentAccount).getFileDatabase().removeFiles(Collections.singletonList(new CacheModel.FileInfo(this.cacheFileTemp)));
                }
                if (this.cacheFileParts != null) {
                    FileLoader.getInstance(this.currentAccount).getFileDatabase().removeFiles(Collections.singletonList(new CacheModel.FileInfo(this.cacheFileParts)));
                }
            }
            this.delegate.didPreFinishLoading(this, this.cacheFileFinal);
            this.delegate.didFinishLoadingFile(this, this.cacheFileFinal);
        }
    }

    private void removePart(ArrayList<Range> arrayList, long j, long j2) {
        boolean z;
        if (arrayList == null || j2 < j) {
            return;
        }
        int size = arrayList.size();
        int i = 0;
        for (int i2 = 0; i2 < size; i2++) {
            Range range = arrayList.get(i2);
            if (j == range.end) {
                range.end = j2;
            } else if (j2 == range.start) {
                range.start = j;
            }
            z = true;
        }
        z = false;
        Collections.sort(arrayList, new Comparator() {
            @Override
            public final int compare(Object obj, Object obj2) {
                int lambda$removePart$1;
                lambda$removePart$1 = FileLoadOperation.lambda$removePart$1((FileLoadOperation.Range) obj, (FileLoadOperation.Range) obj2);
                return lambda$removePart$1;
            }
        });
        while (i < arrayList.size() - 1) {
            Range range2 = arrayList.get(i);
            int i3 = i + 1;
            Range range3 = arrayList.get(i3);
            if (range2.end == range3.start) {
                range2.end = range3.end;
                arrayList.remove(i3);
                i--;
            }
            i++;
        }
        if (z) {
            return;
        }
        arrayList.add(new Range(j, j2));
    }

    private void requestFileOffsets(long j) {
        if (this.requestingCdnOffsets) {
            return;
        }
        this.requestingCdnOffsets = true;
        TLRPC.TL_upload_getCdnFileHashes tL_upload_getCdnFileHashes = new TLRPC.TL_upload_getCdnFileHashes();
        tL_upload_getCdnFileHashes.file_token = this.cdnToken;
        tL_upload_getCdnFileHashes.offset = j;
        ConnectionsManager.getInstance(this.currentAccount).sendRequest(tL_upload_getCdnFileHashes, new RequestDelegate() {
            @Override
            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                FileLoadOperation.this.lambda$requestFileOffsets$21(tLObject, tL_error);
            }
        }, null, null, 0, this.datacenterId, 1, true);
    }

    private void requestReference(RequestInfo requestInfo) {
        TLRPC.Message message;
        TLRPC.MessageMedia messageMedia;
        TLRPC.WebPage webPage;
        if (this.requestingReference) {
            return;
        }
        clearOperation(null, false, false);
        this.requestingReference = true;
        Object obj = this.parentObject;
        if (obj instanceof MessageObject) {
            MessageObject messageObject = (MessageObject) obj;
            if (messageObject.getId() < 0 && (message = messageObject.messageOwner) != null && (messageMedia = message.media) != null && (webPage = messageMedia.webpage) != null) {
                this.parentObject = webPage;
                this.isStory = false;
            }
        }
        if (BuildVars.LOGS_ENABLED) {
            FileLog.d("debug_loading: " + this.cacheFileFinal.getName() + " file reference expired ");
        }
        FileRefController.getInstance(this.currentAccount).requestReference(this.parentObject, this.location, this, requestInfo);
    }

    private void updateParams() {
        int i;
        if ((this.preloadPrefixSize > 0 || MessagesController.getInstance(this.currentAccount).getfileExperimentalParams) && !this.forceSmallChunk) {
            this.downloadChunkSizeBig = 524288;
            i = 8;
        } else {
            this.downloadChunkSizeBig = 131072;
            i = 4;
        }
        this.maxDownloadRequests = i;
        this.maxDownloadRequestsBig = i;
        this.maxCdnParts = (int) (2097152000 / this.downloadChunkSizeBig);
    }

    public void cancel() {
        cancel(false);
    }

    public boolean checkPrefixPreloadFinished() {
        int i = this.preloadPrefixSize;
        if (i > 0 && this.downloadedBytes > i) {
            ArrayList<Range> arrayList = this.notLoadedBytesRanges;
            if (arrayList == null) {
                return true;
            }
            long j = Long.MAX_VALUE;
            for (int i2 = 0; i2 < arrayList.size(); i2++) {
                try {
                    j = Math.min(j, arrayList.get(i2).start);
                } catch (Throwable th) {
                    FileLog.e(th);
                    return true;
                }
            }
            if (j > this.preloadPrefixSize) {
                return true;
            }
        }
        return false;
    }

    public File getCacheFileFinal() {
        return this.cacheFileFinal;
    }

    public File getCurrentFile() {
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        final File[] fileArr = new File[1];
        Utilities.stageQueue.postRunnable(new Runnable() {
            @Override
            public final void run() {
                FileLoadOperation.this.lambda$getCurrentFile$3(fileArr, countDownLatch);
            }
        });
        try {
            countDownLatch.await();
        } catch (Exception e) {
            FileLog.e(e);
        }
        return fileArr[0];
    }

    public File getCurrentFileFast() {
        return (this.state == 3 && !this.preloadFinished && this.cacheFileFinalReady) ? this.cacheFileFinal : this.cacheFileTemp;
    }

    public int getCurrentType() {
        return this.currentType;
    }

    public int getDatacenterId() {
        return this.initialDatacenterId;
    }

    public long getDocumentId() {
        return this.documentId;
    }

    public float getDownloadedLengthFromOffset(float f) {
        ArrayList<Range> arrayList = this.notLoadedBytesRangesCopy;
        long j = this.totalBytesCount;
        if (j == 0 || arrayList == null) {
            return 0.0f;
        }
        return f + (((float) getDownloadedLengthFromOffsetInternal(arrayList, (int) (((float) j) * f), j)) / ((float) this.totalBytesCount));
    }

    public long[] getDownloadedLengthFromOffset(final long j, final long j2) {
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        final long[] jArr = new long[2];
        Utilities.stageQueue.postRunnable(new Runnable() {
            @Override
            public final void run() {
                FileLoadOperation.this.lambda$getDownloadedLengthFromOffset$4(jArr, j, j2, countDownLatch);
            }
        });
        try {
            countDownLatch.await();
        } catch (Exception unused) {
        }
        return jArr;
    }

    public String getFileName() {
        return this.fileName;
    }

    public int getPositionInQueue() {
        return getQueue().getPosition(this);
    }

    public int getPriority() {
        return this.priority;
    }

    public FileLoaderPriorityQueue getQueue() {
        return this.priorityQueue;
    }

    public boolean isFinished() {
        return this.state == 3;
    }

    public boolean isForceRequest() {
        return this.isForceRequest;
    }

    public boolean isPaused() {
        return this.paused;
    }

    public boolean isPreloadFinished() {
        return this.preloadFinished;
    }

    public boolean isPreloadVideoOperation() {
        return this.isPreloadVideoOperation;
    }

    public void onFail(boolean z, final int i) {
        StringBuilder sb;
        cleanup();
        this.state = i == 1 ? 4 : 2;
        if (this.delegate != null && BuildVars.LOGS_ENABLED) {
            long currentTimeMillis = this.startTime != 0 ? System.currentTimeMillis() - this.startTime : 0L;
            if (i == 1) {
                sb = new StringBuilder();
                sb.append("cancel downloading file to ");
                sb.append(this.cacheFileFinal);
            } else {
                sb = new StringBuilder();
                sb.append("failed downloading file to ");
                sb.append(this.cacheFileFinal);
                sb.append(" reason = ");
                sb.append(i);
            }
            sb.append(" time = ");
            sb.append(currentTimeMillis);
            sb.append(" dc = ");
            sb.append(this.datacenterId);
            sb.append(" size = ");
            sb.append(AndroidUtilities.formatFileSize(this.totalBytesCount));
            FileLog.d(sb.toString());
        }
        if (z) {
            Utilities.stageQueue.postRunnable(new Runnable() {
                @Override
                public final void run() {
                    FileLoadOperation.this.lambda$onFail$23(i);
                }
            });
            return;
        }
        FileLoadOperationDelegate fileLoadOperationDelegate = this.delegate;
        if (fileLoadOperationDelegate != null) {
            fileLoadOperationDelegate.didFailedLoadingFile(this, i);
        }
        notifyStreamListeners();
    }

    public void pause() {
        if (this.state != 1) {
            return;
        }
        this.paused = true;
        Utilities.stageQueue.postRunnable(new Runnable() {
            @Override
            public final void run() {
                FileLoadOperation.this.lambda$pause$7();
            }
        });
    }

    protected boolean processRequestResult(org.telegram.messenger.FileLoadOperation.RequestInfo r45, org.telegram.tgnet.TLRPC.TL_error r46) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.FileLoadOperation.processRequestResult(org.telegram.messenger.FileLoadOperation$RequestInfo, org.telegram.tgnet.TLRPC$TL_error):boolean");
    }

    public void removeStreamListener(final FileLoadOperationStream fileLoadOperationStream) {
        Utilities.stageQueue.postRunnable(new Runnable() {
            @Override
            public final void run() {
                FileLoadOperation.this.lambda$removeStreamListener$5(fileLoadOperationStream);
            }
        });
    }

    public void setDelegate(FileLoadOperationDelegate fileLoadOperationDelegate) {
        this.delegate = fileLoadOperationDelegate;
    }

    public void setEncryptFile(boolean z) {
        this.encryptFile = z;
        if (z) {
            this.allowDisordererFileSave = false;
        }
    }

    public void setForceRequest(boolean z) {
        this.isForceRequest = z;
    }

    public void setIsPreloadVideoOperation(final boolean z) {
        if (this.isPreloadVideoOperation != z) {
            if (!z || this.totalBytesCount > 2097152) {
                FileLog.e("setIsPreloadVideoOperation " + z + " file=" + this.fileName);
                if (!z && this.isPreloadVideoOperation) {
                    if (this.state == 3) {
                        this.isPreloadVideoOperation = z;
                        this.state = 0;
                        this.preloadFinished = false;
                        start();
                        return;
                    }
                    if (this.state == 1) {
                        Utilities.stageQueue.postRunnable(new Runnable() {
                            @Override
                            public final void run() {
                                FileLoadOperation.this.lambda$setIsPreloadVideoOperation$12(z);
                            }
                        });
                        return;
                    }
                }
                this.isPreloadVideoOperation = z;
            }
        }
    }

    public void setPaths(int i, String str, FileLoaderPriorityQueue fileLoaderPriorityQueue, File file, File file2, String str2) {
        this.storePath = file;
        this.tempPath = file2;
        this.currentAccount = i;
        this.fileName = str;
        this.storeFileName = str2;
        this.priorityQueue = fileLoaderPriorityQueue;
    }

    public void setPriority(int i) {
        this.priority = i;
    }

    public void setStream(final FileLoadOperationStream fileLoadOperationStream, boolean z, long j) {
        this.stream = fileLoadOperationStream;
        this.streamOffset = j;
        this.streamPriority = z;
        Utilities.stageQueue.postRunnable(new Runnable() {
            @Override
            public final void run() {
                FileLoadOperation.this.lambda$setStream$0(fileLoadOperationStream);
            }
        });
    }

    public boolean start() {
        return start(this.stream, this.streamOffset, this.streamPriority);
    }

    public boolean start(final org.telegram.messenger.FileLoadOperationStream r30, final long r31, final boolean r33) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.FileLoadOperation.start(org.telegram.messenger.FileLoadOperationStream, long, boolean):boolean");
    }

    public void startDownloadRequest(int r29) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.FileLoadOperation.startDownloadRequest(int):void");
    }

    public void updateProgress() {
        FileLoadOperationDelegate fileLoadOperationDelegate = this.delegate;
        if (fileLoadOperationDelegate != null) {
            long j = this.downloadedBytes;
            long j2 = this.totalBytesCount;
            if (j == j2 || j2 <= 0) {
                return;
            }
            fileLoadOperationDelegate.didChangedLoadProgress(this, j, j2);
        }
    }

    public boolean wasStarted() {
        return this.started && !this.paused;
    }
}
