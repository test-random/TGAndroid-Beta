package org.telegram.ui.Stories.recorder;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.view.View;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.R;
import org.telegram.ui.Components.AnimatedFloat;
import org.telegram.ui.Components.AnimatedTextView;
import org.telegram.ui.Components.ButtonBounce;
import org.telegram.ui.Components.CubicBezierInterpolator;
import org.telegram.ui.Components.RLottieDrawable;

public class TrashView extends View {
    private final ButtonBounce bounce;
    private final Paint circlePaint;
    private boolean dragged;
    private final AnimatedFloat draggedT;
    private final RLottieDrawable drawable;
    private final Paint greyPaint;
    private final AnimatedTextView.AnimatedTextDrawable textDrawable;

    public TrashView(Context context) {
        super(context);
        Paint paint = new Paint(1);
        this.circlePaint = paint;
        Paint paint2 = new Paint(1);
        this.greyPaint = paint2;
        this.bounce = new ButtonBounce(this);
        CubicBezierInterpolator cubicBezierInterpolator = CubicBezierInterpolator.EASE_OUT_QUINT;
        this.draggedT = new AnimatedFloat(this, 0L, 240L, cubicBezierInterpolator);
        paint.setColor(-1);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(AndroidUtilities.dpf2(2.66f));
        paint.setShadowLayer(AndroidUtilities.dpf2(3.0f), 0.0f, AndroidUtilities.dp(1.66f), 805306368);
        paint2.setColor(855638016);
        int i = R.raw.group_pip_delete_icon;
        RLottieDrawable rLottieDrawable = new RLottieDrawable(i, "" + i, AndroidUtilities.dp(48.0f), AndroidUtilities.dp(48.0f), true, null);
        this.drawable = rLottieDrawable;
        rLottieDrawable.setMasterParent(this);
        rLottieDrawable.setColorFilter(new PorterDuffColorFilter(-1, PorterDuff.Mode.MULTIPLY));
        rLottieDrawable.setPlayInDirectionOfCustomEndFrame(true);
        rLottieDrawable.setCustomEndFrame(0);
        rLottieDrawable.setAllowDecodeSingleFrame(true);
        rLottieDrawable.start();
        AnimatedTextView.AnimatedTextDrawable animatedTextDrawable = new AnimatedTextView.AnimatedTextDrawable(true, true, false);
        this.textDrawable = animatedTextDrawable;
        animatedTextDrawable.setAnimationProperties(0.3f, 0L, 250L, cubicBezierInterpolator);
        animatedTextDrawable.setOverrideFullWidth(AndroidUtilities.displaySize.x);
        animatedTextDrawable.setTextSize(AndroidUtilities.dp(14.0f));
        animatedTextDrawable.setTextColor(-1);
        animatedTextDrawable.setShadowLayer(AndroidUtilities.dpf2(1.33f), 0.0f, AndroidUtilities.dp(1.0f), 1073741824);
        animatedTextDrawable.setText(LocaleController.getString(R.string.TrashHintDrag));
        animatedTextDrawable.setGravity(17);
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        float dp = AndroidUtilities.dp(30.0f);
        float width = getWidth() / 2.0f;
        float height = getHeight() / 2.0f;
        float dp2 = (AndroidUtilities.dp(3.0f) * this.draggedT.set(this.dragged)) + dp;
        canvas.drawCircle(width, height, dp2, this.greyPaint);
        canvas.drawCircle(width, height, dp2, this.circlePaint);
        float dp3 = AndroidUtilities.dp(48.0f) / 2.0f;
        this.drawable.setBounds((int) (width - dp3), (int) (height - dp3), (int) (width + dp3), (int) (dp3 + height));
        this.drawable.draw(canvas);
        this.textDrawable.setBounds(0, (int) (height + dp + AndroidUtilities.dp(7.0f)), getWidth(), getHeight());
        this.textDrawable.draw(canvas);
    }

    public void onDragInfo(boolean z, boolean z2) {
        this.bounce.setPressed(z);
        this.textDrawable.setText(LocaleController.getString((z || z2) ? R.string.TrashHintRelease : R.string.TrashHintDrag));
        boolean z3 = z && !z2;
        this.dragged = z3;
        if (z3) {
            if (this.drawable.getCurrentFrame() > 34) {
                this.drawable.setCurrentFrame(0, false);
            }
            this.drawable.setCustomEndFrame(33);
        } else {
            this.drawable.setCustomEndFrame(z2 ? 66 : 0);
        }
        this.drawable.start();
        invalidate();
    }

    @Override
    protected void onMeasure(int i, int i2) {
        setMeasuredDimension(i, AndroidUtilities.dp(120.0f));
    }

    @Override
    protected boolean verifyDrawable(Drawable drawable) {
        return drawable == this.textDrawable || super.verifyDrawable(drawable);
    }
}
