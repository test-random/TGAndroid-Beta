package org.telegram.ui.Components;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.view.View;
import androidx.dynamicanimation.animation.DynamicAnimation;
import androidx.dynamicanimation.animation.FloatPropertyCompat;
import androidx.dynamicanimation.animation.SpringAnimation;
import androidx.dynamicanimation.animation.SpringForce;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ImageReceiver;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.R;
import org.telegram.messenger.UserObject;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.SimpleFloatPropertyCompat;

public class SenderSelectView extends View {
    private static final FloatPropertyCompat MENU_PROGRESS = new SimpleFloatPropertyCompat("menuProgress", new SimpleFloatPropertyCompat.Getter() {
        @Override
        public final float get(Object obj) {
            float f;
            f = ((SenderSelectView) obj).menuProgress;
            return f;
        }
    }, new SimpleFloatPropertyCompat.Setter() {
        @Override
        public final void set(Object obj, float f) {
            SenderSelectView.lambda$static$1((SenderSelectView) obj, f);
        }
    }).setMultiplier(100.0f);
    private AvatarDrawable avatarDrawable;
    private ImageReceiver avatarImage;
    private Paint backgroundPaint;
    private ValueAnimator menuAnimator;
    private Paint menuPaint;
    private float menuProgress;
    private SpringAnimation menuSpring;
    private boolean scaleIn;
    private boolean scaleOut;
    private Drawable selectorDrawable;

    public SenderSelectView(Context context) {
        super(context);
        this.avatarImage = new ImageReceiver(this);
        this.avatarDrawable = new AvatarDrawable();
        this.backgroundPaint = new Paint(1);
        this.menuPaint = new Paint(1);
        this.avatarImage.setRoundRadius(AndroidUtilities.dp(28.0f));
        this.menuPaint.setStrokeWidth(AndroidUtilities.dp(2.0f));
        this.menuPaint.setStrokeCap(Paint.Cap.ROUND);
        this.menuPaint.setStyle(Paint.Style.STROKE);
        updateColors();
        setContentDescription(LocaleController.formatString("AccDescrSendAsPeer", R.string.AccDescrSendAsPeer, ""));
    }

    public void lambda$setProgress$2(boolean z, float f, float f2, DynamicAnimation dynamicAnimation, float f3, float f4) {
        if (z) {
            if (f3 > f / 2.0f || !this.scaleIn) {
                return;
            }
        } else if (f3 < f2 / 2.0f || !this.scaleOut) {
            return;
        }
        this.scaleIn = !z;
        this.scaleOut = z;
    }

    public void lambda$setProgress$3(DynamicAnimation dynamicAnimation, boolean z, float f, float f2) {
        this.scaleIn = false;
        this.scaleOut = false;
        if (!z) {
            dynamicAnimation.cancel();
        }
        if (dynamicAnimation == this.menuSpring) {
            this.menuSpring = null;
        }
    }

    public void lambda$setProgress$4(ValueAnimator valueAnimator) {
        this.menuProgress = ((Float) valueAnimator.getAnimatedValue()).floatValue();
        invalidate();
    }

    public static void lambda$static$1(SenderSelectView senderSelectView, float f) {
        senderSelectView.menuProgress = f;
        senderSelectView.invalidate();
    }

    private void updateColors() {
        this.backgroundPaint.setColor(Theme.getColor(Theme.key_chat_messagePanelVoiceBackground));
        this.menuPaint.setColor(Theme.getColor(Theme.key_chat_messagePanelVoicePressed));
        Drawable createSimpleSelectorRoundRectDrawable = Theme.createSimpleSelectorRoundRectDrawable(AndroidUtilities.dp(16.0f), 0, Theme.getColor(Theme.key_windowBackgroundWhite));
        this.selectorDrawable = createSimpleSelectorRoundRectDrawable;
        createSimpleSelectorRoundRectDrawable.setCallback(this);
    }

    @Override
    protected void drawableStateChanged() {
        super.drawableStateChanged();
        this.selectorDrawable.setState(getDrawableState());
    }

    public float getProgress() {
        return this.menuProgress;
    }

    @Override
    public void jumpDrawablesToCurrentState() {
        super.jumpDrawablesToCurrentState();
        this.selectorDrawable.jumpToCurrentState();
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
        canvas.save();
        float f = 1.0f;
        if (this.scaleOut) {
            f = 1.0f - this.menuProgress;
        } else if (this.scaleIn) {
            f = this.menuProgress;
        }
        canvas.scale(f, f, getWidth() / 2.0f, getHeight() / 2.0f);
        super.onDraw(canvas);
        this.avatarImage.draw(canvas);
        int i = (int) (this.menuProgress * 255.0f);
        this.backgroundPaint.setAlpha(i);
        canvas.drawCircle(getWidth() / 2.0f, getHeight() / 2.0f, Math.min(getWidth(), getHeight()) / 2.0f, this.backgroundPaint);
        canvas.save();
        this.menuPaint.setAlpha(i);
        float dp = AndroidUtilities.dp(9.0f) + this.menuPaint.getStrokeWidth();
        canvas.drawLine(dp, dp, getWidth() - dp, getHeight() - dp, this.menuPaint);
        canvas.drawLine(dp, getHeight() - dp, getWidth() - dp, dp, this.menuPaint);
        canvas.restore();
        this.selectorDrawable.setBounds(0, 0, getWidth(), getHeight());
        this.selectorDrawable.draw(canvas);
        canvas.restore();
    }

    @Override
    protected void onMeasure(int i, int i2) {
        super.onMeasure(View.MeasureSpec.makeMeasureSpec(getLayoutParams().width, 1073741824), View.MeasureSpec.makeMeasureSpec(getLayoutParams().height, 1073741824));
        this.avatarImage.setImageCoords(0.0f, 0.0f, getMeasuredWidth(), getMeasuredHeight());
    }

    public void setAvatar(TLObject tLObject) {
        setContentDescription(LocaleController.formatString("AccDescrSendAsPeer", R.string.AccDescrSendAsPeer, tLObject instanceof TLRPC.User ? UserObject.getFirstName((TLRPC.User) tLObject) : tLObject instanceof TLRPC.Chat ? ((TLRPC.Chat) tLObject).title : tLObject instanceof TLRPC.ChatInvite ? ((TLRPC.ChatInvite) tLObject).title : ""));
        this.avatarDrawable.setInfo(tLObject);
        this.avatarImage.setForUserOrChat(tLObject, this.avatarDrawable);
    }

    public void setProgress(float f) {
        setProgress(f, true);
    }

    public void setProgress(float f, boolean z) {
        setProgress(f, z, f != 0.0f);
    }

    public void setProgress(float f, boolean z, boolean z2) {
        if (!z) {
            this.menuProgress = f;
            invalidate();
            return;
        }
        SpringAnimation springAnimation = this.menuSpring;
        if (springAnimation != null) {
            springAnimation.cancel();
        }
        ValueAnimator valueAnimator = this.menuAnimator;
        if (valueAnimator != null) {
            valueAnimator.cancel();
        }
        this.scaleIn = false;
        this.scaleOut = false;
        float f2 = this.menuProgress;
        if (!z2) {
            ValueAnimator duration = ValueAnimator.ofFloat(f2, f).setDuration(200L);
            this.menuAnimator = duration;
            duration.setInterpolator(CubicBezierInterpolator.DEFAULT);
            this.menuAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public final void onAnimationUpdate(ValueAnimator valueAnimator2) {
                    SenderSelectView.this.lambda$setProgress$4(valueAnimator2);
                }
            });
            this.menuAnimator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animator) {
                    if (animator == SenderSelectView.this.menuAnimator) {
                        SenderSelectView.this.menuAnimator = null;
                    }
                }
            });
            this.menuAnimator.start();
            return;
        }
        final float f3 = f2 * 100.0f;
        SpringAnimation springAnimation2 = (SpringAnimation) new SpringAnimation(this, MENU_PROGRESS).setStartValue(f3);
        this.menuSpring = springAnimation2;
        final boolean z3 = f < this.menuProgress;
        final float f4 = f * 100.0f;
        this.scaleIn = z3;
        this.scaleOut = !z3;
        springAnimation2.setSpring(new SpringForce(f4).setFinalPosition(f4).setStiffness(450.0f).setDampingRatio(1.0f));
        this.menuSpring.addUpdateListener(new DynamicAnimation.OnAnimationUpdateListener() {
            @Override
            public final void onAnimationUpdate(DynamicAnimation dynamicAnimation, float f5, float f6) {
                SenderSelectView.this.lambda$setProgress$2(z3, f3, f4, dynamicAnimation, f5, f6);
            }
        });
        this.menuSpring.addEndListener(new DynamicAnimation.OnAnimationEndListener() {
            @Override
            public final void onAnimationEnd(DynamicAnimation dynamicAnimation, boolean z4, float f5, float f6) {
                SenderSelectView.this.lambda$setProgress$3(dynamicAnimation, z4, f5, f6);
            }
        });
        this.menuSpring.start();
    }

    @Override
    protected boolean verifyDrawable(Drawable drawable) {
        return super.verifyDrawable(drawable) || this.selectorDrawable == drawable;
    }
}
