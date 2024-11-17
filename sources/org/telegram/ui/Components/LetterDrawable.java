package org.telegram.ui.Components;

import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.FileLog;
import org.telegram.ui.ActionBar.Theme;

public class LetterDrawable extends Drawable {
    private static TextPaint namePaint;
    private static TextPaint namePaintSmallTopic;
    private static TextPaint namePaintTopic;
    public static Paint paint = new Paint();
    int style;
    private float textHeight;
    private StaticLayout textLayout;
    private float textLeft;
    final TextPaint textPaint;
    private float textWidth;
    private RectF rect = new RectF();
    private StringBuilder stringBuilder = new StringBuilder(5);
    public float scale = 1.0f;

    public LetterDrawable(Theme.ResourcesProvider resourcesProvider, int i) {
        TextPaint textPaint;
        this.style = i;
        if (i == 0) {
            if (namePaint == null) {
                namePaint = new TextPaint(1);
            }
            namePaint.setTextSize(AndroidUtilities.dp(28.0f));
            paint.setColor(Theme.getColor(Theme.key_sharedMedia_linkPlaceholder, resourcesProvider));
            namePaint.setColor(Theme.getColor(Theme.key_sharedMedia_linkPlaceholderText, resourcesProvider));
            textPaint = namePaint;
        } else if (i == 1) {
            if (namePaintTopic == null) {
                namePaintTopic = new TextPaint(1);
            }
            namePaintTopic.setColor(-1);
            namePaintTopic.setTextSize(AndroidUtilities.dp(13.0f));
            namePaintTopic.setTypeface(Typeface.create(Typeface.DEFAULT, 1));
            textPaint = namePaintTopic;
        } else {
            if (namePaintSmallTopic == null) {
                namePaintSmallTopic = new TextPaint(1);
            }
            namePaintSmallTopic.setColor(-1);
            namePaintSmallTopic.setTextSize(Theme.chat_topicTextPaint.getTextSize() * 0.75f);
            namePaintSmallTopic.setTypeface(Typeface.create(Typeface.DEFAULT, 1));
            textPaint = namePaintSmallTopic;
        }
        this.textPaint = textPaint;
    }

    @Override
    public void draw(Canvas canvas) {
        android.graphics.Rect bounds = getBounds();
        if (bounds == null) {
            return;
        }
        if (this.style == 0) {
            this.rect.set(bounds.left, bounds.top, bounds.right, bounds.bottom);
            canvas.drawRoundRect(this.rect, AndroidUtilities.dp(4.0f), AndroidUtilities.dp(4.0f), paint);
        }
        canvas.save();
        float f = this.scale;
        if (f != 1.0f) {
            canvas.scale(f, f, bounds.centerX(), bounds.centerY());
        }
        if (this.textLayout != null) {
            float width = bounds.width();
            canvas.translate((bounds.left + ((width - this.textWidth) / 2.0f)) - this.textLeft, bounds.top + ((width - this.textHeight) / 2.0f));
            this.textLayout.draw(canvas);
        }
        canvas.restore();
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
        return -2;
    }

    @Override
    public void setAlpha(int i) {
        this.textPaint.setAlpha(i);
        paint.setAlpha(i);
    }

    public void setBackgroundColor(int i) {
        paint.setColor(i);
    }

    public void setColor(int i) {
        this.textPaint.setColor(i);
    }

    @Override
    public void setColorFilter(ColorFilter colorFilter) {
    }

    public void setTitle(String str) {
        this.stringBuilder.setLength(0);
        if (str != null && str.length() > 0) {
            this.stringBuilder.append(str.substring(0, 1));
        }
        if (this.stringBuilder.length() <= 0) {
            this.textLayout = null;
            return;
        }
        try {
            StaticLayout staticLayout = new StaticLayout(this.stringBuilder.toString().toUpperCase(), this.textPaint, AndroidUtilities.dp(100.0f), Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
            this.textLayout = staticLayout;
            if (staticLayout.getLineCount() > 0) {
                this.textLeft = this.textLayout.getLineLeft(0);
                this.textWidth = this.textLayout.getLineWidth(0);
                this.textHeight = this.textLayout.getLineBottom(0);
            }
        } catch (Exception e) {
            FileLog.e(e);
        }
    }
}
