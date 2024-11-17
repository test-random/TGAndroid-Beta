package org.telegram.ui.Components;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.text.style.ReplacementSpan;
import android.view.View;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.ui.ActionBar.Theme;

public class LoadingSpan extends ReplacementSpan {
    private LoadingDrawable drawable;
    private Paint paint;
    private float scaleY;
    private int size;
    private View view;
    public int yOffset;

    public LoadingSpan(View view, int i) {
        this(view, i, AndroidUtilities.dp(2.0f));
    }

    public LoadingSpan(View view, int i, int i2) {
        this(view, i, i2, null);
    }

    public LoadingSpan(View view, int i, int i2, Theme.ResourcesProvider resourcesProvider) {
        this.scaleY = 1.0f;
        this.view = view;
        this.size = i;
        this.yOffset = i2;
        LoadingDrawable loadingDrawable = new LoadingDrawable(resourcesProvider);
        this.drawable = loadingDrawable;
        loadingDrawable.setRadiiDp(4.0f);
    }

    @Override
    public void draw(Canvas canvas, CharSequence charSequence, int i, int i2, float f, int i3, int i4, int i5, Paint paint) {
        int i6 = (int) f;
        this.drawable.setBounds(i6, (int) (i3 + ((((i5 - AndroidUtilities.dp(2.0f)) - i3) / 2.0f) * (1.0f - this.scaleY)) + this.yOffset), this.size + i6, (int) (((i5 - AndroidUtilities.dp(2.0f)) - ((((i5 - AndroidUtilities.dp(2.0f)) - i3) / 2.0f) * (1.0f - this.scaleY))) + this.yOffset));
        if (paint != null) {
            this.drawable.setAlpha(paint.getAlpha());
        }
        this.drawable.draw(canvas);
        View view = this.view;
        if (view != null) {
            view.invalidate();
        }
    }

    @Override
    public int getSize(Paint paint, CharSequence charSequence, int i, int i2, Paint.FontMetricsInt fontMetricsInt) {
        Paint.FontMetrics fontMetrics = paint.getFontMetrics();
        if (fontMetricsInt != null) {
            fontMetricsInt.ascent = (int) fontMetrics.ascent;
            fontMetricsInt.bottom = (int) fontMetrics.bottom;
            fontMetricsInt.descent = (int) fontMetrics.descent;
            fontMetricsInt.leading = (int) fontMetrics.leading;
            fontMetricsInt.top = (int) fontMetrics.top;
        }
        this.paint = paint;
        LoadingDrawable loadingDrawable = this.drawable;
        if (loadingDrawable.color1 == null && loadingDrawable.color2 == null) {
            loadingDrawable.setColors(Theme.multAlpha(paint.getColor(), 0.1f), Theme.multAlpha(paint.getColor(), 0.25f));
        }
        return this.size;
    }

    public void setColors(int i, int i2) {
        this.drawable.color1 = Integer.valueOf(i);
        this.drawable.color2 = Integer.valueOf(i2);
    }

    public void setScaleY(float f) {
        this.scaleY = f;
    }

    public void setView(View view) {
        this.view = view;
    }
}
