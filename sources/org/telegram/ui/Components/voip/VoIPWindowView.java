package org.telegram.ui.Components.voip;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.os.Build;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.WindowManager;
import android.widget.FrameLayout;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.AnimationNotificationsLocker;
import org.telegram.messenger.UserConfig;
import org.telegram.ui.Components.CubicBezierInterpolator;
import org.telegram.ui.VoIPFragment;
import org.webrtc.OrientationHelper;

public abstract class VoIPWindowView extends FrameLayout {
    Activity activity;
    boolean finished;
    protected boolean lockOnScreen;
    private AnimationNotificationsLocker notificationsLocker;
    private int orientationBefore;
    boolean runEnterTransition;
    boolean startDragging;
    float startX;
    float startY;
    VelocityTracker velocityTracker;

    public VoIPWindowView(Activity activity, boolean z) {
        super(activity);
        this.notificationsLocker = new AnimationNotificationsLocker();
        this.activity = activity;
        setSystemUiVisibility(1792);
        setFitsSystemWindows(true);
        this.orientationBefore = activity.getRequestedOrientation();
        activity.setRequestedOrientation(1);
        OrientationHelper.cameraRotationDisabled = true;
        if (z) {
            return;
        }
        this.runEnterTransition = true;
    }

    public WindowManager.LayoutParams createWindowLayoutParams() {
        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        layoutParams.height = -1;
        layoutParams.format = -2;
        layoutParams.width = -1;
        layoutParams.gravity = 51;
        layoutParams.type = 99;
        layoutParams.screenOrientation = 1;
        int i = Build.VERSION.SDK_INT;
        if (i >= 28) {
            layoutParams.layoutInDisplayCutoutMode = 1;
        }
        layoutParams.flags = i >= 21 ? -2147286784 : 131072;
        layoutParams.flags |= 2621568;
        return layoutParams;
    }

    public void finish() {
        finish(330L);
    }

    public void finish(long j) {
        if (this.finished) {
            return;
        }
        this.finished = true;
        VoIPFragment.clearInstance();
        if (this.lockOnScreen) {
            try {
                ((WindowManager) this.activity.getSystemService("window")).removeView(this);
            } catch (Exception unused) {
            }
        } else {
            int i = UserConfig.selectedAccount;
            this.notificationsLocker.lock();
            animate().translationY(getMeasuredHeight()).alpha(0.0f).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animator) {
                    VoIPWindowView.this.notificationsLocker.unlock();
                    if (VoIPWindowView.this.getParent() != null) {
                        VoIPWindowView voIPWindowView = VoIPWindowView.this;
                        voIPWindowView.activity.setRequestedOrientation(voIPWindowView.orientationBefore);
                        WindowManager windowManager = (WindowManager) VoIPWindowView.this.activity.getSystemService("window");
                        VoIPWindowView.this.setVisibility(8);
                        try {
                            windowManager.removeView(VoIPWindowView.this);
                        } catch (Exception unused2) {
                        }
                        OrientationHelper.cameraRotationDisabled = false;
                    }
                }
            }).setDuration(j).setInterpolator(CubicBezierInterpolator.DEFAULT).start();
        }
    }

    public void finishImmediate() {
        if (getParent() != null) {
            this.activity.setRequestedOrientation(this.orientationBefore);
            WindowManager windowManager = (WindowManager) this.activity.getSystemService("window");
            setVisibility(8);
            windowManager.removeView(this);
            OrientationHelper.cameraRotationDisabled = false;
        }
    }

    public boolean isLockOnScreen() {
        return this.lockOnScreen;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent motionEvent) {
        return onTouchEvent(motionEvent);
    }

    @Override
    protected void onMeasure(int i, int i2) {
        super.onMeasure(i, i2);
        if (this.runEnterTransition) {
            return;
        }
        this.runEnterTransition = true;
        startEnterTransition();
    }

    @Override
    public boolean onTouchEvent(MotionEvent motionEvent) {
        if (this.lockOnScreen) {
            return false;
        }
        if (motionEvent.getAction() == 0) {
            this.startX = motionEvent.getX();
            this.startY = motionEvent.getY();
            if (this.velocityTracker == null) {
                this.velocityTracker = VelocityTracker.obtain();
            }
            this.velocityTracker.clear();
        } else {
            if (motionEvent.getAction() == 2) {
                float x = motionEvent.getX() - this.startX;
                float y = motionEvent.getY() - this.startY;
                if (!this.startDragging && Math.abs(y) > AndroidUtilities.getPixelsInCM(0.4f, true) && Math.abs(y) / 3.0f > x) {
                    this.startY = motionEvent.getY();
                    this.startDragging = true;
                    y = 0.0f;
                }
                if (this.startDragging) {
                    float f = y >= 0.0f ? y : 0.0f;
                    if (this.velocityTracker == null) {
                        this.velocityTracker = VelocityTracker.obtain();
                    }
                    this.velocityTracker.addMovement(motionEvent);
                    setTranslationY(f);
                }
                return this.startDragging;
            }
            if (motionEvent.getAction() == 1 || motionEvent.getAction() == 3) {
                float translationY = getTranslationY();
                if (this.velocityTracker == null) {
                    this.velocityTracker = VelocityTracker.obtain();
                }
                this.velocityTracker.computeCurrentVelocity(1000);
                float xVelocity = this.velocityTracker.getXVelocity();
                float yVelocity = this.velocityTracker.getYVelocity();
                if (translationY >= getMeasuredHeight() / 3.0f || (xVelocity >= 3500.0f && xVelocity >= yVelocity)) {
                    finish(Math.max((int) ((200.0f / getMeasuredHeight()) * (getMeasuredHeight() - getTranslationY())), 50));
                } else {
                    animate().translationY(0.0f).start();
                }
                this.startDragging = false;
            }
        }
        return false;
    }

    public void requestFullscreen(boolean z) {
        setSystemUiVisibility(z ? getSystemUiVisibility() | 4 : getSystemUiVisibility() & (-5));
    }

    public void setLockOnScreen(boolean z) {
        this.lockOnScreen = z;
    }

    public void startEnterTransition() {
        if (this.lockOnScreen) {
            return;
        }
        setTranslationY(getMeasuredHeight());
        setAlpha(0.0f);
        animate().translationY(0.0f).alpha(1.0f).setDuration(330L).setInterpolator(CubicBezierInterpolator.DEFAULT).start();
    }
}
