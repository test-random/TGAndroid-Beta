package org.telegram.ui.Components;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.os.SystemClock;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.R;
import org.telegram.ui.ActionBar.Theme;

public class ProxyDrawable extends Drawable {
    private boolean connected;
    private float connectedAnimationProgress;
    private Drawable emptyDrawable;
    private Drawable fullDrawable;
    private boolean isEnabled;
    private long lastUpdateTime;
    private Paint outerPaint = new Paint(1);
    private RectF cicleRect = new RectF();
    private int radOffset = 0;
    private int colorKey = -1;

    public ProxyDrawable(Context context) {
        this.emptyDrawable = context.getResources().getDrawable(R.drawable.msg2_proxy_off).mutate();
        this.fullDrawable = context.getResources().getDrawable(R.drawable.msg2_proxy_on).mutate();
        this.outerPaint.setStyle(Paint.Style.STROKE);
        this.outerPaint.setStrokeWidth(AndroidUtilities.dp(1.66f));
        this.outerPaint.setStrokeCap(Paint.Cap.ROUND);
        this.lastUpdateTime = SystemClock.elapsedRealtime();
    }

    private void setBounds(Drawable drawable) {
        android.graphics.Rect bounds = getBounds();
        drawable.setBounds(bounds.centerX() - (drawable.getIntrinsicWidth() / 2), bounds.centerY() - (drawable.getIntrinsicHeight() / 2), bounds.centerX() + (drawable.getIntrinsicWidth() / 2), bounds.centerY() + (drawable.getIntrinsicHeight() / 2));
    }

    @Override
    public void draw(Canvas canvas) {
        long elapsedRealtime = SystemClock.elapsedRealtime();
        long j = elapsedRealtime - this.lastUpdateTime;
        this.lastUpdateTime = elapsedRealtime;
        if (!this.isEnabled) {
            setBounds(this.emptyDrawable);
            this.emptyDrawable.draw(canvas);
        } else if (!this.connected || this.connectedAnimationProgress != 1.0f) {
            setBounds(this.emptyDrawable);
            this.emptyDrawable.draw(canvas);
            Paint paint = this.outerPaint;
            int i = this.colorKey;
            if (i < 0) {
                i = Theme.key_contextProgressOuter2;
            }
            paint.setColor(Theme.getColor(i));
            this.outerPaint.setAlpha((int) ((1.0f - this.connectedAnimationProgress) * 255.0f));
            this.radOffset = (int) (this.radOffset + (((float) (360 * j)) / 1000.0f));
            int width = getBounds().width();
            int height = getBounds().height();
            int dp = AndroidUtilities.dp(4.0f);
            this.cicleRect.set((width / 2) - dp, (height / 2) - dp, r0 + dp + dp, r1 + dp + dp);
            canvas.drawArc(this.cicleRect, this.radOffset - 90, 90.0f, false, this.outerPaint);
            invalidateSelf();
        }
        if (this.isEnabled && (this.connected || this.connectedAnimationProgress != 0.0f)) {
            this.fullDrawable.setAlpha((int) (this.connectedAnimationProgress * 255.0f));
            setBounds(this.fullDrawable);
            this.fullDrawable.draw(canvas);
        }
        boolean z = this.connected;
        if (z) {
            float f = this.connectedAnimationProgress;
            if (f != 1.0f) {
                float f2 = f + (((float) j) / 300.0f);
                this.connectedAnimationProgress = f2;
                if (f2 > 1.0f) {
                    this.connectedAnimationProgress = 1.0f;
                }
                invalidateSelf();
            }
        }
        if (z) {
            return;
        }
        float f3 = this.connectedAnimationProgress;
        if (f3 != 0.0f) {
            float f4 = f3 - (((float) j) / 300.0f);
            this.connectedAnimationProgress = f4;
            if (f4 < 0.0f) {
                this.connectedAnimationProgress = 0.0f;
            }
            invalidateSelf();
        }
    }

    @Override
    public int getIntrinsicHeight() {
        return AndroidUtilities.dp(24.0f);
    }

    @Override
    public int getIntrinsicWidth() {
        return AndroidUtilities.dp(24.0f);
    }

    @Override
    public int getOpacity() {
        return -2;
    }

    @Override
    public void setAlpha(int i) {
    }

    @Override
    public void setColorFilter(ColorFilter colorFilter) {
        this.emptyDrawable.setColorFilter(colorFilter);
        this.fullDrawable.setColorFilter(colorFilter);
    }

    public void setColorKey(int i) {
        this.colorKey = i;
    }

    public void setConnected(boolean z, boolean z2, boolean z3) {
        this.isEnabled = z;
        this.connected = z2;
        this.lastUpdateTime = SystemClock.elapsedRealtime();
        if (!z3) {
            this.connectedAnimationProgress = this.connected ? 1.0f : 0.0f;
        }
        invalidateSelf();
    }
}
