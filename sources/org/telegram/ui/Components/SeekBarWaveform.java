package org.telegram.ui.Components;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.Shader;
import android.os.SystemClock;
import android.view.View;
import androidx.core.graphics.ColorUtils;
import androidx.core.math.MathUtils;
import java.util.ArrayList;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.MediaController;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.Utilities;
import org.telegram.ui.Components.SeekBar;

public class SeekBarWaveform {
    private static Paint paintInner;
    private static Paint paintOuter;
    private Path alphaPath;
    private ArrayList animatedValues;
    private SeekBar.SeekBarDelegate delegate;
    public float explodeProgress;
    public float explosionRate;
    private float[] fromHeights;
    private int fromWidth;
    private int height;
    private float[] heights;
    private int innerColor;
    private boolean isUnread;
    private boolean loading;
    private Paint loadingPaint;
    private int loadingPaintColor1;
    private int loadingPaintColor2;
    private float loadingPaintWidth;
    private long loadingStart;
    private MessageObject messageObject;
    private int outerColor;
    private View parentView;
    private Particles particles;
    private Path path;
    private float progress;
    private boolean selected;
    private int selectedColor;
    private float startX;
    private float[] toHeights;
    private int toWidth;
    private byte[] waveformBytes;
    private int width;
    private int thumbX = 0;
    private int thumbDX = 0;
    private boolean startDraging = false;
    private boolean pressed = false;
    private float alpha = 1.0f;
    private float clearProgress = 1.0f;
    private AnimatedFloat appearFloat = new AnimatedFloat(125, 600, CubicBezierInterpolator.EASE_OUT_QUINT);
    private float waveScaling = 1.0f;
    private AnimatedFloat loadingFloat = new AnimatedFloat(150, CubicBezierInterpolator.DEFAULT);
    private boolean exploding = false;

    public static class Particles {
        private final int count;
        private RectF emitArea;
        private Runnable invalidate;
        private long lastTime;
        private final Paint paint;
        private final ArrayList particles = new ArrayList(50);
        private final ArrayList deadParticles = new ArrayList(50);

        public class Particle {
            float d;
            float t;
            float v;
            float vx;
            float vy;
            float x;
            float y;

            private Particle() {
            }
        }

        public Particles(int i, Runnable runnable) {
            Paint paint = new Paint(1);
            this.paint = paint;
            this.count = i;
            this.invalidate = runnable;
            paint.setStrokeWidth(AndroidUtilities.dp(1.33f));
        }

        public void draw(Canvas canvas, float f) {
            long currentTimeMillis = System.currentTimeMillis();
            long min = Math.min(20L, currentTimeMillis - this.lastTime);
            this.lastTime = currentTimeMillis;
            int i = 0;
            while (i < this.particles.size()) {
                Particle particle = (Particle) this.particles.get(i);
                float f2 = (float) min;
                float f3 = particle.t - (f2 / particle.d);
                particle.t = f3;
                if (f3 < 0.0f) {
                    this.deadParticles.add(particle);
                    this.particles.remove(i);
                    i--;
                } else {
                    float f4 = particle.x;
                    float f5 = particle.vx;
                    float f6 = particle.v;
                    particle.x = f4 + (((f5 * f6) * f2) / 500.0f);
                    float f7 = particle.y;
                    float f8 = particle.vy;
                    particle.y = f7 + (((f6 * f8) * f2) / 500.0f);
                    particle.vy = f8 - (((float) (AndroidUtilities.dp(0.33f) * min)) / 500.0f);
                }
                i++;
            }
            if (this.emitArea != null) {
                int min2 = Math.min(4, this.count - this.particles.size());
                for (int i2 = 0; i2 < min2; i2++) {
                    Particle particle2 = this.deadParticles.isEmpty() ? new Particle() : (Particle) this.deadParticles.remove(0);
                    RectF rectF = this.emitArea;
                    particle2.x = rectF.left + (rectF.width() * Utilities.random.nextFloat());
                    RectF rectF2 = this.emitArea;
                    particle2.y = rectF2.top + (rectF2.height() * Utilities.random.nextFloat());
                    double nextInt = Utilities.random.nextInt(200) - 125;
                    Double.isNaN(nextInt);
                    double d = nextInt * 0.017453292519943295d;
                    particle2.vx = ((float) (Math.cos(d) - Math.sin(d))) * 0.8f;
                    particle2.vy = ((float) (Math.sin(d) + Math.cos(d))) - 0.2f;
                    particle2.t = 1.0f;
                    particle2.v = AndroidUtilities.dp((Utilities.random.nextFloat() * 7.0f) + 10.0f);
                    particle2.d = AndroidUtilities.lerp(420, 550, Utilities.random.nextFloat());
                    this.particles.add(particle2);
                }
            }
            for (int i3 = 0; i3 < this.particles.size(); i3++) {
                Particle particle3 = (Particle) this.particles.get(i3);
                this.paint.setAlpha((int) (255.0f * f * particle3.t));
                canvas.drawPoint(particle3.x, particle3.y, this.paint);
            }
            Runnable runnable = this.invalidate;
            if (runnable != null) {
                runnable.run();
            }
        }

        public Particles setColor(int i) {
            this.paint.setColor(i);
            return this;
        }

        public Particles setEmitArea(RectF rectF) {
            this.emitArea = rectF;
            return this;
        }
    }

    public SeekBarWaveform(Context context) {
        if (paintInner == null) {
            paintInner = new Paint(1);
            paintOuter = new Paint(1);
            Paint paint = paintInner;
            Paint.Style style = Paint.Style.FILL;
            paint.setStyle(style);
            paintOuter.setStyle(style);
        }
    }

    private void addBar(Path path, float f, float f2) {
        float dpf2 = AndroidUtilities.dpf2(2.0f);
        int dp = (this.height - AndroidUtilities.dp(14.0f)) / 2;
        float f3 = f2 * this.waveScaling;
        RectF rectF = AndroidUtilities.rectTmp;
        float f4 = dpf2 / 2.0f;
        rectF.set((AndroidUtilities.dpf2(1.0f) + f) - f4, AndroidUtilities.dp(7.0f) + dp + ((-f3) - f4), f + AndroidUtilities.dpf2(1.0f) + f4, dp + AndroidUtilities.dp(7.0f) + f3 + f4);
        path.addRoundRect(rectF, dpf2, dpf2, Path.Direction.CW);
    }

    private float[] calculateHeights(int i) {
        byte[] bArr = this.waveformBytes;
        if (bArr == null || i <= 0) {
            return null;
        }
        float[] fArr = new float[i];
        int i2 = 5;
        int length = (bArr.length * 8) / 5;
        float f = length / i;
        int i3 = 0;
        int i4 = 0;
        float f2 = 0.0f;
        int i5 = 0;
        while (i3 < length) {
            if (i3 == i4) {
                int i6 = i4;
                int i7 = 0;
                while (i4 == i6) {
                    f2 += f;
                    i6 = (int) f2;
                    i7++;
                }
                int i8 = i3 * 5;
                int i9 = i8 / 8;
                int i10 = i8 - (i9 * 8);
                int i11 = 5 - (8 - i10);
                byte min = (byte) ((this.waveformBytes[i9] >> i10) & ((2 << (Math.min(i2, r15) - 1)) - 1));
                if (i11 > 0) {
                    int i12 = i9 + 1;
                    byte[] bArr2 = this.waveformBytes;
                    if (i12 < bArr2.length) {
                        min = (byte) (((byte) (min << i11)) | (bArr2[i12] & ((2 << (4 - r15)) - 1)));
                    }
                }
                int i13 = 0;
                while (i13 < i7) {
                    if (i5 >= i) {
                        return fArr;
                    }
                    fArr[i5] = Math.max(0.0f, (min * 7) / 31.0f);
                    i13++;
                    i5++;
                }
                i4 = i6;
            }
            i3++;
            i2 = 5;
        }
        return fArr;
    }

    private void drawFill(Canvas canvas, float f) {
        float dpf2 = AndroidUtilities.dpf2(2.0f);
        MessageObject messageObject = this.messageObject;
        boolean z = messageObject != null && messageObject.isContentUnread() && !this.messageObject.isOut() && this.progress <= 0.0f;
        this.isUnread = z;
        paintInner.setColor(z ? this.outerColor : this.selected ? this.selectedColor : this.innerColor);
        paintOuter.setColor(this.outerColor);
        this.loadingFloat.setParent(this.parentView);
        float f2 = this.loadingFloat.set((!this.loading || MediaController.getInstance().isPlayingMessage(this.messageObject)) ? 0.0f : 1.0f);
        Paint paint = paintInner;
        paint.setColor(ColorUtils.blendARGB(paint.getColor(), this.innerColor, f2));
        float f3 = 1.0f - f2;
        paintOuter.setAlpha((int) (r5.getAlpha() * f3 * f));
        paintInner.setAlpha((int) (r5.getAlpha() * f));
        canvas.drawRect(0.0f, 0.0f, this.width + dpf2, this.height, paintInner);
        if (f2 < 1.0f) {
            canvas.drawRect(0.0f, 0.0f, this.progress * (this.width + dpf2) * f3, this.height, paintOuter);
        }
        if (f2 > 0.0f) {
            if (this.loadingPaint == null || Math.abs(this.loadingPaintWidth - this.width) > AndroidUtilities.dp(8.0f) || this.loadingPaintColor1 != this.innerColor || this.loadingPaintColor2 != this.outerColor) {
                if (this.loadingPaint == null) {
                    this.loadingPaint = new Paint(1);
                }
                this.loadingPaintColor1 = this.innerColor;
                this.loadingPaintColor2 = this.outerColor;
                Paint paint2 = this.loadingPaint;
                float f4 = this.width;
                this.loadingPaintWidth = f4;
                int i = this.loadingPaintColor1;
                paint2.setShader(new LinearGradient(0.0f, 0.0f, f4, 0.0f, new int[]{i, this.loadingPaintColor2, i}, new float[]{0.0f, 0.2f, 0.4f}, Shader.TileMode.CLAMP));
            }
            this.loadingPaint.setAlpha((int) (f2 * 255.0f * f));
            canvas.save();
            float pow = ((((float) Math.pow(((float) (SystemClock.elapsedRealtime() - this.loadingStart)) / 270.0f, 0.75d)) % 1.6f) - 0.6f) * this.loadingPaintWidth;
            canvas.translate(pow, 0.0f);
            canvas.drawRect(-pow, 0.0f, (this.width + 5) - pow, this.height, this.loadingPaint);
            canvas.restore();
            View view = this.parentView;
            if (view != null) {
                view.invalidate();
            }
        }
    }

    public void draw(Canvas canvas, View view) {
        int i;
        float f;
        float f2;
        RectF rectF;
        float[] fArr;
        float[] fArr2;
        int i2;
        int i3;
        int i4;
        if (this.waveformBytes == null || (i = this.width) == 0 || this.alpha <= 0.0f) {
            return;
        }
        float dpf2 = i / AndroidUtilities.dpf2(3.0f);
        if (dpf2 <= 0.1f) {
            return;
        }
        float f3 = this.clearProgress;
        if (f3 != 1.0f) {
            float f4 = f3 + 0.10666667f;
            this.clearProgress = f4;
            if (f4 > 1.0f) {
                this.clearProgress = 1.0f;
            } else {
                view.invalidate();
            }
        }
        float f5 = this.appearFloat.set(1.0f);
        Path path = this.path;
        if (path == null) {
            this.path = new Path();
        } else {
            path.reset();
        }
        Path path2 = this.alphaPath;
        if (path2 == null) {
            this.alphaPath = new Path();
        } else {
            path2.reset();
        }
        SeekBar.SeekBarDelegate seekBarDelegate = this.delegate;
        boolean z = seekBarDelegate != null && seekBarDelegate.reverseWaveform();
        float[] fArr3 = this.fromHeights;
        if (fArr3 == null || (fArr2 = this.toHeights) == null) {
            if (this.heights != null) {
                int i5 = 0;
                while (true) {
                    float f6 = i5;
                    if (f6 >= dpf2 || i5 >= this.heights.length) {
                        break;
                    }
                    float dpf22 = AndroidUtilities.dpf2(3.0f) * f6;
                    float clamp = MathUtils.clamp((f5 * dpf2) - f6, 0.0f, 1.0f);
                    float[] fArr4 = this.heights;
                    addBar(this.path, dpf22, (AndroidUtilities.dpf2(fArr4[z ? (fArr4.length - 1) - i5 : i5]) * clamp) - (AndroidUtilities.dpf2(1.0f) * (1.0f - clamp)));
                    i5++;
                }
            }
            f = 0.0f;
        } else {
            int i6 = this.width;
            int i7 = this.fromWidth;
            float f7 = (i6 - i7) / (this.toWidth - i7);
            int max = Math.max(fArr3.length, fArr2.length);
            int min = Math.min(this.fromHeights.length, this.toHeights.length);
            float[] fArr5 = this.fromHeights;
            int length = fArr5.length;
            float[] fArr6 = this.toHeights;
            float[] fArr7 = length < fArr6.length ? fArr5 : fArr6;
            float[] fArr8 = fArr5.length < fArr6.length ? fArr6 : fArr5;
            if (fArr5.length >= fArr6.length) {
                f7 = 1.0f - f7;
            }
            int i8 = -1;
            f = 0.0f;
            int i9 = 0;
            while (i9 < max) {
                float f8 = i9;
                int i10 = max;
                int clamp2 = MathUtils.clamp((int) Math.floor((f8 / max) * min), 0, min - 1);
                if (i8 < clamp2) {
                    float lerp = AndroidUtilities.lerp(clamp2, f8, f7) * AndroidUtilities.dpf2(3.0f);
                    if (z) {
                        i3 = 1;
                        i4 = (fArr7.length - 1) - clamp2;
                    } else {
                        i3 = 1;
                        i4 = clamp2;
                    }
                    addBar(this.path, lerp, AndroidUtilities.dpf2(AndroidUtilities.lerp(fArr7[i4], fArr8[z ? (fArr8.length - i3) - i9 : i9], f7)));
                    i8 = clamp2;
                } else {
                    float lerp2 = AndroidUtilities.lerp(clamp2, f8, f7) * AndroidUtilities.dpf2(3.0f);
                    if (z) {
                        i2 = 1;
                        clamp2 = (fArr7.length - 1) - clamp2;
                    } else {
                        i2 = 1;
                    }
                    addBar(this.alphaPath, lerp2, AndroidUtilities.dpf2(AndroidUtilities.lerp(fArr7[clamp2], fArr8[z ? (fArr8.length - i2) - i9 : i9], f7)));
                    f = f7;
                }
                i9++;
                max = i10;
            }
        }
        if (this.exploding || this.explosionRate > 0.0f) {
            canvas.save();
            f2 = 0.0f;
            canvas.clipRect(0.0f, 0.0f, AndroidUtilities.dpf2(3.0f) * dpf2 * (1.0f - (this.explodeProgress * this.explosionRate)), this.height);
        } else {
            f2 = 0.0f;
        }
        if (f > f2) {
            canvas.save();
            canvas.clipPath(this.alphaPath);
            drawFill(canvas, f * this.alpha);
            canvas.restore();
        }
        canvas.save();
        canvas.clipPath(this.path);
        drawFill(canvas, this.alpha);
        canvas.restore();
        if (this.exploding || this.explosionRate > 0.0f) {
            canvas.restore();
            if (this.particles == null) {
                this.particles = new Particles(250, new Runnable() {
                    @Override
                    public final void run() {
                        SeekBarWaveform.this.invalidate();
                    }
                });
            }
            float f9 = this.explodeProgress;
            if (f9 < 0.99f && (fArr = this.heights) != null) {
                int i11 = (int) ((1.0f - f9) * dpf2);
                if (z) {
                    i11 = (int) ((dpf2 - 1.0f) - i11);
                }
                if (i11 >= 0 && i11 < fArr.length) {
                    float dpf23 = AndroidUtilities.dpf2(this.heights[i11]) * MathUtils.clamp((f5 * dpf2) - i11, 0.0f, 1.0f);
                    rectF = AndroidUtilities.rectTmp;
                    float dpf24 = dpf2 * (1.0f - this.explodeProgress) * AndroidUtilities.dpf2(3.0f);
                    float dpf25 = AndroidUtilities.dpf2(2.0f);
                    int dp = (this.height - AndroidUtilities.dp(14.0f)) / 2;
                    float f10 = dpf23 * this.waveScaling;
                    float f11 = dpf25 / 2.0f;
                    rectF.set((AndroidUtilities.dpf2(1.0f) + dpf24) - f11, AndroidUtilities.dp(7.0f) + dp + ((-f10) - f11), dpf24 + AndroidUtilities.dpf2(1.0f) + f11, dp + AndroidUtilities.dp(7.0f) + f10 + f11);
                    this.particles.setColor(this.outerColor).setEmitArea(rectF).draw(canvas, this.explosionRate);
                }
            }
            rectF = null;
            this.particles.setColor(this.outerColor).setEmitArea(rectF).draw(canvas, this.explosionRate);
        }
    }

    public void explodeAt(float f) {
        this.exploding = true;
        this.explodeProgress = f;
        invalidate();
    }

    public float getProgress() {
        return this.thumbX / this.width;
    }

    public void invalidate() {
        View view = this.parentView;
        if (view != null) {
            view.invalidate();
        }
    }

    public boolean isDragging() {
        return this.pressed;
    }

    public boolean isStartDraging() {
        return this.startDraging;
    }

    public boolean onTouch(int i, float f, float f2) {
        SeekBar.SeekBarDelegate seekBarDelegate;
        if (!this.delegate.isSeekBarDragAllowed()) {
            this.progress = 1.0f;
            return false;
        }
        if (i == 0) {
            if (0.0f <= f && f <= this.width && f2 >= 0.0f && f2 <= this.height) {
                this.startX = f;
                this.pressed = true;
                this.thumbDX = (int) (f - this.thumbX);
                this.startDraging = false;
                this.delegate.onSeekBarPressed();
                return true;
            }
        } else if (i == 1 || i == 3) {
            if (this.pressed) {
                if (i == 1 && (seekBarDelegate = this.delegate) != null) {
                    seekBarDelegate.onSeekBarDrag(this.thumbX / this.width);
                }
                this.pressed = false;
                this.delegate.onSeekBarReleased();
                return true;
            }
        } else if (i == 2 && this.pressed) {
            if (this.startDraging) {
                int i2 = (int) (f - this.thumbDX);
                this.thumbX = i2;
                if (i2 < 0) {
                    this.thumbX = 0;
                } else {
                    int i3 = this.width;
                    if (i2 > i3) {
                        this.thumbX = i3;
                    }
                }
                this.progress = this.thumbX / this.width;
            }
            float f3 = this.startX;
            if (f3 != -1.0f && Math.abs(f - f3) > AndroidUtilities.getPixelsInCM(0.2f, true)) {
                View view = this.parentView;
                if (view != null && view.getParent() != null) {
                    this.parentView.getParent().requestDisallowInterceptTouchEvent(true);
                }
                this.startDraging = true;
                this.startX = -1.0f;
            }
            return true;
        }
        return false;
    }

    public void setAlpha(float f) {
        this.alpha = f;
    }

    public void setColors(int i, int i2, int i3) {
        this.innerColor = i;
        this.outerColor = i2;
        this.selectedColor = i3;
    }

    public void setDelegate(SeekBar.SeekBarDelegate seekBarDelegate) {
        this.delegate = seekBarDelegate;
    }

    public void setExplosionRate(float f) {
        this.explosionRate = f;
        invalidate();
    }

    public void setLoading(boolean z) {
        if (!this.loading && z && this.loadingFloat.get() <= 0.0f) {
            this.loadingStart = SystemClock.elapsedRealtime();
        }
        this.loading = z;
        View view = this.parentView;
        if (view != null) {
            view.invalidate();
        }
    }

    public void setMessageObject(MessageObject messageObject) {
        MessageObject messageObject2;
        if (this.animatedValues != null && (messageObject2 = this.messageObject) != null && messageObject != null && messageObject2.getId() != messageObject.getId()) {
            this.animatedValues.clear();
        }
        this.messageObject = messageObject;
    }

    public void setParentView(View view) {
        this.parentView = view;
        this.loadingFloat.setParent(view);
        this.appearFloat.setParent(view);
    }

    public void setProgress(float f) {
        setProgress(f, false);
    }

    public void setProgress(float f, boolean z) {
        if (!this.delegate.isSeekBarDragAllowed()) {
            this.progress = 1.0f;
            return;
        }
        boolean z2 = this.isUnread;
        this.progress = z2 ? 1.0f : f;
        int i = z2 ? this.width : this.thumbX;
        if (z && i != 0 && f == 0.0f) {
            this.clearProgress = 0.0f;
        } else if (!z) {
            this.clearProgress = 1.0f;
        }
        int ceil = (int) Math.ceil(this.width * f);
        this.thumbX = ceil;
        if (ceil < 0) {
            this.thumbX = 0;
            return;
        }
        int i2 = this.width;
        if (ceil > i2) {
            this.thumbX = i2;
        }
    }

    public void setSelected(boolean z) {
        this.selected = z;
    }

    public void setSent() {
        this.appearFloat.set(0.0f, true);
        View view = this.parentView;
        if (view != null) {
            view.invalidate();
        }
    }

    public void setSize(int i, int i2) {
        setSize(i, i2, i, i);
    }

    public void setSize(int i, int i2, int i3, int i4) {
        this.width = i;
        this.height = i2;
        float[] fArr = this.heights;
        if (fArr == null || fArr.length != ((int) (i / AndroidUtilities.dpf2(3.0f)))) {
            this.heights = calculateHeights((int) (this.width / AndroidUtilities.dpf2(3.0f)));
        }
        if (i3 == i4 || (this.fromWidth == i3 && this.toWidth == i4)) {
            if (i3 == i4) {
                this.toHeights = null;
                this.fromHeights = null;
                return;
            }
            return;
        }
        this.fromWidth = i3;
        this.toWidth = i4;
        this.fromHeights = calculateHeights((int) (i3 / AndroidUtilities.dpf2(3.0f)));
        this.toHeights = calculateHeights((int) (this.toWidth / AndroidUtilities.dpf2(3.0f)));
    }

    public void setWaveScaling(float f) {
        this.waveScaling = f;
    }

    public void setWaveform(byte[] bArr) {
        this.waveformBytes = bArr;
        this.heights = calculateHeights((int) (this.width / AndroidUtilities.dpf2(3.0f)));
        if (this.delegate.isSeekBarDragAllowed()) {
            return;
        }
        this.progress = 1.0f;
    }
}
