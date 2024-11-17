package org.telegram.ui.Components.voip;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ArgbEvaluator;
import android.animation.TimeInterpolator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.transition.ChangeBounds;
import android.transition.TransitionManager;
import android.transition.TransitionSet;
import android.transition.TransitionValues;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import androidx.core.content.ContextCompat;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.R;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.CubicBezierInterpolator;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.voip.EndCloseLayout;

public class EndCloseLayout extends FrameLayout {
    private final EndCloseView endCloseView;
    private boolean isClosedState;
    private final TransitionSet transitionSet;

    class AnonymousClass1 extends ChangeBounds {
        AnonymousClass1() {
        }

        public static void lambda$createAnimator$0(TransitionValues transitionValues, ValueAnimator valueAnimator) {
            ((EndCloseView) transitionValues.view).backColor = ((Integer) valueAnimator.getAnimatedValue()).intValue();
        }

        public static void lambda$createAnimator$1(TransitionValues transitionValues, ValueAnimator valueAnimator) {
            ((EndCloseView) transitionValues.view).round = ((Integer) valueAnimator.getAnimatedValue()).intValue();
        }

        public static void lambda$createAnimator$2(TransitionValues transitionValues, ValueAnimator valueAnimator) {
            ((EndCloseView) transitionValues.view).callDeclineAlpha = ((Integer) valueAnimator.getAnimatedValue()).intValue();
        }

        public static void lambda$createAnimator$3(TransitionValues transitionValues, ValueAnimator valueAnimator) {
            ((EndCloseView) transitionValues.view).closeTextAlpha = ((Integer) valueAnimator.getAnimatedValue()).intValue();
        }

        @Override
        public void captureEndValues(TransitionValues transitionValues) {
            super.captureEndValues(transitionValues);
            View view = transitionValues.view;
            if (view instanceof EndCloseView) {
                EndCloseView endCloseView = (EndCloseView) view;
                int i = endCloseView.backColor;
                int i2 = endCloseView.round;
                int i3 = endCloseView.callDeclineAlpha;
                int i4 = endCloseView.closeTextAlpha;
                transitionValues.values.put("back_color_end_close", Integer.valueOf(i));
                transitionValues.values.put("round_end_close", Integer.valueOf(i2));
                transitionValues.values.put("decline_call_alpha_end_close", Integer.valueOf(i3));
                transitionValues.values.put("close_text_alpha_end_close", Integer.valueOf(i4));
            }
        }

        @Override
        public void captureStartValues(TransitionValues transitionValues) {
            super.captureStartValues(transitionValues);
            View view = transitionValues.view;
            if (view instanceof EndCloseView) {
                EndCloseView endCloseView = (EndCloseView) view;
                int i = endCloseView.backColor;
                int i2 = endCloseView.round;
                int i3 = endCloseView.callDeclineAlpha;
                int i4 = endCloseView.closeTextAlpha;
                transitionValues.values.put("back_color_end_close", Integer.valueOf(i));
                transitionValues.values.put("round_end_close", Integer.valueOf(i2));
                transitionValues.values.put("decline_call_alpha_end_close", Integer.valueOf(i3));
                transitionValues.values.put("close_text_alpha_end_close", Integer.valueOf(i4));
            }
        }

        @Override
        public Animator createAnimator(ViewGroup viewGroup, final TransitionValues transitionValues, TransitionValues transitionValues2) {
            if (transitionValues == null || transitionValues2 == null || !(transitionValues.view instanceof EndCloseView)) {
                return super.createAnimator(viewGroup, transitionValues, transitionValues2);
            }
            AnimatorSet animatorSet = new AnimatorSet();
            Animator createAnimator = super.createAnimator(viewGroup, transitionValues, transitionValues2);
            if (createAnimator != null) {
                animatorSet.playTogether(createAnimator);
            }
            Integer num = (Integer) transitionValues.values.get("back_color_end_close");
            Integer num2 = (Integer) transitionValues2.values.get("back_color_end_close");
            Integer num3 = (Integer) transitionValues.values.get("round_end_close");
            Integer num4 = (Integer) transitionValues2.values.get("round_end_close");
            Integer num5 = (Integer) transitionValues.values.get("decline_call_alpha_end_close");
            Integer num6 = (Integer) transitionValues2.values.get("decline_call_alpha_end_close");
            Integer num7 = (Integer) transitionValues.values.get("close_text_alpha_end_close");
            Integer num8 = (Integer) transitionValues2.values.get("close_text_alpha_end_close");
            ValueAnimator valueAnimator = new ValueAnimator();
            valueAnimator.setIntValues(num.intValue(), num2.intValue());
            valueAnimator.setEvaluator(new ArgbEvaluator());
            valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public final void onAnimationUpdate(ValueAnimator valueAnimator2) {
                    EndCloseLayout.AnonymousClass1.lambda$createAnimator$0(transitionValues, valueAnimator2);
                }
            });
            animatorSet.playTogether(valueAnimator);
            ValueAnimator ofInt = ValueAnimator.ofInt(num3.intValue(), num4.intValue());
            ofInt.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public final void onAnimationUpdate(ValueAnimator valueAnimator2) {
                    EndCloseLayout.AnonymousClass1.lambda$createAnimator$1(transitionValues, valueAnimator2);
                }
            });
            animatorSet.playTogether(ofInt);
            ValueAnimator ofInt2 = ValueAnimator.ofInt(num5.intValue(), num6.intValue(), num6.intValue(), num6.intValue(), num6.intValue(), num6.intValue(), num6.intValue());
            ofInt2.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public final void onAnimationUpdate(ValueAnimator valueAnimator2) {
                    EndCloseLayout.AnonymousClass1.lambda$createAnimator$2(transitionValues, valueAnimator2);
                }
            });
            animatorSet.playTogether(ofInt2);
            ValueAnimator ofInt3 = ValueAnimator.ofInt(num7.intValue(), num7.intValue(), (int) (num8.intValue() * 0.25f), (int) (num8.intValue() * 0.5f), (int) (num8.intValue() * 0.75f), num8.intValue());
            ofInt3.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public final void onAnimationUpdate(ValueAnimator valueAnimator2) {
                    EndCloseLayout.AnonymousClass1.lambda$createAnimator$3(transitionValues, valueAnimator2);
                }
            });
            animatorSet.playTogether(ofInt3);
            animatorSet.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animator) {
                    super.onAnimationEnd(animator);
                    transitionValues.view.setEnabled(true);
                }

                @Override
                public void onAnimationStart(Animator animator) {
                    super.onAnimationStart(animator);
                    transitionValues.view.setEnabled(false);
                }
            });
            return animatorSet;
        }
    }

    public static class EndCloseView extends View {
        public int backColor;
        private final Paint backgroundPaint;
        private final RectF backgroundRect;
        public int callDeclineAlpha;
        private final Drawable callDeclineDrawable;
        private final String closeText;
        public int closeTextAlpha;
        private Drawable rippleDrawable;
        public int round;
        private final Paint textPaint;
        private final Paint textPaintMask;

        public EndCloseView(Context context) {
            super(context);
            this.backgroundPaint = new Paint(1);
            Paint paint = new Paint(1);
            this.textPaintMask = paint;
            Paint paint2 = new Paint(1);
            this.textPaint = paint2;
            this.backgroundRect = new RectF();
            this.backColor = -761748;
            this.round = AndroidUtilities.dp(26.0f);
            this.callDeclineAlpha = 255;
            this.closeTextAlpha = 0;
            Drawable mutate = ContextCompat.getDrawable(getContext(), R.drawable.calls_decline).mutate();
            this.callDeclineDrawable = mutate;
            mutate.setColorFilter(new PorterDuffColorFilter(-1, PorterDuff.Mode.MULTIPLY));
            paint.setTextSize(AndroidUtilities.dp(18.0f));
            paint.setTypeface(AndroidUtilities.bold());
            Paint.Align align = Paint.Align.CENTER;
            paint.setTextAlign(align);
            paint.setColor(-16777216);
            paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_OUT));
            paint2.setTextSize(AndroidUtilities.dp(18.0f));
            paint2.setTypeface(AndroidUtilities.bold());
            paint2.setTextAlign(align);
            paint2.setColor(-16777216);
            setLayerType(2, null);
            setClickable(true);
            this.closeText = LocaleController.getString(R.string.Close);
        }

        @Override
        public boolean dispatchTouchEvent(MotionEvent motionEvent) {
            if (isEnabled()) {
                return super.dispatchTouchEvent(motionEvent);
            }
            return false;
        }

        @Override
        protected void drawableStateChanged() {
            super.drawableStateChanged();
            Drawable drawable = this.rippleDrawable;
            if (drawable != null) {
                drawable.setState(getDrawableState());
            }
        }

        @Override
        public void jumpDrawablesToCurrentState() {
            super.jumpDrawablesToCurrentState();
            Drawable drawable = this.rippleDrawable;
            if (drawable != null) {
                drawable.jumpToCurrentState();
            }
        }

        @Override
        protected void onDraw(Canvas canvas) {
            float width = getWidth() / 2.0f;
            float height = getHeight() / 2.0f;
            this.backgroundPaint.setColor(this.backColor);
            this.backgroundRect.set(0.0f, 0.0f, getWidth(), getHeight());
            RectF rectF = this.backgroundRect;
            float f = this.round;
            canvas.drawRoundRect(rectF, f, f, this.backgroundPaint);
            this.callDeclineDrawable.setBounds((int) (width - (r3.getIntrinsicWidth() / 2.0f)), (int) (height - (this.callDeclineDrawable.getIntrinsicHeight() / 2)), (int) ((this.callDeclineDrawable.getIntrinsicWidth() / 2) + width), (int) ((this.callDeclineDrawable.getIntrinsicHeight() / 2) + height));
            this.callDeclineDrawable.setAlpha(this.callDeclineAlpha);
            this.callDeclineDrawable.draw(canvas);
            this.textPaintMask.setAlpha(this.closeTextAlpha);
            this.textPaint.setAlpha((this.closeTextAlpha / 255) * 38);
            canvas.drawText(this.closeText, width, AndroidUtilities.dp(6.0f) + height, this.textPaintMask);
            canvas.drawText(this.closeText, width, height + AndroidUtilities.dp(6.0f), this.textPaint);
            if (this.rippleDrawable == null) {
                Drawable createRadSelectorDrawable = Theme.createRadSelectorDrawable(Theme.getColor(Theme.key_listSelector), 8, 8);
                this.rippleDrawable = createRadSelectorDrawable;
                createRadSelectorDrawable.setCallback(this);
            }
            this.rippleDrawable.setBounds(0, 0, getWidth(), getHeight());
            this.rippleDrawable.draw(canvas);
        }

        @Override
        public boolean verifyDrawable(Drawable drawable) {
            return this.rippleDrawable == drawable || super.verifyDrawable(drawable);
        }
    }

    public EndCloseLayout(Context context) {
        super(context);
        this.isClosedState = false;
        setWillNotDraw(false);
        EndCloseView endCloseView = new EndCloseView(context);
        this.endCloseView = endCloseView;
        addView(endCloseView, LayoutHelper.createFrame(52, 52, 5));
        TransitionSet transitionSet = new TransitionSet();
        this.transitionSet = transitionSet;
        transitionSet.setOrdering(0);
        transitionSet.addTransition(new AnonymousClass1());
        transitionSet.setDuration(500L);
        transitionSet.setInterpolator((TimeInterpolator) CubicBezierInterpolator.DEFAULT);
    }

    public void lambda$switchToClose$0(View.OnClickListener onClickListener) {
        this.endCloseView.setOnClickListener(onClickListener);
    }

    public EndCloseView getEndCloseView() {
        return this.endCloseView;
    }

    public void switchToClose(final View.OnClickListener onClickListener, boolean z) {
        if (this.isClosedState) {
            return;
        }
        this.isClosedState = true;
        if (z) {
            TransitionManager.beginDelayedTransition(this, this.transitionSet);
        }
        EndCloseView endCloseView = this.endCloseView;
        endCloseView.closeTextAlpha = 255;
        endCloseView.backColor = -1;
        endCloseView.callDeclineAlpha = 0;
        endCloseView.round = AndroidUtilities.dp(8.0f);
        ViewGroup.LayoutParams layoutParams = this.endCloseView.getLayoutParams();
        layoutParams.width = -1;
        this.endCloseView.setLayoutParams(layoutParams);
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                EndCloseLayout.this.lambda$switchToClose$0(onClickListener);
            }
        }, 500L);
    }
}
