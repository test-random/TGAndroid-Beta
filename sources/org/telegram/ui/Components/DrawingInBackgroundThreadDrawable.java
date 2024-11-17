package org.telegram.ui.Components;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import java.util.ArrayList;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.BuildVars;
import org.telegram.messenger.DispatchQueue;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.SharedConfig;
import org.telegram.ui.ActionBar.Theme;

public abstract class DrawingInBackgroundThreadDrawable implements NotificationCenter.NotificationCenterDelegate {
    public static DispatchQueuePool queuePool;
    boolean attachedToWindow;
    Bitmap backgroundBitmap;
    Canvas backgroundCanvas;
    private final DispatchQueue backgroundQueue;
    Bitmap bitmap;
    Canvas bitmapCanvas;
    private boolean bitmapUpdating;
    private int currentOpenedLayerFlags;
    boolean error;
    int frameGuid;
    int height;
    private int lastFrameId;
    boolean needSwapBitmaps;
    int padding;
    protected boolean paused;
    private boolean reset;
    public final int threadIndex;
    int width;
    private int currentLayerNum = 1;
    private Paint paint = new Paint(1);
    private final Runnable bitmapCreateTask = new Runnable() {
        @Override
        public void run() {
            int i;
            Bitmap bitmap;
            try {
                DrawingInBackgroundThreadDrawable drawingInBackgroundThreadDrawable = DrawingInBackgroundThreadDrawable.this;
                i = drawingInBackgroundThreadDrawable.height + drawingInBackgroundThreadDrawable.padding;
                bitmap = drawingInBackgroundThreadDrawable.backgroundBitmap;
            } catch (Exception e) {
                FileLog.e(e);
                DrawingInBackgroundThreadDrawable.this.error = true;
            }
            if (bitmap != null) {
                int width = bitmap.getWidth();
                DrawingInBackgroundThreadDrawable drawingInBackgroundThreadDrawable2 = DrawingInBackgroundThreadDrawable.this;
                if (width == drawingInBackgroundThreadDrawable2.width) {
                    if (drawingInBackgroundThreadDrawable2.backgroundBitmap.getHeight() != i) {
                    }
                    DrawingInBackgroundThreadDrawable.this.backgroundBitmap.eraseColor(0);
                    DrawingInBackgroundThreadDrawable.this.backgroundCanvas.save();
                    DrawingInBackgroundThreadDrawable.this.backgroundCanvas.translate(0.0f, r0.padding);
                    DrawingInBackgroundThreadDrawable drawingInBackgroundThreadDrawable3 = DrawingInBackgroundThreadDrawable.this;
                    drawingInBackgroundThreadDrawable3.drawInBackground(drawingInBackgroundThreadDrawable3.backgroundCanvas);
                    DrawingInBackgroundThreadDrawable.this.backgroundCanvas.restore();
                    DrawingInBackgroundThreadDrawable.this.backgroundBitmap.prepareToDraw();
                    AndroidUtilities.runOnUIThread(DrawingInBackgroundThreadDrawable.this.uiFrameRunnable);
                }
            }
            Bitmap bitmap2 = DrawingInBackgroundThreadDrawable.this.backgroundBitmap;
            if (bitmap2 != null) {
                bitmap2.recycle();
            }
            DrawingInBackgroundThreadDrawable drawingInBackgroundThreadDrawable4 = DrawingInBackgroundThreadDrawable.this;
            drawingInBackgroundThreadDrawable4.backgroundBitmap = Bitmap.createBitmap(drawingInBackgroundThreadDrawable4.width, i, Bitmap.Config.ARGB_8888);
            DrawingInBackgroundThreadDrawable.this.backgroundCanvas = new Canvas(DrawingInBackgroundThreadDrawable.this.backgroundBitmap);
            DrawingInBackgroundThreadDrawable.this.backgroundBitmap.eraseColor(0);
            DrawingInBackgroundThreadDrawable.this.backgroundCanvas.save();
            DrawingInBackgroundThreadDrawable.this.backgroundCanvas.translate(0.0f, r0.padding);
            DrawingInBackgroundThreadDrawable drawingInBackgroundThreadDrawable32 = DrawingInBackgroundThreadDrawable.this;
            drawingInBackgroundThreadDrawable32.drawInBackground(drawingInBackgroundThreadDrawable32.backgroundCanvas);
            DrawingInBackgroundThreadDrawable.this.backgroundCanvas.restore();
            DrawingInBackgroundThreadDrawable.this.backgroundBitmap.prepareToDraw();
            AndroidUtilities.runOnUIThread(DrawingInBackgroundThreadDrawable.this.uiFrameRunnable);
        }
    };
    Runnable uiFrameRunnable = new Runnable() {
        @Override
        public void run() {
            DrawingInBackgroundThreadDrawable.this.bitmapUpdating = false;
            DrawingInBackgroundThreadDrawable.this.onFrameReady();
            DrawingInBackgroundThreadDrawable drawingInBackgroundThreadDrawable = DrawingInBackgroundThreadDrawable.this;
            if (!drawingInBackgroundThreadDrawable.attachedToWindow) {
                drawingInBackgroundThreadDrawable.recycleBitmaps();
            } else {
                if (drawingInBackgroundThreadDrawable.frameGuid != drawingInBackgroundThreadDrawable.lastFrameId) {
                    return;
                }
                DrawingInBackgroundThreadDrawable.this.needSwapBitmaps = true;
            }
        }
    };

    public static class DispatchQueuePool {
        int pointer;
        public final DispatchQueue[] pool;
        final int size;

        private DispatchQueuePool(int i) {
            this.size = i;
            this.pool = new DispatchQueue[i];
        }

        public DispatchQueue getNextQueue() {
            int i = this.pointer + 1;
            this.pointer = i;
            if (i > this.size - 1) {
                this.pointer = 0;
            }
            DispatchQueue[] dispatchQueueArr = this.pool;
            int i2 = this.pointer;
            DispatchQueue dispatchQueue = dispatchQueueArr[i2];
            if (dispatchQueue != null) {
                return dispatchQueue;
            }
            DispatchQueue dispatchQueue2 = new DispatchQueue("draw_background_queue_" + this.pointer);
            dispatchQueueArr[i2] = dispatchQueue2;
            return dispatchQueue2;
        }
    }

    public DrawingInBackgroundThreadDrawable() {
        if (queuePool == null) {
            queuePool = new DispatchQueuePool(2);
        }
        this.backgroundQueue = queuePool.getNextQueue();
        this.threadIndex = queuePool.pointer;
    }

    public void recycleBitmaps() {
        ArrayList arrayList = new ArrayList();
        Bitmap bitmap = this.bitmap;
        if (bitmap != null) {
            arrayList.add(bitmap);
        }
        Bitmap bitmap2 = this.backgroundBitmap;
        if (bitmap2 != null) {
            arrayList.add(bitmap2);
        }
        this.bitmap = null;
        this.backgroundBitmap = null;
        this.backgroundCanvas = null;
        this.bitmapCanvas = null;
        AndroidUtilities.recycleBitmaps(arrayList);
    }

    @Override
    public void didReceivedNotification(int i, int i2, Object... objArr) {
        int i3;
        if (i == NotificationCenter.stopAllHeavyOperations) {
            Integer num = (Integer) objArr[0];
            if (this.currentLayerNum < num.intValue()) {
                if (num.intValue() != 512 || SharedConfig.getDevicePerformanceClass() < 2) {
                    int intValue = num.intValue() | this.currentOpenedLayerFlags;
                    this.currentOpenedLayerFlags = intValue;
                    if (intValue == 0 || this.paused) {
                        return;
                    }
                    this.paused = true;
                    onPaused();
                    return;
                }
                return;
            }
            return;
        }
        if (i == NotificationCenter.startAllHeavyOperations) {
            Integer num2 = (Integer) objArr[0];
            if (this.currentLayerNum >= num2.intValue() || (i3 = this.currentOpenedLayerFlags) == 0) {
                return;
            }
            int intValue2 = (num2.intValue() ^ (-1)) & i3;
            this.currentOpenedLayerFlags = intValue2;
            if (intValue2 == 0 && this.paused) {
                this.paused = false;
                onResume();
            }
        }
    }

    public void draw(Canvas canvas, long j, int i, int i2, float f) {
        if (this.error) {
            if (BuildVars.DEBUG_PRIVATE_VERSION) {
                canvas.drawRect(0.0f, 0.0f, i, i2, Theme.DEBUG_RED);
                return;
            }
            return;
        }
        this.height = i2;
        this.width = i;
        if (this.needSwapBitmaps) {
            this.needSwapBitmaps = false;
            Bitmap bitmap = this.bitmap;
            Canvas canvas2 = this.bitmapCanvas;
            this.bitmap = this.backgroundBitmap;
            this.bitmapCanvas = this.backgroundCanvas;
            this.backgroundBitmap = bitmap;
            this.backgroundCanvas = canvas2;
        }
        Bitmap bitmap2 = this.bitmap;
        if (bitmap2 == null || this.reset) {
            this.reset = false;
            if (bitmap2 != null) {
                ArrayList arrayList = new ArrayList();
                arrayList.add(this.bitmap);
                AndroidUtilities.recycleBitmaps(arrayList);
                this.bitmap = null;
            }
            int i3 = this.height + this.padding;
            Bitmap bitmap3 = this.bitmap;
            if (bitmap3 != null && bitmap3.getHeight() == i3 && this.bitmap.getWidth() == this.width) {
                this.bitmap.eraseColor(0);
            } else {
                this.bitmap = Bitmap.createBitmap(this.width, i3, Bitmap.Config.ARGB_8888);
                this.bitmapCanvas = new Canvas(this.bitmap);
            }
            this.bitmapCanvas.save();
            this.bitmapCanvas.translate(0.0f, this.padding);
            drawInUiThread(this.bitmapCanvas, f);
            this.bitmapCanvas.restore();
        }
        if (!this.bitmapUpdating && !this.paused) {
            this.bitmapUpdating = true;
            prepareDraw(j);
            this.lastFrameId = this.frameGuid;
            this.backgroundQueue.postRunnable(this.bitmapCreateTask);
        }
        Bitmap bitmap4 = this.bitmap;
        if (bitmap4 != null) {
            this.paint.setAlpha((int) (f * 255.0f));
            canvas.save();
            canvas.translate(0.0f, -this.padding);
            drawBitmap(canvas, bitmap4, this.paint);
            canvas.restore();
        }
    }

    protected void drawBitmap(Canvas canvas, Bitmap bitmap, Paint paint) {
        canvas.drawBitmap(bitmap, 0.0f, 0.0f, paint);
    }

    public abstract void drawInBackground(Canvas canvas);

    protected abstract void drawInUiThread(Canvas canvas, float f);

    public void onAttachToWindow() {
        if (this.attachedToWindow) {
            return;
        }
        this.attachedToWindow = true;
        this.error = false;
        int currentHeavyOperationFlags = NotificationCenter.getGlobalInstance().getCurrentHeavyOperationFlags() & (this.currentLayerNum ^ (-1));
        this.currentOpenedLayerFlags = currentHeavyOperationFlags;
        if (currentHeavyOperationFlags == 0 && this.paused) {
            this.paused = false;
            onResume();
        }
        NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.stopAllHeavyOperations);
        NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.startAllHeavyOperations);
    }

    public void onDetachFromWindow() {
        if (this.attachedToWindow) {
            if (!this.bitmapUpdating) {
                recycleBitmaps();
            }
            this.attachedToWindow = false;
            NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.stopAllHeavyOperations);
            NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.startAllHeavyOperations);
        }
    }

    public void onFrameReady() {
    }

    public void onPaused() {
    }

    public void onResume() {
    }

    public abstract void prepareDraw(long j);

    public void reset() {
        this.reset = true;
        this.frameGuid++;
        if (this.bitmap != null) {
            ArrayList arrayList = new ArrayList();
            arrayList.add(this.bitmap);
            this.bitmap = null;
            AndroidUtilities.recycleBitmaps(arrayList);
        }
    }

    public void setLayerNum(int i) {
        this.currentLayerNum = i;
        if (this.attachedToWindow) {
            this.currentOpenedLayerFlags = NotificationCenter.getGlobalInstance().getCurrentHeavyOperationFlags() & (this.currentLayerNum ^ (-1));
        }
    }
}
