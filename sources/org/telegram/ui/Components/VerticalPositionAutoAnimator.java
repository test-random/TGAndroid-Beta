package org.telegram.ui.Components;

import android.view.View;
import androidx.dynamicanimation.animation.DynamicAnimation;
import androidx.dynamicanimation.animation.SpringAnimation;
import org.telegram.messenger.AndroidUtilities;

public final class VerticalPositionAutoAnimator {
    private final AnimatorLayoutChangeListener animatorLayoutChangeListener;
    private SpringAnimation floatingButtonAnimator;
    private View floatingButtonView;
    private float offsetY;

    private class AnimatorLayoutChangeListener implements View.OnLayoutChangeListener {
        private boolean ignoreNextLayout;
        private Boolean orientation;

        public AnimatorLayoutChangeListener(View view, float f) {
            VerticalPositionAutoAnimator.this.floatingButtonAnimator = new SpringAnimation(view, DynamicAnimation.TRANSLATION_Y, VerticalPositionAutoAnimator.this.offsetY);
            VerticalPositionAutoAnimator.this.floatingButtonAnimator.getSpring().setDampingRatio(1.0f);
            VerticalPositionAutoAnimator.this.floatingButtonAnimator.getSpring().setStiffness(f);
        }

        private void checkOrientation() {
            android.graphics.Point point = AndroidUtilities.displaySize;
            boolean z = point.x > point.y;
            Boolean bool = this.orientation;
            if (bool == null || bool.booleanValue() != z) {
                this.orientation = Boolean.valueOf(z);
                this.ignoreNextLayout = true;
            }
        }

        @Override
        public void onLayoutChange(View view, int i, int i2, int i3, int i4, int i5, int i6, int i7, int i8) {
            checkOrientation();
            if (i6 == 0 || i6 == i2 || this.ignoreNextLayout) {
                this.ignoreNextLayout = false;
                return;
            }
            VerticalPositionAutoAnimator.this.floatingButtonAnimator.cancel();
            if (view.getVisibility() != 0) {
                view.setTranslationY(VerticalPositionAutoAnimator.this.offsetY);
                return;
            }
            VerticalPositionAutoAnimator.this.floatingButtonAnimator.getSpring().setFinalPosition(VerticalPositionAutoAnimator.this.offsetY);
            view.setTranslationY((i6 - i2) + VerticalPositionAutoAnimator.this.offsetY);
            VerticalPositionAutoAnimator.this.floatingButtonAnimator.start();
        }
    }

    private VerticalPositionAutoAnimator(View view, float f) {
        this.floatingButtonView = view;
        AnimatorLayoutChangeListener animatorLayoutChangeListener = new AnimatorLayoutChangeListener(view, f);
        this.animatorLayoutChangeListener = animatorLayoutChangeListener;
        view.addOnLayoutChangeListener(animatorLayoutChangeListener);
    }

    public static VerticalPositionAutoAnimator attach(View view) {
        return attach(view, 350.0f);
    }

    public static VerticalPositionAutoAnimator attach(View view, float f) {
        return new VerticalPositionAutoAnimator(view, f);
    }

    public void addUpdateListener(DynamicAnimation.OnAnimationUpdateListener onAnimationUpdateListener) {
        this.floatingButtonAnimator.addUpdateListener(onAnimationUpdateListener);
    }

    public float getOffsetY() {
        return this.offsetY;
    }

    public void ignoreNextLayout() {
        this.animatorLayoutChangeListener.ignoreNextLayout = true;
    }

    public void setOffsetY(float f) {
        this.offsetY = f;
        if (this.floatingButtonAnimator.isRunning()) {
            this.floatingButtonAnimator.getSpring().setFinalPosition(f);
        } else {
            this.floatingButtonView.setTranslationY(f);
        }
    }
}
