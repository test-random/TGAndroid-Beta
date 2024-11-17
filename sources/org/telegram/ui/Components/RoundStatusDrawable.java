package org.telegram.ui.Components;

import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.ui.ActionBar.Theme;

public class RoundStatusDrawable extends StatusDrawable {
    private Paint currentPaint;
    private float progress;
    private boolean isChat = false;
    private long lastUpdateTime = 0;
    private boolean started = false;
    private int progressDirection = 1;

    public RoundStatusDrawable(boolean z) {
        if (z) {
            this.currentPaint = new Paint(1);
        }
    }

    private void update() {
        long currentTimeMillis = System.currentTimeMillis();
        long j = currentTimeMillis - this.lastUpdateTime;
        this.lastUpdateTime = currentTimeMillis;
        if (j > 50) {
            j = 50;
        }
        float f = this.progress;
        int i = this.progressDirection;
        float f2 = f + (((float) (i * j)) / 400.0f);
        this.progress = f2;
        if (i > 0 && f2 >= 1.0f) {
            this.progressDirection = -1;
            this.progress = 1.0f;
        } else if (i < 0 && f2 <= 0.0f) {
            this.progressDirection = 1;
            this.progress = 0.0f;
        }
        invalidateSelf();
    }

    @Override
    public void draw(Canvas canvas) {
        Paint paint = this.currentPaint;
        if (paint == null) {
            paint = Theme.chat_statusPaint;
        }
        paint.setAlpha(((int) (this.progress * 200.0f)) + 55);
        canvas.drawCircle(AndroidUtilities.dp(6.0f), AndroidUtilities.dp(this.isChat ? 8.0f : 9.0f), AndroidUtilities.dp(4.0f), paint);
        if (this.started) {
            update();
        }
    }

    @Override
    public int getIntrinsicHeight() {
        return AndroidUtilities.dp(10.0f);
    }

    @Override
    public int getIntrinsicWidth() {
        return AndroidUtilities.dp(12.0f);
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
