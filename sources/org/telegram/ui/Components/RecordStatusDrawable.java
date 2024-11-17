package org.telegram.ui.Components;

import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.RectF;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.ui.ActionBar.Theme;

public class RecordStatusDrawable extends StatusDrawable {
    Paint currentPaint;
    private float progress;
    private boolean isChat = false;
    private long lastUpdateTime = 0;
    private boolean started = false;
    private RectF rect = new RectF();
    int alpha = 255;

    public RecordStatusDrawable(boolean z) {
        if (z) {
            Paint paint = new Paint(1);
            this.currentPaint = paint;
            paint.setStyle(Paint.Style.STROKE);
            this.currentPaint.setStrokeCap(Paint.Cap.ROUND);
            this.currentPaint.setStrokeWidth(AndroidUtilities.dp(2.0f));
        }
    }

    private void update() {
        long currentTimeMillis = System.currentTimeMillis();
        long j = currentTimeMillis - this.lastUpdateTime;
        this.lastUpdateTime = currentTimeMillis;
        if (j > 50) {
            j = 50;
        }
        float f = this.progress + (((float) j) / 800.0f);
        while (true) {
            this.progress = f;
            float f2 = this.progress;
            if (f2 <= 1.0f) {
                invalidateSelf();
                return;
            }
            f = f2 - 1.0f;
        }
    }

    @Override
    public void draw(Canvas canvas) {
        int i;
        float f;
        float f2;
        Paint paint = this.currentPaint;
        if (paint == null) {
            paint = Theme.chat_statusRecordPaint;
        }
        if (paint.getStrokeWidth() != AndroidUtilities.dp(2.0f)) {
            paint.setStrokeWidth(AndroidUtilities.dp(2.0f));
        }
        canvas.save();
        canvas.translate(0.0f, (getIntrinsicHeight() / 2) + AndroidUtilities.dp(this.isChat ? 1.0f : 2.0f));
        for (int i2 = 0; i2 < 4; i2++) {
            if (i2 == 0) {
                f = this.alpha;
                f2 = this.progress;
            } else if (i2 == 3) {
                f = this.alpha;
                f2 = 1.0f - this.progress;
            } else {
                i = this.alpha;
                paint.setAlpha(i);
                float dp = (AndroidUtilities.dp(4.0f) * i2) + (AndroidUtilities.dp(4.0f) * this.progress);
                float f3 = -dp;
                this.rect.set(f3, f3, dp, dp);
                canvas.drawArc(this.rect, -15.0f, 30.0f, false, paint);
            }
            i = (int) (f * f2);
            paint.setAlpha(i);
            float dp2 = (AndroidUtilities.dp(4.0f) * i2) + (AndroidUtilities.dp(4.0f) * this.progress);
            float f32 = -dp2;
            this.rect.set(f32, f32, dp2, dp2);
            canvas.drawArc(this.rect, -15.0f, 30.0f, false, paint);
        }
        canvas.restore();
        if (this.started) {
            update();
        }
    }

    @Override
    public int getIntrinsicHeight() {
        return AndroidUtilities.dp(14.0f);
    }

    @Override
    public int getIntrinsicWidth() {
        return AndroidUtilities.dp(18.0f);
    }

    @Override
    public int getOpacity() {
        return 0;
    }

    @Override
    public void setAlpha(int i) {
        this.alpha = i;
    }

    @Override
    public void setColor(int i) {
        Paint paint = this.currentPaint;
        if (paint != null) {
            paint.setColor(i);
        }
    }

    @Override
    public void setColorFilter(ColorFilter colorFilter) {
    }

    @Override
    public void setIsChat(boolean z) {
        this.isChat = z;
    }

    @Override
    public void start() {
        this.lastUpdateTime = System.currentTimeMillis();
        this.started = true;
        invalidateSelf();
    }

    @Override
    public void stop() {
        this.started = false;
    }
}
