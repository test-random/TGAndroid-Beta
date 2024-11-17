package org.telegram.ui.Components;

import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.ui.ActionBar.Theme;

public class SendingFileDrawable extends StatusDrawable {
    Paint currentPaint;
    private float progress;
    private boolean isChat = false;
    private long lastUpdateTime = 0;
    private boolean started = false;

    public SendingFileDrawable(boolean z) {
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
        float f = this.progress + (((float) j) / 500.0f);
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
        Paint paint = this.currentPaint;
        if (paint == null) {
            paint = Theme.chat_statusRecordPaint;
        }
        int i = 0;
        while (i < 3) {
            paint.setAlpha(i == 0 ? (int) (this.progress * 255.0f) : i == 2 ? (int) ((1.0f - this.progress) * 255.0f) : 255);
            float dp = (AndroidUtilities.dp(5.0f) * i) + (AndroidUtilities.dp(5.0f) * this.progress);
            float f = 8.0f;
            canvas.drawLine(dp, AndroidUtilities.dp(this.isChat ? 3.0f : 4.0f), dp + AndroidUtilities.dp(4.0f), AndroidUtilities.dp(this.isChat ? 7.0f : 8.0f), paint);
            float dp2 = AndroidUtilities.dp(this.isChat ? 11.0f : 12.0f);
            float dp3 = dp + AndroidUtilities.dp(4.0f);
            if (this.isChat) {
                f = 7.0f;
            }
            canvas.drawLine(dp, dp2, dp3, AndroidUtilities.dp(f), paint);
            i++;
        }
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
