package org.telegram.ui.Components;

import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.ui.ActionBar.Theme;

public class MapPlaceholderDrawable extends Drawable {
    private Paint linePaint;
    private Paint paint;

    public MapPlaceholderDrawable() {
        this(Theme.getCurrentTheme().isDark());
    }

    public MapPlaceholderDrawable(boolean z) {
        Paint paint;
        int i;
        this.paint = new Paint();
        Paint paint2 = new Paint();
        this.linePaint = paint2;
        paint2.setStrokeWidth(AndroidUtilities.dp(1.0f));
        if (z) {
            this.paint.setColor(-14865331);
            paint = this.linePaint;
            i = -15854042;
        } else {
            this.paint.setColor(-2172970);
            paint = this.linePaint;
            i = -3752002;
        }
        paint.setColor(i);
    }

    @Override
    public void draw(Canvas canvas) {
        canvas.drawRect(getBounds(), this.paint);
        int dp = AndroidUtilities.dp(9.0f);
        int width = getBounds().width() / dp;
        int height = getBounds().height() / dp;
        int i = getBounds().left;
        int i2 = getBounds().top;
        int i3 = 0;
        int i4 = 0;
        while (i4 < width) {
            i4++;
            float f = (dp * i4) + i;
            canvas.drawLine(f, i2, f, getBounds().height() + i2, this.linePaint);
        }
        while (i3 < height) {
            i3++;
            float f2 = (dp * i3) + i2;
            canvas.drawLine(i, f2, getBounds().width() + i, f2, this.linePaint);
        }
    }

    @Override
    public int getIntrinsicHeight() {
        return 0;
    }

    @Override
    public int getIntrinsicWidth() {
        return 0;
    }

    @Override
    public int getOpacity() {
        return 0;
    }

    @Override
    public void setAlpha(int i) {
    }

    @Override
    public void setColorFilter(ColorFilter colorFilter) {
    }
}
