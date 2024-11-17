package org.telegram.ui.Components;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.CornerPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.drawable.Drawable;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.ui.ActionBar.Theme;

public class FolderDrawable extends Drawable {
    private final Drawable drawable;
    private final Paint fillPaint;
    private final Path path;
    private boolean pathInvalidated = true;
    private final Paint strokePaint;

    public FolderDrawable(Context context, int i, int i2) {
        this.drawable = context.getResources().getDrawable(i);
        if (i2 < 0) {
            this.path = null;
            this.strokePaint = null;
            this.fillPaint = null;
            return;
        }
        this.path = new Path();
        Paint paint = new Paint(1);
        this.strokePaint = paint;
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(Theme.getColor(Theme.key_dialogBackground));
        paint.setPathEffect(new CornerPathEffect(AndroidUtilities.dp(1.0f)));
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setStrokeJoin(Paint.Join.ROUND);
        Paint paint2 = new Paint(1);
        this.fillPaint = paint2;
        paint2.setStyle(Paint.Style.FILL);
        int[] iArr = Theme.keys_avatar_nameInMessage;
        paint2.setColor(Theme.getColor(iArr[i2 % iArr.length]));
        paint2.setPathEffect(new CornerPathEffect(AndroidUtilities.dp(1.0f)));
    }

    @Override
    public void draw(Canvas canvas) {
        this.drawable.setBounds(getBounds());
        this.drawable.draw(canvas);
        Path path = this.path;
        if (path != null) {
            if (this.pathInvalidated) {
                path.rewind();
                this.path.moveTo(x(0.4871f), y(0.6025f));
                this.path.lineTo(x(0.8974f), y(0.6025f));
                this.path.lineTo(x(1.0f), y(0.7564f));
                this.path.lineTo(x(0.8974f), y(0.9102f));
                this.path.lineTo(x(0.4871f), y(0.9102f));
                this.path.close();
                this.pathInvalidated = false;
                this.strokePaint.setStrokeWidth(AndroidUtilities.dp(3.0f));
            }
            canvas.drawPath(this.path, this.strokePaint);
            canvas.drawPath(this.path, this.fillPaint);
        }
    }

    @Override
    public int getIntrinsicHeight() {
        return this.drawable.getIntrinsicHeight();
    }

    @Override
    public int getIntrinsicWidth() {
        return this.drawable.getIntrinsicWidth();
    }

    @Override
    public int getOpacity() {
        return this.drawable.getOpacity();
    }

    @Override
    public void setAlpha(int i) {
        this.drawable.setAlpha(i);
    }

    @Override
    public void setBounds(int i, int i2, int i3, int i4) {
        super.setBounds(i, i2, i3, i4);
        this.pathInvalidated = true;
    }

    @Override
    public void setColorFilter(ColorFilter colorFilter) {
        this.drawable.setColorFilter(colorFilter);
    }

    int x(float f) {
        return AndroidUtilities.lerp(getBounds().left, getBounds().right, f);
    }

    int y(float f) {
        return AndroidUtilities.lerp(getBounds().top, getBounds().bottom, f);
    }
}
