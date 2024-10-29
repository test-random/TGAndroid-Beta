package org.telegram.ui;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.R;
import org.telegram.ui.Components.AnimatedTextView;

public class ChooseQualityLayout$QualityIcon extends Drawable {
    private final Drawable base;
    private final Paint bgLinePaint;
    private final Paint bgPaint = new Paint(1);
    public final AnimatedTextView.AnimatedTextDrawable bottomText;
    private final Drawable.Callback callback;
    private final RectF rect;
    private float rotation;
    public final AnimatedTextView.AnimatedTextDrawable topText;

    public ChooseQualityLayout$QualityIcon(Context context) {
        Paint paint = new Paint(1);
        this.bgLinePaint = paint;
        this.rect = new RectF();
        AnimatedTextView.AnimatedTextDrawable animatedTextDrawable = new AnimatedTextView.AnimatedTextDrawable();
        this.topText = animatedTextDrawable;
        AnimatedTextView.AnimatedTextDrawable animatedTextDrawable2 = new AnimatedTextView.AnimatedTextDrawable();
        this.bottomText = animatedTextDrawable2;
        Drawable.Callback callback = new Drawable.Callback() {
            @Override
            public void invalidateDrawable(Drawable drawable) {
                ChooseQualityLayout$QualityIcon.this.invalidateSelf();
            }

            @Override
            public void scheduleDrawable(Drawable drawable, Runnable runnable, long j) {
                ChooseQualityLayout$QualityIcon.this.scheduleSelf(runnable, j);
            }

            @Override
            public void unscheduleDrawable(Drawable drawable, Runnable runnable) {
                ChooseQualityLayout$QualityIcon.this.unscheduleSelf(runnable);
            }
        };
        this.callback = callback;
        this.base = context.getResources().getDrawable(R.drawable.msg_settings).mutate();
        paint.setColor(-1);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_OUT));
        animatedTextDrawable.setTypeface(AndroidUtilities.getTypeface("fonts/num.otf"));
        animatedTextDrawable.setTextColor(-16777216);
        animatedTextDrawable.setTextSize(AndroidUtilities.dp(7.0f));
        animatedTextDrawable.setCallback(callback);
        animatedTextDrawable.setGravity(17);
        animatedTextDrawable.setOverrideFullWidth(AndroidUtilities.displaySize.x);
        animatedTextDrawable2.setTypeface(AndroidUtilities.getTypeface("fonts/num.otf"));
        animatedTextDrawable2.setTextColor(-16777216);
        animatedTextDrawable2.setTextSize(AndroidUtilities.dp(7.0f));
        animatedTextDrawable2.setCallback(callback);
        animatedTextDrawable2.setGravity(17);
        animatedTextDrawable2.setOverrideFullWidth(AndroidUtilities.displaySize.x);
    }

    @Override
    public void draw(Canvas canvas) {
        float dp = (AndroidUtilities.dp(5.0f) * this.topText.isNotEmpty()) + this.topText.getCurrentWidth();
        float dp2 = (AndroidUtilities.dp(5.0f) * this.bottomText.isNotEmpty()) + this.bottomText.getCurrentWidth();
        Rect bounds = getBounds();
        if (dp > 0.0f || dp2 > 0.0f) {
            canvas.saveLayerAlpha(bounds.left, bounds.top, bounds.right, bounds.bottom, 255, 31);
        }
        Rect rect = AndroidUtilities.rectTmp2;
        rect.set(AndroidUtilities.dp(6.0f), AndroidUtilities.dp(6.0f), (AndroidUtilities.dp(6.0f) + bounds.width()) - AndroidUtilities.dp(12.0f), (AndroidUtilities.dp(6.0f) + bounds.height()) - AndroidUtilities.dp(12.0f));
        this.base.setBounds(rect);
        canvas.save();
        canvas.rotate(this.rotation * (-180.0f), bounds.centerX(), bounds.centerY());
        this.base.draw(canvas);
        canvas.restore();
        this.bgPaint.setColor(-1);
        float width = bounds.left + (bounds.width() * 0.98f);
        float height = bounds.top + (bounds.height() * 0.18f);
        float height2 = bounds.top + (bounds.height() * 0.78f);
        float dp3 = AndroidUtilities.dp(10.0f);
        if (dp > 0.0f) {
            float f = dp3 / 2.0f;
            this.rect.set(width - dp, height - f, width, f + height);
            canvas.drawRoundRect(this.rect, AndroidUtilities.dp(3.0f), AndroidUtilities.dp(3.0f), this.bgLinePaint);
        }
        if (dp2 > 0.0f) {
            float f2 = dp3 / 2.0f;
            this.rect.set(width - dp2, height2 - f2, width, f2 + height2);
            canvas.drawRoundRect(this.rect, AndroidUtilities.dp(3.0f), AndroidUtilities.dp(3.0f), this.bgLinePaint);
        }
        if (dp > 0.0f || dp2 > 0.0f) {
            canvas.restore();
        }
        if (dp > 0.0f) {
            this.bgPaint.setAlpha((int) (this.topText.isNotEmpty() * 255.0f));
            AnimatedTextView.AnimatedTextDrawable animatedTextDrawable = this.topText;
            animatedTextDrawable.setAlpha((int) (animatedTextDrawable.isNotEmpty() * 255.0f));
            float f3 = dp3 / 2.0f;
            this.rect.set(width - dp, height - f3, width, height + f3);
            this.rect.inset(AndroidUtilities.dp(1.0f), AndroidUtilities.dp(1.0f));
            canvas.drawRoundRect(this.rect, AndroidUtilities.dp(3.0f), AndroidUtilities.dp(3.0f), this.bgPaint);
            this.rect.inset(-AndroidUtilities.dp(1.0f), -AndroidUtilities.dp(1.0f));
            this.topText.setBounds(this.rect);
            this.topText.draw(canvas);
        }
        if (dp2 > 0.0f) {
            this.bgPaint.setAlpha((int) (this.bottomText.isNotEmpty() * 255.0f));
            AnimatedTextView.AnimatedTextDrawable animatedTextDrawable2 = this.bottomText;
            animatedTextDrawable2.setAlpha((int) (animatedTextDrawable2.isNotEmpty() * 255.0f));
            float f4 = dp3 / 2.0f;
            this.rect.set(width - dp2, height2 - f4, width, height2 + f4);
            this.rect.inset(AndroidUtilities.dp(1.0f), AndroidUtilities.dp(1.0f));
            canvas.drawRoundRect(this.rect, AndroidUtilities.dp(3.0f), AndroidUtilities.dp(3.0f), this.bgPaint);
            this.rect.inset(-AndroidUtilities.dp(1.0f), -AndroidUtilities.dp(1.0f));
            this.bottomText.setBounds(this.rect);
            this.bottomText.draw(canvas);
        }
    }

    @Override
    public int getIntrinsicHeight() {
        return this.base.getIntrinsicHeight() + AndroidUtilities.dp(12.0f);
    }

    @Override
    public int getIntrinsicWidth() {
        return this.base.getIntrinsicWidth() + AndroidUtilities.dp(12.0f);
    }

    @Override
    public int getOpacity() {
        return this.base.getOpacity();
    }

    @Override
    public void setAlpha(int i) {
        this.base.setAlpha(i);
    }

    @Override
    public void setColorFilter(ColorFilter colorFilter) {
        this.base.setColorFilter(colorFilter);
    }
}
