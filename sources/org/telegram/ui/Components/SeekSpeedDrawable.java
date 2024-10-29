package org.telegram.ui.Components;

import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.CornerPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.R;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.AnimatedTextView;

public class SeekSpeedDrawable extends Drawable {
    private final AnimatedFloat animatedDirection;
    private final AnimatedFloat animatedHintShown;
    private final AnimatedFloat animatedShown;
    private final AnimatedFloat animatedSpeed;
    private final Paint arrowPaint;
    private final Paint backgroundPaint = new Paint(1);
    private int direction;
    private final Runnable hideHintRunnable;
    private boolean hideHintScheduled;
    private final Path hintArrow;
    private RLottieDrawable hintDrawable;
    private final RectF hintRect;
    private final Text hintText;
    private Runnable invalidate;
    private final boolean isPiP;
    private final boolean isRound;
    private long lastFrameTime;
    private float lastSpeed;
    private final Path leftArrow;
    private final Path rightArrow;
    private boolean showHint;
    private boolean shown;
    private final RectF speedRect;
    private final AnimatedTextView.AnimatedTextDrawable speedText;
    private float t;

    public SeekSpeedDrawable(final Runnable runnable, boolean z, boolean z2) {
        Paint paint = new Paint(1);
        this.arrowPaint = paint;
        Path path = new Path();
        this.hintArrow = path;
        this.hintText = new Text(LocaleController.getString(R.string.SeekSpeedHint), 14.0f);
        Path path2 = new Path();
        this.leftArrow = path2;
        Path path3 = new Path();
        this.rightArrow = path3;
        this.direction = 1;
        this.speedRect = new RectF();
        this.hintRect = new RectF();
        this.hideHintRunnable = new Runnable() {
            @Override
            public final void run() {
                SeekSpeedDrawable.this.lambda$new$0();
            }
        };
        this.invalidate = runnable;
        this.isPiP = z;
        this.isRound = z2;
        CubicBezierInterpolator cubicBezierInterpolator = CubicBezierInterpolator.EASE_OUT_QUINT;
        AnimatedFloat animatedFloat = new AnimatedFloat(runnable, 0L, 360L, cubicBezierInterpolator);
        this.animatedShown = animatedFloat;
        animatedFloat.set(false, true);
        this.animatedDirection = new AnimatedFloat(runnable, 0L, 320L, cubicBezierInterpolator);
        this.animatedSpeed = new AnimatedFloat(runnable, 0L, 200L, cubicBezierInterpolator);
        AnimatedFloat animatedFloat2 = new AnimatedFloat(runnable, 0L, 360L, cubicBezierInterpolator);
        this.animatedHintShown = animatedFloat2;
        animatedFloat2.set(false, true);
        boolean z3 = false;
        AnimatedTextView.AnimatedTextDrawable animatedTextDrawable = new AnimatedTextView.AnimatedTextDrawable(false, true, true, true) {
            @Override
            public void invalidateSelf() {
                runnable.run();
            }
        };
        this.speedText = animatedTextDrawable;
        animatedTextDrawable.setScaleProperty(0.3f);
        animatedTextDrawable.setAnimationProperties(0.4f, 0L, 650L, 1.6f, cubicBezierInterpolator);
        animatedTextDrawable.setTypeface(AndroidUtilities.getTypeface("fonts/num.otf"));
        animatedTextDrawable.setTextSize(AndroidUtilities.dp(16.0f));
        setSpeed(2.0f, false);
        animatedTextDrawable.setTextColor(-1);
        animatedTextDrawable.setGravity(17);
        paint.setPathEffect(new CornerPathEffect(AndroidUtilities.dp(1.66f)));
        path2.moveTo(AndroidUtilities.dp(8.66f), -AndroidUtilities.dp(6.33f));
        path2.lineTo(0.0f, 0.0f);
        path2.lineTo(AndroidUtilities.dp(8.66f), AndroidUtilities.dp(6.33f));
        path2.close();
        path3.moveTo(0.0f, -AndroidUtilities.dp(6.33f));
        path3.lineTo(AndroidUtilities.dp(8.66f), 0.0f);
        path3.lineTo(0.0f, AndroidUtilities.dp(6.33f));
        path3.close();
        if (!z && !z2 && !MessagesController.getGlobalMainSettings().getBoolean("seekSpeedHintShowed", false)) {
            z3 = true;
        }
        this.showHint = z3;
        path.moveTo(-AndroidUtilities.dp(6.5f), 0.0f);
        path.lineTo(0.0f, -AndroidUtilities.dp(6.33f));
        path.lineTo(AndroidUtilities.dp(6.5f), 0.0f);
        path.close();
    }

    public void lambda$new$0() {
        this.showHint = false;
        this.invalidate.run();
    }

    @Override
    public void draw(Canvas canvas) {
        android.graphics.Rect bounds = getBounds();
        float currentWidth = this.speedText.getCurrentWidth() + AndroidUtilities.dp(46.0f);
        float f = this.animatedShown.set(this.shown);
        float f2 = this.animatedDirection.set(this.direction);
        if (f <= 0.0f) {
            return;
        }
        float f3 = this.animatedSpeed.set(Math.abs(this.lastSpeed));
        long currentTimeMillis = System.currentTimeMillis();
        float min = Math.min(0.016f, ((float) (currentTimeMillis - this.lastFrameTime)) / 1000.0f);
        this.lastFrameTime = currentTimeMillis;
        this.t += min * Math.min(f3, 4.0f) * 1.5f;
        this.invalidate.run();
        float f4 = currentWidth / 2.0f;
        this.speedRect.set(bounds.centerX() - f4, bounds.top + AndroidUtilities.dp(9.0f), bounds.centerX() + f4, bounds.top + AndroidUtilities.dp(37.0f));
        canvas.save();
        float f5 = f * 0.4f;
        float f6 = 0.6f + f5;
        if (bounds.width() < AndroidUtilities.displaySize.x * 0.7f) {
            f6 *= 0.75f;
            if (this.isPiP) {
                canvas.translate(-AndroidUtilities.dp(45.0f), 0.0f);
            }
        }
        canvas.scale(f6, f6, this.speedRect.centerX(), this.speedRect.top);
        canvas.translate(0.0f, (-AndroidUtilities.dp(15.0f)) * (1.0f - f));
        canvas.clipRect(this.speedRect);
        this.backgroundPaint.setColor(Theme.multAlpha(-16777216, f5));
        RectF rectF = this.speedRect;
        canvas.drawRoundRect(rectF, rectF.height() / 2.0f, this.speedRect.height() / 2.0f, this.backgroundPaint);
        this.speedText.setBounds(this.speedRect);
        canvas.save();
        float f7 = -f2;
        canvas.translate(((this.speedRect.centerX() - f4) + AndroidUtilities.dp(9.0f)) - (AndroidUtilities.dp(30.0f) * (1.0f - Math.max(0.0f, f7))), this.speedRect.centerY());
        double d = this.t;
        Double.isNaN(d);
        this.arrowPaint.setColor(Theme.multAlpha(-1, Math.max(0.0f, f7) * f * ((((((float) Math.sin(d * 3.141592653589793d)) / 2.0f) + 1.0f) * 0.75f) + 0.2f)));
        canvas.drawPath(this.leftArrow, this.arrowPaint);
        canvas.translate(AndroidUtilities.dp(10.66f), 0.0f);
        double d2 = this.t + 0.17f;
        Double.isNaN(d2);
        this.arrowPaint.setColor(Theme.multAlpha(-1, Math.max(0.0f, f7) * f * ((((((float) Math.sin(d2 * 3.141592653589793d)) / 2.0f) + 1.0f) * 0.75f) + 0.2f)));
        canvas.drawPath(this.leftArrow, this.arrowPaint);
        canvas.restore();
        canvas.save();
        canvas.translate(((-AndroidUtilities.dp(28.0f)) / 2.0f) * f2, 0.0f);
        this.speedText.setAlpha((int) (f * 255.0f));
        this.speedText.draw(canvas);
        canvas.restore();
        canvas.save();
        canvas.translate(((this.speedRect.centerX() + f4) - AndroidUtilities.dp(30.0f)) + (AndroidUtilities.dp(30.0f) * (1.0f - Math.max(0.0f, f2))), this.speedRect.centerY());
        double d3 = this.t;
        Double.isNaN(d3);
        this.arrowPaint.setColor(Theme.multAlpha(-1, Math.max(0.0f, f2) * f * ((((((float) Math.sin(d3 * 3.141592653589793d)) / 2.0f) + 1.0f) * 0.75f) + 0.2f)));
        canvas.drawPath(this.rightArrow, this.arrowPaint);
        canvas.translate(AndroidUtilities.dp(10.66f), 0.0f);
        double d4 = this.t - 0.17f;
        Double.isNaN(d4);
        this.arrowPaint.setColor(Theme.multAlpha(-1, Math.max(0.0f, f2) * f * ((((((float) Math.sin(d4 * 3.141592653589793d)) / 2.0f) + 1.0f) * 0.75f) + 0.2f)));
        canvas.drawPath(this.rightArrow, this.arrowPaint);
        canvas.restore();
        canvas.restore();
        float f8 = this.animatedHintShown.set(this.showHint && this.shown);
        if (f8 > 0.0f) {
            if (this.hintDrawable == null) {
                int i = R.raw.seek_speed_hint;
                RLottieDrawable rLottieDrawable = new RLottieDrawable(i, "" + i, AndroidUtilities.dp(24.0f), AndroidUtilities.dp(24.0f), true, null);
                this.hintDrawable = rLottieDrawable;
                rLottieDrawable.setAllowDecodeSingleFrame(true);
                this.hintDrawable.setCallback(new Drawable.Callback() {
                    @Override
                    public void invalidateDrawable(Drawable drawable) {
                        SeekSpeedDrawable.this.invalidate.run();
                    }

                    @Override
                    public void scheduleDrawable(Drawable drawable, Runnable runnable, long j) {
                    }

                    @Override
                    public void unscheduleDrawable(Drawable drawable, Runnable runnable) {
                    }
                });
                this.hintDrawable.setAutoRepeat(1);
                this.hintDrawable.start();
            }
            float currentWidth2 = this.hintText.getCurrentWidth() + AndroidUtilities.dp(54.0f);
            float dp = AndroidUtilities.dp(32.0f);
            RectF rectF2 = this.hintRect;
            float f9 = currentWidth2 / 2.0f;
            float centerX = bounds.centerX() - f9;
            RectF rectF3 = this.speedRect;
            float height = rectF3.top + (rectF3.height() * f) + AndroidUtilities.dp(11.0f);
            float centerX2 = bounds.centerX() + f9;
            RectF rectF4 = this.speedRect;
            rectF2.set(centerX, height, centerX2, rectF4.top + (rectF4.height() * f) + AndroidUtilities.dp(11.0f) + dp);
            canvas.save();
            float f10 = (0.25f * f8) + 0.75f;
            canvas.scale(f10, f10, this.hintRect.centerX(), this.hintRect.top);
            this.backgroundPaint.setColor(Theme.multAlpha(-16777216, f8 * 0.4f));
            canvas.save();
            canvas.translate(this.hintRect.centerX(), this.hintRect.top);
            canvas.drawPath(this.hintArrow, this.backgroundPaint);
            canvas.restore();
            canvas.drawRoundRect(this.hintRect, AndroidUtilities.dp(8.0f), AndroidUtilities.dp(8.0f), this.backgroundPaint);
            this.hintDrawable.setBounds(((int) this.hintRect.left) + AndroidUtilities.dp(11.0f), ((int) this.hintRect.centerY()) - (AndroidUtilities.dp(24.0f) / 2), ((int) this.hintRect.left) + AndroidUtilities.dp(35.0f), ((int) this.hintRect.centerY()) + (AndroidUtilities.dp(24.0f) / 2));
            this.hintDrawable.setAlpha((int) (f8 * 255.0f));
            if (!this.hintDrawable.isRunning()) {
                this.hintDrawable.restart(true);
            }
            this.hintDrawable.draw(canvas);
            this.hintText.draw(canvas, AndroidUtilities.dp(39.0f) + this.hintRect.left, this.hintRect.centerY(), -1, f8);
            canvas.restore();
        }
    }

    @Override
    public int getOpacity() {
        return -2;
    }

    public boolean isShown() {
        return this.shown || this.animatedShown.get() > 0.0f;
    }

    @Override
    public void setAlpha(int i) {
    }

    @Override
    public void setColorFilter(ColorFilter colorFilter) {
    }

    public void setShown(boolean z, boolean z2) {
        this.shown = z;
        if (!z2) {
            this.animatedShown.set(z, true);
        }
        this.invalidate.run();
        RLottieDrawable rLottieDrawable = this.hintDrawable;
        if (rLottieDrawable == null || !this.showHint) {
            return;
        }
        if (z) {
            rLottieDrawable.restart();
        } else {
            rLottieDrawable.stop();
        }
    }

    public void setSpeed(float f, boolean z) {
        if (Math.floor(this.lastSpeed * 10.0f) != Math.floor(10.0f * f)) {
            this.speedText.cancelAnimation();
            this.speedText.setText(String.format("%.1fx", Float.valueOf(Math.abs(f))), z);
            this.lastSpeed = f;
        }
        int i = f > 0.0f ? 1 : -1;
        this.direction = i;
        if (!z) {
            this.animatedDirection.set(i, true);
        }
        this.invalidate.run();
        if (!this.showHint || Math.abs(f) <= 3.0f || this.hideHintScheduled) {
            return;
        }
        this.hideHintScheduled = true;
        AndroidUtilities.runOnUIThread(this.hideHintRunnable, 2500L);
        MessagesController.getGlobalMainSettings().edit().putBoolean("seekSpeedHintShowed", true).apply();
    }
}
