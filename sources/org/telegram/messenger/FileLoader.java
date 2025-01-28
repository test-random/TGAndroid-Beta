package org.telegram.messenger;

import android.text.TextUtils;
import android.util.SparseArray;
import j$.util.concurrent.ConcurrentHashMap;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.telegram.messenger.FileLoadOperation;
import org.telegram.messenger.FileLoader;
import org.telegram.messenger.FilePathDatabase;
import org.telegram.messenger.FileUploadOperation;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.tgnet.tl.TL_stories;
import org.telegram.ui.LaunchActivity;

public class FileLoader extends BaseController {
    public static final long DEFAULT_MAX_FILE_SIZE = 2097152000;
    public static final long DEFAULT_MAX_FILE_SIZE_PREMIUM = 4194304000L;
    public static final int IMAGE_TYPE_ANIMATION = 2;
    public static final int IMAGE_TYPE_LOTTIE = 1;
    public static final int IMAGE_TYPE_SVG = 3;
    public static final int IMAGE_TYPE_SVG_WHITE = 4;
    public static final int IMAGE_TYPE_THEME_PREVIEW = 5;
    public static final int MEDIA_DIR_AUDIO = 1;
    public static final int MEDIA_DIR_CACHE = 4;
    public static final int MEDIA_DIR_DOCUMENT = 3;
    public static final int MEDIA_DIR_FILES = 5;
    public static final int MEDIA_DIR_IMAGE = 0;
    public static final int MEDIA_DIR_IMAGE_PUBLIC = 100;
    public static final int MEDIA_DIR_STORIES = 6;
    public static final int MEDIA_DIR_VIDEO = 2;
    public static final int MEDIA_DIR_VIDEO_PUBLIC = 101;
    public static final int PRELOAD_CACHE_TYPE = 11;
    public static final int PRIORITY_HIGH = 3;
    public static final int PRIORITY_LOW = 0;
    public static final int PRIORITY_NORMAL = 1;
    public static final int PRIORITY_NORMAL_UP = 2;
    private static final int PRIORITY_STREAM = 4;
    private static Pattern sentPattern;
    private int currentUploadOperationsCount;
    private int currentUploadSmallOperationsCount;
    private FileLoaderDelegate delegate;
    Runnable dumpFilesQueueRunnable;
    private final FilePathDatabase filePathDatabase;
    private String forceLoadingFile;
    private final FileLoaderPriorityQueue[] largeFilesQueue;
    private int lastReferenceId;
    private final ConcurrentHashMap<String, FileLoadOperation> loadOperationPaths;
    private final ConcurrentHashMap<String, LoadOperationUIObject> loadOperationPathsUI;
    private final HashMap<String, Boolean> loadingVideos;
    private final ConcurrentHashMap<Integer, Object> parentObjectReferences;
    private int priorityIncreasePointer;
    private final FileLoaderPriorityQueue[] smallFilesQueue;
    private final ConcurrentHashMap<String, FileUploadOperation> uploadOperationPaths;
    private final ConcurrentHashMap<String, FileUploadOperation> uploadOperationPathsEnc;
    private final LinkedList<FileUploadOperation> uploadOperationQueue;
    private final HashMap<String, Long> uploadSizes;
    private final LinkedList<FileUploadOperation> uploadSmallOperationQueue;
    private static volatile DispatchQueue fileLoaderQueue = new DispatchQueue("fileUploadQueue");
    private static SparseArray<File> mediaDirs = null;
    private static final FileLoader[] Instance = new FileLoader[4];

    public class AnonymousClass1 implements FileUploadOperation.FileUploadOperationDelegate {
        final boolean val$encrypted;
        final String val$location;
        final boolean val$small;

        AnonymousClass1(boolean z, String str, boolean z2) {
            this.val$encrypted = z;
            this.val$location = str;
            this.val$small = z2;
        }

        public void lambda$didFailedUploadingFile$1(boolean z, String str, boolean z2) {
            FileUploadOperation fileUploadOperation;
            FileLoader fileLoader = FileLoader.this;
            (z ? fileLoader.uploadOperationPathsEnc : fileLoader.uploadOperationPaths).remove(str);
            if (FileLoader.this.delegate != null) {
                FileLoader.this.delegate.fileDidFailedUpload(str, z);
            }
            FileLoader fileLoader2 = FileLoader.this;
            if (z2) {
                FileLoader.access$810(fileLoader2);
                if (FileLoader.this.currentUploadSmallOperationsCount >= 1 || (fileUploadOperation = (FileUploadOperation) FileLoader.this.uploadSmallOperationQueue.poll()) == null) {
                    return;
                } else {
                    FileLoader.access$808(FileLoader.this);
                }
            } else {
                FileLoader.access$1010(fileLoader2);
                if (FileLoader.this.currentUploadOperationsCount >= 1 || (fileUploadOperation = (FileUploadOperation) FileLoader.this.uploadOperationQueue.poll()) == null) {
                    return;
                } else {
                    FileLoader.access$1008(FileLoader.this);
                }
            }
            fileUploadOperation.start();
        }

        public void lambda$didFinishUploadingFile$0(boolean z, String str, boolean z2, TLRPC.InputFile inputFile, TLRPC.InputEncryptedFile inputEncryptedFile, byte[] bArr, byte[] bArr2, FileUploadOperation fileUploadOperation) {
            FileUploadOperation fileUploadOperation2;
            FileLoader fileLoader = FileLoader.this;
            (z ? fileLoader.uploadOperationPathsEnc : fileLoader.uploadOperationPaths).remove(str);
            FileLoader fileLoader2 = FileLoader.this;
            if (z2) {
                FileLoader.access$810(fileLoader2);
                if (FileLoader.this.currentUploadSmallOperationsCount < 1 && (fileUploadOperation2 = (FileUploadOperation) FileLoader.this.uploadSmallOperationQueue.poll()) != null) {
                    FileLoader.access$808(FileLoader.this);
                    fileUploadOperation2.start();
                }
            } else {
                FileLoader.access$1010(fileLoader2);
                if (FileLoader.this.currentUploadOperationsCount < 1 && (fileUploadOperation2 = (FileUploadOperation) FileLoader.this.uploadOperationQueue.poll()) != null) {
                    FileLoader.access$1008(FileLoader.this);
                    fileUploadOperation2.start();
                }
            }
            if (FileLoader.this.delegate != null) {
                FileLoader.this.delegate.fileDidUploaded(str, inputFile, inputEncryptedFile, bArr, bArr2, fileUploadOperation.getTotalFileSize());
            }
        }

        @Override
        public void didChangedUploadProgress(FileUploadOperation fileUploadOperation, long j, long j2) {
            if (FileLoader.this.delegate != null) {
                FileLoader.this.delegate.fileUploadProgressChanged(fileUploadOperation, this.val$location, j, j2, this.val$encrypted);
            }
        }

        @Override
        public void didFailedUploadingFile(FileUploadOperation fileUploadOperation) {
            DispatchQueue dispatchQueue = FileLoader.fileLoaderQueue;
            final boolean z = this.val$encrypted;
            final String str = this.val$location;
            final boolean z2 = this.val$small;
            dispatchQueue.postRunnable(new Runnable() {
                @Override
                public final void run() {
                    FileLoader.AnonymousClass1.this.lambda$didFailedUploadingFile$1(z, str, z2);
                }
            });
        }

        @Override
        public void didFinishUploadingFile(final FileUploadOperation fileUploadOperation, final TLRPC.InputFile inputFile, final TLRPC.InputEncryptedFile inputEncryptedFile, final byte[] bArr, final byte[] bArr2) {
            DispatchQueue dispatchQueue = FileLoader.fileLoaderQueue;
            final boolean z = this.val$encrypted;
            final String str = this.val$location;
            final boolean z2 = this.val$small;
            dispatchQueue.postRunnable(new Runnable() {
                @Override
                public final void run() {
                    FileLoader.AnonymousClass1.this.lambda$didFinishUploadingFile$0(z, str, z2, inputFile, inputEncryptedFile, bArr, bArr2, fileUploadOperation);
                }
            });
        }
    }

    public class AnonymousClass2 implements FileLoadOperation.FileLoadOperationDelegate {
        final TLRPC.Document val$document;
        final String val$fileName;
        final int val$finalType;
        final Object val$parentObject;

        AnonymousClass2(Object obj, TLRPC.Document document, String str, int i) {
            this.val$parentObject = obj;
            this.val$document = document;
            this.val$fileName = str;
            this.val$finalType = i;
        }

        public static void lambda$didPreFinishLoading$0(FileLoadOperation fileLoadOperation, FileLoaderPriorityQueue fileLoaderPriorityQueue) {
            fileLoadOperation.preFinished = true;
            fileLoaderPriorityQueue.checkLoadingOperations();
        }

        @Override
        public void didChangedLoadProgress(FileLoadOperation fileLoadOperation, long j, long j2) {
            if (FileLoader.this.delegate != null) {
                FileLoader.this.delegate.fileLoadProgressChanged(fileLoadOperation, this.val$fileName, j, j2);
            }
        }

        @Override
        public void didFailedLoadingFile(FileLoadOperation fileLoadOperation, int i) {
            FileLoader.this.loadOperationPathsUI.remove(this.val$fileName);
            FileLoader.this.checkDownloadQueue(fileLoadOperation, fileLoadOperation.getQueue());
            if (FileLoader.this.delegate != null) {
                FileLoader.this.delegate.fileDidFailedLoad(this.val$fileName, i);
            }
            if (this.val$document != null && (this.val$parentObject instanceof MessageObject) && i == 0) {
                FileLoader.this.getDownloadController().onDownloadFail((MessageObject) this.val$parentObject, i);
            } else if (i == -1) {
                LaunchActivity.checkFreeDiscSpaceStatic(2);
            }
        }

        @Override
        public void didFinishLoadingFile(FileLoadOperation fileLoadOperation, File file) {
            if (fileLoadOperation.isPreloadVideoOperation() || !fileLoadOperation.isPreloadFinished()) {
                FilePathDatabase.FileMeta fileMetadataFromParent = FileLoader.getFileMetadataFromParent(FileLoader.this.currentAccount, this.val$parentObject);
                if (fileMetadataFromParent != null) {
                    FileLoader.this.getFileLoader().getFileDatabase().saveFileDialogId(file, fileMetadataFromParent);
                }
                Object obj = this.val$parentObject;
                if (obj instanceof MessageObject) {
                    MessageObject messageObject = (MessageObject) obj;
                    if (this.val$document != null && messageObject.putInDownloadsStore) {
                        FileLoader.this.getDownloadController().onDownloadComplete(messageObject);
                    }
                }
                if (!fileLoadOperation.isPreloadVideoOperation()) {
                    FileLoader.this.loadOperationPathsUI.remove(this.val$fileName);
                    if (FileLoader.this.delegate != null) {
                        FileLoader.this.delegate.fileDidLoaded(this.val$fileName, file, this.val$parentObject, this.val$finalType);
                    }
                }
            }
            FileLoader.this.checkDownloadQueue(fileLoadOperation, fileLoadOperation.getQueue(), 0L);
        }

        @Override
        public void didPreFinishLoading(final FileLoadOperation fileLoadOperation, File file) {
            final FileLoaderPriorityQueue queue = fileLoadOperation.getQueue();
            FileLoader.fileLoaderQueue.postRunnable(new Runnable() {
                @Override
                public final void run() {
                    FileLoader.AnonymousClass2.lambda$didPreFinishLoading$0(FileLoadOperation.this, queue);
                }
            });
        }

        @Override
        public boolean hasAnotherRefOnFile(String str) {
            return FileLoader.this.getFileDatabase().hasAnotherRefOnFile(str);
        }

        @Override
        public boolean isLocallyCreatedFile(String str) {
            return FileLoader.this.getFileDatabase().isLocallyCreated(str);
        }

        @Override
        public void saveFilePath(FilePathDatabase.PathData pathData, File file) {
            FileLoader.this.getFileDatabase().putPath(pathData.id, pathData.dc, pathData.type, 0, file != null ? file.toString() : null);
        }
    }

    public interface FileLoaderDelegate {
        void fileDidFailedLoad(String str, int i);

        void fileDidFailedUpload(String str, boolean z);

        void fileDidLoaded(String str, File file, Object obj, int i);

        void fileDidUploaded(String str, TLRPC.InputFile inputFile, TLRPC.InputEncryptedFile inputEncryptedFile, byte[] bArr, byte[] bArr2, long j);

        void fileLoadProgressChanged(FileLoadOperation fileLoadOperation, String str, long j, long j2);

        void fileUploadProgressChanged(FileUploadOperation fileUploadOperation, String str, long j, long j2, boolean z);
    }

    public interface FileResolver {
        File getFile();
    }

    public static class LoadOperationUIObject {
        Runnable loadInternalRunnable;

        private LoadOperationUIObject() {
        }

        LoadOperationUIObject(AnonymousClass1 anonymousClass1) {
            this();
        }
    }

    public FileLoader(int i) {
        super(i);
        this.smallFilesQueue = new FileLoaderPriorityQueue[5];
        this.largeFilesQueue = new FileLoaderPriorityQueue[5];
        this.uploadOperationQueue = new LinkedList<>();
        this.uploadSmallOperationQueue = new LinkedList<>();
        this.uploadOperationPaths = new ConcurrentHashMap<>();
        this.uploadOperationPathsEnc = new ConcurrentHashMap<>();
        this.currentUploadOperationsCount = 0;
        this.currentUploadSmallOperationsCount = 0;
        this.loadOperationPaths = new ConcurrentHashMap<>();
        this.loadOperationPathsUI = new ConcurrentHashMap<>(10, 1.0f, 2);
        this.uploadSizes = new HashMap<>();
        this.loadingVideos = new HashMap<>();
        this.delegate = null;
        this.parentObjectReferences = new ConcurrentHashMap<>();
        this.dumpFilesQueueRunnable = new Runnable() {
            @Override
            public final void run() {
                FileLoader.this.lambda$new$18();
            }
        };
        this.filePathDatabase = new FilePathDatabase(i);
        int i2 = 0;
        while (true) {
            FileLoaderPriorityQueue[] fileLoaderPriorityQueueArr = this.smallFilesQueue;
            if (i2 >= fileLoaderPriorityQueueArr.length) {
                dumpFilesQueue();
                return;
            }
            StringBuilder sb = new StringBuilder();
            sb.append("smallFilesQueue dc");
            int i3 = i2 + 1;
            sb.append(i3);
            fileLoaderPriorityQueueArr[i2] = new FileLoaderPriorityQueue(i, sb.toString(), 0, fileLoaderQueue);
            this.largeFilesQueue[i2] = new FileLoaderPriorityQueue(i, "largeFilesQueue dc" + i3, 1, fileLoaderQueue);
            i2 = i3;
        }
    }

    static int access$1008(FileLoader fileLoader) {
        int i = fileLoader.currentUploadOperationsCount;
        fileLoader.currentUploadOperationsCount = i + 1;
        return i;
    }

    static int access$1010(FileLoader fileLoader) {
        int i = fileLoader.currentUploadOperationsCount;
        fileLoader.currentUploadOperationsCount = i - 1;
        return i;
    }

    static int access$808(FileLoader fileLoader) {
        int i = fileLoader.currentUploadSmallOperationsCount;
        fileLoader.currentUploadSmallOperationsCount = i + 1;
        return i;
    }

    static int access$810(FileLoader fileLoader) {
        int i = fileLoader.currentUploadSmallOperationsCount;
        fileLoader.currentUploadSmallOperationsCount = i - 1;
        return i;
    }

    private void addOperationToQueue(FileLoadOperation fileLoadOperation, LinkedList<FileLoadOperation> linkedList) {
        int priority = fileLoadOperation.getPriority();
        if (priority <= 0) {
            linkedList.add(fileLoadOperation);
            return;
        }
        int size = linkedList.size();
        int size2 = linkedList.size();
        int i = 0;
        while (true) {
            if (i >= size2) {
                break;
            }
            if (linkedList.get(i).getPriority() < priority) {
                size = i;
                break;
            }
            i++;
        }
        linkedList.add(size, fileLoadOperation);
    }

    private void awaitFileLoadOperation(CountDownLatch countDownLatch, boolean z) {
        try {
            countDownLatch.await();
        } catch (Exception e) {
            FileLog.e((Throwable) e, false);
            if (z) {
                awaitFileLoadOperation(countDownLatch, false);
            }
        }
    }

    public static long bytesToLong(byte[] bArr) {
        long j = 0;
        for (int i = 0; i < 8; i++) {
            j = (j << 8) ^ (bArr[i] & 255);
        }
        return j;
    }

    public static boolean canSaveAsFile(Object obj) {
        if (obj instanceof MessageObject) {
            MessageObject messageObject = (MessageObject) obj;
            if (messageObject.isDocument() && !messageObject.isRoundVideo() && !messageObject.isVoice()) {
                return true;
            }
        }
        return false;
    }

    private boolean canSaveToPublicStorage(Object obj) {
        FilePathDatabase.FileMeta fileMetadataFromParent;
        MessageObject messageObject;
        if (!BuildVars.NO_SCOPED_STORAGE && (fileMetadataFromParent = getFileMetadataFromParent(this.currentAccount, obj)) != null) {
            long j = fileMetadataFromParent.dialogId;
            long j2 = -j;
            if (!getMessagesController().isChatNoForwards(getMessagesController().getChat(Long.valueOf(j2))) && !DialogObject.isEncryptedDialog(j)) {
                int i = 2;
                if (obj instanceof MessageObject) {
                    messageObject = (MessageObject) obj;
                    if (messageObject.isRoundVideo() || messageObject.isVoice() || messageObject.isAnyKindOfSticker() || messageObject.messageOwner.noforwards) {
                        return false;
                    }
                } else {
                    int i2 = fileMetadataFromParent.messageType;
                    if (i2 != 5 && i2 != 13 && i2 != 2) {
                        messageObject = null;
                    }
                }
                if (j >= 0) {
                    i = 1;
                } else if (ChatObject.isChannelAndNotMegaGroup(getMessagesController().getChat(Long.valueOf(j2)))) {
                    i = 4;
                }
                if (SaveToGallerySettingsHelper.needSave(i, fileMetadataFromParent, messageObject, this.currentAccount)) {
                    return true;
                }
            }
        }
        return false;
    }

    private void cancelLoadFile(TLRPC.Document document, SecureDocument secureDocument, WebFile webFile, TLRPC.FileLocation fileLocation, String str, final String str2, boolean z) {
        if (fileLocation == null && document == null && webFile == null && secureDocument == null && TextUtils.isEmpty(str2)) {
            return;
        }
        if (fileLocation != null) {
            str2 = getAttachFileName(fileLocation, str);
        } else if (document != null) {
            str2 = getAttachFileName(document);
        } else if (secureDocument != null) {
            str2 = getAttachFileName(secureDocument);
        } else if (webFile != null) {
            str2 = getAttachFileName(webFile);
        }
        LoadOperationUIObject remove = this.loadOperationPathsUI.remove(str2);
        Runnable runnable = remove != null ? remove.loadInternalRunnable : null;
        boolean z2 = remove != null;
        if (runnable != null) {
            fileLoaderQueue.cancelRunnable(runnable);
        }
        fileLoaderQueue.postRunnable(new Runnable() {
            @Override
            public final void run() {
                FileLoader.this.lambda$cancelLoadFile$7(str2);
            }
        });
        if (!z2 || document == null) {
            return;
        }
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                FileLoader.this.lambda$cancelLoadFile$8();
            }
        });
    }

    public static File checkDirectory(int i) {
        return mediaDirs.get(i);
    }

    public void checkDownloadQueue(FileLoadOperation fileLoadOperation, FileLoaderPriorityQueue fileLoaderPriorityQueue) {
        checkDownloadQueue(fileLoadOperation, fileLoaderPriorityQueue, 0L);
    }

    public void checkDownloadQueue(final FileLoadOperation fileLoadOperation, final FileLoaderPriorityQueue fileLoaderPriorityQueue, long j) {
        fileLoaderQueue.postRunnable(new Runnable() {
            @Override
            public final void run() {
                FileLoader.this.lambda$checkDownloadQueue$15(fileLoaderPriorityQueue, fileLoadOperation);
            }
        }, j);
    }

    public static boolean checkUploadFileSize(int i, long j) {
        boolean isPremium = AccountInstance.getInstance(i).getUserConfig().isPremium();
        if (j >= 2097152000) {
            return j < 4194304000L && isPremium;
        }
        return true;
    }

    public static boolean copyFile(InputStream inputStream, File file) {
        return copyFile(inputStream, file, -1);
    }

    public static boolean copyFile(InputStream inputStream, File file, int i) {
        FileOutputStream fileOutputStream = new FileOutputStream(file);
        byte[] bArr = new byte[4096];
        int i2 = 0;
        while (true) {
            int read = inputStream.read(bArr);
            if (read <= 0) {
                break;
            }
            Thread.yield();
            fileOutputStream.write(bArr, 0, read);
            i2 += read;
            if (i > 0 && i2 >= i) {
                break;
            }
        }
        fileOutputStream.getFD().sync();
        fileOutputStream.close();
        return true;
    }

    public static String fixFileName(String str) {
        return str != null ? str.replaceAll("[\u0001-\u001f<>\u202e:\"/\\\\|?*\u007f]+", "").trim() : str;
    }

    public static String getAttachFileName(TLObject tLObject) {
        return getAttachFileName(tLObject, null);
    }

    public static String getAttachFileName(TLObject tLObject, String str) {
        return getAttachFileName(tLObject, null, str);
    }

    public static String getAttachFileName(TLObject tLObject, String str, String str2) {
        if (tLObject instanceof TLRPC.Document) {
            TLRPC.Document document = (TLRPC.Document) tLObject;
            String documentFileName = getDocumentFileName(document);
            int lastIndexOf = documentFileName.lastIndexOf(46);
            String substring = lastIndexOf != -1 ? documentFileName.substring(lastIndexOf) : "";
            if (substring.length() <= 1) {
                substring = getExtensionByMimeType(document.mime_type);
            }
            if (substring.length() <= 1) {
                return document.dc_id + "_" + document.id;
            }
            return document.dc_id + "_" + document.id + substring;
        }
        if (tLObject instanceof SecureDocument) {
            SecureDocument secureDocument = (SecureDocument) tLObject;
            return secureDocument.secureFile.dc_id + "_" + secureDocument.secureFile.id + ".jpg";
        }
        if (tLObject instanceof TLRPC.TL_secureFile) {
            TLRPC.TL_secureFile tL_secureFile = (TLRPC.TL_secureFile) tLObject;
            return tL_secureFile.dc_id + "_" + tL_secureFile.id + ".jpg";
        }
        if (tLObject instanceof WebFile) {
            WebFile webFile = (WebFile) tLObject;
            return Utilities.MD5(webFile.url) + "." + ImageLoader.getHttpUrlExtension(webFile.url, getMimeTypePart(webFile.mime_type));
        }
        if (tLObject instanceof TLRPC.PhotoSize) {
            TLRPC.PhotoSize photoSize = (TLRPC.PhotoSize) tLObject;
            TLRPC.FileLocation fileLocation = photoSize.location;
            if (fileLocation == null || (fileLocation instanceof TLRPC.TL_fileLocationUnavailable)) {
                return "";
            }
            StringBuilder sb = new StringBuilder();
            sb.append(photoSize.location.volume_id);
            sb.append("_");
            sb.append(photoSize.location.local_id);
            sb.append(".");
            if (str2 == null) {
                str2 = "jpg";
            }
            sb.append(str2);
            return sb.toString();
        }
        if (tLObject instanceof TLRPC.TL_videoSize) {
            TLRPC.TL_videoSize tL_videoSize = (TLRPC.TL_videoSize) tLObject;
            TLRPC.FileLocation fileLocation2 = tL_videoSize.location;
            if (fileLocation2 == null || (fileLocation2 instanceof TLRPC.TL_fileLocationUnavailable)) {
                return "";
            }
            StringBuilder sb2 = new StringBuilder();
            sb2.append(tL_videoSize.location.volume_id);
            sb2.append("_");
            sb2.append(tL_videoSize.location.local_id);
            sb2.append(".");
            if (str2 == null) {
                str2 = "mp4";
            }
            sb2.append(str2);
            return sb2.toString();
        }
        if (tLObject instanceof TLRPC.FileLocation) {
            if (tLObject instanceof TLRPC.TL_fileLocationUnavailable) {
                return "";
            }
            TLRPC.FileLocation fileLocation3 = (TLRPC.FileLocation) tLObject;
            StringBuilder sb3 = new StringBuilder();
            sb3.append(fileLocation3.volume_id);
            sb3.append("_");
            sb3.append(fileLocation3.local_id);
            sb3.append(".");
            if (str2 == null) {
                str2 = "jpg";
            }
            sb3.append(str2);
            return sb3.toString();
        }
        if (tLObject instanceof TLRPC.UserProfilePhoto) {
            if (str == null) {
                str = "s";
            }
            TLRPC.UserProfilePhoto userProfilePhoto = (TLRPC.UserProfilePhoto) tLObject;
            if (userProfilePhoto.photo_small != null) {
                return "s".equals(str) ? getAttachFileName(userProfilePhoto.photo_small, str2) : getAttachFileName(userProfilePhoto.photo_big, str2);
            }
            StringBuilder sb4 = new StringBuilder();
            sb4.append(userProfilePhoto.photo_id);
            sb4.append("_");
            sb4.append(str);
            sb4.append(".");
            if (str2 == null) {
                str2 = "jpg";
            }
            sb4.append(str2);
            return sb4.toString();
        }
        if (!(tLObject instanceof TLRPC.ChatPhoto)) {
            return "";
        }
        TLRPC.ChatPhoto chatPhoto = (TLRPC.ChatPhoto) tLObject;
        if (chatPhoto.photo_small != null) {
            return "s".equals(str) ? getAttachFileName(chatPhoto.photo_small, str2) : getAttachFileName(chatPhoto.photo_big, str2);
        }
        StringBuilder sb5 = new StringBuilder();
        sb5.append(chatPhoto.photo_id);
        sb5.append("_");
        sb5.append(str);
        sb5.append(".");
        if (str2 == null) {
            str2 = "jpg";
        }
        sb5.append(str2);
        return sb5.toString();
    }

    public static TLRPC.PhotoSize getClosestPhotoSizeWithSize(ArrayList<TLRPC.PhotoSize> arrayList, int i) {
        return getClosestPhotoSizeWithSize(arrayList, i, false);
    }

    public static TLRPC.PhotoSize getClosestPhotoSizeWithSize(ArrayList<TLRPC.PhotoSize> arrayList, int i, boolean z) {
        return getClosestPhotoSizeWithSize(arrayList, i, z, null, false);
    }

    public static org.telegram.tgnet.TLRPC.PhotoSize getClosestPhotoSizeWithSize(java.util.ArrayList<org.telegram.tgnet.TLRPC.PhotoSize> r8, int r9, boolean r10, org.telegram.tgnet.TLRPC.PhotoSize r11, boolean r12) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.FileLoader.getClosestPhotoSizeWithSize(java.util.ArrayList, int, boolean, org.telegram.tgnet.TLRPC$PhotoSize, boolean):org.telegram.tgnet.TLRPC$PhotoSize");
    }

    public static TLRPC.VideoSize getClosestVideoSizeWithSize(ArrayList<TLRPC.VideoSize> arrayList, int i) {
        return getClosestVideoSizeWithSize(arrayList, i, false);
    }

    public static TLRPC.VideoSize getClosestVideoSizeWithSize(ArrayList<TLRPC.VideoSize> arrayList, int i, boolean z) {
        return getClosestVideoSizeWithSize(arrayList, i, z, false);
    }

    public static org.telegram.tgnet.TLRPC.VideoSize getClosestVideoSizeWithSize(java.util.ArrayList<org.telegram.tgnet.TLRPC.VideoSize> r7, int r8, boolean r9, boolean r10) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.FileLoader.getClosestVideoSizeWithSize(java.util.ArrayList, int, boolean, boolean):org.telegram.tgnet.TLRPC$VideoSize");
    }

    public static File getDirectory(int i) {
        File file = mediaDirs.get(i);
        if (file == null && i != 4) {
            file = mediaDirs.get(4);
        }
        if (BuildVars.NO_SCOPED_STORAGE && file != null) {
            try {
                if (!file.isDirectory()) {
                    file.mkdirs();
                }
            } catch (Exception unused) {
            }
        }
        return file;
    }

    public static String getDocumentExtension(TLRPC.Document document) {
        String documentFileName = getDocumentFileName(document);
        int lastIndexOf = documentFileName.lastIndexOf(46);
        String substring = lastIndexOf != -1 ? documentFileName.substring(lastIndexOf + 1) : null;
        if (substring == null || substring.length() == 0) {
            substring = document.mime_type;
        }
        if (substring == null) {
            substring = "";
        }
        return substring.toUpperCase();
    }

    public static String getDocumentFileName(TLRPC.Document document) {
        String str = null;
        if (document == null) {
            return null;
        }
        String str2 = document.file_name_fixed;
        if (str2 != null) {
            return str2;
        }
        String str3 = document.file_name;
        if (str3 == null) {
            for (int i = 0; i < document.attributes.size(); i++) {
                TLRPC.DocumentAttribute documentAttribute = document.attributes.get(i);
                if (documentAttribute instanceof TLRPC.TL_documentAttributeFilename) {
                    str = documentAttribute.file_name;
                }
            }
            str3 = str;
        }
        String fixFileName = fixFileName(str3);
        return fixFileName != null ? fixFileName : "";
    }

    public static TLRPC.VideoSize getEmojiMarkup(ArrayList<TLRPC.VideoSize> arrayList) {
        for (int i = 0; i < arrayList.size(); i++) {
            if ((arrayList.get(i) instanceof TLRPC.TL_videoSizeEmojiMarkup) || (arrayList.get(i) instanceof TLRPC.TL_videoSizeStickerMarkup)) {
                return arrayList.get(i);
            }
        }
        return null;
    }

    public static String getExtensionByMimeType(String str) {
        if (str == null) {
            return "";
        }
        char c = 65535;
        switch (str.hashCode()) {
            case 187091926:
                if (str.equals("audio/ogg")) {
                    c = 0;
                    break;
                }
                break;
            case 1331848029:
                if (str.equals("video/mp4")) {
                    c = 1;
                    break;
                }
                break;
            case 2039520277:
                if (str.equals("video/x-matroska")) {
                    c = 2;
                    break;
                }
                break;
        }
        switch (c) {
            case 0:
                return ".ogg";
            case 1:
                return ".mp4";
            case 2:
                return ".mkv";
            default:
                return "";
        }
    }

    public static String getFileExtension(File file) {
        String name = file.getName();
        try {
            return name.substring(name.lastIndexOf(46) + 1);
        } catch (Exception unused) {
            return "";
        }
    }

    public static FilePathDatabase.FileMeta getFileMetadataFromParent(int i, Object obj) {
        if (!(obj instanceof String)) {
            if (!(obj instanceof MessageObject)) {
                if (!(obj instanceof TL_stories.StoryItem)) {
                    return null;
                }
                FilePathDatabase.FileMeta fileMeta = new FilePathDatabase.FileMeta();
                fileMeta.dialogId = ((TL_stories.StoryItem) obj).dialogId;
                fileMeta.messageType = 23;
                return fileMeta;
            }
            MessageObject messageObject = (MessageObject) obj;
            FilePathDatabase.FileMeta fileMeta2 = new FilePathDatabase.FileMeta();
            fileMeta2.messageId = messageObject.getId();
            fileMeta2.dialogId = messageObject.getDialogId();
            fileMeta2.messageType = messageObject.type;
            fileMeta2.messageSize = messageObject.getSize();
            return fileMeta2;
        }
        String str = (String) obj;
        if (!str.startsWith("sent_")) {
            return null;
        }
        if (sentPattern == null) {
            sentPattern = Pattern.compile("sent_.*_([0-9]+)_([0-9]+)_([0-9]+)_([0-9]+)");
        }
        try {
            Matcher matcher = sentPattern.matcher(str);
            if (!matcher.matches()) {
                return null;
            }
            FilePathDatabase.FileMeta fileMeta3 = new FilePathDatabase.FileMeta();
            fileMeta3.messageId = Integer.parseInt(matcher.group(1));
            fileMeta3.dialogId = Long.parseLong(matcher.group(2));
            fileMeta3.messageType = Integer.parseInt(matcher.group(3));
            fileMeta3.messageSize = Long.parseLong(matcher.group(4));
            return fileMeta3;
        } catch (Exception e) {
            FileLog.e(e);
            return null;
        }
    }

    public static FileLoader getInstance(int i) {
        FileLoader[] fileLoaderArr = Instance;
        FileLoader fileLoader = fileLoaderArr[i];
        if (fileLoader == null) {
            synchronized (FileLoader.class) {
                try {
                    fileLoader = fileLoaderArr[i];
                    if (fileLoader == null) {
                        fileLoader = new FileLoader(i);
                        fileLoaderArr[i] = fileLoader;
                    }
                } finally {
                }
            }
        }
        return fileLoader;
    }

    public static File getInternalCacheDir() {
        return ApplicationLoader.applicationContext.getCacheDir();
    }

    public static String getMessageFileName(TLRPC.Message message) {
        TLRPC.WebDocument webDocument;
        TLRPC.PhotoSize closestPhotoSizeWithSize;
        TLRPC.PhotoSize closestPhotoSizeWithSize2;
        TLRPC.PhotoSize closestPhotoSizeWithSize3;
        if (message == null) {
            return "";
        }
        if (message instanceof TLRPC.TL_messageService) {
            TLRPC.Photo photo = message.action.photo;
            if (photo != null) {
                ArrayList<TLRPC.PhotoSize> arrayList = photo.sizes;
                if (arrayList.size() > 0 && (closestPhotoSizeWithSize3 = getClosestPhotoSizeWithSize(arrayList, AndroidUtilities.getPhotoSize())) != null) {
                    return getAttachFileName(closestPhotoSizeWithSize3);
                }
            }
        } else {
            if (MessageObject.getMedia(message) instanceof TLRPC.TL_messageMediaDocument) {
                return getAttachFileName(MessageObject.getMedia(message).document);
            }
            if (MessageObject.getMedia(message) instanceof TLRPC.TL_messageMediaPhoto) {
                ArrayList<TLRPC.PhotoSize> arrayList2 = MessageObject.getMedia(message).photo.sizes;
                if (arrayList2.size() > 0 && (closestPhotoSizeWithSize2 = getClosestPhotoSizeWithSize(arrayList2, AndroidUtilities.getPhotoSize(), false, null, true)) != null) {
                    return getAttachFileName(closestPhotoSizeWithSize2);
                }
            } else if (MessageObject.getMedia(message) instanceof TLRPC.TL_messageMediaWebPage) {
                if (MessageObject.getMedia(message).webpage.document != null) {
                    return getAttachFileName(MessageObject.getMedia(message).webpage.document);
                }
                if (MessageObject.getMedia(message).webpage.photo != null) {
                    ArrayList<TLRPC.PhotoSize> arrayList3 = MessageObject.getMedia(message).webpage.photo.sizes;
                    if (arrayList3.size() > 0 && (closestPhotoSizeWithSize = getClosestPhotoSizeWithSize(arrayList3, AndroidUtilities.getPhotoSize())) != null) {
                        return getAttachFileName(closestPhotoSizeWithSize);
                    }
                }
            } else if ((MessageObject.getMedia(message) instanceof TLRPC.TL_messageMediaInvoice) && (webDocument = ((TLRPC.TL_messageMediaInvoice) MessageObject.getMedia(message)).webPhoto) != null) {
                return Utilities.MD5(webDocument.url) + "." + ImageLoader.getHttpUrlExtension(webDocument.url, getMimeTypePart(webDocument.mime_type));
            }
        }
        return "";
    }

    public static String getMimeTypePart(String str) {
        int lastIndexOf = str.lastIndexOf(47);
        return lastIndexOf != -1 ? str.substring(lastIndexOf + 1) : "";
    }

    public static TLRPC.TL_photoPathSize getPathPhotoSize(ArrayList<TLRPC.PhotoSize> arrayList) {
        if (arrayList != null && !arrayList.isEmpty()) {
            for (int i = 0; i < arrayList.size(); i++) {
                TLRPC.PhotoSize photoSize = arrayList.get(i);
                if (!(photoSize instanceof TLRPC.TL_photoPathSize)) {
                    return (TLRPC.TL_photoPathSize) photoSize;
                }
            }
        }
        return null;
    }

    public static long getPhotoId(TLObject tLObject) {
        if (tLObject instanceof TLRPC.Photo) {
            return ((TLRPC.Photo) tLObject).id;
        }
        if (tLObject instanceof TLRPC.ChatPhoto) {
            return ((TLRPC.ChatPhoto) tLObject).photo_id;
        }
        if (tLObject instanceof TLRPC.UserProfilePhoto) {
            return ((TLRPC.UserProfilePhoto) tLObject).photo_id;
        }
        return 0L;
    }

    private int getPriorityValue(int i) {
        int i2;
        int i3;
        if (i == 4) {
            return Integer.MAX_VALUE;
        }
        if (i == 3) {
            i2 = this.priorityIncreasePointer;
            this.priorityIncreasePointer = i2 + 1;
            i3 = 1048577;
        } else {
            if (i != 2) {
                return i == 1 ? 65536 : 0;
            }
            i2 = this.priorityIncreasePointer;
            this.priorityIncreasePointer = i2 + 1;
            i3 = 65537;
        }
        return i2 + i3;
    }

    public static TLRPC.TL_photoStrippedSize getStrippedPhotoSize(ArrayList<TLRPC.PhotoSize> arrayList) {
        if (arrayList == null) {
            return null;
        }
        for (int i = 0; i < arrayList.size(); i++) {
            TLRPC.PhotoSize photoSize = arrayList.get(i);
            if (photoSize instanceof TLRPC.TL_photoStrippedSize) {
                return (TLRPC.TL_photoStrippedSize) photoSize;
            }
        }
        return null;
    }

    public static TLRPC.VideoSize getVectorMarkupVideoSize(TLRPC.Photo photo) {
        if (photo != null && photo.video_sizes != null) {
            for (int i = 0; i < photo.video_sizes.size(); i++) {
                TLRPC.VideoSize videoSize = photo.video_sizes.get(i);
                if ((videoSize instanceof TLRPC.TL_videoSizeEmojiMarkup) || (videoSize instanceof TLRPC.TL_videoSizeStickerMarkup)) {
                    return videoSize;
                }
            }
        }
        return null;
    }

    public static boolean isSamePhoto(TLObject tLObject, TLObject tLObject2) {
        if ((tLObject == null && tLObject2 != null) || (tLObject != null && tLObject2 == null)) {
            return false;
        }
        if (tLObject == null && tLObject2 == null) {
            return true;
        }
        if (tLObject.getClass() != tLObject2.getClass()) {
            return false;
        }
        return tLObject instanceof TLRPC.UserProfilePhoto ? ((TLRPC.UserProfilePhoto) tLObject).photo_id == ((TLRPC.UserProfilePhoto) tLObject2).photo_id : (tLObject instanceof TLRPC.ChatPhoto) && ((TLRPC.ChatPhoto) tLObject).photo_id == ((TLRPC.ChatPhoto) tLObject2).photo_id;
    }

    public static boolean isSamePhoto(TLRPC.FileLocation fileLocation, TLRPC.Photo photo) {
        if (fileLocation != null && (photo instanceof TLRPC.TL_photo)) {
            int size = photo.sizes.size();
            for (int i = 0; i < size; i++) {
                TLRPC.FileLocation fileLocation2 = photo.sizes.get(i).location;
                if (fileLocation2 != null && fileLocation2.local_id == fileLocation.local_id && fileLocation2.volume_id == fileLocation.volume_id) {
                    return true;
                }
            }
            if ((-fileLocation.volume_id) == photo.id) {
                return true;
            }
        }
        return false;
    }

    public static boolean isVideoMimeType(String str) {
        return "video/mp4".equals(str) || (SharedConfig.streamMkv && "video/x-matroska".equals(str));
    }

    public void lambda$cancel$10() {
        getNotificationCenter().lambda$postNotificationNameOnUIThread$1(NotificationCenter.onDownloadingFilesChanged, new Object[0]);
    }

    public void lambda$cancel$9(String str) {
        FileLoadOperation remove = this.loadOperationPaths.remove(str);
        if (remove != null) {
            remove.getQueue().cancel(remove);
        }
    }

    public void lambda$cancelFileUpload$2(boolean z, String str) {
        FileUploadOperation fileUploadOperation = (!z ? this.uploadOperationPaths : this.uploadOperationPathsEnc).get(str);
        this.uploadSizes.remove(str);
        if (fileUploadOperation != null) {
            this.uploadOperationPathsEnc.remove(str);
            this.uploadOperationQueue.remove(fileUploadOperation);
            this.uploadSmallOperationQueue.remove(fileUploadOperation);
            fileUploadOperation.cancel();
        }
    }

    public void lambda$cancelLoadAllFiles$12(String str) {
        FileLoadOperation remove = this.loadOperationPaths.remove(str);
        if (remove != null) {
            remove.getQueue().cancel(remove);
        }
    }

    public void lambda$cancelLoadFile$7(String str) {
        FileLoadOperation remove = this.loadOperationPaths.remove(str);
        if (remove != null) {
            remove.getQueue().cancel(remove);
        }
    }

    public void lambda$cancelLoadFile$8() {
        getNotificationCenter().lambda$postNotificationNameOnUIThread$1(NotificationCenter.onDownloadingFilesChanged, new Object[0]);
    }

    public void lambda$changePriority$11(String str, int i) {
        int priorityValue;
        FileLoadOperation fileLoadOperation = this.loadOperationPaths.get(str);
        if (fileLoadOperation == null || fileLoadOperation.getPriority() == (priorityValue = getPriorityValue(i))) {
            return;
        }
        fileLoadOperation.setPriority(priorityValue);
        FileLoaderPriorityQueue queue = fileLoadOperation.getQueue();
        queue.remove(fileLoadOperation);
        queue.add(fileLoadOperation);
        queue.checkLoadingOperations();
        FileLog.d("update priority " + str + " position in queue " + fileLoadOperation.getPositionInQueue() + " account=" + this.currentAccount);
    }

    public void lambda$checkCurrentDownloadsFiles$17(ArrayList arrayList) {
        getDownloadController().recentDownloadingFiles.removeAll(arrayList);
        getNotificationCenter().lambda$postNotificationNameOnUIThread$1(NotificationCenter.onDownloadingFilesChanged, new Object[0]);
    }

    public void lambda$checkDownloadQueue$15(FileLoaderPriorityQueue fileLoaderPriorityQueue, FileLoadOperation fileLoadOperation) {
        if (fileLoaderPriorityQueue.remove(fileLoadOperation)) {
            this.loadOperationPaths.remove(fileLoadOperation.getFileName());
            fileLoaderPriorityQueue.checkLoadingOperations(fileLoadOperation.isStory);
        }
    }

    public void lambda$checkUploadNewDataAvailable$3(boolean z, String str, long j, long j2, Float f) {
        FileUploadOperation fileUploadOperation = (z ? this.uploadOperationPathsEnc : this.uploadOperationPaths).get(str);
        if (fileUploadOperation != null) {
            fileUploadOperation.checkNewDataAvailable(j, j2, f);
        } else if (j2 != 0) {
            this.uploadSizes.put(str, Long.valueOf(j2));
        }
    }

    public static void lambda$deleteFiles$16(ArrayList arrayList, int i) {
        for (int i2 = 0; i2 < arrayList.size(); i2++) {
            File file = (File) arrayList.get(i2);
            File file2 = new File(file.getAbsolutePath() + ".enc");
            if (file2.exists()) {
                try {
                    if (!file2.delete()) {
                        file2.deleteOnExit();
                    }
                } catch (Exception e) {
                    FileLog.e(e);
                }
                try {
                    File file3 = new File(getInternalCacheDir(), file.getName() + ".enc.key");
                    if (!file3.delete()) {
                        file3.deleteOnExit();
                    }
                } catch (Exception e2) {
                    FileLog.e(e2);
                }
            } else if (file.exists() && !file.delete()) {
                file.deleteOnExit();
            }
            try {
                File file4 = new File(file.getParentFile(), "q_" + file.getName());
                if (file4.exists() && !file4.delete()) {
                    file4.deleteOnExit();
                }
            } catch (Exception e3) {
                FileLog.e(e3);
            }
        }
        if (i == 2) {
            ImageLoader.getInstance().clearMemory();
        }
    }

    public void lambda$loadFile$13(TLRPC.Document document, SecureDocument secureDocument, WebFile webFile, TLRPC.TL_fileLocationToBeDeprecated tL_fileLocationToBeDeprecated, ImageLocation imageLocation, Object obj, String str, long j, int i, int i2) {
        loadFileInternal(document, secureDocument, webFile, tL_fileLocationToBeDeprecated, imageLocation, obj, str, j, i, null, 0L, false, i2);
    }

    public void lambda$loadStreamFile$14(FileLoadOperation[] fileLoadOperationArr, TLRPC.Document document, ImageLocation imageLocation, Object obj, int i, FileLoadOperationStream fileLoadOperationStream, long j, boolean z, int i2, CountDownLatch countDownLatch) {
        String str = null;
        TLRPC.TL_fileLocationToBeDeprecated tL_fileLocationToBeDeprecated = (document != null || imageLocation == null) ? null : imageLocation.location;
        if (document == null && imageLocation != null) {
            str = "mp4";
        }
        fileLoadOperationArr[0] = loadFileInternal(document, null, null, tL_fileLocationToBeDeprecated, imageLocation, obj, str, (document != null || imageLocation == null) ? 0L : imageLocation.currentSize, i, fileLoadOperationStream, j, z, i2);
        countDownLatch.countDown();
    }

    public void lambda$new$18() {
        int i = 0;
        while (true) {
            FileLoaderPriorityQueue[] fileLoaderPriorityQueueArr = this.smallFilesQueue;
            if (i >= fileLoaderPriorityQueueArr.length) {
                dumpFilesQueue();
                return;
            }
            if (fileLoaderPriorityQueueArr[i].getCount() > 0 || this.largeFilesQueue[i].getCount() > 0) {
                FileLog.d("download queue: dc" + (i + 1) + " account=" + this.currentAccount + " small_operations=" + this.smallFilesQueue[i].getCount() + " large_operations=" + this.largeFilesQueue[i].getCount());
            }
            i++;
        }
    }

    public void lambda$onNetworkChanged$4(boolean z) {
        Iterator<Map.Entry<String, FileUploadOperation>> it = this.uploadOperationPaths.entrySet().iterator();
        while (it.hasNext()) {
            it.next().getValue().onNetworkChanged(z);
        }
        Iterator<Map.Entry<String, FileUploadOperation>> it2 = this.uploadOperationPathsEnc.entrySet().iterator();
        while (it2.hasNext()) {
            it2.next().getValue().onNetworkChanged(z);
        }
    }

    public void lambda$setForceStreamLoadingFile$6(TLRPC.FileLocation fileLocation, String str) {
        String attachFileName = getAttachFileName(fileLocation, str);
        this.forceLoadingFile = attachFileName;
        FileLoadOperation fileLoadOperation = this.loadOperationPaths.get(attachFileName);
        if (fileLoadOperation != null) {
            if (fileLoadOperation.isPreloadVideoOperation()) {
                fileLoadOperation.setIsPreloadVideoOperation(false);
            }
            fileLoadOperation.setForceRequest(true);
            fileLoadOperation.setPriority(getPriorityValue(4));
            fileLoadOperation.getQueue().remove(fileLoadOperation);
            fileLoadOperation.getQueue().add(fileLoadOperation);
            fileLoadOperation.getQueue().checkLoadingOperations();
        }
    }

    public void lambda$uploadFile$5(boolean z, String str, long j, int i, boolean z2, boolean z3) {
        long j2;
        LinkedList<FileUploadOperation> linkedList;
        if (z) {
            if (this.uploadOperationPathsEnc.containsKey(str)) {
                return;
            }
        } else if (this.uploadOperationPaths.containsKey(str)) {
            return;
        }
        if (j == 0 || this.uploadSizes.get(str) == null) {
            j2 = j;
        } else {
            this.uploadSizes.remove(str);
            j2 = 0;
        }
        FileUploadOperation fileUploadOperation = new FileUploadOperation(this.currentAccount, str, z, j2, i);
        FileLoaderDelegate fileLoaderDelegate = this.delegate;
        if (fileLoaderDelegate != null && j != 0) {
            fileLoaderDelegate.fileUploadProgressChanged(fileUploadOperation, str, 0L, j, z);
        }
        (z ? this.uploadOperationPathsEnc : this.uploadOperationPaths).put(str, fileUploadOperation);
        if (z2) {
            fileUploadOperation.setForceSmallFile();
        }
        fileUploadOperation.setDelegate(new AnonymousClass1(z, str, z3));
        if (z3) {
            int i2 = this.currentUploadSmallOperationsCount;
            if (i2 < 1) {
                this.currentUploadSmallOperationsCount = i2 + 1;
                fileUploadOperation.start();
            } else {
                linkedList = this.uploadSmallOperationQueue;
                linkedList.add(fileUploadOperation);
            }
        }
        int i3 = this.currentUploadOperationsCount;
        if (i3 < 1) {
            this.currentUploadOperationsCount = i3 + 1;
            fileUploadOperation.start();
        } else {
            linkedList = this.uploadOperationQueue;
            linkedList.add(fileUploadOperation);
        }
    }

    private void loadFile(final TLRPC.Document document, final SecureDocument secureDocument, final WebFile webFile, final TLRPC.TL_fileLocationToBeDeprecated tL_fileLocationToBeDeprecated, final ImageLocation imageLocation, final Object obj, final String str, final long j, final int i, final int i2) {
        String str2;
        String attachFileName;
        AnonymousClass1 anonymousClass1 = null;
        if (tL_fileLocationToBeDeprecated != null) {
            attachFileName = getAttachFileName(tL_fileLocationToBeDeprecated, str);
        } else if (document != null) {
            attachFileName = getAttachFileName(document);
        } else {
            if (webFile == null) {
                str2 = null;
                Runnable runnable = new Runnable() {
                    @Override
                    public final void run() {
                        FileLoader.this.lambda$loadFile$13(document, secureDocument, webFile, tL_fileLocationToBeDeprecated, imageLocation, obj, str, j, i, i2);
                    }
                };
                if (i2 == 10 && !TextUtils.isEmpty(str2) && !str2.contains("-2147483648")) {
                    LoadOperationUIObject loadOperationUIObject = new LoadOperationUIObject(anonymousClass1);
                    loadOperationUIObject.loadInternalRunnable = runnable;
                    this.loadOperationPathsUI.put(str2, loadOperationUIObject);
                }
                fileLoaderQueue.postRunnable(runnable);
            }
            attachFileName = getAttachFileName(webFile);
        }
        str2 = attachFileName;
        Runnable runnable2 = new Runnable() {
            @Override
            public final void run() {
                FileLoader.this.lambda$loadFile$13(document, secureDocument, webFile, tL_fileLocationToBeDeprecated, imageLocation, obj, str, j, i, i2);
            }
        };
        if (i2 == 10) {
        }
        fileLoaderQueue.postRunnable(runnable2);
    }

    private org.telegram.messenger.FileLoadOperation loadFileInternal(org.telegram.tgnet.TLRPC.Document r33, org.telegram.messenger.SecureDocument r34, org.telegram.messenger.WebFile r35, org.telegram.tgnet.TLRPC.TL_fileLocationToBeDeprecated r36, org.telegram.messenger.ImageLocation r37, java.lang.Object r38, java.lang.String r39, long r40, int r42, org.telegram.messenger.FileLoadOperationStream r43, long r44, boolean r46, int r47) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.FileLoader.loadFileInternal(org.telegram.tgnet.TLRPC$Document, org.telegram.messenger.SecureDocument, org.telegram.messenger.WebFile, org.telegram.tgnet.TLRPC$TL_fileLocationToBeDeprecated, org.telegram.messenger.ImageLocation, java.lang.Object, java.lang.String, long, int, org.telegram.messenger.FileLoadOperationStream, long, boolean, int):org.telegram.messenger.FileLoadOperation");
    }

    public static byte[] longToBytes(long j) {
        ByteBuffer allocate = ByteBuffer.allocate(8);
        allocate.putLong(j);
        return allocate.array();
    }

    public void lambda$removeLoadingVideo$1(TLRPC.Document document, boolean z) {
        String attachFileName = getAttachFileName(document);
        StringBuilder sb = new StringBuilder();
        sb.append(attachFileName);
        sb.append(z ? "p" : "");
        if (this.loadingVideos.remove(sb.toString()) != null) {
            getNotificationCenter().lambda$postNotificationNameOnUIThread$1(NotificationCenter.videoLoadingStateChanged, attachFileName);
        }
    }

    public static void setMediaDirs(SparseArray<File> sparseArray) {
        mediaDirs = sparseArray;
    }

    public void cancel(FileLoadOperation fileLoadOperation) {
        if (fileLoadOperation == null) {
            return;
        }
        final String fileName = fileLoadOperation.getFileName();
        LoadOperationUIObject remove = this.loadOperationPathsUI.remove(fileName);
        Runnable runnable = remove != null ? remove.loadInternalRunnable : null;
        boolean z = remove != null;
        if (runnable != null) {
            fileLoaderQueue.cancelRunnable(runnable);
        }
        fileLoaderQueue.postRunnable(new Runnable() {
            @Override
            public final void run() {
                FileLoader.this.lambda$cancel$9(fileName);
            }
        });
        if (z) {
            AndroidUtilities.runOnUIThread(new Runnable() {
                @Override
                public final void run() {
                    FileLoader.this.lambda$cancel$10();
                }
            });
        }
    }

    public void cancelFileUpload(final String str, final boolean z) {
        if (str == null) {
            return;
        }
        fileLoaderQueue.postRunnable(new Runnable() {
            @Override
            public final void run() {
                FileLoader.this.lambda$cancelFileUpload$2(z, str);
            }
        });
    }

    public void cancelLoadAllFiles() {
        for (final String str : this.loadOperationPathsUI.keySet()) {
            LoadOperationUIObject loadOperationUIObject = this.loadOperationPathsUI.get(str);
            Runnable runnable = loadOperationUIObject != null ? loadOperationUIObject.loadInternalRunnable : null;
            if (runnable != null) {
                fileLoaderQueue.cancelRunnable(runnable);
            }
            fileLoaderQueue.postRunnable(new Runnable() {
                @Override
                public final void run() {
                    FileLoader.this.lambda$cancelLoadAllFiles$12(str);
                }
            });
        }
    }

    public void cancelLoadFile(String str) {
        cancelLoadFile(null, null, null, null, null, str, true);
    }

    public void cancelLoadFile(SecureDocument secureDocument) {
        cancelLoadFile(null, secureDocument, null, null, null, null, false);
    }

    public void cancelLoadFile(WebFile webFile) {
        cancelLoadFile(null, null, webFile, null, null, null, false);
    }

    public void cancelLoadFile(TLRPC.Document document) {
        cancelLoadFile(document, false);
    }

    public void cancelLoadFile(TLRPC.Document document, boolean z) {
        cancelLoadFile(document, null, null, null, null, null, z);
    }

    public void cancelLoadFile(TLRPC.FileLocation fileLocation, String str) {
        cancelLoadFile(fileLocation, str, false);
    }

    public void cancelLoadFile(TLRPC.FileLocation fileLocation, String str, boolean z) {
        cancelLoadFile(null, null, null, fileLocation, str, null, z);
    }

    public void cancelLoadFile(TLRPC.PhotoSize photoSize) {
        cancelLoadFile(photoSize, false);
    }

    public void cancelLoadFile(TLRPC.PhotoSize photoSize, boolean z) {
        cancelLoadFile(null, null, null, photoSize.location, null, null, z);
    }

    public void cancelLoadFiles(ArrayList<String> arrayList) {
        int size = arrayList.size();
        for (int i = 0; i < size; i++) {
            cancelLoadFile(null, null, null, null, null, arrayList.get(i), true);
        }
    }

    public void changePriority(final int i, TLRPC.Document document, SecureDocument secureDocument, WebFile webFile, TLRPC.FileLocation fileLocation, String str, final String str2) {
        if (fileLocation == null && document == null && webFile == null && secureDocument == null && TextUtils.isEmpty(str2)) {
            return;
        }
        if (fileLocation != null) {
            str2 = getAttachFileName(fileLocation, str);
        } else if (document != null) {
            str2 = getAttachFileName(document);
        } else if (secureDocument != null) {
            str2 = getAttachFileName(secureDocument);
        } else if (webFile != null) {
            str2 = getAttachFileName(webFile);
        }
        fileLoaderQueue.postRunnable(new Runnable() {
            @Override
            public final void run() {
                FileLoader.this.lambda$changePriority$11(str2, i);
            }
        });
    }

    public void checkCurrentDownloadsFiles() {
        final ArrayList arrayList = new ArrayList();
        ArrayList arrayList2 = new ArrayList(getDownloadController().recentDownloadingFiles);
        for (int i = 0; i < arrayList2.size(); i++) {
            ((MessageObject) arrayList2.get(i)).checkMediaExistance();
            if (((MessageObject) arrayList2.get(i)).mediaExists) {
                arrayList.add((MessageObject) arrayList2.get(i));
            }
        }
        if (arrayList.isEmpty()) {
            return;
        }
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                FileLoader.this.lambda$checkCurrentDownloadsFiles$17(arrayList);
            }
        });
    }

    public boolean checkLoadCaughtPremiumFloodWait(String str) {
        FileLoadOperation fileLoadOperation;
        if (str == null || (fileLoadOperation = this.loadOperationPaths.get(str)) == null || !fileLoadOperation.caughtPremiumFloodWait) {
            return false;
        }
        fileLoadOperation.caughtPremiumFloodWait = false;
        return true;
    }

    public void checkMediaExistance(ArrayList<MessageObject> arrayList) {
        getFileDatabase().checkMediaExistance(arrayList);
    }

    public boolean checkUploadCaughtPremiumFloodWait(String str) {
        FileUploadOperation fileUploadOperation;
        if (str == null || (fileUploadOperation = this.uploadOperationPaths.get(str)) == null || !fileUploadOperation.caughtPremiumFloodWait) {
            return false;
        }
        fileUploadOperation.caughtPremiumFloodWait = false;
        return true;
    }

    public void checkUploadNewDataAvailable(String str, boolean z, long j, long j2) {
        checkUploadNewDataAvailable(str, z, j, j2, null);
    }

    public void checkUploadNewDataAvailable(final String str, final boolean z, final long j, final long j2, final Float f) {
        fileLoaderQueue.postRunnable(new Runnable() {
            @Override
            public final void run() {
                FileLoader.this.lambda$checkUploadNewDataAvailable$3(z, str, j, j2, f);
            }
        });
    }

    public void clearFilePaths() {
        this.filePathDatabase.clear();
    }

    public void clearRecentDownloadedFiles() {
        getDownloadController().clearRecentDownloadedFiles();
    }

    public void deleteFiles(final ArrayList<File> arrayList, final int i) {
        if (arrayList == null || arrayList.isEmpty()) {
            return;
        }
        fileLoaderQueue.postRunnable(new Runnable() {
            @Override
            public final void run() {
                FileLoader.lambda$deleteFiles$16(arrayList, i);
            }
        });
    }

    public void dumpFilesQueue() {
        if (BuildVars.LOGS_ENABLED) {
            fileLoaderQueue.cancelRunnable(this.dumpFilesQueueRunnable);
            fileLoaderQueue.postRunnable(this.dumpFilesQueueRunnable, 10000L);
        }
    }

    public FileLoadOperation findLoadOperationByRequestToken(int i) {
        for (FileLoadOperation fileLoadOperation : this.loadOperationPaths.values()) {
            if (fileLoadOperation != null && fileLoadOperation.uiRequestTokens.contains(Integer.valueOf(i))) {
                return fileLoadOperation;
            }
        }
        return null;
    }

    public FileUploadOperation findUploadOperationByRequestToken(int i) {
        for (FileUploadOperation fileUploadOperation : this.uploadOperationPaths.values()) {
            if (fileUploadOperation != null && fileUploadOperation.uiRequestTokens.contains(Integer.valueOf(i))) {
                return fileUploadOperation;
            }
        }
        return null;
    }

    public float getBufferedProgressFromPosition(float f, String str) {
        FileLoadOperation fileLoadOperation;
        if (TextUtils.isEmpty(str) || (fileLoadOperation = this.loadOperationPaths.get(str)) == null) {
            return 0.0f;
        }
        return fileLoadOperation.getDownloadedLengthFromOffset(f);
    }

    public void getCurrentLoadingFiles(ArrayList<MessageObject> arrayList) {
        arrayList.clear();
        arrayList.addAll(getDownloadController().downloadingFiles);
        for (int i = 0; i < arrayList.size(); i++) {
            arrayList.get(i).isDownloadingFile = true;
        }
    }

    public FilePathDatabase getFileDatabase() {
        return this.filePathDatabase;
    }

    public DispatchQueue getFileLoaderQueue() {
        return fileLoaderQueue;
    }

    public int getFileReference(Object obj) {
        int i = this.lastReferenceId;
        this.lastReferenceId = i + 1;
        this.parentObjectReferences.put(Integer.valueOf(i), obj);
        return i;
    }

    public File getLocalFile(ImageLocation imageLocation) {
        String attachFileName;
        if (imageLocation == null) {
            return null;
        }
        TLRPC.TL_fileLocationToBeDeprecated tL_fileLocationToBeDeprecated = imageLocation.location;
        if (tL_fileLocationToBeDeprecated != null) {
            attachFileName = getAttachFileName(tL_fileLocationToBeDeprecated, null);
        } else {
            TLObject tLObject = imageLocation.document;
            attachFileName = (tLObject == null && (tLObject = imageLocation.webFile) == null) ? null : getAttachFileName(tLObject);
        }
        if (attachFileName == null) {
            return null;
        }
        File file = new File(getDirectory(4), attachFileName);
        if (file.exists()) {
            return file;
        }
        File file2 = new File(getDirectory(0), attachFileName);
        if (file2.exists()) {
            return file2;
        }
        File file3 = new File(getDirectory(2), attachFileName);
        if (file3.exists()) {
            return file3;
        }
        File file4 = new File(getDirectory(5), attachFileName);
        if (file4.exists()) {
            return file4;
        }
        return null;
    }

    public Object getParentObject(int i) {
        return this.parentObjectReferences.get(Integer.valueOf(i));
    }

    public File getPathToAttach(TLObject tLObject) {
        return getPathToAttach(tLObject, null, false);
    }

    public java.io.File getPathToAttach(org.telegram.tgnet.TLObject r16, java.lang.String r17, java.lang.String r18, boolean r19, boolean r20) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.FileLoader.getPathToAttach(org.telegram.tgnet.TLObject, java.lang.String, java.lang.String, boolean, boolean):java.io.File");
    }

    public File getPathToAttach(TLObject tLObject, String str, boolean z) {
        return getPathToAttach(tLObject, null, str, z, true);
    }

    public File getPathToAttach(TLObject tLObject, String str, boolean z, boolean z2) {
        return getPathToAttach(tLObject, null, str, z, z2);
    }

    public File getPathToAttach(TLObject tLObject, boolean z) {
        return getPathToAttach(tLObject, null, z);
    }

    public File getPathToMessage(TLRPC.Message message) {
        return getPathToMessage(message, true);
    }

    public File getPathToMessage(TLRPC.Message message, boolean z) {
        return getPathToMessage(message, false, z);
    }

    public File getPathToMessage(TLRPC.Message message, boolean z, boolean z2) {
        TLRPC.PhotoSize closestPhotoSizeWithSize;
        TLRPC.PhotoSize closestPhotoSizeWithSize2;
        TLRPC.PhotoSize closestPhotoSizeWithSize3;
        if (message == null) {
            return new File("");
        }
        if (message instanceof TLRPC.TL_messageService) {
            TLRPC.Photo photo = message.action.photo;
            if (photo != null) {
                ArrayList<TLRPC.PhotoSize> arrayList = photo.sizes;
                if (arrayList.size() > 0 && (closestPhotoSizeWithSize3 = getClosestPhotoSizeWithSize(arrayList, AndroidUtilities.getPhotoSize())) != null) {
                    return getPathToAttach(closestPhotoSizeWithSize3, null, z, z2);
                }
            }
        } else {
            if (MessageObject.getMedia(message) instanceof TLRPC.TL_messageMediaDocument) {
                return getPathToAttach(MessageObject.getMedia(message).document, null, z || MessageObject.getMedia(message).ttl_seconds != 0, z2);
            }
            if (MessageObject.getMedia(message) instanceof TLRPC.TL_messageMediaPhoto) {
                ArrayList<TLRPC.PhotoSize> arrayList2 = MessageObject.getMedia(message).photo.sizes;
                if (arrayList2.size() > 0 && (closestPhotoSizeWithSize2 = getClosestPhotoSizeWithSize(arrayList2, AndroidUtilities.getPhotoSize(), false, null, true)) != null) {
                    return getPathToAttach(closestPhotoSizeWithSize2, null, z || MessageObject.getMedia(message).ttl_seconds != 0, z2);
                }
            } else if (MessageObject.getMedia(message) instanceof TLRPC.TL_messageMediaWebPage) {
                if (MessageObject.getMedia(message).webpage.document != null) {
                    return getPathToAttach(MessageObject.getMedia(message).webpage.document, null, z, z2);
                }
                if (MessageObject.getMedia(message).webpage.photo != null) {
                    ArrayList<TLRPC.PhotoSize> arrayList3 = MessageObject.getMedia(message).webpage.photo.sizes;
                    if (arrayList3.size() > 0 && (closestPhotoSizeWithSize = getClosestPhotoSizeWithSize(arrayList3, AndroidUtilities.getPhotoSize())) != null) {
                        return getPathToAttach(closestPhotoSizeWithSize, null, z, z2);
                    }
                }
            } else if (MessageObject.getMedia(message) instanceof TLRPC.TL_messageMediaInvoice) {
                return getPathToAttach(((TLRPC.TL_messageMediaInvoice) MessageObject.getMedia(message)).photo, null, true, z2);
            }
        }
        return new File("");
    }

    public void getRecentLoadingFiles(ArrayList<MessageObject> arrayList) {
        arrayList.clear();
        arrayList.addAll(getDownloadController().recentDownloadingFiles);
        for (int i = 0; i < arrayList.size(); i++) {
            arrayList.get(i).isDownloadingFile = true;
        }
    }

    public boolean isLoadingFile(String str) {
        return str != null && this.loadOperationPathsUI.containsKey(str);
    }

    public boolean isLoadingVideo(TLRPC.Document document, boolean z) {
        if (document != null) {
            HashMap<String, Boolean> hashMap = this.loadingVideos;
            StringBuilder sb = new StringBuilder();
            sb.append(getAttachFileName(document));
            sb.append(z ? "p" : "");
            if (hashMap.containsKey(sb.toString())) {
                return true;
            }
        }
        return false;
    }

    public boolean isLoadingVideoAny(TLRPC.Document document) {
        return isLoadingVideo(document, false) || isLoadingVideo(document, true);
    }

    public void loadFile(ImageLocation imageLocation, Object obj, String str, int i, int i2) {
        if (imageLocation == null) {
            return;
        }
        loadFile(imageLocation.document, imageLocation.secureDocument, imageLocation.webFile, imageLocation.location, imageLocation, obj, str, imageLocation.getSize(), i, (i2 != 0 || (!imageLocation.isEncrypted() && (imageLocation.photoSize == null || imageLocation.getSize() != 0))) ? i2 : 1);
    }

    public void loadFile(SecureDocument secureDocument, int i) {
        if (secureDocument == null) {
            return;
        }
        loadFile(null, secureDocument, null, null, null, null, null, 0L, i, 1);
    }

    public void loadFile(WebFile webFile, int i, int i2) {
        loadFile(null, null, webFile, null, null, null, null, 0L, i, i2);
    }

    public void loadFile(TLRPC.Document document, Object obj, int i, int i2) {
        if (document == null) {
            return;
        }
        loadFile(document, null, null, null, null, obj, null, 0L, i, (i2 != 0 || document.key == null) ? i2 : 1);
    }

    public FileLoadOperation loadStreamFile(FileLoadOperationStream fileLoadOperationStream, TLRPC.Document document, ImageLocation imageLocation, Object obj, long j, boolean z, int i) {
        return loadStreamFile(fileLoadOperationStream, document, imageLocation, obj, j, z, i, document == null ? 1 : 0);
    }

    public FileLoadOperation loadStreamFile(final FileLoadOperationStream fileLoadOperationStream, final TLRPC.Document document, final ImageLocation imageLocation, final Object obj, final long j, final boolean z, final int i, final int i2) {
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        final FileLoadOperation[] fileLoadOperationArr = new FileLoadOperation[1];
        fileLoaderQueue.postRunnable(new Runnable() {
            @Override
            public final void run() {
                FileLoader.this.lambda$loadStreamFile$14(fileLoadOperationArr, document, imageLocation, obj, i, fileLoadOperationStream, j, z, i2, countDownLatch);
            }
        });
        awaitFileLoadOperation(countDownLatch, true);
        return fileLoadOperationArr[0];
    }

    public void onNetworkChanged(final boolean z) {
        fileLoaderQueue.postRunnable(new Runnable() {
            @Override
            public final void run() {
                FileLoader.this.lambda$onNetworkChanged$4(z);
            }
        });
    }

    public void removeLoadingVideo(final TLRPC.Document document, final boolean z, boolean z2) {
        if (document == null) {
            return;
        }
        if (z2) {
            AndroidUtilities.runOnUIThread(new Runnable() {
                @Override
                public final void run() {
                    FileLoader.this.lambda$removeLoadingVideo$1(document, z);
                }
            });
        } else {
            lambda$removeLoadingVideo$1(document, z);
        }
    }

    public void setDelegate(FileLoaderDelegate fileLoaderDelegate) {
        this.delegate = fileLoaderDelegate;
    }

    public void setForceStreamLoadingFile(final TLRPC.FileLocation fileLocation, final String str) {
        if (fileLocation == null) {
            return;
        }
        fileLoaderQueue.postRunnable(new Runnable() {
            @Override
            public final void run() {
                FileLoader.this.lambda$setForceStreamLoadingFile$6(fileLocation, str);
            }
        });
    }

    public void setLoadingVideo(final TLRPC.Document document, final boolean z, boolean z2) {
        if (document == null) {
            return;
        }
        if (z2) {
            AndroidUtilities.runOnUIThread(new Runnable() {
                @Override
                public final void run() {
                    FileLoader.this.lambda$setLoadingVideo$0(document, z);
                }
            });
        } else {
            lambda$setLoadingVideo$0(document, z);
        }
    }

    public void setLoadingVideoForPlayer(TLRPC.Document document, boolean z) {
        if (document == null) {
            return;
        }
        String attachFileName = getAttachFileName(document);
        HashMap<String, Boolean> hashMap = this.loadingVideos;
        StringBuilder sb = new StringBuilder();
        sb.append(attachFileName);
        sb.append(z ? "" : "p");
        if (hashMap.containsKey(sb.toString())) {
            HashMap<String, Boolean> hashMap2 = this.loadingVideos;
            StringBuilder sb2 = new StringBuilder();
            sb2.append(attachFileName);
            sb2.append(z ? "p" : "");
            hashMap2.put(sb2.toString(), Boolean.TRUE);
            getNotificationCenter().lambda$postNotificationNameOnUIThread$1(NotificationCenter.videoLoadingStateChanged, attachFileName);
        }
    }

    public void lambda$setLoadingVideo$0(TLRPC.Document document, boolean z) {
        String attachFileName = getAttachFileName(document);
        StringBuilder sb = new StringBuilder();
        sb.append(attachFileName);
        sb.append(z ? "p" : "");
        this.loadingVideos.put(sb.toString(), Boolean.TRUE);
        getNotificationCenter().lambda$postNotificationNameOnUIThread$1(NotificationCenter.videoLoadingStateChanged, attachFileName);
    }

    public void setLocalPathTo(TLObject tLObject, String str) {
        int i;
        long j;
        int i2;
        if (tLObject instanceof TLRPC.Document) {
            TLRPC.Document document = (TLRPC.Document) tLObject;
            i = document.key != null ? 4 : MessageObject.isVoiceDocument(document) ? 1 : MessageObject.isVideoDocument(document) ? 2 : 3;
            j = document.id;
            i2 = document.dc_id;
        } else {
            if (!(tLObject instanceof TLRPC.PhotoSize)) {
                return;
            }
            TLRPC.PhotoSize photoSize = (TLRPC.PhotoSize) tLObject;
            if ((photoSize instanceof TLRPC.TL_photoStrippedSize) || (photoSize instanceof TLRPC.TL_photoPathSize)) {
                return;
            }
            TLRPC.FileLocation fileLocation = photoSize.location;
            i = (fileLocation == null || fileLocation.key != null || (fileLocation.volume_id == -2147483648L && fileLocation.local_id < 0) || photoSize.size < 0) ? 4 : 0;
            j = fileLocation.volume_id;
            i2 = fileLocation.dc_id + (fileLocation.local_id << 16);
        }
        this.filePathDatabase.putPath(j, i2, i, 1, str);
    }

    public void uploadFile(String str, boolean z, boolean z2, int i) {
        uploadFile(str, z, z2, 0L, i, false);
    }

    public void uploadFile(final String str, final boolean z, final boolean z2, final long j, final int i, final boolean z3) {
        if (str == null) {
            return;
        }
        fileLoaderQueue.postRunnable(new Runnable() {
            @Override
            public final void run() {
                FileLoader.this.lambda$uploadFile$5(z, str, j, i, z3, z2);
            }
        });
    }
}
