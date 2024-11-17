package org.telegram.ui.Components.voip;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.util.Property;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;
import androidx.core.graphics.ColorUtils;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.R;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.CubicBezierInterpolator;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.RLottieImageView;
import org.telegram.ui.Components.voip.RateCallLayout;

public class RateCallLayout extends FrameLayout {
    private final VoIPBackgroundProvider backgroundProvider;
    private OnRateSelected onRateSelected;
    private final RateCallContainer rateCallContainer;
    private final FrameLayout starsContainer;
    private final StarContainer[] startsViews;

    public interface OnRateSelected {
        void onRateSelected(int i);
    }

    public static class RateCallContainer extends FrameLayout {
        private final VoIPBackgroundProvider backgroundProvider;
        private final RectF bgRect;
        private final TextView messageTextView;
        private final TextView titleTextView;

        public RateCallContainer(Context context, VoIPBackgroundProvider voIPBackgroundProvider) {
            super(context);
            this.bgRect = new RectF();
            this.backgroundProvider = voIPBackgroundProvider;
            voIPBackgroundProvider.attach(this);
            setWillNotDraw(false);
            TextView textView = new TextView(context);
            this.titleTextView = textView;
            textView.setTextColor(-1);
            textView.setText(LocaleController.getString(R.string.VoipRateCallTitle));
            textView.setTextSize(1, 15.0f);
            textView.setGravity(1);
            textView.setTypeface(AndroidUtilities.bold());
            TextView textView2 = new TextView(context);
            this.messageTextView = textView2;
            textView2.setTextSize(1, 15.0f);
            textView2.setTextColor(-1);
            textView2.setGravity(1);
            textView2.setText(LocaleController.getString(R.string.VoipRateCallDescription));
            addView(textView, LayoutHelper.createFrame(-1, -2.0f, 3, 0.0f, 24.0f, 0.0f, 0.0f));
            addView(textView2, LayoutHelper.createFrame(-1, -2.0f, 3, 0.0f, 50.0f, 0.0f, 0.0f));
        }

        @Override
        protected void dispatchDraw(Canvas canvas) {
            this.bgRect.set(0.0f, 0.0f, getWidth(), getHeight());
            this.backgroundProvider.setDarkTranslation(getX() + ((View) getParent()).getX(), getY() + ((View) getParent()).getY());
            canvas.drawRoundRect(this.bgRect, AndroidUtilities.dp(28.0f), AndroidUtilities.dp(28.0f), this.backgroundProvider.getDarkPaint());
            super.dispatchDraw(canvas);
        }
    }

    public static class StarContainer extends FrameLayout {
        private AllStarsProvider allStarsProvider;
        public RLottieImageView defaultStar;
        private OnSelectedStar onSelectedStar;
        private int pos;
        private final Drawable rippleDrawable;
        public RLottieImageView selectedStar;

        public interface AllStarsProvider {
            StarContainer[] getAllStartsViews();
        }

        public interface OnSelectedStar {
            void onSelected(float f, float f2, int i);
        }

        public StarContainer(Context context) {
            super(context);
            this.pos = 0;
            setWillNotDraw(false);
            this.defaultStar = new RLottieImageView(context);
            this.selectedStar = new RLottieImageView(context);
            this.defaultStar.setAnimation(R.raw.star_stroke, 37, 37);
            this.selectedStar.setAnimation(R.raw.star_fill, 37, 37);
            this.selectedStar.setAlpha(0.0f);
            addView(this.defaultStar, LayoutHelper.createFrame(37, 37.0f));
            addView(this.selectedStar, LayoutHelper.createFrame(37, 37.0f));
            Drawable createSimpleSelectorCircleDrawable = Theme.createSimpleSelectorCircleDrawable(AndroidUtilities.dp(37.0f), 0, ColorUtils.setAlphaComponent(-1, 76));
            this.rippleDrawable = createSimpleSelectorCircleDrawable;
            createSimpleSelectorCircleDrawable.setCallback(this);
            setClickable(true);
        }

        @Override
        public boolean dispatchTouchEvent(MotionEvent motionEvent) {
            int i;
            AllStarsProvider allStarsProvider;
            int action = motionEvent.getAction();
            int i2 = 0;
            if (action == 0) {
                AllStarsProvider allStarsProvider2 = this.allStarsProvider;
                if (allStarsProvider2 != null) {
                    StarContainer[] allStartsViews = allStarsProvider2.getAllStartsViews();
                    while (true) {
                        i = this.pos;
                        if (i2 > i) {
                            break;
                        }
                        StarContainer starContainer = allStartsViews[i2];
                        RLottieImageView rLottieImageView = starContainer.defaultStar;
                        RLottieImageView rLottieImageView2 = starContainer.selectedStar;
                        rLottieImageView.animate().alpha(0.0f).scaleX(0.8f).scaleY(0.8f).setDuration(250L).start();
                        rLottieImageView2.animate().alpha(1.0f).scaleX(0.8f).scaleY(0.8f).setDuration(250L).start();
                        i2++;
                    }
                    for (int i3 = i + 1; i3 < allStartsViews.length; i3++) {
                        StarContainer starContainer2 = allStartsViews[i3];
                        RLottieImageView rLottieImageView3 = starContainer2.defaultStar;
                        RLottieImageView rLottieImageView4 = starContainer2.selectedStar;
                        rLottieImageView3.animate().alpha(1.0f).scaleX(1.0f).scaleY(1.0f).setDuration(250L).start();
                        rLottieImageView4.animate().alpha(0.0f).scaleX(1.0f).scaleY(1.0f).setDuration(250L).start();
                    }
                }
            } else if (action == 1) {
                AllStarsProvider allStarsProvider3 = this.allStarsProvider;
                if (allStarsProvider3 != null) {
                    StarContainer[] allStartsViews2 = allStarsProvider3.getAllStartsViews();
                    for (int i4 = 0; i4 <= this.pos; i4++) {
                        StarContainer starContainer3 = allStartsViews2[i4];
                        RLottieImageView rLottieImageView5 = starContainer3.defaultStar;
                        RLottieImageView rLottieImageView6 = starContainer3.selectedStar;
                        rLottieImageView5.animate().scaleX(1.0f).scaleY(1.0f).setDuration(250L).start();
                        rLottieImageView6.animate().scaleX(1.0f).scaleY(1.0f).setDuration(250L).start();
                    }
                }
                if (this.onSelectedStar != null) {
                    getLocationOnScreen(new int[2]);
                    this.onSelectedStar.onSelected(r0[0] + (getWidth() / 2.0f), r0[1] + (getHeight() / 2.0f), this.pos + 1);
                }
            } else if (action == 3 && (allStarsProvider = this.allStarsProvider) != null) {
                StarContainer[] allStartsViews3 = allStarsProvider.getAllStartsViews();
                int length = allStartsViews3.length;
                while (i2 < length) {
                    StarContainer starContainer4 = allStartsViews3[i2];
                    RLottieImageView rLottieImageView7 = starContainer4.defaultStar;
                    RLottieImageView rLottieImageView8 = starContainer4.selectedStar;
                    rLottieImageView7.animate().alpha(1.0f).scaleX(1.0f).scaleY(1.0f).setDuration(250L).start();
                    rLottieImageView8.animate().alpha(0.0f).scaleX(1.0f).scaleY(1.0f).setDuration(250L).start();
                    i2++;
                }
            }
            return super.dispatchTouchEvent(motionEvent);
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
            super.onDraw(canvas);
            this.rippleDrawable.setBounds(0, 0, getWidth(), getHeight());
            this.rippleDrawable.draw(canvas);
        }

        public void setAllStarsProvider(AllStarsProvider allStarsProvider) {
            this.allStarsProvider = allStarsProvider;
        }

        public void setOnSelectedStar(OnSelectedStar onSelectedStar, int i) {
            this.onSelectedStar = onSelectedStar;
            this.pos = i;
        }

        @Override
        public boolean verifyDrawable(Drawable drawable) {
            return this.rippleDrawable == drawable || super.verifyDrawable(drawable);
        }
    }

    public RateCallLayout(final Context context, VoIPBackgroundProvider voIPBackgroundProvider) {
        super(context);
        this.startsViews = new StarContainer[5];
        this.backgroundProvider = voIPBackgroundProvider;
        setWillNotDraw(false);
        RateCallContainer rateCallContainer = new RateCallContainer(context, voIPBackgroundProvider);
        this.rateCallContainer = rateCallContainer;
        FrameLayout frameLayout = new FrameLayout(context);
        this.starsContainer = frameLayout;
        rateCallContainer.setVisibility(8);
        frameLayout.setVisibility(8);
        for (int i = 0; i < 5; i++) {
            this.startsViews[i] = new StarContainer(context);
            this.startsViews[i].setAllStarsProvider(new StarContainer.AllStarsProvider() {
                @Override
                public final RateCallLayout.StarContainer[] getAllStartsViews() {
                    RateCallLayout.StarContainer[] lambda$new$0;
                    lambda$new$0 = RateCallLayout.this.lambda$new$0();
                    return lambda$new$0;
                }
            });
            this.startsViews[i].setOnSelectedStar(new StarContainer.OnSelectedStar() {
                @Override
                public final void onSelected(float f, float f2, int i2) {
                    RateCallLayout.this.lambda$new$3(context, f, f2, i2);
                }
            }, i);
            this.starsContainer.addView(this.startsViews[i], LayoutHelper.createFrame(-2, -2.0f, 51, i * 41, 0.0f, 0.0f, 0.0f));
        }
        addView(this.rateCallContainer, LayoutHelper.createFrame(300, 152.0f, 49, 0.0f, 0.0f, 0.0f, 0.0f));
        addView(this.starsContainer, LayoutHelper.createFrame(201, 100.0f, 49, 0.0f, 90.0f, 0.0f, 0.0f));
    }

    public StarContainer[] lambda$new$0() {
        return this.startsViews;
    }

    public void lambda$new$1(RLottieImageView rLottieImageView) {
        removeView(rLottieImageView);
    }

    public void lambda$new$2(final RLottieImageView rLottieImageView) {
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                RateCallLayout.this.lambda$new$1(rLottieImageView);
            }
        });
    }

    public void lambda$new$3(Context context, float f, float f2, int i) {
        if (i >= 4) {
            final RLottieImageView rLottieImageView = new RLottieImageView(context);
            int dp = AndroidUtilities.dp(133.0f);
            rLottieImageView.setAnimation(R.raw.rate, 133, 133);
            int[] iArr = new int[2];
            getLocationOnScreen(iArr);
            int i2 = iArr[0];
            int i3 = iArr[1];
            addView(rLottieImageView, LayoutHelper.createFrame(133, 133.0f));
            float f3 = dp / 2.0f;
            rLottieImageView.setTranslationX((f - i2) - f3);
            rLottieImageView.setTranslationY((f2 - i3) - f3);
            rLottieImageView.setOnAnimationEndListener(new Runnable() {
                @Override
                public final void run() {
                    RateCallLayout.this.lambda$new$2(rLottieImageView);
                }
            });
            rLottieImageView.playAnimation();
        }
        OnRateSelected onRateSelected = this.onRateSelected;
        if (onRateSelected != null) {
            onRateSelected.onRateSelected(i);
        }
    }

    public void show(OnRateSelected onRateSelected) {
        this.onRateSelected = onRateSelected;
        this.rateCallContainer.setVisibility(0);
        this.starsContainer.setVisibility(0);
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(ObjectAnimator.ofFloat(this.rateCallContainer, (Property<RateCallContainer, Float>) View.ALPHA, 0.0f, 1.0f), ObjectAnimator.ofFloat(this.rateCallContainer, (Property<RateCallContainer, Float>) View.SCALE_X, 0.7f, 1.0f), ObjectAnimator.ofFloat(this.rateCallContainer, (Property<RateCallContainer, Float>) View.SCALE_Y, 0.7f, 1.0f), ObjectAnimator.ofFloat(this.rateCallContainer, (Property<RateCallContainer, Float>) View.TRANSLATION_Y, AndroidUtilities.dp(24.0f), 0.0f));
        animatorSet.setInterpolator(CubicBezierInterpolator.DEFAULT);
        animatorSet.setDuration(250L);
        for (int i = 0; i < this.startsViews.length; i++) {
            AnimatorSet animatorSet2 = new AnimatorSet();
            this.startsViews[i].setAlpha(0.0f);
            animatorSet2.playTogether(ObjectAnimator.ofFloat(this.startsViews[i], (Property<StarContainer, Float>) View.ALPHA, 0.0f, 1.0f), ObjectAnimator.ofFloat(this.startsViews[i], (Property<StarContainer, Float>) View.SCALE_X, 0.3f, 1.0f), ObjectAnimator.ofFloat(this.startsViews[i], (Property<StarContainer, Float>) View.SCALE_Y, 0.3f, 1.0f), ObjectAnimator.ofFloat(this.startsViews[i], (Property<StarContainer, Float>) View.TRANSLATION_Y, AndroidUtilities.dp(30.0f), 0.0f));
            animatorSet2.setDuration(250L);
            animatorSet2.setStartDelay(i * 16);
            animatorSet2.start();
        }
        animatorSet.start();
    }
}
