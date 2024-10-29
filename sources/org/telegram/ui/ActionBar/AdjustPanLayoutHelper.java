package org.telegram.ui.ActionBar;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.os.SystemClock;
import android.view.ContextThemeWrapper;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowInsets;
import android.view.WindowInsetsAnimation;
import android.view.animation.Interpolator;
import android.widget.FrameLayout;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.ChatListItemAnimator;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.AnimationNotificationsLocker;

public abstract class AdjustPanLayoutHelper {
    public static boolean USE_ANDROID11_INSET_ANIMATOR = false;
    public static final Interpolator keyboardInterpolator = ChatListItemAnimator.DEFAULT_INTERPOLATOR;
    private boolean animationInProgress;
    ValueAnimator animator;
    boolean checkHierarchyHeight;
    private ViewGroup contentView;
    private Runnable delayedAnimationRunnable;
    private boolean enabled;
    float from;
    private boolean ignoreOnce;
    boolean inverse;
    boolean isKeyboardVisible;
    protected float keyboardSize;
    private boolean needDelay;
    AnimationNotificationsLocker notificationsLocker;
    ViewTreeObserver.OnPreDrawListener onPreDrawListener;
    private final View parent;
    View parentForListener;
    int previousContentHeight;
    int previousHeight;
    int previousStartOffset;
    private View resizableView;
    private View resizableViewToSet;
    public boolean showingKeyboard;
    long startAfter;
    float to;
    private boolean useInsetsAnimator;
    private boolean usingInsetAnimator;
    ArrayList viewsToHeightSet;

    public AdjustPanLayoutHelper(View view) {
        this(view, USE_ANDROID11_INSET_ANIMATOR);
    }

    public AdjustPanLayoutHelper(View view, boolean z) {
        this.usingInsetAnimator = false;
        this.delayedAnimationRunnable = new Runnable() {
            @Override
            public void run() {
                ValueAnimator valueAnimator = AdjustPanLayoutHelper.this.animator;
                if (valueAnimator == null || valueAnimator.isRunning()) {
                    return;
                }
                AdjustPanLayoutHelper.this.animator.start();
            }
        };
        this.previousHeight = -1;
        this.previousContentHeight = -1;
        this.previousStartOffset = -1;
        this.notificationsLocker = new AnimationNotificationsLocker();
        this.viewsToHeightSet = new ArrayList();
        this.onPreDrawListener = new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                int height = AdjustPanLayoutHelper.this.parent.getHeight();
                int startOffset = height - AdjustPanLayoutHelper.this.startOffset();
                AdjustPanLayoutHelper adjustPanLayoutHelper = AdjustPanLayoutHelper.this;
                int i = adjustPanLayoutHelper.previousHeight;
                if (startOffset == i - adjustPanLayoutHelper.previousStartOffset || height == i || adjustPanLayoutHelper.animator != null) {
                    if (adjustPanLayoutHelper.animator == null) {
                        adjustPanLayoutHelper.previousHeight = height;
                        adjustPanLayoutHelper.previousContentHeight = adjustPanLayoutHelper.contentView.getHeight();
                        AdjustPanLayoutHelper adjustPanLayoutHelper2 = AdjustPanLayoutHelper.this;
                        adjustPanLayoutHelper2.previousStartOffset = adjustPanLayoutHelper2.startOffset();
                        AdjustPanLayoutHelper.this.usingInsetAnimator = false;
                    }
                    return true;
                }
                if (!adjustPanLayoutHelper.heightAnimationEnabled() || Math.abs(AdjustPanLayoutHelper.this.previousHeight - height) < AndroidUtilities.dp(20.0f)) {
                    AdjustPanLayoutHelper adjustPanLayoutHelper3 = AdjustPanLayoutHelper.this;
                    adjustPanLayoutHelper3.previousHeight = height;
                    adjustPanLayoutHelper3.previousContentHeight = adjustPanLayoutHelper3.contentView.getHeight();
                    AdjustPanLayoutHelper adjustPanLayoutHelper4 = AdjustPanLayoutHelper.this;
                    adjustPanLayoutHelper4.previousStartOffset = adjustPanLayoutHelper4.startOffset();
                    AdjustPanLayoutHelper.this.usingInsetAnimator = false;
                    return true;
                }
                AdjustPanLayoutHelper adjustPanLayoutHelper5 = AdjustPanLayoutHelper.this;
                if (adjustPanLayoutHelper5.previousHeight != -1 && adjustPanLayoutHelper5.previousContentHeight == adjustPanLayoutHelper5.contentView.getHeight()) {
                    AdjustPanLayoutHelper adjustPanLayoutHelper6 = AdjustPanLayoutHelper.this;
                    adjustPanLayoutHelper6.isKeyboardVisible = height < adjustPanLayoutHelper6.contentView.getBottom();
                    AdjustPanLayoutHelper adjustPanLayoutHelper7 = AdjustPanLayoutHelper.this;
                    adjustPanLayoutHelper7.animateHeight(adjustPanLayoutHelper7.previousHeight, height, adjustPanLayoutHelper7.isKeyboardVisible);
                }
                AdjustPanLayoutHelper adjustPanLayoutHelper8 = AdjustPanLayoutHelper.this;
                adjustPanLayoutHelper8.previousHeight = height;
                adjustPanLayoutHelper8.previousContentHeight = adjustPanLayoutHelper8.contentView.getHeight();
                AdjustPanLayoutHelper adjustPanLayoutHelper9 = AdjustPanLayoutHelper.this;
                adjustPanLayoutHelper9.previousStartOffset = adjustPanLayoutHelper9.startOffset();
                return false;
            }
        };
        this.enabled = true;
        this.useInsetsAnimator = z;
        this.parent = view;
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                AdjustPanLayoutHelper.this.onAttach();
            }
        });
    }

    public void animateHeight(int i, int i2, boolean z) {
        if (this.ignoreOnce) {
            this.ignoreOnce = false;
            return;
        }
        if (this.enabled) {
            startTransition(i, i2, z);
            this.animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                    AdjustPanLayoutHelper.this.lambda$animateHeight$0(valueAnimator);
                }
            });
            this.animator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animator) {
                    if (AdjustPanLayoutHelper.this.usingInsetAnimator) {
                        return;
                    }
                    AdjustPanLayoutHelper.this.stopTransition();
                }
            });
            this.animator.setDuration(250L);
            this.animator.setInterpolator(keyboardInterpolator);
            this.notificationsLocker.lock();
            if (!this.needDelay) {
                this.animator.start();
                this.startAfter = -1L;
            } else {
                this.needDelay = false;
                this.startAfter = SystemClock.elapsedRealtime() + 100;
                AndroidUtilities.runOnUIThread(this.delayedAnimationRunnable, 100L);
            }
        }
    }

    private View findResizableView(View view) {
        View view2 = this.resizableViewToSet;
        if (view2 != null) {
            return view2;
        }
        while (view != null) {
            if (!(view.getParent() instanceof DrawerLayoutContainer)) {
                if (!(view.getParent() instanceof View)) {
                    break;
                }
                view = (View) view.getParent();
            } else {
                return view;
            }
        }
        return null;
    }

    private Activity getActivity(Context context) {
        if (context instanceof Activity) {
            return (Activity) context;
        }
        if (context instanceof ContextThemeWrapper) {
            return getActivity(((ContextThemeWrapper) context).getBaseContext());
        }
        return null;
    }

    public void lambda$animateHeight$0(ValueAnimator valueAnimator) {
        if (this.usingInsetAnimator) {
            return;
        }
        updateTransition(((Float) valueAnimator.getAnimatedValue()).floatValue());
    }

    private void setupNewCallback() {
        View view = this.resizableView;
        if (view == null) {
            return;
        }
        view.setWindowInsetsAnimationCallback(new WindowInsetsAnimation.Callback(1) {
            @Override
            public void onEnd(WindowInsetsAnimation windowInsetsAnimation) {
                if (!AdjustPanLayoutHelper.this.animationInProgress || AndroidUtilities.screenRefreshRate < 90.0f) {
                    return;
                }
                AdjustPanLayoutHelper.this.stopTransition();
            }

            @Override
            public WindowInsets onProgress(WindowInsets windowInsets, List list) {
                WindowInsetsAnimation windowInsetsAnimation;
                float interpolatedFraction;
                int typeMask;
                if (AdjustPanLayoutHelper.this.animationInProgress && AndroidUtilities.screenRefreshRate >= 90.0f) {
                    Iterator it = list.iterator();
                    while (true) {
                        if (!it.hasNext()) {
                            windowInsetsAnimation = null;
                            break;
                        }
                        windowInsetsAnimation = AdjustPanLayoutHelper$4$$ExternalSyntheticApiModelOutline0.m(it.next());
                        typeMask = windowInsetsAnimation.getTypeMask();
                        if ((typeMask & WindowInsetsCompat.Type.ime()) != 0) {
                            break;
                        }
                    }
                    if (windowInsetsAnimation != null) {
                        long elapsedRealtime = SystemClock.elapsedRealtime();
                        AdjustPanLayoutHelper adjustPanLayoutHelper = AdjustPanLayoutHelper.this;
                        if (elapsedRealtime >= adjustPanLayoutHelper.startAfter) {
                            adjustPanLayoutHelper.usingInsetAnimator = true;
                            AdjustPanLayoutHelper adjustPanLayoutHelper2 = AdjustPanLayoutHelper.this;
                            interpolatedFraction = windowInsetsAnimation.getInterpolatedFraction();
                            adjustPanLayoutHelper2.updateTransition(interpolatedFraction);
                        }
                    }
                }
                return windowInsets;
            }
        });
    }

    public boolean animationInProgress() {
        return this.animationInProgress;
    }

    protected boolean applyTranslation() {
        return true;
    }

    public void delayAnimation() {
        this.needDelay = true;
    }

    public void getViewsToSetHeight(View view) {
        this.viewsToHeightSet.clear();
        while (view != null) {
            this.viewsToHeightSet.add(view);
            if (view == this.resizableView) {
                return;
            } else {
                view = view.getParent() instanceof View ? (View) view.getParent() : null;
            }
        }
    }

    protected abstract boolean heightAnimationEnabled();

    public void ignoreOnce() {
        this.ignoreOnce = true;
    }

    public void onAttach() {
        onDetach();
        Activity activity = getActivity(this.parent.getContext());
        if (activity != null) {
            this.contentView = (ViewGroup) ((ViewGroup) activity.getWindow().getDecorView()).findViewById(16908290);
        }
        View findResizableView = findResizableView(this.parent);
        this.resizableView = findResizableView;
        if (findResizableView != null) {
            this.parentForListener = findResizableView;
            findResizableView.getViewTreeObserver().addOnPreDrawListener(this.onPreDrawListener);
        }
        if (!this.useInsetsAnimator || Build.VERSION.SDK_INT < 30) {
            return;
        }
        setupNewCallback();
    }

    public void onDetach() {
        ValueAnimator valueAnimator = this.animator;
        if (valueAnimator != null) {
            valueAnimator.cancel();
        }
        View view = this.parentForListener;
        if (view != null) {
            view.getViewTreeObserver().removeOnPreDrawListener(this.onPreDrawListener);
            this.parentForListener = null;
        }
        View view2 = this.parent;
        if (view2 == null || !this.useInsetsAnimator || Build.VERSION.SDK_INT < 30) {
            return;
        }
        view2.setWindowInsetsAnimationCallback(null);
    }

    public void onPanTranslationUpdate(float f, float f2, boolean z) {
    }

    public void onTransitionEnd() {
    }

    public void onTransitionStart(boolean z, int i) {
    }

    protected void onTransitionStart(boolean z, int i, int i2) {
        onTransitionStart(z, i2);
    }

    public void runDelayedAnimation() {
        AndroidUtilities.cancelRunOnUIThread(this.delayedAnimationRunnable);
        this.delayedAnimationRunnable.run();
    }

    public void setResizableView(FrameLayout frameLayout) {
        this.resizableViewToSet = frameLayout;
    }

    public void setViewHeight(int i) {
        for (int i2 = 0; i2 < this.viewsToHeightSet.size(); i2++) {
            ((View) this.viewsToHeightSet.get(i2)).getLayoutParams().height = i;
            ((View) this.viewsToHeightSet.get(i2)).requestLayout();
        }
    }

    protected int startOffset() {
        return 0;
    }

    public void startTransition(int r8, int r9, boolean r10) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.ActionBar.AdjustPanLayoutHelper.startTransition(int, int, boolean):void");
    }

    public void stopTransition() {
        ValueAnimator valueAnimator = this.animator;
        if (valueAnimator != null) {
            valueAnimator.cancel();
        }
        this.animationInProgress = false;
        this.usingInsetAnimator = false;
        this.notificationsLocker.unlock();
        this.animator = null;
        setViewHeight(-1);
        this.viewsToHeightSet.clear();
        this.resizableView.requestLayout();
        boolean z = this.isKeyboardVisible;
        onPanTranslationUpdate(0.0f, z ? 1.0f : 0.0f, z);
        if (applyTranslation()) {
            this.parent.setTranslationY(0.0f);
        }
        onTransitionEnd();
    }

    public void updateTransition(float f) {
        if (this.inverse) {
            f = 1.0f - f;
        }
        float f2 = (int) ((this.from * f) + (this.to * (1.0f - f)));
        if (applyTranslation()) {
            this.parent.setTranslationY(f2);
        }
        onPanTranslationUpdate(-f2, f, this.isKeyboardVisible);
    }
}
