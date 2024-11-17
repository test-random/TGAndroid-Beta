package org.telegram.ui.Stories;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.view.View;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.R;
import org.telegram.messenger.Utilities;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.AnimatedFloat;
import org.telegram.ui.Components.CubicBezierInterpolator;
import org.telegram.ui.Stories.PeerStoriesView;

public class StoryLinesDrawable {
    float bufferingProgress;
    boolean incrementBuffering;
    int lastPosition;
    private final PeerStoriesView.SharedResources sharedResources;
    private final View view;
    private final StaticLayout zoomHintLayout;
    private final float zoomHintLayoutLeft;
    private final float zoomHintLayoutWidth;
    private final TextPaint zoomHintPaint;
    private final AnimatedFloat zoomT;

    public StoryLinesDrawable(View view, PeerStoriesView.SharedResources sharedResources) {
        this.view = view;
        this.sharedResources = sharedResources;
        this.zoomT = new AnimatedFloat(view, 0L, 360L, CubicBezierInterpolator.EASE_OUT_QUINT);
        TextPaint textPaint = new TextPaint(1);
        this.zoomHintPaint = textPaint;
        textPaint.setTextSize(AndroidUtilities.dp(14.0f));
        textPaint.setColor(-1);
        textPaint.setShadowLayer(AndroidUtilities.dp(3.0f), 0.0f, AndroidUtilities.dp(1.0f), 805306368);
        StaticLayout staticLayout = new StaticLayout(LocaleController.getString(R.string.StorySeekHelp), textPaint, AndroidUtilities.displaySize.x, Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
        this.zoomHintLayout = staticLayout;
        this.zoomHintLayoutLeft = staticLayout.getLineCount() > 0 ? staticLayout.getLineLeft(0) : 0.0f;
        this.zoomHintLayoutWidth = staticLayout.getLineCount() > 0 ? staticLayout.getLineWidth(0) : 0.0f;
    }

    public void draw(Canvas canvas, int i, int i2, float f, int i3, float f2, float f3, boolean z, boolean z2, float f4) {
        Paint paint;
        float f5;
        int i4;
        float f6;
        Paint paint2;
        float f7;
        Paint paint3;
        int i5;
        StoryLinesDrawable storyLinesDrawable = this;
        int i6 = i3;
        if (i6 <= 0) {
            return;
        }
        boolean z3 = z && !z2;
        if (storyLinesDrawable.lastPosition != i2) {
            storyLinesDrawable.bufferingProgress = 0.0f;
            storyLinesDrawable.incrementBuffering = true;
        }
        storyLinesDrawable.lastPosition = i2;
        PeerStoriesView.SharedResources sharedResources = storyLinesDrawable.sharedResources;
        Paint paint4 = sharedResources.barPaint;
        Paint paint5 = sharedResources.selectedBarPaint;
        int dp = i6 > 100 ? 1 : i6 >= 50 ? AndroidUtilities.dp(1.0f) : AndroidUtilities.dp(2.0f);
        float dp2 = ((i - AndroidUtilities.dp(10.0f)) - ((i6 - 1) * dp)) / i6;
        AndroidUtilities.dp(5.0f);
        float min = Math.min(dp2 / 2.0f, AndroidUtilities.dp(1.0f));
        float f8 = storyLinesDrawable.zoomT.set(z2);
        float f9 = f;
        if (f8 > 0.0f) {
            float lerp = AndroidUtilities.lerp(f9, f4, f8);
            canvas.save();
            storyLinesDrawable.zoomHintPaint.setAlpha((int) (f8 * 255.0f));
            paint = paint5;
            storyLinesDrawable.zoomHintPaint.setShadowLayer(AndroidUtilities.dp(3.0f), 0.0f, AndroidUtilities.dp(1.0f), Theme.multAlpha(805306368, f8));
            canvas.translate(((i - storyLinesDrawable.zoomHintLayoutWidth) / 2.0f) - storyLinesDrawable.zoomHintLayoutLeft, AndroidUtilities.lerp(AndroidUtilities.dp(4.0f), AndroidUtilities.dp(16.0f), f8));
            storyLinesDrawable.zoomHintLayout.draw(canvas);
            canvas.restore();
            f9 = lerp;
        } else {
            paint = paint5;
        }
        int i7 = 0;
        while (i7 < i6) {
            float dp3 = AndroidUtilities.dp(5.0f) + (-0.0f) + (dp * i7) + (i7 * dp2);
            if (dp3 <= i) {
                float f10 = dp3 + dp2;
                if (f10 >= 0.0f && f3 > 0.0f) {
                    float lerp2 = AndroidUtilities.lerp(min, AndroidUtilities.dpf2(2.0f), f8);
                    if (i7 > i2 || i7 != i2) {
                        f5 = min;
                        i4 = dp;
                        f6 = f9;
                        f7 = 1.0f;
                    } else {
                        f5 = min;
                        RectF rectF = AndroidUtilities.rectTmp;
                        i4 = dp;
                        f6 = f9;
                        rectF.set(dp3, 0.0f, f10, AndroidUtilities.lerp(AndroidUtilities.dpf2(2.0f), AndroidUtilities.dpf2(5.0f), (i2 == i7 ? 1 : 0) * f8));
                        if (z3) {
                            if (storyLinesDrawable.incrementBuffering) {
                                float f11 = storyLinesDrawable.bufferingProgress + 0.026666667f;
                                storyLinesDrawable.bufferingProgress = f11;
                                if (f11 > 0.5f) {
                                    storyLinesDrawable.incrementBuffering = false;
                                }
                            } else {
                                float f12 = storyLinesDrawable.bufferingProgress - 0.026666667f;
                                storyLinesDrawable.bufferingProgress = f12;
                                if (f12 < -0.5f) {
                                    storyLinesDrawable.incrementBuffering = true;
                                }
                            }
                            i5 = (int) (51.0f * f3 * f2 * storyLinesDrawable.bufferingProgress);
                        } else {
                            i5 = 0;
                        }
                        paint4.setAlpha(((int) (85.0f * f3 * f2)) + i5);
                        if (f8 > 0.0f) {
                            int i8 = i7 - i2;
                            rectF.left = Utilities.clamp(AndroidUtilities.lerp(rectF.left, (i8 * i) + AndroidUtilities.dp(5.0f), f8), i - AndroidUtilities.dp(5.0f), AndroidUtilities.dp(5.0f));
                            rectF.right = Utilities.clamp(AndroidUtilities.lerp(rectF.right, ((i8 + 1) * i) - AndroidUtilities.dp(5.0f), f8), i - AndroidUtilities.dp(5.0f), AndroidUtilities.dp(5.0f));
                        }
                        canvas.drawRoundRect(rectF, lerp2, lerp2, paint4);
                        f7 = f6;
                    }
                    RectF rectF2 = AndroidUtilities.rectTmp;
                    rectF2.set(dp3, 0.0f, f10, AndroidUtilities.lerp(AndroidUtilities.dpf2(2.0f), AndroidUtilities.dpf2(5.0f), (i2 == i7 ? 1 : 0) * f8));
                    if (f8 > 0.0f) {
                        int i9 = i7 - i2;
                        rectF2.left = Utilities.clamp(AndroidUtilities.lerp(rectF2.left, (i9 * i) + AndroidUtilities.dp(5.0f), f8), i - AndroidUtilities.dp(5.0f), AndroidUtilities.dp(5.0f));
                        rectF2.right = Utilities.clamp(AndroidUtilities.lerp(rectF2.right, ((i9 + 1) * i) - AndroidUtilities.dp(5.0f), f8), i - AndroidUtilities.dp(5.0f), AndroidUtilities.dp(5.0f));
                    }
                    rectF2.right = AndroidUtilities.lerp(rectF2.left, rectF2.right, f7);
                    if (i7 <= i2) {
                        paint2 = paint;
                        paint2.setAlpha((int) (f3 * 255.0f * f2));
                        paint3 = paint2;
                    } else {
                        paint2 = paint;
                        paint4.setAlpha((int) (85 * f3 * f2));
                        paint3 = paint4;
                    }
                    canvas.drawRoundRect(rectF2, lerp2, lerp2, paint3);
                    i7++;
                    storyLinesDrawable = this;
                    f9 = f6;
                    i6 = i3;
                    paint = paint2;
                    min = f5;
                    dp = i4;
                }
            }
            f5 = min;
            i4 = dp;
            f6 = f9;
            paint2 = paint;
            i7++;
            storyLinesDrawable = this;
            f9 = f6;
            i6 = i3;
            paint = paint2;
            min = f5;
            dp = i4;
        }
    }
}
