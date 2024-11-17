package org.telegram.ui.Components;

import android.graphics.Canvas;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.text.StaticLayout;
import android.view.View;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.SharedConfig;
import org.telegram.ui.ActionBar.Theme;

public class ForwardBackground {
    public final ButtonBounce bounce;
    private LoadingDrawable loadingDrawable;
    private Drawable rippleDrawable;
    private int rippleDrawableColor;
    private final View view;
    public final Path path = new Path();
    public final android.graphics.Rect bounds = new android.graphics.Rect();
    private final RectF r = new RectF();

    public ForwardBackground(View view) {
        this.view = view;
        this.bounce = new ButtonBounce(view, 0.8f, 1.4f);
    }

    public void draw(Canvas canvas, boolean z) {
        canvas.save();
        canvas.clipPath(this.path);
        Drawable drawable = this.rippleDrawable;
        if (drawable != null) {
            drawable.setBounds(this.bounds);
            this.rippleDrawable.draw(canvas);
        }
        if (z) {
            LoadingDrawable loadingDrawable = this.loadingDrawable;
            if (loadingDrawable == null) {
                LoadingDrawable loadingDrawable2 = new LoadingDrawable();
                this.loadingDrawable = loadingDrawable2;
                loadingDrawable2.setAppearByGradient(true);
            } else if (loadingDrawable.isDisappeared() || this.loadingDrawable.isDisappearing()) {
                this.loadingDrawable.reset();
                this.loadingDrawable.resetDisappear();
            }
        } else {
            LoadingDrawable loadingDrawable3 = this.loadingDrawable;
            if (loadingDrawable3 != null && !loadingDrawable3.isDisappearing() && !this.loadingDrawable.isDisappeared()) {
                this.loadingDrawable.disappear();
            }
        }
        canvas.restore();
        LoadingDrawable loadingDrawable4 = this.loadingDrawable;
        if (loadingDrawable4 == null || loadingDrawable4.isDisappeared()) {
            return;
        }
        this.loadingDrawable.usePath(this.path);
        this.loadingDrawable.setColors(Theme.multAlpha(this.rippleDrawableColor, 0.7f), Theme.multAlpha(this.rippleDrawableColor, 1.3f), Theme.multAlpha(this.rippleDrawableColor, 1.5f), Theme.multAlpha(this.rippleDrawableColor, 2.0f));
        this.loadingDrawable.setBounds(this.bounds);
        canvas.save();
        this.loadingDrawable.draw(canvas);
        canvas.restore();
        this.view.invalidate();
    }

    public void set(StaticLayout[] staticLayoutArr, boolean z) {
        float dp;
        int dp2 = AndroidUtilities.dp(4.0f) + (((int) Theme.chat_forwardNamePaint.getTextSize()) * 2);
        float max = Math.max(0, Math.min(6, SharedConfig.bubbleRadius) - 1);
        float min = Math.min(9, SharedConfig.bubbleRadius);
        float min2 = Math.min(3, SharedConfig.bubbleRadius);
        float f = -AndroidUtilities.dp(((min / 9.0f) * 2.66f) + 4.0f);
        float f2 = -AndroidUtilities.dp(3.0f);
        float dp3 = dp2 + AndroidUtilities.dp(5.0f);
        float lineWidth = staticLayoutArr[0].getLineWidth(0) + AndroidUtilities.dp(r8);
        float lineWidth2 = staticLayoutArr[1].getLineWidth(0) + AndroidUtilities.dp(r8);
        this.path.rewind();
        if (!z) {
            max = SharedConfig.bubbleRadius / 2.0f;
        }
        float dp4 = AndroidUtilities.dp(max) * 2;
        this.r.set(f, f2, f + dp4, dp4 + f2);
        this.path.arcTo(this.r, 180.0f, 90.0f);
        float f3 = lineWidth - lineWidth2;
        float max2 = Math.abs(f3) < ((float) AndroidUtilities.dp(min2 + min)) ? Math.max(lineWidth, lineWidth2) : lineWidth;
        if (Math.abs(f3) > AndroidUtilities.dp(r12)) {
            float dp5 = AndroidUtilities.dp(min2) * 2;
            if (lineWidth < lineWidth2) {
                float f4 = ((dp3 - f2) * 0.45f) + f2;
                dp = AndroidUtilities.dp(min) * 2;
                this.r.set(max2 - dp, f2, max2, f2 + dp);
                this.path.arcTo(this.r, 270.0f, 90.0f);
                this.r.set(lineWidth, f4 - dp5, dp5 + lineWidth, f4);
                this.path.arcTo(this.r, 180.0f, -90.0f);
                float f5 = lineWidth2 - (dp3 - f4);
                this.r.set(f5, f4, lineWidth2, dp3);
                this.path.arcTo(this.r, 270.0f, 90.0f);
                this.r.set(f5, f4, lineWidth2, dp3);
            } else {
                float f6 = ((dp3 - f2) * 0.55f) + f2;
                float f7 = f6 - f2;
                this.r.set(max2 - f7, f2, max2, f6);
                this.path.arcTo(this.r, 270.0f, 90.0f);
                dp = AndroidUtilities.dp(min) * 2;
                this.r.set(lineWidth - f7, f2, lineWidth, f6);
                this.path.arcTo(this.r, 0.0f, 90.0f);
                this.r.set(lineWidth2, f6, lineWidth2 + dp5, dp5 + f6);
                this.path.arcTo(this.r, 270.0f, -90.0f);
                this.r.set(lineWidth2 - dp, dp3 - dp, lineWidth2, dp3);
            }
        } else {
            dp = AndroidUtilities.dp(min) * 2;
            float f8 = max2 - dp;
            this.r.set(f8, f2, max2, f2 + dp);
            this.path.arcTo(this.r, 270.0f, 90.0f);
            this.r.set(f8, dp3 - dp, max2, dp3);
        }
        this.path.arcTo(this.r, 0.0f, 90.0f);
        this.r.set(f, dp3 - dp, dp + f, dp3);
        this.path.arcTo(this.r, 90.0f, 90.0f);
        this.path.close();
        this.bounds.set((int) f, (int) f2, (int) Math.max(lineWidth, lineWidth2), (int) dp3);
    }

    public void setColor(int i) {
        if (this.rippleDrawableColor != i) {
            Drawable drawable = this.rippleDrawable;
            if (drawable == null) {
                this.rippleDrawable = Theme.createSelectorDrawable(i, 2);
            } else {
                Theme.setSelectorDrawableColor(drawable, i, true);
            }
            this.rippleDrawable.setCallback(this.view);
            this.rippleDrawableColor = i;
        }
    }

    public void setPressed(boolean z) {
        setPressed(z, this.bounds.centerX(), this.bounds.centerY());
    }

    public void setPressed(boolean z, float f, float f2) {
        Drawable drawable;
        this.bounce.setPressed(z);
        if (z && (drawable = this.rippleDrawable) != null && Build.VERSION.SDK_INT >= 21) {
            drawable.setHotspot(f, f2);
        }
        Drawable drawable2 = this.rippleDrawable;
        if (drawable2 != null) {
            drawable2.setState(z ? new int[]{16842910, 16842919} : new int[0]);
        }
        this.view.invalidate();
    }
}
