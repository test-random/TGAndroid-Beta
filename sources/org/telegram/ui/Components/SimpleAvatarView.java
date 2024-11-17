package org.telegram.ui.Components;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.view.View;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ImageReceiver;
import org.telegram.tgnet.TLObject;
import org.telegram.ui.ActionBar.Theme;

public class SimpleAvatarView extends View {
    private ValueAnimator animator;
    private AvatarDrawable avatarDrawable;
    private ImageReceiver avatarImage;
    private boolean isAvatarHidden;
    private Paint selectPaint;
    private float selectProgress;

    public SimpleAvatarView(Context context) {
        super(context);
        this.avatarImage = new ImageReceiver(this);
        this.avatarDrawable = new AvatarDrawable();
        this.selectPaint = new Paint(1);
        this.avatarImage.setRoundRadius(AndroidUtilities.dp(28.0f));
        this.selectPaint.setStrokeWidth(AndroidUtilities.dp(2.0f));
        this.selectPaint.setStyle(Paint.Style.STROKE);
    }

    public void lambda$setSelected$0(ValueAnimator valueAnimator) {
        this.selectProgress = ((Float) valueAnimator.getAnimatedValue()).floatValue();
        invalidate();
    }

    @Override
    public boolean isSelected() {
        return this.selectProgress == 1.0f;
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        this.avatarImage.onAttachedToWindow();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        this.avatarImage.onDetachedFromWindow();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.save();
        float f = (this.selectProgress * 0.1f) + 0.9f;
        canvas.scale(f, f);
        this.selectPaint.setColor(Theme.getColor(Theme.key_dialogTextBlue));
        this.selectPaint.setAlpha((int) (Color.alpha(r0.getColor()) * this.selectProgress));
        float strokeWidth = this.selectPaint.getStrokeWidth();
        RectF rectF = AndroidUtilities.rectTmp;
        rectF.set(strokeWidth, strokeWidth, getWidth() - strokeWidth, getHeight() - strokeWidth);
        canvas.drawArc(rectF, -90.0f, this.selectProgress * 360.0f, false, this.selectPaint);
        canvas.restore();
        if (this.isAvatarHidden) {
            return;
        }
        float strokeWidth2 = this.selectPaint.getStrokeWidth() * 2.5f * this.selectProgress;
        float f2 = 2.0f * strokeWidth2;
        this.avatarImage.setImageCoords(strokeWidth2, strokeWidth2, getWidth() - f2, getHeight() - f2);
        this.avatarImage.draw(canvas);
    }

    public void setAvatar(TLObject tLObject) {
        this.avatarDrawable.setInfo(tLObject);
        this.avatarImage.setForUserOrChat(tLObject, this.avatarDrawable);
    }

    public void setHideAvatar(boolean z) {
        this.isAvatarHidden = z;
        invalidate();
    }

    public void setSelected(boolean z, boolean z2) {
        ValueAnimator valueAnimator = this.animator;
        if (valueAnimator != null) {
            valueAnimator.cancel();
        }
        if (!z2) {
            this.selectProgress = z ? 1.0f : 0.0f;
            invalidate();
            return;
        }
        ValueAnimator duration = ValueAnimator.ofFloat(this.selectProgress, z ? 1.0f : 0.0f).setDuration(200L);
        duration.setInterpolator(CubicBezierInterpolator.DEFAULT);
        duration.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public final void onAnimationUpdate(ValueAnimator valueAnimator2) {
                SimpleAvatarView.this.lambda$setSelected$0(valueAnimator2);
            }
        });
        duration.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animator) {
                if (SimpleAvatarView.this.animator == animator) {
                    SimpleAvatarView.this.animator = null;
                }
            }
        });
        duration.start();
        this.animator = duration;
    }
}
