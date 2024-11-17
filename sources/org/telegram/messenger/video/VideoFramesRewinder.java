package org.telegram.messenger.video;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.view.View;
import java.io.File;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.TreeSet;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.SharedConfig;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.Utilities;
import org.telegram.messenger.video.VideoFramesRewinder;
import org.telegram.ui.Components.AnimatedFileDrawable;

public class VideoFramesRewinder {
    private Frame currentFrame;
    private boolean destroyAfterPrepare;
    int h;
    private boolean isPreparing;
    private long lastSeek;
    private int maxFrameSide;
    private int maxFramesCount;
    private View parentView;
    private long prepareToMs;
    private float prepareWithSpeed;
    private long ptr;
    int w;
    private final Paint paint = new Paint(2);
    private final int[] meta = new int[6];
    private final ArrayList<Frame> freeFrames = new ArrayList<>();
    private final TreeSet<Frame> frames = new TreeSet<>(new Comparator() {
        @Override
        public final int compare(Object obj, Object obj2) {
            int lambda$new$0;
            lambda$new$0 = VideoFramesRewinder.lambda$new$0((VideoFramesRewinder.Frame) obj, (VideoFramesRewinder.Frame) obj2);
            return lambda$new$0;
        }
    });
    private AtomicBoolean stop = new AtomicBoolean(false);
    private AtomicLong until = new AtomicLong(0);
    private float lastSpeed = 1.0f;
    private Runnable prepareRunnable = new Runnable() {
        @Override
        public final void run() {
            VideoFramesRewinder.this.lambda$new$2();
        }
    };

    public class Frame {
        Bitmap bitmap;
        long position;

        private Frame() {
        }
    }

    public VideoFramesRewinder() {
        int i;
        int devicePerformanceClass = SharedConfig.getDevicePerformanceClass();
        if (devicePerformanceClass == 1) {
            this.maxFramesCount = 200;
            i = 580;
        } else if (devicePerformanceClass != 2) {
            this.maxFramesCount = 100;
            i = 480;
        } else {
            this.maxFramesCount = 400;
            i = 720;
        }
        this.maxFrameSide = i;
    }

    private void invalidate() {
        View view = this.parentView;
        if (view != null) {
            view.invalidate();
        }
    }

    public static int lambda$new$0(Frame frame, Frame frame2) {
        return (int) (frame.position - frame2.position);
    }

    public void lambda$new$1(ArrayList arrayList, long j) {
        FileLog.d("[VideoFramesRewinder] total prepare of " + arrayList.size() + " took " + (System.currentTimeMillis() - j) + "ms");
        if (!arrayList.isEmpty()) {
            FileLog.d("[VideoFramesRewinder] prepared from " + ((Frame) arrayList.get(0)).position + "ms to " + ((Frame) arrayList.get(arrayList.size() - 1)).position + "ms (requested up to " + this.prepareToMs + "ms)");
        }
        this.isPreparing = false;
        Iterator<Frame> it = this.frames.iterator();
        while (it.hasNext()) {
            Frame next = it.next();
            if (this.currentFrame != next && next.position > this.lastSeek) {
                if (this.freeFrames.size() > 20) {
                    AndroidUtilities.recycleBitmap(next.bitmap);
                } else {
                    this.freeFrames.add(next);
                }
                it.remove();
            }
        }
        while (!arrayList.isEmpty() && this.frames.size() < this.maxFramesCount) {
            this.frames.add((Frame) arrayList.remove(arrayList.size() - 1));
        }
        if (arrayList.size() > 0) {
            FileLog.d("[VideoFramesRewinder] prepared " + arrayList.size() + " more frames than I could fit :(");
        }
        if (this.destroyAfterPrepare) {
            release();
            this.stop.set(false);
        }
    }

    public void lambda$new$2() {
        int i;
        int i2;
        int i3;
        final ArrayList arrayList = new ArrayList();
        final long currentTimeMillis = System.currentTimeMillis();
        int[] iArr = this.meta;
        int i4 = iArr[4];
        int i5 = 0;
        int min = Math.min(this.w / 4, iArr[0]);
        int min2 = Math.min(this.h / 4, this.meta[1]);
        int i6 = this.maxFrameSide;
        if (min > i6 || min2 > i6) {
            float max = i6 / Math.max(min, min2);
            min = (int) (min * max);
            min2 = (int) (min2 * max);
        }
        AnimatedFileDrawable.seekToMs(this.ptr, this.prepareToMs - (this.prepareWithSpeed * 350.0f), this.meta, false);
        long j = this.meta[3];
        int i7 = 0;
        int i8 = 0;
        for (char c = 3; this.meta[c] <= this.until.get() && i7 < this.maxFramesCount && !this.stop.get(); c = 3) {
            float f = 1000.0f / i4;
            long j2 = j;
            long j3 = ((float) j) + (this.prepareWithSpeed * f);
            Frame remove = !this.freeFrames.isEmpty() ? this.freeFrames.remove(i5) : new Frame();
            Bitmap bitmap = remove.bitmap;
            if (bitmap == null || bitmap.getWidth() != min || remove.bitmap.getHeight() != min2) {
                AndroidUtilities.recycleBitmap(remove.bitmap);
                try {
                    remove.bitmap = Bitmap.createBitmap(min, min2, Bitmap.Config.ARGB_8888);
                } catch (OutOfMemoryError unused) {
                    FileLog.d("[VideoFramesRewinder] failed to create bitmap: out of memory");
                }
            }
            while (true) {
                i = i7;
                i2 = i4;
                i3 = min2;
                if (this.meta[3] + ((long) Math.ceil(f)) >= j3) {
                    break;
                }
                AnimatedFileDrawable.getVideoFrame(this.ptr, null, this.meta, 0, true, 0.0f, r8[4], false);
                i4 = i2;
                i7 = i;
                min2 = i3;
            }
            long j4 = this.ptr;
            Bitmap bitmap2 = remove.bitmap;
            if (AnimatedFileDrawable.getVideoFrame(j4, bitmap2, this.meta, bitmap2.getRowBytes(), true, 0.0f, this.meta[4], false) == 0) {
                i8++;
                if (i8 > 6) {
                    break;
                }
            } else {
                long j5 = this.meta[3];
                remove.position = j5;
                arrayList.add(remove);
                j2 = j5;
            }
            i7 = i + 1;
            i4 = i2;
            j = j2;
            min2 = i3;
            i5 = 0;
        }
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                VideoFramesRewinder.this.lambda$new$1(arrayList, currentTimeMillis);
            }
        });
    }

    private void prepare(long j) {
        if (this.isPreparing) {
            return;
        }
        FileLog.d("[VideoFramesRewinder] starting preparing " + j + "ms");
        this.isPreparing = true;
        this.prepareToMs = j;
        this.prepareWithSpeed = this.lastSpeed;
        Utilities.themeQueue.postRunnable(this.prepareRunnable);
    }

    public void clearCurrent() {
        if (this.currentFrame != null) {
            this.currentFrame = null;
            invalidate();
        }
    }

    public void draw(Canvas canvas, int i, int i2) {
        this.w = i;
        this.h = i2;
        if (this.ptr == 0 || this.currentFrame == null) {
            return;
        }
        canvas.save();
        canvas.scale(i / this.currentFrame.bitmap.getWidth(), i2 / this.currentFrame.bitmap.getHeight());
        canvas.drawBitmap(this.currentFrame.bitmap, 0.0f, 0.0f, this.paint);
        canvas.restore();
    }

    public boolean isReady() {
        return this.ptr != 0;
    }

    public void release() {
        if (this.isPreparing) {
            this.stop.set(true);
            this.destroyAfterPrepare = true;
            return;
        }
        AnimatedFileDrawable.destroyDecoder(this.ptr);
        this.ptr = 0L;
        this.destroyAfterPrepare = false;
        clearCurrent();
        this.until.set(0L);
        Iterator<Frame> it = this.frames.iterator();
        while (it.hasNext()) {
            AndroidUtilities.recycleBitmap(it.next().bitmap);
        }
        this.frames.clear();
        Iterator<Frame> it2 = this.freeFrames.iterator();
        while (it2.hasNext()) {
            AndroidUtilities.recycleBitmap(it2.next().bitmap);
        }
        this.freeFrames.clear();
    }

    public void seek(long j, float f) {
        if (this.ptr == 0) {
            return;
        }
        this.lastSeek = j;
        this.lastSpeed = f;
        this.until.set(j);
        Iterator<Frame> it = this.frames.iterator();
        ArrayList arrayList = new ArrayList();
        while (true) {
            if (!it.hasNext()) {
                FileLog.d("[VideoFramesRewinder] didn't find a frame, wanting to prepare " + j + "ms");
                break;
            }
            Frame next = it.next();
            arrayList.add(Long.valueOf(next.position));
            float f2 = 25.0f * f;
            if (((float) Math.abs(next.position - j)) < f2) {
                if (this.currentFrame != next) {
                    FileLog.d("[VideoFramesRewinder] found a frame " + next.position + "ms to fit to " + j + "ms from " + this.frames.size() + " frames");
                    this.currentFrame = next;
                    invalidate();
                    int i = 0;
                    while (it.hasNext()) {
                        it.next();
                        it.remove();
                        i++;
                    }
                    if (i > 0) {
                        FileLog.d("[VideoFramesRewinder] also deleted " + i + " frames after this frame");
                    }
                }
                for (int size = arrayList.size() - 2; size >= 0; size--) {
                    long longValue = ((Long) arrayList.get(size + 1)).longValue();
                    long longValue2 = ((Long) arrayList.get(size)).longValue();
                    if (((float) Math.abs(longValue - longValue2)) > f2) {
                        prepare(longValue2);
                        return;
                    }
                }
                j = this.frames.first().position - 20;
            }
        }
        prepare(Math.max(0L, j));
    }

    public void setParentView(View view) {
        this.parentView = view;
    }

    public void setup(File file) {
        if (file == null) {
            release();
        } else {
            this.stop.set(false);
            this.ptr = AnimatedFileDrawable.createDecoder(file.getAbsolutePath(), this.meta, UserConfig.selectedAccount, 0L, null, true);
        }
    }
}
