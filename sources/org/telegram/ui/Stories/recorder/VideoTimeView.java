package org.telegram.ui.Stories.recorder;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.view.View;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.ui.Components.AnimatedTextView;
import org.telegram.ui.Components.CubicBezierInterpolator;

public class VideoTimeView extends View {
    private final Paint backgroundPaint;
    private boolean shown;
    private final AnimatedTextView.AnimatedTextDrawable textDrawable;

    public VideoTimeView(Context context) {
        super(context);
        Paint paint = new Paint(1);
        this.backgroundPaint = paint;
        this.shown = true;
        paint.setColor(Integer.MIN_VALUE);
        AnimatedTextView.AnimatedTextDrawable animatedTextDrawable = new AnimatedTextView.AnimatedTextDrawable(false, true, true);
        this.textDrawable = animatedTextDrawable;
        animatedTextDrawable.setAnimationProperties(0.2f, 0L, 200L, CubicBezierInterpolator.EASE_OUT_QUINT);
        animatedTextDrawable.setTextSize(AndroidUtilities.dp(13.0f));
        animatedTextDrawable.setTextColor(-1);
        animatedTextDrawable.setTypeface(AndroidUtilities.bold());
        animatedTextDrawable.setCallback(this);
        animatedTextDrawable.setGravity(1);
        setTime(0L, false);
    }

    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        float currentWidth = this.textDrawable.getCurrentWidth();
        RectF rectF = AndroidUtilities.rectTmp;
        rectF.set(((getWidth() - currentWidth) / 2.0f) - AndroidUtilities.dp(6.0f), AndroidUtilities.dp(2.0f), ((getWidth() + currentWidth) / 2.0f) + AndroidUtilities.dp(6.0f), AndroidUtilities.dp(23.0f));
        canvas.drawRoundRect(rectF, AndroidUtilities.dp(5.0f), AndroidUtilities.dp(5.0f), this.backgroundPaint);
        this.textDrawable.setBounds((int) rectF.left, ((int) rectF.top) - AndroidUtilities.dp(1.0f), (int) rectF.right, (int) rectF.bottom);
        this.textDrawable.draw(canvas);
    }

    @Override
    protected void onMeasure(int i, int i2) {
        super.onMeasure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(i), 1073741824), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(25.0f), 1073741824));
    }

    public void setTime(long j, boolean z) {
        long j2 = j / 1000;
        long j3 = j2 % 60;
        long j4 = j2 - j3;
        long j5 = j4 / 60;
        long j6 = (j4 - (j5 * 60)) / 60;
        StringBuilder sb = new StringBuilder(8);
        if (j6 < 10) {
            sb.append('0');
        }
        sb.append(j6);
        sb.append(':');
        if (j5 < 10) {
            sb.append('0');
        }
        sb.append(j5);
        sb.append(':');
        if (j3 < 10) {
            sb.append('0');
        }
        sb.append(j3);
        if (TextUtils.equals(sb, this.textDrawable.getText())) {
            return;
        }
        this.textDrawable.cancelAnimation();
        this.textDrawable.setText(sb, z && !LocaleController.isRTL);
    }

    public void show(boolean z, boolean z2) {
        if (z == this.shown && z2) {
            return;
        }
        this.shown = z;
        animate().cancel();
        if (z2) {
            animate().translationY(z ? 0.0f : AndroidUtilities.dp(6.0f)).alpha(z ? 1.0f : 0.0f).scaleX(z ? 1.0f : 0.8f).scaleY(z ? 1.0f : 0.8f).setInterpolator(CubicBezierInterpolator.EASE_OUT_QUINT).setDuration(220L).start();
            return;
        }
        setTranslationY(z ? 0.0f : AndroidUtilities.dp(6.0f));
        setScaleX(z ? 1.0f : 0.8f);
        setScaleY(z ? 1.0f : 0.8f);
        setAlpha(z ? 1.0f : 0.0f);
    }

    @Override
    protected boolean verifyDrawable(Drawable drawable) {
        return this.textDrawable == drawable || super.verifyDrawable(drawable);
    }
}
