package org.telegram.ui.Components;

import android.animation.TimeInterpolator;
import android.os.SystemClock;
import android.view.View;
import androidx.core.math.MathUtils;
import org.telegram.messenger.AndroidUtilities;

public class AnimatedFloat {
    private boolean firstSet;
    private Runnable invalidate;
    private View parent;
    private float startValue;
    private float targetValue;
    private boolean transition;
    private long transitionDelay;
    private long transitionDuration;
    private TimeInterpolator transitionInterpolator;
    private long transitionStart;
    private float value;

    public AnimatedFloat(float f, View view) {
        this.transitionDelay = 0L;
        this.transitionDuration = 200L;
        this.transitionInterpolator = CubicBezierInterpolator.DEFAULT;
        this.parent = view;
        this.targetValue = f;
        this.value = f;
        this.firstSet = false;
    }

    public AnimatedFloat(float f, View view, long j, long j2, TimeInterpolator timeInterpolator) {
        this.transitionDelay = 0L;
        this.transitionDuration = 200L;
        CubicBezierInterpolator cubicBezierInterpolator = CubicBezierInterpolator.DEFAULT;
        this.parent = view;
        this.targetValue = f;
        this.value = f;
        this.transitionDelay = j;
        this.transitionDuration = j2;
        this.transitionInterpolator = timeInterpolator;
        this.firstSet = false;
    }

    public AnimatedFloat(float f, Runnable runnable, long j, long j2, TimeInterpolator timeInterpolator) {
        this.transitionDelay = 0L;
        this.transitionDuration = 200L;
        CubicBezierInterpolator cubicBezierInterpolator = CubicBezierInterpolator.DEFAULT;
        this.invalidate = runnable;
        this.targetValue = f;
        this.value = f;
        this.transitionDelay = j;
        this.transitionDuration = j2;
        this.transitionInterpolator = timeInterpolator;
        this.firstSet = false;
    }

    public AnimatedFloat(long j, long j2, TimeInterpolator timeInterpolator) {
        this.transitionDelay = 0L;
        this.transitionDuration = 200L;
        CubicBezierInterpolator cubicBezierInterpolator = CubicBezierInterpolator.DEFAULT;
        this.parent = null;
        this.transitionDelay = j;
        this.transitionDuration = j2;
        this.transitionInterpolator = timeInterpolator;
        this.firstSet = true;
    }

    public AnimatedFloat(long j, TimeInterpolator timeInterpolator) {
        this.transitionDelay = 0L;
        this.transitionDuration = 200L;
        CubicBezierInterpolator cubicBezierInterpolator = CubicBezierInterpolator.DEFAULT;
        this.parent = null;
        this.transitionDuration = j;
        this.transitionInterpolator = timeInterpolator;
        this.firstSet = true;
    }

    public AnimatedFloat(View view) {
        this.transitionDelay = 0L;
        this.transitionDuration = 200L;
        this.transitionInterpolator = CubicBezierInterpolator.DEFAULT;
        this.parent = view;
        this.firstSet = true;
    }

    public AnimatedFloat(View view, long j, long j2, TimeInterpolator timeInterpolator) {
        this.transitionDelay = 0L;
        this.transitionDuration = 200L;
        CubicBezierInterpolator cubicBezierInterpolator = CubicBezierInterpolator.DEFAULT;
        this.parent = view;
        this.transitionDelay = j;
        this.transitionDuration = j2;
        this.transitionInterpolator = timeInterpolator;
        this.firstSet = true;
    }

    public AnimatedFloat(View view, long j, TimeInterpolator timeInterpolator) {
        this.transitionDelay = 0L;
        this.transitionDuration = 200L;
        CubicBezierInterpolator cubicBezierInterpolator = CubicBezierInterpolator.DEFAULT;
        this.parent = view;
        this.transitionDuration = j;
        this.transitionInterpolator = timeInterpolator;
        this.firstSet = true;
    }

    public AnimatedFloat(Runnable runnable, long j, long j2, TimeInterpolator timeInterpolator) {
        this.transitionDelay = 0L;
        this.transitionDuration = 200L;
        CubicBezierInterpolator cubicBezierInterpolator = CubicBezierInterpolator.DEFAULT;
        this.invalidate = runnable;
        this.transitionDelay = j;
        this.transitionDuration = j2;
        this.transitionInterpolator = timeInterpolator;
        this.firstSet = true;
    }

    public AnimatedFloat(Runnable runnable, long j, TimeInterpolator timeInterpolator) {
        this.transitionDelay = 0L;
        this.transitionDuration = 200L;
        CubicBezierInterpolator cubicBezierInterpolator = CubicBezierInterpolator.DEFAULT;
        this.invalidate = runnable;
        this.transitionDuration = j;
        this.transitionInterpolator = timeInterpolator;
        this.firstSet = true;
    }

    public void force(boolean z) {
        set(z ? 1.0f : 0.0f, true);
    }

    public float get() {
        return this.value;
    }

    public long getDuration() {
        return this.transitionDuration;
    }

    public float getTransitionProgress() {
        if (this.transition) {
            return MathUtils.clamp(((float) ((SystemClock.elapsedRealtime() - this.transitionStart) - this.transitionDelay)) / ((float) this.transitionDuration), 0.0f, 1.0f);
        }
        return 0.0f;
    }

    public float getTransitionProgressInterpolated() {
        TimeInterpolator timeInterpolator = this.transitionInterpolator;
        return timeInterpolator != null ? timeInterpolator.getInterpolation(getTransitionProgress()) : getTransitionProgress();
    }

    public boolean isInProgress() {
        return this.transition;
    }

    public float set(float f) {
        return set(f, false);
    }

    public float set(float f, boolean z) {
        if (z || this.transitionDuration <= 0 || this.firstSet) {
            this.targetValue = f;
            this.value = f;
            this.transition = false;
            this.firstSet = false;
        } else if (Math.abs(this.targetValue - f) > 1.0E-4f) {
            this.transition = true;
            this.targetValue = f;
            this.startValue = this.value;
            this.transitionStart = SystemClock.elapsedRealtime();
        }
        if (this.transition) {
            long elapsedRealtime = SystemClock.elapsedRealtime();
            float clamp = MathUtils.clamp(((float) ((elapsedRealtime - this.transitionStart) - this.transitionDelay)) / ((float) this.transitionDuration), 0.0f, 1.0f);
            if (elapsedRealtime - this.transitionStart >= this.transitionDelay) {
                TimeInterpolator timeInterpolator = this.transitionInterpolator;
                this.value = timeInterpolator == null ? AndroidUtilities.lerp(this.startValue, this.targetValue, clamp) : AndroidUtilities.lerp(this.startValue, this.targetValue, timeInterpolator.getInterpolation(clamp));
            }
            if (clamp >= 1.0f) {
                this.transition = false;
            } else {
                View view = this.parent;
                if (view != null) {
                    view.invalidate();
                }
                Runnable runnable = this.invalidate;
                if (runnable != null) {
                    runnable.run();
                }
            }
        }
        return this.value;
    }

    public float set(boolean z) {
        return set(z ? 1.0f : 0.0f, false);
    }

    public float set(boolean z, boolean z2) {
        return set(z ? 1.0f : 0.0f, z2);
    }

    public void setDelay(long j) {
        this.transitionDelay = j;
    }

    public void setDuration(long j) {
        this.transitionDuration = j;
    }

    public void setParent(View view) {
        this.parent = view;
    }
}
