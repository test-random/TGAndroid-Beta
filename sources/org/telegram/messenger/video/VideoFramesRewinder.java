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
            this.maxFramesCount = 120;
            i = 580;
        } else if (devicePerformanceClass != 2) {
            this.maxFramesCount = 80;
            i = 480;
        } else {
            this.maxFramesCount = 300;
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
            FileLog.d("[VideoFramesRewinder] prepared more frames than I could fit :(");
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
        int min = Math.min(this.w / 4, iArr[0]);
        int min2 = Math.min(this.h / 4, this.meta[1]);
        int i5 = this.maxFrameSide;
        if (min > i5 || min2 > i5) {
            float max = i5 / Math.max(min, min2);
            min = (int) (min * max);
            min2 = (int) (min2 * max);
        }
        long j = this.prepareToMs;
        AnimatedFileDrawable.seekToMs(this.ptr, j - (this.prepareWithSpeed * 350.0f), this.meta, false);
        char c = 3;
        long j2 = this.meta[3];
        int i6 = 0;
        while (true) {
            int i7 = i4;
            if (this.meta[c] > j || this.stop.get()) {
                break;
            }
            int i8 = i7;
            float f = 1000.0f / i8;
            long j3 = j;
            long j4 = ((float) j2) + (this.prepareWithSpeed * f);
            Frame remove = !this.freeFrames.isEmpty() ? this.freeFrames.remove(0) : new Frame();
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
                i = i6;
                i2 = i8;
                i3 = min2;
                if (this.meta[3] + ((long) Math.ceil(f)) >= j4) {
                    break;
                }
                AnimatedFileDrawable.getVideoFrame(this.ptr, null, this.meta, 0, true, 0.0f, r10[4], false);
                min2 = i3;
                i6 = i;
                i8 = i2;
                min = min;
            }
            int i9 = min;
            long j5 = this.ptr;
            Bitmap bitmap2 = remove.bitmap;
            if (AnimatedFileDrawable.getVideoFrame(j5, bitmap2, this.meta, bitmap2.getRowBytes(), true, 0.0f, this.meta[4], false) == 0) {
                i6 = i + 1;
                if (i6 > 6) {
                    break;
                }
                min2 = i3;
                j = j3;
            } else {
                j2 = this.meta[3];
                remove.position = j2;
                arrayList.add(remove);
                min2 = i3;
                j = j3;
                i6 = i;
            }
            i4 = i2;
            min = i9;
            c = 3;
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
        Iterator<Frame> it = this.frames.iterator();
        while (it.hasNext()) {
            Frame next = it.next();
            if (this.freeFrames.size() > 20) {
                AndroidUtilities.recycleBitmap(next.bitmap);
            } else {
                this.freeFrames.add(next);
            }
            it.remove();
        }
    }

    public void seek(long j, float f) {
        if (this.ptr == 0) {
            return;
        }
        this.lastSeek = j;
        this.lastSpeed = f;
        Iterator<Frame> it = this.frames.iterator();
        while (true) {
            if (!it.hasNext()) {
                FileLog.d("[VideoFramesRewinder] didn't find a frame, wanting to prepare " + j + "ms");
                break;
            }
            Frame next = it.next();
            if (((float) Math.abs(next.position - j)) < 25.0f * f) {
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
