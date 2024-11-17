package org.telegram.ui.Components.Premium.boosts;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.view.View;
import android.view.animation.OvershootInterpolator;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.AnimatedTextView;
import org.telegram.ui.Components.CubicBezierInterpolator;

public class BoostCounterView extends View {
    private final Paint bgPaint;
    private ValueAnimator countAnimator;
    private float countScale;
    private final AnimatedTextView.AnimatedTextDrawable countText;
    private int lastCount;

    public BoostCounterView(Context context, Theme.ResourcesProvider resourcesProvider) {
        super(context);
        this.countScale = 1.0f;
        AnimatedTextView.AnimatedTextDrawable animatedTextDrawable = new AnimatedTextView.AnimatedTextDrawable(false, false, true);
        this.countText = animatedTextDrawable;
        animatedTextDrawable.setAnimationProperties(0.3f, 0L, 250L, CubicBezierInterpolator.EASE_OUT_QUINT);
        animatedTextDrawable.setCallback(this);
        animatedTextDrawable.setTextSize(AndroidUtilities.dp(11.5f));
        animatedTextDrawable.setTypeface(AndroidUtilities.bold());
        animatedTextDrawable.setTextColor(-1);
        animatedTextDrawable.setText("");
        animatedTextDrawable.setGravity(17);
        Paint paint = new Paint(1);
        this.bgPaint = paint;
        paint.setColor(-6915073);
        setVisibility(8);
    }

    private void animateCount() {
        ValueAnimator valueAnimator = this.countAnimator;
        if (valueAnimator != null) {
            valueAnimator.cancel();
            this.countAnimator = null;
        }
        ValueAnimator ofFloat = ValueAnimator.ofFloat(0.0f, 1.0f);
        this.countAnimator = ofFloat;
        ofFloat.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public final void onAnimationUpdate(ValueAnimator valueAnimator2) {
                BoostCounterView.this.lambda$animateCount$0(valueAnimator2);
            }
        });
        this.countAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animator) {
                BoostCounterView.this.countScale = 1.0f;
                BoostCounterView.this.invalidate();
            }
        });
        this.countAnimator.setInterpolator(new OvershootInterpolator(2.0f));
        this.countAnimator.setDuration(200L);
        this.countAnimator.start();
    }

    public void lambda$animateCount$0(ValueAnimator valueAnimator) {
        this.countScale = Math.max(1.0f, ((Float) valueAnimator.getAnimatedValue()).floatValue());
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.save();
        canvas.translate(AndroidUtilities.dp(3.0f), AndroidUtilities.dp(3.0f));
        Rect rect = AndroidUtilities.rectTmp2;
        rect.set(0, 0, AndroidUtilities.dp(8.0f) + ((int) this.countText.getCurrentWidth()), AndroidUtilities.dp(20.0f));
        RectF rectF = AndroidUtilities.rectTmp;
        rectF.set(rect);
        if (this.countScale != 1.0f) {
            canvas.save();
            float f = this.countScale;
            canvas.scale(f, f, rect.centerX(), rect.centerY());
        }
        canvas.drawRoundRect(rectF, AndroidUtilities.dp(10.0f), AndroidUtilities.dp(10.0f), this.bgPaint);
        rect.set(0, 0, (int) rectF.width(), AndroidUtilities.dp(19.0f));
        this.countText.setBounds(rect);
        this.countText.draw(canvas);
        if (this.countScale != 1.0f) {
            canvas.restore();
        }
        canvas.restore();
    }

    @Override
    protected void onMeasure(int i, int i2) {
        super.onMeasure(View.MeasureSpec.makeMeasureSpec((int) (AndroidUtilities.dp(15.0f) + this.countText.getWidth()), 1073741824), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(26.0f), 1073741824));
    }

    public void setCount(int i, boolean z) {
        if (!BoostRepository.isMultiBoostsAvailable()) {
            i = 0;
        }
        if (i > 0) {
            setVisibility(0);
        }
        if (z) {
            this.countText.cancelAnimation();
        }
        if (z && i != this.lastCount && i > 0) {
            animateCount();
        }
        this.lastCount = i;
        int length = this.countText.getText().length();
        this.countText.setText("x" + i, z);
        int length2 = this.countText.getText().length();
        invalidate();
        if (length != length2) {
            requestLayout();
        }
    }
}
