package org.telegram.ui.Components;

import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.RectF;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.UserConfig;
import org.telegram.ui.ActionBar.Theme;

public class PlayingGameDrawable extends StatusDrawable {
    private final boolean isDialogScreen;
    private float progress;
    Theme.ResourcesProvider resourcesProvider;
    private boolean isChat = false;
    private Paint paint = new Paint(1);
    private int currentAccount = UserConfig.selectedAccount;
    private long lastUpdateTime = 0;
    private boolean started = false;
    private RectF rect = new RectF();

    public PlayingGameDrawable(boolean z, Theme.ResourcesProvider resourcesProvider) {
        this.isDialogScreen = z;
        this.resourcesProvider = resourcesProvider;
    }

    public void checkUpdate() {
        if (this.started) {
            if (NotificationCenter.getInstance(this.currentAccount).isAnimationInProgress()) {
                AndroidUtilities.runOnUIThread(new Runnable() {
                    @Override
                    public final void run() {
                        PlayingGameDrawable.this.checkUpdate();
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
        if (j > 16) {
            j = 16;
        }
        if (this.progress >= 1.0f) {
            this.progress = 0.0f;
        }
        float f = this.progress + (((float) j) / 300.0f);
        this.progress = f;
        if (f > 1.0f) {
            this.progress = 1.0f;
        }
        invalidateSelf();
    }

    @Override
    public void draw(Canvas canvas) {
        int dp = AndroidUtilities.dp(10.0f);
        int intrinsicHeight = getBounds().top + ((getIntrinsicHeight() - dp) / 2);
        if (!this.isChat) {
            intrinsicHeight += AndroidUtilities.dp(1.0f);
        }
        int i = intrinsicHeight;
        this.paint.setColor(Theme.getColor(this.isDialogScreen ? Theme.key_chats_actionMessage : Theme.key_chat_status, this.resourcesProvider));
        this.rect.set(0.0f, i, dp, i + dp);
        float f = this.progress;
        int i2 = (int) (f < 0.5f ? (1.0f - (f / 0.5f)) * 35.0f : ((f - 0.5f) * 35.0f) / 0.5f);
        for (int i3 = 0; i3 < 3; i3++) {
            float dp2 = (AndroidUtilities.dp(5.0f) * i3) + AndroidUtilities.dp(9.2f);
            float dp3 = AndroidUtilities.dp(5.0f);
            float f2 = this.progress;
            float f3 = dp2 - (dp3 * f2);
            if (i3 == 2) {
                this.paint.setAlpha(Math.min(255, (int) ((f2 * 255.0f) / 0.5f)));
            } else if (i3 != 0 || f2 <= 0.5f) {
                this.paint.setAlpha(255);
            } else {
                this.paint.setAlpha((int) ((1.0f - ((f2 - 0.5f) / 0.5f)) * 255.0f));
            }
            canvas.drawCircle(f3, (dp / 2) + i, AndroidUtilities.dp(1.2f), this.paint);
        }
        this.paint.setAlpha(255);
        canvas.drawArc(this.rect, i2, 360 - (i2 * 2), true, this.paint);
        this.paint.setColor(Theme.getColor(this.isDialogScreen ? Theme.key_windowBackgroundWhite : Theme.key_actionBarDefault));
        canvas.drawCircle(AndroidUtilities.dp(4.0f), (i + (dp / 2)) - AndroidUtilities.dp(2.0f), AndroidUtilities.dp(1.0f), this.paint);
        checkUpdate();
    }

    @Override
    public int getIntrinsicHeight() {
        return AndroidUtilities.dp(18.0f);
    }

    @Override
    public int getIntrinsicWidth() {
        return AndroidUtilities.dp(20.0f);
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
        this.progress = 0.0f;
        this.started = false;
    }
}
