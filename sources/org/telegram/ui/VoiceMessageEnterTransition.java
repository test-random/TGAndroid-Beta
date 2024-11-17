package org.telegram.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Shader;
import android.view.animation.LinearInterpolator;
import androidx.core.graphics.ColorUtils;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Cells.ChatMessageCell;
import org.telegram.ui.Components.ChatActivityEnterView;
import org.telegram.ui.Components.CubicBezierInterpolator;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.MessageEnterTransitionContainer;

public class VoiceMessageEnterTransition implements MessageEnterTransitionContainer.Transition {
    private final ValueAnimator animator;
    final Paint circlePaint = new Paint(1);
    MessageEnterTransitionContainer container;
    float fromRadius;
    private final Matrix gradientMatrix;
    private final Paint gradientPaint;
    private final LinearGradient gradientShader;
    float lastToCx;
    float lastToCy;
    private final RecyclerListView listView;
    private final int messageId;
    private final ChatMessageCell messageView;
    float progress;
    private final ChatActivityEnterView.RecordCircle recordCircle;
    private final Theme.ResourcesProvider resourcesProvider;

    public VoiceMessageEnterTransition(final ChatMessageCell chatMessageCell, ChatActivityEnterView chatActivityEnterView, RecyclerListView recyclerListView, final MessageEnterTransitionContainer messageEnterTransitionContainer, Theme.ResourcesProvider resourcesProvider) {
        this.resourcesProvider = resourcesProvider;
        this.messageView = chatMessageCell;
        this.container = messageEnterTransitionContainer;
        this.listView = recyclerListView;
        chatMessageCell.setEnterTransitionInProgress(true);
        ChatActivityEnterView.RecordCircle recordCircle = chatActivityEnterView.getRecordCircle();
        this.recordCircle = recordCircle;
        if (recordCircle != null) {
            this.fromRadius = recordCircle.drawingCircleRadius;
            recordCircle.voiceEnterTransitionInProgress = true;
            recordCircle.skipDraw = true;
        }
        this.gradientMatrix = new Matrix();
        Paint paint = new Paint(1);
        this.gradientPaint = paint;
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));
        LinearGradient linearGradient = new LinearGradient(0.0f, AndroidUtilities.dp(12.0f), 0.0f, 0.0f, 0, -16777216, Shader.TileMode.CLAMP);
        this.gradientShader = linearGradient;
        paint.setShader(linearGradient);
        this.messageId = chatMessageCell.getMessageObject().stableId;
        messageEnterTransitionContainer.addTransition(this);
        ValueAnimator ofFloat = ValueAnimator.ofFloat(0.0f, 1.0f);
        this.animator = ofFloat;
        ofFloat.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                VoiceMessageEnterTransition.this.lambda$new$0(messageEnterTransitionContainer, valueAnimator);
            }
        });
        ofFloat.setInterpolator(new LinearInterpolator());
        ofFloat.setDuration(220L);
        ofFloat.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animator) {
                chatMessageCell.setEnterTransitionInProgress(false);
                messageEnterTransitionContainer.removeTransition(VoiceMessageEnterTransition.this);
                if (VoiceMessageEnterTransition.this.recordCircle != null) {
                    VoiceMessageEnterTransition.this.recordCircle.skipDraw = false;
                }
            }
        });
        if (chatMessageCell.getSeekBarWaveform() != null) {
            chatMessageCell.getSeekBarWaveform().setSent();
        }
    }

    private int getThemedColor(int i) {
        return Theme.getColor(i, this.resourcesProvider);
    }

    public void lambda$new$0(MessageEnterTransitionContainer messageEnterTransitionContainer, ValueAnimator valueAnimator) {
        this.progress = ((Float) valueAnimator.getAnimatedValue()).floatValue();
        messageEnterTransitionContainer.invalidate();
    }

    public void lambda$onDraw$1(Canvas canvas, float f, float f2, float f3, float f4, float f5, float f6, float f7, float f8) {
        this.messageView.getRadialProgress().draw(canvas);
        canvas.translate(-f, -f2);
        float f9 = 1.0f / f3;
        canvas.scale(f9, f9, f4, f5);
        ChatActivityEnterView.RecordCircle recordCircle = this.recordCircle;
        if (recordCircle != null) {
            recordCircle.drawIcon(canvas, (int) f6, (int) f7, 1.0f - f8);
        }
        canvas.scale(f3, f3, f4, f5);
        canvas.translate(f, f2);
    }

    @Override
    public void onDraw(final Canvas canvas) {
        float centerY;
        float centerX;
        final float f = this.progress;
        float f2 = f > 0.6f ? 1.0f : f / 0.6f;
        ChatActivityEnterView.RecordCircle recordCircle = this.recordCircle;
        final float x = recordCircle == null ? 0.0f : (recordCircle.drawingCx + recordCircle.getX()) - this.container.getX();
        ChatActivityEnterView.RecordCircle recordCircle2 = this.recordCircle;
        final float y = recordCircle2 == null ? 0.0f : (recordCircle2.drawingCy + recordCircle2.getY()) - this.container.getY();
        if (this.messageView.getMessageObject().stableId != this.messageId) {
            centerX = this.lastToCx;
            centerY = this.lastToCy;
        } else {
            centerY = ((this.messageView.getRadialProgress().getProgressRect().centerY() + this.messageView.getY()) + this.listView.getY()) - this.container.getY();
            centerX = ((this.messageView.getRadialProgress().getProgressRect().centerX() + this.messageView.getX()) + this.listView.getX()) - this.container.getX();
        }
        this.lastToCx = centerX;
        this.lastToCy = centerY;
        float interpolation = CubicBezierInterpolator.DEFAULT.getInterpolation(f);
        float interpolation2 = CubicBezierInterpolator.EASE_OUT_QUINT.getInterpolation(f);
        final float f3 = ((1.0f - interpolation2) * x) + (centerX * interpolation2);
        float f4 = 1.0f - interpolation;
        final float f5 = (y * f4) + (centerY * interpolation);
        float height = this.messageView.getRadialProgress().getProgressRect().height() / 2.0f;
        float f6 = (this.fromRadius * f4) + (height * interpolation);
        this.listView.getY();
        this.container.getY();
        this.listView.getMeasuredHeight();
        if (this.container.getMeasuredHeight() > 0) {
            this.container.getMeasuredHeight();
        }
        int circleColorKey = this.messageView.getRadialProgress().getCircleColorKey();
        Paint paint = this.circlePaint;
        int i = Theme.key_chat_messagePanelVoiceBackground;
        int themedColor = getThemedColor(i);
        if (circleColorKey < 0) {
            circleColorKey = i;
        }
        paint.setColor(ColorUtils.blendARGB(themedColor, getThemedColor(circleColorKey), interpolation));
        ChatActivityEnterView.RecordCircle recordCircle3 = this.recordCircle;
        if (recordCircle3 != null) {
            recordCircle3.drawWaves(canvas, f3, f5, 1.0f - f2);
        }
        canvas.drawCircle(f3, f5, f6, this.circlePaint);
        canvas.save();
        final float f7 = f6 / height;
        canvas.scale(f7, f7, f3, f5);
        final float centerX2 = f3 - this.messageView.getRadialProgress().getProgressRect().centerX();
        final float centerY2 = f5 - this.messageView.getRadialProgress().getProgressRect().centerY();
        canvas.translate(centerX2, centerY2);
        this.messageView.getRadialProgress().setOverrideAlpha(interpolation);
        this.messageView.getRadialProgress().setDrawBackground(false);
        this.messageView.drawVoiceOnce(canvas, interpolation, new Runnable() {
            @Override
            public final void run() {
                VoiceMessageEnterTransition.this.lambda$onDraw$1(canvas, centerX2, centerY2, f7, f3, f5, x, y, f);
            }
        });
        this.messageView.getRadialProgress().setDrawBackground(true);
        this.messageView.getRadialProgress().setOverrideAlpha(1.0f);
        canvas.restore();
    }

    public void start() {
        this.animator.start();
    }
}
