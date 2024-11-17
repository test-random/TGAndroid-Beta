package org.telegram.messenger.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import j$.util.concurrent.ConcurrentHashMap;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.FileLoader;
import org.telegram.messenger.SharedConfig;
import org.telegram.messenger.Utilities;
import org.telegram.messenger.utils.BitmapsCache;
import org.telegram.ui.Components.RLottieDrawable;

public class BitmapsCache {
    private static ThreadPoolExecutor bitmapCompressExecutor;
    static volatile boolean cleanupScheduled;
    private static boolean mkdir;
    private static CacheGeneratorSharedTools sharedTools;
    private static int taskCounter;
    byte[] bufferTmp;
    volatile boolean cacheCreated;
    RandomAccessFile cachedFile;
    public volatile boolean checked;
    int compressQuality;
    boolean error;
    final File file;
    volatile boolean fileExist;
    String fileName;
    private int frameIndex;
    int h;
    BitmapFactory.Options options;
    volatile boolean recycled;
    private final Cacheable source;
    private int tryCount;
    final boolean useSharedBuffers;
    int w;
    static ConcurrentHashMap sharedBuffers = new ConcurrentHashMap();
    private static final int N = Utilities.clamp(Runtime.getRuntime().availableProcessors() - 2, 6, 1);
    public final AtomicInteger framesProcessed = new AtomicInteger(0);
    ArrayList frameOffsets = new ArrayList();
    private final Object mutex = new Object();
    public AtomicBoolean cancelled = new AtomicBoolean(false);
    private Runnable cleanupSharedBuffers = new Runnable() {
        @Override
        public void run() {
            for (Thread thread : BitmapsCache.sharedBuffers.keySet()) {
                if (!thread.isAlive()) {
                    BitmapsCache.sharedBuffers.remove(thread);
                }
            }
            if (BitmapsCache.sharedBuffers.isEmpty()) {
                BitmapsCache.cleanupScheduled = false;
            } else {
                AndroidUtilities.runOnUIThread(BitmapsCache.this.cleanupSharedBuffers, 5000L);
            }
        }
    };

    public static class CacheGeneratorSharedTools {
        private Bitmap[] bitmap;
        ImmutableByteArrayOutputStream[] byteArrayOutputStream;
        private int lastSize;

        private CacheGeneratorSharedTools() {
            this.byteArrayOutputStream = new ImmutableByteArrayOutputStream[BitmapsCache.N];
            this.bitmap = new Bitmap[BitmapsCache.N];
        }

        public static void lambda$allocate$0(Bitmap bitmap) {
            try {
                bitmap.recycle();
            } catch (Exception unused) {
            }
        }

        public static void lambda$release$1(ArrayList arrayList) {
            Iterator it = arrayList.iterator();
            while (it.hasNext()) {
                ((Bitmap) it.next()).recycle();
            }
        }

        void allocate(int i, int i2) {
            int i3 = (i2 << 16) + i;
            boolean z = this.lastSize != i3;
            this.lastSize = i3;
            for (int i4 = 0; i4 < BitmapsCache.N; i4++) {
                if (z || this.bitmap[i4] == null) {
                    final Bitmap bitmap = this.bitmap[i4];
                    if (bitmap != null) {
                        Utilities.globalQueue.postRunnable(new Runnable() {
                            @Override
                            public final void run() {
                                BitmapsCache.CacheGeneratorSharedTools.lambda$allocate$0(bitmap);
                            }
                        });
                    }
                    this.bitmap[i4] = Bitmap.createBitmap(i2, i, Bitmap.Config.ARGB_8888);
                }
                ImmutableByteArrayOutputStream[] immutableByteArrayOutputStreamArr = this.byteArrayOutputStream;
                if (immutableByteArrayOutputStreamArr[i4] == null) {
                    immutableByteArrayOutputStreamArr[i4] = new ImmutableByteArrayOutputStream(i2 * i * 2);
                }
            }
        }

        void release() {
            final ArrayList arrayList = null;
            for (int i = 0; i < BitmapsCache.N; i++) {
                if (this.bitmap[i] != null) {
                    if (arrayList == null) {
                        arrayList = new ArrayList();
                    }
                    arrayList.add(this.bitmap[i]);
                }
                this.bitmap[i] = null;
                this.byteArrayOutputStream[i] = null;
            }
            if (arrayList.isEmpty()) {
                return;
            }
            Utilities.globalQueue.postRunnable(new Runnable() {
                @Override
                public final void run() {
                    BitmapsCache.CacheGeneratorSharedTools.lambda$release$1(arrayList);
                }
            });
        }
    }

    public static class CacheOptions {
        public int compressQuality = 100;
        public boolean fallback = false;
        public boolean firstFrame;
    }

    public interface Cacheable {
        int getNextFrame(Bitmap bitmap);

        void prepareForGenerateCache();

        void releaseForGenerateCache();
    }

    public class FrameOffset {
        int frameOffset;
        int frameSize;
        final int index;

        private FrameOffset(int i) {
            this.index = i;
        }
    }

    public static class Metadata {
        public int frame;
    }

    public BitmapsCache(File file, Cacheable cacheable, CacheOptions cacheOptions, int i, int i2, boolean z) {
        RandomAccessFile randomAccessFile;
        Throwable th;
        this.source = cacheable;
        this.w = i;
        this.h = i2;
        this.compressQuality = cacheOptions.compressQuality;
        this.fileName = file.getName();
        if (bitmapCompressExecutor == null) {
            int i3 = N;
            bitmapCompressExecutor = new ThreadPoolExecutor(i3, i3, 60L, TimeUnit.SECONDS, new LinkedBlockingQueue());
        }
        File file2 = new File(FileLoader.checkDirectory(4), "acache");
        if (!mkdir) {
            file2.mkdir();
            mkdir = true;
        }
        StringBuilder sb = new StringBuilder();
        sb.append(this.fileName);
        sb.append("_");
        sb.append(i);
        sb.append("_");
        sb.append(i2);
        sb.append(z ? "_nolimit" : " ");
        sb.append(".pcache2");
        File file3 = new File(file2, sb.toString());
        this.file = file3;
        this.useSharedBuffers = i < AndroidUtilities.dp(60.0f) && i2 < AndroidUtilities.dp(60.0f);
        if (SharedConfig.getDevicePerformanceClass() < 2) {
            this.fileExist = false;
            this.cacheCreated = false;
            return;
        }
        this.fileExist = file3.exists();
        if (this.fileExist) {
            try {
                try {
                    randomAccessFile = new RandomAccessFile(file3, "r");
                } catch (Throwable th2) {
                    randomAccessFile = null;
                    th = th2;
                }
                try {
                    this.cacheCreated = randomAccessFile.readBoolean();
                    if (this.cacheCreated && this.frameOffsets.isEmpty()) {
                        randomAccessFile.seek(randomAccessFile.readInt());
                        int readInt = randomAccessFile.readInt();
                        fillFrames(randomAccessFile, readInt > 10000 ? 0 : readInt);
                        if (this.frameOffsets.size() == 0) {
                            this.cacheCreated = false;
                            this.fileExist = false;
                            this.checked = true;
                            file3.delete();
                        } else {
                            if (this.cachedFile != randomAccessFile) {
                                closeCachedFile();
                            }
                            this.cachedFile = randomAccessFile;
                        }
                    }
                    if (this.cachedFile != randomAccessFile) {
                        randomAccessFile.close();
                    }
                } catch (Throwable th3) {
                    th = th3;
                    try {
                        th.printStackTrace();
                        this.file.delete();
                        this.fileExist = false;
                        this.checked = true;
                        if (this.cachedFile != randomAccessFile && randomAccessFile != null) {
                            randomAccessFile.close();
                        }
                        this.checked = true;
                    } catch (Throwable th4) {
                        try {
                            if (this.cachedFile != randomAccessFile && randomAccessFile != null) {
                                randomAccessFile.close();
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        throw th4;
                    }
                }
            } catch (IOException e2) {
                e2.printStackTrace();
            }
        }
        this.checked = true;
    }

    private void closeCachedFile() {
        RandomAccessFile randomAccessFile = this.cachedFile;
        if (randomAccessFile != null) {
            try {
                randomAccessFile.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void decrementTaskCounter() {
        int i = taskCounter - 1;
        taskCounter = i;
        if (i <= 0) {
            taskCounter = 0;
            RLottieDrawable.lottieCacheGenerateQueue.postRunnable(new Runnable() {
                @Override
                public final void run() {
                    BitmapsCache.lambda$decrementTaskCounter$0();
                }
            });
        }
    }

    private void fillFrames(RandomAccessFile randomAccessFile, int i) {
        if (i == 0) {
            return;
        }
        byte[] bArr = new byte[i * 8];
        randomAccessFile.read(bArr);
        ByteBuffer wrap = ByteBuffer.wrap(bArr);
        for (int i2 = 0; i2 < i; i2++) {
            FrameOffset frameOffset = new FrameOffset(i2);
            frameOffset.frameOffset = wrap.getInt();
            frameOffset.frameSize = wrap.getInt();
            this.frameOffsets.add(frameOffset);
        }
    }

    private byte[] getBuffer(FrameOffset frameOffset) {
        boolean z = this.useSharedBuffers && Thread.currentThread().getName().startsWith("DispatchQueuePoolThreadSafety_");
        byte[] bArr = z ? (byte[]) sharedBuffers.get(Thread.currentThread()) : this.bufferTmp;
        if (bArr == null || bArr.length < frameOffset.frameSize) {
            bArr = new byte[(int) (frameOffset.frameSize * 1.3f)];
            if (z) {
                sharedBuffers.put(Thread.currentThread(), bArr);
                if (!cleanupScheduled) {
                    cleanupScheduled = true;
                    AndroidUtilities.runOnUIThread(this.cleanupSharedBuffers, 5000L);
                }
            } else {
                this.bufferTmp = bArr;
            }
        }
        return bArr;
    }

    public static void incrementTaskCounter() {
        taskCounter++;
    }

    public void lambda$createCache$1(AtomicBoolean atomicBoolean, Bitmap[] bitmapArr, int i, ImmutableByteArrayOutputStream[] immutableByteArrayOutputStreamArr, int i2, RandomAccessFile randomAccessFile, ArrayList arrayList, CountDownLatch[] countDownLatchArr) {
        if (this.cancelled.get() || atomicBoolean.get()) {
            return;
        }
        Bitmap.CompressFormat compressFormat = Bitmap.CompressFormat.WEBP;
        if (Build.VERSION.SDK_INT <= 28) {
            compressFormat = Bitmap.CompressFormat.PNG;
        }
        bitmapArr[i].compress(compressFormat, this.compressQuality, immutableByteArrayOutputStreamArr[i]);
        int i3 = immutableByteArrayOutputStreamArr[i].count;
        try {
            synchronized (this.mutex) {
                FrameOffset frameOffset = new FrameOffset(i2);
                frameOffset.frameOffset = (int) randomAccessFile.length();
                arrayList.add(frameOffset);
                randomAccessFile.write(immutableByteArrayOutputStreamArr[i].buf, 0, i3);
                frameOffset.frameSize = i3;
                immutableByteArrayOutputStreamArr[i].reset();
            }
        } catch (IOException e) {
            e.printStackTrace();
            try {
                randomAccessFile.close();
            } catch (Exception unused) {
            } catch (Throwable th) {
                atomicBoolean.set(true);
                throw th;
            }
            atomicBoolean.set(true);
        }
        countDownLatchArr[i].countDown();
    }

    public static void lambda$decrementTaskCounter$0() {
        CacheGeneratorSharedTools cacheGeneratorSharedTools = sharedTools;
        if (cacheGeneratorSharedTools != null) {
            cacheGeneratorSharedTools.release();
            sharedTools = null;
        }
    }

    public void cancelCreate() {
    }

    public void createCache() {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.utils.BitmapsCache.createCache():void");
    }

    public int getFrame(int r8, android.graphics.Bitmap r9) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.utils.BitmapsCache.getFrame(int, android.graphics.Bitmap):int");
    }

    public int getFrame(Bitmap bitmap, Metadata metadata) {
        int frame = getFrame(this.frameIndex, bitmap);
        metadata.frame = this.frameIndex;
        if (this.cacheCreated && !this.frameOffsets.isEmpty()) {
            int i = this.frameIndex + 1;
            this.frameIndex = i;
            if (i >= this.frameOffsets.size()) {
                this.frameIndex = 0;
            }
        }
        return frame;
    }

    public int getFrameCount() {
        return this.frameOffsets.size();
    }

    public boolean needGenCache() {
        return (this.cacheCreated && this.fileExist) ? false : true;
    }

    public void recycle() {
        RandomAccessFile randomAccessFile = this.cachedFile;
        if (randomAccessFile != null) {
            try {
                randomAccessFile.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            this.cachedFile = null;
        }
        this.recycled = true;
    }
}
