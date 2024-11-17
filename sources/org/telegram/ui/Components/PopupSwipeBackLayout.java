package org.telegram.ui.Components;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.SparseIntArray;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import androidx.core.view.GestureDetectorCompat;
import java.util.ArrayList;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.AnimationNotificationsLocker;
import org.telegram.messenger.UserConfig;
import org.telegram.ui.ActionBar.ActionBarMenuSlider;
import org.telegram.ui.ActionBar.ActionBarPopupWindow;
import org.telegram.ui.ActionBar.Theme;

public class PopupSwipeBackLayout extends FrameLayout {
    private int currentForegroundIndex;
    private GestureDetectorCompat detector;
    private ValueAnimator foregroundAnimator;
    private int foregroundColor;
    private Paint foregroundPaint;
    private android.graphics.Rect hitRect;
    private boolean isAnimationInProgress;
    private boolean isProcessingSwipe;
    private boolean isSwipeBackDisallowed;
    private boolean isSwipeDisallowed;
    private int lastHeightReported;
    float lastToProgress;
    float lastTransitionProgress;
    private Path mPath;
    private RectF mRect;
    private AnimationNotificationsLocker notificationsLocker;
    private Runnable onForegroundOpen;
    private IntCallback onHeightUpdateListener;
    private ArrayList onSwipeBackProgressListeners;
    private Paint overlayPaint;
    private float overrideForegroundHeight;
    SparseIntArray overrideHeightIndex;
    Theme.ResourcesProvider resourcesProvider;
    public boolean stickToRight;
    private float toProgress;
    public float transitionProgress;

    public interface IntCallback {
        void run(int i);
    }

    public interface OnSwipeBackProgressListener {
        void onSwipeBackProgress(PopupSwipeBackLayout popupSwipeBackLayout, float f, float f2);
    }

    public PopupSwipeBackLayout(Context context, Theme.ResourcesProvider resourcesProvider) {
        super(context);
        this.overrideHeightIndex = new SparseIntArray();
        this.toProgress = -1.0f;
        this.overlayPaint = new Paint(1);
        this.foregroundPaint = new Paint();
        this.foregroundColor = 0;
        this.mPath = new Path();
        this.mRect = new RectF();
        this.onSwipeBackProgressListeners = new ArrayList();
        this.currentForegroundIndex = -1;
        this.notificationsLocker = new AnimationNotificationsLocker();
        this.lastHeightReported = -1;
        this.hitRect = new android.graphics.Rect();
        this.resourcesProvider = resourcesProvider;
        final int scaledTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
        this.detector = new GestureDetectorCompat(context, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onDown(MotionEvent motionEvent) {
                return true;
            }

            @Override
            public boolean onFling(MotionEvent motionEvent, MotionEvent motionEvent2, float f, float f2) {
                if (!PopupSwipeBackLayout.this.isAnimationInProgress && !PopupSwipeBackLayout.this.isSwipeDisallowed && f >= 600.0f) {
                    PopupSwipeBackLayout.this.clearFlags();
                    PopupSwipeBackLayout.this.animateToState(0.0f, f / 6000.0f);
                }
                return false;
            }

            @Override
            public boolean onScroll(MotionEvent motionEvent, MotionEvent motionEvent2, float f, float f2) {
                if (!PopupSwipeBackLayout.this.isProcessingSwipe && !PopupSwipeBackLayout.this.isSwipeDisallowed) {
                    if (!PopupSwipeBackLayout.this.isSwipeBackDisallowed && PopupSwipeBackLayout.this.transitionProgress == 1.0f && f <= (-scaledTouchSlop) && Math.abs(f) >= Math.abs(1.5f * f2)) {
                        PopupSwipeBackLayout popupSwipeBackLayout = PopupSwipeBackLayout.this;
                        if (!popupSwipeBackLayout.isDisallowedView(motionEvent2, popupSwipeBackLayout.getChildAt(popupSwipeBackLayout.transitionProgress > 0.5f ? 1 : 0))) {
                            PopupSwipeBackLayout.this.isProcessingSwipe = true;
                            MotionEvent obtain = MotionEvent.obtain(0L, 0L, 3, 0.0f, 0.0f, 0);
                            for (int i = 0; i < PopupSwipeBackLayout.this.getChildCount(); i++) {
                                PopupSwipeBackLayout.this.getChildAt(i).dispatchTouchEvent(obtain);
                            }
                            obtain.recycle();
                        }
                    }
                    PopupSwipeBackLayout.this.isSwipeDisallowed = true;
                }
                if (PopupSwipeBackLayout.this.isProcessingSwipe) {
                    PopupSwipeBackLayout.this.toProgress = -1.0f;
                    PopupSwipeBackLayout.this.transitionProgress = 1.0f - Math.max(0.0f, Math.min(1.0f, (motionEvent2.getX() - motionEvent.getX()) / PopupSwipeBackLayout.this.getWidth()));
                    PopupSwipeBackLayout.this.invalidateTransforms();
                }
                return PopupSwipeBackLayout.this.isProcessingSwipe;
            }
        });
        this.overlayPaint.setColor(-16777216);
    }

    public void animateToState(final float f, float f2) {
        ValueAnimator duration = ValueAnimator.ofFloat(this.transitionProgress, f).setDuration(Math.max(0.5f, Math.abs(this.transitionProgress - f) - Math.min(0.2f, f2)) * 300.0f);
        duration.setInterpolator(CubicBezierInterpolator.DEFAULT);
        int i = UserConfig.selectedAccount;
        this.notificationsLocker.lock();
        duration.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                PopupSwipeBackLayout.this.lambda$animateToState$0(valueAnimator);
            }
        });
        duration.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animator) {
                PopupSwipeBackLayout.this.notificationsLocker.unlock();
                PopupSwipeBackLayout popupSwipeBackLayout = PopupSwipeBackLayout.this;
                float f3 = f;
                popupSwipeBackLayout.transitionProgress = f3;
                if (f3 <= 0.0f) {
                    popupSwipeBackLayout.currentForegroundIndex = -1;
                }
                PopupSwipeBackLayout.this.invalidateTransforms();
                PopupSwipeBackLayout.this.isAnimationInProgress = false;
                if (PopupSwipeBackLayout.this.onForegroundOpen == null || Math.abs(f - 1.0f) >= 0.01f) {
                    return;
                }
                PopupSwipeBackLayout.this.onForegroundOpen.run();
            }

            @Override
            public void onAnimationStart(Animator animator) {
                PopupSwipeBackLayout.this.isAnimationInProgress = true;
                PopupSwipeBackLayout.this.toProgress = f;
            }
        });
        duration.start();
    }

    public void clearFlags() {
        this.isProcessingSwipe = false;
        this.isSwipeDisallowed = false;
    }

    private void invalidateVisibility() {
        for (int i = 0; i < getChildCount(); i++) {
            View childAt = getChildAt(i);
            if (i == 0) {
                if (this.transitionProgress == 1.0f && childAt.getVisibility() != 4) {
                    childAt.setVisibility(4);
                }
                if (this.transitionProgress != 1.0f) {
                    if (childAt.getVisibility() == 0) {
                    }
                    childAt.setVisibility(0);
                }
            } else {
                if (i == this.currentForegroundIndex) {
                    if (this.transitionProgress == 0.0f && childAt.getVisibility() != 4) {
                        childAt.setVisibility(4);
                    }
                    if (this.transitionProgress != 0.0f) {
                        if (childAt.getVisibility() == 0) {
                        }
                        childAt.setVisibility(0);
                    }
                } else {
                    childAt.setVisibility(4);
                }
            }
        }
    }

    public boolean isDisallowedView(MotionEvent motionEvent, View view) {
        view.getHitRect(this.hitRect);
        if (this.hitRect.contains((int) motionEvent.getX(), (int) motionEvent.getY()) && (view.canScrollHorizontally(-1) || (view instanceof ActionBarMenuSlider))) {
            return true;
        }
        if (view instanceof ViewGroup) {
            ViewGroup viewGroup = (ViewGroup) view;
            for (int i = 0; i < viewGroup.getChildCount(); i++) {
                if (isDisallowedView(motionEvent, viewGroup.getChildAt(i))) {
                    return true;
                }
            }
        }
        return false;
    }

    public void lambda$animateToState$0(ValueAnimator valueAnimator) {
        this.transitionProgress = ((Float) valueAnimator.getAnimatedValue()).floatValue();
        invalidateTransforms();
    }

    public void lambda$setNewForegroundHeight$1(ValueAnimator valueAnimator) {
        this.overrideForegroundHeight = ((Float) valueAnimator.getAnimatedValue()).floatValue();
        invalidateTransforms();
    }

    private boolean processTouchEvent(MotionEvent motionEvent) {
        int action = motionEvent.getAction() & 255;
        if (this.isAnimationInProgress) {
            return true;
        }
        if (this.detector.onTouchEvent(motionEvent) || !(action == 1 || action == 3)) {
            return this.isProcessingSwipe;
        }
        if (this.isProcessingSwipe) {
            clearFlags();
            animateToState(this.transitionProgress >= 0.5f ? 1.0f : 0.0f, 0.0f);
            return false;
        }
        if (!this.isSwipeDisallowed) {
            return false;
        }
        clearFlags();
        return false;
    }

    public void addOnSwipeBackProgressListener(OnSwipeBackProgressListener onSwipeBackProgressListener) {
        this.onSwipeBackProgressListeners.add(onSwipeBackProgressListener);
    }

    @Override
    public void addView(View view, int i, ViewGroup.LayoutParams layoutParams) {
        super.addView(view, i, layoutParams);
        invalidateTransforms();
    }

    public void closeForeground() {
        closeForeground(true);
    }

    public void closeForeground(boolean z) {
        if (this.isAnimationInProgress) {
            return;
        }
        if (z) {
            animateToState(0.0f, 0.0f);
            return;
        }
        this.currentForegroundIndex = -1;
        this.transitionProgress = 0.0f;
        invalidateTransforms();
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        RectF rectF;
        if (getChildCount() == 0) {
            return;
        }
        View childAt = getChildAt(0);
        float top = childAt.getTop();
        float measuredWidth = childAt.getMeasuredWidth();
        float measuredHeight = childAt.getMeasuredHeight();
        int i = this.currentForegroundIndex;
        float f = 0.0f;
        if (i != -1 && i < getChildCount()) {
            View childAt2 = getChildAt(this.currentForegroundIndex);
            float top2 = childAt2.getTop();
            float measuredWidth2 = childAt2.getMeasuredWidth();
            float f2 = this.overrideForegroundHeight;
            if (f2 == 0.0f) {
                f2 = childAt2.getMeasuredHeight();
            }
            if (childAt.getMeasuredWidth() != 0 && childAt.getMeasuredHeight() != 0 && childAt2.getMeasuredWidth() != 0 && childAt2.getMeasuredHeight() != 0) {
                top = AndroidUtilities.lerp(top, top2, this.transitionProgress);
                measuredWidth = AndroidUtilities.lerp(measuredWidth, measuredWidth2, this.transitionProgress);
                measuredHeight = AndroidUtilities.lerp(measuredHeight, f2, this.transitionProgress);
            }
        }
        int save = canvas.save();
        this.mPath.rewind();
        int dp = AndroidUtilities.dp(6.0f);
        if (this.stickToRight) {
            rectF = this.mRect;
            f = getWidth() - measuredWidth;
            measuredWidth = getWidth();
        } else {
            rectF = this.mRect;
        }
        rectF.set(f, top, measuredWidth, measuredHeight + top);
        float f3 = dp;
        this.mPath.addRoundRect(this.mRect, f3, f3, Path.Direction.CW);
        canvas.clipPath(this.mPath);
        super.dispatchDraw(canvas);
        canvas.restoreToCount(save);
        if (this.onHeightUpdateListener == null || this.lastHeightReported == this.mRect.height()) {
            return;
        }
        IntCallback intCallback = this.onHeightUpdateListener;
        int height = (int) this.mRect.height();
        this.lastHeightReported = height;
        intCallback.run(height);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent motionEvent) {
        if (processTouchEvent(motionEvent)) {
            return true;
        }
        int actionMasked = motionEvent.getActionMasked();
        RectF rectF = this.mRect;
        if (rectF != null) {
            rectF.contains(motionEvent.getX(), motionEvent.getY());
        }
        if (actionMasked == 0 && !this.mRect.contains(motionEvent.getX(), motionEvent.getY())) {
            callOnClick();
            return true;
        }
        int i = this.currentForegroundIndex;
        if (i < 0 || i >= getChildCount()) {
            return super.dispatchTouchEvent(motionEvent);
        }
        View childAt = getChildAt(0);
        View childAt2 = getChildAt(this.currentForegroundIndex);
        if (this.transitionProgress > 0.5f) {
            childAt = childAt2;
        }
        boolean dispatchTouchEvent = childAt.dispatchTouchEvent(motionEvent);
        return (!dispatchTouchEvent && actionMasked == 0) || dispatchTouchEvent || onTouchEvent(motionEvent);
    }

    @Override
    protected boolean drawChild(Canvas canvas, View view, long j) {
        int indexOfChild = indexOfChild(view);
        int save = canvas.save();
        if (indexOfChild != 0) {
            int i = this.foregroundColor;
            if (i == 0) {
                this.foregroundPaint.setColor(Theme.getColor(Theme.key_actionBarDefaultSubmenuBackground, this.resourcesProvider));
            } else {
                this.foregroundPaint.setColor(i);
            }
            canvas.drawRect(view.getX(), 0.0f, view.getX() + view.getMeasuredWidth(), getMeasuredHeight(), this.foregroundPaint);
        }
        boolean drawChild = super.drawChild(canvas, view, j);
        if (indexOfChild == 0) {
            this.overlayPaint.setAlpha((int) (this.transitionProgress * 64.0f));
            canvas.drawRect(0.0f, 0.0f, getWidth(), getHeight(), this.overlayPaint);
        }
        canvas.restoreToCount(save);
        return drawChild;
    }

    public void invalidateTransforms() {
        invalidateTransforms(true);
    }

    public void invalidateTransforms(boolean z) {
        float f;
        float f2;
        if (this.lastToProgress != this.toProgress || this.lastTransitionProgress != this.transitionProgress) {
            if (!this.onSwipeBackProgressListeners.isEmpty()) {
                for (int i = 0; i < this.onSwipeBackProgressListeners.size(); i++) {
                    ((OnSwipeBackProgressListener) this.onSwipeBackProgressListeners.get(i)).onSwipeBackProgress(this, this.toProgress, this.transitionProgress);
                }
            }
            this.lastToProgress = this.toProgress;
            this.lastTransitionProgress = this.transitionProgress;
        }
        View childAt = getChildAt(0);
        int i2 = this.currentForegroundIndex;
        View childAt2 = (i2 < 0 || i2 >= getChildCount()) ? null : getChildAt(this.currentForegroundIndex);
        childAt.setTranslationX((-this.transitionProgress) * getWidth() * 0.5f);
        float f3 = ((1.0f - this.transitionProgress) * 0.05f) + 0.95f;
        childAt.setScaleX(f3);
        childAt.setScaleY(f3);
        if (childAt2 != null) {
            childAt2.setTranslationX((1.0f - this.transitionProgress) * getWidth());
        }
        invalidateVisibility();
        float measuredWidth = childAt.getMeasuredWidth();
        float measuredHeight = childAt.getMeasuredHeight();
        if (childAt2 != null) {
            f = childAt2.getMeasuredWidth();
            f2 = this.overrideForegroundHeight;
            if (f2 == 0.0f) {
                f2 = childAt2.getMeasuredHeight();
            }
        } else {
            f = 0.0f;
            f2 = 0.0f;
        }
        if (childAt.getMeasuredWidth() == 0 || childAt.getMeasuredHeight() == 0) {
            return;
        }
        ActionBarPopupWindow.ActionBarPopupWindowLayout actionBarPopupWindowLayout = (ActionBarPopupWindow.ActionBarPopupWindowLayout) getParent();
        float f4 = this.transitionProgress;
        float paddingTop = measuredHeight + ((f2 - measuredHeight) * f4) + actionBarPopupWindowLayout.getPaddingTop() + actionBarPopupWindowLayout.getPaddingBottom();
        actionBarPopupWindowLayout.updateAnimation = false;
        actionBarPopupWindowLayout.setBackScaleX(((measuredWidth + ((f - measuredWidth) * f4)) + (actionBarPopupWindowLayout.getPaddingLeft() + actionBarPopupWindowLayout.getPaddingRight())) / actionBarPopupWindowLayout.getMeasuredWidth());
        if (z) {
            actionBarPopupWindowLayout.setBackScaleY(Math.min(1.0f, paddingTop / actionBarPopupWindowLayout.getMeasuredHeight()));
        }
        actionBarPopupWindowLayout.updateAnimation = true;
        for (int i3 = 0; i3 < getChildCount(); i3++) {
            View childAt3 = getChildAt(i3);
            childAt3.setPivotX(0.0f);
            childAt3.setPivotY(0.0f);
        }
        invalidate();
    }

    public boolean isForegroundOpen() {
        return this.transitionProgress > 0.0f;
    }

    @Override
    protected void onLayout(boolean z, int i, int i2, int i3, int i4) {
        for (int i5 = 0; i5 < getChildCount(); i5++) {
            View childAt = getChildAt(i5);
            if ((childAt.getLayoutParams() instanceof FrameLayout.LayoutParams) && ((FrameLayout.LayoutParams) childAt.getLayoutParams()).gravity == 80) {
                if (this.stickToRight) {
                    int i6 = i3 - i;
                    int i7 = i4 - i2;
                    childAt.layout(i6 - childAt.getMeasuredWidth(), i7 - childAt.getMeasuredHeight(), i6, i7);
                } else {
                    int i8 = i4 - i2;
                    childAt.layout(0, i8 - childAt.getMeasuredHeight(), childAt.getMeasuredWidth(), i8);
                }
            } else if (this.stickToRight) {
                int i9 = i3 - i;
                childAt.layout(i9 - childAt.getMeasuredWidth(), 0, i9, childAt.getMeasuredHeight());
            } else {
                childAt.layout(0, 0, childAt.getMeasuredWidth(), childAt.getMeasuredHeight());
            }
        }
    }

    @Override
    protected void onSizeChanged(int i, int i2, int i3, int i4) {
        super.onSizeChanged(i, i2, i3, i4);
        invalidateTransforms();
    }

    public void openForeground(int i) {
        if (this.isAnimationInProgress) {
            return;
        }
        this.currentForegroundIndex = i;
        this.overrideForegroundHeight = this.overrideHeightIndex.get(i);
        animateToState(1.0f, 0.0f);
    }

    public void setForegroundColor(int i) {
        this.foregroundColor = i;
    }

    public void setNewForegroundHeight(int i, int i2, boolean z) {
        this.overrideHeightIndex.put(i, i2);
        int i3 = this.currentForegroundIndex;
        if (i == i3 && i3 >= 0 && i3 < getChildCount()) {
            ValueAnimator valueAnimator = this.foregroundAnimator;
            if (valueAnimator != null) {
                valueAnimator.cancel();
                this.foregroundAnimator = null;
            }
            if (!z) {
                this.overrideForegroundHeight = i2;
                invalidateTransforms();
                return;
            }
            View childAt = getChildAt(this.currentForegroundIndex);
            float f = this.overrideForegroundHeight;
            if (f == 0.0f) {
                f = childAt.getMeasuredHeight();
            }
            ValueAnimator duration = ValueAnimator.ofFloat(f, i2).setDuration(240L);
            duration.setInterpolator(Easings.easeInOutQuad);
            duration.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public final void onAnimationUpdate(ValueAnimator valueAnimator2) {
                    PopupSwipeBackLayout.this.lambda$setNewForegroundHeight$1(valueAnimator2);
                }
            });
            this.isAnimationInProgress = true;
            duration.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animator) {
                    PopupSwipeBackLayout.this.isAnimationInProgress = false;
                    PopupSwipeBackLayout.this.foregroundAnimator = null;
                }
            });
            duration.start();
            this.foregroundAnimator = duration;
        }
    }

    public void setOnForegroundOpenFinished(Runnable runnable) {
        this.onForegroundOpen = runnable;
    }

    public void setOnHeightUpdateListener(IntCallback intCallback) {
        this.onHeightUpdateListener = intCallback;
    }

    public void setStickToRight(boolean z) {
        this.stickToRight = z;
    }

    public void setSwipeBackDisallowed(boolean z) {
        this.isSwipeBackDisallowed = z;
    }
}
