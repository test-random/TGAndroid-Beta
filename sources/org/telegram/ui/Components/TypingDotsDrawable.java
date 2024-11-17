package org.telegram.ui.Components;

import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.view.animation.DecelerateInterpolator;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.UserConfig;
import org.telegram.ui.ActionBar.Theme;

public class TypingDotsDrawable extends StatusDrawable {
    private Paint currentPaint;
    private int currentAccount = UserConfig.selectedAccount;
    private boolean isChat = false;
    private float[] scales = new float[3];
    private float[] startTimes = {0.0f, 150.0f, 300.0f};
    private float[] elapsedTimes = {0.0f, 0.0f, 0.0f};
    private long lastUpdateTime = 0;
    private boolean started = false;
    private DecelerateInterpolator decelerateInterpolator = new DecelerateInterpolator();

    public TypingDotsDrawable(boolean z) {
        if (z) {
            this.currentPaint = new Paint(1);
        }
    }

    public void checkUpdate() {
        if (this.started) {
            if (NotificationCenter.getInstance(this.currentAccount).isAnimationInProgress()) {
                AndroidUtilities.runOnUIThread(new Runnable() {
                    @Override
                    public final void run() {
                        TypingDotsDrawable.this.checkUpdate();
                    }
                }, 100L);
            } else {
                update();
            }
        }
    }

    private void update() {
        long currentTimeMillis = System.currentTimeMillis();
        long j = currentTimeMillis - this.lastUpdateTime;
        this.lastUpdateTime = currentTimeMillis;
        if (j > 50) {
            j = 50;
        }
        for (int i = 0; i < 3; i++) {
            float[] fArr = this.elapsedTimes;
            float f = fArr[i] + ((float) j);
            fArr[i] = f;
            float[] fArr2 = this.startTimes;
            float f2 = f - fArr2[i];
            if (f2 <= 0.0f) {
                this.scales[i] = 1.33f;
            } else if (f2 <= 320.0f) {
                this.scales[i] = this.decelerateInterpolator.getInterpolation(f2 / 320.0f) + 1.33f;
            } else if (f2 <= 640.0f) {
                this.scales[i] = (1.0f - this.decelerateInterpolator.getInterpolation((f2 - 320.0f) / 320.0f)) + 1.33f;
            } else if (f2 >= 800.0f) {
                fArr[i] = 0.0f;
                fArr2[i] = 0.0f;
                this.scales[i] = 1.33f;
            } else {
                this.scales[i] = 1.33f;
            }
        }
        invalidateSelf();
    }

    @Override
    public void draw(Canvas canvas) {
        int dp = AndroidUtilities.dp(this.isChat ? 8.5f : 9.3f) + getBounds().top;
        Paint paint = this.currentPaint;
        if (paint == null) {
            paint = Theme.chat_statusPaint;
            paint.setAlpha(255);
        }
        float f = dp;
        canvas.drawCircle(AndroidUtilities.dp(3.0f), f, this.scales[0] * AndroidUtilities.density, paint);
        canvas.drawCircle(AndroidUtilities.dp(9.0f), f, this.scales[1] * AndroidUtilities.density, paint);
        canvas.drawCircle(AndroidUtilities.dp(15.0f), f, this.scales[2] * AndroidUtilities.density, paint);
        checkUpdate();
    }

    @Override
    public int getIntrinsicHeight() {
        return AndroidUtilities.dp(18.0f);
    }

    @Override
    public int getIntrinsicWidth() {
        return AndroidUtilities.dp(18.0f);
    }

    @Override
    public int getOpacity() {
        return -2;
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
        for (int i = 0; i < 3; i++) {
            this.elapsedTimes[i] = 0.0f;
            this.scales[i] = 1.33f;
        }
        float[] fArr = this.startTimes;
        fArr[0] = 0.0f;
        fArr[1] = 150.0f;
        fArr[2] = 300.0f;
        this.started = false;
    }
}
