package org.telegram.ui.Components;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.Property;
import android.view.MotionEvent;
import android.view.View;
import android.view.accessibility.AccessibilityNodeInfo;
import android.view.animation.DecelerateInterpolator;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.R;

public class ShutterButton extends View {
    private ShutterButtonDelegate delegate;
    private DecelerateInterpolator interpolator;
    private long lastUpdateTime;
    private Runnable longPressed;
    private boolean pressed;
    private boolean processRelease;
    private Paint redPaint;
    private float redProgress;
    private Drawable shadowDrawable;
    private State state;
    private long totalTime;
    private Paint whitePaint;

    public interface ShutterButtonDelegate {
        boolean onTranslationChanged(float f, float f2);

        void shutterCancel();

        boolean shutterLongPressed();

        void shutterReleased();
    }

    public enum State {
        DEFAULT,
        RECORDING
    }

    public ShutterButton(Context context) {
        super(context);
        this.interpolator = new DecelerateInterpolator();
        this.longPressed = new Runnable() {
            @Override
            public void run() {
                if (ShutterButton.this.delegate == null || ShutterButton.this.delegate.shutterLongPressed()) {
                    return;
                }
                ShutterButton.this.processRelease = false;
            }
        };
        this.shadowDrawable = getResources().getDrawable(R.drawable.camera_btn);
        Paint paint = new Paint(1);
        this.whitePaint = paint;
        Paint.Style style = Paint.Style.FILL;
        paint.setStyle(style);
        this.whitePaint.setColor(-1);
        Paint paint2 = new Paint(1);
        this.redPaint = paint2;
        paint2.setStyle(style);
        this.redPaint.setColor(-3324089);
        this.state = State.DEFAULT;
    }

    private void setHighlighted(boolean z) {
        AnimatorSet animatorSet = new AnimatorSet();
        if (z) {
            animatorSet.playTogether(ObjectAnimator.ofFloat(this, (Property<ShutterButton, Float>) View.SCALE_X, 1.06f), ObjectAnimator.ofFloat(this, (Property<ShutterButton, Float>) View.SCALE_Y, 1.06f));
        } else {
            animatorSet.playTogether(ObjectAnimator.ofFloat(this, (Property<ShutterButton, Float>) View.SCALE_X, 1.0f), ObjectAnimator.ofFloat(this, (Property<ShutterButton, Float>) View.SCALE_Y, 1.0f));
            animatorSet.setStartDelay(40L);
        }
        animatorSet.setDuration(120L);
        animatorSet.setInterpolator(this.interpolator);
        animatorSet.start();
    }

    public ShutterButtonDelegate getDelegate() {
        return this.delegate;
    }

    public State getState() {
        return this.state;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        float dp;
        int measuredWidth = getMeasuredWidth() / 2;
        int measuredHeight = getMeasuredHeight() / 2;
        this.shadowDrawable.setBounds(measuredWidth - AndroidUtilities.dp(36.0f), measuredHeight - AndroidUtilities.dp(36.0f), AndroidUtilities.dp(36.0f) + measuredWidth, AndroidUtilities.dp(36.0f) + measuredHeight);
        this.shadowDrawable.draw(canvas);
        if (!this.pressed && getScaleX() == 1.0f) {
            if (this.redProgress != 0.0f) {
                this.redProgress = 0.0f;
                return;
            }
            return;
        }
        float scaleX = (getScaleX() - 1.0f) / 0.06f;
        this.whitePaint.setAlpha((int) (255.0f * scaleX));
        float f = measuredWidth;
        float f2 = measuredHeight;
        canvas.drawCircle(f, f2, AndroidUtilities.dp(26.0f), this.whitePaint);
        if (this.state == State.RECORDING) {
            if (this.redProgress != 1.0f) {
                long abs = Math.abs(System.currentTimeMillis() - this.lastUpdateTime);
                if (abs > 17) {
                    abs = 17;
                }
                long j = this.totalTime + abs;
                this.totalTime = j;
                if (j > 120) {
                    this.totalTime = 120L;
                }
                this.redProgress = this.interpolator.getInterpolation(((float) this.totalTime) / 120.0f);
                invalidate();
            }
            dp = AndroidUtilities.dp(26.5f) * scaleX;
            scaleX = this.redProgress;
        } else if (this.redProgress == 0.0f) {
            return;
        } else {
            dp = AndroidUtilities.dp(26.5f);
        }
        canvas.drawCircle(f, f2, dp * scaleX, this.redPaint);
    }

    @Override
    public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo accessibilityNodeInfo) {
        AccessibilityNodeInfo.AccessibilityAction accessibilityAction;
        int id;
        AccessibilityNodeInfo.AccessibilityAction accessibilityAction2;
        int id2;
        super.onInitializeAccessibilityNodeInfo(accessibilityNodeInfo);
        accessibilityNodeInfo.setClassName("android.widget.Button");
        accessibilityNodeInfo.setClickable(true);
        accessibilityNodeInfo.setLongClickable(true);
        if (Build.VERSION.SDK_INT >= 21) {
            accessibilityAction = AccessibilityNodeInfo.AccessibilityAction.ACTION_CLICK;
            id = accessibilityAction.getId();
            accessibilityNodeInfo.addAction(new AccessibilityNodeInfo.AccessibilityAction(id, LocaleController.getString(R.string.AccActionTakePicture)));
            accessibilityAction2 = AccessibilityNodeInfo.AccessibilityAction.ACTION_LONG_CLICK;
            id2 = accessibilityAction2.getId();
            accessibilityNodeInfo.addAction(new AccessibilityNodeInfo.AccessibilityAction(id2, LocaleController.getString(R.string.AccActionRecordVideo)));
        }
    }

    @Override
    protected void onMeasure(int i, int i2) {
        setMeasuredDimension(AndroidUtilities.dp(84.0f), AndroidUtilities.dp(84.0f));
    }

    @Override
    public boolean onTouchEvent(MotionEvent motionEvent) {
        float x = motionEvent.getX();
        float y = motionEvent.getY();
        int action = motionEvent.getAction();
        if (action == 0) {
            AndroidUtilities.runOnUIThread(this.longPressed, 800L);
            this.pressed = true;
            this.processRelease = true;
            setHighlighted(true);
        } else if (action == 1) {
            setHighlighted(false);
            AndroidUtilities.cancelRunOnUIThread(this.longPressed);
            if (this.processRelease) {
                this.delegate.shutterReleased();
            }
        } else if (action == 2) {
            if (x >= 0.0f && x <= getMeasuredWidth()) {
                x = 0.0f;
            }
            if (y >= 0.0f && y <= getMeasuredHeight()) {
                y = 0.0f;
            }
            if (this.delegate.onTranslationChanged(x, y)) {
                AndroidUtilities.cancelRunOnUIThread(this.longPressed);
                if (this.state == State.RECORDING) {
                    this.processRelease = false;
                    setHighlighted(false);
                    this.delegate.shutterCancel();
                    setState(State.DEFAULT, true);
                }
            }
        } else if (action == 3) {
            setHighlighted(false);
            this.pressed = false;
        }
        return true;
    }

    public void setDelegate(ShutterButtonDelegate shutterButtonDelegate) {
        this.delegate = shutterButtonDelegate;
    }

    @Override
    public void setScaleX(float f) {
        super.setScaleX(f);
        invalidate();
    }

    public void setState(org.telegram.ui.Components.ShutterButton.State r2, boolean r3) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.ShutterButton.setState(org.telegram.ui.Components.ShutterButton$State, boolean):void");
    }
}
