package org.telegram.ui.Stories.recorder;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import androidx.core.graphics.ColorUtils;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.Utilities;
import org.telegram.ui.Components.CubicBezierInterpolator;
import org.telegram.ui.Stories.recorder.FlashViews;

public abstract class PhotoVideoSwitcherView extends View implements FlashViews.Invertable {
    private ValueAnimator animator;
    private boolean mIsScrolling;
    private boolean mIsTouch;
    private long mLastTouchTime;
    private float mLastX;
    private int mTouchSlop;
    private VelocityTracker mVelocityTracker;
    private float mode;
    private float modeAtTouchDown;
    private Utilities.Callback onSwitchModeListener;
    private Utilities.Callback onSwitchingModeListener;
    private RectF photoRect;
    private StaticLayout photoText;
    private float photoTextHeight;
    private float photoTextLeft;
    private float photoTextWidth;
    private float scrollWidth;
    private boolean scrolledEnough;
    private Paint selectorPaint;
    private RectF selectorRect;
    private TextPaint textPaint;
    private RectF videoRect;
    private StaticLayout videoText;
    private float videoTextHeight;
    private float videoTextLeft;
    private float videoTextWidth;

    public PhotoVideoSwitcherView(Context context) {
        super(context);
        this.textPaint = new TextPaint(1);
        this.selectorPaint = new Paint(1);
        this.photoRect = new RectF();
        this.videoRect = new RectF();
        this.selectorRect = new RectF();
        this.selectorPaint.setColor(855638015);
        this.textPaint.setColor(-1);
        this.textPaint.setTypeface(AndroidUtilities.bold());
        this.textPaint.setTextSize(AndroidUtilities.dpf2(14.0f));
        this.textPaint.setShadowLayer(AndroidUtilities.dpf2(1.0f), 0.0f, AndroidUtilities.dpf2(0.4f), 855638016);
        String string = LocaleController.getString("StoryPhoto");
        string = string == null ? "Photo" : string;
        TextPaint textPaint = this.textPaint;
        int i = AndroidUtilities.displaySize.x / 2;
        Layout.Alignment alignment = Layout.Alignment.ALIGN_NORMAL;
        StaticLayout staticLayout = new StaticLayout(string, textPaint, i, alignment, 1.0f, 0.0f, false);
        this.photoText = staticLayout;
        this.photoTextLeft = staticLayout.getLineCount() > 0 ? this.photoText.getLineLeft(0) : 0.0f;
        this.photoTextWidth = this.photoText.getLineCount() > 0 ? this.photoText.getLineWidth(0) : 0.0f;
        this.photoTextHeight = this.photoText.getHeight();
        String string2 = LocaleController.getString("StoryVideo");
        StaticLayout staticLayout2 = new StaticLayout(string2 == null ? "Video" : string2, this.textPaint, AndroidUtilities.displaySize.x / 2, alignment, 1.0f, 0.0f, false);
        this.videoText = staticLayout2;
        this.videoTextLeft = staticLayout2.getLineCount() > 0 ? this.videoText.getLineLeft(0) : 0.0f;
        this.videoTextWidth = this.videoText.getLineCount() > 0 ? this.videoText.getLineWidth(0) : 0.0f;
        this.videoTextHeight = this.videoText.getHeight();
        this.scrollWidth = AndroidUtilities.dp(32.0f) + (this.photoTextWidth / 2.0f) + (this.videoTextWidth / 2.0f);
        this.mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
    }

    private float getScrollCx() {
        return (getWidth() / 2.0f) + AndroidUtilities.lerp(AndroidUtilities.dp(16.0f) + (this.photoTextWidth / 2.0f), -(AndroidUtilities.dp(16.0f) + (this.videoTextWidth / 2.0f)), this.mode);
    }

    public void lambda$switchMode$0(ValueAnimator valueAnimator) {
        float floatValue = ((Float) valueAnimator.getAnimatedValue()).floatValue();
        this.mode = floatValue;
        Utilities.Callback callback = this.onSwitchingModeListener;
        if (callback != null) {
            callback.run(Float.valueOf(Utilities.clamp(floatValue, 1.0f, 0.0f)));
        }
        invalidate();
    }

    protected abstract boolean allowTouch();

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
        float height = getHeight() / 2.0f;
        float scrollCx = getScrollCx();
        int i = -AndroidUtilities.dp(1.0f);
        float dp = AndroidUtilities.dp(26.0f) / 2.0f;
        float f = i;
        float f2 = (height - dp) + f;
        float f3 = height + dp + f;
        this.photoRect.set((scrollCx - AndroidUtilities.dp(28.0f)) - this.photoTextWidth, f2, scrollCx - AndroidUtilities.dp(4.0f), f3);
        this.videoRect.set(AndroidUtilities.dp(4.0f) + scrollCx, f2, AndroidUtilities.dp(28.0f) + scrollCx + this.videoTextWidth, f3);
        AndroidUtilities.lerp(this.photoRect, this.videoRect, Utilities.clamp(this.mode, 1.025f, -0.025f), this.selectorRect);
        canvas.drawRoundRect(this.selectorRect, dp, dp, this.selectorPaint);
        canvas.save();
        canvas.translate(((scrollCx - AndroidUtilities.dp(16.0f)) - this.photoTextWidth) - this.photoTextLeft, (height - (this.photoTextHeight / 2.0f)) + f);
        this.photoText.draw(canvas);
        canvas.restore();
        canvas.save();
        canvas.translate((scrollCx + AndroidUtilities.dp(16.0f)) - this.videoTextLeft, (height - (this.videoTextHeight / 2.0f)) + f);
        this.videoText.draw(canvas);
        canvas.restore();
    }

    @Override
    public boolean onTouchEvent(android.view.MotionEvent r8) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Stories.recorder.PhotoVideoSwitcherView.onTouchEvent(android.view.MotionEvent):boolean");
    }

    public void scrollX(float f) {
        if (!this.mIsScrolling && Math.abs(f) > this.mTouchSlop) {
            this.mIsScrolling = true;
            this.modeAtTouchDown = this.mode;
        }
        if (this.mIsScrolling) {
            float f2 = this.mode;
            if ((f2 <= 0.0f && f < 0.0f) || (f2 >= 1.0f && f > 0.0f)) {
                f *= 0.2f;
            }
            float f3 = f2 + ((f / this.scrollWidth) / 2.5f);
            this.mode = f3;
            float clamp = Utilities.clamp(f3, 1.2f, -0.2f);
            this.mode = clamp;
            Utilities.Callback callback = this.onSwitchingModeListener;
            if (callback != null) {
                callback.run(Float.valueOf(Utilities.clamp(clamp, 1.0f, 0.0f)));
            }
            invalidate();
        }
    }

    @Override
    public void setInvert(float f) {
        this.selectorPaint.setColor(ColorUtils.blendARGB(855638015, 536870912, f));
        this.textPaint.setColor(ColorUtils.blendARGB(-1, -16777216, f));
    }

    public void setOnSwitchModeListener(Utilities.Callback<Boolean> callback) {
        this.onSwitchModeListener = callback;
    }

    public void setOnSwitchingModeListener(Utilities.Callback<Float> callback) {
        this.onSwitchingModeListener = callback;
    }

    public boolean stopScroll(float f) {
        if (!this.mIsScrolling) {
            this.scrolledEnough = false;
            return false;
        }
        this.mIsScrolling = false;
        boolean z = Math.abs(f) <= 500.0f ? this.mode > 0.5f : f < 0.0f;
        switchMode(z);
        Utilities.Callback callback = this.onSwitchModeListener;
        if (callback != null) {
            callback.run(Boolean.valueOf(z));
        }
        this.scrolledEnough = false;
        return true;
    }

    public void switchMode(boolean z) {
        ValueAnimator valueAnimator = this.animator;
        if (valueAnimator != null) {
            valueAnimator.cancel();
        }
        ValueAnimator ofFloat = ValueAnimator.ofFloat(this.mode, z ? 1.0f : 0.0f);
        this.animator = ofFloat;
        ofFloat.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public final void onAnimationUpdate(ValueAnimator valueAnimator2) {
                PhotoVideoSwitcherView.this.lambda$switchMode$0(valueAnimator2);
            }
        });
        this.animator.setDuration(320L);
        this.animator.setInterpolator(CubicBezierInterpolator.EASE_OUT_QUINT);
        this.animator.start();
    }
}
