package org.telegram.ui.Stories.recorder;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.view.MotionEvent;
import android.view.View;
import android.view.accessibility.AccessibilityNodeInfo;
import android.view.animation.OvershootInterpolator;
import android.widget.FrameLayout;
import androidx.core.content.ContextCompat;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.R;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.AnimatedFloat;
import org.telegram.ui.Components.AnimatedTextView;
import org.telegram.ui.Components.CircularProgressDrawable;
import org.telegram.ui.Components.CubicBezierInterpolator;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.Loadable;
import org.telegram.ui.Components.LoadingDrawable;
import org.telegram.ui.Components.ScaleStateListAnimator;

public class ButtonWithCounterView extends FrameLayout implements Loadable {
    private float countAlpha;
    private final AnimatedFloat countAlphaAnimated;
    private ValueAnimator countAnimator;
    private boolean countFilled;
    private float countScale;
    private final AnimatedTextView.AnimatedTextDrawable countText;
    private Drawable counterDrawable;
    private boolean enabled;
    private ValueAnimator enabledAnimator;
    private float enabledT;
    private final boolean filled;
    private boolean flickeringLoading;
    private LoadingDrawable flickeringLoadingDrawable;
    private int globalAlpha;
    private int lastCount;
    private boolean loading;
    private ValueAnimator loadingAnimator;
    private CircularProgressDrawable loadingDrawable;
    private float loadingT;
    private int minWidth;
    private final Paint paint;
    private Theme.ResourcesProvider resourcesProvider;
    public final View rippleView;
    private boolean showZero;
    public final AnimatedTextView.AnimatedTextDrawable subText;
    private final int subTextAlpha;
    private float subTextT;
    private boolean subTextVisible;
    private ValueAnimator subTextVisibleAnimator;
    public final AnimatedTextView.AnimatedTextDrawable text;
    private Runnable tick;
    private int timerSeconds;
    private boolean withCounterIcon;
    public boolean wrapContentDynamic;
    private boolean wrapWidth;

    public ButtonWithCounterView(Context context, Theme.ResourcesProvider resourcesProvider) {
        this(context, true, resourcesProvider);
    }

    public ButtonWithCounterView(Context context, boolean z, Theme.ResourcesProvider resourcesProvider) {
        super(context);
        CubicBezierInterpolator cubicBezierInterpolator = CubicBezierInterpolator.EASE_OUT_QUINT;
        this.countAlphaAnimated = new AnimatedFloat(350L, cubicBezierInterpolator);
        this.countFilled = true;
        this.timerSeconds = 0;
        this.subTextT = 0.0f;
        this.loadingT = 0.0f;
        this.countScale = 1.0f;
        this.enabledT = 1.0f;
        this.enabled = true;
        this.globalAlpha = 255;
        this.subTextAlpha = 200;
        this.filled = z;
        this.resourcesProvider = resourcesProvider;
        ScaleStateListAnimator.apply(this, 0.02f, 1.2f);
        View view = new View(context);
        this.rippleView = view;
        addView(view, LayoutHelper.createFrame(-1, -1.0f));
        if (z) {
            setBackground(Theme.createRoundRectDrawable(AndroidUtilities.dp(8.0f), Theme.getColor(Theme.key_featuredStickers_addButton, resourcesProvider)));
        }
        Paint paint = new Paint(1);
        this.paint = paint;
        paint.setColor(Theme.getColor(Theme.key_featuredStickers_buttonText, resourcesProvider));
        AnimatedTextView.AnimatedTextDrawable animatedTextDrawable = new AnimatedTextView.AnimatedTextDrawable(true, true, false);
        this.text = animatedTextDrawable;
        animatedTextDrawable.setAnimationProperties(0.3f, 0L, 250L, cubicBezierInterpolator);
        animatedTextDrawable.setCallback(this);
        animatedTextDrawable.setTextSize(AndroidUtilities.dp(14.0f));
        if (z) {
            animatedTextDrawable.setTypeface(AndroidUtilities.bold());
        }
        animatedTextDrawable.setGravity(1);
        AnimatedTextView.AnimatedTextDrawable animatedTextDrawable2 = new AnimatedTextView.AnimatedTextDrawable(subTextSplitToWords(), true, false);
        this.subText = animatedTextDrawable2;
        animatedTextDrawable2.setAnimationProperties(0.3f, 0L, 250L, cubicBezierInterpolator);
        animatedTextDrawable2.setCallback(this);
        animatedTextDrawable2.setTextSize(AndroidUtilities.dp(12.0f));
        animatedTextDrawable2.setGravity(1);
        AnimatedTextView.AnimatedTextDrawable animatedTextDrawable3 = new AnimatedTextView.AnimatedTextDrawable(false, false, true);
        this.countText = animatedTextDrawable3;
        animatedTextDrawable3.setAnimationProperties(0.3f, 0L, 250L, cubicBezierInterpolator);
        animatedTextDrawable3.setCallback(this);
        animatedTextDrawable3.setTextSize(AndroidUtilities.dp(12.0f));
        animatedTextDrawable3.setTypeface(AndroidUtilities.bold());
        animatedTextDrawable3.setText("");
        animatedTextDrawable3.setGravity(1);
        setWillNotDraw(false);
        updateColors();
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
                ButtonWithCounterView.this.lambda$animateCount$4(valueAnimator2);
            }
        });
        this.countAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animator) {
                ButtonWithCounterView.this.countScale = 1.0f;
                ButtonWithCounterView.this.invalidate();
            }
        });
        this.countAnimator.setInterpolator(new OvershootInterpolator(2.0f));
        this.countAnimator.setDuration(200L);
        this.countAnimator.start();
    }

    private void cleanSubTextVisibleAnimator() {
        ValueAnimator valueAnimator = this.subTextVisibleAnimator;
        if (valueAnimator != null) {
            valueAnimator.cancel();
            this.subTextVisibleAnimator = null;
        }
    }

    public void lambda$animateCount$4(ValueAnimator valueAnimator) {
        this.countScale = Math.max(1.0f, ((Float) valueAnimator.getAnimatedValue()).floatValue());
        invalidate();
    }

    public void lambda$setEnabled$5(ValueAnimator valueAnimator) {
        this.enabledT = ((Float) valueAnimator.getAnimatedValue()).floatValue();
        invalidate();
    }

    public void lambda$setLoading$3(ValueAnimator valueAnimator) {
        this.loadingT = ((Float) valueAnimator.getAnimatedValue()).floatValue();
        invalidate();
    }

    public void lambda$setSubText$1(ValueAnimator valueAnimator) {
        this.subTextT = ((Float) valueAnimator.getAnimatedValue()).floatValue();
        invalidate();
    }

    public void lambda$setSubText$2(ValueAnimator valueAnimator) {
        this.subTextT = ((Float) valueAnimator.getAnimatedValue()).floatValue();
        invalidate();
    }

    public void lambda$setTimer$0(Runnable runnable) {
        int i = this.timerSeconds - 1;
        this.timerSeconds = i;
        setCount(i, true);
        if (this.timerSeconds > 0) {
            AndroidUtilities.runOnUIThread(this.tick, 1000L);
            return;
        }
        setClickable(true);
        if (runnable != null) {
            runnable.run();
        }
    }

    protected float calculateCounterWidth(float f, float f2) {
        return f * f2;
    }

    public void disableRippleView() {
        removeView(this.rippleView);
    }

    @Override
    protected boolean drawChild(Canvas canvas, View view, long j) {
        return false;
    }

    @Override
    public boolean isEnabled() {
        return this.enabled;
    }

    @Override
    public boolean isLoading() {
        return this.loading;
    }

    public boolean isTimerActive() {
        return this.timerSeconds > 0;
    }

    @Override
    public void onDraw(Canvas canvas) {
        boolean z;
        this.rippleView.draw(canvas);
        if (this.flickeringLoading) {
            if (this.loading) {
                if (this.flickeringLoadingDrawable == null) {
                    LoadingDrawable loadingDrawable = new LoadingDrawable(this.resourcesProvider);
                    this.flickeringLoadingDrawable = loadingDrawable;
                    loadingDrawable.setCallback(this);
                    this.flickeringLoadingDrawable.setGradientScale(2.0f);
                    this.flickeringLoadingDrawable.setAppearByGradient(true);
                    this.flickeringLoadingDrawable.strokePaint.setStrokeWidth(0.0f);
                    this.flickeringLoadingDrawable.setColors(Theme.multAlpha(-1, 0.02f), Theme.multAlpha(-1, 0.375f));
                }
                this.flickeringLoadingDrawable.resetDisappear();
                this.flickeringLoadingDrawable.setBounds(0, 0, getMeasuredWidth(), getMeasuredHeight());
                this.flickeringLoadingDrawable.setRadiiDp(8.0f);
                this.flickeringLoadingDrawable.draw(canvas);
            } else {
                LoadingDrawable loadingDrawable2 = this.flickeringLoadingDrawable;
                if (loadingDrawable2 != null) {
                    loadingDrawable2.disappear();
                    this.flickeringLoadingDrawable.draw(canvas);
                    if (this.flickeringLoadingDrawable.isDisappeared()) {
                        this.flickeringLoadingDrawable.reset();
                    }
                }
            }
        }
        if (this.loadingT > 0.0f) {
            if (this.loadingDrawable == null) {
                this.loadingDrawable = new CircularProgressDrawable(this.text.getTextColor());
            }
            int dp = (int) ((1.0f - this.loadingT) * AndroidUtilities.dp(24.0f));
            this.loadingDrawable.setBounds(0, dp, getWidth(), getHeight() + dp);
            this.loadingDrawable.setAlpha((int) (this.loadingT * 255.0f));
            this.loadingDrawable.draw(canvas);
            invalidate();
        }
        float f = this.loadingT;
        if (f < 1.0f) {
            if (f != 0.0f) {
                canvas.save();
                canvas.translate(0.0f, (int) (this.loadingT * AndroidUtilities.dp(-24.0f)));
                canvas.scale(1.0f, 1.0f - (this.loadingT * 0.4f));
                z = true;
            } else {
                z = false;
            }
            float currentWidth = this.text.getCurrentWidth();
            float f2 = this.countAlphaAnimated.set(this.countAlpha);
            float dp2 = this.withCounterIcon ? AndroidUtilities.dp(12.0f) : 0.0f;
            float calculateCounterWidth = currentWidth + dp2 + calculateCounterWidth(AndroidUtilities.dp(15.66f) + this.countText.getCurrentWidth(), f2);
            Rect rect = AndroidUtilities.rectTmp2;
            rect.set((int) (((getMeasuredWidth() - calculateCounterWidth) - getWidth()) / 2.0f), (int) (((getMeasuredHeight() - this.text.getHeight()) / 2.0f) - AndroidUtilities.dp(1.0f)), (int) ((((getMeasuredWidth() - calculateCounterWidth) + getWidth()) / 2.0f) + currentWidth), (int) (((getMeasuredHeight() + this.text.getHeight()) / 2.0f) - AndroidUtilities.dp(1.0f)));
            rect.offset(0, (int) ((-AndroidUtilities.dp(7.0f)) * this.subTextT));
            this.text.setAlpha((int) (this.globalAlpha * (1.0f - this.loadingT) * AndroidUtilities.lerp(0.5f, 1.0f, this.enabledT)));
            this.text.setBounds(rect);
            this.text.draw(canvas);
            if (this.subTextVisible) {
                calculateCounterWidth = this.subText.getCurrentWidth();
                rect.set((int) (((getMeasuredWidth() - calculateCounterWidth) - getWidth()) / 2.0f), (int) (((getMeasuredHeight() - this.subText.getHeight()) / 2.0f) - AndroidUtilities.dp(1.0f)), (int) ((((getMeasuredWidth() - calculateCounterWidth) + getWidth()) / 2.0f) + calculateCounterWidth), (int) (((getMeasuredHeight() + this.subText.getHeight()) / 2.0f) - AndroidUtilities.dp(1.0f)));
                rect.offset(0, AndroidUtilities.dp(11.0f));
                canvas.save();
                float lerp = AndroidUtilities.lerp(0.1f, 1.0f, this.subTextT);
                canvas.scale(lerp, lerp, rect.centerX(), rect.bottom);
                this.subText.setAlpha((int) ((1.0f - this.loadingT) * 200.0f * this.subTextT * AndroidUtilities.lerp(0.5f, 1.0f, this.enabledT)));
                this.subText.setBounds(rect);
                this.subText.draw(canvas);
                canvas.restore();
            }
            rect.set((int) (((getMeasuredWidth() - calculateCounterWidth) / 2.0f) + currentWidth + AndroidUtilities.dp(this.countFilled ? 5.0f : 2.0f)), (int) ((getMeasuredHeight() - AndroidUtilities.dp(18.0f)) / 2.0f), (int) (((getMeasuredWidth() - calculateCounterWidth) / 2.0f) + currentWidth + AndroidUtilities.dp((this.countFilled ? 5 : 2) + 8) + Math.max(AndroidUtilities.dp(9.0f), this.countText.getCurrentWidth() + dp2)), (int) ((getMeasuredHeight() + AndroidUtilities.dp(18.0f)) / 2.0f));
            RectF rectF = AndroidUtilities.rectTmp;
            rectF.set(rect);
            if (this.countScale != 1.0f) {
                canvas.save();
                float f3 = this.countScale;
                canvas.scale(f3, f3, rect.centerX(), rect.centerY());
            }
            if (this.countFilled) {
                this.paint.setAlpha((int) (this.globalAlpha * (1.0f - this.loadingT) * f2 * f2 * AndroidUtilities.lerp(0.5f, 1.0f, this.enabledT)));
                float dp3 = AndroidUtilities.dp(this.withCounterIcon ? 4.0f : 10.0f);
                canvas.drawRoundRect(rectF, dp3, dp3, this.paint);
            }
            rect.offset(-AndroidUtilities.dp((this.countText.getText() != null ? this.countText.getText().length() : 0) > 1 ? 0.3f : 0.0f), -AndroidUtilities.dp(0.4f));
            this.countText.setAlpha((int) (this.globalAlpha * (1.0f - this.loadingT) * f2 * (this.countFilled ? 1.0f : 0.5f)));
            this.countText.setBounds(rect);
            canvas.save();
            if (this.countFilled && this.withCounterIcon) {
                this.counterDrawable.setAlpha((int) (this.globalAlpha * (1.0f - this.loadingT) * f2 * 1.0f));
                this.counterDrawable.setBounds(AndroidUtilities.dp(1.0f) + rect.left, AndroidUtilities.dp(2.0f) + rect.top, AndroidUtilities.dp(1.0f) + rect.left + this.counterDrawable.getIntrinsicWidth(), AndroidUtilities.dp(2.0f) + rect.top + this.counterDrawable.getIntrinsicHeight());
                this.counterDrawable.draw(canvas);
                canvas.translate(dp2 / 2.0f, 0.0f);
            }
            this.countText.draw(canvas);
            canvas.restore();
            if (this.countScale != 1.0f) {
                canvas.restore();
            }
            if (z) {
                canvas.restore();
            }
        }
    }

    @Override
    public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo accessibilityNodeInfo) {
        super.onInitializeAccessibilityNodeInfo(accessibilityNodeInfo);
        accessibilityNodeInfo.setClassName("android.widget.Button");
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent motionEvent) {
        if (this.filled && isClickable() && getParent() != null) {
            getParent().requestDisallowInterceptTouchEvent(true);
        }
        return super.onInterceptTouchEvent(motionEvent);
    }

    @Override
    public void onMeasure(int i, int i2) {
        if (this.wrapWidth) {
            i = View.MeasureSpec.makeMeasureSpec((int) Math.min(Math.max(getPaddingLeft() + this.text.getCurrentWidth() + getPaddingRight(), this.minWidth), View.MeasureSpec.getSize(i)), 1073741824);
        }
        super.onMeasure(i, i2);
    }

    public void setColor(int i) {
        if (this.filled) {
            setBackground(Theme.createRoundRectDrawable(AndroidUtilities.dp(8.0f), i));
        }
    }

    public void setCount(int i, boolean z) {
        int i2;
        if (z) {
            this.countText.cancelAnimation();
        }
        if (z && i != (i2 = this.lastCount) && i > 0 && i2 > 0) {
            animateCount();
        }
        this.lastCount = i;
        this.countAlpha = (i != 0 || this.showZero) ? 1.0f : 0.0f;
        this.countText.setText(LocaleController.formatNumber(i, ' '), z);
        invalidate();
    }

    public void setCountFilled(boolean z) {
        this.countFilled = z;
        this.countText.setTextSize(AndroidUtilities.dp(z ? 12.0f : 14.0f));
        this.countText.setTextColor(this.countFilled ? Theme.getColor(Theme.key_featuredStickers_addButton, this.resourcesProvider) : this.text.getTextColor());
    }

    public void setCounterColor(int i) {
        this.countText.setTextColor(i);
        this.counterDrawable.setColorFilter(new PorterDuffColorFilter(i, PorterDuff.Mode.SRC_IN));
    }

    @Override
    public void setEnabled(boolean z) {
        if (this.enabled != z) {
            ValueAnimator valueAnimator = this.enabledAnimator;
            if (valueAnimator != null) {
                valueAnimator.cancel();
                this.enabledAnimator = null;
            }
            float f = this.enabledT;
            this.enabled = z;
            ValueAnimator ofFloat = ValueAnimator.ofFloat(f, z ? 1.0f : 0.0f);
            this.enabledAnimator = ofFloat;
            ofFloat.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public final void onAnimationUpdate(ValueAnimator valueAnimator2) {
                    ButtonWithCounterView.this.lambda$setEnabled$5(valueAnimator2);
                }
            });
            this.enabledAnimator.start();
        }
        super.setEnabled(z);
    }

    public void setFlickeringLoading(boolean z) {
        this.flickeringLoading = z;
    }

    public void setGlobalAlpha(float f) {
        this.globalAlpha = (int) (f * 255.0f);
    }

    @Override
    public void setLoading(final boolean z) {
        if (this.loading != z) {
            if (this.flickeringLoading) {
                this.loading = z;
                invalidate();
                return;
            }
            ValueAnimator valueAnimator = this.loadingAnimator;
            if (valueAnimator != null) {
                valueAnimator.cancel();
                this.loadingAnimator = null;
            }
            float f = this.loadingT;
            this.loading = z;
            ValueAnimator ofFloat = ValueAnimator.ofFloat(f, z ? 1.0f : 0.0f);
            this.loadingAnimator = ofFloat;
            ofFloat.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public final void onAnimationUpdate(ValueAnimator valueAnimator2) {
                    ButtonWithCounterView.this.lambda$setLoading$3(valueAnimator2);
                }
            });
            this.loadingAnimator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animator) {
                    ButtonWithCounterView.this.loadingT = z ? 1.0f : 0.0f;
                    ButtonWithCounterView.this.invalidate();
                }
            });
            this.loadingAnimator.setDuration(320L);
            this.loadingAnimator.setInterpolator(CubicBezierInterpolator.EASE_OUT_QUINT);
            this.loadingAnimator.start();
        }
    }

    public void setMinWidth(int i) {
        this.wrapWidth = true;
        this.minWidth = i;
    }

    public void setShowZero(boolean z) {
        this.showZero = z;
    }

    public void setSubText(CharSequence charSequence, boolean z) {
        boolean z2 = charSequence != null;
        if (z) {
            this.subText.cancelAnimation();
        }
        setContentDescription(charSequence);
        invalidate();
        if (!this.subTextVisible || z2) {
            this.subText.setText(charSequence, z);
        } else {
            cleanSubTextVisibleAnimator();
            ValueAnimator ofFloat = ValueAnimator.ofFloat(this.subTextT, 0.0f);
            this.subTextVisibleAnimator = ofFloat;
            ofFloat.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                    ButtonWithCounterView.this.lambda$setSubText$1(valueAnimator);
                }
            });
            this.subTextVisibleAnimator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animator) {
                    ButtonWithCounterView.this.subTextVisible = false;
                    ButtonWithCounterView.this.subText.setText(null, false);
                }
            });
            this.subTextVisibleAnimator.setDuration(200L);
            this.subTextVisibleAnimator.setInterpolator(CubicBezierInterpolator.DEFAULT);
            this.subTextVisibleAnimator.start();
        }
        if (this.subTextVisible || !z2) {
            return;
        }
        this.subTextVisible = true;
        cleanSubTextVisibleAnimator();
        ValueAnimator ofFloat2 = ValueAnimator.ofFloat(this.subTextT, 1.0f);
        this.subTextVisibleAnimator = ofFloat2;
        ofFloat2.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                ButtonWithCounterView.this.lambda$setSubText$2(valueAnimator);
            }
        });
        this.subTextVisibleAnimator.setDuration(200L);
        this.subTextVisibleAnimator.setInterpolator(CubicBezierInterpolator.DEFAULT);
        this.subTextVisibleAnimator.start();
    }

    public void setText(CharSequence charSequence, boolean z) {
        setText(charSequence, z, true);
    }

    public void setText(CharSequence charSequence, boolean z, boolean z2) {
        if (z) {
            this.text.cancelAnimation();
        }
        this.text.setText(charSequence, z, z2);
        setContentDescription(charSequence);
        invalidate();
    }

    public void setTextAlpha(float f) {
        this.text.setAlpha((int) (f * 255.0f));
    }

    public void setTextColor(int i) {
        this.text.setTextColor(i);
        if (this.filled) {
            return;
        }
        this.rippleView.setBackground(Theme.createRadSelectorDrawable(Theme.multAlpha(this.text.getTextColor(), 0.1f), 8, 8));
    }

    public void setTimer(int i, final Runnable runnable) {
        AndroidUtilities.cancelRunOnUIThread(this.tick);
        setCountFilled(false);
        this.timerSeconds = i;
        setCount(i, false);
        setShowZero(false);
        Runnable runnable2 = new Runnable() {
            @Override
            public final void run() {
                ButtonWithCounterView.this.lambda$setTimer$0(runnable);
            }
        };
        this.tick = runnable2;
        AndroidUtilities.runOnUIThread(runnable2, 1000L);
    }

    protected boolean subTextSplitToWords() {
        return true;
    }

    public void updateColors() {
        View view;
        int multAlpha;
        this.text.setTextColor(Theme.getColor(this.filled ? Theme.key_featuredStickers_buttonText : Theme.key_featuredStickers_addButton, this.resourcesProvider));
        if (this.filled) {
            view = this.rippleView;
            multAlpha = Theme.getColor(Theme.key_listSelector, this.resourcesProvider);
        } else {
            view = this.rippleView;
            multAlpha = Theme.multAlpha(this.text.getTextColor(), 0.1f);
        }
        view.setBackground(Theme.createRadSelectorDrawable(multAlpha, 8, 8));
        this.subText.setTextColor(Theme.getColor(this.filled ? Theme.key_featuredStickers_buttonText : Theme.key_featuredStickers_addButton, this.resourcesProvider));
        this.countText.setTextColor(Theme.getColor(Theme.key_featuredStickers_addButton, this.resourcesProvider));
    }

    public void updateColors(Theme.ResourcesProvider resourcesProvider) {
        this.resourcesProvider = resourcesProvider;
        updateColors();
    }

    @Override
    protected boolean verifyDrawable(Drawable drawable) {
        return this.flickeringLoadingDrawable == drawable || this.text == drawable || this.subText == drawable || this.countText == drawable || super.verifyDrawable(drawable);
    }

    public void withCounterIcon() {
        this.withCounterIcon = true;
        Drawable mutate = ContextCompat.getDrawable(getContext(), R.drawable.mini_boost_button).mutate();
        this.counterDrawable = mutate;
        mutate.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_featuredStickers_addButton, this.resourcesProvider), PorterDuff.Mode.SRC_IN));
    }

    public void wrapContentDynamic() {
        this.wrapContentDynamic = true;
    }
}
