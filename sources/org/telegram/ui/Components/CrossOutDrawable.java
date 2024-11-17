package org.telegram.ui.Components;

import android.content.Context;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import androidx.core.content.ContextCompat;
import org.telegram.messenger.AndroidUtilities;

public class CrossOutDrawable extends Drawable {
    int color;
    int colorKey;
    boolean cross;
    Drawable iconDrawable;
    private float lenOffsetBottom;
    private float lenOffsetTop;
    float progress;
    private float xOffset;
    final Paint xRefPaint;
    RectF rectF = new RectF();
    Paint paint = new Paint(1);

    public CrossOutDrawable(Context context, int i, int i2) {
        Paint paint = new Paint(1);
        this.xRefPaint = paint;
        this.iconDrawable = ContextCompat.getDrawable(context, i);
        this.colorKey = i2;
        Paint paint2 = this.paint;
        Paint.Style style = Paint.Style.STROKE;
        paint2.setStyle(style);
        this.paint.setStrokeWidth(AndroidUtilities.dpf2(1.7f));
        this.paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setColor(-16777216);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        paint.setStyle(style);
        paint.setStrokeWidth(AndroidUtilities.dpf2(2.5f));
    }

    @Override
    public void draw(android.graphics.Canvas r13) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.CrossOutDrawable.draw(android.graphics.Canvas):void");
    }

    @Override
    public int getIntrinsicHeight() {
        return this.iconDrawable.getIntrinsicHeight();
    }

    @Override
    public int getIntrinsicWidth() {
        return this.iconDrawable.getIntrinsicWidth();
    }

    @Override
    public int getOpacity() {
        return -2;
    }

    public float getProgress() {
        return this.progress;
    }

    @Override
    public void setAlpha(int i) {
    }

    @Override
    public void setBounds(int i, int i2, int i3, int i4) {
        super.setBounds(i, i2, i3, i4);
        this.iconDrawable.setBounds(i, i2, i3, i4);
    }

    @Override
    public void setColorFilter(ColorFilter colorFilter) {
    }

    public void setCrossOut(boolean z, boolean z2) {
        if (this.cross != z) {
            this.cross = z;
            this.progress = (z2 ? !z : z) ? 1.0f : 0.0f;
            invalidateSelf();
        }
    }

    public void setOffsets(float f, float f2, float f3) {
        this.xOffset = f;
        this.lenOffsetTop = f2;
        this.lenOffsetBottom = f3;
        invalidateSelf();
    }

    public void setStrokeWidth(float f) {
        this.paint.setStrokeWidth(f);
        this.xRefPaint.setStrokeWidth(f * 1.47f);
    }
}
