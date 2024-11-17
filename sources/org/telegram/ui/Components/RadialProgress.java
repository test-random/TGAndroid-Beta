package org.telegram.ui.Components;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.ui.ActionBar.Theme;

public class RadialProgress {
    private static DecelerateInterpolator decelerateInterpolator;
    private Drawable checkBackgroundDrawable;
    private Drawable currentDrawable;
    private Drawable currentMiniDrawable;
    private boolean currentMiniWithRound;
    private boolean currentWithRound;
    private boolean disableUpdate;
    private boolean drawMiniProgress;
    private boolean hideCurrentDrawable;
    private Bitmap miniDrawBitmap;
    private Canvas miniDrawCanvas;
    private Paint miniProgressBackgroundPaint;
    private Paint miniProgressPaint;
    private View parent;
    private Drawable previousDrawable;
    private Drawable previousMiniDrawable;
    private boolean previousMiniWithRound;
    private boolean previousWithRound;
    private Paint progressPaint;
    private boolean roundRectProgress;
    private long lastUpdateTime = 0;
    private float radOffset = 0.0f;
    private float currentProgress = 0.0f;
    private float animationProgressStart = 0.0f;
    private long currentProgressTime = 0;
    private float animatedProgressValue = 0.0f;
    private RectF progressRect = new RectF();
    private RectF cicleRect = new RectF();
    private float animatedAlphaValue = 1.0f;
    private int progressColor = -1;
    private int diff = AndroidUtilities.dp(4.0f);
    private boolean alphaForPrevious = true;
    private boolean alphaForMiniPrevious = true;
    private float overrideAlpha = 1.0f;
    private Paint overridePaint = null;
    private final Path roundProgressRectPath = new Path();
    private final Matrix roundProgressRectMatrix = new Matrix();
    private final PathMeasure roundProgressRectPathMeasure = new PathMeasure();
    private final Path roundRectProgressPath = new Path();

    public RadialProgress(View view) {
        if (decelerateInterpolator == null) {
            decelerateInterpolator = new DecelerateInterpolator();
        }
        Paint paint = new Paint(1);
        this.progressPaint = paint;
        Paint.Style style = Paint.Style.STROKE;
        paint.setStyle(style);
        Paint paint2 = this.progressPaint;
        Paint.Cap cap = Paint.Cap.ROUND;
        paint2.setStrokeCap(cap);
        this.progressPaint.setStrokeWidth(AndroidUtilities.dp(3.0f));
        Paint paint3 = new Paint(1);
        this.miniProgressPaint = paint3;
        paint3.setStyle(style);
        this.miniProgressPaint.setStrokeCap(cap);
        this.miniProgressPaint.setStrokeWidth(AndroidUtilities.dp(2.0f));
        this.miniProgressBackgroundPaint = new Paint(1);
        this.parent = view;
    }

    private void drawArc(Canvas canvas, RectF rectF, float f, float f2, boolean z, Paint paint) {
        if (!this.roundRectProgress) {
            canvas.drawArc(rectF, f, f2, z, paint);
            return;
        }
        float height = rectF.height() * 0.32f;
        if (Math.abs(f2) == 360.0f) {
            canvas.drawRoundRect(rectF, height, height, paint);
            return;
        }
        float f3 = ((((int) f) / 90) * 90) + 90;
        float f4 = (-199.0f) + f3;
        float f5 = ((f + f2) - f4) / 360.0f;
        this.roundProgressRectPath.rewind();
        this.roundProgressRectPath.addRoundRect(rectF, height, height, Path.Direction.CW);
        this.roundProgressRectMatrix.reset();
        this.roundProgressRectMatrix.postRotate(f3, rectF.centerX(), rectF.centerY());
        this.roundProgressRectPath.transform(this.roundProgressRectMatrix);
        this.roundProgressRectPathMeasure.setPath(this.roundProgressRectPath, false);
        float length = this.roundProgressRectPathMeasure.getLength();
        this.roundRectProgressPath.reset();
        this.roundProgressRectPathMeasure.getSegment(((f - f4) / 360.0f) * length, length * f5, this.roundRectProgressPath, true);
        this.roundRectProgressPath.rLineTo(0.0f, 0.0f);
        canvas.drawPath(this.roundRectProgressPath, paint);
        if (f5 > 1.0f) {
            drawArc(canvas, rectF, f + 90.0f, f2 - 90.0f, z, paint);
        }
    }

    private void invalidateParent() {
        int dp = AndroidUtilities.dp(2.0f);
        View view = this.parent;
        RectF rectF = this.progressRect;
        int i = ((int) rectF.left) - dp;
        int i2 = ((int) rectF.top) - dp;
        int i3 = dp * 2;
        view.invalidate(i, i2, ((int) rectF.right) + i3, ((int) rectF.bottom) + i3);
    }

    private void updateAnimation(boolean r18) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.RadialProgress.updateAnimation(boolean):void");
    }

    public void copyParams(RadialProgress radialProgress) {
        this.currentProgress = radialProgress.currentProgress;
        this.animatedProgressValue = radialProgress.animatedProgressValue;
        this.radOffset = radialProgress.radOffset;
        this.lastUpdateTime = System.currentTimeMillis();
        invalidateParent();
    }

    public void draw(Canvas canvas) {
        Paint paint;
        float f;
        Drawable drawable;
        boolean z;
        float centerX;
        float centerY;
        int i;
        int i2;
        Paint paint2;
        int i3;
        Paint paint3;
        float f2;
        Drawable drawable2;
        if (!this.drawMiniProgress || this.currentDrawable == null) {
            Drawable drawable3 = this.previousDrawable;
            if (drawable3 != null) {
                drawable3.setAlpha((int) (this.alphaForPrevious ? this.animatedAlphaValue * 255.0f * this.overrideAlpha : this.overrideAlpha * 255.0f));
                Drawable drawable4 = this.previousDrawable;
                RectF rectF = this.progressRect;
                drawable4.setBounds((int) rectF.left, (int) rectF.top, (int) rectF.right, (int) rectF.bottom);
                this.previousDrawable.draw(canvas);
            }
            if (!this.hideCurrentDrawable && (drawable = this.currentDrawable) != null) {
                drawable.setAlpha(this.previousDrawable != null ? (int) ((1.0f - this.animatedAlphaValue) * 255.0f * this.overrideAlpha) : (int) (this.overrideAlpha * 255.0f));
                Drawable drawable5 = this.currentDrawable;
                RectF rectF2 = this.progressRect;
                drawable5.setBounds((int) rectF2.left, (int) rectF2.top, (int) rectF2.right, (int) rectF2.bottom);
                this.currentDrawable.draw(canvas);
            }
            if (this.currentWithRound || this.previousWithRound) {
                Paint paint4 = this.overridePaint;
                if (paint4 == null) {
                    this.progressPaint.setColor(this.progressColor);
                    if (this.previousWithRound) {
                        paint = this.progressPaint;
                        f = this.animatedAlphaValue * 255.0f * this.overrideAlpha;
                    } else {
                        paint = this.progressPaint;
                        f = this.overrideAlpha * 255.0f;
                    }
                    paint.setAlpha((int) f);
                    paint4 = this.progressPaint;
                }
                Paint paint5 = paint4;
                RectF rectF3 = this.cicleRect;
                RectF rectF4 = this.progressRect;
                float f3 = rectF4.left;
                float f4 = this.diff;
                rectF3.set(f3 + f4, rectF4.top + f4, rectF4.right - f4, rectF4.bottom - f4);
                drawArc(canvas, this.cicleRect, (-90.0f) + this.radOffset, Math.max(4.0f, this.animatedProgressValue * 360.0f), false, paint5);
                z = true;
            }
            z = false;
        } else {
            if (this.miniDrawCanvas != null) {
                this.miniDrawBitmap.eraseColor(0);
            }
            this.currentDrawable.setAlpha((int) (this.overrideAlpha * 255.0f));
            if (this.miniDrawCanvas != null) {
                this.currentDrawable.setBounds(0, 0, (int) this.progressRect.width(), (int) this.progressRect.height());
                this.currentDrawable.draw(this.miniDrawCanvas);
            } else {
                Drawable drawable6 = this.currentDrawable;
                RectF rectF5 = this.progressRect;
                drawable6.setBounds((int) rectF5.left, (int) rectF5.top, (int) rectF5.right, (int) rectF5.bottom);
                this.currentDrawable.draw(canvas);
            }
            if (Math.abs(this.progressRect.width() - AndroidUtilities.dp(44.0f)) < AndroidUtilities.density) {
                float f5 = 16;
                centerX = this.progressRect.centerX() + AndroidUtilities.dp(f5);
                centerY = this.progressRect.centerY() + AndroidUtilities.dp(f5);
                i = 20;
                i2 = 0;
            } else {
                centerX = this.progressRect.centerX() + AndroidUtilities.dp(18.0f);
                centerY = this.progressRect.centerY() + AndroidUtilities.dp(18.0f);
                i = 22;
                i2 = 2;
            }
            int i4 = i / 2;
            float f6 = (this.previousMiniDrawable == null || !this.alphaForMiniPrevious) ? 1.0f : this.animatedAlphaValue * this.overrideAlpha;
            Canvas canvas2 = this.miniDrawCanvas;
            if (canvas2 != null) {
                float f7 = i + 18 + i2;
                canvas2.drawCircle(AndroidUtilities.dp(f7), AndroidUtilities.dp(f7), AndroidUtilities.dp(i4 + 1) * f6, Theme.checkboxSquare_eraserPaint);
            } else {
                this.miniProgressBackgroundPaint.setColor(this.progressColor);
                if (this.previousMiniDrawable == null || this.currentMiniDrawable != null) {
                    paint2 = this.miniProgressBackgroundPaint;
                    i3 = 255;
                } else {
                    paint2 = this.miniProgressBackgroundPaint;
                    i3 = (int) (this.animatedAlphaValue * 255.0f * this.overrideAlpha);
                }
                paint2.setAlpha(i3);
                canvas.drawCircle(centerX, centerY, AndroidUtilities.dp(12.0f), this.miniProgressBackgroundPaint);
            }
            if (this.miniDrawCanvas != null) {
                Bitmap bitmap = this.miniDrawBitmap;
                RectF rectF6 = this.progressRect;
                canvas.drawBitmap(bitmap, (int) rectF6.left, (int) rectF6.top, (Paint) null);
            }
            Drawable drawable7 = this.previousMiniDrawable;
            if (drawable7 != null) {
                drawable7.setAlpha((int) (this.alphaForMiniPrevious ? this.animatedAlphaValue * 255.0f * this.overrideAlpha : this.overrideAlpha * 255.0f));
                float f8 = i4;
                this.previousMiniDrawable.setBounds((int) (centerX - (AndroidUtilities.dp(f8) * f6)), (int) (centerY - (AndroidUtilities.dp(f8) * f6)), (int) ((AndroidUtilities.dp(f8) * f6) + centerX), (int) ((AndroidUtilities.dp(f8) * f6) + centerY));
                this.previousMiniDrawable.draw(canvas);
            }
            if (!this.hideCurrentDrawable && (drawable2 = this.currentMiniDrawable) != null) {
                drawable2.setAlpha((int) (this.previousMiniDrawable != null ? (1.0f - this.animatedAlphaValue) * 255.0f * this.overrideAlpha : this.overrideAlpha * 255.0f));
                float f9 = i4;
                this.currentMiniDrawable.setBounds((int) (centerX - AndroidUtilities.dp(f9)), (int) (centerY - AndroidUtilities.dp(f9)), (int) (AndroidUtilities.dp(f9) + centerX), (int) (AndroidUtilities.dp(f9) + centerY));
                this.currentMiniDrawable.draw(canvas);
            }
            if (this.currentMiniWithRound || this.previousMiniWithRound) {
                this.miniProgressPaint.setColor(this.progressColor);
                if (this.previousMiniWithRound) {
                    paint3 = this.miniProgressPaint;
                    f2 = this.animatedAlphaValue * 255.0f * this.overrideAlpha;
                } else {
                    paint3 = this.miniProgressPaint;
                    f2 = this.overrideAlpha * 255.0f;
                }
                paint3.setAlpha((int) f2);
                float f10 = i4 - 2;
                this.cicleRect.set(centerX - (AndroidUtilities.dp(f10) * f6), centerY - (AndroidUtilities.dp(f10) * f6), centerX + (AndroidUtilities.dp(f10) * f6), centerY + (AndroidUtilities.dp(f10) * f6));
                canvas.drawArc(this.cicleRect, (-90.0f) + this.radOffset, Math.max(4.0f, this.animatedProgressValue * 360.0f), false, this.miniProgressPaint);
                z = true;
            }
            z = false;
        }
        updateAnimation(z);
    }

    public float getAnimatedProgress() {
        return this.animatedProgressValue;
    }

    public void setBackground(Drawable drawable, boolean z, boolean z2) {
        Drawable drawable2;
        this.lastUpdateTime = System.currentTimeMillis();
        if (!z2 || (drawable2 = this.currentDrawable) == drawable) {
            this.previousDrawable = null;
            this.previousWithRound = false;
        } else {
            this.previousDrawable = drawable2;
            this.previousWithRound = this.currentWithRound;
            this.animatedAlphaValue = 1.0f;
            setProgress(1.0f, z2);
        }
        this.currentWithRound = z;
        this.currentDrawable = drawable;
        if (z2) {
            invalidateParent();
        } else {
            this.parent.invalidate();
        }
    }

    public void setDiff(int i) {
        this.diff = i;
    }

    public void setPaint(Paint paint) {
        this.overridePaint = paint;
    }

    public void setProgress(float f, boolean z) {
        if (this.drawMiniProgress) {
            if (f != 1.0f && this.animatedAlphaValue != 0.0f && this.previousMiniDrawable != null) {
                this.animatedAlphaValue = 0.0f;
                this.previousMiniDrawable = null;
                this.drawMiniProgress = this.currentMiniDrawable != null;
            }
        } else if (f != 1.0f && this.animatedAlphaValue != 0.0f && this.previousDrawable != null) {
            this.animatedAlphaValue = 0.0f;
            this.previousDrawable = null;
        }
        if (z) {
            if (this.animatedProgressValue > f) {
                this.animatedProgressValue = f;
            }
            this.animationProgressStart = this.animatedProgressValue;
        } else {
            this.animatedProgressValue = f;
            this.animationProgressStart = f;
        }
        this.currentProgress = f;
        this.currentProgressTime = 0L;
        invalidateParent();
    }

    public void setProgressColor(int i) {
        this.progressColor = i;
    }

    public void setProgressRect(int i, int i2, int i3, int i4) {
        this.progressRect.set(i, i2, i3, i4);
    }

    public void setRoundRectProgress(boolean z) {
        this.roundRectProgress = z;
    }
}
