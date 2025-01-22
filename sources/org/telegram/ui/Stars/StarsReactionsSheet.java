package org.telegram.ui.Stars;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.animation.Interpolator;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.google.zxing.common.detector.MathUtils;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Objects;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.DialogObject;
import org.telegram.messenger.ImageReceiver;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.MessagesStorage;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.R;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.UserObject;
import org.telegram.messenger.Utilities;
import org.telegram.messenger.browser.Browser;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.BottomSheet;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Cells.ChatActionCell;
import org.telegram.ui.Cells.ChatMessageCell;
import org.telegram.ui.ChatActivity;
import org.telegram.ui.Components.AnimatedFloat;
import org.telegram.ui.Components.AnimatedTextView;
import org.telegram.ui.Components.AvatarDrawable;
import org.telegram.ui.Components.ButtonBounce;
import org.telegram.ui.Components.CheckBox2;
import org.telegram.ui.Components.ColoredImageSpan;
import org.telegram.ui.Components.CubicBezierInterpolator;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.LinkSpanDrawable;
import org.telegram.ui.Components.Premium.GLIcon.GLIconRenderer;
import org.telegram.ui.Components.Premium.GLIcon.GLIconTextureView;
import org.telegram.ui.Components.Reactions.ReactionsLayoutInBubble;
import org.telegram.ui.Components.ScaleStateListAnimator;
import org.telegram.ui.Components.Text;
import org.telegram.ui.LaunchActivity;
import org.telegram.ui.ProfileActivity;
import org.telegram.ui.Stars.StarsController;
import org.telegram.ui.Stars.StarsIntroActivity;
import org.telegram.ui.Stars.StarsReactionsSheet;
import org.telegram.ui.Stories.recorder.ButtonWithCounterView;

public class StarsReactionsSheet extends BottomSheet {
    public boolean anonymous;
    private final StarsIntroActivity.StarsBalanceView balanceView;
    private final ButtonWithCounterView buttonView;
    private ChatActivity chatActivity;
    private final CheckBox2 checkBox;
    private final LinearLayout checkLayout;
    private final View checkSeparatorView;
    private final TextView checkTextView;
    private boolean checkedVisiblity;
    private final int currentAccount;
    private final GLIconTextureView icon3dView;
    private ValueAnimator iconAnimator;
    private final LinearLayout layout;
    private View messageCell;
    private int messageId;
    private final MessageObject messageObject;
    private final ArrayList reactors;
    private final Theme.ResourcesProvider resourcesProvider;
    private long selectedDialogId;
    private boolean sending;
    private final View separatorView;
    private final StarsSlider slider;
    private final ColoredImageSpan[] starRef;
    private final TextView statusView;
    private final TextView titleView;
    private final FrameLayout topLayout;
    private final TopSendersView topSendersView;

    public static class Particles {
        public final Bitmap b;
        private int bPaintColor;
        private long lastTime;
        public final ArrayList particles;
        public final int type;
        private int visibleCount;
        public final RectF bounds = new RectF();
        public final Paint bPaint = new Paint(3);
        public final Rect rect = new Rect();
        private float speed = 1.0f;
        private boolean firstDraw = true;

        public class Particle {
            public float a;
            public float la;
            public long lifetime;
            public float s;
            public long start;
            public float vx;
            public float vy;
            public float x;
            public float y;

            public Particle() {
            }

            public void draw(Canvas canvas, int i, float f) {
                Particles.this.bPaint.setAlpha((int) (255.0f * f));
                Particles.this.rect.set((int) (this.x - ((((r8.b.getWidth() / 2.0f) * this.a) * this.s) * f)), (int) (this.y - ((((Particles.this.b.getHeight() / 2.0f) * this.a) * this.s) * f)), (int) (this.x + ((Particles.this.b.getWidth() / 2.0f) * this.a * this.s * f)), (int) (this.y + ((Particles.this.b.getHeight() / 2.0f) * this.a * this.s * f)));
                Particles particles = Particles.this;
                canvas.drawBitmap(particles.b, (Rect) null, particles.rect, particles.bPaint);
            }
        }

        public Particles(int i, int i2) {
            this.type = i;
            this.visibleCount = i2;
            this.particles = new ArrayList(i2);
            for (int i3 = 0; i3 < i2; i3++) {
                this.particles.add(new Particle());
            }
            int dp = AndroidUtilities.dp(10.0f);
            Bitmap createBitmap = Bitmap.createBitmap(dp, dp, Bitmap.Config.ARGB_8888);
            this.b = createBitmap;
            Path path = new Path();
            float f = dp >> 1;
            int i4 = (int) (0.85f * f);
            path.moveTo(0.0f, f);
            float f2 = i4;
            path.lineTo(f2, f2);
            path.lineTo(f, 0.0f);
            float f3 = dp - i4;
            path.lineTo(f3, f2);
            float f4 = dp;
            path.lineTo(f4, f);
            path.lineTo(f3, f3);
            path.lineTo(f, f4);
            path.lineTo(f2, f3);
            path.lineTo(0.0f, f);
            path.close();
            Canvas canvas = new Canvas(createBitmap);
            Paint paint = new Paint();
            paint.setColor(Theme.multAlpha(-1, 0.75f));
            canvas.drawPath(path, paint);
        }

        public void draw(Canvas canvas, int i) {
            if (this.bPaintColor != i) {
                Paint paint = this.bPaint;
                this.bPaintColor = i;
                paint.setColorFilter(new PorterDuffColorFilter(i, PorterDuff.Mode.SRC_IN));
            }
            for (int i2 = 0; i2 < Math.min(this.visibleCount, this.particles.size()); i2++) {
                Particle particle = (Particle) this.particles.get(i2);
                particle.draw(canvas, i, particle.la);
            }
            this.firstDraw = false;
        }

        public void gen(Particle particle, long j, boolean z) {
            particle.start = j;
            particle.lifetime = AndroidUtilities.lerp(500, 2500, Utilities.fastRandom.nextFloat());
            if (z) {
                particle.start -= ((float) r4) * Utilities.clamp01(Utilities.fastRandom.nextFloat());
            }
            RectF rectF = this.bounds;
            particle.x = AndroidUtilities.lerp(rectF.left, rectF.right, Utilities.fastRandom.nextFloat());
            RectF rectF2 = this.bounds;
            particle.y = AndroidUtilities.lerp(rectF2.top, rectF2.bottom, Utilities.fastRandom.nextFloat());
            if (this.type == 0) {
                particle.vx = AndroidUtilities.dp(AndroidUtilities.lerp(-7.0f, -18.0f, Utilities.fastRandom.nextFloat()));
                particle.vy = AndroidUtilities.dp(AndroidUtilities.lerp(-2.0f, 2.0f, Utilities.fastRandom.nextFloat()));
            } else {
                particle.vx = this.bounds.centerX() - particle.x;
                particle.vy = this.bounds.centerY() - particle.y;
                float dp = AndroidUtilities.dp(AndroidUtilities.lerp(1.0f, 4.0f, Utilities.fastRandom.nextFloat()));
                float f = particle.vx;
                float f2 = particle.vy;
                float sqrt = dp / ((float) Math.sqrt((f * f) + (f2 * f2)));
                particle.vx *= sqrt;
                particle.vy *= sqrt;
            }
            particle.a = AndroidUtilities.lerp(0.4f, 1.0f, Utilities.fastRandom.nextFloat());
            particle.s = AndroidUtilities.lerp(0.8f, 1.2f, Utilities.fastRandom.nextFloat()) * 0.7f;
        }

        public void process() {
            long currentTimeMillis = System.currentTimeMillis();
            float min = (((float) Math.min(this.lastTime - currentTimeMillis, 16L)) / 1000.0f) * this.speed;
            for (int i = 0; i < Math.min(this.visibleCount, this.particles.size()); i++) {
                Particle particle = (Particle) this.particles.get(i);
                long j = particle.lifetime;
                float f = j <= 0 ? 2.0f : ((float) (currentTimeMillis - particle.start)) / ((float) j);
                if (f > 1.0f) {
                    gen(particle, currentTimeMillis, this.firstDraw);
                    f = 0.0f;
                }
                particle.x += particle.vx * min;
                particle.y += particle.vy * min;
                float f2 = 4.0f * f;
                particle.la = f2 - (f * f2);
            }
            this.lastTime = currentTimeMillis;
        }

        public void removeParticlesOutside() {
            if (this.type == 2) {
                long currentTimeMillis = System.currentTimeMillis();
                for (int i = 0; i < this.particles.size(); i++) {
                    Particle particle = (Particle) this.particles.get(i);
                    if (!this.bounds.contains((int) particle.x, (int) particle.y)) {
                        gen(particle, currentTimeMillis, this.firstDraw);
                    }
                }
            }
        }

        public void setBounds(int i, int i2, int i3, int i4) {
            this.bounds.set(i, i2, i3, i4);
            removeParticlesOutside();
        }

        public void setBounds(Rect rect) {
            this.bounds.set(rect);
            removeParticlesOutside();
        }

        public void setBounds(RectF rectF) {
            this.bounds.set(rectF);
            removeParticlesOutside();
        }

        public void setSpeed(float f) {
            this.speed = f;
        }

        public void setVisible(float f) {
            this.visibleCount = (int) (this.particles.size() * f);
        }
    }

    public static class SenderData {
        public boolean anonymous;
        public long did;
        public long stars;

        public static SenderData of(boolean z, long j, long j2) {
            SenderData senderData = new SenderData();
            senderData.anonymous = z;
            senderData.did = j;
            senderData.stars = j2;
            return senderData;
        }
    }

    public static class StarsSlider extends View {
        public float aprogress;
        private final RectF arc;
        private final Drawable counterImage;
        private final AnimatedTextView.AnimatedTextDrawable counterText;
        private long currentTop;
        public boolean drawCounterImage;
        private final LinearGradient gradient;
        private final Matrix gradientMatrix;
        private float lastX;
        private float lastY;
        private final AnimatedFloat overTop;
        private final AnimatedFloat overTopText;
        private int pointerId;
        private long pressTime;
        public float progress;
        private ValueAnimator progressAnimator;
        private final Paint sliderCirclePaint;
        private final RectF sliderCircleRect;
        private final Paint sliderInnerPaint;
        private final Path sliderInnerPath;
        private final RectF sliderInnerRect;
        private final Paint sliderPaint;
        private final Particles sliderParticles;
        private final Path sliderPath;
        private final RectF sliderRect;
        private final ColoredImageSpan[] starRef;
        public int steps;
        public int[] stops;
        private final Paint textBackgroundPaint;
        private final Particles textParticles;
        private final Path textPath;
        private final RectF textRect;
        private final Paint topPaint;
        private final Text topText;
        private boolean tracking;

        public StarsSlider(Context context) {
            super(context);
            this.sliderInnerPaint = new Paint(1);
            this.sliderPaint = new Paint(1);
            this.sliderCirclePaint = new Paint(1);
            this.textBackgroundPaint = new Paint(1);
            this.sliderParticles = new Particles(0, 300);
            this.textParticles = new Particles(2, 30);
            this.gradient = new LinearGradient(0.0f, 0.0f, 255.0f, 0.0f, new int[]{-1135603, -404714}, new float[]{0.0f, 1.0f}, Shader.TileMode.CLAMP);
            this.gradientMatrix = new Matrix();
            this.drawCounterImage = true;
            AnimatedTextView.AnimatedTextDrawable animatedTextDrawable = new AnimatedTextView.AnimatedTextDrawable(false, true, true);
            this.counterText = animatedTextDrawable;
            this.starRef = new ColoredImageSpan[1];
            Paint paint = new Paint(1);
            this.topPaint = paint;
            this.topText = new Text(LocaleController.getString(R.string.StarsReactionTop), 14.0f, AndroidUtilities.getTypeface("fonts/rcondensedbold.ttf"));
            CubicBezierInterpolator cubicBezierInterpolator = CubicBezierInterpolator.EASE_OUT_QUINT;
            this.overTop = new AnimatedFloat(this, 0L, 320L, cubicBezierInterpolator);
            this.overTopText = new AnimatedFloat(this, 0L, 320L, cubicBezierInterpolator);
            this.currentTop = -1L;
            this.sliderInnerRect = new RectF();
            this.sliderRect = new RectF();
            this.sliderCircleRect = new RectF();
            this.arc = new RectF();
            this.sliderInnerPath = new Path();
            this.sliderPath = new Path();
            this.textRect = new RectF();
            this.textPath = new Path();
            this.progress = 0.0f;
            Drawable mutate = context.getResources().getDrawable(R.drawable.msg_premium_liststar).mutate();
            this.counterImage = mutate;
            mutate.setColorFilter(new PorterDuffColorFilter(-1, PorterDuff.Mode.SRC_IN));
            animatedTextDrawable.setTextColor(-1);
            animatedTextDrawable.setTypeface(AndroidUtilities.getTypeface("fonts/num.otf"));
            animatedTextDrawable.setTextSize(AndroidUtilities.dp(21.0f));
            animatedTextDrawable.setCallback(this);
            animatedTextDrawable.setOverrideFullWidth(AndroidUtilities.displaySize.x);
            animatedTextDrawable.setGravity(17);
            paint.setColor(Theme.getColor(Theme.key_dialogBackground));
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(AndroidUtilities.dp(1.0f));
        }

        private void animateProgressTo(final float f) {
            ValueAnimator valueAnimator = this.progressAnimator;
            if (valueAnimator != null) {
                valueAnimator.cancel();
            }
            ValueAnimator ofFloat = ValueAnimator.ofFloat(this.progress, f);
            this.progressAnimator = ofFloat;
            ofFloat.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public final void onAnimationUpdate(ValueAnimator valueAnimator2) {
                    StarsReactionsSheet.StarsSlider.this.lambda$animateProgressTo$0(valueAnimator2);
                }
            });
            this.progressAnimator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animator) {
                    int value = StarsSlider.this.getValue();
                    StarsSlider starsSlider = StarsSlider.this;
                    starsSlider.progress = f;
                    if (starsSlider.getValue() != value) {
                        StarsSlider starsSlider2 = StarsSlider.this;
                        starsSlider2.onValueChanged(starsSlider2.getValue());
                    }
                    StarsSlider.this.invalidate();
                }
            });
            this.progressAnimator.setDuration(320L);
            this.progressAnimator.setInterpolator(CubicBezierInterpolator.EASE_OUT_QUINT);
            this.progressAnimator.start();
            if (getValue(f) != getValue()) {
                onValueChanged(getValue(f));
            }
            this.counterText.cancelAnimation();
            this.counterText.setText(StarsIntroActivity.replaceStars(LocaleController.formatNumber(getValue(f), ','), this.starRef), true);
        }

        public void lambda$animateProgressTo$0(ValueAnimator valueAnimator) {
            this.progress = ((Float) valueAnimator.getAnimatedValue()).floatValue();
            invalidate();
        }

        @Override
        protected void dispatchDraw(Canvas canvas) {
            float f;
            super.dispatchDraw(canvas);
            this.gradientMatrix.reset();
            this.gradientMatrix.postTranslate(this.sliderInnerRect.left, 0.0f);
            this.gradientMatrix.postScale(this.sliderInnerRect.width() / 255.0f, 1.0f);
            this.gradient.setLocalMatrix(this.gradientMatrix);
            this.sliderPaint.setShader(this.gradient);
            this.sliderInnerPath.rewind();
            Path path = this.sliderInnerPath;
            RectF rectF = this.sliderInnerRect;
            float dp = AndroidUtilities.dp(12.0f);
            float dp2 = AndroidUtilities.dp(12.0f);
            Path.Direction direction = Path.Direction.CW;
            path.addRoundRect(rectF, dp, dp2, direction);
            canvas.drawPath(this.sliderInnerPath, this.sliderInnerPaint);
            this.sliderRect.set(this.sliderInnerRect);
            float progress = getProgress(getValue());
            RectF rectF2 = this.sliderRect;
            rectF2.right = AndroidUtilities.lerp(rectF2.left + AndroidUtilities.dp(24.0f), this.sliderRect.right, progress);
            this.sliderPath.rewind();
            this.sliderPath.addRoundRect(this.sliderRect, AndroidUtilities.dp(12.0f), AndroidUtilities.dp(12.0f), direction);
            this.sliderParticles.setBounds(this.sliderInnerRect);
            this.sliderParticles.setSpeed((this.progress * 15.0f) + 1.0f);
            this.sliderParticles.setVisible((this.progress * 0.85f) + 0.15f);
            this.sliderParticles.process();
            canvas.save();
            canvas.clipPath(this.sliderInnerPath);
            this.sliderParticles.draw(canvas, -673522);
            long j = this.currentTop;
            if (j != -1 && getProgress((int) j) < 1.0f && getProgress((int) this.currentTop) > 0.0f) {
                float dp3 = this.sliderInnerRect.left + AndroidUtilities.dp(12.0f) + ((this.sliderInnerRect.width() - AndroidUtilities.dp(24.0f)) * Utilities.clamp01(getProgress((int) this.currentTop)));
                float f2 = this.overTop.set(Math.abs((this.sliderRect.right - ((float) AndroidUtilities.dp(10.0f))) - dp3) < ((float) AndroidUtilities.dp(14.0f)));
                float lerp = AndroidUtilities.lerp(AndroidUtilities.dp(9.0f), AndroidUtilities.dp(16.0f), this.overTopText.set(Math.abs((this.sliderRect.right - ((float) AndroidUtilities.dp(10.0f))) - dp3) < ((float) AndroidUtilities.dp(12.0f))));
                float currentWidth = (this.topText.getCurrentWidth() + dp3) + ((float) (AndroidUtilities.dp(16.0f) * 2)) > this.sliderInnerRect.right ? (dp3 - lerp) - this.topText.getCurrentWidth() : lerp + dp3;
                this.topPaint.setStrokeWidth(AndroidUtilities.dp(1.0f));
                this.topPaint.setColor(Theme.multAlpha(-673522, 0.6f));
                RectF rectF3 = this.sliderInnerRect;
                float lerp2 = AndroidUtilities.lerp(rectF3.top, rectF3.centerY(), f2);
                RectF rectF4 = this.sliderInnerRect;
                canvas.drawLine(dp3, lerp2, dp3, AndroidUtilities.lerp(rectF4.bottom, rectF4.centerY(), f2), this.topPaint);
                this.topText.draw(canvas, currentWidth, this.sliderInnerRect.centerY(), -673522, 0.6f);
            }
            canvas.drawPath(this.sliderPath, this.sliderPaint);
            canvas.clipPath(this.sliderPath);
            this.sliderParticles.draw(canvas, -1);
            long j2 = this.currentTop;
            if (j2 != -1 && getProgress((int) j2) < 1.0f && getProgress((int) this.currentTop) > 0.0f) {
                float dp4 = this.sliderInnerRect.left + AndroidUtilities.dp(12.0f) + ((this.sliderInnerRect.width() - AndroidUtilities.dp(24.0f)) * Utilities.clamp01(getProgress((int) this.currentTop)));
                float f3 = this.overTop.set(Math.abs((this.sliderRect.right - ((float) AndroidUtilities.dp(10.0f))) - dp4) < ((float) AndroidUtilities.dp(14.0f)));
                float lerp3 = AndroidUtilities.lerp(AndroidUtilities.dp(9.0f), AndroidUtilities.dp(16.0f), this.overTopText.set(Math.abs((this.sliderRect.right - ((float) AndroidUtilities.dp(10.0f))) - dp4) < ((float) AndroidUtilities.dp(12.0f))));
                float currentWidth2 = (this.topText.getCurrentWidth() + dp4) + ((float) (AndroidUtilities.dp(16.0f) * 2)) > this.sliderInnerRect.right ? (dp4 - lerp3) - this.topText.getCurrentWidth() : lerp3 + dp4;
                this.topPaint.setStrokeWidth(AndroidUtilities.dp(1.0f));
                this.topPaint.setColor(Theme.multAlpha(Theme.getColor(Theme.key_dialogBackground), 0.4f));
                RectF rectF5 = this.sliderInnerRect;
                float lerp4 = AndroidUtilities.lerp(rectF5.top, rectF5.centerY(), f3);
                RectF rectF6 = this.sliderInnerRect;
                canvas.drawLine(dp4, lerp4, dp4, AndroidUtilities.lerp(rectF6.bottom, rectF6.centerY(), f3), this.topPaint);
                this.topText.draw(canvas, currentWidth2, this.sliderInnerRect.centerY(), -1, 0.75f);
            }
            canvas.restore();
            invalidate();
            this.sliderCircleRect.set((this.sliderRect.right - AndroidUtilities.dp(16.0f)) - AndroidUtilities.dp(4.0f), this.sliderRect.centerY() - (AndroidUtilities.dp(16.0f) / 2.0f), this.sliderRect.right - AndroidUtilities.dp(4.0f), this.sliderRect.centerY() + (AndroidUtilities.dp(16.0f) / 2.0f));
            canvas.drawRoundRect(this.sliderCircleRect, AndroidUtilities.dp(12.0f), AndroidUtilities.dp(12.0f), this.sliderCirclePaint);
            float dp5 = AndroidUtilities.dp(9.0f) / this.sliderInnerRect.width();
            RectF rectF7 = this.sliderCircleRect;
            float lerp5 = AndroidUtilities.lerp(AndroidUtilities.lerp(rectF7.left, rectF7.right, progress), AndroidUtilities.lerp(this.sliderCircleRect.left + AndroidUtilities.dp(9.0f), this.sliderCircleRect.right - AndroidUtilities.dp(9.0f), progress), Math.min(Utilities.clamp01(progress / dp5), Utilities.clamp01((1.0f - progress) / dp5)));
            float currentWidth3 = this.counterText.getCurrentWidth() + AndroidUtilities.dp(50.0f);
            float dp6 = AndroidUtilities.dp(44.0f);
            float clamp = Utilities.clamp(lerp5 - (currentWidth3 / 2.0f), (this.sliderInnerRect.right - currentWidth3) - AndroidUtilities.dp(4.0f), this.sliderInnerRect.left + AndroidUtilities.dp(4.0f));
            this.textRect.set(clamp, (this.sliderInnerRect.top - AndroidUtilities.dp(21.0f)) - dp6, currentWidth3 + clamp, this.sliderInnerRect.top - AndroidUtilities.dp(21.0f));
            float height = this.textRect.height();
            float f4 = height / 2.0f;
            RectF rectF8 = this.textRect;
            float clamp2 = Utilities.clamp(lerp5, rectF8.right, rectF8.left);
            RectF rectF9 = this.textRect;
            float clamp3 = Utilities.clamp(clamp2 - AndroidUtilities.dp(9.0f), rectF9.right, rectF9.left);
            RectF rectF10 = this.textRect;
            float clamp4 = Utilities.clamp(AndroidUtilities.dp(9.0f) + clamp2, rectF10.right, rectF10.left);
            float clamp5 = Utilities.clamp(this.progress - this.aprogress, 1.0f, -1.0f) * 60.0f;
            float dp7 = this.textRect.bottom + AndroidUtilities.dp(8.0f);
            this.textPath.rewind();
            RectF rectF11 = this.arc;
            RectF rectF12 = this.textRect;
            float f5 = rectF12.left;
            float f6 = rectF12.top;
            rectF11.set(f5, f6, f5 + height, f6 + height);
            this.textPath.arcTo(this.arc, -180.0f, 90.0f);
            RectF rectF13 = this.arc;
            RectF rectF14 = this.textRect;
            float f7 = rectF14.right;
            float f8 = rectF14.top;
            rectF13.set(f7 - height, f8, f7, f8 + height);
            this.textPath.arcTo(this.arc, -90.0f, 90.0f);
            RectF rectF15 = this.arc;
            RectF rectF16 = this.textRect;
            float f9 = rectF16.right;
            float f10 = rectF16.bottom;
            rectF15.set(f9 - height, f10 - height, f9, f10);
            this.textPath.arcTo(this.arc, 0.0f, (float) Utilities.clamp(((Math.acos(Utilities.clamp01((clamp4 - this.arc.centerX()) / f4)) * 0.8500000238418579d) / 3.141592653589793d) * 180.0d, 90.0d, 0.0d));
            RectF rectF17 = this.textRect;
            float f11 = 0.7f * height;
            if (clamp3 < rectF17.right - f11) {
                this.textPath.lineTo(clamp4, rectF17.bottom);
                f = 8.0f;
                this.textPath.lineTo(clamp2 + 2.0f, this.textRect.bottom + AndroidUtilities.dp(8.0f));
            } else {
                f = 8.0f;
            }
            this.textPath.lineTo(clamp2, this.textRect.bottom + AndroidUtilities.dp(f) + 1.0f);
            RectF rectF18 = this.textRect;
            if (clamp4 > rectF18.left + f11) {
                this.textPath.lineTo(clamp2 - 2.0f, rectF18.bottom + AndroidUtilities.dp(8.0f));
                this.textPath.lineTo(clamp3, this.textRect.bottom);
            }
            RectF rectF19 = this.arc;
            RectF rectF20 = this.textRect;
            float f12 = rectF20.left;
            float f13 = rectF20.bottom;
            rectF19.set(f12, f13 - height, height + f12, f13);
            float clamp6 = ((float) Utilities.clamp(((Math.acos(Utilities.clamp01((clamp3 - this.arc.left) / f4)) * 0.8500000238418579d) / 3.141592653589793d) * 180.0d, 90.0d, 0.0d)) + 90.0f;
            this.textPath.arcTo(this.arc, clamp6, 180.0f - clamp6);
            Path path2 = this.textPath;
            RectF rectF21 = this.textRect;
            path2.lineTo(rectF21.left, rectF21.bottom);
            this.textPath.close();
            RectF rectF22 = AndroidUtilities.rectTmp;
            rectF22.set(this.textRect);
            rectF22.inset(-AndroidUtilities.dp(12.0f), -AndroidUtilities.dp(12.0f));
            this.textParticles.setBounds(rectF22);
            this.textParticles.setSpeed((this.progress * 15.0f) + 1.0f);
            this.textParticles.process();
            canvas.save();
            this.textParticles.draw(canvas, -673522);
            canvas.restore();
            canvas.save();
            canvas.rotate(clamp5, clamp2, dp7);
            if (Math.abs(this.progress - this.aprogress) > 0.001f) {
                this.aprogress = AndroidUtilities.lerp(this.aprogress, this.progress, 0.1f);
                invalidate();
            }
            this.textBackgroundPaint.setShader(this.gradient);
            canvas.drawPath(this.textPath, this.textBackgroundPaint);
            canvas.save();
            canvas.clipPath(this.textPath);
            canvas.rotate(-clamp5, clamp2, dp7);
            this.textParticles.draw(canvas, -1);
            canvas.restore();
            this.counterImage.setBounds((int) (this.textRect.left + AndroidUtilities.dp(13.0f)), (int) (this.textRect.centerY() - AndroidUtilities.dp(10.0f)), (int) (this.textRect.left + AndroidUtilities.dp(33.0f)), (int) (this.textRect.centerY() + AndroidUtilities.dp(10.0f)));
            if (this.drawCounterImage) {
                this.counterImage.draw(canvas);
            }
            AnimatedTextView.AnimatedTextDrawable animatedTextDrawable = this.counterText;
            float dp8 = this.textRect.left + AndroidUtilities.dp(24.0f);
            RectF rectF23 = this.textRect;
            animatedTextDrawable.setBounds(dp8, rectF23.top, rectF23.right, rectF23.bottom);
            this.counterText.draw(canvas);
            canvas.restore();
        }

        @Override
        public boolean dispatchTouchEvent(MotionEvent motionEvent) {
            if (motionEvent.getAction() != 0) {
                if (motionEvent.getAction() == 2 && motionEvent.getPointerId(0) == this.pointerId) {
                    float x = motionEvent.getX() - this.lastX;
                    float y = motionEvent.getY() - this.lastY;
                    if (!this.tracking && Math.abs(x) > Math.abs(y * 1.5f) && Math.abs(x) > AndroidUtilities.touchSlop) {
                        getParent().requestDisallowInterceptTouchEvent(true);
                        this.tracking = true;
                        ValueAnimator valueAnimator = this.progressAnimator;
                        if (valueAnimator != null) {
                            valueAnimator.cancel();
                        }
                    }
                    if (this.tracking) {
                        int value = getValue();
                        this.progress = Utilities.clamp01(this.progress + (x / (getWidth() * 1.0f)));
                        if (getValue() != value) {
                            onValueChanged(getValue());
                            updateText(true);
                        }
                        this.lastX = motionEvent.getX();
                    }
                } else if (motionEvent.getAction() == 1 || motionEvent.getAction() == 3) {
                    if (!this.tracking && motionEvent.getPointerId(0) == this.pointerId && MathUtils.distance(this.lastX, this.lastY, motionEvent.getX(), motionEvent.getY()) < AndroidUtilities.touchSlop && ((float) (System.currentTimeMillis() - this.pressTime)) <= ViewConfiguration.getTapTimeout() * 1.5f) {
                        float x2 = motionEvent.getX();
                        RectF rectF = this.sliderInnerRect;
                        float clamp01 = Utilities.clamp01((x2 - rectF.left) / rectF.width());
                        long j = this.currentTop;
                        if (j > 0 && Math.abs(getProgress((int) j) - clamp01) < 0.035f) {
                            clamp01 = Utilities.clamp01(getProgress((int) this.currentTop));
                        }
                        animateProgressTo(clamp01);
                    }
                }
                return true;
            }
            this.lastX = motionEvent.getX();
            this.lastY = motionEvent.getY();
            this.pointerId = motionEvent.getPointerId(0);
            this.pressTime = System.currentTimeMillis();
            this.tracking = false;
            return true;
        }

        public float getProgress() {
            return this.progress;
        }

        public float getProgress(int i) {
            int i2 = 1;
            while (true) {
                int[] iArr = this.stops;
                if (i2 >= iArr.length) {
                    return 1.0f;
                }
                if (i <= iArr[i2]) {
                    int i3 = i2 - 1;
                    int i4 = iArr[i3];
                    return (i3 + ((i - i4) / (r3 - i4))) / (iArr.length - 1);
                }
                i2++;
            }
        }

        public int getValue() {
            return getValue(this.progress);
        }

        public int getValue(float f) {
            if (f <= 0.0f) {
                return this.stops[0];
            }
            if (f >= 1.0f) {
                return this.stops[r5.length - 1];
            }
            float length = f * (r0.length - 1);
            int i = (int) length;
            float f2 = length - i;
            return Math.round(this.stops[i] + (f2 * (r0[i + 1] - r2)));
        }

        @Override
        protected void onMeasure(int i, int i2) {
            setMeasuredDimension(View.MeasureSpec.getSize(i), AndroidUtilities.dp(220.0f));
            int measuredWidth = getMeasuredWidth();
            getMeasuredHeight();
            this.sliderInnerRect.set(AndroidUtilities.dp(14.0f), AndroidUtilities.dp(135.0f), measuredWidth - r6, r0 + AndroidUtilities.dp(24.0f));
            this.sliderInnerPaint.setColor(653241613);
            this.sliderPaint.setColor(-1069811);
            this.sliderCirclePaint.setColor(-1);
        }

        protected abstract void onValueChanged(int i);

        public void setStarsTop(long j) {
            this.currentTop = j;
            invalidate();
        }

        public void setSteps(int i, int... iArr) {
            this.steps = i;
            this.stops = iArr;
        }

        public void setValue(int i) {
            setValue(i, false);
        }

        public void setValue(int i, boolean z) {
            float progress = getProgress(i);
            this.progress = progress;
            if (!z) {
                this.aprogress = progress;
            }
            updateText(true);
        }

        public void updateText(boolean z) {
            this.counterText.cancelAnimation();
            this.counterText.setText(StarsIntroActivity.replaceStars(LocaleController.formatNumber(getValue(), ','), this.starRef), z);
        }

        @Override
        protected boolean verifyDrawable(Drawable drawable) {
            return drawable == this.counterText || super.verifyDrawable(drawable);
        }
    }

    public class TopSendersView extends View {
        public final AnimatedFloat animatedCount;
        public final Paint backgroundPaint;
        private Utilities.Callback clickListener;
        public float count;
        public final ArrayList oldSenders;
        private Sender pressedSender;
        public final ArrayList senders;
        public final Paint starsBackgroundPaint;

        public class Sender {
            public final AnimatedFloat animatedAnonymous;
            public final AnimatedFloat animatedPosition;
            public final AnimatedFloat animatedScale;
            public boolean anonymous;
            public final AvatarDrawable anonymousAvatarDrawable;
            public final AvatarDrawable avatarDrawable;
            public final ButtonBounce bounce;
            public final RectF clickBounds = new RectF();
            public final long did;
            public final ImageReceiver imageReceiver;
            public int index;
            public Text starsText;
            public Text text;

            public Sender(long j) {
                String str;
                TLRPC.Chat chat;
                CubicBezierInterpolator cubicBezierInterpolator = CubicBezierInterpolator.EASE_OUT_QUINT;
                this.animatedPosition = new AnimatedFloat(TopSendersView.this, 0L, 600L, cubicBezierInterpolator);
                this.animatedScale = new AnimatedFloat(TopSendersView.this, 0L, 200L, cubicBezierInterpolator);
                this.animatedAnonymous = new AnimatedFloat(TopSendersView.this, 0L, 350L, cubicBezierInterpolator);
                ImageReceiver imageReceiver = new ImageReceiver(TopSendersView.this);
                this.imageReceiver = imageReceiver;
                AvatarDrawable avatarDrawable = new AvatarDrawable();
                this.avatarDrawable = avatarDrawable;
                AvatarDrawable avatarDrawable2 = new AvatarDrawable();
                this.anonymousAvatarDrawable = avatarDrawable2;
                this.bounce = new ButtonBounce(TopSendersView.this);
                this.did = j;
                MessagesController messagesController = MessagesController.getInstance(StarsReactionsSheet.this.currentAccount);
                if (j >= 0) {
                    TLRPC.User user = messagesController.getUser(Long.valueOf(j));
                    str = UserObject.getForcedFirstName(user);
                    avatarDrawable.setInfo(user);
                    chat = user;
                } else {
                    TLRPC.Chat chat2 = messagesController.getChat(Long.valueOf(-j));
                    str = chat2 == null ? "" : chat2.title;
                    avatarDrawable.setInfo(chat2);
                    chat = chat2;
                }
                imageReceiver.setForUserOrChat(chat, avatarDrawable);
                imageReceiver.setRoundRadius(AndroidUtilities.dp(56.0f));
                imageReceiver.onAttachedToWindow();
                avatarDrawable2.setAvatarType(21);
                avatarDrawable2.setColor(Theme.getColor(Theme.key_avatar_backgroundGray));
                this.text = new Text(str, 12.0f);
            }

            public void draw(Canvas canvas) {
                float f = this.animatedPosition.set(this.index);
                AnimatedFloat animatedFloat = this.animatedScale;
                int i = this.index;
                float f2 = animatedFloat.set(i >= 0 && i < TopSendersView.this.senders.size());
                canvas.save();
                float dp = AndroidUtilities.dp(40.0f) + (((TopSendersView.this.getWidth() - AndroidUtilities.dp(80.0f)) / Math.max(1.0f, TopSendersView.this.count)) * (TopSendersView.this.count - (f + 0.5f)));
                float dp2 = AndroidUtilities.dp(40.0f);
                float width = ((TopSendersView.this.getWidth() - AndroidUtilities.dp(80.0f)) / Math.max(1.0f, TopSendersView.this.count)) / 2.0f;
                this.clickBounds.set(dp - width, dp2 - AndroidUtilities.dp(50.0f), width + dp, AndroidUtilities.dp(50.0f) + dp2);
                float f3 = (0.3f * f2) + 0.7f;
                canvas.scale(f3, f3, dp, dp2);
                float scale = this.bounce.getScale(0.04f);
                canvas.scale(scale, scale, dp, dp2);
                if (f2 > 0.0f) {
                    float f4 = this.animatedAnonymous.set(this.anonymous);
                    if (f4 < 1.0f) {
                        this.imageReceiver.setImageCoords(dp - (AndroidUtilities.dp(56.0f) / 2.0f), dp2 - (AndroidUtilities.dp(56.0f) / 2.0f), AndroidUtilities.dp(56.0f), AndroidUtilities.dp(56.0f));
                        this.imageReceiver.setAlpha(f2);
                        this.imageReceiver.draw(canvas);
                        this.imageReceiver.setAlpha(1.0f);
                    }
                    if (f4 > 0.0f) {
                        int i2 = (int) dp;
                        int i3 = (int) dp2;
                        this.anonymousAvatarDrawable.setBounds(i2 - (AndroidUtilities.dp(56.0f) / 2), i3 - (AndroidUtilities.dp(56.0f) / 2), i2 + (AndroidUtilities.dp(56.0f) / 2), i3 + (AndroidUtilities.dp(56.0f) / 2));
                        this.anonymousAvatarDrawable.setAlpha((int) (f2 * 255.0f * f4));
                        this.anonymousAvatarDrawable.draw(canvas);
                        this.anonymousAvatarDrawable.setAlpha(255);
                    }
                }
                RectF rectF = AndroidUtilities.rectTmp;
                rectF.set((dp - (this.starsText.getCurrentWidth() / 2.0f)) - AndroidUtilities.dp(5.66f), (AndroidUtilities.dp(23.0f) + dp2) - (AndroidUtilities.dp(16.0f) / 2.0f), (this.starsText.getCurrentWidth() / 2.0f) + dp + AndroidUtilities.dp(5.66f), AndroidUtilities.dp(23.0f) + dp2 + (AndroidUtilities.dp(16.0f) / 2.0f));
                canvas.drawRoundRect(rectF, rectF.height() / 2.0f, rectF.height() / 2.0f, TopSendersView.this.backgroundPaint);
                TopSendersView.this.starsBackgroundPaint.setAlpha((int) (255.0f * f2));
                canvas.drawRoundRect(rectF, rectF.height() / 2.0f, rectF.height() / 2.0f, TopSendersView.this.starsBackgroundPaint);
                Text text = this.starsText;
                text.draw(canvas, dp - (text.getCurrentWidth() / 2.0f), dp2 + AndroidUtilities.dp(23.0f), -1, f2);
                Text text2 = this.text;
                text2.draw(canvas, dp - (text2.getCurrentWidth() / 2.0f), dp2 + AndroidUtilities.dp(42.0f), Theme.getColor(Theme.key_windowBackgroundWhiteBlackText, StarsReactionsSheet.this.resourcesProvider), f2);
                canvas.restore();
            }

            public void setAnonymous(boolean z) {
                String str;
                if (this.anonymous != z) {
                    this.anonymous = z;
                    if (z) {
                        str = LocaleController.getString(R.string.StarsReactionAnonymous);
                    } else if (this.did >= 0) {
                        str = UserObject.getForcedFirstName(MessagesController.getInstance(StarsReactionsSheet.this.currentAccount).getUser(Long.valueOf(this.did)));
                    } else {
                        TLRPC.Chat chat = MessagesController.getInstance(StarsReactionsSheet.this.currentAccount).getChat(Long.valueOf(-this.did));
                        str = chat == null ? "" : chat.title;
                    }
                    this.text = new Text(str, 12.0f);
                    TopSendersView.this.invalidate();
                }
            }

            public void setStars(long j) {
                this.starsText = new Text(StarsIntroActivity.replaceStars("⭐️" + LocaleController.formatNumber(j, ','), 0.85f), 12.0f, AndroidUtilities.getTypeface("fonts/num.otf"));
            }
        }

        public TopSendersView(Context context) {
            super(context);
            this.senders = new ArrayList();
            this.oldSenders = new ArrayList();
            Paint paint = new Paint(1);
            this.backgroundPaint = paint;
            Paint paint2 = new Paint(1);
            this.starsBackgroundPaint = paint2;
            this.animatedCount = new AnimatedFloat(this, 0L, 320L, CubicBezierInterpolator.EASE_OUT_QUINT);
            paint.setStyle(Paint.Style.FILL_AND_STROKE);
            paint.setStrokeWidth(AndroidUtilities.dp(3.0f));
            paint.setColor(Theme.getColor(Theme.key_dialogBackground));
            paint2.setColor(-1002750);
        }

        @Override
        protected void dispatchDraw(Canvas canvas) {
            this.count = this.animatedCount.set(this.senders.size());
            for (int i = 0; i < this.oldSenders.size(); i++) {
                ((Sender) this.oldSenders.get(i)).draw(canvas);
            }
            for (int i2 = 0; i2 < this.senders.size(); i2++) {
                ((Sender) this.senders.get(i2)).draw(canvas);
            }
        }

        @Override
        protected void onAttachedToWindow() {
            super.onAttachedToWindow();
            for (int i = 0; i < this.senders.size(); i++) {
                ((Sender) this.senders.get(i)).imageReceiver.onAttachedToWindow();
            }
        }

        @Override
        protected void onDetachedFromWindow() {
            super.onDetachedFromWindow();
            for (int i = 0; i < this.senders.size(); i++) {
                ((Sender) this.senders.get(i)).imageReceiver.onDetachedFromWindow();
            }
        }

        @Override
        public boolean onTouchEvent(MotionEvent motionEvent) {
            Sender sender;
            Utilities.Callback callback;
            if (motionEvent.getAction() == 0) {
                Sender sender2 = this.pressedSender;
                if (sender2 != null) {
                    sender2.bounce.setPressed(false);
                }
                this.pressedSender = null;
                int i = 0;
                while (true) {
                    if (i >= this.senders.size()) {
                        break;
                    }
                    if (((Sender) this.senders.get(i)).clickBounds.contains(motionEvent.getX(), motionEvent.getY())) {
                        this.pressedSender = (Sender) this.senders.get(i);
                        break;
                    }
                    i++;
                }
                Sender sender3 = this.pressedSender;
                if (sender3 != null) {
                    sender3.bounce.setPressed(true);
                }
            } else if (motionEvent.getAction() == 1 || motionEvent.getAction() == 3) {
                if (motionEvent.getAction() == 1 && (sender = this.pressedSender) != null && !sender.anonymous && sender.clickBounds.contains(motionEvent.getX(), motionEvent.getY()) && (callback = this.clickListener) != null) {
                    callback.run(Long.valueOf(this.pressedSender.did));
                }
                Sender sender4 = this.pressedSender;
                if (sender4 != null) {
                    sender4.bounce.setPressed(false);
                }
                this.pressedSender = null;
            }
            return this.pressedSender != null;
        }

        public void setMyselfAsAnonymous(boolean z) {
            long clientUserId = UserConfig.getInstance(StarsReactionsSheet.this.currentAccount).getClientUserId();
            for (int i = 0; i < this.senders.size(); i++) {
                Sender sender = (Sender) this.senders.get(i);
                if (sender.did == clientUserId) {
                    sender.setAnonymous(z);
                    return;
                }
            }
        }

        public void setOnSenderClickListener(Utilities.Callback<Long> callback) {
            this.clickListener = callback;
        }

        public void setSenders(ArrayList<SenderData> arrayList) {
            Sender sender;
            int i = 0;
            while (true) {
                SenderData senderData = null;
                if (i >= this.senders.size()) {
                    break;
                }
                Sender sender2 = (Sender) this.senders.get(i);
                int i2 = 0;
                while (true) {
                    if (i2 >= arrayList.size()) {
                        break;
                    }
                    if (arrayList.get(i2).did == sender2.did) {
                        senderData = arrayList.get(i2);
                        break;
                    }
                    i2++;
                }
                if (senderData == null) {
                    sender2.imageReceiver.onDetachedFromWindow();
                    this.senders.remove(i);
                    i--;
                    sender2.index = -1;
                    this.oldSenders.add(sender2);
                }
                i++;
            }
            for (int i3 = 0; i3 < arrayList.size(); i3++) {
                SenderData senderData2 = arrayList.get(i3);
                int i4 = 0;
                while (true) {
                    if (i4 >= this.senders.size()) {
                        sender = null;
                        break;
                    } else {
                        if (((Sender) this.senders.get(i4)).did == senderData2.did) {
                            sender = (Sender) this.senders.get(i4);
                            break;
                        }
                        i4++;
                    }
                }
                if (sender == null) {
                    int i5 = 0;
                    while (true) {
                        if (i5 >= this.oldSenders.size()) {
                            break;
                        }
                        if (((Sender) this.oldSenders.get(i5)).did == senderData2.did) {
                            sender = (Sender) this.oldSenders.get(i5);
                            break;
                        }
                        i5++;
                    }
                    if (sender != null) {
                        this.oldSenders.remove(sender);
                        sender.imageReceiver.onAttachedToWindow();
                        this.senders.add(sender);
                    }
                }
                if (sender == null) {
                    sender = new Sender(senderData2.did);
                    sender.animatedScale.set(0.0f, true);
                    this.senders.add(sender);
                    sender.animatedPosition.set((arrayList.size() - 1) - i3, true);
                }
                sender.setStars(senderData2.stars);
                sender.setAnonymous(senderData2.anonymous);
                sender.index = (arrayList.size() - 1) - i3;
            }
            invalidate();
        }
    }

    public StarsReactionsSheet(final Context context, final int i, long j, final ChatActivity chatActivity, final MessageObject messageObject, ArrayList arrayList, boolean z, final Theme.ResourcesProvider resourcesProvider) {
        super(context, false, resourcesProvider);
        TLRPC.MessageReactor messageReactor;
        int i2 = 9;
        this.starRef = new ColoredImageSpan[1];
        this.checkedVisiblity = false;
        this.resourcesProvider = resourcesProvider;
        this.currentAccount = i;
        this.messageObject = messageObject;
        this.reactors = arrayList;
        this.selectedDialogId = UserConfig.getInstance(i).getClientUserId();
        long clientUserId = UserConfig.getInstance(i).getClientUserId();
        if (arrayList != null) {
            Iterator it = arrayList.iterator();
            TLRPC.MessageReactor messageReactor2 = null;
            while (it.hasNext()) {
                TLRPC.MessageReactor messageReactor3 = (TLRPC.MessageReactor) it.next();
                long peerDialogId = DialogObject.getPeerDialogId(messageReactor3.peer_id);
                if (messageReactor3.anonymous && messageReactor3.my) {
                    peerDialogId = clientUserId;
                }
                if (messageReactor3.my || peerDialogId == clientUserId) {
                    messageReactor2 = messageReactor3;
                }
            }
            messageReactor = messageReactor2;
        } else {
            messageReactor = null;
        }
        boolean z2 = (arrayList == null || arrayList.isEmpty()) ? false : true;
        this.anonymous = StarsController.getInstance(i).arePaidReactionsAnonymous(messageObject);
        fixNavigationBar(Theme.getColor(Theme.key_dialogBackground, resourcesProvider));
        LinearLayout linearLayout = new LinearLayout(context);
        this.layout = linearLayout;
        linearLayout.setOrientation(1);
        FrameLayout frameLayout = new FrameLayout(context);
        this.topLayout = frameLayout;
        linearLayout.addView(frameLayout, LayoutHelper.createLinear(-1, -2));
        this.slider = new StarsSlider(context) {
            @Override
            public void onValueChanged(int i3) {
                long j2 = i3;
                StarsReactionsSheet.this.updateSenders(j2);
                if (StarsReactionsSheet.this.buttonView != null) {
                    StarsReactionsSheet.this.buttonView.setText(StarsIntroActivity.replaceStars(LocaleController.formatString(R.string.StarsReactionSend, LocaleController.formatNumber(j2, ',')), StarsReactionsSheet.this.starRef), true);
                }
            }
        };
        int[] iArr = {1, 50, 100, 500, 1000, 2000, 5000, 7500, 10000};
        long j2 = MessagesController.getInstance(i).starsPaidReactionAmountMax;
        ArrayList arrayList2 = new ArrayList();
        int i3 = 0;
        while (true) {
            if (i3 >= i2) {
                break;
            }
            int i4 = iArr[i3];
            int i5 = i3;
            if (i4 > j2) {
                arrayList2.add(Integer.valueOf((int) j2));
                break;
            }
            arrayList2.add(Integer.valueOf(i4));
            if (iArr[i5] == j2) {
                break;
            }
            i3 = i5 + 1;
            i2 = 9;
        }
        int[] iArr2 = new int[arrayList2.size()];
        for (int i6 = 0; i6 < arrayList2.size(); i6++) {
            iArr2[i6] = ((Integer) arrayList2.get(i6)).intValue();
        }
        this.slider.setSteps(100, iArr2);
        if (z) {
            this.topLayout.addView(this.slider, LayoutHelper.createFrame(-1, -2.0f));
        }
        LinearLayout linearLayout2 = new LinearLayout(context);
        linearLayout2.setOrientation(0);
        this.topLayout.addView(linearLayout2, LayoutHelper.createFrame(-1, -2.0f, 55, 0.0f, 0.0f, 0.0f, 0.0f));
        StarsIntroActivity.StarsBalanceView starsBalanceView = new StarsIntroActivity.StarsBalanceView(context, i);
        this.balanceView = starsBalanceView;
        starsBalanceView.setDialogId(this.selectedDialogId);
        TextView textView = new TextView(context) {
            @Override
            protected void onMeasure(int i7, int i8) {
                super.onMeasure(i7, View.MeasureSpec.makeMeasureSpec(ActionBar.getCurrentActionBarHeight(), 1073741824));
            }
        };
        this.titleView = textView;
        int i7 = Theme.key_windowBackgroundWhiteBlackText;
        textView.setTextColor(Theme.getColor(i7));
        textView.setTextSize(1, 20.0f);
        textView.setGravity(3);
        textView.setText(LocaleController.getString(R.string.StarsReactionTitle));
        textView.setTypeface(AndroidUtilities.bold());
        textView.setMaxLines(2);
        linearLayout2.addView(textView, LayoutHelper.createLinear(-2, -2, 1.0f, 51, 6, 0, 6, 0));
        ScaleStateListAnimator.apply(starsBalanceView);
        starsBalanceView.setOnClickListener(new View.OnClickListener() {
            @Override
            public final void onClick(View view) {
                StarsReactionsSheet.this.lambda$new$0(chatActivity, view);
            }
        });
        linearLayout2.addView(starsBalanceView, LayoutHelper.createLinear(-2, -2, 0.0f, 53, 6, 0, 6, 0));
        LinearLayout linearLayout3 = new LinearLayout(context);
        linearLayout3.setOrientation(1);
        this.topLayout.addView(linearLayout3, LayoutHelper.createFrame(-1, -2.0f, 55, 0.0f, z ? 179.0f : 45.0f, 0.0f, 15.0f));
        final TLRPC.Chat chat = MessagesController.getInstance(i).getChat(Long.valueOf(-j));
        TextView textView2 = new TextView(context);
        this.statusView = textView2;
        textView2.setTextColor(Theme.getColor(i7));
        textView2.setTextSize(1, 14.0f);
        textView2.setGravity(17);
        textView2.setSingleLine(false);
        textView2.setMaxLines(3);
        textView2.setText(AndroidUtilities.replaceTags(messageReactor != null ? LocaleController.formatPluralStringComma("StarsReactionTextSent", messageReactor.count) : LocaleController.formatString(R.string.StarsReactionText, chat == null ? "" : chat.title)));
        if (z) {
            linearLayout3.addView(textView2, LayoutHelper.createLinear(-1, -2, 55, 40, 0, 40, 0));
        }
        if (z2) {
            View view = new View(context) {
                private final LinearGradient gradient = new LinearGradient(0.0f, 0.0f, 255.0f, 0.0f, new int[]{-1135603, -404714}, new float[]{0.0f, 1.0f}, Shader.TileMode.CLAMP);
                private final Matrix gradientMatrix = new Matrix();
                private final Paint backgroundPaint = new Paint(1);
                private final Paint separatorPaint = new Paint(1);
                private final Text text = new Text(LocaleController.getString(R.string.StarsReactionTopSenders), 14.16f, AndroidUtilities.bold());

                @Override
                public void dispatchDraw(Canvas canvas) {
                    this.gradientMatrix.reset();
                    this.gradientMatrix.postTranslate(AndroidUtilities.dp(14.0f), 0.0f);
                    this.gradientMatrix.postScale((getWidth() - AndroidUtilities.dp(28.0f)) / 255.0f, 1.0f);
                    this.gradient.setLocalMatrix(this.gradientMatrix);
                    this.backgroundPaint.setShader(this.gradient);
                    float currentWidth = this.text.getCurrentWidth() + AndroidUtilities.dp(30.0f);
                    this.separatorPaint.setColor(Theme.getColor(Theme.key_divider, resourcesProvider));
                    canvas.drawRect(AndroidUtilities.dp(24.0f), (getHeight() / 2.0f) - 1.0f, ((getWidth() - currentWidth) / 2.0f) - AndroidUtilities.dp(8.0f), getHeight() / 2.0f, this.separatorPaint);
                    canvas.drawRect(((getWidth() + currentWidth) / 2.0f) + AndroidUtilities.dp(8.0f), (getHeight() / 2.0f) - 1.0f, getWidth() - AndroidUtilities.dp(24.0f), getHeight() / 2.0f, this.separatorPaint);
                    RectF rectF = AndroidUtilities.rectTmp;
                    rectF.set((getWidth() - currentWidth) / 2.0f, 0.0f, (getWidth() + currentWidth) / 2.0f, getHeight());
                    canvas.drawRoundRect(rectF, getHeight() / 2.0f, getHeight() / 2.0f, this.backgroundPaint);
                    this.text.draw(canvas, (getWidth() - this.text.getCurrentWidth()) / 2.0f, getHeight() / 2.0f, -1, 1.0f);
                }
            };
            this.separatorView = view;
            linearLayout3.addView(view, LayoutHelper.createLinear(-1, 30, 55, 0, 20, 0, 0));
            TopSendersView topSendersView = new TopSendersView(context);
            this.topSendersView = topSendersView;
            topSendersView.setOnSenderClickListener(new Utilities.Callback() {
                @Override
                public final void run(Object obj) {
                    StarsReactionsSheet.this.lambda$new$1(i, chatActivity, (Long) obj);
                }
            });
            this.layout.addView(topSendersView, LayoutHelper.createLinear(-1, 110));
            View view2 = new View(context);
            this.checkSeparatorView = view2;
            view2.setBackgroundColor(Theme.getColor(Theme.key_divider, resourcesProvider));
            if (z || messageReactor != null) {
                this.layout.addView(view2, LayoutHelper.createLinear(-1, 1.0f / AndroidUtilities.density, 7, 24, 0, 24, 0));
            }
        } else {
            this.separatorView = null;
            this.topSendersView = null;
            this.checkSeparatorView = null;
        }
        CheckBox2 checkBox2 = new CheckBox2(context, 21, resourcesProvider);
        this.checkBox = checkBox2;
        checkBox2.setColor(Theme.key_radioBackgroundChecked, Theme.key_checkboxDisabled, Theme.key_checkboxCheck);
        checkBox2.setDrawUnchecked(true);
        checkBox2.setChecked(!this.anonymous, false);
        TopSendersView topSendersView2 = this.topSendersView;
        if (topSendersView2 != null) {
            topSendersView2.setMyselfAsAnonymous(this.anonymous);
        }
        checkBox2.setDrawBackgroundAsArc(10);
        TextView textView3 = new TextView(context);
        this.checkTextView = textView3;
        textView3.setTextColor(Theme.getColor(i7, resourcesProvider));
        textView3.setTextSize(1, 14.0f);
        textView3.setText(LocaleController.getString(R.string.StarsReactionShowMeInTopSenders));
        LinearLayout linearLayout4 = new LinearLayout(context);
        this.checkLayout = linearLayout4;
        linearLayout4.setOrientation(0);
        linearLayout4.setPadding(AndroidUtilities.dp(12.0f), AndroidUtilities.dp(8.0f), AndroidUtilities.dp(12.0f), AndroidUtilities.dp(8.0f));
        linearLayout4.addView(checkBox2, LayoutHelper.createLinear(21, 21, 16, 0, 0, 9, 0));
        linearLayout4.addView(textView3, LayoutHelper.createLinear(-2, -2, 16));
        linearLayout4.setOnClickListener(new View.OnClickListener() {
            @Override
            public final void onClick(View view3) {
                StarsReactionsSheet.this.lambda$new$2(view3);
            }
        });
        ScaleStateListAnimator.apply(linearLayout4, 0.05f, 1.2f);
        linearLayout4.setBackground(Theme.createRadSelectorDrawable(Theme.getColor(Theme.key_listSelector, resourcesProvider), 6, 6));
        if (z || messageReactor != null) {
            this.layout.addView(linearLayout4, LayoutHelper.createLinear(-2, -2, 1, 0, z2 ? 10 : 4, 0, 10));
        }
        ButtonWithCounterView buttonWithCounterView = new ButtonWithCounterView(context, resourcesProvider);
        this.buttonView = buttonWithCounterView;
        if (z) {
            this.layout.addView(buttonWithCounterView, LayoutHelper.createLinear(-1, 48, 14.0f, 0.0f, 14.0f, 0.0f));
        }
        updateSenders(0L);
        buttonWithCounterView.setText(StarsIntroActivity.replaceStars(LocaleController.formatString(R.string.StarsReactionSend, LocaleController.formatNumber(50L, ',')), this.starRef), true);
        buttonWithCounterView.setOnClickListener(new View.OnClickListener() {
            @Override
            public final void onClick(View view3) {
                StarsReactionsSheet.this.lambda$new$5(messageObject, chatActivity, i, context, resourcesProvider, chat, view3);
            }
        });
        LinkSpanDrawable.LinksTextView linksTextView = new LinkSpanDrawable.LinksTextView(context, resourcesProvider);
        linksTextView.setTextSize(1, 13.0f);
        linksTextView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteGrayText2, resourcesProvider));
        linksTextView.setText(AndroidUtilities.replaceSingleTag(LocaleController.getString(R.string.StarsReactionTerms), new Runnable() {
            @Override
            public final void run() {
                StarsReactionsSheet.lambda$new$6(context);
            }
        }));
        linksTextView.setGravity(17);
        linksTextView.setLinkTextColor(getThemedColor(Theme.key_dialogTextLink));
        if (z) {
            this.layout.addView(linksTextView, LayoutHelper.createLinear(-1, -2, 17, 14, 14, 14, 12));
        }
        setCustomView(this.layout);
        GLIconTextureView gLIconTextureView = new GLIconTextureView(context, 1, 2) {
            @Override
            protected void startIdleAnimation() {
            }
        };
        this.icon3dView = gLIconTextureView;
        GLIconRenderer gLIconRenderer = gLIconTextureView.mRenderer;
        gLIconRenderer.colorKey1 = Theme.key_starsGradient1;
        gLIconRenderer.colorKey2 = Theme.key_starsGradient2;
        gLIconRenderer.updateColors();
        gLIconTextureView.mRenderer.white = 1.0f;
        gLIconTextureView.setVisibility(4);
        gLIconTextureView.setPaused(true);
        this.container.addView(gLIconTextureView, LayoutHelper.createFrame(150, 150.0f));
        this.slider.setValue(50);
        if (arrayList != null) {
            long j3 = 0;
            for (int i8 = 0; i8 < arrayList.size(); i8++) {
                long j4 = ((TLRPC.MessageReactor) arrayList.get(i8)).count;
                if (j4 > j3) {
                    j3 = j4;
                }
            }
            j3 = messageReactor != null ? j3 - messageReactor.count : j3;
            if (j3 > 0) {
                this.slider.setStarsTop(j3 + 1);
            }
        }
    }

    private void animate3dIcon(final Runnable runnable) {
        View view;
        ReactionsLayoutInBubble reactionsLayoutInBubble;
        MessageObject messageObject;
        if (this.messageObject == null || (view = this.chatActivity.fragmentView) == null || !view.isAttachedToWindow()) {
            return;
        }
        View view2 = this.messageCell;
        if (view2 instanceof ChatMessageCell) {
            reactionsLayoutInBubble = ((ChatMessageCell) view2).reactionsLayoutInBubble;
        } else if (!(view2 instanceof ChatActionCell)) {
            return;
        } else {
            reactionsLayoutInBubble = ((ChatActionCell) view2).reactionsLayoutInBubble;
        }
        ReactionsLayoutInBubble.ReactionButton reactionButton = reactionsLayoutInBubble.getReactionButton(ReactionsLayoutInBubble.VisibleReaction.asStar());
        if (reactionButton == null) {
            MessageObject.GroupedMessages validGroupedMessage = this.chatActivity.getValidGroupedMessage(this.messageObject);
            if (validGroupedMessage != null && !validGroupedMessage.posArray.isEmpty()) {
                Iterator<MessageObject> it = validGroupedMessage.messages.iterator();
                while (true) {
                    if (!it.hasNext()) {
                        messageObject = null;
                        break;
                    }
                    messageObject = it.next();
                    MessageObject.GroupedMessagePosition position = validGroupedMessage.getPosition(messageObject);
                    if (position != null) {
                        int i = position.flags;
                        if ((i & 1) != 0 && (i & 8) != 0) {
                            break;
                        }
                    }
                }
                if (messageObject != null) {
                    view2 = this.chatActivity.findMessageCell(messageObject.getId(), false);
                }
            }
            if (view2 == null) {
                return;
            }
            if (view2 instanceof ChatMessageCell) {
                reactionsLayoutInBubble = ((ChatMessageCell) view2).reactionsLayoutInBubble;
                reactionButton = reactionsLayoutInBubble.getReactionButton(ReactionsLayoutInBubble.VisibleReaction.asStar());
            }
        }
        final View view3 = view2;
        final ReactionsLayoutInBubble reactionsLayoutInBubble2 = reactionsLayoutInBubble;
        final ReactionsLayoutInBubble.ReactionButton reactionButton2 = reactionButton;
        if (reactionButton2 == null) {
            return;
        }
        final int[] iArr = new int[2];
        final RectF rectF = new RectF();
        this.slider.getLocationInWindow(iArr);
        rectF.set(this.slider.counterImage.getBounds());
        rectF.inset(-AndroidUtilities.dp(3.5f), -AndroidUtilities.dp(3.5f));
        rectF.offset(iArr[0], iArr[1]);
        this.icon3dView.whenReady(new Runnable() {
            @Override
            public final void run() {
                StarsReactionsSheet.this.lambda$animate3dIcon$9();
            }
        });
        reactionButton2.drawImage = false;
        view3.invalidate();
        final RectF rectF2 = new RectF();
        final Runnable runnable2 = new Runnable() {
            @Override
            public final void run() {
                StarsReactionsSheet.lambda$animate3dIcon$10(view3, iArr, rectF2, reactionsLayoutInBubble2, reactionButton2);
            }
        };
        runnable2.run();
        this.icon3dView.setPaused(false);
        this.icon3dView.setVisibility(0);
        final RectF rectF3 = new RectF();
        rectF3.set(rectF);
        this.icon3dView.setTranslationX(rectF3.centerX() - (AndroidUtilities.dp(150.0f) / 2.0f));
        this.icon3dView.setTranslationY(rectF3.centerY() - (AndroidUtilities.dp(150.0f) / 2.0f));
        this.icon3dView.setScaleX(rectF3.width() / AndroidUtilities.dp(150.0f));
        this.icon3dView.setScaleY(rectF3.height() / AndroidUtilities.dp(150.0f));
        ValueAnimator valueAnimator = this.iconAnimator;
        if (valueAnimator != null) {
            valueAnimator.cancel();
        }
        final boolean[] zArr = new boolean[1];
        ValueAnimator ofFloat = ValueAnimator.ofFloat(0.0f, 1.0f);
        this.iconAnimator = ofFloat;
        ofFloat.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public final void onAnimationUpdate(ValueAnimator valueAnimator2) {
                StarsReactionsSheet.this.lambda$animate3dIcon$11(runnable2, rectF, rectF2, rectF3, zArr, runnable, valueAnimator2);
            }
        });
        this.iconAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animator) {
                StarsReactionsSheet.this.icon3dView.setVisibility(4);
                StarsReactionsSheet.this.icon3dView.setPaused(true);
                reactionButton2.drawImage = true;
                View view4 = view3;
                if (view4 != null) {
                    view4.invalidate();
                }
                StarsReactionsSheet.super.dismissInternal();
                boolean[] zArr2 = zArr;
                if (!zArr2[0]) {
                    zArr2[0] = true;
                    LaunchActivity.makeRipple(rectF2.centerX(), rectF2.centerY(), 1.5f);
                    try {
                        StarsReactionsSheet.this.container.performHapticFeedback(0, 1);
                    } catch (Exception unused) {
                    }
                    Runnable runnable3 = runnable;
                    if (runnable3 != null) {
                        runnable3.run();
                    }
                }
                LaunchActivity launchActivity = LaunchActivity.instance;
                if (launchActivity == null || launchActivity.getFireworksOverlay() == null) {
                    return;
                }
                LaunchActivity.instance.getFireworksOverlay().start(true);
            }
        });
        this.iconAnimator.setDuration(800L);
        this.iconAnimator.setInterpolator(new Interpolator() {
            @Override
            public float getInterpolation(float f) {
                return (float) Math.pow(f, 2.0d);
            }
        });
        this.iconAnimator.start();
    }

    private void checkVisibility() {
        if (this.checkedVisiblity) {
            return;
        }
        this.checkedVisiblity = true;
        MessageObject messageObject = this.messageObject;
        if (messageObject == null) {
            return;
        }
        Boolean isMyPaidReactionAnonymous = messageObject.isMyPaidReactionAnonymous();
        if (isMyPaidReactionAnonymous == null || isMyPaidReactionAnonymous.booleanValue() != this.anonymous) {
            this.messageObject.setMyPaidReactionAnonymous(this.anonymous);
            StarsController.MessageId from = StarsController.MessageId.from(this.messageObject);
            TLRPC.TL_messages_togglePaidReactionPrivacy tL_messages_togglePaidReactionPrivacy = new TLRPC.TL_messages_togglePaidReactionPrivacy();
            tL_messages_togglePaidReactionPrivacy.peer = MessagesController.getInstance(this.currentAccount).getInputPeer(from.did);
            tL_messages_togglePaidReactionPrivacy.msg_id = from.mid;
            tL_messages_togglePaidReactionPrivacy.isPrivate = this.anonymous;
            NotificationCenter.getInstance(this.currentAccount).lambda$postNotificationNameOnUIThread$1(NotificationCenter.starReactionAnonymousUpdate, Long.valueOf(from.did), Integer.valueOf(from.mid), Boolean.valueOf(this.anonymous));
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(tL_messages_togglePaidReactionPrivacy, new RequestDelegate() {
                @Override
                public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                    StarsReactionsSheet.this.lambda$checkVisibility$8(tLObject, tL_error);
                }
            });
        }
    }

    public static void lambda$animate3dIcon$10(View view, int[] iArr, RectF rectF, ReactionsLayoutInBubble reactionsLayoutInBubble, ReactionsLayoutInBubble.ReactionButton reactionButton) {
        view.getLocationInWindow(iArr);
        rectF.set(iArr[0] + reactionsLayoutInBubble.x + reactionButton.x + AndroidUtilities.dp(4.0f), iArr[1] + reactionsLayoutInBubble.y + reactionButton.y + ((reactionButton.height - AndroidUtilities.dp(22.0f)) / 2.0f), iArr[0] + reactionsLayoutInBubble.x + reactionButton.x + AndroidUtilities.dp(26.0f), iArr[1] + reactionsLayoutInBubble.y + reactionButton.y + ((reactionButton.height + AndroidUtilities.dp(22.0f)) / 2.0f));
    }

    public void lambda$animate3dIcon$11(Runnable runnable, RectF rectF, RectF rectF2, RectF rectF3, boolean[] zArr, Runnable runnable2, ValueAnimator valueAnimator) {
        float floatValue = ((Float) valueAnimator.getAnimatedValue()).floatValue();
        runnable.run();
        AndroidUtilities.lerp(rectF, rectF2, floatValue, rectF3);
        this.icon3dView.setTranslationX(rectF3.centerX() - (AndroidUtilities.dp(150.0f) / 2.0f));
        this.icon3dView.setTranslationY(rectF3.centerY() - (AndroidUtilities.dp(150.0f) / 2.0f));
        float max = Math.max(rectF3.width() / AndroidUtilities.dp(150.0f), rectF3.height() / AndroidUtilities.dp(150.0f));
        double d = floatValue;
        Double.isNaN(d);
        float lerp = AndroidUtilities.lerp(max, 1.0f, (float) Math.sin(d * 3.141592653589793d));
        this.icon3dView.setScaleX(lerp);
        this.icon3dView.setScaleY(lerp);
        GLIconRenderer gLIconRenderer = this.icon3dView.mRenderer;
        gLIconRenderer.angleX = 360.0f * floatValue;
        gLIconRenderer.white = Math.max(0.0f, 1.0f - (4.0f * floatValue));
        if (zArr[0] || floatValue <= 0.95f) {
            return;
        }
        zArr[0] = true;
        LaunchActivity.makeRipple(rectF2.centerX(), rectF2.centerY(), 1.5f);
        try {
            this.container.performHapticFeedback(0, 1);
        } catch (Exception unused) {
        }
        if (runnable2 != null) {
            runnable2.run();
        }
    }

    public void lambda$animate3dIcon$9() {
        StarsSlider starsSlider = this.slider;
        starsSlider.drawCounterImage = false;
        starsSlider.invalidate();
    }

    public void lambda$checkVisibility$8(TLObject tLObject, TLRPC.TL_error tL_error) {
        if (tLObject instanceof TLRPC.TL_boolTrue) {
            MessagesStorage.getInstance(this.currentAccount).putMessages(new ArrayList<>(Arrays.asList(this.messageObject.messageOwner)), true, true, true, 0, 0, 0L);
        }
    }

    public void lambda$new$0(final ChatActivity chatActivity, View view) {
        dismiss();
        chatActivity.presentFragment(new StarsIntroActivity() {
            @Override
            public void onFragmentDestroy() {
                super.onFragmentDestroy();
                if (chatActivity.isFullyVisible) {
                    StarsReactionsSheet.this.show();
                }
            }
        });
    }

    public void lambda$new$1(int i, ChatActivity chatActivity, Long l) {
        Bundle bundle = new Bundle();
        bundle.putLong("user_id", l.longValue());
        if (l.longValue() == UserConfig.getInstance(i).getClientUserId()) {
            bundle.putBoolean("my_profile", true);
        }
        chatActivity.presentFragment(new ProfileActivity(bundle) {
            @Override
            public void onFragmentDestroy() {
                super.onFragmentDestroy();
                StarsReactionsSheet.this.show();
            }
        });
        dismiss();
    }

    public void lambda$new$2(View view) {
        this.checkBox.setChecked(!r3.isChecked(), true);
        boolean z = !this.checkBox.isChecked();
        this.anonymous = z;
        TopSendersView topSendersView = this.topSendersView;
        if (topSendersView != null) {
            topSendersView.setMyselfAsAnonymous(z);
        }
    }

    public void lambda$new$3(final StarsController.PendingPaidReactions pendingPaidReactions) {
        this.sending = true;
        Objects.requireNonNull(pendingPaidReactions);
        animate3dIcon(new Runnable() {
            @Override
            public final void run() {
                StarsController.PendingPaidReactions.this.apply();
            }
        });
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                StarsReactionsSheet.this.dismiss();
            }
        }, 240L);
    }

    public void lambda$new$4(MessageObject messageObject, StarsController starsController, ChatActivity chatActivity, long j) {
        if (messageObject != null) {
            messageObject.isMyPaidReactionAnonymous();
        }
        final StarsController.PendingPaidReactions sendPaidReaction = starsController.sendPaidReaction(messageObject, chatActivity, j, false, true, Boolean.valueOf(!this.checkBox.isChecked()));
        if (sendPaidReaction == null) {
            return;
        }
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                StarsReactionsSheet.this.lambda$new$3(sendPaidReaction);
            }
        });
    }

    public void lambda$new$5(final MessageObject messageObject, final ChatActivity chatActivity, int i, Context context, Theme.ResourcesProvider resourcesProvider, TLRPC.Chat chat, View view) {
        if (messageObject == null || chatActivity == null || this.iconAnimator != null) {
            return;
        }
        final long value = this.slider.getValue();
        final StarsController starsController = StarsController.getInstance(i);
        Runnable runnable = new Runnable() {
            @Override
            public final void run() {
                StarsReactionsSheet.this.lambda$new$4(messageObject, starsController, chatActivity, value);
            }
        };
        if (!starsController.balanceAvailable() || starsController.getBalance().amount >= value) {
            runnable.run();
        } else {
            new StarsIntroActivity.StarsNeededSheet(context, resourcesProvider, value, 5, chat == null ? "" : chat.title, runnable).show();
        }
    }

    public static void lambda$new$6(Context context) {
        Browser.openUrl(context, LocaleController.getString(R.string.StarsReactionTermsLink));
    }

    public static int lambda$updateSenders$7(SenderData senderData, SenderData senderData2) {
        return (int) (senderData2.stars - senderData.stars);
    }

    @Override
    public boolean canDismissWithSwipe() {
        if (this.slider.tracking) {
            return false;
        }
        return super.canDismissWithSwipe();
    }

    @Override
    public void dismiss() {
        if (!this.sending) {
            checkVisibility();
        }
        super.dismiss();
    }

    @Override
    public void dismissInternal() {
        ValueAnimator valueAnimator = this.iconAnimator;
        if (valueAnimator == null || !valueAnimator.isRunning()) {
            super.dismissInternal();
        }
    }

    public void setMessageCell(ChatActivity chatActivity, int i, View view) {
        this.chatActivity = chatActivity;
        this.messageId = i;
        this.messageCell = view;
    }

    public void updateSenders(long j) {
        if (this.topSendersView != null) {
            ArrayList arrayList = new ArrayList();
            long clientUserId = UserConfig.getInstance(this.currentAccount).getClientUserId();
            long j2 = 0;
            if (this.reactors != null) {
                for (int i = 0; i < this.reactors.size(); i++) {
                    TLRPC.MessageReactor messageReactor = (TLRPC.MessageReactor) this.reactors.get(i);
                    long peerDialogId = DialogObject.getPeerDialogId(messageReactor.peer_id);
                    boolean z = messageReactor.anonymous;
                    if (z) {
                        peerDialogId = messageReactor.my ? clientUserId : -i;
                    }
                    if (messageReactor.my || peerDialogId == clientUserId) {
                        j2 = messageReactor.count;
                    } else {
                        arrayList.add(SenderData.of(z, peerDialogId, messageReactor.count));
                    }
                }
            }
            long j3 = j2 + j;
            if (j3 > 0) {
                arrayList.add(SenderData.of(this.anonymous, clientUserId, j3));
            }
            Collections.sort(arrayList, new Comparator() {
                @Override
                public final int compare(Object obj, Object obj2) {
                    int lambda$updateSenders$7;
                    lambda$updateSenders$7 = StarsReactionsSheet.lambda$updateSenders$7((StarsReactionsSheet.SenderData) obj, (StarsReactionsSheet.SenderData) obj2);
                    return lambda$updateSenders$7;
                }
            });
            this.topSendersView.setSenders(new ArrayList<>(arrayList.subList(0, Math.min(3, arrayList.size()))));
        }
    }
}
