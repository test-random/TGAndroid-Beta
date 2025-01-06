package org.telegram.ui.Components;

import android.animation.ValueAnimator;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RecordingCanvas;
import android.graphics.RectF;
import android.graphics.RenderEffect;
import android.graphics.RenderNode;
import android.graphics.Shader;
import android.graphics.drawable.Drawable;
import android.opengl.GLES20;
import android.os.Build;
import android.text.TextUtils;
import android.view.View;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.Iterator;
import javax.microedition.khronos.egl.EGL10;
import javax.microedition.khronos.egl.EGLContext;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.BotFullscreenButtons$$ExternalSyntheticApiModelOutline2;
import org.telegram.messenger.DispatchQueue;
import org.telegram.messenger.ImageReceiver;
import org.telegram.messenger.R;
import org.telegram.messenger.Utilities;
import org.telegram.ui.Components.BlurringShader;

public class BlurringShader {
    private Bitmap bitmap;
    private boolean bitmapAvailable;
    private final Object bitmapLock;
    private ByteBuffer buffer;
    private BlurManager currentManager;
    private final int[] framebuffer;
    private int gradientBottom;
    private int gradientTop;
    private int height;
    private final Matrix iMatrix;
    private final Runnable invalidateViews;
    private final float[] m3x3;
    private final float[] matrix;
    private final Object matrixLock;
    private FloatBuffer padPosBuffer;
    private int padding;
    private FilterGLThread parent;
    private FloatBuffer posBuffer;
    private Program[] program;
    private boolean setupTransform;
    private final int[] texture;
    private FloatBuffer uvBuffer;
    private int width;

    public static class BlurManager {
        private Object blurRenderNode;
        private EGLContext context;
        private BlurringShader currentShader;
        private Bitmap fallbackBitmap;
        public int padding;
        private Object renderNode;
        private int renderNodeBackgroundColor;
        private View renderNodeView;
        private final View view;
        private final ArrayList parents = new ArrayList();
        private final ArrayList holders = new ArrayList();
        private final ArrayList invalidateHolders = new ArrayList();
        private final Object contextLock = new Object();
        private final Object textureLock = new Object();
        private final ThumbBlurer thumbBlurer = new ThumbBlurer(0, new Runnable() {
            @Override
            public final void run() {
                BlurringShader.BlurManager.this.invalidateFallbackBlur();
            }
        });
        private int i = 0;

        public BlurManager(View view) {
            this.view = view;
            if (view.isAttachedToWindow()) {
                updateParents();
            }
            view.addOnAttachStateChangeListener(new View.OnAttachStateChangeListener() {
                @Override
                public void onViewAttachedToWindow(View view2) {
                    BlurManager.this.updateParents();
                }

                @Override
                public void onViewDetachedFromWindow(View view2) {
                    BlurManager.this.parents.clear();
                }
            });
        }

        public void invalidateFallbackBlur() {
            this.fallbackBitmap = this.thumbBlurer.thumbBitmap;
            invalidate();
        }

        public void updateParents() {
            this.parents.clear();
            for (View view = this.view; view != null; view = (View) view.getParent()) {
                this.parents.add(0, view);
                if (!(view.getParent() instanceof View)) {
                    return;
                }
            }
        }

        public void acquiredContext(EGLContext eGLContext) {
            synchronized (this.contextLock) {
                try {
                    if (this.context == null) {
                        this.context = eGLContext;
                    }
                } catch (Throwable th) {
                    throw th;
                }
            }
        }

        public void attach(Runnable runnable) {
            this.invalidateHolders.add(runnable);
        }

        public void attach(StoryBlurDrawer storyBlurDrawer) {
            this.holders.add(storyBlurDrawer);
        }

        public void destroyedContext(EGLContext eGLContext) {
            synchronized (this.contextLock) {
                try {
                    if (this.context == eGLContext) {
                        this.context = null;
                    }
                } catch (Throwable th) {
                    throw th;
                }
            }
        }

        public void detach(Runnable runnable) {
            this.invalidateHolders.remove(runnable);
            if (this.invalidateHolders.isEmpty() && this.holders.isEmpty()) {
                this.thumbBlurer.destroy();
            }
        }

        public void detach(StoryBlurDrawer storyBlurDrawer) {
            this.holders.remove(storyBlurDrawer);
            if (this.invalidateHolders.isEmpty() && this.holders.isEmpty()) {
                this.thumbBlurer.destroy();
            }
        }

        public Bitmap getBitmap() {
            Bitmap bitmap;
            BlurringShader blurringShader = this.currentShader;
            return (blurringShader == null || (bitmap = blurringShader.getBitmap()) == null) ? this.fallbackBitmap : bitmap;
        }

        public EGLContext getParentContext() {
            synchronized (this.contextLock) {
                try {
                    EGLContext eGLContext = this.context;
                    if (eGLContext != null) {
                        return eGLContext;
                    }
                    return EGL10.EGL_NO_CONTEXT;
                } catch (Throwable th) {
                    throw th;
                }
            }
        }

        public int getTexture() {
            BlurringShader blurringShader = this.currentShader;
            if (blurringShader != null) {
                return blurringShader.getTexture();
            }
            return -1;
        }

        public Object getTextureLock() {
            return this.textureLock;
        }

        public boolean hasRenderNode() {
            return this.blurRenderNode != null;
        }

        public void invalidate() {
            Iterator it = this.holders.iterator();
            while (it.hasNext()) {
                ((StoryBlurDrawer) it.next()).view.invalidate();
            }
            Iterator it2 = this.invalidateHolders.iterator();
            while (it2.hasNext()) {
                ((Runnable) it2.next()).run();
            }
        }

        public void resetBitmap() {
            BlurringShader blurringShader = this.currentShader;
            if (blurringShader != null) {
                blurringShader.resetBitmap();
            }
        }

        public void setFallbackBlur(Bitmap bitmap, int i) {
            setFallbackBlur(bitmap, i, false);
        }

        public void setFallbackBlur(Bitmap bitmap, int i, boolean z) {
            ThumbBlurer thumbBlurer = this.thumbBlurer;
            StringBuilder sb = new StringBuilder();
            sb.append("");
            int i2 = this.i;
            this.i = i2 + 1;
            sb.append(i2);
            this.fallbackBitmap = thumbBlurer.getBitmap(bitmap, sb.toString(), i, 0, z);
        }

        public void setRenderNode(View view, Object obj, int i) {
            RenderEffect createBlurEffect;
            int width;
            int height;
            RecordingCanvas beginRecording;
            this.renderNodeView = view;
            this.renderNode = obj;
            this.renderNodeBackgroundColor = i;
            if (obj == null || Build.VERSION.SDK_INT < 31) {
                this.blurRenderNode = null;
                return;
            }
            RenderNode m = BotFullscreenButtons$$ExternalSyntheticApiModelOutline2.m(obj);
            RenderNode renderNode = new RenderNode("blurRenderNode");
            createBlurEffect = RenderEffect.createBlurEffect(AndroidUtilities.dp(35.0f), AndroidUtilities.dp(35.0f), Shader.TileMode.CLAMP);
            renderNode.setRenderEffect(createBlurEffect);
            width = m.getWidth();
            height = m.getHeight();
            renderNode.setPosition(0, 0, width, height);
            beginRecording = renderNode.beginRecording();
            beginRecording.drawColor(i);
            beginRecording.drawRenderNode(m);
            renderNode.endRecording();
            this.blurRenderNode = renderNode;
        }

        public void setShader(BlurringShader blurringShader) {
            if (this.currentShader == blurringShader) {
                return;
            }
            this.currentShader = blurringShader;
            if (blurringShader != null) {
                invalidate();
            }
        }
    }

    private static class Program {
        int flipyHandle;
        int gl;
        int gradientBottomHandle;
        int gradientTopHandle;
        int hasVideoMatrixHandle;
        int matrixHandle;
        int posHandle;
        int stepHandle;
        int szHandle;
        int texHandle;
        int texSzHandle;
        int uvHandle;
        int videoMatrixHandle;

        public Program(int i) {
            this.gl = i;
            this.posHandle = GLES20.glGetAttribLocation(i, "p");
            this.uvHandle = GLES20.glGetAttribLocation(i, "inputuv");
            this.matrixHandle = GLES20.glGetUniformLocation(i, "matrix");
            this.texHandle = GLES20.glGetUniformLocation(i, "tex");
            this.szHandle = GLES20.glGetUniformLocation(i, "sz");
            this.texSzHandle = GLES20.glGetUniformLocation(i, "texSz");
            this.gradientTopHandle = GLES20.glGetUniformLocation(i, "gtop");
            this.gradientBottomHandle = GLES20.glGetUniformLocation(i, "gbottom");
            this.stepHandle = GLES20.glGetUniformLocation(i, "step");
            this.videoMatrixHandle = GLES20.glGetUniformLocation(i, "videoMatrix");
            this.hasVideoMatrixHandle = GLES20.glGetUniformLocation(i, "hasVideoMatrix");
            this.flipyHandle = GLES20.glGetUniformLocation(i, "flipy");
        }
    }

    public static class StoryBlurDrawer {
        private boolean animateBitmapChange;
        private Integer bgColor;
        private BitmapShader bitmapShader;
        RectF bounds;
        private final Path clipPath;
        private int clipPathHeight;
        private int clipPathWidth;
        public final ColorMatrix colorMatrix;
        private ValueAnimator crossfadeAnimator;
        private boolean customOffset;
        private float customOffsetX;
        private float customOffsetY;
        private Bitmap lastBitmap;
        private final int[] loc1;
        private final int[] loc2;
        private final BlurManager manager;
        private final Matrix matrix;
        public Paint oldPaint;
        private float oldPaintAlpha;
        private boolean oldPaintSet;
        public Paint paint;
        public RenderNode renderNode;
        private Paint[] tempPaints;
        private final int type;
        private final View view;
        private boolean wasDark;
        public boolean xfer;

        public StoryBlurDrawer(BlurManager blurManager, View view, int i) {
            this(blurManager, view, i, false);
        }

        public StoryBlurDrawer(final BlurManager blurManager, View view, int i, boolean z) {
            float f;
            this.oldPaint = new Paint(3);
            this.paint = new Paint(3);
            this.clipPath = new Path();
            this.matrix = new Matrix();
            this.bounds = new RectF();
            this.wasDark = false;
            this.loc1 = new int[2];
            this.loc2 = new int[2];
            this.manager = blurManager;
            this.view = view;
            this.type = i;
            this.animateBitmapChange = z;
            ColorMatrix colorMatrix = new ColorMatrix();
            this.colorMatrix = colorMatrix;
            if (i == 0) {
                AndroidUtilities.adjustSaturationColorMatrix(colorMatrix, 0.45f);
            } else {
                if (i == 5) {
                    Paint paint = this.paint;
                    PorterDuff.Mode mode = PorterDuff.Mode.SRC_IN;
                    paint.setXfermode(new PorterDuffXfermode(mode));
                    this.oldPaint.setXfermode(new PorterDuffXfermode(mode));
                } else if (i == 2) {
                    Paint paint2 = this.paint;
                    PorterDuff.Mode mode2 = PorterDuff.Mode.SRC_IN;
                    paint2.setXfermode(new PorterDuffXfermode(mode2));
                    this.oldPaint.setXfermode(new PorterDuffXfermode(mode2));
                    AndroidUtilities.adjustBrightnessColorMatrix(colorMatrix, 0.4f);
                } else {
                    if (i == 1) {
                        AndroidUtilities.adjustSaturationColorMatrix(colorMatrix, 0.35f);
                        AndroidUtilities.adjustBrightnessColorMatrix(colorMatrix, 0.7f);
                        f = 1.5f;
                    } else if (i == 3) {
                        AndroidUtilities.adjustSaturationColorMatrix(colorMatrix, 0.5f);
                    } else if (i == 4) {
                        this.bgColor = -10329502;
                        AndroidUtilities.adjustSaturationColorMatrix(colorMatrix, 0.6f);
                        AndroidUtilities.adjustBrightnessColorMatrix(colorMatrix, 0.3f);
                        f = 1.2f;
                    } else if (i == 6) {
                        AndroidUtilities.adjustSaturationColorMatrix(colorMatrix, 0.4f);
                        AndroidUtilities.multiplyBrightnessColorMatrix(colorMatrix, 0.35f);
                    } else if (i == 7) {
                        AndroidUtilities.adjustSaturationColorMatrix(colorMatrix, 0.5f);
                        f = 0.95f;
                    } else if (i == 8) {
                        AndroidUtilities.adjustBrightnessColorMatrix(colorMatrix, -0.15f);
                        AndroidUtilities.adjustSaturationColorMatrix(colorMatrix, 0.47f);
                    } else if (i == 9) {
                        Paint paint3 = this.paint;
                        PorterDuff.Mode mode3 = PorterDuff.Mode.SRC_IN;
                        paint3.setXfermode(new PorterDuffXfermode(mode3));
                        this.oldPaint.setXfermode(new PorterDuffXfermode(mode3));
                        AndroidUtilities.adjustBrightnessColorMatrix(colorMatrix, 0.4f);
                        AndroidUtilities.adjustSaturationColorMatrix(colorMatrix, 0.45f);
                        this.xfer = true;
                    } else if (i == 10) {
                        colorMatrix.setSaturation(1.6f);
                        AndroidUtilities.multiplyBrightnessColorMatrix(colorMatrix, this.wasDark ? 0.97f : 0.92f);
                        AndroidUtilities.adjustBrightnessColorMatrix(colorMatrix, this.wasDark ? 0.12f : -0.06f);
                    }
                    AndroidUtilities.multiplyBrightnessColorMatrix(colorMatrix, f);
                }
                AndroidUtilities.adjustSaturationColorMatrix(colorMatrix, 0.3f);
                this.xfer = true;
            }
            this.paint.setColorFilter(new ColorMatrixColorFilter(colorMatrix));
            this.oldPaint.setColorFilter(new ColorMatrixColorFilter(colorMatrix));
            if (view.isAttachedToWindow() && blurManager != null) {
                blurManager.attach(this);
            }
            view.addOnAttachStateChangeListener(new View.OnAttachStateChangeListener() {
                @Override
                public void onViewAttachedToWindow(View view2) {
                    BlurManager blurManager2 = blurManager;
                    if (blurManager2 != null) {
                        blurManager2.attach(StoryBlurDrawer.this);
                    }
                }

                @Override
                public void onViewDetachedFromWindow(View view2) {
                    BlurManager blurManager2 = blurManager;
                    if (blurManager2 != null) {
                        blurManager2.detach(StoryBlurDrawer.this);
                    }
                    StoryBlurDrawer.this.recycle();
                }
            });
        }

        private void animateOldPaint() {
            ValueAnimator valueAnimator = this.crossfadeAnimator;
            if (valueAnimator != null) {
                valueAnimator.cancel();
                this.crossfadeAnimator = null;
            }
            this.oldPaintAlpha = 1.0f;
            ValueAnimator ofFloat = ValueAnimator.ofFloat(1.0f, 0.0f);
            this.crossfadeAnimator = ofFloat;
            ofFloat.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public final void onAnimationUpdate(ValueAnimator valueAnimator2) {
                    BlurringShader.StoryBlurDrawer.this.lambda$animateOldPaint$0(valueAnimator2);
                }
            });
            this.crossfadeAnimator.start();
        }

        private int getBackgroundColor() {
            Integer num = this.bgColor;
            return num != null ? num.intValue() : this.manager.renderNodeBackgroundColor;
        }

        public void lambda$animateOldPaint$0(ValueAnimator valueAnimator) {
            this.oldPaintAlpha = ((Float) valueAnimator.getAnimatedValue()).floatValue();
            this.view.invalidate();
        }

        public void recycle() {
            this.lastBitmap = null;
            Paint paint = this.paint;
            this.bitmapShader = null;
            paint.setShader(null);
        }

        private boolean setupMatrix(int i, int i2, boolean z) {
            View view;
            this.matrix.reset();
            BlurManager blurManager = this.manager;
            View view2 = blurManager != null ? z ? blurManager.renderNodeView : blurManager.view : null;
            if (this.customOffset) {
                this.matrix.postTranslate(-this.customOffsetX, -this.customOffsetY);
            } else if (this.manager != null) {
                View view3 = this.view;
                do {
                    this.matrix.preScale(1.0f / view3.getScaleX(), 1.0f / view3.getScaleY(), view3.getPivotX(), view3.getPivotY());
                    this.matrix.preRotate(-view3.getRotation(), view3.getPivotX(), view3.getPivotY());
                    this.matrix.preTranslate(-view3.getX(), -view3.getY());
                    if (!(view3.getParent() instanceof View) || (view3 = (View) view3.getParent()) == null) {
                        break;
                    }
                } while (!this.manager.parents.contains(view3));
                if (view2 != view3) {
                    int indexOf = this.manager.parents.indexOf(view3) + 1;
                    if (indexOf == 0 && (view = (View) this.manager.parents.get(indexOf)) != null) {
                        view3.getLocationOnScreen(this.loc1);
                        view.getLocationOnScreen(this.loc2);
                        Matrix matrix = this.matrix;
                        int i3 = this.loc2[0];
                        int[] iArr = this.loc1;
                        matrix.preTranslate(i3 - iArr[0], r3[1] - iArr[1]);
                    }
                    while (indexOf >= 0 && indexOf < this.manager.parents.size()) {
                        View view4 = (View) this.manager.parents.get(indexOf);
                        if (view4 != null) {
                            this.matrix.preScale(view4.getScaleX(), view4.getScaleY(), view4.getPivotX(), view4.getPivotY());
                            this.matrix.preRotate(view4.getRotation(), view4.getPivotX(), view4.getPivotY());
                            this.matrix.preTranslate(view4.getX(), view4.getY());
                            indexOf++;
                        }
                    }
                }
            }
            if (view2 != null) {
                this.matrix.preScale(view2.getWidth() / i, view2.getHeight() / i2);
            }
            return true;
        }

        private void updateBounds() {
            Bitmap bitmap = this.manager.getBitmap();
            if (bitmap == null) {
                return;
            }
            if (this.bitmapShader == null || this.lastBitmap != bitmap) {
                this.lastBitmap = bitmap;
                Shader.TileMode tileMode = Shader.TileMode.CLAMP;
                BitmapShader bitmapShader = new BitmapShader(bitmap, tileMode, tileMode);
                this.bitmapShader = bitmapShader;
                this.paint.setShader(bitmapShader);
            }
            float width = this.bounds.width() / this.lastBitmap.getWidth();
            float height = this.bounds.height() / this.lastBitmap.getHeight();
            this.matrix.reset();
            Matrix matrix = this.matrix;
            RectF rectF = this.bounds;
            matrix.postTranslate(rectF.left, rectF.top);
            this.matrix.preScale(width, height);
            this.bitmapShader.setLocalMatrix(this.matrix);
        }

        public StoryBlurDrawer adapt(boolean z) {
            if (this.wasDark != z) {
                this.wasDark = z;
                if (this.type == 10) {
                    ColorMatrix colorMatrix = new ColorMatrix();
                    colorMatrix.setSaturation(1.6f);
                    AndroidUtilities.multiplyBrightnessColorMatrix(colorMatrix, this.wasDark ? 0.97f : 0.92f);
                    AndroidUtilities.adjustBrightnessColorMatrix(colorMatrix, this.wasDark ? 0.12f : -0.06f);
                    this.paint.setColorFilter(new ColorMatrixColorFilter(colorMatrix));
                    this.oldPaint.setColorFilter(new ColorMatrixColorFilter(colorMatrix));
                }
            }
            return this;
        }

        public void drawRect(Canvas canvas) {
            drawRect(canvas, 0.0f, 0.0f, 1.0f);
        }

        public void drawRect(Canvas canvas, float f, float f2, float f3) {
            drawRect(canvas, f, f2, f3, true);
        }

        public void drawRect(android.graphics.Canvas r6, float r7, float r8, float r9, boolean r10) {
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.BlurringShader.StoryBlurDrawer.drawRect(android.graphics.Canvas, float, float, float, boolean):void");
        }

        public Paint getPaint(float f) {
            return getPaint(f, 0.0f, 0.0f);
        }

        public Paint getPaint(float f, float f2, float f3) {
            Bitmap bitmap;
            Bitmap bitmap2;
            BlurManager blurManager = this.manager;
            if (blurManager == null || (bitmap = blurManager.getBitmap()) == null) {
                return null;
            }
            BitmapShader bitmapShader = this.bitmapShader;
            if (bitmapShader == null || this.lastBitmap != bitmap) {
                if (this.animateBitmapChange && bitmapShader != null && (bitmap2 = this.lastBitmap) != null && !bitmap2.isRecycled() && !bitmap.isRecycled()) {
                    Paint paint = this.paint;
                    this.paint = this.oldPaint;
                    this.oldPaint = paint;
                    this.oldPaintSet = true;
                    animateOldPaint();
                }
                this.lastBitmap = bitmap;
                Shader.TileMode tileMode = Shader.TileMode.CLAMP;
                BitmapShader bitmapShader2 = new BitmapShader(bitmap, tileMode, tileMode);
                this.bitmapShader = bitmapShader2;
                this.paint.setShader(bitmapShader2);
            }
            if (!setupMatrix(bitmap.getWidth(), bitmap.getHeight(), false)) {
                return null;
            }
            this.matrix.postTranslate(-f2, -f3);
            this.bitmapShader.setLocalMatrix(this.matrix);
            this.paint.setAlpha((int) (f * 255.0f));
            return this.paint;
        }

        public Paint[] getPaints(float f, float f2, float f3) {
            Paint paint = getPaint(f, f2, f3);
            boolean z = this.oldPaintSet;
            Paint paint2 = z ? this.oldPaint : null;
            if (paint != null && z) {
                paint.setAlpha((int) ((1.0f - this.oldPaintAlpha) * 255.0f * f));
            }
            if (paint2 != null) {
                paint2.setAlpha((int) (f * 255.0f));
            }
            if (this.tempPaints == null) {
                this.tempPaints = new Paint[2];
            }
            Paint[] paintArr = this.tempPaints;
            paintArr[0] = paint2;
            paintArr[1] = paint;
            return paintArr;
        }

        public Drawable makeDrawable(final float f, final float f2, final Drawable drawable, final float f3) {
            return new Drawable() {
                float alpha = 1.0f;
                private final Paint dimPaint = new Paint(1);
                private final android.graphics.Rect rect = new android.graphics.Rect();
                private final Path clipPath = new Path();

                private Paint getPaint() {
                    Bitmap bitmap;
                    if (StoryBlurDrawer.this.manager == null || (bitmap = StoryBlurDrawer.this.manager.getBitmap()) == null) {
                        return null;
                    }
                    if (StoryBlurDrawer.this.bitmapShader == null || StoryBlurDrawer.this.lastBitmap != bitmap) {
                        StoryBlurDrawer storyBlurDrawer = StoryBlurDrawer.this;
                        Bitmap bitmap2 = StoryBlurDrawer.this.lastBitmap = bitmap;
                        Shader.TileMode tileMode = Shader.TileMode.CLAMP;
                        storyBlurDrawer.bitmapShader = new BitmapShader(bitmap2, tileMode, tileMode);
                        StoryBlurDrawer storyBlurDrawer2 = StoryBlurDrawer.this;
                        storyBlurDrawer2.paint.setShader(storyBlurDrawer2.bitmapShader);
                    }
                    StoryBlurDrawer.this.matrix.reset();
                    StoryBlurDrawer.this.matrix.postTranslate((-StoryBlurDrawer.this.customOffsetX) - f, (-StoryBlurDrawer.this.customOffsetY) - f2);
                    if (StoryBlurDrawer.this.manager.view != null) {
                        StoryBlurDrawer.this.matrix.preScale(StoryBlurDrawer.this.manager.view.getWidth() / bitmap.getWidth(), StoryBlurDrawer.this.manager.view.getHeight() / bitmap.getHeight());
                    }
                    StoryBlurDrawer.this.bitmapShader.setLocalMatrix(StoryBlurDrawer.this.matrix);
                    StoryBlurDrawer.this.paint.setAlpha((int) (this.alpha * 255.0f));
                    return StoryBlurDrawer.this.paint;
                }

                @Override
                public void draw(android.graphics.Canvas r13) {
                    throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.BlurringShader.StoryBlurDrawer.AnonymousClass2.draw(android.graphics.Canvas):void");
                }

                @Override
                public int getOpacity() {
                    return -2;
                }

                @Override
                public boolean getPadding(android.graphics.Rect rect) {
                    Drawable drawable2 = drawable;
                    if (drawable2 != null) {
                        return drawable2.getPadding(rect);
                    }
                    rect.set(0, 0, 0, 0);
                    return true;
                }

                @Override
                public void setAlpha(int i) {
                    this.alpha = i / 255.0f;
                }

                @Override
                public void setColorFilter(ColorFilter colorFilter) {
                }
            };
        }

        public void setBounds(float f, float f2, float f3, float f4) {
            RectF rectF = AndroidUtilities.rectTmp;
            rectF.set(f, f2, f3, f4);
            setBounds(rectF);
        }

        public void setBounds(RectF rectF) {
            RectF rectF2 = this.bounds;
            if (rectF2.top == rectF.top && rectF2.bottom == rectF.bottom && rectF2.left == rectF.left && rectF2.right == rectF.right) {
                return;
            }
            rectF2.set(rectF);
            updateBounds();
        }
    }

    public static class ThumbBlurer {
        private final Paint clearPaint;
        private Runnable generate;
        private final Runnable invalidate;
        private final int padding;
        private Bitmap thumbBitmap;
        private String thumbKey;

        public ThumbBlurer(int i, Runnable runnable) {
            Paint paint = new Paint(1);
            this.clearPaint = paint;
            this.padding = i;
            this.invalidate = runnable;
            paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        }

        public void lambda$getBitmap$0(String str, Bitmap bitmap, boolean z, Bitmap bitmap2) {
            if (TextUtils.equals(this.thumbKey, str)) {
                this.generate = null;
                Bitmap bitmap3 = this.thumbBitmap;
                if (bitmap3 != null) {
                    bitmap3.recycle();
                }
                this.thumbBitmap = bitmap;
                Runnable runnable = this.invalidate;
                if (runnable != null) {
                    runnable.run();
                }
            } else {
                bitmap.recycle();
            }
            if (z) {
                bitmap2.recycle();
            }
        }

        public void lambda$getBitmap$1(final Bitmap bitmap, int i, int i2, final String str, final boolean z) {
            int i3;
            int i4;
            if (bitmap == null || bitmap.isRecycled()) {
                return;
            }
            float width = bitmap.getWidth() / bitmap.getHeight();
            int round = (int) Math.round(Math.sqrt(width * 324.0f));
            int round2 = (int) Math.round(Math.sqrt(324.0f / width));
            if (i == 90 || i == 270) {
                i3 = round2;
                i4 = round;
            } else {
                i4 = round2;
                i3 = round;
            }
            int i5 = this.padding * 2;
            final Bitmap createBitmap = Bitmap.createBitmap(i5 + i3, i5 + i4, Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(createBitmap);
            android.graphics.Rect rect = new android.graphics.Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
            int i6 = this.padding;
            android.graphics.Rect rect2 = new android.graphics.Rect(i6, i6, i6 + round, i6 + round2);
            float f = this.padding;
            canvas.translate((i3 / 2.0f) + f, f + (i4 / 2.0f));
            if (i2 == 1) {
                canvas.scale(-1.0f, 1.0f);
            } else if (i2 == 2) {
                canvas.scale(1.0f, -1.0f);
            }
            canvas.rotate(i);
            float f2 = -this.padding;
            canvas.translate(f2 - (round / 2.0f), f2 - (round2 / 2.0f));
            try {
                canvas.drawBitmap(bitmap, rect, rect2, (Paint) null);
            } catch (Exception unused) {
            }
            Utilities.stackBlurBitmap(createBitmap, 6);
            int i7 = this.padding;
            if (i7 > 0) {
                canvas.drawRect(0.0f, 0.0f, round + i7, i7, this.clearPaint);
                float f3 = this.padding;
                canvas.drawRect(0.0f, f3, f3, r0 + round2, this.clearPaint);
                int i8 = this.padding;
                canvas.drawRect(i8 + round, i8, r2 + i8, i8 + round2, this.clearPaint);
                canvas.drawRect(0.0f, round2 + this.padding, round + r0 + r0, r3 + r0, this.clearPaint);
            }
            AndroidUtilities.runOnUIThread(new Runnable() {
                @Override
                public final void run() {
                    BlurringShader.ThumbBlurer.this.lambda$getBitmap$0(str, createBitmap, z, bitmap);
                }
            });
        }

        public void destroy() {
            this.thumbKey = null;
            if (this.generate != null) {
                Utilities.globalQueue.cancelRunnable(this.generate);
            }
            Bitmap bitmap = this.thumbBitmap;
            if (bitmap != null && !bitmap.isRecycled()) {
                this.thumbBitmap.recycle();
            }
            this.thumbBitmap = null;
        }

        public Bitmap getBitmap(final Bitmap bitmap, final String str, final int i, final int i2, final boolean z) {
            if (bitmap == null || bitmap.isRecycled()) {
                return null;
            }
            if (TextUtils.equals(this.thumbKey, str)) {
                Bitmap bitmap2 = this.thumbBitmap;
                if (bitmap2 != null) {
                    return bitmap2;
                }
                if (this.generate != null) {
                    return null;
                }
            }
            if (this.generate != null) {
                Utilities.globalQueue.cancelRunnable(this.generate);
            }
            this.thumbKey = str;
            DispatchQueue dispatchQueue = Utilities.globalQueue;
            Runnable runnable = new Runnable() {
                @Override
                public final void run() {
                    BlurringShader.ThumbBlurer.this.lambda$getBitmap$1(bitmap, i, i2, str, z);
                }
            };
            this.generate = runnable;
            dispatchQueue.postRunnable(runnable);
            return this.thumbBitmap;
        }

        public Bitmap getBitmap(ImageReceiver.BitmapHolder bitmapHolder) {
            if (bitmapHolder == null) {
                return null;
            }
            return getBitmap(bitmapHolder.bitmap, bitmapHolder.getKey(), bitmapHolder.orientation, 0, false);
        }

        public Bitmap getBitmap(ImageReceiver imageReceiver) {
            if (imageReceiver == null) {
                return null;
            }
            return getBitmap(imageReceiver.getBitmap(), imageReceiver.getImageKey(), imageReceiver.getOrientation(), imageReceiver.getInvert(), false);
        }
    }

    public BlurringShader() {
        this(null);
    }

    public BlurringShader(FilterGLThread filterGLThread) {
        this.width = 1;
        this.height = 1;
        this.padding = 0;
        this.program = new Program[2];
        this.m3x3 = new float[9];
        this.matrix = new float[16];
        this.matrixLock = new Object();
        this.bitmapLock = new Object();
        this.framebuffer = new int[3];
        this.texture = new int[3];
        this.invalidateViews = new Runnable() {
            @Override
            public final void run() {
                BlurringShader.this.lambda$new$0();
            }
        };
        this.iMatrix = new Matrix();
        this.parent = filterGLThread;
    }

    public void lambda$new$0() {
        BlurManager blurManager = this.currentManager;
        if (blurManager != null) {
            blurManager.invalidate();
        }
    }

    public void draw(float[] fArr, int i, int i2, int i3) {
        char c = fArr != null ? (char) 1 : (char) 0;
        Program program = this.program[c];
        if (program == null) {
            return;
        }
        GLES20.glBindFramebuffer(36160, this.framebuffer[0]);
        GLES20.glViewport(0, 0, this.width, this.height);
        GLES20.glClear(16384);
        GLES20.glUseProgram(program.gl);
        GLES20.glUniform1i(program.texHandle, 0);
        GLES20.glActiveTexture(33984);
        if (c != 0) {
            GLES20.glBindTexture(36197, i);
        } else {
            GLES20.glBindTexture(3553, i);
        }
        GLES20.glEnableVertexAttribArray(program.uvHandle);
        GLES20.glVertexAttribPointer(program.uvHandle, 2, 5126, false, 8, (Buffer) this.uvBuffer);
        GLES20.glEnableVertexAttribArray(program.posHandle);
        GLES20.glVertexAttribPointer(program.posHandle, 2, 5126, false, 8, (Buffer) this.posBuffer);
        GLES20.glUniform2f(program.szHandle, this.width, this.height);
        float f = i2;
        float f2 = i3;
        GLES20.glUniform2f(program.texSzHandle, f, f2);
        GLES20.glUniform1i(program.stepHandle, 0);
        GLES20.glUniform1f(program.flipyHandle, c != 0 ? 1.0f : 0.0f);
        if (c != 0) {
            GLES20.glUniformMatrix4fv(program.videoMatrixHandle, 1, false, fArr, 0);
        }
        GLES20.glUniform1f(program.hasVideoMatrixHandle, c == 0 ? 0.0f : 1.0f);
        org.telegram.ui.Components.Paint.Shader.SetColorUniform(program.gradientTopHandle, this.gradientTop);
        org.telegram.ui.Components.Paint.Shader.SetColorUniform(program.gradientBottomHandle, this.gradientBottom);
        synchronized (this.matrixLock) {
            GLES20.glUniformMatrix4fv(program.matrixHandle, 1, false, this.matrix, 0);
        }
        GLES20.glDrawArrays(5, 0, 4);
        if (c != 0) {
            program = this.program[0];
            if (program == null) {
                return;
            }
            GLES20.glUseProgram(program.gl);
            GLES20.glEnableVertexAttribArray(program.uvHandle);
            GLES20.glVertexAttribPointer(program.uvHandle, 2, 5126, false, 8, (Buffer) this.uvBuffer);
            GLES20.glEnableVertexAttribArray(program.posHandle);
            GLES20.glVertexAttribPointer(program.posHandle, 2, 5126, false, 8, (Buffer) this.posBuffer);
            GLES20.glUniform2f(program.szHandle, this.width, this.height);
            GLES20.glUniform2f(program.texSzHandle, f, f2);
            GLES20.glUniform1i(program.stepHandle, 0);
            org.telegram.ui.Components.Paint.Shader.SetColorUniform(program.gradientTopHandle, this.gradientTop);
            org.telegram.ui.Components.Paint.Shader.SetColorUniform(program.gradientBottomHandle, this.gradientBottom);
            GLES20.glUniform1f(program.flipyHandle, 0.0f);
            synchronized (this.matrixLock) {
                GLES20.glUniformMatrix4fv(program.matrixHandle, 1, false, this.matrix, 0);
            }
        }
        GLES20.glBindFramebuffer(36160, this.framebuffer[1]);
        GLES20.glUniform1i(program.stepHandle, 1);
        GLES20.glUniform1i(program.texHandle, 0);
        GLES20.glActiveTexture(33984);
        GLES20.glBindTexture(3553, this.texture[0]);
        GLES20.glDrawArrays(5, 0, 4);
        GLES20.glBindFramebuffer(36160, this.framebuffer[2]);
        int i4 = this.width;
        int i5 = this.padding * 2;
        GLES20.glViewport(0, 0, i4 + i5, this.height + i5);
        GLES20.glClear(16384);
        GLES20.glEnableVertexAttribArray(program.posHandle);
        GLES20.glVertexAttribPointer(program.posHandle, 2, 5126, false, 8, (Buffer) this.padPosBuffer);
        GLES20.glUniform1i(program.stepHandle, 2);
        GLES20.glUniform1i(program.texHandle, 0);
        GLES20.glActiveTexture(33984);
        GLES20.glBindTexture(3553, this.texture[1]);
        BlurManager blurManager = this.currentManager;
        Object textureLock = blurManager != null ? blurManager.getTextureLock() : null;
        if (textureLock != null) {
            synchronized (textureLock) {
                GLES20.glDrawArrays(5, 0, 4);
            }
        } else {
            GLES20.glDrawArrays(5, 0, 4);
        }
        ByteBuffer byteBuffer = this.buffer;
        if (byteBuffer != null) {
            byteBuffer.rewind();
            int i6 = this.width;
            int i7 = this.padding * 2;
            GLES20.glReadPixels(0, 0, i6 + i7, this.height + i7, 6408, 5121, this.buffer);
            synchronized (this.bitmapLock) {
                this.bitmap.copyPixelsFromBuffer(this.buffer);
                this.bitmapAvailable = true;
            }
            GLES20.glBindFramebuffer(36160, 0);
        }
        AndroidUtilities.cancelRunOnUIThread(this.invalidateViews);
        AndroidUtilities.runOnUIThread(this.invalidateViews);
    }

    public Bitmap getBitmap() {
        synchronized (this.bitmapLock) {
            try {
                if (!this.bitmapAvailable) {
                    return null;
                }
                return this.bitmap;
            } catch (Throwable th) {
                throw th;
            }
        }
    }

    public int getTexture() {
        return this.texture[2];
    }

    public void resetBitmap() {
        synchronized (this.bitmapLock) {
            this.bitmapAvailable = false;
        }
    }

    public void setBlurManager(BlurManager blurManager) {
        BlurManager blurManager2 = this.currentManager;
        if (blurManager2 != null) {
            blurManager2.setShader(null);
        }
        this.currentManager = blurManager;
        if (blurManager != null) {
            blurManager.setShader(this);
        }
    }

    public boolean setup(float f, boolean z, int i) {
        this.width = (int) Math.round(Math.sqrt(f * 324.0f));
        this.height = (int) Math.round(Math.sqrt(324.0f / f));
        this.padding = i;
        if (!this.setupTransform) {
            updateTransform(new Matrix(), 1, 1);
        }
        float[] fArr = new float[8];
        fArr[0] = -1.0f;
        fArr[1] = 1.0f;
        fArr[2] = 1.0f;
        fArr[3] = 1.0f;
        fArr[4] = -1.0f;
        fArr[5] = -1.0f;
        fArr[6] = 1.0f;
        fArr[7] = -1.0f;
        ByteBuffer allocateDirect = ByteBuffer.allocateDirect(32);
        allocateDirect.order(ByteOrder.nativeOrder());
        FloatBuffer asFloatBuffer = allocateDirect.asFloatBuffer();
        this.posBuffer = asFloatBuffer;
        asFloatBuffer.put(fArr);
        this.posBuffer.position(0);
        for (int i2 = 0; i2 < 4; i2++) {
            int i3 = i2 * 2;
            fArr[i3] = fArr[i3] * ((r13 - i) / this.width);
            int i4 = i3 + 1;
            fArr[i4] = fArr[i4] * ((r13 - i) / this.height);
        }
        ByteBuffer allocateDirect2 = ByteBuffer.allocateDirect(32);
        allocateDirect2.order(ByteOrder.nativeOrder());
        FloatBuffer asFloatBuffer2 = allocateDirect2.asFloatBuffer();
        this.padPosBuffer = asFloatBuffer2;
        asFloatBuffer2.put(fArr);
        this.padPosBuffer.position(0);
        ByteBuffer allocateDirect3 = ByteBuffer.allocateDirect(32);
        allocateDirect3.order(ByteOrder.nativeOrder());
        FloatBuffer asFloatBuffer3 = allocateDirect3.asFloatBuffer();
        this.uvBuffer = asFloatBuffer3;
        asFloatBuffer3.put(new float[]{0.0f, 1.0f, 1.0f, 1.0f, 0.0f, 0.0f, 1.0f, 0.0f});
        this.uvBuffer.position(0);
        String readRes = AndroidUtilities.readRes(R.raw.blur_vrt);
        String readRes2 = AndroidUtilities.readRes(R.raw.blur_frg);
        if (readRes == null || readRes2 == null) {
            return false;
        }
        for (int i5 = 0; i5 < 2; i5++) {
            if (i5 == 1) {
                readRes2 = "#extension GL_OES_EGL_image_external : require\n" + readRes2.replace("sampler2D tex", "samplerExternalOES tex");
            }
            int loadShader = FilterShaders.loadShader(35633, readRes);
            int loadShader2 = FilterShaders.loadShader(35632, readRes2);
            if (loadShader == 0 || loadShader2 == 0) {
                return false;
            }
            int glCreateProgram = GLES20.glCreateProgram();
            GLES20.glAttachShader(glCreateProgram, loadShader);
            GLES20.glAttachShader(glCreateProgram, loadShader2);
            GLES20.glBindAttribLocation(glCreateProgram, 0, "p");
            GLES20.glBindAttribLocation(glCreateProgram, 1, "inputuv");
            GLES20.glLinkProgram(glCreateProgram);
            int[] iArr = new int[1];
            GLES20.glGetProgramiv(glCreateProgram, 35714, iArr, 0);
            if (iArr[0] == 0) {
                GLES20.glDeleteProgram(glCreateProgram);
                return false;
            }
            this.program[i5] = new Program(glCreateProgram);
        }
        GLES20.glGenFramebuffers(3, this.framebuffer, 0);
        GLES20.glGenTextures(3, this.texture, 0);
        int i6 = 0;
        while (i6 < 3) {
            GLES20.glBindTexture(3553, this.texture[i6]);
            GLES20.glTexImage2D(3553, 0, 6408, this.width + (i6 == 2 ? i * 2 : 0), this.height + (i6 == 2 ? i * 2 : 0), 0, 6408, 5121, null);
            GLES20.glTexParameteri(3553, 10242, 33071);
            GLES20.glTexParameteri(3553, 10243, 33071);
            GLES20.glTexParameteri(3553, 10241, 9729);
            GLES20.glTexParameteri(3553, 10240, 9729);
            GLES20.glBindFramebuffer(36160, this.framebuffer[i6]);
            GLES20.glFramebufferTexture2D(36160, 36064, 3553, this.texture[i6], 0);
            if (GLES20.glCheckFramebufferStatus(36160) != 36053) {
                return false;
            }
            i6++;
        }
        GLES20.glBindFramebuffer(36160, 0);
        if (z) {
            int i7 = i * 2;
            this.bitmap = Bitmap.createBitmap(this.width + i7, this.height + i7, Bitmap.Config.ARGB_8888);
            this.buffer = ByteBuffer.allocateDirect((this.width + i7) * (i7 + this.height) * 4);
        }
        return true;
    }

    public void updateGradient(int i, int i2) {
        this.gradientTop = i;
        this.gradientBottom = i2;
    }

    public void updateTransform(Matrix matrix) {
        this.setupTransform = true;
        matrix.getValues(this.m3x3);
        synchronized (this.matrixLock) {
            float[] fArr = this.matrix;
            float[] fArr2 = this.m3x3;
            fArr[0] = fArr2[0];
            fArr[1] = fArr2[3];
            fArr[2] = 0.0f;
            fArr[3] = fArr2[6];
            fArr[4] = fArr2[1];
            fArr[5] = fArr2[4];
            fArr[6] = 0.0f;
            fArr[7] = fArr2[7];
            fArr[8] = 0.0f;
            fArr[9] = 0.0f;
            fArr[10] = 1.0f;
            fArr[11] = 0.0f;
            fArr[12] = fArr2[2];
            fArr[13] = fArr2[5];
            fArr[14] = 0.0f;
            fArr[15] = fArr2[8];
        }
    }

    public void updateTransform(Matrix matrix, int i, int i2) {
        matrix.invert(this.iMatrix);
        float f = i;
        float f2 = i2;
        this.iMatrix.preScale(f, f2);
        this.iMatrix.postScale(1.0f / f, 1.0f / f2);
        updateTransform(this.iMatrix);
    }
}
