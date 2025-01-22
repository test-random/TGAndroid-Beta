package org.telegram.ui.Stories.recorder;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.core.content.ContextCompat;
import androidx.vectordrawable.graphics.drawable.AnimatedVectorDrawableCompat$$ExternalSyntheticApiModelOutline0;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LiteMode;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.R;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.Utilities;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.AnimatedFloat;
import org.telegram.ui.Components.AnimatedTextView;
import org.telegram.ui.Components.BlobDrawable;
import org.telegram.ui.Components.BlurringShader;
import org.telegram.ui.Components.ButtonBounce;
import org.telegram.ui.Components.CubicBezierInterpolator;
import org.telegram.ui.Components.ItemOptions;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.RLottieDrawable;
import org.telegram.ui.Components.SizeNotifierFrameLayout;
import org.telegram.ui.Components.Text;
import org.telegram.ui.Stories.recorder.CaptionContainerView;

public abstract class CaptionStory extends CaptionContainerView {
    public static final int[] periods = {21600, 43200, 86400, 172800};
    private float amplitude;
    private final AnimatedFloat animatedAmplitude;
    private final BlobDrawable bigWaveDrawable;
    private final Path boundsPath;
    private final AnimatedFloat cancel2T;
    private final RectF cancelBounds;
    private final AnimatedFloat cancelT;
    private Text cancelText;
    private boolean cancelling;
    private final Path circlePath;
    private RoundVideoRecorder currentRecorder;
    private final Runnable doneCancel;
    private Drawable flipButton;
    private float fromX;
    private float fromY;
    private boolean hasRoundVideo;
    private final AnimatedFloat lock2T;
    private final Paint lockBackgroundPaint;
    private final RectF lockBounds;
    private final AnimatedFloat lockCancelledT;
    private final Path lockHandle;
    private final Paint lockHandlePaint;
    private final Paint lockPaint;
    private float lockProgress;
    private final RectF lockRect;
    private final Paint lockShadowPaint;
    private final AnimatedFloat lockT;
    private boolean locked;
    private Utilities.Callback onPeriodUpdate;
    private Utilities.Callback onPremiumHintShow;
    public ImageView periodButton;
    public CaptionContainerView.PeriodDrawable periodDrawable;
    private int periodIndex;
    private ItemOptions periodPopup;
    private boolean periodVisible;
    private final RecordDot recordPaint;
    private boolean recordTouch;
    private boolean recording;
    public ImageView roundButton;
    public ButtonBounce roundButtonBounce;
    private final Drawable roundDrawable;
    private final Paint roundPaint;
    private float slideProgress;
    private Paint slideToCancelArrowPaint;
    private Path slideToCancelArrowPath;
    private Text slideToCancelText;
    private long startTime;
    private boolean stopping;
    private final AnimatedTextView.AnimatedTextDrawable timerTextDrawable;
    private final BlobDrawable tinyWaveDrawable;
    private final Paint whitePaint;

    public class RecordDot extends Drawable {
        private float alpha;
        boolean attachedToWindow;
        RLottieDrawable drawable;
        private boolean enterAnimation;
        private boolean isIncr;
        private long lastUpdateTime;
        private final View parent;
        boolean playing;
        private final Paint redDotPaint = new Paint(1);
        private float alpha2 = 1.0f;

        public RecordDot(View view) {
            this.parent = view;
            int i = R.raw.chat_audio_record_delete_3;
            RLottieDrawable rLottieDrawable = new RLottieDrawable(i, "" + i, AndroidUtilities.dp(28.0f), AndroidUtilities.dp(28.0f), false, null);
            this.drawable = rLottieDrawable;
            rLottieDrawable.setCurrentParentView(view);
            this.drawable.setInvalidateOnProgressSet(true);
            updateColors();
        }

        public void attach() {
            this.attachedToWindow = true;
            if (this.playing) {
                this.drawable.start();
            }
            this.drawable.setMasterParent(this.parent);
        }

        public void detach() {
            this.attachedToWindow = false;
            this.drawable.stop();
            this.drawable.setMasterParent(null);
        }

        @Override
        public void draw(Canvas canvas) {
            boolean z;
            if (this.playing) {
                this.drawable.setAlpha((int) (this.alpha * 255.0f * this.alpha2));
            }
            this.redDotPaint.setAlpha((int) (this.alpha * 255.0f * this.alpha2));
            long currentTimeMillis = System.currentTimeMillis() - this.lastUpdateTime;
            if (this.enterAnimation) {
                this.alpha = 1.0f;
            } else if (this.isIncr || this.playing) {
                float f = this.alpha + (((float) currentTimeMillis) / 600.0f);
                this.alpha = f;
                if (f >= 1.0f) {
                    this.alpha = 1.0f;
                    z = false;
                    this.isIncr = z;
                }
            } else {
                float f2 = this.alpha - (((float) currentTimeMillis) / 600.0f);
                this.alpha = f2;
                if (f2 <= 0.0f) {
                    this.alpha = 0.0f;
                    z = true;
                    this.isIncr = z;
                }
            }
            this.lastUpdateTime = System.currentTimeMillis();
            this.drawable.setBounds(getBounds());
            if (this.playing) {
                this.drawable.draw(canvas);
            }
            if (!this.playing || !this.drawable.hasBitmap()) {
                canvas.drawCircle(getBounds().centerX(), getBounds().centerY(), AndroidUtilities.dp(5.0f), this.redDotPaint);
            }
            CaptionStory.this.invalidate();
        }

        @Override
        public int getOpacity() {
            return -2;
        }

        public void playDeleteAnimation() {
            this.playing = true;
            this.drawable.setProgress(0.0f);
            if (this.attachedToWindow) {
                this.drawable.start();
            }
        }

        public void reset() {
            this.playing = false;
            this.drawable.stop();
            this.drawable.setProgress(0.0f);
        }

        @Override
        public void setAlpha(int i) {
            this.alpha2 = i / 255.0f;
        }

        @Override
        public void setColorFilter(ColorFilter colorFilter) {
        }

        public void updateColors() {
            this.redDotPaint.setColor(-2406842);
            this.drawable.beginApplyLayerColors();
            this.drawable.setLayerColor("Cup Red.**", -2406842);
            this.drawable.setLayerColor("Box.**", -2406842);
            this.drawable.commitApplyLayerColors();
        }
    }

    public CaptionStory(Context context, final FrameLayout frameLayout, SizeNotifierFrameLayout sizeNotifierFrameLayout, FrameLayout frameLayout2, final Theme.ResourcesProvider resourcesProvider, BlurringShader.BlurManager blurManager) {
        super(context, frameLayout, sizeNotifierFrameLayout, frameLayout2, resourcesProvider, blurManager);
        this.periodVisible = true;
        this.periodIndex = 0;
        this.recordPaint = new RecordDot(this);
        AnimatedTextView.AnimatedTextDrawable animatedTextDrawable = new AnimatedTextView.AnimatedTextDrawable(false, true, true);
        this.timerTextDrawable = animatedTextDrawable;
        CubicBezierInterpolator cubicBezierInterpolator = CubicBezierInterpolator.DEFAULT;
        animatedTextDrawable.setAnimationProperties(0.16f, 0L, 50L, cubicBezierInterpolator);
        animatedTextDrawable.setTextSize(AndroidUtilities.dp(15.0f));
        animatedTextDrawable.setTypeface(AndroidUtilities.bold());
        animatedTextDrawable.setText("0:00.0");
        animatedTextDrawable.setTextColor(-1);
        Paint paint = new Paint(1);
        this.whitePaint = paint;
        Paint paint2 = new Paint(1);
        this.roundPaint = paint2;
        BlobDrawable blobDrawable = new BlobDrawable(11, 98784);
        this.tinyWaveDrawable = blobDrawable;
        BlobDrawable blobDrawable2 = new BlobDrawable(12, 98784);
        this.bigWaveDrawable = blobDrawable2;
        paint.setColor(-1);
        paint2.setColor(-15033089);
        blobDrawable.minRadius = AndroidUtilities.dp(47.0f);
        blobDrawable.maxRadius = AndroidUtilities.dp(55.0f);
        blobDrawable.generateBlob();
        blobDrawable2.minRadius = AndroidUtilities.dp(47.0f);
        blobDrawable2.maxRadius = AndroidUtilities.dp(55.0f);
        blobDrawable2.generateBlob();
        this.roundDrawable = getContext().getResources().getDrawable(R.drawable.input_video_pressed).mutate();
        this.animatedAmplitude = new AnimatedFloat(new Runnable() {
            @Override
            public final void run() {
                CaptionStory.this.invalidateDrawOver2();
            }
        }, 0L, 200L, cubicBezierInterpolator);
        this.circlePath = new Path();
        this.boundsPath = new Path();
        this.lockBackgroundPaint = new Paint(1);
        this.lockShadowPaint = new Paint(1);
        this.lockPaint = new Paint(1);
        Paint paint3 = new Paint(1);
        this.lockHandlePaint = paint3;
        paint3.setStyle(Paint.Style.STROKE);
        Runnable runnable = new Runnable() {
            @Override
            public final void run() {
                CaptionStory.this.invalidateDrawOver2();
            }
        };
        CubicBezierInterpolator cubicBezierInterpolator2 = CubicBezierInterpolator.EASE_OUT_QUINT;
        this.lockCancelledT = new AnimatedFloat(runnable, 350L, cubicBezierInterpolator2);
        this.lockBounds = new RectF();
        this.cancelBounds = new RectF();
        this.lockRect = new RectF();
        this.lockHandle = new Path();
        this.cancelT = new AnimatedFloat(this, 0L, 350L, cubicBezierInterpolator2);
        this.cancel2T = new AnimatedFloat(new Runnable() {
            @Override
            public final void run() {
                CaptionStory.this.invalidateDrawOver2();
            }
        }, 0L, 420L, cubicBezierInterpolator2);
        this.lockT = new AnimatedFloat(this, 0L, 350L, cubicBezierInterpolator2);
        this.lock2T = new AnimatedFloat(new Runnable() {
            @Override
            public final void run() {
                CaptionStory.this.invalidateDrawOver2();
            }
        }, 0L, 350L, cubicBezierInterpolator2);
        this.doneCancel = new Runnable() {
            @Override
            public final void run() {
                CaptionStory.this.lambda$new$6();
            }
        };
        ImageView imageView = new ImageView(context);
        this.roundButton = imageView;
        this.roundButtonBounce = new ButtonBounce(imageView);
        this.roundButton.setImageResource(R.drawable.input_video_story);
        this.roundButton.setBackground(Theme.createSelectorDrawable(1090519039, 1, AndroidUtilities.dp(18.0f)));
        ImageView imageView2 = this.roundButton;
        ImageView.ScaleType scaleType = ImageView.ScaleType.CENTER;
        imageView2.setScaleType(scaleType);
        addView(this.roundButton, LayoutHelper.createFrame(44, 44.0f, 85, 0.0f, 0.0f, 11.0f, 10.0f));
        this.roundButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public final void onClick(View view) {
                CaptionStory.this.lambda$new$0(view);
            }
        });
        ImageView imageView3 = new ImageView(context);
        this.periodButton = imageView3;
        CaptionContainerView.PeriodDrawable periodDrawable = new CaptionContainerView.PeriodDrawable();
        this.periodDrawable = periodDrawable;
        imageView3.setImageDrawable(periodDrawable);
        this.periodButton.setBackground(Theme.createSelectorDrawable(1090519039, 1, AndroidUtilities.dp(18.0f)));
        this.periodButton.setScaleType(scaleType);
        setPeriod(86400, false);
        addView(this.periodButton, LayoutHelper.createFrame(44, 44.0f, 85, 0.0f, 0.0f, 51.0f, 10.0f));
        this.periodButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public final void onClick(View view) {
                CaptionStory.this.lambda$new$5(frameLayout, resourcesProvider, view);
            }
        });
    }

    private void checkFlipButton() {
        if (this.flipButton != null) {
            return;
        }
        this.flipButton = Build.VERSION.SDK_INT >= 21 ? AnimatedVectorDrawableCompat$$ExternalSyntheticApiModelOutline0.m(ContextCompat.getDrawable(getContext(), R.drawable.avd_flip)) : getContext().getResources().getDrawable(R.drawable.vd_flip).mutate();
    }

    private void drawLock(Canvas canvas, RectF rectF, float f) {
        RectF rectF2;
        Paint paint;
        float f2 = this.cancel2T.get();
        float f3 = this.lock2T.get();
        float lerp = AndroidUtilities.lerp(this.lockCancelledT.set(this.slideProgress < 0.4f), 0.0f, f3) * (1.0f - f2) * f;
        float dp = rectF.right - AndroidUtilities.dp(20.0f);
        float lerp2 = (AndroidUtilities.lerp(AndroidUtilities.dp(50.0f), AndroidUtilities.dp(36.0f), f3) * lerp) / 2.0f;
        float f4 = 1.0f - f3;
        float lerp3 = AndroidUtilities.lerp(((rectF.bottom - AndroidUtilities.dp(80.0f)) - lerp2) - ((AndroidUtilities.dp(120.0f) * this.lockProgress) * f4), rectF.bottom - AndroidUtilities.dp(20.0f), 1.0f - lerp);
        float dp2 = (AndroidUtilities.dp(36.0f) * lerp) / 2.0f;
        this.lockBounds.set(dp - dp2, lerp3 - lerp2, dp2 + dp, lerp2 + lerp3);
        float lerp4 = AndroidUtilities.lerp(AndroidUtilities.dp(18.0f), AndroidUtilities.dp(14.0f), f3);
        this.lockShadowPaint.setShadowLayer(AndroidUtilities.dp(1.0f), 0.0f, AndroidUtilities.dp(0.66f), Theme.multAlpha(536870912, lerp));
        this.lockShadowPaint.setColor(0);
        canvas.drawRoundRect(this.lockBounds, lerp4, lerp4, this.lockShadowPaint);
        Paint paint2 = this.backgroundBlur.getPaint(lerp);
        if (paint2 == null) {
            this.lockBackgroundPaint.setColor(1073741824);
            this.lockBackgroundPaint.setAlpha((int) (64.0f * lerp));
            rectF2 = this.lockBounds;
            paint = this.lockBackgroundPaint;
        } else {
            canvas.drawRoundRect(this.lockBounds, lerp4, lerp4, paint2);
            this.backgroundPaint.setAlpha((int) (51.0f * lerp));
            rectF2 = this.lockBounds;
            paint = this.backgroundPaint;
        }
        canvas.drawRoundRect(rectF2, lerp4, lerp4, paint);
        canvas.save();
        canvas.scale(lerp, lerp, dp, lerp3);
        this.lockPaint.setColor(Theme.multAlpha(-1, lerp));
        this.lockHandlePaint.setColor(Theme.multAlpha(-1, lerp * f4));
        float lerp5 = AndroidUtilities.lerp(AndroidUtilities.dp(15.33f), AndroidUtilities.dp(13.0f), f3);
        float lerp6 = AndroidUtilities.lerp(AndroidUtilities.dp(12.66f), AndroidUtilities.dp(13.0f), f3);
        float dp3 = lerp3 + (AndroidUtilities.dp(4.0f) * f4);
        canvas.rotate(this.lockProgress * 12.0f * f4, dp, dp3);
        float f5 = lerp5 / 2.0f;
        float f6 = lerp6 / 2.0f;
        float f7 = dp3 - f6;
        this.lockRect.set(dp - f5, f7, f5 + dp, dp3 + f6);
        canvas.drawRoundRect(this.lockRect, AndroidUtilities.dp(3.66f), AndroidUtilities.dp(3.66f), this.lockPaint);
        if (f3 < 1.0f) {
            canvas.save();
            canvas.rotate(this.lockProgress * 12.0f * f4, dp, f7);
            canvas.translate(0.0f, f6 * f3);
            canvas.scale(f4, f4, dp, f7);
            this.lockHandle.rewind();
            float dp4 = AndroidUtilities.dp(4.33f);
            float dp5 = f7 - AndroidUtilities.dp(3.66f);
            float f8 = dp + dp4;
            this.lockHandle.moveTo(f8, AndroidUtilities.dp(3.66f) + dp5);
            this.lockHandle.lineTo(f8, dp5);
            RectF rectF3 = AndroidUtilities.rectTmp;
            float f9 = dp - dp4;
            rectF3.set(f9, dp5 - dp4, f8, dp4 + dp5);
            this.lockHandle.arcTo(rectF3, 0.0f, -180.0f, false);
            this.lockHandle.lineTo(f9, dp5 + (AndroidUtilities.dp(3.66f) * AndroidUtilities.lerp(AndroidUtilities.lerp(0.4f, 0.0f, this.lockProgress), 1.0f, f3)));
            this.lockHandlePaint.setStrokeWidth(AndroidUtilities.dp(2.0f));
            canvas.drawPath(this.lockHandle, this.lockHandlePaint);
            canvas.restore();
        }
        canvas.restore();
    }

    public void lambda$new$0(View view) {
        showRemoveRoundAlert();
    }

    public void lambda$new$1(Integer num) {
        setPeriod(num.intValue());
        Utilities.Callback callback = this.onPeriodUpdate;
        if (callback != null) {
            callback.run(num);
        }
    }

    public void lambda$new$2(Integer num) {
        Utilities.Callback callback = this.onPremiumHintShow;
        if (callback != null) {
            callback.run(num);
        }
    }

    public static void lambda$new$3(Utilities.Callback callback, int i) {
        callback.run(Integer.valueOf(i));
    }

    public static void lambda$new$4(Utilities.Callback callback, int i) {
        callback.run(Integer.valueOf(i));
    }

    public void lambda$new$5(FrameLayout frameLayout, Theme.ResourcesProvider resourcesProvider, View view) {
        ItemOptions itemOptions = this.periodPopup;
        if (itemOptions != null && itemOptions.isShown()) {
            return;
        }
        final Utilities.Callback callback = new Utilities.Callback() {
            @Override
            public final void run(Object obj) {
                CaptionStory.this.lambda$new$1((Integer) obj);
            }
        };
        boolean isPremium = UserConfig.getInstance(this.currentAccount).isPremium();
        final Utilities.Callback callback2 = isPremium ? null : new Utilities.Callback() {
            @Override
            public final void run(Object obj) {
                CaptionStory.this.lambda$new$2((Integer) obj);
            }
        };
        ItemOptions makeOptions = ItemOptions.makeOptions(frameLayout, resourcesProvider, this.periodButton);
        this.periodPopup = makeOptions;
        makeOptions.addText(LocaleController.getString("StoryPeriodHint"), 13, AndroidUtilities.dp(200.0f));
        this.periodPopup.addGap();
        int i = 0;
        while (true) {
            int[] iArr = periods;
            if (i >= iArr.length) {
                this.periodPopup.setDimAlpha(0).show();
                return;
            }
            final int i2 = iArr[i];
            this.periodPopup.add(0, i2 == Integer.MAX_VALUE ? LocaleController.getString("StoryPeriodKeep") : LocaleController.formatPluralString("Hours", i2 / 3600, new Object[0]), Theme.key_actionBarDefaultSubmenuItem, new Runnable() {
                @Override
                public final void run() {
                    CaptionStory.lambda$new$3(Utilities.Callback.this, i2);
                }
            }).putPremiumLock((isPremium || i2 == 86400 || i2 == Integer.MAX_VALUE) ? null : new Runnable() {
                @Override
                public final void run() {
                    CaptionStory.lambda$new$4(Utilities.Callback.this, i2);
                }
            });
            if (this.periodIndex == i) {
                this.periodPopup.putCheck();
            }
            i++;
        }
    }

    public void lambda$new$6() {
        setCollapsed(false, Integer.MIN_VALUE);
        this.roundButton.setVisibility(0);
        this.periodButton.setVisibility(0);
    }

    public void lambda$showRemoveRoundAlert$7(AlertDialog alertDialog, int i) {
        removeRound();
    }

    public void releaseRecord(boolean z, boolean z2) {
        AndroidUtilities.cancelRunOnUIThread(this.doneCancel);
        this.stopping = true;
        this.recording = false;
        setCollapsed(false, (int) ((getBounds().right - AndroidUtilities.dp(20.0f)) - ((getWidth() * 0.35f) * this.slideProgress)));
        RoundVideoRecorder roundVideoRecorder = this.currentRecorder;
        if (roundVideoRecorder != null) {
            if (!z) {
                if (z2) {
                    roundVideoRecorder.cancel();
                } else {
                    roundVideoRecorder.stop();
                }
            }
            this.currentRecorder = null;
        }
        invalidateDrawOver2();
    }

    private boolean roundButtonTouchEvent(MotionEvent motionEvent) {
        if (motionEvent.getAction() == 0) {
            if (stopRecording()) {
                return true;
            }
            this.recordTouch = true;
            if (getParent() != null) {
                getParent().requestDisallowInterceptTouchEvent(true);
            }
            if (!canRecord()) {
                return true;
            }
            AndroidUtilities.cancelRunOnUIThread(this.doneCancel);
            this.fromX = motionEvent.getX();
            this.fromY = motionEvent.getY();
            this.amplitude = 0.0f;
            this.slideProgress = 0.0f;
            this.cancelT.set(0.0f, true);
            this.cancel2T.set(0.0f, true);
            this.cancelling = false;
            this.stopping = false;
            this.locked = false;
            this.recordPaint.reset();
            this.recording = true;
            this.startTime = System.currentTimeMillis();
            setCollapsed(true, Integer.MAX_VALUE);
            invalidateDrawOver2();
            RoundVideoRecorder roundVideoRecorder = new RoundVideoRecorder(getContext()) {
                @Override
                protected void receivedAmplitude(double d) {
                    CaptionStory.this.setAmplitude(d);
                }

                @Override
                public void stop() {
                    super.stop();
                    if (CaptionStory.this.recording) {
                        CaptionStory.this.releaseRecord(true, false);
                    }
                }
            };
            this.currentRecorder = roundVideoRecorder;
            putRecorder(roundVideoRecorder);
            return true;
        }
        if (motionEvent.getAction() == 2) {
            if (!this.cancelling) {
                this.slideProgress = Utilities.clamp((this.fromX - motionEvent.getX()) / (getWidth() * 0.35f), 1.0f, 0.0f);
                float clamp = Utilities.clamp((this.fromY - motionEvent.getY()) / (getWidth() * 0.3f), 1.0f, 0.0f);
                this.lockProgress = clamp;
                boolean z = this.locked;
                if (!z && !this.cancelling && this.slideProgress >= 1.0f) {
                    this.cancelling = true;
                    this.recording = false;
                    this.roundButton.setVisibility(4);
                    this.periodButton.setVisibility(4);
                    this.recordPaint.playDeleteAnimation();
                    RoundVideoRecorder roundVideoRecorder2 = this.currentRecorder;
                    if (roundVideoRecorder2 != null) {
                        roundVideoRecorder2.cancel();
                    }
                    AndroidUtilities.runOnUIThread(this.doneCancel, 800L);
                } else if (!z && !this.cancelling && clamp >= 1.0f && this.slideProgress < 0.4f) {
                    this.locked = true;
                    try {
                        performHapticFeedback(3, 1);
                    } catch (Exception unused) {
                    }
                }
                invalidate();
                invalidateDrawOver2();
            }
        } else if (motionEvent.getAction() == 1 || motionEvent.getAction() == 3) {
            if (!this.cancelling && !this.locked) {
                releaseRecord(false, false);
            }
            this.recordTouch = false;
        }
        return this.recordTouch;
    }

    @Override
    public int additionalRightMargin() {
        return 36;
    }

    @Override
    protected void afterUpdateShownKeyboard(boolean z) {
        this.periodButton.setVisibility((z || !this.periodVisible) ? 8 : 0);
        this.roundButton.setVisibility(z ? 8 : 0);
        if (z) {
            this.periodButton.setVisibility(8);
        }
    }

    @Override
    protected void beforeUpdateShownKeyboard(boolean z) {
        if (z) {
            return;
        }
        this.periodButton.setVisibility(this.periodVisible ? 0 : 8);
        this.roundButton.setVisibility(0);
    }

    public abstract boolean canRecord();

    @Override
    public boolean dispatchTouchEvent(MotionEvent motionEvent) {
        RoundVideoRecorder roundVideoRecorder;
        Drawable drawable;
        if (this.recording && (roundVideoRecorder = this.currentRecorder) != null && roundVideoRecorder.cameraView != null && (drawable = this.flipButton) != null) {
            RectF rectF = AndroidUtilities.rectTmp;
            rectF.set(drawable.getBounds());
            rectF.inset(-AndroidUtilities.dp(12.0f), -AndroidUtilities.dp(12.0f));
            int i = 0;
            while (true) {
                if (i >= motionEvent.getPointerCount()) {
                    break;
                }
                if (AndroidUtilities.rectTmp.contains(motionEvent.getX(i), motionEvent.getY(i))) {
                    if (motionEvent.getAction() == 0 || motionEvent.getActionMasked() == 5) {
                        this.currentRecorder.cameraView.switchCamera();
                        if (Build.VERSION.SDK_INT >= 21) {
                            Drawable drawable2 = this.flipButton;
                            if (CaptionStory$$ExternalSyntheticApiModelOutline0.m(drawable2)) {
                                AnimatedVectorDrawableCompat$$ExternalSyntheticApiModelOutline0.m(drawable2).start();
                            }
                        }
                    }
                    if (!this.recordTouch) {
                        return true;
                    }
                } else {
                    i++;
                }
            }
        }
        RectF rectF2 = AndroidUtilities.rectTmp;
        rectF2.set(this.roundButton.getX(), this.roundButton.getY(), this.roundButton.getX() + this.roundButton.getMeasuredWidth(), this.roundButton.getY() + this.roundButton.getMeasuredHeight());
        if (this.recordTouch || !(this.hasRoundVideo || this.keyboardShown || !rectF2.contains(motionEvent.getX(), motionEvent.getY()))) {
            return roundButtonTouchEvent(motionEvent);
        }
        if (this.recording && this.locked && this.cancelBounds.contains(motionEvent.getX(), motionEvent.getY())) {
            releaseRecord(false, true);
        } else {
            if (!this.recording || (!this.lockBounds.contains(motionEvent.getX(), motionEvent.getY()) && !getBounds().contains(motionEvent.getX(), motionEvent.getY()))) {
                return super.dispatchTouchEvent(motionEvent);
            }
            releaseRecord(false, false);
        }
        this.recordTouch = false;
        return true;
    }

    @Override
    public void drawOver(Canvas canvas, RectF rectF) {
        Paint paint;
        float f;
        Paint paint2;
        float f2;
        float f3;
        if (this.currentRecorder != null) {
            float f4 = this.cancelT.set(this.cancelling);
            float f5 = this.lockT.set(this.locked);
            if (this.startTime <= 0) {
                this.startTime = System.currentTimeMillis();
            }
            double currentTimeMillis = ((float) (System.currentTimeMillis() - this.startTime)) / 900.0f;
            Double.isNaN(currentTimeMillis);
            float sin = (((float) Math.sin(currentTimeMillis * 3.141592653589793d)) + 1.0f) / 2.0f;
            float dp = rectF.left + AndroidUtilities.dp(21.0f);
            float dp2 = rectF.bottom - AndroidUtilities.dp(20.0f);
            this.recordPaint.setBounds((int) (dp - AndroidUtilities.dp(12.0f)), (int) (dp2 - AndroidUtilities.dp(12.0f)), (int) (dp + AndroidUtilities.dp(12.0f)), (int) (dp2 + AndroidUtilities.dp(12.0f)));
            this.recordPaint.draw(canvas);
            this.timerTextDrawable.setBounds((int) ((rectF.left + AndroidUtilities.dp(33.3f)) - (AndroidUtilities.dp(10.0f) * f4)), (int) ((rectF.bottom - AndroidUtilities.dp(20.0f)) - AndroidUtilities.dp(9.0f)), (int) (rectF.left + AndroidUtilities.dp(133.3f)), (int) ((rectF.bottom - AndroidUtilities.dp(20.0f)) + AndroidUtilities.dp(9.0f)));
            this.timerTextDrawable.setText(this.currentRecorder.sinceRecordingText());
            this.timerTextDrawable.setAlpha((int) ((1.0f - f4) * 255.0f));
            this.timerTextDrawable.draw(canvas);
            float f6 = 1.0f - f5;
            float f7 = (1.0f - this.slideProgress) * f6;
            Paint paint3 = this.captionBlur.getPaint(1.0f);
            if (paint3 != null) {
                paint = paint3;
                f = f7;
                canvas.saveLayerAlpha(rectF.left, rectF.top, rectF.right, rectF.bottom, 255, 31);
            } else {
                paint = paint3;
                f = f7;
            }
            if (f > 0.0f) {
                if (this.slideToCancelText == null) {
                    this.slideToCancelText = new Text(LocaleController.getString(R.string.SlideToCancel2), 15.0f);
                }
                if (this.slideToCancelArrowPath == null) {
                    Path path = new Path();
                    this.slideToCancelArrowPath = path;
                    path.moveTo(AndroidUtilities.dp(3.83f), 0.0f);
                    this.slideToCancelArrowPath.lineTo(0.0f, AndroidUtilities.dp(5.0f));
                    this.slideToCancelArrowPath.lineTo(AndroidUtilities.dp(3.83f), AndroidUtilities.dp(10.0f));
                    Paint paint4 = new Paint(1);
                    this.slideToCancelArrowPaint = paint4;
                    paint4.setStyle(Paint.Style.STROKE);
                    this.slideToCancelArrowPaint.setStrokeCap(Paint.Cap.ROUND);
                    this.slideToCancelArrowPaint.setStrokeJoin(Paint.Join.ROUND);
                }
                this.slideToCancelArrowPaint.setStrokeWidth(AndroidUtilities.dp(1.33f));
                this.slideToCancelText.ellipsize((int) ((rectF.width() - AndroidUtilities.dp(116.0f)) - this.timerTextDrawable.getCurrentWidth()));
                float centerX = ((rectF.centerX() - ((AndroidUtilities.dp(11.33f) + this.slideToCancelText.getWidth()) / 2.0f)) - ((rectF.width() / 6.0f) * AndroidUtilities.lerp(this.slideProgress, 1.0f, f5))) - ((sin * AndroidUtilities.dp(6.0f)) * (1.0f - this.slideProgress));
                paint2 = paint;
                int multAlpha = Theme.multAlpha(paint2 != null ? -1 : -2130706433, f);
                canvas.save();
                canvas.translate(centerX, rectF.centerY() - AndroidUtilities.dp(5.0f));
                this.slideToCancelArrowPaint.setColor(multAlpha);
                canvas.drawPath(this.slideToCancelArrowPath, this.slideToCancelArrowPaint);
                canvas.restore();
                f2 = 0.0f;
                f3 = 15.0f;
                this.slideToCancelText.draw(canvas, centerX + AndroidUtilities.dp(11.33f), rectF.centerY(), multAlpha, 1.0f);
            } else {
                paint2 = paint;
                f2 = 0.0f;
                f3 = 15.0f;
            }
            if (f5 > f2) {
                if (this.cancelText == null) {
                    this.cancelText = new Text(LocaleController.getString(R.string.CancelRound), f3, AndroidUtilities.bold());
                }
                this.cancelText.ellipsize((int) ((rectF.width() - AndroidUtilities.dp(116.0f)) - this.timerTextDrawable.getCurrentWidth()));
                float centerX2 = (rectF.centerX() - (this.cancelText.getWidth() / 2.0f)) + ((rectF.width() / 4.0f) * f6);
                this.cancelText.draw(canvas, centerX2, rectF.centerY(), Theme.multAlpha(paint2 != null ? -1 : -2130706433, f5), 1.0f);
                this.cancelBounds.set(centerX2 - AndroidUtilities.dp(12.0f), rectF.top, centerX2 + this.cancelText.getWidth() + AndroidUtilities.dp(12.0f), rectF.bottom);
            }
            if (paint2 != null) {
                canvas.drawRect(rectF, paint2);
                canvas.restore();
            }
            invalidate();
        }
    }

    @Override
    public void drawOver2(Canvas canvas, RectF rectF, float f) {
        if (f <= 0.0f) {
            return;
        }
        float f2 = this.cancel2T.set(this.cancelling);
        float f3 = this.lock2T.set(this.locked);
        float f4 = this.animatedAmplitude.set(this.amplitude);
        float f5 = 1.0f - f2;
        float dp = (AndroidUtilities.dp(41.0f) + (AndroidUtilities.dp(30.0f) * f4 * (1.0f - this.slideProgress))) * f5 * f;
        float lerp = AndroidUtilities.lerp((rectF.right - AndroidUtilities.dp(20.0f)) - (((getWidth() * 0.35f) * this.slideProgress) * (1.0f - f3)), rectF.left + AndroidUtilities.dp(20.0f), f2);
        float dp2 = rectF.bottom - AndroidUtilities.dp(20.0f);
        if (LiteMode.isEnabled(98784)) {
            this.tinyWaveDrawable.minRadius = AndroidUtilities.dp(47.0f);
            this.tinyWaveDrawable.maxRadius = AndroidUtilities.dp(47.0f) + (AndroidUtilities.dp(15.0f) * BlobDrawable.FORM_SMALL_MAX);
            this.bigWaveDrawable.minRadius = AndroidUtilities.dp(50.0f);
            this.bigWaveDrawable.maxRadius = AndroidUtilities.dp(50.0f) + (AndroidUtilities.dp(12.0f) * BlobDrawable.FORM_BIG_MAX);
            this.bigWaveDrawable.update(f4, 1.01f);
            this.tinyWaveDrawable.update(f4, 1.02f);
            this.bigWaveDrawable.paint.setColor(Theme.multAlpha(this.roundPaint.getColor(), 0.15f * f));
            canvas.save();
            float f6 = dp / this.bigWaveDrawable.minRadius;
            canvas.scale(f6, f6, lerp, dp2);
            BlobDrawable blobDrawable = this.bigWaveDrawable;
            blobDrawable.draw(lerp, dp2, canvas, blobDrawable.paint);
            canvas.restore();
            this.tinyWaveDrawable.paint.setColor(Theme.multAlpha(this.roundPaint.getColor(), 0.3f * f));
            canvas.save();
            float f7 = dp / this.tinyWaveDrawable.minRadius;
            canvas.scale(f7, f7, lerp, dp2);
            BlobDrawable blobDrawable2 = this.tinyWaveDrawable;
            blobDrawable2.draw(lerp, dp2, canvas, blobDrawable2.paint);
            canvas.restore();
        }
        float min = Math.min(dp, AndroidUtilities.dp(55.0f));
        float f8 = f * 255.0f;
        this.roundPaint.setAlpha((int) f8);
        canvas.drawCircle(lerp, dp2, min, this.roundPaint);
        canvas.save();
        this.circlePath.rewind();
        Path path = this.circlePath;
        Path.Direction direction = Path.Direction.CW;
        path.addCircle(lerp, dp2, min, direction);
        canvas.clipPath(this.circlePath);
        this.roundDrawable.setBounds((int) (lerp - (((r6.getIntrinsicWidth() / 2.0f) * f5) * (this.stopping ? f : 1.0f))), (int) (dp2 - (((this.roundDrawable.getIntrinsicHeight() / 2.0f) * f5) * (this.stopping ? f : 1.0f))), (int) (((this.roundDrawable.getIntrinsicWidth() / 2.0f) * f5 * (this.stopping ? f : 1.0f)) + lerp), (int) (((this.roundDrawable.getIntrinsicHeight() / 2.0f) * f5 * (this.stopping ? f : 1.0f)) + dp2));
        this.roundDrawable.setAlpha((int) (f5 * 255.0f * (this.stopping ? f : 1.0f)));
        this.roundDrawable.draw(canvas);
        if (f3 > 0.0f) {
            float dpf2 = (AndroidUtilities.dpf2(19.33f) / 2.0f) * f3 * f;
            RectF rectF2 = AndroidUtilities.rectTmp;
            rectF2.set(lerp - dpf2, dp2 - dpf2, lerp + dpf2, dp2 + dpf2);
            canvas.drawRoundRect(rectF2, AndroidUtilities.dp(5.33f), AndroidUtilities.dp(5.33f), this.whitePaint);
        }
        canvas.restore();
        drawLock(canvas, rectF, f);
        if (this.cancelling && (this.roundButton.getVisibility() == 4 || this.periodButton.getVisibility() == 4 || this.collapsedT.get() > 0.0f)) {
            canvas.saveLayerAlpha(rectF, (int) ((1.0f - this.keyboardT) * 255.0f), 31);
            this.boundsPath.rewind();
            this.boundsPath.addRoundRect(rectF, AndroidUtilities.dp(21.0f), AndroidUtilities.dp(21.0f), direction);
            canvas.clipPath(this.boundsPath);
            if (this.roundButton.getVisibility() == 4 || this.collapsedT.get() > 0.0f) {
                canvas.save();
                canvas.translate(this.roundButton.getX() + (AndroidUtilities.dp(180.0f) * f5), this.roundButton.getY());
                this.roundButton.draw(canvas);
                canvas.restore();
            }
            if (this.periodButton.getVisibility() == 4 || this.collapsedT.get() > 0.0f) {
                canvas.save();
                canvas.translate(this.periodButton.getX() + (AndroidUtilities.dp(180.0f) * f5), this.periodButton.getY());
                this.periodButton.draw(canvas);
                canvas.restore();
            }
            canvas.restore();
        }
        checkFlipButton();
        this.flipButton.setAlpha((int) (f8 * f5));
        float timelineHeight = getTimelineHeight();
        this.flipButton.setBounds(((int) rectF.left) + AndroidUtilities.dp(4.0f), (int) ((rectF.top - timelineHeight) - AndroidUtilities.dp(48.0f)), (int) (rectF.left + AndroidUtilities.dp(40.0f)), (int) ((rectF.top - timelineHeight) - AndroidUtilities.dp(12.0f)));
        this.flipButton.draw(canvas);
    }

    @Override
    protected int getCaptionDefaultLimit() {
        return MessagesController.getInstance(this.currentAccount).storyCaptionLengthLimitDefault;
    }

    @Override
    protected int getCaptionPremiumLimit() {
        return MessagesController.getInstance(this.currentAccount).storyCaptionLengthLimitPremium;
    }

    public int getTimelineHeight() {
        return 0;
    }

    public void hidePeriodPopup() {
        ItemOptions itemOptions = this.periodPopup;
        if (itemOptions != null) {
            itemOptions.dismiss();
            this.periodPopup = null;
        }
    }

    public boolean isRecording() {
        return this.recording;
    }

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        this.recordPaint.attach();
    }

    @Override
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        this.recordPaint.detach();
    }

    @Override
    protected void onUpdateShowKeyboard(float f) {
        float f2 = 1.0f - f;
        this.periodButton.setAlpha(f2);
        this.roundButton.setAlpha(f2);
    }

    public abstract void putRecorder(RoundVideoRecorder roundVideoRecorder);

    public abstract void removeRound();

    public void setAmplitude(double d) {
        this.amplitude = (float) (Math.min(1800.0d, d) / 1800.0d);
        invalidate();
    }

    public void setHasRoundVideo(boolean z) {
        this.roundButton.setImageResource(z ? R.drawable.input_video_story_remove : R.drawable.input_video_story);
        this.hasRoundVideo = z;
    }

    public void setOnPeriodUpdate(Utilities.Callback<Integer> callback) {
        this.onPeriodUpdate = callback;
    }

    public void setOnPremiumHint(Utilities.Callback<Integer> callback) {
        this.onPremiumHintShow = callback;
    }

    public void setPeriod(int i) {
        setPeriod(i, true);
    }

    public void setPeriod(int i, boolean z) {
        int i2 = 0;
        while (true) {
            int[] iArr = periods;
            if (i2 >= iArr.length) {
                i2 = 2;
                break;
            } else if (iArr[i2] == i) {
                break;
            } else {
                i2++;
            }
        }
        if (this.periodIndex == i2) {
            return;
        }
        this.periodIndex = i2;
        this.periodDrawable.setValue(i / 3600, false, z);
    }

    public void setPeriodVisible(boolean z) {
        this.periodVisible = z;
        this.periodButton.setVisibility((!z || this.keyboardShown) ? 8 : 0);
    }

    public void showRemoveRoundAlert() {
        TextView textView;
        if (this.hasRoundVideo && (textView = (TextView) new AlertDialog.Builder(getContext(), this.resourcesProvider).setTitle(LocaleController.getString(R.string.StoryRemoveRoundTitle)).setMessage(LocaleController.getString(R.string.StoryRemoveRoundMessage)).setPositiveButton(LocaleController.getString(R.string.Remove), new AlertDialog.OnButtonClickListener() {
            @Override
            public final void onClick(AlertDialog alertDialog, int i) {
                CaptionStory.this.lambda$showRemoveRoundAlert$7(alertDialog, i);
            }
        }).setNegativeButton(LocaleController.getString(R.string.Cancel), null).show().getButton(-1)) != null) {
            textView.setTextColor(Theme.getColor(Theme.key_text_RedBold, this.resourcesProvider));
        }
    }

    public boolean stopRecording() {
        if (!this.recording) {
            return false;
        }
        this.recordTouch = false;
        releaseRecord(false, false);
        return true;
    }
}
