package org.telegram.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.widget.FrameLayout;
import androidx.dynamicanimation.animation.DynamicAnimation;
import androidx.dynamicanimation.animation.FloatValueHolder;
import androidx.dynamicanimation.animation.SpringAnimation;
import androidx.dynamicanimation.animation.SpringForce;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.AnimationNotificationsLocker;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.SharedConfig;
import org.telegram.messenger.UserConfig;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.INavigationLayout;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.CubicBezierInterpolator;

public abstract class RightSlidingDialogContainer extends FrameLayout {
    public static long fragmentDialogId;
    private Paint actionModePaint;
    private int currentAccount;
    ActionBar currentActionBarView;
    BaseFragment currentFragment;
    View currentFragmentFullscreenView;
    View currentFragmentView;
    float currentTop;
    public boolean enabled;
    public int fragmentViewPadding;
    boolean isOpenned;
    boolean isPaused;
    int lastSize;
    private boolean maybeStartTracking;
    INavigationLayout navigationLayout;
    private AnimationNotificationsLocker notificationsLocker;
    ValueAnimator openAnimator;
    float openedProgress;
    SpringAnimation replaceAnimation;
    private boolean replaceAnimationInProgress;
    float replaceProgress;
    BaseFragment replacingFragment;
    protected boolean startedTracking;
    private int startedTrackingPointerId;
    private int startedTrackingX;
    private int startedTrackingY;
    float swipeBackX;
    private VelocityTracker velocityTracker;

    public interface BaseFragmentWithFullscreen {
        View getFullscreenView();
    }

    public RightSlidingDialogContainer(Context context) {
        super(context);
        this.openedProgress = 0.0f;
        this.notificationsLocker = new AnimationNotificationsLocker();
        this.currentAccount = UserConfig.selectedAccount;
        this.enabled = true;
    }

    private void animateReplace(final BaseFragment baseFragment) {
        final BaseFragment baseFragment2 = this.currentFragment;
        if (!SharedConfig.animationsEnabled()) {
            baseFragment2.onTransitionAnimationStart(true, false);
            baseFragment2.onTransitionAnimationEnd(true, false);
            setReplaceProgress(baseFragment, baseFragment2, 1.0f);
            this.replaceAnimationInProgress = false;
            this.replacingFragment = null;
            baseFragment.onPause();
            baseFragment.onFragmentDestroy();
            removeView(baseFragment.getFragmentView());
            removeView(baseFragment.getActionBar());
            this.notificationsLocker.unlock();
            return;
        }
        SpringAnimation springAnimation = this.replaceAnimation;
        if (springAnimation != null) {
            springAnimation.cancel();
        }
        baseFragment2.onTransitionAnimationStart(true, false);
        this.replacingFragment = baseFragment;
        this.replaceAnimationInProgress = true;
        this.notificationsLocker.lock();
        SpringAnimation springAnimation2 = new SpringAnimation(new FloatValueHolder(0.0f));
        this.replaceAnimation = springAnimation2;
        springAnimation2.setSpring(new SpringForce(1000.0f).setStiffness(400.0f).setDampingRatio(1.0f));
        setReplaceProgress(baseFragment, baseFragment2, 0.0f);
        this.replaceAnimation.addUpdateListener(new DynamicAnimation.OnAnimationUpdateListener() {
            @Override
            public final void onAnimationUpdate(DynamicAnimation dynamicAnimation, float f, float f2) {
                RightSlidingDialogContainer.this.lambda$animateReplace$2(dynamicAnimation, f, f2);
            }
        });
        this.replaceAnimation.addEndListener(new DynamicAnimation.OnAnimationEndListener() {
            @Override
            public final void onAnimationEnd(DynamicAnimation dynamicAnimation, boolean z, float f, float f2) {
                RightSlidingDialogContainer.this.lambda$animateReplace$3(baseFragment2, baseFragment, dynamicAnimation, z, f, f2);
            }
        });
        this.replaceAnimation.start();
    }

    public static int getRightPaddingSize() {
        return SharedConfig.useThreeLinesLayout ? 74 : 76;
    }

    public void lambda$animateReplace$2(DynamicAnimation dynamicAnimation, float f, float f2) {
        this.replaceProgress = f / 1000.0f;
        invalidate();
    }

    public void lambda$animateReplace$3(BaseFragment baseFragment, BaseFragment baseFragment2, DynamicAnimation dynamicAnimation, boolean z, float f, float f2) {
        if (this.replaceAnimation == null) {
            return;
        }
        this.replaceAnimation = null;
        baseFragment.onTransitionAnimationEnd(true, false);
        setReplaceProgress(baseFragment2, baseFragment, 1.0f);
        this.replaceAnimationInProgress = false;
        this.replacingFragment = null;
        baseFragment2.onPause();
        baseFragment2.onFragmentDestroy();
        removeView(baseFragment2.getFragmentView());
        removeView(baseFragment2.getActionBar());
        this.notificationsLocker.unlock();
    }

    public void lambda$finishPreviewInernal$4(ValueAnimator valueAnimator) {
        this.openedProgress = ((Float) valueAnimator.getAnimatedValue()).floatValue();
        updateOpenAnimationProgress();
    }

    public void lambda$onTouchEvent$5(ValueAnimator valueAnimator) {
        this.openedProgress = ((Float) valueAnimator.getAnimatedValue()).floatValue();
        updateOpenAnimationProgress();
    }

    public void lambda$presentFragment$0(ValueAnimator valueAnimator) {
        this.openedProgress = ((Float) valueAnimator.getAnimatedValue()).floatValue();
        updateOpenAnimationProgress();
    }

    private void prepareForMoving(MotionEvent motionEvent) {
        this.maybeStartTracking = false;
        this.startedTracking = true;
        this.startedTrackingX = (int) motionEvent.getX();
        openAnimationStarted(false);
    }

    private void setReplaceProgress(BaseFragment baseFragment, BaseFragment baseFragment2, float f) {
        if (baseFragment == null && baseFragment2 == null) {
            return;
        }
        int measuredWidth = (baseFragment != null ? baseFragment.getFragmentView() : baseFragment2.getFragmentView()).getMeasuredWidth();
        if (baseFragment != null) {
            if (baseFragment.getFragmentView() != null) {
                baseFragment.getFragmentView().setAlpha(1.0f - f);
                baseFragment.getFragmentView().setTranslationX(measuredWidth * 0.6f * f);
            }
            baseFragment.setPreviewOpenedProgress(1.0f - f);
        }
        if (baseFragment2 != null) {
            if (baseFragment2.getFragmentView() != null) {
                baseFragment2.getFragmentView().setAlpha(1.0f);
                baseFragment2.getFragmentView().setTranslationX(measuredWidth * (1.0f - f));
            }
            baseFragment2.setPreviewReplaceProgress(f);
        }
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        View view;
        if (this.replaceAnimationInProgress) {
            setReplaceProgress(this.replacingFragment, this.currentFragment, this.replaceProgress);
            invalidate();
        }
        super.dispatchDraw(canvas);
        float f = this.openedProgress;
        ActionBar actionBar = this.currentActionBarView;
        float alpha = (actionBar == null || actionBar.getActionMode() == null) ? 0.0f : this.currentActionBarView.getActionMode().getAlpha();
        ActionBar actionBar2 = this.currentActionBarView;
        float max = f * Math.max(alpha, actionBar2 == null ? 0.0f : actionBar2.searchFieldVisibleAlpha);
        if (this.currentFragment == null || this.currentActionBarView == null || max <= 0.0f) {
            return;
        }
        if (this.actionModePaint == null) {
            this.actionModePaint = new Paint();
        }
        this.actionModePaint.setColor(Theme.getColor(Theme.key_actionBarActionModeDefault));
        if (max == 1.0f) {
            canvas.save();
        } else {
            canvas.saveLayerAlpha(0.0f, 0.0f, getMeasuredWidth(), this.currentTop, (int) (max * 255.0f), 31);
        }
        canvas.drawRect(0.0f, 0.0f, getMeasuredWidth(), this.currentTop, this.actionModePaint);
        canvas.translate(this.currentActionBarView.getX(), this.currentActionBarView.getY());
        canvas.save();
        canvas.translate(this.currentActionBarView.getBackButton().getX(), this.currentActionBarView.getBackButton().getY());
        this.currentActionBarView.getBackButton().draw(canvas);
        canvas.restore();
        if (this.currentActionBarView.getActionMode() == null) {
            view = this.currentActionBarView;
        } else {
            if (max != this.openedProgress * this.currentActionBarView.getActionMode().getAlpha()) {
                this.currentActionBarView.draw(canvas);
                canvas.saveLayerAlpha(0.0f, 0.0f, getMeasuredWidth(), this.currentTop, (int) (this.currentActionBarView.getActionMode().getAlpha() * 255.0f), 31);
                this.currentActionBarView.getActionMode().draw(canvas);
                canvas.restore();
                canvas.restore();
                invalidate();
            }
            view = this.currentActionBarView.getActionMode();
        }
        view.draw(canvas);
        canvas.restore();
        invalidate();
    }

    @Override
    protected boolean drawChild(Canvas canvas, View view, long j) {
        ActionBar actionBar = this.currentActionBarView;
        if (view == actionBar && actionBar.getActionMode() != null && this.currentActionBarView.getActionMode().getAlpha() == 1.0f) {
            return true;
        }
        return super.drawChild(canvas, view, j);
    }

    public void lambda$presentFragment$1() {
        if (this.isOpenned) {
            openAnimationStarted(false);
            finishPreviewInernal();
        }
    }

    public void finishPreviewInernal() {
        this.isOpenned = false;
        if (SharedConfig.animationsEnabled()) {
            this.notificationsLocker.lock();
            ValueAnimator ofFloat = ValueAnimator.ofFloat(this.openedProgress, 0.0f);
            this.openAnimator = ofFloat;
            ofFloat.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                    RightSlidingDialogContainer.this.lambda$finishPreviewInernal$4(valueAnimator);
                }
            });
            this.openAnimator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animator) {
                    RightSlidingDialogContainer rightSlidingDialogContainer = RightSlidingDialogContainer.this;
                    if (rightSlidingDialogContainer.openAnimator == null) {
                        return;
                    }
                    rightSlidingDialogContainer.openAnimator = null;
                    rightSlidingDialogContainer.openedProgress = 0.0f;
                    rightSlidingDialogContainer.updateOpenAnimationProgress();
                    RightSlidingDialogContainer.this.notificationsLocker.unlock();
                    BaseFragment baseFragment = RightSlidingDialogContainer.this.currentFragment;
                    if (baseFragment != null) {
                        baseFragment.onPause();
                        RightSlidingDialogContainer.this.currentFragment.onFragmentDestroy();
                        RightSlidingDialogContainer.this.removeAllViews();
                        RightSlidingDialogContainer.this.currentFragment = null;
                        NotificationCenter.getGlobalInstance().lambda$postNotificationNameOnUIThread$1(NotificationCenter.needCheckSystemBarColors, new Object[0]);
                    }
                    RightSlidingDialogContainer.this.openAnimationFinished(false);
                }
            });
            this.openAnimator.setDuration(250L);
            this.openAnimator.setInterpolator(CubicBezierInterpolator.DEFAULT);
            this.openAnimator.start();
            return;
        }
        this.openedProgress = 0.0f;
        updateOpenAnimationProgress();
        BaseFragment baseFragment = this.currentFragment;
        if (baseFragment != null) {
            baseFragment.onPause();
            this.currentFragment.onFragmentDestroy();
            removeAllViews();
            this.currentFragment = null;
            NotificationCenter.getGlobalInstance().lambda$postNotificationNameOnUIThread$1(NotificationCenter.needCheckSystemBarColors, new Object[0]);
        }
        openAnimationFinished(false);
    }

    public long getCurrentFragmetDialogId() {
        return fragmentDialogId;
    }

    public BaseFragment getFragment() {
        return this.currentFragment;
    }

    public View getFragmentView() {
        return this.currentFragmentView;
    }

    abstract boolean getOccupyStatusbar();

    public boolean hasFragment() {
        return this.currentFragment != null;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent motionEvent) {
        return onTouchEvent(motionEvent);
    }

    @Override
    protected void onMeasure(int i, int i2) {
        int i3 = getOccupyStatusbar() ? AndroidUtilities.statusBarHeight : 0;
        View view = this.currentFragmentView;
        if (view != null) {
            FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) view.getLayoutParams();
            layoutParams.leftMargin = AndroidUtilities.dp(getRightPaddingSize());
            layoutParams.topMargin = ActionBar.getCurrentActionBarHeight() + i3 + this.fragmentViewPadding;
        }
        ActionBar actionBar = this.currentActionBarView;
        if (actionBar != null) {
            ((FrameLayout.LayoutParams) actionBar.getLayoutParams()).topMargin = i3;
        }
        super.onMeasure(i, i2);
        int measuredHeight = (getMeasuredHeight() + getMeasuredWidth()) << 16;
        if (this.lastSize != measuredHeight) {
            this.lastSize = measuredHeight;
            updateOpenAnimationProgress();
        }
    }

    public void onPause() {
        this.isPaused = true;
        BaseFragment baseFragment = this.currentFragment;
        if (baseFragment != null) {
            baseFragment.onPause();
        }
    }

    public void onResume() {
        this.isPaused = false;
        BaseFragment baseFragment = this.currentFragment;
        if (baseFragment != null) {
            baseFragment.onResume();
        }
    }

    @Override
    public boolean onTouchEvent(android.view.MotionEvent r9) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.RightSlidingDialogContainer.onTouchEvent(android.view.MotionEvent):boolean");
    }

    public abstract void openAnimationFinished(boolean z);

    public abstract void openAnimationStarted(boolean z);

    public void presentFragment(INavigationLayout iNavigationLayout, final BaseFragment baseFragment) {
        if (this.isPaused) {
            return;
        }
        this.navigationLayout = iNavigationLayout;
        if (baseFragment.onFragmentCreate()) {
            baseFragment.setInPreviewMode(true);
            baseFragment.setParentLayout(iNavigationLayout);
            View createView = baseFragment.createView(getContext());
            baseFragment.onResume();
            this.currentFragmentView = createView;
            addView(createView);
            BaseFragment baseFragment2 = this.currentFragment;
            if (baseFragment instanceof BaseFragmentWithFullscreen) {
                View fullscreenView = ((BaseFragmentWithFullscreen) baseFragment).getFullscreenView();
                this.currentFragmentFullscreenView = fullscreenView;
                addView(fullscreenView);
            }
            this.currentFragment = baseFragment;
            fragmentDialogId = 0L;
            if (baseFragment instanceof TopicsFragment) {
                fragmentDialogId = -((TopicsFragment) baseFragment).chatId;
            }
            if (baseFragment.getActionBar() != null) {
                ActionBar actionBar = baseFragment.getActionBar();
                this.currentActionBarView = actionBar;
                addView(actionBar);
                this.currentActionBarView.listenToBackgroundUpdate(new Runnable() {
                    @Override
                    public final void run() {
                        RightSlidingDialogContainer.this.invalidate();
                    }
                });
            }
            if (baseFragment2 != null) {
                animateReplace(baseFragment2);
            } else if (!this.isOpenned) {
                this.isOpenned = true;
                if (!SharedConfig.animationsEnabled()) {
                    openAnimationStarted(true);
                    baseFragment.onTransitionAnimationStart(true, false);
                    baseFragment.onTransitionAnimationEnd(true, false);
                    this.openedProgress = 1.0f;
                    updateOpenAnimationProgress();
                    openAnimationFinished(false);
                    return;
                }
                this.notificationsLocker.lock();
                this.openAnimator = ValueAnimator.ofFloat(0.0f, 1.0f);
                this.openedProgress = 0.0f;
                openAnimationStarted(true);
                updateOpenAnimationProgress();
                baseFragment.onTransitionAnimationStart(true, false);
                this.openAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                        RightSlidingDialogContainer.this.lambda$presentFragment$0(valueAnimator);
                    }
                });
                this.openAnimator.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animator) {
                        RightSlidingDialogContainer rightSlidingDialogContainer = RightSlidingDialogContainer.this;
                        if (rightSlidingDialogContainer.openAnimator == null) {
                            return;
                        }
                        rightSlidingDialogContainer.openAnimator = null;
                        rightSlidingDialogContainer.notificationsLocker.unlock();
                        baseFragment.onTransitionAnimationEnd(true, false);
                        RightSlidingDialogContainer rightSlidingDialogContainer2 = RightSlidingDialogContainer.this;
                        rightSlidingDialogContainer2.openedProgress = 1.0f;
                        rightSlidingDialogContainer2.updateOpenAnimationProgress();
                        RightSlidingDialogContainer.this.openAnimationFinished(false);
                    }
                });
                this.openAnimator.setDuration(250L);
                this.openAnimator.setInterpolator(CubicBezierInterpolator.DEFAULT);
                this.openAnimator.setStartDelay(SharedConfig.getDevicePerformanceClass() >= 2 ? 50L : 150L);
                this.openAnimator.start();
            }
            baseFragment.setPreviewDelegate(new BaseFragment.PreviewDelegate() {
                @Override
                public final void finishFragment() {
                    RightSlidingDialogContainer.this.lambda$presentFragment$1();
                }
            });
        }
    }

    @Override
    public void removeView(View view) {
        super.removeView(view);
        if (view == this.currentFragmentView) {
            lambda$presentFragment$1();
        }
    }

    @Override
    public void removeViewInLayout(View view) {
        super.removeViewInLayout(view);
        if (view == this.currentFragmentView) {
            lambda$presentFragment$1();
        }
    }

    public void setCurrentTop(int i) {
        this.currentTop = i;
        View view = this.currentFragmentView;
        if (view != null) {
            view.setTranslationY((i - view.getTop()) + this.fragmentViewPadding);
        }
        View view2 = this.currentFragmentFullscreenView;
        if (view2 != null) {
            view2.setTranslationY(i - view2.getTop());
        }
    }

    public void setFragmentViewPadding(int i) {
        this.fragmentViewPadding = i;
    }

    public void setOpenProgress(float f) {
    }

    public void setTransitionPaddingBottom(int i) {
        BaseFragment baseFragment = this.currentFragment;
        if (baseFragment instanceof TopicsFragment) {
            ((TopicsFragment) baseFragment).setTransitionPadding(i);
        }
    }

    protected void updateOpenAnimationProgress() {
        if (this.replaceAnimationInProgress || !hasFragment()) {
            return;
        }
        setOpenProgress(this.openedProgress);
        View view = this.currentFragmentView;
        if (view != null) {
            view.setTranslationX((getMeasuredWidth() - AndroidUtilities.dp(getRightPaddingSize())) * (1.0f - this.openedProgress));
        }
        ActionBar actionBar = this.currentActionBarView;
        if (actionBar != null) {
            actionBar.setTranslationX(AndroidUtilities.dp(48.0f) * (1.0f - this.openedProgress));
        }
        BaseFragment baseFragment = this.currentFragment;
        if (baseFragment != null) {
            baseFragment.setPreviewOpenedProgress(this.openedProgress);
        }
        invalidate();
    }
}
