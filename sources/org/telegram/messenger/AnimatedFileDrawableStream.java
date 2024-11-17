package org.telegram.messenger;

import java.util.concurrent.CountDownLatch;
import org.telegram.tgnet.TLRPC;

public class AnimatedFileDrawableStream implements FileLoadOperationStream {
    private volatile boolean canceled;
    private CountDownLatch countDownLatch;
    private int currentAccount;
    private int debugCanceledCount;
    private boolean debugReportSend;
    private TLRPC.Document document;
    private String finishedFilePath;
    private boolean finishedLoadingFile;
    private long lastOffset;
    private FileLoadOperation loadOperation;
    private int loadingPriority;
    private ImageLocation location;
    private Object parentObject;
    private boolean preview;
    private final Object sync = new Object();
    private boolean waitingForLoad;

    public AnimatedFileDrawableStream(TLRPC.Document document, ImageLocation imageLocation, Object obj, int i, boolean z, int i2, int i3) {
        this.document = document;
        this.location = imageLocation;
        this.parentObject = obj;
        this.currentAccount = i;
        this.preview = z;
        this.loadingPriority = i2;
        this.loadOperation = FileLoader.getInstance(i).loadStreamFile(this, this.document, this.location, this.parentObject, 0L, this.preview, i2, i3);
    }

    private void cancelLoadingInternal() {
        FileLoader.getInstance(this.currentAccount).cancelLoadFile(this.document);
        if (this.location != null) {
            FileLoader.getInstance(this.currentAccount).cancelLoadFile(this.location.location, "mp4");
        }
    }

    public void cancel() {
        cancel(true);
    }

    public void cancel(boolean z) {
        if (this.canceled) {
            return;
        }
        synchronized (this.sync) {
            try {
                CountDownLatch countDownLatch = this.countDownLatch;
                if (countDownLatch != null) {
                    countDownLatch.countDown();
                    this.countDownLatch = null;
                    if (z && !this.canceled && !this.preview) {
                        FileLoader.getInstance(this.currentAccount).removeLoadingVideo(this.document, false, true);
                    }
                }
                Object obj = this.parentObject;
                if (obj instanceof MessageObject) {
                    MessageObject messageObject = (MessageObject) obj;
                    if (DownloadController.getInstance(messageObject.currentAccount).isDownloading(messageObject.getId())) {
                        z = false;
                    }
                }
                if (z) {
                    cancelLoadingInternal();
                }
                this.canceled = true;
            } catch (Throwable th) {
                throw th;
            }
        }
    }

    public int getCurrentAccount() {
        return this.currentAccount;
    }

    public TLRPC.Document getDocument() {
        return this.document;
    }

    public String getFinishedFilePath() {
        return this.finishedFilePath;
    }

    public ImageLocation getLocation() {
        return this.location;
    }

    public Object getParentObject() {
        return this.document;
    }

    public boolean isCanceled() {
        return this.canceled;
    }

    public boolean isFinishedLoadingFile() {
        return this.finishedLoadingFile;
    }

    public boolean isPreview() {
        return this.preview;
    }

    public boolean isWaitingForLoad() {
        return this.waitingForLoad;
    }

    @Override
    public void newDataAvailable() {
        CountDownLatch countDownLatch = this.countDownLatch;
        if (countDownLatch != null) {
            countDownLatch.countDown();
            this.countDownLatch = null;
        }
    }

    public int read(int i, int i2) {
        long j;
        long j2;
        synchronized (this.sync) {
            try {
                if (this.canceled) {
                    int i3 = this.debugCanceledCount + 1;
                    this.debugCanceledCount = i3;
                    if (!this.debugReportSend && i3 > 200) {
                        this.debugReportSend = true;
                        FileLog.e(new RuntimeException("infinity stream reading!!!"));
                    }
                    return 0;
                }
                if (i2 == 0) {
                    return 0;
                }
                long j3 = 0;
                while (j3 == 0) {
                    try {
                        j = i;
                        long[] downloadedLengthFromOffset = this.loadOperation.getDownloadedLengthFromOffset(j, i2);
                        j2 = downloadedLengthFromOffset[0];
                        try {
                            if (!this.finishedLoadingFile && downloadedLengthFromOffset[1] != 0) {
                                this.finishedLoadingFile = true;
                                this.finishedFilePath = this.loadOperation.getCacheFileFinal().getAbsolutePath();
                            }
                        } catch (Exception e) {
                            e = e;
                            j3 = j2;
                        }
                    } catch (Exception e2) {
                        e = e2;
                    }
                    if (j2 == 0) {
                        synchronized (this.sync) {
                            try {
                                if (this.canceled) {
                                    cancelLoadingInternal();
                                    return 0;
                                }
                                this.countDownLatch = new CountDownLatch(1);
                                if (this.loadOperation.isPaused() || this.lastOffset != j || this.preview) {
                                    FileLoadOperation loadStreamFile = FileLoader.getInstance(this.currentAccount).loadStreamFile(this, this.document, this.location, this.parentObject, j, this.preview, this.loadingPriority);
                                    FileLoadOperation fileLoadOperation = this.loadOperation;
                                    if (fileLoadOperation != loadStreamFile) {
                                        fileLoadOperation.removeStreamListener(this);
                                        this.loadOperation = loadStreamFile;
                                    }
                                    this.lastOffset = j + j2;
                                }
                                synchronized (this.sync) {
                                    try {
                                        if (this.canceled) {
                                            this.countDownLatch = null;
                                            cancelLoadingInternal();
                                            return 0;
                                        }
                                    } finally {
                                    }
                                }
                                if (!this.preview) {
                                    FileLoader.getInstance(this.currentAccount).setLoadingVideo(this.document, false, true);
                                }
                                CountDownLatch countDownLatch = this.countDownLatch;
                                if (countDownLatch != null) {
                                    this.waitingForLoad = true;
                                    countDownLatch.await();
                                    this.waitingForLoad = false;
                                }
                            } finally {
                            }
                            e = e;
                            j3 = j2;
                            FileLog.e((Throwable) e, false);
                            return (int) j3;
                        }
                    }
                    j3 = j2;
                }
                this.lastOffset = i + j3;
                return (int) j3;
            } finally {
            }
        }
    }

    public void reset() {
        synchronized (this.sync) {
            this.canceled = false;
        }
    }
}
