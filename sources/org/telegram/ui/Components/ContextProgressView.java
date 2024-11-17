package org.telegram.ui.Components;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.view.View;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.ui.ActionBar.Theme;

public class ContextProgressView extends View {
    private RectF cicleRect;
    private int innerColor;
    private int innerKey;
    private Paint innerPaint;
    private long lastUpdateTime;
    private int outerColor;
    private int outerKey;
    private Paint outerPaint;
    private int radOffset;

    public ContextProgressView(Context context, int i) {
        super(context);
        int i2;
        this.innerPaint = new Paint(1);
        this.outerPaint = new Paint(1);
        this.cicleRect = new RectF();
        this.radOffset = 0;
        Paint paint = this.innerPaint;
        Paint.Style style = Paint.Style.STROKE;
        paint.setStyle(style);
        this.innerPaint.setStrokeWidth(AndroidUtilities.dp(2.0f));
        this.outerPaint.setStyle(style);
        this.outerPaint.setStrokeWidth(AndroidUtilities.dp(2.0f));
        this.outerPaint.setStrokeCap(Paint.Cap.ROUND);
        if (i == 0) {
            this.innerKey = Theme.key_contextProgressInner1;
            i2 = Theme.key_contextProgressOuter1;
        } else if (i == 1) {
            this.innerKey = Theme.key_contextProgressInner2;
            i2 = Theme.key_contextProgressOuter2;
        } else {
            if (i != 2) {
                if (i == 3) {
                    this.innerKey = Theme.key_contextProgressInner4;
                    i2 = Theme.key_contextProgressOuter4;
                }
                updateColors();
            }
            this.innerKey = Theme.key_contextProgressInner3;
            i2 = Theme.key_contextProgressOuter3;
        }
        this.outerKey = i2;
        updateColors();
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        this.lastUpdateTime = System.currentTimeMillis();
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (getVisibility() != 0) {
            return;
        }
        long currentTimeMillis = System.currentTimeMillis();
        long j = currentTimeMillis - this.lastUpdateTime;
        this.lastUpdateTime = currentTimeMillis;
        this.radOffset = (int) (this.radOffset + (((float) (j * 360)) / 1000.0f));
        this.cicleRect.set((getMeasuredWidth() / 2) - AndroidUtilities.dp(9.0f), (getMeasuredHeight() / 2) - AndroidUtilities.dp(9.0f), r0 + AndroidUtilities.dp(18.0f), r2 + AndroidUtilities.dp(18.0f));
        canvas.drawCircle(getMeasuredWidth() / 2, getMeasuredHeight() / 2, AndroidUtilities.dp(9.0f), this.innerPaint);
        canvas.drawArc(this.cicleRect, this.radOffset - 90, 90.0f, false, this.outerPaint);
        invalidate();
    }

    public void setColors(int i, int i2) {
        this.innerKey = -1;
        this.outerKey = -1;
        this.innerColor = i;
        this.outerColor = i2;
        updateColors();
    }

    @Override
    public void setVisibility(int i) {
        super.setVisibility(i);
        this.lastUpdateTime = System.currentTimeMillis();
        invalidate();
    }

    public void updateColors() {
        int i = this.innerKey;
        if (i >= 0) {
            this.innerPaint.setColor(Theme.getColor(i));
        } else {
            this.innerPaint.setColor(this.innerColor);
        }
        int i2 = this.outerKey;
        if (i2 >= 0) {
            this.outerPaint.setColor(Theme.getColor(i2));
        } else {
            this.outerPaint.setColor(this.outerColor);
        }
        invalidate();
    }
}
