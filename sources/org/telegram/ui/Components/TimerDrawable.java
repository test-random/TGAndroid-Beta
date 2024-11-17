package org.telegram.ui.Components;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.text.StaticLayout;
import android.text.TextPaint;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.ui.ActionBar.Theme;

public class TimerDrawable extends Drawable {
    Context context;
    ColorFilter currentColorFilter;
    private Drawable currentTtlIcon;
    private int iconColor;
    private boolean isDialog;
    private boolean isStaticIcon;
    private boolean overrideColor;
    Theme.ResourcesProvider resourcesProvider;
    private StaticLayout timeLayout;
    private TextPaint timePaint = new TextPaint(1);
    private Paint paint = new Paint(1);
    private Paint linePaint = new Paint(1);
    private float timeWidth = 0.0f;
    private int timeHeight = 0;
    private int time = -1;

    public TimerDrawable(Context context, Theme.ResourcesProvider resourcesProvider) {
        this.context = context;
        this.resourcesProvider = resourcesProvider;
        this.timePaint.setTypeface(AndroidUtilities.getTypeface("fonts/rcondensedbold.ttf"));
        this.linePaint.setStrokeWidth(AndroidUtilities.dp(1.0f));
        this.linePaint.setStyle(Paint.Style.STROKE);
    }

    public static TimerDrawable getTtlIcon(int i) {
        TimerDrawable timerDrawable = new TimerDrawable(ApplicationLoader.applicationContext, null);
        timerDrawable.setTime(i);
        timerDrawable.isStaticIcon = true;
        return timerDrawable;
    }

    public static TimerDrawable getTtlIconForDialogs(int i) {
        TimerDrawable timerDrawable = new TimerDrawable(ApplicationLoader.applicationContext, null);
        timerDrawable.isDialog = true;
        timerDrawable.setTime(i);
        return timerDrawable;
    }

    @Override
    public void draw(Canvas canvas) {
        TextPaint textPaint;
        int i;
        float f;
        int intrinsicWidth = getIntrinsicWidth();
        int intrinsicHeight = getIntrinsicHeight();
        if (this.isDialog) {
            this.timePaint.setColor(-1);
        } else {
            if (this.isStaticIcon) {
                textPaint = this.timePaint;
                i = Theme.key_actionBarDefaultSubmenuItemIcon;
            } else {
                if (!this.overrideColor) {
                    this.paint.setColor(Theme.getColor(Theme.key_actionBarDefault, this.resourcesProvider));
                }
                textPaint = this.timePaint;
                i = Theme.key_actionBarDefaultTitle;
            }
            textPaint.setColor(Theme.getColor(i, this.resourcesProvider));
        }
        if (this.currentTtlIcon != null) {
            if (!this.isStaticIcon && !this.isDialog) {
                canvas.drawCircle(getBounds().centerX(), getBounds().centerY(), getBounds().width() / 2.0f, this.paint);
                int color = Theme.getColor(Theme.key_actionBarDefaultTitle, this.resourcesProvider);
                if (this.iconColor != color) {
                    this.iconColor = color;
                    this.currentTtlIcon.setColorFilter(new PorterDuffColorFilter(color, PorterDuff.Mode.MULTIPLY));
                }
            }
            if (this.isDialog) {
                this.currentTtlIcon.setBounds(getBounds().left, getBounds().top, getBounds().left + this.currentTtlIcon.getIntrinsicWidth(), getBounds().top + this.currentTtlIcon.getIntrinsicHeight());
            } else {
                android.graphics.Rect rect = AndroidUtilities.rectTmp2;
                rect.set(getBounds().centerX() - AndroidUtilities.dp(10.5f), getBounds().centerY() - AndroidUtilities.dp(10.5f), (getBounds().centerX() - AndroidUtilities.dp(10.5f)) + this.currentTtlIcon.getIntrinsicWidth(), (getBounds().centerY() - AndroidUtilities.dp(10.5f)) + this.currentTtlIcon.getIntrinsicHeight());
                this.currentTtlIcon.setBounds(rect);
            }
            this.currentTtlIcon.draw(canvas);
        }
        if (this.time == 0 || this.timeLayout == null) {
            return;
        }
        int i2 = AndroidUtilities.density != 3.0f ? 0 : -1;
        if (this.isDialog) {
            double width = getBounds().width() / 2;
            double ceil = Math.ceil(this.timeWidth / 2.0f);
            Double.isNaN(width);
            double d = i2;
            Double.isNaN(d);
            f = (float) ((width - ceil) + d);
            intrinsicHeight = getBounds().height();
        } else {
            double d2 = intrinsicWidth / 2;
            double ceil2 = Math.ceil(this.timeWidth / 2.0f);
            Double.isNaN(d2);
            f = ((int) (d2 - ceil2)) + i2;
        }
        canvas.translate(f, (intrinsicHeight - this.timeHeight) / 2.0f);
        this.timeLayout.draw(canvas);
    }

    @Override
    public int getIntrinsicHeight() {
        return AndroidUtilities.dp(23.0f);
    }

    @Override
    public int getIntrinsicWidth() {
        return AndroidUtilities.dp(23.0f);
    }

    @Override
    public int getOpacity() {
        return -2;
    }

    public int getTime() {
        return this.time;
    }

    @Override
    public void setAlpha(int i) {
    }

    public void setBackgroundColor(int i) {
        this.overrideColor = true;
        this.paint.setColor(i);
    }

    @Override
    public void setColorFilter(ColorFilter colorFilter) {
        this.currentColorFilter = colorFilter;
        if (this.isStaticIcon) {
            this.currentTtlIcon.setColorFilter(colorFilter);
        }
    }

    public void setTime(int r9) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.TimerDrawable.setTime(int):void");
    }
}
