package org.telegram.ui.Components;

import android.graphics.ColorFilter;
import android.graphics.CornerPathEffect;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.os.SystemClock;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.ui.ActionBar.Theme;

public class LoadingDrawable extends Drawable {
    private boolean appearByGradient;
    private LinearGradient appearGradient;
    private int appearGradientWidth;
    private Matrix appearMatrix;
    private Paint appearPaint;
    public Paint backgroundPaint;
    public Integer color1;
    public Integer color2;
    public int colorKey1;
    public int colorKey2;
    private LinearGradient disappearGradient;
    private int disappearGradientWidth;
    private Matrix disappearMatrix;
    private Paint disappearPaint;
    private long disappearStart;
    private LinearGradient gradient;
    private int gradientColor1;
    private int gradientColor2;
    private int gradientStrokeColor1;
    private int gradientStrokeColor2;
    private int gradientWidth;
    private float gradientWidthScale;
    private android.graphics.Rect lastBounds;
    private Matrix matrix;
    public Paint paint;
    private Path path;
    private float[] radii;
    private RectF rectF;
    public Theme.ResourcesProvider resourcesProvider;
    private float speed;
    private long start;
    public boolean stroke;
    public Integer strokeColor1;
    public Integer strokeColor2;
    private LinearGradient strokeGradient;
    private Matrix strokeMatrix;
    public Paint strokePaint;
    private Path usePath;

    public LoadingDrawable() {
        this.start = -1L;
        this.disappearStart = -1L;
        this.matrix = new Matrix();
        this.strokeMatrix = new Matrix();
        this.colorKey1 = Theme.key_dialogBackground;
        this.colorKey2 = Theme.key_dialogBackgroundGray;
        this.gradientWidthScale = 1.0f;
        this.speed = 1.0f;
        this.paint = new Paint(1);
        this.strokePaint = new Paint(1);
        this.path = new Path();
        this.radii = new float[8];
        this.rectF = new RectF();
        this.strokePaint.setStyle(Paint.Style.STROKE);
        this.strokePaint.setStrokeWidth(AndroidUtilities.density > 2.0f ? 2.0f : 1.0f);
    }

    public LoadingDrawable(Theme.ResourcesProvider resourcesProvider) {
        this();
        this.resourcesProvider = resourcesProvider;
    }

    public void disappear() {
        if (isDisappeared() || isDisappearing()) {
            return;
        }
        this.disappearStart = SystemClock.elapsedRealtime();
    }

    @Override
    public void draw(android.graphics.Canvas r29) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.LoadingDrawable.draw(android.graphics.Canvas):void");
    }

    @Override
    public int getOpacity() {
        return -2;
    }

    public int getPaintAlpha() {
        return this.paint.getAlpha();
    }

    public boolean isDisappeared() {
        return this.disappearStart > 0 && ((float) (SystemClock.elapsedRealtime() - this.disappearStart)) >= 320.0f;
    }

    public boolean isDisappearing() {
        return this.disappearStart > 0 && ((float) (SystemClock.elapsedRealtime() - this.disappearStart)) < 320.0f;
    }

    public void reset() {
        this.start = -1L;
    }

    public void resetDisappear() {
        this.disappearStart = -1L;
    }

    @Override
    public void setAlpha(int i) {
        this.paint.setAlpha(i);
        this.strokePaint.setAlpha(i);
        if (i > 0) {
            invalidateSelf();
        }
    }

    public void setAppearByGradient(boolean z) {
        this.appearByGradient = z;
    }

    public void setBounds(RectF rectF) {
        super.setBounds((int) rectF.left, (int) rectF.top, (int) rectF.right, (int) rectF.bottom);
        this.lastBounds = null;
    }

    @Override
    public void setColorFilter(ColorFilter colorFilter) {
        this.paint.setColorFilter(colorFilter);
    }

    public void setColors(int i, int i2) {
        this.color1 = Integer.valueOf(i);
        this.color2 = Integer.valueOf(i2);
        this.stroke = false;
    }

    public void setColors(int i, int i2, int i3, int i4) {
        this.color1 = Integer.valueOf(i);
        this.color2 = Integer.valueOf(i2);
        this.stroke = true;
        this.strokeColor1 = Integer.valueOf(i3);
        this.strokeColor2 = Integer.valueOf(i4);
    }

    public void setGradientScale(float f) {
        this.gradientWidthScale = f;
    }

    public void setRadii(float f, float f2, float f3, float f4) {
        float[] fArr = this.radii;
        boolean z = (fArr[0] == f && fArr[2] == f2 && fArr[4] == f3 && fArr[6] == f4) ? false : true;
        fArr[1] = f;
        fArr[0] = f;
        fArr[3] = f2;
        fArr[2] = f2;
        fArr[5] = f3;
        fArr[4] = f3;
        fArr[7] = f4;
        fArr[6] = f4;
        if (this.lastBounds == null || !z) {
            return;
        }
        this.path.rewind();
        this.rectF.set(this.lastBounds);
        this.path.addRoundRect(this.rectF, this.radii, Path.Direction.CW);
    }

    public void setRadii(float[] fArr) {
        if (fArr == null || fArr.length != 8) {
            return;
        }
        boolean z = false;
        for (int i = 0; i < 8; i++) {
            float[] fArr2 = this.radii;
            float f = fArr2[i];
            float f2 = fArr[i];
            if (f != f2) {
                fArr2[i] = f2;
                z = true;
            }
        }
        if (this.lastBounds == null || !z) {
            return;
        }
        this.path.rewind();
        this.rectF.set(this.lastBounds);
        this.path.addRoundRect(this.rectF, fArr, Path.Direction.CW);
    }

    public void setRadiiDp(float f) {
        if (this.usePath == null) {
            setRadiiDp(f, f, f, f);
        } else {
            this.paint.setPathEffect(new CornerPathEffect(AndroidUtilities.dp(f)));
            this.strokePaint.setPathEffect(new CornerPathEffect(AndroidUtilities.dp(f)));
        }
    }

    public void setRadiiDp(float f, float f2, float f3, float f4) {
        setRadii(AndroidUtilities.dp(f), AndroidUtilities.dp(f2), AndroidUtilities.dp(f3), AndroidUtilities.dp(f4));
    }

    public void setSpeed(float f) {
        this.speed = f;
    }

    public long timeToDisappear() {
        if (this.disappearStart > 0) {
            return 320 - (SystemClock.elapsedRealtime() - this.disappearStart);
        }
        return 0L;
    }

    public void updateBounds() {
        Path path = this.usePath;
        if (path != null) {
            RectF rectF = AndroidUtilities.rectTmp;
            path.computeBounds(rectF, false);
            setBounds(rectF);
        }
    }

    public void usePath(Path path) {
        this.usePath = path;
    }
}
