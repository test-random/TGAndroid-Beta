package org.telegram.ui.ActionBar;

import android.content.Context;
import android.graphics.Canvas;
import android.widget.TextView;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.ui.Components.AnimatedFloat;
import org.telegram.ui.Components.CircularProgressDrawable;
import org.telegram.ui.Components.CubicBezierInterpolator;

public abstract class TextViewWithLoading extends TextView {
    private final AnimatedFloat animatedLoading;
    private boolean loading;
    private CircularProgressDrawable spinner;

    public TextViewWithLoading(Context context) {
        super(context);
        this.loading = false;
        this.animatedLoading = new AnimatedFloat(this, 320L, CubicBezierInterpolator.EASE_OUT_QUINT);
        this.spinner = new CircularProgressDrawable();
    }

    public boolean isLoading() {
        return this.loading;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        float f = this.animatedLoading.set(this.loading);
        if (f < 1.0f) {
            if (f <= 0.0f) {
                canvas.save();
            } else {
                canvas.saveLayerAlpha(0.0f, 0.0f, getWidth(), getHeight(), (int) ((1.0f - f) * 255.0f), 31);
            }
            canvas.translate(0.0f, AndroidUtilities.dp(6.0f) * f);
            super.onDraw(canvas);
            canvas.restore();
        }
        if (f > 0.0f) {
            int width = getWidth() / 2;
            int height = getHeight() / 2;
            int dp = width - ((int) (AndroidUtilities.dp(6.0f) * (1.0f - f)));
            this.spinner.setAlpha((int) (f * 255.0f));
            CircularProgressDrawable circularProgressDrawable = this.spinner;
            circularProgressDrawable.setBounds(dp - (circularProgressDrawable.getIntrinsicWidth() / 2), height - (this.spinner.getIntrinsicWidth() / 2), dp + (this.spinner.getIntrinsicWidth() / 2), height + (this.spinner.getIntrinsicHeight() / 2));
            this.spinner.draw(canvas);
            invalidate();
        }
    }

    public void setLoading(boolean z, boolean z2) {
        if (this.loading == z) {
            return;
        }
        this.loading = z;
        invalidate();
        if (z2) {
            return;
        }
        this.animatedLoading.force(z);
    }

    @Override
    public void setTextColor(int i) {
        super.setTextColor(i);
        this.spinner.setColor(i);
    }
}
