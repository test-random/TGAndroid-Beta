package org.telegram.ui.Stories.recorder;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.RadialGradient;
import android.graphics.RectF;
import android.graphics.Shader;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import androidx.core.graphics.ColorUtils;
import java.util.ArrayList;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.Utilities;
import org.telegram.ui.Components.CubicBezierInterpolator;
public class FlashViews {
    public static final int[] COLORS = {-1, -70004, -7544833};
    private ValueAnimator animator;
    public final View backgroundView;
    private int color;
    public final View foregroundView;
    private RadialGradient gradient;
    private int lastColor;
    private int lastHeight;
    private float lastInvert;
    private int lastWidth;
    private final Paint paint;
    private final WindowManager windowManager;
    private final View windowView;
    private final WindowManager.LayoutParams windowViewParams;
    private final ArrayList<Invertable> invertableViews = new ArrayList<>();
    private float invert = 0.0f;
    private final Matrix gradientMatrix = new Matrix();

    public interface Invertable {
        void invalidate();

        void setInvert(float f);
    }

    public FlashViews(Context context, WindowManager windowManager, View view, WindowManager.LayoutParams layoutParams) {
        Paint paint = new Paint(1);
        this.paint = paint;
        this.windowManager = windowManager;
        this.windowView = view;
        this.windowViewParams = layoutParams;
        this.backgroundView = new View(context) {
            @Override
            protected void onMeasure(int i, int i2) {
                super.onMeasure(i, i2);
                FlashViews.this.invalidateGradient();
            }

            @Override
            protected void dispatchDraw(Canvas canvas) {
                FlashViews.this.gradientMatrix.reset();
                FlashViews.this.drawGradient(canvas, true);
            }
        };
        this.foregroundView = new View(context) {
            @Override
            protected void dispatchDraw(Canvas canvas) {
                FlashViews.this.gradientMatrix.reset();
                FlashViews.this.gradientMatrix.postTranslate(-getX(), -getY());
                FlashViews.this.gradientMatrix.postScale(1.0f / getScaleX(), 1.0f / getScaleY(), getPivotX(), getPivotY());
                FlashViews.this.drawGradient(canvas, false);
            }
        };
        paint.setAlpha(0);
        setColor(2);
    }

    public void flash(final Utilities.Callback<Utilities.Callback<Runnable>> callback) {
        WindowManager.LayoutParams layoutParams = this.windowViewParams;
        layoutParams.screenBrightness = 1.0f;
        this.windowManager.updateViewLayout(this.windowView, layoutParams);
        flashTo(1.0f, 320L, new Runnable() {
            @Override
            public final void run() {
                FlashViews.this.lambda$flash$3(callback);
            }
        });
    }

    public void lambda$flash$3(final Utilities.Callback callback) {
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                FlashViews.this.lambda$flash$2(callback);
            }
        }, 320L);
    }

    public void lambda$flash$2(Utilities.Callback callback) {
        callback.run(new Utilities.Callback() {
            @Override
            public final void run(Object obj) {
                FlashViews.this.lambda$flash$1((Runnable) obj);
            }
        });
    }

    public void lambda$flash$1(final Runnable runnable) {
        WindowManager.LayoutParams layoutParams = this.windowViewParams;
        layoutParams.screenBrightness = -1.0f;
        this.windowManager.updateViewLayout(this.windowView, layoutParams);
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                FlashViews.this.lambda$flash$0(runnable);
            }
        }, 80L);
    }

    public void lambda$flash$0(Runnable runnable) {
        flashTo(0.0f, 240L, runnable);
    }

    public void flashIn(Runnable runnable) {
        WindowManager.LayoutParams layoutParams = this.windowViewParams;
        layoutParams.screenBrightness = 1.0f;
        this.windowManager.updateViewLayout(this.windowView, layoutParams);
        flashTo(1.0f, 320L, runnable);
    }

    public void flashOut() {
        WindowManager.LayoutParams layoutParams = this.windowViewParams;
        layoutParams.screenBrightness = -1.0f;
        this.windowManager.updateViewLayout(this.windowView, layoutParams);
        flashTo(0.0f, 240L, null);
    }

    private void flashTo(final float f, long j, final Runnable runnable) {
        ValueAnimator valueAnimator = this.animator;
        if (valueAnimator != null) {
            valueAnimator.cancel();
            this.animator = null;
        }
        if (j <= 0) {
            this.invert = f;
            for (int i = 0; i < this.invertableViews.size(); i++) {
                this.invertableViews.get(i).setInvert(this.invert);
                this.invertableViews.get(i).invalidate();
            }
            this.paint.setAlpha((int) (this.invert * 255.0f));
            this.backgroundView.invalidate();
            this.foregroundView.invalidate();
            this.paint.setAlpha((int) (this.invert * 255.0f));
            if (runnable != null) {
                runnable.run();
                return;
            }
            return;
        }
        ValueAnimator ofFloat = ValueAnimator.ofFloat(this.invert, f);
        this.animator = ofFloat;
        ofFloat.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public final void onAnimationUpdate(ValueAnimator valueAnimator2) {
                FlashViews.this.lambda$flashTo$4(valueAnimator2);
            }
        });
        this.animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animator) {
                FlashViews.this.invert = f;
                for (int i2 = 0; i2 < FlashViews.this.invertableViews.size(); i2++) {
                    ((Invertable) FlashViews.this.invertableViews.get(i2)).setInvert(FlashViews.this.invert);
                    ((Invertable) FlashViews.this.invertableViews.get(i2)).invalidate();
                }
                FlashViews.this.paint.setAlpha((int) (FlashViews.this.invert * 255.0f));
                FlashViews.this.backgroundView.invalidate();
                FlashViews.this.foregroundView.invalidate();
                Runnable runnable2 = runnable;
                if (runnable2 != null) {
                    runnable2.run();
                }
            }
        });
        this.animator.setDuration(j);
        this.animator.setInterpolator(CubicBezierInterpolator.EASE_IN);
        this.animator.start();
    }

    public void lambda$flashTo$4(ValueAnimator valueAnimator) {
        this.invert = ((Float) valueAnimator.getAnimatedValue()).floatValue();
        for (int i = 0; i < this.invertableViews.size(); i++) {
            this.invertableViews.get(i).setInvert(this.invert);
            this.invertableViews.get(i).invalidate();
        }
        this.paint.setAlpha((int) (this.invert * 255.0f));
        this.backgroundView.invalidate();
        this.foregroundView.invalidate();
        this.paint.setAlpha((int) (this.invert * 255.0f));
    }

    public void add(Invertable invertable) {
        invertable.setInvert(this.invert);
        this.invertableViews.add(invertable);
    }

    public void setColor(int i) {
        this.color = COLORS[i];
        invalidateGradient();
    }

    public void invalidateGradient() {
        if (this.lastColor == this.color && this.lastWidth == this.backgroundView.getMeasuredWidth() && this.lastHeight == this.backgroundView.getMeasuredHeight() && Math.abs(this.lastInvert - this.invert) <= 0.005f) {
            return;
        }
        this.lastColor = this.color;
        this.lastWidth = this.backgroundView.getMeasuredWidth();
        int measuredHeight = this.backgroundView.getMeasuredHeight();
        this.lastHeight = measuredHeight;
        this.lastInvert = this.invert;
        if (this.lastWidth <= 0 || measuredHeight <= 0) {
            return;
        }
        int i = this.lastWidth;
        int i2 = this.lastHeight;
        RadialGradient radialGradient = new RadialGradient(0.5f * i, i2 * 0.4f, (Math.min(i, i2) / 2.0f) * 1.35f * (2.0f - this.invert), new int[]{ColorUtils.setAlphaComponent(this.color, 0), this.color}, new float[]{AndroidUtilities.lerp(0.9f, 0.22f, this.invert), 1.0f}, Shader.TileMode.CLAMP);
        this.gradient = radialGradient;
        this.paint.setShader(radialGradient);
        invalidate();
    }

    private void invalidate() {
        this.backgroundView.invalidate();
        this.foregroundView.invalidate();
    }

    public void drawGradient(Canvas canvas, boolean z) {
        if (this.gradient != null) {
            invalidateGradient();
            this.gradient.setLocalMatrix(this.gradientMatrix);
            if (z) {
                canvas.drawRect(0.0f, 0.0f, this.lastWidth, this.lastHeight, this.paint);
                return;
            }
            RectF rectF = AndroidUtilities.rectTmp;
            rectF.set(0.0f, 0.0f, this.foregroundView.getMeasuredWidth(), this.foregroundView.getMeasuredHeight());
            canvas.drawRoundRect(rectF, AndroidUtilities.dp(12.0f) - 2, AndroidUtilities.dp(12.0f) - 2, this.paint);
        }
    }

    public static class ImageViewInvertable extends ImageView implements Invertable {
        public ImageViewInvertable(Context context) {
            super(context);
        }

        @Override
        public void setInvert(float f) {
            setColorFilter(new PorterDuffColorFilter(ColorUtils.blendARGB(-1, -16777216, f), PorterDuff.Mode.MULTIPLY));
        }
    }
}